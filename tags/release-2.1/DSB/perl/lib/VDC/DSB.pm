package DSB;

use CGI qw(:standard);

use MIME::Base64;

use Compress::Zlib;


use DSB::Ingest;
use DSB::RAP;
use DSB::XML;
use DSB::Temp;
use URI;
use VDCdiag;


require "glv03";


# CONSTRUCTOR: 

sub new {return bless{};}


# DSB INITIALIZATION

sub init {
    my $self = shift; 

    $self->{'TMPDIR'} = $TMPDIR; 

    # check the temp directories: 

    my $temp_monitor = new DSB::Temp ( $TMPDIR ); 

    return 0 unless $temp_monitor->check_TempDirectory; 

    return 1; 
}


# DSB VERBS/METHODS: 

sub ListEncodings {
    my $self = shift;
    
    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <List-Encodings version=\"0.1\">\n";
    print "    <encoding>gzip</encoding>\n";
    print "  </List-Encoding>\n";

    return undef;
}

sub ListBinders  {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');
    
    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <List-Binders version=\"0.1\">\n";
    print "    <binder>tar</binder>\n";
    print "  </List-Binders>\n";

    return undef;
}

sub ListVerbs {
    $self = shift;

    print $self->{query}->header ( -type=>'text/xml');
    
    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <List-Verbs version=\"0.1\">\n";
    print "    <verb>List-Verbs</verb>\n";
    print "  </List-Verbs>\n";

    return undef;
}

sub ListConstraints {
    $self = shift;

    print $self->{query}->header ( -type=>'text/xml');
    
    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <List-Constraints version=\"0.1\">\n";
    print "    <constraint>time</constraint>\n";
    print "  </List-Constraints>\n";

    return undef;
}

sub ListPartTypes  {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <List-Part-Types  version=\"0.1\">\n";

    print "    <Part-Type>data-readable</Part-Type>\n";
    print "    <Part-Type>study-header</Part-Type>\n";
    print "    <Part-Type>supplementary-codebook</Part-Type>\n";
    print "    <Part-Type>opaque</Part-Type>\n";

    print "  </List-Part-Types >\n";

    return undef;
}

sub ListFormats {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <List-Formats version=\"0.1\">\n";

    print "  <formats>text-plain</format>\n";
    print "  <format>text-xml.ddi</format>\n";
    print "  <format>application-stata</format>\n";
    print "  <format>text-csv</format>\n";

    print "  </List-Formats>\n";

    return undef;
}

sub ListMetaFormats {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <List-Meta-Formats version=\"0.1\">\n";

    print "  <meta-format name=\"DDI\" version=\"1.0\">URL-to-DTD</meta-format>\n";
    print "  <meta-format name=\"DDI-VARS\" version=\"1.0\">DTD</meta-format>\n";

    print "  </List-Meta-Formats>\n";

return undef;
}

sub ListTransformations {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <List-Transformations version=\"0.1\">\n";
    print "  </List-Transformations>\n";

    return undef;
}

sub DescribeVerb {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Describe-Verb version=\"0.1\">\n";
    print "  </Describe-Verb>\n";

    return undef;
}

sub DescribeConstraints {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Describe-Constraints version=\"0.1\">\n";
    print "  </Describe-Constraints>\n";

return undef;
}

sub DescribeFormats {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Describe-Formats version=\"0.1\">\n";
    print "  </Describe-Formats>\n";
    
    return undef;
}

sub DescribeBinder {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Describe-Binder version=\"0.1\">\n";
    print "  </Describe-Binder>\n";

    return undef;
}

sub DescribeEncoding {
    my ($self) = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Describe-Encoding version=\"0.1\">\n";
    print "  </Describe-Encoding>\n";

    return undef;
}

sub DescribeTransformations {
    my $self = shift;
	
    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Describe-Transformations version=\"0.1\">\n";
    print "  </Describe-Transformations>\n";

    return undef;
}

sub Structure {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Structure version=\"0.1\">\n";
    print "  </Structure>\n";

    return undef;
}

sub ListVariables {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <List-Variables version=\"0.1\">\n";
    print "  </List-Variables>\n";

    return undef;
}

sub DescribePart {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Describe-Part version=\"0.1\">\n";
    print "  </Describe-Part>\n";

    return undef;
}

sub DescribeVariable {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Describe-Variable version=\"0.1\">\n";
    print "  </Describe-Variable>\n";

    return undef;
}

