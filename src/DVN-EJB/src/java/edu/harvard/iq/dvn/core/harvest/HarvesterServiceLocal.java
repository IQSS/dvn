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
 * HarvesterServiceLocal.java
 *
 * Created on May 1, 2007, 1:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.harvest;

import edu.harvard.hmdc.vdcnet.jaxb.oai.ResumptionTokenType;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverse;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.xml.sax.SAXException;

/**
 *
 * @author Ellen Kraffmiller
 */ 
public interface HarvesterServiceLocal extends java.io.Serializable  {
    public void createScheduledHarvestTimers();
    public void updateHarvestTimer(HarvestingDataverse dataverse);
    public void removeHarvestTimer(HarvestingDataverse dataverse);
    public List<HarvestTimerInfo> getHarvestTimers();
    public void doAsyncHarvest(HarvestingDataverse dataverse);

    public List<SetDetailBean> getSets(String oaiUrl); 
    public List<String> getMetadataFormats(String oaiUrl);
    public ResumptionTokenType harvestFromIdentifiers(Logger hdLogger, ResumptionTokenType resumptionToken, 
            HarvestingDataverse dataverse, String from, String until, List<Long> harvestedStudyIds, List<String> failedIdentifiers,
            MutableBoolean harvestErrorOccurred) throws java.io.IOException, ParserConfigurationException, SAXException, TransformerException, JAXBException;
    
    public Long getRecord(HarvestingDataverse dataverse, String identifier, String metadataPrefix);
    public Long getRecord(Logger hdLogger, HarvestingDataverse dataverse, String identifier, String metadataPrefix, MutableBoolean harvestErrorOccurred);

    public List<HarvestFormatType> findAllHarvestFormatTypes();    
    public HarvestFormatType findHarvestFormatTypeByMetadataPrefix(String metadataPrefix);

    public void doHarvesting(java.lang.Long dataverseId) throws java.io.IOException;
  
}
