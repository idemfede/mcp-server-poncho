package com.argendata.mcp.poncho.service;

import com.argendata.mcp.poncho.model.GeneratedCode;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Servicio para generar código de componentes Poncho
 */
@Service
public class CodeGeneratorService {
    
    private static final Logger log = LoggerFactory.getLogger(CodeGeneratorService.class);
    
    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    private final Map<String, String> templates = new HashMap<>();
    
    @PostConstruct
    public void init() {
        loadTemplates();
    }
    
    private void loadTemplates() {
        String[] templateNames = {
            "setup.html.mustache",
            "poncho-table.html.mustache",
            "poncho-map.html.mustache",
            "national-holidays.html.mustache",
            "mapa-argentina-svg.html.mustache"
        };
        
        for (String name : templateNames) {
            try {
                var resource = getClass().getClassLoader()
                    .getResourceAsStream("templates/" + name);
                if (resource != null) {
                    String content = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
                    templates.put(name.replace(".mustache", ""), content);
                    log.info("Template cargado: {}", name);
                }
            } catch (Exception e) {
                log.error("Error cargando template {}: {}", name, e.getMessage());
            }
        }
    }
    
    /**
     * Genera código para una PonchoTable
     */
    public GeneratedCode generateTable(
            String caption,
            String jsonUrl,
            String spreadsheetId,
            String sheetName,
            int orderColumn,
            String orderType,
            int itemsPerPage,
            List<String> hiddenColumns,
            boolean enableFilters,
            boolean enableSearch,
            boolean enableUrlParams,
            boolean enablePushState,
            boolean enableCopyResults,
            boolean enableWizard) {
        
        Map<String, Object> context = new HashMap<>();
        context.put("caption", caption != null ? caption : "Datos");
        context.put("orderColumn", orderColumn);
        context.put("orderType", orderType != null ? orderType : "asc");
        context.put("itemsPerPage", itemsPerPage > 0 ? itemsPerPage : 10);
        context.put("hiddenColumns", hiddenColumns != null ? hiddenColumns : Collections.emptyList());
        context.put("filterClasses", Arrays.asList("col-sm-12", "col-md-6"));
        context.put("allowedTags", Collections.singletonList("*"));
        context.put("orderFilter", true);
        
        if (spreadsheetId != null && !spreadsheetId.isEmpty()) {
            context.put("useGoogleSheets", true);
            context.put("spreadsheetId", spreadsheetId);
            context.put("sheetName", sheetName != null ? sheetName : "dataset");
        } else {
            context.put("useGoogleSheets", false);
            context.put("jsonUrl", jsonUrl != null ? jsonUrl : "data.json");
        }
        
        context.put("enableWizard", enableWizard);
        context.put("enableUrlParams", enableUrlParams);
        context.put("enablePushState", enablePushState);
        context.put("enableCopyResults", enableCopyResults);
        
        String html = renderTemplate("poncho-table.html", context);
        
        List<String> cssDeps = Arrays.asList(
            "https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css",
            "https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/css/poncho.min.css",
            "https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/css/ponchoTable-1.1.css"
        );
        
        List<String> jsDeps = Arrays.asList(
            "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js",
            "https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js",
            "https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/js/datatables.min.js",
            "https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/js/poncho.min.js"
        );
        
        String instructions = """
            ## Instrucciones de uso
            
            1. Incluir las dependencias CSS en el <head>
            2. Incluir las dependencias JS antes de </body>
            3. Pegar el HTML generado donde corresponda
            4. El script se ejecuta automáticamente al cargar la página
            
            ### Para filtros dependientes
            - En Google Sheets, usar prefijo "filtro-" en las columnas de filtro
            - Ejemplo: "filtro-provincia", "filtro-categoria"
            """;
        
        return new GeneratedCode(html, null, cssDeps, jsDeps, instructions);
    }
    
