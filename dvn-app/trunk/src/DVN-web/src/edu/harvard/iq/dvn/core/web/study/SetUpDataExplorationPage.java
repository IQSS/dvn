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
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.EditStudyFilesService;
import edu.harvard.iq.dvn.core.study.Metadata;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
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
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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
    private List <DataVariableUI> dvMeasureListUI = new ArrayList();
    private List <DataVariableUI> dvMeasureFilteredListUI = new ArrayList();
    private List <DataVariableUI> dvFilterListUI = new ArrayList();
    private List <DataVariableUI> dvFilterFilteredListUI = new ArrayList();
    private List <DataVariableUI> dvGenericListUI = new ArrayList();
    private List <DataVariableUI> dvGenericFilteredListUI = new ArrayList();
    private List <VarGroupType> measureGroupTypes = new ArrayList();
    private VarGroupingUI measureGrouping = new VarGroupingUI();
    private List <VarGroupingUI> filterGroupings = new ArrayList();
    private List <SelectItem> measureGroupTypesUI = new ArrayList();
    private List <SelectItem> filterGroupTypesUI = new ArrayList();
    private List <SelectItem> dataVariableSelectItems = new ArrayList();
    private List <SelectItem> studyFileIdSelectItems = new ArrayList();
    private List <SelectItem> fragmentFilterGroupTypeSelectItems = new ArrayList();

    private DataVariable xAxisVariable = new DataVariable();
    private Long xAxisVariableId =  new Long(0);


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
    private boolean editFilter = false;
    private VarGroupUI editFragmentVarGroup = new VarGroupUI();
    private VarGroupUI editFilterVarGroup = new VarGroupUI();
    private VarGroupUI editMeasureVarGroup = new VarGroupUI();
    private VarGroupUI editVariableVarGroup = new VarGroupUI();
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
         loadDvListUIs();
         measureGroupTypes = loadSelectMeasureGroupTypes();
         measureGroupTypesUI = loadSelectItemGroupTypes(GroupingType.MEASURE);
         loadMeasureGroupUIs();
         xAxisVariable = visualizationService.getXAxisVariable(dataTable.getId());
         xAxisVariableId = xAxisVariable.getId();

         if (xAxisVariable.getDataVariableMappings() != null) {
            Iterator iterator = xAxisVariable.getDataVariableMappings().iterator();
            if (iterator.hasNext()){
                DataVariableMapping mapping = (DataVariableMapping) iterator.next();
                if (mapping.isX_axis())xAxisUnits = mapping.getLabel();
            }
         }

         if (measureGrouping.getVarGrouping() == null){
             addMeasureGrouping();
         }
         dataVariableSelectItems = loadSelectItemDataVariables();
         loadFilterGroupings();
    }




    public List<VarGrouping> getVarGroupings() {
        return varGroupings;
    }

    public void setVarGroupings(List<VarGrouping> varGroupings) {
        this.varGroupings = varGroupings;        
    }
    

    public List<VarGroupType> loadSelectMeasureGroupTypes() {

        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            // Don't show OAISets that have been created for dataverse-level Lockss Harvesting
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                measureGrouping.setVarGrouping(varGrouping);
                return (List<VarGroupType>) varGrouping.getVarGroupTypes();
            }
        }
        
        return null;
    }


    public void loadFilterGroupings() {
        filterGroupings.clear();
        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            
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

    public List<VarGroupType> getMeasureGroupTypes() {
        if (varGroupings == null) return null;
        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            // Don't show OAISets that have been created for dataverse-level Lockss Harvesting
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){

                return (List<VarGroupType>) varGrouping.getVarGroupTypes();
            }
        }
        return null;
    }

    public void loadDvListUIs(){
        List <DataVariableUI> dvListUILoc = new ArrayList();
        for (DataVariable dataVariable : dvList){
            DataVariableUI dataVariableUI = new DataVariableUI();
            dataVariableUI.setDataVariable(dataVariable);
            dataVariableUI.setSelected(false);
            dvListUILoc.add(dataVariableUI);
        }
        dvMeasureListUI = dvListUILoc;
        List <DataVariableUI> dvListUILocF = new ArrayList();
        for (DataVariable dataVariable : dvList){
            DataVariableUI dataVariableUI = new DataVariableUI();
            dataVariableUI.setDataVariable(dataVariable);
            dataVariableUI.setSelected(false);
            dvListUILocF.add(dataVariableUI);
        }
        dvFilterListUI = dvListUILocF;
        List <DataVariableUI> dvListUILocG = new ArrayList();
        for (DataVariable dataVariable : dvList){
            DataVariableUI dataVariableUI = new DataVariableUI();
            dataVariableUI.setDataVariable(dataVariable);
            dataVariableUI.setSelected(false);
            dvListUILocG.add(dataVariableUI);
        }
        dvGenericListUI = dvListUILocG;
    }

    public void loadMeasureGroupUIs (){
        List <VarGroupUI> returnList = new ArrayList();
        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
           
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                List <VarGroup> localMeasureGroups = (List<VarGroup>) varGrouping.getVarGroups();
                for(VarGroup varGroup: localMeasureGroups) {

                   VarGroupUI varGroupUI = new VarGroupUI();
                   varGroupUI.setVarGroup(varGroup);
                   varGroupUI.setVarGroupTypesSelected(new ArrayList());
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
                           varGroupTypeUI.getVarGroupType().getName();
                           varGroupType.getName();
                           varGroupUI.getVarGroupTypesSelected().add(varGroupTypeUI);
                           varGroupUI.getVarGroupTypesSelectItems().add(new Long(varGroupType.getId()));
                           if (groupTypesSelectedString.isEmpty()){
                               groupTypesSelectedString+=varGroupTypeUI.getVarGroupType().getName();
                           } else {
                              groupTypesSelectedString = groupTypesSelectedString + ", " + varGroupTypeUI.getVarGroupType().getName();
                           }

                       }
                       varGroupUI.setShowTypeList(false);
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

    public void deleteMeasureGroup(){
        HtmlDataTable dataTable2 = dataTableVarGroup;
        if (dataTable2.getRowCount()>0) {
            VarGroupUI varGroupUI2 = (VarGroupUI) dataTable2.getRowData();
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



    public void deleteFilterGroupType(){
        HtmlDataTable dataTable2 = dataTableFilterGroupType;
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

        }
    }

    public void deleteMeasureGroupType(){
        HtmlDataTable dataTable2 = dataTableVarGroupType;
        if (dataTable2.getRowCount()>0) {
            List data = (List)dataTable2.getValue();
            visualizationService.removeCollectionElement(data,dataTable2.getRowIndex());
        }
    }



    public void editMeasureGroup (ActionEvent ae){
        
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupUI varGroupUI = (VarGroupUI) tempTable.getRowData();
        varGroupUI.setShowTypeList(true);
        dvGenericFilteredListUI.clear();
        for (DataVariableUI dataVariableUI: dvGenericListUI){
            dataVariableUI.setSelected(false);
                for (Object dv : varGroupUI.getDataVariablesSelected() ){
                    Long testId = new Long((String)dv);
                    if (testId.equals(dataVariableUI.getDataVariable().getId())){
                        dataVariableUI.setSelected(true);
                    }
                }
              dvGenericFilteredListUI.add(dataVariableUI);
        }


        editFragmentVarGroup = varGroupUI;
        editFilter = false;
        editMeasure = true;
        editXAxis = false;
    }

    public void editFilterGroup (ActionEvent ae){
        
        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VarGroupUI varGroupUI = (VarGroupUI) tempTable.getRowData();
        varGroupUI.setShowTypeList(true);
        varGroupUI.getVarGroup().getName();
        dvGenericFilteredListUI.clear();

        for (DataVariableUI dataVariableUI: dvGenericListUI){
            dataVariableUI.setSelected(false);
                for (Object dv : varGroupUI.getDataVariablesSelected() ){
                    Long testId = new Long((String)dv);
                    if (testId.equals(dataVariableUI.getDataVariable().getId())){
                        dataVariableUI.setSelected(true);
                    }
                }
                dvGenericFilteredListUI.add(dataVariableUI);
        }
        
        for (VarGroupingUI varGroupingUI: filterGroupings){
            if (varGroupingUI.getVarGrouping().equals(varGroupUI.getVarGroup().getGroupAssociation())){
                fragmentFilterGroupTypeSelectItems = varGroupingUI.getVarGroupTypesSelect();
            }
        }

        editFilterVarGroup = varGroupUI;
        editFilter = true;
        editMeasure = false;
        editXAxis = false;
    }


    public void editXAxisAction (){

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
        editXAxis = true;
        editMeasure = false;
        editFilter = false;


    }


    public void cancelXAxis(){
        editFilter = false;
        editMeasure = false;
        editXAxis = false;

    }

    public void cancelFragment(){
        editFilter = false;
        editMeasure = false;
        editXAxis = false;

    }

    public void saveXAxis(){
        Long SelectedId = new Long(0);
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

        }

        if (countSelected == 0){
            xAxisVariableId = SelectedId;
            xAxisVariable = new DataVariable();

        }

        editFilter = false;
        editMeasure = false;
        editXAxis = false;
    }

    public void saveFragment(){

        editFragmentVarGroup.getDataVariablesSelected().clear();
        int countSelected = 0;
        for (DataVariableUI dataVariableUI: dvGenericListUI){
            if (dataVariableUI.isSelected()){
                editFragmentVarGroup.getDataVariablesSelected().add(dataVariableUI.getDataVariable().getId());
                countSelected++;
            }
        }
        editFragmentVarGroup.setNumberOfVariablesSelected(new Long(countSelected));
             VarGroup varGroup = editFragmentVarGroup.getVarGroup();
             List  groupTypeIds = editFragmentVarGroup.getVarGroupTypesSelectItems();
             varGroup.getGroupTypes().clear();
              String groupTypesSelectedString = "";
             for(Object stringUI: groupTypeIds){
                 String id = stringUI.toString();
                 for (VarGroupType varGroupType: measureGroupTypes){

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

        editFragmentVarGroup.setGroupTypesSelectedString(groupTypesSelectedString);
        editFragmentVarGroup = new VarGroupUI();


        editMeasure = false;
        editXAxis = false;
    }

    public void saveFilterFragment(){


            editFilterVarGroup.getDataVariablesSelected().clear();

        int countSelected = 0;
        for (DataVariableUI dataVariableUI: dvGenericListUI){
            if (dataVariableUI.isSelected()){
                editFilterVarGroup.getDataVariablesSelected().add(dataVariableUI.getDataVariable().getId());
                countSelected++;
            }
        }
        editFilterVarGroup.setNumberOfVariablesSelected(new Long(countSelected));
             VarGroup varGroup = editFilterVarGroup.getVarGroup();
             List  groupTypeIds = editFilterVarGroup.getVarGroupTypesSelectItems();
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

        editFilterVarGroup.setGroupTypesSelectedString(groupTypesSelectedString);
        editFilterVarGroup = new VarGroupUI();

        editFilter = false;
        editMeasure = false;
        editXAxis = false;
    }



    public void addMeasureGroup() {

        VarGroupUI newElem = new VarGroupUI();
        newElem.setVarGroup(new VarGroup());
        newElem.getVarGroup().setGroupAssociation(measureGrouping.getVarGrouping());
        newElem.setDvMappings(null);
        newElem.getVarGroup().setGroupTypes(new ArrayList());
        newElem.setShowTypeList(true);
        int i = measureGrouping.getVarGrouping().getVarGroups().size();
        measureGrouping.getVarGrouping().getVarGroups().add(i,  newElem.getVarGroup());
        measureGrouping.getVarGroupUI().add(newElem);
        editFragmentVarGroup = newElem;
        editFilterVarGroup = new VarGroupUI();
        editMeasure = true;
        editXAxis = false;
        editFilter = false;
        
    }

    public void addFilterGroup() {

        VarGroupUI newElem = new VarGroupUI();
        newElem.setVarGroup(new VarGroup());
        newElem.getVarGroup().setGroupTypes(new ArrayList());
        Long varGroupingId = (Long) getAddFilterGroupLink().getValue();

        for(VarGrouping varGrouping: varGroupings){
             if (varGrouping.getId() == varGroupingId){
                newElem.getVarGroup().setGroupAssociation(varGrouping);
                newElem.setDvMappings(null);
                int i = varGrouping.getVarGroups().size();
                varGrouping.getVarGroups().add(i,  newElem.getVarGroup());

             }
         }
          for(VarGroupingUI varGroupingUI: filterGroupings){
             if (varGroupingUI.getVarGrouping().getId() == varGroupingId){
                varGroupingUI.getVarGroupUI().add(newElem);

             }
         }

         for (VarGroupingUI varGroupingUI: filterGroupings){
            if (varGroupingUI.getVarGrouping().equals(newElem.getVarGroup().getGroupAssociation())){
                fragmentFilterGroupTypeSelectItems = varGroupingUI.getVarGroupTypesSelect();
            }
        }

         editFilterVarGroup = newElem;

        editMeasure = false;
        editXAxis = false;
        editFilter = true;
    }

    public void addMeasureGroupType() {
        VarGroupType newElem = new VarGroupType();
        newElem.setVarGrouping(measureGrouping.getVarGrouping());
        measureGrouping.getVarGrouping().getVarGroupTypes().add(newElem);
        VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
        varGroupTypeUI.setVarGroupType(newElem);
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
                varGroupingUI.getVarGroupTypesUI().add(varGroupTypeUI);

             }
         }
    }

    public void addMeasureGrouping() {
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
        measureGroupTypesUI = loadSelectItemGroupTypes(GroupingType.MEASURE);

        dataTable.getVarGroupings().add(varGrouping);

        measureGrouping.setVarGrouping(varGrouping);


    }

    public void addFilterGrouping() {
        VarGrouping varGrouping = new VarGrouping();

        varGrouping.setGroupingType(GroupingType.FILTER);
        varGrouping.setDataTable(dataTable);
        varGrouping.setDataVariableMappings(new ArrayList());
        varGrouping.setGroups(new ArrayList());
        varGrouping.setVarGroupTypes(new ArrayList());

        dataTable.getVarGroupings().add(varGrouping);
        loadFilterGroupings();
    }

    public void deleteFilterGroup(){
        HtmlDataTable dataTable2 = dataTableFilterGroup;
        if (dataTable2.getRowCount()>0) {
            VarGroupUI varGroupUI2 = (VarGroupUI) dataTable2.getRowData();
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
            for(VarGroupingUI varGroupingUI: filterGroupings){
                if (varGroupingUI.getVarGrouping().equals(varGroup.getGroupAssociation())){
                    varGroupingUI.getVarGroupUI().remove(varGroupUI2);
                    varGroupingUI.getVarGrouping().getVarGroups().remove(varGroup);
                }
            }

        }
    }

    public void deleteFilterGrouping(){

        Long varGroupingId = (Long) getDeleteFilterGroupLink().getValue();

        List <VarGrouping> removeList = new ArrayList();
        List <VarGroupingUI> removeListUI = new ArrayList();
        List <VarGroupingUI> tempList = new ArrayList(filterGroupings);
           for(VarGroupingUI varGroupingUI: tempList){
                if (varGroupingUI.getVarGrouping().getId().equals(varGroupingId)){
                    removeList.add(varGroupingUI.getVarGrouping());
                    removeListUI.add(varGroupingUI);
                }
             }


           for(VarGrouping varGroupingRem : removeList){

               visualizationService.removeCollectionElement(varGroupings,varGroupingRem);
           }

           for(VarGroupingUI varGroupingRem : removeListUI){

              filterGroupings.remove(varGroupingRem);
           }

    }


    public String cancel(){
        visualizationService.cancel();
        return "editStudyFiles";
    }

    public boolean validateForRelease(){

        boolean valid = true;

        if (!visualizationService.validateAtLeastOneFilterMapping(dataTable)) {
            FacesMessage message = new FacesMessage("Each variable mapping must include at least one Filter.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
            valid = false;
        }

        if (!visualizationService.validateXAxisMapping(dataTable)) {
            FacesMessage message = new FacesMessage("You must select one X-axis variable and it cannot be mapped to any Measure or Filter.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
            valid = false;
        }

        if (!visualizationService.validateOneMeasureMapping(dataTable)) {
            FacesMessage message = new FacesMessage("Each variable mapping must include one Measure.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
            valid = false;
        }

        return valid;
    }

    public String validate(){

        if (!visualizationService.validateAtLeastOneFilterMapping(dataTable)) {
            FacesMessage message = new FacesMessage("Each variable mapping must include at least one Filter.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
        }

        if (!visualizationService.validateXAxisMapping(dataTable)) {
            FacesMessage message = new FacesMessage("You must select one X-axis variable and it cannot be mapped to any Measure or Filter.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
        }

        if (!visualizationService.validateOneMeasureMapping(dataTable)) {
            FacesMessage message = new FacesMessage("Each variable mapping must include one Measure.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(validateButton.getClientId(fc), message);
        }

        return "";
    }

    public String release(){
        if (validateForRelease()) {
            dataTable.setVisualizationEnabled(true);
        }
        return "";
    }

    public String unRelease(){
        dataTable.setVisualizationEnabled(false);
        return "";
    }


    public String saveAndExit(){
       save();
       return "editStudyFiles";
    }

    public String save() {

        
        List <DataVariableMapping> removeList = new ArrayList();
        List <DataVariable> tempList = new ArrayList(dvList);
           for(DataVariable dataVariable: tempList){
               List <DataVariableMapping> deleteList = (List <DataVariableMapping>) dataVariable.getDataVariableMappings();
                for (DataVariableMapping dataVariableMapping : deleteList ){
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

        List <VarGroupUI> measureVarGroupsUI  = (List) measureGrouping.getVarGroupUI();
        if (measureVarGroupsUI !=null && measureGroupTypes != null ) {
         for(VarGroupUI varGroupUI: measureVarGroupsUI){
             VarGroup varGroup = varGroupUI.getVarGroup();
             List  groupTypeIds = varGroupUI.getVarGroupTypesSelectItems();
             varGroup.getGroupTypes().clear();
             for(Object stringUI: groupTypeIds){
                 String id = stringUI.toString();
                 for (VarGroupType varGroupType: measureGroupTypes){

                     if (varGroupType.getId() !=null &&  varGroupType.getId().equals(new Long(id))){
                         varGroup.getGroupTypes().add(varGroupType);
                     }                        
                 }
             }
             List  dataVariableIds = varGroupUI.getDataVariablesSelected();

             for(Object dataVariableId:  dataVariableIds){
                 String id = dataVariableId.toString();
                for(DataVariable dataVariable: dvList){
                    if (dataVariable.getId() !=null &&  dataVariable.getId().equals(new Long(id))){
                         DataVariableMapping dataVariableMapping = new DataVariableMapping();
                         dataVariableMapping.setDataTable(dataTable);
                         dataVariableMapping.setDataVariable(dataVariable);
                         dataVariableMapping.setGroup(varGroup);
                         dataVariableMapping.setVarGrouping(measureGrouping.getVarGrouping());
                         dataVariableMapping.setX_axis(false);
                         dataVariable.getDataVariableMappings().add(dataVariableMapping);
                     }

              }
             }

         }
        }
        for(VarGroupingUI varGroupingUI: filterGroupings){
            List <VarGroupUI> filterVarGroupsUI  = (List) varGroupingUI.getVarGroupUI();
            if (filterVarGroupsUI !=null && varGroupingUI.getVarGroupTypesUI() != null ) {
            for(VarGroupUI varGroupUI: filterVarGroupsUI){
                VarGroup varGroup = varGroupUI.getVarGroup();
                List  groupTypeIds = varGroupUI.getVarGroupTypesSelectItems();
                varGroup.getGroupTypes().clear();
                for(Object stringUI: groupTypeIds){
                    String id = stringUI.toString();
                    for (VarGroupType varGroupType: varGroupingUI.getVarGrouping().getVarGroupTypes()){
                        if (varGroupType.getId() !=null &&  varGroupType.getId().equals(new Long(id))){
                            varGroup.getGroupTypes().add(varGroupType);
                        }
                    }
                }

                List  dataVariableIds = varGroupUI.getDataVariablesSelected();

                    for(Object dataVariableId:  dataVariableIds){
                        String id = dataVariableId.toString();
                        for(DataVariable dataVariable: dvList){
                            if (dataVariable.getId() !=null &&  dataVariable.getId().equals(new Long(id))){
                                DataVariableMapping dataVariableMapping = new DataVariableMapping();
                                dataVariableMapping.setDataTable(dataTable);
                                dataVariableMapping.setDataVariable(dataVariable);
                                dataVariableMapping.setGroup(varGroup);
                                dataVariableMapping.setVarGrouping(varGroupingUI.getVarGrouping());
                                dataVariableMapping.setX_axis(false);
                                dataVariable.getDataVariableMappings().add(dataVariableMapping);
                            }

                        }
                    }
                }
            }
        }


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

    public List<SelectItem> loadSelectItemGroupTypes(GroupingType groupingType){
        List selectItems = new ArrayList<SelectItem>();

        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            // Don't show OAISets that have been created for dataverse-level Lockss Harvesting
            if (varGrouping.getGroupingType().equals(groupingType)){
                selectItems.add(new SelectItem(0, "Select a Measure Type..."));
                List <VarGroupType> varGroupTypes = (List<VarGroupType>) varGrouping.getVarGroupTypes();
                for(VarGroupType varGroupType: varGroupTypes) {
                    selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                }
            }
        }
        return selectItems;
    }

   public List<SelectItem> loadStudyFileSelectItems(){
        List selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem(0, "Select a File"));
        Iterator iterator = study.getStudyFiles().iterator();
        while (iterator.hasNext() ){
            StudyFile studyFile = (StudyFile) iterator.next();
            // Don't show OAISets that have been created for dataverse-level Lockss Harvesting
            if (studyFile.isSubsettable()){

                    selectItems.add(new SelectItem(studyFile.getId(), studyFile.getFileName()));
            }
        }
        return selectItems;
    }

    public void  updateMeasureVariableList(ValueChangeEvent ce){
         
        String checkString = (String) getInputVariableMeasure().getValue();
 
        Iterator iterator = dvGenericListUI.iterator();

        dvGenericFilteredListUI.clear();
        while (iterator.hasNext() ){
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            dataVariableUI.setSelected(false);
            List  dvSelected = editFragmentVarGroup.getDataVariablesSelected();
                for (Object selectedID:   dvSelected){
                    Long testID = new Long ((String) selectedID);
                    if(testID.equals(dataVariableUI.getDataVariable().getId())) {
                        dataVariableUI.setSelected(true);
                    }
                }
            if (dataVariableUI.getDataVariable().getName().contains(checkString)) {
                dvGenericFilteredListUI.add(dataVariableUI);
            }
        }
    }

    public void  updateVariableList(ValueChangeEvent ce){

        String checkString = (String) inputVariableGeneric.getValue();

        Iterator iterator = dvGenericListUI.iterator();
        boolean xAxis = true;
        dvGenericFilteredListUI.clear();
        while (iterator.hasNext() ){
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            dataVariableUI.setSelected(false);
            if(!xAxis){
                List  dvSelected = editVariableVarGroup.getDataVariablesSelected();
                for (Object selectedID:   dvSelected){
                    Long testID = new Long ((String) selectedID);
                    if(testID.equals(dataVariableUI.getDataVariable().getId())) {
                        dataVariableUI.setSelected(true);
                    }
                }

            } else {

                    if(xAxisVariable.getId().equals(dataVariableUI.getDataVariable().getId())) {
                        dataVariableUI.setSelected(true);
                    }

            }

            if (checkString != null && dataVariableUI.getDataVariable().getName().contains(checkString)) {
                dvGenericFilteredListUI.add(dataVariableUI);
            }
        }
        currentTitle = checkString;
    }

    public void  updateXAxisVariableList(ValueChangeEvent ce){

        String checkString = (String) inputVariableGeneric.getValue();

        Iterator iterator = dvGenericListUI.iterator();
        dvGenericFilteredListUI.clear();
        while (iterator.hasNext() ){

            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            dataVariableUI.setSelected(false);

            if (dataVariableUI.getDataVariable().getName().contains(checkString)) {
                dvGenericFilteredListUI.add(dataVariableUI);
            }
        }

    }

    public void  updateFilterVariableList(ValueChangeEvent ce){
        
        String checkString = (String) getInputVariableFilter().getValue();
        Iterator iterator = dvGenericListUI.iterator();
        dvGenericFilteredListUI.clear();

        while (iterator.hasNext() ){
            DataVariableUI dataVariableUI = (DataVariableUI) iterator.next();
            dataVariableUI.setSelected(false);
            List  dvSelected = editFilterVarGroup.getDataVariablesSelected();
                for (Object selectedID:   dvSelected){
                    Long testID = new Long ((String) selectedID);
                    if(testID.equals(dataVariableUI.getDataVariable().getId())) {
                        dataVariableUI.setSelected(true);
                    }
                }
            if (dataVariableUI.getDataVariable().getName().contains(checkString)) {
                dvGenericFilteredListUI.add(dataVariableUI);
            }
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
                           varGroupUILoc.setVarGroupTypesSelected(new ArrayList());
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
                    if (varGroupTypes !=null ) {
                        String groupTypesSelectedString  = "";
                       for(VarGroupType varGroupType: varGroupTypes){
                           VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
                           varGroupTypeUI.setVarGroupType(varGroupType);
                           varGroupTypeUI.setEnabled(true);
                           varGroupTypeUI.getVarGroupType().getName();
                           varGroupType.getName();
                           varGroupUILoc.getVarGroupTypesSelected().add(varGroupTypeUI);
                           varGroupUILoc.getVarGroupTypesSelectItems().add(new Long(varGroupType.getId()));
                           if (groupTypesSelectedString.isEmpty()){
                               groupTypesSelectedString+=varGroupTypeUI.getVarGroupType().getName();
                           } else {
                              groupTypesSelectedString = groupTypesSelectedString + ", " + varGroupTypeUI.getVarGroupType().getName();
                           }

                       }
                       varGroupUILoc.setShowTypeList(false);
                       varGroupUILoc.setGroupTypesSelectedString(groupTypesSelectedString);
                       }
                    
                           varGroupUIList.add(varGroupUILoc);
                       }
                    }

        varGroupingUI.setVarGroupUI(varGroupUIList) ;
    }

    public List<SelectItem> loadSelectItemDataVariables(){
        List selectItems = new ArrayList<SelectItem>();

        Iterator iterator = dvList.iterator();
        while (iterator.hasNext() ){
            DataVariable dataVariable = (DataVariable) iterator.next();
            selectItems.add(new SelectItem(dataVariable.getId(), dataVariable.getName()));
        }
        return selectItems;
    }
    public List<SelectItem> getSelectMeasureGroupTypes() {

        return measureGroupTypesUI;
    }

    public List<SelectItem> getSelectFilterGroupTypes() {

        return filterGroupTypesUI;
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

    public void setDataTableXAxis(HtmlDataTable dataTableXAxisXAxis) {
        this.dataTableXAxis = dataTableXAxis;
    }

    private HtmlDataTable dataTableFilterGroup;

    public HtmlDataTable getDataTableFilterGroup() {
        return this.dataTableFilterGroup;
    }

    public void setDataTableFilterGroup(HtmlDataTable dataTableFilterGroup) {
        this.dataTableFilterGroup = dataTableFilterGroup;
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
    public List<VarGroupingUI> getFilterGroupings() {
        return filterGroupings;
    }

    public void setFilterGroupings(List<VarGroupingUI> filterGroupings) {
        this.filterGroupings = filterGroupings;
    }


    private HtmlDataTable dataTableFilterGroupings;

    public HtmlDataTable getDataTableFilterGroupings() {
        return dataTableFilterGroupings;
    }

    public void setDataTableFilterGroupings(HtmlDataTable dataTableFilterGroupings) {
        this.dataTableFilterGroupings = dataTableFilterGroupings;
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

    private HtmlInputText inputFilterGroupTypeName;

    public HtmlInputText getInputFilterGroupTypeName() {
        return this.inputFilterGroupTypeName;
    }
    public void setInputFilterGroupTypeName(HtmlInputText inputFilterGroupTypeName) {
        this.inputFilterGroupTypeName = inputFilterGroupTypeName;
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
    private HtmlCommandLink deleteFilterGroupLink;

    public HtmlCommandLink getDeleteFilterGroupLink() {
        return this.deleteFilterGroupLink;
    }
    public void setDeleteFilterGroupLink(HtmlCommandLink deleteFilterGroupLink) {
        this.deleteFilterGroupLink = deleteFilterGroupLink;
    }
    public List<SelectItem> getDataVariableSelectItems() {
        return dataVariableSelectItems;
    }

    public void setDataVariableSelectItems(List<SelectItem> dataVariableSelectItems) {
        this.dataVariableSelectItems = dataVariableSelectItems;
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
    public List<DataVariableUI> getDvMeasureFilteredListUI() {
        if (getInputVariableMeasure() == null) return dvMeasureListUI;
        if (getInputVariableMeasure().getValue() == null) return dvMeasureListUI;
        if (getInputVariableMeasure().getValue().equals("")) return dvMeasureListUI;
        return dvMeasureFilteredListUI;
    }

   public List<DataVariableUI> getDvGenericListUI() {

        return dvGenericListUI;
    }

    public void setDvMeasureFilteredListUI(List<DataVariableUI> dvFilteredListUI) {
        this.dvMeasureFilteredListUI = dvFilteredListUI;
    }

    public List<DataVariableUI> getDvFilterFilteredListUI() {
        
        return dvFilterFilteredListUI;

    }

    public void setDvFilterFilteredListUI(List<DataVariableUI> dvFilterFilteredListUI) {
        this.dvFilterFilteredListUI = dvFilterFilteredListUI;
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
        return dvGenericFilteredListUI;
    }

    public VarGroupUI getEditFragmentVarGroup() {
        return editFragmentVarGroup;
    }

    public void setEditFragmentVarGroup(VarGroupUI editFragmentVarGroup) {
        this.editFragmentVarGroup = editFragmentVarGroup;
    }
        public boolean isEditFilter() {
        return editFilter;
    }

    public void setEditFilter(boolean editFilter) {
        this.editFilter = editFilter;
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

    public List<SelectItem> getFragmentFilterGroupTypeSelectItems() {
        return fragmentFilterGroupTypeSelectItems;
    }

    public void setFragmentFilterGroupTypeSelectItems(List<SelectItem> fragmentFilterGroupTypeSelectItems) {
        this.fragmentFilterGroupTypeSelectItems = fragmentFilterGroupTypeSelectItems;
    }
}
