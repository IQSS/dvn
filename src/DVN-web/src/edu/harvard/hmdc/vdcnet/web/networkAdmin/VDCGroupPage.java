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
import com.sun.rave.web.ui.model.Option;
import edu.harvard.hmdc.vdcnet.util.ExceptionMessageWriter;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author wbossons
 */
public class VDCGroupPage extends VDCBaseBean implements java.io.Serializable  {
     @EJB VDCGroupServiceLocal vdcGroupService;
     @EJB VDCServiceLocal vdcService;
     @EJB VDCNetworkServiceLocal vdcNetworkService;
    /** Creates a new instance of VDCGroupPage */
    public VDCGroupPage() {
    }

    private Long vdcGroupId;
    //the model for the detail page
    private DataModel  model;
    private List       groupList;
    
    //the model for the edit page
    private DataModel editModel;
    private List      editList;
    private VDCGroup  vdcGroup;
    private Long      defaultDisplayNumber;
    private Long      _DISPLAY_COLUMNS = new Long("4");
    private String    description;
    private String    name;

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
                if ( key instanceof String && ((String) key).indexOf("vdcGroupId") != -1 && !request.getParameter((String)key).equals("")) {
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
        return "dvgroup_add";
    }
    
   public String update() {
        String msg    = SUCCESS_MESSAGE;
        success       = true;
        String result = "success";
        try {
            VDCGroup vdcgroup = null;
            if (this.getVdcGroup() != null) {
                vdcgroup = this.getVdcGroup();
                vdcgroup.setDefaultDisplayNumber(vdcgroup.getDefaultDisplayNumber());
            } else {
                vdcgroup = new VDCGroup();
                vdcGroupService.create(vdcgroup);
                this.setVdcGroup(vdcgroup);
                vdcgroup.setDisplayOrder(vdcGroupService.getNextInOrder());
                vdcgroup.setDefaultDisplayNumber(getVDCRequestBean().getVdcNetwork().getDefaultDisplayNumber());
            }
           vdcgroup.setName(this.getName());
           vdcgroup.setDescription(this.getDescription());
           if (vdcgroup.getName() == "") {
               msg    = "Validation Error: The name field is required.";
               result = "failed";
               success = false;
               FacesContext.getCurrentInstance().addMessage("content:vDCGroupEditView:vdcGroupEditForm:name", new FacesMessage(msg));
               if (vdcgroup.getId() != null) {
                HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
                request.setAttribute("vdcGroupId", vdcgroup.getId());
                this.setVdcGroup(vdcgroup);
               } else {
                    vdcGroupService.removeVdcGroup(vdcgroup);
               }
           } else {
               FacesContext.getCurrentInstance().addMessage("vdcGroupEditForm:btnSave", new FacesMessage(msg));
               if (addRemoveList.getValueAsStringArray(FacesContext.getCurrentInstance()).length > 0) {
                   String[] selectValues = addRemoveList.getValueAsStringArray (getFacesContext ());
                   vdcGroupService.updateWithVdcs(vdcgroup, addRemoveList.getValueAsStringArray(FacesContext.getCurrentInstance()));
               } 
               vdcGroupService.updateVdcGroup(vdcgroup);
           }
        } catch (Exception e) {
            ExceptionMessageWriter.removeGlobalMessage(SUCCESS_MESSAGE);
            msg     = "An error occurred. The Dataverse group was not saved.";
            result  ="failed";
            FacesContext.getCurrentInstance().addMessage("vdcGroupEditForm:btnSave", new FacesMessage(msg));
        } finally {
            return result;
        }
    }
      
