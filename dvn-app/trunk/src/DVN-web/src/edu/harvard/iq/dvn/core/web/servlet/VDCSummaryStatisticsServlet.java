/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.servlet;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.ejb.EJB;
import java.util.*;
import java.util.Map.*;
import static java.lang.System.*;

import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.study.DataVariable;

import edu.harvard.iq.dvn.core.study.SummaryStatistic;
import edu.harvard.iq.dvn.core.study.VariableCategory;

/**
 *
 * @author a_sone
 */
public class VDCSummaryStatisticsServlet extends HttpServlet {
    
    // static fields
    // Headers  for summary statistics  
    public static Map<String, String> sumStatHeaderCntn = new LinkedHashMap<String, String>();
    private static String summaryStatTableRootTagIdPrefix="quickSummaryStatTableRootDiv_";
    static {
        sumStatHeaderCntn.put("mean","mean");
        sumStatHeaderCntn.put("medn","median");
        sumStatHeaderCntn.put("mode","mode");
        sumStatHeaderCntn.put("vald","valid cases");
        sumStatHeaderCntn.put("invd","invalid cases");
        sumStatHeaderCntn.put("min","minimum");
        sumStatHeaderCntn.put("max","maximum");
        sumStatHeaderCntn.put("stdev","standard deviation");
    }
    
    @EJB
    private VariableServiceLocal variableService;
    private DataTable dataTable;

    public VariableServiceLocal getVariableService() {
        return variableService;
    }
    
    private static String imgPrefix = "<img src='/dvn/resources/images/headerblue.png' height='10px' hspace='2px' width='";
    
    private List<DataVariable> dataVariables = new ArrayList<DataVariable>();
    /**
     * Holds value of property summaryStatistics.
     */
    private Collection<SummaryStatistic> summaryStatistics;
    /**
     * Getter for property summaryStatistics.
     * @return Value of property summaryStatistics.
     */
//    public Collection<SummaryStatistic> getSummaryStatistics() {
//        return this.summaryStatistics;
//    }
    /**
     * Setter for property summaryStatistics.
     * @param summaryStatistics New value of property summaryStatistics.
     */
//    public void setSummaryStatistics(Collection<SummaryStatistic> summaryStatistics) {
//        this.summaryStatistics = summaryStatistics;
//    }
    private Collection<VariableCategory> categories;

    /**
     * Getter for property categories.
     * @return Value of property categories.
     */
//    public Collection<VariableCategory> getCategories() {
//        return this.categories;
//    }
    /**
     * Setter for property categories.
     * @param categories New value of property categories.
     */
//    public void setCategories(Collection<VariableCategory> categories) {
//        this.categories = categories;
//    }
    public DataVariable getVariableById(String varId) {

        DataVariable dv = null;
        for (Iterator el = dataVariables.iterator(); el.hasNext();) {
            dv = (DataVariable) el.next();
            // Id is Long
            if (dv.getId().toString().equals(varId)) {
                return dv;
            }
        }
        return dv;
    }

    public String getHtmlTableFragment(Map mp, String hdrKey, String hdrValue, String varId) {
        //StringBuilder sb = new StringBuilder("<table border='1px'><tr><td>"+hdrKey+"</td><td>"+hdrValue+"</td></tr>");
        // /resources/images/headerblue.png

        // <img src="./headerblue.png" height="10" hspace="2" width="157">
        // <img src='/resources/images/headerblue.png' height='10px' hspace='2px' width='157'>

        StringBuilder sb = new StringBuilder(
            "<div id='"+summaryStatTableRootTagIdPrefix+ varId +"' class='statbox'>"
            +"<table class='viTblinx'><tbody><tr><th>" 
            + hdrKey + "</th><th>" + hdrValue + "</th></tr></tbody><tbody>");

        Set ent = mp.entrySet();
        long maxBarLen = 200L;
        double maxFreq = 0L;
        out.println("header value"+hdrValue);
        if (hdrValue.equals("Frequency")) {
            // get max freq
            for (Iterator itr = ent.iterator(); itr.hasNext();) {
                Map.Entry en = (Map.Entry) itr.next();
                if (en.getKey().equals("UNF")) {
                } else {
                    double tmpMax = Double.parseDouble((String) en.getValue());
                    out.println("tmpMax=" + tmpMax);
                    if (tmpMax > maxFreq) {
                        maxFreq = tmpMax;
                        out.println("tmp max=" + maxFreq);
                    }
                }
            }
        // 
        }
        out.println("maxFreq(final)=" + maxFreq);

        for (Iterator itr = ent.iterator(); itr.hasNext();) {
            Map.Entry en = (Map.Entry) itr.next();
            if (en.getKey().equals("UNF")) {
                sb.append(
                    "<tr><th class='viUNF' title='Univeral Numeric Fingerprint'>"
                    + en.getKey() + "</th><td>" + en.getValue() + "</td></tr>");
            } else {
                if (hdrValue.equals("Frequency")) {
                    StringBuilder img = new StringBuilder("");
                    // calculate width
                    long w = 0L;
                    if (maxFreq != 0) {
                        w =  Math.round( maxBarLen * Double.parseDouble(((String) en.getValue())) / maxFreq );
                        out.println("bar chart width=" + w);
                    }
                    img.append(imgPrefix + w + "'/>");
                    sb.append("<tr><th>" + en.getKey() + "</th><td>" 
                        + img.toString() + en.getValue() + "</td></tr>");
                } else {
                    sb.append("<tr><th>" + en.getKey() + "</th><td>" 
                        + en.getValue() + "</td></tr>");
                }

            }
        }
        sb.append("</tbody></table></div>");

        return sb.toString();
    }


