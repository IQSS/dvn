/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.core.vdc;

import javax.ejb.Local;

/**
 *
 * @author roberttreacy
 */
@Local
public interface CaptchaServiceLocal {

    Captcha findCaptcha();
    
}
