library(foreign)
library(stats)
library(methods)
library(UNF)
library(R2HTML)

############ parameters ########################

univarstathdr<-c("Valid Cases", "Missing Cases(NAs)", "Total", "Mean", "Standard deviation", "Skewness", "Kurtosis", "Coefficient of variation", "Mode", "Minimum","1st Quartile","Median","3rd Quartile","Maximum","Range","Interquartile Range","Normality Test(Shapiro-Wilk Statistic)", "Normality Test(Shapiro-Wilk Statistic: p value)")

imgprfx1<-c("<img src=\"http://")
imgprfx2<-c("/nph-dmpJpg.pl?jpgfn=")
imgsffx1<-c("\" >\n")
imgsffx2<-c("\" >\n")

###########################################################
read.table141vdc<-function (file, header = FALSE, sep = "\t", quote = "", dec = ".", col.names=NULL, na.strings = "NA",colClasses = NA,  colClassesx = NA, nrows = -1, skip = 0, check.names = TRUE,fill = !blank.lines.skip, strip.white = FALSE, blank.lines.skip = TRUE, comment.char = "", varFormat=list()) 
{
    if (is.character(file)) {
        file <- file(file, "r")
        on.exit(close(file))
    }
    if (!inherits(file, "connection")) stop("argument 'file' must be a character string or connection")
    if (!isOpen(file)) {
        open(file, "r")
        on.exit(close(file))
    }
    if (skip > 0) readLines(file, skip)

    cols<- length(colClassesx)
    if (is.null(col.names)) col.names<-paste("V", 1:cols, sep = "")
    if(check.names) col.names <- make.names(col.names, unique = TRUE)
    what <- rep(list(""), cols)
    names(what) <- col.names
    known <- colClasses %in% c("logical", "integer", "numeric", "complex", "character")
    what[known] <- sapply(colClasses[known], do.call, list(0))
    
    data <- scan(file = file, what = what, sep = sep, quote = quote, dec = dec, nmax = nrows, skip = 0, na.strings = na.strings, quiet = TRUE, fill = fill, strip.white = strip.white, blank.lines.skip = blank.lines.skip, multi.line = FALSE, comment.char = comment.char)
    
    nlines <- length(data[[1]])
    
    if (cols != length(data)) {
        warning(paste("cols =", cols, " != length(data) =", length(data)))
        cols <- length(data)
    }
    
    for (i in 1:cols) {
        #if (known[i]) next
        #data[[i]] <- as(data[[i]], colClasses[i])
        if (colClassesx[i] == 0) {
            
             if (is.null(unlist(varFormat[col.names[i]]))){
                #cat("before-s=",i, "\n")
                data[[i]] <- as(data[[i]], "character")
                #cat("after-s=",i, "\n")
             } else if (!is.null(unlist(varFormat[col.names[i]]))){
                if (varFormat[col.names[i]] == 'D'){
                    #cat("before-d=",i, "\n")
                    data[[i]]<-as.Date(data[[i]])
                    #cat("after-d=",i, "\n")
                    colClassesx[i]<-1
                } else if (varFormat[col.names[i]] == 'T'){
                    #cat("before-t=",i,"\n")
                    data[[i]]<-as.POSIXct(strptime(data[[i]], "%T"))
                    #cat("after-t=", i,"\n")
                    colClassesx[i]<-1
                } else if (varFormat[col.names[i]] == 'DT'){
                    data[[i]]<-as.POSIXct(strptime(data[[i]], "%F %T"))
                    colClassesx[i]<-1
                } else if (varFormat[col.names[i]] == 'JT'){
                    data[[i]]<-as.POSIXct(strptime(data[[i]], "%j %T"))
                    colClassesx[i]<-1
                }
             }
        } else {
            data[[i]]<-type.convert(data[[i]], dec = dec)
            #cat("data[[", i, "]]:", class(data[[i]]), "\n", sep="")
            #if ( (class(data[[i]]) == "numeric") & (colClassesx[i]==1) ) {
            #   colClassesx[i]<-2
            #}
        }
    }

    class(data) <- "data.frame"
    row.names(data) <- as.character(seq(len = nlines))
    attr(data, "var.type")<-colClassesx
    data
} # end of read.table141vdc
###########################################################
createvalindex <-function(dtfrm, attrname=NULL){
    # this version relies on the list-based approach
    # completely new final [without old cod block]
    if (is.null(dtfrm)) {
        stop("dataframe is not specified\n")
    } else if (is.null(attrname)){
        stop("attrname is is not specified\n")
    } else if (!exists('dtfrm')) {
        stop("dataframe is not found\n")
    } else if (!is.data.frame(dtfrm) ) {
        stop("Specified object is not a data.frame\n")
    }
        
    #DBG<-TRUE
    DBG<-FALSE
    try ( {
    if (attrname == 'val.index') {
        tabletype<-'val.table'
        valtable<-attr(dtfrm, 'val.table')
    } else if (attrname == 'missval.index') {
        tabletype<-'missval.table'
        valtable<-attr(dtfrm, 'missval.table')
    } else stop ("Specified attrname must be either val.index or missval.index\n")
    
    if (DBG) {cat("\nattribute name=",attrname,"\n")}
    if (length(valtable)) {
        vlindex  <- list();
        vlst  <- list();
        lstall<-list()
        vltbl<-list()
        if (DBG) {
            cat("length(",attrname,")=",length(valtable),"\n")
            cat("varidset(",attrname,")=",names(valtable),"\n")
        }
        nameset<-names(valtable)
        if (DBG) {
            str(nameset)
            cat("\nnameset:", paste(nameset,collapse="|"), "\n",sep="")
        }
        for (i in 1:(length(valtable))){
        if (DBG) {
            cat("var=",i,"\n", sep="")
            cat("\tlstall:", paste(if (length(lstall)) {as.vector(lstall,mode="integer")} else {"empty"}, collapse=","), "\n",sep="")
        }
            nameseti<-nameset[i]
            if (!is.null(lstall[[as.character(i)]])){next}
            lsti<-list()

            # set i to the new list
            lsti[[as.character(i)]]<-i
            lstall[[as.character(i)]]<-i
            vlindex[[as.character(nameseti)]]<-nameset[i]
            vltbl[[as.character(nameseti)]]<-valtable[[i]]

            if (DBG) {cat("\tlsti:", paste(as.vector(lsti, mode="integer"),collapse=","), "\n",sep="")}
            for (j in i:length(valtable)){
                if (!is.null(lstall[[as.character(j)]])){next}
                if (attrname == 'val.index') {
                    if (  identical( names(valtable[[i]]), names(valtable[[j]])  ) & identical(valtable[[i]], valtable[[j]]) ) {
                        if (DBG) {cat("\tVL:new duplicate (var#) to be added:", j,"\n",sep="")}
                        lsti[[as.character(j)]]<-j
                        vlindex[[as.character(nameset[j])]]<-nameseti
                        lstall[[as.character(j)]]<-j
                    }
                } else if (attrname == 'missval.index') {
                    if ( identical(valtable[[i]], valtable[[j]]) ) {
                        if (DBG) {cat("\tMSVL: new duplicate (var#) to be added:", j,"\n",sep="")}
                        lsti[[as.character(j)]]<-j
                        vlindex[[as.character(nameset[j])]]<-nameseti
                        lstall[[as.character(j)]]<-j
                    }
                }
            }
            if (DBG) {cat("\tlsti to be attached to vlst:", paste(as.vector(lsti, mode="integer"),collapse=","), "\n",sep="")}
            if (length(lsti)){
                vlst[[nameseti]]<-nameset[as.vector(lsti, mode="integer")]
            }
        }
        if (DBG) {
            cat("\nvlst=attr(dtfrm,'val.list')  <- vlst\n")
            str(vlst)
            cat("\nvlindex=attr(dtfrm,'val.index') <- vlindex\n")
            str(vlindex)
            cat("\nvltbl=attr(dtfrm,'val.table')<- valtablex\n")
            str(vltbl)
            cat("\nnames(vltbl): equivalent to tmpunique\n")
            cat("unique var IDs:", paste(names(vltbl),collapse="|"), "\n",sep="")
        }
        attr(dtfrm, attrname)<-vlindex

        if (attrname == 'val.index') {
            attr(dtfrm, 'val.list')  <- vlst
            attr(dtfrm, 'val.table') <- vltbl
        } else if (attrname == 'missval.index') {
            attr(dtfrm, 'missval.list')  <- vlst
            attr(dtfrm, 'missval.table')<-vltbl
        }
            
    } else {
            # no value labels
            #vlindex<-rep(NA, dim(dtfrm)[2])
            attr(dtfrm, attrname)<-NULL
            if (attrname == 'val.index') {
                attr(dtfrm, 'val.list')<- NA 
            } else if (attrname == 'missval.index') {
                attr(dtfrm, 'missval.list')  <- NA
            }
    }
        
    invisible(dtfrm)
    }) # end try    
} # end of createvalindex

