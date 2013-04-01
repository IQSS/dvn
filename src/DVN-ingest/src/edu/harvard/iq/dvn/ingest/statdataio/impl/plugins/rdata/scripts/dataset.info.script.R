types <- c()

for (col in colnames(data.set)) {
  types <- c(types, class(data.set[, col])[1])
}

list(varNames = colnames(data.set), caseQnty = nrow(data.set), dataTypes = types)