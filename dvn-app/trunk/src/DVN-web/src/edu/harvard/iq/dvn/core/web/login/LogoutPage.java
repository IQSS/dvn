/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * LoginPage.java
 *
 * Created on October 2, 2006, 5:28 PM
 */
package edu.harvard.iq.dvn.core.web.login;

import edu.harvard.iq.dvn.core.web.StudyListing;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.common.VDCSessionBean;
import java.util.Map;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */

@ViewScoped
@Named("LogoutPage")
public class LogoutPage extends VDCBaseBean implements java.io.Serializable  {
 
    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public LogoutPage() {
    }


 

    public void init() {
        super.init();
        // Do logout here
        doLogout(getVDCSessionBean(), getSessionMap());
        getVDCRenderBean().getFlash().put("successMessage","You are now logged out.");        
    }


    
    public static void doLogout(VDCSessionBean vdcSessionBean, Map sessionMap) {
        vdcSessionBean.setLoginBean(null);
        sessionMap.remove("ORIGINAL_URL");
        StudyListing.clearStudyListingMap( sessionMap );       
    }
    
}