###########################################################
# 2 table functions that return univariate statistics
# continuous case

frqtbl.ctn<-function(x){
    frqtbl<-list()
    tbl1<-table(x, useNA='ifany')
    frqtbl[['Mode']]<-NA
    if (length(x) > length(tbl1)) {
        frqtbl[['Mode']]<- names(tbl1)[which.max(tbl1)]
    }
    frqtbl
}

frqtbl.dsc<-function(x){
    frqtbl<-list()
    DBG<-FALSE
        
        # ftbl: frequency table
        ftbl<-table(x, useNA='ifany')
            
        # get the mode
        frqtbl[['Mode']]<-NA
        frqtbl[['freqtbl']]<-NA
        frqtbl[['pcnttbl']]<- NA
        if (length(x) > length(ftbl)){
            frqtbl[['Mode']]<-names(ftbl[which.max(ftbl)])
            if ((length(ftbl)<=50)){
                # ptbl: percentage table
                ptbl<-100*(ftbl/sum(ftbl))
                # set up the return list
                frqtbl[['freqtbl']]<- ftbl
                frqtbl[['pcnttbl']]<- ptbl
                if (DBG){
                    cat("\ttable header:",paste(dimnames(ftbl)[[1]], collapse='|'), "\n")
                    cat("\ttable frequency:",paste(ftbl, collapse='|'), "\n")
                    cat("\tstatistical mode:", frqtbl[['Mode']], "\n")
                    cat("\tstatistical mode(freq):", tbl1[which.max(ftbl)], "\n")
                }
            }
        }
        
    frqtbl
}

sw.stat<-function(x,N){
    DBG<-TRUE
    DBG<-FALSE
    SW<-list()
    SW$value <- NA
    SW$Pvalue <- NA
    if ((N >= 3) & (N <= 5000)) {
        shpr <- try(shapiro.test(x))
        if (attr(shpr, "class") == 'htest') {
            if(DBG) {cat("sw statistics assigned\n")}
            SW$value <- shpr[[1]][[1]]
            SW$Pvalue <- shpr[[2]]
        }
        if(DBG) {cat("sw statistics end\n")}
    }
    SW
}

univarStat.cntn<-function(varseti){
    options(digits=3)
    DBG<-TRUE
    DBG<-FALSE
    if(DBG) {cat("pass the point univStat(continuous)\n")}

    N<-sum(complete.cases(varseti))
    svnm<-summary(varseti)

    if (N) {
        min.value <- svnm[[1]]
        q1.value <- svnm[[2]]
        #median.value <- median(varseti)
        median.value <- svnm[[3]] 
        q3.value <- svnm[[5]]
        max.value <- svnm[[6]]
        range.value <- svnm[[6]]-svnm[[1]]
        iqr.value <- svnm[[5]]-svnm[[1]]
        mean.value <- svnm[[4]]
    } else {
        min.value <- NA
        q1.value <- NA
        median.value <- NA
        q3.value <- NA
        max.value <- NA
        range.value <- NA
        iqr.value <- NA
        mean.value <- NA
    }

    stdv.value <- sd(varseti, na.rm=T)
    z0 <- scale(varseti)
    if (N >= 2) {cv.value <- stdv.value/svnm[[4]] } else {cv.value <- NA}
    if (N >= 3) {skewness.value <- (N/(N-1)/(N-2))*sum((z0)^3, na.rm=T)} else {skewness.value <- NA}
    if (N >= 4) {kurtosis.value <- ((N*(N+1)/(N-1))*sum((z0)^4, na.rm=T) - 3*(N-1)^2)/(N-2)/(N-3)} else {kurtosis.value <-NA}
    # find the maximum frequency cell
    # index: which.max(table(dtfrm[[i]]))

    maxfreq<-frqtbl.ctn(x=varseti)[["Mode"]]
    SW<-sw.stat(x=varseti,N=N)
    statset<- list(
        Vald = N,
        Invald = sum(is.na(varseti)), 
        Total = length(varseti), 
        Mean = mean.value, 
        Stdev = stdv.value, 
        Skewness = skewness.value,
        Kurtosis = kurtosis.value,
        CV = cv.value, 
        Mode = maxfreq,
        Minimum = min.value, 
        Q1 = q1.value,
        Median = median.value, 
        Q3 = q3.value, 
        Maximum = max.value, 
        Range = range.value, 
        I.Q.R = iqr.value,
        S.W.statistic = SW$value, 
        S.W.P.value = SW$Pvalue
    )
    statset
}
    
univarStat.dscrt<-function(varseti, ordnl=TRUE){
    DBG<-TRUE
    DBG<-FALSE

    if(DBG) {cat("pass the point univStat(discrete)\n")}
    N<-sum(complete.cases(varseti))
    if (ordnl){
        median.value <-NULL
        if (N) {median.value <- median(varseti, na.rm=TRUE) }
    }
    tmpfrq<-frqtbl.dsc(x=varseti)

    statset<- list(
        Vald = N,
        Invald = sum(is.na(varseti)), 
        Total = length(varseti),
        Mode = tmpfrq[["Mode"]],
        freqtbl = tmpfrq[["freqtbl"]],
        pcnttbl = tmpfrq[["pcnttbl"]]
    )
    if (ordnl){
        statset$Median<-median.value
    }
    statset
}
    

