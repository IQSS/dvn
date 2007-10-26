#!/usr/bin/perl

$c = 0; 

while (<>)
{
    chop;
    s/^.*<notes[^>]*>//;
    s/<\/notes>.*$//;

    push @unf_strings, '"' . $_ . '"';

    $c++; 
}

if ( $c == 0 )
{
    exit 0; 
}

$unfstrings_merged = join (",", @unf_strings);

$R_code = 'library("UNF"); cat(as.character(summary(as.unf(c(' . $unfstrings_merged . ')))))'; 

$unf_study = `echo '$R_code' | R --slave`; 

print $unf_study . "\n";

exit 0; 



