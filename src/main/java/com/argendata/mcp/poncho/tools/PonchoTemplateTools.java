package com.argendata.mcp.poncho.tools;

import com.argendata.mcp.poncho.model.ComponentSearchResult;
import com.argendata.mcp.poncho.model.PonchoComponent;
import com.argendata.mcp.poncho.service.DocumentationService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.argendata.mcp.poncho.config.PonchoConstants.*;

/**
 * Tools MCP para acceso a plantillas de páginas completas de Poncho.
 * Usa el servicio unificado de documentación para búsqueda por keywords.
 */
@Component
public class PonchoTemplateTools {

    private static final String TEMPLATES_BASE_PATH = "classpath:templates/";
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final DocumentationService documentationService;

    public PonchoTemplateTools(DocumentationService documentationService) {
        this.documentationService = documentationService;
    }

    @Tool(description = "USAR PRIMERO cuando el usuario quiera crear una página completa (home, landing, login, formulario, etc). " +
            "Recomienda la plantilla más adecuada según el caso de uso descrito usando búsqueda semántica por keywords. " +
            "Retorna la plantilla recomendada con su código HTML listo para personalizar. " +
            "Al usar el HTML devuelto: respetar al máximo el original; no eliminar clases CSS, estilos inline ni atributos necesarios; " +
            "solo personalizar textos, imágenes y enlaces.")
    public String recomendar_plantilla(
            @ToolParam(description = "Descripción del caso de uso (ej: 'página de inicio para un ministerio', " +
                    "'formulario de contacto', 'login de usuarios', 'mostrar datos en tabla')") 
            String casoDeUso
    ) {
        // Buscar templates que coincidan con el caso de uso
        List<ComponentSearchResult> results = documentationService.searchOnlyTemplates(casoDeUso, 3);
        
        if (results.isEmpty()) {
            return buildNoMatchResponse(casoDeUso);
        }
        
        // Tomar el mejor resultado
        ComponentSearchResult bestMatch = results.get(0);
        PonchoComponent template = documentationService.getComponent(bestMatch.id()).orElse(null);
        
        if (template == null || template.templatePath() == null) {
            return buildNoMatchResponse(casoDeUso);
        }
        
        return obtenerPlantillaConRecomendacion(template, casoDeUso, results);
    }
    
    private String buildNoMatchResponse(String casoDeUso) {
        StringBuilder sb = new StringBuilder();
        sb.append("# No encontré una plantilla específica para: \"").append(casoDeUso).append("\"\n\n");
        sb.append("## Sugerencias\n\n");
        sb.append("Puedo ayudarte con estos tipos de páginas:\n\n");
        sb.append("- **Páginas institucionales**: home, área, noticia, servicio\n");
        sb.append("- **Autenticación**: login, registro, recuperar contraseña\n");
        sb.append("- **Formularios**: contacto, datos personales, consultas\n");
        sb.append("- **Visualización de datos**: tablas simples, con filtros, responsive\n");
        sb.append("- **Navegación**: paneles, tarjetas, accesos rápidos\n");
        sb.append("- **Destacados**: estadísticas, íconos, imágenes\n\n");
        sb.append("¿Podrías describir mejor qué tipo de página necesitas?\n\n");
        sb.append("> También puedes usar `listar_categorias_plantillas` para ver todas las opciones.\n");
        return sb.toString();
    }
    
    private String obtenerPlantillaConRecomendacion(PonchoComponent template, String casoDeUso, List<ComponentSearchResult> alternativas) {
        String resourcePath = TEMPLATES_BASE_PATH + template.templatePath();
        
        try {
            Resource resource = resolver.getResource(resourcePath);
            if (!resource.exists()) {
                return "Error interno: plantilla no encontrada en " + template.templatePath();
            }
            
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            StringBuilder sb = new StringBuilder();
            sb.append("# Plantilla recomendada: `").append(template.name()).append("`\n\n");
            sb.append("**Para tu caso:** ").append(casoDeUso).append("\n\n");
            sb.append("**Descripción:** ").append(template.description()).append("\n\n");
            
            String categoryName = getCategoryName(template.category());
            sb.append("**Categoría:** ").append(categoryName).append("\n\n");
            
            // Mostrar alternativas si hay más de una
            if (alternativas.size() > 1) {
                sb.append("### Otras opciones relevantes\n\n");
                for (int i = 1; i < alternativas.size(); i++) {
                    ComponentSearchResult alt = alternativas.get(i);
                    sb.append("- **").append(alt.name()).append("** (`").append(alt.id()).append("`): ");
                    sb.append(alt.description()).append("\n");
                }
                sb.append("\n");
            }
            
            sb.append("---\n\n");
            sb.append("## Código HTML\n\n");
            sb.append("Personaliza el siguiente código según las necesidades del organismo:\n\n");
            sb.append("```html\n");
            sb.append(content);
            sb.append("\n```\n\n");
            sb.append("> **IMPORTANTE:** Respeta al máximo el HTML original. No elimines clases CSS, estilos inline ni atributos necesarios; " +
                    "no simplifiques ni reescribas la estructura. Solo personaliza textos, imágenes, enlaces y rutas de assets.\n\n");
            sb.append("## Próximos pasos\n\n");
            sb.append("1. Modifica los textos (títulos, descripciones, contenido)\n");
            sb.append("2. Actualiza las imágenes con las del organismo\n");
            sb.append("3. Ajusta los enlaces de navegación\n");
            sb.append("4. Revisa las rutas de assets CSS/JS según tu proyecto\n\n");
            sb.append("> Si prefieres otra plantilla, usa `buscar_plantilla` con otros términos.\n");
            
            return sb.toString();
            
        } catch (IOException e) {
            return "Error al leer la plantilla: " + e.getMessage();
        }
    }

