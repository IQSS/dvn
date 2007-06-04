/*
 * EditCollectionsPage.java
 *
 * Created on September 15, 2006, 1:33 PM
 * Copyright mcrosas
 */
package edu.harvard.hmdc.vdcnet.web.collection;

import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import com.sun.rave.web.ui.model.Option;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.ReviewStateServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyField;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
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
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.component.html.HtmlPanelGrid;
import com.sun.rave.web.ui.component.PanelLayout;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlInputText;
import com.sun.rave.web.ui.component.PanelGroup;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlSelectOneRadio;
import com.sun.jsfcl.data.DefaultSelectItemsArray;
import com.sun.rave.web.ui.component.AddRemove;
import com.sun.rave.web.ui.model.MultipleSelectOptionsList;
import com.sun.rave.web.ui.component.HelpInline;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditCollectionPage extends VDCBaseBean {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB StudyServiceLocal studyService;
    @EJB ReviewStateServiceLocal reviewStateService;
    long vdcId =  0;
    VDCCollection vdcCollection;
    private ResourceBundle messages = ResourceBundle.getBundle("Bundle");
    
    private HashMap searchFields = new HashMap();
    private Map collectionsMap = new HashMap();
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    private Long collId;
    public Long getCollId(){
        return collId;
    }
    public void setCollId(Long collId){
        this.collId = collId;
    }
    
    String collectionName;
    public String getCollectionName(){
        return collectionName;
    }
    public void setCollectionName(String name){
        this.collectionName = name;
    }

    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init(){
        super.init();
        initVdcId();
        Long id =  (Long)getRequestMap().get("collectionId");
        if (id != null){
            collId = id;
        } else{
//            String collIdStr = getRequestParam("content:editCollectionPageView:form1:collectionId");
            String collIdStr = getRequestParam("collectionId");
            if (collIdStr == null){
                collIdStr = getRequestParam("content:editCollectionPageView:form1:collectionId");
            }
            collId = Long.parseLong(collIdStr);
        }
        vdcCollection = vdcCollectionService.find(collId);
        setCollectionName(vdcCollection.getName());
        textField1.setValue(vdcCollection.getName());
        VDCCollection editCollection = vdcCollectionService.find(vdcCollection.getId());
        Collection <Study> editStudies = vdcCollection.getStudies();
        int i=0;
        Option [] studies = new Option[editStudies.size()];
        Object [] studiesValues = new Object[editStudies.size()];
        for (Iterator it = editStudies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            studiesValues[i] = Long.toString(elem.getId().longValue());
            studies[i++]= new Option(Long.toString(elem.getId().longValue()),elem.getTitle());
        }
        
        collectionsMap = getCollectionsMap();
        try{
            dropdown2DefaultItems.setItems(getVDCCollections(vdcId));
        } catch (NullPointerException n){
            n.printStackTrace();
        }
        dropdown4DefaultItems.setItems(getAllViewableCollections());
        
        addRemoveList2DefaultOptions.setOptions(studies);
        addRemoveList2DefaultOptions.setSelectedValue(studiesValues);
        addRemoveList2.setSelected(addRemoveList2DefaultOptions.getSelectedValue());
        dropdown5DefaultItems.setItems(getSearchFields());
        radioButtonList1DefaultItems.setItems(new String[] {"Search Studies in All Dataverses", "Search from \"Studies to Choose from\" box"});
    }
    
    private void initVdcId(){
        vdcId = getVDCRequestBean().getCurrentVDC().getId().longValue();;
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

    private PanelGroup groupPanel3 = new PanelGroup();

    public PanelGroup getGroupPanel3() {
        return groupPanel3;
    }

    public void setGroupPanel3(PanelGroup pg) {
        this.groupPanel3 = pg;
    }

    private HtmlOutputText outputText7 = new HtmlOutputText();

    public HtmlOutputText getOutputText7() {
        return outputText7;
    }

    public void setOutputText7(HtmlOutputText hot) {
        this.outputText7 = hot;
    }

    private HtmlInputText textField1 = new HtmlInputText();

    public HtmlInputText getTextField1() {
        return textField1;
    }

    public void setTextField1(HtmlInputText hit) {
        this.textField1 = hit;
    }

    private HtmlOutputText outputText8 = new HtmlOutputText();

    public HtmlOutputText getOutputText8() {
        return outputText8;
    }

    public void setOutputText8(HtmlOutputText hot) {
        this.outputText8 = hot;
    }

    private HtmlSelectOneMenu dropdown3 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown3() {
        return dropdown3;
    }

    public void setDropdown3(HtmlSelectOneMenu hsom) {
        this.dropdown3 = hsom;
    }

    private DefaultSelectItemsArray dropdown2DefaultItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown2DefaultItems() {
        return dropdown2DefaultItems;
    }

    public void setDropdown2DefaultItems(DefaultSelectItemsArray dsia) {
        this.dropdown2DefaultItems = dsia;
    }

    private UISelectItems dropdown2SelectItems2 = new UISelectItems();

    public UISelectItems getDropdown2SelectItems2() {
        return dropdown2SelectItems2;
    }

    public void setDropdown2SelectItems2(UISelectItems uisi) {
        this.dropdown2SelectItems2 = uisi;
    }

    private PanelGroup groupPanel4 = new PanelGroup();

    public PanelGroup getGroupPanel4() {
        return groupPanel4;
    }

    public void setGroupPanel4(PanelGroup pg) {
        this.groupPanel4 = pg;
    }

    private HtmlOutputText outputText10 = new HtmlOutputText();

    public HtmlOutputText getOutputText10() {
        return outputText10;
    }

    public void setOutputText10(HtmlOutputText hot) {
        this.outputText10 = hot;
    }

    private HtmlPanelGrid gridPanel2 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel2() {
        return gridPanel2;
    }

    public void setGridPanel2(HtmlPanelGrid hpg) {
        this.gridPanel2 = hpg;
    }

    private HtmlOutputText outputText11 = new HtmlOutputText();

    public HtmlOutputText getOutputText11() {
        return outputText11;
    }

    public void setOutputText11(HtmlOutputText hot) {
        this.outputText11 = hot;
    }

    private PanelGroup groupPanel5 = new PanelGroup();

    public PanelGroup getGroupPanel5() {
        return groupPanel5;
    }

    public void setGroupPanel5(PanelGroup pg) {
        this.groupPanel5 = pg;
    }

    private HtmlSelectOneMenu dropdown4 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown4() {
        return dropdown4;
    }

    public void setDropdown4(HtmlSelectOneMenu hsom) {
        this.dropdown4 = hsom;
    }

    private UISelectItems dropdown4SelectItems1 = new UISelectItems();

    public UISelectItems getDropdown4SelectItems1() {
        return dropdown4SelectItems1;
    }

    public void setDropdown4SelectItems1(UISelectItems uisi) {
        this.dropdown4SelectItems1 = uisi;
    }

    private DefaultSelectItemsArray dropdown4DefaultItems = new DefaultSelectItemsArray();
    

    public DefaultSelectItemsArray getDropdown4DefaultItems() {
        return dropdown4DefaultItems;
    }

    public void setDropdown4DefaultItems(DefaultSelectItemsArray dsia) {
        this.dropdown4DefaultItems = dsia;
    }

    private HtmlCommandButton selectButton = new HtmlCommandButton();

    public HtmlCommandButton getSelectButton() {
        return selectButton;
    }

    public void setSelectButton(HtmlCommandButton hcb) {
        this.selectButton = hcb;
    }

    private HtmlOutputText outputText12 = new HtmlOutputText();

    public HtmlOutputText getOutputText12() {
        return outputText12;
    }

    public void setOutputText12(HtmlOutputText hot) {
        this.outputText12 = hot;
    }

    private PanelGroup groupPanel6 = new PanelGroup();

    public PanelGroup getGroupPanel6() {
        return groupPanel6;
    }

    public void setGroupPanel6(PanelGroup pg) {
        this.groupPanel6 = pg;
    }

    private HtmlSelectOneMenu dropdown5 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown5() {
        return dropdown5;
    }

    public void setDropdown5(HtmlSelectOneMenu hsom) {
        this.dropdown5 = hsom;
    }

    private UISelectItems dropdown5SelectItems1 = new UISelectItems();

    public UISelectItems getDropdown5SelectItems1() {
        return dropdown5SelectItems1;
    }

    public void setDropdown5SelectItems1(UISelectItems uisi) {
        this.dropdown5SelectItems1 = uisi;
    }

    private DefaultSelectItemsArray dropdown5DefaultItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown5DefaultItems() {
        return dropdown5DefaultItems;
    }

    public void setDropdown5DefaultItems(DefaultSelectItemsArray dsia) {
        this.dropdown5DefaultItems = dsia;
    }

    private HtmlInputText textField2 = new HtmlInputText();

    public HtmlInputText getTextField2() {
        return textField2;
    }

    public void setTextField2(HtmlInputText hit) {
        this.textField2 = hit;
    }

    private HtmlCommandButton button4 = new HtmlCommandButton();

    public HtmlCommandButton getButton4() {
        return button4;
    }

    public void setButton4(HtmlCommandButton hcb) {
        this.button4 = hcb;
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

    private PanelGroup groupPanel7 = new PanelGroup();

    public PanelGroup getGroupPanel7() {
        return groupPanel7;
    }

    public void setGroupPanel7(PanelGroup pg) {
        this.groupPanel7 = pg;
    }

    private HtmlOutputText outputText13 = new HtmlOutputText();

    public HtmlOutputText getOutputText13() {
        return outputText13;
    }

    public void setOutputText13(HtmlOutputText hot) {
        this.outputText13 = hot;
    }

    private HelpInline helpInline2 = new HelpInline();

    public HelpInline getHelpInline2() {
        return helpInline2;
    }

    public void setHelpInline2(HelpInline hi) {
        this.helpInline2 = hi;
    }

    private PanelGroup groupPanel8 = new PanelGroup();

    public PanelGroup getGroupPanel8() {
        return groupPanel8;
    }

    public void setGroupPanel8(PanelGroup pg) {
        this.groupPanel8 = pg;
    }

    private HtmlInputTextarea textAreaQuery = new HtmlInputTextarea();

    public HtmlInputTextarea getTextAreaQuery() {
        return textAreaQuery;
    }

    public void setTextAreaQuery(HtmlInputTextarea hit) {
        this.textAreaQuery = hit;
    }

    private HtmlCommandButton button5 = new HtmlCommandButton();

    public HtmlCommandButton getButton5() {
        return button5;
    }

    public void setButton5(HtmlCommandButton hcb) {
        this.button5 = hcb;
    }

    private AddRemove addRemoveList2 = new AddRemove();

    public AddRemove getAddRemoveList2() {
        return addRemoveList2;
    }

    public void setAddRemoveList2(AddRemove ar) {
        this.addRemoveList2 = ar;
    }

    private MultipleSelectOptionsList addRemoveList1DefaultOptions = new MultipleSelectOptionsList();

    public MultipleSelectOptionsList getAddRemoveList1DefaultOptions() {
        return addRemoveList1DefaultOptions;
    }

    public void setAddRemoveList1DefaultOptions(MultipleSelectOptionsList msol) {
        this.addRemoveList1DefaultOptions = msol;
    }

    private MultipleSelectOptionsList addRemoveList2DefaultOptions = new MultipleSelectOptionsList();

    public MultipleSelectOptionsList getAddRemoveList2DefaultOptions() {
        return addRemoveList2DefaultOptions;
    }

    public void setAddRemoveList2DefaultOptions(MultipleSelectOptionsList msol) {
        this.addRemoveList2DefaultOptions = msol;
    }

    private PanelGroup groupPanel9 = new PanelGroup();

    public PanelGroup getGroupPanel9() {
        return groupPanel9;
    }

    public void setGroupPanel9(PanelGroup pg) {
        this.groupPanel9 = pg;
    }

    private HtmlCommandButton saveCollectionButton = new HtmlCommandButton();

    public HtmlCommandButton getSaveCollectionButton() {
        return saveCollectionButton;
    }

    public void setSaveCollectionButton(HtmlCommandButton hcb) {
        this.saveCollectionButton = hcb;
    }

    private HtmlCommandButton button7 = new HtmlCommandButton();

    public HtmlCommandButton getButton7() {
        return button7;
    }

    public void setButton7(HtmlCommandButton hcb) {
        this.button7 = hcb;
    }
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditCollectionPage() {
    }



    /** 
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
        Map m = getRequestMap();
        m.put("collectionId",collId);
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
            collectionsMap.put(getLevelRepStr(elem.getLevel())+elem.getName(),elem.getId());

        }

        return collectionsMap;
    }
        
    private String[] getAllViewableCollections() {
        List <VDC> allVDC = vdcService.findAll();
        List allVDCCollections = new ArrayList();
        for (Iterator it = allVDC.iterator(); it.hasNext();) {
            VDC elem = (VDC) it.next();
            if (!elem.isRestricted() || elem.getId() ==getVDCRequestBean().getCurrentVDC().getId()){
                List <VDCCollection> collections = getCollections(elem.getId().longValue());
                List viewableCollections = new ArrayList();
                for (int i = 0; i < collections.size(); i++) {
                    VDCCollection collection = collections.get(i);
                    if ((collection.isVisible()) || collection.getOwner().getId().equals(getVDCRequestBean().getCurrentVDCId())){
                        viewableCollections.add(collection);
                    }
                }
              
                allVDCCollections.addAll(viewableCollections);
            }
        }
        String [] allVDCs = new String[allVDCCollections.size()];
        int i=0;
        for (Iterator it = allVDCCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            String name=null;
            if (elem.isVisible()){
                name = getLevelRepStr(elem.getLevel()) + elem.getName()+ " (Visible)";
            } else{
                name = getLevelRepStr(elem.getLevel()) + elem.getName()+ " (Hidden)";
            }
            allVDCs[i++] = name;

        }
        return allVDCs;
    }
    
    private String[] getVDCCollections(List <VDCCollection> vdcCollections){
        String[] thisVDCCollections = new String[vdcCollections.size()+1];
        int i = 0;
        for (Iterator it = vdcCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            thisVDCCollections[i++] = getLevelRepStr(elem.getLevel()) + elem.getName();
        }
        return thisVDCCollections;
    }

    private String[] getVDCCollections(long vdcId) {
        /*
        VDC thisVDC = vdcService.find(new Long(vdcId));
        VDCCollection thisVDCRootCollection = thisVDC.getRootCollection();
        Collection <VDCCollRelationship> thisVDCRootCollectionRelationships = thisVDCRootCollection.getVdcCollRelationships();
         **/
        List thisVDC = getCollections(vdcId);
        String[] thisVDCCollections = new String[thisVDC.size() ];
        int i = 0;
        for (Iterator it = thisVDC.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            thisVDCCollections[i++] = getLevelRepStr(elem.getLevel()) + elem.getName();
        }
       return thisVDCCollections;
    }

    public String queryStudies(){
        VDCCollection editCollection = vdcCollectionService.find(vdcCollection.getId());
        editCollection.setQuery((String) textAreaQuery.getValue());
        editCollection.setVisible(true);
        vdcCollectionService.edit(editCollection);
        StatusMessage msg = new StatusMessage();
        msg.setMessageText("Collection "+ editCollection.getName()+ " has been modified");
        msg.setStyleClass("successMessage");
        Map m = getRequestMap();
        m.put("statusMessage",msg);
        return "manageCollections";
        
    }
    
    public String saveCollection(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        Object[] s = (Object[]) addRemoveList2DefaultOptions.getSelectedValue();
        VDCCollection editCollection = vdcCollectionService.find(vdcCollection.getId());
        editCollection.setName((String)textField1.getValue());
        ArrayList studies = new ArrayList();
        for (int i=0;i<s.length;i++){
            String studyStr =  s[i].toString();
            Long studyId = new Long(Long.parseLong(studyStr));
            Study study = studyService.getStudy(studyId);
            study.getStudyColls().add(editCollection);
            studies.add(study);
        }
        editCollection.setStudies(studies);
        editCollection.setOwner(thisVDC);
        
        vdcCollectionService.edit(editCollection);
        StatusMessage msg = new StatusMessage();
        msg.setMessageText("Collection "+ editCollection.getName()+ " has been modified");
        msg.setStyleClass("successMessage");
        Map m = getRequestMap();
        m.put("statusMessage",msg);
        return "manageCollections";
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

    private int buildList( ArrayList collections, Collection<VDCCollection> vdcCollections,int level) {
            level++;
        for (Iterator it = vdcCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            elem.setLevel(level);
            collections.add(elem);
            Collection <VDCCollection> subcollections = elem.getSubCollections();
            if (subcollections.size()>0){
                buildList(collections,subcollections,level);
            }
        }
        level--;
        return level;
    }

    private String getLevelRepStr(int i) {
        StringBuffer levelRep = new StringBuffer();
        for (int level = 0; level < i; level++) {
            levelRep.append('-');
        }
        return levelRep.toString();
    }
    

    public String[] getSearchFields() {
        VDC vdc = vdcService.find(new Long(vdcId));
        Collection advSearchFields = vdc.getAdvSearchFields();
        String[] fields = new String[advSearchFields.size()];
        int i=0;
        for (Iterator it = advSearchFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            fields[i++]=elem.getDescription();
            searchFields.put(messages.getString(elem.getName()),elem.getName());
            
        }
        return fields;
    }
    
    public void selectCollectionsStudies(ActionEvent e){
       String collectionNameStr = (String) dropdown4.getValue();
       int x =collectionNameStr.indexOf(" (Hidden)");
       if (x>-1){
           collectionNameStr = collectionNameStr.substring(0,x);
       }else {
           x=collectionNameStr.indexOf(" (Visible)");
           if (x>-1){
               collectionNameStr = collectionNameStr.substring(0,x);
           }
       }
//        Map m = getRequestMap();
//        m.put("collectionId",collId);
       Long collectionId = (Long) collectionsMap.get(collectionNameStr);
       long collectionIdSelected = collectionId.longValue() ;
       VDCCollection collectionSelected = vdcCollectionService.find(new Long(collectionIdSelected));
       Collection <Study> collectionStudies = collectionSelected.getStudies();
       List <Study> viewableStudies = new ArrayList();
       for (Iterator it = collectionStudies.iterator(); it.hasNext();) {
           Study elem = (Study) it.next();
           if (isViewable(elem)){
               viewableStudies.add(elem);
           }
           
       }
       int i=0;
       Option [] studies = new Option[viewableStudies.size()];
       for (Iterator it = viewableStudies.iterator(); it.hasNext();) {
           Study elem = (Study) it.next();
           if (isViewable(elem)){
           studies[i++]= new Option(Long.toString(elem.getId().longValue()),elem.getTitle());
           }
       }

       addRemoveList2.setItems(studies);       
    }

    private boolean isViewable(Study s){
        boolean viewable;
        viewable = ((!s.isRestricted() || isUserAllowed(s.getAllowedUsers(),getVDCSessionBean().getLoginBean().getUser().getId())) && !s.getReviewState().getId().equals(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW).getId()) && !s.getReviewState().getId().equals(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_NEW).getId()) && (!s.getOwner().isRestricted() || s.getOwner().getId().equals(getVDCSessionBean().getLoginBean().getCurrentVDC().getId())));
        return viewable;

    }

    private boolean isUserAllowed(Collection allowedList, Long userId){
        boolean userAllowed = false;
        for (Iterator it = allowedList.iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            if (elem.getId().equals(userId)){
                userAllowed = true;
                break;
            }
        }
        return userAllowed;
    }

    public void saveLink() {
    }

}

