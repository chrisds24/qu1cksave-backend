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

        // TODO: I need to verify using Firebase Admin SDK
        //  - First, I need to setup my Firebase project
        //    -- Create Firebase accounts for users I'll use in dev mode + tests
        //    -- Put their Firebase UID in the dev and test databases
        //  - Second, I need to setup Admin SDK here
        //    -- Install
        //    -- Then setup the config for the backend to use
        //    -- Is it possible to use a Bean in a filter?
        //  - I then need to fix my tests
        //  - I also need to edit my exceptions
        //  --------------------
        //  Basically, first use verifyToken to verify token validity
        //  Then from the result of that, check if that user's email has been
        //    verified
        //  If it is indeed verified, check for user existence in the DB
        //    + If user exists, can proceed as usual
        //    + If user doesn't exist, signup the user to the DB, then proceed
        //      as usual
        //  -------------------
        //  *** IMPORTANT: Tricky part is how I'll do the tests since I now
        //      need to have an actual valid token for verifyToken to work.
        //      Should I actually login via Firebase to get a valid token?
        //      Or should I use a workaround so I don't have to make a call to
        //      Firebase?
        //  - Also, how do I test login?
        //    -- I can't really test certain things such as wrong credentials
        //       and non-existent user, since Firebase Auth does that in the
        //       frontend.
        //       + However, I can still test:
        //         * Valid token: I can just say this is tested for any test
        //           where the request is able to go past the JWT Filter.
        //         * Invalid token: Just make a request to fetch the jobs list,
        //           but use an invalid format token (I don't want to accidentally
        //           use a valid format Firebase token that actually ends up being
        //           valid)
        //         * Verified email: Same as valid token
        //         * Unverified email: Just use a user with email_verified = false
        //       + How to have a verified email if none of the emails here in
        //         the dev environment are real emails? I can just manually edit
        //         email_verified = true
        //    -- Remember: Login via Firebase -> Put token into cookie ->
        //       Redirect/Refresh -> Request to fetch jobs list ->
        //       JWT Filter to perform the checks mentioned above
        //  - How about testing signup?
        //    -- Signup to Firebase happens in the frontend
        //    -- When does a backend signup attempt happen? It's when a user who's
        //       successfully signed up via Firebase and has verified their
        //       email makes an authenticated request but they don't have
        //       an entry in my database
        //    -- For testing signing up a new user, I can use a user from
        //       Firebase but they don't have an entry here in my DB.
        //       + After they're signed up and their jobs list (empty) has
        //         been fetched, just delete them from the test db. (I don't
        //         really need to delete them in the db since the tests ending
        //         resets the test db.)
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

    // As of the addition of Firebase Authentication, there are no longer login
    //   and signup endpoints. Keeping this for reference
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest req) {
//        String path = req.getRequestURI();
//        return path.startsWith("/api/v0/user/login") || path.startsWith("/api/v0/user/signup");
//    }
}
