package com.qu1cksave.qu1cksave_backend.filters;

import com.qu1cksave.qu1cksave_backend.exceptions.RequestBodyTooLargeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Used to limit request body size to 2 MB. Sources:
// - https://javadoc.io/doc/javax.servlet/javax.servlet-api/latest/javax/servlet/http/HttpServletRequest.html
// - https://javadoc.io/static/javax.servlet/javax.servlet-api/4.0.1/javax/servlet/ServletRequest.html
// - https://stackoverflow.com/questions/78569774/how-to-restrict-post-request-payload-size-in-spring-boot-to-1mb-or-2mb
// - https://stackoverflow.com/questions/73685979/what-is-the-default-max-post-content-size-in-spring-boot/73686336#73686336
// - https://stackoverflow.com/questions/38607159/spring-boot-embedded-tomcat-application-json-post-request-restriction-to-10kb/38611154#38611154
//   -- Has example of a filter
public class ReqBodySizeFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req,
        // Needed to remove "Not annotated parameter overrides @NonNullApi parameter"
        @NonNull HttpServletResponse res,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        int contentLength = req.getContentLength();
        // 2097152 is 2 MB
        // If length is not known or is greater than Integer.MAX_VALUE,
        //   contentLength would be -1
        if (contentLength < 0 || contentLength > 2097152) {
            throw new RequestBodyTooLargeException(
                "Request body size must not exceed 2 MB"
            );
        }

        filterChain.doFilter(req, res);
    }

    protected boolean shouldNotFilter(HttpServletRequest req) {
        String httpMethod = req.getMethod();
        // Should not apply this filter to methods that aren't
        //   POST, PUT, or PATCH
        return !(httpMethod.equalsIgnoreCase("POST") ||
            httpMethod.equalsIgnoreCase("PUT") ||
            httpMethod.equalsIgnoreCase("PATCH"));
    }
}
