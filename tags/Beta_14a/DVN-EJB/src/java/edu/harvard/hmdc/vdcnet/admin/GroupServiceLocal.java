/*
 * GroupServiceLocal.java
 *
 * Created on October 26, 2006, 3:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ellen Kraffmiller
 */
@Local
public interface GroupServiceLocal {
    public UserGroup findById(Long id);

    public UserGroup findByName(String name);
    
    public List<UserGroup> findAll();

    public UserGroup findIpGroupUser(String remotehost);
    
    public Collection<LoginAffiliate> findAllLoginAffiliates();
    
    public void remove(Long groupId);
    
}
