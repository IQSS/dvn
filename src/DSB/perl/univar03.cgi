#!/usr/bin/perl -I/usr/local/VDC/perl -I/usr/local/VDC/perl/lib -I/usr/local/VDC/perl/lib/VDC
# Copyright (C) 2001-4 President and Fellows of Harvard University
#	  (Written by Akio Sone)
#	  (<URL:http://thedata.org/>)
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
# USA.
# 
# Redistributions of source code or binaries must retain the above copyright
# notice.
#

select(STDERR); $| = 1;
select(STDOUT); $| = 1;
# use strict;
use vdcLOG;
use sigtrap qw(die untrapped normal-signals stack-trace any error-signals);
use sigtrap qw(handler termHandler SIGTERM); # handle Apache timeout 

my ($logger)= new_vdcLOG();
$logger->vdcLOG_info ( 'VDC::DSB', '(init)', 'starting initialization' );

#use vdcPROGRESS;
use vdcCGI;
use CGI::Cookie;
use vdcLWP;

use vdcRAP::Subsetting;
use vdcRAP::SubsettingMeta;

use HTTP::Cookies;
use HTTP::Request;
use HTTP::Response;
use HTTP::Request::Common qw(POST);
use Data::Dumper;
use File::Copy;
use URI::Escape;

#
use XML::SAX::ParserFactory;
$XML::SAX::ParserPackage = "XML::SAX::Expat";
use VDC::DSB::DataService;
use VDC::DSB::CaseWiseSubset;
use VDC::DSB::DDISAXparser;
use VDC::DSB::varMetaDataDirect;
use VDC::DSB::StatCodeWriter;
use DSB::Temp; 
our $DEBUG;  # debugging?

# performance
our $NICE=20; # nice value
my $MEMTHRESH = .95; #active memory threshold to pause
my $LOADTHRESH= 10; #load thresh
my ($MAX_URL) = 2000; # max GET URL length
my $QWAIT= 20; #active memory threshold to pause
my($script_name) = $ENV{'SCRIPT_URL'};

require "glv03";

our $q = new CGI;
our ($Rcode,$RtxtOutput, $RtmpData, $RvlsPrfx, $SRVRCGI, $RtmpHtml, $DwnldPrfx, $DataPrfx,$ddiforR, $RtmpDataRaw);

my $stdyttl =    $q->param('studytitle');
my $stdyno  =    $q->param('studyno');
my $dataURL =    $q->param('URLdata');
my $ddiURL  =    $q->param('uri');
my $fileid  =    $q->param('fileid'); 
my $appSERVER  = $q->param('appSERVER'); 
my $studyURL  =  $q->param('studyURL');
my $browserType= $q->param('browserType');
my $packageType= $q->param('packageType'); 

my $studyLink =  $studyURL . "/faces/study/StudyPage.jsp?studyId=" . $stdyno  . '&tab=files';
my($tmpurl0, $datasetname) = split('=', $dataURL);
$script_name .="($dataURL)";

# temp dir check

my $temp_monitor = new DSB::Temp $TMPDIR; 
unless ( $temp_monitor->check_TempDirectory )
{
		print $q->start_html ( -title=> "error -- failed to create temp directories");
		print $q->h2("The cgi script failed to create a working directory ($TMPDIR, etc.)" );
		print $q->end_html;
		$logger->vdcLOG_warning( 'VDC::DSB',  $script_name,  "Server Error: could not create temp directories.");
		{my($ts)=$@; &exitChores; die $ts;}
}

# working dir
my $TMPDSBDIR =   "DSB";
my $TMPDSBDIRFULL = "${TMPDIR}/${TMPDSBDIR}";


open(TMPX, ">${TMPDIR}/${TMPDSBDIR}/args.$$.svd");
print TMPX "CGI-pm object:\n", Dumper($q);
my $frmprm  = { %{$q->Vars} };
print TMPX "raw CGI params:\n", Dumper($frmprm);
print TMPX "appServer:". $appSERVER . "\n";
my $DSBWRKDIR=$TMPDIR;
my $CGIparamSet = VDC::DSB::DataService->new(RAWCGIPARAM=>$frmprm, WRKDIR=>$DSBWRKDIR);

# ///////////////////////////////////////////////
# check the contents of the query string
# ///////////////////////////////////////////////

print TMPX "CGI params:\n" .  Dumper($CGIparamSet) . "\n\n";

print TMPX "\nstudy Link=$appSERVER$studyLink\n\n";
# ///////////////////////////////////////////////
# L.A.: COOKIES:
# ///////////////////////////////////////////////

#my %cookies =  CGI::Cookie->raw_fetch;
# Create the user agent to make the HTTP call

my  $ua  = new vdcLWP;
$ua->timeout ( 15 * 60 ); 

# If a cookie is passed to the function, repackage it and send it along

my $vdcCookie_value = $q->cookie('VDCSESSIONID');
#print STDERR " VDCCookie: $vdcCookie_value";

if ( $vdcCookie_value ){
	# Construct a cookie specifically for the Authorize function with the
	# correct vdcCookie value

	my $auth_cookies = HTTP::Cookies->new;

	# build the domain for the cookie

	if ($dataURL =~ m#(\.\w+\.\w+)/VDC/#) {
		$domain = $1;
	}

	my $server_port = "80"; # ???

	$auth_cookies->set_cookie(1, 'VDCSESSIONID', $vdcCookie_value,
	'/VDC',
	$domain,
	$server_port,
	0, 0, 10000, 1);
	#$ua->cookie_jar($auth_cookies);
}

# ///////////////////////////////////////////////
# Set up the returned html page (analysis case)
# ///////////////////////////////////////////////
# initialize progress
#
my $dtdwnldf = $CGIparamSet->getDwnldType();
	my $progress;
        if ( 0 )
	{
		$progress = new vdcPROGRESS;
			my @stages= ("Starting Up", "Getting Metadata", "Extracting Variable Information", "Extracting Variables", "Selecting Cases", "Generating R code", "Queuing Job");

		if ($dtdwnldf) {
			push(@stages,"Converting Data Formats");
		} else  {
			push(@stages,"Analyzing Data");
			push(@stages,"Preparing Report");
		}
			if (! $progress -> init( "DSB",\@stages,$q) ) {
			$logger->vdcLOG_warning( 'VDC::DSB', $script_name,  "failed to initialize progress");
			$progress="";
		}
		if ($progress && (!$progress -> start())) {
			$logger->vdcLOG_warning( 'VDC::DSB', $script_name,  "failed to start progress");
			$progress="";
		}
	    }
###prog_update_and_check($progress); # 0. Starting Up


# ///////////////////////////////////////////////
# Create temp filenames
# ///////////////////////////////////////////////

&crtTmpFls; 

my $MD; 

if ( $USE_SQL_DIRECT )
{
	$logger->vdcLOG_warning('VDC::DSB', $script_name, "using alternative, direct-to-SQL mechanism for retreiving metadata"); # debug

	my $varIDhsh=$CGIparamSet->getVarIDhsh();
	my $fileid = 0; 

	if ( $dataURL =~/fileId=([0-9]*)/ )
	{
	    $fileid = $1; 
	}

	$logger->vdcLOG_warning('VDC::DSB', $script_name, "fileid: " . $fileid); # debug
	$logger->vdcLOG_warning('VDC::DSB', $script_name, "vars: " . join (",", keys (%$varIDhsh) ) ); # debug


	my $metaDataDirect = VDC::DSB::varMetaDataDirect->new
	    (FileID  => $fileid, 
	     VarID   => $varIDhsh,
	     sqlHost => $SQLHOST,
	     sqlPort => $SQLPORT,
	     sqlDB   => $SQLDB,
	     sqlUser => $SQLUSER,
	     sqlPw   => $SQLPW );

	$MD = $metaDataDirect->obtainMeta (); 

	unless ( $MD )
	{
	    $logger->vdcLOG_info ("VDC::DSB", "Disseminate",
				  "SQL connect failed; switching to DDI." );

	    $USE_SQL_DIRECT = 0; 

	}
	else
	{
	    print TMPX "metadata0: (obtained from SQL tables)\n", Dumper($MD);
	    $CGIparamSet->addMetaData($MD);
	    $CGIparamSet->setMVtypeCode();
	    print TMPX "metadata1:\n", Dumper($CGIparamSet);
	}
}

