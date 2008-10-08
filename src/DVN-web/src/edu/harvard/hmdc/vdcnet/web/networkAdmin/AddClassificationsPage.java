/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlInputHidden;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.icesoft.faces.component.ext.HtmlMessages;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.DataverseGrouping;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 * @author wbossons
 */
public class AddClassificationsPage extends VDCBaseBean implements Serializable {
    @EJB VDCGroupServiceLocal vdcGroupService;
    @EJB VDCServiceLocal vdcService;

    private HtmlInputText     nameInput = new HtmlInputText();
    private HtmlInputTextarea descriptionInput = new HtmlInputTextarea();
    private HtmlInputText     parentInput;
    private HtmlSelectOneMenu parentSelect;
    private SelectItem[]      parentSelectItems;
    private HtmlMessages      iceMessage = new HtmlMessages();

    //rowselection fields
    private ArrayList selectedDataverses;
    private ArrayList dataverses;// internal list of retreived records.
    private boolean multiRowSelect = true;
    private HtmlInputHidden classificationId;
    private HtmlInputHidden multiRowSelector;
    private Long cid;

    private boolean result;
    private String statusMessage;
    private String SUCCESS_MESSAGE   = new String("Success. The classifications and dataverses operation completed successfully.");
    private String FAIL_MESSAGE      = new String("Problems occurred during the form submission. Please see error messages below.");


    public void init() {
        super.init();
        initParentSelectItems();
        dataverses         = new ArrayList();
        selectedDataverses = new ArrayList();
        initItemBeans();
        if (cid != null) {
            initClassificationBean();
            initSelectedItemBeans();
        }
        result = false;
    }

    private void initClassificationBean() {
        VDCGroup vdcGroup = vdcGroupService.findById(cid);
        nameInput.setValue(vdcGroup.getName());
        descriptionInput.setValue(vdcGroup.getDescription());
    }

    private void initSelectedItemBeans() {
         VDCGroup vdcGroup = vdcGroupService.findById(cid);
         List list = (List)vdcGroup.getVdcs();
         Iterator iterator = list.iterator();
         while(iterator.hasNext()) {
             VDC vdc = (VDC)iterator.next();
             DataverseGrouping dataverse = new DataverseGrouping(vdc.getId(), vdc.getName(), vdc.getAffiliation());
             dataverse.setSelected(true);
             selectedDataverses.add(dataverse);
             System.out.println("RowSelectController:dataversegrouping: " + dataverse.toString());
             //initialize the members
             Iterator innerIterator = dataverses.iterator();
             while (innerIterator.hasNext()) {
                 DataverseGrouping selectorDataverse = (DataverseGrouping)innerIterator.next();
                 if (dataverse.getId().equals(selectorDataverse.getId()) ) {
                    selectorDataverse.setSelected(true);
                    break;
                 }
             }
         }
    }

     private void initItemBeans() {
         List list = (List)vdcService.findAll();
         Iterator iterator = list.iterator();
         while(iterator.hasNext()) {
             VDC vdc = (VDC)iterator.next();
             DataverseGrouping dataversegrouping = new DataverseGrouping(vdc.getId(), vdc.getName(), vdc.getAffiliation());
             System.out.println("RowSelectController:dataversegrouping: " + dataversegrouping.toString());
             dataverses.add(dataversegrouping);
         }
    }

    private void initParentSelectItems() {
        List list = (List) vdcGroupService.findAll();
        Iterator iterator = list.iterator();
        parentSelectItems = new SelectItem[list.size()];
        int i = 0;
        while (iterator.hasNext()) {
            VDCGroup vdcgroup = (VDCGroup)iterator.next();
            SelectItem selectitem = new SelectItem();
            selectitem.setLabel((String) vdcgroup.getName());
            selectitem.setValue(vdcgroup.getId());
            parentSelectItems[i] = selectitem;
            i++;
        }
    }

    //getters
    
    public HtmlInputText getNameInput() {
        return this.nameInput;
    }
    
    public HtmlInputTextarea getDescriptionInput() {
        return this.descriptionInput;
    }

    public HtmlInputText getParentInput() {
        return this.parentInput;
    }

    public HtmlSelectOneMenu getParentSelect() {
        return this.parentSelect;
    }

    public SelectItem[] getParentSelectItems() {
        return parentSelectItems;
    }

    public HtmlMessages getIceMessage() {
        return this.iceMessage;
    }

    public boolean isResult() {
        return result;
    }

    //setters
    public void setNameInput(HtmlInputText nameinput) {
        this.nameInput = nameinput;
    }

    public void setDescriptionInput(HtmlInputTextarea descriptioninput) {
        this.descriptionInput = descriptioninput;
    }

    public void setParentInput(HtmlInputText parentinput) {
        this.parentInput = parentinput;
    }

