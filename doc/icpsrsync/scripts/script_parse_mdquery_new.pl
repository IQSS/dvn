#!/usr/bin/perl

use URI::Escape;
use MIME::Base64;

$repository = shift @ARGV; 

unless ( $repository )
{
    print STDERR "usage: ./script <REPOSITORY>\n";
    exit 1; 
}


$objects = {}; 

%FILETYPE = 
    ( 
	"dct",	"",
	"dmp",	"",
	"do",	"",
	"doc",	"application/msword",
	"dta",	"application/x-stata-v6",
	"hpm",	"",
	"odd",	"",
	"pdf",	"application/pdf",
	"por",	"application/x-spss-por",
	"sas",	"text/plain",
	"sav",	"application/x-spss-sav",
	"sps",	"text/plain",
	"stc",	"",
	"txt",	"text/plain",
	"wpd",	"application/wordperfect",
	"xls",	"application/vnd.ms-excel",
	"xpt",	"",
	"zip",	""
      ); 


while (<>)
{
    if ( /^Study: ([0-9]*)$/ )
    {
        $study = $1; 

        %otherm = (); 

        $_ = <>; chop; 

	unless ( s/^Title: // )
	{
	    print STDERR "WARNING: no title line! -- $_";
	}

        $title = $_; 

	$dataset = 0; 
	$dataset_title = "Study-Level Files"; 

        $$objects{$study} = {}; 

        $$objects{$study}->{title} = $title; 
        $$objects{$study}->{datasets} = []; 
        $$objects{$study}->{downloads} = {}; 
        $$objects{$study}->{max} = 0; 


	$$objects{$study}->{datasets}->[0] = {}; 
        $$objects{$study}->{datasets}->[0]->{title} = $dataset_title; 


        $study_n = sprintf ( "%05d", $study ); 

        unless ( -d $study_n )
        {
            system "mkdir $study_n" || die $@; 
        }

        close OMR;
        
        open ( OMR, ">$study_n/othermat.xml" ) || die $@; 

#        print OMR "STUDY: " . $study_n . "\n";

        print STDERR "."; 

    }
    elsif ( /^Dataset: ([0-9]*)$/ )
    {
        $dataset = $1; 
        $_ = <>; 
        chop; 

	unless ( s/^Dataset Title: // )
	{
	    print STDERR "WARNING: no dataset title line! -- $_";
	}

        s/[ \t]+/ /g;
 
        $dataset_title = $_; 

	unless ( $$objects{$study}->{datasets}->[$dataset] )
	{
	    $$objects{$study}->{datasets}->[$dataset] = {}; 
	}
        $$objects{$study}->{datasets}->[$dataset]->{title} = $dataset_title; 
        
    }
    elsif ( /^([0-9]*) ([0-9]*) ([0-9]*) ([^ ]*) ([0-9]*) ([0-9\-]*) ([^ ]*) ([A-Z]*)/ )
    {

    	$study = $1; 
	$dset  = $2; 

	unless ( $1 == $study )
        {
            print STDERR "Error parsing input (study $study):\n";
            print STDERR $_; 
            exit 1; 
        }
        unless ( $2 == $dataset )
        {
	    if ( $2 == 1 )
	    {
		# we'll just assume the dataset title is skipped and it's the 
		# same as the title of the study: 

		$dataset = 1; 
		$dataset_title = $title; 
		unless ( $$objects{$study}->{datasets}->[1] )
		{
		    $$objects{$study}->{datasets}->[1] = {}; 
		}
		else
		{
		    print STDERR "Error parsing input (study $study, 1st dataset):\n";
		    print STDERR $_; 
		    exit 1; 
		}		    

		$$objects{$study}->{datasets}->[$dataset]->{title} = $dataset_title; 

	    }
	    else
	    {
		print STDERR "Error parsing input (study $study, dataset $dataset):\n";
		print STDERR $_; 
		exit 1; 
	    }

        }

        $fileid     = $3; 
        $filename   = $4; 

	$linelen    = $5; ## (only for datafiles!)
	$cases      = $6; 
	$timestamp  = $7; 
	$status     = $8; 


	next unless $status eq "AVAILABLE";


        $url = "comp=gzip&study=" . $study . "&ds=" . $dataset . 
	    "&file_id=" . $fileid; 

	$url = "http://www.icpsr.umich.edu/cgi-bin/bob/file?" . $url; 

	$url_encoded = encode_base64 ( $url ); 
	$url_encoded =~s/\n//g; 
	$url_encoded .= ("/" . $filename); 


	print OMR "<otherMat type=\"" . $type . "\" level=\"study\"  "; 

	print OMR "URI=\"http://" . $repository . "/VDC/Proxy/0.1/Access/" . $url_encoded . "\">\n";
	print OMR "    <labl>" . $filename  . "</labl>\n";


	$txt = $filename; 
	$txt =~s/[0-9]*\-//g; 
	$txt =~s/\.[a-z]*$//; 


#	$txt=~s/[\(\)]//g;
#
#	if ( $txt =~m/\&/ )
#	{
#	    $txt =~s/(\&[a-zA-Z0-9\#;]*)/&fixamp($1)/ge;
#	}

	print OMR "    <txt>"  . $txt  . "</txt>\n";


	print OMR "    <notes type=\"icpsr:category\" subject=\"id\">". $dataset . "</notes>\n";

	
	if ( $dataset_title =~/\&/ )
	{
	    $dataset_title =~s/(\&[a-zA-Z0-9\#;]*)/&fixamp($1)/ge;
	}
	print OMR "    <notes type=\"icpsr:category\" subject=\"description\">". $dataset_title . "</notes>\n";
	
	print OMR "</otherMat>\n";


	# codebooks and data files: 

#	# skipping the 13XXX series studies:

#	next if $study =~/^13[0-9][0-9][0-9]/; 

	if ( $filename =~ /\.([a-z]*)$/ )
	{
	    $extension = $1; 
	}
	else
	{
	    print STDERR "WARNING: no filename extension -- $filename\n";
	    $extension = ""; 
	}
	    
	# this is an SPSS codebook: 

	if ( $filename =~/\-Setup\.sps$/ )
	{
	    $$objects{$study}->{datasets}->[$dataset]->{card} = $filename; 
	    $$objects{$study}->{datasets}->[$dataset]->{cardurl} = $url; 
	}

	# this on the other hand is a data file: 

	if ( $filename =~/\-Data\.txt$/ )
	{
	    unless ( $$objects{$study}->{datasets}->[$dataset]->{data} )
	    {
		$$objects{$study}->{datasets}->[$dataset]->{data} = []; 
		$$objects{$study}->{datasets}->[$dataset]->{size} = []; 
		$$objects{$study}->{datasets}->[$dataset]->{dataurl} = []; 
	    }

	    push ( @{$$objects{$study}->{datasets}->[$dataset]->{data}}, $filename ); 
	    push ( @{$$objects{$study}->{datasets}->[$dataset]->{dataurl}}, $url ); 
	    push ( @{$$objects{$study}->{datasets}->[$dataset]->{size}}, ($cases * $linelen) ); 

	}

    }
    else
    {
	print STDERR "WARNING: unrecognized line format: $_";
    }
}

print STDERR "\n";
close OMR;

# OK, let's see what we got now:

@k = keys %$objects; 

open (D, ">downloads") || die $@; 

for $s (@k)
{
    $study_n = sprintf ( "%05d", $s ); 
    $ref = $$objects{$s}; 

    $max   = 0; 
    $total = 0; 
    $files = "";

    $downloads = {}; 

    open ( RDF, ">$study_n/datafiles.raw" ) || die $@; 
    @rawdatafiles = ();

    for ( $i = 1; $i <= $#{$ref->{datasets}}; $i++ )
    {
        $ds = $ref->{datasets}->[$i]; 

        $dsfiles = "";
	$card = ""; 

	if ( $card = $ds->{card} )
	{
	    print STDERR "found card (dataset $i, study $s).\n";
	    $cardurl = $ds->{cardurl};
	}
	elsif ( $card = $ref->{datasets}->[0]->{card} )
	{
	    print STDERR "no dataset-level card, but study-level card available  (dataset $i, study $s).\n"; 
	    $cardurl = $ref->{datasets}->[0]->{cardurl}; 
	}

	if ( $ds->{data} && $card ) 
	{
	    if ( $#{$ds->{data}} == 0 )
	    {
		$dsfiles .= " " . $i . " " . 
		    $ds->{data}->[0] . " " .
		    $card;
		
		$total += $ds->{size}->[0]; 
		
		$max = $ds->{size}->[0] 
		    if $ds->{size}->[0] > $max; 

		$downloads->{$ds->{data}->[0]} = $ds->{dataurl}->[0]; 
#		$downloads->{$ds->{card}} = $cardurl; 
		$downloads->{$card} = $cardurl; 

		unless ( $ds->{title} )
		{
		    print STDERR "no title for dataset $i, study $study_n;\n";
		}

		$rd = $ds->{dataurl}->[0] . "\t" . $i . "\t" .  $ds->{title} . "\t" . $ds->{data}->[0] . "\n";
		push @rawdatafiles, $rd; 
	    }
	    else
	    {
		print STDERR "WARNING: more than one physical file per dataset (" . $#{$ds->{data}} . "); skipping. ($study_n, $i)\n";
	    }
	}

	if ( $dsfiles )
	{
	    $files .= $dsfiles;
	}
    }

    # OK, can print it all out now: 

    if ( $files )
    {
        print $study_n; 
        print " " . $max . " " . $total . $files;

        print D $study_n . " " . $max . " " . $total; 

        for $u ( keys %$downloads )
        {
            print D " " . $u . ".zip " . $downloads->{$u}; 
        }
	
	for $r ( @rawdatafiles ) 
	{
	    print RDF $r; 
	}

        print D "\n";
        print "\n";
	
    }

    close RDF; 

}


sub fixamp {
    local ( $tok ) = $_[0]; 

    return $tok if $tok =~ /\&[a-zA-Z0-9\#]+;/; 

    $tok =~ s/&/&amp;/;
    
    return $tok; 

}
