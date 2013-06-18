/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.rserve;

import edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.rdata.RDATAFileReader;
import java.io.*;
import java.util.logging.Logger;
import org.apache.commons.lang.RandomStringUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RFileInputStream;
import org.rosuda.REngine.Rserve.RFileOutputStream;
import org.rosuda.REngine.Rserve.RserveException;

/**
  * Helper Object to Handle Creation and Destruction of R Workspace
  */
public class RWorkspace {
  // Instantiate logger
  private static final Logger LOG = Logger.getLogger(RWorkspace.class.getPackage().getName());
  
  // 
  private String mParent, mWeb, mDvn, mDsb;
  private File mDataFile, mCsvDataFile;
  private RRequest mRRequest;
  private BufferedInputStream mInStream;
  
  private String mHost, mUser, mPassword;
  private int mPort;
  
  // Builds R Requests for an R-server
  private RRequestBuilder mRequestBuilder;
  
  // Eh
  // private String mPID = RandomStringUtils.randomNumeric(6);
  
  /**
    * 
    */
  public RWorkspace () {
    mParent = mWeb = mDvn = mDsb = "";
    mDataFile = null;
    mCsvDataFile = null;
    mInStream = null;
    
    mHost = mUser = mPassword = "";
    mPort = -1;
    mRequestBuilder = new RRequestBuilder()
            .host(mHost)
            .port(mPort)
            .user(mUser)
            .password(mPassword);
  }
  public RWorkspace (String host, int port, String user, String password) {
    mParent = mWeb = mDvn = mDsb = "";
    mDataFile = null;
    mCsvDataFile = null;
    mInStream = null;
    mHost = host;
    mPort = port;
    mUser = user;
    mPassword = password;
    mRequestBuilder = new RRequestBuilder()
        .host(mHost)
        .port(mPort)
        .user(mUser)
        .password(mPassword);
  }
  /**
    * Create the Actual R Workspace
    */
  public void create () {
    try {
      LOG.fine("RDATAFileReader: Creating R Workspace");

      REXP result = mRequestBuilder
              .script("REPLACE WITH: RSCRIPT_CREATE_WORKSPACE")
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

    String fileName = String.format("DVN.dataframe.%s.Rdata", RandomStringUtils.randomNumeric(6));

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
      rServerConnection = new RConnection(mHost, mPort);

      LOG.fine("RDATAFileReader: Logging into R connection");
      rServerConnection.login(mUser, mPassword);

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
    mCsvDataFile = new File(getRdataFile().getParent(), "data.csv");

    // 
    String csvScript = new StringBuilder("")
      .append("options(digits.secs=3)")
      .append("\n")
      .append("REPLACE WITH: SCRIPT_WRITE_DVN_TABLE")
      .append("\n")
      .append(String.format("load(\"%s\")", getRdataAbsolutePath()))
      .append("\n")
      .append("REPLACE WITH: SCRIPT_GET_DATASET")
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