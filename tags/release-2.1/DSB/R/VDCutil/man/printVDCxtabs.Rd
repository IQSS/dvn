\name{print.VDCxtabs}
\alias{print.VDCxtabs}
\title{ print VDCxtabs}
\description{
	This is used to pretty-print VDCxtabs
}
\usage{
	\method{print}{VDCxtabs}(x, \dots,
	 wantPercentages=T, wantTotals=T, wantStats=T, nameLength=15 )
}
\arguments{
  \item{x}{ object to printed}
  \item{\dots}{ additional parameters passed to print.default}
  \item{wantPercentages} { include percentages in the output }
  \item{wantTotals} { include marginals in the output }
  \item{wantStats} { include chi-squared statistics in the output }
  \item{nameLength} { maximum length of variable labels}

}

\value{
	None
}
\examples{
	# print methods called implicitly
	VDCxtabs(Freq~.,as.data.frame(Titanic))

}
\author{
Micah Altman
\email{vdc-dev\@latte.harvard.edu}
\url{http://thedata.org}
}


\seealso{\link{VDCxtabs}}

\keyword{iplot}
\keyword{IO}
\keyword{print}
