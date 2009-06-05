/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.analysis;

import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import javax.ejb.Local;

/**
 *
 * @author gdurand
 */
@Local
public interface NetworkDataServiceLocal extends java.io.Serializable {

    public NetworkDataSubsetResult runManualQuery(String RDataFileName, String attributeSet, String query, Boolean eliminateDisconnectedVertices);

    public void ingest(StudyFileEditBean editBean);
    
    
}
