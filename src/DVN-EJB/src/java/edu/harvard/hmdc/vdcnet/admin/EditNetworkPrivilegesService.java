/*
 * EditNetworkPrivilegesService.java
 *
 * Created on October 28, 2006, 6:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import java.util.List;
import javax.ejb.Remove;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Ellen Kraffmiller
 */
public interface EditNetworkPrivilegesService {
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    void cancel();

    List<CreatorRequestBean> getCreatorRequests();

    VDCNetwork getNetwork();

    List<NetworkPrivilegedUserBean> getPrivilegedUsers();

    /**
     *  Initialize the bean with a Study for editing
     */
    void init();

    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void save(String creatorUrl);

    void setCreatorRequests(List<CreatorRequestBean> creatorRequests);

    void setNetwork(VDCNetwork network);

    void setPrivilegedUsers(List<NetworkPrivilegedUserBean> privilegedUsers);
    
    public void addPrivilegedUser(Long userId );
    
}
