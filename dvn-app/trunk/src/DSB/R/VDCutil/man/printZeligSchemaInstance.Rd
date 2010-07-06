\name{printZeligSchemaInstance}
\alias{printZeligSchemaInstance}
\title{ generate list of Zelig models as single XML document }
\description{
	This is used to generate a single XML file describing the supported Zelig models
}
\usage{
	printZeligSchemaInstance(filename=NULL, serverName=NULL,vdcAbsDirPrefix=NULL)

}
\arguments{
  \item{filename}{ output file }
  \item{serverName}{ server name, for schema location}
  \item{vdcAbsDirPrefix}{ prefix for schema location, directory prefix }
}

\value{
	None. Outputs to file.
}
\examples{
	printZeligSchemaInstance(filename="",serverName="thedata.org", vdcAbsDirPrefix="/zelig/")
}
\author{
Micah Altman
\email{thedata-users\@lists.sourceforge.net}
\url{http://thedata.org}
}


\seealso{\link[pkg:Zelig]{zelig}}

\keyword{IO}
\keyword{print}
