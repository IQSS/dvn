/*
 * Dataverse Network - A web application to distribute, share and
 * analyze quantitative data.
 * Copyright (C) 2009
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata;



import java.util.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.*;

/**
 *  A class to represents the metadata component of a statistical data file.
 * 
 * @author Akio Sone at UNC-Odum
 */
public class SDIOMetadata {

    /**
     * The name of the native metadata format for this object.
     */
    protected String nativeMetadataFormatName = null;

    /**
     * Returns the name of the native metadata format for this object.
     * 
     * @return the name of the native metadata format, or <code>null</code>.
     */
    public String getNativeMetadataFormatName() {
        return nativeMetadataFormatName;
    }

    /**
     * Key terms used in the mapping table of file information
     */
    public static String[] COMMON_FILE_INFORMATION_ITEMS= {
        "fileID", "varQnty", "caseQnty", "recPrCas", "charset","mimeType",
        "fileType ", "fileFormat", "fileUNF", "fileDate", "fileTime",
        "tabDelimitedDataFileLocation"};

    /** this field is available for Stata DTA format only */
    private static Map<String, Integer> variableTypeNumber =
            new LinkedHashMap<String, Integer>();

    static {
        variableTypeNumber.put("Byte",    1);
        variableTypeNumber.put("Integer", 1);
        variableTypeNumber.put("Long",    1);
        variableTypeNumber.put("Float",   2);
        variableTypeNumber.put("Double",  2);
        variableTypeNumber.put("String",  0);
    }

    /**
     * Constructs an empty <code>SDIOMetadata</code> object. 
     */
    public SDIOMetadata() {
    }

    /**
     * The name of the case-weighting variable of the given
     * statistical data file.
     */
    protected String caseWeightVariableName;

    /**
     * Return the name of the case-weighting variable.
     * @return the name of the case-weighting variable.
     */
    public String getCaseWeightVariableName() {
        return caseWeightVariableName;
    }

    /**
     * Sets the name of the case-weighting variable
     * 
     * @param caseWeightVariableName the name of the case-weighting variable.
     */
    public void setCaseWeightVariableName(String caseWeightVariableName) {
        this.caseWeightVariableName = caseWeightVariableName;
    }

    /**
     * A <code>String</code> array that holds the set of variable Names 
     * in the given statistical data file.
     */
    protected String[] variableName;

    /**
     * Gets the value of variableName
     *
     * @return the value of variableName
     */
    public String[] getVariableName() {
        return variableName;
    }

    /**
     * Sets the value of variableName
     *
     * @param variableName new value of variableName
     */
    public void setVariableName(String[] variableName) {
        this.variableName = variableName;
    }
    /**
     * A mapping table from a variable name to its variable label.
     */
    protected Map<String, String> variableLabel;

    /**
     * Gets the value of variableLabel
     *
     * @return the value of variableLabel
     */
    public Map<String, String> getVariableLabel() {
        return variableLabel;
    }

    /**
     * Sets the value of variableLabel
     *
     * @param variableLabel new value of variableLabel
     */
    public void setVariableLabel(Map<String, String> variableLabel) {
        this.variableLabel = variableLabel;
    }

    
    /** This field is available for Stata DTA file*/
    private String[] variableStorageType;

    /**
     * Gets the value of variableStorageType
     * 
     * @return the value of variableStorageType
     */
    public String[] getVariableStorageType() {
        return variableStorageType;
    }

    /**
     * Sets the value of variableStorageType
     * @param variableStorageType new value of variableStorageType
     */
    public void setVariableStorageType(String[] variableStorageType) {
        this.variableStorageType = variableStorageType;
    }
    
    /** minimal dichotomous (string v. numeric) classification used 
        by SPSS SAV and POR formats. Zero(0) is numeric and positive 
        integer is string
    */
    private int[] variableTypeMinimal;

    /**
     * Gets the value of variableTypeMinimal.
     * 
     * @return the value of variableTypeMinimal.
     */
    public int[] getVariableTypeMinimal() {
        return variableTypeMinimal;
    }

    /**
     * Sets the value of variableTypeMinimal.
     * @param variableTypeMinimal the value of variableTypeMinimal.
     */
    public void setVariableTypeMinimal(int[] variableTypeMinimal) {
        this.variableTypeMinimal = variableTypeMinimal;
    }
    
    
    /** 
     * An array of <code>VariableType</code>s of variables
     */
    protected VariableType[] variableType;

    /**
     * Get the value of variableType
     * This method provides unified type information based on format-specific
     * type information found in an ingested file
     *
     * @return the value of variableType
     */
    public VariableType[] getVariableType() {


        if ((variableStorageType != null) && (variableStorageType.length >0)){
            // Stata DTA case

        } else if ((variableTypeMinimal != null)&&( variableTypeMinimal.length>0)){
            // SPSS POR/SAV cases

        }
        return variableType;
    }

