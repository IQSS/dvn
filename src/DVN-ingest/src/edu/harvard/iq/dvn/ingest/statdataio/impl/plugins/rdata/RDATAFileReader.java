/*
   Copyright (C) 2005-2013, by the President and Fellows of Harvard College.

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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.rdata;

/*
 * @author Matt Owen
 * @date 02-20-2013
 */
import java.io.*;
import java.text.*;
import java.util.logging.*;
import java.util.*;
import java.security.NoSuchAlgorithmException;

// Rosunda's Wrappers and Methods for R-calls to Rserve
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RFileInputStream;
import org.rosuda.REngine.Rserve.RFileOutputStream;
import org.rosuda.REngine.Rserve.*;

// DVN-made parts
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;
import edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util.*;
import edu.harvard.iq.dvn.rserve.*;
import edu.harvard.iq.dvn.unf.*;
import edu.harvard.iq.dvn.unf.UNF5Util;
import edu.harvard.iq.dvn.ingest.thedata.helpers.*;
import nesstar.util.FileUtils;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.ArrayUtils;
/**
 * A DVN-Project-implementation of <code>StatDataFileReader</code> for the 
 * RData Binary Format.
 * 
 * @author Matthew Owen
 * @date 02-11-2012
 *
 * This implementation uses R-Scripts to do the bulk of the processing.
 * @note The code is primarily based on the SAV and CSV file readers.
 */
public class RDATAFileReader extends StatDataFileReader {
  
  // Formats for R
  public static final int FORMAT_INTEGER = 0;
  public static final int FORMAT_NUMERIC = 1;
  public static final int FORMAT_STRING = -1;
  public static final int FORMAT_DATE = -2;
  public static final int FORMAT_DATETIME = -3;
  
  // FORMAT CATEGORY TABLE
  public static Map <String, String> R_FORMAT_CATEGORY_TABLE;
  
  private int [] mFormatTable = null;
  
  // Date-time things
  public static final String[] FORMATS = { "other", "date", "date-time", "date-time-timezone" };

  // R-ingest recognition files
  private static final String[] FORMAT_NAMES = { "RDATA", "Rdata", "rdata" };
  private static final String[] EXTENSIONS = { "Rdata", "rdata" };
  private static final String[] MIME_TYPE = { "application/x-rlang-transport" };
  
  // R Scripts
  static private String RSCRIPT_CREATE_WORKSPACE;
  static private String RSCRIPT_DATASET_INFO_SCRIPT = "";
  static private String RSCRIPT_GET_DATASET = "";
  static private String RSCRIPT_GET_LABELS = "";
  static private String RSCRIPT_WRITE_DVN_TABLE = "";
  
  // RServe static variables
  private static String RSERVE_HOST = System.getProperty("vdc.dsb.host");
  private static String RSERVE_USER = System.getProperty("vdc.dsb.rserve.user");
  private static String RSERVE_PASSWORD = System.getProperty("vdc.dsb.rserve.pwrd");
  private static int RSERVE_PORT;
  
  public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
  public static final String DSB_TEMP_DIR = System.getProperty("vdc.dsb.temp.dir");
  public static final String DVN_TEMP_DIR = null;
  public static final String WEB_TEMP_DIR = null;

  // DATE FORMATS
  private static SimpleDateFormat[] DATE_FORMATS = new SimpleDateFormat[] {
    new SimpleDateFormat("yyyy-MM-dd")
  };
  
  // TIME FORMATS
  private static SimpleDateFormat[] TIME_FORMATS = new SimpleDateFormat[] {
    // Date-time up to milliseconds with timezone, e.g. 2013-04-08 13:14:23.102 -0500
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z"),
    // Date-time up to milliseconds, e.g. 2013-04-08 13:14:23.102
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"),
    // Date-time up to seconds with timezone, e.g. 2013-04-08 13:14:23 -0500
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"),
    // Date-time up to seconds and no timezone, e.g. 2013-04-08 13:14:23
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  };
  
  // Logger
  private static final Logger LOG = Logger.getLogger(RDATAFileReader.class.getPackage().getName());

  // UNF Version
  private static final String unfVersionNumber = "5";
  
  // DataTable
  private DataTable mDataTable = new DataTable();
  
  // Specify which are decimal values
  Set <Integer> mDecimalVariableSet = new HashSet <Integer>(); 
  Set <Integer> booleanVariableSet = new HashSet <Integer>();
  
  private Map <String, String> mPrintFormatTable = new LinkedHashMap<String, String>(); 
  private Map <String, String> mPrintFormatNameTable = new LinkedHashMap<String, String>(); 
  private Map <String, String> mFormatCategoryTable = new LinkedHashMap<String, String>();
  private List <Integer> mPrintFormatList = new ArrayList<Integer>();
  
  Map<String, List<String>> missingValueTable = new LinkedHashMap<String, List<String>>();
  /*
   * Object Variables
   */
  
  // Number of observations
  private int mCaseQuantity = 0;
  
  // Number of variables
  private int mVarQuantity = 0;
  
  // Array specifying column-wise data-types
  private String [] mDataTypes;
  
  
  
  // Process ID, used partially in the generation of temporary directories
  private String mPID;
  
  // Object containing all the informatin for an R-workspace (including
  // temporary directories on and off server)
  private RWorkspace mRWorkspace;

  // Names of variables
  private List <String> variableNameList = new ArrayList <String> ();

  // The meta-data object
  SDIOMetadata smd = new RDATAMetadata();
  
  private Map <Integer, VariableMetaData> mVariableMetaDataTable;
  
  // Entries from TAB Data File
  DataTable mCsvDataTable = null;
  SDIOData sdiodata = null;

  // Number formatter
  NumberFormat doubleNumberFormatter = new DecimalFormat();

