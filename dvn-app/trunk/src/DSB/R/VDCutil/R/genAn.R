#
# genAnalysisInternal
#
# Internal function for generalized analysis
#
# Takes a zelig formula and data, control flags for output, returns paths to
# output files
#


VDCgenAnalysis<-function(
      # required arguments to zelig
      formula,model,data, 
      # zelig strata
      by=NULL,
      # additional agrs to setx, sim, zelig. Note: setting setx2Args
      # causes setx to be run a second time
      setxArgs=NULL, setx2Args=NULL, simArgs=NULL,zeligArgs=NULL, 
      sensitivityArgs=list(ptb.s=sensitivityQ),
      # HTMLInitArgs
      HTMLInitArgs=list(Title="VDC Analysis"),
      HTMLbodyHeading = HTMLInitArgs$Title,
      HTMLnote= "<em>The following are the results of your requested analysis.</em>",
      # NA processing?
      naMethod="exclude",
      # size of sensitivity analysis noise
      sensitivityQ=NULL,
      # where does html, css, javascript, plots go?
      outDir=tempdir(),
      # analysis options: perform summary, plots, sensitivity, sims?
      wantSummary=T,wantPlots=F,wantSensitivity=F,wantSim=F,
      # deliver binary output?
      wantBinOutput=F,  
      debug=F) {
      

      # workaround Zelig 2.5.2 bug
    attr(formula,".Environment")=environment()
      # list of objects to save

      savelist=character(0)
      
      # preprocess data
      # TODO: Add multiple imputation option in 1.05
      if (naMethod == "omit") {
         data<-na.omit(data);
      } else if (naMethod=="exclude") {
         data<-na.exclude(data);
      }

      # add vdc values labels and MV, recode to factors
      #data<-recodeVDCdf(data)

      # add special to formula if necessary 
      formula = formulaAddSpecial(formula,model)
      
      # run initial analysis through zelig
      zel.out<-do.call("zelig",
           c(list(formula=formula,model,as.name("data")),zeligArgs) )
      savelist<-c(savelist,"zel.out")

      if (wantSim) {
         # setx setup
     setx.out<-do.call("setx",c(list(zel.out),setxArgs) )
     simArgs$x=setx.out
         # run setx2 if requested
         setx2.out=NULL
         if (!is.null(setx2Args)) {
            setx2.out<-do.call("setx",c(list(zel.out),setx2Args ))
        simArgs$x1=setx2.out
         }
      }

      # run sims and sentivity analysis , if requested
      if (wantSensitivity) {
      sensitivityArgs$ptb.s=sensitivityQ
          savelist<-c(savelist,"zel.rob.out")
          if (wantSim) {
         zel.rob.out <- do.call("sensitivityZelig", 
        c(list(zel.out), sensitivityArgs, simulate = TRUE, simArgs=list(simArgs)) )

             sim.rob.out=zel.rob.out$sim
             savelist=c(savelist,"sim.rob.out")
          } else {
             zel.rob.out<-do.call("sensitivityZelig", c(list(zel.out),sensitivityArgs,summarize=T))
      }
      }
      
      if (wantSim) {
         sim.out=do.call("sim", 
        c(list(zel.out),simArgs))
         savelist=c(savelist,"sim.out")
      }
      
      
      # function for unique id's
      
      uniqid <-  paste(
         as.character(as.numeric(Sys.time())),
         Sys.getpid(),
         as.integer(runif(1,min=0,max=.Machine$integer.max)),
         sep="A"
      )     # this is unique with very high probability, and comprises safe chars
      

      # setup output paths, html output
      HTMLInitArgs$filename=paste("index",uniqid,sep="")
      HTMLInitArgs$outdir=outDir

      binFilePath <-  file.path(outDir,paste("binfile",uniqid,".Rdata",sep=""))
      htmlFilePath <-  do.call("HTMLInitFile",HTMLInitArgs)

      copyR2HTMLfiles(outDir)

      if (!is.null(HTMLbodyHeading)) {
        HTML.title(HTMLbodyHeading,HR=1)
      }
      if (!is.null(HTMLnote)) {
    HTML(HTMLnote)
      }

      #print output
      if(wantSummary) {
          HTML.title("Summary Results")
      tmpsum = try(summary(zel.out),silent=TRUE);
          if (!inherits(tmpsum,"try-error")) {
        # R2HTML bug workaround in 1.57
        ow=options(warn=-1)
        try(HTML(tmpsum, file=htmlFilePath),silent=TRUE);
        options(ow)
      }
          if (wantPlots) {
                try(hplot(zel.out),silent=T)
          }
      }

      if(wantSummary && wantSensitivity) {
          HTML.title("Sensitivity Analysis")
      tmpsum <-try(summary(zel.rob.out), silent=TRUE)
          if (!inherits(tmpsum,"try-error")) {
            HTML(tmpsum, file=htmlFilePath)
      }
          if (wantPlots) {
             try(hplot(zel.rob.out),silent=T)
          }
      }
      
      if(wantSim) {
          HTML.title("Simulation  Results")
      tmpsum = try(summary(sim.out),silent=TRUE);
          if (!inherits(tmpsum,"try-error")) {
            HTML(tmpsum, file=htmlFilePath);
      }

          if (wantPlots) {
             try(hplot(sim.out),silent=T)
          }
      }
      
      if(wantSim && wantSensitivity) {
          HTML.title("Simulation Sensitivity Analysis")
      tmpsum = try(summary(sim.rob.out),silent=TRUE);
          if (!inherits(tmpsum,"try-error")) {
            HTML(tmpsum, file=htmlFilePath);
      }

          if (wantPlots) {
             try(hplot(sim.rob.out),silent=T)
          }
      }

      
      # binary output
      if(wantBinOutput) {
          save(list=savelist,file=binFilePath)
        #HTML( paste("<br/><center><small><a href='",basename(binFilePath), "'>[Replication data]</a></small></center><br/>",sep=""))
        
      } else {
          binFilePath=NULL;
      }
      # cleanup and return paths
      HTMLEndFile(file=htmlFilePath)
      
      res=  list(htmlFilePath,binFilePath)
      names(res)=c("html","Rdata")
      return(res)
}

