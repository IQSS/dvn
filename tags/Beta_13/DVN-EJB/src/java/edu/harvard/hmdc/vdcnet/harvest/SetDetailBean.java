/*
 * SetDetailBean.java
 *
 * Created on May 3, 2007, 3:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.harvest;

/**
 *
 * @author Ellen Kraffmiller
 */
public class SetDetailBean {
    
    /** Creates a new instance of SetDetailBean */
    public SetDetailBean() {
    }

    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Holds value of property spec.
     */
    private String spec;

    /**
     * Getter for property spec.
     * @return Value of property spec.
     */
    public String getSpec() {
        return this.spec;
    }

    /**
     * Setter for property spec.
     * @param spec New value of property spec.
     */
    public void setSpec(String spec) {
        this.spec = spec;
    }
    
}