unless ( $USE_SQL_DIRECT )
{

# ///////////////////////////////////////////////
# get var-data from the ddi
# ///////////////////////////////////////////////
###prog_update_and_check($progress); # 1. Getting Metadata

	$logger->vdcLOG_warning('VDC::DSB', $script_name, "getting ddi"); # debug
	if ($ddiURL) {
                $ddiURL=~s:\?name=:/:;
		my $request1 = HTTP::Request->new('GET', $ddiURL );
		my($tcookie1) = $q->http('Cookie');
		if ($tcookie1) {
			$request1->header('cookie' => $tcookie1);
		}

		#$ua->proxy('http', $PROXY_URL);

		# Make the call
		$request1->header('X-VDC-Component' => "DSB");
		$request1->header('Pragma' => "no-cache");
		$request1->header('Cache-control' => "no-store");

		my $response1 = $ua->request($request1,$ddiforR);

		if ( !$response1->is_success ){
			my $err= $response1->status_line;	
			print $q->header ( -status=>"$err : could not access ddi file", -type=>'text/html');
			print $q->start_html ( -title=> "error -- could not access ddi file ($err)");
			print $q->h2("The cgi script failed to access the requested ddi file:");
			print $q->h2("$ddiURL");
			print $q->h2("($err)");
			print $q->end_html;
			$logger->vdcLOG_warning( 'VDC::DSB',  $script_name,  "Server Error: could not access ddi file");
				 {my($ts)=$@; &exitChores; die $ts;}
		}
	}
	
# below is a temporary solution (aka hack) for the problem with 
# getting invalid DDIs from the application;
# The DDI is created by the Dataverse and, apparently, always
# tagged as "UTF-8" content. So it was discovered that this will 
# cause problems for the parser in Akio's script below when there are 
# 8-bit characters in the DDI (most often, the result of ingest from 
# Stata and SPSS files). 
# As an immediate fix, I'm going to preprocess the DDI and 
# strip all such potentially-dangerous characters. This cannot 
# possibly cause any real content loss, since at this stage we are 
# only interested in the byte offsets of the variable columns in the 
# datafiles, so any text in the DDI can be safely disregarded. 
# 
# However, the problem of exporting illegal XML by the application 
# still remains, and will be addressed in the next build series.


open ( DDI, $ddiforR ); 
open ( STRIPPED_DDI, ">" . $ddiforR . "_stripped" ); 

while ( <DDI> )
{
    #chop; 
    s/\r//g;
    s/\n*$//g;
    s/[\000-\037\200-\377]/\?/g; 
    print STRIPPED_DDI $_ . "\n";
}

close DDI; 
close STRIPPED_DDI; 


$logger->vdcLOG_warning( 'VDC::DSB', $script_name,  "got ddi"); # debug


###	prog_update_and_check($progress); # 2. Extracting Variable Information

	my $varIDhsh=$CGIparamSet->getVarIDhsh();
	# Instantiate new handler and parser objects.
	my $ddimtdt = VDC::DSB::DDISAXparser->new(DDIname =>$ddiforR . "_stripped", VarID=>$varIDhsh);
	my $parser = XML::SAX::ParserFactory->parser(Handler => $ddimtdt);
	# Parse the document name that was passed in off the command line.
	eval {
		$parser->parse_uri($ddiforR . "_stripped");
	};
	if ($@){
		print TMPX "entering error-follow-up routine\n";
		if ($@ =~ /^all var tags are processed/) {
			print TMPX "processing has been successfully finished\n";
		} else {
			print TMPX "some parsing error occured\n";
			 {my($ts)=$@; &exitChores; die $ts;}
		}
	}

	# add retrieved metadata 
	$MD =$ddimtdt->passMetadata();
	print TMPX "metadata0:\n", Dumper($MD);
	$CGIparamSet->addMetaData($MD);
	$CGIparamSet->setMVtypeCode();
	print TMPX "metadata1:\n", Dumper($CGIparamSet);
}




# ///////////////////////////////////////////////
# column-wise (var-wise) subsetting
# ///////////////////////////////////////////////




my $varNoStrng=join(',',@{$CGIparamSet->getVarIDorder()});
print TMPX "var ID sequence=",$varNoStrng,"\n";

###prog_update_and_check($progress); # 3 Extracting Variables
# temporarily store the data set as a temp file in $TMPDIR

