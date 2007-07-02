
package edu.harvard.hmdc.vdcnet.admin;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Local;


/**
 * This is the business interface for EditUserService enterprise bean.
 */
@Local
public interface EditUserService {
    public Long getRequestStudyId();
    public void setRequestStudyId(Long studyId);
    public void setUser(Long id);
    public void newUser();
    public void cancel();
    public void save();
    public void save(Long contributorRequestVdcId, boolean creatorRequest, Long studyRequestId);
    public VDCUser getUser();
    public void deleteUser();

    
}