sub Prepare {
    my $self = shift;

    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Prepare version=\"0.1\">\n";
    print "  </Prepare>\n";

    return undef;
}

sub CalculateUNF {
    my $self = shift;
    my $params = shift;
    my $context = shift; 

    my $logger = $self->{logger}; 
    my $query  = $self->{query};


    my $code = 200; 

    my @unf_strings = $self->{query}->param ( 'unf' ); 

    my $ingest = DSB::Ingest->new ( $query, $logger, $self->{'TMPDIR'} ); 

    unless ( $ingest )
    {
	$logger->vdcLOG_fatal ( "VDC::DSB", "(updateStudyUNF)",
				"could not initialize DSB::Ingest subsystem;" );
	return ( 500, "could not initialize DSB::Ingest subsystem" ); 
    }

    my $study_unf = $ingest->calculate_UNF ( @unf_strings );

    print $query->header ( -Cache_control=>'max-age= 0', -type=>'text/plain');
    print $study_unf . "\n"; 

    return undef; 
}

sub updateStudyUNF {
    my $self = shift;
    my $params = shift;
    my $context = shift; 

    my $uri  = $params->{'uri'}; 

    my $logger = $self->{logger}; 
    my $query  = $self->{query};

    print $self->{query}->header ( -Cache_control=>'max-age= 0', -status=>$code, -type=>'text/xml');

    print "<?xml version=\"1.0\" coding=\"UTF-8\"?>\n";
    print "  <updateStudyUNF version=\"0.9a\">\n";

    my $rap = DSB::RAP->new ( $query, $logger, '', '' ); 

    unless ( $rap )
    {
	$self->{DSB_ERROR} = "could not initialize DSB::RAP subsystem"; 

	$logger->vdcLOG_fatal ( "VDC::DSB", "(updateStudyUNF)",
				"could not initialize DSB::RAP subsystem;" );

	print "  <status>failed to initialize DSB::RAP subsystem</status>\n";
	print "</updateStudyUNF>\n";

	return undef; 
    }

    unless ( my $ddi = $rap->get_RepositoryObject ( $uri ) )
    {
	$logger->vdcLOG_fatal ( "VDC::DSB", "(updateStudyUNF)",
				"could obtain the ddi from the repository;" );

	print "  <status>could obtain the ddi from the repository;/status>\n";
	print "</updateStudyUNF>\n";

	return undef; 

    }

    my $ingest = DSB::Ingest->new ( $query, $logger, $self->{'TMPDIR'} ); 

    unless ( $ingest )
    {
	$self->{DSB_ERROR} = "could not initialize DSB::Ingest subsystem"; 
	$logger->vdcLOG_fatal ( "VDC::DSB", "(updateStudyUNF)",
				"could not initialize DSB::Ingest subsystem;" );
	print "  <status>failed to initialize DSB::Ingest subsystem</status>\n";
	print "</updateStudyUNF>\n";
	return undef; 
    }

    my $xml = DSB::XML->new ( $logger, $self->{'TMPDIR'} ); 

    unless ( $xml )
    {
	$self->{DSB_ERROR} = "could not initialize DSB::XML subsystem"; 
	$logger->vdcLOG_fatal ( "VDC::DSB", "(updateStudyUNF)",
				"could not initialize DSB::XML subsystem;" );
	print "  <status>failed to initialize DSB::XML subsystem</status>\n";
	print "</updateStudyUNF>\n";
	return undef; 
    }
	
    $ingest->{xml} = $xml; 

    unless ( $ddi = $ingest->updateStudyUNF ( $ddi ) )
    {
	print "  <status>failed to update the study-level UNF</status>\n";
	print "</updateStudyUNF>\n";
	return undef; 
    }


    unless ( $rap->mutate_RepositoryObject ( $uri, $ddi ) )
    {
	print "  <status>Failed to mutate the ddi object in the repository</status>\n";
	print "</updateStudyUNF>\n";
	return undef; 
    }
    else
    {
	print "  <status>study-level UNF successfully updated</status>\n";
	print "</updateStudyUNF>\n";
    }

    return undef; 
}    

