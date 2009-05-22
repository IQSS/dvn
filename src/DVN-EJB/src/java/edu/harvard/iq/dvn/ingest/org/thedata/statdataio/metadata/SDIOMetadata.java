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
import org.apache.commons.lang.builder.*;

/**
 *
 * @author Akio Sone
 */
public class SDIOMetadata {

    protected String nativeMetadataFormatName = null;

    public String getNativeMetadataFormatName() {
        return nativeMetadataFormatName;
    }

    public static String[] COMMON_FILE_INFORMATION_ITEMS= {
        "fileID", "varQnty", "caseQnty", "recPrCas", "charset","mimeType",
        "fileType ", "fileFormat", "fileUNF", "fileDate", "fileTime",
        "tabDelimitedDataFileLocation"};

    public static Map<String, Integer> variableTypeNumber =
            new LinkedHashMap<String, Integer>();

    static {
        variableTypeNumber.put("Byte",    1);
        variableTypeNumber.put("Integer", 1);
        variableTypeNumber.put("Long",    1);
        variableTypeNumber.put("Float",   2);
        variableTypeNumber.put("Double",  2);
        variableTypeNumber.put("String",  0);
    }

    public SDIOMetadata() {
    }

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
    protected String[] variableLabel;

    /**
     * Get the value of variableLabel
     *
     * @return the value of variableLabel
     */
    public String[] getVariableLabel() {
        return variableLabel;
    }

    /**
     * Set the value of variableLabel
     *
     * @param variableLabel new value of variableLabel
     */
    public void setVariableLabel(String[] variableLabel) {
        this.variableLabel = variableLabel;
    }
    protected VariableType[] variableType;

    /**
     * Get the value of variableType
     *
     * @return the value of variableType
     */
    public VariableType[] getVariableType() {
        return variableType;
    }

    /**
     * Set the value of variableType
     *
     * @param variableType new value of variableType
     */
    public void setVariableType(VariableType[] variableType) {
        this.variableType = variableType;
    }

    protected VariableFormat[] variableFormat;

    /**
     * Get the value of variableFormat
     *
     * @return the value of variableFormat
     */
    public VariableFormat[] getVariableFormat() {
        return variableFormat;
    }

    /**
     * Set the value of variableFormat
     *
     * @param variableFormat new value of variableFormat
     */
    public void setVariableFormat(VariableFormat[] variableFormat) {
        this.variableFormat = variableFormat;
    }
    protected VariableFormatName[] variableFormatName;

    /**
     * Get the value of variableFormatName
     *
     * @return the value of variableFormatName
     */
    public VariableFormatName[] getVariableFormatName() {
        return variableFormatName;
    }

    /**
     * Set the value of variableFormatName
     *
     * @param variableFormatName new value of variableFormatName
     */
    public void setVariableFormatName(VariableFormatName[] variableFormatName) {
        this.variableFormatName = variableFormatName;
    }

    protected String[] variableStorageType;

    public String[] getVariableStorageType() {
        return variableStorageType;
    }

    public void setVariableStorageType(String[] variableStorageType) {
        this.variableStorageType = variableStorageType;
    }

    

    protected String[] variableUNF;

    public String[] getVariableUNF() {
        return variableUNF;
    }

    public void setVariableUNF(String[] variableUNF) {
        this.variableUNF = variableUNF;
    }
    

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

    public Map<Integer, Object> summaryStatisticsTable =
        new LinkedHashMap<Integer, Object>();

    public Map<Integer, Object> getSummaryStatisticsTable() {
        return summaryStatisticsTable;
    }

    public void setSummaryStatisticsTable(Map<Integer, Object>
        summaryStatisticsTable) {
        this.summaryStatisticsTable = summaryStatisticsTable;
    }


    public String generateDDI(){
        StringBuilder sb = new StringBuilder();
        sb.append(generateDDISection1());
        //sb.append(generateDDISection2());
        sb.append(generateDDISection3());
        sb.append(generateDDISection4());
        return sb.toString();
    }

    private String generateDDISection1(){
        
        String defaultEncoding = "UTF-8";
        
        String fileEncoding = (String)getFileInformation().get("charset");

        String encoding_attr = !fileEncoding.equals("") ?
                       " encoding=\""+fileEncoding+"\"" :
                       " encoding=\""+defaultEncoding+"\"";

        StringBuilder sb =
            new StringBuilder( "<?xml version=\"1.0\"" + encoding_attr +"?>\n");
        sb.append("<codeBook xmlns=\"http://www.icpsr.umich.edu/DDI\">\n");
        return sb.toString();
    }

