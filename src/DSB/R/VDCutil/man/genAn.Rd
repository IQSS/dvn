\name{VDCgenAnalysis}
\alias{VDCgenAnalysis}
\title{A generalized analysis framework used by the VDC system}
\description{
	This runs a statisical analysis through Zelig and summarizes the results
as html output. Options include simulation results, sensitivity analysis, and plots.
}
\usage{
VDCgenAnalysis(
      formula,model,data, by=NULL,                 
      setxArgs=NULL, setx2Args=NULL, simArgs=NULL,zeligArgs=NULL, 
      sensitivityArgs=list("ptb.s"=sensitivityQ),
      HTMLInitArgs=list(Title="VDC Analysis"),
      HTMLbodyHeading = HTMLInitArgs$Title,
      HTMLnote= "<em>The following are the results of your requested analysis.</em>",
      naMethod="exclude",                            
      sensitivityQ=NULL,
      outDir=tempdir(),                           
      wantSummary=T,wantPlots=F,wantSensitivity=F,wantSim=F,
      wantBinOutput=F,    
      debug=F)     
}

\arguments{
  \item{formula}{ formula to be passed to \code{Zelig} }
  \item{model}{ model to be passed to \code{Zelig} }
  \item{data}{ data for analysis, passed to \code{Zelig} }
  \item{by}{ strata, passed to \code{Zelig} }
  \item{setxArgs}{ argument list to pass to \code{setx} }
  \item{setx2Args}{ argument list, if present,  setx is called a second time }
  \item{simArgs}{ argument list, passed to \code{sim}}
  \item{zeligArgs}{ argument list, passed to \code{zelig}, which in turn passes any non-zelig arguments to the model function itself }
  \item{sensitivityArgs}{ argument list, passed to \code{pzelig}}
  \item{HTMLInitArgs}{ argument list, passed to \code{HTMLInitFile}}
  \item{HTMLbodyHeading}{ mainh heading for html page }
  \item{HTMLnote}{ note text below main heading }
  \item{naMethod}{ if omit \code{na.omit} is used , if exclude \code{na.exclude} is used }
  \item{sensitivityQ}{ q-value passed to sensitivity analysis }
  \item{outDir}{directory in which to create output files} 
  \item{wantSummary}{Flag, summary output wanted} 
  \item{wantPlots}{Flag, plot output wanted} 
  \item{wantSensitivity}{Flag, sensitivity analysis wanted} 
  \item{wantSim}{Flag, simulation output wanted}
  \item{wantBinOutput}{Flag, binary output wanted, in addition to html}
  \item{debug}{debugging flag}
}

\note{
	Only formula, model and data are required. Everything else can be defaulted.
	And in the formula if you use \code{list()} to enclose multiple outcome variables
	the appropriate zelig special will automatically be supplied.

        VDCxtabs will call \code{recodeVDCdf} to apply VDC metadata attributes for labels, and missing v
alues.
}

\value{
	The paths to html index file and the binary save file.
}
\examples{
# using required fields and defaults, output will be summary html
VDCgenAnalysis(Employed~Year,"ls",longley)

# shows additional output options, passing arguments to sim() 
# and through zeligArgs to lm()


# should be Employed~. but Zelig 2.52 currently has a bug in setx wrt to dots
VDCgenAnalysis(Employed~GNP.deflator+GNP+Unemployed+Armed.Forces+Population+Year,"ls",longley,   
        wantPlots=TRUE,  wantSim=TRUE, wantBinOutput=TRUE, wantSensitivity=TRUE,
        naMethod="omit",
        sensitivityQ=0.95,
        simArgs = list(num=c(2000,200)),
	setxArgs = list(Year=1970),
	setx2Args = list(Year=1990),
        zeligArgs = list(singular.ok=TRUE)
        )

}
\author{
Micah Altman
\email{vdc-dev\@latte.harvard.edu}
\url{http://thedata.org}
}


\seealso{
\code{\link{hplot}}, 
\code{\link[pkg:R2HTML]{HTML}}, 
\code{\link[pkg:accuracy]{pzelig}}, 
\code{\link[pkg:Zelig]{zelig}}, 
}

\keyword{iplot}
\keyword{IO}
\keyword{print}
