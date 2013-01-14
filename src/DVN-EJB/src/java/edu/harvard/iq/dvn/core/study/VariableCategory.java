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
 * VariableRange.java
 *
 * Created on October 11, 2006, 11:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.util.AlphaNumericComparator;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VariableCategory implements Comparable, java.io.Serializable {
    private static AlphaNumericComparator alphaNumericComparator = new AlphaNumericComparator();
    
    /** Creates a new instance of VariableRange */
    public VariableCategory() {
    }
    
       /**
     * Holds value of property id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Holds value of property balue.
     */
    @Column(columnDefinition = "TEXT")
    private String value;

    /**
     * Getter for property beginValue.
     * @return Value of property beginValue.
     */
    public String getValue() {
        return this.value;
    }

    // helper for html display  
    private transient List charList;

    public List getValueCharacterList() {
        if (charList == null) {
            charList = new ArrayList();
            for (int i=0; i < this.value.length(); i++) {
                if (this.value.charAt(i) == ' ') {
                    charList.add( "&nbsp;" );
                } else {
                    charList.add( this.value.charAt(i) );
                }
            }
        }
        return charList;
    }

    /**
     * Setter for property beginValue.
     * @param beginValue New value of property beginValue.
     */
    public void setValue(String value) {
        this.value = value;
        charList = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Holds value of property label.
     */
    private String label;

    /**
     * Getter for property label.
     * @return Value of property label.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Setter for property label.
     * @param label New value of property label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Holds value of property missing.
     */
    private boolean missing;

    /**
     * Getter for property missing.
     * @return Value of property missing.
     */
    public boolean isMissing() {
        return this.missing;
    }

    /**
     * Setter for property missing.
     * @param missing New value of property missing.
     */
    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    /**
     * Holds value of property dataVariable.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
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

    /**
     * Holds value of property frequency.
     */
    private Double frequency;


    /**
     * Getter for property frequency.
     * @return Value of property frequency.
     */
    public Double getFrequency() {
        return this.frequency;
    }

    /**
     * Setter for property frequency.
     * @param frequency New value of property frequency.
     */
    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }

   
 public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VariableCategory)) {
            return false;
        }
        VariableCategory other = (VariableCategory)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }             

 public int compareTo(Object obj) {
        VariableCategory ss = (VariableCategory)obj;     
        return alphaNumericComparator.compare(this.getValue(),ss.getValue());
        
    }

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this,
//            ToStringStyle.MULTI_LINE_STYLE);
//    }
 
}
