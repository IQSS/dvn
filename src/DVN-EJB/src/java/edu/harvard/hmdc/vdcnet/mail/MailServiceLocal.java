
package edu.harvard.hmdc.vdcnet.mail;

import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.util.List;
import javax.ejb.Local;


/**
 * This is the business interface for Mailer enterprise bean.
 */
@Local
public interface MailServiceLocal {
    public void sendMail(String host, String from, String to, String subject, String messageTest);

    public void sendMail( String from, String to, String subject, String messageTest);

    public void sendDoNotReplyMail(String to, String subject, String messageText);
    
    public void sendCreatorRequestNotification(String adminEmail, String userName);
    
    public void sendCreatorRequestConfirmation(String userEmail);
    
    public void sendCreatorRejectNotification(String userEmail, String vdcNetworkName,String adminEmail);
    
    public void sendCreatorApprovalNotification(String userEmail, String createUrl);

    public void sendContributorRequestNotification(String adminEmail, String userName,String vdcName);
    
    public void sendContributorRequestConfirmation(String userEmail, String vdcName);
    
    public void sendContributorApprovalNotification(String userEmail, String vdcName,String contributorUrl);
    
    public void sendContributorRejectNotification(String userEmail, String vdcName,String adminEmail);
    

    public void sendFileAccessRequestNotification(String sendToEmail, String userName,String studyTitle,String globalId);
    
    public void sendFileAccessRequestConfirmation(String userEmail, String studyTitle,String globalId);
    
    public void sendFileAccessApprovalNotification(String userEmail, String studyTitle,String globalId,String studyUrl);
    
    public void sendFileAccessRejectNotification(String userEmail, String studyTitle,String globalId,String adminEmail);

    
    public void sendIngestRequestedNotification(String userMail, List subsettableFiles);

    public void sendIngestCompletedNotification(String userEmail, List successfulFiles, List problemFiles);    

    public void sendAddSiteNotification(String dataverseCreatorEmail, String siteName, String siteAddress);

    public void sendStudyInReviewNotification(String userEmail, String studyName);

    public void sendStudyReleasedNotification(String userEmail, String studyName, String dataverseName);
    
    public void sendStudyAddedNotification(String userEmail, String studyName, String vdcName );
    
    public void sendStudyEditedNotification(String userEmail, String studyName, String dataverseName);
    
    public void sendStudyAddedCuratorNotification(String curatorEmail, String userEmail, String studyName, String vdcName );
    
    public void sendStudyEditedCuratorNotification(String curatorEmail, String userEmail, String studyName, String vdcName );

    public void sendHarvestErrorNotification(String email,VDC vdc);
    public void sendHarvestErrorNotification(String email );   
}
