#! /bin/sh

MYDIR=`dirname $0`
MYDIR=`cd $MYDIR && pwd -P`
cd $MYDIR
java -Dcom.sun.net.ssl.checkRevocation=false -ea -classpath "lib/ivy/core/*:lib/third/*:bin" com.dataiku.dctc.Main "$@"
