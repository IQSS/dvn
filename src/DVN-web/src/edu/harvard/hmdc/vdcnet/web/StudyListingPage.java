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

/*
 * StudyListingPage.java
 *
 * Created on September 19, 2006, 6:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web;

import com.icesoft.faces.component.datapaginator.DataPaginator;
import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.index.SearchTerm;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyField;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.collection.CollectionUI;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.component.VDCCollectionTree;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.component.UIData;
import javax.faces.event.ValueChangeEvent;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author gdurand
 */
public class StudyListingPage extends VDCBaseBean  implements java.io.Serializable {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB StudyServiceLocal studyService;
    @EJB IndexServiceLocal indexService;
    @EJB VariableServiceLocal varService;

    /** Creates a new instance of StudyListingPageBean */
    public StudyListingPage() {
    }

    // data members
    private StudyListing studyListing;
    private DefaultTreeModel collectionTree;
    private UIData studyTable; // no longer bound on page (can probably be removed)
    private DataPaginator paginator;

    
    private String searchField;
    private String searchValue;
    private Integer searchFilter;
    private Map studyFields;

    private String studyListingIndex;

    // display items
    boolean renderTree;
    String treeHeader;

    boolean renderSearch;
    List searchRadioItems = new ArrayList();

    boolean renderSort;
    private boolean renderScroller;

    String listHeader;
    String listMessagePrefix;
    String listMessageContent;
    String listMessageSuffix;
    String subListHeader;


    public StudyListing getStudyListing() {
        return studyListing;
    }

    public void setStudyListing(StudyListing studyListing) {
        this.studyListing = studyListing;
    }

    public Collection getStudies() {
        List studyUIList = new ArrayList();

        if ( studyListing != null && studyListing.getStudyIds() != null ) {
            Iterator iter = studyListing.getStudyIds().iterator();
            while (iter.hasNext()) {
                Long sid = (Long) iter.next();
                StudyUI sui = new StudyUI(sid, getStudyFields());
                if (studyListing.getVariableMap() != null) {
                    List dvList = (List) studyListing.getVariableMap().get( sid );
                    sui.setFoundInVariables( dvList );
                }
                studyUIList.add(sui) ;
            }
        }

        return studyUIList;
    }

    /**
     * Getter for property collectionTree.
     * @return Value of property collectionTree.
     */
    public DefaultTreeModel getCollectionTree() {
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
        return  listHeader;
    }

    public String getSubListHeader() {
        return  subListHeader;
    }

    public String getTreeHeader() {
        return  treeHeader;
    }

    public String getListMessagePrefix() {
        return  listMessagePrefix;
    }

    public String getListMessageContent() {
        return listMessageContent;
    }

    public String getListMessageSuffix() {
        return listMessageSuffix;
    }

    public List getSearchRadioItems() {
        return searchRadioItems;
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

    public String search_action() {
        List searchTerms = new ArrayList();
        SearchTerm st = new SearchTerm();
        st.setFieldName( searchField );
        st.setValue( searchValue );
        searchTerms.add(st);

        List studyIDList = new ArrayList();
        Map variableMap = new HashMap();

        if (searchFilter == null) { searchFilter = 0; }

        // currently search filter is determined from a set of boolean checkboxes
        if (searchResultsFilter) { searchFilter = 2; }
        if (searchCollectionFilter) { searchFilter = 1; }

        if ( searchField.equals("variable") ) {
            List variables = null;
            if ( searchFilter ==  1 ) {
                // just this collection
                List collections = new ArrayList();
                collections.add( vdcCollectionService.find( studyListing.getCollectionId() ) );
                variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), collections, st);
            } else if ( searchFilter == 2 ) {
                // subsearch
                variables = indexService.searchVariables(studyListing.getStudyIds(), st);
            } else {
                variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            }

            varService.determineStudiesFromVariables(variables, studyIDList, variableMap);
        } else {
            if ( searchFilter ==  1 ) {
                // just this collection
                List collections = new ArrayList();
                collections.add( vdcCollectionService.find( studyListing.getCollectionId() ) );
                studyIDList = indexService.search(getVDCRequestBean().getCurrentVDC(), collections, searchTerms);
            } else if ( searchFilter == 2 ) {
                // subsearch
                studyIDList = indexService.search(studyListing.getStudyIds(), searchTerms);
            } else {
                studyIDList = indexService.search(getVDCRequestBean().getCurrentVDC(), searchTerms);
            }
        }


        // now we handle the display of the page
        // first get the bound collection tree
        collectionTree = studyListing.getCollectionTree();

