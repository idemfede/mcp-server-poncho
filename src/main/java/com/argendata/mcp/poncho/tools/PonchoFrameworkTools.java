package com.argendata.mcp.poncho.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Tools MCP para configuración de Poncho en frameworks frontend
 */
@Component
public class PonchoFrameworkTools {

    @Tool(description = "Genera instrucciones de configuración para integrar Poncho en un proyecto Angular. " +
            "Incluye configuración de angular.json, imports de estilos, manejo de jQuery y ejemplos de inicialización.")
    public String configurar_poncho_angular(
            @ToolParam(description = "Versión de Angular (ej: 17, 18, 19). Afecta las recomendaciones.")
            Integer version,
            @ToolParam(description = "Usar standalone components (true para Angular 17+)")
            Boolean standalone,
            @ToolParam(description = "Componentes Poncho a usar: tabla, mapa, calendario, todos")
            String componentes
    ) {
        int angularVersion = version != null ? version : 17;
        boolean useStandalone = standalone != null ? standalone : angularVersion >= 17;
        String comps = componentes != null ? componentes.toLowerCase() : "todos";
        
        StringBuilder sb = new StringBuilder();
        sb.append("# Configuración de Poncho para Angular ").append(angularVersion).append("\n\n");
        
        // Advertencias según versión
        if (angularVersion >= 17) {
            sb.append("> **Nota**: Angular ").append(angularVersion).append(" usa standalone components por defecto. ");
            sb.append("Las instrucciones están adaptadas a este enfoque.\n\n");
        }
        
        // 1. angular.json
        sb.append("## 1. Configurar `angular.json`\n\n");
        sb.append("Agregar en `projects > tu-proyecto > architect > build > options`:\n\n");
        sb.append("```json\n");
        sb.append("{\n");
        sb.append("  \"styles\": [\n");
        sb.append("    \"src/styles.scss\",\n");
        sb.append("    \"node_modules/bootstrap/dist/css/bootstrap.min.css\"\n");
        sb.append("  ],\n");
        sb.append("  \"scripts\": [\n");
        sb.append("    \"node_modules/jquery/dist/jquery.min.js\",\n");
        sb.append("    \"node_modules/bootstrap/dist/js/bootstrap.min.js\"\n");
        sb.append("  ]\n");
        sb.append("}\n");
        sb.append("```\n\n");
        
        // 2. Instalar dependencias
        sb.append("## 2. Instalar dependencias\n\n");
        sb.append("```bash\n");
        sb.append("npm install jquery bootstrap@3.4.1\n");
        sb.append("npm install --save-dev @types/jquery\n");
        sb.append("```\n\n");
        
        // 3. Estilos
        sb.append("## 3. Configurar estilos en `src/styles.scss`\n\n");
        sb.append("```scss\n");
        sb.append("// Fuente Encode Sans\n");
        sb.append("@import url('https://fonts.googleapis.com/css2?family=Encode+Sans:wght@100;200;300;400;500;600;700;800;900&display=swap');\n\n");
        sb.append("// Bootstrap 3 (si no está en angular.json)\n");
        sb.append("// @import '~bootstrap/dist/css/bootstrap.min.css';\n\n");
        sb.append("// Poncho CSS (desde CDN)\n");
        sb.append("@import url('https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/css/poncho.min.css');\n");
        sb.append("@import url('https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/css/icono-arg.css');\n");
        
        if (comps.contains("tabla") || comps.equals("todos")) {
            sb.append("\n// PonchoTable\n");
            sb.append("@import url('https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/css/ponchoTable-1.1.css');\n");
        }
        sb.append("```\n\n");
        
        // 4. Tipado jQuery
        sb.append("## 4. Configurar tipado de jQuery\n\n");
        sb.append("Crear o editar `src/typings.d.ts`:\n\n");
        sb.append("```typescript\n");
        sb.append("declare var $: JQueryStatic;\n");
        sb.append("declare var jQuery: JQueryStatic;\n");
        sb.append("declare var ponchoTableDependant: any;\n");
        sb.append("declare var PonchoMap: any;\n");
        sb.append("declare var GapiSheetData: any;\n");
        sb.append("```\n\n");
        
        // 5. Cargar scripts de Poncho
        sb.append("## 5. Cargar scripts de Poncho\n\n");
        sb.append("En `src/index.html`, antes de `</body>`:\n\n");
        sb.append("```html\n");
        if (comps.contains("tabla") || comps.equals("todos")) {
            sb.append("<script src=\"https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/js/datatables.min.js\"></script>\n");
        }
        if (comps.contains("mapa") || comps.equals("todos")) {
            sb.append("<script src=\"https://mapa-ign.argentina.gob.ar/js/leaflet/leaflet.js\"></script>\n");
        }
        sb.append("<script src=\"https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/js/poncho.min.js\"></script>\n");
        sb.append("```\n\n");
        
        // 6. Ejemplo de uso
        sb.append("## 6. Ejemplo de uso en componente\n\n");
        
        if (useStandalone) {
            sb.append("```typescript\n");
            sb.append("import { Component, AfterViewInit, OnDestroy, ElementRef, ViewChild } from '@angular/core';\n\n");
            sb.append("@Component({\n");
            sb.append("  selector: 'app-mi-tabla',\n");
            sb.append("  standalone: true,\n");
            sb.append("  template: `\n");
            sb.append("    <div #tableContainer>\n");
            sb.append("      <div id=\"ponchoTableFiltroCont\" style=\"display:none\">\n");
            sb.append("        <form><div class=\"form-group\">\n");
            sb.append("          <label for=\"ponchoTableFiltro\">Filtro</label>\n");
            sb.append("          <select class=\"form-control\" id=\"ponchoTableFiltro\"></select>\n");
            sb.append("        </div></form>\n");
            sb.append("      </div>\n");
            sb.append("      <table class=\"table table-striped\" id=\"ponchoTable\">\n");
            sb.append("        <caption></caption><thead></thead><tbody></tbody>\n");
            sb.append("      </table>\n");
            sb.append("    </div>\n");
            sb.append("  `\n");
            sb.append("})\n");
            sb.append("export class MiTablaComponent implements AfterViewInit, OnDestroy {\n");
            sb.append("  @ViewChild('tableContainer') container!: ElementRef;\n\n");
            sb.append("  ngAfterViewInit(): void {\n");
            sb.append("    // Inicializar después de que el DOM esté listo\n");
            sb.append("    const options = {\n");
            sb.append("      jsonUrl: 'https://mi-api.com/datos.json',\n");
            sb.append("      tituloTabla: 'Mi Tabla',\n");
            sb.append("      cantidadItems: 10\n");
            sb.append("    };\n");
            sb.append("    (window as any).ponchoTableDependant(options);\n");
            sb.append("  }\n\n");
            sb.append("  ngOnDestroy(): void {\n");
            sb.append("    // Limpiar si es necesario\n");
            sb.append("    $('#ponchoTable').DataTable().destroy();\n");
            sb.append("  }\n");
            sb.append("}\n");
            sb.append("```\n\n");
        } else {
            sb.append("```typescript\n");
            sb.append("import { Component, AfterViewInit, OnDestroy } from '@angular/core';\n\n");
            sb.append("@Component({\n");
            sb.append("  selector: 'app-mi-tabla',\n");
            sb.append("  templateUrl: './mi-tabla.component.html'\n");
            sb.append("})\n");
            sb.append("export class MiTablaComponent implements AfterViewInit, OnDestroy {\n");
            sb.append("  ngAfterViewInit(): void {\n");
            sb.append("    const options = { jsonUrl: '...', tituloTabla: 'Mi Tabla' };\n");
            sb.append("    (window as any).ponchoTableDependant(options);\n");
            sb.append("  }\n\n");
            sb.append("  ngOnDestroy(): void {\n");
            sb.append("    $('#ponchoTable').DataTable().destroy();\n");
            sb.append("  }\n");
            sb.append("}\n");
            sb.append("```\n\n");
        }
        
        // 7. SSR
        sb.append("## 7. Consideraciones para SSR (Angular Universal)\n\n");
        sb.append("Si usás Server-Side Rendering, Poncho requiere el DOM del navegador:\n\n");
        sb.append("```typescript\n");
        sb.append("import { isPlatformBrowser } from '@angular/common';\n");
        sb.append("import { PLATFORM_ID, inject } from '@angular/core';\n\n");
        sb.append("export class MiTablaComponent implements AfterViewInit {\n");
        sb.append("  private platformId = inject(PLATFORM_ID);\n\n");
        sb.append("  ngAfterViewInit(): void {\n");
        sb.append("    if (isPlatformBrowser(this.platformId)) {\n");
        sb.append("      // Solo inicializar en el navegador\n");
        sb.append("      (window as any).ponchoTableDependant(options);\n");
        sb.append("    }\n");
        sb.append("  }\n");
        sb.append("}\n");
        sb.append("```\n");
        
        return sb.toString();
    }