if ($dataURL) {
	my $dataURLG = $dataURL; # the url for GET requests,
                                 # with parameters appended.
	my $request;

	my $cgi_ParamHash = {}; 

	if ( $varNoStrng ){
###	    $cgi_ParamHash->{vars} = $varNoStrng;
	}

	$logger->vdcLOG_debug( 'VDC::DSB', $script_name,  "getting data "); # debug

	# let's cook the final URL and calculate the total
	# length; if it's too long, we are going to use POST
	# instead of GET.

	my $cgiParams = ""; 

	if ( $MD->{censusURL} )
	{
	    $logger->vdcLOG_info ("VDC::DSB", "Disseminate",
				  "census url detected;" );
	    $logger->vdcLOG_info ("VDC::DSB", "Disseminate",
				   $MD->{censusURL} );

	    $dataURLG = $MD->{censusURL}; 
	    $cgi_ParamHash->{vars} = join ( ",", @{$MD->{_varNameA}} ); 
	}
	else
	{
	    $cgi_ParamHash->{'noVarHeader'} = 1; 

	    # Detect and recycle some parameters sent to us by the 
	    # application (these are added to URLs for accounting/logging
	    # purposes)

	    my $isSSRparam = $q->param('isSSR');
	    
	    if ( $isSSRparam )
	    {
		$cgi_ParamHash->{$isSSR} = $isSSRparam; 
	    }
	    
	    my $isMITparam = $q->param('isMIT');

	    if ( $isMITparam )
	    {
		$cgi_ParamHash->{$isMIT} = $isMITparam; 
	    }
	}

	for my $v (keys %$cgi_ParamHash)
	{
	    $cgiParams .= ($v . "=" . $cgi_ParamHash->{$v} . "&"); 
	}

	if ( $cgiParams ) 
	{
	    chop $cgiParams; 
	    ###$cgiParams = "?" . $cgiParams; 
	}
	
	if (length($dataURLG) + length($cgiParams) < $MAX_URL) { 
	    if ( $dataURLG =~/\?/ )
	    {
		$dataURLG .= ( '&' . $cgiParams ); 
	    }
	    else
	    {
		$dataURLG .= ( '?' . $cgiParams ); 
	    }

	    $request = HTTP::Request->new('GET', $dataURLG );
	    $logger->vdcLOG_info( 'VDC::DSB', 'Disseminate',  $dataURLG . "(GET)" );
	} else {
	    $request = POST $dataURLG, $cgi_ParamHash; 
	    $logger->vdcLOG_info( 'VDC::DSB', 'Disseminate',  $dataURLG . "(POST)" );
	}

	# Detect and recycle some useful headers: 

	my($tcookie) = $q->http('Cookie');
	if ($tcookie) {
	    $request->header('cookie' => $tcookie);
	}

	unless ( $MD->{censusURL} ) 
	{
	    # the "x-forwarded-for" is the header set up by the 
	    # mit proxy; we are sending it back for logging purposes.

	    my $xforward = $q->http('X-Forwarded-For');
	    
	    if ( $xforward )
	    {
		$request->header('X-Forwarded-For' => $xforward);
	    }
	}


	#$ua->proxy('http', $PROXY_URL);
	#$ua->proxy('timeout', 3600);

	# Make the call
	$request->header('X-VDC-Component' => "DSB");
	my $RtmpDataFile=$RtmpDataRaw;
	my $response;
	if ( $dataURLG !~ /census/i ) {
#		# our repository returns what we ask for
#		$response = $ua->request($request,$RtmpDataFile);
#		$logger->vdcLOG_warning( 'VDC::DSB', $script_name,  "got data "); # debug
#	} elsif ( $dataURLG=~/test_files/ ) {	    
	    $logger->vdcLOG_info( 'VDC::DSB', $script_name, "entering Dataverse subsetting"); 

	    my $subs_obj = vdcRAP::Subsetting::new_SubObject;

	    if ( $subs_obj )
	    {
		$logger->vdcLOG_info ("VDC::DSB", "Disseminate",
				      "subsetting object created"); 
	    }
	    else
	    {
		$logger->vdcLOG_info ("VDC::DSB", "Disseminate",
				      "failed to create subsetting object");
		
	    } 
	    
	    my $datafile_format = ""; 
	    my $rcut_filter = ""; 

	    unless ( $USE_SQL_DIRECT )
	    {
		my $datafile_format = &check_fileFormat ( $ddiforR . "_stripped", $fileid ); 

		$logger->vdcLOG_info ("VDC::DSB", "Disseminate",
				      "format detected (from ddi): " . 
				      $datafile_format );

		my $coldef_file = &locate_subsettingMetaFile ( $ddiforR . "_stripped", $fileid, $datafile_format ); 

		if ( $coldef_file )
		{
		    $logger->vdcLOG_info ("VDC::DSB", "Disseminate",
					      "found coldef file: "
					      . $coldef_file );
		}
		else
		{
		    $logger->vdcLOG_fatal ("VDC::DSB", "Disseminate",
					   "failed to locate coldef file"); 
		}
	    
	    
		# below is a (bit of a hackish) fix for an exotic
		# problem we've discovered: rcut doesn't handle the 
		# case of a single variable column in a datafile 
		# -- it must be expecting at least one variable 
		# separator character. The good news is that, 
		# obviously, we don't need to call rcut at all when 
		# we need a single column from a file that only contains
		# one column. 

		open ( VLM, $coldef_file ); 
		my $v_counter = 0;
		while ( <VLM> )
		{
		    $v_counter++; 
		    last if $v_counter > 2; 
		}
		close VLM; 

		if ( ( $v_counter == 2 ) && ( $datafile_format eq 'tab' ) )
		{
		    $logger->vdcLOG_info ("VDC::DSB", "Disseminate",
					  "single-column datafile; no filter needed" );

		    $rcut_filter = "/bin/cat"; 
		}
		else
		{
		    my $output_filter = $subs_obj->produce_subset_filter ( $coldef_file, $datafile_format, '', $varNoStrng );

		
		    $logger->vdcLOG_info( 'VDC::DSB', $script_name,  "starting rcut pipe for new, Dataverse-style subsetting"); # 
		    my $delim="\t";
		    $rcut_filter = $output_filter . " -d'" . $delim . "'";  

		    $logger->vdcLOG_info ("VDC::DSB", "Disseminate",
					  "filter (ddi mtd.): "
					  . $rcut_filter );
		}
	    }
	    else
	    {
		$logger->vdcLOG_info ("VDC::DSB", "Disseminate",
				      "(subsetting meta via db-direct method)" );
		$rcut_filter = &make_SubsetFilter ( $MD, $varNoStrng );

		$logger->vdcLOG_info ("VDC::DSB", "Disseminate",
				      "filter (NEW mtd.): "
				      . $rcut_filter );
	    }

	 
	    my $do_stream;
	    my $buf="";

	    $response = $ua->request($request,$RtmpDataFile . ".raw");


	    my $code = $response->code; 
	    my $data_status = $response->status_line;


	    if ( $code == 302 )
	    {
		# A redirect. 
		my $location = $response->header ( 'Location' );

		my $jsession; 
		my $viewstate; 
		my $studyid; 

		$request = HTTP::Request->new('GET', $location );
		$response = $ua->request($request,$RtmpDataFile . ".raw");

		$code = $response->code; 


		open (F, $RtmpDataFile . ".raw"); 

		while ( <F> )
		{
		    if ( /<form [^>]*jsessionid=([0-9a-f]*)\"/ )
		    {
			$jsessionid=$1; 
		    }

		    if ( /<input [^>]*ViewState\" value=\"([^\"]*)\"/ )
		    {
			$viewstate=$1; 
		    }

		    if ( /studyId=([0-9]*)[^0-9]/ )
		    {
			$studyid=$1; 
		    }

		}

		close F; 

		if ( $jsessionid )
		{
		    my $content = 'content:termsOfUsePageView:form1:vdcId='; 


		    $content .= '&pageName=TermsOfUsePage';
		    $content .= '&content:termsOfUsePageView:form1:studyId=' . $studyid;
		    $content .= '&content:termsOfUsePageView:form1:redirectPage=/FileDownload/?fileId=' . $fileid; 
		    $content .= '&content:termsOfUsePageView:form1:tou=download';
		    $content .= '&content:termsOfUsePageView:form1:termsAccepted=on';
		    $content .= '&content:termsOfUsePageView:form1:termsButton=Continue';
		    $content .= '&content:termsOfUsePageView:form1_hidden=content:termsOfUsePageView:form1_hidden';
		    $content .= '&javax.faces.ViewState=' . $viewstate; 

		    my $url_tou = $location;
		    $url_tou=~s/\?.*$//g; 

		    my $url_tou_ext = $url_tou . ';jsessionid=' . $jsessionid; 

		    $request = HTTP::Request->new(POST => $url_tou_ext);
		    $request->header('Cookie' => 'JSESSIONID=' . $jsessionid); 
		    $request->content_type('application/x-www-form-urlencoded');
		    $request->content( $content );

		    $response = $ua->request($request,$file_final);
    
		    $data_status = $response->status_line;

		    if ($response->code==302) 
		    { 
			### my $location = $response->header ( 'Location' ); 

			$request = HTTP::Request->new('GET', $url );
			$request->header('Cookie' => 'JSESSIONID=' . $jsessionid); 

			$response = $ua->request($request,$RtmpDataFile . ".raw");
		    }
		}
	    }


	    if ( -f $RtmpDataFile . ".raw" )
	    {
		system ( "$rcut_filter < $RtmpDataFile" . ".raw > $RtmpDataFile " ); 
	    }

#	    $response = $ua->request($request,
#				     sub {
#					 my($chunk, $res) = @_;
#					 
#					 unless ($do_stream) {
#					     open(RCUT,"|" . $rcut_filter . " >  $RtmpDataFile") 
#						 ||  $logger->vdcLOG_warning ( 'VDC::DSB',  $script_name, "error opening rcut");
#					     $do_stream = 1; 
#					 }
#
#					 print RCUT  $chunk;
#				     }
#				     );
#	    if ($buf) {print "$buf";} else { close(RCUT); }
	} else {
	    # Census.gov url:

	        $logger->vdcLOG_info( 'VDC::DSB', $script_name,  "starting rcut pipe for incoming census stream"); # debug
	        my $RCUT_BIN = "/usr/local/VDC/bin/rcut";
		my $buf="";
		my $delim="\t";

		#my $fieldnames= join(',',@{$CGIparamSet->getVarNameSet()});
		my $fieldnames= join( ',', @{$MD->{_varNameA}} );

		my $do_stream;

		$response = $ua->request($request,
		   sub {
    			my($chunk, $res) = @_;

    			# In first phase, buffer content looking for end of line
    			# When first line read, parse it and start up rcut
    			# The main phase - stream rest of data

    			if ($do_stream) {
        			print RCUT $chunk;
    			} else {
        			$buf.=$chunk;
        			if (my($header,$remainder)=$buf=~/(.*\n)((.|\n)*)/) {
                			$buf="";
                			$do_stream=1;

                			my ($offsets)=create_rcut_field_offsets
						($header,$fieldnames,$delim);

					$header=~s/[ \r\t\n]//g; 
					$logger->vdcLOG_info ( 'VDC::DSB',  'Disseminate', ":" . $header . ":" . $fieldnames . ":" . $delim );

					if ( $header eq $fieldnames )
					{
					    open(RCUT,"|cat > $RtmpDataFile") 
						||  $logger->vdcLOG_warning ( 'VDC::DSB', "Disseminate", "error opening cat");

					    $logger->vdcLOG_info ( 'VDC::DSB', "Disseminate", "using cat" );
					}
					else
					{
					    open(RCUT,"|$RCUT_BIN -f" 
						. $offsets . " -d'" . $delim .
						 "'" . " >  $RtmpDataFile") 
						||  $logger->vdcLOG_warning ( 'VDC::DSB',  "Disseminate", "error opening rcut");
					    $logger->vdcLOG_info ( 'VDC::DSB',  "Disseminate", "$RCUT_BIN -f" . $offsets . " -d'" . $delim . "'" . " >  $RtmpDataFile" );
					}
                			print RCUT  $remainder;
				}
        		}
		   }
 		);
		if ($buf) {print "$buf";} else { close(RCUT); }
	    }
	
	if ( !$response->is_success )
	{
	    my $err= $response->status_line;
	    print $q->header;
	    print $q->start_html ( -title=> "error -- could not access datafile ($err)");
	    print $q->h2("The DSB subsystem failed to access the requested data file:");

	    if ( $MD->{censusURL} )
	    {
		print $q->h2( $MD->{censusURL} );
	    }
	    else
	    {
		print $q->h2("$dataURL");
	    }

	    print $q->h2("the remote server response:");
	    print $q->h2("($err)");
	    print $q->p; 
	    print $q->h2("It is possible that the resource is temporarily unavailable; please try again later. If this behavior persists, please contact the administrators of the Dataverse network"); 

	    print $q->end_html;
	    $logger->vdcLOG_warning('VDC::DSB',$script_name,"Server Error: could not access datafile");
	    {my($ts)=$@; &exitChores; die $ts;}
	} 
	elsif (-z $RtmpDataRaw) 
	{
	    print $q->header;
	    print $q->start_html ( -title=> 'error -- data file is empty after the column-wise subsetting request ');
	    print $q->h2 ( "ERROR: The DSB subsystem failed to perform column-wise subsetting on the requested data file:" );

	    if ( $MD->{censusURL} )
	    {
		print $q->h2( $MD->{censusURL} );
	    }
	    else
	    {
		print $q->h2("$dataURL");
	    }
	    
	    print $q->p; 
	    print $q->h2("It is possible that the resource is temporarily unavailable; please try again later. If this behavior persists, please contact the administrators of the Dataverse network"); 

	    print $q->end_html;
	    $logger->vdcLOG_warning('VDC::DSB',$script_name,"Server Error (XXX): empty  data file");
	    {my($ts)=$@; &exitChores; die $ts;}
	}

        if ( $dtdwnldf eq "D00" )
        {
	    # this format tag indicates that the user requested to convert 
	    # a fixed-field data file to tab-delimited on the fly;
	    # no need to generate citations and such, just dumping out the file.

	    system ( "sed 's/ " . '*' . "//g' < $RtmpDataFile > $RtmpDataFile.cleaned" ); 

	    my $buf; 
	    if ( open (TAB, $RtmpDataFile . ".cleaned" ) )
	    {
		my $filename = 'data_' . $fileid . '.tab';
		my $size = (stat(TAB))[7]; 

		print $q->header (
		      -type=>"text/tab-separated-values; name=\"$filename\"",
		      -Content_disposition=> "attachment; filename=\"$filename\"",
		      -Content_length=>$size
		     );

		while ( read ( TAB, $buf, 8192 ) )
		{
		    print $buf; 
		}

		close TAB; 
		unlink $RtmpDataFile . ".cleaned";
		&exitChores;
		exit 0; 
	    }
	}
}



# ///////////////////////////////////////////////
# row-wise (case[observation]-wise) subsetting
# ///////////////////////////////////////////////

	$logger->vdcLOG_debug( 'VDC::DSB', $script_name,  "starting rowise "); # debug
###	prog_update_and_check($progress); # 4 Selecting Cases

	my $caseSubsetOn = $CGIparamSet->checkCaseSubset();
	if ($caseSubsetOn){
		print TMPX "case-wise subset is necessary:",$caseSubsetOn,"\n";
		my $rwsbstparam= $CGIparamSet->getRwSbst();
		my $rs = VDC::DSB::CaseWiseSubset->new($rwsbstparam, RAWFILE=>$RtmpDataRaw, OUTFILE=>$RtmpData);
		print TMPX "rs:\n", Dumper($rs);
		unless ($rs->checkSubsetParams()) {
			my $vdc_MSV=0;  # turn-off the missing value elimination
			my $vdc_DCML=1; # turn-off the decimalization
			my $caseQnty = $rs->SubsetCaseWise(\$vdc_MSV, \$vdc_DCML);
			print TMPX "no of cases =",$caseQnty,"\n";

			unless ($caseQnty){
				print TMPX "The subset data file is empty\n";
				print $q->header ( -status=>'500 Server Error: empty data file', -type=>'text/html');
				print $q->start_html ( -title=> 'error -- data file is empty after the row-wise subsetting request ');
				print $q->end_html;
				$logger->vdcLOG_warning('VDC::DSB',$script_name,"Server Error: empty  data file");
				{my($ts)=$@; &exitChores; die $ts;}

			} else {
				$CGIparamSet->updateCaseQnty($caseQnty);
				print TMPX "caseQnty aftrer update=",$CGIparamSet->getCaseQnty(),"\n";
				
				if ($CGIparamSet->checkDwnldTab()){
					print TMPX "no more fast track [go to download section]\n";
					copy ($RtmpData, $DwnldPrfx);
					#goto DOWNLOAD;
				}

				
			}

		} else {
			print TMPX "row-wise subsetting parameters have problems\n";
			print $q->header ( -status=>'500 Server Error: invalid case-wise subsetting parameters', -type=>'text/html');
			print $q->start_html ( -title=> 'error -- invalid case-wise subsetting parameters ');
			print $q->end_html;
			$logger->vdcLOG_warning('VDC::DSB',$script_name,"Server Error: invalid case-wise subsetting parameters ");
			{my($ts)=$@; &exitChores; die $ts;}
		}
	} else {
		move($RtmpDataRaw,$RtmpData);

		if ($CGIparamSet->checkDwnldTab()){
			print TMPX "no more fast track [go to download section]\n";
			copy ($RtmpData, $DwnldPrfx);
			#goto DOWNLOAD;
		}
	}




# ///////////////////////////////////////////////
# open $Rcode file for storing the R code
# ///////////////////////////////////////////////

	# /// new code writer
###	prog_update_and_check($progress); # 5 Generating R code
	$logger->vdcLOG_debug( 'VDC::DSB', $script_name,  "generating rcode "); # debug
	
	my $ZELIGOUTPUTDIR="${TMPDIR}/${TMPDSBDIR}/zlg_$$";
	my $zlgOutIdFile  ="${TMPDIR}/${TMPDSBDIR}/zlout_" . $$ ;

	open (R, "> $Rcode");
	if ($CGIparamSet->zeligModelsRequested() || $CGIparamSet->checkNonZeligArqst()){
		mkdir $ZELIGOUTPUTDIR;
		if ($CGIparamSet->checkNonZeligArqst()){
			# rewrite 	$RvlsPrfx   = "$TMPDIR/Rvls.$$";
			$RvlsPrfx   = "${ZELIGOUTPUTDIR}/visuals/Rvls.$$";
			mkdir "${ZELIGOUTPUTDIR}/visuals";
		}
		$CGIparamSet->printRcodeData(WH=>*R,HTMLFILE=>$RtmpHtml,TABFILE=>$RtmpData,SRVRCGI=>$SRVRCGI,DWNLDPRFX=>$DwnldPrfx,IMGFLPRFX=>$RvlsPrfx, WITHZLG=>'F');
		$CGIparamSet->printZeligCode(ZELIGOUTPUTDIR=>$ZELIGOUTPUTDIR,DATAFRAME=>'x', ZLGOUTIDFILE=>$zlgOutIdFile);
	} else {
		$CGIparamSet->printRcodeData(WH=>*R,HTMLFILE=>$RtmpHtml,TABFILE=>$RtmpData,SRVRCGI=>$SRVRCGI,DWNLDPRFX=>$DwnldPrfx,IMGFLPRFX=>$RvlsPrfx);
	}
	close(R);
	
	# end of R code generation

	$logger->vdcLOG_debug( 'VDC::DSB', $script_name,  "done rcode "); # debug

	print TMPX "metadata:last:\n", Dumper($CGIparamSet);

###	prog_update_and_check($progress); # 6 Queuing Job
	while (get_pmem_active() > $MEMTHRESH && get_load_avg() > $LOADTHRESH ) {
		sleep($QWAIT + int(rand(10)));
		$logger->vdcLOG_info( 'VDC::DSB',  $script_name,  "system busy, queueing");
	}
	
# ///////////////////////////////////////////////
# run the above R code 
# ///////////////////////////////////////////////

	$logger->vdcLOG_info( 'VDC::DSB',  $script_name,  "performing analysis");
###	prog_update_and_check($progress); # A7 Analyzing Data
	my $status = &runR();

# ///////////////////////////////////////////////
# post-R processing 
# ///////////////////////////////////////////////
###	prog_update_and_check($progress); # A8 Preparing Report
	if ($status) {
		print $q->header ( -status=>'500 Server Error: R failed', -type=>'text/html');
		print $q->start_html ( -title=>'R failed' );
		print $q->h2 ( 'R failed miserably' );
		print $q->end_html;
		$logger->vdcLOG_warning( 'VDC::DSB',  $script_name,  "Server Error: R failed");
		{my($ts)=$@; &exitChores; die $ts;}
	}

	# check if R generated any output: 

	if(!$dtdwnldf){
		if ($CGIparamSet->zeligModelsRequested() || $CGIparamSet->checkNonZeligArqst()){
			# zelig included 
			my $zRoutHtml="";
			# check whetehr non-zelig requests are submitted
			my $nonZeligJob=-1;
			if ($CGIparamSet->checkNonZeligArqst()){
				print TMPX "enter zelig-plus case\n";
				# zelig + etc
				# move html file to zelig output directory
				$nonZeligJob=0;
				my @tmpRh = split(?/?, $RtmpHtml);
				$zRoutHtml= $tmpRh[$#tmpRh];
				my $zRtmpHtml = "${TMPDIR}/${TMPDSBDIR}/zlg_$$/" . $zRoutHtml;
				if ( (-e $RtmpHtml) && (-s $RtmpHtml)) {
					# R returned some results
					$nonZeligJob=1;
					open(M, "> $zRtmpHtml");
					print M  &start_analysis_page ($stdyno, $stdyttl, $datasetname);
						open(N, $RtmpHtml);
						while(<N>){
							print M $_;
						}
						close(N);

					print M $q->end_html;
					close(M);
				}
				
			}
			print TMPX "enter zelig-rendering process\n";
			my $zlgOutFileSet = [];
			#my $zlgOutIdFile="${TMPDIR}/${TMPDSBDIR}/zlout_" . $$ ;
			my $noZoutFiles; 
			if (-e $zlgOutIdFile) {
				$noZoutFiles = &getZoutFileNames($zlgOutIdFile, $zlgOutFileSet);
				print TMPX "Zelig output file ID list:\n",Dumper($zlgOutFileSet), "\n";
				print TMPX "no of zelig ouput files:\n",$noZoutFiles, "\n";
			} else {
				print TMPX "R failed without zelig results or non-zelig case\n";
				$noZoutFiles=0;
			}
							
				# create a manifest html page
				my $zlgMnfstFile="${ZELIGOUTPUTDIR}/index.html";

				my $RunOrder = $CGIparamSet->zeligModelsRunOrder();
				print TMPX "model sequence:\n", Dumper($RunOrder);
				# get citation info
				my $citationMessage=$CGIparamSet->createDataCitationFile(SBSTDATAFILE=>$RtmpData, OUTPUTTYPE=>'html');
				print TMPX "self:\n". Dumper($CGIparamSet). "\n";
				my $dirStringId = &getRandomString(4);
				print TMPX "browserType=", $browserType, "\n";
				open(M, "> $zlgMnfstFile ");
				print M &printZeligManifestPage(\*M, $stdyno, $stdyttl,$datasetname,$zlgOutFileSet, $RunOrder, $zRoutHtml, $RtxtOutput, $citationMessage, $nonZeligJob, $dirStringId, $q->param('analysis'));
				close(M);
				
				# output dir: $ZELIGOUTPUTDIR="${TMPDIR}/${TMPDSBDIR}/zlg_$$";
				my $DSBDIR   = "${TMPDIR}/${TMPDSBDIR}";
				my $dirString = 'zlg_'. $dirStringId;

				print TMPX 'dir string=' . $dirString . "\n";
				print TMPX 'current server=' . $SERVER . "\n";
				my $ZHTMLDIR = $WEBTEMPDIR . '/' . $dirString;

				`cp $Rcode  $ZELIGOUTPUTDIR`;
				`cp $RtmpData  $ZELIGOUTPUTDIR`;

				
				`cd $DSBDIR; mv $ZELIGOUTPUTDIR $ZHTMLDIR`;
				
				my $VDCFXCSS = 'R2HTML.css'; 
				`cd $VDCRLIB; cp $VDCFXCSS $ZHTMLDIR`;
				
my $jumpScript=<<ENDJ;
<!--
function jump(url){
	window.location=url;
}
//-->
ENDJ

			my $port_requested = $q->server_port();
			my $location_url = ""; 

			if ( $port_requested != 80 )
			{
			    $location_url = "http://${SERVER}:${port_requested}/VDC/temp/${dirString}";
			}
			else
			{
			    $location_url = "http://${SERVER}/VDC/temp/${dirString}";
			}
					print $q->header (-type=>'text/html');
					print $q->start_html (
					-title=>'Modeling Results are Ready to Check',
					-script=>$jumpScript,
					-onload=>"jump(\"${location_url}\")",
					);
					print $q->end_html;
					$logger->vdcLOG_info( 'VDC::DSB',  $script_name,  "Zelig modeling request completed");
					if (-e $zlgOutIdFile) {
						unlink($zlgOutIdFile);
					}
		} else {
			# non-zelig analysis
			my $RsltHtmlFD = &openResults; 
			unless ( $RsltHtmlFD ) {
				print $q->header ( -status=>'500 Server Error: Statistical Analysis Failed (MisSpecification)', -type=>'text/html');
				print $q->start_html ( -title=>'Statistical Analysis Failed');

				print "<pre><b><font color='red'>Error: the statistical analysis failed, usually due to a mispecification</font></b></pre><hr>\n";

				print $q->end_html;
				$logger->vdcLOG_warning( 'VDC::DSB', $script_name, "Server Error: Statistical Analysis Failed");
				 {my($ts)=$@; &exitChores; die $ts;}
			}

			# dump results from R to the html page
			if ($CGIparamSet->getRqustType('A')){
				#my ($dozip) = $q->http('Accept-encoding')=~/gzip/;
				my ($dozip) =0; # turn this off since mod-gzip will take over
				if ($dozip)  {
					if (!open(GZ,"| gzip -c")) {
						*GZ = *STDOUT;
						print $q->header ( -type=>'text/html');
						$logger->vdcLOG_warning( 'VDC::DSB', 
						 $script_name,  "couldn't gzip output stream: $!");
				   } else {
						print $q->header ( -type=>'text/html', -Content_encoding => "gzip");
				   }
				} else {
					*GZ = *STDOUT;
					print $q->header ( -type=>'text/html');
				}

				print GZ &start_analysis_page ( $stdyno, $stdyttl, $datasetname); 

###				# prog_update_and_check($progress);
				&printResults ( $RsltHtmlFD,\*GZ );

				my ($tmp) = $Rcode;
				$tmp=~s/(.*)\/(.*)/$2/;
				print GZ $q->p($q->center($q->strong($q->a({-href=>"/cgi-bin/nph-dmpData.pl?dataflnm=${tmp}&flxtn=R&stdyno=$stdyno"},  "[Download Replication Code]"))));
				print GZ $q->p($q->center($q->small("[Note: These results are stored only temporarily. Please do not bookmark or refresh this page. ]")));
				print GZ $q->end_html;
				close(GZ);
				$logger->vdcLOG_info( 'VDC::DSB',  $script_name,  "Analysis request completed");
			}
		}
	}
# ///////////////////////////////////////////////
# dataset-download specific
# ///////////////////////////////////////////////
	
	DOWNLOAD: if ($dtdwnldf) {
		my $varnameline;
		my $citationFile="citation_$$.txt";
		my $citationMessage=$CGIparamSet->createDataCitationFile(SBSTDATAFILE=>$RtmpData, OUTPUTTYPE=>'txt');
		#`cd $TMPDIR; touch $citationFile; echo -en "$citationMessage" >> $citationFile`;
		open TMPFILE, ">" . $TMPDIR . "/" . $citationFile;
		print TMPFILE $citationMessage; 
		close TMPFILE; 
		if ($dtdwnldf eq 'D01'){
			# tab-delimited case
			$varnameline = '"'. join("\"\t\"", @{$CGIparamSet->getVarNameSet()}) . "\"\n";
			print TMPX "varnameline=",$varnameline,"\n";
			# generate the node set string for extracting metadata
			my $paramNodeSet=$CGIparamSet->getRsubsetparam();
			print TMPX "paramNodeSet(New)=",$paramNodeSet,"\n";
			my $tmpfl = "$TMPDIR/dwnld_tabwc_$$";	
			#`touch $tmpfl; echo -en  "$varnameline" >> $tmpfl; cat $DwnldPrfx >> $tmpfl;mv $tmpfl $TMPDIR/data_$$.dat;rm $DwnldPrfx`;
			`touch $tmpfl; cat $DwnldPrfx >> $tmpfl;mv $tmpfl $TMPDIR/data_$$.dat;rm $DwnldPrfx`;
			
			my $ct = [ qw ( sps sas dta ) ];
			my $ccMD= $CGIparamSet->getMetaData4CC();
			print  TMPX "metadataset4cc(ccMD):\n" . Dumper($ccMD) . "\n"; 
			my $ccw = VDC::DSB::StatCodeWriter->new($ccMD);
			print  TMPX "metadataset4cc(ccw):\n" . Dumper($ccw) . "\n"; 
			$ccw->printCC(TYPE=>$ct, CCFLPFX=>"$TMPDIR/CC$$", DATA=>"data_$$.dat");
	
			# By popular demand, we are changing the default
			# multi-file package format from gzipped tar to 
			# zip:

			#`cd $TMPDIR; tar -czvf data_$$.tar.gz data_$$.dat citation_$$.txt CC$$.sps CC$$.do CC$$.sas; rm -f data_$$.dat citation_$$.txt CC$$.sps CC$$.do CC$$.sas`;
			`cd $TMPDIR; zip data_$$.zip data_$$.dat citation_$$.txt CC$$.sps CC$$.do CC$$.sas; rm -f data_$$.dat citation_$$.txt CC$$.sps CC$$.do CC$$.sas`;
			
			$DwnldPrfx = "$TMPDIR/data_$$.zip";
			#$dtdwnldf="GZ";
			$dtdwnldf="ZIP";
		    } elsif ($CGIparamSet->getDwnldType() eq 'D03') {
			# stata case
			my $binDwnldFile= "data_$$.dta"; 
			
			# attach value-labels as a do file
			my $ccMD= $CGIparamSet->getMetaData4CC();
			print  TMPX "metadataset4cc(ccMD):\n" . Dumper($ccMD) . "\n"; 
			my $ccw = VDC::DSB::StatCodeWriter->new($ccMD);
			print  TMPX "metadataset4cc(ccw):\n" . Dumper($ccw) . "\n"; 
			$ccw->printDTAvalueLabelDoFile(CCFLPFX=>"$TMPDIR/CC$$");
			unless ( $packageType eq 'fileonly' )
			{
			
			    #`cd $TMPDIR; mv $DwnldPrfx $binDwnldFile;  tar -czvf data_$$.tar.gz $binDwnldFile CC$$.do citation_$$.txt ; rm -f $binDwnldFile CC$$.do citation_$$.txt`;
			    `cd $TMPDIR; mv $DwnldPrfx $binDwnldFile; zip data_$$.zip $binDwnldFile CC$$.do citation_$$.txt ; rm -f $binDwnldFile CC$$.do citation_$$.txt`;
			    $DwnldPrfx = "$TMPDIR/data_$$.zip";
			    $dtdwnldf="ZIP";
			}
			else
			{
			    `cd $TMPDIR; /bin/mv $DwnldPrfx $binDwnldFile`;
			    $DwnldPrfx = $TMPDIR . "/" . $binDwnldFile;
			}
		    } else {
			my $binDwnldFile;
			if ($CGIparamSet->getDwnldType() eq 'D02'){
				$binDwnldFile= "data_$$.ssc"; 
			} elsif ($CGIparamSet->getDwnldType() eq 'D04'){
				$binDwnldFile= "data_$$.RData"; 
			}
			
			unless ( $packageType eq 'fileonly' )
			{
			    #`cd $TMPDIR; mv $DwnldPrfx $binDwnldFile;  tar -czvf data_$$.tar.gz $binDwnldFile citation_$$.txt ; rm -f $binDwnldFile citation_$$.txt`;
			    `cd $TMPDIR; mv $DwnldPrfx $binDwnldFile; zip data_$$.zip $binDwnldFile citation_$$.txt ; rm -f $binDwnldFile citation_$$.txt`;
			
			    $DwnldPrfx = "$TMPDIR/data_$$.zip";
			    $dtdwnldf="ZIP";
			}
			else
			{
			    `cd $TMPDIR; /bin/mv $DwnldPrfx $binDwnldFile`;
			    $DwnldPrfx = $TMPDIR . "/" . $binDwnldFile;
			}
			
		}

		$stdyno = $fileid unless $stdyno; 

		print TMPX "DwnldPrfx=",$DwnldPrfx,"\n";
		print TMPX "dtdwnldf=",$dtdwnldf,"\n";
		print TMPX "stdyno=",$stdyno,"\n";


		if (!&dataDownload(\$DwnldPrfx, \$dtdwnldf, \$stdyno, \$varnameline, 0, \$packageType)) { 
			$logger->vdcLOG_info( 'VDC::DSB',  $script_name,  "Download request completed");
		}
	}
	
# ///////////////////////////////////////////////
# unlink tmp files
# ///////////////////////////////////////////////

&exitChores;
	$logger->vdcLOG_info( 'VDC::DSB',  $script_name,  "Cleanup completed, exiting.");
	exit;
	
# ///////////////////////////////////////////////
#  end of the main 
# ///////////////////////////////////////////////

# ///////////////////////////////////////////////
# Support routines;
# ///////////////////////////////////////////////

#############
### create_rcut_field_offsets
###

# generate rcut field position offset list based on list of
# varnames and header line in delimited file file
#
# header - first (header) line of delimited file
# delim - field delimiter
# fieldnames- list of names of variables to be extracted

sub create_rcut_field_offsets{
        my ($header,  $fieldnames, $delim)=@_;

        chomp ($header);
        my @header_fields = split($delim,$header);
        my @cut_fields= split(",",$fieldnames);
        my @offsets;

        for (my $i=0; $i<=$#cut_fields; $i++) {
                for (my $j=0; $j<=$#header_fields; $j++) {
                        if ($header_fields[$j] eq $cut_fields[$i]) {
                                push(@offsets,$j+1);
                        }
                }
        }

	return join ( ",", @offsets ); 
}

#############
### start_analysis_page
###

sub start_analysis_page {
	my $stdyno = $_[0]; 
	my $stdyttl = $_[1]; 
	my $datasetname = $_[2]; 
	my $ret = "";

my $xtabStyle=<<END;
<!-- 

H1 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 20pt;
	font-style: normal;
	font-weight: normal;
	color: #FFFFFF;
	background: #004080;
	text-align: center;
	margin: 10pt 2.5%
}

H2 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 18pt;
	font-style: normal;
	font-weight: normal;
	color: #FFFFFF;
	background: #000000
}


H3 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14pt;
	font-style: normal;
	font-weight: bold;
	color: #004080
}

H4 {
	font-family: T, Helvetica, sans-serif;
	font-size: 10pt;
	font-style: normal;
	font-weight: bold;
	color: #000000;
	line-height: 16pt
}

	.xtb_fnt {
		color:#000080;font-weight:bold;
	}
	.xtb_lgnd {
		padding:1em;font:caption;background-color:#ccccff;
	}
	.xtb_vllbl {
		font:caption;background-color:#e6e6fa;
	}
	.xtb_vllblc {
		font:caption;background-color:#e6e6fa;text-align:right;
	}
	.xtb_bdycll {
		text-align:right;font:caption;background-color:#F0F8f0;
	}
	.xtb_vn {
		font-family:Helvetica;background-color:#ccccff;text-align:center;vertical-align:middle;
	}
	.xtb_mrgn {
		text-align:right;font:caption;background-color:#ccccff;
	}
	.xtb_mrgnl {
		text-align:center;font-family:Helvetica;background-color:#ccccff;
	}
	
	
	.dvnUnvStatTbl{
		background-color:#ffffff;
	}
	.dvnUnvStatTblHdr{
		background-color:#e6e6fa;
	}
	.dvnUnvStatTblRowO{
		background-color:#f5f5f5;
	}
	.dvnUnvStatTblRowE{
		background-color:#ffffff;
	}

	
-->
END
	
	 $ret .= $q->start_html ( -title=> "Dataverse Analysis: Request # $$ ", -style=>{-code=>$xtabStyle,'src'=>"R2HTML.css"});
	#$ret .= $q->start_html ( -title=> "Dataverse Analysis: Request # $$ ", -style=>{-code=>$xtabStyle});
	#$ret .= $q->start_html ( -title=> 'Univariate Data Analysis: Results', -style=>{'src'=>'/VDC/css/varlstColor.css'});
	$ret .= $q->h1 ('Dataverse Analysis');
	$ret .= $q->h2 ('Results');
	#$ret .= $q->p ("Study ID Number: $stdyno");
	$ret .= $q->p ("Study Title: $stdyttl");
	#if ($datasetname) {
	#	$ret .= $q->p ("Data Set File Name:$datasetname");
	#}
	
	#my $goBackParam = '-2';
	#if ($browserType eq 'Firefox'){
	#	$goBackParam = '-1';
	#}
	#my $hrefString  = 'javascript:window.history.go(' . $goBackParam . ');';

	$ret .= $q->blockquote($q->strong($q->a( {href=>'javascript:window.history.back();'},'Go back to the previous page')));
	#$ret .= $q->blockquote($q->strong($q->a( {href=>$hrefString},'Go back to the previous page')));
	$ret .= $q->hr;

    return $ret; 
}