    /**
     * Tells whether or not each of the variables is string.
     * 
     * @return a <code>boolean</code> array, true if a variable is string.
     */
    public boolean[] isStringVariable(){
        boolean [] stringYes = new boolean[variableName.length];

        if ((variableStorageType != null) && (variableStorageType.length >0)){
            // Stata DTA case
            for (int i =0; i< variableName.length;i++){

                stringYes[i] = variableStorageType[i].equals("String") ? true : false;
            }
        } else if ((variableTypeMinimal != null)&&( variableTypeMinimal.length>0)){
            // SPSS POR/SAV cases
            for (int i=0;i<variableName.length;i++){
                stringYes[i] = variableTypeMinimal[i] > 0 ? true : false;
            }
        }
        return stringYes;
    }

    /**
     * Tells whether or not each of the variables is continuous.
     * 
     * @return a <code>boolean</code> array, true if a variable is continuous.
     */
    public boolean[] isContinuousVariable(){
        boolean[] continuousYes = new boolean[variableName.length];
        if ((variableStorageType != null) && (variableStorageType.length >0)){
            // Stata DTA case
            for (int i =0; i< variableName.length;i++){
                if ( variableTypeNumber.get(variableStorageType[i]) == 2  ){
                    if (valueLabelTable.containsKey(valueLabelMappingTable.get(variableName[i]))){
                        
                        continuousYes[i] = false;
                    } else {
                        continuousYes[i] = true;
                    }
                    
                } else {
                    continuousYes[i] = false;
                }
            }
        } else if (decimalVariables != null){
            // SPSS POR/SAV cases
            if ( decimalVariables.size()>0){
                for (int i=0;i<variableName.length;i++){
                    continuousYes[i] = decimalVariables.contains(i) ? true : false;
                }
            } else {
                Arrays.fill(continuousYes, false);
            }
        }
        return continuousYes;
    }

    /**
     * A set that contains the position number of a decimal variable.
     */
    protected Set<Integer> decimalVariables;

    /**
     * Gets the value of the decimalVariables field.
     * 
     * @return the value of decimalVariables field.
     */
    public Set<Integer> getDecimalVariables() {
        return decimalVariables;
    }

    /**
     * Sets the value of the decimalVariables field.
     * 
     * @param decimalVariables the new value of the decimalVariables field.
     */
    public void setDecimalVariables(Set<Integer> decimalVariables) {
        this.decimalVariables = decimalVariables;
    }



    /**
     * Sets the value of variableType
     *
     * @param variableType new value of variableType
     */
//    public void setVariableType(VariableType[] variableType) {
//        this.variableType = variableType;
//    }

    /**
     * A <code>List</code> object of each variable's format number.
     *
     */
    protected List<Integer> variableFormat;

    /**
     * Gets the value of variableFormat
     *
     * @return the value of variableFormat
     */
    public List<Integer> getVariableFormat() {
        return variableFormat;
    }

    /**
     * Sets the value of variableFormat
     *
     * @param variableFormat new value of variableFormat
     */
    public void setVariableFormat(List<Integer> variableFormat) {
        this.variableFormat = variableFormat;
    }
    /**
     * A mapping table from a variable name to its format name.
     */
    protected Map<String, String> variableFormatName;

    /**
     * Get the value of variableFormatName
     *
     * @return the value of variableFormatName
     */
    public Map<String, String> getVariableFormatName() {
        return variableFormatName;
    }

    /**
     * Set the value of variableFormatName
     *
     * @param variableFormatName new value of variableFormatName
     */
    public void setVariableFormatName(Map<String, String> variableFormatName) {
        this.variableFormatName = variableFormatName;
    }
    
    /**
     * A mapping table from a variable name to its format category.
     */
    protected Map<String, String> variableFormatCategory;

    /**
     * Returns the value of the variableFormatCategory field.
     * 
     * @return the value of the variableFormatCategory field
     */
    public Map<String, String> getVariableFormatCategory() {
        return variableFormatCategory;
    }

    /**
     * Sets the new value of the variableFormatCategory field.
     *
     * @param variableFormatCategory the new value of the variableFormatCategory field.
     */
    public void setVariableFormatCategory(Map<String, String> variableFormatCategory) {
        this.variableFormatCategory = variableFormatCategory;
    }



    /**
     * A <code>String</code> array that holds each variable's UNF value.
     */
    protected String[] variableUNF;

    /**
     * Returns the value of the variableUNF field.
     *
     * @return the value of the variableUNF field.
     */
    public String[] getVariableUNF() {
        return variableUNF;
    }

    /**
     * Sets the new value of the variableUNF field.
     * 
     * @param variableUNF the new value of the variableUNF field.
     */
    public void setVariableUNF(String[] variableUNF) {
        this.variableUNF = variableUNF;
    }
    

    /**
     * A mapping table that stores various file-related information.
     */
    protected Map<String, Object> fileInformation =
        new LinkedHashMap<String, Object>();


    /**
     * Gets the value of fileInformation
     *
     * @return the value of fileInformation
     */
    public Map<String, Object> getFileInformation(){
        return this.fileInformation;
    }
    
    /**
     * 
     */
    protected Map<String, Map<String, String>> valueLabelTable =
        new LinkedHashMap<String,Map<String, String>>();

