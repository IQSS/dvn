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
package edu.harvard.iq.dvn.core.web.subsetting;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.context.Resource;
//COMMENTED OUT TO COMPILE UNDER ICEFACES 2.O -- L.A. import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import edu.harvard.iq.dvn.core.analysis.NetworkDataServiceLocal;
import edu.harvard.iq.dvn.core.analysis.NetworkDataSubsetResult;
import edu.harvard.iq.dvn.core.analysis.NetworkMeasureParameter;
import edu.harvard.iq.dvn.core.study.*;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.GuestBookResponse;
import edu.harvard.iq.dvn.core.vdc.GuestBookResponseServiceBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ConcurrentAccessException;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author gdurand
 */
@EJB(name="networkData", beanInterface=edu.harvard.iq.dvn.core.analysis.NetworkDataServiceLocal.class)
@ViewScoped
@Named("NetworkDataAnalysisPage")
public class NetworkDataAnalysisPage extends VDCBaseBean implements Serializable {

    

    @EJB
    StudyServiceLocal studyService;
    @EJB
    StudyFileServiceLocal studyFileService;
    @EJB
    GuestBookResponseServiceBean guestBookResponseServiceBean;

    NetworkDataServiceLocal networkDataService;
    
    private static Logger dbgLog = Logger.getLogger(NetworkDataAnalysisPage.class.getPackage().getName());

    private Long fileId;
    private Long versionNumber;
    private StudyUI studyUI;
    private NetworkDataFile file;
    
    private String actionType = "manualQuery";
    private String manualQueryType = DataTable.TYPE_VERTEX;
    private String manualQuery;
    private boolean eliminateDisconnectedVertices = false;
    private String automaticQueryType = NetworkDataServiceLocal.AUTOMATIC_QUERY_NTHLARGEST;
    private String automaticQueryNthValue;
    private String networkMeasureType = NetworkDataServiceLocal.NETWORK_MEASURE_RANK;

    private List<NetworkDataAnalysisEvent> events = new ArrayList();
    private List<NetworkMeasureParameter> networkMeasureParamterList = new ArrayList();
    private Map<String,String> friendlyNameMap;
    private Map<String,List> networkMeasureParameterMap;

    private List<SelectItem> vertexAttributeSelectItems;
    private List<SelectItem> edgeAttributeSelectItems;
    private List<SelectItem> automaticQuerySelectItems;
    private List<SelectItem> networkMeasureSelectItems;

    private boolean canUndo = false;

    // used for displaying errros
    private UIComponent manualQueryError;
    private UIComponent automaticQueryError;
    private UIComponent networkMeasureError;
    private HtmlDataTable eventTable;
    private boolean downloadInProgress = false;
    private boolean showInProgressPopup = false;

    
    public void init() {
        super.init();

        try {
            file = (NetworkDataFile) studyFileService.getStudyFile(fileId);           
            
            if (versionNumber!=null) {
                StudyVersion sv = file.getStudy().getStudyVersionByNumber(versionNumber);
                studyUI = new StudyUI(sv, null);
                if (sv == null) {
                    redirect("/faces/IdDoesNotExistPage.xhtml?type=Study%20Version");
                    return;
                }
                
            } else {
                studyUI = new StudyUI(file.getStudy().getReleasedVersion(), null);
            }
            
        } catch (Exception e) { // id not a long, or file is not a NetworkDataFile (TODO: redirect to a different page if not network data file)
            redirect("/faces/IdDoesNotExistPage.xhtml?type=File");
            return;
        }
        
        //init workspace and page components 
        try {
            Context ctx = new InitialContext();
            networkDataService = (NetworkDataServiceLocal) ctx.lookup("java:comp/env/networkData");
            String sessionId = FacesContext.getCurrentInstance().getExternalContext().getSession(false).toString();
            networkDataService.initAnalysis(file.getFileSystemLocation(), sessionId);
            initComponents();
        } catch (Exception ex) {
            Logger.getLogger(NetworkDataAnalysisPage.class.getName()).log(Level.SEVERE, null, ex);
            redirect("/faces/ErrorPage.xhtml?errorMsg=" + ex.getMessage());
        }
    }

