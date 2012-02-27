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
 * StudyPage.java
 *
 * Created on September 5, 2006, 4:25 PM
 */
package edu.harvard.iq.dvn.core.web.study;



import edu.harvard.iq.dvn.core.study.EditTemplateService;
import edu.harvard.iq.dvn.core.study.StudyAbstract;
import edu.harvard.iq.dvn.core.study.StudyAuthor;
import edu.harvard.iq.dvn.core.study.StudyDistributor;
import edu.harvard.iq.dvn.core.study.StudyGeoBounding;
import edu.harvard.iq.dvn.core.study.StudyGrant;
import edu.harvard.iq.dvn.core.study.StudyKeyword;
import edu.harvard.iq.dvn.core.study.StudyNote;
import edu.harvard.iq.dvn.core.study.StudyOtherId;
import edu.harvard.iq.dvn.core.study.StudyOtherRef;
import edu.harvard.iq.dvn.core.study.StudyProducer;
import edu.harvard.iq.dvn.core.study.StudyRelMaterial;
import edu.harvard.iq.dvn.core.study.StudyRelPublication;
import edu.harvard.iq.dvn.core.study.StudyRelStudy;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudySoftware;
import edu.harvard.iq.dvn.core.study.StudyTopicClass;
import edu.harvard.iq.dvn.core.study.Template;
import edu.harvard.iq.dvn.core.study.TemplateField;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.study.StudyFieldConstant;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputHidden;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.icesoft.faces.component.ext.HtmlSelectManyListbox;
import com.icesoft.faces.component.panelseries.PanelSeries;
import com.icesoft.faces.context.effects.JavascriptContext;
import edu.harvard.iq.dvn.core.study.FieldInputLevel;
import edu.harvard.iq.dvn.core.study.StudyField;

import edu.harvard.iq.dvn.core.study.StudyFieldValue;
import edu.harvard.iq.dvn.core.study.TemplateServiceLocal;
import java.util.Collections;
import java.util.Comparator;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("TemplateFormPage")
public class TemplateFormPage extends VDCBaseBean implements java.io.Serializable  {
    @Inject EditTemplateService editTemplateService;
    @EJB StudyServiceLocal studyService;
    @EJB TemplateServiceLocal templateService;

    
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public TemplateFormPage() {
        
    }
    
   public void preRenderView() {
       super.preRenderView();
       // add javascript call on each partial submit to initialize the help tips for added fields
       JavascriptContext.addJavascriptCall(getFacesContext(),"initInlineHelpTip();");
   }     
    
   public void init() {
       System.out.println("Start of init.....");       
       super.init();
       Long vdcId = new Long(0);
       try {
           Context ctx = new InitialContext();
           /*editTemplateService = (EditTemplateService) ctx.lookup("java:comp/env/editTemplate");*/
       } catch (NamingException e) {
           e.printStackTrace();
           FacesContext context = FacesContext.getCurrentInstance();
           FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
           context.addMessage(null, errMessage);

       }
        
       if (getTemplateId() != null) {
              
           editTemplateService.setTemplate(templateId);
           template = editTemplateService.getTemplate();
           
           if (getVDCRequestBean().getCurrentVDC() != null){
               vdcId = getVDCRequestBean().getCurrentVDC().getId();
           }
           if (vdcId > 0 && template.isNetwork()){

               editTemplateService.newClonedTemplate(vdcId, template);
               template = editTemplateService.getTemplate(); 
           }
       } else {
           if (getVDCRequestBean().getCurrentVDC() != null){
               vdcId = getVDCRequestBean().getCurrentVDC().getId();
           }
           
           if (studyVersionId != null) {
               editTemplateService.newTemplate(vdcId, studyVersionId);
           } else if(!vdcId.equals(new Long(0))) {               
               editTemplateService.newTemplate(vdcId);
           } else {
               editTemplateService.newNetworkTemplate();
           }   
           
           template = editTemplateService.getTemplate();         
       }
       initStudyMap();
       initAdHocFieldMap();

       fieldTypeSelectItems = loadFieldTypeSelectItems();

       //sek - needed to set template to get the init to work....
       // - moved to Template contructor
       //template.getMetadata().setTemplate(template);
       template.getMetadata().initCollections(); 
       
       if (vdcId > 0){
          networkEdit = false;
       } else {
          networkEdit = true;
       }
       
       fieldInputLevelSelectItems = loadFieldInputLevelSelectItems();
    }
    
   
   
    
    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
        System.out.println("in preprocess");
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
        System.out.println("in prerender");
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
     * Holds value of property template.getMetadata().
     */
    private Template template;

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
    
    private boolean networkEdit;

    public boolean isNetworkEdit() {
        return networkEdit;
    }

    public void setNetworkEdit(boolean networkEdit) {
        this.networkEdit = networkEdit;
    }
    
        
    
    private Map studyMap;
    
    public Map getStudyMap() {
        return studyMap;
    }
    
    public void initStudyMap() {
        studyMap = new HashMap();
        for (Iterator<TemplateField> it =template.getTemplateFields().iterator(); it.hasNext();) {
            TemplateField tf = it.next();
            StudyMapValue smv = new StudyMapValue();
            smv.setTemplateFieldUI(new TemplateFieldUI(tf));
            if(!tf.getStudyField().isDcmField()){
                studyMap.put(tf.getStudyField().getName(),smv);              
            }           
        } 
    }
    
    
    public Map getTemplatesMap() {
        return templateService.getVdcTemplatesMap(getVDCRequestBean().getCurrentVDCId());
    }
    
    private List<TemplateField> adHocFields;
    
