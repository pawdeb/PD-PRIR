# MANDELBROT W R

# Paweł Dębowski

library(foreach)
library(parallel)
library(doParallel)

rm(list=ls())


# F U N K C J E 

punkt <- function(ile, xpos, ypos) {
  x <-  0
  y <-  0
  for(i in 1:ile) {
    oldx <- x
    oldy <- y
    x <-  oldx^2 - oldy^2 + xpos
    y <-  2*oldx*oldy + ypos
    
    if(sqrt(x^2 + y^2) >= 2) {
      return(0)
    }
  }
  return(1)
}

# D A N E

ile <- 200
res <-  1000
xmin = -2
xmax = 0.5
ymin = -1
ymax = 1

x <- seq(xmin, xmax, length.out=res)
y <- seq(ymin, ymax, length.out=res)
c <- outer(x,y*1i,FUN="+")


# K L A S Y C Z N I E

k <- matrix(0.0, nrow=length(x), ncol=length(y))

system.time(
  for(i in 1:res) {
    for(j in 1:res) {
      k[i,j] <- punkt(ile, Re(c[i,j]), Im(c[i,j]))
    }
  }
) # zajęło  24.09 sekundy

image(x,y,k)


# R Ó W N O L E G L E

k <- matrix(0.0, nrow=length(x), ncol=length(y))

numCores <- detectCores(logical = FALSE)
cl <- makeCluster(numCores)
registerDoParallel(cl)

pasek <- function(i) {
  pas <- matrix(0.0, nrow=res, ncol=1)
  for(j in 1:res) {
    pas[j,1] <- punkt(ile, Re(c[i,j]), Im(c[i,j]))
  }
  return(pas)
}

system.time(
  k <- foreach(i=1:res, .combine = cbind) %dopar% {
      ktmp <- pasek(i)
      ktmp
    }
) # zajęło  14.53 sekund

image(x,y,k)
