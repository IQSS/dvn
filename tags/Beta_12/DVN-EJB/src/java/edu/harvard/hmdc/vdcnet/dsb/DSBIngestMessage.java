/*
 * DSBIngestMessage.java
 *
 * Created on March 22, 2007, 4:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb;

import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author gdurand
 */
public class DSBIngestMessage implements Serializable {
    
    /** Creates a new instance of DSBIngestMessage */
    public DSBIngestMessage()  {
    }
    
    private String ingestEmail;
    private Long ingestUserId;
    
    private Long studyId;
    private List fileBeans;

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

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public List getFileBeans() {
        return fileBeans;
    }

    public void setFileBeans(List fileBeans) {
        this.fileBeans = fileBeans;
    }
}
