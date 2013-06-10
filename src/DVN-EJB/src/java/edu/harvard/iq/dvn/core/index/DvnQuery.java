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

import edu.harvard.iq.dvn.core.index.SearchTerm;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class DvnQuery {

    private static final Logger logger = Logger.getLogger(DvnQuery.class.getCanonicalName());
    @EJB
    IndexServiceLocal indexService;
//    SearchTerm searchTerm;
    List<SearchTerm> searchTerms;
    VDC vdc;
    Query query = null;
    List<CategoryPath> facetsToQuery = new ArrayList<CategoryPath>();
    private boolean clearPreviousFacetRequests = false;
//    private static boolean clearPreviousFacetRequests = false;
//    List<Long> limitToStudyIds;
    List<Query> collectionQueries = new ArrayList<Query>();
    private Query dvOwnerIdQuery;
    Query subNetworkQuery = null; 
    Query singleCollectionQuery;
    List<Query> multipleCollectionQueries = new ArrayList<Query>();

    public Query getDvOwnerIdQuery() {
        return dvOwnerIdQuery;
    }

    public void setDvOwnerIdQuery(Query dvOwnerIdQuery) {
        this.dvOwnerIdQuery = dvOwnerIdQuery;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public VDC getVdc() {
        return vdc;
    }

    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }

    public List<SearchTerm> getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(List<SearchTerm> searchTerms) {
        this.searchTerms = searchTerms;
    }

    public List<CategoryPath> getFacetsToQuery() {
        return facetsToQuery;
    }

    public void setFacetsToQuery(List<CategoryPath> facetsToQuery) {
        this.facetsToQuery = facetsToQuery;
    }

    public boolean isClearPreviousFacetRequests() {
        return clearPreviousFacetRequests;
    }

    public void setClearPreviousFacetRequests(boolean clearPreviousFacetRequests) {
        this.clearPreviousFacetRequests = clearPreviousFacetRequests;
    }

//    public List<Long> getLimitToStudyIds() {
//        return limitToStudyIds;
//    }

//    public void setLimitToStudyIds(List<Long> limitToStudyIds) {
//        this.limitToStudyIds = limitToStudyIds;
//    }

    public List<Query> getCollectionQueries() {
        return collectionQueries;
    }

    public void setCollectionQueries(List<Query> collectionQueries) {
        this.collectionQueries = collectionQueries;
    }

    
    /*
     * Commented-out parts used in Phil's (also commented-out) brute force
     * implementation of a subnetwork search; see my comment in the 
     * constructQuery() method. -- L.A. 
     * 
    List<Query> subNetworkDvMemberQueries = new ArrayList<Query>();
    List<Query> subNetworkCollectionQueries = new ArrayList<Query>();
     
    public List<Query> getSubNetworkCollectionQueries() {
        return subNetworkCollectionQueries;
    }

    public void setSubNetworkCollectionQueries(List<Query> subNetworkCollectionQueries) {
        this.subNetworkCollectionQueries = subNetworkCollectionQueries;
    }

    public List<Query> getSubNetworkDvMemberQueries() {
        return subNetworkDvMemberQueries;
    }

    public void setSubNetworkDvMemberQueries(List<Query> subNetworkDvMemberQueries) {
        this.subNetworkDvMemberQueries = subNetworkDvMemberQueries;
    }*/

    public Query getSubNetworkQuery() {
        return subNetworkQuery;
    }

    public void setSubNetworkQuery(Query subNetworkQuery) {
        this.subNetworkQuery = subNetworkQuery;
    }
    
    public Query getSingleCollectionQuery() {
        return singleCollectionQuery;
    }

    public void setSingleCollectionQuery(Query singleCollectionQuery) {
        this.singleCollectionQuery = singleCollectionQuery;
    }

    public List<Query> getMultipleCollectionQueries() {
        return multipleCollectionQueries;
    }

    public void setMultipleCollectionQueries(List<Query> multipleCollectionQueries) {
        this.multipleCollectionQueries = multipleCollectionQueries;
    }

    public void constructQuery() {
        logger.fine("in constructQuery...");
        BooleanQuery searchQuery = null;

        // FIXME: how much of this logic do we need? from indexService.searchwithFacets()...

        List<BooleanQuery> searchParts = new ArrayList();

        // "study-level search" is our "normal", default search, that is 
        // performed on the study metadata keywords.
        boolean studyLevelSearch = false;
        boolean containsStudyLevelAndTerms = false;

        // We also support searches on variables and file-level metadata:
        // We do have to handle these 2 separately, because of the 2 different
        // levels of granularity: one searches on variables, the other on files.
        boolean variableSearch = false;
        boolean fileMetadataSearch = false;

        // And the boolean below indicates any file-level searche - i.e., 
        // either a variable, or file metadata search.  
        // -- L.A. 
        boolean fileLevelSearch = false;


        List<SearchTerm> studyLevelSearchTerms = new ArrayList();
        List<SearchTerm> variableSearchTerms = new ArrayList();
        List<SearchTerm> fileMetadataSearchTerms = new ArrayList();
        Indexer indexer = Indexer.getInstance();

        for (Iterator it = searchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            logger.fine("elem field name = " + elem.getFieldName().toString());
            if (elem.getFieldName().equals("variable")) {
//                SearchTerm st = dvnTokenizeSearchTerm(elem);
//                variableSearchTerms.add(st);
                variableSearchTerms.add(elem);
                variableSearch = true;
            } else if (indexer.isFileMetadataField(elem.getFieldName())) {

                fileMetadataSearch = true;
                fileMetadataSearchTerms.add(elem);
            } else {
//                SearchTerm nvst = dvnTokenizeSearchTerm(elem);
//                nonVariableSearchTerms.add(nvst);
                if (elem.getOperator().equals("=")) {
                    containsStudyLevelAndTerms = true;
                }
                studyLevelSearchTerms.add(elem);
                studyLevelSearch = true;

            }

        }

        BooleanQuery searchTermsQuery = indexer.andSearchTermClause(studyLevelSearchTerms);
        searchParts.add(searchTermsQuery);
        searchQuery = indexer.andQueryClause(searchParts);


        if (!collectionQueries.isEmpty() || dvOwnerIdQuery != null) {
            BooleanQuery queryAcrossAllCollections = new BooleanQuery();

            BooleanQuery allCollections = new BooleanQuery();
            BooleanQuery submittedAndInCollection = new BooleanQuery();
            for (Query collectionQuery : collectionQueries) {
                allCollections.add(collectionQuery, BooleanClause.Occur.SHOULD);
            }
            submittedAndInCollection.add(searchQuery, BooleanClause.Occur.MUST);
            submittedAndInCollection.add(allCollections, BooleanClause.Occur.MUST);
            queryAcrossAllCollections.add(submittedAndInCollection, BooleanClause.Occur.SHOULD);

            BooleanQuery dvSpecific = new BooleanQuery();
            dvSpecific.add(searchQuery, BooleanClause.Occur.MUST);
            dvSpecific.add(dvOwnerIdQuery, BooleanClause.Occur.MUST);
            queryAcrossAllCollections.add(dvSpecific, BooleanClause.Occur.SHOULD);
            searchQuery = queryAcrossAllCollections;
        } else if (singleCollectionQuery != null) {
            logger.fine("single collection will be queried");
            BooleanQuery submittedAndInCollection = new BooleanQuery();
            submittedAndInCollection.add(searchQuery, BooleanClause.Occur.MUST);
            submittedAndInCollection.add(singleCollectionQuery, BooleanClause.Occur.MUST);
            searchQuery = submittedAndInCollection;
        } else if (!multipleCollectionQueries.isEmpty()) {
            logger.fine("adding multipleCollection queries...");
            BooleanQuery queryMultipleCollections = new BooleanQuery();
            for (Query collectionQuery : multipleCollectionQueries) {
                BooleanQuery submittedAndInCollection = new BooleanQuery();
                submittedAndInCollection.add(searchQuery, BooleanClause.Occur.MUST);
                submittedAndInCollection.add(collectionQuery, BooleanClause.Occur.MUST);
                queryMultipleCollections.add(submittedAndInCollection, BooleanClause.Occur.SHOULD);
            }            
            searchQuery = queryMultipleCollections;
        } else if (subNetworkQuery != null) {
            /**
             * When a user is in the context of a subnetwork any search that is
             * performed will return studies that are owned by dataverses in
             * that subnetwork along with any studies from outside dataverses
             * that are included in collections."
             */

            BooleanQuery combinedSubNetworkQuery = new BooleanQuery();
            combinedSubNetworkQuery.add(searchQuery, BooleanClause.Occur.MUST);
            combinedSubNetworkQuery.add(subNetworkQuery, BooleanClause.Occur.MUST);

            searchQuery = combinedSubNetworkQuery;
        }/* This commented-out code is Phil's implementation of subnetwork searching,
          * that does it by logically AND-ing and running all the queries that 
          * define the dataverses in the subnetwork, and all the collections inside. 
          * The performance should be atrocious, on a subnetwork the size of IQSS; 
          * but it's good to have this implementation (it's supposed to be working)
          * for reference and comparison. --L.A. 
          * 
          else if (!subNetworkDvMemberQueries.isEmpty() || !subNetworkCollectionQueries.isEmpty()) {
            logger.fine("When a user is in the context of a subnetwork any search that is performed will return studies that are owned by dataverses in that subnetwork along with any studies from outside dataverses that are included in collections.");

            BooleanQuery queryAcrossSubNetworkMembers = new BooleanQuery();
            for (Query collectionQuery : subNetworkDvMemberQueries) {
                BooleanQuery submittedAndInCollection = new BooleanQuery();
                submittedAndInCollection.add(searchQuery, BooleanClause.Occur.MUST);
                submittedAndInCollection.add(collectionQuery, BooleanClause.Occur.MUST);
                queryAcrossSubNetworkMembers.add(submittedAndInCollection, BooleanClause.Occur.SHOULD);
            }

            BooleanQuery queryAcrossAllSubNetworkCollections = new BooleanQuery();
            for (Query collectionQuery : subNetworkCollectionQueries) {
                BooleanQuery submittedAndInCollection = new BooleanQuery();
                submittedAndInCollection.add(searchQuery, BooleanClause.Occur.MUST);
                submittedAndInCollection.add(collectionQuery, BooleanClause.Occur.MUST);
                queryAcrossAllSubNetworkCollections.add(submittedAndInCollection, BooleanClause.Occur.SHOULD);
            }

            BooleanQuery queryForEntireSubnetwork = new BooleanQuery();
            queryForEntireSubnetwork.add(queryAcrossSubNetworkMembers, BooleanClause.Occur.SHOULD);
            queryForEntireSubnetwork.add(queryAcrossAllSubNetworkCollections, BooleanClause.Occur.SHOULD);

            searchQuery = queryForEntireSubnetwork;
        }*/ else {
            logger.fine("DVN-wide search will be made");
        }
        query = searchQuery;
    }
}
