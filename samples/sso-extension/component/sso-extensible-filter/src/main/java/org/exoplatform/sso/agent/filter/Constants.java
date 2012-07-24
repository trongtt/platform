package org.exoplatform.sso.agent.filter;

/**
 * Created with IntelliJ IDEA.
 * User: kmenzli
 * Date: 24/07/12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public interface Constants {

    public static final String SSO_SERVER_URL = "sso.server.url" ;
    public static final String SSO_COOKIE_NAME = "sso.cookie.name" ;
    public static final String LOGIN_URL = "login.url" ;
    public static final String CAS_RENEW_TICKET = "cas.renew.ticket" ;
    public static final String CAS_SERVICE_URL = "cas.service.url" ;

    //-- Constants for LoginRedirectFilter
    public static final String LOGIN_SERVICE_URL = "login.redirect.url" ;
    public static final String SSO_PATH = "/sso" ;
    //-- Constants for LogoutFilter
    public static final String LOGOUT_URL = "logout.url" ;
    public static final String FILE_ENCODING = "file.encoding" ;
    public static final String SSO_SERVER_TYPE = "sso.server.type";
    public static final String SSO_SERVER_FLAG = "SSO_SERVER_FLAG";
    public static final String SSO_LOGOUT_FLAG = "SSO_LOGOUT_FLAG";
    //-- Server Type
    public static final String SSO_SERVER_CAS = "cas";
    public static final String SSO_SERVER_OPENAM = "openAM";
    public static final String SSO_SERVER_JOSSO = "josso";


}
