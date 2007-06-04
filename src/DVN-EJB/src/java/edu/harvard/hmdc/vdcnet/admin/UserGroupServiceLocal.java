
package edu.harvard.hmdc.vdcnet.admin;

import javax.ejb.Local;


/**
 * This is the business interface for NewSession enterprise bean.
 */
@Local
public interface UserGroupServiceLocal {
    public UserGroup findById(Long id);
    
}
