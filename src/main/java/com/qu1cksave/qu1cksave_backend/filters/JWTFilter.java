package com.qu1cksave.qu1cksave_backend.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

// Checks the provided JWT in the auth header
public class JWTFilter extends GenericFilterBean {
    private final String secret = System.getenv("ACCESS_TOKEN");

    @Override
    public void doFilter(
        ServletRequest servletReq,
        ServletResponse servletRes,
        FilterChain filterChain
    ) throws ServletException, IOException {
        final HttpServletRequest req = (HttpServletRequest) servletReq;
        final HttpServletResponse res = (HttpServletResponse) servletRes;
        final String jwt = (String) req.getAttribute("jwt");

        // Verify the jwt
//        Claims claims = Jwts.parser().setSigningKey("secret").parseClaimsJws(token).getBody();
        // https://javadoc.io/static/io.jsonwebtoken/jjwt-api/0.12.6/io/jsonwebtoken/JwtParserBuilder.html#verifyWith(javax.crypto.SecretKey)
        // https://stackoverflow.com/questions/78115368/java-21-jwts-parser-is-deprecated
        // - I used this
        // https://github.com/jwtk/jjwt#json-jackson-custom-types
        // - .json(new JacksonDeserializer(Maps.of("user", User.class).build()))
        //   -- TODO: Maybe I need to deserialize it here?
        //       + Or can I just deserialize it in the next filter (via
        //         Jackson's ObjectMapper)
        Claims claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
            .build()
            .parseSignedClaims(jwt)
            .getPayload();

        // Set the userId and the roles
        // NOTE: If I ever need more than the userId, I can set it here
        Object userId = claims.get("id");
        Object roles = claims.get("roles");
        if (userId == null || roles == null) {
            throw new ServletException("Missing claims.");
        }
        req.setAttribute("userId", userId);
        req.setAttribute("roles", roles);

        filterChain.doFilter(req, res);
    }
}
