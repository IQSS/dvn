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
import java.beans.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import com.icesoft.faces.component.panelseries.PanelSeries;
//import com.sun.corba.se.impl.io.TypeMismatchException;
import javax.management.Query;
import javax.servlet.http.HttpServletRequest;



import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.visualization.DataVariableMapping;
import edu.harvard.iq.dvn.ingest.dsb.FieldCutter;
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnJavaFieldCutter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 *
 * @author skraffmiller
 */
public class ExploreDataPage extends VDCBaseBean  implements Serializable {
    @EJB
    VariableServiceLocal varService;
    @EJB
    VisualizationServiceLocal      visualizationService;

    private String measureLabel;
    private String lineLabel;
    private String lineColor;
    private String dataTableId = "";
    private List <VarGrouping> varGroupings = new ArrayList();
    private List <VarGroupingUI> filterGroupings = new ArrayList();
    private VarGrouping measureGrouping;
    private List <String> filterStrings = new ArrayList();
    private List <SelectItem> selectMeasureItems = new ArrayList();
    private List <SelectItem> selectMeasureGroupTypes = new ArrayList();
 
    private List <VisualizationLineDefinition> vizLines = new ArrayList();
    private Long selectedMeasureId = new Long(0);
    private Long selectedFilterGroupId = new Long(0);
    private int groupTypeId = 0;
    private List <DataVariable> dvList;
    private List <String> measureString= new ArrayList();
    private DataVariable xAxisVar;
    private DataTable dt = new DataTable();
    private DataVariable dataVariableSelected = new DataVariable();
    private String columnString = new String();
    private Long numberOfColumns =  new Long(0);
    private String dataString = "";
    private Long displayType = new Long(0);
    private Study studyIn = new Study();
    private Long studyId = new Long(0);



    public ExploreDataPage() {
        
    }

