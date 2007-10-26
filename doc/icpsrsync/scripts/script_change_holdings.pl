#!/usr/bin/perl -n

if ( /^(.*<holdings.*URI=\").*\/ABSTRACTS\/([0-9][0-9][0-9][0-9][0-9].xml.*)$/ )
{
    $head = $1; 
    $tail = $2; 

    print $head; 

    print "http://www.icpsr.umich.edu/cocoon/ICPSR/STUDY/"; 

    print $tail . "\n";
}
else
{
    print; 
}
