package org.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.models.ErrorDetails;
import org.converters.ErrorDetailsConverter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthorizationFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger(AuthorizationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String authorization = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        if (authorization == null || !authorization.equals("1234567890qawsedrftgthyujkiol")) {
            LOGGER.error("Token not valid");
            ErrorDetails error = new ErrorDetails("The call is not allowed");
            ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
            String errorJson = errorDetailsConverter.convert(error);
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            servletResponse.setContentType("application/json");
            try (PrintWriter writer = servletResponse.getWriter()) {
                writer.write(errorJson);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
