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
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package edu.harvard.iq.dvn.core.harvest;

import java.io.Serializable;

/**
 *  This class is used when creating an EJB Timer for scheduling Harvesting.
 *  We use this class rather than the HarvestingDataverse entity because
 *  the class must be Serializable, and there is too much info associated with the HarvestingDataverse
 *  in order to realistically serialize it.  (We can't make related mapped entities transient.)
 *
 * @author Ellen Kraffmiller
 */
public class HarvestTimerInfo implements Serializable {
    private Long harvestingDataverseId;
    private String name;
    private String schedulePeriod;
    private Integer scheduleHourOfDay;
    
    public HarvestTimerInfo() {
        
    }
    
   
    public HarvestTimerInfo(Long harvestingDataverseId, String name, String schedulePeriod, Integer scheduleHourOfDay, Integer scheduleDayOfWeek) {
        this.harvestingDataverseId=harvestingDataverseId;
        this.name=name;
        this.schedulePeriod=schedulePeriod;
        this.scheduleDayOfWeek=scheduleDayOfWeek;
        this.scheduleHourOfDay=scheduleHourOfDay;
    }
    
    
    public Long getHarvestingDataverseId() {
        return harvestingDataverseId;
    }

    public void setHarvestingDataverseId(Long harvestingDataverseId) {
        this.harvestingDataverseId = harvestingDataverseId;
    }    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchedulePeriod() {
        return schedulePeriod;
    }

    public void setSchedulePeriod(String schedulePeriod) {
        this.schedulePeriod = schedulePeriod;
    }

    public Integer getScheduleHourOfDay() {
        return scheduleHourOfDay;
    }

    public void setScheduleHourOfDay(Integer scheduleHourOfDay) {
        this.scheduleHourOfDay = scheduleHourOfDay;
    }

    public Integer getScheduleDayOfWeek() {
        return scheduleDayOfWeek;
    }

    public void setScheduleDayOfWeek(Integer scheduleDayOfWeek) {
        this.scheduleDayOfWeek = scheduleDayOfWeek;
    }
    private Integer scheduleDayOfWeek;
  
    
}
