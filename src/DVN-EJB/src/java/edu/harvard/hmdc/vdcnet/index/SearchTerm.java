/*
 * SearchTerm.java
 *
 * Created on October 16, 2006, 3:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.index;

/**
 *
 * @author roberttreacy
 */
public class SearchTerm {
    
    /** Creates a new instance of SearchTerm */
    public SearchTerm() {
        //default operator for simple searches
        operator = "=";
    }

    private String fieldName;

    private String operator;

    private String value;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
