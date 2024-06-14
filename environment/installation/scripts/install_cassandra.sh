#!/bin/bash

# Make sure only root can run our script
if [[ $EUID -ne 0 ]]; then
 echo "This script must be run as root" 1>&2
 exit 1
fi

echo "Starting cassandra installation on `hostname`"
date

ENV_DIR=$install_environment$install_environment_node
YAML_PATH=/data/project_distrelec/environment/$ENV_DIR/cassandra


# create installation dir
# unzip cassandra

cd /data/
cp /data_nfs/installation/apache-cassandra-3.9-bin.tar.gz .
tar -zxvf apache-cassandra-3.9-bin.tar.gz
rm -f apache-cassandra-3.9-bin.tar.gz

mkdir apache-cassandra
mkdir apache-cassandra/data
mkdir apache-cassandra/logs
mkdir apache-cassandra/conf

chown -R hybris:hybris apache-cassandra
chown -R hybris:hybris apache-cassandra/*

# Create symbolic links
cd /data/apache-cassandra-3.9-bin.tar.gz

# Logs
rm -rf logs
ln -s /data/apache-cassandra/logs logs

# Data
ln -s /data/apache-cassandra/data data

# Configuration files
cp -r conf/* /data/apache-cassandra/conf
rm -rf conf
ln -s /data/apache-cassandra/conf conf

## rename the old configuration file
mv conf/cassandra.yaml conf/cassandra.yaml_init
mv conf/cassandra-env.sh conf/cassandra-env.sh_init

## Copy the cassandra.yaml configuration file
cp $YAML_PATH/cassandra.yaml conf/
cp $YAML_PATH/cassandra-env.sh conf/


#########################################################
# Create the service command                            #
#########################################################

# etc/init.d
cd /etc/init.d/

touch cassandra
chown -R hybris:hybris cassandra
chmod ugo+x cassandra


# var/run pid file
cd /var/run/
touch cassandra.pid
chown -R hybris:hybris cassandra.pid

# File: /etc/super.tab.customer
echo "service-cassandra \"/sbin/service cassandra\" :shybris uid=0 gid=0" >> /etc/super.tab.customer

# Disable the swap
#swapoff --all

# enable cassandra
chkconfig cassandra on


## Do a deployment and run update_root configuration and then run 'sysctl -p', then run 'service cassandra start'
