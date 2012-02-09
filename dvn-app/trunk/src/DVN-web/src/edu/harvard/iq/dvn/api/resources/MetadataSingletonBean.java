package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.MetadataInstance;
import edu.harvard.iq.dvn.api.entities.MetadataFormats;
import edu.harvard.iq.dvn.api.entities.MetadataSearchFields;
import edu.harvard.iq.dvn.api.entities.MetadataSearchResults;


import java.util.List;
import java.util.ArrayList; 
import javax.ejb.Singleton;
import javax.ejb.EJB;

import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyExporterFactoryLocal;
import edu.harvard.iq.dvn.core.study.StudyExporter;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.MetadataFormatType;
//import edu.harvard.iq.dvn.core.study.StudyField;
//import edu.harvard.iq.dvn.core.study.StudyFieldServiceLocal;


/**
 *
 * @author leonidandreev
 */
@Singleton
public class MetadataSingletonBean {
    @EJB 
    private StudyServiceLocal studyService;
    @EJB
    IndexServiceLocal indexService;
    @EJB 
    StudyExporterFactoryLocal studyExporterFactory;
    
    //@EJB
    //StudyFieldServiceLocal studyFieldService;
    
    private List<String> searchableFields = null; 

    public MetadataSingletonBean() {
    }
    
    // Looks up Metadata Instance by Global ID:
    public MetadataInstance getMetadata(String globalId, String formatType, Long versionNumber, String partialExclude, String partialInclude) {
        MetadataInstance m = null; 
        StudyVersion sv = null; 
        Long studyId = null; 
        
        if (globalId != null) {
            try {
                sv = studyService.getStudyVersion(globalId, versionNumber);
                if (sv != null) {
                    // First, verify that the format requested is legit/supported:
                    
                    if (formatType == null) {
                        formatType = "ddi";
                    }
                    
                    MetadataFormatType mfTypeSupported = null; 
                    
                    for (MetadataFormatType mfType : studyService.findAllMetadataExportFormatTypes()) {
                            if (formatType.equals(mfType.getName())) {
                                mfTypeSupported = mfType;
                            }
                    }
                    
                    if (mfTypeSupported == null) {
                        m = new MetadataInstance (globalId);
                        m.setAvailability(false);
                        return m; 
                    }
                    
                    // If optional partial exclude or include argument are 
                    // supplied, verify that the functionality is supported:
                    
                    if (partialExclude != null) {
                        if (!(mfTypeSupported.isPartialExcludeSupported())) {
                            m = new MetadataInstance (globalId);
                            m.setAvailability(false);
                            return m; 
                        }
                    }
                    
                    if (partialInclude != null) {
                        if (!(mfTypeSupported.isPartialSelectSupported())) {
                            m = new MetadataInstance (globalId);
                            m.setAvailability(false);
                            return m;
                        }
                    }

                    m = new MetadataInstance (globalId, formatType, partialExclude, partialInclude);
                    if (m != null) {
                        StudyExporter studyExporter = null; 
                        if (partialExclude != null || partialInclude != null) {
                            studyExporter = studyExporterFactory.getStudyExporter(formatType);
                            m.setStudy(sv.getStudy());
                        }
                        m.lookupMetadata(studyExporter);
                        // local database id:
                        if (sv.getStudy() != null) {
                            studyId = sv.getStudy().getId();
                        } else {
                            return null; 
                        }
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
    public MetadataInstance getMetadata(Long studyId, String formatType, Long versionNumber, String partialExclude, String partialInclude) {
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
                            m = new MetadataInstance (globalId, formatType, partialExclude, partialInclude); 
                            if (m != null) {
                                m.lookupMetadata();
                                m.setStudyId(studyId);
                            }
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

    public MetadataSearchFields getMetadataSearchFields() {
        MetadataSearchFields msf = null; 
        
        if (searchableFields == null) {
            // In order to initialize the list, eventually we want to use the
            // StudyFieldService, like this: 
            //
            //   searchableFields = studyFieldService.findAdvSearchDefault();
            //
            // for now, however, we are going to hard-code it, the same way
            // it is done in the AdvStudyPage:
            String[] fieldsHardCoded = {"title", "authorName", "globalId", "otherId", "abstractText", "keywordValue", "keywordVocabulary", "topicClassValue", "topicClassVocabulary", "producerName", "distributorName", "fundingAgency", "productionDate", "distributionDate", "dateOfDeposit", "timePeriodCoveredStart", "timePeriodCoveredEnd", "country", "geographicCoverage", "geographicUnit", "universe", "kindOfData"};
            searchableFields = new ArrayList<String>(fieldsHardCoded.length);
            
            for (int i = 0; i < fieldsHardCoded.length; i++) {
                searchableFields.add(fieldsHardCoded[i]);
            }
            // TODO: discuss this with the team. -- L.A.
        }
        msf = new MetadataSearchFields(searchableFields);

     
        return msf; 
    }
    
    public MetadataSearchResults getMetadataSearchResults(String queryString) {
        MetadataSearchResults msr = null; 
        
        // Run the search utilizing Index Service:
        
        List<Long> matchingStudyIds = indexService.query(queryString);
        
        if (matchingStudyIds == null) {
            msr = new MetadataSearchResults();
        } else {
            // Convert numeric (database) study ids to global ids:
            List<String> matchingGlobalIds = new ArrayList<String>();
            for (Long studyId : matchingStudyIds) {
                Study lookupStudy = studyService.getStudy(studyId);
                String lookupStudyGlobalId = null; 
                if (lookupStudy != null) {
                    lookupStudyGlobalId = lookupStudy.getGlobalId();
                    if (lookupStudyGlobalId != null && !lookupStudyGlobalId.equals("")) {
                        matchingGlobalIds.add(lookupStudyGlobalId);
                    }
                }
            }
            msr = new MetadataSearchResults(matchingGlobalIds);
        }
        
        if (msr != null) {
            msr.setQueryString(queryString);
        }
        
        return msr; 
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
