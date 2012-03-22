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
/*
 * StudyFieldConstant.java
 *
 * Created on September 26, 2006, 12:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author Ellen Kraffmiller
 */
@Named("sfc")
@ApplicationScoped
public class StudyFieldConstant implements java.io.Serializable  {
    
    public final static String publication = "publication";
    public final static String otherId = "otherId";
    public final static String author =  "author";
    public final static String producer = "producer";
    public final static String software = "software";
    public final static String grantNumber = "grantNumber";
    public final static String distributor = "distributor";
    public final static String distributorContact = "distributorContact";
    public final static String series = "series";
    public final static String version = "version";
    
    public final static String description = "description";
    public final static String keyword = "keyword";
    public final static String topicClassification = "topicClassification";
    public final static String geographicBoundingBox = "geographicBoundingBox";
    
    public final static String note = "note";
    
    public final static String publicationCitation = "publicationCitation";
    public final static String publicationIDType = "publicationIDType";
    public final static String publicationIDNumber = "publicationIDNumber";
    public final static String publicationURL = "publicationURL";
    public final static String publicationReplicationData = "publicationReplicationData";
    
    
    public final static String title = "title";
    public final static String subTitle="subTitle";
    public final static String studyId = "studyId";
    public final static String authorName ="authorName";
    public final static String authorAffiliation = "authorAffiliation";
    public final static String authorIdType = "authorIdType";
    public final static String authorIdValue = "authorIdValue";
    public final static String otherIdValue="otherIdValue";
    public final static String otherIdAgency= "otherIdAgency";
    
    public final static String producerName="producerName";
    public final static String producerURL="producerURL";
    public final static String producerLogo="producerLogo";
    public final static String producerAffiliation="producerAffiliation";
    public final static String producerAbbreviation= "producerAbbreviation";
    public final static String productionDate="productionDate";
    public final static String productionPlace="productionPlace";
    public final static String softwareName="softwareName";
    public final static String softwareVersion="softwareVersion";
    public final static String fundingAgency="fundingAgency";
    public final static String grantNumberValue="grantNumberValue";
    public final static String grantNumberAgency="grantNumberAgency";
    public final static String distributorName="distributorName";
    public final static String distributorURL="distributorURL";
    public final static String distributorLogo="distributorLogo";
    public final static String distributionDate="distributionDate";
    public final static String distributorContactName="distributorContactName";
    public final static String distributorContactAffiliation="distributorContactAffiliation";
    public final static String distributorContactEmail="distributorContactEmail";
    public final static String distributorAffiliation="distributorAffiliation";
    
    public final static String distributorAbbreviation="distributorAbbreviation";
    public final static String depositor="depositor";
    public final static String dateOfDeposit="dateOfDeposit";
    public final static String seriesName="seriesName";
    public final static String seriesInformation="seriesInformation";
    public final static String studyVersion="studyVersion";
    public final static String versionDate="versionDate";
    public final static String keywordValue="keywordValue";
    public final static String keywordVocab="keywordVocab";
    public final static String keywordVocabURI="keywordVocabURI";
    public final static String topicClassValue="topicClassValue";
    public final static String topicClassVocab="topicClassVocab";
    public final static String topicClassVocabURI="topicClassVocabURI";
    public final static String abstractText="abstractText";
    public final static String abstractDate="abstractDate";
    public final static String timePeriodCoveredStart="timePeriodCoveredStart";
    public final static String timePeriodCoveredEnd="timePeriodCoveredEnd";
    public final static String dateOfCollectionStart="dateOfCollectionStart";
    public final static String dateOfCollectionEnd="dateOfCollectionEnd";
    public final static String country="country";
    public final static String geographicCoverage="geographicCoverage";
    public final static String geographicUnit="geographicUnit";
    public final static String westLongitude="westLongitude";
    public final static String eastLongitude="eastLongitude";
    public final static String northLatitude="northLatitude";
    public final static String southLatitude="southLatitude";
    public final static String unitOfAnalysis="unitOfAnalysis";
    public final static String universe="universe";
    public final static String kindOfData="kindOfData";
    public final static String timeMethod="timeMethod";
    public final static String dataCollector="dataCollector";
    public final static String frequencyOfDataCollection="frequencyOfDataCollection";
    public final static String samplingProcedure="samplingProcedure";
    public final static String deviationsFromSampleDesign="deviationsFromSampleDesign";
    public final static String collectionMode="collectionMode";
    public final static String researchInstrument="researchInstrument";
    public final static String dataSources="dataSources";
    public final static String originOfSources="originOfSources";
    public final static String characteristicOfSources="characteristicOfSources";
    public final static String accessToSources="accessToSources";
    public final static String dataCollectionSituation="dataCollectionSituation";
    public final static String actionsToMinimizeLoss="actionsToMinimizeLoss";
    public final static String controlOperations="controlOperations";
    public final static String weighting="weighting";
    public final static String cleaningOperations="cleaningOperations";
    public final static String studyLevelErrorNotes="studyLevelErrorNotes";
    public final static String responseRate="responseRate";
    public final static String samplingErrorEstimates="samplingErrorEstimates";
    public final static String otherDataAppraisal="otherDataAppraisal";
    public final static String placeOfAccess="placeOfAccess";
    public final static String originalArchive="originalArchive";
    public final static String availabilityStatus="availabilityStatus";
    public final static String collectionSize="collectionSize";
    public final static String studyCompletion="studyCompletion";
    public final static String numberOfFiles="numberOfFiles";
    public final static String confidentialityDeclaration="confidentialityDeclaration";
    public final static String specialPermissions="specialPermissions";
    public final static String restrictions="restrictions";
    public final static String contact="contact";
    public final static String citationRequirements="citationRequirements";
    public final static String depositorRequirements="depositorRequirements";
    public final static String conditions="conditions";
    public final static String disclaimer="disclaimer";
    public final static String relatedMaterial="relatedMaterial";
    //public final static String replicationFor="replicationFor";
    //public final static String relatedPublications="relatedPublications";
    public final static String relatedStudies="relatedStudies";
    public final static String otherReferences="otherReferences";
    public final static String notesText="notesText";
    public final static String notesInformationType="notesInformationType";
    public final static String notesInformationSubject="notesInformationSubject";
    
