package com.qu1cksave.qu1cksave_backend.filters;

import com.qu1cksave.qu1cksave_backend.exceptions.CustomFilterException;
import com.qu1cksave.qu1cksave_backend.exceptions.ForbiddenResourceException;
import com.qu1cksave.qu1cksave_backend.exceptions.RequestBodyTooLargeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req,
        @NonNull HttpServletResponse res,
        @NonNull FilterChain filterChain
    ) {
        try {
            filterChain.doFilter(req, res);
        } catch (CustomFilterException err) {
//            System.out.println("****** MESSAGE (CustomFilterException): " + err.getMessage());
            setStatusAndContentToJson(
                res,
                HttpStatus.UNAUTHORIZED
            );
        } catch (ForbiddenResourceException err) {
//            System.out.println("****** MESSAGE (ForbiddenResourceException): " + err.getMessage());
            setStatusAndContentToJson(
                res,
                HttpStatus.FORBIDDEN
            );
        } catch (RequestBodyTooLargeException err) {
            setStatusAndContentToJson(
                res,
                HttpStatus.PAYLOAD_TOO_LARGE
            );
        } catch (Exception err) {
//            System.out.println("****** MESSAGE (Exception): " + err.getMessage());
            setStatusAndContentToJson(
                res,
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private void setStatusAndContentToJson(
        HttpServletResponse res,
        HttpStatus status
    ) {
        res.setStatus(status.value());
        res.setContentType("application/json");
//        res.getWriter().print((String) null); // No need to write since empty
    }
}
