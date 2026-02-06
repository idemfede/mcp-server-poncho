# MCP Server Poncho

[![CI](https://github.com/idemfede/mcp-server-poncho/actions/workflows/ci.yml/badge.svg)](https://github.com/idemfede/mcp-server-poncho/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://openjdk.org/)

Servidor MCP (Model Context Protocol) para la librería **Poncho** del Gobierno Argentino - el sistema de diseño de [argentina.gob.ar](https://www.argentina.gob.ar).

## ¿Qué es Poncho?

[Poncho](https://github.com/argob/poncho) es el sistema de diseño del Gobierno Argentino que proporciona componentes, estilos y patrones para crear sitios web institucionales consistentes con la identidad visual de argentina.gob.ar.

## ¿Qué hace este MCP?

Este servidor MCP permite a los asistentes de IA (Claude, Cursor, etc.) acceder a:

- **Documentación completa** de todos los componentes Poncho
- **Búsqueda por keywords** de componentes y plantillas (nombre, descripción, palabras clave)
- **Generación de código** HTML/JS para tablas, mapas, calendarios y más
- **Plantillas listas para usar** de páginas completas (home, login, formularios, etc.)
- **Utilidades** como paleta de colores, códigos de provincias y feriados
- **Guías de integración** con Angular, React, Vue y Next.js
- **Resources** (URIs `poncho://`) y **Prompts** guiados para adaptar plantillas e insertar componentes

## Características

- 25+ tools organizados en categorías
- **Búsqueda por keywords** de componentes y plantillas (nombre, descripción, palabras clave)
- Plantillas HTML completas del sistema de diseño argentino
- Generación de código para PonchoTable, PonchoMap y más
- Soporte para frameworks modernos (Angular 17+, React 18+)
- Recursos MCP (Resources) y prompts guiados (adaptar plantilla, insertar componente)

## Requisitos

- Java 21+ instalado (OpenJDK u otra distribución compatible)

## Uso rápido (3 pasos)

**Paso 1: Descargar el JAR**

1. Abrí la página de releases de GitHub del proyecto: `https://github.com/idemfede/mcp-server-poncho/releases/latest`.
2. Descargá el archivo `mcp-server-poncho-*.jar` (el JAR más reciente).
3. Guardalo en una carpeta fácil de recordar, por ejemplo: `/home/tu-usuario/apps/poncho/`.

**Paso 2: Configurar el MCP o ejecutarlo a mano**

- **Opción A – Ejecutarlo a mano**

  En una terminal, ejecutá:

  ```bash
  java -jar /ruta/al/archivo/mcp-server-poncho-1.0.0.jar
  ```

  (Reemplazá `/ruta/al/archivo/` por la ruta donde guardaste el JAR).

- **Opción B – Configurarlo en tu IDE**

  - En **Cursor**, agregá el servidor en tu configuración MCP (ver sección **"Configuración en Cursor"** más abajo).
  - En **Claude Desktop**, agregalo en `claude_desktop_config.json` (ver sección **"Configuración en Claude Desktop"**).

El servidor se iniciará en `http://localhost:8081` por defecto.

**Paso 3: Probar en el chat**

Una vez que el servidor esté corriendo y configurado:

- Probá en el chat algo como:
  - *"Creame una página de login usando Poncho"*
  - *"Mostrame los colores institucionales de Poncho"*

## Configuración en Cursor

Agrega el siguiente bloque a tu configuración de MCP en Cursor (`~/.cursor/mcp.json` o la configuración del workspace):

```json
{
  "mcpServers": {
    "argendata-poncho": {
      "command": "java",
      "args": ["-jar", "/ruta/a/mcp-server-poncho-1.0.0.jar"]
    }
  }
}
```

El servidor también expone un endpoint HTTP SSE en `http://localhost:8081/sse`, pero la integración recomendada con Cursor es mediante stdio usando `command` y `args`.

## Configuración en Claude Desktop

Agrega a tu `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "argendata-poncho": {
      "command": "java",
      "args": ["-jar", "/ruta/a/mcp-server-poncho-1.0.0.jar"]
    }
  }
}
```

## Tools Disponibles

### Documentación

| Tool | Descripción |
|------|-------------|
| `buscar_componente` | Búsqueda por keywords de componentes (nombre, funcionalidad, descripción) |
| `obtener_documentacion` | Documentación completa de un componente específico |
| `listar_componentes` | Lista todos los componentes agrupados por categoría |
| `obtener_dependencias` | URLs de CSS/JS necesarios para un componente |

### Generadores de Código

| Tool | Descripción |
|------|-------------|
| `generar_tabla` | Código HTML/JS para PonchoTable con filtros, paginación, Google Sheets |
| `generar_mapa` | Código HTML/JS para PonchoMap con Leaflet (marcadores, clusters, temas) |
| `generar_html_base` | Estructura HTML base vacía con dependencias Poncho |
| `generar_calendario_feriados` | Calendario de feriados nacionales argentinos (i18n) |
| `generar_mapa_argentina_svg` | Mapa SVG de Argentina con provincias coloreables (ISO 3166-2:AR) |
| `generar_conexion_google_sheets` | Código JavaScript para conectar PonchoTable/PonchoMap con Google Sheets |

### Plantillas de Páginas

| Tool | Descripción |
|------|-------------|
| `recomendar_plantilla` | Recomienda la mejor plantilla según el caso de uso (búsqueda por keywords) |
| `listar_categorias_plantillas` | Lista categorías: paginas-argentina, login-registro, formularios, tablas, etc. |
| `listar_plantillas_categoria` | Plantillas disponibles en una categoría con descripción |
| `obtener_plantilla` | Código HTML completo de una plantilla por categoría e ID |
| `buscar_plantilla` | Busca plantillas por palabras clave (nombre, descripción, keywords) |

### Utilidades

| Tool | Descripción |
|------|-------------|
| `obtener_colores` | Paleta de colores institucionales Poncho (azul, rojo, verde, etc.) |
| `obtener_codigos_provincias` | Códigos ISO 3166-2:AR de provincias argentinas |
| `obtener_feriados` | Feriados nacionales argentinos por año |
| `validar_configuracion` | Valida configuración JSON de componentes Poncho |
| `listar_temas_mapa` | Temas visuales disponibles para PonchoMap (Leaflet) |

### Integración con Frameworks

| Tool | Descripción |
|------|-------------|
| `configurar_poncho_angular` | Guía de integración con Angular (17+), angular.json, standalone |
| `configurar_poncho_react` | Guía de integración con React (Vite, CRA, Next.js), hooks |
| `verificar_compatibilidad_framework` | Compatibilidad con Angular, React, Vue, Next.js, Nuxt |

---

## Prompts

Prompts guiados que devuelven instrucciones para el modelo (system + user message):

| Prompt | Descripción | Argumentos |
|--------|-------------|------------|
| `poncho-adaptar-plantilla` | Instrucciones para adaptar una plantilla sin romper estilos ni estructura | `categoria`, `id`, `objetivo` |
| `poncho-insertar-componente` | Instrucciones para insertar un componente en una página existente | `componente`, `contexto` |

---

## Resources (URIs)

Recursos de solo lectura vía protocolo MCP. Se accede con `read_resource` o equivalente del cliente.

### Categorías y plantillas

| URI | Tipo | Descripción |
|-----|------|-------------|
| `poncho://templates/categories` | application/json | Lista de categorías de plantillas (slug → nombre) |
| `poncho://templates/{categoria}` | application/json | Lista de plantillas de una categoría (id, name, description, templatePath). Categorías: `paginas-argentina`, `login-registro`, `formularios`, `tablas`, `paneles`, `destacados`, `headers-footers` |
| `poncho://templates/html/{id}` | text/html | Código HTML original de la plantilla (respetar estructura y clases) |

### Componentes y dependencias

| URI | Tipo | Descripción |
|-----|------|-------------|
| `poncho://components/{id}` | text/markdown | Documentación del componente (opciones, dependencias, ejemplos) |
| `poncho://deps/{id}` | application/json | Dependencias CSS y JS del componente |

## Ejemplo de Uso

Una vez configurado, puedes pedirle al asistente:

- *"Creame una página de login usando Poncho"*
- *"Generá una tabla con filtros para mostrar datos de un Google Sheet"*
- *"¿Cómo integro PonchoMap en mi proyecto Angular?"*
- *"Mostrame los colores institucionales del sistema Poncho"*
- *"Necesito un mapa de Argentina con las provincias del NOA coloreadas"*

## Desarrollo

### Ejecutar tests

```bash
mvn test
```

### Verificación completa (tests + cobertura + Checkstyle + SpotBugs + Dependency-Check)

```bash
mvn verify
```

Reportes: `target/site/jacoco/index.html` (cobertura), Checkstyle y SpotBugs en consola. Para omitir el scan de CVEs en local: `mvn verify -DskipDependencyCheck=true`.

### Ejecutar en modo desarrollo

```bash
mvn spring-boot:run
```

### Estructura del proyecto

```
src/main/java/com/argendata/mcp/poncho/
├── PonchoMcpApplication.java    # Punto de entrada
├── config/                      # Configuración Spring
├── model/                       # Records y DTOs
├── service/                     # Lógica de negocio
└── tools/                       # Tools MCP
    ├── PonchoDocsTools.java     # Documentación
    ├── PonchoGeneratorTools.java # Generación de código
    ├── PonchoTemplateTools.java  # Plantillas
    ├── PonchoUtilsTools.java     # Utilidades
    └── PonchoFrameworkTools.java # Integración frameworks
```

## Contribuir

Ver [CONTRIBUTING.md](CONTRIBUTING.md) para guías de contribución.

## Licencia

Este proyecto está licenciado bajo la Licencia Apache 2.0 - ver el archivo [LICENSE](LICENSE) para más detalles.

## Links

- [Poncho - Sistema de Diseño](https://github.com/argob/poncho)
- [argentina.gob.ar](https://www.argentina.gob.ar)
- [MCP Protocol](https://modelcontextprotocol.io/)
- [Spring AI MCP](https://docs.spring.io/spring-ai/reference/api/mcp.html)
