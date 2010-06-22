/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.analysis;

import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnRGraphServiceImpl.DvnRGraphException;
import java.io.File;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author gdurand
 */
@Local
public interface NetworkDataServiceLocal extends java.io.Serializable {

    public String initAnalysis(String fileLocation) throws DvnRGraphException;
    
    public NetworkDataSubsetResult runManualQuery(String rWorkspace, String attributeSet, String query, boolean eliminateDisconnectedVertices) throws DvnRGraphException;
    public NetworkDataSubsetResult runAutomaticQuery(String rWorkspace, String automaticQuery, String nValue) throws DvnRGraphException;
    public String runNetworkMeasure(String rWorkspace, String networkMeasure, List<NetworkMeasureParameter> parameters) throws DvnRGraphException;

    public void undoLastEvent(String rWorkspace) throws DvnRGraphException;
    public void resetAnalysis(String rWorkspace) throws DvnRGraphException;
            
    public File getSubsetExport(String rWorkspace) throws DvnRGraphException;

    public void ingest(StudyFileEditBean editBean);
   
    
}
