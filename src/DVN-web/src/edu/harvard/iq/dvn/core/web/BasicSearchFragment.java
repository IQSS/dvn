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
package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.admin.KeywordSearchServiceBean;
import edu.harvard.iq.dvn.core.index.DvnQuery;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.index.ResultsWithFacets;
import edu.harvard.iq.dvn.core.index.SearchTerm;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.*;
import java.util.logging.Logger;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import org.apache.lucene.facet.search.results.FacetResult;
import org.apache.lucene.facet.search.results.FacetResultNode;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.search.Query;


@Named ("BasicSearchFragment")
@ViewScoped
public class BasicSearchFragment extends VDCBaseBean implements java.io.Serializable {
    private static final Logger logger = Logger.getLogger(BasicSearchFragment.class.getCanonicalName());
    @EJB
    IndexServiceLocal      indexService;
    @EJB
    VariableServiceLocal varService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @EJB
    VDCCollectionServiceLocal vdcCollectionService;
    @EJB
    KeywordSearchServiceBean keywordSearchServiceBean;
    private String searchValue;
    private String searchField;
    private List <String> keywordSearchTerms;
    
    public void init () {
        super.init();
        if ( getVDCRequestBean().getCurrentVDC() == null ) {
            keywordSearchTerms = keywordSearchServiceBean.findAll();
            VDCNetwork checkForSubnetwork = getVDCRequestBean().getCurrentVdcNetwork();
            if ( checkForSubnetwork.equals(vdcNetworkService.findRootNetwork()) ) {
                searchValue = ResourceBundle.getBundle("BundlePageInfo").getString("searchBoxTextNetwork");
            }
            else {
                searchValue = ResourceBundle.getBundle("BundlePageInfo").getString("searchBoxTextSubnetwork");
            }
        } else {
            searchValue = ResourceBundle.getBundle("BundlePageInfo").getString("searchBoxTextDataverse");
        }
    }

    public String search_action() {
        searchField = (searchField == null) ? "any" : searchField; // default searchField, in case no dropdown

        List searchTerms    = new ArrayList();
        SearchTerm st       = new SearchTerm();
        st.setFieldName( searchField );
        st.setValue( searchValue );
        StudyListing sl = getSearchResult(st);
        String studyListingIndex = StudyListing.addToStudyListingMap(sl, getSessionMap());
        return "/StudyListingPage.xhtml?faces-redirect=true&studyListingIndex=" + studyListingIndex + getContextSuffix();
    }

    public String facet_search() {
        searchField = (searchField == null) ? "any" : searchField; // default searchField, in case no dropdown

        List searchTerms    = new ArrayList();
        SearchTerm st       = new SearchTerm();
        st.setFieldName( searchField );
        st.setValue( searchValue );
        StudyListing sl = getSearchResultsWithFacets(st);
        String studyListingIndex = StudyListing.addToStudyListingMap(sl, getSessionMap());
        return "/StudyListingPage.xhtml?faces-redirect=true&studyListingIndex=" + studyListingIndex + getContextSuffix();
    }

    public String keywordSearch_action(String searchIn){
        System.out.print("searchIn: "+ searchIn);
        searchField = (searchField == null) ? "any" : searchField; // default searchField, in case no dropdown       
        SearchTerm st  = new SearchTerm();
        st.setFieldName( searchField );
        st.setValue( searchIn );
        StudyListing sl = getSearchResult(st);
        String studyListingIndex = StudyListing.addToStudyListingMap(sl, getSessionMap());
        return "/StudyListingPage.xhtml?faces-redirect=true&studyListingIndex=" + studyListingIndex + getContextSuffix();
    }

    public String keywordSearchWithFacets_action(String searchIn) {
        System.out.print("searchIn: " + searchIn);
        searchField = (searchField == null) ? "any" : searchField; // default searchField, in case no dropdown       
        SearchTerm st = new SearchTerm();
        st.setFieldName(searchField);
        st.setValue(searchIn);
        StudyListing sl = getSearchResultsWithFacets(st);
        String studyListingIndex = StudyListing.addToStudyListingMap(sl, getSessionMap());
        return "/StudyListingPage.xhtml?faces-redirect=true&studyListingIndex=" + studyListingIndex + getContextSuffix();
    }