    /**
     * Genera código para un PonchoMap
     */
    public GeneratedCode generateMap(
            String scope,
            String mapId,
            int mapHeight,
            double centerLatitude,
            double centerLongitude,
            int zoomLevel,
            String titleKey,
            String summaryTitle,
            boolean showTooltip,
            boolean showThemeTool,
            boolean hideInfo,
            boolean enableCluster,
            boolean showFilters,
            boolean showSearch,
            String jsonUrl,
            String spreadsheetId,
            String sheetName) {
        
        Map<String, Object> context = new HashMap<>();
        context.put("scope", scope != null ? scope : "poncho-map");
        context.put("mapId", mapId != null ? mapId : "map");
        context.put("mapHeight", mapHeight > 0 ? mapHeight : 400);
        context.put("centerLatitude", centerLatitude != 0 ? centerLatitude : -34.6037);
        context.put("centerLongitude", centerLongitude != 0 ? centerLongitude : -58.3816);
        context.put("zoomLevel", zoomLevel > 0 ? zoomLevel : 12);
        context.put("titleKey", titleKey != null ? titleKey : "name");
        context.put("summaryTitle", summaryTitle != null ? summaryTitle : "Ubicaciones");
        context.put("showTooltip", showTooltip);
        context.put("showThemeTool", showThemeTool);
        context.put("hideInfo", hideInfo);
        context.put("hideSummary", false);
        context.put("enableCluster", enableCluster);
        context.put("showFilters", showFilters);
        context.put("showSearch", showSearch);
        
        if (spreadsheetId != null && !spreadsheetId.isEmpty()) {
            context.put("useGoogleSheets", true);
            context.put("spreadsheetId", spreadsheetId);
            context.put("sheetName", sheetName != null ? sheetName : "dataset");
            context.put("useStaticData", false);
        } else if (jsonUrl != null && !jsonUrl.isEmpty()) {
            context.put("useGoogleSheets", false);
            context.put("jsonUrl", jsonUrl);
            context.put("useStaticData", false);
        } else {
            context.put("useGoogleSheets", false);
            context.put("useStaticData", true);
            context.put("latitude", centerLatitude);
            context.put("longitude", centerLongitude);
            context.put("titleValue", "Mi ubicación");
        }
        
        String html = renderTemplate("poncho-map.html", context);
        
        List<String> cssDeps = Arrays.asList(
            "https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css",
            "https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/css/poncho.min.css",
            "https://mapa-ign.argentina.gob.ar/js/leaflet/leaflet.css"
        );
        
        List<String> jsDeps = Arrays.asList(
            "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js",
            "https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js",
            "https://mapa-ign.argentina.gob.ar/js/leaflet/leaflet.js",
            "https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/js/poncho.min.js"
        );
        
        String instructions = """
            ## Instrucciones de uso
            
            1. Incluir las dependencias CSS (incluyendo Leaflet) en el <head>
            2. Incluir las dependencias JS (incluyendo Leaflet) antes de </body>
            3. Pegar el HTML generado donde corresponda
            
            ### Formato de datos
            Los datos deben tener las propiedades:
            - `latitud` o `lat`: Latitud del punto
            - `longitud` o `lng`: Longitud del punto
            - Clave configurada en `titleKey` para el nombre
            
            ### Temas disponibles
            default, contrast, dark, grayscale, sepia, blue, relax, transparent
            """;
        
        return new GeneratedCode(html, null, cssDeps, jsDeps, instructions);
    }
    
    /**
     * Genera HTML base con dependencias Poncho
     */
    public GeneratedCode generateSetup(
            String title,
            boolean includeTable,
            boolean includeMap) {
        
        Map<String, Object> context = new HashMap<>();
        context.put("title", title != null ? title : "Mi página con Poncho");
        context.put("includeTableStyles", includeTable);
        context.put("includeTableScripts", includeTable);
        context.put("includeMapStyles", includeMap);
        context.put("includeMapScripts", includeMap);
        
        String html = renderTemplate("setup.html", context);
        
        List<String> cssDeps = new ArrayList<>();
        cssDeps.add("https://fonts.googleapis.com/css2?family=Encode+Sans:wght@100;200;300;400;500;600;700;800;900&display=swap");
        cssDeps.add("https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css");
        cssDeps.add("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css");
        cssDeps.add("https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/css/poncho.min.css");
        
        List<String> jsDeps = new ArrayList<>();
        jsDeps.add("https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js");
        jsDeps.add("https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js");
        jsDeps.add("https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/js/poncho.min.js");
        
        String instructions = """
            ## HTML Base con Poncho
            
            Este archivo incluye todas las dependencias básicas de Poncho:
            - Bootstrap 3.4.1
            - Font Awesome 4.7
            - Fuente Encode Sans
            - CSS y JS de Poncho
            
            Agrega tu contenido dentro del <main class="container">
            """;
        
        return new GeneratedCode(html, null, cssDeps, jsDeps, instructions);
    }
    
