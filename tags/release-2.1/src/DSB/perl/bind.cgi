#!/usr/bin/perl -I/usr/local/VDC/perl 

# Copyright (C) 2001 President and Fellows of Harvard University
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

use vdcLOG;
use vdcCGI; 
use CGI::Cookie;
use vdcLWP;
use HTTP::Cookies;
use HTTP::Request;
use HTTP::Response;
use File::Temp qw/tempfile/;
use sigtrap qw(die untrapped normal-signals stack-trace any error-signals);
require "glv03";

# FTP Setup
#
# FTP proxying works directly through SQUID, as apache isn't
# correctly setup for it. Since no VDC auth works over ftp anyway
# going through AuthZ is not required for ftp: url's.
#
# Since FTP requests are proxied over HTTP (LWP makes an http 
# request to SQUID which then makes a real FTP request,
# I believe that Net::FTP may not be called in this circumstance
# avoiding the LWP->Net::FTP bug. This requires further testing
#
$ENV{'FTP_PASSIVE'}=1; # needed for firewalls, affects Net:FTP under LWP
my ($FTP_PROXY);
if (defined($FTP_PROXY_URL)) {
        $FTP_PROXY=$FTP_PROXY_URL;
} 
my ($HTTP_PROXY) = $PROXY_URL;
my $logger= new_vdcLOG();
my($tmpfh, $tmpfilename) = tempfile( DIR => $TMPDIR);
my $MYTMPDIR = $TMPDIR;

# use strict AFTER glv03 variables used
use strict;
use warnings;

# if USE_MOD_DEFLATE set, leave compression of non-binder
# things to mod_deflate in apache, and do not attempt 
# to use gzip-encoding

my ($USE_MOD_DEFLATE)=0;

# default name to use if none supplied
my ($DEFAULTNAME)="savefile";

# _cookieSet
#
# set VDC auth cookies
#

sub _cookieSet {
   my ($ua,$url,$q,$request) = @_;
   my ($tcookie) = $q->http('HTTP_COOKIE');
   $request->header('X-VDC-Component' => "DSB");
   if ($tcookie) {
   	$request->header('cookie' => $tcookie);
   }
}

#
# parseBind
#
# Parses args to the Bind verb

