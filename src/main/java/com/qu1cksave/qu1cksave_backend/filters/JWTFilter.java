package com.qu1cksave.qu1cksave_backend.filters;

import com.qu1cksave.qu1cksave_backend.exceptions.CustomFilterException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
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

// Checks the provided JWT in the auth header
public class JWTFilter extends OncePerRequestFilter {
    private final String secret = System.getenv("ACCESS_TOKEN");

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req,
        @NonNull HttpServletResponse res,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String jwt = (String) req.getAttribute("jwt");

        if (jwt == null || jwt.isEmpty()) {
            throw new CustomFilterException("Missing JWT");
        }

        // Verify the jwt
//        Claims claims = Jwts.parser().setSigningKey("secret").parseClaimsJws(token).getBody();
        // https://javadoc.io/static/io.jsonwebtoken/jjwt-api/0.12.6/io/jsonwebtoken/JwtParserBuilder.html#verifyWith(javax.crypto.SecretKey)
        // https://stackoverflow.com/questions/78115368/java-21-jwts-parser-is-deprecated
        // - I used this
        // https://github.com/jwtk/jjwt#json-jackson-custom-types
        // - .json(new JacksonDeserializer(Maps.of("user", User.class).build()))
        // - I just converted from ArrayList to String[]
        Claims claims;
        try {
            claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        } catch (DecodingException err) {
            throw new CustomFilterException("Error decoding the String secret", err);
        } catch (WeakKeyException err) {
            throw new CustomFilterException(
                "The key byte array length is less than 256 bits (32 bytes)",
                err
            );
        } catch (UnsupportedJwtException err) {
            throw new CustomFilterException(
                "During parseSignedClaims, jwt argument is not a signed Claims JWT",
                err
            );
        } catch (JwtException err) {
            throw new CustomFilterException(
                "During parseSignedClaims, jwt cannot be parsed or validated",
                err
            );
        } catch (IllegalArgumentException err) {
            throw new CustomFilterException(
                "During parseSignedClaims, jwt is null/empty/only whitespace",
                err
            );
        }
        if (claims == null) {
            throw new CustomFilterException("Null claims");
        }

        // Set the userId and the roles
        // NOTE: If I ever need more than the userId, I can set it here
        Object userId = claims.get("id");
        Object roles = claims.get("roles");
        if (userId == null) {
            throw new CustomFilterException("Missing userId in claims.");
        }
        req.setAttribute("userId", userId);
        req.setAttribute("roles", roles);

        filterChain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String path = req.getRequestURI();
        return path.startsWith("/login") || path.startsWith("/signup");
    }
}
