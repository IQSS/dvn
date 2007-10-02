/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package edu.harvard.hmdc.vdcnet.ddi;

import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CodeBook;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyExporter;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import java.io.IOException;
import java.io.Writer;
import javax.ejb.Local;
import javax.xml.bind.JAXBException;


/**
 * This is the business interface for DDI20Service enterprise bean.
 */
@Local
public interface DDI20ServiceLocal extends StudyExporter  {
    edu.harvard.hmdc.vdcnet.study.Study mapDDI(CodeBook _cb);

    edu.harvard.hmdc.vdcnet.study.Study mapDDI(CodeBook _cb, Study study);
  
    void exportStudy(Study study, Writer out, boolean exportToLegacyVDC) throws IOException, JAXBException;
    
    java.lang.String determineId(CodeBook _cb, String agency);  
    
    void exportDataFile(StudyFile sf, Writer out) throws IOException, JAXBException;
    
}
