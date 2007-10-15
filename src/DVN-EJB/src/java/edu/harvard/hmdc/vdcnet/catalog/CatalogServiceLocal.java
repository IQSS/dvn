
package edu.harvard.hmdc.vdcnet.catalog;

import edu.harvard.hmdc.vdcnet.study.Study;
import javax.ejb.Local;


/**
 * This is the business interface for CatalogService enterprise bean.
 */
@Local
public interface CatalogServiceLocal {
    public String [] listRecords(String from, String until, String set, String metadataPrefix);

    public String getRecord(Study study, String metadataPrefix);

    public Study[] listStudies(String from, String until, String set, String metadataPrefix);
    
}
