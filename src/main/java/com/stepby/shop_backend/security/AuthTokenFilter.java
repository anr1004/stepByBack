package com.stepby.shop_backend.security;

import com.stepby.shop_backend.service.UserDetailsServiceImpl;
import com.stepby.shop_backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    // <<<--- 생성자 내부에 System.out.println 추가 --->>>
    public AuthTokenFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        System.out.println("########## AuthTokenFilter CONSTRUCTOR: jwtUtil is " + (jwtUtil != null ? "NOT NULL" : "NULL"));
        System.out.println("########## AuthTokenFilter CONSTRUCTOR: userDetailsService is " + (userDetailsService != null ? "NOT NULL" : "NULL"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("########## AuthTokenFilter - DOING INTERNAL FILTER FOR URI: " + request.getRequestURI());

        // <<<--- doFilterInternal 내부에서 의존성 NULL 체크 추가 --->>>
        if (jwtUtil == null || userDetailsService == null) {
            System.err.println("########## AuthTokenFilter ERROR: Dependencies (jwtUtil or userDetailsService) are NULL inside doFilterInternal! Cannot proceed with authentication.");
            filterChain.doFilter(request, response); // 다음 필터로 진행 (인증 없이)
            return; // 조기 종료
        }

        logger.debug("AuthTokenFilter - Processing request to: {}", request.getRequestURI());

        try {
            String jwt = parseJwt(request);
            logger.debug("AuthTokenFilter - Extracted JWT from header: {}", (jwt != null ? jwt.substring(0, Math.min(jwt.length(), 30)) + "..." : "null (No JWT)"));

            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                String username = jwtUtil.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("AuthTokenFilter - User '{}' authenticated and context set.", username);
            } else if (jwt != null) {
                logger.warn("AuthTokenFilter - JWT token validation failed.");
            } else {
                logger.debug("AuthTokenFilter - No JWT token found in request. Proceeding as anonymous.");
            }
        } catch (Exception e) {
            logger.error("AuthTokenFilter - Cannot set user authentication in SecurityContext: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        logger.debug("AuthTokenFilter - Raw Authorization header: {}", headerAuth);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String jwt = headerAuth.substring(7);
            logger.debug("AuthTokenFilter - Parsed JWT: {}", jwt.substring(0, Math.min(jwt.length(), 30)) + "...");
            return jwt;
        }

        return null;
    }
}