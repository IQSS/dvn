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
    private Collection <VarGroupTypeUI> varGroupTypesUI;
    private Collection <VarGroupUI> varGroupUI;
    private List <SelectItem> varGroupTypesSelect = new ArrayList();
    private boolean showVariables = false;
    private List <DataVariableUI> dataVariableUIList = new ArrayList();






    private Long selectedGroupId;

    public Collection<VarGroupTypeUI> getVarGroupTypesUI() {
        return varGroupTypesUI;
    }

    public void setVarGroupTypesUI(Collection<VarGroupTypeUI> varGroupTypesUI) {
        this.varGroupTypesUI = varGroupTypesUI;
    }

    public void setVarGroupTypesUI(VarGroupingUI varGroupingUI) {
        List<VarGroupTypeUI> varGroupTypeUIList = new ArrayList();
        VarGrouping varGroupingIn = varGroupingUI.getVarGrouping();
        varGroupingIn.getVarGroupTypes();

                   List <VarGroupType> varGroupTypes = new ArrayList();
                   List <SelectItem> selectGroupTypes = new ArrayList();
                   selectGroupTypes.add(new SelectItem(new Long(0), "Select a Filter Type" ) );
                   varGroupTypes = (List<VarGroupType>) varGroupingIn.getVarGroupTypes();
                    if (varGroupTypes !=null ) {
                       for(VarGroupType varGroupType: varGroupTypes){
                           VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
                           varGroupTypeUI.setVarGroupType(varGroupType);
                           varGroupTypeUI.setEnabled(true);
                           selectGroupTypes.add(new SelectItem(varGroupType.getId(), varGroupType.getName() ) );
                           varGroupTypeUI.getVarGroupType().getName();
                           varGroupTypeUIList.add(varGroupTypeUI);
                       }
                    }
        this.varGroupTypesSelect = selectGroupTypes;
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

    public boolean isShowVariables() {
        return showVariables;
    }

    public void setShowVariables(boolean showVariables) {
        this.showVariables = showVariables;
    }
    
    public List<DataVariableUI> getDataVariableUIList() {
        return dataVariableUIList;
    }

    public void setDataVariableUIList(List<DataVariableUI> dataVariableUIList) {
        this.dataVariableUIList = dataVariableUIList;
    }
}
