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
 * EditUserGroupPage.java
 *
 * Created on October 20, 2006, 4:36 PM
 *
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import edu.harvard.iq.dvn.core.admin.EditUserGroupService;
import edu.harvard.iq.dvn.core.admin.GroupServiceLocal;
import edu.harvard.iq.dvn.core.admin.LoginAffiliate;
import edu.harvard.iq.dvn.core.admin.LoginDomain;
import edu.harvard.iq.dvn.core.admin.UserDetailBean;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */

@ViewScoped
@Named("EditUserGroupPage")
public class EditUserGroupPage extends VDCBaseBean implements java.io.Serializable  {
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

        if (userGroupId == null) {
            editUserGroupService.newUserGroup();
            group = editUserGroupService.getUserGroup();
        
        } else {
            editUserGroupService.setUserGroup(userGroupId);
            group = editUserGroupService.getUserGroup();
            userGroupType = (group.getLoginDomains() == null || group.getLoginDomains().size() == 0) ? "usergroup" : "ipgroup";
        }
       
        userDetails = editUserGroupService.getUserDetailBeans();
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
           return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true";
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
                
             /*   if (logindomain.getIpAddress() != null && logindomain.getIpAddress().equals("")){
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    this.setUserGroupType("ipgroup");//to maintain state for the radio buttons and the group datatable
                    FacesMessage message = new FacesMessage("There must be at least one domain listed in order to save an ip group.");
                 //   facesContext.addMessage(htmldatatable.getClientId(facesContext) + ":iptable", message);
                    return true;
                } */
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
                                    if ( loginaffiliate.getName() == null || loginaffiliate.getName().equals("") || this.chkAffiliateLoginService == false  )
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
            else if (this.userGroupType.equals("ipgroup")) {
                userDetails.clear();
                editUserGroupService.save();
                return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true";
            } else {
                editUserGroupService.removeLoginDomains();
                editUserGroupService.setUserDetailBeans(userDetails);
                editUserGroupService.save();
                return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true";
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
                loginDomain.setIpAddress(""); 
                group.getLoginDomains().add(loginDomain);
            }
        }
        
        public void removeIpRow(ActionEvent ae) {
            HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();
            if (dataTable == this.dataTableIpAddresses) {
                // we are removing a row from ipaddress list
                if (dataTable.getRowCount() > 1) {
                    List data         = (List)dataTable.getValue();
                    Iterator iterator = this.group.getLoginDomains().iterator();
                    String ipaddress  = ((LoginDomain)dataTable.getRowData()).getIpAddress();
                    while (iterator.hasNext()) {
                        LoginDomain logindomain = (LoginDomain)iterator.next();
                        if (logindomain.getIpAddress().equals(ipaddress)) {
                            this.editUserGroupService.removeCollectionElement(this.group.getLoginDomains(), logindomain);
                            dataTable.setValue(this.group.getLoginDomains());
                            break;
                        }
                    }
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

    protected HtmlInputText inputLoginDomains = new HtmlInputText();

    /**
     * Get the value of inputLoginDomains
     *
     * @return the value of inputLoginDomains
     */
    public HtmlInputText getInputLoginDomains() {
        return inputLoginDomains;
    }

    /**
     * Set the value of inputLoginDomains
     *
     * @param inputLoginDomains new value of inputLoginDomains
     */
    public void setInputLoginDomains(HtmlInputText inputLoginDomains) {
        this.inputLoginDomains = inputLoginDomains;
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
                Iterator iterator = this.group.getLoginAffiliates().iterator();
                String affiliatename = ((LoginAffiliate)affiliatesTable.getRowData()).getName();
                String affiliateurl = ((LoginAffiliate)affiliatesTable.getRowData()).getUrl();
                while (iterator.hasNext()) {
                    LoginAffiliate loginaffiliate = (LoginAffiliate)iterator.next();
                    if (loginaffiliate.getName().equals(affiliatename) && loginaffiliate.getUrl().equals(affiliateurl)) {
                        this.editUserGroupService.removeCollectionElement(this.group.getLoginAffiliates(), loginaffiliate);
                        affiliatesTable.setValue(this.group.getLoginAffiliates());
                        break;
                    }
                }
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
            FacesMessage message = null;
            if (loginDomainStr == null || loginDomainStr.trim().equals("")) {
                   ((UIInput)toValidate).setValid(false);
                   msg = "IP Address is a required field.";
            } else {
                List<UserGroup> userGroups = groupService.findAll();
                Iterator iteratorOuter = userGroups.iterator();
                while (iteratorOuter.hasNext()) {
                    UserGroup usergroup          = (UserGroup) iteratorOuter.next();
                    Iterator iteratorInner  = usergroup.getLoginDomains().iterator();
                    while (iteratorInner.hasNext()){
                        LoginDomain logindomain = (LoginDomain)iteratorInner.next();
                        String existingAddress = getRange(logindomain.getIpAddress());
                        String newAddress      = getRange(loginDomainStr);
                        if (logindomain.getUserGroup().getId().equals(this.group.getId()) ) {
                            continue;
                        } else {
                            if (existingAddress.equals(newAddress)) { 
                                ((UIInput)toValidate).setValid(false);
                                msg = loginDomainStr + " already exists in another group. IP user groups must be unique.";
                                ((UIInput)toValidate).setValid(false);
                                break;
                            } else {
                                if (!isNewRangeValid(existingAddress, newAddress)) {
                                        ((UIInput)toValidate).setValid(false);
                                        msg = "Entry overlaps with a range found in another ip group, " +
                                                logindomain.getUserGroup().getFriendlyName() +
                                                ". Ip addresses and/or ip address ranges must be unique to each group.";
                                        break;
                                 }
                            }
                        }
                    }
                }
            }
            //finally loop through the whole list to check for duplicates.
            //if there is more than one of a configured address then throw an error and break.
            if (((UIInput)toValidate).isValid()) {
                UserGroup usergroup = editUserGroupService.getUserGroup();
                List list = (List)usergroup.getLoginDomains();
                Iterator listIterator = list.iterator();
                System.out.println("the row index is " + dataTableIpAddresses.getRowIndex() + " the count is " + dataTableIpAddresses.getRowCount());
                if (dataTableIpAddresses.getRowIndex() == dataTableIpAddresses.getRowCount()-1) {
                    for (int j = 1; j < dataTableIpAddresses.getRowCount(); j++) {
                        LoginDomain logindomain = (LoginDomain)list.get(j-1);
                        String existingAddress = getRange(logindomain.getIpAddress());
                        String newAddress      = getRange(loginDomainStr);
                        if (existingAddress.equals(newAddress)) {
                                    ((UIInput)toValidate).setValid(false);
                                    msg = loginDomainStr + " already exists in this group. IP user groups must be unique.";
                                    ((UIInput)toValidate).setValid(false);
                                    break;
                        } else {
                            if (!isNewRangeValid(existingAddress, newAddress)) {
                                    ((UIInput)toValidate).setValid(false);
                                    msg = "Entry overlaps with a range already found in this group, " +
                                            logindomain.getUserGroup().getFriendlyName() +
                                            ". Ip addresses and/or ip address ranges must be unique within each group.";
                                    break;
                             }
                        }
                    }
                }
            } //end if toValidate.isValid() ...
            if (!((UIInput)toValidate).isValid()) {
                message = new FacesMessage(msg);
                context.addMessage(toValidate.getClientId(context), message);
            }
             this.setUserGroupType("ipgroup");//to maintain state for the radio buttons and the group datatable
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

        public void changeAffiliateTable(ValueChangeEvent event) {
            LoginAffiliate loginaffiliate = (LoginAffiliate)affiliatesTable.getRowData();
            String elementName = (String)event.getComponent().getClientId(FacesContext.getCurrentInstance());
            if (elementName.indexOf("affiliateName") != -1)
                loginaffiliate.setName((String)event.getNewValue());
            else
                loginaffiliate.setUrl((String)event.getNewValue());
        }

        public void changeIpTable(ValueChangeEvent event) {
            LoginDomain logindomain = (LoginDomain)this.dataTableIpAddresses.getRowData();
            logindomain.setIpAddress((String)event.getNewValue());
        }

        protected String getRange(String ipaddress) {
            String addressRange    = new String(""); // make this the new range
            if (ipaddress.indexOf("*") == -1) {
                if (ipaddress.indexOf("-") != -1) {
                    return ipaddress;
                } else {
                    addressRange = ipaddress + "-" + ipaddress;
                }
            } else {
                InternetAddressValidator validAddress = new InternetAddressValidator();
                addressRange  = validAddress.getNumericFromWildcardedIp(ipaddress.trim());
            }
            return addressRange;
        }

        // utils
        public boolean isNewRangeValid(String existingIpRange, String newIpRange) {
            String[] newSubnets       = existingIpRange.split("-");
            InternetAddressValidator validAddress = new InternetAddressValidator();
            String strRangeBegin   = validAddress.toBinaryFormat(validAddress.getNumericFromWildcard(newSubnets[0].trim(), true));
            validAddress.lowRange  = validAddress.makeMeDecimal(strRangeBegin);
            String strRangeEnd     = validAddress.toBinaryFormat(validAddress.getNumericFromWildcard(newSubnets[1].trim(), false));
            validAddress.highRange = validAddress.makeMeDecimal(strRangeEnd);
            String[] existingRange = newIpRange.split("-");
            boolean isValid        = true;
            String strInOut        = null;
            int i                  = 0;
            for (i = 0; i < existingRange.length; i++) {
                strInOut        = validAddress.toBinaryFormat(existingRange[i].trim());
                validAddress.clientIp  = validAddress.makeMeDecimal(strInOut);
                if ( (validAddress.lowRange <= validAddress.clientIp && validAddress.clientIp <= validAddress.highRange) ||
                        (validAddress.lowRange == 0.0 && validAddress.highRange == 0.0) || (validAddress.lowRange > validAddress.highRange) ) {
                    isValid = false;
                    break;
                } 
            } // end for loop
            return isValid;
    }

         /**
         *
         * @author wbossons
         */
        public static class InternetAddressValidator {

            private double lowRange   = 0;
            private double highRange  = 0;
            private double clientIp   = 0;
            private boolean inRange   = false;


            InternetAddressValidator() {
                // default constructor
            }

          private char ipClass (byte[] ip) {
            int highByte = 0xff & ip[0];
            return (highByte < 128) ? 'A' : (highByte < 192) ? 'B' :
              (highByte < 224) ? 'C' : (highByte < 240) ? 'D' : 'E';
          }

          /** printRemoteAddress
           * A simple utility to show some information about the machine
           *
           * @param name
           */
          private void printRemoteAddress (String name) {
            try {
              System.out.println ("Looking up " + name + "...");
              InetAddress machine = InetAddress.getByName (name);
              System.out.println ("Host name : " + machine.getHostName ());
              System.out.println ("Host IP : " + machine.getHostAddress ());
              System.out.println ("Host class : " + ipClass(machine.getAddress()));
            } catch (UnknownHostException ex) {
              System.out.println ("Failed to lookup " + name);
            }
          }

          int base = 10;
          private String toBinaryFormat(String ipaddress) {
              String binaryIpAddress    = new String("");
              String[] stringArray      = ipaddress.split("\\.");
              String[] binaryArray      = new String[stringArray.length];
              String localString        = new String("");
              int decimalValue          = 0;
              for (int i = 0; i < stringArray.length; i++) {
                  int networkPart = Integer.parseInt(stringArray[i]);
                  localString = Integer.toBinaryString(networkPart);
                  // add any zeroes needed to make each octet 8 bits
                  if (localString.length() < 8) {
                      StringBuffer buffer = new StringBuffer(localString);
                      buffer = buffer.reverse();
                      String reverseString = buffer.toString();
                      for (int j = 0; j < 8 - localString.length(); j++) {
                          reverseString += "0";
                      }
                      buffer = new StringBuffer(reverseString);
                      buffer = buffer.reverse();
                      localString = buffer.toString();
                  }
                  binaryIpAddress = binaryIpAddress + localString;
              }
              return binaryIpAddress;
          }


          private double makeMeDecimal(String binaryString) {
              int strLength = binaryString.length();
              double j = 0;
              double result = j;
              int charIndex = 0;
              // do the binary math
              int pos = 0;
              for (int i = (strLength - 1); i >= 0; i--) {
                  j = Math.pow(2, i);
              // find either 1 or 0
              Character binarychar = binaryString.charAt(pos);
              pos++;
              result += Math.abs(j * Integer.parseInt(binarychar.toString()));
              }
              return result;
          }

          /** return an ip range from a wildcarded single ip
           *
           * @param subnetAddress
           * @return
           */
          private String getNumericFromWildcardedIp(String subnetAddress) {
              int firstOctet   = 0;
                int secondOctet  = 0;
                int thirdOctet   = 0;
                int fourthOctet  = 0;
                String startRange = new String("");
                String endRange   = new String("");
                    String[] subnets = subnetAddress.split("\\.");
                    for (int i = 0; i < 4; i++) {
                        // System.out.println("The octet is " + subnets[i]);
                       if (i == 0) {
                            startRange += Integer.valueOf(subnets[i].substring(0, subnets[i].length()));
                            endRange   += Integer.valueOf(subnets[i].substring(0, subnets[i].length()));
                       } else  {
                           if ( (i >= subnets.length) || (subnets[i].indexOf("*") != -1) ) {
                            startRange +=  ".0";
                            endRange   +=  ".255";
                            } else {
                                startRange += "." + Integer.parseInt(subnets[i]);
                                endRange   += "." + Integer.parseInt(subnets[i]);
                           }
                       }

                    }
                    return startRange + " - " + endRange;

                }
              

          private String getNumericFromWildcard(String subnetAddress, boolean isStartRange) {
                String ipaddress = new String("");
                if (subnetAddress.indexOf("*") == -1) {
                    return subnetAddress;
                } else {
                    String[] subnets = subnetAddress.split("\\.");
                    for (int i = 0; i < 4; i++) {
                        // System.out.println("The octet is " + subnets[i]);
                       if (i == 0) {
                            ipaddress += Integer.valueOf(subnets[i].substring(0, subnets[i].length()));
                       } else  {
                           if ( (i >= subnets.length) || (subnets[i].indexOf("*") != -1) ) {
                                ipaddress += (isStartRange) ? ".0" : ".255";
                            } else {
                                ipaddress += "." + Integer.parseInt(subnets[i]);
                           }
                       }
                    }
                }
              return ipaddress;
          }

            public void setHighRange(double highRange) {
                this.highRange = highRange;
            }

            public void setClientIp(double clientIp) {
                this.clientIp = clientIp;
            }

            public void setLowRange(double lowRange) {
                this.lowRange = lowRange;
            }

            public boolean isInRange() {
                return inRange;
            }

        }
}
    
