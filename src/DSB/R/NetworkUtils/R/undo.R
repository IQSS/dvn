undo <- function(){
    g <<- g_last
    return(c(vcount(g), ecount(g)))
}

can_undo <- function(g){
    return(!identical(g, g_last))
}
