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
	print OUT $_; 
    }

    close ET; 
    close OUT; 
}

exit 0; 
    

