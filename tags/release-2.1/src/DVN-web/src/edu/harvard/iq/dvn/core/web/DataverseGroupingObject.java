/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author wbossons
 */
public class DataverseGroupingObject extends NodeGroupingObject {
     private DataverseGrouping grouping;

    public DataverseGroupingObject(DefaultMutableTreeNode defaultMutableTreeNode) {
        super(defaultMutableTreeNode);
    }

    public DataverseGrouping getGrouping() {
        return grouping;
    }

    public void setGrouping(DataverseGrouping grouping) {
        this.grouping = grouping;
    }

}
