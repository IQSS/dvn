/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb.impl;

import edu.harvard.iq.dvn.core.analysis.NetworkMeasureParameter; 

import edu.harvard.iq.dvn.core.util.StringUtil;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.lang.reflect.*;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import org.apache.commons.lang.*;
import org.apache.commons.lang.builder.*;

/**
 *
 * @author landreev
 */

public class DvnRService implements java.io.Serializable {

    // - static filelds
    
    private static Logger dbgLog = Logger.getLogger(DvnRGraphServiceImpl.class.getPackage().getName());

    private static DvnRConnectionPool RConnectionPool = null; 

    
    private int myConnection = 0; 

    // - constants for defining the subset 

    public static String RSUBSETFUNCTION = "RSUBSETFUNCTION";


    // - return result fields:

    public static String SAVED_RWORK_SPACE = "SAVED_RWORK_SPACE";

    public static String TMP_DIR_LOCAL=null;
    public static String TMP_DIR_REMOTE=null;

    private static String GRAPHML_FILE_NAME = "iGraph";
    public static String GRAPHML_FILE_EXT =".xml";

    private static String RDATA_FILE_NAME = "commands";
    public static String RDATA_FILE_EXT =".RData";

    private static String RSERVE_HOST = null;
    private static String RSERVE_USER = null;
    private static String RSERVE_PWD = null;    
    private static int RSERVE_PORT;
    private static int RSERVE_CONNECTION_POOLSIZE; 
//    private static String DSB_HOST_PORT= null;

    public static String TEMP_DIR = System.getProperty("java.io.tmpdir");
    
    static {
    
        TMP_DIR_REMOTE = System.getProperty("dvn.temp.dir");
        
        // fallout case: last resort
        if (TMP_DIR_REMOTE == null){
            
            TMP_DIR_LOCAL ="/tmp/VDC";
            TMP_DIR_REMOTE = TMP_DIR_LOCAL + "/DSB";
            
        }
        
        RSERVE_HOST = System.getProperty("dvn.rserve.host");
                
        RSERVE_USER = System.getProperty("dvn.rserve.user");
        if (RSERVE_USER == null){
            RSERVE_USER= "rserve";
        }
        
        RSERVE_PWD = System.getProperty("dvn.rserve.password");
        if (RSERVE_PWD == null){
            RSERVE_PWD= "rserve";
        }
        

        if (System.getProperty("vdc.dsb.rserve.port") == null ){
            RSERVE_PORT= 6311;
        } else {
            RSERVE_PORT = Integer.parseInt(System.getProperty("vdc.dsb.rserve.port"));
        }

        if (System.getProperty("dvn.rserve.poolsize") == null) {
            RSERVE_CONNECTION_POOLSIZE = 10;
        } else {
            RSERVE_CONNECTION_POOLSIZE = Integer.parseInt(System.getProperty("dvn.rserve.poolsize"));
        }
    }

    static String librarySetup= "library('NetworkUtils');";
    boolean DEBUG = true;
    
    public String IdSuffix = null;
    public String GraphMLfileNameRemote = null;    
    public String RDataFileName = null;
    public String wrkdir = null;
    public String requestdir = null;
    public List<String> historyEntry = new ArrayList<String>();
    public List<String> replicationFile = new LinkedList<String>();
    
    // ----------------------------------------------------- constructor
    public DvnRService(){
        
        // initialization
        IdSuffix = RandomStringUtils.randomNumeric(6);
                 
        requestdir = "Grph_" + IdSuffix;
        
        wrkdir = TMP_DIR_REMOTE + "/" + requestdir;
        
        RDataFileName = TMP_DIR_REMOTE + "/" + RDATA_FILE_NAME
            +"." + IdSuffix + RDATA_FILE_EXT;

        GraphMLfileNameRemote = TMP_DIR_REMOTE + "/" + GRAPHML_FILE_NAME
            + "." + IdSuffix + GRAPHML_FILE_EXT;

        if ( RConnectionPool == null ) {
            dbgLog.info ("number of RServe connections: "+RSERVE_CONNECTION_POOLSIZE);
            RConnectionPool = new DvnRConnectionPool ( RSERVE_CONNECTION_POOLSIZE );
        }
	
    }

