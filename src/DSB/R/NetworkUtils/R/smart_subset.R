#Returns the largest component of a graph as a graph object. Faster than decompose.graph
component <- function(g,num=1){
	k <- clusters(g)
	comp_num <- as.numeric(names(sort(tapply(k$membership, k$membership, length), decreasing=T)[num]))
	g1 <- subgraph(g, V(g)[(which(k$membership==comp_num)-1)])
	return(g1)
}
