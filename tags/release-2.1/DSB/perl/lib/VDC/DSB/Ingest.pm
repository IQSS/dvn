
package DSB::Ingest; 

use vars qw(%SUPPORTED_FORMATS $VDC_SCRIPT_PATH @ENTITY_INVALID_255);

use Exporter;
##use CGI;

#@SUPPORTED_FORMATS = ( "application/x-stata", "application/x-spss-por", "application/x-spss-sav" ); 
%SUPPORTED_FORMATS = ( 
		       "application/x-stata" => "dta",
		       "application/x-spss-por" => "por", 
		       "application/x-spss-sav" => "sav" ); 

$VDC_SCRIPT_PATH   = '/usr/local/VDC/sbin';

$ENTITY_INVALID_255[0] = 1;
$ENTITY_INVALID_255[1] = 1;
$ENTITY_INVALID_255[2] = 1;
$ENTITY_INVALID_255[3] = 1;
$ENTITY_INVALID_255[4] = 1;
$ENTITY_INVALID_255[5] = 1;
$ENTITY_INVALID_255[6] = 1;
$ENTITY_INVALID_255[7] = 1;
$ENTITY_INVALID_255[8] = 1;
$ENTITY_INVALID_255[11] = 1;
$ENTITY_INVALID_255[12] = 1;
$ENTITY_INVALID_255[14] = 1;
$ENTITY_INVALID_255[15] = 1;
$ENTITY_INVALID_255[16] = 1;
$ENTITY_INVALID_255[17] = 1;
$ENTITY_INVALID_255[18] = 1;
$ENTITY_INVALID_255[19] = 1;
$ENTITY_INVALID_255[20] = 1;
$ENTITY_INVALID_255[21] = 1;
$ENTITY_INVALID_255[22] = 1;
$ENTITY_INVALID_255[23] = 1;
$ENTITY_INVALID_255[24] = 1;
$ENTITY_INVALID_255[25] = 1;
$ENTITY_INVALID_255[26] = 1;
$ENTITY_INVALID_255[27] = 1;
$ENTITY_INVALID_255[28] = 1;
$ENTITY_INVALID_255[29] = 1;
$ENTITY_INVALID_255[30] = 1;
$ENTITY_INVALID_255[31] = 1;

# CONSTRUCTOR: 

sub new {
    my $skip = shift;
    my $query = shift;
    my $logger = shift; 
    my $tmpdir = shift; 

    my $self = bless {};

    # we inherit some objects from the parent object (DSB): 

    $self->{query} = $query if $query; 
    $self->{logger} = $logger if $logger;

    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", "(new)", "initialized itself; reusing the supplied logger and query objects" ); 

    $self->{'TMPDIR'} = $tmpdir; 

    return $self;
}

# METHODS: 



sub produce_TabFiles {

    my $self = shift;
    my $base = shift; 
    my $dep_object  = shift;
    my $control_card = shift; 

    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", "(produce_TabFiles)", "attempting to convert " . ($#{$dep_object} + 1) . " datafile(s)" ); 


    my $ret = []; 

    my $format; 

    my $f;
    my @filetypes; 

    return $ret if $#{$dep_object} == -1; 

    for $f ( @$dep_object )
    {
	if ( $f->{status} eq "ok" ) 
	{
	    push @filetypes, $f->{mime};
	    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", "(produce_TabFiles)", "adding " . $f->{input_file} . "..." ); 
	}
    }


    if  ( $control_card )
    {
	$format = "sps"; 
    }
    else
    {
	unless  ( $format = $self->check_FileFormats ( @filetypes ) )
	{
	    # {INGEST_ERROR} has already been set; 
	    $self->{logger}->vdcLOG_fatal ( "VDC::DSB::Ingest", "(produce_TabFiles)", "bad input datafile format: " . $self->{INGEST_ERROR} ); 
	    return undef; 
	}
    }

    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", "(produce_TabFiles)", "datafile format extension ok: " . $format ); 

    my $success = $self->run_ConverterScripts ( $format, $base, $ret, $dep_object, $control_card );

    return $ret if $success;

    return undef;
}

