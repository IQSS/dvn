/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author skraffmiller
 */
@Stateless
public class KeywordSearchServiceBean  {
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    
   public List<String> findAll() {
        List <String> keywords = (List <String>) em.createQuery("SELECT kst.searchTerm from KeywordSearchTerm kst").getResultList();
        return keywords;
    }
}
