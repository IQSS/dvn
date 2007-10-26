#!/usr/bin/perl

unless ( $#ARGV == 3 )
{
    print STDERR "usage: ./script.pl downloads downloads.sorted list.successfull mdquery.out\n";
    exit 1; 
}

$downloadlist = shift @ARGV; 
$downloadlistsorted = shift @ARGV; 
$listsuccessfull = shift @ARGV; 
$mdquery = shift @ARGV;  

%NUM_MONTHS = 
    ( 'JAN', 1, 
      'FEB', 2,
      'MAR', 3,
      'APR', 4, 
      'MAY', 5, 
      'JUN', 6,
      'JUL', 7,
      'AUG', 8,
      'SEP', 9,
      'OCT', 10,
      'NOV', 11, 
      'DEC', 12 ); 

open S, $downloadlist || die $@; 

while ( <S> )
{
    chop; 
    split; 

    $study = shift @_; 
    $study =~s/^0*//g; 

    shift @_; # maxfilesize
    shift @_; # totalfilesize


    while ( shift @_ ) # filename
    {
	$url = shift @_; 

	$fileid = 0; 
	if ( $url =~/file_id=([0-9]+)/g )
	{
	    $file_id = $1; 
	    $POTENTIALLY_SUBSETTABLE_FILES{$file_id} = 1; 
	}
	else
	{
	    print STDERR "WARNING! unrecognized url format: $url\n";
	}
    }

}

close S; 

open S, $downloadlistsorted || die $@; 

while ( <S> )
{
    chop; 
    split; 

    $study = shift @_; 
    $study =~s/^0*//g; 

    shift @_; # maxfilesize
    shift @_; # totalfilesize

    while ( shift @_ ) # filename
    {
	$url = shift @_; 

	$fileid = 0; 
	if ( $url =~/file_id=([0-9]+)/g )
	{
	    $file_id = $1; 
	    $PROCESSING_ATTEMPTED{$file_id} = 1; 
	}
	else
	{
	    print STDERR "WARNING! unrecognized url format: $url\n";
	}
    }

}

close S; 

open S, $listsuccessfull || die $@; 

while ( <S> )
{
    chop; 

    $study = $_; 
    $study =~s/^0*//g; 

    $SUCCESSFULLY_PROCESSED{$study} = 1; 
}

close S; 


# now read the mdquery file: 

$vsyncdate = (stat ( $mdquery ))[9]; 

open ( M, $mdquery ) || die $@; 

while ( <M> )
{
    if ( /^([0-9]+) ([0-9]+) ([0-9]*) ([^ ]*) ([0-9]*) ([0-9]*) ([^ ]*) ([A-Z]*)/ )
    {
	$study = $1; 
	$dset  = $2; 
	$file_id     = $3; 
        $filename   = $4; 

	$linelen    = $5; ## (only for datafiles!)
	$cases      = $6; 
	$date       = $7; 
	$status     = $8; 

	
	if ( $POTENTIALLY_SUBSETTABLE_FILES{$file_id} )
	{
	    # first, the ICPSR metadata fields: 

	    $date = &convert_Date ( $date ); 

	    if ( $status eq "AVAILABLE" )
	    {
		$status = 1; 
	    }
	    elsif ( $status eq "UNAVAILABLE" )
	    {
		$status = 0; 
	    }
	    else
	    {
		print STDERR "unrecognized STATUS format: $status.\n";
		$status = 0; 
	    }
    	       
	    print $study . "\t" . $dset . "\t" . $file_id . "\t" . $filename . 
		"\t" . $linelen . "\t" . $cases . "\t" . $date . "\t" . $status . "\t"; 

	    # additional VDC custom fields: 






	    if ( $filename =~/Setup\.sps/ )
	    {
		$vtype = "s"; 
	    }
	    elsif ( $filename =~/Data\.txt/ )
	    {
		$vtype = "d"; 
	    }
	    else
	    {
		print STDERR "WARNING! unexpected fname format: $filename\n";
		print STDERR "setting the file type to 'u' ('unknown')\n";
		$vtype = "u";
	    }


	    if ( $status )
	    {		
		$vstatus = &get_fStatus ( $file_id, $study ); 
	    }
	    else
	    {
		# this means the dataset was "skipped becasue of the 
		# 'unavailable' status" 

		$vstatus = 3; 
	    }

	    if ( $vstatus == 4 )
	    {
		$vsize = 0; 
		$vprocdate = 0; 
	    }
	    else
	    {
		$vsize = &get_fSize ( $study, $filename ); 
		unless ( $vsize )
		{
		    $vstatus = 6; 
		}
		$vprocdate = &get_procTime ( $study ); 
	    }


	    print $vsyncdate . "\t" . $vprocdate . "\t" . $vtype . "\t" . 
		$vsize . "\t" . $vstatus . "\n";
	    
	}		
	
    }
}

exit 0; 

sub get_procTime {
    my $study = shift @_; 

    $study = sprintf ( "%05d", $study );


    unless ( -f $study . "/" . $study . ".log" )
    {
	print STDERR "WARNING: could not find $study.log\n";
	return 0; 
    }

    return (stat ( $study . "/" . $study . ".log" ))[9]; 
}


sub get_fStatus {
    my $file_id = shift @_; 
    my $study = shift @_; 

    if ( $SUCCESSFULLY_PROCESSED{$study} )
    {
	# we'll need to do something more intelligent once we start
	# supporting success on select datafiles!

	return 2; 
    }

    if ( $PROCESSING_ATTEMPTED{$file_id} )
    {
	# this means the processing failed; 
	
	return 7; 
    }

    # this can only mean that processing was skipped on this data file
    # or card; most probably because of the size of the dataset. 

    return 4; 

    # there case when we skip a dataset because of its 
    # "unavailable" status is already addressed outside of this sub; 
    # (what about the case where we *remove* an existing dataset because 
    # of that?) -- TODO. 
}
    


sub get_fSize {
    my $study = shift @_; 
    my $filename = shift @_; 

    $study = sprintf ( "%05d", $study );

    if ( -f $study . "/" . $filename )
    {
	return (stat($study . "/" . $filename))[7]; 
    }

    if ( -f $study . "/" . $filename . ".zip" )
    {
	return &get_zipfSize ( $study . "/" . $filename . ".zip" ); 
    }

    print STDERR "warning! $study/$filename not found.\n";
    return 0; 
}

sub get_zipfSize {
    my $zipfile = shift @_; 

    unless ( open ZI, "zipinfo $zipfile |" )
    {
	print STDERR "must be a bad zip file; returning 0.\n";
	return 0; 
    }
    
    my $zil = ""; 

    while ( $zil = <ZI> )
    {
	if ( $zil =~/ ([0-9]*) bytes uncompressed/ )
	{
	    my $size = $1; 
	    close ZI; 
	    return $size; 
	}
    }

    close ZI; 

    print STDERR "most probably, that was not a valid zip file; returning 0.\n";
    return 0; 


    return 0; 
}

sub convert_Date {
    my $date = shift @_; 

    if ( $date =~/([0-9][0-9])\-([A-Z][A-Z][A-Z])\-([0-9][0-9])/ )
    {
	$in_day  = $1; 
	$in_mon  = $2; 
	$in_year = $3; 
    }
    else
    {
	print STDERR "warning! unrecognized DATE format: $date\n";
	return "1/1/1970";
    }

    $out_day = $in_day; 
    $out_mon = $NUM_MONTHS{$in_mon}; 
    $out_year = $in_year + 1900; 

    unless ( $out_mon )
    {
	print STDERR "warning! unrecognized MONTH format: $in_mon\n";
	return "1/1/1970";
    }

    return $out_mon . "/" . $out_day . "/" . $out_year; 
}
	