univarStat<-function(dtfrm){
    DBG<-TRUE
    DBG<-FALSE
    if(DBG) {
        cat("\n\nEntered the function univarStat\n")
        NAMESET<-names(dtfrm)
    }
    
    STATLST<-list()
    
    # create temp vars
    VARTYPE<-attr(dtfrm, "var.type")
    for (i in 1: dim(dtfrm)[2]) {
        try ({
            varseti<-dtfrm[[i]]
            
            if(DBG) {cat("variable name =",NAMESET[i],"\n")}

            N<-sum(complete.cases(varseti))

            if (VARTYPE[i]== 2) {
            
                STATLST[[as.character(i)]]<-univarStat.cntn(varseti=varseti)
                
            } else if (VARTYPE[i] == 1) {
            
                STATLST[[as.character(i)]]<-univarStat.dscrt(varseti=varseti)
                
            } else if (VARTYPE[i] == 0) {
            
                STATLST[[as.character(i)]]<-univarStat.dscrt(varseti=varseti,ordnl=FALSE)
                
            } else {
            
                STATLST[[as.character(i)]]<-NULL
            
            }

        }) # end of try
    } # end of the loop
    
    attr(dtfrm, "univarStat.lst")<-STATLST

    invisible(dtfrm)
} # end of univarStat
###########################################################
univarChart<-function(dtfrm, analysisoptn=NULL, imgflprfx=NULL, standalone=T){
    # description
    # to print univariate charts
    #
    # arguments
    # dtfrm[[i]] variable name
    # analysisoptn Analysis option
    # imgflprfx temporary image file prefix

    # local variable 
    # varlabel variable label (local variable)
    # No return value; each image file is written in /tmp
    # $RvlsPrfx   = "$TMPDIR/Rvls.$PRCSSID";
    # note: value labels will be printed in html tables
    # unvlst[[as.character(i)]]<-statset
    
    # new list-based notations
    # USL<-attr(dtfrm,"univarStat.lst")
    # chartset[["hstbx"]]<-hstgrmfile
    # chartset[["qqplt"]]<-qqpltfile
    # chartset[["brchrt"]]<-barpltfile
    # USL[[as.character(i)]][["freqtbl"]]
    # chrtlst[[as.character(i)]]<-chartset


    DBG<-FALSE
    #DBG<-TRUE
    if (is.null(analysisoptn)){
        analysisoptn<-c(1,1,0)
    }
    
    if (is.null(imgflprfx)) {
        PRCID<-format(Sys.time(), "R%Y%m%d_%H%M%S")
        #imgflprfx<-paste("c:/asone/R/temp/",PRCID,sep="")
        imgflprfx<-PRCID
        if (DBG) {cat("\nprocessID=",imgflprfx,"\n", sep="")}
    }



    # function defintions



varlabel.chrt<-function(lblset){
    DBG<-FALSE
    #DBG<-TRUE
    # variable label processing
    if (DBG) {cat("\nEntered varlabel.chrt\n")}

    if (nchar(lblset[["varlabel"]])>45) {
        varlabel<- paste(substr(lblset[["varlabel"]], 1, 45), "...")
    } else {
        varlabel<-lblset[["varlabel"]]
    }
    lblset[["varlabel"]]<-paste(lblset[["varname"]], ": ", varlabel, sep="")
    lblset
}

    

univarChart.cntn<-function(varseti, imgflprfx, labelset) {
    DBG<-FALSE
    #DBG<-TRUE
    chartset<-list()

    if (DBG) {cat ("univarChart.cntn:varname:", labelset[["varname"]], "\n")}

    #histgram/boxplot
    hstgrmfile<-paste(imgflprfx, labelset[["varname"]],"hs.jpg", sep=".")
    bitmap(hstgrmfile, type = "jpeg", height = 3.5, width = 3, res=100, pointsize=9)

    layout(matrix(c(1,2),nrow=2,ncol=1), widths=c(1), heights=c(5,1))
    par(mar=c(4,4,1,1), mgp=c(2, 0.5, 0), tcl=-0.25, cex.axis=0.9, cex.lab=0.9)

    hist(varseti, main="", xlab=labelset[["varlabel"]], col="lightgrey")

    par(mar=c(2,4,0,1))
    boxplot(varseti, main="", xlab="", ylab="", col="lightgrey", horizontal=T)

    dev.off()
    #par(def.par)
    
    if (!standalone){
        tmpvsldirhs<-unlist(strsplit(hstgrmfile,"/"))
        hstgrmfile<-paste(tmpvsldirhs[(length(tmpvsldirhs)-1):length(tmpvsldirhs)],collapse="/")
    }
    chartset[["hstbx"]]<-hstgrmfile

    #qq-plot
    qqpltfile<-paste(imgflprfx, labelset[["varname"]],"qq.jpg", sep=".")
    bitmap(qqpltfile, type = "jpeg", height = 3, width = 3, res= 100, pointsize=8.5)

    par(tcl=-0.25, cex.axis=0.9, cex.lab=1.0)
    qqnorm(varseti, main="Normal Q-Q Plot", ylab=labelset[["varlabel"]], pch=15)
    qqline(varseti)
    dev.off()
    #par(def.par)
    if (!standalone){
        tmpvsldirqq<-unlist(strsplit(qqpltfile,"/"))
        qqpltfile<-paste(tmpvsldirqq[(length(tmpvsldirqq)-1):length(tmpvsldirqq)],collapse="/")
    }
    chartset[["qqplt"]]<-qqpltfile
    chartset
}
    
univarChart.dscrt<-function(frqtbl, imgflprfx, labelset){
    DBG<-FALSE
    #DBG<-TRUE
    chartset<-list()
    if (DBG) {cat ("univarChart.dscrt:varname:", labelset[["varname"]], "\n")}

    barpltfile<-paste(imgflprfx, labelset[["varname"]], "bp.jpg", sep=".")
    bitmap(barpltfile, type = "jpeg", height = 3, width = 3, res= 100, pointsize=8.5)
    par(tcl=-0.25, cex.axis=0.9, cex.lab=1.0)
    barplot(frqtbl, col="lightgrey", main="", xlab=labelset[["varlabel"]], ylab="Frequency")
    dev.off()
    #par(def.par)
    
    if (!standalone){
        tmpvsldirbp<-unlist(strsplit(barpltfile,"/"))
        barpltfile<-paste(tmpvsldirbp[(length(tmpvsldirbp)-1):length(tmpvsldirbp)],collapse="/")
    }

    chartset[["brchrt"]]<-barpltfile
    chartset
}
    
    ############################
    # implementation
    
    
    varlabels<-attr(dtfrm,"var.labels")
    varnames<-names(dtfrm)
    vartypes<-attr(dtfrm,"var.type")
    
    
    STATLST<-NULL
    if (!is.null(attr(dtfrm,"univarStat.lst"))) {
        STATLST<-attr(dtfrm,"univarStat.lst")
    }
    
    chrtlst<-list()
    for (i in 1: dim(dtfrm)[2]){
    try( {
        if (DBG) {cat("univarChart:",i,"-th var\n")}
        chrtlbl<-list(varname=varnames[i], varlabel=varlabels[i])
        labelset<-varlabel.chrt(lblset=chrtlbl)
        
        varseti<-dtfrm[[i]]

        if (is.null(STATLST[[as.character(i)]])) {
            tmpvald<-sum(complete.cases(varseti))
        } else {
            tmpvald<-STATLST[[as.character(i)]][["Vald"]]
        }
        if (DBG) {cat("tmpvald=",tmpvald,"\n")}
        
        chartset<-list()

        if (vartypes[i]==2) {
            #Continuous Variable
            if (analysisoptn[2] & tmpvald) {
                chrtlst[[as.character(i)]]<-univarChart.cntn(varseti=varseti, imgflprfx=imgflprfx, labelset=labelset)
            }
        } else {
            #Discrete Variable
            #bar plot
            if (analysisoptn[2] & tmpvald ) {
            
                # chart option is chosen
                if (analysisoptn[1]){
                    # univariate statistics option is chosen -> freq table is available
                    # note: univariate statistics option is not chosen, tmpfrqtbl is NA
                    tmpfrqtbl<-STATLST[[as.character(i)]][["freqtbl"]]
                } else {
                    # calculate statistics
                    if (vartypes[i]==1) {
                        statlst<-univarStat.dscrt(varseti=varseti)
                    } else {
                        statlst<-univarStat.dscrt(varseti=varseti,ordnl=FALSE)
                    }
                    tmpfrqtbl<-statlst[["freqtbl"]]
                }
                
                chartset[["brchrt"]]<-NA
                if( (length(tmpfrqtbl)<=10) & (length(tmpfrqtbl)>1) ) {
                    chartset<-univarChart.dscrt(frqtbl=tmpfrqtbl, imgflprfx=imgflprfx, labelset=labelset)
                } else if (class(tmpfrqtbl)=="table") {
                    # number of categories <= 50
                    # no chart but table
                    if (!analysisoptn[1]){
                        STATLST[[as.character(i)]]<-statlst
                    }
                } else if (is.na(tmpfrqtbl)) {
                    # no table available
                    if (!analysisoptn[1]){
                        STATLST[[as.character(i)]]<-statlst
                    }
                }
                chrtlst[[as.character(i)]]<-chartset
            }
        } # end of D case
    }) # end of try
    } # end of var-wise-loop
    attr(dtfrm, "univarChart.lst")<-chrtlst
    
    if (is.null(attr(dtfrm,"univarStat.lst")) ) {
        attr(dtfrm,"univarStat.lst")<-STATLST
    }
    
    invisible(dtfrm)    
} # end of univarChart
#######################################################################
univarStatHtml<-function(dtfrm, tmpimgfile, analysisoptn, tmphtmlfile, standalone=T){
    # Description
    # 
    # arguments
    # dtfrm          variable furnished with attributes
    # tmpimgfile    temporary image file prefix: =$SRVRCGI=$SERVER$CGIDIR
    # analysisoptn  analysis option
    # nrows         local variable
    # tmphtmlfile   temporary html file
    # file          tmphtmlfile 
    
    DBG<-TRUE
    DBG<-FALSE

    # open the connection
    whtml<-file(tmphtmlfile, "w")
    on.exit(close(whtml))
    
    # color parameters
    # legend: c(1:background, 2:table header, 3: table body(o), 4: table body(e))
    # clschm <-c("#FFFFFF", "#CCFFCC","#e0ffff","#f0fff0") # green-based palette
    # blue-based palette
    #clschm <-c("#FFFFFF", "#e6e6fa","#ffffff","#f5f5f5")
    clschm <-c("dvnUnvStatTbl", "dvnUnvStatTblHdr","dvnUnvStatTblRowO","dvnUnvStatTblRowE")
    
    # table parameters
    # legend: c(border, cellspacing)
     tblprm <-c(0, 2)
    
    #cat("\nEntered the function univarStatHtml\n")
    
    # values for local tests
    # set localtest 0 after local tests
    localtest<-TRUE
    localtest<-FALSE
    if (localtest){
        tmpimgfile<-c("")
        imgprfx1<-c("<img src=\"")
        imgprfx2<-c("")
        univarstathdr<-c("Valid Cases", "Invalid Cases(NAs)", "Total", "Mean", "Standard deviation", "Skewness", "Kurtosis", "Coefficient of variation", "Mode", "Minimum","1st Quartile","Median","3rd Quartile","Maximum","Range","Interquartile Range","Normality Test:Shapiro-Wilk Statistic", "(Shapiro-Wilk Statistic: p value)")
    }
    if (standalone) {
        imgflprfx<-paste(imgprfx1,tmpimgfile,imgprfx2,sep="")
    } else {
        imgflprfx<-"<img src=\""
    }
    # constant for rendering a table for univariate statistics(continuous vars only)
    uslstlen<-length(univarstathdr)
    nrows <-ceiling(uslstlen/2)
    blnkcell<-uslstlen%%2==TRUE
    
    
    nameset<-names(dtfrm)
    varlabelset<-attr(dtfrm,"var.labels")
    CHRTLST<-attr(dtfrm, "univarChart.lst")
    STATLST<-attr(dtfrm, "univarStat.lst")
    VARTYPE<-attr(dtfrm, "var.type")
    VALINDEX<-attr(dtfrm, "val.index")
    VALTABLE<-attr(dtfrm, "val.table")
    
    
    pt.varheader<-function(namesi, varlabelsi=NA) {h3<-paste("<h3>", namesi, if (!is.na(varlabelsi)) {paste(": ", varlabelsi, sep="")}, "</h3>\n",sep="");h3}

    ###################
    # continuous case
    univarStatHtml.cntn<-function(statlst, imgfllst, cmbntn, namesi, varlabelsi){

        # statlst   STATLST[[as.character(i)]]
        # imgfllst  imgfllst=CHRTLST[[as.character(i)]]
        # cmbntn    analysisoptn
        # function definition sections

        # create the first tr tag: chart part
        pt.tr1<-function(imgfllst, cmbntn){
            tr1<-""
            if (cmbntn[2]) {

                if (cmbntn[1]) { colspan<-" colspan=\"2\"" } else { colspan<-""}

                # both

                if(!is.null(imgfllst[["hstbx"]])){
                    tr1.l<-paste("<td",colspan,">\n",imgflprfx,imgfllst[["hstbx"]],imgsffx1,"</td>\n",sep="")
                } else {
                    tr1.l<-paste("<td",colspan,">\n<p><B><font color=red>Histogram/Boxplot Not Available</font></B></p>\n</td>\n")
                }

                if(!is.null(imgfllst[["qqplt"]])) {
                    tr1.r<-paste("<td",colspan,">\n",imgflprfx,imgfllst[["qqplt"]],imgsffx1,"</td>\n",sep="")
                } else {
                    tr1.r<-paste("<td",colspan,">\n<p><B><font color=red>Normal Q-Q plot Not Available</font></B></p>\n</td>\n",sep="")
                }

                tr1<-paste("<tr>\n",tr1.l,tr1.r,"</tr>\n",sep="")
            }
            tr1
        }

        # create the 2nd and thereafter tr tags: statistics part
        pt.tr2<-function(statlst, cmbntn){
            tr2<-""
            if (cmbntn[1]) {
                # statistics on
                # table header
                tr2<-paste("<tr class=\"",clschm[2],"\">\n<td align=\"left\"><b>Statistic</b></td><td align=\"right\"><b>Value</b></td>\n<td align=\"left\"><b>Statistic</b></td><td align=\"right\"><b>Value</b></td>\n</tr>\n",sep="")

                # statistical data
                # when # of statistics is not even
                if (blnkcell){ univarstathdr[length(statlst)+1]<-"&nbsp;"}

                # table body
                for (j in 1:nrows) {
                    if (j%%2==FALSE) colorprm <- clschm[3] else colorprm <-clschm[4]

                    tr2<-paste(tr2, 
                    "<tr class=\"",colorprm,"\">\n",
                    "<td align=\"left\">",univarstathdr[j],"</td>\n", 
                    "<td align=\"right\">", prettyNum(statlst[[j]]),"</td>\n", 
                    "<td align=\"left\">",univarstathdr[j+nrows],"</td>\n", 
                    "<td align=\"right\">", if ( (j==nrows) & (blnkcell) ) {"&nbsp;"} else {prettyNum(statlst[[j+nrows]])},"</td>\n</tr>\n", sep="")
                }
            }
            tr2
        }

        # create the chart/statistics table segment
        pt.tbl<-function(statlst=statlst,cmbntn=cmbntn,imgfllst=imgfllst){
            tr1<-pt.tr1(imgfllst=imgfllst, cmbntn=cmbntn)
            tr2<-pt.tr2(statlst=statlst, cmbntn=cmbntn)
            tbl<-paste("<center>\n<table border=\"",tblprm[1],"\" class=\"",clschm[1],"\" cellspacing=\"",tblprm[1],"\" >\n",tr1,tr2,"</table>\n</center>\n",sep="")
            tbl
        }

        # create per variable html segment
        pt.varunit.cntn<-function(vhdr,vcntnts){varunit<-paste(vhdr,vcntnts,"<hr/>", sep="");varunit}
        ## end of function definitions ##

        # implementation

        pttbl<-pt.tbl(statlst=statlst, imgfllst=imgfllst, cmbntn=cmbntn)
        ptvarheader<-pt.varheader(namesi=namesi, varlabelsi=varlabelsi)
        ptvarunitc<-pt.varunit.cntn(vhdr=ptvarheader, vcntnts=pttbl)

        ptvarunitc
    } # end of continuous case
    
    
    ######################
    # discrete case

    univarStatHtml.dscrt<-function(statlst, imgfllst, cmbntn, namesi, varlabelsi, vltbl) {
        # statlst   STATLST[[as.character(i)]]
        # imgfllst  imgfllst=CHRTLST[[as.character(i)]]
        # cmbntn    analysisoptn
        # function definition sections

        #statlst[["freqtbl"]]
        # mode and median even if a freq table is not available 
        nrw<-3
        # add one for "total" row
        #if (!is.na(statlst$freqtbl)) {nrw<-length(statlst$freqtbl)+1+nrw}

        if (class(statlst$freqtbl)=="table") {nrw<-length(statlst$freqtbl)+nrw}
        # nrws: rowspan parameter value if the chart option is chosen
        nrws<-nrw+1

        pt.tr1<-function(imgfllst, cmbntn){
            try({
            # tr1.l: chart part
            tr1.l<-""
            sprsstr1r<-FALSE
            if (cmbntn[2]) {
                rowspan<-""
                if (cmbntn[1]) { rowspan<-paste(" rowspan=\"",nrws,"\"",sep="") }

                if(!is.na(imgfllst[["brchrt"]])){
                    tr1.l<-paste("<td",rowspan," valign=\"top\">\n",imgflprfx,imgfllst[["brchrt"]], imgsffx1, "</td>\n", sep="")
                } else {
                    if (class(statlst$freqtbl)=="table"){
                        rowspan<-paste(" rowspan=\"",nrws,"\"",sep="")
                        tr1.l<-paste("<td",rowspan," valign=\"top\">\n<p><B><small>The number of categories is more than 10 or equal to 1.<br>Table substitutes for Bar plot</small></B></p>\n</td>\n",sep="")
                        cmbntn[1]<-1
                    } else {
                        tr1.lm<-paste("<td align=\"left\" colspan=\"3\" valign=\"top\">\n<p><B><small>The number of categories is more than 50. Frequency/Percentage tables are not shown here</small></B></p>\n</td></tr>\n",sep="")
                        
                        tr1.lhdr<-paste("<tr><td align=\"left\" class=\"",clschm[2],"\" ><b>Value: Value Label</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Freq</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Percent</b></td>\n",sep="")
                        
                        tr1.l<-paste(tr1.lm,tr1.lhdr, sep="")
                        
                        sprsstr1r<-TRUE
                    }
                }
            }
            # tr1.r: freq/pcnt table header part
            tr1.r<-""
            if (cmbntn[1]) {
                if (class(statlst$freqtbl)=="table"){
                    tr1.r<-paste("<td align=\"left\" class=\"",clschm[2],"\" ><b>Value: Value Label</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Freq</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Percent</b></td>\n",sep="")
                } else if (!sprsstr1r){
                    tr1.rm<-paste("<td align=\"left\" colspan=\"3\" valign=\"top\">\n<p><B><small>The number of categories is more than 50. Frequency/Percentage tables are not shown here</small></B></p>\n</td></tr>\n",sep="")
                    
                    tr1.rhdr<-paste("<tr><td align=\"left\" class=\"",clschm[2],"\" ><b>Value: Value Label</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Freq</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Percent</b></td>\n",sep="")
                    
                    tr1.r<-paste(tr1.rm,tr1.rhdr, sep="")
                }
            }
            tr1<-paste("<tr>\n",tr1.l,tr1.r,"</tr>\n",sep="")
            }) # end of try
        }

        # create the 2nd and thereafter tr tags: statistics part
        pt.tr2<-function(statlst, cmbntn, vltbl, imgfllst){
            try({
            tr2<-""
            tableon<-FALSE
            if ( cmbntn[2]){
                if (is.na(imgfllst[["brchrt"]])){
                    tableon<-TRUE
                }
            }
            if (cmbntn[1] | tableon) {

                if (class(statlst$freqtbl)=="table") {tblkey<-names(statlst$freqtbl)}
                # if freqtbl is NA, tblkey becomes NULL
                for (j in 1:nrw) {
                    if (j%%2==FALSE) { colorprm <- clschm[3]} else {colorprm <-clschm[4]}
                    if (j < (nrw -2)) {

                        catgrylbl<-""
                        if (!is.null(vltbl)){
                            if(!is.null(vltbl[[tblkey[j]]])) {
                                catgrylbl<-paste("(",vltbl[[tblkey[j]]],")",sep="")
                            }
                        }
                        tr2<-paste(tr2, "<tr class=\"",colorprm,"\">\n<td align=\"left\">",tblkey[j],catgrylbl,"</td>\n<td align=\"right\">",statlst$freqtbl[[j]],"</td>\n<td align=\"right\">", signif(statlst$pcnttbl[[j]],3),"</td>\n</tr>\n", sep="")

                    } else if (j == (nrw -2)) {
                        #cat("entering the total row\n")
                        tr2<-paste(tr2, "<tr class=\"",colorprm,"\">\n<td align=\"left\">Total</td>\n<td align=\"right\">",statlst$Vald+statlst$Invald,"</td>\n<td align=\"right\">100</td>\n</tr>\n", sep="")

                    } else if (j == (nrw -1)) {
                        # median
                        #cat("entering the median\n")
                        median.vl<- "Not Available"
                        median.lbl<-""
                        if (!is.null(statlst$Median)) {
                            median.vl<- as.character(statlst$Median)
                            if (!is.null(vltbl) && (nrw>3)){
                                if (!is.null(vltbl[[median.vl]])) {
                                    median.lbl<-paste("(",vltbl[[median.vl]],")",sep="")
                                }
                            }
                        }

                        tr2<-paste(tr2,"<tr class=\"",colorprm,"\">\n<td align=\"left\">Median</td>\n<td align=\"right\">",median.vl,"</td>\n<td align=\"right\">",median.lbl,"</td>\n</tr>\n", sep="")

                    } else if (j == nrw) {
                        # mode
                        #cat("entering the Mode\n")
                        mode.vl<-"Not Available"
                        mode.lbl<-""
                        if (!is.null(statlst$Mode)) {
                            mode.vl<-statlst$Mode
                            if (!is.null(vltbl) && (nrw>3) ) {
                                if (!is.null(vltbl[[mode.vl]])) {
                                    mode.lbl<-paste("(",vltbl[[mode.vl]], ")", sep="")
                                }
                            }
                        }

                        tr2<-paste(tr2,"<tr class=\"",colorprm,"\">\n<td align=\"left\">Mode</td>\n<td align=\"right\">",mode.vl,"</td>\n<td align=\"right\">",mode.lbl,"</td>\n</tr>\n", sep="")
                    }
                }
            }
            tr2
            }) # end of try
        }

        # create the chart/statistics table segment
        pt.tbl<-function(statlst=statlst,cmbntn=cmbntn,imgfllst=imgfllst,vltbl=vltbl){
            try({
            tr1<-pt.tr1(imgfllst=imgfllst, cmbntn=cmbntn)
            tr2<-pt.tr2(statlst=statlst, cmbntn=cmbntn, vltbl=vltbl,imgfllst=imgfllst)
            tbl<-paste("<center>\n<table border=\"",tblprm[1],"\" class=\"",clschm[1],"\" cellspacing=\"",tblprm[1],"\" >\n",tr1,tr2,"</table>\n</center>\n",sep="")
            tbl
            })
        }

        # create per variable html segment
        pt.varunit.dscrt<-function(vhdr,vcntnts){varunit<-paste(vhdr,vcntnts,"<hr/>", sep="");varunit}
        
        ## end of function definitions ##


        # implementation
        try({
        #cat("enters the discrete html body function\n", sep="")
        pttbl<-pt.tbl(statlst=statlst, imgfllst=imgfllst, cmbntn=cmbntn, vltbl=vltbl)

        ptvarheader<-pt.varheader(namesi=namesi, varlabelsi=varlabelsi)
        ptvarunitd<-pt.varunit.dscrt(vhdr=ptvarheader, vcntnts=pttbl)

        ptvarunitd
        })
    } # end of discrete case
    
    
    
    # main 
    # implementation
        rawVarName <- nameset
        if (length(attr(dtfrm, "Rsafe2raw"))>0){
            Rsafe2raw <- attr(dtfrm, "Rsafe2raw")
            for (i in 1:length(nameset)){
                if (!is.null(Rsafe2raw[[nameset[i]]])){
                    rawVarName[i] <-  Rsafe2raw[[nameset[i]]];
                }
            }
        }
    
    for (i in 1:dim(dtfrm)[2]){
        try({
        if (VARTYPE[i]==2) {
            varsgmnt.c<-univarStatHtml.cntn(statlst=STATLST[[as.character(i)]], imgfllst=CHRTLST[[as.character(i)]], cmbntn=analysisoptn, namesi=rawVarName[i], varlabelsi=varlabelset[i])
            cat(file=whtml, varsgmnt.c, sep="")
        } else {
            if (DBG) {cat(i,"-th var before entering the discrete html function\n", sep="")}
            #cat("check the value table=",VALTABLE[[VALINDEX[[i]]]],"\n", sep="")
            if (is.null(VALINDEX[[as.character(i)]])){valtable<-NULL} else {valtable<-VALTABLE[[VALINDEX[[as.character(i)]]]]}
            varsgmnt.d<-univarStatHtml.dscrt(statlst=STATLST[[as.character(i)]], imgfllst=CHRTLST[[as.character(i)]], cmbntn=analysisoptn, namesi=rawVarName[i], varlabelsi=varlabelset[i], vltbl=valtable)
            cat(file=whtml, varsgmnt.d, sep="")
        }
        }) # end of try
    } # end of var-wise for-loop
    

} #end of the function univarStatHtml


