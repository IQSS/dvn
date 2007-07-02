
package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.ejb.Local;


/**
 * This is the business interface for HandlePrefixService enterprise bean.
 */
@Local
public interface HandlePrefixServiceLocal {
    
    public List<HandlePrefix> findAll();
    
}
