#!/usr/bin/perl

unless ( $#ARGV == 0 || $#ARGV == 1 )
{
    print STDERR "USAGE: script_othermat.pl <Repository> [datafiles.raw] < othermat.raw > othermat.xml\n";
    exit 1; 
}

$repository = shift @ARGV;
$datafiles = shift @ARGV; 

use URI::Escape;
use MIME::Base64;

my %datafilesFound; 

if ( -f $datafiles ) 
{
    open DF, $datafiles || die "could not open $datafiles";

    while ( <DF> )
    {
	split; 
	$_[0] =~s/^.*\?//; 
	$datafilesFound{$_[0]} = 1; 
    }

    close DF; 
}

$study = <>; 

if ( $study =~ /study could not be downloaded/ )
{
    print STDERR "no datafiles/othermat info found for this study";
    exit 0;
}

while (<>)
{
    chop; 

    s/<[^>]*>//g;
    s/[<>]//g;


    ($file, $restrict, $lbl, $ds, $note, $txt, $url) = split ( "\t", $_ ); 

    $urlCheck = $url; 
    $urlCheck =~s/\.zip$//; 

    next if $datafilesFound{$urlCheck}; 

#    $access = $restrict ? " (Restricted access)" : " (Freely available)"; 
    $type   = $restrict ? "other" : "documentation";


    print "<otherMat type=\"" . $type . "\" level=\"study\"  ";

#    print "URI=\"http://vdc-prod.hmdc.harvard.edu/VDC/Repository/0.1/Access?name=" . $file . "\">\n";

    $file =~ s/\.gz$//;
    $url =~ s/\.gz$//;

#    if ( $file !~ /\.pdf$/ && $file =~ /^cb/ )
#    {
#	$file .= '.txt';
#    }

    $url = "http://www.icpsr.umich.edu/cgi/OR/file?" . $url; 
#    $url = uri_escape ($url, "&?=:/");
#    $url = uri_escape ($url, "%");

    $url = encode_base64 ( $url ); 
    $url =~s/\n//g; 
    $url .= ("/" . $file); 

    print "URI=\"http://" . $repository . "/VDC/Proxy/0.1/Access/" . $url . "\">\n";
	   $labl = $file;
	   $labl =~s:^.*/::g;

#    print "    <labl>" . $labl  . $access . "</labl>\n";
    print "    <labl>" . $labl  . "</labl>\n";

    $txt=~s/[\(\)]//g;
    if ( $txt =~/\&/ )
    {
        $txt =~s/(\&[a-zA-Z0-9#;]*)/&fixamp($1)/ge;
    }

    print "    <txt>"  . $txt  . "</txt>\n";

    if ( $note =~/\&/ )
    {
        $note =~s/(\&[a-zA-Z0-9#;]*)/&fixamp($1)/ge;
    }

    print "    <notes type=\"icpsr:category\" subject=\"id\">". $ds . "</notes>\n";
    print "    <notes type=\"icpsr:category\" subject=\"description\">". $note . "</notes>\n";

    print "</otherMat>\n";

}

sub fixamp {
    local ( $tok ) = $_[0]; 

    return $tok if $tok =~ /\&[a-zA-Z0-9#]+;/; 

    $tok =~ s/&/&amp;/;

    return $tok; 

}



