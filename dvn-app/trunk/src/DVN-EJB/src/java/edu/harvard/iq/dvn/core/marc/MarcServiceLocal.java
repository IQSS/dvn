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
package edu.harvard.iq.dvn.core.marc;

import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CodeBook;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyExporter;
import edu.harvard.iq.dvn.core.study.StudyFile;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.ejb.Local;
import javax.xml.bind.JAXBException;


/**
 * This is the business interface for DDI20Service enterprise bean.
 */
@Local
public interface MarcServiceLocal extends StudyExporter, java.io.Serializable  {
  
  
    void exportStudy(Study study, OutputStream out) throws IOException;
    
 
    
}
