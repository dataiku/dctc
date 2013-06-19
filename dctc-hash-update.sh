#! /bin/sh

set -e

echo The version is: `git describe --tags`

if [ "x$1" = "x" ]; then
    echo Need an input file.
    exit 1
fi

if git describe --tags > /dev/null 2>&1; then
    describe="`git describe --tags`"

else
    describe="0.0"
fi

git checkout -- "$1" || echo -- $1 is not gitted # Reset the file.

sed -e "s/XXX_GIT_VERSION_XXX/$describe/" < "$1" > "${1}e"
mv -- "${1}e" "$1"
