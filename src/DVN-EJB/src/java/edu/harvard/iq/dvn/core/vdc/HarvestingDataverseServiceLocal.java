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
package edu.harvard.iq.dvn.core.vdc;

import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 * This is the business interface for HarvestDataverseService enterprise bean.
 */
@Local
public interface HarvestingDataverseServiceLocal extends java.io.Serializable {

    public List findAll();
    public List<Object[]> findInfoAll(); // returns harvesting dv id and vdc name

    public HarvestingDataverse find(Long id);

    public void edit(HarvestingDataverse harvestingDataverse);

    public void delete(Long hdId);

    public void setHarvestingNow(Long hdId, boolean harvestingNow);

    public void setLastHarvestTime(Long hdId, Date lastHarvestTime);

    public boolean getHarvestingNow(Long hdId);

    public Date getLastHarvestTime(Long hdId);

    public void resetAllHarvestingStatus();

    public void resetHarvestingStatus(Long hdId);

    public void setLastSuccessfulHarvestTime(Long hdId, Date lastHarvestTime);

    public void setHarvestSuccess(Long hdId, Date harvestSuccessTime, int harvestedStudyCount, int failedCount);

    public void setHarvestSuccessNotEmpty(Long hdId, Date currentTime, int harvestedCount, int failedCount);

    public void setHarvestFailure(Long hdId, int harvestedStudyCount, int failedCount);

    public void setHarvestResult(Long hdId, String result);
}
