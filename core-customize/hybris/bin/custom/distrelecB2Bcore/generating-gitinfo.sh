#!/bin/sh
if [ -d "resources/gitinfo" ]; then
    rm -r resources/gitinfo
fi
mkdir resources/gitinfo

git fetch --tags
echo "git.revision="$(git log --format="%H" -n 1) >>resources/gitinfo/gitinfo.properties
echo "git.tag="$(git describe --tags --abbrev=0 --exact-match) >>resources/gitinfo/gitinfo.properties
echo "builddate=$(date "+%a %b%e %H\:%M\:%S %Y %Z")" >>resources/gitinfo/gitinfo.properties
echo "git.branch="$(git branch | sed -n -e 's/^\* \(.*\)/\1/p') >>resources/gitinfo/gitinfo.properties
echo "git.lastcommit="$(git log -1 --format=%cd --date=format-local:"%a %b%e %H\:%M\:%S %Y %Z") >>resources/gitinfo/gitinfo.properties
