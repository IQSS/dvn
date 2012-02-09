package edu.harvard.iq.dvn.api.entities;

import java.io.File; 
import java.io.UnsupportedEncodingException; 
import java.util.List;
import java.util.ArrayList;

import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.MetadataFormatType; 


/**
 *
 * @author leonidandreev
 */
public class MetadataFormats {

    private String globalStudyId; 
    private Long studyId;

    private List<MetadataFormatType> formatTypesAvailable; 
    
    public MetadataFormats() {
    }
    
    public MetadataFormats(String globalId) {
        globalStudyId = globalId;

        formatTypesAvailable = new ArrayList<MetadataFormatType>();
        //lookupMetadataFiles(); 
        
    }

    public MetadataFormats(Long localId) {
        studyId = localId;
        
        formatTypesAvailable = new ArrayList<MetadataFormatType>();
        //lookupMetadataFiles(); 
    }
     
    public Long getStudyId() {
        return studyId;
    }
    
    public void setStudyId(Long localId) {
        studyId = localId; 
    }
    
    public String getGlobalStudyId() {
        return globalStudyId; 
    }
    
    public void setGlobalStudyId(String globalId) {
        globalStudyId = globalId; 
    }
    
    public void addFormatType(MetadataFormatType formatType) {
        formatTypesAvailable.add(formatType);
    }
    
    public List<MetadataFormatType> getFormatTypesAvailable() {
	return formatTypesAvailable; 
    }
    
    public Boolean isFormatAvailable(MetadataFormatType formatType) {
	        
        if (globalStudyId != null) {
            int index1 = globalStudyId.indexOf(':');
            int index2 = globalStudyId.indexOf('/');
            String idAuthority = globalStudyId.substring(index1 + 1, index2);
            String idToken = globalStudyId.substring(index2 + 1).toUpperCase();

            File studyFileDir = FileUtil.getStudyFileDir(idAuthority, idToken);

            String formatName = formatType.getName();
            String cachedFileName = null;
            
            if (studyFileDir != null && studyFileDir.exists() && formatName != null) {
                cachedFileName = studyFileDir.getAbsolutePath() + File.separator + "export_" + formatName +".xml";
            } else {
                return false;
            }
            
            File lookupFile = new File (cachedFileName);
            
            if (lookupFile.exists()) {
                return true; 
            }
        }
        return false; 
    }
    
}