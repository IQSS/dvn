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

public class DvnRGraphServiceImpl implements java.io.Serializable {

    // - static filelds
    
    private static Logger dbgLog = Logger.getLogger(DvnRGraphServiceImpl.class.getPackage().getName());

    private static DvnRConnectionPool RConnectionPool = null; 

    
    private int myConnection = 0; 

    // - constants for defining the subset 

    public static String RSUBSETFUNCTION = "RSUBSETFUNCTION";

    // - different kinds of subset functions: 

    public static String MANUAL_QUERY_SUBSET = "MANUAL_QUERY_SUBSET";
    public static String MANUAL_QUERY_TYPE = "MANUAL_QUERY_TYPE";
    public static String MANUAL_QUERY = "MANUAL_QUERY";
    public static String ELIMINATE_DISCONNECTED = "ELIMINATE_DISCONNECTED";
    public static String EDGE_SUBSET = "EDGE_SUBSET";
    public static String VERTEX_SUBSET = "VERTEX_SUBSET";

    public static String AUTOMATIC_QUERY_SUBSET = "AUTOMATIC_QUERY_SUBSET";
    public static String AUTOMATIC_QUERY_TYPE = "AUTOMATIC_QUERY_TYPE";
    public static String AUTOMATIC_QUERY_N_VALUE = "AUTOMATIC_QUERY_N_VALUE";


    public static String NETWORK_MEASURE = "NETWORK_MEASURE"; 
    public static String NETWORK_MEASURE_TYPE = "NETWORK_MEASURE_TYPE"; 
    public static String NETWORK_MEASURE_PARAMETER = "NETWORK_MEASURE_PARAMETER";

    public static String UNDO = "UNDO";
    public static String RESET = "RESET"; 

    // - return result fields:

    public static String SAVED_RWORK_SPACE = "SAVED_RWORK_SPACE";
    public static String NUMBER_OF_VERTICES = "NUMBER_OF_VERTICES"; 
    public static String NUMBER_OF_EDGES = "NUMBER_OF_EDGES"; 
    public static String NETWORK_MEASURE_NEW_COLUMN = "NETWORK_MEASURE_NEW_COLUMN";
    public static String GRAPHML_FILE_EXPORTED  = "GRAPHML_FILE_EXPORTED"; 

    public static String DVN_TMP_DIR=null;
    public static String DSB_TMP_DIR=null;

    private static String GRAPHML_FILE_NAME = "iGraph";
    public static String GRAPHML_FILE_EXT =".xml";

    private static String RDATA_FILE_NAME = "iGraph";
    public static String RDATA_FILE_EXT =".RData";

    private static String RSERVE_HOST = null;
    private static String RSERVE_USER = null;
    private static String RSERVE_PWD = null;    
    private static int RSERVE_PORT;
    private static int RSERVE_CONNECTION_POOLSIZE; 
    private static String DSB_HOST_PORT= null;

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

