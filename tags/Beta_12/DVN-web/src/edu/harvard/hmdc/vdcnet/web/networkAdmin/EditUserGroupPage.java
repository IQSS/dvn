/*
 * EditUserGroupPage.java
 *
 * Created on October 20, 2006, 4:36 PM
 *
 */
package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import edu.harvard.hmdc.vdcnet.admin.EditUserGroupService;
import edu.harvard.hmdc.vdcnet.admin.GroupServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.LoginAffiliate;
import edu.harvard.hmdc.vdcnet.admin.LoginDomain;
import edu.harvard.hmdc.vdcnet.admin.UserDetailBean;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditUserGroupPage extends VDCBaseBean {
    @EJB private EditUserGroupService editUserGroupService;
    @EJB private UserServiceLocal userService;
    @EJB GroupServiceLocal groupService;
    
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public EditUserGroupPage() {
    }
    
    public void init() {
        super.init();
        if ( isFromPage("EditUserGroupPage") && sessionGet(editUserGroupService.getClass().getName()) != null ) {
            editUserGroupService =(EditUserGroupService) sessionGet(editUserGroupService.getClass().getName());
            System.out.println("Getting stateful session bean editUserGroupService ="+editUserGroupService);
            group = editUserGroupService.getUserGroup();
            userDetails = editUserGroupService.getUserDetailBeans();
        } else {
            System.out.println("Putting stateful session bean in request, editUserGroupService =" + editUserGroupService);
            if (userGroupId==null ) {
                editUserGroupService.newUserGroup();
                sessionPut(editUserGroupService.getClass().getName(),editUserGroupService);
                group = editUserGroupService.getUserGroup();
            } else {
                editUserGroupService.setUserGroup(userGroupId);
                sessionPut(editUserGroupService.getClass().getName(),editUserGroupService);
                group = editUserGroupService.getUserGroup();
                //if it's not a new group, then figure out what type of group it is
                Iterator iterator = group.getLoginDomains().iterator();
                if (!iterator.hasNext()) {
                    this.userGroupType = "usergroup";
                } else {
                    this.userGroupType = "ipgroup";
                }
            }
            userDetails = editUserGroupService.getUserDetailBeans();
        }       
        initAffiliates();
        initCollections();
    }
    
   
    public UserGroup getGroup( ) {
        return group;
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
    
    private void initCollections() {
        
        if ( (group.getLoginDomains() == null || group.getLoginDomains().size() == 0) ) {
            LoginDomain elem = new LoginDomain();
            UserGroup group = editUserGroupService.getUserGroup();
            group.getLoginDomains();
            elem.setUserGroup(group);
            Collection loginDomains = new ArrayList();
            loginDomains.add(elem);
            group.setLoginDomains(loginDomains);
        }
       if (userDetails==null || userDetails.size()==0) {
            userDetails = new ArrayList();
            userDetails.add(new UserDetailBean());
       }
    }
    
       public String cancel() {
           return "result";
       }
       

        private boolean validateEmptyRows() {
            if (this.getUserGroupType().equals("usergroup")) {
                return false;
            } else if (this.getUserGroupType().equals("ipgroup")) {
                //check through the datatable to see if any of the fields are empty
                HtmlDataTable htmldatatable = this.dataTableIpAddresses;
                UserGroup group = this.getGroup();
                htmldatatable.setRowIndex(0);
                LoginDomain logindomain = (LoginDomain)htmldatatable.getRowData();
                
                if (logindomain.getIpAddress() != null && logindomain.getIpAddress().equals("")){
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    this.setUserGroupType("ipgroup");//to maintain state for the radio buttons and the group datatable
                    FacesMessage message = new FacesMessage("There must be at least one domain listed in order to save an ip group.");
                    facesContext.addMessage(htmldatatable.getClientId(facesContext) + ":iptable", message);
                    return true;
                }
                //if we passed the first check, now check for empty fields and remove them from the collection
                if (group.getLoginDomains().size() > 0) {
                    List data = Collections.synchronizedList((List)group.getLoginDomains());
                    Iterator iterator = data.iterator();
                        while (iterator.hasNext()) {
                            logindomain = (LoginDomain)iterator.next();
                            if (logindomain.getIpAddress() == "") {
                                iterator.remove();
                            }
                        }
                }
            }
            return false;
        }

         private boolean validateEmptyRows(String affiliateStr) {
            /*TODO: Combine these two validate methods into one */
                //check through the datatable to see if any of the fields are empty
                if (this.getUserGroupType().equals("usergroup")) {
                    return false;
                } else {
                    HtmlDataTable htmldatatable = this.affiliatesTable;
                    UserGroup group = this.getGroup();
                    htmldatatable.setRowIndex(0);
                   if (htmldatatable.isRowAvailable() == true){
                        LoginAffiliate loginaffiliate = (LoginAffiliate)htmldatatable.getRowData();
                        if (loginaffiliate.getName() != null && loginaffiliate.getName().equals("") && this.chkAffiliateLoginService == true){
                            FacesContext facesContext = FacesContext.getCurrentInstance();
                            this.setChkAffiliateLoginService(true);//to maintain state for the radio buttons and the group datatable
                            FacesMessage message = new FacesMessage("There must be at least one login service listed in order to save an affiliate.");
                            facesContext.addMessage(htmldatatable.getClientId(facesContext) + ":affiliateName", message);
                            return true;
                        }
                        //if we passed the first check, now check for empty fields and remove them from the collection
                        if (group.getLoginAffiliates().size() > 0) {
                            //remove the loginaffiliates from the table
                            List list = (List)htmldatatable.getValue();
                            int rowcount = htmldatatable.getRowCount();
                            int i = rowcount - 1;
                            while(i >= 0){
                                htmldatatable.setRowIndex(i);
                                if (htmldatatable.isRowAvailable()) {
                                    loginaffiliate = (LoginAffiliate)htmldatatable.getRowData();
                                    if ( (loginaffiliate.getName().equals("") || loginaffiliate.getName() == null ) || ( this.chkAffiliateLoginService == false ) )
                                        this.editUserGroupService.removeCollectionElement(list, loginaffiliate);
                                }
                                htmldatatable.getRowCount();
                                i--;
                            }
                        }
                    }
                }
                return false;
            }

        
        public String save( ) {
            boolean invalid;
            invalid = validateEmptyRows();
             if (invalid) {
                return null;
            }
            invalid = validateEmptyRows("affiliates");
            if (invalid) {
                return null;
            }
            invalid = false;
            for (int i=0; i < userDetails.size(); i++) {
                UserDetailBean elem = userDetails.get(i);
                elem.setValid(true);
                elem.setDuplicate(false);
                if (elem.getUserName()!=null && !elem.getUserName().trim().equals("")) {
                    VDCUser user = userService.findByUserName(elem.getUserName());
                    if (user==null) {
                        elem.setValid(false);
                        invalid=true;
                    } else {
                        // Check whether this is a duplicate user name
                        for (int j=0;j<i;j++) {
                            UserDetailBean elem2 = userDetails.get(j);
                            if (elem2.getUserName().equals(elem.getUserName())) {
                                elem.setDuplicate(true);
                                invalid=true;
                            }
                        }
                    }
                }
           }
            if(invalid) {
                return null;
            }
            else {
                editUserGroupService.setUserDetailBeans(userDetails);
                editUserGroupService.save(  );
                StatusMessage msg = new StatusMessage();
                msg.setMessageText("Successful!");
                msg.setStyleClass("successMessage");
                getRequestMap().put("statusMessage",msg);
                return "result";
            }
        }
        
        public void addRow(ActionEvent ae) {
            UIComponent dataTable = ae.getComponent().getParent().getParent();
            if (dataTable == this.dataTableUserNames) {
                UserDetailBean udb = new UserDetailBean();
                udb.setUserName("");
                userDetails.add(udb);
            }
        }
        
       
        public void removeRow(ActionEvent ae) {
            HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();
            // We are removing a row from the users list
                if (this.dataTableUserNames.getRowCount()>1){
                    userDetails.remove(dataTable.getRowData());
                } else {
                    UserDetailBean udb = (UserDetailBean)dataTable.getRowData();
                    udb.setUserName("");
                }
        }
        
        public void addIpRow(ActionEvent ae) {
            UIComponent dataTable = ae.getComponent().getParent().getParent();
            if (dataTable== this.dataTableIpAddresses) {
                LoginDomain loginDomain = new LoginDomain();
                loginDomain.setUserGroup(group);
                group.getLoginDomains().add(loginDomain);
            }
        }
        
       
        public void removeIpRow(ActionEvent ae) {
            HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();
            if (dataTable== this.dataTableIpAddresses) {
                // we are removing a row from ipaddress list
                if (dataTable.getRowCount()>1) {
                    List data = (List)dataTable.getValue();
                    this.editUserGroupService.removeCollectionElement(data,dataTable.getRowData());
                } else {
                    LoginDomain loginDomain = (LoginDomain)dataTable.getRowData();
                    loginDomain.setIpAddress("");
                }
            }  
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
        
        private HtmlDataTable dataTableIpAddresses;
        
        public HtmlDataTable getDataTableIpAddresses() {
            return dataTableIpAddresses;
        }
        
        public void setDataTableIpAddresses(HtmlDataTable dataTableIpAddresses) {
            this.dataTableIpAddresses = dataTableIpAddresses;
        }
        
        private HtmlDataTable dataTableUserNames;
        
        public EditUserGroupService getEditUserGroupService() {
            return editUserGroupService;
        }
        
        public void setEditUserGroupService(EditUserGroupService editUserGroupService) {
            this.editUserGroupService = editUserGroupService;
        }
        
        public HtmlDataTable getDataTableUserNames() {
            return dataTableUserNames;
        }
        
        public void setDataTableUserNames(HtmlDataTable dataTableUserNames) {
            this.dataTableUserNames = dataTableUserNames;
        }
        
        private UserGroup group;
        
        /**
         * Holds value of property  userDetails.
         */
        private List<UserDetailBean>  userDetails;
        
        /**
         * Getter for property  userDetails.
         *
         * @return Value of property  userDetails.
         */
        public List<UserDetailBean> getUserDetails() {
            return this.userDetails;
        }
        
        /**
         * Setter for property  userDetails.
         *
         * @param userDetails New value of property  userDetails.
         */
        public void setUserDetails(List<UserDetailBean>  userNames) {
            this.userDetails =  userNames;
        }

        /**
         *  props and methods to populate usergrouptype radio buttons
         * and also to set the userGroupType.
         *
         * @author wbossons
         */ 
        private String userGroupType;
        
        public String getUserGroupType(){
            return this.userGroupType;
        }
        
        public void setUserGroupType(String usergrouptype) {
            this.userGroupType = usergrouptype;
            if (this.userGroupType.equals("ipgroup"))
                this.setDisplayAttributes("block");
            else
                this.setDisplayAttributes("none");
        }
        
        private String displayAttributes;
        
        public String getDisplayAttributes(){
            return this.displayAttributes;
        }
        
        public void setDisplayAttributes(String displayattributes) {
                this.displayAttributes = "display:" + displayattributes;
        }
        
        private SelectItem[] userGroupTypes = {
            new SelectItem("usergroup", "Username Group"),
            new SelectItem("ipgroup", "IP User Group")
        };

        public SelectItem[] getUserGroupTypes() {
            return userGroupTypes;
        } 
        
        public void changeUserGroupType(ValueChangeEvent event){
            String newValue = (String)event.getNewValue();
            setUserGroupType(newValue);
        } 
        
        /** methods to support the affiliated login services
         *
         * 
         * @author wbossons
         */
        private boolean chkAffiliateLoginService;
        
        public boolean isChkAffiliateLoginService() {
            return chkAffiliateLoginService;
        }
        
        public void setChkAffiliateLoginService(boolean isaffiliateloginservice) {
            this.chkAffiliateLoginService = isaffiliateloginservice;
        }
        
        /**
         * value change listener for
         * isAffiateService
         *
         * @author wbossons
         */
        public void changeChkAffiliateLoginService(ValueChangeEvent event) {
            Boolean newValue = (Boolean)event.getNewValue();
            this.setChkAffiliateLoginService(newValue.booleanValue());
        }
        
         /**
         * Holds value of property affiliateName.
         */
        private String affiliateName;

        /**
         * Getter for property affiliateName.
         * @return Value of property affiliateName.
         */
        public String getAffiliateName() {
            return this.affiliateName;
        }

        /**
         * Setter for property affiliateName.
         * @param affiliateName New value of property affiliateName.
         */
        public void setAffiliateName(String affiliateName) {
            this.affiliateName = affiliateName;
        }

        /**
         * Holds value of property affiliateURL.
         */
        private String affiliateURL;

        /**
         * Getter for property affiliateURL.
         * @return Value of property affiliateURL.
         */
        public String getAffiliateURL() {
            return this.affiliateURL;
        }

        /**
         * Setter for property affiliateURL.
         * @param affiliateURL New value of property affiliateURL.
         */
        public void setAffiliateURL(String affiliateURL) {
            this.affiliateURL = affiliateURL;
        }
        
        /** the datatable for the affilate name and url
         *
         * @author wbossons
         *
         */
        private HtmlDataTable affiliatesTable;
        
        public HtmlDataTable getAffiliatesTable() {
            return this.affiliatesTable;
        }
        
        public void setAffiliatesTable(HtmlDataTable datatable){
            datatable.setBgcolor("LightPink");
            datatable.setSummary("Table showing the affiliate information for the current ip group.");
            datatable.setCellpadding("3");
            datatable.setCellspacing("3");
            datatable.setTitle("Enter affiliate names and urls.");
            this.affiliatesTable = datatable;
        }
        
        private void initAffiliates(){
            if ( (group.getLoginAffiliates() == null || group.getLoginAffiliates().size() == 0) ) {
                LoginAffiliate loginaffiliate = new LoginAffiliate();
                UserGroup group = editUserGroupService.getUserGroup();
                loginaffiliate.setUserGroup(group);
                loginaffiliate.setName("");
                loginaffiliate.setUrl("");
                Collection affiliates = new ArrayList();
                affiliates.add(loginaffiliate);
                group.setLoginAffiliates(affiliates);
            } else if (group.getLoginAffiliates().size() == 1) {
                //if there's only 1, is it a record or just init'd
                List list = (List)group.getLoginAffiliates();
                Iterator iterator = list.iterator();
                while (iterator.hasNext()){
                    LoginAffiliate loginaffiliate = (LoginAffiliate)iterator.next();
                    if (loginaffiliate.getName() == null) {
                        this.setChkAffiliateLoginService(false);
                    } else {
                        this.setChkAffiliateLoginService(true);
                    }
                }
            } else if (group.getLoginAffiliates().size() > 1) {
                this.setChkAffiliateLoginService(true);
            }
        }
        
        public void addAffiliateRow(ActionEvent ae) {
            LoginAffiliate loginaffiliate = new LoginAffiliate();
            loginaffiliate.setUserGroup(group);
            loginaffiliate.setName("");
            loginaffiliate.setUrl("");
            group.getLoginAffiliates().add(loginaffiliate);
        }
       
        public void removeAffiliateRow(ActionEvent ae) {
            if (affiliatesTable.getRowCount() > 1) {
                List data = (List)affiliatesTable.getValue();
                this.editUserGroupService.removeCollectionElement(data, affiliatesTable.getRowData());
            } else {
                LoginAffiliate loginaffiliate = (LoginAffiliate)affiliatesTable.getRowData();
                loginaffiliate.setName("");
                loginaffiliate.setUrl("");
            }
        }
        
        /**
         * a method to validate the ip address/
         * logindomain doesn't duplicate one that is
         * already in another ip group
         *
         * @author wbossons
         */
         
        public void validateLoginDomain (FacesContext context, 
                          UIComponent toValidate,
                          Object value) {
            boolean isDuplicate = false;
            String msg = new String();
            String loginDomainStr = (String)value;
            List<UserGroup> userGroups = groupService.findAll();
            Iterator iteratorOuter = userGroups.iterator();
            while (iteratorOuter.hasNext()) {
                UserGroup elem = (UserGroup) iteratorOuter.next();
                Iterator iteratorInner = elem.getLoginDomains().iterator();
                while (iteratorInner.hasNext()){
                    LoginDomain logindomain = (LoginDomain)iteratorInner.next();
                    //this next check will match the input field against the bean value provided it's not this user group
                    if (logindomain.getIpAddress().equals(loginDomainStr) && logindomain.getUserGroup().getId() != this.group.getId()) { // this is what I want to match against
                        msg = " already exists in another group. IP user groups must be unique.";
                        ((UIInput)toValidate).setValid(false);
                        this.setUserGroupType("ipgroup");//to maintain state for the radio buttons and the group datatable
                        FacesMessage message = new FacesMessage(loginDomainStr + msg);
                        context.addMessage(toValidate.getClientId(context), message);
                        isDuplicate = true;
                        break;
                    }
                }
            }
            //finally loop through the whole list to check for duplicates.
            //if there is more than one of a configured address then throw an error and break.
            if (!isDuplicate) {
                UserGroup usergroup = editUserGroupService.getUserGroup();
                List list = (List)usergroup.getLoginDomains();
                int tablesize = this.dataTableIpAddresses.getRowCount();
                int j = this.dataTableIpAddresses.getRowIndex();
                Object[] logindomainArray = list.toArray();
                if (j > 0) {
                    LoginDomain lastInList = (LoginDomain)logindomainArray[j-1];
                    if (loginDomainStr.equals(lastInList.getIpAddress()) ) {
                         msg = " already exists in this list. IP addresses must be unique.";
                       ((UIInput)toValidate).setValid(false);
                       System.out.println("the uiinput to validate is " + toValidate.getClientId(context));
                        this.setUserGroupType("ipgroup");//to maintain state for the radio buttons and the group datatable
                        FacesMessage message = new FacesMessage(loginDomainStr + msg);
                        context.addMessage(toValidate.getClientId(context), message);
                    } 
                }
            }
        }
        
        /**
         * a method to validate the usernames
         * 
         *
         * @author wbossons
         */
        public void validateUserName(FacesContext context, 
                          UIComponent toValidate,
                          Object value) {
            String adduserString = (String)value;
            boolean invalid=false;
            if (adduserString != "" && userService.findByUserName(adduserString) == null) {
                ((UIInput)toValidate).setValid(false);
                this.setUserGroupType("usergroup");//to maintain state for the radio buttons and the group datatable
                FacesMessage message = new FacesMessage(adduserString + " is not a valid username.  Please enter a valid username.");
                context.addMessage(toValidate.getClientId(context), message);
            }
        }
}
    
