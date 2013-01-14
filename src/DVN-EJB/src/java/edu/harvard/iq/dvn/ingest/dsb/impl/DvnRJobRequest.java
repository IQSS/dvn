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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.dsb.impl;

/**
 *
 * @author asone
 */
import edu.harvard.iq.dvn.ingest.dsb.*;
import edu.harvard.iq.dvn.core.study.*;
import static java.lang.System.*;
import java.util.*;
import java.util.logging.*;
import org.apache.commons.lang.*;


public class DvnRJobRequest {

    private static Logger dbgLog = Logger.getLogger(DvnRJobRequest.class.getPackage().getName());


    public static Map<String, Integer> xtabOutputOptions =
        new HashMap<String, Integer>();
        
    public static Map<String, Integer> zeligOutputOptions =
        new HashMap<String, Integer>();
    public static Map<String, Integer> zeligAnalysisOptions =
        new HashMap<String, Integer>();

    public static Map<String, String> rangeOpMap = new HashMap<String, String>();


    static {
        xtabOutputOptions.put("xtb_Totals",0);
        xtabOutputOptions.put("xtb_Statistics", 1);
        xtabOutputOptions.put("xtb_Percentages",2);
        xtabOutputOptions.put("xtb_ExtraTables",3);
        
        zeligOutputOptions.put("Summary",0);
        zeligOutputOptions.put("Plots", 1);
        zeligOutputOptions.put("BinOutput",2);
        
        rangeOpMap.put("1","=");
        rangeOpMap.put("2","!=");
        rangeOpMap.put("3",">=");
        rangeOpMap.put("4","<=");
        rangeOpMap.put("5",">");
        rangeOpMap.put("6","<");

        
    }
    // ----------------------------------------------------- Constructors

    /**
     * 4-arg Constructor for zelig cases
     * 
     */

    public DvnRJobRequest(List<DataVariable> dv, 
        Map<String, List<String>> listParams,
        Map<String, Map<String, String>> vts,
        Map<String, List<Object>> rs,
        AdvancedStatGUIdata.Model zp){
        

        dataVariablesForRequest = dv;
        
        listParametersForRequest = listParams;
        
        valueTables = vts;
        recodeSchema  = rs;
        zeligModelSpec = zp;
        dbgLog.fine("***** DvnRJobRequest: within the default constructor : initial *****");
        dbgLog.fine("DvnRJobRequest: variables="+dataVariablesForRequest);
        dbgLog.fine("DvnRJobRequest: map="+listParametersForRequest);
        dbgLog.fine("DvnRJobRequest: value table="+valueTables);
        dbgLog.fine("DvnRJobRequest: recodeSchema"+recodeSchema);
        dbgLog.fine("DvnRJobRequest: model spec="+zeligModelSpec);
        checkVariableNames();
        
        if (rs != null){
            if (rs.size() > 0){
                this.subsetRecodeConditions = generateSubsetRecodeConditions();
                for (int i= 0; i < getRecodedVarIdSet().length; i++){
                    recodedVarIdToName.put(getRecodedVarIdSet()[i],getRecodedVarNameSet()[i]);
                }
            }
        }
        dbgLog.fine("***** DvnRJobRequest: within the default constructor ends here *****");
    }

    
    /**
     * 3-arg Constructor for non-zelig cases
     *
     */
    public DvnRJobRequest(List<DataVariable> dv, 
        Map<String, List<String>> listParams, 
        Map<String, Map<String, String>> vts,
        Map<String, List<Object>> rs){
        this(dv,listParams,vts,rs, null);
        dbgLog.fine("***** DvnRJobRequest: within the 3-option constructor ends here *****");


    }

    /**
     * 2-arg Constructor for non-zelig, whole-file downloading cases
     *
     */
    public DvnRJobRequest(List<DataVariable> dv, 
        Map<String, List<String>> listParams, 
        Map<String, Map<String, String>> vts){

        this(dv,listParams, vts, null, null);
        dbgLog.fine("***** DvnRJobRequest: within the 2-option constructor ends here *****");

    }

    /**
     * Constructor for Graph/Network Rserve request
     *
     */

    public DvnRJobRequest(String RDataFile, 
        Map<String, Object> listParams){
        
        mapParametersForGraphSubset = listParams;
        savedRworkSpace = RDataFile; 

        dbgLog.fine("***** DvnRJobRequest: Network call constructor ends here *****");
    }



    // ----------------------------------------------------- fields
    
    public boolean IsOutcomeVarRecoded = false;
    
    /** metadata of requested variables */
    private List<DataVariable> dataVariablesForRequest;
    
    /** list-type (one-to-many) parameter */
    private Map<String, List<String>> listParametersForRequest;

    /** list-type (one-to-many) parameter */
    private Map<String, Object> mapParametersForGraphSubset; 


    /** R work space, saved and cached on the Application side **/ 
    private String savedRworkSpace; 

    /**  */
    
    private Map<String, Map<String, String>> valueTables;
    
    private Map<String, List<Object>> recodeSchema;
    /**  */
    private AdvancedStatGUIdata.Model zeligModelSpec;
    
    public Map<String, List<String>> rowSelectionData =  new LinkedHashMap<String, List<String>>();
    
    public Map<String, List<String>> subsetRecodeConditions;
    // ----------------------------------------------------- accessors

    /**
     * Getter for property dataVariablesForRequest
     *
     * @return    List<DataVariable>
     */
    public List<DataVariable> getDataVariablesForRequest(){
        return this.dataVariablesForRequest;
    }
    
    /**
     * Getter for property listParametersForRequest
     *
     * @return     
     */
     
    public Map<String, List<String>> getListParametersForRequest(){
        return this.listParametersForRequest;
    }

    /**
     * Getter for property mapParametersForGraphSubset
     *
     * @return     
     */
     
    public Map<String, Object> getParametersForGraphSubset(){
        return this.mapParametersForGraphSubset;
    }

    /**
     * Getter for property zeligModelSpec
     *
     * @return    
     */

    public AdvancedStatGUIdata.Model getZeligModelSpec(){
        return this.zeligModelSpec;
    }

    public List<String> getSubsetConditions(){
        return subsetRecodeConditions.get("subset");
    }
    
    public String getCachedRworkSpace(){
	return this.savedRworkSpace; 
    }

