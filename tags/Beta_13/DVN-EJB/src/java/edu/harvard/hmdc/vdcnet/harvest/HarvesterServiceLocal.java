/*
 * HarvesterServiceLocal.java
 *
 * Created on May 1, 2007, 1:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.harvest;

import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import java.util.List;
import javax.xml.bind.JAXBContext;

/**
 *
 * @author Ellen Kraffmiller
 */
public interface HarvesterServiceLocal {
    public void createHarvestTimer();
    public void doAsyncHarvest(HarvestingDataverse dataverse);
    public void harvest(HarvestingDataverse dataverse, String from, String until);
    public List<SetDetailBean> getSets(String oaiUrl); 
    public List<String> getMetadataFormats(String oaiUrl);
   
}