#############
## get_pmem_active
##
## check memory threshold, load avg

sub get_pmem_active {
	my ($key,$val,%mem,$r);
	open(MEM,"</proc/meminfo") || return;
	while(<MEM>) {
		($key,$val)=/(.*)\:\s+(\S+).*/;		
		$mem{$key}=$val;
	}
	close(MEM);

	return if ($mem{'MemTotal'}==0);
	$r=$mem{'Active'}/$mem{'MemTotal'};
	undef($r) if ($r==0);
	return ($r);
}

sub get_load_avg {
        my (@load,$r);
        open(LOAD,"</proc/loadavg") || return;
        $_=<LOAD>;
        @load= split(/\s/,$_);
        close(LOAD);

        $r=@load[0]+0;
        return ($r);
}


#############
## prog_update_and_check
##
## update progress, and abort if user cancelled

###sub prog_update_and_check {
###	my ($p) = @_;	
###	my $status;
###	if ($p) { 
###		$status=$p->update();
###	}
###	if ($status==-1) { # user cancelled
###		print $q->header ( 
###			-status=>'400 Malformed Request: Client Cancelled Request Before Completion', 
###			-type=>'text/html');
###		print $q->start_html ( -title=> 'Client Cancelled Request Before Completion');
###		print $q->h2('Client Cancelled Request Before Completion');
###		print $q->end_html;
###		$logger->vdcLOG_warning( 'VDC::DSB', 
###			 $script_name,  "Exiting: Client cancelled request");
###		&exitChores;
###		die;
###	}
###}

