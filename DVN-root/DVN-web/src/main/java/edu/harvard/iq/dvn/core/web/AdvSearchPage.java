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
 * AdvSearchPage.java
 *
 * Created on September 14, 2006, 11:27 AM
 */
package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.index.SearchTerm;
import edu.harvard.iq.dvn.core.study.StudyField;
import edu.harvard.iq.dvn.core.study.FileMetadataField; 
import edu.harvard.iq.dvn.core.study.StudyFieldServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.collection.CollectionModel;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.site.VDCUI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ActionEvent;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.icesoft.faces.component.ext.HtmlSelectOneRadio;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlPanelGrid;
import edu.harvard.iq.dvn.core.index.DVNAnalyzer;
import edu.harvard.iq.dvn.core.index.DvnQuery;
import edu.harvard.iq.dvn.core.index.ResultsWithFacets;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import javax.faces.component.UIColumn;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import org.apache.lucene.facet.search.results.FacetResult;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@Named("AdvSearchPage")
@ViewScoped
public class AdvSearchPage extends VDCBaseBean implements java.io.Serializable {

    private static final Logger logger = Logger.getLogger(AdvSearchPage.class.getCanonicalName());

    @EJB
    VDCServiceLocal vdcService;
    @EJB
    VDCCollectionServiceLocal vdcCollectionService;
    @EJB
    IndexServiceLocal indexServiceBean;
    @EJB
    StudyFieldServiceLocal studyFieldService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    VariableServiceLocal varService;
    private Locale currentLocale = getExternalContext().getRequestLocale();
    private ResourceBundle messages = ResourceBundle.getBundle("Bundle");
    private HashMap advSearchFieldMap = new HashMap();
    private HashMap fileMetadataFieldMap = new HashMap(); 
    private HashMap operatorMap = new HashMap();
    private boolean collectionsIncluded;
    private boolean searchableFileLevelMetadata; 
    private boolean variableSearch;
    private boolean fileLevelMetadataSearch;
    private List <SearchTerm>  variableInfoList = new ArrayList();
    
    // 3.3: 
    // We are no longer using the hard-coded list of "advanced search fields" 
    // (i.e., the fields that appear in the pulldown menu on the AdvancedSearch 
    // page); instead we retrieve the list from the StudyField database table. 
    // However, at this point some extra processing of that list is required: 
    // some field names need to be changed, because the names of the 
    // corresponding Lucene indexes do not match. 
    // So for these we still have to maintain the following hard-coded map.
    // 
    // We are planning to clean this up, possibly as early as in 3.4, by 
    // introducing another database table that would keep this mapping, 
    // between the metadata fields and the Lucene indexes. 
    // -L.A. 
    
    private static Map<String, String> StudyFields2Indexes = new HashMap<String,String>(); 
    static {
        StudyFields2Indexes.put("description", "abstractText");
        StudyFields2Indexes.put("studyId", "globalId");
        StudyFields2Indexes.put("publicationCitation", "relatedPublications");
        StudyFields2Indexes.put("publicationReplicationData", "replicationFor");

    }
    
  
    public boolean isVariableSearch() {
        return variableSearch;
    }

    public void setVariableSearch(boolean variableSearch) {
        this.variableSearch = variableSearch;
    }
    
    public boolean isFileLevelMetadataSearch() {
        return fileLevelMetadataSearch;
    }

    public void setFileLevelMetadataSearch(boolean flms) {
        this.fileLevelMetadataSearch = flms;
    }

    public boolean isCollectionsIncluded() {
        return collectionsIncluded;
    }

    public boolean isSearchableFileLevelMetadata() {
        return searchableFileLevelMetadata;
    } 

    public List getVariableInfoList() {
        return variableInfoList;
    }

    public void setVariableInfoList(List variableInfo) {
        this.variableInfoList = variableInfo;
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
        radioButtonList1DefaultItems = initSelectItemList(new String[]{messages.getString("searchAllCollections"), messages.getString("searchOnlySelectedCollections")});
        dropdown3DefaultItems = initSelectItemList(getAdvSearchFieldDefaults());
        dropdown4DateItems = initSelectItemList(new String[]{messages.getString("isGreaterThan"), messages.getString("isLessThan")});
        dropdown4NotDateItems = initSelectItemList(new String[]{messages.getString("contains"), messages.getString("doesNotContain")});

        // Adding 4 rows here and removing user's ability to add/remove in interface.
        /*  Add these back to xhtml when/if user is given control
                    <ice:column>
                        <ice:commandButton image="/resources/images/icon_add.gif" actionListener="#{AdvSearchPage.addRow}"/>
                        <ice:commandButton rendered="#{AdvSearchPage.dataTableVariableInfo.rowCount gt 1}" image="/resources/images/icon_remove.gif" actionListener="#{AdvSearchPage.removeRow}" />
                    </ice:column>
         */
        initVariableInfoList();
        operatorMap.put(messages.getString("contains"), "=");
        operatorMap.put(messages.getString("doesNotContain"), "-");
        operatorMap.put(messages.getString("isGreaterThan"), ">");
        operatorMap.put(messages.getString("isLessThan"), "<");
        
        if (getVDCRequestBean().getCurrentVDC() != null){
        long vdcId=getVDCRequestBean().getCurrentVDC().getId().longValue();
        dropdown3DefaultItems = initSelectItemList(getSearchScopeList(vdcId));
        }
        else{
        dropdown3DefaultItems = initSelectItemList(getAdvSearchFieldDefaults());
        }
        

        collectionModelList = getCollectionsDisplay();
        
        // Finally, check if we have searchable file-level metadata fields (keys);
        // and if available,  initiate the select item dropdown:
        fileLevelMetaKeysDropdown = initSelectItemList(getFileLevelMetaKeys()); 
    }
    
