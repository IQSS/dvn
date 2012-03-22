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
/**
*
* AdvancedStatGUIdata.java
*
*/
package edu.harvard.iq.dvn.ingest.dsb;

import java.util.*;
import static java.lang.System.*;
import java.math.*;
import edu.harvard.iq.dvn.ingest.dsb.zelig.*;
import java.util.logging.*;
import java.util.regex.*;

import org.apache.commons.lang.*;
 

/*
*  AdvancedStatGUIdata
*
*/
public class AdvancedStatGUIdata  implements java.io.Serializable {

    // static fields
    
    private static String regex = "/zelig/doc/";
    private static Pattern p;
    private static Logger dbgLog = Logger.getLogger(AdvancedStatGUIdata.class.getPackage().getName());
    
    
    // private static String[] modelFilter = {"gam.logit", "gam.normal","gam.poisson","gam.probit","logit.gee"};
    // as of 2008-07-22
    // last update: 2008-11-25 (Zelig 3.4.0: mloglm and aov are  added)
    private static String[] modelFilter = {"aov", "coxph","ei.dynamic", "ei.hier", 
    "gamma.gee", "logit.gee", "normal.gee", "poisson.gee", "probit.gee", 
    "logit.gam", "normal.gam", "poisson.gam", "probit.gam",
    "cloglog.net", "gamma.net", "logit.net", "ls.net", "normal.net", 
    "poisson.net", "probit.net",
    "gamma.survey", "logit.survey", "normal.survey", "probit.survey","poisson.survey",
    "factor.mix","factor.ord","irt1d",
    "factor.bayes", "logit.bayes", "mlogit.bayes", "mloglm",
    "oprobit.bayes", "poisson.bayes", "probit.bayes",
    "tobit.bayes" };
    
    private static Set<String> excludedModels = new HashSet<String>();
    static {
        for (int i = 0; i < modelFilter.length;i++){
            excludedModels.add(modelFilter[i]);
        }
        p = Pattern.compile(regex);
    }
   // accessors
   // List of model-specs
    protected List<AdvancedStatGUIdata.Model> model=  new ArrayList<AdvancedStatGUIdata.Model>();
    
    public List<AdvancedStatGUIdata.Model> getModel(){
      return this.model;
    }
    
    // its Map version: mdlId=> spec
    protected Map<String, AdvancedStatGUIdata.Model> modelId2SpecMap = new TreeMap<String, AdvancedStatGUIdata.Model>();
    
    public Map<String, AdvancedStatGUIdata.Model> getModelId2SpecMap(){
      return this.modelId2SpecMap;
    }
    // get mdlId=>mdl_category list
    protected Map<String, String> modelCategory;
    
    public Map<String, String> getModelCategory(){
      return this.modelCategory;
    }
    
    // get mdl_category=>model_ID_List Map
    protected Map<String, List> modelCategoryId;
    
    public Map<String, List> getModelCategoryId(){
      return this.modelCategoryId;
    }
    
    // get sorted model-category list
    protected List<String> modelCategoryList;
    
    public List<String> getModelCategoryList(){
      if (modelCategoryList == null) {
        modelCategoryList =  new ArrayList<String>();
        modelCategoryList.addAll(new TreeSet<String>(modelCategoryId.keySet()));
      }
      dbgLog.finer("List size="+modelCategoryList.size());
      return modelCategoryList;
    }

    // get Id-name hash
    
    protected Map<String, String> modelId2NameMap;
    public Map<String, String> getModelId2NameMap() {
      if (modelId2NameMap == null){
        modelId2NameMap = new HashMap<String, String>();
        for ( AdvancedStatGUIdata.Model mdl : this.model){
          dbgLog.finer(mdl.getMdlId()+"=>"+mdl.getMdlName());
          modelId2NameMap.put(mdl.getMdlId(),mdl.getMdlName());
        }
      }
      return modelId2NameMap;
    }

