/*
 * VDCContextListener.java
 *
 * Created on February 6, 2007, 10:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import edu.harvard.hmdc.vdcnet.harvest.HarvesterServiceLocal;
import edu.harvard.hmdc.vdcnet.study.SyncVDCServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceLocal;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * 
 */
@EJBs({
 @EJB(name="harvesterService", beanInterface=edu.harvard.hmdc.vdcnet.harvest.HarvesterServiceLocal.class),
 @EJB(name="harvestingDataverseService", beanInterface=edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceLocal.class)
})
public class VDCContextListener implements ServletContextListener {
    @EJB SyncVDCServiceLocal syncVDCService;

    public void contextDestroyed(ServletContextEvent event) {
    
    }
    
    public void contextInitialized(ServletContextEvent event) {
        // This call initializes the Harvest Timer that will activate once a day and 
        // run all scheduled Harvest Dataverses.
      
        HarvesterServiceLocal harvesterService = null;
        HarvestingDataverseServiceLocal harvestingDataverseService = null;
         
        try {
            harvesterService=(HarvesterServiceLocal)new InitialContext().lookup("java:comp/env/harvesterService");
            harvestingDataverseService=(HarvestingDataverseServiceLocal)new InitialContext().lookup("java:comp/env/harvestingDataverseService");
           
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            harvestingDataverseService.resetHarvestingStatus();         
            harvesterService.createHarvestTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // This initializes the export Timer - runs once a day and exports changes to old VDC
        String export = event.getServletContext().getInitParameter("edu.harvard.hmdc.export");
        if (export!=null && export.equalsIgnoreCase("true")) {
            System.out.println("Found export initParameter, scheduling study export.");
            syncVDCService.scheduleDaily();
        } else {
            System.out.println("Export not scheduled.");
        }

    }
    /** Creates a new instance of VDCContextListener */
    public VDCContextListener() {
    }
    
}
