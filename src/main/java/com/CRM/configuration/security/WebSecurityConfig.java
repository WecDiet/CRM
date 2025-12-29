package com.CRM.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.CRM.constant.Enpoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                httpSecurity
                                .csrf(AbstractHttpConfigurer::disable)

                                .authorizeHttpRequests(request -> request
                                                .requestMatchers(
                                                                // Swagger
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/index.html",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/swagger/**",
                                                                "/api-docs/**",
                                                                "/swagger-resources/**",
                                                                "/swagger-resources/",
                                                                "/configuration/ui",
                                                                "/configuration/security")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET,
                                                                String.format("%s/*", Enpoint.API_PREFIX),
                                                                String.format("%s/products/*", Enpoint.API_PREFIX),
                                                                String.format("%s/detail/*", Enpoint.Product.BASE),
                                                                String.format("%s/roles/*", Enpoint.API_PREFIX),
                                                                String.format("%s/detail/*", Enpoint.Role.BASE))
                                                .permitAll()

                                                .requestMatchers(HttpMethod.POST,
                                                                String.format("%s/new", Enpoint.Role.BASE))
                                                .permitAll()

                                                .requestMatchers(HttpMethod.PUT,
                                                                String.format("%s/update/*", Enpoint.Role.BASE))
                                                .permitAll()

                                                .requestMatchers(HttpMethod.DELETE,
                                                                String.format("%s/delete/*", Enpoint.Role.BASE))
                                                .permitAll());
                // .anyRequest()
                // .authenticated());
                return httpSecurity.build();

        }
}
