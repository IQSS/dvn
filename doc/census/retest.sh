#!/bin/sh

dir=$1 

while read study url
do
  links -source "$url" > $dir/$study.out
  echo -n . 
done
echo
