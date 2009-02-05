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
    
    public ClassificationUI(VDCGroup vdcGroup, int level ) {
        this.vdcGroup = vdcGroup;
         this.level = level;
    }

  

    private VDCGroup vdcGroup;
    private boolean selected;
    private int level;

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }  

    public String getIndentStyle() {
        String indentStyle="";
        if (level>1) {
            indentStyle = ".childRowIndentStyle";
        }
        return indentStyle;
    }
     

}
