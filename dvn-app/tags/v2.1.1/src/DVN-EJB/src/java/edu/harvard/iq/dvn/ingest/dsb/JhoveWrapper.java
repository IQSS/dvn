package edu.harvard.iq.dvn.ingest.dsb;


import edu.harvard.hul.ois.jhove.*;
import java.io.*;
import java.util.*;
import static java.lang.System.*;

/**
 * JhoveWrapper.java
 * 
 * @author Akio Sone
 *
 */
public class JhoveWrapper implements java.io.Serializable  {


    /** 
     * constructor
     */
     
    public JhoveWrapper() {
        
    }
    
    /* jhove.conf
     * the original construct
     * the location of a conf file is given as an option,
     * e.g.,  -c jhove.conf 
     * 
     *  <jhoveHome>"C:\ahome\Desktop\jhove"</jhoveHome>
     *  <defaultEncoding>utf-8</defaultEncoding>
     *  <tempDirectory>C:\temp</tempDirectory>
     *   
     */
    
    public static String getJhoveConfigFile(){
        String dir=System.getProperty("jhove.conf.dir");
        return dir + File.separator + "jhove.conf";
    }
    
    private static final int[] ORIGINAL_RELEASE_DATE = { 2007, 8, 30 };
    private static final String ORIGINAL_COPR_RIGHTS = "Copyright"
        +"2004-2007 by the President and Fellows of Harvard College. "
        + "Released under the GNU Lesser General Public License.";
    
    /**
     * 
     * A method that returns Jhove's RepInfo
     */
    
    public RepInfo checkFileType(File file) {
        RepInfo info = null;
        boolean DEBUG = false;
        
        try {
            // initialize the application spec object
            // name, release number, build date, usage, Copyright infor
            App jhoveApp = new App("Jhove", "1.1 (pre-release g) dvn", 
                           ORIGINAL_RELEASE_DATE, "Java JhoveWrapper file_name", 
                           ORIGINAL_COPR_RIGHTS);

            String configFile = JhoveBase.getConfigFileFromProperties();
            String saxClass = JhoveBase.getSaxClassFromProperties();

            configFile = getJhoveConfigFile();
        
            // create an instance of jhove engine
            JhoveBase jb = new JhoveBase();
            // setup jhove engine
            // set the log level
            jb.setLogLevel("SEVERE"); // logLevel
            jb.init(configFile, saxClass);

            jb.setEncoding("utf-8"); // encoding
            jb.setTempDirectory(System.getProperty("user.dir")); // tempDir
            jb.setBufferSize(131072); // bufferSize

            jb.setChecksumFlag(false); // -s option
            jb.setShowRawFlag(false);  // -r option 
            jb.setSignatureFlag(true); // -k option
            
            // String moduleName = null;
            Module module = jb.getModule(null);
            
            // String aboutHandler = null;
            // OutputHandler about = jb.getHandler(null);
            
            // String handlerName = "xml"|text;
            // OutputHandler handler = jb.getHandler("text");

            // outputfile
            // String outputFile = null;
            
            // target file name
            //String[] dirFileOrUri = new String[1];
            //dirFileOrUri[0] = file.getAbsolutePath(); // file.getCanonicalPath();
            
            if (DEBUG){
                //out.println("file name"+dirFileOrUri[0]);
                out.println("file name="+file.getAbsolutePath());
            }
            
            // get a RepInfo instance
            if (file.exists() &&  file.isFile() && (file.length() > 0L)){
                info = jb.processRepInfo(jhoveApp, module, file);
                if (DEBUG) {
                    out.println("mime type="+info.getMimeType()); 
                }
            } else {
                err.println("The specified file does not exist or not a file or empty");
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }   catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
    
    /**
     * A convenience method that returns the value of the mime type tag only
     */
     
    public String getFileMimeType(File file) {
        String mimeType = null;
        boolean DEBUG = false;
        
        if (file.exists() &&  file.isFile() && (file.length() > 0L)){
            RepInfo info = checkFileType(file);
            mimeType = info.getMimeType();
            if (mimeType != null){
                if (DEBUG) {
                    out.println("returned mime-type:\n"+mimeType); 
                }
            }
        }
        
        return mimeType;
    }
}
