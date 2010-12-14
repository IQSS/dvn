/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.visualization;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.visualization.VarGrouping.GroupingType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

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
    public void updateGroupings(List<VarGrouping> groupings) {
        throw new UnsupportedOperationException("Not supported yet.");
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

    @Override
    public boolean validateVariableMappings(DataTable dataTable) {
        boolean valid = true;

        valid &= validateOneMeasureMapping(dataTable);
        valid &= validateAtLeastOneFilterMapping(dataTable);
        //valid &= validateUniqueVariableMappings(dataTable);
        valid &= validateXAxisMapping(dataTable);
        
        return valid;
    }

    private boolean validateUniqueVariableMappings(Long dataTableId){

        boolean valid = true;
        List mappingIds = new ArrayList();
        List testIds = new ArrayList();
        mappingIds = getUniqueMappedVariables(dataTableId);
        testIds = getUniqueMappedVariables(dataTableId);
        Iterator iterator = mappingIds.iterator();
        while (iterator.hasNext()) {

            Long dataVariableId = (Long) iterator.next();
            List thisVariablesMappings = getVariableMappingsById(dataVariableId);
            Iterator iteratorTest = testIds.iterator();
            while (iteratorTest.hasNext()) {
                Long testDataVariableId = (Long) iteratorTest.next();
                List testVariablesMappings = getVariableMappingsById(dataVariableId);
                if (!dataVariableId.equals(testDataVariableId)){
                    valid &= getAreVariableMappingsDifferent(thisVariablesMappings, testVariablesMappings  );
                }
            }

        }


        return valid;
    }

    public boolean validateOneMeasureMapping(DataTable dataTable){

       int countMeasures = 0;
       boolean hasMappings = false;
       boolean xAxis = false;
        List variableMappings = new ArrayList();
        List dataVariables = new ArrayList();
        dataVariables = dataTable.getDataVariables();
        if (!dataVariables.isEmpty())
        {
            Iterator iterator = dataVariables.iterator();
            while (iterator.hasNext()) {
                DataVariable dataVariable = (DataVariable) iterator.next();
                xAxis = false;
                hasMappings = false;
                countMeasures = 0;
                variableMappings = (List) dataVariable.getDataVariableMappings();
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
                if (!xAxis && hasMappings && countMeasures != 1){
                    return false;
                }
                else{
                    xAxis = false;
                    hasMappings = false;
                    countMeasures = 0;
                }

            }
        }



        return true;
    }

    public boolean validateXAxisMapping(DataTable dataTable){

        int countXAxis = 0;
        List variableMappings = new ArrayList();
        List dataVariables = new ArrayList();
        DataVariable dvVerify = new DataVariable();
        dataVariables = dataTable.getDataVariables();
        if (!dataVariables.isEmpty())
        {
            Iterator iteratorV = dataVariables.iterator();
            while (iteratorV.hasNext()) {
                DataVariable dataVariable = (DataVariable) iteratorV.next();

                variableMappings = (List) dataVariable.getDataVariableMappings();
                Iterator iterator = variableMappings.iterator();
                while (iterator.hasNext()) {
                    DataVariableMapping dataVariableMapping = (DataVariableMapping) iterator.next();


                    if (dataVariableMapping.isX_axis()){
                        countXAxis++;
                        dvVerify = dataVariableMapping.getDataVariable();
                    }
                }
                if (countXAxis != 1){
                    return false;
                }
            }
        }
        List variableMappingsXAxis = (List) dvVerify.getDataVariableMappings();

        if (variableMappingsXAxis.size() != 1 ){
            return false;
        }


        return true;
    }



    public boolean validateAtLeastOneFilterMapping(DataTable dataTable){

       int countFilters = 0;
       boolean xAxis = false;
       boolean hasMappings = false;
        Long oldId = new Long(0);
        List variableMappings = new ArrayList();
        List dataVariables = new ArrayList();
        dataVariables = dataTable.getDataVariables();
        Iterator iteratorV = dataVariables.iterator();
            while (iteratorV.hasNext()) {
                DataVariable dataVariable = (DataVariable) iteratorV.next();
                xAxis = false;
                hasMappings = false;
                countFilters = 0;
                variableMappings = (List) dataVariable.getDataVariableMappings();
                Iterator iterator = variableMappings.iterator();
                while (iterator.hasNext()) {
                    hasMappings = true;
                    DataVariableMapping dataVariableMapping = (DataVariableMapping) iterator.next();
                    Long newId = dataVariableMapping.getDataVariable().getId();

                    if (dataVariableMapping.isX_axis()) xAxis = true;
                    if (!xAxis && dataVariableMapping.getVarGrouping().getGroupingType().equals(GroupingType.FILTER)){
                        countFilters++;
                    }
                }
                    if (hasMappings  && !xAxis){
                        if (countFilters < 1){
                            return false;
                        }
                        else{
                            xAxis = false;
                            hasMappings = false;
                            countFilters = 0;
                        }
                    }

            }

        return true;
    }

    private List getUniqueMappedVariables(Long dataTableId){
        String query = "SELECT distinct m.dataVariable.id FROM  datavariablemapping m where m.datatable.id = " + dataTableId + "  ORDER BY m.dataVariable.id";
        return (List) em.createQuery(query).getResultList();

    }

    private List getVariableMappingsById(Long dataVariableId){
        String query = "SELECT  m FROM  datavariablemapping m where m.dataVariable.id = " + dataVariableId + "  ";
        return (List) em.createQuery(query).getResultList();

    }

    private boolean getAreVariableMappingsDifferent(List <DataVariableMapping> mapping1, List <DataVariableMapping> mapping2){

        if (mapping1.size() != mapping2.size()){
            return true;
        }

        if (!getMeasureGroupIdFromMapping(mapping1).equals(getMeasureGroupIdFromMapping(mapping2)) ){
            return true;
        }

        Iterator iterator1 = mapping1.iterator();
        Iterator iterator2 = mapping2.iterator();
        int countMatches = 0;
        int compareMappings = mapping1.size();
        while (iterator1.hasNext()){
             DataVariableMapping dataVariableMapping = (DataVariableMapping) iterator1.next();
             VarGroup group1 = (VarGroup) dataVariableMapping.getGroup();
             while (iterator2.hasNext()){
                DataVariableMapping dataVariableMapping2 = (DataVariableMapping) iterator2.next();
                VarGroup group2 = (VarGroup) dataVariableMapping2.getGroup();
                if (group1.equals(group2)){
                    countMatches++;
                }
             }
        }

        if (countMatches != compareMappings){
            return true;
        }

        return false;
    }

    private Long getMeasureGroupIdFromMapping(List <DataVariableMapping> mapping){
        Iterator iterator = mapping.iterator();
        while (iterator.hasNext() ){
            DataVariableMapping dataVariableMapping = (DataVariableMapping) iterator.next();

            if (dataVariableMapping.getVarGrouping().getGroupingType().equals(GroupingType.MEASURE)){
                return dataVariableMapping.getGroup().getId();
            }

        }
        return new Long(0);
    }

    @Override
    public List getGroupsFromGroupTypeId(Long groupTypeId) {

        String query = "SELECT g.varGroups FROM  VarGroupType g where g.id = " + groupTypeId + "  ORDER BY g.name";
        return (List) em.createQuery(query).getResultList();
    }

    @Override
    public List getGroupsFromGroupingId(Long groupingId) {
        String query = "SELECT g.varGroups FROM  VarGrouping g where g.id = " + groupingId + "  ORDER BY g.name";
        return (List) em.createQuery(query).getResultList();

    }

    @Override
    public List getGroupTypesFromGroupingId(Long groupingId) {
        String query = "SELECT g.varGroupTypes FROM  VarGrouping g where g.id = " + groupingId + "  ORDER BY g.name";
        return (List) em.createQuery(query).getResultList();
    }

    @Override
    public VarGrouping getGroupingFromId(Long groupingId) {
        String query = "SELECT g FROM  VarGrouping g where g.id = " + groupingId + "  ORDER BY g.name";
        return (VarGrouping) em.createQuery(query).getSingleResult();
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
        String query = "SELECT g FROM  DataVariableMapping m, DataVariable v, varGroup g where v.id = m.dataVariable.id " +
                " and m.varGroup.id = " + measureId + "  " +
                " and m.varGroup.id = g.id and g.units is null ORDER BY v.name";
        return (List) em.createQuery(query).getResultList();
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


    public void removeCollectionElement(Collection coll, Object elem) {
        coll.remove(elem);
        em.remove(elem);
    }

    public void removeCollectionElement(List list,int index) {
        System.out.println("index is "+index+", list size is "+list.size());
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

    @Remove
    public void cancel() {

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addGroupType() {
        em.flush();
    }

 
}
