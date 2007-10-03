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
 * VDCNetworkServiceBean.java
 *
 * Created on October 26, 2006, 11:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import edu.harvard.hmdc.vdcnet.harvest.HarvestTimerInfo;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyExporter;
import edu.harvard.hmdc.vdcnet.study.StudyExporterFactoryLocal;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class VDCNetworkServiceBean implements VDCNetworkServiceLocal {

    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    @Resource javax.ejb.TimerService timerService;
    @EJB MailServiceLocal mailService;
     @EJB StudyServiceLocal studyService;
  
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceBean");    
    /**
     * Creates a new instance of VDCNetworkServiceBean
     */
    public VDCNetworkServiceBean() {
    }

    public void create(VDCNetwork vDCNetwork) {
        em.persist(vDCNetwork);
    }

    public void edit(VDCNetwork vDCNetwork) {
        em.merge(vDCNetwork);
        this.updateExportTimer();
    }

    public void destroy(VDCNetwork vDCNetwork) {
        em.merge(vDCNetwork);
        em.remove(vDCNetwork);
    }

    public VDCNetwork find(Object pk) {
        return (VDCNetwork) em.find(VDCNetwork.class, pk);
    }

  
    
    public VDCNetwork find() {
        return (VDCNetwork) em.find(VDCNetwork.class, new Long(1));
    }
    
      
        
    
    private void removeExportTimer() {
        // Clear dataverse timer, if one exists 
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo() instanceof ExportTimerInfo ) {
                    timer.cancel();
                
            }    
        } 
    }
    
    public void updateExportTimer() {
        removeExportTimer();
        createExportTimer();
    }
    
    private void createExportTimer() {
            VDCNetwork vdcNetwork = this.find();
            long intervalDuration=0;
            Calendar initExpiration = Calendar.getInstance();
            initExpiration.set(Calendar.MINUTE, 0);
            initExpiration.set(Calendar.SECOND, 0);
            if (vdcNetwork.getExportPeriod().equals(vdcNetwork.EXPORT_PERIOD_DAILY)) {
                 intervalDuration = 1000*60 *60*24; 
                 initExpiration.set(Calendar.HOUR_OF_DAY, vdcNetwork.getExportHourOfDay());  

            } else if (vdcNetwork.getExportPeriod().equals(vdcNetwork.EXPORT_PERIOD_WEEKLY)) {
                intervalDuration = 1000*60 *60*24*7; 
                initExpiration.set(Calendar.HOUR_OF_DAY, vdcNetwork.getExportHourOfDay());
                initExpiration.set(Calendar.DAY_OF_WEEK, vdcNetwork.getExportDayOfWeek());

            } else {
                logger.log(Level.WARNING, "Could not set timer for export, unknown schedule period: "+ vdcNetwork.getExportPeriod());
                return;
            }
            Date  initExpirationDate = initExpiration.getTime();
            Date currTime = new Date();
            if (initExpirationDate.before(currTime)) {
                initExpirationDate.setTime(initExpiration.getTimeInMillis()+intervalDuration);
            }
            logger.log(Level.INFO, "Setting timer for export, initial expiration: "+ initExpirationDate);
            String exportPeriod = vdcNetwork.getExportPeriod();
            Integer exportHourOfDay= vdcNetwork.getExportHourOfDay();
            Integer exportDayOfWeek = vdcNetwork.getExportDayOfWeek();
            ExportTimerInfo exportTimerInfo = new ExportTimerInfo(exportPeriod,exportHourOfDay, exportDayOfWeek);
            logger.info("Just checking timerService: "+timerService.getTimers());
            timerService.createTimer(initExpirationDate, intervalDuration,exportTimerInfo);
      
    }

    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void handleTimeout(javax.ejb.Timer timer) {
        // We have to put all the code in a try/catch block because
        // if an exception is thrown from this method, Glassfish will automatically
        // call the method a second time. (The minimum number of re-tries for a Timer method is 1)
        try {
            if (timer.getInfo() instanceof ExportTimerInfo) {
                ExportTimerInfo info = (ExportTimerInfo)timer.getInfo();
                logger.info("handling timeout");
                studyService.exportUpdatedStudies();             
            }
         } catch (Throwable e) {
            mailService.sendHarvestErrorNotification(find().getContactEmail());
           logException(e,logger);
        }
    }
    
     private void logException(Throwable e, Logger logger) {
     
       boolean cause=false;
       String fullMessage = "";
        do  {   
            String message = e.getClass().getName()+ " " +e.getMessage();
            if (cause) {
                message = "\nCaused By Exception.................... "+e.getClass().getName()+" "+e.getMessage();
            }
            StackTraceElement[] ste = e.getStackTrace();
            message+= "\nStackTrace: \n";
            for(int m=0;m<ste.length;m++) {
                message+=ste[m].toString()+"\n";
            }
            fullMessage+=message;
            cause=true;
        } while ((e=e.getCause())!=null);
         logger.severe(fullMessage);
    }    
}
