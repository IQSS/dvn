/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package edu.harvard.iq.dvn.core.web.component;

import com.icesoft.faces.component.tree.IceUserObject;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.web.collection.CollectionUI;
import edu.harvard.iq.dvn.core.web.site.VDCUI;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/*
 * VDCCollectionTree.java
 *
 * Created on September 19, 2006, 12:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author gdurand
 */
public class VDCCollectionTree implements java.io.Serializable  {
    
    /** Creates a new instance of VDCCollectionTree */
    public VDCCollectionTree() {
        
        // create root node with its children expanded
        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode();
        IceUserObject rootObject = new IceUserObject(rootTreeNode);
        rootObject.setText("Root Node");
        rootObject.setExpanded(true);
        rootTreeNode.setUserObject(rootObject);
      
        tree = new DefaultTreeModel(rootTreeNode);
    }
    
    public VDCCollectionTree(DefaultTreeModel tree) {
        this.tree = tree;
    }
    
    private DefaultTreeModel tree;
    
    private String vdcUrl;
    private String collectionUrl;
    private String studyUrl;
    
    private boolean includeLinkedCollections = true;
    
    private boolean includeStudies = false;
    private boolean includeCount = false;
    private List studyFilter = null;
    
    private Long collectionToBeExpanded = null;
    private boolean expandAll = false;
    
    
    //
    // Getters and Setters
    //
    
    
    public boolean isIncludeStudies() {
        return includeStudies;
    }
    
    public void setIncludeStudies(boolean includeStudies) {
        this.includeStudies = includeStudies;
    }
    
    public Long getCollectionToBeExpanded() {
        return collectionToBeExpanded;
    }
    
    public void setCollectionToBeExpanded(Long collectionToBeExpanded) {
        this.collectionToBeExpanded = collectionToBeExpanded;
    }
    
    public boolean isExpandAll() {
        return expandAll;
    }
    
    public void setExpandAll(boolean expandAll) {
        this.expandAll = expandAll;
    }
    
    public String getVDCUrl() {
        return vdcUrl;
    }
    
    public void setVDCUrl(String vdcUrl) {
        this.vdcUrl = vdcUrl;
    }
    
    public String getCollectionUrl() {
        return collectionUrl;
    }
    
    public void setCollectionUrl(String collectionUrl) {
        this.collectionUrl = collectionUrl;
    }
    
    public String getStudyUrl() {
        return studyUrl;
    }
    
    public void setStudyUrl(String studyUrl) {
        this.studyUrl = studyUrl;
    }
    
    public boolean isIncludeCount() {
        return includeCount;
    }
    
    public void setIncludeCount(boolean includeCount) {
        this.includeCount = includeCount;
    }
    
    public List getStudyFilter() {
        return studyFilter;
    }
    
    public void setStudyFilter(List studyFilter) {
        this.studyFilter = studyFilter;
    }
    public boolean isIncludeLinkedCollections() {
        return includeLinkedCollections;
    }
    
    public void setIncludeLinkedCollections(boolean includeLinkedCollections) {
        this.includeLinkedCollections = includeLinkedCollections;
    }

    //
    // Populate methods
    //
    /*
    public DefaultTreeModel populate(List vdcs) {
        
        clearNode((DefaultMutableTreeNode) tree.getRoot());
        
        Iterator iter = vdcs.iterator();
        while (iter.hasNext()) {
            VDC vdc = (VDC) iter.next();
            DefaultMutableTreeNode baseNode = newTreeNode(vdc);
            ((DefaultMutableTreeNode) tree.getRoot()).add(baseNode);
            
            if (!vdc.isRestricted()) {
                addVDC(baseNode, vdc);
            }
        }
        
        return tree;
    }
    */
    
    public DefaultTreeModel populate(VDC vdc) {
        // by default, expand the root node
        if (collectionToBeExpanded == null) {
            collectionToBeExpanded = vdc.getRootCollection().getId();
        }
        
        clearNode( getRootNode() );
        addVDC( getRootNode(), vdc);
        return tree;
    }
    
    
    
    
    //
    // Private helper methods
    //
    
    private void clearNode(DefaultMutableTreeNode node) {
        while (node.getChildCount() > 0 ) {
            node.removeAllChildren();
        }
    }
    
