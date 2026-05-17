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

            // 엔드포인트별 인증 정책
            .authorizeHttpRequests(auth -> auth
                // Actuator의 모니터링 엔드포인트는 인증 없이 접근 가능
                // - prometheus: 메트릭 수집 (Prometheus가 인증 없이 호출)
                // - health: 헬스체크 (ALB / ECS 등에서 사용)
                .requestMatchers("/actuator/prometheus", "/actuator/health").permitAll()
                // 그 외 모든 요청은 Basic Auth 필요 (설정 조회 등)
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
