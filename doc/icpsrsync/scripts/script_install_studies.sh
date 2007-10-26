#!/bin/sh

#ls -d [0-9][0-9][0-9][0-9][0-9] | while read d

cat list.final.valid | while read d
do
  if [ -d /export/VDC/data/FILES/$d ]
  then
      rm -rf /export/VDC/data/FILES/$d
      echo "wiped /export/VDC/data/FILES/$d"
  fi

  mkdir /export/VDC/data/FILES/$d

  mv $d/$d.icpsr.xml /export/VDC/data/FILES/$d  
  mv $d/$d.study.xml /export/VDC/data/FILES/$d
  if [ -f $d/$d.data.xml ]
  then
      gzip < $d/$d.data.xml > /export/VDC/data/FILES/$d/$d.data.xml.gz
      rm -f $d/$d.data.xml
      mv $d/datafiles.raw /export/VDC/data/FILES/$d
  fi

  mv $d/othermat.xml /export/VDC/data/FILES/$d

  if [ ! -d /export/VDC/data/1902.2/$d ]
  then
      mkdir /export/VDC/data/1902.2/$d
      echo "created /export/VDC/data/1902.2/$d"
  fi

  if [ -f /export/VDC/data/1902.2/$d/study.xml ]
  then
      echo "preserving the existing study DDI ($d)"
      gzip < /export/VDC/data/1902.2/$d/study.xml > /export/VDC/data/1902.2/$d/study.xml.PRESERVED.gz
  fi

  script_change_holdings.pl < $d/$d.merged.xml > /export/VDC/data/1902.2/$d/study.xml
  /bin/rm -f $d/$d.merged.xml

  chown -R apache.apache /export/VDC/data/1902.2/$d


done

echo 

echo "ingesting the metadata..."

cat ddi.meta | /usr/local/VDC/sbin/script_mutate_meta.pl vdc.hmdc.harvard.edu
echo done
