/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.component.ext.HtmlCommandButton;
import edu.harvard.iq.dvn.core.study.DataTable;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.panelseries.UISeries;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.EditStudyFilesService;
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
import edu.harvard.iq.dvn.core.visualization.VisualizationServiceLocal;
import edu.harvard.iq.dvn.core.web.DataVariableUI;
import edu.harvard.iq.dvn.core.web.VarGroupTypeUI;
import edu.harvard.iq.dvn.core.web.VarGroupUI;
import edu.harvard.iq.dvn.core.web.VarGroupingUI;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
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
    private List <VarGroupType> measureGroupTypes = new ArrayList();
    private VarGroupingUI measureGrouping = new VarGroupingUI();
    private List <VarGroupingUI> filterGroupings = new ArrayList();

    private List <SelectItem> studyFileIdSelectItems = new ArrayList();


    private DataVariable xAxisVariable = new DataVariable();
    private Long xAxisVariableId =  new Long(0);
    private StudyVersion studyVersion;

    private Study study;
    private Long studyFileId = new Long(0);
    private Long studyId ;
    private Metadata metadata;
    private String currentTitle;
    private String xAxisUnits = "";
    DataTable dataTable;
    private boolean showCommands = false;
    private boolean showMeasureVariables = false;
    private boolean editXAxis = false;
    private boolean editMeasure = false;
    private boolean editFilterGroup = false;
    private boolean editMeasureGrouping = false;
    private boolean addMeasureType = false;
    private boolean addFilterType = false;
    private boolean addMeasureGroup = false;
    private boolean addFilterGroup = false;
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


    private VarGroupUI editFragmentVarGroup = new VarGroupUI();
    private VarGroupUI editFilterVarGroup = new VarGroupUI();
    private VarGroupUI editMeasureVarGroup = new VarGroupUI();
    private VarGroupingUI editFilterVarGrouping = new VarGroupingUI();

    private boolean showFilterVariables = false;
    private boolean selectFile = true;

    public SetUpDataExplorationPage(){
        
    }

    public void init() {
        super.init();

         studyId = new Long( getVDCRequestBean().getRequestParam("studyId"));


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
            currentTitle = metadata.getTitle();
            setFiles(editStudyFilesService.getCurrentFiles());
        }
        else {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The Study ID is null", null);
            context.addMessage(null, errMessage);
            //Should not get here.
            //Must always be in a study to get to this page.
        }
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

         measureGroupTypes = loadSelectMeasureGroupTypes();
         loadMeasureGroupUIs();
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
             addMeasureGrouping();
         } else {
             if (!measureGrouping.getVarGroupUI().isEmpty()){
                 hasMeasureGroups = true;
             }
         }

         loadFilterGroupings();
         if (!filterGroupings.isEmpty()){
             hasFilterGroupings = true;
         }
         
        

    }

    public List<VarGrouping> getVarGroupings() {
        return varGroupings;
    }

    public void setVarGroupings(List<VarGrouping> varGroupings) {
        this.varGroupings = varGroupings;        
    }
    

    public List<VarGroupType> loadSelectMeasureGroupTypes() {

        for (VarGrouping varGrouping: varGroupings ){
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                measureGrouping.setVarGrouping(varGrouping);
                measureGrouping.setVarGroupTypesUI(new ArrayList());
                for (VarGroupType varGroupType: varGrouping.getVarGroupTypes()){
                    VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
                    varGroupTypeUI.setVarGroupType(varGroupType);
                    varGroupTypeUI.setEditMode(false);
                    measureGrouping.getVarGroupTypesUI().add(varGroupTypeUI);
                }
                return (List<VarGroupType>) varGrouping.getVarGroupTypes();
            }

        }
        
        return null;
    }


    private void loadFilterGroupings() {
        filterGroupings.clear();

        for (VarGrouping varGrouping: varGroupings ){
            
            if (varGrouping.getGroupingType().equals(GroupingType.FILTER)){
                VarGroupingUI varGroupingUI = new VarGroupingUI();
                varGroupingUI.setVarGrouping(varGrouping);
                varGroupingUI.setVarGroupTypesUI(varGroupingUI);
                setVarGroupUI(varGroupingUI);
                filterGroupings.add(varGroupingUI);
            }
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
            if ((isDate &&  "date".equals(dataVariable.getFormatCategory())) ||
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

    public void loadMeasureGroupUIs (){
        List <VarGroupUI> returnList = new ArrayList();
        for (VarGrouping varGrouping: varGroupings ){
           
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                List <VarGroup> localMeasureGroups = (List<VarGroup>) varGrouping.getVarGroups();
                for(VarGroup varGroup: localMeasureGroups) {

                   VarGroupUI varGroupUI = new VarGroupUI();
                   varGroupUI.setVarGroup(varGroup);
                   varGroupUI.setVarGroupTypes(new ArrayList());
                   varGroupUI.setVarGroupTypesSelectItems(new ArrayList());
                   varGroupUI.setDataVariablesSelected(new ArrayList());
                   List <VarGroupType> varGroupTypes = new ArrayList();
                   varGroupTypes = (List<VarGroupType>) varGroupUI.getVarGroup().getGroupTypes();
                    if (varGroupTypes !=null ) {
                       String groupTypesSelectedString = "";
                       for(VarGroupType varGroupType: varGroupTypes){
                           VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
                           varGroupTypeUI.setVarGroupType(varGroupType);
                           varGroupTypeUI.setEnabled(true);
                           varGroupTypeUI.setSelected(true);
                           varGroupTypeUI.getVarGroupType().getName();
                           varGroupType.getName();
                           varGroupUI.getVarGroupTypes().add(varGroupTypeUI);
                           varGroupUI.getVarGroupTypesSelectItems().add(new Long(varGroupType.getId()));
                           if (groupTypesSelectedString.isEmpty()){
                               groupTypesSelectedString+=varGroupTypeUI.getVarGroupType().getName();
                           } else {
                              groupTypesSelectedString = groupTypesSelectedString + ", " + varGroupTypeUI.getVarGroupType().getName();
                           }

                       }
                       varGroupUI.setGroupTypesSelectedString(groupTypesSelectedString);
                    }

                    List <DataVariable> dataVariables = new ArrayList();
                    dataVariables = (List<DataVariable>) visualizationService.getDataVariableMappingsFromGroupId(varGroupUI.getVarGroup().getId());
                    int selectedVariables = 0;
                    if (dataVariables !=null ) {
                       for(DataVariable dataVariable: dataVariables){
                           varGroupUI.getDataVariablesSelected().add(new Long(dataVariable.getId()));
                           selectedVariables++;
                       }
                    }
                    varGroupUI.setNumberOfVariablesSelected(new Long(selectedVariables));
                    returnList.add(varGroupUI);
                }
                measureGrouping.setVarGroupUI(returnList);

            }
        }

    }

    public List<VarGroupUI> getMeasureGroups() {
        return (List) measureGrouping.getVarGroupUI();
    }

    public VarGroupingUI getMeasureGrouping() {
        return measureGrouping;
    }





    public void deleteFilterGroupType(ActionEvent ae){

        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
         HtmlDataTable dataTable2 = (HtmlDataTable) uiComponent;
        if (dataTable2.getRowCount()>0) {
            VarGroupTypeUI varGroupTypeUI2 = (VarGroupTypeUI) dataTable2.getRowData();
            VarGroupType varGroupType = varGroupTypeUI2.getVarGroupType();
            List varGroupTypeList = (List) dataTable2.getValue();
            Iterator iterator = varGroupTypeList.iterator();
            List deleteList = new ArrayList();
            while (iterator.hasNext() ){
                VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) iterator.next();
                VarGroupType data = varGroupTypeUI.getVarGroupType();
                deleteList.add(data);
            }
            visualizationService.removeCollectionElement(deleteList,dataTable2.getRowIndex());
             for(VarGroupingUI varGroupingUI: filterGroupings){
             if (varGroupingUI.getVarGrouping().equals(varGroupType.getVarGrouping())){
                varGroupingUI.getVarGroupTypesUI().remove(varGroupTypeUI2);
                varGroupingUI.getVarGrouping().getVarGroupTypes().remove(varGroupType);
             }
           }
             if (editFilterVarGroup.getVarGroup() != null){
                 removeTypeFromGroupUI(editFilterVarGroup, varGroupTypeUI2 );
             }

        }
    }

    public void deleteMeasureGroupType(ActionEvent ae){
        HtmlDataTable dataTable2 = dataTableMeasureGroupType;
                UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        dataTable2 = (HtmlDataTable) uiComponent;
        if (dataTable2.getRowCount()>0) {
            VarGroupTypeUI varGroupTypeUI2 = (VarGroupTypeUI) dataTable2.getRowData();
            VarGroupType varGroupType = varGroupTypeUI2.getVarGroupType();
            List varGroupTypeList = (List) dataTable2.getValue();
            Iterator iterator = varGroupTypeList.iterator();
            List deleteList = new ArrayList();
            while (iterator.hasNext() ){
                VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) iterator.next();
                VarGroupType data = varGroupTypeUI.getVarGroupType();
                deleteList.add(data);
            }
            visualizationService.removeCollectionElement(deleteList,dataTable2.getRowIndex());
                measureGrouping.getVarGroupTypesUI().remove(varGroupTypeUI2);
                measureGrouping.getVarGrouping().getVarGroupTypes().remove(varGroupType);
             if (editMeasureVarGroup.getVarGroup() != null){
                 removeTypeFromGroupUI(editMeasureVarGroup, varGroupTypeUI2 );
             }
        }
    }



    public void editMeasureGroup (ActionEvent ae){
        
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

                List <VarGroupTypeUI> allVarGroupTypes = (List<VarGroupTypeUI>) measureGrouping.getVarGroupTypesUI();
                for (VarGroupTypeUI varGroupTypeUI : allVarGroupTypes) {
                    varGroupTypeUI.setSelected(false);
                     List <VarGroupType> varGroupTypes = new ArrayList();
                        varGroupTypes = (List<VarGroupType>) varGroupUI.getVarGroup().getGroupTypes();
                        if (varGroupTypes !=null ) {
                            for(VarGroupType varGroupType: varGroupTypes){
                                if (varGroupType.getId().equals(varGroupTypeUI.getVarGroupType().getId())){
                                    varGroupTypeUI.setSelected(true);
                                }

                            }

                        }
                    varGroupUI.getVarGroupTypes().add(varGroupTypeUI);
                }

        editMeasureVarGroup = varGroupUI;

        cancelAddEdit();
        getInputMeasureName().setValue(editMeasureVarGroup.getVarGroup().getName());
        getInputMeasureUnits().setValue(editMeasureVarGroup.getVarGroup().getUnits());
        editMeasure = true;
    }

    public void editMeasureTypeAction(ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) tempTable.getRowData();

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

     public void saveFilterType(ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) tempTable.getRowData();

        if (tempTable.equals(dataTableManageFilterGroupType)) {
            varGroupTypeUI.getVarGroupType().setName((String) getEditManageFilterGroupTypeName().getValue());
        } else {
            varGroupTypeUI.getVarGroupType().setName((String) getEditFilterGroupTypeName().getValue());
        }

        
        loadFilterGroupings();

        varGroupTypeUI.setEditMode(false);

     }

    public void saveMeasureType(ActionEvent ae){
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupTypeUI varGroupTypeUI = (VarGroupTypeUI) tempTable.getRowData();

        if (tempTable.equals(dataTableManageMeasureGroupType)) {
            varGroupTypeUI.getVarGroupType().setName((String) getEditManageMeasureGroupTypeName().getValue());
        } else {
            varGroupTypeUI.getVarGroupType().setName((String) getEditMeasureGroupTypeName().getValue());
        }

        
        varGroupTypeUI.setEditMode(false);

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
            if (varGroupingUI.getVarGrouping().equals(varGroupUI.getVarGroup().getGroupAssociation())){
                
                List <VarGroupTypeUI> allVarGroupTypes = (List<VarGroupTypeUI>) varGroupingUI.getVarGroupTypesUI();
                for (VarGroupTypeUI varGroupTypeUI : allVarGroupTypes) {
                    varGroupTypeUI.setSelected(false);
                     List <VarGroupType> varGroupTypes = new ArrayList();
                        varGroupTypes = (List<VarGroupType>) varGroupUI.getVarGroup().getGroupTypes();
                        if (varGroupTypes !=null ) {
                            for(VarGroupType varGroupType: varGroupTypes){
                                if (varGroupType.getId().equals(varGroupTypeUI.getVarGroupType().getId())){
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
    }

    public void saveMeasureFragment(){

        String chkGroupName = (String) getInputMeasureName().getValue();

        if (chkGroupName.isEmpty() || chkGroupName.trim().equals("") ) {
            FacesMessage message = new FacesMessage("Please Enter a Measure Name");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
            return;
        }

        editMeasureVarGroup.getVarGroup().setName((String) getInputMeasureName().getValue());
        editMeasureVarGroup.getVarGroup().setUnits((String) getInputMeasureUnits().getValue());


        
        if(addMeasureGroup){
            addMeasureGroupSave();
        }
        updateVariableByGroup(editMeasureVarGroup);
        resetDVMappingsByGroup(editMeasureVarGroup);
        editMeasureVarGroup = null;
        cancelAddEdit();
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
    }

    public void cancelMeasureGrouping(){
        cancelAddEdit();
    }

    public void saveFilterFragment(ActionEvent ae){

        String chkGroupName = (String) getInputFilterGroupName().getValue();

        if (chkGroupName.isEmpty()) {
            FacesMessage message = new FacesMessage("Please Enter a Filter Name");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
            return;
        }


        editFilterVarGroup.getVarGroup().setName((String) getInputFilterGroupName().getValue());


        if(addFilterGroup){
            addFilterGroupSave();
        }
        updateVariableByGroup(editFilterVarGroup);
        resetDVMappingsByGroup(editFilterVarGroup);
        editFilterVarGroup = null;
        getInputFilterGroupName().setValue("");
        cancelAddEdit();
    }

    private void updateVariableByGroup(VarGroupUI varGroupUIin){

        if (varGroupUIin.getDataVariablesSelected() != null){
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
                varGroupUIin.getDataVariablesSelected().add(dataVariableUI.getDataVariable().getId());
                countSelected++;
            }
        }

        if (varGroupUIin.getVarGroupTypesSelectItems() != null) {
            varGroupUIin.getVarGroupTypesSelectItems().clear();
        }  else {
           varGroupUIin.setVarGroupTypesSelectItems(new ArrayList());
        }

        for (VarGroupTypeUI varGroupTypeUI: varGroupUIin.getVarGroupTypes()){
            if (varGroupTypeUI.isSelected()){
                varGroupUIin.getVarGroupTypesSelectItems().add(varGroupTypeUI.getVarGroupType().getId());
            }
        }

        varGroupUIin.setNumberOfVariablesSelected(new Long(countSelected));
             VarGroup varGroup = varGroupUIin.getVarGroup();
             List  groupTypeIds = varGroupUIin.getVarGroupTypesSelectItems();
             varGroup.getGroupTypes().clear();
             String groupTypesSelectedString = "";
             VarGrouping varGrouping = varGroup.getGroupAssociation();

             for(Object stringUI: groupTypeIds){
                 String id = stringUI.toString();
                 for (VarGroupType varGroupType: varGrouping.getVarGroupTypes()){

                     if (varGroupType.getId() !=null &&  varGroupType.getId().equals(new Long(id))){
                         varGroup.getGroupTypes().add(varGroupType);
                           if (groupTypesSelectedString.isEmpty()){
                               groupTypesSelectedString+=varGroupType.getName();
                           } else {
                              groupTypesSelectedString = groupTypesSelectedString + ", " + varGroupType.getName();
                           }
                     }
                 }
             }

        varGroupUIin.setGroupTypesSelectedString(groupTypesSelectedString);
    }

    public void saveFilterGrouping(){
        String testString = (String) getInputFilterGroupingName().getValue();
        if (!testString.isEmpty()){
            if (addFilterGrouping) {
                addFilterGroupingSave();
                cancelAddEdit();
            } else {
                if (!getInputFilterGroupingName().getValue().equals("")) {
                editFilterVarGrouping.getVarGrouping().setName((String) getInputFilterGroupingName().getValue());
                }
                cancelAddEdit();
            }

        }  else {
            FacesMessage message = new FacesMessage("Please Enter a Filter Group Name");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
            return;
        }


    }



    public void addMeasureGroup() {

        VarGroupUI newElem = new VarGroupUI();
        newElem.setVarGroup(new VarGroup());
        newElem.getVarGroup().setGroupAssociation(measureGrouping.getVarGrouping());
        newElem.getVarGroup().setGroupTypes(new ArrayList());
                newElem.setVarGroupTypes(new ArrayList());

                List <VarGroupTypeUI> allVarGroupTypes = (List<VarGroupTypeUI>) measureGrouping.getVarGroupTypesUI();
                for (VarGroupTypeUI varGroupTypeUI : allVarGroupTypes) {
                    varGroupTypeUI.setSelected(false);
                    newElem.getVarGroupTypes().add(varGroupTypeUI);
                }
        dvGenericListUI = dvNumericListUI;

        loadEmptyVariableList();

        editMeasureVarGroup = newElem;
        cancelAddEdit();
        editMeasure = true;
        addMeasureGroup = true;

        
    }

    private void addMeasureGroupSave() {

        int i = measureGrouping.getVarGrouping().getVarGroups().size();
        measureGrouping.getVarGrouping().getVarGroups().add(i,  editMeasureVarGroup.getVarGroup());
        measureGrouping.getVarGroupUI().add(editMeasureVarGroup);
        visualizationService.addGroup();
        cancelAddEdit();
        editMeasure = false;
        addMeasureGroup = false;

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

         visualizationService.addGroup();
         for(VarGroupingUI varGroupingUI: filterGroupings){
             if (varGroupingUI.getVarGrouping().getId().equals(editFilterVarGroup.getVarGroup().getGroupAssociation().getId())){
                varGroupingUI.getVarGroupUI().add(editFilterVarGroup);
                varGroupingUI.getVarGrouping().getVarGroups().add( editFilterVarGroup.getVarGroup());
             }
         }
         visualizationService.addGroup();
        cancelAddEdit();

    }

    public void addFilterGroupAction(ActionEvent ae) {
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        VarGroupingUI varGroupingUI = (VarGroupingUI) dataTableFilterGrouping.getRowData();
        VarGroupUI newElem = new VarGroupUI();
        newElem.setVarGroup(new VarGroup());
        newElem.getVarGroup().setGroupTypes(new ArrayList());
        newElem.getVarGroup().setGroupAssociation(varGroupingUI.getVarGrouping());
        loadEmptyVariableList();

        newElem.setVarGroupTypes(new ArrayList());

        List <VarGroupTypeUI> allVarGroupTypes = (List<VarGroupTypeUI>) varGroupingUI.getVarGroupTypesUI();
        for (VarGroupTypeUI varGroupTypeUI : allVarGroupTypes) {
                varGroupTypeUI.setSelected(false);
                newElem.getVarGroupTypes().add(varGroupTypeUI);
        }
        
        editFilterVarGroup = newElem;
        dvGenericListUI = dvNumericListUI;
        loadEmptyVariableList();
        cancelAddEdit();
        editFilterGroup = true;
        addFilterGroup = true;
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
        String addName = newElem.getName();

        measureGrouping.getVarGrouping().getVarGroupTypes().add(newElem);
        VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
        varGroupTypeUI.setVarGroupType(newElem);
        if (measureGrouping.getVarGroupTypesUI() == null){
            measureGrouping.setVarGroupTypesUI(new ArrayList());
        }
        measureGrouping.getVarGroupTypesUI().add(varGroupTypeUI);
        visualizationService.addGroupType();
        Long newID =  newElem.getId();
        if (groupEdit){
            addNewTypeToGroupUI(editMeasureVarGroup, varGroupTypeUI);
        }
        addMeasureType = false;
    }

    public void saveFilterTypeButton(){
        Long varGroupingId = new Long(0);
        boolean groupEdit = false;
        VarGroupType newElem = new VarGroupType();
        if (editFilterVarGroup != null && editFilterVarGroup.getVarGroup() != null ) {
            newElem.setVarGrouping(editFilterVarGroup.getVarGroup().getGroupAssociation());
            varGroupingId = editFilterVarGroup.getVarGroup().getGroupAssociation().getId();
            groupEdit = true;
        } else {
            newElem.setVarGrouping(editFilterVarGrouping.getVarGrouping());
            varGroupingId = editFilterVarGrouping.getVarGrouping().getId();
        }

        newElem.setName((String)getInputFilterGroupTypeName().getValue());

        for(VarGrouping varGrouping: varGroupings){
             if (varGrouping.getId() == varGroupingId){
                newElem.setVarGrouping(varGrouping);
                int i = varGrouping.getVarGroupTypes().size();
                varGrouping.getVarGroupTypes().add(newElem);
             }
         }

        VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
        varGroupTypeUI.setVarGroupType(newElem);
        for(VarGroupingUI varGroupingUI: filterGroupings){
             if (varGroupingUI.getVarGrouping().getId() == varGroupingId){
                varGroupingUI.getVarGroupTypesUI().add(varGroupTypeUI);
                varGroupingUI.setVarGroupTypesUI(varGroupingUI);
                visualizationService.addGroupType();
                Long newID =  newElem.getId();
                
             }
         }
        if (groupEdit){           
            addNewTypeToGroupUI(editFilterVarGroup, varGroupTypeUI);
        }

        loadFilterGroupings();
                addFilterType = false;
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
        editMeasureGrouping = true;
    }
    public void cancelFilterTypeButton(){
        addFilterType = false;
        manageFilterTypes = true;
        editFilterGrouping = true;
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



    public void addFilterGroupType() {
        VarGroupType newElem = new VarGroupType();

        Long varGroupingId = (Long) getAddFilterGroupLink().getValue();
        for(VarGrouping varGrouping: varGroupings){
             if (varGrouping.getId() == varGroupingId){
                newElem.setVarGrouping(varGrouping);
                int i = varGrouping.getVarGroupTypes().size();
                varGrouping.getVarGroupTypes().add(newElem);
            }
         }
         for(VarGroupingUI varGroupingUI: filterGroupings){
             if (varGroupingUI.getVarGrouping().getId() == varGroupingId){
                 VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
                 varGroupTypeUI.setVarGroupType(newElem);
                 varGroupTypeUI.setEditMode(true);
                varGroupingUI.getVarGroupTypesUI().add(varGroupTypeUI);
                varGroupingUI.setVarGroupTypesUI(varGroupingUI);
             }
         }
    }

    private void addMeasureGrouping() {
        VarGrouping varGrouping = new VarGrouping();
        varGrouping.setGroupingType(GroupingType.MEASURE);
        varGrouping.setDataTable(dataTable);
        varGrouping.setDataVariableMappings(new ArrayList());
        varGrouping.setGroups(new ArrayList());
        varGrouping.setVarGroupTypes(new ArrayList());

        measureGrouping.setVarGrouping(varGrouping);
        measureGrouping.setSelectedGroupId(new Long(0));
        measureGrouping.setVarGroupTypesUI(new ArrayList());
        measureGrouping.setVarGroupUI(new ArrayList());
        measureGroupTypes = loadSelectMeasureGroupTypes();

        dataTable.getVarGroupings().add(varGrouping);

        measureGrouping.setVarGrouping(varGrouping);


    }

    public void addFilterGroupingAction() {
        cancelAddEdit();

        addFilterGrouping = true;
        editFilterGrouping = true;
    }

    private void addFilterGroupingSave(){
        VarGrouping varGrouping = new VarGrouping();

        varGrouping.setGroupingType(GroupingType.FILTER);
        varGrouping.setDataTable(dataTable);
        varGrouping.setDataVariableMappings(new ArrayList());
        varGrouping.setGroups(new ArrayList());
        varGrouping.setVarGroupTypes(new ArrayList());

        dataTable.getVarGroupings().add(varGrouping);
        visualizationService.addGrouping();
        Long newID =  varGrouping.getId();

        loadFilterGroupings();
        VarGroupingUI varGroupingUI = new VarGroupingUI();
        varGroupingUI.setVarGrouping(varGrouping);
        editFilterVarGrouping = varGroupingUI;
        editFilterVarGrouping.getVarGrouping().setName((String) getInputFilterGroupingName().getValue());
    }

    public void deleteFilterGroup(ActionEvent ae){


        HtmlDataTable dataTable2 = dataTableFilterGroup;
        if (dataTable2.getRowCount()>0) {
            VarGroupUI varGroupUI2 = (VarGroupUI) dataTable2.getRowData();

            VarGroup varGroup = varGroupUI2.getVarGroup();
            deleteVariablesFromGroup(varGroupUI2);
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
                if (varGroupingUI.getVarGrouping().equals(varGroup.getGroupAssociation())){
                    varGroupingUI.getVarGroupUI().remove(varGroupUI2);
                    varGroupingUI.getVarGrouping().getVarGroups().remove(varGroup);
                    setVarGroupUI(varGroupingUI);

                    dataTableFilterGroup.setValue(varGroupingUI.getVarGroupUI());
                }
            }

        }

        loadFilterGroupings();
        forceRender();
    }

    public void deleteMeasureGroup(){
        HtmlDataTable dataTable2 = dataTableVarGroup;
        if (dataTable2.getRowCount()>0) {
            VarGroupUI varGroupUI2 = (VarGroupUI) dataTable2.getRowData();
            deleteVariablesFromGroup(varGroupUI2);
            VarGroup varGroup = varGroupUI2.getVarGroup();
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
        }
    }

    public void deleteFilterGrouping(ActionEvent ae){

            HtmlDataTable dataTable2 = dataTableFilterGrouping;
            VarGroupingUI varGroupingUI2 = (VarGroupingUI) dataTable2.getRowData();
            if (dataTable2.getRowCount()>0) {                
                filterGroupings.remove(varGroupingUI2);
                varGroupings.remove(varGroupingUI2.getVarGrouping());
            }

            visualizationService.removeCollectionElement(varGroupings,varGroupingUI2.getVarGrouping());
            loadFilterGroupings();
            for(VarGroupingUI varGroupingUI: filterGroupings){

                    dataTableFilterGroup.setValue(varGroupingUI.getVarGroupUI());
            }
    }


    public String cancel(){
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
        editFilterGroup = false;
        editMeasureGrouping = false;
        addMeasureType = false;
        addFilterType = false;
        addMeasureGroup = false;
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

        if (!visualizationService.validateAtLeastOneFilterMapping(dataTable)) {
            if (messages){
                FacesMessage message = new FacesMessage("Each variable mapping must include at least one Filter.");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(validateButton.getClientId(fc), message);
            }
            valid = false;
        }

        if (!xAxisSet) {
            if (messages){
                FacesMessage message = new FacesMessage("You must select one X-axis variable and it cannot be mapped to any Measure or Filter.");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(validateButton.getClientId(fc), message);
            }

            valid = false;
        }

        if (!visualizationService.validateXAxisMapping(dataTable, xAxisVariableId)) {
            if (messages){
                FacesMessage message = new FacesMessage("You must select one X-axis variable and it cannot be mapped to any Measure or Filter.");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(validateButton.getClientId(fc), message);
            }

            valid = false;
        }

        if (!visualizationService.validateOneMeasureMapping(dataTable)) {
            if (messages){
                FacesMessage message = new FacesMessage("Each variable mapping must include one Measure.");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(validateButton.getClientId(fc), message);
            }

            valid = false;
        }

        if (!visualizationService.validateAtLeastOneMeasureMapping(dataTable)) {
            if (messages){
                FacesMessage message = new FacesMessage("The Data Visualization must include at least one Measure.");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(validateButton.getClientId(fc), message);
            }
            valid = false;
        }

        if (valid && messages){
                FacesMessage message = new FacesMessage("The Data Visualization is valid for release.");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(validateButton.getClientId(fc), message);
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
            saveAndExit();
            return "viewStudy";
        }
        return "";
    }

    public String unRelease(){
        dataTable.setVisualizationEnabled(false);
        return "";
    }


    public String saveAndExit(){
       if (dataTable.isVisualizationEnabled()){
           if (!validateForRelease(false)) {
               FacesMessage message = new FacesMessage("Changes not saved because exploration is no longer valid for release.");
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

    private void resetDVMappingsByGroup(VarGroupUI varGroupUI){

        deleteVariablesFromGroup(varGroupUI);

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
                     }

                  }
                }

             }



    }

    private void deleteVariablesFromGroup(VarGroupUI varGroupUI){

                List <DataVariableMapping> removeList = new ArrayList();
                List <DataVariable> tempList = new ArrayList(dvList);
           for(DataVariable dataVariable: tempList){
               List <DataVariableMapping> deleteList = (List <DataVariableMapping>) dataVariable.getDataVariableMappings();
                for (DataVariableMapping dataVariableMapping : deleteList ){

                    if (dataVariableMapping.getGroup() != null && dataVariableMapping.getGroup().equals(varGroupUI.getVarGroup()))
                    removeList.add(dataVariableMapping);
                     }
             }

           for(DataVariableMapping dataVarMappingRemove : removeList){

               visualizationService.removeCollectionElement(dataVarMappingRemove.getDataVariable().getDataVariableMappings(),dataVarMappingRemove);
           }

    }

    private void resetDVMappingsXAxis(){

        List <DataVariableMapping> removeList = new ArrayList();
        List <DataVariable> tempList = new ArrayList(dvList);
           for(DataVariable dataVariable: tempList){
               List <DataVariableMapping> deleteList = (List <DataVariableMapping>) dataVariable.getDataVariableMappings();
                for (DataVariableMapping dataVariableMapping : deleteList ){
                    if (dataVariableMapping.getGroup() == null )
                    removeList.add(dataVariableMapping);
                     }
             }

           for(DataVariableMapping dataVarMappingRemove : removeList){

               visualizationService.removeCollectionElement(dataVarMappingRemove.getDataVariable().getDataVariableMappings(),dataVarMappingRemove);
           }

           if(xAxisVariableId != null && !xAxisVariableId.equals(new Long(0))){

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
 

    private HtmlInputText inputMeasureName;

    public HtmlInputText getInputMeasureName() {
        return this.inputMeasureName;
    }
    public void setInputMeasureName(HtmlInputText inputMeasureName) {
        this.inputMeasureName = inputMeasureName;
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
        Iterator iterator = study.getStudyFiles().iterator();
        while (iterator.hasNext() ){
            StudyFile studyFile = (StudyFile) iterator.next();            
            if (studyFile.isSubsettable()){
                    selectItems.add(new SelectItem(studyFile.getId(), studyFile.getFileName()));
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

    public void selectOneFilterVariableClick(){
        boolean setSelected = checkSelectAllVariablesPrivate();
        filterCheckBox.setSelected(setSelected) ;
    }

    public void selectOneMeasureVariableClick(){
        boolean setSelected = checkSelectAllVariablesPrivate();
        measureCheckBox.setSelected(setSelected) ;
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
        boolean getSelected = filterCheckBox.isSelected();
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

    private void forceRender(){

            FacesContext.getCurrentInstance().renderResponse();

    }

    public void selectAllMeasureButton(){
        Iterator iterator = dvGenericFilteredListUI.iterator();
        boolean selectMeasureVariables = (Boolean)measureCheckBox.getValue();
        while (iterator.hasNext() ){
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            dataVariableUI.setSelected(selectMeasureVariables);
        }

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

                                dataVariables = (List<DataVariable>) visualizationService.getDataVariableMappingsFromGroupId(varGroup.getId());
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

                                varGroupUILoc.getVarGroupTypesSelectItems().add(new Long(varGroupType.getId()));
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


    private HtmlDataTable dataTableVarGroup;

    public HtmlDataTable getDataTableVarGroup() {
        return this.dataTableVarGroup;
    }

    public void setDataTableVarGroup(HtmlDataTable dataTableVarGroup) {
        this.dataTableVarGroup = dataTableVarGroup;
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

        /**
     * Holds value of property dataTableVarGroup.
     */
    private HtmlDataTable dataTableVarGroupType;

    /**
     * Getter for property dataTableVarGroup.
     * @return Value of property dataTableVarGroup.
     */
    public HtmlDataTable getDataTableVarGroupType() {
        return this.dataTableVarGroupType;
    }


    public void setDataTableVarGroupType(HtmlDataTable dataTableVarGroupType) {
        this.dataTableVarGroupType = dataTableVarGroupType;
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

    private HtmlDataTable dataTableFilterGroupTypes;

    public HtmlDataTable getDataTableFilterGroupTypes() {
        return dataTableFilterGroupTypes;
    }

    public void setDataTableFilterGroupTypes(HtmlDataTable dataTableFilterGroupTypes) {
        this.dataTableFilterGroupTypes = dataTableFilterGroupTypes;
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

    public HtmlCommandButton getValidateButton() {
        return validateButton;
    }

    public void setValidateButton(HtmlCommandButton hit) {
        this.validateButton = hit;
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

    private HtmlSelectBooleanCheckbox measureCheckBox;

    /**
     * @return the archiveCheckBox
     */
    public HtmlSelectBooleanCheckbox getMeasureCheckBox() {
        return measureCheckBox;
    }

    /**
     * @param archiveCheckBox the archiveCheckBox to set
     */
    public void setMeasureCheckBox(HtmlSelectBooleanCheckbox measureCheckBox) {
        this.measureCheckBox = measureCheckBox;
    }

    private HtmlSelectBooleanCheckbox filterCheckBox;

    /**
     * @return the archiveCheckBox
     */
    public HtmlSelectBooleanCheckbox getFilterCheckBox() {
        return filterCheckBox;
    }

    /**
     * @param archiveCheckBox the archiveCheckBox to set
     */
    public void setFilterCheckBox(HtmlSelectBooleanCheckbox filterCheckBox) {
        this.filterCheckBox = filterCheckBox;
    }
}
