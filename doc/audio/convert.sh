#!/bin/sh
DESTINATIONDIR=$1

ls *.wav | sed 's/\.wav//' | while read f
do
  ../bin/lame $f.wav $DESTINATIONDIR/$f.mp3
done
