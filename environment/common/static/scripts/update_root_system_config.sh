#!/bin/bash

# Make sure only root can run our script
if [[ $EUID -ne 0 ]]; then
 echo "This script must be run as root" 1>&2
 exit 1
fi

# temporary: to ensure correct permissions
chown root:root /etc/cron.d/hybris
chmod 644  /etc/cron.d/hybris
touch /etc/cron.d/hybris
rm -f /readmy.md
rm -f /.readmy.md


changes=0
datesuffix=`date +"-%Y%m%d-%s"`

for file in `find ./unix-root/ -type f | grep -v readme.md`
do
  sfile=`echo $file |sed 's/\.\/unix-root//'` 
  echo -n "$sfile: "
  if diff $file $sfile >/dev/null
  then
    # files are the same
    echo "up to date"
  else
    changes=1
    echo "replacing"
    # cp $sfile{,$datesuffix}
    cp $file $sfile 
    
  fi
done

if [[ "$changes" -ne 0 ]]
then
 echo
 echo "some files have been replaced"
 echo "please restart corresponding service"
else
 echo
 echo "no files replaced"
fi 
