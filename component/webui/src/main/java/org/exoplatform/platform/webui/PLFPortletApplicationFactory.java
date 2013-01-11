package org.exoplatform.platform.webui;

import org.exoplatform.webui.application.portlet.PortletApplication;
import org.exoplatform.webui.application.portlet.PortletApplicationFactory;

import javax.portlet.PortletConfig;

/**
 * 
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class PLFPortletApplicationFactory implements PortletApplicationFactory {
    
    public PortletApplication createApplication(PortletConfig portletConfig) {
        return new PLFPortletApplication(portletConfig);
    }
}
