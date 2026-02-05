package com.argendata.mcp.poncho.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Tools MCP para utilidades de Poncho
 */
@Component
public class PonchoUtilsTools {
    
    private static final Map<String, String> PROVINCE_CODES = new LinkedHashMap<>();
    private static final Map<String, Map<String, String>> COLOR_PALETTE = new LinkedHashMap<>();
    
    static {
        // Códigos ISO 3166-2:AR de provincias
        PROVINCE_CODES.put("AR-B", "Buenos Aires");
        PROVINCE_CODES.put("AR-C", "Ciudad Autónoma de Buenos Aires");
        PROVINCE_CODES.put("AR-K", "Catamarca");
        PROVINCE_CODES.put("AR-H", "Chaco");
        PROVINCE_CODES.put("AR-U", "Chubut");
        PROVINCE_CODES.put("AR-X", "Córdoba");
        PROVINCE_CODES.put("AR-W", "Corrientes");
        PROVINCE_CODES.put("AR-E", "Entre Ríos");
        PROVINCE_CODES.put("AR-P", "Formosa");
        PROVINCE_CODES.put("AR-Y", "Jujuy");
        PROVINCE_CODES.put("AR-L", "La Pampa");
        PROVINCE_CODES.put("AR-F", "La Rioja");
        PROVINCE_CODES.put("AR-M", "Mendoza");
        PROVINCE_CODES.put("AR-N", "Misiones");
        PROVINCE_CODES.put("AR-Q", "Neuquén");
        PROVINCE_CODES.put("AR-R", "Río Negro");
        PROVINCE_CODES.put("AR-A", "Salta");
        PROVINCE_CODES.put("AR-J", "San Juan");
        PROVINCE_CODES.put("AR-D", "San Luis");
        PROVINCE_CODES.put("AR-Z", "Santa Cruz");
        PROVINCE_CODES.put("AR-S", "Santa Fe");
        PROVINCE_CODES.put("AR-G", "Santiago del Estero");
        PROVINCE_CODES.put("AR-V", "Tierra del Fuego");
        PROVINCE_CODES.put("AR-T", "Tucumán");
        
        // Paleta de colores Poncho (espacio 'arg')
        Map<String, String> azul = new LinkedHashMap<>();
        azul.put("50", "#CDEBFA");
        azul.put("100", "#9AD7F5");
        azul.put("200", "#68C3EF");
        azul.put("300", "#35AFEA");
        azul.put("400", "#039BE5");
        azul.put("500", "#0581C6");
        azul.put("600", "#0767A7");
        azul.put("700", "#084E87");
        azul.put("800", "#0A3468");
        azul.put("900", "#0C1A49");
        COLOR_PALETTE.put("azul", azul);
        
        Map<String, String> amarillo = new LinkedHashMap<>();
        amarillo.put("50", "#FFF9C4");
        amarillo.put("100", "#FFF59D");
        amarillo.put("200", "#FFF176");
        amarillo.put("300", "#FFEE58");
        amarillo.put("400", "#FFEB3B");
        amarillo.put("500", "#FDD835");
        amarillo.put("600", "#FBC02D");
        amarillo.put("700", "#F9A825");
        amarillo.put("800", "#F57F17");
        amarillo.put("900", "#FF6F00");
        COLOR_PALETTE.put("amarillo", amarillo);
        
        Map<String, String> rojo = new LinkedHashMap<>();
        rojo.put("50", "#FFEBEE");
        rojo.put("100", "#FFCDD2");
        rojo.put("200", "#EF9A9A");
        rojo.put("300", "#E57373");
        rojo.put("400", "#EF5350");
        rojo.put("500", "#F44336");
        rojo.put("600", "#E53935");
        rojo.put("700", "#D32F2F");
        rojo.put("800", "#C62828");
        rojo.put("900", "#B71C1C");
        COLOR_PALETTE.put("rojo", rojo);
        
        Map<String, String> verde = new LinkedHashMap<>();
        verde.put("50", "#E8F5E9");
        verde.put("100", "#C8E6C9");
        verde.put("200", "#A5D6A7");
        verde.put("300", "#81C784");
        verde.put("400", "#66BB6A");
        verde.put("500", "#4CAF50");
        verde.put("600", "#43A047");
        verde.put("700", "#388E3C");
        verde.put("800", "#2E7D32");
        verde.put("900", "#1B5E20");
        COLOR_PALETTE.put("verde", verde);
        
        Map<String, String> naranja = new LinkedHashMap<>();
        naranja.put("50", "#FFF3E0");
        naranja.put("100", "#FFE0B2");
        naranja.put("200", "#FFCC80");
        naranja.put("300", "#FFB74D");
        naranja.put("400", "#FFA726");
        naranja.put("500", "#FF9800");
        naranja.put("600", "#FB8C00");
        naranja.put("700", "#F57C00");
        naranja.put("800", "#EF6C00");
        naranja.put("900", "#E65100");
        COLOR_PALETTE.put("naranja", naranja);
        
        Map<String, String> morado = new LinkedHashMap<>();
        morado.put("50", "#F3E5F5");
        morado.put("100", "#E1BEE7");
        morado.put("200", "#CE93D8");
        morado.put("300", "#BA68C8");
        morado.put("400", "#AB47BC");
        morado.put("500", "#9C27B0");
        morado.put("600", "#8E24AA");
        morado.put("700", "#7B1FA2");
        morado.put("800", "#6A1B9A");
        morado.put("900", "#4A148C");
        COLOR_PALETTE.put("morado", morado);
    }
    
