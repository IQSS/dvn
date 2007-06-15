/*
 * AnalysisPage.java
 *
 * Created on October 12, 2006, 12:13 AM
 *
 */

package edu.harvard.hmdc.vdcnet.web.subsetting;

/**
 *
 * @author asone
 */


import static java.lang.System.*;

import java.util.*;
import java.util.Map.*;
import java.util.regex.*;
import java.util.Collection.*;



// jsf classes
import javax.faces.component.*;
import javax.faces.component.UIComponent.*;
import javax.faces.component.html.*;
import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.FacesException;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.render.ResponseStateManager;


// new VDCRequestBean VDCRequestBean
import javax.ejb.EJB;

import edu.harvard.hmdc.vdcnet.study.DataTable;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.SummaryStatistic;
import edu.harvard.hmdc.vdcnet.study.SummaryStatisticType;
import edu.harvard.hmdc.vdcnet.study.VariableCategory;
import edu.harvard.hmdc.vdcnet.study.StudyFile;

import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
// java studio creator's classes
import com.sun.rave.web.ui.component.*;
import com.sun.rave.web.ui.model.*;

// the following package is deprecaged
// the new name is 
// com.sun.rave.faces.data 

import com.sun.jsfcl.data.*;

import javax.servlet.http.*;
import edu.harvard.hmdc.vdcnet.dsb.DSBWrapper;
import java.io.*;


public class AnalysisPage extends VDCBaseBean {

// <editor-fold desc="inital settings">
  
  
    @EJB 
    private VariableServiceLocal variableService;


    /**
     * Holds value of property dataTable.
     */
    private edu.harvard.hmdc.vdcnet.study.DataTable dataTable;
    
    /**
     * Getter for property dataTable.
     * @return Value of property dataTable.
     */
/*
    public edu.harvard.hmdc.vdcnet.study.DataTable getDataTable() {
        return dataTable;
    }
*/
    /**
     * Setter for property dataTable.
     * @param study New value of property dataTable.
     */
/*
    public void setDataTable(edu.harvard.hmdc.vdcnet.study.DataTable dataTable) {
        this.dataTable = dataTable;
    }
*/
    /**
     * Holds value of property dtId.
      specified as a managed-property in managed-beans.xml
     <property-class>java.lang.Long</property-class>
     <value>#{param.dtId}</value>
     */
    private Long dtId;

    /**
     * Getter for property dtId.
     * @return Value of property dtId.
     */
    public Long getDtId() {
        return this.dtId;
    }

    /**
     * Setter for property dtId.
     * @param dtId New value of property dtId.
     */
    public void setDtId(Long dtId) {
        this.dtId = dtId;
    }

    public VariableServiceLocal getVariableService() {
        return variableService;
    }
  
  private void _init() throws Exception {
    
    radioButtonGroup1DefaultOptions.setOptions(
      new Option[] {
        new Option("0", "Use average values (setx default)"), 
        new Option("1", "Select values")
      }
    );
    
    radioButtonGroup1DefaultOptions.setSelectedValue("0");
    
    // zelig output option set
    checkboxGroup2DefaultOptions.setOptions(
      new Option[] {
        new Option("Summary", "Include Summary Statistics"), 
        new Option("Plots", "Include Plot"), 
        new Option("BinOutput", "Include Replication Data")
      }
    );
    
    checkboxGroup2DefaultOptions.setSelectedValue(new Object[] {"Summary", "Plots","false"});
    
    // advStat: xtab output options
    checkboxGroupXtbOptions.setOptions(
      new Option[] {
        new Option("xtb_ExtraTables", "Include Totals"), 
        new Option("xtb_Statistics", "Include Statistics"), 
        new Option("xtb_Totals", "Include Percentages"),
        new Option("xtb_Percentages", "Include Extra Tables")
      }
    );

    checkboxGroupXtbOptions.setSelectedValue(
      new Object[] {"xtb_ExtraTables", "xtb_Statistics","xtb_Totals", "false"}
    );

    // set the number of rows to be displayed
    howManyRowsOptions.setOptions(
      new Option[] {
        new Option("10", "10 Variables"), 
        new Option("20", "20 Variables"), 
        new Option("50", "50 Variables"),
        new Option("0", "All")
      }
    );
    
    howManyRowsOptions.setSelectedValue("20");

    setStudyUIclassName("edu.harvard.hmdc.vdcnet.web.study.StudyUI");

    setModelMenuOptions(getAnalysisApplicationBean().getModelMenuOptions());
    
    // setup the Map for switch in checkVarType()
    dataType2Int.put("binary",Integer.valueOf("1"));
    dataType2Int.put("nominal",Integer.valueOf("2"));
    dataType2Int.put("nominal|ordinal",Integer.valueOf("23"));
    dataType2Int.put("ordinal|nominal",Integer.valueOf("32"));
    dataType2Int.put("ordinal",Integer.valueOf("3"));
    dataType2Int.put("discrete",Integer.valueOf("4"));
    dataType2Int.put("continuous",Integer.valueOf("5"));
    dataType2Int.put("any",Integer.valueOf("0"));

    subsettingPageAccess =Boolean.TRUE;

    vtInt2String.put("2","continuous");
    vtInt2String.put("1","discrete");
    vtInt2String.put("0","character");
    
    sumStatHeaderCntn.put("mean","mean");
    sumStatHeaderCntn.put("medn","median");
    sumStatHeaderCntn.put("mode","mode");
    sumStatHeaderCntn.put("vald","valid cases");
    sumStatHeaderCntn.put("invd","invalid cases");
    sumStatHeaderCntn.put("min","minimum");
    sumStatHeaderCntn.put("max","maximum");
    sumStatHeaderCntn.put("stdev","standard deviation");
    
    
  } // end of _init()
  
  
  
  private static final int PANE_DWNLD =   3;
  private static final int PANE_RECODE = 2;
  private static final int PANE_EDA =    4;
  private static final int PANE_ADVSTAT= 5;
  
  private Boolean subsettingPageAccess;
  private int clickedTab=3;
  
  // </editor-fold>
  // <editor-fold  desc="components">
  
  
  // @value: options for dropDown menu
  private List<Option> modelMenuOptions = new ArrayList<Option>();
  
  public List<Option> getModelMenuOptions (){
    return this.modelMenuOptions;
  }
  private void setModelMenuOptions( List<Option> sig){
    this.modelMenuOptions = sig;
  }

  
  private String dataTableId;
  
  // Map as a variable-shopping cart
  //public Map<Long, String> varCart= new HashMap<Long, String>();
  public Map<String, String> varCart= new HashMap<String, String>();

  // Map for switch statement in checkVarType()
  
  public Map<String, Integer> dataType2Int = new HashMap<String, Integer>();

  public Map<String, String> vtInt2String = new HashMap<String, String>();
  // study-related data
  
    private String studyUIclassName;
    public void setStudyUIclassName(String suiClass) {
        studyUIclassName = suiClass;
    }
    public String getStudyUIclassName() {
        return studyUIclassName;
    }

    private String citation;
    public String getCitation(){
      return citation;
    }
    public void setCitation(String c){
      citation=c;
    }

    private String studyTitle;
    
    public String getStudyTitle() {
        return studyTitle;
    }
    
    public void setStudyTitle(String sTl) {
        studyTitle = sTl;
    }

    private Long studyId;
    
    public Long getStudyId() {
        return studyId;
    }
    
    public void setStudyId(Long sId) {
        studyId = sId;
    }


    private String fileName;
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fNm) {
        fileName = fNm;
    }
    

    private String studyURL;
    
    public String getAtudyURL() {
        return studyURL;
    }
    
    public void setStudyURL(String url) {
        studyURL = url;
    }

  // ends here


// page structure components

    private HtmlPanelGrid gridPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel1() {
        return gridPanel1;
    }

    public void setGridPanel1(HtmlPanelGrid hpg) {
        this.gridPanel1 = hpg;
    }

  // tab
    private TabSet tabSet1 = new TabSet();

    public TabSet getTabSet1() {
        return tabSet1;
    }

    public void setTabSet1(TabSet ts) {
        this.tabSet1 = ts;
    }
    
    
    private Tab tabDwnld = new Tab();
    public Tab getTabDwnld() {
        return tabDwnld;
    }
    // tabDwnld: ui:tab@actionListener
    
    public void resetVariableInLBox(ActionEvent acev){
      
      out.println("Within resetVariableInLBox: tab Id ="+acev.getComponent().getId());
      // remove vars from RHS boxes
      advStatVarRBox1.clear();
      advStatVarRBox2.clear();
      advStatVarRBox3.clear();

      // add existing vars to LHS box
      // add user-defined vars to LHS box if available
      resetVarSetAdvStat(varCart);
    }
    

    public void setTabDwnld(Tab tab3) {
        this.tabDwnld = tab3;
    }

    private Tab tabRecode = new Tab();
    public Tab getTabRecode() {
        return tabRecode;
    }

    public void setTabRecode(Tab tab2) {
        this.tabRecode = tab2;
    }

    private Tab tabEda = new Tab();
    
    public Tab getTabEda() {
        return tabEda;
    }

    public void setTabEda(Tab tab4) {
        this.tabEda = tab4;
    }

    private Tab tabAdvStat = new Tab();
    
    public Tab getTabAdvStat() {
        return tabAdvStat;
    }
    public void setTabAdvStat(Tab tab5) {
        this.tabAdvStat = tab5;
    }

    private String currentTabId;
    public String getCurrentTabId(){
        return currentTabId;
    }
    public void setCurrentTabId(String tb){
      this.currentTabId = tb;
    }
//--------------------------------------------------------------------------//
// download section
//--------------------------------------------------------------------------//

    // download radio button selection
    private HtmlSelectOneRadio dwnldFileTypeSet = new HtmlSelectOneRadio();

    public HtmlSelectOneRadio getDwnldFileTypeSet() {
        return dwnldFileTypeSet;
    }

    public void setDwnldFileTypeSet(HtmlSelectOneRadio hsor) {
        this.dwnldFileTypeSet = hsor;
    }

  // download: radio button items
    
    private List dwnldFileTypeItems = null;
    
    public List getDwnldFileTypeItems(){
      if (dwnldFileTypeItems == null){
        dwnldFileTypeItems = new ArrayList();
        dwnldFileTypeItems.add("D01");//Text
        dwnldFileTypeItems.add("D04");//RData
        dwnldFileTypeItems.add("D02");//Splus
        dwnldFileTypeItems.add("D03");//Stata
        //dwnldFileTypeItems.add("SAS");//
      }
      return dwnldFileTypeItems;
    }
    
    //dwnldBttn:h:commandButton@binding
    private HtmlCommandButton dwnldButton = new HtmlCommandButton();

    public HtmlCommandButton getDwnldButton() {
      return dwnldButton;
    }

    public void setDwnldButton(HtmlCommandButton hcb) {
      this.dwnldButton = hcb;
    }

    // dwnldBttn:h:commandButton@action
    public boolean checkDwnldParameters (){
      boolean result=true;
      /*
      // param-checking conditions
      if () {
      
        result=false;
      }
      
      */
      return result;
    }
    
    
    public String dwnldAction() {
    
      if (checkDwnldParameters()){
          FacesContext cntxt = FacesContext.getCurrentInstance();

          HttpServletResponse res = (HttpServletResponse) cntxt.getExternalContext().getResponse();
          HttpServletRequest  req = (HttpServletRequest) cntxt.getExternalContext().getRequest();
          try {
              out.println("**************** within dwnldAction() ****************");
              StudyFile sf = dataTable.getStudyFile();
              //String formatType =  req.getParameter("formatType");
              String formatType = (String)dwnldFileTypeSet.getValue();
              out.println("file type from the binding="+formatType);
              //String formatType = "D01";
              
              String dsbUrl = System.getProperty("vdc.dsb.url");
              out.println("dsbUrl="+dsbUrl);

              //String serverPrefix = "http://vdc-build.hmdc.harvard.edu:8080/dvn";
              String serverPrefix = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath(); 
              //String serverPrefix = req.getScheme() +"://" + dsbUrl + ":" + req.getServerPort() + req.getContextPath(); 
              
              Map <String, List<String>> mpl= new HashMap<String, List<String>>();
              
              // if there is a user-defined (recoded) variables
              if (recodedVarSet.size()>0){
                mpl.putAll(getRecodedVarParameters());
              }
              
                out.println("citation info to be sent:\n"+citation);
                mpl.put("OfflineCitation", Arrays.asList(citation));
                
                mpl.put("appSERVER",Arrays.asList(req.getServerName() + ":" + req.getServerPort() + req.getContextPath()));
                //mpl.put("appSERVER",Arrays.asList(dsbUrl + ":" + req.getServerPort() + req.getContextPath()));
                mpl.put("studytitle",Arrays.asList(studyTitle));
                mpl.put("studyno", Arrays.asList(studyId.toString()));
              
              new DSBWrapper().disseminate(res, mpl, sf, serverPrefix, getDataVariableForRequest(), formatType);
              
          } catch (IOException ex) {
              out.println("disseminate:download filed due to io exception");
              ex.printStackTrace();
          }
          
          cntxt.responseComplete();
          return "success";
          
      } else {
        out.println("download: param check failed");
        return "failure";
      }
    }
    
// end of download section
//<---------------------------------------------------------------------------

    public List<DataVariable> getDataVariableForRequest(){
      List<DataVariable> dvs = new ArrayList<DataVariable>();
        for (Iterator el = dataVariables.iterator(); el.hasNext();) {
          DataVariable dv =(DataVariable) el.next();
          String keyS = dv.getId().toString();
          if (varCart.containsKey(keyS)){
            dvs.add(dv);
          }
        }
        return dvs;
    }
    
    public Map <String, List<String>> getRecodedVarParameters(){
      Map <String, List<String>> mpl= new HashMap<String, List<String>>();
      // new var name list
      List<String> vns = new ArrayList<String>();
      //Set<String> vids = recodeSchema.keySet();
      for( Object rw : recodedVarSet){
        // add variable label
        List<Object> rwi = (List<Object>)rw;
        // [0] varName
        // [1] varLabel
        // [2] varId
        String newVarName = (String)rwi.get(0);
        String newVarLabel= (String)rwi.get(1);
        String newVarId   = (String)rwi.get(2);
        // add new varName
        vns.add(newVarName);
        mpl.put("ud_" + newVarName + "_q_lebaLrav", Arrays.asList(newVarLabel));
       // add variable type
        mpl.put("ud_"+ newVarName + "_q_epyTrav", Arrays.asList("1") ); // or 0 for character var
        // add value-label-condition
        
        
        List<Object> rdtbl  = (List<Object>)recodeSchema.get(newVarId);
        out.println("rdtbl="+rdtbl);
        List<String> delList = new ArrayList<String>();
        for ( Object rdtblrw : rdtbl){
          List<Object> rdtbli = (List<Object>)rdtblrw;
          String baseVarName = getVariableNamefromId(derivedVarToBaseVar.get(newVarId));
          out.println("rdtbli="+rdtbli);
          
          if ((Boolean)rdtbli.get(0)){
              // delete flag: boolean true
              // delete value
              //"ud_"+ {newVarName} + "_q_eteleD"     "eteleD|"+{VarName}+"|"+{condition}
              out.println("delete this value="+ rdtbli.get(3));
              delList.add("eteleD|"+baseVarName+"|"+rdtbli.get(3));

          } else {
              // delete flag: boolean false, i.e., 
              // value - label - condition
              //"ud_"+ {newVarName} + "_q_" +{value}  {label}+"|"+{VarName}+"|"+{condition}
              out.println("keep this value="+ rdtbli.get(3));
              String pmky = "ud_"+newVarName+"_q_"+rdtbli.get(1);
              out.println("pmky="+pmky);
              if (mpl.containsKey(pmky)){
                // key exits
                List<String> tmpvl = (List<String>)mpl.get(pmky);
                out.println("tmpvl:b="+tmpvl);
                String pmvl = rdtbli.get(2)+"|"+baseVarName+"|"+rdtbli.get(3);
                out.println("pmvl="+pmvl);
                tmpvl.add(pmvl );
                out.println("tmpvl:a="+tmpvl);
                //mpl.put(pmky, new ArrayList(tmpvl) );
                mpl.put(pmky, tmpvl );
                
              } else {
                List<String> pvlst = new ArrayList();
                pvlst.add(rdtbli.get(2)+"|"+baseVarName+"|"+rdtbli.get(3));
                mpl.put(pmky, pvlst);
              }
              //mpl.put("ud_"+newVarName+"_q_"+rdtbli.get(1), Arrays.asList(rdtbli.get(2)+"|"+baseVarName+"|"+rdtbli.get(3)) );
          }
          
        } // for:inner
        
        if (delList.size()>0) {
            mpl.put("ud_"+ newVarName + "_q_eteleD",delList);
        }
        
        
        
      } // for:outer
      // add newVarNameSet 
      mpl.put("newVarNameSet", vns);

      return mpl;
    }
    
//--------------------------------------------------------------------------->
// recode section
//--------------------------------------------------------------------------->

