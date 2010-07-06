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