    private List<SelectItem> initSelectItemList(String[] itemsArray ) {
        List<SelectItem> list = new ArrayList();
        for (int i = 0; i < itemsArray.length; i++) {
            SelectItem selectItem = new SelectItem(itemsArray[i]);
            list.add(selectItem);          
        }
        return list;
    }

    private void initVariableInfoList(){
        variableInfoList.add(initVariableSearchTerm());
        //We originally added 4 rows but the AND on the search would never return a result.
        //left these here as a reminder just in case we re-visit.  SEK
        //variableInfoList.add(initVariableSearchTerm());
        //variableInfoList.add(initVariableSearchTerm());
        //variableInfoList.add(initVariableSearchTerm());

    }

    private SearchTerm initVariableSearchTerm(){
        SearchTerm addToList = new SearchTerm();
        addToList.setFieldName("variable");
        addToList.setOperator("=");
        addToList.setValue("");
        return addToList;
    }
    private HtmlSelectOneMenu dropdown1 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown1() {
        return dropdown1;
    }
 /**
     * Holds value of property inputVariableInfo.
     */
    private HtmlInputText inputVariableInfo;

    /**
     * Getter for property variableInfo.
     * @return Value of property inputVariableInfo.
     */
    public HtmlInputText getInputVariableInfo() {
        return this.inputVariableInfo;
    }

    /**
     * Setter for property variableInfo.
     * @param variableInfo New value of property variableInfo.
     */
    public void setInputVariableInfo(HtmlInputText inputVariableInfo) {
        this.inputVariableInfo = inputVariableInfo;
    }
    
    
    /**
     * fileMetaSearchValue - field for the file-level metadata search
     * query, plus its getter and setter: 
     */
    private HtmlInputText fileMetaSearchValue = new HtmlInputText();

    public HtmlInputText getFileMetaSearchValue() {
        return this.fileMetaSearchValue;
    }

    public void setFileMetaSearchValue(HtmlInputText input) {
        this.fileMetaSearchValue = input;
    }
    
    public void setDropdown1(HtmlSelectOneMenu hsom) {
        this.dropdown1 = hsom;
    }

    private UISelectItems dropdown1SelectItems = new UISelectItems();

    public UISelectItems getDropdown1SelectItems() {
        return dropdown1SelectItems;
    }

    public void setDropdown1SelectItems(UISelectItems uisi) {
        this.dropdown1SelectItems = uisi;
    }

    private HtmlCommandButton searchCommand;

    private HtmlPanelGrid gridPanel1;

    private HtmlInputText textField1 = new HtmlInputText();

    public HtmlInputText getTextField1() {
        return textField1;
    }

    public void setTextField1(HtmlInputText hit) {
        this.textField1 = hit;
    }
    private HtmlSelectOneMenu dropdown3 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown3() {
        return dropdown3;
    }

    public void setDropdown3(HtmlSelectOneMenu hsom) {
        this.dropdown3 = hsom;
    }
    
    private List<SelectItem> dropdown3DefaultItems = new ArrayList();

    public List<SelectItem> getDropdown3DefaultItems() {
        return dropdown3DefaultItems;
    }

    public void setDropdown3DefaultItems(List<SelectItem> dropdown3DefaultItems) {
        this.dropdown3DefaultItems = dropdown3DefaultItems;
    }

    // Dropdown for the searchable studyfile-level metadata keys, 
    // if available: 
    
    private List<SelectItem> fileLevelMetaKeysDropdown = new ArrayList();

    public List<SelectItem> getFileLevelMetaKeysDropdown() {
        return fileLevelMetaKeysDropdown;
    }

    public void setFileLevelMetaKeysDropdown(List<SelectItem> dropDownItems) {
        this.fileLevelMetaKeysDropdown = dropDownItems;
    }

    private UISelectItems dropdown3SelectItems = new UISelectItems();

    public UISelectItems getDropdown3SelectItems() {
        return dropdown3SelectItems;
    }

    public void setDropdown3SelectItems(UISelectItems uisi) {
        this.dropdown3SelectItems = uisi;
    }
    
    
    /**
     * Bindings for the file metadata keyword search dropdown 
     * and its select items: 
     */
    
    private HtmlSelectOneMenu dropdownFileMeta = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdownFileMeta() {
        return dropdownFileMeta;
    }
    
    public void setDropdownFileMeta(HtmlSelectOneMenu dd) {
        this.dropdownFileMeta = dd; 
    }
    
    private UISelectItems dropdownFileMetaSelectItems = new UISelectItems();

    public UISelectItems getDropdownFileMetaSelectItems() {
        return dropdownFileMetaSelectItems;
    }

    public void setDropdownFileMetaSelectItems(UISelectItems uisi) {
        this.dropdownFileMetaSelectItems = uisi;
    }
    
    
    private HtmlSelectOneMenu dropdown4 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown4() {
        return dropdown4;
    }

    public void setDropdown4(HtmlSelectOneMenu hsom) {
        this.dropdown4 = hsom;
    }

    private List<SelectItem>  dropdown4DateItems = new ArrayList();
    
    public List<SelectItem> getDropdown4DateItems() {
        return dropdown4DateItems;
    }

    public void setDropdown4DateItems(List<SelectItem> dropdown4DateItems) {
        this.dropdown4DateItems = dropdown4DateItems;
    }

    private List<SelectItem>  dropdown4NotDateItems = new ArrayList();

    public List<SelectItem> getDropdown4NotDateItems() {
        return dropdown4NotDateItems;
    }

    public void setDropdown4NotDateItems(List<SelectItem> dropdown4NotDateItems) {
        this.dropdown4NotDateItems = dropdown4NotDateItems;
    }




    private UISelectItems dropdown4SelectItems = new UISelectItems();

    public UISelectItems getDropdown4SelectItems() {
        return dropdown4SelectItems;
    }

    public void setDropdown4SelectItems(UISelectItems uisi) {
        this.dropdown4SelectItems = uisi;
    }
    private HtmlInputText textField2 = new HtmlInputText();

