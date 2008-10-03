/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class CaptchaServiceBean implements CaptchaServiceLocal {

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    private EntityManager em;

    public Captcha findCaptcha() {
        return (Captcha) em.createQuery("SELECT cp from Captcha cp").getSingleResult();

    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method" or "Web Service > Add Operation")
}
