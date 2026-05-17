package com.eyc.key.modules.auth.security;

import com.eyc.key.modules.auth.entity.User;
import com.eyc.key.modules.auth.repository.UserRepository;
import com.eyc.key.modules.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFillter extends  OncePerRequestFilter {
    private final JwtService  jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChai)
        throws ServletException, IOException{
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChai.doFilter(request, response);
            return;
        }
        String jwt = authorizationHeader.substring(7);
        String username =  jwtService.extractUsername(jwt);

        try {
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                User user  = userRepository.findByUsername(username).orElse(null);
                if (user != null && jwtService.isTokenValid(jwt,user)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }catch (Exception e){
            log.warn("[JWT] JWT Không hợp lệ");
        }
        filterChai.doFilter(request, response);
    }


}
