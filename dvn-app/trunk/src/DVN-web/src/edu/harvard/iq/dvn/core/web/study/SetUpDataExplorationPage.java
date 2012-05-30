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
package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.async.render.SessionRenderer;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import edu.harvard.iq.dvn.core.study.DataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.EditStudyFilesService;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.Metadata;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.visualization.DataVariableMapping;
import edu.harvard.iq.dvn.core.visualization.VarGroup;
import edu.harvard.iq.dvn.core.visualization.VarGroupType;
import edu.harvard.iq.dvn.core.visualization.VarGrouping;
import edu.harvard.iq.dvn.core.visualization.VarGrouping.GroupingType;
import edu.harvard.iq.dvn.core.visualization.VisualizationDisplay;
import edu.harvard.iq.dvn.core.visualization.VisualizationServiceLocal;
import edu.harvard.iq.dvn.core.web.DataVariableUI;
import edu.harvard.iq.dvn.core.web.VarGroupTypeUI;
import edu.harvard.iq.dvn.core.web.VarGroupUI;
import edu.harvard.iq.dvn.core.web.VarGroupUIList;
import edu.harvard.iq.dvn.core.web.VarGroupingUI;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.ingest.dsb.FieldCutter;
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnJavaFieldCutter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader; 
import java.io.IOException;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author skraffmiller
 */
@EJB(name="visualizationService", beanInterface=edu.harvard.iq.dvn.core.visualization.VisualizationServiceBean.class)
@ViewScoped
@Named("SetUpDataExplorationPage")
public class SetUpDataExplorationPage extends VDCBaseBean implements java.io.Serializable {
    @EJB
    VisualizationServiceLocal      visualizationService;
    @EJB
    VariableServiceLocal varService;
    private EditStudyFilesService editStudyFilesService;
    private List <VarGrouping> varGroupings = new ArrayList();;
    private List <DataVariable> dvList = new ArrayList();
    private List <DataVariable> dvFilteredList = new ArrayList();
    private List <DataVariableUI> dvGenericListUI = new ArrayList();
    private List <DataVariableUI> dvDateListUI = new ArrayList();
    private List <DataVariableUI> dvNumericListUI = new ArrayList();
    private List <DataVariableUI> dvGenericFilteredListUI = new ArrayList();
    private VarGroupingUI measureGrouping = new VarGroupingUI();
    private VarGroupUIList measureGroupList;
    private List <VarGroupUIList> filterGroupList;            
    private VarGroupingUI sourceGrouping = new VarGroupingUI();
    private List <VarGroupingUI> filterGroupings = new ArrayList();

    private List <SelectItem> studyFileIdSelectItems = new ArrayList();
    private List <SelectItem> studyFileHasExplorationSelectItems = new ArrayList();
    private VisualizationDisplay visualizationDisplay;

    private DataVariable xAxisVariable = new DataVariable();
    private Long xAxisVariableId =  new Long(0);
    private StudyVersion studyVersion;

    private Study study;
    private StudyUI studyUI;
    private Long studyFileId = new Long(0);
    private Long sourceFileId = new Long(0);
    private Long studyId ;
    private Metadata metadata;
    private String xAxisUnits = "";
    DataTable dataTable;
    private boolean showCommands = false;
    private boolean showMeasureVariables = false;
    private boolean editXAxis = false;
    private boolean editMeasure = false;
    private boolean editSource = false;

    private boolean editFilterGroup = false;
    private boolean editMeasureGrouping = false;
    private boolean addMeasureType = false;
    private boolean addFilterType = false;
    private boolean addMeasureGroup = false;
    private boolean addFilterGroup = false;
    private boolean addSourceGroup = false;
    private boolean addFilterGrouping = false;
    private boolean manageMeasureTypes = false;
    private boolean manageFilterTypes = false;
    private boolean editMeasureType = false;
    private boolean editFilterType = false;
    private boolean editFilterGrouping = false;
    private String studyFileName = "";

    private boolean xAxisSet = false;
    private boolean hasMeasureGroups = false;
    private boolean hasFilterGroupings = false;
    private boolean hasFilterGroups = false;
    private boolean edited = false;
    
    private boolean hasExploration;
    private boolean showMigrationPopup = false;

    private VarGroupUI editFragmentVarGroup = new VarGroupUI();
    private VarGroupUI editFilterVarGroup = new VarGroupUI();
    private VarGroupUI editMeasureVarGroup = new VarGroupUI();
    private VarGroupUI editSourceVarGroup = new VarGroupUI();
    private VarGroupingUI editFilterVarGrouping = new VarGroupingUI();

    private boolean showFilterVariables = false;
    private boolean selectFile = true;


    public SetUpDataExplorationPage(){
        
    }

    public void init() {
        super.init();
        if (studyId == null){
            studyId = new Long( getVDCRequestBean().getRequestParam("studyId"));
        }
        
        try {
            Context ctx = new InitialContext();
            editStudyFilesService = (EditStudyFilesService) ctx.lookup("java:comp/env/editStudyFiles");

        } catch (NamingException e) {
            e.printStackTrace();
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
            context.addMessage(null, errMessage);

        }
        if (getStudyId() != null) {
            editStudyFilesService.setStudyVersion(studyId);
            study = editStudyFilesService.getStudyVersion().getStudy();
            studyVersion = study.getEditVersion();
            metadata = editStudyFilesService.getStudyVersion().getMetadata();
            setFiles(editStudyFilesService.getCurrentFiles());
        }
        else {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The Study ID is null", null);
            context.addMessage(null, errMessage);
            //Should not get here.
            //Must always be in a study to get to this page.
        }
         studyUI = new StudyUI(studyVersion, null);
         studyFileIdSelectItems = loadStudyFileSelectItems();
         studyFileHasExplorationSelectItems = loadStudyFileMigrationSelectItems();
    }



    public void resetStudyFileId(){

        Object value= this.selectStudyFile.getValue();
        studyFileName = this.selectStudyFile.getLabel();

        Iterator iterator = study.getStudyFiles().iterator();
        while (iterator.hasNext() ){
            StudyFile studyFile = (StudyFile) iterator.next();
            if (studyFile.getId().equals((Long) value)){
                    studyFileName = studyFile.getFileName();
            }
        }
        studyFileId = (Long) value;       
        if (!studyFileId.equals(new Long(0))) {  
            if (!popupMigration()){
               loadDataTable(true);
                showCommands = true;
                selectFile = false;
                hasExploration = true;
            } else {
                hasExploration = false;
                showMigrationPopup = true;
            }
        }    
    }
    
    private boolean popupMigration(){
        dataTable = new DataTable();
        visualizationService.setDataTableFromStudyFileId(studyFileId);
        dataTable = visualizationService.getDataTable();
        if ( (dataTable.getVarGroupings() == null || dataTable.getVarGroupings().isEmpty()) && 
                studyFileHasExplorationSelectItems.size() > 1){
            return true;
        }  
        return false;
    }

    private void loadDataTable(boolean getFromDataBase){
        if (getFromDataBase){
            dataTable = new DataTable();
            visualizationService.setDataTableFromStudyFileId(studyFileId);
            dataTable = visualizationService.getDataTable();            
        }

        varGroupings = dataTable.getVarGroupings();
        dvList = dataTable.getDataVariables();
        measureGrouping = new VarGroupingUI();
        sourceGrouping = new VarGroupingUI();
        loadVarGroupingUIs();

        dvNumericListUI = loadDvListUIs(false);
        dvDateListUI = loadDvListUIs(true);
        if (getFromDataBase) {
            xAxisVariable = visualizationService.getXAxisVariable(dataTable.getId());
            xAxisVariableId = xAxisVariable.getId();
            if (xAxisVariableId.intValue() > 0) {
                xAxisSet = true;
            } else {
                xAxisSet = false;
                editXAxisAction();
            }

            if (xAxisSet) {
                for (DataVariableMapping mapping : xAxisVariable.getDataVariableMappings()) {
                    if (mapping.isX_axis()) {
                        xAxisUnits = mapping.getLabel();
                    }
                }
            }
        }


        if (measureGrouping.getVarGrouping() == null) {
            addNewGrouping(measureGrouping, GroupingType.MEASURE);
        } else {
            if (!measureGrouping.getVarGroupUI().isEmpty()) {
                hasMeasureGroups = true;
            }
        }

        if (sourceGrouping.getVarGrouping() == null) {
            addNewGrouping(sourceGrouping, GroupingType.SOURCE);
        }

        if (!filterGroupings.isEmpty()) {
            hasFilterGroupings = true;
        }
        visualizationDisplay = dataTable.getVisualizationDisplay();

        if (visualizationDisplay == null) {
            visualizationDisplay = getDefaultVisualizationDisplay();
            dataTable.setVisualizationDisplay(visualizationDisplay);
        }
        edited = !getFromDataBase;
    }
    
    private VisualizationDisplay getDefaultVisualizationDisplay(){
       
        VisualizationDisplay retVal = new VisualizationDisplay();
        retVal.setDataTable(dataTable);
        retVal.setShowDataTable(true);
        retVal.setShowFlashGraph(true);
        retVal.setShowImageGraph(true);
        retVal.setDefaultDisplay(0);
        retVal.setSourceInfoLabel("Source Info");
        return retVal;
    }

