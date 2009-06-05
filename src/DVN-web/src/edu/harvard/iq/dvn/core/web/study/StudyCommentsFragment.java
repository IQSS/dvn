/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.icesoft.faces.component.panelpopup.PanelPopup;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyComment;
import edu.harvard.iq.dvn.core.study.StudyCommentService;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.web.common.LoginBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author wbossons
 */
public class StudyCommentsFragment extends VDCBaseBean implements Serializable {
    @EJB
    private StudyCommentService studyCommentService;
    @EJB
    private StudyServiceLocal studyService;
    @EJB
    private MailServiceLocal mailService;

    protected List<StudyCommentUI> studyComments;
    protected Long studyId = null;  // corresponds to the primary key in the table, id
    protected Long studyIdForComments = null;  // corresponds to the foreign key int he table, studyId ex. 10001
    private VDCUser user = null;

     public void init() {
        super.init();
        if (getVDCSessionBean().getUser() != null) {
            user = getVDCSessionBean().getUser();
        }
        if (studyId == null && getVDCRequestBean().getStudyId() != null) {
            studyId = getVDCRequestBean().getStudyId();
        }
     }

     /** togglePopup
      * actionListener method for hiding
      * and showing the popup
      *
      * @param ActionEvent
      *
      */
     public void togglePopup(javax.faces.event.ActionEvent event) {
         if (reportAbuseLink.getAttributes().get("commentId") != null) {
             flaggedCommentId = new Long(reportAbuseLink.getAttributes().get("commentId").toString());
         }
         showPopup = !showPopup;
         actionComplete = false;
     }


     /** reportAbuse
      *
      *  Flag a comment as abusive. Sends an email
      *  to the admin. Sets the rendered attribute to false and
      *  sets the "Flagged for abuse" output text on.
      * @param event
      * @return string indicating sucess or failure - used in navigation.
      */
     public String reportAbuse(ActionEvent event) {
         studyCommentService.flagStudyCommentAbuse(flaggedCommentId, user.getId());
         study = studyService.getStudy(studyId);
         mailService.sendMail(user.getEmail(), getVDCRequestBean().getVdcNetwork().getContactEmail(), "Study Comment Abuse Reported", "A study comment " +
                                "has been reported for abuse.  Please review the details below. " +
                                "\n\r" + "\n\r" +
                                "Study Name: " + study.getTitle() +
                                "\n" +
                                "Study Id: " + studyId +
                                "\n" +
                                "Comment Id: " + flaggedCommentId +
                                "\n" +
                                "Comment Content: " + getFlaggedStudyComment() +
                                "\n\r" + "\n\r" +
                                "Flagged comments can be reviewed and acted on at:  " +
                                getCancelLink());
         showPopup = !showPopup;
         String truncatedComment = (getFlaggedStudyComment().length() <= 25) ? getFlaggedStudyComment() : getFlaggedStudyComment().substring(0, 25);
         truncatedComment += "...";
         FacesContext context = FacesContext.getCurrentInstance();
         context.addMessage(event.getComponent().getClientId(context), new FacesMessage("Success!  The comment, " + truncatedComment + ", was flagged for abuse."));
         actionComplete = true;
         studyComments = null;
         // cleanup
         flaggedCommentId = new Long("0");
         return "done";
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
         String truncatedComment = (getFlaggedStudyComment().length() <= 25) ? getFlaggedStudyComment() : getFlaggedStudyComment().substring(0, 25);
         truncatedComment += "...";
         FacesContext context = FacesContext.getCurrentInstance();
         context.addMessage(event.getComponent().getClientId(context), new FacesMessage("Success!  The comment, " + truncatedComment + ", was deleted."));
         actionComplete = true;
          //cleanup
         flaggedCommentId = new Long("0");
         getVDCRequestBean().setStudyId(studyId);
         getVDCRequestBean().setSelectedTab("comments");
         studyComments = null;
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
         String truncatedComment = (getFlaggedStudyComment().length() <= 25) ? getFlaggedStudyComment() : getFlaggedStudyComment().substring(0, 25);
         truncatedComment += "...";
         FacesContext context = FacesContext.getCurrentInstance();
         context.addMessage(event.getComponent().getClientId(context), new FacesMessage("Success!  The comment, " + truncatedComment + ", was reset to OK."));
         actionComplete = true;
         //cleanup
         flaggedCommentId = new Long("0");
         getVDCRequestBean().setStudyId(studyId);
         getVDCRequestBean().setSelectedTab("comments");
         studyComments = null;
     }

