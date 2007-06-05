/*
 * HarvestSitesPage.java
 *
 * Created on April 5, 2007, 10:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.site;

import edu.harvard.hmdc.vdcnet.harvest.HarvesterServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import java.util.List;
import javax.ejb.EJB;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Ellen Kraffmiller
 */
public class HarvestSitesPage extends VDCBaseBean {
              @EJB HarvestingDataverseServiceLocal harvestingDataverseService;
              @EJB HarvesterServiceLocal harvesterService;
              @EJB VDCServiceLocal vdcService;

    /** Creates a new instance of HarvestSitesPage */
    public HarvestSitesPage() {
    }
    
    public void init(){
        super.init();
    }

    /**
     * Holds value of property harvestSiteList.
     */
    private List<HarvestingDataverse> harvestSiteList;

    /**
     * Getter for property harvestSiteList.
     * @return Value of property harvestSiteList.
     */
    public List<HarvestingDataverse> getHarvestSiteList() {
  
               return harvestingDataverseService.findAll();
               
    
    }

      /**
     * Getter for property harvestSiteList.
     * @return Value of property harvestSiteList.
     */
    public List<VDC> getDataverseSiteList() {
  
               return vdcService.findAllNonHarvesting();
               
    
    }


    /**
     * Holds value of property harvestDataTable.
     */
    private HtmlDataTable harvestDataTable;

    /**
     * Getter for property siteDataTable.
     * @return Value of property siteDataTable.
     */
    public HtmlDataTable getHarvestDataTable() {
        return this.harvestDataTable;
    }
    
    
    public void doSchedule(ActionEvent ae) {
            HarvestingDataverse hd = (HarvestingDataverse)this.harvestDataTable.getRowData();
            hd.setScheduled(true);
            harvestingDataverseService.edit(hd);
    }
    
    public void doUnschedule(ActionEvent ae) {
            HarvestingDataverse hd = (HarvestingDataverse)this.harvestDataTable.getRowData();
            hd.setScheduled(false);
            harvestingDataverseService.edit(hd);     
    }

   public void doHarvestNow(ActionEvent ae) {
   
    }
   
    
    /**
     * Setter for property siteDataTable.
     * @param siteDataTable New value of property siteDataTable.
     */
    public void setHarvestDataTable(HtmlDataTable harvestDataTable) {
        this.harvestDataTable = harvestDataTable;
    }
    
    public void doRunNow(ActionEvent ae) {
   
         HarvestingDataverse hd = (HarvestingDataverse)this.harvestDataTable.getRowData();
        // TODO: replace this with lastUpdateTime when after we remove call to stateful session bean in harvesterService
         harvesterService.harvest(hd,null,null);
    //       harvesterService.getRecord(hd,"hdl:1902.2/06635",hd.getFormat(),null);
     //       harvesterService.getRecord(hd,"hdl:1902.2/zzzzzzz",hd.getFormat(),null);
   }
    
    public void doRemoveHarvestDataverse(ActionEvent ae) {
           HarvestingDataverse hd = (HarvestingDataverse)this.harvestDataTable.getRowData();      
           harvestingDataverseService.delete(hd.getId());
        
        
    }
       public void doRemoveDataverse(ActionEvent ae) {
           VDC vdc = (VDC)this.dataverseDataTable.getRowData();      
           vdcService.delete(vdc.getId());
       
        
    }

    /**
     * Holds value of property dataverseDataTable.
     */
    private HtmlDataTable dataverseDataTable;

    /**
     * Getter for property dataverseDataTable.
     * @return Value of property dataverseDataTable.
     */
    public HtmlDataTable getDataverseDataTable() {
        return this.dataverseDataTable;
    }

    /**
     * Setter for property dataverseDataTable.
     * @param dataverseDataTable New value of property dataverseDataTable.
     */
    public void setDataverseDataTable(HtmlDataTable dataverseDataTable) {
        this.dataverseDataTable = dataverseDataTable;
    }
}
