vertex_subset <- function(tmp_g, subset_str){
    subset_str <- clean_subset(subset_str)
    cache_g()
    vs <- V(tmp_g)[eval(parse(text=subset_str))]
    tmp_g <- subgraph(g, vs)

    g <<- tmp_g

    return(c(vcount(tmp_g), ecount(tmp_g)))
}

#Like subgraph, but takes an edgeset and returns a graph that only contains those edges
#and vertices incident to those edges.
edge_subset <- function(tmp_g, subset_str, drop_disconnected=FALSE){
    subset_str <- clean_subset(subset_str)
    cache_g()
    es <- E(tmp_g)[eval(parse(text=paste("!(",subset_str,")")))]
    tmp_g <- delete.edges(tmp_g, es)

    if(drop_disconnected){
        e <- get.edges(tmp_g, E(tmp_g))
        gc()
        vset <- V(tmp_g)[unique(c(e[,1],e[,2]))]
        tmp_g <- subgraph(tmp_g, vset)
    }

    g <<- tmp_g

    return(c(vcount(tmp_g), ecount(tmp_g)))
}

clean_subset <- function(subset_str){
    subset_str <- gsub("||","|",subset_str, fixed=T)

    evil_keywords <- c("system(", "eval(", ";")
    e <- simpleError("Bad query error.")
    sapply(evil_keywords, function(k){
                            if(length(grep(k, subset_str, fixed=T)))
                                stop(e,sprintf("Query contained blacklisted string \"%s\".",k))
                          }
          );
    return(subset_str)
}
