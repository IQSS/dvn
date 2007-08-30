
package edu.harvard.hmdc.vdcnet.gnrs;

import javax.ejb.Local;


/**
 * This is the business interface for GNRSService enterprise bean.
 */
@Local
public interface GNRSServiceLocal {
    public String getNewObjectId(String protocol, String authority);

    public void delete(String authority, String studyId);

    public String resolveHandleUrl(String handle);

    public void deleteHandle(String handle);

    public void createHandle(String handle);
    
    public void fixHandle(String handle);

    public void registerAll();
    
}
