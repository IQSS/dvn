/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlMessages;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.DataverseGrouping;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author wbossons
 */
public class ManageClassificationsPage extends VDCBaseBean implements Serializable {

    @EJB VDCGroupServiceLocal vdcGroupService;
    @EJB VDCServiceLocal vdcService;

     private Long cid;
     private HtmlCommandLink linkDelete = new HtmlCommandLink();

     private boolean result;
     private String statusMessage;
     private String SUCCESS_MESSAGE   = new String("Success. The classifications and dataverses operation completed successfully.");
     private String FAIL_MESSAGE      = new String("Problems occurred during the form submission. Please see error messages below.");
     private HtmlMessages      iceMessage = new HtmlMessages();

     //fields from dvrecordsmanager
    private DataverseGrouping parentItem = null;
    private DataverseGrouping childItem  = null;
    private ArrayList itemBeans;
    private boolean isInit;
    private int itemBeansSize = 0;
    public static final String GROUP_INDENT_STYLE_CLASS = "GROUP_INDENT_STYLE_CLASS";
    public static final String GROUP_ROW_STYLE_CLASS = "groupRow";
    public static final String CHILD_INDENT_STYLE_CLASS = "CHILD_INDENT_STYLE_CLASS";
    public String CHILD_ROW_STYLE_CLASS;
    public static final String CONTRACT_IMAGE = "tree_nav_top_close_no_siblings.gif";
    public static final String EXPAND_IMAGE = "tree_nav_top_open_no_siblings.gif";

    //these static variables have a dependency on the Network Stats Server e.g.
    // they should be held as constants in a constants file ... TODO
    private static Long   SCHOLAR_ID = new Long("-1");
    private static String   SCHOLAR_SHORT_DESCRIPTION = new String("A short description for the research scholar group");
    private static Long   OTHER_ID   = new Long("-2");
    private static String   OTHER_SHORT_DESCRIPTION = new String("A short description for the unclassified dataverses group (other).");

    private String task = new String("");

     public void init() {
        super.init();
        linkDelete.setValue("Delete");
         // initialize the list
        if (itemBeans != null) {
            itemBeans.clear();
        } else {
            itemBeans = new ArrayList();
        }
        //DEBUG support subcollections
            List list = (List)vdcGroupService.findAll();
            //loop through results to get parents and subgroups
            Iterator iterator = list.iterator();
             //find subgroups
             VDCGroup vdcgroup = null;
             while(iterator.hasNext()) {
                //add DataListItems to the list
                itemBeansSize++;
                vdcgroup = (VDCGroup)iterator.next();
                populateParentClassification(vdcgroup);
                List innerlist = vdcGroupService.findByParentId(vdcgroup.getId());
                Iterator inneriterator = innerlist.iterator();
                long childCount = 0;
                while(inneriterator.hasNext()) {
                    VDCGroup subgroup = (VDCGroup)inneriterator.next();
                    populateSubClassification(subgroup);
                    childCount++;
                }
                parentItem.setSubclassification(childCount);
                /*Long parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("0");
                System.out.println("dv records manager: parent in group is " + vdcgroup.getParent());
                parentItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, parent);
                parentItem.setShortDescription(vdcgroup.getDescription());*/

                //Now check for subgroups by recursion
             }
     }

