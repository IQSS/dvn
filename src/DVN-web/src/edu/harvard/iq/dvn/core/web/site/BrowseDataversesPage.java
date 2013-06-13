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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.site;

import com.icesoft.faces.component.datapaginator.DataPaginator;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.context.effects.JavascriptContext;
import edu.harvard.iq.dvn.core.vdc.VDCGroup;
import edu.harvard.iq.dvn.core.vdc.VDCGroupServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.DataverseGrouping;
import edu.harvard.iq.dvn.core.web.VDCUIList;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 * @author wbossons
 */
@ViewScoped
@Named("BrowseDataversesPage")
public class BrowseDataversesPage  extends VDCBaseBean implements Serializable {

    
    @EJB
    VDCServiceLocal vdcService;
    @EJB VDCGroupServiceLocal   vdcGroupService;
    private boolean hideRestricted = true;
    private Long vdcUIListSize;
    private Long groupId = new Long("-1");
    private String groupName;
    private String filterTerm ="";
    private String initialSort = "";
    private String initialFilter = "";
    private String savedSort = "";
    private VDCUIList vdcUIList;
    private boolean firstRun = true;
    private ArrayList groupItemBeans;
    private List sortOrderItems;
    private String sortOrderString;
    
    
    public BrowseDataversesPage() {
    }

    public void init() {
        super.init();        
        hideRestricted = true; 
        sortOrderItems = loadSortSelectItems();
        initGroupMenu();
        populateVDCUIList(); 
    }
    
    public void preRenderView() {  
        super.preRenderView();
        // add javascript call on each partial submit to initialize the help tips for added fields
        JavascriptContext.addJavascriptCall(getFacesContext(),"initListingPanelRemoveHeights();");       
        JavascriptContext.addJavascriptCall(getFacesContext(),"initListingPanelHeights();");
    }
    
    public void sort_action(ValueChangeEvent event) {
        String sortBy = (String) event.getNewValue();
        if (!sortBy.isEmpty()){
            savedSort = sortBy;
            innerSort(sortBy); 
        }
    }
    
    public String updateDVList(){
        String checkString = (String) getInputFilterTerm().getValue();        
        filterTerm = checkString;
        populateVDCUIList(); 
        return "";
    }
        
    protected void innerSort(String sortBy){       
        if (sortBy == null || sortBy.equals("")) {
            return;
        }
        if (this.vdcUIList != null && this.vdcUIList.getVdcUIList().size() > 0) {           
            vdcUIList = new VDCUIList(groupId, "", hideRestricted);
            vdcUIList.setAlphaCharacter("");
            vdcUIList.setOldSort(new String(""));
            vdcUIList.setSortColumnName(sortBy);
            resetScroller();
        }
       vdcUIList.setSortColumnName(sortBy);
       vdcUIList.setOldSort(new String(""));   
    }

    private void resetScroller() {
        if (this.vdcUIList != null  && this.vdcUIList.getPaginator() != null) {
            this.vdcUIList.getPaginator().gotoFirstPage();
        }
    }

    private void populateVDCUIList() {
        Long networkId = getVDCRequestBean().getCurrentVdcNetwork().getId();
        if (firstRun){
           filterTerm = initialFilter; 
        }
        vdcUIList = new VDCUIList(groupId, "", filterTerm, hideRestricted);
        if (networkId !=null && networkId.intValue() > 0){
           vdcUIList.setNetworkId(networkId); 
        } else{
           vdcUIList.setNetworkId(new Long (0));  
        }
        if((initialSort.isEmpty() || !firstRun)  && savedSort.isEmpty()){
            vdcUIList.setSortColumnName(vdcUIList.getDateReleasedColumnName()); 
            sortOrderString = vdcUIList.getDateReleasedColumnName();
        } else {
            if (!initialSort.isEmpty() && firstRun){
                vdcUIList.setSortColumnName(initialSort); 
                sortOrderString = initialSort;
            } else if (!savedSort.isEmpty()){
                vdcUIList.setSortColumnName(savedSort); 
            }                
        } 
        vdcUIList.setOldSort("");
        vdcUIList.getVdcUIList();
        if(vdcUIList.getVdcUIList() != null){
            vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
        } else {
            vdcUIListSize = new Long(String.valueOf(0));
        }     
        
        if (groupId == null || groupId.equals(new Long("-1")) ) {
            setGroupName("Released Dataverses");
        } else {
            setGroupName(vdcGroupService.findById(groupId).getName());
        }
        vdcUIList.getVdcUIList();
        vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
        firstRun = false;
    }
    
    protected void initGroupMenu() {
        if (groupItemBeans != null) {
            groupItemBeans.clear();
        } else {
            groupItemBeans = new ArrayList();
        }
        List<VDCGroup> list = (List) vdcGroupService.findAll();
        for (VDCGroup vdcgroup : list) {
            String indentStyle = (vdcgroup.getParent() == null) ? "groupRowIndentStyle" : "childRowIndentStyle";
            if (vdcgroup.getParent() == null) {
                DataverseGrouping parentItem = populateTopNode(vdcgroup, indentStyle);
                // get all of the vdcs that belong to this group and add them to the parent
                populateDescendants(vdcgroup,parentItem, true);
                if (!parentItem.getChildItems().isEmpty()) {
                    groupItemBeans.add(parentItem);
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
                    for (VDCGroup innerGroup : innerlist) {
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

   
    private List<SelectItem> loadSortSelectItems(){
        List selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem("Name", "Name"));
        selectItems.add(new SelectItem("Affiliation", "Affiliation"));
        selectItems.add(new SelectItem("Released", "Release Date"));
        selectItems.add(new SelectItem("Activity", "Download Count"));    
        return selectItems;
    }
    
    public List getSortOrderItems() {
        return sortOrderItems;
    }

    public void setSortOrderItems(List sortOrderItems) {
        this.sortOrderItems = sortOrderItems;
    }
    
    public String getSortOrderString() {
        return sortOrderString;
    }

    public void setSortOrderString(String sortOrderString) {
        this.sortOrderString = sortOrderString;
    }
     
  //getters
    public ArrayList getGroupItemBeans() {
        return groupItemBeans;
    }

    public void setGroupId(Long groupId){
        this.groupId = groupId;       
    }
    
    public Long getGroupId(){
        return groupId;       
    }
    
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
       
    public String getInitialSort() {
        return initialSort;
    }

    public void setInitialSort(String initialSort) {
        this.initialSort = initialSort;
    }
    
    public String getInitialFilter() {
        return initialFilter;
    }

    public void setInitialFilter(String initialFilter) {
        this.initialFilter = initialFilter;
    }
    
    public String getFilterTerm() {
        return filterTerm;
    }

    public void setFilterTerm(String filterTerm) {
        this.filterTerm = filterTerm;
    }    
    
    public String filterAction() {
        return "";
    }
    
    private HtmlInputText inputFilterTerm;
    public HtmlInputText getInputFilterTerm() {
        return this.inputFilterTerm;
    }
    public void setInputFilterTerm(HtmlInputText inputFilterTerm) {
        this.inputFilterTerm = inputFilterTerm;
    }
    

    //getters
    public VDCUIList getVdcUIList() {
         return this.vdcUIList;
     }

    public Long getVdcUIListSize() {return vdcUIListSize;}
    private Long vdcUnreleased;
    public Long getVdcUnreleased() {return vdcUnreleased;}

    //setters
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

