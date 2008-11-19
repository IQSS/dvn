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

/** Source File Name:   ManageClassifications.java
 *
 * DvRecordsManager is the backing bean that supports
 * the network home page. It is through this class that the
 * DataverseGrouping parents and children are created and
 * passed onto the view.
 *
 *
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlGraphicImage;
import com.icesoft.faces.component.ext.HtmlMessages;
import com.icesoft.faces.component.ext.HtmlOutputText;
import edu.harvard.hmdc.vdcnet.study.StudyDownload;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.util.DateUtils;
import edu.harvard.hmdc.vdcnet.vdc.*;
import edu.harvard.hmdc.vdcnet.web.DataverseGrouping;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

public class ManageClassificationsPage extends VDCBaseBean implements Serializable {
    @EJB StudyServiceLocal studyService;
    @EJB VDCGroupServiceLocal vdcGroupService;
    @EJB VDCServiceLocal vdcService;

     private Long cid;

     private boolean result;
     private String statusMessage;
     private String SUCCESS_MESSAGE   = new String("Success. The classifications and dataverses operation completed successfully.");
     private String FAIL_MESSAGE      = new String("Problems occurred during the form submission. Please see error messages below.");
     private HtmlMessages      iceMessage = new HtmlMessages();

    private boolean isInit;
    private int itemBeansSize = 0; //used to output the number of classifications
    private int classificationsSize = 0;
    private DataverseGrouping parentItem = null;
    private DataverseGrouping childItem  = null;
    private final ArrayList itemBeans    = new ArrayList();
    
    public String CHILD_ROW_STYLE_CLASS;
    public static final String GROUP_INDENT_STYLE_CLASS = "GROUP_INDENT_STYLE_CLASS";
    public static final String GROUP_ROW_STYLE_CLASS    = "groupRow";
    public static final String CHILD_INDENT_STYLE_CLASS = "CHILD_INDENT_STYLE_CLASS";
    public static final String CONTRACT_IMAGE           = "tree_nav_top_close_no_siblings.gif";
    public static final String EXPAND_IMAGE             = "tree_nav_top_open_no_siblings.gif";

    public ManageClassificationsPage() {
        //init();
        CHILD_ROW_STYLE_CLASS = "";
    }

     @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        // initialize the list
        if (itemBeans != null) {
            itemBeans.clear();
        } 
        List list = (List)vdcGroupService.findAll();
        initMenu();
     }

    // ***************** DEBUG START *****************
    
    
    //Manage classification
     private void populateParentClassification(VDCGroup vdcgroup, String indentStyle) {
         Long parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("-1");
         List list              = vdcGroupService.findByParentId(vdcgroup.getId());
         Iterator iterator      = list.iterator();
         String expandImage     = EXPAND_IMAGE;
         String contractImage   = CONTRACT_IMAGE;
         boolean isExpanded     = false;
         synchronized(itemBeans) {
            parentItem  = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", itemBeans, isExpanded, expandImage, contractImage, parent);
         }
         parentItem.setShortDescription(vdcgroup.getDescription());
         parentItem.setSubclassification(new Long(list.size()));
         parentItem.setTextIndent("0px");
         if (!indentStyle.equals(""))
             parentItem.setIndentStyleClass(indentStyle);
     }

    protected void initMenu() {
        if (itemBeans != null) {
            itemBeans.clear();
        } 
       
        List list = (List)vdcGroupService.findAll();
        Iterator outeriterator = list.iterator();
        while(outeriterator.hasNext()) {
            classificationsSize++;
            VDCGroup vdcgroup = (VDCGroup)outeriterator.next();
                String indentStyle = (vdcgroup.getParent() == null) ? "groupRowIndentStyle" : "childRowIndentStyle";
                if (vdcgroup.getParent() == null) {
                    populateParentClassification(vdcgroup, indentStyle);
                }
            }
    }

    /**
     * Toggles the expanded state of this dataverse group.
     *
     * @param event
     */
    public void toggleChildren(javax.faces.event.ActionEvent event) {
        Long parentId = new Long(toggleImage.getAttributes().get("groupingId").toString());
        Iterator iterator = itemBeans.iterator();
        DataverseGrouping parentitem = null;
        while (iterator.hasNext()) {
            parentitem = (DataverseGrouping)iterator.next();
            if (parentitem.getId().equals(toggleImage.getAttributes().get("groupingId").toString())) {
                break;
            }
        }
        if (!parentitem.isIsExpanded()) {
            expandSubClassification(vdcGroupService.findByParentId(parentId), parentitem);
            parentitem.setIsExpanded(true);
        } else {
            contractSubClassification(vdcGroupService.findByParentId(parentId), parentitem);
            parentitem.setIsExpanded(false);
        }

    }

    int indent = 10;

    private void expandSubClassification(List<VDCGroup> children, DataverseGrouping parentitem) {
         String expandImage     = EXPAND_IMAGE;
         String contractImage   = CONTRACT_IMAGE;
         boolean isExpanded     = false;
         Iterator iterator = children.iterator();
         indent+=5;
         while(iterator.hasNext()) {
             VDCGroup vdcgroup = (VDCGroup)iterator.next();
            synchronized(itemBeans) {
                parentItem  = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", itemBeans, isExpanded, expandImage, contractImage, new Long(parentitem.getId()));
             }
             parentItem.setIndentStyleClass("childRowIndentStyle"); //deprecate in favor of inline indent
             parentItem.setTextIndent(indent + "px");
             parentitem.addChildItem(parentItem);
             itemBeans.add(itemBeans.indexOf(parentitem) + 1, parentItem);
             
         }
     }

    private void contractSubClassification(List<VDCGroup> children, DataverseGrouping parentitem) {
         String expandImage     = EXPAND_IMAGE;
         String contractImage   = CONTRACT_IMAGE;
         boolean isExpanded     = false;
         Iterator iterator      = children.iterator();
         Iterator itemsIterator = itemBeans.iterator();
         while(iterator.hasNext()) {
             VDCGroup vdcgroup = (VDCGroup)iterator.next();
             while (itemsIterator.hasNext()) {
                 DataverseGrouping grouping = (DataverseGrouping)itemsIterator.next();
                 if (new Long(grouping.getId()).equals(vdcgroup.getId())) {
                     itemsIterator.remove();
                 }
             }
         }
         parentitem.recurseAndContractNodeAction();
     }

    private HtmlGraphicImage toggleImage = new HtmlGraphicImage();

    public HtmlGraphicImage getToggleImage() {
        return toggleImage;
    }

    public void setToggleImage(HtmlGraphicImage toggleImage) {
        this.toggleImage = toggleImage;
    }

    





     // ******************** END DEBUG ******************************

   

    
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

    public String getCHILD_ROW_STYLE_CLASS() {
        return CHILD_ROW_STYLE_CLASS;
    }

    public void setCHILD_ROW_STYLE_CLASS(String CHILD_ROW_STYLE_CLASS) {
        this.CHILD_ROW_STYLE_CLASS = CHILD_ROW_STYLE_CLASS;
    }

    public String getFAIL_MESSAGE() {
        return FAIL_MESSAGE;
    }

    public void setFAIL_MESSAGE(String FAIL_MESSAGE) {
        this.FAIL_MESSAGE = FAIL_MESSAGE;
    }

    public String getSUCCESS_MESSAGE() {
        return SUCCESS_MESSAGE;
    }

    public void setSUCCESS_MESSAGE(String SUCCESS_MESSAGE) {
        this.SUCCESS_MESSAGE = SUCCESS_MESSAGE;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public HtmlMessages getIceMessage() {
        return iceMessage;
    }

    public void setIceMessage(HtmlMessages iceMessage) {
        this.iceMessage = iceMessage;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
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