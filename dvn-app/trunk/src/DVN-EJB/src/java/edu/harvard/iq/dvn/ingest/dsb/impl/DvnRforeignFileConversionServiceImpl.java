/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb.impl;

import edu.harvard.iq.dvn.ingest.dsb.*;
import java.io.*;
import static java.lang.System.*;
import java.util.*;
import java.util.logging.*;
import java.lang.reflect.*;
import java.util.regex.*;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import org.apache.commons.lang.*;
import org.apache.commons.lang.builder.*;

/**
 *
 * @author asone
 */

public class DvnRforeignFileConversionServiceImpl{

    // ----------------------------------------------------- static filelds
    
    private static Logger dbgLog = Logger.getLogger(DvnRforeignFileConversionServiceImpl.class.getPackage().getName());

    public static String DVN_TMP_DIR=null;
    public static String DSB_TMP_DIR=null;

    private static String TMP_DATA_FILE_NAME = "susetfile4Rjob";
    public static String TMP_DATA_FILE_EXT =".tab";
    private static String RSERVE_HOST = null;
    private static String RSERVE_USER = null;
    private static String RSERVE_PWD = null;    
    private static int RSERVE_PORT;
    private static String DSB_HOST_PORT= null;
    public static String DSB_CTXT_DIR =null;
    
    private static Map<String, String> dwlndParam = new HashMap<String, String>();
    
    private static Map<String, Method> runMethods = new HashMap<String, Method>();
    
    private static String regexForRunMethods = "^run(\\w+)Request$" ;
    private static String RESULT_DIR_PREFIX = "Zlg_";


    public static String RWRKSP_FILE_PREFIX = "dvnDataFramefile_";
    
        
    
    public static String dtflprefix = "dvnDataFile_";
    
    public static String TEMP_DIR = System.getProperty("java.io.tmpdir");
    
    static {
    
        DSB_TMP_DIR = System.getProperty("vdc.dsb.temp.dir");
        
        // fallout case: last resort
        if (DSB_TMP_DIR == null){
//            DSB_TMP_DIR="/tmp/VDC/DSB";
//            WEB_TMP_DIR="/tmp/VDC/webtemp";
            
            DVN_TMP_DIR ="/tmp/VDC";
            DSB_TMP_DIR = DVN_TMP_DIR + "/DSB";
            
        }
        
        RSERVE_HOST = System.getProperty("vdc.dsb.host");
        if (RSERVE_HOST == null){
            //RSERVE_HOST= "dsb-2.hmdc.harvard.edu";
            RSERVE_HOST= "vdc-build.hmdc.harvard.edu";
        }
        
        DSB_HOST_PORT = System.getProperty("vdc.dsb.port");
        if (DSB_HOST_PORT == null){
            DSB_HOST_PORT= "80";
        }
        
        RSERVE_USER = System.getProperty("vdc.dsb.rserve.user");
        if (RSERVE_USER == null){
            RSERVE_USER= "rserve";
        }
        
        RSERVE_PWD = System.getProperty("vdc.dsb.rserve.pwrd");
        if (RSERVE_PWD == null){
            RSERVE_PWD= "rserve";
        }
        

        if (System.getProperty("vdc.dsb.rserve.port") == null ){
            RSERVE_PORT= 6311;
        } else {
            RSERVE_PORT = Integer.parseInt(System.getProperty("vdc.dsb.rserve.port"));
        }
        

        
        
        
        // various constants: option-wise
        // download formats
        dwlndParam.put("D01","dat");
        dwlndParam.put("D02","ssc");
        dwlndParam.put("D03","dta");
        dwlndParam.put("D04","RData");
        
        Pattern p = Pattern.compile(regexForRunMethods);
        
        for (Method m: DvnRforeignFileConversionServiceImpl.class.getDeclaredMethods()){
            
            Matcher mtr = p.matcher(m.getName());
            
            if (mtr.matches()){
                runMethods.put(mtr.group(1), m);
            }
        }

    }

    static String VDC_R_STARTUP_FILE="vdc_startup.R";
    static String librarySetup = "source(paste(.libPaths()[1], '/../share/dvn/" + VDC_R_STARTUP_FILE + "', sep = ''));";
    boolean DEBUG = true;
    
    // ----------------------------------------------------- instance filelds
    public String PID = null;
    public String tempFileName = null;
    public String tempFileNameNew = null;
    public String wrkdir = null;
    public String requestdir = null;
    public List<String> replicationFile = new LinkedList<String>();
    
