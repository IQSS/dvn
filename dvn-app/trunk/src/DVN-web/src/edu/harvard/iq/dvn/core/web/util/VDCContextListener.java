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

package edu.harvard.iq.dvn.core.web.util;



import edu.harvard.iq.dvn.core.harvest.HarvesterServiceLocal;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkStatsServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;

/**
 *
 * 
 */
@EJBs({
 @EJB(name="harvesterService", beanInterface=edu.harvard.iq.dvn.core.harvest.HarvesterServiceLocal.class),
 @EJB(name="harvestingDataverseService", beanInterface=edu.harvard.iq.dvn.core.vdc.HarvestingDataverseServiceLocal.class),
 @EJB(name="vdcNetworkService", beanInterface=edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal.class),
 @EJB(name="vdcNetworkStatsService", beanInterface=edu.harvard.iq.dvn.core.vdc.VDCNetworkStatsServiceLocal.class),
 @EJB(name="indexService", beanInterface=edu.harvard.iq.dvn.core.index.IndexServiceLocal.class)
})
public class VDCContextListener implements ServletContextListener,ServletRequestAttributeListener, java.io.Serializable   {

    public void contextDestroyed(ServletContextEvent event) {
    
    }
    
    public void contextInitialized(ServletContextEvent event) {
            
        if (PropertyUtil.isTimerServer()) {
            System.out.println("dvn.timerServer== true, Setting DVN timers.");
            initTimers();
           
        } else {
            System.out.println("dvn.timerServer== false; DVN Timers not set.");
        }
    }
     
    
    private void initTimers() {
    
        HarvesterServiceLocal harvesterService = null;
        HarvestingDataverseServiceLocal harvestingDataverseService = null;
        IndexServiceLocal indexService = null;
        VDCNetworkStatsServiceLocal vdcNetworkStatsService= null;
        VDCNetworkServiceLocal vdcNetworkService=null;
         
        try {
            harvesterService=(HarvesterServiceLocal)new InitialContext().lookup("java:comp/env/harvesterService");
            harvestingDataverseService=(HarvestingDataverseServiceLocal)new InitialContext().lookup("java:comp/env/harvestingDataverseService");
            vdcNetworkService = (VDCNetworkServiceLocal)new InitialContext().lookup("java:comp/env/vdcNetworkService");        
            indexService=(IndexServiceLocal)new InitialContext().lookup("java:comp/env/indexService");
            vdcNetworkStatsService = (VDCNetworkStatsServiceLocal)new InitialContext().lookup("java:comp/env/vdcNetworkStatsService");
           
       
            harvestingDataverseService.resetAllHarvestingStatus();         
            harvesterService.createScheduledHarvestTimers();
            
            indexService.createIndexTimer();
            indexService.createIndexNotificationTimer();
    
            vdcNetworkService.updateExportTimer();
            
            vdcNetworkStatsService.updateStats();
            vdcNetworkStatsService.createStatsTimer();


        } catch (Exception e) {
            e.printStackTrace();
        }
        
     
        
   


      
              
    }
  public void attributeAdded(ServletRequestAttributeEvent event) {
      Object value = event.getValue();
        if (value != null) {
            if (value instanceof VDCBaseBean) {
                fireInit((VDCBaseBean) value);
            } 
        }
     }
  
    /**
     * <p>Fire an init event on an AbstractPageBean.</p>
     *
     * @param bean {@link AbstractPageBean} to fire event on
     */
    private void fireInit(VDCBaseBean bean) {

      //  try {
            bean.init(); // -- Commenting out - L.A.
  //      } catch (Exception e) {
  //          log(e.getMessage(), e);
  //          ViewHandlerImpl.cache(FacesContext.getCurrentInstance(), e);
  //      }

    }
 public void attributeRemoved(ServletRequestAttributeEvent event) {}
 public void attributeReplaced(ServletRequestAttributeEvent event) {}

        // If the new value is an 
}
