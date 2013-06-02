#! /bin/sh

if git describe; then
    describe=`git describe`

else
    describe="0.2.1-`git log --pretty=format:'%h' -n1`"
fi
    sed -ie "s/XXX_GIT_VERSION_XXX/$describe/" $1
