/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.analysis;

import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author gdurand
 */
@Local
public interface NetworkDataServiceLocal extends java.io.Serializable {

    public String initAnalysis(String fileLocation);
    
    public NetworkDataSubsetResult runManualQuery(String rWorkspace, String attributeSet, String query, boolean eliminateDisconnectedVertices);
    public NetworkDataSubsetResult runAutomaticQuery(String rWorkspace, String automaticQuery, String nValue);
    public String runNetworkMeasure(String rWorkspace, String networkMeasure, Map<String,String> parameters);

    public void ingest(StudyFileEditBean editBean);
    
    
}
