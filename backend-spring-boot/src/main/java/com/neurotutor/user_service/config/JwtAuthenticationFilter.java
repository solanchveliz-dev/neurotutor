package com.neurotutor.user_service.config;

import com.neurotutor.user_service.service.JwtService;
import com.neurotutor.user_service.repository.EstudianteRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_AUTH_PATHS = List.of(
            "/api/register",
            "/api/login",
            "/api/forgot-password",
            "/api/reset-password"
    );

    private final JwtService jwtService;
    private final EstudianteRepository estudianteRepository;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   EstudianteRepository estudianteRepository) {
        this.jwtService = jwtService;
        this.estudianteRepository = estudianteRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PUBLIC_AUTH_PATHS.contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                String studentId = jwtService.extractStudentId(authorization.substring(7));
                Long id = Long.valueOf(studentId);
                if (!estudianteRepository.existsById(id)) {
                    SecurityContextHolder.clearContext();
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT student does not exist");
                    return;
                }
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                studentId,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
                        )
                );
            } catch (JwtException | IllegalArgumentException | IllegalStateException ignored) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
