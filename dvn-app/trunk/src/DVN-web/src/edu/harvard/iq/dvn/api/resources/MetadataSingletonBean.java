package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.MetadataInstance;
import edu.harvard.iq.dvn.api.entities.MetadataFormats;
import java.util.List;
import javax.ejb.Singleton;
import javax.ejb.EJB;

import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.MetadataFormatType;


/**
 *
 * @author leonidandreev
 */
@Singleton
public class MetadataSingletonBean {
    @EJB private StudyServiceLocal studyService;
    
    private List<MetadataFormatType> allSupportedTypes; 

    public MetadataSingletonBean() {
    }
    
    // Looks up Metadata Instance by Global ID:
    public MetadataInstance getMetadata(String globalId, String formatType, Long versionNumber) {
        MetadataInstance m = null; 
        StudyVersion sv = null; 
        Long studyId = null; 
        
        if (globalId != null) {
            try {
                sv = studyService.getStudyVersion(globalId, versionNumber);
                if (sv != null) {
                    
                    m = new MetadataInstance (globalId, formatType); 
                    // local database id:
                    if (sv.getStudy() != null) {
                        studyId = sv.getStudy().getId();
                    } else {
                        return null; 
                    }
                    m.setStudyId(studyId);
                    return m; 
                }
            } catch (IllegalArgumentException ex) {
                return null; 
                // We don't need to do anything special here -- we simply
                // return null, and jersey app will cook a proper 404 
                // response. 
            }
        } 
        
        return null; 
    }
    
    // Looks up Metadata Instance by Local (database) ID:
    public MetadataInstance getMetadata(Long studyId, String formatType, Long versionNumber) {
        MetadataInstance m = null; 
        StudyVersion sv = null; 
        String globalId = null; 
        
        if (studyId != null) {
            try {
                sv = studyService.getStudyVersion(studyId, versionNumber);
                if (sv != null) {
                    // global id:
                    if (sv.getStudy() != null) {
                        globalId = sv.getStudy().getGlobalId();
                        if (globalId != null) {
                            m = new MetadataInstance (globalId, formatType); 
                            m.setStudyId(studyId);
                            return m; 
                        }
                    } 
                }
            } catch (java.lang.IllegalArgumentException ex) {
                return null; 
                // We don't need to do anything special here -- we simply
                // return null, and jersey app will cook a proper 404 
                // response. 
            }
        } 

        return null; 
    }
    
    // Looks up Metadata Formats by Global ID:
    public MetadataFormats getMetadataFormatsAvailable(String globalId) {
        MetadataFormats mf = null; 
        StudyVersion sv = null; 
        Long studyId = null; 
        
        if (globalId != null) {
            try {
                // We pass versionNumber=null to getStudyVersion() below, 
                // since we are only interested in the currently released version:
                sv = studyService.getStudyVersion(globalId, null);
                if (sv != null) {
                    
                    mf = new MetadataFormats (globalId); 
                    // local database id:
                    if (sv.getStudy() != null) {
                        studyId = sv.getStudy().getId();
                        mf.setStudyId(studyId);
                        lookupMetadataTypesAvailable(mf);
                        return mf; 
                    } 
                } 
            } catch (IllegalArgumentException ex) {
                return null; 
                // We don't need to do anything special here -- we simply
                // return null, and jersey app will cook a proper 404 
                // response. 
            }
        } 
        
        return null; 
    }
    
    // Looks up Metadata Formats by Local (database) ID:
    public MetadataFormats getMetadataFormatsAvailable(Long studyId) {
        MetadataFormats mf = null; 
        StudyVersion sv = null; 
        String globalId = null; 
        
        if (studyId != null) {
            try {
                // We pass versionNumber=null to getStudyVersion() below, 
                // since we are only interested in the currently released version:
                sv = studyService.getStudyVersion(studyId, null);
                if (sv != null) {
                    // global id:
                    if (sv.getStudy() != null) {
                        globalId = sv.getStudy().getGlobalId();
                        if (globalId != null) {
                            mf = new MetadataFormats (globalId); 
                            mf.setStudyId(studyId);
                            lookupMetadataTypesAvailable(mf);
                            return mf;
                        }
                    } 
                }
            } catch (java.lang.IllegalArgumentException ex) {
                return null; 
                // We don't need to do anything special here -- we simply
                // return null, and jersey app will cook a proper 404 
                // response. 
            }
        } 

        return null; 
    }

    private void lookupMetadataTypesAvailable (MetadataFormats metadataFormats) {
        for (MetadataFormatType mfType : studyService.findAllMetadataExportFormatTypes()) {
            // We want to skip the formats that are not XML-based
            // (for now at least):
            if ("application/xml".equals(mfType.getMimeType())) {
                if (metadataFormats.isFormatAvailable(mfType)) {
                    metadataFormats.addFormatType(mfType);
                }
            }
        }
    }
    
    
}
