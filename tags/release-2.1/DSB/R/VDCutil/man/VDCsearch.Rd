\name{VDCWebSearch}
\alias{VDCWebSearch}
\title{Launch a search for data through the VDC network}
\description{
	This launches a search for data files contained in the VDC network.
	The VDC network offers both public and private data, most available in R binary format
through the VDC extraction interface. 
}
\usage{
	VDCWebSearch(search="",host="vdc.hmdc.harvard.edu") 
}

\arguments{
  \item{search}{terms to search for , see below for examples}
  \item{host}{VDC host to search}
}

\value{
	Opens a web browser with the search results.
}

\examples{
\dontrun{
	# search for election data
	VDCWebSearch('elections') 
	# can use booleans, phrases
	VDCWebSearch('elections AND "united states"')
	# can also use fielded searches
	VDCWebSearch('author=Altman OR author=King')
	# search your local host
	VDCWebSearch('author=Colby',host="murraydata.hmdc.harvard.edu")
}
}

\author{
Micah Altman
\email{thedata-users\@lists.sourceforge.net}
\url{http://thedata.org}
}


\seealso{ \link[pkg:Zelig]{zelig}, \link{printZeligSchemaInstance}}

\keyword{IO}
\keyword{print}