    public void setupWorkingDirectories(RConnection c) throws RserveException {
        // set up the working directory in the designated temp directory
        // location;
        // the 4 lines below are R code being sent over to Rserve;
        // it looks kinda messy, true.

        String checkWrkDir = "if (file_test('-d', '" + TMP_DIR_REMOTE + "')) {Sys.chmod('" +
                TMP_DIR_LOCAL + "', mode = '0777'); Sys.chmod('" + TMP_DIR_REMOTE + "', mode = '0777');} else {dir.create('" + TMP_DIR_REMOTE + "', showWarnings = FALSE, recursive = TRUE);Sys.chmod('" + TMP_DIR_LOCAL + "', mode = '0777');Sys.chmod('" +
                TMP_DIR_REMOTE + "', mode = '0777');}";

        dbgLog.fine("w permission=" + checkWrkDir);

        c.voidEval(checkWrkDir);

        // work dir:

        String checkWrkDr = "if (file_test('-d', '" + wrkdir + "')) {Sys.chmod('" +
                wrkdir + "', mode = '0777'); } else {dir.create('" + wrkdir + "', showWarnings = FALSE, recursive = TRUE);Sys.chmod('" + wrkdir + "', mode = '0777');}";
        dbgLog.fine("w permission:wrkdir=" + checkWrkDr);
        c.voidEval(checkWrkDr);

    }
    
    private RConnection openNewConnection() throws RserveException {
        RConnection rc = null;

        dbgLog.fine("RSERVE_USER=" + RSERVE_USER + "[default=rserve]");
        dbgLog.fine("RSERVE_PWD=" + RSERVE_PWD + "[default=rserve]");
        dbgLog.fine("RSERVE_PORT=" + RSERVE_PORT + "[default=6311]");

        rc = new RConnection(RSERVE_HOST, RSERVE_PORT);
        dbgLog.fine("hostname=" + RSERVE_HOST);

        rc.login(RSERVE_USER, RSERVE_PWD);

        // set up the NetworkUtils + dependencies;
        // this needs to be done on all new connections.

        rc.voidEval(librarySetup);

        return rc;

    }

    private void loadWorkSpace (RConnection rc, String workSpace) throws RException, RserveException, REXPMismatchException  {
	
	    String cmdResponse = safeEval(rc, "load('"+workSpace+"')").asString();

        // TODO:
        // Exceptions are expected to be thrown when error conditions are
        // encountered. Still, we should also check the command response
        // string in case it contains any diagnostics information.

    }

    private void loadAndClearWorkSpace (RConnection rc, String workSpace) throws RException, RserveException, REXPMismatchException {
	    String cmdResponse = safeEval(rc, "load_and_clear('"+workSpace+"')").asString();

        // TODO:
        // See the comment above.
    }

    
    /** *************************************************************
     * initialize the RServe connection and load the R data file;
     * keep the open connection.
     *
     * @param sro    a DvnRJobRequest object that contains various parameters
     * @return    a Map that contains various information about the results
     */    
    
