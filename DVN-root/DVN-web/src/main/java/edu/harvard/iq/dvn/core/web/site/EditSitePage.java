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
 * EditSitePage.java
 *
 * Created on September 19, 2006, 9:57 AM
 */
package edu.harvard.iq.dvn.core.web.site;

import com.icesoft.faces.component.ext.*;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyFieldServiceLocal;
import edu.harvard.iq.dvn.core.study.Template;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.StringTokenizer;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import edu.harvard.iq.dvn.core.web.util.CharacterValidator;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCGroupServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.web.util.XhtmlValidator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
@ViewScoped
@Named("EditSitePage")
public class EditSitePage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCServiceLocal           vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB VDCGroupServiceLocal      vdcGroupService;
    @EJB VDCNetworkServiceLocal    vdcNetworkService;
    @EJB StudyFieldServiceLocal    studyFieldService;
    @EJB UserServiceLocal          userService;
    @EJB RoleServiceLocal          roleService;

    private StatusMessage   msg;

    private HtmlInputText          affiliation;
    private HtmlInputText   dataverseAlias;
    private HtmlInputText   dataverseName;
    private HtmlInputText   dataverseFirstName;
    private HtmlInputText   dataverseLastName;
    private HtmlInputTextarea shortDescriptionInput = new HtmlInputTextarea();
    private HtmlOutputText  shortDescriptionLabelText;
    private HtmlOutputLabel shortDescriptionLabel;
    private String          dataverseType = "";
    private String          firstName = new String("");
    private String          lastName;
    private HtmlInputTextarea          shortDescription = new HtmlInputTextarea();
    

    private List<SelectItem> dataverseOptions = null;


    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
    }

    public void initDVGeneralSettings(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        networkSelectItems = loadNetworkSelectItems();
        selectSubNetworkId = thisVDC.getVdcNetwork().getId();
        originalSubNetworkId = new Long (thisVDC.getVdcNetwork().getId());
        //DEBUG
        //check to see if a dataverse type is in request

        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Iterator iterator = request.getParameterMap().keySet().iterator();
        while (iterator.hasNext()) {
            Object key = (Object) iterator.next();
            if ( key instanceof String && ((String) key).indexOf("dataverseType") != -1 && !request.getParameter((String)key).equals("")) {
                this.setDataverseType(request.getParameter((String)key));
            }
        }
        if (this.dataverseType == null && getVDCRequestBean().getCurrentVDC().getDtype() != null) {
            this.setDataverseType(getVDCRequestBean().getCurrentVDC().getDtype());
        }
            if ( (this.dataverseType == null || this.dataverseType.equals("Scholar")) ) {
                //set the default values for the fields
                VDC scholardataverse = (VDC)vdcService.findScholarDataverseByAlias(thisVDC.getAlias());
                setDataverseType("Scholar");
                setFirstName(scholardataverse.getFirstName());
                setLastName(scholardataverse.getLastName());
                HtmlInputText affiliationText = new HtmlInputText();
                affiliationText.setValue(scholardataverse.getAffiliation());
                setAffiliation(affiliationText);
                HtmlInputText nameText = new HtmlInputText();
                nameText.setValue(scholardataverse.getName());
                setDataverseName(nameText);
                HtmlInputText aliasText = new HtmlInputText();
                aliasText.setValue(scholardataverse.getAlias());
                HtmlInputTextarea descriptionText = new HtmlInputTextarea();
                descriptionText.setValue(scholardataverse.getDescription());
                setShortDescription(descriptionText);
                setLocalAnnouncements(scholardataverse.getAnnouncements());
                setChkLocalAnnouncements(scholardataverse.isDisplayAnnouncements());
            } else if (!this.dataverseType.equals("Scholar")) {
                setDataverseType("Basic");
                HtmlInputText nameText = new HtmlInputText();
                nameText.setValue(thisVDC.getName());
                setDataverseName(nameText);
                HtmlInputText affiliationText = new HtmlInputText();
                affiliationText.setValue(thisVDC.getAffiliation());
                setAffiliation(affiliationText);
                HtmlInputText aliasText = new HtmlInputText();
                aliasText.setValue(thisVDC.getAlias());
                HtmlInputTextarea descriptionText = new HtmlInputTextarea();
                descriptionText.setValue(thisVDC.getDvnDescription());
                setShortDescription(descriptionText);
                setLocalAnnouncements(thisVDC.getAnnouncements());
                setChkLocalAnnouncements(thisVDC.isDisplayAnnouncements());

            }
            // initialize the select
           for (ClassificationUI classUI: classificationList.getClassificationUIs()) {
                if (classUI.getVdcGroup().getVdcs().contains(thisVDC)) {
                    classUI.setSelected(true);
                }
           }
        
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

    public HtmlInputText getDataverseName() {
        return dataverseName;
    }

    public void setDataverseName(HtmlInputText hit) {
        this.dataverseName = hit;
    }
    
    public HtmlInputText getDataverseFirstName() {
        return dataverseFirstName;
    }

    public void setDataverseFirstName(HtmlInputText hit) {
        this.dataverseFirstName = hit;
    }
    
    public HtmlInputText getDataverseLastName() {
        return dataverseLastName;
    }

    public void setDataverseLastName(HtmlInputText hit) {
        this.dataverseLastName = hit;
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

    // </editor-fold>
    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditSitePage() {
    }


    ClassificationList classificationList =  new ClassificationList();

    public ClassificationList getClassificationList() {
        return classificationList;
    }

    public void setClassificationList(ClassificationList classificationList) {
        this.classificationList = classificationList;
    }
     private void saveClassifications(VDC vdc) {
        vdc.getVdcGroups().clear();
        for (ClassificationUI classUI: classificationList.getClassificationUIs()) {
            if (classUI.isSelected()) {
                vdc.getVdcGroups().add(classUI.getVdcGroup());
            }
        }
    }
    
    private Long selectSubNetworkId;
    public Long getSelectSubNetworkId() {return selectSubNetworkId;}
    public void setSelectSubNetworkId(Long selectSubNetworkId) {this.selectSubNetworkId = selectSubNetworkId;}
    
    private Long originalSubNetworkId;
    public Long getOriginalSubNetworkId() {return originalSubNetworkId;}
    public void setOriginalSubNetworkId(Long originalSubNetworkId) {this.selectSubNetworkId = originalSubNetworkId;}
    
    /*
    public String edit(){
                    System.out.print("selectSubNetworkId "  + selectSubNetworkId);
            System.out.print("originalSubNetworkId "  + originalSubNetworkId);
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        if (validateClassificationCheckBoxes()) {
            System.out.print("in if "  + selectSubNetworkId);
            System.out.print("in if"  + originalSubNetworkId);
            System.out.print(" before thisVDC.getDefaultTemplate() "  + thisVDC.getDefaultTemplate().getName());
            //needs to be fixed here see options page
            //if(!selectSubNetworkId.equals(originalSubNetworkId)){
            //     thisVDC.setDefaultTemplate(getValidTemplate(thisVDC.getDefaultTemplate()));               
            //}
            //            System.out.print(" after thisVDC.getDefaultTemplate() "  + thisVDC.getDefaultTemplate().getName());
                       
                        
            String dataversetype = dataverseType;
            thisVDC.setDtype(dataversetype);
            thisVDC.setName((String)dataverseName.getValue());
            thisVDC.setAlias((String)dataverseAlias.getValue());
            thisVDC.setAffiliation((String)affiliation.getValue());           
            thisVDC.setDvnDescription((String)shortDescription.getValue());
            if (selectSubNetworkId > 0){
                VDCNetwork vdcNetwork = vdcNetworkService.findById(selectSubNetworkId);
                thisVDC.setVdcNetwork(vdcNetwork);
            } else {
                 thisVDC.setVdcNetwork(vdcNetworkService.findRootNetwork());
            }
            if (dataverseType.equals("Scholar")) {
                thisVDC.setFirstName(this.firstName);
                thisVDC.setLastName(this.lastName);
           
            } else {
                thisVDC.setFirstName(null);
                thisVDC.setLastName(null);
            }
            saveClassifications(thisVDC);
            vdcService.edit(thisVDC);
            getVDCRequestBean().setCurrentVDC(thisVDC);
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated general settings.");
            return "/admin/OptionsPage?faces-redirect=true"+ getVDCRequestBean().getContextSuffix();
        } else {
            return null;
        }
    }*/
    
    public boolean isValidTemplate(Template template){
        if (!template.isNetwork()){ //vdc template so return it
           return true; 
        } //check for root network
         else if(template.getVdcNetwork().equals(vdcNetworkService.findRootNetwork())){
            return true;            
        } //check for same network
         else if(template.getVdcNetwork().getId().equals(selectSubNetworkId)){
            return true;            
        } else { //get default of new subnet
            return false;            
        }        
    }
    
    public String cancel(){
        return "/admin/OptionsPage?faces-redirect=true"+getVDCRequestBean().getContextSuffix();
    }
    
 

    // ***************** GETTERS ********************
    public StatusMessage getMsg(){
        return msg;
    }

    /**
     * Returns the type of dataverse, basic or scholar in this case.
     *
     * @return
     */
    public String getDataverseType() {
        return dataverseType;
    }

    /**
     * set the possible options
     * please note, this was for 16a,
     * but is not used because of
     * issues with type casting - inheritance issues.
     * Keeping it pending a solution ...
     *
     * @author wbossons
     */
    public List<SelectItem> getDataverseOptions() {
        if (this.dataverseOptions == null) {
            dataverseOptions = new ArrayList();
            dataverseOptions.add(new SelectItem(new String("Scholar")));
            dataverseOptions.add(new SelectItem(new String("Basic")));
        }
        return dataverseOptions;
    }

    /**
     * Getter for property firstName.
     * @return Value of property firstName.
     */
    public String getFirstName() {
        return this.firstName;
    }

        /**
     * Getter for property lastName.
     * @return Value of property lastName.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Getter for property affiliation.
     * @return Value of property affiliation.
     */
    public HtmlInputText getAffiliation() {      
        return this.affiliation;
    }

    /**
     *
     *
     * @return Returns a string with the
     * short description of the dataverse.
     * It is viewable on the network page.
     *
     */
    public HtmlInputTextarea getShortDescription() {
        return shortDescription;
    }

    /**
     * Gets the HtmlInputTextarea that the short
     * description is bound to.
     *
     * @return HtmlInputTextarea
     */
    public HtmlInputTextarea getShortDescriptionInput() {
        return shortDescriptionInput;
    }

    /**
     * Gets the HtmlOutputLabel that the short
     * description label is bound to.
     *
     * @return HtmlOutputLabel
     */
    public HtmlOutputLabel getShortDescriptionLabel() {
        return shortDescriptionLabel;
    }

    /**
     * Gets the HtmlOutputText binding for the short
     * description label text.
     *
     * @return HtmlOutputText
     */
    public HtmlOutputText getShortDescriptionLabelText() {
        return shortDescriptionLabelText;
    }



    // ***************** SETTERS ********************
    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }
    public void setDataverseType(String dataverseType) {
        this.dataverseType = dataverseType;
    }

    /**
     * Setter for property firstName.
     * @param firstName New value of property firstName.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Setter for property lastName.
     * @param lastName New value of property lastName.
     */
    public void setLastName(String lastname) {
        this.lastName = lastname;
    }

    /**
     * Setter for property affiliation.
     * @param affiliation New value of property affiliation.
     */
    public void setAffiliation(HtmlInputText affiliation) {
        this.affiliation = affiliation;
    }

    /**
     * Returns the value of the short description.
     *
     * @param shortDescription
     */
    public void setShortDescription(HtmlInputTextarea shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Sets the HtmlInputTextarea that the short
     * description is bound to.
     *
     * @return void
     */
    public void setShortDescriptionInput(HtmlInputTextarea shortDescriptionInput) {
        this.shortDescriptionInput = shortDescriptionInput;
    }

    /**
     * Sets the HtmlOutputLabel binding for the short
     * description label.
     *
     * @return void
     */
    public void setShortDescriptionLabel(HtmlOutputLabel shortDescriptionLabel) {
        this.shortDescriptionLabel = shortDescriptionLabel;
    }

    /**
     * Sets the HtmlOutputText binding for the short
     * description label text.
     *
     * @return void
     */
    public void setShortDescriptionLabelText(HtmlOutputText shortDescriptionLabelText) {
        this.shortDescriptionLabelText = shortDescriptionLabelText;
    }

  

    /**
     * Captures the selected option.
     *
     */
    public void changeDataverseOption(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setDataverseType(newValue);  
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.setAttribute("dataverseType", newValue);
        //FacesContext.getCurrentInstance().renderResponse();
    }

    /**
     * Captures changes to the first name.
     *
     */
    public void changeFirstName(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setFirstName(newValue);        
    }

    /**
     * Captures changes to the last name.
     *
     */
    public void changeLastName(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setLastName(newValue);        
    }
    
        
     private List<SelectItem> loadNetworkSelectItems() {
        List selectItems = new ArrayList<SelectItem>();
        List <VDCNetwork> networkList = vdcNetworkService.getVDCSubNetworks();
        if (networkList.size() > 0){
            selectItems.add(new SelectItem(0, "<None>"));
            for (VDCNetwork vdcNetwork : networkList){
                selectItems.add(new SelectItem(vdcNetwork.getId(), vdcNetwork.getName()));
            }
        }
        return selectItems;
    }

    private List <SelectItem> networkSelectItems = new ArrayList();

    public List<SelectItem> getNetworkSelectItems() {
        return this.networkSelectItems;
    }
    
    private HtmlSelectOneMenu selectSubnetwork;   
    public HtmlSelectOneMenu getSelectSubnetwork() {return selectSubnetwork;}
    public void setSelectSubnetwork(HtmlSelectOneMenu selectSubnetwork) {this.selectSubnetwork = selectSubnetwork;}

    private boolean chkLocalAnnouncements;    
    public boolean isChkLocalAnnouncements() {return chkLocalAnnouncements;}    
    public void setChkLocalAnnouncements(boolean chkLocalAnnouncements) {this.chkLocalAnnouncements = chkLocalAnnouncements;}
    
    private String localAnnouncements;    
    public String getLocalAnnouncements() {return localAnnouncements;}   
    public void setLocalAnnouncements(String localAnnouncements) {this.localAnnouncements = localAnnouncements;}
    
    private HtmlInputTextarea localAnnouncementsInputText;
    public HtmlInputTextarea getLocalAnnouncementsInputText() {return this.localAnnouncementsInputText;}
    public void setLocalAnnouncementsInputText(HtmlInputTextarea localAnnouncementsInputText) {this.localAnnouncementsInputText = localAnnouncementsInputText;}

    // this is a hidden field that is used as a proxy for setting the classification checkboxes error message
    private HtmlInputHidden classificationHidden;
    public HtmlInputHidden getClassificationHidden() {return classificationHidden;}
    public void setClassificationHidden(HtmlInputHidden classificationHidden) {this.classificationHidden = classificationHidden;}
    
    /**
     * Validation so that a required field is not left empty
     *
     * @param context
     * @param toValidate
     * @param value
     */
    public void validateIsEmpty(FacesContext context, UIComponent toValidate, Object value) {
        String newValue = (String)value;
         if (newValue == null || newValue.trim().length() == 0)  {
            FacesMessage message = new FacesMessage("This field is required.");
            context.addMessage(toValidate.getClientId(context), message);
            ((UIInput)toValidate).setValid(false);
        }
    }

    // wrapper method around validate that can be called from the save method
    public boolean isValidName() {   
        validateName(FacesContext.getCurrentInstance(),dataverseName,dataverseName.getValue());
        return ((UIInput)dataverseName).isValid();
    }
  
    
    public void validateName(FacesContext context, UIComponent toValidate, Object value) {
        validateIsEmpty(context, toValidate, value);
        
        String name = (String) value;
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        if (!name.equals(thisVDC.getName())){
            boolean nameFound = false;
            VDC vdc = vdcService.findByName(name);
            if (vdc != null) {
                nameFound=true;
            }
            
            if (nameFound) {
                ((UIInput)toValidate).setValid(false);
                
                FacesMessage message = new FacesMessage("This name is already taken.");
                context.addMessage(toValidate.getClientId(context), message);
            }
        }
    }
    
    // wrapper method around validate that can be called from the save method
    public boolean isValidAlias() {   
        validateAlias(FacesContext.getCurrentInstance(),dataverseAlias,dataverseAlias.getValue());
        return ((UIInput)dataverseAlias).isValid();
    }
      

    public void validateAlias(FacesContext context, UIComponent toValidate, Object value) {
        validateIsEmpty(context, toValidate, value);      
        
        String alias = (String) value;        
        CharacterValidator charactervalidator = new CharacterValidator();
        charactervalidator.validate(context, toValidate, value);
        StringTokenizer strTok = new StringTokenizer(alias);
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
            if (!alias.equals(thisVDC.getAlias())){
                boolean aliasFound = false;
                VDC vdc = vdcService.findByAlias(alias);
                if (vdc != null) {
                    aliasFound=true;
                }
                
                if (aliasFound) {
                    ((UIInput)toValidate).setValid(false);
                    FacesMessage message = new FacesMessage("This alias is already taken.");
                    context.addMessage(toValidate.getClientId(context), message);
                }
            }
    }

        
      // wrapper method around validate that can be called from the save method
    public boolean isValidShortDescription() {   
        validateShortDescription(FacesContext.getCurrentInstance(),shortDescription,shortDescription.getValue());
        return ((UIInput)shortDescription).isValid();
    }
    
    public void validateShortDescription(FacesContext context, UIComponent toValidate, Object value) {
        if (getVDCRequestBean().getVdcNetwork().isRequireDVdescription() ) {
            validateIsEmpty(context, toValidate, value);
        }  
                
        String newValue = (String)value;
        if (newValue != null && newValue.trim().length() > 0) {
            if (newValue.length() > 255) {
                ((UIInput)toValidate).setValid(false);
                FacesMessage message = new FacesMessage("The field cannot be more than 255 characters in length.");
                context.addMessage(toValidate.getClientId(context), message);
            }
        }
    }
    
    // wrapper method around validate that can be called from the save method
    public boolean isValidAffiliation() {   
        validateAffiliation(FacesContext.getCurrentInstance(),affiliation,affiliation.getValue());
        return ((UIInput)affiliation).isValid();
    }
    
    public void validateAffiliation(FacesContext context, UIComponent toValidate, Object value) {
        if (getVDCRequestBean().getVdcNetwork().isRequireDVaffiliation() ) {
            validateIsEmpty(context, toValidate, value);
        }  
    }
    
    public boolean isValidScholarName() {
        validateIsEmpty(FacesContext.getCurrentInstance(), dataverseFirstName, dataverseFirstName.getValue());
        validateIsEmpty(FacesContext.getCurrentInstance(), dataverseLastName, dataverseLastName.getValue());
        
        return  dataverseFirstName.isValid() && dataverseLastName.isValid();
    } 
    
    

   // If we need to use the JSF validator pattern, we should add a validate mehod for this and have this method call that  
    public boolean isValidDescription() {
        if (isChkLocalAnnouncements() && (localAnnouncements == null || localAnnouncements.equals(""))) {
            localAnnouncementsInputText.setValid(false);
            FacesMessage message = new FacesMessage("To enable announcements, you must also enter announcements in the field below.  Please enter local announcements as either plain text or html.");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(localAnnouncementsInputText.getClientId(context), message);
        } else {
            XhtmlValidator xhtmlValidator = new XhtmlValidator();
            xhtmlValidator.validate(FacesContext.getCurrentInstance(), localAnnouncementsInputText, localAnnouncements);
        }     

        return localAnnouncementsInputText.isValid();
    }
   
    // If we need to use the JSF validator pattern, we should add a validate mehod for this and have this method call that    
    public boolean isValidClassificationCheckBoxes() {
        if (!getVDCRequestBean().getVdcNetwork().isRequireDVclassification()){
            return true;
        } else {
            for (ClassificationUI classUI: classificationList.getClassificationUIs()) {
                if (classUI.isSelected()) {
                    return true;
                }
            }
            classificationHidden.setValid(false);
            FacesMessage message = new FacesMessage("You must select at least one classification for your dataverse.");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(classificationHidden.getClientId(context), message);            
            return false;
        }

    }
}