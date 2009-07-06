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
 *
 * @author Akio Sone at UNC-Odum
 */
public class SDIOMetadata {

    /**
     *
     */
    protected String nativeMetadataFormatName = null;

    /**
     *
     * @return
     */
    public String getNativeMetadataFormatName() {
        return nativeMetadataFormatName;
    }

    /**
     *
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
     *
     */
    public SDIOMetadata() {
    }

    /**
     *
     */
    protected String caseWeightVariableName;

    /**
     *
     * @return
     */
    public String getCaseWeightVariableName() {
        return caseWeightVariableName;
    }

    /**
     *
     * @param caseWeightVariableName
     */
    public void setCaseWeightVariableName(String caseWeightVariableName) {
        this.caseWeightVariableName = caseWeightVariableName;
    }

    /**
     *
     */
    protected String[] variableName;

    /**
     * Get the value of variableName
     *
     * @return the value of variableName
     */
    public String[] getVariableName() {
        return variableName;
    }

    /**
     * Set the value of variableName
     *
     * @param variableName new value of variableName
     */
    public void setVariableName(String[] variableName) {
        this.variableName = variableName;
    }
    /**
     *
     */
    protected Map<String, String> variableLabel;

    /**
     * Get the value of variableLabel
     *
     * @return the value of variableLabel
     */
    public Map<String, String> getVariableLabel() {
        return variableLabel;
    }

    /**
     * Set the value of variableLabel
     *
     * @param variableLabel new value of variableLabel
     */
    public void setVariableLabel(Map<String, String> variableLabel) {
        this.variableLabel = variableLabel;
    }

    
    /** This field is available for Stata DTA file*/
    private String[] variableStorageType;

    /**
     *
     * @return
     */
    public String[] getVariableStorageType() {
        return variableStorageType;
    }

    /**
     *
     * @param variableStorageType
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
     *
     * @return
     */
    public int[] getVariableTypeMinimal() {
        return variableTypeMinimal;
    }

    /**
     *
     * @param variableTypeMinimal
     */
    public void setVariableTypeMinimal(int[] variableTypeMinimal) {
        this.variableTypeMinimal = variableTypeMinimal;
    }
    
    


    /** */
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
     *
     * @return
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
     *
     * @return
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
     *
     */
    protected Set<Integer> decimalVariables;

    /**
     *
     * @return
     */
    public Set<Integer> getDecimalVariables() {
        return decimalVariables;
    }

    /**
     *
     * @param decimalVariables
     */
    public void setDecimalVariables(Set<Integer> decimalVariables) {
        this.decimalVariables = decimalVariables;
    }



    /**
     * Set the value of variableType
     *
     * @param variableType new value of variableType
     */
//    public void setVariableType(VariableType[] variableType) {
//        this.variableType = variableType;
//    }

    protected List<Integer> variableFormat;

    /**
     * Get the value of variableFormat
     *
     * @return the value of variableFormat
     */
    public List<Integer> getVariableFormat() {
        return variableFormat;
    }

    /**
     * Set the value of variableFormat
     *
     * @param variableFormat new value of variableFormat
     */
    public void setVariableFormat(List<Integer> variableFormat) {
        this.variableFormat = variableFormat;
    }
    /**
     *
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
     *
     */
    protected Map<String, String> variableFormatCategory;

    /**
     *
     * @return
     */
    public Map<String, String> getVariableFormatCategory() {
        return variableFormatCategory;
    }

    /**
     *
     * @param variableFormatCategory
     */
    public void setVariableFormatCategory(Map<String, String> variableFormatCategory) {
        this.variableFormatCategory = variableFormatCategory;
    }



    /**
     *
     */
    protected String[] variableUNF;

    /**
     *
     * @return
     */
    public String[] getVariableUNF() {
        return variableUNF;
    }

    /**
     *
     * @param variableUNF
     */
    public void setVariableUNF(String[] variableUNF) {
        this.variableUNF = variableUNF;
    }
    

    /**
     *
     */
    protected Map<String, Object> fileInformation =
        new LinkedHashMap<String, Object>();


    /**
     * Get the value of fileInformation
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
     * Get the value of valueLabelTable
     *
     * @return the value of valueLabelTable
     */
    public Map<String, Map<String, String>> getValueLabelTable(){
        return this.valueLabelTable;
    }

    /**
     * Set the value of valueLabelTable
     * @param valueLabelTable
     */
    public  void setValueLabelTable(Map<String, Map<String, String>>
        valueLabelTable){
        this.valueLabelTable = valueLabelTable;
    }


    /**
     *
     */
    protected Map<String, List<String>> missingValueTable =
        new LinkedHashMap<String, List<String>>();

    /**
     *
     * @return
     */
    public Map<String, List<String>> getMissingValueTable() {
        return missingValueTable;
    }

    /**
     *
     * @param missingValueTable
     */
    public void setMissingValueTable(Map<String, List<String>> missingValueTable) {
        this.missingValueTable = missingValueTable;
    }

    /**
     *
     */
    protected  Map<String, InvalidData> invalidDataTable;

    /**
     *
     * @return
     */
    public Map<String, InvalidData> getInvalidDataTable() {
        return invalidDataTable;
    }

    /**
     * 
     * @param invalidDataTable
     */
    public void setInvalidDataTable(Map<String, InvalidData> invalidDataTable) {
        this.invalidDataTable = invalidDataTable;
    }

    


    /**
     *
     */
    protected Map<Integer, Object[]> summaryStatisticsTable =
        new LinkedHashMap<Integer, Object[]>();

    /**
     *
     * @return
     */
    public Map<Integer, Object[]> getSummaryStatisticsTable() {
        return summaryStatisticsTable;
    }

    /**
     *
     * @param summaryStatisticsTable
     */
    public void setSummaryStatisticsTable(Map<Integer, Object[]>
        summaryStatisticsTable) {
        this.summaryStatisticsTable = summaryStatisticsTable;
    }


    /**
     * 
     */
    protected Map<String, Map<String, Integer>>categoryStatisticsTable =
            new LinkedHashMap<String, Map<String,Integer>>();

    /**
     *
     * @return
     */
    public Map<String, Map<String, Integer>> getCategoryStatisticsTable() {
        return categoryStatisticsTable;
    }

    /**
     *
     * @param categoryStatisticsTable
     */
    public void setCategoryStatisticsTable(Map<String,
            Map<String, Integer>> categoryStatisticsTable) {
        this.categoryStatisticsTable = categoryStatisticsTable;
    }

    /**
     * 
     */
    protected Map<String, String> shortToLongVarialbeNameTable =
            new LinkedHashMap<String, String>();

    /**
     *
     * @return
     */
    public Map<String, String> getShortToLongVarialbeNameTable() {
        return shortToLongVarialbeNameTable;
    }

    /**
     *
     * @param shortToLongVarialbeNameTable
     */
    public void setShortToLongVarialbeNameTable(Map<String, String> shortToLongVarialbeNameTable) {
        this.shortToLongVarialbeNameTable = shortToLongVarialbeNameTable;
    }


    /**
     *
     */
    public Map<String, String> valueLabelMappingTable = new LinkedHashMap<String, String>();

    /**
     *
     * @return
     */
    public Map<String, String> getValueLabelMappingTable() {
        return valueLabelMappingTable;
    }

    /**
     *
     * @param valueLabelMappingTable
     */
    public void setValueLabelMappingTable(Map<String, String> valueLabelMappingTable) {
        this.valueLabelMappingTable = valueLabelMappingTable;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE);
    }
}