// ui-listbox-based solution

    // moveRecodeVarBttn:h:commandButton@binding
    private HtmlCommandButton moveRecodeVarBttn = new HtmlCommandButton();

    public HtmlCommandButton getMoveRecodeVarBttn() {
      return moveRecodeVarBttn;
    }
    
    public void setMoveRecodeVarBttn(HtmlCommandButton hcb) {
      this.moveRecodeVarBttn = hcb;
    }
    
    // moveRecodeVarBttn:h:commandButton@actionListener
    public void moveRecodeVariable(ActionEvent acev){
    
      out.println("******************** moveRecodeVariable(): begins here ********************");

        String varId = getSelectedRecodeVariable();

        out.println("recode-variable id="+varId);
        
        out.println("Is this a recoded var?["+isRecodedVar(varId)+"]");
        resetMsgSaveRecodeBttn();
if (isRecodedVar(varId)){
    // newly recoded var case
    out.println("This a recoded var["+varId+"]");
    
    List<Object> rvs= getRecodedVarSetRow(varId);
    if (rvs !=null){
    
      out.println("requested newly recoded var was found");
      out.println("new varName="+rvs.get(0));
      out.println("new varLabel="+rvs.get(1));
      
      recallRecodedVariable(varId);
      
    } else {
      out.println("requested newly recoded var was not found="+varId);
    
    }
    
} else{
        if (varId !=null){
          // set the name
          String varName= getVariableNamefromId(varId);
          out.print("recode variable Name="+varName);
          //setRecodeVariableName(varName);
          
          setCurrentRecodeVariableName(varName);
          setCurrentRecodeVariableId(varId);
          // set the label
          //setRecodeVariableLabel(getVariableLabelfromId(varId));
          
          recodeTargetVarName.setValue(varName);
          recodeTargetVarLabel.setValue(getVariableLabelfromId(varId));
          
          
          // get value/label data and set them to the table
          DataVariable dv = getVariableById(varId);
          
          Collection<VariableCategory> catStat = dv.getCategories();
          recodeDataList.clear();
          out.println("catStat.size="+catStat.size());
          if (catStat.size() > 0){
            // catStat exists
            out.println("catStat exists");
            
            for (Iterator elc = catStat.iterator(); elc.hasNext();) {
              VariableCategory dvcat = (VariableCategory) elc.next();
              List<Object> rw = new ArrayList<Object>();

              // 0th: Drop
              rw.add(new Boolean(false));
              // 1st: New value
              rw.add(dvcat.getValue());
              // 2nd: New value label
              rw.add(dvcat.getLabel());
              // conditions
              rw.add(dvcat.getValue());
              //
              recodeDataList.add(rw);
            }
            
          } else {
            // no catStat, either sumStat or too-many-categories
            out.println("catStat does not exists");
            out.println("create a default two-row table");
            for (int i=0; i<2; i++) {
              List<Object> rw = new ArrayList<Object>();
              // 0th: Drop
              rw.add(new Boolean(false));
              // 1st: New value
              rw.add("enter value here");
              // 2nd: New value label
              rw.add("enter label here");
              // conditions
              rw.add("enter a condition here");
              //
              recodeDataList.add(rw);
            }

          }
            
            // show the recodeTable
            out.println("Number of rows in this Recode Table="+recodeDataList.size());
            
            recodeTable.setRendered(true);
            addValueRangeBttn.setRendered(true);
            // keep this variable's Id
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRecodeVariableId", currentRecodeVariableId);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRecodeVariableName", currentRecodeVariableName);
            
          
        } else {
          out.println("Variable to be recoded is null");
        }
}
      out.println("******************** moveRecodeVariable(): ends here ********************");

    }
    
    public void clearRecodeTargetVarInfo(){
      out.println("pass the clear step");
      recodeTargetVarName.resetValue();
      recodeTargetVarLabel.resetValue();
    }
    
    
    // checkbox column(drop this value)
    // recodeDropValueCheckbox@binding
    private Checkbox recodeDropValueCheckbox = new Checkbox();

    public Checkbox getRecodeDropValueCheckbox() {
        return recodeDropValueCheckbox;
    }

    public void setRecodeDropValueCheckbox(Checkbox c) {
        this.recodeDropValueCheckbox = c;
    }
    
    
    // mListboxRecode:ui:listbox@binding => varSetAdvStat
    
    // mListboxRecode:ui:listbox@selected
    private String selectedRecodeVariable;
    
    public void setSelectedRecodeVariable(String s){
        selectedRecodeVariable=s;
    }
    
    public String getSelectedRecodeVariable(){
        return selectedRecodeVariable;
    }
    // recode-Variable-name for the recode-table header cell
    // h:outputText(recodeHdrVariable)
    // @value
    
    private String currentRecodeVariableName;
    
    public void setCurrentRecodeVariableName (String crv){
      this.currentRecodeVariableName= crv;
    }
    
    public String getCurrentRecodeVariableName(){
      return currentRecodeVariableName;
    }
    
    
    private String currentRecodeVariableId;
    
    public void setCurrentRecodeVariableId (String crv){
      this.currentRecodeVariableId= crv;
    }
    
    public String getCurrentRecodeVariableId(){
      return currentRecodeVariableId;
    }

    // h:inputText: recodeTargetVarName:
    // @binding
    private HtmlInputText recodeTargetVarName = new HtmlInputText();

    public HtmlInputText getRecodeTargetVarName() {
        return recodeTargetVarName;
    }

    public void setRecodeTargetVarName(HtmlInputText vn) {
        this.recodeTargetVarName = vn;
    }
    
    // @value
    private String recodeVariableName;
    
    public void setRecodeVariableName(String vn) {
      recodeVariableName=vn;
    }
    
    public String getRecodeVariableName(){
      return recodeVariableName;
    }
    


    // h:inputText: recodeTargetVarLabel
    // @binding
    private HtmlInputText recodeTargetVarLabel = new HtmlInputText();
    public HtmlInputText getRecodeTargetVarLabel() {
        return recodeTargetVarLabel;
    }

    public void setRecodeTargetVarLabel(HtmlInputText vl) {
        this.recodeTargetVarLabel = vl;
    }
    
    // @value
    private String recodeVariableLabel;

    public void setRecodeVariableLabel(String vl) {
      recodeVariableLabel=vl;
    }
    
    public String getRecodeVariableLabel(){
      return recodeVariableLabel;
    }

    // h:dataTable:recodeTable
    // @binding
    private UIData recodeTable = null;
    
    public UIData getRecodeTable(){
      return recodeTable;
    }
    
    public void setRecodeTable(UIData daTa){
      this.recodeTable=daTa;
    }
    // @value
    private List<Object> recodeDataList = new ArrayList<Object>();
    
    public List<Object> getRecodeDataList(){
      return recodeDataList;
    }
    
    public void setRecodeDataList(List<Object> dl){
      this.recodeDataList=dl;
    }

    // h:commandButton (Add Value/Range Button)
    // addValueRangeBttn@binding
    
    private HtmlCommandButton addValueRangeBttn = new HtmlCommandButton();

    public HtmlCommandButton getAddValueRangeBttn() {
      return addValueRangeBttn;
    }
    
    public void setAddValueRangeBttn(HtmlCommandButton hcb) {
      this.addValueRangeBttn = hcb;
    }
    
    // h:commandButton:addValueRangeBttn
    // @actionListener
    public void addValueRange(ActionEvent acev){
      out.println("before add: recodeDataList="+recodeDataList);
      List<Object> rw = new ArrayList<Object>();
      // 0th: Drop
      rw.add(new Boolean(false));
      // 1st: New value
      rw.add("enter value here");
      // 2nd: New value label
      rw.add("enter label here");
      // conditions
      rw.add("enter a condition here");
      // 
      recodeDataList.add(rw);
      out.println("after add: recodeDataList="+recodeDataList);
      FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodeDataList", recodeDataList);
    }
    
    // A hash map that stores recoding schema
    private Map<String,List<Object>> recodeSchema = new HashMap<String, List<Object>>();
    // private Map<String, UIData> recodeSchema = new HashMap<String, UIData>();
    
    //public void setRecodeSchema(String varId, UIData rs){
    public void setRecodeSchema(String varId, List<Object> rs){
      recodeSchema.put(varId, rs);
    }
    //public UIData getRecodeSchema (String varId){
    public List<Object> getRecodeSchema (String varId){
      List<Object> rt = recodeSchema.get(varId);
      return rt;
    }
    
    // h:commandButton(Apply Recode Button)
    // recodeBttn@binding
    private HtmlCommandButton recodeButton = new HtmlCommandButton();

    public HtmlCommandButton getRecodeButton() {
      return recodeButton;
    }
    
    public void setRecodeButton(HtmlCommandButton hcb) {
      this.recodeButton = hcb;
    }
    
    /**
     * saving the current recoding scheme
     * h:commandButton: recodeBttn
     * attr: actionListener
     * 
     */
    public void saveRecodedVariable(ActionEvent acev){
      out.println("******************** saveRecodedVariable(): begins here ********************");

      // get the current var Id (base variable's ID)
      String oldVarId = getCurrentRecodeVariableId();
      out.println("base var_id="+oldVarId);
      
      // get the varName in the input field
      String newVarName = (String)recodeTargetVarName.getValue();
      out.println("new var name="+newVarName);
      if (isRecodedVar(oldVarId)){
        // replace mode
        out.println("This variable id is found in the new variable set and recoding scheme would be updated");
        out.println("currentVarId="+oldVarId);
        replaceRecodedVariable(oldVarId);
        return;
      } else {
        // newly create
        if (isDuplicatedVariableName(newVarName)){
          // duplicated name
            out.println("The new variable name is already in use");
            msgSaveRecodeBttn.setRendered(true);
            msgSaveRecodeBttn.setText("The variable Name you entered is found among the existing variables;<br /> enter a new variable name");
            return;
        } else {
          // sanity check against the name
          String whiteSpace="\\s+";
          String prohibitedChars = "\\W+";
          String firstChar ="^[^a-zA-Z]";
          if (isVariableNameValid(newVarName, whiteSpace)){
            // whitespace found
            msgSaveRecodeBttn.setRendered(true);
            msgSaveRecodeBttn.setText("A whitespace character was found in the variable name;<br />Whitesapce characters are not allowed in a variable name.");
            return;
          } else if (isVariableNameValid(newVarName, prohibitedChars)) {
            // non-permissible character found
            msgSaveRecodeBttn.setRendered(true);
            msgSaveRecodeBttn.setText("At least one non-permissible character was found in the variable name;<br />Use a-z, A-Z, _, 0-9 characters.");
            return;
          } else if (isVariableNameValid(newVarName, firstChar)) {
            // non-permissible character found
            msgSaveRecodeBttn.setRendered(true);
            msgSaveRecodeBttn.setText("The first character of a variable name must be an alphabet character.");
            return;
          } else {
            // unique and safe name
            out.println("The new variable name is unique");
            msgSaveRecodeBttn.setRendered(false);
            //msgSaveRecodeBttn.setText("The variable name is unique");
          }
        }
      }
      
      // get the varLabel in the input field
      String newVarLabel = (String)getRecodeTargetVarLabel().getValue();
      out.println("new var Label="+newVarLabel);
      
      // create a new var Id
      // new-case only
      
      StringBuilder sb = new StringBuilder(oldVarId+"_");
      sb.append(varIdGenerator(oldVarId));
      String newVarId = sb.toString();
      out.println("newVarId="+newVarId);
      FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRecodeVariableId", newVarId);

      // new-case only
      varCart.put( newVarId, newVarName);
      // new only
      getVarSetAdvStat().add( new Option( newVarId, newVarName ) );
      
      
      // add this new var to the old2new mapping table
      // new-case only
      if (baseVarToDerivedVar.containsKey(oldVarId)){
          // already used for recoding
          Set<String> tmps = baseVarToDerivedVar.get(oldVarId);
          if (tmps.contains(newVarId)){
            out.println("This new var Id ["+newVarId+"] is found in the set");
          } else {
            tmps.add(newVarId);
          }
      } else {
          // not-yet used for recoding
          Set<String> tmps = new HashSet<String>();
          out.println("This new var Id ["+newVarId+"] is NOT found in the set");
          tmps.add(newVarId);
          baseVarToDerivedVar.put(oldVarId, tmps);
      }
      out.println("old-2-new-var map="+baseVarToDerivedVar);
      
      // remove a row whose condition cell is blank
      // both
      out.println("start normalization");
      for (int i=(recodeDataList.size()-1); i>=0; i--) {
          List<Object> row  = (List<Object>) recodeDataList.get(i);
          String raw = removeWhiteSpacesfromBothEnds((String)row.get(3));
          out.println("after removing white spaces[="+raw+"]");
         
          if (raw.equals("")){
            recodeDataList.remove(i);
            out.println("element["+i+"] was removed");
          }
      }
      out.println("end of normalization");
      
      out.println("recodeDataList="+recodeDataList);
      // saving the data for the recode-table
      // new and replace: both cases
      // replace-case: remove the key first?
      recodeSchema.put(newVarId, new ArrayList(recodeDataList));
      
      // update the value-label-mapping-data storage
      // new and replace: both cases
      FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodeSchema", recodeSchema);
      out.println("recodeSchema="+recodeSchema);
      
      
      if (!recodeVarNameSet.contains(newVarName)){
        // 1st-time save
      
        // add this var to the mapping table
        // add this new var to the new2old mappting table
        derivedVarToBaseVar.put(newVarId, oldVarId);
        out.println("new-2-old-var map="+derivedVarToBaseVar);

        // add this var's name, label, id to the backing object (recodedVarSet)
        List<Object> rw = new ArrayList<Object>();
        // [0]
        rw.add(new String(newVarName));
        // [1]
        rw.add(new String(newVarLabel));
        // [2]
        rw.add(newVarId);
        
        recodedVarSet.add(rw);
        // update recodeVarSet for the recodedVarTable
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodedVarSet", recodedVarSet);
        // add this newly created var to the set
        recodeVarNameSet.add(newVarName);
        
        //
        
      } else {
        // 2nd-time save
        // not required to add this var to mapping tables
      }
      // show the recoded-var table
      //recodedVarTable.setRendered(true);
      out.println("recodeVarSet="+recodedVarSet);
      
      // save recode-source variable name for the header
      FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRecodeVariableName", currentRecodeVariableName);

      
      out.println("******************** saveRecodedVariable(): ends here ********************");
    }
    
    public String removeWhiteSpacesfromBothEnds(String src){
      String s = src.replaceAll("\\s+$", "");
      return s.replaceAll("^\\s+","");
    }

    public boolean isDuplicatedVariableName(String newVarName){
        boolean rtvl=false;
        // against the set of the existing variable names
        for (Iterator el = dataVariables.iterator(); el.hasNext();) {
          DataVariable dv =(DataVariable) el.next();
          if (dv.getName().equals(newVarName)){
            rtvl = true;
            break;
          }
        }
      return rtvl;
    }
    
    

    public boolean isVariableNameValid(String newVarName, String regex){
      boolean rtvl=false;
      // 
      Pattern p = null;
      try {
        p=Pattern.compile(regex);
      } catch (PatternSyntaxException pex) {
        pex.printStackTrace();
        
      }
      Matcher matcher = p.matcher(newVarName);
      rtvl = matcher.find();
      return rtvl;
    }
    

    
    
    // Map from base-variable=>derived variable by varId
    // old(one) => new(many)
    private Map<String, Set<String>> baseVarToDerivedVar = new HashMap<String, Set<String>>();
    
    // new(one) => old(one)
    private Map<String, String> derivedVarToBaseVar = new HashMap<String,String>();
    
    // for duplication check
    private Set<String> recodeVarNameSet = new HashSet<String>();
    
    
    // newly derived variable's ID generator
    public String varIdGenerator (String varId){
      int lstlen = 0;
      if (baseVarToDerivedVar.containsKey(varId)){
        lstlen = baseVarToDerivedVar.get(varId).size();
      }
        return  Integer.toString(lstlen);
    }
   
   
   // errormessage for recode-save button
   // ui:staticText
    private StaticText msgSaveRecodeBttn = new StaticText();

    public StaticText getMsgSaveRecodeBttn() {
        return msgSaveRecodeBttn;
    }

    public void setMsgSaveRecodeBttn(StaticText txt) {
        this.msgSaveRecodeBttn = txt;
    }
    
    public void resetMsgSaveRecodeBttn() {
      out.println("******* within resetMsgSaveRecodeBttn *******");
      msgSaveRecodeBttn.setRendered(false);
      msgSaveRecodeBttn.setText(" ");
    }
    
    /**
     * remove a recoded Variable from the cart
     * 
     * ui:hyperlink
     * attr: actionListener
     * see recode block above
     */
    public void removeRecodedVariable(ActionEvent e){
      out.println("******************** removeRecodedVariable(): begins here ********************");

      // get data stored in the event row
      List<Object> tmpRecodeVarLine = (List<Object>) getRecodedVarTable().getRowData();
      // get varId as a key of recodeSchema
      String newVarId =(String) tmpRecodeVarLine.get(2);
      String newVarName = (String) tmpRecodeVarLine.get(0);
      out.println("recoded-var id="+newVarId);
      // clear the error message if it still exists
      resetMsgVariableSelection();

      
      // remove this recoded var from the value-label storage Map (recodeSchema)
      if (recodeSchema.containsKey(newVarId)){
        recodeSchema.remove(newVarId);
      } else {
        out.println("value-label table of this var ["+newVarId+"] is not found");
      }
      // remove this recoded var from the recoded-var table (recodedVarSet)
      
      for (int i=0;i<recodedVarSet.size();i++){
        List<Object> rvs = (List<Object>)recodedVarSet.get(i); 
        String iter = (String) rvs.get(2);
        if (newVarId.equals(iter)){
          out.println("iter="+i+" th element removed");
          recodedVarSet.remove(i);
          break;
        }
      }
      // remove this key from the old-2-new mapping table
      String oldVarId = derivedVarToBaseVar.get(newVarId);
      if (baseVarToDerivedVar.containsKey(oldVarId)){
      
          Set<String> tmps = baseVarToDerivedVar.get(oldVarId);
          if (tmps.contains(newVarId)){
            out.println("This new var Id ["+newVarId+"] is found in the set and to be removed");
            tmps.remove(newVarId);
            if (tmps.size()<1){
              out.println("There is no recoded var for this base-var(id="+oldVarId+" name="+getVariableNamefromId(oldVarId)+")");
              baseVarToDerivedVar.remove(oldVarId);
            } else {
              out.println("The set is "+tmps.size()+" for this base-var(id="+oldVarId+" name="+getVariableNamefromId(oldVarId)+")");
            }
          } else {
            out.println("This new var Id ["+newVarId+"] is NOT found in the set");
          }

      
        derivedVarToBaseVar.remove(newVarId);
      } else {
        out.println("recoded variable ["+newVarId+"] is not found in the new2old mapping table");
      }

      
      // remove this key from the new-2-old map
      if (derivedVarToBaseVar.containsKey(newVarId)){
        derivedVarToBaseVar.remove(newVarId);
      } else {
        out.println("recoded variable ["+newVarId+"] is not found in the new2old mapping table");
      }
      
      
      
      
      // if this variable is in the recode-table, 
      String currentVarNameInBox = (String)recodeTargetVarName.getValue();
      out.println("currentVarName in inputBox="+currentVarNameInBox);
      if (newVarName.equals(currentVarNameInBox)){
        out.println("The variable in the recode table is the variable to be removed");
        // clear the table
        recodeDataList.clear();
        // reset the current variable Id
        setCurrentRecodeVariableName(null);
        setCurrentRecodeVariableId(null);
        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRecodeVariableId", currentRecodeVariableId);
        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRecodeVariableName", currentRecodeVariableName);

        // reset variable name and label
        recodeTargetVarName.resetValue();
        recodeTargetVarLabel.resetValue();
        // hide the recode table and add-row button
        recodeTable.setRendered(false);
        addValueRangeBttn.setRendered(false);



        varCart.remove( newVarId );
        // remove the existing option first
        removeOption(newVarId, getVarSetAdvStat());
      } else {
        out.println("The variable in the recode table differs from the variable to be removed");
      }
      
      out.println("******************** removeRecodedVariable(): ends here ********************");
    }
    
    public boolean isRecodedVar(String varId){
        Pattern p=Pattern.compile("_");
        Matcher m = p.matcher(varId);
        return m.find();
    }
    
    public String getNewVarName(String newVarId){
      for (int i=0;i<recodedVarSet.size();i++){
        List<Object> rvs = (List<Object>)recodedVarSet.get(i); 
        String iter = (String) rvs.get(2);
        if (newVarId.equals(iter)){
          out.println("recode data(name) found at "+i+" th row");
          return  (String)rvs.get(0);
        }
      }
      return null;
    }
    
    public String getNewVarLabel(String newVarId){
      for (int i=0;i<recodedVarSet.size();i++){
        List<Object> rvs = (List<Object>)recodedVarSet.get(i); 
        String iter = (String) rvs.get(2);
        if (newVarId.equals(iter)){
          out.println("recode data(label) found at "+i+" th row");
          return  (String)rvs.get(1);
        }
      }
      return null;
    }
    
    public List<Object> getRecodedVarSetRow(String newVarId){
      for (int i=0;i<recodedVarSet.size();i++){
        List<Object> rvs = (List<Object>)recodedVarSet.get(i); 
        String iter = (String) rvs.get(2);
        if (newVarId.equals(iter)){
          out.println("recode data(row) found at "+i+" th row");
          return  rvs;
        }
      }
      return null;
    }
    
    
    // editRecodedVariable
    // ui:hyperlink @actionListener
    // se recode block above
    
    public void editRecodedVariable(ActionEvent e){

        // get data stored in the event row
        List<Object> tmpRecodeVarLine = (List<Object>) getRecodedVarTable().getRowData();
        out.println("current recodedVar row:size="+tmpRecodeVarLine.size());
        out.println("current recodedVar row="+tmpRecodeVarLine);
        // get varId
        String newVarId = (String) tmpRecodeVarLine.get(2);
        out.println("recoded-var id="+newVarId);
        
        out.println("Is this a recoded var?["+isRecodedVar(newVarId)+"]");
        // set this varId's list to the recodeTable
        
        if (recodeSchema.containsKey(newVarId)){
          setRecodeDataList((List<Object>)recodeSchema.get(newVarId));
          
          recodeTable.setValue( (List<Object>)recodeSchema.get(newVarId) );//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodeDataList", recodeDataList);
          out.println("contents of new value-label set="+(List<Object>)recodeSchema.get(newVarId));
          out.println("contents of new value-label set="+recodeDataList);
          clearRecodeTargetVarInfo();
          // update the current recode-Variable's ID
          String [] tmp=null;
          tmp = newVarId.split("_");
          out.println("base-var Id from new var id="+tmp[0]);
          out.println("base-var Id from the map="+derivedVarToBaseVar.get(newVarId));
          // setCurrentRecodeVariableId(tmp[0]);
          
          currentRecodeVariableId=tmp[0];
          FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRecodeVariableId", currentRecodeVariableId);
          
          
          // update the varName
          //setRecodeVariableName( (String) tmpRecodeVarLine.get(0));
          
          // update the varLabel
          //setRecodeVariableLabel((String) tmpRecodeVarLine.get(1));
          
          
          recodeTargetVarName.setValue((String) tmpRecodeVarLine.get(0));
          recodeTargetVarLabel.setValue((String) tmpRecodeVarLine.get(1));

          //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodeVariableName", recodeVariableName);
          //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodeVariableLabel", recodeVariableLabel);
          
          FacesContext.getCurrentInstance().renderResponse();
          
        } else {
          out.println("value-label table of this var ["+newVarId+"] is not found");
        }
        
    }


    public void recallRecodedVariable(String newVarId){
      out.println("******************** recallRecodedVariable(): begins here ********************");

      // get data stored in a row of the recodedVarTable by Id
      List<Object> rvs= getRecodedVarSetRow(newVarId);
      if (rvs !=null){
        out.println("requested newly recoded var was found");
        out.println("new varName="+rvs.get(0));
        out.println("new varLabel="+rvs.get(1));
        
        // set this varId's list to the recodeTable
        
        if (recodeSchema.containsKey(newVarId)){
          setRecodeDataList((List<Object>)recodeSchema.get(newVarId));
          
          recodeTable.setValue( (List<Object>)recodeSchema.get(newVarId) );
          out.println("contents of new value-label set="+(List<Object>)recodeSchema.get(newVarId));
          out.println("contents of new value-label set="+recodeDataList);
          FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodeDataList", recodeDataList);
          
          clearRecodeTargetVarInfo();
          
          // update the current recode-Variable's ID
          String [] tmp=null;
          tmp = newVarId.split("_");
          out.println("base-var Id from new var id="+tmp[0]);
          out.println("base-var Id from the map="+derivedVarToBaseVar.get(newVarId));
          
          //currentRecodeVariableId=tmp[0];
          currentRecodeVariableId = newVarId;
          FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRecodeVariableId", currentRecodeVariableId);
          
          
          recodeTargetVarName.setValue((String) rvs.get(0));
          recodeTargetVarLabel.setValue((String) rvs.get(1));


          FacesContext.getCurrentInstance().renderResponse();
          
        } else {
          out.println("value-label table of this var ["+newVarId+"] is not found");
        }
      } else {
        out.println("requested newly recoded var was not found="+newVarId);
      }
      out.println("******************** recallRecodedVariable(): ends here ********************");

    }


    // updating the current recoding scheme
    public void replaceRecodedVariable(String newVarId){
    
      out.println("******************** replaceRecodedVariable(): begins here ********************");
      out.println("current var id (from: args)="+newVarId);
      out.println("current var id (from: method)="+getCurrentRecodeVariableId());
      
      // get the latest varName in the input field
      String newVarName = (String)recodeTargetVarName.getValue();
      out.println("new var Name ="+newVarName);
      
      
      // sanity check against the name
      String whiteSpace="\\s+";
      String prohibitedChars = "\\W+";
      String firstChar ="^[^a-zA-Z]";
      if (isVariableNameValid(newVarName, whiteSpace)){
        // whitespace found
        msgSaveRecodeBttn.setRendered(true);
        msgSaveRecodeBttn.setText("A whitespace character was found in the variable name;<br />Whitesapce characters are not allowed in a variable name.");
        return;
      } else if (isVariableNameValid(newVarName, prohibitedChars)) {
        // non-permissible character found
        msgSaveRecodeBttn.setRendered(true);
        msgSaveRecodeBttn.setText("At least one non-permissible character was found in the variable name;<br />Use a-z, A-Z, _, 0-9 characters.");
        return;
      } else if (isVariableNameValid(newVarName, firstChar)) {
        // non-permissible character found
        msgSaveRecodeBttn.setRendered(true);
        msgSaveRecodeBttn.setText("The first character of a variable name must be an alphabet character.");
        return;
      } else {
        // unique and safe name
        out.println("The new variable name is unique");
        msgSaveRecodeBttn.setRendered(false);
        //msgSaveRecodeBttn.setText("The variable name is unique");
      }


      // get the latest varLabel in the input field
      String newVarLabel = (String)getRecodeTargetVarLabel().getValue();
      out.println("new var Label="+newVarLabel);
      
      // create a new var Id
      // new-case only
      out.println("newVarId="+newVarId);
      
      // replace-case only
      // remove the current varId-varName pair: variable name might have been updated
      varCart.remove(newVarId);
      
      // new and replace: both cases
      varCart.put(newVarId, newVarName);
      
      // replace only: remove the existing option first
      removeOption(newVarId, getVarSetAdvStat());
      // new and replace: both cases
      getVarSetAdvStat().add( new Option(newVarId, newVarName));
      
      
      // add this new var to the old2new mapping table
      // new-case only
      out.println("old-2-new-var map="+baseVarToDerivedVar);
      
      // remove a row whose condition cell is blank
      // both
      out.println("start normalization");
      for (int i=(recodeDataList.size()-1); i>=0; i--) {
          List<Object> row  = (List<Object>) recodeDataList.get(i);
          String raw = removeWhiteSpacesfromBothEnds((String)row.get(3));
          out.println("after removing white spaces[="+raw+"]");
         
          if (raw.equals("")){
            recodeDataList.remove(i);
            out.println("element["+i+"] was removed");
          }
      }
      out.println("end of normalization");
      
      out.println("recodeDataList="+recodeDataList);
      // saving the data for the recode-table
      // replace-case: remove the existing entry first
      recodeSchema.remove(newVarId);
      
      // new and replace: both cases
      recodeSchema.put(newVarId, new ArrayList(recodeDataList));
      
      // update the value-label-mapping-data storage
      // new and replace: both cases
      FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodeSchema", recodeSchema);
      out.println("recodeSchema="+recodeSchema);
      
      
      // replace case
      // 2nd-time save
      // update the existing variable name and label because they might have been modified
      out.println("new-2-old-var map="+derivedVarToBaseVar);

      // get the current row of this recoded var
      int location=-1;
      for (int i=0; i < recodedVarSet.size();i++){
        List<Object> row = (List<Object>)recodedVarSet.get(i);
        String varId = (String)row.get(2);
        out.println(i+"-th current varId="+varId);
        if (varId.equals(newVarId)){
          out.println("The ID was found in the "+i+"-th row");
          location=i;
        }
      }
      if (location >= 0){
        List<Object> oldrw = (List<Object>)recodedVarSet.get(location);

        boolean isVariableNameUpdated = false;
        String oldVarName = (String)oldrw.get(0);
        if (oldVarName.equals(newVarName)){
          out.println("The variable name was not updated");
        } else {
          out.println("The old variable name("+oldVarName+ ") is replaced by "+newVarName);

          // The variable name has been updated;
          // remove the current name from recodeVarNameSet first
          if (recodeVarNameSet.remove(oldVarName)){
            out.println("The old variable name was successfully removed");
          } else{
            out.println("The old variable name("+oldVarName+") was not removed");
          }
          isVariableNameUpdated=true;
        }
        // remove the current row first
        recodedVarSet.remove(location);

        // add the new row
        // add this var's name, label, id to the backing object (recodedVarSet)
        List<Object> rw = new ArrayList<Object>();
        // [0]
        rw.add(new String(newVarName));
        // [1]
        rw.add(new String(newVarLabel));
        // [2]
        rw.add(newVarId);
        recodedVarSet.add(rw);
        // update recodeVarSet for the recodedVarTable
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodedVarSet", recodedVarSet);

        // if the variable name has been updated,
        // remove the current name from recodeVarNameSet and 
        // add the new one to it

        if (isVariableNameUpdated){
           recodeVarNameSet.add(newVarName);
        }
      } else {
        out.println("This variable was not saved(id="+newVarId+")");
      }
      // show the recoded-var table
      //recodedVarTable.setRendered(true);
      out.println("recodeVarSet(after update)="+recodedVarSet);
      
      out.println("******************** replaceRecodedVariable(): ends here ********************");
    }


