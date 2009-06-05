/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb.impl;

import edu.harvard.iq.dvn.ingest.dsb.*;
import java.io.*;
import static java.lang.System.*;
import java.util.*;
import java.util.logging.*;
import java.lang.reflect.*;
import java.util.regex.*;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import org.apache.commons.lang.*;
import org.apache.commons.lang.builder.*;

/**
 *
 * @author landreev
 */

public class DvnRGraphServiceImpl{

    // - static filelds
    
    private static Logger dbgLog = Logger.getLogger(DvnRGraphServiceImpl.class.getPackage().getName());

    // - constants for defining the subset queries: 

    public static String MANUAL_QUERY = "MANUAL_QUERY";
    public static String ELIMINATE_DISCONNECTED = "ELIMINATE_DISCONNECTED";
    public static String AUTOMATIC_QUERY = "AUTOMATIC_QUERY";
    public static String N_VALUE = "N_VALUE";
    public static String NETWORK_MEASURE = "NETWORK_MEASURE"; 
    public static String NETWORK_MEASURE_PARAMETER = "NETWORK_MEASURE_PARAMETER";
    public static String VERTEX_ATTRIBUTE = "VERTEX_ATTRIBUTE"; 
    public static String EDGE_ATTRIBUTE = "EDGE_ATTRIBUTE";



    public static String DVN_TMP_DIR=null;
    public static String DSB_TMP_DIR=null;

    private static String GRAPHML_FILE_NAME = "iGraph";
    public static String GRAPHML_FILE_EXT =".xml";

    private static String RDATA_FILE_NAME = "iGraph";
    public static String RDATA_FILE_EXT =".RData";

    private static String TMP_DATA_FILE_NAME = "susetfile4Rjob";
    public static String TMP_DATA_FILE_EXT =".tab";

    private static String RSERVE_HOST = null;
    private static String RSERVE_USER = null;
    private static String RSERVE_PWD = null;    
    private static int RSERVE_PORT;
    private static String DSB_HOST_PORT= null;
    public static String RWRKSP_FILE_PREFIX = "dvnGraphRData_";

    private static Map<String, Method> runMethods = new HashMap<String, Method>();
    private static String regexForRunMethods = "^run(\\w+)Request$" ;
    public static String TEMP_DIR = System.getProperty("java.io.tmpdir");
    
    static {
    
        DSB_TMP_DIR = System.getProperty("vdc.dsb.temp.dir");
        
        // fallout case: last resort
        if (DSB_TMP_DIR == null){
            
            DVN_TMP_DIR ="/tmp/VDC";
            DSB_TMP_DIR = DVN_TMP_DIR + "/DSB";
            
        }
        
        RSERVE_HOST = System.getProperty("vdc.dsb.host");
	DSB_HOST_PORT = System.getProperty("vdc.dsb.port");
        if (DSB_HOST_PORT == null){
            DSB_HOST_PORT= "80";
        }
                
        RSERVE_USER = System.getProperty("vdc.dsb.rserve.user");
        if (RSERVE_USER == null){
            RSERVE_USER= "rserve";
        }
        
        RSERVE_PWD = System.getProperty("vdc.dsb.rserve.pwrd");
        if (RSERVE_PWD == null){
            RSERVE_PWD= "rserve";
        }
        

        if (System.getProperty("vdc.dsb.rserve.port") == null ){
            RSERVE_PORT= 6311;
        } else {
            RSERVE_PORT = Integer.parseInt(System.getProperty("vdc.dsb.rserve.port"));
        }
        

    }

    static String librarySetup= "library('NetworkUtils');";
    boolean DEBUG = true;
    
    // ----------------------------------------------------- instance filelds
    public String IdSuffix = null;
    public String tempFileName = null;
    public String GraphMLfileNameRemote = null;    
    public String RDataFileName = null;
    public String wrkdir = null;
    public String requestdir = null;
    public List<String> historyEntry = new ArrayList<String>();
    public List<String> replicationFile = new LinkedList<String>();
    
