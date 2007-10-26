#!/usr/bin/perl

while (<>)
{
    chop; 
    split; 

    $study = shift @_; 

    $max = shift @_;
    $total = shift @_; 

    $s = sprintf ( "%05d", $study ); 

    system "mkdir -p $s";

    open ( D, "|/usr/local/vdc-admin/icpsrsync/bin/get_icpsr.pl" ) || die $@; 

    while ( $file = shift @_)
    {
	$url = shift @_; 

#	print D $s . "/" . $file . " http://www.icpsr.umich.edu/cgi/bob/file?" . $url . "\n";
	print D $s . "/" . $file . " " . $url . "\n";
    }

    close D; 
    if ( $? )
    {
	exit 1; 
    }

    print STDERR ".";
}

print STDERR "\n";

