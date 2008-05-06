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
package edu.harvard.hmdc.vdcnet.web.site;

import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudyFieldServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.StringTokenizer;
import javax.ejb.EJB;
import com.sun.rave.web.ui.component.PanelLayout;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlInputText;
import com.sun.rave.web.ui.component.PanelGroup;
import edu.harvard.hmdc.vdcnet.util.CharacterValidator;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.html.HtmlCommandButton;
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
public class EditSitePage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCServiceLocal           vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB VDCGroupServiceLocal      vdcGroupService;
    @EJB VDCNetworkServiceLocal    vdcNetworkService;
    @EJB StudyFieldServiceLocal    studyFieldService;
    @EJB UserServiceLocal          userService;
    @EJB RoleServiceLocal          roleService;
    StatusMessage msg;
    
    public StatusMessage getMsg(){
        return msg;
    }
    
    public void setMsg(StatusMessage msg){
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
        //what kind of vdc is this, basic or scholar
        try {
            if ( (this.dataverseType == null || this.dataverseType.equals("Scholar")) ) {
                //set the default values for the fields
                VDC scholardataverse = (VDC)vdcService.findScholarDataverseByAlias(thisVDC.getAlias());
                setDataverseType("Scholar");
                setFirstName(scholardataverse.getFirstName());
                setLastName(scholardataverse.getLastName());
                setAffiliation(scholardataverse.getAffiliation());
                HtmlInputText nameText = new HtmlInputText();
                nameText.setValue(scholardataverse.getName());
                setDataverseName(nameText);
                HtmlInputText aliasText = new HtmlInputText();
                aliasText.setValue(scholardataverse.getAlias());
            } else if (!this.dataverseType.equals("Scholar")) {
                setDataverseType("Basic");
                HtmlInputText nameText = new HtmlInputText();
                nameText.setValue(thisVDC.getName());
                setDataverseName(nameText);
                if (thisVDC.getAffiliation() != null)
                    this.setAffiliation(thisVDC.getAffiliation());
                else
                    this.setAffiliation(new String(""));
                HtmlInputText aliasText = new HtmlInputText();
                aliasText.setValue(thisVDC.getAlias());
            }
        } catch (Exception nfe) {
            System.out.println("An error occurred " + nfe.toString());
        }
        
        
    }
    
    private Page page1 = new Page();
    
    public Page getPage1() {
        return page1;
    }
    
    public void setPage1(Page p) {
        this.page1 = p;
    }
    
    private Html html1 = new Html();
    
    public Html getHtml1() {
        return html1;
    }
    
    public void setHtml1(Html h) {
        this.html1 = h;
    }
    
    private Head head1 = new Head();
    
    public Head getHead1() {
        return head1;
    }
    
    public void setHead1(Head h) {
        this.head1 = h;
    }
    
    private Link link1 = new Link();
    
    public Link getLink1() {
        return link1;
    }
    
    public void setLink1(Link l) {
        this.link1 = l;
    }
    
    private Body body1 = new Body();
    
    public Body getBody1() {
        return body1;
    }
    
    public void setBody1(Body b) {
        this.body1 = b;
    }
    
    private Form form1 = new Form();
    
    public Form getForm1() {
        return form1;
    }
    
    public void setForm1(Form f) {
        this.form1 = f;
    }

    private PanelLayout layoutPanel1 = new PanelLayout();

    public PanelLayout getLayoutPanel1() {
        return layoutPanel1;
    }

    public void setLayoutPanel1(PanelLayout pl) {
        this.layoutPanel1 = pl;
    }

    private PanelLayout layoutPanel2 = new PanelLayout();

    public PanelLayout getLayoutPanel2() {
        return layoutPanel2;
    }

    public void setLayoutPanel2(PanelLayout pl) {
        this.layoutPanel2 = pl;
    }

    private HtmlOutputText outputText1 = new HtmlOutputText();

    public HtmlOutputText getOutputText1() {
        return outputText1;
    }

    public void setOutputText1(HtmlOutputText hot) {
        this.outputText1 = hot;
    }

    private PanelLayout layoutPanel3 = new PanelLayout();

    public PanelLayout getLayoutPanel3() {
        return layoutPanel3;
    }

    public void setLayoutPanel3(PanelLayout pl) {
        this.layoutPanel3 = pl;
    }

    private HtmlOutputText outputText2 = new HtmlOutputText();

    public HtmlOutputText getOutputText2() {
        return outputText2;
    }

    public void setOutputText2(HtmlOutputText hot) {
        this.outputText2 = hot;
    }

    private HtmlPanelGrid gridPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel1() {
        return gridPanel1;
    }

    public void setGridPanel1(HtmlPanelGrid hpg) {
        this.gridPanel1 = hpg;
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

    private HtmlPanelGrid gridPanel2 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel2() {
        return gridPanel2;
    }

    public void setGridPanel2(HtmlPanelGrid hpg) {
        this.gridPanel2 = hpg;
    }

    private PanelGroup groupPanel1 = new PanelGroup();

    public PanelGroup getGroupPanel1() {
        return groupPanel1;
    }

    public void setGroupPanel1(PanelGroup pg) {
        this.groupPanel1 = pg;
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
    
    public String edit(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        String dataversetype = dataverseType;
        thisVDC.setDtype(dataversetype);
        thisVDC.setName((String)dataverseName.getValue());
        thisVDC.setAlias((String)dataverseAlias.getValue());
        thisVDC.setAffiliation(this.getAffiliation());
        if (dataverseType.equals("Scholar")) {
            thisVDC.setFirstName(this.firstName);
            thisVDC.setLastName(this.lastName);
            if (thisVDC.getVdcGroups().size() != 0) {
                //remove the group relationships.
                Iterator iterator = thisVDC.getVdcGroups().iterator();
                while (iterator.hasNext()) {
                    VDCGroup vdcgroup = (VDCGroup)iterator.next();
                    iterator.remove();
                    vdcGroupService.updateVdcGroup(vdcgroup);
                }
                
            }
        } else {
            thisVDC.setFirstName(null);
            thisVDC.setLastName(null);
        }

        vdcService.edit(thisVDC);
        getVDCRequestBean().setCurrentVDC(thisVDC);
        msg = new StatusMessage();
        msg.setMessageText("Update Successful!");
        msg.setStyleClass("successMessage");
        return "editSite";
    }
    
    public String editScholarDataverse(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        VDC scholardataverse = (VDC)thisVDC;
        String dataversetype = dataverseType;
        scholardataverse.setDtype(dataversetype);
        scholardataverse.setName((String)dataverseName.getValue());
        scholardataverse.setAlias((String)dataverseAlias.getValue());
        scholardataverse.setFirstName(this.firstName);
        scholardataverse.setLastName(this.lastName);
        scholardataverse.setAffiliation(this.affiliation);
        vdcService.edit(scholardataverse);
        getVDCRequestBean().setCurrentVDC(scholardataverse);
        msg = new StatusMessage();
        msg.setMessageText("Update Successful!");
        msg.setStyleClass("successMessage");
        return "editSite";
    }
    
    public String cancel(){
        return "myOptions";
    }
    
    public void validateName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String name = (String) value;
        if (name != null && name.trim().length() == 0) {
            FacesMessage message = new FacesMessage("The dataverse name field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
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
        return dataverseType;
    }
    
    public void setDataverseType(String dataverseType) {
        this.dataverseType = dataverseType;
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
    private List<SelectItem> dataverseOptions = null;

    public List<SelectItem> getDataverseOptions() {
        if (this.dataverseOptions == null) {
            dataverseOptions = new ArrayList();
            dataverseOptions.add(new SelectItem(new String("Scholar")));
            dataverseOptions.add(new SelectItem(new String("Basic")));
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
        if (affiliation == null)
            this.setAffiliation(getVDCRequestBean().getCurrentVDC().getAffiliation());
        return this.affiliation;
    }

    /**
     * Setter for property affiliation.
     * @param affiliation New value of property affiliation.
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    
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
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.setAttribute("dataverseType", newValue);
        //FacesContext.getCurrentInstance().renderResponse();
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
        String newValue = (String)value;
         if (newValue == null || newValue.trim().length() == 0)  {
            FacesMessage message = new FacesMessage("The field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
}