
package edu.harvard.hmdc.vdcnet.gnrs;

import javax.ejb.Local;


/**
 * This is the business interface for TestNRSService enterprise bean.
 */
@Local
public interface TestNRSServiceLocal {
    String getNewObjectId(String protocol, String authority);
    
}
