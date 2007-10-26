#!/usr/bin/perl

unless ( $#ARGV == 3 )
{
    print STDERR "usage: ./script.pl list.studies dirname list.icpsr.valid list.final.valid < mdquery.out\n";
    exit 1; 
}

$list = shift @ARGV; 
$dir = shift @ARGV; 
$listicpsrvalid = shift @ARGV; 
$listfinalvalid = shift @ARGV; 


open L, $list || die $@; 

while ( <L> )
{
    chop; 
    s/^0*//g; 
    s/ .*$//g; 
    $PROCESSING{$_} = 4; 
}

close L; 

open L, $listicpsrvalid || die $@; 

while ( <L> )
{
    chop; 
    s/^0*//g; 
    $PROCESSING{$_} = 5; 
}

close L; 

open L, $listfinalvalid || die $@; 

while ( <L> )
{
    chop; 
    s/^0*//g; 
    $PROCESSING{$_} = 2; 
}

close L; 

while ( <> )
{
    if ( /^Study: ([0-9]*)$/ )
    {
        $study = $1; 

	if  ( $PROCESSING{$study} )
	{
	    $_ = <>; chop; 

	    if ( s/^Title: // )
	    {
		$title = $_; 

		$title=~s/\t/ /g; 

		$date = time; 
		# hack: 
		$date -= (3600 * 24 * 6); 

		$TITLES{$study} = $title; 
#		print  $study . "\t" . $title . "\t" . $date . "\t" . $dir . "\t" . ($PROCESSING{$study} - 1) . "\n"; 
		$DSETLAST{$study} = 0; 
		$NDSETS{$study} = 0; 
		$NFILES{$study} = 0; 

	    }
	    else
	    {
		print STDERR "WARNING: no title line! -- $_";
	    }

	}
    }
    elsif ( /^([0-9]+) ([0-9]+) .* AVAILABLE/ )
    {
	$study = $1; 
	$dset  = $2; 
	
#	print STDERR "study: $study\n";
#	print STDERR "dataset: $dset\n";

	if ( $dset != $DSETLAST{$study} )
	{
	    $NDSETS{$study}++; 
	    $DSETLAST{$study} = $dset; 
	}
	$NFILES{$study}++; 
    }
}


for $study ( keys %PROCESSING )
{
    $title = $TITLES{$study}; 
    $date = time; 
    # hack: 
    $date -= (3600 * 24 * 6); 
    $ndsets = $NDSETS{$study}; 
    $nfiles = $NFILES{$study}; 

    print  $study . "\t" . $title . "\t" . $date . "\t" . $dir . "\t" . $ndsets . "\t" . $nfiles . "\t" . ($PROCESSING{$study} - 1) . "\n"; 
}
