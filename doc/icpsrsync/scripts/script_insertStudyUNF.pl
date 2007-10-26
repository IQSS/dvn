#!/usr/bin/perl

$unf_file = shift @ARGV; 

unless ( open U, $unf_file )
{
    print STDERR "could not open $unf_file.\n";
    exit 1; 
}

$unf_string = <U>; close U; 

chop $unf_string if $unf_string;

$stdyddi = ""; 

while (<>)
{
    $stdyddi .=$_;
    last if /<\/stdyDscr/; 
}

if ( $unf_string =~/^UNF:[34]:/ )
{    
    $unf_note = "<notes subject=\"Universal Numeric Fingerprint\" level=\"study\" source=\"archive\" type=\"VDC:UNF\">" . $unf_string . "</notes>\n";

    unless ( $stdyddi =~s/(<stdyDscr.*[^ \t])([ \t]*)(<\/citation>.*<stdyInfo)/$1$2$unf_note$2$3/si )
    {
	$unf_file =~/([0-9]*)\//; 
	$study = $1; 
	print STDERR "warning: no <citation> in the stdyDscr, study $study.\n";
    }
}

print $stdyddi; 

while ( <> )
{
    print; 
}

exit 0; 
