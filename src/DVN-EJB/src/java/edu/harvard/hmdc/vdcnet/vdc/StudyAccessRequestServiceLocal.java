/*
 * VDCServiceLocal.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import edu.harvard.hmdc.vdcnet.study.StudyAccessRequest;
import javax.ejb.Local;

/**
 *
 * @author ekraffmiller
 */
@Local

public interface StudyAccessRequestServiceLocal {
  
    public StudyAccessRequest findByUserStudy(Long userId, Long studyId);

  public void create(Long vdcUserId, Long studyId);  
    
}
