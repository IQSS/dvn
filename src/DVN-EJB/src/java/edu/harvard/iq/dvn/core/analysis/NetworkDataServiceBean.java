/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.analysis;

import java.util.List;
import javax.ejb.Stateless;
import edu.harvard.iq.dvn.ingest.dsb.impl.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gdurand
 */
@Stateless
public class NetworkDataServiceBean implements NetworkDataServiceLocal, java.io.Serializable {

    public String initAnalysis() {
        return null;
    }

    public NetworkDataSubsetResult runManualQuery(String RDataFileName, String attributeSet, String query, Boolean eliminateDisconnectedVertices) {
        /*
        Map<String, Object> mpl = new HashMap<String, Object>();
        Map<String, String> resultInfo = new HashMap<String, String>();

        mpl.put("rFunction","manualQuery");
        mpl.put("attributeSet",attributeSet);
        mpl.put("query", query);
        mpl.put("eliminate",eliminateDisconnectedVertices);

        DvnRJobRequest rjr = new DvnRJobRequest(RDataFileName, mpl);
        DvnRGraphServiceImpl dgs = new DvnRGraphServiceImpl();
        resultInfo = dgs.execute(rjr);

        if (resultInfo.get("RexecError").equals("true")){
            // throw exception
            // resultInfo.get("RexecErrorDescription") -- error condition;
            // resultInfo.get("RexecErrorMessage") -- more detailed error
            //					  message, if available
        }
        */
        NetworkDataSubsetResult result = new NetworkDataSubsetResult();
        //result.setVertices( Long.parseLong( resultInfo.get("numVertices") ) );
        //result.setEdges( Long.parseLong( resultInfo.get("numEdges") ) );
        return result;
    }

    public NetworkDataSubsetResult runAutomaticQuery() {
        NetworkDataSubsetResult result = new NetworkDataSubsetResult();
        //result.setVertices( Long.parseLong( resultInfo.get("numVertices") ) );
        //result.setEdges( Long.parseLong( resultInfo.get("numEdges") ) );
        return result;
    }

    public String runNetworkMeasure() {
        return null;
    }



}
