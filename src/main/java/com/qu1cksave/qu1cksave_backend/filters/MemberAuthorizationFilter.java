package com.qu1cksave.qu1cksave_backend.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qu1cksave.qu1cksave_backend.exceptions.CustomFilterException;
import com.qu1cksave.qu1cksave_backend.exceptions.ForbiddenResourceException;
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
import java.util.Arrays;

public class MemberAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req,
        @NonNull HttpServletResponse res,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        final String[] roles =
            objectMapper.readValue((String) req.getAttribute("roles"), String[].class);

        // Note: The Node.js version does things differently. It checks all the
        //   roles (scopes) needed for an endpoint, checks if the request
        //   has all these roles, then rejecting if it doesn't. Though, it
        //   still resolves the user if the endpoint doesn't require any roles
        //   It's the same here, I'm just using a different filter for each
        //   role.
        if (!Arrays.asList(roles).contains("member")) {
            throw new ForbiddenResourceException("Missing member role");
        }

        filterChain.doFilter(req, res);

        // TODO: (7/1/25)
        //  1.) Register the filters
        //  2.) Add order
        //  3.) Create exceptions for filters (Ex. JWTFilterException,
        //        APIKeyFilterException, etc.)
        //  4.) Retrieve userId from each endpoint that needs it
        //  5.) Tests
        //      - Would now need to add API key to header before running tests
        //      - Would now need to login before each authenticated endpoint
        //  6.) Check if roles are deserialized properly
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String path = req.getRequestURI();
        return path.startsWith("/login") || path.startsWith("/signup");
    }
}
