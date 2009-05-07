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
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
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

    private HtmlSelectBooleanCheckbox productionDateCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getProductionDateCheckbox() {
        return productionDateCheckbox;
    }

    public void setProductionDateCheckbox(HtmlSelectBooleanCheckbox productionDateCheckbox) {
        this.productionDateCheckbox = productionDateCheckbox;
    }
    
    private HtmlSelectBooleanCheckbox producerCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getProducerCheckbox() {
        return producerCheckbox;
    }

    public void setProducerCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.producerCheckbox = hsbc;
    }


    private HtmlSelectBooleanCheckbox distributionDateCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getDistributionDateCheckbox() {
        return distributionDateCheckbox;
    }

    public void setDistributionDateCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.distributionDateCheckbox = hsbc;
    }

    private HtmlSelectBooleanCheckbox distributorCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getDistributorCheckbox() {
        return distributorCheckbox;
    }

    public void setDistributorCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.distributorCheckbox = hsbc;
    }

    private HtmlSelectBooleanCheckbox replicationCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getReplicationCheckbox() {
        return replicationCheckbox;
    }

    public void setReplicationCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.replicationCheckbox = hsbc;
    }

    private HtmlSelectBooleanCheckbox relatedpubCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getRelatedpubCheckbox() {
        return relatedpubCheckbox;
    }

    public void setRelatedpubCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.relatedpubCheckbox = hsbc;
    }

    private HtmlSelectBooleanCheckbox relatedmatCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getRelatedmatCheckbox() {
        return relatedmatCheckbox;
    }

    public void setRelatedmatCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.relatedmatCheckbox = hsbc;
    }

    private HtmlSelectBooleanCheckbox relatedstudiesCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getRelatedstudiesCheckbox() {
        return relatedstudiesCheckbox;
    }

    public void setRelatedstudiesCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.relatedstudiesCheckbox = hsbc;
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
        if (productionDateCheckbox.isSelected()){
            StudyField productionDateResultsField = studyFieldService.findByName("productionDate");
            newSearchResultsFields.add(productionDateResultsField);
        }        
        if (producerCheckbox.isSelected()){
            StudyField producerResultsField = studyFieldService.findByName("producerName");
            newSearchResultsFields.add(producerResultsField);
        }
        if (distributionDateCheckbox.isSelected()){
            StudyField distributionDateResultsField = studyFieldService.findByName("distributionDate");
            newSearchResultsFields.add(distributionDateResultsField);
        }
        if (distributorCheckbox.isSelected()){
            StudyField distributorResultsField = studyFieldService.findByName("distributorName");
            newSearchResultsFields.add(distributorResultsField);
        }
        if (replicationCheckbox.isSelected()){
            StudyField replicationResultsField = studyFieldService.findByName("replicationFor");
            newSearchResultsFields.add(replicationResultsField);
        }
        if (relatedpubCheckbox.isSelected()){
            StudyField relatedpubResultsField = studyFieldService.findByName("relatedPublications");
            newSearchResultsFields.add(relatedpubResultsField);
        }
        if (relatedmatCheckbox.isSelected()){
            StudyField relatedmatResultsField = studyFieldService.findByName("relatedMaterial");
            newSearchResultsFields.add(relatedmatResultsField);
        }
        if (relatedstudiesCheckbox.isSelected()){
            StudyField relatedstudiesResultsField = studyFieldService.findByName("relatedStudies");
            newSearchResultsFields.add(relatedstudiesResultsField);
        }
        if (!newSearchResultsFields.isEmpty()){
            thisVDC.setSearchResultFields(newSearchResultsFields);
            vdcService.edit(thisVDC);
        }
     
        getVDCRequestBean().setSuccessMessage("Successfully updated search fields.");
        return "myOptions";
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
//s        indexService.indexAll();
        return "myOptions";
    }
    
    private boolean success = false;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    
}