sub run_ConverterScripts {
    my $self   = shift; 

    my $format = shift; 
    my $base   = shift; 
    my $res    = shift; 
    my $dep_object = shift; 
    my $control_card = shift; 


    my @files; 
    my $file_hold; 
    my $f; 

    for $f ( @$dep_object )
    {
	push @files, $f->{input_file} if $f->{status} eq "ok";
    }

    my $size   = 0; 
    my $status; 
    my $status_code = 1; 

    my $script = ""; 

    if ( $control_card ) 
    {
	$script = $DSB::Ingest::VDC_SCRIPT_PATH . "/DDIsec4sps.pl"; 
    }
    else
    {
	$script = $DSB::Ingest::VDC_SCRIPT_PATH . "/read" . $format . ".pl"; 
    }

    unless  ( -f $script )
    {
	$self->{INGEST_ERROR} = "cannot find data converter script! ($script)";
	$self->{logger}->vdcLOG_fatal ( "VDC::DSB::Ingest", "(run_ConverterScripts)", $self->{INGEST_ERROR} ); 
	return undef; 
    }

    my $tmpdir = $self->{'TMPDIR'}; 


#    my $dirandprefix="/tmp/ingest.$$";
    my $logfile = $tmpdir . "/ingest." . $$ . ".log";
    my $dataddi = $tmpdir . "/" . $$ . ".data.xml"; 


    for $file_hold ( @files )
    {
	$file_hold = "'" . $file_hold . "'"; 
    }

    my $command = ""; 

    if ( $control_card ) 
    {
	$control_card = $tmpdir . "/" . $control_card;
	$command = join ( " ", ( $script, $logfile, $dataddi, "sps", $control_card, @files ) );
    }
    else
    {
	$command = join ( " ", ( $script, $logfile, $dataddi, @files ) );
    }

    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", "(run_ConverterScripts)", 
				    "I'm going to run $command..." ); 


    sleep 1; 

    $ENV{'PATH'} = $ENV{'PATH'} . ":/usr/local/VDC/bin:/usr/local/VDC/sbin";
    $ENV{'LANG'} = ""; 

    system ( "(cd /tmp; " . $command . " >/dev/null)" ); 

    my $c; 

    # let's check if we got the files: 

    unless ( $control_card )
    {
	my $tabfile; 

	for $f ( @$dep_object )
	{
	    next if $f->{status} ne "ok";

	    $tabfile = $f->{input_file};
	    unless ( $tabfile =~s/\.[^\.]*$/.tab/ )
	    {	    
		$tabfile .= ".tab";
	    }

	    unless ( ( $size = (stat($tabfile))[7] ) > 0  )
	    {
		$f->{status} = "conversion script failed";
		$self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
					       "(run_ConverterScripts)", 
					       "failed to produce $tabfile" ); 
		$self->{INGEST_DATA_STATUS} = "converter script failed to produce tab-delimited data file";
		return undef;
	    }
	    else
	    {
		push @$res, $tabfile;
		$f->{mime} = "text/plain";
		$f->{mime_u} = "tab";
		$self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
					       "(run_ConverterScripts)", 
					       $tabfile . " produced (" . $size . " bytes)" ); 

	    }

	}
    }

    $self->{'data_ddi'} = $dataddi;

    unless ( ( $size = (stat($self->{'data_ddi'}))[7] ) > 0  )
    {
	$status_code = undef; 
	$self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
				       "(run_ConverterScripts)", 
				       "(failed to produce data-level ddi" . $dataddi . ")" );
	$self->{INGEST_DATA_STATUS} = "converter script failed to produce DATA-level DDI.";
	return undef;
    }

    $self->check_xmlCharEntities ( $dataddi ); 

    $self->{INGEST_DATA_STATUS} = "<dataddi>data-level ddi xml produced (" . $size . " bytes)</dataddi>\n";
    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
				   "(run_ConverterScripts)", 
				   "data-level DDI produced (" . $size . " bytes)" );
    
    return $status_code; 
}

sub prepare_DataDeposit {
    my $self   = shift; 
    my $base   = shift; 
    my $params = shift; 
    my $ret    = shift; 

    return $self->prepare_FileDeposit ( 'data', $base, $params, $ret ); 
}

sub prepare_OthmDeposit {
    my $self   = shift; 
    my $base   = shift; 
    my $params = shift; 
    my $ret    = shift; 

    return $self->prepare_FileDeposit ( 'othm', $base, $params, $ret ); 
}