    public String getSubsetConditionsForCitation(){
        dbgLog.fine("subsetForCitation"+subsetRecodeConditions.get("subsetForCitation"));
        return  StringUtils.join(subsetRecodeConditions.get("subsetForCitation"), " & ") ;
    }
    
    public List<String> getRecodeConditions(){
        return subsetRecodeConditions.get("recode");
    }

    
    public Map<String, String> recodedVarIdToName = new HashMap<String, String>();

    // ----------------------------------------------------- accessors
    // metadata for RServe
    
    
    public String getStudytitle(){
        List<String> studytitle = listParametersForRequest.get("studytitle");
        dbgLog.fine("studytitle="+studytitle.get(0));
        String title = null;
        if (studytitle.get(0) != null) {
            title =  studytitle.get(0).replaceAll("\"", "'");
        } else {
            title = "[no study ]";
        }
        return title;
    }

    /**
     * Getter for property datafile path
     *
     * @param     
     * @return    
     */
    public String getSubsetFileName(){
        List<String> subsetFile = listParametersForRequest.get("subsetFileName");
        dbgLog.fine("subsetFileName="+subsetFile.get(0));
        return subsetFile.get(0);
    }

    /**
     * Getter for property datafile name
     *
     * @param     
     * @return    
     */
    public String getSubsetDataFileName(){
        List<String> subsetDataFile = listParametersForRequest.get("subsetDataFileName");
        dbgLog.fine("subsetDataFileName="+subsetDataFile.get(0));
        return subsetDataFile.get(0);
    }


