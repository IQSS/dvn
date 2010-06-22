/*
 * DVNVersionServiceBean.java
 * 
 * Created on Sep 20, 2007, 10:24:41 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class DVNVersionServiceBean implements DVNVersionServiceLocal {
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    // Add business logic below. (Right-click in editor and choose
    // "EJB Methods > Add Business Method" or "Web Service > Add Operation")
    public DVNVersion getLatestVersion(){
        return (DVNVersion) em.createQuery("select d from DVNVersion d").getSingleResult();
    }
 
}
