#!/bin/bash
export CLASSPATH=`find ./lib -name "*.jar" | tr '\n' ':'`
export MAINCLASS=Main
java -cp ${CLASSPATH}:classes $MAINCLASS
