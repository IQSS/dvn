#!/bin/bash

mvn clean compile
#cp -r ../RNAi_db target/classes
#cp -r ../test_env target/classes
#cp -r ../boston_db target/classes
#cp -r ../full_db target/classes
export CLASSPATH=$CLASSPATH:`cat .classpath`:.
#echo $CLASSPATH
