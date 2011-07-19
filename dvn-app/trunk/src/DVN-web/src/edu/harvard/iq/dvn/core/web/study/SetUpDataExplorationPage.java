/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.async.render.SessionRenderer;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import edu.harvard.iq.dvn.core.study.DataTable;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.context.effects.JavascriptContext;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author skraffmiller
 */
@EJB(name="visualizationService", beanInterface=edu.harvard.iq.dvn.core.visualization.VisualizationServiceBean.class)
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
    private VisualizationDisplay visualizationDisplay;

    private DataVariable xAxisVariable = new DataVariable();
    private Long xAxisVariableId =  new Long(0);
    private StudyVersion studyVersion;

    private Study study;
    private StudyUI studyUI;
    private Long studyFileId = new Long(0);
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
           
            loadDataTable();
            showCommands = true;
            selectFile = false;
        }

    }

    private void loadDataTable(){

        dataTable = new DataTable();
        visualizationService.setDataTableFromStudyFileId(studyFileId);
        dataTable = visualizationService.getDataTable();
        varGroupings = dataTable.getVarGroupings();
        study = visualizationService.getStudyFromStudyFileId(studyFileId);
        dvList = dataTable.getDataVariables();
        measureGrouping = new VarGroupingUI();
        sourceGrouping = new VarGroupingUI();
        loadVarGroupingUIs();

         dvNumericListUI = loadDvListUIs(false);
         dvDateListUI = loadDvListUIs(true);
         xAxisVariable = visualizationService.getXAxisVariable(dataTable.getId());
         xAxisVariableId = xAxisVariable.getId();
         if (xAxisVariableId.intValue() > 0){
             xAxisSet = true;
         } else {
             xAxisSet = false;
             editXAxisAction();
         }
         
         if (xAxisSet){
            for (DataVariableMapping mapping : xAxisVariable.getDataVariableMappings()){
                if (mapping.isX_axis())xAxisUnits = mapping.getLabel();
            }
         }

         if (measureGrouping.getVarGrouping() == null){
             addNewGrouping(measureGrouping, GroupingType.MEASURE);
         } else {
             if (!measureGrouping.getVarGroupUI().isEmpty()){
                 hasMeasureGroups = true;
             }
         }

         if(sourceGrouping.getVarGrouping() == null){
            addNewGrouping(sourceGrouping, GroupingType.SOURCE);
         }

         if (!filterGroupings.isEmpty()){
             hasFilterGroupings = true;
         }
         visualizationDisplay = dataTable.getVisualizationDisplay();

         if (visualizationDisplay == null){
             visualizationDisplay = getDefaultVisualizationDisplay();
             dataTable.setVisualizationDisplay(visualizationDisplay);
         }
         edited = false;
         
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
            if (varGrouping.getGroupingType().equals(GroupingType.FILTER)){
                setVarGroupUIList(vgLocalUI, true);
            } else {
                setVarGroupUIList(vgLocalUI, false);
            }
            
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




    public void setMeasureGroups(){
        
    }

    public void setMeasureGroupTypes(){

    }

    public List<VarGroupTypeUI> getMeasureGroupTypes() {
        if (measureGrouping == null) return null;

        return (List) measureGrouping.getVarGroupTypesUI();
       
    }

    public List <DataVariableUI> loadDvListUIs(boolean isDate){

        List <DataVariableUI> dvListUILocG = new ArrayList();
        for (DataVariable dataVariable : dvList){
            if ((isDate &&  ("date".equals(dataVariable.getFormatCategory())  || "time".equals(dataVariable.getFormatCategory()))) ||
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


    private void setVarGroupUI(VarGroupingUI varGroupingUI) {
        List<VarGroupUI> varGroupUIList = new ArrayList();
        VarGrouping varGroupingIn = varGroupingUI.getVarGrouping();
        varGroupingIn.getVarGroupTypes();

                   List <VarGroup> varGroups = new ArrayList();
                   varGroups = (List<VarGroup>) varGroupingIn.getVarGroups();
                    if (varGroups !=null ) {
                       for(VarGroup varGroup: varGroups){
                           VarGroupUI varGroupUILoc = new VarGroupUI();
                           varGroupUILoc.setVarGroup(varGroup);
                           varGroupUILoc.getVarGroup().getName();
                           varGroupUILoc.setVarGroupTypes(new ArrayList());
                           varGroupUILoc.setVarGroupTypesSelectItems(new ArrayList());
                           varGroupUILoc.setDataVariablesSelected(new ArrayList());
                           List <VarGroupType> varGroupTypes = new ArrayList();

                           List <DataVariable> dataVariables = new ArrayList();

                                dataVariables = (List<DataVariable>) visualizationService.getDataVariableMappingsFromDataTableGroup(dataTable, varGroup);
                                int selectedVariables = 0;
                                if (dataVariables !=null ) {
                                    for(DataVariable dataVariable: dataVariables){
                                        varGroupUILoc.getDataVariablesSelected().add(new Long(dataVariable.getId()));
                                        selectedVariables++;
                                    }
                                }
                                varGroupUILoc.setNumberOfVariablesSelected(new Long(selectedVariables));

                           varGroupTypes = (List<VarGroupType>) varGroupUILoc.getVarGroup().getGroupTypes();
                           String groupTypesSelectedString  = "";
                            if (varGroupTypes !=null ) {

                            for(VarGroupType varGroupType: varGroupTypes){
                                VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
                                varGroupTypeUI.setVarGroupType(varGroupType);
                                varGroupTypeUI.getVarGroupType().getName();
                                varGroupType.getName();

                                varGroupUILoc.getVarGroupTypesSelectItems().add(varGroupTypeUI);
                                if (groupTypesSelectedString.isEmpty()){
                                    groupTypesSelectedString+=varGroupTypeUI.getVarGroupType().getName();
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

        varGroupingUI.setVarGroupUI(varGroupUIList) ;
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
            
            if (varGrouping !=null){
                if (varGrouping.getGroupingType().equals(GroupingType.FILTER)){
                    for (VarGroupingUI testGrouping : filterGroupings){
                        if(testGrouping.getVarGrouping().getName().equals(varGrouping.getName())){
                            for (VarGroup varGroup : varGrouping.getVarGroups()){
                                for (VarGroupType testGroupType: varGroup.getGroupTypes()){
                                    if (testGroupType.getName().equals(varGroupTypeIn.getName())){                                       
                                        FacesMessage message = new FacesMessage("You may not delete a type that is assigned to a measure or filter.");
                                        FacesContext fc = FacesContext.getCurrentInstance();
                                        fc.addMessage(validateButton.getClientId(fc), message);
                                        return true;                                       
                                    }
                                }
                            }
                        }
                    }
                }
                if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                    for (VarGroup varGroup: measureGrouping.getVarGrouping().getVarGroups()){
                         for(VarGroupType testGroupType: varGroup.getGroupTypes()){
                             if (testGroupType.getName().equals(varGroupTypeIn.getName())){
                                  FacesMessage message = new FacesMessage("You may not delete a type that is assigned to a measure or filter.");
                                  FacesContext fc = FacesContext.getCurrentInstance();
                                  fc.addMessage(validateButton.getClientId(fc), message);
                                  return true;
                             }
                         }
                    }
                }


                
            }

                return false;
 }
 
 private boolean hasAssignedGroupTypes(VarGroup varGroupIn){                       
          if(  !varGroupIn.getGroupTypes().isEmpty()) {
                FacesMessage message = new FacesMessage("You may not delete a group that is assigned to a type.");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(validateButton.getClientId(fc), message);
                return true;                 
          }
       return false;
 }
 
 

 public void deleteGroupType(ActionEvent ae){
    boolean measure = false;
    boolean filter = false;
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
         HtmlDataTable dataTable2 = (HtmlDataTable) uiComponent;

         if (uiComponent.equals(dataTableManageMeasureGroupType)){
            measure = true;
         }
         if (uiComponent.equals(dataTableMeasureGroupType)){
             measure = true;
         }

         if (uiComponent.equals(dataTableManageFilterGroupType)){
            filter = true;
         }
         if (uiComponent.equals(dataTableFilterGroupType)){
             filter = true;
         }

        if (dataTable2.getRowCount()>0) {
            VarGroupTypeUI varGroupTypeUI2 = (VarGroupTypeUI) dataTable2.getRowData();
            VarGroupType varGroupType = varGroupTypeUI2.getVarGroupType();
            List varGroupTypeList = (List) dataTable2.getValue();
            if (hasAssignedGroups(varGroupType)){
                return;
            }


            Iterator iterator = varGroupTypeList.iterator();
            List deleteList = new ArrayList();
            while (iterator.hasNext() ){
                VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) iterator.next();
                VarGroupType data = varGroupTypeUI.getVarGroupType();
                deleteList.add(data);
            }
            visualizationService.removeCollectionElement(deleteList,dataTable2.getRowIndex());

             if (filter) {
                for(VarGroupingUI varGroupingUI: filterGroupings){
                    if (varGroupingUI.getVarGrouping() == (varGroupType.getVarGrouping())){
                        varGroupingUI.getVarGroupTypesUI().remove(varGroupTypeUI2);
                        varGroupingUI.getVarGrouping().getVarGroupTypes().remove(varGroupType);
                        dataTableManageFilterGroupType.setValue(varGroupingUI.getVarGroupTypesUI());
                        dataTable2.setValue(varGroupingUI.getVarGroupTypesUI());
                    }
                }
                 if (editFilterVarGroup !=null &&  editFilterVarGroup.getVarGroup() != null){
                    removeTypeFromGroupUI(editFilterVarGroup, varGroupTypeUI2 );
                }
             }
             if (measure) {
                 measureGrouping.getVarGroupTypesUI().remove(varGroupTypeUI2);
                 measureGrouping.getVarGrouping().getVarGroupTypes().remove(varGroupType);
                if (editMeasureVarGroup != null && editMeasureVarGroup.getVarGroup() != null){
                    removeTypeFromGroupUI(editMeasureVarGroup, varGroupTypeUI2 );
                }
             }

        }
        
        edited = true;
    }




    public void editMeasureGroup (ActionEvent ae){
        
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupUI varGroupUI = new VarGroupUI();
        varGroupUI = (VarGroupUI) tempTable.getRowData();

        dvGenericFilteredListUI.clear();
        dvGenericListUI = dvNumericListUI;
        for (DataVariableUI dataVariableUI: dvGenericListUI){
            dataVariableUI.setSelected(false);
            if (varGroupUI.getDataVariablesSelected() != null){
                for (Object dv : varGroupUI.getDataVariablesSelected() ){
                    Long testId = (Long) dv;
                    if (testId.equals(dataVariableUI.getDataVariable().getId())){
                        dataVariableUI.setSelected(true);
                    }
                }

            }

              dvGenericFilteredListUI.add(dataVariableUI);
        }
        varGroupUI.setVarGroupTypes(new ArrayList());

                List <VarGroupTypeUI> allVarGroupTypes = (List<VarGroupTypeUI>) measureGrouping.getVarGroupTypesUI();
                for (VarGroupTypeUI varGroupTypeUI : allVarGroupTypes) {
                    varGroupTypeUI.setSelected(false);
                     List <VarGroupType> varGroupTypes = new ArrayList();
                        varGroupTypes = (List<VarGroupType>) varGroupUI.getVarGroup().getGroupTypes();
                        if (varGroupTypes !=null ) {
                            for(VarGroupType varGroupType: varGroupTypes){
                                if (varGroupType == varGroupTypeUI.getVarGroupType()){
                                    varGroupTypeUI.setSelected(true);
                                }

                            }

                        }
                    varGroupUI.getVarGroupTypes().add(varGroupTypeUI);
                }
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

        dvGenericFilteredListUI.clear();
        dvGenericListUI = dvNumericListUI;
        for (DataVariableUI dataVariableUI: dvGenericListUI){
            dataVariableUI.setSelected(false);
            if (varGroupUI.getDataVariablesSelected() != null){
                for (Object dv : varGroupUI.getDataVariablesSelected() ){
                    Long testId = (Long) dv;
                    if (testId.equals(dataVariableUI.getDataVariable().getId())){
                        dataVariableUI.setSelected(true);
                    }
                }

            }

              dvGenericFilteredListUI.add(dataVariableUI);
        }
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
        dataTableFilterGrouping.getChildren().clear();
        varGroupTypeUI.setEditMode(false);
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
        edited = true;
    }

    public void editFilterGroup (ActionEvent ae){
        
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupUI varGroupUI = (VarGroupUI) tempTable.getRowData();

        varGroupUI.getVarGroup().getName();
        dvGenericFilteredListUI.clear();
        getDataTableFilterGroupType().getChildren().clear();

        /*
         * #{SetUpDataExplorationPage.editFilterVarGroup.varGroupTypes}"
                   rendered="#{SetUpDataExplorationPage.editFilterGroup}" binding="#{SetUpDataExplorationPage.dataTableFilterGroupType}">
         */

        dvGenericListUI = dvNumericListUI;
        for (DataVariableUI dataVariableUI: dvGenericListUI){
            dataVariableUI.setSelected(false);
            if (varGroupUI.getDataVariablesSelected() != null){
                for (Object dv : varGroupUI.getDataVariablesSelected() ){
                    Long testId = (Long) dv;
                    if (testId.equals(dataVariableUI.getDataVariable().getId())){
                        dataVariableUI.setSelected(true);
                    }
                }
            }

                dvGenericFilteredListUI.add(dataVariableUI);
        }

        varGroupUI.setVarGroupTypes(new ArrayList());
        for (VarGroupingUI varGroupingUI: filterGroupings){
            if (varGroupingUI.getVarGrouping() == (varGroupUI.getVarGroup().getGroupAssociation())){
                
                List <VarGroupTypeUI> allVarGroupTypes = (List<VarGroupTypeUI>) varGroupingUI.getVarGroupTypesUI();
                for (VarGroupTypeUI varGroupTypeUI : allVarGroupTypes) {
                    varGroupTypeUI.setSelected(false);
                     List <VarGroupType> varGroupTypes = new ArrayList();
                        varGroupTypes = (List<VarGroupType>) varGroupUI.getVarGroup().getGroupTypes();
                        if (varGroupTypes !=null ) {
                            for(VarGroupType varGroupType: varGroupTypes){
                                if (varGroupType == varGroupTypeUI.getVarGroupType()){
                                    varGroupTypeUI.setSelected(true);
                                }

                            }

                        }
                    varGroupUI.getVarGroupTypes().add(varGroupTypeUI);
                }
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
            FacesMessage message = new FacesMessage("You may not select more than one variable");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
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
            FacesMessage message = new FacesMessage("Please Enter a Measure Label");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
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
            FacesMessage message = new FacesMessage("Please Enter a Source Name");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
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
            FacesMessage message = new FacesMessage("Please Enter a Measure Name");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
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
        setVarGroupUIList(measureGrouping, true);
        editMeasureVarGroup = null;
        cancelAddEdit();
        edited = true;
    }
    
    private void setVarGroupUIList(VarGroupingUI varGrouping, boolean sort){
        varGrouping.setVarGroupUIList( new VarGroupUIList ((List)varGrouping.getVarGroupUI(), true));
    }

    public void saveFilterFragment(ActionEvent ae){
        String chkGroupName = (String) getInputFilterGroupName().getValue();

        if (chkGroupName.isEmpty() || chkGroupName.trim().equals("")) {
            FacesMessage message = new FacesMessage("Please Enter a Filter Name");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
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
            setVarGroupUIList(editFilterVarGrouping, true);
        }
        editFilterVarGroup = null;
        getInputFilterGroupName().setValue("");
        dataTableFilterGrouping.getChildren().clear();

        cancelAddEdit();
        edited = true;
    }

    private void saveGroupFragment(VarGroupUI varGroupIn){
        updateVariableByGroup(varGroupIn);
        /*resetDVMappingsByGroup(varGroupIn);*/
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


    private void updateVariableByGroup(VarGroupUI varGroupUIin){

        List <Long> priorVariablesSelected = new ArrayList();
        List <Long> newVariablesSelected = new ArrayList();
        List <Long> variablesToDelete = new ArrayList();
        List <Long> variablesToAdd = new ArrayList();

        if (varGroupUIin.getDataVariablesSelected() != null){
            for (Long id:varGroupUIin.getDataVariablesSelected()){
                priorVariablesSelected.add(id);
            }
            varGroupUIin.getDataVariablesSelected().clear();
        } else {
            varGroupUIin.setDataVariablesSelected(new ArrayList());
        }

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
                newVariablesSelected.add(dataVariableUI.getDataVariable().getId());
                varGroupUIin.getDataVariablesSelected().add(dataVariableUI.getDataVariable().getId());
                countSelected++;
            }
        }

        varGroupUIin.setNumberOfVariablesSelected(new Long(countSelected));

        for (int i=0;i<priorVariablesSelected.size();i++) {            
            boolean match = false;
            for (int j=0;j<newVariablesSelected.size(); j++) {
		if(priorVariablesSelected.get(i).equals(newVariablesSelected.get(j))) {                    
                    match = true;
                }
            }
            if (!match){
                variablesToDelete.add(priorVariablesSelected.get(i));
            }
	}

        for (int i=0;i<newVariablesSelected.size();i++) {
            boolean match = false;
            for (int j=0;j<priorVariablesSelected.size(); j++) {
		if(newVariablesSelected.get(i).equals(priorVariablesSelected.get(j))) {                   
                    match = true;
                }
            }
            if (!match){
                variablesToAdd.add(newVariablesSelected.get(i));
            }
	}
        if (!variablesToDelete.isEmpty()){
            System.out.println("trying to delete");
            deleteVariablesFromGroup(varGroupUIin.getVarGroup(), variablesToDelete);
        }
        if (!variablesToAdd.isEmpty()){
            addDVMappingsByGroup(varGroupUIin, variablesToAdd);
        }


        

        if (varGroupUIin.getVarGroupTypesSelectItems() != null) {
            varGroupUIin.getVarGroupTypesSelectItems().clear();
        }  else {
           varGroupUIin.setVarGroupTypesSelectItems(new ArrayList());
        }

        for (VarGroupTypeUI varGroupTypeUI: varGroupUIin.getVarGroupTypes()){
            if (varGroupTypeUI.isSelected()){
                varGroupUIin.getVarGroupTypesSelectItems().add(varGroupTypeUI);
            }
        }



             VarGroup varGroup = varGroupUIin.getVarGroup();

             varGroup.getGroupTypes().clear();
             String groupTypesSelectedString = "";
             for (VarGroupTypeUI vgtUI: varGroupUIin.getVarGroupTypesSelectItems()){
                 varGroup.getGroupTypes().add(vgtUI.getVarGroupType());
                         if (groupTypesSelectedString.isEmpty()){
                               groupTypesSelectedString+=vgtUI.getVarGroupType().getName();
                           } else {
                              groupTypesSelectedString = groupTypesSelectedString + ", " + vgtUI.getVarGroupType().getName();
                           }
                 
             }

        varGroupUIin.setGroupTypesSelectedString(groupTypesSelectedString);
    }

    public void saveFilterGrouping(){
        String testString = (String) getInputFilterGroupingName().getValue();
        boolean duplicates = false;
        if (!testString.isEmpty()){
            List filterVarGroupings = new ArrayList();
            for (VarGroupingUI varGroupingUI : filterGroupings){
                filterVarGroupings.add(varGroupingUI.getVarGrouping());

            }

            if (addFilterGrouping){
                 duplicates = visualizationService.checkForDuplicateGroupings(filterVarGroupings, testString, null     );
            } else {
                duplicates = visualizationService.checkForDuplicateGroupings(filterVarGroupings, testString, editFilterVarGrouping.getVarGrouping());
            }
            
            if (duplicates) {
                FacesContext fc = FacesContext.getCurrentInstance();
                String fullErrorMessage = "This name already exists.  Please enter another.  <br>" ;
                FacesMessage message = new FacesMessage(fullErrorMessage);
                fc.addMessage(validateButton.getClientId(fc), message);
                JavascriptContext.addJavascriptCall(fc, "initRoundedCorners();" );
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
            editFilterVarGrouping  = null;

        }  else {
            FacesMessage message = new FacesMessage("Please Enter a Filter Group Name");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
            return;
        }
            
        edited = true;
    }
    public void addFilterGroupAction(ActionEvent ae) {
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        getDataTableFilterGroupType().getChildren().clear();
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

    private VarGroupUI addGroupingGroup(VarGroupingUI varGroupingUIIn){
        VarGroupUI newElem = new VarGroupUI();
        newElem.setVarGroup(new VarGroup());
        newElem.getVarGroup().setGroupAssociation(varGroupingUIIn.getVarGrouping());
        newElem.getVarGroup().setGroupTypes(new ArrayList());

        newElem.setVarGroupTypes(new ArrayList());
        newElem.getVarGroupTypes().clear();
        List <VarGroupTypeUI> allVarGroupTypes = (List<VarGroupTypeUI>) varGroupingUIIn.getVarGroupTypesUI();
        if (!allVarGroupTypes.isEmpty()){
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

    private void loadEmptyVariableList(){
        Iterator iterator = dvGenericListUI.iterator();
        dvGenericFilteredListUI.clear();
        while (iterator.hasNext() ){
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            dataVariableUI.setSelected(false);
            dvGenericFilteredListUI.add(dataVariableUI);
        }

    }

    private void addFilterGroupSave() {

        editFilterVarGrouping.getVarGroupUI().add(editFilterVarGroup);
        editFilterVarGrouping.getVarGrouping().getVarGroups().add(editFilterVarGroup.getVarGroup());

        dataTableFilterGrouping.getChildren().clear();
        cancelAddEdit();

    }



    public void addMeasureTypeButton(){
        
        addMeasureType = true;
    }

    public void addFilterTypeButton(){

        addFilterType = true;
    }

    public void saveMeasureTypeButton(){
        boolean groupEdit = false;
        VarGroupType newElem = new VarGroupType();
        if (editMeasureVarGroup != null && editMeasureVarGroup.getVarGroup() != null ) {
            groupEdit = true;
        }
        
        newElem.setVarGrouping(measureGrouping.getVarGrouping());

        newElem.setName((String)getInputMeasureGroupTypeName().getValue());
        if (newElem.getName().isEmpty()){
            newElem.setName((String)getInputManageMeasureGroupTypeName().getValue());
        }

        VarGrouping varGroupingTest = new VarGrouping();
        if (editMeasureVarGroup != null && editMeasureVarGroup.getVarGroup() != null ) {
            newElem.setVarGrouping(editMeasureVarGroup.getVarGroup().getGroupAssociation());
            varGroupingTest = editMeasureVarGroup.getVarGroup().getGroupAssociation();
            groupEdit = true;
        } else {
            newElem.setVarGrouping(measureGrouping.getVarGrouping());
            varGroupingTest = measureGrouping.getVarGrouping();
        }

        if (checkForDuplicateEntries(varGroupingTest, newElem.getName(), false, null )){
            return;
        }


        measureGrouping.getVarGrouping().getVarGroupTypes().add(newElem);
        VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
        varGroupTypeUI.setVarGroupType(newElem);
        if (measureGrouping.getVarGroupTypesUI() == null){
            measureGrouping.setVarGroupTypesUI(new ArrayList());
        }
        measureGrouping.getVarGroupTypesUI().add(varGroupTypeUI);

        if (groupEdit){
            addNewTypeToGroupUI(editMeasureVarGroup, varGroupTypeUI);
        }
        addMeasureType = false;
        edited = true;
    }

    private boolean checkForDuplicateEntries(VarGrouping varGrouping,  String name, boolean group, Object testObject){
        boolean duplicates = visualizationService.checkForDuplicateEntries(varGrouping, name, group, testObject);
            if (duplicates) {
                FacesContext fc = FacesContext.getCurrentInstance();
                String fullErrorMessage = "This name already exists.  Please enter another.  <br>" ;
                FacesMessage message = new FacesMessage(fullErrorMessage);
                fc.addMessage(validateButton.getClientId(fc), message);
                JavascriptContext.addJavascriptCall(fc, "initRoundedCorners();");
            }

         return duplicates;
    }


    public void saveFilterTypeButton(){
        boolean groupEdit = false;
        VarGroupType newElem = new VarGroupType();
        VarGrouping varGroupingTest = new VarGrouping();

        if (editFilterVarGroup != null && editFilterVarGroup.getVarGroup() != null ) {
            newElem.setVarGrouping(editFilterVarGroup.getVarGroup().getGroupAssociation());
            varGroupingTest = editFilterVarGroup.getVarGroup().getGroupAssociation();
            groupEdit = true;
        } else {
            newElem.setVarGrouping(editFilterVarGrouping.getVarGrouping());
            varGroupingTest = editFilterVarGrouping.getVarGrouping();
        }

        newElem.setName((String)getInputFilterGroupTypeName().getValue());
        if (newElem.getName().isEmpty()){
            newElem.setName((String)getInputManageFilterGroupTypeName().getValue());
        }

        if (checkForDuplicateEntries(varGroupingTest, newElem.getName(), false, null )){
            return;
        }

        for(VarGrouping varGrouping: varGroupings){
             if (varGrouping == newElem.getVarGrouping()){
                varGrouping.getVarGroupTypes().add(newElem);
             }
         }

        VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
        varGroupTypeUI.setVarGroupType(newElem);
        for(VarGroupingUI varGroupingUI: filterGroupings){
             if (varGroupingUI.getVarGrouping() == newElem.getVarGrouping()){
                varGroupingUI.getVarGroupTypesUI().add(varGroupTypeUI);
                dataTableManageFilterGroupType.setValue(varGroupingUI.getVarGroupTypesUI());
                varGroupingUI.getVarGrouping().getVarGroupTypes().add(newElem);
             }
         }
        if (groupEdit){           
            addNewTypeToGroupUI(editFilterVarGroup, varGroupTypeUI);
            dataTableFilterGroupType.setValue(editFilterVarGroup.getVarGroupTypes());
        }

        dataTableFilterGrouping.getChildren().clear();

        addFilterType = false;
        edited = true;
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
        VarGroupType newElem = new VarGroupType();
        newElem.setVarGrouping(editFilterVarGrouping.getVarGrouping());
        editFilterVarGrouping.getVarGrouping().getVarGroupTypes().add(newElem);
        VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
        varGroupTypeUI.setVarGroupType(newElem);
        varGroupTypeUI.setEditMode(true);
        editFilterVarGrouping.getVarGroupTypesUI().add(varGroupTypeUI);
        dataTableFilterGrouping.getChildren().clear();
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
        dataTableFilterGrouping.getChildren().clear();
    }

    public void deleteFilterGroup(ActionEvent ae){



        HtmlDataTable dataTable2 = dataTableFilterGroup;
        if (dataTable2.getRowCount()>0) {
            VarGroupUI varGroupUI2 = (VarGroupUI) dataTable2.getRowData();

            VarGroup varGroup = varGroupUI2.getVarGroup();
            if(hasAssignedGroupTypes(varGroup)){
                return;
            }
                
            deleteVariablesFromGroup(varGroup);
            List varGroupList = (List) dataTable2.getValue();
            Iterator iterator = varGroupList.iterator();
            List deleteList = new ArrayList();
            while (iterator.hasNext() ){
                VarGroupUI varGroupUI = (VarGroupUI) iterator.next();
                VarGroup data = varGroupUI.getVarGroup();
                deleteList.add(data);
            }
            visualizationService.removeCollectionElement(deleteList,dataTable2.getRowIndex());
            for(VarGroupingUI varGroupingUI: filterGroupings){
                if (varGroupingUI.getVarGrouping() == (varGroup.getGroupAssociation())){
                    varGroupingUI.getVarGroupUI().remove(varGroupUI2);
                    varGroupingUI.getVarGrouping().getVarGroups().remove(varGroup);
                    dataTableFilterGroup.setValue(varGroupingUI.getVarGroupUI());
                    setVarGroupUIList(varGroupingUI, true);
                }
            }

        }
        edited = true;
        
        dataTableFilterGrouping.getChildren().clear();
        
    }

    public void deleteMeasureGroup(){
        HtmlDataTable dataTable2 = dataTableVarGroup;
        if (dataTable2.getRowCount()>0) {
            VarGroupUI varGroupUI2 = (VarGroupUI) dataTable2.getRowData();
            
            VarGroup varGroup = varGroupUI2.getVarGroup();
            if(hasAssignedGroupTypes(varGroup)){
                return;
            }
            deleteVariablesFromGroup(varGroup);
            List varGroupList = (List) dataTable2.getValue();
            Iterator iterator = varGroupList.iterator();
            List deleteList = new ArrayList();
            while (iterator.hasNext() ){
                VarGroupUI varGroupUI = (VarGroupUI) iterator.next();
                VarGroup data = varGroupUI.getVarGroup();
                deleteList.add(data);
            }
            visualizationService.removeCollectionElement(deleteList,dataTable2.getRowIndex());
            measureGrouping.getVarGroupUI().remove(varGroupUI2);
            measureGrouping.getVarGrouping().getVarGroups().remove(varGroup);
            setVarGroupUIList(measureGrouping, true);
            edited = true;
        }
    }

    public void deleteSourceGroup(){
        HtmlDataTable dataTable2 = dataTableSourceVarGroup;
        if (dataTable2.getRowCount()>0) {
            VarGroupUI varGroupUI2 = (VarGroupUI) dataTable2.getRowData();

            VarGroup varGroup = varGroupUI2.getVarGroup();
            deleteVariablesFromGroup(varGroup);
            List varGroupList = (List) dataTable2.getValue();
            Iterator iterator = varGroupList.iterator();
            List deleteList = new ArrayList();
            while (iterator.hasNext() ){
                VarGroupUI varGroupUI = (VarGroupUI) iterator.next();
                VarGroup data = varGroupUI.getVarGroup();
                deleteList.add(data);
            }
            visualizationService.removeCollectionElement(deleteList,dataTable2.getRowIndex());
            sourceGrouping.getVarGroupUI().remove(varGroupUI2);
            sourceGrouping.getVarGrouping().getVarGroups().remove(varGroup);
            edited = true;
        }
    }


    public void deleteFilterGrouping(ActionEvent ae){

        List <VarGroupType> removeListGroupType = new ArrayList();
        List <VarGroup> removeListGroup= new ArrayList();
        List <VarGroupType> typeList;
        List <VarGroup> groupList;

            HtmlDataTable dataTable2 = dataTableFilterGrouping;

            VarGroupingUI varGroupingUI2 = (VarGroupingUI) dataTable2.getRowData();

        for (VarGroupType groupType: varGroupingUI2.getVarGrouping().getVarGroupTypes()){
            removeListGroupType.add(groupType);
        }
        
        typeList = new ArrayList(varGroupingUI2.getVarGrouping().getVarGroupTypes());

        for (VarGroup group: varGroupingUI2.getVarGrouping().getVarGroups()){
            removeListGroup.add(group);
        }

        groupList = new ArrayList(varGroupingUI2.getVarGrouping().getVarGroups());


            if (dataTable2.getRowCount()>0) {                
                filterGroupings.remove(varGroupingUI2);
                varGroupings.remove(varGroupingUI2.getVarGrouping());
            }

            for (VarGroupUI groupUI: varGroupingUI2.getVarGroupUI() ){
                for (VarGroupTypeUI varGroupTypeTest : groupUI.getVarGroupTypes()) {
                    varGroupTypeTest.setSelected(false);
                }
                saveGroupTypes(groupUI);
            }
            for (VarGroup group: removeListGroup ){
                visualizationService.removeCollectionElement(groupList,group);
            }

            for (VarGroupType groupType: removeListGroupType ){
                visualizationService.removeCollectionElement(typeList,groupType);
            }
            visualizationService.removeCollectionElement(varGroupings,varGroupingUI2.getVarGrouping());
            for(VarGroupingUI varGroupingUI: filterGroupings){
                dataTableFilterGroup.setValue(varGroupingUI.getVarGroupUI());
            }
            
            edited = true;
    }


    public String cancel(){
        if (edited){
            setShowInProgressPopup(true);
            return "";
        }
        visualizationService.cancel();
        getVDCRequestBean().setStudyId(study.getId());
        if ( studyVersion.getId() == null ) {
            getVDCRequestBean().setStudyVersionNumber(study.getReleasedVersion().getVersionNumber());
        } else {
            getVDCRequestBean().setStudyVersionNumber(studyVersion.getVersionNumber());
        }
        getVDCRequestBean().setSelectedTab("files");

        return "viewStudy";
    }
    
    boolean showInProgressPopup = false;
    
    public boolean isShowInProgressPopup() {
        return showInProgressPopup;
    }

    public void setShowInProgressPopup(boolean showInProgressPopup) {
        this.showInProgressPopup = showInProgressPopup;
    }
    
    public void togglePopup(javax.faces.event.ActionEvent event) {
         showInProgressPopup = !showInProgressPopup;
    }

    public String cancelAction() {
        visualizationService.cancel();
        getVDCRequestBean().setStudyId(study.getId());
        if ( studyVersion.getId() == null ) {
            getVDCRequestBean().setStudyVersionNumber(study.getReleasedVersion().getVersionNumber());
        } else {
            getVDCRequestBean().setStudyVersionNumber(studyVersion.getVersionNumber());
        }
        getVDCRequestBean().setSelectedTab("files");

        return "viewStudy";
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

     public boolean validateForRelease(boolean messages){
        boolean valid = true;
        List fullListOfErrors = new ArrayList();
        List returnListOfErrors = new ArrayList();
        List duplicateVariables = new ArrayList();
        String fullErrorMessage = "";

        if (!visualizationService.validateDisplayOptions(dataTable)) {
            if (messages){
                fullErrorMessage += "<br>Please select at least one view and the default view must be selected. <br>";
            }
            valid = false;
        }

        if (!xAxisSet  || !visualizationService.validateXAxisMapping(dataTable, xAxisVariableId)) {
            if (messages){
                fullErrorMessage += "<br>You must select one X-axis variable and it cannot be mapped to any Measure or Filter.<br>";
            }

            valid = false;
        }
        
        if (!visualizationService.validateAtLeastOneFilterMapping(dataTable, returnListOfErrors)) {
            
            if (messages){
                if (!returnListOfErrors.isEmpty()){
                    fullErrorMessage += "<br>Measure-filter combinations were found that would result in multiple variables selected at visualization.  ";
                    
                    boolean firstVar = true;
                    for(Object errorObject: returnListOfErrors){
                       if (errorObject instanceof VarGroup){
                           firstVar = true;
                            VarGroup vg = (VarGroup) errorObject;
                            fullErrorMessage += "<br>&#8226;&nbsp;&nbsp;Measure " + vg.getName() + " contains at least one variable with no associated filter. <br> ";
                       }


                        if (errorObject instanceof DataVariable ){
                            DataVariable dv = (DataVariable) errorObject;
                            if (firstVar) {
                                  fullErrorMessage += "&nbsp;&nbsp;&nbsp;&nbsp;Affected Variables: " + dv.getName();
                            } else{
                                fullErrorMessage += ", " + dv.getName();
                            }
                            firstVar = false;
                        }
                    }
                    fullErrorMessage += 
                            "<br>To correct this, create filters to limit each measure filter combination to "
                            + "a single variable or assign only one variable to each measure.<br>";
                }                
            }
                                  

            valid = false;
        }
        returnListOfErrors.clear();


        if (!visualizationService.validateAllGroupsAreMapped(dataTable, returnListOfErrors)) {
            if (messages){
                if (!returnListOfErrors.isEmpty()){
                    fullErrorMessage += "<br>Measure-filter combinations were found that would result in no variables selected at visualization.  <br>";
                    String filterErrorMessage = "";
                    int filterCount = 0;
                    String measureErrorMessage = "";
                    int measureCount = 0;
                    for(Object varGroupIn: returnListOfErrors){
                        VarGroup varGroup = (VarGroup) varGroupIn;
                        VarGrouping varGrouping = varGroup.getGroupAssociation();
                        if (varGrouping.getGroupingType().equals(GroupingType.FILTER)){
                            if (filterCount ==0){
                                filterErrorMessage+= varGroup.getName();
                            } else {
                                filterErrorMessage= filterErrorMessage+ ", " + varGroup.getName();
                            }
                            filterCount++;
                        }
                        if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                            if (measureCount ==0){
                                measureErrorMessage+= varGroup.getName();
                            } else {
                                measureErrorMessage= measureErrorMessage+ ", " + varGroup.getName();
                            }
                            measureCount++;
                        }
                    }
                    if (filterCount == 1){
                      fullErrorMessage = fullErrorMessage + "&#8226;&nbsp;&nbsp;Filter " + filterErrorMessage + " contains no variables.<br>";
                    }
                    if (filterCount > 1){
                      fullErrorMessage = fullErrorMessage + "&#8226;&nbsp;&nbsp;Filters " + filterErrorMessage + " contain no variables.<br>";
                    }
                    if (measureCount == 1){
                      fullErrorMessage = fullErrorMessage + "&#8226;&nbsp;&nbsp;Measure " + measureErrorMessage + " contains no variables.<br>";
                    }
                    if (measureCount > 1){
                      fullErrorMessage = fullErrorMessage + "&#8226;&nbsp;&nbsp;Measures " + measureErrorMessage + " contain no variables.<br>";
                    }
                    fullErrorMessage +=   "To correct this, assign at least one variable or remove the empty measures or filters. <br>" ;
                            
                }

            }
            valid = false;
        }
        returnListOfErrors.clear();

        if (!visualizationService.validateMoreThanZeroMeasureMapping(dataTable, returnListOfErrors)) {
            if (messages){
                if (!returnListOfErrors.isEmpty()){
                    fullErrorMessage += "<br>At least one filter was found that is not mapped to any measures  <br>";
                    boolean firstVar = true;
                    for(Object errorObject: returnListOfErrors){
                       if (errorObject instanceof VarGroup){
                           firstVar = true;
                            VarGroup vg = (VarGroup) errorObject;
                            fullErrorMessage += "<br>&#8226;&nbsp;&nbsp;Filter " + vg.getName() + " contains at least one variable that is not assigned to any measure. <br> ";
                       }
                        if (errorObject instanceof DataVariable ){
                            DataVariable dv = (DataVariable) errorObject;
                            if (firstVar) {
                                  fullErrorMessage += "&nbsp;&nbsp;&nbsp;&nbsp;Affected Variables: " + dv.getName();
                            } else{
                                fullErrorMessage += ", " + dv.getName();
                            }
                            firstVar = false;
                        }
                    }
                    fullErrorMessage +=
                            "<br>To correct this, remove unassigned variables or assign them to measures.<br>";
                }
            }

            valid = false;
        }

        returnListOfErrors.clear();

        if (!visualizationService.validateOneMeasureMapping(dataTable, returnListOfErrors)) {
            if (messages){
                if (!returnListOfErrors.isEmpty()){

                    fullErrorMessage += "<br>Variables were found that are mapped to multiple measures.  ";
                    boolean firstGroup = true;
                    for(Object dataVariableIn: returnListOfErrors){
                        if (dataVariableIn instanceof DataVariable ){
                            DataVariable dataVariable = (DataVariable) dataVariableIn;
                            fullErrorMessage += "<br>&#8226;&nbsp;&nbsp;Variable: " + dataVariable.getName() + "<br>";
                            firstGroup = true;

                        }
                        if (dataVariableIn instanceof VarGroup){
                            VarGroup varGroup = (VarGroup) dataVariableIn;
                            if (firstGroup){
                                fullErrorMessage += "&nbsp;&nbsp;&nbsp;&nbsp;Measures: " + varGroup.getName() ;
                            } else {
                                 fullErrorMessage += " , " + varGroup.getName() ;
                            }
                            firstGroup = false;
                        }
                       
                    }
                }
                fullErrorMessage +=
                            "<br>To correct this, map each variable to a single measure.<br>";
            }

            valid = false;
        }

        if (!visualizationService.validateAtLeastOneMeasureMapping(dataTable)) {
            if (messages){
                fullErrorMessage += "<br>Measure-filter combinations were found that would result in no variables selected at visualization.  <br>";
                fullErrorMessage +=" No Measures or filters configured.<br>";
                fullErrorMessage +=
                            "To correct this, configure at least one measure or one measure-filter combination.<br>";

            }
            valid = false;
        }
        returnListOfErrors.clear();
        duplicateVariables = visualizationService.getDuplicateMappings(dataTable, returnListOfErrors);
        if (duplicateVariables.size() > 0) {

             if (messages){
                    if (!returnListOfErrors.isEmpty()){
                    fullErrorMessage += "<br>Measure-filter combinations were found that would result in multiple variables selected at visualization.  ";
                    boolean firstVar = true;
                    for(Object errorObject: returnListOfErrors){
                       if (errorObject instanceof VarGroup){
                           firstVar = true;
                            VarGroup vg = (VarGroup) errorObject;
                            fullErrorMessage += "<br>&#8226;&nbsp;&nbsp;Measure " + vg.getName() + " contains multiple variables with insufficient filters to make them uniquely selectable.";
                       }


                        if (errorObject instanceof DataVariable ){
                            DataVariable dv = (DataVariable) errorObject;
                            if (firstVar) {
                                  fullErrorMessage += " <br>&nbsp;&nbsp;&nbsp;&nbsp;Affected Variables: " + dv.getName();
                            } else{
                                fullErrorMessage += ", " + dv.getName();
                            }
                            firstVar = false;
                        }
                    }
                    fullErrorMessage +=
                            "<br>To correct this, for measures where multiple variables are assigned, " +
                            "also assign each variable to a filter where the measure-filter combinations "
                            +"result in a single variable selected at visualization <br>";
                }  
            }
             returnListOfErrors.clear();
            valid=false;
        }

        if (valid && messages){
                FacesMessage message = new FacesMessage("The Data Visualization is valid for release.");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(releaseButton.getClientId(fc), message);
                JavascriptContext.addJavascriptCall(fc, "initRoundedCorners();" );
        }
        if (!valid&& messages) {
            // add rounded corners to the validation message box
            FacesContext fc = FacesContext.getCurrentInstance();
            fullErrorMessage = "This configuration is invalid so it cannot be released.  <br>" + fullErrorMessage;
            FacesMessage message = new FacesMessage(fullErrorMessage);
            fc.addMessage(validateButton.getClientId(fc), message);
            JavascriptContext.addJavascriptCall(fc, "initRoundedCorners();" );
        }

        return valid;
    }

    public String validate(){

        validateForRelease(true);
        return "";
    }

    public String release(){
        if (validateForRelease(true)) {
            dataTable.setVisualizationEnabled(true);
            saveAndContinue();
            return "";
        }
        return "";
    }

    public String unRelease(){
        dataTable.setVisualizationEnabled(false);
            saveAndContinue();
            return "";
    }


    public String saveAndExit(){
       if (dataTable.isVisualizationEnabled()){
           if (!validateForRelease(false)) {
               FacesMessage message = new FacesMessage("Your current changes are invalid.  Correct these issues or unrelease your visualization before saving.<br>Click Validate button to get a full list of validation issues.");
               FacesContext fc = FacesContext.getCurrentInstance();
               fc.addMessage(validateButton.getClientId(fc), message);
               return "";
           }
       }
       save();
       getVDCRequestBean().setStudyId(study.getId());
        if ( studyVersion.getId() == null ) {
            getVDCRequestBean().setStudyVersionNumber(study.getReleasedVersion().getVersionNumber());
        } else {
            getVDCRequestBean().setStudyVersionNumber(studyVersion.getVersionNumber());
        }
        getVDCRequestBean().setSelectedTab("files");
       return "viewStudy";
    }
    
    public String saveAndContinue(){
       if (dataTable.isVisualizationEnabled()){
           if (!validateForRelease(false)) {
               dataTable.setVisualizationEnabled(false);
               FacesMessage message = new FacesMessage("Your current changes are invalid. This visualization has been set to 'unreleased'. <br>Click Validate button to get a full list of validation issues.");
               FacesContext fc = FacesContext.getCurrentInstance();
               fc.addMessage(validateButton.getClientId(fc), message);               
           }
       }
       visualizationService.saveAndContinue();
       edited = false;
       return "";
    }

    private void resetDVMappingsByGroup(VarGroupUI varGroupUI){

        deleteVariablesFromGroup(varGroupUI.getVarGroup());
        List  dataVariableIds = varGroupUI.getDataVariablesSelected();
        if (!dataVariableIds.isEmpty()){
            for(Object dataVariableId:  dataVariableIds){
                String id = dataVariableId.toString();
                for(DataVariable dataVariable: dvList){
                    if (dataVariable.getId() !=null &&  dataVariable.getId().equals(new Long(id))){
                         DataVariableMapping dataVariableMapping = new DataVariableMapping();
                         dataVariableMapping.setDataTable(dataTable);
                         dataVariableMapping.setDataVariable(dataVariable);
                         dataVariableMapping.setGroup(varGroupUI.getVarGroup());
                         dataVariableMapping.setVarGrouping(varGroupUI.getVarGroup().getGroupAssociation());
                         dataVariableMapping.setX_axis(false);
                         dataVariable.getDataVariableMappings().add(dataVariableMapping);
                         for(VarGrouping varGrouping : varGroupings){
                             if (varGrouping.equals(varGroupUI.getVarGroup().getGroupAssociation())){
                                varGrouping.getDataVariableMappings().add(dataVariableMapping);
                             }
                         }
                     }
                }
            }
        }
    }

    private void addDVMappingsByGroup(VarGroupUI varGroupUI, List addList){
        if (!addList.isEmpty()){
            for(Object dataVariableId:  addList){
                String id = dataVariableId.toString();
                for(DataVariable dataVariable: dvList){
                    if (dataVariable.getId() !=null &&  dataVariable.getId().equals(new Long(id))){
                         DataVariableMapping dataVariableMapping = new DataVariableMapping();
                         dataVariableMapping.setDataTable(dataTable);
                         dataVariableMapping.setDataVariable(dataVariable);
                         dataVariableMapping.setGroup(varGroupUI.getVarGroup());
                         dataVariableMapping.setVarGrouping(varGroupUI.getVarGroup().getGroupAssociation());
                         dataVariableMapping.setX_axis(false);
                         dataVariable.getDataVariableMappings().add(dataVariableMapping);
                         for(VarGrouping varGrouping : varGroupings){
                             if (varGrouping.equals(varGroupUI.getVarGroup().getGroupAssociation())){
                                varGrouping.getDataVariableMappings().add(dataVariableMapping);
                             }
                         }
                     }
                }
            }
        }
    }

    private void deleteVariablesFromGroup(VarGroup varGroup, List <Long> longList){

        List <DataVariableMapping> removeList = new ArrayList();
        List <DataVariable> checkList = new ArrayList(dvList);
        List <DataVariable> dvRemoveList = new ArrayList();
        List <DataVariableMapping> groupingRemoveList = new ArrayList();

        for (DataVariable dataVariable: checkList){
            for (Long id: longList){
                if(id.equals(dataVariable.getId())){
                    dvRemoveList.add(dataVariable);
                }
            }
        }

        for (DataVariable dataVariable: dvRemoveList){
            List <DataVariableMapping> deleteList = (List <DataVariableMapping>) dataVariable.getDataVariableMappings();
            for (DataVariableMapping dataVariableMapping : deleteList ){
                if (dataVariableMapping.getGroup() != null && !dataVariableMapping.isX_axis()
                        && dataVariableMapping.getGroup().equals(varGroup) ) {
                          removeList.add(dataVariableMapping);
                          groupingRemoveList.add(dataVariableMapping);
                }
            }
        }

        for(DataVariableMapping dataVarMappingRemove : removeList){
            visualizationService.removeCollectionElement(dataVarMappingRemove.getDataVariable().getDataVariableMappings(),dataVarMappingRemove);
        }

        for(DataVariableMapping dataVarMappingRemove : groupingRemoveList){
            visualizationService.removeCollectionElement(dataVarMappingRemove.getVarGrouping().getDataVariableMappings(),dataVarMappingRemove);
        }


    }

    private void deleteVariablesFromGroup(VarGroup varGroup){

        List <DataVariableMapping> removeList = new ArrayList();
        List <DataVariable> tempList = new ArrayList(dvList);
        List <DataVariableMapping> groupingRemoveList = new ArrayList();
        for (DataVariable dataVariable: tempList){
            List <DataVariableMapping> deleteList = (List <DataVariableMapping>) dataVariable.getDataVariableMappings();

            for (DataVariableMapping dataVariableMapping : deleteList ){
                 
                if (dataVariableMapping.getGroup() != null && !dataVariableMapping.isX_axis()
                        && dataVariableMapping.getGroup().getName().equals(varGroup.getName())
                        && dataVariableMapping.getVarGrouping().getGroupingType().equals(varGroup.getGroupAssociation().getGroupingType()))

                    removeList.add(dataVariableMapping);
            }
            for (DataVariableMapping dataVariableMapping : deleteList ){
                if (dataVariableMapping.getGroup() != null && !dataVariableMapping.isX_axis()
                        && dataVariableMapping.getGroup().getName().equals(varGroup.getName())
                        && dataVariableMapping.getVarGrouping().getGroupingType().equals(varGroup.getGroupAssociation().getGroupingType()))
                groupingRemoveList.add(dataVariableMapping);
            }
        }

        for(DataVariableMapping dataVarMappingRemove : removeList){
            visualizationService.removeCollectionElement(dataVarMappingRemove.getDataVariable().getDataVariableMappings(),dataVarMappingRemove);
        }

        for(DataVariableMapping dataVarMappingRemove : groupingRemoveList){
            visualizationService.removeCollectionElement(dataVarMappingRemove.getVarGrouping().getDataVariableMappings(),dataVarMappingRemove);
        }

    }
    


    private void resetDVMappingsXAxis(){

        List <DataVariableMapping> removeList = new ArrayList();
        List <DataVariable> tempList = new ArrayList(dvList);
        for(DataVariable dataVariable: tempList){
            List <DataVariableMapping> deleteList = (List <DataVariableMapping>) dataVariable.getDataVariableMappings();
            for (DataVariableMapping dataVariableMapping : deleteList ){
                if (dataVariableMapping.getGroup() == null) removeList.add(dataVariableMapping);
            }
        }

        for(DataVariableMapping dataVarMappingRemove : removeList){
            if (dataVarMappingRemove.getDataVariable() != null   ){
                visualizationService.removeCollectionElement(dataVarMappingRemove.getDataVariable().getDataVariableMappings(),dataVarMappingRemove);
            }
            
        }

        if (xAxisVariableId != null && !xAxisVariableId.equals(new Long(0))){
           for(DataVariable dataVariable: dvList){
               if (dataVariable.getId() !=null &&  dataVariable.getId().equals(xAxisVariableId)){
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

   public void updateGenericGroupVariableList (String checkString){


        for(DataVariableUI dataVariableUI:dvGenericListUI){
            for(DataVariableUI dvTest: dvGenericFilteredListUI){
                if (dvTest.getDataVariable().equals(dataVariableUI.getDataVariable())){
                    dataVariableUI.setSelected(dvTest.isSelected());
                }
            }

        }

        dvGenericFilteredListUI.clear();
        for(DataVariableUI dataVariableUI:dvGenericListUI){
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
        
     List <DataVariableUI> returnList = new ArrayList();
               Iterator iterator = dvGenericListUI.iterator();


        iterator = dvGenericListUI.iterator();
        while (iterator.hasNext() ){
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
                if (dataVariableUI.isDisplay()){
                    DataVariableUI dvAdd = new DataVariableUI();
                    dvAdd.setDataVariable(dataVariableUI.getDataVariable());
                    dvAdd.setSelected(dataVariableUI.isSelected());
                    dvAdd.setDisplay(true);
                    returnList.add(dvAdd);
                }
        }
        return dvGenericFilteredListUI;
        //return returnList;
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

    public boolean isDisplayValidationFailure() {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getMessages(validateButton.getClientId(fc)).hasNext();
    }

    public boolean isDisplayValidationSuccess() {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getMessages(releaseButton.getClientId(fc)).hasNext();
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
}
