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

@Application
@Portlet
@Assets(
        scripts = { @Script(id = "jquery",src ="js/jquery-1.8.3.js"),
                 @Script( src = "js/invitations/invitations.js",depends = "jquery") },
        stylesheets = {@Stylesheet(src = "style/invitations/invitations.css")
        },
        location = juzu.asset.AssetLocation.SERVER
)
package org.exoplatform.platform.portlet.juzu.invitations;

import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.portlet.Portlet;

/**
 * @author <a href="hzekri@exoplatform.com">hzekri</a>
 * @date 10/12/12
 */