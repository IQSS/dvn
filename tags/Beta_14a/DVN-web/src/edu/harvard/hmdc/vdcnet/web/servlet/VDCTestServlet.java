/*
 * VDCTestServlet.java
 *
 * Created on December 14, 2006, 4:41 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal;
import edu.harvard.hmdc.vdcnet.dsb.DSBWrapper;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.EditStudyService;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.bind.JAXBException;

/**
 *
 * @author gdurand
 * @version
 */
@EJB(name="editStudy", beanInterface=edu.harvard.hmdc.vdcnet.study.EditStudyService.class)
public class VDCTestServlet extends HttpServlet {
    
    @EJB DDI20ServiceLocal ddiService;
    @EJB StudyServiceLocal studyService;
    @EJB IndexServiceLocal indexService;
    @EJB VariableServiceLocal varService;
    
    public static final int TEST_ZELIG_CONFIG = 0;
    public static final int TEST_MAP_DDI = 1;
    public static final int TEST_MAP_STUDY = 2;
    public static final int TEST_DISSEMINATE = 3;
    
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        int test = -1;
        
        try {
            test = Integer.parseInt( req.getParameter("test") );
        } catch (NumberFormatException ex) {}
        
        switch ( test ) {
            case TEST_ZELIG_CONFIG : zeligTest(req,res); break;
            case TEST_MAP_DDI : res.getWriter().println("This test is no longer valid."); break;
            case TEST_MAP_STUDY : mapStudyTest(req,res); break;
            case TEST_DISSEMINATE : disseminateTest(req,res); break;            
            default: res.getWriter().println("Invalid test specified.");
        }
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc="disseminateTest">
    protected void disseminateTest(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        String path = req.getPathInfo();
        String type = path.substring( path.indexOf("/") + 1);
        
        String fileIdString =  req.getParameter("fileId");
        StudyFile sf = studyService.getStudyFile( new Long( fileIdString ) );
        
        //String serverPrefix = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();        
        String serverPrefix = "http://vdc-build.hmdc.harvard.edu:8080/dvn";

        if (type.equals("d")) {
            String formatType =  req.getParameter("formatType");
            if (formatType==null) {formatType = "D01";}
            new DSBWrapper().disseminate(res, sf, serverPrefix, formatType);
            
        } else if (type.equals("a")) {
            Map parameters = new HashMap();
            parameters.put("optnlst_a","A01|A02|A03");
            parameters.put("analysis","A01");

            List variables = new ArrayList();
            String dvIdString =  req.getParameter("dvId");
            DataVariable dv = varService.getDataVariable( new Long(dvIdString) );
            variables.add(dv);

            new DSBWrapper().disseminate(res, parameters, sf, serverPrefix, variables);            
        }

    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="mapStudyTest">
    protected void mapStudyTest(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        String path = req.getPathInfo();
        String studyIdString = path.substring( path.indexOf("/") + 1);
        Study s = studyService.getStudy( new Long( studyIdString ) );
        
        try {
            res.setContentType("text/xml");
            PrintWriter out = res.getWriter();
            ddiService.exportStudy(s, out);
            out.close();
            
            
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        
    }
    // </editor-fold>
    
    
    private void readUrlIntoFile(URL inputUrl, File outputFile) throws IOException {
        InputStream in = inputUrl.openStream();
        OutputStream out = new FileOutputStream(outputFile);
        
        try {
            int i = in.read();
            while (i != -1 ) {
                out.write(i);
                i = in.read();
            }
            
        } finally {
            if (in != null) { in.close(); }
            if (out != null) { out.close(); }
        }
    }
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="zeligTest">
    protected void zeligTest(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        try {
            String zeligConfig = new DSBWrapper().getZeligConfig();
            
            res.setContentType("text/xml");
            PrintWriter out = res.getWriter();
            out.println(zeligConfig);
            out.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
