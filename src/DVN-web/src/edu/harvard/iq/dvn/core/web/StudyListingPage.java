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
 * StudyListingPage.java
 *
 * Created on September 19, 2006, 6:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web;

import com.icesoft.faces.component.datapaginator.DataPaginator;
import com.icesoft.faces.component.tree.IceUserObject;
import com.icesoft.faces.context.effects.JavascriptContext;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.index.DVNAnalyzer;
import edu.harvard.iq.dvn.core.index.DvnQuery;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.index.Indexer;
import edu.harvard.iq.dvn.core.index.ResultsWithFacets;
import edu.harvard.iq.dvn.core.index.SearchTerm;
import edu.harvard.iq.dvn.core.study.*;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.collection.CollectionUI;
import edu.harvard.iq.dvn.core.web.common.LoginBean;
import edu.harvard.iq.dvn.core.web.common.VDCApplicationBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.component.VDCCollectionTree;
import edu.harvard.iq.dvn.core.web.site.VDCUI;
import edu.harvard.iq.dvn.core.web.study.FacetResultUI;
import edu.harvard.iq.dvn.core.web.study.FacetUI;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIData;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.apache.lucene.facet.search.results.FacetResult;
import org.apache.lucene.facet.search.results.FacetResultNode;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

/**
 *
 * @author gdurand
 */
@ViewScoped
@Named("StudyListingPage")
public class StudyListingPage extends VDCBaseBean implements java.io.Serializable {

    @EJB
    VDCServiceLocal vdcService;
    @EJB
    VDCCollectionServiceLocal vdcCollectionService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    IndexServiceLocal indexService;
    @EJB
    VariableServiceLocal varService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;

    private static final Logger logger = Logger.getLogger(StudyListingPage.class.getCanonicalName());
    private List<CategoryPath> facetsOfInterest = new ArrayList<CategoryPath>();

    public List<CategoryPath> getFacetsOfInterest() {
        return facetsOfInterest;
    }

    /** Creates a new instance of StudyListingPageBean */
    public StudyListingPage() {
    }

    // data members
    private StudyListing studyListing;
    private DefaultTreeModel collectionTree = null;
    private UIData studyTable;
    private DataPaginator paginator;
    private DataPaginator paginator2;
    private String searchField;
    private String searchValue = "Search Studies";
    private Map studyFields;
    private String studyListingIndex;
    private Query baseQuery;


    // display items
    boolean renderTree;
    boolean renderSearch;
    boolean renderSort;
    boolean renderFacets = false;
    private boolean renderScroller;
    private boolean renderDescription;
    private boolean renderContributorLink;
    private boolean renderDVPermissionsBox;
    private boolean renderDownloadCount;
    private List sortOrderItems;
    private String sortOrderString;
    private boolean recentVisitToDvPage = false;
    private String collectionIdInURL;

    public String getCollectionIdInURL() {
        return collectionIdInURL;
    }

    public void setCollectionIdInURL(String collectionIdInURL) {
        this.collectionIdInURL = collectionIdInURL;
    }

    public void setRenderFacets(boolean renderFacets) {
        this.renderFacets = renderFacets;
    }

    public boolean isRenderFacets() {
        return renderFacets;
    }


    public boolean isRenderDownloadCount() {
        return renderDownloadCount;
    }

    public void setRenderDownloadCount(boolean renderDownloadCount) {
        this.renderDownloadCount = renderDownloadCount;
    }

    String listHeader;
    String listMessage;
    String listDescription;
    
    Long downloadCount;
    
    public Long getDownloadCount() {
        downloadCount = studyService.getStudyDownloadCount(studyListing.getStudyIds());
        return downloadCount;
    }
      
    
    public StudyListing getStudyListing() {
        return studyListing;
    }

    public void setStudyListing(StudyListing studyListing) {
        this.studyListing = studyListing;
    }

    public Collection getStudies() {
        List studyUIList = new ArrayList();
        VDCUser user = getVDCSessionBean().getUser();
        UserGroup usergroup = getVDCSessionBean().getIpUserGroup();

        if (studyListing != null && studyListing.getStudyIds() != null) {
            Iterator iter = studyListing.getStudyIds().iterator();
            while (iter.hasNext()) {
                Long sid = (Long) iter.next();
                StudyUI sui = new StudyUI(sid, getStudyFields(), user, usergroup);
                if (studyListing.getVariableMap() != null) {
                    List dvList = (List) studyListing.getVariableMap().get(sid);
                    sui.setFoundInVariables(dvList);
                }
                if (studyListing.getFileMap() != null) {
                    List fileList = (List) studyListing.getFileMap().get(sid);
                    sui.setFoundInStudyFiles(fileList);
                }
                if (studyListing.getVersionMap() != null){
                    List versionList = (List) studyListing.getVersionMap().get(sid);
                    sui.setFoundInVersions(versionList);
                }
                if (studyListing.getDisplayStudyVersionsList() != null){
                    sui.setDisplayVersions(studyListing.getDisplayStudyVersionsList().contains(sid));
                }
                studyUIList.add(sui);
            }
        }

        return studyUIList;
    }

    public Collection getFacets(Integer limit) {
        List facetUIList = new ArrayList();
        if (studyListing.getResultsWithFacets().getResultList() != null) {
            List<FacetResult> facetResults = studyListing.getResultsWithFacets().getResultList();
            for (int i = 0; i < facetResults.size(); i++) {
                FacetResult facetResult = facetResults.get(i);
                FacetUI facetUI = new FacetUI();
                String category = facetResult.getFacetResultNode().getLabel().toString();
                facetUI.setName(category);
                Integer count = 0;
                for (FacetResultNode n : facetResult.getFacetResultNode().getSubResults()) {
                    CategoryPath label = n.getLabel();
                    String last = n.getLabel().lastComponent().toString();
                    Double hits = n.getValue();
                    if (last != null && hits.toString() != null && count < limit ) {
                        FacetResultUI facetResultUI = new FacetResultUI();
                        facetResultUI.setName(last);
                        facetResultUI.setHits(hits.intValue());

                        boolean selected = false;
                        List<CategoryPath> categoryPathList = studyListing.getResultsWithFacets().getFacetsQueried();
                        for (CategoryPath categoryPath : categoryPathList) {
                            String queriedLabel = categoryPath.getComponent(0).toString();
                            if (queriedLabel.equals(category)) {
                                String queriedValue = categoryPath.getComponent(1).toString();
                                if (last.equals(queriedValue)) {
                                    selected = true;
                                }
                            }
                        }
                        facetResultUI.setSelected(selected);

                        facetUI.add(facetResultUI);
                        count++;
                    }
                }
                facetUIList.add(facetUI);
            }
        }
        return facetUIList;
    }

