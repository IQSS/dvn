/*
 * FileUtil.java
 *
 * Created on February 12, 2007, 5:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import javax.ejb.EJBException;

/**
 *
 * @author Ellen Kraffmiller
 */
public class FileUtil {
    
    /** Creates a new instance of FileUtil */
    public FileUtil() {
    }
    
      public static void copyFile(File inputFile, File outputFile) throws IOException {
        FileChannel in = null;
        WritableByteChannel out = null;
        
        try {
            in = new FileInputStream(inputFile).getChannel();
            out = new FileOutputStream(outputFile).getChannel();
            int bytesPerIteration = 50000;
            int start = 0;
            while ( start < in.size() ) {
                in.transferTo(start, bytesPerIteration, out);
                start += bytesPerIteration;
            }
            
        } finally {
            if (in != null) { in.close(); }
            if (out != null) { out.close(); }
        }
    }
      
    public static String getStudyFileDir() {
        String studyFileDir = System.getProperty("vdc.study.file.dir");
        if (studyFileDir != null) {
            return studyFileDir;
        } else {
            throw new EJBException("System property \"vdc.study.file.dir\" has not been set.");
        }
    }     
    
       public static String getLegacyFileDir() {
        String studyFileDir = System.getProperty("vdc.legacy.file.dir");
        if (studyFileDir != null) {
            return studyFileDir;
        } else {
            throw new EJBException("System property \"vdc.study.file.dir\" has not been set.");
        }
    }     
       
       public static String getImportFileDir() {
        String importFileDir = System.getProperty("vdc.import.log.dir");
        if (importFileDir != null) {
            File importLogDir = new File(importFileDir);
            if (!importLogDir.exists()) {
                importLogDir.mkdir();
            }            
            return importFileDir;
        } else {
            throw new EJBException("System property \"vdc.import.log.dir\" has not been set.");
        }
    }            
}
