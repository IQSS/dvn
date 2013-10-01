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
 * VariableServiceBean.java
 *
 * Created on November 8, 2006, 3:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author gdurand
 */
@Stateless
public class VariableServiceBean implements edu.harvard.iq.dvn.core.study.VariableServiceLocal {
    
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    @EJB IndexServiceLocal indexService;
    @EJB StudyServiceLocal studyService;
    
    /** Creates a new instance of VariableServiceBean */
    public VariableServiceBean() {
    }
    
    public DataTable getDataTable(Long dtId) {
        DataTable dt = em.find(DataTable.class,dtId);
        if (dt==null) {
            throw new IllegalArgumentException("Unknown dtId: "+dtId);
        }
        
        
        return dt;
    }
    
    public DataVariable getDataVariable(Long dvId) {
        DataVariable dv = em.find(DataVariable.class,dvId);
        if (dv==null) {
            throw new IllegalArgumentException("Unknown dvId: "+dvId);
        }
        
        
        return dv;
    }
    
    public List getDataVariablesByFileOrder(Long dtId) {
        String queryStr = "SELECT dv FROM DataTable dt JOIN dt.dataVariables dv where dt.id = " + dtId +" ORDER BY dv.fileOrder";
        Query query =em.createQuery(queryStr);
        List <DataVariable> dvs = query.getResultList();
        
	// Before we return the list, we need to make an adjustment to 
	// convert the (possibly) relative column numbers to the 
	// absolute:


	int relOrder = 0; 
	int absOffset = -1; 

        for (Iterator el = dvs.iterator(); el.hasNext();) {
            DataVariable dv = (DataVariable) el.next();

	    if ( absOffset == -1 ) {
		absOffset = dv.getFileOrder(); 
		// This is the column number stored in the database
		// for the first variable in the data file.
		// We want to use it as the offset, i.e. to 
		// subtract it from all the other variable orders in the 
		// data table, thus converting them into absolute 
		// numbers. 

		if ( absOffset == 0 ) {
		    return dvs; 
		}
		// (of course, if the offset is 0, we don't need
		// to do anything!)
	    }

	    relOrder = dv.getFileOrder(); 
	    dv.setFileOrder(relOrder - absOffset); 
        }

	return dvs; 
    }
    
    public SummaryStatisticType findSummaryStatisticTypeByName(String name) {
        String query="SELECT t from SummaryStatisticType t where t.name = '"+name+"'";
        SummaryStatisticType type = null;
        try {
            type=(SummaryStatisticType)em.createQuery(query).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return type;
    }
    
    
  public List<SummaryStatisticType> findAllSummaryStatisticType() {
        String query="SELECT t from SummaryStatisticType t ";
        return em.createQuery(query).getResultList();
          
    }
    
    /**
     * Find type from prefetched list
     * TODO: find a place to put the global list
     */
    public SummaryStatisticType findSummaryStatisticTypeByName(List<SummaryStatisticType> typeList,String name) {
       SummaryStatisticType type = null;
       for (Iterator<SummaryStatisticType> it = typeList.iterator(); it.hasNext();) {
           SummaryStatisticType elem = it.next();
           if (elem.getName().equals(name)) {
               type=elem;
               break;
           }
       }
       return type;
    }      
        
    
 public List<VariableRangeType> findAllVariableRangeType() {
        String query="SELECT t from VariableRangeType t ";
        return em.createQuery(query).getResultList();
          
    }
    
    /**
     * Find type from prefetched list
     * TODO: find a place to put the global list
     */
    public VariableRangeType findVariableRangeTypeByName(List<VariableRangeType> typeList,String name) {
       VariableRangeType type = null;
       for (Iterator<VariableRangeType> it = typeList.iterator(); it.hasNext();) {
           VariableRangeType elem = it.next();
           if (elem.getName().equals(name)) {
               type=elem;
               break;
           }
       }
       return type;
    }          
    
    public VariableFormatType findVariableFormatTypeByName(String name) {
        String query="SELECT t from VariableFormatType t where t.name = '"+name+"'";
        VariableFormatType type = null;
        try {
            type=(VariableFormatType)em.createQuery(query).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return type;
    }
    
    public List<VariableFormatType> findAllVariableFormatType() {
        String query="SELECT t from VariableFormatType t ";
        return em.createQuery(query).getResultList();
          
    }
    
    /**
     * Find type from prefetched list
     * TODO: find a place to put the global list
     */
    public VariableFormatType findVariableFormatTypeByName(List<VariableFormatType> typeList,String name) {
       VariableFormatType type = null;
       for (Iterator<VariableFormatType> it = typeList.iterator(); it.hasNext();) {
           VariableFormatType elem = it.next();
           if (elem.getName().equals(name)) {
               type=elem;
               break;
           }
       }
       return type;
    }   
    
  public List<VariableIntervalType> findAllVariableIntervalType() {
        String query="SELECT t from VariableIntervalType t ";
        return em.createQuery(query).getResultList();
          
    }
    
    /**
     * Find type from prefetched list
     * TODO: find a place to put the global list
     */
    public VariableIntervalType findVariableIntervalTypeByName(List<VariableIntervalType> typeList,String name) {
       VariableIntervalType type = null;
       for (Iterator<VariableIntervalType> it = typeList.iterator(); it.hasNext();) {
           VariableIntervalType elem = it.next();
           if (elem.getName().equals(name)) {
               type=elem;
               break;
           }
       }
       return type;
    }      
    
    
    public VariableIntervalType findVariableIntervalTypeByName(String name) {
        String query="SELECT t from VariableIntervalType t where t.name = '"+name+"'";
        VariableIntervalType type = null;
        try {
            type=(VariableIntervalType)em.createQuery(query).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return type;
    }
    
    public VariableRangeType findVariableRangeTypeByName(String name) {
        String query="SELECT t from VariableRangeType t where t.name = '"+name+"'";
        VariableRangeType type = null;
        try {
            type=(VariableRangeType)em.createQuery(query).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return type;
    }
    
        public void determineStudiesFromVariables(List variables, List studies, Map variableMap) {
        Iterator iter = variables.iterator();
        while (iter.hasNext()) {
            Long dvId = (Long) iter.next();
            DataVariable dv = null;
            
            try {                    
                dv = getDataVariable(dvId);
            } catch (IllegalArgumentException ex) {
                System.out.println("Data variable (ID=" + dvId + ") was found in index, but is not in DB.");
            }
            
            if (dv != null) {
                Long studyId = dv.getDataTable().getStudyFile().getStudy().getId();
                if ( studies.contains(studyId) ) {
                    List dvList = (List) variableMap.get(studyId);
                    dvList.add(dv);
                    variableMap.put(studyId, dvList);

                } else {
                    studies.add( studyId );
                    List dvList = new ArrayList();
                    dvList.add(dv);
                    variableMap.put(studyId, dvList);
                }
            }
            
        }
    }

}