    // ----------------------------------------------------- constructor
    public DvnRforeignFileConversionServiceImpl(){
        dbgLog.fine("***** DvnRforeignFileConversionServiceImpl: within the constructor starts here *****");
        // initialization
        PID = RandomStringUtils.randomNumeric(6);
                 
        requestdir = "Zlg_" + PID;
        
        wrkdir = DSB_TMP_DIR + "/" + requestdir;

        
        tempFileName = DSB_TMP_DIR + "/" + TMP_DATA_FILE_NAME
                 +"." + PID + TMP_DATA_FILE_EXT;
        
        tempFileNameNew = wrkdir + "/" + TMP_DATA_FILE_NAME
                 +"." + PID + TMP_DATA_FILE_EXT;
                 
            dbgLog.fine("requestdir="+requestdir);
            dbgLog.fine("wrkdir="+wrkdir);
            dbgLog.fine("tempFileName="+tempFileName);
            dbgLog.fine("tempFileNameNew="+tempFileNameNew);

                 
    }


    
    public void setupWorkingDirectory(RConnection c){
        try{
            // set up the working directory
            // parent dir
            String checkWrkDir = "if (file_test('-d', '"+DSB_TMP_DIR+"')) {Sys.chmod('"+
            DVN_TMP_DIR+"', mode = '0777'); Sys.chmod('"+DSB_TMP_DIR+"', mode = '0777');} else {dir.create('"+DSB_TMP_DIR+"', showWarnings = FALSE, recursive = TRUE);Sys.chmod('"+DVN_TMP_DIR+"', mode = '0777');Sys.chmod('"+
            DSB_TMP_DIR+"', mode = '0777');}";
            dbgLog.fine("w permission="+checkWrkDir);
            c.voidEval(checkWrkDir);


        
        } catch (RserveException rse) {
            rse.printStackTrace();
        }
    }
    
    
    
    /** *************************************************************
     * Execute an R-based dvn statistical analysis request 
     *
     * @param sro    a DvnRJobRequest object that contains various parameters
     * @return    a Map that contains various information about results
     */    
    
