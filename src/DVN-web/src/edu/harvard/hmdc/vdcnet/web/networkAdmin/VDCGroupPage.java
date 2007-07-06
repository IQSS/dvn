/*
 * VDCGroupPage.java
 *
 * Created on June 20, 2007, 2:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import com.sun.rave.web.ui.component.AddRemove;
import com.sun.rave.web.ui.model.MultipleSelectOptionsList;
import com.sun.rave.web.ui.model.Option;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author wbossons
 */
public class VDCGroupPage extends VDCBaseBean {
     @EJB VDCGroupServiceLocal vdcGroupService;
     @EJB VDCServiceLocal vdcService;
    /** Creates a new instance of VDCGroupPage */
    public VDCGroupPage() {
    }

    //the model for the detail page
    private DataModel model;
    private List groupList;
    
    //the model for the edit page
    private DataModel editModel;
    private List     editList;
    private VDCGroup vdcGroup;

    public void init() {
        super.init();
        if (vdcGroupId != null){
            HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            request.setAttribute("vdcGroupId", vdcGroup.getId());
        }
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String vdcGroupIdParam=request.getParameter("vdcGroupId");
        if (vdcGroupIdParam==null) {
            Iterator iter = request.getParameterMap().keySet().iterator();
            while (iter.hasNext()) {
                Object key = (Object) iter.next();
                if ( key instanceof String && ((String) key).indexOf("vdcGroupId") != -1 ) {
                    vdcGroupIdParam = request.getParameter((String)key);
                    Long longid = new Long(vdcGroupIdParam);
                    if (longid != 0) {
                        this.setVdcGroupId(longid);
                        this.setVdcGroup(vdcGroupService.findById(this.getVdcGroupId()));
                    }
                    break;
                }
            }
        }
    }
    private Long vdcGroupId;
    
    public Long getVdcGroupId() {
        return this.vdcGroupId;
    }
    
    public void setVdcGroupId(Long vdcgroupid) {
        this.vdcGroupId = vdcgroupid;
    }
    public String edit() {
        VDCGroup vdcgroup = (VDCGroup)model.getRowData();
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.setAttribute("vdcGroupId", vdcgroup.getId());
        this.setVdcGroup(vdcgroup);
        return "dvgroup_edit";
    }
    
    public String addGroup() {
        this.setName("");
        this.setDescription("");
        VDCGroup vdcgroup = new VDCGroup();
        vdcgroup.setName("");
        vdcgroup.setDescription("");
        vdcGroupService.create(vdcgroup);
        this.setVdcGroup(vdcgroup);
        HttpServletRequest request  = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.setAttribute("vdcGroupId", this.getVdcGroup().getId());
        return "dvgroup_add";
    }
    
    public String add() {
        String msg = SUCCESS_MESSAGE;
        success = true;
        try {
           this.getVdcGroup().setName(this.getName());
           this.getVdcGroup().setDescription(this.getDescription());
           if (!addRemoveList.getSelectedValues().equals("")) {
               vdcGroupService.updateWithVdcs(this.getVdcGroup(), addRemoveList.getValueAsStringArray(FacesContext.getCurrentInstance()));
           } else {
                vdcGroupService.updateVdcGroup(this.getVdcGroup());
           }
        } catch (Exception e) {
            System.out.println("An error occurred ... ");
        } finally {
            return "success";
        }
    }
    
   public String update() {
        String msg = SUCCESS_MESSAGE;
        success = true;
        try {
           VDCGroup vdcgroup = this.getVdcGroup();
           System.out.println(this.getVdcGroup().getName());
           if (addRemoveList.getValueAsStringArray(FacesContext.getCurrentInstance()).length > 0) {
               String[] selectValues = addRemoveList.getValueAsStringArray (getFacesContext ());
               System.out.println(selectValues.length + " and the val pos 0 is " + selectValues[0]);
               vdcGroupService.updateWithVdcs(vdcgroup, addRemoveList.getValueAsStringArray(FacesContext.getCurrentInstance()));
           } //else {
                vdcGroupService.updateVdcGroup(vdcgroup);
           //}
        } catch (Exception e) {
            System.out.println("An error occurred ... ");
        } finally {
            return "success";
        }
    }
    
