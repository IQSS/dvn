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
