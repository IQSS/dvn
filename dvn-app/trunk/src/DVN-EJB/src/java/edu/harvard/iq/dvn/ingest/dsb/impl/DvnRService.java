/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb.impl;

import java.util.*;
import java.util.logging.*;


import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import org.apache.commons.lang.*;

/**
 *
 * @author landreev
 */

public class DvnRService implements java.io.Serializable {

    // - static filelds
    
    private static Logger dbgLog = Logger.getLogger(DvnRService.class.getPackage().getName());

    private static DvnRConnectionPool RConnectionPool = null; 

    
    private int myConnection = 0; 

    public static String TMP_DIR_REMOTE=null;

    private static String RDATA_FILE_NAME = "data";
    public static String RDATA_FILE_EXT =".RData";

    private static String RSERVE_HOST = null;
    private static String RSERVE_USER = null;
    private static String RSERVE_PWD = null;    
    private static int RSERVE_PORT;
    private static int RSERVE_CONNECTION_POOLSIZE; 

    public static String TEMP_DIR = System.getProperty("java.io.tmpdir");
    
    static {
    
        TMP_DIR_REMOTE = System.getProperty("dvn.temp.dir");
        
        // fallout case: last resort
        if (TMP_DIR_REMOTE == null){
            TMP_DIR_REMOTE = "/tmp";
        }
        
        RSERVE_HOST = System.getProperty("dvn.rserve.host");
        // the defaults are for testing only
        if (RSERVE_HOST == null){
            RSERVE_HOST = "dvndb-qa1.hmdc.harvard.edu";
        }
                
        RSERVE_USER = System.getProperty("dvn.rserve.user");
        if (RSERVE_USER == null){
            RSERVE_USER = "rserve";
        }
        
        RSERVE_PWD = System.getProperty("dvn.rserve.password");
        if (RSERVE_PWD == null){
            RSERVE_PWD = "rserve";
        }
        

        if (System.getProperty("dvn.rserve.port") == null ){
            RSERVE_PORT= 6311;
        } else {
            RSERVE_PORT = Integer.parseInt(System.getProperty("dvn.rserve.port"));
        }

        if (System.getProperty("dvn.rserve.poolsize") == null) {
            RSERVE_CONNECTION_POOLSIZE = 10;
        } else {
            RSERVE_CONNECTION_POOLSIZE = Integer.parseInt(System.getProperty("dvn.rserve.poolsize"));
        }
    }

    private String librarySetup = null;

    boolean DEBUG = true;
    
    public String IdSuffix = null;
    public String RDataFileName = null;
    public List<String> historyEntry = new ArrayList<String>();
    public List<String> replicationFile = new LinkedList<String>();
    
    // ----------------------------------------------------- constructor
    public DvnRService(){
        
        // initialization
        IdSuffix = RandomStringUtils.randomNumeric(6);
                 
        
        RDataFileName = TMP_DIR_REMOTE + "/" + RDATA_FILE_NAME
            +"." + IdSuffix + RDATA_FILE_EXT;

        if ( RConnectionPool == null ) {
            dbgLog.fine ("number of RServe connections: "+RSERVE_CONNECTION_POOLSIZE);
            RConnectionPool = new DvnRConnectionPool ( RSERVE_CONNECTION_POOLSIZE );
        }
	
    }

    public void setupWorkingDirectories(RConnection c) throws RserveException {
        // set up the working directory in the designated temp directory
        // location;

        // Not necessary anymore;
        // Keeping the function around, just in case any additional setup becomes
        // necessary again.
    }

    public void setInitialSetup (String initialSetup) {
        librarySetup = initialSetup;
    }

    public String getInitialSetup () {
        return librarySetup;
    }

    private RConnection openNewConnection(String initialSetup) throws RserveException {
        RConnection rc = null;

        dbgLog.fine("RSERVE_USER=" + RSERVE_USER + "[default=rserve]");
        dbgLog.fine("RSERVE_PWD=" + RSERVE_PWD + "[default=rserve]");
        dbgLog.fine("RSERVE_PORT=" + RSERVE_PORT + "[default=6311]");

        rc = new RConnection(RSERVE_HOST, RSERVE_PORT);
        dbgLog.fine("hostname=" + RSERVE_HOST);

        rc.login(RSERVE_USER, RSERVE_PWD);

        // load the required libraries.
        // this needs to be done on all new connections.

        if (initialSetup != null && (!initialSetup.equals(""))) {
            rc.voidEval(initialSetup);
        }

        return rc;

    }

