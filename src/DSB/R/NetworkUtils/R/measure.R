add_pagerank <- function(g, d=0.85){
    g$pagerank <- page.rank(g, damping=d)
    return(g)
}

add_degree <- function(g){
    g$degree <- degree(g, V(g))
    return(g)
}
