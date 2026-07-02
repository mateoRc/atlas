package dev.backendlab.atlas;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
class ServiceAuthFilter extends OncePerRequestFilter {

    private final byte[] expectedAuthorization;

    ServiceAuthFilter(@Value("${atlas.auth-token}") String authToken) {
        if (authToken.isBlank()) {
            throw new IllegalArgumentException("atlas.auth-token must not be blank");
        }
        this.expectedAuthorization =
                ("Bearer " + authToken).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals("/search");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        byte[] actual = authorization == null
                ? new byte[0]
                : authorization.getBytes(StandardCharsets.UTF_8);

        if (!MessageDigest.isEqual(expectedAuthorization, actual)) {
            response.setHeader("WWW-Authenticate", "Bearer");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
