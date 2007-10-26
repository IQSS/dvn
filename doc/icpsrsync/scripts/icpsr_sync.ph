# subroutines:

sub select_Successful {
    my $spss_file = shift @_; 
    my $result_file = shift @_; 

    return undef unless open IN_FILE, $spss_file; 

    my $line; 
    my @studies; 

    while ( $line = <IN_FILE> )
    {
	return undef unless $line =~/^([0-9]+)[ \t]/; 
	push @studies, $1;
    }

    close IN_FILE; 

    return undef unless open OUT_FILE, ">" . $result_file; 

    my $study; 
    my $status; 

    for $study ( @studies )
    {
	# first we check if the section 4 (data) DDI fragment has been generated:

	unless ( -f $study . "/" . $study . "sec4.xml" )
	{
	    next; 
	}

	$status = &parse_LogFile ( "$study" . "/" . $study . ".log" ); 

	if ( $status->{'ok'} )
	{
	    print OUT_FILE $study;
	    print OUT_FILE " " . $status->{'failed_parts'} 
	           if $status->{'failed_parts'}; 
	    print OUT_FILE "\n";	    
	}
    }

    close OUT_FILE; 
    
    1; 
}

sub parse_LogFile {
    my $log_file = shift @_; 

    my $status = {}; 

    $status->{'ok'} = undef; 
    $status->{'failed_parts'} = undef; 

    open LF, "tail $log_file|" || return $status; 

    my $l; 
    $status->{'ok'} = 1; 
    while ( $l = <LF> )
    {
	if ( $l =~/^validation result=[0-9]*/ ||
	     $l =~/^The requested processing failed/ )
	{
	    $status->{'ok'} = undef; 
	    last; 
	}

	if ( $l =~/^The requested processing is partially failed/ )
	{
	    my @failed_files; 

	    $l = <>; 
	    chop $l;

	    while ( $l=~/^[0-9]*th file\tsource file=(.*)/ )
	    {
		push @failed_files, $1;
		$l = <>; 
		chop $l;
	    }
	    $status->{'failed_parts'} = join ( " ", @failed_files ); 	    
	    last; 
	}		
    }

    close LF; 
    return $status; 
}

1; 
