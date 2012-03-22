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

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class VDCNetworkStatsServiceBean implements VDCNetworkStatsServiceLocal {
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    @Resource
    javax.ejb.TimerService timerService;

    @EJB VDCNetworkServiceLocal vdcNetworkService;

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
        return (VDCNetworkStats) em.find(VDCNetworkStats.class, new Long(1));
    }
    
    @Timeout
    public void handleTimeout(javax.ejb.Timer timer) {
        logger.log(Level.FINE,"in handleTimeout, timer = "+timer.getInfo());
        try {
            if (timer.getInfo().equals(STATS_TIMER)) {
                logger.log(Level.FINE, "Stats update");
                updateStats();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

  
    public void updateStats() {
        VDCNetworkStats vdcNetworkStats = (VDCNetworkStats) em.find(VDCNetworkStats.class, new Long(1));
        logger.log(Level.FINE, "found vdcNetworkStats" +vdcNetworkStats );
        Long releasedStudies = vdcNetworkService.getTotalStudies(true);
        Long releasedFiles = vdcNetworkService.getTotalFiles(true);
        logger.log(Level.FINE, "releasedStudies ="+releasedStudies+"releasedFiles="+releasedFiles);
        vdcNetworkStats.setStudyCount(releasedStudies);
        vdcNetworkStats.setFileCount(releasedFiles);
    }
}
