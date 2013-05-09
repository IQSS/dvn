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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.index;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.facet.search.results.FacetResult;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.search.Query;

public class ResultsWithFacets {

    private ArrayList matchIds;
    List<FacetResult> facetResultList;
    private List<CategoryPath> facetsQueried = new ArrayList<CategoryPath>();
    /** @todo: should this be static? */
    private static boolean clearPreviousFacetRequests = false;
//    private Query query;
    private Query baseQuery;

    public Query getBaseQuery() {
        return baseQuery;
    }

    public void setBaseQuery(Query baseQuery) {
        this.baseQuery = baseQuery;
    }

//    public Query getQuery() {
//        return query;
//    }
//
//    public void setQuery(Query query) {
//        this.query = query;
//    }
//
    public void setResultList(List<FacetResult> resultList) {
        this.facetResultList = resultList;
    }

    public ArrayList getMatchIds() {
        return matchIds;
    }

    public List<FacetResult> getResultList() {
        return facetResultList;
    }

    protected void setMatchIds(List<Long> matchIds) {
        this.matchIds = (ArrayList) matchIds;
    }
    
    public List<CategoryPath> getFacetsQueried() {
        return facetsQueried;
    }

    public void setFacetsQueried(List<CategoryPath> facetsQueried) {
        this.facetsQueried = facetsQueried;
    }

    public boolean isClearPreviousFacetRequests() {
        return clearPreviousFacetRequests;
    }

    public void setClearPreviousFacetRequests(boolean clearPreviousFacetRequests) {
        this.clearPreviousFacetRequests = clearPreviousFacetRequests;
    }

}
