/*
 * MailServiceBean.java
 *
 * Created on November 15, 2006, 8:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.mail;

import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class MailServiceBean implements edu.harvard.hmdc.vdcnet.mail.MailServiceLocal {
    
    /**
     * Creates a new instance of MailServiceBean
     */
    public MailServiceBean() {
    }
    
    public void sendMail(String host, String from, String to, String subject, String messageText) {
        Properties props = System.getProperties(  );
        props.put("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(props, null);
        
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to, false));
            msg.setSubject(subject);
            msg.setText(messageText);
            Transport.send(msg);
        } catch (AddressException ae) {
            ae.printStackTrace(System.out);
        } catch (MessagingException me) {
            me.printStackTrace(System.out);
        }
    }
    
    @Resource(name="mail/notifyMailSession")
    private Session session;
    public void sendDoNotReplyMail(String to, String subject, String messageText){
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom();
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to, false));
            msg.setSubject(subject);
            msg.setText(messageText+"\n\nPlease do not reply to this email.\n Dataverse Network Project");
            Transport.send(msg);
        } catch (AddressException ae) {
            ae.printStackTrace(System.out);
        } catch (MessagingException me) {
            me.printStackTrace(System.out);
        }
    }
    
//    @Resource(name="mail/notifyMailSession")
    public void sendMail(String from, String to, String subject, String messageText){
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to, false));
            msg.setSubject(subject);
            msg.setText(messageText);
            Transport.send(msg);
        } catch (AddressException ae) {
            ae.printStackTrace(System.out);
        } catch (MessagingException me) {
            me.printStackTrace(System.out);
        }
    }
    
    public void sendCreatorRequestNotification(String adminEmail, String userName) {
        sendDoNotReplyMail(adminEmail,"Dataverse Network: New Request to Create a dataverse",
                "User '"+userName+"' has requested to be a dataverse Creator.");
        
        
    }
    
    public void sendCreatorRequestConfirmation(String userEmail) {
        sendDoNotReplyMail(userEmail,"Dataverse Network: Your Request to Create a dataverse has been received",
                "Thanks for your interest in Creating a dataverse in the Dataverse Network. You will be notified as soon as your request is approved.");
        
        
    }
    
    public void sendCreatorApprovalNotification(String userEmail,String createUrl) {
        String msgText = "You can now create your own dataverse in the Dataverse "+
                "Network. Please follow this link "+createUrl+" to start creating "+
                "your dataverse (you will need to log in to access this page).";
        
        sendDoNotReplyMail(userEmail,"Dataverse Network: Your Request to Create a dataverse has been approved",msgText);
        
        
    }
    
    public void sendIngestRequestedNotification(String userEmail, List subsettableFiles) {
        String msgText = "You have requested the following subsettable files to be uploaded: \n";
        Iterator iter = subsettableFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            msgText += "  " + fileBean.getStudyFile().getFileName() + "\n";
        }
        msgText +="\nUpload in progress ...";
        sendDoNotReplyMail(userEmail, "Dataverse Network: The upload of your subsettable file(s) is in progress", msgText );
    }
    
    public void sendIngestCompletedNotification(String userEmail, List successfulFiles, List problemFiles) {
        String msgSubject = "Dataverse Network: Upload request complete";
        
        String msgText = "Your upload request has completed.\n";
         if (successfulFiles != null && successfulFiles.size() != 0) {               
            msgText +=  "\nThe following subsettable files were successfully uploaded: \n";
            Iterator iter = successfulFiles.iterator();
            while (iter.hasNext()) {
                StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
                msgText += "  " + fileBean.getStudyFile().getFileName() +"\n"; 
            }
        }
        
        if (problemFiles != null && problemFiles.size() != 0) {
            msgText += "\nThe following subsettable files failed to upload: \n";
            Iterator iter = problemFiles.iterator();
            while (iter.hasNext()) {
                StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
                msgText += "  " + fileBean.getStudyFile().getFileName() +"\n"; 
            }
        }
        
        sendDoNotReplyMail(userEmail, msgSubject, msgText);
    }
    
    public void sendContributorRequestNotification(String adminEmail, String userName, String vdcName) {
        sendDoNotReplyMail(adminEmail,"Dataverse Network: New request to become a Contributor",
                "User '"+userName+"' has requested to be a Contributor in the '"+vdcName+"' dataverse.");      
    }
    
    
    public void sendFileAccessRequestNotification(VDC currentVDC, String userName, String studyTitle, String globalId) {
        // Send email to all admins and all curators in the vdc
        for (Iterator it = currentVDC.getVdcRoles().iterator(); it.hasNext();) {
            VDCRole vdcRole = (VDCRole) it.next();
            if (vdcRole.getRole().getName().equals(RoleServiceLocal.ADMIN) ||vdcRole.getRole().getName().equals(RoleServiceLocal.CURATOR)) {
                String email = vdcRole.getVdcUser().getEmail();
                sendDoNotReplyMail(email,"Dataverse Network: New request to access restricted files",
                "User '"+userName+"' has requested access to restricted files in study '"+studyTitle+"' ("+globalId+"). ");     
            } 
        }
    }
    
    public void sendContributorRequestConfirmation(String userEmail, String vdcName) {
        sendDoNotReplyMail(userEmail,"Dataverse Network: Your request to become a Contributor has been received",
                "Thanks for your interest contributing to the '"+vdcName+"' dataverse. You will be notified as soon as your request is approved.");
        
        
    }
    public void sendFileAccessRequestConfirmation(String userEmail, String studyTitle, String globalId) {
        sendDoNotReplyMail(userEmail,"Dataverse Network: Your Request to access restricted files has been received",
                "Thanks for your interest in the study '"+studyTitle+"' ("+globalId+"). You will be notified as soon as your request is approved.");
        
        
    }
    
    public void sendContributorApprovalNotification(String userEmail, String vdcName,String url) {
        String msgText= "You can now contribute to '"+vdcName+"' dataverse. \n"+
                " Please follow this link "+
                "to start uploading your study information and data files (you will need to "+
                "log in to access this page): "+url;
        sendDoNotReplyMail(userEmail,"Dataverse Network: Your Request to become a Contributor has been approved",msgText);
        
    }
    
    public void sendFileAccessApprovalNotification(String userEmail, String studyTitle, String globalId, String url) {
        String msgText= "You now have access to restricted files in study '"+studyTitle+"' ("+globalId+"). \n"+
                " Please follow this link "+
                "to view the study files: "+url;
        sendDoNotReplyMail(userEmail,"Dataverse Network: Your request to access restricted files has been approved",msgText);
        
    }
    public void sendAddSiteNotification(String dataverseCreatorEmail, String siteName, String siteAddress){
        String subject = "Dataverse Network: Your dataverse has been created";
        String messageText = "Your dataverse with name '"+siteName+ "' has been created. You can use the following url to access your dataverse:\n http://"+siteAddress+"\n Keep in mind that this dataverse is set as restricted by default, so only you, and any privileged user you add, will have access to it. When you are ready to make your dataverse public, you can do so by changing the restrictions in My Options.";
        sendDoNotReplyMail(dataverseCreatorEmail,subject,messageText);
    }
    
    public void sendStudyInReviewNotification(String userEmail, String studyName){
        String subject = "Dataverse Network: Your Study is in Review";
        String messageText = "Your study '"+ studyName + "' is now under review. You will be notified when the study is released.";
        sendDoNotReplyMail(userEmail,subject,messageText);
    }
    
    public void sendStudyReleasedNotification(String userEmail, String studyName, String dataverseName){
        String subject = "Dataverse Network: Your Study has been released";
        String messageText = "Your study '" + studyName + " has been released to '" + dataverseName + "' dataverse.";
        sendDoNotReplyMail(userEmail,subject,messageText);
    }
    
    public void sendStudyAddedNotification(String userEmail, String studyName, String dataverseName){
        String subject = "Dataverse Network: Your New Study has been set to ready for review";
        String messageText = "Your study '" + studyName + "' has been uploaded to '" + dataverseName + "' dataverse. and is ready for review."+
                "Your study will go under review before it is released. You will be notified when the status of your study changes. You may also go to 'My Options > View My Studies' to check on the status of your study.";
        sendDoNotReplyMail(userEmail,subject,messageText);
    }
    public void sendStudyEditedNotification(String userEmail, String studyName, String dataverseName){
        String subject = "Dataverse Network: Your Study has been updated";
        String messageText = "Your study '" + studyName + "' has been updated in the '" + dataverseName + "' dataverse. "+
                "Your study will go under review before it is released. You will be notified when the status of your study changes. You may also go to 'My Options > View My Studies' to check on the status or edit your study.";
        sendDoNotReplyMail(userEmail,subject,messageText);
    }
    public void sendStudyAddedCuratorNotification(String curatorEmail, String contributorName, String studyName, String dataverseName){
        String subject = "Dataverse Network: A New Study has been uploaded and is ready for review";
        String messageText = "A new Study '" + studyName + "' has been uploaded to  '" + dataverseName +"'"+
                " by contributor '"+contributorName+"'.";
        sendDoNotReplyMail(curatorEmail,subject,messageText);
    }
    
    public void sendStudyEditedCuratorNotification(String curatorEmail, String contributorName, String studyName, String dataverseName){
        String subject = "Dataverse Network: A Study has been updated";
        String messageText = "Changes to Study '" + studyName + "' have been saved to '" + dataverseName+"' dataverse "+
                " by contributor '"+contributorName+"'.";
        sendDoNotReplyMail(curatorEmail,subject,messageText);
    }
    
    public void sendContributorRejectNotification(String userEmail, String vdcName,String adminEmail) {
        String subject = "Dataverse Network: Your Request to become a Contributor was denied";
        String messageText = "Your request to become a contributor to '"+vdcName+"' dataverse was denied.  "+
                "Please contact the dataverse Administrator at "+adminEmail+
                " for more information as to why your request did not go through. ";
        sendDoNotReplyMail(userEmail,subject,messageText);
        
    }
    
    public void sendFileAccessRejectNotification(String userEmail, String studyTitle,String globalId,String adminEmail) {
        String subject = "Dataverse Network: Your Request to access restricted files was denied";
        String messageText = "Your request to access to restricted files in study '"+studyTitle+"' ("+globalId+") was denied.  \n"+
                "Please contact the dataverse Administrator at "+adminEmail+
                " for more information as to why your request did not go through. ";
        sendDoNotReplyMail(userEmail,subject,messageText);
        
    }
    
    public void sendCreatorRejectNotification(String userEmail, String vdcNetworkName,String adminEmail) {
        String subject = "Dataverse Network: Your Request to Create a dataverse was denied";
        String messageText = "Your request to become a dataverse creator in '"+vdcNetworkName+"' Network has been denied.  "+
                "Please contact the Network Administrator at "+adminEmail+
                " for more information as to why your request did not go through. ";
        sendDoNotReplyMail(userEmail,subject,messageText);
        
    }
}
