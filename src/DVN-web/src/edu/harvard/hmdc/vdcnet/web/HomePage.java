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

import com.icesoft.faces.component.datapaginator.DataPaginator;
import com.icesoft.faces.component.datapaginator.PaginatorActionEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.tree.IceUserObject;
import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.RoleRequestServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.index.SearchTerm;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.util.PagedDataModel;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.*;
import edu.harvard.hmdc.vdcnet.web.common.LoginBean;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
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

    
    private boolean isInit;


    private int classificationsSize  = 0;
    private long totalStudyDownloads = -1;

    public static final String CONTRACT_IMAGE  = "tree_nav_top_close_no_siblings.gif";
    public static final String EXPAND_IMAGE    = "tree_nav_top_open_no_siblings.gif";
    private String ALL_DATAVERSES_LABEL        = "All Dataverses";

    StatusMessage msg;

    private ArrayList itemBeans;
    private ArrayList dvGroupItemBeans;
    private List allVdcGroups   = new ArrayList();

    DataverseGrouping parentItem = null;
    DataverseGrouping childItem  = null;
    
    public HomePage() {
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
        initAllDataverses();
        allVdcGroups = (List)vdcGroupService.findAll();
        initMenu();
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


     /**
      * @description Prepare the itemBeans
      * @param list
      */
     private void initAllDataverses() {
         parentItem = new DataverseGrouping(new Long("0"), ALL_DATAVERSES_LABEL, "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, null);
         parentItem.setSubclassification(new Long("0"));
         long vdcGroupId = 0;
         Integer groupSize = Integer.parseInt((vdcService.getVdcCount(vdcGroupId)).toString());
         List groupList = new ArrayList();
         dataModel = new PagedDataModel(groupList, groupSize, 10);
         parentItem.setDataModel(dataModel);
         parentItem.setDataModelRowCount(groupSize);
     }

     private int currentRow;
     private PagedDataModel dataModel;

     public void setDataModel(PagedDataModel datamodel) {
         this.dataModel = datamodel;
     }

    

      private void initGroupBean(VDCGroup vdcgroup) {
            List innerlist  = vdcgroup.getVdcs();
            Iterator inneriterator = innerlist.iterator();
            Long parent     = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("-1");
            parentItem      = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, parent);
            parentItem.setShortDescription(vdcgroup.getDescription());
            List groupList = new ArrayList();
            dataModel = new PagedDataModel(groupList, innerlist.size(), 10);
            parentItem.setDataModel(dataModel);
            parentItem.setDataModelRowCount(innerlist.size());
     }

      //  pagination
     DataPaginator dataPaginator = new DataPaginator(); //DEBUG test
     public DataPaginator getDataPaginator() {
         return this.dataPaginator;
     }

     public void setDataPaginator(DataPaginator dataPaginator) {
         this.dataPaginator = dataPaginator;
     }

     /** paginate
      * @description the home page pagination is bound to the home page backing
      * bean so that large data sets can be populated dynamically
      * 
      * @param action
      */
      public void paginate(ActionEvent action) {
        PaginatorActionEvent pEvent = (PaginatorActionEvent) action;
        DataverseGrouping grouping = (DataverseGrouping)itemBeans.get(0);
        if (DataPaginator.FACET_FIRST.equals(pEvent.getScrollerfacet())) {
            grouping.setFirstRow(0);
            grouping.setPageAction(true);
            grouping.getDataModel();
         } else if (DataPaginator.FACET_PREVIOUS.equals(pEvent.getScrollerfacet())) {
             grouping.setFirstRow(grouping.getFirstRow() - 10);
             grouping.setPageAction(true);
             grouping.getDataModel();
         }
         else if (DataPaginator.FACET_NEXT.equals(pEvent.getScrollerfacet())) {
             if (grouping.getFirstRow() + 10 < grouping.getDataModelRowCount()) {
                grouping.setFirstRow(grouping.getFirstRow() + 10);
                grouping.setPageAction(true);
                grouping.getDataModel();
             } else {
                FacesContext context = FacesContext.getCurrentInstance();
                context.renderResponse();
             }
         } else if (DataPaginator.FACET_LAST.equals(pEvent.getScrollerfacet())) {
              grouping.setFirstRow(grouping.getDataModelRowCount() - (grouping.getDataModelRowCount() % 10));
              grouping.setPageAction(true);
              grouping.getDataModel();
         } else { // This is a paging event
              System.out.println("The page event is " + pEvent.getPageIndex());
              int page = pEvent.getPageIndex();
              grouping.setFirstRow((page - 1) * 10);
              grouping.setPageAction(true);
              grouping.getDataModel();
         }
     }

      //actions and actionListeners

    public String search_action() {
        List searchTerms    = new ArrayList();
        SearchTerm st       = new SearchTerm();
        st.setFieldName( searchField );
        st.setValue( searchValue );
        searchTerms.add(st);
        List studies        = new ArrayList();
        Map variableMap     = new HashMap();

        if ( searchField.equals("variable") ) {
            List variables  = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            varService.determineStudiesFromVariables(variables, studies, variableMap);

        } else {
            studies         = indexService.search(getVDCRequestBean().getCurrentVDC(), searchTerms);
        }


        StudyListing sl = new StudyListing(StudyListing.SEARCH);
        sl.setStudyIds(studies);
        sl.setSearchTerms(searchTerms);
        sl.setVariableMap(variableMap);
        getVDCRequestBean().setStudyListing(sl);

        return "search";
    }

      // getters and setters

    public int getClassificationsSize() {
        return this.classificationsSize;
    }
       
   public ArrayList getItemBeans() {
       return itemBeans;
    }

   private DataModel pagedDataModel;

   private HtmlDataTable dataverseList = new HtmlDataTable();
   
   public HtmlDataTable getDataverseList() {
       return this.dataverseList;
   }
   
   public void setDataverseList(HtmlDataTable dataverselist) {
       this.dataverseList = dataverselist;
   }


   int currentItemIndex = 0;

      //TREE items
    public ArrayList getDvGroupItemBeans() {
        return dvGroupItemBeans;
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

     // ***************** START TREE *****************
     // tree default model, used as a value for the tree component
    private DefaultTreeModel model;
    private DataverseGroupingObject selectedUserObject;
    private String groupingId;
    DefaultMutableTreeNode rootNode;
     DefaultMutableTreeNode regionNode;
     List descendants = new ArrayList();

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
         Iterator outeriterator = allVdcGroups.iterator();
         VDCGroup vdcgroup = null;
         Long parent;
         List list;
         Iterator iterator;
         String expandImage     = EXPAND_IMAGE;
         String contractImage   = CONTRACT_IMAGE;
         boolean isExpanded     = false;
         while(outeriterator.hasNext()) {
            classificationsSize++;
            vdcgroup = (VDCGroup)outeriterator.next();
            Long id = vdcgroup.getId();
            parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("-1");
            if (parent.equals(new Long("-1"))) {
               parentItem = new DataverseGrouping(id, vdcgroup.getName(), "group", dvGroupItemBeans, isExpanded, expandImage, contractImage, parent);
               populateTopNode(parentItem);
            }
         }
         descendants = allVdcGroups;
         sort(descendants);
         iterator = descendants.iterator();
         while (iterator.hasNext()) {
             VDCGroup group = (VDCGroup)iterator.next();
             Long parentId = group.getParent();
             if (parentId == null) {
                 iterator.remove();
             } else {
                 parentItem = new DataverseGrouping(group.getId(), group.getName(), "subgroup", dvGroupItemBeans, isExpanded, expandImage, contractImage, parentId);
                 populateTopNode(parentItem);
             }

         }
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
            VDCGroup vdcgroup = vdcGroupService.findById(new Long(groupingId));
            initGroupBean(vdcgroup);
        } else {
            initAllDataverses();

        }
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

        // grouping node or branch
        if (title != null) {
            userObject.setText(title);
            userObject.setLeaf(false);
            userObject.setExpanded(false); //false
            node.setAllowsChildren(true);
        }
        // non grouping node or leaf
        else {
            userObject.setText(grouping.getName());
            userObject.setLeaf(true);
            node.setAllowsChildren(false);
        }
        // finally add the node to the parent.
        if (parent != null) {
            parent.add(node);
        }

        return node;
    }

    //Manage classification
     private void populateTopNode(DataverseGrouping topnode) {
         String expandImage     = EXPAND_IMAGE;
         String contractImage   = CONTRACT_IMAGE;
         boolean isExpanded     = false;
         if (topnode.getParentClassification().equals(Long.parseLong("-1"))) {
             regionNode = addNode(rootNode, topnode.getName(), topnode);
         } else {//find the parent node by the id and add topnode to it.
             DefaultMutableTreeNode node = findTreeNode(topnode.getParentClassification().toString());
             if (node != null)
                regionNode = addNode(node, topnode.getName(), topnode);
         }
         //parentItem.setShortDescription(vdcgroup.getDescription());
         //parentItem.setSubclassification(new Long(list.size()));
     }

    
    protected void sort(List alldescendants) {
        Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                VDCGroup c1 = (VDCGroup) o1;
                VDCGroup c2 = (VDCGroup) o2;
                if (c1.getParent() == null) return 1;
                else if (c2.getParent() == null) return -1;
                else return c1.getParent().compareTo(c2.getParent());
            }
        };
        Collections.sort(alldescendants, comparator);
    }

    
    private DefaultMutableTreeNode findTreeNode(String groupingId) {
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

    public DefaultTreeModel getModel() {
        return model;
    }

    public void setModel(DefaultTreeModel model) {
        this.model = model;
    }

    public DataverseGroupingObject getSelectedUserObject() {
        return selectedUserObject;
    }

    public String getGroupingId() {
        return groupingId;
    }

    public void setGroupingId(String groupingId) {
        this.groupingId = groupingId;
    }

     // ******************** END TREE ******************************

        // utility props and methods
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
}