    @Tool(description = "Genera instrucciones de configuración para integrar Poncho en un proyecto React. " +
            "Incluye configuración según bundler (Vite, CRA, Next.js), manejo de jQuery y ejemplos con hooks.")
    public String configurar_poncho_react(
            @ToolParam(description = "Versión de React (ej: 18, 19)")
            Integer version,
            @ToolParam(description = "Bundler/framework: vite, cra, nextjs")
            String bundler,
            @ToolParam(description = "Usar TypeScript")
            Boolean typescript,
            @ToolParam(description = "Componentes Poncho a usar: tabla, mapa, calendario, todos")
            String componentes
    ) {
        int reactVersion = version != null ? version : 18;
        String build = bundler != null ? bundler.toLowerCase() : "vite";
        boolean useTs = typescript != null ? typescript : true;
        String comps = componentes != null ? componentes.toLowerCase() : "todos";
        String ext = useTs ? "tsx" : "jsx";
        
        StringBuilder sb = new StringBuilder();
        sb.append("# Configuración de Poncho para React ").append(reactVersion);
        sb.append(" (").append(build.toUpperCase()).append(")\n\n");
        
        // Advertencias según versión
        if (reactVersion >= 18) {
            sb.append("> **Advertencia**: React ").append(reactVersion);
            sb.append(" con Strict Mode ejecuta effects dos veces en desarrollo. ");
            sb.append("Esto puede causar doble inicialización de componentes Poncho. ");
            sb.append("Ver sección de cleanup.\n\n");
        }
        
        // 1. Instalar dependencias
        sb.append("## 1. Instalar dependencias\n\n");
        sb.append("```bash\n");
        sb.append("npm install jquery\n");
        if (useTs) {
            sb.append("npm install --save-dev @types/jquery\n");
        }
        sb.append("```\n\n");
        
        // 2. Configuración según bundler
        sb.append("## 2. Configuración de ").append(build.toUpperCase()).append("\n\n");
        
        if (build.equals("vite")) {
            sb.append("En `vite.config.ts`:\n\n");
            sb.append("```typescript\n");
            sb.append("import { defineConfig } from 'vite';\n");
            sb.append("import react from '@vitejs/plugin-react';\n\n");
            sb.append("export default defineConfig({\n");
            sb.append("  plugins: [react()],\n");
            sb.append("  // jQuery está disponible globalmente desde index.html\n");
            sb.append("});\n");
            sb.append("```\n\n");
        } else if (build.equals("nextjs")) {
            sb.append("En `next.config.js`:\n\n");
            sb.append("```javascript\n");
            sb.append("/** @type {import('next').NextConfig} */\n");
            sb.append("const nextConfig = {\n");
            sb.append("  // Poncho no es compatible con SSR, usar 'use client'\n");
            sb.append("  reactStrictMode: true,\n");
            sb.append("};\n");
            sb.append("module.exports = nextConfig;\n");
            sb.append("```\n\n");
        } else {
            sb.append("CRA no requiere configuración adicional del bundler.\n\n");
        }
        
        // 3. index.html
        sb.append("## 3. Configurar `index.html`\n\n");
        
        String htmlPath = build.equals("nextjs") ? "`app/layout.tsx` o `_document.tsx`" : "`index.html`";
        sb.append("En ").append(htmlPath).append(":\n\n");
        
        sb.append("```html\n");
        sb.append("<head>\n");
        sb.append("  <!-- Fuente -->\n");
        sb.append("  <link href=\"https://fonts.googleapis.com/css2?family=Encode+Sans:wght@100;200;300;400;500;600;700;800;900&display=swap\" rel=\"stylesheet\">\n");
        sb.append("  \n");
        sb.append("  <!-- Bootstrap 3 -->\n");
        sb.append("  <link href=\"https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css\" rel=\"stylesheet\">\n");
        sb.append("  \n");
        sb.append("  <!-- Poncho CSS -->\n");
        sb.append("  <link href=\"https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/css/poncho.min.css\" rel=\"stylesheet\">\n");
        if (comps.contains("tabla") || comps.equals("todos")) {
            sb.append("  <link href=\"https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/css/ponchoTable-1.1.css\" rel=\"stylesheet\">\n");
        }
        if (comps.contains("mapa") || comps.equals("todos")) {
            sb.append("  <link href=\"https://mapa-ign.argentina.gob.ar/js/leaflet/leaflet.css\" rel=\"stylesheet\">\n");
        }
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("  <div id=\"root\"></div>\n");
        sb.append("  \n");
        sb.append("  <!-- jQuery y Bootstrap JS -->\n");
        sb.append("  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js\"></script>\n");
        sb.append("  <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js\"></script>\n");
        if (comps.contains("tabla") || comps.equals("todos")) {
            sb.append("  <script src=\"https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/js/datatables.min.js\"></script>\n");
        }
        if (comps.contains("mapa") || comps.equals("todos")) {
            sb.append("  <script src=\"https://mapa-ign.argentina.gob.ar/js/leaflet/leaflet.js\"></script>\n");
        }
        sb.append("  <script src=\"https://www.argentina.gob.ar/profiles/argentinagobar/themes/contrib/poncho/js/poncho.min.js\"></script>\n");
        sb.append("</body>\n");
        sb.append("```\n\n");
        
        // 4. Tipado
        if (useTs) {
            sb.append("## 4. Configurar tipos globales\n\n");
            sb.append("Crear `src/types/poncho.d.ts`:\n\n");
            sb.append("```typescript\n");
            sb.append("interface Window {\n");
            sb.append("  $: JQueryStatic;\n");
            sb.append("  jQuery: JQueryStatic;\n");
            sb.append("  ponchoTableDependant: (options: PonchoTableOptions) => void;\n");
            sb.append("  PonchoMap: new (entries: any[], options: PonchoMapOptions) => PonchoMapInstance;\n");
            sb.append("  GapiSheetData: new () => GapiSheetDataInstance;\n");
            sb.append("}\n\n");
            sb.append("interface PonchoTableOptions {\n");
            sb.append("  jsonUrl: string;\n");
            sb.append("  tituloTabla?: string;\n");
            sb.append("  ordenColumna?: number;\n");
            sb.append("  ordenTipo?: 'asc' | 'desc';\n");
            sb.append("  cantidadItems?: number;\n");
            sb.append("  urlParams?: boolean;\n");
            sb.append("  pushState?: boolean;\n");
            sb.append("}\n\n");
            sb.append("interface PonchoMapOptions {\n");
            sb.append("  scope: string;\n");
            sb.append("  title?: string;\n");
            sb.append("  map_view?: [number, number];\n");
            sb.append("  map_zoom?: number;\n");
            sb.append("  tooltip?: boolean;\n");
            sb.append("  theme_tool?: boolean;\n");
            sb.append("}\n\n");
            sb.append("interface PonchoMapInstance {\n");
            sb.append("  render(): void;\n");
            sb.append("}\n\n");
            sb.append("interface GapiSheetDataInstance {\n");
            sb.append("  url(sheetName: string, spreadsheetId: string): string;\n");
            sb.append("}\n");
            sb.append("```\n\n");
        }
        
        // 5. Ejemplo de componente
        sb.append("## ").append(useTs ? "5" : "4").append(". Ejemplo de componente con hook\n\n");
        sb.append("```").append(ext).append("\n");
        sb.append("import { useEffect, useRef } from 'react';\n\n");
        
        if (build.equals("nextjs")) {
            sb.append("'use client'; // Necesario en Next.js App Router\n\n");
        }
        
        if (useTs) {
            sb.append("interface PonchoTableProps {\n");
            sb.append("  jsonUrl: string;\n");
            sb.append("  titulo?: string;\n");
            sb.append("  itemsPorPagina?: number;\n");
            sb.append("}\n\n");
            sb.append("export function PonchoTableComponent({ jsonUrl, titulo, itemsPorPagina = 10 }: PonchoTableProps) {\n");
        } else {
            sb.append("export function PonchoTableComponent({ jsonUrl, titulo, itemsPorPagina = 10 }) {\n");
        }
        sb.append("  const containerRef = useRef").append(useTs ? "<HTMLDivElement>" : "").append("(null);\n");
        sb.append("  const initializedRef = useRef(false);\n\n");
        sb.append("  useEffect(() => {\n");
        sb.append("    // Prevenir doble inicialización en Strict Mode\n");
        sb.append("    if (initializedRef.current) return;\n");
        sb.append("    initializedRef.current = true;\n\n");
        sb.append("    const options = {\n");
        sb.append("      jsonUrl,\n");
        sb.append("      tituloTabla: titulo || 'Datos',\n");
        sb.append("      cantidadItems: itemsPorPagina,\n");
        sb.append("    };\n\n");
        sb.append("    // Verificar que Poncho esté cargado\n");
        sb.append("    if (typeof window.ponchoTableDependant === 'function') {\n");
        sb.append("      window.ponchoTableDependant(options);\n");
        sb.append("    }\n\n");
        sb.append("    // Cleanup\n");
        sb.append("    return () => {\n");
        sb.append("      if (window.$ && window.$('#ponchoTable').DataTable) {\n");
        sb.append("        try {\n");
        sb.append("          window.$('#ponchoTable').DataTable().destroy();\n");
        sb.append("        } catch (e) {\n");
        sb.append("          // Ignorar si ya fue destruido\n");
        sb.append("        }\n");
        sb.append("      }\n");
        sb.append("      initializedRef.current = false;\n");
        sb.append("    };\n");
        sb.append("  }, [jsonUrl, titulo, itemsPorPagina]);\n\n");
        sb.append("  return (\n");
        sb.append("    <div ref={containerRef}>\n");
        sb.append("      <div id=\"ponchoTableFiltroCont\" style={{ display: 'none' }}>\n");
        sb.append("        <form>\n");
        sb.append("          <div className=\"form-group\">\n");
        sb.append("            <label htmlFor=\"ponchoTableFiltro\">Filtro</label>\n");
        sb.append("            <select className=\"form-control\" id=\"ponchoTableFiltro\" />\n");
        sb.append("          </div>\n");
        sb.append("        </form>\n");
        sb.append("      </div>\n");
        sb.append("      <table className=\"table table-striped\" id=\"ponchoTable\">\n");
        sb.append("        <caption />\n");
        sb.append("        <thead />\n");
        sb.append("        <tbody />\n");
        sb.append("      </table>\n");
        sb.append("    </div>\n");
        sb.append("  );\n");
        sb.append("}\n");
        sb.append("```\n\n");
        
        // 6. Notas importantes
        sb.append("## Notas importantes\n\n");
        sb.append("1. **Strict Mode**: El `initializedRef` previene la doble inicialización\n");
        sb.append("2. **Cleanup**: Es importante destruir DataTable al desmontar\n");
        sb.append("3. **IDs únicos**: Si usás múltiples tablas, los IDs deben ser únicos\n");
        sb.append("4. **SSR**: En Next.js, usar `'use client'` y verificar `typeof window`\n");
        
        return sb.toString();
    }

