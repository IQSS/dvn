/*
 * HarvesterServiceLocal.java
 *
 * Created on May 1, 2007, 1:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.harvest;

import edu.harvard.hmdc.vdcnet.jaxb.oai.ResumptionTokenType;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Ellen Kraffmiller
 */
public interface HarvesterServiceLocal {
    public void createHarvestTimer();
    public void doAsyncHarvest(HarvestingDataverse dataverse);

    public List<SetDetailBean> getSets(String oaiUrl); 
    public List<String> getMetadataFormats(String oaiUrl);
    public ResumptionTokenType harvestFromIdentifiers(Logger hdLogger, ResumptionTokenType resumptionToken, HarvestingDataverse dataverse, String from, String until, List<Long> harvestedStudyIds);
    public Long getRecord(Logger hdLogger, HarvestingDataverse dataverse, String identifier, String metadataPrefix);
          
}
