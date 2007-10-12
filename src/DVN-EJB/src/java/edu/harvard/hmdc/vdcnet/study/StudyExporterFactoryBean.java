/*
 * StudyExporterFactoryBean.java
 *
 * Created on Oct 2, 2007, 4:02:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal;
import edu.harvard.hmdc.vdcnet.dublinCore.DCServiceLocal;
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
public class StudyExporterFactoryBean implements StudyExporterFactoryLocal {
    @EJB DDI20ServiceLocal ddiService;
    @EJB DCServiceLocal dcService;
    
    private List<String> exportFormats;
    
    public List<String> getExportFormats() {
        return exportFormats;
    }
    
    public void ejbCreate() {
        exportFormats = new ArrayList<String>();
        exportFormats.add("ddi");
        exportFormats.add("oai_dc");
        
    }
    
    public StudyExporter getStudyExporter(String exportFormat) {
        if (exportFormat.equals("ddi")) { 
            return ddiService;
        } else if (exportFormat.equals("oai_dc")) {
            return dcService;
        }
        else throw new EJBException("Unknown export format: "+exportFormat);
    }
    
  
 
}
