#################################################################
#                                                               #
# Shell script to install both JDK 8 and Apache Cassandra 3.x   #
#                                                               #
#################################################################


# Go to the data folder
cd /data

## Install JDK 8

echo "JDK 8 u131 installation"
cp /data_nfs/installation/jdk-8u131-linux-x64.tar.gz .
tar -xzvf jdk-8u131-linux-x64.tar.gz

rm jdk-8u131-linux-x64.tar.gz

## Install Apache Cassandra 3.10

echo "Apache Cassandra v3.10 installation"
cp /data_nfs/installation/apache-cassandra-3.10-bin.tar.gz .
tar -xzvf apache-cassandra-3.10-bin.tar.gz
rm apache-cassandra-3.10-bin.tar.gz

echo "Stopping old cassandra instance "
pgrep -u hybris -f cassandra | xargs kill -9

mkdir apache-cassandra_back
cp -r apache-cassandra/* apache-cassandra_back/
cd apache-cassandra-3.10
mv conf conf_orig
ln -s /data/apache-cassandra/conf conf
ln -s /data/apache-cassandra/data data
ln -s /data/apache-cassandra/logs logs

echo "***********************************************************************************************"
echo ""
echo "***** You should update the bashrc file before the installation of the new hybris version *****"