        // now create the new StudyListing
        studyListing = new StudyListing(StudyListing.SEARCH);
        studyListing.setVdcId( getVDCRequestBean().getCurrentVDCId() );
        studyListing.setStudyIds(studyIDList);
        studyListing.setSearchTerms(searchTerms);
        studyListing.setVariableMap(variableMap);
        studyListing.setCollectionTree(collectionTree);
        setStudyListingIndex(addToStudyListingMap(studyListing));

        // finally reinit!
        initStudies();
        initPageComponents(StudyListing.SEARCH);
        if (renderTree) {
            initCollectionTree();
        }

        resetScroller();
        return "search";
    }

    public void sort_action(ValueChangeEvent event) {
        String sortBy = (String) event.getNewValue();
        if ( sortBy == null || sortBy.equals("") ) {
            return;
        }

        if (studyListing.getStudyIds() != null && studyListing.getStudyIds().size() > 0) {
            List sortedStudies = studyService.getOrderedStudies(studyListing.getStudyIds(), sortBy);
            studyListing.setStudyIds(sortedStudies);
            resetScroller();


        }
    }

    private void resetScroller() {
        if (paginator != null) {
            paginator.gotoFirstPage();  
        }
    }


    public void init() {
        super.init();
        if ( isFromPage("StudyListingPage") ) { // this is a post, so get the studyListing and let actions handle the rest
            String slIndex = getRequestParam("form1:studyListingIndex");
            if (slIndex != null) {
                studyListing =  getStudyListingFromMap(slIndex);
                initPageComponents(studyListing.getMode());
            }
        } else {
            // first check for slIndex
            String slIndex = getRequestParam("studyListingIndex");
            if (slIndex != null) {
                studyListing =  getStudyListingFromMap(slIndex);
                setStudyListingIndex(slIndex);

            } else {
                // we need to create a new studyListing
                initNewStudyListing();
                initStudies();
            }

            initPageComponents(studyListing.getMode());
            if (renderTree) {
                initCollectionTree();
            }

        }
    }


    private void initStudies() {
        if (studyListing.getStudyIds() != null) {
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            VDCUser user = getVDCSessionBean().getUser();
            UserGroup usergroup = getVDCSessionBean().getIpUserGroup();

            // first filter the visible studies; visible studies are those that are released
            // and not from a restricted VDC )unless you are in that VDC)
            studyListing.getStudyIds().retainAll( studyService.getVisibleStudies(
                studyListing.getStudyIds(),
                vdc!= null ? vdc.getId() : null
            ) );


            // next  determine if user is admin or curator of that vdc, or networkAdmin; if they are, they
            // can see all visible studies; otherwise we have to filter out those that are restricted to them
            boolean isAdminOrCurator = false;
            if (user != null) {
                if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
                    isAdminOrCurator = true;
                } else {
                    VDCRole userRole = user.getVDCRole(vdc);
                    String userRoleName = userRole != null ? userRole.getRole().getName() : null;
                    if (RoleServiceLocal.ADMIN.equals(userRoleName) || RoleServiceLocal.CURATOR.equals(userRoleName) ) {
                        isAdminOrCurator = true;
                    }
                }
            }

            if (!isAdminOrCurator) {
                studyListing.getStudyIds().retainAll( studyService.getViewableStudies(
                    studyListing.getStudyIds(),
                    (user != null ? user.getId() : null),
                    (usergroup != null ? usergroup.getId() : null)
                ) );
            }
        }
    }



    private void initPageComponents(int mode) {
        int matches = studyListing.getStudyIds() != null ? studyListing.getStudyIds().size() : 0;
        renderSort = matches == 0 ? false : true;
        renderScroller = matches == 0 ? false : true;

        // dafault the following to false; will likely change after checking mode
        renderSearchResultsFilter = false;
        renderSearchCollectionFilter = false;

        subListHeader = null;


        if (mode == StudyListing.SEARCH) {
            listHeader =  "Results";
            listMessagePrefix = "You searched for " ;

            listMessageContent = "";
            if (studyListing.getSearchTerms() != null) {
                Iterator iter = studyListing.getSearchTerms().iterator();
                while (iter.hasNext()) {
                    SearchTerm st = (SearchTerm) iter.next();
                    listMessageContent += getUserFriendlySearchField(st.getFieldName()) + " " + st.getOperator() + " \"" + st.getValue() + "\"";
                    if ( iter.hasNext() ) {
                        listMessageContent += " AND ";
                    }
                }
            }

            if (matches == 1) {
                listMessageSuffix = "; " + matches + " match was found.";
            } else {
                listMessageSuffix = "; " + matches + " matches were found.";
            }


            renderSearch = true;
            renderSearchResultsFilter = matches == 0 ? false : true;
            /*
             searchRadioItems = new ArrayList();
            searchRadioItems.add(new SelectItem("0", "New Search"));
            searchRadioItems.add(new SelectItem("2", "Search These Results"));
            searchFilter = 0;
            */


        } else if (mode == StudyListing.COLLECTION_STUDIES) {
            CollectionUI collUI = new CollectionUI( vdcCollectionService.find( studyListing.getCollectionId() ) );
            subListHeader =  collUI.getShortCollectionPath(getVDCRequestBean().getCurrentVDC() );
            listHeader = vdcCollectionService.find( studyListing.getCollectionId() ).getName();

            if (matches == 0) {
                listMessageContent = "There are no studies in this collection.";
            } else if (matches == 1) {
                listMessageContent = "There is 1 study in this collection.";
            } else {
                listMessageContent = "There are " +  matches + " studies in this collection.";
            }


            renderSearch = true;
            renderSearchCollectionFilter = true;
            /*
            searchRadioItems = new ArrayList();
            searchRadioItems.add(new SelectItem("0", "All Collections"));
            searchRadioItems.add(new SelectItem("1", "This Collection"));
            searchFilter = 0;
             * */

        } else if (mode == StudyListing.VDC_RECENT_STUDIES) {
            listHeader =  "Studies Uploaded and Released to This Dataverse";
            renderSearch = true;
            renderSearchResultsFilter = matches == 0 ? false : true;
            /*
            searchRadioItems = new ArrayList();
            searchRadioItems.add(new SelectItem("0", "New Search"));
            searchRadioItems.add(new SelectItem("2", "Search These Results"));
            searchFilter = 0;
             * */

        } else if (mode == StudyListing.GENERIC_LIST) {
            // this needs to be fleshed out if it's ever used
            listHeader =  "Studies";

        } else {
            // in this case we have an invalid list
            renderSearch = true;
            renderTree = false;

            if (mode == StudyListing.GENERIC_ERROR) {
                listHeader =  "Error";
                listMessageContent = "Sorry. You must specify a valid mode (and corresponding parameters) for this page.";
            } else if (mode == StudyListing.EXPIRED_LIST) {
                listHeader =  "Expired Listing";
                listMessageContent = "The results for this listing have expired.";
            }
            else if (mode == StudyListing.INCORRECT_VDC) {
                listHeader =  "Invalid Listing";
                listMessageContent = "The results for this listing were generated while searching or browsing a different dataverse.";
            }
        }

        /* lastly if no search radio items have been set, add a default
        if (searchRadioItems == null || searchRadioItems.size() == 0) {
            searchRadioItems = new ArrayList();
            searchRadioItems.add(new SelectItem("0", "New Search"));
        }
        */


        // determine renderTree
        VDC currentVDC = getVDCRequestBean().getCurrentVDC();
        if (currentVDC == null ||
                (currentVDC.getRootCollection().getSubCollections().size() == 0 &&
                currentVDC.getLinkedCollections().size() == 0) ) {
            renderTree = false;
        } else {
            renderTree = true;
        }


    }
    
    private String getUserFriendlySearchField(String searchField) {
        try {
            return ResourceBundle.getBundle("SearchFieldBundle").getString( searchField );
        } catch (MissingResourceException e) {
            return searchField;
        }
    }

    private void initCollectionTree() {
        VDCCollectionTree vdcTree = null;
        
        if (studyListing.getCollectionTree() == null) {
            vdcTree = new VDCCollectionTree();
        } else {
            vdcTree = new VDCCollectionTree( studyListing.getCollectionTree() );    
        }

        vdcTree.setCollectionUrl("/faces/StudyListingPage.xhtml?mode=1");
        
        if (studyListing.getCollectionId() != null) {
            vdcTree.setCollectionToBeExpanded( new Long(studyListing.getCollectionId()) );
        }

        collectionTree = vdcTree.populate( getVDCRequestBean().getCurrentVDC() );
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

    private void initNewStudyListing() {
        StudyListing sl = null;
        int mode = -1;
        try {
            mode = Integer.parseInt(getRequestParam("mode"));
        } catch (Exception e) {
        }    // mode is -1


        if (mode == StudyListing.COLLECTION_STUDIES) {
            String collectionId = getRequestParam("collectionId");
            if (collectionId != null) {
                sl = new StudyListing(StudyListing.COLLECTION_STUDIES);
                sl.setCollectionId(new Long(collectionId));
                CollectionUI collUI = new CollectionUI( vdcCollectionService.find( new Long(sl.getCollectionId()) ) );
                sl.setStudyIds(collUI.getStudyIds());
                setStudyListingIndex(addToStudyListingMap(sl));
            }

        }  else if (mode == StudyListing.SEARCH) {
            String  searchValue = getRequestParam("searchValue");
            if (searchValue != null) {
                String  searchField = getRequestParam("searchField");
                if (searchField == null ) {
                    searchField = "any"; // set a default searchField
                }

                sl = search(searchField, searchValue);
                setStudyListingIndex(addToStudyListingMap(sl));
            }

        } else if (mode == StudyListing.COLLECTION_FILTER) {
            String  oslIndex = getRequestParam("oslIndex");
            String collectionId = getRequestParam("collectionId");
            if (oslIndex != null && collectionId != null) {
                StudyListing osl = getStudyListingFromMap(oslIndex);
                if (osl.getMode() > 0) { // all study listings <= 0 are error type listings
                    List newStudyIds = new ArrayList();

                    // so we create a new studyListing based on the old one
                    sl = new StudyListing( osl.getMode() );
                    sl.setSearchTerms(osl.getSearchTerms());
                    sl.setVariableMap(osl.getVariableMap());
                    sl.setCollectionId(new Long(collectionId));
                    VDCCollection narrowingColl = vdcCollectionService.find(sl.getCollectionId());
                    Iterator iter =  osl.getStudyIds().iterator();
                    while (iter.hasNext()) {
                        Study study =  studyService.getStudy( (Long) iter.next() );
                        if ( VDCCollectionTree.isStudyInCollection( study, narrowingColl, true ) ) {
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
                List studies = StudyUI.filterVisibleStudies( studyService.getRecentStudies(vdc.getId(), -1), vdc, user, getVDCSessionBean().getIpUserGroup(), numResults );
                List studyIds = new ArrayList();
                Iterator iter = studies.iterator();
                while (iter.hasNext()) {
                    Study study = (Study) iter.next();
                    studyIds.add(study.getId());
                }
                sl.setStudyIds(studyIds);
            } else {
                sl.setStudyIds( new ArrayList() );
            }

            setStudyListingIndex(addToStudyListingMap(sl));

        } else {
            // in this case we don't have a mode so we check to see if we
            // have a studyListing passed via the request
            sl = getVDCRequestBean().getStudyListing();
            if (sl != null) {
                setStudyListingIndex(addToStudyListingMap(sl));
            }
        }

        // at this point we should throw an error
        if (sl == null) {
            sl = new StudyListing(StudyListing.GENERIC_ERROR);
        }

        studyListing = sl;
        studyListing.setVdcId( getVDCRequestBean().getCurrentVDCId() );

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

    public Integer getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(Integer searchFilter) {
        this.searchFilter = searchFilter;
    }

    public DataPaginator getPaginator() {
        return paginator;
    }

    public void setPaginator(DataPaginator paginator) {
        this.paginator = paginator;
    }

    public Map getStudyFields() {
        if (studyFields == null) {
            studyFields = new HashMap();
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            if (vdc != null) {
                Iterator iter = vdc.getSearchResultFields().iterator();
                while (iter.hasNext()) {
                    StudyField sf = (StudyField) iter.next();
                    studyFields.put(sf.getName(), sf.getName() );
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
        List searchTerms = new ArrayList();
        SearchTerm st = new SearchTerm();
        st.setFieldName( searchField );
        st.setValue( searchValue );
        searchTerms.add(st);

        List studies = new ArrayList();
        Map variableMap = new HashMap();

        if ( searchField.equals("variable") ) {
            List variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            varService.determineStudiesFromVariables(variables, studies, variableMap);

        } else {
            studies = indexService.search(getVDCRequestBean().getCurrentVDC(), searchTerms);
        }


        StudyListing sl = new StudyListing(StudyListing.SEARCH);
        sl.setStudyIds(studies);
        sl.setSearchTerms(searchTerms);
        sl.setVariableMap(variableMap);

        sl.setVdcId( getVDCRequestBean().getCurrentVDCId() );
        sl.setCollectionTree(collectionTree);

        return sl;
    }

    public int getStudyCount() {
        return studyListing.getStudyIds().size();
    }

    public String getCollectionName() {
        if (studyListing.getCollectionId() != null) {
            return vdcCollectionService.find( studyListing.getCollectionId() ).getName();
            //CollectionUI collUI = new CollectionUI( vdcCollectionService.find( studyListing.getCollectionId() ) );
            //return collUI.getShortCollectionPath(getVDCRequestBean().getCurrentVDC() );
        }

        return null;
    }


}
