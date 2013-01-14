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

import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.faces.component.UIComponent.*;
import javax.faces.context.*;
import javax.faces.FacesException;
import javax.faces.render.ResponseStateManager;

import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.context.ByteArrayResource;
import com.icesoft.faces.context.Resource;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import java.util.*;
import java.util.logging.*;
import javax.servlet.http.*;
import java.io.*;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author asone
 */
@ViewScoped
@Named("AnalysisResultsPage")
public class AnalysisResultsPage extends VDCBaseBean implements java.io.Serializable  {

    @EJB StudyServiceLocal studyService;
    @EJB VariableServiceLocal varService;
    
    /** Sets the logger (use the package name) */
    private static Logger dbgLog = Logger.getLogger(AnalysisResultsPage.class.getPackage().getName());
    
    private static Map<String, String> OPTION_NAME_TABLE = new HashMap<String, String>();
    
    static {
    
        OPTION_NAME_TABLE.put("download", "Download Subset");
        OPTION_NAME_TABLE.put("eda",      "Descriptive Statistics");
        OPTION_NAME_TABLE.put("xtab",     "Advanced Statistical Analysis");
        OPTION_NAME_TABLE.put("zelig",    "Advanced Statistical Analysis");
        
    }
    
    private Resource zipResourceDynFileName;

    public Resource getZipResourceDynFileName() {
        return zipResourceDynFileName;
    }

    public void setZipResourceDynFileName(Resource zipResourceDynFileName) {
        this.zipResourceDynFileName = zipResourceDynFileName;
    }
    
    /**
     * The ID of the requested DataTable instance. Specified as a managed-property in
     * managed-beans.xml <property-class>java.lang.Long</property-class>
     * <value>#{param.dtId}</value>
     */
    private String dtId;

    /**
     * Getter for property dtId
     * 
     * @return    value of property dtId
     */
    public String getDtId() {
        return this.dtId;
    }

    /**
     * Setter for property dtId.
     * 
     * @param dtId    new value of property dtId
     *           
     */
    public void setDtId(String dtId) {
        this.dtId = dtId;
    }

    private String versionNumber;

    public String getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(String vn) {
        this.versionNumber = vn;
    }
    private String zipFile;

    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }
    
    private String zipFileName;

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }
    
    
    private String requestedOption;

    public String getRequestedOption() {
        return requestedOption;
    }

    public void setRequestedOption(String requestedOption) {
        this.requestedOption = requestedOption;
    }
        
    private String requestResultPID;

    public String getRequestResultPID() {
        return requestResultPID;
    }

    public void setRequestResultPID(String requestResultPID) {
        this.requestResultPID = requestResultPID;
    }
    
    private Map<String, String> resultInfo = new HashMap<String, String>();
    
    public Map<String, String> getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(Map<String, String> resultInfo) {
        this.resultInfo = resultInfo;
    }
    
    private String offlineCitation;

    public String getOfflineCitation() {
        return offlineCitation;
    }

    public void setOfflineCitation(String offlineCitation) {
        this.offlineCitation = offlineCitation;
    }
    
    private String studyTitle;

    public String getStudyTitle() {
        return studyTitle;
    }

    public void setStudyTitle(String studyTitle) {
        this.studyTitle = studyTitle;
    }
    
    private String fileUNF;

    public String getFileUNF() {
        return fileUNF;
    }

    public void setFileUNF(String fileUNF) {
        this.fileUNF = fileUNF;
    }
    
    private String variableList;

    public String getVariableList() {
        return variableList;
    }

    public void setVariableList(String variableList) {
        this.variableList = variableList;
    }
    
    private String rversion ;

    public String getRversion() {
        return rversion;
    }

    public void setRversion(String rversion) {
        this.rversion = rversion;
    }
    
    private String rexecDate;

    public String getRexecDate() {
        return rexecDate;
    }

    public void setRexecDate(String rexecDate) {
        this.rexecDate = rexecDate;
    }
    
    private String zeligVersion;

    public String getZeligVersion() {
        return zeligVersion;
    }

    public void setZeligVersion(String zeligVersion) {
        this.zeligVersion = zeligVersion;
    }    
    
    // PanelGroups
    
    
    private String resultURLdwnld;
    
    public String getResultURLdwnld() {
        return resultURLdwnld;
    }

    public void setResultURLdwnld(String resultURLdwnld) {
        this.resultURLdwnld = resultURLdwnld;
    }
    
    private String resultURLhtml;

    public String getResultURLhtml() {
        return resultURLhtml;
    }

    public void setResultURLhtml(String resultURLhtml) {
        this.resultURLhtml = resultURLhtml;
    }

    public boolean isVersionNumberSupplied() {
        return !(versionNumber == null || versionNumber.equals(""));
    }
