/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
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

