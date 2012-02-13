package edu.harvard.iq.dvn.api.resources;


import edu.harvard.iq.dvn.api.entities.DownloadInfo; 


import java.util.List;
import javax.ejb.Singleton;
import javax.ejb.EJB;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
//import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.DataFileFormatType;

import edu.harvard.iq.dvn.core.admin.VDCUser; 
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.web.dataaccess.OptionalAccessService;

import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;


/**
 *
 * @author leonidandreev
 */
@Singleton
public class FileAccessSingletonBean {
    @EJB private StudyFileServiceLocal studyFileService; 
     
    @EJB UserServiceLocal userService;
    
    @EJB StudyServiceLocal studyService; 
    
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
            
            if (checkAccessPermissions(authenticatedUser, di)) {
                di.setPassAccessPermissions(true);
            }
            
            // and then, for any Access Restrictions (Terms of Use)
            if (checkAccessRestrictions(authenticatedUser, di)) {
                di.setPassAccessRestrictions(true);
            }
            
            // Add optional services, if available: 
            
            String fileMimeType = sf.getFileType();
            
            // Image Thumbnail:
            
            if (fileMimeType != null && fileMimeType.startsWith("image/")) {
                di.addServiceAvailable(new OptionalAccessService("thumbnail", "image/png", "imageThumb=true", "Image Thumbnail (64x64)"));
            }
            
            // Services for subsettable files: 
            
            if (sf.isSubsettable()) {
                // Subsetting: (TODO: separate auth)
                di.addServiceAvailable(new OptionalAccessService("subset", "text/tab-separated-values", "variables=&lt;LIST&gt;", "Column-wise Subsetting"));
            
                // "saved original" file, if available: 
                
                String originalFormatType = sf.getOriginalFileType();
                String userFriendlyOriginalFormatName = null;

                if ( !StringUtil.isEmpty( originalFormatType ) ) {

                    userFriendlyOriginalFormatName = FileUtil.getUserFriendlyOriginalType(sf);
                    String originalTypeLabel = "";

                    if (!StringUtil.isEmpty(userFriendlyOriginalFormatName)) {
                        originalTypeLabel = userFriendlyOriginalFormatName;
                    } else {
                        originalTypeLabel = originalFormatType;
                    }

                    String originalFileDesc = "Saved original (" + originalTypeLabel + ")";
                    String originalFileServiceArg = "fileFormat=original";
                    
                    di.addServiceAvailable(new OptionalAccessService(
                            "original", 
                            originalFormatType, 
                            originalFileServiceArg, 
                            originalFileDesc));
                }

            
                // "No variable header" download
                
                di.addServiceAvailable(new OptionalAccessService("dataonly", "text/tab-separated-values", "noVarHeader=true", "Data only, no variable header"));
                
                // Finally, conversion formats: 
                
                if (allSupportedTypes == null) {
                    allSupportedTypes = studyService.getDataFileFormatTypes();
                }
                
                for (DataFileFormatType dft : allSupportedTypes) {
                    if (originalFormatType == null ||
                            !originalFormatType.equals(dft.getMimeType()) ) {
                        
                        String formatServiceArg = "fileFormat="+dft.getName();
                        String formatDescription = "Data in "+ dft.getName() + " format (generated)";
                        di.addServiceAvailable(new OptionalAccessService(
                            dft.getName(), 
                            dft.getMimeType(), 
                            formatServiceArg, 
                            formatDescription));
                    }
                }
            }  
        } 
        
        return di; 
    }
    
    // Decodes the Base64 credential string (passed with the request in 
    // the "Authenticate: " header), extracts the username and password, 
    // and attempts to authenticate the user with the DVN User Service. 
    
    public VDCUser authenticateAccess (String authCredentials) {
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
    
    private Boolean checkAccessPermissions (VDCUser vdcUser, DownloadInfo di) {
        StudyFile studyFile = di.getStudyFile();
        
        if (studyFile == null) {
            return false; 
        }
 
        if (!isPublicAccess(studyFile)) {
            di.setAccessPermissionsApply(true);
        } else {
            return true; 
        }
        
        if (studyFile.isFileRestrictedForUser(vdcUser, null, null)) {
            return false; 
        }
        
        return true; 
    }
    
    
    // Access Restrictions: (Terms of Use)
    
    private Boolean checkAccessRestrictions (VDCUser vdcUser, DownloadInfo di) {
        StudyFile studyFile = di.getStudyFile();        
        
        if (studyFile == null) {
            return false; 
        }
        
        if (!isUnderTermsOfUse(studyFile)) {
            return true; 
        }       
        
        di.setAccessRestrictionsAply(true);
        
        if (vdcUser == null) {
            return false; 
        }
            
        // Finally, heck if the user is authorized to be responsible for the 
        // enforcement of the Terms of use:
        
        if (vdcUser.isBypassTermsOfUse()) {
            return true; 
        }
        
        return false; 

    }
  
    // Check if this file is freely available ("public")
    // note that restrictions can apply on multiple levels. 
    
    private Boolean isPublicAccess (StudyFile studyFile) {
        if (studyFile.isRestricted()) {
            return false; 
        }
        
        if (studyFile.getStudy() != null) {
            if (studyFile.getStudy().isRestricted()) {
                return false; 
            }
            if (studyFile.getStudy().getOwner() != null) {
                if (studyFile.getStudy().getOwner().isFilesRestricted()) {
                    return false; 
                }
            }
        }
        
        return true; 
    }
    
    // Check if any Terms of Use apply
    
    private Boolean isUnderTermsOfUse (StudyFile studyFile) {
        if (studyFile == null) {
            return false; 
        }
        
        if (studyFile.getStudy() != null) {
        
            if (studyFile.getStudy().getOwner() != null) {
                if (studyFile.getStudy().getOwner().isDownloadTermsOfUseEnabled()) {
                    return true; 
                }
            }
        
            if (studyFile.getStudy().getReleasedVersion() != null &&
                    studyFile.getStudy().getReleasedVersion().getMetadata() != null) {
                if (studyFile.getStudy().getReleasedVersion().getMetadata().isTermsOfUseEnabled()) {
                    return true; 
                }
            }
        } 
        
        return false; 
    }
    
    
}