sub Ingest {
    my $self = shift;
    my $params = shift;
    my $context = shift; 


    my $logger = $self->{logger}; 
    my $query  = $self->{query};

    my $error_msg; 

    my $exit_code = "200 OK"; 

    my $final_status     = "ok";
    my $datafiles_status = "n/a";
    my $ddi_status       = "ok"; 
    my $deposit_status   = "ok"; 


    $logger->vdcLOG_info ( "VDC::DSB", "(Ingest)", 
			   "executing the verb" );


    my $sps_card = $query->param('controlCard0'); 

    my $sps_card_size = (stat( $sps_card ))[7];    

    if ( $sps_card )
    {
	$logger->vdcLOG_info ( "VDC::DSB", "(Ingest)", 
			       "spss control card uploaded: " . $sps_card_size . "  bytes;" );
    }

    $sps_card =~s:^.*[\\/]::;


    my $dfname = $query->param('dataFile0'); 
    my $dfsize = (stat( $dfname ))[7];    


    $logger->vdcLOG_info ( "VDC::DSB", "(Ingest)", 
			   "datafile: " . $dfsize . " bytes uploaded" );


    $logger->vdcLOG_info ( "VDC::DSB", "(Ingest)", 
			   "Entering Ingest; (POST parameters suppressed)" );





    my $ingest = DSB::Ingest->new ( $query, $logger, $self->{'TMPDIR'} ); 




    unless ( $ingest )
    {
	$self->{DSB_ERROR} = "could not initialize DSB::Ingest subsystem"; 
	$logger->vdcLOG_info ( "VDC::DSB", "(Ingest)",
				"could not initialize DSB::Ingest subsystem;" );
	print $self->DSB_StatusXML ( "500 Internal Server Error",
				     "error", 
				     "unknown", 
				     "",
				     [], 
				     [], 
				     "could not initialize DSB::Ingest subsystem" );

	return undef; 
    }


    $logger->vdcLOG_info ( "VDC::DSB", "(Ingest)", 
			   "DSB::Ingest subsystem initialized;" );


    my $base = "hdl:1902.0/LOCALHANDLE"; 

    $logger->vdcLOG_info ( "VDC::DSB", "(Ingest)", 
			   "using base=" . $base );

    # DATAFILES:
    # =========
    #
    # we attempt to convert the SPSS data files supplied as "data files" 
    # into subsettable tab-delimited format. If some of these conversion
    # attempts fail, it's not a fatal condition. The original data file
    # will still be ingested as "other materials" (but no subsetting will 
    # be available on it). We also want to return a detailed status report
    # indicating which files have been converted and which ones have failed.

    my @dfDO;

    my $datafiles_DO = \@dfDO;
    my $n_processed  = $ingest->prepare_DataDeposit ( $base, $params, $datafiles_DO ); 
    my $tab_files = [];
   
    if ( $n_processed > 0 )
    {
	$tab_files = $ingest->produce_TabFiles ( $base, $datafiles_DO, $sps_card ); 

	unless ( $sps_card || ( $#{$tab_files} >= 0 ) )
	{	    
	    my $xml; 
	    
	    $xml .= $self->{query}->header ( -Cache_control=>'max-age= 0', -status=>$code, -type=>'text/xml');
	    
	    $xml .= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	    $xml .= "  <Ingest version=\"1.1\">\n";

	    $xml .= "  <system status=\"failed\">" . $ingest->{INGEST_DATA_STATUS} . "</system>\n";
	    
	    $xml .= "  </Ingest>\n";

	    print $xml;
	    return undef; 
	}
    }

#    $xml .= $self->{query}->header ( -Cache_control=>'max-age= 0', -status=>$code, -type=>'text/xml');
#    print $self->{query}->header ( -Cache_control=>'max-age= 0', -type=>'text/tab-separated-values');

    $| = 1;

#    print $self->{query}->header ( -type=>'multipart/mixed; boundary="----vdc-ingest-multipart----"' );
    print "Content-type: multipart/mixed;boundary=\"=vdc-ingest-multipart\"\n\n";

    print "--=vdc-ingest-multipart\n";
    print "Content-Type: text/xml\n\n";

    open DATADDI, $ingest->{'data_ddi'}; 



    my $print_flag = 0; 

    while ( <DATADDI> )
    {
   
	if ( /<fileDscr/ )
	{
	    $print_flag = 1; 
	}

	print if $print_flag; 

	if ( /<\/fileDscr/ )
	{
	    last; 
	}
    }

    print "\n";

    print "--=vdc-ingest-multipart\n";
    print "Content-Type: text/xml\n\n";
    

    $print_flag = 0; 

    while ( <DATADDI> )
    {
   
	if ( /<dataDscr/ )
	{
	    $print_flag = 1; 
	}

	print if $print_flag; 

	if ( /<\/dataDscr/ )
	{
	    last; 
	}
    }
    print "\n";


    close DATADDI; 


    unless ( $sps_card_size )
    {

	print "--=vdc-ingest-multipart\n";
	print "Content-Type: text/tab-separated-values\n\n";


	my $file_o; 
    
	for $file_o ( @$datafiles_DO )
	{
	    next unless $file_o->{status} eq "ok";

	    my $file_o_name = $file_o->{'file'}; 


###    print tabfile!

	    open TF, $file_o_name; 

	    while ( <TF> )
	    {
		print $_; 
	    }

	    close TF; 
	}

	print "\n";
    }

    print "--=vdc-ingest-multipart--\n";

    return undef;     

#    print $self->DSB_StatusXML ( $status_code,
#				 $final_status, 
#				 $ddi_status, 
#				 "", 
#				 $datafiles_DO,
#				 [],
#				 $error_msg );

}

sub DSB_StatusXML {
    my $self = shift; 

    my $code = shift; 
    my $status = shift; 
    my $ddi = shift;

    my $uri = shift; 

    my $ddo = shift; 
    my $odo = shift;

    my $desc = shift; 


    my $xml = ""; 

    $xml .= $self->{query}->header ( -Cache_control=>'max-age= 0', -status=>$code, -type=>'text/xml');

    $xml .= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    $xml .= "  <Ingest version=\"0.9a\">\n";

    $xml .= "  <system status=\"" . $status . "\">" . $desc . "</system>\n";

    $xml .= "  <ddi url=\"" . $uri . "\" status=\"\">" . $ddi . "</ddi>\n";

    my $o; my $s; my $c = 0; 

    for $o ( @$ddo )
    {
	$o->{status} = "unknown" unless $o->{status};
	$s = $o->{status};
	$s = "error" unless ( $s eq "ok" || $s eq "unknown" );

	$xml .= "<data id=\"dataFile" . ($c) . "\" name=\"" . $o->{file} . "\" status=\"" . $s . "\">" . $o->{status} . "</data>\n";

	$c++;
    }

    $c = 0; 

    for $o ( @$odo )
    {
	$o->{status} = "unknown" unless $o->{status};
	$s = $o->{status};
	$s = "error" unless ( $s eq "ok" || $s eq "unknown" );

	$xml .= "<other id=\"othmFile" . ($c) . "\" name=\"" . $o->{input_file} . "\" status=\"" . $s . "\">" . $o->{status} . "</other>\n";

	$c++;
    }

    $xml .= "  </Ingest>\n";

    return $xml;
}



sub Disseminate {
    my $self = shift;
    my $params = shift;
    my $context = shift; 

    my $spec = $params->{'specification'}; 

    if ( !defined ( $spec ) )
    {
        return (400, "Malformed DSB verb: 'specification' is missing");
    }

    my $binder = $params->{'binder'}; 

    if ( $binder && $binder ne "tar" )
    {
	return (400, "The only binder supported is tar;");
    }

    my $encoding = $params->{'encoding'}; 

    if ( $encoding && $encoding ne "gzip" )
    {
	return (400, "The only supported encoding is gzip;");
    }
 
    print $self->{query}->header ( -type=>'text/xml');

    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Disseminate version=\"0.1\">\n";

    # disseminate...

    print "  </Disseminate>\n";

    return undef;
}

sub Diagnose {
    my ( $self, $params, $context ) = @_;
    my $dg = new VDCdiag;
    my $status;
    $dg->begin();
    $params->{'file_header'}='<? xml version="1.0" encoding="UTF-8" ?>' . "\n\n" . '<foo version="0.01"></foo>';
    $params->{'file_name'}='test.xml';
    
    $status = $dg->diagnose($self,"Analyze",$params);
    $dg->end();
    return undef;
}   

sub Analyze {
    my $self = shift;
    my $params = shift;
    my $context = shift; 


    my $header = $params->{'file_header'}; 
    my $filename = $params->{'file_name'}; 

    my $logger = $self->{logger}; 
    my $query  = $self->{query};
    my ($success);
    my $error="Unknown analyze error";
    my $a_type ="othermat";
    my $a_disposition="none";    
    my ($mime, $description, $fragfn) ;

    my @codebook_types = (
	"text/html", 
	"text/rtf",                                                                  
	"text/richtext",                                                                  
	"application/rtf",
	"application/msword",
	"application/vnd.ms-office",
 	"application/vnd.wordperfect", 
	"application/pdf",
	"application/postscript");

    $logger->vdcLOG_info ( "VDC::DSB", "(Analyze)", 
			   "Entering Analyze; (POST parameters suppressed)" );


   if (!$header) {
	$self->{DSB_ERROR} = "syntax error: missing header file"; 
	$logger->vdcLOG_info ( "VDC::DSB", "(Analyze)",
				"syntax error: missing header." );
	return (400, "syntax error: missing header");
   }

    my $header_decoded = decode_base64($header);


    $fragfn = $TMPDIR . "/" . "analyze_". $$. "_" . $filename ;	#should use TMP from buildit!

     if (!open (FRAG,">$fragfn")) 
	{  $error="couldn't create $fragfn"; goto ABORT;  }   
     binmode(FRAG);
     if (!syswrite(FRAG, $header_decoded)) 
	{  $error="couldn't write to $fragfn"; goto ABORT;  }   
     close(FRAG); 

     if (!open (FRAG,"<$fragfn")) 
	{  $error="couldn't reopen $fragfn"; goto ABORT;  }   

     binmode(FRAG);
    #if ($mime=$self->_checkfile(FRAG, $fragfn)){
    # $a_type="datafile";
    # $a_disposition="datafile";
    # $description = "Subsettable statistical data format";
    # $success=1;
    #} else {

    	if (!open(FILEC,"file -ibn '$fragfn'|"))  {  $error="couldn't run file "; goto ABORT;  }
    	my $fres=<FILEC>;
    	chomp($fres);
    	close(FILEC);
    	open(FILEC,"file -bn '$fragfn'|");
    	$description=<FILEC>;
    	chomp($description);
    	close(FILEC);
	
	if (($? >> 8) !=0 ) 	{
		# THIS IS SORT OF USELESS AS FILE NEVER RETURNS 
		# A GOOD EXIT CODE, IT'S HERE FOR GOOD FORM,
		# AND IN CASE FILE EVER GET'S UPDATED
    		 $error="internal error in 'file' command (". ($?>>8) . "):" . $fres ; 
		goto ABORT; 
	}
    	if (!$fres) { 
		#$error="error parsing file formats"; goto ABORT;  
		$logger->vdcLOG_debug( "VDC::DSB", "(Analyze)",
				"file could not parse format");
	    	$mime="application/octet-stream";
	}
    	$success=1;
        if ($fres=~/^(\S\S*)\/(\S\S*)[\s,;\n]/) {
	   $mime=$fres;
	   if (($fres=~/text\/plain/i) && ($description =~ /XML document/i)){
	    	$mime="text/xml";
	   }
        } else {
	    if ($description =~ /PDF\s+Document/i) {
	    	$mime="application/pdf";
	    } elsif ($description =~ /Microsoft\s+Office/i){
	    	$mime="application/vnd.ms-office";
	    }  else {
	    	$mime="application/octet-stream";
	    }
    	}
    #}


    ABORT:    unless ($success) 
    {
	$self->{DSB_ERROR} = $error; 
	$logger->vdcLOG_fatal ( "VDC::DSB", "(Analyze)",
				$error );
	return (500, $error);
###	unlink $fragfn;
    }
  
    for my $type (@codebook_types) {
	if ($mime =~/$type/) {
		$a_disposition="codebook";
		last;
	}
    }	

    print $self->{query}->header ( -type=>'text/xml');
    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print "  <Analyze version=\"0.1\">\n";
    print " <mime>" . $mime . "</mime>\n" ;
    print " <description>" . $description . "</description>\n" ;
    print " <services>\n";
    print "<Ingest type=\"$a_type\" disposition=\"$a_disposition\" />\n";
    print "</services>\n";
    print "</Analyze>\n";
#    unlink $fragfn;

    return undef; 
}    


sub _checkfile {
	my ($self, $dfname, $savedfile) = @_;
#	open(DTA, $dfname) or die "can't open $dfname: $!";
	binmode($dfname, ":raw");

	# sas transport file
	my $filesignature = 91;
	my $buff;
	read($dfname, $buff, $filesignature);

	my $template = "a80a11";
	my ($frst80, $next11) = unpack($template, $buff);
	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "first 80 bytes:" . $frst80 );
	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "next  11 bytes:" . $next11 );
	if ( ($frst80 eq 'HEADER RECORD*******LIBRARY HEADER RECORD!!!!!!!000000000000000000000000000000  ') 
		&& ($next11 eq 'SAS     SAS')) {
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "$dfname is a sas export file" );
		#return "application/x-sas-xport";
		return undef; # should'nt be returning subsettable if no input script
	} else {
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "$dfname is NOT a sas export file");
	}

	# return to the top of the file
 	seek $dfname, 0, 0;

	# stata and sav
	$filesignature = 4;
	read($dfname, $buff, $filesignature);
	# check the file against dta

	$template = "c4";
	my @first4b1 = unpack($template, $buff);
	my @frst4b = unpack("H8", $buff);
	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "contents(4 byte hex dump)=" . join("-", @frst4b) );
	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "contents(1st byte: int)=" . "$first4b1[0]");
	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "contents(2nd byte: int)=" . "$first4b1[1]");
	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "contents(3rd byte: int)=" . "$first4b1[2]");
	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "contents(4th byte: int)=" . "$first4b1[3]");	
	my $dtaRelNo = { 
		104 => rel_3,      105 =>  rel_4or5,  108 => rel_6, 110 => rel_7first,
		111 => rel_7scnd,  113 => rel_8_or_9, 114 => rel_10
	}; 
	if ($first4b1[2] != 1) {
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "$dfname is not sata type(failed: 1st step)");
	} elsif ($first4b1[1] != 1 && $first4b1[1] != 2) {
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "$dfname is not stata type(failed: 2nd step)");
	} elsif (!exists($dtaRelNo->{"$first4b1[0]"})) {
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "$dfname is neither stata-type nor one of the permissible release numbers(3rd step)");
	} else {
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "$dfname is a stata file: release number=" . $dtaRelNo->{"$first4b1[0]"} );
		return("application/x-stata");
	}

	# check the file against sav
	$template = "a4";
	my $first4b2 = unpack($template, $buff);
	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "The first 4 byte[expected value=\$FL2]:" . $first4b2);
	if ($first4b2 ne '$FL2'){
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "$dfname is not an spss system (.sav) file or the character representation code of $buff might be non-7bit-ASCII");
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "not an spss system (.sav) file or the character representation code of " . $buff  . "might be non-7bit-ASCII");
	} else {
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "$dfname is an sav file");
		return "application/x-spss-sav";
	}


	seek $dfname,  0, 0;
	binmode ($dfname);
	my $lines =[];
	my $line='';
	while (<$dfname>) {
		tr/\015//d;
		chomp $_;
		$line .= $_;
		push @{$lines}, length($_);
		last if ($. == 6);
	}
	close ($dfname);

	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "contents of 6 lines(por)=" . $line );

	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "length of 6 lines[should be 480](por)=" . length($line) );

	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","lines(len:por)=" . join('-',@{$lines}));
	my $bias =0;
	for (my $l=0;$l<=4;$l++){
		$bias += ($lines->[$l] - 80);
	}
	my $bias1st = $lines->[0]-80;
	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","bias(por)=" . $bias);
	if (uc(substr($line, (456+$bias), 8)) eq "SPSSPORT") {
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "$dfname is a por file(step1)");
		return "application/x-spss-por";
	} elsif ( (uc(substr($line, (40+$bias1st), 20)) eq "ASCII SPSS PORT FILE") && ($line->[5] =~ /SPSSPORT/i) ) {
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)", "$dfname is a por file(step2)");
		return("application/x-spss-por");
	}
	
	
	
	# RData format
	################################
	seek $dfname, 0, 0;

	$filesignature = 5;
	read($dfname, $buff, $filesignature);
	# step 1: read 5 byptes and check their pattern(up to 4th byte first)
	$template = "H2H2H2H2H2";
	#$templateB = "h2h2h2h2h2";
	my @first5b = unpack($template, $buff);
	#my @first5bB = unpack($templateB, $buff);
	
	# The firt 4 bytes of a GZIP file= 1f 8b 08 00
	$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","Check 1st 5 bytes: RData(H)=" . join("-", @first5b));
	#$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","Check 1st 5 bytes: RData(h)=" . join("-", @first5bB));
	if (join('',@first5b[0..1]) ne '1f8b'){
		# check the first 5 bytes, assuming it is ASCII type
		# if so, the hex pattern is 52-44-41-32-0a, etc.
		
		$template ="H*" ;
		my $first5byte = pack($template, join('', @first5b));
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","1st 5 bytes (Not gzipped): RData=" . $first5byte);

		if ($first5byte =~ /^RD[ABX][12]\012/){
			# cases: RDX2(non-ASCII format) or RDA2(ASCII format)
			# cases: RDX1(non-ASCII format) or RDA1(ASCII format)
			$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","This file is an ungzipped-RData type");
			return undef;
			#return("application/x-rlang-transport");
		} else {
			$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","This file is not an RData type");
			return undef;
		}
	} else {
		# this file is gzip-type
		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","This file is GZIP-type");
		# extra check:
		if ($first5b[3] eq '00') {
			$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","Header of this GZIP file is 10-byte");
		}
		# wind back the file and
		# gunzip it and check its contents
#		seek $dfname, 0, 0;
#		binmode ($dfname);

		close $dfname; 


		my $gzbuff;
		my $rdheadersize= 5;
		my $gz = gzopen($savedfile, 'rb');
	
		unless ( $gz )
		{
		    $self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","Error! could not open gzipped file");
		    return undef; 		    
		}

		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","opened gzipped header file");

		my $bytesread = $gz->gzread($gzbuff, $rdheadersize) ;

		$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","read 5 bytes");


		if ($bytesread == 5) {
			my $first5byte = pack("H*", join('',unpack("H2H2H2H2H2", $gzbuff)));
			$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","1st 5 bytes (gzipped): RData=" . $first5byte);

			if ($first5byte =~ /^RD[ABX][12]\012/){
				# cases: RDX2(non-ASCII format) or RDA2(ASCII format)
				# cases: RDX1(non-ASCII format) or RDA1(ASCII format)
				$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","This file is an gzipped-RData type");
				return undef;
				#return("application/x-rlang-transport");
			} else {
				$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","The expected byte-pattern was not found; This file is not an RData type");
				return undef;
			}
		} else {
			$self->{logger}->vdcLOG_info ( "VDC::DSB", "(Analyze)","error in ungunzipping the file");
		}
		$gz->gzclose();
	}
	
	return;
}



