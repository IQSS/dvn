/*
 * CollectionUI.java
 *
 * Created on December 20, 2006, 4:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package edu.harvard.hmdc.vdcnet.web.collection;

import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author gdurand
 */
public class CollectionUI {
    
    VDCCollection coll;
    
    /** Creates a new instance of CollectionUI */
    public CollectionUI(VDCCollection coll) {
        this.coll = coll;
    }
    
    public String getCollectionPath(VDC vdc) {
        if (coll.getParentCollection() == null) {
            return coll.getName();
        }
        
        CollectionUI parentCollUI = new CollectionUI( coll.getParentCollection() );

        if (!coll.getOwner().getId().equals( vdc.getId())
        && isLinkedCollection(vdc)
        && !parentCollUI.isLinkedCollection(vdc) ) {
            return coll.getName();
        }

        return parentCollUI.getCollectionPath(vdc) + " > " + coll.getName();
    }
    
    public String getShortCollectionPath(VDC vdc) {
        
        if (coll.getParentCollection() == null) {
            return null;
        } else {
            return getCollectionPath(vdc);
        }   
    }
    
    public boolean isLinkedCollection(VDC vdc) {
        Iterator iter = coll.getLinkedVDCs().iterator();
        while (iter.hasNext()) {
            VDC linkedVDC = (VDC) iter.next();
            if ( linkedVDC.getId().equals(vdc.getId() )) {
                return true;
            }
        }
        return false;
    }
    
    public List getSubCollections() {
        return getSubCollections(false);
    }
    public List getSubCollections(boolean getHiddenCollections) {
        VDCCollectionServiceLocal collectionService = null;
        try {
            collectionService=(VDCCollectionServiceLocal)new InitialContext().lookup("java:comp/env/collectionService");
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        List subCollections = collectionService.findSubCollections(coll.getId(), getHiddenCollections);
        return subCollections;
        
    }
    
    public List getStudies() {
        if (coll.getQuery() != null) {
            return getQueryStudies();
        } else {
            return getActualStudys();
        }
    }
    
    public List getStudyIds() {
        if (coll.getQuery() != null) {
            return getQueryStudyIds();
        } else {
            return getActualStudyIds();
        }
    }
    
    public List getActualStudys() {
        VDCCollectionServiceLocal collectionService = null;
        try {
            collectionService=(VDCCollectionServiceLocal)new InitialContext().lookup("java:comp/env/collectionService");
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return collectionService.getOrderedStudiesByCollection(coll.getId());
    }
    
    public List getActualStudyIds() {
        List studyIds = new ArrayList();
        Iterator iter = getActualStudys().iterator();
        while (iter.hasNext()) {
            Study study = (Study) iter.next();
            studyIds.add(study.getId());
        }
        return studyIds;
    }
    
    public List getQueryStudyIds() {
        IndexServiceLocal indexService = null;
        try {
            // call Indexer
            indexService =(IndexServiceLocal)new InitialContext().lookup("java:comp/env/indexService");
            return indexService.query(coll.getQuery());
        } catch (NamingException ex) {
            ex.printStackTrace();
            return new ArrayList();
        }
    }
    
    public List getQueryStudies() {
        List studies = new ArrayList();
        StudyServiceLocal studyService = null;
        try {
            studyService=(StudyServiceLocal)new InitialContext().lookup("java:comp/env/studyService");
            Iterator iter = getQueryStudyIds().iterator();
            while (iter.hasNext()) {
                Long studyId = (Long) iter.next();
                studies.add(studyService.getStudy(studyId));
            }
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
        
        return studies;
        
    }
}
