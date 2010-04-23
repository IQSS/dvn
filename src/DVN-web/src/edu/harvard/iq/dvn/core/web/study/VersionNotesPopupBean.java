/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import javax.faces.event.ActionEvent;

/**
 *
 * @author ekraffmiller
 */
public class VersionNotesPopupBean {

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
    
    public enum ActionType {VERSION_RELEASE, EDIT_STUDY, EDIT_NOTE, ADD_FILES, MANAGE_STUDIES };

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

    public boolean isAddFilesAction() {
        return actionType == ActionType.ADD_FILES;
    }

    public boolean isEditNoteAction() {
        return actionType == ActionType.EDIT_NOTE;
    }

    public boolean isManageStudiesAction(){
        return actionType == ActionType.MANAGE_STUDIES;
    }

}
