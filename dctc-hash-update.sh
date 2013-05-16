#! /bin/sh

sed -ie "s/XXX_GIT_HASH_XXX/`git log --pretty=format:'%h' -n1`/" $1
