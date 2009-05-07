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
 * Study.java
 *
 * Created on July 28, 2006, 2:44 PM
 *
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCRole;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.ReviewState;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJBException;
import javax.persistence.*;

import org.apache.commons.lang.builder.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"authority,protocol,studyId"}))
public class Study implements java.io.Serializable {

    @OneToOne(mappedBy = "study", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private StudyDownload studyDownload;    
    @OneToOne(mappedBy = "study", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private StudyLock studyLock;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastUpdateTime;
    @ManyToOne
    private ReviewState reviewState;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastExportTime;
    @ManyToMany(cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST })
    private Collection<UserGroup> allowedGroups;
    @ManyToOne
    private VDCUser creator;
    @ManyToOne
    private VDCUser lastUpdater;
    private boolean isHarvested;
    @ManyToMany( cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST })
    private Collection<StudyField> summaryFields;
    @OneToMany(mappedBy="study", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<StudyAccessRequest> studyRequests;
    @OneToOne(cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Metadata metadata;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastIndexTime;
    
    public Study () {
        metadata = new Metadata();
        
    }    
     public Study(VDC vdc, VDCUser creator, ReviewState reviewState) {
         this(vdc,creator,reviewState,null);
     }
        
    public Study(VDC vdc, VDCUser creator, ReviewState reviewState, Template initTemplate) {
        if (vdc==null) {
            throw new EJBException("Cannot create study with null VDC");
        }
        this.setOwner(vdc);
        if (initTemplate == null ){
            setTemplate(vdc.getDefaultTemplate());                    
        } else {
            this.setTemplate(initTemplate);
          
        }
        metadata = new Metadata();
        template.getMetadata().copyMetadata(metadata);
        if (vdc != null) {
            vdc.getOwnedStudies().add(this);
        }
        
        Date createDate = new Date();

        this.setCreator(creator);
        this.setCreateTime(createDate);

        this.setLastUpdater(creator);
        this.setLastUpdateTime(createDate);

        this.setReviewState( reviewState );
    }
       
    
    public String getGlobalId() {
        return protocol+":"+authority+"/"+getStudyId();
    }

    public String getCitation() {
        return getCitation(true);
    }

    public String getCitation(boolean isOnlineVersion) {
        
        String str="";
        boolean includeAffiliation=false;
        String authors = getAuthorsStr(includeAffiliation);
        if (!StringUtil.isEmpty(authors)) {
            str+=authors;
        }
        
        if (!StringUtil.isEmpty(getDistributionDate())) {
            if (!StringUtil.isEmpty(str)) {
                str+=", ";
            }
            str+=getDistributionDate();
        } else {
            if (!StringUtil.isEmpty(getProductionDate())) {
                if (!StringUtil.isEmpty(str)) {
                    str+=", ";
                }
                str+=getProductionDate();
            }
        }
        if (!StringUtil.isEmpty(metadata.getTitle())) {
            if (!StringUtil.isEmpty(str)) {
                str+=", ";
            }
            str+="\""+metadata.getTitle()+"\"";
        }
        if (!StringUtil.isEmpty(getStudyId())) {
            if (!StringUtil.isEmpty(str)) {
                str+=", ";
            }
            if (isOnlineVersion) {
                str+="<a href='"+getHandleURL()+"'>"+getGlobalId()+"</a>";
            } else {
                str+=getHandleURL();
            }
                    

        }
        
        if (!StringUtil.isEmpty(metadata.getUNF())) {
            if (!StringUtil.isEmpty(str)) {
                str+=" ";
            }
            str+=metadata.getUNF();
        }
        String distributorNames = getDistributorNames();
        if (distributorNames.length()>0) {
            str+=" "+distributorNames;
            str+=" [Distributor]";
        }
        
        return str;
    }
    
    public String getHandleURL() {
         return "http://hdl.handle.net/"+authority+"/"+getStudyId();
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
    private String studyId;
    
    public String getStudyId() {
        return studyId;
    }
    
    public void setStudyId(String studyId) {
        this.studyId = (studyId != null ? studyId.toUpperCase() : null);
    }
 
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    
    
    public boolean isInReview() {
        return reviewState.getName().equals(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW);
    }
    
    public boolean isNew() {
        return reviewState.getName().equals(ReviewStateServiceLocal.REVIEW_STATE_NEW);
    }
    
    public boolean isReleased() {
        return reviewState.getName().equals(ReviewStateServiceLocal.REVIEW_STATE_RELEASED);
    }
    
    public ReviewState getReviewState() {
        return reviewState;
    }
    
    public void setReviewState(ReviewState reviewState) {
        this.reviewState = reviewState;
    }
    
    public String getUNF() {
        return metadata.getUNF();
    }
    
    public void setUNF(String UNF) {
        metadata.setUNF(UNF);
    }
    
    public Collection<UserGroup> getAllowedGroups() {
        return allowedGroups;
    }
    
    public void setAllowedGroups(Collection<UserGroup> allowedGroups) {
        this.allowedGroups = allowedGroups;
    }
    
    public VDCUser getCreator() {
        return creator;
    }
    
    public void setCreator(VDCUser creator) {
        this.creator = creator;
    }
    
    public VDCUser getLastUpdater() {
        return lastUpdater;
    }
    
    public void setLastUpdater(VDCUser lastUpdater) {
        this.lastUpdater = lastUpdater;
    }
    
    public boolean isIsHarvested() {
        return isHarvested;
    }
    
    public void setIsHarvested(boolean isHarvested) {
        this.isHarvested = isHarvested;
    }
    
    public Collection<StudyField> getSummaryFields() {
        return summaryFields;
    }
    
    public void setSummaryFields(Collection<StudyField> summaryFields) {
        this.summaryFields = summaryFields;
    }
    
    public String getTitle() {
        return metadata.getTitle();
    }
    
    public void setTitle(String title) {
        metadata.setTitle(title);
    }
    
    public java.util.List<StudyAuthor> getStudyAuthors() {
        return metadata.getStudyAuthors();
    }
    
    public void setStudyAuthors(java.util.List<StudyAuthor> studyAuthors) {
        metadata.setStudyAuthors(studyAuthors);
    }
    
    /**
     * Holds value of property numberOfDownloads.
     */
    private long numberOfDownloads;
    
    /**
     * Getter for property numberOfDownloads.
     * @return Value of property numberOfDownloads.
     */
    public long getNumberOfDownloads() {
        return this.numberOfDownloads;
    }
    
    /**
     * Setter for property numberOfDownloads.
     * @param numberOfDownloads New value of property numberOfDownloads.
     */
    public void setNumberOfDownloads(long numberOfDownloads) {
        this.numberOfDownloads = numberOfDownloads;
    }
    
   
    
    public java.util.List<StudyKeyword> getStudyKeywords() {
        return metadata.getStudyKeywords();
    }
    
    public void setStudyKeywords(java.util.List<StudyKeyword> studyKeywords) {
        metadata.setStudyKeywords(studyKeywords);
    }
    
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
    
    
    /**
     * Holds value of property defaultFileCategory.
     */
    @OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="defaultFileCategory_id")
    private FileCategory defaultFileCategory;
    
    /**
     * Getter for property defaultFileCategory.
     * @return Value of property defaultFileCategory.
     */
    public FileCategory getDefaultFileCategory() {
        return this.defaultFileCategory;
    }
    
    /**
     * Setter for property defaultFileCategory.
     * @param defaultFileCategory New value of property defaultFileCategory.
     */
    public void setDefaultFileCategory(FileCategory defaultFileCategory) {
        this.defaultFileCategory = defaultFileCategory;
    }
    
    /**
     * Holds value of property fileCategories.
     */
    @OneToMany(mappedBy="study", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("name ASC")
    private java.util.List<edu.harvard.iq.dvn.core.study.FileCategory> fileCategories;
    
    /**
     * Getter for property fileCategories.
     * @return Value of property fileCategories.
     */
    public java.util.List<edu.harvard.iq.dvn.core.study.FileCategory> getFileCategories() {
        return this.fileCategories;
    }
    
    /**
     * Setter for property fileCategories.
     * @param fileCategories New value of property fileCategories.
     */
    public void setFileCategories(java.util.List<edu.harvard.iq.dvn.core.study.FileCategory> fileCategories) {
        this.fileCategories = fileCategories;
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
    @ManyToMany(mappedBy="studies")
    private Collection<VDCCollection> studyColls;
    
    
    
    public Collection<VDCCollection> getStudyColls() {
        return studyColls;
    }
    
    public void setStudyColls(Collection<VDCCollection> studyColls) {
        this.studyColls = studyColls;
    }
    
    
    /**
     * Getter for property studyProducers.
     * @return Value of property studyProducers.
     */
    public List<StudyProducer> getStudyProducers() {
        return metadata.getStudyProducers();
    }
    
    /**
     * Setter for property studyProducers.
     * @param studyProducers New value of property studyProducers.
     */
    public void setStudyProducers(List<StudyProducer> studyProducers) {
        metadata.setStudyProducers(studyProducers);
    }
    
   
    /**
     * Getter for property studySoftware.
     * @return Value of property studySoftware.
     */
    public List<StudySoftware> getStudySoftware() {
        return metadata.getStudySoftware();
    }
    
    /**
     * Setter for property studySoftware.
     * @param studySoftware New value of property studySoftware.
     */
    public void setStudySoftware(List<StudySoftware> studySoftware) {
        metadata.setStudySoftware(studySoftware);
    }
    
    
    /**
     * Getter for property studyDistributors.
     * @return Value of property studyDistributors.
     */
    public List<StudyDistributor> getStudyDistributors() {
        return metadata.getStudyDistributors();
    }
    
    /**
     * Setter for property studyDistributors.
     * @param studyDistributors New value of property studyDistributors.
     */
    public void setStudyDistributors(List<StudyDistributor> studyDistributors) {
        metadata.setStudyDistributors(studyDistributors);
    }
        
    /**
     * Getter for property studyTopicClasses.
     * @return Value of property studyTopicClasses.
     */
    public List<StudyTopicClass> getStudyTopicClasses() {
        return metadata.getStudyTopicClasses();
    }
    
    /**
     * Setter for property studyTopicClasses.
     * @param studyTopicClasses New value of property studyTopicClasses.
     */
    public void setStudyTopicClasses(List<StudyTopicClass> studyTopicClasses) {
      
        metadata.setStudyTopicClasses(studyTopicClasses);
    }
    
     
    /**
     * Getter for property studyAbstracts.
     * @return Value of property studyAbstracts.
     */
    public List<StudyAbstract> getStudyAbstracts() {
        return metadata.getStudyAbstracts();
    }
    
    /**
     * Setter for property studyAbstracts.
     * @param studyAbstracts New value of property studyAbstracts.
     */
    public void setStudyAbstracts(List<StudyAbstract> studyAbstracts) {
        metadata.setStudyAbstracts(studyAbstracts);
    }
       
    /**
     * Getter for property studyNotes.
     * @return Value of property studyNotes.
     */
    public List<StudyNote> getStudyNotes() {
        return metadata.getStudyNotes();
    }
    
    /**
     * Setter for property studyNotes.
     * @param studyNotes New value of property studyNotes.
     */
    public void setStudyNotes(List<StudyNote> studyNotes) {
        metadata.setStudyNotes(studyNotes);
    }
    
    
    /**
     * Getter for property fundingAgency.
     * @return Value of property fundingAgency.
     */
    public String getFundingAgency() {
        return metadata.getFundingAgency();
    }
      
    /**
     * Setter for property fundingAgency.
     * @param fundingAgency New value of property fundingAgency.
     */
    public void setFundingAgency(String fundingAgency) {
        metadata.setFundingAgency(fundingAgency);
    }
     
    /**
     * Getter for property seriesName.
     * @return Value of property seriesName.
     */
    public String getSeriesName() {
        return metadata.getSeriesName();
    }
    
    /**
     * Setter for property seriesName.
     * @param seriesName New value of property seriesName.
     */
    public void setSeriesName(String seriesName) {
        metadata.setSeriesName(seriesName);
    }
        
    /**
     * Getter for property seriesInformation.
     * @return Value of property seriesInformation.
     */
    public String getSeriesInformation() {
        return metadata.getSeriesInformation();
    }
    
    /**
     * Setter for property seriesInformation.
     * @param seriesInformation New value of property seriesInformation.
     */
    public void setSeriesInformation(String seriesInformation) {
        metadata.setSeriesInformation(seriesInformation);
    }
    
    /**
     * Getter for property studyVersion.
     * @return Value of property studyVersion.
     */
    public String getStudyVersion() {
        return metadata.getStudyVersion();
    }
    
    /**
     * Setter for property studyVersion.
     * @param studyVersion New value of property studyVersion.
     */
    public void setStudyVersion(String studyVersion) {
        metadata.setStudyVersion(studyVersion);
    }

    /**
     * Getter for property timePeriodCoveredStart.
     * @return Value of property timePeriodCoveredStart.
     */
    public String getTimePeriodCoveredStart() {
        return metadata.getTimePeriodCoveredStart();
    }
    
    /**
     * Setter for property timePeriodCoveredStart.
     * @param timePeriodCoveredStart New value of property timePeriodCoveredStart.
     */
    public void setTimePeriodCoveredStart(String timePeriodCoveredStart) {
        metadata.setTimePeriodCoveredStart(timePeriodCoveredStart);
    }
     
    /**
     * Getter for property timePeriodCoveredEnd.
     * @return Value of property timePeriodCoveredEnd.
     */
    public String getTimePeriodCoveredEnd() {
        return metadata.getTimePeriodCoveredEnd();
    }
    
    /**
     * Setter for property timePeriodCoveredEnd.
     * @param timePeriodCoveredEnd New value of property timePeriodCoveredEnd.
     */
    public void setTimePeriodCoveredEnd(String timePeriodCoveredEnd) {
        metadata.setTimePeriodCoveredEnd(timePeriodCoveredEnd);
    }
    
    /**
     * Getter for property dateOfCollectionStart.
     * @return Value of property dateOfCollectionStart.
     */
    public String getDateOfCollectionStart() {
        return metadata.getDateOfCollectionStart();
    }
    
    /**
     * Setter for property dateOfCollectionStart.
     * @param dateOfCollectionStart New value of property dateOfCollectionStart.
     */
    public void setDateOfCollectionStart(String dateOfCollectionStart) {
        metadata.setDateOfCollectionStart(dateOfCollectionStart);
    }
     
    /**
     * Getter for property dateOfCollectionEnd.
     * @return Value of property dateOfCollectionEnd.
     */
    public String getDateOfCollectionEnd() {
        return metadata.getDateOfCollectionEnd();
    }
    
    /**
     * Setter for property dateOfCollectionEnd.
     * @param dateOfCollectionEnd New value of property dateOfCollectionEnd.
     */
    public void setDateOfCollectionEnd(String dateOfCollectionEnd) {
        metadata.setDateOfCollectionEnd(dateOfCollectionEnd);
    }
    
    /**
     * Getter for property country.
     * @return Value of property country.
     */
    public String getCountry() {
        return metadata.getCountry();
    }
    
    /**
     * Setter for property country.
     * @param country New value of property country.
     */
    public void setCountry(String country) {
        metadata.setCountry(country);
    }

    /**
     * Getter for property geographicCoverage.
     * @return Value of property geographicCoverage.
     */
    public String getGeographicCoverage() {
        return metadata.getGeographicCoverage();
    }
    
    /**
     * Setter for property geographicCoverage.
     * @param geographicCoverage New value of property geographicCoverage.
     */
    public void setGeographicCoverage(String geographicCoverage) {
        metadata.setGeographicCoverage(geographicCoverage);
    }
    
    
    /**
     * Getter for property geographicUnit.
     * @return Value of property geographicUnit.
     */
    public String getGeographicUnit() {
        return metadata.getGeographicUnit();
    }
    
    /**
     * Setter for property geographicUnit.
     * @param geographicUnit New value of property geographicUnit.
     */
    public void setGeographicUnit(String geographicUnit) {
        metadata.setGeographicUnit(geographicUnit);
    }
    
    /**
     * Getter for property unitOfAnalysis.
     * @return Value of property unitOfAnalysis.
     */
    public String getUnitOfAnalysis() {
        return metadata.getUnitOfAnalysis();
    }
    
    /**
     * Setter for property unitOfAnalysis.
     * @param unitOfAnalysis New value of property unitOfAnalysis.
     */
    public void setUnitOfAnalysis(String unitOfAnalysis) {
        metadata.setUnitOfAnalysis(unitOfAnalysis);
    }
    /**
     * Getter for property universe.
     * @return Value of property universe.
     */
    public String getUniverse() {
        return metadata.getUniverse();
    }
    
    /**
     * Setter for property universe.
     * @param universe New value of property universe.
     */
    public void setUniverse(String universe) {
        metadata.setUniverse(universe);
    }
     
    /**
     * Getter for property kindOfData.
     * @return Value of property kindOfData.
     */
    public String getKindOfData() {
        return metadata.getKindOfData();
    }
    
    /**
     * Setter for property kindOfData.
     * @param kindOfData New value of property kindOfData.
     */
    public void setKindOfData(String kindOfData) {
        metadata.setKindOfData(kindOfData);
    }

    /**
     * Getter for property timeMethod.
     * @return Value of property timeMethod.
     */
    public String getTimeMethod() {
        return metadata.getTimeMethod();
    }
    
    /**
     * Setter for property timeMethod.
     * @param timeMethod New value of property timeMethod.
     */
    public void setTimeMethod(String timeMethod) {
        metadata.setTimeMethod(timeMethod);
    }
    
    /**
     * Getter for property dataCollector.
     * @return Value of property dataCollector.
     */
    public String getDataCollector() {
        return metadata.getDataCollector();
    }
    
    /**
     * Setter for property dataCollector.
     * @param dataCollector New value of property dataCollector.
     */
    public void setDataCollector(String dataCollector) {
        metadata.setDataCollector(dataCollector);
    }
    
    /**
     * Getter for property frequencyOfDataCollection.
     * @return Value of property frequencyOfDataCollection.
     */
    public String getFrequencyOfDataCollection() {
        return metadata.getFrequencyOfDataCollection();
    }
    
    /**
     * Setter for property frequencyOfDataCollection.
     * @param frequencyOfDataCollection New value of property frequencyOfDataCollection.
     */
    public void setFrequencyOfDataCollection(String frequencyOfDataCollection) {
        metadata.setFrequencyOfDataCollection( frequencyOfDataCollection);
    }
    /**
     * Getter for property samplingProdedure.
     * @return Value of property samplingProdedure.
     */
    public String getSamplingProcedure() {
        return metadata.getSamplingProcedure();
    }
    
    /**
     * Setter for property samplingProdedure.
     * @param samplingProdedure New value of property samplingProdedure.
     */
    public void setSamplingProcedure(String samplingProcedure) {
        metadata.setSamplingProcedure( samplingProcedure);
    }

    /**
     * Getter for property deviationsFromSampleDesign.
     * @return Value of property deviationsFromSampleDesign.
     */
    public String getDeviationsFromSampleDesign() {
        return metadata.getDeviationsFromSampleDesign();
    }
    
    /**
     * Setter for property deviationsFromSampleDesign.
     * @param deviationsFromSampleDesign New value of property deviationsFromSampleDesign.
     */
    public void setDeviationsFromSampleDesign(String deviationsFromSampleDesign) {
        metadata.setDeviationsFromSampleDesign( deviationsFromSampleDesign);
    }
    
    /**
     * Getter for property collectionMode.
     * @return Value of property collectionMode.
     */
    public String getCollectionMode() {
        return metadata.getCollectionMode();
    }
    
    /**
     * Setter for property collectionMode.
     * @param collectionMode New value of property collectionMode.
     */
    public void setCollectionMode(String collectionMode) {
        metadata.setCollectionMode(collectionMode);
    }
     
    /**
     * Getter for property researchInstrument.
     * @return Value of property researchInstrument.
     */
    public String getResearchInstrument() {
        return metadata.getResearchInstrument();
    }
    
    /**
     * Setter for property researchInstrument.
     * @param researchInstrument New value of property researchInstrument.
     */
    public void setResearchInstrument(String researchInstrument) {
        metadata.setResearchInstrument( researchInstrument);
    }
     
    /**
     * Getter for property dataSources.
     * @return Value of property dataSources.
     */
    public String getDataSources() {
        return metadata.getDataSources();
    }
    
    /**
     * Setter for property dataSources.
     * @param dataSources New value of property dataSources.
     */
    public void setDataSources(String dataSources) {
        metadata.setDataSources(dataSources);
    }
    
    /**
     * Getter for property originOfSources.
     * @return Value of property originOfSources.
     */
    public String getOriginOfSources() {
        return metadata.getOriginOfSources();
    }
    
    /**
     * Setter for property originOfSources.
     * @param originOfSources New value of property originOfSources.
     */
    public void setOriginOfSources(String originOfSources) {
        metadata.setOriginOfSources( originOfSources);
    }
    
    /**
     * Getter for property characteristicOfSources.
     * @return Value of property characteristicOfSources.
     */
    public String getCharacteristicOfSources() {
        return metadata.getCharacteristicOfSources();
    }
    
    /**
     * Setter for property characteristicOfSources.
     * @param characteristicOfSources New value of property characteristicOfSources.
     */
    public void setCharacteristicOfSources(String characteristicOfSources) {
        metadata.setCharacteristicOfSources( characteristicOfSources);
    }
 
    /**
     * Getter for property accessToSources.
     * @return Value of property accessToSources.
     */
    public String getAccessToSources() {
        return metadata.getAccessToSources();
    }
    
    /**
     * Setter for property accessToSources.
     * @param accessToSources New value of property accessToSources.
     */
    public void setAccessToSources(String accessToSources) {
        metadata.setAccessToSources(accessToSources);
    }
    
    /**
     * Getter for property dataCollectionSituation.
     * @return Value of property dataCollectionSituation.
     */
    public String getDataCollectionSituation() {
        return metadata.getDataCollectionSituation();
    }
    
    /**
     * Setter for property dataCollectionSituation.
     * @param dataCollectionSituation New value of property dataCollectionSituation.
     */
    public void setDataCollectionSituation(String dataCollectionSituation) {
        metadata.setDataCollectionSituation(dataCollectionSituation);
    }
    
    /**
     * Getter for property actionsToMinimizeLoss.
     * @return Value of property actionsToMinimizeLoss.
     */
    public String getActionsToMinimizeLoss() {
        return metadata.getActionsToMinimizeLoss();
    }
    
    /**
     * Setter for property actionsToMinimizeLoss.
     * @param actionsToMinimizeLoss New value of property actionsToMinimizeLoss.
     */
    public void setActionsToMinimizeLoss(String actionsToMinimizeLoss) {
        metadata.setActionsToMinimizeLoss(actionsToMinimizeLoss);
    }
    
   
    /**
     * Getter for property controlOperations.
     * @return Value of property controlOperations.
     */
    public String getControlOperations() {
        return metadata.getControlOperations();
    }
    
    /**
     * Setter for property controlOperations.
     * @param controlOperations New value of property controlOperations.
     */
    public void setControlOperations(String controlOperations) {
        metadata.setControlOperations( controlOperations);
    }
    
    /**
     * Getter for property weighting.
     * @return Value of property weighting.
     */
    public String getWeighting() {
        return metadata.getWeighting();
    }
    
    /**
     * Setter for property weighting.
     * @param weighting New value of property weighting.
     */
    public void setWeighting(String weighting) {
        metadata.setWeighting(weighting);
    }
    
    /**
     * Getter for property cleaningOperations.
     * @return Value of property cleaningOperations.
     */
    public String getCleaningOperations() {
        return metadata.getCleaningOperations();
    }
    
    /**
     * Setter for property cleaningOperations.
     * @param cleaningOperations New value of property cleaningOperations.
     */
    public void setCleaningOperations(String cleaningOperations) {
        metadata.setCleaningOperations( cleaningOperations);
    }
    
    /**
     * Getter for property studyLevelErrorNotes.
     * @return Value of property studyLevelErrorNotes.
     */
    public String getStudyLevelErrorNotes() {
        return metadata.getStudyLevelErrorNotes();
    }
    
    /**
     * Setter for property studyLevelErrorNotes.
     * @param studyLevelErrorNotes New value of property studyLevelErrorNotes.
     */
    public void setStudyLevelErrorNotes(String studyLevelErrorNotes) {
        metadata.setStudyLevelErrorNotes(studyLevelErrorNotes);
    }
      
    /**
     * Getter for property responseRate.
     * @return Value of property responseRate.
     */
    public String getResponseRate() {
        return metadata.getResponseRate();
    }
    
    /**
     * Setter for property responseRate.
     * @param responseRate New value of property responseRate.
     */
    public void setResponseRate(String responseRate) {
        metadata.setResponseRate(responseRate);
    }
    
    
    /**
     * Getter for property samplingErrorEstimate.
     * @return Value of property samplingErrorEstimate.
     */
    public String getSamplingErrorEstimate() {
        return metadata.getSamplingErrorEstimate();
    }
    
    /**
     * Setter for property samplingErrorEstimate.
     * @param samplingErrorEstimate New value of property samplingErrorEstimate.
     */
    public void setSamplingErrorEstimate(String samplingErrorEstimate) {
        metadata.setSamplingErrorEstimate( samplingErrorEstimate);
    }
    
    /**
     * Getter for property dataAppraisal.
     * @return Value of property dataAppraisal.
     */
    public String getOtherDataAppraisal() {
        return metadata.getOtherDataAppraisal();
    }
    
    /**
     * Setter for property dataAppraisal.
     * @param dataAppraisal New value of property dataAppraisal.
     */
    public void setOtherDataAppraisal(String otherDataAppraisal) {
        metadata.setOtherDataAppraisal(otherDataAppraisal);
    }

    /**
     * Getter for property placeOfAccess.
     * @return Value of property placeOfAccess.
     */
    public String getPlaceOfAccess() {
        return metadata.getPlaceOfAccess();
    }
    
    /**
     * Setter for property placeOfAccess.
     * @param placeOfAccess New value of property placeOfAccess.
     */
    public void setPlaceOfAccess(String placeOfAccess) {
        metadata.setPlaceOfAccess(placeOfAccess);
    }
    
     /**
     * Getter for property originalArchive.
     * @return Value of property originalArchive.
     */
    public String getOriginalArchive() {
        return metadata.getOriginalArchive();
    }
    
    /**
     * Setter for property originalArchive.
     * @param originalArchive New value of property originalArchive.
     */
    public void setOriginalArchive(String originalArchive) {
        metadata.setOriginalArchive(originalArchive);
    }
        
    /**
     * Getter for property availabilityStatus.
     * @return Value of property availabilityStatus.
     */
    public String getAvailabilityStatus() {
        return metadata.getAvailabilityStatus();
    }
    
    /**
     * Setter for property availabilityStatus.
     * @param availabilityStatus New value of property availabilityStatus.
     */
    public void setAvailabilityStatus(String availabilityStatus) {
        metadata.setAvailabilityStatus(availabilityStatus);
    }
    
    /**
     * Getter for property collectionSize.
     * @return Value of property collectionSize.
     */
    public String getCollectionSize() {
        return metadata.getCollectionSize();
    }
    
    /**
     * Setter for property collectionSize.
     * @param collectionSize New value of property collectionSize.
     */
    public void setCollectionSize(String collectionSize) {
        metadata.setCollectionSize( collectionSize);
    }
    
    /**
     * Getter for property studyCompletion.
     * @return Value of property studyCompletion.
     */
    public String getStudyCompletion() {
        return metadata.getStudyCompletion();
    }
    
    /**
     * Setter for property studyCompletion.
     * @param studyCompletion New value of property studyCompletion.
     */
    public void setStudyCompletion(String studyCompletion) {
        metadata.setStudyCompletion( studyCompletion);
    }
    
    /**
     * Holds value of property numberOfFiles.
     */
    @Column(columnDefinition="TEXT")
    private String numberOfFiles;
    
    /**
     * Getter for property numberOfFiles.
     * @return Value of property numberOfFiles.
     */
    public String getNumberOfFiles() {
        return this.numberOfFiles;
    }
    
    /**
     * Setter for property numberOfFiles.
     * @param numberOfFiles New value of property numberOfFiles.
     */
    public void setNumberOfFiles(String numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }
    

    
    /**
     * Getter for property specialPermissions.
     * @return Value of property specialPermissions.
     */
    public String getSpecialPermissions() {
        return metadata.getSpecialPermissions();
    }
    
    /**
     * Setter for property specialPermissions.
     * @param specialPermissions New value of property specialPermissions.
     */
    public void setSpecialPermissions(String specialPermissions) {
        metadata.setSpecialPermissions(specialPermissions);
    }
    
    
    /**
     * Getter for property restrictions.
     * @return Value of property restrictions.
     */
    public String getRestrictions() {
        return metadata.getRestrictions();
    }
    
    /**
     * Setter for property restrictions.
     * @param restrictions New value of property restrictions.
     */
    public void setRestrictions(String restrictions) {
        metadata.setRestrictions(restrictions);
    }
        
    /**
     * Getter for property contact.
     * @return Value of property contact.
     */
    public String getContact() {
        return metadata.getContact();
    }
    
    /**
     * Setter for property contact.
     * @param contact New value of property contact.
     */
    public void setContact(String contact) {
        metadata.setContact(contact);
    }
    
    /**
     * Getter for property citationRequirements.
     * @return Value of property citationRequirements.
     */
    public String getCitationRequirements() {
        return metadata.getCitationRequirements();
    }
    
    /**
     * Setter for property citationRequirements.
     * @param citationRequirements New value of property citationRequirements.
     */
    public void setCitationRequirements(String citationRequirements) {
        metadata.setCitationRequirements(citationRequirements);
    }
    
     
    /**
     * Getter for property dipositorRequirements.
     * @return Value of property dipositorRequirements.
     */
    public String getDepositorRequirements() {
        return metadata.getDepositorRequirements();
    }
    
    /**
     * Setter for property dipositorRequirements.
     * @param dipositorRequirements New value of property dipositorRequirements.
     */
    public void setDepositorRequirements(String depositorRequirements) {
        metadata.setDepositorRequirements( depositorRequirements);
    }
    
    /**
     * Getter for property conditions.
     * @return Value of property conditions.
     */
    public String getConditions() {
        return metadata.getConditions();
    }
    
    /**
     * Setter for property conditions.
     * @param conditions New value of property conditions.
     */
    public void setConditions(String conditions) {
        metadata.setConditions(conditions);
    }
    
     /**
     * Getter for property disclaimer.
     * @return Value of property disclaimer.
     */
    public String getDisclaimer() {
        return metadata.getDisclaimer();
    }
    
    /**
     * Setter for property disclaimer.
     * @param disclaimer New value of property disclaimer.
     */
    public void setDisclaimer(String disclaimer) {
        metadata.setDisclaimer( disclaimer);
    }
    
    /**
     * Getter for property productionDate.
     * @return Value of property productionDate.
     */
    public String getProductionDate() {
        return metadata.getProductionDate();
    }
    
    /**
     * Setter for property productionDate.
     * @param productionDate New value of property productionDate.
     */
    public void setProductionDate(String productionDate) {
        metadata.setProductionDate(productionDate);
    }
    
    
    /**
     * Getter for property productionPlace.
     * @return Value of property productionPlace.
     */
    public String getProductionPlace() {
        return metadata.getProductionPlace();
    }
    
    /**
     * Setter for property productionPlace.
     * @param productionPlace New value of property productionPlace.
     */
    public void setProductionPlace(String productionPlace) {
        metadata.setProductionPlace(productionPlace);
    }
    
    /**
     * Getter for property confidentialityDeclaration.
     * @return Value of property confidentialityDeclaration.
     */
    public String getConfidentialityDeclaration() {
        return metadata.getConfidentialityDeclaration();
    }
    
    /**
     * Setter for property confidentialityDeclaration.
     * @param confidentialityDeclaration New value of property confidentialityDeclaration.
     */
    public void setConfidentialityDeclaration(String confidentialityDeclaration) {
        metadata.setConfidentialityDeclaration(confidentialityDeclaration);
    }
    
    
    
    /**
     * Getter for property studyGrants.
     * @return Value of property studyGrants.
     */
    public List<StudyGrant> getStudyGrants() {
        return metadata.getStudyGrants();
    }
    
    /**
     * Setter for property studyGrants.
     * @param studyGrants New value of property studyGrants.
     */
    public void setStudyGrants(List<StudyGrant> studyGrants) {
        metadata.setStudyGrants(studyGrants);
    }
    
 
    
    /**
     * Getter for property distributionDate.
     * @return Value of property distributionDate.
     */
    public String getDistributionDate() {
        return metadata.getDistributionDate();
    }
    
    /**
     * Setter for property distributionDate.
     * @param distributionDate New value of property distributionDate.
     */
    public void setDistributionDate(String distributionDate) {
        metadata.setDistributionDate(distributionDate);
    }
    
    /**
     * Getter for property distributorContact.
     * @return Value of property distributorContact.
     */
    public String getDistributorContact() {
        return metadata.getDistributorContact();
    }
    
    /**
     * Setter for property distributorContact.
     * @param distributorContact New value of property distributorContact.
     */
    public void setDistributorContact(String distributorContact) {
        metadata.setDistributorContact(distributorContact);
    }
    
    /**
     * Getter for property distributorContactAffiliation.
     * @return Value of property distributorContactAffiliation.
     */
    public String getDistributorContactAffiliation() {
        return metadata.getDistributorContactAffiliation();
    }
    
    /**
     * Setter for property distributorContactAffiliation.
     * @param distributorContactAffiliation New value of property distributorContactAffiliation.
     */
    public void setDistributorContactAffiliation(String distributorContactAffiliation) {
        metadata.setDistributorContactAffiliation(distributorContactAffiliation);
    }
    
    
    /**
     * Getter for property distributorContactEmail.
     * @return Value of property distributorContactEmail.
     */
    public String getDistributorContactEmail() {
        return metadata.getDistributorContactEmail();
    }
    
    /**
     * Setter for property distributorContactEmail.
     * @param distributorContactEmail New value of property distributorContactEmail.
     */
    public void setDistributorContactEmail(String distributorContactEmail) {
        metadata.setDistributorContactEmail(distributorContactEmail);
    }
    
    /**
     * Getter for property depositor.
     * @return Value of property depositor.
     */
    public String getDepositor() {
        return metadata.getDepositor();
    }
    
    /**
     * Setter for property depositor.
     * @param depositor New value of property depositor.
     */
    public void setDepositor(String depositor) {
        metadata.setDepositor(depositor);
    }
    
    /**
     * Getter for property dateOfDeposit.
     * @return Value of property dateOfDeposit.
     */
    public String getDateOfDeposit() {
        return metadata.getDateOfDeposit();
    }
    
    /**
     * Setter for property dateOfDeposit.
     * @param dateOfDeposit New value of property dateOfDeposit.
     */
    public void setDateOfDeposit(String dateOfDeposit) {
        metadata.setDateOfDeposit( dateOfDeposit);
    }
        
    /**
     * Getter for property studyOtherIds.
     * @return Value of property studyOtherIds.
     */
    public List<StudyOtherId> getStudyOtherIds() {
        return metadata.getStudyOtherIds();
    }
    
    /**
     * Setter for property studyOtherIds.
     * @param studyOtherIds New value of property studyOtherIds.
     */
    public void setStudyOtherIds(List<StudyOtherId> studyOtherIds) {
        metadata.setStudyOtherIds(studyOtherIds);
    }
    
    /**
     * Holds value of property template.
     */
    @ManyToOne
    private Template template;
    
    /**
     * Getter for property template.
     * @return Value of property template.
     */
    public Template getTemplate() {
        return this.template;
    }
    
    /**
     * Setter for property template.
     * @param template New value of property template.
     */
    public void setTemplate(Template template) {
        this.template = template;
    }
    
    /**
     * Holds value of property restricted.
     */
    private boolean restricted;
    
    /**
     * Getter for property restricted.
     * @return Value of property restricted.
     */
    public boolean isRestricted() {
        return this.restricted;
    }
    
    /**
     * Setter for property restricted.
     * @param restricted New value of property restricted.
     */
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }
    
    /**
     * Holds value of property allowedUsers.
     */
    @ManyToMany(cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST })
    private Collection<VDCUser> allowedUsers;
    
    /**
     * Getter for property allowedUsers.
     * @return Value of property allowedUsers.
     */
    public Collection<VDCUser> getAllowedUsers() {
        return this.allowedUsers;
    }
    
    /**
     * Setter for property allowedUsers.
     * @param allowedUsers New value of property allowedUsers.
     */
    public void setAllowedUsers(Collection<VDCUser> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }
    
    /**
     * Holds value of property requestAccess.
     * If this value is true, then the user can send an email
     * to the vdc administrator requesting access to the study
     */
    private boolean requestAccess;
    
    /**
     * Getter for property requestAccess.
     * @return Value of property requestAccess.
     */
    public boolean isRequestAccess() {
        return this.requestAccess;
    }
    
    /**
     * Setter for property requestAccess.
     * @param requestAccess New value of property requestAccess.
     */
    public void setRequestAccess(boolean requestAccess) {
        this.requestAccess = requestAccess;
    }
    
  
    /**
     * Getter for property versionDate.
     * @return Value of property versionDate.
     */
    public String getVersionDate() {
        return metadata.getVersionDate();
    }
    
    /**
     * Setter for property versionDate.
     * @param versionDate New value of property versionDate.
     */
    public void setVersionDate(String versionDate) {
        metadata.setVersionDate(versionDate);
    }
    
    /**
     * Holds value of property owner.
     */
    @ManyToOne
    private VDC owner;
    
    /**
     * Getter for property owner.
     * @return Value of property owner.
     */
    public VDC getOwner() {
        return this.owner;
    }
    
    /**
     * Setter for property owner.
     * @param owner New value of property owner.
     */
    public void setOwner(VDC owner) {
        this.owner = owner;
    }
    
    /**
     * Holds value of property reviewer.
     */
    @ManyToOne
    private VDCUser reviewer;
    
    /**
     * Getter for property reviewer.
     * @return Value of property reviewer.
     */
    public VDCUser getReviewer() {
        return this.reviewer;
    }
    
    /**
     * Setter for property reviewer.
     * @param reviewer New value of property reviewer.
     */
    public void setReviewer(VDCUser reviewer) {
        this.reviewer = reviewer;
    }
    
    public Collection<StudyAccessRequest> getStudyRequests() {
        return studyRequests;
    }
    
    public void setStudyRequests(Collection<StudyAccessRequest> studyRequests) {
        this.studyRequests = studyRequests;
    }
    

    
    // these are wrapper methods to support old code; once old calls are
    // cleaned up and use the method directly, these should be removed
    public boolean isStudyRestrictedForUser(VDC vdc, VDCUser user) {
        return isStudyRestrictedForUser(user, null);
    }
    
    public boolean isStudyRestrictedForGroup(UserGroup usergroup) {
        return isStudyRestrictedForUser(null, usergroup);
    }    
    
    public boolean isStudyRestrictedForUser(VDCUser user, UserGroup ipUserGroup) {
        
        // the restrictions should be checked on the owner of the study, not the currentVDC (needs cleanup)
        VDC vdc = this.getOwner();
      
        // first check restrictions at the dataverse level
        if (this.getOwner().isVDCRestrictedForUser(user, null) ) {
            return true;
        }

        // otherwise check for restriction on study itself
        if ( isRestricted() ) {
            if (user == null) {
                if (ipUserGroup==null) {
                    return true;
                } else {
                    Iterator iter = this.getAllowedGroups().iterator();
                    while (iter.hasNext()) {
                        UserGroup allowedGroup = (UserGroup) iter.next();
                        if (allowedGroup.equals(ipUserGroup)) {
                            return false;
                        }    
                    } 
                    return true;
                }
            }
          
            // 1. check network role
            if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN) ) {
                // If you are network admin, you can do anything!
                return false;
            }
            
            // 2. check vdc role
            VDCRole userRole = user.getVDCRole(vdc);
            if (userRole != null) {
                String userRoleName = userRole.getRole().getName();
                if ( userRoleName.equals(RoleServiceLocal.ADMIN) || userRoleName.equals(RoleServiceLocal.CURATOR) ) {
                    return false;
                }
            }
            
            // 2a. check if creator
            if (user.getId().equals(this.getCreator().getId())) {
                return false;
            }
            
            // 3. check user
            Iterator iter = this.getAllowedUsers().iterator();
            while (iter.hasNext()) {
                VDCUser allowedUser = (VDCUser) iter.next();
                if ( allowedUser.getId().equals(user.getId()) ) {
                    return false;
                }
            }
            
            // 4. check groups
            iter = this.getAllowedGroups().iterator();
            while (iter.hasNext()) {
                UserGroup allowedGroup = (UserGroup) iter.next();
                if (user.getUserGroups().contains(allowedGroup)) {
                    return false;
                }
            }
            return true;
        }      
        else {
            return false;
        }
     }


/*********************************************************************
The methods in this section are the old way and need to be removed, once they are no longer called;
I left in for now because I want to make sure that using new code doesn't break anything
*********************************************************************/
    public  boolean isUserRestricted( VDC vdc, VDCUser user) {

        // the restrictions should be checked on the owner of the study, not the currentVDC (needs cleanup)
        vdc = this.getOwner();
        
        if (user == null) {
            return true;
        }
        if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
            return false;
        }
        VDCRole userRole = user.getVDCRole(vdc);
        String userRoleName=null;
        if (userRole!=null) {
            userRoleName = userRole.getRole().getName();
        }
        
