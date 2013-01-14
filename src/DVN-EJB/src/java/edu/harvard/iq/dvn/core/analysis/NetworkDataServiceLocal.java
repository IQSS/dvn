/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
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
    public File getSubsetExport(boolean getGraphML, boolean getTabular);

    public void ingest(StudyFileEditBean editBean);
   
    
}
