package com.myoffice.payroll_system.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${app.security.jwt.jwk-set-uri:}")
    private String jwkSetUri;

    @Value("${app.security.jwt.issuer-uri:}")
    private String issuerUri;

    @Value("${app.security.allowed-origins:http://localhost:5173,http://127.0.0.1:5173,http://localhost:3000,http://127.0.0.1:3000}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .exceptionHandling(exceptions -> exceptions
                                .authenticationEntryPoint((request, response, authException) -> {
                                    logAuthenticationFailure(request.getRequestURI(), authException);
                                    writeErrorResponse(
                                            response,
                                            HttpServletResponse.SC_UNAUTHORIZED,
                                            "Authentication is required to access this resource.",
                                            request.getRequestURI());
                                })
                        .accessDeniedHandler((request, response, accessDeniedException) -> writeErrorResponse(
                                response,
                                HttpServletResponse.SC_FORBIDDEN,
                                "You do not have permission to access this resource.",
                                request.getRequestURI())))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/workshifts/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/employees").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/payrolls").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/shift-assignments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/employees/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/payrolls/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/shift-assignments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String resolvedJwkSetUri = resolveJwkSetUri();
        if (StringUtils.hasText(resolvedJwkSetUri)) {
            log.info("Configuring JWT decoder with JWKS. issuer={}, jwkSetUri={}", resolveIssuerUri(), resolvedJwkSetUri);
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(resolvedJwkSetUri)
                    .jwsAlgorithm(SignatureAlgorithm.ES256)
                    .jwsAlgorithm(SignatureAlgorithm.RS256)
                    .build();
            jwtDecoder.setJwtValidator(jwtValidator(resolveIssuerUri()));
            return jwtDecoder;
        }

        if (!StringUtils.hasText(jwtSecret)) {
            throw new IllegalStateException(
                    "JWT verification must be configured with either app.security.jwt.jwk-set-uri/supabase.url or jwt.secret.");
        }

        SecretKey secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        log.info("Configuring JWT decoder with shared secret fallback.");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    private OAuth2TokenValidator<Jwt> jwtValidator(String resolvedIssuerUri) {
        if (!StringUtils.hasText(resolvedIssuerUri)) {
            return JwtValidators.createDefault();
        }
        return new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                JwtValidators.createDefaultWithIssuer(resolvedIssuerUri));
    }

    private String resolveJwkSetUri() {
        if (StringUtils.hasText(jwkSetUri)) {
            return jwkSetUri;
        }
        if (StringUtils.hasText(supabaseUrl)) {
            return normalizeBaseUrl(supabaseUrl) + "/auth/v1/.well-known/jwks.json";
        }
        return null;
    }

    private String resolveIssuerUri() {
        if (StringUtils.hasText(issuerUri)) {
            return issuerUri;
        }
        if (StringUtils.hasText(supabaseUrl)) {
            return normalizeBaseUrl(supabaseUrl) + "/auth/v1";
        }
        return null;
    }

    private String normalizeBaseUrl(String value) {
        try {
            URL parsed = new URL(value);
            String protocol = parsed.getProtocol();
            String host = parsed.getHost();
            int port = parsed.getPort();
            String authority = port > 0 ? host + ":" + port : host;
            return protocol + "://" + authority;
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("supabase.url must be a valid URL.", ex);
        }
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter defaultAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>(defaultAuthoritiesConverter.convert(jwt));
            authorities.addAll(extractRoleAuthorities(jwt));
            return authorities;
        });
        return authenticationConverter;
    }

    private Collection<GrantedAuthority> extractRoleAuthorities(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        String role = jwt.getClaimAsString("role");
        if (role != null && !role.isBlank()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            roles.stream()
                    .filter(value -> value != null && !value.isBlank())
                    .map(value -> new SimpleGrantedAuthority("ROLE_" + value))
                    .forEach(authorities::add);
        }

        return authorities;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void writeErrorResponse(jakarta.servlet.http.HttpServletResponse response,
            int status,
            String message,
            String path) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String error = status == HttpServletResponse.SC_UNAUTHORIZED ? "Unauthorized" : "Forbidden";
        String escapedMessage = escapeJson(message);
        String escapedPath = escapeJson(path);
        String body = """
                {"status":%d,"error":"%s","message":"%s","path":"%s"}
                """.formatted(status, error, escapedMessage, escapedPath);
        response.getWriter().write(body);
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private void logAuthenticationFailure(String path, Exception authException) {
        Throwable cause = authException.getCause();
        if (cause instanceof OAuth2AuthenticationException oauth2Exception) {
            log.warn("JWT authentication failed for path {}: {} - {}",
                    path,
                    oauth2Exception.getError().getErrorCode(),
                    oauth2Exception.getError().getDescription());
            return;
        }

        if (cause != null) {
            log.warn("JWT authentication failed for path {}: {}", path, cause.getMessage());
            return;
        }

        log.warn("JWT authentication failed for path {}: {}", path, authException.getMessage());
    }
}
