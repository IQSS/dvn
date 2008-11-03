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

import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.tree.IceUserObject;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

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
    private String ALL_DATAVERSES_LABEL = "All Dataverses";

    //these static variables have a dependency on the Network Stats Server e.g.
    // they should be held as constants in a constants file ... TODO
    private static Long   OTHER_ID   = new Long("-2");
    private static String OTHER_SHORT_DESCRIPTION = new String("A short description for the unclassified dataverses group (other).");

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
        List otherlist = (List)vdcService.findVdcsNotInGroups("Basic");
        otherlist.addAll(scholarlist);
        initUnGroupedBeans(otherlist, "Other", OTHER_ID);
        initMenu();
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
        parentItem.setShortDescription(""); //TODO add short description to the vdc create/edit pages.
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

     private void initAllDataverses(List list) {
         parentItem = new DataverseGrouping(new Long("0"), ALL_DATAVERSES_LABEL, "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, null);
         parentItem.setSubclassification(new Long("0"));
         Iterator iterator = list.iterator();
         VDC vdc = null;
         while(iterator.hasNext()) {
            //add DataListItems to the list
            itemBeansSize++;
            vdc = (VDC)iterator.next();
            Long parent = new Long("0");
            Timestamp lastUpdateTime = (studyService.getLastUpdatedTime(vdc.getId()) != null ? studyService.getLastUpdatedTime(vdc.getId()) : vdc.getReleaseDate());
            Long localActivity       = calculateActivity(vdc);
            String activity          = getActivityClass(localActivity);
            childItem = new DataverseGrouping(vdc.getName(), vdc.getAlias(), vdc.getAffiliation(), vdc.getReleaseDate(), lastUpdateTime, vdc.getDvnDescription(), "dataverse", activity);
            parentItem.addChildItem(childItem);
         }
     }


     // ***************** DEBUG START TREE *****************
     // tree default model, used as a value for the tree component
    private DefaultTreeModel model;
    private DataverseGroupingObject selectedUserObject;

    /**
     * Construct the default tree structure by combining tree nodes.
     */

    // REMOVE THIS IN FAVOR OF THIS CLASS'S CONSTRUCTOR
    //public TreeController() {
       // init();
    //}

    public DefaultTreeModel getModel() {
        return model;
    }

    public void setModel(DefaultTreeModel model) {
        this.model = model;
    }

    public DataverseGroupingObject getSelectedUserObject() {
        return selectedUserObject;
    }

    private String groupingId;

    public String getGroupingId() {
        return groupingId;
    }

    public void setGroupingId(String groupingId) {
        this.groupingId = groupingId;
    }

    public void groupingNodeSelected(ActionEvent event) {
        // get grouping id
        setGroupingId((String)linkSelect.getAttributes().get("groupingId"));

        // find grouping node by id and make it the selected node
        DefaultMutableTreeNode node = findTreeNode(groupingId);
        selectedUserObject = (DataverseGroupingObject) node.getUserObject();
        // TODO: change the content in the window
        itemBeans.clear();
        List list = null;
        if (!groupingId.equals("0")) {
            list = (List)vdcGroupService.findByParentId(new Long(groupingId));
            initGroupBean(list);
        } else {
            list = (List)vdcService.findAll();
            initAllDataverses(list);

        }
        // initMenu();
        // fire effects.);
        //valueChangeEffect.setFired(false);
    }

    HtmlCommandLink linkSelect = new HtmlCommandLink();

    public HtmlCommandLink getLinkSelect() {
        return linkSelect;
    }

    public void setLinkSelect(HtmlCommandLink linkSelect) {
        this.linkSelect = linkSelect;
    }


    public ArrayList getSelectedTreePath() {
        Object[] objectPath = selectedUserObject.getWrapper().getUserObjectPath();
        ArrayList treePath = new ArrayList();
        Object anObjectPath;
        for(int i= 0, max = objectPath.length; i < max; i++){
            anObjectPath = objectPath[i];
            IceUserObject userObject = (IceUserObject) anObjectPath;
            treePath.add(userObject.getText());
        }
        return treePath;
    }

    public boolean isMoveUpDisabled() {
        DefaultMutableTreeNode selectedNode = selectedUserObject.getWrapper();
        return isMoveDisabled(selectedNode, selectedNode.getPreviousNode());
    }

    public boolean isMoveDownDisabled() {
        DefaultMutableTreeNode selectedNode = selectedUserObject.getWrapper();
        return isMoveDisabled(selectedNode, selectedNode.getNextNode());
    }

    public boolean isMoveDisabled(DefaultMutableTreeNode selected, DefaultMutableTreeNode swapper) {
        return selected == null || swapper == null || selected.getAllowsChildren() || swapper.isRoot();
    }

    public void moveUp(ActionEvent event) {
        DefaultMutableTreeNode selectedNode = selectedUserObject.getWrapper();
        exchangeNodes(selectedNode.getPreviousNode(), selectedNode);
    }

    public void moveDown(ActionEvent event) {
        DefaultMutableTreeNode selectedNode = selectedUserObject.getWrapper();
        exchangeNodes(selectedNode, selectedNode.getNextNode());
    }

    public void exchangeNodes(DefaultMutableTreeNode node1, DefaultMutableTreeNode node2) {
        DefaultMutableTreeNode node1Parent = (DefaultMutableTreeNode) node1.getParent();
        DefaultMutableTreeNode node2Parent = (DefaultMutableTreeNode) node2.getParent();
        DefaultMutableTreeNode node1PrevNode = node1.getPreviousNode();
        DefaultMutableTreeNode node1PrevNodeParent = (DefaultMutableTreeNode) node1PrevNode.getParent();
        int childCount = 0;

        if (node1.isNodeDescendant(node2)) {
            while (node2.getChildCount() > 0) {
                node1.insert((MutableTreeNode) node2.getFirstChild(), childCount++);
            }
            if (node1PrevNode == node1Parent ||
                    (node1PrevNode.isNodeSibling(node1) && !node1PrevNode.getAllowsChildren())) {
                node1Parent.insert(node2, node1Parent.getIndex(node1));
            } else if (node1PrevNode.getAllowsChildren()) {
                node1PrevNode.add(node2);
            } else {
                node1PrevNodeParent.add(node2);
            }

            return;
        }

        if (node2.getAllowsChildren()) {
            node2.insert(node1, 0);
        } else {
            node1.removeFromParent();
            node2Parent.insert(node1, node2Parent.getIndex(node2) + 1);
        }
    }

    public void dropListener(DropEvent event) {
        HtmlPanelGroup panelGroup = (HtmlPanelGroup) event.getComponent();

        DefaultMutableTreeNode dragNode = (DefaultMutableTreeNode) event.getTargetDragValue();
        DefaultMutableTreeNode dropNode = (DefaultMutableTreeNode) panelGroup.getDropValue();
        DefaultMutableTreeNode dropNodeParent = (DefaultMutableTreeNode) dropNode.getParent();

        if (dragNode.isNodeDescendant(dropNode)) return;

        if (dropNode.getAllowsChildren()) {
            dropNode.insert(dragNode, 0);
        } else {
            dragNode.removeFromParent();
            dropNodeParent.insert(dragNode, dropNodeParent.getIndex(dropNode) + 1);
        }
    }

    private DefaultMutableTreeNode addNode(DefaultMutableTreeNode parent,
                                           String title,
                                           DataverseGrouping grouping) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        DataverseGroupingObject userObject = new DataverseGroupingObject(node);
        node.setUserObject(userObject);
        userObject.setGrouping(grouping);

        // non-grouping node or branch
        if (title != null) {
            userObject.setText(title);
            userObject.setLeaf(false);
            userObject.setExpanded(false);
            node.setAllowsChildren(true);
        }
        // grouping node
        else {
            userObject.setText(grouping.getName());
            userObject.setLeaf(true);//TODO: change to false because only groups are being created
            node.setAllowsChildren(false);
        }
        // finally add the node to the parent.
        if (parent != null) {
            parent.add(node);
        }

        return node;
    }

    //Manage classification
     private void populateParentClassification(VDCGroup vdcgroup, String indentStyle) {
         Long parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("-1");
         //System.out.println("dv records manager: parent in group is " + vdcgroup.getParent());
         List list = vdcGroupService.findByParentId(vdcgroup.getId());
         Iterator iterator = list.iterator();
         String expandImage = null;
         String contractImage = null;
         boolean isExpanded   = false;
         // if (iterator.hasNext()) {
            // expandImage   = EXPAND_IMAGE;
            // contractImage = CONTRACT_IMAGE;
            // isExpanded    = true;
         // }
         synchronized(dvGroupItemBeans) {
            parentItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", dvGroupItemBeans, isExpanded, expandImage, contractImage, parent);
            // DEBUG
             regionNode = addNode(rootNode, parentItem.getName(), parentItem);
             //END DEBUG
         }
         parentItem.setShortDescription(vdcgroup.getDescription());
         parentItem.setSubclassification(new Long(list.size()));
         if (!indentStyle.equals(""))
             parentItem.setIndentStyleClass(indentStyle);
         List innerlist = vdcGroupService.findByParentId(vdcgroup.getId());//get the children
         Iterator inneriterator = innerlist.iterator();
         System.out.println("populateParent: " + vdcgroup.getName());
         removeFromList.add(vdcgroup);
         while(inneriterator.hasNext()) {
            VDCGroup subgroup = (VDCGroup)inneriterator.next();
            parent = vdcgroup.getParent();
            populateSubClassification(subgroup, parentItem);
                //remove the subgroup from the iterator
            removeFromList.add(subgroup);
         }
     }

     private void populateSubClassification(VDCGroup vdcgroup, DataverseGrouping parentitem) {

         List list = vdcGroupService.findByParentId(vdcgroup.getId());//get the children
         Iterator iterator = list.iterator();
         String expandImage = null;
         String contractImage = null;
         boolean isExpanded   = false;
         // if (!list.isEmpty()) {
             // expandImage    = EXPAND_IMAGE;
             // contractImage  = CONTRACT_IMAGE;
             // isExpanded     = true;
         // }
         childItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "subgroup", isExpanded, expandImage, contractImage, new Long(parentitem.getId()));
         childItem.setShortDescription(vdcgroup.getDescription());
         childItem.setIndentStyleClass("childRowIndentStyle");
         parentitem.addChildItem(childItem);
         addNode(regionNode, childItem.getName(), childItem);
         //parentitem.toggleSubGroupAction();
         System.out.println("Created a child whose name is " + vdcgroup.getName());
         removeFromList.add(vdcgroup);
        while (iterator.hasNext() && !removeFromList.contains(vdcgroup)) {
             VDCGroup subgroup = (VDCGroup)iterator.next();
             populateSubClassification(subgroup, childItem);

         }

     }

     DefaultMutableTreeNode rootNode;
     DefaultMutableTreeNode regionNode;
     List removeFromList = new ArrayList();

    protected void initMenu() {
        if (dvGroupItemBeans != null) {
            dvGroupItemBeans.clear();
        } else {
            dvGroupItemBeans = new ArrayList();
        }
       
        // Top Level
        rootNode = addNode(null, "All Dataverses", new DataverseGrouping(new Long("0"), "", ""));
        model = new DefaultTreeModel(rootNode);
        selectedUserObject = (DataverseGroupingObject) rootNode.getUserObject();
        selectedUserObject.setExpanded(true);
        //END Top
            List list = (List)vdcGroupService.findAll();
            Iterator outeriterator = list.iterator();
             VDCGroup vdcgroup = null;
             System.out.println(list.toString());
             while(outeriterator.hasNext()) {
                vdcgroup = (VDCGroup)outeriterator.next();
                if (removeFromList.contains(vdcgroup)) {
                    continue;
                } else {
                String indentStyle = (vdcgroup.getParent() == null) ? "" : "childRowIndentStyle";
                populateParentClassification(vdcgroup, indentStyle);
                }
             }
    }

    private DefaultMutableTreeNode findTreeNode(String nodeId) {
        DefaultMutableTreeNode theRootNode =
                (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode node;
        DataverseGroupingObject tmp;
        Enumeration nodes = theRootNode.depthFirstEnumeration();

        while (nodes.hasMoreElements()) {
            node = (DefaultMutableTreeNode) nodes.nextElement();
            tmp = (DataverseGroupingObject) node.getUserObject();
            if (groupingId.equals(String.valueOf(tmp.getGrouping().getId()))) {
                return node;
            }
        }
        return null;
    }
     // ******************** END DEBUG TREE ******************************

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
    
    //tree map related
    public ArrayList getDvGroupItemBeans() {
        return dvGroupItemBeans;
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