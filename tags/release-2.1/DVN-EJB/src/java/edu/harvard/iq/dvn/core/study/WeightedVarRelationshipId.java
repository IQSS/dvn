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

/*
 * WeightedVarRelationshipId.java
 *
 * Created on August 14, 2006, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import org.apache.commons.lang.builder.*;

/**
 *
 * @author Ellen Kraffmiller
 */
public class WeightedVarRelationshipId implements java.io.Serializable {
    
    /**
     * Creates a new instance of WeightedVarRelationshipId
     */
    public WeightedVarRelationshipId() {
    }

    /**
     * Holds value of property variableId.
     */
    private Long variableId;

    /**
     * Getter for property variableId.
     * @return Value of property variableId.
     */
    public Long getVariableId() {
        return this.variableId;
    }

    /**
     * Setter for property variableId.
     * @param variableId New value of property variableId.
     */
    public void setVariableId(Long variableId) {
        this.variableId = variableId;
    }

    /**
     * Holds value of property weightedVariableId.
     */
    private Long weightedVariableId;

    /**
     * Getter for property weightedVariableId.
     * @return Value of property weightedVariableId.
     */
    public Long getWeightedVariableId() {
        return this.weightedVariableId;
    }

    /**
     * Setter for property weightedVariableId.
     * @param weightedVariableId New value of property weightedVariableId.
     */
    public void setWeightedVariableId(Long weightedVariableId) {
        this.weightedVariableId = weightedVariableId;
    }

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this,
//            ToStringStyle.MULTI_LINE_STYLE);
//    }
}
