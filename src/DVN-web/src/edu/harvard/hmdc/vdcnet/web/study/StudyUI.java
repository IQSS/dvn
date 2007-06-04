/*
 * StudyUI.java
 *
 * Created on November 10, 2006, 7:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.FileCategory;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAbstract;
import edu.harvard.hmdc.vdcnet.study.StudyAuthor;
import edu.harvard.hmdc.vdcnet.study.StudyDistributor;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyGeoBounding;
import edu.harvard.hmdc.vdcnet.study.StudyGrant;
import edu.harvard.hmdc.vdcnet.study.StudyKeyword;
import edu.harvard.hmdc.vdcnet.study.StudyNote;
import edu.harvard.hmdc.vdcnet.study.StudyOtherId;
import edu.harvard.hmdc.vdcnet.study.StudyOtherRef;
import edu.harvard.hmdc.vdcnet.study.StudyProducer;
import edu.harvard.hmdc.vdcnet.study.StudyRelMaterial;
import edu.harvard.hmdc.vdcnet.study.StudyRelPublication;
import edu.harvard.hmdc.vdcnet.study.StudyRelStudy;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudySoftware;
import edu.harvard.hmdc.vdcnet.study.StudyTopicClass;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;

/**
 *
 * @author gdurand
 */
public class StudyUI {
    
    private Study study;
    
    /** Creates a new instance of StudyUI 
     *  this consturctor does not initialize the file category ui list
     *  and is meant to be used in places where you do not need them
     *  e.g. the SearchPage or the CollectionTree
     */
    public StudyUI(Study s) {
        this.study = s;
    }
    
      /** 
       * Creates a new instance of StudyUI 
       * this constructor initializes the file category ui list
       * Use this constructor if you want to set the StudyFileUI.fileRestrictedFor user value
       */
    
    public StudyUI(Study s, VDC vdc, VDCUser user) {
        this.study = s;
        initFileCategoryUIList(vdc,user);
    }
    
    public Study getStudy() {
        return study;
    }
    
 
    
    /**
     * Return for each studyAuthor: Author (Affiliation), only if affiliation is not empty
     */
    public String getAuthors() {
        String str="";
        for (Iterator<StudyAuthor> it = study.getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor sa =  it.next();
            if (!StringUtil.isEmpty(sa.getName())) {
                if (str!="") {
                    str+="; ";
                }
                str += sa.getName();
                
            }
        }
        return str;
        
    }
    
    
    public String getAuthorAffiliations() {
        String str="";
        boolean hasAffiliation=false;
        
        for (Iterator<StudyAuthor> it = study.getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor sa =  it.next();
            if (!StringUtil.isEmpty(sa.getName())) {
                if (str!="") {
                    str+="; ";
                }
                str += sa.getName();
                
            }
            if (!StringUtil.isEmpty(sa.getAffiliation())) {
                hasAffiliation=true;
                str+=" ("+sa.getAffiliation()+")";
            }
        }
        /** commented (MC) - show authors always
        * if (!hasAffiliation) {
        *     str="";
        * }
         */
        return str;
        
    }
    
    public String getAbstracts() {
        String str="";
        for (Iterator<StudyAbstract> it = study.getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem =  it.next();
            if (!StringUtil.isEmpty(elem.getText())) {
                str+="<p>"+elem.getText()+"</p>";
            }
            
        }
        return str;
    }
    
    public String getDistributorContact() {
        String str="";
        if (!StringUtil.isEmpty(study.getDistributorContact())) {
            str+=study.getDistributorContact();
        }
        if (!StringUtil.isEmpty(study.getDistributorContactAffiliation())) {
            str+=" ("+study.getDistributorContactAffiliation()+")";
        }
        if (!StringUtil.isEmpty(study.getDistributorContactEmail())) {
            if (str!="") {
                str+=", ";
            }
            str+=study.getDistributorContactEmail();
        }
        /*"Distributor Contact (affiliation), e-mail"*/
        return str;
    }
    
    
    public String getSeries() {
        String str="";
        if (!StringUtil.isEmpty(study.getSeriesName())) {
            str+=study.getSeriesName();
        }
        if (!StringUtil.isEmpty(study.getSeriesInformation())) {
            if (str!="") {
                str+=", ";
            }
            str+=study.getSeriesInformation();
        }
        return str;
    }
    
    
    public String getStudyVersion() {
        String str="";
        if (!StringUtil.isEmpty(study.getStudyVersion())) {
            str+=study.getStudyVersion();
        }
        if (!StringUtil.isEmpty(study.getVersionDate())) {
            if (str!="") {
                str+=", ";
            }
            str+=study.getVersionDate();
        }
        return str;
    }
    
