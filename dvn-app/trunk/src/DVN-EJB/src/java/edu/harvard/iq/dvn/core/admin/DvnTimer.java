/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.harvest.HarvestTimerInfo;
import edu.harvard.iq.dvn.core.harvest.HarvesterServiceLocal;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.ExportTimerInfo;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverse;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Timer;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class DvnTimer implements DvnTimerRemote {
    @Resource
    javax.ejb.TimerService timerService;
    
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

}
