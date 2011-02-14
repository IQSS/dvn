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

package edu.harvard.iq.dvn.core.dublinCore;

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
public interface DCServiceLocal extends StudyExporter, java.io.Serializable  {
  
 
    void exportStudy(Study study, OutputStream out) throws IOException;
    
 
    
}
