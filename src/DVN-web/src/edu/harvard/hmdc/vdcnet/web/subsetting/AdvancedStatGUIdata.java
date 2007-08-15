/**

AdvancedStatGUIdata.java

*/

package edu.harvard.hmdc.vdcnet.web.subsetting;

import java.util.*;
import static java.lang.System.*;
import java.math.*;
import edu.harvard.hmdc.vdcnet.dsb.zelig.*;
import java.util.logging.Logger;

/*
  AdvancedStatGUIdata

*/
public class AdvancedStatGUIdata {
    private static Logger theLogger = Logger.getLogger(AdvancedStatGUIdata.class.getName());
    
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
      //  out.println("List size="+modelCategoryList.size());
      return modelCategoryList;
    }

    // get Id-name hash
    
    protected Map<String, String> modelId2NameMap;
    public Map<String, String> getModelId2NameMap() {
      if (modelId2NameMap == null){
        modelId2NameMap = new HashMap<String, String>();
        for ( AdvancedStatGUIdata.Model mdl : this.model){
          //out.println(mdl.getMdlId()+"=>"+mdl.getMdlName());
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
          // 
          out.println("\n+++++++++++++++ start of model("+z.getName()+") +++++++++++++++\n");

          // fields that do not require calculations

          // mdlId
          String id = String.format("%03d",ii);
          StringBuilder sb = new StringBuilder("zlg_");
          sb.append(id);
          String mdlId = sb.toString();
         out.println("mdlId="+mdlId);

          mdlii.setMdlId(mdlId);

          // mdlName
          String mdlName = z.getName();
          // out.println("mdlName="+mdlName);

          mdlii.setMdlName(mdlName);


          // packageDep: might be > 1
          List<Zelig.Model.PackageDependency> pdl = z.getPackageDependency();
          // out.println("how many dependency-packages="+pdl.size());


          // create shallow String-type list for convenience
          List<String> pckgDp = new ArrayList<String>();

          int ipd = 0;
          for (Zelig.Model.PackageDependency pd : pdl ){
              ipd++;
              if (pd.getName() != null && !(pd.getName().equals(null)) ) {
                  // out.println("packageDep("+ipd+")="+pd.getName());
                  if (pd.getName().equals("sandwich")){
                      // do nothing
                  } else {
                      pckgDp.add(pd.getName());
                  }
              }
          }

          // out.println("pd check = "+list2String(pckgDp));
          mdlii.setPackageDep(pckgDp.get(0));

          // [model] category
          String category = z.getLabel();
          // out.println("category="+category);
          
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
          String helplink = z.getHelpLink().getUrl();
          // out.println("helplink="+ helplink );

          mdlii.setHelplink(helplink);

          // sepcialFunction
          String specialFunction = z.getSpecialFunction();
          // out.println("specialFunction="+specialFunction);

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
          // out.println("title="+title);

          mdlii.setTitle(title);

          // maxSetx
          int maxSetx = z.getSetx().getMaxSetx();
          // out.println("maxSetx="+maxSetx);

          mdlii.setMaxSetx(maxSetx);

          // how many equations
          // out.println("# of equations="+z.getFormula().getEquation().size());


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
              // out.println("outcome: total minVar="+minOutc);

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
              // out.println("exMinVar: explanatory: minVar total="+exMinVar );
              // out.println("explanatory: sum of minVar="+minExpl);

              // minVars = minOutc + minExpl
              int minVars = minOutc + minExpl;
              // out.println("minVars: sum(minOutc + minExpl)="+minVars);

          mdlii.setMinVars(minVars);

              // nobox
              int nobox =  Eq.get(0).getOutcome().size() + Eq.get(0).getExplanatory().size();
              // out.println("# of equations: all="+nobox);

              // noYs
              int noYs = Eq.get(0).getOutcome().size();
              // out.println("# of equations: outcome="+noYs);

              // noXs
              int noXs = Eq.get(0).getExplanatory().size();                 
              // out.println("# of equations: explanatory="+noXs);

              // noRBoxes
              int noRBoxes = noXs + noYs;
              // out.println("noRBoxes: # of RHS boxes="+noRBoxes);
          mdlii.setNoRboxes(noRBoxes);



              //out.println("How many outcome boxes="+ Eq.get(0).getOutcome().size());

              //out.println("\n+++++++++++++++ start of outcome +++++++++++++++\n");

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
                  //out.println("\n/////////// start of outcome("+k+") ///////////\n");

              vbxi.setType("D");

                  int minvl = ot.getMinVar().intValue();
                  int maxvl = ot.getMaxVar().intValue();

                  // fail-safe measure for factor analysis models
                  if (maxvl == 1) {
                    if ((minvl > 1) && (noXs == 0)) {
                        maxvl = Integer.MAX_VALUE;
                    }
                  }
                  //out.println("outcome("+k+"-th): minvl="+minvl);
                  //out.println("outcome("+k+"-th): maxvl="+maxvl);


                  vbxi.setMinvar(minvl);
                  vbxi.setMaxvar(maxvl);
                  // modelingType String
                  StringBuilder sbmt = new StringBuilder();

                  // for (Zelig.Model.Formula.Equation.Outcome.ModelingType mdt :ot.getModelingType()){

                  // iterate over modelingType tags
                  // check exists(continuous) ?

                  int otsize = ot.getModelingType().size();

                  //out.println("how many modeling types="+otsize);
                  //out.println("modeling types: contents="+list2string(ot.getModelingType()));
                  int fotsize =0;
                  for (int i=0; i<otsize ; i++){
                      Zelig.Model.Formula.Equation.Outcome.ModelingType mdt = ot.getModelingType().get(i);
                      // for each modelingType
                      //if  (mdt.getValue().equals("continuous")) {
                      //if ( mdt.getValue() == MODEL.fromValue("continuous")){
                      out.print("i="+i+"\t howmany="+otsize+"\n");
                      if (mdt.getValue() != null){
                      //if ( (mdt.getValue().value() != null) || (mdt.getValue().value().equals(""))){
                          String mdtv = mdt.getValue().value();

                          //out.println("current("+i+"-th) modelingType="+mdtv);

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
                        out.println("skip this model="+mdlName+" (modeling type for outcome is missing");
                        continue modelloop;
                    }

                  //out.println("noContin: continuous modelingtype="+noContin);
                  //out.println("noMT: # of modelingtype tags="+noMT);

                  // varType
                  String mdltset = sbmt.toString();
                  //out.println("varType(outcome: raw)("+k+")="+ mdltset);

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
                  //out.println("varType("+k+")="+varType);
              vbxi.setVarType(varType);


                  // outcome: box label


                  //out.println("raw yLabel: outcome box="+ot.getLabel());
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
                  //out.println("outcome:box-label="+yLabel);
                  //out.println("\n/////////// end of outcome("+k+") ///////////\n");

                  // varBox processing

              mdlii.getVarBox().add(vbxi);
              } // each outcome tag

              //out.println("\n+++++++++++++++ end of outcome +++++++++++++++\n");
              //out.println("\n+++++++++++++++ start of explanatory +++++++++++++++\n");
              // for each explanatory tag

              //if (Eq.get(0).getExplanatory().size() > 0){
              if (exMinVar > 0){
                  // explanatory var exists

                  int j=0;
                  for ( Zelig.Model.Formula.Equation.Explanatory ex : Eq.get(0).getExplanatory()) {
                      j++;
                  AdvancedStatGUIdata.Model.VarBox vbxi = new AdvancedStatGUIdata.Model.VarBox();

                      //out.println("\n/////////// start of explanatory("+j+") ///////////\n");

                  vbxi.setType("E");
                      int Eminvl=ex.getMinVar().intValue();
                      int Emaxvl=ex.getMaxVar().intValue();

                      if (Emaxvl == -1 ) {
                         Emaxvl = Integer.MAX_VALUE;
                      }
                      //out.println("explanatory("+j+"-th): Eminvl="+Eminvl);
                      //out.println("explanatory("+j+"-th): Emaxvl="+Emaxvl);

                  vbxi.setMinvar(Eminvl);
                  vbxi.setMaxvar(Emaxvl);


                      StringBuilder sbmte = new StringBuilder();
                      int etsize = ex.getModelingType().size();

                      //out.println("how many modeling types="+etsize);
                      //out.println("modeling types: contents="+list2string(ex.getModelingType()));

                      int EnoContin = 0;
                      int EnoMT =0;
                      int fetsize=0;
                      for (int i=0; i<etsize ; i++){
                        Zelig.Model.Formula.Equation.Explanatory.ModelingType emdt = ex.getModelingType().get(i);
                        if (emdt.getValue() != null){
                        String emdtv = emdt.getValue().value();
                        //out.println("current("+j+"-th) modelingType="+emdtv);
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
                            out.println("skip this model="+mdlName+" (modeling type for explanatory is missing)");
                            continue modelloop;
                        }
                      //out.println("EnoContin: continuous modelingtype="+EnoContin);
                      //out.println("EnoMT: # of modelingtype tags="+EnoMT);

                      // varType
                      String mdltsete = sbmte.toString();
                      //out.println("varType(explanatory: raw)("+j+")="+ mdltsete);


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
                      //out.println("varType("+j+")="+varType);

              vbxi.setVarType(varType);

                     // str xLabel;
                      //out.println("raw xLabel: explanatory box="+ex.getLabel());

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
                      //out.println("explanatory:box-label="+xLabel);
              vbxi.setLabel(xLabel);
                  // varBox processing

              mdlii.getVarBox().add(vbxi);

                     //out.println("\n/////////// end of explantory("+j+") ///////////\n");
                  }

              }

              //out.println("\n+++++++++++++++ end of explantory +++++++++++++++\n");
          } // each eq

          //out.println("\n+++++++++++++++ end of model("+z.getName()+") +++++++++++++++\n");
          
          //out.println("model dump:\n"+mdlii);
          //theLogger.info("model dump:\n"+mdlii);
          // add this model to the list
          this.getModel().add((ii-1), mdlii);
          //this.getModelId2SpecMap().put(mdlId, mdlii);
          this.getModelId2SpecMap().put(mdlName, mdlii);
        } // each model

        // test
        //out.println("how many models are processed="+this.getModel().size());
        /*
        for (AdvancedStatGUIdata.Model zl : this.getModel()){
          out.println("title="+zl.getTitle());
        }
        */
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

} // top class

