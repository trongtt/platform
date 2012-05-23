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

public class GenericInitiateLoginFilter extends AbstractFilter implements Filter
	{
	  private String ssoServerUrl;
	  private String ssoCookieName;
	  private boolean casRenewTicket;
	  private String casServiceUrl;
	  private String loginUrl;

	  public void init()
	    throws ServletException
	  {
		    this.ssoServerUrl =  System.getProperty("sso.server.url");
		    this.ssoCookieName = System.getProperty("sso.cookie.name");
		    this.loginUrl = System.getProperty("login.url");

		    String casRenewTicketConfig = System.getProperty("cas.renew.ticket");
		    if (casRenewTicketConfig != null)
		    {
		      this.casRenewTicket = Boolean.parseBoolean(casRenewTicketConfig);
		    }

		    String casServiceUrlConfig =  System.getProperty("cas.service.url");
		    if ((casServiceUrlConfig == null) || (casServiceUrlConfig.trim().length() <= 0))
		      return;
		    this.casServiceUrl = casServiceUrlConfig;
		  
		 /* System.getProperty("login.url");
		  System.getProperty("enable.sso");
		  System.getProperty("enable.sso");
		  System.getProperty("cas.service.url");
		  System.getProperty("cas.renew.ticket");
		  this.ssoCookieName = System.getProperty("sso.cookie.name");
		  this.ssoServerUrl = System.getProperty("sso.server.url");
		  System.getProperty("logout.url");
		  System.getProperty("sso.server.type");
		  System.getProperty("login.redirect.url");
		  */
	  }

	  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	    throws IOException, ServletException
	  {
	    try
	    {
	      init();
	      HttpServletRequest req = (HttpServletRequest)request;
	      HttpServletResponse resp = (HttpServletResponse)response;

	      processSSOToken(req, resp);

	      String portalContext = req.getContextPath();
	      if (req.getAttribute("abort") != null)
	      {
	        String ssoRedirect = portalContext + "/sso";
	        resp.sendRedirect(ssoRedirect);
	        return;
	      }

	      resp.sendRedirect(this.loginUrl);
	      return;
	    }
	    catch (Exception e)
	    {
	      throw new ServletException(e);
	    }
	  }

	  public void destroy()
	  {
	  }

	  private void processSSOToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception
	  {
	    String ticket = httpRequest.getParameter("ticket");
	    String jossoAssertion = httpRequest.getParameter("josso_assertion_id");

	    if ((ticket != null) && (ticket.trim().length() > 0))
	    {
	      CASAgent casagent = CASAgent.getInstance(this.ssoServerUrl, this.casServiceUrl);
	      casagent.setRenewTicket(this.casRenewTicket);
	      casagent.validateTicket(httpRequest, ticket);
	    }
	    else if ((jossoAssertion != null) && (jossoAssertion.trim().length() > 0))
	    {
	      JOSSOAgent.getInstance().validateTicket(httpRequest, httpResponse);
	    }
	    else
	    {
	      try
	      {
	        OpenSSOAgent.getInstance(this.ssoServerUrl, this.ssoCookieName).validateTicket(httpRequest);
	      }
	      catch (IllegalStateException ilse)
	      {
	        httpRequest.setAttribute("abort", Boolean.TRUE);
	      }
	    }
	  }
	}
	
