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
import org.gatein.sso.agent.cas.CASAgent;
import org.gatein.sso.agent.josso.JOSSOAgent;
import org.gatein.sso.agent.opensso.OpenSSOAgent;

public class GenericInitiateLoginFilter extends AbstractFilter implements Filter,Constants {

    private String ssoServerUrl;

    private String ssoCookieName;

    private boolean casRenewTicket;

	private String casServiceUrl;

	private String loginUrl;

	public void init() throws ServletException {

    this.ssoServerUrl =  SSOConfigUtils.getSsoServerUrl();

    this.ssoCookieName = SSOConfigUtils.getSsoCookieName();

    this.loginUrl = SSOConfigUtils.getLoginUrl();

 	String casServiceUrlConfig =  SSOConfigUtils.getCasServiceUrl();

    if ((casServiceUrlConfig == null) || (casServiceUrlConfig.trim().length() <= 0))

		  return;
		  this.casServiceUrl = casServiceUrlConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

	    try {

            init();

            HttpServletRequest req = (HttpServletRequest)request;

            HttpServletResponse resp = (HttpServletResponse)response;


            processSSOToken(req, resp);


            String portalContext = req.getContextPath();

            if (req.getAttribute("abort") != null) {

                String ssoRedirect = portalContext + SSO_PATH;

                resp.sendRedirect(ssoRedirect);

                return;
	        }

	      resp.sendRedirect(this.loginUrl);

	      return;
	    } catch (Exception e) {

	        throw new ServletException(e);

	    }
	}

	public void destroy() {

	}

	private void processSSOToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {

        String ticket = httpRequest.getParameter(CAS_TICKET);

        String jossoAssertion = httpRequest.getParameter(JOSSO_ASSERTION_ID);

	    if ((ticket != null) && (ticket.trim().length() > 0)) {

            CASAgent casagent = CASAgent.getInstance(this.ssoServerUrl, this.casServiceUrl);

            casagent.setRenewTicket(this.casRenewTicket);

            casagent.validateTicket(httpRequest, ticket);

	    } else if ((jossoAssertion != null) && (jossoAssertion.trim().length() > 0)) {

	        JOSSOAgent.getInstance().validateTicket(httpRequest, httpResponse);

	    } else {

	        try {

	            OpenSSOAgent.getInstance(this.ssoServerUrl, this.ssoCookieName).validateTicket(httpRequest);

	        } catch (IllegalStateException ilse) {

                httpRequest.setAttribute("abort", Boolean.TRUE);

            }
	    }

	  }

	}
	