    @Tool(description = "Verifica la compatibilidad de Poncho con un framework frontend específico. " +
            "Retorna nivel de compatibilidad, advertencias y recomendaciones.")
    public String verificar_compatibilidad_framework(
            @ToolParam(description = "Framework: angular, react, vue, nextjs, nuxt, vanilla")
            String framework,
            @ToolParam(description = "Versión del framework (ej: 17, 18, 3)")
            Integer version
    ) {
        String fw = framework != null ? framework.toLowerCase() : "vanilla";
        int ver = version != null ? version : 0;
        
        StringBuilder sb = new StringBuilder();
        sb.append("# Compatibilidad de Poncho con ").append(capitalize(fw));
        if (ver > 0) {
            sb.append(" ").append(ver);
        }
        sb.append("\n\n");
        
        String nivel;
        String emoji;
        
        switch (fw) {
            case "vanilla" -> {
                nivel = "ALTA";
                emoji = "✅";
                sb.append("## Nivel de compatibilidad: ").append(emoji).append(" ").append(nivel).append("\n\n");
                sb.append("Poncho está diseñado para HTML/CSS/JS vanilla. Funciona perfectamente.\n\n");
                sb.append("### Componentes\n\n");
                sb.append("| Componente | Estado |\n");
                sb.append("|------------|--------|\n");
                sb.append("| PonchoTable | ✅ Totalmente compatible |\n");
                sb.append("| PonchoMap | ✅ Totalmente compatible |\n");
                sb.append("| NationalHolidays | ✅ Totalmente compatible |\n");
                sb.append("| Mapa Argentina SVG | ✅ Totalmente compatible |\n");
            }
            
            case "angular" -> {
                if (ver >= 17) {
                    nivel = "MEDIA-ALTA";
                    emoji = "🟡";
                } else {
                    nivel = "ALTA";
                    emoji = "✅";
                }
                sb.append("## Nivel de compatibilidad: ").append(emoji).append(" ").append(nivel).append("\n\n");
                
                sb.append("### Componentes\n\n");
                sb.append("| Componente | Estado | Notas |\n");
                sb.append("|------------|--------|-------|\n");
                sb.append("| PonchoTable | ✅ Compatible | Usar `ngAfterViewInit` |\n");
                sb.append("| PonchoMap | ✅ Compatible | Usar `ngAfterViewInit` |\n");
                sb.append("| NationalHolidays | ✅ Compatible | Requiere template en DOM |\n");
                sb.append("| Mapa Argentina SVG | ✅ Compatible | Simple de integrar |\n\n");
                
                sb.append("### Advertencias\n\n");
                if (ver >= 17) {
                    sb.append("- ⚠️ **Standalone components**: Funcionan correctamente pero requieren importar módulos manualmente\n");
                    sb.append("- ⚠️ **Signals**: No afectan la compatibilidad con Poncho\n");
                }
                if (ver >= 18) {
                    sb.append("- ⚠️ **Control flow (@if, @for)**: Funcionan bien, pero Poncho manipula el DOM directamente\n");
                }
                sb.append("- ⚠️ **Zone.js**: Poncho usa jQuery que opera fuera de la zona de Angular\n");
                sb.append("- ⚠️ **Angular Universal (SSR)**: Requiere `isPlatformBrowser` guard\n\n");
                
                sb.append("### Recomendaciones\n\n");
                sb.append("1. Inicializar componentes Poncho en `ngAfterViewInit`\n");
                sb.append("2. Destruir componentes en `ngOnDestroy`\n");
                sb.append("3. Usar `declare var` para tipado de funciones globales\n");
                sb.append("4. Evitar binding de Angular en elementos controlados por Poncho\n");
            }
            
            case "react" -> {
                if (ver >= 18) {
                    nivel = "MEDIA";
                    emoji = "🟡";
                } else {
                    nivel = "MEDIA-ALTA";
                    emoji = "🟡";
                }
                sb.append("## Nivel de compatibilidad: ").append(emoji).append(" ").append(nivel).append("\n\n");
                
                sb.append("### Componentes\n\n");
                sb.append("| Componente | Estado | Notas |\n");
                sb.append("|------------|--------|-------|\n");
                sb.append("| PonchoTable | ⚠️ Requiere cuidado | Strict Mode causa doble init |\n");
                sb.append("| PonchoMap | ⚠️ Requiere cuidado | Cleanup importante |\n");
                sb.append("| NationalHolidays | ✅ Compatible | Más simple de integrar |\n");
                sb.append("| Mapa Argentina SVG | ✅ Compatible | Solo manipula estilos |\n\n");
                
                sb.append("### Advertencias\n\n");
                if (ver >= 18) {
                    sb.append("- ⚠️ **Strict Mode**: Effects se ejecutan 2 veces en desarrollo. Usar ref para evitar doble inicialización\n");
                    sb.append("- ⚠️ **Concurrent Features**: Poncho no es compatible con Suspense para datos\n");
                }
                sb.append("- ⚠️ **Virtual DOM**: Poncho manipula el DOM real, puede causar conflictos\n");
                sb.append("- ⚠️ **jQuery**: Requiere cargar jQuery globalmente\n");
                sb.append("- ⚠️ **Estado**: No usar estado de React para datos manejados por Poncho\n\n");
                
                sb.append("### Recomendaciones\n\n");
                sb.append("1. Usar `useRef` para prevenir múltiples inicializaciones\n");
                sb.append("2. Implementar cleanup en el return de `useEffect`\n");
                sb.append("3. No mezclar estado de React con datos de Poncho\n");
                sb.append("4. Usar IDs únicos si hay múltiples instancias\n");
            }
            
            case "nextjs" -> {
                nivel = "MEDIA-BAJA";
                emoji = "🟠";
                sb.append("## Nivel de compatibilidad: ").append(emoji).append(" ").append(nivel).append("\n\n");
                
                sb.append("### Componentes\n\n");
                sb.append("| Componente | Estado | Notas |\n");
                sb.append("|------------|--------|-------|\n");
                sb.append("| PonchoTable | ⚠️ Solo client-side | Usar 'use client' |\n");
                sb.append("| PonchoMap | ⚠️ Solo client-side | Usar 'use client' |\n");
                sb.append("| NationalHolidays | ⚠️ Solo client-side | Usar 'use client' |\n");
                sb.append("| Mapa Argentina SVG | ✅ Compatible | Puede funcionar en server |\n\n");
                
                sb.append("### Advertencias\n\n");
                sb.append("- ⚠️ **SSR**: Poncho requiere `window` y `document`. NO funciona en server\n");
                sb.append("- ⚠️ **App Router**: Requiere `'use client'` en componentes que usan Poncho\n");
                sb.append("- ⚠️ **Hydration**: Puede haber mismatch entre server y client\n");
                sb.append("- ⚠️ **Scripts externos**: Cargar con `next/script` strategy 'afterInteractive'\n\n");
                
                sb.append("### Recomendaciones\n\n");
                sb.append("1. Siempre usar `'use client'` en componentes Poncho\n");
                sb.append("2. Verificar `typeof window !== 'undefined'` antes de usar\n");
                sb.append("3. Usar `next/script` para cargar scripts externos\n");
                sb.append("4. Considerar dynamic import con `ssr: false`\n\n");
                
                sb.append("### Ejemplo con dynamic import\n\n");
                sb.append("```typescript\n");
                sb.append("import dynamic from 'next/dynamic';\n\n");
                sb.append("const PonchoTable = dynamic(\n");
                sb.append("  () => import('./PonchoTableClient'),\n");
                sb.append("  { ssr: false }\n");
                sb.append(");\n");
                sb.append("```\n");
            }
            
            case "vue" -> {
                if (ver >= 3) {
                    nivel = "MEDIA-ALTA";
                    emoji = "🟡";
                } else {
                    nivel = "ALTA";
                    emoji = "✅";
                }
                sb.append("## Nivel de compatibilidad: ").append(emoji).append(" ").append(nivel).append("\n\n");
                
                sb.append("### Componentes\n\n");
                sb.append("| Componente | Estado | Notas |\n");
                sb.append("|------------|--------|-------|\n");
                sb.append("| PonchoTable | ✅ Compatible | Usar `onMounted` |\n");
                sb.append("| PonchoMap | ✅ Compatible | Usar `onMounted` |\n");
                sb.append("| NationalHolidays | ✅ Compatible | Simple integración |\n");
                sb.append("| Mapa Argentina SVG | ✅ Compatible | Sin problemas |\n\n");
                
                sb.append("### Advertencias\n\n");
                if (ver >= 3) {
                    sb.append("- ⚠️ **Composition API**: Usar `onMounted` y `onUnmounted`\n");
                    sb.append("- ⚠️ **Reactivity**: No usar refs de Vue para elementos controlados por Poncho\n");
                }
                sb.append("- ⚠️ **Template refs**: Usar para acceder al contenedor, no a elementos internos\n\n");
                
                sb.append("### Recomendaciones\n\n");
                sb.append("1. Inicializar en `onMounted` (Vue 3) o `mounted` (Vue 2)\n");
                sb.append("2. Limpiar en `onUnmounted` o `beforeUnmount`\n");
                sb.append("3. Vue es más compatible que React por su filosofía menos restrictiva con el DOM\n");
            }
            
            case "nuxt" -> {
                nivel = "MEDIA-BAJA";
                emoji = "🟠";
                sb.append("## Nivel de compatibilidad: ").append(emoji).append(" ").append(nivel).append("\n\n");
                
                sb.append("Similar a Next.js, Nuxt tiene SSR por defecto.\n\n");
                
                sb.append("### Recomendaciones\n\n");
                sb.append("1. Usar `<ClientOnly>` wrapper\n");
                sb.append("2. O crear plugin con `ssr: false`\n");
                sb.append("3. Verificar `process.client` antes de usar Poncho\n\n");
                
                sb.append("```vue\n");
                sb.append("<template>\n");
                sb.append("  <ClientOnly>\n");
                sb.append("    <PonchoTable :json-url=\"url\" />\n");
                sb.append("  </ClientOnly>\n");
                sb.append("</template>\n");
                sb.append("```\n");
            }
            
            default -> {
                nivel = "DESCONOCIDO";
                emoji = "❓";
                sb.append("## Nivel de compatibilidad: ").append(emoji).append(" ").append(nivel).append("\n\n");
                sb.append("Framework no reconocido. Frameworks soportados:\n");
                sb.append("- `vanilla` - HTML/CSS/JS puro\n");
                sb.append("- `angular` - Angular 14+\n");
                sb.append("- `react` - React 17+\n");
                sb.append("- `nextjs` - Next.js 13+\n");
                sb.append("- `vue` - Vue 2/3\n");
                sb.append("- `nuxt` - Nuxt 3\n");
            }
        }
        
        sb.append("\n---\n\n");
        sb.append("Usa `configurar_poncho_angular` o `configurar_poncho_react` para obtener instrucciones detalladas de configuración.");
        
        return sb.toString();
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
