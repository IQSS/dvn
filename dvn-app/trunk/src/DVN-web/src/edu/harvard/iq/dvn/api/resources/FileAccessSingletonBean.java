package edu.harvard.iq.dvn.api.resources;


import edu.harvard.iq.dvn.api.entities.DownloadInfo; 


import java.util.List;
import javax.ejb.Singleton;
import javax.ejb.EJB;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
//import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.DataFileFormatType;

import edu.harvard.iq.dvn.core.admin.VDCUser; 
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.web.dataaccess.OptionalAccessService;

/**
 *
 * @author leonidandreev
 */
@Singleton
public class FileAccessSingletonBean {
    @EJB private StudyFileServiceLocal studyFileService; 
     
    @EJB UserServiceLocal userService;
    
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
        VDCUser authenticatedUser = null; 
        String authenticatedUserName = null; 
        
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
            
            // Let's try to authenticate: 
            
            if (authCredentials != null) {
                di.setAuthMethod("password");
                authenticatedUser = authenticateAccess(authCredentials);
                if (authenticatedUser != null) {
                    di.setAuthUserName(authenticatedUser.getUserName());
                }
            } else {
                di.setAuthMethod("anonymous");
            }
            
            // And authorization: 
            // 1st, for Access permissions: 
            
            if (checkAccessPermissions(authenticatedUser, sf)) {
                di.setPassAccessPermissions(true);
            }
            
            // and then, for any Access Restrictions (Terms of Use)
            if (checkAccessRestrictions(authenticatedUser, sf)) {
                di.setPassAccessRestrictions(true);
            }
            
            // Add optional services, if available: 
            
            String fileMimeType = sf.getFileType();
            
            // Image Thumbnail:
            
            if (fileMimeType != null && fileMimeType.startsWith("image/")) {
                di.addServiceAvailable(new OptionalAccessService("thumbnail", "image/png", "imageThumb=true", "Image Thumbnail"));
            }
            
            // Services for subsettable files: 
            
            if (sf.isSubsettable()) {
                // Subsetting: (TODO: separate auth)
                di.addServiceAvailable(new OptionalAccessService("subset", "text/tab-separated-values", "variables=<LIST>", "Column-wise Subsetting"));
            
                // "saved original" file, if available: 
            
                // "No variable header" download 
                
                // Finally, conversion formats: 
                
                
                
            }  
        } 
        
        return di; 
    }
    
    // Decodes the Base64 credential string (passed with the request in 
    // the "Authenticate: " header), extracts the username and password, 
    // and attempts to authenticate the user with the DVN User Service. 
    
    private VDCUser authenticateAccess (String authCredentials) {
        VDCUser vdcUser = null;
        Base64 base64codec = new Base64(); 
        
        String decodedCredentials = ""; 
        byte[] authCredBytes = authCredentials.getBytes();
        
        try {
            byte[] decodedBytes = base64codec.decode(authCredBytes);
            decodedCredentials = new String (decodedBytes, "ASCII");
        } catch (UnsupportedEncodingException e) {
            return null; 
        }

        if (decodedCredentials != null ) {
            int i = decodedCredentials.indexOf(':');
            if (i != -1) { 
                String userPassword = decodedCredentials.substring(i+1);
                String userName = decodedCredentials.substring(0, i);
                
                if (!"".equals(userName)) {
                    vdcUser = userService.findByUserName(userName, true);
                    if (vdcUser == null || 
                        !userService.validatePassword(vdcUser.getId(),userPassword)) {
                        return null;
                    } 
                }
            }
        } 
        
        return vdcUser; 
    }
    
    // Access Permissions:
    
    private Boolean checkAccessPermissions (VDCUser vdcUser, StudyFile studyFile) {
        Boolean accessAuthorized = true; 
        
        if (vdcUser == null || studyFile == null) {
            return accessAuthorized; 
        }
        
        return accessAuthorized; 
    }
    
    
    // Access Restrictions: (Terms of Use)
    
    private Boolean checkAccessRestrictions (VDCUser vdcUser, StudyFile studyFile) {
        Boolean accessAuthorized = true; 
        
        if (vdcUser == null || studyFile == null) {
            return accessAuthorized; 
        }
        
        return accessAuthorized; 
    }
    
    
}