    public Map<String, String> initializeConnection(DvnRJobRequest sro) throws DvnRServiceException {

        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        if (sro != null) {
            dbgLog.fine("sro dump:\n" + ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
        } else {
            throw new DvnRServiceException("init: NULL R JOB OBJECT");
        }

        String SavedRworkSpace = null;
        String CachedRworkSpace = sro.getCachedRworkSpace();

        Map<String, Object> SubsetParameters = sro.getParametersForGraphSubset();

        if (SubsetParameters != null) {
            SavedRworkSpace = (String) SubsetParameters.get(SAVED_RWORK_SPACE);
        }

        if (SavedRworkSpace != null) {
            myConnection = RConnectionPool.securePooledConnection(SavedRworkSpace, null, true, 0);
            RDataFileName = SavedRworkSpace;

        } else if (CachedRworkSpace != null) {
            myConnection = RConnectionPool.securePooledConnection(RDataFileName, CachedRworkSpace, false, 0);
        } else {
            throw new DvnRServiceException("Initialize method called without either local or remote RData file");
        }

        if (myConnection == 0) {
            throw new DvnRServiceException("failed to obtain an R connection");
        } else {
            DvnRConnection drc = RConnectionPool.getConnection(myConnection);
            if (drc == null) {
                throw new DvnRServiceException("failed to obtain an R connection");
            } else {
                // set the connection time stamp:
                Date now = new Date();
                drc.setLastQueryTime(now.getTime());
                drc.unlockConnection();
            }
        }

        dbgLog.info("Initialize: obtained connection " + myConnection);

        result.put(SAVED_RWORK_SPACE, RDataFileName);
        result.put("IdSuffix", IdSuffix);


        return result;

    }


    /** *************************************************************
     * close RServe connection;
     * (should I remove the saved workspace(s) on the server side? 
     *
     * @return    a Map that contains diagnostics information 
     * in case of an error. 
     */    

    // TODO: do we still need this method?
    
    public Map<String, String> closeConnection() {
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        
	return result;

    }

    /** *************************************************************
     * Execute an R-based dvn analysis request on a Graph object
     * using an open connection created during the Initialize call. 
     *
     * @param sro    a DvnJobRequest object that contains various parameters
     * @return    a Map that contains various information about the results
     */    
    
    public Map<String, String> liveConnectionExecute(DvnRJobRequest sro) throws DvnRServiceException {

        // set the return object
        Map<String, String> result = new HashMap<String, String>();

        if (sro != null) {
            dbgLog.fine("LCE sro dump:\n" + ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
        } else {
            throw new DvnRServiceException("execute method called with a NULL job ob ject.");

        }

        String SavedRworkSpace = null;

        Map<String, Object> SubsetParameters = sro.getParametersForGraphSubset();

        if (SubsetParameters != null) {
            SavedRworkSpace = (String) SubsetParameters.get(SAVED_RWORK_SPACE);
        } else {
            throw new DvnRServiceException("execute method called with a null parameters object");

        }

        DvnRConnection drc = null;


        try {
            // let's see if we have a connection that we can use:

            if (myConnection == 0) {
                throw new DvnRServiceException("execute method called without securing a connection first");
            }

            myConnection = RConnectionPool.securePooledConnection(SavedRworkSpace, null, true, myConnection);
            dbgLog.info("Execute: obtained connection " + myConnection);


            drc = RConnectionPool.getConnection(myConnection);


            String GraphSubsetType = (String) SubsetParameters.get(RSUBSETFUNCTION);

            if (GraphSubsetType != null) {

                if (GraphSubsetType.equals("UNDO")) {
                    String cEval = safeEval(drc.Rcon, "undo()").asString();
                } else if (GraphSubsetType.equals("RESET")) {
                    dbgLog.info("resetting the workspace; using reset(" + SavedRworkSpace + ")");

                    String cEval = safeEval(drc.Rcon, "reset('" + SavedRworkSpace + "')").asString();


                } else { //if (GraphSubsetType.equals(MANUAL_QUERY_SUBSET)) {

                    String manualQueryType = ""; // (String) SubsetParameters.get(MANUAL_QUERY_TYPE);
                    String manualQuery = ""; // (String) SubsetParameters.get(MANUAL_QUERY);

                    String subsetCommand = null; // TODO

                    dbgLog.fine("manualQuery=" + subsetCommand);
                        historyEntry.add(subsetCommand);
                        String cmdResponse = safeEval(drc.Rcon, subsetCommand).asString();

                }

            }

           // // get the vertices and edges counts:

            // String countCommand = "vcount(g)";
            // int countResponse = safeEval(drc.Rcon, countCommand).asInteger();
            // result.put(NUMBER_OF_VERTICES, Integer.toString(countResponse));

            // countCommand = "ecount(g)";
            // countResponse = safeEval(drc.Rcon, countCommand).asInteger();
            // result.put(NUMBER_OF_EDGES, Integer.toString(countResponse));


            result.put(SAVED_RWORK_SPACE, RDataFileName);

            // we're done; let's add whatever potentially useful
            // information we have to the result map and return:

            String RexecDate = drc.Rcon.eval("as.character(as.POSIXct(Sys.time()))").asString();
            String RversionLine = "R.Version()$version.string";
            String Rversion = drc.Rcon.eval(RversionLine).asString();

            result.put("rserveHost", RSERVE_HOST);
            result.put("IdSuffix", IdSuffix);
            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry, "\n"));


            if (!SavedRworkSpace.equals(drc.getWorkSpace())) {
                throw new DvnRServiceException("Could not execute query: connection lost");
            }

        } catch (DvnRServiceException dre) {
            throw dre;
        } catch (RException re) {
            throw new DvnRServiceException(re.getMessage());

        } catch (RserveException rse) {

            dbgLog.info("rserve exception message: " + rse.getMessage());
            dbgLog.info("rserve exception description: " + rse.getRequestErrorDescription());
            throw new DvnRServiceException("RServe failure: " + rse.getMessage());

        } catch (REXPMismatchException mme) {

            throw new DvnRServiceException("REXPmismatchException occured: " + mme.getMessage());

        } catch (Exception ex) {

            throw new DvnRServiceException("Execute: unknown exception occured: " + ex.getMessage());

        } finally {
            if (drc != null) {
                // set the connection time stamp:
                Date now = new Date();
                drc.setLastQueryTime(now.getTime());

                drc.unlockConnection();
            }
        }

        return result;

    }

