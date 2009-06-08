/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.subsetting;

import com.icesoft.faces.context.FileResource;
import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.StringResource;
import com.sun.corba.se.impl.resolver.FileResolverImpl;
import edu.harvard.iq.dvn.core.analysis.NetworkDataServiceLocal;
import edu.harvard.iq.dvn.core.analysis.NetworkDataSubsetResult;
import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.NetworkDataFile;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnRGraphServiceImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author gdurand
 */
public class NetworkDataAnalysisPage extends VDCBaseBean implements Serializable {

    @EJB
    StudyServiceLocal studyService;

    @EJB
    NetworkDataServiceLocal networkDataService;

    private Long fileId;
    private NetworkDataFile file;
    private String rWorkspace;

    public NetworkDataFile getFile() {
        return file;
    }

    public void setFile(NetworkDataFile file) {
        this.file = file;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public void init() {
        super.init();

        String fileIdStr = getRequestParam("fileId");

        if (fileIdStr != null) {
            fileId = Long.parseLong(fileIdStr);
            file = (NetworkDataFile) studyService.getStudyFile(fileId);

            rWorkspace = networkDataService.initAnalysis(file.getFileSystemLocation() + ".RData");

            NetworkDataAnalysisEvent initialEvent = new NetworkDataAnalysisEvent();
            initialEvent.setLabel("Initial State");
            initialEvent.setVertices(file.getVertexDataTable().getCaseQuantity());
            initialEvent.setEdges(file.getEdgeDataTable().getCaseQuantity());
            events.add(initialEvent);

        } else {
            FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
            HttpServletResponse response = (javax.servlet.http.HttpServletResponse) fc.getExternalContext().getResponse();
            try {
                response.sendRedirect("/dvn/dv/" + getVDCRequestBean().getCurrentVDC().getAlias() + "/faces/IdDoesNotExistPage.xhtml");
                fc.responseComplete();
            } catch (IOException ex) {
                //Logger.getLogger(EditCollectionPage.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("IOException thrown while trying to redirect to Manage Collections Page");
            }

        }


    }

    private String actionType;
    private String manualQueryType = DataTable.TYPE_VERTEX;
    private String manualQuery;
    private boolean eliminateDisconnectedVertices;
    private String automaticQueryType;
    private String automaticQueryNthValue;
    private String networkMeasureType;

    private List<NetworkDataAnalysisEvent> events = new ArrayList();
    private List<NetworkMeasureParameter> networkMeasureParamterList = new ArrayList();

    private List<SelectItem> vertexAttributeSelectItems;
    private List<SelectItem> edgeAttributeSelectItems;
    private List<SelectItem> automaticQuerySelectItems;
    private List<SelectItem> networkMeasureSelectItems;

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getManualQueryType() {
        return manualQueryType;
    }

    public void setManualQueryType(String manualQueryType) {
        this.manualQueryType = manualQueryType;
    }

    public String getManualQuery() {
        return manualQuery;
    }

    public void setManualQuery(String manualQuery) {
        this.manualQuery = manualQuery;
    }

    public boolean isEliminateDisconnectedVertices() {
        return eliminateDisconnectedVertices;
    }

    public void setEliminateDisconnectedVertices(boolean eliminateDisconnectedVertices) {
        this.eliminateDisconnectedVertices = eliminateDisconnectedVertices;
    }

    public String getAutomaticQueryType() {
        return automaticQueryType;
    }

    public void setAutomaticQueryType(String automaticQueryType) {
        this.automaticQueryType = automaticQueryType;
    }

    public String getAutomaticQueryNthValue() {
        return automaticQueryNthValue;
    }

    public void setAutomaticQueryNthValue(String automaticQueryNthValue) {
        this.automaticQueryNthValue = automaticQueryNthValue;
    }

    

    public String getNetworkMeasureType() {
        return networkMeasureType;
    }

    public void setNetworkMeasureType(String networkMeasureType) {
        this.networkMeasureType = networkMeasureType;
    }


    public List<NetworkDataAnalysisEvent> getEvents() {
        return events;
    }

    public void setEvents(List<NetworkDataAnalysisEvent> events) {
        this.events = events;
    }

    


    public List<SelectItem> getAttributeSelectItems() {
        if (vertexAttributeSelectItems == null) {
            // initialize lists
            vertexAttributeSelectItems =  new ArrayList();
            edgeAttributeSelectItems = new ArrayList();

            for (DataVariable dv : file.getVertexDataTable().getDataVariables()) {
                vertexAttributeSelectItems.add(new SelectItem(dv.getName()) );
            }

            for (DataVariable dv : file.getEdgeDataTable().getDataVariables()) {
                edgeAttributeSelectItems.add(new SelectItem(dv.getName()) );
            }
        }

        if (DataTable.TYPE_VERTEX.equals(manualQueryType)) {
            return vertexAttributeSelectItems;
        } else if (DataTable.TYPE_EDGE.equals(manualQueryType)) {
            return edgeAttributeSelectItems;
        }

        return new ArrayList();

    }

    public List<SelectItem> getAutomaticQuerySelectItem() {
        // TODO: we will eventually have to read all this from xml
        if (automaticQuerySelectItems == null) {
            automaticQuerySelectItems = new ArrayList();
            automaticQuerySelectItems.add(new SelectItem(DvnRGraphServiceImpl.AUTOMATIC_QUERY_NTHLARGEST, "Largest Graph"));
            automaticQuerySelectItems.add(new SelectItem(DvnRGraphServiceImpl.AUTOMATIC_QUERY_BICONNECTED, "Biconnected Graph"));

        }

        return automaticQuerySelectItems;
    }

    public List<SelectItem> getNetworkMeasureSelectItems() {
        // TODO: we will eventually have to read all this from xml
        if (networkMeasureSelectItems == null) {
            networkMeasureSelectItems = new ArrayList();
            networkMeasureSelectItems.add(new SelectItem(DvnRGraphServiceImpl.NETWORK_MEASURE_RANK, "Page Rank"));
            networkMeasureSelectItems.add(new SelectItem(DvnRGraphServiceImpl.NETWORK_MEASURE_DEGREE, "Degree"));
        }

        return networkMeasureSelectItems;
    }

    public List<NetworkMeasureParameter> getNetworkMeasureParameterList() {
        return networkMeasureParamterList;
    }

    public void networkMeasureSelect_action(ValueChangeEvent e) {

        // TODO: we will eventually have to read all this from xml
        networkMeasureParamterList = new ArrayList();
        if ( "pageRank".equals( e.getNewValue().toString() ) ) {
            NetworkMeasureParameter d = new NetworkMeasureParameter();
            d.setName("d");
            networkMeasureParamterList.add(d);
        }

    }


    public String manualQuery_action() {

        NetworkDataSubsetResult result = networkDataService.runManualQuery(rWorkspace, manualQueryType, manualQuery, eliminateDisconnectedVertices );

        NetworkDataAnalysisEvent event = new NetworkDataAnalysisEvent();
        event.setLabel("Manual Query");
        event.setAttributeSet(manualQueryType);
        event.setQuery(manualQuery);
        event.setVertices( result.getVertices() );
        event.setEdges( result.getEdges() );
        events.add(event);

        return null;
    }

    public String automaticQuery_action() {

        NetworkDataSubsetResult result = networkDataService.runAutomaticQuery(rWorkspace, automaticQueryType, automaticQueryNthValue);

        NetworkDataAnalysisEvent event = new NetworkDataAnalysisEvent();
        event.setLabel("Automatic Query");
        event.setAttributeSet("N/A");
        event.setQuery(automaticQueryType + "(" + automaticQueryNthValue + ")");
        event.setVertices( result.getVertices() );
        event.setEdges( result.getEdges() );
        events.add(event);

        return null;
    }

    public String networkMeasure_action() {

        String result = networkDataService.runNetworkMeasure(rWorkspace, networkMeasureType, null);

        NetworkDataAnalysisEvent event = new NetworkDataAnalysisEvent();
        event.setLabel("Network Measure");
        event.setAttributeSet("N/A");
        event.setQuery(networkMeasureType + "("+ listString(getNetworkMeasureParameterList()) + ")");
        event.setVertices( getLastEvent().getVertices() );
        event.setEdges( getLastEvent().getEdges() );
        events.add(event);

        // add measure to attributeList
        vertexAttributeSelectItems.add(new SelectItem(result));
        
        return null;
    }
    

    public Resource getSubsetFile() {
        File subsetFile = networkDataService.getSubsetExport(rWorkspace);
        return new FileResource( subsetFile );

    }

    private String listString(List<NetworkMeasureParameter> paramterList) {
        String returnString = "";
        for (NetworkMeasureParameter parameter : paramterList) {
            if (!"".equals(returnString)) {
                returnString += "; ";
            }
            returnString += parameter.getName() + "=" + parameter.getValue();
        }
        return returnString;
    }

    private NetworkDataAnalysisEvent getLastEvent() {
        return events.get( events.size() - 1);
    }



    public class NetworkMeasureParameter {

        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public class NetworkDataAnalysisEvent {

        private String label;
        private String attributeSet;
        private String query;
        private long edges;
        private long vertices;

        public String getAttributeSet() {
            return attributeSet;
        }

        public void setAttributeSet(String attributeSet) {
            this.attributeSet = attributeSet;
        }

        public long getEdges() {
            return edges;
        }

        public void setEdges(long edges) {
            this.edges = edges;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public long getVertices() {
            return vertices;
        }

        public void setVertices(long vertices) {
            this.vertices = vertices;
        }
    }


}
