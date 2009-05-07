/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.harvest;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Gustavo
 */
@Local
public interface HarvestStudyServiceLocal {
    
    public void updateHarvestStudies();
    
    public void markHarvestStudiesAsRemoved(Collection<HarvestStudy> harvestStudies, Date updateTime);
    
    public HarvestStudy findHarvestStudyBySetNameandGlobalId(String setName, String globalId);
    public List <HarvestStudy> findHarvestStudiesByGlobalId(String globalId);
    public List <HarvestStudy> findHarvestStudiesBySetName(String setName);
    public List <HarvestStudy> findHarvestStudiesBySetName(String setName, Date from, Date until);
}
