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

/** Source File Name:   DvRecordsManager.java
 *
 * DvRecordsManager is the backing bean that supports
 * the network home page. It is through this class that the
 * DataverseGrouping parents and children are created and
 * passed onto the view.
 *
 *
 */

package edu.harvard.hmdc.vdcnet.web;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.RoleRequestServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.index.SearchTerm;
import edu.harvard.hmdc.vdcnet.study.StudyDownload;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.util.DateUtils;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.*;
import edu.harvard.hmdc.vdcnet.web.common.LoginBean;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;

public class HomePage extends VDCBaseBean implements Serializable {
    @EJB StudyServiceLocal      studyService;
    @EJB VDCGroupServiceLocal   vdcGroupService;
    @EJB VDCServiceLocal        vdcService;
    @EJB VariableServiceLocal   varService;
    @EJB IndexServiceLocal      indexService;
    @EJB NetworkRoleServiceLocal networkRoleService;
    @EJB RoleRequestServiceLocal roleRequestService;
    @EJB UserServiceLocal        userService;

    private ArrayList itemBeans;
    private ArrayList dvGroupItemBeans;
    private boolean isInit;
    private int itemBeansSize = 0;
    public static final String GROUP_INDENT_STYLE_CLASS = "GROUP_INDENT_STYLE_CLASS";
    public static final String GROUP_ROW_STYLE_CLASS    = "groupRow";
    public static final String CHILD_INDENT_STYLE_CLASS = "CHILD_INDENT_STYLE_CLASS";
    public static final String CONTRACT_IMAGE           = "tree_nav_top_close_no_siblings.gif";
    public static final String EXPAND_IMAGE             = "tree_nav_top_open_no_siblings.gif";
    public String CHILD_ROW_STYLE_CLASS;

    //these static variables have a dependency on the Network Stats Server e.g.
    // they should be held as constants in a constants file ... TODO
    private static Long   SCHOLAR_ID = new Long("-1");
    private static String   SCHOLAR_SHORT_DESCRIPTION = new String("A short description for the research scholar group");
    private static Long   OTHER_ID   = new Long("-2");
    private static String   OTHER_SHORT_DESCRIPTION = new String("A short description for the unclassified dataverses group (other).");

    StatusMessage msg;



    public HomePage() {
        //init();
        CHILD_ROW_STYLE_CLASS = "";
    }