    private void initAdHocFieldMap(){
        Long defaultdcmOrder = new Long(1);
        if (adHocFields == null) {
           adHocFields = new ArrayList();
            for (Iterator<TemplateField> it =template.getTemplateFields().iterator(); it.hasNext();) {
                TemplateField tf = it.next();
                StudyMapValue smv = new StudyMapValue();
                smv.setTemplateFieldUI(new TemplateFieldUI(tf));
                if(tf.getStudyField().isDcmField()){
                    /*
                    if (tf.getTemplateFieldValues().isEmpty()){
                        tf.initValues();
                    } else {
                        ArrayList <TemplateFieldValue> matchingValues = new ArrayList<TemplateFieldValue>();
                        for (TemplateFieldValue tfv: tf.getTemplateFieldValues()){
                            if (tfv.getMetadata().equals(template.getMetadata())){
                                   matchingValues.add(tfv);
                            }

                        }                        
                        tf.setTemplateFieldValues(matchingValues);
                    }
                    if (tf.getTemplateFieldValues().isEmpty()){
                        tf.initValues();
                    }*/
                        ArrayList <TemplateFieldControlledVocabulary> matchingControlledVocab = new ArrayList<TemplateFieldControlledVocabulary>();
                        for (TemplateFieldControlledVocabulary tfv: tf.getTemplateFieldControlledVocabulary()){

                                   matchingControlledVocab.add(tfv);
                       

                        }                        
                        tf.setTemplateFieldControlledVocabulary(matchingControlledVocab);
                    
                    adHocFields.add(tf);
                }
            }

            for (Object o : adHocFields){
                TemplateField c1 = (TemplateField) o;
                
                if (c1.getDcmSortOrder() == null){
                    c1.setdcmSortOrder(defaultdcmOrder);
                    defaultdcmOrder++;
                }
            }
        }

        Collections.sort(adHocFields, comparator);
        
        //after sort close up any gaps that may have been introduced
        defaultdcmOrder = new Long(1);
        
            for (Object o : adHocFields){
                TemplateField c1 = (TemplateField) o;
                    c1.setdcmSortOrder(defaultdcmOrder);
                    defaultdcmOrder++;
            }
        
    }

    Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                TemplateField c1 = (TemplateField) o1;
                TemplateField c2 = (TemplateField) o2;
                return c1.getDcmSortOrder().compareTo(c2.getDcmSortOrder());

            }
    };
    
    public List<TemplateField> getAdHocFields() {
        return adHocFields;
    }
    
    public List<SelectItem> loadFieldInputLevelSelectItems() {
        List selectItems = new ArrayList<SelectItem>();
        
        FieldInputLevel rec = new FieldInputLevel();
        rec.setName("recommended");
        FieldInputLevel hidden = new FieldInputLevel();
        hidden.setName("hidden");
        FieldInputLevel optional = new FieldInputLevel();
        optional.setName("optional");
        FieldInputLevel required = new FieldInputLevel();
        required.setName("required");
        FieldInputLevel disabled = new FieldInputLevel();
        disabled.setName("disabled");       

        selectItems.add(new SelectItem("required", "Required"));
        selectItems.add(new SelectItem("recommended", "Recommended"));
        selectItems.add(new SelectItem("optional", "Optional"));
        selectItems.add(new SelectItem("hidden", "Hidden"));
        if(networkEdit){
           selectItems.add(new SelectItem("disabled", "Disabled")); 
        }

        return selectItems;
    }
    
    private List <SelectItem> fieldInputLevelSelectItems = new ArrayList();
    
    public List<SelectItem> getFieldInputLevelSelectItems() {

        return this.fieldInputLevelSelectItems;
    }

    public List<SelectItem> loadFieldTypeSelectItems() {
        List selectItems = new ArrayList<SelectItem>();

        selectItems.add(new SelectItem("textBox", "Text Box"));
        selectItems.add(new SelectItem("textArea", "Text Area"));
        selectItems.add(new SelectItem("date", "Date"));
        selectItems.add(new SelectItem("html", "Allow HTML"));
        return selectItems;
    }

    private List <SelectItem> fieldTypeSelectItems = new ArrayList();

    public List<SelectItem> getFieldTypeSelectItems() {

        return this.fieldTypeSelectItems;
    }

       
    public void addRow(ActionEvent ae) {
        
        //      UIComponent dataTable = ae.getComponent().getParent().getParent().getParent();
        HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();
        
        if (dataTable.equals(dataTableOtherIds)) {
            StudyOtherId newElem = new StudyOtherId();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyOtherIds().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableAuthors)) {
            StudyAuthor newElem = new StudyAuthor();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyAuthors().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableAbstracts)) {
            StudyAbstract newElem = new StudyAbstract();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyAbstracts().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableDistributors)) {
            StudyDistributor newElem = new StudyDistributor();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyDistributors().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableGrants)) {
            StudyGrant newElem = new StudyGrant();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyGrants().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableKeywords)) {
            StudyKeyword newElem = new StudyKeyword();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyKeywords().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableNotes)) {
            StudyNote newElem = new StudyNote();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyNotes().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableProducers)) {
            StudyProducer newElem = new StudyProducer();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyProducers().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableSoftware)) {
            StudySoftware newElem = new StudySoftware();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudySoftware().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableTopicClass)) {
            StudyTopicClass newElem = new StudyTopicClass();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyTopicClasses().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(this.dataTableGeoBoundings)) {
            StudyGeoBounding newElem = new StudyGeoBounding();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyGeoBoundings().add(dataTable.getRowIndex()+1,newElem);
        }  else  if (dataTable.equals(this.dataTableRelPublications)) {
            StudyRelPublication newElem = new StudyRelPublication();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyRelPublications().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(this.dataTableRelMaterials)) {
            StudyRelMaterial newElem = new StudyRelMaterial();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyRelMaterials().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(this.dataTableRelStudies)) {
            StudyRelStudy newElem = new StudyRelStudy();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyRelStudies().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(this.dataTableOtherReferences)) {
            StudyOtherRef newElem = new StudyOtherRef();
            newElem.setMetadata(template.getMetadata());
            template.getMetadata().getStudyOtherRefs().add(dataTable.getRowIndex()+1,newElem);
        }       
    }
    
    public void removeRow(ActionEvent ae) {
        
        HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();
        if (dataTable.getRowCount()>1) {
            List data = (List)dataTable.getValue();
            editTemplateService.removeCollectionElement(data,dataTable.getRowData());
        }
    }

    public void removeAdHocField(ActionEvent ae) {
        // first set the rowindex of the data model to the row index of the panel series
        // TODO: (until we cache the DataModel, we have to do this)
        DataModel fieldsDataModel = getCustomFieldsDataModel();
        fieldsDataModel.setRowIndex(customFieldsPanelSeries.getRowIndex());

        Object[] fieldsRowData = (Object[]) fieldsDataModel.getRowData();
        TemplateField removeTF = (TemplateField) fieldsRowData[0];
        editTemplateService.removeCollectionElement(template.getTemplateFields(), removeTF);

        adHocFields.remove(removeTF);

        List valuesWrappedData = (List) ((ListDataModel) fieldsRowData[1]).getWrappedData();
        for (Object elem : valuesWrappedData) {
            Object[] valuesRowData = (Object[]) elem;
            editTemplateService.removeCollectionElement((List) valuesRowData[1], valuesRowData[0]);
        }
    }
    
    /**
     * Holds value of property dataTableOtherIds.
     */
    private HtmlDataTable dataTableOtherIds;
    
    /**
     * Getter for property dataTableOtherIds.
     * @return Value of property dataTableOtherIds.
     */
    public HtmlDataTable getDataTableOtherIds() {
        return this.dataTableOtherIds;
    }
    
    /**
     * Setter for property dataTableOtherIds.
     * @param dataTableOtherIds New value of property dataTableOtherIds.
     */
    public void setDataTableOtherIds(HtmlDataTable dataTableOtherIds) {
        this.dataTableOtherIds = dataTableOtherIds;
    }
    
        /**
     * Holds value of property dataTableOtherIds.
     */
    private HtmlDataTable dataTableDCMFieldValues;
    
    /**
     * Getter for property dataTableOtherIds.
     * @return Value of property dataTableOtherIds.
     */
    public HtmlDataTable getDataTableDCMFieldValues() {
        return this.dataTableDCMFieldValues;
    }
    
    /**
     * Setter for property dataTableOtherIds.
     * @param dataTableOtherIds New value of property dataTableOtherIds.
     */
    public void setDataTableDCMFieldValues(HtmlDataTable dataTableDCMFieldValues) {
        this.dataTableDCMFieldValues = dataTableDCMFieldValues;
    }
    
    private void removeEmptyRows() {
        // Remove empty collection rows
        
        // StudyAuthor
        for (Iterator<StudyAuthor> it = template.getMetadata().getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor elem =  it.next();
            if (elem.isEmpty()) {
                  editTemplateService.removeCollectionElement(it,elem);
            }
        }
        
        // StudyAbstract
        for (Iterator<StudyAbstract> it = template.getMetadata().getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem =  it.next();
            if (elem.isEmpty()) {
                   editTemplateService.removeCollectionElement(it,elem);
            }
        }
        
        // StudyDistributor
        for (Iterator<StudyDistributor> it = template.getMetadata().getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor elem =  it.next();
            if (elem.isEmpty()) {
                   editTemplateService.removeCollectionElement(it,elem);
           }
        }
        
        // StudyGrant
        for (Iterator<StudyGrant> it = template.getMetadata().getStudyGrants().iterator(); it.hasNext();) {
            StudyGrant elem =  it.next();
            if (elem.isEmpty()) {
                    editTemplateService.removeCollectionElement(it,elem);
           }
        }
        // StudyGeobounding
        for (Iterator<StudyGeoBounding> it = template.getMetadata().getStudyGeoBoundings().iterator(); it.hasNext();) {
            StudyGeoBounding elem =  it.next();
            if (elem.isEmpty()) {
                  editTemplateService.removeCollectionElement(it,elem);
            }
        }
        // StudyKeyword
        for (Iterator<StudyKeyword> it = template.getMetadata().getStudyKeywords().iterator(); it.hasNext();) {
            StudyKeyword elem =  it.next();
            if (elem.isEmpty()) {
                   editTemplateService.removeCollectionElement(it,elem);
            }
        }
        // StudyNote
        for (Iterator<StudyNote> it = template.getMetadata().getStudyNotes().iterator(); it.hasNext();) {
            StudyNote elem =  it.next();
            if (elem.isEmpty()) {
                  editTemplateService.removeCollectionElement(it,elem);
            }
        }
        // StudyOtherId
        for (Iterator<StudyOtherId> it = template.getMetadata().getStudyOtherIds().iterator(); it.hasNext();) {
            StudyOtherId elem =  it.next();
            if (elem.isEmpty()) {
                  editTemplateService.removeCollectionElement(it,elem);
            }
        }
        // StudyProducer
        for (Iterator<StudyProducer> it = template.getMetadata().getStudyProducers().iterator(); it.hasNext();) {
            StudyProducer elem =  it.next();
            if ( elem.isEmpty()) {
                  editTemplateService.removeCollectionElement(it,elem);
            }
        }
        
        // StudySoftware
        for (Iterator<StudySoftware> it = template.getMetadata().getStudySoftware().iterator(); it.hasNext();) {
            StudySoftware elem =  it.next();
            if (elem.isEmpty()) {
                   editTemplateService.removeCollectionElement(it,elem);
           }
        }
        
        // StudyTopicClass
        for (Iterator<StudyTopicClass> it = template.getMetadata().getStudyTopicClasses().iterator(); it.hasNext();) {
            StudyTopicClass elem =  it.next();
            if (elem.isEmpty()) {
                   editTemplateService.removeCollectionElement(it,elem);
           }
        }
        // StudyRelMaterial
        for (Iterator<StudyRelMaterial> it = template.getMetadata().getStudyRelMaterials().iterator(); it.hasNext();) {
            StudyRelMaterial elem =  it.next();
            if (elem.isEmpty()) {
                   editTemplateService.removeCollectionElement(it,elem);
           }
        }
        // StudyRelPublication
        for (Iterator<StudyRelPublication> it = template.getMetadata().getStudyRelPublications().iterator(); it.hasNext();) {
            StudyRelPublication elem =  it.next();
            if (elem.isEmpty()) {
                   editTemplateService.removeCollectionElement(it,elem);
           }
        }
        // StudyRelStudy
        for (Iterator<StudyRelStudy> it = template.getMetadata().getStudyRelStudies().iterator(); it.hasNext();) {
            StudyRelStudy elem =  it.next();
            if (elem.isEmpty()) {
                   editTemplateService.removeCollectionElement(it,elem);
           }
        }
        // StudyOtherRef
        for (Iterator<StudyOtherRef> it = template.getMetadata().getStudyOtherRefs().iterator(); it.hasNext();) {
            StudyOtherRef elem =  it.next();
            if (elem.isEmpty()) {
                   editTemplateService.removeCollectionElement(it,elem);
           }
        }
        
        // custom fields
        for (StudyField studyField : template.getMetadata().getStudyFields()) {
            for (Iterator<StudyFieldValue> it = studyField.getStudyFieldValues().iterator(); it.hasNext();) {
                StudyFieldValue elem =  it.next();
                if (elem.isEmpty()) {
                    editTemplateService.removeCollectionElement(it,elem);
                }
            }
        }         
              
    }
    
    
    
    
    public String cancel() {
        /* TODO: cleanup; can we remove this commented out section?
        if (editTemplateService.getCreatedFromStudyId()!=null) {
            forwardPage="viewStudy";
            getVDCRequestBean().setStudyId(editTemplateService.getCreatedFromStudyId());
        }*/
        editTemplateService.cancel();

        return "/admin/ManageTemplatesPage?faces-redirect=true" + ((getVDCRequestBean().getCurrentVDCId() != null) ? "&vdcId=" + getVDCRequestBean().getCurrentVDCId() : "");
 
    }
    
    
    
    /**
     * Holds value of property editMode.
     */
    private String editMode;
    
    /**
     * Getter for property editMode.
     * @return Value of property editMode.
     */
    public String getEditMode() {
        return this.editMode;
    }
    
    /**
     * Setter for property editMode.
     * @param editMode New value of property editMode.
     */
    public void setEditMode(String editMode) {
        this.editMode = editMode;
    }
    
    /**
     * Holds value of property dataTableAuthors.
     */
    private HtmlDataTable dataTableAuthors;
    
    /**
     * Getter for property dataTableAuthors.
     * @return Value of property dataTableAuthors.
     */
    public HtmlDataTable getDataTableAuthors() {
        return this.dataTableAuthors;
    }
    
    /**
     * Setter for property dataTableAuthors.
     * @param dataTableAuthors New value of property dataTableAuthors.
     */
    public void setDataTableAuthors(HtmlDataTable dataTableAuthors) {
        this.dataTableAuthors = dataTableAuthors;
    }
    
    /**
     * Holds value of property dataTableAbstracts.
     */
    private HtmlDataTable dataTableAbstracts;
    
    /**
     * Getter for property dataTableAbstract.
     * @return Value of property dataTableAbstract.
     */
    public HtmlDataTable getDataTableAbstracts() {
        return this.dataTableAbstracts;
    }
    
    /**
     * Setter for property dataTableAbstract.
     * @param dataTableAbstract New value of property dataTableAbstract.
     */
    public void setDataTableAbstracts(HtmlDataTable dataTableAbstracts) {
        this.dataTableAbstracts = dataTableAbstracts;
    }
    
    /**
     * Holds value of property dataTableDistributors.
     */
    private HtmlDataTable dataTableDistributors;
    
    /**
     * Getter for property dataTableDistributor.
     * @return Value of property dataTableDistributor.
     */
    public HtmlDataTable getDataTableDistributors() {
        return this.dataTableDistributors;
    }
    
    /**
     * Setter for property dataTableDistributor.
     * @param dataTableDistributor New value of property dataTableDistributor.
     */
    public void setDataTableDistributors(HtmlDataTable dataTableDistributors) {
        this.dataTableDistributors = dataTableDistributors;
    }
    
    /**
     * Holds value of property dataTableGrants.
     */
    private HtmlDataTable dataTableGrants;
    
    /**
     * Getter for property dataTableGrant.
     * @return Value of property dataTableGrant.
     */
    public HtmlDataTable getDataTableGrants() {
        return this.dataTableGrants;
    }
    
    /**
     * Setter for property dataTableGrant.
     * @param dataTableGrant New value of property dataTableGrant.
     */
    public void setDataTableGrants(HtmlDataTable dataTableGrants) {
        this.dataTableGrants = dataTableGrants;
    }
    
    /**
     * Holds value of property dataTableKeywords.
     */
    private HtmlDataTable dataTableKeywords;
    
    /**
     * Getter for property dataTableKeyword.
     * @return Value of property dataTableKeyword.
     */
    public HtmlDataTable getDataTableKeywords() {
        return this.dataTableKeywords;
    }
    
    /**
     * Setter for property dataTableKeyword.
     * @param dataTableKeyword New value of property dataTableKeyword.
     */
    public void setDataTableKeywords(HtmlDataTable dataTableKeywords) {
        this.dataTableKeywords = dataTableKeywords;
    }
    
    /**
     * Holds value of property dataTableNotes.
     */
    private HtmlDataTable dataTableNotes;
    
    /**
     * Getter for property dataTableNotes.
     * @return Value of property dataTableNotes.
     */
    public HtmlDataTable getDataTableNotes() {
        return this.dataTableNotes;
    }
    
    /**
     * Setter for property dataTableNotes.
     * @param dataTableNotes New value of property dataTableNotes.
     */
    public void setDataTableNotes(HtmlDataTable dataTableNotes) {
        this.dataTableNotes = dataTableNotes;
    }

    /**
     * Holds value of property dataTableProducers.
     */
    private HtmlDataTable dataTableProducers;
    
    /**
     * Getter for property dataTableProducers.
     * @return Value of property dataTableProducers.
     */
    public HtmlDataTable getDataTableProducers() {
        return this.dataTableProducers;
    }
    
    /**
     * Setter for property dataTableProducers.
     * @param dataTableProducers New value of property dataTableProducers.
     */
    public void setDataTableProducers(HtmlDataTable dataTableProducers) {
        this.dataTableProducers = dataTableProducers;
    }
    
    /**
     * Holds value of property dataTableSoftware.
     */
    private HtmlDataTable dataTableSoftware;
    
    /**
     * Getter for property dataTableSoftware.
     * @return Value of property dataTableSoftware.
     */
    public HtmlDataTable getDataTableSoftware() {
        return this.dataTableSoftware;
    }
    
    /**
     * Setter for property dataTableSoftware.
     * @param dataTableSoftware New value of property dataTableSoftware.
     */
    public void setDataTableSoftware(HtmlDataTable dataTableSoftware) {
        this.dataTableSoftware = dataTableSoftware;
    }
    
    /**
     * Holds value of property dataTableTopicClass.
     */
    private HtmlDataTable dataTableTopicClass;
    
    /**
     * Getter for property dataTableTopicClass.
     * @return Value of property dataTableTopicClass.
     */
    public HtmlDataTable getDataTableTopicClass() {
        return this.dataTableTopicClass;
    }
    
    /**
     * Setter for property dataTableTopicClass.
     * @param dataTableTopicClass New value of property dataTableTopicClass.
     */
    public void setDataTableTopicClass(HtmlDataTable dataTableTopicClass) {
        this.dataTableTopicClass = dataTableTopicClass;
    }
    
 
    
   

   private String getInputLevel(String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (((StudyMapValue) getStudyMap().get(fieldName))!=null) {
                if (((StudyMapValue) getStudyMap().get(fieldName)).isRequired()) {
                    return "required";
                }
            }
        }
        for (String fieldName : fieldNames) {
            if (((StudyMapValue) getStudyMap().get(fieldName))!=null) {
                if (((StudyMapValue) getStudyMap().get(fieldName)).isRecommended()) {
                    return "recommended";
                }
            }
        }
        return "optional";
    }
    
     public String getAbstractAndScopeInputLevel() {
        return getInputLevel(StudyFieldConstant.abstractDate,
               StudyFieldConstant.abstractText,
               StudyFieldConstant.keywordValue,
               StudyFieldConstant.keywordVocab,
               StudyFieldConstant.keywordVocabURI,
               StudyFieldConstant.topicClassValue,
               StudyFieldConstant.topicClassVocab,
               StudyFieldConstant.topicClassVocabURI,
               StudyFieldConstant.relatedPublications,
               StudyFieldConstant.relatedMaterial,
               StudyFieldConstant.relatedStudies,
               StudyFieldConstant.otherReferences,
               StudyFieldConstant.timePeriodCoveredEnd,
               StudyFieldConstant.timePeriodCoveredStart,
               StudyFieldConstant.dateOfCollectionEnd,
               StudyFieldConstant.dateOfCollectionStart,
               StudyFieldConstant.country,
               StudyFieldConstant.geographicCoverage,
               StudyFieldConstant.geographicUnit,
               StudyFieldConstant.eastLongitude,
               StudyFieldConstant.westLongitude,
               StudyFieldConstant.northLatitude,
               StudyFieldConstant.southLatitude,
               StudyFieldConstant.unitOfAnalysis,
               StudyFieldConstant.kindOfData,
               StudyFieldConstant.universe);
     }

    public String getDataCollectionMethodologyInputLevel() {
        return getInputLevel(StudyFieldConstant.timeMethod,
                StudyFieldConstant.dataCollector,
                StudyFieldConstant.frequencyOfDataCollection,
                StudyFieldConstant.samplingProcedure,
                StudyFieldConstant.deviationsFromSampleDesign,
                StudyFieldConstant.collectionMode,
                StudyFieldConstant.researchInstrument,
                StudyFieldConstant.dataSources,
                StudyFieldConstant.originOfSources,
                StudyFieldConstant.characteristicOfSources,
                StudyFieldConstant.accessToSources,
                StudyFieldConstant.dataCollectionSituation,
                StudyFieldConstant.actionsToMinimizeLoss,
                StudyFieldConstant.controlOperations,
                StudyFieldConstant.weighting,
                StudyFieldConstant.studyLevelErrorNotes,
                StudyFieldConstant.studyLevelErrorNotes,
                StudyFieldConstant.samplingErrorEstimates,
                StudyFieldConstant.otherDataAppraisal);


    }
    
    public String getAdHocFieldsInputLevel() {
        return null;
    }
    
    

    public String getTermsOfUseInputLevel() {
        return getInputLevel(StudyFieldConstant.disclaimer,
                StudyFieldConstant.conditions,
                StudyFieldConstant.depositorRequirements,
                StudyFieldConstant.citationRequirements,
                StudyFieldConstant.contact,
                StudyFieldConstant.restrictions,
                StudyFieldConstant.specialPermissions,
                StudyFieldConstant.confidentialityDeclaration);

    }

    public String getDataSetAvailabilityInputLevel() {
        return getInputLevel(StudyFieldConstant.placeOfAccess,
                StudyFieldConstant.originalArchive,
                StudyFieldConstant.availabilityStatus,
                StudyFieldConstant.collectionSize,
                StudyFieldConstant.studyCompletion);

    }
   public String getGeoBoundingInputLevel() {
        return getInputLevel(StudyFieldConstant.eastLongitude,
                StudyFieldConstant.westLongitude,
                StudyFieldConstant.northLatitude,
                StudyFieldConstant.southLatitude);

    }
    public String getOtherInformationInputLevel() {
        return getInputLevel(StudyFieldConstant.NotesInformationSubject,
                StudyFieldConstant.NotesInformationType,
                StudyFieldConstant.NotesText);

    }

    public String getOtherIdLevel() {
        return getInputLevel(StudyFieldConstant.otherId,
                StudyFieldConstant.otherIdAgency);

    }

    public String getAuthorInputLevel() {
        return getInputLevel(StudyFieldConstant.authorName,
                StudyFieldConstant.authorAffiliation);


    }

    public String getProducerInputLevel() {
        return getInputLevel(StudyFieldConstant.producerName,
                StudyFieldConstant.producerAffiliation,
                StudyFieldConstant.producerAbbreviation,
                StudyFieldConstant.producerURL,
                StudyFieldConstant.producerLogo);
    }

    public String getSeriesInputLevel() {
        return getInputLevel(StudyFieldConstant.seriesName,
                StudyFieldConstant.seriesInformation);

    }
    
    public String getVersionInputLevel() {
        return getInputLevel(StudyFieldConstant.versionDate,
                StudyFieldConstant.studyVersion);

    }

    public String getSoftwareInputLevel() {
        return getInputLevel(StudyFieldConstant.softwareName,
                StudyFieldConstant.softwareVersion);

    }

    public String getGrantInputLevel() {
        return getInputLevel(StudyFieldConstant.grantNumber,
                StudyFieldConstant.grantNumberAgency);
    }

    public String getDistributorInputLevel() {
        return getInputLevel(StudyFieldConstant.distributorName,
                StudyFieldConstant.distributorAffiliation,
                StudyFieldConstant.distributorAbbreviation,
                StudyFieldConstant.distributorURL,
                StudyFieldConstant.distributorLogo);

    }

    public String getContactInputLevel() {
        return getInputLevel(StudyFieldConstant.distributorContact,
                StudyFieldConstant.distributorContactAffiliation,
                StudyFieldConstant.distributorContactEmail);

    }

    public String getAbstractInputLevel() {
        return getInputLevel(StudyFieldConstant.abstractText,
                StudyFieldConstant.abstractDate);

    }

    public String getKeywordInputLevel() {
        return getInputLevel(StudyFieldConstant.keywordValue,
                StudyFieldConstant.keywordVocab,
                StudyFieldConstant.keywordVocabURI);

    }

    public String getTopicInputLevel() {
        return getInputLevel(StudyFieldConstant.topicClassValue,
                StudyFieldConstant.topicClassVocab,
                StudyFieldConstant.topicClassVocabURI);
    }

    public String getNoteInputLevel() {
        return getInputLevel(StudyFieldConstant.NotesText,
                StudyFieldConstant.NotesInformationType,
                StudyFieldConstant.NotesInformationSubject);

    }

 
  
   
   
    private Long studyVersionId;

    public Long getStudyVersionId() {
        return studyVersionId;
    }

    public void setStudyVersionId(Long studyVersionId) {
        this.studyVersionId = studyVersionId;
    }
    
    
    private HtmlInputHidden hiddenStudyId;

    public HtmlInputHidden getHiddenStudyId() {
        return hiddenStudyId;
    }

    public void setHiddenStudyId(HtmlInputHidden hiddenStudyId) {
        this.hiddenStudyId = hiddenStudyId;
    }
    
    
    
    private Long templateId;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
    
        
    private List files;
    
    public List getFiles() {
        return files;
    }
    
    public void setFiles(List files) {
        this.files = files;
    }
    
    public String addTemplateAction() {
        return "templateForm";
    }
    public String save() {
        if (this.getTemplate()==null) {
            return "home";
        }      
        removeEmptyRows();
        editTemplateService.save();
        return "/admin/ManageTemplatesPage?faces-redirect=true" + ((getVDCRequestBean().getCurrentVDCId() != null) ? "&vdcId=" + getVDCRequestBean().getCurrentVDCId() : "");
    }

    HtmlSelectOneMenu selectFieldType;

    public HtmlSelectOneMenu getSelectFieldType() {
        return selectFieldType;
    }

    public void setSelectFieldType(HtmlSelectOneMenu selectFieldType) {
        this.selectFieldType = selectFieldType;
    }


    HtmlSelectBooleanCheckbox allowMultiplesCheck;

    public HtmlSelectBooleanCheckbox getAllowMultiplesCheck() {
        return allowMultiplesCheck;
    }

    public void setAllowMultiplesCheck(HtmlSelectBooleanCheckbox allowMultiplesCheck) {
        this.allowMultiplesCheck = allowMultiplesCheck;
    }

    public String saveField() {
        if (this.getTemplate()==null) {
            return "home";
        }

        String fieldName = (String)inputStudyFieldName.getLocalValue();
        String fieldDescription = (String)inputStudyFieldDescription.getLocalValue();
        Boolean allowMultiples = (Boolean) this.allowMultiplesCheck.getLocalValue();
        String fieldType = (String) this.selectFieldType.getValue();
                
        if(fieldName.trim().isEmpty()){
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage("New field name may not be blank.");
            context.addMessage(null, message);
            getVDCRenderBean().getFlash().put("warningMessage","New field name may not be blank."); 
            return "";
        }
        
        if(fieldDescription.trim().isEmpty()){
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage("New field description may not be blank.");
            context.addMessage(null, message);
            getVDCRenderBean().getFlash().put("warningMessage","New field description may not be blank."); 
            return "";
        }

        int maxDCM = 0;

        for (TemplateField tfl : adHocFields){
            if (tfl.getDcmSortOrder().intValue() >= maxDCM ){
                maxDCM = tfl.getDcmSortOrder().intValue();
            }
        }

                
        // Add the new Study Field (with an empty value)
        StudyField newSF = new StudyField();

        newSF.setName(fieldName);
        newSF.setDescription(fieldDescription);
        newSF.setDcmField(true);
        newSF.setFieldType(fieldType);
        newSF.setAllowMultiples(allowMultiples);        
       
        // add the initial empty value
        StudyFieldValue newSFV = new StudyFieldValue();
        newSFV.setStudyField(newSF);
        newSFV.setMetadata(template.getMetadata());
        List list = new ArrayList();
        list.add(newSFV);
        newSF.setStudyFieldValues(list);  
        
        // set the new study field in the metadata
        template.getMetadata().getStudyFields().add(newSF);

        // And add the new template field
        TemplateField newTF = new TemplateField();
        newTF.setTemplate(template);
        newTF.setStudyField(newSF);
        newTF.setdcmSortOrder(new Long(maxDCM + 1));
        newTF.setTemplateFieldControlledVocabulary(new ArrayList());
        newTF.setFieldInputLevelString("recommended"); // make all new fields recommended at creation
        TemplateFieldUI newUI = new TemplateFieldUI();
        newUI.setTemplateField(newTF);
        
        
        template.getTemplateFields().add(newTF);
        adHocFields.add(newTF);
        
        // clear the form fields
        inputStudyFieldName.setValue("");
        inputStudyFieldDescription.setValue("");
        return "";
    }
        
    public void validateLongitude(FacesContext context,
            UIComponent toValidate,
            Object value) {
        boolean valid=true;
        
        Double longitude = new Double(value.toString().trim());
        BigDecimal decimalLongitude = new BigDecimal(value.toString().trim());
        BigDecimal maxLongitude = new BigDecimal("180");
        BigDecimal minLongitude = new BigDecimal("-180");
        
        // To be valid longitude must be between 180 and -180
        if (decimalLongitude.compareTo(maxLongitude)==1 || decimalLongitude.compareTo(minLongitude)==-1) {
            valid=false;
        }
        
     
        
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("Invalid Longitude.  Value must be between -180 and 180. (Unit is decimal degrees.)");
            context.addMessage(toValidate.getClientId(context), message);
           
        }
        
    }
    
    public void validateLatitude(FacesContext context,
            UIComponent toValidate,
            Object value) {
        boolean valid=true;
        
        Double latitude = new Double(value.toString().trim());
        BigDecimal decimalLatitude = new BigDecimal(value.toString().trim());
        BigDecimal maxLatitude = new BigDecimal("90");
        BigDecimal minLatitude = new BigDecimal("-90");
        
        // To be valid latitude must be between 90 and -90
        if (decimalLatitude.compareTo(maxLatitude)==1 || decimalLatitude.compareTo(minLatitude)==-1) {
            valid=false;
        }
        
    
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Invalid Latitude.  Value must be between -90 and 90. (Unit is decimal degrees.)");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
    
    public void validateStudyAuthor(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
        // StudyAuthor
        String name = (String)inputAuthorName.getLocalValue();
        String affiliation = value.toString();
        
        if (StringUtil.isEmpty(name) && !StringUtil.isEmpty(affiliation)) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Author name is required if Affiliation is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
     public void validateStudyOtherId(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputOtherId.getLocalValue())
        && !StringUtil.isEmpty((String)value)  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Other ID  is required if Agency is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
     
      public void validateSeries(FacesContext context,
            UIComponent toValidate, Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputSeries.getLocalValue())
        && !StringUtil.isEmpty((String)value))   {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Series is required if Series Information is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
     
 public void validateVersion(FacesContext context,
            UIComponent toValidate, Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputVersion.getLocalValue())
        && !StringUtil.isEmpty((String)value)) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Version is required if Version Date is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }     
    public void validateStudyAbstract(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputAbstractText.getLocalValue())
        && !StringUtil.isEmpty((String)value)  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Abstract text  is required if Date is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }   
         public void validateStudyNote(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)this.inputNoteType.getLocalValue())
        && (!StringUtil.isEmpty((String)this.inputNoteText.getLocalValue())
            || !StringUtil.isEmpty((String)this.inputNoteSubject.getLocalValue())
            || !StringUtil.isEmpty((String)value))  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Note type is required if other note data is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }   
     
     public void validateStudySoftware(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)this.inputSoftwareName.getLocalValue())
        && !StringUtil.isEmpty((String)value)  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Software Name is required if Version is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }  
    }     
    
        public void validateStudyGrant(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputGrantNumber.getLocalValue())
        && !StringUtil.isEmpty((String)value)  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Grant Number is required if Grant Number Agency is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }  
    }       
     
     
    public void validateStudyDistributor(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputDistributorName.getLocalValue())
        && (!StringUtil.isEmpty((String)inputDistributorAbbreviation.getLocalValue()) 
             || !StringUtil.isEmpty((String)inputDistributorAffiliation.getLocalValue())
             || !StringUtil.isEmpty((String)inputDistributorLogo.getLocalValue())
             || !StringUtil.isEmpty((String)value) )) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Distributor name is required if other distributor data is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
       public void validateDistributorContact(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputDistributorContact.getLocalValue())
        && (!StringUtil.isEmpty((String)inputDistributorContactAffiliation.getLocalValue()) 
             || !StringUtil.isEmpty((String)inputDistributorContactEmail.getLocalValue())
             || !StringUtil.isEmpty((String)value) )) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Distributor contact name is required if distributor contact affiliation is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
    public void validateStudyKeyword(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputKeywordValue.getLocalValue())
        && (!StringUtil.isEmpty((String)inputKeywordVocab.getLocalValue()) 
             || !StringUtil.isEmpty((String)value) )) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Keyword value is required if other keyword data is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
   public void validateStudyTopicClass(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputTopicClassValue.getLocalValue())
        && (!StringUtil.isEmpty((String)inputTopicClassVocab.getLocalValue()) 
             || !StringUtil.isEmpty((String)value) )) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Topic Classification value is required if other topic classification data is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
           
   public void validateGeographicBounding(FacesContext context,
            UIComponent toValidate,
            Object value) {
     boolean valid=true;
     // if any geographic values are filled, then they all must be filled
     if (!StringUtil.isEmpty((String)inputWestLongitude.getLocalValue())
        ||  !StringUtil.isEmpty((String)inputEastLongitude.getLocalValue())
        ||  !StringUtil.isEmpty((String)inputNorthLatitude.getLocalValue())
        || !StringUtil.isEmpty((String)inputSouthLatitude.getLocalValue())) {
         if ( StringUtil.isEmpty((String)inputWestLongitude.getLocalValue())
            ||  StringUtil.isEmpty((String)inputEastLongitude.getLocalValue())
            ||  StringUtil.isEmpty((String)inputNorthLatitude.getLocalValue())
            || StringUtil.isEmpty((String)inputSouthLatitude.getLocalValue())) {
             
             valid=false;
         }
         
    }
     
     
        
        if (!valid) {
            inputSouthLatitude.setValid(false);
            FacesMessage message = new FacesMessage("If any geographic field is filled, then all geographic fields must be filled.");
            context.addMessage(inputSouthLatitude.getClientId(context), message);
        }
   }
   
    public void validateStudyProducer(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputProducerName.getLocalValue())
        && (!StringUtil.isEmpty((String)inputProducerAbbreviation.getLocalValue()) 
             || !StringUtil.isEmpty((String)inputProducerAffiliation.getLocalValue())
             || !StringUtil.isEmpty((String)inputProducerLogo.getLocalValue())
             || !StringUtil.isEmpty((String)value) )) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Producer name is required if other producer data is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
      
    
    
    /**
     * Holds value of property dataTableGeoBoundings.
     */
    private HtmlDataTable dataTableGeoBoundings;
    
    /**
     * Getter for property dataTableGeoBoundings.
     * @return Value of property dataTableGeoBoundings.
     */
    public HtmlDataTable getDataTableGeoBoundings() {
        return this.dataTableGeoBoundings;
    }
    
    /**
     * Setter for property dataTableGeoBoundings.
     * @param dataTableGeoBoundings New value of property dataTableGeoBoundings.
     */
    public void setDataTableGeoBoundings(HtmlDataTable dataTableGeoBoundings) {
        this.dataTableGeoBoundings = dataTableGeoBoundings;
    }
    
    private List validationFileNames = new ArrayList();
    
    public List getValidationFileNames() {
        return validationFileNames;
    }
    
    /**
     * Holds value of property dataTableRelMaterials.
     */
    private HtmlDataTable dataTableRelMaterials;
    
    /**
     * Getter for property dataTableRelMaterials.
     * @return Value of property dataTableRelMaterials.
     */
    public HtmlDataTable getDataTableRelMaterials() {
        return this.dataTableRelMaterials;
    }
    
    /**
     * Setter for property dataTableRelMaterials.
     * @param dataTableRelMaterials New value of property dataTableRelMaterials.
     */
    public void setDataTableRelMaterials(HtmlDataTable dataTableRelMaterials) {
        this.dataTableRelMaterials = dataTableRelMaterials;
    }
    
    /**
     * Holds value of property dataTableRelStudies.
     */
    private HtmlDataTable dataTableRelStudies;
    
    /**
     * Getter for property dataTableRelStudies.
     * @return Value of property dataTableRelStudies.
     */
    public HtmlDataTable getDataTableRelStudies() {
        return this.dataTableRelStudies;
    }
    
    /**
     * Setter for property dataTableRelStudies.
     * @param dataTableRelStudies New value of property dataTableRelStudies.
     */
    public void setDataTableRelStudies(HtmlDataTable dataTableRelStudies) {
        this.dataTableRelStudies = dataTableRelStudies;
    }
    
    /**
     * Holds value of property dataTableRelPublications.
     */
    private HtmlDataTable dataTableRelPublications;
    
    /**
     * Getter for property dataTableRelPublications.
     * @return Value of property dataTableRelPublications.
     */
    public HtmlDataTable getDataTableRelPublications() {
        return this.dataTableRelPublications;
    }
    
    /**
     * Setter for property dataTableRelPublications.
     * @param dataTableRelPublications New value of property dataTableRelPublications.
     */
    public void setDataTableRelPublications(HtmlDataTable dataTableRelPublications) {
        this.dataTableRelPublications = dataTableRelPublications;
    }
    
    /**
     * Holds value of property dataTableOtherReferences.
     */
    private HtmlDataTable dataTableOtherReferences;
    
    /**
     * Getter for property dataTableOtherReferences.
     * @return Value of property dataTableOtherReferences.
     */
    public HtmlDataTable getDataTableOtherReferences() {
        return this.dataTableOtherReferences;
    }
    
    /**
     * Setter for property dataTableOtherReferences.
     * @param dataTableOtherReferences New value of property dataTableOtherReferences.
     */
    public void setDataTableOtherReferences(HtmlDataTable dataTableOtherReferences) {
        this.dataTableOtherReferences = dataTableOtherReferences;
    }

    /**
     * Holds value of property inputAuthorName.
     */
    private HtmlInputText inputAuthorName;

    /**
     * Getter for property studyAuthorName.
     * @return Value of property studyAuthorName.
     */
    public HtmlInputText getInputAuthorName() {
        return this.inputAuthorName;
    }

    /**
     * Setter for property studyAuthorName.
     * @param studyAuthorName New value of property studyAuthorName.
     */
    public void setInputAuthorName(HtmlInputText inputAuthorName) {
        this.inputAuthorName = inputAuthorName;
    }


    private HtmlInputText inputStudyFieldName;


    public HtmlInputText getInputStudyFieldName() {
        return this.inputStudyFieldName;
    }

    public void setInputStudyFieldName(HtmlInputText inputStudyFieldName) {
        this.inputStudyFieldName = inputStudyFieldName;
    }

    private HtmlInputText inputStudyFieldDescription;


    public HtmlInputText getInputStudyFieldDescription() {
        return this.inputStudyFieldDescription;
    }

    public void setInputStudyFieldDescription(HtmlInputText inputStudyFieldDescription) {
        this.inputStudyFieldDescription = inputStudyFieldDescription;
    }

    private HtmlInputText inputTitle;

    public HtmlInputText getInputTitle() {
        return inputTitle;
    }

    public void setInputTitle(HtmlInputText inputTitle) {
        this.inputTitle = inputTitle;
    }

    private HtmlInputText inputTemplateValue;

    public HtmlInputText getInputTemplateValue() {
        return inputTemplateValue;
    }

    public void setInputTemplateValue(HtmlInputText inputTemplateValue) {
        this.inputTemplateValue = inputTemplateValue;
    }
    
    
    /**
     * Holds value of property inputAuthorAffiliation.
     */
    private HtmlInputText inputAuthorAffiliation;

    /**
     * Getter for property studyAuthorAffiliation.
     * @return Value of property studyAuthorAffiliation.
     */
    public HtmlInputText getInputAuthorAffiliation() {
        return this.inputAuthorAffiliation;
    }

    /**
     * Setter for property studyAuthorAffiliation.
     * @param studyAuthorAffiliation New value of property studyAuthorAffiliation.
     */
    public void setInputAuthorAffiliation(HtmlInputText inputAuthorAffiliation) {
        this.inputAuthorAffiliation = inputAuthorAffiliation;
    }

    /**
     * Holds value of property sessionCounter.
     */
    private int sessionCounter;

    /**
     * Getter for property sessionCounter.
     * @return Value of property sessionCounter.
     */
    public int getSessionCounter() {
        return this.sessionCounter;
    }

    /**
     * Setter for property sessionCounter.
     * @param sessionCounter New value of property sessionCounter.
     */
    public void setSessionCounter(int sessionCounter) {
        this.sessionCounter = sessionCounter;
    }

    /**
     * Holds value of property inputProducerName.
     */
    private HtmlInputText inputProducerName;

    /**
     * Getter for property inputStudyProducerName.
     * @return Value of property inputStudyProducerName.
     */
    public HtmlInputText getInputProducerName() {
        return this.inputProducerName;
    }

    /**
     * Setter for property inputStudyProducerName.
     * @param inputStudyProducerName New value of property inputStudyProducerName.
     */
    public void setInputProducerName(HtmlInputText inputProducerName) {
        this.inputProducerName = inputProducerName;
    }

    /**
     * Holds value of property inputProducerAffiliation.
     */
    private HtmlInputText inputProducerAffiliation;

    /**
     * Getter for property inputProducerAffiliation.
     * @return Value of property inputProducerAffiliation.
     */
    public HtmlInputText getInputProducerAffiliation() {
        return this.inputProducerAffiliation;
    }

    /**
     * Setter for property inputProducerAffiliation.
     * @param inputProducerAffiliation New value of property inputProducerAffiliation.
     */
    public void setInputProducerAffiliation(HtmlInputText inputProducerAffiliation) {
        this.inputProducerAffiliation = inputProducerAffiliation;
    }

    /**
     * Holds value of property inputProducerAbbreviation.
     */
    private HtmlInputText inputProducerAbbreviation;

    /**
     * Getter for property inputProducerAbbreviation.
     * @return Value of property inputProducerAbbreviation.
     */
    public HtmlInputText getInputProducerAbbreviation() {
        return this.inputProducerAbbreviation;
    }

    /**
     * Setter for property inputProducerAbbreviation.
     * @param inputProducerAbbreviation New value of property inputProducerAbbreviation.
     */
    public void setInputProducerAbbreviation(HtmlInputText inputProducerAbbreviation) {
        this.inputProducerAbbreviation = inputProducerAbbreviation;
    }

    /**
     * Holds value of property inputProducerUrl.
     */
    private HtmlInputText inputProducerUrl;

    /**
     * Getter for property inputProducerUrl.
     * @return Value of property inputProducerUrl.
     */
    public HtmlInputText getInputProducerUrl() {
        return this.inputProducerUrl;
    }

    /**
     * Setter for property inputProducerUrl.
     * @param inputProducerUrl New value of property inputProducerUrl.
     */
    public void setInputProducerUrl(HtmlInputText inputProducerUrl) {
        this.inputProducerUrl = inputProducerUrl;
    }

    /**
     * Holds value of property inputProducerLogo.
     */
    private HtmlInputText inputProducerLogo;

    /**
     * Getter for property inputProducerLogo.
     * @return Value of property inputProducerLogo.
     */
    public HtmlInputText getInputProducerLogo() {
        return this.inputProducerLogo;
    }

    /**
     * Setter for property inputProducerLogo.
     * @param inputProducerLogo New value of property inputProducerLogo.
     */
    public void setInputProducerLogo(HtmlInputText inputProducerLogo) {
        this.inputProducerLogo = inputProducerLogo;
    }

    /**
     * Holds value of property inputSoftwareVersion.
     */
    private HtmlInputText inputSoftwareVersion;

    /**
     * Getter for property inputSoftwareVersion.
     * @return Value of property inputSoftwareVersion.
     */
    public HtmlInputText getInputSoftwareVersion() {
        return this.inputSoftwareVersion;
    }

    /**
     * Setter for property inputSoftwareVersion.
     * @param inputSoftwareVersion New value of property inputSoftwareVersion.
     */
    public void setInputSoftwareVersion(HtmlInputText inputSoftwareVersion) {
        this.inputSoftwareVersion = inputSoftwareVersion;
    }

    /**
     * Holds value of property inputSoftwareName.
     */
    private HtmlInputText inputSoftwareName;

    /**
     * Getter for property inputSoftwareName.
     * @return Value of property inputSoftwareName.
     */
    public HtmlInputText getInputSoftwareName() {
        return this.inputSoftwareName;
    }

    /**
     * Setter for property inputSoftwareName.
     * @param inputSoftwareName New value of property inputSoftwareName.
     */
    public void setInputSoftwareName(HtmlInputText inputSoftwareName) {
        this.inputSoftwareName = inputSoftwareName;
    }

    /**
     * Holds value of property inputGrantNumber.
     */
    private HtmlInputText inputGrantNumber;

    /**
     * Getter for property inputGrantNumber.
     * @return Value of property inputGrantNumber.
     */
    public HtmlInputText getInputGrantNumber() {
        return this.inputGrantNumber;
    }

    /**
     * Setter for property inputGrantNumber.
     * @param inputGrantNumber New value of property inputGrantNumber.
     */
    public void setInputGrantNumber(HtmlInputText inputGrantNumber) {
        this.inputGrantNumber = inputGrantNumber;
    }

    /**
     * Holds value of property inputDistributorName.
     */
    private HtmlInputText inputDistributorName;

    /**
     * Getter for property inputDistributorName.
     * @return Value of property inputDistributorName.
     */
    public HtmlInputText getInputDistributorName() {
        return this.inputDistributorName;
    }

    /**
     * Setter for property inputDistributorName.
     * @param inputDistributorName New value of property inputDistributorName.
     */
    public void setInputDistributorName(HtmlInputText inputDistributorName) {
        this.inputDistributorName = inputDistributorName;
    }

    /**
     * Holds value of property inputDistributorAffiliation.
     */
    private HtmlInputText inputDistributorAffiliation;

    /**
     * Getter for property inputDistributorAffiliation.
     * @return Value of property inputDistributorAffiliation.
     */
    public HtmlInputText getInputDistributorAffiliation() {
        return this.inputDistributorAffiliation;
    }

    /**
     * Setter for property inputDistributorAffiliation.
     * @param inputDistributorAffiliation New value of property inputDistributorAffiliation.
     */
    public void setInputDistributorAffiliation(HtmlInputText inputDistributorAffiliation) {
        this.inputDistributorAffiliation = inputDistributorAffiliation;
    }

    /**
     * Holds value of property inputDistributorAbbreviation.
     */
    private HtmlInputText inputDistributorAbbreviation;

    /**
     * Getter for property inputDistributorAbbreviation.
     * @return Value of property inputDistributorAbbreviation.
     */
    public HtmlInputText getInputDistributorAbbreviation() {
        return this.inputDistributorAbbreviation;
    }

    /**
     * Setter for property inputDistributorAbbreviation.
     * @param inputDistributorAbbreviation New value of property inputDistributorAbbreviation.
     */
    public void setInputDistributorAbbreviation(HtmlInputText inputDistributorAbbreviation) {
        this.inputDistributorAbbreviation = inputDistributorAbbreviation;
    }

    /**
     * Holds value of property inputDistributorUrl.
     */
    private HtmlInputText inputDistributorUrl;

    /**
     * Getter for property inputDistributorUrl.
     * @return Value of property inputDistributorUrl.
     */
    public HtmlInputText getInputDistributorUrl() {
        return this.inputDistributorUrl;
    }

    /**
     * Setter for property inputDistributorUrl.
     * @param inputDistributorUrl New value of property inputDistributorUrl.
     */
    public void setInputDistributorUrl(HtmlInputText inputDistributorUrl) {
        this.inputDistributorUrl = inputDistributorUrl;
    }

    /**
     * Holds value of property inputDistributorLogo.
     */
    private HtmlInputText inputDistributorLogo;

    /**
     * Getter for property inputDistributorLogo.
     * @return Value of property inputDistributorLogo.
     */
    public HtmlInputText getInputDistributorLogo() {
        return this.inputDistributorLogo;
    }

    /**
     * Setter for property inputDistributorLogo.
     * @param inputDistributorLogo New value of property inputDistributorLogo.
     */
    public void setInputDistributorLogo(HtmlInputText inputDistributorLogo) {
        this.inputDistributorLogo = inputDistributorLogo;
    }

    /**
     * Holds value of property inputRelPublicationName.
     */
    private HtmlInputTextarea inputRelPublicationName;

    /**
     * Getter for property inputRelPublicationName.
     * @return Value of property inputRelPublicationName.
     */
    public HtmlInputTextarea getInputRelPublicationName() {
        return this.inputRelPublicationName;
    }

    /**
     * Setter for property inputRelPublicationName.
     * @param inputRelPublicationName New value of property inputRelPublicationName.
     */
    public void setInputRelPublicationName(HtmlInputTextarea inputRelPublicationName) {
        this.inputRelPublicationName = inputRelPublicationName;
    }

    /**
     * Holds value of property inputRelMaterial.
     */
    private HtmlInputTextarea inputRelMaterial;

    /**
     * Getter for property inputRelMaterial.
     * @return Value of property inputRelMaterial.
     */
    public HtmlInputTextarea getInputRelMaterial() {
        return this.inputRelMaterial;
    }

    /**
     * Setter for property inputRelMaterial.
     * @param inputRelMaterial New value of property inputRelMaterial.
     */
    public void setInputRelMaterial(HtmlInputTextarea inputRelMaterial) {
        this.inputRelMaterial = inputRelMaterial;
    }

    /**
     * Holds value of property inputReltemplate.getMetadata().
     */
    private HtmlInputTextarea inputRelStudy;

    /**
     * Getter for property inputReltemplate.getMetadata().
     * @return Value of property inputReltemplate.getMetadata().
     */
    public HtmlInputTextarea getInputRelStudy() {
        return this.inputRelStudy;
    }

    /**
     * Setter for property inputReltemplate.getMetadata().
     * @param inputRelStudy New value of property inputReltemplate.getMetadata().
     */
    public void setInputRelStudy(HtmlInputTextarea inputRelStudy) {
        this.inputRelStudy = inputRelStudy;
    }

    /**
     * Holds value of property inputOtherReference.
     */
    private HtmlInputText inputOtherReference;

    /**
     * Getter for property inputOtherReference.
     * @return Value of property inputOtherReference.
     */
    public HtmlInputText getInputOtherReference() {
        return this.inputOtherReference;
    }

    /**
     * Setter for property inputOtherReference.
     * @param inputOtherReference New value of property inputOtherReference.
     */
    public void setInputOtherReference(HtmlInputText inputOtherReference) {
        this.inputOtherReference = inputOtherReference;
    }

    /**
     * Holds value of property inputKeywordValue.
     */
    private HtmlInputText inputKeywordValue;

    /**
     * Getter for property inputKeywordValue.
     * @return Value of property inputKeywordValue.
     */
    public HtmlInputText getInputKeywordValue() {
        return this.inputKeywordValue;
    }

    /**
     * Setter for property inputKeywordValue.
     * @param inputKeywordValue New value of property inputKeywordValue.
     */
    public void setInputKeywordValue(HtmlInputText inputKeywordValue) {
        this.inputKeywordValue = inputKeywordValue;
    }

    /**
     * Holds value of property inputKeywordVocab.
     */
    private HtmlInputText inputKeywordVocab;

    /**
     * Getter for property inputKeywordVocab.
     * @return Value of property inputKeywordVocab.
     */
    public HtmlInputText getInputKeywordVocab() {
        return this.inputKeywordVocab;
    }

    /**
     * Setter for property inputKeywordVocab.
     * @param inputKeywordVocab New value of property inputKeywordVocab.
     */
    public void setInputKeywordVocab(HtmlInputText inputKeywordVocab) {
        this.inputKeywordVocab = inputKeywordVocab;
    }

    /**
     * Holds value of property inputKeywordVocabUri.
     */
    private HtmlInputText inputKeywordVocabUri;

    /**
     * Getter for property inputKeywordVocabUri.
     * @return Value of property inputKeywordVocabUri.
     */
    public HtmlInputText getInputKeywordVocabUri() {
        return this.inputKeywordVocabUri;
    }

    /**
     * Setter for property inputKeywordVocabUri.
     * @param inputKeywordVocabUri New value of property inputKeywordVocabUri.
     */
    public void setInputKeywordVocabUri(HtmlInputText inputKeywordVocabUri) {
        this.inputKeywordVocabUri = inputKeywordVocabUri;
    }

    /**
     * Holds value of property inputTopicClassValue.
     */
    private HtmlInputText inputTopicClassValue;

    /**
     * Getter for property inputTopicClassValue.
     * @return Value of property inputTopicClassValue.
     */
    public HtmlInputText getInputTopicClassValue() {
        return this.inputTopicClassValue;
    }

    /**
     * Setter for property inputTopicClassValue.
     * @param inputTopicClassValue New value of property inputTopicClassValue.
     */
    public void setInputTopicClassValue(HtmlInputText inputTopicClassValue) {
        this.inputTopicClassValue = inputTopicClassValue;
    }

    /**
     * Holds value of property inputTopicClassVocab.
     */
    private HtmlInputText inputTopicClassVocab;

    /**
     * Getter for property inputTopicClassVocab.
     * @return Value of property inputTopicClassVocab.
     */
    public HtmlInputText getInputTopicClassVocab() {
        return this.inputTopicClassVocab;
    }

    /**
     * Setter for property inputTopicClassVocab.
     * @param inputTopicClassVocab New value of property inputTopicClassVocab.
     */
    public void setInputTopicClassVocab(HtmlInputText inputTopicClassVocab) {
        this.inputTopicClassVocab = inputTopicClassVocab;
    }

    /**
     * Holds value of property inputTopicClassVocabUri.
     */
    private HtmlInputText inputTopicClassVocabUri;

    /**
     * Getter for property inputTopicVocabUri.
     * @return Value of property inputTopicVocabUri.
     */
    public HtmlInputText getInputTopicClassVocabUri() {
        return this.inputTopicClassVocabUri;
    }

    /**
     * Setter for property inputTopicVocabUri.
     * @param inputTopicVocabUri New value of property inputTopicVocabUri.
     */
    public void setInputTopicClassVocabUri(HtmlInputText inputTopicClassVocabUri) {
        this.inputTopicClassVocabUri = inputTopicClassVocabUri;
    }

    /**
     * Holds value of property inputAbstractText.
     */
    private javax.faces.component.html.HtmlInputTextarea inputAbstractText;

    /**
     * Getter for property inputAbstractText.
     * @return Value of property inputAbstractText.
     */
    public javax.faces.component.html.HtmlInputTextarea getInputAbstractText() {
        return this.inputAbstractText;
    }

    /**
     * Setter for property inputAbstractText.
     * @param inputAbstractText New value of property inputAbstractText.
     */
    public void setInputAbstractText(javax.faces.component.html.HtmlInputTextarea inputAbstractText) {
        this.inputAbstractText = inputAbstractText;
    }

    /**
     * Holds value of property inputAbstractDate.
     */
    private HtmlInputText inputAbstractDate;

    /**
     * Getter for property inputAbstractDate.
     * @return Value of property inputAbstractDate.
     */
    public HtmlInputText getInputAbstractDate() {
        return this.inputAbstractDate;
    }

    /**
     * Setter for property inputAbstractDate.
     * @param inputAbstractDate New value of property inputAbstractDate.
     */
    public void setInputAbstractDate(HtmlInputText inputAbstractDate) {
        this.inputAbstractDate = inputAbstractDate;
    }

    /**
     * Holds value of property inputNoteText.
     */
    private HtmlInputText inputNoteText;

    /**
     * Getter for property inputNoteText.
     * @return Value of property inputNoteText.
     */
    public HtmlInputText getInputNoteText() {
        return this.inputNoteText;
    }

    /**
     * Setter for property inputNoteText.
     * @param inputNoteText New value of property inputNoteText.
     */
    public void setInputNoteText(HtmlInputText inputNoteText) {
        this.inputNoteText = inputNoteText;
    }

    /**
     * Holds value of property inputNoteSubject.
     */
    private HtmlInputText inputNoteSubject;

    /**
     * Getter for property inputNoteSubject.
     * @return Value of property inputNoteSubject.
     */
    public HtmlInputText getInputNoteSubject() {
        return this.inputNoteSubject;
    }

    /**
     * Setter for property inputNoteSubject.
     * @param inputNoteSubject New value of property inputNoteSubject.
     */
    public void setInputNoteSubject(HtmlInputText inputNoteSubject) {
        this.inputNoteSubject = inputNoteSubject;
    }

    /**
     * Holds value of property inputNoteType.
     */
    private HtmlInputText inputNoteType;

    /**
     * Getter for property inputNoteType.
     * @return Value of property inputNoteType.
     */
    public HtmlInputText getInputNoteType() {
        return this.inputNoteType;
    }

    /**
     * Setter for property inputNoteType.
     * @param inputNoteType New value of property inputNoteType.
     */
    public void setInputNoteType(HtmlInputText inputNoteType) {
        this.inputNoteType = inputNoteType;
    }

    /**
     * Holds value of property inputOtherId.
     */
    private HtmlInputText inputOtherId;

    /**
     * Getter for property inputOtherId.
     * @return Value of property inputOtherId.
     */
    public HtmlInputText getInputOtherId() {
        return this.inputOtherId;
    }

    /**
     * Setter for property inputOtherId.
     * @param inputOtherId New value of property inputOtherId.
     */
    public void setInputOtherId(HtmlInputText inputOtherId) {
        this.inputOtherId = inputOtherId;
    }

    /**
     * Holds value of property inputOtherIdAgency.
     */
    private HtmlInputText inputOtherIdAgency;

    /**
     * Getter for property inputOtherIdAgency.
     * @return Value of property inputOtherIdAgency.
     */
    public HtmlInputText getInputOtherIdAgency() {
        return this.inputOtherIdAgency;
    }

    /**
     * Setter for property inputOtherIdAgency.
     * @param inputOtherIdAgency New value of property inputOtherIdAgency.
     */
    public void setInputOtherIdAgency(HtmlInputText inputOtherIdAgency) {
        this.inputOtherIdAgency = inputOtherIdAgency;
    }

    /**
     * Holds value of property inputGrantAgency.
     */
    private HtmlInputText inputGrantAgency;

    /**
     * Getter for property inputGrantAgency.
     * @return Value of property inputGrantAgency.
     */
    public HtmlInputText getInputGrantAgency() {
        return this.inputGrantAgency;
    }

    /**
     * Setter for property inputGrantAgency.
     * @param inputGrantAgency New value of property inputGrantAgency.
     */
    public void setInputGrantAgency(HtmlInputText inputGrantAgency) {
        this.inputGrantAgency = inputGrantAgency;
    }

    /**
     * Holds value of property inputSeries.
     */
    private HtmlInputText inputSeries;

    /**
     * Getter for property inputSeries.
     * @return Value of property inputSeries.
     */
    public HtmlInputText getInputSeries() {
        return this.inputSeries;
    }

    /**
     * Setter for property inputSeries.
     * @param inputSeries New value of property inputSeries.
     */
    public void setInputSeries(HtmlInputText inputSeries) {
        this.inputSeries = inputSeries;
    }

    /**
     * Holds value of property inputSeriesInformation.
     */
    private HtmlInputText inputSeriesInformation;

    /**
     * Getter for property inputSeriesInformation.
     * @return Value of property inputSeriesInformation.
     */
    public HtmlInputText getInputSeriesInformation() {
        return this.inputSeriesInformation;
    }

    /**
     * Setter for property inputSeriesInformation.
     * @param inputSeriesInformation New value of property inputSeriesInformation.
     */
    public void setInputSeriesInformation(HtmlInputText inputSeriesInformation) {
        this.inputSeriesInformation = inputSeriesInformation;
    }

    /**
     * Holds value of property inputVersion.
     */
    private HtmlInputText inputVersion;

    /**
     * Getter for property inputVersion.
     * @return Value of property inputVersion.
     */
    public HtmlInputText getInputVersion() {
        return this.inputVersion;
    }

    /**
     * Setter for property inputVersion.
     * @param inputVersion New value of property inputVersion.
     */
    public void setInputVersion(HtmlInputText inputVersion) {
        this.inputVersion = inputVersion;
    }

    /**
     * Holds value of property inputVersionDate.
     */
    private HtmlInputText inputVersionDate;

    /**
     * Getter for property inputVersionDate.
     * @return Value of property inputVersionDate.
     */
    public HtmlInputText getInputVersionDate() {
        return this.inputVersionDate;
    }

    /**
     * Setter for property inputVersionDate.
     * @param inputVersionDate New value of property inputVersionDate.
     */
    public void setInputVersionDate(HtmlInputText inputVersionDate) {
        this.inputVersionDate = inputVersionDate;
    }

    /**
     * Holds value of property inputDistributorContact.
     */
    private HtmlInputText inputDistributorContact;

    /**
     * Getter for property inputDistributorContact.
     * @return Value of property inputDistributorContact.
     */
    public HtmlInputText getInputDistributorContact() {
        return this.inputDistributorContact;
    }

    /**
     * Setter for property inputDistributorContact.
     * @param inputDistributorContact New value of property inputDistributorContact.
     */
    public void setInputDistributorContact(HtmlInputText inputDistributorContact) {
        this.inputDistributorContact = inputDistributorContact;
    }

    /**
     * Holds value of property inputDistributorContactAffiliation.
     */
    private HtmlInputText inputDistributorContactAffiliation;

    /**
     * Getter for property inputDistributorContactAffiliation.
     * @return Value of property inputDistributorContactAffiliation.
     */
    public HtmlInputText getInputDistributorContactAffiliation() {
        return this.inputDistributorContactAffiliation;
    }

    /**
     * Setter for property inputDistributorContactAffiliation.
     * @param inputDistributorContactAffiliation New value of property inputDistributorContactAffiliation.
     */
    public void setInputDistributorContactAffiliation(HtmlInputText inputDistributorContactAffiliation) {
        this.inputDistributorContactAffiliation = inputDistributorContactAffiliation;
    }

    /**
     * Holds value of property inputDistributorContactEmail.
     */
    private HtmlInputText inputDistributorContactEmail;

    /**
     * Getter for property inputDistributorContactEmail.
     * @return Value of property inputDistributorContactEmail.
     */
    public HtmlInputText getInputDistributorContactEmail() {
        return this.inputDistributorContactEmail;
    }

    /**
     * Setter for property inputDistributorContactEmail.
     * @param inputDistributorContactEmail New value of property inputDistributorContactEmail.
     */
    public void setInputDistributorContactEmail(HtmlInputText inputDistributorContactEmail) {
        this.inputDistributorContactEmail = inputDistributorContactEmail;
    }

    /**
     * Holds value of property inputWestLongitude.
     */
    private HtmlInputText inputWestLongitude;

    /**
     * Getter for property inputWestLongitude.
     * @return Value of property inputWestLongitude.
     */
    public HtmlInputText getInputWestLongitude() {
        return this.inputWestLongitude;
    }

    /**
     * Setter for property inputWestLongitude.
     * @param inputWestLongitude New value of property inputWestLongitude.
     */
    public void setInputWestLongitude(HtmlInputText inputWestLongitude) {
        this.inputWestLongitude = inputWestLongitude;
    }

    /**
     * Holds value of property inputEastLongitude.
     */
    private HtmlInputText inputEastLongitude;

    /**
     * Getter for property inputEastLongitude.
     * @return Value of property inputEastLongitude.
     */
    public HtmlInputText getInputEastLongitude() {
        return this.inputEastLongitude;
    }

    /**
     * Setter for property inputEastLongitude.
     * @param inputEastLongitude New value of property inputEastLongitude.
     */
    public void setInputEastLongitude(HtmlInputText inputEastLongitude) {
        this.inputEastLongitude = inputEastLongitude;
    }

    /**
     * Holds value of property inputNorthLatitude.
     */
    private HtmlInputText inputNorthLatitude;

    /**
     * Getter for property inputNorthLatitude.
     * @return Value of property inputNorthLatitude.
     */
    public HtmlInputText getInputNorthLatitude() {
        return this.inputNorthLatitude;
    }

    /**
     * Setter for property inputNorthLatitude.
     * @param inputNorthLatitude New value of property inputNorthLatitude.
     */
    public void setInputNorthLatitude(HtmlInputText inputNorthLatitude) {
        this.inputNorthLatitude = inputNorthLatitude;
    }

    /**
     * Holds value of property inputSouthLatitude.
     */
    private HtmlInputText inputSouthLatitude;

    /**
     * Getter for property inputSouthLatitude.
     * @return Value of property inputSouthLatitude.
     */
    public HtmlInputText getInputSouthLatitude() {
        return this.inputSouthLatitude;
    }

    /**
     * Setter for property inputSouthLatitude.
     * @param inputSouthLatitude New value of property inputSouthLatitude.
     */
    public void setInputSouthLatitude(HtmlInputText inputSouthLatitude) {
        this.inputSouthLatitude = inputSouthLatitude;
    }

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() {
    }
    
    public void changeRecommend(ValueChangeEvent event) {
            Boolean newValue = (Boolean)event.getNewValue();
            String studyFieldName=  event.getComponent().getId();
            StudyMapValue studyMapValue = (StudyMapValue)studyMap.get(studyFieldName);
            editTemplateService.changeRecommend(studyMapValue.getTemplateField(), newValue);
    }

    public void changeFieldInputValue(ValueChangeEvent event) {
            String newValue = (String)event.getNewValue();
            String id =event.getComponent().getId();
            String getFieldName = (String) event.getComponent().getAttributes().get("fieldName");
            StudyMapValue studyMapValue = (StudyMapValue)studyMap.get(getFieldName);
             
            editTemplateService.changeFieldInputLevel(studyMapValue.getTemplateField(), newValue);
    }
    

    public void changeFieldInputValueDCM(ValueChangeEvent event) {
        Long getOrder = (Long) event.getComponent().getAttributes().get("dcmSortOrder");
        TemplateField changeValue = adHocFields.get(getOrder.intValue() -1 );
        String newValue = (String)event.getNewValue();
        editTemplateService.changeFieldInputLevel(changeValue, newValue);
    }
    
    public void changeSingleValCV(ValueChangeEvent event) {
        /*
        Long sf_Id = (Long) event.getComponent().getAttributes().get("sf_id");
        
        for (TemplateField tfTest: adHocFields){
            if (tfTest.getStudyField().getId().equals(sf_Id)){
                
                List data = (List)tfTest.getTemplateFieldValues();
                List removeItems = new ArrayList();
                for (TemplateFieldValue tfv: tfTest.getTemplateFieldValues()){
                    removeItems.add(tfv);
                }
                for (Object o: removeItems){
                    editTemplateService.removeCollectionElement(data,o);
                }
                String inStr = (String)event.getNewValue();
                if (!inStr.equals("--No Value--")){
                    TemplateFieldValue elem = new TemplateFieldValue();
                    elem.setTemplateField(tfTest);
                    elem.setMetadata(this.getTemplate().getMetadata());
                    elem.setStrValue((String)event.getNewValue());
                    List values = new ArrayList();
                    values.add(elem);
                    tfTest.setTemplateFieldValues(values);
                }
            }
        }*/
    }
    
    public void changeMultiValCV(ValueChangeEvent event) {
        Long sf_Id = (Long) event.getComponent().getAttributes().get("sf_id");
        /*
        for (TemplateField tfTest: adHocFields){
            if (tfTest.getStudyField().getId().equals(sf_Id)){
                
                List data = (List)tfTest.getTemplateFieldValues();
                List removeItems = new ArrayList();
                for (TemplateFieldValue tfv: tfTest.getTemplateFieldValues()){
                    removeItems.add(tfv);
                }
                for (Object o: removeItems){
                    editTemplateService.removeCollectionElement(data,o);
                } 
                
                List inStringList = (List) event.getNewValue();
                List values = new ArrayList(); 
                for (Object inObj: inStringList){                  
                    String inStr = (String) inObj;
                    if (!inStr.equals("--No Value--")){
                        TemplateFieldValue elem = new TemplateFieldValue();
                        elem.setTemplateField(tfTest);
                        elem.setMetadata(this.getTemplate().getMetadata());
                        elem.setStrValue(inStr);
                        values.add(elem); 
                    }                                      
                }
                tfTest.setTemplateFieldValues(values);
            }
        }*/
    }
    
    public void openPopup(ActionEvent ae) {
        Long getId = (Long) ae.getComponent().getAttributes().get("sf_id");
        String getName = (String) ae.getComponent().getAttributes().get("sf_name");
            for (TemplateField tfTest : template.getTemplateFields()) {
                if(getId !=null && getId.equals(tfTest.getStudyField().getId())){
                   setTemplateCVField(tfTest); 
                }
                if (getName.equals(tfTest.getStudyField().getName())){
                   setTemplateCVField(tfTest);  
                }
                
            }
        showPopup = true;
    }
    
    boolean showPopup;
    
    private TemplateField templateCVField;

    public TemplateField getTemplateField() {
        return templateCVField;
    }

    public void setTemplateCVField(TemplateField templateField) {
        
        this.templateCVField = templateField;
    }

    public boolean isShowPopup() {
        
        return showPopup;
    }

    public void setShowPopup(boolean showPopup) {
        this.showPopup = showPopup;
    }
    
    private String newControlledVocab;

    public String getNewControlledVocab() {
        return newControlledVocab;
    }

    public void setNewControlledVocab(String newControlledVocab) {
        this.newControlledVocab = newControlledVocab;
    }
    
    public void addToControlledVocabList(){
                
        if(newControlledVocab.isEmpty()){
            return;
        }
        for (TemplateFieldControlledVocabulary tfCvPrior: templateCVField.getTemplateFieldControlledVocabulary() ){
            if(newControlledVocab.equals(tfCvPrior.getStrValue())){
                return;
            }
        }
               
        TemplateFieldControlledVocabulary tfCV = new TemplateFieldControlledVocabulary();
        tfCV.setTemplateField(templateCVField);
        tfCV.setStrValue(newControlledVocab);

        templateCVField.getTemplateFieldControlledVocabulary().add(tfCV);
        return;
    }
    
    private List<String> selectedControlledVocabStrings;

    public List<String> getSelectedControlledVocabStrings() {
        return selectedControlledVocabStrings;
    }

    public void setSelectedControlledVocabStrings(List<String> selectedControlledVocabStrings) {
        this.selectedControlledVocabStrings = selectedControlledVocabStrings;
    }
    
    public List<String> getTemplateFieldValueStrings(){
        return null;
    }
    
    private String selectedControlledVocabString = new String("");

    public String getSelectedControlledVocabString() {
        return selectedControlledVocabString;
    }

    public void setSelectedControlledVocabString(String selectedControlledVocabId) {
        this.selectedControlledVocabString = selectedControlledVocabId;
    }
        
    public void removeSelectedControlledVocab(){
       
        List data = (List)templateCVField.getTemplateFieldControlledVocabulary();
        List <TemplateFieldControlledVocabulary> dataReset = new ArrayList <TemplateFieldControlledVocabulary>();
        TemplateFieldControlledVocabulary removeVal = new TemplateFieldControlledVocabulary();
           
        boolean removeExisting = false;
        List inStringList = (List) this.selectControlledVocabulary.getValue();  
        if (inStringList.isEmpty()){
            return;
        }
        boolean readded = false;
        boolean thisOneremoved = false;
        for (TemplateFieldControlledVocabulary tfCV  : templateCVField.getTemplateFieldControlledVocabulary()){
            readded = false;
            thisOneremoved = false;
             for (Object inObj: inStringList){                  
             String inStr = (String) inObj;
                if (inStr.equals(tfCV.getStrValue())){
                    removeVal = tfCV;
                    removeExisting = true;
                    thisOneremoved = true;
                } 
                
            }
            if (!thisOneremoved){
                dataReset.add(tfCV);
            }
        }
        


        if (removeExisting){
            editTemplateService.removeCollectionElement(data,removeVal);
            templateCVField.getTemplateFieldControlledVocabulary().remove(removeVal);
        }
        
        //clear and re-add the controlled vocab
        templateCVField.getTemplateFieldControlledVocabulary().clear();
        if (!dataReset.isEmpty()){
            for (TemplateFieldControlledVocabulary tfCV: dataReset ){
                 templateCVField.getTemplateFieldControlledVocabulary().add(tfCV);                
            }            
        }
        
            
        return;
    }
    
    public void closePopup(ActionEvent ae) {
        showPopup = false;
    }
    
    public List<String> getControlledVocabStrings() {

        List stringList = new ArrayList<String>();
        
        if (templateCVField != null  && templateCVField.getTemplateFieldControlledVocabulary() !=null ){
            for(TemplateFieldControlledVocabulary tfCV: templateCVField.getTemplateFieldControlledVocabulary()) {
                     stringList.add(tfCV.getStrValue());
                }             
        }

        return stringList;
    }
    

    
    HtmlSelectManyListbox selectControlledVocabulary;

    public HtmlSelectManyListbox getSelectControlledVocabulary() {
        return selectControlledVocabulary;
    }

    public void setSelectControlledVocabulary(HtmlSelectManyListbox selectControlledVocabulary) {
        this.selectControlledVocabulary = selectControlledVocabulary;
    }
    private HtmlInputText inputControlledVocabulary;

    /**
     * Getter for property studyAuthorName.
     * @return Value of property studyAuthorName.
     */
    public HtmlInputText getInputControlledVocabulary() {
        return this.inputControlledVocabulary;
    }

    /**
     * Setter for property studyAuthorName.
     * @param studyAuthorName New value of property studyAuthorName.
     */
    public void setInputControlledVocabulary(HtmlInputText inputControlledVocabulary) {
        this.inputControlledVocabulary = inputControlledVocabulary;
    }
    
    
    private PanelSeries customFieldsPanelSeries;

    public PanelSeries getCustomFieldsPanelSeries() {
        return customFieldsPanelSeries;
    }

    public void setCustomFieldsPanelSeries(PanelSeries customFieldsPanelSeries) {
        this.customFieldsPanelSeries = customFieldsPanelSeries;
    }
   
    public DataModel getCustomFieldsDataModel() {
        List values = new ArrayList();
        for (TemplateField tf : getAdHocFields()) {

            Object[] row = new Object[2];
            row[0] = tf;
            row[1] = getCustomValuesDataModel(tf);
            values.add(row);
        }
        return new ListDataModel(values);

    }

    private DataModel getCustomValuesDataModel(TemplateField customField) {
        List values = new ArrayList();
        
        for (StudyField studyField : template.getMetadata().getStudyFields()) {
            if (studyField.equals(customField.getStudyField())) {            
                for (StudyFieldValue sfv : studyField.getStudyFieldValues()) {

                    Object[] row = new Object[2];
                    row[0] = sfv;
                    row[1] = studyField.getStudyFieldValues(); // used by the remove method
                    values.add(row);
                }
                
                break;
            }
        }
        return new ListDataModel(values);
    }

    public void addDcmRow(ActionEvent ae) {

        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        Object[] data = (Object[]) ((ListDataModel) dataTable.getValue()).getRowData();

        StudyFieldValue newElem = new StudyFieldValue();
        newElem.setMetadata(template.getMetadata());
        newElem.setStudyField(((StudyFieldValue) data[0]).getStudyField());
        newElem.setStrValue("");
        ((List) data[1]).add(dataTable.getRowIndex() + 1, newElem);
    }

    public void removeDcmRow(ActionEvent ae) {

        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        if (dataTable.getRowCount() > 1) {
            Object[] data = (Object[]) ((ListDataModel) dataTable.getValue()).getRowData();
            editTemplateService.removeCollectionElement((List) data[1], data[0]);

        }
    }

    public void moveUp(ActionEvent ae) {
        int rowIndex = customFieldsPanelSeries.getRowIndex();
        if (rowIndex > 0) {
            TemplateField moveDown = adHocFields.get(rowIndex - 1);
            TemplateField moveUp = adHocFields.get(rowIndex);
            moveUp.setdcmSortOrder(new Long(rowIndex - 1));
            moveDown.setdcmSortOrder(new Long(rowIndex));
        }

        Collections.sort(adHocFields, comparator);
    }

    public void moveDown(ActionEvent ae) {
        int rowIndex = customFieldsPanelSeries.getRowIndex();
        if (rowIndex < adHocFields.size() - 1) {
            TemplateField moveDown = adHocFields.get(rowIndex);
            TemplateField moveUp = adHocFields.get(rowIndex + 1);
            moveUp.setdcmSortOrder(new Long(rowIndex));
            moveDown.setdcmSortOrder(new Long(rowIndex + 1));
        }

        Collections.sort(adHocFields, comparator);
    }
}
