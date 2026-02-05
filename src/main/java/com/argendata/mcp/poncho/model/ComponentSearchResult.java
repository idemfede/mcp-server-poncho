package com.argendata.mcp.poncho.model;

/**
 * Resultado de búsqueda de componentes
 */
public record ComponentSearchResult(
    String id,
    String name,
    String category,
    String description,
    double score
) {
    public static ComponentSearchResult from(PonchoComponent component, double score) {
        return new ComponentSearchResult(
            component.id(),
            component.name(),
            component.category(),
            component.description(),
            score
        );
    }
}
