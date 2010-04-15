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
    
    enum ActionType {VERSION_RELEASE, EDIT_STUDY, EDIT_NOTE, ADD_FILES };

    private ActionType actionType;


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

    public boolean isVersionReleaseAction() {
        return actionType == ActionType.VERSION_RELEASE;
    }

    public boolean isAddFilesAction() {
        return actionType == ActionType.ADD_FILES;
    }

    public boolean isEditNoteAction() {
        return actionType == ActionType.EDIT_NOTE;
    }

}
