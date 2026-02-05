package com.argendata.mcp.poncho.model;

import java.util.List;

/**
 * Representa un componente o template de la librería Poncho
 */
public record PonchoComponent(
    String id,
    String type,  // "component" o "template"
    String name,
    String category,
    String description,
    List<String> keywords,
    ComponentDependencies dependencies,
    List<ComponentOption> options,
    String mainFunction,
    String mainClass,
    String mainObject,
    String usageExample,
    String templatePath  // Solo para templates
) {
    
    /**
     * Constructor de compatibilidad para componentes sin type ni templatePath
     */
    public PonchoComponent(
        String id,
        String name,
        String category,
        String description,
        List<String> keywords,
        ComponentDependencies dependencies,
        List<ComponentOption> options,
        String mainFunction,
        String mainClass,
        String mainObject,
        String usageExample
    ) {
        this(id, "component", name, category, description, keywords, dependencies, 
             options, mainFunction, mainClass, mainObject, usageExample, null);
    }
    
    public boolean isTemplate() {
        return "template".equals(type);
    }
    
    public boolean isComponent() {
        return "component".equals(type) || type == null;
    }
    
    public record ComponentDependencies(
        List<String> css,
        List<String> js
    ) {}
    
    public record ComponentOption(
        String name,
        String type,
        boolean required,
        String defaultValue,
        String description
    ) {}
}
