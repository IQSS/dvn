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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web;


import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.visualization.VarGroup;
import edu.harvard.iq.dvn.core.visualization.VarGroupType;
import edu.harvard.iq.dvn.core.visualization.VarGrouping;
import edu.harvard.iq.dvn.core.visualization.VarGrouping.GroupingType;
import edu.harvard.iq.dvn.core.visualization.VisualizationServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.ext.HtmlSelectOneRadio;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.PngImage;

import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.visualization.DataVariableMapping;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import edu.harvard.iq.dvn.ingest.dsb.FieldCutter;
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnJavaFieldCutter;
import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import javax.inject.Named;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;




/**
 *
 * @author skraffmiller
 */
@ViewScoped
@Named("ExploreDataPage")
public class ExploreDataPage extends VDCBaseBean  implements Serializable {
    @EJB
    VariableServiceLocal varService;
    @EJB
    VisualizationServiceLocal      visualizationService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    VDCServiceLocal vdcService;


    private String measureLabel;
    private String measureTypeCue;

    private String lineLabel;

    private String lineColor;
    private String dataTableId = "";
    private List <VarGrouping> varGroupings = new ArrayList();
    private List <VarGroupingUI> filterGroupings = new ArrayList();

    private List <String> filterStrings = new ArrayList();
    private List <SelectItem> selectMeasureItems = new ArrayList();
    private List <SelectItem> selectMeasureGroupTypes = new ArrayList();
    private List <SelectItem> selectBeginYears = new ArrayList();
    private List <SelectItem> selectIndexDate = new ArrayList();

    private List <SelectItem> selectEndYears = new ArrayList();    
    private List <SelectItem> selectView = new ArrayList();

    public List<SelectItem> getSelectView() {
        return selectView;
    }
    
    private List <VisualizationLineDefinition> vizLines = new ArrayList();

    private Long selectedMeasureId = new Long(0);
    private boolean selectedMeasureHasFilterTypes = false;
    private boolean selectedMeasureHasFilters = false;

    private Long selectedFilterGroupId = new Long(0);
    
    private Integer selectedIndex = new Integer(0);

    public Integer getSelectedIndex() {
        return selectedIndex;
    }
    private int groupTypeId = 0;
    private List <DataVariable> dvList;
    private List <DataVariableMapping> sourceList = new ArrayList();;
    private List <String> measureString= new ArrayList();
    private DataVariable xAxisVar;
    private DataTable dt = new DataTable();
    private DataVariable dataVariableSelected = new DataVariable();
    private String dtColumnString = new String();
    private String columnString = new String();
    private String csvColumnString = new String();
    private String imageColumnString = new String();
    
    private List <VarGroup> allMeasureGroups = new ArrayList();
    private List <VarGroup> allFilterGroups = new ArrayList();

    private Long numberOfColumns =  new Long(0);
    private String dataString = "";
    private String indexedDataString = "";
    private String csvString = "";
    private String indexDate = "";
    private String sources = "";
    private String sourceString = "";

    private String imageSourceFooter = "";
    private String imageSourceFooterNoYLabel = "";
    private String displaySourceFooter = "";
    private String displaySourceFooterNoYLabel = "";
    private String imageAxisLabel = "";
    private String imageAxisLabelNoYLabel = "";   

    private boolean displayIndexes = false;
    private boolean includeImage = true;
    private boolean includePdf = true;
    private boolean includeExcel = true;
    private boolean includeCSV = true;
    private boolean imageAvailable = false;

    private boolean dataTableAvailable = false;           
    private boolean showFilterGroupTypes = false;
    
    private boolean titleEdited = false;
    private boolean sourceEdited = false;
    private Long displayType = new Long(0);
    private String startYear = "0";
    private String endYear = "3000";
    private Study studyIn = new Study();
    private StudyFile sf = null;
    private StudyUI studyUI;
    private String fileName = "";
    private String graphTitle = "";
    private String imageURL = "";
    private Long studyId = new Long(0);
    private Long versionNumber;
    private VDC vdc = null;
    private List filterGroupingMeasureAssociation = new ArrayList();
    private List groupingTypeAssociation = new ArrayList();
    private List filterGroupMeasureAssociation = new ArrayList();
    private List <VarGrouping> allVarGroupings = new ArrayList();
    private boolean showVariableInfoPopup = false;
    private String variableLabel = "";
    private boolean variableLabelLink = false;
    private String xAxisLabel = "";
    private String sourceLineLabel = "";
    private Long studyFileId;

    private String yAxisLabel = "";

    private boolean displayLegend = true;
    private int legendInt = 2;   
    private int heightInt = 2;


    private Integer defaultView = new Integer(2);
    private String[] transformedData;
    private String transformedDataOut;
    private String[] transformedDataIndexed;
    private String transformedDataIndexedOut;
    private String forcedIndexMessage; 
    private String dataNotAddedMessage;
    private Float lowValStandard = new Float(0);
    private Float lowValIndex = new Float (100);
    private Float highValStandard = new Float (0);

    private Float highValIndex = new Float(0);

    public ExploreDataPage() {
        
    }
    
    public void preRenderView() {
        super.preRenderView();
        // add javascript call on each partial submit to init the edit line functionality
        JavascriptContext.addJavascriptCall(getFacesContext(), "initGraphResultsVizLineEdit();");
    }    

    @Override
    public void init() {
        super.init();        
        studyFileId = new Long( getVDCRequestBean().getRequestParam("fileId"));       
        setUp();
     }
    
    private void setUp(){
        
        legendInt = 2;
        visualizationService.setDataTableFromStudyFileId(studyFileId);
        studyIn = visualizationService.getStudyFromStudyFileId(studyFileId);
        studyId = studyIn.getId();
        dt = visualizationService.getDataTable();
        Study thisStudy = dt.getStudyFile().getStudy();
        studyUI = new StudyUI(thisStudy);
        sf = dt.getStudyFile();
        fileName = sf.getFileName();
        dvList = dt.getDataVariables();
        allVarGroupings = dt.getVarGroupings();
        
        // Get current VDC, if available, from the 
        // VDC request bean. 
        // We will need it when incrementing the download
        // count:
        vdc = getVDCRequestBean().getCurrentVDC();


        measureLabel = loadMeasureLabel();  
        if (dt.getVisualizationDisplay() != null && dt.getVisualizationDisplay().getMeasureTypeLabel() != null ){
            measureTypeCue = loadMeasureTypeCue();
        } else {
            measureTypeCue = measureLabel + " Type ";
        }
        
        selectMeasureGroupTypes = loadSelectMeasureGroupTypes();        
        selectMeasureItems = loadSelectMeasureItems(0);
        
        selectView = loadSelectViewItems();
        loadAllFilterGroupings();
        loadAllFilterGroupTypes();
        loadAllMeasureGroups();
        loadAllFilterGroups();
        if (!dt.getVisualizationDisplay().getSourceInfoLabel().isEmpty()){
            setSourceLineLabel(dt.getVisualizationDisplay().getSourceInfoLabel());
        } else {
            setSourceLineLabel("Source Info"); 
        }
        xAxisVar =  visualizationService.getXAxisVariable(dt.getId());
        for (DataVariableMapping mapping : xAxisVar.getDataVariableMappings()){
             if (mapping.isX_axis())xAxisLabel = mapping.getLabel();
        }               
    }
    
    private void reInit(){
        setUp();
    }
    
    private void refreshSettings(){        
        titleEdited = false;
        displayType = new Long(0);
        groupTypeId = 0;
        startYear = "0";
        endYear = "3000";
        legendInt = 2;
        this.heightInt = 2;
        graphTitle = "";
        imageURL = "";
        showVariableInfoPopup = false;
        variableLabel = "";
        variableLabelLink = false;
        displayLegend = true;
        setDisplayIndexes(false);
        loadSelectViewItems();
        selectedMeasureId = new Long (0);
        selectedIndex = new Integer(0);
        tabSet1.setSelectedIndex(selectedIndex);
        groupTypeId = 0;
        this.selectMeasureItems = loadSelectMeasureItems(0);
        lineLabel = "";
        displayType = new Long(defaultView);     
    }

    private PanelTabSet tabSet1 = new PanelTabSet();

    public PanelTabSet getTabSet1() {
        return tabSet1;
    }

    public void setTabSet1(PanelTabSet tabSet1) {
        this.tabSet1 = tabSet1;
    }


    public List getVarGroupings() {
        return varGroupings;
    }

    public void setVarGroupings(List varGroupings) {
        this.varGroupings = varGroupings;
    }

    public List<VarGroupingUI> getFilterGroupings() {
        return filterGroupings;
    }

    public void setFilterGroupings(List<VarGroupingUI> filterGroupings) {
        this.filterGroupings = filterGroupings;
    }


