package com.gursewak.texttolearn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;

import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import com.nimbusds.jose.JWSAlgorithm;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${supabase.jwks.uri}")
    private String jwksUri;

    @Value("${app.env:dev}")
    private String appEnv;

    @Value("${supabase.jwks.local-key:}")
    private String localJwk;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // SSE stream endpoints must be public (EventSource can't send auth headers)
                .requestMatchers("/api/courses/*/stream").permitAll()
                // Require authentication for all other API endpoints
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        if ("dev".equalsIgnoreCase(appEnv) && localJwk != null && !localJwk.isEmpty()) {
            // LOCAL DEV: Load the public key from .env to prevent java.net.SocketTimeoutException 
            // without pushing actual key strings to GitHub
            ECKey ecKey;
            try {
                ecKey = ECKey.parse(localJwk);
            } catch (java.text.ParseException e) {
                throw new IllegalStateException("Failed to parse local JWK from environment", e);
            }
            JWKSet jwkSet = new JWKSet(ecKey);
            ImmutableJWKSet<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);
            
            JWSKeySelector<SecurityContext> jwsKeySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.ES256, jwkSource);
                
            DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(jwsKeySelector);
            
            return new NimbusJwtDecoder(jwtProcessor);
        } else {
            // PRODUCTION: Fetch keys from Supabase JWKS URI to handle automatic key rotation
            return NimbusJwtDecoder.withJwkSetUri(jwksUri)
                    .jwsAlgorithm(SignatureAlgorithm.ES256)
                    .build();
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        String frontendUrl = System.getenv("FRONTEND_URL");
        configuration.setAllowedOrigins(Arrays.asList(
            frontendUrl != null ? frontendUrl : "http://localhost:5173"
        )); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
