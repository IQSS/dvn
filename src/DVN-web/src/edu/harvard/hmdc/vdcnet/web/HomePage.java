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
import edu.harvard.hmdc.vdcnet.util.BundleReader;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.ScholarDataverse;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.collection.CollectionUI;
import edu.harvard.hmdc.vdcnet.web.common.LoginBean;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.component.DataListing;
import edu.harvard.hmdc.vdcnet.web.component.VDCCollectionTree;
import edu.harvard.hmdc.vdcnet.web.site.VDCUI;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.ejb.EJB;



/*
 * HomePageBean.java
 *
 * Created on September 19, 2006, 1:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

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
    private Map dataMap = new LinkedHashMap();
    private Map tabsMap = new LinkedHashMap();
    private BundleReader datalistbundle = new BundleReader("DataListBundle");
    
    StatusMessage msg;
    
    public StatusMessage getMsg(){
        return msg;
    }
    
    public void setMsg(StatusMessage msg){
        this.msg = msg;
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
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String tab = request.getParameter("tab");
        if (tab == null) {
            Iterator iterator = request.getParameterMap().keySet().iterator();
            while (iterator.hasNext()) {
                Object key = (Object) iterator.next();
                if ( key instanceof String && ((String) key).indexOf("tab") != -1 && !request.getParameter((String)key).equals("")) {
                    tab = request.getParameter((String)key);
                }
            }
        }
        if (tab != null) {
            setSelectedTab(tab);
        } else {
            setSelectedTab(datalistbundle.getMessageValue("tab1key")); //default value
        }
        request.setAttribute("tab", getSelectedTab());
        initTabsMap();
        initVdcGroupData();
        initScholarDVGroup();
        initVdcsSansGroups();
        initNetworkData();
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
        String truncatedAnnouncements = StringUtil.truncateString(announcements, 1000);
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
    
    public Map getTabsMap() {
        return this.tabsMap;
    }
    
    //setters
    public void setDataMap(TreeMap map) {
        this.dataMap = map;
    }
    
    public void setTabsMap(TreeMap map) {
        this.tabsMap = map;
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
            //check coming soon or is available
            List<VDC> innerlist = vdcgroup.getVdcs();
            Iterator inneriterator = innerlist.iterator();
            while (inneriterator.hasNext()) {
                VDC vdc = (VDC)inneriterator.next();
                if (this.selectedTab.equals(datalistbundle.getMessageValue("tab2key")) && !vdc.isRestricted()) // coming soon
                    inneriterator.remove();
                else if (this.selectedTab.equals(datalistbundle.getMessageValue("tab1key")) && vdc.isRestricted()) // now available
                    inneriterator.remove();  
            }
            List<DataListing> dataList = sortVdcs(innerlist);
            if (!dataList.isEmpty()) {
                dataMap.put(vdcgroup.getName(), dataList);
                vdcGroupCount++;
            } else {
                vdcGroupCount = 0;
            }
        }
    }
    
    private List vdcGroups;
    private static int vdcGroupCount = 0;
    
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
        List newlist = new ArrayList();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Object object = (Object)iterator.next();
            if (object instanceof ScholarDataverse) {
                continue;
            } else {
                VDC vdc = (VDC)object;
                if (this.selectedTab.equals(datalistbundle.getMessageValue("tab2key")) && vdc.isRestricted()) {
                    newlist.add(vdc);
                } else if (this.selectedTab.equals(datalistbundle.getMessageValue("tab1key")) && !vdc.isRestricted()) {
                    newlist.add(vdc);
                }
            }
        }
        setVdcsSansGroups(newlist);
        List<DataListing> dataList = sortVdcs(this.getVdcsSansGroups());
        String heading = (vdcGroupCount > 0) ? datalistbundle.getMessageValue("otherGroupLabel") : "";
        if (!dataList.isEmpty()) 
                dataMap.put(heading, dataList);
     }
   
   private List scholarDvGroup;
    
    public List getScholarDvGroup() {
        return (List)scholarDvGroup;
    }
    
    public void setScholarDvGroup(List groups) {
        this.scholarDvGroup = groups;
    }
    
   public void initScholarDVGroup() {
        // Get all the vdc groups
        List list = (List)vdcService.findVdcsNotInGroups();
        List newlist = new ArrayList();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Object object = (Object)iterator.next();
            ScholarDataverse sdv = null;
            if (object instanceof ScholarDataverse) {
                sdv = (ScholarDataverse)object;
                if (this.selectedTab.equals(datalistbundle.getMessageValue("tab2key")) && sdv.isRestricted()) {
                    newlist.add(sdv);
                } else if (this.selectedTab.equals(datalistbundle.getMessageValue("tab1key")) && !sdv.isRestricted()) {
                    newlist.add(sdv);
                }
            }
        }
        setScholarDvGroup(newlist);
        List<DataListing> dataList = sortVdcs(this.getScholarDvGroup());
        if (!dataList.isEmpty()) 
            dataMap.put(this.datalistbundle.getMessageValue("scholarDvGroupLabel"), dataList);
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
    
    /** TABS */
    
    public void initTabsMap() {
       ArrayList<Tab> tabs = new ArrayList<Tab>();
       int i = 1;
       while (i <= Integer.parseInt(datalistbundle.getMessageValue("numberOfTabs"))) {
          String tabkey  = datalistbundle.getMessageValue("tab" + i + "key");
          String tabname = datalistbundle.getMessageValue("tab" + i + "name");
          String tabnodisplaymsg = datalistbundle.getMessageValue("tab" + i + "nodisplaymsg");
          Integer taborder   = new Integer(datalistbundle.getMessageValue("tab" + i + "order"));
          Tab tab = new Tab(tabkey, tabname, tabnodisplaymsg, taborder);
          tabs.add(tab);
          i++;
       }
       TabSort tabsort = new TabSort();
       synchronized(tabsort){
            Collections.sort(tabs, tabsort.TAB_ORDER);
        }
      
       Iterator iterator = tabs.iterator();
       while (iterator.hasNext()) {
           Tab tab = (Tab)iterator.next();
           tabsMap.put(tab.getKey(), tab);
       }
   }
    
    
    public class Tab implements Comparable {
        
        private String key;
        private String name;
        private String noDisplayMsg;
        private Integer order;
        
        /**
         * Creates a new instance of DataListing
         */
        public Tab() {

        }

        public Tab(String key, String name, String noDisplayMsg, int order) {
            this.key        = key;
            this.name       = name;
            this.noDisplayMsg = noDisplayMsg;
            this.order      = order;
        }

        public String getKey() {
                return this.key;
        }

        public String getName() {
            return this.name;
        }

        public String getNoDisplayMsg() {
                return this.noDisplayMsg;
        }
        
        public Integer getOrder() {
                return this.order;
        }
        
        public String toString() {
        return "[key=" + key + " | name=" + name + " | noDisplayMsg=" + noDisplayMsg + " | order=" + order + "\n\r";
      }
        
        public int compareTo(Object obj) {
            Tab tab = (Tab) obj;
            int keyComparison = key.toUpperCase().compareTo(tab.getKey().toUpperCase());
            return ((keyComparison == 0) ? name.compareTo(tab.getName()) : keyComparison);
          }

          public boolean equals(Object obj) {
            if (!(obj instanceof Tab)) {
              return false;
            }
            Tab tab = (Tab) obj;
            return key.equals(tab.getKey())
                && name.equals(tab.getName());
          }

          public int hashCode() {
            return 31 * key.hashCode() + name.hashCode();
          }
      }
    
    public class TabSort {
        final Comparator<Tab> TAB_ORDER =
                                     new Comparator<Tab>() {
            public int compare(Tab e1, Tab e2) {
                return e1.getOrder().compareTo(e2.getOrder());
            }
        };
    } 
    
    private String selectedTab;
    
    public String getSelectedTab() {
        return this.selectedTab;
    }
    
    public void setSelectedTab(String selected) {
        this.selectedTab = selected;
    }
    
    //network data
    private Long totalDataverses;
    private Long totalStudies;
    private Long totalFiles;
    
    private String networkData = new String();
    
    @EJB VDCNetworkServiceLocal vdcNetworkService;
       /**
     * Holds value of property total.
     */
     private void initNetworkData() {
         boolean isReleased         = true;
         if (this.selectedTab.equals(datalistbundle.getMessageValue("tab2key"))); //comingsoon
             isReleased = false;
         ResourceBundle messages = ResourceBundle.getBundle("Bundle");
         Object[] messageArguments = { this.getTotalDataverses(isReleased), this.getTotalStudies(isReleased), this.getTotalFiles(isReleased) };
         MessageFormat formatter = new MessageFormat("");
         formatter.applyPattern(messages.getString("totals"));
         String output = formatter.format(messageArguments);
         setNetworkData(output);
     }
    
    /**
     * Getters 
     */
    public Long getTotalDataverses(boolean released) {
        return vdcNetworkService.getTotalDataverses(released);
    }
    
    public Long getTotalStudies(boolean released) {
        return vdcNetworkService.getTotalStudies(released);
    }
    
    public Long getTotalFiles(boolean released) {
        return vdcNetworkService.getTotalFiles(released);
    }
    
    public String getNetworkData() {
        return this.networkData;
    }

    /**
     * Setters
     */
    public void setTotalDataverses(Long totalDvs) {
        this.totalDataverses = totalDvs;
    }
    
    public void setTotalStudies(Long totalstudies) {
        this.totalStudies = totalstudies;
    }
    
    public void setTotalFiles(Long totalfiles) {
        this.totalFiles = totalfiles;
    }
    
    public void setNetworkData(String networkdata) {
        this.networkData = networkdata;
    }
    
    
}