    /**
     * Getter for property collectionTree.
     * @return Value of property collectionTree.
     */
    public DefaultTreeModel getCollectionTree() {
        if (this.collectionTree == null) {
            
            // initialize an empty DefaultTreeModel: (?)
            //
            // weird thing is, if you simply return null here, the 
            // StudyListingPage dies with "NullPointerException
            // at com.icesoft.faces.component.tree.Tree.visitRows..."
            // even when the corresponding "<ice:tree" section 
            // has a correct "rendered=..." attribute, telling
            // the page not to display it...
            // so it looks like it tries to parse the tree anyway! 
            //  -- L.A. 
            DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode();
            IceUserObject rootObject = new IceUserObject(rootTreeNode);
            rootObject.setText("Root Node");
            rootObject.setExpanded(true);
            rootTreeNode.setUserObject(rootObject);
      
            return new DefaultTreeModel(rootTreeNode);

        }
        return this.collectionTree;
    }

    public void setCollectionTree(DefaultTreeModel collectionTree) {
        this.collectionTree = collectionTree;
    }

    public UIData getStudyTable() {
        return studyTable;
    }

    public void setStudyTable(UIData studyTable) {
        this.studyTable = studyTable;
    }

    public String getStudyListingIndex() {
        return studyListingIndex;
    }

    public void setStudyListingIndex(String studyListingIndex) {
        this.studyListingIndex = studyListingIndex;
    }

    public String getListHeader() {
        return listHeader;
    }

    public String getListDescription() {
        return listDescription;
    }
    public String getListMessage() {
        return listMessage;
    }

    public boolean isRenderTree() {
        return renderTree;
    }

    public boolean isRenderSearch() {
        return renderSearch;
    }

    public boolean isRenderSort() {
        return renderSort;
    }

    public boolean isRenderScroller() {
        return renderScroller;
    }

    public boolean isRenderContributorLink() {
        return renderContributorLink;
    }

    public boolean isRenderDescription() {
        return renderDescription;
    }
    
    public boolean isRenderDVPermissionsBox() {
        return renderDVPermissionsBox;
    }

    /*
     * This method runs a search initiated *on* the StudyListingPage itself 
     * (query entered in the search box in the top right corner of the page, 
     * "Go" button clicked). 
     * Searching on file-level metadata is not yet implemented here. 
     */
    public String search_action() {
        searchField = (searchField == null) ? "any" : searchField; // default searchField, in case no dropdown

        List searchTerms = new ArrayList();
        SearchTerm st = new SearchTerm();
        st.setFieldName(searchField);
        st.setValue(searchValue);
        searchTerms.add(st);

        List studyIDList = new ArrayList();
        Map variableMap = new HashMap();
        Map fileMap = new HashMap(); 
        Map versionMap = new HashMap();
        List displayVersionList = new ArrayList();

        // currently search filter is determined from a set of boolean checkboxes
        int searchFilter = 0;
        if (renderSearchResultsFilter && searchResultsFilter) {
            searchFilter = 2;
        }
        if (renderSearchCollectionFilter && searchCollectionFilter) {
            searchFilter = 1;
        }

        if (searchField.equals("variable")) {
            List variables = null;
            if (searchFilter == 1) {
                // just this collection
                List collections = new ArrayList();
                collections.add(vdcCollectionService.find(studyListing.getCollectionId()));
                variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), collections, st);
            } else if (searchFilter == 2) {
                // subsearch
                variables = indexService.searchVariables(studyListing.getStudyIds(), st);
            } else {
                variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            }

