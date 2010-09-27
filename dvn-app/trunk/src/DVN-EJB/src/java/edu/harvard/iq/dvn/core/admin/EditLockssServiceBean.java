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
 * EditLockssServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.LicenseType;
import edu.harvard.iq.dvn.core.vdc.LockssConfig;
import edu.harvard.iq.dvn.core.vdc.OAISet;
import edu.harvard.iq.dvn.core.vdc.OAISetServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.util.Collection;
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
public class EditLockssServiceBean implements EditLockssService, java.io.Serializable { 
    @EJB OAISetServiceLocal oaiService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB StudyServiceLocal studyService;
   
    
    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    EntityManager em;
   
    private LockssConfig lockssConfig;
    private boolean newLockssConfig=false;


    public List<LicenseType> getLicenseTypes() {

        return em.createQuery("select object(o) from LicenseType as o order by o.id").getResultList();
    }
    
    /**
     *  Initialize the bean with a studyVersion for editing
     */
    public void initLockssConfig(Long lockssConfigId ) {
        lockssConfig = em.find(LockssConfig.class, lockssConfigId);
        if (lockssConfig==null) {
            throw new IllegalArgumentException("Unknown LockssConfig id: "+lockssConfigId);
        }
   
    }
    

    public LockssConfig getLockssConfig() {
        return lockssConfig;
    }

    public void newLockssConfig(Long vdcId) {
        newLockssConfig=true;
        lockssConfig = new LockssConfig();
        VDC vdc = null;
        if (vdcId!=null) {
            vdc = em.find(VDC.class, vdcId);
            lockssConfig.setVDC(vdc);
            OAISet oaiSet = new OAISet();
            oaiSet.setName("LOCKSS Archival Unit for "+lockssConfig.getVdc().getName()+" Dataverse");
            oaiSet.setSpec(lockssConfig.getVdc().getAlias());  // TODO: make sure the spec name is unique!
            oaiSet.setLockssConfig(lockssConfig);
            oaiSet.setDefinition("dvOwnerId:"+lockssConfig.getVdc().getId());
            lockssConfig.setOaiSet(oaiSet);
            em.persist(lockssConfig);
            em.persist(oaiSet);
        } else {
            em.persist(lockssConfig);
        }
  // call flush to generate a lockssConfig id, we will need that when assigning a dvn lockssConfig to an existing oaiSet
        
    }
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeLockssConfig() {
        if (lockssConfig.getVdc()!=null && lockssConfig.getOaiSet()!=null) {
            em.remove(lockssConfig.getOaiSet());
        } else {
            if (lockssConfig.getOaiSet()!=null) {
                lockssConfig.getOaiSet().setLockssConfig(null);
            }
        }
        em.remove(lockssConfig);
        // TODO: remove harvestStudy data (same thing needs to happen when deleting a dataverse)
    }

    public void removeCollectionElement(Collection coll, Object elem) {
        coll.remove(elem);
        em.remove(elem);
    }
     public void removeCollectionElement(List list,int index) {
        System.out.println("index is "+index+", list size is "+list.size());
        em.remove(list.get(index));
        list.remove(index);
    }  
    public void removeCollectionElement(Iterator iter, Object elem) {
        iter.remove();
        em.remove(elem);
    }
    
    /**
     * This method should only be called for network-level LockssConfig objects,
     * because the OAISets for the dataverse-level LockssConfig can't be changed by the user - they
     * will always be an  OAISet that represents every owned study in the dataverse.
     * @param oaiSetId
     */
    public void updateOaiSet(Long oaiSetId) {
        // First, remove lockssConfig foreign key from current oaiSet, if it exists
        if (lockssConfig.getOaiSet()!=null) {
            OAISet currentOAISet = em.find(OAISet.class, lockssConfig.getOaiSet().getId());
            currentOAISet.setLockssConfig(null);
         
        }
        if (oaiSetId==null) {
            lockssConfig.setOaiSet(null);
        } else {
            OAISet oaiSet = em.find(OAISet.class, oaiSetId);
            lockssConfig.setOaiSet(oaiSet);
            oaiSet.setLockssConfig(lockssConfig);
        }
    }
   
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveChanges(Long oaiSetId) {
        if (lockssConfig.getVdc() == null) {
            // First, remove lockssConfig foreign key from current oaiSet, if it exists
            if (lockssConfig.getOaiSet() != null) {
                OAISet currentOAISet = em.find(OAISet.class, lockssConfig.getOaiSet().getId());
                currentOAISet.setLockssConfig(null);
                em.flush();
            }
            if (oaiSetId == null) {
                lockssConfig.setOaiSet(null);
            } else {
                OAISet oaiSet = em.find(OAISet.class, oaiSetId);
                lockssConfig.setOaiSet(oaiSet);
                oaiSet.setLockssConfig(lockssConfig);
            }
        }
        System.out.println("oaiSet="+lockssConfig.getOaiSet());
      

    }

    
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    public void cancel() {
        
    }
    
   
    
    /**
     * Creates a new instance of EditLockssServiceBean
     */
    public EditLockssServiceBean() {
    }
    
  
    
    public boolean isNewLockssConfig() {
        return newLockssConfig;
    }
    
    
    
    private void clearCollection(Collection collection) {
        if (collection!=null) {
            for (Iterator it = collection.iterator(); it.hasNext();) {
                Object elem =  it.next();          
                it.remove();
                em.remove(elem);
            }
        }
    }

   
    
}

