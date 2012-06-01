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
package edu.harvard.iq.dvn.core.visualization;

import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.visualization.VarGrouping.GroupingType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.PrePassivate;


import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

/**
 *
 * @author skraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class VisualizationServiceBean implements VisualizationServiceLocal {

    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    EntityManager em;
    DataTable dt;

        /**
     *  Initialize the bean with a dataTable for editing
     */
    public void setDataTable(Long dataTableId ) {
        dt = em.find(DataTable.class,dataTableId);
        if (dt==null) {
            throw new IllegalArgumentException("Unknown data table id: "+dataTableId);
        }
    }

    @Override
    public void setDataTableFromStudyFileId(Long studyFileId) {
        String query = "SELECT d FROM  DataTable d where d.studyFile.id = " + studyFileId + "  ";
        dt = (DataTable) em.createQuery(query).getSingleResult();
        if (dt==null) {
            throw new IllegalArgumentException("Unknown data table  with study file id: "+studyFileId);
        }
    }

    @Override
    public Study getStudyFromStudyFileId(Long studyFileId) {
        String query = "SELECT s FROM  Study s, StudyFile f where s.id = " +
                "f.study.id and f.id = " + studyFileId + "  ";
        Study study = (Study) em.createQuery(query).getSingleResult();
        if (study==null) {
            throw new IllegalArgumentException("Unknown data table  with study file id: "+studyFileId);
        }
        return study;
    }

    public DataTable getDataTable( ) {
        return dt;
    }

    @Override
    public List <VarGrouping> getGroupings(Long dataTableId) {

        String query = "SELECT g FROM  VarGrouping g where g.dataTable.id = " + dataTableId + "  ORDER BY g.id";
        return (List) em.createQuery(query).getResultList();

    }

    @Override
    public List <VarGroupType> getGroupTypes(Long dataTableId) {

        String query = "SELECT v FROM  VarGroupType v, VarGrouping g where g.dataTable.id = " + dataTableId + " and g.id = v.varGrouping.id  ORDER BY g.id";
        return (List) em.createQuery(query).getResultList();

    }

    @Override
    public List <VarGroupType> getFilterGroupTypes(Long dataTableId) {

        String query = "SELECT v FROM  VarGroupType v, VarGrouping g where g.dataTable.id = " + dataTableId + " and g.id = v.varGrouping.id and v.varGrouping.groupingType = v.GroupingType.FILTER  ORDER BY g.id";
        return (List) em.createQuery(query).getResultList();

    }


    @Override
    public boolean validateGroupings(Long dataTableId) {
        List <VarGrouping> groupings = getGroupings(dataTableId);
        return true;
    }

    @Override
    public List getVariableMappings(Long dataTableId) {

        String query = "SELECT m FROM  DataVariableMapping m where m.datatable.id = " + dataTableId + "  ORDER BY m.datavariable.id";
        return (List) em.createQuery(query).getResultList();

    }

    private List getMeasureAndFilterMappings (List<DataVariableMapping> mappingList){
        List <DataVariableMapping> returnList = new ArrayList();

        for (DataVariableMapping dvm: mappingList){
            if (!dvm.isX_axis()  && !(dvm.getVarGrouping().getGroupingType().equals(GroupingType.SOURCE))){
                returnList.add(dvm);
            }
        }

        return returnList;
    }

    private List <DataVariableMapping> getMeasureMappings (List<DataVariableMapping> mappingList){
        List <DataVariableMapping> returnList = new ArrayList();

        for (DataVariableMapping dvm: mappingList){
            if (!dvm.isX_axis()  &&  (dvm.getVarGrouping().getGroupingType().equals(GroupingType.MEASURE))){
                returnList.add(dvm);
            }
        }

        return returnList;
    }

    private List <DataVariableMapping> getFilterMappings (List<DataVariableMapping> mappingList){
        List <DataVariableMapping> returnList = new ArrayList();

        for (DataVariableMapping dvm: mappingList){
            if (!dvm.isX_axis()  &&  (dvm.getVarGrouping().getGroupingType().equals(GroupingType.FILTER))){
                returnList.add(dvm);
            }
        }

        return returnList;
    }

    public List getSourceMappings (List<DataVariableMapping> mappingList){
        List <DataVariableMapping> returnList = new ArrayList();

        for (DataVariableMapping dvm: mappingList){
            if (!dvm.isX_axis()  &&  (dvm.getVarGrouping().getGroupingType().equals(GroupingType.SOURCE))){
                returnList.add(dvm);
            }
        }

        return returnList;
    }

    @Override
    public boolean validateOneMeasureMapping(DataTable dataTable, List returnListOfErrors){
       boolean valid = true;

       List <DataVariableMapping> variableMappings = new ArrayList();
       List<DataVariable> dataVariables = (List<DataVariable>) dataTable.getDataVariables();

            for (DataVariable dataVariable: dataVariables){

                variableMappings = getMeasureMappings((List) dataVariable.getDataVariableMappings());

                if ( variableMappings.size() > 1){
                    returnListOfErrors.add(dataVariable);
                    for (DataVariableMapping dvm: variableMappings){
                        returnListOfErrors.add(dvm.getGroup());
                    }
                    
                    valid = false;
                }

        }
        return valid;
    }

    @Override
    public boolean validateAllGroupsAreMapped(DataTable dataTable, List returnListOfErrors){
       boolean valid = true;
       List<DataVariable> dataVariables = (List<DataVariable>) dataTable.getDataVariables();

       List<VarGrouping> varGroupings = dataTable.getVarGroupings();



       List <VarGroup> groupsMapped = new ArrayList();

       List variableMappings = new ArrayList();
            for (DataVariable dataVariable: dataVariables){
                variableMappings = getMeasureAndFilterMappings((List) dataVariable.getDataVariableMappings());
                if (!variableMappings.isEmpty()){
                    Iterator iteratorMap = variableMappings.iterator();

                    while (iteratorMap.hasNext()) {
                        DataVariableMapping dataVariableMapping = (DataVariableMapping) iteratorMap.next();
                        boolean xAxis = false;
                        if (dataVariableMapping.isX_axis()) xAxis = true;
                        if (!xAxis && dataVariableMapping.getVarGrouping() != null ){
                            groupsMapped.add(dataVariableMapping.getGroup());
                        }
                    }
                }

            }

       for (VarGrouping vgr: varGroupings){
           if (!vgr.getGroupingType().equals(GroupingType.SOURCE)){
               List <VarGroup> vgList = vgr.getVarGroups();
               for (VarGroup vgTest: vgList ){
                   boolean mapped = false;
                   for (VarGroup vgMapped: groupsMapped){
                       if (vgTest.getName().equals(vgMapped.getName())){
                           mapped = true;
                       }
                       }
                   if (!mapped){
                       returnListOfErrors.add(vgTest);
                       valid = false;
                   }
              }
           }
       }

        return valid;
    }

    @Override
    public boolean validateMoreThanZeroMeasureMapping(DataTable dataTable, List returnListOfErrors){
        boolean valid = true;
       int countMeasures = 0;
       boolean hasMappings = false;
       boolean xAxis = false;
        List variableMappings = new ArrayList();
        List<DataVariable> dataVariables = (List<DataVariable>) dataTable.getDataVariables();
        List <DataVariable>  errorVariables = new ArrayList();
            for (DataVariable dataVariable: dataVariables){


                xAxis = false;
                hasMappings = false;
                countMeasures = 0;
                variableMappings = getMeasureAndFilterMappings((List) dataVariable.getDataVariableMappings());
                if (!variableMappings.isEmpty()){
                    hasMappings = true;
                    Iterator iteratorMap = variableMappings.iterator();

                    while (iteratorMap.hasNext()) {
                        DataVariableMapping dataVariableMapping = (DataVariableMapping) iteratorMap.next();

                        if (dataVariableMapping.isX_axis()) xAxis = true;
                        if (!xAxis && dataVariableMapping.getVarGrouping() != null &&
                            dataVariableMapping.getVarGrouping().getGroupingType().equals(GroupingType.MEASURE)){
                            countMeasures++;
                        }
                    }
                }
                if (!xAxis && hasMappings && countMeasures == 0){
                    errorVariables.add(dataVariable);
                    valid = false;
                }
                else{
                    xAxis = false;
                    hasMappings = false;
                    countMeasures = 0;
                }


        }

        if (!errorVariables.isEmpty()){
            List <DataVariableMapping> allMappings = new ArrayList();

            if (!errorVariables.isEmpty()){
                for (DataVariable dv : errorVariables){
                    List <DataVariableMapping>  errorMappings = getFilterMappings((List) dv.getDataVariableMappings());
                    if (!errorMappings.isEmpty()){
                        for (DataVariableMapping dvm: errorMappings){
                            allMappings.add(dvm);
                        }
                    }
                }
            }
            List <VarGroup> distinctFilters = getDistinctGroups(allMappings);

               for ( VarGroup filter: distinctFilters){
                returnListOfErrors.add(filter);
                for (DataVariableMapping dvm: allMappings){
                    if (dvm.getGroup().getName().equals(filter.getName())){
                        returnListOfErrors.add(dvm.getDataVariable());
                    }
                }

            }

        }



        return valid;
    }


    private boolean validateSingleVariableMeasureGroup(DataTable dataTable, DataVariable dataVariable){

       int countMeasures = 0;
       boolean hasMappings = false;
       boolean xAxis = false;
       List variableMappings = new ArrayList();
       VarGroup varGroupChk = new VarGroup();
       xAxis = false;
       hasMappings = false;
       countMeasures = 0;
       variableMappings = getMeasureAndFilterMappings((List) dataVariable.getDataVariableMappings());

       if (!variableMappings.isEmpty()){
            hasMappings = true;
            Iterator iteratorMap = variableMappings.iterator();

            while (iteratorMap.hasNext()) {
                DataVariableMapping dataVariableMapping = (DataVariableMapping) iteratorMap.next();
                if (!xAxis && dataVariableMapping.getVarGrouping() != null &&
                    dataVariableMapping.getVarGrouping().getGroupingType().equals(GroupingType.MEASURE)){
                    varGroupChk = dataVariableMapping.getGroup();
                }
            }
        }
       List <DataVariableMapping> dvmTestList = this.getDataVariableMappingsFromDataTableGroup(dataTable, varGroupChk);
       if (dvmTestList.size() > 1){
           return false;
       }

        return true;
    }

    @Override
    public boolean validateXAxisMapping(DataTable dataTable, Long xAxisVariableId){

        List variableMappings = new ArrayList();
        List dataVariables = new ArrayList();

        dataVariables = dataTable.getDataVariables();
        if (!dataVariables.isEmpty())
        {
            Iterator iteratorV = dataVariables.iterator();
            while (iteratorV.hasNext()) {
                DataVariable dataVariable = (DataVariable) iteratorV.next();
                if (dataVariable.getId().equals(xAxisVariableId)){
                    variableMappings = (List) dataVariable.getDataVariableMappings();
                        if (variableMappings.size() != 1 ){
                            return false;
                        }
                }

            }
        }

        return true;
    }


    @Override
    public boolean validateAtLeastOneFilterMapping(DataTable dataTable, List returnListOfErrors){

       int countFilters = 0;
       boolean xAxis = false;
       boolean hasMappings = false;
       boolean retVal = true;

        List<DataVariable> dataVariables = (List<DataVariable>) dataTable.getDataVariables();
        List<DataVariable> errorVariables = new ArrayList();
        List<VarGroup> errorGroups = new ArrayList();
            for (DataVariable dataVariable: dataVariables){
                
                xAxis = false;
                hasMappings = false;
                countFilters = 0;
                List <DataVariableMapping> variableMappings = getMeasureAndFilterMappings ( (List<DataVariableMapping>) dataVariable.getDataVariableMappings());
                for (DataVariableMapping dataVariableMapping: variableMappings){
                    hasMappings = true;

                    if (dataVariableMapping.isX_axis()) xAxis = true;
                    if (!xAxis && dataVariableMapping.getVarGrouping().getGroupingType().equals(GroupingType.FILTER)){
                        countFilters++;
                    }

                }

                    if (hasMappings  && !xAxis && !validateSingleVariableMeasureGroup(dataTable, dataVariable)){
                        if (countFilters < 1){
                            errorVariables.add(dataVariable);
                            retVal = false;
                        }
                        else{
                            xAxis = false;
                            hasMappings = false;
                            countFilters = 0;
                        }
                    }

            }

            List <DataVariableMapping> allMappings = new ArrayList();
            
            if (!errorVariables.isEmpty()){
                for (DataVariable dv : errorVariables){
                    List <DataVariableMapping>  variableMappings = getMeasureMappings((List) dv.getDataVariableMappings());
                    if (!variableMappings.isEmpty()){
                        for (DataVariableMapping dvm: variableMappings){
                            allMappings.add(dvm);
                        }
                    }
                }
            }
            List <VarGroup> distinctMeasures = getDistinctGroups(allMappings);

            for ( VarGroup measure: distinctMeasures){
                int countMappings = 0;
                for (DataVariableMapping dvm: allMappings){
                    if (dvm.getGroup().getName().equals(measure.getName())){
                        countMappings++;
                    }
                }
                if (countMappings > 1){
                    returnListOfErrors.add(measure);
                    for (DataVariableMapping dvm: allMappings){
                        if (dvm.getGroup().getName().equals(measure.getName())){
                            returnListOfErrors.add(dvm.getDataVariable());
                        }
                    }
                }
                countMappings = 0;
            }
            if (!retVal && !errorVariables.isEmpty() && returnListOfErrors.isEmpty() ){
                for (DataVariable dv : errorVariables){
                    if (!returnListOfErrors.contains(dv)){
                        List <DataVariableMapping>  variableMappings = getMeasureMappings((List) dv.getDataVariableMappings());
                        if (!variableMappings.isEmpty()){
                            for (DataVariableMapping dvm: variableMappings){
                                returnListOfErrors.add(dvm.getGroup());
                                returnListOfErrors.add(dvm.getDataVariable());
                            }
                        }
                        
                    }

                }                                
            }
        return retVal;
    }

    /**
     * Test that for each variable, that there is  defined set of filters
     * such that the variable can be individually chosen in the Explore data page
     *
     * @return a list of variables that are not uniquely identified by measure & filters
     */

    public List getDuplicateMappings( DataTable datatable, List returnListOfErrors ) {
        List <DataVariable> duplicateVariables = new ArrayList();
        Set<ArrayList<String>> set = new HashSet();
        Set<String> setString = new HashSet();
        List<DataVariable> variables = datatable.getDataVariables();
        List<VarGrouping> varGroupings = datatable.getVarGroupings();
        if (variables == null){
            return duplicateVariables;
        }
        for (DataVariable var: variables) {
            if (!var.getDataVariableMappings().isEmpty()){
                ArrayList<String> groupMembership = getGroupMembership(var,varGroupings);
                if (groupMembership != null && groupMembership.size() >0) {
                    Set <ArrayList<String>> groupSet = new HashSet();
                    groupSet.add(groupMembership);
                    if (set.contains(groupMembership) && groupMembership.size() > 1 ) {
                        duplicateVariables.add(var);
                        
                    } else {
                        set.add(groupMembership);
                        for(String groupString: groupMembership){
                            setString.add(groupString);
                        }
                    }
                }
            }
        }
            List <DataVariableMapping> allMappings = new ArrayList();

            if (!duplicateVariables.isEmpty()){
                for (DataVariable dv : duplicateVariables){
                    List <DataVariableMapping>  variableMappings = getMeasureMappings((List) dv.getDataVariableMappings());
                    if (!variableMappings.isEmpty()){
                        for (DataVariableMapping dvm: variableMappings){
                            allMappings.add(dvm);
                        }
                    }
                }
            }
            List <VarGroup> distinctMeasures = getDistinctGroups(allMappings);
            for ( VarGroup measure: distinctMeasures){
                returnListOfErrors.add(measure);
                for (DataVariable var: variables) {
                    if (!var.getDataVariableMappings().isEmpty()){
                        List dvmList = (List)  var.getDataVariableMappings();
                        for (Object dvmIn :dvmList){
                            DataVariableMapping dvm = (DataVariableMapping) dvmIn;
                            
                            if (!dvm.isX_axis() && dvm.getGroup().getName().equals(measure.getName())){
                                returnListOfErrors.add(dvm.getDataVariable());
                            }
                        }
                    }
                }
            }
        
        return duplicateVariables;
    }
    /*
     * For each grouping, get the group that this var belongs to, if any
     */
    private ArrayList<String> getGroupMembership(DataVariable var, List<VarGrouping> groupings ) {
        int countgroups = 0;
        ArrayList<String> membership = new ArrayList<String>();
        for (VarGrouping grouping : groupings) {
            if (!grouping.getGroupingType().equals(GroupingType.SOURCE)  && grouping.getDataVariableMappings() != null ){
                for (DataVariableMapping dvm : grouping.getDataVariableMappings()) {
                    if (dvm.getDataVariable() !=null && dvm.getDataVariable().getId().equals(var.getId())) {
                        membership.add(dvm.getGroup().getName());
                        countgroups++;
                    }
                }
            }
        }
        if (countgroups > 1){
            return membership;
        } else {
            return null;
        }
        
    }

    private List getDistinctGroups( List <DataVariableMapping> dvmList) {
        List <VarGroup> returnList = new ArrayList();
        
        for (DataVariableMapping dvm: dvmList) {
            if (returnList.isEmpty()){
                returnList.add(dvm.getGroup());
            } else {
                boolean added = false;
                for (VarGroup test: returnList){
                    if (test.getName().equals(dvm.getGroup().getName())){
                        added = true;
                    }
                }
                if (!added){
                    returnList.add(dvm.getGroup());
                }
            }

        }
        
        return returnList;
    }


    @Override
    public List getGroupsFromGroupTypeId(Long groupTypeId) {

        String query = "SELECT g.varGroups FROM  VarGroupType g where g.id = " + groupTypeId + "  ORDER BY g.name";
        return (List) em.createQuery(query).getResultList();
    }

    

    @Override
    public List getGroupTypesFromGroupingId(Long groupingId) {
        String query = "SELECT g.varGroupTypes FROM  VarGrouping g where g.id = " + groupingId + "  ORDER BY g.name";
        return (List) em.createQuery(query).getResultList();
    }


    @Override
    public VarGroup getGroupFromId(Long groupId) {
        String query = "SELECT g FROM  VarGroup g where g.id = " + groupId + "  ORDER BY g.name";
        return (VarGroup) em.createQuery(query).getSingleResult();

    }

    @Override
    public DataVariable getXAxisVariable(Long dataTableId) {
        String query = "SELECT v FROM  DataVariableMapping m, DataVariable v where v.id = m.dataVariable.id " +
                " and m.x_axis = true " +
                " and m.dataTable.id = " + dataTableId + "  ORDER BY v.id";
        if (em.createQuery(query).getResultList().isEmpty()) {
            DataVariable dataVariable = new DataVariable();
            dataVariable.setId(new Long (0));
            return dataVariable;
        }

        return (DataVariable) em.createQuery(query).getSingleResult();
    }

    @Override
    public List getDataVariableMappingsFromGroupId(Long groupId) {
        if (groupId != null) {
            String query = "SELECT v FROM  DataVariableMapping m, DataVariable v where v.id = m.dataVariable.id " +
                " and m.varGroup.id = " + groupId + "  ORDER BY v.id";
            return (List) em.createQuery(query).getResultList();
        } else {
            return new ArrayList();
        }
    }

    @Override
    public List getGroupTypesFromGroupId(Long groupId) {
        String query = "SELECT g.groupTypes FROM  VarGroup g where g.id = " + groupId + "  ORDER BY g.name";
        return (List) em.createQuery(query).getResultList();
    }

    @Override
    public List getFilterGroupsFromMeasureId(Long measureId) {
        String query = "SELECT distinct g FROM  DataVariableMapping m, DataVariableMapping m2, VarGroup g where " +
                " m.varGroup.id = " + measureId + "  " +
                " and  m.dataVariable.id = m2.dataVariable.id " +
                " and m2.varGroup.id = g.id and g.id <> " + measureId + "  ORDER BY g.name";
        if (em.createQuery(query).getResultList() != null){
            List varGroupList = (List) em.createQuery(query).getResultList();

                    List <VarGroup> retList = getSortedGroupList(varGroupList);
                    Collections.sort(retList);
                    return retList;

        } else {
            return new ArrayList();
        }

    }
    
    private boolean checkSortByType(List  varGroupList){
        
        for (Object group:varGroupList){
            VarGroup varGroup = (VarGroup) group;
            if (varGroup.getGroupTypes().size() != 1) return false;
        }        
        return true;
    }
    
    private List <VarGroup> getSortedGroupList(List varGroupList){
        List <VarGroup> varGroupListOut = new ArrayList();
        for (Object group:varGroupList){
            VarGroup varGroup = (VarGroup) group;
            varGroupListOut.add(varGroup);            
        }
        return varGroupListOut;
    }

    @Override
    public List getFilterGroupingsFromMeasureId(Long measureId) {

        String query = "select  DISTINCT po from VarGrouping po, VarGroup g2 " +
                " where g2.varGrouping.id = po.id  " +
                "  AND g2.id in ( " +
                " select distinct m2.varGroup.id from DataVariableMapping m2 " +
                " where m2.dataVariable.id in " +
                "   ( " +
                "  SELECT DISTINCT M.dataVariable.id FROM  DataVariableMapping m, DataVariable v, VarGroup g, VarGrouping p " +
                " where v.id = m.dataVariable.id " +
                " and m.varGroup.id = " + measureId + "  " +
                " and g.varGrouping.id = p.id " +
                " and m.varGroup.id = g.id ) ) ";
        List checkList = (List) em.createQuery(query).getResultList();
        return (List) checkList;
    }

    @Override
    public List getFilterMeasureAssociationFromDataTableId(Long dataTableId) {

        String query = "select  DISTINCT po.id, g2.id from VarGrouping po, VarGroup g2 " +
                " where g2.varGrouping.id = po.id  " +
                "  AND g2.id in ( " +
                " select distinct m2.varGroup.id from DataVariableMapping m2 " +
                " where m2.dataVariable.id in " +
                "   ( " +
                "  SELECT DISTINCT M.dataVariable.id FROM  DataVariableMapping m, DataVariable v, VarGroup g, VarGrouping p " +
                " where v.id = m.dataVariable.id " +
                " and m.dataTable.id = " + dataTableId + "  " +
                " and g.varGrouping.id = p.id " +
                " and m.varGroup.id = g.id ) ) ";
        List checkList = (List) em.createQuery(query).getResultList();
        return (List) checkList;
    }


    public void removeCollectionElement(Collection coll, Object elem) {
        coll.remove(elem);
        em.remove(elem);
    }

    public void removeCollectionElement(List list,int index) {
        em.remove(list.get(index));
        list.remove(index);
    }

    public void removeCollectionElement(Iterator iter, Object elem) {
        iter.remove();
        em.remove(elem);
    }

    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveAll() {


    }
    
        
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveAndContinue() {


    }
    
    @Remove
    public void cancel() {

    }

    @Override
    public boolean validateAtLeastOneMeasureMapping(DataTable dataTable) {
       int countMeasures = 0;

        List variableMappings = new ArrayList();
        List dataVariables = new ArrayList();
        dataVariables = dataTable.getDataVariables();
        Iterator iteratorV = dataVariables.iterator();
            while (iteratorV.hasNext()) {
                DataVariable dataVariable = (DataVariable) iteratorV.next();
                variableMappings = (List) dataVariable.getDataVariableMappings();
                Iterator iterator = variableMappings.iterator();
                while (iterator.hasNext()) {

                    DataVariableMapping dataVariableMapping = (DataVariableMapping) iterator.next();
                    if (dataVariableMapping.getVarGrouping() != null && dataVariableMapping.getVarGrouping().getGroupingType().equals(GroupingType.MEASURE)){
                        countMeasures++;
                    }
                }
            }
            if (countMeasures < 1){
                return false;
            }

        return true;
    }



    @Override
    public List getDataVariableMappingsFromDataTableGroup(DataTable dataTable, VarGroup varGroup) {

        List <DataVariableMapping> variableMappings = new ArrayList();
        List <DataVariable> dataVariables = dataTable.getDataVariables();
        List <DataVariable> returnDataVariables = new ArrayList();


        for (DataVariable dataVariable : dataVariables ){
            variableMappings = (List) dataVariable.getDataVariableMappings();
            for (DataVariableMapping dataVariableMapping: variableMappings){

                if (!dataVariableMapping.isX_axis() &&
                        dataVariableMapping.getGroup().getName().equals(varGroup.getName())
                        && dataVariableMapping.getVarGrouping().getGroupingType().equals(varGroup.getGroupAssociation().getGroupingType()))
                {
                    returnDataVariables.add(dataVariable);
                }

            }

        }


        return returnDataVariables;
    }

    @Override
    public boolean checkForDuplicateEntries(VarGrouping varGrouping, String name, boolean group, Object testObject) {

        boolean hasDuplicates = false;

        List<String> set = new ArrayList();
        List checkList = new ArrayList();
        List editList = new ArrayList();

        if (group){
           checkList =  varGrouping.getVarGroups();

        } else {
           checkList = (List)  varGrouping.getVarGroupTypes();
        }

        for (Object obj: checkList) {
            if (group) {
                VarGroup varGroup = (VarGroup) obj;
                set.add(varGroup.getName());
                editList.add(varGroup);
            } else {
                VarGroupType varGroupType = (VarGroupType) obj;
                set.add(varGroupType.getName());
                editList.add(varGroupType);
            }
        }
        
        if (set.contains(name)) {
            hasDuplicates = true;
        }
        if (testObject!=null){
            if (group){
               VarGroup varGroupTest = (VarGroup) testObject;
               for(Object listObj: editList){
                   VarGroup varGroup = (VarGroup) listObj;

                   boolean checkForMatch =  (varGroup.getId() != null && varGroupTest.getId() != null ) && varGroup.getId().equals(varGroupTest.getId() ) ;
                   checkForMatch |= (varGroup.getId() == null && varGroupTest.getId() == null );
                   if (checkForMatch  && name.equals(varGroup.getName())){
                       hasDuplicates = false;
                   }
               }

            } else {

               VarGroupType  varGroupTypeTest = (VarGroupType) testObject;
               for(Object listObj: editList){
                   VarGroupType varGroupType = (VarGroupType) listObj;
                   boolean checkForMatch =  (varGroupType.getId() != null && varGroupTypeTest.getId() != null ) && varGroupType.getId().equals(varGroupTypeTest.getId() ) ;
                   checkForMatch |= (varGroupType.getId() == null && varGroupTypeTest.getId() == null );
                   if (checkForMatch  && name.equals(varGroupType.getName())){
                       hasDuplicates = false;
                   }
               }

            }


        }


        return hasDuplicates;
    }

    @Override
    public boolean checkForDuplicateGroupings(List filterGroupings, String name, Object testObject) {
    boolean hasDuplicates = false;

        List<String> set = new ArrayList();
        List editList = new ArrayList();

        for (Object filterGrouping: filterGroupings ){
            VarGrouping varGrouping = (VarGrouping) filterGrouping;
            set.add(varGrouping.getName());
            editList.add(varGrouping);
        }


        if (set.contains(name)) {
            hasDuplicates = true;
        }
        if (testObject!=null){

               VarGrouping varGroupingTest = (VarGrouping) testObject;
               for(Object listObj: editList){
                   VarGrouping varGrouping = (VarGrouping) listObj;

                   boolean checkForMatch =  (varGrouping.getId() != null && varGroupingTest.getId() != null ) && varGrouping.getId().equals(varGroupingTest.getId() ) ;
                   checkForMatch |= (varGrouping.getId() == null && varGroupingTest.getId() == null );
                   if (checkForMatch  && name.equals(varGrouping.getName())){
                       hasDuplicates = false;
                   }
               }

         }

        return hasDuplicates;
    }

    public boolean validateDisplayOptions(DataTable dataTable) {
        int countDisplay = 0;
        
        if(dataTable.getVisualizationDisplay().isShowDataTable()){
            countDisplay++;
        }
        
        if(dataTable.getVisualizationDisplay().isShowImageGraph()){
            countDisplay++;
        }
        
        if(dataTable.getVisualizationDisplay().isShowFlashGraph()){
            countDisplay++;
        }
                        
        if (countDisplay == 0 ){
            return false;
        } else {
            if (!dataTable.getVisualizationDisplay().isShowFlashGraph() && dataTable.getVisualizationDisplay().getDefaultDisplay() == 1 ) {
                return false;
            }
            if (!dataTable.getVisualizationDisplay().isShowImageGraph() && dataTable.getVisualizationDisplay().getDefaultDisplay() == 0 ) {
                return false;
            }
            if (!dataTable.getVisualizationDisplay().isShowDataTable() && dataTable.getVisualizationDisplay().getDefaultDisplay() == 2 ) {
                return false;
            }
            return true;            
        }
    }

    public boolean validateOneFilterPerGrouping(DataTable dataTable, List returnListOfErrors) {
       boolean valid = true;
       boolean xAxis = false;
        List variableMappings = new ArrayList();
        List<DataVariable> dataVariables = (List<DataVariable>) dataTable.getDataVariables();
        List<DataVariable>  errorVariables = new ArrayList();
        List<VarGroup> filterGroups = new ArrayList();
            for (DataVariable dataVariable: dataVariables){
                filterGroups.clear();
                xAxis = false;
                variableMappings = getFilterMappings((List) dataVariable.getDataVariableMappings());
                if (!variableMappings.isEmpty()){
                    Iterator iteratorMap = variableMappings.iterator();
                    while (iteratorMap.hasNext()) {
                        DataVariableMapping dataVariableMapping = (DataVariableMapping) iteratorMap.next();
                        if (!xAxis && dataVariableMapping.getVarGrouping() != null &&
                            dataVariableMapping.getVarGrouping().getGroupingType().equals(GroupingType.FILTER)){
                            filterGroups.add(dataVariableMapping.getGroup());
                        }
                    }
                }
                if (filterGroups.size() > 1){
                    int i = 0;
                    VarGrouping vgt = null;
                    for (VarGroup vg : filterGroups){
                        if (i==0){
                            vgt = vg.getGroupAssociation();
                        } else {
                            if (vgt.equals(vg.getGroupAssociation())){
                                    errorVariables.add(dataVariable);
                                    valid = false;
                            }
                        }
                        i++;
                    }

                }
        }

        if (!errorVariables.isEmpty()){
            List <DataVariableMapping> allMappings = new ArrayList();

            if (!errorVariables.isEmpty()){
                for (DataVariable dv : errorVariables){
                    List <DataVariableMapping>  errorMappings = getFilterMappings((List) dv.getDataVariableMappings());
                    if (!errorMappings.isEmpty()){
                        returnListOfErrors.add(dv);
                        for (DataVariableMapping dvm: errorMappings){
                            returnListOfErrors.add(dvm.getGroup());
                        }
                    }
                }
            }
        }



        return valid;
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean migrateVisualization(Long oldFileId, Long newFileId) {
        String       queryString ="";
        queryString  = "SELECT g FROM  VarGrouping g where g.dataTable.id = " + newFileId + "  ORDER BY g.id";
        System.out.print("Before: " + em.createQuery(queryString).getResultList());
        em.createNativeQuery("DROP TABLE IF EXISTS vargroupingjoin").executeUpdate();
        em.createNativeQuery("DROP TABLE IF EXISTS vargroupjoin").executeUpdate();
        em.createNativeQuery("DROP TABLE IF EXISTS vargrouptypejoin").executeUpdate();

         queryString = "INSERT INTO vargrouping  "
                + "(name, groupingtype, datatable_id) "
                + " SELECT name, groupingtype, " + newFileId
                + " FROM vargrouping "
                + " WHERE datatable_id = " + oldFileId;

        Query query = em.createNativeQuery(queryString);
        query.executeUpdate();
            
        
        queryString = "SELECT vg1.id as id1, vg2.id as id2, vg1.name  "
                + " into varGroupingJoin "
                + " FROM varGrouping vg1, varGrouping vg2 " 
                + " where vg1.datatable_id = " + oldFileId
                + " and vg2.datatable_id = " + newFileId
                + " and vg1.name = vg2.name";

        query = em.createNativeQuery(queryString);
        query.executeUpdate();
        
        
        queryString = "INSERT INTO vargroup (name, units, varGrouping_id)  "
                + " select vgr.name, vgr.units, vj.id2 " 
                + " from varGroup vgr, varGroupingjoin vj " 
                + " where vj.id1 = vgr.vargrouping_id ";
            
        query = em.createNativeQuery(queryString);
        query.executeUpdate();
        
        queryString = "SELECT vg1.id as id1, vg2.id as id2, vg1.name "
                + " into varGroupJoin " 
                + " FROM varGroup vg1, varGroup vg2, varGroupingjoin vgj " 
                + " where vg1.vargrouping_id = vgj.id1 and vg2.vargrouping_id = vgj.id2  and vg1.name = vg2.name ";
            
        query = em.createNativeQuery(queryString);
        query.executeUpdate();
        
        queryString = "INSERT INTO vargroupType (name, varGrouping_id) "
                + " select vgr.name, vj.id2 " 
                + " from varGroupType vgr, varGroupingjoin vj " 
                + " where vj.id1 = vgr.vargrouping_id ";
            
        query = em.createNativeQuery(queryString);
        query.executeUpdate();
        
        queryString = " SELECT vg1.id as id1, vg2.id as id2, vg1.name "
                + " into varGroupTypeJoin" 
                + " FROM varGroupType vg1, varGroupType vg2, varGroupingjoin vgj " 
                + " where vg1.vargrouping_id = vgj.id1 and vg2.vargrouping_id = vgj.id2 and vg1.name = vg2.name ";
            
        query = em.createNativeQuery(queryString);
        query.executeUpdate();

        queryString = " INSERT INTO group_groupTypes (group_id, group_type_id) "
                + " select   vgj.id2, vtj.id2 " 
                + " from varGroupTypeJoin vtj, varGroupjoin vgj, group_groupTypes ggt " 
                + " where ggt.group_id in ( select id from varGroup where vargrouping_id in (select id from vargrouping "
                + " where datatable_id =  " +  oldFileId + ")) "
                + " and group_type_id in ( select id from varGrouptype where vargrouping_id in (select id from vargrouping " 
                + " where datatable_id =  "  +  oldFileId + ")) "
                + " and vtj.id1 = ggt.group_type_id  and vgj.id1 = ggt.group_id " ;      
        query = em.createNativeQuery(queryString);
        query.executeUpdate();
        
        queryString = "  INSERT INTO datavariablemapping (label, x_axis, datavariable_id, vargrouping_id, vargroup_id, datatable_id) "
                + " select dm.label, dm.x_axis, dv2.id, gpj.id2, gj.id2, " + newFileId 
                + " from datavariablemapping dm, datavariable dv1,  datavariable dv2,  varGroupJoin gj, varGroupingJoin gpj" 
                + " where dm.datatable_id = " +  oldFileId 
                + " and dm.datavariable_id = dv1.id "
                + " and dv1.name = dv2.name and dv2.datatable_id = "  + newFileId 
                + " and gj.id1 = dm.vargroup_id and gpj.id1 = dm.vargrouping_id " ;     
        query = em.createNativeQuery(queryString);
        query.executeUpdate();
        
        queryString = "  INSERT INTO datavariablemapping (label, x_axis, datavariable_id,  datatable_id) "
                + " select dm.label, dm.x_axis, dv2.id, " + newFileId 
                + " from datavariablemapping dm, datavariable dv1,  datavariable dv2 " 
                + " where dm.datatable_id = " +  oldFileId 
                + " and dm.datavariable_id = dv1.id "
                + " and dv1.name = dv2.name and dv2.datatable_id = "  + newFileId 
                + " and dm.x_axis = true " ;     
        query = em.createNativeQuery(queryString);
        query.executeUpdate();       
        
        queryString = "  INSERT INTO visualizationdisplay( showdatatable, defaultdisplay, showimagegraph, sourceinfolabel, measuretypelabel, showflashgraph, datatable_id)"
                + " SELECT showdatatable, defaultdisplay, showimagegraph, sourceinfolabel,  measuretypelabel, showflashgraph, " + newFileId 
                + "   FROM visualizationdisplay " 
                + " where datatable_id = " +  oldFileId;    
        query = em.createNativeQuery(queryString);
        query.executeUpdate();        
        
        queryString = "SELECT g FROM  VarGrouping g where g.dataTable.id = " + newFileId + "  ORDER BY g.id";
        System.out.print("after: " + em.createQuery(queryString).getResultList());
        saveAndContinue();
        setDataTableFromStudyFileId(newFileId);
        return true;
    }
}