    // Model class
    public static class Model {
          // scalar fields
          // mdlId
          protected String mdlId;
          public String getMdlId() {
              return mdlId;
          }
          public void setMdlId(String value) {
              this.mdlId = value;
          }

          // mdlName
          protected String mdlName;
          public String getMdlName() {
              return mdlName;
          }
          public void setMdlName(String value) {
              this.mdlName = value;
          }

          // packageDep 
          protected String packageDep;
          public String getPackageDep() {
              return packageDep;
          }
          public void setPackageDep(String value) {
              this.packageDep = value;
          }

          // category
          protected String category;
          public String getCategory(){
              return category;
          }
          public void setCategory (String value) {
            this.category = value;
          }

          // helplink
          protected String helplink;
          public String getHelplink(){
              return helplink;
          }
          public void setHelplink (String value) {
            this.helplink = value;
          }

          // minVars: int
          protected int minVars;
          public int getMinVars(){
              return minVars;
          }
          public void setMinVars (int value) {
            this.minVars = value;
          }

          // noRboxes: int
          protected int noRboxes;
          public int getNoRboxes(){
              return noRboxes;
          }
          public void setNoRboxes (int value) {
            this.noRboxes = value;
          }

          // title
          protected String title;
          public String getTitle() {
              return title;
          }
          public void setTitle(String value) {
              this.title = value;
          }

          // specialFn
          protected String specialFn;
          public String getSpecialFn(){
              return specialFn;
          }
          public void setSpecialFn (String value) {
            this.specialFn = value;
          }

          // maxSetx: int
          protected int maxSetx;
          public int getMaxSetx(){
              return maxSetx;
          }
          public void setMaxSetx(int value) {
            this.maxSetx = value;
          }

          @Override
          public String toString(){
            StringBuilder sb = new StringBuilder("object dump:\nclass name=model\n");
            sb.append("\nmdlName="+mdlName);
            sb.append("\nmdlId="+mdlId);
            sb.append("\ncategory="+category);
            sb.append("\nspecialFn="+specialFn);
            sb.append("\ntitle="+title);
            sb.append("\nminVars="+minVars);
            sb.append("\nnoRboxes="+noRboxes);
            sb.append("\nmaxSetx="+maxSetx);
            for (int i=0; i< varBox.size(); i++){
              sb.append("\n\nContents of varBox("+i+"):"+varBox.get(i).toString());
            }
            sb.append("\n--- end of object dump ---\n");
            return sb.toString();
          }

          // bxAttr
          // collection field: varBox
          protected List<AdvancedStatGUIdata.Model.VarBox> varBox;

          public List<AdvancedStatGUIdata.Model.VarBox> getVarBox() {
              if (varBox == null) {
                  varBox = new ArrayList<AdvancedStatGUIdata.Model.VarBox>();
              }
              return this.varBox;
          }

          public static class VarBox {
/*
              VarBox(String type, int minvar, int maxvar,
                     String varType,  String label
              ){
                  setType(type);
                  setMinvar(minvar);
                  setMaxvar(maxvar);
                  setVarType(varType);
                  setLabel(label);
              }
*/
              // fields
              // type: String
              protected String type;
              public String getType() {
                  return type;
              }
              public void setType(String value) {
                  this.type = value;
              }

              // minvar: int 
              protected int minvar;
              public int getMinvar() {
                  return minvar;
              }
              public void setMinvar(int value) {
                  this.minvar = value;
              }

              // maxvar: int
              protected int maxvar;
              public int getMaxvar() {
                  return maxvar;
              }
              public void setMaxvar(int value) {
                  this.maxvar = value;
              }

              // varType: String
              protected String varType;
              public String getVarType() {
                  return varType;
              }
              public void setVarType(String value) {
                  this.varType = value;
              }

