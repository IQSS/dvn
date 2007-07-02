/*
 * WeightedVarRelationshipId.java
 *
 * Created on August 14, 2006, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

/**
 *
 * @author Ellen Kraffmiller
 */
public class WeightedVarRelationshipId {
    
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
    
}