//--------------------------------------------------------------------------->
// recoded-variable Table section
//--------------------------------------------------------------------------->
/// title and message line

//  h:HtmlOutputText: recodedVarTableTitle@binding
    private HtmlOutputText recodedVarTableTitle = new HtmlOutputText();

    public HtmlOutputText getRecodedVarTableTitle() {
        return recodedVarTableTitle;
    }

    public void setRecodedVarTableTitle(HtmlOutputText hot) {
        this.recodedVarTableTitle = hot;
    }

// h:HtmlOutputText: recodedVarTableState@binding
/*
    private HtmlOutputText recodedVarTableState = new HtmlOutputText();

    public HtmlOutputText getRecodedVarTableState() {
        return recodedVarTableState;
    }

    public void setRecodedVarTableState(HtmlOutputText hot) {
        this.recodedVarTableState = hot;
    }
*/

// ui:panelGroup PGrecodedVarTable
    // @binding
    private PanelGroup pgRecodedVarTable = new PanelGroup();
    
     public PanelGroup getPgRecodedVarTable() {
         return pgRecodedVarTable;
     }
     
     public void setPgRecodedVarTable(PanelGroup pg) {
         this.pgRecodedVarTable = pg;
     }

// h:dataTable: recodedVarTable
    // @binding
    private UIData recodedVarTable = null;
    
    public UIData getRecodedVarTable() {
        return recodedVarTable;
    }

    public void setRecodedVarTable(UIData data) {
        this.recodedVarTable = data;
    }
    
    // @value
    private List<Object> recodedVarSet = new ArrayList<Object>();
    
    public void setRecodedVarSet(List<Object> dt) {
      this.recodedVarSet=dt;
    }

    public List<Object> getRecodedVarSet() {
      return recodedVarSet;
    }
    
/*
    private void removeRecodedVariable(){
    }

// Checkbox removeRecodedVarCheckbox
    // @binding
    private Checkbox removeRecodedVarCheckbox = new Checkbox();

    public Checkbox getRemoveRecodedVarCheckbox() {
       return removeRecodedVarCheckbox;
    }
    public void setRemoveRecodedVarCheckbox(Checkbox c) {
       this.removeRecodedVarCheckbox = c;
    }

// removeRecodedVar (ValueChangeEvent vce)
    // @valueChangeListener
    public void removeRecodedVar(ValueChangeEvent vce){
    
    
    }
*/
    // removeRecodedVariable
    //
    // ui:hyperlink @actionListener
    // see recode block above
    /*
    public void removeRecodedVariable(ActionEvent e){
    }
    */

    // editRecodedVariable
    // ui:hyperlink @actionListener
    // se recode block above
    /*
    public void editRecodedVariable(ActionEvent e){
    }
    */
    
    public void hideRecodeTableArea(){
      recodeTable.setRendered(false);
      addValueRangeBttn.setRendered(false);
    }

// recode section ends here
//<---------------------------------------------------------------------------

    
//--------------------------------------------------------------------------->
// eda section
//--------------------------------------------------------------------------->
    
    // LHS: list box related fields
    
    
    private HelpInline helpInline3 = new HelpInline();

    public HelpInline getHelpInline3() {
        return helpInline3;
    }

    public void setHelpInline3(HelpInline helpInline3) {
        this.helpInline3 = helpInline3;
    }

    // RHS: checkbox area related fields
    // analysis:h:selectManyCheckbox@binding
    private HtmlSelectManyCheckbox edaOptionSet = new HtmlSelectManyCheckbox();
    
    public HtmlSelectManyCheckbox getEdaOptionSet() {
        return edaOptionSet;
    }

    public void setEdaOptionSet(HtmlSelectManyCheckbox edaOptionSet) {
        this.edaOptionSet = edaOptionSet;
    }
    
    // edaOptionNumeric:f:selectItem@itemValue
    // edaOptionGraphic:f:selectItem@itemValue
    private List edaOptionItems = null;
    
    public List getEdaOptionItems(){
      if (edaOptionItems == null){
        edaOptionItems = new ArrayList();
        edaOptionItems.add("A01");
        edaOptionItems.add("A02");
      }
      return edaOptionItems;
    }
    
    // submit button
    // edaBttn:h:commandButton@binding
    private HtmlCommandButton edaButton = new HtmlCommandButton();

    public HtmlCommandButton getEdaButton() {
      return edaButton;
    }
    
    public void setEdaButton(HtmlCommandButton hcb) {
      this.edaButton = hcb;
    }
    
    // checking parameters before submission-
    public boolean checkEdaParameters (){
      boolean result=true;
      /*
      // param-checking conditions
      if () {
      
        result=false;
      }
      
      */
      return result;
    }
    
    // edaBttn:h:commandButton@action
    public String edaAction() {
        if (checkEdaParameters()){
        
            FacesContext cntxt = FacesContext.getCurrentInstance();

            HttpServletResponse res = (HttpServletResponse) cntxt.getExternalContext().getResponse();
            HttpServletRequest  req = (HttpServletRequest) cntxt.getExternalContext().getRequest();
            try {
              out.println("**************** within edaAction() ****************");

                StudyFile sf = dataTable.getStudyFile();
                
                String dsbUrl = System.getProperty("vdc.dsb.url");
                out.println("dsbUrl="+dsbUrl);
                
                //String serverPrefix = "http://vdc-build.hmdc.harvard.edu:8080/dvn";
                String serverPrefix = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath(); 
                //String serverPrefix = req.getScheme() +"://" + dsbUrl + ":" + req.getServerPort() + req.getContextPath(); 

                /*
                  "optnlst_a" => "A01|A02|A03",
                  "analysis" => "A01 A02",
                  "varbl" => "v1.3 v1.10 v1.13 v1.22 v1.40",
                  "charVarNoSet" => "v1.10|v1.719",
                */
                
                
                Map <String, List<String>> mpl = new HashMap<String, List<String>>();
                Map <String, String> mps= new HashMap<String, String>();
                mps.put("optnlst_a", "A01|A02|A03");
                Object[] vs = edaOptionSet.getSelectedValues();
                List<String> alst = new ArrayList<String>();
                
                for (int i=0;i<vs.length;i++){
                  out.println("eda option["+i+"]="+vs[i]);
                  alst.add((String)vs[i]);
                }
                //mps.put("analysis", "A01");
                mpl.put("analysis", alst);
                //List<String> aoplst = new ArrayList<String>();
                //aoplst.add("A01|A02|A03");
                //mpl.put("optnlst_a", aoplst);
                mpl.put("optnlst_a", Arrays.asList("A01|A02|A03"));
                
                
                // if there is a user-defined (recoded) variables
                if (recodedVarSet.size()>0){
                  mpl.putAll(getRecodedVarParameters());
                }
                
                out.println("citation info to be sent:\n"+citation);
                mpl.put("OfflineCitation", Arrays.asList(citation));
                
                mpl.put("appSERVER",Arrays.asList(req.getServerName() + ":" + req.getServerPort() + req.getContextPath()));
                //mpl.put("appSERVER",Arrays.asList(dsbUrl + ":" + req.getServerPort() + req.getContextPath()));
                mpl.put("studytitle",Arrays.asList(studyTitle));
                mpl.put("studyno", Arrays.asList(studyId.toString()));
                mpl.put("studyURL", Arrays.asList(studyURL));
                
                
                // disseminate(HttpServletResponse res, Map parameters, StudyFile sf, String serverPrefix, List variables)
                //new DSBWrapper().disseminate(res, mps, sf, serverPrefix, getDataVariableForRequest());
                new DSBWrapper().disseminate(res, mpl, sf, serverPrefix, getDataVariableForRequest());

            } catch (IOException ex) {
                out.println("disseminate:download filed due to io exception");
                ex.printStackTrace();
            }

            cntxt.responseComplete();
            return "success";

        
        
        
        } else {
            return "failure";
            
        }
    }


