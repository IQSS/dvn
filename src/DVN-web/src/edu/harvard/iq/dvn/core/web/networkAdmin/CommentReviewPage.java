/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.networkAdmin;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import edu.harvard.iq.dvn.core.study.StudyComment;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.study.StudyCommentService;
import edu.harvard.iq.dvn.core.web.study.StudyCommentUI;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author wbossons
 */
public class CommentReviewPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB
    StudyCommentService studyCommentService;

    protected List<StudyCommentUI> commentsForReview = null;
    protected Long flaggedCommentId;

    @Override
    public void init() {
        super.init();
        getCommentsForReview();
        System.out.println("The total notifications are: " + totalNotifications);
     }

     public void deleteFlaggedComment(ActionEvent event) {
         if (deleteCommentLink.getAttributes().get("commentId") != null) {
             flaggedCommentId = new Long(deleteCommentLink.getAttributes().get("commentId").toString());
         }
         studyCommentService.deleteComment(flaggedCommentId);
         //cleanup
         flaggedCommentId = new Long("0");
         commentsForReview = null;
     }

     public void ignoreCommentFlag(ActionEvent event) {
         if (ignoreCommentFlagLink.getAttributes().get("commentId") != null) {
             flaggedCommentId = new Long(ignoreCommentFlagLink.getAttributes().get("commentId").toString());
         }
         studyCommentService.okComment(flaggedCommentId);
         //cleanup
         flaggedCommentId = new Long("0");
         commentsForReview = null;
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



}
