/**
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.websphere.web.security;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.portal.webui.util.Util;
import javax.servlet.http.Cookie;
import org.exoplatform.portal.application.PortalRequestContext;

/**
 * @author <a href="kmenzli@exoplatform.com">Kmenzli</a>
 * @date 03/10/12
 */
public class LogoutListener extends Listener<ConversationRegistry, ConversationState> {

    private static final Log logger = ExoLogger.getLogger(LogoutListener.class);

    private ExoContainerContext context;

    public LogoutListener(ExoContainerContext context) throws Exception {

        this.context = context;
    }

    @Override
    public void onEvent(Event<ConversationRegistry, ConversationState> event) throws Exception {

        String userId = event.getData().getIdentity().getUserId();

        try {

            if (logger.isInfoEnabled()) {

                logger.info("Starting logout USER ["+userId+"] ....");

            }
            PortalRequestContext prContext = Util.getPortalRequestContext();

            Cookie cookie = new Cookie("LtpaToken2", "");

            cookie.setPath("/");

            prContext.getResponse().addCookie(cookie);

            if (logger.isInfoEnabled()) {

                logger.info("USER ["+userId+"] logout.");

            }

        } catch (Exception E) {

            logger.debug("Error while logout the user [" + userId + "]: " + E.getMessage(), E);

        }
    }
}
