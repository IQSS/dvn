/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.analysis;

import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author gdurand
 */
@Local
public interface NetworkDataServiceLocal extends java.io.Serializable {

    public static final String AUTOMATIC_QUERY_NTHLARGEST = "component";
    public static final String AUTOMATIC_QUERY_BICONNECTED = "biconnected_component";
    public static final String AUTOMATIC_QUERY_NEIGHBORHOOD = "add_neighborhood";

    public static final String NETWORK_MEASURE_DEGREE = "add_degree";
    public static final String NETWORK_MEASURE_UNIQUE_DEGREE = "add_unique_degree";
    public static final String NETWORK_MEASURE_RANK = "add_pagerank";
    public static final String NETWORK_MEASURE_IN_LARGEST = "add_in_largest_component";
    public static final String NETWORK_MEASURE_BONACICH_CENTRALITY = "add_bonacich_centrality";

    public void initAnalysis(String fileLocation, String sessionId);
    
    public NetworkDataSubsetResult runManualQuery( String attributeSet, String query, boolean eliminateDisconnectedVertices) throws SQLException;
    public NetworkDataSubsetResult runAutomaticQuery( String automaticQuery, int nValue);
    public String runNetworkMeasure( String networkMeasure, List<NetworkMeasureParameter> parameters) ;

    public void undoLastEvent();
    public void resetAnalysis();
            
    public File getSubsetExport();

    public void ingest(StudyFileEditBean editBean);
   
    
}