#############
## dataDownload
## return the requested data file to the browser

sub dataDownload  {
	my ($rfDwnldPrfx, $rfdtdwnldf, $rfstdyno, $rfvn, $rfzlg, $pkgType) = @_;
	my $flnm   = $$rfDwnldPrfx;
	my $xtsn   = $$rfdtdwnldf;
	my $stdyno = $$rfstdyno;
	my $varnameline = $$rfvn;
	my $zlg;    $zlg = $$rfzlg if $rfzlg;
	my $pkg    = $$pkgType; 
	select(STDOUT);

	my $buffer;
	my %typemap = (
		'D01'=> 	{mime=>'text/tab-separated-values',		ext=>'dat', bm=>0},
		'D02'=> 	{mime=>'application/x-rlang-transport',	ext=>'ssc', bm=>0},
		'D03'=> 	{mime=>'application/x-stata-6',			ext=>'dta', bm=>1},
		'D04'=> 	{mime=>'application/x-R-2',			ext=>'RData', bm=>1},
		'TAR'=> 	{mime=>'application/x-tar',				ext=>'tar', bm=>1},
		'GZ' => 	{mime=>'application/x-gzip-tar',		ext=>'tar.gz', bm=>1}, 
		'ZIP' => 	{mime=>'application/zip',		ext=>'zip', bm=>1}, 
		'unknown'=>	{mime=>'application/x-unknown',			ext=>'bin', bm=>1},
	);
	if (!$typemap{"$xtsn"}) { $xtsn="unknown";}
	my $filename;
	if ($zlg){
		$filename = 'zelig_output.' . $stdyno . '.' . $$ . '.' . $typemap{"$xtsn"}{'ext'};
	} elsif ( $pkg eq 'fileonly' ) {
	    $filename = 'data_' . $stdyno . "." . $typemap{"$xtsn"}{'ext'}; 
	} else {
		$filename = 'da' . $stdyno . "_subset.$$." . $typemap{"$xtsn"}{'ext'};
	}
	my $size = -s $flnm;
		
	my $cntnttyp = $typemap{"$xtsn"}{'mime'} ;  
	if ($typemap{"$xtsn"}{'bm'}) {binmode STDOUT;}

	if (open(dwnldFile, $flnm)) {
		print $q->header (
			-type=>"$cntnttyp ; name=\"$filename\"",
			-Content_disposition=> "attachment; filename=\"$filename\"",
			-Content_length=>$size
		);
		#if ($xtsn eq 'D01') {print $varnameline};
		while (read dwnldFile, $buffer, $size){
			print $buffer;
		}
		close(dwnldFile);
		unlink ($flnm);
		return(1);
	} else {
		if ($zlg){
			print $q->header ( -status=>"404 Not Found: Results TAR File Not Found", -type=>'text/html');
			print $q->start_html ( -title=> 'Zelig Output TAR File not Found');
		} else {
			print $q->header ( -status=>"404 Not Found: Data File Not Found", -type=>'text/html');
			print $q->start_html ( -title=> 'Data File not Found');
		}
		print $q->h2("Could not locate $flnm");
		print $q->end_html;
		return;
	}
}

