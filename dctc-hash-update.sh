#! /bin/sh
git describe --tags
if git describe --tags > /dev/null 2>&1; then
    describe="`git describe --tags`"

else
    describe="0.0"
fi
sed -ie "s/XXX_GIT_VERSION_XXX/$describe/" $1
