/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;

import com.icesoft.faces.component.tree.IceUserObject;
import javax.swing.tree.DefaultMutableTreeNode;



/**
 *
 * @author wbossons
 */
public class NodeGroupingObject extends IceUserObject {

    public NodeGroupingObject(DefaultMutableTreeNode defaultMutableTreeNode) {
        super(defaultMutableTreeNode);
        setLeafIcon("tree_document.gif");
        setBranchContractedIcon("tree_folder_closed.gif");
        setBranchExpandedIcon("tree_folder_open.gif");
        setExpanded(true);
    }

}