##################
## crtTmpFls
## create various temp files and parameters

sub crtTmpFls {
	$Rcode      = "$TMPDIR/Rcode.$$.txt";
	$RtxtOutput = "$TMPDIR/Rout.$$.log";
	$RtmpData   = "$TMPDIR/Rdata.$$.data";
	$RvlsPrfx   = "$TMPDIR/Rvls.$$";
	$SRVRCGI    = "$SERVER/$CGIDIR";
	$RtmpHtml   = "$TMPDIR/Rout.$$.html";
	
	$DwnldPrfx  = "$TMPDIR/Data.$$";
	$DataPrfx   = "Data.$$";
	$ddiforR    = "$TMPDIR/DDI.$$.xml";
	$RtmpDataRaw= "$TMPDIR/Rdata.$$.raw";
}


#######
## runR
## run R in batch mode in the directory where .Rprofile is stored
## "--slave option" can be masked for debugging the cgi script

sub runR {
	my $status; 

	#system("cd $RPROFILEDIR; $RDIR -q --no-save  < $Rcode >& $RtxtOutput");
	$status = system("cd $RPROFILEDIR; /bin/nice -n $NICE $RDIR    --no-save  < $Rcode >& $RtxtOutput");
	#system("cd $RPROFILEDIR; $RDIR -q --no-save --slave < $Rcode >& $RtxtOutput");

	return undef; #$status; 

}

