package com.argendata.mcp.poncho.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PonchoComponentTest {

    @Test
    void shouldCreateComponentWithAllFields() {
        var dependencies = new PonchoComponent.ComponentDependencies(
            List.of("poncho.min.css"),
            List.of("poncho.min.js")
        );
        
        var options = List.of(
            new PonchoComponent.ComponentOption("id", "string", true, null, "ID del elemento")
        );
        
        var component = new PonchoComponent(
            "poncho-table",
            "component",
            "PonchoTable",
            "data",
            "Tabla interactiva con filtros",
            List.of("tabla", "filtros", "datos"),
            dependencies,
            options,
            "ponchoTable",
            "PonchoTable",
            null,
            "<div id='table'></div>",
            null
        );
        
        assertEquals("poncho-table", component.id());
        assertEquals("component", component.type());
        assertEquals("PonchoTable", component.name());
        assertEquals("data", component.category());
        assertNotNull(component.dependencies());
        assertEquals(1, component.dependencies().css().size());
        assertEquals(1, component.options().size());
        assertTrue(component.isComponent());
        assertFalse(component.isTemplate());
        assertNull(component.templatePath());
    }

    @Test
    void shouldCreateTemplateWithPath() {
        var template = new PonchoComponent(
            "template-login",
            "template",
            "Login",
            "login-registro",
            "Formulario de login",
            List.of("login", "acceso", "usuario"),
            null,
            List.of(),
            null,
            null,
            null,
            null,
            "pages/login-registro/login.html"
        );
        
        assertEquals("template-login", template.id());
        assertEquals("template", template.type());
        assertTrue(template.isTemplate());
        assertFalse(template.isComponent());
        assertEquals("pages/login-registro/login.html", template.templatePath());
    }

    @Test
    void shouldUseCompatibilityConstructorForComponents() {
        var component = new PonchoComponent(
            "poncho-table",
            "PonchoTable",
            "data",
            "Descripción",
            List.of("tabla"),
            null,
            List.of(),
            null,
            null,
            null,
            null
        );
        
        // El constructor de compatibilidad asigna type="component" y templatePath=null
        assertEquals("component", component.type());
        assertTrue(component.isComponent());
        assertNull(component.templatePath());
    }

    @Test
    void shouldCreateComponentWithNullOptionalFields() {
        var component = new PonchoComponent(
            "test-component",
            "component",
            "Test",
            "test",
            "Descripción",
            List.of(),
            null,
            List.of(),
            null,
            null,
            null,
            null,
            null
        );
        
        assertNull(component.dependencies());
        assertNull(component.mainFunction());
        assertTrue(component.keywords().isEmpty());
    }

    @Test
    void componentOptionShouldHandleDefaultValues() {
        var optionWithDefault = new PonchoComponent.ComponentOption(
            "pageSize", "number", false, "10", "Items por página"
        );
        
        var optionRequired = new PonchoComponent.ComponentOption(
            "data", "array", true, null, "Datos de la tabla"
        );
        
        assertEquals("10", optionWithDefault.defaultValue());
        assertFalse(optionWithDefault.required());
        
        assertNull(optionRequired.defaultValue());
        assertTrue(optionRequired.required());
    }

    @Test
    void isComponentShouldReturnTrueForNullType() {
        // Simula un componente con type null (compatibilidad con JSON antiguo)
        var component = new PonchoComponent(
            "test",
            null,  // type null
            "Test",
            "test",
            "Desc",
            List.of(),
            null,
            List.of(),
            null,
            null,
            null,
            null,
            null
        );
        
        assertTrue(component.isComponent());
        assertFalse(component.isTemplate());
    }
}