###########################################################
univarDataDwnld<-function(dtfrm, dwnldoptn, dsnprfx) {
    # dtfrm(=z1)        dataset to be downloaded
    # dwnldoptn(=z2)    data download option
    # dsnprfx(=z3)      dataset name prefix
    
    
    if (dwnldoptn == 'D01') {
        # In the future when a sample program file is attached to
        # a text file, col.names should be set to F to avoid
        # printing a variable list
        write.table(dtfrm, file=dsnprfx, sep="\t", row.names=F, na=".")
    } else if (dwnldoptn == 'D02') {
        for (i in 1:length(x)) {
            if (class(x[[i]]) == 'AsIs'){
                x[[i]]<-as.character(x[[i]]);
            }
        }
        #attach(dtfrm)
        dump('x', file=dsnprfx)
        #dump(ls(dtfrm), file=dsnprfx)
        #detach(dtfrm)
    } else if (dwnldoptn == 'D03') {
        # truncate over-sized string variables
        MaxLenStringVar <- 127
        vt <- attr(dtfrm, 'var.type')
        for (i in 1:length(vt)){
            if (vt[i] == 0){
                #cat(paste(i, "-th var is char type", sep=""), "\n")
                maxlen <- max(unlist(lapply(dtfrm[[i]],nchar)))
                if (maxlen > MaxLenStringVar) {
                    #cat(paste(i, "-th var is over-sized string var", sep=""), "\n")
                    dtfrm[[i]] <- strtrim(dtfrm[[i]], MaxLenStringVar)
                }
            }
        }
        write.dta(dtfrm, file=dsnprfx, version=7)
    } else if (dwnldoptn == 'D04') {
        save(x,file=dsnprfx)
    }
} # end of univarDataDwnld.R