    private void initComponents() {
        vertexAttributeSelectItems =  new ArrayList();
        edgeAttributeSelectItems = new ArrayList();
        automaticQuerySelectItems = new ArrayList();
        networkMeasureSelectItems = new ArrayList();
        friendlyNameMap = new HashMap();
        networkMeasureParameterMap = new HashMap();


        // start with manual query atribute lists
        for (DataVariable dv : file.getVertexDataTable().getDataVariables()) {
            vertexAttributeSelectItems.add(new SelectItem(dv.getName()) );
        }

        for (DataVariable dv : file.getEdgeDataTable().getDataVariables()) {
            edgeAttributeSelectItems.add(new SelectItem(dv.getName()) );
        }
        
        // TODO: we will eventually have to read all the queries and network measures from xml
        // add automatic queries
        friendlyNameMap.put(NetworkDataServiceLocal.AUTOMATIC_QUERY_NTHLARGEST, "Largest Graph");
        automaticQuerySelectItems.add(new SelectItem(NetworkDataServiceLocal.AUTOMATIC_QUERY_NTHLARGEST, friendlyNameMap.get(NetworkDataServiceLocal.AUTOMATIC_QUERY_NTHLARGEST)));

        /*
        friendlyNameMap.put(NetworkDataServiceLocal.AUTOMATIC_QUERY_BICONNECTED, "Biconnected Graph");
        automaticQuerySelectItems.add(new SelectItem(NetworkDataServiceLocal.AUTOMATIC_QUERY_BICONNECTED, friendlyNameMap.get(NetworkDataServiceLocal.AUTOMATIC_QUERY_BICONNECTED)));
        */
        
        friendlyNameMap.put(NetworkDataServiceLocal.AUTOMATIC_QUERY_NEIGHBORHOOD, "Neighborhood");
        automaticQuerySelectItems.add(new SelectItem(NetworkDataServiceLocal.AUTOMATIC_QUERY_NEIGHBORHOOD, friendlyNameMap.get(NetworkDataServiceLocal.AUTOMATIC_QUERY_NEIGHBORHOOD)));

        // and network measures
        friendlyNameMap.put(NetworkDataServiceLocal.NETWORK_MEASURE_RANK, "Page Rank");
        networkMeasureSelectItems.add(new SelectItem(NetworkDataServiceLocal.NETWORK_MEASURE_RANK, friendlyNameMap.get(NetworkDataServiceLocal.NETWORK_MEASURE_RANK)));
        List parameters = new ArrayList();
        NetworkMeasureParameter d = new NetworkMeasureParameter();
        d.setName("d");
        d.setDefaultValue(".85");
        parameters.add(d);
        networkMeasureParameterMap.put(NetworkDataServiceLocal.NETWORK_MEASURE_RANK, parameters);
                
        friendlyNameMap.put(NetworkDataServiceLocal.NETWORK_MEASURE_DEGREE, "Degree");
        networkMeasureSelectItems.add(new SelectItem(NetworkDataServiceLocal.NETWORK_MEASURE_DEGREE, friendlyNameMap.get(NetworkDataServiceLocal.NETWORK_MEASURE_DEGREE)));

        friendlyNameMap.put(NetworkDataServiceLocal.NETWORK_MEASURE_UNIQUE_DEGREE, "Unique Degree");
        networkMeasureSelectItems.add(new SelectItem(NetworkDataServiceLocal.NETWORK_MEASURE_UNIQUE_DEGREE, friendlyNameMap.get(NetworkDataServiceLocal.NETWORK_MEASURE_UNIQUE_DEGREE)));

        friendlyNameMap.put(NetworkDataServiceLocal.NETWORK_MEASURE_IN_LARGEST, "In Largest Component");
        networkMeasureSelectItems.add(new SelectItem(NetworkDataServiceLocal.NETWORK_MEASURE_IN_LARGEST, friendlyNameMap.get(NetworkDataServiceLocal.NETWORK_MEASURE_IN_LARGEST)));

        /*
        friendlyNameMap.put(NetworkDataServiceLocal.NETWORK_MEASURE_BONACICH_CENTRALITY, "Bonacich Centrality");
        networkMeasureSelectItems.add(new SelectItem(NetworkDataServiceLocal.NETWORK_MEASURE_BONACICH_CENTRALITY, friendlyNameMap.get(NetworkDataServiceLocal.NETWORK_MEASURE_BONACICH_CENTRALITY)));
        parameters = new ArrayList();
        NetworkMeasureParameter p1 = new NetworkMeasureParameter();
        p1.setName("alpha");
        p1.setDefaultValue("1");
        parameters.add(p1);

        NetworkMeasureParameter p2 = new NetworkMeasureParameter();
        p2.setName("exo");
        p2.setDefaultValue("1");
        parameters.add(p2);
        networkMeasureParameterMap.put(NetworkDataServiceLocal.NETWORK_MEASURE_BONACICH_CENTRALITY, parameters);
        */
        networkMeasureParamterList = networkMeasureParameterMap.get(networkMeasureType);

        // and finally, add the initial event
        events.add(getInitialEvent());
    }

