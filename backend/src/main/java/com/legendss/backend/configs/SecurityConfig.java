package com.legendss.backend.configs;

import com.legendss.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // health check
                        .requestMatchers("/api/v2/health", "/health").permitAll()

                        // auth
                        .requestMatchers("/api/v2/auth/register", "/api/v2/auth/verify", "/api/v2/auth/login").permitAll()
                        .requestMatchers("/api/v2/auth/get", "/api/v2/auth/getall").authenticated()

                        // wheelhairs
                        .requestMatchers("/api/v2/wheelchair/add").hasRole("USER")
                        .requestMatchers("/api/v2/wheelchair/update").hasRole("USER")
                        .requestMatchers("/api/v2/wheelchair/delete").hasRole("USER")
                        .requestMatchers("/api/v2/wheelchair/my").hasRole("USER")

                        // users functionalities
                        .requestMatchers("/api/v2/wheelchair/add-relative").hasRole("USER")
                        .requestMatchers("/api/v2/wheelchair/relative/my-tracked").hasRole("RELATIVE")
                        .requestMatchers("/api/v2/wheelchair/getallrel").hasRole("USER")
                        
                        // panic & fakepanic logs
                        .requestMatchers("/api/v2/panic/relative/my-tracked").hasRole("RELATIVE")
                        .requestMatchers("/api/v2/fakepanic/relative/my-tracked").hasRole("RELATIVE")

                        .requestMatchers("/api/v2/navigation/**").permitAll()

                        .requestMatchers("/api/v2/mobility/relative/my-tracked").hasRole("RELATIVE")
                        .requestMatchers("/api/v2/mobility/relative/activity-logs/**").hasRole("RELATIVE")

                        .requestMatchers("/api/v2/wheelchair/update/{id}").permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}