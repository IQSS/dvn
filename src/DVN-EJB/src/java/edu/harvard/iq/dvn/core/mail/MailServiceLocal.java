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
package edu.harvard.iq.dvn.core.mail;

import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.ingest.dsb.DSBIngestMessage;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;


/**
 * This is the business interface for Mailer enterprise bean.
 */
@Local
public interface MailServiceLocal  extends java.io.Serializable  {
    public void sendMail(String host, String from, String to, String subject, String messageTest);

    public void sendMail( String from, String to, String subject, String messageTest);
    
    public void sendMail(String from, String to, String subject, String messageText, Map extraHeaders);

    public void sendDoNotReplyMail(String to, String subject, String messageText);
    
    public void sendPasswordUpdateNotification(String userEmail, String userFirstName, String userName, String newPassword);
    
    public void sendCreatorAccountNotification(String adminEmail, String userName);

    public void sendContributorAccountNotification(String adminEmail, String userName, String dataverseName);
    
    public void sendFileAccessRequestNotification(String sendToEmail, String userName,String studyTitle,String globalId);
    
    public void sendFileAccessRequestNotification(String sendToEmail, String userName, String studyTitle, String globalId, String fileNameList);
    
    public void sendFileAccessRequestConfirmation(String userEmail, String studyTitle,String globalId);
    
    public void sendFileAccessRequestConfirmation(String userEmail, String studyTitle, String globalId, String fileNameList);

    public void sendFileAccessApprovalNotification(String userEmail, String studyTitle,String globalId,String studyUrl);
    
    public void sendFileAccessApprovalNotification(String userEmail, String studyTitle, String globalId, String fileLabel, String fileId, String studyUrl);

    public void sendFileAccessRejectNotification(String userEmail, String studyTitle,String globalId,String adminEmail);

    public void sendFileAccessRejectNotification(String userEmail, String studyTitle,String globalId, String fileLable, String fileId, String adminEmail);

    public void sendFileAccessResolvedNotification(String userEmail, String studyTitle,String globalId, List<String> acceptedFiles, List<String> rejectedFiles, String url, String adminEmail);
    
    public void sendIngestRequestedNotification(DSBIngestMessage ingestMessage, List subsettableFiles);

    public void sendIngestCompletedNotification(DSBIngestMessage ingestMessage, List successfulFiles, List problemFiles);    

    public void sendAddSiteNotification(String dataverseCreatorEmail, String siteName, String siteAddress);

    public void sendReleaseSiteNotification(String dataverseCreatorEmail, String siteName, String siteAddress);

    public void sendReleaseSiteNotificationNetwork(String networkAdminEmail, String siteName, String siteAddress);

    public void sendStudyInReviewNotification(String userEmail, String studyName);

    public void sendStudyReleasedNotification(String userEmail, String studyName, String dataverseName);
    
    public void sendStudyAddedNotification(String userEmail, String studyName, String vdcName );
    
    public void sendStudyEditedNotification(String userEmail, String studyName, String dataverseName);
    
    public void sendStudyAddedCuratorNotification(String curatorEmail, String userEmail, String studyName, String vdcName );
    
    public void sendStudyEditedCuratorNotification(String curatorEmail, String userEmail, String studyName, String vdcName );

    public void sendHarvestErrorNotification(String email,VDC vdc);
    public void sendHarvestErrorNotification(String email, String vdcNetworkName );   
    public void sendHarvestNotification(String email, String vdcName, String logFileName, String logTimestamp, boolean harvestError, int harvestedStudyCount, List<String> failedIdentifiers);

    public void sendExportErrorNotification(String email, String vdcNetworkName);

    public void sendIndexUpdateErrorNotification(String email, String vdcNetworkName);
    public void sendIndexErrorNotification(String email, String vdcNetworkName, int howMany);
}