    // ----------------------------------------------------- constructor
    public DvnRGraphServiceImpl(){
        
        // initialization
        IdSuffix = RandomStringUtils.randomNumeric(6);
                 
        requestdir = "Grph_" + IdSuffix;
        
        wrkdir = DSB_TMP_DIR + "/" + requestdir;
        
        tempFileName = DSB_TMP_DIR + "/" + TMP_DATA_FILE_NAME
                 +"." + IdSuffix + TMP_DATA_FILE_EXT;
	
	RDataFileName = DSB_TMP_DIR + "/" + RDATA_FILE_NAME
                 +"." + IdSuffix + RDATA_FILE_EXT;

	GraphMLfileNameRemote = DSB_TMP_DIR + "/" + GRAPHML_FILE_NAME
                 + "." + IdSuffix + GRAPHML_FILE_EXT;
	
    }


    public void setupWorkingDirectories(RConnection c){
        try{

            // set up the working directory
            // parent dir;

	    // the 4 lines below are R code being sent over to Rserve;
	    // it looks kinda messy, true.

            String checkWrkDir = "if (file_test('-d', '"+DSB_TMP_DIR+"')) {Sys.chmod('"+
            DVN_TMP_DIR+"', mode = '0777'); Sys.chmod('"+DSB_TMP_DIR+"', mode = '0777');} else {dir.create('"+DSB_TMP_DIR+"', showWarnings = FALSE, recursive = TRUE);Sys.chmod('"+DVN_TMP_DIR+"', mode = '0777');Sys.chmod('"+
            DSB_TMP_DIR+"', mode = '0777');}";

            dbgLog.fine("w permission="+checkWrkDir);

            c.voidEval(checkWrkDir);

            // wrkdir
            String checkWrkDr = "if (file_test('-d', '"+wrkdir+"')) {Sys.chmod('"+
            wrkdir+"', mode = '0777'); } else {dir.create('"+wrkdir+"', showWarnings = FALSE, recursive = TRUE);Sys.chmod('"+wrkdir+"', mode = '0777');}";
            dbgLog.fine("w permission:wrkdir="+checkWrkDr);
            c.voidEval(checkWrkDr);

        } catch (RserveException rse) {
            rse.printStackTrace();
        }

    }
    
    public void setupWorkingDirectory(RConnection c){
        try{
            // set up the working directory
            // parent dir

            String checkWrkDir = "if (file_test('-d', '"+DSB_TMP_DIR+"')) {Sys.chmod('"+
            DVN_TMP_DIR+"', mode = '0777'); Sys.chmod('"+DSB_TMP_DIR+"', mode = '0777');} else {dir.create('"+DSB_TMP_DIR+"', showWarnings = FALSE, recursive = TRUE);Sys.chmod('"+DVN_TMP_DIR+"', mode = '0777');Sys.chmod('"+
            DSB_TMP_DIR+"', mode = '0777');}";

            dbgLog.fine("w permission="+checkWrkDir);

            c.voidEval(checkWrkDir);
        
        } catch (RserveException rse) {
            rse.printStackTrace();
        }
    }
    
    
    
    /** *************************************************************
     * Execute an R-based dvn analysis request on a Graph object
     *
     * @param sro    a DvnRJobRequest object that contains various parameters
     * @return    a Map that contains various information about results
     */    
    
