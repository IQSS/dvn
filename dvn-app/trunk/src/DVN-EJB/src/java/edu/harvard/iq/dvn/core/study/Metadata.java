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
 * Metadata.java
 *
 * Created on June 1, 2008 2:44 PM
 *
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.util.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class Metadata implements java.io.Serializable {
    /**
     * Holds value of property id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
  
    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getId() {
        return this.id;
    }
    
    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(Long id) {
        this.id = id;
    }
    @OneToOne(mappedBy="metadata")
    private StudyVersion studyVersion;

    public StudyVersion getStudyVersion() {
        return studyVersion;
    }

    public void setStudyVersion(StudyVersion studyVersion) {
        this.studyVersion = studyVersion;
    }

    
    @OneToOne(mappedBy="metadata")
    private Template template;

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

  

    
    public Metadata () {
    }
    
    private boolean copyField(TemplateField tf, boolean copyHidden, boolean copyDisabled) {
        return (!tf.isHidden() && !tf.isDisabled()) ||
               (copyHidden && tf.isHidden() ) ||
               (copyDisabled && tf.isDisabled());       
    }
    
        
    public  Metadata(Metadata source, boolean copyHidden, boolean copyDisabled ) {
     
        this.setUNF(source.UNF);
        this.setStudyFieldValues(new ArrayList<StudyFieldValue>());
        
        Template sourceTemplate = source.getTemplate() != null ? source.getTemplate() : source.getStudyVersion().getStudy().getTemplate();
        
        // create a Map so we can look up each template field and check its field input level
        Map<String,TemplateField> tfMap = new HashMap();        
        for (TemplateField tf : sourceTemplate.getTemplateFields()){
            tfMap.put(tf.getStudyField().getName(), tf);
        }
            
        if( copyField(tfMap.get(StudyFieldConstant.accessToSources), copyHidden, copyDisabled) ) {
             this.setAccessToSources(source.accessToSources); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.actionsToMinimizeLoss), copyHidden, copyDisabled) ) {
             this.setActionsToMinimizeLoss(source.actionsToMinimizeLoss); 
        }
        
        if( copyField(tfMap.get(StudyFieldConstant.availabilityStatus), copyHidden, copyDisabled) ) {
             this.setAvailabilityStatus(source.availabilityStatus); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.characteristicOfSources), copyHidden, copyDisabled) ) {
             this.setCharacteristicOfSources(source.characteristicOfSources); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.citationRequirements), copyHidden, copyDisabled) ) {
             this.setCitationRequirements(source.citationRequirements); 
        }
        
        if( copyField(tfMap.get(StudyFieldConstant.cleaningOperations), copyHidden, copyDisabled) ) {
             this.setCleaningOperations(source.cleaningOperations); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.collectionMode), copyHidden, copyDisabled) ) {
             this.setCollectionMode(source.collectionMode); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.collectionSize), copyHidden, copyDisabled) ) {
             this.setCollectionSize(source.collectionSize); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.conditions), copyHidden, copyDisabled) ) {
             this.setConditions(source.conditions); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.confidentialityDeclaration), copyHidden, copyDisabled) ) {
             this.setConfidentialityDeclaration(source.confidentialityDeclaration); 
        }
        
        if( copyField(tfMap.get(StudyFieldConstant.contact), copyHidden, copyDisabled) ) {
             this.setContact(source.contact); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.controlOperations), copyHidden, copyDisabled) ) {
             this.setControlOperations(source.controlOperations); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.country), copyHidden, copyDisabled) ) {
             this.setCountry(source.country); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.dataCollectionSituation), copyHidden, copyDisabled) ) {
             this.setDataCollectionSituation(source.dataCollectionSituation); 
        }
        
        if( copyField(tfMap.get(StudyFieldConstant.dataCollector), copyHidden, copyDisabled) ) {
             this.setDataCollector(source.dataCollector); 
        }
        
        if( copyField(tfMap.get(StudyFieldConstant.dataSources), copyHidden, copyDisabled) ) {
             this.setDataSources(source.dataSources); 
        }
        
        if( copyField(tfMap.get(StudyFieldConstant.dateOfCollectionEnd), copyHidden, copyDisabled) ) {
             this.setDateOfCollectionEnd(source.dateOfCollectionEnd); 
        }        

        if( copyField(tfMap.get(StudyFieldConstant.dateOfCollectionStart), copyHidden, copyDisabled) ) {
             this.setDateOfCollectionStart(source.dateOfCollectionStart); 
        }  

        if( copyField(tfMap.get(StudyFieldConstant.dateOfDeposit), copyHidden, copyDisabled) ) {
             this.setDateOfDeposit(source.dateOfDeposit); 
        }         

        if( copyField(tfMap.get(StudyFieldConstant.depositor), copyHidden, copyDisabled) ) {
             this.setDepositor(source.depositor); 
        }          

        if( copyField(tfMap.get(StudyFieldConstant.depositorRequirements), copyHidden, copyDisabled) ) {
             this.setDepositorRequirements(source.depositorRequirements); 
        } 
        
        if( copyField(tfMap.get(StudyFieldConstant.deviationsFromSampleDesign), copyHidden, copyDisabled) ) {
             this.setDeviationsFromSampleDesign(source.deviationsFromSampleDesign); 
        } 
        
        if( copyField(tfMap.get(StudyFieldConstant.disclaimer), copyHidden, copyDisabled) ) {
             this.setDisclaimer(source.disclaimer); 
        } 

        if( copyField(tfMap.get(StudyFieldConstant.distributionDate), copyHidden, copyDisabled) ) {
             this.setDistributionDate(source.distributionDate); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.distributorContact), copyHidden, copyDisabled) ) {
             this.setDistributorContact(source.distributorContact); 
        }
        
        if( copyField(tfMap.get(StudyFieldConstant.distributorContactAffiliation), copyHidden, copyDisabled) ) {
             this.setDistributorContactAffiliation(source.distributorContactAffiliation); 
        }

        if( copyField(tfMap.get(StudyFieldConstant.distributorContactEmail), copyHidden, copyDisabled) ) {
             this.setDistributorContactEmail(source.distributorContactEmail); 
        }   
        
        if( copyField(tfMap.get(StudyFieldConstant.frequencyOfDataCollection), copyHidden, copyDisabled) ) {
             this.setFrequencyOfDataCollection(source.frequencyOfDataCollection); 
        } 

        if( copyField(tfMap.get(StudyFieldConstant.fundingAgency), copyHidden, copyDisabled) ) {
             this.setFundingAgency(source.fundingAgency); 
        }
        
        if( copyField(tfMap.get(StudyFieldConstant.geographicCoverage), copyHidden, copyDisabled) ) {
             this.setGeographicCoverage(source.geographicCoverage); 
        } 

        if( copyField(tfMap.get(StudyFieldConstant.geographicUnit), copyHidden, copyDisabled) ) {
             this.setGeographicUnit(source.geographicUnit); 
        } 
        
        if (copyField(tfMap.get(StudyFieldConstant.kindOfData), copyHidden, copyDisabled)) {
            this.setKindOfData(source.kindOfData);
        }
        
        if (copyField(tfMap.get(StudyFieldConstant.originOfSources), copyHidden, copyDisabled)) {
            this.setOriginOfSources(source.originOfSources);
        }

        if (copyField(tfMap.get(StudyFieldConstant.originalArchive), copyHidden, copyDisabled)) {
            this.setOriginalArchive(source.originalArchive);
        }

        if (copyField(tfMap.get(StudyFieldConstant.otherDataAppraisal), copyHidden, copyDisabled)) {
            this.setOtherDataAppraisal(source.otherDataAppraisal);
        }

        if (copyField(tfMap.get(StudyFieldConstant.placeOfAccess), copyHidden, copyDisabled)) {
            this.setPlaceOfAccess(source.placeOfAccess);
        }

        if (copyField(tfMap.get(StudyFieldConstant.productionDate), copyHidden, copyDisabled)) {
            this.setProductionDate(source.productionDate);
        }

        if (copyField(tfMap.get(StudyFieldConstant.productionPlace), copyHidden, copyDisabled)) {
            this.setProductionPlace(source.productionPlace);
        }

        if (copyField(tfMap.get(StudyFieldConstant.replicationFor), copyHidden, copyDisabled)) {
            this.setReplicationFor(source.replicationFor);
        }

        if (copyField(tfMap.get(StudyFieldConstant.researchInstrument), copyHidden, copyDisabled)) {
            this.setResearchInstrument(source.researchInstrument);
        }

        if (copyField(tfMap.get(StudyFieldConstant.responseRate), copyHidden, copyDisabled)) {
            this.setResponseRate(source.responseRate);
        }

        if (copyField(tfMap.get(StudyFieldConstant.restrictions), copyHidden, copyDisabled)) {
            this.setRestrictions(source.restrictions);
        }

        if (copyField(tfMap.get(StudyFieldConstant.samplingErrorEstimates), copyHidden, copyDisabled)) {
            this.setSamplingErrorEstimate(source.samplingErrorEstimate);
        }

        if (copyField(tfMap.get(StudyFieldConstant.samplingProcedure), copyHidden, copyDisabled)) {
            this.setSamplingProcedure(source.samplingProcedure);
        }

        if (copyField(tfMap.get(StudyFieldConstant.seriesInformation), copyHidden, copyDisabled)) {
            this.setSeriesInformation(source.seriesInformation);
        }

        if (copyField(tfMap.get(StudyFieldConstant.seriesName), copyHidden, copyDisabled)) {
            this.setSeriesName(source.seriesName);
        }
 
        if (copyField(tfMap.get(StudyFieldConstant.specialPermissions), copyHidden, copyDisabled)) {
            this.setSpecialPermissions(source.specialPermissions);
        }

        if (copyField(tfMap.get(StudyFieldConstant.studyVersion), copyHidden, copyDisabled)) {
            this.setStudyVersionText(source.studyVersionText);
        }

        if (copyField(tfMap.get(StudyFieldConstant.subTitle), copyHidden, copyDisabled)) {
            this.setSubTitle(source.subTitle);
        }

        if (copyField(tfMap.get(StudyFieldConstant.timeMethod), copyHidden, copyDisabled)) {
            this.setTimeMethod(source.timeMethod);
        }

        if (copyField(tfMap.get(StudyFieldConstant.timePeriodCoveredEnd), copyHidden, copyDisabled)) {
            this.setTimePeriodCoveredEnd(source.timePeriodCoveredEnd);
        }
        
        if (copyField(tfMap.get(StudyFieldConstant.timePeriodCoveredStart), copyHidden, copyDisabled)) {
            this.setTimePeriodCoveredStart(source.timePeriodCoveredStart);
        }
        
        if (copyField(tfMap.get(StudyFieldConstant.title), copyHidden, copyDisabled)) {
            this.setTitle(source.title);
        }

        if (copyField(tfMap.get(StudyFieldConstant.unitOfAnalysis), copyHidden, copyDisabled)) {
            this.setUnitOfAnalysis(source.unitOfAnalysis);
        }

        if (copyField(tfMap.get(StudyFieldConstant.universe), copyHidden, copyDisabled)) {
            this.setUniverse(source.universe);
        }

        if (copyField(tfMap.get(StudyFieldConstant.versionDate), copyHidden, copyDisabled)) {
            this.setVersionDate(source.versionDate);
        }

        if (copyField(tfMap.get(StudyFieldConstant.weighting), copyHidden, copyDisabled)) {
            this.setWeighting(source.weighting);
        }

        if (copyField(tfMap.get(StudyFieldConstant.studyLevelErrorNotes), copyHidden, copyDisabled)) {
            this.setStudyLevelErrorNotes(source.studyLevelErrorNotes);
        }

        if (copyField(tfMap.get(StudyFieldConstant.studyCompletion), copyHidden, copyDisabled)) {
            this.setStudyCompletion(source.studyCompletion);
        }            

        if (copyField(tfMap.get(StudyFieldConstant.abstractText), copyHidden, copyDisabled)) {
            this.setStudyAbstracts(new ArrayList<StudyAbstract>());
            for (StudyAbstract sa : source.studyAbstracts) {
                StudyAbstract cloneAbstract = new StudyAbstract();
                cloneAbstract.setDate(sa.getDate());
                cloneAbstract.setDisplayOrder(sa.getDisplayOrder());
                cloneAbstract.setMetadata(this);
                cloneAbstract.setText(sa.getText());
                this.getStudyAbstracts().add(cloneAbstract);
            }
        }

        if (copyField(tfMap.get(StudyFieldConstant.authorName), copyHidden, copyDisabled)) {
            this.setStudyAuthors(new ArrayList<StudyAuthor>());
            for (StudyAuthor author : source.studyAuthors) {
                StudyAuthor cloneAuthor = new StudyAuthor();
                cloneAuthor.setAffiliation(author.getAffiliation());
                cloneAuthor.setDisplayOrder(author.getDisplayOrder());
                cloneAuthor.setMetadata(this);
                cloneAuthor.setName(author.getName());
                this.getStudyAuthors().add(cloneAuthor);
            }
        }
        
        if (copyField(tfMap.get(StudyFieldConstant.distributorName), copyHidden, copyDisabled)) {
            this.setStudyDistributors(new ArrayList<StudyDistributor>());
            for (StudyDistributor dist : source.studyDistributors) {
                StudyDistributor cloneDist = new StudyDistributor();
                cloneDist.setAbbreviation(dist.getAbbreviation());
                cloneDist.setAffiliation(dist.getAffiliation());
                cloneDist.setDisplayOrder(dist.getDisplayOrder());
                cloneDist.setMetadata(this);
                cloneDist.setLogo(dist.getLogo());
                cloneDist.setName(dist.getName());
                cloneDist.setUrl(dist.getUrl());
                this.getStudyDistributors().add(cloneDist);
            }
        }

        if (copyField(tfMap.get(StudyFieldConstant.westLongitude), copyHidden, copyDisabled)) {
            this.setStudyGeoBoundings(new ArrayList<StudyGeoBounding>());
            for (StudyGeoBounding geo : source.studyGeoBoundings) {
                StudyGeoBounding cloneGeo = new StudyGeoBounding();
                cloneGeo.setDisplayOrder(geo.getDisplayOrder());
                cloneGeo.setMetadata(this);
                cloneGeo.setEastLongitude(geo.getEastLongitude());
                cloneGeo.setNorthLatitude(geo.getNorthLatitude());
                cloneGeo.setSouthLatitude(geo.getSouthLatitude());
                cloneGeo.setWestLongitude(geo.getWestLongitude());
                this.getStudyGeoBoundings().add(cloneGeo);
            }
        }

        if (copyField(tfMap.get(StudyFieldConstant.grantNumber), copyHidden, copyDisabled)) {
            this.setStudyGrants(new ArrayList<StudyGrant>());
            for (StudyGrant grant : source.studyGrants) {
                StudyGrant cloneGrant = new StudyGrant();
                cloneGrant.setAgency(grant.getAgency());
                cloneGrant.setDisplayOrder(grant.getDisplayOrder());
                cloneGrant.setMetadata(this);
                cloneGrant.setNumber(grant.getNumber());
                this.getStudyGrants().add(cloneGrant);
            }
        }

        if (copyField(tfMap.get(StudyFieldConstant.keywordValue), copyHidden, copyDisabled)) {
            this.setStudyKeywords(new ArrayList<StudyKeyword>());
            for (StudyKeyword key : source.studyKeywords) {
                StudyKeyword cloneKey = new StudyKeyword();
                cloneKey.setDisplayOrder(key.getDisplayOrder());
                cloneKey.setMetadata(this);
                cloneKey.setValue(key.getValue());
                cloneKey.setVocab(key.getVocab());
                cloneKey.setVocabURI(key.getVocabURI());
                this.getStudyKeywords().add(cloneKey);
            }
        }
        
        if (copyField(tfMap.get(StudyFieldConstant.notesInformationType), copyHidden, copyDisabled)) {
            this.setStudyNotes(new ArrayList<StudyNote>());
            for (StudyNote note : source.studyNotes) {
                StudyNote cloneNote = new StudyNote();
                cloneNote.setDisplayOrder(note.getDisplayOrder());
                cloneNote.setMetadata(this);
                cloneNote.setSubject(note.getSubject());
                cloneNote.setText(note.getText());
                cloneNote.setType(note.getType());
                this.getStudyNotes().add(cloneNote);
            }
        }

        if (copyField(tfMap.get(StudyFieldConstant.otherId), copyHidden, copyDisabled)) {
            this.setStudyOtherIds(new ArrayList<StudyOtherId>());
            for (StudyOtherId id : source.studyOtherIds) {
                StudyOtherId cloneId = new StudyOtherId();
                cloneId.setAgency(id.getAgency());
                cloneId.setDisplayOrder(id.getDisplayOrder());
                cloneId.setMetadata(this);
                cloneId.setOtherId(id.getOtherId());
                this.getStudyOtherIds().add(cloneId);
            }
        }

        if (copyField(tfMap.get(StudyFieldConstant.otherReferences), copyHidden, copyDisabled)) {
            this.setStudyOtherRefs(new ArrayList<StudyOtherRef>());
            for (StudyOtherRef ref : source.studyOtherRefs) {
                StudyOtherRef cloneRef = new StudyOtherRef();
                cloneRef.setDisplayOrder(ref.getDisplayOrder());
                cloneRef.setMetadata(this);
                cloneRef.setText(ref.getText());
                this.getStudyOtherRefs().add(cloneRef);
            }
        }

        if (copyField(tfMap.get(StudyFieldConstant.producerName), copyHidden, copyDisabled)) {
            this.setStudyProducers(new ArrayList<StudyProducer>());
            for (StudyProducer prod : source.studyProducers) {
                StudyProducer cloneProd = new StudyProducer();
                cloneProd.setAbbreviation(prod.getAbbreviation());
                cloneProd.setAffiliation(prod.getAffiliation());
                cloneProd.setDisplayOrder(prod.getDisplayOrder());
                cloneProd.setLogo(prod.getLogo());
                cloneProd.setMetadata(this);
                cloneProd.setName(prod.getName());
                cloneProd.setUrl(prod.getUrl());
                this.getStudyProducers().add(cloneProd);
            }
        }        
        
        if (copyField(tfMap.get(StudyFieldConstant.relatedMaterial), copyHidden, copyDisabled)) {
            this.setStudyRelMaterials(new ArrayList<StudyRelMaterial>());
            for (StudyRelMaterial rel : source.studyRelMaterials) {
                StudyRelMaterial cloneRel = new StudyRelMaterial();
                cloneRel.setDisplayOrder(rel.getDisplayOrder());
                cloneRel.setMetadata(this);
                cloneRel.setText(rel.getText());
                this.getStudyRelMaterials().add(cloneRel);
            }
        }

        if (copyField(tfMap.get(StudyFieldConstant.relatedPublications), copyHidden, copyDisabled)) {
            this.setStudyRelPublications(new ArrayList<StudyRelPublication>());
            for (StudyRelPublication rel : source.studyRelPublications) {
                StudyRelPublication cloneRel = new StudyRelPublication();
                cloneRel.setDisplayOrder(rel.getDisplayOrder());
                cloneRel.setMetadata(this);
                cloneRel.setText(rel.getText());
                this.getStudyRelPublications().add(cloneRel);
            }
        }

        if (copyField(tfMap.get(StudyFieldConstant.relatedStudies), copyHidden, copyDisabled)) {
            this.setStudyRelStudies(new ArrayList<StudyRelStudy>());
            for (StudyRelStudy rel : source.studyRelStudies) {
                StudyRelStudy cloneRel = new StudyRelStudy();
                cloneRel.setDisplayOrder(rel.getDisplayOrder());
                cloneRel.setMetadata(this);
                cloneRel.setText(rel.getText());
                this.getStudyRelStudies().add(cloneRel);
            }
        }
        
        if (copyField(tfMap.get(StudyFieldConstant.softwareName), copyHidden, copyDisabled)) {
            this.setStudySoftware(new ArrayList<StudySoftware>());
            for (StudySoftware soft : source.studySoftware) {
                StudySoftware cloneSoft = new StudySoftware();
                cloneSoft.setDisplayOrder(soft.getDisplayOrder());
                cloneSoft.setMetadata(this);
                cloneSoft.setName(soft.getName());
                cloneSoft.setSoftwareVersion(soft.getSoftwareVersion());
                this.getStudySoftware().add(cloneSoft);
            }
        }
        
        
        if (copyField(tfMap.get(StudyFieldConstant.topicClassValue), copyHidden, copyDisabled)) {

            this.setStudyTopicClasses(new ArrayList<StudyTopicClass>());
            for (StudyTopicClass topic : source.studyTopicClasses) {
                StudyTopicClass cloneTopic = new StudyTopicClass();
                cloneTopic.setDisplayOrder(topic.getDisplayOrder());
                cloneTopic.setMetadata(this);
                cloneTopic.setValue(topic.getValue());
                cloneTopic.setVocab(topic.getVocab());
                cloneTopic.setVocabURI(topic.getVocabURI());
                this.getStudyTopicClasses().add(cloneTopic);
            }
        }
        
        // custom values
        for (StudyField sf : source.getStudyFields()) {                     
            if( copyField(tfMap.get(sf.getName()), copyHidden, copyDisabled) ) {
                for (StudyFieldValue sfv: sf.getStudyFieldValues()){
                    StudyFieldValue cloneSfv = new StudyFieldValue();
                    cloneSfv.setDisplayOrder(sfv.getDisplayOrder());
                    cloneSfv.setStudyField(sfv.getStudyField());
                    cloneSfv.setStrValue(sfv.getStrValue());
                    cloneSfv.setMetadata(this);
                    this.getStudyFieldValues().add(cloneSfv);    
                }
            }            
        }                
    }
    
    // This constructor is for an exact clone, regarldes of field input levels
    public Metadata(Metadata source ) {
        this.setUNF(source.UNF);
        this.setAccessToSources(source.accessToSources);
        this.setActionsToMinimizeLoss(source.actionsToMinimizeLoss);
        this.setAvailabilityStatus(source.availabilityStatus);
        this.setCharacteristicOfSources(source.characteristicOfSources);
        this.setCitationRequirements(source.citationRequirements);
        this.setCleaningOperations(source.cleaningOperations);
        this.setCollectionMode(source.collectionMode);
        this.setCollectionSize(source.collectionSize);
        this.setConditions(source.conditions);
        this.setConfidentialityDeclaration(source.confidentialityDeclaration);
        this.setContact(source.contact);
        this.setControlOperations(source.controlOperations);
        this.setCountry(source.country);
        this.setDataCollectionSituation(source.dataCollectionSituation);
        this.setDataCollector(source.dataCollector);
        this.setDataSources(source.dataSources);
        this.setDateOfCollectionEnd(source.dateOfCollectionEnd);
        this.setDateOfCollectionStart(source.dateOfCollectionStart);
        this.setDateOfDeposit(source.dateOfDeposit);
        this.setDepositor(source.depositor);
        this.setDepositorRequirements(source.depositorRequirements);
        this.setDeviationsFromSampleDesign(source.deviationsFromSampleDesign);
        this.setDisclaimer(source.disclaimer);
        this.setDistributionDate(source.distributionDate);
        this.setDistributorContact(source.distributorContact);
        this.setDistributorContactAffiliation(source.distributorContactAffiliation);
        this.setDistributorContactEmail(source.distributorContactEmail);
        this.setFrequencyOfDataCollection(source.frequencyOfDataCollection);
        this.setFundingAgency(source.fundingAgency);
        this.setGeographicCoverage(source.geographicCoverage);
        this.setGeographicUnit(source.geographicUnit);
        this.setKindOfData(source.kindOfData);
        this.setOriginOfSources(source.originOfSources);
        this.setOriginalArchive(source.originalArchive);
        this.setOtherDataAppraisal(source.otherDataAppraisal);
        this.setPlaceOfAccess(source.placeOfAccess);
        this.setProductionDate(source.productionDate);
        this.setProductionPlace(source.productionPlace);
        this.setReplicationFor(source.replicationFor);
        this.setResearchInstrument(source.researchInstrument);
        this.setResponseRate(source.responseRate);
        this.setRestrictions(source.restrictions);
        this.setSamplingErrorEstimate(source.samplingErrorEstimate);
        this.setSamplingProcedure(source.samplingProcedure);
        this.setSeriesInformation(source.seriesInformation);
        this.setSeriesName(source.seriesName);
        this.setSpecialPermissions(source.specialPermissions);
        this.setStudyVersionText(source.studyVersionText);
        this.setSubTitle(source.subTitle);
        this.setTimeMethod(source.timeMethod);
        this.setTimePeriodCoveredEnd(source.timePeriodCoveredEnd);
        this.setTimePeriodCoveredStart(source.timePeriodCoveredStart);
        this.setTitle(source.title);
        this.setUnitOfAnalysis(source.unitOfAnalysis);
        this.setUniverse(source.universe);
        this.setVersionDate(source.versionDate);
        this.setWeighting(source.weighting);
        this.setStudyLevelErrorNotes(source.studyLevelErrorNotes);
        this.setStudyCompletion(source.studyCompletion);
        
        
        
     
        this.setStudyAbstracts(new ArrayList<StudyAbstract>());
        for(StudyAbstract sa: source.studyAbstracts) {
            StudyAbstract cloneAbstract = new StudyAbstract();
            cloneAbstract.setDate(sa.getDate());
            cloneAbstract.setDisplayOrder(sa.getDisplayOrder());
            cloneAbstract.setMetadata(this);
            cloneAbstract.setText(sa.getText());
            this.getStudyAbstracts().add(cloneAbstract);
        }
        this.setStudyAuthors(new ArrayList<StudyAuthor>());
        for (StudyAuthor author: source.studyAuthors) {
            StudyAuthor cloneAuthor = new StudyAuthor();
            cloneAuthor.setAffiliation(author.getAffiliation());
            cloneAuthor.setDisplayOrder(author.getDisplayOrder());
            cloneAuthor.setMetadata(this);
            cloneAuthor.setName(author.getName());
            this.getStudyAuthors().add(cloneAuthor);
        }
        this.setStudyDistributors(new ArrayList<StudyDistributor>());
        for (StudyDistributor dist: source.studyDistributors){
            StudyDistributor cloneDist = new StudyDistributor();
            cloneDist.setAbbreviation(dist.getAbbreviation());
            cloneDist.setAffiliation(dist.getAffiliation());
            cloneDist.setDisplayOrder(dist.getDisplayOrder());
            cloneDist.setMetadata(this);
            cloneDist.setLogo(dist.getLogo());
            cloneDist.setName(dist.getName());
            cloneDist.setUrl(dist.getUrl());
            this.getStudyDistributors().add(cloneDist);
        }
        this.setStudyGeoBoundings(new ArrayList<StudyGeoBounding>());
        for(StudyGeoBounding geo: source.studyGeoBoundings) {
            StudyGeoBounding cloneGeo = new StudyGeoBounding();
            cloneGeo.setDisplayOrder(geo.getDisplayOrder());
            cloneGeo.setMetadata(this);
            cloneGeo.setEastLongitude(geo.getEastLongitude());
            cloneGeo.setNorthLatitude(geo.getNorthLatitude());
            cloneGeo.setSouthLatitude(geo.getSouthLatitude());
            cloneGeo.setWestLongitude(geo.getWestLongitude());
            this.getStudyGeoBoundings().add(cloneGeo);
        }
        this.setStudyGrants(new ArrayList<StudyGrant>());
        for(StudyGrant grant: source.studyGrants) {
            StudyGrant cloneGrant = new StudyGrant();
            cloneGrant.setAgency(grant.getAgency());
            cloneGrant.setDisplayOrder(grant.getDisplayOrder());
            cloneGrant.setMetadata(this);
            cloneGrant.setNumber(grant.getNumber());
            this.getStudyGrants().add(cloneGrant);
        }
        this.setStudyKeywords(new ArrayList<StudyKeyword>());
        for(StudyKeyword key: source.studyKeywords) {
            StudyKeyword cloneKey = new StudyKeyword();
            cloneKey.setDisplayOrder(key.getDisplayOrder());
            cloneKey.setMetadata(this);
            cloneKey.setValue(key.getValue());
            cloneKey.setVocab(key.getVocab());
            cloneKey.setVocabURI(key.getVocabURI());
            this.getStudyKeywords().add(cloneKey);
        }
       this.setStudyNotes(new ArrayList<StudyNote>());
       for(StudyNote note: source.studyNotes) {
            StudyNote cloneNote = new StudyNote();
            cloneNote.setDisplayOrder(note.getDisplayOrder());
            cloneNote.setMetadata(this);
            cloneNote.setSubject(note.getSubject());
            cloneNote.setText(note.getText());
            cloneNote.setType(note.getType());
            this.getStudyNotes().add(cloneNote);
        }
        this.setStudyOtherIds(new ArrayList<StudyOtherId>());
        for(StudyOtherId id: source.studyOtherIds) {
            StudyOtherId cloneId = new StudyOtherId();
            cloneId.setAgency(id.getAgency());
            cloneId.setDisplayOrder(id.getDisplayOrder());
            cloneId.setMetadata(this);
            cloneId.setOtherId(id.getOtherId());
            this.getStudyOtherIds().add(cloneId);
        }
        this.setStudyOtherRefs(new ArrayList<StudyOtherRef>());
        for(StudyOtherRef ref: source.studyOtherRefs) {
            StudyOtherRef cloneRef = new StudyOtherRef();
            cloneRef.setDisplayOrder(ref.getDisplayOrder());
            cloneRef.setMetadata(this);
            cloneRef.setText(ref.getText());
            this.getStudyOtherRefs().add(cloneRef);
        }
        this.setStudyProducers(new ArrayList<StudyProducer>());
        for(StudyProducer prod: source.studyProducers) {
            StudyProducer cloneProd = new StudyProducer();
            cloneProd.setAbbreviation(prod.getAbbreviation());
            cloneProd.setAffiliation(prod.getAffiliation());
            cloneProd.setDisplayOrder(prod.getDisplayOrder());
            cloneProd.setLogo(prod.getLogo());
            cloneProd.setMetadata(this);
            cloneProd.setName(prod.getName());
            cloneProd.setUrl(prod.getUrl());
            this.getStudyProducers().add(cloneProd);
        }
       this.setStudyRelMaterials(new ArrayList<StudyRelMaterial>());
       for(StudyRelMaterial rel: source.studyRelMaterials) {
            StudyRelMaterial cloneRel = new StudyRelMaterial();
            cloneRel.setDisplayOrder(rel.getDisplayOrder());
            cloneRel.setMetadata(this);
            cloneRel.setText(rel.getText());
            this.getStudyRelMaterials().add(cloneRel);
        }
       this.setStudyRelPublications(new ArrayList<StudyRelPublication>());
        for(StudyRelPublication rel: source.studyRelPublications){
            StudyRelPublication cloneRel = new StudyRelPublication();
            cloneRel.setDisplayOrder(rel.getDisplayOrder());
            cloneRel.setMetadata(this);
            cloneRel.setText(rel.getText());
            this.getStudyRelPublications().add(cloneRel);
        }
        this.setStudyRelStudies(new ArrayList<StudyRelStudy>());
        for(StudyRelStudy rel: source.studyRelStudies){
            StudyRelStudy cloneRel = new StudyRelStudy();
            cloneRel.setDisplayOrder(rel.getDisplayOrder());
            cloneRel.setMetadata(this);
            cloneRel.setText(rel.getText());
            this.getStudyRelStudies().add(cloneRel);
        }
        this.setStudySoftware(new ArrayList<StudySoftware>());
        for(StudySoftware soft: source.studySoftware){
            StudySoftware cloneSoft = new StudySoftware();
            cloneSoft.setDisplayOrder(soft.getDisplayOrder());
            cloneSoft.setMetadata(this);
            cloneSoft.setName(soft.getName());
            cloneSoft.setSoftwareVersion(soft.getSoftwareVersion());
            this.getStudySoftware().add(cloneSoft);
        }
        this.setStudyTopicClasses(new ArrayList<StudyTopicClass>());
        for (StudyTopicClass topic: source.studyTopicClasses){
            StudyTopicClass cloneTopic = new StudyTopicClass();
            cloneTopic.setDisplayOrder(topic.getDisplayOrder());
            cloneTopic.setMetadata(this);
            cloneTopic.setValue(topic.getValue());
            cloneTopic.setVocab(topic.getVocab());
            cloneTopic.setVocabURI(topic.getVocabURI());
            this.getStudyTopicClasses().add(cloneTopic);
        }
        this.setStudyFieldValues(new ArrayList<StudyFieldValue>());
        for (StudyFieldValue sfv: source.getStudyFieldValues()){
            StudyFieldValue cloneSfv = new StudyFieldValue();
            cloneSfv.setDisplayOrder(sfv.getDisplayOrder());
            cloneSfv.setStudyField(sfv.getStudyField());
            cloneSfv.setStrValue(sfv.getStrValue());
            cloneSfv.setMetadata(this);
            this.getStudyFieldValues().add(cloneSfv);
        }
       
    } 
            
    public String getAuthorsStr() {
        return getAuthorsStr(true);
    }
    
    public String getAuthorsStr(boolean affiliation) {
        String str="";
        for (Iterator<StudyAuthor> it = getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor sa =  it.next();
            if (str.trim().length()>1) {
                str+="; ";
            }
            str += sa.getName();
            if (affiliation) {
                if (!StringUtil.isEmpty(sa.getAffiliation())) {
                    str+=" ("+sa.getAffiliation()+")";
                }
            }
            
        }
        return str;
        
    }
    public String getDistributorNames() {
        String str="";
        for (Iterator<StudyDistributor> it = this.getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor sd =  it.next();
            if (str.trim().length()>1) {
                str+=";";
            }
            str += sd.getName();
            
            
        }
        return str;
        
    }
   
    @Column(columnDefinition="TEXT")
    private String UNF;
  
    public String getUNF() {
        return UNF;
    }
    
    public void setUNF(String UNF) {
        this.UNF=UNF;
    }
    
    @Column(columnDefinition="TEXT")
    private String title;
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    @OneToMany (mappedBy="metadata", cascade={ CascadeType.REMOVE, CascadeType.MERGE,CascadeType.PERSIST})
    @OrderBy ("displayOrder")
    private List<StudyFieldValue> studyFieldValues;

    public List<StudyFieldValue> getStudyFieldValues() {
        return studyFieldValues;
    }

    public void setStudyFieldValues(List<StudyFieldValue> studyFieldValues) {
        this.studyFieldValues = studyFieldValues;
    }

    
 
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private java.util.List<StudyAuthor> studyAuthors;
 
    public java.util.List<StudyAuthor> getStudyAuthors() {
        return studyAuthors;
    }
    
    public void setStudyAuthors(java.util.List<StudyAuthor> studyAuthors) {
        this.studyAuthors = studyAuthors;
    }
    

    
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private java.util.List<StudyKeyword> studyKeywords;
    
    public java.util.List<StudyKeyword> getStudyKeywords() {
        return studyKeywords;
    }
    
    public void setStudyKeywords(java.util.List<StudyKeyword> studyKeywords) {
        this.studyKeywords = studyKeywords;
    }
    

    
    
    
    
   
   
    
    /**
     * Holds value of property version.
     */
    @Version
    private Long version;
    
    /**
     * Getter for property version.
     * @return Value of property version.
     */
    public Long getVersion() {
        return this.version;
    }
    
    /**
     * Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(Long version) {
        this.version = version;
    }
 
    
    /**
     * Holds value of property studyProducers.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyProducer> studyProducers;
    
    /**
     * Getter for property studyProducers.
     * @return Value of property studyProducers.
     */
    public List<StudyProducer> getStudyProducers() {
        return this.studyProducers;
    }
    
    /**
     * Setter for property studyProducers.
     * @param studyProducers New value of property studyProducers.
     */
    public void setStudyProducers(List<StudyProducer> studyProducers) {
        this.studyProducers = studyProducers;
    }
    
    /**
     * Holds value of property studySoftware.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudySoftware> studySoftware;
    
    /**
     * Getter for property studySoftware.
     * @return Value of property studySoftware.
     */
    public List<StudySoftware> getStudySoftware() {
        return this.studySoftware;
    }
    
    /**
     * Setter for property studySoftware.
     * @param studySoftware New value of property studySoftware.
     */
    public void setStudySoftware(List<StudySoftware> studySoftware) {
        this.studySoftware = studySoftware;
    }
    
    /**
     * Holds value of property studyDistributors.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyDistributor> studyDistributors;
    
    /**
     * Getter for property studyDistributors.
     * @return Value of property studyDistributors.
     */
    public List<StudyDistributor> getStudyDistributors() {
        return this.studyDistributors;
    }
    
    /**
     * Setter for property studyDistributors.
     * @param studyDistributors New value of property studyDistributors.
     */
    public void setStudyDistributors(List<StudyDistributor> studyDistributors) {
        this.studyDistributors = studyDistributors;
    }
    
    /**
     * Holds value of property studyTopicClasses.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyTopicClass> studyTopicClasses;
    
    /**
     * Getter for property studyTopicClasses.
     * @return Value of property studyTopicClasses.
     */
    public List<StudyTopicClass> getStudyTopicClasses() {
        return this.studyTopicClasses;
    }
    
    /**
     * Setter for property studyTopicClasses.
     * @param studyTopicClasses New value of property studyTopicClasses.
     */
    public void setStudyTopicClasses(List<StudyTopicClass> studyTopicClasses) {
        this.studyTopicClasses = studyTopicClasses;
    }
    
    /**
     * Holds value of property studyAbstracts.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyAbstract> studyAbstracts;
    
    /**
     * Getter for property studyAbstracts.
     * @return Value of property studyAbstracts.
     */
    public List<StudyAbstract> getStudyAbstracts() {
        return this.studyAbstracts;
    }
    
    /**
     * Setter for property studyAbstracts.
     * @param studyAbstracts New value of property studyAbstracts.
     */
    public void setStudyAbstracts(List<StudyAbstract> studyAbstracts) {
        this.studyAbstracts = studyAbstracts;
    }
    
    /**
     * Holds value of property studyNotes.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyNote> studyNotes;
    
    /**
     * Getter for property studyNotes.
     * @return Value of property studyNotes.
     */
    public List<StudyNote> getStudyNotes() {
        return this.studyNotes;
    }
    
    /**
     * Setter for property studyNotes.
     * @param studyNotes New value of property studyNotes.
     */
    public void setStudyNotes(List<StudyNote> studyNotes) {
        this.studyNotes = studyNotes;
    }
    
    /**
     * Holds value of property fundingAgency.
     */
    @Column(columnDefinition="TEXT")    
    private String fundingAgency;
    
    /**
     * Getter for property fundingAgency.
     * @return Value of property fundingAgency.
     */
    public String getFundingAgency() {
        return this.fundingAgency;
    }
    
    /**
     * Setter for property fundingAgency.
     * @param fundingAgency New value of property fundingAgency.
     */
    public void setFundingAgency(String fundingAgency) {
        this.fundingAgency = fundingAgency;
    }
    
    /**
     * Holds value of property seriesName.
     */
    @Column(columnDefinition="TEXT")   
    private String seriesName;
    
    /**
     * Getter for property seriesName.
     * @return Value of property seriesName.
     */
    public String getSeriesName() {
        return this.seriesName;
    }
    
    /**
     * Setter for property seriesName.
     * @param seriesName New value of property seriesName.
     */
    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
    
    /**
     * Holds value of property seriesInformation.
     */
    @Column(columnDefinition="TEXT")
    private String seriesInformation;
    
    /**
     * Getter for property seriesInformation.
     * @return Value of property seriesInformation.
     */
    public String getSeriesInformation() {
        return this.seriesInformation;
    }
    
    /**
     * Setter for property seriesInformation.
     * @param seriesInformation New value of property seriesInformation.
     */
    public void setSeriesInformation(String seriesInformation) {
        this.seriesInformation = seriesInformation;
    }
    
    /**
     * Holds value of property studyVersion.
     */
    @Column(name="studyVersion",columnDefinition="TEXT")
    private String studyVersionText;
    
    /**
     * Getter for property studyVersion.
     * @return Value of property studyVersion.
     */
    public String getStudyVersionText() {
        return this.studyVersionText;
    }
    
    /**
     * Setter for property studyVersion.
     * @param studyVersion New value of property studyVersion.
     */
    public void setStudyVersionText(String studyVersionText) {
        this.studyVersionText = studyVersionText;
    }
    
    /**
     * Holds value of property timePeriodCoveredStart.
     */
    @Column(columnDefinition="TEXT")
    private String timePeriodCoveredStart;
    
    /**
     * Getter for property timePeriodCoveredStart.
     * @return Value of property timePeriodCoveredStart.
     */
    public String getTimePeriodCoveredStart() {
        return this.timePeriodCoveredStart;
    }
    
    /**
     * Setter for property timePeriodCoveredStart.
     * @param timePeriodCoveredStart New value of property timePeriodCoveredStart.
     */
    public void setTimePeriodCoveredStart(String timePeriodCoveredStart) {
        this.timePeriodCoveredStart = timePeriodCoveredStart;
    }
    
    /**
     * Holds value of property timePeriodCoveredEnd.
     */
    @Column(columnDefinition="TEXT")
    private String timePeriodCoveredEnd;
    
    /**
     * Getter for property timePeriodCoveredEnd.
     * @return Value of property timePeriodCoveredEnd.
     */
    public String getTimePeriodCoveredEnd() {
        return this.timePeriodCoveredEnd;
    }
    
    /**
     * Setter for property timePeriodCoveredEnd.
     * @param timePeriodCoveredEnd New value of property timePeriodCoveredEnd.
     */
    public void setTimePeriodCoveredEnd(String timePeriodCoveredEnd) {
        this.timePeriodCoveredEnd = timePeriodCoveredEnd;
    }
    
    /**
     * Holds value of property dateOfCollectionStart.
     */
    @Column(columnDefinition="TEXT")
    private String dateOfCollectionStart;
    
    /**
     * Getter for property dateOfCollectionStart.
     * @return Value of property dateOfCollectionStart.
     */
    public String getDateOfCollectionStart() {
        return this.dateOfCollectionStart;
    }
    
    /**
     * Setter for property dateOfCollectionStart.
     * @param dateOfCollectionStart New value of property dateOfCollectionStart.
     */
    public void setDateOfCollectionStart(String dateOfCollectionStart) {
        this.dateOfCollectionStart = dateOfCollectionStart;
    }
    
    /**
     * Holds value of property dateOfCollectionEnd.
     */
    @Column(columnDefinition="TEXT")
    private String dateOfCollectionEnd;
    
    /**
     * Getter for property dateOfCollectionEnd.
     * @return Value of property dateOfCollectionEnd.
     */
    public String getDateOfCollectionEnd() {
        return this.dateOfCollectionEnd;
    }
    
    /**
     * Setter for property dateOfCollectionEnd.
     * @param dateOfCollectionEnd New value of property dateOfCollectionEnd.
     */
    public void setDateOfCollectionEnd(String dateOfCollectionEnd) {
        this.dateOfCollectionEnd = dateOfCollectionEnd;
    }
    
    /**
     * Holds value of property country.
     */
    @Column(columnDefinition="TEXT")
    private String country;
    
    /**
     * Getter for property country.
     * @return Value of property country.
     */
    public String getCountry() {
        return this.country;
    }
    
    /**
     * Setter for property country.
     * @param country New value of property country.
     */
    public void setCountry(String country) {
        this.country = country;
    }
    
    /**
     * Holds value of property geographicCoverage.
     */
    @Column(columnDefinition="TEXT")
    private String geographicCoverage;
    
    /**
     * Getter for property geographicCoverage.
     * @return Value of property geographicCoverage.
     */
    public String getGeographicCoverage() {
        return this.geographicCoverage;
    }
    
    /**
     * Setter for property geographicCoverage.
     * @param geographicCoverage New value of property geographicCoverage.
     */
    public void setGeographicCoverage(String geographicCoverage) {
        this.geographicCoverage = geographicCoverage;
    }
    
    /**
     * Holds value of property geographicUnit.
     */
    @Column(columnDefinition="TEXT")
    private String geographicUnit;
    
    /**
     * Getter for property geographicUnit.
     * @return Value of property geographicUnit.
     */
    public String getGeographicUnit() {
        return this.geographicUnit;
    }
    
    /**
     * Setter for property geographicUnit.
     * @param geographicUnit New value of property geographicUnit.
     */
    public void setGeographicUnit(String geographicUnit) {
        this.geographicUnit = geographicUnit;
    }
    
    /**
     * Holds value of property unitOfAnalysis.
     */
    @Column(columnDefinition="TEXT")
    private String unitOfAnalysis;
    
    /**
     * Getter for property unitOfAnalysis.
     * @return Value of property unitOfAnalysis.
     */
    public String getUnitOfAnalysis() {
        return this.unitOfAnalysis;
    }
    
    /**
     * Setter for property unitOfAnalysis.
     * @param unitOfAnalysis New value of property unitOfAnalysis.
     */
    public void setUnitOfAnalysis(String unitOfAnalysis) {
        this.unitOfAnalysis = unitOfAnalysis;
    }
    
    /**
     * Holds value of property universe.
     */
    @Column(columnDefinition="TEXT")
    private String universe;
    
    /**
     * Getter for property universe.
     * @return Value of property universe.
     */
    public String getUniverse() {
        return this.universe;
    }
    
    /**
     * Setter for property universe.
     * @param universe New value of property universe.
     */
    public void setUniverse(String universe) {
        this.universe = universe;
    }
    
    /**
     * Holds value of property kindOfData.
     */
    @Column(columnDefinition="TEXT")
    private String kindOfData;
    
    /**
     * Getter for property kindOfData.
     * @return Value of property kindOfData.
     */
    public String getKindOfData() {
        return this.kindOfData;
    }
    
    /**
     * Setter for property kindOfData.
     * @param kindOfData New value of property kindOfData.
     */
    public void setKindOfData(String kindOfData) {
        this.kindOfData = kindOfData;
    }
    
    /**
     * Holds value of property timeMethod.
     */
    @Column(columnDefinition="TEXT")
    private String timeMethod;
    
    /**
     * Getter for property timeMethod.
     * @return Value of property timeMethod.
     */
    public String getTimeMethod() {
        return this.timeMethod;
    }
    
    /**
     * Setter for property timeMethod.
     * @param timeMethod New value of property timeMethod.
     */
    public void setTimeMethod(String timeMethod) {
        this.timeMethod = timeMethod;
    }
    
    /**
     * Holds value of property dataCollector.
     */
    @Column(columnDefinition="TEXT")
    private String dataCollector;
    
    /**
     * Getter for property dataCollector.
     * @return Value of property dataCollector.
     */
    public String getDataCollector() {
        return this.dataCollector;
    }
    
    /**
     * Setter for property dataCollector.
     * @param dataCollector New value of property dataCollector.
     */
    public void setDataCollector(String dataCollector) {
        this.dataCollector = dataCollector;
    }
    
    /**
     * Holds value of property frequencyOfDataCollection.
     */
    @Column(columnDefinition="TEXT")
    private String frequencyOfDataCollection;
    
    /**
     * Getter for property frequencyOfDataCollection.
     * @return Value of property frequencyOfDataCollection.
     */
    public String getFrequencyOfDataCollection() {
        return this.frequencyOfDataCollection;
    }
    
    /**
     * Setter for property frequencyOfDataCollection.
     * @param frequencyOfDataCollection New value of property frequencyOfDataCollection.
     */
    public void setFrequencyOfDataCollection(String frequencyOfDataCollection) {
        this.frequencyOfDataCollection = frequencyOfDataCollection;
    }
    
    /**
     * Holds value of property samplingProcedure.
     */
    @Column(columnDefinition="TEXT")
    private String samplingProcedure;
    
    /**
     * Getter for property samplingProdedure.
     * @return Value of property samplingProdedure.
     */
    public String getSamplingProcedure() {
        return this.samplingProcedure;
    }
    
    /**
     * Setter for property samplingProdedure.
     * @param samplingProdedure New value of property samplingProdedure.
     */
    public void setSamplingProcedure(String samplingProcedure) {
        this.samplingProcedure = samplingProcedure;
    }
    
    /**
     * Holds value of property deviationsFromSampleDesign.
     */
    @Column(columnDefinition="TEXT")
    private String deviationsFromSampleDesign;
    
    /**
     * Getter for property deviationsFromSampleDesign.
     * @return Value of property deviationsFromSampleDesign.
     */
    public String getDeviationsFromSampleDesign() {
        return this.deviationsFromSampleDesign;
    }
    
    /**
     * Setter for property deviationsFromSampleDesign.
     * @param deviationsFromSampleDesign New value of property deviationsFromSampleDesign.
     */
    public void setDeviationsFromSampleDesign(String deviationsFromSampleDesign) {
        this.deviationsFromSampleDesign = deviationsFromSampleDesign;
    }
    
    /**
     * Holds value of property collectionMode.
     */
    @Column(columnDefinition="TEXT")
    private String collectionMode;
    
    /**
     * Getter for property collectionMode.
     * @return Value of property collectionMode.
     */
    public String getCollectionMode() {
        return this.collectionMode;
    }
    
    /**
     * Setter for property collectionMode.
     * @param collectionMode New value of property collectionMode.
     */
    public void setCollectionMode(String collectionMode) {
        this.collectionMode = collectionMode;
    }
    
    /**
     * Holds value of property researchInstrument.
     */
    @Column(columnDefinition="TEXT")
    private String researchInstrument;
    
    /**
     * Getter for property researchInstrument.
     * @return Value of property researchInstrument.
     */
    public String getResearchInstrument() {
        return this.researchInstrument;
    }
    
    /**
     * Setter for property researchInstrument.
     * @param researchInstrument New value of property researchInstrument.
     */
    public void setResearchInstrument(String researchInstrument) {
        this.researchInstrument = researchInstrument;
    }
    
    /**
     * Holds value of property dataSources.
     */
    @Column(columnDefinition="TEXT")
    private String dataSources;
    
    /**
     * Getter for property dataSources.
     * @return Value of property dataSources.
     */
    public String getDataSources() {
        return this.dataSources;
    }
    
    /**
     * Setter for property dataSources.
     * @param dataSources New value of property dataSources.
     */
    public void setDataSources(String dataSources) {
        this.dataSources = dataSources;
    }
    
    /**
     * Holds value of property originOfSources.
     */
    @Column(columnDefinition="TEXT")
    private String originOfSources;
    
    /**
     * Getter for property originOfSources.
     * @return Value of property originOfSources.
     */
    public String getOriginOfSources() {
        return this.originOfSources;
    }
    
    /**
     * Setter for property originOfSources.
     * @param originOfSources New value of property originOfSources.
     */
    public void setOriginOfSources(String originOfSources) {
        this.originOfSources = originOfSources;
    }
    
    /**
     * Holds value of property characteristicOfSources.
     */
    @Column(columnDefinition="TEXT")
    private String characteristicOfSources;
    
    /**
     * Getter for property characteristicOfSources.
     * @return Value of property characteristicOfSources.
     */
    public String getCharacteristicOfSources() {
        return this.characteristicOfSources;
    }
    
    /**
     * Setter for property characteristicOfSources.
     * @param characteristicOfSources New value of property characteristicOfSources.
     */
    public void setCharacteristicOfSources(String characteristicOfSources) {
        this.characteristicOfSources = characteristicOfSources;
    }
    
    /**
     * Holds value of property accessToSources.
     */
    @Column(columnDefinition="TEXT")
    private String accessToSources;
    
    /**
     * Getter for property accessToSources.
     * @return Value of property accessToSources.
     */
    public String getAccessToSources() {
        return this.accessToSources;
    }
    
    /**
     * Setter for property accessToSources.
     * @param accessToSources New value of property accessToSources.
     */
    public void setAccessToSources(String accessToSources) {
        this.accessToSources = accessToSources;
    }
    
    /**
     * Holds value of property dataCollectionSituation.
     */
    @Column(columnDefinition="TEXT")
    private String dataCollectionSituation;
    
    /**
     * Getter for property dataCollectionSituation.
     * @return Value of property dataCollectionSituation.
     */
    public String getDataCollectionSituation() {
        return this.dataCollectionSituation;
    }
    
    /**
     * Setter for property dataCollectionSituation.
     * @param dataCollectionSituation New value of property dataCollectionSituation.
     */
    public void setDataCollectionSituation(String dataCollectionSituation) {
        this.dataCollectionSituation = dataCollectionSituation;
    }
    
    /**
     * Holds value of property actionsToMinimizeLoss.
     */
    @Column(columnDefinition="TEXT")
    private String actionsToMinimizeLoss;
    
    /**
     * Getter for property actionsToMinimizeLoss.
     * @return Value of property actionsToMinimizeLoss.
     */
    public String getActionsToMinimizeLoss() {
        return this.actionsToMinimizeLoss;
    }
    
    /**
     * Setter for property actionsToMinimizeLoss.
     * @param actionsToMinimizeLoss New value of property actionsToMinimizeLoss.
     */
    public void setActionsToMinimizeLoss(String actionsToMinimizeLoss) {
        this.actionsToMinimizeLoss = actionsToMinimizeLoss;
    }
    
    /**
     * Holds value of property controlOperations.
     */
    @Column(columnDefinition="TEXT")
    private String controlOperations;
    
    
    /**
     * Getter for property controlOperations.
     * @return Value of property controlOperations.
     */
    public String getControlOperations() {
        return this.controlOperations;
    }
    
    /**
     * Setter for property controlOperations.
     * @param controlOperations New value of property controlOperations.
     */
    public void setControlOperations(String controlOperations) {
        this.controlOperations = controlOperations;
    }
    
    /**
     * Holds value of property weighting.
     */
    @Column(columnDefinition="TEXT")
    private String weighting;
    
    /**
     * Getter for property weighting.
     * @return Value of property weighting.
     */
    public String getWeighting() {
        return this.weighting;
    }
    
    /**
     * Setter for property weighting.
     * @param weighting New value of property weighting.
     */
    public void setWeighting(String weighting) {
        this.weighting = weighting;
    }
    
    /**
     * Holds value of property cleaningOperations.
     */
    @Column(columnDefinition="TEXT")
    private String cleaningOperations;
    
    /**
     * Getter for property cleaningOperations.
     * @return Value of property cleaningOperations.
     */
    public String getCleaningOperations() {
        return this.cleaningOperations;
    }
    
    /**
     * Setter for property cleaningOperations.
     * @param cleaningOperations New value of property cleaningOperations.
     */
    public void setCleaningOperations(String cleaningOperations) {
        this.cleaningOperations = cleaningOperations;
    }
    
    /**
     * Holds value of property studyLevelErrorNotes.
     */
    @Column(columnDefinition="TEXT")
    private String studyLevelErrorNotes;
    
    /**
     * Getter for property studyLevelErrorNotes.
     * @return Value of property studyLevelErrorNotes.
     */
    public String getStudyLevelErrorNotes() {
        return this.studyLevelErrorNotes;
    }
    
    /**
     * Setter for property studyLevelErrorNotes.
     * @param studyLevelErrorNotes New value of property studyLevelErrorNotes.
     */
    public void setStudyLevelErrorNotes(String studyLevelErrorNotes) {
        this.studyLevelErrorNotes = studyLevelErrorNotes;
    }
    
    /**
     * Holds value of property responseRate.
     */
    @Column(columnDefinition="TEXT")
    private String responseRate;
    
    /**
     * Getter for property responseRate.
     * @return Value of property responseRate.
     */
    public String getResponseRate() {
        return this.responseRate;
    }
    
    /**
     * Setter for property responseRate.
     * @param responseRate New value of property responseRate.
     */
    public void setResponseRate(String responseRate) {
        this.responseRate = responseRate;
    }
    
    /**
     * Holds value of property samplingErrorEstimate.
     */
    @Column(columnDefinition="TEXT")
    private String samplingErrorEstimate;
    
    /**
     * Getter for property samplingErrorEstimate.
     * @return Value of property samplingErrorEstimate.
     */
    public String getSamplingErrorEstimate() {
        return this.samplingErrorEstimate;
    }
    
    /**
     * Setter for property samplingErrorEstimate.
     * @param samplingErrorEstimate New value of property samplingErrorEstimate.
     */
    public void setSamplingErrorEstimate(String samplingErrorEstimate) {
        this.samplingErrorEstimate = samplingErrorEstimate;
    }
    
    /**
     * Holds value of property otherDataAppraisal.
     */
    @Column(columnDefinition="TEXT")
    private String otherDataAppraisal;
    
    /**
     * Getter for property dataAppraisal.
     * @return Value of property dataAppraisal.
     */
    public String getOtherDataAppraisal() {
        return this.otherDataAppraisal;
    }
    
    /**
     * Setter for property dataAppraisal.
     * @param dataAppraisal New value of property dataAppraisal.
     */
    public void setOtherDataAppraisal(String otherDataAppraisal) {
        this.otherDataAppraisal = otherDataAppraisal;
    }
    
    /**
     * Holds value of property placeOfAccess.
     */
    @Column(columnDefinition="TEXT")
    private String placeOfAccess;
    
    /**
     * Getter for property placeOfAccess.
     * @return Value of property placeOfAccess.
     */
    public String getPlaceOfAccess() {
        return this.placeOfAccess;
    }
    
    /**
     * Setter for property placeOfAccess.
     * @param placeOfAccess New value of property placeOfAccess.
     */
    public void setPlaceOfAccess(String placeOfAccess) {
        this.placeOfAccess = placeOfAccess;
    }
    
    /**
     * Holds value of property originalArchive.
     */
    @Column(columnDefinition="TEXT")
    private String originalArchive;
    
    /**
     * Getter for property originalArchive.
     * @return Value of property originalArchive.
     */
    public String getOriginalArchive() {
        return this.originalArchive;
    }
    
    /**
     * Setter for property originalArchive.
     * @param originalArchive New value of property originalArchive.
     */
    public void setOriginalArchive(String originalArchive) {
        this.originalArchive = originalArchive;
    }
    
    /**
     * Holds value of property availabilityStatus.
     */
    @Column(columnDefinition="TEXT")
    private String availabilityStatus;
    
    /**
     * Getter for property availabilityStatus.
     * @return Value of property availabilityStatus.
     */
    public String getAvailabilityStatus() {
        return this.availabilityStatus;
    }
    
    /**
     * Setter for property availabilityStatus.
     * @param availabilityStatus New value of property availabilityStatus.
     */
    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
    
    /**
     * Holds value of property collectionSize.
     */
    @Column(columnDefinition="TEXT")
    private String collectionSize;
    
    /**
     * Getter for property collectionSize.
     * @return Value of property collectionSize.
     */
    public String getCollectionSize() {
        return this.collectionSize;
    }
    
    /**
     * Setter for property collectionSize.
     * @param collectionSize New value of property collectionSize.
     */
    public void setCollectionSize(String collectionSize) {
        this.collectionSize = collectionSize;
    }
    
    /**
     * Holds value of property studyCompletion.
     */
    @Column(columnDefinition="TEXT")
    private String studyCompletion;
    
    /**
     * Getter for property studyCompletion.
     * @return Value of property studyCompletion.
     */
    public String getStudyCompletion() {
        return this.studyCompletion;
    }
    
    /**
     * Setter for property studyCompletion.
     * @param studyCompletion New value of property studyCompletion.
     */
    public void setStudyCompletion(String studyCompletion) {
        this.studyCompletion = studyCompletion;
    }
    
   /**
     * Holds value of property restrictions.
     */
    @Column(columnDefinition="TEXT")
    private String specialPermissions;
    
    /**
     * Getter for property specialPermissions.
     * @return Value of property specialPermissions.
     */
    public String getSpecialPermissions() {
        return this.specialPermissions;
    }
    
    /**
     * Setter for property specialPermissions.
     * @param specialPermissions New value of property specialPermissions.
     */
    public void setSpecialPermissions(String specialPermissions) {
        this.specialPermissions = specialPermissions;
    }
    
    /**
     * Holds value of property restrictions.
     */
    @Column(columnDefinition="TEXT")
    private String restrictions;
    
    /**
     * Getter for property restrictions.
     * @return Value of property restrictions.
     */
    public String getRestrictions() {
        return this.restrictions;
    }
    
    /**
     * Setter for property restrictions.
     * @param restrictions New value of property restrictions.
     */
    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }
    
    /**
     * Holds value of property contact.
     */
    @Column(columnDefinition="TEXT")
    private String contact;
    
    /**
     * Getter for property contact.
     * @return Value of property contact.
     */
    public String getContact() {
        return this.contact;
    }
    
    /**
     * Setter for property contact.
     * @param contact New value of property contact.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    /**
     * Holds value of property citationRequirements.
     */
    @Column(columnDefinition="TEXT")
    private String citationRequirements;
    
    /**
     * Getter for property citationRequirements.
     * @return Value of property citationRequirements.
     */
    public String getCitationRequirements() {
        return this.citationRequirements;
    }
    
    /**
     * Setter for property citationRequirements.
     * @param citationRequirements New value of property citationRequirements.
     */
    public void setCitationRequirements(String citationRequirements) {
        this.citationRequirements = citationRequirements;
    }
    
    /**
     * Holds value of property depositorRequirements.
     */
    @Column(columnDefinition="TEXT")
    private String depositorRequirements;
    
    /**
     * Getter for property dipositorRequirements.
     * @return Value of property dipositorRequirements.
     */
    public String getDepositorRequirements() {
        return this.depositorRequirements;
    }
    
    /**
     * Setter for property dipositorRequirements.
     * @param dipositorRequirements New value of property dipositorRequirements.
     */
    public void setDepositorRequirements(String depositorRequirements) {
        this.depositorRequirements = depositorRequirements;
    }
    
    /**
     * Holds value of property conditions.
     */
    @Column(columnDefinition="TEXT")
    private String conditions;
    
    /**
     * Getter for property conditions.
     * @return Value of property conditions.
     */
    public String getConditions() {
        return this.conditions;
    }
    
    /**
     * Setter for property conditions.
     * @param conditions New value of property conditions.
     */
    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
    
    /**
     * Holds value of property disclaimer.
     */
    @Column(columnDefinition="TEXT")
    private String disclaimer;
    
    /**
     * Getter for property disclaimer.
     * @return Value of property disclaimer.
     */
    public String getDisclaimer() {
        return this.disclaimer;
    }
    
    /**
     * Setter for property disclaimer.
     * @param disclaimer New value of property disclaimer.
     */
    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
    
    /**
     * Holds value of property productionDate.
     */
    @Column(columnDefinition="TEXT")
    private String productionDate;
    
    /**
     * Getter for property productionDate.
     * @return Value of property productionDate.
     */
    public String getProductionDate() {
        return this.productionDate;
    }
    
    /**
     * Setter for property productionDate.
     * @param productionDate New value of property productionDate.
     */
    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }
    
    /**
     * Holds value of property productionPlace.
     */
    @Column(columnDefinition="TEXT")
    private String productionPlace;
    
    /**
     * Getter for property productionPlace.
     * @return Value of property productionPlace.
     */
    public String getProductionPlace() {
        return this.productionPlace;
    }
    
    /**
     * Setter for property productionPlace.
     * @param productionPlace New value of property productionPlace.
     */
    public void setProductionPlace(String productionPlace) {
        this.productionPlace = productionPlace;
    }
    
    /**
     * Holds value of property confidentialityDeclaration.
     */
    @Column(columnDefinition="TEXT")
    private String confidentialityDeclaration;
    
    /**
     * Getter for property confidentialityDeclaration.
     * @return Value of property confidentialityDeclaration.
     */
    public String getConfidentialityDeclaration() {
        return this.confidentialityDeclaration;
    }
    
    /**
     * Setter for property confidentialityDeclaration.
     * @param confidentialityDeclaration New value of property confidentialityDeclaration.
     */
    public void setConfidentialityDeclaration(String confidentialityDeclaration) {
        this.confidentialityDeclaration = confidentialityDeclaration;
    }
    
    /**
     * Holds value of property studyGrants.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyGrant> studyGrants;
    
    /**
     * Getter for property studyGrants.
     * @return Value of property studyGrants.
     */
    public List<StudyGrant> getStudyGrants() {
        return this.studyGrants;
    }
    
    /**
     * Setter for property studyGrants.
     * @param studyGrants New value of property studyGrants.
     */
    public void setStudyGrants(List<StudyGrant> studyGrants) {
        this.studyGrants = studyGrants;
    }
    
    
    /**
     * Holds value of property distributionDate.
     */
    @Column(columnDefinition="TEXT")
    private String distributionDate;
    
    /**
     * Getter for property distributionDate.
     * @return Value of property distributionDate.
     */
    public String getDistributionDate() {
        return this.distributionDate;
    }
    
    /**
     * Setter for property distributionDate.
     * @param distributionDate New value of property distributionDate.
     */
    public void setDistributionDate(String distributionDate) {
        this.distributionDate = distributionDate;
    }
    
    /**
     * Holds value of property distributorContact.
     */
    @Column(columnDefinition="TEXT")
    private String distributorContact;
    
    /**
     * Getter for property distributorContact.
     * @return Value of property distributorContact.
     */
    public String getDistributorContact() {
        return this.distributorContact;
    }
    
    /**
     * Setter for property distributorContact.
     * @param distributorContact New value of property distributorContact.
     */
    public void setDistributorContact(String distributorContact) {
        this.distributorContact = distributorContact;
    }
    
    /**
     * Holds value of property distributorContactAffiliation.
     */
    @Column(columnDefinition="TEXT")
    private String distributorContactAffiliation;
    
    /**
     * Getter for property distributorContactAffiliation.
     * @return Value of property distributorContactAffiliation.
     */
    public String getDistributorContactAffiliation() {
        return this.distributorContactAffiliation;
    }
    
    /**
     * Setter for property distributorContactAffiliation.
     * @param distributorContactAffiliation New value of property distributorContactAffiliation.
     */
    public void setDistributorContactAffiliation(String distributorContactAffiliation) {
        this.distributorContactAffiliation = distributorContactAffiliation;
    }
    
    /**
     * Holds value of property distributorContactEmail.
     */
    @Column(columnDefinition="TEXT")
    private String distributorContactEmail;
    
    /**
     * Getter for property distributorContactEmail.
     * @return Value of property distributorContactEmail.
     */
    public String getDistributorContactEmail() {
        return this.distributorContactEmail;
    }
    
    /**
     * Setter for property distributorContactEmail.
     * @param distributorContactEmail New value of property distributorContactEmail.
     */
    public void setDistributorContactEmail(String distributorContactEmail) {
        this.distributorContactEmail = distributorContactEmail;
    }
    
    /**
     * Holds value of property depositor.
     */
    @Column(columnDefinition="TEXT")
    private String depositor;
    
    /**
     * Getter for property depositor.
     * @return Value of property depositor.
     */
    public String getDepositor() {
        return this.depositor;
    }
    
    /**
     * Setter for property depositor.
     * @param depositor New value of property depositor.
     */
    public void setDepositor(String depositor) {
        this.depositor = depositor;
    }
    
    /**
     * Holds value of property dateOfDeposit.
     */
    @Column(columnDefinition="TEXT")
    private String dateOfDeposit;
    
    /**
     * Getter for property dateOfDeposit.
     * @return Value of property dateOfDeposit.
     */
    public String getDateOfDeposit() {
        return this.dateOfDeposit;
    }
    
    /**
     * Setter for property dateOfDeposit.
     * @param dateOfDeposit New value of property dateOfDeposit.
     */
    public void setDateOfDeposit(String dateOfDeposit) {
        this.dateOfDeposit = dateOfDeposit;
    }
    
    /**
     * Holds value of property studyOtherIds.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyOtherId> studyOtherIds;
    
    /**
     * Getter for property studyOtherIds.
     * @return Value of property studyOtherIds.
     */
    public List<StudyOtherId> getStudyOtherIds() {
        return this.studyOtherIds;
    }
    
    /**
     * Setter for property studyOtherIds.
     * @param studyOtherIds New value of property studyOtherIds.
     */
    public void setStudyOtherIds(List<StudyOtherId> studyOtherIds) {
        this.studyOtherIds = studyOtherIds;
    }
    
  
    
    
    /**
     * Holds value of property versionDate.
     */
    @Column(columnDefinition="TEXT")
    private String versionDate;
    
    /**
     * Getter for property versionDate.
     * @return Value of property versionDate.
     */
    public String getVersionDate() {
        return this.versionDate;
    }
    
    /**
     * Setter for property versionDate.
     * @param versionDate New value of property versionDate.
     */
    public void setVersionDate(String versionDate) {
        this.versionDate = versionDate;
    }
    
 
    
    /**
     * Holds value of property replicationFor.
     */
    @Column(columnDefinition="TEXT")
    private String replicationFor;
    
    /**
     * Getter for property replicationFor.
     * @return Value of property replicationFor.
     */
    
    public String getReplicationFor() {
        return this.replicationFor;
    }
    
    /**
     * Setter for property replicationFor.
     * @param replicationFor New value of property replicationFor.
     */
    public void setReplicationFor(String replicationFor) {
        this.replicationFor = replicationFor;
    }
    
    /**
     * Holds value of property subTitle.
     */
    @Column(columnDefinition="TEXT")
    private String subTitle;
    
    /**
     * Getter for property subTitle.
     * @return Value of property subTitle.
     */
    public String getSubTitle() {
        return this.subTitle;
    }
    
    /**
     * Setter for property subTitle.
     * @param subTitle New value of property subTitle.
     */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
    
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private java.util.List<StudyGeoBounding> studyGeoBoundings;
    
    public java.util.List<StudyGeoBounding> getStudyGeoBoundings() {
        return studyGeoBoundings;
    }
    
    public void setStudyGeoBoundings(java.util.List<StudyGeoBounding> studyGeoBoundings) {
        this.studyGeoBoundings = studyGeoBoundings;
    }
    
  
    /**
     * Holds value of property studyOtherRefs.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyOtherRef> studyOtherRefs;
    
    /**
     * Getter for property studyOtherRefs.
     * @return Value of property studyOtherRefs.
     */
    public List<StudyOtherRef> getStudyOtherRefs() {
        return this.studyOtherRefs;
    }
    
    /**
     * Setter for property studyOtherRefs.
     * @param studyOtherRefs New value of property studyOtherRefs.
     */
    public void setStudyOtherRefs(List<StudyOtherRef> studyOtherRefs) {
        this.studyOtherRefs = studyOtherRefs;
    }
    
    /**
     * Holds value of property studyRelMaterials.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyRelMaterial> studyRelMaterials;
    
    /**
     * Getter for property studyRelMaterial.
     * @return Value of property studyRelMaterial.
     */
    public List<StudyRelMaterial> getStudyRelMaterials() {
        return this.studyRelMaterials;
    }
    
    /**
     * Setter for property studyRelMaterial.
     * @param studyRelMaterial New value of property studyRelMaterial.
     */
    public void setStudyRelMaterials(List<StudyRelMaterial> studyRelMaterials) {
        this.studyRelMaterials = studyRelMaterials;
    }
    
    /**
     * Holds value of property studyRelPublications.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyRelPublication> studyRelPublications;
    
    /**
     * Getter for property studyRelPublications.
     * @return Value of property studyRelPublications.
     */
    public List<StudyRelPublication> getStudyRelPublications() {
        return this.studyRelPublications;
    }
    
    /**
     * Setter for property studyRelPublications.
     * @param studyRelPublications New value of property studyRelPublications.
     */
    public void setStudyRelPublications(List<StudyRelPublication> studyRelPublications) {
        this.studyRelPublications = studyRelPublications;
    }
    
    /**
     * Holds value of property studyRelStudies.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyRelStudy> studyRelStudies;
    
    /**
     * Getter for property studyRelStudies.
     * @return Value of property studyRelStudies.
     */
    public List<StudyRelStudy> getStudyRelStudies() {
        return this.studyRelStudies;
    }
    
    /**
     * Setter for property studyRelStudies.
     * @param studyRelStudies New value of property studyRelStudies.
     */
    public void setStudyRelStudies(List<StudyRelStudy> studyRelStudies) {
        this.studyRelStudies = studyRelStudies;
    }
    
   

  
    
   

     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Metadata)) {
            return false;
        }
        Metadata other = (Metadata)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

  
    
    private String harvestHoldings;

    public String getHarvestHoldings() {
        return harvestHoldings;
    }

    public void setHarvestHoldings(String harvestHoldings) {
        this.harvestHoldings = harvestHoldings;
    }



    @Column(columnDefinition="TEXT")
    private String harvestDVTermsOfUse;

    @Column(columnDefinition="TEXT")
    private String harvestDVNTermsOfUse;

    public String getHarvestDVTermsOfUse() {
        return harvestDVTermsOfUse;
    }

    public void setHarvestDVTermsOfUse(String harvestDVTermsOfUse) {
        this.harvestDVTermsOfUse = harvestDVTermsOfUse;
    }

    public String getHarvestDVNTermsOfUse() {
        return harvestDVNTermsOfUse;
    }

    public void setHarvestDVNTermsOfUse(String harvestDVNTermsOfUse) {
        this.harvestDVNTermsOfUse = harvestDVNTermsOfUse;
    }

    private String getCitation() {
        return getCitation(true);
    }

    public String getTextCitation() {
        return getCitation(false);
    }

    public String getWebCitation() {
        return getCitation(true);
    }
    
    public String getCitation(boolean isOnlineVersion) {

        Study study = getStudy();

        String str = "";
        boolean includeAffiliation = false;
        String authors = getAuthorsStr(includeAffiliation);
        if (!StringUtil.isEmpty(authors)) {
            str += authors;
        }

        if (!StringUtil.isEmpty(getDistributionDate())) {
            if (!StringUtil.isEmpty(str)) {
                str += ", ";
            }
            str += getDistributionDate();
        } else {
            if (!StringUtil.isEmpty(getProductionDate())) {
                if (!StringUtil.isEmpty(str)) {
                    str += ", ";
                }
                str += getProductionDate();
            }
        }
        if (!StringUtil.isEmpty(getTitle())) {
            if (!StringUtil.isEmpty(str)) {
                str += ", ";
            }
            str += "\"" + getTitle() + "\"";
        }
        if (!StringUtil.isEmpty(study.getStudyId())) {
            if (!StringUtil.isEmpty(str)) {
                str += ", ";
            }
            if (isOnlineVersion) {
                str += "<a href=\"" + study.getHandleURL() + "\">" + study.getGlobalId() + "</a>";
            } else {
                str += study.getHandleURL();
            }


        }

        if (!StringUtil.isEmpty(getUNF())) {
            if (!StringUtil.isEmpty(str)) {
                str += " ";
            }
            str += getUNF();
        }
        String distributorNames = getDistributorNames();
        if (distributorNames.length() > 0) {
            str += " " + distributorNames;
            str += " [Distributor]";
        }

        if (getStudyVersion().getVersionNumber() != null) {
            str += " V" + getStudyVersion().getVersionNumber();
            str += " [Version]";
        }

        return str;
    }

    public boolean isTermsOfUseEnabled() {
        // we might make this a true boolean stored in the db at some point;
        // for now, just check if any of the "terms of use" fields are not empty

        // terms of use fields are those from the "use statement" part of the ddi
        if ( !StringUtil.isEmpty(getConfidentialityDeclaration()) ) { return true; }
        if ( !StringUtil.isEmpty(getSpecialPermissions()) ) { return true; }
        if ( !StringUtil.isEmpty(getRestrictions()) ) { return true; }
        if ( !StringUtil.isEmpty(getContact()) ) { return true; }
        if ( !StringUtil.isEmpty(getCitationRequirements()) ) { return true; }
        if ( !StringUtil.isEmpty(getDepositorRequirements()) ) { return true; }
        if ( !StringUtil.isEmpty(getConditions()) ) { return true; }
        if ( !StringUtil.isEmpty(getDisclaimer()) ) { return true; }
        if ( !StringUtil.isEmpty(getHarvestDVNTermsOfUse()) ) { return true; }
        if ( !StringUtil.isEmpty(getHarvestDVTermsOfUse()) ) { return true; }

        return false;
    }
    
    // Return all the Terms of Use-related metadata fields concatenated as 
    // one string, if available: 
    
    public String getTermsOfUseAsString() {
        String touString = ""; 
        
        if ( !StringUtil.isEmpty(getConfidentialityDeclaration()) ) { 
            touString = touString.concat(getConfidentialityDeclaration());
        }
        if ( !StringUtil.isEmpty(getSpecialPermissions()) ) { 
            touString = touString.concat(getSpecialPermissions()); 
        }
        if ( !StringUtil.isEmpty(getRestrictions()) ) { 
            touString = touString.concat(getRestrictions()); 
        }
        if ( !StringUtil.isEmpty(getContact()) ) { 
            touString = touString.concat(getContact()); 
        }
        if ( !StringUtil.isEmpty(getCitationRequirements()) ) { 
            touString = touString.concat(getCitationRequirements()); 
        }
        if ( !StringUtil.isEmpty(getDepositorRequirements()) ) { 
            touString = touString.concat(getDepositorRequirements());
        }
        if ( !StringUtil.isEmpty(getConditions()) ) { 
            touString = touString.concat(getConditions());
        }
        if ( !StringUtil.isEmpty(getDisclaimer()) ) { 
            touString = touString.concat(getDisclaimer()); 
        }
        
        return !StringUtil.isEmpty(touString) ? touString : null; 
    }

    public Study getStudy() {
        return getStudyVersion().getStudy();
    }

    /**
     *  This method populates the dependent collections with at least one element.
     *  It's necessary to do this before displaying the metadata in a form, because
     *  we need empty elements for the users to enter data into.
     */
    public void initCollections() {        
        if ( this.getStudyOtherIds()==null || this.getStudyOtherIds().size()==0) {
            StudyOtherId elem = new StudyOtherId();
            elem.setMetadata(this);
            List otherIds = new ArrayList();
            otherIds.add(elem);
            this.setStudyOtherIds(otherIds);
        }
        if ( this.getStudyAuthors()==null || this.getStudyAuthors().size()==0) {
            List authors = new ArrayList();
            StudyAuthor anAuthor = new StudyAuthor();
            anAuthor.setMetadata(this);
            authors.add(anAuthor);
            this.setStudyAuthors(authors);
        }

        if ( this.getStudyAbstracts()==null || this.getStudyAbstracts().size()==0) {
            List abstracts = new ArrayList();
            StudyAbstract elem = new StudyAbstract();
            elem.setMetadata(this);
            abstracts.add(elem);
            this.setStudyAbstracts(abstracts);
        }

        if (this.getStudyDistributors()==null || this.getStudyDistributors().size()==0) {
            List distributors = new ArrayList();
            StudyDistributor elem = new StudyDistributor();
            elem.setMetadata(this);
            distributors.add(elem);
            this.setStudyDistributors(distributors);
        }
        if (this.getStudyGrants()==null || this.getStudyGrants().size()==0) {
            List grants = new ArrayList();
            StudyGrant elem = new StudyGrant();
            elem.setMetadata(this);
            grants.add(elem);
            this.setStudyGrants(grants);
        }

        if (this.getStudyKeywords()==null || this.getStudyKeywords().size()==0 ) {
            List keywords = new ArrayList();
            StudyKeyword elem = new StudyKeyword();
            elem.setMetadata(this);
            keywords.add(elem);
            this.setStudyKeywords(keywords);
        }

        if (this.getStudyTopicClasses()==null || this.getStudyTopicClasses().size()==0 ) {
            List topicClasses = new ArrayList();
            StudyTopicClass elem = new StudyTopicClass();
            elem.setMetadata(this);
            topicClasses.add(elem);
            this.setStudyTopicClasses(topicClasses);
        }

        if (this.getStudyNotes()==null || this.getStudyNotes().size()==0) {
            List notes = new ArrayList();
            StudyNote elem = new StudyNote();
            elem.setMetadata(this);
            notes.add(elem);
            this.setStudyNotes(notes);
        }

        if (this.getStudyProducers()==null || this.getStudyProducers().size()==0) {
            List producers = new ArrayList();
            StudyProducer elem = new StudyProducer();
            elem.setMetadata(this);
            producers.add(elem);
            this.setStudyProducers(producers);
        }

        if (this.getStudySoftware()==null || this.getStudySoftware().size()==0) {
            List software = new ArrayList();
            StudySoftware elem = new StudySoftware();
            elem.setMetadata(this);
            software.add(elem);
            this.setStudySoftware(software);
        }
        if (this.getStudyGeoBoundings()==null || this.getStudyGeoBoundings().size()==0) {
            List boundings = new ArrayList();
            StudyGeoBounding elem = new StudyGeoBounding();
            elem.setMetadata(this);
            boundings.add(elem);
            this.setStudyGeoBoundings(boundings);
        }
        if (this.getStudyRelMaterials()==null || this.getStudyRelMaterials().size()==0) {
            List mats = new ArrayList();
            StudyRelMaterial elem = new StudyRelMaterial();
            elem.setMetadata(this);
            mats.add(elem);
            this.setStudyRelMaterials(mats);
        }
        if (this.getStudyRelPublications()==null || this.getStudyRelPublications().size()==0) {
            List list = new ArrayList();
            StudyRelPublication elem = new StudyRelPublication();
            elem.setMetadata(this);
            list.add(elem);
            this.setStudyRelPublications(list);
        }
        if (this.getStudyRelStudies()==null || this.getStudyRelStudies().size()==0) {
            List list = new ArrayList();
            StudyRelStudy elem = new StudyRelStudy();
            elem.setMetadata(this);
            list.add(elem);
            this.setStudyRelStudies(list);
        }
        if (this.getStudyOtherRefs()==null || this.getStudyOtherRefs().size()==0) {
            List list = new ArrayList();
            StudyOtherRef elem = new StudyOtherRef();
            elem.setMetadata(this);
            list.add(elem);
            this.setStudyOtherRefs(list);
        }
        
        // custom fields
        for (StudyField sf : this.getStudyFields()) {
            if (sf.getStudyFieldValues()==null || sf.getStudyFieldValues().size()==0) {
                List list = new ArrayList();
                StudyFieldValue elem = new StudyFieldValue();
                elem.setStudyField(sf);
                elem.setMetadata(this);
                list.add(elem);
                sf.setStudyFieldValues(list);
            }            
        }
    }
    
    
   public void setDisplayOrders() {

        int i = 0;
        for (StudyAuthor elem : this.getStudyAuthors()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyAbstract elem : this.getStudyAbstracts()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyDistributor elem : this.getStudyDistributors()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyGeoBounding elem : this.getStudyGeoBoundings()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyGrant elem : this.getStudyGrants()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyKeyword elem : this.getStudyKeywords()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyNote elem : this.getStudyNotes()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyOtherId elem : this.getStudyOtherIds()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyOtherRef elem : this.getStudyOtherRefs()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyProducer elem : this.getStudyProducers()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyRelPublication elem : this.getStudyRelPublications()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyRelStudy elem : this.getStudyRelStudies()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudyRelMaterial elem : this.getStudyRelMaterials()) {
            elem.setDisplayOrder(i++);
        }
        
        i = 0;
        for (StudySoftware elem : this.getStudySoftware()) {
            elem.setDisplayOrder(i++);
        }

        i = 0;
        for (StudyTopicClass elem : this.getStudyTopicClasses()) {
            elem.setDisplayOrder(i++);
        }

        // custom fields
        for (StudyField studyField : this.getStudyFields()) {
            i = 0;
            for (StudyFieldValue elem : studyField.getStudyFieldValues()) {
                elem.setDisplayOrder(i++);
            }
        }  
    }
    
    // this is a transient list of the study fields, so we can initialize it on the first get and then store it here
    @Transient
    List<StudyField> studyFields;

    public List<StudyField> getStudyFields() {
        if (studyFields == null) {
            studyFields = new ArrayList();
            Template templateIn = this.getTemplate() != null ? this.getTemplate() : this.getStudyVersion().getStudy().getTemplate();
            for (TemplateField tf : templateIn.getTemplateFields()) {
                StudyField sf = tf.getStudyField();
                if (sf.isDcmField()) {
                    List sfvList = new ArrayList();
                    // now iterate through values and map accordingly
                    if (studyFieldValues != null){
                        for (StudyFieldValue sfv : studyFieldValues) {
                            if (sf.equals(sfv.getStudyField())) {
                                sfvList.add(sfv);
                            }
                        }
                    }

                    sf.setStudyFieldValues(sfvList);
                    studyFields.add(sf);
                }
            }
            
        }
            
        return studyFields;
    }
    
}
