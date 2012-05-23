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
 * TermsOfUseFilter.java
 *
 * Created on December 13, 2006, 2:07 PM
 */
package edu.harvard.iq.dvn.core.web.servlet;

import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCSessionBean;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import java.util.StringTokenizer;
import javax.ejb.EJB;


import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** 
 *
 * @author  gdurand
 * @version
 */
public class TermsOfUseFilter implements Filter {

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured.
    private FilterConfig filterConfig = null;
    public static String TOU_DOWNLOAD="download";
    public static String TOU_DEPOSIT="deposit";

    public TermsOfUseFilter() {
    }
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    VariableServiceLocal variableService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @EJB
    StudyFileServiceLocal studyFileService;
    
    @Inject VDCSessionBean vdcSession;

    public static boolean isDownloadDataverseTermsRequired(Study study, Map termsOfUseMap) {
        boolean vdcTermsRequired = study.getOwner().isDownloadTermsOfUseEnabled();
        if (vdcTermsRequired) {
            return termsOfUseMap.get("vdc_download_" + study.getOwner().getId()) == null;
        }
        return false;
    }
    
    public static boolean isGuestbookRequired(Study study, Map termsOfUseMap) {
        boolean vdcTermsRequired = study.getOwner().getGuestBookQuestionnaire().isEnabled();
        if (vdcTermsRequired) {
            return termsOfUseMap.get("study_guestbook_" + study.getOwner().getId()) == null;
        }

        return false;
    }

    public static boolean isDepositDataverseTermsRequired(VDC currentVDC,
            Map termsOfUseMap) {
        boolean vdcTermsRequired = currentVDC.isDepositTermsOfUseEnabled();
        if (vdcTermsRequired) {
            return termsOfUseMap.get("vdc_deposit_" + currentVDC.getId()) == null;
        }

        return false;
    }

    public static boolean isDownloadStudyTermsRequired(Study study, Map termsOfUseMap) {
        boolean studyTermsRequired = study.getReleasedVersion().getMetadata().isTermsOfUseEnabled();
        if (studyTermsRequired) {
            return termsOfUseMap.get("study_download_" + study.getId()) == null;
        }

        return false;
    }

    public static boolean isDownloadDvnTermsRequired(VDCNetwork vdcNetwork, Map termsOfUseMap) {
        boolean dvnTermsRequired = vdcNetwork.isDownloadTermsOfUseEnabled();
        if (dvnTermsRequired) {
            return termsOfUseMap.get("dvn_download") == null;
        }
        return false;
    }
   public static boolean isDepositDvnTermsRequired(VDCNetwork vdcNetwork, Map termsOfUseMap) {
        boolean dvnTermsRequired = vdcNetwork.isDepositTermsOfUseEnabled();
        if (dvnTermsRequired) {
            return termsOfUseMap.get("dvn_deposit") == null;
        }
        return false;
    }

