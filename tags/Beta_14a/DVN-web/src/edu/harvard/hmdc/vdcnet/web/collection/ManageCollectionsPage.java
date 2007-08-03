/*
 * ManageCollectionsPage.java
 *
 * Created on October 2, 2006, 1:56 PM
 * Copyright mcrosas
 */
package edu.harvard.hmdc.vdcnet.web.collection;

import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import edu.harvard.hmdc.vdcnet.study.ReviewStateServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.site.VDCUI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.component.html.HtmlPanelGrid;
import com.sun.rave.web.ui.component.PanelLayout;
import javax.faces.component.html.HtmlOutputText;
import com.sun.rave.web.ui.component.PanelGroup;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.UISelectItems;
import com.sun.rave.web.ui.component.HelpInline;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.component.html.HtmlDataTable;
import com.sun.jsfcl.data.DefaultTableDataModel;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import com.sun.jsfcl.data.DefaultSelectItemsArray;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlGraphicImage;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class ManageCollectionsPage extends VDCBaseBean {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB ReviewStateServiceLocal reviewStateService;
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    private void _init() throws Exception {
    }
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        msg =  (StatusMessage)getRequestMap().get("statusMessage");
        dropdown1DefaultItems.setItems(new String[] {"Hidden", "Visible"});
        dataTable1Model.setWrappedData(getCollectionsDisplay());
        try {
            _init();
        } catch (Exception e) {
            log("Page1 Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        } 
    }
    
    StatusMessage msg;
    
    public StatusMessage getMsg(){
        return msg;
    }
    
    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }
    
    private Page page1 = new Page();
    
    public Page getPage1() {
        return page1;
    }
    
    public void setPage1(Page p) {
        this.page1 = p;
    }
    
    private Html html1 = new Html();
    
    public Html getHtml1() {
        return html1;
    }
    
    public void setHtml1(Html h) {
        this.html1 = h;
    }
    
    private Head head1 = new Head();
    
    public Head getHead1() {
        return head1;
    }
    
    public void setHead1(Head h) {
        this.head1 = h;
    }
    
    private Link link1 = new Link();
    
    public Link getLink1() {
        return link1;
    }
    
    public void setLink1(Link l) {
        this.link1 = l;
    }
    
    private Body body1 = new Body();
    
    public Body getBody1() {
        return body1;
    }
    
    public void setBody1(Body b) {
        this.body1 = b;
    }
    
    private Form form1 = new Form();
    
    public Form getForm1() {
        return form1;
    }
    
    public void setForm1(Form f) {
        this.form1 = f;
    }

    private HtmlPanelGrid gridPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel1() {
        return gridPanel1;
    }

    public void setGridPanel1(HtmlPanelGrid hpg) {
        this.gridPanel1 = hpg;
    }

    private PanelLayout layoutPanel1 = new PanelLayout();

    public PanelLayout getLayoutPanel1() {
        return layoutPanel1;
    }

    public void setLayoutPanel1(PanelLayout pl) {
        this.layoutPanel1 = pl;
    }

    private PanelLayout layoutPanel2 = new PanelLayout();

    public PanelLayout getLayoutPanel2() {
        return layoutPanel2;
    }

    public void setLayoutPanel2(PanelLayout pl) {
        this.layoutPanel2 = pl;
    }

    private HtmlOutputText outputText1 = new HtmlOutputText();

    public HtmlOutputText getOutputText1() {
        return outputText1;
    }

    public void setOutputText1(HtmlOutputText hot) {
        this.outputText1 = hot;
    }

    private PanelLayout layoutPanel3 = new PanelLayout();

    public PanelLayout getLayoutPanel3() {
        return layoutPanel3;
    }

    public void setLayoutPanel3(PanelLayout pl) {
        this.layoutPanel3 = pl;
    }

    private HelpInline helpInline1 = new HelpInline();

    public HelpInline getHelpInline1() {
        return helpInline1;
    }

    public void setHelpInline1(HelpInline hi) {
        this.helpInline1 = hi;
    }                
    
    private PanelGroup groupPanel1 = new PanelGroup();

    public PanelGroup getGroupPanel1() {
        return groupPanel1;
    }

    public void setGroupPanel1(PanelGroup pg) {
        this.groupPanel1 = pg;
    }

    private HtmlCommandLink linkAction1 = new HtmlCommandLink();

    public HtmlCommandLink getLinkAction1() {
        return linkAction1;
    }

    public void setLinkAction1(HtmlCommandLink hcl) {
        this.linkAction1 = hcl;
    }

    private HtmlOutputText linkAction1Text = new HtmlOutputText();

    public HtmlOutputText getLinkAction1Text() {
        return linkAction1Text;
    }

    public void setLinkAction1Text(HtmlOutputText hot) {
        this.linkAction1Text = hot;
    }

    private HtmlSelectOneMenu dropdown1 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown1() {
        return dropdown1;
    }

    public void setDropdown1(HtmlSelectOneMenu hsom) {
        this.dropdown1 = hsom;
    }

    private DefaultSelectItemsArray dropdown1DefaultItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown1DefaultItems() {
        return dropdown1DefaultItems;
    }

    public void setDropdown1DefaultItems(DefaultSelectItemsArray dsia) {
        this.dropdown1DefaultItems = dsia;
    }

    private UISelectItems dropdown1SelectItems = new UISelectItems();

    public UISelectItems getDropdown1SelectItems() {
        return dropdown1SelectItems;
    }

    public void setDropdown1SelectItems(UISelectItems uisi) {
        this.dropdown1SelectItems = uisi;
    }

    private HtmlCommandButton changeStatusButton = new HtmlCommandButton();

    public HtmlCommandButton getChangeStatusButton() {
        return changeStatusButton;
    }

    public void setChangeStatusButton(HtmlCommandButton hcb) {
        this.changeStatusButton = hcb;
    }

    public String changeStatusButton_action() {
        List <CollectionModel> collections = (List) dataTable1Model.getWrappedData();
        boolean visibleState = false;
        boolean linkSelected = false;
        StringBuffer selectedLinks = null;
        if (((String)dropdown1.getValue()).equalsIgnoreCase("Hidden")){  // Bundle this
            visibleState = false;
        } else if (((String)dropdown1.getValue()).equalsIgnoreCase("Visible")){  // Bundle this
            visibleState = true;
        }
        Long collectionId=null;
        for (Iterator it = collections.iterator(); it.hasNext();) {
            CollectionModel elem = (CollectionModel) it.next();
            if (elem.isSelected()){
                if (elem.isLink()){
                    if (selectedLinks == null){
                        selectedLinks = new StringBuffer(elem.getName());
                    } else{
                        selectedLinks.append(","+elem.getName());
                    }
                    linkSelected = true;
                } else {
                    collectionId = elem.getId();
                    VDCCollection changeStatusCollection = vdcCollectionService.find(collectionId);
                    changeStatusCollection.setVisible(visibleState);
                    vdcCollectionService.edit(changeStatusCollection);
                }
            }
        }
        if (linkSelected){
            StatusMessage newMsg = new StatusMessage();
            String msgText;
            msgText = "The linked collection(s) " + selectedLinks.toString()+ "cannot be changed.";
            newMsg.setStyleClass("warnMessage");
            newMsg.setMessageText(msgText);
            Map m = getRequestMap();
            m.put("statusMessage",newMsg);            
        }
        
        try {
            init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
 
        
        return "manageCollections";
    }

    private HtmlCommandButton removeButton = new HtmlCommandButton();

    public HtmlCommandButton getRemoveButton() {
        return removeButton;
    }

    public void setRemoveButton(HtmlCommandButton hcb) {
        this.removeButton = hcb;
    }

    public String removeButton_action() {
        boolean removeRootCollection = false;
        List <CollectionModel> collections = (List) dataTable1Model.getWrappedData();
        Long collectionId=null;
        Long pId = null;
        List <Long> removedCollectionIds = new ArrayList();
        HashSet removeLinks = new HashSet();
        for (Iterator it = collections.iterator(); it.hasNext();) {
            CollectionModel elem = (CollectionModel) it.next();
            if (elem.isLink()){
                if (elem.isSelected()){
                    removeLinks.add(elem.getId());
                }
            }else{
                if (elem.isSelected()){
                    if (elem.getParentId()==0){
                        removeRootCollection = true;
                    } else {
                        collectionId = elem.getId();
                        removedCollectionIds.add(elem.getId());
                    }
                }
            }
        }
        if (!removeLinks.isEmpty()){
            removeLinks(removeLinks);
        }
        for (Iterator it = removedCollectionIds.iterator(); it.hasNext();) {
            Long elem = (Long) it.next();
            removeCollection(elem);
        }
        if (removeRootCollection){
            StatusMessage newMsg = new StatusMessage();
            String msgText;
            msgText = "The root collection cannot be removed.";
            newMsg.setStyleClass("warnMessage");
            newMsg.setMessageText(msgText);
            Map m = getRequestMap();
            m.put("statusMessage",newMsg);
        } else{
            StatusMessage newMsg = new StatusMessage();
            String msgText;
            msgText = "Collections removed.";
            newMsg.setStyleClass("successMessage");
            newMsg.setMessageText(msgText);
            Map m = getRequestMap();
            m.put("statusMessage",newMsg);            
        }
        
        try {
            init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
 
        
        return "manageCollections";
    }
    
    
    
    private void removeCollection(Long collectionId){
        VDCCollection removedCollection = vdcCollectionService.find(collectionId);
        if (!(removedCollection == null)){
            List <VDCCollection> subcollections = vdcCollectionService.findSubCollections(collectionId,true);
            for (Iterator it = subcollections.iterator(); it.hasNext();) {
                VDCCollection elem = (VDCCollection) it.next();
                removeCollection(elem.getId());
                
            }
            removeCollection(removedCollection);
        }
    }

     private void removeCollection(VDCCollection removedCollection){        
        VDC vdc = vdcService.find( getVDCRequestBean().getCurrentVDC().getId());
        ArrayList newCollections = new ArrayList();
        VDCCollection parentCollection = removedCollection.getParentCollection();
        Collection <VDCCollection> collections = parentCollection.getSubCollections();
        for (Iterator it = collections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            if (!(elem.getId()==removedCollection.getId())){
                newCollections.add(elem);
            }
        }
        parentCollection.setSubCollections(newCollections);
        vdcCollectionService.edit(parentCollection);
        vdcService.edit(vdc);
        removedCollection.removeParentRelationship();
        vdcCollectionService.destroy(removedCollection);
    }
    
     private void removeCollection(Long parentId, Long selectedId){
         
         long pId = parentId.longValue();
         long sId = selectedId.longValue();
         VDC vdc = vdcService.find( getVDCRequestBean().getCurrentVDC().getId());
         ArrayList newCollections = new ArrayList();
         VDCCollection parentCollection = vdcCollectionService.find(Long.valueOf(pId));
         Collection <VDCCollection> collections = parentCollection.getSubCollections();
         for (Iterator it = collections.iterator(); it.hasNext();) {
             VDCCollection elem = (VDCCollection) it.next();
             if (!(elem.getId()==selectedId)){
                 newCollections.add(elem);
             }
         }
         parentCollection.setSubCollections(newCollections);
         vdcCollectionService.edit(parentCollection);
         vdcService.edit(vdc);
         VDCCollection removedCollection = vdcCollectionService.find(selectedId);
         vdcCollectionService.destroy(removedCollection);
     }
     
    private void removeLinks(HashSet<VDCCollection> removeLinks){
        Long vdcId = getVDCRequestBean().getCurrentVDC().getId();
        VDC vdc = vdcService.find(vdcId);
        List <VDCCollection> remainingCollections = new ArrayList();
        List <VDCCollection> linkedCollections = vdc.getLinkedCollections();
        for (Iterator it = linkedCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            if (!removeLinks.contains(elem.getId())){
                remainingCollections.add(elem);
            } else {
                Collection <VDC> linkedVdcs = elem.getLinkedVDCs();
                linkedVdcs.remove(vdc);
                elem.setLinkedVDCs(linkedVdcs);
                VDCCollection removeLinkCollection = vdcCollectionService.find(elem.getId());
                removeLinkCollection.setLinkedVDCs(linkedVdcs);
                vdcCollectionService.edit(removeLinkCollection);  
            }
        }
        vdc.setLinkedCollections(remainingCollections);
        vdcService.edit(vdc);
    }
    
    private long getCollectionId(String nodeId){
        long collectionId = Long.parseLong(nodeId.substring(11));
        return collectionId;
    }
    
    private HtmlOutputText outputText2 = new HtmlOutputText();

    public HtmlOutputText getOutputText2() {
        return outputText2;
    }

    public void setOutputText2(HtmlOutputText hot) {
        this.outputText2 = hot;
    }
    
    private HtmlDataTable dataTable1 = new HtmlDataTable();

    public HtmlDataTable getDataTable1() {
        return dataTable1;
    }

    public void setDataTable1(HtmlDataTable hdt) {
        this.dataTable1 = hdt;
    }

    private DefaultTableDataModel dataTable1Model = new DefaultTableDataModel();

    public DefaultTableDataModel getDataTable1Model() {
        return dataTable1Model;
    }

    public void setDataTable1Model(DefaultTableDataModel dtdm) {
        this.dataTable1Model = dtdm;
    }

    private UIColumn column1 = new UIColumn();

    public UIColumn getColumn1() {
        return column1;
    }

    public void setColumn1(UIColumn uic) {
        this.column1 = uic;
    }

    private HtmlOutputText outputText11 = new HtmlOutputText();

    public HtmlOutputText getOutputText11() {
        return outputText11;
    }

    public void setOutputText11(HtmlOutputText hot) {
        this.outputText11 = hot;
    }

    private HtmlSelectBooleanCheckbox checkbox1 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox1() {
        return checkbox1;
    }

    public void setCheckbox1(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox1 = hsbc;
    }

    private HtmlGraphicImage image1 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage1() {
        return image1;
    }

    public void setImage1(HtmlGraphicImage hgi) {
        this.image1 = hgi;
    }

    private HtmlSelectOneRadio radioButtonList1 = new HtmlSelectOneRadio();

    public HtmlSelectOneRadio getRadioButtonList1() {
        return radioButtonList1;
    }

    public void setRadioButtonList1(HtmlSelectOneRadio hsor) {
        this.radioButtonList1 = hsor;
    }

    private UISelectItems radioButtonList1SelectItems1 = new UISelectItems();

    public UISelectItems getRadioButtonList1SelectItems1() {
        return radioButtonList1SelectItems1;
    }

    public void setRadioButtonList1SelectItems1(UISelectItems uisi) {
        this.radioButtonList1SelectItems1 = uisi;
    }

    private DefaultSelectItemsArray radioButtonList1DefaultItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getRadioButtonList1DefaultItems() {
        return radioButtonList1DefaultItems;
    }

    public void setRadioButtonList1DefaultItems(DefaultSelectItemsArray dsia) {
        this.radioButtonList1DefaultItems = dsia;
    }

    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public ManageCollectionsPage() {
    }



    /** 
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }

    /** 
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
    }

    /** 
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
    }
    
    public List getCollectionsDisplay(){
        ArrayList collections = new ArrayList();
        Long vdcId = getVDCRequestBean().getCurrentVDC().getId();
        VDC vdc = vdcService.find(vdcId);
        int treeLevel=1;
        VDCCollection vdcRootCollection = vdc.getRootCollection();
        List <VDCCollection> subCollections = vdcCollectionService.findSubCollections(vdcRootCollection.getId(),true);
        CollectionModel row = new CollectionModel();
        row.setLevel(treeLevel);
        row.setId(vdcRootCollection.getId());
        row.setName(vdcRootCollection.getName() + (vdcRootCollection.isVisible()? " (Visible)" : " (Hidden)") + " (Root) (Studies Assigned)");
        row.setSelected(false);
        row.setParentId(0);
        row.setLink(false);
        collections.add(row);
//        treeLevel = buildDisplayModel(collections, vdcRootCollectionRelationships,treeLevel, vdcRootCollection.getId());
        treeLevel = buildDisplayModel(collections, subCollections,treeLevel, vdcRootCollection.getId());
        VDCUI vdcUI = new VDCUI(vdc);
        List <VDCCollection> linkedCollections = vdcUI.getLinkedCollections(false);
        Iterator linkIterator = linkedCollections.iterator();
        while(linkIterator.hasNext()){
            CollectionModel linkRow = new CollectionModel();
            VDCCollection linkCollection = (VDCCollection) linkIterator.next();
            linkRow.setLevel(1);
            linkRow.setId(linkCollection.getId());
            linkRow.setName(linkCollection.getName()+ " (Link) " );
            linkRow.setSelected(false);
            linkRow.setParentId(0);
            linkRow.setLink(true);
            collections.add(linkRow);
        }
        return collections;
    }
    
        
    private int buildDisplayModel( ArrayList collections, List <VDCCollection> vdcSubCollections,int level, long parentId) {
        level++;
        for (Iterator it = vdcSubCollections.iterator(); it.hasNext();) {
            VDCCollection collection = (VDCCollection) it.next();
            collection.setLevel(level);
            CollectionModel row = new CollectionModel();
            row.setLevel(level);
            row.setId(collection.getId());
            row.setName(collection.getName() + (collection.isVisible()? " (Visible)" : " (Hidden)") + ((collection.getQuery() != null)? " (Query)" : " (Studies Assigned)"));
            row.setSelected(false);
            row.setQueryType(collection.getQuery()!=null);
            row.setParentId(collection.getParentCollection().getId());
            row.setLink(false);
            collections.add(row);
            List <VDCCollection> vdcChildCollections = vdcCollectionService.findSubCollections(collection.getId(),true);
            if (vdcChildCollections.size()>0){
                buildDisplayModel(collections,vdcChildCollections,level,collection.getId());
            }
        }
        level--;
        return level;
    }
}