    private void addVDC(DefaultMutableTreeNode parentNode, VDC vdc) {
        if ( !new VDCUI(vdc).containsOnlyLinkedCollections() ) {
            addCollectionNode(parentNode, vdc.getRootCollection(), vdc, true);
        }

        if (includeLinkedCollections) {
            Iterator iter = new VDCUI(vdc).getLinkedCollections().iterator();
            while (iter.hasNext()) {
                VDCCollection lc = (VDCCollection) iter.next();
                addCollectionNode(parentNode, lc, vdc, false);
            }
        }
    }
    
    
    private Boolean addCollectionNode(DefaultMutableTreeNode parentNode, VDCCollection c, VDC vdc, boolean alwaysInclude) {
        boolean includeFlag = alwaysInclude;
        boolean expandFlag = expandAll;
        int studyCount = 0;
        
        DefaultMutableTreeNode collectionNode = newTreeNode(c, vdc);
        CollectionUI collUI = new CollectionUI(c);
        
        
        Collection subCollections = collUI.getSubCollections();
        for (Iterator it = subCollections.iterator(); it.hasNext();) {
            getNodeObject( collectionNode ).setLeaf(false);
            VDCCollection subColl = (VDCCollection) it.next();
            Boolean expanded = addCollectionNode(collectionNode, subColl, vdc, false );
            if (expanded != null) {
                includeFlag = true;
                expandFlag = expanded ? true : expandFlag;
            }
        }
        
        // check if collection should be included (and determine count)
        if (studyFilter == null) {
            includeFlag = true;
        } else {
            Iterator studyIter = studyFilter.iterator();
            while (studyIter.hasNext()) {
                Study study =  (Study) studyIter.next();
                if ( isStudyInCollection( study, c, true ) ) {
                    studyCount++;
                }
            }
            
            if (studyCount > 0) {
                includeFlag = true;
            }
        }
        
        
        if (includeFlag) {
            if (c.getId().equals(collectionToBeExpanded) ) {
                expandFlag = true;
            }

            // get studies, if need be
            if (includeStudies) {
                Iterator studyIter = collUI.getStudies().iterator();
                while (studyIter.hasNext()) {
                    Study s = (Study) studyIter.next();
                    if ( studyFilter == null || StudyUI.isStudyInList(s, studyFilter) ) {
                        DefaultMutableTreeNode studyNode = newTreeNode(s, vdc);
                        getNodeObject( collectionNode ).setLeaf(false);
                        collectionNode.add(studyNode);
                    }
                }
            }
            
            if (includeCount && studyFilter != null) {
                getNodeObject(collectionNode).setText( getNodeObject(collectionNode).getText() + " (" + studyCount + ")" );
            }
            
            getNodeObject(collectionNode).setExpanded(expandFlag);
            parentNode.add(collectionNode);
            return expandFlag;
        } else {
            // this collection is not included
            return null;
        }
    }
    
    // new node methods
    /*
    private DefaultMutableTreeNode newTreeNode(VDC vdc) {
        String id = "vdc_" + vdc.getId().toString();
        String styleClass = "vdcTreeDataverse";
        
        String url = null;
        if (vdcUrl != null) {
            url = "/dv/" + vdc.getAlias() + vdcUrl;
        }
        
        
        ImageComponent image = new ImageComponent();
        if (vdc.isRestricted()) {
            image.setUrl("/resources/images/icon_lock.gif");
            image.setAlt("Restricted Dataverse");
        } else {
            image.setUrl("/resources/images/icon_dataverse.gif");
            image.setAlt("Dataverse");
            
        }
        return newTreeNode(id, vdc.getName(), url, image, styleClass);
    }
    */
    private DefaultMutableTreeNode newTreeNode(VDCCollection coll, VDC vdc) {        
        String url = null;
        if (collectionUrl != null) {
            url = appendParameterToUrl("/dvn/dv/" + vdc.getAlias() + collectionUrl, "collectionId=" + coll.getId());
        }
        return newTreeNode( coll.getName(), url, true);
    }

    private DefaultMutableTreeNode newTreeNode(Study study, VDC vdc) {
        
        String url = null;
        if (studyUrl != null) {
            url = appendParameterToUrl("/dvn/dv/" + vdc.getAlias() + studyUrl, "studyId=" + study.getId());
        }
                
        StudyUI studyUI = new StudyUI(study);

        String studyText = studyUI.getMetadata().getTitle();
        if ( !StringUtil.isEmpty(studyUI.getAuthors()) ) {
            studyText += " by " + studyUI.getAuthors();
        }
        
        return newTreeNode(studyText, url, false);
    }

    
    private DefaultMutableTreeNode newTreeNode(String text, String url, boolean isColl) {
        // create node
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        UrlNodeUserObject nodeObject = new UrlNodeUserObject(node, isColl);
        nodeObject.setText(text);
        nodeObject.setUrl(url);
       
        node.setUserObject(nodeObject);
        return node;
    }
       
   
    private String appendParameterToUrl(String nodeUrl, String parameter) {
        if ( nodeUrl.indexOf("?") != -1) {
            nodeUrl += "&";
        } else {
            nodeUrl += "?";
        }
        
        return nodeUrl + parameter;
    }
    
    
    
    public static boolean isStudyInCollection(Study study, VDCCollection coll, boolean checkChildren) {
        
        CollectionUI collUI = new CollectionUI(coll);
        if ( collUI.getStudyIds().contains(study.getId()) ) {
            return true;
        }
        
        
        // check Children
        if (checkChildren) {
            Iterator collIter = collUI.getSubCollections().iterator();
            while (collIter.hasNext()) {
                VDCCollection subColl = (VDCCollection) collIter.next();
                if ( isStudyInCollection( study, subColl, true ) ) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private UrlNodeUserObject getNodeObject(DefaultMutableTreeNode node) {
        return (UrlNodeUserObject) node.getUserObject();
    }
    
    private DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) tree.getRoot();
    } 
    

    private static final String XP_BRANCH_CONTRACTED_ICON = "../resources/icefaces/dvn_rime/css-images/tree_folder_close.gif";
    private static final String XP_BRANCH_EXPANDED_ICON = "../resources/icefaces/dvn_rime/css-images/tree_folder_open.gif";
    private static final String XP_BRANCH_LEAF_ICON = "../resources/icefaces/dvn_rime/css-images/tree_document.gif";

    public class UrlNodeUserObject extends IceUserObject {

        // url to show when a node is clicked
        private String url;

        public UrlNodeUserObject(DefaultMutableTreeNode wrapper, boolean isColl) {
            super(wrapper);
            this.setLeaf(true);
            this.setBranchContractedIcon(XP_BRANCH_CONTRACTED_ICON);
            this.setBranchExpandedIcon(XP_BRANCH_EXPANDED_ICON);
            
            if (isColl) {
                this.setLeafIcon(XP_BRANCH_CONTRACTED_ICON);
            } else {
                this.setLeafIcon(XP_BRANCH_LEAF_ICON);   
            }
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    
}
