/*
 * WeightedVarRelationship.java
 *
 * Created on August 14, 2006, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
@IdClass(WeightedVarRelationshipId.class)
public class WeightedVarRelationship {
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
    
   
        
    
}