    public String getTimePeriodCovered() {
        String str="";
        if (!StringUtil.isEmpty(study.getTimePeriodCoveredStart())) {
            str+=study.getTimePeriodCoveredStart();
        }
        if (!StringUtil.isEmpty(study.getTimePeriodCoveredEnd())) {
            if (str!="") {
                str+=" - ";
            }
            str+=study.getTimePeriodCoveredEnd();
        }
        return str;
        
    }
    
    public String getDateOfCollection() {
        String str="";
        if (!StringUtil.isEmpty(study.getDateOfCollectionStart())) {
            str+=study.getDateOfCollectionStart();
        }
        if (!StringUtil.isEmpty(study.getDateOfCollectionEnd())) {
            if (str!="") {
                str+=" - ";
            }
            str+=study.getDateOfCollectionEnd();
        }
        return str;
    }
    
    public String getTruncatedAbstracts() {
        String abstracts = getAbstracts();
        String truncatedAbstracts = StringUtil.truncateString( abstracts, 200 );
        
        return truncatedAbstracts;
    }
    
    public boolean isRenderAbstractsMoreLink() {
        String abstracts = getAbstracts();
        String truncatedAbstracts = StringUtil.truncateString( abstracts, 200 );
        
        return  !truncatedAbstracts.equals(abstracts);
    }
    
