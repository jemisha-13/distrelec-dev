#!/bin/bash

# Make sure only root can run our script
if [[ $EUID -ne 0 ]]; then
 echo "This script must be run as root" 1>&2
 exit 1
fi

echo "Starting filebeat installation on `hostname`"
date

#ENV_DIR=$install_environment$install_environment_node
#YAML_PATH=/data/project_distrelec/environment/$ENV_DIR/filebeat

# install elastic key used to sign rpm packages if it is not already installed
rpm -q gpg-pubkey --qf ' %{summary}\n' | grep  -qw dev_ops@elasticsearch.org && echo "Elastic RPM key already installed" || rpm --import https://packages.elastic.co/GPG-KEY-elasticsearch

# create yum repository of elastic if it doesn't exist already
[ -f /etc/yum.repos.d/elastic-5.repo ] && echo "Yum repo already installed" || /bin/cat<<EOF > /etc/yum.repos.d/elastic-5.repo
[elastic-5.x]
name=Elastic repository for 5.x packages
baseurl=https://artifacts.elastic.co/packages/5.x/yum
gpgcheck=1
gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
enabled=1
autorefresh=1
type=rpm-md
EOF

# install filebeat if not already present
rpm -qa | grep -qw filebeat  && echo "Filebeat rpm was already installed" || yum -y install filebeat

# enable filebeat startup on boot
chkconfig filebeat on

