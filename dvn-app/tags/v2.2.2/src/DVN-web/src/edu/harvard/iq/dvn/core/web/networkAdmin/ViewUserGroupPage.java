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
 * ViewUserGroupPage.java
 *
 * Created on October 20, 2006, 4:36 PM
 *
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.UserGroupServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.EJB;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class ViewUserGroupPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB private UserGroupServiceLocal userGroupService;
   
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public ViewUserGroupPage() {
    }
    
    public void init() {
        super.init();
    }
    
    public String getUserList() {
        String userStr = "";
        Collection users = getGroup().getUsers();
        for (Iterator it = users.iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            userStr+=elem.getUserName();
            if (it.hasNext()) {
                userStr+=", ";
            }
        }
        return userStr;
    }
    
    public UserGroup getGroup( ) {
        return userGroupService.findById(userGroupId);
    }
    
    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }
    
    /**
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
    }
    
    /**
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
    }
    
  
    
     
        
        /**
         * Holds value of property userGroupId.
         */
        private Long userGroupId;
        
        /**
         * Getter for property userGroupId.
         * @return Value of property userGroupId.
         */
        public Long getUserGroupId() {
            return this.userGroupId;
        }
        
        /**
         * Setter for property userGroupId.
         * @param userGroupId New value of property userGroupId.
         */
        public void setUserGroupId(Long userGroupId) {
            this.userGroupId = userGroupId;
        }
        
     
        
        
        
        
    }
    
