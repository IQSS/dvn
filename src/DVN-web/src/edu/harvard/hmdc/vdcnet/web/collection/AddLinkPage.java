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
 * AddLinkPage.java
 *
 * Created on December 3, 2006, 10:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.collection;

import com.sun.jsfcl.data.DefaultSelectItemsArray;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.component.UISelectItems;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;

/**
 *
 * @author roberttreacy
 */
public class AddLinkPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    private Map collectionsMap = new HashMap();
    private static final String [] emptyCollection = {"No collections available for selection"};
    private boolean saveDisabled = false;
    
    /** Creates a new instance of AddLinkPage */
    public AddLinkPage() {
    }
    
    private HtmlSelectOneMenu dropdown1 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown1() {
        return dropdown1;
    }

    public void setDropdown1(HtmlSelectOneMenu hsom) {
        this.dropdown1 = hsom;
    }

    private UISelectItems dropdown1SelectItems = new UISelectItems();

    public UISelectItems getDropdown1SelectItems() {
        return dropdown1SelectItems;
    }

    public void setDropdown1SelectItems(UISelectItems uisi) {
        this.dropdown1SelectItems = uisi;
    }

    private DefaultSelectItemsArray dropdown1DefaultItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown1DefaultItems() {
        return dropdown1DefaultItems;
    }

    public void setDropdown1DefaultItems(DefaultSelectItemsArray dsia) {
        this.dropdown1DefaultItems = dsia;
    }

    public void init() {
        super.init();
        collectionsMap = getCollectionsMap();
        dropdown1DefaultItems.setItems(getOtherPublicVDCs());
    }
    
    private String[] getOtherPublicVDCs() {
        String[] otherVDCs = null;
        List <VDC> allVDC = vdcService.findAll();
        List otherVDCCollections = new ArrayList();
        long vdcId = getVDCRequestBean().getCurrentVDC().getId().longValue();
        for (Iterator it = allVDC.iterator(); it.hasNext();) {
            VDC elem = (VDC) it.next();
            if ((elem.getId().longValue() != vdcId) && (elem.isRestricted() == false)){
                otherVDCCollections.addAll(getCollections(elem.getId().longValue()));
            }
        }
        if (otherVDCCollections.size() > 0){
            setSaveDisabled(false);
            otherVDCs = new String[otherVDCCollections.size()];
            int i=0;
            for (Iterator it = otherVDCCollections.iterator(); it.hasNext();) {
                VDCCollection elem = (VDCCollection) it.next();
                otherVDCs[i++] = new String( elem.getFullCollectionName());
                
            }
        } else{
            setSaveDisabled(true);
            otherVDCs = emptyCollection;
        }
        return otherVDCs;
    }
    
    public List getCollections(long vdcId){
        ArrayList collections = new ArrayList();
        VDC vdc = vdcService.find(new Long(vdcId));
        int treeLevel=0;
        VDCCollection vdcRootCollection = vdc.getRootCollection();
        if (vdcRootCollection != null){
            vdcRootCollection.setLevel(treeLevel);
            collections.add(vdcRootCollection);
            Collection <VDCCollection> subcollections = vdcRootCollection.getSubCollections();
            treeLevel = buildList(collections, subcollections,treeLevel);
        }
        return collections;
    }

    public Map getCollectionsMap(){
        List <VDC> allVDC = vdcService.findAll();
        List allVDCCollections = new ArrayList();
        for (Iterator it = allVDC.iterator(); it.hasNext();) {
            VDC elem = (VDC) it.next();
            allVDCCollections.addAll(getCollections(elem.getId().longValue()));
        }
        String [] allVDCs = new String[allVDCCollections.size()];
        for (Iterator it = allVDCCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            collectionsMap.put(elem.getFullCollectionName(),elem.getId());

        }

        return collectionsMap;
    }

    private int buildList( ArrayList collections, Collection<VDCCollection> vdcCollections,int level) {
        level++;
        for (Iterator it = vdcCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            if (elem.isVisible()){
                elem.setLevel(level);
                collections.add(elem);
                Collection <VDCCollection> subcollections = elem.getSubCollections();
                if (subcollections.size()>0){
                    buildList(collections,subcollections,level);
                }
            }
        }
        level--;
        return level;
    }

    public String saveLink() {
        VDC vdc = vdcService.find(getVDCRequestBean().getCurrentVDC().getId());
        List <VDCCollection> linkedCollections = vdc.getLinkedCollections();
        VDCCollection addCollection = vdcCollectionService.find(collectionsMap.get((String) dropdown1.getValue()));
        linkedCollections.add(addCollection);
        vdc.setLinkedCollections(linkedCollections);
        Collection <VDC> linkedVdcs = addCollection.getLinkedVDCs();
        linkedVdcs.add(vdc);
        addCollection.setLinkedVDCs(linkedVdcs);
        vdcCollectionService.edit(addCollection);
        vdcService.edit(vdc);
        StatusMessage msg = new StatusMessage();
        msg.setMessageText("Link "+ addCollection.getFullCollectionName()+ " was added to dataverse "+ getVDCRequestBean().getCurrentVDC().getName());
        msg.setStyleClass("successMessage");
        Map m = getRequestMap();
        m.put("statusMessage",msg);
        return "home";
    }
    
    public String cancel(){
        return "myOptions";
    }

    public boolean isSaveDisabled() {
        return saveDisabled;
    }

    public void setSaveDisabled(boolean saveEnabled) {
        this.saveDisabled = saveEnabled;
    }
}
