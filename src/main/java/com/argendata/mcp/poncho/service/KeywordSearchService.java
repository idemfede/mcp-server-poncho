package com.argendata.mcp.poncho.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Servicio de búsqueda por keywords sin dependencias de ML/ONNX.
 * Implementa búsqueda por coincidencia de términos con scoring ponderado.
 */
@Service
public class KeywordSearchService {

    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    
    // Pesos para el algoritmo de scoring
    private static final double WEIGHT_EXACT_KEYWORD = 3.0;
    private static final double WEIGHT_PARTIAL_KEYWORD = 2.0;
    private static final double WEIGHT_NAME_MATCH = 2.5;
    private static final double WEIGHT_DESCRIPTION_MATCH = 1.0;
    private static final double WEIGHT_CATEGORY_MATCH = 1.5;
    
    /**
     * Representa un item buscable con sus metadatos
     */
    public record SearchableItem(
        String id,
        String name,
        String category,
        String description,
        List<String> keywords,
        String type,
        Map<String, Object> metadata
    ) {}
    
    /**
     * Resultado de búsqueda con score
     */
    public record SearchResult(
        SearchableItem item,
        double score
    ) implements Comparable<SearchResult> {
        @Override
        public int compareTo(SearchResult other) {
            return Double.compare(other.score, this.score);
        }
    }
    
    /**
     * Busca items que coincidan con la query
     */
    public List<SearchResult> search(String query, List<SearchableItem> items, int maxResults) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        
        Set<String> queryTokens = tokenize(query);
        
        return items.stream()
            .map(item -> new SearchResult(item, calculateScore(queryTokens, query, item)))
            .filter(result -> result.score() > 0)
            .sorted()
            .limit(maxResults)
            .collect(Collectors.toList());
    }
    
    /**
     * Calcula el score de relevancia para un item
     */
    private double calculateScore(Set<String> queryTokens, String originalQuery, SearchableItem item) {
        double score = 0.0;
        
        // 1. Match en keywords (peso alto)
        score += calculateKeywordScore(queryTokens, item.keywords());
        
        // 2. Match en nombre
        score += calculateNameScore(queryTokens, originalQuery, item.name());
        
        // 3. Match en descripción
        score += calculateDescriptionScore(queryTokens, item.description());
        
        // 4. Match en categoría
        score += calculateCategoryScore(queryTokens, item.category());
        
        return score;
    }
    
    /**
     * Score por coincidencia en keywords
     */
    private double calculateKeywordScore(Set<String> queryTokens, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return 0.0;
        }
        
        double score = 0.0;
        Set<String> normalizedKeywords = keywords.stream()
            .map(this::normalize)
            .collect(Collectors.toSet());
        
        for (String queryToken : queryTokens) {
            // Match exacto
            if (normalizedKeywords.contains(queryToken)) {
                score += WEIGHT_EXACT_KEYWORD;
                continue;
            }
            
            // Match parcial (el keyword contiene el token o viceversa)
            for (String keyword : normalizedKeywords) {
                if (keyword.contains(queryToken) || queryToken.contains(keyword)) {
                    score += WEIGHT_PARTIAL_KEYWORD;
                    break;
                }
            }
        }
        
        return score;
    }
    
    /**
     * Score por coincidencia en nombre
     */
    private double calculateNameScore(Set<String> queryTokens, String originalQuery, String name) {
        if (name == null || name.isBlank()) {
            return 0.0;
        }
        
        String normalizedName = normalize(name);
        String normalizedQuery = normalize(originalQuery);
        
        // Match exacto del nombre completo
        if (normalizedName.equals(normalizedQuery)) {
            return WEIGHT_NAME_MATCH * 2;
        }
        
        // El nombre contiene la query completa
        if (normalizedName.contains(normalizedQuery)) {
            return WEIGHT_NAME_MATCH * 1.5;
        }
        
        // Match de tokens individuales
        Set<String> nameTokens = tokenize(name);
        long matches = queryTokens.stream()
            .filter(qt -> nameTokens.stream().anyMatch(nt -> nt.contains(qt) || qt.contains(nt)))
            .count();
        
        return matches > 0 ? WEIGHT_NAME_MATCH * ((double) matches / queryTokens.size()) : 0.0;
    }
    
    /**
     * Score por coincidencia en descripción (TF-IDF simplificado)
     */
    private double calculateDescriptionScore(Set<String> queryTokens, String description) {
        if (description == null || description.isBlank()) {
            return 0.0;
        }
        
        Set<String> descTokens = tokenize(description);
        
        long matches = queryTokens.stream()
            .filter(qt -> descTokens.stream().anyMatch(dt -> dt.contains(qt) || qt.contains(dt)))
            .count();
        
        return matches > 0 ? WEIGHT_DESCRIPTION_MATCH * ((double) matches / queryTokens.size()) : 0.0;
    }
    
    /**
     * Score por coincidencia en categoría
     */
    private double calculateCategoryScore(Set<String> queryTokens, String category) {
        if (category == null || category.isBlank()) {
            return 0.0;
        }
        
        String normalizedCategory = normalize(category);
        Set<String> categoryTokens = tokenize(category);
        
        for (String queryToken : queryTokens) {
            if (normalizedCategory.contains(queryToken) || 
                categoryTokens.stream().anyMatch(ct -> ct.contains(queryToken))) {
                return WEIGHT_CATEGORY_MATCH;
            }
        }
        
        return 0.0;
    }
    
    /**
     * Tokeniza un texto en palabras normalizadas
     */
    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptySet();
        }
        
        return Arrays.stream(normalize(text).split("\\s+"))
            .filter(token -> token.length() > 1) // Ignorar tokens de 1 caracter
            .filter(token -> !isStopWord(token))
            .collect(Collectors.toSet());
    }
    
    /**
     * Normaliza texto: minúsculas, sin acentos, sin caracteres especiales
     */
    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        
        // Convertir a minúsculas
        String result = text.toLowerCase();
        
        // Remover acentos/diacríticos
        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        result = DIACRITICS_PATTERN.matcher(result).replaceAll("");
        
        // Reemplazar guiones y underscores por espacios
        result = result.replaceAll("[-_]", " ");
        
        // Remover caracteres especiales excepto espacios
        result = result.replaceAll("[^a-z0-9\\s]", "");
        
        return result.trim();
    }
    
    /**
     * Verifica si es una stop word (palabras muy comunes sin valor semántico)
     */
    private static final Set<String> STOP_WORDS = Set.of(
        "de", "la", "el", "en", "y", "los", "las", "del", "un", "una",
        "con", "para", "por", "es", "al", "se", "que", "su", "o", "como",
        "the", "an", "and", "or", "of", "to", "in", "for", "on", "with"
    );
    
    private boolean isStopWord(String word) {
        return STOP_WORDS.contains(word);
    }
}
