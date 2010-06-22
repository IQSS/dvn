undo <- function(){
    e <- simpleError("Undo Error.")
    if(!undo_on)
        stop(e, "Already at last undo.")
    #else
    g <<- g_last
    #load(paste(g$filestub, "_last.RData", sep=''), envir=parent.frame())
    undo_on <<- FALSE
    return(c(vcount(g), ecount(g)))
}

can_undo <- function(){
    return(undo_on)
}

cache_g <- function(){
    g_last <<- g
    #save(g, file=paste(g$filestub, "_last.RData", sep=''))
    undo_on <<- TRUE 
}