# For formulas with multiple outcome variables, 
# allow caller to pass in as list(Y,Y2,Y3)~X1+X2+X3
# and change to correct special

formulaAddSpecial<-function(formula,model) {
    modelSpecial= zeligGetSpecial(model)
    if (!is.null(modelSpecial) && is.na(modelSpecial)) {
        modelSpecial = NULL;
    }
    if ( (!is.null(modelSpecial))  && 
        length(formula[[2]])>1 &&
        formula[[2]][[1]]=="list"
        )  {
        formula[[2]][[1]]=as.name(modelSpecial)
        attr(formula,".Environment") <-  globalenv()
    } else  if ((!is.null(modelSpecial))  && 
        length(formula[[2]]==1)) {
        formula[[2]]=call(modelSpecial,formula[[2]])
        attr(formula,".Environment") <-  globalenv()
    } else if (model == "tobit"){
        attr(formula,".Environment") <-  globalenv()
    }
    return(formula)
}

# WORKAROUND For R2HTML not copying all the supporting files
copyR2HTMLfiles<-function(outDir){
  assign("HTMLenv",new.env(parent=.GlobalEnv),envir=.GlobalEnv)
  assign(".HTML.outdir",outDir,envir=get("HTMLenv",envir=.GlobalEnv))
  r2hbasedir=   file.path(.find.package(package = "R2HTML"),"output")
  file.copy(file.path(r2hbasedir,"R2HTML.css"),outDir)
  file.copy(file.path(r2hbasedir,"ASCIIMathML.js"),outDir)
  file.copy(file.path(r2hbasedir,"gridR2HTML.css"),outDir)
  file.copy(file.path(r2hbasedir,"gridR2HTML.js"),outDir)
  dir.create(file.path(outDir,"runtime/styles/xp/"),recursive=T,showWarnings=F)
  file.copy(file.path(r2hbasedir,"grid.css"),file.path(outDir,"runtime","styles","xp/"))
  dir.create(file.path(outDir,"runtime","lib"),recursive=T,showWarnings=F)
  file.copy(file.path(r2hbasedir,"grid.js"),file.path(outDir,"runtime","lib"))
}
