#!/usr/bin/perl

unless ( $#ARGV == 2 || $#ARGV == 3 )
{
    print STDERR "usage: ./script_merge.pl [--studyonly] <STUDY_NUM> <SCHEMA_VERSION> <REPOSITORY>\n";
    die; 
}

use MIME::Base64; 

$datalevel = 1; 

if ( $#ARGV == 3 )
{
    if ( shift @ARGV eq "--studyonly" )
    {
	$datalevel = 0; 
    }
}

$study = shift @ARGV; 
$schema_ver = shift @ARGV;
$repository = shift @ARGV; 

$study_n = sprintf ( "%05d", $study ); 

if ( open ( TABFILES, $study_n . "/datafiles.raw" ) )
{

    $i = 0; 
    $tabfiles[$i] = {}; 
    
    while ( <TABFILES> )
    {
	chop; 
	($tf, $ds, $ttl, $datafilename) = split ( "\t", $_ ); 
	
	$tf = encode_base64 ($tf); 
	$tf =~s/\n//g; 

	$tf .= ( "/" . $datafilename ); 

	$tabfiles[$i]->{tabfile} = $tf;
	$tabfiles[$i]->{dataset} = $ds;
	$tabfiles[$i]->{note} = $ttl; 
	$tabfiles[$i]->{fname} = $datafilename; 

	$i++;
    }
    close TABFILES;
}
#else
#{
#    print STDERR "warning: no datafiles description for the study;\n";
#}

open ( STUDY, $study_n . "/" . $study_n . ".study.xml" ) || die $@; 

#$on = "yes";

while ( <STUDY> )
{
#    $on = "yes" if /<stdyDscr/; 
    $on = "yes" if /<docDscr/; 

#    # fix screwed up PURLs: 
#
#    s/\/\/purl\.data\.org/\/\/purl\.thedata\.org/g;

    s/(<holdings.*URI="http:\/\/).*(\/VDC\/Repository\/)/$1$repository$2/; 

    if  ( /<\/stdyDscr>/ )
    {
      s:^(.*</stdyDscr>).*$:$1:;
	push @study_level, $_; 
	last; 
    }

    push @study_level, $_ if $on; 
	
}

close STUDY; 

if ( $datalevel && open ( DATA, $study_n . "/" . $study_n . ".data.xml" ) )
{
    $file_n = 0; 
    $on = undef; 

    print "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
    print "<codeBook xmlns=\"http://www.icpsr.umich.edu/DDI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.icpsr.umich.edu/DDI http\://www.icpsr.umich.edu/DDI/Version" . $schema_ver . ".xsd\">\n";
#    print "<docDscr>\n";
#    print "</docDscr>\n";

    print join ( "", @study_level ); 

    while ( $_ = <DATA> )
    {
	if ( /<fileDscr/ )
	{
	    while ( /<fileDscr/ )
	    {
		$fileDscr = ""; 

		# let's insert the proper fileDscr URIs here: 
	    
		$url = "http://" . $repository . "/VDC/Proxy/0.1/Access/" . $tabfiles[$file_n]->{tabfile};
		
		while ( !/<\/fileDscr>/ )
		{
		  s/URI\=\"\"/URI\=\"$url\"/;
		  $fileDscr .= $_; 
		  if ( /<fileTxt/ )
		  {
		      $fileDscr .= 
			  "\t\t<fileName>" . $tabfiles[$file_n]->{fname} . "<\/fileName>\n";
		  }
		  $_ = <DATA>;
		}


		# let's add the "notes" section: 
		$fileDscr .= "\t<notes type=\"icpsr:category\" subject=\"id\">";
		    
		$ds = $tabfiles[$file_n]->{dataset};
		    
		$fileDscr .= $ds if $ds; 
		    
		$fileDscr .= "</notes>\n";

		$fileDscr .= "\t<notes type=\"icpsr:category\" subject=\"description\">";
#		$fileDscr .= "\t";
		    
		$note = $tabfiles[$file_n]->{note};
		    
		print STDERR "WARNING: could not map " . $study_n . "_" . $file_n . ".tab\n" unless $note;

#		$fileDscr .= ($note . "\n") if $note; 
		$fileDscr .= $note if $note; 
		    
		$fileDscr .= "</notes>\n";
		$fileDscr .= $_;

		print $fileDscr;  

		$_ = <DATA>;

		$file_n++; 
	    }

	    $on = "yes"; 

	}

	print if $on; 

	$on = undef if /<\/dataDscr>/;

    }

    close DATA;
}
else
{
    # We haven't generated a data-level DDI for this study; 
    # let's ingest the study-level part we got from ICPSR, with 
    # minimal cosmetic improvements. 

#    print "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
#   print "<!DOCTYPE codeBook SYSTEM \"http://www.icpsr.umich.edu/DDI/" . $ddistring . "\">\n"; 
#    print "<codeBook>\n";

    print "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
    print "<codeBook xmlns=\"http://www.icpsr.umich.edu/DDI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.icpsr.umich.edu/DDI http\://www.icpsr.umich.edu/DDI/Version" . $schema_ver . ".xsd\">\n";
#    print "<docDscr>\n";
#    print "</docDscr>\n";

    print join ( "", @study_level ); 
	    
}

if ( open ( OTHERMAT, $study_n . "/othermat.xml" ) )
{
    while ( <OTHERMAT> )
    {
	print; 
    }
}
else 
{
    print STDERR "WARNING: no \"othermat.xml\" file found for $study.\n";
}

# and, finally

print "</codeBook>\n";
