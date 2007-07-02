
package DSB::XML; 

use vars qw($DSB_XML_JAVA $DSB_XML_CLASS $DSB_XML_DTD);

use Exporter;

$DSB_XML_JAVA  = 'java';
$DSB_XML_CLASS = '/usr/local/msv/msv.jar';
$DSB_XML_DTD   = '/usr/local/VDC/xslt/Version1.dtd';
$DSB_XML_SCHEMA = '/usr/local/VDC/UIS/VDC/Schema/DDI/Version1-3.xsd';
# CONSTRUCTOR: 

sub new {
    my $skip = shift;
    my $logger = shift; 
    my $tmpdir = shift; 

    my $self = bless {};

    # we inherit some objects from the parent object (DSB): 

    $self->{logger} = $logger if $logger;

    $self->{logger}->vdcLOG_info ( "VDC::DSB::XML", "(new)", "initialized itself; reusing supplied logger object" ); 

    $self->{'TMPDIR'} = $tmpdir; 

    return $self;
}

# METHODS: 

sub validate_DDI {

    my $self    = shift;
    my $ddifile = shift; 

    my $tmpdir = $self->{'TMPDIR'}; 

    $ddiname = $tmpdir . "/DSB.ddi.$$";

    open (DDI, ">$ddiname");
    print DDI $ddifile;
    close (DDI);

    $self->{logger}->vdcLOG_info ( "VDC::DSB::XML", "(validate_DDI)", "attempting to validate " .  $ddiname); 


    my $commandline = $DSB_XML_JAVA . " -jar " . $DSB_XML_CLASS .
   	 " " . $DSB_XML_SCHEMA . " " . $ddiname; 

    system("$commandline >/dev/null 2>&1");
    $success = ($? >> 8);
    if ($success !=0 && $success!=255) {
    	$self->{logger}->vdcLOG_fatal( "VDC::DSB::XML", "(validate_DDI)", "fatal error $commandline ($?)" ); 
	die(); 
    }


#    print STDERR "INGEST: $commandline ($success)\n";

    return undef unless ($success == 0);

    return 1;
}


sub DSB_XML_extract {
    my $self    = shift;
    my $xml     = shift; 
    my $tag     = shift; 

    my @lines = split ( "\n", $xml ); 
    my $l; 
    my @ret = (); 
    my $found = "";

    my $c; 
    
#    while ( $l = shift @lines )
    for ( $c = 0; $c <= $#lines; $c++ )
    {
	$l = $lines[$c];

	if ( ( $l =~/<\/*$tag[> \t]/ ) || $found )
	{
	    $l.="\n";

	    if ( $l =~ /<$tag[> \t]/ )
	    {
		$l =~ s/^.*(<$tag[> \t])/$1/;
	    }
	    if ( $l =~ /<\/$tag[> \t]/ )
	    {
		$l =~ s:(</$tag>).*$:$1:;
		$found .= $l; 
		push ( @ret, $found ); 
		$l = $found = "";
	    }
	    $found .= $l; 
	}
    }

    return  @ret; 
}

sub DSB_XML_insert_StudyUNF {
    my $self = shift; 
    my $xml = shift; 
    my $unf = shift; 

    # really fool-proof regex code for replacement/insertion of the UNF note;
    # xslt still is for wusses.

    my $unf_note = "<notes subject=\"Universal Numeric Fingerprint\" level=\"study\" source=\"archive\" type=\"VDC:UNF\">" . $unf . "</notes>\n";

    if ( $xml =~s/<notes[^>]*level=.study.[^>]*VDC:UNF[^<]*<\/notes>/$unf_note/si )
    {
	return $xml; 
    }	

    if ( $xml =~s/<notes[^>]*VDC:UNF[^>]*level=.study.[^<]*<\/notes>/$unf_note/si )
    {
	return $xml; 
    }	

    $xml =~s/(<stdyDscr.*[^ \t])([ \t]*)(<\/citation>)/$1$2$unf_note$2$3/si;

    return $xml; 
}

sub DSB_XML_extract_uri {
    my $self    = shift;
    my $xml     = shift; 

    if ( $xml =~ /<[^>]*URI=\"([^\"]*)\"/ )
    {
	return $1; 
    }
    
    return undef; 
}
    
    