// end of eda
//<---------------------------------------------------------------------------

//--------------------------------------------------------------------------->
// AdvStat section
//--------------------------------------------------------------------------->
    // Selected variable box 
    
 
    // ui: dropDown solution
    // @binding
    private DropDown dropDown1 = new DropDown();

    public DropDown getDropDown1() {
        return dropDown1;
    }

    public void setDropDown1(DropDown dd) {
        this.dropDown1 = dd;
    }
    
    private String currentModelName;
    
    public String getCurrentModelName(){
      return currentModelName;
    }
    public void setCurrentModelName(String cmn){
      this.currentModelName=cmn;
    }
    
    // dropDown1: ui: dropDown@valueChangeListener
    public void dropDown1_processValueChange(ValueChangeEvent vce) {
        String lastModelName = getCurrentModelName();
        out.println("stored model name(get)="+lastModelName);
        out.println("stored model name="+currentModelName);
        FacesContext cntxt = FacesContext.getCurrentInstance();
        
        out.println("pass the valueChangeListener dropDown1_processValueChange");
        String newModelName = (String)vce.getNewValue();
        out.println("Newly selected model="+newModelName);
        // for the first time only
        if (!groupPanel8below.isRendered()){
          out.println("this is the first time to render the model-option panel");
          groupPanel8below.setRendered(true);
        } else {
          out.println("this is NOT the first time to render the model-option panel");
          if (!newModelName.equals(lastModelName)){
            out.println("A New Model is selected: Clear all variables in the R boxes");
            advStatVarRBox1.clear();
            advStatVarRBox2.clear();
            advStatVarRBox3.clear();
            resetVarSetAdvStat(varCart);
          }
          
        }
        
        // this model's name
        //String selectedModelName= (String)dropDown1.getSelected();
        setCurrentModelName((String)dropDown1.getSelected());
        out.println("selected model="+getCurrentModelName());
        cntxt.getExternalContext().getSessionMap().put("currentModelName",currentModelName);

        // this model's spec
        AdvancedStatGUIdata.Model selectedModelSpec = getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName());
        out.println("model info:\n"+selectedModelSpec);
        //cntxt.getExternalContext().getSessionMap().put("selectedModelSpec",selectedModelSpec);
        
        // this model's required variable boxes
        int noRboxes= selectedModelSpec.getNoRboxes();
        out.println("model info:RBoxes="+noRboxes);
        
        if (noRboxes == 1) {
          // hide 2nd/3rd panels
          groupPanel13.setRendered(false);
          groupPanel14.setRendered(false);
        
          out.println("varBoxR1 label="+selectedModelSpec.getVarBox().get(0).getLabel());
          
          // set var box label
          varListbox1Lbl.setText(selectedModelSpec.getVarBox().get(0).getLabel());
          
        } else if (noRboxes == 2) {
          // open 2nd panel and hide 3rd one
          groupPanel13.setRendered(true);
          groupPanel14.setRendered(false);
          
          out.println("varBoxR1 label="+selectedModelSpec.getVarBox().get(0).getLabel());
          out.println("varBoxR2 label="+selectedModelSpec.getVarBox().get(1).getLabel());
          
          // set var box label
          varListbox1Lbl.setText(selectedModelSpec.getVarBox().get(0).getLabel());
          varListbox2Lbl.setText(selectedModelSpec.getVarBox().get(1).getLabel());
          
        } else if (noRboxes == 3) {
          // open 2nd/3rd panels
          groupPanel13.setRendered(true);
          groupPanel14.setRendered(true);
          
          out.println("varBoxR1 label="+selectedModelSpec.getVarBox().get(0).getLabel());
          out.println("varBoxR2 label="+selectedModelSpec.getVarBox().get(1).getLabel());
          out.println("varBoxR3 label="+selectedModelSpec.getVarBox().get(2).getLabel());
          
          // set var box label
          varListbox1Lbl.setText(selectedModelSpec.getVarBox().get(0).getLabel());
          varListbox2Lbl.setText(selectedModelSpec.getVarBox().get(1).getLabel());
          varListbox3Lbl.setText(selectedModelSpec.getVarBox().get(2).getLabel());
          
        }
        
        // set up option panels
        if (getCurrentModelName().equals("xtb")){
            // cross-tabulation (non-zelig)
            checkboxGroup2.setRendered(false);
            checkboxGroupXtb.setRendered(true);
            
            analysisOptionPanel.setRendered(false);
        } else {
            // zelig
            checkboxGroup2.setRendered(true);
            checkboxGroupXtb.setRendered(false);
            analysisOptionPanel.setRendered(true);
            // show/hide setx-option panel
            if (selectedModelSpec.getMaxSetx() == 0){
              // hide analysis option
              setxOptionPanel.setRendered(false);
            } else {
              setxOptionPanel.setRendered(true);
            }
        }
           
       cntxt.renderResponse();

    }

    private String dropDown1ClientId;
    
    public String getDropDown1ClientId(){
      FacesContext context = FacesContext.getCurrentInstance();
      return dropDown1.getClientId(context);
    }
    


    // panel below the dropDown Menu
    private PanelGroup groupPanel8below = new PanelGroup();

    public PanelGroup getGroupPanel8below() {
        return groupPanel8below;
    }

    public void setGroupPanel8below(PanelGroup pg) {
        this.groupPanel8below = pg;
    }


    // label@binding for variable Left box1
    private Label varListbox1Lbl = new Label();

    public Label getVarListbox1Lbl() {
        return varListbox1Lbl;
    }

    public void setVarListbox1Lbl(Label l) {
        this.varListbox1Lbl = l;
    }

    // label@binding for variable box2
    private Label varListbox2Lbl = new Label();

    public Label getVarListbox2Lbl() {
        return varListbox2Lbl;
    }

    public void setVarListbox2Lbl(Label l) {
        this.varListbox2Lbl = l;
    }

    // label@binding for variable box3
    private Label varListbox3Lbl = new Label();

    public Label getVarListbox3Lbl() {
        return varListbox3Lbl;
    }

    public void setVarListbox3Lbl(Label l) {
        this.varListbox3Lbl = l;
    }


    // panel for the 1st var box
    // PanelGroup@binding
    private PanelGroup groupPanel12 = new PanelGroup();

    public PanelGroup getGroupPanel12() {
        return groupPanel12;
    }

    public void setGroupPanel12(PanelGroup pg) {
        this.groupPanel12 = pg;
    }

    // panel for the 2nd var box
    // PanelGroup@binding
    private PanelGroup groupPanel13 = new PanelGroup();

    public PanelGroup getGroupPanel13() {
        return groupPanel13;
    }

    public void setGroupPanel13(PanelGroup pg) {
        this.groupPanel13 = pg;
    }

    // plane for the 3rd var box
    // PanelGroup@binding
    private PanelGroup groupPanel14 = new PanelGroup();

    public PanelGroup getGroupPanel14() {
        return groupPanel14;
    }

    public void setGroupPanel14(PanelGroup pg) {
        this.groupPanel14 = pg;
    }


    // boxL1:listbox@binding
    private Listbox listboxAdvStat = new Listbox();

    public Listbox getListboxAdvStat() {
        return listboxAdvStat;
    }

    public void setListboxAdvStat(Listbox lstbx) {
        this.listboxAdvStat = lstbx;
    }

    // boxL1: listbox@items (data for the listbox)
    
    private Collection<Option> varSetAdvStat= new ArrayList<Option>();
    
    public Collection<Option> getVarSetAdvStat(){
      return varSetAdvStat;
    }
    
    public void setVarSetAdvStat(Collection<Option> co){
      this.varSetAdvStat=co;
    }
    
    // re-populating the list of Options
    public void resetVarSetAdvStat(Map vs){
      if (!varSetAdvStat.isEmpty()){
        varSetAdvStat.clear();
      }
      
      /*
      out.println("resetVarSetAdvStat: current tab id="+tabSet1.getSelected());
      if (tabSet1.getSelected().equals("tabRecode")){
        out.println("current tab is: Recode[no recoded variables added]");
      } else{
        out.println("current tab is not Recode [add recoded variables]");
      }
      */
      
      Iterator all = vs.entrySet().iterator();
      while(all.hasNext()){
        Entry entry = (Entry) all.next();
        varSetAdvStat.add(new Option( entry.getKey().toString(), (String)entry.getValue() ) );
      }
    }
    
    // boxL1: listbox@selected : storage object for selected value
    private String[] advStatSelectedVarLBox;

    public String[] getAdvStatSelectedVarLBox() {
        return advStatSelectedVarLBox;
    }

    public void setAdvStatSelectedVarLBox(String[] dol) {
        this.advStatSelectedVarLBox = dol;
    }

    // boxR1: listbox@binding
    // 
     private Listbox advStatVarListboxR1 = new Listbox();
     
     public Listbox getAdvStatVarListboxR1() {
         return advStatVarListboxR1;
     }
     public void setAdvStatVarListboxR1(Listbox l1) {
         this.advStatVarListboxR1 = l1;
     }
     
    // boxR1 : listbox@items [provide data for the listbox]

    private Collection<Option> advStatVarRBox1 = new ArrayList<Option>();

    public Collection<Option> getAdvStatVarRBox1() {
        return advStatVarRBox1;
    }
    public void setAdvStatVarRBox1(Collection<Option> dol) {
        this.advStatVarRBox1 = dol;
    }
    
    // boxR1: listbox@selected
    private String[] advStatSelectedVarRBox1;

    public String[] getAdvStatSelectedVarRBox1() {
        return advStatSelectedVarRBox1;
    }

    public void setAdvStatSelectedVarRBox1(String[] dol) {
        this.advStatSelectedVarRBox1 = dol;
    }


    // boxR2: listbox@binding
    private Listbox advStatVarListboxR2 = new Listbox();

    public Listbox getAdvStatVarListboxR2() {
       return advStatVarListboxR2;
    }
    public void setAdvStatVarListboxR2(Listbox l2) {
       this.advStatVarListboxR2 = l2;
    }

    // boxR2: listbox@items [provide data for the listbox]
    private Collection<Option> advStatVarRBox2 = new ArrayList<Option>();

    public Collection<Option> getAdvStatVarRBox2() {
        return advStatVarRBox2;
    }

    public void setAdvStatVarRBox2(Collection<Option> dol) {
        this.advStatVarRBox2 = dol;
    }

    // boxR2: listbox@selected
    private String[] advStatSelectedVarRBox2;

    public String[] getAdvStatSelectedVarRBox2() {
        return advStatSelectedVarRBox2;
    }

    public void setAdvStatSelectedVarRBox2(String[] dol) {
        this.advStatSelectedVarRBox2 = dol;
    }


    // boxR3: listbox@binding
    private Listbox advStatVarListboxR3 = new Listbox();

    public Listbox getAdvStatVarListboxR3() {
       return advStatVarListboxR3;
    }
    public void setAdvStatVarListboxR3(Listbox l3) {
       this.advStatVarListboxR3 = l3;
    }

    // boxR3: listbox@items [provide data for the listbox]
    private Collection<Option> advStatVarRBox3 = new ArrayList<Option>();

    public Collection<Option> getAdvStatVarRBox3() {
        return advStatVarRBox3;
    }

    public void setAdvStatVarRBox3(Collection<Option> dol) {
        this.advStatVarRBox3 = dol;
    }

    // boxR3: listbox@selected
    private String[] advStatSelectedVarRBox3;

    public String[] getAdvStatSelectedVarRBox3() {
        return advStatSelectedVarRBox3;
    }

    public void setAdvStatSelectedVarRBox3(String[] dol) {
        this.advStatSelectedVarRBox3 = dol;
    }


    // moveVar1Bttn
    // > button (add)
    // @binding
    private HtmlCommandButton button4 = new HtmlCommandButton();

    public HtmlCommandButton getButton4() {
        return button4;
    }
    public void setButton4(HtmlCommandButton hcb) {
        this.button4 = hcb;
    }
    // < button (remove)
    // @binding
    private HtmlCommandButton button4b = new HtmlCommandButton();

    public HtmlCommandButton getButton4b() {
        return button4b;
    }

    public void setButton4b(HtmlCommandButton hcb) {
        this.button4 = hcb;
    }

    // moveVar2Bttn
    // > button (add)
    // @binding
    private HtmlCommandButton button5 = new HtmlCommandButton();

    public HtmlCommandButton getButton5() {
        return button5;
    }
    
    public void setButton5(HtmlCommandButton hcb) {
        this.button5 = hcb;
    }
    
    // < button (remove)
    // @binding
    private HtmlCommandButton button5b = new HtmlCommandButton();

    public HtmlCommandButton getButton5b() {
        return button5b;
    }
    
    public void setButton5b(HtmlCommandButton hcb) {
        this.button5b = hcb;
    }

    // moveVar3Bttn
    // > button (add)
    // @binding
    private HtmlCommandButton button6 = new HtmlCommandButton();

    public HtmlCommandButton getButton6() {
        return button6;
    }

    public void setButton6(HtmlCommandButton hcb) {
        this.button6 = hcb;
    }

    // < button (remove)
    // @binding
    private HtmlCommandButton button6b = new HtmlCommandButton();

    public HtmlCommandButton getButton6b() {
        return button6b;
    }

    public void setButton6b(HtmlCommandButton hcb) {
        this.button6b = hcb;
    }
    
    
    // get variable type (int) from a given row of the dataTable
    public int getVariableType (DataVariable dv){
      Integer varType;
      
      if (dv.getVariableFormatType().getName().equals("numeric")){
          if (dv.getVariableIntervalType().getName().equals("continuous") ){
             varType = Integer.valueOf("2");
          } else {
             varType = Integer.valueOf("1");
          }
      } else {
         varType = Integer.valueOf("0");
      }
      return varType.intValue();
    }
    
        
    // character var or not    
    public boolean isCharacterVariable(String newVarId){
      boolean chr = false;
      Pattern p = Pattern.compile("[a-zA-Z_]+");
      int counter=0;
      // get the recode table by varId
      List<Object> rvtbl =  (List<Object>)recodeSchema.get(newVarId);
      for (int i=0;i<rvtbl.size();i++){
        List<Object> rvs = (List<Object>)rvtbl.get(i); 
        // check character or not
        String value = (String)rvs.get(1);
        Matcher m = p.matcher(value);
        if (m.find()){
          counter++;
        }
      }
      if (counter>0){
        chr = true;
      }
      return chr;
    }
    // get the number of valid categories (rows) of a recode Table
    public int getValidCategories(String newVarId){
      int noCat=0;
      // get the recode table by varId
      List<Object> rvtbl =  (List<Object>)recodeSchema.get(newVarId);
      for (int i=0;i<rvtbl.size();i++){
        List<Object> rvs = (List<Object>)rvtbl.get(i); 
        if(!((Boolean)rvs.get(0))){
          // false [== not exclude this category]
          noCat++;
        }
      }
      return noCat;
    }
    
    
    public int getSumStatSize(String varId){
          int sumStatSize =  getVariableById(varId).getSummaryStatistics().size();
          out.println("sumStat size="+sumStatSize);
          return sumStatSize;
    }
    public int getCatStatSize(String varId){
          Collection<VariableCategory> catStatSet= getVariableById(varId).getCategories();
          // count non-missing category only
          int catStatSize = 0;
          for (Iterator elc =catStatSet.iterator(); elc.hasNext();) {
              VariableCategory dvcat = (VariableCategory) elc.next();
              if (!dvcat.isMissing()){
                  catStatSize++;
              }
          }
          out.println("valid categories="+catStatSize);
          return catStatSize;
    }
    
    // check whether the requested move is permissible
    private Boolean checkVarType(String varId, String boxVarType, Boolean strict){
        out.println("variable id="+varId);
    int varType = 1;
if (isRecodedVar(varId)){
    // recoded var case
    if (isCharacterVariable(varId)){
      // 
      varType=0;
    }
} else{
   // existing var case
      //DataVariable dv = getVariableById(varId);
      varType = getVariableType(getVariableById(varId));
}


      out.println("Type of the variable to be moved="+varType);
      Integer boxType = dataType2Int.get(boxVarType);

      Boolean result=false;
      switch(boxType != null ? boxType : -1){
        case 5:
          // continuous
          if (strict) {
            // stricter test
            if (varType ==2) {
              result = true;
            } else {
              result = false;
            }
          } else {
            if (varType !=0) {
              result = true;
            } else {
              result = false;
            }
          }

          break;
        case 4:
          // discrete
          // this categorization is zelig-context
          if (strict) {
            // stricter test
            if ((varType !=0) && (varType !=2)) {
              result = true;
            } else {
              result = false;
            }
          } else {
            if (varType !=0) {
              result = true;
            } else {
              result = false;
            }
          }


          break;
        case 3:
          // ordinal
          if (strict) {
            // stricter test
            if ((varType !=0) && (varType !=2)) {
              result = true;
            } else {
              result = false;
            }
          } else {
            if (varType !=0) {
              result = true;
            } else {
              result = false;
            }
          }
          break;
          
        case 2:
          // nominal
          // no stricter case
          if (varType == 0) {
            result = true;
          } else {
            result = false;
          }
          break;
          
        case 23:
        case 32:
          // nominal|ordinal
          // no stricter case
          if (varType !=2) {
            result = true;
          } else {
            result = false;
          }
          break;

        case 1:
          // binary
          int sumStatSize =0;
          int catStatSize =0;
          if (isRecodedVar(varId)){
              catStatSize = getValidCategories(varId);
          } else {
              sumStatSize = getSumStatSize(varId);
              catStatSize = getCatStatSize(varId);
          }
          if ((varType ==2) || 
              (sumStatSize > 2) || 
              (catStatSize < 2) ){
            // continuous var or more-than-ten-categories (=> not binary)
            result = false;
          } else {
            // net test
            if (catStatSize == 2) {
              result = true;
            } else {
              result = false;
            }
          }
          
          break;
        case 0:
          // any
          // do nothing
          result = true;
          
          break;
        default:
          result = true;
          break;
      }
      
      if (result){
        out.println("The move passed the test");
      } else {
        out.println("The move failed the test");
        out.println("expected type="+boxVarType);
        out.println("variable's type="+vtInt2String.get(Integer.toString(varType)));
      }
      return result;
    }

    // remove the selected option from a given listbox@items
    private Option removeOption (String varId, Collection<Option> co){
      Option matchedOptn = null;
      Iterator  iter = co.iterator();
      while (iter.hasNext()){
        Option optn  = (Option) iter.next();
        if (varId.equals(optn.getValue())){
          matchedOptn =optn;
          co.remove(optn);
          break;
        }
      }
      return matchedOptn;
    }
    
    // reset all error message text-fields related to moveVar buttons
    public void resetMsg4MoveVar(){
      msgMoveVar1Bttn.setRendered(false);
      msgMoveVar1Bttn.setText(" ");
      msgMoveVar2Bttn.setRendered(false);
      msgMoveVar2Bttn.setText(" ");
      msgMoveVar3Bttn.setRendered(false);
      msgMoveVar3Bttn.setText(" ");
    }
    
    private StaticText msgMoveVar1Bttn = new StaticText();

    public StaticText getMsgMoveVar1Bttn() {
        return msgMoveVar1Bttn;
    }

    public void setMsgMoveVar1Bttn(StaticText txt) {
        this.msgMoveVar1Bttn = txt;
    }
    
    
    // move from L to R1
    public void addVarBoxR1(ActionEvent acev){
      String[] OptnSet = getAdvStatSelectedVarLBox();
      
      out.println("within addVarBoxR1: model name="+getCurrentModelName());
      
      int BoxR1max = getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName()).getVarBox().get(0).getMaxvar();
      
      out.println("BoxR1max="+BoxR1max );
      
      //out.println("BoxR1min="+getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName()).getVarBox().get(0).getMinvar() );
      
      out.println("current listboxR1 size="+advStatVarRBox1.size());
      
      String varType = getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName()).getVarBox().get(0).getVarType();
      
      out.println("permissible variable type for advStatVarRBox1="+varType);
      
      
      // for each selected item
      if (advStatVarRBox1.size() < BoxR1max){
        for (int i=0; i<OptnSet.length; i++) {
          // reset error message field
          resetMsg4MoveVar();
          // type check
          out.println("OptnSet["+i+"]="+OptnSet[i]);
          if (checkVarType(OptnSet[i], varType, true)){
            getAdvStatVarRBox1().add(removeOption(OptnSet[i], getVarSetAdvStat()));
            
            out.println("current listboxR1 size(within loop)="+advStatVarRBox1.size());
            if (advStatVarRBox1.size() == BoxR1max) {
              return;
            }
          } else {
            // show error message
            msgMoveVar1Bttn.setText("* Incompatible type:<br />required="+varType+"<br />found="+vtInt2String.get(Integer.toString(getVariableType(getVariableById(OptnSet[i])))));
            msgMoveVar1Bttn.setRendered(true);
          }
        }
      }
    }
    
    
    private StaticText msgMoveVar2Bttn = new StaticText();

    public StaticText getMsgMoveVar2Bttn() {
        return msgMoveVar2Bttn;
    }

    public void setMsgMoveVar2Bttn(StaticText txt) {
        this.msgMoveVar2Bttn = txt;
    }

    
    // move from L to R2
    public void addVarBoxR2(ActionEvent acev){
      // set left-selected items to a temp list
      String[] OptnSet = getAdvStatSelectedVarLBox();
      
      out.println("within addVarBoxR2: model name="+getCurrentModelName());
      
      int BoxR2max = getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName()).getVarBox().get(1).getMaxvar();

      out.println("BoxR2max="+BoxR2max );

      //out.println("BoxR2min="+getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName()).getVarBox().get(1).getMinvar() );

      out.println("current listbox size="+advStatVarRBox2.size());
      
      String varType = getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName()).getVarBox().get(1).getVarType();
      
      out.println("permissible variable type for advStatVarRBox2="+varType);


      // for each selected item
      if (advStatVarRBox2.size() < BoxR2max){

        for (int i=0; i<OptnSet.length; i++) {
          // reset error message field
          resetMsg4MoveVar();
          // type check
          out.println("OptnSet["+i+"]="+OptnSet[i]);
          if (checkVarType(OptnSet[i], varType, true)){
            getAdvStatVarRBox2().add(removeOption(OptnSet[i], getVarSetAdvStat()));
            out.println("current listboxR2 size(within loop)="+advStatVarRBox2.size());
            if (advStatVarRBox2.size() == BoxR2max) {
              return;
            }
          } else {
            // show error message
            msgMoveVar2Bttn.setText("* Incompatible type:<br />required="+varType+"<br />found="+vtInt2String.get(Integer.toString(getVariableType(getVariableById(OptnSet[i])))));
            msgMoveVar2Bttn.setRendered(true);
          }
        }
      }
    }
    
    private StaticText msgMoveVar3Bttn = new StaticText();

    public StaticText getMsgMoveVar3Bttn() {
        return msgMoveVar3Bttn;
    }

    public void setMsgMoveVar3Bttn(StaticText txt) {
        this.msgMoveVar3Bttn = txt;
    }

    
    // move from L to R3
    public void addVarBoxR3(ActionEvent acev){
      // set left-selected items to a temp list
      String[] OptnSet = getAdvStatSelectedVarLBox();
      
      out.println("within addVarBoxR3: model name="+getCurrentModelName());
      
      int BoxR3max = getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName()).getVarBox().get(2).getMaxvar();
      
      out.println("BoxR3max="+BoxR3max );

      //out.println("BoxR2min="+getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName()).getVarBox().get(2).getMinvar() );

      out.println("current listboxR3 size="+advStatVarRBox3.size());
      
      String varType = getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName()).getVarBox().get(2).getVarType();
      
      out.println("permissible variable type for advStatVarRBox3="+varType);

      
      // for each selected item
      if (advStatVarRBox3.size() < BoxR3max){
        for (int i=0; i<OptnSet.length; i++) {
          // reset error message field
          resetMsg4MoveVar();
          // type check
          out.println("OptnSet["+i+"]="+OptnSet[i]);
          if (checkVarType(OptnSet[i], varType, true)){

            getAdvStatVarRBox3().add(removeOption(OptnSet[i], getVarSetAdvStat()));

            out.println("current listboxR3 size(within loop)="+advStatVarRBox3.size());
            if (advStatVarRBox3.size() == BoxR3max) {
              return;
            }
          } else {
            // show error message
            msgMoveVar3Bttn.setText("* Incompatible type:<br />required="+varType+"<br />found="+vtInt2String.get(Integer.toString(getVariableType(getVariableById(OptnSet[i])))));
            msgMoveVar3Bttn.setRendered(true);
          }
        }
      }
    }
    
    // move from R1 to L
    public void removeVarBoxR1(ActionEvent acev){
      // set left-selected items to a temp list
      String[] OptnSet = getAdvStatSelectedVarRBox1();
      // for each selected item
      for (int i=0; i<OptnSet.length; i++) {
        getVarSetAdvStat().add(removeOption(OptnSet[i], getAdvStatVarRBox1()) );
      }
    }
    // move from R2 to L
    public void removeVarBoxR2(ActionEvent acev){
      // set left-selected items to a temp array, Option []
      String[] OptnSet = getAdvStatSelectedVarRBox2();
      // for each selected item
      for (int i=0; i<OptnSet.length; i++) {
        getVarSetAdvStat().add(removeOption(OptnSet[i], getAdvStatVarRBox2()) );
      }
    }

    // move from R3 to L
    public void removeVarBoxR3(ActionEvent acev){
      // set left-selected items to a temp array, Option []
      String[] OptnSet = getAdvStatSelectedVarRBox3();
      // for each selected item
      for (int i=0; i<OptnSet.length; i++) {
        getVarSetAdvStat().add(removeOption(OptnSet[i], getAdvStatVarRBox3()) );
      }
    }



