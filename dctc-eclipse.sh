#! /bin/sh

if echo $0 | grep -E '^/' > /dev/null; then
    path=$0
else
    path=`pwd`/$0
fi

path="`readlink -e $path`"
basedir=`dirname $path`

java -Dcom.sun.net.ssl.checkRevocation=false \
    -ea \
    -classpath "$basedir/lib/ivy/core/*:$basedir/lib/third/*:$basedir/bin" \
    com.dataiku.dctc.Main "$@"
