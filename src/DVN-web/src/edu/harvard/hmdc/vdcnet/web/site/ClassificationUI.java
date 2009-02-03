/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.site;

import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;

/**
 *
 * @author Ellen Kraffmiller
 */
public class ClassificationUI {
    
    public ClassificationUI(VDCGroup vdcGroup) {
        this.vdcGroup = vdcGroup;
    }

    private VDCGroup vdcGroup;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public VDCGroup getVdcGroup() {
        return vdcGroup;
    }

    public void setVdcGroup(VDCGroup vdcGroup) {
        this.vdcGroup = vdcGroup;
    }

    public int getIndentLevel() {
        if (getVdcGroup().getParent()==null) {
            return 0;
        }
        return 1;
    }

    
    

}
