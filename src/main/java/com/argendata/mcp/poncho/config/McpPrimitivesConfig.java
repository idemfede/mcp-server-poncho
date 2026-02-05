package com.argendata.mcp.poncho.config;

import com.argendata.mcp.poncho.model.PonchoComponent;
import com.argendata.mcp.poncho.service.DocumentationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.argendata.mcp.poncho.config.PonchoConstants.TEMPLATE_CATEGORIES;

/**
 * Configuración de primitives MCP adicionales: Resources, Prompts y Completions.
 * Complementa los Tools existentes con acceso read-only a contenido y recetas guiadas.
 */
@Configuration
public class McpPrimitivesConfig {

    private static final String TEMPLATES_BASE_PATH = "templates/";

    // ========== RESOURCES ==========

    @Bean
    public List<McpServerFeatures.SyncResourceSpecification> ponchoResources(
            DocumentationService documentationService,
            ObjectMapper objectMapper
    ) {
        List<McpServerFeatures.SyncResourceSpecification> specs = new ArrayList<>();

        // Resource: Lista de categorías de templates
        specs.add(new McpServerFeatures.SyncResourceSpecification(
            new Resource(
                "poncho://templates/categories",
                "Categorías de Templates Poncho",
                "Lista de categorías de plantillas HTML disponibles en el sistema de diseño Poncho",
                "application/json",
                null
            ),
            (exchange, request) -> {
                try {
                    String json = objectMapper.writeValueAsString(TEMPLATE_CATEGORIES);
                    return new ReadResourceResult(List.of(
                        new TextResourceContents(request.uri(), "application/json", json)
                    ));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error generando JSON de categorías", e);
                }
            }
        ));

        // Resource: Templates por categoría (URI con parámetro)
        for (String categoria : TEMPLATE_CATEGORIES.keySet()) {
            final String cat = categoria;
            specs.add(new McpServerFeatures.SyncResourceSpecification(
                new Resource(
                    "poncho://templates/" + cat,
                    "Templates de " + TEMPLATE_CATEGORIES.get(cat),
                    "Lista de plantillas HTML disponibles en la categoría " + cat,
                    "application/json",
                    null
                ),
                (exchange, request) -> {
                    List<PonchoComponent> templates = documentationService.getTemplatesByCategory(cat);
                    List<Map<String, String>> data = templates.stream()
                        .map(t -> Map.of(
                            "id", t.id(),
                            "name", t.name(),
                            "description", t.description(),
                            "templatePath", t.templatePath() != null ? t.templatePath() : ""
                        ))
                        .collect(Collectors.toList());
                    try {
                        String json = objectMapper.writeValueAsString(data);
                        return new ReadResourceResult(List.of(
                            new TextResourceContents(request.uri(), "application/json", json)
                        ));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error generando JSON de templates", e);
                    }
                }
            ));
        }

        // Resource: HTML original de cada template conocido
        List<PonchoComponent> allTemplates = documentationService.getOnlyTemplates();
        for (PonchoComponent template : allTemplates) {
            if (template.templatePath() == null) continue;
            
            final String templatePath = template.templatePath();
            final String templateId = template.id();
            final String templateName = template.name();
            
            specs.add(new McpServerFeatures.SyncResourceSpecification(
                new Resource(
                    "poncho://templates/html/" + templateId,
                    "HTML: " + templateName,
                    "Código HTML original de la plantilla " + templateName + ". " +
                    "IMPORTANTE: Respetar al máximo el original; no eliminar clases CSS, estilos ni estructura.",
                    "text/html",
                    null
                ),
                (exchange, request) -> {
                    try {
                        ClassPathResource resource = new ClassPathResource(TEMPLATES_BASE_PATH + templatePath);
                        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                        return new ReadResourceResult(List.of(
                            new TextResourceContents(request.uri(), "text/html", html)
                        ));
                    } catch (IOException e) {
                        throw new RuntimeException("Error leyendo template " + templatePath, e);
                    }
                }
            ));
        }

        // Resource: Documentación de componentes
        List<PonchoComponent> allComponents = documentationService.getOnlyComponents();
        for (PonchoComponent comp : allComponents) {
            final String compId = comp.id();
            final String compName = comp.name();
            
            specs.add(new McpServerFeatures.SyncResourceSpecification(
                new Resource(
                    "poncho://components/" + compId,
                    "Doc: " + compName,
                    "Documentación del componente " + compName + " incluyendo opciones, dependencias y ejemplos",
                    "text/markdown",
                    null
                ),
                (exchange, request) -> {
                    String markdown = documentationService.getFormattedDocumentation(compId);
                    return new ReadResourceResult(List.of(
                        new TextResourceContents(request.uri(), "text/markdown", markdown)
                    ));
                }
            ));
        }

        // Resource: Dependencias de un componente (JSON)
        for (PonchoComponent comp : allComponents) {
            if (comp.dependencies() == null) continue;
            
            final String compId = comp.id();
            final String compName = comp.name();
            final PonchoComponent.ComponentDependencies deps = comp.dependencies();
            
            specs.add(new McpServerFeatures.SyncResourceSpecification(
                new Resource(
                    "poncho://deps/" + compId,
                    "Deps: " + compName,
                    "Dependencias CSS y JS del componente " + compName,
                    "application/json",
                    null
                ),
                (exchange, request) -> {
                    try {
                        Map<String, Object> depsMap = Map.of(
                            "css", deps.css() != null ? deps.css() : List.of(),
                            "js", deps.js() != null ? deps.js() : List.of()
                        );
                        String json = objectMapper.writeValueAsString(depsMap);
                        return new ReadResourceResult(List.of(
                            new TextResourceContents(request.uri(), "application/json", json)
                        ));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error generando JSON de dependencias", e);
                    }
                }
            ));
        }

        return specs;
    }

