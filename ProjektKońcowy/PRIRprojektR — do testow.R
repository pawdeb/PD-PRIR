# MANDELBROT W R

# Pawel Debowski

library(foreach)
library(parallel)
library(doParallel)

rm(list=ls())



#######################
# P O R O W N A N I E #
#######################

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
) # zajelo 20 sekund

image(x,y,k)


# R O W N O L E G L E

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
) # zajelo 13 sekund

image(x,y,k)


#########################
# D O   P O R O W N A N #
#########################

wymiary <- c(32, 64, 128, 256, 512, 1024, 2048, 4096)#, 8192) bo za d³ugo
t = c()

for (w in wymiary) {
  res <- w
  print(w)
  x <- seq(xmin, xmax, length.out=w)
  y <- seq(ymin, ymax, length.out=w)
  c <- outer(x,y*1i,FUN="+")
  k <- matrix(0.0, nrow=length(x), ncol=length(y))
  
  ti <- system.time(
    k <- foreach(i=1:w, .combine = cbind) %dopar% {
      ktmp <- pasek(i)
      ktmp
    }
  )
  print(ti)
  t <- c(t,ti[3])
}

df <- data.frame(bok = wymiary, czas = t)
path <- dirname(rstudioapi::getSourceEditorContext()$path)
plik <- paste(path, "/MandelbrotR.csv", sep="")
write.csv(df,plik, row.names = FALSE)











