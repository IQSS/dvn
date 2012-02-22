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
 * EditSitePage.java
 *
 * Created on September 19, 2006, 9:57 AM
 */
package edu.harvard.iq.dvn.core.web.site;

import com.icesoft.faces.component.ext.HtmlCommandButton;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyFieldServiceLocal;
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
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.component.ext.HtmlOutputText;
import edu.harvard.iq.dvn.core.web.util.CharacterValidator;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCGroup;
import edu.harvard.iq.dvn.core.vdc.VDCGroupServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
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
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
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
   
    public String edit(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        boolean success = true;
        if (validateClassificationCheckBoxes()) {
            String dataversetype = dataverseType;
            thisVDC.setDtype(dataversetype);
            thisVDC.setName((String)dataverseName.getValue());
            thisVDC.setAlias((String)dataverseAlias.getValue());
            thisVDC.setAffiliation((String)affiliation.getValue());           
            thisVDC.setDvnDescription((String)shortDescription.getValue());
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
            return "/admin/OptionsPage?faces-redirect=true&vdcId="+thisVDC.getId();
        } else {
            success = false;
            return null;
        }
    }
    
   
    
    public String cancel(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        return "/admin/OptionsPage?faces-redirect=true&vdcId="+thisVDC.getId();
    }
    
    public void validateName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String name = (String) value;
        if (name != null && name.trim().length() == 0) {
            FacesMessage message = new FacesMessage("The dataverse name field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
            ((UIInput)toValidate).setValid(false);
        }
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

    public void validateAlias(FacesContext context,
            UIComponent toValidate,
            Object value) {
        CharacterValidator charactervalidator = new CharacterValidator();
        charactervalidator.validate(context, toValidate, value);
        String alias = (String) value;
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
        if (affiliation == null)
        {
            HtmlInputText affiliationText = new HtmlInputText();
            affiliationText.setValue(getVDCRequestBean().getCurrentVDC().getAffiliation());
            this.setAffiliation(affiliationText);
        }
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

    /**
     * Validation so that a required field is not left empty
     *
     * @param context
     * @param toValidate
     * @param value
     */
    public void validateIsEmpty(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String newValue = (String)value;
         if (newValue == null || newValue.trim().length() == 0)  {
            FacesMessage message = new FacesMessage("The field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
            ((UIInput)toValidate).setValid(false);
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
                ((UIInput)toValidate).setValid(false);
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
                ((UIInput)toValidate).setValid(false);
                context.renderResponse();
            }
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
            FacesContext.getCurrentInstance().addMessage("editsiteform", message);
            return false;
        }

    }
}