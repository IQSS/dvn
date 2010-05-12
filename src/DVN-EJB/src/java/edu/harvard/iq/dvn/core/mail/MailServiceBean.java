/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * MailServiceBean.java
 *
 * Created on November 15, 2006, 8:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.mail;

import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.EJB;
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
public class MailServiceBean implements edu.harvard.iq.dvn.core.mail.MailServiceLocal, java.io.Serializable {

    @EJB VDCNetworkServiceLocal vdcNetworkService;

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
            msg.setText(messageText+"\n\nPlease do not reply to this email.\nThank you,\nThe Dataverse Network Project");
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
    
    public void sendPasswordUpdateNotification(String userEmail, String userFirstName,String userName, String newPassword) {
        String subject = "Dataverse Network: New Password Request";
        String msgText ="Hello "+userFirstName+",\n";
        msgText+="Here is the new password information that you requested for the Dataverse Network:\n";
        msgText+="Username:  "+userName+"\n";
        msgText+="Password:  "+newPassword+"\n";
        msgText+="After logging in, change your password by clicking the username displayed on the top right of the menubar.\n";
        msgText+="If you continue to have problems logging in, send us an e-mail through the Dataverse Network Contact form.\n";
        sendDoNotReplyMail(userEmail,subject,msgText);
    }
    
    public void sendCreatorAccountNotification(String adminEmail, String userName) {
        String msgText="A new account has been created with Dataverse Creator privileges";
        msgText+=" (user name: "+userName+").";
        sendDoNotReplyMail(adminEmail,"Dataverse Network: New Creator Account",msgText);
     }
    
   public void sendContributorAccountNotification(String adminEmail, String userName, String dataverseName) {
        String msgText="A new account has been created with Contributor privileges in "+dataverseName+" dataverse";
        msgText+=" (user name: "+userName+").";    
        sendDoNotReplyMail(adminEmail,"Dataverse Network: New Contributor Account",msgText);
              
    }
   
