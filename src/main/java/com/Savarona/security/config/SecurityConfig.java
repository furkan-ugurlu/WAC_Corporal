package com.Savarona.security.config;

import com.Savarona.security.filter.FirebaseAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, FirebaseAuthenticationFilter authFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/api/users/hello", "/api/users/hello/**", "/api/users/test").permitAll()
                        .requestMatchers("/api/users/register").permitAll()

                        // Protected endpoints (authentication gerektiren)
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/users/credits").authenticated()
                        .requestMatchers("/api/reflections/**").authenticated()  // ✅ Fix: It must be authenticated()
                        .requestMatchers("/api/credits/**").authenticated()

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}