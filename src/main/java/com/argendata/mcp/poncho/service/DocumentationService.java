package com.argendata.mcp.poncho.service;

import com.argendata.mcp.poncho.model.ComponentSearchResult;
import com.argendata.mcp.poncho.model.PonchoComponent;
import com.argendata.mcp.poncho.model.PonchoComponent.ComponentDependencies;
import com.argendata.mcp.poncho.model.PonchoComponent.ComponentOption;
import com.argendata.mcp.poncho.service.KeywordSearchService.SearchableItem;
import com.argendata.mcp.poncho.service.KeywordSearchService.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para búsqueda y acceso a documentación de componentes y templates Poncho.
 * Usa búsqueda por keywords en lugar de embeddings vectoriales.
 */
@Service
public class DocumentationService {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentationService.class);
    
    private final ObjectMapper objectMapper;
    private final KeywordSearchService keywordSearchService;
    
    private Map<String, PonchoComponent> componentsById = new HashMap<>();
    private Map<String, List<PonchoComponent>> componentsByCategory = new HashMap<>();
    private List<SearchableItem> searchableItems = new ArrayList<>();
    
    public DocumentationService(ObjectMapper objectMapper, KeywordSearchService keywordSearchService) {
        this.objectMapper = objectMapper;
        this.keywordSearchService = keywordSearchService;
    }
    
    @PostConstruct
    public void init() {
        loadComponents();
        buildSearchIndex();
    }
    
    private void loadComponents() {
        try {
            ClassPathResource resource = new ClassPathResource("docs/components.json");
            try (InputStream is = resource.getInputStream()) {
                JsonNode root = objectMapper.readTree(is);
                JsonNode componentsNode = root.get("components");
                
                for (JsonNode node : componentsNode) {
                    PonchoComponent component = parseComponent(node);
                    componentsById.put(component.id(), component);
                    componentsByCategory
                        .computeIfAbsent(component.category(), k -> new ArrayList<>())
                        .add(component);
                }
                
                long componentCount = componentsById.values().stream().filter(PonchoComponent::isComponent).count();
                long templateCount = componentsById.values().stream().filter(PonchoComponent::isTemplate).count();
                log.info("Cargados {} componentes y {} templates Poncho", componentCount, templateCount);
            }
        } catch (IOException e) {
            log.error("Error cargando componentes: {}", e.getMessage());
        }
    }
    
    private PonchoComponent parseComponent(JsonNode node) {
        String id = node.get("id").asText();
        String type = node.has("type") ? node.get("type").asText() : "component";
        String name = node.get("name").asText();
        String category = node.get("category").asText();
        String description = node.get("description").asText();
        
        List<String> keywords = new ArrayList<>();
        if (node.has("keywords")) {
            node.get("keywords").forEach(k -> keywords.add(k.asText()));
        }
        
        ComponentDependencies deps = null;
        if (node.has("dependencies")) {
            JsonNode depsNode = node.get("dependencies");
            List<String> css = new ArrayList<>();
            List<String> js = new ArrayList<>();
            if (depsNode.has("css")) {
                depsNode.get("css").forEach(c -> css.add(c.asText()));
            }
            if (depsNode.has("js")) {
                depsNode.get("js").forEach(j -> js.add(j.asText()));
            }
            deps = new ComponentDependencies(css, js);
        }
        
        List<ComponentOption> options = new ArrayList<>();
        if (node.has("options")) {
            for (JsonNode optNode : node.get("options")) {
                options.add(new ComponentOption(
                    optNode.has("name") ? optNode.get("name").asText() : "",
                    optNode.has("type") ? optNode.get("type").asText() : "string",
                    optNode.has("required") && optNode.get("required").asBoolean(),
                    optNode.has("default") ? optNode.get("default").asText() : null,
                    optNode.has("description") ? optNode.get("description").asText() : ""
                ));
            }
        }
        
        String mainFunction = node.has("mainFunction") ? node.get("mainFunction").asText() : null;
        String mainClass = node.has("mainClass") ? node.get("mainClass").asText() : null;
        String mainObject = node.has("mainObject") ? node.get("mainObject").asText() : null;
        String usageExample = node.has("usageExample") ? node.get("usageExample").asText() : null;
        String templatePath = node.has("templatePath") ? node.get("templatePath").asText() : null;
        
        return new PonchoComponent(id, type, name, category, description, keywords, deps, options,
            mainFunction, mainClass, mainObject, usageExample, templatePath);
    }
    
    /**
     * Construye el índice de búsqueda con todos los componentes y templates
     */
    private void buildSearchIndex() {
        searchableItems = componentsById.values().stream()
            .map(this::componentToSearchableItem)
            .collect(Collectors.toList());
        
        log.info("Índice de búsqueda construido con {} items", searchableItems.size());
    }
    
    private SearchableItem componentToSearchableItem(PonchoComponent component) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", component.type());
        if (component.templatePath() != null) {
            metadata.put("templatePath", component.templatePath());
        }
        
        return new SearchableItem(
            component.id(),
            component.name(),
            component.category(),
            component.description(),
            component.keywords(),
            component.type(),
            metadata
        );
    }
    
    /**
     * Busca componentes y templates usando búsqueda por keywords
     */
    public List<ComponentSearchResult> searchComponents(String query, int maxResults) {
        List<SearchResult> results = keywordSearchService.search(query, searchableItems, maxResults);
        
        return results.stream()
            .map(result -> {
                PonchoComponent component = componentsById.get(result.item().id());
                return ComponentSearchResult.from(component, result.score());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Busca solo componentes (no templates)
     */
    public List<ComponentSearchResult> searchOnlyComponents(String query, int maxResults) {
        List<SearchableItem> onlyComponents = searchableItems.stream()
            .filter(item -> "component".equals(item.type()))
            .collect(Collectors.toList());
        
        List<SearchResult> results = keywordSearchService.search(query, onlyComponents, maxResults);
        
        return results.stream()
            .map(result -> {
                PonchoComponent component = componentsById.get(result.item().id());
                return ComponentSearchResult.from(component, result.score());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Busca solo templates (no componentes)
     */
    public List<ComponentSearchResult> searchOnlyTemplates(String query, int maxResults) {
        List<SearchableItem> onlyTemplates = searchableItems.stream()
            .filter(item -> "template".equals(item.type()))
            .collect(Collectors.toList());
        
        List<SearchResult> results = keywordSearchService.search(query, onlyTemplates, maxResults);
        
        return results.stream()
            .map(result -> {
                PonchoComponent component = componentsById.get(result.item().id());
                return ComponentSearchResult.from(component, result.score());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un componente o template por su ID
     */
    public Optional<PonchoComponent> getComponent(String id) {
        return Optional.ofNullable(componentsById.get(id));
    }
    
    /**
     * Lista todos los componentes y templates
     */
    public List<PonchoComponent> getAllComponents() {
        return new ArrayList<>(componentsById.values());
    }
    
    /**
     * Lista solo componentes (sin templates)
     */
    public List<PonchoComponent> getOnlyComponents() {
        return componentsById.values().stream()
            .filter(PonchoComponent::isComponent)
            .collect(Collectors.toList());
    }
    
    /**
     * Lista solo templates (sin componentes)
     */
    public List<PonchoComponent> getOnlyTemplates() {
        return componentsById.values().stream()
            .filter(PonchoComponent::isTemplate)
            .collect(Collectors.toList());
    }
    
    /**
     * Lista componentes/templates por categoría
     */
    public List<PonchoComponent> getComponentsByCategory(String category) {
        return componentsByCategory.getOrDefault(category, Collections.emptyList());
    }
    
    /**
     * Lista templates por categoría
     */
    public List<PonchoComponent> getTemplatesByCategory(String category) {
        return componentsByCategory.getOrDefault(category, Collections.emptyList())
            .stream()
            .filter(PonchoComponent::isTemplate)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene todas las categorías disponibles
     */
    public Set<String> getCategories() {
        return componentsByCategory.keySet();
    }
    
    /**
     * Obtiene categorías de solo componentes
     */
    public Set<String> getComponentCategories() {
        return componentsById.values().stream()
            .filter(PonchoComponent::isComponent)
            .map(PonchoComponent::category)
            .collect(Collectors.toSet());
    }
    
    /**
     * Obtiene categorías de solo templates
     */
    public Set<String> getTemplateCategories() {
        return componentsById.values().stream()
            .filter(PonchoComponent::isTemplate)
            .map(PonchoComponent::category)
            .collect(Collectors.toSet());
    }
    
    /**
     * Genera documentación formateada de un componente
     */
    public String getFormattedDocumentation(String componentId) {
        return getComponent(componentId)
            .map(this::formatComponentDocs)
            .orElse("Componente no encontrado: " + componentId);
    }
    
    private String formatComponentDocs(PonchoComponent c) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("# ").append(c.name()).append("\n\n");
        sb.append("**Tipo:** ").append(c.isTemplate() ? "Template" : "Componente").append("\n");
        sb.append("**Categoría:** ").append(c.category()).append("\n\n");
        sb.append("## Descripción\n").append(c.description()).append("\n\n");
        
        if (c.keywords() != null && !c.keywords().isEmpty()) {
            sb.append("**Palabras clave:** ").append(String.join(", ", c.keywords())).append("\n\n");
        }
        
        if (c.isTemplate() && c.templatePath() != null) {
            sb.append("**Archivo:** `").append(c.templatePath()).append("`\n\n");
        }
        
        if (c.dependencies() != null) {
            sb.append("## Dependencias\n\n");
            if (c.dependencies().css() != null && !c.dependencies().css().isEmpty()) {
                sb.append("### CSS\n");
                c.dependencies().css().forEach(css -> sb.append("- ").append(css).append("\n"));
                sb.append("\n");
            }
            if (c.dependencies().js() != null && !c.dependencies().js().isEmpty()) {
                sb.append("### JavaScript\n");
                c.dependencies().js().forEach(js -> sb.append("- ").append(js).append("\n"));
                sb.append("\n");
            }
        }
        
        if (c.options() != null && !c.options().isEmpty()) {
            sb.append("## Opciones de configuración\n\n");
            sb.append("| Opción | Tipo | Requerido | Default | Descripción |\n");
            sb.append("|--------|------|-----------|---------|-------------|\n");
            for (ComponentOption opt : c.options()) {
                sb.append("| ").append(opt.name())
                  .append(" | ").append(opt.type())
                  .append(" | ").append(opt.required() ? "Sí" : "No")
                  .append(" | ").append(opt.defaultValue() != null ? opt.defaultValue() : "-")
                  .append(" | ").append(opt.description())
                  .append(" |\n");
            }
            sb.append("\n");
        }
        
        if (c.mainFunction() != null) {
            sb.append("**Función principal:** `").append(c.mainFunction()).append("`\n\n");
        }
        if (c.mainClass() != null) {
            sb.append("**Clase principal:** `").append(c.mainClass()).append("`\n\n");
        }
        if (c.mainObject() != null) {
            sb.append("**Objeto principal:** `").append(c.mainObject()).append("`\n\n");
        }
        
        return sb.toString();
    }
}
