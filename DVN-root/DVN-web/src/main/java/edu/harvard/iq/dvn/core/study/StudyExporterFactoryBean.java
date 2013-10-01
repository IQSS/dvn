/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
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
