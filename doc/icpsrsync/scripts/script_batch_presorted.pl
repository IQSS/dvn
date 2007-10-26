#!/usr/bin/perl 
use POSIX ":sys_wait_h";

open (P, "mdquery.spss.sorted" ) || die "could not open mdquery.spss.sorted";
open (D, "downloads.sorted" ) || die "could not open downloads.sorted";

while ( <D> )
{
    chop;

    split;    
    $study = shift @_; 

    system ( "echo '$_' | /usr/local/vdc-admin/icpsrsync/bin/download.pl" ); 

    if ( $? )
    {
	print STDERR "could not download files for study $study (skipping).\n";
	system ( "/bin/touch $study/DOWNLOADBOMBED" );
	$p = <P>;
	next; 
    }

    shift @_;
    shift @_;

    $files = "";

    $zip_error = 0; 

    while ( $f = shift @_ )
    {
	$f =~ s/\.zip$//;
	system ( '(cd ' . $study . '; unzip -p ' . $f . '.zip > ' . $f . ')' ); 
	$zip_error = $?; 
	shift @_;

	last if $zip_error; 
    }


    if ( $zip_error )
    {
	print STDERR "could not unzip file $f (study $study)\n"; 
	print STDERR "skipping.\n";
	next; 
    }

    sleep 3; 

    $p = <P>; 

    chop $p; 
    @_ = split ( " ", $p ); 

    $study_p = shift @_; 

    if ( $study != $study_p )
    {
	die "mismatch in the downloads and processing files.";
    }

    shift @_;
    shift @_; 

    $datafiles = "";
    $cardfiles = ""; 

    while ( $n = shift @_ )
    {
	$data = shift @_; 
	$card = shift @_; 

	unless ( $data eq "[NODATA]" )
	{
	    $datafiles .= ( " " . $data );
	    $cardfiles .= ( " " . $card );
	}
    }

    if ( $datafiles )
    {
#	system ( "./ddi_sec4.mlt.pl $study $cardfiles $datafiles" );

# fork here...

	$start = time; 

	if ( ( $pid = fork ) == 0 )
	{
	    # child

	    exec "(cd $study; DDIsec4sps.pl ${study}.log ${study}sec4.xml sps $cardfiles $datafiles)" || print STDERR "could not execute the script.\n";
	}
	elsif ( $pid > 0 )
	{
	    # parent 

	    while ( 1 )
	    {
		if ( ( time - $start ) > 3600 )
		{
		    system ( "touch $study/PROCESSING.TIMEOUT" ); 
		    print STDERR "i'm bored... next!\n";
		    last; 
		}		   

		$status = waitpid ( $pid, WNOHANG); 

		last if $status > 0; 

		print STDERR "waiting...\n";

		sleep 10; 

	    }

	    print STDERR "done...\n";
	}
	else
	{
	    print STDERR "fork failed!\n";
	}

# time out here: 
# 	
 
    }

    system ( "(cd $study; rm -f $files)" );

#    system ( "(cd $study; gzip *.tab)" );

#    system ( "(cd $study; gzip *.xml)" );
}