    private NetworkDataAnalysisEvent getInitialEvent() {
        NetworkDataAnalysisEvent initialEvent = new NetworkDataAnalysisEvent();
        initialEvent.setLabel("Initial State");
        initialEvent.setVertices(file.getVertexDataTable().getCaseQuantity());
        initialEvent.setEdges(file.getEdgeDataTable().getCaseQuantity());
        return initialEvent;
    }

    public StudyUI getStudyUI() {
        return studyUI;
    }

    public void setStudyUI(StudyUI studyUI) {
        this.studyUI = studyUI;
    }

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

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }


    public String getActionType() {
        return actionType;
    }

    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    // helper methods for checking action type
    public boolean isManualQueryAction() {
        return "manualQuery".equals(actionType);
    }
    

    public boolean isAutomaticQueryAction() {
        return "automaticQuery".equals(actionType);
    }

    public boolean isNetworkMeasureAction() {
        return "networkMeasure".equals(actionType);
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

    public boolean isCanUndo() {
        return canUndo;
    }

    public void setCanUndo(boolean canUndo) {
        this.canUndo = canUndo;
    }



    public UIComponent getAutomaticQueryError() {
        return automaticQueryError;
    }

    public void setAutomaticQueryError(UIComponent automaticQueryError) {
        this.automaticQueryError = automaticQueryError;
    }

    public UIComponent getManualQueryError() {
        return manualQueryError;
    }

    public void setManualQueryError(UIComponent manualQueryError) {
        this.manualQueryError = manualQueryError;
    }

    public UIComponent getNetworkMeasureError() {
        return networkMeasureError;
    }

    public void setNetworkMeasureError(UIComponent networkMeasureError) {
        this.networkMeasureError = networkMeasureError;
    }

    public HtmlDataTable getEventTable() {
        return eventTable;
    }

    public void setEventTable(HtmlDataTable eventTable) {
        this.eventTable = eventTable;
    }


 

    public List<NetworkDataAnalysisEvent> getEvents() {
        return events;
    }

    public void setEvents(List<NetworkDataAnalysisEvent> events) {
        this.events = events;
    }

    public boolean isDownloadInProgress() {
        System.out.println("downloadInProgress is called by IS "+ downloadInProgress);
        return downloadInProgress;
    }

    public void setDownloadInProgress(boolean downloadInProgress) {
        this.downloadInProgress = downloadInProgress;
    }
    

    public List<SelectItem> getAttributeSelectItems() {
        if (DataTable.TYPE_VERTEX.equals(manualQueryType)) {
            return vertexAttributeSelectItems;
        } else if (DataTable.TYPE_EDGE.equals(manualQueryType)) {
            return edgeAttributeSelectItems;
        }

        return new ArrayList();
    }

    public List<SelectItem> getAutomaticQuerySelectItem() {
        return automaticQuerySelectItems;
    }

    public List<SelectItem> getNetworkMeasureSelectItems() {
        return networkMeasureSelectItems;
    }

    public List<NetworkMeasureParameter> getNetworkMeasureParameterList() {
        return networkMeasureParamterList;
    }


    public void networkMeasureSelect_action(ValueChangeEvent e) {
        networkMeasureParamterList = networkMeasureParameterMap.get(e.getNewValue().toString());
    }

    public String manualQuery_action() {
        try {
            NetworkDataSubsetResult result = networkDataService.runManualQuery( manualQueryType, manualQuery, eliminateDisconnectedVertices );
            NetworkDataAnalysisEvent event = new NetworkDataAnalysisEvent();
            event.setLabel("Manual Query");
            event.setAttributeSet(manualQueryType);
            event.setQuery(manualQuery);
            event.setVertices( result.getVertices() );
            event.setEdges( result.getEdges() );
            events.add(event);
            canUndo=true;

        } catch (SQLException e) {
            FacesMessage message = new FacesMessage("Error executing query: "+e.getMessage());
            getFacesContext().addMessage(manualQueryError.getClientId(getFacesContext()), message);
        } catch (ConcurrentAccessException a){
             setShowInProgressPopup(true); 
        }
        
        return "";
    }

    public String automaticQuery_action() {

        try {
            int queryValue = 1;
            if (!StringUtil.isEmpty(automaticQueryNthValue)) {
                queryValue = Integer.valueOf(automaticQueryNthValue).intValue();
            }
            NetworkDataSubsetResult result = networkDataService.runAutomaticQuery( automaticQueryType, queryValue);

            NetworkDataAnalysisEvent event = new NetworkDataAnalysisEvent();
            event.setLabel("Automatic Query");
            event.setAttributeSet("N/A");
            event.setQuery(friendlyNameMap.get(automaticQueryType) + " (" + (StringUtil.isEmpty(automaticQueryNthValue) ? "1" : automaticQueryNthValue) + ")");
            event.setVertices( result.getVertices() );
            event.setEdges( result.getEdges() );
            events.add(event);
            canUndo = true;
            
       } catch (ConcurrentAccessException a){
             setShowInProgressPopup(true); 
       } 
        catch (Exception e) {
                FacesMessage message = new FacesMessage(e.getMessage());
                getFacesContext().addMessage(automaticQueryError.getClientId(getFacesContext()), message);
       }


        return null;
    }

    public String networkMeasure_action() {

        try {
            String result = networkDataService.runNetworkMeasure( networkMeasureType, networkMeasureParamterList);

            NetworkDataAnalysisEvent event = new NetworkDataAnalysisEvent();
            event.setLabel("Network Measure");
            event.setAttributeSet("N/A");
            event.setQuery(friendlyNameMap.get(networkMeasureType) + " ("+ getNetworkMeasureParametersAsString(networkMeasureParamterList) + ")");
            event.setVertices( getLastEvent().getVertices() );
            event.setEdges( getLastEvent().getEdges() );
            event.setAddedAttribute(result); // in case we need to undo later
            events.add(event);

            // add measure to attributeList
            vertexAttributeSelectItems.add(new SelectItem(result));
            canUndo = true;

        } catch (ConcurrentAccessException a){
             setShowInProgressPopup(true); 
        } catch (Exception e) {
                FacesMessage message = new FacesMessage(e.getMessage());
                getFacesContext().addMessage(networkMeasureError.getClientId(getFacesContext()), message);
        }

        return null;
    }

    public String restart_action() throws Exception {
        //reinit workspace and clear events
        try {
            networkDataService.resetAnalysis();
        } catch (ConcurrentAccessException a){
             setShowInProgressPopup(true); 
        }
        
        
        events.clear();
        events.add(getInitialEvent());
        canUndo = false;

        return null;
    }

    public String undo_action() throws Exception {
        networkDataService.undoLastEvent();
        NetworkDataAnalysisEvent lastEvent = getLastEvent();
        if (lastEvent.getAddedAttribute() != null) {
            for (SelectItem selectItem : vertexAttributeSelectItems) {
                if (lastEvent.getAddedAttribute().equals( selectItem.getValue() ) ) {
                    vertexAttributeSelectItems.remove(selectItem);
                    break;
                }

            }
        }

        events.remove( getLastEvent() );
        canUndo = false;

        return null;
    }

    public Resource getSubsetResource() {
        return new RFileResource( getVDCRequestBean().getCurrentVDCId() );
    }

    public Resource getSubsetResourceGraphML() {
        return new RFileResource( getVDCRequestBean().getCurrentVDCId(), true, false );
    }

    public Resource getSubsetResourceTabular() {
        return new RFileResource( getVDCRequestBean().getCurrentVDCId(), false, true );
    }

    public String getSubsetFileName() {
        return "subset_" + FileUtil.replaceExtension(file.getFileName(),"zip");
    }

    private String getNetworkMeasureParametersAsString(List<NetworkMeasureParameter> parameterLists) {
        String returnString = "";
        if (parameterLists != null) {
            for (NetworkMeasureParameter parameter : parameterLists) {
                if (!"".equals(returnString)) {
                    returnString += "; ";
                }
                returnString += parameter.getName() + " = ";
                returnString += !StringUtil.isEmpty(parameter.getValue()) ? parameter.getValue() : parameter.getDefaultValue();
            }
        }
        return returnString;
    }

    public NetworkDataAnalysisEvent getLastEvent() {
        return events.get( events.size() - 1);
    }
    
    public boolean isShowInProgressPopup() {
        return showInProgressPopup;
    }

    public void setShowInProgressPopup(boolean showInProgressPopup) {
        this.showInProgressPopup = showInProgressPopup;
    }
    
    public void togglePopup(javax.faces.event.ActionEvent event) {
         showInProgressPopup = !showInProgressPopup;
    }

    public class NetworkDataAnalysisEvent {

        private String label;
        private String attributeSet;
        private String query;
        private long edges;
        private long vertices;
        private String addedAttribute;

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

        public String getAddedAttribute() {
            return addedAttribute;
        }

        public void setAddedAttribute(String addedAttribute) {
            this.addedAttribute = addedAttribute;
        }

        
    }
    
    //
    class IOExceptionInProgress extends IOException{
          private int id; // a unique id

            private String message; // a detailed message 

  
  
  public IOExceptionInProgress(int id, 
    String message) {
    this.id        = id;
    this.message   = message;
  }
        
    }

    // resource class which doesn't create the file (called via R) until the open method is called (ie the download button is pressed)
    class RFileResource implements Resource, Serializable{
        File file;
        Long vdcId;
        boolean getGraphML = false;
        boolean getTabular = false;

        public RFileResource(Long vdcId) {
            this.vdcId = vdcId;
            getGraphML = true;
            getTabular = true;
        }

        public RFileResource(Long vdcId, boolean getGraphML, boolean getTabular) {
            this.vdcId = vdcId;
            this.getGraphML = getGraphML;
            this.getTabular = getTabular;
        }

        public String calculateDigest() {
            return file != null ? file.getPath() : null;
        }

        public Date lastModified() {
            return file != null ? new Date(file.lastModified()) : null;
        }

        public InputStream open() throws IOException {

            try {
                file = networkDataService.getSubsetExport(getGraphML, getTabular);
            } catch (ConcurrentAccessException a){
               setShowInProgressPopup(true);  
               throw new IOExceptionInProgress(599, "There was a download or query already in progress.  Please wait.");

            } 
            catch (Exception ex) {
                Logger.getLogger(NetworkDataAnalysisPage.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOException("There was a problem attempting to get the export file");
            }
            
            StudyFile sfile = (NetworkDataFile) studyFileService.getStudyFile(fileId);
            if (sfile != null) {
                GuestBookResponse guestbookResponse = (GuestBookResponse) getVDCSessionBean().getGuestbookResponseMap().get("guestBookResponse_" + sfile.getStudy().getId());

                if (guestbookResponse == null) {
                    //need to set up dummy network response
                    guestbookResponse = guestBookResponseServiceBean.initNetworkGuestBookResponse(sfile.getStudy(), sfile, getVDCSessionBean().getLoginBean());
                }
                
                String formatRequested = "Network Subsetting - ";
                if (getGraphML){
                    formatRequested += "GraphML";
                    if (getTabular) formatRequested += "/Tabular";
                } else {
                    if (getTabular) formatRequested += "Tabular";
                }
                guestbookResponse.setDownloadtype(formatRequested);
                String[] stringArray = getVDCSessionBean().toString().split("@");
                String sessiodId = stringArray[1];
                if (FacesContext.getCurrentInstance() != null) {
                    sessiodId = FacesContext.getCurrentInstance().getExternalContext().getSession(false).toString();
                }
                guestbookResponse.setSessionId(sessiodId);
                studyService.incrementNumberOfDownloads(fileId, vdcId, (GuestBookResponse) guestbookResponse);
            } else {
                studyService.incrementNumberOfDownloads(fileId, vdcId, (GuestBookResponse) null);
            }
            return new FileInputStream(file);              
        }

        public void withOptions(Options arg0) throws IOException {
        }
    }
}
