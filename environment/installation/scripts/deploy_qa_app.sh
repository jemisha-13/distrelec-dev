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

# install_environment=env-hp-q-web
# install_environment_node=01
# install_environment_ip=10.191.17.10


for file in `ls | grep env-hp-q-app |grep -v "common"`
do
 echo $file
 . $file/static/scripts/install-release-env.rc
 	
 # ssh hybris@$install_environment_ip '(cd /data/project_distrelec/;  $1)'
 ssh hybris@$install_environment_ip "(cd /data/project_distrelec/; ./install-release-env-hp-q-app.sh $release)"
done
