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
 * AddSitePage.java
 *
 * Created on September 19, 2006, 9:57 AM
 */
package edu.harvard.hmdc.vdcnet.web.site;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudyFieldServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlMessages;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.util.CharacterValidator;
import edu.harvard.hmdc.vdcnet.util.PropertyUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.web.DataverseGrouping;
import edu.harvard.hmdc.vdcnet.web.login.LoginWorkflowBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class AddSitePage extends VDCBaseBean implements java.io.Serializable  {

       // </editor-fold>
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public AddSitePage() {

    }

    @EJB
    VDCServiceLocal vdcService;
    @EJB
    VDCGroupServiceLocal vdcGroupService;
    @EJB
    VDCCollectionServiceLocal vdcCollectionService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @EJB
    StudyFieldServiceLocal studyFieldService;
    @EJB
    UserServiceLocal userService;
    @EJB
    RoleServiceLocal roleService;
    @EJB
    MailServiceLocal mailService;
    StatusMessage msg;
    
    //private BundleReader messagebundle = new BundleReader("Bundle");
    
    
    private ResourceBundle messagebundle = ResourceBundle.getBundle("Bundle");

    public StatusMessage getMsg() {
        return msg;
    }

    public void setMsg(StatusMessage msg) {
        this.msg = msg;
    }
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        //check to see if a dataverse type is in request
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Iterator iterator = request.getParameterMap().keySet().iterator();
        while (iterator.hasNext()) {
            Object key = (Object) iterator.next();
            if (key instanceof String && ((String) key).indexOf("dataverseType") != -1 && !request.getParameter((String) key).equals("")) {
                this.setDataverseType(request.getParameter((String) key));
            }
        }
        if (groupItems == null) {
            groupItems = new ArrayList<SelectItem>();
            List list = (List) vdcGroupService.findAll();
            iterator = list.iterator();
            groupItems.add(new SelectItem(0, "None"));
            while (iterator.hasNext()) {
                VDCGroup vdcgroup = (VDCGroup) iterator.next();
                groupItems.add(new SelectItem(vdcgroup.getId(), vdcgroup.getName()));
            }
            setGroupItems(groupItems);
        }
         synchronized(itemBeans) {
            if (itemBeans != null) {
                itemBeans.clear();
            }
         }
            List list = (List)vdcGroupService.findAll();
            Iterator outeriterator = list.iterator();
             VDCGroup vdcgroup = null;
             System.out.println(list.toString());
             while(outeriterator.hasNext()) {
                itemBeansSize++;
                vdcgroup = (VDCGroup)outeriterator.next();
                if (removeFromList.contains(vdcgroup)) {
                    continue;
                } else {
                String indentStyle = (vdcgroup.getParent() == null) ? "" : "childRowIndentStyle";
                populateParentClassification(vdcgroup, indentStyle);
                }
             }
             List myList = itemBeans;
             Iterator myListiterator = myList.iterator();
             synchronized(myList) {
                 while (myListiterator.hasNext()) {
                     DataverseGrouping grouping = (DataverseGrouping)myListiterator.next();
                     grouping.toggleSubGroupAction();
                 }
             }
    }

    //copied from manageclassificationsPage.java
    private boolean result;
     private String statusMessage;
     private String SUCCESS_MESSAGE   = new String("Success. The classifications and dataverses operation completed successfully.");
     private String FAIL_MESSAGE      = new String("Problems occurred during the form submission. Please see error messages below.");
     private HtmlMessages      iceMessage = new HtmlMessages();

     //fields from dvrecordsmanager
    private DataverseGrouping parentItem = null;
    private DataverseGrouping childItem  = null;
    private final ArrayList itemBeans = new ArrayList();
    private boolean isInit;
    private int itemBeansSize = 0; //used to output the number of classifications
    public static final String GROUP_INDENT_STYLE_CLASS = "GROUP_INDENT_STYLE_CLASS";
    public static final String GROUP_ROW_STYLE_CLASS = "groupRow";
    public static final String CHILD_INDENT_STYLE_CLASS = "CHILD_INDENT_STYLE_CLASS";
    public String CHILD_ROW_STYLE_CLASS;
    public static final String CONTRACT_IMAGE = "tree_nav_top_close_no_siblings.gif";
    public static final String EXPAND_IMAGE = "tree_nav_top_open_no_siblings.gif";

    //these static variables have a dependency on the Network Stats Server e.g.
    // they should be held as constants in a constants file ... TODO
    private static Long   SCHOLAR_ID                    = new Long("-1");
    private static String   SCHOLAR_SHORT_DESCRIPTION   = new String("A short description for the research scholar group");
    private static Long   OTHER_ID                      = new Long("-2");
    private static String   OTHER_SHORT_DESCRIPTION     = new String("A short description for the unclassified dataverses group (other).");
    List removeFromList = new ArrayList();

     //Manage classification
     private void populateParentClassification(VDCGroup vdcgroup, String indentStyle) {
         Long parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("-1");
         //System.out.println("dv records manager: parent in group is " + vdcgroup.getParent());
         List list = vdcGroupService.findByParentId(vdcgroup.getId());
         Iterator iterator = list.iterator();
         String expandImage = null;
         String contractImage = null;
         boolean isExpanded   = false;
         if (iterator.hasNext()) {
            expandImage   = EXPAND_IMAGE;
            contractImage = CONTRACT_IMAGE;
            isExpanded    = true;
         }
         synchronized(itemBeans) {
            parentItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", itemBeans, isExpanded, expandImage, contractImage, parent);
         }
         parentItem.setShortDescription(vdcgroup.getDescription());
         parentItem.setSubclassification(new Long(list.size()));
         if (!indentStyle.equals(""))
             parentItem.setIndentStyleClass(indentStyle);
         List innerlist = vdcGroupService.findByParentId(vdcgroup.getId());//get the children
         Iterator inneriterator = innerlist.iterator();
         System.out.println("populateParent: " + vdcgroup.getName());
         removeFromList.add(vdcgroup);
         while(inneriterator.hasNext()) {
            VDCGroup subgroup = (VDCGroup)inneriterator.next();
            parent = vdcgroup.getParent();
            populateSubClassification(subgroup, parentItem);
                //remove the subgroup from the iterator
            removeFromList.add(subgroup);
         }
     }

     private void populateSubClassification(VDCGroup vdcgroup, DataverseGrouping parentitem) {

         List list = vdcGroupService.findByParentId(vdcgroup.getId());//get the children
         Iterator iterator = list.iterator();
         String expandImage = null;
         String contractImage = null;
         boolean isExpanded   = false;
         if (!list.isEmpty()) {
             expandImage    = EXPAND_IMAGE;
             contractImage  = CONTRACT_IMAGE;
             isExpanded     = true;
         }
         childItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "subgroup", isExpanded, expandImage, contractImage, new Long(parentitem.getId()));
         childItem.setShortDescription(vdcgroup.getDescription());
         childItem.setIndentStyleClass("childRowIndentStyle");
         parentitem.addChildItem(childItem);
         //parentitem.toggleSubGroupAction();
         System.out.println("Created a child whose name is " + vdcgroup.getName());
         removeFromList.add(vdcgroup);
        while (iterator.hasNext() && !removeFromList.contains(vdcgroup)) {
             VDCGroup subgroup = (VDCGroup)iterator.next();
             populateSubClassification(subgroup, childItem);

         }

     }


    public HtmlMessages getIceMessage() {
        return this.iceMessage;
    }

     public boolean getResult() {
        return result;
    }
     //setters

     public void setIceMessage(HtmlMessages icemessage) {
        iceMessage.setStyleClass("successMessage");
        this.iceMessage = icemessage;
    }

     public void setResult(boolean result) {
        this.result = result;
    }

    public void dispose() {
        isInit = false;
        if(itemBeans != null) {
            DataverseGrouping dataversegrouping;
            ArrayList tempList;
            for(int i = 0; i < itemBeans.size(); i++) {
                dataversegrouping = (DataverseGrouping)itemBeans.get(i);
                tempList = dataversegrouping.getChildItems();
                if(tempList != null)
                    tempList.clear();
            }

            itemBeans.clear();
        }
    }

    public ArrayList getItemBeans() {
        return itemBeans;
    }

    public int getItemBeansSize() {
        return itemBeansSize;
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



    private HtmlOutputLabel componentLabel1 = new HtmlOutputLabel();

    public HtmlOutputLabel getComponentLabel1() {
        return componentLabel1;
    }

    public void setComponentLabel1(HtmlOutputLabel hol) {
        this.componentLabel1 = hol;
    }
    private HtmlOutputText componentLabel1Text = new HtmlOutputText();

    public HtmlOutputText getComponentLabel1Text() {
        return componentLabel1Text;
    }

    public void setComponentLabel1Text(HtmlOutputText hot) {
        this.componentLabel1Text = hot;
    }
    private HtmlInputText dataverseName = new HtmlInputText();

    public HtmlInputText getDataverseName() {
        return dataverseName;
    }

    public void setDataverseName(HtmlInputText hit) {
        this.dataverseName = hit;
    }
    private HtmlOutputLabel componentLabel2 = new HtmlOutputLabel();

    public HtmlOutputLabel getComponentLabel2() {
        return componentLabel2;
    }

    public void setComponentLabel2(HtmlOutputLabel hol) {
        this.componentLabel2 = hol;
    }
    private HtmlOutputText componentLabel2Text = new HtmlOutputText();

    public HtmlOutputText getComponentLabel2Text() {
        return componentLabel2Text;
    }

    public void setComponentLabel2Text(HtmlOutputText hot) {
        this.componentLabel2Text = hot;
    }
    private HtmlInputText dataverseAlias = new HtmlInputText();

    public HtmlInputText getDataverseAlias() {
        return dataverseAlias;
    }

    public void setDataverseAlias(HtmlInputText hit) {
        this.dataverseAlias = hit;
    }

    HtmlSelectBooleanCheckbox selectClassification = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getSelectClassification() {
        return selectClassification;
    }

    public void setSelectClassification(HtmlSelectBooleanCheckbox selectClassification) {
        this.selectClassification = selectClassification;
    }

    

    private HtmlCommandButton button1 = new HtmlCommandButton();

    public HtmlCommandButton getButton1() {
        return button1;
    }

    public void setButton1(HtmlCommandButton hcb) {
        this.button1 = hcb;
    }
    private HtmlCommandButton button2 = new HtmlCommandButton();

    public HtmlCommandButton getButton2() {
        return button2;
    }

    public void setButton2(HtmlCommandButton hcb) {
        this.button2 = hcb;
    }

 
    public String create() {

        Long selectedgroup  = this.getSelectedGroup();
        String dtype        = dataverseType;
        String name         = (String) dataverseName.getValue();
        String alias        = (String) dataverseAlias.getValue();
        Long userId = getVDCSessionBean().getLoginBean().getUser().getId();
        vdcService.create(userId, name, alias, dtype);
        Iterator itemIterator = itemBeans.iterator();
        while (itemIterator.hasNext()) {
            DataverseGrouping grouping = (DataverseGrouping)itemIterator.next();
            selectedGroup = new Long(grouping.getId());
            if (selectedGroup != null && selectedGroup > 0 && grouping.getClassificationSelect() == true) {
                //the following method requires a string array
                VDCGroup vdcgroup   = vdcGroupService.findById(selectedGroup);
                List list           = vdcgroup.getVdcs();
                Iterator iterator   = list.iterator();
                int i               = 0;
                String[] stringArray = new String[(list.size() + 1)];
                while (iterator.hasNext()) {
                    VDC vdc = (VDC) iterator.next();
                    stringArray[i] = vdc.getId().toString();
                    i++;
                }
                if (!iterator.hasNext()) {
                    stringArray[i] = vdcService.findByAlias(alias).getId().toString();
                }
                vdcGroupService.updateWithVdcs(vdcGroupService.findById(selectedGroup), stringArray);
            }
        }
        VDC createdVDC = vdcService.findByAlias(alias);
        createdVDC.setDtype(dataverseType);
        createdVDC.setDisplayNetworkAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayAnnouncements());
        createdVDC.setDisplayAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayVDCAnnouncements());
        createdVDC.setAnnouncements(getVDCRequestBean().getVdcNetwork().getDefaultVDCAnnouncements());
        createdVDC.setDisplayNewStudies(getVDCRequestBean().getVdcNetwork().isDisplayVDCRecentStudies());
        createdVDC.setAboutThisDataverse(getVDCRequestBean().getVdcNetwork().getDefaultVDCAboutText());
        createdVDC.setContactEmail(getVDCSessionBean().getLoginBean().getUser().getEmail());      
        createdVDC.setAffiliation(affiliation);
        createdVDC.setDvnDescription(shortDescription);
        vdcService.edit(createdVDC);
        getVDCRequestBean().setCurrentVDC(createdVDC);

        // Refresh User object in LoginBean so it contains the user's new role of VDC administrator.
        getVDCRequestBean().getCurrentVDCURL();
        StatusMessage msg = new StatusMessage();

        String hostUrl = PropertyUtil.getHostUrl();
        msg.setMessageText("Your new dataverse <a href='http://" + hostUrl + "/dvn" + getVDCRequestBean().getCurrentVDCURL()+ "'>http://" + hostUrl + "/dvn" + getVDCRequestBean().getCurrentVDCURL()+ "</a> has been successfully created!");
        msg.setStyleClass("successMessage");
        Map m = getRequestMap();
        m.put("statusMessage", msg);
        VDCUser creator = userService.findByUserName(getVDCSessionBean().getLoginBean().getUser().getUserName());
        String toMailAddress = getVDCSessionBean().getLoginBean().getUser().getEmail();
        String siteAddress = "unknown";

        siteAddress = hostUrl + "/dvn" + getVDCRequestBean().getCurrentVDCURL();

        mailService.sendAddSiteNotification(toMailAddress, name, siteAddress);

        getVDCSessionBean().getLoginBean().setUser(creator);

        return "addSiteSuccess";        
    }

    public String createScholarDataverse() {
        String dataversetype = dataverseType;
     
        String name = (String) dataverseName.getValue();
        String alias = (String) dataverseAlias.getValue();
        Long userId = getVDCSessionBean().getLoginBean().getUser().getId();
        vdcService.createScholarDataverse(userId, firstName, lastName, name, affiliation, alias, dataversetype);
        Iterator itemIterator = itemBeans.iterator();
        while (itemIterator.hasNext()) {
            DataverseGrouping grouping = (DataverseGrouping)itemIterator.next();
            selectedGroup = new Long(grouping.getId());
            if (selectedGroup != null && selectedGroup > 0 && grouping.getClassificationSelect() == true) {
                //the following method requires a string array
                VDCGroup vdcgroup   = vdcGroupService.findById(selectedGroup);
                List list           = vdcgroup.getVdcs();
                Iterator iterator   = list.iterator();
                int i               = 0;
                String[] stringArray = new String[(list.size() + 1)];
                while (iterator.hasNext()) {
                    VDC vdc = (VDC) iterator.next();
                    stringArray[i] = vdc.getId().toString();
                    i++;
                }
                if (!iterator.hasNext()) {
                    stringArray[i] = vdcService.findByAlias(alias).getId().toString();
                }
                vdcGroupService.updateWithVdcs(vdcGroupService.findById(selectedGroup), stringArray);
            }

        }
        VDC createdScholarDataverse = vdcService.findScholarDataverseByAlias(alias);
        //  add default values to the VDC table and commit/set the vdc bean props
        createdScholarDataverse.setDisplayNetworkAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayAnnouncements());
        createdScholarDataverse.setDisplayAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayVDCAnnouncements());
        createdScholarDataverse.setAnnouncements(getVDCRequestBean().getVdcNetwork().getDefaultVDCAnnouncements());
        createdScholarDataverse.setDisplayNewStudies(getVDCRequestBean().getVdcNetwork().isDisplayVDCRecentStudies());
        createdScholarDataverse.setAboutThisDataverse(getVDCRequestBean().getVdcNetwork().getDefaultVDCAboutText());
        createdScholarDataverse.setContactEmail(getVDCSessionBean().getLoginBean().getUser().getEmail());
        createdScholarDataverse.setDvnDescription(shortDescription);
        vdcService.edit(createdScholarDataverse);
        getVDCRequestBean().setCurrentVDC(createdScholarDataverse);
          // Refresh User object in LoginBean so it contains the user's new role of VDC administrator.
    
        StatusMessage msg = new StatusMessage();

        String hostUrl = PropertyUtil.getHostUrl();

        msg.setMessageText("Your new scholar dataverse <a href='http://" + hostUrl + "/dvn" + getVDCRequestBean().getCurrentVDCURL()+ "'>http://" + hostUrl + "/dvn" + getVDCRequestBean().getCurrentVDCURL()+ "</a> has been successfully created!");

        msg.setStyleClass("successMessage");
        Map m = getRequestMap();
        m.put("statusMessage", msg);
        VDCUser creator = userService.findByUserName(getVDCSessionBean().getLoginBean().getUser().getUserName());
        String toMailAddress = getVDCSessionBean().getLoginBean().getUser().getEmail();
        String siteAddress = "unknown";

        siteAddress = hostUrl + "/dvn" + getVDCRequestBean().getCurrentVDCURL();

        mailService.sendAddSiteNotification(toMailAddress, name, siteAddress);

        getVDCSessionBean().getLoginBean().setUser(creator);

        return "addSiteSuccess";
    }
    
    public String cancel() {
        return "cancel";
    }
    
    public void validateShortDescription(FacesContext context,
                UIComponent toValidate,
                Object value) {
            String newValue = (String)value;
            if (newValue != null && newValue.trim().length() > 0) {
                if (newValue.length() > 255) {
                    ((UIInput)toValidate).setValid(false);
                    FacesMessage message = new FacesMessage("The field cannot be more than 255 characters in length.");
                    context.addMessage(toValidate.getClientId(context), message);
                }
            }
        }

    public void validateName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String name = (String) value;
        if (name != null && name.trim().length() == 0) {
            FacesMessage message = new FacesMessage("The dataverse name field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
            context.renderResponse();
        }
        boolean nameFound = false;
        VDC vdc = vdcService.findByName(name);
        if (vdc != null) {
            nameFound = true;
        }
        if (nameFound) {
            ((UIInput) toValidate).setValid(false);

            FacesMessage message = new FacesMessage("This name is already taken.");
            context.addMessage(toValidate.getClientId(context), message);
        }

        resetScholarProperties();
    }

    public void validateAlias(FacesContext context,
            UIComponent toValidate,
            Object value) {
        CharacterValidator charactervalidator = new CharacterValidator();
        charactervalidator.validate(context, toValidate, value);
        String alias = (String) value;

        boolean isValid = false;
        VDC vdc = vdcService.findByAlias(alias);
        if (alias.equals("") || vdc != null) {
            isValid = true;
        }

        if (isValid) {
            ((UIInput) toValidate).setValid(false);

            FacesMessage message = new FacesMessage("This alias is already taken.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        resetScholarProperties();
    }

    private void resetScholarProperties() {
        if (dataverseType != null) {
            this.setDataverseType(dataverseType);
        }
    }
    /**
     * Changes for build 16
     * to support scholar
     * dataverses and display
     *
     * @author wbossons
     */
    /**
     * Used to set the discriminator value
     * in the entity
     *
     */
    private String dataverseType = null;

    public String getDataverseType() {
        if (dataverseType == null) {
            setDataverseType("Basic");
        }
        return dataverseType;
    }

    public void setDataverseType(String dataverseType) {
        this.dataverseType = dataverseType;
    }
    /**
     * Used to set the discriminator value
     * in the entity
     *
     */
    private String selected = null;

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
    /**
     * set the possible options
     *
     *
     */

    private List<SelectItem> dataverseOptions = null;

    public List getDataverseOptions() {
        if (this.dataverseOptions == null) {
            dataverseOptions = new ArrayList();
            /**
             * Choose Scholar if this dataverse will have your 
             * own name and will contain your own research, 
             * and Basic for any other dataverse.
             * 
             * 
               Select the group that will most likely fit your 
             * dataverse, be it a university department, a journal, 
             * a research center, etc. If you create a Scholar dataverse, 
             * it will be automatically entered under the Scholar group.        
             * 
             */
            try {
                String scholarOption       = messagebundle.getString("scholarOption");
                String basicOption          = messagebundle.getString("basicOption");
                String scholarLabel        = messagebundle.getString("scholarOptionDetail");
                String basicLabel          = messagebundle.getString("basicOptionDetail");
                dataverseOptions.add(new SelectItem(basicOption, basicLabel));
                dataverseOptions.add(new SelectItem(scholarOption, scholarLabel));
            } catch (Exception uee) {
                System.out.println("Exception:  " + uee.toString());
            }
        }
        return dataverseOptions;
    }
    /**
     * Holds value of property firstName.
     */
    private String firstName = new String("");

    /**
     * Getter for property firstName.
     * @return Value of property firstName.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Setter for property firstName.
     * @param firstName New value of property firstName.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Holds value of property lastName.
     */
    private String lastName;

    /**
     * Getter for property lastName.
     * @return Value of property lastName.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Setter for property lastName.
     * @param lastName New value of property lastName.
     */
    public void setLastName(String lastname) {
        this.lastName = lastname;
    }
    /**
     * Holds value of property affiliation.
     */
    private String affiliation;

    /**
     * Getter for property affiliation.
     * @return Value of property affiliation.
     */
    public String getAffiliation() {
        return this.affiliation;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Setter for property affiliation.
     * @param affiliation New value of property affiliation.
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    String shortDescription;
    /**
     *
     * Add group select
     *
     */
    private List<SelectItem> groupItems;

    public List<SelectItem> getGroupItems() {
        return this.groupItems;
    }

    public void setGroupItems(List<SelectItem> groupitems) {
        this.groupItems = groupitems;
    }
    /**
     *
     * Add group select
     *
     */
    private Long selectedGroup;

    public Long getSelectedGroup() {
        if (selectedGroup == null) {
            selectedGroup = new Long("0"); // This will be the default selected item.
        }
        return selectedGroup;
    }

    public void setSelectedGroup(Long selectedGroup) {
        this.selectedGroup = selectedGroup;
    }
    private HtmlSelectOneMenu groupMenu;

    public HtmlSelectOneMenu getGroupMenu() {
        return this.groupMenu;
    }

    public void setGroupMenu(HtmlSelectOneMenu groupmenu) {
        this.groupMenu = groupmenu;
    }

    public void changeSelectedGroup(ValueChangeEvent event) {
        Long groupId = (Long) event.getNewValue();
        setSelectedGroup(groupId);
    }

    //END Group Select widgets
    /**
     * value change listeners and validators
     *
     *
     */
    /**
     * capture value change event
     *
     */
    public void changeAffiliation(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setAffiliation(newValue);
    }

    public void changeDataverseOption(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setDataverseType(newValue);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.setAttribute("dataverseType", newValue);
        FacesContext.getCurrentInstance().renderResponse();
    }

    public void changeFirstName(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setFirstName(newValue);
    }

    public void changeLastName(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setLastName(newValue);
    }

    public void validateIsEmpty(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String newValue = (String) value;
        if (newValue == null || newValue.trim().length() == 0) {
            FacesMessage message = new FacesMessage("The field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
            context.renderResponse();
        }
    }
}

