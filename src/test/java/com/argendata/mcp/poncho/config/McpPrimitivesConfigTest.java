package com.argendata.mcp.poncho.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke test: verifica que Resources y Prompts MCP se registran correctamente.
 */
@SpringBootTest
class McpPrimitivesConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void ponchoResourcesBeanShouldBeRegisteredAndNonEmpty() {
        @SuppressWarnings("unchecked")
        List<Object> resources = (List<Object>) applicationContext.getBean("ponchoResources");
        assertNotNull(resources);
        assertFalse(resources.isEmpty(), "Debe haber al menos un resource (ej. poncho://templates/categories)");
    }

    @Test
    void ponchoPromptsBeanShouldBeRegisteredAndNonEmpty() {
        @SuppressWarnings("unchecked")
        List<Object> prompts = (List<Object>) applicationContext.getBean("ponchoPrompts");
        assertNotNull(prompts);
        assertFalse(prompts.isEmpty(), "Debe haber al menos un prompt guiado");
    }
}
