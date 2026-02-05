package com.argendata.mcp.poncho.config;

import com.argendata.mcp.poncho.tools.PonchoDocsTools;
import com.argendata.mcp.poncho.tools.PonchoFrameworkTools;
import com.argendata.mcp.poncho.tools.PonchoGeneratorTools;
import com.argendata.mcp.poncho.tools.PonchoTemplateTools;
import com.argendata.mcp.poncho.tools.PonchoUtilsTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de tools MCP para Poncho
 */
@Configuration
public class McpConfig {
    
    @Bean
    public ToolCallbackProvider ponchoDocsToolCallbacks(PonchoDocsTools tools) {
        return MethodToolCallbackProvider.builder()
            .toolObjects(tools)
            .build();
    }
    
    @Bean
    public ToolCallbackProvider ponchoGeneratorToolCallbacks(PonchoGeneratorTools tools) {
        return MethodToolCallbackProvider.builder()
            .toolObjects(tools)
            .build();
    }
    
    @Bean
    public ToolCallbackProvider ponchoUtilsToolCallbacks(PonchoUtilsTools tools) {
        return MethodToolCallbackProvider.builder()
            .toolObjects(tools)
            .build();
    }
    
    @Bean
    public ToolCallbackProvider ponchoFrameworkToolCallbacks(PonchoFrameworkTools tools) {
        return MethodToolCallbackProvider.builder()
            .toolObjects(tools)
            .build();
    }
    
    @Bean
    public ToolCallbackProvider ponchoTemplateToolCallbacks(PonchoTemplateTools tools) {
        return MethodToolCallbackProvider.builder()
            .toolObjects(tools)
            .build();
    }
}
