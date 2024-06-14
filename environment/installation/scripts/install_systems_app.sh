#!/bin/bash

# Make sure only root can run our script
if [[ $EUID -ne 0 ]]; then
 echo "This script must be run as root" 1>&2
 exit 1
fi

echo "starting systems installation on `hostname`"
date

# create installation dir
mkdir /data/project_distrelec
chown -R hybris:hybris /data

cd /data 
tar xvf /data_nfs/installation/jdk-8u121-linux-x64.tar.gz

chmod 774 /data
chmod 775  /data/project_distrelec

chmod o+x /data
chmod o+x /data/mysql
chown -R mysql:mysql /data/mysql


chgrp hybris /etc/httpd/conf.d
chmod g+w  /etc/httpd/conf.d
#rm /etc/httpd/conf.d/*


chgrp hybris /etc/sysconfig/httpd
chmod g+rw /etc/sysconfig/httpd

touch /etc/cron.d/hybris
#chgrp hybris /etc/cron.d/hybris
#chmod g+rw /etc/cron.d/hybris

chgrp hybris /var/log/httpd
chmod g+rwx  /var/log/httpd

touch /etc/init.d/hybris
chgrp hybris  /etc/init.d/hybris
chmod 764 /etc/init.d/hybris

chmod +r /var/log/cron

touch /etc/java_home
chown hybris:hybris /etc/java_home 

# install packages
yum -y --nogpg install nc

# enable apache
chkconfig httpd on

# start apache
service httpd start

# enable hybris
chkconfig hybris on

# start hybris
# service hybris start