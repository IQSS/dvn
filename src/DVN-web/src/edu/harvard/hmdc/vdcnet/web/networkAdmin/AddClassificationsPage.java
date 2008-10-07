/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import edu.harvard.hmdc.vdcnet.admin.LoginAffiliate;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;

/**
 * @author wbossons
 */
public class AddClassificationsPage extends VDCBaseBean implements Serializable {
    @EJB VDCGroupServiceLocal vdcGroupService;

    private HtmlInputText     nameInput;
    private HtmlInputTextarea descriptionInput;
    private HtmlInputText     parentInput;
    private HtmlSelectOneMenu parentSelect;
    private SelectItem[]      parentSelectItems;
    
    
    public void init() {
        super.init();
        initParentSelectItems();
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

    public String add_action() {
        VDCGroup vdcgroup = new VDCGroup();
        vdcgroup.setName((String)nameInput.getValue());
        vdcgroup.setDescription((String)descriptionInput.getValue());
        vdcgroup.setParent(new Long((String)parentSelect.getValue()));
        vdcGroupService.create(vdcgroup);
        return "Success";
    }


    
}
