add_pagerank <- function(g, d=0.85){
    V(g)$pagerank <- page.rank(g, damping=d)$vector
    return(g)
}

add_degree <- function(g){
    V(g)$degree <- degree(g, V(g))
    return(g)
}
