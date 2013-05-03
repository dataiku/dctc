#! /bin/sh

MYDIR=`dirname $0`
MYDIR=`cd $MYDIR && pwd -P`
cd $MYDIR
java -Dcom.sun.net.ssl.checkRevocation=false -ea -jar dist/dctc-tool.jar "$@"