sub prepare_FileDeposit {

    my $self   = shift; 
    my $type   = shift; 
    my $base   = shift; 
    my $params = shift; 
    my $ret    = shift; 

    my $e; 

    my $c = 0; 
    my $ret_n = 0; 

    my $fname; 
    my $size; 

    my $tmpdir = $self->{'TMPDIR'}; 

    while ( defined ( $params->{'controlCard' . $c} ) )
    {
	$fname = $params->{'controlCard' . $c};

##	$self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
##				       "(prepare_FileDeposit)", 
##				       "processing ccard " . $fname ); 

	if ( ( $size = (stat( $fname ))[7] ) > 0 )
	{
	    my $ccard_localfile = $fname; 
	    $ccard_localfile =~ s:^.*[\\/]::;
	    $ccard_localfile =~ s:^:$tmpdir/:;
	    
	    my $bytes = 0; 

	    open ( LOCAL, ">" . $ccard_localfile ); 

	    while ( <$fname> )
	    {
		print LOCAL $_; 
		$bytes += length ( $_ ); 
	    }

	    close LOCAL;
#	    close UPLOADED;

##	    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
##				       "(prepare_FileDeposit)", 
##				       "ccard: " . $bytes . " bytes read;" ); 
	}
	$c++;
    }

    $c = 0; 


    while ( defined ( $params->{$type . 'File' . $c} ) )
    {
	$fname = $params->{$type . 'File' . $c};

##	$self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
##				       "(prepare_FileDeposit)", 
##				       "processing " . $fname ); 

	$e = {};
	
	$e->{'input_file'} = $fname; 
	$e->{'input_file'} =~ s:^.*[\\/]::;
	$e->{'input_file'} =~ s:^:$tmpdir/:;

	if ( ( $size = (stat( $fname ))[7] ) > 0 )
	{

##	    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
##				       "(prepare_FileDeposit)", 
##				       $size . " bytes uploaded;" ); 

	    $e = {};

	    $e->{'input_file'} = $fname; 
	    $e->{'input_file'} =~ s:^.*[\\/]::;
	    $e->{'input_file'} =~ s:^:$tmpdir/:;
	    
	    my $bytes = 0; 

	    open ( LOCAL, ">" . $e->{'input_file'} ); 

	    while ( <$fname> )
	    {
		print LOCAL $_; 
		$bytes += length ( $_ ); 
	    }

	    close LOCAL;
#	    close UPLOADED;

##	    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
##				       "(prepare_FileDeposit)", 
##				       $bytes . " bytes read;" ); 


	    if ( $type eq 'data' )
	    {
		$e->{file} = $e->{input_file};

		$e->{name} = $e->{file};
		$e->{name} =~s:^.*/::g;
		$e->{name} = $base . "/" . $e->{name};

		unless ( $e->{file} =~s/\.[^\.]*$/.tab/ )
		{		    
		    $e->{file} .= '.tab';
		}
	    }
	    elsif ( $type eq 'othm' )
	    {
		$e->{file} = $e->{'input_file'}; 
		$e->{input_file} =~ s/^.*[\\\/]//;
		$e->{name} = sprintf ( "%s/%s", $base, $e->{input_file} );
	    }
#	    else
#	    {
#		$self->{logger}->vdcLOG_fatal ( "VDC::DSB::Ingest", 
#				       "(prepare_FileDeposit)", 
#				       "files can only be ingested as 'data' or 'othm'!" ); 
#		$self->{INGEST_ERROR} = 
#		    "files can only be ingested as 'data' or 'othm' (\"other materials\").";
#		return undef; 
#	    }

	    $e->{class} = $params->{$type . 'FileClass' . $c}; 
	    $e->{class} = 'vdcClass=PUBLIC_OBJ,o=vdc' unless $e->{class}; 

	    $e->{mime} = $params->{$type . 'FileMime' . $c}; 
#	    $e->{mime} = 'text/plain' unless $e->{mime}; 

	    $e->{labl} = $params->{$type . 'FileLabel' . $c}; 
	    $e->{desc} = $params->{$type . 'FileDesc' . $c}; 
	    $e->{note} = $params->{$type . 'FileNote' . $c}; 

	    $e->{filen} = $params->{$type . 'FileName' . $c}; 

	    $e->{mime_u} = 'plain'; 

	    $ret_n++;

	    $e->{status} = "ok";

	}
	else
	{
	    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
				       "(prepare_FileDeposit)", 
				       "input data file not found or empty file." ); 

#	    $self->{INGEST_ERROR} = "input data file not found or empty file.";
#	    return undef; 

	    $e->{status} = "uploaded file not found or empty";
	}

	$$ret[$c] = $e; 
	$c++;
    }

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", 
##				   "(prepare_FileDeposit)", 
##				   "processed $c items; $ret_n files \"good\". returning" ); 
    return $ret_n; 
}

