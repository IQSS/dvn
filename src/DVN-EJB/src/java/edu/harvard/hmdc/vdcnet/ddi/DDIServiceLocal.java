/*
 * DDIServiceLocal.java
 *
 * Created on Jan 11, 2008, 3:08:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.ddi;

import edu.harvard.hmdc.vdcnet.study.StudyExporter;
import javax.ejb.Local;

/**
 *
 * @author Gustavo
 */
@Local
public interface DDIServiceLocal extends StudyExporter {
    
}