    private void loadWorkSpace (RConnection rc, String workSpace) throws RException, RserveException, REXPMismatchException  {
	
	    String cmdResponse = safeEval(rc, "load('"+workSpace+"')").asString();

        // TODO:
        // Exceptions are expected to be thrown when error conditions are
        // encountered. Still, we should also check the command response
        // string in case it contains any diagnostics information.

    }

    
    /** *************************************************************
     * initialize the RServe connection and load the data file;
     * keep the connection open.
     *
     */
    

    public String initializeConnection(REXP initialData, String initialSetup, String savedRworkSpace) throws DvnRServiceException {

        setInitialSetup(initialSetup);

        if (savedRworkSpace != null) {
            myConnection = RConnectionPool.securePooledConnection(savedRworkSpace, null, true, 0);
            RDataFileName = savedRworkSpace;

        } else if (initialData != null) {
            myConnection = RConnectionPool.securePooledConnection(RDataFileName, initialData, false, 0);

        } else {
            throw new DvnRServiceException("Initialize method called without either a matrix or remote session data file");
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

        dbgLog.fine("Initialize: obtained connection " + myConnection);

        return RDataFileName;

    }

    //  Creates a new connection with an integer matrix as the data frame:

    public String initializeConnectionWithIntegerDataframe(int[][] cMatrix, String initialSetup, String savedRworkSpace) throws DvnRServiceException {
        RList rowVectorsList = new RList();

        REXP matrixData = null;

        for (int j = 0; j < cMatrix[0].length; j++) {
            int[] column = new int[cMatrix.length];

            for (int i = 0; i < cMatrix.length; i++) {
                column[i] = cMatrix[i][j];
            }
            REXPInteger rowVector = new REXPInteger(column);
            rowVectorsList.add(rowVector);

        }

        try {
            matrixData = org.rosuda.REngine.REXP.createDataFrame(rowVectorsList);
        } catch (REXPMismatchException rmme) {
            throw new DvnRServiceException ("Data type mismatch: "+rmme.getMessage());
        }

        
        return initializeConnection(matrixData, initialSetup, savedRworkSpace);
    }

    // New connection with a matrix of Doubles as the data frame:

    public String initializeConnectionWithDoubleDataframe(double[][] cMatrix, String initialSetup, String savedRworkSpace) throws DvnRServiceException {
        RList rowVectorsList = new RList();

        REXP matrixData = null;

        for (int j = 0; j < cMatrix[0].length; j++) {
            double[] column = new double[cMatrix.length];

            for (int i = 0; i < cMatrix.length; i++) {
                column[i] = cMatrix[i][j];
            }
            REXPDouble rowVector = new REXPDouble(column);
            rowVectorsList.add(rowVector);

        }

        try {
            matrixData = org.rosuda.REngine.REXP.createDataFrame(rowVectorsList);
        } catch (REXPMismatchException rmme) {
            throw new DvnRServiceException ("Data type mismatch: "+rmme.getMessage());
        }


        return initializeConnection(matrixData, initialSetup, savedRworkSpace);
    }

    // We may need to add more versions of the "initialize function above, to
    // support creating original data frames of different types;
    // Will be trivial to do.


    /** *************************************************************
     * Execute an R command.
     * Returns a generic R expression, that needs to be properly interpreted
     * by the application code.
     *
     */

    public REXP executeRCommand(String savedRworkSpace, String rCommand) throws DvnRServiceException {

        // set the return object

        REXP resultExpression;

        if (savedRworkSpace == null) {
            throw new DvnRServiceException("execute method called with a null " 
                    + "session Id (i.e. file name of the saved R work space).");
        }

        DvnRConnection drc = null;


        try {
            // let's see if we have a connection that we can use:

            if (myConnection == 0) {
                throw new DvnRServiceException("execute method called without securing a connection first");
            }


            myConnection = RConnectionPool.securePooledConnection(savedRworkSpace, null, true, myConnection);
            dbgLog.fine("Execute: obtained connection " + myConnection);

            drc = RConnectionPool.getConnection(myConnection);

            resultExpression = safeEval(drc.Rcon, rCommand);


            if (!savedRworkSpace.equals(drc.getWorkSpace())) {
                throw new DvnRServiceException("Could not execute query: connection lost");
            }

        } catch (DvnRServiceException dre) {
            throw dre;

        } catch (RException re) {
            throw new DvnRServiceException(re.getMessage());

        } catch (RserveException rse) {

            dbgLog.fine("rserve exception message: " + rse.getMessage());
            dbgLog.fine("rserve exception description: " + rse.getRequestErrorDescription());
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

        return resultExpression;

    }

   /** *************************************************************
     * Creates an R integer vector value and assigns it to an
     * R expression in an ongoing R session:
     *
     */

    public void assignRIntegerVector (String savedRworkSpace, String vectorName, int[] intVector) throws DvnRServiceException {

        if (intVector == null) {
            throw new DvnRServiceException("Assign method called with a NULL integer vector.");

        }


        if (savedRworkSpace == null) {
            throw new DvnRServiceException("Assign method called with a null "
                    + "session Id (i.e. file name of the saved R work space).");
        }

        DvnRConnection drc = null;


        try {
            // let's see if we have a connection that we can use:

            if (myConnection == 0) {
                throw new DvnRServiceException("assignment method called without securing a connection first");
            }


            myConnection = RConnectionPool.securePooledConnection(savedRworkSpace, null, true, myConnection);
            dbgLog.fine("Execute: obtained connection " + myConnection);

            drc = RConnectionPool.getConnection(myConnection);

            REXPInteger RintVector = new REXPInteger(intVector);
            drc.Rcon.assign (vectorName, RintVector);

            if (!savedRworkSpace.equals(drc.getWorkSpace())) {
                throw new DvnRServiceException("Could not execute query: connection lost");
            }

        } catch (DvnRServiceException dre) {
            throw dre;

        } catch (RserveException rse) {

            dbgLog.fine("rserve exception message: " + rse.getMessage());
            dbgLog.fine("rserve exception description: " + rse.getRequestErrorDescription());
            throw new DvnRServiceException("RServe failure: " + rse.getMessage());

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
    }



   /** *************************************************************
     * Creates an R string vector value and assigns it to an
     * R expression in an ongoing R session:
     *
     */

    public void assignStringVector (String savedRworkSpace, String vectorName, String[] stringVector) throws DvnRServiceException {

        if (stringVector == null) {
            throw new DvnRServiceException("Assign method called with a NULL string vector.");

        }


        if (savedRworkSpace == null) {
            throw new DvnRServiceException("execute method called with a null "
                    + "session Id (i.e. file name of the saved R work space).");
        }

        DvnRConnection drc = null;


        try {
            // let's see if we have a connection that we can use:

            if (myConnection == 0) {
                throw new DvnRServiceException("assignment method called without securing a connection first");
            }


            myConnection = RConnectionPool.securePooledConnection(savedRworkSpace, null, true, myConnection);
            dbgLog.fine("Execute: obtained connection " + myConnection);

            drc = RConnectionPool.getConnection(myConnection);

            REXPString RstringVector = new REXPString(stringVector);
            drc.Rcon.assign (vectorName, RstringVector);

            if (!savedRworkSpace.equals(drc.getWorkSpace())) {
                throw new DvnRServiceException("Could not execute query: connection lost");
            }

        } catch (DvnRServiceException dre) {
            throw dre;

        } catch (RserveException rse) {

            dbgLog.fine("rserve exception message: " + rse.getMessage());
            dbgLog.fine("rserve exception description: " + rse.getRequestErrorDescription());
            throw new DvnRServiceException("RServe failure: " + rse.getMessage());

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
    }


    // We will likely need more assignment functions for other data types.
    // Will be trivial to add.

    /** *************************************************************
     * close RServe connection;
     */

    public void closeConnection(String savedRworkSpace) {

        if (myConnection != 0) {
            RConnectionPool.closePooledConnection(savedRworkSpace, myConnection);
        }
    }



    // -- utilitiy methods
    
        
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
         * @param workSpaceRemote saved R space ("remote" means saved on the server side);
         *        it is also used as the session id tag.
         * @param cMatrix  words/doc matrix, on the application side
         * @param reestablishConnection boolean indicating if this is an attempt to open a connection for an ongoing session;
         * @param existingConnecton index of a connection already on the stack
         * 
         * @return index of the pooled connection secured and locked for the requestor.
         */


         public int securePooledConnection(String workSpaceRemote, REXP initialData, boolean reestablishConnection, int existingConnection) throws DvnRServiceException {

            DvnRConnection drc = null;

            // return value: index of the secured connection
            int retConnectionNumber = 0;

            try {
                retConnectionNumber = openPooledConnection(workSpaceRemote, existingConnection);

                drc = RConnectionStack[retConnectionNumber - 1];


                // We got a connection; if it's new, it needs to be
                // set up for this R session, by loading the right
                // data matrix.

                if (reestablishConnection) {
                    if (drc.getWorkSpace() == null) {
                        loadWorkSpace(drc.Rcon, workSpaceRemote);
                        drc.setWorkSpace(workSpaceRemote);
                    }

                } else {
                    // we are creating a brand new connection; i.e., there's
                    // no saved workspace file on the R server side. so
                    // we need to first create an R matrix and load it in the
                    // remote workspace.

                    drc.Rcon.assign ("data", initialData);

                    drc.setWorkSpace(workSpaceRemote);


                    String saveWS = "save.image(file='" + drc.getWorkSpace() + "')";
                    drc.Rcon.voidEval(saveWS);


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

                        RConnectionStack[i].Rcon = openNewConnection(getInitialSetup());


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

                        drc.Rcon = openNewConnection(getInitialSetup());
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

        public synchronized void closePooledConnection (String workSpaceRemote, int existingConnection) {

            if (existingConnection > 0) {
                DvnRConnection drc = RConnectionStack[existingConnection - 1];

                // check if the connection exists, if it's alive, and if it's still ours

                if (drc != null) {
                    String wspace = drc.getWorkSpace();

                    if (wspace != null) {
                        if (wspace.equals(workSpaceRemote)) {

                            // close it (if alive):
                            if (drc.Rcon.isConnected()) {
                                drc.Rcon.close();
                            }

                            // free the space on the stack:
                            RConnectionStack[existingConnection - 1] = null;
                        }
                    }
                }
            }

            // And if any of the conditions above are not true, it means that
            // the connection has already been closed or recycled.

        }
    } // end of DvnRConnectionPool class

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


    public static void main(String[] args) {

        DvnRService rs = new DvnRService();

        int[][] matrix = {{1,2,3},{4,5,6},{7,8,9}};
        int[] testClustVector = {0,0,0};


        // the initial library setup:
        // these are semicolon-separated R commands that need to be executed
        // once, when a new connection is created.
        // Usually this will be a list of libraries and source files.

        String initialLibrarySetup = "source('/usr/lib64/R/share/dvn/Labelling.R'); "
                + "library(MASS); "
                + "library(randomForest);";

        try {
            // Initialize R connection,
            // with the integer matrix as the main data frame.
            //
            // !This only needs to be done once!

            String RsessionId = rs.initializeConnectionWithIntegerDataframe(matrix, initialLibrarySetup, null);


            // Create an R integer vector, to be used as a parameter by the
            // next R function:

            rs.assignRIntegerVector(RsessionId, "clust", testClustVector);

            // Execute an R command:

            // (the function below has 2 parameters; note that the second parameter
            // is a vector variable that was created in the previous step).

            String rCommand = "meansLabel(3, clust)";

            // We expect the command to produce a vector of integers as the
            // output, hence we use ".asIntegers()" below:

            int[] labelledVector = rs.executeRCommand(RsessionId, rCommand).asIntegers();

            // If you need to execute an R command that returns a character string,
            // use .asString():
            //String[] stringVector = rs.executeRCommand(RsessionId, rCommand).asStrings();
            // a vector of character strings -- .asStrings()
            // ... etc.

            if (labelledVector != null) {
                for (int i = 0; i < labelledVector.length; i++) {
                    System.out.println("result: "+labelledVector[i]);
                }
            } else {
                System.out.println("NULL response from the labelling command");
            }

            rs.closeConnection(RsessionId);
            
        } catch (DvnRServiceException drse) {
            System.out.println("Exception caught:");
            System.out.println(drse.getMessage());
        } catch (REXPMismatchException rmme) {
            System.out.println("Exception: the return result is not a vector of Integers!");
            System.out.println(rmme.getMessage());
        }

    }


}
