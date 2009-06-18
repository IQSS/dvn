next_measure_name <- function(g, measure){
    vattrs <- list.vertex.attributes(g)
    matches <- vattrs[grep(paste('\\b',measure,'_[0-9]+\\b',sep=''), vattrs)]
    if(!length(matches))
        next_num <- 1
    else {
        next_num <- max(
                      unlist(
                        lapply(strsplit(matches, "_"), function(x) as.numeric(x[length(x)]))
                      )
                    ) + 1
    }
    return(paste(measure, next_num, sep='_'))
}

add_pagerank <- function(tmp_g, damping=0.85){
    g_last <<- tmp_g
    e <- simpleError("Out of Bounds Error.")
    if(damping < 0 || damping > 1)
        stop(e, "Damping parameter is a probability and must be between 0 and 1")
    meas_name <- next_measure_name(tmp_g, "pagerank") 
    tmp_g <- set.vertex.attribute(tmp_g, meas_name, V(tmp_g), page.rank(tmp_g, damping=damping)$vector)
    g <<- tmp_g
    return(meas_name)
}

add_degree <- function(tmp_g){
    g_last <<- tmp_g
    meas_name <- next_measure_name(tmp_g, "degree")
    tmp_g <- set.vertex.attribute(tmp_g, meas_name, V(tmp_g), degree(tmp_g, V(tmp_g)))
    g <<- tmp_g
    return(meas_name)
}

add_unique_degree <- function(tmp_g){
    g_last <<- tmp_g
    meas_name <- next_measure_name(tmp_g, "unique_degree")
    tmp_g <- set.vertex.attribute(tmp_g, meas_name, V(tmp_g), degree(simplify(tmp_g), V(tmp_g)))
    g <<- tmp_g
    return(meas_name)
}

add_in_largest_component <- function(tmp_g){
    g_last <<- tmp_g
    meas_name <- next_measure_name(tmp_g, "in_largest_component") 

    k <- clusters(tmp_g)
    big_comp <- which(k$csize==max(k$csize))-1
    in_big_comp <- which(k$membership==big_comp)-1
    
    tmp_g <- set.vertex.attribute(tmp_g, meas_name, V(tmp_g), 0)
    tmp_g <- set.vertex.attribute(tmp_g, meas_name, in_big_comp, 1)
    g <<- tmp_g
    return(meas_name)
}

add_bonacich_centrality <- function(tmp_g, alpha=0, exo=1){
    g_last <<- tmp_g
    meas_name <- next_measure_name(tmp_g, "bonacich_centrality")

    alpha <- as.numeric(alpha)
    exo <- as.numeric(exo)
    e <- simpleError("Type Error.")
    if(is.na(alpha))
        stop(e, "alpha must be a number.")
    if(is.na(exo))
        stop(e, "exo must be a number or numeric vector.")

    tmp_g <- set.vertex.attribute(tmp_g, meas_name, V(tmp_g),
                alpha.centrality.sparse(tmp_g, alpha=alpha, exo=exo))

    g <<- tmp_g
    return(meas_name)
}

#Basically just bonpow.sparse recipe from http://igraph.wikidot.com without 
#node selection.
alpha.centrality.sparse <-
    function(graph, alpha=1, exo=1, loops=F, rescale=T, tol=.Machine$double.eps){

    ev <- evcent(g)$value
    if(alpha >= 1)
        stop("alpha must be greater than or equal to 0 and strictly less than 1.")
    if(alpha > 1/ev)
        warning(sprintf("alpha values larger than the inverse of the dominant eigenvalue cause unpredictable behavior. Consider trying a value between 0 and %f.", 1/ev)) 
    else if(alpha < 0)
        stop("alpha must be between 0 and 1.")

    require(Matrix)
    
    if(!loops) {
        graph <- simplify(graph, remove.multiple=F, remove.loops=T)
    }

    exo <- rep(exo, vcount(g))
    exo <- as.matrix(exo, nc=1)

    d <- get.adjacency(graph, sparse=T)

    id <- spMatrix(vcount(graph), vcount(graph),
                    i=1:vcount(graph), j=1:vcount(graph), rep(1, vcount(graph)))
    id <- as(id, "dgCMatrix")

    ev <- solve(id - alpha * d, tol=tol) %*% exo 

    if(rescale)
        ev <- ev/sum(ev)

    return(as.numeric(ev))
}
