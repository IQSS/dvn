/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ekraffmiller
 */
public class DocumentViewerServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // get documentSetId
            String setId = request.getParameter("setId");
            String docName = request.getParameter("docName");
            // TODO: cleanup getting of JVM option
            String docRoot = System.getProperty("text.documentRoot");

            // look for xml file
            File setDir = new File(docRoot, setId);
            File docDir = new File( setDir, "docs");
            File document = new File(docDir, docName);

                FileInputStream fin = new FileInputStream(document);
                BufferedInputStream bis = new BufferedInputStream(fin);

                // Now read the buffered stream.
                while (bis.available() > 0) {
                    out.print((char) bis.read());
                }

            } catch (Exception e) {
                // TODO: cleanup error handling
                System.err.println("Error reading file: " + e);
            }
            /*
        try {
             TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DocumentViewerServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DocumentViewerServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");

            String setId = request.getParameter("setId");
            String doc = request.getParameter("doc");
            File file = new File()
        } */
                finally {
            out.close();
        }
    }
    

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
