\name{HTML.VDCxtabs}
\alias{HTML.VDCxtabs}
\title{ pretty print VDCxtabs}
\description{
	This is used with R2HTML to pretty-print VDCxtabs
}
\usage{
	HTML.VDCxtabs(x,...,
	 wantPercentages=T, wantTotals=T, wantStats=T, nameLength=15)
}
\arguments{
  \item{x}{ object to converted to HTML}
  \item{\dots}{ additional parameters passed to HTML}
  \item{wantPercentages} { include percentages in the output }
  \item{wantTotals} { include marginals in the output }
  \item{wantStats} { include chi-squared statistics in the output }
  \item{nameLength} { maximum length of variable labels}

}

\value{
	None
}
\examples{
	vxt=VDCxtabs(Freq~.,as.data.frame(Titanic))
	# HTML methods called implicitly
	HTML(vxt,file="")

}
\author{
Micah Altman
\email{vdc-dev\@latte.harvard.edu}
\url{http://thedata.org}
}


\seealso{
\code{\link[pkg:R2HTML]{HTML}}, \code{\link{VDCxtabs}}
}

\keyword{iplot}
\keyword{IO}
\keyword{print}
