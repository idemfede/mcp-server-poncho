package com.argendata.mcp.poncho.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComponentSearchResultTest {

    @Test
    void shouldCreateFromPonchoComponent() {
        var component = new PonchoComponent(
            "poncho-map",
            "component",
            "PonchoMap",
            "maps",
            "Mapa interactivo con Leaflet",
            List.of("mapa", "geolocalización"),
            null,
            List.of(),
            null,
            null,
            null,
            null,
            null
        );
        
        var result = ComponentSearchResult.from(component, 0.95);
        
        assertEquals("poncho-map", result.id());
        assertEquals("PonchoMap", result.name());
        assertEquals("maps", result.category());
        assertEquals("Mapa interactivo con Leaflet", result.description());
        assertEquals(0.95, result.score());
    }

    @Test
    void shouldCreateFromTemplate() {
        var template = new PonchoComponent(
            "template-login",
            "template",
            "Login",
            "login-registro",
            "Formulario de login",
            List.of("login", "acceso"),
            null,
            List.of(),
            null,
            null,
            null,
            null,
            "pages/login-registro/login.html"
        );
        
        var result = ComponentSearchResult.from(template, 0.85);
        
        assertEquals("template-login", result.id());
        assertEquals("Login", result.name());
        assertEquals("login-registro", result.category());
        assertEquals(0.85, result.score());
    }

    @Test
    void shouldHandleZeroScore() {
        var component = new PonchoComponent(
            "test", "component", "Test", "test", "Desc", List.of(), null, List.of(), null, null, null, null, null
        );
        
        var result = ComponentSearchResult.from(component, 0.0);
        
        assertEquals(0.0, result.score());
    }
}
