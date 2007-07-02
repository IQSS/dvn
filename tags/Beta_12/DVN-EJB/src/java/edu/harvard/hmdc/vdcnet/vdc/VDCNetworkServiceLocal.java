/*
 * VDCNetworkServiceLocal.java
 *
 * Created on October 26, 2006, 11:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author roberttreacy
 */
@Local
public interface VDCNetworkServiceLocal {
    void create(VDCNetwork vDCNetwork);

    void edit(VDCNetwork vDCNetwork);

    void destroy(VDCNetwork vDCNetwork);

    VDCNetwork find(Object pk);
    VDCNetwork find();

    
    
}
