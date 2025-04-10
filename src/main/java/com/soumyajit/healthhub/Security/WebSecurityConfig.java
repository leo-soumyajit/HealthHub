package com.soumyajit.healthhub.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Permit OPTIONS preflight requests for all endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/mealplan").authenticated()
                        .requestMatchers(HttpMethod.POST,"/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/posts/createPost/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.PUT,"/posts/update/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.DELETE,"/posts/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.POST,"/posts/createPost/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/posts/update/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/posts/**").hasRole("ADMIN")
                        .requestMatchers("/like/**","/removelike/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandlingConfig ->
                        exceptionHandlingConfig.accessDeniedHandler(accessDeniedHandler()));

        return httpSecurity.build();
    }

    @Bean // Authentication Manager bean for login
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return (request, response, accessDeniedException) -> {
            handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
        };
    }

    // Define a CorsConfigurationSource to set up CORS settings.
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // Set allowed origins; for development you may use "*" to allow all origins
        config.setAllowedOrigins(Arrays.asList("http://localhost:63342"));
        // Allowed headers
        config.addAllowedHeader("*");
        // Allowed HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        // Optionally set the max age for the pre-flight request to be cached
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration for all endpoints
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