    public void setVDCGroupFromRequestParam() {
        VDCGroup VdcGroup = getVDCGroupFromRequestParam();
        setVdcGroup(VdcGroup);
    }
    
    public VDCGroup getVDCGroupFromRequestParam() {
        VDCGroup o = this.vdcGroupService.findById((Long)model.getRowData());
        return o;
    }
    
    public String save() {
        String msg = SUCCESS_MESSAGE;
        success    = true;
        try {
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
    
    public DataModel getVDCGroups() {
        try {
            List list = this.getGroupList();
            if (!list.isEmpty())
                model = new ListDataModel(list);
            else
                model = null;
        } catch (Exception e) {
            System.out.println("An error occurred while getting the VDC Groups . . .");
        } finally {
            return model;
        }
    }

    public DataModel getDetailVDCGroups() {
        return model;
    }

    public void setDetailVDCGroups(Collection<VDCGroup> m) {
        model = new ListDataModel(new ArrayList(m));
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
    
   //The edit page
    public VDCGroup getVdcGroup() {
        return this.vdcGroup;
    }
    
    public void setVdcGroup(VDCGroup vdcgroup) {
        this.vdcGroup = vdcgroup;
    }
    
    /** add/edit add and remove widge
     *
     *
     * populate the select lists
     *
     * @author wbossons
     */
    private AddRemove addRemoveList;

    public AddRemove getAddRemoveList() {
        initAddRemoveList();
        return addRemoveList;
    }

    public void setAddRemoveList(AddRemove addremovelist) {
        this.addRemoveList = addremovelist;
    }
    
    public void initAddRemoveList() {
        /** new */
        addRemoveList = new AddRemove();
        List list = new ArrayList(vdcService.findAll());
        Iterator iterator = list.iterator();
        Option[] options = new Option[list.size()];
        int i = 0;
            while (iterator.hasNext()) {
                VDC vdc = (VDC)iterator.next();
                options[i] = new Option(Long.toString(vdc.getId().longValue()),vdc.getName());
                i++;
            }
            addRemoveListDefaultOptions.setMultiple(true);
            addRemoveListDefaultOptions.setItems(options);
            addRemoveListDefaultOptions.setSelected(this.setSelectedValues());
            addRemoveList.setSelected(addRemoveListDefaultOptions.getSelected());        
    }
    /** new */

    private Object[] setSelectedValues() {
        List list = new ArrayList(this.getVdcGroup().getVdcs());
        Iterator iterator = list.iterator();
        Object[] selectedValues = new Object[list.size()];
        int i = 0;
            while (iterator.hasNext()) {
                VDC vdc = (VDC)iterator.next();
                selectedValues[i] = Long.toString(vdc.getId().longValue());
                i++;
            }
        return selectedValues;
    }
    
    private AddRemove addRemoveListDefaultOptions = new AddRemove();

    public AddRemove getAddRemoveListDefaultOptions() {
        return addRemoveListDefaultOptions;
    }

    public void setAddRemoveListDefaultOptions(AddRemove msol) {
        this.addRemoveListDefaultOptions = msol;
    }
    
   /** end new */ 
    private String name;
    
    public String getName() {
        if (name == null) {
            setName(this.getVdcGroup().getName());
        }
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    private String description;
    
    public String getDescription() {
        if (description == null) {
            setDescription(this.getVdcGroup().getDescription());
        }
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    /** value change listener
     *
     * changeName
     * 
     * This method detects the changed name in the
     * edit page. It operates on the
     * VDCGroup class' name property
     *  to ready it for submission.
     *
     * @author wbossons
     */
    public void changeName(ValueChangeEvent event) {
            String newValue = (String)event.getNewValue();
            if (this.getVdcGroup() != null) {
                this.getVdcGroup().setName(newValue);
            }
            this.setName(newValue);
    }
    
    /** value change listener
     *
     * changeDescription
     * 
     * This method detects the changed name in the
     * edit page. It operates on the
     * VDCGroup class' name property
     *  to ready it for submission.
     *
     * @author wbossons
     */
    public void changeDescription(ValueChangeEvent event) {
            String newValue = (String)event.getNewValue();
            if (this.getVdcGroup() != null) { //this is an edit
            this.getVdcGroup().setDescription(newValue);
            } 
            //both add and edit do the following
            this.setDescription(newValue);
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
