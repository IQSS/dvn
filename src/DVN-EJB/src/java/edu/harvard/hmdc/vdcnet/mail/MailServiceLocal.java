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
    public void sendHarvestErrorNotification(String email, String vdcNetworkName );   
    public void sendExportErrorNotification(String email, String vdcNetworkName ); 
}
