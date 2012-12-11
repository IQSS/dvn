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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.rdata;

/* DOCUMENTATION
 * 
 * 
 * 
 */

// The usual java stuff
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
import edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util.CSVFileReader;
import edu.harvard.iq.dvn.unf.*;
import edu.harvard.iq.dvn.rserve.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

/**
 * A DVN-Project-implementation of <code>StatDataFileReader</code> for the 
 * RData Binary Format.
 * 
 * @author Matthew Owen
 * @date 11-24-2012
 *
 * This implementation uses R-Scripts to do the bulk of the processing.
 * @note The code is primarily based on the SAV and CSV file readers.
 */
public class RDATAFileReader extends StatDataFileReader {
  
  /*
   * Class Variables
   */

  // R-ingest recognition files
  private static final String[] FORMAT_NAMES = { "RDATA", "Rdata", "rdata" };
  private static final String[] EXTENSIONS = { "Rdata", "rdata" };
  private static final String[] MIME_TYPE = { "application/x-rlang-transport" };
  
  // R Scripts
  static private String RSCRIPT_UNF = "";
  static private String RSCRIPT_META = "";
  static private String RSCRIPT_READ = "";
  static private String RSCRIPT_CREATE_WORKSPACE;
  static private String RSCRIPT_GET_LABELS = "";
  static private String RSCRIPT_DATASET_INFO_SCRIPT = "";
  static private String RSCRIPT_GET_DATASET = "";
  
  // RServe static variables
  private static String RSERVE_HOST = System.getProperty("vdc.dsb.host");
  private static String RSERVE_USER = System.getProperty("vdc.dsb.rserve.user");
  private static String RSERVE_PASSWORD = System.getProperty("vdc.dsb.rserve.pwrd");
  private static int RSERVE_PORT;
  
  // DSB
  public static int DSB_PORT;

  
  //</editor-fold>
  // Temporary directory
  
  public static String TEMP_DIR = System.getProperty("java.io.tmpdir");
  public static String DSB_TEMP_DIR = System.getProperty("vdc.dsb.temp.dir");
  public static String DVN_TEMP_DIR = null;
  public static String WEB_TEMP_DIR = null;

  // Date-time formats
  private SimpleDateFormat sdf_ymd    = new SimpleDateFormat("yyyy-MM-dd");
  private SimpleDateFormat sdf_ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private SimpleDateFormat sdf_dhms   = new SimpleDateFormat("DDD HH:mm:ss");
  private SimpleDateFormat sdf_hms    = new SimpleDateFormat("HH:mm:ss");

  // Logger
  private static Logger LOG = Logger.getLogger(RDATAFileReader.class.getPackage().getName());
  
  // Directories
  private File mTempDir, mTempWebDir, mTempDvnDir, mTempDsbDir;

  // UNF Version
  private static final String unfVersionNumber = "5";
  
  // DataTable
  private static DataTable mDataTable = new DataTable();
  private Object [][] mDataTable2;
  private String [] mMetaTable;
  private String [][] mDataFormats;
  /*
   * Object Variables
   */
  
  private int mCaseQuantity = 0;
  private int mVarQuantity = 0;
  private char mDelimiterChar;
  
  private String mPID;
  private RWorkspace mRWorkspace;


  private Map <String, String> commandStrings = new HashMap <String, String>();
  private Map <String, Integer> unfVariableTypes = new HashMap <String, Integer>();
  private List <String> variableNameList = new ArrayList <String> ();

  SDIOMetadata smd = new RDATAMetadata();
  
  DataTable csvData = null;
  SDIOData sdiodata = null;

  NumberFormat doubleNumberFormatter = new DecimalFormat();

  private RRequestBuilder mRequestBuilder;

