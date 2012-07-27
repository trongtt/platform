package org.exoplatform.sso.agent.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.exoplatform.container.web.AbstractFilter;
import org.exoplatform.web.filter.Filter;

public  class GenericLogoutFilter  extends AbstractFilter implements Filter,Constants {

	protected String logoutUrl;
    protected String ssoServerType;

	private static final String fileEncoding = System.getProperty(FILE_ENCODING);
		
	void init(HttpServletRequest httpRequest)  throws UnsupportedEncodingException
    {
        logoutUrl=SSOConfigUtils.getLogoutUrl(httpRequest);
        ssoServerType=SSOConfigUtils.getSsoServerType();

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest)request;

        HttpServletResponse httpResponse = (HttpServletResponse)response;

        init(httpRequest);

        boolean isLogoutInProgress = isLogoutInProgress(httpRequest);

		if (isLogoutInProgress) {

            if (httpRequest.getSession().getAttribute(SSO_SERVER_FLAG) == null) {

                httpRequest.getSession().setAttribute(SSO_LOGOUT_FLAG, Boolean.TRUE);

               /* String redirectUrl = getRedirectUrl(httpRequest);

                redirectUrl = httpResponse.encodeRedirectURL(redirectUrl);

                httpResponse.sendRedirect(redirectUrl);  */

                httpResponse.sendRedirect(logoutUrl);

                if(((HttpServletRequest) request).getSession()!=null) ((HttpServletRequest) request).getSession().invalidate();

                return;
            }

            httpRequest.getSession().removeAttribute(SSO_LOGOUT_FLAG);
		}

		chain.doFilter(request, response);}

	private boolean isLogoutInProgress(HttpServletRequest request) throws UnsupportedEncodingException {

        if (fileEncoding != null) {
		    request.setCharacterEncoding(fileEncoding);
	    }

        String action = request.getParameter("portal:action");

        return (action != null) && (action.equals("Logout"));
	}

/*	protected String getRedirectUrl(HttpServletRequest httpRequest) {

        String redirectUrl=null;
		try {
			if(ssoServerType!=null) {

				if(ssoServerType.equals(SSO_SERVER_JOSSO)){

                    String parameters = URLEncoder.encode("portal:componentId=UIPortal&portal:action=Logout", "UTF-8");

                    redirectUrl = this.logoutUrl + "?josso_back_to=" + httpRequest.getRequestURL() + "?" + parameters;
				} else if (ssoServerType.equals(SSO_SERVER_CAS)) {

                    redirectUrl = this.logoutUrl + "?service=" + httpRequest.getRequestURL() + "&url=" + httpRequest.getRequestURL();

                    httpRequest.getSession().invalidate();
				} else if (ssoServerType.equals(SSO_SERVER_OPENAM)){

					String parameters = URLEncoder.encode("portal:componentId=UIPortal&portal:action=Logout", "UTF-8");

                    redirectUrl = this.logoutUrl + "?realm=gatein&goto=" + httpRequest.getRequestURL() + "?" + parameters;
				}
			}

            return redirectUrl;
		}  catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
    */
	public void destroy() {
		
	}
 }
