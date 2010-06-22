/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.site;

import edu.harvard.iq.dvn.core.vdc.VDCGroup;

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
    private static int LEVEL_TWO_INDENT      = 2;
    private static int LEVEL_THREE_INDENT     = 5;
    protected int textIndent                  = 0;
    protected Integer totalSubclassifications = 0;
    protected Long parentId;
     // Images used to represent expand/contract, spacer by default
    protected static final String DEFAULT_IMAGE_DIR = "/resources/icefaces/dvn_rime/css-images/";
    String SPACER_IMAGE = "tree_line_blank.gif";
    public static final String CONTRACT_IMAGE           = "tree_nav_top_close_no_siblings.gif";
    public static final String EXPAND_IMAGE             = "tree_nav_top_open_no_siblings.gif";

    private boolean expanded = true;
    private boolean visible  = false;

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
        if (level > 1) {
            indentStyle = "childRowIndentStyle";
        }
        return indentStyle;
    }

     /**
     * Get the value of textIndent
     *
     * @return the value of textIndent
     */
    public int getTextIndent() {
        if (level == 2 || level == 3) {
            textIndent = (level == 2) ? LEVEL_TWO_INDENT : LEVEL_THREE_INDENT;
        }
        return textIndent;
    }

    /**
     * Set the value of textIndent
     *
     * @param textIndent new value of textIndent
     */
    public void setTextIndent(int textIndent) {
        this.textIndent = textIndent;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Get the value of parentId
     *
     * @return the value of parentId
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * Set the value of parentId
     *
     * @param parentId new value of parentId
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }


    public String getExpandContractImage() {
        if (this.totalSubclassifications > 0) {
            String dir = DEFAULT_IMAGE_DIR;
            String img = expanded ? CONTRACT_IMAGE : EXPAND_IMAGE;
            return dir + img;
        } else {
            return DEFAULT_IMAGE_DIR + SPACER_IMAGE;
        }
    }

    /**
     * Get the value of totalSubclassifications
     *
     * @return the value of totalSubclassifications
     */
    public Integer getTotalSubclassifications() {
        return totalSubclassifications;
    }

    /**
     * Set the value of totalSubclassifications
     *
     * @param totalSubclassifications new value of totalSubclassifications
     */
    public void setTotalSubclassifications(Integer totalSubclassifications) {
        this.totalSubclassifications = totalSubclassifications;
    }

    //utils 
    public String toString() {
        String groupToString = new String("");
              groupToString+="[ name = " + vdcGroup.getName() + "; ";
              groupToString+=" level = " + level + "; ";
              groupToString+=" expanded = " + expanded + "]; " + "\r\n";
              return  groupToString;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ClassificationUI &&
        this.vdcGroup.getName().equals(((ClassificationUI)obj).vdcGroup.getName()));
    }
     

}
