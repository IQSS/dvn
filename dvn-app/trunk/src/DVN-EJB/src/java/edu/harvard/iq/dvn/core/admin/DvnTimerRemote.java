/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

import java.io.Serializable;
import java.util.Date;
import javax.ejb.Timer;
import javax.ejb.Remote;

/**
 *
 * @author roberttreacy
 */
@Remote
public interface DvnTimerRemote extends java.io.Serializable {

    void createTimer(Date initialExpiration, long intervalDuration, Serializable info);

    @javax.ejb.Timeout
    @javax.ejb.TransactionAttribute(value = javax.ejb.TransactionAttributeType.NOT_SUPPORTED)
    public void handleTimeout(javax.ejb.Timer timer);
    
}
