#!/usr/bin/perl

use Getopt::Long;
GetOptions (%opt, "version=s" => \$version, "build=s" => \$build);


$specfile = shift @ARGV;

open ( S, $specfile ) || die "couldn't open spec file\n";
open ( T, ">/tmp/temp.spec.$$" ) || die "couldn't open temp file\n";

while ( <S> )
{
    chop;
    if ( /^Release:[ \t]*(.*)$/ )
    {
	$m_build = $1; 
	if ( defined($build) )
	{
	    print T "Release: " . $build . "\n";
	}
	else
	{
	    print T "Release: " . ($m_build+1) . "\n";
	}
    }
    elsif ( /^Version:[ \t]*(.*)$/ && $version )
    {
	print T "Version: " . $version . "\n";
    }
    else
    {
	print T $_ . "\n";
    }
}

close S;
close T; 

unless ( defined ($m_build) )
{
    die "could not find Release number in the spec file.\n";
}

system "/bin/mv /tmp/temp.spec.$$ $specfile";

exit 0;
