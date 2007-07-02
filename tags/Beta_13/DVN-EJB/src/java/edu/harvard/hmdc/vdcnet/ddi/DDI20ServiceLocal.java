
package edu.harvard.hmdc.vdcnet.ddi;

import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CodeBook;
import edu.harvard.hmdc.vdcnet.study.Study;
import java.io.IOException;
import java.io.Writer;
import javax.ejb.Local;
import javax.xml.bind.JAXBException;


/**
 * This is the business interface for DDI20Service enterprise bean.
 */
@Local


public interface DDI20ServiceLocal {
    edu.harvard.hmdc.vdcnet.study.Study mapDDI(CodeBook _cb);

    edu.harvard.hmdc.vdcnet.study.Study mapDDI(CodeBook _cb, Study study, boolean allowUpdates);
    
    void exportStudy(Study study, Writer out) throws IOException, JAXBException;
    
    void exportStudy(Study study, Writer out, boolean exportToLegacyVDC) throws IOException, JAXBException;
    
}
