/*
 * StudyExporterFactoryBean.java
 *
 * Created on Oct 2, 2007, 4:02:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.ddi.DDIServiceLocal;
import edu.harvard.iq.dvn.core.dublinCore.DCServiceLocal;
import edu.harvard.iq.dvn.core.marc.MarcServiceLocal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class StudyExporterFactoryBean implements StudyExporterFactoryLocal, java.io.Serializable {
    @EJB DDIServiceLocal ddiService;
    @EJB DCServiceLocal dcService;
    @EJB MarcServiceLocal marcService;
  
    private List<String> exportFormats;
    
    public List<String> getExportFormats() {
        return exportFormats;
    }
    
    public void ejbCreate() {
        exportFormats = new ArrayList<String>();
        exportFormats.add(EXPORT_FORMAT_DDI);
        exportFormats.add(EXPORT_FORMAT_DC);
        exportFormats.add(EXPORT_FORMAT_MARC);
        
    }
    
    public StudyExporter getStudyExporter(String exportFormat) {
        if (exportFormat.equals(EXPORT_FORMAT_DDI)) { 
            return ddiService;
        } else if (exportFormat.equals(EXPORT_FORMAT_DC)) {
            return dcService;
        } else if (exportFormat.equals(EXPORT_FORMAT_MARC)) {
            return marcService;
        }
        else throw new EJBException("Unknown export format: "+exportFormat);
    }
    
  
 
}
