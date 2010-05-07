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
