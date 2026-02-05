# Contribuir a MCP Server Poncho

Gracias por tu interés en contribuir a este proyecto. Esta guía te ayudará a comenzar.

## Código de Conducta

Este proyecto sigue el [Código de Conducta de Contributor Covenant](https://www.contributor-covenant.org/). Al participar, se espera que respetes este código.

## Cómo Reportar Bugs

Si encontrás un bug, por favor abrí un issue en GitHub con:

1. **Título descriptivo**: Resume el problema en pocas palabras
2. **Descripción**: Explicá qué esperabas que pasara y qué pasó realmente
3. **Pasos para reproducir**: Lista los pasos exactos para reproducir el bug
4. **Entorno**: 
   - Versión de Java
   - Sistema operativo
   - Versión del MCP server
   - Cliente MCP usado (Cursor, Claude Desktop, etc.)
5. **Logs**: Si es posible, incluí logs relevantes

## Cómo Proponer Features

Para proponer una nueva funcionalidad:

1. Abrí un issue con el label `enhancement`
2. Describí el caso de uso
3. Explicá cómo beneficiaría a los usuarios
4. Si tenés ideas de implementación, incluílas

## Proceso de Pull Requests

### 1. Fork y Clone

```bash
git clone https://github.com/TU_USUARIO/poncho-mcp.git
cd poncho-mcp
```

### 2. Crear una rama

```bash
git checkout -b feature/mi-nueva-funcionalidad
# o
git checkout -b fix/descripcion-del-bug
```

### 3. Hacer cambios

- Seguí los estándares de código (ver abajo)
- Agregá tests para código nuevo
- Actualizá la documentación si es necesario

### 4. Ejecutar tests

```bash
mvn test
```

### 5. Commit

Usamos commits convencionales:

```bash
git commit -m "feat: agregar tool para validar HTML"
git commit -m "fix: corregir error en generación de mapa"
git commit -m "docs: actualizar README con nuevos tools"
```

Prefijos válidos:
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Cambios en documentación
- `style`: Formato, espacios, etc. (sin cambios de código)
- `refactor`: Refactorización de código
- `test`: Agregar o corregir tests
- `chore`: Tareas de mantenimiento

### 6. Push y Pull Request

```bash
git push origin feature/mi-nueva-funcionalidad
```

Luego abrí un Pull Request en GitHub.

## Estándares de Código

### Java 21

Este proyecto usa Java 21. Aprovechamos sus características modernas:

- **Records** para DTOs y modelos inmutables
- **Pattern matching** en switch expressions
- **Virtual threads** habilitados por defecto
- **Text blocks** para strings multilinea

### Estilo de código

```java
// Usar records para datos inmutables
public record ComponentSearchResult(
    String id,
    String name,
    String description,
    double score
) {}

// Usar pattern matching en switch
return switch (category) {
    case "data" -> "Datos y Tablas";
    case "maps" -> "Mapas";
    default -> category;
};

// Text blocks para HTML/JSON
String html = """
    <div class="container">
        <h1>Título</h1>
    </div>
    """;
```

### Comentarios

- Escribir comentarios en **español**
- Solo agregar comentarios cuando aporten valor
- Evitar comentarios obvios

```java
// Correcto: explica el "por qué"
// Usamos búsqueda semántica para encontrar componentes similares aunque no coincida el nombre exacto

// Incorrecto: describe lo obvio
// Incrementa el contador en 1
counter++;
```

### Tools MCP

Al crear un nuevo tool:

1. Usar nombres en **snake_case** y en español: `generar_tabla`, `obtener_colores`
2. Incluir descripción detallada con ejemplos en `@Tool`
3. Documentar cada parámetro con `@ToolParam`
4. Retornar Markdown formateado

```java
@Tool(description = "Genera código HTML para una tabla. " +
        "Ejemplo: generar_tabla('Mi Tabla', 'https://api.com/datos.json')")
public String generar_tabla(
    @ToolParam(description = "Título de la tabla") 
    String titulo,
    @ToolParam(description = "URL del JSON con datos") 
    String jsonUrl
) {
    // Implementación
}
```

## Estructura del Proyecto

```
src/main/java/com/argendata/mcp/poncho/
├── PonchoMcpApplication.java    # Punto de entrada Spring Boot
├── config/
│   ├── McpConfig.java           # Configuración MCP (tools)
│   ├── McpPrimitivesConfig.java # Resources y Prompts MCP
│   └── PonchoConstants.java     # Constantes (categorías, etc.)
├── model/
│   ├── PonchoComponent.java     # Modelo de componente
│   ├── ComponentSearchResult.java # Resultado de búsqueda
│   └── GeneratedCode.java       # Código generado
├── service/
│   ├── DocumentationService.java  # Documentación y plantillas
│   ├── KeywordSearchService.java   # Búsqueda por keywords
│   └── CodeGeneratorService.java   # Generación de código
└── tools/
    ├── PonchoDocsTools.java      # Tools de documentación
    ├── PonchoGeneratorTools.java # Tools de generación
    ├── PonchoTemplateTools.java  # Tools de plantillas
    ├── PonchoUtilsTools.java     # Tools de utilidades
    └── PonchoFrameworkTools.java # Tools de frameworks
```

## Recursos Útiles

- [Documentación de Poncho](https://github.com/argob/poncho)
- [MCP Protocol](https://modelcontextprotocol.io/)
- [Spring AI MCP](https://docs.spring.io/spring-ai/reference/api/mcp.html)
- [Java 21 Features](https://openjdk.org/projects/jdk/21/)

## Preguntas

Si tenés preguntas, podés:

1. Abrir un issue con el label `question`
2. Revisar issues existentes por si ya fue respondida

¡Gracias por contribuir!
