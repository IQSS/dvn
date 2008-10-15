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
import edu.harvard.hmdc.vdcnet.ddi.DDIServiceBean;
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
import edu.harvard.hmdc.vdcnet.util.DvnDate;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;

/**
 *
 * @author gdurand
 */
public class StudyUI  implements java.io.Serializable {
    
    private Study study;
    private Long studyId;
    private Map studyFields;
    private UserGroup ipUserGroup;
    
    /** Creates a new instance of StudyUI
     *  this consturctor does not initialize the file category ui list
     *  and is meant to be used in places where you do not need them
     *  e.g. the StudyListingPage or the CollectionTree
     */
    public StudyUI(Study s) {
        this.study = s;
    }
    
    public StudyUI(Long sid) {
        this.studyId = sid;
    }
    
    public StudyUI(Long sid, Map studyFields) {
        this.studyId = sid;
        this.studyFields = studyFields;
    }
    
    public StudyUI(Long sid, boolean selected) {
        this.studyId = sid;
        this.selected = selected;
    }

    public StudyUI(Study s, boolean selected) {
        this.study = s;
        this.studyId = s.getId();
        this.selected = selected;
    }   
    
    /**
     * Creates a new instance of StudyUI
     * this constructor initializes the file category ui list
     * Use this constructor if you want to set the StudyFileUI.fileRestrictedFor user value
     */
    public StudyUI(Study s, VDC vdc, VDCUser user, UserGroup ipUserGroup) {
        this.study = s;
        this.studyId = s.getId();
        this.ipUserGroup = ipUserGroup;
        initFileCategoryUIList(vdc, user, ipUserGroup);
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }    
    