    @Override
    public void init() {
        super.init();
        Long studyFileId = new Long( getVDCRequestBean().getRequestParam("fileId"));
        visualizationService.setDataTableFromStudyFileId(studyFileId);
        studyIn = visualizationService.getStudyFromStudyFileId(studyFileId);
        studyId = studyIn.getId();
        dt = visualizationService.getDataTable();
        dvList = dt.getDataVariables();
        varGroupings = dt.getVarGroupings();
        measureLabel = loadMeasureLabel();
        
        measureGrouping = loadMeasureGrouping();
        selectMeasureGroupTypes = loadSelectMeasureGroupTypes();        
        selectMeasureItems = loadSelectMeasureItems(0);
        
        xAxisVar =  visualizationService.getXAxisVariable(dt.getId());

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

    protected PanelSeries filterPanelGroup;

    public PanelSeries getFilterPanelGroup() {
        return filterPanelGroup;
    }

    public void setFilterPanelGroup(PanelSeries filterPanelGroup) {
        this.filterPanelGroup = filterPanelGroup;
    }

    public String loadMeasureLabel() {
        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();

            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                return varGrouping.getName();
            }

        }
        return "Measure";
    }

    private void loadFilterGroupings(){
        filterStrings.clear();
        filterGroupings.clear();
        varGroupings = visualizationService.getFilterGroupingsFromMeasureId(selectedMeasureId);
        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();

            if (varGrouping.getGroupingType().equals(GroupingType.FILTER)){
                List <VarGroup> filterGroups = new ArrayList();
                List <VarGroupTypeUI> filterGroupTypes = new ArrayList();

                VarGroupingUI vgUI = new VarGroupingUI();
                vgUI.setVarGrouping(varGrouping);
                loadFilterGroups(filterGroups);
                loadFilterGroupTypes(filterGroupTypes, varGrouping.getId());
                vgUI.setVarGroupTypesUI(filterGroupTypes);
                filterGroupings.add(vgUI);
            }
        }

    }

    private void loadFilterGroups(List <VarGroup> inList){
        inList.clear();

            List groups = visualizationService.getFilterGroupsFromMeasureId(selectedMeasureId);

                Iterator iterator = groups.iterator();
                while (iterator.hasNext() ){
                VarGroup varGroup = (VarGroup) iterator.next();
                inList.add(varGroup);
            }



    }


    private void loadFilterGroupTypes(List <VarGroupTypeUI> inList, Long groupingId){
        
        List groups = visualizationService.getGroupTypesFromGroupingId(groupingId);
        Iterator iterator = groups.iterator();
        while (iterator.hasNext() ){
            VarGroupType varGroupType = (VarGroupType) iterator.next();
            VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
            varGroupTypeUI.setVarGroupType(varGroupType);
            varGroupTypeUI.setEnabled(false);
            inList.add(varGroupTypeUI);
        }
    }

     private VarGrouping loadMeasureGrouping(){

        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                return varGrouping;
            }
        }
        return null;

    }

    public void setMeasureLabel(String measureLabel) {
        this.measureLabel = measureLabel;
    }

    public String getMeasureLabel() {
        return measureLabel;
    }

    public VarGrouping getMeasureGrouping() {
        return measureGrouping;
    }

    public void setMeasureGrouping(VarGrouping measureGrouping) {
        this.measureGrouping = measureGrouping;
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
        //this.selectMeasureItems = loadSelectMeasureItems(i);
        if (selectedMeasureId != null) {
            loadFilterGroupings();
        }
       
    }

    public List<SelectItem> loadSelectMeasureItems(int grouptype_id) {

        List selectItems = new ArrayList<SelectItem>();
        List <VarGroup> varGroups = new ArrayList();
        varGroupings = dt.getVarGroupings();
        selectItems.add(new SelectItem(0, "Select" ));
        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            // Don't show OAISets that have been created for dataverse-level Lockss Harvesting
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){

                if (grouptype_id == 0) {
                    varGroups = (List<VarGroup>) varGrouping.getVarGroups();
                } else {
                    varGroups = visualizationService.getGroupsFromGroupTypeId(new Long(grouptype_id));
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
                        added = true;
                    }

                }
            }
        }
        return selectItems;
    }

    public List<SelectItem> loadSelectMeasureGroupTypes() {
        List selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem(0, "Select"));
        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            // Don't show OAISets that have been created for dataverse-level Lockss Harvesting
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){

                List <VarGroupType> varGroupTypes = (List<VarGroupType>) varGrouping.getVarGroupTypes();
                for(VarGroupType varGroupType: varGroupTypes) {
                    selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
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
                selectItems.add(new SelectItem(varGrouping.getId(), "Select"));
                List <VarGroupType> varGroupTypes = (List<VarGroupType>) varGrouping.getVarGroupTypes();
                for(VarGroupType varGroupType: varGroupTypes) {
                    selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                }
            }
        }
        return selectItems;
    }

    public List<SelectItem> getSelectFilterGroupTypes() {
        Long groupingId = new Long(0);
        groupingId = new Long(filterPanelGroup.getAttributes().get("groupingId").toString());
        List selectItems = new ArrayList<SelectItem>();
            
                selectItems.add(new SelectItem(groupingId, "Select"));
                List <VarGroupType> varGroupTypes = (List<VarGroupType>)  visualizationService.getGroupTypesFromGroupingId(new Long(groupingId));
                for(VarGroupType varGroupType: varGroupTypes) {
                    selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                }

        return selectItems;
    }

    public List<VarGroupType> loadFilterGroupTypes() {
        Long groupingId = new Long(0);

        List selectItems = new ArrayList<VarGroupType>();

                selectItems.add(new SelectItem(groupingId, "Select"));
                List <VarGroupType> varGroupTypes = (List<VarGroupType>)  visualizationService.getGroupTypesFromGroupingId(new Long(groupingId));
                for(VarGroupType varGroupType: varGroupTypes) {
                    selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                }

        return selectItems;
    }

    public List<SelectItem> getSelectFilterGroups() {
        if (selectedMeasureId.equals(new Long(0))){
            return getFilterGroupsWithoutMeasure();
        } else {
            return getFilterGroupsWithMeasure();
        }



    }

    private List<SelectItem> getFilterGroupsWithMeasure(){

         List selectItems = new ArrayList<SelectItem>();
          Long groupingId = new Long(filterPanelGroup.getAttributes().get("groupingId").toString());
          selectItems.add(new SelectItem(0, "Select"));
          boolean addAll = false;
          List <VarGroup> varGroupsAll = (List<VarGroup>)  visualizationService.getFilterGroupsFromMeasureId(selectedMeasureId);
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

                            for (VarGroupTypeUI varGroupTypeUI: varGroupTypesUI){
                                if (varGroupTypeUI.isEnabled() || allFalse ){
                                    List <VarGroup> varGroups = (List) varGroupTypeUI.getVarGroupType().getGroups();
                                    for (VarGroup varGroupTest: varGroups) {
                                        if (!added && varGroupTest.getId().equals(varGroup.getId())  && varGroup.getGroupAssociation().equals(varGroupingUI.getVarGrouping()) ){
                                            selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                                            added = true;
                                        }
                                    }

                                }
                            }

                        } else {
                               if (!added && varGroup.getGroupAssociation().equals(varGroupingUI.getVarGrouping()) ){
                                    selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                                    added = true;
                               }
                        }
                      }
                  }                   
              }
          return selectItems;

    }

    private List<SelectItem> getFilterGroupsWithoutMeasure(){
            List selectItems = new ArrayList<SelectItem>();
        Long groupingId = new Long(0);
        if (filterPanelGroup.getAttributes().get("groupingId") != null){
            groupingId = new Long(filterPanelGroup.getAttributes().get("groupingId").toString());
        }
         selectItems.add(new SelectItem(0, "Select"));
            Iterator itrG = filterGroupings.iterator();
            while (itrG.hasNext()){
                VarGroupingUI thisVarGrouping = (VarGroupingUI) itrG.next();
                if (thisVarGrouping.getVarGrouping().getId().equals(groupingId)){
                   List  varGroupTypeUIList = (List) thisVarGrouping.getVarGroupTypesUI();
                   if (!varGroupTypeUIList.isEmpty()) {
                        List <VarGroup> varGroupsAll = (List<VarGroup>)  visualizationService.getGroupsFromGroupingId(groupingId);
                        for(VarGroup varGroup: varGroupsAll) {
                            if (visualizationService.getGroupTypesFromGroupId(varGroup.getId()).isEmpty()){
                                selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                            }

                        }
                   Iterator iterator = varGroupTypeUIList.iterator();
                    while (iterator.hasNext() ){
                        VarGroupTypeUI varGroupType = (VarGroupTypeUI) iterator.next();
                        if (varGroupType.isEnabled()){
                        List <VarGroup> varGroups = (List<VarGroup>)  visualizationService.getGroupsFromGroupTypeId(varGroupType.getVarGroupType().getId());
                        for(VarGroup varGroup: varGroups) {
                            boolean add = true;
                            Iterator itrb = selectItems.iterator();
                            while (itrb.hasNext()){
                                SelectItem selectItemTest = (SelectItem) itrb.next();
                                SelectItem selectItemNew = new SelectItem(varGroup.getId(), varGroup.getName());
                                if (selectItemTest.getValue().equals(selectItemNew.getValue()) ) {
                                            add = false;
                                 }
                            }
                            if (add) selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                        }
                        }

                    }
                   }  else {
                        List <VarGroup> varGroups = (List<VarGroup>)  visualizationService.getGroupsFromGroupingId(groupingId);
                        for(VarGroup varGroup: varGroups) {

                            selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                        }

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
        this.displayType = new Long((String) value );
    }

    public void reset_MeasureItems(ValueChangeEvent ae){
        int i = (Integer) ae.getNewValue();
        this.selectMeasureItems = loadSelectMeasureItems(i);
    }

    public void reset_FilterItems(){
        
        this.getSelectFilterGroups();
    }

    public int getGroupTypeId() {
        return groupTypeId;
    }

    public void setGroupTypeId(int groupTypeId) {
        this.groupTypeId = groupTypeId;
    }


    public void addLine(){
        
        if ( lineLabel.isEmpty() || lineLabel.trim().equals("") ) {
            FacesMessage message = new FacesMessage("Please Enter a label");
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

          VisualizationLineDefinition vizLine = new VisualizationLineDefinition();
          vizLine.setMeasureGrouping(measureGrouping);
          vizLine.setMeasureGroup(visualizationService.getGroupFromId(selectedMeasureId));

          List <VarGroup> selectedFilterGroups = new ArrayList();
           for(VarGroupingUI varGrouping: filterGroupings) {
               VarGroup filterGroup = visualizationService.getGroupFromId(varGrouping.getSelectedGroupId());
               selectedFilterGroups.add(filterGroup);
           }
           vizLine.setFilterGroups(selectedFilterGroups);
           vizLine.setLabel(lineLabel);
           vizLine.setMeasureLabel(measureLabel);
           vizLine.setColor(lineColor);
           vizLine.setBorder("border:1px solid black;");
           vizLine.setVariableId(dataVariableSelected.getId());
           vizLines.add(vizLine);
           this.numberOfColumns = new Long(vizLines.size());
           getDataTable();
        }

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
           getDataTable();

    }
    private boolean validateSelections(){
        boolean valid  = true;
        int count = 0;
        if (selectedMeasureId == 0){
            FacesMessage message = new FacesMessage("You must select a measure.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);
            return false;
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

           for(VarGroupingUI varGrouping: filterGroupings) {
               ArrayList <DataVariable> tempList = new ArrayList();
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
           }

           List <DataVariable> testCounts = resultListFilter;
           int maxCount = 0;

           for(DataVariable resultDV:resultListFilter ) {
                    int maxCountTest = 0;
                    for (DataVariable testDV:testCounts ){
                        if (resultDV.equals(testDV)){
                            maxCountTest++;
                        }
                    }

                    if (maxCountTest > maxCount) maxCount = maxCountTest;

            }

        List <DataVariable> finalList = new ArrayList();
        finalList = resultList;
        valid = (maxCount == filterGroupings.size() );
        List <DataVariable> removeList = new ArrayList();
        for (DataVariable dv : finalList){
            boolean remove = true;
            for (ArrayList arrayList : filterGroupingList){
                for (Object dvO: arrayList){
                    DataVariable dvF = (DataVariable) dvO;
                        if (dvF.equals(dv)){
                            remove = false;
                        }
                }
                if (remove) removeList.add(dv);
            }
        }

        for(DataVariable dataVarRemove : removeList){
              finalList.remove(dataVarRemove);
        }


        if(finalList.size() == 1) {
            dataVariableSelected = finalList.get(0);
        } else {
            FacesMessage message = new FacesMessage("Your selections do not match any in the data table.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);
            return false;
        }



        if (!valid){
            FacesMessage message = new FacesMessage("Your selections do not match any in the data table.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);

        }
        return (maxCount == filterGroupings.size() );
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

    public String getDataTableId() {
        return dataTableId;
    }

    public void setDataTableId(String dataTableId) {
        this.dataTableId = dataTableId;
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

    public void setDeleteLineButton(HtmlCommandButton hit) {
        this.deleteLineButton = hit;
    }

    public String getDataTable(){

    StudyFile sf = dt.getStudyFile();
    columnString = "";
    try {
        File tmpSubsetFile = File.createTempFile("tempsubsetfile.", ".tab");
        List <DataVariable> dvsIn = new ArrayList();
        dvsIn.add(xAxisVar);
        columnString = columnString + xAxisVar.getName();
        for (VisualizationLineDefinition vld: vizLines){
            Long testId = vld.getVariableId();
            for (DataVariable dv : dt.getDataVariables()){
                if (dv.getId().equals(testId)){
                     dvsIn.add(dv);
                     columnString = columnString + "," + vld.getLabel();
                }
            }

        }

        Set<Integer> fields = new LinkedHashSet<Integer>();
        List<DataVariable> dvs = dvsIn;

        for (Iterator el = dvs.iterator(); el.hasNext();) {
            DataVariable dv = (DataVariable) el.next();
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

            loadDataTableData(fileList);

            int countRows = 0;

          // googleDataTable = com.google.gwt.visualization.client.DataTable.create();

           // com.google.gwt.visualization.client.DataTable googleDataTable = com.google.gwt.visualization.client.DataTable.create();

            
            return subsetFileSize.toString();


        }
        return "";

        } catch  (IOException e) {
            e.printStackTrace();
                            return "failure";
           }


    }

    private void loadDataTableData(List inStr){
                    String output = "";
                for (Object inObj: inStr ){
                    String nextStr = (String) inObj;
                    String[] columnDetail = nextStr.split("\t");
                    String[] test = columnDetail;

                    String col = "";
                    if (test.length > 1)
                    {
                        for (int i=0; i<test.length; i++){
                            if (i == 0) {
                                col =  test[i];
                            } else {
                               col = col + ", " +  test[i];
                            }

                        }

                        col = col + ";";

                    }

                    output = output + col;
                }

                dataString = output;
    }

    private HtmlDataTable dataTableVizLines;

    public HtmlDataTable getDataTableVizLines() {
        return this.dataTableVizLines;
    }

    public void setDataTableVizLines(HtmlDataTable dataTableVizLines) {
        this.dataTableVizLines = dataTableVizLines;
    }


    public String getColumnString() {

        return columnString;
    }


    public void setDataFile(String dataFile) {
        this.columnString = dataFile;
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
}