     public void save(ActionEvent event) {
         studyCommentService.addComment(commentsTextarea.getValue().toString(), user.getId(), studyId);
         String truncatedComment = (commentsTextarea.getValue().toString().length() <= 25) ? commentsTextarea.getValue().toString() : commentsTextarea.getValue().toString().substring(0, 25);
         truncatedComment += "...";
         FacesContext context = FacesContext.getCurrentInstance();
         context.addMessage(event.getComponent().getClientId(context), new FacesMessage("Success!  The comment, " + truncatedComment + ", was saved."));
         actionComplete = true;
         studyComments = null;
         commentsTextarea.setValue("");
     }

     public String cancel() {
         commentsTextarea.setValue("");
         actionComplete = false;
         return "cancelStudyComment";
     }

     // getters and setters
     protected String getFlaggedStudyComment() {
         String comment = new String("");
         Iterator iterator = studyComments.iterator();
         while (iterator.hasNext()) {
             StudyCommentUI studycommentui = (StudyCommentUI)iterator.next();
             if (studycommentui.getStudyComment().getId().equals(flaggedCommentId)) {
                 comment = studycommentui.getStudyComment().getComment();
                 break;
             }
         }
         return comment;
     }

      protected String getFlaggedStudyTitle() {
         String title = new String("");
         Iterator iterator = studyComments.iterator();
         while (iterator.hasNext()) {
             StudyCommentUI studycommentui = (StudyCommentUI)iterator.next();
             if (studycommentui.getStudyComment().getId().equals(flaggedCommentId)) {
                 title = studycommentui.getStudyComment().getStudy().getTitle();
                 break;
             }
         }
         return title;
     }

    protected Long flaggedCommentId;

    /**
     * Get the value of flaggedCommentId
     *
     * @return the value of flaggedCommentId
     */
    public Long getFlaggedCommentId() {
        return flaggedCommentId;
    }

    /**
     * Set the value of flaggedCommentId
     *
     * @param flaggedCommentId new value of flaggedCommentId
     */
    public void setFlaggedCommentId(Long flaggedCommentId) {
        this.flaggedCommentId = flaggedCommentId;
    }


    protected Study study;

    /**
     * Get the value of study
     *
     * @return the value of study
     */
    public Study getStudy() {
        return study;
    }

    /**
     * Set the value of study
     *
     * @param study new value of study
     */
    public void setStudy(Study study) {
        this.study = study;
    }


     /**
     * Get the studyComments wrapper
     *
     * @return an array list of studyComments
     */
    public List<StudyCommentUI> getStudyComments() {
        if (studyComments == null) {
            studyComments = new ArrayList();
            List<StudyComment> tempStudyComments = studyCommentService.getStudyComments(studyId);
            Iterator iterator = tempStudyComments.iterator();
            while (iterator.hasNext()) {
                StudyComment studyComment     = (StudyComment)iterator.next();
                StudyCommentUI studyCommentUI = new StudyCommentUI(studyComment, user);
                studyComments.add(studyCommentUI);
            }
        }
        return studyComments;
    }

    // end example.

    /**
     * Set the value of studyComments
     *
     * @param studyComments new value of studyComments
     */
    public void setStudyComments(List<StudyCommentUI> studyComments) {
        this.studyComments = studyComments;
    }

    /**
     * Get the value of studyId
     *
     * @return the value of studyId
     */
    public Long getStudyId() {
        return studyId;
    }

    /**
     * Set the value of studyId
     *
     * @param studyId new value of studyId
     */
    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    protected PanelPopup reportPopup;

    /**
     * Get the value of reportPopup
     *
     * @return the value of reportPopup
     */
    public PanelPopup getReportPopup() {
        return reportPopup;
    }

    /**
     * Set the value of reportPopup
     *
     * @param reportPopup new value of reportPopup
     */
    public void setReportPopup(PanelPopup reportPopup) {
        this.reportPopup = reportPopup;
    }

    protected boolean showPopup = false;

    /**
     * Get the value of showPopup
     *
     * @return the value of showPopup
     */
    public boolean isShowPopup() {
        return showPopup;
    }

    /**
     * Set the value of showPopup
     *
     * @param showPopup new value of showPopup
     */
    public void setShowPopup(boolean showPopup) {
        this.showPopup = showPopup;
    }

    protected HtmlCommandLink reportAbuseLink = new HtmlCommandLink();

    /**
     * Get the value of reportAbuseLink
     *
     * @return the value of reportAbuseLink
     */
    public HtmlCommandLink getReportAbuseLink() {
        return reportAbuseLink;
    }

