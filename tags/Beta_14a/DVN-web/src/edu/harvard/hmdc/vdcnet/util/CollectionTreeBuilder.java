/*
 * CollectionTreeBuilder.java
 *
 * Created on December 1, 2006, 9:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import com.sun.rave.web.ui.component.Tree;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.component.VDCCollectionTree;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author wbossons
 */
public class CollectionTreeBuilder extends VDCBaseBean {
    @EJB VDCServiceLocal vdcService;
    @EJB StudyServiceLocal studyService;
    @EJB IndexServiceLocal indexService;
    
    /** Creates a new instance of CollectionTreeBuilder */
    public CollectionTreeBuilder() {
    }
    
    private Tree collectionTree;
    /**
     * Getter for property collectionTree.
     * @return Value of property collectionTree.
     */
   /* public Tree getCollectionTree() {
        if (collectionTree == null) {
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            if (vdc != null) {
                VDCCollectionTree vdcTree = new VDCCollectionTree();
                vdcTree.setExpandAll(true);
                vdcTree.setCollectionUrl("/faces/SearchPage.jsp?mode=1");
                collectionTree = vdcTree.populate(vdc);
            }
        }
        
        return this.collectionTree;
    }
    */
    /**
     * Getter for property collectionTree.
     * @return Value of property collectionTree.
     */
    public Tree getCollectionTree() {
        if (collectionTree == null) {
            VDCCollectionTree vdcTree = new VDCCollectionTree();
            vdcTree.setVDCUrl("/faces/HomePage.jsp");
            vdcTree.setCollectionUrl("/faces/SearchPage.jsp?mode=1");
            
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            vdcTree.setExpandAll(true);
            if (vdc == null) {
                collectionTree = vdcTree.populate(vdcService.findAll());                
            } else {
                collectionTree = vdcTree.populate(vdc);    
            }
        }
        
        return this.collectionTree;
    }
    

    /**
     * Setter for property collectionTree.
     * @param collectionTree New value of property collectionTree.
     */
    public void setCollectionTree(Tree collectionTree) {
        this.collectionTree = collectionTree;
    }
    
}
