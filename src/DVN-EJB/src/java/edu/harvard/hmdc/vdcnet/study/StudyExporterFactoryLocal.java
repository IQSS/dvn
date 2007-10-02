/*
 * StudyExporterFactoryLocal.java
 *
 * Created on Oct 2, 2007, 4:02:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ellen Kraffmiller
 */
@Local
public interface StudyExporterFactoryLocal {
    public List<String> getExportFormats();
    
    public StudyExporter getStudyExporter(String exportFormat);
}
