undo <- function(){
    g <<- g_last
    return(c(vcount(g), ecount(g)))
}

can_undo <- function(){
    return(!identical(g, g_last))
}
