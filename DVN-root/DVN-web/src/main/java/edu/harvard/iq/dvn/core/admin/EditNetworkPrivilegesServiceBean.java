/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
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
        setNetwork(em.find(VDCNetwork.class, new Long(0)));
        
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
   
    public List<NetworkPrivilegedUserBean> getPrivilegedUsersByName(String searchName) {
        setNetwork(em.find(VDCNetwork.class, new Long(0)));
        String lowerSearchString = searchName.toLowerCase();
        String queryString = "SELECT u from VDCUser u where lower(u.lastName) like '" + lowerSearchString.replaceAll("'", "''") 
                + "%' or lower(u.email) like '" + lowerSearchString.replaceAll("'", "''") + "%'"
                + " or lower(u.firstName) like '" + lowerSearchString.replaceAll("'", "''") + "%'"
                + " or lower(u.userName) like '" + lowerSearchString.replaceAll("'", "''") + "%';";
        System.out.print (queryString);
        List<VDCUser> users = em.createQuery(queryString).getResultList();
        List<NetworkPrivilegedUserBean> returnList = new ArrayList<NetworkPrivilegedUserBean>();
        int i = 1;
        for (Object entry : users){
            i++;
            VDCUser elem = (VDCUser) entry;
            if (elem.getNetworkRole() != null){
                returnList.add(new NetworkPrivilegedUserBean(elem, elem.getNetworkRole().getId()));
            } else {
                returnList.add(new NetworkPrivilegedUserBean(elem, null));
            } 
            if (i > 100){
                return returnList;
            }
        }
        return returnList;
    }
   
   public void initTOUPrivilegedUsers() {
     setNetwork(em.find(VDCNetwork.class, new Long(0)));
     TOUprivilegedUsers = em.createQuery("SELECT u from VDCUser u where u.bypassTermsOfUse = true").getResultList();
   }
    

   // @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(String creatorUrl, boolean allowCreate) {

        if (privilegedUsers != null) {
            for (Iterator it = privilegedUsers.iterator(); it.hasNext();) {
                NetworkPrivilegedUserBean elem = (NetworkPrivilegedUserBean) it.next();
                if (elem.getUser().getId() != network.getDefaultNetworkAdmin().getId()) {
                    if (elem.getNetworkRoleId() == null) {
                        elem.getUser().setNetworkRole(null);
                    } else {
                        elem.getUser().setNetworkRole(em.find(NetworkRole.class, elem.getNetworkRoleId()));
                    }
                }
            }
        }

        for (Iterator it = TOUprivilegedUsers.iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            if (elem.isBypassTermsOfUse()) {
                elem.setBypassTermsOfUse(Boolean.TRUE);
            }
        }
        network.setAllowCreateRequest(allowCreate);
        em.flush();
    }

   
    
    public void addPrivilegedUser(Long userId ) {
        VDCUser user = em.find(VDCUser.class, userId);
        this.privilegedUsers.add(new NetworkPrivilegedUserBean(user, null));
        
    }
    
    public void addTOUPrivilegedUser(Long userId) {
        VDCUser user = em.find(VDCUser.class, userId);
        
        int i = this.TOUprivilegedUsers.indexOf(user); 
        
        if (i > -1) {
            this.TOUprivilegedUsers.get(i).setBypassTermsOfUse(Boolean.TRUE);
        } else {
            user.setBypassTermsOfUse(Boolean.TRUE);
            this.TOUprivilegedUsers.add(user);
        }
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

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(String creatorUrl) {
        
        if (privilegedUsers != null) {
            for (Iterator it = privilegedUsers.iterator(); it.hasNext();) {
                NetworkPrivilegedUserBean elem = (NetworkPrivilegedUserBean) it.next();
                if (elem.getUser().getId() != network.getDefaultNetworkAdmin().getId()) {
                    if (elem.getNetworkRoleId() == null) {
                        elem.getUser().setNetworkRole(null);
                    } else {
                        elem.getUser().setNetworkRole(em.find(NetworkRole.class, elem.getNetworkRoleId()));
                    }
                }
            }
        }

        for (Iterator it = TOUprivilegedUsers.iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            if (elem.isBypassTermsOfUse()) {
                elem.setBypassTermsOfUse(Boolean.TRUE);
            }
        }
        
        em.flush();
    }
    

  

  
    
}
