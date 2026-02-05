package com.argendata.mcp.poncho.service;

import com.argendata.mcp.poncho.service.KeywordSearchService.SearchResult;
import com.argendata.mcp.poncho.service.KeywordSearchService.SearchableItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KeywordSearchServiceTest {

    private KeywordSearchService searchService;
    private List<SearchableItem> testItems;

    @BeforeEach
    void setUp() {
        searchService = new KeywordSearchService();
        
        testItems = List.of(
            new SearchableItem(
                "poncho-table",
                "PonchoTable",
                "data",
                "Tabla interactiva con filtros dependientes, paginación y búsqueda",
                List.of("tabla", "filtros", "paginación", "búsqueda", "grilla", "datos"),
                "component",
                Map.of()
            ),
            new SearchableItem(
                "poncho-map",
                "PonchoMap",
                "maps",
                "Mapas interactivos basados en Leaflet con marcadores y clusters",
                List.of("mapa", "leaflet", "marcadores", "geolocalización", "ubicación"),
                "component",
                Map.of()
            ),
            new SearchableItem(
                "template-login",
                "Login",
                "login-registro",
                "Formulario de inicio de sesión con opción de recuperar contraseña",
                List.of("login", "iniciar sesión", "acceso", "usuario", "contraseña"),
                "template",
                Map.of()
            ),
            new SearchableItem(
                "template-home",
                "Home",
                "paginas-argentina",
                "Página de inicio institucional con destacados y noticias",
                List.of("home", "inicio", "landing", "principal", "institucional"),
                "template",
                Map.of()
            )
        );
    }

    @Test
    void shouldFindExactKeywordMatch() {
        List<SearchResult> results = searchService.search("tabla", testItems, 5);
        
        assertFalse(results.isEmpty());
        assertEquals("poncho-table", results.get(0).item().id());
        assertTrue(results.get(0).score() > 0);
    }

    @Test
    void shouldFindPartialKeywordMatch() {
        List<SearchResult> results = searchService.search("filtro", testItems, 5);
        
        assertFalse(results.isEmpty());
        // "filtro" debería matchear con "filtros"
        boolean foundTable = results.stream()
            .anyMatch(r -> r.item().id().equals("poncho-table"));
        assertTrue(foundTable);
    }

    @Test
    void shouldFindByDescription() {
        List<SearchResult> results = searchService.search("Leaflet", testItems, 5);
        
        assertFalse(results.isEmpty());
        assertEquals("poncho-map", results.get(0).item().id());
    }

    @Test
    void shouldFindByName() {
        List<SearchResult> results = searchService.search("Login", testItems, 5);
        
        assertFalse(results.isEmpty());
        assertEquals("template-login", results.get(0).item().id());
    }

    @Test
    void shouldFindByCategory() {
        List<SearchResult> results = searchService.search("maps", testItems, 5);
        
        assertFalse(results.isEmpty());
        assertEquals("poncho-map", results.get(0).item().id());
    }

    @Test
    void shouldHandleAccentedQueries() {
        // "paginación" tiene acento en el keyword
        List<SearchResult> results = searchService.search("paginacion", testItems, 5);
        
        assertFalse(results.isEmpty());
        assertEquals("poncho-table", results.get(0).item().id());
    }

    @Test
    void shouldHandleMultipleWords() {
        List<SearchResult> results = searchService.search("tabla filtros datos", testItems, 5);
        
        assertFalse(results.isEmpty());
        assertEquals("poncho-table", results.get(0).item().id());
    }

    @Test
    void shouldReturnEmptyForNoMatch() {
        List<SearchResult> results = searchService.search("xyznonexistent123", testItems, 5);
        
        assertTrue(results.isEmpty());
    }

    @Test
    void shouldReturnEmptyForNullQuery() {
        List<SearchResult> results = searchService.search(null, testItems, 5);
        
        assertTrue(results.isEmpty());
    }

    @Test
    void shouldReturnEmptyForBlankQuery() {
        List<SearchResult> results = searchService.search("   ", testItems, 5);
        
        assertTrue(results.isEmpty());
    }

    @Test
    void shouldLimitResults() {
        List<SearchResult> results = searchService.search("a", testItems, 2);
        
        assertTrue(results.size() <= 2);
    }

    @Test
    void shouldSortByScoreDescending() {
        List<SearchResult> results = searchService.search("tabla datos", testItems, 5);
        
        if (results.size() > 1) {
            for (int i = 0; i < results.size() - 1; i++) {
                assertTrue(results.get(i).score() >= results.get(i + 1).score(),
                    "Los resultados deben estar ordenados por score descendente");
            }
        }
    }

    @Test
    void shouldIgnoreStopWords() {
        // "de" y "la" son stop words
        List<SearchResult> results = searchService.search("de la tabla", testItems, 5);
        
        // Debería encontrar tabla ignorando "de" y "la"
        assertFalse(results.isEmpty());
        assertEquals("poncho-table", results.get(0).item().id());
    }

    @Test
    void shouldHandleSpecialCharacters() {
        List<SearchResult> results = searchService.search("inicio sesión", testItems, 5);
        
        assertFalse(results.isEmpty());
        // Debería encontrar login por "iniciar sesión" keyword
        boolean foundLogin = results.stream()
            .anyMatch(r -> r.item().id().equals("template-login"));
        assertTrue(foundLogin);
    }

    @Test
    void shouldFindTemplatesByType() {
        // Buscar "página inicio" debería encontrar el template home
        List<SearchResult> results = searchService.search("página inicio institucional", testItems, 5);
        
        assertFalse(results.isEmpty());
        boolean foundHome = results.stream()
            .anyMatch(r -> r.item().id().equals("template-home"));
        assertTrue(foundHome);
    }
}