//////////////////////
// Output option 
//////////////////////



// output option panel

/*
    private PanelGroup groupPanel25 = new PanelGroup();

    public PanelGroup getGroupPanel25() {
       return groupPanel25;
    }

    public void setGroupPanel25(PanelGroup pg) {
        this.groupPanel25 = pg;
    }
*/


// output option checkbox group: zelig
     private CheckboxGroup checkboxGroup2 = new CheckboxGroup();
     public CheckboxGroup getCheckboxGroup2() {
         return checkboxGroup2;
     }
     public void setCheckboxGroup2(CheckboxGroup cg) {
         this.checkboxGroup2 = cg;
     }

     private MultipleSelectOptionsList checkboxGroup2DefaultOptions = new MultipleSelectOptionsList();
     public MultipleSelectOptionsList getCheckboxGroup2DefaultOptions() {
         return checkboxGroup2DefaultOptions;
     }
     public void setCheckboxGroup2DefaultOptions(MultipleSelectOptionsList msol) {
         this.checkboxGroup2DefaultOptions = msol;
     }

// output option checkbox group: xtab
     private CheckboxGroup checkboxGroupXtb = new CheckboxGroup();
     public CheckboxGroup getCheckboxGroupXtb() {
         return checkboxGroupXtb;
     }
     public void setCheckboxGroupXtb(CheckboxGroup cg) {
         this.checkboxGroupXtb = cg;
     }

     private MultipleSelectOptionsList checkboxGroupXtbOptions = new MultipleSelectOptionsList();
     public MultipleSelectOptionsList getCheckboxGroupXtbOptions() {
         return checkboxGroupXtbOptions;
     }
     public void setCheckboxGroupXtbOptions(MultipleSelectOptionsList msol) {
         this.checkboxGroupXtbOptions = msol;
     }
     public void checkboxGroupXtbProcessValueChange(ValueChangeEvent vce){
        FacesContext cntxt = FacesContext.getCurrentInstance();
       // String outOptn = (String)vce.getNewValue();
        //out.println("outOptn="+outOptn);
        /*
        for (int i=0;i<outOptn.length;i++){
          out.println("output option["+i+"]="+outOptn[i].getValue());
        }
        */
        out.println("checkbox: new value="+ vce.getNewValue());
        Option[] outOption = (Option[])checkboxGroupXtbOptions.getOptions();
        for (int i=0;i<outOption.length;i++){
          out.println("output option["+i+"]="+outOption[i].getValue());
        }
        //cntxt.getExternalContext().getSessionMap().put("checkboxGroupXtbOptions",checkboxGroupXtbOptions);
     }

//////////////////////
// Analysis option 
//////////////////////

    // Analysis option block: casing panel

    private PanelGroup analysisOptionPanel = new PanelGroup();

    public PanelGroup getAnalysisOptionPanel() {
      return analysisOptionPanel;
    }
    public void setAnalysisOptionPanel(PanelGroup pg) {
      this.analysisOptionPanel = pg;
    }
    // setx-option panel
    private PanelGroup setxOptionPanel = new PanelGroup();

    public PanelGroup getSetxOptionPanel() {
      return setxOptionPanel;
    }
    public void setSetxOptionPanel(PanelGroup pg) {
      this.setxOptionPanel = pg;
    }

/* analysis option block: label 

    private HtmlOutputText outputText46 = new HtmlOutputText();

    public HtmlOutputText getOutputText46() {
        return outputText46;
    }

    public void setOutputText46(HtmlOutputText hot) {
        this.outputText46 = hot;
    }
*/


// simulation: casing panel 
     private PanelGroup groupPanel20 = new PanelGroup();
     public PanelGroup getGroupPanel20() {
         return groupPanel20;
     }
     public void setGroupPanel20(PanelGroup pg) {
         this.groupPanel20 = pg;
     }


// simulation option: checkbox

     private Checkbox checkbox3 = new Checkbox();

     public Checkbox getCheckbox3() {
         return checkbox3;
     }
     public void setCheckbox3(Checkbox c) {
         this.checkbox3 = c;
     }

    public void showHideSimulationsOptPanel(ValueChangeEvent vce){
        FacesContext context = FacesContext.getCurrentInstance();
        
        Boolean currentState= (Boolean) vce.getNewValue();
        if ((currentState.toString()).equals("true")) {
          groupPanel20.setRendered(true);
        } else if ((currentState.toString()).equals("false")){
          groupPanel20.setRendered(false);
        }

        FacesContext.getCurrentInstance().renderResponse();

    }
// simulation option: label for radio button group
/*
    private Label label4 = new Label();

    public Label getLabel4() {
        return label4;
    }

    public void setLabel4(Label l) {
        this.label4 = l;
    }
*/

// simulation option: radio button group
     // @binding
     private RadioButtonGroup radioButtonGroup1 = new RadioButtonGroup();
     
     public RadioButtonGroup getRadioButtonGroup1() {
         return radioButtonGroup1;
     }
     public void setRadioButtonGroup1(RadioButtonGroup rbg) {
         this.radioButtonGroup1 = rbg;
     }
     // @items
     private SingleSelectOptionsList radioButtonGroup1DefaultOptions = new SingleSelectOptionsList();
     
     public SingleSelectOptionsList getRadioButtonGroup1DefaultOptions() {
         return radioButtonGroup1DefaultOptions;
     }
     public void setRadioButtonGroup1DefaultOptions(SingleSelectOptionsList ssol) {
         this.radioButtonGroup1DefaultOptions = ssol;
     }
     
     // @selected
     private Object lastSimCndtnSelected="0";
     
    // @valueChangeListener
    public void showHideSimCndtnOptPanel(ValueChangeEvent vce){
        FacesContext cntxt = FacesContext.getCurrentInstance();
        
        String currentState = (String) vce.getNewValue();
        out.println("currentState="+currentState);
        out.println("current model name in setx="+getCurrentModelName());
        
        out.print("within simulation-type choice: new selected"+radioButtonGroup1.getSelected());
        if (getCurrentModelName()!=null){

            AdvancedStatGUIdata.Model selectedModelSpec = getAnalysisApplicationBean().getSpecMap().get(getCurrentModelName());
            out.println("spec within setx:\n"+selectedModelSpec);
            if ((currentState.toString()).equals("1")) {
              groupPanel22.setRendered(true);


              // set up the @items for dropDown2/dropDown3
              
              if (selectedModelSpec.getNoRboxes() == 2) {
                setSetxDiffVarBox1(getAdvStatVarRBox2());
                setSetxDiffVarBox2(getAdvStatVarRBox2());

              } else if (selectedModelSpec.getNoRboxes()== 3){
                setSetxDiffVarBox1(getAdvStatVarRBox3());
                setSetxDiffVarBox2(getAdvStatVarRBox3());
                
              } 

                cntxt.getExternalContext().getSessionMap().put("setxDiffVarBox1",setxDiffVarBox1);
                cntxt.getExternalContext().getSessionMap().put("setxDiffVarBox2",setxDiffVarBox2);


            } else if ((currentState.toString()).equals("0")){
              groupPanel22.setRendered(false);
            }
        } else {
            groupPanel22.setRendered(false);
        }
        /*
        if (radioButtonGroup1DefaultOptions.getSelectedValue().eqauls(lastSimCndtnSelected)){
          groupPanel22.setRendered(false);
        } else {
          groupPanel22.setRendered(true);
        }
        */
        cntxt.renderResponse();
    }


// simulation: option panel for the radio-button-option of select values
      //@binding
     private PanelGroup groupPanel22 = new PanelGroup();
     public PanelGroup getGroupPanel22() {
         return groupPanel22;
     }
     public void setGroupPanel22(PanelGroup pg) {
         this.groupPanel22 = pg;
     }


// simulation : value for 1st diff: casing pane

     private HtmlPanelGrid gridPanel10 = new HtmlPanelGrid();
     public HtmlPanelGrid getGridPanel10() {
         return gridPanel10;
     }
     public void setGridPanel10(HtmlPanelGrid hpg) {
         this.gridPanel10 = hpg;
     }

// simulation : value for 1st diff: label for the casing panel

     private Label label2 = new Label();
     public Label getLabel2() {
         return label2;
     }
     public void setLabel2(Label l) {
         this.label2 = l;
     }


// simulation : value for 1st diff: pull-down var selection
    // dropdown2: ui:dropdown@binding
    private DropDown dropDown2 = new DropDown();

    public DropDown getDropDown2() {
        return dropDown2;
    }

    public void setDropDown2(DropDown dd) {
        this.dropDown2 = dd;
    }
    // dropdown2: ui:dropdown@items
    private Collection<Option> setxDiffVarBox1 = new ArrayList<Option>();

    public Collection<Option> getSetxDiffVarBox1() {
        return setxDiffVarBox1;
    }

    public void setSetxDiffVarBox1(Collection<Option> dol) {
        this.setxDiffVarBox1 = dol;
    }

// simulation : value for 1st diff: = 
/*
     private StaticText staticText2 = new StaticText();
     public StaticText getStaticText2() {
         return staticText2;
     }
     public void setStaticText2(StaticText st) {
         this.staticText2 = st;
     }
*/

// simulation : value for 1st diff: text box

     private TextField textField8 = new TextField();
     public TextField getTextField8() {
         return textField8;
     }
     public void setTextField8(TextField tf) {
         this.textField8 = tf;
     }



// simulation : explanatory variable value: casing panel

     private HtmlPanelGrid gridPanel11 = new HtmlPanelGrid();
     public HtmlPanelGrid getGridPanel11() {
         return gridPanel11;
     }
     public void setGridPanel11(HtmlPanelGrid hpg) {
         this.gridPanel11 = hpg;
     }


// simulation : explanatory variable value: label for casing panel
     private Label label1 = new Label();
     public Label getLabel1() {
         return label1;
     }
     public void setLabel1(Label l) {
         this.label1 = l;
     }


// simulation : explanatory variable value: pull-down var selection
    // dropdown3: ui:dropDown@binding
    private DropDown dropDown3 = new DropDown();

    public DropDown getDropDown3() {
        return dropDown3;
    }

    public void setDropDown3(DropDown dd) {
        this.dropDown3 = dd;
    }

    // dropdown3: ui:dropdown@items
    private Collection<Option> setxDiffVarBox2 = new ArrayList<Option>();

    public Collection<Option> getSetxDiffVarBox2() {
        return setxDiffVarBox2;
    }

    public void setSetxDiffVarBox2(Collection<Option> dol) {
        this.setxDiffVarBox2 = dol;
    }




