package com.argendata.mcp.poncho.tools;

import com.argendata.mcp.poncho.model.GeneratedCode;
import com.argendata.mcp.poncho.service.CodeGeneratorService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Tools MCP para generación de código Poncho
 */
@Component
public class PonchoGeneratorTools {
    
    private final CodeGeneratorService codeGeneratorService;
    
    public PonchoGeneratorTools(CodeGeneratorService codeGeneratorService) {
        this.codeGeneratorService = codeGeneratorService;
    }
    
    @Tool(description = "Genera código HTML y JavaScript para una PonchoTable (tabla interactiva con filtros). " +
            "Soporta datos desde JSON URL o Google Sheets. Incluye filtros dependientes, búsqueda y paginación.")
    public String generar_tabla(
            @ToolParam(description = "Título/caption de la tabla") 
            String titulo,
            @ToolParam(description = "URL del JSON con los datos (opcional si usa Google Sheets)") 
            String jsonUrl,
            @ToolParam(description = "ID del spreadsheet de Google Sheets (opcional)") 
            String spreadsheetId,
            @ToolParam(description = "Nombre de la hoja en Google Sheets (default: 'dataset')") 
            String sheetName,
            @ToolParam(description = "Índice de columna para ordenar (default: 0)") 
            Integer ordenColumna,
            @ToolParam(description = "Tipo de orden: 'asc' o 'desc' (default: 'asc')") 
            String ordenTipo,
            @ToolParam(description = "Cantidad de items por página (default: 10)") 
            Integer itemsPorPagina,
            @ToolParam(description = "Habilitar parámetros en URL para filtros") 
            Boolean habilitarUrlParams,
            @ToolParam(description = "Actualizar URL cuando se usan filtros") 
            Boolean habilitarPushState,
            @ToolParam(description = "Mostrar botón para copiar URL de resultados") 
            Boolean habilitarCopiarResultados,
            @ToolParam(description = "Modo wizard: mostrar filtros progresivamente") 
            Boolean modoWizard
    ) {
        GeneratedCode code = codeGeneratorService.generateTable(
            titulo,
            jsonUrl,
            spreadsheetId,
            sheetName,
            ordenColumna != null ? ordenColumna : 0,
            ordenTipo,
            itemsPorPagina != null ? itemsPorPagina : 10,
            null,
            true,
            true,
            habilitarUrlParams != null && habilitarUrlParams,
            habilitarPushState != null && habilitarPushState,
            habilitarCopiarResultados != null && habilitarCopiarResultados,
            modoWizard != null && modoWizard
        );
        
        return formatGeneratedCode("PonchoTable", code);
    }
    
    @Tool(description = "Genera código HTML y JavaScript para un PonchoMap (mapa interactivo con Leaflet). " +
            "Soporta marcadores, clusters, tooltips y múltiples temas visuales.")
    public String generar_mapa(
            @ToolParam(description = "Identificador del scope del mapa (default: 'poncho-map')") 
            String scope,
            @ToolParam(description = "Altura del mapa en píxeles (default: 400)") 
            Integer altura,
            @ToolParam(description = "Latitud del centro del mapa (default: Buenos Aires)") 
            Double latitudCentro,
            @ToolParam(description = "Longitud del centro del mapa (default: Buenos Aires)") 
            Double longitudCentro,
            @ToolParam(description = "Nivel de zoom inicial (default: 12)") 
            Integer zoom,
            @ToolParam(description = "Clave del JSON para el título de marcadores (default: 'name')") 
            String claveTitle,
            @ToolParam(description = "Título del resumen del mapa") 
            String tituloResumen,
            @ToolParam(description = "Mostrar tooltips en marcadores") 
            Boolean mostrarTooltips,
            @ToolParam(description = "Mostrar selector de tema visual") 
            Boolean mostrarSelectorTema,
            @ToolParam(description = "Agrupar marcadores cercanos en clusters") 
            Boolean habilitarClusters,
            @ToolParam(description = "URL del JSON con los datos (opcional)") 
            String jsonUrl,
            @ToolParam(description = "ID del spreadsheet de Google Sheets (opcional)") 
            String spreadsheetId,
            @ToolParam(description = "Nombre de la hoja en Google Sheets") 
            String sheetName
    ) {
        GeneratedCode code = codeGeneratorService.generateMap(
            scope,
            "map",
            altura != null ? altura : 400,
            latitudCentro != null ? latitudCentro : -34.6037,
            longitudCentro != null ? longitudCentro : -58.3816,
            zoom != null ? zoom : 12,
            claveTitle,
            tituloResumen,
            mostrarTooltips != null && mostrarTooltips,
            mostrarSelectorTema == null || mostrarSelectorTema,
            false,
            habilitarClusters != null && habilitarClusters,
            false,
            false,
            jsonUrl,
            spreadsheetId,
            sheetName
        );
        
        return formatGeneratedCode("PonchoMap", code);
    }
    
