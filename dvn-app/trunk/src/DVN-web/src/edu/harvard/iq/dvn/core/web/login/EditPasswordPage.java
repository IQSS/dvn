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
 * AddAccountPage.java
 *
 * Created on October 4, 2006, 1:04 PM
 */
package edu.harvard.iq.dvn.core.web.login;

import edu.harvard.iq.dvn.core.admin.EditUserService;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlInputHidden;
import com.icesoft.faces.component.ext.HtmlInputSecret;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@EJB(name="editUser", beanInterface=edu.harvard.iq.dvn.core.admin.EditUserService.class)

@ViewScoped
@Named("EditPasswordPage")
public class EditPasswordPage extends VDCBaseBean implements java.io.Serializable  {
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
      }
   
    
    // </editor-fold>
    EditUserService editUserService;
    @EJB UserServiceLocal userService;
    HtmlInputHidden hiddenUserId;
    HtmlInputSecret inputNewPassword;

    public HtmlInputSecret getInputNewPassword() {
        return inputNewPassword;
    }

    public void setInputNewPassword(HtmlInputSecret inputNewPassword) {
        this.inputNewPassword = inputNewPassword;
    }

    public HtmlInputHidden getHiddenUserId() {
        return hiddenUserId;
    }

    public void setHiddenUserId(HtmlInputHidden hiddenUserId) {
        this.hiddenUserId = hiddenUserId;
    }
    

    
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public EditPasswordPage() {
    }
    
    
    
    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     *
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    public void init() {
        super.init();
        
        if ("viewAccount".equals(returnPage)) {
            returnPage = "/login/AccountPage.xhtml?faces-redirect=true&userId="+userId;
        } else {
            returnPage = "/login/AccountOptionsPage?faces-redirect=true&userId="+userId;
        }        
       
                  // we need to create the editStudyService bean
        try {
            Context ctx = new InitialContext();
            editUserService = (EditUserService) ctx.lookup("java:comp/env/editUser");
        } catch(NamingException e) {
            e.printStackTrace();
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),null);
            context.addMessage(null,errMessage);

        }        
        editUserService.setUser(userId); 
        user = editUserService.getUser();
         
      
        
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
     * Holds value of property user.
     */
    private VDCUser user;
    
    /**
     * Getter for property user.
     * @return Value of property user.
     */
    public VDCUser getUser() {
        return this.user;
    }
    
    /**
     * Setter for property user.
     * @param user New value of property user.
     */
    public void setUser(VDCUser user) {
        this.user = user;
    }
    
    public EditUserService getEditUserService() {
        return editUserService;
    }
    
  
    
    public String save() {
        
       
        
        editUserService.save();
        // If the currently logged-in user is updating is account, reset the User object in the session
        if (getVDCSessionBean().getLoginBean().getUser().getId().equals(user.getId())) {
            this.getVDCSessionBean().getLoginBean().setUser(user);
        }

        getVDCRenderBean().getFlash().put("successMessage", "Password updated successfully.");
        return returnPage;
    }
    
    public String cancel() {
        // Save userId as requestAttribute so it can be used by AccountPage
        this.getRequestMap().put("userId",user.getId());
             editUserService.cancel();
        return returnPage;
    }

    String returnPage;

    public String getReturnPage() {
        return returnPage;
    }

    public void setReturnPage(String returnPage) {
        this.returnPage = returnPage;
    }


    /**
     * Holds value of property userId.
     */
    private Long userId;

    /**
     * Getter for property userId.
     * @return Value of property userId.
     */
    public Long getUserId() {
        return this.userId;
    }

    /**
     * Setter for property userId.
     * @param userId New value of property userId.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

 
    public void validateOldPassword(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String oldPasswordName = (String) value;
        String errorMessage = null;
        
        // check invalid characters
     
        if (!userService.validatePassword(getVDCSessionBean().getUser().getId(), oldPasswordName)   ) {
            errorMessage = "Password is invalid.";
        }
        
      
       
        if (errorMessage != null) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }

       public void validateConfirmPassword(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String confirmPassword = (String) value;
        String errorMessage = null;
        
        // check invalid characters
        if (!inputNewPassword.getLocalValue().equals(confirmPassword)  ) {
            errorMessage = "Passwords do not match.";
        }
        
      
       
        if (errorMessage != null) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }

  private String getUserIdFromRequest() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String userIdParam=request.getParameter("userId");
        if (userIdParam==null) {
            Iterator iter = request.getParameterMap().keySet().iterator();
            while (iter.hasNext()) {
                Object key = (Object) iter.next();
                if ( key instanceof String && ((String) key).indexOf("hiddenUserId") != -1 ) {
                    userIdParam = request.getParameter((String)key);
                    break;
                }
            }
        }
        return userIdParam;
        
    }    

}

