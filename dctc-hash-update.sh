#! /bin/sh

if git describe; then
    describe=`git describe`

else
    describe="0.0"
fi
    sed -ie "s/XXX_GIT_VERSION_XXX/$describe/" $1
