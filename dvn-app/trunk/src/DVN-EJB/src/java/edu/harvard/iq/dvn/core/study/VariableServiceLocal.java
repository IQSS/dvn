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
package edu.harvard.iq.dvn.core.study;

import java.util.List;
import java.util.Map;
import javax.ejb.Local;


/**
 * This is the business interface for VariableService enterprise bean.
 */
@Local
public interface VariableServiceLocal extends java.io.Serializable {
    edu.harvard.iq.dvn.core.study.SummaryStatisticType findSummaryStatisticTypeByName(String ssName);

    edu.harvard.iq.dvn.core.study.VariableFormatType findVariableFormatTypeByName(String name);

    edu.harvard.iq.dvn.core.study.DataTable getDataTable(Long dtId);

    edu.harvard.iq.dvn.core.study.VariableIntervalType findVariableIntervalTypeByName(String name);

    edu.harvard.iq.dvn.core.study.DataVariable getDataVariable(Long dvId);

    void determineStudiesFromVariables(List variables, List studies, Map variableMap);

    edu.harvard.iq.dvn.core.study.VariableRangeType findVariableRangeTypeByName(String name);

    java.util.List getDataVariablesByFileOrder(Long dtId);
    
    List<SummaryStatisticType> findAllSummaryStatisticType();
    SummaryStatisticType findSummaryStatisticTypeByName(List<SummaryStatisticType> typeList, String name);
    
    List<VariableFormatType> findAllVariableFormatType();
    VariableFormatType findVariableFormatTypeByName(List<VariableFormatType> typeList, String name);

    
    List<VariableIntervalType> findAllVariableIntervalType();
    VariableIntervalType findVariableIntervalTypeByName(List<VariableIntervalType> typeList, String name);

    List<VariableRangeType> findAllVariableRangeType();
    VariableRangeType findVariableRangeTypeByName(List<VariableRangeType> typeList, String name);
    
}
