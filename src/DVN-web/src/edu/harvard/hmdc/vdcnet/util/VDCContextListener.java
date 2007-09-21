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
            harvesterService.createScheduledHarvestTimers();
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