    public HtmlInputText getTextField2() {
        return textField2;
    }

    public void setTextField2(HtmlInputText hit) {
        this.textField2 = hit;
    }
    private HtmlSelectOneMenu dropdown5 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown5() {
        return dropdown5;
    }

    public void setDropdown5(HtmlSelectOneMenu hsom) {
        this.dropdown5 = hsom;
    }
    private UISelectItems dropdown3SelectItems1 = new UISelectItems();

    public UISelectItems getDropdown3SelectItems1() {
        return dropdown3SelectItems1;
    }

    public void setDropdown3SelectItems1(UISelectItems uisi) {
        this.dropdown3SelectItems1 = uisi;
    }
    private HtmlSelectOneMenu dropdown6 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown6() {
        return dropdown6;
    }

    public void setDropdown6(HtmlSelectOneMenu hsom) {
        this.dropdown6 = hsom;
    }
    private UISelectItems dropdown4SelectItems1 = new UISelectItems();

    public UISelectItems getDropdown4SelectItems1() {
        return dropdown4SelectItems1;
    }

    public void setDropdown4SelectItems1(UISelectItems uisi) {
        this.dropdown4SelectItems1 = uisi;
    }
    private HtmlInputText textField3 = new HtmlInputText();

    public HtmlInputText getTextField3() {
        return textField3;
    }

    public void setTextField3(HtmlInputText hit) {
        this.textField3 = hit;
    }
    private HtmlSelectOneMenu dropdown7 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown7() {
        return dropdown7;
    }

    public void setDropdown7(HtmlSelectOneMenu hsom) {
        this.dropdown7 = hsom;
    }
    private UISelectItems dropdown3SelectItems2 = new UISelectItems();

    public UISelectItems getDropdown3SelectItems2() {
        return dropdown3SelectItems2;
    }

    public void setDropdown3SelectItems2(UISelectItems uisi) {
        this.dropdown3SelectItems2 = uisi;
    }
    private HtmlSelectOneMenu dropdown8 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown8() {
        return dropdown8;
    }

    public void setDropdown8(HtmlSelectOneMenu hsom) {
        this.dropdown8 = hsom;
    }
    private UISelectItems dropdown4SelectItems2 = new UISelectItems();

    public UISelectItems getDropdown4SelectItems2() {
        return dropdown4SelectItems2;
    }

    public void setDropdown4SelectItems2(UISelectItems uisi) {
        this.dropdown4SelectItems2 = uisi;
    }
    private HtmlInputText textField4 = new HtmlInputText();

    public HtmlInputText getTextField4() {
        return textField4;
    }

    public void setTextField4(HtmlInputText hit) {
        this.textField4 = hit;
    }
    private HtmlSelectOneRadio radioButtonList1 = new HtmlSelectOneRadio();

    public HtmlSelectOneRadio getRadioButtonList1() {
        return radioButtonList1;
    }

    public void setRadioButtonList1(HtmlSelectOneRadio hsor) {
        this.radioButtonList1 = hsor;
    }
    
    private List<SelectItem> radioButtonList1DefaultItems = new ArrayList();

    public List<SelectItem> getRadioButtonList1DefaultItems() {
        return radioButtonList1DefaultItems;
    }

    public void setRadioButtonList1DefaultItems(List<SelectItem> radioButtonList1DefaultItems) {
        this.radioButtonList1DefaultItems = radioButtonList1DefaultItems;
    }


    private UISelectItems radioButtonList1SelectItems = new UISelectItems();

    public UISelectItems getRadioButtonList1SelectItems() {
        return radioButtonList1SelectItems;
    }

    public void setRadioButtonList1SelectItems(UISelectItems uisi) {
        this.radioButtonList1SelectItems = uisi;
    }
    private HtmlSelectOneMenu dropdown9 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown9() {
        return dropdown9;
    }

    public void setDropdown9(HtmlSelectOneMenu hsom) {
        this.dropdown9 = hsom;
    }
    private UISelectItems dropdown4SelectItems3 = new UISelectItems();

    public UISelectItems getDropdown4SelectItems3() {
        return dropdown4SelectItems3;
    }

    public void setDropdown4SelectItems3(UISelectItems uisi) {
        this.dropdown4SelectItems3 = uisi;
    }
    private HtmlDataTable dataTable1 = new HtmlDataTable();

    public HtmlDataTable getDataTable1() {
        return dataTable1;
    }

    public void setDataTable1(HtmlDataTable hdt) {
        this.dataTable1 = hdt;
    }
    
    private List<CollectionModel> collectionModelList = new ArrayList();

    public List<CollectionModel> getCollectionModelList() {
        return collectionModelList;
    }

    public void setCollectionModelList(List<CollectionModel> collectionModelList) {
        this.collectionModelList = collectionModelList;
    }

    private UIColumn column1 = new UIColumn();

    public UIColumn getColumn1() {
        return column1;
    }

    public void setColumn1(UIColumn uic) {
        this.column1 = uic;
    }
    private HtmlSelectBooleanCheckbox checkbox1 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox1() {
        return checkbox1;
    }

    public void setCheckbox1(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox1 = hsbc;
    }

    // </editor-fold>
        /**
     * Holds value of property dataVariableInfo.
     */
    private HtmlDataTable dataTableVariableInfo;

    /**
     * Getter for property dataVariableInfo.
     * @return Value of property dataVariableInfo.
     */
    public HtmlDataTable getDataTableVariableInfo() {
        return this.dataTableVariableInfo;
    }

    public void setDataTableVariableInfo(HtmlDataTable hdt) {
        this.dataTableVariableInfo = hdt;
    }

