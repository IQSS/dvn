/*
 * EditUserServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAccessRequest;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
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
public class EditUserServiceBean implements edu.harvard.hmdc.vdcnet.admin.EditUserService {   
    @PersistenceContext (type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")  
    EntityManager em;
    VDCUser user;
    @EJB UserServiceLocal userService;
    private String newPassword1;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword1() {
        return newPassword1;
    }

    public void setNewPassword1(String newPassword1) {
        this.newPassword1 = newPassword1;
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }
    private String newPassword2;
    private String currentPassword;

  
  
    /**
     *  Initialize the bean with a Study for editing
     */
    public void setUser(Long id ) {
        user = em.find(VDCUser.class,id);
        if (user==null) {
            throw new IllegalArgumentException("Unknown study id: "+id);
        }
        
      
    }
    
    public void newUser() {
     
        user = new VDCUser();
        em.persist(user);
      
    }
    
    
    
    public  VDCUser getUser() {
        return user;
    }
    
    
   @Remove
   @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)     
   public void deleteUser() {
     
        em.remove(user);
    }
    
  
   
  
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)    
    public void save() {
    
        user.setEncryptedPassword(userService.encryptPassword(newPassword1));
        // Don't really need to do call flush(), because a method that 
        // requires a transaction will automatically trigger a flush to the database,
        // but include this just to show what's happening here
        em.flush();    
    }

    public void setUser(VDCUser user) {
        this.user = user;
    }
   
      @Remove  
     @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)    
     public void save(Long contributorRequestVdcId, boolean creatorRequest, Long studyRequestId) {
         user.setEncryptedPassword(userService.encryptPassword(newPassword1));
         // If user requests to be a contributor in a VDC, 
         // create a request object for this
         if (contributorRequestVdcId!=null) { 
             String query = "select r from Role r where r.name = '"+RoleServiceLocal.CONTRIBUTOR+"'";
             Role contributor = (Role)em.createQuery(query).getSingleResult();
             VDC vdc = em.find(VDC.class, contributorRequestVdcId);
             RoleRequest roleRequest = new RoleRequest();
             roleRequest.setRole(contributor);
             roleRequest.setVdcUser(user);
             roleRequest.setVdc(vdc);
             vdc.getRoleRequests().add(roleRequest);
            
         }
         // If user requests to be  a VDC Creator in the network, 
         // create a request object for this
         if (creatorRequest) {
             String query = "select n from NetworkRole n where n.name = '"+ NetworkRoleServiceLocal.CREATOR+"'";
             NetworkRole creator = (NetworkRole)em.createQuery(query).getSingleResult();
             NetworkRoleRequest roleRequest = new NetworkRoleRequest();
             roleRequest.setNetworkRole(creator);
             roleRequest.setVdcUser(user);
             em.persist(roleRequest);
         }
         if (studyRequestId!=null ) {
             Study study = em.find(Study.class,studyRequestId);
             StudyAccessRequest studyRequest = new StudyAccessRequest();
             studyRequest.setStudy(study); 
             studyRequest.setVdcUser(user);
             study.getStudyRequests().add(studyRequest);
             em.persist(studyRequest);
         }
         em.flush();  // Save the user and all the new request objects to the database.
         
     }

    private Long requestStudyId;
    public Long getRequestStudyId() {return requestStudyId;}
    public void setRequestStudyId(Long studyId) {this.requestStudyId = studyId;}
 
    
    /**
     * Remove this Stateful Session bean from the EJB Container without 
     * saving updates to the database.
     */
   @Remove
   public void cancel() {
       
   }
    /**
     * Creates a new instance of EditUserServiceBean
     */
    public EditUserServiceBean() {
    }

 

   
}
