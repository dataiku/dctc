#! /bin/sh

set -e
me=`basename $0`
JARFILE=../dist/dctc-tool.jar

line_pattern=XX_LINE_XX

stderr ()
{
  local i
  for i
  do
    echo >&2 "$me: $i"
  done
}

run() {
    stderr "$@"
    "$@"
}

if ! [ -f $JARFILE ]; then
    echo Run ant
    sleep 1
    cd ..
    run ant distclean
    run ant configure
    run ant tooljar
    cd -
fi

if [ "x$1" = "x" ]; then
    DEST=dctc
else
    DEST="$1"
fi
TMP_DEST=`mktemp /tmp/dctc.XXXX`

cat >"$TMP_DEST"<<.
#! /bin/sh
TMPFILE=\`mktemp /tmp/temp.XXXX\`
tail -n+$line_pattern \$0 > \$TMPFILE
java -jar \$TMPFILE "\$@"
RET=\$?; rm -f \$TMPFILE; exit $RET
.

sed -e "s/$line_pattern/`wc -l <$TMP_DEST | sed 's/ *//'`/" < "$TMP_DEST" > "$DEST"
cat "$JARFILE" >> "$DEST"
chmod u+x -- "$DEST"

rm -- "$TMP_DEST"
