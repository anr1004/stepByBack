package com.stepby.shop_backend.config;

import com.stepby.shop_backend.security.AuthEntryPointJwt;
import com.stepby.shop_backend.security.AuthTokenFilter;
import com.stepby.shop_backend.service.UserDetailsServiceImpl;
import com.stepby.shop_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }


    // JWT 토큰 검증 필터 Bean 생성
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        return new AuthTokenFilter(jwtUtil, userDetailsService);
    }

    // 비밀번호 암호화를 위한 BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 사용자 정보 로딩 및 비밀번호 검증 담당
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // SOBOOM님의 환경에서 확인된 1개의 인자(UserDetailsService)만 받는 생성자 사용
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        // PasswordEncoder는 setter 메서드를 통해 별도로 설정 (이전 버전 방식)
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // 인증 처리 핵심
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // 자격 증명 (쿠키, 인증 헤더) 허용

        // !!! 프론트엔드 Origin을 정확히 입력합니다. !!!
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173")); // 여러개 허용 시 Arrays.asList

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")); // 모든 HTTP 메서드 허용
        config.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        config.setExposedHeaders(Arrays.asList("Authorization", "RefreshToken")); // 프론트엔드에서 접근 가능한 헤더 명시 (필요시)
        config.setMaxAge(3600L); // Preflight 요청 캐싱 시간 (초)

        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 설정 적용
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthTokenFilter authTokenFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (JWT 사용 시 일반적으로 필요 없음)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // 인증 실패 시 처리
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함 (JWT는 무상태)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 인증/회원가입 API는 모두 허용
                        .requestMatchers("/api/products/**").permitAll() // 상품 조회 API는 모두 허용
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/user/me").authenticated()
                        // 여기에 다른 공개 API 경로를 추가
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                );

        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)); // H2 콘솔 사용 시 필요

        http.authenticationProvider(authenticationProvider()); // 인증 프로바이더 설정

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }

}
