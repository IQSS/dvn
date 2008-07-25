\name{HTML.summary.zelig}
\alias{HTML.summary.zelig}
\alias{HTML.summary.zelig.strata}
\alias{HTML.summary.MCMCZelig}
\title{ pretty print zelig sim summaries}
\description{
	This is used with R2HTML to pretty-print zelig simulation summaries
}
\usage{
	HTML.summary.zelig(x,digits = getOption("digits"), print.x = FALSE, ...) 
	HTML.summary.MCMCZelig(x,digits = max(3, getOption("digits") - 3), ...) 
	HTML.summary.zelig.strata(x,subset = NULL, ...)
}
\arguments{
  \item{x}{ object to plotted }
  \item{digits}{ number of digits to print }
  \item{subset}{ strata subset }
  \item{print.x}{ include x values }
  \item{\dots}{ additional parameters passed to plot.default }
}

\value{
	None
}
\examples{
	# should be Employed~. , but bug in Zelig 2.52 
	z.out = zelig(Employed~Year,"ls",longley)
	sim.out =sim(z.out, setx(z.out))
	summary(sim.out)
	# HTML methods called implicitly
	HTML(summary(sim.out),file="")

}
\author{
Micah Altman
\email{vdc-dev\@latte.harvard.edu}
\url{http://thedata.org}
}


\seealso{
\code{\link[pkg:R2HTML]{HTML}}, \code{\link[pkg:Zelig]{sim}}
}

\keyword{iplot}
\keyword{IO}
\keyword{print}