    private String loadMeasureLabel() {
        Iterator iterator = allVarGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();

            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                return varGrouping.getName();
            }

        }
        return "Measure";
    }
    
    private String loadMeasureTypeCue() {
        String retVal =  measureLabel + " Type ";    
        if ( !dt.getVisualizationDisplay().getMeasureTypeLabel().isEmpty()){
            retVal = dt.getVisualizationDisplay().getMeasureTypeLabel();
        } 
        return retVal;
    }

    private void loadFilterGroupings(){
        
        
        filterStrings.clear();
        filterGroupings.clear();
        List <VarGrouping> localVGList = new ArrayList();
        Iterator i = filterGroupingMeasureAssociation.listIterator();
        int count = 0;
        while (i.hasNext()){
            Object test = i.next();
            if ( count % 2 == 0 ){
                Long id = (Long) test;
                if (id.equals(selectedMeasureId)){
                    localVGList.add((VarGrouping) i.next());
                    count++;
                }
            }
            count++;
        }


        for (VarGrouping varGrouping: localVGList){

            if (varGrouping.getGroupingType().equals(GroupingType.FILTER)){
                
                List <VarGroup> filterGroups = new ArrayList();
                List <VarGroupTypeUI> filterGroupTypes = new ArrayList();
                VarGroupingUI vgUI = new VarGroupingUI();
                vgUI.setVarGrouping(varGrouping);
                loadFilterGroups(filterGroups);
                loadFilterGroupTypes(filterGroupTypes, varGrouping.getId());
                vgUI.setVarGroupTypesUI(filterGroupTypes);
                selectedMeasureHasFilters = true;
                if (!filterGroupTypes.isEmpty()){
                    selectedMeasureHasFilterTypes = true;
                }
                
                vgUI.setSelectedGroupId(new Long (0));
                filterGroupings.add(vgUI);
            }

        }
    }

    private void loadAllFilterGroupings(){
        filterGroupingMeasureAssociation.clear();
        
        for (VarGrouping varGroupingTest: allVarGroupings){
            if (varGroupingTest.getGroupingType().equals(GroupingType.MEASURE)){
                for (VarGroup varGroup : varGroupingTest.getVarGroups() ){
                     List <VarGrouping> varGroupingsBack = visualizationService.getFilterGroupingsFromMeasureId(varGroup.getId());
                     for(VarGrouping varGroupingBack: varGroupingsBack){
                         filterGroupingMeasureAssociation.add(varGroup.getId());
                         filterGroupingMeasureAssociation.add(varGroupingBack);
                     }
                     List <VarGroup> varGroupsBack = visualizationService.getFilterGroupsFromMeasureId(varGroup.getId());
                     for(VarGroup varGroupBack: varGroupsBack){
                         filterGroupMeasureAssociation.add(varGroup.getId());
                         filterGroupMeasureAssociation.add(varGroupBack);
                     }
                }
            }

        }
    }
    
    private void loadAllFilterGroups(){

        allFilterGroups.clear();
        for (VarGrouping varGroupingTest: allVarGroupings){
            if (varGroupingTest.getGroupingType().equals(GroupingType.FILTER)){
                for (VarGroup varGroup : varGroupingTest.getVarGroups() ){
                    allFilterGroups.add(varGroup);
                }
            }

        }
    }
    
    private void loadAllMeasureGroups(){
        allMeasureGroups.clear();
        
        for (VarGrouping varGroupingTest: allVarGroupings){
            if (varGroupingTest.getGroupingType().equals(GroupingType.MEASURE)){
                for (VarGroup varGroup : varGroupingTest.getVarGroups() ){
                    allMeasureGroups.add(varGroup);
                }
            }

        }
    }

    private void loadAllFilterGroupTypes(){


        for (VarGrouping varGroupingTest: allVarGroupings){
            if (varGroupingTest.getGroupingType().equals(GroupingType.FILTER)){
                     List <VarGroupType> varGroupTypesBack = visualizationService.getGroupTypesFromGroupingId(varGroupingTest.getId());

                     for(VarGroupType varGroupTypeBack: varGroupTypesBack){
                         groupingTypeAssociation.add(varGroupingTest.getId());
                         groupingTypeAssociation.add(varGroupTypeBack);
                     }

            }

        }
    }


    private void loadFilterGroups(List <VarGroup> inList){
        inList.clear();
         List <VarGroup> localVGList = new ArrayList();

        Iterator i = filterGroupMeasureAssociation.listIterator();
        int count = 0;
        while (i.hasNext()){
            Object test = i.next();
            if ( count % 2 == 0 ){
                Long id = (Long) test;
                if (id.equals(selectedMeasureId)){
                    localVGList.add((VarGroup) i.next());
                    count++;
                }
            }

            count++;
        }

        for (VarGroup varGroup: localVGList ){
            inList.add(varGroup);
        }
    }

    private List <VarGroup> getFilterGroupsFromMeasureId(Long MeasureId){
    
        
        List returnList = new ArrayList();
         List <VarGroup> localVGList = new ArrayList();

        Iterator i = filterGroupMeasureAssociation.listIterator();
        int count = 0;
        while (i.hasNext()){
            Object test = i.next();
            if ( count % 2 == 0 ){
                Long id = (Long) test;
                if (id.equals(MeasureId)){
                    localVGList.add((VarGroup) i.next());
                    count++;
                }
            }

            count++;
        }

        for (VarGroup varGroup: localVGList ){
            returnList.add(varGroup);
        }

       return returnList;
    }


    private void loadFilterGroupTypes(List <VarGroupTypeUI> inList, Long groupingId){
        

        List <VarGroupType> localVGList = new ArrayList();

        Iterator i = groupingTypeAssociation.listIterator();
        int count = 0;
        while (i.hasNext()){
            Object test = i.next();
            if ( count % 2 == 0 ){
                Long id = (Long) test;
                if (id.equals(groupingId)){
                    localVGList.add((VarGroupType) i.next());
                    count++;
                }
            }

            count++;
        }
        
        List <VarGroup> varGroupsAll = (List<VarGroup>)  getFilterGroupsFromMeasureId(selectedMeasureId);
            List <VarGroupType> varGroupTypesAll = new ArrayList();
            for (VarGroup varGroupFilter:varGroupsAll){
                ;
                for (VarGroupType vgt: varGroupFilter.getGroupTypes()){
                    varGroupTypesAll.add(vgt);
                }
            }


        for (VarGroupType varGroupType: localVGList ){
            if (varGroupTypesAll.contains(varGroupType) ){
                VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
                varGroupTypeUI.setVarGroupType(varGroupType);
                varGroupTypeUI.setEnabled(false);
                inList.add(varGroupTypeUI);
            }
        }
    }


    public void setMeasureLabel(String measureLabel) {
        this.measureLabel = measureLabel;
    }

    public String getMeasureLabel() {
        return measureLabel;
    }
    
    
    public String getMeasureTypeCue() {
        return measureTypeCue;
    }

    public void setMeasureTypeCue(String measureTypeCue) {
        this.measureTypeCue = measureTypeCue;
    }
    
    
    public boolean isSelectedMeasureHasFilterTypes() {
        return selectedMeasureHasFilterTypes;
    }

    public void setSelectedMeasureHasFilterTypes(boolean selectedMeasureHasFilterTypes) {
        this.selectedMeasureHasFilterTypes = selectedMeasureHasFilterTypes;
    }

    public List<String> getFilterStrings() {
        return filterStrings;
    }

    public void setFilterStrings(List<String> filterStrings) {
        this.filterStrings = filterStrings;
    }

    public List<String> getMeasureString() {
        return measureString;
    }

    public void setMeasureString(List<String> measureString) {
        this.measureString = measureString;
    }

    public List<SelectItem> getSelectMeasureItems() {
        return this.selectMeasureItems;
    }

    public void resetFiltersForMeasure(ValueChangeEvent ae){ 
        selectedMeasureId = (Long) ae.getNewValue();
        selectedMeasureHasFilterTypes = false;
        selectedMeasureHasFilters = false;
        if (selectedMeasureId == null ){
            selectedMeasureId = new Long(0);
        }
        if (selectedMeasureId != null) {
            loadFilterGroupings();
            
        }
        
        if (!selectedMeasureHasFilters && selectedMeasureId > 0) {
            VarGroup measure = visualizationService.getGroupFromId(selectedMeasureId);
            inputLineLabel.setValue(measure.getName());
        } else {
            inputLineLabel.setValue(""); 
        }
       
        // commented out because we don't need to reset the visualization when all we are doing is selecting the filter
        //callDrawVisualization();
        
        // added the following to fix issue where you would change from one measure to anoter, but the filter values were being kept
        getFacesContext().renderResponse();
    }
    
   
    public void updateLineLabelForFilter(ValueChangeEvent ae){
       Long filterId =  (Long) ae.getNewValue();
       
       VarGroup filterGroup = getFilterGroupFromId(filterId);
       
       String tmpLineLabel = ""; 
       
       VarGrouping updatedGrouping = new VarGrouping();
       
       if (filterGroup != null){
           updatedGrouping =  filterGroup.getGroupAssociation();
       }
       
       for (VarGroupingUI varGroupingUI: filterGroupings){
                if (updatedGrouping.equals(varGroupingUI.getVarGrouping())){
                   varGroupingUI.setSelectedGroupId(filterId); 
                }
                Long gfilterId = varGroupingUI.getSelectedGroupId();
                if (gfilterId > 0){
                    if (tmpLineLabel.isEmpty()) {
                        tmpLineLabel += getFilterGroupFromId(gfilterId).getName();  
                    } else {
                        tmpLineLabel += ", " + getFilterGroupFromId(gfilterId).getName(); 
                    }
                      
                } 
        }
       
      
       getInputLineLabel().setValue(tmpLineLabel);                      
    }
    
    public List<SelectItem> loadSelectMeasureItems(int grouptype_id) {
        boolean resetSelected = true;

        List selectItems = new ArrayList<SelectItem>();
        List <VarGroup> varGroups = new ArrayList();
        varGroupings = dt.getVarGroupings();
        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){

                if (grouptype_id == 0) {
                    varGroups = (List<VarGroup>) varGrouping.getVarGroups();
                } else {
                    List <VarGroup> varGroupsTest = (List<VarGroup>) varGrouping.getVarGroups();
                    for (VarGroup vgTest: varGroupsTest){
                       List <VarGroupType> varGroupTypes = vgTest.getGroupTypes();
                       for (VarGroupType vgTypeTest: varGroupTypes ){
                           if(vgTypeTest.getId().intValue() == grouptype_id ){
                               varGroups.add(vgTest);
                           }
                       }
                    }
                }
 
                for(VarGroup varGroup: varGroups) {
                    boolean added = false;
                    Long testId  = varGroup.getId();
                    for (Object testItem:  selectItems){
                         SelectItem testListSI = (SelectItem) testItem;
                         if(testId.equals(testListSI.getValue())){
                             added = true;
                         }
                    }
                    if (!added ){
                        selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                        if (varGroup.getId().equals(selectedMeasureId)){

                        }
                        added = true;
                    }

                }
            }
        }
        if (resetSelected){
            selectedMeasureId = new Long(0);
        }
        return selectItems;
    }

    public List<SelectItem> loadSelectViewItems() {
        defaultView = 0;
        boolean image = false;
        boolean flash = false;
        boolean datatable = false;
        
        List selectItems = new ArrayList<SelectItem>();


        
        int defaultViewDisplay = dt.getVisualizationDisplay().getDefaultDisplay();
        

        switch (defaultViewDisplay) {
            case 0:  defaultView = 2;  
                        selectItems.add(new SelectItem(2, "Image Graph"));
                        imageAvailable = true;
                        image = true;
                        break;
            case 1:  defaultView = 1;   
                        selectItems.add(new SelectItem(1, "Flash Graph"));  
                        flash = true;
                        break;
            case 2:  defaultView = 3;   
                        selectItems.add(new SelectItem(3, "Data Table"));
                        dataTableAvailable = true; 
                        datatable = true;
                        break;
            default:    break;
        }
        
        if ( !image && dt.getVisualizationDisplay().isShowImageGraph()) {
            selectItems.add(new SelectItem(2, "Image Graph"));
            imageAvailable = true;
        }
        if (!flash && dt.getVisualizationDisplay().isShowFlashGraph()) {
            selectItems.add(new SelectItem(1, "Flash Graph"));  
        }
        if (!datatable && dt.getVisualizationDisplay().isShowDataTable()) {
            selectItems.add(new SelectItem(3, "Data Table"));
            dataTableAvailable = true;  
        }
        
        includeImage = true;
        includePdf = true;
        includeExcel = true;
        includeCSV = true;

        if (!dt.getVisualizationDisplay().isShowImageGraph()) {
            includeImage = false;
            includePdf = false;
        }

        if (!dt.getVisualizationDisplay().isShowDataTable()) {
            includeExcel = false;
        }
        
        return selectItems;
    }
    
    public List<SelectItem> loadSelectMeasureGroupTypes() {
        List selectItems = new ArrayList<SelectItem>();
        for(VarGrouping varGrouping: allVarGroupings) {           
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                List <VarGroupType> varGroupTypes = (List<VarGroupType>) varGrouping.getVarGroupTypes();
                for(VarGroupType varGroupType: varGroupTypes) {
                    if (varGroupType.getGroups().size() > 0) {
                        selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                    }
                }
            }
        }
        return selectItems;
    }

    public List<SelectItem> loadSelectFilterGroupTypes() {
        List selectItems = new ArrayList<SelectItem>();

        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            if (varGrouping.getGroupingType().equals(GroupingType.FILTER)){
                List <VarGroupType> varGroupTypes = (List<VarGroupType>) varGrouping.getVarGroupTypes();
                for(VarGroupType varGroupType: varGroupTypes) {
                    selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                }
            }
        }
        return selectItems;
    }
    
    public List<SelectItem> getSelectFilterGroups(Long groupingId) {
        if (!selectedMeasureId.equals(new Long(0))){
          
            return getFilterGroupsWithMeasure(groupingId);
        }
        return null;
    }


    public List<SelectItem> getSelectFilterGroupTypes(Long groupingId) {
        showFilterGroupTypes = false;
        List selectItems = new ArrayList<SelectItem>();
        if (!selectedMeasureId.equals(new Long(0))){

            List <VarGroup> varGroupsAll = (List<VarGroup>)  getFilterGroupsFromMeasureId(selectedMeasureId);
            List <VarGroupType> varGroupTypesAll = new ArrayList();
            for (VarGroup varGroupFilter:varGroupsAll){
                ;
                for (VarGroupType vgt: varGroupFilter.getGroupTypes()){
                    varGroupTypesAll.add(vgt);
                }
            }
            
            List <VarGroupType> varGroupTypes = (List<VarGroupType>)  visualizationService.getGroupTypesFromGroupingId(new Long(groupingId));
                for(VarGroupType varGroupType: varGroupTypes) {
                    if (varGroupTypesAll.contains(varGroupType) ){
                        selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                        showFilterGroupTypes = true;
                    }
                    
                }
            
        }
        return selectItems;
    }

    public List<VarGroupType> loadFilterGroupTypes() {
        Long groupingId = new Long(0);

        List selectItems = new ArrayList<VarGroupType>();

                List <VarGroupType> varGroupTypes = (List<VarGroupType>)  visualizationService.getGroupTypesFromGroupingId(new Long(groupingId));
                for(VarGroupType varGroupType: varGroupTypes) {
                    selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                }

        return selectItems;
    }


    private List<SelectItem> getFilterGroupsWithMeasure(Long groupingId){

         List selectItems = new ArrayList<SelectItem>();
          boolean addAll = false;
          List <VarGroup> multipleSelections = new ArrayList <VarGroup>();
          List <VarGroup> varGroupsAll = (List<VarGroup>)  getFilterGroupsFromMeasureId(selectedMeasureId);
              for(VarGroup varGroup: varGroupsAll) {
                  boolean added = false;
                  for (VarGroupingUI varGroupingUI :filterGroupings){
                      if (varGroupingUI.getVarGrouping().getId().equals(groupingId)){
                        List <VarGroupTypeUI> varGroupTypesUI = varGroupingUI.getVarGroupTypesUI();
                        if (!varGroupTypesUI.isEmpty()){
                            boolean allFalse = true;
                            for (VarGroupTypeUI varGroupTypeUI: varGroupTypesUI){
                                allFalse &= !varGroupTypeUI.isEnabled();
                            }
                            if (allFalse){
                                
                                       for (VarGroup varGroupTest: varGroupsAll) {
                                            if (!added && varGroupTest.getId().equals(varGroup.getId())  && varGroup.getGroupAssociation().equals(varGroupingUI.getVarGrouping()) ){
                                                selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                                                added = true;
                                            }
                                        }


                            }
                            if (!allFalse){
                                int countEnabled = 0;
                                for (VarGroupTypeUI varGroupTypeUI: varGroupTypesUI){
                                    if (varGroupTypeUI.isEnabled()){
                                        countEnabled++;
                                    }
                                }
                                if( countEnabled == 1){
                                    for (VarGroupTypeUI varGroupTypeUI: varGroupTypesUI){
                                    if (varGroupTypeUI.isEnabled()){
                                        List <VarGroup> varGroups = (List) varGroupTypeUI.getVarGroupType().getGroups();
                                            for (VarGroup varGroupTest: varGroups) {
                                                if (!added && varGroupTest.getId().equals(varGroup.getId())  
                                                        && varGroup.getGroupAssociation().equals(varGroupingUI.getVarGrouping()) 
                                                            && varGroupsAll.contains(varGroupTest) ){
                                                    selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                                                    added = true;
                                                }
                                            }

                                        }
                                      }
                                }
                                if( countEnabled > 1){
                                    int counter = countEnabled;
                                    List[] varGroupArrayList = new  List [countEnabled];

                                    
                                    for (VarGroupTypeUI varGroupTypeUI: varGroupTypesUI){
                                    if (varGroupTypeUI.isEnabled()){
                                        List <VarGroup> varGroups = (List) varGroupTypeUI.getVarGroupType().getGroups();
                                        varGroupArrayList[counter-1] = varGroups;
                                        counter--;
                                        }
                                    }
                                    List <VarGroup> varGroupsSaveGet = new ArrayList((List) varGroupArrayList[0]);
                                    List <VarGroup> varGroupsTempGet = new ArrayList((List) varGroupArrayList[0]);
                                    List <VarGroup> varGroupsSave = new ArrayList(varGroupsSaveGet);
                                    List <VarGroup> varGroupsTemp = new ArrayList(varGroupsTempGet);
                                    List <VarGroup> vsrGroupRemove = new ArrayList();
                                    for (int i=1; i<=countEnabled-1; i++){
                                        List <VarGroup> varGroupsTest = (List) varGroupArrayList[i];
                                        for (VarGroup vgs: varGroupsTemp){
                                            boolean save = false;
                                            for(VarGroup vgt : varGroupsTest){
                                               if (vgt.getId().equals(vgs.getId())  && varGroupsAll.contains(vgs) ){
                                                   save=true;
                                               }
                                                
                                            }
                                            if (!save){
                                                vsrGroupRemove.add(vgs);
                                            }
                                        }

                                        
                                     }


                                     for(VarGroup vgr : vsrGroupRemove){
                                          varGroupsSave.remove(vgr);
                                     }
                                     multipleSelections = varGroupsSave;                                    
                                }                                
                            }


                        } else {
                               if (!added && varGroup.getGroupAssociation().equals(varGroupingUI.getVarGrouping())){
                                    selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                                    added = true;
                               }
                        }
                      }
                  }                   
              }
              if (multipleSelections.size()>0){
                  for (VarGroup vgs: multipleSelections){
                      if (varGroupsAll.contains(vgs)){
                          selectItems.add(new SelectItem(vgs.getId(), vgs.getName()));
                      }                     
                  }
              }
          return selectItems;

    }


    public List<SelectItem> getSelectMeasureGroupTypes() {

        return this.selectMeasureGroupTypes;
    }

    public void reset_DisplayType(){
        Object value= this.selectGraphType.getValue();
        this.displayType =  (Long) value ;
        callDrawVisualization();
    }
    
    private String yAxisLabelForIndex(String strIn){
        
        return strIn + " = 100";
    }
    
    private void callDrawVisualization(){
        FacesContext fc = FacesContext.getCurrentInstance();
        JavascriptContext.addJavascriptCall(fc, "drawVisualization();");
        //JavascriptContext.addJavascriptCall(fc, "initLineDetails"); // commented out because this was throwing JS error and we couldn't find how it was getting called; leaving in, in case we need to investigate further
    }
    
    public void update_StartYear(){
        Object value= this.selectStartYear.getValue();
        this.startYear = (String) value;
        getDataTable(true);
        if (!this.displayIndexes){
            yAxisLabel=  getYAxisLabelFromVizLines(); 
        }  else {
             yAxisLabel =  yAxisLabelForIndex(indexDate);
        }
        disableIneligibleYears();
        callDrawVisualization();
    }
    
    private void disableIneligibleYears(){
        List <SelectItem> selectEndYearsTest = new ArrayList();
        for (SelectItem present: selectEndYears){
            selectEndYearsTest.add(present);
        }        
        selectEndYears.clear(); 
        boolean disabled;
        for (SelectItem present: selectEndYearsTest){
            disabled = false;
            Integer testYear = new Integer((String)present.getValue()).intValue();
            if (testYear.intValue() <= new Integer(this.startYear).intValue() ){
                disabled = true;               
            }
            present.setDisabled(disabled);
            selectEndYears.add(present);
        }
        List <SelectItem> selectBeginYearsTest = new ArrayList();        
        for (SelectItem present: selectBeginYears){
            selectBeginYearsTest.add(present);
        }        
        selectBeginYears.clear();        
        for (SelectItem present: selectBeginYearsTest){
            disabled = false;
            Integer testYear = new Integer((String)present.getValue()).intValue();
            if (testYear.intValue() >= new Integer(this.endYear).intValue() ){
                disabled = true;
            }
            present.setDisabled(disabled);
            selectBeginYears.add(present);
        }
        
    }
    
    public void update_LegendPosition(){
        Object value= this.selectLegendPosition.getValue();
        Integer intVal =  (Integer) value;
        this.legendInt = intVal.intValue();
        callDrawVisualization();
    }
    
    public void update_GraphHeight(){
        Object value= this.selectGraphHeight.getValue();
        Integer intVal =  (Integer) value;
        this.heightInt = intVal.intValue();
        callDrawVisualization();
    }

    public void update_EndYear(){
        Object value= this.selectEndYear.getValue();
        this.endYear = (String) value ;
        getDataTable(true);
        if (!this.displayIndexes){
            yAxisLabel=  getYAxisLabelFromVizLines(); 
        }  else {
             yAxisLabel =  yAxisLabelForIndex(indexDate);
        }
        disableIneligibleYears();
        callDrawVisualization();
    }

    public void update_IndexYear(){
        Object value= this.selectIndexYear.getValue();
        this.indexDate = (String) value ;        
        yAxisLabel =  yAxisLabelForIndex(indexDate);
        getDataTable(false);
        updateImageFooters();
        resetLineBorder();
        callDrawVisualization();
    }

    public void updateUseIndex(){
        Object value= this.useIndicesCheckBox.getValue();

        this.displayIndexes = (Boolean) value;
        if (!this.displayIndexes){
            forcedIndexMessage = ""; 
            yAxisLabel=  getYAxisLabelFromVizLines(); 
        }  else {
             yAxisLabel = yAxisLabelForIndex(indexDate);
        }
        callDrawVisualization();
    }
    
    private String getYAxisLabelFromVizLines(){
        
        for (VisualizationLineDefinition vld: vizLines){                        
            if (!vld.getMeasureGroup().getUnits().isEmpty()){
                return vld.getMeasureGroup().getUnits();
            }
        }
        return "";
    }

    public void reset_MeasureItems(ValueChangeEvent ae){
        int i = (Integer) ae.getNewValue();
        this.selectMeasureItems = loadSelectMeasureItems(i);
        selectedMeasureId = new Long(0);
        selectedMeasureHasFilterTypes = false;
        selectedMeasureHasFilters = false;
        loadFilterGroupings();
        inputLineLabel.setValue("");
        callDrawVisualization();
    }
    
    
    private VarGroup getFilterGroupFromId(Long Id) {
        
        for (VarGroup varGroupTest: allFilterGroups){
            if (varGroupTest.getId().equals(Id)){
                return varGroupTest;
            }
        }
        return null;
        
    }
    
    private void updateGraphTitleForMeasure(){
        String tmpLineLabel = "";       
        Set<String> set = new HashSet(); 
        for (VisualizationLineDefinition vld: vizLines){
            
            String checkName = vld.getMeasureGroup().getName();
            if (!checkName.isEmpty()){
                 if (tmpLineLabel.isEmpty()){
                        
                        tmpLineLabel = checkName;
                        set.add(checkName);
                 } else if (!set.contains(checkName)){
                     
                        tmpLineLabel += ", ";
                        tmpLineLabel += checkName;
                       set.add(checkName);
                 }
            }
        }

           setGraphTitle(tmpLineLabel);
           getInputGraphTitle().setValue(tmpLineLabel);                
    }


    public int getGroupTypeId() {
        return groupTypeId;
    }

    public void setGroupTypeId(int groupTypeId) {
        this.groupTypeId = groupTypeId;
    }


    public void addLine(ActionEvent ae){
    
        dataNotAddedMessage = "";
        
        if ( lineLabel.isEmpty() || lineLabel.trim().equals("") ) {
            FacesMessage message = new FacesMessage("Please complete your selections");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);
            return;
        }

        if (vizLines.size() >= 8){
            FacesMessage message = new FacesMessage("A maximum of 8 lines may be displayed in a single graph");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);
            return;
        }

        if (validateSelections()){
            for (VisualizationLineDefinition vl :vizLines) {
                if (vl.getVariableId().equals(dataVariableSelected.getId())){
                   FacesMessage message = new FacesMessage("This data has already been selected");
                    FacesContext fc = FacesContext.getCurrentInstance();
                    fc.addMessage(addLineButton.getClientId(fc), message);
                    return;
                }                
            }                            
        }

        if (validateSelections()){
           FacesContext.getCurrentInstance().renderResponse();
           VisualizationLineDefinition vizLine = new VisualizationLineDefinition();

           vizLine.setMeasureGroup(visualizationService.getGroupFromId(selectedMeasureId));

           List <VarGroup> selectedFilterGroups = new ArrayList();
           for(VarGroupingUI varGrouping: filterGroupings) {
               if (!(varGrouping.getSelectedGroupId() == 0)){
                    for (VarGroup filterGroup: allFilterGroups){
                        if (filterGroup.getGroupAssociation().equals(varGrouping.getVarGrouping()) && filterGroup.getId().equals(varGrouping.getSelectedGroupId())){
                               selectedFilterGroups.add(filterGroup);
                        }
                    }                                              
               }
           }
           vizLine.setFilterGroups(selectedFilterGroups);
           vizLine.setLabel(lineLabel);
           vizLine.setMeasureLabel(measureLabel);
           vizLine.setColor(lineColor);
           vizLine.setBorder("background-color:black;");
           vizLine.setVariableId(dataVariableSelected.getId());
           vizLine.setVariableName(dataVariableSelected.getName());
           vizLine.setVariableLabel(dataVariableSelected.getLabel());
           
           vizLines.add(vizLine);
           this.numberOfColumns = new Long(vizLines.size());
           startYear = "0";
           endYear = "3000";
           getDataTable(true);
           resetLineBorder();
           getSourceList();
           checkUnits();
           finalizeForcedIndexMessage();
           
                   
            if (new Integer(indexDate).intValue() == 0) {
                dataNotAddedMessage = "You cannot add this line as an index. It does not have any time period in common with the previously selected lines.";
                deleteLatestLine();
            }
           
           updateImageFooters();

           if (!titleEdited){
                updateGraphTitleForMeasure();
           }

            dataTableVizLines.getChildren().clear();
            dataTableVizLines.getSavedChildren().clear();
            
            callDrawVisualization();

           return;
        } else {
            FacesContext fc = FacesContext.getCurrentInstance();
        }

    }
    
    private void finalizeForcedIndexMessage(){

        if (!forcedIndexMessage.isEmpty()  && new Integer(indexDate).intValue() > 0){
                yAxisLabel =  yAxisLabelForIndex(indexDate);
                setIndexDate(indexDate);          
        }  

                
    }
    
    private void deleteLatestLine(){
        
        List <VisualizationLineDefinition> removeList = new ArrayList();

        List <VisualizationLineDefinition> tempList = new ArrayList(vizLines);
        
        VisualizationLineDefinition  removeLine = null;
        
           for(VisualizationLineDefinition vizLineCheck: tempList){
               
               removeLine = vizLineCheck;

             }

           removeList.add(removeLine);
           
           for(VisualizationLineDefinition vizLineRem : removeList){
                        vizLines.remove(vizLineRem);
           }
           
           this.numberOfColumns = new Long(vizLines.size());
            
            dataTableVizLines.getChildren().clear();
            dataTableVizLines.getSavedChildren().clear();
           startYear = "0";
           endYear = "3000";
           getDataTable(true);
           resetLineBorder();
           getSourceList();
           setDisplayIndexes(false);
           checkUnits();
           finalizeForcedIndexMessage();
           updateImageFooters();
           
           if (!this.displayIndexes){               
                yAxisLabel=  getYAxisLabelFromVizLines(); 
            }  else {
                yAxisLabel = yAxisLabelForIndex(indexDate);
            }
           
           if (!titleEdited){
                updateGraphTitleForMeasure();
           }

        callDrawVisualization();
    }

    public void removeAllLines(ActionEvent ae){
    
        List <VisualizationLineDefinition> vizLinesRemove = new ArrayList();
        for (VisualizationLineDefinition vld: vizLines){
            
            vizLinesRemove.add(vld);
        }
        
           for (VisualizationLineDefinition vl :vizLines) {
                dataTableFilterGroups.setValue(null);
            }
        
        for (VisualizationLineDefinition vld: vizLinesRemove){
            vizLines.remove(vld);
        }        

        vizLines = new ArrayList();
        dataTableVizLines.getChildren().clear();
        dataTableVizLines.getChildren().clear();
 /*       
        getDataTable();
        resetLineBorder();
        sourceList.clear();
        updateImageFooters();
        FacesContext fc = FacesContext.getCurrentInstance();
        JavascriptContext.addJavascriptCall(fc, "drawVisualization();");           
        JavascriptContext.addJavascriptCall(fc, "initLineDetails");
         * 
         */
        refreshSettings();
    }
   
    private void resetLineBorder(){
        int i = 0;
        if (vizLines.size()>=1){
            for (VisualizationLineDefinition vl: vizLines){
                if (i==0){
                    vl.setBorder("background-color:#4684EE;");
                }
                if (i==1){
                    vl.setBorder("background-color:#DC3912;");
                }
                if (i==2){
                    vl.setBorder("background-color:#FF9900;");
                }
                if (i==3){
                    vl.setBorder("background-color:#008000;");
                }
                if (i==4){
                    vl.setBorder("background-color:#4942CC;");
                }
                if (i==5){
                    vl.setBorder("background-color:#990099;");
                }
                if (i==6){
                    vl.setBorder("background-color:#FF80F2;");
                }
                if (i==7){
                    vl.setBorder("background-color:#7FD127;");
                }
                i++;
            }
        }
    }
    
    public void updateLineLabel(ActionEvent ae){
        String  newLineLabel =  (String) inputTextLineLabel.getValue();
        String oldLabel = "";        
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VisualizationLineDefinition vizLine = (VisualizationLineDefinition) tempTable.getRowData();
        oldLabel = vizLine.getLabel();
        if (newLineLabel.trim().isEmpty()){
            vizLine.setLabel(oldLabel);
            inputTextLineLabel.setValue(oldLabel);

        } else {
            vizLine.setLabel(newLineLabel);
        }  
        getDataTable(false);
        callDrawVisualization();
        
    }

    
    public void deleteLine(ActionEvent ae){

        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VisualizationLineDefinition vizLine = (VisualizationLineDefinition) tempTable.getRowData();
        

        List <VisualizationLineDefinition> removeList = new ArrayList();

        List <VisualizationLineDefinition> tempList = new ArrayList(vizLines);
           for(VisualizationLineDefinition vizLineCheck: tempList){
                if (vizLineCheck.equals(vizLine)){
                    removeList.add(vizLineCheck);
                }
             }

           for(VisualizationLineDefinition vizLineRem : removeList){
                        vizLines.remove(vizLineRem);
           }
           
           this.numberOfColumns = new Long(vizLines.size());
           if (numberOfColumns == 0){
                   titleEdited = false;
                   sourceEdited = false;              
           }
            dataTableVizLines.getChildren().clear();
            dataTableVizLines.getSavedChildren().clear();
           startYear = "0";
           endYear = "3000";
           getDataTable(true);
           resetLineBorder();
           getSourceList();
           checkUnits();
           finalizeForcedIndexMessage();
           updateImageFooters();
           
           if (!this.displayIndexes){               
                yAxisLabel=  getYAxisLabelFromVizLines(); 
            }  else {
                yAxisLabel = yAxisLabelForIndex(indexDate);
            }
           
           if (!titleEdited){
                updateGraphTitleForMeasure();
           }

        callDrawVisualization();

    }
    
    private boolean validateSelections(){
        boolean valid  = true;
        int count = 0;

        if (selectedMeasureId == 0){
            FacesMessage message = new FacesMessage("Please complete your selections.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);
            return false;
        }



        if (!filterGroupings.isEmpty()){
            
            for(VarGroupingUI varGrouping: filterGroupings) {
               if(varGrouping.getSelectedGroupId()==0){
                    FacesMessage message = new FacesMessage("Please complete your selections.");
                    FacesContext fc = FacesContext.getCurrentInstance();
                    fc.addMessage(addLineButton.getClientId(fc), message);
                    return false;
               }
            }

        }

           
           List <DataVariable> resultList = new ArrayList();

           Iterator varIter = dvList.iterator();
           while (varIter.hasNext()) {
                DataVariable dv = (DataVariable) varIter.next();
                Collection <DataVariableMapping> dvMappings = dv.getDataVariableMappings();
                for(DataVariableMapping dvMapping:dvMappings ) {

                    if (!dvMapping.isX_axis() && dvMapping.getGroup().getId().equals(selectedMeasureId)){
                        resultList.add(dv);
                         count++;
                    }
                }
           }
           List <ArrayList> filterGroupingList = new ArrayList();

           List <DataVariable> resultListFilter = new ArrayList();
           
           List <DataVariable> measureList = resultList;

           List <ArrayList> filterGroupsList = new ArrayList();
           int filterGroupListCount = 0;
           for(VarGroupingUI varGrouping: filterGroupings) {
               if(varGrouping.getSelectedGroupId()!=0){
                   ArrayList <DataVariable> tempList = new ArrayList();
                   for (DataVariable dv : dvList){
                       Collection <DataVariableMapping> dvMappings = dv.getDataVariableMappings();
                        for(DataVariableMapping dvMapping:dvMappings ) {
                            if (!dvMapping.isX_axis() && dvMapping.getGroup().getId().equals(varGrouping.getSelectedGroupId())){
                                tempList.add(dv);
                                 count++;
                            }
                        }                       
                   }
                   filterGroupsList.add(tempList);
                   filterGroupListCount++;
               }
               
               if (filterGroupListCount == 0){
                    FacesMessage message = new FacesMessage("Please complete your selections.");
                    FacesContext fc = FacesContext.getCurrentInstance();
                    fc.addMessage(addLineButton.getClientId(fc), message);
                    return false;
                }
               
           }
           

           
           if (filterGroupListCount == 1){
               for (List groupList: filterGroupsList){
                   for (Object dvGroup: groupList){
                       DataVariable dvGroupFilter = (DataVariable) dvGroup;
                       for (DataVariable dvMeasure: measureList){
                           if (dvGroupFilter.equals(dvMeasure)){
                                dataVariableSelected = dvMeasure;
                                return true;
                           }                      
                       }                  
                   }              
               }               
           }
           
                     
           
           for(VarGroupingUI varGrouping: filterGroupings) {
               ArrayList <DataVariable> tempList = new ArrayList();
               if(varGrouping.getSelectedGroupId().intValue() !=0){
                   Iterator varIterb = resultList.iterator();
                   while (varIterb.hasNext()) {
                        DataVariable dv = (DataVariable) varIterb.next();
                        Collection <DataVariableMapping> dvMappings = dv.getDataVariableMappings();
                        for(DataVariableMapping dvMapping:dvMappings ) {  
                            if (!dvMapping.isX_axis() && dvMapping.getGroup().getId().equals(varGrouping.getSelectedGroupId())){
                                resultListFilter.add(dv);
                                tempList.add(dv);
                                count++;
                            }
                        }
                   }
                   filterGroupingList.add(tempList);

                           List <DataVariable> removeList = new ArrayList();
                    for (DataVariable dv : measureList){
                        boolean remove = true;
                            for (Object dvO: tempList){
                                DataVariable dvF = (DataVariable) dvO;
                                    if (dvF.equals(dv)){
                                        remove = false;
                                    }
                            }
                            if (remove) removeList.add(dv);
                    }


                    for(DataVariable dataVarRemove : removeList){
                          measureList.remove(dataVarRemove);
                    }

               } else {
                    FacesMessage message = new FacesMessage("Please complete your selections.");
                    FacesContext fc = FacesContext.getCurrentInstance();
                    fc.addMessage(addLineButton.getClientId(fc), message);
                    return false;

               }
           }




        List <DataVariable> finalList = new ArrayList();
        finalList = measureList;


        if(finalList.size() == 1) {
            dataVariableSelected = finalList.get(0);
        } else {
            FacesMessage message = new FacesMessage("Please complete your selections.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);
            return false;
        }



        if (!valid){
            FacesMessage message = new FacesMessage("You must select a filter for each group.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);

        }

        return valid;
    }

    public List<VisualizationLineDefinition> getVizLines() {
        return vizLines;
    }

    public void setVizLines(List<VisualizationLineDefinition> vizLines) {
        this.vizLines = vizLines;
    }
    public Long getSelectedFilterGroupId() {
        return selectedFilterGroupId;
    }

    public void setSelectedFilterGroupId(Long selectedFilterGroupId) {
        this.selectedFilterGroupId = selectedFilterGroupId;
    }

    public Long getSelectedMeasureId() {
        return selectedMeasureId;
    }

    public void setSelectedMeasureId(Long selectedMeasureId) {
        this.selectedMeasureId = selectedMeasureId;
    }

    public String getLineLabel() {
        return lineLabel;
    }

    public void setLineLabel(String lineLabel) {
        this.lineLabel = lineLabel;
    }
    public String getLineColor() {
        return lineColor;
    }

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }
    
    public String getSourceString() {
        return sourceString;
    }

    public void setSourceString(String sourceString) {
        this.sourceString = sourceString;
    }
    public boolean isTitleEdited() {
        return titleEdited;
    }

    public void setTitleEdited(boolean titleEdited) {
        this.titleEdited = titleEdited;
    }

    public boolean isShowFilterGroupTypes() {
        return showFilterGroupTypes;
    }

    public void setShowFilterGroupTypes(boolean showFilterGroupTypes) {
        this.showFilterGroupTypes = showFilterGroupTypes;
    }
    
    public String getDisplaySourceFooter() {
        return displaySourceFooter;
    }

    public void setDisplaySourceFooter(String displaySourceFooter) {
        this.displaySourceFooter = displaySourceFooter;
    }

    public String getDisplaySourceFooterNoYLabel() {
        return displaySourceFooterNoYLabel;
    }

    public void setDisplaySourceFooterNoYLabel(String displaySourceFooterNoYLabel) {
        this.displaySourceFooterNoYLabel = displaySourceFooterNoYLabel;
    }

    public String getDataTableId() {
        return dataTableId;
    }

    public void setDataTableId(String dataTableId) {
        this.dataTableId = dataTableId;
    }
        
    public String getDtColumnString() {
        return dtColumnString;
    }

    public void setDtColumnString(String dtColumnString) {
        this.dtColumnString = dtColumnString;
    }
    
    private HtmlCommandButton addLineButton = new HtmlCommandButton();

    public HtmlCommandButton getAddLineButton() {
        return addLineButton;
    }

    public void setAddLineButton(HtmlCommandButton hit) {
        this.addLineButton = hit;
    }

    private HtmlCommandButton deleteLineButton = new HtmlCommandButton();

    public HtmlCommandButton getDeleteLineButton() {
        return deleteLineButton;
    }
    
    
    public boolean isSelectedMeasureHasFilters() {
        return selectedMeasureHasFilters;
    }

    public void setDeleteLineButton(HtmlCommandButton hit) {
        this.deleteLineButton = hit;
    }
    
    public String getyAxisLabel() {
        return yAxisLabel;
    }

    public String getDataTable(boolean resetIndexYear){

    StudyFile sf = dt.getStudyFile();
    csvColumnString = "";
    dtColumnString = "";
    columnString = "";
    imageColumnString = "";
    try {
        File tmpSubsetFile = File.createTempFile("tempsubsetfile.", ".tab");
        List <DataVariable> dvsIn = new ArrayList();
        dvsIn.add(xAxisVar);
        columnString += xAxisVar.getName();
        csvColumnString += xAxisVar.getName();
        dtColumnString += xAxisVar.getName();
        for (VisualizationLineDefinition vld: vizLines){

            Long testId = vld.getVariableId();
            for (DataVariable dv : dt.getDataVariables()){
                if (dv.getId().equals(testId)){
                     dvsIn.add(dv);
                     columnString = columnString + "|" + vld.getLabel();
                     if (!vld.getMeasureGroup().getUnits().isEmpty()){                          
                         dtColumnString = dtColumnString + "|" + vld.getLabel();
                     } else {
                         dtColumnString = dtColumnString + "|" + vld.getLabel();
                     }                     
                     csvColumnString = csvColumnString + "," + getSafeCString( vld.getLabel());
                     imageColumnString= imageColumnString + "|" + vld.getLabel();
                }
            }

        }

        Set<Integer> fields = new LinkedHashSet<Integer>();
        List<DataVariable> dvs = dvsIn;
        sourceList.clear();
        List <DataVariableMapping> variableMappings = new ArrayList();
        for (Iterator el = dvs.iterator(); el.hasNext();) {
            DataVariable dv = (DataVariable) el.next();
            variableMappings = visualizationService.getSourceMappings((List)  dv.getDataVariableMappings());
                for (DataVariableMapping dvm: variableMappings ){
                sourceList.add(dvm);
            }
            fields.add(dv.getFileOrder());
        }

        // Execute the subsetting request:
        FieldCutter fc = new DvnJavaFieldCutter();


        fc.subsetFile(
            sf.getFileSystemLocation(),
            tmpSubsetFile.getAbsolutePath(),
            fields,
            dt.getCaseQuantity() );

        if (tmpSubsetFile.exists()) {
            Long subsetFileSize = tmpSubsetFile.length();
            List <String>  fileList = new ArrayList();
            BufferedReader reader = new BufferedReader(new FileReader(tmpSubsetFile));
            String line = null;
            while ((line=reader.readLine()) != null) {
               String check =  line.toString();
               fileList.add(check);
            }
            loadDataTableData(fileList, resetIndexYear);           
            return subsetFileSize.toString();
        }
        return "";

        } catch  (IOException e) {
            e.printStackTrace();
            return "failure";
        }
    }
    
    private String getSafeCString(String strIn){
        String retString = strIn;
        int nextSpace = strIn.indexOf(",");  
        if(nextSpace > 0){
            // If the string is already enclosed in double quotes, remove them:
            retString = retString.replaceFirst("^\"", "");
            retString = retString.replaceFirst("\"$", "");

            // Escape all remaining double quotes, by replacing each one with
            // 2 double quotes in a row ("").
            // This is an ancient (IBM Mainframe ancient) CSV convention.
            // (see http://en.wikipedia.org/wiki/Comma-separated_values#Basic_rules)
            // Excel apparently follows it to this day. On the other hand,
            // Excel does NOT understand backslash escapes and will in fact
            // be confused by it!

            retString = retString.replaceAll("\"", "\"\"");

            // finally, add double quotes around the string:
            retString = "\"" + retString + "\"";
        } 
        return retString;
    }

    private void loadDataTableData(List inStr, boolean resetIndexYear){
    selectBeginYears = new ArrayList();
    selectEndYears = new ArrayList();
    selectIndexDate = new ArrayList();
    selectEndYears.add(new SelectItem(3000, "Max"));
    lowValStandard = new Float(0);
    lowValIndex = new Float (100);
    highValStandard = new Float (0);
    highValIndex = new Float(0);
    boolean indexesAvailable = false;
    
    int startYearTransform = 0;
    boolean startYearTransformSet = false;
    int endYearTransform = 3000;
    boolean endYearTransformSet = false;
    boolean firstYearSet = false;
    String maxYear = "";
    String output = "";
    String indexedOutput = "";
    String csvOutput = csvColumnString + "\n";

    boolean addIndexDate = false;
    boolean[] getIndexes = new boolean[9];
    boolean firstIndexDateSet = false;
    int firstIndexDate = 0;
    int indexYearForCalc = 0;
    
    if (new Integer(startYear.toString()).intValue() != 0){
        startYearTransform = new Integer(startYear.toString()).intValue();
        startYearTransformSet = true;
    }
    
    if (new Integer(endYear.toString()).intValue() != 3000){
        endYearTransform = new Integer(endYear.toString()).intValue();
        endYearTransformSet = true;
    }
    
    if (indexDate != null && !indexDate.isEmpty()){
        indexYearForCalc = new Integer(indexDate.toString()).intValue();
    }
    
    for (int i = 1; i<9; i++){
        getIndexes[i] = false;
    }
    String[] indexVals = new String[9];
    int maxLength = 0;
    for (Object inObj: inStr ){
        String nextStr = (String) inObj;
        String[] columnDetail = nextStr.split("\t");
        String[] test = columnDetail;
        if (test.length -1 > maxLength){
            maxLength = test.length -1;
        }
    }
    
    for (Object inObj: inStr ){
        String nextStr = (String) inObj;
        String[] columnDetail = nextStr.split("\t");
        String[] test = columnDetail;
        if (!startYearTransformSet && test.length > 1){
            startYearTransformSet = true;
            startYearTransform = new Integer(test[0]).intValue();
        }
        if (!endYearTransformSet && test.length > 1  && (new Integer(test[0]).intValue() > endYearTransform  || endYearTransform == 3000) ){
            endYearTransform = new Integer(test[0]).intValue();
        }
        
        addIndexDate = true;
        for (int i=0; i<test.length; i++){
            if (test.length - 1 == maxLength){
                if (i>0 && (test[i].isEmpty()  || new Float(test[i]).floatValue() == 0)){
                    addIndexDate = false;
                }                             
            }  else {
                addIndexDate = false;
            }
                 
        }
        
        if (addIndexDate){
              for (int k = 1; k<maxLength+1; k++){
                  getIndexes[k] = true;
              }
              if(!firstIndexDateSet){
                 firstIndexDateSet = true; 
                 firstIndexDate =  new Integer(test[0].toString()).intValue();
              }
              selectIndexDate.add(new SelectItem(test[0], test[0]));  
        }
            if (!resetIndexYear){
                indexYearForCalc = Math.max(firstIndexDate, indexYearForCalc);
            } else {
                if (new Integer(this.startYear).intValue() > 0){
                    indexYearForCalc = Math.max(firstIndexDate, new Integer(this.startYear).intValue());
                } else {
                      indexYearForCalc = firstIndexDate;
                }                  
            }                         
            indexDate = new Integer(indexYearForCalc).toString();
    }
    
    transformedData = new String[maxLength + 1];
    transformedDataIndexed = new String[maxLength + 1];
    boolean indexesDone = false;
    for (Object inObj: inStr ){
        String nextStr = (String) inObj;
        String[] columnDetail = nextStr.split("\t");
        String[] test = columnDetail;

        String col = "";
        String csvCol = "";
        int testYear = 0;
        if (test.length > 1) {
                for (int i=0; i<test.length; i++){
                if (i == 0) {
                    testYear = new Integer(test[0]).intValue();
                    col =  test[i];
                    csvCol  = test[i];

                    if (!firstYearSet){
                         selectBeginYears.add(new SelectItem(col, "Min"));
                         firstYearSet = true;
                    }

                    maxYear = col;
                    selectBeginYears.add(new SelectItem( col, col));
                    selectEndYears.add(new SelectItem(col, col));
                    transformedData[i] = "";
                } else {
                        col = col + ", " +  test[i];
                        csvCol = csvCol + ", " +  test[i];
                        if (testYear >= startYearTransform && testYear <= endYearTransform){
                            transformedData[i] += test[0] +", " + test[i] + ", ";      
                        }
                                                  
                        if(!test[i].isEmpty()  && testYear >= startYearTransform && testYear <= endYearTransform){
                            if (lowValStandard.equals(new Float  (0))  || lowValStandard.compareTo(new Float (test[i])) > 0) {
                                lowValStandard = new Float(test[i]);
                            }
                            if (highValStandard.equals(new Float (0))  || highValStandard.compareTo(new Float (test[i])) < 0 ){
                                highValStandard = new Float(test[i]);
                            }                            
                        }
                                                
                        Double testIndexVal = new Double (0);
                        if (!test[i].isEmpty()  ){
                            testIndexVal =  new Double (test[i]);
                        }
                        if (getIndexes[i] && testIndexVal > 0){
                            indexVals[i] = test[i];
                            getIndexes[i] = false;
                        }
                        boolean allfalse = true;
                        for (int j = 1; j<9; j++){
                            if( getIndexes[j] == true){
                                allfalse = false;
                            }
                        }
                        
                        if (allfalse && !indexesDone && testYear == indexYearForCalc){
                            for (int q = 1; q<test.length; q++){
                                    indexVals[q] = test[q];
                                    indexesDone = true; 
                                    indexesAvailable = true;
                            }
                        }
                    }

                }
                  col = col + ";";
                  csvCol = csvCol + "\n";
           } else {
            
        }
           output = output + col;
           csvOutput = csvOutput + csvCol;           
    }
    
    for (Object inObj: inStr ){
        String nextStr = (String) inObj;
        String[] columnDetail = nextStr.split("\t");
        String[] test = columnDetail;
        String indexCol = "";   
        int testYear = 0;
        if (test.length > 1)
            {
                for (int i=0; i<test.length; i++){
                    if (i == 0) {
                        indexCol = test[i];
                        testYear = new Integer(test[0]).intValue();
                    } else {

                            Float numerator = new Float(0);
                            if (!test[i].isEmpty()){
                                numerator = new Float (test[i]);
                            }
                            Float denominator = new Float (0);
                            if (indexesAvailable && indexVals[i] != null && !indexVals[i].isEmpty()){
                                denominator = new Float (indexVals[i]);
                            }

                            Float result = new Float(0);
                            Object outputIndex = new Double(0);
                            if (!denominator.equals(new Float (0))  && !numerator.equals(new Float (0))){
                                outputIndex = Math.round((numerator / denominator) *  new Double (10000))/ new Float(100);                              
                                result = (numerator / denominator) *  new Float (100);
                                if ((lowValIndex.compareTo(result) > 0)){
                                    if (testYear >= startYearTransform && testYear <= endYearTransform){
                                        lowValIndex = result;
                                    }
                                } 
                                if (!test[i].isEmpty() && (highValIndex.equals(new Float (0))  || highValIndex.compareTo(result) < 0 )){
                                    if (testYear >= startYearTransform && testYear <= endYearTransform){
                                        highValIndex = result;
                                    }
                                    
                                }
                            } else {
                                outputIndex = "";
                            } 
                            if (testYear >= startYearTransform && testYear <= endYearTransform){
                                    transformedDataIndexed[i] += test[0] + ", " + outputIndex.toString() + ", ";
                             }
                            indexCol = indexCol + ", " +  outputIndex.toString();
                    }
                }
                  indexCol = indexCol + ";";
           }


           indexedOutput = indexedOutput + indexCol;
    }

          SelectItem setSI = selectEndYears.get(0);
          setSI.setValue(maxYear);
          selectEndYears.set(0, setSI);
          csvString = csvOutput;
          dataString = output;
          indexedDataString = indexedOutput;          
          cleanUpTransformedData(startYearTransform, endYearTransform);
    }
    
    private void cleanUpTransformedData(int startYear, int endYear){
        
        transformedDataOut = "";
        transformedDataIndexedOut = "";
        int maxLength = transformedData.length;
        
        double maxYear = 0;
        
        for (int i = 1; i<maxLength; i++){
            
            if (transformedData[i] != null){               
                transformedData[i] = transformedData[i].substring(4);
                transformedDataIndexed[i] = transformedDataIndexed[i].substring(4);
            }             
        }
        for (int i = 1; i<maxLength; i++){
            
            if (transformedData[i] != null){

                String[] transformedDataSplit = transformedData[i].split(",");
                for (int t = 0; t < transformedDataSplit.length ; t=t+2){
                        double testYr = 0;
                        if (!transformedDataSplit[t].trim().isEmpty()){
                            testYr = new Float(transformedDataSplit[t]).floatValue();
                        }
                        if (testYr > maxYear){
                            maxYear = testYr;   
                        }
                }
                
            } 

        }
        
        for (int i = 1; i<maxLength; i++){
            double dataMaxYear = 0;
            
            if (transformedData[i] != null){

                String[] transformedDataSplit = transformedData[i].split(",");
                for (int t = 0; t < transformedDataSplit.length ; t=t+2){
                        double testYr = 0;
                        if (!transformedDataSplit[t].trim().isEmpty()){
                            testYr = new Float(transformedDataSplit[t]).floatValue();
                        }
                        if (testYr > dataMaxYear){
                            dataMaxYear = testYr;   
                        }
                }
                
            }
            if (dataMaxYear < maxYear){
                int iDataMax = new Double (dataMaxYear).intValue();
                int iMaxYear = new Double (maxYear).intValue();
                for (int j=iDataMax + 1; j <= iMaxYear; j++){
                     transformedData[i] += j +", , "; 
                     transformedDataIndexed[i] += j +", , ";
                }
                
            }
        }
        
        
        
        for (int i = 1; i<maxLength; i++){           
            if (transformedData[i] != null){
                int len = transformedData[i].length();
                transformedData[i] =  transformedData[i].substring(0, len-2) ; 
            } 
        }
        
        for (int i = 1; i<maxLength; i++){           
            if (transformedDataIndexed[i] != null){
                int lenI = transformedDataIndexed[i].length();
                transformedDataIndexed[i] =  transformedDataIndexed[i].substring(0, lenI-2);  
            } 
        }
        for (int i = 1; i<maxLength; i++){
            if (transformedData[i] != null){
                String transformedDataSelect = "";
                String transformedDataIndexSelect = "";
                String[] transformedDataSplit = transformedData[i].split(",");
                String[] transformedDataIndexSplit = transformedDataIndexed[i].split(",");
                for (int yr = startYear; yr <= endYear; yr++ ){
                    String getVal = "";
                    for (int t = 0; t < transformedDataSplit.length ; t++){
                        double testYr = 0;
                        if (!transformedDataSplit[t].trim().isEmpty()){
                            testYr = new Float(transformedDataSplit[t]).floatValue();
                        }
                            
                        if (yr == testYr  ){
                            getVal = transformedDataSplit[t+1];
                        }
                    }                    
                        transformedDataSelect += getVal + ",";        
                }               
                for (int yr = startYear; yr <= endYear; yr++ ){
                    String getValIndex = "";
                    for (int t = 0; t < transformedDataIndexSplit.length ; t++){
                        double testYr = 0;
                        if (!transformedDataIndexSplit[t].trim().isEmpty()){
                            testYr = new Float(transformedDataIndexSplit[t]).floatValue();
                        }
                            
                        if (yr == testYr ){
                            getValIndex = transformedDataIndexSplit[t+1];
                        }
                    }
                    transformedDataIndexSelect += getValIndex + ",";
                }

                while (dataHasGaps(transformedDataSelect)){
                    transformedDataSelect = fillInGaps(transformedDataSelect);
                }
                
                while (dataHasGaps(transformedDataIndexSelect)){
                    transformedDataIndexSelect = fillInGaps(transformedDataIndexSelect);
                }
                
                int len = transformedDataSelect.length();
                int lenI = transformedDataIndexSelect.length();
                transformedDataSelect =  transformedDataSelect.substring(0, len-1) ;
                transformedDataIndexSelect =  transformedDataIndexSelect.substring(0, lenI-1);               
                if (i > 1){
                    transformedDataOut += ";";
                    transformedDataIndexedOut += ";";
                }
                transformedDataIndexedOut += transformedDataIndexSelect;
                transformedDataOut += transformedDataSelect;   
            }            
        }  
    }
    
    private boolean dataHasGaps(String stringIn){
        List <String> list = Arrays.asList(stringIn.split(","));
        boolean firstVal = false;
        boolean endBlank = false;
        boolean retVal = false;
        
        for (String checkString: list){
            if (!checkString.trim().isEmpty() && Double.parseDouble(checkString.trim()) > 0){
                firstVal = true;
                if (endBlank) {
                    retVal = true;
                }
            }
            if (firstVal && checkString.trim().isEmpty()){
                endBlank = true;
            }
            
            
        }
        
        return retVal;
    }
    
    private String fillInGaps(String stringIn){
        
           String[] stringInSplit = stringIn.split(",");
           Double firstVal =  new Double(0);
           Double endVal = new Double(0);
           boolean gapFound = false;
           int indexUpdate = 0;
           int firstIndex = 0;
           int lastIndex = 0;
           for (int i = 0; i < stringInSplit.length; i++ ){
               if (!gapFound  && !stringInSplit[i].trim().isEmpty()){
                   firstVal = new Double (stringInSplit[i].trim());
                   firstIndex = i;
               }
               if (!gapFound  && firstVal > 0 && stringInSplit[i].trim().isEmpty()){
                   gapFound = true;
                   indexUpdate = i;
               }
               if (gapFound && !stringInSplit[i].trim().isEmpty()  && lastIndex == 0 ){
                   endVal = new Double (stringInSplit[i].trim());
                   lastIndex = i;                   
               }                              
           }
           Double interVal = (firstVal * (lastIndex - indexUpdate) + endVal) / (lastIndex - firstIndex) ;
           stringInSplit[indexUpdate] = interVal.toString();
           
           String retString = "";
           for (int i = 0; i < stringInSplit.length; i++ ){
                retString += stringInSplit[i] + ",";              
           }
           
        return retString;
    }


    public File getZipFileExport() {
        File zipOutputFile;
        ZipOutputStream zout;

        String exportTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());

        try {
            zipOutputFile = File.createTempFile("dataDownload","zip");
            zout = new ZipOutputStream((OutputStream) new FileOutputStream(zipOutputFile));

            if (includeCSV) {
                File csvFile = File.createTempFile("dataDownload_","csv");
                //writeFile(csvFile, csvString.toString().toCharArray(), csvString.length());
                writeFile(csvFile, csvString, csvString.length() );
                addZipEntry(zout, csvFile.getAbsolutePath(), "csvData_" + exportTimestamp + ".txt");

            }
            if (includeImage || includePdf) {
                File imageUrlFile = includeImage ? File.createTempFile("dataDownload","png") : null;
                File imagePdfFile = includePdf ? File.createTempFile("dataDownload","pdf") : null;
                writeImageFile(imageUrlFile, imagePdfFile);
                if (includeImage) {
                    addZipEntry(zout, imageUrlFile.getAbsolutePath(), "imageGraph_" + exportTimestamp + ".png");
                }
                if (includePdf) {
                    addZipEntry(zout, imagePdfFile.getAbsolutePath(), "imagePdf_" + exportTimestamp + ".pdf");
                }
            }
            if (includeExcel) {
                File excelDataFile = File.createTempFile("dataDownload","xls");
                writeExcelFile(excelDataFile);
                addZipEntry(zout, excelDataFile.getAbsolutePath(), "excelData_" + exportTimestamp + ".xls");
            }

            zout.close();
        } catch (IOException e) {
            throw new EJBException(e);
        } catch (Exception ie) {
            zipOutputFile = null;
        }


        return zipOutputFile;
    }


    private void writeFile(File fileIn, String dataIn, int bufSize) {
        ByteBuffer dataByteBuffer = ByteBuffer.wrap(dataIn.getBytes());

        try {
            FileOutputStream outputFile = null;
            outputFile = new FileOutputStream(fileIn, true);
            WritableByteChannel outChannel = outputFile.getChannel();

            try {
                outChannel.write(dataByteBuffer);
                outputFile.close();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        } catch (IOException e) {
            throw new EJBException(e);
        }
    }

    private void writeFile(File fileIn, char[] charArrayIn, int bufSize){
        try {

            FileOutputStream outputFile = null;
            outputFile = new FileOutputStream(fileIn, true);
            FileChannel outChannel = outputFile.getChannel();
            ByteBuffer buf = ByteBuffer.allocate((bufSize * 2) + 1000);
            for (char ch : charArrayIn) {
              buf.putChar(ch);
            }

            buf.flip();

            try {
              outChannel.write(buf);
              outputFile.close();
            } catch (IOException e) {
              e.printStackTrace(System.err);
            }



        } catch (IOException e) {
            throw new EJBException(e);
        }


    }

    private void writeImageFile(File fileIn, File pdfFileIn) {

        try {
            String decoded = URLDecoder.decode(imageURL, "UTF-8");

        if (!decoded.isEmpty()){
                URL imageURLnew = new URL(imageURL);
                
                try{
                    BufferedImage image =     ImageIO.read(imageURLnew);
                                  
                    BufferedImage combinedImage = getCompositeImage(image);
 
                    if (fileIn != null) {                         
                        ImageIO.write(combinedImage, "png", fileIn);                         
                    }

                    if (pdfFileIn != null) {                        
                        File imagePngFile = File.createTempFile("pdfDownload","png");
                        ImageIO.write(combinedImage, "png", imagePngFile);
                        Document convertPngToPdf=new Document();
                        PdfWriter.getInstance(convertPngToPdf, new FileOutputStream(pdfFileIn, true));
                        convertPngToPdf.open();
                        Image convertBmp=PngImage.getImage(imagePngFile.getAbsolutePath());
                        convertBmp.scaleToFit(530f, 500f);
                        convertPngToPdf.add(convertBmp);
                        convertPngToPdf.close();
                    }
                } catch (IIOException io){
                    System.out.println(io.getMessage().toString());
                     System.out.println(io.getCause().toString());
                     System.out.println("IIOException " + imageURLnew);
                    
                }    catch (FontFormatException ff){
                    System.out.println("FontFormatException " + imageURLnew);
                    
                    System.out.println("FontFormatException " + ff.toString());
                }
            }

        } catch (UnsupportedEncodingException uee){
              System.out.println("UnsupportedEncodingException ");

        } catch (MalformedURLException mue){
            System.out.println("MalformedURLException ");
        } catch (IOException io){
            System.out.println("IOException - outer ");
        } 

        catch (DocumentException io){
            System.out.println("IOException - document ");
            System.out.println(io.getMessage());

        } 
    }
    
    
    private BufferedImage getCompositeImage(BufferedImage image) throws FontFormatException, IOException{ Integer heightAdjustment = new Integer(0);
        if (this.heightInt == 1){
            heightAdjustment = 40;
        }
        if (this.heightInt == 3){
            heightAdjustment = -100;
        }        
        
        BufferedImage yAxisImage = new BufferedImage(100, 500, BufferedImage.TYPE_INT_ARGB);
        BufferedImage combinedImage = new BufferedImage(776, 575 + heightAdjustment , BufferedImage.TYPE_INT_ARGB);

        if(graphTitle.trim().isEmpty()){
            graphTitle = " ";
        }
       
        File retFile = generateImageString("16", "620x", "South", "0", graphTitle);       
        BufferedImage titleImage =     ImageIO.read(retFile);

        String source = "";

        if (!sourceString.trim().isEmpty()) {
             source = sourceString;
        }
        
        if(source.trim().isEmpty()){
            source = " ";
        }
        retFile = generateImageString("14", "676x", "NorthWest", "0", source);        
        BufferedImage sourceImage =     ImageIO.read(retFile);
        
        if(yAxisLabel.trim().isEmpty()){
            yAxisLabel = " ";
        }                
        retFile = generateImageString("14", new Integer(350 + heightAdjustment) + "x", "South", "-90", yAxisLabel);        
        BufferedImage yAxisVertImage =     ImageIO.read(retFile);
        
        Graphics2D yag2 = yAxisImage.createGraphics();
        Graphics2D cig2 = combinedImage.createGraphics();
        Graphics2D sig2 = sourceImage.createGraphics();
        

        cig2.setColor(Color.WHITE);
        yag2.setColor(Color.WHITE);
        yag2.fillRect(0, 0, 676, 500);
        cig2.fillRect(0, 0, 776, 550);

        cig2.drawImage(yAxisImage, 0, 0, null);
        cig2.drawImage(yAxisVertImage, 15, 50 , null);
        cig2.drawImage(image, 50, 50, null);
        cig2.drawImage(titleImage, 85, 15, null);
        cig2.drawImage(sourceImage, 45, 475 + heightAdjustment, null);

        yag2.dispose();
        sig2.dispose();
        cig2.dispose();        
        
        return combinedImage;
    }
    
    private File generateImageString(String size, String width, String orientation, String rotate, String inStr) throws IOException {
        // let's attempt to generate the Text image:
        int exitValue = 0;
        File file = File.createTempFile("imageString","tmp");
        if (new File("/usr/bin/convert").exists()) {           
            
            String ImageMagickCmd[] = new String[15];
            
            ImageMagickCmd[0] = "/usr/bin/convert";
            ImageMagickCmd[1] = "-background";
            ImageMagickCmd[2] = "white";
            ImageMagickCmd[3] = "-font";
            ImageMagickCmd[4] = "Helvetica";
            ImageMagickCmd[5] = "-pointsize";
            ImageMagickCmd[6] = size;
            ImageMagickCmd[7] = "-gravity";
            ImageMagickCmd[8] = orientation;
            ImageMagickCmd[9] = "-rotate";
            ImageMagickCmd[10] = rotate;
            ImageMagickCmd[11] = "-size";
            ImageMagickCmd[12] = width;
            ImageMagickCmd[13] = "caption:" + inStr;
            ImageMagickCmd[14] = "png:" + file.getAbsolutePath();
                       
            
            try {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(ImageMagickCmd);
                exitValue = process.waitFor();
            } catch (Exception e) {
                exitValue = 1;
            }

            if (exitValue == 0) {
                return file;
            }

            return file;
        }

        return null;
    }
        
    
    private void addZipEntry(ZipOutputStream zout, String inputFileName, String outputFileName) throws IOException{
        FileInputStream tmpin = new FileInputStream(inputFileName);
        byte[] dataBuffer = new byte[8192];
        int i = 0;

        ZipEntry e = new ZipEntry(outputFileName);
        zout.putNextEntry(e);

        while ((i = tmpin.read(dataBuffer)) > 0) {
            zout.write(dataBuffer, 0, i);
            zout.flush();
        }
        tmpin.close();
        zout.closeEntry();
     }
    
  private void writeExcelFile   (File fileIn) throws IOException {
      String parseString = dataString;
      List list = Arrays.asList(parseString.split(";"));
      String parseColumn = columnString;
      
      try {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("en", "EN"));
            WritableWorkbook w =
            Workbook.createWorkbook(fileIn, ws);

            WritableSheet s = w.createSheet("Data", 0);
            
            int rowCounter = 0;
            
            if (!graphTitle.isEmpty()){
                Label h = new Label (0, 0,  graphTitle.toString());
                s.addCell(h);
                rowCounter++;
            }
            
            List columnHeads = Arrays.asList(parseColumn.split("\\|"));
            

            
            int ccounter = 0;
            for (Object c: columnHeads){
                 Label h = new Label (ccounter, rowCounter,  c.toString());
                 s.addCell(h);
                 ccounter++;
            }
            rowCounter++;
            
            for (Object o: list){
                List dataFields = Arrays.asList(o.toString().split(","));
                int dcounter = 0;
                for (Object d: dataFields){
                    if (dcounter == 0){
                      Label l = new Label (dcounter, rowCounter,  d.toString());
                      s.addCell(l);

                    } else {
                        if ( !d.toString().trim().isEmpty()){
                            jxl.write.Number n = new jxl.write.Number(dcounter, rowCounter,  new Double(d.toString()));
                            s.addCell(n);
                        } else {
                            Label m = new Label  (dcounter, rowCounter,  "N/A");
                            s.addCell(m);
                        }
                    }
                    
                    dcounter++;
                }
                rowCounter++;
            }
            
            if(!sourceString.isEmpty()){
                
                Label h = new Label (0, rowCounter,  "" );
                s.addCell(h);
                rowCounter++;
                
                 h = new Label (0, rowCounter,   sourceString.toString());
                s.addCell(h);
                rowCounter++;
                
            }
            
            w.write();
            w.close();
        }
            catch (Exception e){
                
                 System.out.println("excel creation exception  " ); 
                 System.out.println(e.getMessage().toString());
                 System.out.println(e.getCause().toString());
        }

  }

    private void getSourceList(  ) {
        String returnString = "";
        sourceEdited = false;
        Set<String> set = new HashSet();
        if (sourceList != null  && !sourceList.isEmpty()){
            for (DataVariableMapping dvm: sourceList){
            String checkSource = dvm.getGroup().getName();
            if (!checkSource.isEmpty()){
                if (set.contains(checkSource)){

                    } else {
                        set.add(checkSource);
                        if (returnString.isEmpty()){
                            returnString = "Source: " + returnString + checkSource;
                        } else {
                            returnString = returnString + ", " + checkSource;
                        }
                    }
                }
            }

        }
        sourceString = returnString;
        sources = returnString;
    }
    
    private void checkUnits() {   

        Set<String> set = new HashSet(); 
        int countLines = 0;
        forcedIndexMessage = "";
            for (VisualizationLineDefinition vld: vizLines){
                countLines++;
            String checkUnit = vld.getMeasureGroup().getUnits();

                if (!set.isEmpty() && !set.contains(checkUnit) && countLines > 1){
                    setDisplayIndexes(true);
                    yAxisLabel = "";
                    forcedIndexMessage = "Series have been displayed as indices because their measurement units are different.";
                } else {

                    set.add(checkUnit);
                    yAxisLabel = checkUnit;
                }

            }
      
    }

    private void updateImageFooters() {

        String footerNotes = "";
        String footerNotesNoY = "";
        String displayFooterNotes = "";
        String displayFooterNotesNoY = "";
        
        Set<String> set = new HashSet();
        String axisLabelTemp = "x,y";
        String axisLabelTempNoY = "x,y";
          Integer lineNum = 4;
          Integer lineNumNoY = 4;
          footerNotesNoY = "|";
          footerNotes = "|";
          displayFooterNotesNoY = "|";
          displayFooterNotes = "|";
          
        if (!yAxisLabel.isEmpty()){
               axisLabelTemp += ",y";
              String codedYAxisLabel = "";
              try {
                  codedYAxisLabel = URLEncoder.encode(yAxisLabel, "UTF-8");
              } catch (Exception e) {
                  codedYAxisLabel = yAxisLabel;
              }
               footerNotes += "|" + lineNum + ":||"+ codedYAxisLabel +"|";
               displayFooterNotes += "|" + lineNum + ":||"+ yAxisLabel +"|";
               lineNum++;
        }
        
        if (!xAxisLabel.isEmpty()){
               axisLabelTemp += ",x";
               axisLabelTempNoY += ",x";
               String codedXAxisLabel = "";
              try {
                  codedXAxisLabel = URLEncoder.encode(xAxisLabel, "UTF-8");
              } catch (Exception e) {                
                  codedXAxisLabel = xAxisLabel;
              }
               footerNotes += "|" + lineNum + ":||"+ codedXAxisLabel +"|";
               footerNotesNoY += "|" + lineNumNoY + ":||"+ codedXAxisLabel +"|";
               displayFooterNotes += "|" + lineNum + ":||"+ xAxisLabel +"|";
               displayFooterNotesNoY += "|" + lineNumNoY + ":||"+ xAxisLabel +"|";
               lineNum++;
               lineNumNoY++;
        }
          setImageAxisLabel(axisLabelTemp);
          setImageSourceFooter(footerNotes);
          setImageAxisLabelNoYLabel(axisLabelTempNoY);
          setImageSourceFooterNoYLabel(footerNotesNoY);
          setDisplaySourceFooter(displayFooterNotes);
          setDisplaySourceFooterNoYLabel(displayFooterNotesNoY);          

    }
    

    public String getTitleOut() {
        return "";
    }

    public void openVariableInfoPopup(ActionEvent ae){

        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VisualizationLineDefinition vizLine = (VisualizationLineDefinition) tempTable.getRowData();

        variableLabel = vizLine.getVariableLabel();
        variableLabel = convertVariableLabel(variableLabel);
        variableLabelLink = false;

        showVariableInfoPopup = true;
    }
    
    private String convertVariableLabel(String variableLabelIn){
                String s = variableLabelIn;
        // separete input by spaces ( URLs don't have spaces )
        String [] parts = s.split("\\s");
        String retString = "";
        // Attempt to convert each item into an URL.   
        for( String item : parts ) try {
            URL url = new URL(item);
            // If possible then replace with anchor...
            if (!retString.isEmpty()) {
               retString += " "; 
            }
            retString += "<a href=\'" + url + "\' target=\'varInfo\'>"+ url + "</a> " ;   
        } catch (MalformedURLException e) {
            // If there was an URL that was not it!...
            retString += " " + item;
        }
        return retString;
    }

    public void closeVariableInfoPopup(ActionEvent ae){
        variableLabel = "";
        showVariableInfoPopup = false;
    }
    private HtmlDataTable dataTableVizLines;

    public HtmlDataTable getDataTableVizLines() {
        return this.dataTableVizLines;
    }

    public void setDataTableVizLines(HtmlDataTable dataTableVizLines) {
        this.dataTableVizLines = dataTableVizLines;
    }
    
    private HtmlDataTable dataTableFilterGroups;

    public HtmlDataTable getDataTableFilterGroups() {
        return this.dataTableFilterGroups;
    }

    public void setDataTableFilterGroups(HtmlDataTable dataTableFilterGroups) {
        this.dataTableFilterGroups = dataTableFilterGroups;
    }



    public String getColumnString() {

        return columnString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    public String getDataString() {
        return  this.dataString;
    }

    public String getDataColumns() {
        return  this.numberOfColumns.toString();
    }

    HtmlSelectOneMenu selectGraphType;

    public HtmlSelectOneMenu getSelectGraphType() {
        return selectGraphType;
    }

    public void setSelectGraphType(HtmlSelectOneMenu selectGraphType) {
        this.selectGraphType = selectGraphType;
    }
    
    HtmlSelectOneRadio selectLegendPosition;

    public HtmlSelectOneRadio getSelectLegendPosition() {
        return selectLegendPosition;
    }

    public void setSelectLegendPosition(HtmlSelectOneRadio selectLegendPosition) {
        this.selectLegendPosition = selectLegendPosition;
    }
    
    HtmlSelectOneRadio selectGraphHeight;

    public HtmlSelectOneRadio getSelectGraphHeight() {
        return selectGraphHeight;
    }

    public void setSelectGraphHeight(HtmlSelectOneRadio selectHeightPosition) {
        this.selectGraphHeight = selectHeightPosition;
    }


    HtmlSelectOneMenu selectStartYear;

    public HtmlSelectOneMenu getSelectStartYear() {
        return selectStartYear;
    }

    public void setSelectStartYear(HtmlSelectOneMenu selectStartYear) {
        this.selectStartYear = selectStartYear;
    }

    HtmlSelectOneMenu selectEndYear;

    public HtmlSelectOneMenu getSelectEndYear() {
        return selectEndYear;
    }

    public void setSelectEndYear(HtmlSelectOneMenu selectEndYear) {
        this.selectEndYear = selectEndYear;
    }

    public String getImageAxisLabel() {
        return imageAxisLabel;
    }

    public String getImageSourceFooter() {
        return imageSourceFooter;
    }

    public void setImageAxisLabel(String imageAxisLabel) {
        this.imageAxisLabel = imageAxisLabel;
    }

    public void setImageSourceFooter(String imageSourceFooter) {
        this.imageSourceFooter = imageSourceFooter;
    }
    HtmlSelectOneMenu selectIndexYear;

    public HtmlSelectOneMenu getSelectIndexYear() {
        return selectIndexYear;
    }

    public void setSelectIndexYear(HtmlSelectOneMenu selectIndexYear) {
        this.selectIndexYear = selectIndexYear;
    }
    public Long getDisplayType() {
        return displayType;
    }

    public void setDisplayType(Long displayType) {
        this.displayType = displayType;
    }
    
    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public StudyUI getStudyUI() {
        return studyUI;
    }

    public void setStudyUI(StudyUI studyUI) {
        this.studyUI = studyUI;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    private HtmlInputText inputGraphTitle;

    public HtmlInputText getInputGraphTitle() {
        return this.inputGraphTitle;
    }
    public void setInputGraphTitle(HtmlInputText inputGraphTitle) {
        this.inputGraphTitle = inputGraphTitle;
    }
    
    private HtmlInputText inputSourceString;

    public HtmlInputText getInputSourceString() {
        return this.inputSourceString;
    }
    public void setInputSourceString(HtmlInputText inputSourceString) {
        this.inputSourceString = inputSourceString;
    }
    
    private HtmlInputText inputTextLineLabel;

    public HtmlInputText getInputTextLineLabel() {
        return this.inputTextLineLabel;
    }
    public void setInputTextLineLabel(HtmlInputText inputTextLineLabel) {
        this.inputTextLineLabel = inputTextLineLabel;
    }
    
    private HtmlInputText inputDownloadFileName;

    public HtmlInputText getInputDownloadFileName() {
        return this.inputDownloadFileName;
    }
    public void setInputDownloadFileName(HtmlInputText inputDownloadFileName) {
        this.inputDownloadFileName = inputDownloadFileName;
    }
    private HtmlInputText inputImageURL;

    public HtmlInputText getInputImageURL() {
        return this.inputImageURL;
    }
    public void setInputImageURL(HtmlInputText inputImageURL) {
        this.inputImageURL = inputImageURL;
    }
    
    private HtmlInputText inputLineLabel;

    public HtmlInputText getInputLineLabel() {
        return this.inputLineLabel;
    }
    public void setInputLineLabel(HtmlInputText inputLineLabel) {
        this.inputLineLabel = inputLineLabel;
    }

    public String getGraphTitle() {
        return graphTitle;
    }

    public void setGraphTitle(String graphTitle) {
        this.graphTitle = graphTitle;
    }

    public String updateGraphTitle(){
        titleEdited = true;
        String graphTitleIn = (String) getInputGraphTitle().getValue();
        setGraphTitle(graphTitleIn); 
        callDrawVisualization();
        return "";
    }
    
    public String updateSourceString(){
        sourceEdited = true;
        String sourceStringIn = (String) getInputSourceString().getValue();
        setSourceString(sourceStringIn); 
        callDrawVisualization();
        return "";
    }

    public String updateImageURL(){
        String imageURLIn = (String) getInputImageURL().getValue();   
        setImageURL(imageURLIn);               
        return "";
    }

    public List<SelectItem> getSelectBeginYears() {
        return selectBeginYears;
    }

    public void setSelectBeginYears(List<SelectItem> selectBeginYears) {
        this.selectBeginYears = selectBeginYears;
    }

    public List<SelectItem> getSelectEndYears() {
        return selectEndYears;
    }

    public void setSelectEndYears(List<SelectItem> selectEndYears) {
        this.selectEndYears = selectEndYears;
    }
    public String getEndYear() {
        return endYear;
    }

    public void setEndYear( String endYear) {
        this.endYear = endYear;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getIndexedDataString() {
        return indexedDataString;
    }

    public void setIndexedDataString(String indexedDataString) {
        this.indexedDataString = indexedDataString;
    }

    public String getIndexDate() {
        return indexDate;
    }

    public void setIndexDate(String indexDate) {
        this.indexDate = indexDate;
    }


    public List<SelectItem> getSelectIndexDate() {
        return selectIndexDate;
    }

    public void setSelectIndexDate(List<SelectItem> selectIndexDate) {
        this.selectIndexDate = selectIndexDate;
    }


    public boolean isDisplayIndexes() {
        return displayIndexes;
    }

    public void setDisplayIndexes(boolean displayIndexes) {
        this.displayIndexes = displayIndexes;
    }

    public boolean isDisplayLegend() {
        return displayLegend;
    }

    public void setDisplayLegend(boolean displayLegend) {
        this.displayLegend = displayLegend;
    }
    
    
    public String getImageAxisLabelNoYLabel() {
        return imageAxisLabelNoYLabel;
    }

    public void setImageAxisLabelNoYLabel(String imageAxisLabelNoYLabel) {
        this.imageAxisLabelNoYLabel = imageAxisLabelNoYLabel;
    }


    public String getSources() {
        if (!sourceEdited){
            getSourceList();
            return sources;
        }
        
        return sourceString;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public boolean isShowVariableInfoPopup() {
        return showVariableInfoPopup;
    }

    public void setShowVariableInfoPopup(boolean showVariableInfoPopup) {
        this.showVariableInfoPopup = showVariableInfoPopup;
    }

    public String getVariableLabel() {
        return variableLabel;
    }

    public void setVariableLabel(String variableLabel) {
        this.variableLabel = variableLabel;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
     
    public Long getStudyFileId() {
        return studyFileId;
    }

    public void setStudyFileId(Long studyFileId) {
        this.studyFileId = studyFileId;
    }

    
    public boolean isVariableLabelLink() {
        
        return variableLabelLink;
    }
    
    
    public Float getHighValStandard() {
        return highValStandard;
    }

    public void setHighValStandard(Float highValStandard) {
        this.highValStandard = highValStandard;
    }
    
    public Float getLowValStandard(){
        return lowValStandard;
    }
    
    public Float getHighValIndexed() {
        return highValIndex;
    }
    
    public Float getLowValIndexed(){
        return lowValIndex;
    }
    public String getForcedIndexMessage() {
        return forcedIndexMessage;
    }

    public String getDataNotAddedMessage() {
        return dataNotAddedMessage;
    }

    
    public void setForcedIndexMessage(String forcedIndexMessage) {
        this.forcedIndexMessage = forcedIndexMessage;
    }
    public String getImageColumnString() {
        return imageColumnString;
    }

    public void setImageColumnString(String imageColumnString) {
        this.imageColumnString = imageColumnString;
    }
    
    public String getImageSourceFooterNoYLabel() {
        return imageSourceFooterNoYLabel;
    }
    
    
    public boolean isDataTableAvailable() {
        return dataTableAvailable;
    }

    public void setDataTableAvailable(boolean dataTableAvailable) {
        this.dataTableAvailable = dataTableAvailable;
    }

    public boolean isImageAvailable() {
        return imageAvailable;
    }

    public void setImageAvailable(boolean imageAvailable) {
        this.imageAvailable = imageAvailable;
    }
    
    public String getSourceLineLabel() {
        return sourceLineLabel;
    }

    public void setSourceLineLabel(String sourceLineLabel) {
        this.sourceLineLabel = sourceLineLabel;
    }
    
    public Integer getDefaultView() {

        return defaultView;
    }


    public void setImageSourceFooterNoYLabel(String imageSourceFooterNoYLabel) {
        this.imageSourceFooterNoYLabel = imageSourceFooterNoYLabel;
    }

    private HtmlSelectBooleanCheckbox dataExcelCheckBox;

    public HtmlSelectBooleanCheckbox getDataExcelCheckBox() {
        return dataExcelCheckBox;
    }

    public void setDataExcelCheckBox(HtmlSelectBooleanCheckbox dataExcelCheckBox) {
        this.dataExcelCheckBox = dataExcelCheckBox;
    }

    
    private HtmlSelectBooleanCheckbox useIndicesCheckBox;

    public HtmlSelectBooleanCheckbox getUseIndicesCheckBox() {
        return useIndicesCheckBox;
    }

    public void setUseIndicesCheckBox(HtmlSelectBooleanCheckbox useIndicesCheckBox) {
        this.useIndicesCheckBox = useIndicesCheckBox;
    }
    
    public String getTransformedData() {
        if (!(transformedDataOut == null)  && !transformedDataOut.isEmpty() ){
            return transformedDataOut;
        } else {
            return "";
        }
        
    }
    public String getTransformedDataIndexed() {
        if (!(transformedDataIndexedOut == null)  && !transformedDataIndexedOut.isEmpty() ){
            return transformedDataIndexedOut;
        } else {
            return "";
        }
        
    }
    public void setTransformedData(String[] transformedData) {
        this.transformedData = transformedData;
    }


    public boolean isIncludeCSV() {
        return includeCSV;
    }

    public void setIncludeCSV(boolean includeCSV) {
        this.includeCSV = includeCSV;
    }

    public boolean isIncludeExcel() {
        return includeExcel;
    }

    public void setIncludeExcel(boolean includeExcel) {
        this.includeExcel = includeExcel;
    }

    public boolean isIncludeImage() {
        return includeImage;
    }

    public void setIncludeImage(boolean includeImage) {
        this.includeImage = includeImage;
    }


    public boolean isIncludePdf() {
        return includePdf;
    }

    public void setIncludePdf(boolean includePdf) {
        this.includePdf = includePdf;
    }

    public Resource getDownloadImage() {
        return new ExportFileResource("png");
    }

    public Resource getDownloadPDF() {
        return new ExportFileResource("pdf");
    }

    public Resource getDownloadExcel() {
        return new ExportFileResource("xls");
    }

    public Resource getDownloadCSV() {
        return new ExportFileResource("csv");
    }

    public Resource getDownloadZip() {
        return new ExportFileResource("zip");
    }


    public Integer getLegendInt() {
        return legendInt;
    }

    public void setLegendInt(Integer legendInt) {
        this.legendInt = legendInt;
    }
    
    public int getHeightInt() {
        return heightInt;
    }

    public void setHeightInt(int heightInt) {
        this.heightInt = heightInt;
    }
    
    public class ExportFileResource implements Resource, Serializable{
        File file;
        String fileType;



        public ExportFileResource(String fileType) {
            this.fileType = fileType;
        }

        public String calculateDigest() {
            return file != null ? file.getPath() : null;
        }

        public Date lastModified() {
            return file != null ? new Date(file.lastModified()) : null;
        }

        public InputStream open() throws IOException {
            file = File.createTempFile("downloadFile","tmp");
            if ("png".equals(fileType) ) {
                 writeImageFile(file, null);
            } else if ("pdf".equals(fileType) ) {
                 writeImageFile(null, file);
            } else if ("xls".equals(fileType) ) {
                writeExcelFile(file);
            } else if ("csv".equals(fileType) ) {
                //writeFile(file, csvString.toString().toCharArray(), csvString.toString().length() );
                writeFile(file, csvString, csvString.length() );
            } else if ("zip".equals(fileType) ) {
                file = getZipFileExport();
            } else {
                throw new IOException("Invalid file type selected.");
            }

            // Increment download count:

            if ( vdc != null ) {
                studyService.incrementNumberOfDownloads(sf.getId(), vdc.getId());
            } else {
                studyService.incrementNumberOfDownloads(sf.getId(), (Long)null);
            }


            return new FileInputStream(file);
        }

        public void withOptions(Options options) throws IOException {
            String filePrefix = "dataDownload_" + new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());
            options.setFileName(filePrefix + "." + fileType);

        }

        public File getFile() {
            return file;
        }

    }
}
