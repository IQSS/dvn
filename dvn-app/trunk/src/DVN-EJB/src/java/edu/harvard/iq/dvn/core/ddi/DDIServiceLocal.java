/*
 * DDIServiceLocal.java
 *
 * Created on Jan 11, 2008, 3:08:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.ddi;

import edu.harvard.iq.dvn.core.study.StudyExporter;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import java.io.File;
import java.io.OutputStream;
import javax.ejb.Local;

/**
 *
 * @author Gustavo
 */
@Local
public interface DDIServiceLocal extends StudyExporter, java.io.Serializable {

    void mapDDI(String xmlToParse, StudyVersion studyVersion);
    void mapDDI(File ddiFile, StudyVersion studyVersion);

    void exportDataFile(TabularDataFile tdf, OutputStream out);

}