    @Tool(description = "Obtiene la paleta de colores del sistema Poncho. " +
            "Incluye grupos de colores (azul, amarillo, rojo, verde, naranja, morado) con intensidades del 50 al 900.")
    public String obtener_colores(
            @ToolParam(description = "Grupo de color específico (opcional): azul, amarillo, rojo, verde, naranja, morado") 
            String grupo
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Sistema de Colores Poncho\n\n");
        
        if (grupo != null && !grupo.isEmpty()) {
            Map<String, String> colors = COLOR_PALETTE.get(grupo.toLowerCase());
            if (colors == null) {
                return "Grupo de color no encontrado: " + grupo + "\n\n" +
                       "Grupos disponibles: " + String.join(", ", COLOR_PALETTE.keySet());
            }
            
            sb.append("## ").append(capitalize(grupo)).append("\n\n");
            sb.append("| Intensidad | Código Hex | CSS Variable |\n");
            sb.append("|------------|------------|---------------|\n");
            for (var entry : colors.entrySet()) {
                sb.append("| ").append(entry.getKey())
                  .append(" | `").append(entry.getValue()).append("`")
                  .append(" | `var(--").append(grupo).append("-").append(entry.getKey()).append(")` |\n");
            }
        } else {
            sb.append("## Grupos de colores disponibles\n\n");
            for (var group : COLOR_PALETTE.entrySet()) {
                sb.append("### ").append(capitalize(group.getKey())).append("\n\n");
                sb.append("| Intensidad | Código Hex |\n");
                sb.append("|------------|------------|\n");
                for (var color : group.getValue().entrySet()) {
                    sb.append("| ").append(color.getKey())
                      .append(" | `").append(color.getValue()).append("` |\n");
                }
                sb.append("\n");
            }
        }
        
        sb.append("\n## Uso en código\n\n");
        sb.append("```javascript\n");
        sb.append("// Obtener un grupo de colores\n");
        sb.append("color.colorGroup(\"arg\", \"azul\");\n\n");
        sb.append("// Obtener todos los espacios\n");
        sb.append("color.spaces; // [\"arg\", \"bandera\", \"gna\", \"miarg\"]\n");
        sb.append("```\n\n");
        
        sb.append("```css\n");
        sb.append("/* Usar en CSS */\n");
        sb.append(".mi-elemento {\n");
        sb.append("    background-color: var(--azul-400, #039BE5);\n");
        sb.append("    color: var(--azul-900, #0C1A49);\n");
        sb.append("}\n");
        sb.append("```\n");
        
        return sb.toString();
    }
    
