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

package edu.harvard.hmdc.vdcnet.web.component;

import com.sun.rave.web.ui.component.Hyperlink;
import com.sun.rave.web.ui.component.ImageComponent;
import com.sun.rave.web.ui.component.Tree;
import com.sun.rave.web.ui.component.TreeNode;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.web.collection.CollectionUI;
import edu.harvard.hmdc.vdcnet.web.site.VDCUI;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.faces.el.MethodBinding;

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
public class VDCCollectionTree {
    
    /** Creates a new instance of VDCCollectionTree */
    public VDCCollectionTree() {
        tree = new Tree();
        tree.setClientSide(true);
        tree.setExpandOnSelect(false);
    }
    
    public VDCCollectionTree(Tree tree) {
        this.tree = tree;
        tree.setClientSide(true);
        tree.setExpandOnSelect(false);
    }
    
    private Tree tree;
    
    private String vdcUrl;
    private String collectionUrl;
    private String studyUrl;
    
    private boolean includeLinkedCollections = true;
    
    private boolean includeStudies = false;
    private boolean includeCount = false;
    private List studyFilter = null;
    
    private Long collectionToBeExpanded = null;
    private boolean expandAll = false;
    
    // this is deprecated and should be replaced by new way
    private MethodBinding actionMethodBinding;
    
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
    
    public MethodBinding getActionMethodBinding() {
        return actionMethodBinding;
    }
    
    public void setActionMethodBinding(MethodBinding actionMethodBinding) {
        this.actionMethodBinding = actionMethodBinding;
    }
    
    //
    // Populate methods
    //
    
    public Tree populate(List vdcs) {
        
        clearNode(tree);
        
        Iterator iter = vdcs.iterator();
        while (iter.hasNext()) {
            VDC vdc = (VDC) iter.next();
            TreeNode baseNode = newTreeNode(vdc);
            baseNode.setExpanded(expandAll);
            tree.getChildren().add(baseNode);
            
            if (!vdc.isRestricted()) {
                addVDC(baseNode, vdc);
            }
        }
        
        return tree;
    }
    
    public Tree populate(VDC vdc) {
        // by default, expand the root node
        if (collectionToBeExpanded == null) {
            collectionToBeExpanded = vdc.getRootCollection().getId();
        }
        
        clearNode(tree);
        addVDC(tree, vdc);
        return tree;
    }
    
    
    
    
    //
    // Private helper methods
    //
    
    private void clearNode(TreeNode node) {
        while (node.getChildCount() > 0 ) {
            node.getChildren().remove(0);
        }
    }
    
    private void addVDC(TreeNode parentNode, VDC vdc) {
        addCollectionNode(parentNode, vdc.getRootCollection(), vdc, true);
        if (includeLinkedCollections) {
            Iterator iter = new VDCUI(vdc).getLinkedCollections().iterator();
            while (iter.hasNext()) {
                VDCCollection lc = (VDCCollection) iter.next();
                addCollectionNode(parentNode, lc, vdc, false);
            }
        }
    }
    
    
    private Boolean addCollectionNode(TreeNode parentNode, VDCCollection c, VDC vdc, boolean alwaysInclude) {
        boolean includeFlag = alwaysInclude;
        boolean expandFlag = expandAll;
        int studyCount = 0;
        
        TreeNode collectionNode = newTreeNode(c, vdc);
        CollectionUI collUI = new CollectionUI(c);
        
        
        Collection subCollections = collUI.getSubCollections();
        for (Iterator it = subCollections.iterator(); it.hasNext();) {
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
                        TreeNode studyNode = newTreeNode(s, vdc);
                        collectionNode.getChildren().add(studyNode);
                    }
                }
            }
            
            if (includeCount && studyFilter != null) {
                collectionNode.setText(collectionNode.getText() + " (" + studyCount + ")" );
            }
            
            collectionNode.setExpanded(expandFlag);
            parentNode.getChildren().add(collectionNode);
            return expandFlag;
        } else {
            // this collection is not included
            return null;
        }
    }
    
    // new node methods
    
    private TreeNode newTreeNode(VDC vdc) {
        String id = "vdc_" + vdc.getId().toString();
        String styleClass = "vdcTreeDataverse";
        
        String url = null;
        if (vdcUrl != null) {
            url = "/dv/" + vdc.getAlias() + vdcUrl;
        }
        
        
        ImageComponent image = new ImageComponent();
        if (vdc.isRestricted()) {
            image.setUrl("/resources/icon_lock.gif");
            image.setAlt("Restricted Dataverse");
        } else {
            image.setUrl("/resources/icon_dataverse.gif");
            image.setAlt("Dataverse");
            
        }
        return newTreeNode(id, vdc.getName(), url, image, styleClass);
    }
    
    private TreeNode newTreeNode(VDCCollection coll, VDC vdc) {
        String id = "collection_" + coll.getId();
        String styleClass = "vdcTreeCollection";
        
        String url = null;
        if (collectionUrl != null) {
            url = appendParameterToUrl("/dv/" + vdc.getAlias() + collectionUrl, "collectionId=" + coll.getId());
        }
        
        ImageComponent image = new ImageComponent();
        if ( coll.getOwner().getId().equals( vdc.getId() ) ) {
            image.setUrl("/resources/tree_folder.gif");
            image.setAlt("Collection");
        } else {
            //image.setUrl("/resources/icon_link.gif"); change icon to make link collections to look the same as regular collections
            image.setUrl("/resources/tree_folder.gif");
            image.setAlt("Linked Collection");
        }
        
        return newTreeNode(id, coll.getName(), url, image, styleClass);
    }
    
    private TreeNode newTreeNode(Study study, VDC vdc) {
        String id =  "study_" + study.getId();
        String styleClass = "vdcTreeStudy";
        
        String url = null;
        if (studyUrl != null) {
            url = appendParameterToUrl("/dv/" + vdc.getAlias() + studyUrl, "studyId=" + study.getId());
        }
        
        ImageComponent image = new ImageComponent();
        image.setUrl("/resources/tree_document.gif");
        image.setAlt("Study");
        
        StudyUI studyUI = new StudyUI(study);
        String studyText = study.getTitle();
        if ( !StringUtil.isEmpty(studyUI.getAuthors()) ) {
            studyText += " by " + studyUI.getAuthors();
        }
        
        return newTreeNode(id, studyText, url, image, styleClass);
    }
    
    private TreeNode newTreeNode(String id, String text, String url, ImageComponent image) {
        return newTreeNode(id,text,url,image,null);
    }
    
    private TreeNode newTreeNode(String id, String text, String url, ImageComponent image, String styleClass) {
        // create node
        TreeNode node = new TreeNode();
        node.setId(id);
        node.setStyleClass(styleClass);
        
        //format the node link
        
        Hyperlink nodelink = new Hyperlink();
        nodelink.setText(text);
        nodelink.setToolTip(image.getAlt());
        nodelink.setUrl(url);
        
        // this is deprecated and should be replaced by new way
        node.setAction(actionMethodBinding); 
        
        // set up some global image stuff
        image.setAlign("top");
        image.setToolTip(image.getAlt());
        
        node.getFacets().put(node.IMAGE_FACET_KEY, image);
        node.getFacets().put(node.CONTENT_FACET_KEY, nodelink);

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
    
    
}
