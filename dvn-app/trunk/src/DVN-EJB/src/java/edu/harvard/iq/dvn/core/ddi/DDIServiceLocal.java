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
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Gustavo
 */
@Local
public interface DDIServiceLocal extends StudyExporter, java.io.Serializable {

    Map mapDDI(String xmlToParse, StudyVersion studyVersion);
    Map mapDDI(File ddiFile, StudyVersion studyVersion);
    Map reMapDDI(String xmlToParse, StudyVersion studyVersion, Map filesMap);
    Map reMapDDI(File ddiFile, StudyVersion studyVersion, Map filesMap);

    void exportDataFile(TabularDataFile tdf, OutputStream out);

}