    // contents creator
    public String quickSummaryContentsCreator(String varId) {
        StringBuilder colQS = new StringBuilder();
//    colQS.append("QS requested for variableId="+varId);
        String columnHeaderCat = "Value (Label)";
        Collection<SummaryStatistic> sumStat = null;
        // Collection<VariableCategory> catStat=  null;
        List<VariableCategory> catStat = null;
       // List catStat = null;
        int counter = 0;
        int dbglns = 50;
        DataVariable dv = null;
        for (Iterator el = dataVariables.iterator(); el.hasNext();) {
            counter++;
            dv = (DataVariable) el.next();
            // Id is Long
            // out.println("varName="+dv.getName());
            if (dv.getId().toString().equals(varId)) {
                out.println("matched Id="+varId);
                out.println("matched varName="+dv.getName());
                sumStat = dv.getSummaryStatistics();
                out.println("sumStat:size="+sumStat.size());
                // catStat = dv.getCategories();
                catStat = new ArrayList<VariableCategory>();
                catStat.addAll(dv.getCategories());
                out.println("catStat:size="+catStat.size());
                Collections.sort(catStat);
                break;
            }
        }
        out.println("how many items in this table=" + counter);
        if (sumStat == null) {
            out.println("sumStat is still null");
        }
        if (catStat == null) {
            out.println("catStat is still null");
        }
        if (sumStat.isEmpty() && catStat.isEmpty()) {
            // no data case
            colQS.append("Summary Statistics not Recorded");
        } else if (!(sumStat.isEmpty()) && catStat.isEmpty()) {
            if (counter <= dbglns) {
                out.println("sumStat-only case started: row no=" + counter);
            }
            // sumstat only case

            // continuous
            //dv.getSummaryStatistics() returns the wholec Collection of SummaryStatistic type
            //dv.getSummaryStatistics().getType() returns SummaryStatisticType ()
            //                         .getValue() returns String
            //getName() returns String ["average", etc.]
            // note: exists(sumStat) only may not sumStat only: both
            Map<String, String> sumStatSetus = new HashMap<String, String>();
            for (Iterator els = sumStat.iterator(); els.hasNext();) {
                // take one set of stummary statistics from sumStat
                SummaryStatistic dvsum = (SummaryStatistic) els.next();
                // key:statistic-type, value: its value
                sumStatSetus.put(dvsum.getType().getName(), dvsum.getValue());
            }
            
            Map<String, String> sumStatSet = new LinkedHashMap<String, String>();
            Set contHeader = sumStatHeaderCntn.entrySet();
            for (Iterator elsx = contHeader.iterator();elsx.hasNext();){
                Map.Entry et = (Map.Entry)elsx.next();
                // key  et.getKey()
                // value et.getValue()
                if (sumStatSetus.get((String)et.getKey()) != null){
                    sumStatSet.put((String) et.getValue(), sumStatSetus.get((String)et.getKey()));
                }
            }
            sumStatSet.put("UNF", dv.getUnf());

            colQS.append(getHtmlTableFragment(sumStatSet, "Statistic", "Value", varId));

            if (counter <= dbglns) {
                out.println("sumStat-only case ended: row no=" + counter);
            }

        } else if (sumStat.isEmpty() && !(catStat.isEmpty())) {
            // catStat only
            // discrete

            if (counter <= dbglns) {
                out.println("catStat-only case started: row no=" + counter);
            }

            //dv.getCategories() returns the whole Collection of VariableCategory type.

            Map<String, String> catStatSet = new LinkedHashMap<String, String>();

            for (Iterator elc = catStat.iterator(); elc.hasNext();) {
                VariableCategory dvcat = (VariableCategory) elc.next();
                if ((dvcat.getValue().equals(".")) && (dvcat.getFrequency() == 0)){
                    continue;
                }                
                // key:statistic-type, value: its freq

                StringBuilder sb = new StringBuilder();
                sb.append(dvcat.getValue());
                if ((dvcat.getLabel() == null) || (dvcat.getLabel().equals(""))) {
                } else {
                    sb.append("(");
                    sb.append(dvcat.getLabel());
                    sb.append(")");
                }
                // getFrequency() may be null

                String freq;
                if (dvcat.getFrequency() == null) {
                    freq = "0";
                } else {
                    freq = dvcat.getFrequency().toString();
                }

                catStatSet.put(sb.toString(), freq);
            }
            catStatSet.put("UNF", dv.getUnf());

            colQS.append(getHtmlTableFragment(catStatSet, columnHeaderCat, "Frequency", varId));


            if (counter <= dbglns) {
                out.println("catStat-only case ended: row no=" + counter);
            }



        } else if (!(sumStat.isEmpty()) && !(catStat.isEmpty())) {

            // discrete
            if (counter <= dbglns) {
                out.println("sumStat/catStat case started: row no=" + counter);
            }

            //dv.getCategories() returns the whole Collection of VariableCategory type.

            Map<String, String> catStatSet = new LinkedHashMap<String, String>();
            for (Iterator elc = catStat.iterator(); elc.hasNext();) {
                VariableCategory dvcat = (VariableCategory) elc.next();
                
                if ((dvcat.getValue().equals(".")) && (dvcat.getFrequency() == 0)){
                       continue;
                }
                // key:statistic-type, value: its freq
                StringBuilder sb = new StringBuilder();
                sb.append( dvcat.getValue().replaceAll(" ", "&nbsp;") );

                if ((dvcat.getLabel() == null) || (dvcat.getLabel().equals(""))) {
                } else {
                    sb.append(" (");
                    sb.append(dvcat.getLabel());
                    sb.append(")");
                }
                // getFrequency() might be null
                String freq;
                if (dvcat.getFrequency() == null) {
                    freq = "0";
                } else {
                    freq = dvcat.getFrequency().toString();
                }
                catStatSet.put(sb.toString(), freq);
            }
            catStatSet.put("UNF", dv.getUnf());

            colQS.append(getHtmlTableFragment(catStatSet, columnHeaderCat, "Frequency", varId));

            if (counter <= dbglns) {
                out.println("sumStat/catStat case ended: row no=" + counter);
            }

        } else {
            colQS.append("Summary Statistics Not Available");
        }

        return colQS.toString();
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String dtId = request.getParameter("dtId");
        out.println("sumStatServlet: dtId=" + dtId);
        String varId = request.getParameter("varId");
        out.println("sumStatServlet: varId=" + varId);

        // retrieve the datatable by dtId and
        // set it into dataTable
        dataTable = variableService.getDataTable(Long.valueOf(dtId));

        // retrieve each var's data as DataVariable Class data 
        // and set them into Collection<DataVariable> dataVariables
        dataVariables.addAll(dataTable.getDataVariables());
        out.println("pass the addAll line");
        out.println("Number of vars in this data table(" + dtId + ")=" + dataTable.getVarQuantity());

        String qsblock = quickSummaryContentsCreator(varId);
        //out.println("quick summary-statistics table:" + qsblock);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter outPW = response.getWriter();
        try {
            /* TODO output your page here*/
            //outPW.println("<html>");
            //outPW.println("<head>");
            //outPW.println("<title>Servlet VDCSummaryStatisticsServlet</title>");  
            //outPW.println("</head>");
            //outPW.println("<body>");
            //outPW.println("<h1>Servlet VDCSummaryStatisticsServlet at " + request.getContextPath () + "</h1>");

            // outPW.println("<p>Summary Statistics of var whose id is" + varId + "</p>");
            outPW.println(qsblock);
                      
        //outPW.println("</body>");
        //outPW.println("</html>");
        /*  */

        } finally {
            outPW.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "return the summary-statistics table of a requested variable";
    }
    // </editor-fold>
}
