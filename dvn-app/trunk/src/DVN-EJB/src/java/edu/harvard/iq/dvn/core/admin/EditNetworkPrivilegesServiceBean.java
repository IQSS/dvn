/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * EditNetworkPrivilegesServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditNetworkPrivilegesServiceBean implements EditNetworkPrivilegesService   {
   @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
   EntityManager em;
   @EJB MailServiceLocal mailService;
   @EJB UserServiceLocal userService; 
    private VDCNetwork network;
    private List<NetworkPrivilegedUserBean> privilegedUsers;
    private List<VDCUser> TOUprivilegedUsers;
    private List<CreatorRequestBean> creatorRequests;
    
    
    /**
     *  Initialize the bean with a Study for editing
     */
    public void init( ) {
        setNetwork(em.find(VDCNetwork.class, new Long(1)));
        
        initPrivilegedUsers();
        initTOUPrivilegedUsers();
    
        
    }
    

    
   private void initPrivilegedUsers() {
     List<VDCUser> users =em.createQuery("SELECT u from VDCUser u where u.networkRole IS NOT NULL").getResultList();
     privilegedUsers = new ArrayList<NetworkPrivilegedUserBean>();
     for (Iterator it = users.iterator(); it.hasNext();) {
         VDCUser elem = (VDCUser) it.next();
         privilegedUsers.add(new NetworkPrivilegedUserBean(elem, elem.getNetworkRole().getId()) );
     }
   }
    
   private void initTOUPrivilegedUsers() {
     TOUprivilegedUsers = em.createQuery("SELECT u from VDCUser u where u.bypassTermsOfUse = true").getResultList();
   }
    

   // @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(String creatorUrl) {
        
      
        for (Iterator it = privilegedUsers.iterator(); it.hasNext();) {
            NetworkPrivilegedUserBean elem = (NetworkPrivilegedUserBean) it.next();
            if (elem.getUser().getId()!= network.getDefaultNetworkAdmin().getId()) {
                if (elem.getNetworkRoleId()==null) {  
                    elem.getUser().setNetworkRole(null);
                } else {
                    elem.getUser().setNetworkRole(em.find(NetworkRole.class,elem.getNetworkRoleId()));
                }
            }
        }
        
        for (Iterator it = TOUprivilegedUsers.iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            //elem.setBypassTermsOfUse(Boolean.TRUE);
        }
  
        em.flush();
    }

   
    
    public void addPrivilegedUser(Long userId ) {
        VDCUser user = em.find(VDCUser.class, userId);
        this.privilegedUsers.add(new NetworkPrivilegedUserBean(user, null));
        
    }
    
    public void addTOUPrivilegedUser(Long userId) {
        VDCUser user = em.find(VDCUser.class, userId);
        this.TOUprivilegedUsers.add(user);
    }
    
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    public void cancel() {
        
    }
    /**
     * Creates a new instance of EditVDCPrivilegesServiceBean
     */
    public EditNetworkPrivilegesServiceBean() {
    }

    public List<CreatorRequestBean> getCreatorRequests() {
        return creatorRequests;
    }

    public void setCreatorRequests(List<CreatorRequestBean> creatorRequests) {
        this.creatorRequests = creatorRequests;
    }

    public VDCNetwork getNetwork() {
        return network;
    }

    public void setNetwork(VDCNetwork network) {
        this.network = network;
    }

    public List<NetworkPrivilegedUserBean> getPrivilegedUsers() {
        return privilegedUsers;
    }

    public void setPrivilegedUsers(List<NetworkPrivilegedUserBean> privilegedUsers) {
        this.privilegedUsers = privilegedUsers;
    }

    public List<VDCUser> getTOUPrivilegedUsers() {
        return TOUprivilegedUsers;
    }

    public void setTOUPrivilegedUsers(List<VDCUser> userList) {
        this.TOUprivilegedUsers = userList;
    }
    

  

  
    
}
