
package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.ejb.Local;


/**
 * This is the business interface for HarvestDataverseService enterprise bean.
 */
@Local
public interface HarvestingDataverseServiceLocal {
     
    public List findAll();
    public void edit(HarvestingDataverse harvestingDataverse);
    public void delete(Long hdId);
}
