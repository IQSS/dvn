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
 * WeightedVarRelationship.java
 *
 * Created on August 14, 2006, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
@IdClass(WeightedVarRelationshipId.class)
public class WeightedVarRelationship  implements java.io.Serializable {
    @Column(name="variable_id", insertable=false, updatable=false)
    @Id private Long variableId;
    
    @Column(name="weighted_variable_id", insertable=false, updatable=false)
    @Id private Long weightedVariableId;
       
    /**
     * Creates a new instance of WeightedVarRelationship
     */
    public WeightedVarRelationship() {
    }

    /**
     * Holds value of property dataVariable.
     */
    @ManyToOne
    @JoinColumn (name="variable_id")
    private DataVariable dataVariable;

    /**
     * Getter for property vdcCollection.
     * @return Value of property vdcCollection.
     */
    public DataVariable getDataVariable() {
        return this.dataVariable;
    }

    /**
     * Setter for property vdcCollection.
     * @param vdcCollection New value of property vdcCollection.
     */
    public void setDataVariable(DataVariable dataVariable) {
        this.dataVariable = dataVariable;
    }

    /**
     * Holds value of property weightedVariable.
     */
    @ManyToOne
    @JoinColumn(name="weighted_variable_id")
    private DataVariable weightedVariable;

    /**
     * Getter for property subCollection.
     * @return Value of property subCollection.
     */
    public DataVariable getWeightedVariable() {
        return this.weightedVariable;
    }

    /**
     * Setter for property subCollection.
     * @param subCollection New value of property subCollection.
     */
    public void setWeightedVariable(DataVariable weightedVariable) {
        this.weightedVariable = weightedVariable;
    }
    
//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this,
//            ToStringStyle.MULTI_LINE_STYLE);
//    }
    
}
