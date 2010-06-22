#Returns the largest component of a graph as a graph object. Faster than decompose.graph
component <- function(tmp_g, num=1){
    cache_g()
	k <- clusters(tmp_g)
	comp_num <- as.numeric(names(sort(tapply(k$membership, k$membership, length), decreasing=T)[num]))
    e <- simpleError("Out of bounds error.")
    if(num > k$no)
        stop(e, sprintf("There are only %d connected components in the graph.", k$no))
	tmp_g <- subgraph(tmp_g, V(tmp_g)[(which(k$membership==comp_num)-1)])
    g <<- tmp_g 
	return(c(vcount(tmp_g), ecount(tmp_g)))
}

biconnected_component <- function(tmp_g, num=1){
    cache_g()
    bc <- biconnected.components(tmp_g)
    verts <- lapply(bc$component, function(x) unique(as.vector(get.edges(tmp_g, x))))
    num_comps <- length(verts)
    e <- simpleError("Out of bounds error.")
    if(num > length(verts))
        stop(e, sprintf("There are only %d biconnected components in the graph.", num_comps))
    lengths <- unlist(lapply(verts, length))
    s_lengths <- sort(lengths, decreasing=T)
    tmp_g <- subgraph(tmp_g, verts[[which(lengths==s_lengths[num])]])
    g <<- tmp_g
    return(c(vcount(tmp_g), ecount(tmp_g)))
}

add_neighborhood <- function(tmp_g, num=1){
    cache_g()
    load(paste(tmp_g$filestub, "_orig.RData", sep=''))
    vs <- match(V(tmp_g)$id, V(g_orig)$id)-1
    tmp_g <- subgraph(g_orig,
                unique(unlist(neighborhood(g_orig, num, vs))))
    rm(g_orig); gc()
    g <<- tmp_g
    return(c(vcount(tmp_g), ecount(tmp_g)))
}