    private HtmlInputText input_VariableInfo;
    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public AdvSearchPage() {
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

    public void checkboxList1_processValueChange(ValueChangeEvent vce) {
        // TODO: Replace with your code
    }

    public String[] getAdvSearchFieldDefaults() {

        List<StudyField> advSearchFieldsDefault = studyFieldService.findAdvSearchDefault();
        String[] advS = new String[advSearchFieldsDefault.size()]; 
        
        advS = getFieldList(advSearchFieldsDefault);

        return advS;
    }

    public String[] getFileLevelMetaKeys() {
        List<FileMetadataField> availableFileMetadataFields = studyFieldService.findAvailableFileMetadataFields(); 
        String[] fields = getFileMetadataFieldList(availableFileMetadataFields); 
        
        if (fields.length > 0) {
            searchableFileLevelMetadata = true; 
        }
        return fields; 
    }
    public String[] getSearchScopeList(long vdcId) {
//        ArrayList displayNames = new ArrayList();
        VDC vdc = vdcService.find(new Long(vdcId));
        Collection advSearchFields = vdc.getAdvSearchFields();
        String[] advS = getFieldList(advSearchFields);
        return advS;
    }

    private String[] getFieldList(final Collection advSearchFields) {
        String[] advS = new String[advSearchFields.size()];
//        DefaultSelectItemsArray dsia = new DefaultSelectItemsArray();
        int i = 0;
        for (Iterator it = advSearchFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            elem.getId();
            
            String sfname = elem.getName();
            
            // And one final post-processing step is required on the name of
            // the field: 
            // (see the comment above, where StudyFields2Indexes map is defined)
            // -L.A.
            
            if (sfname != null) {
                if (StudyFields2Indexes.get(sfname) != null) {
                    sfname = StudyFields2Indexes.get(sfname);
                }              
            }        
            
            // Now check if we have a "user-friendly" description for this field 
            // in our resource bundle: 
            advS[i] = getUserFriendlySearchField(sfname);
            // if not, check if the field has a description in the database: 
            if (advS[i].equals(sfname)) {
                if (elem.getTitle() != null && !elem.getTitle().equals("")) {
                    advS[i] = elem.getTitle();
                }
            }

            advSearchFieldMap.put(advS[i++], sfname);

        }
        return advS;
    }

    
    private String[] getFileMetadataFieldList(final Collection fileMetaFields) {
        String[] fields = new String[fileMetaFields.size()];
//        DefaultSelectItemsArray dsia = new DefaultSelectItemsArray();
        int i = 0;
        for (Iterator it = fileMetaFields.iterator(); it.hasNext();) {
            FileMetadataField elem = (FileMetadataField) it.next();
            elem.getId();
            
            // This implements a scheme similar to what we use with the 
            // advanced study fields; with a mechanism for providing
            // "user-friendly" names for the search fields - but for now we are 
            // not using it. 
            
            fields[i] = elem.getFileFormatName() + "-" + elem.getName();
            String indexName = elem.getFileFormatName() + "-" + elem.getName();
            
            
            
            fileMetadataFieldMap.put(fields[i], indexName);
            i++; 

        }
        return fields;
    }
    
    private String getUserFriendlySearchField(String searchField) {
        try {
            return ResourceBundle.getBundle("SearchFieldBundle").getString(searchField);
        } catch (MissingResourceException e) {
            
            return searchField;
        }
    }    


    public List getCollectionsDisplay() {
        ArrayList collections = new ArrayList();
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        if (vdc != null) {
            getVDCCollections(vdc, collections);
        } else {
            collectionsIncluded = false;
        }
        return collections;
    }

    private void getVDCCollections(final VDC vdc, final ArrayList collections) {
        int treeLevel = 1;
        
        if (  !new VDCUI(vdc).containsOnlyLinkedCollections() ) {
            VDCCollection vdcRootCollection = vdc.getRootCollection();
            CollectionModel row = getRow(vdcRootCollection, treeLevel);
            collections.add(row);
            List<VDCCollection> subcollections = vdcCollectionService.findSubCollections(vdcRootCollection.getId(), false);
            if (!subcollections.isEmpty()) {
                collectionsIncluded = true;
            }
            buildDisplayModel(collections, subcollections, treeLevel);
        }
        
        // linked collections
        List<VDCCollection> linkedCollections = new VDCUI(vdc).getLinkedCollections(false);
        for (Iterator it = linkedCollections.iterator(); it.hasNext();) {
            VDCCollection link = (VDCCollection) it.next();
            CollectionModel linkedRow = getRow(link, treeLevel);
            collections.add(linkedRow);
            // linked collection subcollections
            buildDisplayModel(collections, vdcCollectionService.findSubCollections(link.getId(), false), treeLevel);

        }
        if (!linkedCollections.isEmpty()) {
            collectionsIncluded = true;
        }
    }

    private int buildDisplayModel(ArrayList collections, List<VDCCollection> vdcCollections, int level) {
        level++;
        for (Iterator it = vdcCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
//            collection.setLevel(level);
            CollectionModel row = getRow(elem, level);
//            collections.add(collection);
            collections.add(row);
            List<VDCCollection> subcollections = vdcCollectionService.findSubCollections(elem.getId(), false);
//                Collection <VDCCollection> subcollections = elem.getSubCollections();
            if (!subcollections.isEmpty()) {
                buildDisplayModel(collections, subcollections, level);
            }
        }
        level--;
        return level;
    }

    private CollectionModel getRow(final VDCCollection collection, final int level) {
        CollectionModel row = new CollectionModel();
        row.setLevel(level);
        row.setId(collection.getId());
        row.setName(collection.getName());
        row.setSelected(false);
        return row;
    }


  

    

    public String search() {
//        String query = buildQuery();
        List searchCollections = null;
        boolean searchOnlySelectedCollections = false;
        
        if (validateAllSearchCriteria()){
            if (radioButtonList1.getValue() != null) {
                String radioButtonStr = (String) radioButtonList1.getValue();
                if (radioButtonStr.indexOf("Only") > 1) {
                    searchOnlySelectedCollections = true;

                 searchCollections = new ArrayList();
                 for (Iterator it = collectionModelList.iterator(); it.hasNext();) {
                      CollectionModel elem = (CollectionModel) it.next();
                      if (elem.isSelected()) {
                          VDCCollection selectedCollection = vdcCollectionService.find(elem.getId());
                            searchCollections.add(selectedCollection);
                        }
                 }
                 if (searchCollections.isEmpty()) {
                      searchOnlySelectedCollections = false;
                   }
                }
            }

            List<SearchTerm> searchTerms = buildSearchTermList();
            VDC thisVDC = getVDCRequestBean().getCurrentVDC();
            List<Long> viewableIds = null;
            if (searchOnlySelectedCollections) {
                viewableIds = indexServiceBean.search(thisVDC, searchCollections, searchTerms);
            } else {
                viewableIds = indexServiceBean.search(thisVDC, searchTerms);
            }

            StudyListing sl = new StudyListing(StudyListing.SEARCH);
            sl.setVdcId(getVDCRequestBean().getCurrentVDCId());
            sl.setSearchTerms(searchTerms);
            if (isVariableSearch()) {
                Map variableMap = new HashMap();
                List studies = new ArrayList();
                varService.determineStudiesFromVariables(viewableIds, studies, variableMap);
                sl.setStudyIds(studies);
                sl.setVariableMap(variableMap);

            } else if (isFileLevelMetadataSearch()) {
                /**
                * This (experimental) code is for treating
                * file-level metadata searches in the same way that we treat
                * variable searches; meaning that the actual files that matched 
                * the query will be shown on the StudyListingPage; just like 
                * the individual variables are shown there for variable searches.
                */
              
                Map fileMap = new HashMap(); 
                List studies = new ArrayList();
                studyService.determineStudiesFromFiles(viewableIds, studies, fileMap);
                sl.setStudyIds(studies);
                sl.setFileMap(fileMap);
            } else {
                sl.setStudyIds(viewableIds);
            }

            String studyListingIndex = StudyListing.addToStudyListingMap(sl, getSessionMap());
            return "/StudyListingPage.xhtml?faces-redirect=true&studyListingIndex=" + studyListingIndex + getContextSuffix();
        }
        else{
            return "";
        }        
        
    }

    public String searchWithFacets() {
//        String query = buildQuery();
//        List searchCollections = null;
        ArrayList<VDCCollection> searchCols = new ArrayList<VDCCollection>();
        boolean searchOnlySelectedCollections = false;

        if (validateAllSearchCriteria()) {
            if (radioButtonList1.getValue() != null) {
                String radioButtonStr = (String) radioButtonList1.getValue();
                if (radioButtonStr.indexOf("Only") > 1) {
                    searchOnlySelectedCollections = true;

//                    searchCollections = new ArrayList();
                    for (Iterator it = collectionModelList.iterator(); it.hasNext();) {
                        CollectionModel elem = (CollectionModel) it.next();
                        if (elem.isSelected()) {
                            VDCCollection selectedCollection = vdcCollectionService.find(elem.getId());
                            searchCols.add(selectedCollection);
                        }
                    }
                    if (searchCols.isEmpty()) {
//                    if (searchCollections.isEmpty()) {
                        searchOnlySelectedCollections = false;
                    }
                }
            }

            List<SearchTerm> searchTerms = buildSearchTermList();
            VDC thisVDC = getVDCRequestBean().getCurrentVDC();
            List<Long> viewableIds = null;
            ResultsWithFacets resultsWithFacets = null;
            
            if (searchOnlySelectedCollections) {
//                viewableIds = indexServiceBean.search(thisVDC, searchCollections, searchTerms);
                // older, non-faceted method above
                if (isVariableSearch() || isFileLevelMetadataSearch()) {
                    viewableIds = indexServiceBean.search(thisVDC, searchCols, searchTerms);
                    logger.fine("searching selected collections [old, facet-less method...]");
                } else {
                    logger.fine("searching selected collections [new, facet-enabled method...]");
                    List<Query> collectionQueries = new ArrayList<Query>();
                    QueryParser parser = new QueryParser(Version.LUCENE_30, "abstract", new DVNAnalyzer());
                    parser.setDefaultOperator(QueryParser.AND_OPERATOR);
                    for (VDCCollection col : searchCols) {
                        Long colId = col.getId();
                        String type = col.getType();
                        String queryString = col.getQuery();
                        boolean isDynamic = col.isDynamic();
                        boolean isLocalScope = col.isLocalScope();
                        boolean isSubnetworkScope = col.isSubnetworkScope();
                                
                        boolean isRootCollection = col.isRootCollection();
                        VDC colOwner = col.getOwner();
                        StringBuilder sbOuter = new StringBuilder();
                        logger.fine("For " + col.getName() + " (id=" + colId + "|isRootCollection=" + isRootCollection + "|type=" + type + "|isDynamic=" + isDynamic + "|isLocalScope=" + isLocalScope + ") adding query: <<<" + queryString + ">>>");
                        if (queryString != null && !queryString.isEmpty()) {
                            try {
                                Query dynamicQuery = parser.parse(queryString);
                                BooleanQuery dynamicLocal = new BooleanQuery();

                                // Check the scope:
                                if (isLocalScope) {
                                    logger.fine("local scope collection; owned by dataverse " + colOwner.getId());
                                    Query dvOwnerIdQuery = indexServiceBean.constructDvOwnerIdQuery(colOwner);
                                    dynamicLocal.add(dynamicQuery, BooleanClause.Occur.MUST);
                                    dynamicLocal.add(dvOwnerIdQuery, BooleanClause.Occur.MUST);
                                    collectionQueries.add(dynamicLocal);

                                } else if (isSubnetworkScope) {
                                    logger.fine("subnetwork scope collection; owned by dataverse " + colOwner.getId() + ", in the subnetwork "+colOwner.getVdcNetwork().getId());
                                    Query dvOwnerIdQuery = indexServiceBean.constructNetworkOwnerIdQuery(colOwner.getVdcNetwork().getId());
                                    dynamicLocal.add(dynamicQuery, BooleanClause.Occur.MUST);
                                    dynamicLocal.add(dvOwnerIdQuery, BooleanClause.Occur.MUST);
                                    collectionQueries.add(dynamicLocal);

                                } else {
                                    collectionQueries.add(dynamicQuery);
                                }


                            } catch (org.apache.lucene.queryParser.ParseException ex) {
                                Logger.getLogger(StudyListingPage.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            List<Study> studies = col.getStudies();
                            StringBuilder sbInner = new StringBuilder();
                            for (Study study : studies) {
                                logger.fine("- has StudyId: " + study.getId());
                                String idColonId = "id:" + study.getId().toString() + " ";
                                sbInner.append(idColonId);
                            }
                            if (isRootCollection) {
                                List<Long> rootCollectionStudies = vdcService.getOwnedStudyIds(col.getOwner().getId());
                                for (Long id : rootCollectionStudies) {
                                    logger.fine("- has StudyId: " + id);
                                    String idColonId = "id:" + id.toString() + " ";
                                    sbInner.append(idColonId);

                                }
                            }
                            logger.fine("sbInner: " + sbInner.toString());
                            sbOuter.append(sbInner);

                        }
                        logger.fine("sbOuter: " + sbOuter);
                        if (!sbOuter.toString().isEmpty()) {
                            try {
                                parser.setDefaultOperator(QueryParser.OR_OPERATOR);
                                Query staticColQuery = parser.parse(sbOuter.toString());
                                parser.setDefaultOperator(QueryParser.AND_OPERATOR);
                                logger.fine("staticCollectionQuery: " + staticColQuery);
                                collectionQueries.add(staticColQuery);
                            } catch (org.apache.lucene.queryParser.ParseException ex) {
                                Logger.getLogger(AdvSearchPage.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    DvnQuery dvnQuery = new DvnQuery();
                    dvnQuery.setSearchTerms(searchTerms);
                    dvnQuery.setMultipleCollectionQueries(collectionQueries);
                    dvnQuery.constructQuery();
                    resultsWithFacets = indexServiceBean.searchNew(dvnQuery);
                    viewableIds = resultsWithFacets.getMatchIds();
                }
                
            } else {
                logger.fine("in searchWithFacets in AdvSearchPage");
                /* 
                 * This is the logical order here (for both the new,
                 * facets-enabled methods, and the older methods still 
                 * used for the variables and file-level metadata (FITS)
                 * searches: 
                 * We first check if we are inside a DV; then we run a 
                 * DV-scoped search; 
                 * If not, we check if we are inside a subnetwork; 
                 * And if that is not the case either, we'll run a 
                 * full DVN-wide search. 
                 * 
                 * TODO: it should probably be "Collection, DV, Subnetwork, DVN 
                 * instead; i.e., no need to be treating the collection case 
                 * separately, above. [?]
                 *  -- L.A.
                 */
                
                Long rootSubnetworkId = getVDCRequestBean().getVdcNetwork().getId();
                Long currentSubnetworkId = getVDCRequestBean().getCurrentVdcNetwork().getId(); 

                if (isVariableSearch() || isFileLevelMetadataSearch()) {
                    // older, non-facet method

                    if (thisVDC != null) {
                        viewableIds = indexServiceBean.search(thisVDC, searchTerms); 
                    } else {
                        logger.fine("Checking if in subnetwork... [id="+currentSubnetworkId+"]");
                        if (!currentSubnetworkId.equals(rootSubnetworkId)) {
                            SearchTerm subnetworkSearchTerm = makeSubnetworkSearchTerm(currentSubnetworkId);
                            if (subnetworkSearchTerm != null) {
                                searchTerms.add(subnetworkSearchTerm);
                            }
                        }
                        
                        viewableIds = indexServiceBean.search((VDC)null, searchTerms);
                        
                    }
                    // TODO: 
                    // Make sure the variable and file metadata searches are 
                    // properly scoped for subnetworks as well. -- L.A.
                } else {
                    
                    DvnQuery dvnQuery = new DvnQuery();
                    
                    if (thisVDC != null) {
                        logger.fine("Running DVN-wide search from AdvSearchPage");
                        
                        dvnQuery.setVdc(thisVDC);

                        List<Query> collectionQueries = new ArrayList<Query>();
                        dvnQuery.setDvOwnerIdQuery(indexServiceBean.constructDvOwnerIdQuery(dvnQuery.getVdc()));
                        collectionQueries = indexServiceBean.getCollectionQueries(dvnQuery.getVdc());
                        if (!collectionQueries.isEmpty()) {
                            logger.fine("collectionQueries: " + collectionQueries);
                            dvnQuery.setCollectionQueries(collectionQueries);
                        } else {
                            logger.fine("empty collectionQueries");
                        }
                    } else if (!currentSubnetworkId.equals(rootSubnetworkId)) {
                        logger.fine("Searching only a subnetwork");
                        VDCNetwork vdcNetwork = getVDCRequestBean().getVdcNetwork();
                        String vdcNetworkName = vdcNetwork.getName();
                        logger.fine("vdcNetwork name: " + vdcNetworkName);
                        
                        /*
                         * Brute force implementation: 
                         * going through every DV in the subnetwork, and adding 
                         * all the owner id and collection queries to the search
                         * term query:
                         * -- L.A.
                        Collection<VDC> vdcs = vdcNetwork.getNetworkVDCs();
                        List<Query> subNetworkCollectionQueries = new ArrayList<Query>();
                        List<Query> subNetworkDvMemberQueries = new ArrayList<Query>();
                        for (VDC vdc : vdcs) {
                            String name = vdc.getName();
                            logger.fine("adding queries for: " + name);
                            Query dvnSpecificQuery = indexServiceBean.constructDvOwnerIdQuery(vdc);
                            logger.fine("adding dvnSpecific query:" + dvnSpecificQuery);
                            subNetworkDvMemberQueries.add(dvnSpecificQuery);
                            List<Query> vdcCollectionQueries = indexServiceBean.getCollectionQueries(vdc);
                            for (Query collectionQuery : vdcCollectionQueries) {
                                logger.fine("adding collection query: " + collectionQuery);
                            }
                            subNetworkCollectionQueries.addAll(vdcCollectionQueries);
                        }*/
                        
                        Query subNetworkQuery = indexServiceBean.constructNetworkIdQuery(currentSubnetworkId);
                        dvnQuery.setSubNetworkQuery(subNetworkQuery); 
                    } else {
                        logger.fine("Running DVN-wide search from AdvSearchPage");
                    }
                    
                    // Now, run the final search: 
                    
                    dvnQuery.setSearchTerms(searchTerms);
                    dvnQuery.constructQuery();
                    dvnQuery.setClearPreviousFacetRequests(true); // is this still needed?
                    resultsWithFacets = indexServiceBean.searchNew(dvnQuery);
                    viewableIds = resultsWithFacets.getMatchIds();
                    
                }
            }

            StudyListing sl = new StudyListing(StudyListing.SEARCH);
            sl.setVdcId(getVDCRequestBean().getCurrentVDCId());
            sl.setSearchTerms(searchTerms);
            sl.setResultsWithFacets(resultsWithFacets);
            if (isVariableSearch()) {
                Map variableMap = new HashMap();
                List studies = new ArrayList();
                varService.determineStudiesFromVariables(viewableIds, studies, variableMap);
                sl.setStudyIds(studies);
                sl.setVariableMap(variableMap);

            } else if (isFileLevelMetadataSearch()) {
              
                Map fileMap = new HashMap();
                List studies = new ArrayList();
                studyService.determineStudiesFromFiles(viewableIds, studies, fileMap);
                sl.setStudyIds(studies);
                sl.setFileMap(fileMap);
            } else {
                sl.setStudyIds(viewableIds);
            }

            String studyListingIndex = StudyListing.addToStudyListingMap(sl, getSessionMap());
            return "/StudyListingPage.xhtml?faces-redirect=true&studyListingIndex=" + studyListingIndex + getContextSuffix();
        } else {
            return "";
        }

    }

    public boolean isDateItem(String s) {
        // there is an issue with the date items, so for the time being, we are treating all fields as non date
        //boolean retVal = s != null && (s.equalsIgnoreCase("Production Date") || s.equalsIgnoreCase("Distribution Date") || s.equalsIgnoreCase("Date of Deposit") || s.startsWith("Time Period Covered"));
        //return retVal;
        return false;
    }

    public boolean isDateItem1() {
        return isDateItem(dropdown1.getValue().toString());
    }

    public boolean isDateItem2() {
        return isDateItem(dropdown3.getValue().toString());
    }

    public boolean isDateItem3() {
        return isDateItem(dropdown5.getValue().toString());
    }

    public boolean isDateItem4() {
        return isDateItem(dropdown7.getValue().toString());
    }

    /*
    public String indexAll() {
        indexServiceBean.indexAll();
        return "success";
    }
*/
    // 
    // What is this for? -- L.A. 
    protected String buildQuery() {
        StringBuffer query = new StringBuffer();
        if (((String) textField1.getValue()).length() > 0) {
            query.append(advSearchFieldIndexName((String) dropdown1.getValue()) + " " + operatorToken((String) dropdown9.getValue()) + " " + (String) textField1.getValue());
        }
        if (((String) textField2.getValue()).length() > 0) {
            query.append(" AND " + advSearchFieldIndexName((String) dropdown3.getValue()) + " " + operatorToken((String) dropdown4.getValue()) + " " + (String) textField2.getValue());
        }
        if (((String) textField3.getValue()).length() > 0) {
            query.append(" AND " + advSearchFieldIndexName((String) dropdown5.getValue()) + " " + operatorToken((String) dropdown6.getValue()) + " " + (String) textField3.getValue());
        }
        if (((String) textField4.getValue()).length() > 0) {
            query.append(" AND " + advSearchFieldIndexName((String) dropdown7.getValue()) + " " + operatorToken((String) dropdown8.getValue()) + " " + (String) textField4.getValue());
        }
        return query.toString();
    }

    protected List<SearchTerm> buildSearchTermList() {
        List<SearchTerm> searchTerms = new ArrayList();
        if (((String) textField1.getValue()).length() > 0) {
            SearchTerm searchTerm1 = new SearchTerm();
            searchTerm1.setFieldName(advSearchFieldIndexName((String) dropdown1.getValue()));
            searchTerm1.setOperator(operatorToken((String) dropdown9.getValue()));
            searchTerm1.setValue((String) textField1.getValue());
            searchTerms.add(searchTerm1);
        }
        if (((String) textField2.getValue()).length() > 0) {
            SearchTerm searchTerm2 = new SearchTerm();
            searchTerm2.setFieldName(advSearchFieldIndexName((String) dropdown3.getValue()));
            searchTerm2.setOperator(operatorToken((String) dropdown4.getValue()));
            searchTerm2.setValue((String) textField2.getValue());
            searchTerms.add(searchTerm2);
        }
        if (((String) textField3.getValue()).length() > 0) {
            SearchTerm searchTerm3 = new SearchTerm();
            searchTerm3.setFieldName(advSearchFieldIndexName((String) dropdown5.getValue()));
            searchTerm3.setOperator(operatorToken((String) dropdown6.getValue()));
            searchTerm3.setValue((String) textField3.getValue());
            searchTerms.add(searchTerm3);
        }
        if (((String) textField4.getValue()).length() > 0) {
            SearchTerm searchTerm4 = new SearchTerm();
            searchTerm4.setFieldName(advSearchFieldIndexName((String) dropdown7.getValue()));
            searchTerm4.setOperator(operatorToken((String) dropdown8.getValue()));
            searchTerm4.setValue((String) textField4.getValue());
            searchTerms.add(searchTerm4);
        }
        if (variableInfoList.size() > 0){
            
            for (SearchTerm searchTerm : variableInfoList) {
                if ( searchTerm.getValue().length() > 0 ){
                    searchTerms.add(searchTerm);
                    setVariableSearch(true);
                }
            }
        }
        if (fileMetaSearchValue.getValue() != null &&
                !(fileMetaSearchValue.getValue().equals(""))) {
            if (dropdownFileMeta.getValue() != null ) {
                SearchTerm fileLevelMetaSearchTerm = new SearchTerm();
                fileLevelMetaSearchTerm.setFieldName(fileMetadataFieldIndexName((String)dropdownFileMeta.getValue()));
                fileLevelMetaSearchTerm.setOperator("="); 
                fileLevelMetaSearchTerm.setValue((String)fileMetaSearchValue.getValue());
                searchTerms.add(fileLevelMetaSearchTerm);
                setFileLevelMetadataSearch(true);
            }
            
        }
        return searchTerms;
    }
    
    SearchTerm makeSubnetworkSearchTerm(Long subNetworkId) {
        SearchTerm subNetworkTerm = new SearchTerm();
        
        subNetworkTerm.setFieldName("dvNetworkId");
        subNetworkTerm.setOperator("="); 
        subNetworkTerm.setValue(subNetworkId.toString());
        
        return subNetworkTerm;
        
    }

    protected String operatorToken(String operator) {
        return (String) operatorMap.get(operator);
    }

    protected String advSearchFieldIndexName(String displayName) {
        return (String) advSearchFieldMap.get(displayName);
    }

    protected String fileMetadataFieldIndexName(String field) {
        return (String) fileMetadataFieldMap.get(field);
    }
    
    private boolean isValid(String dateString, String pattern) {
        boolean valid;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            sdf.parse(dateString);
            valid = true;
        } catch (ParseException e) {
            valid = false;
        }
        return valid;
    }

    public boolean validateAllSearchCriteria() {
            
            if (hasCatalogSearchCriteria() && !hasCatalogSearchCriteriaWithContains()) {
                FacesMessage message = new FacesMessage("Must enter at least one 'Contains' for Cataloging Information Search.");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(textField1.getClientId(fc), message);
                return false;
            }

            if (!hasVariableSearchCriteria() 
                    && !hasFileLevelMetadataSearchCriteria() 
                    && !hasCatalogSearchCriteria()) {
                //((UIInput)gridPanel1).setValid(false);
                FacesMessage message = new FacesMessage("Must enter some Search Criteria.");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(textField1.getClientId(fc), message);
                return false;
            }
            return true;
    }
   
    private boolean hasCatalogSearchCriteria(){

        if (((String) textField1.getValue()).length() > 0) {
            return true;
        }
        if (((String) textField2.getValue()).length() > 0  ) {
            return true;
        }
        if (((String) textField3.getValue()).length() > 0) {
            return true;
        }
        if (((String) textField4.getValue()).length() > 0) {
            return true;
        }
        return false;
    }

    private boolean hasCatalogSearchCriteriaWithContains(){

        if (((String) textField1.getValue()).length() > 0) {
            if (operatorToken((String) dropdown9.getValue()).equalsIgnoreCase("=")){
                return true;
            }
        }
        if (((String) textField2.getValue()).length() > 0 ) {
            if (operatorToken((String) dropdown4.getValue()).equalsIgnoreCase("=")){
                return true;
            }
        }
        if (((String) textField3.getValue()).length() > 0) {
            if (operatorToken((String) dropdown6.getValue()).equalsIgnoreCase("=")){
                return true;
            }
        }
        if (((String) textField4.getValue()).length() > 0) {
            if (operatorToken((String) dropdown8.getValue()).equalsIgnoreCase("=")){
                return true;
            }
        }
        return false;
    }

   
    protected boolean hasVariableSearchCriteria(){

        if (variableInfoList.size() > 0){
            for (SearchTerm searchTerm : variableInfoList) {
                if ( searchTerm.getValue().length() > 0 ){
                    setVariableSearch(true);
                    return true;
                }
            }
        }

        return false;
    }
    
    protected boolean hasFileLevelMetadataSearchCriteria (){
         if (dropdownFileMeta.getValue() != null &&
                 fileMetaSearchValue.getValue() != null &&
                !(fileMetaSearchValue.getValue().equals(""))) {
             return true; 
         }
        

        return false;
    }    


    public void validateDate(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String dateString = (String) value;
        boolean valid = false;

        String monthDayYear = "yyyy-MM-dd";
        String monthYear = "yyyy-MM";
        String year = "yyyy";

        if (dateString.length() == 4) {
            valid = isValid(dateString, year);
        } else if (dateString.length() > 4 && dateString.length() <= 7) {
            valid = isValid(dateString, monthYear);
        } else if (dateString.length() > 7) {
            valid = isValid(dateString, monthDayYear);
        }

        if (!valid) {
            ((UIInput) toValidate).setValid(false);

            FacesMessage message = new FacesMessage("Invalid Date Format.  Valid formats are YYYY-MM-DD, YYYY-MM, or YYYY.");
            context.addMessage(toValidate.getClientId(context), message);
        }

    }
      public void addRow(ActionEvent ae) {

        HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();

        if (dataTable.equals(dataTableVariableInfo)) {
            this.variableInfoList.add(dataTable.getRowIndex()+1, initVariableSearchTerm());
        }
    }

        public void removeRow(ActionEvent ae) {

            HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();
            if (dataTable.getRowCount()>1) {
                List data = (List)dataTable.getValue();
                int i = dataTable.getRowIndex();
                data.remove(i);
            }
    }

}

