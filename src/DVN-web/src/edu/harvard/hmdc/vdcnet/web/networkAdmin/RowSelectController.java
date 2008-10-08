/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlInputHidden;
import java.util.ArrayList;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.DataverseGrouping;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;

/**
 * <p>The AddDataverseClassificationsPage is responsible for handling the RowSelectorEvent
 * that is fire from the rowSelector component.  This simple class keeps
 * a list of selected.  A user can also change the selection more of the
 * row Selector component. </p>
 * @author wbossons
 */

public class RowSelectController extends VDCBaseBean implements Serializable {

    @EJB VDCServiceLocal vdcService;
    @EJB VDCGroupServiceLocal vdcGroupService;
         // list of selected dataverses
    private ArrayList selectedDataverses;
     // internal list of retreived records.
    private ArrayList dataverses;

    // flat to indicate multiselect row enabled.
    private boolean multiRowSelect;

    private HtmlInputHidden classificationId;
    private Long cid;


    public void init() {
        super.init();
        dataverses         = new ArrayList();
        selectedDataverses = new ArrayList();
        initItemBeans();
    }
    
     private void initItemBeans() {
         List list = (List)vdcService.findAll();
         Iterator iterator = list.iterator();
         while(iterator.hasNext()) {
             VDC vdc = (VDC)iterator.next();
             DataverseGrouping dataversegrouping = new DataverseGrouping(vdc.getId(), vdc.getName(), "No Affiliation");
             System.out.println("RowSelectController:dataversegrouping: " + dataversegrouping.toString());
             dataverses.add(dataversegrouping);
         }
    }



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
    //            dataverse.setSelected(false);
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
    
    /** add_action
     *
     * Add the dataverse to the classification
     *
     * @return String success or failure
     */
    public String add_action() {
        //now add all of these dataverses to the parent
        //vdcgroup_vdcs
        Long[] vdcs         = new Long[selectedDataverses.size()];
        Iterator iterator   = selectedDataverses.iterator();
        int count           = 0;
        while (iterator.hasNext()) {
            DataverseGrouping dataversegrouping = (DataverseGrouping)iterator.next();
            vdcs[count] =  new Long(dataversegrouping.getId());
            count++;
        }
        System.out.println("RowSelectController: class Id is " + classificationId.getValue());
        VDCGroup vdcgroup = vdcGroupService.findById((Long)classificationId.getValue());
        vdcGroupService.updateWithVdcs(vdcgroup, vdcs);
        return "Success";
    }

    
}
