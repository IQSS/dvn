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
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputHidden;
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
import java.lang.String;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

public class HomePage extends VDCBaseBean implements Serializable {
    @EJB StudyServiceLocal      studyService;
    @EJB VDCGroupServiceLocal   vdcGroupService;
    @EJB VDCServiceLocal        vdcService;
    @EJB VariableServiceLocal   varService;
    @EJB IndexServiceLocal      indexService;
    @EJB NetworkRoleServiceLocal networkRoleService;
    @EJB RoleRequestServiceLocal roleRequestService;
    @EJB UserServiceLocal        userService;
    @EJB VDCNetworkStatsServiceLocal vdcNetworkStatsService;
    
    

    //Primitives
    private boolean isInit;
    private boolean showRequestCreator;
    private int classificationsSize  = 0;
    int currentItemIndex = 0;
    private int currentRow;
    private long totalStudyDownloads = -1;

    //Classes
    private ArrayList itemBeans;
    private ArrayList dvGroupItemBeans;
    private ArrayList accordionItemBeans;
    private DataModel pagedDataModel;
    DataPaginator dataPaginator     = new DataPaginator();
    DataverseGrouping parentItem    = null;
    DataverseGrouping childItem     = null;
    private DataverseGroupingObject selectedUserObject;
    private HtmlDataTable dataverseList = new HtmlDataTable();
    private HtmlInputHidden hiddenGroupId = new HtmlInputHidden();
    private HtmlInputHidden hiddenAlphaCharacter = new HtmlInputHidden();
    private List allVdcGroups       = new ArrayList();
    List descendants                = new ArrayList();
    private List recentStudies;
    private Long groupId;
    private PagedDataModel dataModel;
    private String ALL_DATAVERSES_LABEL = "All Dataverses";
    private String defaultVdcPath;
    private String groupName;
    private String parsedLocalAnnouncements     = parseAnnouncements((getVDCRequestBean().getCurrentVDC()!= null) ? getVDCRequestBean().getCurrentVDC().getAnnouncements(): "", true);
    private String parsedNetworkAnnouncements   = parseAnnouncements((getVDCRequestBean().getVdcNetwork() != null) ? getVDCRequestBean().getVdcNetwork().getAnnouncements(): "", false);
    private String searchField;
    private String searchValue;
    private boolean showRequestContributor;
    public static final String CONTRACT_IMAGE   = "tree_nav_top_close_no_siblings.gif";
    public static final String EXPAND_IMAGE     = "tree_nav_top_open_no_siblings.gif";
    StatusMessage msg;
    private boolean isAlphaSort;

        
    

    
    
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
        initAccordionMenu();
        initAlphabeticFilter();
        populateVDCUIList(false);
        isAlphaSort = false;
     }

    public boolean isIsAlphaSort() {
        return isAlphaSort;
    }

    public void setIsAlphaSort(boolean isAlphaSort) {
        this.isAlphaSort = isAlphaSort;
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

     //DEBUG -- new way to get at VDCS
     private ArrayList vdcUI;
     private VDCUIList vdcUIList;

     public VDCUIList getVdcUIList() {
         return this.vdcUIList;
     }

    public HtmlInputHidden getHiddenGroupId() {
        return hiddenGroupId;
    }

    public void setHiddenGroupId(HtmlInputHidden hiddenGroupId) {
        this.hiddenGroupId = hiddenGroupId;
    }

    public HtmlInputHidden getHiddenAlphaCharacter() {
        return hiddenAlphaCharacter;
    }

    public void setHiddenAlphaCharacter(HtmlInputHidden hiddenAlphaCharacter) {
        this.hiddenAlphaCharacter = hiddenAlphaCharacter;
    }

    private HtmlInputHidden hiddenFilterType = new HtmlInputHidden();

    public HtmlInputHidden getHiddenFilterType() {
        return hiddenFilterType;
    }

    public void setHiddenFilterType(HtmlInputHidden hiddenFilterType) {
        this.hiddenFilterType = hiddenFilterType;
        if (hiddenFilterType.getValue() != null && hiddenFilterType.getValue().toString().equals("alphabetic")) {
            isAlphaSort = true;
        } else {
            isAlphaSort = false;
        }
    }

    public void changeGroupId(ValueChangeEvent event) {
        String changedValue = event.getNewValue().toString();
        if (!changedValue.equals("")) {
            Long newValue = new Long(changedValue);
            Long oldValue = null;
            if (event.getOldValue() != null && !event.getOldValue().equals("")) {
                oldValue = new Long(((Object)event.getOldValue()).toString());
            }
            if ( !newValue.toString().isEmpty()) {
                hiddenGroupId.setValue(newValue);
                groupId = newValue;
                if (oldValue != null && oldValue.equals(newValue) && hiddenFilterType.getValue().equals("alphabetic") && !hiddenAlphaCharacter.getValue().equals("All"))
                    populateVDCUIList(true);
                else
                    populateVDCUIList(false);
            }
        }
    }

    public void changeAlphaCharacter(ValueChangeEvent event) {
        String newValue = (String)event.getNewValue();
        String oldValue = (String)event.getOldValue();
        if (!newValue.isEmpty()) {
            if (newValue.equals("All")) {
                populateVDCUIList(false);
            } else {
                hiddenAlphaCharacter.setValue(newValue);
                populateVDCUIList(true);

            }
        }
    }

    public void changeFilterType(ValueChangeEvent event) {
        String newValue = (String)event.getNewValue();
        if (newValue != null && newValue.equals("alphabetic")) {
            isAlphaSort = true;
        } else {
            isAlphaSort = false;
        }
    }

    private Long vdcUIListSize;
    private VDCGroup group;

    private void populateVDCUIList(boolean isAlphaSort) {
        boolean isNewGroup = false;
        if ( hiddenGroupId.getValue() == null || (vdcUIList != null &&
                ( (vdcUIList.getVdcGroupId() != null &&
                        !vdcUIList.getVdcGroupId().equals(groupId)) ||
                                    vdcUIList.getVdcGroupId() == null)) ) {
            isNewGroup = true;
        }
        if (groupId == null || hiddenGroupId.getValue() == null || hiddenGroupId.getValue().equals("") || hiddenGroupId.getValue().toString().equals("-1")) {
            groupId = new Long("-1");
        }
        if (!isAlphaSort) {
            if (isNewGroup || vdcUIList != null && vdcUIList.getAlphaCharacter() != null && !vdcUIList.getAlphaCharacter().equals("")) {
                vdcUIList = new VDCUIList(groupId);
            } else {
                vdcUIList.setAlphaCharacter("");
                vdcUIList.getVdcUIList();
                vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
            }
        } else {
            if (isNewGroup && groupId != null) {
                vdcUIList = new VDCUIList(groupId, (String)hiddenAlphaCharacter.getValue());
            } else if (!((String)hiddenAlphaCharacter.getValue()).equals(vdcUIList.getAlphaCharacter())) {
                vdcUIList.setAlphaCharacter((String)hiddenAlphaCharacter.getValue());
                vdcUIList.oldSort = "";
            }
        } 
        if (groupId == null || groupId.equals(new Long("-1")) ) {
            setGroupName("All Dataverses");
        } else {
            group = vdcGroupService.findById(groupId);
            setGroupName(group.getName());
        }
        vdcUIList.getVdcUIList();
        vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
    }

    
    private ArrayList alphaCharacterList;
    private void initAlphabeticFilter() {
        if (alphaCharacterList == null) {
            alphaCharacterList = new ArrayList();
            for ( char ch = 'A';  ch <= 'Z';  ch++ ) {
              alphaCharacterList.add(String.valueOf(ch));
            }
        }
    }

    public ArrayList getAlphaCharacterList() {
        return this.alphaCharacterList;
    }

    public void setAlphaCharacterList(ArrayList list) {
        this.alphaCharacterList = list;
    }

    public Long getVdcUIListSize() {
        return vdcUIListSize;
    }

    public void setVdcUIListSize(Long vdcUIListSize) {
        this.vdcUIListSize = vdcUIListSize;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public VDCGroup getGroup() {
        return group;
    }

    public void setGroup(VDCGroup group) {
        this.group = group;
    }

    

    
    

     // END DEBUG




     /**
      * @description Prepare the itemBeans
      * @param list
      */
     private void initAllDataverses() {
         parentItem = new DataverseGrouping(new Long("0"), ALL_DATAVERSES_LABEL, "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, null);
         parentItem.setSubclassification(new Long("0"));
         long vdcGroupId = 0;
         Integer groupSize = Integer.parseInt((vdcService.getUnrestrictedVdcCount(vdcGroupId)).toString());
         List groupList = new ArrayList();
         dataModel = new PagedDataModel(groupList, groupSize, 10);
         parentItem.setDataModel(dataModel);
         parentItem.setDataModelRowCount(groupSize);
     }

      private void initGroupBean(VDCGroup vdcgroup) {
            Long vdcGroupId = vdcgroup.getId();
            Integer groupSize = Integer.parseInt((vdcService.getUnrestrictedVdcCount(vdcGroupId)).toString());
            Long parent     = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("-1");
            parentItem      = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, parent);
            parentItem.setShortDescription(vdcgroup.getDescription());
            List groupList = new ArrayList();
            dataModel = new PagedDataModel(groupList, groupSize, 10);
            parentItem.setDataModel(dataModel);
            parentItem.setDataModelRowCount(groupSize);
     }

      protected void initAccordionMenu() {
        if (accordionItemBeans != null) {
            accordionItemBeans.clear();
        } else {
            accordionItemBeans = new ArrayList();
        }

        List list = (List)vdcGroupService.findAll();
        //itemBeansSize = list.size();
        Iterator outeriterator = list.iterator();
        while(outeriterator.hasNext()) {
            classificationsSize++;
            VDCGroup vdcgroup = (VDCGroup)outeriterator.next();
                String indentStyle = (vdcgroup.getParent() == null) ? "groupRowIndentStyle" : "childRowIndentStyle";
                if (vdcgroup.getParent() == null) {
                    populateTopNode(vdcgroup, indentStyle);
                    // get all of the vdcs that belong to this group and add them to the parent
                    populateDescendants(vdcgroup, true);
                }
        }
        writeJavascript();
    }

      //Manage classification
     protected void populateTopNode(VDCGroup vdcgroup, String indentStyle) {
         String expandImage     = EXPAND_IMAGE;
         String contractImage   = CONTRACT_IMAGE;
         boolean isExpanded     = false;
         synchronized(accordionItemBeans) {
            parentItem  = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", accordionItemBeans, isExpanded, expandImage, contractImage, new Long("-1"));
         }
         parentItem.setShortDescription(vdcgroup.getDescription());
         //parentItem.setSubclassification(new Long(list.size()));
         parentItem.setTextIndent(0);

         if (!indentStyle.equals(""))
             parentItem.setIndentStyleClass(indentStyle);
         //if (sortColumnName == null) {
             //initColumnNames(parentItem);
         //}
     }

      protected void populateDescendants(VDCGroup vdcgroup, boolean isExpanded) {
         Long parentId        = vdcgroup.getId();
         List list          = vdcGroupService.findByParentId(parentId);
         Iterator iterator  = list.iterator();
         DataverseGrouping childItem;
         while (iterator.hasNext()) {
            VDCGroup group = (VDCGroup)iterator.next();
            childItem = new DataverseGrouping(group.getId(), group.getName(), "subgroup", isExpanded, "", "", parentId);
            parentItem.addChildItem(childItem);
            //this next piece won't work. I'll have to add a listener to the class to show the children -- see ManageClassifications for this
            if (vdcGroupService.findByParentId(group.getId()) != null) {
                //populateDescendants(group, false);
                List innerlist       = vdcGroupService.findByParentId(group.getId());
                Iterator inneriterator  = innerlist.iterator();
                DataverseGrouping xtraItem;
                childItem.xtraItems = new ArrayList();
                this.childItemBeans.add(childItem.getId());
                while (inneriterator.hasNext()) {
                    VDCGroup innergroup = (VDCGroup)inneriterator.next();
                    xtraItem = new DataverseGrouping(innergroup.getId(), innergroup.getName(), "subgroup", isExpanded, "", "", parentId);
                    childItem.addXtraItem(xtraItem);
                }
            }
         }
      }
      
      private void writeJavascript() {
          accordionJavascript +=  "<script type=\"text/javascript\">"  + "\n\r" +
                "// <![CDATA[ " +  "\n\r" +
                "jQuery(document).ready(function(){" + "\n\r" +
                      "jQuery('#theMenu').Accordion({ " +
                        "active: 'h3.selected', " +
                        "header: 'h3.head', " +
                        "alwaysOpen: false, " +
                        "animated: true, " +
                        "showSpeed: 400, " +
                        "hideSpeed: 800 " +
                "});" + "\n\r" +
                "jQuery('.xtraMenu').Accordion({" +
                "active: 'h4.selected'," +
                "header: 'h4.head', " +
                "alwaysOpen: false, " +
                "animated: true, " +
                "showSpeed: 400, " +
                "hideSpeed: 800 " +
                "});" + "\n\r";
        Iterator iterator = childItemBeans.iterator();
        while (iterator.hasNext()) {
            String grouping = (String)iterator.next();
            String divId = "xtraMenu" + grouping;
                accordionJavascript += "jQuery('#" + divId + "').Accordion({" + 
                "active: 'h4.selected'," +
                "header: 'h4.head', " +
                "alwaysOpen: false, " +
                "animated: true, " +
                "showSpeed: 400, " +
                "hideSpeed: 800 " +
                "});" + "\n\r";
        }
        accordionJavascript += "  });"  + "\n\r" +
              "// ]] >"  + "\n\r" + "</" + "script>";
      }

      private ArrayList childItemBeans = new ArrayList();
      private String accordionJavascript = new String("");

    public String getAccordionJavascript() {
        return accordionJavascript;
    }

    public void setAccordionJavascript(String accordionJavascript) {
        this.accordionJavascript = accordionJavascript;
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
        grouping.isPopulated = false;
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

  //getters



  //Accordion items
    public ArrayList getAccordionItemBeans() {
        return accordionItemBeans;
    }

    public int getClassificationsSize() {
        return this.classificationsSize;
    }

    public DataPaginator getDataPaginator() {
         return this.dataPaginator;
     }

     public HtmlDataTable getDataverseList() {
       return this.dataverseList;
   }

     public String getDefaultVdcPath() {
        return defaultVdcPath;
    }

      public ArrayList getDvGroupItemBeans() {
        return dvGroupItemBeans;
    }

    public Long getGroupId() {
        return groupId;
    }

    public ArrayList getItemBeans() {
       return itemBeans;
    }

    public StatusMessage getMsg(){
        return msg;
    }

     public String getParsedLocalAnnouncements() {
        return this.parsedLocalAnnouncements;
    }

     public String getParsedNetworkAnnouncements() {
        return this.parsedNetworkAnnouncements;
    }

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

     public String getSearchField() {
        return searchField;
    }

     public String getSearchValue() {
        return searchValue;
    }

    public boolean isShowRequestContributor() {
        return this.showRequestContributor;
    }

    public boolean isShowRequestCreator() {
        return this.showRequestCreator;
    }

   //setters
   
    public DataverseGroupingObject getSelectedUserObject() {
        return selectedUserObject;
    }

    public void setDataModel(PagedDataModel datamodel) {
         this.dataModel = datamodel;
     }

     public void setDataPaginator(DataPaginator dataPaginator) {
         this.dataPaginator = dataPaginator;
     }

     public void setDataverseList(HtmlDataTable dataverselist) {
       this.dataverseList = dataverselist;
   }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }

    public void setParsedLocalAnnouncements(String announcements) {
        this.parsedLocalAnnouncements = announcements;
    }

    public void setParsedNetworkAnnouncements(String announcements) {
        this.parsedNetworkAnnouncements = announcements;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }


   /**
     * Setter for property showRequestCreator.
     * @param showRequestCreator New value of property showRequestCreator.
     */
    public void setShowRequestCreator(boolean showRequestCreator) {
        this.showRequestCreator = showRequestCreator;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
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
    

    public String getStudyCount() {
        Long count = vdcNetworkStatsService.getVDCNetworkStats().getStudyCount();
        return NumberFormat.getIntegerInstance().format(count);
    }

      public String getFileCount() {
        Long count = vdcNetworkStatsService.getVDCNetworkStats().getFileCount();
        return NumberFormat.getIntegerInstance().format(count);
    }
}