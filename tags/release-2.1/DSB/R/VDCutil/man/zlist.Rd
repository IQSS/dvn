\name{zeligModelDependencies}
\alias{zeligModelDependencies}
\title{ Derivative Zelig interface functions}
\description{
	Zelig interface functions. Used by VDC DSB to  communicate with Zelig.
}
\usage{
	zeligModelDependencies(inZeligOnly=T,schemaVersion="1.1",uninstalledOnly=T, repos=NULL) 
}

\arguments{
  \item{inZeligOnly}{Flag, include only models in official Zelig distribution}
  \item{uninstalledOnly}{Flag, include only uninstalled models}
  \item{repos}{URL of default repository to use}
  \item{schemaVersion}{version of Zelig schema}
}

\value{
Use zeligModelDependencies to generate a list of package
dependencies for models. 
}

\examples{
	zeligModelDependencies(uninstalledOnly=FALSE)
}

\author{
Micah Altman
\email{thedata-users\@lists.sourceforge.net}
\url{http://thedata.org}
}


\seealso{ \link[pkg:Zelig]{zelig}, \link{printZeligSchemaInstance}}

\keyword{IO}
\keyword{print}
