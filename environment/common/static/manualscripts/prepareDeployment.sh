#! /bin/bash
#cd environment/
#ant installAnt
#cd ../
cp environment/common/static/manualscripts/setantenv.sh .
chmod 755 setantenv.sh
. ./setantenv.sh
#cd environment
#echo "  --> setupDeploymentScript"
#ant setupDeploymentScript $1
#cd ../init/
echo "  --> createDeploymentUnit"
#ant createDeploymentUnit $1 $2
ant -buildfile environment/build.xml createDeploymentUnit