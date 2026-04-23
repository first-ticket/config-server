package com.firstticket.configserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (Config Server는 상태 없는 API이므로 불필요)
            .csrf(csrf -> csrf.disable())

            // 모든 엔드포인트에 Basic Auth 필요
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )

            // Basic Auth 활성화
            .httpBasic(basic -> {})

            // 세션 사용 안 함 (stateless API)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
