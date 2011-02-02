/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;


import edu.harvard.iq.dvn.core.visualization.VarGroupType;

/**
 *
 * @author skraffmiller
 */
public class VarGroupTypeUI {
    private VarGroupType varGroupType;
    private boolean enabled;
    private boolean editMode;
    private boolean selected;
    private boolean deletable;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public VarGroupType getVarGroupType() {
        return varGroupType;
    }

    public void setVarGroupType(VarGroupType varGroupType) {
        this.varGroupType = varGroupType;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }


}
