package edu.harvard.iq.dvn.core.web.dataaccess;

import java.io.File;
import java.io.InputStream; 
import java.io.FileInputStream;
import java.io.OutputStream; 
import java.io.FileOutputStream; 
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.Metadata;

import edu.harvard.iq.dvn.core.util.StringUtil;

/**
 *
 * @author leonidandreev
 */
public class PackageWithTermsOfUse {
    
    public static FileAccessObject repackage (StudyFile file, FileAccessObject fileDownload) {
        File tempZip = null; 
        
        // Retrieve Terms of Use, saved as a file: 
        
        FileAccessObject tou = ExportTermsOfUse.export(file.getStudy());
        
        InputStream inTOU = tou.getInputStream(); 
        
        if (inTOU == null) {
            return null; 
        }
                
        InputStream inFile = fileDownload.getInputStream();
        
        if (inFile == null) {
            return null; 
        }
        
        // Package the zip file: 

        try {
            tempZip = File.createTempFile("PkgWithTOU.", ".zip");
            OutputStream outFile = new FileOutputStream(tempZip);
            ZipOutputStream outZip = new ZipOutputStream(outFile);
            
            // Package the file:
            
            ZipEntry ze = new ZipEntry(file.getFileName());
            outZip.putNextEntry(ze);

            if (!fileDownload.noVarHeader() && fileDownload.getVarHeader() != null) {
                byte[] headerBuffer = fileDownload.getVarHeader().getBytes();
                outZip.write(headerBuffer);
            }

            byte[] dataBuffer = new byte[8192];

            int i = 0;
            while ((i = inFile.read(dataBuffer)) > 0) {
                outZip.write(dataBuffer, 0, i);
                outZip.flush();
            }
            inFile.close();
            outZip.closeEntry();
            
            // Package Terms of Use: 
            
            ze = new ZipEntry("TermsOfUse.txt");
            outZip.putNextEntry(ze);

            while ((i = inTOU.read(dataBuffer)) > 0) {
                outZip.write(dataBuffer, 0, i);
                outZip.flush();
            }
            inTOU.close();
            outZip.closeEntry();
            
            outZip.close();
            
            // Set the InputStream on the Access Object:
            
            fileDownload.setInputStream(new FileInputStream(tempZip ));
          

        } catch (Exception ex) {
            return null; 
        }
        
        // Adjust the File Download object metadata:
        
        // Cook new file name: 
        
        String packageFileName = fileDownload.getFileName();        
        packageFileName = packageFileName.replaceAll("\\.[a-zA-Z0-9]*$", "");
        packageFileName = packageFileName.concat("_pkg.zip");
        
        fileDownload.setIsLocalFile(true);
        fileDownload.setMimeType("application/zip");        
        fileDownload.setFileName(packageFileName);              
        fileDownload.setNoVarHeader(true);
        fileDownload.setVarHeader(null);
        
        return fileDownload; 
    }
    
}
