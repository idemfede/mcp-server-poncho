package com.argendata.mcp.poncho.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Constantes compartidas del sistema Poncho.
 * Fuente de verdad única para categorías de templates y otros datos estáticos.
 */
public final class PonchoConstants {

    private PonchoConstants() {}

    /**
     * Categorías de templates Poncho.
     * Key: slug de la categoría, Value: nombre legible.
     */
    public static final Map<String, String> TEMPLATE_CATEGORIES;
    /**
     * Descripciones de las categorías de templates.
     * Key: slug de la categoría, Value: descripción.
     */
    public static final Map<String, String> TEMPLATE_CATEGORY_DESCRIPTIONS;

    static {
        Map<String, String> categories = new LinkedHashMap<>();
        categories.put("paginas-argentina", "Páginas de Argentina");
        categories.put("login-registro", "Login y Registro");
        categories.put("formularios", "Formularios");
        categories.put("tablas", "Tablas");
        categories.put("paneles", "Paneles");
        categories.put("destacados", "Destacados sin enlace");
        categories.put("headers-footers", "Headers y Footers");
        TEMPLATE_CATEGORIES = Collections.unmodifiableMap(categories);

        Map<String, String> descriptions = new LinkedHashMap<>();
        descriptions.put("paginas-argentina",
                "Plantillas base para sitios de argentina.gob.ar: home, páginas de área, noticias, servicios");
        descriptions.put("login-registro",
                "Flujo completo de autenticación: login, registro, recuperación de contraseña y correos transaccionales");
        descriptions.put("formularios",
                "Formularios con validación, campos de datos personales, consultas y páginas de respuesta");
        descriptions.put("tablas",
                "Variantes de tablas: simple, responsive, con filtros DataTables, scroll horizontal");
        descriptions.put("paneles",
                "Paneles de navegación: simples, con íconos, destacados con imágenes o colores");
        descriptions.put("destacados",
                "Elementos destacados informativos: íconos, números estadísticos, imágenes");
        descriptions.put("headers-footers",
                "Cabeceras y pies de página para sitios externos a argentina.gob.ar");
        TEMPLATE_CATEGORY_DESCRIPTIONS = Collections.unmodifiableMap(descriptions);
    }
    
    /**
     * Obtiene el nombre legible de una categoría.
     */
    public static String getCategoryName(String categorySlug) {
        return TEMPLATE_CATEGORIES.getOrDefault(categorySlug, categorySlug);
    }
    
    /**
     * Obtiene la descripción de una categoría.
     */
    public static String getCategoryDescription(String categorySlug) {
        return TEMPLATE_CATEGORY_DESCRIPTIONS.getOrDefault(categorySlug, "");
    }
    
    /**
     * Verifica si una categoría existe.
     */
    public static boolean isValidCategory(String categorySlug) {
        return TEMPLATE_CATEGORIES.containsKey(categorySlug);
    }
}
