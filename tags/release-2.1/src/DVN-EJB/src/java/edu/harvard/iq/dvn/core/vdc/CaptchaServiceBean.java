/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class CaptchaServiceBean implements CaptchaServiceLocal {

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    private EntityManager em;
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.vdc.CaptchaServiceBean");

    public Captcha findCaptcha() {
        Captcha c = null;
        try {
            c = (Captcha) em.createQuery("SELECT cp from Captcha cp").getSingleResult();
        } catch (NoResultException n){
            logger.info("Captcha is not initialized");
        }
        return c;
    }
}
