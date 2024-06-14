#!/bin/bash

# Make sure only root can run our script
if [[ $EUID -ne 0 ]]; then
 echo "This script must be run as root" 1>&2
 exit 1
fi

echo "starting systems installation on `hostname`"
date



echo "d/q/p ?"

read env

echo "/data_nfs/env-hp-$env/factfinder/ directory needs to be created manually  by scp"

installDir="/data/installation"

# create installation dir
# unzip tomcat
cd /data/
cp $installDir/tomcat7_XY.zip .
unzip tomcat7_XY.zip
rm -f tomcat7_XY.zip

# logs
mkdir /var/log/tomcat7
cd /var/log/
chown -R hybris:root tomcat7
cd /data/apache-tomcat7
rm -rf logs
ln -s /var/log/tomcat7 logs
cd /data/apache-tomcat7/bin
chmod ugo+x *

# etc/init.d
cd /etc/init.d/
cp /data/apache-tomcat7/etc_init.d/tomcat7 .
chmod ugo+x tomcat7
chown -R hybris:root tomcat7
cd /data/apache-tomcat7/
rm -rf etc_init.d/

# super tab customer script
echo "service-tomcat7 \"/sbin/service tomcat7\" :shybris uid=0 gid=0" >> /etc/super.tab.customer
rm -rf /data/apache-tomcat7/super.tab.customer

# set rights
chmod 775 /data
chown -R hybris:hybris /data
chkconfig tomcat7 on

# var/run pid file
mkdir /var/run/tomcat7
chown -R hybris:hybris /var/run/tomcat7


# create installation dir
mkdir /data/project_distrelec
chown -R hybris:hybris /data

chmod 774 /data
chmod 775  /data/project_distrelec

# install packages
yum -y --nogpg install nc
yum -y --nogpg install libgomp

rpm -i $installDir/libunwind-0.99-1.1.x86_64.rpm
rpm -i $installDir/gperftools-libs-2.0-4.1.x86_64.rpm

chkconfig tomcat7 on

envDir="/data_nfs/env-hp-$env/"

su hybris -c "mkdir -p  $envDir/factfinder/ff-resources/FACT-Finder/logs"
su hybris -c "mkdir -p  $envDir/factfinder/ff-resources/FACT-Finder/logs/`hostname`"
su hybris -c "mkdir -p  $envDir/factfinder/ff-builds"
su hybris -c "mkdir -p  $envDir/factfinder/ff-resources/dumps/`hostname`"
su hybris -c "mkdir -p  $envDir/factfinder/ff-resources/FACT-Finder/scripts"
su hybris -c "mkdir -p  $envDir/factfinder/ff-resources/FACT-Finder/cache"

chown -R  hybris /var/log/tomcat7
chown -R  hybris /etc/init.d/tomcat7

touch /etc/java_home
chown hybris:hybris /etc/java_home

echo "installation close to be done, you need to create the symlink in /data/apache-tomcat7  and do a deplyoment to update the configuration"
