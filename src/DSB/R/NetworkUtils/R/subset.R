vertex_subset <- function(g, subset_str){
    vs <- V(g)[eval(parse(text=subset_str))]
    return(subgraph(g, vs))
}
    

#Like subgraph, but takes an edgeset and returns a graph that only contains those edges
#and vertices incident to those edges.
edge_subset <- function(g, subset_str, drop_disconnected=FALSE){
    if(drop_disconnected){
        es <- E(g)[eval(parse(text=subset_str))]
        e <- get.edges(g, es)
        gc()
        vset <- V(g)[unique(c(e[,1],e[,2]))]
        return(subgraph(g,vset))
    }
    else{
        es <- E(g)[eval(parse(text=paste("!(",subset_str,")")))]
        return(delete.edges(g, es))
    }
}