    public boolean isFiles() {
        for (Iterator<FileCategory> it = study.getFileCategories().iterator(); it.hasNext();) {
            if ( it.next().getStudyFiles().size() > 0) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isSubsettable() {
        for (Iterator<FileCategory> it = study.getFileCategories().iterator(); it.hasNext();) {
            for (Iterator<StudyFile> fit = it.next().getStudyFiles().iterator(); fit.hasNext();) {
                if ( fit.next().isSubsettable()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean isNonSubsettable() {
        for (Iterator<FileCategory> it = study.getFileCategories().iterator(); it.hasNext();) {
            for (Iterator<StudyFile> fit = it.next().getStudyFiles().iterator(); fit.hasNext();) {
                if ( !fit.next().isSubsettable()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public String getProducers() {
        String str ="";
        for (Iterator<StudyProducer> it = study.getStudyProducers().iterator(); it.hasNext();) {
            StudyProducer elem = it.next();
            if (!StringUtil.isEmpty(elem.getName())) {
                if (str!="") {
                    str+="; ";
                }
                if (!StringUtil.isEmpty(elem.getUrl())) {
                    str +="<a href='"+elem.getUrl()+"' target='_blank' title='"+elem.getName()+"'>"+elem.getName()+"</a>";
                } else {
                    str+=elem.getName();
                }
                
            }
            if (!StringUtil.isEmpty(elem.getAbbreviation())) {
                str+=" ("+elem.getAbbreviation()+")";
            }
            if (!StringUtil.isEmpty(elem.getAffiliation())) {
                str+=", "+elem.getAffiliation();
            }
            if (!StringUtil.isEmpty(elem.getLogo())) {
                str+=" <img src='"+elem.getLogo()+"' height='30px' alt='Logo' title='Logo' />";
            }
            
            
            
        }
        return str;
        
    }
    
    
    public String getAbstractDates() {
        String str="";
        for (Iterator<StudyAbstract> it = getStudy().getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem =  it.next();
            if (!StringUtil.isEmpty(elem.getDate())) {
                if (str!="") {
                    str+="; ";
                }
                str+=elem.getDate();
            }
            
        }
        return str;
        
    }
    
    public String getNotes() {
        String str="";
        for (Iterator<StudyNote> it = getStudy().getStudyNotes().iterator(); it.hasNext();) {
            StudyNote elem = it.next();
            if (!StringUtil.isEmpty(elem.getType())) {
                if (str!="") {
                    str+="; ";
                }
                str+=elem.getType();
            }
            if (!StringUtil.isEmpty(elem.getSubject())) {
                str+=" ("+elem.getSubject()+")";
            }
            if (!StringUtil.isEmpty(elem.getText())) {
                str+=" "+elem.getText();
            }
            
        }
        return str;
    }
    
     public String getRelPublications() {
        String str="";
        for (Iterator<StudyRelPublication> it = getStudy().getStudyRelPublications().iterator(); it.hasNext();) {
            StudyRelPublication elem = it.next();
            if (!StringUtil.isEmpty(elem.getText())) {
                if (str!="") {
                    str+="; ";
                }
                str+=elem.getText();
            }
            
        }
        return str;
    } 
     
   
    public String getRelMaterials() {
        String str="";
        for (Iterator<StudyRelMaterial> it = getStudy().getStudyRelMaterials().iterator(); it.hasNext();) {
            StudyRelMaterial elem = it.next();
            if (!StringUtil.isEmpty(elem.getText())) {
                if (str!="") {
                    str+="; ";
                }
                str+=elem.getText();
            }
            
        }
        return str;
    } 
      public String getRelStudies() {
        String str="";
        for (Iterator<StudyRelStudy> it = getStudy().getStudyRelStudies().iterator(); it.hasNext();) {
            StudyRelStudy elem = it.next();
            if (!StringUtil.isEmpty(elem.getText())) {
                if (str!="") {
                    str+="; ";
                }
                str+=elem.getText();
            }
            
        }
        return str;
    } 
      public String getOtherRefs() {
        String str="";
        for (Iterator<StudyOtherRef> it = getStudy().getStudyOtherRefs().iterator(); it.hasNext();) {
            StudyOtherRef elem = it.next();
            if (!StringUtil.isEmpty(elem.getText())) {
                if (str!="") {
                    str+="; ";
                }
                str+=elem.getText();
            }
            
        }
        return str;
    } 
      
    
    public String getSoftware() {
        String str="";
        for (Iterator<StudySoftware> it = study.getStudySoftware().iterator(); it.hasNext();) {
            StudySoftware ss =  it.next();
            if (!StringUtil.isEmpty(ss.getName())) {
                if (str!="") {
                    str+="; ";
                }
                str+= ss.getName();
            }
            if (!StringUtil.isEmpty(ss.getSoftwareVersion())) {
                str+=", "+ss.getSoftwareVersion();
            }
        }
        return str;
    }
    
    public String getGrants() {
        String str="";
        for (Iterator<StudyGrant> it = study.getStudyGrants().iterator(); it.hasNext();) {
            StudyGrant elem =  it.next();
            if (!StringUtil.isEmpty(elem.getNumber())) {
                if (str!="") {
                    str+="; ";
                }
                str+=elem.getNumber();
                if (!StringUtil.isEmpty(elem.getAgency())) {
                    str+=", ";
                }
            }
            if (!StringUtil.isEmpty(elem.getAgency())){
                str+=elem.getAgency();
            }
            
        }
        return str;
    }
    
    public String getOtherIds() {
        String str="";
        for (Iterator<StudyOtherId> it = study.getStudyOtherIds().iterator(); it.hasNext();) {
            StudyOtherId elem =  it.next();
            if (!StringUtil.isEmpty(elem.getAgency())) {
                if (str!="") {
                    str+="; ";
                }
                str+=elem.getAgency();
                if (elem.getAgency()!=null) {
                    str+=": ";
                }
            }
            if (!StringUtil.isEmpty(elem.getOtherId())){
                str+=elem.getOtherId();
            }
            
        }
        return str;
    }
    
    public String getGeographicBoundings() {
        String str = "";
        for (Iterator it = study.getStudyGeoBoundings().iterator(); it.hasNext();) {
            StudyGeoBounding elem = (StudyGeoBounding) it.next();
            String boundingStr = "";
            if (!StringUtil.isEmpty(elem.getWestLongitude())) {
                boundingStr+="West Bounding Longitude: "+elem.getWestLongitude()+" ";
            }
            if (!StringUtil.isEmpty(elem.getEastLongitude())) {
                boundingStr+="East Bounding Longitude: "+elem.getEastLongitude()+" ";
            }
            if (!StringUtil.isEmpty(elem.getNorthLatitude())) {
                boundingStr+="North Bounding Latitude: "+elem.getNorthLatitude()+" ";
            }
            if (!StringUtil.isEmpty(elem.getSouthLatitude())) {
                boundingStr+="South Bounding Latitude: "+elem.getSouthLatitude();
            }
            
            if (boundingStr!="" ) {
                if (str!="") {
                    str+="; ";
                }
                str+= boundingStr;
            }
        }
        
        return str;
    }
    
    public String getKeywords() {
        String str = "";
        for (Iterator<StudyKeyword> it = study.getStudyKeywords().iterator(); it.hasNext();) {
            StudyKeyword elem =  it.next();
            if (!StringUtil.isEmpty(elem.getValue())) {
                if (str!="") {
                    str+="; ";
                }
                str+=elem.getValue();
            }
            if (!StringUtil.isEmpty(elem.getVocab())) {
                if (!StringUtil.isEmpty(elem.getVocabURI())) {
                    str +="&#32; (<a href='"+elem.getVocabURI()+"' target='_blank'>"+elem.getVocab()+"</a>)";
                } else {
                    str+="&#32; ("+elem.getVocab()+")";
                }
            }
        }
        
        return str;
    }
    
    public String getTopicClasses() {
        String str = "";
        for (Iterator<StudyTopicClass> it = study.getStudyTopicClasses().iterator(); it.hasNext();) {
            StudyTopicClass elem =  it.next();
            if (!StringUtil.isEmpty(elem.getValue())) {
                if (str!="") {
                    str+="; ";
                }
                str+=elem.getValue();
            }
            if (!StringUtil.isEmpty(elem.getVocab())) {
                if (!StringUtil.isEmpty(elem.getVocabURI())) {
                    str +="&#32; (<a href='"+elem.getVocabURI()+"' target='_blank'>"+elem.getVocab()+"</a>)";
                } else {
                    str+="&#32; ("+elem.getVocab()+")";
                }
            }
        }
        
        return str;
    }
    
    
    public String getDistributors() {
        String str = "";
        for (Iterator<StudyDistributor> it = study.getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor elem = it.next();
            if (!StringUtil.isEmpty(elem.getName())) {
                if (str!="") {
                    str+="; ";
                }
                
                if (!StringUtil.isEmpty(elem.getUrl())) {
                    str +="<a href='"+elem.getUrl()+"' target='_blank' title='"+elem.getName()+"'>"+elem.getName()+"</a>";
                } else {
                    str+=elem.getName();
                }
                
            }
            if (!StringUtil.isEmpty(elem.getAbbreviation())) {
                str+=" ("+elem.getAbbreviation()+")";
            }
            if (!StringUtil.isEmpty(elem.getAffiliation())) {
                str+=", "+elem.getAffiliation();
            }
            if (!StringUtil.isEmpty(elem.getLogo())) {
                str+=" <img src='"+elem.getLogo()+"' height='30px' alt='Logo' title='Logo' />";
            }
            
            
            
        }
        return str;
        
    }
    
    
    private boolean abstractAndScopePanelIsRendered;
    
    private boolean citationInformationPanelIsRendered;
    
    private boolean dataCollectionPanelIsRendered;
    
    private boolean notesPanelIsRendered;
    
    private boolean termsOfUsePanelIsRendered;
    
    public boolean isAbstractAndScopePanelIsRendered() {
        return abstractAndScopePanelIsRendered;
    }
    
    public void setAbstractAndScopePanelIsRendered(boolean abstractAndScopePanelIsRendered) {
        this.abstractAndScopePanelIsRendered = abstractAndScopePanelIsRendered;
    }
    
    public boolean isCitationInformationPanelIsRendered() {
        return citationInformationPanelIsRendered;
    }
    
    public void setCitationInformationPanelIsRendered(boolean citationInformationPanelIsRendered) {
        this.citationInformationPanelIsRendered = citationInformationPanelIsRendered;
    }
    
    public boolean isDataCollectionPanelIsRendered() {
        return dataCollectionPanelIsRendered;
    }
    
    public void setDataCollectionPanelIsRendered(boolean dataCollectionPanelIsRendered) {
        this.dataCollectionPanelIsRendered = dataCollectionPanelIsRendered;
    }
    
    public boolean isNotesPanelIsRendered() {
        return notesPanelIsRendered;
    }
    
    public void setNotesPanelIsRendered(boolean notesPanelIsRendered) {
        this.notesPanelIsRendered = notesPanelIsRendered;
    }
    
    public boolean isTermsOfUsePanelIsRendered() {
        return termsOfUsePanelIsRendered;
    }
    
    public void setTermsOfUsePanelIsRendered(boolean termsOfUsePanelIsRendered) {
        this.termsOfUsePanelIsRendered = termsOfUsePanelIsRendered;
    }
    
    public void initFileCategoryUIList(VDC vdc, VDCUser user) {
        categoryUIList = new ArrayList<FileCategoryUI>();
        StudyServiceLocal studyService = null;
        try {
            studyService=(StudyServiceLocal)new InitialContext().lookup("java:comp/env/studyService");
        } catch(Exception e) {
            e.printStackTrace();
        }
    
        List categories = studyService.getOrderedFileCategories(study.getId());
      
        Iterator iter = categories.iterator();
        while (iter.hasNext()) {
            FileCategory fc = (FileCategory) iter.next();
            FileCategoryUI catUI = new FileCategoryUI(fc,vdc,user);
            categoryUIList.add(catUI);
        }
       
    }
    
    
    public boolean isAnyFileUnrestricted() {
        
        for (Iterator it = categoryUIList.iterator(); it.hasNext();) {
            FileCategoryUI catUI = (FileCategoryUI) it.next();
            if (catUI.isAnyFileUnrestricted()) {
                return true;
            }
        }
        return false;
        
    }
    
    public boolean isAnyFileRestricted() {
       for (Iterator it = categoryUIList.iterator(); it.hasNext();) {
            FileCategoryUI catUI = (FileCategoryUI) it.next();
            for (Iterator it2 = catUI.getStudyFileUIs().iterator(); it2.hasNext();) {
                StudyFileUI studyFileUI = (StudyFileUI) it2.next();
                if (studyFileUI.isRestrictedForUser()) {
                    return true;
                }
            }
           
        }
        return false;
        
    }
    
    
    
    public static List filterVisibleStudies(List originalStudies, VDC vdc, VDCUser user) {
        return filterVisibleStudies(originalStudies, vdc, user, -1);
    }


    public static List filterVisibleStudies(List originalStudies, VDC vdc, VDCUser user, int numResults) {
        List filteredStudies = new ArrayList();
        
        if (numResults != 0) {
            int count = 0;
            Iterator iter = originalStudies.iterator();
            while (iter.hasNext()) {
                Study study = (Study) iter.next();
                if (StudyUI.isStudyVisibleToUser(study, vdc, user) ) {
                    filteredStudies.add(study);
                    if (numResults > 0 && ++count >= numResults) {
                        break;
                    }   
                }
            }
        }
        
        return filteredStudies;
    }
    
    public static boolean isStudyVisibleToUser(Study study, VDC vdc, VDCUser user) {
        // if restricted vdc, only visible in that VDC
        if ( study.getOwner().isRestricted() &&
                ( vdc == null || !study.getOwner().getId().equals(vdc.getId()) )    ) {
            return false;
        }
        
        // only visible if released
        if ( !study.isReleased() ) {
            return false;
        }
        
        // lastly check restrictions
        return !study.isStudyRestrictedForUser( vdc, user);

        
    }
    
    /** 
     * check if this is visible for the ipgroup
     *
     * @author wbossons
     */
    public static boolean isStudyVisibleToGroup(Study study, VDC vdc, UserGroup usergroup) {
        // if restricted vdc, only visible in that VDC
        if ( study.getOwner().isRestricted() &&
                ( vdc == null || !study.getOwner().getId().equals(vdc.getId()) )    ) {
            return false;
        }
        
        // only visible if released
        if ( !study.isReleased() ) {
            return false;
        }
        
        // lastly check restrictions
        return !study.isStudyRestrictedForGroup(usergroup);
        
    }

    /**
     * Holds value of property categoryUIList.
     */
    private ArrayList<FileCategoryUI> categoryUIList;

    /**
     * Getter for property categoryUIList.
     * @return Value of property categoryUIList.
     */
    public ArrayList<FileCategoryUI> getCategoryUIList() {
        return this.categoryUIList;
    }

    /**
     * Setter for property categoryUIList.
     * @param categoryUIList New value of property categoryUIList.
     */
    public void setCategoryUIList(ArrayList<FileCategoryUI> categoryUIList) {
        this.categoryUIList = categoryUIList;
    }
    
    
    private List foundInVariables;

    public List getFoundInVariables() {
        return foundInVariables;
    }

    public void setFoundInVariables(List foundInVariables) {
        this.foundInVariables = foundInVariables;
    }  
    
    public static boolean isStudyInList(Study study, List list) {
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Study s = (Study) iter.next();
            if ( s.getId().equals(study.getId()) ) {
                return true;
            }
        }
        
        return false;
    }    
    
    public int getNumberOfDownloads() {
        return (study.getStudyDownload() != null ? study.getStudyDownload().getNumberOfDownloads() : 0);
    }
}
