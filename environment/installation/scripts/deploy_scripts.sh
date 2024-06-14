#!/bin/bash

pwd=`pwd`
cdir=`basename $pwd`



if [ "$cdir" != "environment" ]
then
	echo "please run this script in environment folder"
	exit 0

fi

# install_environment=env-hp-q-web
# install_environment_node=01
# install_environment_ip=10.191.17.10

function deploy
{
 file=$1
 if [ -f $file/static/scripts/install-release-env.rc ]
 then
	. $file/static/scripts/install-release-env.rc
	
	
	
	rsync -avz installation/scripts/install_systems*.sh  hybris@$install_environment_ip:./install/
	
	rsync -avz installation/files/.bashrc  hybris@$install_environment_ip:./
	
	rsync -avz $file/static/scripts/install-release-env.rc hybris@$install_environment_ip:/data/project_distrelec
	rsync -avz $install_environment-common/static/scripts/install-release-env-*.sh hybris@$install_environment_ip:/data/project_distrelec
	
	rsync -avz common/static/scripts/update_system_config.sh hybris@$install_environment_ip:/data/project_distrelec
	rsync -avz common/static/scripts/update_root_system_config.sh hybris@$install_environment_ip:/data/project_distrelec
	
	# quick deploy static & unix file to all servers
	# rsync -avz common/static hybris@$install_environment_ip:/data/project_distrelec/
	# rsync -avz $install_environment-common/static hybris@$install_environment_ip:/data/project_distrelec/
	# rsync -avz $file/static hybris@$install_environment_ip:/data/project_distrelec/

	# rsync -avz common/unix hybris@$install_environment_ip:/data/project_distrelec/
	# rsync -avz $install_environment-common/unix hybris@$install_environment_ip:/data/project_distrelec/
	# rsync -avz $file/unix hybris@$install_environment_ip:/data/project_distrelec/

	# rsync -avz common/unix-root hybris@$install_environment_ip:/data/project_distrelec/
	# rsync -avz $install_environment-common/unix-root hybris@$install_environment_ip:/data/project_distrelec/
	# rsync -avz $file/unix-root hybris@$install_environment_ip:/data/project_distrelec/

	
	## on time tasks
	# ssh rhaemmerli@$install_environment_ip 'rm .bashrc'
	scp -p installation/files/.bashrc  rhaemmerli@$install_environment_ip:./
	
	# ssh hybris@$install_environment_ip 'hostname; mkdir install'
	## remove old script
	# ssh hybris@$install_environment_ip 'rm .bashrc'
	# rsync -av --progress installation/packages hybris@$install_environment_ip:/data/
	# scp -p installation/files/.bashrc  rhaemmerli@$install_environment_ip:./
	# ssh hybris@$install_environment_ip 'hostname; rm ./install/install_systems*.sh'
	# ssh hybris@$install_environment_ip 'cd /data; tar xvf packages/jdk-8u121-linux-x64.tar.gz'
	## update java (rha 14.1.2014)
	# ssh hybris@$install_environment_ip 'cd /data; tar xvf /data_nfs/installation/jdk-8u121-linux-x64.tar.gz'
	
	
	
 else
	echo "config file not found: $file/static/scripts/install-release-env.rc "
 fi
}





red='\033[0;31m'
blue='\033[0;34m'
nocolor='\033[0m'

for file in `ls | egrep "env-hp-|env-q|env-d00" |grep -v "common"`
do
 echo -e "${blue}$file${nocolor}"
 deploy $file
 echo -e "${blue}$file done${nocolor}"
 echo
 echo
done