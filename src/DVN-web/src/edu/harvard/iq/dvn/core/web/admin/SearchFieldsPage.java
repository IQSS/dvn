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
 * SearchFieldsPage.java
 *
 * Created on October 10, 2006, 10:23 AM
 */
package edu.harvard.iq.dvn.core.web.admin;

import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyField;
import edu.harvard.iq.dvn.core.study.StudyFieldServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("SearchFieldsPage")
public class SearchFieldsPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCServiceLocal vdcService;
    @EJB StudyFieldServiceLocal studyFieldService;
    @EJB IndexServiceLocal indexService;
    DataModel studyFields;
    Collection <StudyField> searchResultsFields;
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        if (msg == null){
            msg =  (StatusMessage)getRequestMap().get("statusMessage");
        }
        searchResultsFields = getVDCRequestBean().getCurrentVDC().getSearchResultFields();
        for (Iterator it = searchResultsFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.getName().equals("productionDate")){
                productionDateResults = true;
            }            
            if (elem.getName().equals("producerName")){
                producerResults = true;
            }
            if (elem.getName().equals("distributionDate")){
                distributionDateResults = true;
            }
            if (elem.getName().equals("distributorName")){
                distributorResults = true;
            }
            if (elem.getName().equals("replicationFor")){
                replicationForResults = true;
            }
            if (elem.getName().equals("relatedPublications")){
                relatedPublicationsResults = true;
            }
            if (elem.getName().equals("relatedMaterial")){
                relatedMaterialResults = true;
            }
            if (elem.getName().equals("relatedStudies")){
                relatedStudiesResults = true;
            }
        }
    }
    
    StatusMessage msg;
    
    public StatusMessage getMsg(){
        return msg;
    }
    
    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }
    
    private boolean productionDateResults;

    public boolean isProductionDateResults() {
        return productionDateResults;
    }

    public void setProductionDateResults(boolean productionDateResults) {
        this.productionDateResults = productionDateResults;
    }
    
    private boolean producerResults;
    
    public boolean isProducerResults(){
        return producerResults;
    }
    
    public void setProducerResults(boolean checked){
        this.producerResults = checked;
    }
    
    private boolean distributionDateResults;
    
    public boolean isDistributionDateResults(){
        return distributionDateResults;
    }
    
    public void setDistributionDateResults(boolean checked){
        this.distributionDateResults = checked;
    }
    
    private boolean distributorResults;
    
    public boolean isDistributorResults(){
        return distributorResults;
    }
    
    public void setDistributorResults(boolean checked){
        this.distributorResults = checked;
    }
    
    private boolean replicationForResults;
    
    public boolean isReplicationForResults(){
        return replicationForResults;
    }
    
    public void setReplicationForResults(boolean checked){
        this.replicationForResults = checked;
    }
    
    private boolean relatedPublicationsResults;
    
    public boolean isRelatedPublicationsResults(){
        return relatedPublicationsResults;
    }
    
    public void setRelatedPublicationsResults(boolean checked){
        this.relatedPublicationsResults = checked;
    }
    
    private boolean relatedMaterialResults;
    
    public boolean isRelatedMaterialResults(){
        return relatedMaterialResults;
    }
    
    public void setRelatedMaterialResults(boolean checked){
        this.relatedMaterialResults = checked;
    }
    
    private boolean relatedStudiesResults;
    
    public boolean isRelatedStudiesResults(){
        return relatedStudiesResults;
    }
    
    public void setRelatedStudiesResults(boolean checked){
        this.relatedStudiesResults = checked;
    }


    public IndexServiceLocal getIndexService() {
        return indexService;
    }

    public StudyFieldServiceLocal getStudyFieldService() {
        return studyFieldService;
    }



    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public SearchFieldsPage() {
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
    
    /*
    public List <StudyField> getStudyFields(){
        List studyFields = studyFieldService.findAll();
        return studyFields;
    }
     */
    
    public DataModel getStudyFields(){
        VDC thisVDC = vdcService.find(new Long(1));
        Collection resultFields = thisVDC.getSearchResultFields();
        Collection advSearchFields = thisVDC.getAdvSearchFields();
        boolean searchResultsFieldsEmpty = resultFields.isEmpty();
        List displayFields = new ArrayList();
        List postgresStudyFields = studyFieldService.findAll();
        for (Iterator it = postgresStudyFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            DisplayStudyField displayField = new DisplayStudyField();
            displayField.setName(elem.getName());
            if (searchResultsFieldsEmpty){
                displayField.setDisplayAdvancedSearch(elem.isAdvancedSearchField());
                displayField.setDisplaySearchResults(elem.isSearchResultField());
            }
            else{
                displayField.setDisplayAdvancedSearch(searchCollectionForStudyField(advSearchFields, elem));
                displayField.setDisplaySearchResults(searchCollectionForStudyField(resultFields, elem));
            }
            displayField.setDisplaySearchResultsDisabled(elem.isSearchResultField());
            displayFields.add(displayField);
            
        }
        studyFields = new ListDataModel(displayFields);
        return studyFields;
    }
    
    private boolean searchCollectionForStudyField(Collection collection, StudyField sf){
        boolean retVal = false;
        for (Iterator it = collection.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.getId().equals(sf.getId())){
                retVal = true;
            }
        }
        return retVal;
    }
            
    public String update(){
        ArrayList advancedSearchFields = new ArrayList();
        ArrayList searchResultsFields = new ArrayList();
        List fields = (List<DisplayStudyField>) studyFields.getWrappedData();
        for (Iterator it = fields.iterator(); it.hasNext();) {
            DisplayStudyField elem = (DisplayStudyField) it.next();
            if (elem.isDisplayAdvancedSearch()){
                StudyField advancedSearchField = studyFieldService.findByName(elem.getName());
                advancedSearchFields.add(advancedSearchField);
            }
            if (elem.isDisplaySearchResults()){
                StudyField searchResultsField = studyFieldService.findByName(elem.getName());
                searchResultsFields.add(searchResultsField);
            }
            
        }
//        VDC thisVDC = vdcService.find(new Long(1));
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        thisVDC.setAdvSearchFields(advancedSearchFields);
        thisVDC.setSearchResultFields(searchResultsFields);
        vdcService.edit(thisVDC);
        
        return "success";
    }
    
    public String save(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        List <StudyField> newSearchResultsFields = getDefaultSearchResultsFields();
        if (productionDateResults){
            StudyField productionDateResultsField = studyFieldService.findByName("productionDate");
            newSearchResultsFields.add(productionDateResultsField);
        }        
        if (producerResults){
            StudyField producerResultsField = studyFieldService.findByName("producer");
            newSearchResultsFields.add(producerResultsField);
        }
        if (distributionDateResults){
            StudyField distributionDateResultsField = studyFieldService.findByName("distributionDate");
            newSearchResultsFields.add(distributionDateResultsField);
        }
        if (distributorResults){
            StudyField distributorResultsField = studyFieldService.findByName("distributor");
            newSearchResultsFields.add(distributorResultsField);
        }
        if (replicationForResults){
            StudyField replicationResultsField = studyFieldService.findByName("publicationReplicationData");
            newSearchResultsFields.add(replicationResultsField);
        }
        if (relatedPublicationsResults){
            StudyField relatedpubResultsField = studyFieldService.findByName("publication");
            newSearchResultsFields.add(relatedpubResultsField);
        }
        if (relatedMaterialResults){
            StudyField relatedmatResultsField = studyFieldService.findByName("relatedMaterial");
            newSearchResultsFields.add(relatedmatResultsField);
        }
        if (relatedStudiesResults){
            StudyField relatedstudiesResultsField = studyFieldService.findByName("relatedStudies");
            newSearchResultsFields.add(relatedstudiesResultsField);
        }
        if (!newSearchResultsFields.isEmpty()){
            thisVDC.setSearchResultFields(newSearchResultsFields);
            vdcService.edit(thisVDC);
        }
        
        getVDCRequestBean().setCurrentVDC(thisVDC);
        String forwardPage = "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();

        getVDCRenderBean().getFlash().put("successMessage","Successfully updated search fields.");
        return forwardPage;
     
    }
    
    private List <StudyField> getDefaultSearchResultsFields(){
        ArrayList searchResultsFields = new ArrayList();
        List allStudyFields = studyFieldService.findAll();
        for (Iterator it = allStudyFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.isSearchResultField()){
                searchResultsFields.add(elem);
            }
        }
        return searchResultsFields;        
    }
    
    public String cancel(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        getVDCRequestBean().setCurrentVDC(thisVDC);
        String forwardPage = "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
        return forwardPage;
    }
    
    private boolean success = false;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    
}

