#!/usr/bin/perl

$dstflag = 0; 
$sflag = 0; 

while ( <> )
{
    if ( /^Dataset Title: / )
    {
	chop; print; 
	$dsflag++; 
    }
    elsif ( /^Title: / )
    {
	chop; print; 
	$sflag++; 
    }
    
    elsif ( $dsflag )
    {
	if ( /^[0-9]+ [0-9]+ [0-9]+ [0-9]+/ )
	{
	    print "\n";
	    print; 
	    $dsflag = 0; 
	}
	else
	{
	    chop; 
	    print " "; 
	    print; 
	}
    }
    elsif ( $sflag )
    {
	if ( /^[0-9]+ [0-9]+ [0-9]+ [0-9]+/ || /^Dataset[ \:]/ )
	{
	    print "\n";
	    print; 
	    $sflag = 0; 
	}
	else
	{
	    chop; 
	    print " "; 
	    print; 
	}
    }


    else
    {
	print; 
    }
}
