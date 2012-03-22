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
 * HarvestingDataverseServiceBean.java
 *
 * Created on April 5, 2007, 10:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.harvest.HarvesterServiceLocal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class HarvestingDataverseServiceBean implements edu.harvard.iq.dvn.core.vdc.HarvestingDataverseServiceLocal {
    @EJB VDCServiceLocal vdcService;
    @EJB HarvesterServiceLocal harvesterService;
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    
    /**
     * Creates a new instance of HarvestingDataverseServiceBean
     */
    public HarvestingDataverseServiceBean() {
    }
    
    public List findAll() {
        return em.createQuery("select object(o) from HarvestingDataverse as o order by o.vdc.name").getResultList();
    }
    
    public HarvestingDataverse find(Long id) {
        HarvestingDataverse hd= em.find(HarvestingDataverse.class,id);
        em.refresh(hd);
        return hd;
    }
    
    public void edit(HarvestingDataverse harvestingDataverse) {
        em.merge(harvestingDataverse);
    }
    
    public void delete(Long hdId){
        HarvestingDataverse hd = em.find(HarvestingDataverse.class, hdId);
        em.refresh(hd);
        
        vdcService.delete(hd.getVdc().getId());
        em.remove(hd);
        
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setHarvestingNow(Long hdId, boolean harvestingNow) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class,hdId);
        em.refresh(hd);
        hd.setHarvestingNow(harvestingNow);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setLastHarvestTime(Long hdId, Date lastHarvestTime) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class,hdId);
        em.refresh(hd);
        hd.setLastHarvestTime(lastHarvestTime);
    }
    
    
   @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setLastSuccessfulHarvestTime(Long hdId, Date lastHarvestTime) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class,hdId);
        em.refresh(hd);
        hd.setLastSuccessfulHarvestTime(lastHarvestTime);
    }   
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean getHarvestingNow(Long hdId) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class,hdId);
        em.refresh(hd);
        return hd.isHarvestingNow();
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Date getLastHarvestTime(Long hdId) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class,hdId);
        em.refresh(hd);
        return hd.getLastHarvestTime();
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setHarvestSuccess(Long hdId, Date currentTime, int harvestedCount, int failedCount) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class, hdId);
        em.refresh(hd);
        hd.setLastSuccessfulHarvestTime(currentTime);
        hd.setHarvestedStudyCount(new Long(harvestedCount));
        hd.setFailedStudyCount(new Long(failedCount));
        hd.setHarvestResult(hd.HARVEST_RESULT_SUCCESS);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setHarvestSuccessNotEmpty(Long hdId, Date currentTime, int harvestedCount, int failedCount) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class, hdId);
        em.refresh(hd);
        hd.setLastSuccessfulNonZeroHarvestTime(currentTime);
        hd.setHarvestedStudyCountNonZero(new Long(harvestedCount));
        hd.setFailedStudyCountNonZero(new Long(failedCount));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setHarvestFailure(Long hdId, int harvestedStudyCount, int failedCount) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class, hdId);
        em.refresh(hd);

        hd.setHarvestedStudyCount(new Long(harvestedStudyCount));
        hd.setFailedStudyCount(new Long(failedCount));
        hd.setHarvestResult(hd.HARVEST_RESULT_FAILED);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setHarvestResult(Long hdId, String result) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class, hdId);
        em.refresh(hd);
        hd.setHarvestResult(result);
    }
  
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void resetHarvestingStatus(Long hdId) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class, hdId);
        em.refresh(hd);
        hd.setHarvestingNow(false);
       
    }
   public void resetAllHarvestingStatus() {
        List harvestingDataverses = findAll();
        for (Iterator it = harvestingDataverses.iterator(); it.hasNext();) {
            HarvestingDataverse hd = (HarvestingDataverse)it.next();
            resetHarvestingStatus(hd.getId());
        }
    }
    
}
