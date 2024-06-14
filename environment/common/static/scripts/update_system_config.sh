#!/bin/bash

changes=0
datesuffix=`date +"-%Y%m%d-%s"`

for file in `find ./unix/ -type f | grep -v .svn`
do
  dir=`dirname $file`
  sdir=`echo $dir | sed 's/\.\/unix//'` 

  if [ ! -d $sdir ]; then
    echo "creating directory: $sdir"
    mkdir -p $sdir
  fi

  sfile=`echo $file |sed 's/\.\/unix//'` 
  echo -n "$sfile: "
  if diff $file $sfile >/dev/null
  then
    # files are the same
    echo "up to date"
  else
    changes=1
    echo "replacing"
    cp $sfile{,$datesuffix}
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
