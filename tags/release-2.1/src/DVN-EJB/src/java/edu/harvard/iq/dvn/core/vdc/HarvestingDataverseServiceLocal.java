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

    public void setHarvestFailure(Long hdId, int harvestedStudyCount, int failedCount);

    public void setHarvestResult(Long hdId, String result);
}
