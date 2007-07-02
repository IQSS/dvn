/*
 * VariableDataBean.java
 *
 * Created on November 13, 2006, 9:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.subsetting;


/**
 *
 * @author Administrator
 */
public class VariableDataBean {
    
      private String variableId;
      private String variableName;
      private String variableLabel;
      private String variableType;
      private String variableFormat;
      private String attIntrval;
      private String attDcml;
      
      
      private String validCases;
      private String invalidCases;
      private String UNFvalue;
      private String minValue;
      private String maxValue;
      private String meanValue;
      private String medianValue;
      private String modeValue;
      private String stdDevValue;
      
      
      /*
      
      // Discrete Var Case
      
        String valid,
        String invalid,
        String UNF,
      // continuous var case
        String valid,
        String invalid,
        String UNF,
        String min,
        String max,
        String mean,
        String median,
        String mode,
        String stdDev
       */
    /**
     * Creates a new instance of VariableDataBean
     */
    public VariableDataBean() {
    }
    public VariableDataBean(
        String id,
        String name,
        String label,
        String type,
        String format,
        String intrval,
        String dcml){
        this.setVariableId(id);
        this.setVariableName(name);
        this.setVariableLabel(label);
        this.setVariableType(type);
        this.setVariableFormat(format);
        this.setAttIntrval(intrval);
        this.setAttDcml(dcml);
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableLabel() {
        return variableLabel;
    }

    public void setVariableLabel(String variableLabel) {
        this.variableLabel = variableLabel;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    public String getVariableFormat() {
        return variableFormat;
    }

    public void setVariableFormat(String variableFormat) {
        this.variableFormat = variableFormat;
    }

    public String getAttIntrval() {
        return attIntrval;
    }

    public void setAttIntrval(String attIntrval) {
        this.attIntrval = attIntrval;
    }

    public String getAttDcml() {
        return attDcml;
    }

    public void setAttDcml(String attDcml) {
        this.attDcml = attDcml;
    }

    public String getValidCases() {
        return validCases;
    }

    public void setValidCases(String validCases) {
        this.validCases = validCases;
    }

    public String getInvalidCases() {
        return invalidCases;
    }

    public void setInvalidCases(String invalidCases) {
        this.invalidCases = invalidCases;
    }

    public String getUNFvalue() {
        return UNFvalue;
    }

    public void setUNFvalue(String UNFvalue) {
        this.UNFvalue = UNFvalue;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMeanValue() {
        return meanValue;
    }

    public void setMeanValue(String meanValue) {
        this.meanValue = meanValue;
    }

    public String getMedianValue() {
        return medianValue;
    }

    public void setMedianValue(String medianValue) {
        this.medianValue = medianValue;
    }

    public String getModeValue() {
        return modeValue;
    }

    public void setModeValue(String modeValue) {
        this.modeValue = modeValue;
    }

    public String getStdDevValue() {
        return stdDevValue;
    }

    public void setStdDevValue(String stdDevValue) {
        this.stdDevValue = stdDevValue;
    }
    
}