/*
    public String resultURLRworkspace;

    public String getResultURLRworkspace() {
        return resultURLRworkspace;
    }

    public void setResultURLRworkspace(String resultURLRworkspace) {
        this.resultURLRworkspace = resultURLRworkspace;
    }
*/

    
    private HtmlPanelGroup pgDwnld = new HtmlPanelGroup() ;

    public HtmlPanelGroup getPgDwnld() {
        return pgDwnld;
    }

    
    public void setPgDwnld(HtmlPanelGroup pgDwnld) {
        this.pgDwnld = pgDwnld;
    }
    
    private HtmlPanelGroup pgHtml  = new HtmlPanelGroup();

    public HtmlPanelGroup getPgHtml() {
        return pgHtml;
    }
    
    
    
    public void setPgHtml(HtmlPanelGroup pgHtml) {
        this.pgHtml = pgHtml;
    }
    
    private HtmlPanelGroup pgRwrksp = new HtmlPanelGroup();

    public HtmlPanelGroup getPgRwrksp() {
        return pgRwrksp;
    }

    public void setPgRwrksp(HtmlPanelGroup pgRwrksp) {
        this.pgRwrksp = pgRwrksp;
    }
    
    // msgDwnldButton:ui:StaticText@binding
    private HtmlOutputText msgDwnldButton = new HtmlOutputText();

    public HtmlOutputText getMsgDwnldButton() {
        return msgDwnldButton;
    }

    public void setMsgDwnldButton(HtmlOutputText txt) {
        this.msgDwnldButton = txt;
    }
    
    /**
     * The object backing the value attribute of
     * ui:StaticText for msgDwnldButton that shows 
     * error messages for the action of the download
     * button
     */
    private String msgDwnldButtonTxt;

    /**
     * Getter for property msgDwnldButtonTxt
     *
     * @return the error message text
     */
    public String getMsgDwnldButtonTxt() {
        return msgDwnldButtonTxt;
    }
    /**
     * Setter for property msgDwnldButtonTxt
     *
     * @param txt   the new error message text
     */
    public void setMsgDwnldButtonTxt(String txt) {
        this.msgDwnldButtonTxt = txt;
    }
    
    public String getReplicationPack(){
    
            FacesContext cntxt = FacesContext.getCurrentInstance();

            HttpServletResponse res = (HttpServletResponse) cntxt
                .getExternalContext().getResponse();
            HttpServletRequest req = (HttpServletRequest) cntxt
                .getExternalContext().getRequest();


            // zipping all required files
            try{
                FileInputStream in = new FileInputStream(zipFile);
                
                res.setContentType("application/zip");

                res.setHeader("content-disposition", "attachment; filename=" + zipFileName);

                OutputStream out = res.getOutputStream();

                byte[] buf = new byte[1024];
                int count = 0;
                while ((count = in.read(buf)) >= 0) {
                    out.write(buf, 0, count);
                }
                in.close();
                out.close();

                FacesContext.getCurrentInstance().responseComplete();

                dbgLog.fine("***** within AnalysisResultsPage: getReplicationPack(): ends here *****");

                return "success";

            } catch (IOException e){
                // file-access problem, etc.
                e.printStackTrace();
                dbgLog.fine("download zip file IO exception");
                msgDwnldButton.setValue("* an IO problem occurred during downloading");
                msgDwnldButton.setVisible(true);
                dbgLog.warning("exiting dwnldAction() due to an IO problem ");
                getVDCRequestBean().setSelectedTab("tabDwnld");

                return "failure";
            }
            
    }
    
    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     *
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    public void init() {
        // Perform initializations inherited from our superclass
        super.init();
// Perform application initialization that must complete
        // *before* managed components are initialized
        // TODO - add your own initialiation code here

// <editor-fold defaultstate="collapsed" desc="Visual-Web-managed Component Initialization">
// Initialize automatically managed components
        // *Note* - this logic should NOT be modified
        try {
            _init();
            
            // gets the FacesContext instance
            FacesContext cntxt = FacesContext.getCurrentInstance();
            
            // gets the ExternalContext
            ExternalContext exCntxt = FacesContext.getCurrentInstance()
                .getExternalContext();
            
            // gets session data from the ExternalContext
            Map<String, Object> sessionMap = exCntxt.getSessionMap();
            
            if (true){
                dbgLog.fine("\ncontents of RequestParameterMap:\n"
                    + exCntxt.getRequestParameterMap());
                dbgLog.fine("\ncontents of SessionMap:\n"+sessionMap);
            }
            
            // gets the request header data
            Map<String, String> rqustHdrMp = exCntxt.getRequestHeaderMap();
            
            if (true){
                dbgLog.fine("\nRequest Header Values Map:\n" + rqustHdrMp);
                dbgLog.fine("\nRequest Header Values Map(user-agent):"
                    + rqustHdrMp.get("user-agent"));
            }
            

            // gets the current view state value (for post-back-checking)
            String currentViewStateValue = exCntxt.getRequestParameterMap()
                .get(ResponseStateManager.VIEW_STATE_PARAM);
            
            if (true){
                dbgLog.fine("ViewState value=" + currentViewStateValue);
                dbgLog.fine("VDCRequestBean: current VDC URL ="
                + getVDCRequestBean().getCurrentVDCURL());
            }
            
            resultInfo = (Map<String, String>) sessionMap.get("resultInfo");
            
            dbgLog.fine("resultInfo from the subsetting Page:\n"+resultInfo);
            
            if (resultInfo.containsKey("PID")){
                requestResultPID = resultInfo.get("PID");
            } else {
                requestResultPID = "";
            }
            if (resultInfo.containsKey("option")){
                requestedOption  = OPTION_NAME_TABLE.get( resultInfo.get("option") );
            }
            
            // citation
            // dbgLog.fine("**** resultInfo from the subsetting Page ****:\n"+resultInfo);
            offlineCitation = resultInfo.get("offlineCitation");
            studyTitle      = resultInfo.get("studyTitle");
            fileName        = resultInfo.get("fileName");
            dtId            = resultInfo.get("dtId");
            versionNumber   = resultInfo.get("versionNumber");
            fileUNF         = resultInfo.get("fileUNF");
            variableList    = resultInfo.get("variableList");
            zeligVersion    = resultInfo.get("zeligVersion");
            rversion        = resultInfo.get("Rversion");
            rexecDate       = resultInfo.get("RexecDate");
            zipFile         = resultInfo.get("replicationZipFile");
            zipFileName = resultInfo.get("replicationZipFileName");
            dbgLog.fine("zeligVersion="+zeligVersion);
            dbgLog.fine("rVersion="+rversion);
            dbgLog.fine("zipFile="+zipFile);
            dbgLog.fine("zipFileName="+zipFileName);
            zipResourceDynFileName = new ByteArrayResource( toByteArray( new FileInputStream(zipFile)));

            
            String dsbUrl = "http://"+resultInfo.get("dsbHost");
            if (resultInfo.containsKey("dsbPort") && (!resultInfo.get("dsbPort").equals("")) ){
               dsbUrl += ":"+ resultInfo.get("dsbPort"); 
            }
            /*
            resultURLRworkspace = dsbUrl+
                              resultInfo.get("dsbContextRootDir") +
                              resultInfo.get("Rdata");
            */
                              
                              
            if (resultInfo.get("option").equals("download")){
                // show the panelgroup
                pgDwnld.setRendered(true);
                pgHtml.setRendered(false);
                pgRwrksp.setRendered(false);
            } else {
                pgDwnld.setRendered(false);
                pgHtml.setRendered(true);
                pgRwrksp.setRendered(true);
            }

                // urls

            FacesContext mycntxt = FacesContext.getCurrentInstance();
            HttpServletRequest myreq = (HttpServletRequest) mycntxt.getExternalContext().getRequest();

	    String serverPrefix = myreq.getScheme() + "://"
                + myreq.getServerName() + ":" + myreq.getServerPort();

	    
            if (resultInfo.get("option").equals("download")){
                resultURLdwnld = dsbUrl+
                              resultInfo.get("dsbContextRootDir") +
                              resultInfo.get("subsetfile");
                dbgLog.fine("subset url="+resultURLdwnld);         
            } else if (resultInfo.get("option").equals("eda") ||
                resultInfo.get("option").equals("xtab")) {
                //resultURLhtml = dsbUrl+
		//resultInfo.get("dsbContextRootDir") +
		//resultInfo.get("html");

		

		resultURLhtml = serverPrefix+"/temp/" + 
		    resultInfo.get("html"); 
                
                dbgLog.fine("hmtl url="+ resultURLhtml);
            } else if (resultInfo.get("option").equals("zelig")) {
                //resultURLhtml = dsbUrl+
		//resultInfo.get("dsbContextRootDir") +
		//resultInfo.get("html");

		resultURLhtml = serverPrefix+"/temp/" + 
		    resultInfo.get("html"); 

                dbgLog.fine("html url="+ resultURLhtml);
            }
            
            
                setMsgDwnldButtonTxt("");
                msgDwnldButton.setVisible(false);
            
        } catch (Exception e) {
            dbgLog.severe(e.getMessage());
            throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
        }
    }


    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int len = 0;
        while ((len = input.read(buf)) > -1) output.write(buf, 0, len);
        return output.toByteArray();
    }


    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }

    /**
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
    }

    /**
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
    }

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() {
    }


    // used for the title fragment
    private StudyUI studyUI;
    private String fileName;

    public String getFileName() {
        return fileName;
    }
    
    public StudyUI getStudyUI() {
        if (studyUI == null) {
            Study thisStudy = varService.getDataTable(new Long(dtId)).getStudyFile().getStudy();
            VDCUser user = (getVDCSessionBean().getLoginBean() != null) ? getVDCSessionBean().getLoginBean().getUser() : null;

            if (versionNumber == null) {
                studyUI = new StudyUI(thisStudy);
            } else {
                StudyVersion thisStudyVersion = thisStudy.getStudyVersionByNumber(new Long(versionNumber));

                if (thisStudyVersion == null) {
                    dbgLog.severe("ERROR: Could not find a valid Version of this study");
                    throw new FacesException("Could not find a valid Version of this study, studyId = " + thisStudy.getId() + " versionNumber= " + versionNumber);
                }
                studyUI = new StudyUI(thisStudyVersion, user);
            }
        }

        return studyUI;
    }

}
