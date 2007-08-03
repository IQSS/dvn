
package edu.harvard.hmdc.vdcnet.vdc;

import java.util.Date;
import java.util.List;
import javax.ejb.Local;


/**
 * This is the business interface for HarvestDataverseService enterprise bean.
 */
@Local
public interface HarvestingDataverseServiceLocal {
     
    public List findAll();
    public HarvestingDataverse find(Long id);
    public void edit(HarvestingDataverse harvestingDataverse);
    public void delete(Long hdId);
    public void setHarvestingNow(Long hdId, boolean harvestingNow);
    public void setLastHarvestTime(Long hdId, Date lastHarvestTime);
    public boolean getHarvestingNow(Long hdId);
    public Date getLastHarvestTime(Long hdId);
    public void resetHarvestingStatus();
}
