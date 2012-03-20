/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.visualization.VarGroup;
import edu.harvard.iq.dvn.core.visualization.VarGroupType;
import edu.harvard.iq.dvn.core.visualization.VarGrouping;
import edu.harvard.iq.dvn.core.visualization.VisualizationServiceLocal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;


/**
 *
 * @author skraffmiller
 */
public class VarGroupingUI {


    private VarGrouping varGrouping;
    private List <VarGroupTypeUI> varGroupTypesUI;
    private Collection <VarGroupUI> varGroupUI;
    private VarGroupUIList varGroupUIList;

    private List <SelectItem> varGroupTypesSelect = new ArrayList();
    private Long selectedGroupId;

    public List<VarGroupTypeUI> getVarGroupTypesUI() {
        return varGroupTypesUI;
    }

    public void setVarGroupTypesUI(List<VarGroupTypeUI> varGroupTypesUI) {
        this.varGroupTypesUI = varGroupTypesUI;
    }

    public void setVarGroupTypesUI() {
        List<VarGroupTypeUI> varGroupTypeUIList = new ArrayList();

           List <VarGroupType> varGroupTypes = new ArrayList();

           varGroupTypes = (List<VarGroupType>) this.getVarGrouping().getVarGroupTypes();
           if (varGroupTypes !=null ) {
                for(VarGroupType varGroupType: varGroupTypes){
                    VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
                    varGroupTypeUI.setVarGroupType(varGroupType);
                    varGroupTypeUI.setEnabled(true);                           
                    varGroupTypeUI.getVarGroupType().getName();
                    varGroupTypeUIList.add(varGroupTypeUI);
                }
           }

        this.varGroupTypesUI = varGroupTypeUIList;
    }
    
    public Collection<VarGroupUI> getVarGroupUI() {
        return varGroupUI;
    }

    public void setVarGroupUI(Collection<VarGroupUI> varGroupUI) {
        this.varGroupUI = varGroupUI;
    }

    public VarGrouping getVarGrouping() {
        return varGrouping;
    }

    public void setVarGrouping(VarGrouping varGrouping) {
        this.varGrouping = varGrouping;
    }



    public Long getSelectedGroupId() {
        return selectedGroupId;
    }

    public void setSelectedGroupId(Long selectedGroupId) {
        this.selectedGroupId = selectedGroupId;
    }
    
    public List<SelectItem> getVarGroupTypesSelect() {
        return varGroupTypesSelect;
    }

    public void setVarGroupTypesSelect(List<SelectItem> varGroupTypesSelect) {
        this.varGroupTypesSelect = varGroupTypesSelect;
    }

    public VarGroupUIList getVarGroupUIList() {
        return varGroupUIList;
    }

    public void setVarGroupUIList(VarGroupUIList varGroupUIList) {
        this.varGroupUIList = varGroupUIList;
    }
}
