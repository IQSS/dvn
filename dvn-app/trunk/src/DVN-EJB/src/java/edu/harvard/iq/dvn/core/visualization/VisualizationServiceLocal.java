/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.visualization;

import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.Study;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author skraffmiller
 */
public interface VisualizationServiceLocal extends java.io.Serializable {

    public List getGroupings(Long dataTableId);
    public boolean validateGroupings(Long dataTableId);
    public List getVariableMappings(Long dataTableId);
    public List getGroupsFromGroupTypeId(Long groupTypeId);

    public List getGroupTypesFromGroupingId(Long groupingId);
    public List<VarGroupType> getGroupTypes(Long dataTableId);
    public List<VarGroupType> getFilterGroupTypes(Long dataTableId);
    public VarGroup getGroupFromId(Long groupId);
    public DataVariable getXAxisVariable(Long dataTableId);
    public List getDataVariableMappingsFromGroupId(Long groupId);
    public List getDataVariableMappingsFromDataTableGroup(DataTable dataTable, VarGroup varGroup);

    public List getGroupTypesFromGroupId(Long groupId);

   
    public void saveAll();
    public List getFilterGroupsFromMeasureId(Long measureId);
    public List getFilterGroupingsFromMeasureId(Long measureId);

    public void removeCollectionElement(Collection coll, Object elem);

    public void removeCollectionElement(List list, int index);

    public void removeCollectionElement(Iterator iter, Object elem);

    public void setDataTable(Long dataTableId);
    public void setDataTableFromStudyFileId(Long studyFileId);


    public Study getStudyFromStudyFileId(Long studyFileId);
    public DataTable getDataTable();

    @javax.ejb.Remove
    public void cancel();

    public boolean validateAtLeastOneFilterMapping(DataTable dataTable, List returnListOfErrors);

    public boolean validateXAxisMapping(DataTable dataTable, Long xAxisVariableId);

    public boolean validateOneMeasureMapping(DataTable dataTable, List returnListOfErrors);
    
    public boolean validateOneFilterPerGrouping(DataTable dataTable, List returnListOfErrors);

    public boolean validateAtLeastOneMeasureMapping(DataTable dataTable);
    
    public boolean validateDisplayOptions(DataTable dataTable);

    public List getDuplicateMappings( DataTable datatable, List returnListOfErrors );

    public java.util.List getFilterMeasureAssociationFromDataTableId(Long dataTableId);


    public boolean validateMoreThanZeroMeasureMapping(DataTable dataTable, List returnListOfErrors);

    public boolean checkForDuplicateEntries(VarGrouping varGrouping, String name, boolean group, Object testObject);

    public boolean checkForDuplicateGroupings(List filterGroupings, String name, Object testObject);

    public boolean validateAllGroupsAreMapped(edu.harvard.iq.dvn.core.study.DataTable dataTable, java.util.List returnListOfErrors);

    public java.util.List getSourceMappings(java.util.List<edu.harvard.iq.dvn.core.visualization.DataVariableMapping> mappingList);

    @javax.ejb.TransactionAttribute(value = javax.ejb.TransactionAttributeType.REQUIRED)
    public void saveAndContinue();
}
