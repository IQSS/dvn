#Returns the largest component of a graph as a graph object. Faster than decompose.graph
component <- function(tmp_g,num=1){
	k <- clusters(tmp_g)
	comp_num <- as.numeric(names(sort(tapply(k$membership, k$membership, length), decreasing=T)[num]))
	tmp_g <- subgraph(tmp_g, V(tmp_g)[(which(k$membership==comp_num)-1)])
    g <<- tmp_g 
	return(c(vcount(tmp_g), ecount(tmp_g)))
}
