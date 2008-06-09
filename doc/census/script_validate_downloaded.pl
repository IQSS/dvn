#!/usr/bin/perl

$dir = shift @ARGV; 

$file = shift @ARGV; 

open ( U, $file ) || die $@; 

while ( <U> )
{
    chop; 
    ($study, $url) = split ( / /, $_, 2 ); 

    if ( $url =~/\?vars=(.*)/ )
    {
	$var = $1; 
#	print STDERR "var=$var\n";
    }
    else
    {
	print STDERR "couldn't find the vars parameter! ($url)\n";
    }

    $filelen = (stat($dir . "/" . $study . ".out"))[7]; 

    if ( $filelen )
    {
	open X, $dir . "/" . $study . ".out" || die $@; 

	$line = <X>; 

	if ( $line =~ /^<html>.*Error/ )
	{
	    print $study . " failure\n";
	}
	else
	{
	    chop $line; 

	    close X; 

	    @varnames = split ( /[ \t]/, $line ); 

	    $found = 0; 

	    for $v ( @varnames ) 
	    {
		if ( $var eq $v )
		{
		    $found = 1; 
		    last; 
		}
	    }

	    if ( $found )
	    {
		print $study . " success\n";
	    }
	    else
	    {
		print $study . " failure\n";
	    }
	}
    }
    else
    {
	print $study . " timeout\n";
    }
}

close U; 
