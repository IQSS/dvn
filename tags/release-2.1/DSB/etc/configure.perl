$ETCDIR = shift @ARGV;
$VDCROOT = shift @ARGV; 
$TMPDIR = shift @ARGV; 

$HOSTNAME = `hostname`; chop $HOSTNAME; 

@CONFFILES = ( "glv03",
	       "vdc.conf" );


for $etcfile ( @CONFFILES )
{
    $tmpletcfile = $etcfile . ".tmpl"; 

    if ( $etcfile eq "vdc.conf" )
    {
	$tmpletcfile = "vdc.conf.RedHat-4AS.tmpl"; 

	$DSBPORT = &check_Port (); 

	unless ( $DSBPORT == 80 )
	{
	    print STDERRR "\n"; 
	    print STDERR "Attention: It appears that the DSB HTTP server\n";
	    print STDERR "listens on port " . $DSBPORT . "\n";
	    print STDERR "If this is not correct, please modify\n";
	    print STDERR "the following line in the file\n";
	    print STDERR "/usr/local/VDC/etc/vdc.conf: \n"; 
	    print STDERR "\n";
	    print STDERR "<VirtualHost *:(PORTNUMBER)>\n";
	    print STDERR "\n";
	    print STDERR "and restart httpd.\n";
	    print STDERR "\n"; 
	}
    }

    unless ( open ( ET, $tmpletcfile ) )
    {
	print STDERR "could not find $etcfile template!\n";
	exit 1; 
    }

    unless ( open ( OUT, ">" . $ETCDIR . "/" . $etcfile ) )
    {
	print STDERR "could not find $etcfile for writing!\n";
	exit 1; 
    }

    while ( <ET> )
    {
	s/%VDCROOT%/$VDCROOT/g; 
	s/%VDCTMP%/$TMPDIR/g; 
	s/%DSB_SERVER%/$HOSTNAME/g; 
	s/%DSB_SERVER_PORT%/$DSBPORT/g;
	print OUT $_; 
    }

    close ET; 
    close OUT; 
}

exit 0; 
    
sub check_Port {
    my $listen_port; 

    open C, "/etc/httpd/conf/httpd.conf" || return 80; 

    while ( <C> )
    {
	if ( /^[ \t]*Listen[ \t]*([0-9]*)$/ )
	{
	    $listen_port = $1; 
	}
    }

    close C; 

    $listen_port = 80 unless $listen_port; 

    return $listen_port; 
}