    @Tool(description = "Lista todas las categorías de plantillas de páginas Poncho disponibles. " +
            "Cada categoría contiene plantillas HTML completas listas para usar. " +
            "IMPORTANTE: Usar 'recomendar_plantilla' primero si el usuario describe un caso de uso específico.")
    public String listar_categorias_plantillas() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Categorías de Plantillas Poncho\n\n");
        sb.append("Plantillas HTML completas del sistema de diseño de argentina.gob.ar\n\n");
        
        for (String catSlug : TEMPLATE_CATEGORIES.keySet()) {
            sb.append("## ").append(getCategoryName(catSlug)).append("\n");
            sb.append("**Categoría:** `").append(catSlug).append("`\n\n");
            sb.append(getCategoryDescription(catSlug)).append("\n\n");
            
            // Obtener templates de esta categoría desde el servicio
            List<PonchoComponent> templates = documentationService.getTemplatesByCategory(catSlug);
            if (!templates.isEmpty()) {
                sb.append("**Plantillas disponibles:** ");
                sb.append(templates.stream()
                    .map(PonchoComponent::name)
                    .collect(Collectors.joining(", ")));
                sb.append("\n\n");
            }
            sb.append("---\n\n");
        }
        
        sb.append("\n> Usa `buscar_plantilla` para buscar por funcionalidad.\n");
        sb.append("> Usa `obtener_plantilla` para obtener el código HTML completo de una plantilla.\n");
        
