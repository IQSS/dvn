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
