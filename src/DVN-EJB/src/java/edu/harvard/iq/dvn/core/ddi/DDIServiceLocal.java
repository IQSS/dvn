/*
 * DDIServiceLocal.java
 *
 * Created on Jan 11, 2008, 3:08:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.ddi;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyExporter;
import edu.harvard.iq.dvn.core.study.StudyFile;
import java.io.File;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Gustavo
 */
@Local
public interface DDIServiceLocal extends StudyExporter, java.io.Serializable {

    void mapDDI(String xmlToParse, Study study);
    void mapDDI(File ddiFile, Study study);
    void mapDDI(Reader reader, Study study);

    void exportDataFile(StudyFile sf, OutputStream out);

}