###########################################################
sumStatTabAll.DDI<-function(dtfrm, file="", flid=1, jobNo=0, startno=1, novars=0){

        # sumStatTabAll.DDI(dtfrm=x,file="/tmp/mry/00001/00001.stat.1.tab", flid=1)
        # arguments
        # dtfrm data frame furnished with attributes
        # file  outp file (tab-delimited)
        # flid  file ID
        # ordrDDI == 0 if no division of a job
        # constants
        entref<-c("&", "<", ">", "'", "\"")
        nmstr<-c("&amp;","&lt;", "&gt;", "&apos;","&quot;")
        sumStatset<-c("mean", "medn", "mode", "vald", "invd", "min", "max", "stdev")
        DEBUG<-FALSE

        fileid<-paste("file", flid, sep="")
        #varIDprfx<-paste("v", flid, ".", sep="")

        # open the connection
        if (file!="") {
            if (jobNo==0){
                wxml<-file(file, "w")
            } else if (jobNo >0) {
                wxml<-file(file, "a")
            }
            on.exit(close(wxml))
            
        } else {
            stop("output file name is not specified\n")
        }

        # An auxiliary function that replaces the above five characters with the named entities
        chr2xtrf<-function(lbl){
            for (i in 1:length(entref)) {
                lbl<-gsub(entref[i], nmstr[i], lbl, perl=T, useBytes = T)
            }
        }

        # xml printing up to the section 3
        #if (jobNo<= 1 ){
        #   if (jobNo){novars<-"" } else {novars<-dim(dtfrm)[2]}
        if (jobNo == 0){
            if (novars==0){novar<-"";} else if (novars > 0) {novar <-novars}
            cat(file=wxml, sep="",paste(dim(dtfrm)[1],novar,fileid,sep="\t"),"\n" )
        }
        
        
        VARTYPE<-attr(dtfrm, "var.type")
        NAMES<-names(dtfrm)
    for (i in 1: dim(dtfrm)[2]){

        #   sumStatset<-c("mean", "medn", "mode", "vald", "invd", "min", "max", "stdev")


        tmpvari<-dtfrm[[i]]
            if(DEBUG) {cat("variable name =",NAMES[i],"\n")}
            
            if (VARTYPE[i] ==0){
                # set '' to NA   tmpvari[ tmpvari == ""]<-NA; 
                tmpvari[ sub('^\\s+', '',tmpvari, perl = T,  useBytes=T)==''] <-NA
            }

            
            tbl1<-table(tmpvari, useNA='ifany')

            N<-sum(complete.cases(tmpvari))

            if (VARTYPE[i]== 2) {
                
                svnm<-summary(tmpvari)
                if (N) {
                    min.value <- svnm[[1]]
                    median.value <- median(tmpvari, na.rm=TRUE)
                    max.value <- svnm[[6]]
                    mean.value <- svnm[[4]]
                }
                else {
                    min.value <- NA
                    median.value <- NA
                    max.value <- NA
                    mean.value <- NA
                }
                stdv.value <- sd(tmpvari, na.rm=T)
                # find the maximum frequency cell
                # index: which.max(table(dtfrm[[i]]))
                maxfreq<-NA
                if (length(tmpvari) > length(tbl1)) {
                    maxfreq<- names(tbl1)[which.max(tbl1)]
                }
                statset<- list(
                    Mean = mean.value, 
                    Median = median.value, 
                    Mode.Value = maxfreq,
                    Vald = N,
                    Invald = sum(is.na(tmpvari)), 
                    Minimum = min.value, 
                    Maximum = max.value, 
                    Stdev = stdv.value
                )
                #if (length(attr(tmpvari, 'Univariate'))==0){attr(dtfrm[[i]],"Univariate") <- statset}
            } else if ( (VARTYPE[i] < 2) & (VARTYPE[i] >=0) ){
                
                if(DEBUG) {cat("pass the point univStat(discrete)\n")}
                statset<- list(
                    Vald = N,
                    Invald = (length(tmpvari) - N), 
                    Total = length(tmpvari)
                )

                if (DEBUG){cat("\ttable header:",paste(dimnames(tbl1)[[1]], collapse='|'), "\n")}
                if (DEBUG){cat("\ttable frequency:",paste(tbl1, collapse='|'), "\n")}
            } else {
                if(DEBUG) {cat("out-of-range value", i ,"-th var =", VARTYPE[i],"\n")}
            }

        # section 4
        pt.vr.1<-paste(NAMES[i],VARTYPE[i],sep='\t')
        pt.vr.st<-""
            #iadj <- (i+startno-1)
            if (VARTYPE[i] == 2) {
                # continuous variable case
                #pt.vr.st<- if (!is.na(statset[[1]])) {statset[[1]]} else {"."} 
                pt.vr.st<-"8"  
                for (j in 1:length(sumStatset)) {
                    pt.vr.st<-paste(pt.vr.st,if (!is.na(statset[[j]])) {statset[[j]]} else {"."}, sep="\t")
                }

             # if: end of continuous variable part
            } else {

                # discrete variable case

                # actual value table
                if (dim(tbl1)[1] <= 100) {
                    # integer: how many value-freq sets follw? 
                    pt.vr.st<-dim(tbl1)[1]
                    for (j in 1:(dim(tbl1)[1])) {
                        # each value-freq set
                        tmpvalue<-dimnames(tbl1)[[1]][j]
                        #if (VARTYPE[i] == 0) {tmpvalue<-chr2xtrf(tmpvalue)}
                        if (DEBUG) { cat("i=", i, "\tj=", j,"\ttmpvalue:", tmpvalue, "\n", sep="")}
                        
                        pt.vr.st<-paste(pt.vr.st,if (!is.na(tmpvalue)) {tmpvalue} else {"."}, tbl1[[j]],sep="\t")

                    }
                    
                } else {
                    # for more-than-100-category vars, print 0
                    pt.vr.st<-0
                }
                
                # for all cases, valid, invalid and N are printed
                for (k in 1:length(statset)){
                    pt.vr.st<-paste(pt.vr.st,if (!is.na(statset[[k]])) {statset[[k]]} else {"."}, sep="\t")
                }
                
                
                if (DEBUG) {cat("\n");}
            } # else: end of discrete case
            
            u <- unf(tmpvari, version=3) 
            uxml<-paste(as.character(u),"\n",sep="")
            # dump this var
            cat(file=wxml,paste(pt.vr.1,pt.vr.st,uxml,sep="\t"),sep="")

    } # end of the variable-wise loop 

} #end of the sumStatTabAll.DDIx.R