###############
## openResults
##

sub openResults {
	if (open(RsltHtml, $RtmpHtml)) {
		return RsltHtml; 
	}
	return undef; 
}

###############
## printResults
## print the text-form results and image files to standard output

sub printResults {
	my ($filedesc,$of) = @_; 
	while (<$filedesc>){
		chomp();
		#$_=~s/</&lt;/g;
		print $of ("$_ \n");
	}
	close($filedesc);
}

#############
## termHandler
## handle SIGTERM 

sub termHandler {
	print $q->header ( -status=>'503 Server Error: Time Limit Exceeded', -type=>'text/html');
	print $q->start_html ( -title=>'Time Limit Exeeded');
	print "<pre><b><font color='red'>The time limit for your request was exceeded, and your job has been terminated. Please download the data you would like, and run the analysis on your own system.</font></b></pre><hr>\n";
	$logger->vdcLOG_warning( 'VDC::DSB', "Disseminate", "Server Error: Time Limit Exceeded");
	print $q->end_html;
	exit(1);
}

#############
## exitChores 
## delete temp files

sub exitChores {
	unless ($DEBUG) {
	 	unlink($Rcode);
	 	unlink($RtxtOutput);
	 	unlink($RtmpData);
	 	unlink($RtmpDataRaw);
	 	unlink($RtmpDataRaw . ".raw");
	 	unlink($ddiforR);
	 	unlink($ddiforR . "_stripped");
	 	unlink($RtmpHtml);
		my $nofldl =  unlink(< $TMPDIR/t.$$.*.tab >);
		print TMPX "\n",$nofldl," sub-tab files are deleted\n";
		unlink("$TMPDIR/tmpRange.$$.log");
	}
}

#############
#
#

sub getZoutFileNames{
	my $outfile=shift;
	my $fl     =shift;
	my $DEBUG;
	print $outfile . "\n" if $DEBUG;
	open(Z, "$outfile");
	my @rawout;
	while(<Z>) {
		chomp;
		push @rawout, $_;
	}
	close(Z);
	my $counter;
	foreach my $mdl (@rawout){
		print "rawout=\n" . $mdl ."\n" if $DEBUG;
		my $rtmpdir = '/';
		my ($pos1, $pos2);
		my $fname=[];
		my @out=split(/\t/,$mdl);
		#my @out=split(/\t/,(`cat $outfile`));
		print "out=" . join('|', @out) . "\n" if $DEBUG;

		if ($out[0] ne 'NULL'){
			my @rpath = split(?/?, $out[0]);
			# the temp dir of some platform may not be named '/tmp'
			for (my $i=0; $i<@rpath; $i++) {
				if ($rpath[$i] =~ m/^index/){
					$rtmpdir .=  join('/', @rpath[1..$i]);
					$pos1=$i;
					print "index position=",$pos1,"\n"  if $DEBUG;
					print "html file name =" . $rpath[$pos1] . "\n" if $DEBUG;
					$fname->[0] = join('/', @rpath[($i-1) .. $i]);
					$counter++;
					last;
				}
			} 
			
		} 
		if ($out[1] ne 'NULL') {
			# Rdata is requested
			my @rpath2;
			@rpath2 = split(?/?, $out[1]);
			for (my $i=0; $i<@rpath2; $i++) {
				if ($rpath2[$i] =~ m/^binfile/){
					$rtmpdir .=  join('/', @rpath2[1..$i]);
					$pos2=$i;
					print "binfile position=",$pos2,"\n" if $DEBUG;
					print "Rdata file name =" . $rpath2[$pos2] . "\n" if $DEBUG;
					$fname->[1] = join('/', @rpath2[($i-1) .. $i]);
					$counter++;
					last;
				}
			} 
		}
		push @{$fl}, $fname;
		$fname=[];
	}
	return $counter;
}

#############
##
##


sub printZeligManifestPage {
	my $wh=shift;
	my $stdyno=shift;
	my $stdyttl=shift;
	my $datasetname=shift;
	my $zlgOutFileSet=shift;
	my $RunOrder=shift;
	my $zRtmpHtml=shift;
	my $zRoutLog=shift;
	my $citationMessage=shift;
	my $nonZeligJob=shift;
	my $dirStringId=shift;
	my $aOption=shift;

	my $contents ="";
	for (my $i=0;$i<@{$RunOrder};$i++){
		if ($zlgOutFileSet->[$i]->[0]){
			$contents .= "<li><a href=\"" . $zlgOutFileSet->[$i]->[0] . "\">" . $RunOrder->[$i] . "</a>\&nbsp;<input type=\"button\" onclick=\"shwNshw('Rlog',event,'log')\" value=\"R Log File\" ></li>\n";
		} else {
			# failed case (no output)
			$contents .= "<span style=\"color:red;\"><li>" . $RunOrder->[$i] ." Failed:";
			if ($zRoutLog ne ""){
				$contents .= " <input type=\"button\" onclick=\"shwNshw('Rlog',event,'log')\" value=\"R Log File\" >";
			}
			$contents .= "</li></span>\n";
		}
	}
	my @tmp = split(?/?, $zRtmpHtml);
	
	if ($zRtmpHtml ne ""){
		if ($nonZeligJob ==1){
			# maybe success
			if ($aOption eq 'A03'){
				$contents .= "<li><a href=\"" . $tmp[$#tmp] . "\">" . 'Cross-tabulation' . "</a>\&nbsp;<input type=\"button\" onclick=\"shwNshw('Rlog',event,'log')\" value=\"R Log File\" ></li>\n";
			} else {
				$contents .= "<li><a href=\"" . $tmp[$#tmp] . "\">" . 'Descriptive Statistics' . "</a>\&nbsp;<input type=\"button\" onclick=\"shwNshw('Rlog',event,'log')\" value=\"R Log File\" ></li>\n";
			}
		} elsif ($nonZeligJob ==0){
			# failed case
	 		if ($aOption eq 'A03'){
				$contents .= "<span style='color:red'><li>Cross-tabulation Failed: <input type=\"button\" onclick=\"shwNshw('Rlog',event,'log')\" value=\"R Log File\" ></li></span>\n";
			} else {
				$contents .= "<span style='color:red'><li>Descriptive Statistics Failed: <input type=\"button\" onclick=\"shwNshw('Rlog',event,'log')\" value=\"R Log File\" ></li></span>\n";
			}
		}
	}

	$contents = "<ol>" . $contents . "</ol>\n";
	
	
	my $pt_d ="";
	my $pt_m ="";
	my $pt_e ="";
	
my $JSCRIPT=<<END;
<!--
var ie=(document.all)&&(document.getElementById);
var ns=(document.getElementById)&&(!document.all);

function shwNshw(id,e, label) {
	if (ns) {
		var sourcelement = e.target;
	} else if(ie) {
		var sourcelement = event.srcElement;
	}
	var srcEl = document.getElementById(id);
	
	if (srcEl.style.display == "none") {
		srcEl.style.display = "";
		// this part depends on the design of a button
		sourcelement.value="Hide " +label;
	} else if (srcEl.style.display == "") {
		srcEl.style.display = "none";
		// this part depends on the design of a button
		sourcelement.value ="Show " +label;
	}
}
//-->
END

my $r2htmlStyle=<<ENDX;
<!-- 

H1 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 20pt;
	font-style: normal;
	font-weight: normal;
	color: #FFFFFF;
	background: #004080;
	text-align: center;
	margin: 10pt 2.5%
}

H2 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 18pt;
	font-style: normal;
	font-weight: normal;
	color: #FFFFFF;
	background: #000000
}


H3 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14pt;
	font-style: normal;
	font-weight: bold;
	color: #004080
}

