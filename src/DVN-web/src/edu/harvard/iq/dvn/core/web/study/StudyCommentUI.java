/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.StudyComment;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author wbossons
 */
public class StudyCommentUI {

    protected boolean InFlaggedByUsers;
    protected StudyComment studyComment;
    protected String flaggedByUserNames = new String("");
    protected VDCUser user = null;


    public StudyCommentUI(StudyComment studyComment) {
        this.studyComment = studyComment;
    }

    public StudyCommentUI(StudyComment studyComment, VDCUser currentUser) {
        this.studyComment = studyComment;
        this.user         = currentUser;
    }

    public String getFlaggedByUserNames() {
        List<VDCUser> vdcUsers = (List<VDCUser>)studyComment.getFlaggedByUsers();
        Iterator iterator = vdcUsers.iterator();
        while (iterator.hasNext()) {
            VDCUser vdcuser = (VDCUser)iterator.next();
            flaggedByUserNames += vdcuser.getUserName();
            if (vdcUsers.indexOf(vdcuser) > 0 && vdcUsers.indexOf(vdcuser) < vdcUsers.size()-1)
                flaggedByUserNames += ", ";
        }
        return flaggedByUserNames;
    }

    public StudyComment getStudyComment() {
        return this.studyComment;
    }



    /**
     * Get the value of InFlaggedByUsers
     *
     * @return the value of InFlaggedByUsers
     */
    public boolean isInFlaggedByUsers() {
        if (getFlaggedByUserNames().indexOf(user.getUserName()) != -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set the value of InFlaggedByUsers
     *
     * @param InFlaggedByUsers new value of InFlaggedByUsers
     */
    public void setInFlaggedByUsers(boolean InFlaggedByUsers) {
        this.InFlaggedByUsers = InFlaggedByUsers;
    }

    protected boolean reportAbuseEnabled;

    /**
     * Get the value of reportAbuseEnabled
     *
     * @return the value of reportAbuseEnabled
     */
    public boolean isReportAbuseEnabled() {
        if (studyComment.getStatus().toString().equals("FLAGGED") && isInFlaggedByUsers()) {
            reportAbuseEnabled = false;
        } else {
            reportAbuseEnabled = true;
        }
        return reportAbuseEnabled;
    }



    /**
     * Set the value of reportAbuseEnabled
     *
     * @param reportAbuseEnabled new value of reportAbuseEnabled
     */
    public void setReportAbuseEnabled(boolean reportAbuseEnabled) {
        this.reportAbuseEnabled = reportAbuseEnabled;
    }


}
