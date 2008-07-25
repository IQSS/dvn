# VDC Support Functions for Zelig




#################################################################
# dependency list for models
#################################################################
zeligModelDependencies<-function(inZeligOnly=T,schemaVersion="1.1",uninstalledOnly=T, repos=NULL) {
    zl=zeligListModels()
    if (uninstalledOnly) {
        zi=zeligInstalledModels()
        uninstalled= zl[which(!sapply(zl,function(x)(sum(x==zi)>0)))]
        ml = uninstalled
    } else {
        ml = zl
    }

    deps=NULL
    for (i in ml) {
        zd = zeligModelDependency (i,repos)
            if (is.null(zd)){
                next
            }
        deps=rbind(deps,zd)
    }   
    deps=unique(deps)
    return(deps)
}


#################################################################
# Produce Description of Models in XML format
#################################################################

printZeligSchemaInstance<-function(filename=NULL, serverName=NULL,vdcAbsDirPrefix=NULL){
    # open connection 
    schemaURL<-'http://gking.harvard.edu/zelig';
    if (is.null(serverName)) {
        serverName<-getHostname()
    }
    locationURL<-'http://thedata.org/files/thedata/schema/ZeligInterfaceDefinition_1_1.xsd'
    #if (is.null(vdcAbsDirPrefix)){
    #    locationURL<-paste('http://', serverName, '/VDC/Schema/analysis/ZeligInterfaceDefinition.xsd',sep="");
    #} else {
    #    locationURL<-paste('file://', vdcAbsDirPrefix, '/VDC/Schema/analysis/ZeligInterfaceDefinition.xsd',sep="");
    #}
    schemaLocation<-paste(schemaURL, ' ', locationURL, sep='');
    con<-"";
    if ((!is.null(filename)) && filename !="" ){
        con<-file(filename,"w");
    }
    cat(file=con, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<zelig xmlns=\"",schemaURL,"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"",schemaLocation,"\">\n", sep="");
    zi = zeligInstalledModelsVDC();
    zi=setdiff(zi,c("irtkd","normal","normal.bayes"))   
    CleanzeligDescribeModelXML<-function(model){
        ret= sub("formula(.*)/>","formula\\1>",perl=T,zeligDescribeModelXML(model))
        ret=  sub("equation(.*)/>","equation\\1>",perl=T,ret)
        ret 
    }
    mssg<- sapply(zi,function(x){cat(file=con,CleanzeligDescribeModelXML(x),sep="")},simplify=F);
    cat(file=con,"\n</zelig>\n",sep="");
}

zeligInstalledModelsVDC<-function (inZeligOnly = T, schemaVersion = "1.1"){
    chkpkgs <- function(name) {
        zd = zeligDescribeModelXML(name, schemaVersion = schemaVersion)
        if (is.null(zd)) {
            return(FALSE)
        }
        zdpd = zeligModelDependency(name)[, 1]
        if (is.null(zdpd)) {
            return(TRUE)
        }
        ow = options(warn = -1)
        vdcrequire <- require
        ret = sapply(zdpd, function(x) vdcrequire(x, character.only = TRUE) ==T)
        options(ow)
        return(ret)
    }
    models <- zeligListModels(inZeligOnly = inZeligOnly)
    tmpModels <- sapply(models, chkpkgs)
    models[which(tmpModels)]
}

# taken from R.utils 
getHostname<-function (){
    host <- Sys.getenv(c("HOST", "HOSTNAME", "COMPUTERNAME"))
    host <- host[host != ""]
    if (length(host) == 0) {
        tryCatch({
            host <- readLines(pipe("/usr/bin/env uname -n"))
            host <- host[host != ""]
        }, error = function(ex) {
        })
    }
    if (length(host) == 0){
        host <- NA
    }
    host[1]
}


##############################################################
# Workaround for VGAM's nonstandard manipulation of summary()
##############################################################

summary.vglm<-function(object, ...) {
  return (summaryvglm(object,...))
}


##############################################################
# S3 Generic methods for pretty-printig and plotting
##############################################################

#
# Plot methods for strata
#

