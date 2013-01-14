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
