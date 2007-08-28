
package edu.harvard.hmdc.vdcnet.index;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import java.util.List;
import javax.ejb.Local;


/**
 * This is the business interface for IndexServer enterprise bean.
 */
@Local
public interface IndexServiceLocal {
    public void indexStudy(long studyId);

    public List search(String query);

    public void indexAll();

    public void indexList(List <Long> studyIds);

    public void updateIndexList(List <Long> studyIds);

    public List search(VDC vdc, List<VDCCollection> searchCollections, List<SearchTerm> searchTerm);
    
    public List search(VDC vdc, List<SearchTerm> searchTerms);

    public void updateStudy(long studyId);

    public List search(List<Long> studyIds, List<SearchTerm> searchTerms);

    public void deleteStudy(long studyId);

    public List search(SearchTerm searchTerm);
    
    public List query(String adhocQuery);

    public List searchVariables(SearchTerm searchTerm);

    public List searchVariables(VDC vdc, SearchTerm searchTerm);

    public List searchVariables(List studyIds, SearchTerm searchTerm);

    public List searchVariables(VDC vdc, List<VDCCollection> searchCollections, SearchTerm searchTerm);
    
}
