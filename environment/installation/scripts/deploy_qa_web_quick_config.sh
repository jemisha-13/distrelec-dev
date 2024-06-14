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

for file in `ls | grep env-hp-q-web |grep -v "common"`
do
 echo $file
 . $file/static/scripts/install-release-env.rc
 	
 	
 rsync --exclude ".svn/" -avz $file/unix hybris@$install_environment_ip:/data/project_distrelec/
 rsync --exclude ".svn/" -avz $file/unix-root hybris@$install_environment_ip:/data/project_distrelec/
 rsync --exclude ".svn/" -avz $file/static hybris@$install_environment_ip:/data/project_distrelec/
 rsync --exclude ".svn/" -avz $install_environment-common/unix hybris@$install_environment_ip:/data/project_distrelec
 rsync --exclude ".svn/" -avz $install_environment-common/unix-root hybris@$install_environment_ip:/data/project_distrelec
 rsync --exclude ".svn/" -avz $install_environment-common/static hybris@$install_environment_ip:/data/project_distrelec

done