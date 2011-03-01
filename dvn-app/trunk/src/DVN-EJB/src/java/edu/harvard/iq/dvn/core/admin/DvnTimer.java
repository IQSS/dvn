/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.harvest.HarvestTimerInfo;
import edu.harvard.iq.dvn.core.harvest.HarvesterServiceLocal;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.ExportTimerInfo;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverse;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Timer;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class DvnTimer implements DvnTimerRemote {
    @Resource
    javax.ejb.TimerService timerService;
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    private EntityManager em;
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.admin.DvnTimer");
    @EJB
    HarvesterServiceLocal harvesterService;
    @EJB
    HarvestingDataverseServiceLocal harvestingDataverseService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @EJB
    MailServiceLocal mailService;
    @EJB
    StudyServiceLocal studyService;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public void createTimer(Date initialExpiration, long intervalDuration, Serializable info) {
        try {
            logger.log(Level.INFO,"Creating timer on " + InetAddress.getLocalHost().getCanonicalHostName());
        } catch (UnknownHostException ex) {
            Logger.getLogger(DvnTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
        timerService.createTimer(initialExpiration, intervalDuration, info);
    }


    /**
     * This method is called whenever an EJB Timer goes off.
     * Check to see if this is a Harvest Timer, and if it is
     * Run the harvest for the given (scheduled) dataverse
     * @param timer
     */
    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void handleTimeout(javax.ejb.Timer timer) {
        // We have to put all the code in a try/catch block because
        // if an exception is thrown from this method, Glassfish will automatically
        // call the method a second time. (The minimum number of re-tries for a Timer method is 1)

        try {
            logger.log(Level.INFO,"Handling timeout on " + InetAddress.getLocalHost().getCanonicalHostName());
        } catch (UnknownHostException ex) {
            Logger.getLogger(DvnTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (timer.getInfo() instanceof HarvestTimerInfo) {
            HarvestTimerInfo info = (HarvestTimerInfo) timer.getInfo();
            try {

                logger.log(Level.INFO, "DO HARVESTING of dataverse " + info.getHarvestingDataverseId());
                harvesterService.doHarvesting(info.getHarvestingDataverseId());

            } catch (Throwable e) {
                harvestingDataverseService.setHarvestResult(info.getHarvestingDataverseId(), HarvestingDataverse.HARVEST_RESULT_FAILED);
                mailService.sendHarvestErrorNotification(vdcNetworkService.find().getContactEmail(), vdcNetworkService.find().getName());
                logException(e, logger);
            }
        }
        if (timer.getInfo() instanceof ExportTimerInfo) {
            try {
                ExportTimerInfo info = (ExportTimerInfo) timer.getInfo();
                logger.info("handling timeout");
                studyService.exportUpdatedStudies();
            } catch (Throwable e) {
                mailService.sendExportErrorNotification(vdcNetworkService.find().getContactEmail(), vdcNetworkService.find().getName());
                logException(e, logger);
            }
        }

    }

    public void removeHarvestTimer(HarvestingDataverse dataverse) {
         // Clear dataverse timer, if one exists
        try {
            logger.log(Level.INFO,"Removing harvest timer on " + InetAddress.getLocalHost().getCanonicalHostName());
        } catch (UnknownHostException ex) {
            Logger.getLogger(DvnTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo() instanceof HarvestTimerInfo) {
                HarvestTimerInfo info = (HarvestTimerInfo) timer.getInfo();
                if (info.getHarvestingDataverseId().equals(dataverse.getId())) {
                    timer.cancel();
                }
            }
        }
    }

    public void createExportTimer() {
            VDCNetwork vdcNetwork= (VDCNetwork) em.find(VDCNetwork.class, new Long(1));
            long intervalDuration=0;
            Calendar initExpiration = Calendar.getInstance();
            initExpiration.set(Calendar.MINUTE, 0);
            initExpiration.set(Calendar.SECOND, 0);
            if (StringUtil.isEmpty(vdcNetwork.getExportPeriod())) {
                logger.log(Level.INFO, "No export period found, export not scheduled.");
                return;
            } else if (vdcNetwork.getExportPeriod().equals(VDCNetwork.EXPORT_PERIOD_WEEKLY)) {
                intervalDuration = 1000*60 *60*24*7;
                initExpiration.set(Calendar.HOUR_OF_DAY, vdcNetwork.getExportHourOfDay());
                initExpiration.set(Calendar.DAY_OF_WEEK, vdcNetwork.getExportDayOfWeek());

                logger.log(Level.INFO, "Scheduling weekly export");


            } else if (vdcNetwork.getExportPeriod().equals(vdcNetwork.EXPORT_PERIOD_DAILY)) {
                 intervalDuration = 1000*60 *60*24;
                 initExpiration.set(Calendar.HOUR_OF_DAY, vdcNetwork.getExportHourOfDay());
              //   initExpiration.set(Calendar.MINUTE,15);  //REMOVE!!!!!
                 logger.log(Level.INFO, "Scheduling daily export");

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

            logger.info("Just checking timerService: ");
            for (Object timer:timerService.getTimers()) {
                logger.info("Found timer: "+((Timer)timer).getInfo());
            }
//            timerService.createTimer(initExpirationDate, intervalDuration,exportTimerInfo);
            createTimer(initExpirationDate, intervalDuration, exportTimerInfo);

    }

     public void createExportTimer(VDCNetwork vdcNetwork) {
            long intervalDuration=0;
            Calendar initExpiration = Calendar.getInstance();
            initExpiration.set(Calendar.MINUTE, 0);
            initExpiration.set(Calendar.SECOND, 0);
            if (StringUtil.isEmpty(vdcNetwork.getExportPeriod())) {
                logger.log(Level.INFO, "No export period found, export not scheduled.");
                return;
            } else if (vdcNetwork.getExportPeriod().equals(VDCNetwork.EXPORT_PERIOD_WEEKLY)) {
                intervalDuration = 1000*60 *60*24*7;
                initExpiration.set(Calendar.HOUR_OF_DAY, vdcNetwork.getExportHourOfDay());
                initExpiration.set(Calendar.DAY_OF_WEEK, vdcNetwork.getExportDayOfWeek());

                logger.log(Level.INFO, "Scheduling weekly export");


            } else if (vdcNetwork.getExportPeriod().equals(vdcNetwork.EXPORT_PERIOD_DAILY)) {
                 intervalDuration = 1000*60 *60*24;
                 initExpiration.set(Calendar.HOUR_OF_DAY, vdcNetwork.getExportHourOfDay());
              //   initExpiration.set(Calendar.MINUTE,15);  //REMOVE!!!!!
                 logger.log(Level.INFO, "Scheduling daily export");

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

            logger.info("Just checking timerService: ");
            for (Object timer:timerService.getTimers()) {
                logger.info("Found timer: "+((Timer)timer).getInfo());
            }
//            timerService.createTimer(initExpirationDate, intervalDuration,exportTimerInfo);
            createTimer(initExpirationDate, intervalDuration, exportTimerInfo);

    }

     public void removeExportTimer() {
        // Clear dataverse timer, if one exists
        try {
            logger.log(Level.INFO,"Removing export timer on " + InetAddress.getLocalHost().getCanonicalHostName());
        } catch (UnknownHostException ex) {
            Logger.getLogger(DvnTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo() instanceof ExportTimerInfo ) {
                    timer.cancel();

            }
        }
    }



    private void createHarvestTimer(HarvestingDataverse dataverse) {
        if (dataverse.isScheduled()) {
            long intervalDuration = 0;
            Calendar initExpiration = Calendar.getInstance();
            initExpiration.set(Calendar.MINUTE, 0);
            initExpiration.set(Calendar.SECOND, 0);
            if (dataverse.getSchedulePeriod().equals(HarvestingDataverse.SCHEDULE_PERIOD_DAILY)) {
                intervalDuration = 1000 * 60 * 60 * 24;
                initExpiration.set(Calendar.HOUR_OF_DAY, dataverse.getScheduleHourOfDay());

            } else if (dataverse.getSchedulePeriod().equals(dataverse.SCHEDULE_PERIOD_WEEKLY)) {
                intervalDuration = 1000 * 60 * 60 * 24 * 7;
                initExpiration.set(Calendar.HOUR_OF_DAY, dataverse.getScheduleHourOfDay());
                initExpiration.set(Calendar.DAY_OF_WEEK, dataverse.getScheduleDayOfWeek());

            } else {
                logger.log(Level.WARNING, "Could not set timer for dataverse id, " + dataverse.getId() + ", unknown schedule period: " + dataverse.getSchedulePeriod());
                return;
            }
            Date initExpirationDate = initExpiration.getTime();
            Date currTime = new Date();
            if (initExpirationDate.before(currTime)) {
                initExpirationDate.setTime(initExpiration.getTimeInMillis() + intervalDuration);
            }
            logger.log(Level.INFO, "Setting timer for dataverse " + dataverse.getVdc().getName() + ", initial expiration: " + initExpirationDate);
//            timerService.createTimer(initExpirationDate, intervalDuration, new HarvestTimerInfo(dataverse.getId(), dataverse.getVdc().getName(), dataverse.getSchedulePeriod(), dataverse.getScheduleHourOfDay(), dataverse.getScheduleDayOfWeek()));
            createTimer(initExpirationDate, intervalDuration, new HarvestTimerInfo(dataverse.getId(), dataverse.getVdc().getName(), dataverse.getSchedulePeriod(), dataverse.getScheduleHourOfDay(), dataverse.getScheduleDayOfWeek()));
        }
    }

     private void logException(Throwable e, Logger logger) {

        boolean cause = false;
        String fullMessage = "";
        do {
            String message = e.getClass().getName() + " " + e.getMessage();
            if (cause) {
                message = "\nCaused By Exception.................... " + e.getClass().getName() + " " + e.getMessage();
            }
            StackTraceElement[] ste = e.getStackTrace();
            message += "\nStackTrace: \n";
            for (int m = 0; m < ste.length; m++) {
                message += ste[m].toString() + "\n";
            }
            fullMessage += message;
            cause = true;
        } while ((e = e.getCause()) != null);
        logger.severe(fullMessage);
    }

    public void updateHarvestTimer(HarvestingDataverse dataverse) {
        removeHarvestTimer(dataverse);
        createHarvestTimer(dataverse);
    }

}