    private StudyListing getSearchResult(SearchTerm st){
        
        List searchTerms    = new ArrayList();
        searchTerms.add(st);
        List studies        = new ArrayList();
        Map variableMap     = new HashMap();
        Map versionMap = new HashMap();
        List displayVersionList = new ArrayList();

        if ( searchField.equals("variable") ) {
            List variables  = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            varService.determineStudiesFromVariables(variables, studies, variableMap);

        } else {
            studies = indexService.search(getVDCRequestBean().getCurrentVDC(), searchTerms);
        }
        if (searchField.equals("any")) {
            List<Long> versionIds = indexService.searchVersionUnf(getVDCRequestBean().getCurrentVDC(), st.getValue());
            Iterator iter = versionIds.iterator();
            Long studyId = null;
            while (iter.hasNext()) {
                Long vId = (Long) iter.next();
                StudyVersion sv = null;
                try {
                    sv = studyService.getStudyVersionById(vId);
                    studyId = sv.getStudy().getId();
                    List<StudyVersion> svList = (List<StudyVersion>) versionMap.get(studyId);
                    if (svList == null) {
                        svList = new ArrayList<StudyVersion>();
                    }
                    svList.add(sv);
                    if (!studies.contains(studyId)) {
                        displayVersionList.add(studyId);
                        studies.add(studyId);
                    }
                    versionMap.put(studyId, svList);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }


        StudyListing sl = new StudyListing(StudyListing.SEARCH);
        sl.setVdcId(getVDCRequestBean().getCurrentVDCId());
        sl.setStudyIds(studies);
        sl.setSearchTerms(searchTerms);
        sl.setVariableMap(variableMap);
        sl.setVersionMap(versionMap);
        sl.setDisplayStudyVersionsList(displayVersionList);

        
        return sl;
        
    }

    public StudyListing getSearchResultsWithFacets(SearchTerm st) {

        List searchTerms = new ArrayList();
        searchTerms.add(st);
        List studies = new ArrayList();
        Map variableMap = new HashMap();
        Map versionMap = new HashMap();
        List displayVersionList = new ArrayList();
        ResultsWithFacets resultsWithFacets = null;

        // when is this true?
        if (searchField.equals("variable")) {
            List variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            // how will facetResults get set?
            varService.determineStudiesFromVariables(variables, studies, variableMap);

        } else {
//            resultsWithFacets = indexService.searchwithFacets(getVDCRequestBean().getCurrentVDC(), searchTerms);
            logger.fine("non-variable query...");

            Long rootSubnetworkId = getVDCRequestBean().getVdcNetwork().getId();
            if (getVDCRequestBean().getCurrentVdcNetwork().getId().equals(rootSubnetworkId)) {
                logger.fine("Running DVN-wide search");
                DvnQuery dvnQuery = new DvnQuery();
                dvnQuery.setSearchTerms(searchTerms);
                dvnQuery.constructQuery();
                dvnQuery.setClearPreviousFacetRequests(true);
                resultsWithFacets = indexService.searchNew(dvnQuery);
                studies = resultsWithFacets.getMatchIds();
            } else {
                logger.fine("Searching only a subnetwork");
                VDCNetwork vdcNetwork = getVDCRequestBean().getCurrentVdcNetwork();
                String vdcNetworkName = vdcNetwork.getName();
                logger.fine("vdcNetwork name: " + vdcNetworkName);
                /*
                 * Brute force way of resolving collection queries to match 
                 * all the studies in a subnetwork: 
                Collection<VDC> vdcs = vdcNetwork.getNetworkVDCs();
                List<Query> subNetworkCollectionQueries = new ArrayList<Query>();
                List<Query> subNetworkDvMemberQueries = new ArrayList<Query>();
                for (VDC vdc : vdcs) {
                    String name = vdc.getName();
                    logger.fine("adding queries for: " + name);
                    Query dvnSpecificQuery = indexService.constructDvOwnerIdQuery(vdc);
                    logger.fine("adding dvnSpecific query:" + dvnSpecificQuery);
                    subNetworkDvMemberQueries.add(dvnSpecificQuery);
                    List<Query> vdcCollectionQueries = indexService.getCollectionQueries(vdc);
                    for (Query collectionQuery : vdcCollectionQueries) {
                        logger.fine("adding collection query: " + collectionQuery);
                    }
                    subNetworkCollectionQueries.addAll(vdcCollectionQueries);
                }
                */

                DvnQuery dvnQuery = new DvnQuery();
                /*
                dvnQuery.setSubNetworkDvMemberQueries(subNetworkDvMemberQueries);
                dvnQuery.setSubNetworkCollectionQueries(subNetworkCollectionQueries);
                * */
                Query subNetworkQuery = indexService.constructNetworkIdQuery(getVDCRequestBean().getCurrentVdcNetwork().getId());
                dvnQuery.setSubNetworkQuery(subNetworkQuery);
                dvnQuery.setSearchTerms(searchTerms);
                dvnQuery.constructQuery();
                dvnQuery.setClearPreviousFacetRequests(true);
                resultsWithFacets = indexService.searchNew(dvnQuery);
                studies = resultsWithFacets.getMatchIds();
            }
        }

        if (searchField.equals("any")) {
            List<Long> versionIds = indexService.searchVersionUnf(getVDCRequestBean().getCurrentVDC(), searchValue);
            Iterator iter = versionIds.iterator();
            Long studyId = null;
            while (iter.hasNext()) {
                Long vId = (Long) iter.next();
                StudyVersion sv = null;
                try {
                    sv = studyService.getStudyVersionById(vId);
                    studyId = sv.getStudy().getId();
                    List<StudyVersion> svList = (List<StudyVersion>) versionMap.get(studyId);
                    if (svList == null) {
                        svList = new ArrayList<StudyVersion>();
                    }
                    svList.add(sv);
                    if (!studies.contains(studyId)) {
                        displayVersionList.add(studyId);
                        studies.add(studyId);
                    }
                    versionMap.put(studyId, svList);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }


        StudyListing sl = new StudyListing(StudyListing.SEARCH);
        sl.setVdcId(getVDCRequestBean().getCurrentVDCId());
        sl.setStudyIds(studies);
        sl.setSearchTerms(searchTerms);
        logger.fine("in BasicSearchFrag. queriedFacets is: " + resultsWithFacets.getFacetsQueried());
        sl.setResultsWithFacets(resultsWithFacets);
        sl.setVariableMap(variableMap);
        sl.setVersionMap(versionMap);
        sl.setDisplayStudyVersionsList(displayVersionList);

        return sl;
    }
 

 public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getSearchField() {
        return searchField;
    }

    public String getSearchValue() {
        return searchValue;
    }
    
    public List<String> getSearchTerms() {
        return keywordSearchTerms;
    }

    public void setSearchTerms(List<String> searchTerms) {
        this.keywordSearchTerms = searchTerms;
    }
}