sub DepositFiles {

    my $self   = shift; 
    my $rep    = shift; 
    my $deposit_object  = shift; 
    my $rap    = shift;
    my $study_uri = shift; 

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", "(DepositFiles)", 
##				   "attempting to initialize RAP object;" ); 

#    my $rap = DSB::RAP->new ( $self->{query}, $self->{logger}, ".harvard.edu" ); 

    my $f; 
    my $uri; 

    my $name, $class, $mime, $mime_u, $filen, $gfile; 

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", "(DepositFiles)", 
##				   "attempting to deposit " . ($#{$deposit_object}+1) . " file(s)" ); 

    my $ret = "ok";

    for $f ( @$deposit_object )
    {
	next unless $f->{status} eq "ok";

	$uri = "http://" . $rep . "/VDC/Repository/0.1/Create";

	$name   = $f->{'name'};
	$class  = $f->{'class'};
	$mime   = $f->{'mime'};
	$mime_u = $f->{'mime_u'};
	$filen  = $f->{'filen'};

	unless ( $rap->create_RepositoryMeta ( $uri, $name, $class, $mime, $mime_u, $filen, $study_uri ) )
	{
	    $self->{logger}->vdcLOG_fatal ( "VDC::DSB::Ingest", "(DepositFiles)", 
					   "could not create repository metadata: " . $rap->{RAP_ERROR} ); 
	    $self->{INGEST_ERROR} = $rap->{RAP_ERROR}; 
	    $self->{status} = "failed to create rep meta";
	    $ret =  undef; 
	    next; 
	}

	$uri = "http://" . $rep . "/VDC/Repository/0.1/Deposit";

	$file = $f->{'file'};

	unless ( $rap->deposit_RepositoryObject ( $uri, $name, $file ) )
	{
	    $self->{INGEST_ERROR} = $rap->{RAP_ERROR}; 
	    $self->{logger}->vdcLOG_fatal ( "VDC::DSB::Ingest", "(DepositFiles)", 
					   "could not deposit the object: " . $rap->{RAP_ERROR} ); 
	    $self->{INGEST_ERROR} = $rap->{RAP_ERROR}; 
	    $f->{status} = "could not deposit";
	    $ret = undef; 
	    next; 
	}

	$self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", "(DepositFiles)", 
				       "successfully deposited $name" ); 
    }

    return $ret; 
}
    

sub check_FileFormats {
    my $self = shift; 
    my @mimetypes = @_;

    my $format; 

#    return undef unless $format = $self->check_FileExtension ( @files ); 
    return undef unless $format = $self->check_SuppliedMimeTypes ( @mimetypes ); 

#    return undef unless $self->check_SupportedFormat ( $format ); 

    return $SUPPORTED_FORMATS{$format}; 
}

