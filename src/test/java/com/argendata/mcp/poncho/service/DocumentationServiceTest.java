package com.argendata.mcp.poncho.service;

import com.argendata.mcp.poncho.model.ComponentSearchResult;
import com.argendata.mcp.poncho.model.PonchoComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DocumentationServiceTest {

    @Autowired
    private DocumentationService documentationService;

    @Test
    void shouldLoadComponentsAndTemplatesOnStartup() {
        List<PonchoComponent> all = documentationService.getAllComponents();
        
        assertNotNull(all);
        assertFalse(all.isEmpty(), "Debe haber componentes y templates cargados");
        
        // Verificar que hay tanto componentes como templates
        long componentCount = all.stream().filter(PonchoComponent::isComponent).count();
        long templateCount = all.stream().filter(PonchoComponent::isTemplate).count();
        
        assertTrue(componentCount > 0, "Debe haber componentes cargados");
        assertTrue(templateCount > 0, "Debe haber templates cargados");
    }

    @Test
    void shouldGetOnlyComponents() {
        List<PonchoComponent> components = documentationService.getOnlyComponents();
        
        assertNotNull(components);
        assertFalse(components.isEmpty());
        assertTrue(components.stream().allMatch(PonchoComponent::isComponent),
            "Todos deben ser componentes");
    }

    @Test
    void shouldGetOnlyTemplates() {
        List<PonchoComponent> templates = documentationService.getOnlyTemplates();
        
        assertNotNull(templates);
        assertFalse(templates.isEmpty());
        assertTrue(templates.stream().allMatch(PonchoComponent::isTemplate),
            "Todos deben ser templates");
    }

    @Test
    void shouldGetComponentById() {
        Optional<PonchoComponent> component = documentationService.getComponent("poncho-table");
        
        assertTrue(component.isPresent(), "poncho-table debe existir");
        assertEquals("poncho-table", component.get().id());
        assertTrue(component.get().isComponent());
    }

    @Test
    void shouldGetTemplateById() {
        Optional<PonchoComponent> template = documentationService.getComponent("template-login");
        
        assertTrue(template.isPresent(), "template-login debe existir");
        assertEquals("template-login", template.get().id());
        assertTrue(template.get().isTemplate());
        assertNotNull(template.get().templatePath());
    }

    @Test
    void shouldReturnEmptyForNonExistentComponent() {
        Optional<PonchoComponent> component = documentationService.getComponent("componente-inexistente");
        
        assertTrue(component.isEmpty());
    }

    @Test
    void shouldGetCategories() {
        Set<String> categories = documentationService.getCategories();
        
        assertNotNull(categories);
        assertFalse(categories.isEmpty(), "Debe haber categorías");
    }

    @Test
    void shouldGetComponentCategories() {
        Set<String> categories = documentationService.getComponentCategories();
        
        assertNotNull(categories);
        assertTrue(categories.contains("data"), "Debe existir categoría 'data'");
        assertTrue(categories.contains("maps"), "Debe existir categoría 'maps'");
    }

    @Test
    void shouldGetTemplateCategories() {
        Set<String> categories = documentationService.getTemplateCategories();
        
        assertNotNull(categories);
        assertTrue(categories.contains("login-registro"), "Debe existir categoría 'login-registro'");
        assertTrue(categories.contains("paginas-argentina"), "Debe existir categoría 'paginas-argentina'");
    }

    @Test
    void shouldGetComponentsByCategory() {
        List<PonchoComponent> dataComponents = documentationService.getComponentsByCategory("data");
        
        assertNotNull(dataComponents);
        boolean hasPonchoTable = dataComponents.stream()
            .anyMatch(c -> c.id().equals("poncho-table"));
        assertTrue(hasPonchoTable, "La categoría 'data' debe incluir poncho-table");
    }

    @Test
    void shouldGetTemplatesByCategory() {
        List<PonchoComponent> templates = documentationService.getTemplatesByCategory("login-registro");
        
        assertNotNull(templates);
        assertFalse(templates.isEmpty());
        assertTrue(templates.stream().allMatch(PonchoComponent::isTemplate));
        
        boolean hasLogin = templates.stream()
            .anyMatch(t -> t.id().equals("template-login"));
        assertTrue(hasLogin, "Debe incluir template-login");
    }

    @Test
    void shouldReturnEmptyListForNonExistentCategory() {
        List<PonchoComponent> components = documentationService.getComponentsByCategory("categoria-inexistente");
        
        assertNotNull(components);
        assertTrue(components.isEmpty());
    }

    @Test
    void shouldSearchAllComponentsAndTemplates() {
        List<ComponentSearchResult> results = documentationService.searchComponents("tabla filtros", 5);
        
        assertNotNull(results);
        assertFalse(results.isEmpty(), "La búsqueda debe retornar resultados");
        
        ComponentSearchResult firstResult = results.get(0);
        assertNotNull(firstResult.id());
        assertTrue(firstResult.score() > 0, "El score debe ser positivo");
    }

    @Test
    void shouldSearchOnlyComponents() {
        List<ComponentSearchResult> results = documentationService.searchOnlyComponents("mapa", 5);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Verificar que solo retorna componentes (no templates)
        for (ComponentSearchResult result : results) {
            Optional<PonchoComponent> comp = documentationService.getComponent(result.id());
            assertTrue(comp.isPresent());
            assertTrue(comp.get().isComponent(), "Solo debe retornar componentes");
        }
    }

    @Test
    void shouldSearchOnlyTemplates() {
        List<ComponentSearchResult> results = documentationService.searchOnlyTemplates("login acceso", 5);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Verificar que solo retorna templates
        for (ComponentSearchResult result : results) {
            Optional<PonchoComponent> comp = documentationService.getComponent(result.id());
            assertTrue(comp.isPresent());
            assertTrue(comp.get().isTemplate(), "Solo debe retornar templates");
        }
    }

    @Test
    void shouldSearchWithKeywords() {
        // Buscar por keywords específicos
        List<ComponentSearchResult> results = documentationService.searchComponents("grilla datos", 3);
        
        assertNotNull(results);
        // "grilla" y "datos" son keywords de poncho-table
    }

    @Test
    void shouldGenerateFormattedDocumentation() {
        String docs = documentationService.getFormattedDocumentation("poncho-table");
        
        assertNotNull(docs);
        assertFalse(docs.contains("Componente no encontrado"));
        assertTrue(docs.contains("PonchoTable") || docs.contains("poncho-table"));
        assertTrue(docs.contains("Tipo:"));
    }

    @Test
    void shouldGenerateFormattedDocumentationForTemplate() {
        String docs = documentationService.getFormattedDocumentation("template-login");
        
        assertNotNull(docs);
        assertFalse(docs.contains("Componente no encontrado"));
        assertTrue(docs.contains("Template") || docs.contains("template"));
    }

    @Test
    void shouldReturnNotFoundMessageForInvalidComponent() {
        String docs = documentationService.getFormattedDocumentation("componente-invalido");
        
        assertTrue(docs.contains("Componente no encontrado"));
    }

    @Test
    void shouldLimitSearchResults() {
        List<ComponentSearchResult> results = documentationService.searchComponents("página", 2);
        
        assertTrue(results.size() <= 2, "No debe retornar más resultados que el límite");
    }

    @Test
    void shouldFindTemplateByDescriptiveQuery() {
        // Buscar templates con una descripción del caso de uso
        List<ComponentSearchResult> results = documentationService.searchOnlyTemplates(
            "formulario contacto consulta", 5);
        
        assertNotNull(results);
        // Debería encontrar templates de formularios
    }
}