    /*
     * The following getters are needed so we can use them as properties in JSP 
     */
    
    public String getTitle() {
        return title;
    }
    
    public String getStudyId() {
        return studyId;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public String getAuthorAffiliation() {
        return authorAffiliation;
    }
        
    public static String getAuthorIdType() {
        return authorIdType;
    }

    public static String getAuthorIdValue() {
        return authorIdValue;
    }
    
    public String getOtherId() {
        return otherId;
    }
    
    public String getOtherIdAgency() {
        return otherIdAgency;
    }
    
    public String getProducerName() {
        return producerName;
    }
    
    public String getProducerURL() {
        return producerURL;
    }
    
    public String getProducerLogo() {
        return producerLogo;
    }
    
    public String getProducerAbbreviation() {
        return producerAbbreviation;
    }
    
    public String getProductionDate() {
        return productionDate;
    }
    
    public String getSoftwareName() {
        return softwareName;
    }
    
    public String getSoftwareVersion() {
        return softwareVersion;
    }
    
    public String getFundingAgency() {
        return fundingAgency;
    }
    
    public String getGrantNumber() {
        return grantNumber;
    }
    
    public String getGrantNumberAgency() {
        return grantNumberAgency;
    }
    
    public String getDistributorName() {
        return distributorName;
    }
    
    public String getDistributorURL() {
        return distributorURL;
    }
    
    public String getDistributorLogo() {
        return distributorLogo;
    }
    
    public String getDistributionDate() {
        return distributionDate;
    }
    
    public String getDistributorContactName() {
        return distributorContactName;
    }
    
    public String getDistributorContactAffiliation() {
        return distributorContactAffiliation;
    }
    
    public String getDistributorContactEmail() {
        return distributorContactEmail;
    }
    
    public String getDepositor() {
        return depositor;
    }
    
    public String getDateOfDeposit() {
        return dateOfDeposit;
    }
    
    public String getSeriesName() {
        return seriesName;
    }
    
    public String getSeriesInformation() {
        return seriesInformation;
    }
    
    public String getStudyVersion() {
        return studyVersion;
    }
    
    public String getKeywordValue() {
        return keywordValue;
    }
    
    public String getKeywordVocab() {
        return keywordVocab;
    }
    
    public String getKeywordVocabURI() {
        return keywordVocabURI;
    }
    
    public String getTopicClassValue() {
        return topicClassValue;
    }
    
    public String getTopicClassVocab() {
        return topicClassVocab;
    }
    
    public String getTopicClassVocabURI() {
        return topicClassVocabURI;
    }
    
    public String getAbstractText() {
        return abstractText;
    }
    
    public String getAbstractDate() {
        return abstractDate;
    }
    
    public String getTimePeriodCoveredStart() {
        return timePeriodCoveredStart;
    }
    
    public String getTimePeriodCoveredEnd() {
        return timePeriodCoveredEnd;
    }
    
    public String getDateOfCollectionStart() {
        return dateOfCollectionStart;
    }
    
    public String getDateOfCollectionEnd() {
        return dateOfCollectionEnd;
    }
    
    public String getCountry() {
        return country;
    }
    
    public String getGeographicCoverage() {
        return geographicCoverage;
    }
    
    public String getGeographicUnit() {
        return geographicUnit;
    }
    
    public String getUnitOfAnalysis() {
        return unitOfAnalysis;
    }
    
    public String getUniverse() {
        return universe;
    }
    
    public String getKindOfData() {
        return kindOfData;
    }
    
    public String getTimeMethod() {
        return timeMethod;
    }
    
    public String getDataCollector() {
        return dataCollector;
    }
    
    public String getFrequencyOfDataCollection() {
        return frequencyOfDataCollection;
    }
    
    public String getSamplingProcedure() {
        return samplingProcedure;
    }
    
    public String getDeviationsFromSampleDesign() {
        return deviationsFromSampleDesign;
    }
    
    public String getCollectionMode() {
        return collectionMode;
    }
    
    public String getResearchInstrument() {
        return researchInstrument;
    }
    
    public String getDataSources() {
        return dataSources;
    }
    
    public String getOriginOfSources() {
        return originOfSources;
    }
    
    public String getCharacteristicOfSources() {
        return characteristicOfSources;
    }
    
    public String getAccessToSources() {
        return accessToSources;
    }
    
    public String getDataCollectionSituation() {
        return dataCollectionSituation;
    }
    
    public String getActionsToMinimizeLoss() {
        return actionsToMinimizeLoss;
    }
    
    public String getControlOperations() {
        return controlOperations;
    }
    
    public String getWeighting() {
        return weighting;
    }
    
    public String getCleaningOperations() {
        return cleaningOperations;
    }
    
    public String getStudyLevelErrorNotes() {
        return studyLevelErrorNotes;
    }
    
    public String getResponseRate() {
        return responseRate;
    }
    
    public String getSamplingErrorEstimates() {
        return samplingErrorEstimates;
    }
    
    public String getOtherDataAppraisal() {
        return otherDataAppraisal;
    }
    
    public String getPlaceOfAccess() {
        return placeOfAccess;
    }
    
    public String getOriginalArchive() {
        return originalArchive;
    }
    
    public String getAvailabilityStatus() {
        return availabilityStatus;
    }
    
    public String getCollectionSize() {
        return collectionSize;
    }
    
    public String getStudyCompletion() {
        return studyCompletion;
    }
    
    public String getConfidentialityDeclaration() {
        return confidentialityDeclaration;
    }
    
    public String getSpecialPermissions() {
        return specialPermissions;
    }
    
    public String getRestrictions() {
        return restrictions;
    }
    
    public String getContact() {
        return contact;
    }
    
    public String getCitationRequirements() {
        return citationRequirements;
    }
    
    public String getDepositorRequirements() {
        return depositorRequirements;
    }
    
    public String getConditions() {
        return conditions;
    }
    
    public String getDisclaimer() {
        return disclaimer;
    }
    
    public String getRelatedMaterial() {
        return relatedMaterial;
    }
    
    /*
    public String getRelatedPublications() {
        return relatedPublications;
    }*/
    
    public String getRelatedStudies() {
        return relatedStudies;
    }
    
    public String getOtherReferences() {
        return otherReferences;
    }
    
    public String getNotesText() {
        return notesText;
    }
    
    public String getNotesInformationType() {
        return notesInformationType;
    }
    
    public String getNotesInformationSubject() {
        return notesInformationSubject;
    }
    
    public String getProducerAffiliation() {
        return producerAffiliation;
    }
    
    public String getProductionPlace() {
        return productionPlace;
    }
    
    public String getDistributorAbbreviation() {
        return distributorAbbreviation;
    }

    public String getDistributorAffiliation() {
        return distributorAffiliation;
    }

    public String getVersionDate() {
        return versionDate;
    }

    public String getSubTitle() {
        return subTitle;
    }

    /*
    public String getReplicationFor() {
        return replicationFor;
    }*/

    public String getWestLongitude() {
        return westLongitude;
    }

    public String getEastLongitude() {
        return eastLongitude;
    }

    public String getNorthLatitude() {
        return northLatitude;
    }

    public String getSouthLatitude() {
        return southLatitude;
    }

    public String getNumberOfFiles() {
        return numberOfFiles;
    }

    public static String getAuthor() {
        return author;
    }

    public static String getDistributorContact() {
        return distributorContact;
    }

    public static String getDescription() {
        return description;
    }

    public static String getDistributor() {
        return distributor;
    }

    public static String getGeographicBoundingBox() {
        return geographicBoundingBox;
    }

    public static String getGrantNumberValue() {
        return grantNumberValue;
    }

    public static String getKeyword() {
        return keyword;
    }

    public static String getNote() {
        return note;
    }

    public static String getOtherIdValue() {
        return otherIdValue;
    }

    public static String getProducer() {
        return producer;
    }

    public static String getPublication() {
        return publication;
    }

    public static String getPublicationCitation() {
        return publicationCitation;
    }

    public static String getPublicationIDNumber() {
        return publicationIDNumber;
    }

    public static String getPublicationIDType() {
        return publicationIDType;
    }

    public static String getPublicationReplicationData() {
        return publicationReplicationData;
    }

    public static String getPublicationURL() {
        return publicationURL;
    }

    public static String getSeries() {
        return series;
    }

    public static String getSoftware() {
        return software;
    }

    public static String getTopicClassification() {
        return topicClassification;
    }

    public static String getVersion() {
        return version;
    }
    
}
