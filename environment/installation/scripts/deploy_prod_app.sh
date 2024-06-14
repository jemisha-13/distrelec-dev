#!/bin/bash

pwd=`pwd`
cdir=`basename $pwd`


if [ "$1" == "" ]
then
	echo "no release specified"
	exit 0
fi

release=$1

if [ "$cdir" != "environment" ]
then
	echo "please run this script in environment folder"
	exit 0
fi

for file in `ls | grep env-hp-p-app |grep -v "common"`
do
 echo $file
 . $file/static/scripts/install-release-env.rc
 echo "ssh hybris@$install_environment_ip \"(cd /data/project_distrelec/; ./install-release-env-hp-p-app.sh $release)\""
 ssh hybris@$install_environment_ip "(cd /data/project_distrelec/; ./install-release-env-hp-p-app.sh $release)"
done