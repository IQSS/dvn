#!/usr/bin/perl -I/usr/local/VDC/perl

# Copyright (C) 2001 President and Fellows of Harvard University
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

use vdcCGI;
require "glv03";

my $q = new vdcCGI;
my $flnm = $q->param('dataflnm');
my $xtsn = $q->param('flxtn');
my $stdyno = $q->param('stdyno');
my $buffer;
my %typemap = (
			'D01'=> 	{mime=>'text/tab-separated-values',		ext=>'dat', bm=>0},
			'D02'=> 	{mime=>'application/x-rlang-transport',	ext=>'ssd',   bm=>0},
			'D03'=> 	{mime=>'application/x-stata-6',			ext=>'dta', bm=>1},
			'R'=> 	{mime=>'text/plain',	ext=>'r',   bm=>0},
			'unknown'=>	{mime=>'application/x-unknown',			ext=>'bin', bm=>1},
		);
if (!$typemap{"$xtsn"}) { $xtsn="unknown";}

my $filename = 'da' . $stdyno . '_subset.' . $typemap{"$xtsn"}{'ext'};
$flnm = $TMPDIR . "/" . $flnm; # SECURITY!
my $size = -s $flnm;

	
	my $cntnttyp = $typemap{"$xtsn"}{'mime'} ;  

	if (-r $flnm  && -f $flnm &&  open(dwnldFile, "<$flnm")) 
		{
		       print $q->header ( -type=>"$cntnttyp ; name=\"$filename\"",
                 	-Content_disposition=> "attachment; filename=\"$filename\"",
                 	-Content_length=>$size, -nph=>1);
			if ($typemap{"$xtsn"}{'bm'}) {binmode STDOUT;}
			while (read dwnldFile, $buffer, $size){
				print $buffer;
			}
			close(dwnldFile);

			 unlink ($flnm);
		} 
	else 
		{
    print $q->header ( -status=>'404 Not Found: Temporary Results Deleted', -type=>'text/html', -nph=>1);
           print $q->start_html ( -title=> 'Temporary Results deleted');
           print $q->h2("The temporary file you requested has already been deleted:");
           print $q->end_html;
			die;
		}

