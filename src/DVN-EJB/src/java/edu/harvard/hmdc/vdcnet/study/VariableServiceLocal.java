
package edu.harvard.hmdc.vdcnet.study;

import java.util.List;
import java.util.Map;
import javax.ejb.Local;


/**
 * This is the business interface for VariableService enterprise bean.
 */
@Local
public interface VariableServiceLocal {
    edu.harvard.hmdc.vdcnet.study.SummaryStatisticType findSummaryStatisticTypeByName(String ssName);

    edu.harvard.hmdc.vdcnet.study.VariableFormatType findVariableFormatTypeByName(String name);

    edu.harvard.hmdc.vdcnet.study.DataTable getDataTable(Long dtId);

    edu.harvard.hmdc.vdcnet.study.VariableIntervalType findVariableIntervalTypeByName(String name);

    edu.harvard.hmdc.vdcnet.study.DataVariable getDataVariable(Long dvId);

    void determineStudiesFromVariables(List variables, List studies, Map variableMap);

    edu.harvard.hmdc.vdcnet.study.VariableRangeType findVariableRangeTypeByName(String name);

    java.util.List getDataVariablesByFileOrder(Long dtId);
    
}
