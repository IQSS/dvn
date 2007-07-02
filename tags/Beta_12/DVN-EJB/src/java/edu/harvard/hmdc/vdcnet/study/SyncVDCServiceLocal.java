
package edu.harvard.hmdc.vdcnet.study;

import javax.ejb.Local;


/**
 * This is the business interface for SyncVDCServiceLocal enterprise bean.
 */
@Local
public interface SyncVDCServiceLocal {
    public void scheduleNow(String lastUpdateTime, String authority);
    public void scheduleDaily();
    
}
