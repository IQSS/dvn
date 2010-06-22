/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.harvest;

import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.OAISet;
import edu.harvard.iq.dvn.core.vdc.OAISetServiceLocal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Gustavo
 */
@Stateless
public class HarvestStudyServiceBean implements HarvestStudyServiceLocal {

    @EJB 
    OAISetServiceLocal oaiSetService;    
    @EJB 
    IndexServiceLocal indexService;
    @EJB 
    StudyServiceLocal studyService;

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;    
    
    public void updateHarvestStudies() {
        Date updateTime = new Date();
        List<OAISet> sets = oaiSetService.findAll();
        
        for (OAISet oaiSet : sets) {
            List<Long> studyIds = indexService.query(oaiSet.getDefinition());
            studyIds = studyService.getVisibleStudies(studyIds, null);
            studyIds = studyService.getViewableStudies(studyIds);
            updateHarvestStudies( oaiSet.getSpec(), studyIds, updateTime );
        }
        
        // also do noset membet
        List<Long> studyIds = studyService.getAllNonHarvestedStudyIds();
        studyIds = studyService.getVisibleStudies(studyIds, null);
        studyIds = studyService.getViewableStudies(studyIds);        
        updateHarvestStudies( null, studyIds, updateTime );
        
    }    

    private void updateHarvestStudies(String setName, List<Long> studyIds, Date updateTime) {

        // create Map of HarvestStudies
        List<HarvestStudy> harvestStudies = findHarvestStudiesBySetName( setName );
        Map<String,HarvestStudy> hsMap = new HashMap();
        for (HarvestStudy hsEntry : harvestStudies) {
            hsMap.put(hsEntry.getGlobalId(), hsEntry);
        }


        for (Long studyId : studyIds) {
            Study study = studyService.getStudy(studyId);
            em.refresh(study); // workaround to get updated lastExportTime (to be investigated)
            
            if ( study.getLastExportTime() != null ) {     
                HarvestStudy hs = hsMap.get( study.getGlobalId() );
                if (hs == null) {
                    hs = new HarvestStudy( setName, study.getGlobalId(), updateTime );
                    em.persist(hs);                    
                } else {
                    if (hs.isRemoved()) {
                        hs.setRemoved(false);
                        hs.setLastUpdateTime( updateTime );
                    } else if (study.getLastExportTime().after( hs.getLastUpdateTime() ) ) {
                        hs.setLastUpdateTime( updateTime );
                    }

                    hsMap.remove(hs.getGlobalId());
                }
            }
        }

        // anything left in the map should be marked as removed
        markHarvestStudiesAsRemoved( hsMap.values(), updateTime);
        
    }
    
    
    public void markHarvestStudiesAsRemoved(Collection<HarvestStudy> harvestStudies, Date updateTime) {
        for (HarvestStudy hs : harvestStudies) {
            if ( !hs.isRemoved() ) {
                hs.setRemoved(true);
                hs.setLastUpdateTime(updateTime);
            }
        }
       
    }
    
    public HarvestStudy findHarvestStudyBySetNameandGlobalId(String setName, String globalId) {
        HarvestStudy harvestStudy = null;
        
        String queryString = "SELECT h from HarvestStudy h where h.globalId = :globalId";
        queryString += setName != null ? " and h.setName = :setName" : " and h.setName is null";
        
        Query query = em.createQuery(queryString).setParameter("globalId",globalId);
        if (setName != null) { query.setParameter("setName",setName); }        

        try {
           harvestStudy = (HarvestStudy) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
           // Do nothing, just return null. 
        }
        return harvestStudy;       
    }
    
    public List <HarvestStudy> findHarvestStudiesByGlobalId(String globalId) {
        String query="SELECT h from HarvestStudy h where h.globalId = :globalId";
        List<HarvestStudy> harvestStudies = em.createQuery(query).setParameter("globalId",globalId).getResultList();
        return harvestStudies;     
    }

    public List <HarvestStudy> findHarvestStudiesBySetName(String setName) {
        return findHarvestStudiesBySetName(setName, null, null);
    }    
    
    public List <HarvestStudy> findHarvestStudiesBySetName(String setName, Date from, Date until) {
        
        String queryString ="SELECT h from HarvestStudy h";
        queryString += setName != null ? " where h.setName = :setName" : " where h.setName is null";
        queryString += from != null ? " and h.lastUpdateTime >= :from" : "";
        queryString += until != null ? " and h.lastUpdateTime <= :until" : "";

        Query query = em.createQuery(queryString);
        if (setName != null) { query.setParameter("setName",setName); }
        if (from != null) { query.setParameter("from",from); }
        if (until != null) { query.setParameter("until",until); }
        
        return query.getResultList();        
    }
 
}
