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
 * DeaccessionStudyPage.java
 *
 * Created on March 19, 2010, 1:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.GlobalId;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author gdurand
 */
@Named("DeaccessionStudyPage")
@ViewScoped
public class DeaccessionStudyPage extends VDCBaseBean implements java.io.Serializable  {

    @EJB StudyServiceLocal studyService;

    /** Creates a new instance of VariablePage */
    public DeaccessionStudyPage() {
    }

    private Long studyId;
    private StudyVersion studyVersion;
    private boolean updateDeaccessionDetails = false;

    private String deaccessionLinkAuthority;
    private String deaccessionLinkStudyId;
    private StudyUI studyUI;

    public void init() {
        super.init();

        // we need to create the studyServiceBean
        if (studyId != null) {
            Study study = studyService.getStudy(studyId);
            studyUI = new StudyUI(study);
            studyVersion = study.getReleasedVersion();
            if (studyVersion == null) {
                studyVersion = study.getDeaccessionedVersion();                
                updateDeaccessionDetails = true;
            }

            // populate the deaccessionLink fields
            if (studyVersion.getDeaccessionLink() != null) {
                deaccessionLinkAuthority = studyVersion.getDeaccessionLinkAsGlobalId().getAuthority();
                deaccessionLinkStudyId = studyVersion.getDeaccessionLinkAsGlobalId().getStudyId();
            }

        } else {
            // WE SHOULD HAVE A STUDY ID, throw an error
            System.out.println("ERROR: in DeaccessionStudyPage, without a studyId");
        }

    }

    public StudyUI getStudyUI(){
        return studyUI;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public StudyVersion getStudyVersion() {
        return studyVersion;
    }

    public void setStudyVersion(StudyVersion studyVersion) {
        this.studyVersion = studyVersion;
    }

    public boolean isUpdateDeaccessionDetails() {
        return updateDeaccessionDetails;
    }

    public void setUpdateDeaccessionDetails(boolean updateDeaccessionDetails) {
        this.updateDeaccessionDetails = updateDeaccessionDetails;
    }

    public String getDeaccessionLinkAuthority() {
        return deaccessionLinkAuthority;
    }

    public void setDeaccessionLinkAuthority(String deaccessionLinkAuthority) {
        this.deaccessionLinkAuthority = deaccessionLinkAuthority;
    }

    public String getDeaccessionLinkStudyId() {
        return deaccessionLinkStudyId;
    }

    public void setDeaccessionLinkStudyId(String deaccessionLinkStudyId) {
        this.deaccessionLinkStudyId = deaccessionLinkStudyId;
    }

  

    public int getArchiveNoteMaxLength() {
        return StudyVersion.ARCHIVE_NOTE_MAX_LENGTH;
    }

    public void validateArchiveNote(FacesContext context,
            UIComponent toValidate,
            Object value) {

        String strValue = (String) value;
        if (strValue.length() > StudyVersion.ARCHIVE_NOTE_MAX_LENGTH) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Deaccession comment cannot exceed "+StudyVersion.ARCHIVE_NOTE_MAX_LENGTH+" characters.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }

    // needed for validation
    private javax.faces.component.html.HtmlInputText inputDeaccessionLinkAuthority;

    public HtmlInputText getInputDeaccessionLinkAuthority() {
        return inputDeaccessionLinkAuthority;
    }

    public void setInputDeaccessionLinkAuthority(HtmlInputText inputDeaccessionLinkAuthority) {
        this.inputDeaccessionLinkAuthority = inputDeaccessionLinkAuthority;
    }


    public void validateDeaccessionLink(FacesContext context, UIComponent toValidate, Object value) {
        // we want to validate that the user has either filled in both fields or neither field
        String linkStudyId = (String) value;
        String linkAuthority = (String) inputDeaccessionLinkAuthority.getLocalValue();

        if ( (StringUtil.isEmpty(linkStudyId) && !StringUtil.isEmpty(linkAuthority)) ||
             (!StringUtil.isEmpty(linkStudyId) && StringUtil.isEmpty(linkAuthority)) ) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Deaccession link must contain both an authority and a study Id.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
        
    public String save_action() {
        if ( !StringUtil.isEmpty(deaccessionLinkAuthority) && !StringUtil.isEmpty(deaccessionLinkStudyId) ) {
            studyVersion.setDeaccessionLink( new GlobalId("hdl", deaccessionLinkAuthority, deaccessionLinkStudyId).toString());
        } else {
            studyVersion.setDeaccessionLink(null);
        }

        if (updateDeaccessionDetails) {
            studyService.updateStudyVersion(studyVersion);
        } else {
            studyService.deaccessionStudy(studyVersion);
        }

        // we don't provide a version number, so the user goes to the study deaccessioned page
        return "/study/StudyPage?faces-redirect=true&studyId=" + studyVersion.getStudy().getId() + getContextSuffix();
    }

    public String cancel_action() {       
        return "/study/StudyPage?faces-redirect=true&studyId=" + studyVersion.getStudy().getId() + "&versionNumber=" + studyVersion.getVersionNumber() + getContextSuffix();
    }

}