checkBinaryResponse<-function(binx){
    bnryVarTbl <-attr(table(binx), 'dimnames')[[1]];
    if (length(bnryVarTbl) == 2){
        if ((bnryVarTbl[1] == 0) && (bnryVarTbl[2]==1)){
            #cat('this variable is already 0-1\n');
        } else {
            #cat('this variable needs the conversion\n');
            #cat(paste( bnryVarTbl[1],' is recoded to 1; ', bnryVarTbl[2],' is recoded to 0;\n', sep=''));
            binx<-as.integer(binx == bnryVarTbl[1]);
        }
    }
    invisible(binx)
}


#######################################################################
univarStatHtmlBody<-function(dtfrm, whtml, analysisoptn, standalone=F){
    # Description
    # 
    # arguments
    # dtfrm          variable furnished with attributes
    # tmpimgfile    temporary image file prefix: =$SRVRCGI=$SERVER$CGIDIR
    # analysisoptn  analysis option
    # nrows         local variable
    # tmphtmlfile   temporary html file
    # file          tmphtmlfile 
    
    DBG<-TRUE
    DBG<-FALSE

    # open the connection
    #whtml<-file(tmphtmlfile, "w")
    #on.exit(close(whtml))
    
    # color parameters
    # legend: c(1:background, 2:table header, 3: table body(o), 4: table body(e))
    # clschm <-c("#FFFFFF", "#CCFFCC","#e0ffff","#f0fff0") # green-based palette
    # blue-based palette
    #clschm <-c("#FFFFFF", "#e6e6fa","#ffffff","#f5f5f5")
    clschm <-c("dvnUnvStatTbl", "dvnUnvStatTblHdr","dvnUnvStatTblRowO","dvnUnvStatTblRowE")
    
    # table parameters
    # legend: c(border, cellspacing)
     tblprm <-c(0, 2)
    
    #cat("\nEntered the function univarStatHtml\n")
    
    # values for local tests
    # set localtest 0 after local tests
    localtest<-TRUE
    localtest<-FALSE
    if (localtest){
        tmpimgfile<-c("")
        imgprfx1<-c("<img src=\"")
        imgprfx2<-c("")
        univarstathdr<-c("Valid Cases", "Invalid Cases(NAs)", "Total", "Mean", "Standard deviation", "Skewness", "Kurtosis", "Coefficient of variation", "Mode", "Minimum","1st Quartile","Median","3rd Quartile","Maximum","Range","Interquartile Range","Normality Test:Shapiro-Wilk Statistic", "(Shapiro-Wilk Statistic: p value)")
    }
    if (standalone) {
        imgflprfx<-paste(imgprfx1,tmpimgfile,imgprfx2,sep="")
    } else {
        imgflprfx<-"<img src=\""
    }
    # constant for rendering a table for univariate statistics(continuous vars only)
    uslstlen<-length(univarstathdr)
    nrows <-ceiling(uslstlen/2)
    blnkcell<-uslstlen%%2==TRUE
    
    
    nameset<-names(dtfrm)
    varlabelset<-attr(dtfrm,"var.labels")
    CHRTLST<-attr(dtfrm, "univarChart.lst")
    STATLST<-attr(dtfrm, "univarStat.lst")
    VARTYPE<-attr(dtfrm, "var.type")
    VALINDEX<-attr(dtfrm, "val.index")
    VALTABLE<-attr(dtfrm, "val.table")
    
    
    pt.varheader<-function(namesi, varlabelsi=NA) {h3<-paste("<h3>", namesi, if (!is.na(varlabelsi)) {paste(": ", varlabelsi, sep="")}, "</h3>\n",sep="");h3}

    ###################
    # continuous case
    univarStatHtml.cntn<-function(statlst, imgfllst, cmbntn, namesi, varlabelsi){

        # statlst   STATLST[[as.character(i)]]
        # imgfllst  imgfllst=CHRTLST[[as.character(i)]]
        # cmbntn    analysisoptn
        # function definition sections

        # create the first tr tag: chart part
        pt.tr1<-function(imgfllst, cmbntn){
            tr1<-""
            if (cmbntn[2]) {

                if (cmbntn[1]) { colspan<-" colspan=\"2\"" } else { colspan<-""}

                # both

                if(!is.null(imgfllst[["hstbx"]])){
                    tr1.l<-paste("<td",colspan,">\n",imgflprfx,imgfllst[["hstbx"]],imgsffx1,"</td>\n",sep="")
                } else {
                    tr1.l<-paste("<td",colspan,">\n<p><B><font color=red>Histogram/Boxplot Not Available</font></B></p>\n</td>\n")
                }

                if(!is.null(imgfllst[["qqplt"]])) {
                    tr1.r<-paste("<td",colspan,">\n",imgflprfx,imgfllst[["qqplt"]],imgsffx1,"</td>\n",sep="")
                } else {
                    tr1.r<-paste("<td",colspan,">\n<p><B><font color=red>Normal Q-Q plot Not Available</font></B></p>\n</td>\n",sep="")
                }

                tr1<-paste("<tr>\n",tr1.l,tr1.r,"</tr>\n",sep="")
            }
            tr1
        }

        # create the 2nd and thereafter tr tags: statistics part
        pt.tr2<-function(statlst, cmbntn){
            tr2<-""
            if (cmbntn[1]) {
                # statistics on
                # table header
                tr2<-paste("<tr class=\"",clschm[2],"\">\n<td align=\"left\"><b>Statistic</b></td><td align=\"right\"><b>Value</b></td>\n<td align=\"left\"><b>Statistic</b></td><td align=\"right\"><b>Value</b></td>\n</tr>\n",sep="")

                # statistical data
                # when # of statistics is not even
                if (blnkcell){ univarstathdr[length(statlst)+1]<-"&nbsp;"}

                # table body
                for (j in 1:nrows) {
                    if (j%%2==FALSE) colorprm <- clschm[3] else colorprm <-clschm[4]

                    tr2<-paste(tr2, 
                    "<tr class=\"",colorprm,"\">\n",
                    "<td align=\"left\">",univarstathdr[j],"</td>\n", 
                    "<td align=\"right\">", prettyNum(statlst[[j]]),"</td>\n", 
                    "<td align=\"left\">",univarstathdr[j+nrows],"</td>\n", 
                    "<td align=\"right\">", if ( (j==nrows) & (blnkcell) ) {"&nbsp;"} else {prettyNum(statlst[[j+nrows]])},"</td>\n</tr>\n", sep="")
                }
            }
            tr2
        }

        # create the chart/statistics table segment
        pt.tbl<-function(statlst=statlst,cmbntn=cmbntn,imgfllst=imgfllst){
            tr1<-pt.tr1(imgfllst=imgfllst, cmbntn=cmbntn)
            tr2<-pt.tr2(statlst=statlst, cmbntn=cmbntn)
            tbl<-paste("<center>\n<table border=\"",tblprm[1],"\" class=\"",clschm[1],"\" cellspacing=\"",tblprm[1],"\" >\n",tr1,tr2,"</table>\n</center>\n",sep="")
            tbl
        }

        # create per variable html segment
        pt.varunit.cntn<-function(vhdr,vcntnts){varunit<-paste(vhdr,vcntnts,"<hr/>", sep="");varunit}
        ## end of function definitions ##

        # implementation

        pttbl<-pt.tbl(statlst=statlst, imgfllst=imgfllst, cmbntn=cmbntn)
        ptvarheader<-pt.varheader(namesi=namesi, varlabelsi=varlabelsi)
        ptvarunitc<-pt.varunit.cntn(vhdr=ptvarheader, vcntnts=pttbl)

        ptvarunitc
    } # end of continuous case
    
    
    ######################
    # discrete case

    univarStatHtml.dscrt<-function(statlst, imgfllst, cmbntn, namesi, varlabelsi, vltbl) {
        # statlst   STATLST[[as.character(i)]]
        # imgfllst  imgfllst=CHRTLST[[as.character(i)]]
        # cmbntn    analysisoptn
        # function definition sections

        #statlst[["freqtbl"]]
        # mode and median even if a freq table is not available 
        nrw<-3
        # add one for "total" row
        #if (!is.na(statlst$freqtbl)) {nrw<-length(statlst$freqtbl)+1+nrw}

        if (class(statlst$freqtbl)=="table") {nrw<-length(statlst$freqtbl)+nrw}
        # nrws: rowspan parameter value if the chart option is chosen
        nrws<-nrw+1

        pt.tr1<-function(imgfllst, cmbntn){
            try({
            # tr1.l: chart part
            tr1.l<-""
            sprsstr1r<-FALSE
            if (cmbntn[2]) {
                rowspan<-""
                if (cmbntn[1]) { rowspan<-paste(" rowspan=\"",nrws,"\"",sep="") }

                if(!is.na(imgfllst[["brchrt"]])){
                    tr1.l<-paste("<td",rowspan," valign=\"top\">\n",imgflprfx,imgfllst[["brchrt"]], imgsffx1, "</td>\n", sep="")
                } else {
                    if (class(statlst$freqtbl)=="table"){
                        rowspan<-paste(" rowspan=\"",nrws,"\"",sep="")
                        tr1.l<-paste("<td",rowspan," valign=\"top\">\n<p><B><small>The number of categories is more than 10 or equal to 1.<br>Table substitutes for Bar plot</small></B></p>\n</td>\n",sep="")
                        cmbntn[1]<-1
                    } else {

                        tr1.lm<-paste("<td align=\"left\" colspan=\"3\" valign=\"top\">\n<p><B><small>The number of categories is more than 50. Frequency/Percentage tables are not shown here</small></B></p>\n</td></tr>\n",sep="")
                        
                        tr1.lhdr<-paste("<tr><td align=\"left\" class=\"",clschm[2],"\" ><b>Value: Value Label</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Freq</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Percent</b></td>\n",sep="")
                        tr1.l<-paste(tr1.lm,tr1.lhdr, sep="")
                        
                        sprsstr1r<-TRUE
                    }
                }
            }
            # tr1.r: freq/pcnt table header part
            tr1.r<-""
            if (cmbntn[1]) {
                if (class(statlst$freqtbl)=="table"){
                    tr1.r<-paste("<td align=\"left\" class=\"",clschm[2],"\" ><b>Value: Value Label</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Freq</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Percent</b></td>\n",sep="")
                } else if (!sprsstr1r){
                
                    tr1.rm<-paste("<td align=\"left\" colspan=\"3\" valign=\"top\">\n<p><B><small>The number of categories is more than 50. Frequency/Percentage tables are not shown here</small></B></p>\n</td></tr>\n",sep="")
                    
                    tr1.rhdr<-paste("<tr><td align=\"left\" class=\"",clschm[2],"\" ><b>Value: Value Label</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Freq</b></td><td align=\"right\" class=\"",clschm[2],"\" ><b>Percent</b></td>\n",sep="")
                    
                    tr1.r<-paste(tr1.rm,tr1.rhdr, sep="")
                }
            }
            tr1<-paste("<tr>\n",tr1.l,tr1.r,"</tr>\n",sep="")
            }) # end of try
        }

        # create the 2nd and thereafter tr tags: statistics part
        pt.tr2<-function(statlst, cmbntn, vltbl, imgfllst){
            try({
            tr2<-""
            tableon<-FALSE
            if ( cmbntn[2]){
                if (is.na(imgfllst[["brchrt"]])){
                    tableon<-TRUE
                }
            }
            if (cmbntn[1] | tableon) {

                if (class(statlst$freqtbl)=="table") {tblkey<-names(statlst$freqtbl)}
                # if freqtbl is NA, tblkey becomes NULL
                for (j in 1:nrw) {
                    if (j%%2==FALSE) { colorprm <- clschm[3]} else {colorprm <-clschm[4]}
                    if (j < (nrw -2)) {

                        catgrylbl<-""
                        if (!is.null(vltbl)){
                            if(!is.null(vltbl[[tblkey[j]]])) {
                                catgrylbl<-paste("(",vltbl[[tblkey[j]]],")",sep="")
                            }
                        }
                        tr2<-paste(tr2, "<tr class=\"",colorprm,"\">\n<td align=\"left\">",tblkey[j],catgrylbl,"</td>\n<td align=\"right\">",statlst$freqtbl[[j]],"</td>\n<td align=\"right\">", signif(statlst$pcnttbl[[j]],3),"</td>\n</tr>\n", sep="")

                    } else if (j == (nrw -2)) {
                        #cat("entering the total row\n")
                        tr2<-paste(tr2, "<tr class=\"",colorprm,"\">\n<td align=\"left\">Total</td>\n<td align=\"right\">",statlst$Vald+statlst$Invald,"</td>\n<td align=\"right\">100</td>\n</tr>\n", sep="")

                    } else if (j == (nrw -1)) {
                        # median
                        #cat("entering the median\n")
                        median.vl<- "Not Available"
                        median.lbl<-""
                        if (!is.null(statlst$Median)) {
                            median.vl<- as.character(statlst$Median)
                            if (!is.null(vltbl) && (nrw>3)){
                                if (!is.null(vltbl[[median.vl]])) {
                                    median.lbl<-paste("(",vltbl[[median.vl]],")",sep="")
                                }
                            }
                        }

                        tr2<-paste(tr2,"<tr class=\"",colorprm,"\">\n<td align=\"left\">Median</td>\n<td align=\"right\">",median.vl,"</td>\n<td align=\"right\">",median.lbl,"</td>\n</tr>\n", sep="")

                    } else if (j == nrw) {
                        # mode
                        #cat("entering the Mode\n")
                        mode.vl<-"Not Available"
                        mode.lbl<-""
                        if (!is.null(statlst$Mode)) {
                            mode.vl<-statlst$Mode
                            if (!is.null(vltbl) && (nrw>3) ) {
                                if (!is.null(vltbl[[mode.vl]])) {
                                    mode.lbl<-paste("(",vltbl[[mode.vl]], ")", sep="")
                                }
                            }
                        }

                        tr2<-paste(tr2,"<tr class=\"",colorprm,"\">\n<td align=\"left\">Mode</td>\n<td align=\"right\">",mode.vl,"</td>\n<td align=\"right\">",mode.lbl,"</td>\n</tr>\n", sep="")
                    }
                }
            }
            tr2
            }) # end of try
        }

        # create the chart/statistics table segment
        pt.tbl<-function(statlst=statlst,cmbntn=cmbntn,imgfllst=imgfllst,vltbl=vltbl){
            try({
            tr1<-pt.tr1(imgfllst=imgfllst, cmbntn=cmbntn)
            tr2<-pt.tr2(statlst=statlst, cmbntn=cmbntn, vltbl=vltbl,imgfllst=imgfllst)
            tbl<-paste("<center>\n<table border=\"",tblprm[1],"\" class=\"",clschm[1],"\" cellspacing=\"",tblprm[1],"\" >\n",tr1,tr2,"</table>\n</center>\n",sep="")
            tbl
            })
        }

        # create per variable html segment
        pt.varunit.dscrt<-function(vhdr,vcntnts){varunit<-paste(vhdr,vcntnts,"<hr/>", sep="");varunit}
        
        ## end of function definitions ##


        # implementation
        try({
        #cat("enters the discrete html body function\n", sep="")
        pttbl<-pt.tbl(statlst=statlst, imgfllst=imgfllst, cmbntn=cmbntn, vltbl=vltbl)

        ptvarheader<-pt.varheader(namesi=namesi, varlabelsi=varlabelsi)
        ptvarunitd<-pt.varunit.dscrt(vhdr=ptvarheader, vcntnts=pttbl)

        ptvarunitd
        })
    } # end of discrete case
    
    
    
    # main 
    # implementation
        rawVarName <- nameset
        if (length(attr(dtfrm, "Rsafe2raw"))>0){
            Rsafe2raw <- attr(dtfrm, "Rsafe2raw")
            for (i in 1:length(nameset)){
                if (!is.null(Rsafe2raw[[nameset[i]]])){
                    rawVarName[i] <-  Rsafe2raw[[nameset[i]]];
                }
            }
        }
    
    for (i in 1:dim(dtfrm)[2]){
        try({
        if (VARTYPE[i]==2) {
            varsgmnt.c<-univarStatHtml.cntn(statlst=STATLST[[as.character(i)]], imgfllst=CHRTLST[[as.character(i)]], cmbntn=analysisoptn, namesi=rawVarName[i], varlabelsi=varlabelset[i])
            #cat(file=whtml, varsgmnt.c, sep="")
            HTML(file=whtml, varsgmnt.c)
        } else {
            if (DBG) {cat(i,"-th var before entering the discrete html function\n", sep="")}
            #cat("check the value table=",VALTABLE[[VALINDEX[[i]]]],"\n", sep="")
            if (is.null(VALINDEX[[as.character(i)]])){valtable<-NULL} else {valtable<-VALTABLE[[VALINDEX[[as.character(i)]]]]}
            varsgmnt.d<-univarStatHtml.dscrt(statlst=STATLST[[as.character(i)]], imgfllst=CHRTLST[[as.character(i)]], cmbntn=analysisoptn, namesi=rawVarName[i], varlabelsi=varlabelset[i], vltbl=valtable)
            #cat(file=whtml, varsgmnt.d, sep="")
            HTML(file=whtml, varsgmnt.d)
        }
        }) # end of try
    } # end of var-wise for-loop
    

} #end of the function univarStatHtml