    /** *************************************************************
     * Export a saved RData file as a GraphML file, using the existing
     * open connection
     *
     * @param savedRDatafile;
     * @return    a Map that contains various information about the results
     */    
    
    public Map<String, String> liveConnectionExport(String savedRDataFile) throws DvnRServiceException {

        Map<String, String> result = new HashMap<String, String>();

        DvnRConnection drc = null;


        try {

            // let's see if we have a connection that we can use:

            if (myConnection == 0) {
                throw new DvnRServiceException("execute method called without creating a connection first");
            }

            myConnection = RConnectionPool.securePooledConnection(savedRDataFile, null, true, myConnection);
            dbgLog.info("Export: obtained connection " + myConnection);


            drc = RConnectionPool.getConnection(myConnection);


            String exportCommand = "dump_graphml(g, '" + GraphMLfileNameRemote + "')";
            dbgLog.fine(exportCommand);
            historyEntry.add(exportCommand);
            String cmdResponse = safeEval(drc.Rcon, exportCommand).asString();

            exportCommand = "dump_tab(g, '" + TMP_DIR_REMOTE + "/temp_" + IdSuffix + ".tab')";
            dbgLog.fine(exportCommand);
            historyEntry.add(exportCommand);
            cmdResponse = safeEval(drc.Rcon, exportCommand).asString();


            File zipFile = new File(TEMP_DIR, "subset_" + IdSuffix + ".zip");
            FileOutputStream zipFileStream = new FileOutputStream(zipFile);
            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile));

            addZipEntry(drc.Rcon, zout, GraphMLfileNameRemote, "data/subset.xml");
            addZipEntry(drc.Rcon, zout, TMP_DIR_REMOTE + "/temp_" + IdSuffix + "_verts.tab", "data/vertices.tab");
            addZipEntry(drc.Rcon, zout, TMP_DIR_REMOTE + "/temp_" + IdSuffix + "_edges.tab", "data/edges.tab");

            zout.close();
            zipFileStream.close();

            //result.put(GRAPHML_FILE_EXPORTED, zipFile.getAbsolutePath());

            String RexecDate = drc.Rcon.eval("as.character(as.POSIXct(Sys.time()))").asString();
            String RversionLine = "R.Version()$version.string";
            String Rversion = drc.Rcon.eval(RversionLine).asString();

