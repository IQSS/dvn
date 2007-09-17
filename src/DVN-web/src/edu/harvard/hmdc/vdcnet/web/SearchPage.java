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
 * SearchPageBean.java
 *
 * Created on September 19, 2006, 6:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web;

import com.sun.rave.web.ui.component.Tree;
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
import edu.harvard.hmdc.vdcnet.web.customComponent.scroller.ScrollerComponent;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;

/**
 *
 * @author gdurand
 */
public class SearchPage extends VDCBaseBean{
    @EJB VDCServiceLocal vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB StudyServiceLocal studyService;
    @EJB IndexServiceLocal indexService;
    @EJB VariableServiceLocal varService;
    
    /** Creates a new instance of SearchPageBean */
    public SearchPage() {
    }
    
    // data members
    private StudyListing studyListing;
    private Tree collectionTree;
    private UIData studyTable;
    private ScrollerComponent scroller;
    private String searchField;
    private String searchValue;
    private Integer searchFilter;
    private Map studyFields;
    
    private int studyListingIndex;
    
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
    
    
    public Collection getStudies() {
        List studyUIList = new ArrayList();
 
        if ( studyListing != null && studyListing.getStudyIds() != null ) {
            Iterator iter = studyListing.getStudyIds().iterator();
            while (iter.hasNext()) {
                Long sid = (Long) iter.next();
                StudyUI sui = new StudyUI(sid);
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
    public Tree getCollectionTree() {
        return this.collectionTree;
    }
    
    public void setCollectionTree(Tree collectionTree) {
        this.collectionTree = collectionTree;
    }
    
    public UIData getStudyTable() {
        return studyTable;
    }
    
    public void setStudyTable(UIData studyTable) {
        this.studyTable = studyTable;
    }
    
    public int getStudyListingIndex() {
        return studyListingIndex;
    }
    
    public void setStudyListingIndex(int studyListingIndex) {
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
        
        if (searchFilter == null) { searchFilter = 0;}
        
        
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
        studyListing = new StudyListing(StudyListing.VDC_SEARCH);
        studyListing.setStudyIds(studyIDList);
        studyListing.setSearchTerms(searchTerms);
        studyListing.setVariableMap(variableMap);
        studyListing.setCollectionTree(collectionTree);
        setStudyListingIndex(addToStudyListingMap(studyListing));
        
        // finally reinit!
        initStudies();
        initText(StudyListing.VDC_SEARCH);
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
            List sortedStudies = studyService.getStudies(studyListing.getStudyIds(), sortBy);
            studyListing.setStudyIds(sortedStudies);
            resetScroller();
            
            
        }
    }
    
    public void scroll_action(ActionEvent event) {
        int currentRow = 1;
        
        UIComponent component=event.getComponent();
        Integer currRow = (Integer) component.getAttributes().get("currentRow");
        if (currRow != null) {
            currentRow = currRow.intValue();
        }
        scroll(currentRow);
        
    }
    
    public void scroll(int row) {
        int rows = getStudyTable().getRows();
        if (rows < 1) {
            return;
        }
        if (row <= 0) { //if there is no current row e.g. no search result
            getStudyTable().setFirst(0);
        } else if (row >= getStudyTable().getRowCount() ) { // if the current row is greater than or equal to the total rows returned
            getStudyTable().setFirst( getStudyTable().getRowCount() - 1 );
        } else { //if the current row is less than the total row count, but greater than 0
            getStudyTable().setFirst( row - (row % rows) );
        }
    }
    
    private void resetScroller() {
        scroller.getAttributes().put("currentRow",0);
        scroller.getAttributes().put("currentPage",1);
        scroll(0);
    }
    
    
    public void init() {
        super.init();
        if ( isFromPage("SearchPage") ) { // this is a post, so get the studyListing and let actions handle the rest
            String slIndex = getRequestParam("content:searchPageView:form1:studyListingIndex");
            if (slIndex != null) {
                studyListing =  getStudyListingFromMap(new Integer(slIndex));
                initText(studyListing.getMode());
            }
        } else { // we are coming from this page fresh, so initialize everything
            initNewStudyListing();
            initStudies();
            initText(studyListing.getMode());
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
    
    
    
    private void initText(int mode) {
        int matches = studyListing.getStudyIds() != null ? studyListing.getStudyIds().size() : 0;
        renderSort = matches == 0 ? false : true;
        renderScroller = matches == 0 ? false : true;
        subListHeader = null;
        
        if (mode == StudyListing.VDC_SEARCH) {
            listHeader =  "Results";
            listMessagePrefix = "You searched for " ;
            
            listMessageContent = "";
            if (studyListing.getSearchTerms() != null) {
                Iterator iter = studyListing.getSearchTerms().iterator();
                while (iter.hasNext()) {
                    SearchTerm st = (SearchTerm) iter.next();
                    listMessageContent += st.getFieldName() + " " + st.getOperator() + " \"" + st.getValue() + "\"";
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
            
            // for now show tree, even if no matches.
            //if ( matches > 0 && getVDCRequestBean().getCurrentVDC() != null) {
            if ( getVDCRequestBean().getCurrentVDC() != null) {
                renderTree = true;
                treeHeader = "Narrow Results by Collection";
            } else {
                
                renderTree = false;
            }
            
            renderSearch = true;
            searchRadioItems = new ArrayList();
            searchRadioItems.add(new SelectItem("0", "New Search"));
            searchRadioItems.add(new SelectItem("2", "Search These Results"));
            searchFilter = 0;
            
            
            
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
            
            
            renderTree = true;
            treeHeader = "Browse by Collection";
            
            renderSearch = true;
            searchRadioItems = new ArrayList();
            searchRadioItems.add(new SelectItem("0", "All Collections"));
            searchRadioItems.add(new SelectItem("1", "This Collection"));
            searchFilter = 0;
            
        } else if (mode == StudyListing.VDC_RECENT_STUDIES) {
            listHeader =  "Most Recent";
            renderSearch = true;
            
        } else if (mode == StudyListing.GENERIC_LIST) {
            // this needs to be fleshed out if it's ever used
            listHeader =  "Studies";
            
        } else if (mode == StudyListing.EXPIRED_LIST) {
            listHeader =  "Expired Listing";
            listMessageContent = "The results for this listing have expired.";
            renderSearch = true;
            renderTree = false;
        }
        
        
    }
    
    private void initCollectionTree() {
        if (studyListing.getCollectionTree() == null) {
            studyListing.setCollectionTree(new Tree());
        }
        
        collectionTree = studyListing.getCollectionTree();
        VDCCollectionTree vdcTree = new VDCCollectionTree(collectionTree);
        
        if (studyListing.getMode() == StudyListing.VDC_SEARCH) {
            // performace of filtering the tree is slow, so for now just show entire tree
            //vdcTree.setStudyFilter(studies);
            //vdcTree.setIncludeCount(true);
            vdcTree.setCollectionUrl("/faces/SearchPage.jsp?mode=3&studyListingIndex=" + studyListingIndex);
        } else {
            vdcTree.setCollectionUrl("/faces/SearchPage.jsp?mode=1&studyListingIndex=" + studyListingIndex);
        }
        
        if (studyListing.getCollectionId() != null) {
            vdcTree.setCollectionToBeExpanded( new Long(studyListing.getCollectionId()) );
        }
        
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        vdcTree.populate(vdc);
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
            
        } else if (mode == StudyListing.VDC_SEARCH) {
            String  slIndex = getRequestParam("studyListingIndex");
            String collectionId = getRequestParam("collectionId");
            if (slIndex != null && collectionId != null) {
                sl = getStudyListingFromMap(new Integer(slIndex));
                if (sl.getMode() != studyListing.EXPIRED_LIST) {
                    List oldStudyIds = sl.getStudyIds();
                    List searchTerms = sl.getSearchTerms();
                    Map variableMap = sl.getVariableMap();
                    List newStudyIds = new ArrayList();
                    // so we create a new studyListing
                    sl = new StudyListing(StudyListing.VDC_SEARCH);
                    sl.setSearchTerms(searchTerms);
                    sl.setVariableMap(variableMap);
                    sl.setCollectionId(new Long(collectionId));
                    VDCCollection narrowingColl = vdcCollectionService.find(sl.getCollectionId());
                    Iterator iter = oldStudyIds.iterator();
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
            
        } else if (mode == StudyListing.GENERIC_LIST) {
            // TODO
            
        } else {
            // in this case we don't have a mode so we check to see if we
            // have a studyListing passed via the request
            sl = getVDCRequestBean().getStudyListing();
            if (sl != null) {
                setStudyListingIndex(addToStudyListingMap(sl));
            }
        }
        
        // at this point we should throw an error; for now create empty expired list
        if (sl == null) {
            sl = new StudyListing(StudyListing.EXPIRED_LIST);
        }
        
        studyListing = sl;
        
    }
    
    private int addToStudyListingMap(StudyListing sl) {
        OrderedMap slMap = (OrderedMap) getSessionMap().get("studyListings");
        int newIndex = 0;
        if (slMap == null) {
            slMap = new LinkedMap();
            getSessionMap().put("studyListings", slMap);
            slMap.put( new Integer(0) ,sl);
        } else {
            Integer lastIndex = (Integer) slMap.lastKey();
            newIndex = lastIndex.intValue() + 1;
            slMap.put( new Integer(newIndex), sl);
            if (slMap.size() > 5) {
                slMap.remove(slMap.firstKey());
            }
        }
        return newIndex;
    }
    
    private StudyListing getStudyListingFromMap(Integer slIndex) {
        OrderedMap slMap = (OrderedMap) getSessionMap().get("studyListings");
        if (slMap != null) {
            StudyListing sl = (StudyListing) slMap.get(slIndex);
            if (sl != null) {
                return sl;
            }
        }
        
        // this means that this studyListing or the session has expired
        return new StudyListing(StudyListing.EXPIRED_LIST);
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
    
    public ScrollerComponent getScroller() {
        return scroller;
    }
    
    public void setScroller(ScrollerComponent scroller) {
        this.scroller = scroller;
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
    
    
}
