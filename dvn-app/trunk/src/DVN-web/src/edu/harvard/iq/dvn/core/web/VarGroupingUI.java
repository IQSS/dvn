/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.visualization.VarGrouping;
import java.util.Collection;


/**
 *
 * @author skraffmiller
 */
public class VarGroupingUI {


    private VarGrouping varGrouping;
    private Collection <VarGroupTypeUI> varGroupTypesUI;
    private Collection <VarGroupUI> varGroupUI;
    private Long selectedGroupId;

    public Collection<VarGroupTypeUI> getVarGroupTypesUI() {
        return varGroupTypesUI;
    }

    public void setVarGroupTypesUI(Collection<VarGroupTypeUI> varGroupTypesUI) {
        this.varGroupTypesUI = varGroupTypesUI;
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

    public Collection<VarGroupTypeUI> getVarGroupTypes() {
        return (Collection<VarGroupTypeUI>) this.varGroupTypesUI;
    }

    public void setVarGroupTypes(Collection<VarGroupTypeUI> varGroupTypes) {
        this.varGroupTypesUI = varGroupTypes;
    }

    public Long getSelectedGroupId() {
        return selectedGroupId;
    }

    public void setSelectedGroupId(Long selectedGroupId) {
        this.selectedGroupId = selectedGroupId;
    }

}
