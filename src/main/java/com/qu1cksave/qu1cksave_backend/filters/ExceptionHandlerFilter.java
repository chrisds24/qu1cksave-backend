package com.qu1cksave.qu1cksave_backend.filters;

import com.qu1cksave.qu1cksave_backend.exceptions.CustomFilterException;
import com.qu1cksave.qu1cksave_backend.exceptions.ForbiddenResourceException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain filterChain
    ) {
        try {
            filterChain.doFilter(req, res);
        } catch (CustomFilterException err) {
            setStatusAndContentToJson(
                res,
                HttpStatus.UNAUTHORIZED
            );
        } catch (ForbiddenResourceException err) {
            setStatusAndContentToJson(
                res,
                HttpStatus.FORBIDDEN
            );
        } catch (Exception err) {
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
