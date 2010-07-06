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