    // ========== PROMPTS ==========

    @Bean
    public List<McpServerFeatures.SyncPromptSpecification> ponchoPrompts() {
        List<McpServerFeatures.SyncPromptSpecification> specs = new ArrayList<>();

        // Prompt: Adaptar plantilla sin romperla
        specs.add(new McpServerFeatures.SyncPromptSpecification(
            new Prompt(
                "poncho-adaptar-plantilla",
                "Instrucciones para adaptar una plantilla Poncho sin romper estilos ni estructura",
                List.of(
                    new PromptArgument("categoria", "Categoría de la plantilla (ej: paginas-argentina, formularios)", true),
                    new PromptArgument("id", "ID de la plantilla a adaptar", true),
                    new PromptArgument("objetivo", "Qué se quiere lograr con la adaptación (ej: página de inicio para Ministerio X)", true)
                )
            ),
            (exchange, request) -> {
                String categoria = (String) request.arguments().getOrDefault("categoria", "");
                String id = (String) request.arguments().getOrDefault("id", "");
                String objetivo = (String) request.arguments().getOrDefault("objetivo", "");

                String systemMessage = """
                    Eres un experto en el sistema de diseño Poncho de argentina.gob.ar.
                    Tu tarea es adaptar una plantilla HTML existente para un nuevo caso de uso.
                    
                    REGLAS CRÍTICAS:
                    1. NUNCA elimines clases CSS existentes (especialmente las que empiezan con 'poncho-', 'btn-', 'panel-', etc.)
                    2. NUNCA elimines estilos inline que afecten layout o apariencia
                    3. NUNCA simplifiques ni "limpies" la estructura HTML
                    4. NUNCA cambies atributos data-* que pueden ser necesarios para JavaScript
                    5. SOLO modifica: textos, imágenes (src/alt), enlaces (href), y contenido visible
                    
                    CHECKLIST de adaptación:
                    - [ ] Cambiar títulos y textos descriptivos
                    - [ ] Actualizar URLs de imágenes (o usar placeholders descriptivos)
                    - [ ] Modificar enlaces de navegación
                    - [ ] Ajustar rutas de assets (CSS/JS) si es necesario
                    - [ ] Verificar que todas las clases CSS originales permanecen
                    - [ ] Verificar que la estructura de divs/sections no cambió
                    
                    Plantilla a adaptar: %s (categoría: %s)
                    Objetivo del usuario: %s
                    """.formatted(id, categoria, objetivo);

                String userMessage = """
                    Necesito adaptar la plantilla '%s' de la categoría '%s' para: %s
                    
                    Por favor:
                    1. Lee el HTML original usando el resource poncho://templates/html/%s
                    2. Adapta SOLO el contenido (textos, imágenes, enlaces)
                    3. Entrega el HTML completo adaptado manteniendo TODA la estructura y clases
                    """.formatted(id, categoria, objetivo, id);

                return new GetPromptResult(
                    "Adaptación de plantilla Poncho preservando estructura",
                    List.of(
                        new PromptMessage(Role.ASSISTANT, new TextContent(systemMessage)),
                        new PromptMessage(Role.USER, new TextContent(userMessage))
                    )
                );
            }
        ));

        // Prompt: Insertar componente en página existente
        specs.add(new McpServerFeatures.SyncPromptSpecification(
            new Prompt(
                "poncho-insertar-componente",
                "Instrucciones para insertar un componente Poncho en una página existente",
                List.of(
                    new PromptArgument("componente", "ID del componente a insertar (ej: poncho-table, poncho-map)", true),
                    new PromptArgument("contexto", "Dónde se insertará (descripción del lugar en la página)", true)
                )
            ),
            (exchange, request) -> {
                String componente = (String) request.arguments().getOrDefault("componente", "");
                String contexto = (String) request.arguments().getOrDefault("contexto", "");

                String systemMessage = """
                    Eres un experto en el sistema de diseño Poncho de argentina.gob.ar.
                    Tu tarea es insertar un componente en una página existente.
                    
                    REGLAS:
                    1. Primero lee la documentación del componente (poncho://components/%s)
                    2. Verifica las dependencias necesarias (poncho://deps/%s)
                    3. Genera el código HTML/JS mínimo necesario
                    4. Indica dónde agregar las dependencias CSS/JS si no están presentes
                    
                    Componente: %s
                    Contexto de inserción: %s
                    """.formatted(componente, componente, componente, contexto);

                return new GetPromptResult(
                    "Inserción de componente Poncho",
                    List.of(
                        new PromptMessage(Role.ASSISTANT, new TextContent(systemMessage))
                    )
                );
            }
        ));

        return specs;
    }

