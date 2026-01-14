package com.stepby.shop_backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final String jwtSecretSting; // Base64 인코딩된 시크릿 키 문자열
    private final Key signingKey; // 토큰 서명에 사용될 Key 객체 (한번만 사용)

    @Value("${app.jwtExpirationMs}") // application.properties에서 만료 시간을 주입받음
    private int jwtExpirationMs;

    public JwtUtil(@Value("${app.jwtSecret}") String jwtSecretSting) {
        this.jwtSecretSting = jwtSecretSting;

        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretSting));
    }


    // JWT 토큰 생성
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal(); //인증된 사용자 정보

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims() // playload 설정
                .subject(userPrincipal.getUsername()) // subject는 email
                .issuedAt(now) // 토큰 발행 시간
                .expiration(expiryDate) // 토큰 만료 시간
                .and() // 다시 Jwts.builder()로
                .signWith(signingKey) // 서명 알고리즘 및 키
                .compact(); // 토큰 생성
    }

    // JWT 토큰에서 사용자 이름 추출
    public String getUserNameFromJwtToken(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith((SecretKey) signingKey)
                .build()
                .parseSignedClaims(token);

                return claimsJws.getPayload().getSubject();
    }

    // JWT 토큰 유효성 검사
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) signingKey)
                    .build()
                    .parseSignedClaims(authToken);

            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}

