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
 * DSBIngestMessage.java
 *
 * Created on March 22, 2007, 4:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.dsb;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author gdurand
 */
public class DSBIngestMessage implements Serializable{

    public static final int INGEST_MESAGE_LEVEL_ERROR = 1; 
    public static final int INGEST_MESAGE_LEVEL_INFO = 2;

    /** Creates a new instance of DSBIngestMessage */
    public DSBIngestMessage()  {
    }

    public DSBIngestMessage(int messageLevel)  {
        this.messageLevel = messageLevel;
    }
    
    private String ingestEmail;
    private Long ingestUserId;
    private int messageLevel = INGEST_MESAGE_LEVEL_INFO;
    
    private Long studyId;
    private Long studyVersionId;
    private List fileBeans;
    private String versionNote;

    public String getVersionNote() {
        return versionNote;
    }

    public void setVersionNote(String versionNote) {
        this.versionNote = versionNote;
    }
    
    
    public String getIngestEmail() {
        return ingestEmail;
    }

    public void setIngestEmail(String ingestEmail) {
        this.ingestEmail = ingestEmail;
    }

    public Long getIngestUserId() {
        return ingestUserId;
    }

    public void setIngestUserId(Long ingestUserId) {
        this.ingestUserId = ingestUserId;
    }

    public int getMessageLevel() {
        return messageLevel;
    }

    public void setMessageLevel(int messageLevel) {
        this.messageLevel = messageLevel;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public Long getStudyVersionId() {
        return studyVersionId;
    }

    public void setStudyVersionId(Long studyVersionId) {
        this.studyVersionId = studyVersionId;
    }

    public List getFileBeans() {
        return fileBeans;
    }

    public void setFileBeans(List fileBeans) {
        this.fileBeans = fileBeans;
    }

    public boolean sendInfoMessage() {
        return messageLevel >= INGEST_MESAGE_LEVEL_INFO;
    }

    public boolean sendErrorMessage() {
        return messageLevel >= INGEST_MESAGE_LEVEL_ERROR;
    }
}