     @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        // initialize the list
        if (itemBeans != null) {
            itemBeans.clear();
        } else {
            itemBeans = new ArrayList();
        }
        initChrome();
        List list = (List)vdcGroupService.findAll();
        initGroupBean(list);
        List scholarlist = (List)vdcService.findVdcsNotInGroups("Scholar");
        initUnGroupedBeans(scholarlist, "Scholar Dataverses", SCHOLAR_ID);
        List otherlist = (List)vdcService.findVdcsNotInGroups("Basic");
        initUnGroupedBeans(otherlist, "Other", OTHER_ID);
     }

     DataverseGrouping parentItem = null;
     DataverseGrouping childItem  = null;

     private void initChrome() {
         msg =  (StatusMessage)getRequestMap().get("statusMessage");

        LoginBean loginBean = getVDCSessionBean().getLoginBean();

        if (getVDCRequestBean().getCurrentVDC() == null && getVDCRequestBean().getVdcNetwork().isAllowCreateRequest()) {
            if (loginBean==null ||
                    (loginBean!=null  &&
                      !loginBean.isNetworkAdmin()    &&
                      (!loginBean.isNetworkCreator() || (loginBean.isNetworkCreator() && !userService.hasUserCreatedDataverse(loginBean.getUser().getId()))))) {
                showRequestCreator=true;
            }
        }
        if ( getVDCRequestBean().getCurrentVDC() != null && getVDCRequestBean().getCurrentVDC().isAllowContributorRequests()) {
            if (loginBean==null || (loginBean!=null && loginBean.isBasicUser() )) {
                showRequestContributor=true;
            }
        }

     }


     private void initGroupBean(List list) {
         Iterator iterator = list.iterator();
         VDCGroup vdcgroup = null;
         while(iterator.hasNext()) {
            //add DataListItems to the list
            itemBeansSize++;
            vdcgroup = (VDCGroup)iterator.next();
            Long parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("-1");
            parentItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, parent);
            parentItem.setShortDescription(vdcgroup.getDescription());
            parentItem.setSubclassification(new Long("25"));
            List innerlist = vdcgroup.getVdcs();
            Iterator inneriterator = innerlist.iterator();
            // ArrayList childItems   = new ArrayList();
            while(inneriterator.hasNext()) {
                VDC vdc = (VDC)inneriterator.next();
                //TODO: Make this the timestamp for last update time
                Timestamp lastUpdateTime = (studyService.getLastUpdatedTime(vdc.getId()) != null ? studyService.getLastUpdatedTime(vdc.getId()) : vdc.getReleaseDate());
                Long localActivity       = calculateActivity(vdc);
                String activity          = getActivityClass(localActivity);
                childItem = new DataverseGrouping(vdc.getName(), vdc.getAlias(), vdc.getAffiliation(), vdc.getReleaseDate(), lastUpdateTime, vdc.getDvnDescription(), "dataverse", activity);
                parentItem.addChildItem(childItem);
            }
        }
     }

     private void initUnGroupedBeans(List list, String caption, Long netstatsId) {
        Iterator iterator = list.iterator();
        parentItem = new DataverseGrouping(netstatsId, caption, "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, new Long("-1"));
        parentItem.setShortDescription("Hello Wendy");
        parentItem.setSubclassification(new Long("25"));
        itemBeansSize++;
        while (iterator.hasNext()) {
            VDC vdc = (VDC)iterator.next();
            Timestamp lastUpdateTime = (studyService.getLastUpdatedTime(vdc.getId()) != null ? studyService.getLastUpdatedTime(vdc.getId()) : vdc.getReleaseDate());
            Long localActivity       = calculateActivity(vdc);
            String activity          = getActivityClass(localActivity);
            childItem = new DataverseGrouping(vdc.getName(), vdc.getAlias(), vdc.getAffiliation(), vdc.getReleaseDate(), lastUpdateTime, vdc.getDvnDescription(),  "dataverse", activity);
            parentItem.addChildItem(childItem);
        }
     }

    public void dispose() {
        isInit = false;
        if(itemBeans != null) {
            DataverseGrouping dataversegrouping;
            ArrayList tempList;
            for(int i = 0; i < itemBeans.size(); i++) {
                dataversegrouping = (DataverseGrouping)itemBeans.get(i);
                tempList = dataversegrouping.getChildItems();
                if(tempList != null)
                    tempList.clear();
            }

            itemBeans.clear();
        }
    }

    public ArrayList getItemBeans() {
        return itemBeans;
    }

    public int getItemBeansSize() {
        return itemBeansSize;
    }

    private String getLastUpdatedTime(Long vdcId) {
        Timestamp timestamp = null;
        timestamp = studyService.getLastUpdatedTime(vdcId);
        //TODO: convert this to n (hours, months days) time ago
        String timestampString = DateUtils.getTimeInterval(timestamp.getTime());
        return timestampString;
    }

    private Long calculateActivity(VDC vdc) {
        Integer numberOfDownloads = 0;
        Integer numberOwnedStudies = new Integer(0);
        Long localActivity;
        try {
            Collection collection = vdc.getOwnedStudies();
            numberOwnedStudies = collection.size();
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                StudyDownload studydownload = new StudyDownload();
                numberOfDownloads += studydownload.getNumberOfDownloads();
                iterator.next();
            }
        } catch (Exception e) {
            System.out.println("an exception was thrown while calculating activity");
        } finally {
            if (numberOwnedStudies > 0)
                localActivity = new Long(numberOfDownloads/numberOwnedStudies * 100);
            else
                localActivity = new Long(numberOfDownloads.toString());
            return localActivity;
        }
    }

    private String getActivityClass(Long activity) {
        String activityClass = new String();
        switch (activity.intValue()) {
           case 0: activityClass =  "activitylevelicon al-0"; break;
           case 1: activityClass =  "activitylevelicon al-1"; break;
           case 2: activityClass =  "activitylevelicon al-2"; break;
           case 3: activityClass =  "activitylevelicon al-3"; break;
           case 4: activityClass =  "activitylevelicon al-4"; break;
           case 5: activityClass =  "activitylevelicon al-5"; break;
       }
        return activityClass;
    }

    public StatusMessage getMsg(){
        return msg;
    }

    private String defaultVdcPath;

    public String getDefaultVdcPath() {
        return defaultVdcPath;
    }

    private boolean showRequestCreator;
    /**
     * Getter for property showRequestCreator.
     * @return Value of property showRequestCreator.
     */
    public boolean isShowRequestCreator() {
        return this.showRequestCreator;
    }

    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }

    private String searchField;
    /* SEARCH FIELD RELATED CODE */
    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    private String searchValue;
    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
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


        StudyListing sl = new StudyListing(StudyListing.SEARCH);
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
                truncatedAnnouncements += "<a href=\"/dvn/faces/AnnouncementsPage.xhtml?vdcId=" + getVDCRequestBean().getCurrentVDC().getId() + "\" title=\"" + resourceBundle.getString("moreLocalAnnouncementsTip") + "\" class=\"dvn_more\" >more >></a>";
            } else {
                truncatedAnnouncements += "<a href=\"/dvn/faces/AnnouncementsPage.xhtml\" title=\"" + resourceBundle.getString("moreNetworkAnnouncementsTip") + "\" class=\"dvn_more\" >more >></a>";
            }
        }
        return truncatedAnnouncements;
    }

   /**
     * Setter for property showRequestCreator.
     * @param showRequestCreator New value of property showRequestCreator.
     */
    public void setShowRequestCreator(boolean showRequestCreator) {
        this.showRequestCreator = showRequestCreator;
    }
    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }

    /**
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
    }

    /**
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
    }

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() {
    }

}