              // label (yLabel|xLabel) String
              protected String label;
              public String getLabel() {
                  return label;
              }
              public void setLabel(String value) {
                  this.label = value;
              }
              @Override
              public String toString(){
                StringBuilder sb = new StringBuilder("\nobject dump:\nclass name=varBox");
                sb.append("\ntype="+type);
                sb.append("\nminvar="+minvar);
                sb.append("\nmaxvar="+maxvar);
                sb.append("\nvarType="+varType);
                sb.append("\nlabel="+label);
                sb.append("\n--- end of object dump ---\n");
                return sb.toString();
              }

          } // VarBox
          
    } // Model
    
    // AdvancedStatGUIdata: constructor for zeligConfig input
    public AdvancedStatGUIdata( List<Zelig.Model> zlgLst){
    
        if (this.modelCategory==null){
            this.modelCategory=new HashMap<String, String>();
        }

        if (this.modelCategoryId==null){
            this.modelCategoryId = new TreeMap<String, List>();
        }
        
        int ii =0;
        modelloop:
        for (Zelig.Model z : zlgLst){
          ii++;
          AdvancedStatGUIdata.Model mdlii = new AdvancedStatGUIdata.Model();
          dbgLog.fine("\n+++++++++++++++ start of model("+z.getName()+") +++++++++++++++\n");
          if (excludedModels.contains(z.getName())){
            ii--;
            dbgLog.fine("skip this model="+z.getName()+" (incompatible with DVN)");
            continue modelloop;
          }
          // fields that do not require calculations

          // mdlId
          String id = String.format("%03d",ii);
          StringBuilder sb = new StringBuilder("zlg_");
          sb.append(id);
          String mdlId = sb.toString();
          dbgLog.fine("mdlId="+mdlId);

          mdlii.setMdlId(mdlId);

          // mdlName
          String mdlName = z.getName();
          dbgLog.fine("mdlName="+mdlName);

          mdlii.setMdlName(mdlName);


          // packageDep: might be > 1
          List<Zelig.Model.PackageDependency> pdl = z.getPackageDependency();
          dbgLog.finer("how many dependency-packages="+pdl.size());


          // create shallow String-type list for convenience
          List<String> pckgDp = new ArrayList<String>();

          int ipd = 0;
          for (Zelig.Model.PackageDependency pd : pdl ){
              ipd++;
              if (pd.getName() != null && !(pd.getName().equals(null)) ) {
                  dbgLog.finer("packageDep("+ipd+")="+pd.getName());
                  if (pd.getName().equals("sandwich")){
                      // do nothing
                  } else {
                      pckgDp.add(pd.getName());
                  }
              }
          }

          // dbgLog.fine("pd check = "+list2String(pckgDp));
          mdlii.setPackageDep(pckgDp.get(0));

          // [model] category
          String category = z.getLabel();
          dbgLog.finer("category="+category);
          
          this.modelCategory.put(mdlName, category);
          //this.modelCategory.put(mdlId, category);
          
          if (this.modelCategoryId.containsKey(category)) {
            //this.modelCategoryId.get(category).add(mdlId);
            this.modelCategoryId.get(category).add(mdlName);
          } else {
            this.modelCategoryId.put(category, new ArrayList<String>());
            //this.modelCategoryId.get(category).add(mdlId);
            this.modelCategoryId.get(category).add(mdlName);
          }
          mdlii.setCategory(category);

          // helplink
          String helplinkRaw = z.getHelpLink().getUrl();
          dbgLog.finer("helplinkRaw="+ helplinkRaw);

          // temporary hack -- documentation files for individual Zelig models 
          // are temporarily not available on Gary's site; so instead we are 
          // replacing them all with links to http://gking.harvard.edu/zelig,
          
          // once the new version of Zelig is released and the documentation 
          // site is back up, we'll remove this hack. 
          

          /* 
           * oopsie-daisy, looks like this was already attempted 5 years ago:
           
          // temporary fix: until zelig package is updated
          Matcher matcher = p.matcher(helplinkRaw);
          String helplink = null;
          if (matcher.find()){
            helplink = matcher.replaceFirst("/zelig/docs/");
          }
           *
           */
          
          // So anyway, screw regexes - let's just hard-code the truncated URL:

          String helplink = "http://gking.harvard.edu/zelig";
          
          // -- L.A.
          mdlii.setHelplink(helplink);

          // sepcialFunction
          String specialFunction = z.getSpecialFunction();
          dbgLog.finer("specialFunction="+specialFunction);

          mdlii.setSpecialFn(specialFunction);

          // string var for a later conditional block
          String SF;
          if (specialFunction != null && !(specialFunction.equals(null)) ) {
              SF=specialFunction;
          } else {
              SF="none";
          }

          // title
          String title = z.getDescription();
          dbgLog.fine("title="+title);
         
          String sTitle = shortenTitle(title);
          dbgLog.fine("shorten="+sTitle);
          mdlii.setTitle(sTitle);

          // maxSetx
          int maxSetx = z.getSetx().getMaxSetx();
          dbgLog.finer("maxSetx="+maxSetx);

          mdlii.setMaxSetx(maxSetx);

          // how many equations
          dbgLog.finer("# of equations="+z.getFormula().getEquation().size());


          // mono-equation or not
          if (z.getFormula().getEquation().size() == 1){
              // single eq. model (not multi-equation model)
              //List<AdvancedStatGUIdata.Model.VarBox> vbx = mdlii.getVarBox();

              List<Zelig.Model.Formula.Equation> Eq = z.getFormula().getEquation();

              // minOutc
              List<Zelig.Model.Formula.Equation.Outcome> Outset = Eq.get(0).getOutcome();
              int minOutc  = 0;
              if (Outset.isEmpty()){
                // do nothing
              } else {
                for ( Zelig.Model.Formula.Equation.Outcome outm : Outset){
                  minOutc  += outm.getMinVar().intValue();
                }
              }
              dbgLog.finer("outcome: total minVar="+minOutc);

              // minExpl
              // exMinVar
              List<Zelig.Model.Formula.Equation.Explanatory> Explset = Eq.get(0).getExplanatory();
              int minExpl  = 0;
              int exMinVar= 0;

              if (Explset.isEmpty()){
                // do nothing
              } else {
                for ( Zelig.Model.Formula.Equation.Explanatory exm : Explset){
                  exMinVar += exm.getMinVar().intValue();
                  minExpl  += exm.getMinVar().intValue();
                }
              }
              dbgLog.finer("exMinVar: explanatory: minVar total="+exMinVar );
              dbgLog.finer("explanatory: sum of minVar="+minExpl);

              // minVars = minOutc + minExpl
              int minVars = minOutc + minExpl;
              dbgLog.finer("minVars: sum(minOutc + minExpl)="+minVars);

          mdlii.setMinVars(minVars);

              // nobox
              int nobox =  Eq.get(0).getOutcome().size() + Eq.get(0).getExplanatory().size();
              dbgLog.finer("# of equations: all="+nobox);

              // noYs
              int noYs = Eq.get(0).getOutcome().size();
              dbgLog.finer("# of equations: outcome="+noYs);

              // noXs
              int noXs = Eq.get(0).getExplanatory().size();                 
              dbgLog.finer("# of equations: explanatory="+noXs);

              // noRBoxes
              int noRBoxes = noXs + noYs;
              dbgLog.finer("noRBoxes: # of RHS boxes="+noRBoxes);
          mdlii.setNoRboxes(noRBoxes);



              dbgLog.finer("How many outcome boxes="+ Eq.get(0).getOutcome().size());

              dbgLog.finer("\n+++++++++++++++ start of outcome +++++++++++++++\n");

              // for each outcome tag
              int k = 0;

              for ( Zelig.Model.Formula.Equation.Outcome ot : Eq.get(0).getOutcome()) {
              
              // noContin
              int noContin = 0;
              
              // noMT
              int noMT =0;
              
                  // iteration counter: outcome tag
                  k++;
              AdvancedStatGUIdata.Model.VarBox vbxi = new AdvancedStatGUIdata.Model.VarBox();
                  dbgLog.finer("\n/////////// start of outcome("+k+") ///////////\n");

              vbxi.setType("D");

                  int minvl = ot.getMinVar().intValue();
                  int maxvl = ot.getMaxVar().intValue();

                  // fail-safe measure for factor analysis models
                  if (maxvl == 1) {
                    if (((minvl > 1) && (noXs == 0)) || (mdlName.equals("ei.RxC"))) {
                        maxvl = Integer.MAX_VALUE;
                    } 
                  }
                  dbgLog.finer("outcome("+k+"-th): minvl="+minvl);
                  dbgLog.finer("outcome("+k+"-th): maxvl="+maxvl);


                  vbxi.setMinvar(minvl);
                  vbxi.setMaxvar(maxvl);
                  // modelingType String
                  StringBuilder sbmt = new StringBuilder();

                  // for (Zelig.Model.Formula.Equation.Outcome.ModelingType mdt :ot.getModelingType()){

                  // iterate over modelingType tags
                  // check exists(continuous) ?

                  int otsize = ot.getModelingType().size();

                  dbgLog.finer("how many modeling types="+otsize);
                  dbgLog.finer("modeling types: contents="+list2string(ot.getModelingType()));
                  int fotsize =0;
                  for (int i=0; i<otsize ; i++){
                      Zelig.Model.Formula.Equation.Outcome.ModelingType mdt = ot.getModelingType().get(i);
                      // for each modelingType
                      //if  (mdt.getValue().equals("continuous")) {
                      //if ( mdt.getValue() == MODEL.fromValue("continuous")){
                      dbgLog.finer("i="+i+"\t howmany="+otsize+"\n");
                      if (mdt.getValue() != null){
                      //if ( (mdt.getValue().value() != null) || (mdt.getValue().value().equals(""))){
                          String mdtv = mdt.getValue().value();

                          dbgLog.finer("current("+i+"-th) modelingType="+mdtv);

                          if (mdtv.equals("continuous")){
                            noContin++;
                          }
                          noMT++;
                          // concatenate modelingType
                          sbmt.append(mdtv);
                          if (i < (otsize-1)) {
                              sbmt.append("|");
                          }
                      } else {
                        fotsize++;
                      }
                  }  // each modelingType
                    if (otsize == fotsize){
                        ii--;
                        dbgLog.fine("skip this model="+mdlName+" (modeling type for outcome is missing");
                        continue modelloop;
                    }

                  dbgLog.finer("noContin: continuous modelingtype="+noContin);
                  dbgLog.finer("noMT: # of modelingtype tags="+noMT);

                  // varType
                  String mdltset = sbmt.toString();
                  dbgLog.finer("varType(outcome: raw)("+k+")="+ mdltset);

                  StringBuilder str = new StringBuilder();

                  if (noMT == 5) {
                    str.append("any");
                  } else if (noMT == 1) {

                     String noMTvalue = Eq.get(0).getOutcome().get(k-1).getModelingType().get(0).getValue().value();

                      if (noMTvalue.equals("continuous")) {
                        str.append("continuous");

                      } else if (noMTvalue.equals("binary")) {
                        str.append("binary");

                      } else if (noMTvalue.equals("ordinal")) {
                        str.append("ordinal");

                      } else if (noMTvalue.equals("discrete")) {
                        str.append("discrete");

                      } else if (noMTvalue.equals("nominal")) {
                        str.append("nominal");
                      }

                  } else if ((noMT > 1) && (noMT < 5)) {
                    // concatenate them
                    //str.append("multiple");
                    str.append(mdltset);
                  } else {
                    str.append("any");
                  }

                  String varType= str.toString();
                  dbgLog.finer("varType("+k+")="+varType);
              vbxi.setVarType(varType);


                  // outcome: box label


                  dbgLog.finer("raw yLabel: outcome box="+ot.getLabel());
                  StringBuilder sbyLabel=  new StringBuilder();
                  if (ot.getLabel() != null && !(ot.getLabel().equals(null)) ) {
                     sbyLabel.append(ot.getLabel());
                  } else {
                      // label string is missing
                      if (k == 1) {
                          //if (z.getSpecialFunction().equals("Surv")) {
                          if (SF.equals("Surv")) {
                              sbyLabel.append("Duration");
                          } else {
                              if (noXs == 0) {
                                  sbyLabel.append("Observed");
                              } else if (noYs > 1){
                              } else if ((noYs == 1) && (noXs == 1)) {
                                  sbyLabel.append("Dependent");
                              } else {
                                  sbyLabel.append("Dependent");
                              }
                          }
                      } else if (k==2) {
                          //if (z.getSpecialFunction().equals("Surv")) {
                          if (SF.equals("Surv")) {
                              sbyLabel.append("Censored");
                          } else {                    
                              sbyLabel.append("Outcome("+k+")");
                          }
                      } else if (k >2) {
                          sbyLabel.append("Outcome("+k+")");
                      }

                  }

                  String yLabel = sbyLabel.toString();
              vbxi.setLabel(yLabel);
                  dbgLog.finer("outcome:box-label="+yLabel);
                  dbgLog.finer("\n/////////// end of outcome("+k+") ///////////\n");

                  // varBox processing

              mdlii.getVarBox().add(vbxi);
              } // each outcome tag

              dbgLog.finer("\n+++++++++++++++ end of outcome +++++++++++++++\n");
              dbgLog.finer("\n+++++++++++++++ start of explanatory +++++++++++++++\n");
              // for each explanatory tag

              //if (Eq.get(0).getExplanatory().size() > 0){
              if (exMinVar > 0){
                  // explanatory var exists

                  int j=0;
                  for ( Zelig.Model.Formula.Equation.Explanatory ex : Eq.get(0).getExplanatory()) {
                      j++;
                  AdvancedStatGUIdata.Model.VarBox vbxi = new AdvancedStatGUIdata.Model.VarBox();

                      dbgLog.finer("\n/////////// start of explanatory("+j+") ///////////\n");

                  vbxi.setType("E");
                      int Eminvl=ex.getMinVar().intValue();
                      int Emaxvl=ex.getMaxVar().intValue();

                      if (Emaxvl == -1 ) {
                         Emaxvl = Integer.MAX_VALUE;
                      }
                      dbgLog.finer("explanatory("+j+"-th): Eminvl="+Eminvl);
                      dbgLog.finer("explanatory("+j+"-th): Emaxvl="+Emaxvl);

                  vbxi.setMinvar(Eminvl);
                  vbxi.setMaxvar(Emaxvl);


                      StringBuilder sbmte = new StringBuilder();
                      int etsize = ex.getModelingType().size();

                      dbgLog.finer("how many modeling types="+etsize);
                      dbgLog.finer("modeling types: contents="+list2string(ex.getModelingType()));

                      int EnoContin = 0;
                      int EnoMT =0;
                      int fetsize=0;
                      for (int i=0; i<etsize ; i++){
                        Zelig.Model.Formula.Equation.Explanatory.ModelingType emdt = ex.getModelingType().get(i);
                        if (emdt.getValue() != null){
                        String emdtv = emdt.getValue().value();
                        dbgLog.finer("current("+j+"-th) modelingType="+emdtv);
                        if (emdtv.equals("continuous")){
                          EnoContin++;
                        }
                        EnoMT++;
                        // concatenate modelingType
                        sbmte.append(emdtv);
                        if (i < (etsize-1)) {
                            sbmte.append("|");
                        }
                        } else {
                            fetsize++;
                        }
                      } // each modelingType
                        if (fetsize == etsize){
                            ii--;
                            dbgLog.fine("skip this model="+mdlName+" (modeling type for explanatory is missing)");
                            continue modelloop;
                        }
                      dbgLog.finer("EnoContin: continuous modelingtype="+EnoContin);
                      dbgLog.finer("EnoMT: # of modelingtype tags="+EnoMT);

                      // varType
                      String mdltsete = sbmte.toString();
                      dbgLog.finer("varType(explanatory: raw)("+j+")="+ mdltsete);


                      StringBuilder str = new StringBuilder();


                      if (EnoMT == 5) {
                        str.append("any");
                      } else if (EnoMT == 1) {

                         String noMTvalue = Eq.get(0).getExplanatory().get(j-1).getModelingType().get(0).getValue().value();

                          if (noMTvalue.equals("continuous")) {
                            str.append("continuous");

                          } else if (noMTvalue.equals("binary")) {
                            str.append("binary");

                          } else if (noMTvalue.equals("ordinal")) {
                            str.append("ordinal");

                          } else if (noMTvalue.equals("discrete")) {
                            str.append("discrete");

                          } else if (noMTvalue.equals("nominal")) {
                            str.append("nominal");
                          }

                      } else if (EnoMT == 2) {
                        // concatenate them
                        //str.append("multiple");
                        str.append("nominal|ordinal");
                      } else {
                        str.append("any");
                      }

                      // varType
                      String varType= str.toString();
                      dbgLog.finer("varType("+j+")="+varType);

              vbxi.setVarType(varType);

                     // str xLabel;
                      dbgLog.finer("raw xLabel: explanatory box="+ex.getLabel());

                      StringBuilder sbyLabel=  new StringBuilder();
                      if (ex.getLabel() != null && !(ex.getLabel().equals(null)) ) {
                         sbyLabel.append(ex.getLabel());
                      } else {
                        // label string is missing
                        if (noXs == 1) {
                          sbyLabel.append("Explanatory");
                        } else if (noXs > 1){
                          sbyLabel.append("Explanatory("+j+")");
                        } else {
                          sbyLabel.append("Explanatory("+j+")");
                        }
                      }

                      String xLabel = sbyLabel.toString();
                      dbgLog.finer("explanatory:box-label="+xLabel);
              vbxi.setLabel(xLabel);
                  // varBox processing

              mdlii.getVarBox().add(vbxi);

                     dbgLog.finer("\n/////////// end of explantory("+j+") ///////////\n");
                  }

              }

              dbgLog.finer("\n+++++++++++++++ end of explantory +++++++++++++++\n");
          } // each eq

          dbgLog.finer("\n+++++++++++++++ end of model("+z.getName()+") +++++++++++++++\n");
          
          dbgLog.finer("model dump:\n"+mdlii);

          // add this model to the list
          this.getModel().add((ii-1), mdlii);
          //this.getModelId2SpecMap().put(mdlId, mdlii);
          this.getModelId2SpecMap().put(mdlName, mdlii);
        } // each model

        // test
        dbgLog.fine("\n\nhow many models are processed="+this.getModel().size());

        dbgLog.finer("\n\nmodel ID\tmodel Name\tModel Title");
        
        for (AdvancedStatGUIdata.Model zl : this.getModel()){
          dbgLog.finer(zl.getMdlId()+","+zl.getMdlName()+","+zl.getTitle());
        }

    }  // top: constructor



    private String complexMT() {
      StringBuilder sb = new StringBuilder();
      sb.append("continuous|binary");
      return sb.toString();
    }

    private String list2String (List lst) {
        String str;
        if (lst.isEmpty()){
          str ="";
        } else {
          Object[] obj =  lst.toArray();
          str = Arrays.deepToString(obj);
        }
        return str;
    }

    private String list2string (List lst) {
        String str;
        if (lst.isEmpty()){
          str ="";
        } else {
          Object[] obj =  lst.toArray();
          str = Arrays.toString(obj);
        }
        return str;
    }

    public String shortenTitle(String title){
        String st = StringUtils.replaceEach(title, 
            new String [] {"Dichotomous", "Regression","Continuous","Dependent","Variables","Categorical","Social Network"},
            new String [] {"Binary", "Reg","Cont","Dep","Vars","Cat","SN"});
        
        return st;
    }
} // top class

