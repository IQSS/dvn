#!/bin/sh

while read d
do
  grep '<notes subject="Universal Numeric Fingerprint" level="file" source="archive" type="VDC:UNF">' $d/$d.merged.xml | script_studyUNF.pl | tail -1 > $d/unf.study
  script_insertStudyUNF.pl $d/unf.study < $d/$d.merged.xml > $d/$d.merged.xml.UNF
  echo -n 'u'
  /bin/mv $d/$d.merged.xml.UNF $d/$d.merged.xml
done
echo
