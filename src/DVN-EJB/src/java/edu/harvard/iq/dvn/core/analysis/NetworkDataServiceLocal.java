/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.analysis;

import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import java.io.File;
import java.util.List;
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
    public String runNetworkMeasure(String rWorkspace, String networkMeasure, List<NetworkMeasureParameter> parameters);

    public void undoLastEvent(String rWorkspace);
            
    public File getSubsetExport(String rWorkspace);

    public void ingest(StudyFileEditBean editBean);
    
    
}