            varService.determineStudiesFromVariables(variables, studyIDList, variableMap);
        } else {
            if (searchFilter == 1) {
                // just this collection
                List collections = new ArrayList();
                collections.add(vdcCollectionService.find(studyListing.getCollectionId()));
                studyIDList = indexService.search(getVDCRequestBean().getCurrentVDC(), collections, searchTerms);
            } else if (searchFilter == 2) {
                // subsearch
                studyIDList = indexService.search(studyListing.getStudyIds(), searchTerms);
            } else {
                studyIDList = indexService.search(getVDCRequestBean().getCurrentVDC(), searchTerms);
            }
            if (searchField.equals("any")) {
                List<Long> versionIds = indexService.searchVersionUnf(getVDCRequestBean().getCurrentVDC(),searchValue);
                Iterator iter = versionIds.iterator();
                Long studyId = null;
                while (iter.hasNext()) {
//                    List<StudyVersion> svList = new ArrayList<StudyVersion>();
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
                        if (!studyIDList.contains(studyId)) {
                            displayVersionList.add(studyId);
                            studyIDList.add(studyId);
                        }
                        versionMap.put(studyId, svList);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }

            }
        }


        // now we handle the display of the page
        // first get the bound collection tree
        collectionTree = studyListing.getCollectionTree();

        // now create the new StudyListing
        studyListing = new StudyListing(StudyListing.SEARCH);
        studyListing.setVdcId(getVDCRequestBean().getCurrentVDCId());
        studyListing.setStudyIds(studyIDList);
        studyListing.setSearchTerms(searchTerms);
        studyListing.setVariableMap(variableMap);
        studyListing.setVersionMap(versionMap);
        studyListing.setCollectionTree(collectionTree);
        studyListing.setDisplayStudyVersionsList(displayVersionList);

        String studyListingIndex = StudyListing.addToStudyListingMap(studyListing, getSessionMap());
        return "/StudyListingPage.xhtml?faces-redirect=true&studyListingIndex=" + studyListingIndex + getContextSuffix();
    }

    public String search_actionNew() {
        logger.fine("Entered search_actionNew on StudyListingPage.java");

        DvnQuery dvnQuery = new DvnQuery();
        searchField = (searchField == null) ? "any" : searchField; // default searchField, in case no dropdown
        List searchTerms = new ArrayList();
        SearchTerm st = new SearchTerm();
        st.setFieldName(searchField);
        st.setValue(searchValue); // "Search Studies" by default
        searchTerms.add(st);
        dvnQuery.setSearchTerms(searchTerms);

        ResultsWithFacets resultsWithFacets = new ResultsWithFacets();
        List studyIDList = new ArrayList();
        Map variableMap = new HashMap();
        Map fileMap = new HashMap();
        Map versionMap = new HashMap();
        List displayVersionList = new ArrayList();

        // currently search filter is determined from a set of boolean checkboxes
        int searchFilter = 0;
        if (renderSearchResultsFilter && searchResultsFilter) {
            searchFilter = 2;
        }
        if (renderSearchCollectionFilter && searchCollectionFilter) {
            searchFilter = 1;
        }

        if (searchField.equals("variable")) {
            List variables = null;
            if (searchFilter == 1) {
                // just this collection
                List collections = new ArrayList();
                collections.add(vdcCollectionService.find(studyListing.getCollectionId()));
                variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), collections, st);
            } else if (searchFilter == 2) {
                // subsearch
                variables = indexService.searchVariables(studyListing.getStudyIds(), st);
            } else {
                variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            }

            varService.determineStudiesFromVariables(variables, studyIDList, variableMap);
        } else {
            logger.fine("searchFilter = " + searchFilter);
            if (searchFilter == 1) {
                // just this collection
//                List collections = new ArrayList();
//                collections.add(vdcCollectionService.find(studyListing.getCollectionId()));
//                studyIDList = indexService.search(getVDCRequestBean().getCurrentVDC(), collections, searchTerms);
                // old non-faceted method above

                /**
                 * @todo: refactor? code is similar to getCollectionQueries in
                 * Indexer.java?
                 */
                Query finalQuery = null;
                QueryParser parser = new QueryParser(Version.LUCENE_30, "abstract", new DVNAnalyzer());
                parser.setDefaultOperator(QueryParser.AND_OPERATOR);

                StringBuilder sbOuter = new StringBuilder();
                logger.fine("finding collection for id: " + studyListing.getCollectionId());
                VDCCollection col = vdcCollectionService.find(studyListing.getCollectionId());
                String type = col.getType();
                String queryString = col.getQuery();
               boolean isDynamic = col.isDynamic();
                boolean isLocalScope = col.isLocalScope();
                boolean isSubnetworkScope = col.isSubnetworkScope();
                boolean isRootCollection = col.isRootCollection();
                logger.fine("Single collection query... For " + col.getName() + " (isRootCollection=" + isRootCollection + "|type=" + type + "|isDynamic=" + isDynamic + "|isLocalScope=" + isLocalScope + ")  query: <<<" + queryString + ">>>");

                if (queryString != null && !queryString.isEmpty()) {
                    try {
                        logger.fine("For " + col.getName() + " (isRootCollection=" + isRootCollection + "|type=" + type + "|isDynamic=" + isDynamic + "|isLocalScope=" + isLocalScope + ") adding query: <<<" + queryString + ">>>");
                        Query dynamicQuery = parser.parse(queryString);
                        if (isLocalScope) {
                            BooleanQuery dynamicLocal = new BooleanQuery();
                            //Query dvOwnerIdQuery = indexService.constructDvOwnerIdQuery(getVDCRequestBean().getCurrentVDC());
                            Query dvOwnerIdQuery = indexService.constructDvOwnerIdQuery(col.getOwner());
                            dynamicLocal.add(dynamicQuery, BooleanClause.Occur.MUST);
                            dynamicLocal.add(dvOwnerIdQuery, BooleanClause.Occur.MUST);
                            finalQuery = dynamicLocal;
                        } else if (isSubnetworkScope) {
                            BooleanQuery dynamicLocal = new BooleanQuery();
                            Long subNetId = getVDCRequestBean().getCurrentVdcNetwork().getId();
                            //Query dvnetIdQuery = indexService.constructNetworkIdQuery(getVDCRequestBean().getCurrentVdcNetwork().getId());
                            Query dvnetIdQuery = indexService.constructNetworkOwnerIdQuery(col.getOwner().getVdcNetwork().getId());
                            dynamicLocal.add(dynamicQuery, BooleanClause.Occur.MUST);
                            dynamicLocal.add(dvnetIdQuery, BooleanClause.Occur.MUST);
                            finalQuery = dynamicLocal;
                        } else {
                            finalQuery = dynamicQuery;
                        }
                    } catch (org.apache.lucene.queryParser.ParseException ex) {
                        Logger.getLogger(StudyListingPage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    logger.fine("For " + col.getName() + " (isRootCollection=" + isRootCollection + "|type=" + type + "|isDynamic=" + isDynamic + "|isLocalScope=" + isLocalScope + ") skipping add of query: <<<" + queryString + ">>>");
                    List<Study> studies = col.getStudies();
                    StringBuilder sbInner = new StringBuilder();
                    for (Study study : studies) {
                        logger.fine("- has StudyId: " + study.getId());
                        String idColonId = "id:" + study.getId().toString() + " ";
                        sbInner.append(idColonId);
                    }
                    if (isRootCollection) {
                        List<Long> rootCollectionStudies = vdcService.getOwnedStudyIds(col.getOwner().getId());
                        for (Long id : rootCollectionStudies) {
                            logger.fine("- has StudyId: " + id);
                            String idColonId = "id:" + id.toString() + " ";
                            sbInner.append(idColonId);
                        }
                    }
                    logger.fine("sbInner: " + sbInner.toString());
                    sbOuter.append(sbInner);
                }

                logger.fine("sbOuter: " + sbOuter);
                if (!sbOuter.toString().isEmpty()) {
                    try {
                        parser.setDefaultOperator(QueryParser.OR_OPERATOR);
                        Query staticColQuery = parser.parse(sbOuter.toString());
                        parser.setDefaultOperator(QueryParser.AND_OPERATOR);
                        logger.fine("staticCollectionQuery: " + staticColQuery);
                        finalQuery = staticColQuery;
                    } catch (org.apache.lucene.queryParser.ParseException ex) {
                        Logger.getLogger(AdvSearchPage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                dvnQuery.setSingleCollectionQuery(finalQuery);
                dvnQuery.setVdc(getVDCRequestBean().getCurrentVDC());
                dvnQuery.constructQuery();
                resultsWithFacets = indexService.searchNew(dvnQuery);
                studyIDList = resultsWithFacets.getMatchIds();

            } else if (searchFilter == 2) {
                // subsearch
                logger.fine("with these results searches disabled per https://redmine.hmdc.harvard.edu/issues/2969 ");
//                studyIDList = indexService.search(studyListing.getStudyIds(), searchTerms); // old method
//                dvnQuery.setLimitToStudyIds(studyListing.getStudyIds());
//                dvnQuery.setSearchTerms(searchTerms);
//                dvnQuery.constructQuery();
//                resultsWithFacets = indexService.searchNew(dvnQuery);
//                studyIDList = resultsWithFacets.getMatchIds();
            } else {
                logger.fine("single collection not selected");
                logger.fine("current subnetwork: " + getVDCRequestBean().getCurrentVdcNetwork().getId());
                dvnQuery.setVdc(getVDCRequestBean().getCurrentVDC());

                if (dvnQuery.getVdc() != null) {
                    /**
                     * At the dataverse level, search should not be affected by
                     * the value of getVDCRequestBean().getCurrentVdcNetwork()
                     * which tells us which subnetwork we are currently in.
                     *
                     * That is to say, we don't need to bother to check that
                     * value or make any decisions based on it.
                     */
                    dvnQuery.setDvOwnerIdQuery(indexService.constructDvOwnerIdQuery(dvnQuery.getVdc()));
                    dvnQuery.setCollectionQueries(indexService.getCollectionQueries(dvnQuery.getVdc()));
                    dvnQuery.setSearchTerms(searchTerms);
                    dvnQuery.constructQuery();
                    resultsWithFacets = indexService.searchNew(dvnQuery);
                    studyIDList = resultsWithFacets.getMatchIds();
                } else {
                    Long rootSubnetworkId = getVDCRequestBean().getVdcNetwork().getId();
                    Long currentSubnetworkId = getVDCRequestBean().getCurrentVdcNetwork().getId();
                    if (!currentSubnetworkId.equals(rootSubnetworkId)) {
                        Query subNetworkQuery = indexService.constructNetworkIdQuery(currentSubnetworkId);
                        dvnQuery.setSubNetworkQuery(subNetworkQuery);
                    }
                    dvnQuery.setSearchTerms(searchTerms);
                    dvnQuery.constructQuery();
                    resultsWithFacets = indexService.searchNew(dvnQuery);
                    studyIDList = resultsWithFacets.getMatchIds();
                }
            }
            if (searchField.equals("any")) {
                List<Long> versionIds = indexService.searchVersionUnf(getVDCRequestBean().getCurrentVDC(), searchValue);
                Iterator iter = versionIds.iterator();
                Long studyId = null;
                while (iter.hasNext()) {
//                    List<StudyVersion> svList = new ArrayList<StudyVersion>();
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
                        if (!studyIDList.contains(studyId)) {
                            displayVersionList.add(studyId);
                            studyIDList.add(studyId);
                        }
                        versionMap.put(studyId, svList);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }

            }
        }


        // now we handle the display of the page
        // first get the bound collection tree
        collectionTree = studyListing.getCollectionTree();

        // now create the new StudyListing
        studyListing = new StudyListing(StudyListing.SEARCH);
        studyListing.setVdcId(getVDCRequestBean().getCurrentVDCId());
        studyListing.setStudyIds(studyIDList);
        studyListing.setResultsWithFacets(resultsWithFacets);
        studyListing.setSearchTerms(searchTerms);
        studyListing.setVariableMap(variableMap);
        studyListing.setVersionMap(versionMap);
        studyListing.setCollectionTree(collectionTree);
        studyListing.setDisplayStudyVersionsList(displayVersionList);
        renderFacets = true;

        String studyListingIndex = StudyListing.addToStudyListingMap(studyListing, getSessionMap());
        return "/StudyListingPage.xhtml?faces-redirect=true&studyListingIndex=" + studyListingIndex + getContextSuffix();
    }

    public void sort_action(ValueChangeEvent event) {
        String sortBy = (String) event.getNewValue();
        if (sortBy == null || sortBy.equals("")) {
            return;
        }
        
        /*
         * We're storing sorted lists in a map to eliminate multiple calls
         * and have a way to save the relevence sort from the search
         */
        if (studyListing.getStudyIds() != null && studyListing.getStudyIds().size() > 0) {
            if (studyListing.getSortMap().get(sortBy) != null) {
                studyListing.setStudyIds(studyListing.getSortMap().get(sortBy));
                resetScroller();
            } else {
                //Add the relevance sort to map if available
                if (studyListing.getMode() == StudyListing.SEARCH && studyListing.getSortMap().isEmpty()) {
                    studyListing.getSortMap().put("relevance", studyListing.getStudyIds());
                }
                //Do the actual sort, if necessary
                List sortedStudies = studyService.getOrderedStudies(studyListing.getStudyIds(), sortBy);
                studyListing.setStudyIds(sortedStudies);
                studyListing.getSortMap().put(sortBy, sortedStudies);
                resetScroller();
            }
        }
    }

    private void resetScroller() {
        if (paginator != null) {
            paginator.gotoFirstPage();
        }
        if (paginator2 != null) {
            paginator2.gotoFirstPage();
        }
    }
    
    public void preRenderView() {
       super.preRenderView();
       // add javascript call on each partial submit to initialize the help tips for added fields
       JavascriptContext.addJavascriptCall(getFacesContext(),"initAbstractTruncate();");
    }

    public void init() {
        super.init();
        if (isFromPage("StudyListingPage")) { // this is a post, so get the studyListing and let actions handle the rest
            String slIndex = getRequestParam("form1:studyListingIndex");
            if (slIndex != null) {
                studyListing = getStudyListingFromMap(slIndex);
                initPageComponents(studyListing.getMode());
            }
        } else {
            // first check for slIndex
            String slIndex = getRequestParam("studyListingIndex");
            if (slIndex != null) {
                studyListing = getStudyListingFromMap(slIndex);
                setStudyListingIndex(slIndex);

            } else {
                // we need to create a new studyListing
                initNewStudyListing();
            }
            
            initStudies();
            initPageComponents(studyListing.getMode());
            if (renderTree) {
                initCollectionTree();
            }

        }
    }

    private void initStudies() {
        if (studyListing.getStudyIds() != null) {
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            
            // first filter the visible studies; visible studies are those that are released
            // and not from a restricted VDC (unless you are in that VDC)
            studyListing.setStudyIds( studyService.getVisibleStudies( studyListing.getStudyIds(), (vdc != null ? vdc.getId() : null) ) );
            if (vdc != null && vdc.getDefaultSortOrder() != null){
                String sortBy = vdc.getDefaultSortOrder();
                if (studyListing.getStudyIds() != null && studyListing.getStudyIds().size() > 0) {
                    List sortedStudies = studyService.getOrderedStudies(studyListing.getStudyIds(), sortBy);
                    studyListing.setStudyIds(sortedStudies);
                    resetScroller();
                }
            }

            /*
            // next determine user role:
            // if networkAdmin, skip viewable filter (all studies are viewable)
            // if vdc admin or curator, allow vdc's owned studies to pass through filter (by sending vdcId)

            VDCUser user = getVDCSessionBean().getUser();
            UserGroup usergroup = getVDCSessionBean().getIpUserGroup();
            Long passThroughVdcId = null;

             if (user != null) {
                if (user.getNetworkRole() != null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
                    return;

                } else if (vdc != null) {
                    VDCRole userRole = user.getVDCRole(vdc);
                    String userRoleName = userRole != null ? userRole.getRole().getName() : null;
                    if (RoleServiceLocal.ADMIN.equals(userRoleName) || RoleServiceLocal.CURATOR.equals(userRoleName)) {
                        passThroughVdcId = vdc.getId();
                    }
                }
            }

            studyListing.getStudyIds().retainAll(studyService.getViewableStudies(
                    studyListing.getStudyIds(),
                    (user != null ? user.getId() : null),
                    (usergroup != null ? usergroup.getId() : null),
                    passThroughVdcId ) );
            */
        }
    }

    private void initPageComponents(int mode) {

        sortOrderItems = loadSortSelectItems(mode);
        
        String sort;
        
        sort = getRequestParam("sort");
        
        sortOrderString = sort;
         
        int matches = studyListing.getStudyIds() != null ? studyListing.getStudyIds().size() : 0;
        renderSort = matches == 0 ? false : true;
        renderScroller = matches < 10 ? false : true;

        // default the following to false; will likely change after checking mode
        renderSearch = true;
        renderDVPermissionsBox = true;
        renderSearchResultsFilter = false;
        renderSearchCollectionFilter = false;
        renderDescription = false;
        renderContributorLink = false;
        renderTree = false;
        renderDownloadCount = false;


        
        if (mode == StudyListing.SEARCH) {
            listHeader = "Search Results";
            listMessage = "";

            if (studyListing.getSearchTerms() != null) {
                Iterator iter = studyListing.getSearchTerms().iterator();
                while (iter.hasNext()) {
                    SearchTerm st = (SearchTerm) iter.next();
                    if (st.getFieldName().equals("dvNetworkId")) {
                        VDCNetwork subnetwork = vdcNetworkService.findById(new Long(st.getValue()));
                        String subnetworkName = subnetwork.getName();
                        listMessage += getUserFriendlySearchField(st.getFieldName()) + " " + st.getOperator() + " \"" + subnetworkName + "\"";
                    } else {
                        listMessage += getUserFriendlySearchField(st.getFieldName()) + " " + st.getOperator() + " \"" + st.getValue() + "\"";
                    }
                    if (iter.hasNext()) {
                        listMessage += " AND ";
                    }
                }
            }

            if (matches == 0) {
                listMessage = "Your search for " + listMessage + " returned no results.";
            } else {
                listMessage = "for " + listMessage;
            }
           
            sortOrderString = "relevance";
            renderSearchResultsFilter = matches == 0 ? false : true;
            renderFacets = studyListing.getResultsWithFacets() != null && studyListing.getResultsWithFacets().getResultList() != null && studyListing.getResultsWithFacets().getResultList().size() > 0 ? true : false;
            renderDVPermissionsBox = false;

        } else if (mode == StudyListing.COLLECTION_STUDIES) {
            listHeader = vdcCollectionService.find(studyListing.getCollectionId()).getName();
            listDescription =  vdcCollectionService.find(studyListing.getCollectionId()).getDescription();
            VDC currentVDC = getVDCRequestBean().getCurrentVDC();
            if (currentVDC == null ||
                    (currentVDC.getRootCollection().getSubCollections().size() == 0 &&
                    currentVDC.getLinkedCollections().size() == 0)) {
                renderTree = false;
            } else {
                renderTree = true;
            }
                
            renderDownloadCount = true;

            if (getVDCRequestBean().getCurrentVDC().getRootCollection().getId().equals(studyListing.getCollectionId()) && collectionIdInURL == null) {
                renderSearchCollectionFilter = false;
            } else {
                renderSearchCollectionFilter = true;
            }

            renderDescription = getVDCRequestBean().getCurrentVDC().isDisplayAnnouncements();

            LoginBean loginBean = getVDCSessionBean().getLoginBean();
            renderContributorLink =
                getVDCRequestBean().getCurrentVDC() != null &&
                getVDCRequestBean().getCurrentVDC().isAllowContributorRequests() &&
                (loginBean == null || (loginBean != null && loginBean.isBasicUser() ) );

            if (matches == 0) {
                listMessage = "There are no studies in this " + (renderTree ? "collection." : "dataverse.");
            }

        } else if (mode == StudyListing.VDC_RECENT_STUDIES) {
            listHeader = "Studies Uploaded and Released to This Dataverse";
            renderSearch = true;
            renderSearchResultsFilter = matches == 0 ? false : true;

        } else if (mode == StudyListing.GENERIC_LIST) {
            // this needs to be fleshed out if it's ever used
            listHeader = "Studies";
            /**
             * @todo show facets when browsing studies?
             */
//            renderSearchResultsFilter = true;
            
        } else {
            // in this case we have an invalid list
            if (mode == StudyListing.GENERIC_ERROR) {
                listHeader = "Error";
                listMessage = "Sorry. You must specify a valid mode (and corresponding parameters) for this page.";
            } else if (mode == StudyListing.EXPIRED_LIST) {
                listHeader = "Expired Listing";
                listMessage = "The results for this listing have expired.";
            } else if (mode == StudyListing.INCORRECT_VDC) {
                listHeader = "Invalid Listing";
                listMessage = "The results for this listing were generated while searching or browsing a different dataverse.";
            }
        }

    }

    private String getUserFriendlySearchField(String searchField) {
        try {
            return ResourceBundle.getBundle("SearchFieldBundle").getString(searchField);
        } catch (MissingResourceException e) {
            return searchField;
        }
    }

    private void initCollectionTree() {
        VDCCollectionTree vdcTree = null;

        if (studyListing.getCollectionTree() == null) {
            vdcTree = new VDCCollectionTree();
        } else {
            vdcTree = new VDCCollectionTree(studyListing.getCollectionTree());
        }

        vdcTree.setCollectionUrl("/faces/StudyListingPage.xhtml?mode=1");

        if (studyListing.getCollectionId() != null) {
            vdcTree.setCollectionToBeExpanded(new Long(studyListing.getCollectionId()));
        }

        collectionTree = vdcTree.populate(getVDCRequestBean().getCurrentVDC());
        studyListing.setCollectionTree(collectionTree);

    /* OLD code which sets up different type of tree depending on mode
    if (studyListing.getMode() == StudyListing.SEARCH) {
    // performace of filtering the tree is slow, so for now just show entire tree
    //vdcTree.setStudyFilter(studies);
    //vdcTree.setIncludeCount(true);
    vdcTree.setCollectionUrl("/faces/StudyListingPage.xhtml?mode=3&oslIndex=" + studyListingIndex);
    } else {
    vdcTree.setCollectionUrl("/faces/StudyListingPage.xhtml?mode=1");
    }
     */

    }

   @Inject VDCApplicationBean vdcApplicationBean;
         
    private void initNewStudyListing() {
        StudyListing sl = null;
        int mode = -1;
        try {
            mode = Integer.parseInt(getRequestParam("mode"));
        } catch (Exception e) {
        }         // mode is -1

        String sort;
        
        sort = getRequestParam("sort");
        
        sortOrderString = sort;
        
        if (mode == StudyListing.COLLECTION_STUDIES) {
            String collectionId = getRequestParam("collectionId");
            if (collectionId != null) {
                sl = new StudyListing(StudyListing.COLLECTION_STUDIES);
                sl.setCollectionId(new Long(collectionId));
                CollectionUI collUI = new CollectionUI(vdcCollectionService.find(new Long(sl.getCollectionId())));
                sl.setStudyIds(collUI.getStudyIds());
                setStudyListingIndex(addToStudyListingMap(sl));
            }

        } else if (mode == StudyListing.SEARCH) {
            String searchValue = getRequestParam("searchValue");
            if (searchValue != null) {
                String searchField = getRequestParam("searchField");
                if (searchField == null) {
                    searchField = "any"; // set a default searchField
                }

                sl = search(searchField, searchValue);
                setStudyListingIndex(addToStudyListingMap(sl));
                prefillSearchValue(sl);
            }

        } else if (mode == StudyListing.COLLECTION_FILTER) {
            String oslIndex = getRequestParam("oslIndex");
            String collectionId = getRequestParam("collectionId");
            if (oslIndex != null && collectionId != null) {
                StudyListing osl = getStudyListingFromMap(oslIndex);
                if (osl.getMode() > 0) { // all study listings <= 0 are error type listings
                    List newStudyIds = new ArrayList();

                    // so we create a new studyListing based on the old one
                    sl = new StudyListing(osl.getMode());
                    sl.setSearchTerms(osl.getSearchTerms());
                    sl.setVariableMap(osl.getVariableMap());
                    sl.setCollectionId(new Long(collectionId));
                    VDCCollection narrowingColl = vdcCollectionService.find(sl.getCollectionId());
                    Iterator iter = osl.getStudyIds().iterator();
                    while (iter.hasNext()) {
                        Study study = studyService.getStudy((Long) iter.next());
                        if (VDCCollectionTree.isStudyInCollection(study, narrowingColl, true)) {
                            newStudyIds.add(study.getId());
                        }
                    }
                    sl.setStudyIds(newStudyIds);
                    setStudyListingIndex(addToStudyListingMap(sl));
                }
            }

        } else if (mode == StudyListing.VDC_RECENT_STUDIES) {
            int numResults = 100;
            try {
                numResults = Integer.parseInt(getRequestParam("numResults"));
            } catch (Exception e) {
            } // numResults remains 100

            sl = new StudyListing(StudyListing.VDC_RECENT_STUDIES);
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            if (vdc != null) {
                VDCUser user = getVDCSessionBean().getUser();
                // TODO: change filter method to only return studyIds
                List studies = StudyUI.filterVisibleStudies(studyService.getRecentStudies(vdc.getId(), -1), vdc, user, getVDCSessionBean().getIpUserGroup(), numResults);
                List studyIds = new ArrayList();
                Iterator iter = studies.iterator();
                while (iter.hasNext()) {
                    Study study = (Study) iter.next();
                    studyIds.add(study.getId());
                }
                sl.setStudyIds(studyIds);
            } else {
                sl.setStudyIds(new ArrayList());
            }

            setStudyListingIndex(addToStudyListingMap(sl));

        } else {
            // in this case we don't have a mode so we check to see if we
            // have a studyListing passed via the request
            sl = getVDCRequestBean().getStudyListing();
            if (sl != null) {
                setStudyListingIndex(addToStudyListingMap(sl));
                prefillSearchValue(sl);
            }
        }

        // no params; default behavior, show root collection (or first linked); for dvn, show error version
        if (sl == null) {
            VDC currentVDC = getVDCRequestBean().getCurrentVDC();
            if (currentVDC != null) {
                sl = new StudyListing(StudyListing.COLLECTION_STUDIES);
                if ( new VDCUI(currentVDC).containsOnlyLinkedCollections() ) {
                    sl.setCollectionId(new Long(getVDCRequestBean().getCurrentVDC().getLinkedCollections().get(0).getId()));
                } else {
                    sl.setCollectionId(new Long(getVDCRequestBean().getCurrentVDC().getRootCollection().getId()));
                }

                CollectionUI collUI = new CollectionUI(vdcCollectionService.find(new Long(sl.getCollectionId())));
                sl.setStudyIds(collUI.getStudyIds());
                setStudyListingIndex(addToStudyListingMap(sl));

            }
            else if (mode == StudyListing.GENERIC_LIST && sort != null  && sort.equals("downloadCount")) {
                sl = new StudyListing(StudyListing.GENERIC_LIST);
                sortOrderString = "downloadCount";
                sl.setStudyIds(vdcApplicationBean.getAllStudyIdsByDownloadCount(getVDCRequestBean().getCurrentVdcNetwork().getId()));
                sl.getSortMap().put("downloadCount", sl.getStudyIds());
            } else if (mode == StudyListing.GENERIC_LIST) {
                sl = new StudyListing(StudyListing.GENERIC_LIST);
                sortOrderString = "releaseTime";
                sl.setStudyIds(vdcApplicationBean.getAllStudyIdsByReleaseDate(getVDCRequestBean().getCurrentVdcNetwork().getId()));
                sl.getSortMap().put("releaseTime", sl.getStudyIds());
                /**
                 * @todo show facets when browsing studies?
                 */
//                MatchAllDocsQuery query = new MatchAllDocsQuery();
//                baseQuery = query;
//                ResultsWithFacets resultsWithFacets = indexService.getResultsWithFacets(query, null);
//                sl.setStudyIds(resultsWithFacets.getMatchIds());
//                sl.setResultsWithFacets(resultsWithFacets);
            } else {
                sl = new StudyListing(StudyListing.GENERIC_ERROR);
            }
        }

        studyListing = sl;
        studyListing.setVdcId(getVDCRequestBean().getCurrentVDCId());

    }

    private void prefillSearchValue(StudyListing sl) {
        // this method will prefill the search value if the search done was "any" = xxxxx
        // this covers search from the home page and search from a promo link
         if (sl.getSearchTerms() != null && sl.getSearchTerms().size() == 1) {
            SearchTerm st = (SearchTerm) sl.getSearchTerms().get(0);
            if (st.getFieldName().equals("any") && st.getOperator().equals("=")) {
                searchValue = st.getValue();
            }
        }
    }

    private String addToStudyListingMap(StudyListing sl) {
        return StudyListing.addToStudyListingMap(sl, getSessionMap());
    }

    private StudyListing getStudyListingFromMap(String slIndex) {
        return StudyListing.getStudyListingFromMap(slIndex, getSessionMap(), getVDCRequestBean().getCurrentVDCId());
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public DataPaginator getPaginator() {
        return paginator;
    }

    public void setPaginator(DataPaginator paginator) {
        this.paginator = paginator;
    }

    public DataPaginator getPaginator2() {
        return paginator2;
    }

    public void setPaginator2(DataPaginator paginator2) {
        this.paginator2 = paginator2;
    }

    private List<SelectItem> loadSortSelectItems(int mode){
        List selectItems = new ArrayList<SelectItem>();
        if (mode== StudyListing.SEARCH){ 
             selectItems.add(new SelectItem("relevance", "Relevance"));
        }
        selectItems.add(new SelectItem("globalId", "Global ID"));
        selectItems.add(new SelectItem("title", "Title"));
        selectItems.add(new SelectItem("releaseTime", "Most Recently Released"));
        selectItems.add(new SelectItem("productionDate", "Production Date"));
        selectItems.add(new SelectItem("downloadCount", "Most Downloaded"));

        return selectItems;
    }
    
    public List getSortOrderItems() {
        return sortOrderItems;
    }

    public void setSortOrderItems(List sortOrderItems) {
        this.sortOrderItems = sortOrderItems;
    }
    
    public String getSortOrderString() {
        return sortOrderString;
    }

    public void setSortOrderString(String sortOrderString) {
        this.sortOrderString = sortOrderString;
    }

    public Map getStudyFields() {
        if (studyFields == null) {
            studyFields = new HashMap();
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            if (vdc != null) {
                Iterator iter = vdc.getSearchResultFields().iterator();
                while (iter.hasNext()) {
                    StudyField sf = (StudyField) iter.next();
                    studyFields.put(sf.getName(), sf.getName());
                }
            }
        }

        return studyFields;
    }
    
    

    // booleans for search filters
    boolean searchResultsFilter;
    boolean searchCollectionFilter;
    boolean renderSearchResultsFilter;
    boolean renderSearchCollectionFilter;

    public boolean isSearchResultsFilter() {
        return searchResultsFilter;
    }

    public void setSearchResultsFilter(boolean searchResultsFilter) {
        this.searchResultsFilter = searchResultsFilter;
    }

    public boolean isSearchCollectionFilter() {
        return searchCollectionFilter;
    }

    public void setSearchCollectionFilter(boolean searchCollectionFilter) {
        this.searchCollectionFilter = searchCollectionFilter;
    }

    public boolean isRenderSearchResultsFilter() {
        return renderSearchResultsFilter;
    }

    public void setRenderSearchResultsFilter(boolean renderSearchResultsFilter) {
        this.renderSearchResultsFilter = renderSearchResultsFilter;
    }

    public boolean isRenderSearchCollectionFilter() {
        return renderSearchCollectionFilter;
    }

    public void setRenderSearchCollectionFilter(boolean renderSearchCollectionFilter) {
        this.renderSearchCollectionFilter = renderSearchCollectionFilter;
    }

    private StudyListing search(String searchField, String searchValue) {
        // TODO: combine the search logic from the search_action (which has more
        // flexibility) with this one for one unified search code section
        
        // (what does this method do? is this the same search that search_action
        // implements, but run by hitting return, as opposed to clicking on 
        // "go"? -- L.A.)
        List searchTerms = new ArrayList();
        SearchTerm st = new SearchTerm();
        st.setFieldName(searchField);
        st.setValue(searchValue);
        searchTerms.add(st);

        List studies = new ArrayList();
        Map variableMap = new HashMap();

        if (searchField.equals("variable")) {
            List variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            varService.determineStudiesFromVariables(variables, studies, variableMap);

        } else {
            studies = indexService.search(getVDCRequestBean().getCurrentVDC(), searchTerms);
        }


        StudyListing sl = new StudyListing(StudyListing.SEARCH);
        sl.setStudyIds(studies);
        sl.setSearchTerms(searchTerms);
        sl.setVariableMap(variableMap);

        sl.setVdcId(getVDCRequestBean().getCurrentVDCId());
        sl.setCollectionTree(collectionTree);

        return sl;
    }

    public int getStudyCount() {
        if (studyListing != null && studyListing.getStudyIds() != null){
            return studyListing.getStudyIds().size();
        }
        
        return 0;
    }

    public int getCollectionTreeVisibleNodeCount() {
        int visibleNodeCount = 0;
        if (renderTree) {
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) collectionTree.getRoot();
            visibleNodeCount = countVisibleNodes(rootNode) - 1; // subtract 1 to remove the hidden root from the count
        }

        return visibleNodeCount;
    }

    private int countVisibleNodes(DefaultMutableTreeNode node) {
        int count = 1;

        if ( ((IceUserObject) node.getUserObject()).isExpanded()) {
            Enumeration childrenEnum = node.children();
            while (childrenEnum.hasMoreElements()) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) childrenEnum.nextElement();
                count += countVisibleNodes(childNode);
            }
        }

        return count;
    }

    public void setStudyListingByFacets(String facetKey, String facetValue) {
        logger.fine("called setStudyListingByFacets()");

        if (getVDCRequestBean().getCurrentVDC() != null) {
            recentVisitToDvPage = true;
        } else {
            if (recentVisitToDvPage == true) {
                // clear all facets??
                logger.fine("Clearing facets");
                facetsOfInterest = null;
                // reset flag for next time
                recentVisitToDvPage = false;
            }
        }
        CategoryPath facetToAdd = new CategoryPath(facetKey, facetValue);
        if (!facetsOfInterest.contains(facetToAdd)) {
            facetsOfInterest.add(facetToAdd);
        }

        if (studyListing.getResultsWithFacets() != null && studyListing.getResultsWithFacets().isClearPreviousFacetRequests() == false) {
            for (int i = 0; i < studyListing.getResultsWithFacets().getFacetsQueried().size(); i++) {
                CategoryPath queriedFacet = studyListing.getResultsWithFacets().getFacetsQueried().get(i);
                logger.fine("in setStudyListingBy Facet, adding facet " + i + ": " + queriedFacet);
                if (!facetsOfInterest.contains(queriedFacet)) {
                    facetsOfInterest.add(queriedFacet);
                }
            }
        }

        Query query = null;
        if (baseQuery != null) {
            query = baseQuery;
        } else {
            List<BooleanQuery> searchParts = new ArrayList();
            List<SearchTerm> studyLevelSearchTerms = new ArrayList();
            for (Iterator it = studyListing.getSearchTerms().iterator(); it.hasNext();) {
                SearchTerm elem = (SearchTerm) it.next();
                studyLevelSearchTerms.add(elem);
            }
            BooleanQuery searchTermsQuery = indexService.andSearchTermClause(studyLevelSearchTerms);
            searchParts.add(searchTermsQuery);
            BooleanQuery booleanQuery = indexService.andQueryClause(searchParts);
            query = booleanQuery;
        }

        DvnQuery dvnQuery = new DvnQuery();
        dvnQuery.setVdc(getVDCRequestBean().getCurrentVDC());
        /**
         * @todo: pass in search terms instead?
         */
        if (studyListing.getResultsWithFacets().getBaseQuery() != null) {
            dvnQuery.setQuery(studyListing.getResultsWithFacets().getBaseQuery());
        } else {
            dvnQuery.setQuery(query);
        }
        logger.fine("in setStudyListingByFacets, going to query these facets: " + facetsOfInterest.toString());
        dvnQuery.setFacetsToQuery(facetsOfInterest);
//        ResultsWithFacets resultsWithFacets = indexService.getResultsWithFacets(query, facetsOfInterest);
        ResultsWithFacets resultsWithFacets = indexService.searchNew(dvnQuery);

        studyListing.setStudyIds(resultsWithFacets.getMatchIds());
        studyListing.setResultsWithFacets(resultsWithFacets);
    }

    public void removeFacet(CategoryPath facetToRemove) {
        for (Iterator<CategoryPath> it = facetsOfInterest.iterator(); it.hasNext();) {
            CategoryPath facet = it.next();
            if (facet.equals(facetToRemove)) {
                it.remove();
            }
        }

        /**
         * @todo: refactor this copy/paste from elsewhere
         */
        Query query = null;
        if (baseQuery != null) {
            query = baseQuery;
        } else {
            List<BooleanQuery> searchParts = new ArrayList();
            List<SearchTerm> studyLevelSearchTerms = new ArrayList();
            for (Iterator it = studyListing.getSearchTerms().iterator(); it.hasNext();) {
                SearchTerm elem = (SearchTerm) it.next();
                studyLevelSearchTerms.add(elem);
            }
            BooleanQuery searchTermsQuery = indexService.andSearchTermClause(studyLevelSearchTerms);
            searchParts.add(searchTermsQuery);
            BooleanQuery booleanQuery = indexService.andQueryClause(searchParts);
            query = booleanQuery;
        }

//        ResultsWithFacets resultsWithFacets = indexService.getResultsWithFacets(query, facetsOfInterest);
        DvnQuery dvnQuery = new DvnQuery();
//        dvnQuery.setQuery(query);
        if (studyListing.getResultsWithFacets().getBaseQuery() != null) {
            dvnQuery.setQuery(studyListing.getResultsWithFacets().getBaseQuery());
        } else {
            dvnQuery.setQuery(query);
        }

        dvnQuery.setVdc(getVDCRequestBean().getCurrentVDC());
        // should I have to set this every time? make it static, part of dvnQuery?
        dvnQuery.setFacetsToQuery(facetsOfInterest);
        ResultsWithFacets resultsWithFacets = indexService.searchNew(dvnQuery);
        studyListing.setStudyIds(resultsWithFacets.getMatchIds());
        studyListing.setResultsWithFacets(resultsWithFacets);
    }

    public List<CategoryPath> getFacetsQueried() {
        logger.fine("called getFacetsQueried...");
        logger.fine("facetsOfInterest = " + facetsOfInterest);
        if (studyListing.getResultsWithFacets() != null) {
            logger.fine("from ResultsWithFacets: " + studyListing.getResultsWithFacets().getBaseQuery());
            logger.fine("from ResultsWithFacets: " + studyListing.getResultsWithFacets().getFacetsQueried());
        }
        return studyListing.getResultsWithFacets() != null ? studyListing.getResultsWithFacets().getFacetsQueried() : null;
    }

    public String showFacetPath(String categoryPath) {
        String[] parts = categoryPath.split("/", 2);
        String field = parts[0];
        String value = parts[1];
        try {
            return ResourceBundle.getBundle("SearchFieldBundle").getString(field) + " = \"" + value + "\"";
        } catch (MissingResourceException e) {
            return categoryPath;
        }
    }
}
