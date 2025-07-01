package com.qu1cksave.qu1cksave_backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

// Checks existence of auth header and if its well-formed
//   Well-formed means: Length of 2 after split and using bearer authentication
public class BearerAuthenticationFilter extends GenericFilterBean {
    @Override
    public void doFilter(
        ServletRequest servletReq,
        ServletResponse servletRes,
        FilterChain filterChain
    ) throws ServletException, IOException {
        final HttpServletRequest req = (HttpServletRequest) servletReq;
        final HttpServletResponse res = (HttpServletResponse) servletRes;
        final String authHeader = req.getHeader("Authorization");

        // TODO:
        //  - For now, this goes first before JWTFilter since I mistakenly
        //    thought that putting the api key in the auth header along with
        //    the jwt would be fine.

        // https://swagger.io/docs/specification/v3_0/authentication/api-keys/
        // - X-API-Key for header for API keys
        //   -- Though, I read somewhere that this might be unsafe
        // DO NOT MIX the bearer jwt token and the API key in the Authorization
        //   header. Ex. Bearer APIKey JWT (WRONG !!!)
        // - https://stackoverflow.com/questions/59151878/can-multiple-bearer-token-supported-in-authorization-header
        // - https://stackoverflow.com/questions/74312810/is-it-possible-to-send-two-authorization-header-bearer-and-basic
        // - https://stackoverflow.com/questions/3761845/multiple-authentication-schemes-for-http-authorization-header
        if (authHeader == null) {
            throw new ServletException("Missing auth header");
        }

        // TODO: Later, auth header should be:
        //  Authorization: Bearer JWTTokenHere
        //  - Where the auth header would be in a different header
        //  (TEMPORARY) Auth header would look like:
        //    Authorization: Bearer APIKey JWTTokenHere
        //  - So splitHeader[0] = Bearer
        String[] splitHeader = authHeader.split(" ");
        // TODO: Change to != 2 once api key issue is solved
        if (splitHeader.length != 3) {
            throw new ServletException("Invalid/malformed auth header");
        }

        if (!splitHeader[0].equals("Bearer")) {
            throw new ServletException("Not using bearer authentication");
        }

        req.setAttribute("apiKey", splitHeader[1]);
        // TODO: Change to splitHeader[1] once api key issue is solved
        req.setAttribute("jwt", splitHeader[2]);

        filterChain.doFilter(req, res);
    }
}
