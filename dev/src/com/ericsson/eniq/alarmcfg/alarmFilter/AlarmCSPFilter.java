package com.ericsson.eniq.alarmcfg.alarmFilter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AlarmCSPFilter implements Filter {
	


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//Invoked when filter config is done
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletResponse httpResponse = ((HttpServletResponse) response);
		httpResponse.setHeader("Content-Security-Policy", "frame-ancestors 'self'; default-src 'self' 'unsafe-inline' ; ");
		httpResponse.setHeader("Cache-Control", "no-cache, no-store");
		
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpSession session = httpReq.getSession();
		String sess = (String) session.getAttribute("csrfToken");
		httpResponse.addHeader("Set-Cookie", sess);
		
		chain.doFilter(request, response);
		
		
	}

	@Override
	public void destroy() {
		// This is invoked only once when filter is taken out of the service.

	}


}
