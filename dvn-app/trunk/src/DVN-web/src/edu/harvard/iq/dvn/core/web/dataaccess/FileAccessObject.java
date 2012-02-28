package edu.harvard.iq.dvn.core.web.dataaccess;

// java core imports:
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.Iterator; 


// DVN App imports:
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import edu.harvard.iq.dvn.core.study.DataVariable;

public class FileAccessObject extends DataAccessObject {

    public FileAccessObject () throws IOException {
        this(null);
    }

    public FileAccessObject(StudyFile file) throws IOException {
        this (file, null);
    }

    public FileAccessObject(StudyFile file, DataAccessRequest req) throws IOException {

        super(file, req);

        if (file != null && file.isRemote()) {
            //return null; 
            throw new IOException ("not a local file.");
        }


        this.setIsLocalFile(true);
        this.setIsDownloadSupported(true);
        this.setIsNIOSupported(true);
    }

    public boolean canAccess (String location) throws IOException{
        return true;
    }

    //public void open (String location) throws IOException{

    //}

    //private void open (StudyFile file, Object req) throws IOException {
    public void open () throws IOException {

        StudyFile file = this.getFile();
        DataAccessRequest req = this.getRequest(); 


        //if (req instanceof HttpServletRequest) {
        //    if (((HttpServletRequest)req).getParameter("noVarHeader") != null) {
        //        this.setNoVarHeader(true);
        //    }
        //}

        if (req.getParameter("noVarHeader") != null) {
            this.setNoVarHeader(true);
        }
        InputStream in = openLocalFileAsStream(file);

        if (in == null) {
            throw new IOException ("Failed to open local file "+file.getFileSystemLocation());
        }

        this.setInputStream(in);

        this.setSize(getLocalFileSize(file));

        this.setMimeType(file.getFileType());
        this.setFileName(file.getFileName());

        
        if (file.getFileType() != null &&
            file.getFileType().equals("text/tab-separated-values")  &&
            file.isSubsettable() && (!this.noVarHeader())) {

            List datavariables = ((TabularDataFile) file).getDataTable().getDataVariables();
            String varHeaderLine = generateVariableHeader(datavariables);
            this.setVarHeader(varHeaderLine);
        }

        // The HTTP headers for the final download (when an HTTP download
        // of the file is requested, as opposed to the object being read by
        // another part of the system). This is a TODO -- need to design this
        // carefully.
        
//        setDownloadContentHeaders (localDownload);


        this.setStatus(200);
    } // End of initiateLocalDownload;

    // Auxilary helper methods, filesystem access-specific:

    public long getLocalFileSize (StudyFile file) {
        long fileSize = -1;
        File testFile = null;

        try {
            testFile = new File(file.getFileSystemLocation());
            if (testFile != null) {
                fileSize = testFile.length();
            }
        } catch (Exception ex) {
            return -1;
        }

        return fileSize;
    }

    public InputStream openLocalFileAsStream (StudyFile file) {
        InputStream in;

        try {
            in = new FileInputStream(new File(file.getFileSystemLocation()));
        } catch (Exception ex) {
            // We don't particularly care what the reason why we have
            // failed to access the file was.
            // From the point of view of the download subsystem, it's a
            // binary operation -- it's either successfull or not.
            // If we can't access it for whatever reason, we are saying
            // it's 404 NOT FOUND in our HTTP response.
            return null;
        }

        return in;
    }
    
    private String generateVariableHeader(List dvs) {
        String varHeader = null;

        if (dvs != null) {
            Iterator iter = dvs.iterator();
            DataVariable dv;

            if (iter.hasNext()) {
                dv = (DataVariable) iter.next();
                varHeader = dv.getName();
            }

            while (iter.hasNext()) {
                dv = (DataVariable) iter.next();
                varHeader = varHeader + "\t" + dv.getName();
            }

            varHeader = varHeader + "\n";
        }

        return varHeader;
    }

}