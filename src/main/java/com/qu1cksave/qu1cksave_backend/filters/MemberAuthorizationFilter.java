package com.qu1cksave.qu1cksave_backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;

public class MemberAuthorizationFilter extends GenericFilterBean {
    @Override
    public void doFilter(
        ServletRequest servletReq,
        ServletResponse servletRes,
        FilterChain filterChain
    ) throws ServletException, IOException {
        final HttpServletRequest req = (HttpServletRequest) servletReq;
        final HttpServletResponse res = (HttpServletResponse) servletRes;
        // TODO: Might need to use ObjectMapper readValueAs/writeValueAs
        final String[] roles = (String[]) req.getAttribute("roles");

        // Note: The Node.js version does things differently. It checks all the
        //   roles (scopes) needed for an endpoint, checks if the request
        //   has all these roles, then rejecting if it doesn't. Though, it
        //   still resolves the user if the endpoint doesn't require any roles
        //   It's the same here, I'm just using a different filter for each
        //   role.
        if (!Arrays.asList(roles).contains("member")) {
            throw new ServletException("Unauthorized. Missing member role");
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
}