    // ========== COMPLETIONS ==========

    @Bean
    public List<McpServerFeatures.SyncCompletionSpecification> ponchoCompletions(
            DocumentationService documentationService
    ) {
        List<McpServerFeatures.SyncCompletionSpecification> specs = new ArrayList<>();

        // Completion para categorías de templates (prompt poncho-adaptar-plantilla)
        specs.add(new McpServerFeatures.SyncCompletionSpecification(
            new PromptReference("poncho-adaptar-plantilla"),
            (exchange, request) -> {
                String argName = request.argument().name();
                String prefix = request.argument().value().toLowerCase();

                if ("categoria".equals(argName)) {
                    List<String> matches = TEMPLATE_CATEGORIES.keySet().stream()
                        .filter(cat -> cat.toLowerCase().startsWith(prefix))
                        .limit(10)
                        .collect(Collectors.toList());
                    return new CompleteResult(new CompleteResult.CompleteCompletion(matches, matches.size(), false));
                }

                if ("id".equals(argName)) {
                    // Buscar IDs de templates que coincidan con el prefijo
                    List<String> allIds = documentationService.getOnlyTemplates().stream()
                        .map(PonchoComponent::id)
                        .filter(id -> id.toLowerCase().contains(prefix))
                        .limit(15)
                        .collect(Collectors.toList());
                    return new CompleteResult(new CompleteResult.CompleteCompletion(allIds, allIds.size(), false));
                }

                return new CompleteResult(new CompleteResult.CompleteCompletion(List.of(), 0, false));
            }
        ));

        // Completion para componentes (prompt poncho-insertar-componente)
        specs.add(new McpServerFeatures.SyncCompletionSpecification(
            new PromptReference("poncho-insertar-componente"),
            (exchange, request) -> {
                String argName = request.argument().name();
                String prefix = request.argument().value().toLowerCase();

                if ("componente".equals(argName)) {
                    List<String> matches = documentationService.getOnlyComponents().stream()
                        .map(PonchoComponent::id)
                        .filter(id -> id.toLowerCase().contains(prefix))
                        .limit(15)
                        .collect(Collectors.toList());
                    return new CompleteResult(new CompleteResult.CompleteCompletion(matches, matches.size(), false));
                }

                return new CompleteResult(new CompleteResult.CompleteCompletion(List.of(), 0, false));
            }
        ));

        return specs;
    }
}
