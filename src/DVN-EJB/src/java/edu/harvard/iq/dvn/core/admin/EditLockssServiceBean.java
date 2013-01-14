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
            oaiSet.setSpec(getOaiSpec(lockssConfig.getVdc().getAlias()));
            oaiSet.setName("LOCKSS Archival Unit ("+oaiSet.getSpec()+")");
            oaiSet.setLockssConfig(lockssConfig);
            oaiSet.setDefinition("dvOwnerId:"+lockssConfig.getVdc().getId());
            lockssConfig.setOaiSet(oaiSet);
            em.persist(lockssConfig);
            em.persist(oaiSet);
        } else {
            em.persist(lockssConfig);
        }

        
    }

    /**
     * OAI Set spec needs to be unique within this DVN, so try to use the dataverse alias,
     * but if that already exists, keep trying until we find a unique spec name for this set.
     * @param alias
     * @return unique oai spec
     */
    private String getOaiSpec(String alias) {
        String spec = alias;
        boolean valid = false;
        int count = 0;
        while (!valid) {
            if (count > 0) {
                spec = alias + count;
            }
            try {
                oaiService.findBySpec(spec);
            } catch (ORG.oclc.oai.server.verb.NoItemsMatchException  e) {
                valid = true;
            }
            count++;
        }
        return spec;
    }

    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeLockssConfig() {
        if (lockssConfig.getVdc() != null) {
            // For DV level lockss,
            // need to delete the oaiSet
            if (lockssConfig.getOaiSet().getId() != null) {
                // if the oai existed previously, also delete from harveststudy table
                oaiService.remove(lockssConfig.getOaiSet().getId());
            } else {
                em.remove(lockssConfig.getOaiSet());
            }
        } else {
            // For network level lockss,
            // we just need to delete the relationship to the oaiSet
            if (lockssConfig.getOaiSet() != null) 
            {
                lockssConfig.getOaiSet().setLockssConfig(null);
            }
        }

        em.remove(lockssConfig);

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

