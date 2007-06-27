/*
 * VDCGroupPage.java
 *
 * Created on June 20, 2007, 2:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

/**
 *
 * @author wbossons
 */
public class VDCGroupPage extends VDCBaseBean {
     @EJB VDCGroupServiceLocal vdcGroupService;
     
    /** Creates a new instance of VDCGroupPage */
    public VDCGroupPage() {
    }

    private VDCGroup VDCGroup;

    private DataModel model;
    
    private List groupList;

    public void init() {
        super.init();
        
    }
    
    public VDCGroup getVDCGroup() {
        return VDCGroup;
    }

    public void setVDCGroup(VDCGroup VDCGroup) {
        this.VDCGroup = VDCGroup;
    }

    public DataModel getDetailVDCGroups() {
        return model;
    }

    public void setDetailVDCGroups(Collection<VDCGroup> m) {
        model = new ListDataModel(new ArrayList(m));
    }

    public String save() {
        String msg = SUCCESS_MESSAGE;
        success    = true;
        try {
            model.getRowCount();
            List list = (List)model.getWrappedData();
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                VDCGroup vdcgroup = (VDCGroup)iterator.next();
                this.vdcGroupService.updateVdcGroup(vdcgroup);
                if (vdcgroup.getSelected() == true) {
                    this.vdcGroupService.removeVdcGroup(vdcgroup);
                }
            }
            
        } catch (Exception e) {
            msg = "An error occurred: " + e.getCause().toString();
            success = false;
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
            return "result";
        }
    }
    
    
    /** addGroup
     *
     * a method to add more vdc groups
     *
     * @return success a string to be used for navigation.xml
     *
     * @author wbossons
     *
     */
    
    public String addGroup() {
        //add some code to save the order
        return "success";
    }
    
    
    public DataModel getVDCGroups() {
        model = null;
        try {
            List list = this.getGroupList();
            model = new ListDataModel(list);
        } catch (Exception e) {
            //addErrorMessage(e.getCause().toString());
        } finally {
            return model;
        }
    }
    
    public List getGroupList() {
        if (groupList == null)
            groupList = (List)vdcGroupService.findAll();
        else 
            groupList = groupList;
        return this.groupList;
    }
    
    public void setGroupList(List grouplist) {
        this.groupList = grouplist;
    }

    /** some helper methods
     *
     *
     *
     * @author wbossons
     */
    
    /** value change listener
     *
     * changeSelect
     * 
     * This method detects the selected
     * delete state. It operates on the
     * VDCGroup class' transient property,
     * selected and resets the dataModel
     * to ready it for submission.
     *
     * @author wbossons
     */
    public void changeSelect(ValueChangeEvent event) {
            Boolean newValue = (Boolean)event.getNewValue();
            VDCGroup vdcgroup = (VDCGroup)this.getVDCGroups().getRowData();
            vdcgroup.setSelected(newValue.booleanValue());
            resetWrappedData(vdcgroup, groupList);
    }
    
    /** value change listener
     *
     * changeOrder
     * 
     * This method detects the changed display
     * order input. It operates on the
     * VDCGroup class' displayorder property
     * and resets the dataModel
     * to ready it for submission.
     *
     * @author wbossons
     */
    public void changeOrder(ValueChangeEvent event) {
            Integer newValue = (Integer)event.getNewValue();
            VDCGroup vdcgroup = (VDCGroup)this.getVDCGroups().getRowData();
            vdcgroup.setDisplayOrder(newValue.intValue());
            resetWrappedData(vdcgroup, groupList);
    }
    
    /** commonly used iteration
     * (by change listeners)
     *
     * @param group - The group data that has changed.
     * @param list - The group list that populates the data model. 
     * The list is the same as the class' groupList. This method operates
     * on both this class' groupList and its data model.
     *
     * @author wbossons
     */
    public void resetWrappedData(VDCGroup group, List list) {
        VDCGroup localgroup = group;
        List locallist = list;
        Iterator iterator = locallist.iterator();
        while (iterator.hasNext()) {
            VDCGroup itgroup = (VDCGroup)iterator.next();
            if (itgroup.getId() == localgroup.getId())
                this.groupList.set(this.groupList.indexOf(itgroup), localgroup);
        }
        this.model.setWrappedData(groupList);
    }
    
    private String SUCCESS_MESSAGE = new String("Update Successful! Go to the home page to see your changes.");
    /**
     * Holds value of property success.
     */
    private boolean success;

    /**
     * Getter for property success.
     * @return Value of property success.
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Setter for property success.
     * @param success New value of property success.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
}