    @Tool(description = "Genera un archivo HTML base VACÍO con las dependencias de Poncho configuradas. " +
            "IMPORTANTE: Antes de usar este tool, considera usar 'recomendar_plantilla' para obtener " +
            "una página completa pre-diseñada que solo necesita personalización. " +
            "Este tool es útil solo cuando necesitas una estructura mínima sin contenido predefinido. " +
            "Incluye Bootstrap 3.4.1, Font Awesome, fuente Encode Sans y Poncho CSS/JS.")
    public String generar_html_base(
            @ToolParam(description = "Título de la página") 
            String titulo,
            @ToolParam(description = "Incluir dependencias para PonchoTable") 
            Boolean incluirTabla,
            @ToolParam(description = "Incluir dependencias para PonchoMap (Leaflet)") 
            Boolean incluirMapa
    ) {
        GeneratedCode code = codeGeneratorService.generateSetup(
            titulo,
            incluirTabla != null && incluirTabla,
            incluirMapa != null && incluirMapa
        );
        
        return formatGeneratedCode("HTML Base Poncho", code);
    }
    
    @Tool(description = "Genera código para un calendario de feriados nacionales argentinos. " +
            "Soporta internacionalización y diferentes tipos de feriados.")
    public String generar_calendario_feriados(
            @ToolParam(description = "Año del calendario (ej: 2025)") 
            Integer anio,
            @ToolParam(description = "Idioma: 'es' (español) o 'en' (inglés)") 
            String idioma,
            @ToolParam(description = "ID del contenedor HTML (default: 'calendar-container')") 
            String containerId
    ) {
        GeneratedCode code = codeGeneratorService.generateHolidaysCalendar(
            anio != null ? anio : 2025,
            idioma,
            containerId,
            "month-tpl"
        );
        
        return formatGeneratedCode("Calendario de Feriados", code);
    }
    
    @Tool(description = "Genera código para un mapa SVG de Argentina con provincias personalizables. " +
            "Permite colorear provincias individuales usando códigos ISO 3166-2:AR.")
    public String generar_mapa_argentina_svg(
            @ToolParam(description = "ID del contenedor HTML (default: 'js-mapa-svg')") 
            String containerId,
            @ToolParam(description = "Lista de códigos de provincias a colorear (ej: 'AR-B,AR-C' o '*' para todas)") 
            String provincias,
            @ToolParam(description = "Color para las provincias seleccionadas (hex o nombre CSS)") 
            String color,
            @ToolParam(description = "Color de las líneas del mapa") 
            String colorLineas,
            @ToolParam(description = "Grosor de las líneas (default: 1)") 
            Double grosorLineas,
            @ToolParam(description = "Color por defecto para provincias no seleccionadas") 
            String colorDefault
    ) {
        List<String> provinciasList = null;
        if (provincias != null && !provincias.isEmpty()) {
            if (provincias.equals("*")) {
                provinciasList = Arrays.asList("*");
            } else {
                provinciasList = Arrays.asList(provincias.split(","));
            }
        }
        
        GeneratedCode code = codeGeneratorService.generateArgentinaMapSvg(
            containerId,
            provinciasList,
            color,
            colorLineas,
            grosorLineas != null ? grosorLineas : 1.0,
            colorDefault,
            null
        );
        
        return formatGeneratedCode("Mapa Argentina SVG", code);
    }
    
