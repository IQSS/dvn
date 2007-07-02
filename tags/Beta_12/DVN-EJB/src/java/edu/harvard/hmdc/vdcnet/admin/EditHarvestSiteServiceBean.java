/*
 * EditVDCPrivilegesServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.harvest.SetDetailBean;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
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
public class EditHarvestSiteServiceBean implements EditHarvestSiteService  {
    @EJB MailServiceLocal mailService;
    @EJB RoleServiceLocal roleService;
    @EJB UserServiceLocal userService;
    @EJB VDCServiceLocal vdcService;

    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    //  @EJB RoleServiceLocal roleService;
    EntityManager em;
    private HarvestingDataverse harvestingDataverse;
    
   
  
    
    /**
     *  Initialize the bean with a Study for editing
     */
    public void setHarvestingDataverse(Long id ) {
        harvestingDataverse = em.find(HarvestingDataverse.class,id);
        if (harvestingDataverse==null) {
            throw new IllegalArgumentException("Unknown harvestingDataverse id: "+id);
        }
       
        
    }
    
    public void newHarvestingDataverse( ) {
        harvestingDataverse = new HarvestingDataverse();
     
        em.persist(harvestingDataverse);
    }
    
    public  HarvestingDataverse getHarvestingDataverse() {
        return harvestingDataverse;
    }
    
    
  
 
        
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(String name, String alias, Long userId) {
        if (harvestingDataverse.getVdc()==null) {
            vdcService.create(userId,name,alias);
            VDC vdc = vdcService.findByAlias(alias);
            harvestingDataverse.setVdc(vdc);
            vdc.setHarvestingDataverse(harvestingDataverse);
        } else {
            harvestingDataverse.getVdc().setName(name);
            harvestingDataverse.getVdc().setAlias(alias);
        }
   
        em.flush();
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
    public EditHarvestSiteServiceBean() {
    }
    
    /**
     * Holds value of property contributorRequests.
     */
 
    
    public void removeAllowedFileGroup(Long groupId) {
        UserGroup group = em.find(UserGroup.class,groupId);
        harvestingDataverse.getAllowedFileGroups().remove(group);
        
    }
    
    public void  addAllowedFileGroup(Long groupId) {
        UserGroup group = em.find(UserGroup.class,groupId);
        harvestingDataverse.getAllowedFileGroups().add(group);
       
        
    }
    
     public void removeAllowedFileUser(Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        harvestingDataverse.getAllowedFileUsers().remove(user);
      
    }
    
    public void  addAllowedFileUser(Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        harvestingDataverse.getAllowedFileUsers().add(user);
     
        
    }

    /**
     * Holds value of property harvestingSets.
     */
    private java.util.List<SetDetailBean> harvestingSets;

    /**
     * Getter for property harvestingSets.
     * @return Value of property harvestingSets.
     */
    public java.util.List<SetDetailBean> getHarvestingSets() {
        return this.harvestingSets;
    }

    /**
     * Setter for property harvestingSets.
     * @param harvestingSets New value of property harvestingSets.
     */
    public void setHarvestingSets(java.util.List<SetDetailBean> harvestingSets) {
        this.harvestingSets = harvestingSets;
    }
    
    private List<String> metadataFormats;
     
    public List<String> getMetadataFormats(){
        return metadataFormats;
    }
  
    public void setMetadataFormats(List<String> metadataFormats){
        this.metadataFormats=metadataFormats;
    }
}
