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


public class DvnRJobRequest implements ServiceRequest {



    // ----------------------------------------------------- Constructors

    /**
     * 3-arg Constructor
     * for later extensions
     */
/*
    public DvnRJobRequest(List<DataVariable> dv, 
        Map<String, List<String>> listParams,
        Map<String, String> stringParams 
        ){
        
        dataVariablesForRequest = dv;
        
        stringParametersForRequest = stringParams;
        
        listParametersForRequest = listParams;
    }
*/
    
    /**
     * 2-arg Constructor
     *
     */
    public DvnRJobRequest(List<DataVariable> dv, Map<String, List<String>> listParams){
        
        dataVariablesForRequest = dv;
        
        listParametersForRequest = listParams;
        out.println("variables="+dataVariablesForRequest);
        out.println("map="+listParametersForRequest);
    }


    // ----------------------------------------------------- fields


    /** metadata of requested variables */
    private List<DataVariable> dataVariablesForRequest;
    
    /** list-type (one-to-many) parameter */
    private Map<String, List<String>> listParametersForRequest;

    /** scalar-type(one-to-one) parameter */
    // private Map<String, String> stringParametersForRequest;
    

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
     * Getter for property stringParametersForRequest
     *
     * @return    
     */
/*
    public Map<String, String> getStringParametersForRequest(){
        return this.stringParametersForRequest;
    }
*/


    // ----------------------------------------------------- accessors
    // metadata for RServe
    
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
        
        List rw = new ArrayList();
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
    
    /**
     * Getter for property raw-to-safe-variable-name list
     * @return    A Map that maps an unsafe variable name to 
     *            a safe one
     */
    public Map<String, String> getRaw2SafeVarNameTable(){
        Map<String, String> raw2SafeVarNameTable=null;
        
        return raw2SafeVarNameTable;
    }
    
    /**
     * Getter for property variable ids
     * @return    A String array of variable Ids
     */
    public String[] getVariableIDs (){
        String[] variableIds=null;
        List<String> rw = new ArrayList();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
                rw.add("v"+dv.getId().toString());
        }
        
        variableIds = (String[])rw.toArray(new String[rw.size()]);
        return variableIds;
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

    /**
     * Getter for property value-label list
     *
     * @return    A value-label table as a Map object
     */
    public Map<String, Map<String,String>> getValueTable(){
        Map<String, Map<String,String>> valueTable = new HashMap<String, Map<String, String>>();
        DataVariable dv = null;
        for (Iterator el = dataVariablesForRequest.iterator(); el.hasNext();) {
            dv = (DataVariable) el.next();
            Collection<VariableCategory> vcat = dv.getCategories();
            if (vcat.size()>0){
                Map <String, String> vl = new HashMap<String, String>();
                for (Iterator elc = vcat.iterator(); elc.hasNext();){
                    VariableCategory vcati = (VariableCategory) elc.next();
                    vl.put(vcati.getValue(), vcati.getLabel());
                }
                valueTable.put(dv.getId().toString(), vl);
            }
        }
        return valueTable;
    }
    
    public Map<String, List<String>> getRecodedVarParameters() {
        Map<String, List<String>> mpl = null;

        return mpl;
    }

    /**
     * Getter for property missing value table
     *
     * @return    a missing-value table object as a Map object
     */


    /**
     * Getter for property requestTypeToken
     *
     * @param     
     * @return    
     */
    public String getRequestType() {
        String type=null;
        List<String> requestTypeToken = listParametersForRequest.get("requestType");
        out.println("requestType="+requestTypeToken.get(0));
        type =  requestTypeToken.get(0);
        return type;
    }

    /**
     * Getter for property requestTypeToken
     *
     * @param     
     * @return    
     */
    public String getDownloadRequestParameter() {
        String param=null;
        
        return param;
    }

    public String getZeligModelName() {
        String modelName = null;
        
        return modelName;
    }
    
    
    // -----------------------------------------------------
    // private methods
    

    /**
     * 
     *
     * @param     
     * @return    
     *//*
    public void setServiceRequest(ServiceRequest sr){
    
    
    }
*/
    /**
     * 
     *
     * @param     
     * @return    
     *//*
    public ServiceRequest getServiceRequest(){
        ServiceRequest sr = null;
        
        return sr;
    }
*/
/**/

//    public static void main(String args[]) {
//        // how to test this class
//        // create an instance
//        // List<DataVariable> getDataVariableForRequest()
//        // Map<String, List<String>> mpl
//        ServiceRequest sro = new DvnRJobRequest(getDataVariableForRequest(), mpl);
//        
//        
//        // variable type
//        int [] jvartyp  = {1,1,1};// = mp.get("vartyp").toArray()
//        
//        // variable format
//        String [] varFmtN = {};
//        List<String> varFmtV = new ArrayList<String>();
//        // new RList(varFmtV, varFmtN);
//        
//        // variable names
//        String [] jvnames = {"race","age","vote"};
//        
//        // raw-to-safe variable name map
//        String [] Rsafe2rawN = {};
//        List<String> Rsafe2rawV = new ArrayList<String>();
//        Map<String, String> Rsafe2raw = new HashMap<String, String>();
//        // new RList(Rsafe2rawV, Rsafe2rawN)
//        
//        
//        // variable Ids
//        String[] jvarnmbr = {"v198057","v198059","v198060"};
//            
//        // variable labels
//        String[] jvarlabels = {"race","age","vote"};
//        
//        // value-label table
//        /*
//        
//            VALTABLE[["4"]]<-list(
//            "7"="REFUSAL",
//            "9"="MISSING",
//            "3"="ONCE OR TWICE A WEEK",
//            "5"="EVERY DAY",
//            "2"="LESS THN ONCE A WEEK",
//            "4"="NEARLY EVERY DAY",
//            "8"="DONT KNOW",
//            "1"="NEVER")
//        */
//    }





}
