/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlInputHidden;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.icesoft.faces.component.ext.HtmlMessages;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCGroup;
import edu.harvard.iq.dvn.core.vdc.VDCGroupServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.DataverseGrouping;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.site.ClassificationList;
import edu.harvard.iq.dvn.core.web.site.ClassificationUI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 * @author wbossons
 */
@ViewScoped
@Named("addClassificationsPage")
public class AddClassificationsPage extends VDCBaseBean implements Serializable {
    @EJB VDCGroupServiceLocal vdcGroupService;
    @EJB VDCServiceLocal vdcService;

    private HtmlInputText     nameInput         = new HtmlInputText();
    private HtmlInputTextarea descriptionInput  = new HtmlInputTextarea();
    private HtmlInputText     parentInput;
    private HtmlSelectOneMenu parentSelect;
    private ArrayList         parentSelectItems;

    //rowselection fields
    private ArrayList selectedDataverses = new ArrayList();
    private ArrayList dataverses = new ArrayList();// internal list of retreived records.
    private boolean multiRowSelect = true;
    private HtmlInputHidden classificationId;
    private HtmlInputHidden multiRowSelector;
    private Long classId;
    private Long selectedParent;
    private ClassificationList classificationList = new ClassificationList();

    private boolean result;

    private String SUCCESS_MESSAGE   = new String("Success. The classifications and dataverses operation completed successfully.");
    private String FAIL_MESSAGE      = new String("Problems occurred during the form submission. Please see error messages below.");


    public void init() {
        System.out.println("before super init");
        super.init();
        System.out.println("cid is "+classId);
        initParentSelectItems();
        dataverses         = new ArrayList();
        selectedDataverses = new ArrayList();
        initItemBeans();
        if (classId != null) {
            initClassificationBean();
            initSelectedItemBeans();
        }
        result = false;
    }

    public ClassificationList getClassificationList() {
        return classificationList;
    }

    public void setClassificationList(ClassificationList classificationList) {
        this.classificationList = classificationList;
    }

    private void initClassificationBean() {
        VDCGroup vdcGroup = vdcGroupService.findById(classId);
        nameInput.setValue(vdcGroup.getName());
        descriptionInput.setValue(vdcGroup.getDescription());
        if (vdcGroup.getParent() != null)
            selectedParent = vdcGroup.getParent();
    }

    private void initSelectedItemBeans() {
         VDCGroup vdcGroup = vdcGroupService.findById(classId);
         List list = (List)vdcGroup.getVdcs();
         Iterator iterator = list.iterator();
         while(iterator.hasNext()) {
             VDC vdc = (VDC)iterator.next();
             DataverseGrouping dataverse = new DataverseGrouping(vdc.getId(), vdc.getName(), vdc.getAffiliation());
             //add something here to see if its released or not
             dataverse.setSelected(true);
             dataverse.setRestricted(vdc.isRestricted());
             selectedDataverses.add(dataverse);
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
             dataversegrouping.setRestricted(vdc.isRestricted());
             dataverses.add(dataversegrouping);
         }
    }

    private void initParentSelectItems() {
       
        Iterator iterator     = classificationList.getClassificationUIs().iterator();
        parentSelectItems     = new ArrayList();
        SelectItem parentSelectItem = new SelectItem(new Long("0"), "Select Classification");
        parentSelectItems.add(parentSelectItem);
 
        while (iterator.hasNext()) {
            ClassificationUI classUI =(ClassificationUI)iterator.next();
            VDCGroup vdcgroup = classUI.getVdcGroup();
            if ( classId != null && ( classId.equals(vdcgroup.getId()) || classId.equals(vdcgroup.getParent()) ) ) {
                continue;
            }else if (classUI.getLevel()==3){
                continue;
            }else {
                parentSelectItem = new SelectItem(vdcgroup.getId(), vdcgroup.getName());
                parentSelectItems.add(parentSelectItem);
            }
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

    public ArrayList getParentSelectItems() {
        return parentSelectItems;
    }

    public Long getSelectedParent() {
        return this.selectedParent;
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

    public void setParentSelectItems(ArrayList parentselectitems) {
        this.parentSelectItems = parentselectitems;
    }

    public void setSelectedParent(Long selected) {
        this.selectedParent = selected;
    }


    //TODO 

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

    public Long getClassId() {
        return this.classId;
    }


    public void setClassId(Long cid) {
        this.classId = cid;
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
        result = true;
        try {
            //
            VDCGroup vdcgroup = new VDCGroup();
            vdcgroup.setName((String)nameInput.getValue());
            vdcgroup.setDescription((String)descriptionInput.getValue());
            vdcgroup.setParent((Long)parentSelect.getValue());
            vdcgroup.setDisplayOrder(100);
            vdcGroupService.create(vdcgroup);
            classificationId.setValue(vdcgroup.getId());
            update_action();
            getExternalContext().getFlash().put("successMessage",SUCCESS_MESSAGE);
        } catch (Exception e) {
            getExternalContext().getFlash().put("warningMessage",FAIL_MESSAGE);
        } finally {
            return "/networkAdmin/ManageClassificationsPage.xhtml?faces-redirect=true";
        }
        
    }

    public String update_action() {
        //now add all of these dataverses to the parent
        //Iterator msgiterator = FacesContext.getCurrentInstance().getMessages("AddClassificationsPageForm");
        //if (msgiterator.hasNext())
            //msgiterator.remove();
        result = true;
 
            Long[] vdcs         = new Long[selectedDataverses.size()];
            Iterator iterator   = selectedDataverses.iterator();
            int count           = 0;
            while (iterator.hasNext()) {
                DataverseGrouping dataversegrouping = (DataverseGrouping)iterator.next();
                vdcs[count] =  new Long(dataversegrouping.getId());
                count++;
            }
            VDCGroup vdcgroup = vdcGroupService.findById((Long)classificationId.getValue());
            vdcgroup.setName((String)nameInput.getValue());
            vdcgroup.setDescription((String)descriptionInput.getValue());
            Long selectedValue = (Long)parentSelect.getValue();
            // If the VDCGroup has no parent, then this is a top level classification,
            // which should contain no dataverses.
            if (selectedValue.equals(new Long("0"))) {
                vdcgroup.setParent(null);
                vdcgroup.getVdcs().clear();
                vdcGroupService.updateVdcGroup(vdcgroup);
                vdcs = new Long[0];
                vdcGroupService.updateWithVdcs(vdcgroup, vdcs);               

            } else {
                vdcgroup.setParent((Long)parentSelect.getValue());
                vdcGroupService.updateVdcGroup(vdcgroup);
                vdcGroupService.updateWithVdcs(vdcgroup, vdcs);
            }
            
      
            getExternalContext().getFlash().put("successMessage",SUCCESS_MESSAGE);
            return "/networkAdmin/ManageClassificationsPage.xhtml?faces-redirect=true";
        
    }

    // **************** VALIDATORS ****************** -->
    public void validateClassificationName(FacesContext context, UIComponent toValidate, Object value) {
         String newValue = (String) value;
         VDCGroup group = (VDCGroup)vdcGroupService.findByName(newValue);
        if (newValue == null || newValue.trim().length() == 0) {
            FacesMessage message = new FacesMessage("The field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
            context.renderResponse();
        } else if (vdcGroupService.findByName(newValue.trim()) != null) {
            if (!group.getId().equals(classId)) {
             FacesMessage message = new FacesMessage("This name is in use. Please enter a unique classification name.");
             context.addMessage(toValidate.getClientId(context), message);
             context.renderResponse();
            }
        }
    }

}
