package com.vertex.backendcore.config;

import com.vertex.backendcore.service.PermissionEvaluatorService;
import com.vertex.backendcore.service.PermissionLookupService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@AutoConfiguration
@EnableMethodSecurity
public class MethodSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PermissionEvaluatorService permissionEvaluatorService() {
        return new PermissionEvaluatorService();
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionLookupService permissionLookupService() {
        return new PermissionLookupService();
    }

    @Bean
    @ConditionalOnMissingBean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(PermissionEvaluator evaluator) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(evaluator);
        return handler;
    }
}