          private void initGroupBean(List list) {
             Iterator iterator = list.iterator();
             VDCGroup vdcgroup = null;
             while(iterator.hasNext()) {
                //add DataListItems to the list
                itemBeansSize++;
                vdcgroup = (VDCGroup)iterator.next();
                Long parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("0");
                System.out.println("dv records manager: parent in group is " + vdcgroup.getParent());
                parentItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, parent);
                parentItem.setShortDescription(vdcgroup.getDescription());
                parentItem.setSubclassification(new Long("25"));//TODO: method to get the number of children
                List innerlist = vdcgroup.getVdcs();
                Iterator inneriterator = innerlist.iterator();
                while(inneriterator.hasNext()) {
                    VDC vdc = (VDC)inneriterator.next();
                    //TODO: Make this the timestamp for last update time
                    //Timestamp lastUpdateTime = (studyService.getLastUpdatedTime(vdc.getId()) != null ? studyService.getLastUpdatedTime(vdc.getId()) : vdc.getReleaseDate());
                   // Long localActivity       = calculateActivity(vdc);
                    //String activity          = getActivityClass(localActivity);
                    parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("0");
                    childItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "subgroup", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, parent);
                    parentItem.addChildItem(childItem);
                }
            }
        }

     private void initUnGroupedBeans(List list, String caption, Long netstatsId, String shortDescription) {
        Iterator iterator = list.iterator();
        parentItem = new DataverseGrouping(netstatsId, caption, "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, new Long("0"));
        parentItem.setShortDescription(shortDescription);
        parentItem.setSubclassification(new Long("25"));
        itemBeansSize++;
        while (iterator.hasNext()) {
            VDC vdc = (VDC)iterator.next();
           // Timestamp lastUpdateTime = (studyService.getLastUpdatedTime(vdc.getId()) != null ? studyService.getLastUpdatedTime(vdc.getId()) : vdc.getReleaseDate());
           // Long localActivity       = calculateActivity(vdc);
           // String activity          = getActivityClass(localActivity);
           // childItem = new DataverseGrouping(vdc.getName(), vdc.getAlias(), vdc.getAffiliation(), vdc.getReleaseDate(), lastUpdateTime, vdc.getDvnDescription(),  "dataverse", activity);
            //parentItem.addChildItem(childItem);
        }
     }

     //Manage classification
     private void populateParentClassification(VDCGroup vdcgroup) {
         Long parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("-1");
         System.out.println("dv records manager: parent in group is " + vdcgroup.getParent());
         List list = vdcGroupService.findByParentId(vdcgroup.getId());
         Iterator iterator = list.iterator();
         String expandImage = null;
         String contractImage = null;
         boolean isExpanded   = false;
         if (iterator.hasNext()) {
            expandImage   = EXPAND_IMAGE;
            contractImage = CONTRACT_IMAGE;
            isExpanded    = true;
         }
         parentItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "subgroup", itemBeans, isExpanded, EXPAND_IMAGE, CONTRACT_IMAGE, parent);
         parentItem.setShortDescription(vdcgroup.getDescription());
     }

     private void populateSubClassification(VDCGroup vdcgroup) {
         Long parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("-1");
         System.out.println("dv records manager: parent in group is " + vdcgroup.getParent());
         List list = vdcGroupService.findByParentId(vdcgroup.getId());
         Iterator iterator = list.iterator();
         String expandImage = null;
         String contractImage = null;
         boolean isExpanded   = false;
         if (iterator.hasNext()) {
            expandImage   = EXPAND_IMAGE;
            contractImage = CONTRACT_IMAGE;
            isExpanded    = true;
         }
         childItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "subgroup", itemBeans, isExpanded, expandImage, contractImage, parent);
         childItem.setShortDescription(vdcgroup.getDescription());
         parentItem.addChildItem(childItem);
     }

     public String delete_action() {
        statusMessage = SUCCESS_MESSAGE;
        result = true;
        setCid(new Long((String)linkDelete.getAttributes().get("cid")));
        System.out.println("ManageClass...Page: cid=" + cid);
        try {
            VDCGroup vdcgroup = vdcGroupService.findById(cid);
            vdcGroupService.removeVdcGroup(vdcgroup);
            //and reset all of the subsequent groups
            //this.vdcGroupService.updateGroupOrder(order); // TBD
        } catch (Exception e) {
            statusMessage = FAIL_MESSAGE + " " + e.getCause().toString();
            result = false;
        } finally {
            Iterator iterator = FacesContext.getCurrentInstance().getMessages("AddClassificationsPageForm");
            while (iterator.hasNext()) {
                iterator.remove();
            }
            FacesContext.getCurrentInstance().addMessage("AddClassificationsPageForm", new FacesMessage(statusMessage));
            return "result";

        }
     }

     //getters
     public Long getCid() {
         return this.cid;
     }

     public HtmlCommandLink getLinkDelete() {
         return this.linkDelete;
     }

     public HtmlMessages getIceMessage() {
        return this.iceMessage;
    }

     public boolean getResult() {
        return result;
    }

     //setters
     public void setCid(Long cId) {
         this.cid = cId;
     }

     public void setLinkDelete(HtmlCommandLink linkdelete) {
         this.linkDelete = linkdelete;
     }

     public void setIceMessage(HtmlMessages icemessage) {
        iceMessage.setStyleClass("successMessage");
        this.iceMessage = icemessage;
    }

     public void setResult(boolean result) {
        this.result = result;
    }

    //TODO: remove task from the link and managed bean
    //and then delete these
    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
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


}