sub GetZeligConfig {
    my $self = shift;
    my $params = shift;
    my $context = shift; 
    my $q  = $self->{query};

	my $buffer;
	my $DEBUG=0;
	# R code to print the config file
	# library(VDCutil);  printZeligSchemaInstance('../configZeligGUI.xml')

	my $flnm = "$TMPDIR/configZeligGUI.xml";
	#my $server=`hostname -f`; use  $SERVER from glv03
	my $rCodeFile = "$TMPDIR/zeligConfig.$$.R";
	open(RH, "> $rCodeFile");
	# printZeligSchemaInstance(filename=NULL, serverName=NULL,vdcAbsDirPrefix=NULL)
	print RH "library(VDCutil);  printZeligSchemaInstance('" . $flnm ."',", "'",$SERVER,"')";
	close(RH);
	my $log_file = "$TMPDIR/zeligConfig.$$.log";
	open(FH, "> $log_file") if $DEBUG;
	open(PH, "R --slave < $rCodeFile  2>&1 |");
	while (<PH>) {
		print FH $_ if $DEBUG;
		chomp;
		if (/(Error\s+in)|(Execution\s+halted)/i){
			#print $FH "error in R run=",$1,"\t",$2,"\n" if $DEBUG;
			#print $FH "error in R run=",$_,"\n" if $DEBUG;
			#$runr=0;
		} else {
			#print $FH "Zelig config file is created\n" if $DEBUG;
		}
	}
	close(PH);
	close(FH) if $DEBUG;

	unless ($DEBUG) {
		unlink($rCodeFile);
	}
	my $size = -s $flnm;

	my $cntnttyp = "text/xml" ;  

	if (-r $flnm  && -f $flnm &&  open(dwnldFile, "<$flnm")) {
		print $q->header ( -type=>$cntnttyp);
		while (read dwnldFile, $buffer, $size){
			print $buffer;
		}
		close(dwnldFile);
		unlink($flnm);
	} else {
		return ( 404, 'Not Found: Temporary Results Deleted');
	}

	return undef; 
}


1;
