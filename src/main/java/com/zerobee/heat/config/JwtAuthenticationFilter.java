package com.zerobee.heat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    
    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        // Skip JWT processing for paths that don't need authentication
        String requestPath = request.getRequestURI();
        if (shouldSkipAuthentication(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Check if the Authorization header is present and starts with "Bearer "
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String token = authorizationHeader.substring(7);
        
        try {
            // Extract user email from token
            String userEmail = jwtService.getUserNameFromToken(token);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            
            filterChain.doFilter(request, response);
            
        } catch (ExpiredJwtException ex) {
            handleJwtException(response, HttpStatus.UNAUTHORIZED, "JWT token has expired");
        } catch (MalformedJwtException ex) {
            handleJwtException(response, HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (SignatureException ex) {
            handleJwtException(response, HttpStatus.UNAUTHORIZED, "JWT signature validation failed");
        } catch (Exception ex) {
            handleJwtException(response, HttpStatus.INTERNAL_SERVER_ERROR, "Authentication error: " + ex.getMessage());
        }
    }
    
    private boolean shouldSkipAuthentication(String path) {             // Add paths that should bypass authentication
        return path.contains("/api/auth") ||
                path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                path.equals("/");
    }
    
    private void handleJwtException(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        CustomResponse<Void> errorResponse = new CustomResponse<>(status, message, null);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}