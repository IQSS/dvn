\name{recodeVDCdf}
\alias{recodeVDCdf}
\title{Recode's VDC data frame using embedded attributes.}
\description{
	VDC embeds variable types, labels, value labels, and additional missing value codes in
attributes. This method recodes a VDC data frame for analysis in R.
}
\usage{
recodeVDCdf(x, recodemiss=TRUE, recodenames=FALSE, recodefactors=TRUE, dropfactorlevels=FALSE, orderfactors=TRUE)

}

\arguments{
  \item{x}{ VDC data frame to be recoded }
  \item{recodemiss}{ recode VDC missing data codes to NA's in the data frame}
  \item{recodenames}{ recode VDC variable labels to var names in the data frame}
  \item{recodefactors}{ recode discrete numeric and character variables to factors, and apply VDC value labels as factor labels}
  \item{dropfactorlevels}{ drop unused factor levels in recoding }
  \item{orderfactors}{ use heuristic to determine when to recode to ordered factors. If false, will return unordered factors only}
}

\note{
Warning: Recoding integers as factors alters coding sequences. This does not affect most 
analysis (in fact, most zelig models expect factors). However recoded data frames should not
be saved, as information is lost. Use the original VDC data frame.

Also, we use a heuristic to determine whether a factor is ordered. This works in most 
cases but can mistakenly treat integer variables with more than 3 real categories as ordered. 

}

\value{
	Returns recoded data frame if VDC metadata is available, unaltered data frame otherwise.
}
\author{
Micah Altman
\email{vdc-dev\@latte.harvard.edu}
\url{http://thedata.org}
}


\seealso{
\code{\link{VDCgenAnalysis}}, 
}

\keyword{iplot}
\keyword{IO}
\keyword{print}
