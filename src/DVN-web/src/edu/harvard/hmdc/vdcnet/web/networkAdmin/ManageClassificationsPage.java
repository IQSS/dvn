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
     
     private boolean result;
     private String statusMessage;
     private String SUCCESS_MESSAGE   = new String("Success. The classifications and dataverses operation completed successfully.");
     private String FAIL_MESSAGE      = new String("Problems occurred during the form submission. Please see error messages below.");
     private HtmlMessages      iceMessage = new HtmlMessages();

     //fields from dvrecordsmanager
    private DataverseGrouping parentItem = null;
    private DataverseGrouping childItem  = null;
    private final ArrayList itemBeans = new ArrayList();
    private boolean isInit;
    private int itemBeansSize = 0; //used to output the number of classifications
    public static final String GROUP_INDENT_STYLE_CLASS = "GROUP_INDENT_STYLE_CLASS";
    public static final String GROUP_ROW_STYLE_CLASS = "groupRow";
    public static final String CHILD_INDENT_STYLE_CLASS = "CHILD_INDENT_STYLE_CLASS";
    public String CHILD_ROW_STYLE_CLASS;
    public static final String CONTRACT_IMAGE = "tree_nav_top_close_no_siblings.gif";
    public static final String EXPAND_IMAGE = "tree_nav_top_open_no_siblings.gif";

    //these static variables have a dependency on the Network Stats Server e.g.
    // they should be held as constants in a constants file ... TODO
    private static Long   SCHOLAR_ID                    = new Long("-1");
    private static String   SCHOLAR_SHORT_DESCRIPTION   = new String("A short description for the research scholar group");
    private static Long   OTHER_ID                      = new Long("-2");
    private static String   OTHER_SHORT_DESCRIPTION     = new String("A short description for the unclassified dataverses group (other).");

     public void init() {
        super.init();
         // initialize the list
        synchronized(itemBeans) {
            if (itemBeans != null) {
                itemBeans.clear();
            }
        }
            List list = (List)vdcGroupService.findAll();
            Iterator outeriterator = list.iterator();
             VDCGroup vdcgroup = null;
             System.out.println(list.toString());
             while(outeriterator.hasNext()) {
                itemBeansSize++;
                vdcgroup = (VDCGroup)outeriterator.next();
                if (removeFromList.contains(vdcgroup)) {
                    continue;
                } else {
                String indentStyle = (vdcgroup.getParent() == null) ? "" : "childRowIndentStyle";
                populateParentClassification(vdcgroup, indentStyle);
                }
             }
             List myList = itemBeans;
             Iterator iterator = myList.iterator();
             synchronized(myList) {
                 while (iterator.hasNext()) {
                     DataverseGrouping grouping = (DataverseGrouping)iterator.next();
                     grouping.toggleSubGroupAction();
                 }
             }
     }

    List removeFromList = new ArrayList();

     //Manage classification
     private void populateParentClassification(VDCGroup vdcgroup, String indentStyle) {
         Long parent = (vdcgroup.getParent() != null) ? vdcgroup.getParent() : new Long("-1");
         //System.out.println("dv records manager: parent in group is " + vdcgroup.getParent());
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
         synchronized(itemBeans) {
            parentItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", itemBeans, isExpanded, expandImage, contractImage, parent);
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
         if (!list.isEmpty()) {
             expandImage    = EXPAND_IMAGE;
             contractImage  = CONTRACT_IMAGE;
             isExpanded     = true;
         }
         childItem = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "subgroup", isExpanded, expandImage, contractImage, new Long(parentitem.getId()));
         childItem.setShortDescription(vdcgroup.getDescription());
         childItem.setIndentStyleClass("childRowIndentStyle");
         parentitem.addChildItem(childItem);
         //parentitem.toggleSubGroupAction();
         System.out.println("Created a child whose name is " + vdcgroup.getName());
         removeFromList.add(vdcgroup);
        while (iterator.hasNext() && !removeFromList.contains(vdcgroup)) {
             VDCGroup subgroup = (VDCGroup)iterator.next();
             populateSubClassification(subgroup, childItem);
           
         }
         
     }

     
     public String delete_action() {
        statusMessage = SUCCESS_MESSAGE;
        result = true;
        //setCid(new Long((String)linkDelete.getAttributes().get("cid")));
        try {
            VDCGroup vdcgroup = vdcGroupService.findById(cid);
            vdcGroupService.removeVdcGroup(vdcgroup);
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

     public void setIceMessage(HtmlMessages icemessage) {
        iceMessage.setStyleClass("successMessage");
        this.iceMessage = icemessage;
    }

     public void setResult(boolean result) {
        this.result = result;
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
