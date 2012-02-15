package edu.harvard.iq.dvn.core.web.dataaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import edu.harvard.iq.dvn.core.study.StudyFile;
/**
 *
 * @author leonidandreev
 */
public class StoredOriginalFile {
    
    public StoredOriginalFile () {
        
    }
    
    public static FileAccessObject retrieve (StudyFile file, FileAccessObject fileDownload) {
        File inFile = new File(file.getFileSystemLocation());

        if (inFile != null) {
            File origFile = new File(inFile.getParent(), "_" + file.getFileSystemName());
            
            if (origFile != null && origFile.exists()) {
                
                fileDownload.closeInputStream();
                fileDownload.setSize(origFile.length());
                
                try {
                    fileDownload.setInputStream(new FileInputStream(origFile));
                } catch (IOException ex) {
                    return null; 
                }
                fileDownload.setIsLocalFile(true);

                String originalMimeType = file.getOriginalFileType();

                if (originalMimeType != null && !originalMimeType.equals("")) {
                    if (originalMimeType.matches("application/x-dvn-.*-zip")) {
                        fileDownload.setMimeType("application/zip");
                    }
                    fileDownload.setMimeType(originalMimeType);
                } else {
                    fileDownload.setMimeType("application/x-unknown");
                }

                if (file.getFileName() != null) {
                    if ( file.getOriginalFileType() != null) {
                        String origFileExtension = generateOriginalExtension(file.getOriginalFileType());
                        fileDownload.setFileName(file.getFileName().replaceAll(".tab$", origFileExtension));
                    } else {
                        fileDownload.setFileName(file.getFileName().replaceAll(".tab$", ""));
                    }
                }


                // The fact that we have the "original format" file for this data
                // set, means it's a subsettable, tab-delimited file. Which means
                // we've already prepared a variable header to be added to the
                // stream. We don't want to add it to the stream that's no longer
                // tab-delimited -- that would screw it up! -- so let's remove
                // those headers:

                fileDownload.setNoVarHeader(true);
                fileDownload.setVarHeader(null);

                return fileDownload;
            }
        }
        
        return null;
    }

    // Shouldn't be here; should be part of the DataFileFormatType, or 
    // something like that... 
    
    private static String generateOriginalExtension(String fileType) {

        if (fileType.equalsIgnoreCase("application/x-spss-sav")) {
            return ".sav";
        } else if (fileType.equalsIgnoreCase("application/x-spss-por")) {
            return ".por";
        } else if (fileType.equalsIgnoreCase("application/x-stata")) {
            return ".dta";
        } else if (fileType.equalsIgnoreCase("application/x-dvn-csvspss-zip")) {
            return ".zip";
        } else if (fileType.equalsIgnoreCase("application/x-dvn-tabddi-zip")) {
            return ".zip";
        }

        return "";
    }
}
