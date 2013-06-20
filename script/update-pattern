#! /bin/sh

set -e
me=`basename $0`
stderr() {
    local i
    for i; do
        echo >&2 "$me: $i"
    done
}
error () {
    local status=$1
    shift
    stderr "$@"
    exit $status
}

usage() {
    cat <<.
usage: $0 [OPT...] PATH...

Options:
  -h, --help      Display this message and exit.
  -c, --checkout  Make a git checkout before do the work.
  -d, --describe  Change pattern with \`git describe\'.
  -p, --print     Print the describe string.
.
    exit $1
}

print() {
    echo "version: $describe"
}

work() {
    if $checkout; then
        git checkout -- "$1"
    fi
    if $print; then
        print
    fi
    if [ ! -f "$1" ]; then
        exit 42
    fi

    sed -e "s/$pattern/$describe/g" < "$1" > "${1}e"
    mv -- "${1}e" "$1"
}

set_git_describe() {
    describe="`git describe`"
}


print=false
checkout=false
pattern="XXX_GIT_VERSION_XXX"
for opt; do
    case $opt in
        (-h|--help) usage 0 ;;
        (-c|--checkout) checkout=true ;;
        (-d|--describe) set_git_describe ;;
        (-p|--print) print=true ;;
        (*) work $opt ;;
    esac
done