	if (System.getProperty("dvn.rserve.poolsize") == null) {
	    RSERVE_CONNECTION_POOLSIZE = 10; 
	} else {
	    RSERVE_CONNECTION_POOLSIZE = Integer.parseInt(System.getProperty("dvn.rserve.poolsize")); 
	}
    }

    static String librarySetup= "library('NetworkUtils');";
    boolean DEBUG = true;
    
    // ----------------------------------------------------- instance filelds
    public String IdSuffix = null;
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
        
	RDataFileName = DSB_TMP_DIR + "/" + RDATA_FILE_NAME
                 +"." + IdSuffix + RDATA_FILE_EXT;

	GraphMLfileNameRemote = DSB_TMP_DIR + "/" + GRAPHML_FILE_NAME
                 + "." + IdSuffix + GRAPHML_FILE_EXT;

	if ( RConnectionPool == null ) {
	    dbgLog.info ("number of RServe connections: "+RSERVE_CONNECTION_POOLSIZE); 
	    RConnectionPool = new DvnRConnectionPool ( RSERVE_CONNECTION_POOLSIZE ); 
	}
	
    }


    public void setupWorkingDirectories(RConnection c){
        try{

            // set up the working directory in the designated temp directory
	    // location;
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
    
    private RConnection openNewConnection () throws RserveException {
	RConnection rc = null; 

	try {

	    dbgLog.fine("RSERVE_USER="+RSERVE_USER+"[default=rserve]");
	    dbgLog.fine("RSERVE_PWD="+RSERVE_PWD+"[default=rserve]");
	    dbgLog.fine("RSERVE_PORT="+RSERVE_PORT+"[default=6311]");
	    
	    rc = new RConnection(RSERVE_HOST, RSERVE_PORT);
	    dbgLog.fine("hostname="+RSERVE_HOST);
	
	    rc.login(RSERVE_USER, RSERVE_PWD);

	    // set up the NetworkUtils + dependencies; 
	    // this needs to be done on all new connections. 

	    rc.voidEval(librarySetup);
        } catch (RserveException rse) {
	    throw rse; 
	}

	return rc; 

    }

    private void loadWorkSpace (RConnection rc, String workSpace) throws RException, RserveException, REXPMismatchException  {
	
	try {
	    String cmdResponse = safeEval(rc, "load('"+workSpace+"')").asString();
        } catch (RException re) {
	    throw re; 
        } catch (RserveException rse) {
	    throw rse; 
        } catch (REXPMismatchException rexpm) {
	    throw rexpm; 
	}

    }

    private void loadAndClearWorkSpace (RConnection rc, String workSpace) throws RException, RserveException, REXPMismatchException {
	try {
	    String cmdResponse = safeEval(rc, "load_and_clear('"+workSpace+"')").asString();
        } catch (RException re) {
	    throw re; 
        } catch (RserveException rse) {
	    throw rse; 
        } catch (REXPMismatchException rexpm) {
	    throw rexpm; 
	}

    }

    
    /** *************************************************************
     * initialize the RServe connection and load the graph;
     * keep the open connection.
     *
     * @param sro    a DvnRJobRequest object that contains various parameters
     * @return    a Map that contains various information about the results
     */    
    
    public Map<String, String> initializeConnection(DvnRJobRequest sro) throws DvnRGraphException {
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();

        try {

	    if ( sro != null ) {
		dbgLog.fine("sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
            } else {
		throw new DvnRGraphException ("init: NULL R JOB OBJECT"); 
	    }

	    String SavedRworkSpace = null;  
	    String CachedRworkSpace = sro.getCachedRworkSpace(); 

	    Map <String, Object> SubsetParameters = sro.getParametersForGraphSubset(); 

	    if ( SubsetParameters != null ) {
		SavedRworkSpace = (String) SubsetParameters.get(SAVED_RWORK_SPACE);
	    }
		    
	    if ( SavedRworkSpace != null ) {
		myConnection = RConnectionPool.securePooledConnection ( SavedRworkSpace, null, true, 0 ); 
		RDataFileName = SavedRworkSpace; 

	    } else if ( CachedRworkSpace != null ) {
		myConnection = RConnectionPool.securePooledConnection ( RDataFileName, CachedRworkSpace, false, 0 ); 
	    } else {
		throw new DvnRGraphException ("Initialize method called without either local or remote RData file"); 
	    }

	    if ( myConnection == 0 ) {
		throw new DvnRGraphException ("failed to obtain an R connection"); 
	    } else {
		DvnRConnection drc = RConnectionPool.getConnection(myConnection);
		if ( drc == null ) {
		    throw new DvnRGraphException ("failed to obtain an R connection"); 
		} else {
		    // set the connection time stamp: 
		    Date now = new Date();
		    drc.setLastQueryTime(now.getTime()); 
		    drc.unlockConnection(); 
		}
	    }
		    
	    dbgLog.info ("Initialize: obtained connection "+myConnection); 

	    result.put(SAVED_RWORK_SPACE, RDataFileName);

	    result.put("dsbHost", RSERVE_HOST);
	    result.put("dsbPort", DSB_HOST_PORT);
	    result.put("IdSuffix", IdSuffix);
	} catch (DvnRGraphException dre) {
	    throw dre; 
        }


        return result;
        
    }


    /** *************************************************************
     * close RServe connection;
     * (should I remove the saved workspace(s) on the server side? 
     *
     * @return    a Map that contains diagnostics information 
     * in case of an error. 
     */    

    // do we still need this method? 
    
    public Map<String, String> closeConnection() {
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        
	return result;

    }

    /** *************************************************************
     * checks on the RServe connection status;
     *
     * @return  boolean
     */    
    
    // do we still need this method? 

    public boolean isAlive() {
    
	return false; 
    }


    /** *************************************************************
     * Execute an R-based dvn analysis request on a Graph object
     * using an open connection created during the Initialize call. 
     *
     * @param sro    a DvnJobRequest object that contains various parameters
     * @return    a Map that contains various information about the results
     */    
    
    public Map<String, String> liveConnectionExecute(DvnRJobRequest sro) throws DvnRGraphException {
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();

	if ( sro != null ) {
	    dbgLog.fine("LCE sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
	} else {
	    throw new DvnRGraphException("execute method called with a NULL job ob ject.");

	}
	
	String SavedRworkSpace = null;  

	Map <String, Object> SubsetParameters = sro.getParametersForGraphSubset(); 
	    
	if ( SubsetParameters != null ) {
	    SavedRworkSpace = (String) SubsetParameters.get(SAVED_RWORK_SPACE);
	} else {
	    throw new DvnRGraphException("execute method called with a null parameters object");

	}

	DvnRConnection drc = null;


        try {
	    // let's see if we have a connection that we can use: 

	    if ( myConnection == 0 ) {
		throw new DvnRGraphException("execute method called without securing a connection first");
	    } 

	    myConnection = RConnectionPool.securePooledConnection ( SavedRworkSpace, null, true, myConnection ); 
	    dbgLog.info ("Execute: obtained connection "+myConnection); 


	    drc = RConnectionPool.getConnection(myConnection);


	    String GraphSubsetType = (String) SubsetParameters.get(RSUBSETFUNCTION); 

	    if ( GraphSubsetType != null ) {
		
		if ( GraphSubsetType.equals(MANUAL_QUERY_SUBSET) ) {

		    String manualQueryType  = (String) SubsetParameters.get(MANUAL_QUERY_TYPE); 
		    String manualQuery = (String) SubsetParameters.get(MANUAL_QUERY); 

		    String subsetCommand = null; 

		    if ( manualQueryType != null ) {
			if (manualQueryType.equals(EDGE_SUBSET)) {
			    String dropDisconnected = (String) SubsetParameters.get(ELIMINATE_DISCONNECTED); 
			    if ( dropDisconnected != null ) {
				subsetCommand = "edge_subset(g, '"+manualQuery+"', TRUE)"; 
			    } else {
				subsetCommand = "edge_subset(g, '"+manualQuery+"')"; 
			    }

			} else if (manualQueryType.equals(VERTEX_SUBSET)){
			    subsetCommand = "vertex_subset(g, '"+manualQuery+"')"; 
			} else {
			    throw new DvnRGraphException("execute: unsupported manual query subset");

			}		       

			dbgLog.fine("LCE: manualQuerySubset="+subsetCommand);
			historyEntry.add(subsetCommand);
			String cmdResponse = safeEval(drc.Rcon,subsetCommand).asString();
			
		    }
		    
		} else if ( GraphSubsetType.equals(NETWORK_MEASURE) ) {
		    String networkMeasureType = (String) SubsetParameters.get(NETWORK_MEASURE_TYPE); 
		    String networkMeasureCommand = null; 
		    if ( networkMeasureType != null ) {
			    List<NetworkMeasureParameter> networkMeasureParameterList = (List<NetworkMeasureParameter>)SubsetParameters.get(NETWORK_MEASURE_PARAMETER);
                networkMeasureCommand = networkMeasureType + "(g" + buildParameterComponent(networkMeasureParameterList) + ")";
		    }

		    if ( networkMeasureCommand == null ) {
			throw new DvnRGraphException("ILLEGAL OR UNSUPPORTED NETWORK MEASURE QUERY");

		    }

		    historyEntry.add(networkMeasureCommand);
		    String addedColumn = safeEval(drc.Rcon,networkMeasureCommand).asString();

		    if ( addedColumn != null ) {
			result.put(NETWORK_MEASURE_NEW_COLUMN, addedColumn);
		    } else {
			throw new DvnRGraphException("FAILED TO READ ADDED COLUMN NAME");

		    }
		    
		} else if ( GraphSubsetType.equals(AUTOMATIC_QUERY_SUBSET) ) {
		    String automaticQueryType = (String) SubsetParameters.get(AUTOMATIC_QUERY_TYPE); 
		    String autoQueryCommand = null; 
		    if ( automaticQueryType != null ) {
            String n = (String) SubsetParameters.get(AUTOMATIC_QUERY_N_VALUE);
            autoQueryCommand = automaticQueryType + "(g, " + n + ")";

		    }

		    if ( autoQueryCommand == null ) {
			throw new DvnRGraphException("NULL OR UNSUPPORTED AUTO QUERY");
		    }

		    historyEntry.add(autoQueryCommand);
		    String cEval = safeEval(drc.Rcon, autoQueryCommand).asString();

		} else if ( GraphSubsetType.equals(UNDO) ) {
		    String cEval = safeEval(drc.Rcon, "undo()").asString();
		} else if ( GraphSubsetType.equals(RESET) ) {
		    dbgLog.info("resetting the workspace; using reset("+SavedRworkSpace+")");
		    
		    String cEval = safeEval(drc.Rcon, "reset('"+SavedRworkSpace+"')").asString();


		}

	    }

	    // get the vertices and edges counts: 

	    String countCommand = "vcount(g)";
	    int countResponse = safeEval(drc.Rcon, countCommand).asInteger(); 
	    result.put(NUMBER_OF_VERTICES, Integer.toString(countResponse)); 

	    countCommand = "ecount(g)";
	    countResponse = safeEval(drc.Rcon, countCommand).asInteger(); 
	    result.put(NUMBER_OF_EDGES, Integer.toString(countResponse)); 

            
	    result.put( SAVED_RWORK_SPACE, RDataFileName ); 

	    // we're done; let's add some potentially useful 
	    // information to the result and return: 

	    String RexecDate = drc.Rcon.eval("as.character(as.POSIXct(Sys.time()))").asString();
	    String RversionLine = "R.Version()$version.string";
            String Rversion = drc.Rcon.eval(RversionLine).asString();
            
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("IdSuffix", IdSuffix);
            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

	    
	    if ( !SavedRworkSpace.equals(drc.getWorkSpace()) ) {
		throw new DvnRGraphException("Could not execute query: connection lost");
	    }

	} catch (DvnRGraphException dre) {
	    throw dre; 
	} catch (RException re) {
	    throw new DvnRGraphException(re.getMessage());

        } catch (RserveException rse) {

            dbgLog.info("LCE: rserve exception message: "+rse.getMessage());
            dbgLog.info("LCE: rserve exception description: "+rse.getRequestErrorDescription());
            throw new DvnRGraphException("RServe failure: "+rse.getMessage());
	    
        } catch (REXPMismatchException mme) {
        
            throw new DvnRGraphException("REXPmismatchException occured");

        } catch (Exception ex){
            
            throw new DvnRGraphException("Execute: unknown exception occured: " +ex.getMessage());

        } finally {
	    if ( drc != null ) {
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
    
     public Map<String, String> liveConnectionExport (String savedRDataFile) throws DvnRGraphException {

        Map<String, String> result = new HashMap<String, String>();

	DvnRConnection drc = null;

        
	try {

	    // let's see if we have a connection that we can use: 

	    if ( myConnection == 0 ) {
		throw new DvnRGraphException("execute method called without creating a connection first");
	    } 

	    myConnection = RConnectionPool.securePooledConnection ( savedRDataFile, null, true, myConnection ); 
	    dbgLog.info ("Export: obtained connection "+myConnection); 


	    drc = RConnectionPool.getConnection(myConnection);


	    String exportCommand = "dump_graphml(g, '" + GraphMLfileNameRemote + "')";
	    dbgLog.fine(exportCommand);
	    historyEntry.add(exportCommand);
	    String cmdResponse = safeEval(drc.Rcon, exportCommand).asString(); 

	    exportCommand = "dump_tab(g, '" + DSB_TMP_DIR + "/temp_" + IdSuffix + ".tab')";
	    dbgLog.fine(exportCommand);
	    historyEntry.add(exportCommand);
	    cmdResponse = safeEval(drc.Rcon, exportCommand).asString();


	    File zipFile  = new File(TEMP_DIR, "subset_" + IdSuffix + ".zip");
	    FileOutputStream zipFileStream = new FileOutputStream(zipFile);
	    ZipOutputStream zout = new ZipOutputStream( new FileOutputStream(zipFile) );

	    addZipEntry(drc.Rcon, zout, GraphMLfileNameRemote, "data/subset.xml");
	    addZipEntry(drc.Rcon, zout, DSB_TMP_DIR + "/temp_" + IdSuffix + "_verts.tab", "data/vertices.tab");
	    addZipEntry(drc.Rcon, zout, DSB_TMP_DIR + "/temp_" + IdSuffix + "_edges.tab", "data/edges.tab");

	    zout.close();
	    zipFileStream.close();

	    result.put(GRAPHML_FILE_EXPORTED, zipFile.getAbsolutePath());

	    String RexecDate = drc.Rcon.eval("as.character(as.POSIXct(Sys.time()))").asString();
	    String RversionLine = "R.Version()$version.string";
            String Rversion = drc.Rcon.eval(RversionLine).asString();
            
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("IdSuffix", IdSuffix);

            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            dbgLog.fine("result object (before closing the Rserve):\n"+result);

	} catch (DvnRGraphException dre) {
	    throw dre; 
	} catch (RException re) {
            throw new DvnRGraphException("R run-time error: " +re.getMessage());
        } catch (RserveException rse) {
            dbgLog.info("LCE: rserve exception message: "+rse.getMessage());
            dbgLog.info("LCE: rserve exception description: "+rse.getRequestErrorDescription());
            throw new DvnRGraphException("RServe failure: "+rse.getMessage());
	    
        } catch (REXPMismatchException mme) {
            throw new DvnRGraphException("REXPmismatchException occured");

        } catch (Exception ex){
            throw new DvnRGraphException("Unknown exception occured: " +ex.getMessage());
        } finally {
	    if ( drc != null ) {
		// set the connection time stamp: 
		Date now = new Date();
		drc.setLastQueryTime(now.getTime()); 

		drc.unlockConnection(); 
	    }
	}
        return result;
        
    }
    
    private String buildParameterComponent(List<NetworkMeasureParameter> parameters) {
        String returnString = "";
        if (parameters != null) {
            for (NetworkMeasureParameter param : parameters) {
                if ( !StringUtil.isEmpty(param.getValue()) ) {
                    returnString += ", " + param.getName() + "=" + param.getValue();
                }
            }
        }
        return returnString;
    }
    
    /** *************************************************************
     * Execute an R-based "ingest" of a GraphML file
     *
     * @param graphMLfileName;
     * @param cachedRDatafileName;
     * @return    a Map that contains various information about the results
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
	    String responseVoid = safeEval(c,ingestCommand).asString();
	     
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
        
	} catch (RException re) {
	    result.put("IdSuffix", IdSuffix);
	    result.put("RCommandHistory",  StringUtils.join(historyEntry,"\n"));
	    result.put("RexecError", "true");
	    result.put("RexecErrorMessage", re.getMessage());
	    result.put("RexecErrorDescription", "R runtime Error");

	    dbgLog.info("rserve exception message: "+ re.getMessage());
	    dbgLog.info("rserve exception description: "+ "R runtime Error");
	    return result;
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

    public class RException extends Exception {

	public RException(String msg) {
	    super("\"" + msg + "\"");
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

    public class DvnRGraphException extends Exception {

	public DvnRGraphException(String msg) {
	    super(msg);
	}
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

	public DvnRConnection getConnection (int n) {
	    return RConnectionStack[n-1]; 
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

	public int securePooledConnection ( String workSpaceRemote, String workSpaceLocal, boolean reestablishConnection, int existingConnection ) throws DvnRGraphException {	    

	    DvnRConnection drc = null; 

	    // return value: index of the secured connection
	    int retConnectionNumber = 0; 

	    try {
		retConnectionNumber = openPooledConnection ( workSpaceRemote, existingConnection ); 
	    
		drc = RConnectionStack[retConnectionNumber-1];
		

		// We got a connection; if it's new, it needs to be 
		// set up for this R session, by loading the R 
		// work space saved on the server side. 
	    
		dbgLog.info("send the file over; "+workSpaceRemote); 

		if ( reestablishConnection ) { 
		    if (drc.getWorkSpace() == null) {
			loadWorkSpace ( drc.Rcon, workSpaceRemote  ); 
			drc.setWorkSpace ( workSpaceRemote ); 
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

		    loadAndClearWorkSpace ( drc.Rcon, workSpaceRemote ); 
		    drc.setWorkSpace ( workSpaceRemote ); 

		}

		//drc.unlockConnection(); 

	    } catch (RException re) {
		throw new DvnRGraphException ("init: R runtime error: " + re.getMessage()); 

	    } catch (DvnRGraphException dge) {
		throw dge; 
	    } catch (RserveException rse) {

		throw new DvnRGraphException ("init: RServe error: "+rse.getMessage()); 
	    } catch (REXPMismatchException mme) {
		throw new DvnRGraphException ("init: REXP exception");
	    } catch (Exception ex){
		throw new DvnRGraphException ("init: unknown exception"); 
	    }
        
	    return retConnectionNumber; 
	}

	public synchronized int openPooledConnection ( String workSpaceRemote, int existingConnection ) throws DvnRGraphException {	    

	    DvnRConnection drc = null; 

	    int retConnectionNumber = 0; 	    

	    try {
		// This is a request to use a connection previously opened 
		// for this instance. We may or may not be able to use it again.
		// We need to check if it's still alive, and if it still 
		// belongs to this session. 

		if ( existingConnection > 0 ) {
		    
		    drc = RConnectionStack[existingConnection-1];

		    // check if the connection exists, if it's alive, and if it's still ours

		    boolean needNewConnection = false; 

		    if ( drc != null ) {

			String wspace = drc.getWorkSpace(); 

			if ( wspace != null ) {

			    if ( !wspace.equals(workSpaceRemote) ) {

				needNewConnection = true; 
			    } 
			} else {
			    needNewConnection = true; 
			}

			if ( drc.Rcon == null || !drc.Rcon.isConnected() ) {
			    needNewConnection = true; 
			}

		    } else {
			needNewConnection = true; 
		    }

		    if ( !needNewConnection ) {
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
		
		while ( i < numberOfConnections && drc == null ) {
		
		    if ( RConnectionStack[i] == null ) { 
			RConnectionStack[i] = new DvnRConnection(); 
			drc = RConnectionStack[i];

			if ( drc.Rcon == null ) {
			    RConnectionStack[i].Rcon = openNewConnection(); 
			} 
			
			//drc = RConnectionStack[i];

			
			retConnectionNumber = i + 1; 

		    } 
		    i++;
		}
	    
		if ( retConnectionNumber == 0 ) {

		    // we've gone through the connections on the stack, 
		    // and there are no unused ones.
		    // we'll have to go through the pool one more time, select
		    // the most idle (unlocked) connection, and recycle it. 

		    int mostIdleConnection = 0; 
		    long leastRecentTimeStamp = 0; 

		    for ( i =0; i < numberOfConnections; i++ ) {
			drc = RConnectionStack[i];

			if ( drc.Rcon != null && ( ! drc.isLocked() ) ) {
			    long timeStamp = drc.getLastQueryTime(); 
			    if ( leastRecentTimeStamp != 0 ) {
				if ( timeStamp < leastRecentTimeStamp ) {
				    leastRecentTimeStamp = timeStamp; 
				    mostIdleConnection = i+1;
				}
			    } else {
				leastRecentTimeStamp = timeStamp; 
				mostIdleConnection = i+1;
			    }
			}
		    }
		
		    if ( mostIdleConnection > 0 ) {
			// steal this connection!
			drc = RConnectionStack[mostIdleConnection-1];
			
			// save their workspace: 
			
			
			String saveIdleWS = "save.image(file='"+ drc.getWorkSpace() +"')";
			drc.Rcon.voidEval(saveIdleWS);

			// close it: 

			drc.Rcon.close(); 
		    
			drc.Rcon = openNewConnection(); 
			retConnectionNumber = mostIdleConnection; 
		    } else {
			// all connections are busy AND locked. 
			// just return an error (for now)
		    
			throw new DvnRGraphException ("Could not connect to R: all available connections are busy."); 
		    
		    }
		
		}

		if ( retConnectionNumber > 0 
		     && drc != null 
		     && drc.Rcon.isConnected() ) {
		    
		    // OK, we got ourselves a connection. 

		    drc.lockConnection(); 
		    drc.setWorkSpace(null); 

		    // set the connection time stamp: 
		    Date now = new Date(); 
		    drc.setLastQueryTime(now.getTime()); 
		} else { 
		    throw new DvnRGraphException ("Failed to reestablish a connection to the R server."); 
		}
	    } catch (DvnRGraphException dge) {
		throw dge; 
	    } catch (RserveException rse) {
		throw new DvnRGraphException ("init: RServe error: "+rse.getMessage()); 
	    } catch (Exception ex){
		throw new DvnRGraphException ("init: unknown exception"); 
	    }
        
	    return retConnectionNumber; 
	}

    }

    public class DvnRConnection {
	// constructor 
	public DvnRConnection(){}
	private boolean Locked = false; 

	public RConnection Rcon = null;

	private String SavedRworkSpace = null;  
	
	private long lastQueryTimeStamp; 

	public boolean isLocked () {
	    return Locked; 
	}

	public void lockConnection () {
	    Locked = true; 
	}

	public void unlockConnection () {
	    Locked = false; 
	}

	public String getWorkSpace () {
	    return SavedRworkSpace; 
	}

	public void setWorkSpace ( String workSpace ) {
	    SavedRworkSpace = workSpace; 
	}

	public long getLastQueryTime () {
	    return lastQueryTimeStamp; 
	}

	public void setLastQueryTime (long timeStamp) {
	    lastQueryTimeStamp = timeStamp; 
	}

    }


}
