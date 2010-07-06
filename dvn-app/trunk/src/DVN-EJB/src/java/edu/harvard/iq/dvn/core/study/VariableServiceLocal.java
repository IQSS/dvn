/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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
