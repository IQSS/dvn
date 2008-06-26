/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb.impl;

/**
 *
 * @author asone
 */
import edu.harvard.hmdc.vdcnet.dsb.*;
import edu.harvard.hmdc.vdcnet.study.*;
import static java.lang.System.*;
import java.util.*;
import org.apache.commons.lang.*;


public class DvnRJobRequest {

    public static Map<String, Integer> xtabOutputOptions =
        new HashMap<String, Integer>();
        
        
    public static Map<String, Integer> zeligOutputOptions =
        new HashMap<String, Integer>();
    public static Map<String, Integer> zeligAnalysisOptions =
        new HashMap<String, Integer>();

    static {
        xtabOutputOptions.put("xtb_Totals",0);
        xtabOutputOptions.put("xtb_Statistics", 1);
        xtabOutputOptions.put("xtb_Percentages",2);
        xtabOutputOptions.put("xtb_ExtraTables",3);
        
        zeligOutputOptions.put("Summary",0);
        zeligOutputOptions.put("Plots", 1);
        zeligOutputOptions.put("BinOutput",2);

     
     
     
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
        
        out.println("variables="+dataVariablesForRequest);
        out.println("map="+listParametersForRequest);
        out.println("value table="+valueTables);
        out.println("recodeSchema"+recodeSchema);
        out.println("model spec="+zeligModelSpec);
        checkVariableNames();
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

    }

    // ----------------------------------------------------- fields


    /** metadata of requested variables */
    private List<DataVariable> dataVariablesForRequest;
    
    /** list-type (one-to-many) parameter */
    private Map<String, List<String>> listParametersForRequest;

    /**  */
    
    private Map<String, Map<String, String>> valueTables;
    
    private Map<String, List<Object>> recodeSchema;
    /**  */
    private AdvancedStatGUIdata.Model zeligModelSpec;
    
    private String subsetPrefix = "x <- subset(x, (  ";
    
    private String subsetSuffix = "  ))";
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
     * Getter for property zeligModelSpec
     *
     * @return    
     */

    public AdvancedStatGUIdata.Model getZeligModelSpec(){
        return this.zeligModelSpec;
    }



    // ----------------------------------------------------- accessors
    // metadata for RServe
    
    
    public String getStudytitle(){
        List<String> studytitle = listParametersForRequest.get("studytitle");
        out.println("studytitle="+studytitle.get(0));
        return studytitle.get(0);
    }

    /**
     * Getter for property datafile path
     *
     * @param     
     * @return    
     */
    public String getSubsetFileName(){
        List<String> subsetFile = listParametersForRequest.get("subsetFileName");
        out.println("subsetFileName="+subsetFile.get(0));
        return subsetFile.get(0);
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
        Integer[]tmp = (Integer[])rw.toArray(new Integer[rw.size()]);
        out.println("vartype="+ StringUtils.join(tmp, ", "));
        int[] variableTypes=new int[tmp.length];
        for (int j=0;j<tmp.length;j++){
            variableTypes[j]= tmp[j];
        }
        return variableTypes;
    }

    /**
     * Getter for property variable formats
     *
     * @return    A Map that maps a format to
     *            its corresponding type, either time or date
     */
    public Map<String, String> getVariableFormats() {
        Map<String, String> variableFormats=null;
        
        return variableFormats;
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
            String raw = getVarIdToRawVarNameTable().get(vid);
            if (raw2safeTable.containsKey(raw)){
                varNameSet.add(raw2safeTable.get(raw));
            } else {
                varNameSet.add(raw);
            }
        }
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
        return (String[])ArrayUtils.addAll(getVariableLabels(), getRecodedVarLabelSet());
    }

    public String[] getUpdatedVariableIds(){
        return (String[])ArrayUtils.addAll(getVariableIds(), getRecodedVarIdSet());
    }

    public int[] getUpdatedVariableTypes(){
         return ArrayUtils.addAll(getVariableTypes(),getRecodedVarTypeSet());
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
        out.println("requestType="+type);
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
        out.println("dtdwnld="+param);
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
        out.println("aol="+tmp);
        return tmp;
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
        out.println("modelName="+modelName);
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
        out.println("class var Ids="+ varIdSet);
        if (varIdSet != null){

            List<String> varSet = getFileteredVarNameSet(varIdSet);
            out.println("class-var non-null case:"+ varSet);
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
            out.println("freq-var non-null case:"+ varSet);

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
            tmp.addAll(getFileteredVarNameSet(varIdSet2));

           lhs = "list(" + StringUtils.join(tmp,",") + ")";
             
        } else {
            varIdSet = listParametersForRequest.get("nmBxR1");
            List<String> tmp = getFileteredVarNameSet(varIdSet);

            if (varIdSet.size() > 1){
                lhs = "list(" + StringUtils.join(tmp,",") + ")";
            } else {
                lhs = tmp.get(0);
            }
        }
        out.println("lhs="+lhs);
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


    /**
     * 
     *
     * @return    
     */
    public List<String> getSubsettingConditions(){
        List<String> sb = new ArrayList<String>();
        
        /*
          delete by checkboxe case
          x <- subset(x, (  
          
                !(x[["NAThex5FDISC"]] == 7) & 
                !(x[["NAThex5FDISC"]] == 9) & 
                !(x[["NAThex5FDISC"]] == 8)
                
             ))
             
             
            //iteration unit: numeric case
            "!(x[['" + ${safe_variablename} + "']] == "  + ${value} + ")";

            // iteration unit: char case (quotation marks)
            "!(x[['" + ${safe_variablename} + "']] == '" + ${value} + "')";

             
             
             
        */
        
        
        
        return sb;
    }

}
