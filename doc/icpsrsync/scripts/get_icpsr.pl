#!/usr/bin/perl -I/usr/local/vdc-admin/icpsrsync/bin

use ICPSR_GET; 

$icpsr_get = ICPSR_GET::new_IO(); 

if ( $icpsr_get->{ICPSR_GET_error} )
{
    print STDERR "error: " . $icpsr_get->{ICPSR_GET_error} . "\n";
    exit 1; 
}

while ( <> )
{
    chop; 
    ($file, $url) = split; 

    if ( $debug )
    {
	print STDERR "attempting to download $file/$url";
    }

    $file = $icpsr_get->ICPSR_GET ( $url, $file ); 

    unless ( $file )
    {
	print STDERR "$file could not be downloaded;\n";
	system ( "/bin/rm -f $file" );
	exit 1;
    }
}

exit 0; 
