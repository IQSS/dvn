/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb.impl;

import edu.harvard.hmdc.vdcnet.dsb.*;
import java.io.*;
import static java.lang.System.*;
import java.util.*;
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

public class DvnRDataAnalysisServiceImpl implements DataAnalysisService {

    // ----------------------------------------------------- static filelds
    
    static String DSB_TMP_DIR=null;
    static String WEB_TMP_DIR=null;
    static String TMP_DATA_FILE_NAME = "susetfile4Rjob";
    static String TMP_DATA_FILE_EXT =".tab";
    static String RSERVE_HOST = null;
    static String RSERVE_USER = "rserve";
    static String RSERVE_PWD = "rserve";    
    static int RSERVE_PORT = 6311;
    static String DSB_HOST_PORT= null;
    static String DSB_CTXT_DIR =null;
    static Map<String, String> dwlndParam = new HashMap<String, String>();
    static Map<String, Method> runMethods = new HashMap<String, Method>();
    static String regexForRunMethods = "^run(\\w+)Request$" ;
    
    static {
    
        DSB_TMP_DIR = System.getProperty("vdc.dsb.temp.dir");
        
        // fallout case: last resort
        if (DSB_TMP_DIR == null){
            DSB_TMP_DIR="/tmp/VDC/DSB";
            WEB_TMP_DIR="/tmp/VDC/webtemp";
        }
        
        RSERVE_HOST = System.getProperty("vdc.dsb.host");
        if (RSERVE_HOST == null){
            RSERVE_HOST= "dsb-2.hmdc.harvard.edu";
            //RSERVE_HOST= "vdc-build.hmdc.harvard.edu";
        }
        
        DSB_HOST_PORT = System.getProperty("vdc.dsb.port");
        if (DSB_HOST_PORT == null){
            DSB_HOST_PORT= "80";
        }
        
        DSB_CTXT_DIR = System.getProperty("vdc.dsb.webtempdir");
        if (DSB_CTXT_DIR == null) {
            DSB_CTXT_DIR = "/temp";
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
    
    static String VDC_R_STARTUP = "/usr/local/VDC/R/library/vdc_startup.R";
    static String librarySetup= "source('"+ VDC_R_STARTUP + "');";
    boolean DEBUG = true;
    
    // ----------------------------------------------------- instance filelds
    public String PID = null;
    public String tempFileName = null;
    public String wrkdir = null;
    public String webwrkdir = null;
    public String requestdir = null;

    
    // ----------------------------------------------------- constructor
    public DvnRDataAnalysisServiceImpl(){
        
        // initialization
        PID = RandomStringUtils.randomNumeric(6);
        tempFileName = DSB_TMP_DIR + "/" + TMP_DATA_FILE_NAME
                 +"." + PID + TMP_DATA_FILE_EXT;
        requestdir = "Zlg_" + PID;
        wrkdir = DSB_TMP_DIR + "/" +"Zlg_"+PID;
        webwrkdir = WEB_TMP_DIR + "/" +"Zlg_"+PID;
    }

    /**
     * Execute an R-based dvn statistical analysis request 
     *
     * @param sro    a ServiceRequest object that contains various parameters
     * @return    a Map that contains various information about results
     */    
    
    public Map<String, String> execute(ServiceRequest sro) {
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        
        try {
            // Set up an Rserve connection
            out.println("sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
            RConnection c = new RConnection(RSERVE_HOST, RSERVE_PORT);
            out.println("hostname="+RSERVE_HOST);

            c.login(RSERVE_USER, RSERVE_PWD);
            out.println(">" + c.eval("R.version$version.string").asString() + "<");


            // save the data file at the Rserve side
            String infile = sro.getSubsetFileName();
            InputStream inb = new BufferedInputStream(
                    new FileInputStream(infile));

            int bufsize;
            byte[] bffr = new byte[8192];

            RFileOutputStream os = 
                 c.createFile(tempFileName);
            while ((bufsize = inb.read(bffr)) != -1) {
                    os.write(bffr, 0, bufsize);
            }
            os.close();

            // Rserve code starts here
            out.println("wrkdir="+wrkdir);
            c.voidEval(librarySetup);
            
            // variable type
            /* 
                vartyp <-c(1,1,1)
            */
            // java side
            // int [] jvartyp  = {1,1,1};// = mp.get("vartyp").toArray()
            int [] jvartyp  = sro.getVariableTypes();
            
            // R side
            c.assign("vartyp", new REXPInteger(jvartyp));
            String [] tmpt = c.eval("vartyp").asStrings();
            out.println("vartyp length="+ tmpt.length + "\t " +
                StringUtils.join(tmpt,","));
        
            // variable format (date/time)
            /* 
                varFmt<-list();
                c.voidEval("varFmt<-list()");
            */
            
            Map<String, String> tmpFmt = sro.getVariableFormats();
            if (tmpFmt != null){
                Set<String> vfkeys = tmpFmt.keySet();
                String[] tmpfk = (String[]) vfkeys.toArray(new String[vfkeys.size()]);
                String[] tmpfv = getValueSet(tmpFmt, tmpfk);

                c.assign("tmpfk", new REXPString(tmpfk));
                c.assign("tmpfv", new REXPString(tmpfv));
                c.voidEval("names(tmpfv)<- tmpfk");
                c.voidEval("varFmt<- as.list(tmpfv)");
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
            VariableNameFilterForR nf = new VariableNameFilterForR(jvnamesRaw);
            if (nf.hasRenamedVariables()){
                // create  list
                jvnames = nf.getFilteredVarNames();
            } else {
                jvnames = jvnamesRaw;
            }
            
            c.assign("vnames", new REXPString(jvnames));
            
            // confirmation
            String [] tmpjvnames = c.eval("vnames").asStrings();
            out.println("vnames:"+ StringUtils.join(tmpjvnames, ","));
            
        
            /*
                x<-read.table141vdc(file="/tmp/VDC/t.28948.1.tab", 
                    col.names=vnames, colClassesx=vartyp, varFormat=varFmt)
            */
    
            //String datafilename = "/nfs/home/A/asone/java/rcode/t.28948.1.tab";
            
            // tab-delimited file name = tempFileName
            String readtableline = "x<-read.table141vdc(file='"+tempFileName+
                "', col.names=vnames, colClassesx=vartyp, varFormat=varFmt )";
            
            out.println("readtable="+readtableline);
            c.voidEval(readtableline);
        
            // safe-to-raw variable name
            /* 
              attr(x, "Rsafe2raw")<-list();
            */
            if (nf.hasRenamedVariables()){
                // create  list
                jvnames = nf.getFilteredVarNames();
                String[] rawNameSet  = nf.getRenamedVariableArray();
                String[] safeNameSet = nf.getRenamedResultArray();
                
                c.assign("tmpRN", new REXPString(rawNameSet));
                c.assign("tmpSN", new REXPString(safeNameSet));
                c.voidEval("names(tmpRN)<- tmpSN");
                c.voidEval("attr(x, 'Rsafe2raw')<- as.list(tmpRN)");
            } else {
                String RsafeNameLine = "attr(x, 'Rsafe2raw')<-list();";
                c.voidEval(RsafeNameLine);
            }
            
            Map<String, String> Rsafe2raw = sro.getRaw2SafeVarNameTable();
            // TO DO 
            // list hadnlding process comes here
            //c.assign("attr(x, 'Rsafe2raw')", new REXPList(new RList(Rsafe2rawV, Rsafe2rawN)));
            
            // asIs
            /* 
                for (i in 1:dim(x)[2]){if (attr(x,"var.type")[i] == 0) {
                x[[i]]<-I(x[[i]]);  x[[i]][ x[[i]] == '' ]<-NA  }}
            */
            String asIsline  = "for (i in 1:dim(x)[2]){ "+
                "if (attr(x,'var.type')[i] == 0) {" +
                "x[[i]]<-I(x[[i]]);  x[[i]][ x[[i]] == '' ]<-NA  }}";
            c.voidEval(asIsline);
            
            // variable Id
            /* 
                attr(x, "var.nmbr")<-c("v198057","v198059","v198060")
            */
            
            // String[] jvarnmbr = {"v198057","v198059","v198060"};
            String[] jvarnmbr = sro.getVariableIDs();
            c.assign("varnmbr",new REXPString(jvarnmbr));
            c.voidEval("attr(x, 'var.nmbr')<-varnmbr");
            
            // confrimation
            String [] vno = c.eval("attr(x, 'var.nmbr')").asStrings();
            out.println("varNo="+StringUtils.join(vno, ","));

            
            // variable labels
            /* 
                attr(x, "var.labels")<-c("race","age","vote")
            */
            
            // String[] jvarlabels = {"race","age","vote"};
            String[] jvarlabels = sro.getVariableLabels();
            c.assign("varlabels", new REXPString(jvarlabels));
            c.voidEval("attr(x, 'var.labels')<-varlabels");
            
            // confirmation
            String [] vlbl = c.eval("attr(x, 'var.labels')").asStrings();
            out.println("varlabels="+StringUtils.join(vlbl, ","));
        
        
        
// --------- block to be used for the production code
            // value-label table
            /* 
                VALTABLE<-list()
                VALTABLE[["1"]]<-list(
                "2"="white",
                "1"="others")
                attr(x, 'val.table')<-VALTABLE
            */

            // create the VALTABLE
            c.voidEval("VALTABLE<-list()");
            Map<String, Map<String, String>> vltbl = sro.getValueTable();

            for (int j=0;j<jvnames.length;j++){
                // if this variable has its value-label table,
                // pass its key and value arrays to the Rserve
                // and finalize a value-table at the Rserve
                if (vltbl.containsKey(jvnames[j])){
                    Map<String, String> tmp = (HashMap<String, String>)vltbl.get(jvnames[j]);
                    Set<String> vlkeys = tmp.keySet();
                    String[] tmpk = (String[]) vlkeys.toArray(new String[vlkeys.size()]);
                    String[] tmpv = getValueSet(tmp, tmpk);

                    // debug
                    out.println("tmp:k="+ StringUtils.join(tmpk,","));
                    out.println("tmp:v="+ StringUtils.join(tmpv,","));
                    
                    c.assign("tmpk", new REXPString(tmpk));
                    c.assign("tmpv", new REXPString(tmpv));
                    c.voidEval("names(tmpv)<- tmpk");
                    
                    // index number starts from 1(not 0)
                    int indx = j +1;
                    out.println("index="+indx);
                    
                    String sbvl = "VALTABLE[['"+ Integer.toString(indx)+"']]";
                    out.println("frag="+sbvl);
                    c.voidEval(sbvl + "<- as.list(tmpv)");
                    
                    // confirmation test for j-th variable name
                    REXP jl = c.parseAndEval(sbvl);
                    System.out.println("jl("+j+") = "+jl);
                }
            }
            
            // debug: confirmation test for value-table
            out.println("length of vl="+ c.eval("length(VALTABLE)").asInteger());
            
            c.voidEval("attr(x, 'val.table')<-VALTABLE");
            
// --------- block to be used for the production code

            
            // missing-value list: TO DO
            /*
                MSVLTBL<-list(); attr(x, 'missval.table')<-MSVLTBL
            */
            
            c.voidEval("MSVLTBL<-list();");
            // data structure
            
            c.voidEval("attr(x, 'missval.table')<-MSVLTBL");
            
            // attach attributes(tables) to the data.frame
            /*
                x<-createvalindex(dtfrm=x, attrname='val.index')
                x<-createvalindex(dtfrm=x, attrname='missval.index')
            */
            c.voidEval("x<-createvalindex(dtfrm=x, attrname='val.index');");
            c.voidEval("x<-createvalindex(dtfrm=x, attrname='missval.index');");

// reflection block: start ------------------------------------------>
        
        
            String requestTypeToken = sro.getRequestType();// (Download|EDA|Xtab|Zelig)
            out.println("requestTypeToken="+requestTypeToken);
            
            // get a test method
            Method mthd = runMethods.get(requestTypeToken);
            out.println("method="+mthd);
            
            try {
                // invoke this method
                result = (Map<String, String>) mthd.invoke(this, sro, c);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                err.format(cause.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            
            // add the variable list
            result.put("variableList",StringUtils.join(jvnamesRaw, ","));

            // calculate the file-leve UNF
            
            String fileUNFline = "fileUNF <- paste(summary(unf(x, version=3)))";
            c.voidEval(fileUNFline);
            String fileUNF = c.eval("fileUNF").asString();
            if (fileUNF == null){
                fileUNF = "NA";
            }
            String RversionLine = "R.Version()$version.string";
            String Rversion = c.eval(RversionLine).asString();
            
            String RexecDate = c.eval("date()").asString();
            
            result.put("fileUNF",fileUNF);
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("dsbContextRootDir",  DSB_CTXT_DIR );
            result.put("PID", PID);
            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            out.println("result:\n"+result);
            
        
// reflection block: end
        
            // close the Rserve connection
            c.close();
        
        } catch (RserveException rse) {
            // RserveException (Rserve is not running)
            rse.printStackTrace();
        } catch (REXPMismatchException mme) {
            // REXP mismatch exception (what we got differs from what we expected)
            mme.printStackTrace();
        } catch (IOException ie){
            ie.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
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
     *
     * @param     
     * @return    
     */
    public Map<String, String> runDownloadRequest(ServiceRequest sro, RConnection c){
        out.println("*********** downloading option ***********");
        Map<String, String> sr = new HashMap<String, String>();
        
        try {
            // create a temp dir
            String createTmpDir = "dir.create('"+wrkdir +"')";
            out.println("createTmpDir="+createTmpDir);
            c.voidEval(createTmpDir);
            String dwnldOpt = sro.getDownloadRequestParameter();
            String dwnldFlxt = dwlndParam.get(dwnldOpt);
            String dataFileName = "Data." + PID + "." + dwnldFlxt;
            String dsnprfx = wrkdir + "/" + dataFileName;
            String univarDataDwnld = "univarDataDwnld(dtfrm=x,"+
                "dwnldoptn='"+dwnldOpt+"'"+
                ", dsnprfx='"+dsnprfx+"')";

            out.println("univarDataDwnld="+univarDataDwnld);
            c.voidEval(univarDataDwnld);

            // save workspace
            String RdataFileName = "Rworkspace."+PID+".RData";
            String saveWS = "save('x', file='"+wrkdir +"/"+ RdataFileName +"')";
            out.println("save the workspace="+saveWS);
            c.voidEval(saveWS);

            // move the temp dir to the web-temp root dir
            String mvTmpDir = "file.rename('"+wrkdir+"','"+webwrkdir+"')";
            out.println("web-temp_dir="+mvTmpDir);
            c.voidEval(mvTmpDir);
        
            sr.put("requestdir", requestdir);
            sr.put("option", "download");
            sr.put("format", dwnldFlxt);
            sr.put("subsetfile", "/"+requestdir+ "/" +dataFileName);
            sr.put("Rdata", "/"+requestdir+ "/" + RdataFileName);

        } catch (RserveException rse) {
            rse.printStackTrace();
        }
        
        return sr;
    }
    
    /**
     * Handles an EDA request 
     *
     * @param     
     * @return    
     */
    public Map<String, String> runEDARequest(ServiceRequest sro, RConnection c){
        out.println("*********** EDA option ***********");
        Map<String, String> sr = new HashMap<String, String>();
        try {
            String univarStat = "try(x<-univarStat(dtfrm=x))";
            String ResultHtmlFile = "Rout."+PID+".html" ;
            String vdcUtilLib = "library(VDCutil)";

            c.voidEval("dol<-''");
            String aol4EDA = "aol<-c(1,1,0)";
            c.voidEval(aol4EDA);
            
            // create a manifest page for this job request
            String homePageTitle = "Dataverse Analysis: Request #"+PID ;

            // create a temp dir
            String createTmpDir = "dir.create('"+wrkdir +"')";
            out.println("createTmpDir="+createTmpDir);
            c.voidEval(createTmpDir);

            // copy the dvn-patch css file to the wkdir
            // file.copy(from, to, overwrite = FALSE)
            String cssRootDir = "/usr/local/VDC/R/library";
            String cssFile =cssRootDir + "/" +"R2HTML.css";
            String cpCssFile = "file.copy('"+cssFile+"','"+wrkdir+"')";
            c.voidEval(cpCssFile);

            String ResultHtmlFileBase = "Rout."+PID ;
            String openHtml = "htmlsink<-HTMLInitFile(outdir = '"+
                wrkdir +"', filename='"+ResultHtmlFileBase+
                "', extension='html', CSSFile='R2HTML.css', Title ='"+
                homePageTitle+"')";
            out.println("openHtml="+openHtml);
            c.voidEval(openHtml);
            
            String StudyTitle ="Title comes here";
            String pageHeaderContents = "hdrContents <- \"<h1>Dataverse Analysis</h1><h2>Results</h2><p>Study Title:"+
            StudyTitle+"</p><blockquote><strong> <a href='javascript:window.history.back();'>Go back to the previous page</a></strong></blockquote><hr />\"";
            
            out.println("pageHeaderContents="+pageHeaderContents);
            c.voidEval(pageHeaderContents);
            
            String pageHeader = "HTML(file=htmlsink,hdrContents)";
            out.println("pageHeader="+pageHeader);
            c.voidEval(pageHeader);
            
            // create visuals directory for an EDA case
            String createVisualsDir = "dir.create('"+ wrkdir +
                "/" + "visuals" +"')";
            c.voidEval(createVisualsDir);
            
            // command lines
            out.println("univarStat="+univarStat);
            c.voidEval(univarStat);
            
            String univarChart = "try({x<-univarChart(dtfrm=x, " +
                "analysisoptn=aol, " +
                "imgflprfx='" + wrkdir + "/" + 
                "visuals" + "/" + "Rvls." + PID +
                "',standalone=F)})";
            out.println("univarChart="+univarChart);
            c.voidEval(univarChart);
            
            String univarStatHtmlBody = "try(univarStatHtmlBody(dtfrm=x,"+
            "whtml=htmlsink, analysisoptn=aol))";
            out.println("univarStatHtmlBody="+univarStatHtmlBody);
            c.voidEval(univarStatHtmlBody);

            String closeHtml = "HTMLEndFile()";
            c.voidEval(closeHtml);

            // save workspace
            String RdataFileName = "Rworkspace."+PID+".RData";
            String saveWS = "save('x', file='"+wrkdir +"/"+ RdataFileName +"')";
            out.println("save the workspace="+saveWS);
            c.voidEval(saveWS);
            
            // move the temp dir to the web-temp root dir
            String mvTmpDir = "file.rename('"+wrkdir+"','"+webwrkdir+"')";
            out.println("web-temp_dir="+mvTmpDir);
            c.voidEval(mvTmpDir);

            sr.put("requestdir", requestdir);
            sr.put("option", "eda");
            sr.put("type", "3"); // 1=(1,0), 2=(0,1), 3=(1,1)
            sr.put("html", "/"+requestdir+ "/" +ResultHtmlFile);
            sr.put("Rdata", "/"+requestdir+ "/" + RdataFileName);

        } catch (RserveException rse) {
            rse.printStackTrace();
        }
        return sr;
    }
    
    /**
     * Handles an Xtab request 
     *
     * @param     
     * @return    
     */
    public Map<String, String> runXtabRequest(ServiceRequest sro, RConnection c){
        out.println("*********** xtab option ***********");
        Map<String, String> sr = new HashMap<String, String>();

        try {
            String univarStat = "try(x<-univarStat(dtfrm=x))";
            String ResultHtmlFile = "Rout."+PID+".html" ;
            String vdcUtilLib = "library(VDCutil)";
        
            c.voidEval("dol<-''");
            String aol4xtab = "aol<-c(0,0,1)";
            c.voidEval(aol4xtab);
            
            // create a manifest page for this job request
            String homePageTitle = "Dataverse Analysis: Request #"+PID ;

            // create a temp dir
            String createTmpDir = "dir.create('"+wrkdir +"')";
            out.println("createTmpDir="+createTmpDir);
            c.voidEval(createTmpDir);
            
            // copy the dvn-patch css file to the wkdir
            // file.copy(from, to, overwrite = FALSE)
            String cssRootDir = "/usr/local/VDC/R/library";
            String cssFile =cssRootDir + "/" +"R2HTML.css";
            String cpCssFile = "file.copy('"+cssFile+"','"+wrkdir+"')";
            c.voidEval(cpCssFile);
            
            
            String ResultHtmlFileBase = "Rout."+PID ;
            String openHtml = "htmlsink<-HTMLInitFile(outdir = '"+
                wrkdir +"', filename='"+ResultHtmlFileBase+
                "', extension='html', CSSFile='R2HTML.css', Title ='"+
                homePageTitle+"')";
            out.println("openHtml="+openHtml);
            c.voidEval(openHtml);
            
            String StudyTitle ="Title comes here";
            String pageHeaderContents = "hdrContents <- \"<h1>Dataverse Analysis</h1><h2>Results</h2><p>Study Title:"+
            StudyTitle+"</p><blockquote><strong> <a href='javascript:window.history.back();'>Go back to the previous page</a></strong></blockquote><hr />\"";
            
            out.println("pageHeaderContents="+pageHeaderContents);
            c.voidEval(pageHeaderContents);
            
            String pageHeader = "HTML(file=htmlsink,hdrContents)";
            out.println("pageHeader="+pageHeader);
            c.voidEval(pageHeader);
            /*
                aol<-c(0,0,1)
                dol<-""
                try(x<-univarStat(dtfrm=x))
                library(VDCutil);
                try(VDCcrossTabulation(HTMLfile="/tmp/VDC/Rout.6769.html",
                data=x,
                classificationVars=c('NAThex5FDISChex5Fnew',
                'LOChex5FINThex5Fnew'),
                 wantPercentages=F, wantTotals=T, wantStats=T, wantExtraTables=T));
            */
            
            // command lines
            out.println("univarStat="+univarStat);

            c.voidEval(univarStat);
            c.voidEval(vdcUtilLib);
            String[] classVar = {"vote", "race"};
            String[] freqVar  = {};
            String[] xtabOptns= {"F", "T", "T", "T"};
            c.assign("classVar", new REXPString(classVar));
            c.assign("freqVar", new REXPString(freqVar));
            //"HTMLfile='"+wrkdir+ "/" +ResultHtmlFile+"'"+

            String VDCxtab = "try(VDCcrossTabulation("+
                "HTMLfile=htmlsink"+
                ", data=x"+ 
                ", classificationVars=classVar"+
                ", freqVars=freqVar" +
                ", wantPercentages="+xtabOptns[0]+
                ", wantTotals="+xtabOptns[1]+
                ", wantStats="+xtabOptns[2]+
                ", wantExtraTables="+xtabOptns[3]+"))";
            out.println("VDCxtab="+VDCxtab);
            c.voidEval(VDCxtab);

            String closeHtml = "HTMLEndFile()";
            c.voidEval(closeHtml);

            // save workspace
            String RdataFileName = "Rworkspace."+PID+".RData";
            String saveWS = "save('x', file='"+wrkdir +"/"+ RdataFileName +"')";
            out.println("save the workspace="+saveWS);
            c.voidEval(saveWS);
            
            // move the temp dir to the web-temp root dir
            String mvTmpDir = "file.rename('"+wrkdir+"','"+webwrkdir+"')";
            out.println("web-temp_dir="+mvTmpDir);
            c.voidEval(mvTmpDir);

            sr.put("requestdir", requestdir);
            sr.put("option", "xtab");
            sr.put("html", "/"+requestdir+ "/" +ResultHtmlFile);
            sr.put("Rdata", "/"+requestdir+ "/" + RdataFileName);

        } catch (RserveException rse) {
            rse.printStackTrace();
        }

        return sr;
    }
    
    /**
     * Handles a Zelig request 
     *
     * @param     
     * @return    
     */
    public Map<String, String> runZeligRequest(ServiceRequest sro, RConnection c){
        out.println("*********** zelig option ***********");
        Map<String, String> sr = new HashMap<String, String>();
        
        try {
            String univarStat = "try(x<-univarStat(dtfrm=x))";
            String ResultHtmlFile = "Rout."+PID+".html" ;
            String vdcUtilLib = "library(VDCutil)";

            c.voidEval("dol<-''");
            String aol4Zelig = "aol<-c(0,0,0)";
            c.voidEval(aol4Zelig);
            c.voidEval(vdcUtilLib);

            String modelname = sro.getZeligModelName();
            /* 
                ########## Code listing ##########
                library(VDCutil)
                ########## Code for the requested option starts here ##########
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

                if (exists('zlg.out')) {
                try(cat(file="/tmp/VDC/DSB/zlout_28948",
                paste(zlg.out,collapse="\t"),append=T,sep="\n"))
                } else {
                try(cat(file="/tmp/VDC/DSB/zlout_28948",
                "1_logit_failed",
                append=T,sep="\n"))}
                ########## Code for the request ends here ##########
                ########## Code listing: end ##########

            */
            
// Additional coding required here
            /*
                x[[3]]<- checkBinaryResponse(x[[3]])
             * 3 must be parameterized for binary response models
            */
            String binaryResponsevar = "x[[" +3 +"]]";
            String checkBinaryVarline=  binaryResponsevar +
                "<- checkBinaryResponse("+binaryResponsevar+")";
            c.voidEval(checkBinaryVarline);
            
            Map<String, String> ZlgModelParam = new HashMap<String, String>();
            
            // create a temp dir
            String createTmpDir = "dir.create('"+wrkdir +"')";
            c.voidEval(createTmpDir);
            
            
// Additional coding required here

            String lhsTerm = null;
            String rhsTerm = null;
            
            
            String VDCgenAnalysisline = 
                "zlg.out<- VDCgenAnalysis(" +
                "outDir='"+ wrkdir + "',"+ 
                lhsTerm +
                "~" +
                rhsTerm + ","+
                "model='"+ modelname +"',"+
                "data=x,"+
                "wantSummary="    + "T" + ","+
                "wantPlots="      + "T" + ","+
                "wantSensitivity="+ "F" + ","+
                "wantSim="        + "F" + ","+
                "wantBinOutput="  + "T" + ","+
                "setxArgs="       +"list()"  + ","+
                "setx2Args="      +"list()"  + ")";
                
            /*
               "TMLInitArgs=list(Title='Dataverse Analysis'),"+
                "HTMLnote= '<em>The following are the results of your
                requested analysis.</em><br/>
                <a href=\"javascript:window.history.go(-1);
                \">Go back to the previous page</a>'"+
            
            */
            out.println("VDCgenAnalysis="+VDCgenAnalysisline);
            
            // c.assign("zlg.out", VDCgenAnalysisline);
            c.voidEval(VDCgenAnalysisline);
            RList zlgout = c.parseAndEval("zlg.out").asList();
            //String strzo = c.parseAndEval("str(zlg.out)").asString();
            
            String[] kz = zlgout.keys();
            out.println("zlg.out:no of keys="+kz.length );// "\t"+kz[0]+"\t"+kz[1]
            
            for (int i=0; i<kz.length; i++){
                out.println(kz[i]+"="+zlgout.at(kz[i]).asString());
            }
            
            sr.put("requestdir", requestdir);
            sr.put("option", "zelig");
            sr.put("model", modelname);
            
            // copy the dvn-patch css file to the wkdir
            // file.copy(from, to, overwrite = FALSE)
            String cssRootDir = "/usr/local/VDC/R/library";
            String cssFile =cssRootDir + "/" +"R2HTML.css";
            String cpCssFile = "file.copy('"+cssFile+"','"+wrkdir+"')";
            c.voidEval(cpCssFile);
            /*
            String createHomePage = "file.create('"+ wrkdir + "/"
             + "index.html" +"')";
            c.voidEval(createHomePage);
            
            String manifestPage = wrkdir + "/" + "index.html" ;
            RFileOutputStream osx = c.createFile(manifestPage);
            // create a page string 
            // convert this tring into bytes getBytes() method
            // InputStream bais = new ByteArrayInputStream(byte_array)
            while ((bufsize = bais.read(bffr)) != -1) {
                osx.write(bffr, 0, bufsize);
            }
            osx.close();
            */
            
            // move the temp dir to the web-temp root dir
            String mvTmpDir = "file.rename('"+wrkdir+"','"+webwrkdir+"')";
            out.println("web-temp_dir="+mvTmpDir);
            c.voidEval(mvTmpDir);
            
            /*
            [~/java/rcode]% javac -cp ../../jar/Rserve.jar:../../jar/REngine.jar:../../jar/commons-lang-2.4.jar callRserve2execR1remote.java
            [~/java/rcode]% java -cp .:../../jar/Rserve.jar:../../jar/REngine.jar:../../jar/commons-lang-2.4.jar callRserve2execR1remote
            hostname=dsb-2.hmdc.harvard.edu
            >R version 2.6.2 (2008-02-08)<
            wrkdir=/tmp/VDC/DSB/Zlg_949087
            vartyp length=3 1
            vnames length=3 vote
            readtable=x<-read.table141vdc(file='/tmp/VDC/DSB/susetfile4Rjob.949087.tab', col.names=vnames, colClassesx=vartyp, varFormat=varFmt )
            key=2 value=white
            key=1 value=others
            1       VALTABLE[['1']]<-list('2'='white','1'='others')
            
            VDCgenAnalysis=zlg.out<- VDCgenAnalysis(outDir='/tmp/VDC/DSB/Zlg_949087',vote ~ age+race,model='logit',data=x,wantSummary=T,wantPlots=T,wantSensitivity=F,wantSim=F,wantBinOutput=T,setxArgs=list(),setx2Args=list())
            
            zlg.out:no of keys=2
            
            html=/tmp/VDC/DSB/Zlg_949087/index1208330945.24787A25437A202091187.html
            
            Rdata=/tmp/VDC/DSB/Zlg_949087/binfile1208330945.24787A25437A202091187.Rdata
            
            web-temp_dir=file.rename('/tmp/VDC/DSB/Zlg_949087','/tmp/VDC/webtemp/Zlg_949087')
            
            
            http://dsb-2.hmdc.harvard.edu/temp/Zlg_949087/index1208330945.24787A25437A202091187.html
            /tmp/VDC/webtemp/Zlg_790226/Rout.790226.html
            http://dsb-2.hmdc.harvard.edu/temp/Zlg_949087/Rout.790226.html
            */
            
        } catch (REngineException ree){
            ree.printStackTrace();
        } catch (RserveException rse) {
            rse.printStackTrace();
        } catch (REXPMismatchException mme) {
            mme.printStackTrace();
        }
        
        return sr;
    }
    
    
    /**
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
            out.println("hostname="+hostname);
            
            // set-up R command lines
            // temp file
            String R_TMP_DIR = "/tmp/VDC/";
            String cnfgfl = R_TMP_DIR +"configZeligGUI."+RandomStringUtils.randomNumeric(6)+ ".xml"; 
            out.println("cnfgfl="+cnfgfl);
            // R code line part 1         
            String cmndline = "library(VDCutil);  printZeligSchemaInstance('"+ cnfgfl + "')";
            // cnfgfl  +  "'," + "'" + RSERVE_HOST + "')";
            out.println("comand line="+cmndline);
            
            // write zelig GUI-config data  to the temp ifle
            c.voidEval(cmndline);
            //out.println("commandline output="+outString);
            
            // check whether the temp file exists (Rserve is local)
            long filelength=0;
            try {
                if (RSERVE_HOST.equals("localhost") || RSERVE_HOST.equals(hostname)){
                    File flnm =new File(cnfgfl);
                    boolean exists = flnm.exists();
                    if (exists) {
                        out.println("configuration xml file ("+cnfgfl+") was found");
                        filelength = flnm.length();
                        // Get the number of bytes in the file
                        out.println("The size of the file ="+filelength);
                    } else {
                        out.println("configuration xml file ("+cnfgfl+") was not found");
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
                out.println("string length="+zlgcnfg.length());
                if (zlgcnfg.length()>0){
                    out.println("first 72 bytes=[\n"+zlgcnfg.substring(0, 71) +"\n]\n");
                }
            //}
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return zlgcnfg;
    }

}