  /*
   * Initialize Static Variables
   * This is primarily to construct the R-Script
   */
  static {
    
    /*
     * Set defaults fallbacks for class properties
     */
    
    // RSERVE
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

    // DSB
    if (System.getProperty("vdc.dsb.port") == null)
      DSB_PORT = 80;
    else
      DSB_PORT = Integer.parseInt(System.getProperty("vdc.dsb.port"));
    
    /*
     * Build the Rscripts to execute.
     * Potentially this can be improved by keeping the script file as a separate
     * entity in the source code.
     */
    StringBuilder scriptBuilder = new StringBuilder();

    /*
     * Create Rscript to compute UNF
     */
    
    // R Script to create temporary directories
    RSCRIPT_CREATE_WORKSPACE = new StringBuilder("")
            .append("directories <- list()\n")
            .append("directories$parent <- tempfile('DvnRWorkspace')\n")
            .append("directories$web <- file.path(directories$parent, 'web')\n")
            .append("directories$dvn <- file.path(directories$parent, 'dvn')\n")
            .append("directories$dsb <- file.path(directories$parent, 'dsb')\n")
            .append("created <- list()\n")
            .append("for (key in names(directories)) {\n")
            .append("  dir.name <- directories[[key]]\n")
            .append("  if (dir.create(dir.name))\n")
            .append("    created[[key]] <- dir.name\n")
            .append("}\n")
            .append("created")
            .toString();
    
    RSCRIPT_GET_DATASET =
      "available.data.frames <- ls()\n" +
      "available.data.frames <- Filter(function (y) is.data.frame(get(y)), available.data.frames)\n" +
      "data.set <- available.data.frames[[1]]\n" +
      "data.set <- get(data.set)\n";
    
    RSCRIPT_GET_LABELS =
      "available.data.frames <- ls()\n" +
      "available.data.frames <- Filter(function (y) is.data.frame(get(y)), available.data.frames)\n" +
      "data.set <- available.data.frames[[1]]\n" +
      "data.set <- get(data.set)\n" +
      "colnames(data.set)\n";
    
    RSCRIPT_DATASET_INFO_SCRIPT = 
      "list(varNames = colnames(data.set), caseQnty = nrow(data.set))";
   }
  /**
   * Constructs a <code>RDATAFileReader</code> instance from its "Spi" Class
   * @param originator a <code>StatDataFileReaderSpi</code> object.
   */
  public RDATAFileReader(StatDataFileReaderSpi originator) {

    super(originator);

    LOG.info("RDATAFileReader: INSIDE RDATAFileReader");

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
    LOG.info("RDATAFileReader: Creating RRequest object from RRequestBuilder object");
    
    // Create R Workspace
    mRWorkspace.stream(stream);
    mRWorkspace.create();
    mRWorkspace.saveRdataFile();
    mRWorkspace.saveCsvFile();
    
    // Copy CSV file to a local, temporary directory
    File localCsvFile = copyBackCsvFile(mRWorkspace.mCsvDataFile);
    
    CSVFileReader csvFileReader = new CSVFileReader('\t');
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(localCsvFile)));
    
    // int lineCount = csvFileReader.read(localBufferedReader, smd, null);
    File tabFiledestination = File.createTempFile("data-", ".tab");
    PrintWriter tabFileWriter = new PrintWriter(tabFiledestination.getAbsolutePath());

        
    // Get basic information about data set
    putFileInformation();
    
    // Use CSVFileReader
    int lineCount = csvFileReader.read(localBufferedReader, smd, tabFileWriter);

    // Create a data table in local memory
    // createDataTable();
    
    // Destroy R workspace
    mRWorkspace.destroy();
    
    return null;
  }
  /**
   * Copy Remote File on R-server to a Local Target
   * @param target
   * @return 
   */
  private File copyBackCsvFile (File target) {
    File destination;
    FileOutputStream csvDestinationStream;
    
    try {
      destination = File.createTempFile("data", ".csv");
      LOG.info(String.format("RDATAFileReader: Writing local CSV File to `%s`", destination.getAbsolutePath()));
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
      
      // Buffer char
      int b;
      
      LOG.info("RDATAFileReader: Beginning to write to local destination file");
      
      // Read from stream one character at a time
      while ((b = rServeInputStream.read()) != -1) {
        csvDestinationStream.write(b);
      }
      
      LOG.info(String.format("RDATAFileReader: Finished writing to destination at `%s`", target.getAbsolutePath()));
      
      LOG.info("RDATAFileReader: Closing CSVFileReader R Connection");
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
   * Create Data Table
   */
  private void createDataTable () {
    mDataTable2 = new Object[mVarQuantity][mCaseQuantity];
    mMetaTable = new String[mVarQuantity];
    
    
    REXP r;

    String fileInfoScript = new StringBuilder("")
        .append(String.format("load(\"%s\")\n", mRWorkspace.getRdataAbsolutePath()))
        .append(RSCRIPT_GET_DATASET)
        .append("\n")
        .append("data.set")
        .toString();
    
    try {
      RRequest request = mRequestBuilder.build();
      request.script(fileInfoScript);
      
      RList result = request.eval().asList();
      
      int colNumber = 0;
      for (String key : result.keys()) {
        REXP col = result.at(key);
                
        if (col.isInteger()) {
          int [] column = col.asIntegers();
          for (int k = 0; k < column.length; k++) {
            mDataTable2[colNumber][k] = Integer.valueOf(column[k]);
          }
        }
        
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * Set Information About the Data Set
   */
  private void setDataInformation () {
    String script = new StringBuilder("")
            .append(String.format("load(\"%s\")\n", mRWorkspace.getRdataAbsolutePath()))
            .append(RSCRIPT_GET_LABELS)
            .toString();
    
    RRequest req = mRequestBuilder.build();
    REXP result = req.script(script).eval();
    
    Map <String, String> variableLabels;
    
    try {
      for (String key : result.asStrings())
        LOG.info("1");
    }
    catch (REXPMismatchException ex) {
    }
    
    smd.setVariableLabel(commandStrings);
  }
  /**
   * Create an Output Writer
   * @returnthe output writer
   * @throws IOException if something bad happens?
   */
  private void putFileInformation () {
    String parentDirectory = mRWorkspace.getRdataFile().getParent();
    File tabFile = new File(parentDirectory, "data.tab");
    File unfFile = new File(parentDirectory, "data.unf"); 
    
    String fileInfoScript = new StringBuilder("")
            .append(String.format("load(\"%s\")\n", mRWorkspace.getRdataAbsolutePath()))
            .append(String.format("setwd(\"%s\")\n", parentDirectory))
            .append(RSCRIPT_GET_DATASET)
            .append(String.format("write.table(data.set, \"%s\", row.names=F, col.names=F, na=\"\", sep=\"\t\", eol=\"\r\n\")\n", tabFile.getAbsolutePath()))
            .append(RSCRIPT_DATASET_INFO_SCRIPT)
            .toString();
    
    try {
      RRequest request = mRequestBuilder.build();
      request.script(fileInfoScript);
      RList fileInformation = request.eval().asList();
      
      int varQnty = 0;
      String [] variableNames = fileInformation.at("varNames").asStrings();
      
      for (String varName : variableNames) {
        varQnty++;
      }
      
      smd.getFileInformation().put("varQnty", varQnty);
      smd.getFileInformation().put("caseQnty", fileInformation.at("caseQnty").asInteger());
      smd.getFileInformation().put("tabDelimitedDataFileLocation", tabFile.getAbsolutePath());
      
      mCaseQuantity = fileInformation.at("caseQnty").asInteger();
      mVarQuantity = varQnty;
    }
    catch (REXPMismatchException ex) {
      LOG.warning("RDATAFileReader: Could not put information correctly");
    }
    catch (Exception ex) {
      ex.printStackTrace();
      LOG.warning(ex.getMessage());
    }
    
    // smd.getFileInformation().put("compressedData", true);
    smd.getFileInformation().put("charset", "UTF-8");
    smd.getFileInformation().put("mimeType", MIME_TYPE[0]);
    smd.getFileInformation().put("fileFormat", FORMAT_NAMES[0]);
    smd.getFileInformation().put("varFormat_schema", "RDATA");
    smd.getFileInformation().put("fileUNF", "");
  }
  /**
   * Return the UNF for a particular column
   * @noet This should be moved to the script itself.
   * @param varData ...
   * @param dateFormats ...
   * @param variableType
   * @param Version Number
   * @param variablePosition index of the variable
   */
  private String getUNF(Object[] varData, String[] dateFormats, int variableType, String unfVersionNumber, int variablePosition)
          throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException
  {
    return "";
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
        LOG.info("RDATAFileReader: Creating R Workspace");
        
        REXP result = mRequestBuilder
                .script(RSCRIPT_CREATE_WORKSPACE)
                .build()
                .eval();
        
        RList directoryNames = result.asList();
        
        mParent = directoryNames.at("parent").asString();
        
        LOG.info(String.format("RDATAFileReader: Parent directory of R Workspace is %s", mParent));
        
        // mWeb = directoryNames.at("web").asString();
        // mDvn = directoryNames.at("dvn").asString();
        // mDsb = directoryNames.at("dsb").asString();
        
        LOG.info("RDATAFileReader: Creating file handle");
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
              .append(String.format("unlink(%s, TRUE, TRUE)", mParent))
              .toString();
      
      try {
        LOG.info("RDATAFileReader: Destroying R Workspace");

        mRRequest = mRequestBuilder
                .script(destroyerScript)
                .build();
        
        mRRequest.eval();
        
        LOG.info("RDATAFileReader: DESTROYED R Workspace");
      }
      catch (Exception ex) {
        LOG.warning("RDATAFileReader: R Workspace was not destroyed");
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
      LOG.info("RDATAFileReader: Saving Rdata File from Input Stream");
      
      if (mInStream == null) {
        LOG.info("RDATAFileReader: No input stream was specified. Not writing file and returning NULL");
        return null;
      }
      
      byte [] buffer = new byte [1024];
      int bytesRead = 0;
      RFileOutputStream outStream = null;
      RConnection rServerConnection = null;
      
      try {
        LOG.info("RDATAFileReader: Opening R connection");
        rServerConnection = new RConnection(RSERVE_HOST, RSERVE_PORT);
        
        LOG.info("RDATAFileReader: Logging into R connection");
        rServerConnection.login(RSERVE_USER, RSERVE_PASSWORD);
        
        LOG.info("RDATAFileReader: Attempting to create file");
        outStream = rServerConnection.createFile(mDataFile.getAbsolutePath());
        
        LOG.info(String.format("RDATAFileReader: File created on server at %s", mDataFile.getAbsolutePath()));
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
        LOG.info(String.format("Error message: %s", ex.getMessage()));
      }
      catch (NullPointerException ex) {
        LOG.warning("RDATAFileReader: Data file has not been specified");
      }
      
      // Closing R server connection
      if (rServerConnection != null) {
        LOG.info("RDATAFileReader: Closing R server connection");
        rServerConnection.close();
      }
      
      return mDataFile;
    }
    private File saveCsvFile () {
      mCsvDataFile = new File(mRWorkspace.getRdataFile().getParent(), "data.csv");
      
      String csvScript = new StringBuilder("")
        .append(String.format("load(\"%s\")\n", mRWorkspace.getRdataAbsolutePath()))
        .append(RSCRIPT_GET_DATASET)
        .append("\n")
        .append(String.format("write.table(data.set, file=\"%s\", na=\"\", sep=\",\", eol=\"\r\n\", quote=TRUE, row.names=FALSE, col.names=FALSE)", mCsvDataFile.getAbsolutePath()))
        .toString();
    
      RRequest csvRequest = mRequestBuilder.build();
      
      LOG.info(String.format("RDATAFileReader: Attempting to write table to `%s`", mCsvDataFile.getAbsolutePath()));
      csvRequest.script(csvScript).eval();

      return mCsvDataFile;
    }
    /**
     * Return Rdata File Handle on R Server
     * @return 
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
}