    /**
     * Getter for property variable types
     *
     * @return    An arrary of variable types(0, 1, 2)
     */
    public int[] getVariableTypes() {
        
        List<Integer> rw = new ArrayList<Integer>();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
            if (!StringUtils.isEmpty(dv.getFormatCategory())){
                if (dv.getFormatCategory().toLowerCase().equals("date") ||
                    (dv.getFormatCategory().toLowerCase().equals("time"))){
                    rw.add(0);
                } else {
                    if (dv.getVariableFormatType().getId() == 1L) {
                        if (dv.getVariableIntervalType().getId() == null) {
                            rw.add(2);
                        } else {
                            if (dv.getVariableIntervalType().getId() == 2L) {
                                rw.add(2);
                            } else {
                                rw.add(1);
                            }
                        }
                    } else if (dv.getVariableFormatType().getId() == 2L) {
                        rw.add(0);
                    }
                }
            } else {
                if (dv.getVariableFormatType().getId() == 1L) {
                    if (dv.getVariableIntervalType().getId() == null) {
                        rw.add(2);
                    } else {
                        if (dv.getVariableIntervalType().getId() == 2L) {
                            rw.add(2);
                        } else {
                            rw.add(1);
                        }
                    }
                } else if (dv.getVariableFormatType().getId() == 2L) {
                    rw.add(0);
                }
            }
        }
        Integer[]tmp = (Integer[])rw.toArray(new Integer[rw.size()]);
        dbgLog.fine("vartype="+ StringUtils.join(tmp, ", "));
        int[] variableTypes=new int[tmp.length];
        for (int j=0;j<tmp.length;j++){
            variableTypes[j]= tmp[j];
        }
        return variableTypes;
    }

    public List<String> getVariableTypesAsString() {
        
        List<String> rw = new ArrayList<String>();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
            if (!StringUtils.isEmpty(dv.getFormatCategory())){
                if (dv.getFormatCategory().toLowerCase().equals("date") ||
                    dv.getFormatCategory().toLowerCase().equals("time")){
                    rw.add("0");
                } else {
                    if (dv.getVariableFormatType().getId() == 1L) {
                        if (dv.getVariableIntervalType().getId() == null) {
                            rw.add("2");
                        } else {
                            if (dv.getVariableIntervalType().getId() == 2L) {
                                rw.add("2");
                            } else {
                                rw.add("1");
                            }
                        }
                    } else if (dv.getVariableFormatType().getId() == 2L) {
                        rw.add("0");
                    }
                }
            } else {
                if (dv.getVariableFormatType().getId() == 1L) {
                    if (dv.getVariableIntervalType().getId() == null) {
                        rw.add("2");
                    } else {
                        if (dv.getVariableIntervalType().getId() == 2L) {
                            rw.add("2");
                        } else {
                            rw.add("1");
                        }
                    }
                } else if (dv.getVariableFormatType().getId() == 2L) {
                    rw.add("0");
                }
            }
        }
        return rw;
    }



    /**
     * Getter for property variable formats
     *
     * @return    A Map that maps a format to
     *            its corresponding type, either time or date
     */
    public Map<String, String> getVariableFormats() {
        Map<String, String> variableFormats=new LinkedHashMap<String, String>();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
            dbgLog.fine(i+"-th \tformatschema="+dv.getFormatSchema());
            dbgLog.fine(i+"-th \tformatcategory="+dv.getFormatCategory());
            if (!StringUtils.isEmpty(dv.getFormatCategory())) {
                if (dv.getFormatSchema().toLowerCase().equals("spss")){
                    if (dv.getFormatCategory().toLowerCase().equals("date")){
                        // add this var to this map value D
                        variableFormats.put(getSafeVariableName(dv.getName()), "D");
                    } else if (dv.getFormatCategory().toLowerCase().equals("time")){
                        // add this var to this map
                        if ( dv.getFormatSchemaName().toLowerCase().startsWith("dtime")){
                            // value JT
                            variableFormats.put(getSafeVariableName(dv.getName()), "JT");
                            
                        } else if ( dv.getFormatSchemaName().toLowerCase().startsWith("datetime")){
                            // value DT
                            variableFormats.put(getSafeVariableName(dv.getName()), "DT");
                        } else {
                            // value T
                            variableFormats.put(getSafeVariableName(dv.getName()), "T");
                        }
                    }
                } else if (dv.getFormatSchema().toLowerCase().equals("other")) {
                    if (dv.getFormatCategory().toLowerCase().equals("date")){
                        // value = D
                        variableFormats.put(getSafeVariableName(dv.getName()), "D");
                    }
                }
            } else {
                dbgLog.fine(i+"\t var: not date or time variable");
            }
        }
        dbgLog.fine("format="+variableFormats);
        return variableFormats;
    }
    
    private String getSafeVariableName(String raw){
        String safe =null;
        if ((raw2safeTable == null) || (raw2safeTable.isEmpty())) {
            // use raw
            dbgLog.fine("no unsafe variables");
            safe = raw;
        } else {
            // check this var is unsafe
            
            if (raw2safeTable.containsKey(raw)){
                dbgLog.fine("this var is unsafe="+raw);
                safe = raw2safeTable.get(raw);
                dbgLog.fine("safe var is:"+ safe);
            } else {
                dbgLog.fine("not on the unsafe list");
                safe = raw;
            }
        }
        return safe;
    }
    /**
     * Getter for property variable names
     *
     * @return    An array of variable names
     */
    public String[] getVariableNames() {
        String[] variableNames=null;
        
        List<String> rw = new ArrayList();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
                rw.add(dv.getName());
        }
        
        variableNames = (String[])rw.toArray(new String[rw.size()]);
        return variableNames;
    }
    
    
    public String[] safeVarNames = null;
    public String[] renamedVariableArray=null;
    public String[] renamedResultArray=null;
    public Map<String, String> raw2safeTable = null;
    
    public Map<String, String> safe2rawTable = null;

    public boolean hasUnsafedVariableNames = false;
    
    /**
     * Getter for property raw-to-safe-variable-name list
     * @return    A Map that maps an unsafe variable name to 
     *            a safe one
     */
    public Map<String, String> getRaw2SafeVarNameTable(){
        return raw2safeTable;
    }

    public void checkVariableNames(){
        
        VariableNameFilterForR nf = new VariableNameFilterForR(getVariableNames());
        if (nf.hasRenamedVariables()){
             safeVarNames  = nf.getFilteredVarNames();
             hasUnsafedVariableNames = true;
        }
        
        raw2safeTable = nf.getRaw2safeTable();
        safe2rawTable = nf.getSafe2rawTable();
        renamedVariableArray = nf.getRenamedVariableArray();
        renamedResultArray   = nf.getRenamedResultArray();
    }
    
    public List<String> getFileteredVarNameSet(List<String> varIdSet){
        List<String> varNameSet = new ArrayList<String>();
        for (String vid : varIdSet){
            dbgLog.fine("name list: vid="+vid);
            String raw = getVarIdToRawVarNameTable().get(vid);
            if (raw != null){
                dbgLog.fine("raw is not null case="+raw);
                if (raw2safeTable.containsKey(raw)){
                    dbgLog.fine("raw is unsafe case");
                    varNameSet.add(raw2safeTable.get(raw));
                } else {
                    dbgLog.fine("raw is safe case");
                    varNameSet.add(raw);
                }
            } else {
                dbgLog.fine("raw is null-case");
                if (hasRecodedVariables()){
                    dbgLog.fine("recode case="+ recodedVarIdToName.get(vid));
                    varNameSet.add(recodedVarIdToName.get(vid));
                } else {
                    dbgLog.fine("raw is null and not recode case");
                }
            }
        }
        dbgLog.fine("varNameSet="+varNameSet);
        return varNameSet;
    }
    
    /**
     * Getter for property variable ids
     * @return    A String array of variable Ids
     */
    public String[] getVariableIds(){
        String[] variableIds=null;
        List<String> rw = new ArrayList();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
                rw.add("v"+dv.getId().toString());
        }
        
        variableIds = (String[])rw.toArray(new String[rw.size()]);
        return variableIds;
    }

    public Map<String, String> getVarIdToRawVarNameTable(){
        Map<String, String> vi2rwn = new HashMap<String, String>();
        
        for(DataVariable dv :dataVariablesForRequest){
            vi2rwn.put("v"+dv.getId(), dv.getName());
        }
        return vi2rwn;
    }

    public Map<String, String> getRawVarNameToVarIdTable(){
        Map<String, String> rwn2Id = new HashMap<String, String>();
        
        for(DataVariable dv :dataVariablesForRequest){
            rwn2Id.put(dv.getName(), "v"+dv.getId());
        }
        return rwn2Id;
    }

    public String[] getUpdatedVariableNames(){
        List<String> tmp = new ArrayList<String>();
        if ((!hasUnsafedVariableNames) && (!hasRecodedVariables())){
            // neither renemaed nor recoded vars
            return  getVariableNames();
        } else if ( hasUnsafedVariableNames && !hasRecodedVariables()){
            // renamed vars only
            return safeVarNames;
        } else if (!hasUnsafedVariableNames && hasRecodedVariables()){
            // recoded vars only
            return (String[])ArrayUtils.addAll(getVariableNames(), getRecodedVarNameSet());
        } else {
            // both renamed and rcoded vars
            return (String[])ArrayUtils.addAll(safeVarNames, getRecodedVarNameSet());
        }
    
    }

    /**
     * Getter for property variable labels
     *
     * @return    A String array of variable labels
     */
    public String[] getVariableLabels(){
        String [] variableLabels=null;
        List<String> rw = new ArrayList();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
                rw.add(dv.getLabel());
        }
        
        variableLabels = (String[])rw.toArray(new String[rw.size()]);
        return variableLabels;
    }

    public String[] getUpdatedVariableLabels(){
        if (hasRecodedVariables()){
            return (String[])ArrayUtils.addAll(getVariableLabels(), getRecodedVarLabelSet());
        } else {
            return getVariableLabels();
        }
    }

    public String[] getUpdatedVariableIds(){
        if (hasRecodedVariables()){
            return (String[])ArrayUtils.addAll(getVariableIds(), getRecodedVarIdSet());
        } else {
            return getVariableIds();
        }
    }

    public int[] getUpdatedVariableTypes(){
        if (hasRecodedVariables()){
            return ArrayUtils.addAll(getVariableTypes(),getRecodedVarTypeSet());
        } else {
            return getVariableTypes();
        }
    }
    
    public List<String> getUpdatedVariableTypesAsString(){
        int[] vt;
        
        
        if (hasRecodedVariables()){
            vt =  ArrayUtils.addAll(getVariableTypes(),getRecodedVarTypeSet());
        } else {
            vt = getVariableTypes();
        }
        
        List<String> vts = new ArrayList<String>();
        
        for (int i = 0; i< vt.length; i++){
            vts.add(Integer.toString(vt[i]));
        }
        return vts;
    }
    /**
     * Getter for property value-label list
     *
     * @return    A value-label table as a Map object
     */
    public Map<String, Map<String,String>> getValueTable(){
        return valueTables;
    }
    

    /**
     * Getter for property missing value table
     *
     * @return    a missing-value table object as a Map object
     */


    /**
     * Returns the requeste type: downloading, or descriptive statistics,
     * cross-tabulation, or zelig models
     *
     * @return    a String (download|EDA|Xtab|Zelig)
     */
    public String getRequestType() {
        String type=null;
        List<String> requestTypeToken = listParametersForRequest.get("requestType");
        type =  requestTypeToken.get(0);
        dbgLog.fine("requestType="+type);
        return type;
    }

    /**
     * Returns the requested file format
     *
     * @return    a String (D01|D02|D03|D04)
     */
    public String getDownloadRequestParameter() {
        String param=null;
        List<String> requestTypeToken = listParametersForRequest.get("dtdwnld");
        param =  requestTypeToken.get(0);
        dbgLog.fine("dtdwnld="+param);
        return param;
    }

    public String getEDARequestParameter(){
        List<String> param = listParametersForRequest.get("analysis");
        String[] param3 = new String[3];
        param3[2] = "0";
        
        if (param.size() == 2){
            param3[0] = "1";
            param3[1] = "1";
        } else if (param.get(0).equals("A01")) {
            param3[0] = "1";
            param3[1] = "0";

        } else if (param.get(0).equals("A02")) {
            param3[0] = "0";
            param3[1] = "1";
        } else {
            param3[0] = "1";
            param3[1] = "1";
        }
        String tmp = StringUtils.join(param3, ", ");
        dbgLog.fine("aol="+tmp);
        return tmp;
    }

    public String getEDARequestType(){
        List<String> param = listParametersForRequest.get("analysis");
        int tmp = 0;
        if (param.size() == 2){
            tmp = 3;
        } else if (param.get(0).equals("A01")) {
            tmp = 1;
        } else if (param.get(0).equals("A02")) {
            tmp = 2;
        }

        dbgLog.fine("type="+tmp);
        return Integer.toString(tmp);
    }

    /**
     * Returns the requested model name
     *
     * @return    a String (xtb|zelig_models)
     */
    public String getZeligModelName() {
        String modelName = null;
        List<String> requestTypeToken = listParametersForRequest.get("modelName");
        modelName =  requestTypeToken.get(0);
        dbgLog.fine("modelName="+modelName);
        return modelName;
    }
    
    
    /**
     * 
     *
     * @return    
     */
    public String[] getXtabClassVars(){
        String[] cv = null;
        List<String> varIdSet = listParametersForRequest.get("xtb_nmBxR1");
        dbgLog.fine("class var Ids="+ varIdSet);
        if (varIdSet != null){

            List<String> varSet = getFileteredVarNameSet(varIdSet);
            dbgLog.fine("class-var non-null case:"+ varSet);
            cv = (String[])varSet.toArray(new String[varSet.size()]);
        }
        return cv;
    }

    /**
     * 
     *
     * @return    
     */
    public String[] getXtabFreqVars(){
        String[] fv = null;
        
        List<String> varIdSet = listParametersForRequest.get("xtb_nmBxR2");
        if (varIdSet != null){
            List<String> varSet = getFileteredVarNameSet(varIdSet);
            dbgLog.fine("freq-var non-null case:"+ varSet);

            fv = (String[])varSet.toArray(new String[varSet.size()]);
        }
        return fv;
    }
    
    /**
     * 
     *
     * @return    
     */
    public String[] getXtabOutputOptions(){
        String[] xoo = {"F", "F", "F", "F"};
        List<String> varSet = listParametersForRequest.get("xtb_outputOptions");
        if (varSet != null){
            for (int i=0;i<varSet.size();i++){
                if (xtabOutputOptions.containsKey(varSet.get(i))){
                    xoo[xtabOutputOptions.get(varSet.get(i))]="T";
                }
            }
        }
        return xoo;
    }
    
    public boolean isOutcomeBinary(){
        List<String> oc = listParametersForRequest.get("isOutcomeBinary");
        if (oc.get(0).equals("T")){
            return true;
        } 
        return false;
    }

    public int getOutcomeVarPosition(){
        int no = -1;
        List<String> varIdSet = listParametersForRequest.get("nmBxR1");
        if (varIdSet.size() == 1){
             List<String> vi = Arrays.asList(getVariableIds());
             
             if (vi.indexOf(varIdSet.get(0)) > -1){
                    no = vi.indexOf(varIdSet.get(0));
             } else {
                if (hasRecodedVariables()){
                    List<String> rvi = listParametersForRequest.get("recodedVarIdSet");
                    if (rvi.indexOf(varIdSet.get(0)) > -1){
                        no = rvi.indexOf(varIdSet.get(0));
                        IsOutcomeVarRecoded = true;
                    }
                }
            }
        }
        return no;
    }
    
    public String getLHSformula(){
        String lhs = null;
        List<String> varIdSet2 = null;
        List<String> varIdSet  = null;
        int noRboxes = zeligModelSpec.getNoRboxes();
        
        if (noRboxes >= 3) {

            // box stores Ids = "v" + ID-integer
            varIdSet = listParametersForRequest.get("nmBxR1");
            
            varIdSet2 = listParametersForRequest.get("nmBxR2");
            
            List<String> tmp = getFileteredVarNameSet(varIdSet);
            if (varIdSet2 != null){
                tmp.addAll(getFileteredVarNameSet(varIdSet2));
            }
           lhs = "list(" + StringUtils.join(tmp,",") + ")";
           dbgLog.fine("lhs(noRboxes>=3)="+lhs);
        } else {
            varIdSet = listParametersForRequest.get("nmBxR1");
            List<String> tmp = getFileteredVarNameSet(varIdSet);

            if (varIdSet.size() > 1){
                lhs = "list(" + StringUtils.join(tmp,",") + ")";
            } else {
                lhs = tmp.get(0);
            }
           dbgLog.fine("lhs(noRboxes<3)="+lhs); 
        }
        
        return lhs;
    }
    
    
    public String getLHSformula4rep(){
        String lhs = null;
        List<String> varIdSet2 = null;
        List<String> varIdSet  = null;
        int noRboxes = zeligModelSpec.getNoRboxes();
        
        if (noRboxes >= 3) {

            // box stores Ids = "v" + ID-integer
            varIdSet = listParametersForRequest.get("nmBxR1");
            varIdSet2 = listParametersForRequest.get("nmBxR2");

            List<String> tmp = getFileteredVarNameSet(varIdSet);
            if (varIdSet2 != null){
                tmp.addAll(getFileteredVarNameSet(varIdSet2));
            }
           lhs = "Surv(" + StringUtils.join(tmp,",") + ")";
           dbgLog.fine("lhs4rep(noRboxes>=3)="+lhs);
        } else {
            varIdSet = listParametersForRequest.get("nmBxR1");
            List<String> tmp = getFileteredVarNameSet(varIdSet);

            if (varIdSet.size() > 1){
                lhs = "cbind(" + StringUtils.join(tmp,",") + ")";
            } else {
                if ((getZeligModelName().equals("ologit")) || 
                    (getZeligModelName().equals("oprobit"))){
                    lhs = "as.factor("+ tmp.get(0) +")";
                } else {
                    lhs = tmp.get(0);
                }
            }
           dbgLog.fine("lhs4rep(noRboxes<3)="+lhs); 
        }
        return lhs;
    }
    
    
    public String getRHSformula(){
        String rhs = null;
        List<String> varIdSet = null;
        int noRboxes = zeligModelSpec.getNoRboxes();
        if (noRboxes >= 2) {
            if (noRboxes == 2) {
                varIdSet = listParametersForRequest.get("nmBxR2");
            } else if (noRboxes == 3){
                varIdSet = listParametersForRequest.get("nmBxR3");
            }
            List<String> tmp = getFileteredVarNameSet(varIdSet);
            rhs = StringUtils.join(tmp,"+");
        }else if (noRboxes == 1){
            rhs = "NULL";
        }
        return rhs;
    }
    
    
    public String[] getZeligOutputOptions(){
        String[] zoo = {"F", "F", "F"};
        List<String> varSet = listParametersForRequest.get("zelig_outputOptions");
        if (varSet != null){
            for (int i=0;i<varSet.size();i++){
                if (zeligOutputOptions.containsKey(varSet.get(i))){
                    zoo[zeligOutputOptions.get(varSet.get(i))]="T";
                }
            }
        }
        return zoo;
    }
    
    public String getZeligSimulationOption(){
        String simOptn = "F";
        List<String> valueSet  = listParametersForRequest.get("Sim");
        if (valueSet != null){
            simOptn =  valueSet.get(0);
        }
        return simOptn;
    }
    
    
    public String getZeligSetxType(){
        String type = null;
        List<String> valueSet  = listParametersForRequest.get("setxType");
        if (valueSet != null){
            type =  valueSet.get(0);
        }
        return type;
    }
    
    public String getSetx1stSet(){
        String setxArg = null;
        List<String> valueSet  = listParametersForRequest.get("setx_var1");
        List<String> v = new ArrayList();
        v.add(valueSet.get(0));
        List<String> tmp = getFileteredVarNameSet(v);
        if (!valueSet.get(1).equals("")){
            setxArg = "list(" + tmp.get(0) + " = " + valueSet.get(1) +")";
        }
        return setxArg;
    }
    
    
    public String getSetx2ndSet(){
        String setxArg = null;
        List<String> valueSet  = listParametersForRequest.get("setx_var2");
        List<String> v = new ArrayList();
        v.add(valueSet.get(0));
        List<String> tmp = getFileteredVarNameSet(v);
        if (!valueSet.get(1).equals("")){
            setxArg = "list(" + tmp.get(0) + " = " + valueSet.get(1) +")";
        }
        return setxArg;
    }
    
    public String getSetx1stSet4rep(){
        String setxArg = null;
        List<String> valueSet  = listParametersForRequest.get("setx_var1");
        List<String> v = new ArrayList();
        v.add(valueSet.get(0));
        List<String> tmp = getFileteredVarNameSet(v);
        if (!valueSet.get(1).equals("")){
            setxArg = tmp.get(0) + " = " + valueSet.get(1);
        }
        return setxArg;
    }
    
    
    public String getSetx2ndSet4rep(){
        String setxArg = null;
        List<String> valueSet  = listParametersForRequest.get("setx_var2");
        List<String> v = new ArrayList();
        v.add(valueSet.get(0));
        List<String> tmp = getFileteredVarNameSet(v);
        if (!valueSet.get(1).equals("")){
            setxArg = tmp.get(0) + " = " + valueSet.get(1);
        }
        return setxArg;
    }

    /**
     * Methods for Recoded variables
     *
     */
    
    public boolean hasRecodedVariables(){
        boolean rv = false;
        if ((recodeSchema != null) && (recodeSchema.size()> 0)){
            rv = true;
        }
        return rv;
    }
   
    public String[] getRecodedVarIdSet() {
        List<String> lvi = listParametersForRequest.get("recodedVarIdSet");
        String[] vi = (String[])lvi.toArray(new String[lvi.size()]);
        return vi;
    }
    
    public String[] getRecodedVarNameSet() {
        List<String> lvn = listParametersForRequest.get("recodedVarNameSet");
        String[] vn = (String[])lvn.toArray(new String[lvn.size()]);

        return vn;
    }
   
    public String[] getRecodedVarLabelSet() {
        List<String> lvl = listParametersForRequest.get("recodedVarLabelSet");
        String[] vl = (String[])lvl.toArray(new String[lvl.size()]);
        return vl;
    }

    public int[] getRecodedVarTypeSet() {
        List<String> lvt = listParametersForRequest.get("recodedVarTypeSet");
        String[] svt = (String[])lvt.toArray(new String[lvt.size()]);
        int[] vt = new int[lvt.size()];
        for (int i=0; i< svt.length; i++){
            vt[i] = Integer.parseInt(svt[i]);
        }
        return vt;
    }

    public String[] getBaseVarIdSet(){
        List<String> bvid = listParametersForRequest.get("baseVarIdSet");
        String[] tmp = (String[])bvid.toArray(new String[bvid.size()]);
        return tmp;

    }
    
    public String[] getBaseVarNameSet(){
        List<String> bvn = listParametersForRequest.get("baseVarNameSet");
        String[] tmp = (String[])bvn.toArray(new String[bvn.size()]);
        return tmp;
    }
   
    public Map<String, String> getVarIdToRecodedVarNameTable(){
        Map<String, String> tb = new LinkedHashMap<String, String>();
        List<String> lvi = listParametersForRequest.get("recodedVarIdSet");
        List<String> lvn = listParametersForRequest.get("recodedVarNameSet");

        for (int i = 0; i< lvi.size(); i++){
            tb.put(lvi.get(i), lvn.get(i));
        }
        return tb;
    }

    public boolean isThisIdFromRecodedVar(String id){
        boolean result = false;
        
        List<String> lvi = listParametersForRequest.get("recodedVarIdSet");
        
        if (lvi != null){
            if ((lvi).indexOf(id) > -1){
                result = true;
            }
        }
        return result;
    }
    
    public int[] getRecodedVarBaseTypeSet(){
        List<String> lvt = listParametersForRequest.get("recodedVarBaseTypeSet");
        String[] svt = (String[])lvt.toArray(new String[lvt.size()]);
        int[] vt = new int[lvt.size()];
        for (int i=0; i< svt.length; i++){
            vt[i] = Integer.parseInt(svt[i]);
        }
        return vt;
    }




    /**
     * 
     *
     * @return 
     */
    public Map<String, List<String>> generateSubsetRecodeConditions (){
        List<String> subsetConditions = new ArrayList<String>();
        List<String> subsetConditionsForCitation = new ArrayList<String>();
        List<String> recodeConditions = new ArrayList<String>();
        Map<String, List<String>> conditions = new HashMap<String, List<String>>();
        
        List<String> recodedVarIdSet = listParametersForRequest.get("recodedVarIdSet");
        
        List<String> recodedVarNameSet = listParametersForRequest.get("recodedVarNameSet");
        List<String> baseVarIdSet = listParametersForRequest.get("baseVarIdSet");
        List<String> baseVarNameSet = listParametersForRequest.get("baseVarNameSet");        
        int[] variableTypes = getRecodedVarBaseTypeSet();
        
        
        
        for (int j=0; j< recodedVarIdSet.size(); j++) {
            // get each recode table
//            List<List<Object>> rdtbl = (List<List<Object>>) recodeSchema.get(recodedVarIdSet.get(j));
            // recodeSchema is indexed by the raw variable Id (without "v")
            dbgLog.fine("old varId ="+ recodedVarIdSet.get(j));
            String rVarId = recodedVarIdSet.get(j).substring(1);
            dbgLog.fine("new varId to extract the recode table = "+rVarId);
            List<Object> rdtbl = (List<Object>)recodeSchema.get(rVarId);
            int rcnt=0;
            List<String> delpool = new ArrayList<String>();
            List<String> delpoolForCitation = new ArrayList<String>();
            Map<String, List<String>> recpool = new LinkedHashMap<String, List<String>>();
            
            int delRowCount = 0;
            int nonRecodeRowCount = 0;
            boolean hasRecodeRow = true;
            
            for (int i = 0; i < rdtbl.size(); i++){
            
                List<Object> rdtbli = (List<Object>) rdtbl.get(i);
                
                
                String val = (String)rdtbli.get(1);
                String rawCnd = (String)rdtbli.get(3);
                
                
                if ((Boolean) rdtbli.get(0)) {
                    // delete rows
                    delRowCount++;
                } else {
                    // recode ?
                    if(val.equals(rawCnd)){
                        nonRecodeRowCount++;
                    }
                }
            }
            
            int allrows = delRowCount + nonRecodeRowCount;
            if (allrows == rdtbl.size()) {
                //hasRecodeRow = false;
                dbgLog.fine("no meaningful recodeing request is stored");
            }
            
            
            
            for (int i = 0; i < rdtbl.size(); i++){
            
                List<Object> rdtbli = (List<Object>) rdtbl.get(i);
                
                
                String val = (String)rdtbli.get(1);
                String rawCnd = (String)rdtbli.get(3);
                
                
                dbgLog.fine("condtion= "+rawCnd);
                if ((Boolean) rdtbli.get(0)) {
                    // delete rows                   
                    String varUnit = "x[[\"" + baseVarNameSet.get(j) + "\"]]";
                    String varNameOnly = baseVarNameSet.get(j);
                    String cnd = conditionDecoder(rawCnd, varUnit, "d", variableTypes[j]);
                    String cndForcitation = conditionDecoder(rawCnd, varNameOnly, "d", variableTypes[j]);
                    if ((cnd == null) || (cnd.equals(""))){
                        dbgLog.fine("this subsetting condition was invalid:["+rawCnd+"] ");
                    } else {
                        delpool.add(cnd);
                        delpoolForCitation.add(cndForcitation);
                    }
                } else {
                  if (hasRecodeRow){
                    // recode line
                    rcnt++;
                    String vn = "x[[\"" + recodedVarNameSet.get(j) + "\"]]";
                    String vo = "x[[\"" + baseVarNameSet.get(j) + "\"]]";
                    String recode1stLine = null;
                    //  x[["nv"]]<-NA
                    
                    if (rcnt == 1){
                        recode1stLine = vn  + " <- NA\n";
                        recodeConditions.add(recode1stLine);
                    }
                    if (!recpool.containsKey(val)){
                        // for the first time
                        // x[["nv"]][ (x[["NATINT"]] == 4 ) | () | ...  ] <- 34
                        String cnd = conditionDecoder(rawCnd, vo, "r", variableTypes[j]);
                        if ((cnd == null) || (cnd.equals(""))){
                            dbgLog.fine("this recode condition was invalid:["+rawCnd+"] ");
                        } else {
                            List<String> tmp = new ArrayList<String>();
                            tmp.add(cnd);
                            recpool.put(val, tmp);
                        }
                        
                    } else {
                        // x[["nv"]][ (x[["NATINT"]] == 4 ) | () | ...  ] <- 34

                        String cnd = conditionDecoder(rawCnd, vo, "r", variableTypes[j]);
                        if ((cnd == null) || (cnd.equals(""))){
                            dbgLog.fine("this recode condition was invalid:["+rawCnd+"] ");
                        } else {
                            (recpool.get(val)).add(cnd);
                        }

                        
                    }
                    dbgLog.fine("recpool within loop\n"+recpool);
                  } // no meaningful recoding
                } // subset or not
            } // for each line of the recodeTable
            
            // subset
            if (delpool.size() > 0){
                subsetConditions.add("x <- subset(x, ( " + StringUtils.join(delpool," &" ) + " ))\n\n"  );
            }
            if (delpoolForCitation.size() > 0){
                subsetConditionsForCitation.add(StringUtils.join(delpoolForCitation," AND" ) );
            }

            // recode

            if (recpool.size() > 0){
                    dbgLog.fine("recpool outside loop\n"+recpool);
                    String vn = "x[[\"" + recodedVarNameSet.get(j) + "\"]]";
            
            
                String recode2ndLine = null;
                for (Map.Entry<String, List<String>> recpooli : recpool.entrySet()){
                    //dbgLog.fine("recpooli: foreach loop\n"+recpooli);
                    //dbgLog.fine("recpooli: value foreach loop\n"+recpooli.getValue());
                    //dbgLog.fine("recpooli: value foreach loop: size\n"+recpooli.getValue().size());
                    
                    if (variableTypes[j] > 0){
                        dbgLog.fine("numeric var: key="+recpooli.getKey());
                        if (recpooli.getKey().equals(".")){
                            recode2ndLine =  vn + "[" + StringUtils.join(recpooli.getValue()," |" ) + "] <- " + " NA " + "\n";
                        } else {
                            recode2ndLine =  vn + "[" + StringUtils.join(recpooli.getValue()," |" ) + "] <- " + recpooli.getKey() + "\n";
                        }
                    } else {
                        dbgLog.fine("char var: key="+recpooli.getKey());
                        if (recpooli.getKey().equals("NA")){
                            recode2ndLine =  vn + "[" + StringUtils.join(recpooli.getValue()," |" ) + "] <- " + recpooli.getKey() + "\n";
                        } else if (recpooli.getKey().equals(".")){
                            recode2ndLine =  vn + "[" + StringUtils.join(recpooli.getValue()," |" ) + "] <- " + " NA " + "\n";
                        } else {
                            recode2ndLine =  vn + "[" + StringUtils.join(recpooli.getValue()," |" ) + "] <- '"+ recpooli.getKey() + "'\n";
                        }
                    }
                    recodeConditions.add(recode2ndLine);
                    
                }
            
            }
            
       }
        conditions.put("subset", subsetConditions);
        conditions.put("subsetForCitation", subsetConditionsForCitation);
        conditions.put("recode", recodeConditions);
        return conditions;
    }





    public List<List<String>> getValueRange(String tkn){
  
        dbgLog.fine("received token="+tkn);
        String step0 = StringUtils.strip(tkn);
        dbgLog.fine("step0="+step0);

        // string into tokens
        String[] step1raw = step0.split(",");
        
        dbgLog.fine("step1raw="+StringUtils.join(step1raw, ","));
        
        // remove meaningless commas if exist
        
        List<String> step1 = new ArrayList<String>();

        for (String el : step1raw) {
            if (!el.equals("")) {
                step1.add(el);
            }
        }
        
        dbgLog.fine("step1="+StringUtils.join(step1,","));

        
        List<List<String>> rangeData = new ArrayList<List<String>>();
        
        // for each token, check the range operator
        
        for (int i=0; i<step1.size(); i++){
            LinkedList<String> tmp = new LinkedList<String>(
                Arrays.asList(  String2StringArray(String.valueOf(step1.get(i)))));
            
            Map<String, String> token = new HashMap<String, String>();
            
            if ((!tmp.get(i).equals("[")) && (!tmp.get(i).equals("("))){
                // no LHS range operator
                // assume [
                token.put("start","3");
            } else if (tmp.get(0).equals( "[")) {
                token.put("start", "3");
                tmp.removeFirst();
            } else if (tmp.get(0).equals("(")) {
                token.put("start", "5");
                tmp.removeFirst();
            }
            
            if ((!tmp.getLast().equals("]")) && (!tmp.getLast().equals(")"))){
                // no RHS range operator
                // assume ]
                token.put("end", "4");
            } else if (tmp.getLast().equals("]")){
                tmp.removeLast();
                token.put("end", "4");
            } else if (tmp.getLast().equals(")")){
                tmp.removeLast();
                token.put("end", "6"); 
            }
            
          // after these steps, the string does not have range operators;
          // i.e., '-9--3', '--9', '-9-','-9', '-1-1', '1', '3-4', '6-'

            if ((tmp.get(0).equals("!")) && (tmp.get(1).equals("=")) ){
                // != negation string is found
                token.put("start", "2");
                token.put("end", ""); 
                token.put("v1", StringUtils.join( tmp.subList(2, tmp.size()),""));
                token.put("v2", "");
                dbgLog.fine( "value="+ StringUtils.join( tmp.subList(2, tmp.size()),"," ));
                
            } else if ((tmp.get(0).equals("-")) && (tmp.get(1).equals("-"))){
                // type 2: --9
                token.put("v1", "");
                tmp.removeFirst();
                token.put("v2", StringUtils.join(tmp, "")); 
            } else if ((tmp.get(0).equals("-")) && (tmp.getLast().equals("-"))) {
                // type 3: -9-
                token.put("v2", "");
                tmp.removeLast();
                token.put("v1", StringUtils.join(tmp, ""));
            } else if ((!tmp.get(0).equals("-")) && (tmp.getLast().equals("-"))) {
                // type 8: 6-
                token.put("v2", "");
                tmp.removeLast();
                token.put("v1", StringUtils.join(tmp,""));
            } else {
                int count=0;
                List<Integer> index= new ArrayList<Integer>();
                for (int j=0; j< tmp.size(); j++){
                    if (tmp.get(j).equals("-")){
                        count++;
                        index.add(j);
                    }
                }

                if (count >=2){
                    // range type
                    // divide the second hyphen
                    // types 1 and 5: -9--3, -1-1
                    // token.put("v1", StringUtils.join(tmp[0..($index[1]-1)],"" ));
                    token.put("v2", StringUtils.join(tmp.subList((index.get(1)+1), tmp.size()), ""));

                } else if (count == 1){
                    if (tmp.get(0).equals("-")){
                        // point negative type
                        // type 4: -9 or -inf,9
                        // do nothing
                        if ( (token.get("start").equals("5")) &&
                            ( (token.get("end").equals("6")) || (token.get("end").equals("4")) ) ) {
                            token.put("v1", "");
                            tmp.removeFirst();
                            token.put("v2", StringUtils.join(tmp,""));
                        } else {
                            token.put("v1", StringUtils.join(tmp,""));
                            token.put("v2", StringUtils.join(tmp,""));
                        }
                    } else {
                        // type 7: 3-4
                        // both positive value and range type
                        String[] vset = (StringUtils.join(tmp,"")).split("-");
                        token.put("v1", vset[0]);
                        token.put("v2", vset[1]);
                    }

                } else {
                    // type 6: 1
                    token.put("v1", StringUtils.join(tmp,""));
                    token.put("v2", StringUtils.join(tmp,""));
                }
            }
            
            dbgLog.fine(i + "-th result=" + token.get("start")+ "|" +
                token.get("v1")+"|" +token.get("end")+"|" +token.get("v2"));
            
            List<String> rangeSet = new ArrayList<String>();
            rangeSet.add(token.get("start"));
            rangeSet.add(token.get("v1"));
            rangeSet.add(token.get("end"));
            rangeSet.add(token.get("v2"));
            rangeData.add(rangeSet);
           
        }

        dbgLog.fine("rangeData:\n"+rangeData);
        return rangeData;
    }

    public String[] String2StringArray(String token) {
        char[] temp = token.toCharArray();
        String[] tmp = new String[temp.length];
        for (int i=0; i<temp.length; i++) {
           tmp[i] = String.valueOf(temp[i]);
        }
        return tmp;
    }

    public String conditionDecoder(String cndfrag, String vn, String type, int vt) {
        String condition = null;
        String parsedToken = null;
        
            List<List<String>> range = getValueRange(cndfrag);
            
            dbgLog.fine("cndfrag:\n"+ range);
            
            condition = getConditionString(range, vn, type, vt);
            dbgLog.fine("returned condition:\n"+ parsedToken);
            
        
        return condition;
    }

    
    public String getConditionString( List<List<String>> rangeSet, 
        String variableName, String type, int vtype){

        if ((vtype > 2)|| (vtype < 0)){
            dbgLog.fine("variable type undefined");
            vtype=1;
        }
        
        String sep =" | ";
        if (type.equals("d")){;
            sep = " & ";
        }
        
        dbgLog.fine("range received:\n" + rangeSet);
        dbgLog.fine("variable type =" + vtype);
        
        StringBuilder finalCondition = new StringBuilder();
        List<Integer> removalList = new ArrayList<Integer>();
        
        for (int i=0; i< rangeSet.size(); i++){

            dbgLog.fine( i + "-th set=\n" + rangeSet.get(i));
            dbgLog.fine("range: 1 and 3:" + rangeSet.get(i).get(1) + "\t" + rangeSet.get(i).get(3));
            
            StringBuilder condition= new StringBuilder();

            if (   (rangeSet.get(i).get(1).equals(rangeSet.get(i).get(3))) 
                && (rangeSet.get(i).get(0).equals("3"))
                && (rangeSet.get(i).get(2).equals("4")) ) {
                dbgLog.fine("point case");
                // point type
                if (vtype > 0){
                    if (rangeSet.get(i).get(1).equals(".")){
                        condition.append("(is.na(" +  variableName  + "))");
                        dbgLog.fine("missing value case:numeric var");
                    } else {
                        condition.append("(" +  variableName + " == " +  rangeSet.get(i).get(1) + ")");
                    }
                } else {
                    if (rangeSet.get(i).get(1).equals(".")){
                        dbgLog.fine("missing value case: char var");
                        condition.append("(is.na(" +  variableName  + "))");
                    } else {
                        condition.append("(" +  variableName + " == '" +  rangeSet.get(i).get(1) + "')") ;
                    }
                }

                    if (type.equals("d")){
                        condition.insert(0, " !");
                    } else {
                        condition.insert(0, " ");
                    }                    
                dbgLog.fine(i + "-th condition point:" + condition.toString());

            } else if (rangeSet.get(i).get(0).equals("2")) {
                dbgLog.fine("point-negation case");
                // point negation
                if (vtype > 0){
                    
                    if (rangeSet.get(i).get(1).equals(".")){
                        condition.append("(! is.na(" +  variableName  + "))");
                        dbgLog.fine("missing value case:numeric var");
                        
                    } else {
                    condition.append( "("  +  variableName + " != " + rangeSet.get(i).get(1) + ")" );
                    }
                } else {
                    if (rangeSet.get(i).get(1).equals(".")){
                        dbgLog.fine("missing value case: char var");
                        condition.append("(! is.na(" +  variableName  + "))");
                    } else {
                    condition.append( "("  +  variableName + " != '"+ rangeSet.get(i).get(1) + "')" );
                    }
                }

                if (type.equals("d")){
                    condition.insert(0, " !");
                } else {
                    condition.insert(0, " ") ;
                }

                dbgLog.fine(i + "-th condition point(negation):" + condition.toString());

            } else {
                if (vtype > 0){
                    // range type
                    StringBuilder conditionL = new StringBuilder(); 
                    StringBuilder conditionU = new StringBuilder();

                    if ((rangeSet.get(i).get(0).equals("5")) && (rangeSet.get(i).get(1).equals(""))){
                        conditionL.append("");

                    } else {
                        conditionL.append( "("  +  variableName + " " + 
                            rangeOpMap.get(rangeSet.get(i).get(0)) + "" +
                            rangeSet.get(i).get(1) + ")");
                    }

                    dbgLog.fine(i + "-th condition(Lower/upper bounds)=" + conditionL.toString());

                    if ((rangeSet.get(i).get(2).equals("6")) && (rangeSet.get(i).get(3).equals(""))){

                        conditionU.append("");

                    } else {
                        String andop = null;

                        if (!(conditionL.toString()).equals("")){
                            andop =  " & " ;
                        } else {
                             andop = "";
                        }

                        conditionU.append( andop +  "(" + variableName  
                            + " " +  rangeOpMap.get(rangeSet.get(i).get(2)) 
                            + " " +  rangeSet.get(i).get(3) + ")");
                    }

                    dbgLog.fine("conditionU="+conditionU.toString());

                    condition.append(conditionL.toString() + " " + conditionU.toString());

                    if (type.equals("d")){
                        condition.insert(0, " !");
                    } else {
                        condition.insert(0, " ") ;
                    }
                } else {
                    removalList.add(i);
                }// end: type check
            } // end: range type-loop
            
            
            dbgLog.fine(i + "-th " + condition.toString());

            if (i < (rangeSet.size() -1) ) {
                finalCondition.append(condition.toString() + sep) ;
            } else {
                finalCondition.append(condition.toString());
            }
        }
        
        
        dbgLog.fine("final condition:\n" + finalCondition.toString());
        
        return finalCondition.toString();
        
        
        
        
    }


}
