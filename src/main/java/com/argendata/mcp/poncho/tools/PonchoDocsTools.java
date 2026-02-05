package com.argendata.mcp.poncho.tools;

import com.argendata.mcp.poncho.model.ComponentSearchResult;
import com.argendata.mcp.poncho.model.PonchoComponent;
import com.argendata.mcp.poncho.service.DocumentationService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tools MCP para documentación de Poncho
 */
@Component
public class PonchoDocsTools {
    
    private final DocumentationService documentationService;
    
    public PonchoDocsTools(DocumentationService documentationService) {
        this.documentationService = documentationService;
    }
    
    @Tool(description = "Busca componentes de la librería Poncho por nombre, funcionalidad o descripción. " +
            "Usa búsqueda semántica para encontrar componentes relevantes. " +
            "Ejemplos de búsqueda: 'tabla con filtros', 'mapa interactivo', 'calendario feriados', 'colores'")
    public String buscar_componente(
            @ToolParam(description = "Texto de búsqueda: nombre del componente o descripción de la funcionalidad") 
            String query,
            @ToolParam(description = "Número máximo de resultados (por defecto 5)") 
            Integer maxResultados
    ) {
        int max = maxResultados != null && maxResultados > 0 ? maxResultados : 5;
        List<ComponentSearchResult> results = documentationService.searchComponents(query, max);
        
        if (results.isEmpty()) {
            return "No se encontraron componentes para: " + query;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("## Resultados de búsqueda para: \"").append(query).append("\"\n\n");
        
        for (int i = 0; i < results.size(); i++) {
            ComponentSearchResult r = results.get(i);
            sb.append(String.format("%d. **%s** (id: `%s`)\n", i + 1, r.name(), r.id()));
            sb.append("   - Categoría: ").append(r.category()).append("\n");
            sb.append("   - ").append(r.description()).append("\n");
            sb.append("   - Relevancia: ").append(String.format("%.2f", r.score())).append("\n\n");
        }
        
        sb.append("---\n");
        sb.append("Usa `obtener_documentacion` con el `id` del componente para ver la documentación completa.");
        
        return sb.toString();
    }
    
    @Tool(description = "Obtiene la documentación completa de un componente Poncho específico. " +
            "Incluye descripción, opciones de configuración, dependencias y ejemplos de uso. " +
            "IDs disponibles: poncho-table, poncho-map, poncho-map-filter, poncho-map-search, " +
            "poncho-map-provinces, national-holidays, mapa-argentina-svg, poncho-agenda, poncho-color, " +
            "gapi-sheet-data, translate-html, showdown-extensions")
    public String obtener_documentacion(
            @ToolParam(description = "ID del componente (ej: poncho-table, poncho-map, national-holidays)") 
            String componentId
    ) {
        return documentationService.getFormattedDocumentation(componentId);
    }
    
    @Tool(description = "Lista todos los componentes disponibles en la librería Poncho, agrupados por categoría. " +
            "Categorías: data (tablas, datos), maps (mapas), calendar (calendarios), design (colores, estilos), " +
            "i18n (internacionalización), content (markdown)")
    public String listar_componentes(
            @ToolParam(description = "Filtrar por categoría (opcional): data, maps, calendar, design, i18n, content") 
            String categoria
    ) {
        List<PonchoComponent> components;
        
        if (categoria != null && !categoria.isEmpty()) {
            components = documentationService.getComponentsByCategory(categoria.toLowerCase());
            if (components.isEmpty()) {
                return "Categoría no encontrada: " + categoria + "\n\n" +
                       "Categorías disponibles: " + documentationService.getCategories();
            }
        } else {
            components = documentationService.getAllComponents();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("# Componentes Poncho\n\n");
        
        if (categoria != null && !categoria.isEmpty()) {
            sb.append("**Categoría:** ").append(categoria).append("\n\n");
            formatComponentList(sb, components);
        } else {
            var grouped = components.stream()
                .collect(Collectors.groupingBy(PonchoComponent::category));
            
            for (var entry : grouped.entrySet()) {
                sb.append("## ").append(capitalizeCategory(entry.getKey())).append("\n\n");
                formatComponentList(sb, entry.getValue());
            }
        }
        
        sb.append("---\n");
        sb.append("Usa `obtener_documentacion` para ver detalles de un componente específico.\n");
        sb.append("Usa `buscar_componente` para buscar por funcionalidad.");
        
        return sb.toString();
    }
    
    @Tool(description = "Obtiene las dependencias (CSS y JavaScript) necesarias para usar un componente Poncho. " +
            "Retorna las URLs de los archivos que deben incluirse en el HTML.")
    public String obtener_dependencias(
            @ToolParam(description = "ID del componente") 
            String componentId
    ) {
        return documentationService.getComponent(componentId)
            .map(c -> {
                StringBuilder sb = new StringBuilder();
                sb.append("# Dependencias de ").append(c.name()).append("\n\n");
                
                if (c.dependencies() != null) {
                    if (!c.dependencies().css().isEmpty()) {
                        sb.append("## CSS (incluir en `<head>`)\n\n");
                        sb.append("```html\n");
                        for (String css : c.dependencies().css()) {
                            sb.append("<link href=\"").append(css).append("\" rel=\"stylesheet\">\n");
                        }
                        sb.append("```\n\n");
                    }
                    
                    if (!c.dependencies().js().isEmpty()) {
                        sb.append("## JavaScript (incluir antes de `</body>`)\n\n");
                        sb.append("```html\n");
                        for (String js : c.dependencies().js()) {
                            sb.append("<script src=\"").append(js).append("\"></script>\n");
                        }
                        sb.append("```\n\n");
                    }
                } else {
                    sb.append("Este componente no tiene dependencias adicionales.\n");
                    sb.append("Solo requiere poncho.min.js que incluye la funcionalidad base.\n");
                }
                
                return sb.toString();
            })
            .orElse("Componente no encontrado: " + componentId);
    }
    
    private void formatComponentList(StringBuilder sb, List<PonchoComponent> components) {
        for (PonchoComponent c : components) {
            sb.append("- **").append(c.name()).append("** (`").append(c.id()).append("`)\n");
            sb.append("  ").append(c.description()).append("\n\n");
        }
    }
    
    private String capitalizeCategory(String category) {
        return switch (category) {
            case "data" -> "Datos y Tablas";
            case "maps" -> "Mapas";
            case "calendar" -> "Calendarios";
            case "design" -> "Diseño y Estilos";
            case "i18n" -> "Internacionalización";
            case "content" -> "Contenido";
            default -> category.substring(0, 1).toUpperCase() + category.substring(1);
        };
    }
}
