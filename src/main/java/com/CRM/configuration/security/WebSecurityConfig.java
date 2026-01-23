package com.CRM.configuration.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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

                                .authorizeHttpRequests(requests -> {
                                        requests
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
                                                                        "/configuration/security",
                                                                        String.format("%s/**", Enpoint.API_PREFIX),
                                                                        String.format("%s/roles", Enpoint.API_PREFIX),
                                                                        String.format("%s/categories",
                                                                                        Enpoint.API_PREFIX),
                                                                        String.format("%s/banners", Enpoint.API_PREFIX),
                                                                        String.format("%s/brands",
                                                                                        Enpoint.API_PREFIX),
                                                                        String.format("%s/warehouses",
                                                                                        Enpoint.API_PREFIX)

                                                        )

                                                        .permitAll()
                                                        .anyRequest()
                                                        .authenticated();
                                });

                /*
                 * CORS (Cross-Origin Resource Sharing) là cơ chế bảo mật của trình duyệt.
                 * - Frontend chạy ở http://localhost:3000
                 * - Backend chạy ở http://localhost:8080
                 * --> Trình duyệt sẽ chặn request nếu backend không cho phép CORS
                 * => Swagger UI cũng là một frontend chạy trong browser, nên nó cũng chịu CORS.
                 */

                // Bật CORS trong Spring Security
                httpSecurity.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
                        /* Cấu hình CORS */
                        @Override
                        public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                                CorsConfiguration configuration = new CorsConfiguration();
                                // Cho phép MỌI domain gọi API
                                configuration.setAllowedOrigins(List.of("*"));
                                /*
                                 * GET – đọc dữ liệu
                                 * POST – tạo
                                 * PUT / PATCH – cập nhật
                                 * DELETE – xoá
                                 * OPTIONS – Preflight request (RẤT QUAN TRỌNG)
                                 * --> Thiếu OPTIONS → Swagger & browser bị chặn ngay từ đầu
                                 */
                                configuration.setAllowedMethods(
                                                Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                                /*
                                 * Cho phép header được gửi lên
                                 * Cho phép client gửi các header:
                                 * >>> Authorization → JWT Bearer token
                                 * >>> Content-Type → JSON
                                 * >>> x-auth-token → custom token
                                 * --> Nếu thiếu authorization → JWT không hoạt động
                                 */
                                configuration.setAllowedHeaders(
                                                Arrays.asList("authorization", "content-type", "x-auth-token"));
                                /*
                                 * Expose header cho client đọc
                                 * Mặc định browser KHÔNG cho frontend đọc header response
                                 * --> Dòng này cho phép frontend:
                                 * >>>>>>>đọc x-auth-token từ response
                                 */
                                configuration.setExposedHeaders(List.of("x-auth-token"));
                                // Tạo nguồn cấu hình CORS
                                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                                /*
                                 * Áp dụng cho toàn bộ API
                                 * Áp dụng CORS cho:
                                 * >>> /api/**
                                 * >>> /swagger-ui/**
                                 * >>> /v3/api-docs/**
                                 * ➡️ Swagger trong Docker NHỜ DÒNG NÀY MÀ SỐNG
                                 */
                                source.registerCorsConfiguration("/**", configuration);
                                // Gán vào Spring Security
                                httpSecurityCorsConfigurer.configurationSource(source);
                        }
                });
                return httpSecurity.build();

        }
}
