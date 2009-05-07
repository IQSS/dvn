/*
 * StudyExporterFactoryLocal.java
 *
 * Created on Oct 2, 2007, 4:02:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ellen Kraffmiller
 */
@Local
public interface StudyExporterFactoryLocal  extends java.io.Serializable {
    public static final String EXPORT_FORMAT_DDI="ddi";
    public static final String EXPORT_FORMAT_DC="oai_dc";
    public static final String EXPORT_FORMAT_MARC="marc";
 
    public List<String> getExportFormats();
    
    public StudyExporter getStudyExporter(String exportFormat);
}