  // Builds R Requests for an R-server
  private RRequestBuilder mRequestBuilder;
  /*
   * Initialize Static Variables
   * This is primarily to construct the R-Script
   */
  static {
    /*
     * Set defaults fallbacks for class properties
     */
    if (RSERVE_HOST == null)
      RSERVE_HOST = "vdc-build.hmdc.harvard.edu";

    if (RSERVE_USER == null)
      RSERVE_USER = "rserve";

    if (RSERVE_PASSWORD == null)
      RSERVE_PASSWORD = "rserve";

    if (System.getProperty("vdc.dsb.rserve.port") == null)
      RSERVE_PORT = 6311;
    else
      RSERVE_PORT = Integer.parseInt(System.getProperty("vdc.dsb.rserve.port"));

    /*
     * Create Rscript to compute UNF
     */
    
    // Load R Scripts into memory, so that we can run them via R-serve
    RSCRIPT_WRITE_DVN_TABLE = readLocalResource("scripts/write.dvn.table.R");
    RSCRIPT_GET_DATASET = readLocalResource("scripts/get.dataset.R");
    RSCRIPT_CREATE_WORKSPACE = readLocalResource("scripts/create.workspace.R");
    RSCRIPT_GET_LABELS = readLocalResource("scripts/get.labels.R");
    RSCRIPT_DATASET_INFO_SCRIPT = readLocalResource("scripts/dataset.info.script.R");
    
    
    LOG.finer("R SCRIPTS AS STRINGS --------------");
    LOG.finer(RSCRIPT_WRITE_DVN_TABLE);
    LOG.finer(RSCRIPT_GET_DATASET);
    LOG.finer(RSCRIPT_CREATE_WORKSPACE);
    LOG.finer(RSCRIPT_GET_LABELS);
    LOG.finer(RSCRIPT_DATASET_INFO_SCRIPT);
    LOG.finer("END OF R SCRIPTS AS STRINGS -------");
   }
  /**
   * Helper Object to Handle Creation and Destruction of R Workspace
   */
  private class RWorkspace {
    public String mParent, mWeb, mDvn, mDsb;
    public File mDataFile, mCsvDataFile;
    public RRequest mRRequest;
    public BufferedInputStream mInStream;
    /**
     * 
     */
    public RWorkspace () {
      mParent = mWeb = mDvn = mDsb = "";
      mDataFile = null;
      mCsvDataFile = null;
      mInStream = null;
    }
    /**
     * Create the Actual R Workspace
     */
    public void create () {
      try {
        LOG.fine("RDATAFileReader: Creating R Workspace");
        
        REXP result = mRequestBuilder
                .script(RSCRIPT_CREATE_WORKSPACE)
                .build()
                .eval();
        
        RList directoryNames = result.asList();
        
        mParent = directoryNames.at("parent").asString();
        
        LOG.fine(String.format("RDATAFileReader: Parent directory of R Workspace is %s", mParent));
        
        LOG.fine("RDATAFileReader: Creating file handle");
        
        mDataFile = new File(mParent, "data.Rdata");
      }
      catch (Exception E) {
        LOG.warning("RDATAFileReader: Could not create R workspace");
        mParent = mWeb = mDvn = mDsb = "";
      }
    }
    /**
     * Destroy the Actual R Workspace
     */
    public void destroy () {
      String destroyerScript = new StringBuilder("")
              .append(String.format("unlink(\"%s\", TRUE, TRUE)", mParent))
              .toString();
      
      try {
        LOG.fine("RDATAFileReader: Destroying R Workspace");

        mRRequest = mRequestBuilder
                .script(destroyerScript)
                .build();
        
        mRRequest.eval();
        
        LOG.fine("RDATAFileReader: DESTROYED R Workspace");
      }
      catch (Exception ex) {
        LOG.warning("RDATAFileReader: R Workspace was not destroyed");
        LOG.fine(ex.getMessage());
      }
    }
    /**
     * Create the Data File to Use for Analysis, etc.
     */
    public File dataFile (String target, String prefix, int size) {
      
      String fileName = String.format("DVN.dataframe.%s.Rdata", mPID);
      
      mDataFile = new File(mParent, fileName);
                
      RFileInputStream RInStream = null;
      OutputStream outStream = null;
      
      RRequest req = mRequestBuilder.build();
      
      try {
        outStream = new BufferedOutputStream(new FileOutputStream(mDataFile));
        RInStream = req.getRConnection().openFile(target);
        
        if (size < 1024*1024*500) {
          int bufferSize = size;
          byte [] outputBuffer = new byte[bufferSize];
          RInStream.read(outputBuffer);
          outStream.write(outputBuffer, 0, size);
        }
        
        RInStream.close();
        outStream.close();
        return mDataFile;
      }
      catch (FileNotFoundException exc) {
        exc.printStackTrace();
        LOG.warning("RDATAFileReader: FileNotFound exception occurred");
        return mDataFile;
      }
      catch (IOException exc) {
        exc.printStackTrace();
        LOG.warning("RDATAFileReader: IO exception occurred");
      }

      // Close R input data stream
      if (RInStream != null) {
        try {
          RInStream.close();
        }
        catch (IOException exc) {
        }
      }

      // Close output data stream
      if (outStream != null) {
        try {
          outStream.close();
        }
        catch (IOException ex) {
        }
      }
      
      return mDataFile;
    }
    /**
     * Set the stream
     * @param inStream 
     */
    public void stream (BufferedInputStream inStream) {
      mInStream = inStream;
    }
    /**
     * Save the Rdata File Temporarily
     */
    private File saveRdataFile () {
      LOG.fine("RDATAFileReader: Saving Rdata File from Input Stream");
      
      if (mInStream == null) {
        LOG.fine("RDATAFileReader: No input stream was specified. Not writing file and returning NULL");
        return null;
      }
      
      byte [] buffer = new byte [1024];
      int bytesRead = 0;
      RFileOutputStream outStream = null;
      RConnection rServerConnection = null;
      
      try {
        LOG.fine("RDATAFileReader: Opening R connection");
        rServerConnection = new RConnection(RSERVE_HOST, RSERVE_PORT);
        
        LOG.fine("RDATAFileReader: Logging into R connection");
        rServerConnection.login(RSERVE_USER, RSERVE_PASSWORD);
        
        LOG.fine("RDATAFileReader: Attempting to create file");
        outStream = rServerConnection.createFile(mDataFile.getAbsolutePath());
        
        LOG.fine(String.format("RDATAFileReader: File created on server at %s", mDataFile.getAbsolutePath()));
      }
      catch (IOException ex) {
        LOG.warning("RDATAFileReader: Could not create file on R Server");
      }
      catch (RserveException ex) {
        LOG.warning("RDATAFileReader: Could not connect to R Server");
      }
      
      /*
       * Read stream and write to destination file
       */
      try {
        // Read from local file and write to rserver 1kb at a time
        while (mInStream.read(buffer) != -1) {
          outStream.write(buffer);
          bytesRead++;
        }
      }
      catch (IOException ex) {
        LOG.warning("RDATAFileReader: Could not write to file");
        LOG.fine(String.format("Error message: %s", ex.getMessage()));
      }
      catch (NullPointerException ex) {
        LOG.warning("RDATAFileReader: Data file has not been specified");
      }
      
      // Closing R server connection
      if (rServerConnection != null) {
        LOG.fine("RDATAFileReader: Closing R server connection");
        rServerConnection.close();
      }
      
      return mDataFile;
    }
    private File saveCsvFile () {
      // Specify CSV File Location on Server
      mCsvDataFile = new File(mRWorkspace.getRdataFile().getParent(), "data.csv");

      // 
      String csvScript = new StringBuilder("")
        .append("options(digits.secs=3)")
        .append("\n")
        .append(RSCRIPT_WRITE_DVN_TABLE)
        .append("\n")
        .append(String.format("load(\"%s\")", mRWorkspace.getRdataAbsolutePath()))
        .append("\n")
        .append(RSCRIPT_GET_DATASET)
        .append("\n")
        .append(String.format("write.dvn.table(data.set, file=\"%s\")", mCsvDataFile.getAbsolutePath()))
        .toString();
      
      // 
      RRequest csvRequest = mRequestBuilder.build();
      
      LOG.fine(String.format("RDATAFileReader: Attempting to write table to `%s`", mCsvDataFile.getAbsolutePath()));
      csvRequest.script(csvScript).eval();

      return mCsvDataFile;
    }
    /**
     * Return Rdata File Handle on R Server
     * @return File asdasd 
     */
    public File getRdataFile () {
      return mDataFile;
    }
    /**
     * Return Location of Rdata File on R Server
     * @return the file location as a string on the (potentially) remote R server
     */
    public String getRdataAbsolutePath () {
      return mDataFile.getAbsolutePath();
    }
  }
  /**
   * Constructs a <code>RDATAFileReader</code> instance from its "Spi" Class
   * @param originator a <code>StatDataFileReaderSpi</code> object.
   */
  public RDATAFileReader(StatDataFileReaderSpi originator) {

    super(originator);
    
    

    LOG.fine("RDATAFileReader: INSIDE RDATAFileReader");

    init();

    // Create request builder.
    // This object is used throughout as an RRequest factory
    mRequestBuilder = new RRequestBuilder()
            .host(RSERVE_HOST)
            .port(RSERVE_PORT)
            .user(RSERVE_USER)
            .password(RSERVE_PASSWORD);
    
    // Create R Workspace
    mRWorkspace = new RWorkspace();
    
    // 
    mCsvDataTable = new DataTable();
    
    // 
    mPID = RandomStringUtils.randomNumeric(6);
  }

