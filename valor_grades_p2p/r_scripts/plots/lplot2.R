lplot2 <- function (x, y, labels = "*", srt = 0, tcex = 0.7, ...) 
{
    if (is.factor(x)) 
        x <- as.character(x)
    if (!missing(y)) {
        if (is.factor(y)) 
            y <- as.character(y)
    }
    if (missing(y)) {
        if (is.data.frame(x)) {
            temp <- rep(names(x), rep(nrow(x), ncol(x)))
            y <- c(unlist(x))
            x <- temp
        }
        if (is.matrix(x)) {
            temp <- rep(dimnames(x)[[2]], rep(nrow(x), ncol(x)))
            y <- c(x)
            x <- temp
        }
        if (data.class(x) == "numeric") {
            y <- x
            if (is.null(names(y))) 
                x <- format(1:length(y))
            else x <- names(y)
        }
    }
    if (is.character(x) & is.character(y)) 
        stop(" Both x and y can not be character vectors!")
    if (is.factor(labels)) 
        labels <- as.character(labels)
    if (!is.character(labels)) 
        labels <- format(labels)
    if (is.character(x)) {
        level <- unique(x)
        if (length(level) > 10) 
            srt <- 90
        xr <- match(x, level, nomatch = NA)
        xlim <- c(min(xr) - 1, max(xr) + 1)
        plot(xr, y, type = "n", xaxt = "n", xlab = "", xlim = xlim, 
            ...)
        axis(1, unique(xr), level, srt = srt)
        text(xr, y, labels, cex = tcex)
        invisible()
    }
    else {
        if (is.character(y)) {
            level <- unique(y)
            if (length(level) > 10) 
                srt <- 90
            else srt <- 0
            yr <- match(y, level, nomatch = NA)
            ylim <- c(min(yr) - 1, max(yr) + 1)
            plot(x, yr, type = "n", yaxt = "n", ylab = "", ylim = ylim, 
                ...)
            axis(2, unique(yr), level, srt = srt)
            text(x, yr, labels, cex = tcex)
            invisible()
        }
        else {
				#print("eh esse mesmo")
            plot(x, y, type = "n", ...)
            text(x, y, labels, cex = tcex, ...)
            invisible()
        }
    }
}