#!/usr/bin/perl 

if ( $#ARGV != 2 )
{
#    print STDERR "usage: script_edit_icpsrddi.pl <REPOSITORY> <DDISTRING> <STUDYNUM>\n";
    print STDERR "usage: script_edit_icpsrddi.pl <REPOSITORY> <DDISTRING> <STUDYNUM>\n";
    exit 1; 
}

$ddistring = shift @ARGV;
$repository = shift @ARGV; 
$study = shift @ARGV; 

$xmlout = "";

while (<>)
{

  s:http\://www.icpsr.umich.edu/DDI/Version.*dtd:http\://www.icpsr.umich.edu/DDI/$ddistring:;

    next if /^$/;

    next if /^<\?/;

    # strip their HTML links: 

#    s/<a href[^>]*>//i;
#    s/<\/a>//i;

    # strip the "fileDscr" section provided by the ICPSR

    $filedescr++ if /<fileDscr/; 

    if ( /\&/ )
    {
	s/(\&[a-zA-Z0-9\#\;]*)/&fixamp($1)/ge;
    }

    if ( /<stdyDscr/ )
    {
	$stdyDscr++;
    }

    if ( $stdyDscr && /<citation/ )
    {
	$stdyDscrcitation++; 
    }

    # add "holdings": 

    if ( $stdyDscrcitation )
    { 
	unless ( $holdings )
        {
	    if ( /<\/citation/ )
	    {
		$xmlout .= "\t\t<holdings URI=\"http://" . $repository . "/VDC/Repository/0.1/Access?name=hdl:1902.2/" . $study . "\" source=\"producer\"/>\n";
		$holdings = 1;
	    }

	    $xmlout .= $note if $note; 
	    $note = "";

	    if ( /<notes/ )
	    {
		$readnote++;
		$note = $_; 
		$readnote=0 if /<\/note/;
		while ( $readnote )
		{
		    $_ = <>; 
		    s/<a href[^>]*>//i;
		    s/<\/a>//i;
		    $note .= $_; 
		    $readnote=0 if /<\/note/;
		}
	    }
	}
    }

    $xmlout .= $_ unless $filedescr || $note;

    if ( /<\/stdyDscr/ )
    {
	$stdyDscrcitation=0;
	$note = "";
    }

    $filedescr=0 if /<\/fileDscr/;

}

$xmlout =~s/<a[ \t\n]*href[^>]*>//gis;
$xmlout =~s/<\/a>//gis;
$xmlout =~s:</collSize>[ \t\n]*<collSize>:\n:g;

print $xmlout;

sub fixamp {
    local ( $tok ) = $_[0]; 

    return $tok if $tok =~ /\&[a-zA-Z0-9#]+;/; 

    $tok =~ s/&/&amp;/;

    return $tok; 

}




