package com.qu1cksave.qu1cksave_backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class APIKeyFilter extends GenericFilterBean {
    private final String apiKey = System.getenv("API_KEY");

    @Override
    public void doFilter(
        ServletRequest servletReq,
        ServletResponse servletRes,
        FilterChain filterChain
    ) throws ServletException, IOException {
        final HttpServletRequest req = (HttpServletRequest) servletReq;
        final HttpServletResponse res = (HttpServletResponse) servletRes;
        final String providedApiKey = (String) req.getAttribute("apiKey");

        if (!providedApiKey.equals(apiKey)) {
            throw new ServletException("Invalid API key");
        }

        filterChain.doFilter(req, res);
    }
}

