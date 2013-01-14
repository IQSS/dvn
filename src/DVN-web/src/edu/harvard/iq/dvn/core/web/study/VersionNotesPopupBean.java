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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.StudyVersion;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

/**
 *
 * @author ekraffmiller
 */
@Named("VersionNotesPopupBean")
@ViewScoped
public class VersionNotesPopupBean implements Serializable {

    /**
     * @return the notes
     */
    public String getVersionNote() {
        return versionNote;
    }

    /**
     * @param notes the notes to set
     */
    public void setVersionNote(String versionNote) {
        this.versionNote = versionNote;
    }
    
    public enum ActionType {VERSION_RELEASE, EDIT_STUDY, EDIT_NOTE, ADD_FILES, MANAGE_STUDIES, EDIT_STUDY_FILES };

    private ActionType actionType;

    private String versionNote;


    boolean showPopup;

    public boolean isShowPopup() {
        return showPopup;
    }

    public void setShowPopup(boolean showPopup) {
        this.showPopup = showPopup;
    }

    public void openPopup(ActionEvent ae) {
        showPopup = true;
    }

    public void closePopup(ActionEvent ae) {
        showPopup = false;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public void setActionTypeStr(String actionTypeStr){
        this.actionType = ActionType.valueOf(actionTypeStr);
    }

    public boolean isVersionReleaseAction() {
        return actionType == ActionType.VERSION_RELEASE;
    }

    public boolean isEditStudyAction(){
        return actionType == ActionType.EDIT_STUDY;
    }

    public boolean isEditStudyFilesAction(){
        return actionType == ActionType.EDIT_STUDY_FILES;
    }

    public boolean isAddFilesAction() {
        return actionType == ActionType.ADD_FILES;
    }

    public boolean isEditNoteAction() {
        return actionType == ActionType.EDIT_NOTE;
    }

    public boolean isManageStudiesAction(){
        return actionType == ActionType.MANAGE_STUDIES;
    }


    public int getVersionNoteMaxLength() {
        return StudyVersion.VERSION_NOTE_MAX_LENGTH;
    }

     public void validateVersionNote(FacesContext context,
            UIComponent toValidate,
            Object value) {

        String strValue = (String) value;
        if (strValue.length() > StudyVersion.VERSION_NOTE_MAX_LENGTH) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Study Version Notes cannot exceed "+StudyVersion.VERSION_NOTE_MAX_LENGTH+" characters.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
  

}
