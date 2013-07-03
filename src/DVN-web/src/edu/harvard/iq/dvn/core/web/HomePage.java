/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
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
package edu.harvard.iq.dvn.core.web;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputHidden;
import com.icesoft.faces.component.datapaginator.DataPaginator;
import edu.harvard.iq.dvn.core.admin.*;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.*;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCApplicationBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.site.VDCUI;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import java.io.Serializable;
import java.lang.String;
import java.text.NumberFormat;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@ViewScoped
@Named
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
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB RoleServiceLocal roleService;
    
    

    //Primitives
    private int classificationsSize  = 0;

    

    //Objects
    private ArrayList accordionItemBeans;
    private HtmlDataTable dataverseList = new HtmlDataTable();
    private HtmlInputHidden hiddenGroupId = new HtmlInputHidden();
    private HtmlInputHidden hiddenAlphaCharacter = new HtmlInputHidden();
    List descendants                = new ArrayList();
    private List recentStudies;
    private List mostDownloadedStudies;
    private Long groupId;
    private String defaultVdcPath;
    private String groupName;
    private String parsedLocalAnnouncements     = null;
    private String parsedNetworkAnnouncements   = null;
    private List<VDCNetworkUI> vdcSubnetworks = null;
    
    private String searchField;
    StatusMessage msg;
    private VDCUser pageUser = null;
    private Long userVDCCount = null;
    private String soleVDCAlias = null;
    private String subnetworkAlias = null;
    private Long vdcNetworkId = new Long(0);
    private boolean hideRestricted = true; //show only restricted if set to false wjb

    
    public HomePage() {
    }

     @SuppressWarnings("unchecked")
    public void init() {
        super.init();

        if (getVDCRequestBean().getCurrentVdcNetwork() != null ){
            vdcNetworkId = getVDCRequestBean().getCurrentVdcNetwork().getId();
        }
        initChrome();
        initAccordionMenu();
        initSubnetworks();
        populateVDCUIList(false);        
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
    
    
    private void initSubnetworks(){        
        vdcSubnetworks = new ArrayList();     
        List <VDCNetwork> vdcSubnetworkList = vdcNetworkService.getVDCSubNetworks();
        for (VDCNetwork vdcNetwork: vdcSubnetworkList){
            VDCNetworkUI vdcNetworkUI = new VDCNetworkUI();
            vdcNetworkUI.setVdcNetwork(vdcNetwork);
            Collection <VDC> vdcList = vdcNetwork.getNetworkVDCs();
            int countRel = 0;
            for (VDC vdc : vdcList){
                if (!vdc.isRestricted()){
                    countRel++;
                }
                
            }           
            vdcNetworkUI.setVdcCount(new Long(countRel));
            if (countRel > 0)  {
                vdcSubnetworks.add(vdcNetworkUI);
            }       
        }
    }

    private void initChrome() {
        msg = (StatusMessage) getRequestMap().get("statusMessage");
    }
    //DEBUG -- new way to get at VDCS
    private ArrayList vdcUI;
    private VDCUIList vdcUIList;
    private VDCUIList vdcUIListDownloaded;
    private VDCUIList vdcUIListReleased;

    public VDCUIList getVdcUIList() {
        return this.vdcUIList;
    }

    public DataPaginator getPaginator() {
        if (this.vdcUIList != null) {
            return this.vdcUIList.getPaginator();
        }
        return null; 
    }
    
    public void setPaginator (DataPaginator paginator) {
       if (this.vdcUIList != null) {
            this.vdcUIList.setPaginator(paginator);
        }
    }
    
    public HtmlInputHidden getHiddenGroupId() {
        return hiddenGroupId;
    }

    public void setHiddenGroupId(HtmlInputHidden hiddenGroupId) {
        this.hiddenGroupId = hiddenGroupId;
    }

    private Long vdcUIListSize;
    private VDCGroup group;

    private void populateVDCUIList(boolean isAlphaSort) {
        Long networkId = new Long (0);
        if (getVDCRequestBean().getCurrentVdcNetwork() != null){
            networkId = getVDCRequestBean().getCurrentVdcNetwork().getId();
        }       
        String defaultDVSortColumn =  vdcNetworkService.find().getDefaultDVSortColumn();
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
                vdcUIList = new VDCUIList(groupId, hideRestricted, defaultDVSortColumn);
            } else {
                vdcUIList.setAlphaCharacter("");
                vdcUIList.getVdcUIList();
                vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
            }
        } else {
            vdcUIList = new VDCUIList(groupId, (String)hiddenAlphaCharacter.getValue(), hideRestricted, defaultDVSortColumn);
            if (!((String)hiddenAlphaCharacter.getValue()).equals(vdcUIList.getAlphaCharacter())) {
                vdcUIList.setAlphaCharacter((String)hiddenAlphaCharacter.getValue());
                vdcUIList.oldSort = "";
            }
            vdcUIList.setSortColumnName(vdcUIList.getNameColumnName());
        } 
        if (groupId == null || groupId.equals(new Long("-1")) ) {
            group = null;
            setGroupName("Released Dataverses");
        } else {
            group = vdcGroupService.findById(groupId);
            setGroupName(group.getName());
        }
        if(networkId != null && networkId.intValue() >0){
            vdcUIList.setNetworkId(networkId);
        }
        vdcUIList.getVdcUIList();
        vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
        if (vdcUIListReleased == null){
           vdcUIListReleased = new VDCUIList(groupId, (String)hiddenAlphaCharacter.getValue(), true, "Released" );
        }
        if(networkId != null && networkId.intValue() >0){
            vdcUIListReleased.setNetworkId(networkId);
        }
    }
    

    public Long getVdcUIListSize() {
        return vdcUIListSize;
    }

    public Long getUserVDCCount() {
        if (pageUser == null) {
            pageUser = getVDCSessionBean().getUser();
        }
        VDCUser user = getVDCSessionBean().getUser();
        if (!pageUser.equals(user) || userVDCCount == null) {
            if (user != null) {
                user = userService.find(user.getId());
                userVDCCount = vdcService.getUserContributorOrBetterVDCCount(user.getId());
                return userVDCCount;
            }
            userVDCCount = new Long(0);
            return new Long(0);
        } else {
            return userVDCCount;
        }
    }
    
    public String getSoleVDCAlias() {
        if (pageUser == null) {
            pageUser = getVDCSessionBean().getUser();
        }
        VDCUser user = getVDCSessionBean().getUser();
        if (!pageUser.equals(user) || soleVDCAlias == null) {
            String retAlias = "";
            int count = 0;
            if (user != null) {
                user = userService.find(user.getId());
                Long initialTest = vdcService.getUserContributorOrBetterVDCCount(user.getId());
                if (initialTest.intValue() != 1){
                    soleVDCAlias = "";
                    return soleVDCAlias;
                }
                List<VDC> vdcs = vdcService.getUserVDCs(user.getId());
                for (VDC dv : vdcs) {
                    VDCRole vdcRole = roleService.findByUserVDC(user.getId(), dv.getId());
                    if (!vdcRole.getRole().getName().equals(RoleServiceLocal.PRIVILEGED_VIEWER)) {
                        count++;
                        retAlias = dv.getAlias();
                    }
                    if (count > 1) {
                        return "";
                    }
                }
                if (count == 1) {
                    soleVDCAlias = retAlias;
                    return retAlias;
                }
                soleVDCAlias = "";
                return "";
            }
            soleVDCAlias = "";
            return "";
        } else {
            return soleVDCAlias;
        }
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
    
    public List<VDCNetworkUI> getVdcSubnetworks() {return vdcSubnetworks;}
    public void setVdcSubnetworks(List<VDCNetworkUI> vdcSubnetworks) {this.vdcSubnetworks = vdcSubnetworks;}
    public String getSubnetworkAlias() {return subnetworkAlias;}
    public void setSubnetworkAlias(String subnetworkAlias) {this.subnetworkAlias = subnetworkAlias;}

    protected void initAccordionMenu() {
        if (accordionItemBeans != null) {
            accordionItemBeans.clear();
        } else {
            accordionItemBeans = new ArrayList();
        }

        List<VDCGroup> list = (List<VDCGroup>) vdcGroupService.findAll();
        for (VDCGroup vdcgroup : list) {
            classificationsSize++;
            String indentStyle = (vdcgroup.getParent() == null) ? "groupRowIndentStyle" : "childRowIndentStyle";
            if (vdcgroup.getParent() == null) {
                DataverseGrouping parentItem = populateTopNode(vdcgroup, indentStyle);
                // get all of the vdcs that belong to this group and add them to the parent
                populateDescendants(vdcgroup, parentItem, true);

                if (!parentItem.getChildItems().isEmpty()) {
                    accordionItemBeans.add(parentItem);
                }
            }
        }
    }

    //Manage classification
    protected DataverseGrouping populateTopNode(VDCGroup vdcgroup, String indentStyle) {
        boolean isExpanded = false;
        DataverseGrouping parentItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", isExpanded, "", "", new Long("-1"));

        parentItem.setShortDescription(vdcgroup.getDescription());
        parentItem.setTextIndent(0);

        if (!indentStyle.equals("")) {
            parentItem.setIndentStyleClass(indentStyle);
        }

        return parentItem;
    }

     protected void populateDescendants(VDCGroup vdcgroup, DataverseGrouping parentItem, boolean isExpanded) {
        Long parentId = vdcgroup.getId();         
        Long networkId = getVDCRequestBean().getCurrentVdcNetwork().getId();
        List<VDCGroup> list = vdcGroupService.findByParentId(parentId);

        DataverseGrouping childItem;
        for (VDCGroup group : list) {
            // Check total number of DVs in this VDCGroup and its children
            Long numDVs = vdcGroupService.findCountParentChildVDCsByVDCGroupIdSubnetworkId(group.getId(), networkId);
            if (numDVs > 0) {
                childItem = new DataverseGrouping(group.getId(), group.getName(), "subgroup", isExpanded, "", "", parentId, numDVs);
                parentItem.addItem(childItem);
                parentItem.setIsAccordion(true);
                
                // now iterate through next level
                if (!vdcGroupService.findByParentId(group.getId()).isEmpty()) {
                    List<VDCGroup> innerlist = vdcGroupService.findByParentId(group.getId());
                    DataverseGrouping xtraItem;
                    childItem.setXtraItems(new ArrayList());
                    for (VDCGroup innerGroup : innerlist){
                        numDVs = vdcGroupService.findCountVDCsByVDCGroupIdSubnetworkId(innerGroup.getId(), networkId);
                        if (numDVs > 0) {
                            xtraItem = new DataverseGrouping(innerGroup.getId(), innerGroup.getName(), "subgroup", isExpanded, "", "", parentId, numDVs);
                            childItem.addXtraItem(xtraItem);
                        }
                    }
                }
            }
        }
    }
      


     
  //getters

    public ArrayList getAccordionItemBeans() {
        return accordionItemBeans;
    }

    public int getClassificationsSize() {
        return this.classificationsSize;
    }

    public HtmlDataTable getDataverseList() {
       return this.dataverseList;
   }

     public String getDefaultVdcPath() {
        return defaultVdcPath;
    }

    public Long getGroupId() {
        return groupId;
    }

    public StatusMessage getMsg(){
        return msg;
    }

     public String getParsedLocalAnnouncements() {
         if (parsedLocalAnnouncements == null) {
             parsedLocalAnnouncements = parseAnnouncements((getVDCRequestBean().getCurrentVDC()!= null) ? getVDCRequestBean().getCurrentVDC().getAnnouncements(): "", true);

         }
        return this.parsedLocalAnnouncements;
    }

     public String getParsedNetworkAnnouncements() {
         if (parsedNetworkAnnouncements == null) {
             parsedNetworkAnnouncements = parseAnnouncements((getVDCRequestBean().getCurrentVdcNetwork() != null) ? getVDCRequestBean().getCurrentVdcNetwork().getAnnouncements(): "", false);
         }         
         
        return this.parsedNetworkAnnouncements;
    }

     @Inject VDCApplicationBean vdcApplicationBean;
     
     public List getRecentStudies() {
        if (recentStudies == null) {            
            recentStudies = filterVisibleStudyUIsFromIds( vdcApplicationBean.getAllStudyIdsByReleaseDate(vdcNetworkId), 5 ); 
        }
        return recentStudies;
    }
     
    public List getMostDownloadedStudies() {
        if (mostDownloadedStudies == null) {
            mostDownloadedStudies = filterVisibleStudyUIsFromIds( vdcApplicationBean.getAllStudyIdsByDownloadCount(vdcNetworkId), 5 ); 
        }
        return mostDownloadedStudies;
    }
    
    public List getMostDownloadedDVs(){
        List mostDownloaded = new ArrayList();
        if (vdcUIListDownloaded == null){
           vdcUIListDownloaded = new VDCUIList(groupId, (String)hiddenAlphaCharacter.getValue(), true, "Activity");
        }
        if (!vdcUIListDownloaded.getVdcUIList().isEmpty()){
            int count = 0;
            Iterator iter = vdcUIListDownloaded.getVdcUIList().iterator();
            while (iter.hasNext()) {
                VDCUI vdcUI = (VDCUI) iter.next();
                mostDownloaded.add(vdcUI);
                if (++count >= 5) {
                    break;
                }
            }           
        }
        return mostDownloaded;
    }
    
    public List getMostRecentlyReleasedDVs(){
        List mostRecentlyReleased = new ArrayList();

        if (!vdcUIListReleased.getVdcUIList().isEmpty()){
            int count = 0;
            Iterator iter = vdcUIListReleased.getVdcUIList().iterator();
            while (iter.hasNext()) {
                VDCUI vdcUI = (VDCUI) iter.next();

                mostRecentlyReleased.add(vdcUI);
                if (++count >= 5) {
                    break;
                }
            }           
        }
        return mostRecentlyReleased;
    }


    private List filterVisibleStudyUIsFromIds(List<Long> originalStudies, int numResults) {
        List filteredStudies = new ArrayList();
        if (numResults != 0) {
            int count = 0;
            for (Long studyId : originalStudies) //create studyUI with study id instead of getting study here.
            {
                try {
                    Study test = studyService.getStudyForSearch(studyId, null);
                    StudyUI studyUIToAdd = new StudyUI(test, getVDCSessionBean().getUser(), getVDCSessionBean().getIpUserGroup(), false);
                    filteredStudies.add(studyUIToAdd);
                    count++;
                } catch (Exception e) {
                    // this study id does not have a visible version, so we can skip it
                    //e.printStackTrace();
                }
                
                if (numResults > 0 && count >= numResults) {
                    break;
                }
            }
        }
        return filteredStudies;
    }

     public String getSearchField() {
        return searchField;
    }
     
// checking for null only seemed to be a problem for download count, but it couldn't hurt to 
// make it on all displays
    public String getStudyCount() {
        Long count = vdcNetworkStatsService.getVDCNetworkStatsByNetworkId(vdcNetworkId).getStudyCount();
        if (count == null){
            return NumberFormat.getIntegerInstance().format(0);
        }
        return NumberFormat.getIntegerInstance().format(count);
    }

    public String getFileCount() {
        Long count = vdcNetworkStatsService.getVDCNetworkStatsByNetworkId(vdcNetworkId).getFileCount();
        if (count == null){
            return NumberFormat.getIntegerInstance().format(0);
        }
        return NumberFormat.getIntegerInstance().format(count);
    }
    
    public String getDownloadCount() {
        Long count = vdcNetworkStatsService.getVDCNetworkStatsByNetworkId(vdcNetworkId).getDownloadCount();
        if (count == null){
            return NumberFormat.getIntegerInstance().format(0);
        }
        return NumberFormat.getIntegerInstance().format(count);
    }

   //setters
 
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




    //utils
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
            Long networkId = getVDCRequestBean().getCurrentVdcNetwork() != null ? getVDCRequestBean().getCurrentVdcNetwork().getId() : new Long (0);             
            if (isLocal) {
                truncatedAnnouncements += "<a href=\"/dvn/faces/AnnouncementsPage.xhtml?vdcId=" + getVDCRequestBean().getCurrentVDC().getId() + "\" title=\"" + resourceBundle.getString("moreLocalAnnouncementsTip") + "\" class=\"dvn_more\" >more >></a>";
            } else if (!networkId.equals(new Long (0))) {
                truncatedAnnouncements += "<a href=\"/dvn/dataverses/" + getVDCRequestBean().getCurrentVdcNetwork().getUrlAlias()  + "/faces/AnnouncementsPage.xhtml\" title=\"" + resourceBundle.getString("moreNetworkAnnouncementsTip") + "\" class=\"dvn_more\" >more >></a>";
            } else {
                truncatedAnnouncements += "<a href=\"/dvn/faces/AnnouncementsPage.xhtml\" title=\"" + resourceBundle.getString("moreNetworkAnnouncementsTip") + "\" class=\"dvn_more\" >more >></a>";
            }
        }
        return truncatedAnnouncements;
    }

    public String getSubNetworkLogoUrl(String logo) {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        StringBuffer requestURLbuffer = request.getRequestURL();
        String[] requestURLparts = new String(requestURLbuffer).split(":");
        String httpOrHttps = requestURLparts[0];
        String hostName = System.getProperty("dvn.inetAddress");
        int port = request.getLocalPort();
        String portStr = "";
        if (port != 80) {
            portStr = ":" + port;
        }
        String logoUrl = httpOrHttps + "://" + hostName + portStr + "/images/" + logo;
        return logoUrl;
    }

}