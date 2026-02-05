# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [Unreleased]

### Added
- (Próximas mejoras: ver issues)

## [1.0.0] - 2024-12-01

### Added

#### Tools de Documentación
- `buscar_componente`: Búsqueda por keywords de componentes Poncho (nombre, descripción, palabras clave)
- `obtener_documentacion`: Documentación completa de un componente específico
- `listar_componentes`: Lista todos los componentes agrupados por categoría
- `obtener_dependencias`: URLs de CSS/JS necesarios para cada componente

#### Tools de Generación de Código
- `generar_tabla`: Genera código HTML/JS para PonchoTable con filtros y paginación
- `generar_mapa`: Genera código HTML/JS para PonchoMap con Leaflet
- `generar_html_base`: Estructura HTML base con todas las dependencias Poncho
- `generar_calendario_feriados`: Calendario de feriados nacionales argentinos
- `generar_mapa_argentina_svg`: Mapa SVG de Argentina con provincias coloreables
- `generar_conexion_google_sheets`: Código para conectar componentes con Google Sheets

#### Tools de Plantillas
- `recomendar_plantilla`: Recomienda la mejor plantilla según el caso de uso
- `listar_categorias_plantillas`: Lista todas las categorías de plantillas disponibles
- `listar_plantillas_categoria`: Plantillas disponibles en una categoría específica
- `obtener_plantilla`: Código HTML completo de una plantilla
- `buscar_plantilla`: Busca plantillas por palabra clave

#### Categorías de Plantillas
- **paginas-argentina**: home, noticia, servicio, página de área
- **login-registro**: login, registro, recuperar contraseña, correos transaccionales
- **formularios**: formulario completo, datos personales, consultas, respuestas
- **tablas**: simple, responsive, con filtros DataTables, scroll horizontal
- **paneles**: simples, con íconos, destacados con imagen
- **destacados**: íconos, números estadísticos, imágenes
- **headers-footers**: para sitios externos a argentina.gob.ar

#### Tools de Utilidades
- `obtener_colores`: Paleta de colores institucionales (azul, rojo, verde, amarillo, naranja, morado)
- `obtener_codigos_provincias`: Códigos ISO 3166-2:AR de las 24 provincias
- `obtener_feriados`: Feriados nacionales argentinos por año
- `validar_configuracion`: Valida configuración de componentes Poncho
- `listar_temas_mapa`: Temas visuales disponibles para PonchoMap

#### Tools de Integración con Frameworks
- `configurar_poncho_angular`: Guía completa de integración con Angular 17+
- `configurar_poncho_react`: Guía completa de integración con React 18+ (Vite, CRA, Next.js)
- `verificar_compatibilidad_framework`: Verifica compatibilidad con Angular, React, Vue, Next.js, Nuxt

#### Infraestructura
- Servidor MCP basado en Spring AI con Spring Boot 3.4
- Búsqueda por keywords de componentes y plantillas (sin APIs externas)
- Plantillas Mustache para generación de código
- Soporte para virtual threads de Java 21
- CI/CD con GitHub Actions

### Technical Details
- Java 21 con records, pattern matching y text blocks
- Spring AI 1.1.2 con MCP Server WebFlux
- Búsqueda local sobre documentación (components.json) y metadatos de plantillas
- Sin dependencias de APIs externas para búsqueda

---

[Unreleased]: https://github.com/idemfede/mcp-server-poncho/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/idemfede/mcp-server-poncho/releases/tag/v1.0.0
