/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/*
 * CollectionUI.java
 *
 * Created on December 20, 2006, 4:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.collection;

import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author gdurand
 */
public class CollectionUI implements java.io.Serializable {

    VDCCollection coll;
    VDC vdc; // used with linked Collections
    
    /** Creates a new instance of CollectionUI */
    public CollectionUI(VDCCollection coll) {
        this.coll = coll;
    }

    // this constructor is used for links (so we need to know which VDC)
    public CollectionUI(VDCCollection coll, VDC vdc) {
        this.coll = coll;
        this.vdc = vdc;
    }    
    
    public VDCCollection getCollection() {
        return coll;
    }

    public String getCollectionPath(VDC vdc) {
        if (coll.getParentCollection() == null) {
            return coll.getName();
        }

        CollectionUI parentCollUI = new CollectionUI(coll.getParentCollection());

        if (!coll.getOwner().getId().equals(vdc.getId()) && isLinkedCollection(vdc) && !parentCollUI.isLinkedCollection(vdc)) {
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
            if (linkedVDC.getId().equals(vdc.getId())) {
                return true;
            }
        }
        return false;
    }

    public List<VDCCollection> getSubCollections() {
        return getSubCollections(false);
    }

    public List<VDCCollection> getSubCollections(boolean getHiddenCollections) {
        VDCCollectionServiceLocal collectionService = null;
        try {
            collectionService = (VDCCollectionServiceLocal) new InitialContext().lookup("java:comp/env/collectionService");
        } catch (Exception e) {
            e.printStackTrace();
        }

        List subCollections = collectionService.findSubCollections(coll.getId());
        return subCollections;

    }

    // wrappers around methods in collectionService
    public List<Study> getStudies() {
        VDCCollectionServiceLocal collectionService = null;
        try {
            collectionService = (VDCCollectionServiceLocal) new InitialContext().lookup("java:comp/env/collectionService");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return collectionService.getStudies(coll);
    }    
    
    public List<Long> getStudyIds() {
        VDCCollectionServiceLocal collectionService = null;
        try {
            collectionService = (VDCCollectionServiceLocal) new InitialContext().lookup("java:comp/env/collectionService");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return collectionService.getStudyIds(coll);
    }


    // the following are actions used in the ManageCollectionsPage 
    public String deleteCollection_action() {
        VDCCollectionServiceLocal collectionService = null;
        try {
            collectionService = (VDCCollectionServiceLocal) new InitialContext().lookup("java:comp/env/collectionService");
        } catch (Exception e) {
            e.printStackTrace();
        }

        collectionService.destroy(coll);
        return null;
    }
    
    public String removeLink_action() {
        VDCCollectionServiceLocal collectionService = null;

        try {
            collectionService = (VDCCollectionServiceLocal) new InitialContext().lookup("java:comp/env/collectionService");
        } catch (Exception e) {
            e.printStackTrace();
        }

        vdc.getLinkedCollections().remove(coll);        
        coll.getLinkedVDCs().remove(vdc);
        

        collectionService.edit(coll);  
        return null;
    }    
}
