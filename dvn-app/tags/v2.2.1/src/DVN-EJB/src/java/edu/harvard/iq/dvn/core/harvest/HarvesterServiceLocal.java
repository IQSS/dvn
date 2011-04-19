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
