add_pagerank <- function(tmp_g, damping=0.85){
    tryCatch(pagerank_num <<- pagerank_num+1,
             error=function(e){pagerank_num <<- 1})
    meas_name <- paste("pagerank",pagerank_num, sep="_")
    tmp_g <- set.vertex.attribute(tmp_g, meas_name, V(tmp_g), page.rank(tmp_g, damping=damping)$vector)
    g <<- tmp_g
    return(meas_name)
}

add_degree <- function(tmp_g){
    tryCatch(degree_num <<- degree_num+1,
             error=function(e){degree_num <<- 1})
    meas_name <- paste("degree",degree_num, sep="_")
    tmp_g <- set.vertex.attribute(tmp_g, meas_name, V(tmp_g), degree(tmp_g, V(tmp_g)))
    g <<- tmp_g
    return(meas_name)
}

add_in_largest_component <- function(tmp_g){
    tryCatch(in_largest_component_num <<- in_largest_component_num+1,
             error=function(e){in_largest_component_num <<- 1})
    meas_name <- paste("in_largest_component",in_largest_component_num,sep="_")
    k <- clusters(tmp_g)

    big_comp <- which(k$csize==max(k$csize))-1
    in_big_comp <- which(k$membership==big_comp)-1
    
    tmp_g <- set.vertex.attribute(tmp_g, meas_name, V(tmp_g), 0)
    tmp_g <- set.vertex.attribute(tmp_g, meas_name, in_big_comp, 1)
    g <<- tmp_g
    return(meas_name)
}
