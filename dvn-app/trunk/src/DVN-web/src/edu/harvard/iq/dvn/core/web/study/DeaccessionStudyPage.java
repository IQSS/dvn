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
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;

/**
 *
 * @author gdurand
 */
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

        getVDCRequestBean().setStudyId(studyVersion.getStudy().getId());
        getVDCRequestBean().setStudyVersionNumber(null); // we don't provide a version number, so the user goes to the study deaccessioned page
        return "viewStudy";
    }

    public String cancel_action() {
        getVDCRequestBean().setStudyId(studyVersion.getStudy().getId());
        getVDCRequestBean().setStudyVersionNumber(studyVersion.getVersionNumber());
        return "viewStudy";
    }

}
