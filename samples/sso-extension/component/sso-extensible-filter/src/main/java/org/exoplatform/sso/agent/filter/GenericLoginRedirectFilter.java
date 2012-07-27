package org.exoplatform.sso.agent.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.exoplatform.container.web.AbstractFilter;
import org.exoplatform.web.filter.Filter;

public class GenericLoginRedirectFilter extends AbstractFilter implements Filter, Constants {

    private String loginUrl;

    public void init() {
	    this.loginUrl = SSOConfigUtils.getLoginRedirectUrl();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	    init();

        HttpServletRequest httpRequest = (HttpServletRequest)request;

		HttpServletResponse httpResponse = (HttpServletResponse)response;

		boolean isLoginInProgress = isLoginInProgress(httpRequest);

		if (isLoginInProgress) {

		      httpResponse.sendRedirect(this.loginUrl);

		      return;
		}

		chain.doFilter(request, response);

	}

	private boolean isLoginInProgress(HttpServletRequest request) {

	    String action = request.getRequestURI();

		return (action != null) && (action.equals(request.getContextPath() + SSO_PATH));
	}


	public void destroy() {
	
	}
}