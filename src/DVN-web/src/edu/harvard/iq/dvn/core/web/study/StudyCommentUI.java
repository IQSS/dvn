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

    protected StudyComment studyComment;
    protected String flaggedByUserNames = new String("");

    public StudyCommentUI(StudyComment studyComment) {
        this.studyComment = studyComment;
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


}
