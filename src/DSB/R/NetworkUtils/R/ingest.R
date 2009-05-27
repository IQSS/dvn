ingest_graphml <- function(filename){
    g <- read.graph(filename, format="graphml")
    split_file <- unlist(strsplit(filename, '.', fixed=T))
    binfile <- paste( paste(split_file[-length(split_file)], collapse='.'),
                      ".RData", sep='')
    save(g, file=binfile)
    return(binfile)
}

load_and_clear <- function(filename){
    rm(list=ls(envir=parent.frame()),envir=parent.frame())
    load(filename, envir=parent.frame())
}

dump_graphml <- function(tmp_g, filename){
    write.graph(tmp_g, file=filename, format="graphml")
    return(filename)
}