  private void init(){
    doubleNumberFormatter.setGroupingUsed(false);
    doubleNumberFormatter.setMaximumFractionDigits(340);
  }
  /**
   * Read the Given RData File
   * @param stream a <code>BufferedInputStream</code>.
   * @param ignored
   * @return an <code>SDIOData</code> object
   * @throws java.io.IOException if a reading error occurs.
   */
  @Override
  public SDIOData read (BufferedInputStream stream, File dataFile) throws IOException {
    
    // Create Request object
    LOG.fine("RDATAFileReader: Creating RRequest object from RRequestBuilder object");
    
    // Create R Workspace
    mRWorkspace.stream(stream);
    mRWorkspace.create();
    mRWorkspace.saveRdataFile();
    mRWorkspace.saveCsvFile();
    
    // Copy CSV file to a local, temporary directory
    // Additionally, this sets the "tabDelimitedDataFile" property of the FileInformation
    File localCsvFile = copyBackCsvFile(mRWorkspace.mCsvDataFile);
    
    // Save basic information about data set
    putFileInformation();
    
    // This actually *sets* type lists more than it *gets* them
    getVariableTypeList(mDataTypes);
    
    // Read and parse the TAB-delimited file saved by R, above; do the 
    // necessary post-processinga and filtering, and save the resulting 
    // TAB file as tabFileDestination, below. This is the file we'll be 
    // using to calculate the UNF, and for the storage/preservation of the
    // dataset. 
    
    // IMPORTANT: this must be done *after* the variable metadata has been 
    // created!
    // - L.A. 
    
    RTabFileParser csvFileReader = new RTabFileParser('\t');
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(localCsvFile)));
    
    // int lineCount = csvFileReader.read(localBufferedReader, smd, null);
    File tabFileDestination = File.createTempFile("data-", ".tab");
    PrintWriter tabFileWriter = new PrintWriter(tabFileDestination.getAbsolutePath());
    int lineCount = csvFileReader.read(localBufferedReader, smd, tabFileWriter);
    
    smd.getFileInformation().put("tabDelimitedDataFileLocation", tabFileDestination.getAbsolutePath());
    
    
        
    // File Data Table:
    // Read the (processed) tab file one more time, load the entire data set
    // matrix in memory, for calculation of the UNF and summary stats: 
    // - L.A.
    mCsvDataTable = readTabDataFile(tabFileDestination);
    
    // Set meta information about format names, I guess.
    setMetaInfo();

    // Create UNF for each column
    createUNF(mCsvDataTable);
    
    setFormatData();
    
    //
    LOG.fine("RDATAFileReader: varQnty = " + mVarQuantity);
    LOG.fine("RDATAFileReader: Leaving \"read\" function");

    // Destroy R workspace
    mRWorkspace.destroy();
    
    // Return data properly stored
    return new SDIOData(smd, mCsvDataTable);
  }
  /**
   * Copy Remote File on R-server to a Local Target
   * @param target a target on the remote r-server
   * @return 
   */
  private File copyBackCsvFile (File target) {
    File destination;
    FileOutputStream csvDestinationStream;
    
    try {
      destination = File.createTempFile("data", ".csv");
      LOG.fine(String.format("RDATAFileReader: Writing local CSV File to `%s`", destination.getAbsolutePath()));
      csvDestinationStream = new FileOutputStream(destination);
    }
    catch (IOException ex) {
      LOG.warning("RDATAFileReader: Could not create temporary file!");
      return null;
    }
    
    try {
      // Open connection to R-serve
      RConnection rServeConnection = new RConnection(RSERVE_HOST, RSERVE_PORT);
      rServeConnection.login(RSERVE_USER, RSERVE_PASSWORD);
      
      // Open file for reading from R-serve
      RFileInputStream rServeInputStream = rServeConnection.openFile(target.getAbsolutePath());
      
      int b;
      
      LOG.fine("RDATAFileReader: Beginning to write to local destination file");
      
      // Read from stream one character at a time
      while ((b = rServeInputStream.read()) != -1) {
        // Write to the *local* destination file
        csvDestinationStream.write(b);
      }
      
      LOG.fine(String.format("RDATAFileReader: Finished writing from destination `%s`", target.getAbsolutePath()));
      LOG.fine(String.format("RDATAFileReader: Finished copying to source `%s`", destination.getAbsolutePath()));
      
      
      LOG.fine("RDATAFileReader: Closing CSVFileReader R Connection");
      rServeConnection.close();
    }
    /*
     * TO DO: Make this error catching more intelligent
     */
    catch (Exception ex) {
    }
    
    return destination;
  }
  /**
   * Put File Information into the Meta-data object
   * 
   * Runs an R-script that extracts meta-data from the *original* Rdata object, then 
   * 
   * @returnthe output writer
   * @throws IOException if something bad happens?
   */
  private void putFileInformation () {
    LOG.fine("RDATAFileReader: Entering `putFileInformation` function");
    
    // Store variable names
    String [] variableNames = { };
    
    String parentDirectory = mRWorkspace.getRdataFile().getParent();
    
    String fileInfoScript = new StringBuilder("")
            .append(String.format("load(\"%s\")\n", mRWorkspace.getRdataAbsolutePath()))
            .append(String.format("setwd(\"%s\")\n", parentDirectory))
            .append(RSCRIPT_GET_DATASET)
            .append("\n")
            .append(RSCRIPT_DATASET_INFO_SCRIPT)
            .toString();
    
    Map <String, String> variableLabels = new LinkedHashMap <String, String> ();
    
    try {
      RRequest request = mRequestBuilder.build();
      request.script(fileInfoScript);
      RList fileInformation = request.eval().asList();
      
      RList metaInfo = fileInformation.at("meta.info").asList();
      
      int varQnty = 0;
      variableNames = fileInformation.at("varNames").asStrings();
      
      mDataTypes = fileInformation.at("dataTypes").asStrings();
      
      // 
      for (String varName : variableNames) {
        variableLabels.put(varName, varName);
        variableNameList.add(varName);
        varQnty++;
      }
      
      // Get the Variable Meta Data Table while Populating 
      mVariableMetaDataTable = getVariableMetaDataTable(metaInfo);
      
      mCaseQuantity = fileInformation.at("caseQnty").asInteger();
      mVarQuantity = varQnty;

      smd.getFileInformation().put("varQnty", mVarQuantity);
      smd.getFileInformation().put("caseQnty", mCaseQuantity);
    }
    catch (REXPMismatchException ex) {
      mVariableMetaDataTable = null;
      LOG.warning("RDATAFileReader: Could not put information correctly");
    }
    catch (Exception ex) {
      mVariableMetaDataTable = null;
      ex.printStackTrace();
      LOG.warning(ex.getMessage());
    }
    
    // smd.getFileInformation().put("compressedData", true);
    smd.getFileInformation().put("charset", "UTF-8");
    smd.getFileInformation().put("mimeType", MIME_TYPE[0]);
    smd.getFileInformation().put("fileFormat", FORMAT_NAMES[0]);
    smd.getFileInformation().put("varFormat_schema", "RDATA");
    smd.getFileInformation().put("fileUNF", "");
    smd.getFileInformation().put("mVariableMetaDataTable", mVariableMetaDataTable);
    
    // Variable names
    smd.setVariableLabel(variableLabels);
    smd.setVariableName(variableNames);
  }
  /**
   * Read a Tabular Data File and create a "DataTable" Object
   * 
   * Returns a "DataTable" object that is a representation of the tabular data file. So sick.
   * 
   * @param tabFile a File object specifying the location of tabular data
   * @return a "DataTable" object representing the 
   * @throws IOException 
   */
  private DataTable readTabDataFile (File tabFile) throws IOException {
    DataTable tabData = new DataTable();
    Object[][] dataTable = null;
    
    dataTable = new Object[mVarQuantity][mCaseQuantity];

    //String tabFileName = (String) smd.getFileInformation().get("tabDelimitedDataFileLocation");
    String tabFileName = tabFile.getAbsolutePath();
            
    BufferedReader tabFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(tabFileName)));

    boolean[] isCharacterVariable = smd.isStringVariable();

    String[] valueTokens = new String[mVarQuantity];

    for (int j = 0; j < mCaseQuantity; j++) {
      String line = tabFileReader.readLine();
      
      if (line == null) {
        String msg = String.format("Failed to read %d lines from tabular data file \"%s\"", mCaseQuantity, tabFileName);
        throw new IOException(msg);
      }
      
      valueTokens = line.split("\t", mVarQuantity);
      
      for ( int i = 0; i < mVarQuantity; i++ ) {
 
        // If it's a character variable but not a date
        if (isCharacterVariable[i]) {
          
          if (valueTokens[i].length() == 0) {
            // If it is a missing value
            valueTokens[i] = null;
          } else {
            // Otherwise parse it out
            valueTokens[i] = valueTokens[i].replaceFirst("^\"", "");
            valueTokens[i] = valueTokens[i].replaceFirst("\"$", "");
            valueTokens[i] = valueTokens[i].replaceAll("\\\\\"", "\"");
          }
          
          dataTable[i][j] = valueTokens[i];
        }
        // Otherwise, we don't care
        // (don't care for now that is - we'll later look for the special 
        // tokens, like "NaN" and "Inf" and convert them to the appropriate
        // Double values.
        else {
          dataTable[i][j] = valueTokens[i];
        }
       
      }
    }

    // Close the file reader
    tabFileReader.close();
    
    // Convert dataTable to an actual "DataTable" object
    tabData.setData(dataTable);
    
    return tabData;
  }
  /**
   * Get Variable Type List
   * 
   * Categorize the columns of a data-set according to data-type. Returns a list
   * of integers corresponding to: (-1) String (0) Integer (1) Double-precision.
   * The numbers do not directly correspond with anything used by UNF5Util,
   * however this convention is seen throughout the DVN data-file readers.
   * 
   * This function essentially matches R data-types with those understood by
   * DVN:
   * * integer => "Integer"
   * * numeric (non-integer), double => "Double"
   * * Date => "Date"
   * * Other => "String"
   * 
   * @param dataTypes an array of strings where index corresponds to data-set
   * column and string corresponds to the class of the R-object.
   * @return 
   */
  private List<Integer> getVariableTypeList (String[] dataTypes) {
    /* 
     * TODO: 
     * 
     * Clean up this code; for example, the VariableMetaData variable "columnData"
     * is created below, but never saved or used. A vector of VariableMetaData 
     * values actually gets created somewhere else in the code of the reader, and those 
     * are the values that could be used elsewhere. Need to pick the one we want 
     * to use and remove the other one - for clarity. 
     * 
     * The whole setup with the "minimalTypeList" and "normalTypeList" is 
     * kinda confusing. One is used for the UNF and stats, the other one for 
     * metadata processing; which is ok. But then it is actually the "normal" 
     * one that is used for the "minimal" inside the SDIOMetadata object... 
     * Just renaming these to something that's more intuitive - types_for_UNF vs. 
     * types_for_METADATA - should be enough. 
     * 
     * --L.A.
     */
      
      
    //
    Map <String, HashMap <String, String>> valueLabelTable = new HashMap <String, HashMap <String, String>> ();
    
    //
    mFormatTable = new int [mVarQuantity];
    // Okay.
    List <Integer>
            minimalTypeList = new ArrayList<Integer>(),
            normalTypeList = new ArrayList<Integer>();
    
    Set <Integer> decimalVariableSet = new HashSet <Integer>(); 
    
    int k = 0;
    
    for (String type : dataTypes) {
      VariableMetaData columnMetaData;

      // Log
      
      String variableName = variableNameList.get(k);
      
      // Convention is that integer is zero, right?
      if (type.equals("integer")) {
        minimalTypeList.add(0);
        normalTypeList.add(0);
        mFormatTable[k] = FORMAT_INTEGER;
        mPrintFormatList.add(1);
        // mPrintFormatNameTable.put(variableName, "N");
        
        columnMetaData = new VariableMetaData(1);
      }

      // Double-precision data-types
      else if (type.equals("numeric") || type.equals("double")) {
        minimalTypeList.add(1);
        normalTypeList.add(0);
        decimalVariableSet.add(k);
        mFormatTable[k] = FORMAT_NUMERIC;
        mPrintFormatList.add(1);
        
        columnMetaData = new VariableMetaData(1);
      }
      
      // If date
      else if (type.equals("Date")) {
        minimalTypeList.add(-1);
        normalTypeList.add(1);
        
        mFormatTable[k] = FORMAT_DATE;
        
        mPrintFormatList.add(0);
        mPrintFormatNameTable.put(variableName, "DATE10");
        mFormatCategoryTable.put(variableName, "date");
        
        columnMetaData = new VariableMetaData(0);
        
        LOG.fine("date variable detected. format: "+FORMAT_DATE);
      }
      
      else if (type.equals("POSIXct") || type.equals("POSIXlt") || type.equals("POSIXt")) {
        minimalTypeList.add(-1);
        normalTypeList.add(1);
        
        mFormatTable[k] = FORMAT_DATETIME;
        
        mPrintFormatList.add(0);
        mPrintFormatNameTable.put(variableName, "DATETIME23.3");
        mFormatCategoryTable.put(variableName, "time");
        
        columnMetaData = new VariableMetaData(0);
        
        LOG.fine("POSIXt variable detected. format: "+FORMAT_DATETIME);
      }
      
      else if (type.equals("factor")) {
        /* 
         * This is the counter-intuitive part: in R, factors always have 
         * internal integer values and character labels. However, we will 
         * always treat them as character/string variables, i.e. on the DVN
         * side they will be ingested as string-type categorical variables 
         * (with both the "value" and the "label" being the same string - the 
         * R factor label). Yes, this means we are dropping the numeric value
         * completely. Why not do what we do in SPSS, i.e. use the numeric for 
         * the value (and the TAB file entry)? - well, this is in fact a very 
         * different case: in SPSS, a researcher creating a categorical variable 
         * with numeric values would be hand-picking these numeric variables; 
         * so we assume that the chosen values are in fact meaningful. If they 
         * had some sort of a reason to assign 0 = "Male" and 7 = "Female", we 
         * assume that they wanted to do this. So we use the numeric codes for 
         * storage in the TAB file and for calculation of the UNF. In R however, 
         * the user has no control over the internal numeric codes; they are 
         * always created automatically and are in fact considered meaningless. 
         * So we are going to assume that it is the actual values of the labels 
         * that are meaningful. 
         *  -- L.A. 
         * 
         */
        minimalTypeList.add(-1);
        normalTypeList.add(1);
        mFormatTable[k] = FORMAT_STRING;
        mPrintFormatList.add(0);
        mPrintFormatNameTable.put(variableName, "other");
        mFormatCategoryTable.put(variableName, "other");
        
        columnMetaData = new VariableMetaData(0);
      } else if (type.equals("logical")) {
        minimalTypeList.add(0);
        normalTypeList.add(0);
        mFormatTable[k] = FORMAT_INTEGER;
        mPrintFormatList.add(1);
        // mPrintFormatNameTable.put(variableName, "N");
        
        columnMetaData = new VariableMetaData(1);
        columnMetaData.setBoolean(true);
      // Everything else is a string
      } else {
        minimalTypeList.add(-1);
        normalTypeList.add(1);
        mFormatTable[k] = FORMAT_STRING;
        mPrintFormatList.add(0);
        mPrintFormatNameTable.put(variableName, "other");
        mFormatCategoryTable.put(variableName, "other");
        
        columnMetaData = new VariableMetaData(0);
      }
      
      k++;
    }
    
    // Decimal Variables
    
    smd.setVariableTypeMinimal(ArrayUtils.toPrimitive(normalTypeList.toArray(new Integer[normalTypeList.size()])));
    smd.setDecimalVariables(decimalVariableSet);
    smd.setVariableStorageType(null);

    smd.setVariableFormat(mPrintFormatList);
    smd.setVariableFormatName(mPrintFormatNameTable);
    smd.setVariableFormatCategory(mFormatCategoryTable);
    // smd.set
    
    LOG.fine("minimalTypeList =    " + Arrays.deepToString(minimalTypeList.toArray()));
    LOG.fine("normalTypeList =     " + Arrays.deepToString(normalTypeList.toArray()));
    LOG.fine("decimalVariableSet = " + Arrays.deepToString(decimalVariableSet.toArray()));
    
    LOG.fine("mPrintFormatList =      " + mPrintFormatList);
    LOG.fine("mPrintFormatNameTable = " + mPrintFormatNameTable);
    LOG.fine("mFormatCategoryTable =  " + mFormatCategoryTable);
    
    LOG.fine("mFormatTable = " + mFormatTable);

    // Return the variable type list
    return minimalTypeList;
  }
 /**
   * Create UNF from Tabular File
   * This methods iterates through each column of the supplied data table and
   * invoked the 
   * @param DataTable table a rectangular data table
   * @return void
   */
  private void createUNF (DataTable table) throws IOException {
    List<Integer> variableTypeList = getVariableTypeList(mDataTypes);
    String[] dateFormats = new String[mCaseQuantity];
    String[] unfValues = new String[mVarQuantity];
    String fileUNFvalue = null;
    
    // Set variable types
    // smd.setVariableTypeMinimal(ArrayUtils.toPrimitive(variableTypeList.toArray(new Integer[variableTypeList.size()])));
    
    int [] x = ArrayUtils.toPrimitive(variableTypeList.toArray(new Integer[variableTypeList.size()]));
    
    for (int k = 0; k < mVarQuantity; k++) {
      String unfValue, name = variableNameList.get(k);
      int varType = variableTypeList.get(k);
      
      Object [] varData = table.getData()[k];
      
      LOG.fine(String.format("RDATAFileReader: Column \"%s\" = %s", name, Arrays.deepToString(varData)));
      
      try {
        switch (varType) {
          case 0:
            Long[] integerEntries = new Long[varData.length];

            if (smd.isBooleanVariable()[k]) {
            // This is not a regular integer - but a boolean!
                Boolean[] booleanEntries = new Boolean[varData.length];
                for (int i = 0; i < varData.length; i++) {
                    if (varData[i] == null || varData[i].equals("")) {
                        // Missing Value: 
                        booleanEntries[i] = null; 
                    } else if (((String)varData[i]).equals("0")) {
                        booleanEntries[i] = false; 
                    } else if (((String)varData[i]).equals("1")) {
                        booleanEntries[i] = true; 
                    } else {
                        // Treat it as a missing value? 
                        booleanEntries[i] = null;
                        // TODO: 
                        // Should we throw an exception here instead? 
                    }
                    
                    // We'll also need the integer values, to calculate
                    // the summary statistics: 
                    try {
                        integerEntries[i] = new Long((String) varData[i]);
                    } catch (Exception ex) {
                        integerEntries[i] = null;
                    }
                }

                unfValue = UNF5Util.calculateUNF(booleanEntries);
                // TODO: 
                // we've never calculated UNFs for Booleans before - 
                // need to QA and verify that the values produced are correct.
                // -- L.A.
            
            } else {
            // Regular integer;
            // Treat it as an array of Longs:

                for (int i = 0; i < varData.length; i++) {
                    try {
                        integerEntries[i] = new Long((String) varData[i]);
                    } catch (Exception ex) {
                        integerEntries[i] = null;
                    }
                }

                unfValue = UNF5Util.calculateUNF(integerEntries);
            
            // UNF5Util.cal
            }

            // Summary/category statistics
            smd.getSummaryStatisticsTable().put(k, ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(integerEntries)));
            Map <String, Integer> catStat = StatHelper.calculateCategoryStatistics(integerEntries);
            smd.getCategoryStatisticsTable().put(variableNameList.get(k), catStat);
            smd.getNullValueCounts().put(variableNameList.get(k), StatHelper.countNullValues(integerEntries));


            break;

          // If double
          case 1:
            LOG.fine(k + ": " + name + " is numeric (double)");
            // Convert array of Strings to array of Doubles
            Double[]  doubleEntries = new Double[varData.length];
            
              for (int i = 0; i < varData.length; i++) {
                  try {
                      // Check for the special case of "NaN" - this is the R and DVN
                      // notation for the "Not A Number" value:
                      if (varData[i] != null && ((String) varData[i]).equals("NaN")) {
                          doubleEntries[i] = Double.NaN;
                      // "Inf" is another special case, notation for infinity, 
                      // positive and negative:
                      } else if (varData[i] != null && (
                                 ((String) varData[i]).equals("Inf") 
                              || ((String) varData[i]).equals("+Inf") )) {
                          
                          doubleEntries[i] = Double.POSITIVE_INFINITY;
                      } else if (varData[i] != null && ((String) varData[i]).equals("-Inf")) {
                          
                          doubleEntries[i] = Double.NEGATIVE_INFINITY;
                      } else {
                          // Missing Values don't need to be treated separately; these 
                          // are represented as empty strings in the TAB file; so 
                          // attempting to create a Double object from one will 
                          // throw an exception - which we are going to intercept 
                          // below. For the UNF and Summary Stats purposes, missing
                          // values are represented as NULLs. 
                          doubleEntries[i] = new Double((String) varData[i]);
                      }
                  } catch (Exception ex) {
                      LOG.fine(k + ": " + name + " dropping value " + (String)varData[i] + " (" + i + "); replacing with null");
                      doubleEntries[i] = null;
                  }
              }
            
            LOG.fine("sumstat:long case=" + Arrays.deepToString(
                        ArrayUtils.toObject(StatHelper.calculateSummaryStatisticsContDistSample(doubleEntries))));
            
            // Save summary statistics:
            smd.getSummaryStatisticsTable().put(k, ArrayUtils.toObject(StatHelper.calculateSummaryStatisticsContDistSample(doubleEntries)));

            unfValue = UNF5Util.calculateUNF(doubleEntries);
            
            break;
       
          case -1:
            LOG.fine(k + ": " + name + " is string");

            String[] stringEntries = new String[varData.length];//Arrays.asList(varData).toArray(new String[varData.length]);
            
            LOG.fine("string array passed to calculateUNF: " + Arrays.deepToString(stringEntries));
            
            //
            if (mFormatTable[k] == FORMAT_DATE || mFormatTable[k] == FORMAT_DATETIME) {
              DateFormatter dateFormatter = new DateFormatter();
              
              dateFormatter.setDateFormats(DATE_FORMATS);
              dateFormatter.setTimeFormats(TIME_FORMATS);
              
              for (int i = 0; i < varData.length; i++) {
                DateWithFormatter entryDateWithFormat;
                
                // If data is missing, treat this entry as just that - 
                // a missing value. Just like for all the other data types, 
                // this is represented by a null:
                if (dateFormats[i] != null && (varData[i].equals("") || varData[i].equals(" "))) {
                  stringEntries[i] = dateFormats[i] = null;
                }
                else {
                  entryDateWithFormat = dateFormatter.getDateWithFormat((String)varData[i]);
                  if (entryDateWithFormat == null) {
                      LOG.fine("ATTENTION: the supplied date/time string could not be parsed (" +
                              (String)varData[i]); 
                      throw new IOException("Could not parse supplied date/time string: "+(String)varData[i]);
                  }
                  // Otherwise get the pattern
                  // entryDateWithFormat = dateFormatter.getDateWithFormat(stringEntries[i]);
                  stringEntries[i] = (String)varData[i];
                  dateFormats[i] = entryDateWithFormat.getFormatter().toPattern();
                
                }
              } 
              
              // Compute UNF
              try {
                LOG.fine("RDATAFileReader: strdata = " + Arrays.deepToString(stringEntries));
                LOG.fine("RDATAFileReader: dateFormats = " + Arrays.deepToString(dateFormats));
                
                unfValue = UNF5Util.calculateUNF(stringEntries, dateFormats);
              }
              catch (Exception ex) {
                LOG.warning("RDATAFileReader: UNF for variable "+name+" could not be computed!");
                //unfValue = UNF5Util.calculateUNF(stringEntries);
                //ex.printStackTrace();
                throw ex; 
              }
            } else {
                for (int i = 0; i < varData.length; i++) {
                    if (varData[i] == null) {
                        // Missing Value
                        stringEntries[i] = null;
                    } else {
                        stringEntries[i] = (String)varData[i];
                    }
                }       
                
                unfValue = UNF5Util.calculateUNF(stringEntries);
            }

            smd.getSummaryStatisticsTable().put(k, StatHelper.calculateSummaryStatistics(stringEntries));
            Map <String, Integer> StrCatStat = StatHelper.calculateCategoryStatistics(stringEntries);
            smd.getCategoryStatisticsTable().put(variableNameList.get(k), StrCatStat);
            smd.getNullValueCounts().put(variableNameList.get(k), StatHelper.countNullValues(stringEntries));

            break;
            
          default:
            unfValue = null;
                
        }
        
        //LOG.fine(String.format("RDATAFileReader: Column \"%s\" (UNF) = %s", name, unfValue));

        // Store UNF value
        unfValues[k] = unfValue;
      } catch (Exception ex) { 
          LOG.fine("Exception caught while calculating UNF! " + ex.getMessage());
          ex.printStackTrace();
          throw new IOException ("Exception caught while calculating UNF! "+ex.getMessage());
      }
      LOG.fine(String.format("RDATAFileReader: Column \"%s\" (UNF) = %s", name, unfValues[k]));

    }
    
    try {
      fileUNFvalue = UNF5Util.calculateUNF(unfValues);
    } catch (Exception ex) {
      ex.printStackTrace();
      LOG.fine("Exception caught while calculating the combined UNF for the data set! " + ex.getMessage());
      throw new IOException ("Exception caught while calculating the combined UNF for the data set! "+ex.getMessage());
    } 
    mCsvDataTable.setUnf(unfValues);
    mCsvDataTable.setFileUnf(fileUNFvalue);

    // Set meta-data to make it look like a SAV file
    // smd.setVariableStorageType(null);
    // smd.setDecimalVariables(mDecimalVariableSet);
    
    boolean [] b = smd.isContinuousVariable();
    
    for (int k = 0; k < b.length; k++) {
      String s = b[k] ? "True" : "False";
      LOG.fine(k + " = " + s);
    }
    
    smd.setVariableUNF(unfValues);
    smd.getFileInformation().put("fileUNF", fileUNFvalue);
  }
  /**
   * Set meta information
   * 
   */
  private void setMetaInfo () {
  }
  /**
   * Set Missing Values from CSV File
   * Sets missing value table from CSV file.
   */
  private void setMissingValueTable () {
    smd.setMissingValueTable(null);
    // smd.getFileInformation().put("caseWeightVariableName", caseWeightVariableName);
  }
  /**
   * Read a Local Resource and Return Its Contents as a String
   * <code>readLocalResource</code> searches the local path around the class
   * <code>RDATAFileReader</code> for a file and returns its contents as a
   * string.
   * @param path String specifying the name of the local file to be converted
   * into a UTF-8 string.
   * @return a UTF-8 <code>String</code>
   */
  private static String readLocalResource (String path) {
    // Debug
    LOG.fine(String.format("RDATAFileReader: readLocalResource: reading local path \"%s\"", path));
    
    // Get stream
    InputStream resourceStream = RDATAFileReader.class.getResourceAsStream(path);
    String resourceAsString = "";
    
    // Try opening a buffered reader stream
    try {
      resourceAsString = FileUtils.readAll(resourceStream, "UTF-8");
      resourceStream.close();
    }
    catch (IOException ex) {
      LOG.warning(String.format("RDATAFileReader: (readLocalResource) resource stream from path \"%s\" was invalid", path));
    }
    
    // Return string
    return resourceAsString;
  }
  
  private void setFormatData () {

  }
  
  /**
   * Get a HashMap matching column number to meta-data used in re-creating R Objects
   * @param metaInfo an "RList" Object containing indices - type, type.string,
   * class, levels, and format.
   * @return a HashMap mapping column index to associated metadata
   */
  private HashMap <Integer, VariableMetaData> getVariableMetaDataTable (RList metaInfo) throws IOException {
    // list(type = 1, type.string = "integer", class = class(values), levels = NULL, format = NULL)
    Integer variableType = -1;
    String variableTypeString = "", variableFormat = "";
    String [] variableClass = null, variableLevels = null;
        
    // The result objet that pairs column numbers with VariableMetaData objects
    HashMap <Integer, VariableMetaData> result = new HashMap <Integer, VariableMetaData> ();
    
    // While we are here, we should also fill the valueLabelTable for the meta-data object
    Map <String, Map <String, String>> valueLabelTable = new LinkedHashMap <String, Map <String, String>> ();
    Map <String, String> valueLabelMappingTable = new LinkedHashMap <String, String> ();
    
    // smd.setValueLabelTable(valueLabelTable);
    
    for (int k = 0; k < metaInfo.size(); k++) {
      
      try {
        // Map for factors
        Map <String, String> factorLabelMap = new HashMap <String, String> ();
        
        // Meta-data for a column in the data-set
        RList columnMeta = metaInfo.at(k).asList();
        
        // Extract information from the returned list
        variableType = !columnMeta.at("type").isNull() ? columnMeta.at("type").asInteger() : null;
        variableTypeString = !columnMeta.at("type.string").isNull() ? columnMeta.at("type.string").asString() : null;
        variableClass = !columnMeta.at("class").isNull() ? columnMeta.at("class").asStrings() : null;
        variableLevels = !columnMeta.at("levels").isNull() ? columnMeta.at("levels").asStrings() : new String [0];
        variableFormat = !columnMeta.at("format").isNull() ? columnMeta.at("format").asString() : null;
        
        LOG.fine("variable type: "+variableType);
        LOG.fine("variable type string: "+variableTypeString);
        for (int i = 0; i < variableClass.length; i++) {
            LOG.fine("variable class: "+variableClass[i]);
        }
        LOG.fine("variable format: "+variableFormat);
        
        for (int i = 0; i < variableLevels.length; i++) {
            LOG.fine("variable level: "+variableLevels[i]);
        }
        
        // Create a variable meta-data object
        VariableMetaData columnMetaData = new VariableMetaData(variableType);
        columnMetaData.setDateTimeFormat(variableFormat);
        
        if (variableLevels != null && variableLevels.length > 0) {
            // this is a factor.
            columnMetaData.setFactor(true);
            columnMetaData.setFactorLevels(variableLevels);


            // Create a map between a label to itself. This should include values
            // that are missing from the dataset but present in the levels of the
            // factor.

            // So, this "mapping of label to itself" just means that the same 
            // string will be used for both the "value" and the "label"; and the
            // variable will be treated on the DVN side as a categorical variable
            // of type "string". See my comment somewhere in the code above, "this 
            // is the counter-intuitive part..." that explains why we are treating 
            // R factors this way. 

            for (String label : variableLevels) {
                factorLabelMap.put(label, label);
            }
        }
        // A special case for logical variables: 
        // For all practical purposes, they are handled as numeric factors
        // with 0 and 1 for the values and "FALSE" and "TRUE" for the labels.
        // (so this can also be used as an example of ingesting a *numeric* 
        // categorical variable - as opposed to *string* categoricals, that
        // we turn R factors into - above.
        else if ("logical".equals(variableTypeString)) {
            columnMetaData.setFactor(true);
            booleanVariableSet.add(k);
            
            String booleanFactorLabels[] = new String [2]; 
            booleanFactorLabels[0] = "FALSE";
            booleanFactorLabels[1] = "TRUE";
            
            columnMetaData.setFactorLevels(booleanFactorLabels); 
            
            int booleanFactorValues[] = new int[2];
            booleanFactorValues[0] = 0;
            booleanFactorValues[1] = 1; 
            
            columnMetaData.setIntFactorValues(booleanFactorValues);
            
            factorLabelMap.put("0", "FALSE");
            factorLabelMap.put("1", "TRUE");
        }
        
        
        
        // Value label table matches column-name to list of possible categories
        valueLabelTable.put(variableNameList.get(k), factorLabelMap);
        
        // Value label mapping table specifies which variables produce categorical-type data
        
        // (well, to determine "which variables produce categorical-type data" 
        // we could simply if a variable has a non-empty entry i the valueLabelTable. 
        // This is how this information is looked up elsewhere in the ingest 
        // framework: Akio first looks up the key in the valueLabelMappingTable, 
        // by the variable name, then uses this key to check the valueLabelTable map...
        // Why this double mapping? - it really doesn't seem to serve any function...
        // i.e. - why not look up just by the variable name? - this is essentially
        // what Matt is doing here too)
        // so, TODO: figure out why this is necessary!  -- L.A. 
        // (I would assume there could be a legit case with some formats where 
        // you could have more than one variable with the same name... but this 
        // isn't solving this problem - since we are still using the variable 
        // name in the lvalueLabelMappingTable!)
        
        valueLabelMappingTable.put(variableNameList.get(k), variableNameList.get(k));
        
        // Store the meta-data in a hashmap (to return later)
        result.put(k, columnMetaData);
      }
      catch (REXPMismatchException ex) {
        // If something went wrong, then it wasn't meant to be for that column.
        // And you know what? That's okay.
        ex.printStackTrace();
        LOG.fine(String.format("Column %d of Data Set could not create a VariableMetaData object", k));
      }
    }
    
    
    smd.setValueLabelTable(valueLabelTable);
    smd.setValueLabelMappingTable(valueLabelMappingTable);
    smd.setBooleanVariables(booleanVariableSet);
    
    // Return the array or null
    return result;
  }
}
