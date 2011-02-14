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

import java.util.*;
import java.util.logging.*;
import javax.servlet.http.*;
import java.io.*;

/**
 *
 * @author asone
 */
public class AnalysisResultsPage extends VDCBaseBean implements java.io.Serializable  {

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
    public String zipFile;

    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }
    
    public String zipFileName;

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }
    
    
    public String requestedOption;

    public String getRequestedOption() {
        return requestedOption;
    }

    public void setRequestedOption(String requestedOption) {
        this.requestedOption = requestedOption;
    }
        
    public String requestResultPID;

    public String getRequestResultPID() {
        return requestResultPID;
    }

    public void setRequestResultPID(String requestResultPID) {
        this.requestResultPID = requestResultPID;
    }
    
    public Map<String, String> resultInfo = new HashMap<String, String>();
    
    public Map<String, String> getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(Map<String, String> resultInfo) {
        this.resultInfo = resultInfo;
    }
    
    public String offlineCitation;

    public String getOfflineCitation() {
        return offlineCitation;
    }

    public void setOfflineCitation(String offlineCitation) {
        this.offlineCitation = offlineCitation;
    }
    
    public String studyTitle;

    public String getStudyTitle() {
        return studyTitle;
    }

    public void setStudyTitle(String studyTitle) {
        this.studyTitle = studyTitle;
    }
    
    public String fileUNF;

    public String getFileUNF() {
        return fileUNF;
    }

    public void setFileUNF(String fileUNF) {
        this.fileUNF = fileUNF;
    }
    
    public String variableList;

    public String getVariableList() {
        return variableList;
    }

    public void setVariableList(String variableList) {
        this.variableList = variableList;
    }
    
    public String rversion ;

    public String getRversion() {
        return rversion;
    }

    public void setRversion(String rversion) {
        this.rversion = rversion;
    }
    
    public String rexecDate;

    public String getRexecDate() {
        return rexecDate;
    }

    public void setRexecDate(String rexecDate) {
        this.rexecDate = rexecDate;
    }
    
    public String zeligVersion;

    public String getZeligVersion() {
        return zeligVersion;
    }

    public void setZeligVersion(String zeligVersion) {
        this.zeligVersion = zeligVersion;
    }    
    
    // PanelGroups
    
    
    public String resultURLdwnld;
    
    public String getResultURLdwnld() {
        return resultURLdwnld;
    }

    public void setResultURLdwnld(String resultURLdwnld) {
        this.resultURLdwnld = resultURLdwnld;
    }
    
    public String resultURLhtml;

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

    
    public HtmlPanelGroup pgDwnld = new HtmlPanelGroup() ;

    public HtmlPanelGroup getPgDwnld() {
        return pgDwnld;
    }

    
    public void setPgDwnld(HtmlPanelGroup pgDwnld) {
        this.pgDwnld = pgDwnld;
    }
    
    public HtmlPanelGroup pgHtml  = new HtmlPanelGroup();

    public HtmlPanelGroup getPgHtml() {
        return pgHtml;
    }
    
    
    
    public void setPgHtml(HtmlPanelGroup pgHtml) {
        this.pgHtml = pgHtml;
    }
    
    public HtmlPanelGroup pgRwrksp = new HtmlPanelGroup();

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




    
}
