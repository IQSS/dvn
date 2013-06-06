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

public class DvnRDataAnalysisServiceImpl {

    // ----------------------------------------------------- static filelds
    
    private static Logger dbgLog = Logger.getLogger(DvnRDataAnalysisServiceImpl.class.getPackage().getName());

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
    private static String R2HTML_CSS_DIR = null;
    public static String RWRKSP_FILE_PREFIX = "dvnDataFramefile_";
    private static boolean REPLICATION = true;
    public static String dtflprefix = "dvnDataFile_";
    public static String TEMP_DIR = System.getProperty("java.io.tmpdir");
    // TODO: 
    // relying on java.io.tempdir may not be the best idea here; at least 
    // on some platforms (MacOS!) this directory may be different between
    // JVM sessions; so a link created in glassfish/domain/.../docroot and 
    // pointing there may not necessarily point to the right place next time
    // glassfish runs. (need to check how it behaves on linux!)
    // A better solution would be to default to default to /tmp, but have it 
    // configurable through a JVM option. -- L.A.
    // (or perhaps just checking it with getCanonicalPath could suffice - ?)
    
    static {
    
        DSB_TMP_DIR = System.getProperty("vdc.dsb.temp.dir");
        
        // fallout case, if the JVM option is not set:
        if (DSB_TMP_DIR == null){ 
            DSB_TMP_DIR = "/tmp/VDC/DSB";
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
        
        DSB_CTXT_DIR = System.getProperty("vdc.dsb.webtempdir");
        if (DSB_CTXT_DIR == null) {
            DSB_CTXT_DIR = "/temp";
        }
        
        R2HTML_CSS_DIR = System.getProperty("vdc.dsb.r2htmlcssdir");
        if (R2HTML_CSS_DIR == null) {
            R2HTML_CSS_DIR = "/usr/local/VDC/R/library";
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
        
        for (Method m: DvnRDataAnalysisServiceImpl.class.getDeclaredMethods()){
            
            Matcher mtr = p.matcher(m.getName());
            
            if (mtr.matches()){
              runMethods.put(mtr.group(1), m);
            }
        }

    }
    // This is how to initialize the source library, without hardcoding 
    // the path:    
    static String VDC_R_STARTUP_FILE = "vdc_startup.R";
    static String librarySetup = "source(paste(.libPaths()[1], '/../share/dvn/" + VDC_R_STARTUP_FILE + "', sep = ''));";
    boolean DEBUG = true;
    
    // ----------------------------------------------------- instance filelds
    public String PID = null;
    public String tempFileName = null;
    public String tempRdataFileName = null;
    public String tempFileNameNew = null;
    private String tempOriginalFileName;
    
    public String wrkdir = null;
    public String requestdir = null;
    public List<String> historyEntry = new ArrayList<String>();
    public List<String> replicationFile = new LinkedList<String>();
    
    // ----------------------------------------------------- constructor
    public DvnRDataAnalysisServiceImpl(){
        /* This:
        String sep = File.pathSeparator;
        results in the ":" being used as a separator. This in turn results in 
        all files being piled in the same directory, instead of subdirectories:
        directory:subdirectory:file.txt instead of directory/subdirectory/file.txt.
        Worse still, it confuses the Analysis/Crosstab/Descriptive Stats components
        below - since they expect to find certain files in the slash-separated
        subdirectories below... Hence commented it out and went back to slash. 
        (But, for the millionth time, this code is quite a mess... :( -- L.A. */
        String sep = "/";
        // initialization
        PID = RandomStringUtils.randomNumeric(6);
                 
        requestdir = "Zlg_" + PID;
        
        wrkdir = DSB_TMP_DIR + sep + requestdir;
        
        tempFileName = DSB_TMP_DIR + sep + TMP_DATA_FILE_NAME + "." + PID + TMP_DATA_FILE_EXT;
        tempRdataFileName = DSB_TMP_DIR + sep + TMP_DATA_FILE_NAME + "_" + PID + ".Rdata";
        
        tempFileNameNew = wrkdir + sep + TMP_DATA_FILE_NAME +"." + PID + TMP_DATA_FILE_EXT;
    }


    public void setupWorkingDirectories(RConnection c){
        try{
            // set up the working directory
            // (we only need one; there used to be a separate temp directory, 
            // webtempdir, for files that we served over a direct http 
            // connection - but that has been eliminated many releases ago).
            
            // The chmod lines are almost certainly not needed anymore. Again,
            // this is a legacy leftover from the times when the results were
            // served by httpd; which was running as a separate process, and 
            // usually under a different PID - hence it was necessary to ensure
            // that it had read (and write?) access to the directory. 
            
            // wrkdir
            String checkWrkDr = "if (file_test('-d', '"+wrkdir+"')) {Sys.chmod('"+
            wrkdir+"', mode = '0777'); } else {dir.create('"+wrkdir+"', showWarnings = FALSE, recursive = TRUE);Sys.chmod('"+wrkdir+"', mode = '0777');}";
            dbgLog.fine("w permission:wrkdir="+checkWrkDr);
            c.voidEval(checkWrkDr);

        
        } catch (RserveException rse) {
            rse.printStackTrace();
        }

	// Now test for the temporary directory on the application side:
	
	try {

	    File tempDir = new File(TEMP_DIR, "DVN");
	    if ( !tempDir.exists() ) {
		tempDir.mkdir();
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
        }

	// and the link to it from the docroot directory:

	Properties p = System.getProperties();
        String domainRoot = p.getProperty("com.sun.aas.instanceRoot");
        dbgLog.fine("PROPERTY: com.sun.aas.instanceRoot="+domainRoot);

        String tempLocName = domainRoot+"/docroot/temp";
	File tempLoc = new File (tempLocName); 
	if ( !tempLoc.exists() ) {

	    String createSymLink = "/bin/ln -s "+TEMP_DIR+"/DVN "+tempLocName;
		
	    dbgLog.info("attempting to execute "+createSymLink);

	    try {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(createSymLink);
		int exitValue = process.waitFor();
	    } catch (Exception e) {
                dbgLog.info("failed to create symlink!");
		e.printStackTrace();
	    }
	} else {
	    dbgLog.fine("link "+tempLoc+" exists");
	}

    }
    
    public void setupWorkingDirectory(RConnection c){
        // This method is used when a request for the Zelig model definition 
        // file comes in; so only the top-level temp directory is needed, and
        // no request-specific child directories; and no temp directory for 
        // unpacking of the request results on the application side. 
        try{
            // set up the working directory:
            String checkWrkDir = "if (file_test('-d', '"+DSB_TMP_DIR+"')) {Sys.chmod('"+
            DSB_TMP_DIR+"', mode = '0777');} else {dir.create('"+DSB_TMP_DIR+"', showWarnings = FALSE, recursive = TRUE);Sys.chmod('"+
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
    
    public Map <String, String> execute (DvnRJobRequest sro) {
      
      // Step 1. Copy of Rdata file
      // Step 2. Subset Rdata file
      // Step 3. Return the subsetted Rdata file instead of the original
      
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        // temporary result
        Map<String, String> tmpResult = new HashMap<String, String>();
        
        try {
            // Set up an Rserve connection
            dbgLog.info("sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
            
            dbgLog.fine("RSERVE_USER="+RSERVE_USER+"[default=rserve]");
            dbgLog.fine("RSERVE_PWD="+RSERVE_PWD+"[default=rserve]");
            dbgLog.fine("RSERVE_PORT="+RSERVE_PORT+"[default=6311]");

            RConnection c = new RConnection(RSERVE_HOST, RSERVE_PORT);
            dbgLog.fine("hostname="+RSERVE_HOST);

            c.login(RSERVE_USER, RSERVE_PWD);
            dbgLog.info("R Version = " + c.eval("R.version$version.string").asString() + "<");
            dbgLog.info("SRO request type: "+sro.getRequestType());
            
            // check working directories
            // This needs to be done *before* we try to create any files 
            // there!
            setupWorkingDirectories(c);


            // save the data file at the Rserve side
            String infile = sro.getSubsetFileName();
            
            InputStream inb = new BufferedInputStream(new FileInputStream(infile));

            int bufsize;
            byte[] bffr = new byte[1024];

            RFileOutputStream os = 
                 c.createFile(tempFileName);
            while ((bufsize = inb.read(bffr)) != -1) {
                    os.write(bffr, 0, bufsize);
            }
            os.close();
            inb.close();
            
            // save the original data file on the server-side
            
            // WORKS STARTS HERE
            // os = c.createFile(tempOriginalFileName);
            
            // Rserve code starts here
            dbgLog.fine("DvnRserveComm: "+"wrkdir="+wrkdir);
            dbgLog.fine("DvnRserveComm: "+librarySetup);
            historyEntry.add(librarySetup);
            c.voidEval(librarySetup);
            
            
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
            historyEntry.add("vartyp<-c(" + sb.toString()+")");
*/
            
            //c.assign("vartyp", new REXPInteger(jvartyp));
            if ("Download".equals(sro.getRequestType())) {
                /*
                 * Note that we want to use the "getVariableTypesWithBoolean method 
                 * when the subset is being created for download/conversion; when 
                 * we create a SRO object for analysis, we'll still be using 
                 * the old getVariableTypes
                 * method, that don't recognize Booleans as a distinct class. 
                 * So they will be treated simply as numeric categoricals 
                 * (factors) with the "TRUE" and "FALSE" labels. But for the purposes
                 * of saving the subset in R format, we want to convert these into
                 * R "logical" vectors.
                 * 
                 * TODO: verify what's going to happen to these "logical"
                 * variables when we call R package Foreign to convert the 
                 * dataset into STATA format. -- L.A. 
                 */

                dbgLog.info("raw variable type=" + sro.getVariableTypesWithBoolean());
                c.assign("vartyp", new REXPInteger(sro.getVariableTypesWithBoolean()));
                String[] tmpt = c.eval("vartyp").asStrings();
                dbgLog.info("vartyp length=" + tmpt.length + "\t "
                        + StringUtils.join(tmpt, ","));

            } else {
                historyEntry.add("vartyp<-c(" + StringUtils.join(sro.getVariableTypesAsString(), ",") + ")");
                dbgLog.fine("DvnRserveComm: " + "vartyp<-c(" + StringUtils.join(sro.getVariableTypesAsString(), ",") + ")");

                dbgLog.fine("raw variable type=" + sro.getVariableTypes());
                c.assign("vartyp", new REXPInteger(sro.getVariableTypes()));
                String[] tmpt = c.eval("vartyp").asStrings();
                dbgLog.fine("DvnRserveComm: " + "vartyp length=" + tmpt.length + "\t "
                        + StringUtils.join(tmpt, ","));
            }
        
            // variable format (date/time)
            /* 
                varFmt<-list();
                c.voidEval("varFmt<-list()");
            */
            
            Map<String, String> tmpFmt = sro.getVariableFormats();
            dbgLog.fine("DvnRserveComm: "+"tmpFmt="+tmpFmt);
            if (tmpFmt != null){
                Set<String> vfkeys = tmpFmt.keySet();
                String[] tmpfk = (String[]) vfkeys.toArray(new String[vfkeys.size()]);
                String[] tmpfv = getValueSet(tmpFmt, tmpfk);
                historyEntry.add("tmpfk<-c(" + StringUtils.join(tmpfk,", ")+")");
                dbgLog.fine("DvnRserveComm: "+"tmpfk<-c(" + StringUtils.join(tmpfk,", ")+")");

                c.assign("tmpfk", new REXPString(tmpfk));
                historyEntry.add("tmpfv<-c(" + StringUtils.join(tmpfv,", ")+")");
                dbgLog.fine("DvnRserveComm: "+"tmpfv<-c(" + StringUtils.join(tmpfv,", ")+")");
                c.assign("tmpfv", new REXPString(tmpfv));
                String fmtNamesLine = "names(tmpfv)<- tmpfk";
                historyEntry.add(fmtNamesLine);

		dbgLog.fine("DvnRserveComm: "+fmtNamesLine); 
                c.voidEval(fmtNamesLine);

                String fmtValuesLine ="varFmt<- as.list(tmpfv)";
                historyEntry.add(fmtValuesLine);
		
		dbgLog.fine("DvnRserveComm: "+fmtValuesLine); 
                c.voidEval(fmtValuesLine);
            } else {
                String [] varFmtN ={};
                List<String> varFmtV = new ArrayList<String>();
                historyEntry.add("varFmt <- list()");

		dbgLog.fine("DvnRserveComm: "+"varFmt <- list()");
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
            //historyEntry.add("vnamnes<-c("+ StringUtils.join(jvnames, ", ")+")");
            String vnQList = DvnDSButil.joinNelementsPerLine(jvnames,true);
            historyEntry.add("vnames<-c("+ vnQList+")");
            
            
            c.assign("vnames", new REXPString(jvnames));
            
            // confirmation
            String [] tmpjvnames = c.eval("vnames").asStrings();
            dbgLog.fine("DvnRserveComm: "+"vnames:"+ StringUtils.join(tmpjvnames, ","));
            
        
            /*
                x<-read.table141vdc(file="/tmp/VDC/t.28948.1.tab", 
                    col.names=vnames, colClassesx=vartyp, varFormat=varFmt)
            */
    
            //String datafilename = "/nfs/home/A/asone/java/rcode/t.28948.1.tab";
            
            // tab-delimited file name = tempFileName
            
            // vnames = Arrays.deepToString(new REXPString(jvnames).asStrings())
            // vartyp = Arrays.deepToString(new REXPInteger(sro.getUpdatedVariableTypes()).asStrings())
            // varFmt = 
            dbgLog.info("col names ..... " + Arrays.deepToString(jvnames));
            dbgLog.info("colClassesX ... " + Arrays.toString(sro.getVariableTypesWithBoolean()));
            dbgLog.info("varFormat ..... " + tmpFmt);
            
            String readtableline = "x<-read.table141vdc(file='"+tempFileName+
                "', col.names=vnames, colClassesx=vartyp, varFormat=varFmt )";
            
            historyEntry.add(readtableline);
            dbgLog.fine("DvnRserveComm: "+"readtable="+readtableline);

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
                
                historyEntry.add("tmpRN<-c("+StringUtils.join(rawNameSet,", ")+")");
                c.assign("tmpRN", new REXPString(rawNameSet));
                historyEntry.add("tmpSN<-c("+StringUtils.join(safeNameSet,", ")+")");
                c.assign("tmpSN", new REXPString(safeNameSet));
                
                String raw2safevarNameTableLine = "names(tmpRN)<- tmpSN";
                historyEntry.add(raw2safevarNameTableLine);
                c.voidEval(raw2safevarNameTableLine);
                String attrRsafe2rawLine = "attr(x, 'Rsafe2raw')<- as.list(tmpRN)";
                historyEntry.add(attrRsafe2rawLine);
                c.voidEval(attrRsafe2rawLine);
            } else {
                String attrRsafe2rawLine = "attr(x, 'Rsafe2raw')<-list();";
                historyEntry.add(attrRsafe2rawLine);
                c.voidEval(attrRsafe2rawLine);
            }
            
            //Map<String, String> Rsafe2raw = sro.getRaw2SafeVarNameTable();
            
            // asIs
            /* 
                for (i in 1:dim(x)[2]){if (attr(x,"var.type")[i] == 0) {
                x[[i]]<-I(x[[i]]);  x[[i]][ x[[i]] == '' ]<-NA  }}
            */
            
            /* 
             * Commenting out the fragment below: 
             * - this is now being done early on in the read.table141vdc
             *   R function:
             
            String asIsline  = "for (i in 1:dim(x)[2]){ "+
                "if (attr(x,'var.type')[i] == 0) {" +
                "x[[i]]<-I(x[[i]]);  x[[i]][ x[[i]] == '' ]<-NA  }}";
            historyEntry.add(asIsline);
            c.voidEval(asIsline);
            */
            
            // replication: copy the data.frame
            String repDVN_Xdupline = "dvnData<-x";
            c.voidEval(repDVN_Xdupline);
            tmpResult.put("dvn_dataframe", "dvnData");
            // recoding line
if (sro.hasRecodedVariables()){

            // subsetting 
            
            List<String> scLst = sro.getSubsetConditions();
            if (scLst != null){
                for (String sci : scLst){
                    dbgLog.fine("sci:"+ sci);
                    historyEntry.add(sci);
                    c.voidEval(sci);
                }
                tmpResult.put("subset", "T");
            }
            // recoding
            
            List<String> rcLst = sro.getRecodeConditions();
            if (rcLst != null){
                for (String rci : rcLst){
                    dbgLog.fine("rci:"+ rci);

                    historyEntry.add(rci);
                    c.voidEval(rci);
                }
                tmpResult.put("recode", "T");
            }
}
            // subsetting (mutating the data.frame strips non-default attributes 
            // 
            // variable type must be re-attached


            if (!"Download".equals(sro.getRequestType())) {
                String varTypeNew = "vartyp<-c(" + StringUtils.join( sro.getUpdatedVariableTypesAsString(),",")+")";
                historyEntry.add(varTypeNew);
                dbgLog.fine("updated var Type ="+ sro.getUpdatedVariableTypes());
                c.assign("vartyp", new REXPInteger(sro.getUpdatedVariableTypes()));
            } else {
                dbgLog.fine("updated var Type ="+ sro.getUpdatedVariableTypesWithBoolean());
                c.assign("vartyp", new REXPInteger(sro.getUpdatedVariableTypesWithBoolean()));
            }
            
            String reattachVarTypeLine = "attr(x, 'var.type') <- vartyp";
            historyEntry.add(reattachVarTypeLine);
 
            dbgLog.fine("DvnRserveComm: "+reattachVarTypeLine);
            c.voidEval(reattachVarTypeLine);
                
            // replication: variable type
            String repDVN_vt = "attr(dvnData, 'var.type') <- vartyp";

            dbgLog.fine("DvnRserveComm: "+repDVN_vt);
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
            historyEntry.add("varnmbr <-c("+viQList +")");
            c.assign("varnmbr",new REXPString(jvarnmbr));
            
            String attrVarNmbrLine = "attr(x, 'var.nmbr')<-varnmbr";
            historyEntry.add(attrVarNmbrLine);

            dbgLog.fine("DvnRserveComm: "+attrVarNmbrLine);
            c.voidEval(attrVarNmbrLine);
            
            // confirmation
            String [] vno = c.eval("attr(x, 'var.nmbr')").asStrings();
            dbgLog.fine("varNo="+StringUtils.join(vno, ","));

            // replication: variable number
            String repDVN_vn = "attr(dvnData, 'var.nmbr') <- varnmbr";

	    dbgLog.fine("DvnRserveComm: "+repDVN_vn); 
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

            historyEntry.add("varlabels <-c("+ vlQList +")");

	    dbgLog.fine("DvnRserveComm: "+"varlabels <-c("+ vlQList +")");
            c.assign("varlabels", new REXPString(jvarlabels));
            
            String attrVarLabelsLine = "attr(x, 'var.labels')<-varlabels";
            historyEntry.add(attrVarLabelsLine);

            dbgLog.fine("DvnRserveComm: "+attrVarLabelsLine);
            c.voidEval(attrVarLabelsLine);
            
            // confirmation
            String [] vlbl = c.eval("attr(x, 'var.labels')").asStrings();
            dbgLog.fine("varlabels="+StringUtils.join(vlbl, ","));
        
            // replication: 
            String repDVN_vl = "attr(dvnData, 'var.labels') <- varlabels";

	    dbgLog.fine("DvnRserveComm: "+repDVN_vl);
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
            historyEntry.add(vtFirstLine);

            dbgLog.fine("DvnRserveComm: "+vtFirstLine);
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
                    
                    historyEntry.add("tmpk<-c("+ DvnDSButil.joinNelementsPerLine(tmpk, true) +")");
                    c.assign("tmpk", new REXPString(tmpk));
                    
                    historyEntry.add("tmpv<-c("+ DvnDSButil.joinNelementsPerLine(tmpv, true) +")");
                    c.assign("tmpv", new REXPString(tmpv));
                    
                    String namesValueLine = "names(tmpv)<- tmpk";
                    historyEntry.add(namesValueLine);
                    c.voidEval(namesValueLine);
                    
                    
                    String sbvl = "VALTABLE[['"+ Integer.toString(indx)+"']]" + "<- as.list(tmpv)";
                    dbgLog.fine("frag="+sbvl);
                    historyEntry.add(sbvl);
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
            historyEntry.add(attrValTableLine);
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
            historyEntry.add(msvStartLine);
            c.voidEval(msvStartLine);
            // data structure
            String attrMissvalLine = "attr(x, 'missval.table')<-MSVLTBL";
            historyEntry.add(attrMissvalLine);
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
            historyEntry.add(createVIndexLine);
            c.voidEval(createVIndexLine);
            String createMVIndexLine = "x<-createvalindex(dtfrm=x, attrname='missval.index');";
            historyEntry.add(createMVIndexLine);
            c.voidEval(createMVIndexLine);



// reflection block: start ------------------------------------------>
        
        
            String requestTypeToken = sro.getRequestType();// (Download|EDA|Xtab|Zelig)
            dbgLog.fine("requestTypeToken="+requestTypeToken);
            historyEntry.add("#### The Request is "+ requestTypeToken +" ####");
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
            
            dbgLog.info(String.format("R-version String = %s", Rversion));
            
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
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            result.putAll(tmpResult);
            dbgLog.fine("result object (before closing the Rserve):\n"+result);
            
        
// reflection block: end

	    // create a zip file of the directory created: 
	    
	    //String zipTmpDir = "system(\"(cd "+DSB_TMP_DIR+"; zip -r /tmp/"+requestdir+".zip "+requestdir+")\")";
            //c.voidEval(zipTmpDir);        


	    // transfer the zip file to the application side: 
	    
	    //RFileInputStream ris = null;
	    //OutputStream outbr   = null;

            //int zipSize = getFileSize(c,"/tmp/"+requestdir+".zip");

	    String listAnalysisFiles = "list.files('"+DSB_TMP_DIR+"/"+requestdir+"', recursive=TRUE)"; 
            dbgLog.fine("looking up the analysis result files on the DSB/Rserve side: "+listAnalysisFiles);
	    String[] analysisReportFiles = c.eval(listAnalysisFiles).asStrings(); 
	    RFileInputStream ris = null;
	    OutputStream outbr   = null;

	    try {
		File localReportDir = new File(TEMP_DIR+"/DVN", requestdir);
		if ( !localReportDir.exists() ) {
		    localReportDir.mkdir();
		}

		for (int i = 0; i < analysisReportFiles.length; i++) {
		    String reportFile = analysisReportFiles[i]; 
		    int reportFileSize = getFileSize(c, DSB_TMP_DIR+"/"+requestdir+"/"+reportFile);
		    dbgLog.fine("DvnRData: transferring file "+reportFile);
		    dbgLog.fine("DvnRData: file size: "+reportFileSize); 


		    if ( reportFile.lastIndexOf("/") > 0 ) {
			File localReportSubDir = new File (TEMP_DIR+"/DVN/"+requestdir, reportFile.substring(0,reportFile.lastIndexOf("/")));
			if ( !localReportSubDir.exists() ) {
			    localReportSubDir.mkdirs(); 
			}
		    }

		    ris = c.openFile(DSB_TMP_DIR+"/"+requestdir+"/"+reportFile);		    
		    outbr = new BufferedOutputStream(new FileOutputStream(new File(TEMP_DIR+"/DVN/"+requestdir, reportFile)));
		
		    byte[] obuf = new byte[reportFileSize];
		    int obufsize = 0; 

		    while ((obufsize = ris.read(obuf)) != -1) {
			outbr.write(obuf, 0, reportFileSize);
		    }
		
		    ris.close();
		    outbr.close();
		}

		//String unZipCmd = "/usr/bin/unzip "+TEMP_DIR+"/DVN/"+requestdir+".zip -d "+TEMP_DIR+"/DVN";
		//int exitValue = 1;
		
		//dbgLog.fine("attempting to execute "+unZipCmd);
		
		//try {
		//Runtime runtime = Runtime.getRuntime();
		//Process process = runtime.exec(unZipCmd);
		//exitValue = process.waitFor();
		//} catch (Exception e) {
		//e.printStackTrace();
		//exitValue = 1;
		//}
		    
		//if (exitValue == 0) {
		//result.put("webFolderArchived",TEMP_DIR+"/DVN/"+requestdir+".zip");
		//}

	    } catch (FileNotFoundException fe){
		fe.printStackTrace();
	    } catch (IOException ie){
		ie.printStackTrace();
	    } finally {
		if (ris != null){
		    ris.close();
		}
		if (outbr != null){
		    outbr.close();
		}
	    }
	    
	    // // move the temp dir to the web-temp root dir
            //String mvTmpDir = "file.rename('"+wrkdir+"','"+webwrkdir+"')";
            //dbgLog.fine("web-temp_dir="+mvTmpDir);
            //c.voidEval(mvTmpDir);        

	    
            // close the Rserve connection
            c.close();
        
        } catch (RserveException rse) {
            // RserveException (Rserve is not running)
            rse.printStackTrace();
            
            result.put("dsbContextRootDir",  DSB_CTXT_DIR );
            result.put("PID", PID);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("option",sro.getRequestType().toLowerCase());
            
            result.put("RexecError", "true");
            return result;

        } catch (REXPMismatchException mme) {
        
            // REXP mismatch exception (what we got differs from what we expected)
            mme.printStackTrace();
            
            result.put("dsbContextRootDir",  DSB_CTXT_DIR );
            result.put("PID", PID);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("option",sro.getRequestType().toLowerCase());

            result.put("RexecError", "true");
            return result;

        } catch (IOException ie){
            ie.printStackTrace();
            
            result.put("dsbContextRootDir",  DSB_CTXT_DIR );
            result.put("PID", PID);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("option",sro.getRequestType().toLowerCase());

            result.put("RexecError", "true");
            return result;
            
        } catch (Exception ex){
            ex.printStackTrace();
            
            result.put("dsbContextRootDir",  DSB_CTXT_DIR );
            result.put("PID", PID);

            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
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
    
    
    /** 
     * Handles a downloading request 
     * Prepares a ZIP archive for download
     *
     * @param sro a DVNRJobREquest object used to store information about the
     * R-request to be processed
     * @param c an RConnectionto the R Server
     * @return a String-String map containing information about the result
     */
    public Map<String, String> runDownloadRequest (DvnRJobRequest sro, RConnection c) {

    
        String optionBanner = "########### downloading option ###########\n";
        dbgLog.fine(optionBanner);
        historyEntry.add(optionBanner);
        
        Map<String, String> sr = new HashMap<String, String>();
        sr.put("requestdir", requestdir);
        sr.put("option", "download");
        
        String RcodeFile = "Rcode." + PID + ".R";
        
        try {
            // create a temp dir
            String createTmpDir = "dir.create('"+wrkdir +"')";
            dbgLog.info("DvnRserveComm: "+"createTmpDir="+createTmpDir);
            
            historyEntry.add(createTmpDir);

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
            
            dbgLog.info("univarDataDwnld="+univarDataDwnld);
            
            historyEntry.add(univarDataDwnld);

            dbgLog.info("DvnRserveComm: "+univarDataDwnld);
            c.voidEval(univarDataDwnld);
            
            int wbFileSize = getFileSize(c,dsnprfx);
            
            dbgLog.info("wbFileSize="+wbFileSize);
            
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
            dbgLog.fine("DvnRserveComm: "+"move temp file="+mvTmpTabFile);

        } catch (RserveException rse) {
            rse.printStackTrace();
            sr.put("RexecError", "true");
            return sr;
        }

        sr.put("RexecError", "false");
        return sr;
    }
    
    /** *************************************************************
     * Handles an EDA request 
     *
     * @param     
     * @return    
     */
    public Map<String, String> runEDARequest(DvnRJobRequest sro, RConnection c){
        String optionBanner = "########### EDA option ###########\n";
        dbgLog.fine(optionBanner);
        historyEntry.add(optionBanner);

        Map<String, String> sr = new HashMap<String, String>();
        sr.put("requestdir", requestdir);
        sr.put("option", "eda");
        sr.put("type", sro.getEDARequestType()); // 1=(1,0), 2=(0,1), 3=(1,1)

        String ResultHtmlFile = "Rout."+PID+".html" ;
        sr.put("html", "/"+requestdir+ "/" +ResultHtmlFile);
        
        String RcodeFile = "Rcode." + PID + ".R";
        
        try {
            String univarStatLine = "try(x<-univarStat(dtfrm=x))";

            
            
            //String vdcUtilLibLine = "library(VDCutil)";
            
            //c.voidEval("dol<-''");
            
            dbgLog.fine("aol="+ sro.getEDARequestParameter());
            String aol4EDA = "aol<-c("+ sro.getEDARequestParameter() +")";
            
            dbgLog.fine("aolLine="+ aol4EDA);
            historyEntry.add(aol4EDA);
            c.voidEval(aol4EDA);
            
            // create a manifest page for this job request
            String homePageTitle = "Dataverse Analysis: Request #"+PID ;

            // create a temp dir
            String createTmpDir = "dir.create('"+wrkdir +"')";
            dbgLog.fine("createTmpDir="+createTmpDir);
            historyEntry.add(createTmpDir);
            c.voidEval(createTmpDir);

            String ResultHtmlFileBase = "Rout."+PID ;
            String openHtml = "htmlsink<-HTMLInitFile(outdir = '"+
                wrkdir +"', filename='"+ResultHtmlFileBase+
                "', extension='html', CSSFile='R2HTML.css', Title ='"+
                homePageTitle+"')";
            dbgLog.fine("openHtml="+openHtml);
            historyEntry.add(openHtml);
            
            c.voidEval(openHtml);
            
            String StudyTitle = sro.getStudytitle();
            String pageHeaderContents = "hdrContents <- \"<h1>Dataverse Analysis</h1><h2>Results</h2><p>Study Title:"+
            StudyTitle+"</p><hr />\"";
            
            dbgLog.fine("pageHeaderContents="+pageHeaderContents);
            historyEntry.add(pageHeaderContents);
            c.voidEval(pageHeaderContents);
            
            String pageHeader = "HTML(file=htmlsink,hdrContents)";
            dbgLog.fine("pageHeader="+pageHeader);
            historyEntry.add(pageHeader);
            c.voidEval(pageHeader);
            
            // create visuals directory for an EDA case
            String createVisualsDir = "dir.create('"+ wrkdir +
                "/" + "visuals" +"')";
            dbgLog.fine("visualsDir="+createVisualsDir);
            
            historyEntry.add(createVisualsDir);
            c.voidEval(createVisualsDir);
            
            // command lines
            dbgLog.fine("univarStatLine="+univarStatLine);
            c.voidEval(univarStatLine);
            historyEntry.add(univarStatLine);
            String univarChart = "try({x<-univarChart(dtfrm=x, " +
                "analysisoptn=aol, " +
                "imgflprfx='" + wrkdir + "/" + 
                "visuals" + "/" + "Rvls." + PID +
                "',standalone=F)})";
            dbgLog.fine("univarChart="+univarChart);
            historyEntry.add(univarChart);
            c.voidEval(univarChart);
            
            String univarStatHtmlBody = "try(univarStatHtmlBody(dtfrm=x,"+
            "whtml=htmlsink, analysisoptn=aol))";
            dbgLog.fine("univarStatHtmlBody="+univarStatHtmlBody);
            historyEntry.add(univarStatHtmlBody);
            c.voidEval(univarStatHtmlBody);
            
            String closeHtml = "HTMLEndFile()";
            historyEntry.add(closeHtml);
            c.voidEval(closeHtml);
            
            // save workspace as a replication data set
//            
//            String RdataFileName = "DVNdataFrame."+PID+".RData";
//            sr.put("Rdata", "/"+requestdir+ "/" + RdataFileName);
//            
//            String saveWS = "save('dvnData', file='"+ wrkdir +"/"+ RdataFileName +"')";
//            dbgLog.fine("save the workspace="+saveWS);
//            c.voidEval(saveWS);
//            
//            // write back the R workspace to the dvn 
//            
//            String wrkspFileName = wrkdir +"/"+ RdataFileName;
//            dbgLog.fine("wrkspFileName="+wrkspFileName);
//            
//            int wrkspflSize = getFileSize(c,wrkspFileName);
//            
//            File wsfl = writeBackFileToDvn(c, wrkspFileName, RWRKSP_FILE_PREFIX,"RData", wrkspflSize);
//            
//            if (wsfl != null){
//                sr.put("wrkspFileName", wsfl.getAbsolutePath());
//                dbgLog.fine("wrkspFileName="+wsfl.getAbsolutePath());
//            } else {
//                dbgLog.fine("wrkspFileName is null");
//            }
            // copy the dvn-patch css file to the wkdir
            // file.copy(from, to, overwrite = FALSE)
            String cssFile ="paste(.libPaths(), '/../share/dvn/R2HTML.css', sep='')";
            String cpCssFile = "file.copy("+cssFile+",'"+wrkdir+"')";
            c.voidEval(cpCssFile);
            
            // tab data file
            String mvTmpTabFile = "file.rename('"+ tempFileName +"','"+ tempFileNameNew +"')";
            c.voidEval(mvTmpTabFile);
            dbgLog.fine("move temp file="+mvTmpTabFile);
            

        } catch (RserveException rse) {
            rse.printStackTrace();
            sr.put("RexecError", "true");
            return sr;
        }
        
        sr.put("RexecError", "false");
        return sr;
    }
    
    /** *************************************************************
     * Handles an Xtab request 
     *
     * @param     
     * @return    
     */
    public Map<String, String> runXtabRequest(DvnRJobRequest sro, RConnection c){
        String optionBanner = "########### xtab option ###########\n";
        dbgLog.fine(optionBanner);
        historyEntry.add(optionBanner);
        
        Map<String, String> sr = new HashMap<String, String>();
        sr.put("requestdir", requestdir);
        sr.put("option", "xtab");

        String ResultHtmlFile = "Rout."+PID+".html" ;
        sr.put("html", "/"+requestdir+ "/" +ResultHtmlFile);

        String RcodeFile = "Rcode." + PID + ".R";

        try {
            String univarStatLine = "try(x<-univarStat(dtfrm=x))";
            String vdcUtilLibLine = "library(VDCutil)";
        
            //c.voidEval("dol<-''");
            
            String aol4xtab = "aol<-c(0,0,1)";
            historyEntry.add(aol4xtab);
            c.voidEval(aol4xtab);
            
            // create a manifest page for this job request
            String homePageTitle = "Dataverse Analysis: Request #"+PID ;

            // create a temp dir
            String createTmpDir = "dir.create('"+wrkdir +"')";
            dbgLog.fine("createTmpDir="+createTmpDir);
            historyEntry.add(createTmpDir);
            c.voidEval(createTmpDir);            
            
            String ResultHtmlFileBase = "Rout."+PID ;
            String openHtml = "htmlsink<-HTMLInitFile(outdir = '"+
                wrkdir +"', filename='"+ResultHtmlFileBase+
                "', extension='html', CSSFile='R2HTML.css', Title ='"+
                homePageTitle+"')";
            dbgLog.fine("openHtml="+openHtml);
                historyEntry.add(openHtml);
            c.voidEval(openHtml);
            
            String StudyTitle = sro.getStudytitle();
            String pageHeaderContents = "hdrContents <- \"<h1>Dataverse Analysis</h1><h2>Results</h2><p>Study Title:"+
            StudyTitle+"</p><hr />\"";
            
            dbgLog.fine("pageHeaderContents="+pageHeaderContents);
            historyEntry.add(pageHeaderContents);
            c.voidEval(pageHeaderContents);
            
            String pageHeader = "HTML(file=htmlsink,hdrContents)";
            dbgLog.fine("pageHeader="+pageHeader);
            historyEntry.add(pageHeader);
            c.voidEval(pageHeader);
            
          /*
            aol<-c(0,0,1)
            dol<-""
            try(x<-univarStat(dtfrm=x))
            library(VDCutil);
            try(VDCcrossTabulation(HTMLfile="/tmp/VDC/Rout.6769.html",
            data=x,
            classificationVars=c('NAThex5FDISChex5Fnew','LOChex5FINThex5Fnew'),
             wantPercentages=F, wantTotals=T, wantStats=T, wantExtraTables=T));
           */
            
            // command lines
            dbgLog.fine("univarStatLine="+univarStatLine);
            historyEntry.add(univarStatLine);
            c.voidEval(univarStatLine);
            
            historyEntry.add(vdcUtilLibLine);
            c.voidEval(vdcUtilLibLine);
            
            String[] classVar =  sro.getXtabClassVars();
            String classVarSeg =  DvnDSButil.joinNelementsPerLine(classVar, true);
            historyEntry.add("classVar<-c("+classVarSeg+")");
            c.assign("classVar", new REXPString(classVar));
            sr.put("class_var_set",classVarSeg );
            String[] freqVar = sro.getXtabFreqVars();
            
            if (freqVar != null){
                String freqVarSeg = DvnDSButil.joinNelementsPerLine(freqVar, true);
                sr.put("freq_var", freqVarSeg);
                historyEntry.add("freqVar<-c("+ freqVarSeg +")");
                
                c.assign("freqVar", new REXPString(freqVar));
            }else {
                historyEntry.add("freqVar<-c()");
                sr.put("freq_var", "");

                c.voidEval("freqVar<-c()");
            }
            
            String[] xtabOptns= sro.getXtabOutputOptions();// {"F", "T", "T", "T"};
            
            //"HTMLfile='"+wrkdir+ "/" +ResultHtmlFile+"'"+

            String VDCxtab = "try(VDCcrossTabulation("+
                "HTMLfile=htmlsink"+
                ", data=x"+ 
                ", classificationVars=classVar"+
                ", freqVars=freqVar" +
                ", wantPercentages="+xtabOptns[2]+
                ", wantTotals="+xtabOptns[0]+
                ", wantStats="+xtabOptns[1]+
                ", wantExtraTables="+xtabOptns[3]+"))";
                
            sr.put("wantPercentages",xtabOptns[2]);
            sr.put("wantTotals",xtabOptns[0]);
            sr.put("wantStats",xtabOptns[1]);
            sr.put("wantExtraTables",xtabOptns[3]);
                
            dbgLog.fine("VDCxtab="+VDCxtab);
            historyEntry.add(VDCxtab);
            c.voidEval(VDCxtab);

            String closeHtml = "HTMLEndFile()";
            historyEntry.add(closeHtml);
            c.voidEval(closeHtml);
            
            // save workspace as a replication data set
//            
//            String RdataFileName = "DVNdataFrame."+PID+".RData";
//            sr.put("Rdata", "/"+requestdir+ "/" + RdataFileName);
//            
//            String saveWS = "save('dvnData', file='"+ wrkdir +"/"+ RdataFileName +"')";
//            dbgLog.fine("save the workspace="+saveWS);
//            c.voidEval(saveWS);
//            
//            // write back the R workspace to the dvn 
//            
//            String wrkspFileName = wrkdir +"/"+ RdataFileName;
//            dbgLog.fine("wrkspFileName="+wrkspFileName);
//            
//            int wrkspflSize = getFileSize(c,wrkspFileName);
//            
//            File wsfl = writeBackFileToDvn(c, wrkspFileName, RWRKSP_FILE_PREFIX,"RData", wrkspflSize);
//            
//            if (wsfl != null){
//                sr.put("wrkspFileName", wsfl.getAbsolutePath());
//                dbgLog.fine("wrkspFileName="+wsfl.getAbsolutePath());
//            } else {
//                dbgLog.fine("wrkspFileName is null");
//            }
            
            // copy the dvn-patch css file to the wkdir
            // file.copy(from, to, overwrite = FALSE)
            String cssFile ="paste(.libPaths(), '/../share/dvn/R2HTML.css', sep='')";
            String cpCssFile = "file.copy("+cssFile+",'"+wrkdir+"')";

            c.voidEval(cpCssFile);

            // tab data file
            String mvTmpTabFile = "file.rename('"+ tempFileName +"','"+ tempFileNameNew +"')";
            c.voidEval(mvTmpTabFile);
            dbgLog.fine("move temp file="+mvTmpTabFile);
            

        } catch (RserveException rse) {
            rse.printStackTrace();
            sr.put("RexecError", "true");
            return sr;
        }
        
        sr.put("RexecError", "false");

        return sr;
    }
    
    /** *************************************************************
     * Handles a Zelig request 
     *
     * @param     
     * @return    
     */
    public Map<String, String> runZeligRequest(DvnRJobRequest sro, RConnection c){
        String optionBanner = "########### zelig option ###########\n";
        dbgLog.fine(optionBanner);
        historyEntry.add(optionBanner);
        
        Map<String, String> sr = new HashMap<String, String>();
        sr.put("requestdir", requestdir);
        sr.put("option", "zelig");
        
        String RcodeFile = "Rcode." + PID + ".R";

        
        String modelname = sro.getZeligModelName();
        dbgLog.fine("modelname="+modelname);
        sr.put("model", modelname);

        try {
        
            String univarStatLine = "try(x<-univarStat(dtfrm=x))";
            String ResultHtmlFile = null; //"Rout."+PID+".html" ;
            String RdataFileName  = null;
            String vdcUtilLibLine = "library(VDCutil)";

            //c.voidEval("dol<-''");
            String aol4Zelig = "aol<-c(0,0,0)";
            historyEntry.add(aol4Zelig);
            c.voidEval(aol4Zelig);
            
            historyEntry.add(vdcUtilLibLine);
            c.voidEval(vdcUtilLibLine);

            /* 
                ########## Code listing ##########
                library(VDCutil)
                ########## Requested option = logit ##########
                bnryVarTbl <-attr(table(x[[3]]), 'dimnames')[[1]];
                
                x[[3]]<- checkBinaryResponse(x[[3]])
                
                try( {zlg.out<- VDCgenAnalysis(
                outDir="/tmp/VDC/DSB/zlg_28948/rqst_1",
                vote ~ age+race,
                model="logit",
                data=x,
                wantSummary=T,wantPlots=T,wantSensitivity=F,wantSim=F,wantBinOutput=F,
                setxArgs=list(), setx2Args=list(),
                HTMLInitArgs=list(Title="Dataverse Analysis")
                , HTMLnote= "<em>The following are the results of your requested analysis.
                </em><br/><a href='javascript:window.history.go(-1);
                '>Go back to the previous page</a>")} )

                ########## Code listing: end ##########

            */
            
// Additional coding required here
            /*
                x[[3]]<- checkBinaryResponse(x[[3]])
             * 3 must be parameterized for binary response models
            */
            if (sro.isOutcomeBinary()){
                int bcol = sro.getOutcomeVarPosition();
                dbgLog.fine("col:pos="+bcol);
                String binaryResponseVar = null;
                
                if (sro.IsOutcomeVarRecoded) {
                    String col = sro.getRecodedVarNameSet()[bcol];
                    binaryResponseVar = "x[['" +col +"']]";
                    dbgLog.fine("col:name="+col);
                } else {
                    binaryResponseVar = "x[[" + (bcol+1) +"]]";
                    dbgLog.fine("col:pos(1 added)="+bcol);
                }
                
                String checkBinaryVarLine=  binaryResponseVar +
                    "<- checkBinaryResponse("+binaryResponseVar+")";
                dbgLog.fine(checkBinaryVarLine);
                historyEntry.add(checkBinaryVarLine);
                c.voidEval(checkBinaryVarLine);
            }
            
            Map<String, String> ZlgModelParam = new HashMap<String, String>();
            
            // create a temp dir
            String createTmpDir = "dir.create('"+wrkdir +"')";
            dbgLog.fine("createTmpDir="+createTmpDir);
            historyEntry.add(createTmpDir);
            c.voidEval(createTmpDir);
            
            // output option: sum plot Rdata
            String[] zeligOptns =  sro.getZeligOutputOptions();
            
            String simOptn = sro.getZeligSimulationOption();
            String setxType=  null;
            String setxArgs = null;
            String setx2Args = null;
            
            if (simOptn.equals("T")){
                // simulation was requested
                setxType= sro.getZeligSetxType();
                dbgLog.fine("setxType="+setxType);
                if (setxType == null){
                    setxArgs = "NULL";
                    setx2Args= "NULL";
                } else {
                    if (setxType.equals("0")) {
                        // default case
                        setxArgs = "NULL";
                        setx2Args= "NULL";
                    } else if (setxType.equals("1")){
                        // non-default cases
                        if ( (sro.getSetx1stSet() != null) && (sro.getSetx2ndSet() != null)){
                            // first diff case
                            setxArgs = sro.getSetx1stSet();
                            setx2Args= sro.getSetx2ndSet();
                            
                            sr.put("zelig_sim_1",sro.getSetx1stSet4rep());
                            sr.put("zelig_sim_2",sro.getSetx2ndSet4rep());
                            
                        } else if (sro.getSetx2ndSet() == null){
                            // single condition
                            setxArgs = sro.getSetx1stSet();
                            setx2Args= "NULL";
                            sr.put("zelig_sim_1",sro.getSetx1stSet4rep());
                        } else {
                            setxArgs = "NULL";
                            setx2Args= "NULL";
                        }
                        
                    }
                }
            }else {
                setxArgs = "NULL";
                setx2Args= "NULL";
            }

// Additional coding required here
            
            String lhsTerm = sro.getLHSformula();
            String rhsTerm = sro.getRHSformula();
            
            String VDCgenAnalysisline = 
                "try( { zlg.out<- VDCgenAnalysis(" +
                "outDir='"        + wrkdir        +"',"+
                lhsTerm + "~"     + rhsTerm       + ","+
                "model='"         + modelname     +"',"+
                "data=x,"         +
                "wantSummary="    + zeligOptns[0] + ","+
                "wantPlots="      + zeligOptns[1] + ","+
                "wantSensitivity=F"               + ","+
                "wantSim="        + simOptn       + ","+
                "wantBinOutput="  + zeligOptns[2] + ","+
                "setxArgs="       + setxArgs      + ","+
                "setx2Args="      + setx2Args     + ","+
                "HTMLInitArgs= list(Title='Dataverse Analysis')"+ "," +
                "HTMLnote= '<em>The following are the results of your requested analysis.</em>'"+
                ")  } )";
            
            String lhsTerm4rep = sro.getLHSformula4rep();
            sr.put("zelig_formula", lhsTerm4rep + " ~ "+ rhsTerm );

                
            /*
                o: sum(T), Plot(T), BinOut(F)
                a: sim (F), sen(always F)
            
               "TMLInitArgs=list(Title='Dataverse Analysis'),"+
                "HTMLnote= '<em>The following are the results of your
                requested analysis.</em><br/>
                <a href=\"javascript:window.history.go(-1);
                \">Go back to the previous page</a>'"+
            
            */
            dbgLog.fine("VDCgenAnalysis="+VDCgenAnalysisline);
            historyEntry.add(VDCgenAnalysisline);
            
            c.voidEval(VDCgenAnalysisline);
            
            RList zlgout = c.parseAndEval("zlg.out").asList();
            //String strzo = c.parseAndEval("str(zlg.out)").asString();
            
            String[] kz = zlgout.keys();
            dbgLog.fine("zlg.out:no of keys="+kz.length );// "\t"+kz[0]+"\t"+kz[1]
            
            for (int i=0; i<kz.length; i++){
                if (!zlgout.at(kz[i]).isNull()){
                    String [] tmp = zlgout.at(kz[i]).asString().split("/");

                    dbgLog.fine(kz[i]+"="+tmp[tmp.length-1]);
                    if (kz[i].equals("html")){
                       ResultHtmlFile = tmp[tmp.length-1];
                       sr.put("html", "/"+requestdir+ "/" +ResultHtmlFile);
                    } else if (kz[i].equals("Rdata")){
                       // this workspace is no longer saved
                       //RdataFileName = tmp[tmp.length-1];
                       //sr.put("Rdata", "/"+requestdir+ "/" + RdataFileName);
                    }
                } else {
                    if (kz[i].equals("html")){
                        
                    } else if (kz[i].equals("Rdata")){
                      
                    }
                }
            }
            /*
                outDir = '/tmp/VDC/DSB/Zlg_562491' <= wrkdir
                html   = index1214813662.23516A14671A1080869822.html
                Rdata  = binfile1214813662.23516A14671A1080869822.Rdata
            sr.put("html", "/"+requestdir+ "/" +ResultHtmlFile);
            sr.put("Rdata", "/"+requestdir+ "/" + RdataFileName);                
            */

            // tab data file
            String mvTmpTabFile = "file.rename('"+ tempFileName +"','"+ tempFileNameNew +"')";
            c.voidEval(mvTmpTabFile);
            dbgLog.fine("move temp file="+mvTmpTabFile);


                        
        } catch (REngineException ree){
            ree.printStackTrace();
            sr.put("RexecError", "true");
            return sr;
        } catch (RserveException rse) {
            rse.printStackTrace();
            sr.put("RexecError", "true");
            return sr;
        } catch (REXPMismatchException mme) {
            mme.printStackTrace();
            sr.put("RexecError", "true");
            return sr;
        }
        sr.put("RexecError", "false");

        return sr;
    }
    
    
    /** *************************************************************
     * returns zelig-configuration data as an XML string
     *
     * @return    XML string
     */
    public String getGUIconfigData(){
        String zlgcnfg=null;
        try {
            // get connected to Rserve
            //String RSERVE_HOST =  "vdc-build.hmdc.harvard.edu";//"140.247.115.232"
            //int RSERVE_PORT = 6311;
            RConnection c = new RConnection(RSERVE_HOST, RSERVE_PORT);
            
            // login(String user, String pwd)
            //String RSERVE_USER = "rserve";
            //String RSERVE_PWD  = "rserve";
            c.login(RSERVE_USER, RSERVE_PWD);

            // get the hostname of the caller
            String hostname = null;
            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                if (envName.equals("HOST")){
                    hostname = env.get(envName);
                }
            }
            dbgLog.fine("hostname="+hostname);
            
            setupWorkingDirectory(c);
            // set-up R command lines
            // temp file
            String R_TMP_DIR = "/tmp/VDC/";
            // Why is this directory hard-coded here? Wasn't the point of 
            // running setupWorkingDirectory, in the previous step, to set 
            // up the configured directories? -- L.A. 
            //String cnfgfl = R_TMP_DIR +"configZeligGUI."+RandomStringUtils.randomNumeric(6)+ ".xml"; 
            String cnfgfl = R_TMP_DIR +"configZeligGUI.xml"; 
            dbgLog.fine("cnfgfl="+cnfgfl);
            
            // remove the existing config file if exists
            String removeOLdLine = "if (file.exists('"+cnfgfl+"')){file.remove('"+cnfgfl+"');}";
            c.voidEval(removeOLdLine);
            // R code line part 1         
            String cmndline = "library(VDCutil);  printZeligSchemaInstance('"+ cnfgfl + "')";
            // cnfgfl  +  "'," + "'" + RSERVE_HOST + "')";
            dbgLog.fine("comand line="+cmndline);
            
            // write zelig GUI-config data  to the temp ifle
            c.voidEval(cmndline);
            //dbgLog.fine("commandline output="+outString);
            
            // check whether the temp file exists (Rserve is local)
            long filelength=0;
            try {
                if (RSERVE_HOST.equals("localhost") || RSERVE_HOST.equals(hostname)){
                    File flnm =new File(cnfgfl);
                    boolean exists = flnm.exists();
                    if (exists) {
                        dbgLog.fine("configuration xml file ("+cnfgfl+") was found");
                        filelength = flnm.length();
                        // Get the number of bytes in the file
                        dbgLog.fine("The size of the file ="+filelength);
                    } else {
                        dbgLog.fine("configuration xml file ("+cnfgfl+") was not found");
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            //if (filelength > 0){
                // read-back the config file
                c.voidEval("zz<-file('"+cnfgfl+"', 'r')");
                c.voidEval("open(zz)");
                zlgcnfg = c.eval("paste(readLines(zz),collapse='')").asString();
                dbgLog.fine("string length="+zlgcnfg.length());
                if (zlgcnfg.length()>0){
                    dbgLog.fine("first 20 bytes=[\n"+zlgcnfg.substring(0, 19) +"\n]\n");
                }
            //}
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return zlgcnfg;
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