    private void loadVarGroupingUIs(){
        for (VarGrouping varGrouping: varGroupings ){
            VarGroupingUI vgLocalUI = new VarGroupingUI();
            vgLocalUI.setVarGrouping(varGrouping);
            loadGroupingGroupTypes(vgLocalUI);
            vgLocalUI.setVarGroupTypesUI();
            setVarGroupUI(vgLocalUI);
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                measureGrouping = vgLocalUI;
            } else if (varGrouping.getGroupingType().equals(GroupingType.SOURCE)){
                sourceGrouping = vgLocalUI;
            } else {
                filterGroupings.add(vgLocalUI);
            }
            setVarGroupUIList(vgLocalUI);
        }
    }

    public List<VarGrouping> getVarGroupings() {
        return varGroupings;
    }

    public void setVarGroupings(List<VarGrouping> varGroupings) {
        this.varGroupings = varGroupings;        
    }
    
    public void loadGroupingGroupTypes(VarGroupingUI varGroupingUIIn) {

        varGroupingUIIn.setVarGroupTypesUI(new ArrayList());
        for (VarGroupType varGroupType: varGroupingUIIn.getVarGrouping().getVarGroupTypes()){
             VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
             varGroupTypeUI.setVarGroupType(varGroupType);
             varGroupTypeUI.setEditMode(false);
             varGroupingUIIn.getVarGroupTypesUI().add(varGroupTypeUI);
        }
    }
    
    public void setMeasureGroups(){}
    public void setMeasureGroupTypes(){}

    public List<VarGroupTypeUI> getMeasureGroupTypes() {
        if (measureGrouping == null) return null;
        return (List) measureGrouping.getVarGroupTypesUI();      
    }

    public List <DataVariableUI> loadDvListUIs(boolean isDate){

        List <DataVariableUI> dvListUILocG = new ArrayList();
        for (DataVariable dataVariable : dvList){
            if ((isDate &&  ("date".equals(dataVariable.getFormatCategory())  || "time".equals(dataVariable.getFormatCategory())) 
                    && !hasGapsInTimeVariable(dataVariable)  ) ||
               (!isDate && dataVariable.getVariableFormatType().getName().equals("numeric"))) {
                DataVariableUI dataVariableUI = new DataVariableUI();
                dataVariableUI.setDataVariable(dataVariable);
                dataVariableUI.setSelected(false);
                dataVariableUI.setDisplay(true);
                dvListUILocG.add(dataVariableUI);
            }
        }
        return dvListUILocG;
    }

    public boolean hasGapsInTimeVariable(DataVariable timeVariable) {

        StudyFile sf = dataTable.getStudyFile();

        try {
            File tmpSubsetFile = File.createTempFile("tempsubsetfile.", ".tab");
            Set<Integer> fields = new LinkedHashSet<Integer>();
            fields.add(timeVariable.getFileOrder());
            FieldCutter fc = new DvnJavaFieldCutter();

            fc.subsetFile(
                    sf.getFileSystemLocation(),
                    tmpSubsetFile.getAbsolutePath(),
                    fields,
                    dataTable.getCaseQuantity());

            if (tmpSubsetFile.exists()) {
                List<String> fileList = new ArrayList();
                BufferedReader reader = new BufferedReader(new FileReader(tmpSubsetFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String check = line.toString();
                    fileList.add(check);
                }

                for (Object inObj : fileList) {
                    String nextStr = (String) inObj;
                    String[] columnDetail = nextStr.split("\t");
                    String[] test = columnDetail;
                    if (test.length == 0) {
                        return true;
                    }
                    if (test[0].isEmpty()) {
                        return true;
                    }
                }
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }    

    private void setVarGroupUI(VarGroupingUI varGroupingUI) {
        List<VarGroupUI> varGroupUIList = new ArrayList();
        VarGrouping varGroupingIn = varGroupingUI.getVarGrouping();
        varGroupingIn.getVarGroupTypes();

        List<VarGroup> varGroups;
        varGroups = (List<VarGroup>) varGroupingIn.getVarGroups();
        if (varGroups != null) {
            for (VarGroup varGroup : varGroups) {
                VarGroupUI varGroupUILoc = new VarGroupUI();
                varGroupUILoc.setVarGroup(varGroup);
                varGroupUILoc.getVarGroup().getName();
                varGroupUILoc.setVarGroupTypes(new ArrayList());
                varGroupUILoc.setVarGroupTypesSelectItems(new ArrayList());
                varGroupUILoc.setDataVariablesSelected(new ArrayList());
                List<VarGroupType> varGroupTypes;
                List<DataVariable> dataVariables;

                dataVariables = (List<DataVariable>) visualizationService.getDataVariableMappingsFromDataTableGroup(dataTable, varGroup);
                int selectedVariables = 0;
                if (dataVariables != null) {
                    for (DataVariable dataVariable : dataVariables) {
                        varGroupUILoc.getDataVariablesSelected().add(new Long(dataVariable.getId()));
                        selectedVariables++;
                    }
                }
                varGroupUILoc.setNumberOfVariablesSelected(new Long(selectedVariables));

                varGroupTypes = (List<VarGroupType>) varGroupUILoc.getVarGroup().getGroupTypes();
                String groupTypesSelectedString = "";
                if (varGroupTypes != null) {

                    for (VarGroupType varGroupType : varGroupTypes) {
                        VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
                        varGroupTypeUI.setVarGroupType(varGroupType);
                        varGroupTypeUI.getVarGroupType().getName();
                        varGroupType.getName();

                        varGroupUILoc.getVarGroupTypesSelectItems().add(varGroupTypeUI);
                        if (groupTypesSelectedString.isEmpty()) {
                            groupTypesSelectedString += varGroupTypeUI.getVarGroupType().getName();
                        } else {
                            groupTypesSelectedString = groupTypesSelectedString + ", " + varGroupTypeUI.getVarGroupType().getName();
                        }

                    }
                    varGroupUILoc.setGroupTypesSelectedString(groupTypesSelectedString);
                }
                hasFilterGroups = true;
                varGroupUIList.add(varGroupUILoc);
            }
        }

        varGroupingUI.setVarGroupUI(varGroupUIList);
    }

    public VarGroupUIList getMeasureGroups() {
        return  measureGrouping.getVarGroupUIList();
    }
    
    public List<VarGroupUIList> getFilterGroupList() {
        return filterGroupList;
    }

    public void setFilterGroupList(List<VarGroupUIList> filterGroupList) {
        this.filterGroupList = filterGroupList;
    }

    public VarGroupUIList getMeasureGroupList() {
        return measureGroupList;
    }

    public void setMeasureGroupList(VarGroupUIList measureGroupList) {
        this.measureGroupList = measureGroupList;
    }

    public List<VarGroupUI> getSourceGroups() {
        return (List) sourceGrouping.getVarGroupUI();
    }
    public VarGroupingUI getMeasureGrouping() {
        return measureGrouping;
    }


 private boolean hasAssignedGroups(VarGroupType varGroupTypeIn){
     VarGrouping varGrouping = varGroupTypeIn.getVarGrouping();

     if (varGrouping != null) {
         if (varGrouping.getGroupingType().equals(GroupingType.FILTER)) {
             for (VarGroupingUI testGrouping : filterGroupings) {
                 if (testGrouping.getVarGrouping().getName().equals(varGrouping.getName())) {
                     for (VarGroup varGroup : varGrouping.getVarGroups()) {
                         for (VarGroupType testGroupType : varGroup.getGroupTypes()) {
                             if (testGroupType.getName().equals(varGroupTypeIn.getName())) {
                                 return true;
                             }
                         }
                     }
                 }
             }
         }
         if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)) {
             for (VarGroup varGroup : measureGrouping.getVarGrouping().getVarGroups()) {
                 for (VarGroupType testGroupType : varGroup.getGroupTypes()) {
                     if (testGroupType.getName().equals(varGroupTypeIn.getName())) {
                         return true;
                     }
                 }
             }
         }
     }
     return false;
 }
  
    public void deleteGroupType(ActionEvent ae) {
        boolean measure = false;
        boolean filter = false;
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)) {
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable dataTableDeleteFrom = (HtmlDataTable) uiComponent;
        if (uiComponent.equals(dataTableManageMeasureGroupType)) {
            measure = true;
        }
        if (uiComponent.equals(dataTableMeasureGroupType)) {
            measure = true;
        }
        if (uiComponent.equals(dataTableManageFilterGroupType)) {
            filter = true;
        }
        if (uiComponent.equals(dataTableFilterGroupType)) {
            filter = true;
        }
        VarGroupTypeUI varGroupTypeUIToDelete = (VarGroupTypeUI) dataTableDeleteFrom.getRowData();
        VarGroupType varGroupType = varGroupTypeUIToDelete.getVarGroupType();
        if (filter) {
            for (VarGroupingUI varGroupingUI : filterGroupings) {
                if (varGroupingUI.getVarGrouping() == (varGroupType.getVarGrouping())) {
                    visualizationService.removeCollectionElement(varGroupingUI.getVarGrouping().getVarGroupTypes(), varGroupTypeUIToDelete.getVarGroupType());
                    varGroupingUI.getVarGroupTypesUI().remove(varGroupTypeUIToDelete);
                    varGroupingUI.getVarGrouping().getVarGroupTypes().remove(varGroupType);
                    List <VarGroupUI> varGroupUIList = varGroupingUI.getVarGroupUIList().getVarGroupUIList();
                    deleteGroupsFromType(varGroupTypeUIToDelete.getVarGroupType());
                    for (VarGroupUI varGroupUI : varGroupUIList ){
                        removeTypeFromGroupUI(varGroupUI, varGroupTypeUIToDelete);
                        updateGroupTypesByGroup(varGroupUI);
                    }
                    setVarGroupUIList(varGroupingUI);  
                }
            }
        }
        if (measure) {
            visualizationService.removeCollectionElement(measureGrouping.getVarGrouping().getVarGroupTypes(), varGroupTypeUIToDelete.getVarGroupType());
            measureGrouping.getVarGroupTypesUI().remove(varGroupTypeUIToDelete);
            measureGrouping.getVarGrouping().getVarGroupTypes().remove(varGroupType);
            if (editMeasureVarGroup != null && editMeasureVarGroup.getVarGroup() != null) {
                removeTypeFromGroupUI(editMeasureVarGroup, varGroupTypeUIToDelete);
            }
            deleteGroupsFromType(varGroupTypeUIToDelete.getVarGroupType());
            List<VarGroupUI> varGroupUIList = measureGrouping.getVarGroupUIList().getVarGroupUIList();
            for (VarGroupUI varGroupUI : varGroupUIList) {
                removeTypeFromGroupUI(varGroupUI, varGroupTypeUIToDelete);
                updateGroupTypesByGroup(varGroupUI);
            }
            setVarGroupUIList(measureGrouping);
        }
        edited = true;
    }
    
    private void resetDataVariableListForVarGroupUI(VarGroupUI varGroupUIin){
        dvGenericFilteredListUI.clear();
        dvGenericListUI = dvNumericListUI;
        for (DataVariableUI dataVariableUI : dvGenericListUI) {
            dataVariableUI.setSelected(false);
            if (varGroupUIin.getDataVariablesSelected() != null) {
                for (Object dv : varGroupUIin.getDataVariablesSelected()) {
                    Long testId = (Long) dv;
                    if (testId.equals(dataVariableUI.getDataVariable().getId())) {
                        dataVariableUI.setSelected(true);
                    }
                }
            }
            dvGenericFilteredListUI.add(dataVariableUI);
        }            
    }

    private List<VarGroupTypeUI> resetVarGroupTypesUIForVarGroupUI(VarGroupUI varGroupUIin, VarGroupingUI varGroupingUIin){
        List<VarGroupTypeUI> returnList = new ArrayList<VarGroupTypeUI>();

        List<VarGroupTypeUI> allVarGroupTypes = (List<VarGroupTypeUI>) varGroupingUIin.getVarGroupTypesUI();
        for (VarGroupTypeUI varGroupTypeUI : allVarGroupTypes) {
            varGroupTypeUI.setSelected(false);
            List<VarGroupType> varGroupTypes;
            varGroupTypes = (List<VarGroupType>) varGroupUIin.getVarGroup().getGroupTypes();
            if (varGroupTypes != null) {
                for (VarGroupType varGroupType : varGroupTypes) {
                    if (varGroupType == varGroupTypeUI.getVarGroupType()) {
                        varGroupTypeUI.setSelected(true);
                    }
                }
            }
            returnList.add(varGroupTypeUI);
        }
        return returnList;
    }
    
    public void editMeasureGroup(ActionEvent ae) {
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)) {
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupUI varGroupUI = (VarGroupUI) tempTable.getRowData();
        resetDataVariableListForVarGroupUI(varGroupUI);
        varGroupUI.setVarGroupTypes(resetVarGroupTypesUIForVarGroupUI(varGroupUI, measureGrouping));
        editMeasureVarGroup = new VarGroupUI();
        editMeasureVarGroup = varGroupUI;

        cancelAddEdit();
        getInputMeasureName().setValue(editMeasureVarGroup.getVarGroup().getName());
        getInputMeasureUnits().setValue(editMeasureVarGroup.getVarGroup().getUnits());
        editMeasure = true;
    }

    public void editSourceGroup (ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupUI varGroupUI = (VarGroupUI) tempTable.getRowData();
        resetDataVariableListForVarGroupUI(varGroupUI);
        varGroupUI.setVarGroupTypes(new ArrayList());
        editSourceVarGroup = varGroupUI;
        cancelAddEdit();
        getInputSourceName().setValue(editSourceVarGroup.getVarGroup().getName());
        editSource = true;
    }

    public void editMeasureTypeAction(ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) tempTable.getRowData();
        getEditMeasureGroupTypeName().setValue(varGroupTypeUI.getVarGroupType().getName());
        getEditManageMeasureGroupTypeName().setValue(varGroupTypeUI.getVarGroupType().getName());
        varGroupTypeUI.setEditMode(true);
        editMeasureType = true;
    }

    public void editFilterType(ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) tempTable.getRowData();
        varGroupTypeUI.setEditMode(true);
        getEditFilterGroupTypeName().setValue(varGroupTypeUI.getVarGroupType().getName());
        getEditManageFilterGroupTypeName().setValue(varGroupTypeUI.getVarGroupType().getName());
        editFilterType = true;
    }

    public void editManageFilterType(ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) tempTable.getRowData();
        varGroupTypeUI.setEditMode(true);
        getEditManageFilterGroupTypeName().setValue(varGroupTypeUI.getVarGroupType().getName());
        editFilterType = true;
    }

     public void saveFilterType(ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) tempTable.getRowData();
        String getName = "";

        if (tempTable.equals(dataTableManageFilterGroupType)) {
            getName = (String) getEditManageFilterGroupTypeName().getValue();
        } else {
            getName = (String) getEditFilterGroupTypeName().getValue();
        }
        
        if (checkForDuplicateEntries(varGroupTypeUI.getVarGroupType().getVarGrouping(), getName, false, varGroupTypeUI.getVarGroupType()  )){
            return;
        }

        varGroupTypeUI.getVarGroupType().setName(getName);        
        varGroupTypeUI.setEditMode(false);
        getEditManageFilterGroupTypeName().setValue("");
        getEditFilterGroupTypeName().setValue("");
        edited = true;
     }

    public void saveMeasureType(ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) tempTable.getRowData();

        String getName = "";

        if (tempTable.equals(dataTableManageMeasureGroupType)) {
            getName = (String) getEditManageMeasureGroupTypeName().getValue();
        } else {
            getName = (String) ((String) getEditMeasureGroupTypeName().getValue());
        }

        if (checkForDuplicateEntries(varGroupTypeUI.getVarGroupType().getVarGrouping(), getName, false, varGroupTypeUI.getVarGroupType()  )){
            return;
        }

        varGroupTypeUI.getVarGroupType().setName(getName);               
        varGroupTypeUI.setEditMode(false);
        getEditManageMeasureGroupTypeName().setValue("");
        getEditMeasureGroupTypeName().setValue("");
        edited = true;
    }

    public void editFilterGroup(ActionEvent ae  ) {
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)) {
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
/*
 * We're getting the row index because passing in the filter was not working.
 * we should have been able to get the varGroupUI in question directly from the table
 * but the icefaces datatable inside an h:datatable was causing problems
 * VarGroupUI varGroupUI = (VarGroupUI) tempTable.getRowData();
 * 
 * SEK 
 */
        int index = tempTable.getRowIndex();
        VarGroupingUI varGroupingUIin = (VarGroupingUI) ae.getComponent().getAttributes().get("grouping");
        VarGroupUI varGroupUI = varGroupingUIin.getVarGroupUIList().getVarGroupUIList().get(index);
        resetDataVariableListForVarGroupUI(varGroupUI);
        varGroupUI.setVarGroupTypes(new ArrayList());
        for (VarGroupingUI varGroupingUI : filterGroupings) {
            if (varGroupingUI.getVarGrouping() == (varGroupUI.getVarGroup().getGroupAssociation())) {
                varGroupUI.setVarGroupTypes(resetVarGroupTypesUIForVarGroupUI(varGroupUI, varGroupingUI));
            }
        }
        editFilterVarGroup = varGroupUI;
        cancelAddEdit();
        getInputFilterGroupName().setValue(editFilterVarGroup.getVarGroup().getName());
        editFilterGroup = true;
    }

    public void editFilterGroupingAction (ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupingUI varGroupingUI = (VarGroupingUI) tempTable.getRowData();
        editFilterVarGrouping = varGroupingUI;
        cancelAddEdit();
        getInputFilterGroupingName().setValue(editFilterVarGrouping.getVarGrouping().getName());
        editFilterGrouping = true;
    }

    public void manageFilterGroupTypeAction (ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupingUI varGroupingUI = (VarGroupingUI) tempTable.getRowData();
        editFilterVarGrouping = varGroupingUI;
        cancelAddEdit();
        manageFilterTypes = true;
    }

    public void manageMeasureGroupTypeAction (){
        cancelAddEdit();
        manageMeasureTypes = true;
    }

    public void editMeasureGroupingAction (){
        cancelAddEdit();
        editMeasureGrouping = true;
        getInputMeasureGroupingName().setValue(measureGrouping.getVarGrouping().getName());
    }

    public void editXAxisAction (){
        dvGenericListUI = dvDateListUI;
        Iterator iterator = dvGenericListUI.iterator();
        dvGenericFilteredListUI.clear();
        while (iterator.hasNext() ){

            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            dataVariableUI.setSelected(false);
            if (dataVariableUI.getDataVariable().getId().equals(xAxisVariableId)){
                dataVariableUI.setSelected(true);
            }
                dvGenericFilteredListUI.add(dataVariableUI);
        }
        cancelAddEdit();
        getInputXAxisUnits().setValue(xAxisUnits);
        editXAxis = true;
    }

    public void removeXAxisAction (){
        xAxisVariable = new DataVariable();
        xAxisVariableId =  new Long(0);
        xAxisSet = false;
        xAxisUnits = "";
        resetDVMappingsXAxis();
        cancelAddEdit();
    }

    public void cancelFragment(){
        cancelAddEdit();
    }

    public void saveXAxis(){
        xAxisUnits = (String) getInputXAxisUnits().getValue();
        Long SelectedId = new Long(0);
        for(DataVariableUI dataVariableUI:dvGenericListUI){
            for(DataVariableUI dvTest: dvGenericFilteredListUI){
                if (dvTest.getDataVariable().equals(dataVariableUI.getDataVariable())){
                    dataVariableUI.setSelected(dvTest.isSelected());
                }
            }
        }
        int countSelected = 0;
        for (DataVariableUI dataVariableUI: dvGenericListUI){
            if (dataVariableUI.isSelected()){
                SelectedId = dataVariableUI.getDataVariable().getId();
                countSelected++;
            }
        }
        if (countSelected > 1) {
            getVDCRenderBean().getFlash().put("warningMessage", "You may not select more than one variable.");
        }

        if (countSelected == 1){
            xAxisVariableId = SelectedId;
            xAxisVariable = varService.getDataVariable(SelectedId);
            resetDVMappingsXAxis();
            xAxisSet = true;
        }

        if (countSelected == 0){
            xAxisVariableId = SelectedId;
            xAxisVariable = new DataVariable();
            resetDVMappingsXAxis();
            xAxisSet = false;
        }
        cancelAddEdit();
        edited = true;
    }    

    public void saveMeasureGrouping(){
        String chkGroupName = (String) getInputMeasureGroupingName().getValue();

        if (chkGroupName.isEmpty() || chkGroupName.trim().equals("") ) {
            getVDCRenderBean().getFlash().put("warningMessage", "Please enter a Measure Label.");
            return;
        }
        if (!getInputMeasureGroupingName().getValue().equals("")){
            measureGrouping.getVarGrouping().setName((String) getInputMeasureGroupingName().getValue());
        }
        cancelAddEdit();
        edited = true;
    }

    public void cancelMeasureGrouping(){
        cancelAddEdit();
    }

    public void saveSourceFragment(){
        String chkGroupName = (String) getInputSourceName().getValue();

        if (chkGroupName.isEmpty() || chkGroupName.trim().equals("") ) {
            getVDCRenderBean().getFlash().put("warningMessage", "Please enter a Source Name.");
            return;
        }

        if(addSourceGroup){
            if (checkForDuplicateEntries(editSourceVarGroup.getVarGroup().getGroupAssociation(), chkGroupName, true, null )){
                return;
            }
            editSourceVarGroup.getVarGroup().setName(chkGroupName);
            addSourceGroupSave();
        } else {
            if (checkForDuplicateEntries(editSourceVarGroup.getVarGroup().getGroupAssociation(), chkGroupName, true, editSourceVarGroup.getVarGroup() )){
                return;
            }
            editSourceVarGroup.getVarGroup().setName(chkGroupName);
        }
        editSourceVarGroup.getVarGroup().setGroupAssociation(sourceGrouping.getVarGrouping());
        saveGroupFragment(editSourceVarGroup);
        editSourceVarGroup = null;
        cancelAddEdit();
        edited = true;
    }

    public void saveMeasureFragment(){
        String chkGroupName = (String) getInputMeasureName().getValue();

        if (chkGroupName.isEmpty() || chkGroupName.trim().equals("") ) {
            getVDCRenderBean().getFlash().put("warningMessage", "Please enter a Measure Name.");
            return;
        }
        
        String chkGroupUnits = (String) getInputMeasureUnits().getValue();
        if (chkGroupUnits.length() > 80) {
            getVDCRenderBean().getFlash().put("warningMessage", "Please limit units to 80 characters.");
            return;
        }

        if(addMeasureGroup){
            if (checkForDuplicateEntries(editMeasureVarGroup.getVarGroup().getGroupAssociation(), chkGroupName, true, null )){
                return;
            }
            editMeasureVarGroup.getVarGroup().setName(chkGroupName);
            editMeasureVarGroup.getVarGroup().setUnits((String) getInputMeasureUnits().getValue());
            addMeasureGroupSave();
        } else {
            if (checkForDuplicateEntries(editMeasureVarGroup.getVarGroup().getGroupAssociation(), chkGroupName, true, editMeasureVarGroup.getVarGroup() )){
                return;
            }
            editMeasureVarGroup.getVarGroup().setName(chkGroupName);
            editMeasureVarGroup.getVarGroup().setUnits((String) getInputMeasureUnits().getValue());
        }

        saveGroupFragment(editMeasureVarGroup);
        saveGroupTypes(editMeasureVarGroup);
        setVarGroupUIList(measureGrouping);
        editMeasureVarGroup = null;
        cancelAddEdit();
        edited = true;
    }
    
    private void setVarGroupUIList(VarGroupingUI varGroupingUI){
        varGroupingUI.setVarGroupUIList( new VarGroupUIList ((List)varGroupingUI.getVarGroupUI(), true));
    }

    public void saveFilterFragment(ActionEvent ae){
        String chkGroupName = (String) getInputFilterGroupName().getValue();

        if (chkGroupName.isEmpty() || chkGroupName.trim().equals("")) {
            getVDCRenderBean().getFlash().put("warningMessage", "Please enter a Filter Name.");
            return;
        }
    
        if(addFilterGroup){
            if (checkForDuplicateEntries(editFilterVarGroup.getVarGroup().getGroupAssociation(), chkGroupName, true, null )){
                return;
            }
            addFilterGroupSave();
        } else {
            if (checkForDuplicateEntries(editFilterVarGroup.getVarGroup().getGroupAssociation(), chkGroupName, true, editFilterVarGroup.getVarGroup() )){
                return;
            }            
        }

        editFilterVarGroup.getVarGroup().setName(chkGroupName);

        saveGroupFragment(editFilterVarGroup);
        saveGroupTypes(editFilterVarGroup);
        if (editFilterVarGrouping!=null){ 
            setVarGroupUIList(editFilterVarGrouping);
        }
        editFilterVarGroup = null;
        getInputFilterGroupName().setValue("");
        cancelAddEdit();
        edited = true;
    }

    private void saveGroupFragment(VarGroupUI varGroupIn){
        updateVariablesByGroup(varGroupIn);
        updateGroupTypesByGroup(varGroupIn);
    }

    private void saveGroupTypes(VarGroupUI varGroupIn){
        List <VarGroupType> removeList = new ArrayList();
        for (VarGroupTypeUI varGroupTypeTest : varGroupIn.getVarGroupTypes()){
            if (!varGroupTypeTest.isSelected()){
                VarGroupType varGroupTypeRemove = varGroupTypeTest.getVarGroupType();{
                    for (VarGroupType varGroupType :varGroupIn.getVarGroup().getGroupTypes()){
                        if (varGroupTypeRemove.getName().equals(varGroupType.getName())){
                           removeList.add(varGroupTypeRemove);
                        }
                    }
                }
            }
        }

        if (removeList.size() > 0){
            for (VarGroupType removeType: removeList){
                varGroupIn.getVarGroup().getGroupTypes().remove(removeType);
            }
       }
    }


    private void updateVariablesByGroup(VarGroupUI varGroupUIin) {

        List<Long> priorVariablesSelected = new ArrayList();
        List<Long> newVariablesSelected = new ArrayList();
        List<Long> variablesToDelete = new ArrayList();
        List<Long> variablesToAdd = new ArrayList();

        if (varGroupUIin.getDataVariablesSelected() != null) {
            for (Long id : varGroupUIin.getDataVariablesSelected()) {
                priorVariablesSelected.add(id);
            }
            varGroupUIin.getDataVariablesSelected().clear();
        } else {
            varGroupUIin.setDataVariablesSelected(new ArrayList());
        }

        for (DataVariableUI dataVariableUI : dvGenericListUI) {
            for (DataVariableUI dvTest : dvGenericFilteredListUI) {
                if (dvTest.getDataVariable().equals(dataVariableUI.getDataVariable())) {
                    dataVariableUI.setSelected(dvTest.isSelected());
                }
            }
        }

        int countSelected = 0;
        for (DataVariableUI dataVariableUI : dvGenericListUI) {
            if (dataVariableUI.isSelected()) {
                newVariablesSelected.add(dataVariableUI.getDataVariable().getId());
                varGroupUIin.getDataVariablesSelected().add(dataVariableUI.getDataVariable().getId());
                countSelected++;
            }
        }

        varGroupUIin.setNumberOfVariablesSelected(new Long(countSelected));

        for (int i = 0; i < priorVariablesSelected.size(); i++) {
            boolean match = false;
            for (int j = 0; j < newVariablesSelected.size(); j++) {
                if (priorVariablesSelected.get(i).equals(newVariablesSelected.get(j))) {
                    match = true;
                }
            }
            if (!match) {
                variablesToDelete.add(priorVariablesSelected.get(i));
            }
        }

        for (int i = 0; i < newVariablesSelected.size(); i++) {
            boolean match = false;
            for (int j = 0; j < priorVariablesSelected.size(); j++) {
                if (newVariablesSelected.get(i).equals(priorVariablesSelected.get(j))) {
                    match = true;
                }
            }
            if (!match) {
                variablesToAdd.add(newVariablesSelected.get(i));
            }
        }
        if (!variablesToDelete.isEmpty()) {
            deleteVariablesFromGroup(varGroupUIin.getVarGroup(), variablesToDelete);
        }
        if (!variablesToAdd.isEmpty()) {
            addDVMappingsByGroup(varGroupUIin, variablesToAdd);
        }
    }
    
    private void updateGroupTypesByGroup(VarGroupUI varGroupUIin) {

        if (varGroupUIin.getVarGroupTypesSelectItems() != null) {
            varGroupUIin.getVarGroupTypesSelectItems().clear();
        } else {
            varGroupUIin.setVarGroupTypesSelectItems(new ArrayList());
        }
        for (VarGroupTypeUI varGroupTypeUI : varGroupUIin.getVarGroupTypes()) {
            if (varGroupTypeUI.isSelected()) {
                varGroupUIin.getVarGroupTypesSelectItems().add(varGroupTypeUI);
            }
        }

        VarGroup varGroup = varGroupUIin.getVarGroup();
        varGroup.getGroupTypes().clear();
        String groupTypesSelectedString = "";
        for (VarGroupTypeUI vgtUI : varGroupUIin.getVarGroupTypesSelectItems()) {
            varGroup.getGroupTypes().add(vgtUI.getVarGroupType());
            if (groupTypesSelectedString.isEmpty()) {
                groupTypesSelectedString += vgtUI.getVarGroupType().getName();
            } else {
                groupTypesSelectedString = groupTypesSelectedString + ", " + vgtUI.getVarGroupType().getName();
            }
        }
        varGroupUIin.setGroupTypesSelectedString(groupTypesSelectedString);
    }
     
    public void saveFilterGrouping() {
        String testString = (String) getInputFilterGroupingName().getValue();
        boolean duplicates = false;
        if (!testString.isEmpty()) {
            List filterVarGroupings = new ArrayList();
            for (VarGroupingUI varGroupingUI : filterGroupings) {
                filterVarGroupings.add(varGroupingUI.getVarGrouping());
            }

            if (addFilterGrouping) {
                duplicates = visualizationService.checkForDuplicateGroupings(filterVarGroupings, testString, null);
            } else {
                duplicates = visualizationService.checkForDuplicateGroupings(filterVarGroupings, testString, editFilterVarGrouping.getVarGrouping());
            }

            if (duplicates) {
                getVDCRenderBean().getFlash().put("warningMessage", "This name already exists. Please enter another.");
                return;
            }
            
            if (addFilterGrouping) {
                addFilterGroupingSave(testString);
                cancelAddEdit();
            } else {
                if (!getInputFilterGroupingName().getValue().equals("")) {
                    editFilterVarGrouping.getVarGrouping().setName(testString);
                }
                cancelAddEdit();
            }
            editFilterVarGrouping = null;
        } else {
            getVDCRenderBean().getFlash().put("warningMessage", "Please enter a Filter Group Name.");
            return;
        }
        edited = true;
    }
    
    public void addFilterGroupAction(ActionEvent ae) {
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)) {
            uiComponent = uiComponent.getParent();
        }
        editFilterVarGrouping = (VarGroupingUI) dataTableFilterGrouping.getRowData();
        editFilterVarGroup = null;
        editFilterVarGroup = addGroupingGroup(editFilterVarGrouping);
        SessionRenderer.addCurrentSession("editFilterVarGroup");
        SessionRenderer.render("editFilterVarGroup");
        cancelAddEdit();
        editFilterGroup = true;
        addFilterGroup = true;
    }


    public void addMeasureGroup() {
        editMeasureVarGroup = new VarGroupUI();
        editMeasureVarGroup = addGroupingGroup(measureGrouping);
        cancelAddEdit();
        editMeasure = true;
        addMeasureGroup = true;
    }

    public void addSourceGroup() {
        editSourceVarGroup = addGroupingGroup(sourceGrouping);
        cancelAddEdit();
        editSource = true;
        addSourceGroup = true;
    }

    private VarGroupUI addGroupingGroup(VarGroupingUI varGroupingUIIn) {
        VarGroupUI newElem = new VarGroupUI();
        newElem.setVarGroup(new VarGroup());
        newElem.getVarGroup().setGroupAssociation(varGroupingUIIn.getVarGrouping());
        newElem.getVarGroup().setGroupTypes(new ArrayList());

        newElem.setVarGroupTypes(new ArrayList());
        newElem.getVarGroupTypes().clear();
        List<VarGroupTypeUI> allVarGroupTypes = (List<VarGroupTypeUI>) varGroupingUIIn.getVarGroupTypesUI();
        if (!allVarGroupTypes.isEmpty()) {
            for (VarGroupTypeUI varGroupTypeUI : allVarGroupTypes) {
                varGroupTypeUI.setSelected(false);
                newElem.getVarGroupTypes().add(varGroupTypeUI);
            }
        } else {
            newElem.setVarGroupTypes(new ArrayList());
            newElem.getVarGroupTypes().clear();
        }
        dvGenericListUI = dvNumericListUI;
        loadEmptyVariableList();
        return newElem;
    }
    
    private void loadEmptyVariableList(){
        Iterator iterator = dvGenericListUI.iterator();
        dvGenericFilteredListUI.clear();
        while (iterator.hasNext() ){
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            dataVariableUI.setSelected(false);
            dvGenericFilteredListUI.add(dataVariableUI);
        }
    }

    private void addMeasureGroupSave() {
        int i = measureGrouping.getVarGrouping().getVarGroups().size();
        measureGrouping.getVarGrouping().getVarGroups().add(i,  editMeasureVarGroup.getVarGroup());
        measureGrouping.getVarGroupUI().add(editMeasureVarGroup);
        cancelAddEdit();
        editMeasure = false;
        addMeasureGroup = false;
    }

    private void addSourceGroupSave() {
        int i = sourceGrouping.getVarGrouping().getVarGroups().size();
        sourceGrouping.getVarGrouping().getVarGroups().add(i,  editSourceVarGroup.getVarGroup());
        sourceGrouping.getVarGroupUI().add(editSourceVarGroup);
        cancelAddEdit();
        editSource = false;
        addSourceGroup = false;
    }

    private void addFilterGroupSave() {
        editFilterVarGrouping.getVarGroupUI().add(editFilterVarGroup);
        editFilterVarGrouping.getVarGrouping().getVarGroups().add(editFilterVarGroup.getVarGroup());
        cancelAddEdit();
    }

    public void addMeasureTypeButton(){        
        addMeasureType = true;
    }

    public void addFilterTypeButton(){
        addFilterType = true;
    }
    
    public void saveFilterTypeButton() {
        String newElemName = "";
        VarGroupUI varGroupUI = null;
        VarGroupingUI varGroupingUIToEdit = new VarGroupingUI();
        VarGrouping varGroupingTest;
        if (editFilterVarGroup != null && editFilterVarGroup.getVarGroup() != null) {
            varGroupingTest = editFilterVarGroup.getVarGroup().getGroupAssociation();
            varGroupUI = editFilterVarGroup;
        } else {
            varGroupingTest = editFilterVarGrouping.getVarGrouping();
        }

        newElemName = (String) getInputFilterGroupTypeName().getValue();
        if (newElemName.isEmpty()) {
            newElemName = (String) getInputManageFilterGroupTypeName().getValue();
        }

        for (VarGroupingUI varGroupingUI : filterGroupings) {
            if (varGroupingUI.getVarGrouping() == varGroupingTest) {
                varGroupingUIToEdit = varGroupingUI;
            }
        }
        
        saveType (newElemName, varGroupingUIToEdit, varGroupUI);
        addFilterType = false;
        edited = true;
    }
    
    public void saveMeasureTypeButton() {
        String newElemName;
        VarGroupUI varGroupUI = null;
        if (editMeasureVarGroup != null && editMeasureVarGroup.getVarGroup() != null) {
            varGroupUI = editMeasureVarGroup;
        }

        newElemName = (String) getInputMeasureGroupTypeName().getValue();
        if (newElemName.isEmpty()) {
            newElemName = (String) getInputManageMeasureGroupTypeName().getValue();
        }
        saveType (newElemName, measureGrouping, varGroupUI);
        addMeasureType = false;
        edited = true;
    }
    
    private void saveType(String newElemName, VarGroupingUI addToGroupingUI, VarGroupUI addToVarGroup){
        VarGroupType newElem = new VarGroupType();
        newElem.setVarGrouping(addToGroupingUI.getVarGrouping());
        newElem.setName(newElemName);
        newElem.setVarGrouping(addToGroupingUI.getVarGrouping());
        if (checkForDuplicateEntries(addToGroupingUI.getVarGrouping(), newElem.getName(), false, null)) {
            return;
        }
        addToGroupingUI.getVarGrouping().getVarGroupTypes().add(newElem);
        VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
        varGroupTypeUI.setVarGroupType(newElem);
        addToGroupingUI.getVarGroupTypesUI().add(varGroupTypeUI);
        if (addToVarGroup != null) {
            addNewTypeToGroupUI(addToVarGroup, varGroupTypeUI);
        }
    }

    private boolean checkForDuplicateEntries(VarGrouping varGrouping,  String name, boolean group, Object testObject){
        boolean duplicates = visualizationService.checkForDuplicateEntries(varGrouping, name, group, testObject);
            if (duplicates) {
                getVDCRenderBean().getFlash().put("warningMessage", "This name already exists. Please enter another.");
            }
         return duplicates;
    }

    private void addNewTypeToGroupUI(VarGroupUI varGroupUI, VarGroupTypeUI varGroupTypeUI ){
        varGroupUI.getVarGroupTypes().add(varGroupTypeUI);
    }

    private void removeTypeFromGroupUI(VarGroupUI varGroupUI, VarGroupTypeUI varGroupTypeUI ){
        varGroupUI.getVarGroupTypes().remove(varGroupTypeUI);
    }
    
    public void cancelMeasureTypeButton(){
        addMeasureType = false;
        manageMeasureTypes = true;       
    }
    
    public void cancelFilterTypeButton(){
        addFilterType = false;
        manageFilterTypes = true;        
    }

    public void addMeasureGroupType() {
        cancelAddEdit();
        VarGroupType newElem = new VarGroupType();
        newElem.setVarGrouping(measureGrouping.getVarGrouping());
        measureGrouping.getVarGrouping().getVarGroupTypes().add(newElem);
        VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
        varGroupTypeUI.setVarGroupType(newElem);
        varGroupTypeUI.setEditMode(true);
        if (measureGrouping.getVarGroupTypesUI() == null){
            measureGrouping.setVarGroupTypesUI(new ArrayList());
        }
        measureGrouping.getVarGroupTypesUI().add(varGroupTypeUI);
    }

    public void addFilterGroupType(ActionEvent ae) {
        cancelAddEdit();
        VarGroupType newElem = new VarGroupType();
        newElem.setVarGrouping(editFilterVarGrouping.getVarGrouping());
        editFilterVarGrouping.getVarGrouping().getVarGroupTypes().add(newElem);
        VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
        varGroupTypeUI.setVarGroupType(newElem);
        varGroupTypeUI.setEditMode(true);
        editFilterVarGrouping.getVarGroupTypesUI().add(varGroupTypeUI);
    }

    private void addNewGrouping(VarGroupingUI varGroupingUIin, GroupingType groupingType) {
        VarGrouping varGrouping = new VarGrouping();
        varGrouping.setGroupingType(groupingType);
        varGrouping.setDataTable(dataTable);
        varGrouping.setDataVariableMappings(new ArrayList());
        varGrouping.setGroups(new ArrayList());
        varGrouping.setVarGroupTypes(new ArrayList());
        varGroupingUIin.setVarGrouping(varGrouping);
        varGroupingUIin.setSelectedGroupId(new Long(0));
        varGroupingUIin.setVarGroupTypesUI(new ArrayList());
        varGroupingUIin.setVarGroupUI(new ArrayList());
        if (groupingType.equals(GroupingType.MEASURE)  || groupingType.equals(GroupingType.FILTER)){
            loadGroupingGroupTypes(varGroupingUIin);
        }
        if (groupingType.equals(GroupingType.MEASURE) ){
            varGrouping.setName("Measure");
        }
        if (groupingType.equals(GroupingType.SOURCE) ){
            varGrouping.setName("Source");
        }
        dataTable.getVarGroupings().add(varGrouping);
        varGroupingUIin.setVarGrouping(varGrouping);
    }

    public void addFilterGroupingAction() {
        cancelAddEdit();
        VarGrouping newElem = new VarGrouping();
        newElem.setDataTable(dataTable);
        addFilterGrouping = true;
        editFilterGrouping = true;
    }

    private void addFilterGroupingSave(String name){
        VarGroupingUI varGroupingUI = new VarGroupingUI();
        addNewGrouping(varGroupingUI, GroupingType.FILTER);
        varGroupingUI.getVarGrouping().setName(name); 
        filterGroupings.add(varGroupingUI);
    }

    public void deleteFilterGroup(ActionEvent ae) {

        HtmlDataTable dataTableDelete = dataTableFilterGroup;
        /*
         * Getting filter group from outer grouping similiar to issue with
         * editFilterGroup with and outer h:datatable and inner ice datatable
         * original code was VarGroupUI varGroupUI2 = (VarGroupUI)
         * dataTable2.getRowData();
         */
        int index = dataTableDelete.getRowIndex();
        VarGroupingUI varGroupingUIin = (VarGroupingUI) ae.getComponent().getAttributes().get("grouping");
        VarGroupUI varGroupUIin = varGroupingUIin.getVarGroupUIList().getVarGroupUIList().get(index);
        deleteGroup(varGroupingUIin, varGroupUIin);
        setVarGroupUIList(varGroupingUIin);  
    }

    public void deleteMeasureGroup(){
        VarGroupUI varGroupUItoDelete = (VarGroupUI) dataTableVarGroup.getRowData();
        deleteGroup(measureGrouping, varGroupUItoDelete);
        setVarGroupUIList(measureGrouping);        
    }

    public void deleteSourceGroup() {
        VarGroupUI varGroupUItoDelete = (VarGroupUI) dataTableSourceVarGroup.getRowData();
        deleteGroup(sourceGrouping, varGroupUItoDelete);
    }
    
    public void deleteGroup( VarGroupingUI varGroupingToDeleteFrom, VarGroupUI groupToDeleteUI) {
        deleteVariablesFromGroup(groupToDeleteUI.getVarGroup());
        deleteTypesFromGroup(groupToDeleteUI.getVarGroup());
        visualizationService.removeCollectionElement(varGroupingToDeleteFrom.getVarGrouping().getVarGroups(), groupToDeleteUI.getVarGroup());
        varGroupingToDeleteFrom.getVarGroupUI().remove(groupToDeleteUI);
        varGroupingToDeleteFrom.getVarGrouping().getVarGroups().remove(groupToDeleteUI.getVarGroup());
        edited = true;
    }


    public void deleteFilterGrouping(ActionEvent ae) {
        List<VarGroupType> removeListGroupType = new ArrayList();
        List<VarGroup> removeListGroup = new ArrayList();
        List<VarGroupType> typeList;
        List<VarGroup> groupList;

        HtmlDataTable dataTableDelete = dataTableFilterGrouping;
        VarGroupingUI varGroupingUIDelete = (VarGroupingUI) dataTableDelete.getRowData();

        for (VarGroupType groupType : varGroupingUIDelete.getVarGrouping().getVarGroupTypes()) {
            removeListGroupType.add(groupType);
        }

        typeList = new ArrayList(varGroupingUIDelete.getVarGrouping().getVarGroupTypes());
        for (VarGroup group : varGroupingUIDelete.getVarGrouping().getVarGroups()) {
            removeListGroup.add(group);
        }

        groupList = new ArrayList(varGroupingUIDelete.getVarGrouping().getVarGroups());

        if (dataTableDelete.getRowCount() > 0) {
            filterGroupings.remove(varGroupingUIDelete);
            varGroupings.remove(varGroupingUIDelete.getVarGrouping());
        }

        for (VarGroupUI groupUI : varGroupingUIDelete.getVarGroupUI()) {
            for (VarGroupTypeUI varGroupTypeTest : groupUI.getVarGroupTypes()) {
                varGroupTypeTest.setSelected(false);
            }
            saveGroupTypes(groupUI);
        }
        for (VarGroup group : removeListGroup) {
            visualizationService.removeCollectionElement(groupList, group);
        }

        for (VarGroupType groupType : removeListGroupType) {
            visualizationService.removeCollectionElement(typeList, groupType);
        }
        visualizationService.removeCollectionElement(varGroupings, varGroupingUIDelete.getVarGrouping());
        edited = true;
    }

    public String exit(){
        if (edited){
            setShowInProgressPopup(true);
            return "";
        }
        return cancel();
    }

    public String cancel() {
        visualizationService.cancel();
        return returnToStudy();
    }
    
    public String runMigration() {
        migrateVisualization();
        loadDataTable(false);
        showCommands = true;
        selectFile = false;
        showMigrationPopup = false;
        return "";
    }
    
    private void migrateVisualization() {
        
        visualizationService.setDataTableFromStudyFileId(sourceFileId);
        DataTable sourceDT = visualizationService.getDataTable();
        visualizationService.setDataTableFromStudyFileId(studyFileId);
        dataTable = visualizationService.getDataTable();
        for (VarGrouping varGroupingSource : sourceDT.getVarGroupings()) {
            VarGrouping addVarGrouping = new VarGrouping();
            addVarGrouping.setDataTable(dataTable);
            addVarGrouping.setGroupingType(varGroupingSource.getGroupingType());
            addVarGrouping.setName(varGroupingSource.getName());
            addVarGrouping.setGroups(new ArrayList());
            addVarGrouping.setDataVariableMappings(new ArrayList());
            addVarGrouping.setVarGroupTypes(new ArrayList());
            dataTable.getVarGroupings().add(addVarGrouping);
            for (VarGroup varGroupSource : varGroupingSource.getVarGroups()) {
                VarGroup addVarGroup = new VarGroup();
                addVarGroup.setGroupAssociation(addVarGrouping);
                addVarGroup.setName(varGroupSource.getName());
                addVarGroup.setUnits(varGroupSource.getUnits());
                addVarGrouping.getVarGroups().add(addVarGroup);
            }

            for (VarGroupType varGroupTypeSource : varGroupingSource.getVarGroupTypes()) {
                VarGroupType addVarGroupType = new VarGroupType();
                addVarGroupType.setVarGrouping(addVarGrouping);
                addVarGroupType.setName(varGroupTypeSource.getName());
                addVarGroupType.setGroups(new ArrayList());
                for (VarGroup varGroupGroupTypeSource : varGroupTypeSource.getGroups()) {
                    for (VarGroup addGroupGroupTypeSource : addVarGrouping.getVarGroups()) {
                        if (varGroupGroupTypeSource.getName().equals(addGroupGroupTypeSource.getName())) {
                            addVarGroupType.getGroups().add(addGroupGroupTypeSource);
                            if (addGroupGroupTypeSource.getGroupTypes() == null) {
                                addGroupGroupTypeSource.setGroupTypes(new ArrayList());
                            }
                            addGroupGroupTypeSource.getGroupTypes().add(addVarGroupType);
                        }
                    }
                }
                addVarGrouping.getVarGroupTypes().add(addVarGroupType);
            }
            for (DataVariableMapping dataVariableMappingSource : varGroupingSource.getDataVariableMappings()){
                DataVariableMapping adddataVariableMapping = new DataVariableMapping();
                adddataVariableMapping.setVarGrouping(addVarGrouping);
                adddataVariableMapping.setLabel(dataVariableMappingSource.getLabel());
                adddataVariableMapping.setDataTable(dataTable);
                    for (VarGroup addGroupVariableSource : addVarGrouping.getVarGroups()) {
                        if (dataVariableMappingSource.getGroup().getName().equals(addGroupVariableSource.getName())) {
                            adddataVariableMapping.setGroup(addGroupVariableSource);                                 
                        }
                    }
                for (DataVariable dataVariableTarget: dataTable.getDataVariables()){
                    if (dataVariableTarget.getName().equals(dataVariableMappingSource.getDataVariable().getName())){
                        adddataVariableMapping.setDataVariable(dataVariableTarget);
                        if (dataVariableTarget.getDataVariableMappings() == null){
                           dataVariableTarget.setDataVariableMappings(new ArrayList()); 
                        }
                        dataVariableTarget.getDataVariableMappings().add(adddataVariableMapping);
                    }
                }
                addVarGrouping.getDataVariableMappings().add(adddataVariableMapping);
            }
        }
        DataVariable xAxisVariableSource = visualizationService.getXAxisVariable(sourceDT.getId());
        DataVariableMapping xAxisMapping = new DataVariableMapping();
        for (DataVariable dataVariableTarget : dataTable.getDataVariables()) {
            if (dataVariableTarget.getName().equals(xAxisVariableSource.getName())) {
                xAxisMapping.setDataVariable(dataVariableTarget);
                xAxisMapping.setX_axis(true);
                xAxisMapping.setLabel(xAxisVariableSource.getLabel());
                if(xAxisMapping.getLabel() == null){
                   xAxisMapping.setLabel(""); 
                }
                xAxisMapping.setDataTable(dataTable);
                if (dataVariableTarget.getDataVariableMappings() == null) {
                    dataVariableTarget.setDataVariableMappings(new ArrayList());
                }
                dataVariableTarget.getDataVariableMappings().add(xAxisMapping);
                xAxisSet = true;
                xAxisVariable = dataVariableTarget;
                xAxisVariableId = dataVariableTarget.getId();
                
            }
        }
        
    }
       
    public String cancelMigration() {
        loadDataTable(true);
        showCommands = true;
        selectFile = false;
        showMigrationPopup = false;
        return "";
    }
    
    private boolean checkVisualizationDisplay(){
        dataTable = new DataTable();
        visualizationService.setDataTableFromStudyFileId(studyFileId);
        dataTable = visualizationService.getDataTable();
        System.out.print("number of groupings " + dataTable.getVarGroupings().size());
        visualizationDisplay = dataTable.getVisualizationDisplay();
        System.out.print("visualizationDisplay" + dataTable.getVisualizationDisplay()==null);
        return dataTable.getVisualizationDisplay()==null;
        
    }
        
    private String returnToStudy() {        
        Long redirectVersionNumber = new Long(0);
        if (studyVersion.getId() == null) {
            redirectVersionNumber = study.getReleasedVersion().getVersionNumber();
        } else {
            redirectVersionNumber = studyVersion.getVersionNumber();
        }
        return "/study/StudyPage?faces-redirect=true&studyId=" + study.getId() + "&versionNumber=" + redirectVersionNumber + "&tab=files&vdcId=" + getVDCRequestBean().getCurrentVDCId();
    }   
       
    boolean showInProgressPopup = false;
    
    public boolean isShowInProgressPopup() {
        return showInProgressPopup;
    }

    public void setShowInProgressPopup(boolean showInProgressPopup) {
        this.showInProgressPopup = showInProgressPopup;
    }
    
    public void togglePopup(ActionEvent event) {
         showInProgressPopup = !showInProgressPopup;
    }
    
    public void toggleMigrationPopup(ActionEvent event) {
         
         showMigrationPopup = !showMigrationPopup;
    }
    
    private void cancelAddEdit(){
        getInputFilterGroupName().setValue("");
        getInputFilterGroupTypeName().setValue("");
        getInputFilterGroupingName().setValue("");
        getInputMeasureGroupTypeName().setValue("");
        getInputMeasureName().setValue("");
        getInputMeasureGroupingName().setValue("");
        getInputMeasureUnits().setValue("");
        getInputVariableFilter().setValue("");
        getInputVariableMeasure().setValue("");
        getInputVariableGeneric().setValue("");

        getMeasureCheckBox().setValue(false);
        getFilterCheckBox().setValue(false);

        editXAxis = false;
        editMeasure = false;
        editSource = false;
        editFilterGroup = false;
        editMeasureGrouping = false;
        addMeasureType = false;
        addFilterType = false;
        addMeasureGroup = false;
        addSourceGroup = false;
        addFilterGroup = false;
        addFilterGrouping = false;
        editMeasureType = false;
        editFilterType = false;
        editFilterGrouping = false;
        manageFilterTypes = false;
        manageMeasureTypes = false;
    }

    public boolean validateForRelease(boolean messages) {
        boolean valid = true;
        List returnListOfErrors = new ArrayList();
        List duplicateVariables;
        String fullErrorMessage = "";

        if (!visualizationService.validateDisplayOptions(dataTable)) {
            if (messages) {
                fullErrorMessage += "Please select at least one view and the default view must be selected.";
            }
            valid = false;
        }

        if (!xAxisSet || !visualizationService.validateXAxisMapping(dataTable, xAxisVariableId)) {
            if (messages) {
                fullErrorMessage += "You must select one X-axis variable and it cannot be mapped to any Measure or Filter.";
            }
            valid = false;
        }

        if (!visualizationService.validateAtLeastOneFilterMapping(dataTable, returnListOfErrors)) {
            if (messages) {
                if (!returnListOfErrors.isEmpty()) {
                    fullErrorMessage += "<p>Measure-filter combinations were found that would result in multiple variables selected at visualization.</p>";
                    boolean newgroup = false;
                    boolean firstVar = true;
                    for (Object errorObject : returnListOfErrors) {
                        if (errorObject instanceof VarGroup) {
                            firstVar = true;
                            if (newgroup) {
                                fullErrorMessage += "</li></ul>";
                            }
                            VarGroup vg = (VarGroup) errorObject;
                            fullErrorMessage += "<ul>";
                            fullErrorMessage += "<li>" + "Measure " + vg.getName() + " contains at least one variable with no associated filter.</li> ";
                        }
                        if (errorObject instanceof DataVariable) {
                            DataVariable dv = (DataVariable) errorObject;
                            if (firstVar) {
                                fullErrorMessage += "<li>Affected Variables: " + dv.getName();
                            } else {
                                fullErrorMessage += ", " + dv.getName();
                            }
                            newgroup = true;
                            firstVar = false;
                        }
                    }
                    fullErrorMessage += "</li></ul>";
                    fullErrorMessage += "<p>To correct this, create filters to limit each measure filter combination to a single variable or assign only one variable to each measure.</p>";
                }
            }


            valid = false;
        }
        returnListOfErrors.clear();

        if (!visualizationService.validateAllGroupsAreMapped(dataTable, returnListOfErrors)) {
            if (messages) {
                if (!returnListOfErrors.isEmpty()) {
                    fullErrorMessage += "<p>Measure-filter combinations were found that would result in no variables selected at visualization.</p>";
                    fullErrorMessage += "<ul>";
                    String filterErrorMessage = "";
                    int filterCount = 0;
                    String measureErrorMessage = "";
                    int measureCount = 0;
                    for (Object varGroupIn : returnListOfErrors) {
                        VarGroup varGroup = (VarGroup) varGroupIn;
                        VarGrouping varGrouping = varGroup.getGroupAssociation();
                        if (varGrouping.getGroupingType().equals(GroupingType.FILTER)) {
                            if (filterCount == 0) {
                                filterErrorMessage += varGroup.getName();
                            } else {
                                filterErrorMessage = filterErrorMessage + ", " + varGroup.getName();
                            }
                            filterCount++;
                        }
                        if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)) {
                            if (measureCount == 0) {
                                measureErrorMessage += varGroup.getName();
                            } else {
                                measureErrorMessage = measureErrorMessage + ", " + varGroup.getName();
                            }
                            measureCount++;
                        }
                    }
                    if (filterCount == 1) {
                        fullErrorMessage = fullErrorMessage + "<li>Filter " + filterErrorMessage + " contains no variables.</li>";
                    }
                    if (filterCount > 1) {
                        fullErrorMessage = fullErrorMessage + "<li>Filters " + filterErrorMessage + " contain no variables.</li>";
                    }
                    if (measureCount == 1) {
                        fullErrorMessage = fullErrorMessage + "<li>Measure " + measureErrorMessage + " contains no variables.</li>";
                    }
                    if (measureCount > 1) {
                        fullErrorMessage = fullErrorMessage + "<li>Measures " + measureErrorMessage + " contain no variables.</li>";
                    }
                    fullErrorMessage += "</ul>";
                    fullErrorMessage += "<p>To correct this, assign at least one variable or remove the empty measures or filters.</p>";

                }

            }
            valid = false;
        }
        returnListOfErrors.clear();

        if (!visualizationService.validateMoreThanZeroMeasureMapping(dataTable, returnListOfErrors)) {
            if (messages) {
                if (!returnListOfErrors.isEmpty()) {
                    fullErrorMessage += "<p>At least one filter was found that is not mapped to any measures.</p>";
                    boolean firstVar = true;
                    boolean newgroup = false;
                    for (Object errorObject : returnListOfErrors) {
                        if (errorObject instanceof VarGroup) {
                            firstVar = true;
                            VarGroup vg = (VarGroup) errorObject;
                            if (newgroup) {
                                fullErrorMessage += "</li></ul>";
                            }
                            fullErrorMessage += "<ul>";
                            fullErrorMessage += "<li>Filter " + vg.getName() + " contains at least one variable that is not assigned to any measure.</li>";
                        }
                        if (errorObject instanceof DataVariable) {
                            DataVariable dv = (DataVariable) errorObject;
                            if (firstVar) {
                                fullErrorMessage += "<li>Affected Variables: " + dv.getName();
                            } else {
                                fullErrorMessage += ", " + dv.getName();
                            }
                            newgroup = true;
                            firstVar = false;
                        }
                    }
                    fullErrorMessage += "</li></ul>";
                    fullErrorMessage += "<p>To correct this, remove unassigned variables or assign them to measures.</p>";
                }
            }

            valid = false;
        }

        returnListOfErrors.clear();

        if (!visualizationService.validateOneMeasureMapping(dataTable, returnListOfErrors)) {
            if (messages) {
                if (!returnListOfErrors.isEmpty()) {

                    fullErrorMessage += "<p>Variables were found that are mapped to multiple measures.</p>";
                    boolean firstGroup = true;
                    boolean newvar = false;
                    for (Object dataVariableIn : returnListOfErrors) {
                        if (dataVariableIn instanceof DataVariable) {
                            if (newvar) {
                                fullErrorMessage += "</li></ul>";
                            }
                            fullErrorMessage += "<ul>";
                            DataVariable dataVariable = (DataVariable) dataVariableIn;
                            fullErrorMessage += "<li>Variable: " + dataVariable.getName() + "</li>";
                            firstGroup = true;

                        }
                        if (dataVariableIn instanceof VarGroup) {
                            VarGroup varGroup = (VarGroup) dataVariableIn;
                            if (firstGroup) {
                                fullErrorMessage += "<li>Measures: " + varGroup.getName();
                            } else {
                                fullErrorMessage += " , " + varGroup.getName();
                            }
                            newvar = true;
                            firstGroup = false;
                        }

                    }
                }
                fullErrorMessage += "</li></ul>";
                fullErrorMessage += "<p>To correct this, map each variable to a single measure.</p>";
            }

            valid = false;
        }

        returnListOfErrors.clear();

        if (!visualizationService.validateOneFilterPerGrouping(dataTable, returnListOfErrors)) {
            if (messages) {
                if (!returnListOfErrors.isEmpty()) {

                    fullErrorMessage += "<p>Variables were found that are mapped to multiple filters within a single filter group.</p>";
                    boolean firstGroup = true;
                    boolean newvar = false;
                    for (Object dataVariableIn : returnListOfErrors) {
                        if (dataVariableIn instanceof DataVariable) {
                            DataVariable dataVariable = (DataVariable) dataVariableIn;
                            if (newvar) {
                                fullErrorMessage += "</li></ul>";
                            }
                            fullErrorMessage += "<ul>";
                            fullErrorMessage += "<li>Variable: " + dataVariable.getName() + "</li>";
                            firstGroup = true;

                        }
                        if (dataVariableIn instanceof VarGroup) {
                            VarGroup varGroup = (VarGroup) dataVariableIn;
                            if (firstGroup) {
                                fullErrorMessage += "<li>Filters: " + varGroup.getName();
                            } else {
                                fullErrorMessage += " , " + varGroup.getName();
                            }
                            firstGroup = false;
                            newvar = true;
                        }
                    }
                }
                fullErrorMessage += "</li></ul>";
                fullErrorMessage += "<p>To correct this, map each variable to a single filter within each group.</p>";
            }

            valid = false;
        }

        if (!visualizationService.validateAtLeastOneMeasureMapping(dataTable)) {
            if (messages) {
                fullErrorMessage += "<p>Measure-filter combinations were found that would result in no variables selected at visualization.</p>";
                fullErrorMessage += "No Measures or filters configured.";
                fullErrorMessage += "<p>To correct this, configure at least one measure or one measure-filter combination.</p>";
            }
            valid = false;
        }
        returnListOfErrors.clear();
        duplicateVariables = visualizationService.getDuplicateMappings(dataTable, returnListOfErrors);
        if (duplicateVariables.size() > 0) {

            if (messages) {
                if (!returnListOfErrors.isEmpty()) {
                    fullErrorMessage += "<p>Measure-filter combinations were found that would result in multiple variables selected at visualization.</p>";
                    boolean firstVar = true;
                    boolean newgroup = false;;
                    for (Object errorObject : returnListOfErrors) {
                        if (errorObject instanceof VarGroup) {
                            if (newgroup) {
                                fullErrorMessage += "</li></ul>";
                            }
                            fullErrorMessage += "<ul>";
                            firstVar = true;
                            VarGroup vg = (VarGroup) errorObject;
                            fullErrorMessage += "<li>Measure " + vg.getName() + " contains multiple variables with insufficient filters to make them uniquely selectable.</li>";
                        }

                        if (errorObject instanceof DataVariable) {
                            DataVariable dv = (DataVariable) errorObject;
                            if (firstVar) {
                                fullErrorMessage += "<li>Affected Variables: " + dv.getName();
                            } else {
                                fullErrorMessage += ", " + dv.getName();
                            }
                            newgroup = true;
                            firstVar = false;
                        }
                    }
                    fullErrorMessage += "</li></ul>";
                    fullErrorMessage += "<p>To correct this, for measures where multiple variables are assigned, also assign each variable to a filter where the measure-filter combinations result in a single variable selected at visualization.</p>";
                }
            }
            returnListOfErrors.clear();
            valid = false;
        }

        if (valid && messages) {
            getVDCRenderBean().getFlash().put("successMessage", "The Data Visualization is valid for release.");
        }
        if (!valid && messages) {
            fullErrorMessage = "This configuration is invalid so it cannot be released.</span></p> " + fullErrorMessage;
            getVDCRenderBean().getFlash().put("warningMessage", fullErrorMessage);
        }
        return valid;
    }

    public String validate() {
        validateForRelease(true);
        return "";
    }

    public String release() {
        if (validateForRelease(true)) {
            dataTable.setVisualizationEnabled(true);
            saveAndContinue();
            return "";
        }
        return "";
    }

    public String unRelease() {
        dataTable.setVisualizationEnabled(false);
        saveAndContinue();
        return "";
    }

