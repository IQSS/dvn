/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb;
import java.util.*;
/**
 *
 * @author asone
 */

public interface DataAnalysisService {
    /**
     * 
     *
     * @param     
     * @return    
     */
    
    // public Map<String, List<String>> execute(ServiceRequest sro);
    public Map<String, String> execute(ServiceRequest sro);
    
    /**
     * 
     *
     * @param     
     * @return    
     */
    public String getGUIconfigData();
}