    public Map<String, String> execute(DvnRJobRequest sro) {
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        
        try {
            // Set up an Rserve connection
            dbgLog.fine("sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
            
            dbgLog.fine("RSERVE_USER="+RSERVE_USER+"[default=rserve]");
            dbgLog.fine("RSERVE_PWD="+RSERVE_PWD+"[default=rserve]");
            dbgLog.fine("RSERVE_PORT="+RSERVE_PORT+"[default=6311]");

            RConnection c = new RConnection(RSERVE_HOST, RSERVE_PORT);
            dbgLog.fine("hostname="+RSERVE_HOST);

            c.login(RSERVE_USER, RSERVE_PWD);
            dbgLog.fine(">" + c.eval("R.version$version.string").asString() + "<");


            // save the data file at the Rserve side
            String infile = sro.getSubsetFileName();
            InputStream inb = new BufferedInputStream(
                    new FileInputStream(infile));
            int bufsize;
            byte[] bffr = new byte[1024];

            RFileOutputStream os = 
                 c.createFile(tempFileName);
            while ((bufsize = inb.read(bffr)) != -1) {
                    os.write(bffr, 0, bufsize);
            }
            os.close();
            inb.close();
	            
            // Rserve code starts here

            dbgLog.fine("wrkdir="+wrkdir);
            historyEntry.add(librarySetup);
            c.voidEval(librarySetup);

            // check working directories
            setupWorkingDirectories(c);
            
            // subsetting 

            List<String> scLst = sro.getSubsetConditions();
            if (scLst != null){
                for (String sci : scLst){
                    dbgLog.fine("sci:"+ sci);
                    historyEntry.add(sci);
                    c.voidEval(sci);
                }
            }

             String RexecDate = c.eval("as.character(as.POSIXct(Sys.time()))").asString();
            
            // save workspace as a replication data set

            String RdataFileName = "DVNdataFrame." + IdSuffix + ".RData"; 

            result.put("Rdata", "/"+requestdir+ "/" + RdataFileName);

            String saveWS = "save('dvnData', file='"+ wrkdir +"/"+ RdataFileName +"')";

            dbgLog.fine("save the workspace="+saveWS);
            c.voidEval(saveWS);

            // write back the R workspace to the dvn 
            
            String wrkspFileName = wrkdir +"/"+ RdataFileName;
            dbgLog.fine("wrkspFileName="+wrkspFileName);
            
            int wrkspflSize = getFileSize(c,wrkspFileName);
            
            File wsfl = writeBackFileToDvn(c, wrkspFileName, RWRKSP_FILE_PREFIX,"RData", wrkspflSize);
            

            result.put("dvn_RData_FileName",wsfl.getName());
            
            if (wsfl != null){
                result.put("wrkspFileName", wsfl.getAbsolutePath());
                dbgLog.fine("wrkspFileName="+wsfl.getAbsolutePath());
            } else {
                dbgLog.fine("wrkspFileName is null");
            }
            

	    String RversionLine = "R.Version()$version.string";
            String Rversion = c.eval(RversionLine).asString();
            
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("IdSuffix", IdSuffix);
            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            

            dbgLog.fine("result object (before closing the Rserve):\n"+result);
                    
            c.close();
        
        } catch (RserveException rse) {
            // RserveException (Rserve not running?)
            rse.printStackTrace();
            
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("option",sro.getRequestType().toLowerCase());
            
            result.put("RexecError", "true");
	    result.put("RexecErrorMessage", rse.getMessage()); 
	    result.put("RexecErrorDescription", rse.getRequestErrorDescription()); 
            return result;

        } catch (REXPMismatchException mme) {
        
            // REXP mismatch exception (what we got differs from what we expected)
            mme.printStackTrace();
            
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("option",sro.getRequestType().toLowerCase());

            result.put("RexecError", "true");
            return result;

        } catch (IOException ie){
            ie.printStackTrace();
            
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("option",sro.getRequestType().toLowerCase());

            result.put("RexecError", "true");
            return result;
            
        } catch (Exception ex){
            ex.printStackTrace();
            
            result.put("IdSuffix", IdSuffix);

            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("option",sro.getRequestType().toLowerCase());

            result.put("RexecError", "true");
            return result;
        }
        
        return result;
        
}
    

    /** *************************************************************
     * Execute an R-based "ingest" of a GraphML file
     *
     * @param graphMLfileName;
     * @param cachedRDatafileName;
     * @return    a Map that contains various information about results
     */    
    
     public Map<String, String> ingestGraphML (String graphMLfileName, 
					      String cachedRDatafileName) {

        Map<String, String> result = new HashMap<String, String>();
        
	try {

            // Set up an Rserve connection
            
            dbgLog.fine("RSERVE_USER="+RSERVE_USER+"[default=rserve]");
            dbgLog.fine("RSERVE_PWD="+RSERVE_PWD+"[default=rserve]");
            dbgLog.fine("RSERVE_PORT="+RSERVE_PORT+"[default=6311]");

            RConnection c = new RConnection(RSERVE_HOST, RSERVE_PORT);
            dbgLog.fine("hostname="+RSERVE_HOST);

            c.login(RSERVE_USER, RSERVE_PWD);

            dbgLog.fine(">" + c.eval("R.version$version.string").asString() + "<");

            // send the graphML to the Rserve side

            InputStream inb = new BufferedInputStream(new FileInputStream(graphMLfileName));

            int bufsize;
            byte[] bffr = new byte[1024];

            RFileOutputStream os = c.createFile(GraphMLfileNameRemote);

            while ((bufsize = inb.read(bffr)) != -1) {
                    os.write(bffr, 0, bufsize);
            }
            os.close();
            inb.close();
            
            historyEntry.add(librarySetup);
            c.voidEval(librarySetup);

            this.setupWorkingDirectories(c);

            // ingest itself:

	    String ingestCommand = "ingest_graphml('" + GraphMLfileNameRemote + "')";
	    dbgLog.fine(ingestCommand);
	    historyEntry.add(ingestCommand);
	    c.voidEval(ingestCommand);

            
	     
            int fileSize = getFileSize(c,RDataFileName);
            
	    OutputStream outbr = new BufferedOutputStream(new FileOutputStream(new File(cachedRDatafileName)));
	    RFileInputStream ris = c.openFile(RDataFileName);

	    if (fileSize < 64*1024*1024){
		bufsize = fileSize;
	    } else {
		bufsize = 64*1024*1024; 
	    }

	    byte[] obuf = new byte[bufsize];

	    while ( ris.read(obuf) != -1 ) {
		outbr.write(obuf, 0, bufsize);
	    }

	    ris.close();
	    outbr.close();


	    String RexecDate = c.eval("as.character(as.POSIXct(Sys.time()))").asString();
	    String RversionLine = "R.Version()$version.string";
            String Rversion = c.eval(RversionLine).asString();
            
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("IdSuffix", IdSuffix);

            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            dbgLog.fine("result object (before closing the Rserve):\n"+result);
                    
            c.close();
        
        } catch (RserveException rse) {
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            result.put("RexecError", "true");
	    result.put("RexecErrorMessage", rse.getMessage()); 
	    result.put("RexecErrorDescription", rse.getRequestErrorDescription()); 
            return result;

        } catch (REXPMismatchException mme) {
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;

        } catch (FileNotFoundException fe){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("RexecError", "true");
	    result.put("RexecErrorDescription", "File Not Found"); 
            return result;

	} catch (IOException ie){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
            
        } catch (Exception ex){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
        }
        
        return result;
        
    }
    

    // -- utilitiy methods
    
    /**
     * Returns the array of values that corresponds the order of 
     * provided keys
     *
     * @param     
     * @return    
     */

    public static String[] getValueSet(Map<String, String> mp, String[] keys) {
        
        List<String> tmpvl = new ArrayList<String>();
        for (int i=0; i< keys.length; i++){
            tmpvl.add(mp.get(keys[i]));
        }
        String[] tmpv = (String[])tmpvl.toArray(new String[tmpvl.size()]);
        return tmpv;
    }
    
    
    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public String joinNelementsPerLine(String[] vn, int divisor){
        String vnl = null;
        if (vn.length < divisor){
            vnl = StringUtils.join(vn, ", ");
        } else {
            StringBuilder sb = new StringBuilder();
            
            int iter =  vn.length / divisor;
            int lastN = vn.length % divisor;
            if (lastN != 0){
                iter++;
            }
            int iterm = iter - 1;
            for (int i= 0; i<iter; i++){
                int terminalN = divisor;
                if ((i == iterm )  && (lastN != 0)){
                    terminalN = lastN;
                }                
                for (int j = 0; j< terminalN; j++){
                    if ( (divisor*i +j +1) == vn.length){ 
                        sb.append(vn[j + i*divisor]);
                    } else {
                        
                        sb.append(vn[j + i*divisor] + ", ");
                    }
                }
                sb.append("\n");
            }
            vnl = sb.toString();
            dbgLog.fine(vnl);
        }
        return vnl;
    }
    
    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public String joinNelementsPerLine(String[] vn, int divisor, String sp, 
        boolean quote, String qm, String lnsp){
        if (!(divisor >= 1)){
            divisor = 1;
        } else if ( divisor > vn.length) {
            divisor = vn.length;
        }
        String sep = null;
        if (sp != null){
            sep = sp;
        } else {
            sep = ", ";
        }
        String qmrk = null;
        if (quote){
            if (qm == null){
                qmrk = ",";
            } else {
                if (qm.equals("\"")){
                    qmrk = "\"";
                } else {
                    qmrk = qm;
                }
            }
        } else {
            qmrk = "";
        }
        String lineSep = null;
        if (lnsp == null){
            lineSep = "\n";
        } else {
            lineSep = lnsp;
        }
        
        String vnl = null;
        if (vn.length < divisor){
            vnl = StringUtils.join(vn, sep);
        } else {
            StringBuilder sb = new StringBuilder();
            
            int iter =  vn.length / divisor;
            int lastN = vn.length % divisor;
            if (lastN != 0){
                iter++;
            }
            int iterm = iter - 1;
            for (int i= 0; i<iter; i++){
                int terminalN = divisor;
                if ((i == iterm )  && (lastN != 0)){
                    terminalN = lastN;
                }                
                for (int j = 0; j< terminalN; j++){
                    if ( (divisor*i +j +1) == vn.length){ 
                        
                        sb.append(qmrk + vn[j + i*divisor] + qmrk);
                        
                    } else {
                        
                        sb.append(qmrk + vn[j + i*divisor] + qmrk + sep);
                        
                    }
                }
                if (i < (iter-1)){
                sb.append(lineSep);
                }
            }
            vnl = sb.toString();
            dbgLog.fine("results:\n"+vnl);
        }
        return vnl;
    }
    
    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public File writeBackFileToDvn(RConnection c, String targetFilename,
        String tmpFilePrefix, String tmpFileExt, int fileSize){
        
        // set up a temp file
        File tmprsltfl = null;
        String resultFile =  tmpFilePrefix + IdSuffix + "." + tmpFileExt;
        
        RFileInputStream ris = null;
        OutputStream outbr   = null;

        try {
	    tmprsltfl = new File(TEMP_DIR, resultFile);

            outbr = new BufferedOutputStream(new FileOutputStream(tmprsltfl));

            ris = c.openFile(targetFilename);

	    // what's going on here? 

            if (fileSize < 1024*1024*500){
                int bfsize = fileSize;
		byte[] obuf = new byte[bfsize];
		ris.read(obuf);
		outbr.write(obuf, 0, bfsize);
            }

            ris.close();
            outbr.close();
            return tmprsltfl;

        } catch (FileNotFoundException fe){
            fe.printStackTrace();
            dbgLog.fine("FileNotFound exception occurred");
            return tmprsltfl;
        } catch (IOException ie){
            ie.printStackTrace();
            dbgLog.fine("IO exception occurred");
        } finally {
            if (ris != null){
                try {
                    ris.close();
                } catch (IOException e){
                }
            }            
            if (outbr != null){
                try {
                    outbr.close();
                } catch (IOException e){
                
                }
            }
        
        }
        return tmprsltfl;
    }
    
    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public int getFileSize(RConnection c, String targetFilename){
        dbgLog.fine("targetFilename="+targetFilename);
        int fileSize = 0;
        try {
            String fileSizeLine = "round(file.info('"+targetFilename+"')$size)";
            fileSize = c.eval(fileSizeLine).asInteger();
        } catch (RserveException rse) {
            rse.printStackTrace();
        } catch (REXPMismatchException mme) {
            mme.printStackTrace();
        }
        return fileSize;
    }
}
