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
package edu.harvard.iq.dvn.core.index;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import java.util.List;
import javax.ejb.Local;


/**
 * This is the business interface for IndexServer enterprise bean.
 */
@Local
public interface IndexServiceLocal extends java.io.Serializable {
    public void indexStudy(long studyId);

    public List search(String query);

    public void indexAll();

    public void indexList(List <Long> studyIds);
    
    public void indexBatch();

    public void updateIndexList(List <Long> studyIds);

    public List search(VDC vdc, List<VDCCollection> searchCollections, List<SearchTerm> searchTerm);
    
    public List search(VDC vdc, List<SearchTerm> searchTerms);

    public void updateStudy(long studyId);

    public void deleteIndexList(List<Long> studyIds);

    public List search(List<Long> studyIds, List<SearchTerm> searchTerms);

    public void deleteStudy(long studyId);

    public List search(SearchTerm searchTerm);
    
    public List query(String adhocQuery);

    public List searchVariables(SearchTerm searchTerm);

    public List searchVariables(VDC vdc, SearchTerm searchTerm);

    public List searchVariables(List studyIds, SearchTerm searchTerm);

    public List searchVariables(VDC vdc, List<VDCCollection> searchCollections, SearchTerm searchTerm);

    public List <Long> searchVersionUnf(VDC vdc,String unf);
    
    public void createIndexTimer();
    
    public void createIndexNotificationTimer();
    
}
