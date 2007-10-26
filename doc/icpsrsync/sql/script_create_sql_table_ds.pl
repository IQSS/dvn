#!/usr/bin/perl

unless ( $#ARGV == 0 )
{
    print STDERR "usage: ./script.pl mdquery.spss < mdquery.out\n";
    exit 1; 
}

$spsslist = shift @ARGV; 


open S, $spsslist || die $@; 

while ( <S> )
{
    chop; 
    split; 

    $study = shift @_; 
    $study =~s/^0*//g; 

#    print STDERR "processing study $study;\n"; 

    $POTENTIALLY_SUBSETTABLE{$study} = {}; 
    $POTENTIALLY_SUBSETTABLE_TITLES{$study} = {}; 
    $POTENTIALLY_SUBSETTABLE_NFILES{$study} = {}; 

    shift @_; 
    shift @_; 


    while ( $ds = shift @_ )
    {
#	print STDERR "study $study/dataset $ds -- potentially subsettable;\n";

	$POTENTIALLY_SUBSETTABLE{$study}->{$ds} = 1; 
	shift @_; 
	shift @_; 
    }

}

close S; 

# now read the mdquery file: 

while ( <> )
{
    if ( /^Study: ([0-9]*)$/ )
    {
        $study = $1; 

	$_ = <>; chop; 

	if ( s/^Title: // )
	{
	    $title = $_; 
	    
	    $title=~s/\t/ /g; 

	    $TITLES{$study} = $title; 
	}
	else
	{
	    print STDERR "WARNING: no title line! -- $_";
	}    
    }
    elsif ( /^Dataset: ([0-9]*)$/ )
    {
	$dataset = $1; 
	$_ = <>; 
	chop; 

	unless ( s/^Dataset Title: // )
	{
	    print STDERR "WARNING: no dataset title line! -- $_";
	}

	s/[ \t]+/ /g;
 
	$dataset_title = $_; 

	if ( $POTENTIALLY_SUBSETTABLE{$study}->{$dataset} )
	{
	    $POTENTIALLY_SUBSETTABLE_TITLES{$study}->{$dataset} = $dataset_title; 
	}		
		
    }
    elsif ( /^([0-9]+) ([0-9]+) .* AVAILABLE/ )
    {
	$study = $1; 
	$dset  = $2; 
	
	print STDERR "found file ($study:$dset);\n";

	if ( $POTENTIALLY_SUBSETTABLE{$study}->{$dset} )
	{
	    print STDERR "this dataset is potentially subsettable ($study/$dset)\n";
	    
	    $POTENTIALLY_SUBSETTABLE_NFILES{$study}->{$dset}++;  
	}		
	
    }
}


for $study ( keys %POTENTIALLY_SUBSETTABLE )
{

    $study_ref = $POTENTIALLY_SUBSETTABLE{$study}; 

    for $dset ( keys %{$study_ref} )
    {
	$dsettitle = $POTENTIALLY_SUBSETTABLE_TITLES{$study}->{$dset}; 
	$dsettitle = $TITLES{$study} unless $dsettitle; 

	if ( length ( $dsettitle ) > 255 )
	{
	    $dsettitle = substr ( $dsettitle, 0, 255 ); 
	}

	$dsetnfiles = $POTENTIALLY_SUBSETTABLE_NFILES{$study}->{$dset}; 



	print $study . "\t" . $dset . "\t" . $dsettitle . "\t" . $dsetnfiles . "\n"; 
    }
}
