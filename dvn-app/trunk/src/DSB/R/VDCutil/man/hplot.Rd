\name{hplot}
\alias{hplot}
\title{ plots an object, with html output}
\description{
	This calls plot() to plot the object, bitmap() to output it as bitmaps, and HTMLInsertGraph to
generate HTML referring to it
}
\usage{
hplot(x,y, ... , htmlFile=NULL,  graphfileBase=NULL, bitmapArgs=NULL,htmlArgs=NULL)
}
\arguments{
  \item{x}{ object to plotted }
  \item{y}{ optional object to plotted  }
  \item{\dots}{ additional parameters passed to plot.default }
  \item{htmlFile}{ location of html file to write to, will default to contents of .HTML.file or tempdir/index.html }
  \item{graphfileBase}{ base name for graph files , defaults to "graph"}
  \item{bitmapArgs}{arguments to pass to bitmap}
  \item{htmlArgs}{arguments to pass to HTMLInsertFile}
}

\value{
	None
}
\examples{
# Note: requires R2HTML
   # start HTML output
   HTMLInitFile()
   hplot(1:10) # plot(1:10) embedded as html
   
   # lm produces multiple plots, which are all captured sequentially
   hplot(lm(Employed~.,longley)) 
   #change default options
   hplot(2:10,bitmapArgs=list(type="jpeg",res=300), htmlArgs=list(GraphBorder=0,Caption="This is a Caption"))
 
   HTMLEndFile()


}
\author{
Micah Altman
\email{vdc-dev\@latte.harvard.edu}
\url{http://thedata.org}
}


\seealso{\code{\link[pkg:R2HTML]{HTMLInsertGraph}}, \code{\link{bitmap}}, \code{\link{plot}} }

\keyword{iplot}
\keyword{IO}
\keyword{print}