        if (RoleServiceLocal.ADMIN.equals(userRoleName)
        || RoleServiceLocal.CURATOR.equals(userRoleName)
        || userInAllowedGroups(user)
        || userInAllowedUsers(user)
        || getCreator().getId().equals(user.getId())) {
            return false;
        } else {
            return true;
        }
        
    }
    
    private boolean userInAllowedUsers(VDCUser user) {
        for (Iterator it = allowedUsers.iterator(); it.hasNext();) {
            VDCUser allowedUser = (VDCUser) it.next();
            if (allowedUser.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean userInAllowedGroups(VDCUser user) {
        boolean foundUser=false;
        for (Iterator it = allowedGroups.iterator(); it.hasNext();) {
            UserGroup userGroup = (UserGroup) it.next();
            for (Iterator it2 = userGroup.getUsers().iterator(); it2.hasNext();) {
                VDCUser allowedUser = (VDCUser) it2.next();
                if (allowedUser.getId().equals(user.getId())) {
                    foundUser=true;
                    break;
                }
                
            }
            
            
        }
        return foundUser;
    }
/*********************************************************************
End of deprecated methods section
*********************************************************************/


    public List<StudyFile> getStudyFiles() {
        List files = new ArrayList();
        
        Iterator iter = getFileCategories().iterator();
        while (iter.hasNext()) {
            FileCategory cat = (FileCategory) iter.next();
            files.addAll(cat.getStudyFiles());
        }
        return files;
    }
    
    public boolean isTermsOfUseEnabled() {
        // we might make this a true boolean stored in the db at some point;
        // for now, just check if any of the "terms of use" fields are not empty
        
        // terms of use fields are those from the "use statement" part of the ddi
        if ( !StringUtil.isEmpty(metadata.getConfidentialityDeclaration()) ) { return true; }
        if ( !StringUtil.isEmpty(metadata.getSpecialPermissions()) ) { return true; }
        if ( !StringUtil.isEmpty(metadata.getRestrictions()) ) { return true; }
        if ( !StringUtil.isEmpty(metadata.getContact()) ) { return true; }
        if ( !StringUtil.isEmpty(metadata.getCitationRequirements()) ) { return true; }
        if ( !StringUtil.isEmpty(metadata.getDepositorRequirements()) ) { return true; }
        if ( !StringUtil.isEmpty(metadata.getConditions()) ) { return true; }
        if ( !StringUtil.isEmpty(metadata.getDisclaimer()) ) { return true; }
        if ( !StringUtil.isEmpty(getHarvestDVNTermsOfUse()) ) { return true; }
        if ( !StringUtil.isEmpty(getHarvestDVTermsOfUse()) ) { return true; }
        
        return false;
    }
    

    /**
     * Getter for property replicationFor.
     * @return Value of property replicationFor.
     */
    
    public String getReplicationFor() {
        return metadata.getReplicationFor();
    }
    
    /**
     * Setter for property replicationFor.
     * @param replicationFor New value of property replicationFor.
     */
    public void setReplicationFor(String replicationFor) {
        metadata.setReplicationFor(replicationFor);
    }
    
    /**
     * Getter for property subTitle.
     * @return Value of property subTitle.
     */
    public String getSubTitle() {
        return metadata.getSubTitle();
    }
    
    /**
     * Setter for property subTitle.
     * @param subTitle New value of property subTitle.
     */
    public void setSubTitle(String subTitle) {
        metadata.setSubTitle(subTitle);
    }
    
    public java.util.List<StudyGeoBounding> getStudyGeoBoundings() {
        return metadata.getStudyGeoBoundings();
    }
    
    public void setStudyGeoBoundings(java.util.List<StudyGeoBounding> studyGeoBoundings) {
        metadata.setStudyGeoBoundings( studyGeoBoundings);
    }
    
    /**
     * Holds value of property protocol.
     */
    @Column(columnDefinition="TEXT")
    private String protocol;
    
    /**
     * Getter for property protocol.
     * @return Value of property protocol.
     */
    public String getProtocol() {
        return this.protocol;
    }
    
    /**
     * Setter for property protocol.
     * @param protocol New value of property protocol.
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    /**
     * Holds value of property authority.
     */
    @Column(columnDefinition="TEXT")
    private String authority;
    
    /**
     * Getter for property authority.
     * @return Value of property authority.
     */
    public String getAuthority() {
        return this.authority;
    }
    
    /**
     * Setter for property authority.
     * @param authority New value of property authority.
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }
    
    private String harvestIdentifier;
    
    public String getHarvestIdentifier() {
        return harvestIdentifier;
    }

    public void setHarvestIdentifier(String harvestIdentifier) {
        this.harvestIdentifier = harvestIdentifier;
    }     
    
    /**
     * Getter for property studyOtherRefs.
     * @return Value of property studyOtherRefs.
     */
    public List<StudyOtherRef> getStudyOtherRefs() {
        return metadata.getStudyOtherRefs();
    }
    
    /**
     * Setter for property studyOtherRefs.
     * @param studyOtherRefs New value of property studyOtherRefs.
     */
    public void setStudyOtherRefs(List<StudyOtherRef> studyOtherRefs) {
        metadata.setStudyOtherRefs( studyOtherRefs);
    }
    
 
    /**
     * Getter for property studyRelMaterial.
     * @return Value of property studyRelMaterial.
     */
    public List<StudyRelMaterial> getStudyRelMaterials() {
        return metadata.getStudyRelMaterials();
    }
    
    /**
     * Setter for property studyRelMaterial.
     * @param studyRelMaterial New value of property studyRelMaterial.
     */
    public void setStudyRelMaterials(List<StudyRelMaterial> studyRelMaterials) {
        metadata.setStudyRelMaterials( studyRelMaterials);
    }
    
     
    /**
     * Getter for property studyRelPublications.
     * @return Value of property studyRelPublications.
     */
    public List<StudyRelPublication> getStudyRelPublications() {
        return metadata.getStudyRelPublications();
    }
    
    /**
     * Setter for property studyRelPublications.
     * @param studyRelPublications New value of property studyRelPublications.
     */
    public void setStudyRelPublications(List<StudyRelPublication> studyRelPublications) {
        metadata.setStudyRelPublications(studyRelPublications);
    }
     
    /**
     * Getter for property studyRelStudies.
     * @return Value of property studyRelStudies.
     */
    public List<StudyRelStudy> getStudyRelStudies() {
        return metadata.getStudyRelStudies();
    }
    
    /**
     * Setter for property studyRelStudies.
     * @param studyRelStudies New value of property studyRelStudies.
     */
    public void setStudyRelStudies(List<StudyRelStudy> studyRelStudies) {
        metadata.setStudyRelStudies(studyRelStudies);
    }
    
    public boolean isUserAuthorizedToEdit(VDCUser user) {   
        String  studyVDCRoleName =null;
        // No users are allowed to edit a Harvested Study
        if (owner.isHarvestingDv()) {
            return false;
        }
        if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
            return true;
        }

        if (user.getVDCRole(owner)!=null) {
            studyVDCRoleName= user.getVDCRole(owner).getRole().getName();
        }
        if ((creator.getId().equals(user.getId()) && reviewState.getName().equals(ReviewStateServiceLocal.REVIEW_STATE_NEW))
        || RoleServiceLocal.ADMIN.equals(studyVDCRoleName)
        || RoleServiceLocal.CURATOR.equals(studyVDCRoleName)) {
            return true;
        }
        return false;
    }

    public boolean isUserAuthorizedToRelease(VDCUser user) {   
        String  studyVDCRoleName =null;
        // No users are allowed to edit a Harvested Study
        if (owner.isHarvestingDv()) {
            return false;
        }
        if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
            return true;
        }
        if (user.getVDCRole(owner)!=null) {
            studyVDCRoleName= user.getVDCRole(owner).getRole().getName();
        }
        if (RoleServiceLocal.ADMIN.equals(studyVDCRoleName)
        || RoleServiceLocal.CURATOR.equals(studyVDCRoleName)) {
            return true;
        }
        return false;
    }
    
    public StudyLock getStudyLock() {
        return studyLock;
    }

    public void setStudyLock(StudyLock studyLock) {
        this.studyLock = studyLock;
    }

     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Study)) {
            return false;
        }
        Study other = (Study)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    public StudyDownload getStudyDownload() {
        return studyDownload;
    }

    public void setStudyDownload(StudyDownload studyDownload) {
        this.studyDownload = studyDownload;
    }

    public String getHarvestHoldings() {
        return metadata.getHarvestHoldings();
    }

    public void setHarvestHoldings(String harvestHoldings) {
        metadata.setHarvestHoldings(harvestHoldings);
    }
    
    
    public Date getLastExportTime() {
        return lastExportTime;
    }

    public void setLastExportTime(Date lastExportTime) {
        this.lastExportTime = lastExportTime;
    }

    public String getHarvestDVTermsOfUse() {
        return metadata.getHarvestDVTermsOfUse();
    }

    public void setHarvestDVTermsOfUse(String harvestDVTermsOfUse) {
        metadata.setHarvestDVTermsOfUse( harvestDVTermsOfUse);
    }

    public String getHarvestDVNTermsOfUse() {
        return metadata.getHarvestDVNTermsOfUse();
    }

    public void setHarvestDVNTermsOfUse(String harvestDVNTermsOfUse) {
        metadata.setHarvestDVNTermsOfUse(harvestDVNTermsOfUse);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Date getLastIndexTime() {
        return lastIndexTime;
    }

    public void setLastIndexTime(Date lastIndexTime) {
        this.lastIndexTime = lastIndexTime;
    }

    @OneToMany(mappedBy = "study", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<StudyFileActivity> studyFileActivity;

    public List<StudyFileActivity> getStudyFileActivity() {
        return studyFileActivity;
    }

    public void setStudyFileActivity(List<StudyFileActivity> studyFileActivity) {
        this.studyFileActivity = studyFileActivity;
    }
    
//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this,
//            ToStringStyle.MULTI_LINE_STYLE);
//    }

}