    public void setParentSelect(HtmlSelectOneMenu parentselect) {
        this.parentSelect = parentselect;
    }

    public void setParentSelectItems(SelectItem[] parentselectitems) {
        this.parentSelectItems = parentselectitems;
    }

    public void setIceMessage(HtmlMessages icemessage) {
        iceMessage.setStyleClass("successMessage");
        this.iceMessage = icemessage;
    }

    //TODO from RowSelectController

    /**
     * SelectionListener bound to the ice:rowSelector component.  Called
     * when a row is selected in the UI.
     *
     * @param event from the ice:rowSelector component
     */
    public void rowSelectionListener(RowSelectorEvent event) {
        // clear our list, so that we can build a new one
        selectedDataverses.clear();

        // build the new selected list
        DataverseGrouping dataverse;
        for(int i = 0, max = dataverses.size(); i < max; i++){
        dataverse = (DataverseGrouping)dataverses.get(i);
            if (dataverse.isSelected()) {
                selectedDataverses.add(dataverse);
            }
        }
    }

    /**
     * Clear the selection list if going from multi select to single select.
     *
     * @param event jsf action event.
     */
    public void rowSelectionListener(ValueChangeEvent event) {
        // if multi select then want to make sure we clear the selected states
        if (!((Boolean) event.getNewValue()).booleanValue()) {
            selectedDataverses.clear();

            // build the new selected list
            DataverseGrouping dataverse;
            for(int i = 0, max = dataverses.size(); i < max; i++){
                dataverse = (DataverseGrouping)dataverses.get(i);
                dataverse.setSelected(false);
            }
        }
    }


    public ArrayList getDataverses() {
        return dataverses;
    }

    public ArrayList getSelectedDataverses() {
        return selectedDataverses;
    }

    public void setSelectedDataverses(ArrayList selectedDataverses) {
        this.selectedDataverses = selectedDataverses;
    }

    public boolean isMultiRowSelect() {
        return multiRowSelect;
    }

    public HtmlInputHidden getClassificationId() {
        return this.classificationId;
    }

    public Long getCid() {
        return this.cid;
    }


    public void setCid(Long cid) {
        this.cid = cid;
    }

    public HtmlInputHidden getMultiRowSelector() {
        return this.multiRowSelector;
    }
    /**
     * Sets the selection more of the rowSelector.
     *
     * @param multiRowSelect true indicates multi-row select and false indicates
     *                       single row selection mode.
     */
    public void setMultiRowSelect(boolean multiRowSelect) {
        this.multiRowSelect = multiRowSelect;
    }

    //getters


    //setters
    public void setClassificationId(HtmlInputHidden classificationid) {
        this.classificationId = classificationid;
    }

    public void setMultiRowSelector(HtmlInputHidden multirowselector) {
        this.multiRowSelector = multirowselector;
    }

    public String add_action() {
        Iterator iterator = FacesContext.getCurrentInstance().getMessages("AddClassificationsPageForm");
        if (iterator.hasNext())
            iterator.remove();
        String statusMessage = SUCCESS_MESSAGE;
        result = true;
        try {
            //
            VDCGroup vdcgroup = new VDCGroup();
            vdcgroup.setName((String)nameInput.getValue());
            vdcgroup.setDescription((String)descriptionInput.getValue());
            vdcgroup.setParent(new Long((String)parentSelect.getValue()));
            vdcGroupService.create(vdcgroup);
            classificationId.setValue(vdcgroup.getId());
            update_action();
        } catch (Exception e) {
            statusMessage = FAIL_MESSAGE;
        } finally {
            FacesContext.getCurrentInstance().addMessage("AddClassificationsPageForm", new FacesMessage(statusMessage));
            return "result";
        }
        
    }

    public String update_action() {
        //now add all of these dataverses to the parent
        Iterator msgiterator = FacesContext.getCurrentInstance().getMessages("AddClassificationsPageForm");
        if (msgiterator.hasNext())
            msgiterator.remove();
        String statusMessage = SUCCESS_MESSAGE;
        result = true;
        try {
            Long[] vdcs         = new Long[selectedDataverses.size()];
            Iterator iterator   = selectedDataverses.iterator();
            int count           = 0;
            while (iterator.hasNext()) {
                DataverseGrouping dataversegrouping = (DataverseGrouping)iterator.next();
                vdcs[count] =  new Long(dataversegrouping.getId());
                count++;
            }
            VDCGroup vdcgroup = vdcGroupService.findById((Long)classificationId.getValue());
            vdcGroupService.updateWithVdcs(vdcgroup, vdcs);
        } catch (Exception e) {
            statusMessage = FAIL_MESSAGE;
        } finally {
            FacesContext.getCurrentInstance().addMessage("AddClassificationsPageForm", new FacesMessage(statusMessage));
            return "result";
        }
    }
}
