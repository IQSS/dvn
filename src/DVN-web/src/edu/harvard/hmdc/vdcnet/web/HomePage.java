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
import edu.harvard.hmdc.vdcnet.vdc.VDC;
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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;

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
        
    }
    
    /**
     * Getter for property collectionTree.
     * @return Value of property collectionTree.
     */
    public Tree getCollectionTree() {
        if (collectionTree == null) {
            VDCCollectionTree vdcTree = new VDCCollectionTree();
            vdcTree.setVDCUrl("/faces/HomePage.jsp");
            vdcTree.setCollectionUrl("/faces/SearchPage.jsp?mode=1");
            
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            VDCUser user = getVDCSessionBean().getUser();
            
            if (vdc == null) {
                collectionTree = vdcTree.populate(vdcService.findAll());
            } else {
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
    
}
