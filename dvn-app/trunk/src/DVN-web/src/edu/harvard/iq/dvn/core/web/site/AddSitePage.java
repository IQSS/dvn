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
package edu.harvard.iq.dvn.core.web.site;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyFieldServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCGroupServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.web.util.CharacterValidator;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@Named("AddSitePage")
@ViewScoped
public class AddSitePage extends VDCBaseBean implements java.io.Serializable  {

       // </editor-fold>
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.web.site.AddSitePage");
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
      
    }

    //copied from manageclassificationsPage.java
    private boolean result;
 
     //fields from dvrecordsmanager
    private ArrayList itemBeans = new ArrayList();
    private static String GROUP_INDENT_STYLE_CLASS = "GROUP_INDENT_STYLE_CLASS";
    private static String GROUP_ROW_STYLE_CLASS = "groupRow";
    private static String CHILD_INDENT_STYLE_CLASS = "CHILD_INDENT_STYLE_CLASS";
    private String CHILD_ROW_STYLE_CLASS;
    private static String CONTRACT_IMAGE = "tree_nav_top_close_no_siblings.gif";
    private static String EXPAND_IMAGE = "tree_nav_top_open_no_siblings.gif";

    //these static variables have a dependency on the Network Stats Server e.g.
    // they should be held as constants in a constants file ... TODO
    private static Long   SCHOLAR_ID                    = new Long("-1");
    private static String   SCHOLAR_SHORT_DESCRIPTION   = new String("A short description for the research scholar group");
    private static Long   OTHER_ID                      = new Long("-2");
    private static String   OTHER_SHORT_DESCRIPTION     = new String("A short description for the unclassified dataverses group (other).");
    
     //Manage classification
    
   


  
     public boolean getResult() {
        return result;
    }
     //setters

  

     public void setResult(boolean result) {
        this.result = result;
    }

 

    public ArrayList getItemBeans() {
        return itemBeans;
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

    // I'm initializing classificationList below in order to get the page 
    // to work; otherwise (if it's set to null), the page dies quietly in 
    // classificationList.getClassificationUIs() (in saveClassifications), 
    // after creating the new DV. 
    // I'm still not quite sure how/where this list was initialized before?
    ClassificationList classificationList = new ClassificationList();//null;

    public ClassificationList getClassificationList() {
        return classificationList;
    }

    public void setClassificationList(ClassificationList classificationList) {
        this.classificationList = classificationList;
    }

   

    public String create() {

    //    Long selectedgroup  = this.getSelectedGroup();
        String dtype        = dataverseType;
        String name         = (String) dataverseName.getValue();
        String alias        = (String) dataverseAlias.getValue();
        String strAffiliation = (String) affiliation.getValue();
        String strShortDescription = (String) shortDescription.getValue();
        Long userId = getVDCSessionBean().getLoginBean().getUser().getId();



        boolean success = true;
        if (validateClassificationCheckBoxes()) {
            vdcService.create(userId, name, alias, dtype);
            VDC createdVDC = vdcService.findByAlias(alias);
            saveClassifications(createdVDC);
            createdVDC.setDtype(dataverseType);
            createdVDC.setDisplayNetworkAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayAnnouncements());
            createdVDC.setDisplayAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayVDCAnnouncements());
            createdVDC.setAnnouncements(getVDCRequestBean().getVdcNetwork().getDefaultVDCAnnouncements());
            createdVDC.setDisplayNewStudies(getVDCRequestBean().getVdcNetwork().isDisplayVDCRecentStudies());
            createdVDC.setAboutThisDataverse(getVDCRequestBean().getVdcNetwork().getDefaultVDCAboutText());
            createdVDC.setContactEmail(getVDCSessionBean().getLoginBean().getUser().getEmail());
            createdVDC.setAffiliation(strAffiliation);
            createdVDC.setDvnDescription(strShortDescription);
            vdcService.edit(createdVDC);

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
            
            logger.fine("created dataverse; site address: "+siteAddress);

            mailService.sendAddSiteNotification(toMailAddress, name, siteAddress);

            // Refresh User object in LoginBean so it contains the user's new role of VDC administrator.
            getVDCSessionBean().getLoginBean().setUser(creator);
 
            return "/site/AddSiteSuccess?faces-redirect=true&vdcId=" + createdVDC.getId();
        }
        else {
            success = false;
            return null;
        }

    }

    private void saveClassifications(VDC createdVDC) {
        for (ClassificationUI classUI: classificationList.getClassificationUIs()) {
            if (classUI.isSelected()) {
                createdVDC.getVdcGroups().add(classUI.getVdcGroup());
            }
        }
    }

    public String createScholarDataverse() {
        String dataversetype = dataverseType;
     
        String name = (String) dataverseName.getValue();
        String alias = (String) dataverseAlias.getValue();
        String strAffiliation = (String) affiliation.getValue();
        String strShortDescription = (String) shortDescription.getValue();
        Long userId = getVDCSessionBean().getLoginBean().getUser().getId();

        if (validateClassificationCheckBoxes()) {
            vdcService.createScholarDataverse(userId, firstName, lastName, name, strAffiliation, alias, dataversetype);
            VDC createdScholarDataverse = vdcService.findScholarDataverseByAlias(alias);
            saveClassifications(createdScholarDataverse);
  
            //  add default values to the VDC table and commit/set the vdc bean props
            createdScholarDataverse.setDisplayNetworkAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayAnnouncements());
            createdScholarDataverse.setDisplayAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayVDCAnnouncements());
            createdScholarDataverse.setAnnouncements(getVDCRequestBean().getVdcNetwork().getDefaultVDCAnnouncements());
            createdScholarDataverse.setDisplayNewStudies(getVDCRequestBean().getVdcNetwork().isDisplayVDCRecentStudies());
            createdScholarDataverse.setAboutThisDataverse(getVDCRequestBean().getVdcNetwork().getDefaultVDCAboutText());
            createdScholarDataverse.setContactEmail(getVDCSessionBean().getLoginBean().getUser().getEmail());
            createdScholarDataverse.setDvnDescription(strShortDescription);
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
        else {
            return null;
        }

    }
    
    public String cancel() {
        return "cancel";
    }



    public boolean validateClassificationCheckBoxes() {

        if (!getVDCRequestBean().getVdcNetwork().isRequireDVclassification()){
            return true;
        }
        else {
            for (ClassificationUI classUI: classificationList.getClassificationUIs()) {
                if (classUI.isSelected()) {
                    return true;
                }
            }

            FacesMessage message = new FacesMessage("You must select at least one classification for your dataverse.");
            FacesContext.getCurrentInstance().addMessage("addsiteform", message);
            return false;
        }

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
            if ((newValue == null || newValue.trim().length() == 0) && getVDCRequestBean().getVdcNetwork().isRequireDVdescription()) {
                FacesMessage message = new FacesMessage("The field must have a value.");
                context.addMessage(toValidate.getClientId(context), message);
                ((UIInput) toValidate).setValid(false);
                context.renderResponse();
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
    private HtmlInputText affiliation;

    /**
     * Getter for property affiliation.
     * @return Value of property affiliation.
     */
    public HtmlInputText getAffiliation() {
        return this.affiliation;
    }

    public HtmlInputTextarea getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(HtmlInputTextarea shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Setter for property affiliation.
     * @param affiliation New value of property affiliation.
     */
    public void setAffiliation(HtmlInputText affiliation) {
        this.affiliation = affiliation;
    }

    HtmlInputTextarea shortDescription;

    //END Group Select widgets
    /**
     * value change listeners and validators
     *
     *
     */


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
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("The field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
            context.renderResponse();
        }
    }
    public void validateIsEmptyRequiredAffiliation(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String newValue = (String) value;
        if ((newValue == null || newValue.trim().length() == 0) && getVDCRequestBean().getVdcNetwork().isRequireDVaffiliation()) {
                FacesMessage message = new FacesMessage("The field must have a value.");
                context.addMessage(toValidate.getClientId(context), message);
                context.renderResponse();
                ((UIInput) toValidate).setValid(false);
            }
    }

}

