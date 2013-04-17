#' Write a DVN Tab File
#' Prints its required argument "data.set" to a file or connection.
#' This is used internally by DVN's R-ingester to appropriately create TAB
#' files, the DVN's archival format. Major differences between this and standard
#' write.table are that it has fewer options, and outputs Date and POSIXt
#' entries with timezone information
#' @param data.set a data.frame or matrix object to be printed
#' @param ... parameters passed to the "write.table" function
#' @return NULL (invisibly)
write.dvn.table <- function (data.set, ...) {
  # Set millisecond precision in environment, and save old state

  saved.options <- options(digits.secs = 3)

  # Return a list of indices that need to be quoted
  needs.quotes <- function (data.set, subset = NULL) {
    if (is.null(subset))
      subset <- 1:ncol(data.set)

    quotes <- c()

    for (k in subset) {
      if (is.character(data.set[, k]) || is.factor(data.set[, k]))
        quotes <- c(quotes, k)
    }

    quotes
  }

  # Define reformat method
  reformat <- function (x)
    UseMethod("reformat")

  # Default method does nothing
  reformat.default <- function (x)
    x

  # Convert POSIXt date-times to the good format
  reformat.POSIXt <- function (x) {
    attr(x, "tzone") <- "UTC"
    paste(format(x, format = "%F %H:%M:%OS"), "+0000", sep = " ")
  }

  # Convert date
  reformat.Date <- function (x)
    format(x, format = "%Y-%m-%d")

  # Determine which
  quotes <- needs.quotes(data.set)

  # Reformat each column
  for (k in 1:length(data.set)) {
    data.set[, k] <- reformat(data.set[, k])
  }

  # Write the table in the DVN's TAB file format
  write.table(data.set, quote = quotes, sep = "\t", eol = "\n", na = "", dec = ".", row.names = FALSE, col.names = FALSE, qmethod = "double", ...)

  # Restore options to previous
  options(saved.options)

  # Return NULL invisibly
  invisible(NULL)
}