    public void sendIngestRequestedNotification(String userEmail, List subsettableFiles) {
        String msgText = "You have requested the following subsettable files to be uploaded: \n";
        Iterator iter = subsettableFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            msgText += "  " + fileBean.getFileMetadata().getLabel() + "\n";
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
                msgText += "  " + fileBean.getFileMetadata().getLabel() +"\n";
            }
        }
        
        if (problemFiles != null && problemFiles.size() != 0) {
            msgSubject += " (with failures)";
            
            msgText += "\nThe following subsettable files failed to upload: \n";
            Iterator iter = problemFiles.iterator();
            while (iter.hasNext()) {
                StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
                msgText += "  " + fileBean.getFileMetadata().getLabel() +" (File Type: " + fileBean.getStudyFile().getFileType() + "; File Size: " + fileBean.getSizeFormatted() +")\n";
            }
        }
        
        sendDoNotReplyMail(userEmail, msgSubject, msgText);
    }
    
  
    
    public void sendFileAccessRequestNotification(String sendToEmail, String userName, String studyTitle, String globalId) {
                sendDoNotReplyMail(sendToEmail,"Dataverse Network: New request to access restricted files",
                "User '"+userName+"' has requested access to restricted files in study '"+studyTitle+"' ("+globalId+"). ");     
    }
    
  
    public void sendFileAccessRequestConfirmation(String userEmail, String studyTitle, String globalId) {
        sendDoNotReplyMail(userEmail,"Dataverse Network: Your Request to access restricted files has been received",
                "Thanks for your interest in the study '"+studyTitle+"' ("+globalId+"). You will be notified as soon as your request is approved.");
        
        
    }
    
  
    
    public void sendFileAccessApprovalNotification(String userEmail, String studyTitle, String globalId, String url) {
        String msgText= "You now have access to restricted files in study '"+studyTitle+"' ("+globalId+"). \n"+
                " Please follow this link "+
                "to view the study files: "+url;
        sendDoNotReplyMail(userEmail,"Dataverse Network: Your request to access restricted files has been approved",msgText);
        
    }
    public void sendAddSiteNotification(String dataverseCreatorEmail, String siteName, String siteAddress){
        String subject = "Dataverse Network: Your dataverse has been created";
        String messageText = "Hello, \nYour new dataverse named '"+siteName+"' was"
               + " created in the " + vdcNetworkService.find().getName() + " Dataverse Network. You can access your dataverse"
               + " directly by entering this URL:\n" 
               + "http://"+siteAddress+"\n"
               + "Your dataverse is set to Not Released by default. You can do the"
               +" following as a Dataverse Admin: \n"
               +" -Click Options to administer your dataverse.\n"
               + " -Begin creating your own studies and uploading files or adding collections of data from other dataverses\n"
               + " -Customize the layout, and then you are ready to release your dataverse to others.\n"
               + "For detailed information about how to use your dataverse options, click User Guides on the Dataverse Network menu bar, or go to http://thedata.org/guides.";

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
                "Your study will go under review before it is released. You will be notified when the status of your study changes. You may also go to 'Options > Study Options > Manage Studies' to check on the status of your study.";
        sendDoNotReplyMail(userEmail,subject,messageText);
    }
    public void sendStudyEditedNotification(String userEmail, String studyName, String dataverseName){
        String subject = "Dataverse Network: Your Study has been updated";
        String messageText = "Your study '" + studyName + "' has been updated in the '" + dataverseName + "' dataverse. "+
                "Your study will go under review before it is released. You will be notified when the status of your study changes. You may also go to 'Options > Study Options > Manage Studies' to check on the status or edit your study.";
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
    
    
    public void sendFileAccessRejectNotification(String userEmail, String studyTitle,String globalId,String adminEmail) {
        String subject = "Dataverse Network: Your Request to access restricted files was denied";
        String messageText = "Your request to access to restricted files in study '"+studyTitle+"' ("+globalId+") was denied.  \n"+
                "Please contact the dataverse Administrator at "+adminEmail+
                " for more information as to why your request did not go through. ";
        sendDoNotReplyMail(userEmail,subject,messageText);
        
    }
    
      public void sendHarvestErrorNotification(String email,VDC vdc) {
          String subject = "Dataverse Network: Harvesting error notification";
          String messageText = "An exception occurred while harvesting from "+vdc.getName()+" dataverse. See harvest_"+vdc.getAlias()+" log and server.log for more details. ";
              sendDoNotReplyMail(email,subject,messageText);
      }
      public void sendHarvestErrorNotification(String email, String vdcNetworkName) {
          String subject = "Dataverse Network: Harvesting error notification";
          String messageText = "An exception occurred during harvesting in DVN "+vdcNetworkName+". See server.log for more details. ";
          
              sendDoNotReplyMail(email,subject,messageText);
      }
      public void sendHarvestNotification(String email, String vdcName, String logFileName, String logTimestamp, boolean harvestError, int harvestedStudyCount, List<String> failedIdentifiers) {
          String subject = null;
          if (harvestError) {
              subject="Harvest Error Notification"; 
          }else {
              subject="Harvest Success Notification";
          }
          subject +=" ("+vdcName+","+logTimestamp+")";
          String messageText=null;
          if (!harvestError) {
              messageText = "A harvest has successfully completed for "+vdcName+" Dataverse. \n";
          } else {
            messageText = "A harvest has run for "+vdcName+" Dataverse, with errors.\n";
          }
               
          if (!harvestError && harvestedStudyCount==0) {
            messageText += " No studies have been harvested (no updates found since last harvest). ";
          } else {
            messageText += ""+harvestedStudyCount+" studies were successfully harvested.\n";
          }
          if (failedIdentifiers.size()>0) {
                messageText+= "Harvest failed for the following identifiers - \n";
                Iterator iter = failedIdentifiers.iterator();
                while (iter.hasNext()) {
                    messageText += "  "+(String)iter.next() +"\n"; 
                }                 
          
          }
          if (harvestError ) {
              messageText+="Please see "+logFileName+" and server.log for details of harvest errors.";
          } else {
              messageText+="Please see "+logFileName+" for more details.";
          }
          sendDoNotReplyMail(email,subject,messageText);
      }   
      
    public void sendExportErrorNotification(String email, String vdcNetworkName) {
        String subject = "Dataverse Network: Export error notification";
        String messageText = "An exception occurred during exporting in DVN " + vdcNetworkName + ". See server.log for more details. ";
        sendDoNotReplyMail(email, subject, messageText);
    }

    public void sendIndexUpdateErrorNotification(String email, String vdcNetworkName) {
        String subject = "Dataverse Network: Index update error notification";
        String messageText = "An exception occurred during index update in DVN " + vdcNetworkName + ". See server.log for more details. ";
        sendDoNotReplyMail(email, subject, messageText);
    }

    public void sendIndexErrorNotification(String email, String vdcNetworkName, int howMany) {
        String subject = "Dataverse Network: Index update error notification";
        String messageText = howMany + " exceptions occurred during index update in DVN " + vdcNetworkName + ". See server.log for more details. \n You can use the index update utility or wait for the scheduled overnight update";
        sendDoNotReplyMail(email, subject, messageText);
    }

}
