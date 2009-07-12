ingest_graphml <- function(filename){
    g <- read.graph(filename, format="graphml")
    #e <- simpleError("Namespace Error.")
    #if("DVN_internal_uid" %in% list.vertex.attributes(g))
    #    stop(e, "An input graph cannot have a vertex attribute called \"DVN_internal_uid\".")
    split_file <- unlist(strsplit(filename, '.', fixed=T))
    binfile <- paste( paste(split_file[-length(split_file)], collapse='.'),
                      ".RData", sep='')
    #V(g)$DVN_internal_uid <- as.numeric(V(g)) 
    save(g, file=binfile)
    return(binfile)
}

load_and_clear <- function(filename){
    rm(list=ls(envir=parent.frame()),envir=parent.frame())

    load(filename, envir=parent.frame())

    split_file <- unlist(strsplit(filename, '.', fixed=T))
    g$filestub <<- paste(split_file[-length(split_file)], collapse='.')

    g_orig <- g
    save(g_orig, file=paste(g$filestub, "_orig.RData", sep=''))
    rm(g_orig); gc()

    #save(g, file=paste(g$filestub, "_last.RData", sep=''))

    undo_on <<- FALSE
}

reset <- function(filename){
    rm(list=ls(envir=parent.frame()), envir=parent.frame())

    split_file <- unlist(strsplit(filename, '.', fixed=T))
    filestub <- paste(split_file[-length(split_file)], collapse='.')

    load(paste(filestub, "_orig.RData", sep=''))
    g <<- g_orig
    rm(g_orig); gc()

    undo_on <<- FALSE
}

dump_graphml <- function(tmp_g, filename){
    #tmp_g <- remove.vertex.attribute(tmp_g, "DVN_internal_uid")
    g$filestub <- NULL
    g <- remove.vertex.attribute(g, "id")
    write.graph(tmp_g, file=filename, format="graphml")
    return(filename)
}

dump_tab <- function(tmp_g, filename){
    #tmp_g <- remove.vertex.attribute(tmp_g, "DVN_internal_uid")
    split_file <- unlist(strsplit(filename, '.', fixed=T))
    stem <- paste(split_file[-length(split_file)], collapse='.')
    ext <- split_file[length(split_file)]

    tabs <- graph2df(tmp_g)
    vname <- paste(stem, "_verts.", ext, sep="")
    write.table(tabs$vertices, file=vname, sep="\t", row.names=FALSE)
    ename <- paste(stem, "_edges.", ext, sep="")
    write.table(tabs$edges, file=ename, sep="\t", row.names=FALSE)
    
    return(c(vname, ename))
}

graph2df <- function(g){
    vatts <- list.vertex.attributes(g)
    vdf <- data.frame(sapply(list.vertex.attributes(g),
                      function(x){get.vertex.attribute(g, x, V(g))},
                      simplify=F))

    if("name" %in% vatts)
        everts <- matrix(V(g)$name[get.edges(g, E(g))+1], nc=2)
    else
        everts <- get.edges(g, E(g))

    if(length(list.edge.attributes(g)))
        edf <- data.frame(h=everts[,1], t=everts[,2], sapply(list.edge.attributes(g),
                      function(x){get.edge.attribute(g, x, E(g))},
                      simplify=F))
    else
        edf <- data.frame(h=everts[,1], t=everts[,2])

    return( list(vertices=vdf, edges=edf) )
}
