package com.argendata.mcp.poncho.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PonchoGeneratorToolsTest {

    @Autowired
    private PonchoGeneratorTools ponchoGeneratorTools;

    @Test
    void generarTablaShouldReturnHtmlAndDependencies() {
        String result = ponchoGeneratorTools.generar_tabla(
                "Mi Tabla",
                "https://ejemplo.com/datos.json",
                null, null, null, null, null, null, null, null, null
        );

        assertNotNull(result);
        assertTrue(result.contains("PonchoTable") || result.contains("Código generado"));
        assertTrue(result.contains("```html") || result.contains("Dependencias"));
    }

    @Test
    void generarMapaShouldReturnHtmlAndDependencies() {
        String result = ponchoGeneratorTools.generar_mapa(
                "mi-mapa", 400, -34.6, -58.4, 12,
                "name", "Resumen", true, true, false,
                null, null, null
        );

        assertNotNull(result);
        assertTrue(result.contains("PonchoMap") || result.contains("Código generado"));
        assertTrue(result.contains("```html") || result.contains("Dependencias"));
    }

    @Test
    void generarHtmlBaseShouldReturnSetupWithTitle() {
        String result = ponchoGeneratorTools.generar_html_base("Mi Página", false, false);

        assertNotNull(result);
        assertTrue(result.contains("HTML Base") || result.contains("Código generado"));
        assertTrue(result.contains("Mi Página") || result.contains("titulo") || result.contains("<title"));
    }

    @Test
    void generarHtmlBaseWithTableAndMapShouldIncludeDependencies() {
        String result = ponchoGeneratorTools.generar_html_base("Página con tabla y mapa", true, true);

        assertNotNull(result);
        assertTrue(result.contains("poncho") || result.contains("leaflet") || result.contains("script") || result.contains("link"));
    }

    @Test
    void generarCalendarioFeriadosShouldReturnCode() {
        String result = ponchoGeneratorTools.generar_calendario_feriados(2025, "es", null);

        assertNotNull(result);
        assertTrue(result.contains("Calendario") || result.contains("Feriados") || result.contains("Código generado"));
    }

    @Test
    void generarMapaArgentinaSvgShouldReturnCode() {
        String result = ponchoGeneratorTools.generar_mapa_argentina_svg(
                "mapa-arg", "AR-B,AR-C", "#039BE5", null, null, null
        );

        assertNotNull(result);
        assertTrue(result.contains("Mapa Argentina") || result.contains("Código generado") || result.contains("svg"));
    }

    @Test
    void generarConexionGoogleSheetsShouldReturnJsSnippet() {
        String result = ponchoGeneratorTools.generar_conexion_google_sheets("abc123", "dataset");

        assertNotNull(result);
        assertTrue(result.contains("Google Sheets") || result.contains("GapiSheetData"));
        assertTrue(result.contains("abc123"));
        assertTrue(result.contains("dataset"));
    }
}
