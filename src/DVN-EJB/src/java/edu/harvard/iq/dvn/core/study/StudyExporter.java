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
package edu.harvard.iq.dvn.core.study;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Ellen Kraffmiller
 */
public interface StudyExporter  extends java.io.Serializable{
  
    
  // Will the study be exported to XML output? 
  // (Currently used to determine the filename extension when creating an OutputStream)   
  boolean isXmlFormat(); 
  
  void exportStudy(Study study, OutputStream out) throws IOException;
  void exportStudy(Study study, OutputStream out, String excludeXpath, String includeXpath) throws IOException; 
}
