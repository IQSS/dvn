/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb.impl;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author asone
 */
@Local
public interface DvnDSBTimerServiceLocal extends Serializable {

    public void createTimer(List<File> lst, long minute);
}