/*
    public String saveAndExit(){
       if (dataTable.isVisualizationEnabled()){
           if (!validateForRelease(false)) {
               FacesMessage message = new FacesMessage("Your current changes are invalid. Correct these issues or unrelease your visualization before saving. Click Validate button to get a full list of validation issues.");
               FacesContext fc = FacesContext.getCurrentInstance();
               fc.addMessage(validateButton.getClientId(fc), message);
               return "";
           }
       }
       save();
       return ruturnToStudy();
    }
*/    
    public String saveAndContinue() {
        boolean successMessage = true;
        if (dataTable.isVisualizationEnabled()) {
            if (!validateForRelease(false)) {
                dataTable.setVisualizationEnabled(false);
                getVDCRenderBean().getFlash().put("warningMessage", "Your current changes are invalid. This visualization has been set to 'unreleased'. Click Validate button to get a full list of validation issues.");
                successMessage = false;
            }
        }
        visualizationService.saveAndContinue();

        if (successMessage) {
            getVDCRenderBean().getFlash().put("successMessage", "Successfully saved changes. You may exit or continue editing.");
        }
        edited = false;
        return "";
    }

    private void addDVMappingsByGroup(VarGroupUI varGroupUI, List addList) {
        if (!addList.isEmpty()) {
            for (Object dataVariableId : addList) {
                String id = dataVariableId.toString();
                for (DataVariable dataVariable : dvList) {
                    if (dataVariable.getId() != null && dataVariable.getId().equals(new Long(id))) {
                        DataVariableMapping dataVariableMapping = new DataVariableMapping();
                        dataVariableMapping.setDataTable(dataTable);
                        dataVariableMapping.setDataVariable(dataVariable);
                        dataVariableMapping.setGroup(varGroupUI.getVarGroup());
                        dataVariableMapping.setVarGrouping(varGroupUI.getVarGroup().getGroupAssociation());
                        dataVariableMapping.setX_axis(false);
                        dataVariable.getDataVariableMappings().add(dataVariableMapping);
                        for (VarGrouping varGrouping : varGroupings) {
                            if (varGrouping.equals(varGroupUI.getVarGroup().getGroupAssociation())) {
                                varGrouping.getDataVariableMappings().add(dataVariableMapping);
                            }
                        }
                    }
                }
            }
        }
    }

    private void deleteVariablesFromGroup(VarGroup varGroup, List<Long> longList) {

        List<DataVariableMapping> removeList = new ArrayList();
        List<DataVariable> checkList = new ArrayList(dvList);
        List<DataVariable> dvRemoveList = new ArrayList();
        List<DataVariableMapping> groupingRemoveList = new ArrayList();

        for (DataVariable dataVariable : checkList) {
            for (Long id : longList) {
                if (id.equals(dataVariable.getId())) {
                    dvRemoveList.add(dataVariable);
                }
            }
        }

        for (DataVariable dataVariable : dvRemoveList) {
            List<DataVariableMapping> deleteList = (List<DataVariableMapping>) dataVariable.getDataVariableMappings();
            for (DataVariableMapping dataVariableMapping : deleteList) {
                if (dataVariableMapping.getGroup() != null && !dataVariableMapping.isX_axis()
                        && dataVariableMapping.getGroup().equals(varGroup)) {
                    removeList.add(dataVariableMapping);
                    groupingRemoveList.add(dataVariableMapping);
                }
            }
        }

        for (DataVariableMapping dataVarMappingRemove : removeList) {
            visualizationService.removeCollectionElement(dataVarMappingRemove.getDataVariable().getDataVariableMappings(), dataVarMappingRemove);
        }

        for (DataVariableMapping dataVarMappingRemove : groupingRemoveList) {
            visualizationService.removeCollectionElement(dataVarMappingRemove.getVarGrouping().getDataVariableMappings(), dataVarMappingRemove);
        }
    }

    private void deleteVariablesFromGroup(VarGroup varGroup) {

        List<DataVariableMapping> removeList = new ArrayList();
        List<DataVariable> tempList = new ArrayList(dvList);
        List<DataVariableMapping> groupingRemoveList = new ArrayList();
        for (DataVariable dataVariable : tempList) {
            List<DataVariableMapping> deleteList = (List<DataVariableMapping>) dataVariable.getDataVariableMappings();

            for (DataVariableMapping dataVariableMapping : deleteList) {

                if (dataVariableMapping.getGroup() != null && !dataVariableMapping.isX_axis()
                        && dataVariableMapping.getGroup().getName().equals(varGroup.getName())
                        && dataVariableMapping.getVarGrouping().getGroupingType().equals(varGroup.getGroupAssociation().getGroupingType())) {
                    removeList.add(dataVariableMapping);
                }
            }
            for (DataVariableMapping dataVariableMapping : deleteList) {
                if (dataVariableMapping.getGroup() != null && !dataVariableMapping.isX_axis()
                        && dataVariableMapping.getGroup().getName().equals(varGroup.getName())
                        && dataVariableMapping.getVarGrouping().getGroupingType().equals(varGroup.getGroupAssociation().getGroupingType())) {
                    groupingRemoveList.add(dataVariableMapping);
                }
            }
        }

        for (DataVariableMapping dataVarMappingRemove : removeList) {
            visualizationService.removeCollectionElement(dataVarMappingRemove.getDataVariable().getDataVariableMappings(), dataVarMappingRemove);
        }

        for (DataVariableMapping dataVarMappingRemove : groupingRemoveList) {
            visualizationService.removeCollectionElement(dataVarMappingRemove.getVarGrouping().getDataVariableMappings(), dataVarMappingRemove);
        }
    }
    
    private void deleteTypesFromGroup(VarGroup varGroup) {
        List<VarGroupType> removeList = new ArrayList();
        if (varGroup.getGroupTypes() == null) return;
        for (VarGroupType varGroupType : varGroup.getGroupTypes()) {
            removeList.add(varGroupType);
        }

        for (VarGroupType varGroupTypeRemove : removeList) {
            visualizationService.removeCollectionElement(varGroup.getGroupTypes(), varGroupTypeRemove);
        }
    }
    
    private void deleteGroupsFromType(VarGroupType varGroupType) {
        List<VarGroup> removeList = new ArrayList();
        if (varGroupType.getGroups() == null) return;
        for (VarGroup varGroup : varGroupType.getGroups()) {
            removeList.add(varGroup);
        }
        for (VarGroup varGroupRemove : removeList) {
            visualizationService.removeCollectionElement(varGroupType.getGroups(), varGroupRemove);
        }
    }
    
    private void resetDVMappingsXAxis() {

        List<DataVariableMapping> removeList = new ArrayList();
        List<DataVariable> tempList = new ArrayList(dvList);
        for (DataVariable dataVariable : tempList) {
            List<DataVariableMapping> deleteList = (List<DataVariableMapping>) dataVariable.getDataVariableMappings();
            for (DataVariableMapping dataVariableMapping : deleteList) {
                if (dataVariableMapping.getGroup() == null) {
                    removeList.add(dataVariableMapping);
                }
            }
        }

        for (DataVariableMapping dataVarMappingRemove : removeList) {
            if (dataVarMappingRemove.getDataVariable() != null) {
                visualizationService.removeCollectionElement(dataVarMappingRemove.getDataVariable().getDataVariableMappings(), dataVarMappingRemove);
            }

        }

        if (xAxisVariableId != null && !xAxisVariableId.equals(new Long(0))) {
            for (DataVariable dataVariable : dvList) {
                if (dataVariable.getId() != null && dataVariable.getId().equals(xAxisVariableId)) {
                    DataVariableMapping dataVariableMapping = new DataVariableMapping();
                    dataVariableMapping.setDataTable(dataTable);
                    dataVariableMapping.setDataVariable(dataVariable);
                    dataVariableMapping.setGroup(null);
                    dataVariableMapping.setVarGrouping(null);
                    dataVariableMapping.setX_axis(true);
                    dataVariableMapping.setLabel(xAxisUnits);
                    dataVariable.getDataVariableMappings().add(dataVariableMapping);
                }
            }
        }
    }

    public String save() {
       visualizationService.saveAll();
       return "";
    }

    private HtmlInputText inputXAxisUnits;

    public HtmlInputText getInputXAxisUnits() {
        return this.inputXAxisUnits;
    }

    public void setInputXAxisUnits(HtmlInputText inputXAxisUnits) {
        this.inputXAxisUnits = inputXAxisUnits;
    }
    private HtmlInputText inputVariableGeneric;

    public HtmlInputText getInputVariableGeneric() {
        return this.inputVariableGeneric;
    }

    public void setInputVariableGeneric(HtmlInputText inputVariableGeneric) {
        this.inputVariableGeneric = inputVariableGeneric;
    }
    
    private HtmlInputText inputVariableFilter;

    public HtmlInputText getInputVariableFilter() {
        return this.inputVariableFilter;
    }

    public void setInputVariableFilter(HtmlInputText inputVariableFilter) {
        this.inputVariableFilter = inputVariableFilter;
    }
    private HtmlInputText inputVariableMeasure;

    public HtmlInputText getInputVariableMeasure() {
        return this.inputVariableMeasure;
    }

    public void setInputVariableMeasure(HtmlInputText inputVariableMeasure) {
        this.inputVariableMeasure = inputVariableMeasure;
    }
    private HtmlInputText inputVariableSource;

    public HtmlInputText getInputVariableSource() {
        return this.inputVariableSource;
    }

    public void setInputVariableSource(HtmlInputText inputVariableSource) {
        this.inputVariableSource = inputVariableSource;
    }
    private HtmlInputText inputMeasureName;

    public HtmlInputText getInputMeasureName() {
        return this.inputMeasureName;
    }

    public void setInputMeasureName(HtmlInputText inputMeasureName) {
        this.inputMeasureName = inputMeasureName;
    }
    private HtmlInputText inputSourceName;

    public HtmlInputText getInputSourceName() {
        return this.inputSourceName;
    }

    public void setInputSourceName(HtmlInputText inputSourceName) {
        this.inputSourceName = inputSourceName;
    }
    private HtmlInputText inputMeasureUnits;

    public HtmlInputText getInputMeasureUnits() {
        return this.inputMeasureUnits;
    }

    public void setInputMeasureUnits(HtmlInputText inputMeasureUnits) {
        this.inputMeasureUnits = inputMeasureUnits;
    }
    private HtmlInputText inputGroupTypeName;

    public HtmlInputText getInputGroupTypeName() {
        return this.inputGroupTypeName;
    }

    public void setInputGroupTypeName(HtmlInputText inputGroupTypeName) {
        this.inputGroupTypeName = inputGroupTypeName;
    }
    private HtmlInputText inputFilterGroupTypeName;

    public HtmlInputText getInputFilterGroupTypeName() {
        return this.inputFilterGroupTypeName;
    }

    public void setInputFilterGroupTypeName(HtmlInputText inputFilterGroupTypeName) {
        this.inputFilterGroupTypeName = inputFilterGroupTypeName;
    }
    private HtmlInputText inputMeasureGroupTypeName;

    public HtmlInputText getInputMeasureGroupTypeName() {
        return this.inputMeasureGroupTypeName;
    }

    public void setInputMeasureGroupTypeName(HtmlInputText inputMeasureGroupTypeName) {
        this.inputMeasureGroupTypeName = inputMeasureGroupTypeName;
    }
    private HtmlInputText inputManageMeasureGroupTypeName;

    public HtmlInputText getInputManageMeasureGroupTypeName() {
        return this.inputManageMeasureGroupTypeName;
    }

    public void setInputManageMeasureGroupTypeName(HtmlInputText inputMeasureGroupTypeName) {
        this.inputManageMeasureGroupTypeName = inputMeasureGroupTypeName;
    }
    private HtmlInputText inputManageFilterGroupTypeName;

    public HtmlInputText getInputManageFilterGroupTypeName() {
        return this.inputManageFilterGroupTypeName;
    }

    public void setInputManageFilterGroupTypeName(HtmlInputText inputFilterGroupTypeName) {
        this.inputManageFilterGroupTypeName = inputFilterGroupTypeName;
    }
    private HtmlInputText inputMeasureGroupingName;

    public HtmlInputText getInputMeasureGroupingName() {
        return this.inputMeasureGroupingName;
    }

    public void setInputMeasureGroupingName(HtmlInputText inputMeasureGroupingName) {
        this.inputMeasureGroupingName = inputMeasureGroupingName;
    }
    private HtmlInputText editGroupTypeName;

    public HtmlInputText getEditGroupTypeName() {
        return this.editGroupTypeName;
    }

    public void setEditGroupTypeName(HtmlInputText editGroupTypeName) {
        this.editGroupTypeName = editGroupTypeName;
    }
    private HtmlInputText editFilterGroupTypeName;

    public HtmlInputText getEditFilterGroupTypeName() {
        return this.editFilterGroupTypeName;
    }

    public void setEditFilterGroupTypeName(HtmlInputText editFilterGroupTypeName) {
        this.editFilterGroupTypeName = editFilterGroupTypeName;
    }
    private HtmlInputText editMeasureGroupTypeName;

    public HtmlInputText getEditMeasureGroupTypeName() {
        return this.editMeasureGroupTypeName;
    }

    public void setEditMeasureGroupTypeName(HtmlInputText editMeasureGroupTypeName) {
        this.editMeasureGroupTypeName = editMeasureGroupTypeName;
    }
    private HtmlInputText editManageFilterGroupTypeName;

    public HtmlInputText getEditManageFilterGroupTypeName() {
        return this.editManageFilterGroupTypeName;
    }

    public void setEditManageFilterGroupTypeName(HtmlInputText editManageFilterGroupTypeName) {
        this.editManageFilterGroupTypeName = editManageFilterGroupTypeName;
    }
    private HtmlInputText editManageMeasureGroupTypeName;

    public HtmlInputText getEditManageMeasureGroupTypeName() {
        return this.editManageMeasureGroupTypeName;
    }

    public void setEditManageMeasureGroupTypeName(HtmlInputText editManageMeasureGroupTypeName) {
        this.editManageMeasureGroupTypeName = editManageMeasureGroupTypeName;
    }

   public List<SelectItem> loadStudyFileSelectItems(){
        List selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem(0, "Select a File"));
        for (FileMetadata fileMetaData: studyVersion.getFileMetadatas()){
            if (fileMetaData.getStudyFile().isSubsettable()){
                    selectItems.add(new SelectItem(fileMetaData.getStudyFile().getId(), fileMetaData.getStudyFile().getFileName()));
            }
        }
        return selectItems;
    }
   
    public List<SelectItem> loadStudyFileMigrationSelectItems(){
        List selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem(0, "Select a File"));
        for (FileMetadata fileMetaData: studyVersion.getFileMetadatas()){
            if (fileMetaData.getStudyFile().isSubsettable() && !fileMetaData.getStudyFile().getDataTables().get(0).getVarGroupings().isEmpty() ){
                    selectItems.add(new SelectItem(fileMetaData.getStudyFile().getId(), fileMetaData.getStudyFile().getFileName()));
            }
        }
        return selectItems;
    }

    public void updateGenericGroupVariableList(String checkString) {

        for (DataVariableUI dataVariableUI : dvGenericListUI) {
            for (DataVariableUI dvTest : dvGenericFilteredListUI) {
                if (dvTest.getDataVariable().equals(dataVariableUI.getDataVariable())) {
                    dataVariableUI.setSelected(dvTest.isSelected());
                }
            }

        }

        dvGenericFilteredListUI.clear();
        for (DataVariableUI dataVariableUI : dvGenericListUI) {
            dataVariableUI.setDisplay(false);
            if (dataVariableUI.getDataVariable().getName().contains(checkString)) {
                dataVariableUI.setDisplay(true);
                DataVariableUI dvAdd = new DataVariableUI();
                dvAdd.setDataVariable(dataVariableUI.getDataVariable());
                dvAdd.setSelected(dataVariableUI.isSelected());
                dvAdd.setDisplay(true);
                dvGenericFilteredListUI.add(dvAdd);
            }
        }
    }

    public String updateMeasureList(){
        String checkString = (String) getInputVariableMeasure().getValue();
        updateGenericGroupVariableList(checkString);
        selectOneMeasureVariableClick();
        return "";
    }

    public String updateFilterList(){
        String checkString = (String) getInputVariableFilter().getValue();
        updateGenericGroupVariableList(checkString);
        selectOneFilterVariableClick();
        return "";
    }

    public String updateXAxisList(){
        String checkString = (String) getInputVariableGeneric().getValue();
        updateGenericGroupVariableList(checkString);
        return "";
    }

    public String updateSourceList(){
        String checkString = (String) getInputVariableSource().getValue();
        updateGenericGroupVariableList(checkString);
        return "";
    }

    public void selectAllMeasureVariables(ValueChangeEvent ce){
        selectAllVariablesPrivate((Boolean)measureCheckBox.getValue());
        forceRender(ce);
    }

    public void selectAllFilterVariables(ValueChangeEvent ce){
        selectAllVariablesPrivate((Boolean)filterCheckBox.getValue());
        forceRender(ce);
    }

    public void selectAllFilterVariablesClick(){
        boolean value = (Boolean)filterCheckBox.getValue();
        selectAllVariablesPrivate(value);
    }

    public void selectAllMeasureVariablesClick(){
        boolean value = (Boolean)measureCheckBox.getValue();
        selectAllVariablesPrivate(value);
    }

    public void selectAllSourceVariablesClick(){
        boolean value = (Boolean)sourceCheckBox.getValue();
        selectAllVariablesPrivate(value);
    }

    public void selectOneFilterVariableClick(){
        boolean setSelected = checkSelectAllVariablesPrivate();
        filterCheckBox.setSelected(setSelected) ;
    }

    public void selectOneMeasureVariableClick(){
        boolean setSelected = checkSelectAllVariablesPrivate();
        measureCheckBox.setSelected(setSelected) ;
    }
    
    public void selectOneSourceVariableClick(){
        boolean setSelected = checkSelectAllVariablesPrivate();
        sourceCheckBox.setSelected(setSelected) ;
    }
    
    public void setEdited(){
        edited = true;
    }

    private void selectAllVariablesPrivate(boolean check){
        
        Iterator iterator = dvGenericFilteredListUI.iterator();
        
        while (iterator.hasNext() ){
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            if (dataVariableUI.isDisplay()){
                dataVariableUI.setSelected(check);
            }
        }
    }

    public void checkSelectAllFilterVariables(ValueChangeEvent ce){
        boolean setSelected = checkSelectAllVariablesPrivate();

        filterCheckBox.setSelected(setSelected) ;

        forceRender(ce);

    }

    private boolean checkSelectAllVariablesPrivate(){
        boolean allSelected = true;

        Iterator iterator = dvGenericFilteredListUI.iterator();

        while (iterator.hasNext() ){
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            if (!dataVariableUI.isSelected()){
                allSelected = false;
            }
        }

        return allSelected;
    }

    private void forceRender(ValueChangeEvent ce){
        PhaseId phase = ce.getPhaseId();
        if (phase.equals(PhaseId.INVOKE_APPLICATION)) {

            FacesContext.getCurrentInstance().renderResponse();
        } else {
            ce.setPhaseId(PhaseId.INVOKE_APPLICATION);
            ce.queue();
        }

    }

    public void selectAllMeasureButton(){
        Iterator iterator = dvGenericFilteredListUI.iterator();
        boolean selectMeasureVariables = (Boolean)measureCheckBox.getValue();
        while (iterator.hasNext() ){
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            dataVariableUI.setSelected(selectMeasureVariables);
        }

    }

    private HtmlDataTable dataTableVarGroup;

    public HtmlDataTable getDataTableVarGroup() {
        return this.dataTableVarGroup;
    }

    public void setDataTableVarGroup(HtmlDataTable dataTableVarGroup) {
        this.dataTableVarGroup = dataTableVarGroup;
    }

    private HtmlDataTable dataTableSourceVarGroup;

    public HtmlDataTable getDataTableSourceVarGroup() {
        return this.dataTableSourceVarGroup;
    }

    public void setDataTableSourceVarGroup(HtmlDataTable dataTableSourceVarGroup) {
        this.dataTableSourceVarGroup = dataTableSourceVarGroup;
    }

    private HtmlDataTable dataTableXAxis;

    public HtmlDataTable getDataTableXAxis() {
        return this.dataTableXAxis;
    }

    public void setDataTableXAxis(HtmlDataTable dataTableXAxis) {
        this.dataTableXAxis = dataTableXAxis;
    }

    private HtmlDataTable dataTableFilterGroup;

    public HtmlDataTable getDataTableFilterGroup() {
        return this.dataTableFilterGroup;
    }

    public void setDataTableFilterGroup(HtmlDataTable dataTableFilterGroup) {
        this.dataTableFilterGroup = dataTableFilterGroup;
    }

    private HtmlDataTable dataTableFilterGrouping;

    public HtmlDataTable getDataTableFilterGrouping() {
        return this.dataTableFilterGrouping;
    }

    public void setDataTableFilterGrouping(HtmlDataTable dataTableFilterGrouping) {
        this.dataTableFilterGrouping = dataTableFilterGrouping;
    }

    private HtmlDataTable dataTableFilterGroupType;

    public HtmlDataTable getDataTableFilterGroupType() {
        return this.dataTableFilterGroupType;
    }

    public void setDataTableFilterGroupType(HtmlDataTable dataTableFilterGroupType) {
        this.dataTableFilterGroupType = dataTableFilterGroupType;
    }
    
    private HtmlDataTable dataTableManageFilterGroupType;

    public HtmlDataTable getDataTableManageFilterGroupType() {
        return this.dataTableManageFilterGroupType;
    }

    public void setDataTableManageFilterGroupType(HtmlDataTable dataTableManageFilterGroupType) {
        this.dataTableManageFilterGroupType = dataTableManageFilterGroupType;
    }

    private HtmlDataTable dataTableManageMeasureGroupType;

    public HtmlDataTable getDataTableManageMeasureGroupType() {
        return this.dataTableManageMeasureGroupType;
    }

    public void setDataTableManageMeasureGroupType(HtmlDataTable dataTableManageMeasureGroupType) {
        this.dataTableManageMeasureGroupType = dataTableManageMeasureGroupType;
    }

    private HtmlDataTable dataTableMeasureGroupType;

    public HtmlDataTable getDataTableMeasureGroupType() {
        return this.dataTableMeasureGroupType;
    }

    public void setDataTableMeasureGroupType(HtmlDataTable dataTableMeasureGroupType) {
        this.dataTableMeasureGroupType = dataTableMeasureGroupType;
    }
    public List<VarGroupingUI> getFilterGroupings() {
        return filterGroupings;
    }

    public void setFilterGroupings(List<VarGroupingUI> filterGroupings) {
        this.filterGroupings = filterGroupings;
    }

    private HtmlInputText inputFilterGroupingName;

    public HtmlInputText getInputFilterGroupingName() {
        return this.inputFilterGroupingName;
    }
    public void setInputFilterGroupingName(HtmlInputText inputFilterGroupingName) {
        this.inputFilterGroupingName = inputFilterGroupingName;
    }

    private HtmlDataTable dataTableFilterGroups;

    public HtmlDataTable getDataTableFilterGroups() {
        return dataTableFilterGroups;
    }

    public void setDataTableFilterGroups(HtmlDataTable dataTableFilterGroups) {
        this.dataTableFilterGroups = dataTableFilterGroups;
    }

    private HtmlInputText inputFilterGroupName;

    public HtmlInputText getInputFilterGroupName() {
        return this.inputFilterGroupName;
    }
    public void setInputFilterGroupName(HtmlInputText inputFilterGroupName) {
        this.inputFilterGroupName = inputFilterGroupName;
    }

    private HtmlCommandLink addFilterGroupLink;

    public HtmlCommandLink getAddFilterGroupLink() {
        return this.addFilterGroupLink;
    }
    public void setAddFilterGroupLink(HtmlCommandLink addFilterGroupLink) {
        this.addFilterGroupLink = addFilterGroupLink;
    }

    private HtmlCommandLink addFilterGroupTypeLink;

    public HtmlCommandLink getAddFilterGroupTypeLink() {
        return this.addFilterGroupTypeLink;
    }
    public void setAddFilterGroupTypeLink(HtmlCommandLink addFilterGroupTypeLink) {
        this.addFilterGroupTypeLink = addFilterGroupTypeLink;
    }

    private HtmlCommandLink addSecondFilterGroupTypeLink;

    public HtmlCommandLink getAddSecondFilterGroupTypeLink() {
        return this.addSecondFilterGroupTypeLink;
    }
    public void setAddSecondFilterGroupTypeLink(HtmlCommandLink addFilterGroupTypeLink) {
        this.addSecondFilterGroupTypeLink = addFilterGroupTypeLink;
    }
    private HtmlCommandLink deleteFilterGroupLink;

    public HtmlCommandLink getDeleteFilterGroupLink() {
        return this.deleteFilterGroupLink;
    }
    public void setDeleteFilterGroupLink(HtmlCommandLink deleteFilterGroupLink) {
        this.deleteFilterGroupLink = deleteFilterGroupLink;
    }
    
    public DataVariable getxAxisVariable() {
        return xAxisVariable;
    }

    public void setxAxisVariable(DataVariable xAxisVariable) {
        this.xAxisVariable = xAxisVariable;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Long getStudyFileId() {
        return studyFileId;
    }

    public void setStudyFileId(Long studyFileId) {
        this.studyFileId = studyFileId;
    }

    private List files;

    public List getFiles() {
        return files;
    }

    public void setFiles(List files) {
        this.files = files;
    }

    public List<SelectItem> getStudyFileIdSelectItems() {
        return studyFileIdSelectItems;
    }

    public void setStudyFileIdSelectItems(List<SelectItem> studyFileIdSelectItems) {
        this.studyFileIdSelectItems = studyFileIdSelectItems;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public Long getxAxisVariableId() {
        return xAxisVariableId;
    }

    public void setxAxisVariableId(Long xAxisVariableId) {
        this.xAxisVariableId = xAxisVariableId;
    }

    HtmlSelectOneMenu selectStudyFile;

    public HtmlSelectOneMenu getSelectStudyFile() {
        return selectStudyFile;
    }

    public void setSelectStudyFile(HtmlSelectOneMenu selectStudyFile) {
        this.selectStudyFile = selectStudyFile;
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    private HtmlCommandButton validateButton = new HtmlCommandButton();
    private HtmlCommandButton releaseButton = new HtmlCommandButton();

    public HtmlCommandButton getValidateButton() {
        return validateButton;
    }

    public void setValidateButton(HtmlCommandButton hit) {
        this.validateButton = hit;
    }

    public HtmlCommandButton getReleaseButton() {
        return releaseButton;
    }

    public void setReleaseButton(HtmlCommandButton releaseButton) {
        this.releaseButton = releaseButton;
    }

    public boolean isShowCommands() {
        return showCommands;
    }

    public void setShowCommands(boolean showCommands) {
        this.showCommands = showCommands;
    }

    public boolean isSelectFile() {
        return selectFile;
    }

    public void setSelectFile(boolean selectFile) {
        this.selectFile = selectFile;
    }
    public List<DataVariable> getDvList() {
        return dvList;
    }

    public void setDvList(List<DataVariable> dvList) {
        this.dvList = dvList;
    }

    public String getxAxisUnits() {
        return xAxisUnits;
    }

    public void setxAxisUnits(String xAxisUnits) {
        this.xAxisUnits = xAxisUnits;
    }

    public List<DataVariable> getDvFilteredList() {
        if (getInputVariableFilter() == null) return dvList;
        if (getInputVariableFilter().getValue() == null) return dvList;
        if (getInputVariableFilter().getValue().equals("")) return dvList;
        return dvFilteredList;
    }

    public void setDvFilteredList(List<DataVariable> dvFilteredList) {
        this.dvFilteredList = dvFilteredList;
    }
    
    public List<DataVariableUI> getDvGenericListUI() {
        return dvGenericListUI;
    }

    public boolean isShowFilterVariables() {
        return showFilterVariables;
    }

    public void setShowFilterVariables(boolean showFilterVariables) {
        this.showFilterVariables = showFilterVariables;
    }

    public boolean isShowMeasureVariables() {
        return showMeasureVariables;
    }

    public void setShowMeasureVariables(boolean showMeasureVariables) {
        this.showMeasureVariables = showMeasureVariables;
    }

    public boolean isEditXAxis() {
        return editXAxis;
    }

    public void setEditXAxis(boolean editXAxis) {
        this.editXAxis = editXAxis;
    }

    public void setDvGenericFilteredListUI(List<DataVariableUI> dvGenericFilteredListUI) {
        this.dvGenericFilteredListUI = dvGenericFilteredListUI;
    }

    public List<DataVariableUI> getDvGenericFilteredListUI() {
        List<DataVariableUI> returnList = new ArrayList();
        Iterator iterator = dvGenericListUI.iterator();
        while (iterator.hasNext()) {
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            if (dataVariableUI.isDisplay()) {
                DataVariableUI dvAdd = new DataVariableUI();
                dvAdd.setDataVariable(dataVariableUI.getDataVariable());
                dvAdd.setSelected(dataVariableUI.isSelected());
                dvAdd.setDisplay(true);
                returnList.add(dvAdd);
            }
        }
        return dvGenericFilteredListUI;
    }

    public VarGroupUI getEditFragmentVarGroup() {
        return editFragmentVarGroup;
    }

    public void setEditFragmentVarGroup(VarGroupUI editFragmentVarGroup) {
        this.editFragmentVarGroup = editFragmentVarGroup;
    }
    public boolean isEditFilterGroup() {
        return editFilterGroup;
    }

    public void setEditFilter(boolean editFilterGroup) {
        this.editFilterGroup = editFilterGroup;
    }

    public boolean isEditMeasure() {
        return editMeasure;
    }

    public void setEditMeasure(boolean editMeasure) {
        this.editMeasure = editMeasure;
    }
    public VarGroupUI getEditFilterVarGroup() {
        return editFilterVarGroup;
    }

    public void setEditFilterVarGroup(VarGroupUI editFilterVarGroup) {
        this.editFilterVarGroup = editFilterVarGroup;
    }

    public VarGroupUI getEditMeasureVarGroup() {
        return editMeasureVarGroup;
    }

    public void setEditMeasureVarGroup(VarGroupUI editMeasureVarGroup) {
        this.editMeasureVarGroup = editMeasureVarGroup;
    }

    public boolean isxAxisSet() {
         return xAxisSet;
    }

    public boolean isAddFilterType() {
        return addFilterType;
    }

    public void setAddFilterType(boolean addFilterType) {
        this.addFilterType = addFilterType;
    }

    public boolean isAddMeasureType() {
        return addMeasureType;
    }

    public void setAddMeasureType(boolean addMeasureType) {
        this.addMeasureType = addMeasureType;
    }

    public boolean isHasFilterGroupings() {
        return hasFilterGroupings;
    }

    public void setHasFilterGroupings(boolean hasFilterGroupings) {
        this.hasFilterGroupings = hasFilterGroupings;
    }

    public boolean isHasFilterGroups() {
        return hasFilterGroups;
    }

    public void setHasFilterGroups(boolean hasFilterGroups) {
        this.hasFilterGroups = hasFilterGroups;
    }

    public boolean isHasMeasureGroups() {
        return hasMeasureGroups;
    }

    public void setHasMeasureGroups(boolean hasMeasureGroups) {
        this.hasMeasureGroups = hasMeasureGroups;
    }

    public boolean isEditMeasureType() {
        return editMeasureType;
    }

    public void setEditMeasureType(boolean editMeasureType) {
        this.editMeasureType = editMeasureType;
    }

    public boolean isEditFilterType() {
        return editFilterType;
    }

    public void setEditFilterType(boolean editFilterType) {
        this.editFilterType = editFilterType;
    }
    
    public boolean isEditFilterGrouping() {
        return editFilterGrouping;
    }

    public void setEditFilterGrouping(boolean editFilterGrouping) {
        this.editFilterGrouping = editFilterGrouping;
    }

    public VarGroupingUI getEditFilterVarGrouping() {
        return editFilterVarGrouping;
    }

    public void setEditFilterVarGrouping(VarGroupingUI editFilterVarGrouping) {
        this.editFilterVarGrouping = editFilterVarGrouping;
    }

    public String getStudyFileName() {
        return studyFileName;
    }

    public void setStudyFileName(String studyFileName) {
        this.studyFileName = studyFileName;
    }

    public boolean isEditMeasureGrouping() {
        return editMeasureGrouping;
    }

    public boolean isManageMeasureTypes() {
        return manageMeasureTypes;
    }

    public boolean isAddFilterGroup() {
        return addFilterGroup;
    }

    public boolean isAddMeasureGroup() {
        return addMeasureGroup;
    }

    public boolean isAddFilterGrouping() {
        return addFilterGrouping;
    }

    public boolean isManageFilterTypes() {
        return manageFilterTypes;
    }

    private HtmlSelectBooleanCheckbox sourceCheckBox;

    public HtmlSelectBooleanCheckbox getSourceCheckBox() {
        return sourceCheckBox;
    }

    public void setSourceCheckBox(HtmlSelectBooleanCheckbox sourceCheckBox) {
        this.sourceCheckBox = sourceCheckBox;
    }
    
    private HtmlSelectBooleanCheckbox measureCheckBox;

    public HtmlSelectBooleanCheckbox getMeasureCheckBox() {
        return measureCheckBox;
    }

    public void setMeasureCheckBox(HtmlSelectBooleanCheckbox measureCheckBox) {
        this.measureCheckBox = measureCheckBox;
    }

    private HtmlSelectBooleanCheckbox filterCheckBox;

    public HtmlSelectBooleanCheckbox getFilterCheckBox() {
        return filterCheckBox;
    }

    public void setFilterCheckBox(HtmlSelectBooleanCheckbox filterCheckBox) {
        this.filterCheckBox = filterCheckBox;
    }

    public StudyUI getStudyUI() {
        return studyUI;
    }

    public void setStudyUI(StudyUI studyUI) {
        this.studyUI = studyUI;
    }

    public boolean isEditSource() {
        return editSource;
    }

    public void setEditSource(boolean editSource) {
        this.editSource = editSource;
    }

    public boolean isAddSourceGroup() {
        return addSourceGroup;
    }

    public void setAddSourceGroup(boolean addSource) {
        this.addSourceGroup = addSource;
    }

    public void changeTab(TabChangeEvent te){
        cancelAddEdit();
    }
    
    public Long getSourceFileId() {
        return sourceFileId;
    }

    public void setSourceFileId(Long sourceFileId) {
        this.sourceFileId = sourceFileId;
    }
    public boolean isShowMigrationPopup() {
        return showMigrationPopup;
    }

    public void setShowMigrationPopup(boolean showMigrationPopup) {
        this.showMigrationPopup = showMigrationPopup;
    }

    public boolean isHasExploration() {
        return hasExploration;
    }

    public void setHasExploration(boolean hasExploration) {
        this.hasExploration = hasExploration;
    }

    public List<SelectItem> getStudyFileHasExplorationSelectItems() {
        return studyFileHasExplorationSelectItems;
    }

    public void setStudyFileHasExplorationSelectItems(List<SelectItem> studyFileHasExplorationSelectItems) {
        this.studyFileHasExplorationSelectItems = studyFileHasExplorationSelectItems;
    }
}