    public Map<String, String> execute(DvnRJobRequest sro) {
        dbgLog.fine("***** DvnRforeignFileConversionServiceImpl: execute() starts here *****");
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        // temporary result
        Map<String, String> tmpResult = new HashMap<String, String>();
        
        try {
            // Set up an Rserve connection
            dbgLog.fine("sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
            
            dbgLog.fine("RSERVE_USER="+RSERVE_USER+"[default=rserve]");
            dbgLog.fine("RSERVE_PWD="+RSERVE_PWD+"[default=rserve]");
            dbgLog.fine("RSERVE_PORT="+RSERVE_PORT+"[default=6311]");

            RConnection c = new RConnection(RSERVE_HOST, RSERVE_PORT);
            dbgLog.fine("hostname="+RSERVE_HOST);

            c.login(RSERVE_USER, RSERVE_PWD);
            dbgLog.fine(">" + c.eval("R.version$version.string").asString() + "<");


            // save the data file at the Rserve side
            String infile = sro.getSubsetFileName();
            InputStream inb = new BufferedInputStream(
                    new FileInputStream(infile));

            int bufsize;
            byte[] bffr = new byte[1024];

            RFileOutputStream os = 
                 c.createFile(tempFileName);
            while ((bufsize = inb.read(bffr)) != -1) {
                    os.write(bffr, 0, bufsize);
            }
            os.close();
            inb.close();
            
            // Rserve code starts here
            dbgLog.fine("wrkdir="+wrkdir);
            c.voidEval(librarySetup);
            
            // check working directories
            setupWorkingDirectory(c);
            
            // variable type
            /* 
                vartyp <-c(1,1,1)
            */
            // java side
            // int [] jvartyp  = {1,1,1};// = mp.get("vartyp").toArray()
            // int [] jvartyp  = sro.getVariableTypes();
/*
            StringBuilder sb = new StringBuilder();
            for (int i = 0 ; i< jvartyp.length; i++){
                if (i == (jvartyp.length -1)){
                    sb.append(String.valueOf(jvartyp[i]));
                } else {
                    sb.append(String.valueOf(jvartyp[i])+", ");
                }
            }
            
            // R side
*/
            dbgLog.fine("raw variable type="+sro.getVariableTypes());
            c.assign("vartyp", new REXPInteger(sro.getVariableTypes()));
            String [] tmpt = c.eval("vartyp").asStrings();
            dbgLog.fine("vartyp length="+ tmpt.length + "\t " +
                StringUtils.join(tmpt,","));
        
            // variable format (date/time)
            /* 
                varFmt<-list();
                c.voidEval("varFmt<-list()");
            */
            
            Map<String, String> tmpFmt = sro.getVariableFormats();
            dbgLog.fine("tmpFmt="+tmpFmt);
            if (tmpFmt != null){
                Set<String> vfkeys = tmpFmt.keySet();
                String[] tmpfk = (String[]) vfkeys.toArray(new String[vfkeys.size()]);
                String[] tmpfv = getValueSet(tmpFmt, tmpfk);
                c.assign("tmpfk", new REXPString(tmpfk));
                c.assign("tmpfv", new REXPString(tmpfv));
                String fmtNamesLine = "names(tmpfv)<- tmpfk";
                c.voidEval(fmtNamesLine);
                String fmtValuesLine ="varFmt<- as.list(tmpfv)";
                c.voidEval(fmtValuesLine);
            } else {
                String [] varFmtN ={};
                List<String> varFmtV = new ArrayList<String>();
                c.assign("varFmt", new REXPList(new RList(varFmtV, varFmtN)));
            }
            /*
                vnames<-c("race","age","vote")
            */
            
            // variable names
            // String [] jvnames = {"race","age","vote"};
            
            String [] jvnamesRaw = sro.getVariableNames();
            String [] jvnames = null;
            
            //VariableNameFilterForR nf = new VariableNameFilterForR(jvnamesRaw);
            
            if (sro.hasUnsafedVariableNames){
                // create  list
                jvnames =  sro.safeVarNames;
                dbgLog.fine("renamed="+StringUtils.join(jvnames,","));
            } else {
                jvnames = jvnamesRaw;
            }
            String vnQList = DvnDSButil.joinNelementsPerLine(jvnames,true);
            
            
            c.assign("vnames", new REXPString(jvnames));
            
            // confirmation
            String [] tmpjvnames = c.eval("vnames").asStrings();
            dbgLog.fine("vnames:"+ StringUtils.join(tmpjvnames, ","));
            
        
            /*
                x<-read.table141vdc(file="/tmp/VDC/t.28948.1.tab", 
                    col.names=vnames, colClassesx=vartyp, varFormat=varFmt)
            */
    
            //String datafilename = "/nfs/home/A/asone/java/rcode/t.28948.1.tab";
            
            // tab-delimited file name = tempFileName
            String readtableline = "x<-read.table141vdc(file='"+tempFileName+
                "', col.names=vnames, colClassesx=vartyp, varFormat=varFmt )";
            dbgLog.fine("readtable="+readtableline);

            c.voidEval(readtableline);
        
            // safe-to-raw variable name
            /* 
              attr(x, "Rsafe2raw")<-list();
            */
            //if (nf.hasRenamedVariables()){
            if (sro.hasUnsafedVariableNames){
                dbgLog.fine("unsafeVariableNames exist");
                // create  list
                //jvnames = nf.getFilteredVarNames();
                jvnames = sro.safeVarNames;
                String[] rawNameSet  = sro.renamedVariableArray;
                String[] safeNameSet = sro.renamedResultArray;
                
                c.assign("tmpRN", new REXPString(rawNameSet));
                c.assign("tmpSN", new REXPString(safeNameSet));
                
                String raw2safevarNameTableLine = "names(tmpRN)<- tmpSN";
                c.voidEval(raw2safevarNameTableLine);
                String attrRsafe2rawLine = "attr(x, 'Rsafe2raw')<- as.list(tmpRN)";
                c.voidEval(attrRsafe2rawLine);
            } else {
                String attrRsafe2rawLine = "attr(x, 'Rsafe2raw')<-list();";
                c.voidEval(attrRsafe2rawLine);
            }
            
            //Map<String, String> Rsafe2raw = sro.getRaw2SafeVarNameTable();
            
            // asIs
            /* 
                for (i in 1:dim(x)[2]){if (attr(x,"var.type")[i] == 0) {
                x[[i]]<-I(x[[i]]);  x[[i]][ x[[i]] == '' ]<-NA  }}
            */
            String asIsline  = "for (i in 1:dim(x)[2]){ "+
                "if (attr(x,'var.type')[i] == 0) {" +
                "x[[i]]<-I(x[[i]]);  x[[i]][ x[[i]] == '' ]<-NA  }}";
            c.voidEval(asIsline);
            
            // replication: copy the data.frame
            String repDVN_Xdupline = "dvnData<-x";
            c.voidEval(repDVN_Xdupline);
            tmpResult.put("dvn_dataframe", "dvnData");

            // subsetting (mutating the data.frame strips non-default attributes 
            // 
            // variable type must be re-attached

            String varTypeNew = "vartyp<-c(" + StringUtils.join( sro.getUpdatedVariableTypesAsString(),",")+")";
            // c.voidEval(varTypeNew);
            dbgLog.fine("updated var Type ="+ sro.getUpdatedVariableTypes());
            c.assign("vartyp", new REXPInteger(sro.getUpdatedVariableTypes()));
            
            String reattachVarTypeLine = "attr(x, 'var.type') <- vartyp";
            c.voidEval(reattachVarTypeLine);
            
            // replication: variable type
            String repDVN_vt = "attr(dvnData, 'var.type') <- vartyp";
            c.voidEval(repDVN_vt);
            
            // variable Id
            /* 
                attr(x, "var.nmbr")<-c("v198057","v198059","v198060")
            */
            
            // String[] jvarnmbr = {"v198057","v198059","v198060"};
            //String[] jvarnmbr = sro.getVariableIds();
            // after recoding 
            String[] jvarnmbr = sro.getUpdatedVariableIds();

            String viQList = DvnDSButil.joinNelementsPerLine(jvarnmbr,true);
            c.assign("varnmbr",new REXPString(jvarnmbr));
            
            String attrVarNmbrLine = "attr(x, 'var.nmbr')<-varnmbr";
            c.voidEval(attrVarNmbrLine);
            
            // confirmation
            String [] vno = c.eval("attr(x, 'var.nmbr')").asStrings();
            dbgLog.fine("varNo="+StringUtils.join(vno, ","));

            // replication: variable number
            String repDVN_vn = "attr(dvnData, 'var.nmbr') <- varnmbr";
            c.voidEval(repDVN_vn);
            
            // variable labels
            /* 
                attr(x, "var.labels")<-c("race","age","vote")
            */
            
            // String[] jvarlabels = {"race","age","vote"};
            // String[] jvarlabels = sro.getVariableLabels();
            // after recoding
            String[] jvarlabels = sro.getUpdatedVariableLabels();
            
            String vlQList = DvnDSButil.joinNelementsPerLine(jvarlabels,true);

            c.assign("varlabels", new REXPString(jvarlabels));
            
            String attrVarLabelsLine = "attr(x, 'var.labels')<-varlabels";
            c.voidEval(attrVarLabelsLine);
            
            // confirmation
            String [] vlbl = c.eval("attr(x, 'var.labels')").asStrings();
            dbgLog.fine("varlabels="+StringUtils.join(vlbl, ","));
        
            // replication: 
            String repDVN_vl = "attr(dvnData, 'var.labels') <- varlabels";
            c.voidEval(repDVN_vl);
        
// --------- start: block to be used for the production code
            // value-label table
            /* 
                VALTABLE<-list()
                VALTABLE[["1"]]<-list(
                "2"="white",
                "1"="others")
                attr(x, 'val.table')<-VALTABLE
            */

            // create the VALTABLE
            String vtFirstLine = "VALTABLE<-list()";
            c.voidEval(vtFirstLine);
            
            // vltbl includes both base and recoded cases when it was generated
            Map<String, Map<String, String>> vltbl = sro.getValueTable();
            Map<String, String> rnm2vi = sro.getRawVarNameToVarIdTable();
            String[] updatedVariableIds = sro.getUpdatedVariableIds();
            
            //for (int j=0;j<jvnamesRaw.length;j++){
            for (int j=0;j<updatedVariableIds.length;j++){
                // if this variable has its value-label table,
                // pass its key and value arrays to the Rserve
                // and finalize a value-table at the Rserve
                
                //String varId = rnm2vi.get(jvnamesRaw[j]);
                String varId = updatedVariableIds[j];
                
                if (vltbl.containsKey(varId)){
                    
                    Map<String, String> tmp = (HashMap<String, String>)vltbl.get(varId);
                    Set<String> vlkeys = tmp.keySet();
                    String[] tmpk = (String[]) vlkeys.toArray(new String[vlkeys.size()]);
                    String[] tmpv = getValueSet(tmp, tmpk);
                    // debug
                    dbgLog.fine("tmp:k="+ StringUtils.join(tmpk,","));
                    dbgLog.fine("tmp:v="+ StringUtils.join(tmpv,","));
                    
                    // index number starts from 1(not 0)
                    int indx = j +1;
                    dbgLog.fine("index="+indx);
                    
if (tmpv.length > 0){
                    
                    c.assign("tmpk", new REXPString(tmpk));
                    
                    c.assign("tmpv", new REXPString(tmpv));
                    
                    String namesValueLine = "names(tmpv)<- tmpk";
                    c.voidEval(namesValueLine);
                    
                    
                    String sbvl = "VALTABLE[['"+ Integer.toString(indx)+"']]" + "<- as.list(tmpv)";
                    dbgLog.fine("frag="+sbvl);
                    c.voidEval(sbvl);
                    
                    // confirmation test for j-th variable name
                    REXP jl = c.parseAndEval(sbvl);
                    dbgLog.fine("jl("+j+") = "+jl);
}
                }
            }
            
            // debug: confirmation test for value-table
            dbgLog.fine("length of vl="+ c.eval("length(VALTABLE)").asInteger());
            String attrValTableLine = "attr(x, 'val.table')<-VALTABLE";
            c.voidEval(attrValTableLine);
            
            // replication: value-label table
            String repDVN_vlt = "attr(dvnData, 'val.table') <- VALTABLE";
            c.voidEval(repDVN_vlt);
            
// --------- end: block to be used for the production code

            
            // missing-value list: TO DO
            /*
                MSVLTBL<-list(); attr(x, 'missval.table')<-MSVLTBL
            */
            String msvStartLine = "MSVLTBL<-list();";
            c.voidEval(msvStartLine);
            // data structure
            String attrMissvalLine = "attr(x, 'missval.table')<-MSVLTBL";
            c.voidEval(attrMissvalLine);
            
            
            // replication: missing value table
            String repDVN_mvlt = "attr(dvnData, 'missval.table') <- MSVLTBL";
            c.voidEval(repDVN_mvlt);
            
            // attach attributes(tables) to the data.frame
            /*
                x<-createvalindex(dtfrm=x, attrname='val.index')
                x<-createvalindex(dtfrm=x, attrname='missval.index')
            */
            String createVIndexLine = "x<-createvalindex(dtfrm=x, attrname='val.index');";
            c.voidEval(createVIndexLine);
            String createMVIndexLine = "x<-createvalindex(dtfrm=x, attrname='missval.index');";
            c.voidEval(createMVIndexLine);



// reflection block: start ------------------------------------------>
        
        
            String requestTypeToken = sro.getRequestType();// (Download|EDA|Xtab|Zelig)
            dbgLog.fine("requestTypeToken="+requestTypeToken);
            // get a test method
            Method mthd = runMethods.get(requestTypeToken);
            dbgLog.fine("method="+mthd);
            
            try {
                // invoke this method
                result = (Map<String, String>) mthd.invoke(this, sro, c);
            } catch (InvocationTargetException e) {
                //Throwable cause = e.getCause();
                //err.format(cause.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            
            // add the variable list
            result.put("variableList", joinNelementsPerLine(jvnamesRaw, 5,
            null, false, null, null));
            
            //result.put("variableList",StringUtils.join(jvnamesRaw, ", "));
            
            // replication: var-level unf
            String repDVN_varUNF = "attr(dvnData, 'variableUNF') <- paste(unf(dvnData,version=3))";
            c.voidEval(repDVN_varUNF);
            
            // calculate the file-leve UNF
            
            String fileUNFline = "fileUNF <- paste(summary(unf(dvnData, version=3)))";
            c.voidEval(fileUNFline);
            String fileUNF = c.eval("fileUNF").asString();
            if (fileUNF == null){
                fileUNF = "NA";
            }

            // replication: file-level unf
            String repDVN_fileUNF = "attr(dvnData, 'fileUNF') <- fileUNF";
            c.voidEval(repDVN_fileUNF);

            String RversionLine = "R.Version()$version.string";
            String Rversion = c.eval(RversionLine).asString();
            
            // replication: R version
            String repDVN_Rversion = "attr(dvnData, 'R.version') <- R.Version()$version.string";
            c.voidEval(repDVN_Rversion);
            
            String zeligVersionLine = "packageDescription('Zelig')$Version";
            String zeligVersion = c.eval(zeligVersionLine).asString();
            
            //String RexecDate = c.eval("date()").asString();
             String RexecDate = c.eval("as.character(as.POSIXct(Sys.time()))").asString();
            // replication: date
            String repDVN_date = "attr(dvnData, 'date') <- as.character(as.POSIXct(Sys.time()))";
            c.voidEval(repDVN_date);
            
            String repDVN_dfOrigin = 
                "attr(dvnData, 'data.frame.origin')<- " +
                "list('provider'='Dataverse Network Project'," +
                "'format' = list('name' = 'DVN data.frame', 'version'='1.3'))";
            c.voidEval(repDVN_dfOrigin);
            /*
            if (result.containsKey("option")){
                result.put("R_run_status", "T");
            } else {
                result.put("option", requestTypeToken.toLowerCase()); //download  zelig eda xtab
                result.put("R_run_status", "F");
            }
            */
            if (sro.hasRecodedVariables()){
                if (sro.getSubsetConditions() != null){
                    result.put("subsettingCriteria", StringUtils.join(sro.getSubsetConditions(),"\n"));
                    // replication: subset lines
                    String[] sbst = null;
                    sbst = (String[])sro.getSubsetConditions().toArray(new String[sro.getSubsetConditions().size()]);
                    
                    c.assign("sbstLines", new REXPString(sbst));
                    
                    String repDVN_sbst = "attr(dvnData, 'subsetLines') <- sbstLines";
                    c.voidEval(repDVN_sbst);
                }
                /* to be used in the future? */
                if (sro.getRecodeConditions() != null){
                    result.put("recodingCriteria", StringUtils.join(sro.getRecodeConditions(),"\n"));
                    String[] rcd = null;
                    rcd = (String[])sro.getRecodeConditions().toArray(new String[sro.getRecodeConditions().size()]);
                    
                    c.assign("rcdtLines", new REXPString(rcd));
                    
                    String repDVN_rcd = "attr(dvnData, 'recodeLines') <- rcdtLines";
                    c.voidEval(repDVN_rcd);
                }
                
            }
            
            // save workspace as a replication data set
            
            String RdataFileName = "DVNdataFrame."+PID+".RData";
            result.put("Rdata", "/"+requestdir+ "/" + RdataFileName);
            
            String saveWS = "save('dvnData', file='"+ wrkdir +"/"+ RdataFileName +"')";
            dbgLog.fine("save the workspace="+saveWS);
            c.voidEval(saveWS);
            
            // write back the R workspace to the dvn 
            
            String wrkspFileName = wrkdir +"/"+ RdataFileName;
            dbgLog.fine("wrkspFileName="+wrkspFileName);
            
            int wrkspflSize = getFileSize(c,wrkspFileName);
            
            File wsfl = writeBackFileToDvn(c, wrkspFileName, RWRKSP_FILE_PREFIX,"RData", wrkspflSize);
            
            result.put("dvn_RData_FileName",wsfl.getName());
            
            if (wsfl != null){
                result.put("wrkspFileName", wsfl.getAbsolutePath());
                dbgLog.fine("wrkspFileName="+wsfl.getAbsolutePath());
            } else {
                dbgLog.fine("wrkspFileName is null");
            }
            
            
            
            result.put("library_1","VDCutil");

            result.put("fileUNF",fileUNF);
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("dsbContextRootDir",  DSB_CTXT_DIR );
            result.put("PID", PID);
            result.put("Rversion", Rversion);
            result.put("zeligVersion", zeligVersion);
            result.put("RexecDate", RexecDate);
            
            result.putAll(tmpResult);
            dbgLog.fine("result object (before closing the Rserve):\n"+result);
            
        
// reflection block: end
            
            // close the Rserve connection
            c.close();
        
        } catch (RserveException rse) {
            // RserveException (Rserve is not running)
            rse.printStackTrace();
            
            result.put("dsbContextRootDir",  DSB_CTXT_DIR );
            result.put("PID", PID);
            result.put("option",sro.getRequestType().toLowerCase());
            
            result.put("RexecError", "true");
            return result;

        } catch (REXPMismatchException mme) {
        
            // REXP mismatch exception (what we got differs from what we expected)
            mme.printStackTrace();
            
            result.put("dsbContextRootDir",  DSB_CTXT_DIR );
            result.put("PID", PID);
            result.put("option",sro.getRequestType().toLowerCase());

            result.put("RexecError", "true");
            return result;

        } catch (IOException ie){
            ie.printStackTrace();
            
            result.put("dsbContextRootDir",  DSB_CTXT_DIR );
            result.put("PID", PID);
            result.put("option",sro.getRequestType().toLowerCase());

            result.put("RexecError", "true");
            return result;
            
        } catch (Exception ex){
            ex.printStackTrace();
            
            result.put("dsbContextRootDir",  DSB_CTXT_DIR );
            result.put("PID", PID);

            result.put("option",sro.getRequestType().toLowerCase());

            result.put("RexecError", "true");
            return result;
        }
        
        return result;
        
    }
    

    // ----------------------------------------------------- utilitiy methods
    
    /**
     * Returns the array of values that corresponds the order of 
     * provided keys
     *
     * @param     
     * @return    
     */

    public static String[] getValueSet(Map<String, String> mp, String[] keys) {
        
        List<String> tmpvl = new ArrayList<String>();
        for (int i=0; i< keys.length; i++){
            tmpvl.add(mp.get(keys[i]));
        }
        String[] tmpv = (String[])tmpvl.toArray(new String[tmpvl.size()]);
        return tmpv;
    }
    
    
    /** *************************************************************
     * Handles a downloading request 
     *
     * @param     
     * @return    
     */
    public Map<String, String> runDownloadRequest(DvnRJobRequest sro, RConnection c){
    
        String optionBanner = "########### downloading option ###########\n";
        dbgLog.fine(optionBanner);
        
        Map<String, String> sr = new HashMap<String, String>();
        sr.put("requestdir", requestdir);
        sr.put("option", "download");
        
        String RcodeFile = "Rcode." + PID + ".R";
        
        try {
            // create a temp dir
            String createTmpDir = "dir.create('"+wrkdir +"')";
            dbgLog.fine("createTmpDir="+createTmpDir);

            c.voidEval(createTmpDir);
            
            String dwnldOpt = sro.getDownloadRequestParameter();
            String dwnldFlxt = dwlndParam.get(dwnldOpt);
            
            sr.put("format", dwnldFlxt);
            
            String dataFileName = "Data." + PID + "." + dwnldFlxt;
            
            sr.put("subsetfile", "/"+requestdir+ "/" +dataFileName);

            // data file to be copied back to the dvn
            String dsnprfx = wrkdir + "/" + dataFileName;
            
            String univarDataDwnld = "univarDataDwnld(dtfrm=x,"+
                "dwnldoptn='"+dwnldOpt+"'"+
                ", dsnprfx='"+dsnprfx+"')";
            
            dbgLog.fine("univarDataDwnld="+univarDataDwnld);
            
            c.voidEval(univarDataDwnld);
            
            int wbFileSize = getFileSize(c,dsnprfx);
            
            dbgLog.fine("wbFileSize="+wbFileSize);
            
            // write back the data file to the dvn
            
            File dtfl = writeBackFileToDvn(c, dsnprfx, dtflprefix, dwlndParam.get(dwnldOpt), wbFileSize);
            
            if (dtfl != null){
                sr.put("wbDataFileName", dtfl.getAbsolutePath());
                dbgLog.fine("wbDataFileName="+dtfl.getAbsolutePath());
            } else {
                dbgLog.fine("wbDataFileName is null");
            }
            
            // tab data file
            String mvTmpTabFile = "file.rename('"+ tempFileName +"','"+ tempFileNameNew +"')";
            c.voidEval(mvTmpTabFile);
            dbgLog.fine("move temp file="+mvTmpTabFile);

            // move the temp dir to the web-temp root dir
//            String mvTmpDir = "file.rename('"+wrkdir+"','"+webwrkdir+"')";
//            dbgLog.fine("web-temp_dir="+mvTmpDir);
//            c.voidEval(mvTmpDir);
            
        } catch (RserveException rse) {
            rse.printStackTrace();
            sr.put("RexecError", "true");
            return sr;
        }

        sr.put("RexecError", "false");
        return sr;
    }
    


    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public String joinNelementsPerLine(String[] vn, int divisor){
        String vnl = null;
        if (vn.length < divisor){
            vnl = StringUtils.join(vn, ", ");
        } else {
            StringBuilder sb = new StringBuilder();
            
            int iter =  vn.length / divisor;
            int lastN = vn.length % divisor;
            if (lastN != 0){
                iter++;
            }
            int iterm = iter - 1;
            for (int i= 0; i<iter; i++){
                int terminalN = divisor;
                if ((i == iterm )  && (lastN != 0)){
                    terminalN = lastN;
                }                
                for (int j = 0; j< terminalN; j++){
                    if ( (divisor*i +j +1) == vn.length){ 
                        sb.append(vn[j + i*divisor]);
                    } else {
                        
                        sb.append(vn[j + i*divisor] + ", ");
                    }
                }
                sb.append("\n");
            }
            vnl = sb.toString();
            dbgLog.fine(vnl);
        }
        return vnl;
    }
    
    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public String joinNelementsPerLine(String[] vn, int divisor, String sp, 
        boolean quote, String qm, String lnsp){
        if (!(divisor >= 1)){
            divisor = 1;
        } else if ( divisor > vn.length) {
            divisor = vn.length;
        }
        String sep = null;
        if (sp != null){
            sep = sp;
        } else {
            sep = ", ";
        }
        String qmrk = null;
        if (quote){
            if (qm == null){
                qmrk = ",";
            } else {
                if (qm.equals("\"")){
                    qmrk = "\"";
                } else {
                    qmrk = qm;
                }
            }
        } else {
            qmrk = "";
        }
        String lineSep = null;
        if (lnsp == null){
            lineSep = "\n";
        } else {
            lineSep = lnsp;
        }
        
        String vnl = null;
        if (vn.length < divisor){
            vnl = StringUtils.join(vn, sep);
        } else {
            StringBuilder sb = new StringBuilder();
            
            int iter =  vn.length / divisor;
            int lastN = vn.length % divisor;
            if (lastN != 0){
                iter++;
            }
            int iterm = iter - 1;
            for (int i= 0; i<iter; i++){
                int terminalN = divisor;
                if ((i == iterm )  && (lastN != 0)){
                    terminalN = lastN;
                }                
                for (int j = 0; j< terminalN; j++){
                    if ( (divisor*i +j +1) == vn.length){ 
                        
                        sb.append(qmrk + vn[j + i*divisor] + qmrk);
                        
                    } else {
                        
                        sb.append(qmrk + vn[j + i*divisor] + qmrk + sep);
                        
                    }
                }
                if (i < (iter-1)){
                sb.append(lineSep);
                }
            }
            vnl = sb.toString();
            dbgLog.fine("results:\n"+vnl);
        }
        return vnl;
    }
    
    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public File writeBackFileToDvn(RConnection c, String targetFilename,
        String tmpFilePrefix, String tmpFileExt, int fileSize){
        
        // set up a temp file
        File tmprsltfl = null;
        String resultFile =  tmpFilePrefix + PID + "." + tmpFileExt;
        //String resultFilePrefix = tmpFilePrefix + PID + "_";
        
        //String rfsffx = "." + tmpFileExt;
        RFileInputStream ris = null;
        OutputStream outbr   = null;
        try {
            //tmprsltfl = File.createTempFile(resultFilePrefix, rfsffx);
              tmprsltfl = new File(TEMP_DIR, resultFile);
            //outbr = new FileOutputStream(tmprsltfl);
            outbr = new BufferedOutputStream(new FileOutputStream(tmprsltfl));
            //File tmp = new File(targetFilename);
            //long tmpsize = tmp.length();
            // open the input stream
            ris = c.openFile(targetFilename);

            if (fileSize < 1024*1024*500){
                int bfsize = fileSize;
                 byte[] obuf = new byte[bfsize];
                 ris.read(obuf);
                 //while ((obufsize =)) != -1) {
                 outbr.write(obuf, 0, bfsize);
                 //}
            }
            ris.close();
            outbr.close();
            return tmprsltfl;
        } catch (FileNotFoundException fe){
            fe.printStackTrace();
            dbgLog.fine("FileNotFound exception occurred");
            return tmprsltfl;
        } catch (IOException ie){
            ie.printStackTrace();
            dbgLog.fine("IO exception occurred");
        } finally {
            if (ris != null){
                try {
                    ris.close();
                } catch (IOException e){
                   
                }
            }
            
            if (outbr != null){
                try {
                    outbr.close();
                } catch (IOException e){
                
                }
            }
        
        }
        return tmprsltfl;
    }
    
    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public int getFileSize(RConnection c, String targetFilename){
        dbgLog.fine("targetFilename="+targetFilename);
        int fileSize = 0;
        try {
            String fileSizeLine = "round(file.info('"+targetFilename+"')$size)";
            fileSize = c.eval(fileSizeLine).asInteger();
        } catch (RserveException rse) {
            rse.printStackTrace();
        } catch (REXPMismatchException mme) {
            mme.printStackTrace();
        }
        return fileSize;
    }
}