H4 {
	font-family: T, Helvetica, sans-serif;
	font-size: 10pt;
	font-style: normal;
	font-weight: bold;
	color: #000000;
	line-height: 16pt
}
-->
ENDX
	my $goBackParam = '-2';
	if ($browserType eq 'Firefox'){
		$goBackParam = '-1';
	}
	my $hrefString  = 'javascript:window.history.go(' . $goBackParam . ');';
	$pt_d .= $q->start_html (
	-title=> "Dataverse Analysis: Request # $$ ", 
	-script=>$JSCRIPT,-style=>{-code=>$r2htmlStyle});
	$pt_d .= $q->h1 ("Dataverse Analysis");
	$pt_d .= $q->blockquote($q->strong($q->a( {href=>$hrefString},'Go back to the previous page')));

	$pt_d .= $q->hr ("");
	$pt_d .= $q->h2 ("Citation Information about the Dataset:");
	
	
	$pt_d .= $q->ul (
		#$q->li({-type=>'disc'},["Study ID Number: $stdyno","Study Title: $stdyttl"])
		$q->li({-type=>'disc'},["Study Title: $stdyttl"])
	);
	
	# <input type="button" onclick='shwNshw("citationInfo",event,"Citation Info")' value="Citation Info" >
	if ($citationMessage ne ""){
	$pt_d .= $q->blockquote ('Click ',
		$q->button(-value=>'Citation Info',-onClick=>"shwNshw('citationInfo',event,'Citation Info')"),
	' to see how to cite this data set.');
	}
	# modeling results
	$pt_m .= $q->hr ("");
	$pt_m .= $q->h2 ("Results: Request # $$ (${dirStringId})");
	#$pt_m .= $q->blockquote("The modeling results of your request is included in this tar file. Each model's estimation results and Descriptive Statistics (if applicable) can be displayed by clicking the following links");
	
	$pt_m .= $q->blockquote("To check each model's estimation results and descriptive statistics (if applicable), click the following links.");
	
	$pt_m .= $q->blockquote($q->strong("Please note: the result pages including this index page and other data files will be automatically erased one hour later."));

	$pt_m .=$contents;
	
	
	$pt_e .= $q->hr ("");
	my @rdta = split(?/?,$RtmpData);
	my @rprg    = split(?/?,$Rcode);

	$pt_e .= $q->ul(
	$q->li( $q->a({href=>"$rdta[$#rdta]"}, "Download the tab-delimited data file for this R program") ), 
	$q->li( $q->a({href=>"$rprg[$#rprg]"}, "Download the R code file") )
	);


	$pt_e .= $q->hr ("");

	my $tm = localtime;
	$pt_e .= $q->blockquote ("Thank you for using the Dataverse.", "(This file was created at: $tm (US EST) and to be erased one hour later)");
	my $mailSubject=uri_escape("(About ${SERVER} DSB_Request_$$ ${dirStringId} ${tm} )");
	#$pt_e .= $q->blockquote ($q->a({href=>"http://${SERVER}/VDC/Mail/index.jsp?subject=${mailSubject}",
	$pt_e .= $q->blockquote ($q->a({href=>"http://${appSERVER}/faces/ContactUsPage.jsp",
		onclick=>"window.open(this.href,'contact','width=640,height=480,left=0,top=0,scrollbars=1,resizable=1');return false;", target=>'contact'}, "Contact us about this request"), );
	$pt_e .= $q->end_html;

	# print all contents to the manifest file
	
	print $wh $pt_d . "\n\n";
	if ($citationMessage ne ""){
		print $wh "<div id=\"citationInfo\" style=\"display:none; border:#999 solid 1px ;\">\n\n";
		# print the citation file contents here
		print $wh $citationMessage;
		print $wh "</div>\n\n";
	}
	
	
	
	print $wh $pt_m . "\n\n";
	if ($zRoutLog ne ""){
		print $wh "<div id=\"Rlog\" style=\"display:none; border:#999 solid 1px ;\">\n<h3>R Log File</h3>\n<pre>\n\n";

		# print the log file contents here
		open (Z, $zRoutLog);
		my $logBlock=0;
		my $startLine ='> # iteration lines begin here';
		my $endLine ='> ########## Code listing: end ##########';
		my $masktoken = '**********';
		my $write=1;
		while (<Z>){
			my $temp =$_;
			chomp $temp;
			if ($temp eq $startLine){
				$logBlock=1;
			}
			if ($logBlock){
				$_ =~ s%$TMPDIR%$masktoken%;
				if ( $temp =~ /^> x<-read/){
					$_ =~ s/vdc/dvn/gi;
				}
				if ($_ =~ m/^To cite /){
					$write=0;
				}
				if ($write){
					print $wh $_ ;
				}
				if (($write == 0) && ($_ =~ m/^\s+[}]/)){
					$write=1;
				}
			} 
			if ($temp eq $endLine){
				$logBlock=0;
			}
		}
		close(Z);
		print $wh "</pre></div>\n\n";
	}
	print $wh $pt_e . "\n\n";
	return;
}


sub getRandomString {
	my $pwr = shift ;
	my $maxNo =10**$pwr;
	my @a = ('A' ..'Z');
	my @dirNo =();
	$dirNo[0] = sprintf "%0${pwr}u", int( rand($maxNo)) ;
	$dirNo[1]= $a[ int( rand($maxNo))%26 ];
	$dirNo[2] = sprintf "%0${pwr}u", int( rand($maxNo)) ;
	my $dirString = join('',@dirNo) ;

	#print TMPX 'dir string=' . $dirString . "\n";
	return $dirString;
}

sub locate_subsettingMetaFile {
    my $ddi_file = shift; 
    my $fileid = shift;
    my $format = shift; 

    my $subsetting_meta_object = new vdcRAP::SubsettingMeta; 

    return $subsetting_meta_object->vdc_subsettingMetaFile ( $ddi_file, $fileid, $format ); 
} 

sub make_SubsetFilter {
    my $metadata = shift; 
    my $varIdSeq = shift; 

    my $pat = ""; 

    my @varseq = split ( ",", $varIdSeq ); 

    unless ( $metadata->{_varStartPos}->{$varseq[0]} )
    {
	# tab-delimited file:

	if ( $metadata->{wholeFile} == 1 )
	{
	    return "cat"; 
	}
	    
	$pat = "/usr/local/VDC/bin/rcut -f "; 

	for my $v (@varseq)
	{
	    $pat .= ( $metadata->{_varNoRcut}->{$v} . "," );
	}
	
	chop $pat; 
	
    }
    else
    {
	# fixed-field:

	my $logical_records = $metadata->{logicalRecords}; 

	$logical_records = 1 unless $logical_records; 

	$pat = "/usr/local/VDC/bin/rcut -r " . $logical_records . " -c"; 

	for my $v (@varseq)
	{
	    my $v_recsegno = $metadata->{_varRecSegN}->{$v};
	    my $v_start = $metadata->{_varStartPos}->{$v};
	    my $v_end = $metadata->{_varEndPos}->{$v};

	    $pat .= ( $v_recsegno . ":" . $v_start . "-" . $v_end . ",");
	}

	chop $pat; 

	$pat .= " -o"; 

    }

    my $delim="\t";
    $pat = $pat . " -d'" . $delim . "'";  

    return $pat;
}

sub check_fileFormat {
    my $ddi_file = shift; 
    my $fileid = shift;

    open DDI_IN, $ddi_file || return undef; 

    while ( <DDI_IN> )
    {
	if ( /<location .*fileid=\"([^\"]*)\"/ )
	{
	    my $id_found = $1; 
	    if ( $id_found eq $fileid )
	    {
		if ( /StartPos=\"[0-9]+\"/ &&
		     /EndPos=\"[0-9]+\"/ )
		{
		    close DDI_IN; 
		    return 'fixed'; 
		}
		else
		{
		    close DDI_IN; 
		    return 'tab';
		}
	    }
	}
    }

    close DDI_IN; 
    return undef; 
}

END {
   exitChores();
   kill( 'INT', -$$ );    # This pid and its kids including stray R procs
}


__END__

=head1 NAME

univar03.cgi - a cgi script for statistical analyses and downloading data files

=head1 DESCRIPTION

This cgi scripts receives a set of parameters and 

(0)initializes the progress

(1)gets a DDI to extract requested variables' metadata from it

(2)gets a data file and get requested variables' data

(3)prints an R-code file that executes a requested data analysis or data conversion

(4A)executes the R-code and dumps results as an html file

(5A)prints back the html file

or

(4D)executes the R-code, saves results as a data file, and prints requested code files if requested

(5D)sends back the requested file(s)


=head1 USAGE


=head1 AUTHOR

Akio Sone, Virtual Data Center Project, Harvard University

(<URL:http://thedata.org/>)

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2004 President and Fellows of Harvard University

GNU General Public License



=cut

