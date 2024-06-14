#!/bin/bash
# update unix config files on all systems

pwd=`pwd`
cdir=`basename $pwd`



if [ "$cdir" != "environment" ]
then
	echo "please run this script in environment folder"
	exit 0

fi

for file in `ls | grep env-hp- |grep -v "common"`
do
 echo $file
 if [ -f $file/static/scripts/install-release-env.rc ]
 then
	. $file/static/scripts/install-release-env.rc
	ssh hybris@$install_environment_ip '(cd /data/project_distrelec; ./update_system_config.sh )'
	
 else
	echo "config file not found: $file/static/scripts/install-release-env.rc "
 fi
 echo $file done
 echo
 echo
done

