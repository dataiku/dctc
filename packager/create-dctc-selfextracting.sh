#! /bin/sh

set -e

JARFILE=../dist/dctc-tool.jar

line_pattern=XX_LINE_XX

if ! [ -f $JARFILE ]; then
    echo Run ant
    sleep 1
    cd ..
    ant clean tooljar
    cd -
fi

if [ x$1 = x ]; then
    DEST=dctc
else
    DEST=$1
fi
TMP_DEST=`mktemp /tmp/dctc.XXXX`

cat >$TMP_DEST<<.
#! /bin/sh
TMPFILE=\`mktemp /tmp/temp.XXXX\`
tail -n+$line_pattern \$0 > \$TMPFILE
java -jar \$TMPFILE "\$@"
RET=\$?; rm -f \$TMPFILE; exit $RET
.

sed -e "s/$line_pattern/`wc -l <$TMP_DEST | sed 's/ *//'`/" $TMP_DEST > $DEST
cat $JARFILE >> $DEST
chmod u+x $DEST

rm $TMP_DEST
