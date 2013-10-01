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
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
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
            vdcNetworkService = (VDCNetworkServiceLocal)new InitialContext().lookup("java:comp/env/vdcNetworkService");        
  
            if (vdcNetworkService.defaultTransactionReadOnly()) {
                System.out.println("Network is in read-only mode; skipping timer initialization.");
            } else {
                harvesterService = (HarvesterServiceLocal) new InitialContext().lookup("java:comp/env/harvesterService");
                harvestingDataverseService = (HarvestingDataverseServiceLocal) new InitialContext().lookup("java:comp/env/harvestingDataverseService");
                indexService = (IndexServiceLocal) new InitialContext().lookup("java:comp/env/indexService");
                vdcNetworkStatsService = (VDCNetworkStatsServiceLocal) new InitialContext().lookup("java:comp/env/vdcNetworkStatsService");


                harvestingDataverseService.resetAllHarvestingStatus();
                harvesterService.createScheduledHarvestTimers();

                indexService.createIndexTimer();
                indexService.createCollectionIndexTimer(); 
                indexService.createIndexNotificationTimer();

                vdcNetworkService.updateExportTimer();

                vdcNetworkStatsService.updateStats();
                vdcNetworkStatsService.createStatsTimer();
            }

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
