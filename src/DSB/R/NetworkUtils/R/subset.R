vertex_subset <- function(g, subset_str){
    subset_str <- clean_subset(subset_str)
    vs <- V(g)[eval(parse(text=subset_str))]
    return(subgraph(g, vs))
}
    

#Like subgraph, but takes an edgeset and returns a graph that only contains those edges
#and vertices incident to those edges.
edge_subset <- function(g, subset_str, drop_disconnected=FALSE){
    subset_str <- clean_subset(subset_str)
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

clean_subset <- function(subset_str){
    subset_str <- gsub("||","|",subset_str, fixed=T)

    evil_keywords <- c("system(", "eval(", ";", "]")
    e <- simpleError("Bad query error.")
    sapply(evil_keywords, function(k){
                            if(length(grep(k, subset_str, fixed=T)))
                                stop(e,sprintf("Query contained blacklisted string \"%s\".",k))
                          }
          );
    return(subset_str)
}