    @Tool(description = "Genera código para conectar una PonchoTable o PonchoMap con Google Sheets. " +
            "Retorna el código JavaScript necesario para obtener datos de una hoja de cálculo.")
    public String generar_conexion_google_sheets(
            @ToolParam(description = "ID del documento de Google Sheets (se obtiene de la URL)") 
            String spreadsheetId,
            @ToolParam(description = "Nombre de la pestaña/hoja (default: 'dataset')") 
            String sheetName
    ) {
        String sheet = sheetName != null ? sheetName : "dataset";
        
        StringBuilder sb = new StringBuilder();
        sb.append("# Conexión con Google Sheets\n\n");
        sb.append("## Código JavaScript\n\n");
        sb.append("```javascript\n");
        sb.append("// Crear instancia de GapiSheetData\n");
        sb.append("const gapi = new GapiSheetData();\n\n");
        sb.append("// Generar URL del endpoint JSON\n");
        sb.append("const url = gapi.url(\"").append(sheet).append("\", \"").append(spreadsheetId).append("\");\n\n");
        sb.append("// Usar con fetch para obtener los datos\n");
        sb.append("const response = await fetch(url);\n");
        sb.append("const data = await response.json();\n");
        sb.append("```\n\n");
        
        sb.append("## Configuración del Google Sheet\n\n");
        sb.append("1. El documento debe ser público o tener permisos de lectura\n");
        sb.append("2. La primera fila debe contener los headers de las columnas\n");
        sb.append("3. Para filtros en PonchoTable, usar prefijo `filtro-` en el nombre de columna\n");
        sb.append("   - Ejemplo: `filtro-provincia`, `filtro-categoria`\n\n");
        
        sb.append("## Uso con PonchoTable\n\n");
        sb.append("```javascript\n");
        sb.append("const options = {\n");
        sb.append("    jsonUrl: url,\n");
        sb.append("    tituloTabla: \"Mi tabla\",\n");
        sb.append("    // ... otras opciones\n");
        sb.append("};\n");
        sb.append("ponchoTableDependant(options);\n");
        sb.append("```\n\n");
        
        sb.append("## Uso con PonchoMap\n\n");
        sb.append("```javascript\n");
        sb.append("const response = await fetch(url);\n");
        sb.append("const entries = await response.json();\n");
        sb.append("const map = new PonchoMap(entries, mapOptions);\n");
        sb.append("map.render();\n");
        sb.append("```\n");
        
        return sb.toString();
    }
    
    private String formatGeneratedCode(String componentName, GeneratedCode code) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Código generado: ").append(componentName).append("\n\n");
        
        if (!code.cssDependencies().isEmpty()) {
            sb.append("## Dependencias CSS\n\n");
            sb.append("Incluir en `<head>`:\n");
            sb.append("```html\n");
            for (String css : code.cssDependencies()) {
                sb.append("<link href=\"").append(css).append("\" rel=\"stylesheet\">\n");
            }
            sb.append("```\n\n");
        }
        
        if (!code.jsDependencies().isEmpty()) {
            sb.append("## Dependencias JavaScript\n\n");
            sb.append("Incluir antes de `</body>`:\n");
            sb.append("```html\n");
            for (String js : code.jsDependencies()) {
                sb.append("<script src=\"").append(js).append("\"></script>\n");
            }
            sb.append("```\n\n");
        }
        
        sb.append("## Código HTML y JavaScript\n\n");
        sb.append("```html\n");
        sb.append(code.html());
        sb.append("\n```\n\n");
        
        if (code.instructions() != null) {
            sb.append(code.instructions());
        }
        
        return sb.toString();
    }
}