// simulation : explanatory variable value: = 
/*
     private StaticText staticText1 = new StaticText();
     public StaticText getStaticText1() {
         return staticText1;
     }
     public void setStaticText1(StaticText st) {
         this.staticText1 = st;
     }
*/


// simulation : explanatory variable value: text box
     private TextField textField10 = new TextField();
     public TextField getTextField10() {
         return textField10;
     }
     public void setTextField10(TextField tf) {
         this.textField10 = tf;
     }



/*

// Sensitivity analysis: checkbox
     private Checkbox sensitivity = new Checkbox();
     public Checkbox getSensitivity() {
         return sensitivity;
     }
     public void setSensitivity(Checkbox c) {
         this.sensitivity = c;
     }


// panel for Sensitivity analysis option
     private PanelGroup groupPanel23 = new PanelGroup();
     
     public PanelGroup getGroupPanel23() {
         return groupPanel23;
     }
     public void setGroupPanel23(PanelGroup pg) {
         this.groupPanel23 = pg;
     }

    public void showHideSensitivityOptPanel(ValueChangeEvent vce){
        FacesContext context = FacesContext.getCurrentInstance();
        Boolean currentState= (Boolean) vce.getNewValue();
        if ((currentState.toString()).equals("true")) {
          groupPanel23.setVisible(true);
        } else if ((currentState.toString()).equals("false")){
          groupPanel23.setVisible(false);
        }
        // 
        FacesContext.getCurrentInstance().renderResponse();

    }
*/

// Missing-Value handling: checkbox
    // advStatNaMethod: ui:checkbox
    // @binding
    private Checkbox advStatNaMethod = new Checkbox();

    public Checkbox getAdvStatNaMethod() {
       return advStatNaMethod;
    }

    public void setAdvStatNaMethod(Checkbox c) {
       this.advStatNaMethod = c;
    }
    /*
    private OptionsList checkboxAdvStatNaMethodOption = new OptionsList();
    public OptionsList getCheckboxAdvStatNaMethodOption (){
      return checkboxAdvStatNaMethodOption;
    }
    pbulic void setCheckboxAdvStatNaMethodOption (OptionsList sol){
      this.checkboxAdvStatNaMethodOption=sol;
    }
    */
// submit button (modeling request)
    // advStatBttn:h:commandButton@binding
    private HtmlCommandButton advStatButton = new HtmlCommandButton();

    public HtmlCommandButton getAdvStatButton() {
      return advStatButton;
    }
    
    public void setAdvStatButton(HtmlCommandButton hcb) {
      this.advStatButton = hcb;
    }
    
    private List<Integer> getCurrentVarBoxSize (String mdlName){
        List <Integer> bs = new ArrayList();
        int noBoxR = getAnalysisApplicationBean().getSpecMap().get(mdlName).getNoRboxes();
        if (noBoxR == 1){
          bs.add(advStatVarRBox1.size());
        } else if (noBoxR == 2){
          bs.add(advStatVarRBox1.size());
          bs.add(advStatVarRBox2.size());
        } else if (noBoxR == 3){
          bs.add(advStatVarRBox1.size());
          bs.add(advStatVarRBox2.size());
          bs.add(advStatVarRBox3.size());
        }
        return bs;
    }

    
    
    // checking advStat-related parameters before submission
    public boolean checkAdvStatParameters (String mdlName){
      //boolean result=false;
      
      
      
      Integer noBoxR = getAnalysisApplicationBean().getSpecMap().get(mdlName).getNoRboxes();
      List<Integer> RBoxSizes = getCurrentVarBoxSize(mdlName);
      out.println("RBoxSizes="+RBoxSizes);
      int noe = 0;
      resetMsg4MoveVar();      
      for (int i=0; i<noBoxR; i++) {

        Integer ithBoxMin = getAnalysisApplicationBean().getSpecMap().get(mdlName).getVarBox().get(i).getMinvar();
        out.println("ithBoxMin("+i+")="+ithBoxMin);
        if (RBoxSizes.get(i) < ithBoxMin) {
            String ermsg = "No of Vars in Box"+i+" ("+RBoxSizes.get(i)+") is less than the minimum("+ithBoxMin+")";

            out.println("* "+ ermsg);

            noe++;
            if (i == 0){
              msgMoveVar1Bttn.setText("*"+ ermsg);
              msgMoveVar1Bttn.setRendered(true);
            } else if (i == 1){
              msgMoveVar2Bttn.setText("*"+ ermsg);
              msgMoveVar2Bttn.setRendered(true);
            } else if (i == 2){
              msgMoveVar3Bttn.setText("*"+ ermsg);
              msgMoveVar3Bttn.setRendered(true);
            }
        }

      }
      out.println("noe="+noe);
      return (noe == 0 ?  true : false) ;
    }

    // advStatBttn:h:commandButton@action
    public String advStatAction() {
    
        // check the current model

        String mdlName = (String)dropDown1.getSelected();
        out.println("model name="+mdlName);

        if (checkAdvStatParameters(mdlName)){
        
            FacesContext cntxt = FacesContext.getCurrentInstance();

            HttpServletResponse res = (HttpServletResponse) cntxt.getExternalContext().getResponse();
            HttpServletRequest  req = (HttpServletRequest) cntxt.getExternalContext().getRequest();
            try {
              out.println("**************** within advStatAction() ****************");
                // common parts
                // data file
                StudyFile sf = dataTable.getStudyFile();
                // server prefix
                
              
                String dsbUrl = System.getProperty("vdc.dsb.url");
                out.println("dsbUrl="+dsbUrl);


                
                //String serverPrefix = "http://vdc-build.hmdc.harvard.edu:8080/dvn";
                String serverPrefix = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath(); 
                // String serverPrefix = req.getScheme() +"://" + dsbUrl + ":" + req.getServerPort() + req.getContextPath(); 

                /*
                  "optnlst_a" => "A01|A02|A03",
                  "analysis" => "A01 A02",
                  "varbl" => "v1.3 v1.10 v1.13 v1.22 v1.40",
                  "charVarNoSet" => "v1.10|v1.719",
                */
                // common parameters
                
                Map <String, List<String>> mpl = new HashMap<String, List<String>>();
                Map <String, String> mps= new HashMap<String, String>();
                mps.put("optnlst_a", "A01|A02|A03");
                //Object[] vs = edaOptionSet.getSelectedValues();
                List<String> alst = new ArrayList<String>();
                List<String> aoplst = new ArrayList<String>();
                aoplst.add("A01|A02|A03");
                mpl.put("optnlst_a", aoplst);
                // outoput options
                
                
                List<String> outOptionList = new ArrayList<String>();
                
                if (mdlName.equals("xtb")){
                    alst.add("A03");
                    // output options
                    Object[] outOptn = (Object[])checkboxGroupXtbOptions.getSelectedValue();
                    List<String> tv = new ArrayList<String>();
                    tv.add("T");
                    for (int j=0;j<outOptn.length;j++){
                      out.println("output option["+j+"]="+outOptn[j]);

                      mpl.put((String)outOptn[j], new ArrayList(tv));
                    }
                    // variables: 1st RBox
                    if (advStatVarRBox1.size()>=1){
                      out.println("RB1:"+getDataVariableForRBox1());
                      mpl.put("xtb_nmBxR1", getDataVariableForRBox1());
                    }
                    // variables: 2nd RBox
                    if (advStatVarRBox2.size()>=1){
                      out.println("RB2:"+getDataVariableForRBox2());
                      mpl.put("xtb_nmBxR2", getDataVariableForRBox2());
                    }
                    
                  mpl.put("analysis", alst);

                } else {
                    out.println("+++++++++++++++++++++++ zelig param block +++++++++++++++++++++++");
                    // non-xtb, i.e., zelig cases
                    // check zlg value
                    //String mdlZname= mdlName+;
                    out.println("model spec dump="+getAnalysisApplicationBean().getSpecMap().get(mdlName));
                    out.println("model spec mdlId="+getAnalysisApplicationBean().getSpecMap().get(mdlName).getMdlId());
                    String zligPrefix = getAnalysisApplicationBean().getSpecMap().get(mdlName).getMdlId();
                    out.println("model no="+ zligPrefix);
                    // 1-RBox case
                    if (advStatVarRBox1.size()>=1){
                      out.println("RB1:"+getDataVariableForRBox1());
                      mpl.put(zligPrefix+"_nmBxR1", getDataVariableForRBox1());
                    }
                    // 2-RBox case
                    if (advStatVarRBox2.size()>=1){
                      out.println("RB2:"+getDataVariableForRBox2());
                      mpl.put(zligPrefix+"_nmBxR2", getDataVariableForRBox2());
                    }
                    // 3-RBox case
                    if (advStatVarRBox3.size()>=1){
                      out.println("RB3:"+getDataVariableForRBox3());
                      mpl.put(zligPrefix+"_nmBxR3", getDataVariableForRBox3());
                    }
                    // model name
                    
                    mpl.put("zlg", getZlg(zligPrefix, mdlName));
                    // model type
                    String sfn = getAnalysisApplicationBean().getSpecMap().get(mdlName).getSpecialFn();
                    mpl.put("mdlType_"+mdlName, getMdlType(mdlName, sfn) );
                    
                    // model title
                    String ttl = getAnalysisApplicationBean().getSpecMap().get(mdlName).getTitle();
                    out.println("model title="+ttl);
                    mpl.put("mdlTitle_"+mdlName, Arrays.asList(ttl));
                    
                    // nrBoxes
                    int noRboxes = getAnalysisApplicationBean().getSpecMap().get(mdlName).getNoRboxes();
                    out.println("noRboxes="+noRboxes);
                    
                    mpl.put("noBoxes_"+mdlName, Arrays.asList(Integer.toString(noRboxes)));
                    
                    // binary
                    String mdlCategory = getAnalysisApplicationBean().getSpecMap().get(mdlName).getCategory();
                    out.println("model category="+mdlCategory);
                    if (mdlCategory.equals("Models for Dichotomous Dependent Variables")){
                      mpl.put("mdlDepVarType_"+mdlName, Arrays.asList("binary"));
                    }
                    //  output options
                    /*
                    zlg_017_Summary
                    zlg_017_Plots
                    zlg_017_BinOutput
                    */
                    Object[] outOptn = (Object[])checkboxGroup2DefaultOptions.getSelectedValue();
                    for (int j=0;j<outOptn.length;j++){
                      String outputOptnkey = zligPrefix +"_" + (String)outOptn[j];
                      out.println("zelig: output option["+j+"]="+outputOptnkey);
                      mpl.put(outputOptnkey, Arrays.asList("T"));
                    }
                    
                    // analysis options
                    /*
                    zlg_017_Sim
                    zlg_017_setx
                    zlg_017_setx_var
                    zlg_017_setx_val_1
                    zlg_017_setx_val_2
                    
                    zlg_017_naMethod
                    */
                    //
                    if (checkbox3.isChecked()){
                        mpl.put(zligPrefix+"_Sim",  Arrays.asList("T"));
                        Object simOptn = radioButtonGroup1DefaultOptions.getSelectedValue();
                        mpl.put(zligPrefix+"_setx",  Arrays.asList((String)simOptn ));
                        if ( ((String)simOptn).equals("1")){
                          Object v1 = dropDown2.getSelected();
                          Object v2 = dropDown3.getSelected();
                          Object vl1 = textField8.getValue();
                          Object vl2 = textField10.getValue();
                          List<String> setxVars = new ArrayList<String>();
                          if (v1 !=null){
                            setxVars.add((String)v1);
                            
                            
                          }
                          if (v2 !=null){
                            setxVars.add((String)v2);
                          }
                          mpl.put(zligPrefix+"_setx_var", setxVars);
                          if (vl1 !=null){
                            mpl.put(zligPrefix+"_setx_val_1", Arrays.asList( (String)vl1 ));
                          }
                          if (vl2 !=null){
                            mpl.put(zligPrefix+"_setx_val_2", Arrays.asList( (String)vl2 ));
                          }
                          
                        }
                    }
                    
                    
                    //if (advStatNaMethod.getSelected("naMethod").size() >0){
                      //if (advStatNaMethod.getSelected("naMethod").get(0).equals("none")){
                      if (advStatNaMethod.isChecked()){
                        mpl.put(zligPrefix+"_naMethod",  Arrays.asList("none"));
                      }
                    //}
                    
                }
                out.println("contents(mpl):"+mpl);
                
                
                // if there is a user-defined (recoded) variables
                if (recodedVarSet.size()>0){
                  mpl.putAll(getRecodedVarParameters());
                }

                out.println("citation info to be sent:\n"+citation);
                mpl.put("OfflineCitation", Arrays.asList(citation));
                
                mpl.put("appSERVER",Arrays.asList(req.getServerName() + ":" + req.getServerPort() + req.getContextPath()));
                //mpl.put("appSERVER",Arrays.asList(dsbUrl + ":" + req.getServerPort() + req.getContextPath()));
                mpl.put("studytitle",Arrays.asList(studyTitle));
                mpl.put("studyno", Arrays.asList(studyId.toString()));
                mpl.put("studyURL", Arrays.asList(studyURL));

                // Disseminate Request
                new DSBWrapper().disseminate(res, mpl, sf, serverPrefix, getDataVariableForRequest());
                

            } catch (IOException ex) {
                out.println("disseminate:download filed due to io exception");
                ex.printStackTrace();
            }

            cntxt.responseComplete();
            
            return "success";
        } else {
            return "failure";
        }
    }

    public List<String> getDataVariableForRBox1(){
      List<String> dvs = new ArrayList<String>();
        for (Iterator el = advStatVarRBox1.iterator(); el.hasNext();) {
          Option dv =(Option) el.next();
          String id = (String)dv.getValue();
            dvs.add("v"+id);
        }
        return dvs;
    }
    public List<String> getDataVariableForRBox2(){
      List<String> dvs = new ArrayList<String>();
        for (Iterator el = advStatVarRBox2.iterator(); el.hasNext();) {
          Option dv =(Option) el.next();
          String id = (String)dv.getValue();
            dvs.add("v"+id);
        }
        return dvs;
    }
    public List<String> getDataVariableForRBox3(){
      List<String> dvs = new ArrayList<String>();
        for (Iterator el = advStatVarRBox3.iterator(); el.hasNext();) {
          Option dv =(Option) el.next();
          String id = (String)dv.getValue();
            dvs.add("v"+id);
        }
        return dvs;
    }
    public List<String> getZlg(String mdlId, String mdlName){
      List<String> ls = new ArrayList<String>();
      ls.add(mdlId+"-"+mdlName);
      return ls;
    }
    
    public List<String> getMdlType(String mdlName, String sf){
      List<String> ls = new ArrayList<String>();
      out.println("model name="+mdlName+"  special function="+sf);
      int typeValue=0;
      
      if (mdlName.equals("blogit") || mdlName.equals("bprobit")) {
        typeValue=1;
      } else{
        if (sf !=null){
          if (sf.equals("Surv")) {
            typeValue=2;
          } else {
            String [] tmp=null;
            tmp = mdlName.split("\\.");
            out.println("tmp[0]="+tmp[0]);
            if (tmp[0].equals("factor")){
               typeValue=3;
            }
          }
        
        }
      }
      
      
      out.println("model type="+typeValue);
      ls.add(Integer.toString(typeValue));
      return ls;
    }
    
    
// end of advStat
//<---------------------------------------------------------------------------


//--------------------------------------------------------------------------->
// subsetting-instruction section
//--------------------------------------------------------------------------->

    
    // message block
    private HtmlOutputText txtSubsettingInstruction = new HtmlOutputText();

    public HtmlOutputText getTxtSubsettingInstruction() {
        return txtSubsettingInstruction;
    }

    public void setTxtSubsettingInstruction(HtmlOutputText hot) {
        this.txtSubsettingInstruction = hot;
    }


// end of subsetting-instruction 
//<---------------------------------------------------------------------------

//--------------------------------------------------------------------------->
// search box section
//--------------------------------------------------------------------------->


    private HtmlInputText textField4 = new HtmlInputText();

    public HtmlInputText getTextField4() {
        return textField4;
    }

    public void setTextField4(HtmlInputText hit) {
        this.textField4 = hit;
    }

// ui:drowpDown: howManyRows
// howManyRows@binding

    private DropDown howManyRows = new DropDown();

    public DropDown getHowManyRows() {
        return howManyRows;
    }

    public void setHowManyRows(DropDown dd) {
        this.howManyRows = dd;
    }
// howManyRows@items
    private SingleSelectOptionsList howManyRowsOptions = new SingleSelectOptionsList();
    
    public SingleSelectOptionsList getHowManyRowsOptions(){
      return howManyRowsOptions;
    }
    public void setHowManyRowsOptions(SingleSelectOptionsList ssol){
      this.howManyRowsOptions= ssol;
    }


// howManyRows@valueChangeListener

    public void howManyRows_processValueChange(ValueChangeEvent vce) {
       // the value of show-all-rows option == 0
       out.println("new number of Rows="+vce.getNewValue());
       out.println("current Row Index(1)="+data.getRowIndex());
       String selectedNoRows = (String)howManyRows.getSelected();
       out.println("selected number of Rows="+selectedNoRows);
       int newNoRows = Integer.parseInt(selectedNoRows);
       if (newNoRows == 0){
          newNoRows = data.getRowCount();
       }
       out.println("acutual selected number of Rows="+newNoRows);
       data.setRows(newNoRows);
       out.println("first row to be shown="+data.getFirst());
       out.println("current Row Index(2)="+data.getRowIndex());

       //scroll(0);
       //FacesContext.getCurrentInstance().renderResponse();
    }
    private String howManyRowsClientId;
    
    public String getHowManyRowsClientId(){
      FacesContext context = FacesContext.getCurrentInstance();
      return howManyRows.getClientId(context);
    }

//--------------------------------------------------------------------------->
// variable Table section
//--------------------------------------------------------------------------->


// Variable Table: h:dataTable@binding

    private UIData data = null;

    public UIData getData() {
        return data;
    }

    public void setData(UIData data) {
        this.data = data;
    }
    
