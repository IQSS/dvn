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
package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.web.common.VDCApplicationBean;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class VDCNetworkStatsServiceBean implements VDCNetworkStatsServiceLocal {
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    @Resource
    javax.ejb.TimerService timerService;

    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB StudyServiceLocal studyService;
    
    @Inject VDCApplicationBean vdcApplicationBean;

    private static final String STATS_TIMER = "StatsTimer";
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.index.VDCNetworkStatsServiceBean");
    public void createStatsTimer() {

        // Clear dataverse timer, if one exists
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo().equals(STATS_TIMER) ) {
                    // Cancelling pre-existing timer
                    timer.cancel();

            }
        }


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);
     

        logger.log(Level.INFO, "NetStats timer set for " + cal.getTime());
        Date initialExpiration = cal.getTime();  // First timeout is 1:00 AM of next day
        long intervalDuration = 1000 * 60 * 5;  // 5 minutes
        timerService.createTimer(initialExpiration, intervalDuration, STATS_TIMER);    }

    public VDCNetworkStats getVDCNetworkStats() {
        return (VDCNetworkStats) em.find(VDCNetworkStats.class, new Long(0));
    }
    

    public VDCNetworkStats getVDCNetworkStatsByNetworkId(Long networkId) {
        String query = "SELECT n from VDCNetworkStats n where n.vdcNetwork.id = :id";
        VDCNetworkStats vdcNetworkStats = null;
        try {
            vdcNetworkStats = (VDCNetworkStats) em.createQuery(query).setParameter("id", networkId).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            vdcNetworkStats = new VDCNetworkStats();
        }
        return vdcNetworkStats;
    }
    
    @Timeout
    public void handleTimeout(javax.ejb.Timer timer) {
        logger.log(Level.FINE,"in handleTimeout, timer = "+timer.getInfo());
        
        try {
            boolean readOnly = vdcNetworkService.defaultTransactionReadOnly();
            
            if (timer.getInfo().equals(STATS_TIMER)) {
                if (readOnly) {
                    logger.log(Level.ALL, "Network is in read-only mode; skipping scheduled network stats job."); 
                } else {
                    logger.log(Level.FINE, "Stats update");
                    updateStats();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

  
    public void updateStats() {
        
        List<VDCNetwork> vdcNetworks = vdcNetworkService.getVDCNetworks();        
        // update the stats in the db
        for (VDCNetwork vdcNetwork : vdcNetworks) {
            Long vdcNetwork_id = vdcNetwork.getId();
            VDCNetworkStats vdcNetworkStats = getVDCNetworkStatsByNetworkId(vdcNetwork_id);

            // root network
            if (vdcNetwork_id.intValue() == vdcNetworkService.findRootNetwork().getId().intValue()) {
                Long releasedStudies = vdcNetworkService.getTotalStudies(true);
                Long releasedFiles = vdcNetworkService.getTotalFiles(true);
                Long downloadCount = vdcNetworkService.getTotalDownloads(true);
                logger.log(Level.FINE, "releasedStudies =" + releasedStudies + "releasedFiles=" + releasedFiles);
                
                vdcNetworkStats.setStudyCount(releasedStudies);
                vdcNetworkStats.setFileCount(releasedFiles);
                vdcNetworkStats.setDownloadCount(downloadCount);
                
            } else { // subnetworks
                Long releasedStudies = vdcNetworkService.getTotalStudiesBySubnetwork(vdcNetwork_id, true);
                Long releasedFiles = vdcNetworkService.getTotalFilesBySubnetwork(vdcNetwork_id, true);
                Long downloadCount = vdcNetworkService.getTotalDownloadsBySubnetwork(vdcNetwork_id, true);
                List<Long> varList = new ArrayList();
                List<Study> studyObjects = (List) vdcNetwork.getLinkedStudies();
                        
                for (Study currentResult : studyObjects) {
                    varList.add(new Long(((Integer) currentResult.getId().intValue())));
                }
                if (!varList.isEmpty()){
                    releasedStudies +=varList.size();
                    downloadCount += studyService.getStudyDownloadCount(varList);
                    releasedFiles += studyService.getStudyFileCount(varList);
                }         
                
                vdcNetworkStats.setStudyCount(releasedStudies);
                vdcNetworkStats.setFileCount(releasedFiles);
                vdcNetworkStats.setDownloadCount(downloadCount);
            }
        }
        
        // update the Lists stored in application scope
        Map<Long, List> downloadMap = new HashMap<Long, List>();
        Map<Long, List> recentlyReleasedMap = new HashMap<Long, List>();
        
        for (VDCNetwork vdcNetwork : vdcNetworks){
            downloadMap.put(vdcNetwork.getId(), studyService.getMostDownloadedStudyIds(null, vdcNetwork.getId(), -1)); 
        }

        for (VDCNetwork vdcNetwork : vdcNetworks){
            recentlyReleasedMap.put(vdcNetwork.getId(), studyService.getRecentlyReleasedStudyIds(null, vdcNetwork.getId(), -1));           
        }      
        vdcApplicationBean.setAllStudyIdsByDownloadCountMap(downloadMap);
        vdcApplicationBean.setAllStudyIdsByReleaseDateMap(recentlyReleasedMap);
    }

}
