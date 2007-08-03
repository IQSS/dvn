#!/usr/bin/perl -I/usr/local/VDC/perl

# Copyright (C) 2005 President and Fellows of Harvard University
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
my $buffer;
my $DEBUG=0;
# R code to print the config file
# library(VDCutil);  printZeligSchemaInstance('../configZeligGUI.xml')

my $flnm = "$TMPDIR/configZeligGUI.xml";
#my $server=`hostname -f`; use  $SERVER from glv03
my $rCodeFile = "$TMPDIR/zeligConfig.$$.R";
open(RH, "> $rCodeFile");
# printZeligSchemaInstance(filename=NULL, serverName=NULL,vdcAbsDirPrefix=NULL)
print RH "library(VDCutil);  printZeligSchemaInstance('" . $flnm ."',", "'",$SERVER,"')";
close(RH);
my $log_file = "$TMPDIR/zeligConfig.$$.log";
open(FH, "> $log_file") if $DEBUG;
open(PH, "R --slave < $rCodeFile  2>&1 |");
while (<PH>) {
	print FH $_ if $DEBUG;
	chomp;
	if (/(Error\s+in)|(Execution\s+halted)/i){
		#print $FH "error in R run=",$1,"\t",$2,"\n" if $DEBUG;
		#print $FH "error in R run=",$_,"\n" if $DEBUG;
		#$runr=0;
	} else {
		#print $FH "Zelig config file is created\n" if $DEBUG;
	}
}
close(PH);
close(FH) if $DEBUG;

unless ($DEBUG) {
	unlink($rCodeFile);
}
my $size = -s $flnm;

my $cntnttyp = "text/xml" ;  

if (-r $flnm  && -f $flnm &&  open(dwnldFile, "<$flnm")) {
		print $q->header ( -type=>$cntnttyp);
		while (read dwnldFile, $buffer, $size){
			print $buffer;
		}
		close(dwnldFile);
} else {
	print $q->header ( -status=>'404 Not Found: Temporary Results Deleted', -type=>'text/html');
		die;
}

1;