plot.strata<-function(x,ask=dev.interactive(),...) {
  if (ask) {
        op <- par(ask = TRUE)
        on.exit(par(op))
  }
  count=1
  for (tmp in x) {
     cat("\n\n****Plotting zelig strata #", count,"\n")
     count=count+1
     plot(tmp,...)
  }
  return(invisible())
}

plot.zelig.strata<-function(x,ask=dev.interactive(),...) {
  if (ask) {
        op <- par(ask = TRUE)
        on.exit(par(op))
  }
  count=1
  for (tmp in x) {
     cat("\n\n****Plotting zelig strata #", count,"\n")
     count=count+1
     plot(tmp,...)
  }
  return(invisible())
}

HTML.summary.zelig.strata <- function(x, subset = NULL, ...){
  if (is.null(subset)){
    m <- length(x)
  } else if (any(subset > max(m))) {
    stop("the subset selected lies outside the range of available \n        sets of regression output.")
  } else {
    m <- subset 
  }
  for (i in 1:m) {
    HTML(paste("\nResults for", names(x)[i], "\n"),...)
    HTML(x[[i]],...)
    HTML("\n",...)
  }   
} 

HTML.summary.MCMCZelig <- function(x, digits=max(3, getOption("digits") -
3), ...) {
  HTML("\nCall: ") 
  HTML(x$call) 
  HTML(paste("\n", "Iterations = ", x$start, ":", x$end, "\n", sep = ""),...)
  HTML(paste("Thinning interval =", x$thin, "\n"), ...)
  HTML(paste("Number of chains =", x$nchain, "\n"),...)
  HTML(paste("Sample size per chain =", (x$end - x$start)/x$thin + 1, "\n"),...)
  HTML(paste("\n", "Mean, standard deviation, and quantiles for marginal posterior distributions.", "\n"),...)
  HTML(round(x$summary, digits=digits),...)
  cat("\n",...)
}   

HTML.summary.zelig <- function(x, digits=getOption("digits"), print.x=FALSE, ...){
  HTML(paste("\n  Model:", x$model, "\n"),...)
  if (!is.null(x$num)){
      HTML(paste("  Number of simulations:", x$num, "\n"),...)
  }
  if (!is.null(x$x)) {
    if (print.x || nrow(x$x) == 1 || is.null(dim(x$x))) {
      if (any(class(x$x) == "cond"))
        HTML(paste("\nObserved Data \n"),...)
      else
        HTML(paste("\nValues of X \n"),...)   
      HTML(x$x, digits=digits, ...)
      if(!is.null(x$x1)){
        HTML(paste("\nValues of X1 \n"),...)
        HTML(x$x1, digits=digits, ...)
      }
    }
    else {
      if (any(class(x$x) == "cond")){
        HTML(paste("\nMean Values of Observed Data (n = ", nrow(x$x), ") \n", sep = ""),...)
      } else {
        HTML(paste("\nMean Values of X (n = ", nrow(x$x), ") \n", sep = ""),...)
      }
      HTML(apply(x$x, 2, mean), digits=digits, ...)
      if (!is.null(x$x1)) {
        HTML(paste("\nMean Values of X1 (n = ", nrow(x$x1), ") \n", sep = ""),...)
        HTML(apply(x$x1, 2, mean), digits=digits, ...) 
      }
    }
  }
  for (i in 1:length(x$qi.name)){
    indx <- pmatch(names(x$qi.name[i]), names(x$qi.stats))
    tmp <- x$qi.stats[[indx]]
      lab <- x$qi.name[[i]]
    HTML(paste("\n", lab, "\n", sep = ""),...)
    if (length(dim(tmp)) == 3) {
        for (j in 1:dim(tmp)[3]){
          HTML(paste("\n  Observation", rownames(x$x)[j], "\n"),...)
          if (is.null(rownames(tmp[,,j]))){
            rownames(tmp[,,j]) <- 1:nrow(tmp[,,j])
          }
          if (!is.null(names(tmp[,,j]))){
            names(tmp[,,j]) <- NULL
          }
          HTML(tmp[,,j], digits=digits, ...)
        }
    } else {
      if (is.matrix(tmp) & is.null(rownames(tmp))){
        rownames(tmp) <- 1:nrow(tmp)
      }
      HTML(tmp, digits=digits, ...)
    }
  }
}



