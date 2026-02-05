package com.argendata.mcp.poncho.model;

import java.util.List;

/**
 * Representa código generado con sus dependencias
 */
public record GeneratedCode(
    String html,
    String javascript,
    List<String> cssDependencies,
    List<String> jsDependencies,
    String instructions
) {}