    private Map getTermsOfUseMap() {
        if (vdcSession.getLoginBean() != null) {
            return vdcSession.getLoginBean().getTermsfUseMap();
        } else {
            return vdcSession.getTermsfUseMap();
        }
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param result The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        log("TermsOfUseFilter:doFilter()");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        boolean redirected = false;
        
        if (req.getServletPath().equals("/FileDownload") || (req.getServletPath().equals("/faces") 
                && req.getPathInfo().startsWith("/subsetting/SubsettingPage"))
                || req.getServletPath().equals("/faces") && (req.getPathInfo().startsWith("/viz/ExploreDataPage"))) {
            redirected = checkDownloadTermsOfUse(req, res);
        } else if (req.getServletPath().equals("/faces") && (req.getPathInfo().startsWith("/study/EditStudyPage") || req.getPathInfo().startsWith("/study/AddFilesPage") ) ) {
            redirected = checkDepositTermsOfUse(req, res);
        }
        
        if (!redirected) {
            
            Throwable problem = null;
            
            try {
                chain.doFilter(request, response);
            } catch (Throwable t) {
                //
                // If an exception is thrown somewhere down the filter chain,
                // we still want to execute our after processing, and then
                // rethrow the problem after that.
                //
                problem = t;
                t.printStackTrace();
            }


            //
            // If there was a problem, we want to rethrow it if it is
            // a known type, otherwise log it.
            //
            if (problem != null) {
                if (problem instanceof ServletException) {
                    throw (ServletException) problem;
                }
                if (problem instanceof IOException) {
                    throw (IOException) problem;
                }
                sendProcessingError(problem, response);
            }
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {

        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     *
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     *
     */
    public void init(FilterConfig filterConfig) {

        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("TermsOfUseFilter:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    public String toString() {

        if (filterConfig == null) {
            return ("TermsOfUseFilter()");
        }
        StringBuffer sb = new StringBuffer("TermsOfUseFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());

    }

    private boolean checkDepositTermsOfUse(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        Map termsOfUseMap = getTermsOfUseMap();
        String studyId = req.getParameter("studyId");
        VDC currentVDC = vdcService.getVDCFromRequest(req);
        VDC depositVDC = null;
        if (studyId!=null) {
            depositVDC = studyService.getStudy(Long.parseLong(studyId)).getOwner();
        } else {
            depositVDC = currentVDC;
        }
       if (isDepositDvnTermsRequired(vdcNetworkService.find(), termsOfUseMap) ||(depositVDC!=null  && isDepositDataverseTermsRequired(depositVDC,termsOfUseMap))) {        
            String params = "?tou="+TOU_DEPOSIT;
            params += "&studyId=" + studyId;
            params += "&redirectPage=" + URLEncoder.encode(req.getServletPath() + req.getPathInfo() + "?" + req.getQueryString(), "UTF-8");
            if (currentVDC != null) {
                params += "&vdcId=" + currentVDC.getId();
            }
            res.sendRedirect(req.getContextPath() + "/faces/study/TermsOfUsePage.xhtml" + params);
            return true; // don't continue with chain since we are redirecting'
        }

        return false;
    }

    private boolean checkDownloadTermsOfUse(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        String fileId = req.getParameter("fileId");
        String catId = req.getParameter("catId");
        String studyId = req.getParameter("studyId");
        String versionNumber = req.getParameter("versionNumber");
        String requestPath = req.getPathInfo();
        String imageThumb = req.getParameter("imageThumb");
        Study study = null;
        if (req.getServletPath().equals("/FileDownload")) {
            if (fileId != null) {
                try {
                    // a user can now download a comma delimited list of files; so we have to check the study of each of these
                    // and if any are from a different study, go to error page
                    StringTokenizer st = new StringTokenizer(fileId,",");

                    while (st.hasMoreTokens()) {
                        StudyFile file = studyFileService.getStudyFile(new Long(st.nextToken()));
                        if (study == null) {
                            study = file.getStudy();

                        } else if ( !study.equals(file.getStudy()) ) {
                            res.sendRedirect(req.getContextPath() + "/ExceptionHandler?You may not download multiple files from different studies.");
                            //res.sendRedirect(req.getContextPath() + "/faces/ErrorPage.xhtml");
                            return true; // don't continue with chain since we are redirecting'
                        }
                    }

                } catch (Exception ex) {
                    if (ex.getCause() instanceof IllegalArgumentException) {
                    // do nothing.
                    // if the file does not exist, there sure 
                    // isn't a license/terms of use for it!
                    } else {
                        ex.printStackTrace();
                        return false;
                    }
                }
            } else if (studyId != null) {
                try {
                    study = studyService.getStudy(new Long(studyId));
                } catch (Exception ex) {
                    if (ex.getCause() instanceof IllegalArgumentException) {
                    // do nothing.
                    // if the study does not exist, there sure 
                    // isn't a license/terms of use for it!
                    } else {
                        ex.printStackTrace();
                        return false;
                    }
                }
            }
        } else if (req.getServletPath().equals("/faces")) {
            if (requestPath.startsWith("/subsetting/SubsettingPage")) {
                String dtId = req.getParameter("dtId");
                if (dtId != null) {
                    DataTable dt = variableService.getDataTable(new Long(dtId));
                    study = dt.getStudyFile().getStudy();
                }

            }
            if (requestPath.startsWith("/viz/ExploreDataPage")) {
                String vFileId = req.getParameter("fileId");
                StudyFile file = studyFileService.getStudyFile(new Long(vFileId));
                if (study == null) {
                    study = file.getStudy();
                }
            }
        }

        // if we've populate the study, then check the TermsOfUse'
        // We only need to display the terms if the study is Released.
        if (study.getReleasedVersion() != null) {

            // the code below is for determining if the request is from 
            // our registered DSB host; (then no agreement form should be 
            // displayed!)
            // this logic is essentially cut-and-pasted from 
            // FileDownloadServlet.java, where I added it earlie this year. 

            String dsbHost = System.getProperty("vdc.dsb.host");

            if (dsbHost == null) {
                dsbHost = System.getProperty("vdc.dsb.url");
            }

	    String localHostByName = "localhost"; 
	    String localHostNumeric = "127.0.0.1";

            boolean NOTaDSBrequest = true;

            if ( dsbHost.equals(req.getRemoteHost()) ||
		 localHostByName.equals(req.getRemoteHost()) ||
		 localHostNumeric.equals(req.getRemoteHost()) ) {
                
                NOTaDSBrequest = false;
            } else {
                try {
                    String dsbHostIPAddress = InetAddress.getByName(dsbHost).getHostAddress();
                    if (dsbHostIPAddress.equals(req.getRemoteHost())) {
                        NOTaDSBrequest = false;
                    }
                } catch (UnknownHostException ex) {
                // do nothing; 
                // the "vdc.dsb.host" setting is clearly misconfigured,
                // so we just keep assuming this is NOT a DSB call
                }
            }
	    
	    if ( imageThumb != null ) {
		NOTaDSBrequest = false; 
	    }

            if (NOTaDSBrequest) {
                Map termsOfUseMap = getTermsOfUseMap();
                if (isDownloadDvnTermsRequired(vdcNetworkService.find(), termsOfUseMap) || isDownloadDataverseTermsRequired(study, termsOfUseMap) || isDownloadStudyTermsRequired(study, termsOfUseMap)) {
                    VDC currentVDC = vdcService.getVDCFromRequest(req);
                    String params = "?studyId=" + study.getId();
                    if ( versionNumber != null ) {
                        params += "&versionNumber=" + versionNumber;
                    }
                    if ( fileId != null && !fileId.trim().isEmpty() ) {
                        params += "&fileId=" + fileId;
                    }
                    params += "&redirectPage=" + URLEncoder.encode(req.getServletPath() + req.getPathInfo() + "?" + req.getQueryString(), "UTF-8");
                    params += "&tou="+TOU_DOWNLOAD;
                    if (currentVDC != null) {
                        params += "&vdcId=" + currentVDC.getId();
                    }

                    res.sendRedirect(req.getContextPath() + "/faces/study/TermsOfUsePage.xhtml" + params);
                    return true; // don't continue with chain since we are redirecting'
                }
            }
        }
        return false;
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {

        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {

            try {

                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
                ;
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
                ;
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {

        String stackTrace = null;

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }
    private static final boolean debug = true;
}
