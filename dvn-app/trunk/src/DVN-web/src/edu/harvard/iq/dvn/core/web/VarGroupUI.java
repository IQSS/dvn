/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.visualization.VarGroup;
import java.util.List;

/**
 *
 * @author skraffmiller
 */
public class VarGroupUI {
    private VarGroup varGroup;
    private List <DataVariable> dvMappings;
    private List <VarGroupTypeUI> varGroupTypesSelected;

    public List<DataVariable> getDvMappings() {
        return dvMappings;
    }

    public void setDvMappings(List<DataVariable> dvMappings) {
        this.dvMappings = dvMappings;
    }

    public VarGroup getVarGroup() {
        return varGroup;
    }

    public void setVarGroup(VarGroup varGroup) {
        this.varGroup = varGroup;
    }

    public List<VarGroupTypeUI> getVarGroupTypesSelected() {
        return varGroupTypesSelected;
    }

    public void setVarGroupTypesSelected(List<VarGroupTypeUI> varGroupTypesSelected) {
        this.varGroupTypesSelected = varGroupTypesSelected;
    }


}
