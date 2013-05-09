#! /bin/sh

JARFILE=../dist/dataiku-dctc.jar

DEST=$1

rm -f $DEST

# Add the header
touch $DEST
echo '#! /bin/sh' >> $DEST
echo 'TMPFILE=`mktemp /tmp/temp.XXXX`' >> $DEST
echo "PAYLOAD_START=6" >> $DEST
echo 'tail -n +$PAYLOAD_START $0 > $TMPFILE' >> $DEST
echo 'java -jar $TMPFILE "$@"' >> $DEST
echo 'RET=$?; rm -f $TMPFILE; exit $RET' >> $DEST
cat $JARFILE >> $DEST

chmod 755 $DEST