    /**
     * Genera código para calendario de feriados
     */
    public GeneratedCode generateHolidaysCalendar(
            int year,
            String lang,
            String containerId,
            String templateId) {
        
        Map<String, Object> context = new HashMap<>();
        context.put("year", year > 0 ? year : 2025);
        context.put("lang", lang != null ? lang : "es");
        context.put("containerId", containerId != null ? containerId : "calendar-container");
        context.put("templateId", templateId != null ? templateId : "month-tpl");
        context.put("allowHTML", true);
        context.put("inamovibleClass", "primary");
        context.put("trasladableClass", "success");
        context.put("noLaborableClass", "nl");
        context.put("turisticoClass", "turistico");
        context.put("holidays", Collections.emptyList());
        
        String html = renderTemplate("national-holidays.html", context);
        
        List<String> cssDeps = Arrays.asList(
            "https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/css/poncho.min.css"
        );
        
        List<String> jsDeps = Arrays.asList(
            "https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/js/national-holidays.js"
        );
        
        String instructions = """
            ## Calendario de Feriados Nacionales
            
            ### Formato de datos de feriados
            ```json
            {
              "es": [
                { "date": "01/01/2025", "label": "Año Nuevo", "type": "inamovible" },
                { "date": "24/03/2025", "label": "Día de la Memoria", "type": "inamovible" }
              ]
            }
            ```
            
            ### Tipos de feriados
            - `inamovible`: Feriado fijo
            - `trasladable`: Puede moverse a lunes o viernes
            - `no_laborable`: Día no laborable
            - `turistico`: Feriado puente turístico
            """;
        
        return new GeneratedCode(html, null, cssDeps, jsDeps, instructions);
    }
    
    /**
     * Genera código para mapa SVG de Argentina
     */
    public GeneratedCode generateArgentinaMapSvg(
            String containerId,
            List<String> provinces,
            String color,
            String strokeColor,
            double strokeWidth,
            String defaultColor,
            Map<String, String> selectiveColors) {
        
        Map<String, Object> context = new HashMap<>();
        context.put("containerId", containerId != null ? containerId : "js-mapa-svg");
        context.put("color", color != null ? color : "#039BE5");
        context.put("strokeColor", strokeColor != null ? strokeColor : "#999999");
        context.put("strokeWidth", strokeWidth > 0 ? strokeWidth : 1);
        context.put("defaultColor", defaultColor != null ? defaultColor : "#DDDDDD");
        
        if (selectiveColors != null && !selectiveColors.isEmpty()) {
            context.put("useSelectiveColors", true);
            List<Map<String, String>> colors = new ArrayList<>();
            selectiveColors.forEach((code, c) -> {
                Map<String, String> entry = new HashMap<>();
                entry.put("provinceCode", code);
                entry.put("color", c);
                colors.add(entry);
            });
            context.put("selectiveColors", colors);
        } else {
            context.put("useSelectiveColors", false);
            context.put("provinces", provinces != null ? provinces : Collections.singletonList("*"));
        }
        
        String html = renderTemplate("mapa-argentina-svg.html", context);
        
        List<String> jsDeps = Arrays.asList(
            "https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/js/poncho.min.js"
        );
        
        String instructions = """
            ## Mapa SVG de Argentina
            
            ### Códigos de provincias (ISO 3166-2:AR)
            - AR-B: Buenos Aires
            - AR-C: CABA
            - AR-K: Catamarca
            - AR-H: Chaco
            - AR-U: Chubut
            - AR-X: Córdoba
            - AR-W: Corrientes
            - AR-E: Entre Ríos
            - AR-P: Formosa
            - AR-Y: Jujuy
            - AR-L: La Pampa
            - AR-F: La Rioja
            - AR-M: Mendoza
            - AR-N: Misiones
            - AR-Q: Neuquén
            - AR-R: Río Negro
            - AR-A: Salta
            - AR-J: San Juan
            - AR-D: San Luis
            - AR-Z: Santa Cruz
            - AR-S: Santa Fe
            - AR-G: Santiago del Estero
            - AR-V: Tierra del Fuego
            - AR-T: Tucumán
            
            Usa ["*"] para seleccionar todas las provincias.
            """;
        
        return new GeneratedCode(html, null, Collections.emptyList(), jsDeps, instructions);
    }
    
    private String renderTemplate(String templateName, Map<String, Object> context) {
        String templateContent = templates.get(templateName);
        if (templateContent == null) {
            return "<!-- Template no encontrado: " + templateName + " -->";
        }
        
        try {
            Mustache mustache = mustacheFactory.compile(new StringReader(templateContent), templateName);
            StringWriter writer = new StringWriter();
            mustache.execute(writer, context).flush();
            return writer.toString();
        } catch (Exception e) {
            log.error("Error renderizando template {}: {}", templateName, e.getMessage());
            return "<!-- Error renderizando template: " + e.getMessage() + " -->";
        }
    }
}