            result.put("rserveHost", RSERVE_HOST);
            result.put("IdSuffix", IdSuffix);

            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry, "\n"));

            dbgLog.fine("result object (before closing the Rserve):\n" + result);

        } catch (DvnRServiceException dre) {
            throw dre;
        } catch (RException re) {
            throw new DvnRServiceException("R run-time error: " + re.getMessage());
        } catch (RserveException rse) {
            dbgLog.info("LCE: rserve exception message: " + rse.getMessage());
            dbgLog.info("LCE: rserve exception description: " + rse.getRequestErrorDescription());
            throw new DvnRServiceException("RServe failure: " + rse.getMessage());

        } catch (REXPMismatchException mme) {
            throw new DvnRServiceException("REXPmismatchException occured");

        } catch (Exception ex) {
            throw new DvnRServiceException("Unknown exception occured: " + ex.getMessage());
        } finally {
            if (drc != null) {
                // set the connection time stamp:
                Date now = new Date();
                drc.setLastQueryTime(now.getTime());

                drc.unlockConnection();
            }
        }
        return result;

    }

    private void addZipEntry(RConnection c, ZipOutputStream zout, String inputFileName, String outputFileName) throws IOException{
        RFileInputStream tmpin = c.openFile(inputFileName);
        byte[] dataBuffer = new byte[8192];
        int i = 0;

        ZipEntry e = new ZipEntry(outputFileName);
        zout.putNextEntry(e);

        while ((i = tmpin.read(dataBuffer)) > 0) {
            zout.write(dataBuffer, 0, i);
            zout.flush();
        }
        tmpin.close();
        zout.closeEntry();
     }
    

    // -- utilitiy methods
    
        
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

    // Custom DVN/R service exceptions:
    //
    // (these were created in hopes we could use it to extract useful
    // diagnostics information from R error condition exceptions, which
    // don't provide any meaningful messages)

    public class RException extends Exception {

        public RException(String msg) {
            super("\"" + msg + "\"");
        }
    }

    public class DvnRServiceException extends Exception {

        public DvnRServiceException(String msg) {
            super(msg);
        }
    }

    private REXP safeEval(RConnection c, String s) throws
            RserveException,
            RException, REXPMismatchException {
        REXP r = c.eval("try({" + s + "}, silent=TRUE)");
        if (r.inherits("try-error")) {
            throw new RException(r.asString());
        }
        return r;
    }


    // connection pooling implementation class:

    public class DvnRConnectionPool {

        private DvnRConnection[] RConnectionStack;
        private int numberOfConnections = 0;
        private Logger dbgLog = Logger.getLogger(DvnRConnectionPool.class.getPackage().getName());

        public DvnRConnectionPool(int n) {
            RConnectionStack = new DvnRConnection[n];
            numberOfConnections = n;
        }

        public DvnRConnection getConnection(int n) {
            return RConnectionStack[n - 1];
        }

        /** *************************************************************
         * this method _secures_ a pooled connection for the caller.
         * this can be an existing connection that the thread has been using;
         * or a brand new connection created and placed in an empty spot on
         * position on the stack; or a connection created and put in place of
         * an existing connection that was deemed "the least recent".
         *
         * @param workSpaceRemote saved R space ("remote" means saved on the server side)
         * @param workSpaceLocal  R work space saved on the application side
         * @param reestablishConnection boolean indicating if this is an attempt to open a connection for an ongoing R/Graph subsetting session;
         * @param existingConnecton index of a connection already on the stack
         * 
         * @return index of the pooled connection secured and locked for the requestor.
         */
        public int securePooledConnection(String workSpaceRemote, String workSpaceLocal, boolean reestablishConnection, int existingConnection) throws DvnRServiceException {

            DvnRConnection drc = null;

            // return value: index of the secured connection
            int retConnectionNumber = 0;

            try {
                retConnectionNumber = openPooledConnection(workSpaceRemote, existingConnection);

                drc = RConnectionStack[retConnectionNumber - 1];


                // We got a connection; if it's new, it needs to be
                // set up for this R session, by loading the R
                // work space saved on the server side.

                dbgLog.info("send the file over; " + workSpaceRemote);

                if (reestablishConnection) {
                    if (drc.getWorkSpace() == null) {
                        loadWorkSpace(drc.Rcon, workSpaceRemote);
                        drc.setWorkSpace(workSpaceRemote);
                    }

                } else {
                    // we are creating a brand new connection; i.e., there's
                    // no saved workspace file on the R server side. so
                    // we need to first send the file over,
                    // then attempt to load it as the workspace.


                    InputStream inb = new BufferedInputStream(new FileInputStream(workSpaceLocal));
                    int bufsize;
                    byte[] bffr = new byte[1024];

                    RFileOutputStream os =
                            drc.Rcon.createFile(workSpaceRemote);
                    while ((bufsize = inb.read(bffr)) != -1) {
                        os.write(bffr, 0, bufsize);
                    }
                    os.close();
                    inb.close();

                    loadAndClearWorkSpace(drc.Rcon, workSpaceRemote);
                    drc.setWorkSpace(workSpaceRemote);

                }

            //drc.unlockConnection();

            } catch (RException re) {
                throw new DvnRServiceException("init: R runtime error: " + re.getMessage());

            } catch (DvnRServiceException dge) {
                throw dge;
            } catch (RserveException rse) {

                throw new DvnRServiceException("init: RServe error: " + rse.getMessage());
            } catch (REXPMismatchException mme) {
                throw new DvnRServiceException("init: REXP exception");
            } catch (Exception ex) {
                throw new DvnRServiceException("init: unknown exception");
            }

            return retConnectionNumber;
        }

        public synchronized int openPooledConnection(String workSpaceRemote, int existingConnection) throws DvnRServiceException {

            DvnRConnection drc = null;

            int retConnectionNumber = 0;

            try {
                // This is a request to use a connection previously opened
                // for this instance. We may or may not be able to use it again.
                // We need to check if it's still alive, and if it still
                // belongs to this session.

                if (existingConnection > 0) {

                    drc = RConnectionStack[existingConnection - 1];

                    // check if the connection exists, if it's alive, and if it's still ours

                    boolean needNewConnection = false;

                    if (drc != null) {

                        String wspace = drc.getWorkSpace();

                        if (wspace != null) {

                            if (!wspace.equals(workSpaceRemote)) {

                                needNewConnection = true;
                            }
                        } else {
                            needNewConnection = true;
                        }

                        if (drc.Rcon == null || !drc.Rcon.isConnected()) {
                            needNewConnection = true;
                        }

                    } else {
                        needNewConnection = true;
                    }

                    if (!needNewConnection) {
                        drc.lockConnection();
                        return existingConnection;
                    }

                // nope, we'll have to create a new one.
                }


                // Obtain a new Rserve connection and put it on the stack;
                // either in one of the available spots, or in place of the
                // "least recent" connection.

                drc = null;

                int i = 0;

                while (i < numberOfConnections && drc == null) {

                    if (RConnectionStack[i] == null) {
                        RConnectionStack[i] = new DvnRConnection();
                        drc = RConnectionStack[i];

                        if (drc.Rcon == null) {
                            RConnectionStack[i].Rcon = openNewConnection();
                        }

                        //drc = RConnectionStack[i];


                        retConnectionNumber = i + 1;

                    }
                    i++;
                }

                if (retConnectionNumber == 0) {

                    // we've gone through the connections on the stack,
                    // and there are no unused ones.
                    // we'll have to go through the pool one more time, select
                    // the most idle (unlocked) connection, and recycle it.

                    int mostIdleConnection = 0;
                    long leastRecentTimeStamp = 0;

                    for (i = 0; i < numberOfConnections; i++) {
                        drc = RConnectionStack[i];

                        if (drc.Rcon != null && (!drc.isLocked())) {
                            long timeStamp = drc.getLastQueryTime();
                            if (leastRecentTimeStamp != 0) {
                                if (timeStamp < leastRecentTimeStamp) {
                                    leastRecentTimeStamp = timeStamp;
                                    mostIdleConnection = i + 1;
                                }
                            } else {
                                leastRecentTimeStamp = timeStamp;
                                mostIdleConnection = i + 1;
                            }
                        }
                    }

                    if (mostIdleConnection > 0) {
                        // steal this connection!
                        drc = RConnectionStack[mostIdleConnection - 1];

                        // save their workspace:


                        String saveIdleWS = "save.image(file='" + drc.getWorkSpace() + "')";
                        drc.Rcon.voidEval(saveIdleWS);

                        // close it:

                        drc.Rcon.close();

                        drc.Rcon = openNewConnection();
                        retConnectionNumber = mostIdleConnection;
                    } else {
                        // all connections are busy AND locked.
                        // just return an error (for now)

                        throw new DvnRServiceException("Could not connect to R: all available connections are busy.");

                    }

                }

                if (retConnectionNumber > 0 && drc != null && drc.Rcon.isConnected()) {

                    // OK, we got ourselves a connection.

                    drc.lockConnection();
                    drc.setWorkSpace(null);

                    // set the connection time stamp:
                    Date now = new Date();
                    drc.setLastQueryTime(now.getTime());
                } else {
                    throw new DvnRServiceException("Failed to reestablish a connection to the R server.");
                }
            } catch (DvnRServiceException dge) {
                throw dge;
            } catch (RserveException rse) {
                throw new DvnRServiceException("init: RServe error: " + rse.getMessage());
            } catch (Exception ex) {
                throw new DvnRServiceException("init: unknown exception");
            }

            return retConnectionNumber;
        }
    }

    // And the individual R connection class: 

    public class DvnRConnection {
        // constructor

        public DvnRConnection() {
        }
        private boolean Locked = false;
        public RConnection Rcon = null;
        private String SavedRworkSpace = null;
        private long lastQueryTimeStamp;

        public boolean isLocked() {
            return Locked;
        }

        public void lockConnection() {
            Locked = true;
        }

        public void unlockConnection() {
            Locked = false;
        }

        public String getWorkSpace() {
            return SavedRworkSpace;
        }

        public void setWorkSpace(String workSpace) {
            SavedRworkSpace = workSpace;
        }

        public long getLastQueryTime() {
            return lastQueryTimeStamp;
        }

        public void setLastQueryTime(long timeStamp) {
            lastQueryTimeStamp = timeStamp;
        }
    }


}
