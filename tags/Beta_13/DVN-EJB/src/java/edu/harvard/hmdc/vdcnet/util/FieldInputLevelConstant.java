/*
 * FieldInputLevelConstant.java
 *
 * Created on September 26, 2006, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

/**
 *
 * @author Ellen Kraffmiller
 */
public final class FieldInputLevelConstant {
    private final static String required = "required";
    private final static String recommended = "recommended";
    private final static String optional = "optional";
    
    /** Creates a new instance of FieldInputLevelConstant */
    public  FieldInputLevelConstant() {
    }

    public final static String getRequired() {
        return required;
    }

    public  final static String getRecommended() {
        return recommended;
    }

    public  final static String getOptional() {
        return optional;
    }
    
}
