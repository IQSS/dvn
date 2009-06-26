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

    public String initAnalysis(String fileLocation) throws Exception;
    
    public NetworkDataSubsetResult runManualQuery(String rWorkspace, String attributeSet, String query, boolean eliminateDisconnectedVertices) throws Exception;
    public NetworkDataSubsetResult runAutomaticQuery(String rWorkspace, String automaticQuery, String nValue) throws Exception;
    public String runNetworkMeasure(String rWorkspace, String networkMeasure, List<NetworkMeasureParameter> parameters) throws Exception;

    public void undoLastEvent(String rWorkspace) throws Exception;
            
    public File getSubsetExport(String rWorkspace) throws Exception;

    public void ingest(StudyFileEditBean editBean) throws Exception;
   
    
}
