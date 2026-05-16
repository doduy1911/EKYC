package com.eyc.key.modules.auth.service;

import com.eyc.key.modules.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",user.getRole().name());
        claims.put("email",user.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllclaims(String token){
        return  Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private  String extractUsername(String token){
        return extractAllclaims(token).getSubject();
    }
    private boolean isTokenExpired(String token){
        return extractAllclaims(token).getExpiration().before(new Date());
    }

    private boolean isTokenValid(String token , User user){
        try {
            String username = extractUsername(token);
            return username.equals(user.getUsername()) && !isTokenExpired(token);
        }catch (Exception e){
            log.warn("JWT validation failed {}",e.getMessage());
            return  false;
        }
    }

    private long  getAccessTokenExpiration(){
        return accessTokenExpiration / 1000;
    }


}
