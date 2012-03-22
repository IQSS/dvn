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
package edu.harvard.iq.dvn.core.web.networkAdmin;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlDataTable;
import edu.harvard.iq.dvn.core.study.StudyComment;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.study.StudyCommentService;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.study.StudyCommentUI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

/**
 *
 * @author wbossons
 */
@ViewScoped
@Named("CommentReviewPage")
public class CommentReviewPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB
    StudyCommentService studyCommentService;
    VDCServiceLocal vdcService;

    protected List<StudyCommentUI> commentsForReview = null;
    protected Long flaggedCommentId;

    @Override
    public void init() {
        super.init();
        getVDCRequestBean().setSelectedTab("comments");
        getCommentsForReview();
     }

     public void deleteFlaggedComment(ActionEvent event) {
         if (deleteCommentLink.getAttributes().get("commentId") != null) {
             flaggedCommentId = new Long(deleteCommentLink.getAttributes().get("commentId").toString());
         }
         String deletedMessage = "You reported as abusive a comment in the study titled, " +
                                getFlaggedStudyTitle() + ". " + "\n" +
                                "The comment was, \"" + getFlaggedStudyComment() + "\". " + "\n" +
                               "This comment was deleted in accordance with the " +
                                "study comments terms of use.";
         studyCommentService.deleteComment(flaggedCommentId, deletedMessage);
         getVDCRenderBean().getFlash().put("successMessage","Successfully deleted the flagged comment.");
         //cleanup
         flaggedCommentId  = new Long("0");
         commentsForReview = null;
     }

     public void ignoreCommentFlag(ActionEvent event) {
         if (ignoreCommentFlagLink.getAttributes().get("commentId") != null) {
             flaggedCommentId = new Long(ignoreCommentFlagLink.getAttributes().get("commentId").toString());
         }
         String okMessage = "You reported as abusive a comment in the study titled, " +
                                getFlaggedStudyTitle() + ". " + "\n" +
                                "The comment was, \"" + getFlaggedStudyComment() + "\". " + "\n" +
                                "According to the terms of use of this study, the " +
                                "reported comment is not an abuse. This comment will remain posted, and will " +
                                "no longer appear to you as reported.";
         studyCommentService.okComment(flaggedCommentId, okMessage);
         getVDCRenderBean().getFlash().put("successMessage","Successfully ignored the flagged comment.");
         //cleanup
         flaggedCommentId  = new Long("0");
         commentsForReview = null;
     }

    protected HtmlCommandLink goToCommentsLink;

    /**
     * Get the value of goToCommentsLink
     *
     * @return the value of goToCommentsLink
     */
    public HtmlCommandLink getGoToCommentsLink() {
        return goToCommentsLink;
    }

    /**
     * Set the value of goToCommentsLink
     *
     * @param goToCommentsLink new value of goToCommentsLink
     */
    public void setGoToCommentsLink(HtmlCommandLink goToCommentsLink) {
        this.goToCommentsLink = goToCommentsLink;
    }


    /**
     * Get the value of commentsForReview
     *
     * @return the value of commentsForReview
     */
    public List<StudyCommentUI> getCommentsForReview() {
        if (commentsForReview == null) {
            commentsForReview = new ArrayList();
            List<StudyComment> tempCommentsForReview = studyCommentService.getAbusiveStudyComments();
            Iterator iterator = tempCommentsForReview.iterator();
            while (iterator.hasNext()) {
                StudyComment studyComment     = (StudyComment)iterator.next();
                StudyCommentUI studyCommentUI = new StudyCommentUI(studyComment);
                commentsForReview.add(studyCommentUI);
            }
            totalNotifications = new Long(Integer.toString(commentsForReview.size()));
        }
        return commentsForReview;
    }

    /**
     * Set the value of commentsForReview
     *
     * @param commentsForReview new value of commentsForReview
     */
    public void setCommentsForReview(List<StudyCommentUI> commentsForReview) {
        this.commentsForReview = commentsForReview;
    }

    protected HtmlCommandLink deleteCommentLink;

    /**
     * Get the value of deleteCommentLink
     *
     * @return the value of deleteCommentLink
     */
    public HtmlCommandLink getDeleteCommentLink() {
        return deleteCommentLink;
    }

    /**
     * Set the value of deleteCommentLink
     *
     * @param deleteCommentLink new value of deleteCommentLink
     */
    public void setDeleteCommentLink(HtmlCommandLink deleteCommentLink) {
        this.deleteCommentLink = deleteCommentLink;
    }

    protected HtmlCommandLink ignoreCommentFlagLink;

    /**
     * Get the value of ignoreCommentFlagLink
     *
     * @return the value of ignoreCommentFlagLink
     */
    public HtmlCommandLink getIgnoreCommentFlagLink() {
        return ignoreCommentFlagLink;
    }

    /**
     * Set the value of ignoreCommentFlagLink
     *
     * @param ignoreCommentFlagLink new value of ignoreCommentFlagLink
     */
    public void setIgnoreCommentFlagLink(HtmlCommandLink ignoreCommentFlagLink) {
        this.ignoreCommentFlagLink = ignoreCommentFlagLink;
    }

    protected Long totalNotifications;

    /**
     * Get the value of totalNotifications
     *
     * @return the value of totalNotifications
     */
    public Long getTotalNotifications() {
        return totalNotifications;
    }

    /**
     * Set the value of totalNotifications
     *
     * @param totalNotifications new value of totalNotifications
     */
    public void setTotalNotifications(Long totalNotifications) {
        this.totalNotifications = totalNotifications;
    }

    // getters and setters
     protected String getFlaggedStudyComment() {
         String comment = new String("");
         Iterator iterator = commentsForReview.iterator();
         while (iterator.hasNext()) {
             StudyCommentUI studycommentui = (StudyCommentUI)iterator.next();
             //debug remove this

             if (studycommentui.getStudyComment().getId().equals(flaggedCommentId)) {
                 comment = studycommentui.getStudyComment().getComment();
                 break;
             }
         }
         return comment;
     }

     // getters and setters
     protected String getFlaggedStudyTitle() {
         String title = new String("");
         Iterator iterator = commentsForReview.iterator();
         while (iterator.hasNext()) {
             StudyCommentUI studycommentui = (StudyCommentUI)iterator.next();
             if (studycommentui.getStudyComment().getId().equals(flaggedCommentId)) {
               
                 title = studycommentui.getStudyComment().getStudyVersion().getStudy().getReleasedVersion().getMetadata().getTitle();
                 break;
             }
         }
         return title;
     }

     


    protected HtmlDataTable mainDataTable;

    /**
     * Get the value of mainDataTable
     *
     * @return the value of mainDataTable
     */
    public HtmlDataTable getMainDataTable() {
        return mainDataTable;
    }

    /**
     * Set the value of mainDataTable
     *
     * @param mainDataTable new value of mainDataTable
     */
    public void setMainDataTable(HtmlDataTable mainDataTable) {
        this.mainDataTable = mainDataTable;
    }

    
}
