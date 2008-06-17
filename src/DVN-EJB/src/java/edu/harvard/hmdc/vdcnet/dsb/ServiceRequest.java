/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb;

/**
 *
 * @author asone
 */
import java.util.*;


public interface ServiceRequest {

    /**
     * 
     *
     * @param     
     * @return    
     *//*
    public void setServiceRequest(ServiceRequest sr);
*/
    /**
     * 
     *
     * @param     
     * @return    
     *//*
    public ServiceRequest getServiceRequest();
*/

    public String getSubsetFileName();
    
    public int[] getVariableTypes();
    
    public Map<String, String> getVariableFormats();
    public String[] getVariableNames();
    public Map<String, String> getRaw2SafeVarNameTable();


    public String[] getVariableIDs ();
    public String[] getVariableLabels();
    public Map<String, Map<String,String>> getValueTable();
    public String getRequestType();
    public String getDownloadRequestParameter();
    public String getZeligModelName();
}
