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
