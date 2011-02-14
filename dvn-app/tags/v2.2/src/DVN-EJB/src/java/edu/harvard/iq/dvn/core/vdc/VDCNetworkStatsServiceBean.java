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
