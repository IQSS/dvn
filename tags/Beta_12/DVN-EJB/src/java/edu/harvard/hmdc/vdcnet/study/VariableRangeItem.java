/*
 * VariableRange.java
 *
 * Created on October 11, 2006, 11:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VariableRangeItem {
    
    /** Creates a new instance of VariableRange */
    public VariableRangeItem() {
    }
    
       /**
     * Holds value of property id.
     */
   @SequenceGenerator(name="variablerange_gen", sequenceName="variablerange_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="variablerange_gen")     
    private Long id;

    /**
     * Holds value of property balue.
     */
    private BigDecimal value;

    /**
     * Getter for property beginValue.
     * @return Value of property beginValue.
     */
    public BigDecimal getValue() {
        return this.value;
    }

    /**
     * Setter for property beginValue.
     * @param beginValue New value of property beginValue.
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

  /**
     * Holds value of property dataVariable.
     */
    @ManyToOne
    private DataVariable dataVariable;

    /**
     * Getter for property dataVariable.
     * @return Value of property dataVariable.
     */
    public DataVariable getDataVariable() {
        return this.dataVariable;
    }

    /**
     * Setter for property dataVariable.
     * @param dataVariable New value of property dataVariable.
     */
    public void setDataVariable(DataVariable dataVariable) {
        this.dataVariable = dataVariable;
    }  

   public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VariableRangeItem)) {
            return false;
        }
        VariableRangeItem other = (VariableRangeItem)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
    
}
