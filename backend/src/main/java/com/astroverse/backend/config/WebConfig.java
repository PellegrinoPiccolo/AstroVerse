package com.astroverse.backend.config;

import com.astroverse.backend.component.RequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final RequestInterceptor requestInterceptor;

    public WebConfig(RequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Consente CORS per tutti gli endpoint
                .allowedOrigins("http://localhost:5173/")  // Specifica gli origini consentiti; usa "*" per consentire tutte
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Metodi consentiti
                .allowedHeaders("*")  // Specifica le intestazioni consentite "*" consente tutte
                .allowCredentials(true);  // Permetti le credenziali
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor).addPathPatterns("/**");
    }
}