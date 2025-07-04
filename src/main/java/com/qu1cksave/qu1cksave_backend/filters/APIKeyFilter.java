package com.qu1cksave.qu1cksave_backend.filters;

import com.qu1cksave.qu1cksave_backend.exceptions.CustomFilterException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class APIKeyFilter extends OncePerRequestFilter {
    private final String apiKey = System.getenv("API_KEY");

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req,
        @NonNull HttpServletResponse res,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String providedApiKey = (String) req.getAttribute("apiKey");

        if (providedApiKey == null || providedApiKey.isEmpty()) {
            throw new CustomFilterException("Missing API key");
        }
        if (!providedApiKey.equals(apiKey)) {
            throw new CustomFilterException("Invalid API key");
        }

        filterChain.doFilter(req, res);
    }
}