//--------------------------------------------------------------------------->
// scroller-related settings
//--------------------------------------------------------------------------->
    
    public String first(){
        scroll(0);
        return(null);
    }
    
    public String last(){
        scroll(data.getRowCount()-1);
        return (null);
    }
    public String next(){
        int first=data.getFirst();
        scroll(first + data.getRows());
        return (null);
    }
    
    public String previous() {
        int first = data.getFirst();
        scroll(first - data.getRows());
        return (null);
    }
    
    public void scroll(int row){
        int rows=data.getRows();
        out.println("within scroll:rows="+rows);
        if(rows<1){
            return;
        }
        if (rows<0) {
            data.setFirst(0);
        } else if (rows>=data.getRowCount()) {
            data.setFirst(data.getRowCount()-1);
        } else {
            data.setFirst(row-(row % rows));
        }
    }
    
    public void processScrollEvent(ActionEvent event){
        int currentRow=1;
        UIComponent component = event.getComponent();
        Integer curRow = (Integer) component.getAttributes().get("currentRow");
        out.println("within processScrollEvent: curRow="+curRow);
        if (curRow!=null){
            currentRow=curRow.intValue();
        }
        scroll(currentRow);
    }
    
//--------------------------------------------------------------------------->
// select-all checkbox
//--------------------------------------------------------------------------->

    // ui:checkbox: checkboxSelectUnselectAll
    // @binding: 
    //  select/unselect all checkbox in the table header:

    private Checkbox checkboxSelectUnselectAll = new Checkbox();

    public Checkbox getCheckboxSelectUnselectAll() {
        return checkboxSelectUnselectAll;
    }

    public void setCheckboxSelectUnselectAll(Checkbox c) {
        this.checkboxSelectUnselectAll = c;
    }
    
    // ui:checkbox: checkboxSelectUnselectAll
    // @valueChangeListener
    
    public void selectUnselectAllCheckbox(ValueChangeEvent vce) {
      // toggle false to true or vice versa
      FacesContext context = FacesContext.getCurrentInstance();
      Boolean oldState    = (Boolean) vce.getOldValue();
      out.println("oldState="+oldState);
      Boolean currentState= (Boolean) vce.getNewValue();
      out.println("newState="+currentState);
      
      // clear the error message if it still exists
      resetMsgVariableSelection();


      // check the displayed rows
      int firstRow = data.getFirst();
      out.println("1st row-index value="+firstRow);
      int lastRow = data.getFirst()+data.getRows();
      out.println("tentative last row-index value="+lastRow);
      int remain  = data.getRowCount() - firstRow;
      if ( remain < data.getRows()){
        lastRow = data.getFirst()+remain;
        out.println("adjusted last row-index value="+lastRow);
      }
      out.println("how many rows are to be displayed="+data.getRows());

      //for (int i=0; i<dt4Display.size();i++){
      
      Set<String> bvIdSet = new HashSet<String>(); 
      Set<String> rmIdSet = new HashSet<String>();
      int bvcnt = 0;
      for (int i=firstRow; i<lastRow;i++){
         List<Object> rw = new ArrayList<Object>();
         rw = (ArrayList) dt4Display.get(i); 
         String varId = (String) rw.get(2);
         
         // check this var is a base-var for recoded vars
         if (!baseVarToDerivedVar.containsKey(varId)){
            rw.set(0, currentState);
            rmIdSet.add(varId);
         } else {
            // keep this var's checkbox checked 
            rw.set(0, Boolean.TRUE);
            bvcnt++;
            bvIdSet.add(varId);
         }
         dt4Display.set(i, rw);
      }
      out.println("conents:bvIdSet="+bvIdSet);
      out.println("number of recoded vars="+bvcnt);
      if (currentState){
        // select-all case
        out.println("add all variable to varCart, etc.");
        
        //for (int i=0; i<dt4Display.size();i++){
        for (int i=firstRow;i<lastRow;i++){
          String keyS = (String)((ArrayList)dt4Display.get(i)).get(2);
          String valueS  = (String)((ArrayList)dt4Display.get(i)).get(3);
          //Long newKey= Long.valueOf(keyS);
          //if (!varCart.containsKey(newKey)){
          if (!varCart.containsKey(keyS)){
              // varCart.put( newKey, valueS );
              varCart.put(  keyS, valueS );
              getVarSetAdvStat().add( new Option( keyS, valueS) );
          }
        }
        // activate buttons
        activateButtons();
        
      } else {
        // unselect-all case
        
if (bvcnt == 0){
// no recoded var case        
        out.println("un-select-all case: no recoding vars");
        // backing Map object
        varCart.clear();
        // LHS listbox
        varSetAdvStat.clear();
        // RHS listboxes
        advStatVarRBox1.clear();
        advStatVarRBox2.clear();
        advStatVarRBox3.clear();
        
        
        // RHS simulation option: unchecked
        checkbox3.setSelected(false);
        
        // simulation-type radio button group
        radioButtonGroup1.setSelected("0");
        groupPanel20.setRendered(false);
        groupPanel22.setRendered(false);
        
        // deactivate buttons
        deactivateButtons();
        clearRecodeTargetVarInfo();
        // clear the recodeTable area
        // to do
        
        // hide recode Area
        hideRecodeTableArea();
} else {
// at least one recoded var exists
        out.println("un-select-all: some variables are used for recoding");
        // clear varCart(Map) except for base vars for recoding
   
        for (String v: rmIdSet) {   
            varCart.remove(v);
        }

        out.println("pass the block for varCart");
        // clear varSetAdvStat except for base vars for recoding
        // LHS listbox object (Collection ArrayList<Option>)
        Collection<Option> tmpvs = new ArrayList<Option>();
        for (Iterator i = varSetAdvStat.iterator(); i.hasNext();) {
          Option el =  (Option)i.next();
          if ( bvIdSet.contains((String)el.getValue() ) ){
            tmpvs.add(new Option(el.getValue(), el.getLabel()));
            
          }
        }
        out.println("contents:tmpvs="+tmpvs);
        varSetAdvStat.clear();
        varSetAdvStat.addAll(tmpvs);

        
        out.println("pass the block for varSetAdvState");

        // RHS listbox1
        Collection<Option> tmpRBox1 = new ArrayList<Option>();
        for (Iterator i = advStatVarRBox1.iterator(); i.hasNext();) {
          Option el =  (Option)i.next();
          if ( !bvIdSet.contains((String)el.getValue() ) ){
            tmpRBox1.add(new Option(el.getValue(), el.getLabel()));

          }
        }
        advStatVarRBox1.clear();
        advStatVarRBox1.addAll(tmpRBox1);
        out.println("pass the block for advStatVarRBox1");

        // RHS listbox2
        Collection<Option> tmpRBox2 = new ArrayList<Option>();
        for (Iterator i = advStatVarRBox2.iterator(); i.hasNext();) {
          Option el =  (Option)i.next();
          if (bvIdSet.contains((String)el.getValue() ) ){
            tmpRBox2.add(new Option(el.getValue(), el.getLabel()));
          }
        }
        advStatVarRBox2.clear();
        advStatVarRBox2.addAll(tmpRBox2);

        
        // RHS listbox3
        Collection<Option> tmpRBox3 = new ArrayList<Option>();
        for (Iterator i = advStatVarRBox3.iterator(); i.hasNext();) {
          Option el =  (Option)i.next();
          if (bvIdSet.contains((String)el.getValue() ) ){
            tmpRBox3.add(new Option(el.getValue(), el.getLabel()));
          }
        }
        advStatVarRBox3.clear();
        advStatVarRBox3.addAll(tmpRBox3);


  msgVariableSelection.setRendered(true);
  msgVariableSelection.setText("At least one variable is used for recoding;<br />Remove its recoded variable(s) first.");
}
      }
      FacesContext.getCurrentInstance().renderResponse();
    }
    
//--------------------------------------------------------------------------->
// variable-table: 1st column checkbox
//--------------------------------------------------------------------------->

    
    // variable table: checkbox column
    // varCheckbox@binding
    private Checkbox varCheckbox = new Checkbox();

    public Checkbox getVarCheckbox() {
        return varCheckbox;
    }

    public void setVarCheckbox(Checkbox c) {
        this.varCheckbox = c;
    }
    
    // ui:checkbox (variable table: checkbox column)
    // varCheckbox@valueChangeListener
    public void updateCheckBoxState(ValueChangeEvent vce) {
      int cr=0;

      FacesContext context = FacesContext.getCurrentInstance();

      List<Object> tmpDataLine = (List<Object>) data.getRowData();
      out.println("current varName="+tmpDataLine.get(3));
      out.println("current varId="+tmpDataLine.get(2));
      String varId = (String) tmpDataLine.get(2);
      
      resetMsgVariableSelection();

      if ((Boolean) tmpDataLine.get(0)) {
        out.println("row Id="+tmpDataLine.get(2)+":current checkbox value is [true]");
      } else {
        out.println("row Id="+tmpDataLine.get(2)+":current checkbox value is [false]");
      }

      Boolean currentState= (Boolean) vce.getNewValue();
      

      // update the state of the selected value of this row

      tmpDataLine.set(0,currentState); 
      
      if (currentState){ 
        // put 
        //varCart.put(dt4Display.get(cr).getVarId(), dt4Display.get(cr).getVarName());
        // ID and Variable Name
        // varCart.put(Long.valueOf((String) tmpDataLine.get(2)), (String) tmpDataLine.get(3));
        varCart.put( (String) tmpDataLine.get(2), (String) tmpDataLine.get(3));
        
        getVarSetAdvStat().add( new Option( (String)tmpDataLine.get(2), (String)tmpDataLine.get(3) ) );
      } else{ 
        // remove 
        
        //varCart.remove(Long.valueOf((String) tmpDataLine.get(2)));
        // check this var is used for recoded var
        
if (baseVarToDerivedVar.containsKey(varId)){
  out.println("this var is already used for recoding and cannot be unchekced");
  // flip back the checkbox [checked]
  tmpDataLine.set(0, Boolean.TRUE);
  out.println("flip the boolean value");
  msgVariableSelection.setRendered(true);
  msgVariableSelection.setText("The variable ("+tmpDataLine.get(3)+") is used for recoding;<br />Remove its recoded variable(s) first.");

  
  FacesContext.getCurrentInstance().renderResponse();
} else {      
        
        varCart.remove(varId);
        

        // Lbox
        if (removeOption((String)tmpDataLine.get(2), getVarSetAdvStat()) == null) {
          // Rbox1
          if (removeOption((String)tmpDataLine.get(2), getAdvStatVarRBox1()) == null) {
            // Rbox2
            if (removeOption((String)tmpDataLine.get(2), getAdvStatVarRBox2())== null){
              // Rbox3 
              if (removeOption((String)tmpDataLine.get(2), getAdvStatVarRBox3()) == null){
                out.println("Unchecked var is not found in these boxes");
                log("Unchecked var is not found in these boxes");
              }
            }
          }
        }
        out.println("recoded var is="+getCurrentRecodeVariableId());
/*
        // reset recodeTab's input fields if applicable
        if (getCurrentRecodeVariableId().equals(tmpDataLine.get(2))){
            out.println("unchecked var is the current recode var="+getCurrentRecodeVariableId());
            // clear input fields
            clearRecodeTargetVarInfo();
        } else {
          // check whether this var is used for newly-created variables 
          // if so, remove this var from the table
          // else 
          // do nothing
        }
*/
}

      }

      if (!varCart.isEmpty()){ 
        // enable buttons 
        activateButtons();
      } else { 
        // disable buttons 
        deactivateButtons();
        // reset recode field
        
      }

    }
    

    // error message when a base-variable is unchecked
    // ui:staticText
    private StaticText msgVariableSelection = new StaticText();

    public StaticText getMsgVariableSelection() {
        return msgVariableSelection;
    }

    public void setMsgVariableSelection(StaticText txt) {
        this.msgVariableSelection = txt;
    }

    public void resetMsgVariableSelection() {
      out.println("******* within resetMsgVariableSelection *******");
      msgVariableSelection.setRendered(false);
      msgVariableSelection.setText(" ");
    }


    public void activateButtons(){
        dwnldButton.setDisabled(false);
        recodeButton.setDisabled(false);
        moveRecodeVarBttn.setDisabled(false);
        edaButton.setDisabled(false); 
        advStatButton.setDisabled(false);
    }
    
    public void deactivateButtons(){
        dwnldButton.setDisabled(true); 
        recodeButton.setDisabled(true);
        moveRecodeVarBttn.setDisabled(true);
        edaButton.setDisabled(true);
        advStatButton.setDisabled(true);
    }

//--------------------------------------------------------------------------->
// quick summary box
//--------------------------------------------------------------------------->

    // checkbox-style: deprecated
    public void showNoShowQuickSumStat (ValueChangeEvent vce){
        FacesContext context = FacesContext.getCurrentInstance();
        List<Object> tmpDataLine = (List<Object>) data.getRowData();
        Boolean currentState= (Boolean) vce.getNewValue();
        tmpDataLine.set(6,currentState);
        // 
        FacesContext.getCurrentInstance().renderResponse();
    }
    // icon button-style:
    public void showHideQuickSumStat(ActionEvent e){

        List<Object> tmpDataLine = (List<Object>) data.getRowData();
        if ((Boolean)tmpDataLine.get(6)) {
          // current:T => false(hide)
          tmpDataLine.set(6,Boolean.FALSE);
        } else {
          // current:F => true (show)
          tmpDataLine.set(5,"QS requested for"+tmpDataLine.get(2));
          tmpDataLine.set(6,Boolean.TRUE);
        }
        FacesContext.getCurrentInstance().renderResponse();

    }
    // button including the contents block

    public void displayQuickSumStat(ActionEvent e){
        List<Object> tmpDataLine = (List<Object>) data.getRowData();
         out.println("displayQuickSumStat: current state="+tmpDataLine.get(5));
       if ((Boolean)tmpDataLine.get(6)) {
          // current:T => false(hide)
          tmpDataLine.set(6,Boolean.FALSE);
        } else {
          // current:F => true (show)
          String variableId = (String)tmpDataLine.get(2);
          // create contents
          // set the contents to dataTable's row
          tmpDataLine.set(5,quickSummaryContentsCreator(variableId));
          // update the 6 th column
          tmpDataLine.set(6,Boolean.TRUE);
        }
        FacesContext.getCurrentInstance().renderResponse();
    }

    public DataVariable getVariableById(String varId){

      DataVariable dv=null;
      for (Iterator el = dataVariables.iterator(); el.hasNext();) {
        dv =(DataVariable) el.next();
        // Id is Long
        if (dv.getId().toString().equals(varId)){
          return dv;
        }
      }
      return dv;
    }
    
  // Headers  for summary statistics  
    public Map<String, String> sumStatHeaderCntn = new HashMap<String, String>();

    // contents creator
    public String quickSummaryContentsCreator(String varId){
      StringBuilder colQS = new StringBuilder();
//    colQS.append("QS requested for variableId="+varId);
      String columnHeaderCat = "Value(Label)";
      Collection<SummaryStatistic> sumStat=null;
      Collection<VariableCategory> catStat=null;
      int counter=0;
      int dbglns  = 50;
      DataVariable dv=null;
      for (Iterator el = dataVariables.iterator(); el.hasNext();) {
            counter++;
            dv =(DataVariable) el.next();
            // Id is Long
            if (dv.getId().toString().equals(varId)){
              sumStat = dv.getSummaryStatistics();
              catStat = dv.getCategories();
              break;
            }
      }

      if ( sumStat.isEmpty() && catStat.isEmpty() ){
        // no data case
        colQS.append("Summary Statistics not Recorded");

      } else if ( !(sumStat.isEmpty()) && catStat.isEmpty() ) {



        if (counter <= dbglns){
          out.println("sumStat-only case started: row no="+counter);
        }


        // sumstat only case

        // continuous
        //dv.getSummaryStatistics() returns the wholec Collection of SummaryStatistic type
        //dv.getSummaryStatistics().getType() returns SummaryStatisticType ()
        //                         .getValue() returns String
        //getName() returns String ["average", etc.]
        // note: exists(sumStat) only may not sumStat only: both

        Map<String, String> sumStatSet= new LinkedHashMap<String, String>();

        for (Iterator els = sumStat.iterator(); els.hasNext();) {
           SummaryStatistic dvsum = (SummaryStatistic) els.next();
           // key:statistic-type, value: its value
           sumStatSet.put(sumStatHeaderCntn.get(dvsum.getType().getName()), dvsum.getValue());
        }
        sumStatSet.put("UNF", dv.getUnf());

        colQS.append(getmlTableFrag(sumStatSet, "Statistic", "Value"));

        if (counter <= dbglns){
          out.println("sumStat-only case ended: row no="+counter);
        }


      } else if ( sumStat.isEmpty() && !(catStat.isEmpty()) ){
        // catStat only
        // discrete

        if (counter <= dbglns){
          out.println("catStat-only case started: row no="+counter);
        }

        //dv.getCategories() returns the whole Collection of VariableCategory type.

        Map<String, String> catStatSet= new LinkedHashMap<String, String>();

        for (Iterator elc = catStat.iterator(); elc.hasNext();) {
           VariableCategory dvcat = (VariableCategory) elc.next();
           // key:statistic-type, value: its freq

           StringBuilder sb = new StringBuilder();
           sb.append(dvcat.getValue());
           if ( (dvcat.getLabel() == null) || (dvcat.getLabel().equals(""))) {
           } else {
            sb.append("(");
            sb.append(dvcat.getLabel());
            sb.append(")");
           }
           // getFrequency() may be null

           String freq;
           if (dvcat.getFrequency() == null){
             freq="0";
           } else {
             freq = dvcat.getFrequency().toString();
           }

           catStatSet.put(sb.toString(), freq);
        }
        catStatSet.put("UNF", dv.getUnf());

        colQS.append(getmlTableFrag(catStatSet, columnHeaderCat, "Frequency"));


        if (counter <= dbglns){
          out.println("catStat-only case ended: row no="+counter);
        }



      } else if ( !(sumStat.isEmpty()) && !(catStat.isEmpty()) ) {

        // discrete
        if (counter <= dbglns){
          out.println("sumStat/catStat case started: row no="+counter);
        }

        //dv.getCategories() returns the whole Collection of VariableCategory type.

        Map<String, String> catStatSet= new LinkedHashMap<String, String>();
        for (Iterator elc = catStat.iterator(); elc.hasNext();) {
           VariableCategory dvcat = (VariableCategory) elc.next();
           // key:statistic-type, value: its freq
           StringBuilder sb = new StringBuilder();
           sb.append(dvcat.getValue());

           if ( (dvcat.getLabel() == null) || (dvcat.getLabel().equals(""))) {
           } else {
             sb.append("(");
             sb.append(dvcat.getLabel());
             sb.append(")");
           }
           // getFrequency() might be null
           String freq;
           if (dvcat.getFrequency() == null){
             freq="0";
           } else {
             freq = dvcat.getFrequency().toString();
           }
           catStatSet.put(sb.toString(), freq);
        }
        catStatSet.put("UNF", dv.getUnf());

         colQS.append(getmlTableFrag(catStatSet, columnHeaderCat, "Frequency"));

        if (counter <= dbglns){
          out.println("sumStat/catStat case ended: row no="+counter);
        }

      } else {
        colQS.append("Summary Statistics Not Available");
      }

      return colQS.toString();
    }
    
    
