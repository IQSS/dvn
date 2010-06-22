\name{edge_subset}
\Rdversion{1.1}
\alias{edge_subset}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{
%%  ~~function to do ... ~~
}
\description{
%%  ~~ A concise (1-5 lines) description of what the function does. ~~
}
\usage{
edge_subset(g, subset_str, drop_disconnected = FALSE)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{g}{
%%     ~~Describe \code{g} here~~
}
  \item{subset_str}{
%%     ~~Describe \code{subset_str} here~~
}
  \item{drop_disconnected}{
%%     ~~Describe \code{drop_disconnected} here~~
}
}
\details{
%%  ~~ If necessary, more details than the description above ~~
}
\value{
%%  ~Describe the value returned
%%  If it is a LIST, use
%%  \item{comp1 }{Description of 'comp1'}
%%  \item{comp2 }{Description of 'comp2'}
%% ...
}
\references{
%% ~put references to the literature/web site here ~
}
\author{
%%  ~~who you are~~
}
\note{
%%  ~~further notes~~
}

%% ~Make other sections like Warning with \section{Warning }{....} ~

\seealso{
%% ~~objects to See Also as \code{\link{help}}, ~~~
}
\examples{
##---- Should be DIRECTLY executable !! ----
##-- ==>  Define data, use random,
##--	or do  help(data=index)  for the standard data sets.

## The function is currently defined as
function (g, subset_str, drop_disconnected = FALSE) 
{
    if (drop_disconnected) {
        es <- E(g)[eval(parse(text = subset_str))]
        e <- get.edges(g, es)
        gc()
        vset <- V(g)[unique(c(e[, 1], e[, 2]))]
        return(subgraph(g, vset))
    }
    else {
        es <- E(g)[eval(parse(text = paste("!(", subset_str, 
            ")")))]
        return(delete.edges(g, es))
    }
  }
}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ ~kwd1 }
\keyword{ ~kwd2 }% __ONLY ONE__ keyword per line
