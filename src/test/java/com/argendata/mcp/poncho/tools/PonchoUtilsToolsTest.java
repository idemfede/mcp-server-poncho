package com.argendata.mcp.poncho.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PonchoUtilsToolsTest {

    @Autowired
    private PonchoUtilsTools ponchoUtilsTools;

    @Test
    void obtenerColoresShouldReturnFullPaletteWhenNoGroup() {
        String result = ponchoUtilsTools.obtener_colores(null);

        assertNotNull(result);
        assertTrue(result.contains("Sistema de Colores Poncho"));
        assertTrue(result.contains("azul") || result.contains("Azul"));
        assertTrue(result.contains("rojo") || result.contains("Rojo"));
    }

    @Test
    void obtenerColoresShouldReturnSingleGroupWhenSpecified() {
        String result = ponchoUtilsTools.obtener_colores("azul");

        assertNotNull(result);
        assertTrue(result.contains("azul") || result.contains("Azul"));
        assertTrue(result.contains("#") || result.contains("var(--"));
    }

    @Test
    void obtenerColoresShouldRejectInvalidGroup() {
        String result = ponchoUtilsTools.obtener_colores("color-inexistente");

        assertNotNull(result);
        assertTrue(result.contains("no encontrado") || result.contains("Grupos disponibles"));
    }

    @Test
    void obtenerCodigosProvinciasShouldReturnFullListWhenNoFilter() {
        String result = ponchoUtilsTools.obtener_codigos_provincias(null);

        assertNotNull(result);
        assertTrue(result.contains("ISO 3166-2:AR") || result.contains("Provincias"));
        assertTrue(result.contains("AR-B") && result.contains("Buenos Aires"));
    }

    @Test
    void obtenerCodigosProvinciasShouldFilterByProvinceName() {
        String result = ponchoUtilsTools.obtener_codigos_provincias("Córdoba");

        assertNotNull(result);
        assertTrue(result.contains("AR-X") && result.contains("Córdoba"));
    }

    @Test
    void obtenerFeriadosShouldReturnHolidaysForYear() {
        String result = ponchoUtilsTools.obtener_feriados(2025);

        assertNotNull(result);
        assertTrue(result.contains("2025"));
        assertTrue(result.contains("Feriados") || result.contains("Inamovibles"));
        assertTrue(result.contains("Año Nuevo") || result.contains("25 de mayo"));
    }

    @Test
    void obtenerFeriadosShouldUseDefaultYearWhenNull() {
        String result = ponchoUtilsTools.obtener_feriados(null);

        assertNotNull(result);
        assertTrue(result.contains("Feriados") || result.contains("Inamovibles"));
    }

    @Test
    void validarConfiguracionWithoutConfigShouldReturnMinimalGuide() {
        String result = ponchoUtilsTools.validar_configuracion("poncho-table", null);

        assertNotNull(result);
        assertTrue(result.contains("poncho-table") || result.contains("Validación"));
        assertTrue(result.contains("jsonUrl") || result.contains("Configuración"));
    }

    @Test
    void validarConfiguracionWithConfigShouldReturnAnalysis() {
        String result = ponchoUtilsTools.validar_configuracion("poncho-table", "{\"tituloTabla\": \"Test\"}");

        assertNotNull(result);
        assertTrue(result.contains("Configuración recibida") || result.contains("Análisis"));
        assertTrue(result.contains("Test"));
    }

    @Test
    void validarConfiguracionShouldRejectEmptyComponent() {
        String result = ponchoUtilsTools.validar_configuracion("", "{}");

        assertNotNull(result);
        assertTrue(result.contains("Error") || result.contains("especificar"));
    }

    @Test
    void listarTemasMapaShouldReturnThemes() {
        String result = ponchoUtilsTools.listar_temas_mapa();

        assertNotNull(result);
        assertTrue(result.contains("PonchoMap") || result.contains("Temas"));
        assertTrue(result.contains("default") && result.contains("dark"));
    }
}