    /**
     * Set the value of reportAbuseLink
     *
     * @param reportAbuseLink new value of reportAbuseLink
     */
    public void setReportAbuseLink(HtmlCommandLink reportAbuseLink) {
        this.reportAbuseLink = reportAbuseLink;
    }

    public VDCUser getUser() {
        return user;
    }

    public void setUser(VDCUser user) {
        this.user = user;
    }

    protected HtmlInputTextarea commentsTextarea;

    /**
     * Get the value of commentsTextarea
     *
     * @return the value of commentsTextarea
     */
    public HtmlInputTextarea getCommentsTextarea() {
        return commentsTextarea;
    }

    /**
     * Set the value of commentsTextarea
     *
     * @param commentsTextarea new value of commentsTextarea
     */
    public void setCommentsTextarea(HtmlInputTextarea commentsTextarea) {
        this.commentsTextarea = commentsTextarea;
    }

    protected HtmlCommandLink deleteCommentLink = new HtmlCommandLink();

    /**
     * Get the value of deleteCommentLink
     * Network admin and also the comment creator can
     * delete comments.
     *
     * @return the value of deleteCommentLink
     */
    public HtmlCommandLink getDeleteCommentLink() {
        return deleteCommentLink;
    }

    /**
     * Set the value of deleteCommentLink
     *
     * @param deleteFlaggedCommentLink new value of deleteCommentLink
     */
    public void setDeleteCommentLink(HtmlCommandLink deleteCommentLink) {
        this.deleteCommentLink = deleteCommentLink;
    }

    protected HtmlCommandLink ignoreCommentFlagLink = new HtmlCommandLink();

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

    protected boolean authorizedToDeleteComment = false;

    /**
     * Get the value of authorizedToDeleteFlaggedComment
     *  The network admin and the comment creator can delete comments
     *
     * @return the value of authorizedToDeleteComment
     */
    public boolean isAuthorizedToDeleteComment() {
        LoginBean loginBean = getVDCSessionBean().getLoginBean();
        if (loginBean != null && loginBean.isNetworkAdmin()) {
            authorizedToDeleteComment = true;
        }
        return authorizedToDeleteComment;
    }

    /**
     * Set the value of authorizedToDeleteComment
     *
     * @param authorizedToDelete new value of authorizedToDeleteComment
     */
    public void setAuthorizedToDeleteComment(boolean authorizedToDeleteComment) {
        this.authorizedToDeleteComment = authorizedToDeleteComment;
    }

    protected boolean authorizedToIgnoreFlag = false;

    /**
     * Get the value of authorizedToIgnoreFlag
     * Only network admins can see the ignore flag.
     *
     * @return the value of authorizedToIgnoreFlag
     */
    public boolean isAuthorizedToIgnoreFlag() {
        LoginBean loginBean = getVDCSessionBean().getLoginBean();
        if (loginBean != null && loginBean.isNetworkAdmin()) {
            authorizedToIgnoreFlag = true;
        }
        return authorizedToIgnoreFlag;
    }

    /**
     * Set the value of authorizedToIgnoreFlag
     *
     * @param authorizedToIgnoreFlag new value of authorizedToIgnoreFlag
     */
    public void setAuthorizedToIgnoreFlag(boolean authorizedToIgnoreFlag) {
        this.authorizedToIgnoreFlag = authorizedToIgnoreFlag;
    }

    protected boolean actionComplete = false;

    /**
     * Get the value of actionComplete
     *
     * @return the value of actionComplete
     */
    public boolean isActionComplete() {
        return actionComplete;
    }

    /**
     * Set the value of actionComplete
     *
     * @param actionComplete new value of actionComplete
     */
    public void setActionComplete(boolean actionComplete) {
        this.actionComplete = actionComplete;
    }




    /* utils */
    /**
     *  for url of dataverse home page.
     * @return hostUrl (based on inet Address)
     */
    private String getHostUrl() {
        return PropertyUtil.getHostUrl();
    }

    private String getCancelLink() {
        String cancelLink = new String("");
        FacesContext context            = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpServletRequest request      = (HttpServletRequest) externalContext.getRequest();
        cancelLink = request.getProtocol().substring(0, request.getProtocol().indexOf("/")).toLowerCase() + "://" + getHostUrl() + request.getContextPath() + getVDCRequestBean().getCurrentVDCURL() + "/faces/networkAdmin/CommentReviewPage.xhtml";
        return cancelLink;
    }

}