    /**
     * Gets the value of valueLabelTable
     *
     * @return the value of valueLabelTable
     */
    public Map<String, Map<String, String>> getValueLabelTable(){
        return this.valueLabelTable;
    }

    /**
     * Sets the value of valueLabelTable
     * @param valueLabelTable
     */
    public  void setValueLabelTable(Map<String, Map<String, String>>
        valueLabelTable){
        this.valueLabelTable = valueLabelTable;
    }


    /**
     * A mapping table from a variable name to its <code>List</code> of missig values.
     */
    protected Map<String, List<String>> missingValueTable =
        new LinkedHashMap<String, List<String>>();

    /**
     * Returns the value of the missingValueTable field.
     * 
     * @return the value of the missingValueTable field.
     */
    public Map<String, List<String>> getMissingValueTable() {
        return missingValueTable;
    }

    /**
     * Sets the new value of the missingValueTable field.
     * @param missingValueTable the new value of the missingValueTable field.
     */
    public void setMissingValueTable(Map<String, List<String>> missingValueTable) {
        this.missingValueTable = missingValueTable;
    }

    /**
     * A mapping table from a variable name to its InvalidData object.
     */
    protected  Map<String, InvalidData> invalidDataTable;

    /**
     * Returns the value of the invalidDataTable field.
     * @return the value of the invalidDataTable field.
     */
    public Map<String, InvalidData> getInvalidDataTable() {
        return invalidDataTable;
    }

    /**
     * Sets the new value of the invalidDataTable field.
     * @param invalidDataTable the new value of the invalidDataTable field.
     */
    public void setInvalidDataTable(Map<String, InvalidData> invalidDataTable) {
        this.invalidDataTable = invalidDataTable;
    }


    /**
     * A mapping table from a variable position number to its
     * table of summary statistics.
     */
    protected Map<Integer, Object[]> summaryStatisticsTable =
        new LinkedHashMap<Integer, Object[]>();

    /**
     * Returns the value of the summaryStatisticsTable field.
     * 
     * @return the value of the summaryStatisticsTable field.
     */
    public Map<Integer, Object[]> getSummaryStatisticsTable() {
        return summaryStatisticsTable;
    }

    /**
     * Sets the new value of the summaryStatisticsTable field.
     * @param summaryStatisticsTable  the new value of 
     * the summaryStatisticsTable field.
     */
    public void setSummaryStatisticsTable(Map<Integer, Object[]>
        summaryStatisticsTable) {
        this.summaryStatisticsTable = summaryStatisticsTable;
    }


    /**
     * A mapping table from a variable name to its table of categorical statistics.
     */
    protected Map<String, Map<String, Integer>>categoryStatisticsTable =
            new LinkedHashMap<String, Map<String,Integer>>();

    /**
     * Returns the value of the categoryStatisticsTable field.
     * 
     * @return the value of the categoryStatisticsTable field.
     */
    public Map<String, Map<String, Integer>> getCategoryStatisticsTable() {
        return categoryStatisticsTable;
    }

    /**
     * Sets the new value of the categoryStatisticsTable field.
     *
     * @param categoryStatisticsTable the new value of the 
     * categoryStatisticsTable field.
     */
    public void setCategoryStatisticsTable(Map<String,
            Map<String, Integer>> categoryStatisticsTable) {
        this.categoryStatisticsTable = categoryStatisticsTable;
    }

    /**
     * A mapping table from a short variable name to its long one.
     */
    protected Map<String, String> shortToLongVarialbeNameTable =
            new LinkedHashMap<String, String>();

    /**
     * Returns the value of the shortToLongVarialbeNameTable field.
     *  
     * @return the value of the shortToLongVarialbeNameTable field.
     */
    public Map<String, String> getShortToLongVarialbeNameTable() {
        return shortToLongVarialbeNameTable;
    }

    /**
     * Sets the new value of the shortToLongVarialbeNameTable field.
     * 
     * @param shortToLongVarialbeNameTable the new value of 
     * the shortToLongVarialbeNameTable field.
     */
    public void setShortToLongVarialbeNameTable(Map<String, String> 
        shortToLongVarialbeNameTable) {
        this.shortToLongVarialbeNameTable = shortToLongVarialbeNameTable;
    }


    /**
     * A mapping table from variable name to its value-label-table id.
     */
    public Map<String, String> valueLabelMappingTable = new LinkedHashMap<String, String>();

    /**
     * Returns the value of the valueLabelMappingTable field.
     * @return the value of the valueLabelMappingTable field.
     */
    public Map<String, String> getValueLabelMappingTable() {
        return valueLabelMappingTable;
    }

    /**
     * Sets the new value of the valueLabelMappingTable field.
     * @param valueLabelMappingTable the new value of the valueLabelMappingTable field.
     */
    public void setValueLabelMappingTable(Map<String, String> valueLabelMappingTable) {
        this.valueLabelMappingTable = valueLabelMappingTable;
    }

    /**
     * Returns a string representation of this instance.
     * 
     * @return a string representing this instance.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE);
    }
}
