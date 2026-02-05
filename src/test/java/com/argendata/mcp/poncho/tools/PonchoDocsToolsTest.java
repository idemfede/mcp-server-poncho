package com.argendata.mcp.poncho.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PonchoDocsToolsTest {

    @Autowired
    private PonchoDocsTools ponchoDocsTools;

    @Test
    void buscarComponenteShouldReturnResults() {
        String result = ponchoDocsTools.buscar_componente("tabla", 5);
        
        assertNotNull(result);
        assertTrue(result.contains("Resultados de búsqueda"));
        assertFalse(result.contains("No se encontraron"));
    }

    @Test
    void buscarComponenteShouldHandleNoResults() {
        String result = ponchoDocsTools.buscar_componente("xyzcomponenteinexistente123", 5);
        
        assertNotNull(result);
        // Puede retornar resultados con baja relevancia o mensaje de no encontrado
    }

    @Test
    void buscarComponenteShouldUseDefaultMaxResults() {
        String result = ponchoDocsTools.buscar_componente("mapa", null);
        
        assertNotNull(result);
        assertTrue(result.contains("Resultados de búsqueda"));
    }

    @Test
    void obtenerDocumentacionShouldReturnComponentDocs() {
        String result = ponchoDocsTools.obtener_documentacion("poncho-table");
        
        assertNotNull(result);
        assertFalse(result.contains("Componente no encontrado"));
    }

    @Test
    void obtenerDocumentacionShouldHandleInvalidId() {
        String result = ponchoDocsTools.obtener_documentacion("componente-invalido");
        
        assertNotNull(result);
        assertTrue(result.contains("Componente no encontrado"));
    }

    @Test
    void listarComponentesShouldReturnAllComponents() {
        String result = ponchoDocsTools.listar_componentes(null);
        
        assertNotNull(result);
        assertTrue(result.contains("Componentes Poncho"));
    }

    @Test
    void listarComponentesShouldFilterByCategory() {
        String result = ponchoDocsTools.listar_componentes("data");
        
        assertNotNull(result);
        assertTrue(result.contains("data"));
    }

    @Test
    void listarComponentesShouldHandleInvalidCategory() {
        String result = ponchoDocsTools.listar_componentes("categoria-inexistente");
        
        assertNotNull(result);
        assertTrue(result.contains("Categoría no encontrada") || result.contains("Categorías disponibles"));
    }

    @Test
    void obtenerDependenciasShouldReturnDependencies() {
        String result = ponchoDocsTools.obtener_dependencias("poncho-table");
        
        assertNotNull(result);
        assertTrue(result.contains("Dependencias") || result.contains("dependencias"));
    }

    @Test
    void obtenerDependenciasShouldHandleInvalidComponent() {
        String result = ponchoDocsTools.obtener_dependencias("componente-invalido");
        
        assertNotNull(result);
        assertTrue(result.contains("Componente no encontrado"));
    }
}