/////////////////////////////////////////
//<---------------------------------------------------------------------------


// request(submit) button:
    public void setSubmitButtonEnabled(boolean bttn) {
        this.submitButtonEnabled = bttn;
    }

    boolean submitButtonEnabled= false;
    public boolean isSubmitButtonEnabled() {
        return this.submitButtonEnabled;
    }

    private HtmlCommandLink linkAction1 = new HtmlCommandLink();

    public HtmlCommandLink getLinkAction1() {
        return linkAction1;
    }

    public void setLinkAction1(HtmlCommandLink hcl) {
        this.linkAction1 = hcl;
    }

//<---------------------------------------------------------------------------

    //private Collection<DataVariable> dataVariables;
    private List<DataVariable> dataVariables = new ArrayList<DataVariable>();


    /**
     * Holds value of property summaryStatistics.
     */
    private Collection<SummaryStatistic> summaryStatistics;

    /**
     * Getter for property summaryStatistics.
     * @return Value of property summaryStatistics.
     */
    public Collection<SummaryStatistic> getSummaryStatistics() {
        return this.summaryStatistics;
    }

    /**
     * Setter for property summaryStatistics.
     * @param summaryStatistics New value of property summaryStatistics.
     */
    public void setSummaryStatistics(Collection<SummaryStatistic> summaryStatistics) {
        this.summaryStatistics = summaryStatistics;
    }


    private Collection<VariableCategory> categories;

    /**
     * Getter for property categories.
     * @return Value of property categories.
     */
    public Collection<VariableCategory> getCategories() {
        return this.categories;
    }

    /**
     * Setter for property categories.
     * @param categories New value of property categories.
     */
    public void setCategories(Collection<VariableCategory> categories) {
        this.categories = categories;
    }

//<---------------------------------------------------------------------------

//--------------------------------------------------------------------------->
// data for the Variable Table
//--------------------------------------------------------------------------->

  private List<Object> dt4Display = new ArrayList<Object>();
  
  public void setDt4Display(List<Object> dt) {
    this.dt4Display=dt;
  }

  public List<Object> getDt4Display() {
    return(dt4Display);
  }

  private void initDt4Display(){
      // prepare the list object (dt4Display) whose members are variable info 
      // each member (row) is like this:
      // [boolean(checkbox state), 
      // variable type, 
      // variable Id, 
      // variable name, 
      // variable label, 
      // summary statistics]
      // [false, "C",1L,"CELLS", "Subgroups for sample-see documentation", html-frag]
      int counter =0;
      int dbglns  = 50;
      for (Iterator el = dataVariables.iterator(); el.hasNext();) {
          DataVariable dv =(DataVariable) el.next();
          counter++;
          if (counter <= dbglns){
            out.println("dtId="+dtId+" : within initDt4Display: row no="+counter);
          }

          List<Object> rw = new ArrayList<Object>();
          // 0
          rw.add(new Boolean(false));
          // 1

          if (dv.getVariableFormatType().getName().equals("numeric")){
              if (dv.getVariableIntervalType()==null){
                rw.add("C");
              } else {
                if (dv.getVariableIntervalType().getId().intValue() == 2 ){
                   rw.add("C");
                } else {
                   rw.add("D");
                }
              }
          } else {
            rw.add("Chr");
          }

          // 2: ID
          rw.add(dv.getId().toString());
          // 3: Variable Name
          rw.add(dv.getName());
          //existingVarNameSet.add(dv.getName());
          // 4: Variable Label
          if (dv.getLabel()!=null){
             rw.add(dv.getLabel());
          } else {
             rw.add("[label missing]");
          }

          // 5 summary statistics(removed)

            rw.add("contents_here_"+counter);
          // 6 panelgroup state
          rw.add(Boolean.FALSE);
          dt4Display.add(rw);
      } // end of for loop (setup of dt4Display)

  }
  /*  
  //private Set<String> existingVarNameSet = new HashSet<String>();
  
  // set to check duplicated variable names
  private boolean isDuplicatedVariableName(String varName){
    if (existingVarNameSet.contains(varName)){
      return true;
    } else {
      return false;
    }
  }
  */  
  
//<---------------------------------------------------------------------------


  public String getVariableLabelfromId(String varId){
    
    for (int i=0; i<dt4Display.size();i++){
      if (((String) ((ArrayList) dt4Display.get(i)).get(2)).equals(varId)){
         return (String)((ArrayList)dt4Display.get(i)).get(4);
        
      }
    }
    return null;
  }
  /*
    because dt4Display is not a HashMap but a List,
    loop-through is necessary to get the variable-name of a given variable-ID
  */
  public String getVariableNamefromId(String varId){
    
    for (int i=0; i<dt4Display.size();i++){
      if (((String) ((ArrayList) dt4Display.get(i)).get(2)).equals(varId)){
         return (String)((ArrayList)dt4Display.get(i)).get(3);
        
      }
    }
    return null;
  }



  // quickSummary: HashMap to html table

  public String getmlTableFrag (Map mp, String hdrKey, String hdrValue){
          //StringBuilder sb = new StringBuilder("<table border='1px'><tr><td>"+hdrKey+"</td><td>"+hdrValue+"</td></tr>");
          
          StringBuilder sb = new StringBuilder("<div class='statbox'><table class='viTblinx'><tbody><tr><th>"+hdrKey+"</th><th>"+hdrValue+"</th></tr></tbody><tbody>");
          Set ent = mp.entrySet();
          for (Iterator itr = ent.iterator(); itr.hasNext();){
            Map.Entry en = (Map.Entry) itr.next();
            if (en.getKey().equals("UNF")){
                sb.append("<tr><th class='viUNF' title='Univeral Numeric Fingerprint'>"+en.getKey()+"</th><td>"+en.getValue()+"</td></tr>");
            } else {
                sb.append("<tr><th>"+en.getKey()+"</th><td>"+en.getValue()+"</td></tr>");
            }
          }
          sb.append("</tbody></table></div>");

    return sb.toString();
  }


// hide subsetting functions according to the user's status

    public void hideSubsettingFunctions(){
      // set @rendered of the following components false
      // tabSet
      tabSet1.setRendered(false);
      // subsetting intruction text
      txtSubsettingInstruction.setRendered(false);
      // select-all checkbox in the header(1st column) of dataTable
      checkboxSelectUnselectAll.setRendered(false);
      // variable-checkbox in the 1st column of dataTable
      varCheckbox.setRendered(false);
      // title of the recoded-var table
      recodedVarTableTitle.setRendered(false);
      
      // message of the recoded-var table
      //recodedVarTableState.setRendered(false);
      // recoded-var table
      recodedVarTable.setRendered(false);
    }




//<---------------------------------------------------------------------------



//</editor-fold>
//////////////////////////////////
// <editor-fold  desc="JSC page bean settings">
// defaultstate="collapsed"


    public AnalysisPage() {
    
    }

    /** 
     * <p>Return a reference to the application-scoped bean.</p>
     */
    protected AnalysisApplicationBean getAnalysisApplicationBean() {
        return (AnalysisApplicationBean)getBean("AnalysisApplicationBean");
    }



    public void init(){
      super.init();
      try {
          _init();
          out.println("pass _init() in doInit()");
          
          FacesContext cntxt = FacesContext.getCurrentInstance();
          
          ExternalContext exCntxt = FacesContext.getCurrentInstance().getExternalContext();
          
          Map<String, Object> sessionMap = exCntxt.getSessionMap();
          
          out.println("\ncontents of RequestParameterMap:\n"+exCntxt.getRequestParameterMap());
          
          //out.println("\ncontents of SessionMap:\n"+sessionMap);
          
          String currentViewStateValue= exCntxt.getRequestParameterMap().get(ResponseStateManager.VIEW_STATE_PARAM);
          
          out.println("ViewState value="+currentViewStateValue);
          
          out.println("VDCRequestBean: current VDC URL ="+getVDCRequestBean().getCurrentVDCURL());
          setStudyURL(getVDCRequestBean().getCurrentVDCURL());
          out.println("VDCRequestBean: studyId ="+getVDCRequestBean().getStudyId());
          out.println("VDCRequestBean ="+getVDCRequestBean());


          // clear session-scoped objects if this page is rendered for the first time
          if (currentViewStateValue == null){
            // first time visit to the SubsettingPage.jsp

              List<String> sessionObjects = new ArrayList<String>();

              Collections.addAll(sessionObjects, "dt4Display", "varCart", "varSetAdvStat",
                "groupPanel8below", "advStatVarRBox1", "advStatVarRBox2", "advStatVarRBox3",
                "setxDiffVarBox1", "setxDiffVarBox2", "checkboxSelectUnselectAll", 
                "currentModelName", "currentRecodeVariableId","currentRecodeVariableName", "recodedVarSet", "recodeSchema",
                "baseVarToDerivedVar","derivedVarToBaseVar","recodeVarNameSet");

              // clear the data for the dataTable
              for (String obj : sessionObjects){
                if (sessionMap.containsKey(obj)){
                  cntxt.getExternalContext().getSessionMap().remove(obj);
                }
              }
              out.println("left-over objects of the previous session have been removed");
          } 
          // get the dtId
          //Long dtId = getDtId();
          /*
          // If we're coming from studyPage
          if (dtId==null) {
              dtId = getVDCRequestBean().getDtId();
          }
          */

          // we need to create the VariableServiceBean
          if (dtId != null) {
              out.println("The process enters non-null-dtId case: dtId="+dtId);
              
              // retrieve the datatable by dtId and
              // set it into dataTable
              dataTable = variableService.getDataTable(dtId);
              
              // setDataTable(getVariableService().getDataTable(dtId));
              setFileName(dataTable.getStudyFile().getFileName());
              out.println("file Name="+fileName);

              
              // retrieve each var's data as DataVariable Class data 
              // and set them into Collection<DataVariable> dataVariables
              //setDataVariables(dataTable.getDataVariables());
              dataVariables.addAll(dataTable.getDataVariables());
              out.println("pass the addAll line");

              VDCUser user =null;
          if (getVDCSessionBean().getLoginBean() != null){
              user =  getVDCSessionBean().getLoginBean().getUser();
          }
              VDC vdc =       getVDCRequestBean().getCurrentVDC();
              out.println("VDCUser instnce="+user);
              out.println("VDC instnce="+vdc);


              StudyFile sf = dataTable.getStudyFile();
              // this info is later used to render the page differently
              if (sf.isFileRestrictedForUser(user, vdc)) {
                out.println("restricted=yes: this user does not have the subsetting permission");
                subsettingPageAccess =Boolean.FALSE;
                hideSubsettingFunctions();
              } else {
                out.println("restricted=no: this user has the subsetting permission");
              }
          //} else {
              // not-log-in case
              //out.println("This user reached the subsetting page without log-in, i.e., the file is public");
              
          //}
              //setStudyTitle(dataTable.getStudyFile().getFileCategory().getStudy().getTitle());
              out.println("Number of vars in this data table("+dtId+")="+dataTable.getVarQuantity());
              
              // set up the data for the dataTable
              if (!sessionMap.containsKey("dt4Display")){
                //
                // dt4Display does not exist => the page was rendered for the first time
                //
                out.println("dt4Display does not exist in the session map=>1st-time visit to this page");
                
                initDt4Display();
                
                out.println("how many variables in dt4Display="+getDt4Display().size());
                
                sessionMap.put("dt4Display",dt4Display);

                sessionMap.put("varCart",varCart);


                sessionMap.put("varSetAdvStat",varSetAdvStat);

                sessionMap.put("groupPanel8below",groupPanel8below);

                sessionMap.put("advStatVarRBox1",advStatVarRBox1);
                sessionMap.put("advStatVarRBox2",advStatVarRBox2);
                sessionMap.put("advStatVarRBox3",advStatVarRBox3);

                sessionMap.put("checkboxSelectUnselectAll",checkboxSelectUnselectAll);
                
                  

              } else {
                // set the stored data to the key page-scoped objects
                // postback cases
                out.println("dt4Display found in the session map=>postback");
                setDt4Display((List<Object>) cntxt.getExternalContext().getSessionMap().get("dt4Display"));

                // varCart=(Map<Long, String>) sessionMap.get("varCart");
                varCart=(Map<String, String>) sessionMap.get("varCart");


                varSetAdvStat= (List<Option>) sessionMap.get("varSetAdvStat");

                advStatVarRBox1= (Collection<Option>) sessionMap.get("advStatVarRBox1");

                advStatVarRBox2= (Collection<Option>) sessionMap.get("advStatVarRBox2");

                advStatVarRBox3= (Collection<Option>) sessionMap.get("advStatVarRBox3");
                
                setxDiffVarBox1= (Collection<Option>) sessionMap.get("setxDiffVarBox1");
                setxDiffVarBox2= (Collection<Option>) sessionMap.get("setxDiffVarBox2");
                currentModelName = (String) sessionMap.get("currentModelName");
                checkboxSelectUnselectAll = (Checkbox) sessionMap.get("checkboxSelectUnselectAll");
                
                if (currentRecodeVariableId==null){
                  out.println("currentRecodeVariableId is null");
                } else {
                  out.println("currentRecodeVariableId="+currentRecodeVariableId);
                }
                out.println("currentRecodeVariableId: received value from sessionMap="+(String) sessionMap.get("currentRecodeVariableId"));
                currentRecodeVariableId = (String) sessionMap.get("currentRecodeVariableId");
                out.println("new currentRecodeVariableId="+currentRecodeVariableId);
                
                //dropDown2 = (DropDown)cntxt.getExternalContext().getSessionMap().get("dropDown2");
                //dropDown3 = (DropDown)cntxt.getExternalContext().getSessionMap().get("dropDown3");
                
                out.println("model name(post-back block)="+currentModelName);
                // out.println("model name(post-back block)="+getCurrentModelName());
                
                
                //groupPanel8below= ((PanelGroup) cntxt.getExternalContext().getSessionMap().get("groupPanel8below"));
                
                //if (groupPanel22.isRendered()){}
                
                //checkboxGroupXtbOptions= (MultipleSelectOptionsList)sessionMap.get("checkboxGroupXtbOptions");
                
                
                // recode source variable name: header
                if (!sessionMap.containsKey("currentRecodeVariableName")){
                  FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRecodeVariableName", currentRecodeVariableName);
                } else {
                  currentRecodeVariableName= (String) sessionMap.get("currentRecodeVariableName");
                }
                
                out.println("new currentRecodeVariableName="+currentRecodeVariableName);

                // @value of recode target variable label
                if (!sessionMap.containsKey("recodeVariableLabel")){
                  FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodeVariableLabel", recodeVariableLabel);
                } else {
                  recodeVariableLabel= (String) sessionMap.get("recodeVariableLabel");
                }
                
                // @value of recodeTable in recode tab
                if (!sessionMap.containsKey("recodeDataList")){
                   FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodeDataList", recodeDataList);
                } else {
                  recodeDataList= (List<Object>) sessionMap.get("recodeDataList");
                }
                
                // storage of recodeDataList
                if (!sessionMap.containsKey("recodeSchema")){
                   //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodeSchema", recodeSchema);
                } else {
                  recodeSchema=   (Map<String, List<Object>>) sessionMap.get("recodeSchema");
                }
                
                // @value of recodedVarTable
                if (!sessionMap.containsKey("recodedVarSet")){
                  //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recodedVarSet", recodedVarSet);
                } else {
                    recodedVarSet=  (List<Object>) sessionMap.get("recodedVarSet");
                }
                
                if (!sessionMap.containsKey("derivedVarToBaseVar")){
                  sessionMap.put("derivedVarToBaseVar", derivedVarToBaseVar);
                } else {
                  derivedVarToBaseVar = (Map<String, String>) sessionMap.get("derivedVarToBaseVar");
                }
                
                if (!sessionMap.containsKey("baseVarToDerivedVar")){
                  sessionMap.put("baseVarToDerivedVar", baseVarToDerivedVar);
                } else {
                  baseVarToDerivedVar = (Map<String, Set<String>>) sessionMap.get("baseVarToDerivedVar");
                }
                
                if (!sessionMap.containsKey("recodeVarNameSet")){
                  sessionMap.put("recodeVarNameSet", recodeVarNameSet);
                } else {
                  recodeVarNameSet = (Set<String>) sessionMap.get("recodeVarNameSet");
                }
                
                resetMsgSaveRecodeBttn();
                resetMsgVariableSelection();
                // end of post-back cases
              } // setup for the key data
              
          } else {
            // dtId is not available case
            // WE SHOULD HAVE A dataTable ID, throw an error
            out.println("ERROR: in AnalysisPage, without a serviceBean or a dtId");
            log("ERROR: in AnalysisPage, without a serviceBean or a dtId");
          }
          
          // prepare study Title and Id
          if (sessionMap.containsKey(getStudyUIclassName())){
            out.println("key was found in the session Map");

            StudyUI sui  = (StudyUI) sessionMap.get(getStudyUIclassName());

            setStudyTitle(sui.getStudy().getTitle());
            
            out.println("Study Title="+studyTitle);
            
            setStudyId(sui.getStudy().getId());
            
            out.println("Study Id="+studyId);
            
            setCitation(sui.getStudy().getCitation());
            out.println("ciation="+citation);

          } else {
            out.println("StudyUIclassName was not found in the session Map");
          }

          //out.println("\nSpec Map:\n"+getAnalysisApplicationBean().getSpecMap());
          //out.println("\nMenu Option List:\n"+getAnalysisApplicationBean().getModelMenuOptions());
          out.println("\ncontents of the cart:\n"+varCart);
          
      } catch (Exception e) {
          log("AnalysisPage Initialization Failure", e);
          throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
      } // end of try-catch block


      out.println("doInit(): current tab id="+tabSet1.getSelected());

      
    } // end of doInit()

/*
public void resetSetxDiffVarBox(String modelName){
}
*/


    public void preprocess() {
    }
    public void prerender() {
    }
    public void destroy() {
    }
//</editor-fold>


}