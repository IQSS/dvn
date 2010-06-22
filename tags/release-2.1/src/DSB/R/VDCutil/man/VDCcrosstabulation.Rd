\name{VDCcrossTabulation}
\alias{VDCcrossTabulation}
\title{VDC cross tabs, with pretty printing}
\description{
	This calculate cross tabs with marginals and proportions. 
}
\usage{
VDCcrossTabulation(data=parent.env(),classificationVars=NULL, freqVars=NULL,
  wantPercentages=T, wantTotals=T, wantStats=T, wantExtraTables=FALSE,
  HTMLfile="", ...)
}

\arguments{
  \item{data}{ data to be analyzed }
  \item{classificationVars}{ list of variables to form right hand side of formula for \code{xtabs}}
  \item{freqVars}{ optional list of variables to form left hand side of formula for \code{xtabs}}
  \item{wantPercentages} { include percentages in the output }
  \item{wantTotals} { include marginals in the output }
  \item{wantStats} { include chi-squared statistics in the output } 
  \item{wantExtraTables} { include results for aggregated tables} 
  \item{HTMLfile}{ file argument to pass to \code{HTML}}
  \item{\dots}{arguments to pass to \code{xtabs}}
}

\value{
	None. Writes HTML output to designated file.	
}
\examples{
VDCcrossTabulation(as.data.frame(Titanic),classificationVars=c("Class","Sex","Age","Survived"),freqVars=c("Freq"),wantExtraTables=TRUE) 

}
\author{
Micah Altman
\email{vdc-dev\@latte.harvard.edu}
\url{http://thedata.org}
}


\seealso{
\code{\link{xtabs}}, \code{\link{VDCxtabs}}
}

\keyword{IO}
\keyword{print}
