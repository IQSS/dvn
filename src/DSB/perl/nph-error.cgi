#!/usr/bin/perl -I/usr/local/VDC//perl/
use vdcCGI;
use strict;

my($q)=new vdcCGI;
my($rs)=$ENV{'REDIRECT_STATUS'};
if (!$rs) {
	$rs="500 Internal Server Error: General Error";
}

print $q->header ( -status=>"$rs", -type=>'text/html', -nph=>1);
print $q->start_html ( -title=> 'VDC Error');
print $q->h2("The VDC system has detected an error -- You should be automatically forwarded to the VDC status page\n");

print $q->end_html;

1;
