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
 * AddCollectionsPage.java
 *
 * Created on September 15, 2006, 1:33 PM
 */
package edu.harvard.hmdc.vdcnet.web.collection;

import com.icesoft.faces.component.datapaginator.DataPaginator;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.index.SearchTerm;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.faces.event.ActionEvent;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditCollectionPage extends VDCBaseBean implements java.io.Serializable {

    @EJB
    VDCServiceLocal vdcService;
    @EJB
    VDCCollectionServiceLocal vdcCollectionService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    IndexServiceLocal indexService;

    public EditCollectionPage() {
    }
     
    private VDCCollection collection;
    private Long collId;
    private Long parentId;

    public VDCCollection getCollection() {
        return collection;
    }

    public CollectionUI getCollUI() {
        return new CollectionUI(collection);
    }

    public void setCollection(VDCCollection collection) {
        this.collection = collection;
    }

    public Long getCollId() {
        return collId;
    }

    public void setCollId(Long collId) {
        this.collId = collId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void init() {
        super.init();

        String collIdStr = getRequestParam("collectionId");

        if (collIdStr != null) {
            collId = Long.parseLong(collIdStr);
            collection = vdcCollectionService.find(collId);
            if (collection.getParentCollection() != null) {
                parentId = collection.getParentCollection().getId();
            }
        } else {
            collection = new VDCCollection();
            collection.setStudies(new ArrayList());
            collection.setOwner(getVDCRequestBean().getCurrentVDC());
            collection.setType("static");

            String parentIdStr = getRequestParam("parentId");
            if (parentIdStr != null) {
                parentId = Long.parseLong(parentIdStr);
            } else {
                parentId = getVDCRequestBean().getCurrentVDC().getRootCollection().getId();
            }
            collection.setParentCollection( vdcCollectionService.find(parentId) );
        }

        browseDVId = getVDCRequestBean().getCurrentVDC().getId();
        browseCollectionId = getVDCRequestBean().getCurrentVDC().getRootCollection().getId();
        setAvailableStudies(browseCollectionId);

    }

    public List<SelectItem> getParentCollectionItems() {
        List collSelectItems = new ArrayList<SelectItem>();

        List<VDCCollection> collList = vdcCollectionService.getCollectionList(getVDCRequestBean().getCurrentVDC(), collection);
        for (VDCCollection coll : collList) {
            collSelectItems.add(new SelectItem(coll.getId(), coll.getName()));
        }

        return collSelectItems;
    }
    
    
    private List<StudyUI>  availableStudies;
    private DataPaginator availableStudiesPaginator;
    private String availableStudiesMethod = "browse";
    
    public List<StudyUI> getAvailableStudies() {
        return availableStudies;
    }

    private void setAvailableStudies(Long collectionId) {
        setAvailableStudies( vdcCollectionService.getStudyIds( vdcCollectionService.find(collectionId) ));
    }

    private void setAvailableStudies(List<Long> studyIds) {
        
        Long vdcId = getVDCRequestBean().getCurrentVDCId();
        VDCUser user = getVDCSessionBean().getUser();
        UserGroup ipUserGroup = getVDCSessionBean().getIpUserGroup();
        List ownedStudyIds = new ArrayList();

        if (collection.isRootCollection()) {
            ownedStudyIds = vdcService.getOwnedStudyIds( collection.getOwner().getId() );
        }
        
        // filter out studies that are not released or in (other) restricted vdcs
        studyIds.retainAll( studyService.getVisibleStudies( studyIds, vdcId ) );

        
        availableStudies = new ArrayList();
        
        for (Long sid : studyIds) {
            StudyUI studyUI = new StudyUI(sid, user, ipUserGroup, StudyUI.isStudyInList(sid, collection.getStudies()));
            if (collection.isRootCollection() && ownedStudyIds.contains(sid) ) {
                studyUI.setSelected(true);
                studyUI.setSelectable(false);             
            }
            availableStudies.add(studyUI);
        }
        
        
        resetAvailableStudiesPaginator();
    }
    
    public DataPaginator getAvailableStudiesPaginator() {
        return availableStudiesPaginator;
    }

    public void setAvailableStudiesPaginator(DataPaginator availableStudiesPaginator) {
        this.availableStudiesPaginator = availableStudiesPaginator;
    } 
    
    private void resetAvailableStudiesPaginator() {
        if (availableStudiesPaginator != null) {
            availableStudiesPaginator.gotoFirstPage();  
        }
    }
    
    public String getAvailableStudiesMethod() {
        return availableStudiesMethod;
    }

    public void setAvailableStudiesMethod(String availableStudiesMethod) {
        if (!availableStudiesMethod.equals(this.availableStudiesMethod)) {
            this.availableStudiesMethod = availableStudiesMethod;
            if (availableStudiesMethod.equals("browse") && browseCollectionId != null ) {
                setAvailableStudies(browseCollectionId);
            } else {
                availableStudies = new ArrayList();    
            }
        }
    }    
    
    // browse functionality
    private Long browseDVId;
    private Long browseCollectionId;
    private Long tempCollId;

    public Long getBrowseDVId() {
        return browseDVId;
    }

    public void setBrowseDVId(Long browseDVId) {
        if (!browseDVId.equals(this.browseDVId)) {
            this.browseDVId = browseDVId;
            tempCollId = vdcService.find(browseDVId).getRootCollection().getId();
            setAvailableStudies(tempCollId);
        }
    }

    public Long getBrowseCollectionId() {
        return browseCollectionId;
    }

    public void setBrowseCollectionId(Long browseCollectionId) {
        if (tempCollId != null) {
            this.browseCollectionId = tempCollId;
            tempCollId = null;
        } else if (!browseCollectionId.equals(this.browseCollectionId)) {
            this.browseCollectionId = browseCollectionId;
            setAvailableStudies(browseCollectionId);
        }
    }

    public List<SelectItem> getBrowseDVItems() {
        List dvSelectItems = new ArrayList<SelectItem>();
        
        // first add current
        VDC currentVDC = getVDCRequestBean().getCurrentVDC();
        dvSelectItems.add(new SelectItem(currentVDC.getId(), currentVDC.getName()));
        dvSelectItems.add(new SelectItem("", "-----", null, true));
        
        for (VDC vdc : vdcService.findAllPublic()) {
            if (!vdc.equals(currentVDC)) {
                dvSelectItems.add(new SelectItem(vdc.getId(), vdc.getName()));
            }
        }

        return dvSelectItems;
    }

    public List<SelectItem> getBrowseCollectionItems() {
        List collSelectItems = new ArrayList<SelectItem>();

        if (browseDVId != null) {
            List<VDCCollection> collList = vdcCollectionService.getCollectionList(vdcService.find(browseDVId));
            for (VDCCollection coll : collList) {
                collSelectItems.add(new SelectItem(coll.getId(), coll.getName()));
            }
        }

        return collSelectItems;
    }
    // search functionality
    private String searchField;
    private String searchValue;

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public void searchStudies(ActionEvent e) {
        List searchTerms = new ArrayList();
        SearchTerm st = new SearchTerm();
        st.setFieldName(searchField);
        st.setValue(searchValue);
        searchTerms.add(st);

        setAvailableStudies(indexService.search( (VDC) null, searchTerms) );
    }

    // actions

    public String save_action() {
        // first set parent, if needed
        if (parentId != null && 
                (collId == null || !parentId.equals(collection.getParentCollection().getId() ) ) ) {

            if (collId != null) {
                collection.removeParentRelationship();
            }

            VDCCollection parentColl = vdcCollectionService.find(parentId);
            collection.setParentRelationship(parentColl);
        }

        // now save collection
        if (collId == null) {
            vdcCollectionService.create(collection);
        } else {
            vdcCollectionService.edit(collection);
        }

        return "manageCollections";
    }

    public String cancel_action() {
        return "manageCollections";
    }

    public void addRemoveStudyListener(RowSelectorEvent event) {
        StudyUI studyUI = (StudyUI) availableStudies.get(event.getRow());

        if (studyUI.isSelectable() ) {
            if (studyUI.isSelected()) {
                if (!StudyUI.isStudyInList(studyUI.getStudy(), collection.getStudies())) {
                    collection.getStudies().add(studyUI.getStudy());
                }
            } else {
                collection.getStudies().remove(studyUI.getStudy());
            }
        } else {
            // revert back to what it was before the user clicked
            studyUI.setSelected( !studyUI.isSelected() );
        }
    }

    public void removeStudyListener(RowSelectorEvent event) {
        Study study = collection.getStudies().get(event.getRow());       
        collection.getStudies().remove(study);

        // deselect from availableStudies
        for (StudyUI studyUI : availableStudies) {
            if (study.getId().equals( studyUI.getStudyId() ) ) {
                studyUI.setSelected(false);
                break;
            }
        }
    }
    
    public List<StudyUI> getStudies() {
        List studyUIs = new ArrayList();

        VDCUser user = getVDCSessionBean().getUser();
        UserGroup ipUserGroup = getVDCSessionBean().getIpUserGroup();
        
        for (Study study : collection.getStudies()) {
            studyUIs.add( new StudyUI(study, user, ipUserGroup, false) );
        }
        
        return studyUIs;
    }
}