    private String generateDDISection2(){
        StringBuilder sb = new StringBuilder(
            "<docDscr/>\n"+
            "<stdyDscr>\n\t<citation>\n\t\t<titlStmt>\n"+
            "\t\t\t<titl/>\n\t\t\t<IDNo agency=\"\"/>\n\t\t</titlStmt>\n"+
            "\t</citation>\n</stdyDscr>\n");
        return sb.toString();
    }

    private String generateDDISection3(){
        String charset = (String)getFileInformation().get("charset");
        String nobs = getFileInformation().get("caseQnty").toString();
        String nvar = getFileInformation().get("varQnty").toString();
        String mimeType = (String)getFileInformation().get("mimeType");

        System.out.println("charset="+charset);
        System.out.println("nobs="+nobs);
        System.out.println("nvar="+nvar);
        System.out.println("mimeType="+mimeType);
        //String recPrCas = (String)getFileInformation().get("recPrCas");
        //String fileType = (String)getFileInformation().get("fileType");
        //String fileFormat = (String)getFileInformation().get("fileFormat");
        
        String recPrCasTag = "";
        String fileFormatTag ="";
        
        String fileUNF = (String)getFileInformation().get("fileUNF");
        System.out.println("fileUNF="+fileUNF);
        String fileNoteUNF = 
            "\t<notes subject=\"Universal Numeric Fingerprint\" level=\"file\" "+
            "source=\"archive\" type=\"VDC:UNF\">" + fileUNF + "</notes>\n";
        
        String fileNoteFileType =
            "\t<notes subject=\"original file format\" level=\"file\" "+
            "source=\"archive\" type=\"VDC:MIME\">" +  mimeType + "</notes>\n";

        StringBuilder sb = 
            new StringBuilder("<fileDscr ID=\"file1\" URI=\"\">\n" + "\t<fileTxt>\n"+
        "\t\t<dimensns>\n\t\t\t<caseQnty>"+nobs+"</caseQnty>\n"+
        "\t\t\t<varQnty>" + nvar +"</varQnty>\n"+
        recPrCasTag + "\t\t</dimensns>\n"+
        "\t\t<fileType charset=\""+ charset + "\">" + mimeType +"</fileType>\n"+
        fileFormatTag+"\t</fileTxt>\n"+
        fileNoteUNF+ fileNoteFileType+"</fileDscr>\n");

        return sb.toString();
    }
    private String generateDDISection4(){
        // String[] variableName
        // String[] variableLabel
        // 
       
        StringBuilder sb = new StringBuilder("<dataDscr>\n");
        for (int i=0; i<variableName.length;i++){
            // <var

            String intrvlType = variableTypeNumber.get(variableStorageType[i])
                    > 1 ? "contin": "discrete" ;
            String intrvlAttr = variableTypeNumber.get(variableStorageType[i])
                    > 0 ? "intrvl=\""+intrvlType + "\" " : "";

            sb.append("\t<var ID=\"v1." + i + "\" name=\"" +
                 StringEscapeUtils.escapeXml(variableName[i]) + "\" "+
                 intrvlAttr +">\n");
                 
            sb.append("\t\t<location fileid=\"file1\"/>\n");
            
            // label
            if ((variableLabel[i] != null) && (!variableLabel[i].equals(""))) {
                sb.append("\t\t<labl level=\"variable\">" +
                    StringEscapeUtils.escapeXml(variableLabel[i])+"</labl>\n");
            }
            // format
            String formatTye = variableTypeNumber.get(variableStorageType[i])
                    > 0 ? "numeric": "character";
            sb.append("\t\t<varFormat type=\""+formatTye+"\"/>\n");
            // note: UNF
            sb.append("\t\t<notes subject=\"Universal Numeric Fingerprint\" "+
                "level=\"variable\" source=\"archive\" type=\"VDC:UNF\">"+
                StringEscapeUtils.escapeXml(variableUNF[i])
                +"</notes>\n");
            // closing 
            sb.append("\t</var>\n");
        }
        sb.append("</dataDscr>\n");
        sb.append("</codeBook>\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE);
    }
}