    public String save() {
        String msg = SUCCESS_MESSAGE;
        success    = true;
        getVDCRequestBean().getVdcNetwork().setDefaultDisplayNumber(defaultDisplayNumber);
        vdcNetworkService.updateDefaultDisplayNumber(getVDCRequestBean().getVdcNetwork());
        try {
            List list = (List)model.getWrappedData();
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                VDCGroup vdcgroup = (VDCGroup)iterator.next();
                this.vdcGroupService.updateVdcGroup(vdcgroup);
                if (vdcgroup.getSelected() == true) {
                    // See tbd below  int order = vdcgroup.getDisplayOrder();
                    this.vdcGroupService.removeVdcGroup(vdcgroup);
                    //and reset all of the subsequent groups
                    //this.vdcGroupService.updateGroupOrder(order); // TBD
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
    
    //getters
    public Long getVdcGroupId() {
        return this.vdcGroupId;
    }
    
    public DataModel getVDCGroups() {
        try {
            List list = this.getGroupList();
            if (model == null && !list.isEmpty()) {
                model = new ListDataModel(list);
            } else if (model != null) {
                return model;
            } else {
                model = null;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while getting the VDC Groups . . .");
        } finally {
            return model;
        }
    }
    
    public VDCGroup getVDCGroupFromRequestParam() {
        VDCGroup o = this.vdcGroupService.findById((Long)model.getRowData());
        return o;
    }

    public DataModel getDetailVDCGroups() {
        return model;
    }
    
    public List getGroupList() {
        if (groupList == null)
            groupList = (List)vdcGroupService.findAll();
        else 
            groupList = groupList;
        return this.groupList;
    }
    
     public VDCGroup getVdcGroup() {
        return this.vdcGroup;
    }

     public String getName() {
        if (name == null && this.getVdcGroup() != null) {
            setName(this.getVdcGroup().getName());
        }
        return this.name;
    }
     
     public String getDescription() {
        if (description == null && this.getVdcGroup() != null) {
            setDescription(this.getVdcGroup().getDescription());
        }
        return this.description;
    }
     
     public Long getDefaultDisplayNumber() {
         return getVDCRequestBean().getVdcNetwork().getDefaultDisplayNumber();
     }
     
    //setters
    public void setVdcGroupId(Long vdcgroupid) {
        this.vdcGroupId = vdcgroupid;
    }
    
    public void setDetailVDCGroups(Collection<VDCGroup> m) {
        model = new ListDataModel(new ArrayList(m));
    }
    
    public void setVDCGroupFromRequestParam() {
        VDCGroup VdcGroup = getVDCGroupFromRequestParam();
        setVdcGroup(VdcGroup);
    }
    
    public void setGroupList(List grouplist) {
        this.groupList = grouplist;
    }
    
    public void setVdcGroup(VDCGroup vdcgroup) {
        this.vdcGroup = vdcgroup;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setDefaultDisplayNumber(Long defaultdisplaynumber) {
        this.defaultDisplayNumber = defaultdisplaynumber;
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
    
    private Object[] setSelectedValues() {
             Object[] selectedValues = null;
        if (this.getVdcGroup() != null ){
            List list = new ArrayList(this.getVdcGroup().getVdcs());
            Iterator iterator = list.iterator();
            selectedValues = new Object[list.size()];
            int i = 0;
                while (iterator.hasNext()) {
                    VDC vdc = (VDC)iterator.next();
                    selectedValues[i] = Long.toString(vdc.getId().longValue());
                    i++;
                }
        } else {
            selectedValues = new Object[0];
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
            this.setDescription(newValue);
    }
    
    //VALIDATORS
    
    /** validateDivisible
     * 
     *  validates whether the number is divisible by the specified column
     * number. Note column number is a constant in this class.
     *  
     * @author Wendy Bossons
     */
    
    public void validateDivisible(FacesContext context, 
                          UIComponent toValidate,
                          Object value) {
        Long longValue = (Long) value;
        if (longValue % _DISPLAY_COLUMNS != 0) {
             ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("The number of dataverses to display must be divisible by 4.");
            context.addMessage(toValidate.getClientId(context), message);
            context.renderResponse();
        }
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
