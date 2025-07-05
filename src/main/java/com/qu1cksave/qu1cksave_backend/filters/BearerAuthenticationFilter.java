package com.qu1cksave.qu1cksave_backend.filters;

import com.qu1cksave.qu1cksave_backend.exceptions.CustomFilterException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Checks existence of auth header and if its well-formed
//   Well-formed means: Length of 2 after split and using bearer authentication
public class BearerAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req,
        // Needed to remove "Not annotated parameter overrides @NonNullApi parameter"
        @NonNull HttpServletResponse res,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = req.getHeader("Authorization");

        // TODO:
        //  - For now, this goes first before APIKeyFilter since I mistakenly
        //    thought that putting the api key in the auth header along with
        //    the jwt would be fine.
        //  - APIKeyFilter should also not be applied to login and signup
        //    endpoints later
        //    -- For now, it does since the API key is in the Authorization header

        // https://swagger.io/docs/specification/v3_0/authentication/api-keys/
        // - X-API-Key for header for API keys
        //   -- Though, I read somewhere that this might be unsafe
        // DO NOT MIX the bearer jwt token and the API key in the Authorization
        //   header. Ex. Bearer APIKey JWT (WRONG !!!)
        // - https://stackoverflow.com/questions/59151878/can-multiple-bearer-token-supported-in-authorization-header
        // - https://stackoverflow.com/questions/74312810/is-it-possible-to-send-two-authorization-header-bearer-and-basic
        // - https://stackoverflow.com/questions/3761845/multiple-authentication-schemes-for-http-authorization-header
        if (authHeader == null || authHeader.isEmpty()) {
            throw new CustomFilterException("Missing auth header");
        }

        // TODO: Later, auth header should be:
        //  Authorization: Bearer JWTTokenHere
        //  - Where the auth header would be in a different header
        //  (TEMPORARY) Auth header would look like:
        //    Authorization: Bearer APIKey JWTTokenHere
        //  - So splitHeader[0] = Bearer
        String[] splitHeader = authHeader.split(" ");
        // TODO: Change to < 2 || > 3 once api key issue is solved
        if (splitHeader.length < 2 || splitHeader.length > 3) {
            throw new CustomFilterException("Invalid/malformed auth header");
        }
        if (splitHeader[0] == null || splitHeader[0].isEmpty()) {
            throw new CustomFilterException("Missing authentication scheme");
        }
        if (!splitHeader[0].equals("Bearer")) {
            throw new CustomFilterException("Not using bearer authentication");
        }

        req.setAttribute("apiKey", splitHeader[1]);
        // TODO: Change to splitHeader[1] once api key issue is solved
        // Only set the jwt if one was provided
        if (splitHeader.length == 3) {
            req.setAttribute("jwt", splitHeader[2]);
        } else {
            req.setAttribute("jwt", null);
        }
        filterChain.doFilter(req, res);
    }
}

// BearerAuthenticationFilter -> APIKeyFilter -> JWTFilter -> MemberAuthorizationFilter

// Filters
//  - https://springframework.guru/jwt-authentication-in-spring-microservices-jwt-token/
//    -- Doesn't use Spring Security
//    -- Really good. Even has some code on how to work with JWT
//    -- This one uses a filter for authentication too (to check the jwt in
//       the header)
//       + In addition, I need a way to extract the authorities from the jwt
//    -- *** extends GenericFilterBean
//  - https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac
//    -- Also has info when working with JWTs
//    -- *** extends OncePerRequestFilter
//  - https://stackoverflow.com/questions/75117913/how-do-i-manually-register-filters-in-springboot
//    -- Also uses FilterRegistrationBean
//  - Can register different filters by having multiple FilterRegistrationBeans
//  - https://docs.spring.io/spring-security/reference/servlet/architecture.html
//    -- Has really nice non Spring Security specific info about filters
//  - https://www.baeldung.com/spring-boot-add-filter
//    -- GREAT SOURCE :)
//    -- Don't use @Component if using FilterRegistrationBean
//    -- Contains info about setting url patterns
//    -- *** implements Filter
//  - https://www.baeldung.com/spring-security-custom-filter
//    -- The GenericFilterBean is a simple javax.servlet.Filter implementation that is Spring-aware
//    -- *** extends GenericFilterBean
// *** NOTE: Know the difference between:
//  - implements Filter vs. extends GenericFilterBean vs. extends OncePerRequestFilter
//    -- https://stackoverflow.com/questions/50410901/genericfilterbean-vs-onceperrequestfilter-when-to-use-each
//       + Good explanation on OncePerRequestFilter usage
//    -- https://www.baeldung.com/spring-onceperrequestfilter
//       -- More on OncePerRequestFilter
//    -- https://stackoverflow.com/questions/35978352/why-use-onceperrequestfilter-in-spring
//  - @Component on filter vs using FilterRegistrationBean
//

// Filters (more)
// - RestControllerAdvice can't catch exceptions thrown by Filters
//   -- Filters happens before controllers are even resolved so exceptions thrown from filters can't be caught by a Controller Advice
// - https://stackoverflow.com/questions/34595605/how-to-manage-exceptions-thrown-in-filters-in-spring
//   -- Has something about a custom ExceptionHandlerFilter (GREAT)
// - https://www.marcobehler.com/guides/spring-mvc
//   -- Has a way to write a response using HttpServletResponse and getWriter
//   -- NEED to set type to json
// - https://jenkov.com/tutorials/java-servlets/servlet-filters.html
//   -- Good
//   -- Also has a way to write a response using HttpResponse and getWriter