    public Study getStudy() {
        // check to see if study is loaded or if we only have the studyId
        if (study == null) {
            StudyServiceLocal studyService = null;
            try {
                studyService = (StudyServiceLocal) new InitialContext().lookup("java:comp/env/studyService");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            study = studyService.getStudyForSearch(studyId, studyFields);
        }
        
        return study;
    }
    
    public void setStudy(Study study) {
        this.study = study;
    }
    
    /**
     * Return for each studyAuthor: Author (Affiliation), only if affiliation is not empty
     */
    public String getAuthors() {
        String str = "";
        for (Iterator<StudyAuthor> it = getStudy().getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor sa = it.next();
            if (!StringUtil.isEmpty(sa.getName())) {
                if (str != "") {
                    str += "; ";
                }
                str += sa.getName();
                
            }
        }
        return str;
        
    }
    
    public String getAuthorAffiliations() {
        String str = "";
        boolean hasAffiliation = false;
        
        for (Iterator<StudyAuthor> it = getStudy().getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor sa = it.next();
            if (!StringUtil.isEmpty(sa.getName())) {
                if (str != "") {
                    str += "; ";
                }
                str += sa.getName();
                
            }
            if (!StringUtil.isEmpty(sa.getAffiliation())) {
                hasAffiliation = true;
                str += " (" + sa.getAffiliation() + ")";
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
        String str = "";
        for (Iterator<StudyAbstract> it = getStudy().getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem = it.next();
            if (!StringUtil.isEmpty(elem.getText())) {
                str += "<p>" + elem.getText() + "</p>";
            }
            
        }
        return str;
    }
    
    public String getDistributorContact() {
        String str = "";
        if (!StringUtil.isEmpty(getStudy().getDistributorContact())) {
            str += getStudy().getDistributorContact();
        }
        if (!StringUtil.isEmpty(getStudy().getDistributorContactAffiliation())) {
            str += " (" + getStudy().getDistributorContactAffiliation() + ")";
        }
        if (!StringUtil.isEmpty(getStudy().getDistributorContactEmail())) {
            if (str != "") {
                str += ", ";
            }
            str += getStudy().getDistributorContactEmail();
        }
        /*"Distributor Contact (affiliation), e-mail"*/
        return str;
    }
    
    public String getSeries() {
        String str = "";
        if (!StringUtil.isEmpty(getStudy().getSeriesName())) {
            str += getStudy().getSeriesName();
        }
        if (!StringUtil.isEmpty(getStudy().getSeriesInformation())) {
            if (str != "") {
                str += ", ";
            }
            str += getStudy().getSeriesInformation();
        }
        return str;
    }
    
    public String getStudyVersion() {
        String str = "";
        if (!StringUtil.isEmpty(getStudy().getStudyVersion())) {
            str += getStudy().getStudyVersion();
        }
        if (!StringUtil.isEmpty(getStudy().getVersionDate())) {
            if (str != "") {
                str += ", ";
            }
            str += reformatDate(getStudy().getVersionDate());
        }
        return str;
    }
    
    public String getTimePeriodCovered() {
        String str = "";
        if (!StringUtil.isEmpty(getStudy().getTimePeriodCoveredStart())) {
            str += reformatDate(getStudy().getTimePeriodCoveredStart());
        }
        if (!StringUtil.isEmpty(getStudy().getTimePeriodCoveredEnd())) {
            if (str != "") {
                str += " - ";
            }
            str += reformatDate(getStudy().getTimePeriodCoveredEnd());
        }
        return str;
        
    }
    
    public String getProductionDate() {
        return reformatDate(getStudy().getProductionDate());
    }
    
    public String getDistributionDate() {
        return reformatDate(getStudy().getDistributionDate());
    }
    
    public String getDateOfDeposit() {
        return reformatDate(getStudy().getDateOfDeposit());
    }
    
    private String reformatDate(String dateString) {
         
        Date date = DvnDate.convertFromPattern(dateString,"yyyy-MM-dd");
        if (date!=null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMMMM dd, yyyy");
            return formatter.format(date);
        }
        date = DvnDate.convertFromPattern(dateString,"yyyy-MM");
        if (date!=null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMMMM, yyyy");
            return formatter.format(date);
        }
             
        return dateString;
        
    }
    
    public String getDateOfCollection() {
        String str = "";
        if (!StringUtil.isEmpty(getStudy().getDateOfCollectionStart())) {
            str += reformatDate(getStudy().getDateOfCollectionStart());
        }
        if (!StringUtil.isEmpty(getStudy().getDateOfCollectionEnd())) {
            if (str != "") {
                str += " - ";
            }
            str += reformatDate(getStudy().getDateOfCollectionEnd());
        }
        return str;
    }
    
    public String getTruncatedAbstracts() {
        String abstracts = getAbstracts();
        String truncatedAbstracts = StringUtil.truncateString(abstracts, 200);
        
        return truncatedAbstracts;
    }
    
    public boolean isRenderAbstractsMoreLink() {
        String abstracts = getAbstracts();
        String truncatedAbstracts = StringUtil.truncateString(abstracts, 200);
        
        return !truncatedAbstracts.equals(abstracts);
    }
    
    public boolean isFiles() {
        for (Iterator<FileCategory> it = getStudy().getFileCategories().iterator(); it.hasNext();) {
            if (it.next().getStudyFiles().size() > 0) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isSubsettable() {
        for (Iterator<FileCategory> it = getStudy().getFileCategories().iterator(); it.hasNext();) {
            for (Iterator<StudyFile> fit = it.next().getStudyFiles().iterator(); fit.hasNext();) {
                if (fit.next().isSubsettable()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean isNonSubsettable() {
        for (Iterator<FileCategory> it = getStudy().getFileCategories().iterator(); it.hasNext();) {
            for (Iterator<StudyFile> fit = it.next().getStudyFiles().iterator(); fit.hasNext();) {
                if (!fit.next().isSubsettable()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public String getProducers() {
        String str = "";
        for (Iterator<StudyProducer> it = getStudy().getStudyProducers().iterator(); it.hasNext();) {
            StudyProducer elem = it.next();
            if (!StringUtil.isEmpty(elem.getName())) {
                if (str != "") {
                    str += "; ";
                }
                if (!StringUtil.isEmpty(elem.getUrl())) {
                    str += "<a href='" + elem.getUrl() + "' target='_blank' title='" + elem.getName() + "'>" + elem.getName() + "</a>";
                } else {
                    str += elem.getName();
                }
                
            }
            if (!StringUtil.isEmpty(elem.getAbbreviation())) {
                str += " (" + elem.getAbbreviation() + ")";
            }
            if (!StringUtil.isEmpty(elem.getAffiliation())) {
                str += ", " + elem.getAffiliation();
            }
            if (!StringUtil.isEmpty(elem.getLogo())) {
                str += " <img src='" + elem.getLogo() + "' height='30px' alt='Logo' title='Logo' />";
            }
            
            
            
        }
        return str;
        
    }
    
    public String getAbstractDates() {
        String str = "";
        for (Iterator<StudyAbstract> it = getStudy().getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem = it.next();
            if (!StringUtil.isEmpty(elem.getDate())) {
                if (str != "") {
                    str += "; ";
                }
                str += reformatDate(elem.getDate());
            }
            
        }
        return str;
        
    }
    
    public String getNotes() {
        String str = "";
        for (Iterator<StudyNote> it = getStudy().getStudyNotes().iterator(); it.hasNext();) {
            StudyNote elem = it.next();
            if (elem.getType()==null || !elem.getType().equals(DDIServiceBean.NOTE_TYPE_TERMS_OF_USE)) {
                if (!StringUtil.isEmpty(elem.getType())) {
                    if (str != "") {
                        str += "; ";
                    }
                    str += elem.getType();
                }
                if (!StringUtil.isEmpty(elem.getSubject())) {
                    str += " (" + elem.getSubject() + ")";
                }
                if (!StringUtil.isEmpty(elem.getText())) {
                    str += " " + elem.getText();
                }
            }
            
        }
        return str;
    }
    
    public String getRelPublications() {
        String str = "";
        for (Iterator<StudyRelPublication> it = getStudy().getStudyRelPublications().iterator(); it.hasNext();) {
            StudyRelPublication elem = it.next();
            if (!StringUtil.isEmpty(elem.getText())) {
                if (str != "") {
                    str += "; ";
                }
                str += elem.getText();
            }
            
        }
        return str;
    }
    
    public String getRelMaterials() {
        String str = "";
        for (Iterator<StudyRelMaterial> it = getStudy().getStudyRelMaterials().iterator(); it.hasNext();) {
            StudyRelMaterial elem = it.next();
            if (!StringUtil.isEmpty(elem.getText())) {
                if (str != "") {
                    str += "; ";
                }
                str += elem.getText();
            }
            
        }
        return str;
    }
    
    public String getRelStudies() {
        String str = "";
        for (Iterator<StudyRelStudy> it = getStudy().getStudyRelStudies().iterator(); it.hasNext();) {
            StudyRelStudy elem = it.next();
            if (!StringUtil.isEmpty(elem.getText())) {
                if (str != "") {
                    str += "; ";
                }
                str += elem.getText();
            }
            
        }
        return str;
    }
    
    public String getOtherRefs() {
        String str = "";
        for (Iterator<StudyOtherRef> it = getStudy().getStudyOtherRefs().iterator(); it.hasNext();) {
            StudyOtherRef elem = it.next();
            if (!StringUtil.isEmpty(elem.getText())) {
                if (str != "") {
                    str += "; ";
                }
                str += elem.getText();
            }
            
        }
        return str;
    }
    
    public String getSoftware() {
        String str = "";
        for (Iterator<StudySoftware> it = getStudy().getStudySoftware().iterator(); it.hasNext();) {
            StudySoftware ss = it.next();
            if (!StringUtil.isEmpty(ss.getName())) {
                if (str != "") {
                    str += "; ";
                }
                str += ss.getName();
            }
            if (!StringUtil.isEmpty(ss.getSoftwareVersion())) {
                str += ", " + ss.getSoftwareVersion();
            }
        }
        return str;
    }
    
    public String getGrants() {
        String str = "";
        for (Iterator<StudyGrant> it = getStudy().getStudyGrants().iterator(); it.hasNext();) {
            StudyGrant elem = it.next();
            if (!StringUtil.isEmpty(elem.getNumber())) {
                if (str != "") {
                    str += "; ";
                }
                str += elem.getNumber();
                if (!StringUtil.isEmpty(elem.getAgency())) {
                    str += ", ";
                }
            }
            if (!StringUtil.isEmpty(elem.getAgency())) {
                str += elem.getAgency();
            }
            
        }
        return str;
    }
    
    public String getOtherIds() {
        String str = "";
        for (Iterator<StudyOtherId> it = getStudy().getStudyOtherIds().iterator(); it.hasNext();) {
            StudyOtherId elem = it.next();
            if (!StringUtil.isEmpty(elem.getAgency())) {
                if (str != "") {
                    str += "; ";
                }
                str += elem.getAgency();
                if (elem.getAgency() != null) {
                    str += ": ";
                }
            }
            if (!StringUtil.isEmpty(elem.getOtherId())) {
                str += elem.getOtherId();
            }
            
        }
        return str;
    }
    
    public String getGeographicBoundings() {
        String str = "";
        for (Iterator it = getStudy().getStudyGeoBoundings().iterator(); it.hasNext();) {
            StudyGeoBounding elem = (StudyGeoBounding) it.next();
            String boundingStr = "";
            if (!StringUtil.isEmpty(elem.getWestLongitude())) {
                boundingStr += "West Bounding Longitude: " + elem.getWestLongitude() + " ";
            }
            if (!StringUtil.isEmpty(elem.getEastLongitude())) {
                boundingStr += "East Bounding Longitude: " + elem.getEastLongitude() + " ";
            }
            if (!StringUtil.isEmpty(elem.getNorthLatitude())) {
                boundingStr += "North Bounding Latitude: " + elem.getNorthLatitude() + " ";
            }
            if (!StringUtil.isEmpty(elem.getSouthLatitude())) {
                boundingStr += "South Bounding Latitude: " + elem.getSouthLatitude();
            }
            
            if (boundingStr != "") {
                if (str != "") {
                    str += "; ";
                }
                str += boundingStr;
            }
        }
        
        return str;
    }
    
    public String getKeywords() {
        String str = "";
        for (Iterator<StudyKeyword> it = getStudy().getStudyKeywords().iterator(); it.hasNext();) {
            StudyKeyword elem = it.next();
            if (!StringUtil.isEmpty(elem.getValue())) {
                if (str != "") {
                    str += "; ";
                }
                str += elem.getValue();
            }
            if (!StringUtil.isEmpty(elem.getVocab())) {
                if (!StringUtil.isEmpty(elem.getVocabURI())) {
                    str += "&#32; (<a href='" + elem.getVocabURI() + "' target='_blank'>" + elem.getVocab() + "</a>)";
                } else {
                    str += "&#32; (" + elem.getVocab() + ")";
                }
            }
        }
        
        return str;
    }
    
    public String getTopicClasses() {
        String str = "";
        for (Iterator<StudyTopicClass> it = getStudy().getStudyTopicClasses().iterator(); it.hasNext();) {
            StudyTopicClass elem = it.next();
            if (!StringUtil.isEmpty(elem.getValue())) {
                if (str != "") {
                    str += "; ";
                }
                str += elem.getValue();
            }
            if (!StringUtil.isEmpty(elem.getVocab())) {
                if (!StringUtil.isEmpty(elem.getVocabURI())) {
                    str += "&#32; (<a href='" + elem.getVocabURI() + "' target='_blank'>" + elem.getVocab() + "</a>)";
                } else {
                    str += "&#32; (" + elem.getVocab() + ")";
                }
            }
        }
        
        return str;
    }
    
    /**
     * If a Terms of Use StudyNote exists, return that, else return the study Owner terms of use (if it exists)
     * @return
     */
    public String getDataverseTermsOfUse() {
        if (study.getOwner().isDownloadTermsOfUseEnabled()) {
            return study.getOwner().getDownloadTermsOfUse();
        }
        return "";
    }
    
    public String getDistributors() {
        String str = "";
        for (Iterator<StudyDistributor> it = getStudy().getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor elem = it.next();
            if (!StringUtil.isEmpty(elem.getName())) {
                if (str != "") {
                    str += "; ";
                }
                
                if (!StringUtil.isEmpty(elem.getUrl())) {
                    str += "<a href='" + elem.getUrl() + "' target='_blank' title='" + elem.getName() + "'>" + elem.getName() + "</a>";
                } else {
                    str += elem.getName();
                }
                
            }
            if (!StringUtil.isEmpty(elem.getAbbreviation())) {
                str += " (" + elem.getAbbreviation() + ")";
            }
            if (!StringUtil.isEmpty(elem.getAffiliation())) {
                str += ", " + elem.getAffiliation();
            }
            if (!StringUtil.isEmpty(elem.getLogo())) {
                str += " <img src='" + elem.getLogo() + "' height='30px' alt='Logo' title='Logo' />";
            }
            
            
            
        }
        return str;
        
    }
    private boolean abstractAndScopePanelIsRendered;
    private boolean citationInformationPanelIsRendered;
    private boolean dataCollectionPanelIsRendered;
    private boolean notesPanelIsRendered;
    private boolean dataAvailPanelIsRendered;
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
    
    public boolean isDataAvailPanelIsRendered() {
        return dataAvailPanelIsRendered;
    }
    
    public void setDataAvailPanelIsRendered(boolean dataAvailPanelIsRendered) {
        this.dataAvailPanelIsRendered = dataAvailPanelIsRendered;
    }
    
    public boolean isTermsOfUsePanelIsRendered() {
        return termsOfUsePanelIsRendered;
    }
    
    public void setTermsOfUsePanelIsRendered(boolean termsOfUsePanelIsRendered) {
        this.termsOfUsePanelIsRendered = termsOfUsePanelIsRendered;
    }
    
    public void initFileCategoryUIList(VDC vdc, VDCUser user, UserGroup ipUserGroup) {
        categoryUIList = new ArrayList<FileCategoryUI>();
        StudyServiceLocal studyService = null;
        try {
            studyService = (StudyServiceLocal) new InitialContext().lookup("java:comp/env/studyService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        List files = studyService.getOrderedFilesByStudy(getStudy().getId());
        Iterator iter = files.iterator();
        FileCategoryUI catUI = null;
        while (iter.hasNext()) {
            StudyFile sf = (StudyFile) iter.next();
            if (catUI == null || !sf.getFileCategory().equals(catUI.getFileCategory())) {
                catUI = new FileCategoryUI(sf.getFileCategory());
                categoryUIList.add(catUI);
            }
            
            catUI.getStudyFileUIs().add(new StudyFileUI(sf, vdc, user, ipUserGroup));
        }
        
        Collections.sort(categoryUIList);
        
    /*
    List categories = studyService.getOrderedFileCategories(getStudy().getId());
    Iterator iter = categories.iterator();
    while (iter.hasNext()) {
    FileCategory fc = (FileCategory) iter.next();
    FileCategoryUI catUI = new FileCategoryUI(fc,vdc,user, ipUserGroup);
    categoryUIList.add(catUI);
    }
     */
        
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
    
    public static List filterVisibleStudies(List originalStudies, VDC vdc, VDCUser user, UserGroup ipUserGroup) {
        return filterVisibleStudies(originalStudies, vdc, user, ipUserGroup, -1);
    }
    
    public static List filterVisibleStudies(List originalStudies, VDC vdc, VDCUser user, UserGroup ipUserGroup, int numResults) {
        List filteredStudies = new ArrayList();
        
        if (numResults != 0) {
            int count = 0;
            Iterator iter = originalStudies.iterator();
            while (iter.hasNext()) {
                Study study = (Study) iter.next();
                if (StudyUI.isStudyVisibleToUser(study, vdc, user) || isStudyVisibleToGroup(study, vdc, ipUserGroup)) {
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
        if (study.getOwner().isRestricted() &&
                (vdc == null || !study.getOwner().getId().equals(vdc.getId()))) {
            return false;
        }
        
        // only visible if released
        if (!study.isReleased()) {
            return false;
        }
        
        // lastly check restrictions
        return !study.isStudyRestrictedForUser(vdc, user);
        
        
    }
    
    /**
     * check if this is visible for the ipgroup
     *
     * @author wbossons
     */
    public static boolean isStudyVisibleToGroup(Study study, VDC vdc, UserGroup usergroup) {
        // if restricted vdc, only visible in that VDC
        if (study.getOwner().isRestricted() &&
                (vdc == null || !study.getOwner().getId().equals(vdc.getId()))) {
            return false;
        }
        
        // only visible if released
        if (!study.isReleased()) {
            return false;
        }
        
        // lastly check restrictions
        return !study.isStudyRestrictedForGroup(usergroup);
        
    }
    /**
     * Holds value of property categoryUIList.
     */
    private List<FileCategoryUI> categoryUIList = new ArrayList<FileCategoryUI>();
    

    
    /**
     * Getter for property categoryUIList.
     * @return Value of property categoryUIList.
     */
    public List<FileCategoryUI> getCategoryUIList() {
        return this.categoryUIList;
    }
    
    /**
     * Setter for property categoryUIList.
     * @param categoryUIList New value of property categoryUIList.
     */
    public void setCategoryUIList(List<FileCategoryUI> categoryUIList) {
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
    
    public static boolean isStudyInList(Long studyId, List list) {
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Study s = (Study) iter.next();
            if ( s.getId().equals(studyId) ) {
                return true;
            }
        }
        
        return false;
    }    
    
    public int getDownloadCount() {
        int downloadCount = 0;
        for (StudyFile sf : study.getStudyFiles() ) {
            downloadCount += sf.getStudyFileActivity() != null ? sf.getStudyFileActivity().getDownloadCount() : 0;
        }
        return downloadCount;
    }

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
