/*
 * EditNetworkPrivilegesServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
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
    
    private VDCNetwork network;
    private List<NetworkPrivilegedUserBean> privilegedUsers;
    private List<CreatorRequestBean> creatorRequests;
    
    
    /**
     *  Initialize the bean with a Study for editing
     */
    public void init( ) {
        setNetwork(em.find(VDCNetwork.class, new Long(1)));
        
        initPrivilegedUsers();
        initCreatorRequests();
        
    }
    

    
   private void initPrivilegedUsers() {
     List<VDCUser> users =em.createQuery("SELECT u from VDCUser u where u.networkRole IS NOT NULL").getResultList();
     privilegedUsers = new ArrayList<NetworkPrivilegedUserBean>();
     for (Iterator it = users.iterator(); it.hasNext();) {
         VDCUser elem = (VDCUser) it.next();
         privilegedUsers.add(new NetworkPrivilegedUserBean(elem, elem.getNetworkRole().getId()) );
     }
   }
    
   private void initCreatorRequests() {
       setCreatorRequests(new ArrayList());
       String query = "Select n from NetworkRoleRequest n where n.networkRole.name = '"+NetworkRoleServiceLocal.CREATOR+"'";
       List<NetworkRoleRequest> requests = em.createQuery(query).getResultList();
       for (Iterator it = requests.iterator(); it.hasNext();) {
           NetworkRoleRequest elem = (NetworkRoleRequest) it.next();
           getCreatorRequests().add(new  CreatorRequestBean(elem));
           
       }
     
   }
    
   private NetworkRole  getCreatorRole() {
       String query ="Select n from NetworkRole n where n.name = '"+NetworkRoleServiceLocal.CREATOR+"'";
       return (NetworkRole) em.createQuery(query).getSingleResult(); 
       
   }
   // @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(String creatorUrl) {
        
        // Update Creator Role Requests based on Acceptions and Rejections
        NetworkRole creator = getCreatorRole();
        for (Iterator<CreatorRequestBean> it = getCreatorRequests().iterator(); it.hasNext();) {
            CreatorRequestBean elem = it.next();
            VDCUser user = elem.getNetworkRoleRequest().getVdcUser();
            if (Boolean.TRUE.equals(elem.getAccept()) ){            
                user.setNetworkRole(creator);
                em.remove(elem.getNetworkRoleRequest());
                mailService.sendCreatorApprovalNotification(user.getEmail(),creatorUrl);
            } else if (Boolean.FALSE.equals(elem.getAccept()) ){
                mailService.sendCreatorRejectNotification(user.getEmail(),network.getName(), network.getContactEmail());
                em.remove(elem.getNetworkRoleRequest());
                
            }
            
        }
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
  
        em.flush();
    }

   
    
    public void addPrivilegedUser(Long userId ) {
        VDCUser user = em.find(VDCUser.class, userId);
        this.privilegedUsers.add(new NetworkPrivilegedUserBean(user, null));
        
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

    

  

  
    
}
