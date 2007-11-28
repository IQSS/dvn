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

package edu.harvard.hmdc.vdcnet.web;

import com.sun.rave.web.ui.component.Tree;
import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.RoleRequestServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.index.SearchTerm;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.ScholarDataverse;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.collection.CollectionUI;
import edu.harvard.hmdc.vdcnet.web.common.LoginBean;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.component.DataListing;
import edu.harvard.hmdc.vdcnet.web.component.VDCCollectionTree;
import edu.harvard.hmdc.vdcnet.web.site.VDCUI;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/*
 * HomePageBean.java
 *
 * Created on September 19, 2006, 1:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author gdurand
 */
public class HomePage extends VDCBaseBean {
    
    @EJB VDCServiceLocal vdcService;
    @EJB StudyServiceLocal studyService;
    @EJB VariableServiceLocal varService;
    @EJB IndexServiceLocal indexService;
    @EJB NetworkRoleServiceLocal networkRoleService;
    @EJB RoleRequestServiceLocal roleRequestService;
    
    /** Creates a new instance of HomePageBean */
    public HomePage() {
    }
    
    private Tree collectionTree;
    private String searchField;
    private String searchValue;
    private Map dataMap = new TreeMap();
    
    StatusMessage msg;
    
    public StatusMessage getMsg(){
        return msg;
    }
    
    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }
    
    private String text;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
        
    public void init() {
        super.init();
        msg =  (StatusMessage)getRequestMap().get("statusMessage");
        
        LoginBean loginBean = getVDCSessionBean().getLoginBean();
        
        if (getVDCRequestBean().getCurrentVDC() == null && getVDCRequestBean().getVdcNetwork().isAllowCreateRequest()) {
            if (loginBean==null || (loginBean!=null && !loginBean.isNetworkAdmin() && !loginBean.isNetworkCreator() && networkRoleService.findCreatorRequest(loginBean.getUser().getId())==null )) {
                showRequestCreator=true;
            }
        }
        if ( getVDCRequestBean().getCurrentVDC() != null && getVDCRequestBean().getCurrentVDC().isAllowContributorRequests()) {
            if (loginBean==null || (loginBean!=null && (loginBean.isBasicUser() || loginBean.isPrivilegedViewer()) && roleRequestService.findContributorRequest(loginBean.getUser().getId(), getVDCRequestBean().getCurrentVDCId())==null)) {
                showRequestContributor=true;
            }
        }
        //Add support for VDC Groups on VDC network home page
        initVdcGroupData();
        initVdcsSansGroups();
    }
    
    /**
     * Getter for property collectionTree.
     * @return Value of property collectionTree.
     */
    public Tree getCollectionTree() {
        if (collectionTree == null) {
            VDCCollectionTree vdcTree = new VDCCollectionTree();
            vdcTree.setVDCUrl("");
            vdcTree.setCollectionUrl("/faces/SearchPage.jsp?mode=1");
            
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            VDCUser user = getVDCSessionBean().getUser();
            
            if (vdc == null) {
                collectionTree = vdcTree.populate(vdcService.findAll());
            } else {
                //vdcTree.setExpandAll(true);
                
                // if only root collection (check links and subcollections), then include studies
                VDCUI vdcUI = new VDCUI(vdc);
                if (vdcUI.getLinkedCollections() == null || vdcUI.getLinkedCollections().size() == 0 ) {
                    CollectionUI rootCollUI = new CollectionUI(vdc.getRootCollection());
                    List subColls = rootCollUI.getSubCollections();
                    if (subColls == null || subColls.size() == 0 ) {
                        // we can use the studyFilter for this currently, since the root collection
                        // is always shown; if at some point we include studies for a tree with subcollections
                        // we will have to revisit this
                        vdcTree.setStudyFilter( StudyUI.filterVisibleStudies(rootCollUI.getStudies(), vdc, user, getVDCSessionBean().getIpUserGroup()) );
                        vdcTree.setIncludeStudies(true);
                        vdcTree.setStudyUrl("/faces/study/StudyPage.jsp");
                    }
                }
                
                collectionTree = vdcTree.populate(vdc);
            }
        }
        
        return this.collectionTree;
    }
    
    /**
     * Setter for property collectionTree.
     * @param collectionTree New value of property collectionTree.
     */
    public void setCollectionTree(Tree collectionTree) {
        this.collectionTree = collectionTree;
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
    
    private List recentStudies;
    
    public List getRecentStudies() {
        if (recentStudies == null) {
            recentStudies = new ArrayList();
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            if (vdc != null) {
                VDCUser user = getVDCSessionBean().getUser();
                recentStudies = StudyUI.filterVisibleStudies( studyService.getRecentStudies(vdc.getId(), -1), vdc, user, getVDCSessionBean().getIpUserGroup(), 3 );
            }
        }
        return recentStudies;
    }
    
    
    public List getVdcs() {
        return vdcService.findAll();
    }
    
    
    public String search_action() {
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
        
        
        StudyListing sl = new StudyListing(StudyListing.VDC_SEARCH);
        sl.setStudyIds(studies);
        sl.setSearchTerms(searchTerms);
        sl.setVariableMap(variableMap);
        getVDCRequestBean().setStudyListing(sl);
        
        return "search";
    }
    
    private String parsedLocalAnnouncements = parseAnnouncements((getVDCRequestBean().getCurrentVDC()!= null) ? getVDCRequestBean().getCurrentVDC().getAnnouncements(): "", true);
    
    public String getParsedLocalAnnouncements() {
        return this.parsedLocalAnnouncements;
    }
    
    public void setParsedLocalAnnouncements(String announcements) {
        this.parsedLocalAnnouncements = announcements;
    }
    
    private String parsedNetworkAnnouncements = parseAnnouncements((getVDCRequestBean().getVdcNetwork() != null) ? getVDCRequestBean().getVdcNetwork().getAnnouncements(): "", false);
    
    public String getParsedNetworkAnnouncements() {
        return this.parsedNetworkAnnouncements;
    }
    
    public void setParsedNetworkAnnouncements(String announcements) {
        this.parsedNetworkAnnouncements = announcements;
    }
    
    
    /** public String parseLocalAnnouncements
     *
     * @description This utility method checks a string
     * for a regexp pattern and then parses off the remainder.
     *
     *
     *@return parsed announcements
     *
     */
    public String parseAnnouncements(String announcements, boolean isLocal) {
        String truncatedAnnouncements = StringUtil.truncateString(announcements, 400);
        if ( truncatedAnnouncements != null && !truncatedAnnouncements.equals(announcements) ) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Bundle");
            if (isLocal) {
                truncatedAnnouncements += "<a href=\"/dvn/faces/AnnouncementsPage.jsp?vdcId=" + getVDCRequestBean().getCurrentVDC().getId() + "\" title=\"" + resourceBundle.getString("moreLocalAnnouncementsTip") + "\" class=\"dvn_more\" >more >></a>";
            } else {
                truncatedAnnouncements += "<a href=\"/dvn/faces/AnnouncementsPage.jsp\" title=\"" + resourceBundle.getString("moreNetworkAnnouncementsTip") + "\" class=\"dvn_more\" >more >></a>";
            }
        }
        return truncatedAnnouncements;
    }
    
    /**
     * Holds value of property showRequestCreator.
     */
    private boolean showRequestCreator;
    
    /**
     * Getter for property showRequestCreator.
     * @return Value of property showRequestCreator.
     */
    public boolean isShowRequestCreator() {
        return this.showRequestCreator;
    }
    
    /**
     * Setter for property showRequestCreator.
     * @param showRequestCreator New value of property showRequestCreator.
     */
    public void setShowRequestCreator(boolean showRequestCreator) {
        this.showRequestCreator = showRequestCreator;
    }
    
    /**
     * Holds value of property showRequestContributor.
     */
    private boolean showRequestContributor;
    
    /**
     * Getter for property showRequestContributor.
     * @return Value of property showRequestContributor.
     */
    public boolean isShowRequestContributor() {
        return this.showRequestContributor;
    }
    
    /**
     * Setter for property showRequestContributor.
     * @param showRequestContributor New value of property showRequestContributor.
     */
    public void setShowRequestContributor(boolean showRequestContributor) {
        this.showRequestContributor = showRequestContributor;
    }
    
    /** ************ Add support for VDC Groups on the network home page *************** 
     *
     *
     *
     *
     * @author wbossons
     *
     */
    @EJB VDCGroupServiceLocal vdcGroupService;
     
    private final String defaultVdcPath = "";//TODO: Calculate this from the faces configuration
    //getters
    public Map getDataMap() {
        return this.dataMap;
    }
    
    //setters
    public void setDataMap(TreeMap map) {
        this.dataMap = map;
    }
        
    /** ************ Add support for VDC Groups on the network home page *************** 
     *
     *
     *
     *
     * @author wbossons
     *
     */
     
   
    
    public void initVdcGroupData() {
        // Get all the vdc groups
        List list = (List)vdcGroupService.findAll();
        setVdcGroups(list);
        List localList = vdcGroups;
        Iterator iterator = localList.iterator();
        VDCGroup vdcgroup = null;
        // add the vdc children who are members of a group
        
        while (iterator.hasNext()) {
            vdcgroup = (VDCGroup)iterator.next();
            List<DataListing> dataList = sortVdcs(vdcgroup.getVdcs());
            dataMap.put(vdcgroup.getName(), dataList);
        }
    }
    
    private List vdcGroups;
    
    public List getVdcGroups() {
        return (List)vdcGroups;
    }
    
    public void setVdcGroups(List vdcgroups) {
        this.vdcGroups = vdcgroups;
    }
    
     /**
     * Holds value of property vdcsSansGroups.
     */
    private List<VDC> vdcsSansGroups;

    /**
     * Getter for property vdcsSansGroups.
     * @return Value of property vdcsSansGroups.
     */
    public List<VDC> getVdcsSansGroups() {
        return this.vdcsSansGroups;
    }

    /**
     * Setter for property vdcsSansGroups.
     * @param vdcsSansGroups New value of property vdcsSansGroups.
     */
    public void setVdcsSansGroups(List<VDC> vdcsSansGroups) {
        this.vdcsSansGroups = vdcsSansGroups;
        
    }
    
   public void initVdcsSansGroups() {
        // Get all the vdc groups
        List list = (List)vdcService.findVdcsNotInGroups();
        setVdcsSansGroups(list);
        List localList = vdcsSansGroups;
        Iterator iterator = localList.iterator();
        List<DataListing> dataList = sortVdcs(this.vdcsSansGroups);
        dataMap.put("Other", dataList);
     }
    
    private List sortVdcs(List memberVDCs) {
        List<DataListing> listToSort = new ArrayList<DataListing>();
        List<DataListing> sortedList = new ArrayList<DataListing>();
        DataListing ndvList = null;
        Iterator iterator = memberVDCs.iterator();
        while (iterator.hasNext()) {
            VDC vdc = (VDC)iterator.next();
            String restricted = null;
            if (vdc.isRestricted()) 
                restricted = new String("yes");
            else
                restricted = new String("no");
            String affiliation = null;
            if (vdc.getAffiliation() != null)
                affiliation = new String(vdc.getAffiliation());
            else
                affiliation = new String("");
            if (vdc instanceof ScholarDataverse) {
                ScholarDataverse scholarDV = (ScholarDataverse)vdc;
                String name = new String(scholarDV.getLastName() + ", " + scholarDV.getFirstName());
                String tooltip = new String(scholarDV.getName());
                ndvList = new DataListing(name, scholarDV.getAlias(), affiliation, restricted, tooltip);
            } else {
                ndvList = new DataListing(vdc.getName(), vdc.getAlias(), vdc.getAffiliation(), restricted, vdc.getName());
            }
            listToSort.add(ndvList);
        }
        synchronized(listToSort){
            Collections.sort(listToSort);
        }
        SortedSet set = new TreeSet(listToSort);
        //System.out.println(set); leave this for debugging purposes
        try {
          Iterator setIterator = set.iterator();
          while (setIterator.hasNext()) {
              DataListing ndvListing = (DataListing)setIterator.next();
              sortedList.add(ndvListing);
          }
        } catch (NoSuchElementException e) {
          System.out.println("No elements to printout");
        }
       return sortedList;
    }
 
    public String getDefaultVdcPath() {
        return defaultVdcPath;
    }
    
    // actionlisteners associated with the component
    //action listeners
      public void page_action(ActionEvent event) {
        if (event.getComponent().getClientId(FacesContext.getCurrentInstance()).contains("previous_")) {
            //this.pagePrevious();
        } else {
            //this.pageNext();
        }
        System.out.println("An action was initiated by " + event.getComponent().getClientId(FacesContext.getCurrentInstance()));
        System.out.println("The parent of this component was " + event.getComponent().getParent().getClientId(FacesContext.getCurrentInstance()));
        //pass the id onto the next method to set its content (
      }
      
      public void page_action() {
        System.out.println("An action was initiated by a component...");
      }
}
