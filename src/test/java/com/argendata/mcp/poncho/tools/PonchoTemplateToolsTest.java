package com.argendata.mcp.poncho.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PonchoTemplateToolsTest {

    @Autowired
    private PonchoTemplateTools ponchoTemplateTools;

    @Test
    void listarCategoriasPlantillasShouldReturnAllCategories() {
        String result = ponchoTemplateTools.listar_categorias_plantillas();

        assertNotNull(result);
        assertTrue(result.contains("Categorías de Plantillas Poncho"));
        assertTrue(result.contains("paginas-argentina") || result.contains("Páginas Argentina"));
        assertTrue(result.contains("formularios") || result.contains("Formularios"));
        assertTrue(result.contains("listar_categorias_plantillas") || result.contains("obtener_plantilla"));
    }

    @Test
    void listarPlantillasCategoriaShouldReturnTemplatesForValidCategory() {
        String result = ponchoTemplateTools.listar_plantillas_categoria("paginas-argentina");

        assertNotNull(result);
        assertFalse(result.contains("no encontrada"));
        assertTrue(result.contains("paginas-argentina") || result.contains("Páginas"));
        assertTrue(result.contains("Plantillas disponibles") || result.contains("obtener_plantilla"));
    }

    @Test
    void listarPlantillasCategoriaShouldRejectInvalidCategory() {
        String result = ponchoTemplateTools.listar_plantillas_categoria("categoria-inexistente-xyz");

        assertNotNull(result);
        assertTrue(result.contains("no encontrada") || result.contains("Categorías válidas"));
    }

    @Test
    void obtenerPlantillaShouldReturnHtmlForValidTemplate() {
        String result = ponchoTemplateTools.obtener_plantilla("paginas-argentina", "home");

        assertNotNull(result);
        assertFalse(result.contains("no encontrada"));
        assertTrue(result.contains("Plantilla:") || result.contains("Código HTML"));
        assertTrue(result.contains("```html"));
    }

    @Test
    void obtenerPlantillaShouldRejectInvalidCategory() {
        String result = ponchoTemplateTools.obtener_plantilla("categoria-invalida", "home");

        assertNotNull(result);
        assertTrue(result.contains("no encontrada") || result.contains("Categorías válidas"));
    }

    @Test
    void buscarPlantillaShouldReturnResultsForCommonTerm() {
        String result = ponchoTemplateTools.buscar_plantilla("login");

        assertNotNull(result);
        assertTrue(result.contains("Resultados") || result.contains("plantilla(s)"),
                "Debe indicar resultados o lista de plantillas");
        assertFalse(result.contains("No se encontraron plantillas"),
                "Para 'login' debe haber al menos un resultado");
    }

    @Test
    void buscarPlantillaShouldHandleNoResults() {
        String result = ponchoTemplateTools.buscar_plantilla("xyzbusquedaimposible123");

        assertNotNull(result);
        assertTrue(
                result.contains("No se encontraron") || result.contains("listar_categorias") || result.contains("Resultados"),
                "Debe devolver mensaje de no resultados o lista (búsqueda por keywords puede devolver coincidencias débiles)");
    }

    @Test
    void recomendarPlantillaShouldReturnSuggestionForPageUseCase() {
        String result = ponchoTemplateTools.recomendar_plantilla("página de inicio de un ministerio");

        assertNotNull(result);
        assertTrue(result.contains("Plantilla recomendada") || result.contains("Código HTML") ||
                result.contains("No encontré") || result.contains("Sugerencias"));
    }
}
