#
# This recodes a VDC data frame with embedded metadata attributes
# for analysis.
#
# Warning: Recoding integers as factors alters coding sequences. 
#
# TODO: Add explicit nominal/ordinal/dichotomous information to metadata, instead 
#	of assuming non-character vars are ordered

recodeVDCdf<-function(x, recodemiss=TRUE, recodenames=FALSE, recodefactors=TRUE, dropfactorlevels=FALSE, orderfactors=TRUE) {
   var.labels = attr(x, "var.labels")
   val.table = attr(x,"val.table")
   val.index = attr(x,"val.index")
   val.list = attr(x,"val.list")
   var.type = attr(x,"var.type")
   missval.index=attr(x,"missval.index")
   missval.table=attr(x,"missval.table")
   if (recodenames) {
	tnames=paste(names(x),var.labels,sep=": ")
	names(x)=tnames
   } else {
	attr(x,"var.labels")=var.labels
   }
   for (i in 1:length(x)) {
	if (!is.null(var.type) && var.type[i]<2 && recodefactors) {
		if (is.null(val.index[[as.character(i)]])) {
			vti=NULL
		} else {
			vti=val.table[[val.index[[as.character(i)]]]]
		}
		if (dropfactorlevels) {
			vtilevels=NULL
		} else {
			if (is.numeric(x[[i]])) {
				vtilevels=as.numeric(names(vti))
			} else {
				vtilevels=names(vti)
			}
		}

		vlevsi = as.list(sort(unique.default(c(x[[i]],vtilevels))))
		names(vlevsi)=vlevsi
		tmatch=na.omit(match(names(vti),names(vlevsi)))
		if (length(tmatch)>0) {
			names(vlevsi)[tmatch]=vti
		}
		mti=integer(0);
		if (recodemiss && !is.null(missval.index[[as.character(i)]])) {
			mti=missval.table[[missval.index[[as.character(i)]]]]
			tmatch=na.omit(match(mti,vlevsi))
			if (length(tmatch)>0) {
				vlevsi[tmatch]=NULL
			}
		}
		# TODO: Add explicit nominal/ordinal/dichotomous information to metadata, instead 
		#	of assuming non-character vars are ordered
		x[[i]]=factor(x[[i]],levels=vlevsi,labels=names(vlevsi),
			 ordered=(orderfactors && var.type[i]>0 && ((length(vlevsi)-length(mti)>2))))
	}
    }
    return(x)
}