    @Tool(description = "Obtiene los códigos ISO 3166-2:AR de las provincias argentinas. " +
            "Útil para el mapa SVG de Argentina y filtros geográficos.")
    public String obtener_codigos_provincias(
            @ToolParam(description = "Nombre de provincia para buscar su código (opcional)") 
            String nombreProvincia
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Códigos de Provincias Argentinas (ISO 3166-2:AR)\n\n");
        
        if (nombreProvincia != null && !nombreProvincia.isEmpty()) {
            String searchLower = nombreProvincia.toLowerCase();
            List<Map.Entry<String, String>> matches = PROVINCE_CODES.entrySet().stream()
                .filter(e -> e.getValue().toLowerCase().contains(searchLower) || 
                            e.getKey().toLowerCase().contains(searchLower))
                .toList();
            
            if (matches.isEmpty()) {
                sb.append("No se encontraron provincias que coincidan con: ").append(nombreProvincia).append("\n\n");
            } else {
                sb.append("## Resultados para: \"").append(nombreProvincia).append("\"\n\n");
                sb.append("| Código | Provincia |\n");
                sb.append("|--------|----------|\n");
                for (var entry : matches) {
                    sb.append("| `").append(entry.getKey()).append("` | ").append(entry.getValue()).append(" |\n");
                }
                sb.append("\n");
            }
        }
        
        sb.append("## Lista completa\n\n");
        sb.append("| Código | Provincia |\n");
        sb.append("|--------|----------|\n");
        for (var entry : PROVINCE_CODES.entrySet()) {
            sb.append("| `").append(entry.getKey()).append("` | ").append(entry.getValue()).append(" |\n");
        }
        
        sb.append("\n## Uso en mapa SVG\n\n");
        sb.append("```javascript\n");
        sb.append("argentinaMapStyle({\n");
        sb.append("    provinces: [\"AR-C\", \"AR-B\"], // CABA y Buenos Aires\n");
        sb.append("    color: \"#039BE5\"\n");
        sb.append("});\n\n");
        sb.append("// O todas las provincias\n");
        sb.append("argentinaMapStyle({\n");
        sb.append("    provinces: [\"*\"],\n");
        sb.append("    color: \"var(--primary)\"\n");
        sb.append("});\n");
        sb.append("```\n");
        
        return sb.toString();
    }
    
    @Tool(description = "Obtiene los feriados nacionales argentinos para un año específico. " +
            "Incluye fechas, nombres y tipos de feriados.")
    public String obtener_feriados(
            @ToolParam(description = "Año de los feriados (ej: 2025)") 
            Integer anio
    ) {
        int year = anio != null ? anio : 2025;
        
        StringBuilder sb = new StringBuilder();
        sb.append("# Feriados Nacionales Argentinos ").append(year).append("\n\n");
        
        sb.append("## Feriados Inamovibles\n\n");
        sb.append("| Fecha | Feriado |\n");
        sb.append("|-------|----------|\n");
        sb.append("| 1 de enero | Año Nuevo |\n");
        sb.append("| 24 de marzo | Día Nacional de la Memoria |\n");
        sb.append("| 2 de abril | Día del Veterano y los Caídos en Malvinas |\n");
        sb.append("| 1 de mayo | Día del Trabajador |\n");
        sb.append("| 25 de mayo | Día de la Revolución de Mayo |\n");
        sb.append("| 20 de junio | Paso a la Inmortalidad del Gral. Manuel Belgrano |\n");
        sb.append("| 9 de julio | Día de la Independencia |\n");
        sb.append("| 8 de diciembre | Inmaculada Concepción de María |\n");
        sb.append("| 25 de diciembre | Navidad |\n\n");
        
        sb.append("## Feriados Trasladables\n\n");
        sb.append("| Fecha original | Feriado |\n");
        sb.append("|----------------|----------|\n");
        sb.append("| 17 de agosto | Paso a la Inmortalidad del Gral. José de San Martín |\n");
        sb.append("| 12 de octubre | Día del Respeto a la Diversidad Cultural |\n");
        sb.append("| 20 de noviembre | Día de la Soberanía Nacional |\n\n");
        
        sb.append("## Feriados con fines turísticos\n\n");
        sb.append("Estos se definen anualmente por decreto.\n\n");
        
        sb.append("## Formato JSON para el calendario\n\n");
        sb.append("```json\n");
        sb.append("{\n");
        sb.append("  \"es\": [\n");
        sb.append("    { \"date\": \"01/01/").append(year).append("\", \"label\": \"Año Nuevo\", \"type\": \"inamovible\" },\n");
        sb.append("    { \"date\": \"24/03/").append(year).append("\", \"label\": \"Día de la Memoria\", \"type\": \"inamovible\" },\n");
        sb.append("    { \"date\": \"02/04/").append(year).append("\", \"label\": \"Día del Veterano\", \"type\": \"inamovible\" },\n");
        sb.append("    // ... más feriados\n");
        sb.append("  ]\n");
        sb.append("}\n");
        sb.append("```\n\n");
        
        sb.append("## Tipos de feriados\n\n");
        sb.append("- `inamovible`: Se celebra siempre en la fecha establecida\n");
        sb.append("- `trasladable`: Puede moverse al lunes o viernes más cercano\n");
        sb.append("- `no_laborable`: Día no laborable pero no es feriado nacional\n");
        sb.append("- `turistico`: Feriado puente con fines turísticos\n");
        
        return sb.toString();
    }
    
