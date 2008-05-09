#!/usr/bin/perl

unless ( $#ARGV == 1 )
{
    print STDERR "USAGE: ./script <STUDY_NUM> <DIRECTORY>\n";
    exit 1; 
}

$study = shift @ARGV; 
$dir = shift @ARGV; 

open MD, ">" . $dir . "/ingest.metadata" || die $@; 
open MISSING, ">" . $dir . "/files.missing" || die $@; 

while (<>)
{
    chop; 
    s/\"//g;

    ($tape, $label, $description, $permissions, $category) = split (/\t/, $_); 

    %audio_files = ('A', 0, 
		    'B', 0); 

    ###print $category . "\n";
    
    if ( $category=~/A \+ B/ )
    {
	###print STDERR "both files should be present.\n";
	$audio_files{'A'} = 1; 
	$audio_files{'B'} = 1; 

    }
    elsif ( $category=~/A\.wav/ )
    {
	$audio_files{'A'} = 1; 
    }
    elsif ( $category=~/B\.wav/ )
    {
	$audio_files{'B'} = 1; 
    }

    $tape =~s/^A/A_/; 

    for $side ( 'A', 'B' )
    {
	if ( $audio_files{$side} )
	{
	    $wavfile = $dir . "/" . $tape . "_" . $side . ".wav";
	    $mp3file = $tape . "_" . $side . ".mp3";

	    unless ( -f $wavfile )
	    {
		print STDERR "WARNING: no such file $wavfile!\n";-
		print MISSING $wavfile . "\n";
	    }
	    else
	    {
		print $wavfile . "\n";
		print MD join ( "\t", $mp3file, "/nfs/vdc/DVN/data/1902.1/" . $study . "/" . $mp3file, 'audio/x-mp3', $description ) . "\n";
	    }
	}
	
    }



}

close MISSING; 
close MD; 
