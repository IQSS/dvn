/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * FileUtil.java
 *
 * Created on February 12, 2007, 5:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import edu.harvard.hmdc.vdcnet.dsb.JhoveWrapper;
import edu.harvard.hmdc.vdcnet.dsb.SubsettableFileChecker;
import edu.harvard.hmdc.vdcnet.study.Study;
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
    
    private static final String[] SUBSETTABLE_FORMAT_SET = {"POR", "SAV", "DTA"};

    /** Creates a new instance of FileUtil */
    public FileUtil() {
    }
    
      public static void copyFile(File inputFile, File outputFile) throws IOException {
        FileChannel in = null;
        WritableByteChannel out = null;
        
        try {
            in = new FileInputStream(inputFile).getChannel();
            out = new FileOutputStream(outputFile).getChannel();
            long bytesPerIteration = 50000;
            long start = 0;
            while ( start < in.size() ) {
                in.transferTo(start, bytesPerIteration, out);
                start += bytesPerIteration;
            }
            
        } finally {
            if (in != null) { in.close(); }
            if (out != null) { out.close(); }
        }
    }

    public static String analyzeFile(File f) throws IOException{
        String fileType = null;

        // step 1: check whether the file is subsettable
        SubsettableFileChecker sfchk = new SubsettableFileChecker(SUBSETTABLE_FORMAT_SET);

        fileType = sfchk.detectSubsettableFormat(f);
        if (fileType != null){
            System.out.println("FileUtil:Analyze:mimeType="+fileType);
        }
        // setp 2: check the mime type of this file
        /* to be implemented*/
        if (fileType == null){
            JhoveWrapper jw = new JhoveWrapper();
            fileType = jw.getFileMimeType(f);
            System.out.println("FileUtil:Analyze:mimeType by Jhove="+fileType);
        }
        return fileType;
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
            throw new EJBException("System property \"vdc.legacy.file.dir\" has not been set.");
        }
    }     
       
       public static String getImportFileDir() {
        String importFileDir = System.getProperty("vdc.import.log.dir");
        if (importFileDir != null) {
            File importLogDir = new File(importFileDir);
            if (!importLogDir.exists()) {
                importLogDir.mkdirs();
            }            
            return importFileDir;
        } else {
            throw new EJBException("System property \"vdc.import.log.dir\" has not been set.");
        }
    }    
 public static String getExportFileDir() {
        String exportFileDir = System.getProperty("vdc.export.log.dir");
        if (exportFileDir != null) {
            File exportLogDir = new File(exportFileDir);
            if (!exportLogDir.exists()) {
                exportLogDir.mkdirs();
            }            
            return exportFileDir;
        } else {
            throw new EJBException("System property \"vdc.export.log.dir\" has not been set.");
        }
    }    
    public static File getStudyFileDir(Study study) {
        
        File file = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
        if (!file.exists()) {
             file.mkdirs();
        }
        return file;
    }
}
