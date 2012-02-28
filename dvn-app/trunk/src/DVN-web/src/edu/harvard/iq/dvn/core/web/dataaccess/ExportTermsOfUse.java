package edu.harvard.iq.dvn.core.web.dataaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream; 
import java.io.FileOutputStream; 
import java.io.IOException;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.Metadata;

import edu.harvard.iq.dvn.core.util.StringUtil;

/**
 *
 * @author leonidandreev
 */
public class ExportTermsOfUse {
    private static String dvnTermsOfUse = null; 
    
    public ExportTermsOfUse() {
    } 
        
    public static FileAccessObject export (Study study) {
        return export (study, null);
    }
    
    public static FileAccessObject export (Study study, String dvnTermsOfUse) {
        String dvTermsOfUse = null;
        String studyTermsOfUse = null;
        FileAccessObject touDownload = null; 
        
        String termsOfUseExported = ""; 
        
        // Network level: 
        
        if (!StringUtil.isEmpty(dvnTermsOfUse)) {
            termsOfUseExported = "Dataverse Network-level Terms of Use:\n";
            termsOfUseExported = termsOfUseExported.concat( dvnTermsOfUse );
            termsOfUseExported = termsOfUseExported.concat("\n");            
        }
        
        // Dataverse level: 
        
        if (!study.isIsHarvested() ) {
            dvTermsOfUse = study.getOwner().isDownloadTermsOfUseEnabled() ? study.getOwner().getDownloadTermsOfUse() : null;
        }
        
        if (!StringUtil.isEmpty(dvTermsOfUse)) {
            termsOfUseExported = "Dataverse-level Terms of Use:\n";
            termsOfUseExported = termsOfUseExported.concat( dvTermsOfUse );
            termsOfUseExported = termsOfUseExported.concat("\n");  
        }
        
        // Study level: 
        
        if (study.getReleasedVersion() != null) {
            Metadata metadataReleased = study.getReleasedVersion().getMetadata();
            if ( metadataReleased != null) {
                studyTermsOfUse = metadataReleased.getTermsOfUseAsString();
            }
        }
        
        if (!StringUtil.isEmpty(studyTermsOfUse)) {
            termsOfUseExported = "Study-level Terms of Use:\n";
            termsOfUseExported = termsOfUseExported.concat( studyTermsOfUse );
            termsOfUseExported = termsOfUseExported.concat("\n");  
        }

        
        // Save as a temp file:
        
        if (!StringUtil.isEmpty(termsOfUseExported)) {
            try {
                File tempTOU = File.createTempFile("TermsOfUse.", ".txt");
                OutputStream outTOU = new FileOutputStream(tempTOU);
                outTOU.write(termsOfUseExported.getBytes());
                outTOU.close();
                
                touDownload = new FileAccessObject(); 
                
                touDownload.setInputStream(new FileInputStream(tempTOU));
                touDownload.setIsLocalFile(true);
                touDownload.setMimeType("text/plain");
                touDownload.setFileName(tempTOU.getName());                   
                touDownload.setNoVarHeader(true);
                touDownload.setVarHeader(null);

                
            } catch (IOException ex) {
                return null; 
            }
        }
                
        return touDownload;  
    }
    
}
