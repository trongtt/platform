package org.exoplatform.sso.agent.filter;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: fatma
 * Date: 26/07/12
 * Time: 12:36
 * To change this template use File | Settings | File Templates.
 */
public class SSOConfigUtils implements Constants{
    
    private static String loginRedirectUrl;
    private static  String logoutUrl;
    private static String ssoServerType;
    private static String platformDN;
    private static String ssoServerUrl;
    private static String ssoCookieName;
    private static boolean casRenewTicket;
    private static String casServiceUrl;
    private static String loginUrl;

   
    public static String getPlatformDN() {
        platformDN= System.getProperty(PLATFORM_DN);

        if ((platformDN != null)&&(platformDN.endsWith("/")))
            {
             platformDN.substring(0,platformDN.length()-2);
            }

        if(platformDN == null) platformDN=DEFAULT_PLATFORM_DN;

        return platformDN;
    }

    public static String getSsoServerUrl() {
        ssoServerUrl=System.getProperty(SSO_SERVER_URL);
        if((ssoServerUrl!=null))
        {
            if (ssoServerUrl.endsWith("/"))
                {
                  ssoServerUrl.substring(0,ssoServerUrl.length()-2);
                }
        }
        return ssoServerUrl;
    }

    public static String getSsoServerType ()
    {
        ssoServerType= System.getProperty(SSO_SERVER_TYPE);
        return ssoServerType;
    }
   
    public static String getLoginRedirectUrl()
    {
        String ssoType=getSsoServerType();

       // if((ssoType!=null)&(ssoType.trim().length()>0))
        if(ssoType!=null)
        {
          if ((ssoType.equals(SSO_SERVER_CAS)))
          {
             loginRedirectUrl=getSsoServerUrl()+"/login?service="+getPlatformDN()+LOGIN_INITIATE;

              if(isCasRenewTicket())
                  loginRedirectUrl=loginRedirectUrl+"&renew=true";
          }
        
          else if ((ssoType.equals(SSO_SERVER_JOSSO)))
          {
               loginRedirectUrl=getSsoServerUrl()+"?josso_back_to="+getPlatformDN()+LOGIN_INITIATE;
          }

          else if ((ssoType.equals(SSO_SERVER_OPENAM)))
          {
              loginRedirectUrl=getSsoServerUrl()+"/UI/Login?realm=gatein&goto="+getPlatformDN()+LOGIN_INITIATE;
          }
        }
       return  loginRedirectUrl;
    }

    public static String getLogoutUrl(HttpServletRequest httpRequest) throws UnsupportedEncodingException {

        String ssoType= getSsoServerType();
        if((ssoType!=null))
        {

        if ((ssoType.equals(SSO_SERVER_CAS)))
        {
            logoutUrl= getSsoServerUrl()+"/logout"+ "?service=" + httpRequest.getRequestURL() + "&url=" + httpRequest.getRequestURL();
        }
        else if ((ssoType.equals(SSO_SERVER_JOSSO)))
        {
            String parameters = URLEncoder.encode("portal:componentId=UIPortal&portal:action=Logout","UTF-8");
            logoutUrl=getSsoServerUrl().replace("login.do","logout.do");
            logoutUrl=logoutUrl+"?josso_back_to=" + httpRequest.getRequestURL() + "?" + parameters;
        }
        else if ((ssoType.equals(SSO_SERVER_OPENAM)))
        {
            String parameters = URLEncoder.encode("portal:componentId=UIPortal&portal:action=Logout","UTF-8");
           logoutUrl=getSsoServerUrl()+"/UI/Logout"+ "?realm=gatein&goto=" + httpRequest.getRequestURL() + "?" + parameters;
        }
        }
        return logoutUrl;
    }

    public static String getSsoCookieName() {
     return(ssoCookieName=System.getProperty(SSO_COOKIE_NAME));
    }

    public static boolean isCasRenewTicket() {

        String casRenewTicketConfig=System.getProperty(CAS_RENEW_TICKET);
        if (casRenewTicketConfig != null) {
            casRenewTicket = Boolean.parseBoolean(casRenewTicketConfig);
        }
        return casRenewTicket;
    }

    public static String getCasServiceUrl() {
        return (casServiceUrl=getPlatformDN()+LOGIN_INITIATE);
    }

    public static String getLoginUrl() {
       return (loginUrl=getPlatformDN()+ACTION_DOLOGIN);
    }
}
