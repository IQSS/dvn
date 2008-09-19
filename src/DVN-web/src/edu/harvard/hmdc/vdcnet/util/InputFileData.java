/* MPL License text (see http://www.mozilla.org/MPL/) */

package edu.harvard.hmdc.vdcnet.util;

import com.icesoft.faces.component.inputfile.FileInfo;
import java.io.File;
import java.io.Serializable;
import edu.harvard.hmdc.vdcnet.web.study.AddFilesPage;
/**
 * <p>The InputFileData Class is a simple wrapper/storage for object that are
 * returned by the inputFile component.  The FileInfo Class contains file
 * attributes that are associated during the file upload process.  The File
 * Object is a standard java.io File object which contains the uploaded
 * file data. </p>
 *
 * @since 1.0
 */
public class InputFileData implements Serializable {

    // file info attributes
    private transient FileInfo fileInfo;
    // file that was uplaoded
    private File file= null;
    //
    private String fileName=null;
    private String fileCategoryName = "";  
    private String fileDescription =""; 
    private String sizeFormatted;
 
    /**
     * Create a new InputFileDat object.
     *
     * @param fileInfo fileInfo object created by the inputFile component for
     *                 a given File object.
     * @param file     corresponding File object which as properties described
     *                 by the fileInfo object.
     */
    public InputFileData( ){}
    public InputFileData(FileInfo fileInfo, File file) {
        this.fileInfo = fileInfo;
        this.file = file;
        fileName = fileInfo.getFileName();
    }
   
    /**
     * 
     * @param fileInfo fileInfo object created by the inputFile component for
     *                 a given File object.
     * @param file  corresponding File object which as properties described
     *                 by the fileInfo object.
     * @param unf String with the unf of the file
     */

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
   
    
    
    public String getFileName(){
       return fileName; 
    }
  public void setFileName(String f){
     this.getFileInfo().setFileName(f);
      fileName =f;  
  }
    public String getFileCategoryName(){
	return fileCategoryName;
    }
    public void setFileCategoryName(String fnm){
	fileCategoryName =fnm;
    }
    public String getFileDescription(){
	return fileDescription;
    }
    public void setFileDescription(String str){
	fileDescription = str; 
    }
   
   
    /**
     * Method to return the file size as a formatted string
     * For example, 4000 bytes would be returned as 4kb
     *
     *@return formatted file size
     */
    public String getSizeFormatted() {
        long ourLength = file.length();
        
        // Generate formatted label, such as 4kb, instead of just a plain number
        if (ourLength >= AddFilesPage.MEGABYTE_LENGTH_BYTES) {
            return ourLength / AddFilesPage.MEGABYTE_LENGTH_BYTES + "mb";
        }
        else if (ourLength >= AddFilesPage.KILOBYTE_LENGTH_BYTES) {
            return ourLength / AddFilesPage.KILOBYTE_LENGTH_BYTES + "kb";
        }
        else if (ourLength == 0) {
            return "0";
        }
        else if (ourLength < AddFilesPage.KILOBYTE_LENGTH_BYTES) {
            return ourLength + "b";
        }
        sizeFormatted =  Long.toString(ourLength); 
        return sizeFormatted; 
    }
  private void setSizeFormatted(String s){
      sizeFormatted = s; 
  }
  
}
