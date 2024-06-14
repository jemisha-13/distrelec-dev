#!/bin/bash

pwd=`pwd`
cdir=`basename $pwd`


#if [ "$1" == "" ]
#then
#	echo "no release specified"
#	exit 0
#fi

release=$1

if [ "$cdir" != "environment" ]
then
	echo "please run this script in environment folder"
	exit 0
fi

for file in `ls | grep env-hp-q-ff |grep -v "common"`
do
 echo $file
 . $file/static/scripts/install-release-env.rc
 	
 scp -pr $file/unix hybris@$install_environment_ip:/data/project_distrelec/
 scp -pr $file/unix-root hybris@$install_environment_ip:/data/project_distrelec/
 scp -pr $install_environment-common/unix hybris@$install_environment_ip:/data/project_distrelec
 scp -pr $install_environment-common/unix-root hybris@$install_environment_ip:/data/project_distrelec

done