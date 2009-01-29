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
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkStatsServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
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
 @EJB(name="harvesterService", beanInterface=edu.harvard.hmdc.vdcnet.harvest.HarvesterServiceLocal.class),
 @EJB(name="harvestingDataverseService", beanInterface=edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceLocal.class),
 @EJB(name="vdcNetworkService", beanInterface=edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal.class),
 @EJB(name="vdcNetworkStatsService", beanInterface=edu.harvard.hmdc.vdcnet.vdc.VDCNetworkStatsServiceLocal.class),
 @EJB(name="indexService", beanInterface=edu.harvard.hmdc.vdcnet.index.IndexServiceLocal.class)
})
public class VDCContextListener implements ServletContextListener,ServletRequestAttributeListener, java.io.Serializable  {
  

    public void contextDestroyed(ServletContextEvent event) {
    
    }
    
    public void contextInitialized(ServletContextEvent event) {
       
        HarvesterServiceLocal harvesterService = null;
        HarvestingDataverseServiceLocal harvestingDataverseService = null;
        IndexServiceLocal indexService = null;
        VDCNetworkStatsServiceLocal vdcNetworkStatsService= null;
         
        try {
            harvesterService=(HarvesterServiceLocal)new InitialContext().lookup("java:comp/env/harvesterService");
            harvestingDataverseService=(HarvestingDataverseServiceLocal)new InitialContext().lookup("java:comp/env/harvestingDataverseService");
            indexService=(IndexServiceLocal)new InitialContext().lookup("java:comp/env/indexService");
           
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            harvestingDataverseService.resetAllHarvestingStatus();         
            harvesterService.createScheduledHarvestTimers();
            indexService.createIndexTimer();
            indexService.createIndexNotificationTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        VDCNetworkServiceLocal vdcNetworkService=null;
        
        try {
             vdcNetworkService = (VDCNetworkServiceLocal)new InitialContext().lookup("java:comp/env/vdcNetworkService");
            vdcNetworkService.updateExportTimer();
        } catch(Exception e) {
            e.printStackTrace();
        }


      
         try {
             vdcNetworkStatsService = (VDCNetworkStatsServiceLocal)new InitialContext().lookup("java:comp/env/vdcNetworkStatsService");
             vdcNetworkStatsService.updateStats();
            vdcNetworkStatsService.createStatsTimer();
        } catch(Exception e) {
            e.printStackTrace();
        }     
     

    }
    /** Creates a new instance of VDCContextListener */
    public VDCContextListener() {
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
           
            bean.init();
  //      } catch (Exception e) {
  //          log(e.getMessage(), e);
  //          ViewHandlerImpl.cache(FacesContext.getCurrentInstance(), e);
  //      }

    }
 public void attributeRemoved(ServletRequestAttributeEvent event) {}
 public void attributeReplaced(ServletRequestAttributeEvent event) {}

        // If the new value is an 
}
