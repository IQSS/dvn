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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import java.io.Serializable;
import java.util.Date;
import javax.ejb.Timer;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 *
 * @author roberttreacy
 */
@Remote
public interface DvnTimerRemote extends java.io.Serializable {

    void createTimer(Date initialExpiration, long intervalDuration, Serializable info);

    @javax.ejb.Timeout
    @javax.ejb.TransactionAttribute(value = javax.ejb.TransactionAttributeType.NOT_SUPPORTED)
    public void handleTimeout(javax.ejb.Timer timer);

    public void removeHarvestTimer(edu.harvard.iq.dvn.core.vdc.HarvestingDataverse dataverse);

    public void removeExportTimer();

    public void createExportTimer();

    public void createExportTimer(edu.harvard.iq.dvn.core.vdc.VDCNetwork vdcNetwork);

    public void updateHarvestTimer(edu.harvard.iq.dvn.core.vdc.HarvestingDataverse dataverse);
 
    public void removeHarvestTimers();
}
