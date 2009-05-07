/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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

/**
 *
 * @author Ellen Kraffmiller
 */
public final class StudyFieldConstant implements java.io.Serializable  {
    
    public final static String title = "title";
    public final static String subTitle="subTitle";
    public final static String studyId = "studyId";
    public final static String authorName ="authorName";
    public final static String authorAffiliation = "authorAffiliation";
    public final static String otherId="otherId";
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
    public final static String grantNumber="grantNumber";
    public final static String grantNumberAgency="grantNumberAgency";
    public final static String distributorName="distributorName";
    public final static String distributorURL="distributorURL";
    public final static String distributorLogo="distributorLogo";
    public final static String distributionDate="distributionDate";
    public final static String distributorContact="distributorContact";
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
    public final static String replicationFor="replicationFor";
    public final static String relatedPublications="relatedPublications";
    public final static String relatedStudies="relatedStudies";
    public final static String otherReferences="otherReferences";
    public final static String NotesText="NotesText";
    public final static String NotesInformationType="NotesInformationType";
    public final static String NotesInformationSubject="NotesInformationSubject";
    
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
    
    public String getDistributorContact() {
        return distributorContact;
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
    
    public String getRelatedPublications() {
        return relatedPublications;
    }
    
    public String getRelatedStudies() {
        return relatedStudies;
    }
    
    public String getOtherReferences() {
        return otherReferences;
    }
    
    public String getNotesText() {
        return NotesText;
    }
    
    public String getNotesInformationType() {
        return NotesInformationType;
    }
    
    public String getNotesInformationSubject() {
        return NotesInformationSubject;
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

    public String getReplicationFor() {
        return replicationFor;
    }

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
    
}