        return sb.toString();
    }

    @Tool(description = "Lista las plantillas disponibles en una categoría específica con descripción de cada una. " +
            "Categorías: paginas-argentina, login-registro, formularios, tablas, paneles, destacados, headers-footers")
    public String listar_plantillas_categoria(
            @ToolParam(description = "Nombre de la categoría (ej: 'paginas-argentina', 'formularios', 'tablas')") 
            String categoria
    ) {
        if (!isValidCategory(categoria)) {
            return "Error: Categoría '" + categoria + "' no encontrada. " +
                   "Categorías válidas: " + String.join(", ", TEMPLATE_CATEGORIES.keySet());
        }
        
        List<PonchoComponent> templates = documentationService.getTemplatesByCategory(categoria);
        
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(getCategoryName(categoria)).append("\n\n");
        sb.append(getCategoryDescription(categoria)).append("\n\n");
        sb.append("## Plantillas disponibles\n\n");
        
        for (PonchoComponent template : templates) {
            sb.append("### `").append(template.name()).append("`\n");
            sb.append("**ID:** `").append(template.id()).append("`\n\n");
            sb.append(template.description()).append("\n\n");
            if (template.keywords() != null && !template.keywords().isEmpty()) {
                sb.append("*Keywords:* ").append(String.join(", ", template.keywords())).append("\n\n");
            }
        }
        
        sb.append("\n> Usa `obtener_plantilla(\"").append(categoria).append("\", \"id-plantilla\")` ");
        sb.append("para obtener el código HTML.\n");
        
        return sb.toString();
    }

    @Tool(description = "Obtiene el código HTML completo de una plantilla de página Poncho. " +
            "El HTML incluye la estructura completa con dependencias CSS/JS de Poncho. " +
            "Al usar el HTML devuelto: respetar al máximo el original; no eliminar clases CSS, estilos inline ni atributos necesarios; " +
            "solo personalizar textos, imágenes y enlaces.")
    public String obtener_plantilla(
            @ToolParam(description = "Categoría de la plantilla (ej: 'paginas-argentina', 'formularios')") 
            String categoria,
            @ToolParam(description = "Nombre del archivo sin extensión (ej: 'home', 'login', 'tabla-simple') o ID del template") 
            String nombrePlantilla
    ) {
        if (!isValidCategory(categoria)) {
            return "Error: Categoría '" + categoria + "' no encontrada. " +
                   "Categorías válidas: " + String.join(", ", TEMPLATE_CATEGORIES.keySet());
        }
        
        // Buscar template por ID o por nombre de archivo
        PonchoComponent template = findTemplate(categoria, nombrePlantilla);
        
        if (template == null || template.templatePath() == null) {
            List<PonchoComponent> available = documentationService.getTemplatesByCategory(categoria);
            return "Error: Plantilla '" + nombrePlantilla + "' no encontrada en categoría '" + categoria + "'. " +
                   "Plantillas disponibles: " + available.stream()
                       .map(t -> t.name() + " (" + t.id() + ")")
                       .collect(Collectors.joining(", "));
        }
        
        String resourcePath = TEMPLATES_BASE_PATH + template.templatePath();
        
        try {
            Resource resource = resolver.getResource(resourcePath);
            if (!resource.exists()) {
                return "Error: Archivo de plantilla no encontrado: " + template.templatePath();
            }
            
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            StringBuilder sb = new StringBuilder();
            sb.append("# Plantilla: ").append(template.name()).append("\n");
            sb.append("**Categoría:** ").append(getCategoryName(categoria)).append("\n\n");
            sb.append("**Descripción:** ").append(template.description()).append("\n\n");
            sb.append("## Código HTML\n\n");
            sb.append("```html\n");
            sb.append(content);
            sb.append("\n```\n\n");
            sb.append("## Notas de uso\n\n");
            sb.append("> **IMPORTANTE:** Respeta al máximo el HTML original. No elimines clases CSS, estilos inline ni atributos necesarios; " +
                    "no simplifiques ni reescribas la estructura. Solo personaliza textos, imágenes, enlaces y rutas de assets.\n\n");
            sb.append("- Esta plantilla está diseñada para el sistema de diseño Poncho de argentina.gob.ar\n");
            sb.append("- Modifica el contenido manteniendo la estructura y clases CSS\n");
            sb.append("- Las rutas de assets pueden necesitar ajuste según tu proyecto\n");
            
            return sb.toString();
            
        } catch (IOException e) {
            return "Error al leer la plantilla: " + e.getMessage();
        }
    }
    
    /**
     * Busca un template por ID o por nombre de archivo en una categoría
     */
    private PonchoComponent findTemplate(String categoria, String nombrePlantilla) {
        List<PonchoComponent> templates = documentationService.getTemplatesByCategory(categoria);
        
        // Buscar por ID exacto
        Optional<PonchoComponent> byId = templates.stream()
            .filter(t -> t.id().equals(nombrePlantilla) || t.id().equals("template-" + nombrePlantilla))
            .findFirst();
        
        if (byId.isPresent()) {
            return byId.get();
        }
        
        // Buscar por nombre de archivo en templatePath
        return templates.stream()
            .filter(t -> t.templatePath() != null && 
                        (t.templatePath().contains("/" + nombrePlantilla + ".html") ||
                         t.templatePath().endsWith(nombrePlantilla + ".html")))
            .findFirst()
            .orElse(null);
    }

    @Tool(description = "Busca plantillas Poncho por palabras clave en nombre, descripción o keywords. " +
            "Usa búsqueda semántica para encontrar la plantilla más adecuada.")
    public String buscar_plantilla(
            @ToolParam(description = "Término de búsqueda (ej: 'login', 'tabla filtros', 'formulario contacto', 'panel')") 
            String busqueda
    ) {
        List<ComponentSearchResult> results = documentationService.searchOnlyTemplates(busqueda, 10);
        
        if (results.isEmpty()) {
            return "No se encontraron plantillas para: '" + busqueda + "'\n\n" +
                   "Sugerencias:\n" +
                   "- Prueba con términos más generales (login, tabla, form, panel)\n" +
                   "- Usa `listar_categorias_plantillas` para ver todas las opciones";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("# Resultados de búsqueda: \"").append(busqueda).append("\"\n\n");
        sb.append("Se encontraron ").append(results.size()).append(" plantilla(s)\n\n");
        
        for (ComponentSearchResult r : results) {
            sb.append("### `").append(r.name()).append("`\n");
            sb.append("**ID:** `").append(r.id()).append("`\n");
            sb.append("**Categoría:** ").append(r.category()).append("\n");
            sb.append(r.description()).append("\n");
            sb.append("*Relevancia:* ").append(String.format("%.2f", r.score())).append("\n\n");
        }
        
        sb.append("\n> Usa `obtener_plantilla(\"categoria\", \"id\")` para ver el código HTML.\n");
        sb.append("> Usa `recomendar_plantilla` si describes el caso de uso para obtener directamente el HTML.\n");
        
        return sb.toString();
    }

}
