#! /bin/sh
if echo $0 | grep -qE '^/'; then
    path=$0
else
    path=`pwd`/$0
fi
path="`readlink -e $path`"

java -Dcom.sun.net.ssl.checkRevocation=false -ea -jar `dirname $path`/dist/dctc-tool.jar "$@"
