/*
 * TermsOfUseFilter.java
 *
 * Created on December 13, 2006, 2:07 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.study.DataTable;
import edu.harvard.hmdc.vdcnet.study.FileCategory;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCSessionBean;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import javax.servlet.*;

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
    
    public TermsOfUseFilter() {
    }

    @EJB VDCServiceLocal vdcService;   
    @EJB StudyServiceLocal studyService;
    @EJB VariableServiceLocal variableService;
    
    
    public static boolean isVdcTermsRequired(Study study, Map termsOfUseMap) {
        boolean vdcTermsRequired = study.getOwner().isTermsOfUseEnabled();
        if (vdcTermsRequired) {
            return termsOfUseMap.get("vdc_" + study.getOwner().getId() ) == null;
        }
        
        return false;
    }

    public static boolean isStudyTermsRequired(Study study, Map termsOfUseMap) {
        boolean studyTermsRequired = study.isTermsOfUseEnabled();
        if (studyTermsRequired) {
            return termsOfUseMap.get("study_" + study.getId() ) == null;
        }
        
        return false;
    }  
    
    private Map getTermsOfUseMap(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            VDCSessionBean vdcSession = (VDCSessionBean)session.getAttribute("VDCSession");
            if (vdcSession!=null) {
                if (vdcSession.getLoginBean() != null) {
                    return vdcSession.getLoginBean().getTermsfUseMap();
                } else {
                    return vdcSession.getTermsfUseMap();
                } 
            }       
       }
        
        return new HashMap();
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
        
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        String requestPath = req.getPathInfo();

        Study study = null;        
        
        if ( req.getServletPath().equals("/FileDownload") ) {
            String fileId = req.getParameter("fileId");
            String catId = req.getParameter("catId");
            String studyId = req.getParameter("studyId");
            
            if (fileId != null) {
		
                StudyFile file = null; 
		try {
		    file = studyService.getStudyFile( new Long(fileId));       
		} catch (Exception ex) {
                    if (ex.getCause() instanceof IllegalArgumentException) {
			// do nothing.
			// if the file does not exist, there sure 
			// isn't a license/terms of use for it!
		    } else {
			ex.printStackTrace();
			return; 
		    }
		}
		
		if ( file != null ) {
		    study = file.getFileCategory().getStudy();
		}
            } else if (catId != null) {
                FileCategory cat = studyService.getFileCategory( new Long(catId)); 
                study = cat.getStudy();
            } else if (studyId != null) {
                study = studyService.getStudy( new Long(studyId));
            }  
        } else if ( req.getServletPath().equals("/faces") ) {
            if (requestPath.startsWith("/subsetting/SubsettingPage")) {
                String dtId = req.getParameter("dtId");
                if (dtId != null) {
                    DataTable dt = variableService.getDataTable( new Long(dtId));
                    study = dt.getStudyFile().getFileCategory().getStudy();
                }

            }
        }
        
        // if we've populate the study, then check the TermsOfUse'
        if (study != null) {

	    // the code below is for determining if the request is from 
	    // our registered DSB host; (then no agreement form should be 
	    // displayed!)
	    // this logic is essentially cut-and-pasted from 
	    // FileDownloadServlet.java, where I added it earlie this year. 

	    String dsbHost = System.getProperty("vdc.dsb.url");

	    boolean NOTaDSBrequest = true;

	    if ( dsbHost.equals(req.getRemoteHost()) ) {
		NOTaDSBrequest = false; 
	    } else { 
		try {
		    String dsbHostIPAddress = InetAddress.getByName(dsbHost).getHostAddress(); 
		    if ( dsbHostIPAddress.equals(req.getRemoteHost()) ) {
			NOTaDSBrequest = false;
		    }
		} catch ( UnknownHostException ex ) {
		    // do nothing; 
		    // the "vdc.dsb.url" setting is clearly misconfigured,
		    // so we just keep assuming this is NOT a DSB call
		}
	    }

	    if ( NOTaDSBrequest ) {
		Map termsOfUseMap = getTermsOfUseMap(req);
		if ( isVdcTermsRequired(study, termsOfUseMap) || isStudyTermsRequired(study, termsOfUseMap) ) {
		    VDC currentVDC = vdcService.getVDCFromRequest(req);   
		    String params = "?studyId=" + study.getId();
		    params += "&redirectPage=" + req.getServletPath() + req.getPathInfo() +"?" + req.getQueryString();
		    if (currentVDC!=null) {
			params+="&vdcId="+currentVDC.getId();
		    }   
		    res.sendRedirect(req.getContextPath()+"/faces/study/TermsOfUsePage.jsp" + params);
		    return; // don't continue with chain since we are redirecting'
		}     
	    }
        }
        
        Throwable problem = null;
        
        try {
            chain.doFilter(request, response);
        }
        catch(Throwable t) {
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
            if (problem instanceof ServletException) throw (ServletException)problem;
            if (problem instanceof IOException) throw (IOException)problem;
            sendProcessingError(problem, response);
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
        
        if (filterConfig == null) return ("TermsOfUseFilter()");
        StringBuffer sb = new StringBuffer("TermsOfUseFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
        
    }
    
    
    
    private void sendProcessingError(Throwable t, ServletResponse response) {
        
        String stackTrace = getStackTrace(t);
        
        if(stackTrace != null && !stackTrace.equals("")) {
            
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
                response.getOutputStream().close();;
            }
            
            catch(Exception ex){ }
        }
else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();;
            }
catch(Exception ex){ }
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
        }
catch(Exception ex) {}
        return stackTrace;
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }
    
    private static final boolean debug = true;
}
