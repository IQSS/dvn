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
 * SyncVDCServiceBean.java
 *
 * Created on February 12, 2007, 3:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import java.util.Calendar;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.Timeout;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class SyncVDCServiceBean implements edu.harvard.hmdc.vdcnet.study.SyncVDCServiceLocal {
    @Resource javax.ejb.TimerService timerService;
    @EJB StudyServiceLocal studyService;
    /**
     * Creates a new instance of SyncVDCServiceBean
     */
    public SyncVDCServiceBean() {
        
    }
    
    /**
     *  For testing
     */
    public void scheduleNow(String lastUpdateTime, String authority ) {
           try {    
            studyService.exportStudyFilesToLegacySystem(lastUpdateTime,authority);
          } catch (Exception e) {
             throw new EJBException(e);
          }    
      
    }
    
    /**
     *  
     */
    public void scheduleDaily() {
        if (timerService.getTimers().isEmpty()){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR,1);
            cal.set(Calendar.HOUR_OF_DAY,1);  
            Date initialExpiration = cal.getTime();  // First timeout is 1:00 AM of next day 
            long intervalDuration = 1000*60 *60*24;  // repeat every 24 hours
            timerService.createTimer(initialExpiration, intervalDuration,null);
        }
        
    }
    
    @Timeout
    public void doVDCSync(javax.ejb.Timer timer) {
        String lastUpdateTime =(String)timer.getInfo();
         try {
           
            studyService.exportStudyFilesToLegacySystem(lastUpdateTime,null);
          } catch (Exception e) {
             throw new EJBException(e);
          }    
    }
    
    
  
    
}
