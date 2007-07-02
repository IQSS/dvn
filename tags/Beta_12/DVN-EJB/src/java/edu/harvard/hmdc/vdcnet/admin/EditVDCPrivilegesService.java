/*
 * EditVDCPrivilegesService.java
 *
 * Created on October 23, 2006, 4:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.util.List;
import javax.ejb.Remove;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Ellen Kraffmiller
 */
public interface EditVDCPrivilegesService {
    /**
     * Remove this Stateful Session bean from the EJB Container without 
     * saving updates to the database.
     */
    @Remove
    void cancel();

    /**
     * Getter for property contributorRequests.
     * 
     * @return Value of property contributorRequests.
     */
    List<ContributorRequestBean> getContributorRequests();

    VDC getVdc();

    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(String contributorUrl);

    /**
     * Setter for property contributorRequests.
     * 
     * @param contributorRequests New value of property contributorRequests.
     */
    public void setContributorRequests(List<ContributorRequestBean> contributorRequests);
    
    public void removeAllowedGroup(Long groupId);

    public void addAllowedGroup(Long groupId);
    
    public void removeRole(Long userId);
    /**
     *  Initialize the bean with a Study for editing
     */
    void setVdc(Long id);
    
    
    public void addUserRole(String userName);
    
    public List getPrivilegedUsers();
    
}
