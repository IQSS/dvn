/*
 * StudyFileUI.java
 *
 * Created on January 25, 2007, 2:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.vdc.VDC;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyFileUI {
    
    /** Creates a new instance of StudyFileUI */
    public StudyFileUI() {
    }
    
    public StudyFileUI(StudyFile studyFile, VDC vdc, VDCUser user, UserGroup ipUserGroup) {
        this.studyFile=studyFile;
        this.restrictedForUser = studyFile.isFileRestrictedForUser(user,vdc, ipUserGroup);
    }

    /**
     * Holds value of property studyFile.
     */
    private StudyFile studyFile;

    /**
     * Getter for property studyFile.
     * @return Value of property studyFile.
     */
    public StudyFile getStudyFile() {
        return this.studyFile;
    }

    /**
     * Setter for property studyFile.
     * @param studyFile New value of property studyFile.
     */
    public void setStudyFile(StudyFile studyFile) {
        this.studyFile = studyFile;
    }

    /**
     * Holds value of property restrictedForUser.
     */
    private boolean restrictedForUser;

    /**
     * Getter for property restrictedForUser.
     * @return Value of property restrictedForUser.
     */
    public boolean isRestrictedForUser() {
        return this.restrictedForUser;
    }

    /**
     * Setter for property restrictedForUser.
     * @param restrictedForUser New value of property restrictedForUser.
     */
    public void setRestrictedForUser(boolean restrictedForUser) {
        this.restrictedForUser = restrictedForUser;
    }
    
}
