#!/usr/bin/perl

$listout = shift @ARGV; 

open LO, ">" . $listout; 

while (<>)
{
    chop; 

    if ( /^validating ([0-9][0-9][0-9][0-9][0-9])/ )
    {
	$study = $1
    }
    elsif ( /^the document (.*)/ )
    {
	$result = $1;
	if ( $result =~/is valid/ )
	{
	    print LO $study . "\n";
	}
	else
	{
#	    print STDERR "study $study ddi is invalid.\n";
#	    system "/bin/mv -f $study $study.INVALID\n";
	}
    }
}

close LO; 
