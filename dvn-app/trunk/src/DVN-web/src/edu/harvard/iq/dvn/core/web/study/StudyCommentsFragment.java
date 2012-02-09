/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.icesoft.faces.component.panelpopup.PanelPopup;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyComment;
import edu.harvard.iq.dvn.core.study.StudyCommentService;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.web.common.LoginBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.login.LoginWorkflowBean;
import edu.harvard.iq.dvn.core.web.util.PlainTextValidator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author wbossons
 */

@Named ("StudyCommentsFragment")
@ViewScoped
public class StudyCommentsFragment extends VDCBaseBean implements Serializable {
    @EJB
    private StudyCommentService studyCommentService;
    @EJB
    private StudyServiceLocal studyService;
    @EJB
    private MailServiceLocal mailService;

    private List<StudyCommentUI> studyComments;
    private Long studyId;
    private Long versionNumber;
    private StudyVersion studyVersion;
    private boolean renderStudyVersionReference;

     public void init() {
        super.init();        
        if (studyId == null) {
            studyId = getVDCRequestBean().getStudyId();
        }

        if (versionNumber == null) {
            versionNumber = getVDCRequestBean().getStudyVersionNumber();
        }

        studyVersion = studyService.getStudyVersion(studyId, versionNumber);
        renderStudyVersionReference = studyVersion.getStudy().getLatestVersion().getVersionNumber() > 1;

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

      /** toggleDeletePopup
      * actionListener method for hiding
      * and showing the popup
      *
      * @param ActionEvent
      *
      */
     public void toggleDeletePopup(javax.faces.event.ActionEvent event) {
          if (deleteCommentLink.getAttributes().get("commentId") != null) {
             flaggedCommentId = new Long(deleteCommentLink.getAttributes().get("commentId").toString());
         }
         showDeletePopup = !showDeletePopup;
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
         studyCommentService.flagStudyCommentAbuse(flaggedCommentId, getVDCSessionBean().getUser().getId());
         mailService.sendMail(getVDCSessionBean().getUser().getEmail(), getVDCRequestBean().getVdcNetwork().getContactEmail(), "Study Comment Abuse Reported", "A study comment " +
                                "has been reported for abuse.  Please review the details below. " +
                                "\n\r" + "\n\r" +
                                "Study Name: " + studyVersion.getStudy().getReleasedVersion().getMetadata().getTitle() +
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
         actionComplete = true;
         showDeletePopup = !showDeletePopup;
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
         actionComplete = true;
         //cleanup
         flaggedCommentId = new Long("0");
         getVDCRequestBean().setStudyId(studyId);
         getVDCRequestBean().setSelectedTab("comments");
         studyComments = null;
     }

     public void save(ActionEvent event) {
         String value = commentsTextarea.getValue().toString();
         boolean isValidText = true;
         FacesContext context   = FacesContext.getCurrentInstance();
         UIComponent toValidate = (UIComponent)commentsTextarea;
         Object objValue        = commentsTextarea.getValue();
         PlainTextValidator validator = new PlainTextValidator();
         if (!validator.isValidTextEntry(context, toValidate, value)) {
            isValidText = false;
            context.renderResponse();
         } else if (value == null || (value.toString()).trim().isEmpty()) {
                FacesMessage message = new FacesMessage("Blank comments are not allowed. Please enter a comment to continue.");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                ((UIInput)commentsTextarea).setValid(false);
                context = FacesContext.getCurrentInstance();
                context.addMessage(commentsTextarea.getClientId(context), message);
                context.renderResponse();
                
         } else {
             studyCommentService.addComment(commentsTextarea.getValue().toString(), getVDCSessionBean().getUser().getId(), studyId);
             String truncatedComment = (commentsTextarea.getValue().toString().length() <= 25) ? commentsTextarea.getValue().toString() : commentsTextarea.getValue().toString().substring(0, 25);
             truncatedComment += "...";
             actionComplete = true;
             studyComments = null;
             commentsTextarea.setValue("");
         }
     }

     public String cancel() {
         FacesContext context   = FacesContext.getCurrentInstance();
         UIComponent toValidate = (UIComponent)commentsTextarea;
         Object value           = commentsTextarea.getValue();
         //PlainTextValidator validator = new PlainTextValidator();
         //validator.validate(context, toValidate, value);
         Iterator iterator = context.getMessages(toValidate.getClientId(context));
         while (iterator.hasNext()) {
             FacesMessage message = (FacesMessage)iterator.next();
             iterator.remove();
         }
         commentsTextarea.setValue("");
         actionComplete = false;
         return "";
     }

     public String addAccount() {
         HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
         request.setAttribute("selectedTab", "comments");
         request.setAttribute("studyId", studyId);
         LoginWorkflowBean loginworkflowbean = (LoginWorkflowBean) getBean("LoginWorkflowBean");
         String addAccount = loginworkflowbean.beginCommentsWorkflow(studyId);
         actionComplete = false;
         return addAccount;
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
                 title = studycommentui.getStudyComment().getStudyVersion().getMetadata().getTitle();
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



    public boolean getRenderStudyVersionReference() {
        return renderStudyVersionReference;
    }

     /**
     * Get the studyComments wrapper
     *
     * @return an array list of studyCommentUIs
     */
    public List<StudyCommentUI> getStudyComments() {
        if (studyComments == null) {
            studyComments = new ArrayList();
            List<StudyComment> tempStudyComments = studyCommentService.getStudyComments(studyId);
            Iterator iterator = tempStudyComments.iterator();
            while (iterator.hasNext()) {
                StudyComment studyComment     = (StudyComment)iterator.next();
                StudyCommentUI studyCommentUI = new StudyCommentUI(studyComment);
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

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public StudyVersion getStudyVersion() {
        return studyVersion;
    }

    public void setStudyVersion(StudyVersion studyVersion) {
        this.studyVersion = studyVersion;
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

    protected boolean showDeletePopup = false;

    /**
     * Get the value of showDeletePopup
     *
     * @return the value of showDeletePopup
     */
    public boolean isShowDeletePopup() {
        return showDeletePopup;
    }

    /**
     * Set the value of showDeletePopup
     *
     * @param showDeletePopup new value of showDeletePopup
     */
    public void setShowDeletePopup(boolean showDeletePopup) {
        this.showDeletePopup = showDeletePopup;
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

    protected HtmlCommandLink addAccountLink;

    /**
     * Get the value of addAccountLink
     *
     * @return the value of addAccountLink
     */
    public HtmlCommandLink getAddAccountLink() {
        return addAccountLink;
    }

    /**
     * Set the value of addAccountLink
     *
     * @param addAccountLink new value of addAccountLink
     */
    public void setAddAccountLink(HtmlCommandLink addAccountLink) {
        this.addAccountLink = addAccountLink;
    }

    protected String studyCommentsText = new String("");

    /**
     * Get the value of studyCommentsText
     *
     * @return the value of studyCommentsText
     */
    public String getStudyCommentsText() {
        return studyCommentsText;
    }

    /**
     * Set the value of studyCommentsText
     *
     * @param studyCommentsText new value of studyCommentsText
     */
    public void setStudyCommentsText(String studyCommentsText) {
        this.studyCommentsText = studyCommentsText;
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
        cancelLink = request.getProtocol().substring(0, request.getProtocol().indexOf("/")).toLowerCase() + "://" + getHostUrl() + request.getContextPath() + "/faces/networkAdmin/CommentReviewPage.xhtml";
        return cancelLink;
    }
}
