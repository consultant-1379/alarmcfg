package com.ericsson.eniq.alarmcfg.common;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AdvancedAccessLogFilter implements Filter {

    private static final String PASSWORD_REGEX = "password=[^&]+";
    private static final String PASSWORD_MASK = "password=***";
    private FilterConfig filterConfig = null;

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        if (filterConfig == null) {
            return;
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String maskedPath = request.getRequestURI()
                + (request.getQueryString() == null ? "" : request.getQueryString().replaceAll(PASSWORD_REGEX, PASSWORD_MASK))
                + " "
                + request.getProtocol();

        request.setAttribute("maskedPath", maskedPath);
        chain.doFilter(request, servletResponse);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
}