sub check_SuppliedMimeTypes {
    my $self = shift; 
    my @mimetypes = @_;

    my $mime = shift @mimetypes; 

    unless ( $mime )
    {
	$self->{INGEST_ERROR} = "mime type specifying the format of the data file is required";
	return undef; 
    }

    my $mime_next; 
    my $m; 

    for ( $m = 0; $m<=$#mimetypes; $m++ )
    {
	$mime_next = $mimetypes[$m]; 

	unless ( $mime_next )
	{
	    $self->{INGEST_ERROR} = "mime type specifying the format of the data file is required";
	    return undef; 
	}

	if ( $mime ne $mime_next )
	{
	    $self->{INGEST_ERROR} = "Mixed format datafiles not supported.";
	    return undef;
	}

	$mime = $mime_next; 
    }

    return $mime; 
}



sub check_FileExtension {
    my $self = shift; 
    my @files = @_;

    my $f = shift @files; 
    my $ext;
    my $next; 

    if ( $f =~ /\.([^\.]*)$/ )
    {
	$ext = $1; 
    }
    else
    {
	$self->{INGEST_ERROR} = "data file's extension must specify its format";
	return undef; 
    }

    while ( $f = shift @files )
    {
	$f =~ /\.([^\.]*)$/;
	$next = $1; 

	if ( $ext ne $next )
	{
	    $self->{INGEST_ERROR} = "Mixed format datafiles not supported.";
	    return undef;
	}
    }

    return $ext; 
}

sub check_xmlCharEntities {
    my $self = shift @_; 
    my $ddi = shift @_; 

    open ( F, $ddi ) || return 0; 
    open ( OUT, '>' . $ddi . ".edited" ) || return 0; 

    my $illegal_entity_removed = 0; 
    my $line_saved = ""; 

    while (<F>)
    {
	if ( /&\#x?[0-9a-zA-Z]*\;/ )
	{
	    $line_saved = $_; 
	    s/(&\#x?[0-9a-zA-Z]*\;)/&entcheck($1)/ge;
	    $illegal_entity_removed++ if $line_saved ne $_; 
	}
	print OUT; 
    }
  
    close F; 
    close OUT; 

    if ( $illegal_entity_removed )
    {
	system ( "mv -f $ddi" . ".edited $ddi" ); 
    }

    return $illegal_entity_removed; 
}

sub entcheck {
    my $entity = shift @_; 

    my $charcode; 

    $entity=~/&\#(.*);/; 
    my $numeric = $1; 

    if ( $numeric =~/^x([0-9a-fA-F]*)$/ )
    {
	$charcode = hex ( $1 ); 
    }
    elsif ( $numeric =~/^[0-9]*$/ )
    {
	$charcode = $numeric; 
    }
    else
    {
	return $entity; 
    }

    if ( $ENTITY_INVALID_255[$charcode] == 1 )
    {
	return ""; 
    }

    return $entity; 
}



#sub check_SupportedFormat {
#    my $self = shift; 
#    my $format = shift; 
#
#    my $s; 
#
#    for $s ( @SUPPORTED_FORMATS )
#    {
#	return $format if $s eq $format; 
#    }
#
#    $self->{INGEST_ERROR} = "Unsupported format (" . $format . ")\n";
#    return undef; 
#}

sub DDIedit_AddDataFiles {
    my $self = shift; 
    my $ddi  = shift; 
    my $do   = shift; 
    my $rep  = shift; 

    # i simply append the new datafiles to whatever
    # files might already be in the ddi; if one of the files
    # i'm trying to add is already there, i'm replacing it. 


    my $c = $#{@$do};

    return $ddi unless $c >= 0; 

    my $head;
    my $tail; 

    ( $head, $tail ) = split ( /<\/stdyDscr>/si, $ddi, 2 ); 

    $head .= "</stdyDscr>"; 

    if ( $tail =~ '<fileDscr' )
    {
	$tail  =~ s:<fileDscr.*</fileDscr>::si; 
    }

    if ( $tail =~ '<dataDscr' )
    {
	$tail  =~ s:<dataDscr.*</dataDscr>::si; 
    }

    my $dataddi = "";

    unless ( open ( DATADDI, $self->{'data_ddi'} ) )
    {
##	$self->{logger}->vdcLOG_fatal ( "VDC::DSB::Ingest", (DDIedit_AddDataFiles), "could not open data ddi file (" . $self->{'data_ddi'} . ")" );
	return $ddi; 
    }


    while ( <DATADDI> )
    {
	$dataddi .= $_; 
    }
    close DATADDI;

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", (DDIedit_AddDataFiles), "read the ddi produced, (" . (length($dataddi)) . " bytes)" );


    # let's create the description entries for the incoming
    # datafiles:

    my %uris_new; 
    my $filedesc_new = $self->create_FileDesc ( $dataddi, $do, $rep, \%uris_new ); 

    my @unf_strings; 

    while ( $filedesc_new =~/(UNF:[^ \t\n<]*)/g )
    {
	push @unf_strings, $1; 
#	$self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", (DDIedit_AddDataFiles), "(new) UNF found: " . $unf_strings[$#unf_strings] );
    }

    my $datadesc_new = $self->create_DataDesc ( $dataddi, $do, $rep ); 

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", (DDIedit_AddDataFiles), "produced the new datafiles ddi section, (" . (length($datadesc_new)) . " bytes)" );

    # now, let's see what's already in the DDI:

    my $filedesc_old = "";

    my @filedesc_old = $self->{xml}->DSB_XML_extract ( $ddi, "fileDscr" );

    my $e;
    my $uri; 

    # now we have to go thru the old filedescriptions, throw out the 
    # ones where the files have been updated and rebuild the file ids
    # for the ones that stay:

    my $id = $c + 1; 
    my @replaced; 

    for $e ( @filedesc_old )
    {
	$uri = $self->{xml}->DSB_XML_extract_uri ( $e );

	unless ( $uris_new{$uri} )
	{
	    $id++; 

	    $e =~ s/ID=\"file([0-9]*)\"/ID=\"file$id\"/si;

	    $replaced[$1] = $id; 

	    $fd_old .= $e; 
	    $fd_old .= "\n";

	    if ( $e=~/(UNF:[^ \t\n<]*)/ )
	    {
		push @unf_strings, $1; 
##		$self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", (DDIedit_AddDataFiles), "UNF found: " . $unf_strings[$#unf_strings] );
	    }
	}
	
    }

    my @datadesc_old = $self->{xml}->DSB_XML_extract ( $ddi, "var" );


    # Now, we have to get rid of the variables in the old variable
    # description section of the DDI and re-assign the fileid 
    # references

    my $dd_old; 

    for $e ( @datadesc_old )
    {
	$e =~ m/ID=\"v([0-9]*)\./si;
	$id = $1; 

	my $replacedid;

	if ( $replacedid = $replaced[$id] )
	{
	    $e =~ s/ID=\"v([0-9]*)\./ID=\"v$replacedid./;
	    $e =~ s/(fileid=\"file)[0-9]*\"/$1$replacedid\"/;
	    $dd_old .= $e; 
	}
    }

    $datadesc_new =~s:</dataDscr>::si;
    $dd_old.="</dataDscr>";


    # one last thing to do: we need to generate the study-level
    # UNF signature from the signatures of the datafiles in the
    # DDI (that we found earlier)


    my $unf_study = $self->calculate_UNF ( @unf_strings ); 

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::Ingest", (DDIedit_AddDataFiles), "study-level UNF: " . $unf_study );

    $head = $self->{xml}->DSB_XML_insert_StudyUNF ( $head, $unf_study ); 

    $ddi = $head . "\n" .
	$filedesc_new . "\n" . 
	    $fd_old . "\n" . 
		$datadesc_new . "\n" . 
		    $dd_old . "\n" .
			$tail; 
    
    return $ddi; 

}

sub updateStudyUNF {
    my $self = shift; 
    my $ddi  = shift; 

    my $fileDesc; 

    if ( $ddi =~/<\/stdyDscr>(.*)<dataDscr/s )
    {
	$fileDesc = $1; 
    }

    my @unf_strings; 

    while ( $fileDesc=~/(UNF:[^ \t\n<]*)/ )
    {
	push @unf_strings, $1; 
    }

    if ( $#unf_strings > -1 )
    {
	my $unf_study = $self->calculate_UNF ( @unf_strings ); 

	$ddi = $self->{xml}->DSB_XML_insert_StudyUNF ( $ddi, $unf_study ) if $unf_study;
    }

    return $ddi; 
}

sub create_DataDesc {
    my $self = shift; 
    my $ddi  = shift; 
    my $do   = shift; 
    my $rep  = shift; 


    $ddi =~ s:^.*(<dataDscr):$1:si; 
    $ddi =~ s:(</dataDscr>).*$:$1:si; 

    return $ddi; 
}

sub create_FileDesc {
    my $self = shift; 
    my $ddi  = shift; 
    my $do   = shift; 
    my $rep  = shift; 
    my $ref  = shift; 

    unless ( $ddi =~ /<fileDscr/ )
    {
	$self->{INGEST_ERROR} = "generated DDI contains no fileDscr tags.";
	$self->{logger}->vdcLOG_fatal ( "VDC::DSB::Ingest", "(create_FileDesc)", $self->{INGEST_ERROR} ); 
	return undef; 
    }

    my @fd = split ( /<fileDscr[^>]*>/, $ddi ); 

    shift @fd; 

    $fd[$#fd] =~ s:</fileDscr.*$:</fileDscr>:si; 

    my $c; 
    my $uri; 
    my $note;
    my $fileTxt; 


    for ( $c = 0; $c <= $#fd; $c++ )
    {
	$uri = "http://" . $rep . "/VDC/Repository/0.1/Access/" . $$do[$c]->{'name'};
	$ref->{$uri} = $c+1; 

	$fd[$c] = "<fileDscr ID=\"file" . ($c+1) . "\" URI=\"http://" . 
	    $rep . "/VDC/Repository/0.1/Access/" . $$do[$c]->{'name'} . 
		"\">" . $fd[$c]; 

	$fileTxt = "\t\t<fileName>" . $do->[$c]->{'labl'} . "</fileName>\n" . 
	           "\t\t<fileCont>" . $do->[$c]->{'desc'} . "</fileCont>\n";
						 
	$fd[$c] =~s:(<fileTxt>[ \n]*):$1$fileTxt:si;

	$note = "\t\t<notes type=\"vdc:category\">" . $$do[$c]->{'note'} . "</notes>\n\t\t";
#	$note = "\t\t<notes type=\"icpsr:category\" subject=\"description\">" . $$do[$c]->{'note'} . "</notes>\n\t\t";

	$fd[$c] =~ s:(</fileDscr):$note$1:si; 
    }

    return join ( "", @fd ); 
}

sub calculate_UNF {
    my $self = shift; 
    my @unfstrings = @_; 

    @unfstrings = sort {($a=~/:([^:]*)$/)[0] cmp ($b=~/:([^:]*)$/)[0]} @unfstrings; 

    for my $unf (@unfstrings)
    {
	$unf = '"' . $unf . '"'; 
    }

    my $unfstrings_merged = join (",", @unfstrings); 

    my $R_code = 'library("UNF"); cat(as.character(summary(as.unf(c(' . $unfstrings_merged . ')))))'; 

    my $unf_study = `echo '$R_code' | R --slave`; 

    my @tmp_tokens = split ( "\n", $unf_study ); 

    $unf_study = join ( "\n", grep ( /^UNF:/, @tmp_tokens ) ); 

    return $unf_study; 
}


# this code is dead (though functional) -- since the DSB/Ingest modules
# don't get called to process otherMaterials anymore;

sub DDIedit_AddOthmFiles {
    my $self = shift; 
    my $ddi  = shift; 
    my $do   = shift; 
    my $rep  = shift; 

    # i simply append the new othermats to whatever
    # othermat files might already be in the ddi; if one of the files
    # i'm trying to add is already there, i'm replacing it. 


    # ok, so let's put together the description for the incoming files:

    my $othm_new = ""; 
    my %othm_new_uris;

    my $e; 
    my $desc;
    my $uri; 

    for $e ( @$do )
    {
	$uri = "http://" . $rep . "/VDC/Repository/0.1/Access/" . $$e{'name'};
	$othm_new_uris{$uri} = 1; 

	$desc = "<otherMat type=\"other\" level=\"study\"  URI=\"" . $uri . "\">\n";

	$desc .= "\t<labl>"  . $$e{'name'} . "</labl>\n";
	$desc .= "\t<txt>"   . $$e{'desc'} . "</txt>\n";
	$desc .= "\t<notes>" . $$e{'note'} . "</notes>\n";

	$desc .= "</otherMat>\n";

	$othm_new .= $desc;
    }

    # now, let's see what's already in the DDI:

    my $othm_old = "";

    my @om_existing = $self->{xml}->DSB_XML_extract ( $ddi, "otherMat" );

    for $e ( @om_existing )
    {
	$uri = $self->{xml}->DSB_XML_extract_uri ( $e );

	unless ( $othm_new_uris{$uri} )
	{
	    $othm_old .= $e; 
	    $othm_old .= "\n";
	}
    }

    $ddi =~ s:</codeBook.*$::si; 
    $ddi =~ s/<otherMat.*$//si;

    $ddi = $ddi . $othm_old . $othm_new . "\n</codeBook>\n"; 
    
    return $ddi; 


}


1; 







