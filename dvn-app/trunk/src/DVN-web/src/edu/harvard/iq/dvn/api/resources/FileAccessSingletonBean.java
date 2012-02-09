package edu.harvard.iq.dvn.api.resources;


import edu.harvard.iq.dvn.api.entities.DownloadInfo; 


import java.util.List;
import javax.ejb.Singleton;
import javax.ejb.EJB;

import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;

//import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;

import edu.harvard.iq.dvn.core.study.DataFileFormatType;
import edu.harvard.iq.dvn.core.admin.VDCUser; 
import edu.harvard.iq.dvn.core.web.dataaccess.OptionalAccessService;

/**
 *
 * @author leonidandreev
 */
@Singleton
public class FileAccessSingletonBean {
    @EJB private StudyFileServiceLocal studyFileService; 
    
    private List<DataFileFormatType> allSupportedTypes = null; 

    public FileAccessSingletonBean() {
    }
    
    // Looks up StudyFile by (database) ID:
    
    public DownloadInfo getDownloadInfo(Long studyFileId) {
        return getDownloadInfo(studyFileId, null);
    }
    
    public DownloadInfo getDownloadInfo(Long studyFileId, String authCredentials) {
        DownloadInfo di = null; 
        StudyFile sf = null; 
        Long studyId = null; 
        
        if (studyFileId != null) {
            try {
                sf = studyFileService.getStudyFile(studyFileId);
                if (sf != null) {
                    
                    di = new DownloadInfo (sf);
                    if (di == null) {
                        return null; 
                    }
                } else {
                    return null; 
                }
            } catch (Exception ex) {
                return null; 
                // We don't care much what happened - but for whatever 
                // reason, we haven't been able to look up the file.
                //
                // We don't need to do anything special here -- we simply
                // return null, and jersey app will cook a proper 404 
                // response. 
            }
            
            // Add optional services, if available: 
            
            String fileMimeType = sf.getFileType();
            
            // Image Thumbnail:
            
            if (fileMimeType != null && fileMimeType.startsWith("image/")) {
                di.addServiceAvailable(new OptionalAccessService("thumbnail", "image/png", "imageThumb=true", "Image Thumbnail"));
            }
            
            // Subsetting: (TODO: separate auth)
            
            if (sf.isSubsettable()) {
                di.addServiceAvailable(new OptionalAccessService("subset", "text/tab-separated-values", "variables=<LIST>", "Column-wise Subsetting"));
            }
        } 
        
        return di; 
    }
    
    
    private VDCUser authenticateAccess (String authCredentials) {
        
        // anonymous: 
        
        return null; 
    }
    
    // Access Permissions:
    
    private Boolean checkAccessPermissions (VDCUser vdcUser, StudyFile studyFile) {
        Boolean accessAuthorized = true; 
        
        if (vdcUser == null || studyFile == null) {
            return false; 
        }
        
        return accessAuthorized; 
    }
    
    
    // Access Restrictions: (Terms of Use)
    
    private Boolean checkAccessRestrictions (VDCUser vdcUser, StudyFile studyFile) {
        Boolean accessAuthorized = true; 
        
        if (vdcUser == null || studyFile == null) {
            return false; 
        }
        
        return accessAuthorized; 
    }
    
    
    
}