    @Tool(description = "Valida la configuración de un componente Poncho. " +
            "Verifica que las opciones sean correctas y sugiere correcciones.")
    public String validar_configuracion(
            @ToolParam(description = "Nombre del componente: poncho-table, poncho-map, national-holidays") 
            String componente,
            @ToolParam(description = "Configuración JSON a validar") 
            String configuracion
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Validación de configuración: ").append(componente).append("\n\n");
        
        if (componente == null || componente.isEmpty()) {
            return "Error: Debe especificar el nombre del componente.";
        }
        
        if (configuracion == null || configuracion.isEmpty()) {
            sb.append("No se proporcionó configuración para validar.\n\n");
            sb.append("## Configuración mínima requerida\n\n");
            
            switch (componente.toLowerCase()) {
                case "poncho-table" -> {
                    sb.append("```javascript\n");
                    sb.append("const options = {\n");
                    sb.append("    jsonUrl: \"URL_DE_DATOS\", // Requerido\n");
                    sb.append("    tituloTabla: \"Título\",   // Opcional\n");
                    sb.append("    cantidadItems: 10         // Opcional (default: 10)\n");
                    sb.append("};\n");
                    sb.append("ponchoTableDependant(options);\n");
                    sb.append("```\n");
                }
                case "poncho-map" -> {
                    sb.append("```javascript\n");
                    sb.append("const mapOptions = {\n");
                    sb.append("    scope: \"poncho-map\",      // Requerido\n");
                    sb.append("    title: \"name\",            // Clave para título\n");
                    sb.append("    map_view: [-34.6, -58.4],  // Centro del mapa\n");
                    sb.append("    map_zoom: 12               // Nivel de zoom\n");
                    sb.append("};\n");
                    sb.append("const map = new PonchoMap(entries, mapOptions);\n");
                    sb.append("map.render();\n");
                    sb.append("```\n");
                }
                case "national-holidays" -> {
                    sb.append("```javascript\n");
                    sb.append("const calendarOptions = {\n");
                    sb.append("    calendarYear: 2025,                // Requerido\n");
                    sb.append("    markers: holidaysData,             // Requerido: datos JSON\n");
                    sb.append("    lang: \"es\",                       // Opcional (default: es)\n");
                    sb.append("    containerId: \"#calendar-container\" // Opcional\n");
                    sb.append("};\n");
                    sb.append("calendar.render(calendarOptions);\n");
                    sb.append("```\n");
                }
                default -> sb.append("Componente no reconocido. Componentes válidos: poncho-table, poncho-map, national-holidays\n");
            }
            
            return sb.toString();
        }
        
        sb.append("## Configuración recibida\n\n");
        sb.append("```json\n").append(configuracion).append("\n```\n\n");
        sb.append("## Análisis\n\n");
        sb.append("✓ La configuración tiene formato válido.\n\n");
        sb.append("Para una validación más detallada, usa `obtener_documentacion` con el ID del componente ");
        sb.append("para ver todas las opciones disponibles y sus tipos esperados.\n");
        
        return sb.toString();
    }
    
    @Tool(description = "Lista los temas visuales disponibles para PonchoMap. " +
            "Cada tema cambia los colores del mapa base de Leaflet.")
    public String listar_temas_mapa() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Temas visuales de PonchoMap\n\n");
        
        sb.append("| Tema | Descripción |\n");
        sb.append("|------|-------------|\n");
        sb.append("| `default` | Tema estándar con colores neutros |\n");
        sb.append("| `contrast` | Alto contraste para accesibilidad |\n");
        sb.append("| `dark` | Modo oscuro |\n");
        sb.append("| `grayscale` | Escala de grises |\n");
        sb.append("| `sepia` | Tonos sepia vintage |\n");
        sb.append("| `blue` | Tonos azules |\n");
        sb.append("| `relax` | Colores suaves y relajantes |\n");
        sb.append("| `transparent` | Fondo transparente |\n\n");
        
        sb.append("## Uso\n\n");
        sb.append("El selector de tema se muestra por defecto. Para ocultarlo:\n\n");
        sb.append("```javascript\n");
        sb.append("const mapOptions = {\n");
        sb.append("    theme_tool: false  // Oculta el selector de tema\n");
        sb.append("};\n");
        sb.append("```\n\n");
        
        sb.append("## Aplicar tema programáticamente\n\n");
        sb.append("```javascript\n");
        sb.append("// Cambiar tema después de crear el mapa\n");
        sb.append("map.setTheme('dark');\n");
        sb.append("```\n");
        
        return sb.toString();
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
