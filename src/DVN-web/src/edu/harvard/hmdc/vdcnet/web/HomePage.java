package edu.harvard.hmdc.vdcnet.web;

import com.sun.rave.web.ui.component.Hyperlink;
import com.sun.rave.web.ui.component.Tree;
import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.RoleRequestServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.index.SearchTerm;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.collection.CollectionUI;
import edu.harvard.hmdc.vdcnet.web.common.LoginBean;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.component.VDCCollectionTree;
import edu.harvard.hmdc.vdcnet.web.site.VDCUI;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.component.UIColumn;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

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
public class HomePage extends VDCBaseBean{
    
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
                vdcTree.setExpandAll(true);
                
                // if only root collection (check links and subcollections), then include studies
                VDCUI vdcUI = new VDCUI(vdc);
                if (vdcUI.getLinkedCollections() == null || vdcUI.getLinkedCollections().size() == 0 ) {
                    CollectionUI rootCollUI = new CollectionUI(vdc.getRootCollection());
                    List subColls = rootCollUI.getSubCollections();
                    if (subColls == null || subColls.size() == 0 ) {
                        // we can use the studyFilter for this currently, since the root collection
                        // is always shown; if at some point we include studies for a tree with subcollections
                        // we will have to revisit this
                        vdcTree.setStudyFilter( StudyUI.filterVisibleStudies(rootCollUI.getStudies(), vdc, user) );
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
                recentStudies = StudyUI.filterVisibleStudies( studyService.getRecentStudies(vdc.getId(), -1), vdc, user, 3 );
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
                truncatedAnnouncements += "<br /><a href=\"/dvn/faces/AnnouncementsPage.jsp?vdcId=" + getVDCRequestBean().getCurrentVDC().getId() + "\" title=\"" + resourceBundle.getString("moreLocalAnnouncementsTip") + "\">more >></a>";
            } else {
                truncatedAnnouncements += "<br /><a href=\"/dvn/faces/AnnouncementsPage.jsp\" title=\"" + resourceBundle.getString("moreNetworkAnnouncementsTip") + "\">more >></a>";
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
    
    private void initVdcGroupData() {
        // Get all the vdc groups
        List list = (List)vdcGroupService.findAll();
        setVdcGroups(list);
    }
    private List vdcGroups;
    
    public List getVdcGroups() {
        return (List)vdcGroups;
    }
    
    public void setVdcGroups(List vdcgroups) {
        this.vdcGroups = vdcgroups;
    }
    
    private HtmlPanelGrid mainDataTable;
    
    public HtmlPanelGrid getMainDataTable() {
        //first iterate through the vdcGroups to get the int to split on
        // and to get the vdcs to display etc !
        List localList = vdcGroups;
        mainDataTable = new HtmlPanelGrid();
        mainDataTable.setColumns(1);
        mainDataTable.setColumnClasses("dvnMainColumn");
        mainDataTable.setStyleClass("dvnMainTable");
        UIColumn mainTableColumn = new UIColumn(); // the main data table column used for appending later.
        Iterator iterator = localList.iterator();
        HtmlPanelGrid childTable = null;
        VDCGroup vdcgroup = null;
        // add the vdc children who are members of a group
        while (iterator.hasNext()) {
            vdcgroup = (VDCGroup)iterator.next();
            //add the heading row.
            HtmlPanelGroup panelGroup = formatHeading(vdcgroup.getName());
            mainTableColumn.getChildren().add(panelGroup);
            childTable = formatChildTable(vdcgroup.getVdcs());
            mainTableColumn.getChildren().add(childTable);
            childTable = null;
        }
        // now add the other vdcs sans groups
        if (!localList.isEmpty() && !this.vdcsSansGroups.isEmpty()) {
           HtmlPanelGroup panelGroup = formatHeading("Other");
           mainTableColumn.getChildren().add(panelGroup); 
           childTable = formatChildTable(this.vdcsSansGroups);
           mainTableColumn.getChildren().add(childTable);
        } else if (localList.isEmpty() && !this.vdcsSansGroups.isEmpty()) {
            childTable = formatChildTable(this.vdcsSansGroups);
            mainTableColumn.getChildren().add(childTable);
        } else {
            HtmlOutputText noOutputText = new HtmlOutputText();
            noOutputText.setEscape(false);
            noOutputText.setValue("<br />");
            mainTableColumn.getChildren().add(noOutputText);
            //TODO: See if this can be eliminated.
        }
        // end get other vdcs
        HtmlPanelGrid panelGrid = new HtmlPanelGrid();
        mainDataTable.getChildren().add(mainTableColumn);
        return this.mainDataTable;
    }
    
    /** 
     * Setter method for the mainDataTable
     *
     */
    
    public void setMainDataTable(HtmlPanelGrid panelgrid) {
        this.mainDataTable = panelgrid;
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
    
    private void initVdcsSansGroups() {
        // Get all the vdc groups
        List list = (List)vdcService.findVdcsNotInGroups();
        setVdcsSansGroups(list);
    }
    
    /** Utility methods
     *
     * @author wbossons
     *
     */
    
    /** formatHeading
     *
     *
     * formats the heading for the 
     * VDCGroup
     */
    private HtmlPanelGroup formatHeading(String vdcName) {
        HtmlPanelGroup panelGroup = new HtmlPanelGroup();
        panelGroup.setStyleClass("dvnMainPanel");
        UIOutput headingText = new UIOutput();
        ValueBinding outervaluebinding = FacesContext.getCurrentInstance().getApplication().createValueBinding(vdcName);
        headingText.setValueBinding("value", outervaluebinding);
        panelGroup.getChildren().add(headingText);
        return panelGroup;
    }
    
    /* formatChildTable
     *
     * formats the nested tables
     * of the Vdc groups
     *
     * @author wbossons
     */
    private HtmlPanelGrid formatChildTable(List vdcs) {
        List membervdcs   = (List)vdcs;
        HtmlPanelGrid childTable = new HtmlPanelGrid();
        Iterator iterator = membervdcs.iterator();
        //Set no. of records per column
        int totalColumns = 3;
        int startNew = setColumnLength(membervdcs.size(), totalColumns);
        int startPos = 0;
        // these next three lines can exist in the method
        childTable = new HtmlPanelGrid(); // start the child table which eventually must be added to the view
        childTable.setStyleClass("dvnChildTable");
        childTable.setColumns(3);
        childTable.setColumnClasses("dvnChildColumn");
        UIColumn column           = null;
        HtmlPanelGroup linkPanel  = null;
        HtmlOutputText startLinkTag = null;
        HtmlOutputText endLinkTag = null;
        Hyperlink nodelink        = null;
        HtmlGraphicImage image    = null;
        HtmlOutputText textTag = null;
        while (iterator.hasNext()) {
            if (startPos == 0) {
                column = new UIColumn();
            }
            VDC vdc  = (VDC)iterator.next();
            startLinkTag = new HtmlOutputText();
            startLinkTag.setEscape(false);
            startLinkTag.setValue("<ul class=dvnGroupListStyle><li>");
            nodelink = new Hyperlink();
            nodelink.setText(vdc.getName());
            nodelink.setToolTip(vdc.getName() + " dataverse");
            nodelink.setUrl("/dv/" + vdc.getAlias() + defaultVdcPath);
            nodelink.setStyle("font-size:normal; text-decoration:underline;");
            endLinkTag = new HtmlOutputText();
            endLinkTag.setEscape(false);
            endLinkTag.setValue("</li></ul>");
            linkPanel = new HtmlPanelGroup();
            linkPanel.getChildren().add(startLinkTag);
            linkPanel.getChildren().add(nodelink);
            if ( vdc.isRestricted() ) {
                //image = new HtmlGraphicImage();
                //image.setUrl("/resources/icon_lock.gif");
                //image.setAlt("Restrcited Dataverse");
                //image.setTitle("Restricted Dataverse");
                //image.setStyleClass("dvnRestricted");
                //linkPanel.getChildren().add(image);
                textTag =  new HtmlOutputText();
                textTag.setEscape(false);
                textTag.setValue("<span class=dvnGroupInProgress>In Progress</span>");
                linkPanel.getChildren().add(textTag);
                nodelink.setToolTip(vdc.getName() + " dataverse (Not yet released)");
            } 
            linkPanel.getChildren().add(endLinkTag);
            column.getChildren().add(linkPanel);
            startPos++;
            if (startPos == startNew || iterator.hasNext() == false) {
                childTable.getChildren().add(column);
                startPos = 0;
            }
        }
        //manage the condition where there were only enough records
        // to build two columns -- to achieve balance in the presentation
        if (childTable.getChildCount() < totalColumns) {
            int remainder = totalColumns - childTable.getChildCount();
            int i = 0;
            HtmlOutputText placeholder = null;
            while (i < remainder) {
                column = new UIColumn();
                placeholder = new HtmlOutputText();
                placeholder.setEscape(false);
                placeholder.setValue("<br />");
                column.getChildren().add(placeholder);
                childTable.getChildren().add(column);
                i++;
            }
        }
        return childTable;
    }
    
    /** setColumnLength();
     *
     * used to set the no of
     * records per column
     *
     * @param numRecords
     * @param numColumns
     * @return int
     *
     * @author wbossons
     *
     */
    private int setColumnLength(int numRecords, int numColumns) {
        int startNew = 0;
        double doubleRecords = ((Integer)numRecords).doubleValue();
        double doubleColumns = ((Integer)numColumns).doubleValue();
        startNew = ((Number)Math.ceil(doubleRecords/doubleColumns)).intValue();
        return startNew;
    }
}