sub parseBind {
   my($q)=@_;

   my (@urls)=$q->param('url');
   my ($binder)=$q->param('binder');
   my ($qua)= $q->user_agent();

   if ( $binder && ($binder ne "tar") && ($binder ne "attach") &&
	($binder ne 'NONE') && ($binder ne 'tgz') )  {
	$logger->vdcLOG_warning( 'VDC::DSB',  'BIND:parseBind',  
		"Unsupported Binder request, using tgr");
	$binder="tgz";
   }

   my $use_gz_encoding=0;
   if ( (!$binder) || 
	($binder eq 'tgz') ||
	(($binder eq 'NONE') && ($#urls>0)) ||
	(($binder eq 'attach') && ($#urls>0))
	  ) {
	$binder="tar";
   } elsif ($q->http('Accept-encoding') && $q->http('Accept-encoding')=~/gzip/) {
	$use_gz_encoding=1;
   }
		
   if ($binder eq 'NONE') {
	$binder=undef;
   }

   my $name=$q->param('name')|| "";
   return($name,\@urls,$use_gz_encoding,$binder);
}

#
# dobind
#
# This is the main control subroutine. Generally you call it.

sub dobind {
	my($script_name) = $ENV{'SCRIPT_URL'};
   	$logger->vdcLOG_info ( 'VDC::DSB', $script_name, 'bind' );
	my $q = new vdcCGI;

	my($name,$urls,$use_gz_encoding,$binder)= parseBind($q);
		
	if ($#{$urls}==-1) {
	    print $q->header ( 
		-status=>"400 Malformed Request: You Must Select Some Files to Download", 
		-type=>'text/html');
	    print $q->start_html ( -title=> "You must select some files to download.");
	    print $q->h2("You must select some files to download.");
	    print $q->end_html;
	    $logger->vdcLOG_warning( 'VDC::DSB',  $script_name,  
			"400 Malformed Request: You Must Select some Files to Download");
	}
   	select(STDERR); $| = 1;
	select(STDOUT); $| = 1;
	if (!$tmpfh) {
	    print $q->header ( 
		-status=>"500 Internal Server Error: Cannot Create Temp Files", 
		-type=>'text/html');
	    print $q->start_html ( -title=> "An error has occurred.");
	    print $q->end_html;
   		$logger->vdcLOG_fatal( 'VDC::DSB', $script_name, 
			'bind: could not create tmp file' );
	}
   	$logger->vdcLOG_info( 'VDC::DSB', $script_name, 'bind: fetching files' );
	streamUrls($urls,$name,$q,$use_gz_encoding,$binder);
   	$logger->vdcLOG_info( 'VDC::DSB', $script_name, 'bind: done' );
}


# assignName 
#
# Takes a response header and generates a name for the corresponding file
#

{
   my %names;  # This is a counter for repeated calls to assignNanem
	       # in order to assign unique names to items in the tarball

   my %typemap = (
'text/tab-separated-values'=> {ext=>'dat'},
'application/x-rlang-transport'=> {ext=>'ssd'},
'application/x-stata-6'=>{ext=>'dta'},
'application/pdf'=>{ext=>'pdf'},
'text/plain'=>{ext=>'txt'},
'text/html'=>{ext=>'htm'},
'text/xml'=>{ext=>'xml'},
'image/gif'=>{ext=>'.gif'},
'www/mime'=>{ext=>'.mime'},
'application/macbinary'=>{ext=>'.bin'},
'application/oda'=>{ext=>'.oda'},
'application/octet-stream'=>{ext=>'.exe'},
'application/pdf'=>{ext=>'.pdf'},
'application/postscript'=>{ext=>'.ai'},
'application/postscript'=>{ext=>'.eps'},
'application/postscript'=>{ext=>'.ps'},
'application/x-rtf'=>{ext=>'.rtf'},
'applicaion/x-gzip'=>{ext=>'.gz'},
'application/x-tar'=>{ext=>'.tgz'},
'application/x-csh'=>{ext=>'.csh'},
'application/x-dvi'=>{ext=>'.dvi'},
'application/x-hdf'=>{ext=>'.hdf'},
'application/x-latex'=>{ext=>'.latex'},
'text/plain'=>{ext=>'.lsm'},
'application/x-netcdf'=>{ext=>'.nc'},
'application/x-netcdf'=>{ext=>'.cdf'},
'application/x-sh'=>{ext=>'.sh'},
'application/x-tcl'=>{ext=>'.tcl'},
'application/x-tex'=>{ext=>'.tex'},
'application/x-texinfo'=>{ext=>'.texi'},
'application/x-texinfo'=>{ext=>'.texinfo'},
'application/x-troff'=>{ext=>'.t'},
'application/x-troff'=>{ext=>'.roff'},
'application/x-troff'=>{ext=>'.tr'},
'application/x-troff-man'=>{ext=>'.man'},
'application/x-troff-me'=>{ext=>'.me'},
'application/x-troff-ms'=>{ext=>'.ms'},
'application/x-wais-source'=>{ext=>'.src'},
'application/x-zip-compressed'=>{ext=>'.zip'},
'application/x-bcpio'=>{ext=>'.bcpio'},
'application/x-cpio'=>{ext=>'.cpio'},
'application/x-gtar'=>{ext=>'.gtar'},
'application/x-rpm'=>{ext=>'.rpm'},
'application/x-shar'=>{ext=>'.shar'},
'application/x-sv4cpio'=>{ext=>'.sv4cpio'},
'application/x-sv4crc'=>{ext=>'.sv4crc'},
'application/x-tar'=>{ext=>'.tar'},
'application/x-ustar'=>{ext=>'.ustar'},
'audio/basic'=>{ext=>'.au'},
'audio/basic'=>{ext=>'.snd'},
'audio/basic'=>{ext=>'.mp2'},
'audio/basic'=>{ext=>'.mp3'},
'audio/x-aiff'=>{ext=>'.aif'},
'audio/x-aiff'=>{ext=>'.aiff'},
'audio/x-aiff'=>{ext=>'.aifc'},
'audio/x-wav'=>{ext=>'.wav'},
'image/ief'=>{ext=>'.ief'},
'image/jpeg'=>{ext=>'.jpeg'},
'image/jpeg'=>{ext=>'.jpg'},
'image/jpeg'=>{ext=>'.jpe'},
'image/tiff'=>{ext=>'.tiff'},
'image/tiff'=>{ext=>'.tif'},
'image/cmu-raster'=>{ext=>'.ras'},
'image/x-portable-anymap'=>{ext=>'.pnm'},
'image/x-portable-bitmap'=>{ext=>'.pbm'},
'image/x-portable-graymap'=>{ext=>'.pgm'},
'image/x-portable-pixmap'=>{ext=>'.ppm'},
'image/x-rgb'=>{ext=>'.rgb'},
'image/x-xbitmap'=>{ext=>'.xbm'},
'image/x-xpixmap'=>{ext=>'.xpm'},
'image/x-xwindowdump'=>{ext=>'.xwd'},
'text/html'=>{ext=>'.html'},
'text/html'=>{ext=>'.htm'},
'text/plain'=>{ext=>'.c'},
'text/plain'=>{ext=>'.h'},
'text/plain'=>{ext=>'.cc'},
'text/plain'=>{ext=>'.hh'},
'text/plain'=>{ext=>'.m'},
'text/plain'=>{ext=>'.f90'},
'text/plain'=>{ext=>'.txt'},
'text/richtext'=>{ext=>'.rtx'},
'text/tab-separated-values'=>{ext=>'.tsv'},
'text/x-setext'=>{ext=>'.etx'},
'video/mpeg'=>{ext=>'.mpeg'},
'video/mpeg'=>{ext=>'.mpg'},
'video/mpeg'=>{ext=>'.mpe'},
'video/quicktime'=>{ext=>'.qt'},
'video/quicktime'=>{ext=>'.mov'},
'video/x-msvideo'=>{ext=>'.avi'},
'video/x-sgi-movie'=>{ext=>'.movie'},
'application/mac-binhex40'=>{ext=>'.hqx'},
'application/macwriteii'=>{ext=>'.mwrt'},
'application/msword'=>{ext=>'.msw'},
'application/msword'=>{ext=>'.doc'},
'application/msexcel'=>{ext=>'.xls'},
'application/vnd.lotus-1-2-3'=>{ext=>'.wks'},
'application/vnd.lotus-1-2-3'=>{ext=>'.wk1'},
'application/vnd.lotus-1-2-3'=>{ext=>'.wk2'},
'application/vnd.lotus-1-2-3'=>{ext=>'.wk3'},
'application/vnd.lotus-1-2-3'=>{ext=>'.wk4'},
'application/x-mif'=>{ext=>'.mif'},
'application/stuffit'=>{ext=>'.sit'},
'application/pict'=>{ext=>'.pict'},
'application/pict'=>{ext=>'.pic'},
'application/x-arj-compressed'=>{ext=>'.arj'},
'application/x-lha-compressed'=>{ext=>'.lzh'},
'application/x-lha-compressed'=>{ext=>'.lha'},
'application/x-deflate'=>{ext=>'.zlib'},
                  );


   sub assignName {
	my ($u,$res) = @_;	
	my ($name);
	NAME: {
	       $name = $res->header('content_disposition');
	       if ($name && $name=~s/.*name="(.*)".*/$1/i) { last NAME; }
	       $name = $res->header('content_type');
	       if ($name && $name=~s/.*name="(.*)".*/$1/i) { last NAME; }
	       $name = $u;
	       if ($name=~s/.*\/(.*)/$1/i) { 
			$name=~tr/[A-Z][a-z].[0-9]/_/sc;
			last NAME; 
		}
            } # end NAME
	    my ($ct) = $res->header('content_type');
	    $ct =~s/(.*?);.*/$1/;
	    if (($name!~/\./) && $typemap{$ct}{'ext'}) {
		$name .= '.' . $typemap{$ct}{'ext'};
	    }	
		
	    if (!$names{"$name"}) {
	    	$names{"$name"}=1;
	    } else {
	    	$names{"$name"}++;
		if ($name =~ /(.*)(\..*)/) {
			$name= $1 . "_" . $names{"$name"} . $2 ;
		} else {
	    		$name= $name . "_" . $names{"$name"};
		}
	    }
	    return($name);
   }
}

# streamUrls
#
# Fetches multiple objects, returns them in one 
# binder
#
# $urls = an array ref of urls to fetch
# $name = a name to give with the binder, if any
# $q = the CGI query object 
# $use_gz_encoding= whether gzip encoding is accepted
# $binder = the method for binding multiple files

sub streamUrls{
   my ($urls,$name,$q,$use_gz_encoding,$binder) = @_;

   my $ua = new vdcLWP;
   my ($GZ, $STREAMOUT);
   $ua->proxy('http', $HTTP_PROXY);
   $ua->proxy('ftp', $FTP_PROXY);

   my ($is_response_header_written);	
   my ($use_tar);	
   my ($use_compression);	

   if ( ($binder && $binder eq "tar") || ($use_gz_encoding && !$USE_MOD_DEFLATE) ) {
	$use_compression =1;
   }

   if ($binder && $binder eq "tar" ) {
	$use_tar=1;
   }

   binmode (STDOUT);
   if ($use_compression) {
        if (!open($GZ,"| gzip -c")) {  
		$use_compression=undef;
   		binmode ($GZ);
	}
	$STREAMOUT=$GZ;	
   } else {
	$STREAMOUT=*STDOUT;
   } 

   my $http_header_is_written=undef;
   my $failures=0;

   for my $u (@{$urls}) {

	# setup request and  cookie
    	my $req = HTTP::Request->new('GET',$u);
	_cookieSet($ua,$u,$q,$req);

	# setup tempfile
#	truncate($tmpfh,0);	

	unlink $tmpfilename; 
	($tmpfh, $tmpfilename) = tempfile( DIR => $MYTMPDIR);


	# if content->length not supplied and using tar
	# save to a temp file, otherwise stream out
	my $tar_header_written=undef;
	my $content_written=undef;
	my $fname=undef;
	my $bytes_read=0;
	my $flength=0;
        my $res = $ua->request( $req,
           sub {
 		my($chunk, $res_i) = @_;
		$bytes_read+= length($chunk);
		if (!$fname) {
			$fname = assignName($u,$res_i);
		}
 		if (!$http_header_is_written) {

			# IE PDF BUG Workaround
			# PDF plugin doesn't handle gzip compression
			# only needed if not using MOD_DEFLATE
			if ( ((!$binder) || ($binder ne "tar"))  && 
				 ($res_i->header('content_type')=~/application\/pdf/) 
				&& $use_compression ) {
				$STREAMOUT=*STDOUT;
				$use_compression = undef;
			}
			if ( (!$binder) || ($binder ne "tar") ||  (@{$urls}==0 && (!$name))) {
				$name = $fname;
			} 
			
			# note: must use STDOUT so header is not compressed
			print STDOUT writeHttpHeader($binder,$use_compression,$use_gz_encoding,
				 $name, $res_i->header('content_type'));

			$http_header_is_written=1;
                }
		if ($use_tar && !$tar_header_written && $res_i->header('content_length')) {
			$flength=$res_i->header('content_length');
			print {$STREAMOUT} file_header("$fname",$flength);
			$tar_header_written=1;
			$content_written=1;
		}
		if ($use_tar && !$tar_header_written) {
			print $tmpfh $chunk;
		} else {
               		print {$STREAMOUT} $chunk;
		}
            } # End of sub
	); # End of $ua->request
	

	# Finishing stage:
	#
	# - Check length
	# - Check for failure and create dummy file if necessary 
	# - Write http header if not written
	# - Write tar header if not written
	# - Write content if not written
	

	# if no content-length header
	if ($flength == 0) {
		$flength=$bytes_read;
	}

	# If download fails, you may be in the middle of
	# of a tar. So can't abort, or tar file will simply be
	# corrupted. Generate a failure message text file intead

        if (!$res->is_success) {
		$fname="ERROR_$failures.txt";
		my $content="Sorry, this portion of the download failed.\n" .
			"URL: $u\n" .
			"Reason:" . $res->status_line . "\n" .
			"Body content:\n----------\n\n\n" . $res->content;
		$fname="ERROR_$failures.txt";
		$failures++;
		my $curpos=tell($tmpfh);
		print $tmpfh $content ;
		$flength=tell($tmpfh)-$curpos;
		
		# do we need a header?
		if (!$http_header_is_written) {
			$http_header_is_written=1;
			if ($binder ne "tar")  {
				$name = $fname;
			} 
			# note: must use STDOUT so header is not compressed
			print STDOUT writeHttpHeader($binder,$use_compression,$use_gz_encoding,
				 $name, "text/plain");
		}
	}

	if ($use_tar && !$tar_header_written) {
		print {$STREAMOUT} file_header("$fname",$flength);
		$tar_header_written=1;
	}

	if (!$content_written) {
  	   seek($tmpfh,0,0);
	   my $buffer="";
	   while (sysread $tmpfh, $buffer, 4096) {
		print {$STREAMOUT} $buffer;
    	   }
	   $content_written=1;
	}	

	if ($use_tar) {
		print {$STREAMOUT} end_file($flength);
	}
	select STDOUT;
   }  # end URL processing loop

   # close archive
   if ($use_tar) {
	print {$STREAMOUT} end_archive();
   }
   close($STREAMOUT);
   if ($GZ) {
	close($GZ)
   }
}

# writeHttpHeader
#
# The details of the header depend on finder, compression, and whether
# encoding is permitted
#
# binder = whether tar is being used, eq 'NONE' if no binder
# use_compression
# gz
# name
# type

sub writeHttpHeader {
	my ($binder, $use_compression, $use_gz_encoding, $name, $type) = @_;

	$name=~ tr/[A-Z][a-z][0-9]\._//dc; 
	if ($name eq "") {	
		$name=$DEFAULTNAME;	
	}
	my (%headers);

	$headers{"status"}="200 OK";
	if ((!$binder) || $binder ne "tar") {
		if ($use_compression && !$use_gz_encoding) {
			$headers{'type'}= 'application/x-gzip' . 
				'; name=' . '"' .  $name . '.gz' . '"';
			$headers{'Content_disposition'}='attachment; filename="' 
				. $name . '.gz' . '"';
		} else {
			if ($use_compression) {
				$headers{'Content_encoding'}='gzip';
				$headers{'Vary'}='Accept-Encoding';
			}
			$headers{'type'}=$type . '; name=' . 
				'"' . $name .'"';
			if ($binder && $binder eq "attach") {
				$headers{'Content_disposition'}='attachment; filename="' 
				. $name . '"';
			} else {
				$headers{'Content_disposition'}='inline; filename="' 
				. $name . '"';
			}
		}
	} else {
		if ($use_compression && !$use_gz_encoding) {
			$headers{'type'}= 'application/x-gzip' . 
				'; name=' . '"' .  $name . '.tar.gz' . '"';
			$headers{'Content_disposition'}='attachment; filename="' 
				. $name . '.tar.gz' . '"';
		} else {
			if ($use_compression) {
				$headers{'Content_encoding'}='gzip';
				$headers{'Vary'}='Accept-Encoding';
			}
			$headers{'type'}= 'application/x-tar' . 
				'; name=' . '"' .  $name . '.tar' . '"';
			$headers{'Content_disposition'}='attachment; filename="' 
				. $name . '.tar' . '"';
		}
	}
	return(vdcCGI::header(\%headers));	
}

END {
   if ($tmpfilename) { unlink "$tmpfilename"; }
}


# TAR HANDLING CODE
#
# Derived from Archive::Tar 
# (Copyright 1997 Calle Dybedahl. All rights reserved.  Copyright 1998 Stephen Zander. All rights reserved.)
#
# This library is free software; you can redistribute it and/or modify
# it under the the GPL.


# Check for get* (they don't exist on WinNT)
my $fake_getpwuid;
$fake_getpwuid = "unknown"
    unless eval { $_ = getpwuid (0); }; # Pointless assigment to make -w shut up

my $fake_getgrgid;
$fake_getgrgid = "unknown"
    unless eval { $_ = getgrgid (0); }; # Pointless assigment to make -w shut up

my $tar_pack_header
    = 'a100 a8 a8 a8 a12 a12 A8 a1 a100 a6 a2 a32 a32 a8 a8 a155 x12',

my $time_offset = ($^O eq "MacOS") ? Time::Local::timelocal(0,0,0,1,0,70) : 0;

## Subroutines to return type constants
sub FILE() { return 0; }
sub HARDLINK() { return 1; }
sub SYMLINK() { return 2; }
sub CHARDEV() { return 3; }
sub BLOCKDEV() { return 4; }
sub DIR() { return 5; }
sub FIFO() { return 6; }
sub SOCKET() { return 8; }
sub UNKNOWN() { return 9; }

sub _format_tar_entry {
    my ($ref) = shift;
    my ($tmp,$file,$prefix,$pos);

    $file = $ref->{name};
    if (length ($file) > 99) {
	$pos = index $file, "/", (length ($file) - 100);
	next
	    if $pos == -1;	# Filename longer than 100 chars!

	$prefix = substr $file,0,$pos;
	$file = substr $file,$pos+1;
	substr ($prefix, 0, -155) = ""
	    if length($prefix)>154;
    }
    else {
	$prefix="";
    }

    $tmp = pack ($tar_pack_header,
		 $file,
		 sprintf("%06o ",$ref->{mode}),
		 sprintf("%06o ",$ref->{uid}),
		 sprintf("%06o ",$ref->{gid}),
		 sprintf("%11o ",$ref->{size}),
		 sprintf("%11o ",$ref->{mtime}),
		 "",		#checksum field - space padded by pack("A8")
		 $ref->{type},
		 $ref->{linkname},
		 $ref->{magic},
		 $ref->{version} || '00',
		 $ref->{uname},
		 $ref->{gname},
		 sprintf("%6o ",$ref->{devmajor}),
		 sprintf("%6o ",$ref->{devminor}),
		 $prefix);
    substr($tmp,148,7) = sprintf("%6o\0", unpack("%16C*",$tmp));

    return $tmp;
}

sub end_file {
   my ($size)=@_ ;
   my $tmp;
   $tmp = "\0" x (512 - ($size & 0x1ff)) if ($size & 0x1ff) ;
   return $tmp;
}

sub end_archive {
    return("\0" x 1024);
}

sub file_header {
    my ($file, $size, $opt) = @_;
    my $ref = {};
    my ($key);

    if($^O eq "MacOS") {
	$file = _munge_file($file);
    }
    $ref->{name} = $file;
    $ref->{mode} = 0666 & (0777 - umask);
    $ref->{uid} = $>;
    $ref->{gid} = (split(/ /,$)))[0]; # Yuck
    $ref->{size} = $size;
    $ref->{mtime} = ((time - $time_offset) | 0),
    $ref->{chksum} = "      ";	# Utterly pointless
    $ref->{type} = FILE();		# Ordinary file
    $ref->{linkname} = "";
    $ref->{magic} = "ustar";
    $ref->{version} = "00";
    # WinNT protection
    $ref->{uname} = $fake_getpwuid || getpwuid ($>);
    $ref->{gname} = $fake_getgrgid || getgrgid ($ref->{gid});
    $ref->{devmajor} = 0;
    $ref->{devminor} = 0;
    $ref->{prefix} = "";

    if ($opt) {
	foreach $key (keys %$opt) {
	    $ref->{$key} = $opt->{$key}
	}
    }

    return _format_tar_entry($ref);
}

dobind();
1; 
