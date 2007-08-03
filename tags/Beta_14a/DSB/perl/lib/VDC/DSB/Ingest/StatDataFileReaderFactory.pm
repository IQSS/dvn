package VDC::DSB::Ingest::StatDataFileReaderFactory;

# Copyright (C) 2004 President and Fellows of Harvard University
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
#

use strict;

#use warnings;
use HTML::Entities;
use IO::File;
use File::Copy;
use Data::Dumper;
use VDC::DSB::Ingest::StatData;

# package variables

my $TMPDIR=File::Spec->tmpdir();
my $readableFileTypes ={
	"application/x-stata"    => "DTA",
	"application/x-spss-sav" => "SAV",
	"application/x-spss-por" => "POR",
};

my $classBase   ='VDC::DSB::Ingest::';
my $locationBase="VDC/DSB/Ingest/";

sub instantiate {
	my ($class, %args) =@_;
	my $dfhdl = new IO::File;;
	my ($fileType, $fileTypeCode);
	
	unless(exists($args{SOURCEFILE}) && ($args{SOURCEFILE})){
		print STDERR "argument SOURCEFILE is missing!\n";
		return undef;
	} elsif (exists($args{SPSFILE}) && ($args{SPSFILE})){
		# SPSS sps syntax file case
		$fileType="SPS";
	} else {
		# other data files
		if ($dfhdl->open("< $args{SOURCEFILE}")){
			# check file type
			$fileTypeCode=&checkReadableFileType($dfhdl);
			if ( ($fileTypeCode) && exists($readableFileTypes->{$fileTypeCode})){
				$fileType=$readableFileTypes->{$fileTypeCode};
			} else{
				print STDERR "unknown, unreadable file Type!\n";
				return undef;
			}
		} else {
			print STDERR "source data file cannot be opened!\n";
			return undef;
		}
		
	}
	# call a reader class according to the type of a data file
	my $className = $classBase    . $fileType;
	my $location  = $locationBase . $fileType . '.pm';
	require $location;
	return $className->new(%args);
}

sub checkReadableFileType {
	my $dfname = shift;
	binmode($dfname, ":raw");
	my $DEBUG;
	# sas transport file
	my $filesignature = 91;
	my $buff;
	read($dfname, $buff, $filesignature);

	my $template = "a80a11";
	my ($frst80, $next11) = unpack($template, $buff);
	if ($DEBUG){
		#print STDERR "first 80 bytes:",$frst80,"\n";
		#print STDERR "next  11 bytes:",$next11,"\n";
	}
	if ( ($frst80 eq 'HEADER RECORD*******LIBRARY HEADER RECORD!!!!!!!000000000000000000000000000000  ') 
		&& ($next11 eq 'SAS     SAS')) {
		print STDERR "$dfname is a sas export file\n" if $DEBUG;
		#return "application/x-sas-xport";
		return undef; # should not be returning subsettable if no input script
	} else {
		print STDERR "$dfname is NOT a sas export file\n" if $DEBUG;
	}

	# return to the top of the file
 	seek $dfname, 0, 0;

	# stata and sav
	$filesignature = 4;
	read($dfname, $buff, $filesignature);
	# check the file against dta

	$template = "c4";
	my @first4b1 = unpack($template, $buff);
	my @frst4b = unpack("H8", $buff);
	if ($DEBUG){
		#print STDERR "contents(4 byte hex dump)=",join("-", chr(@frst4b)),"\n";
		print STDERR "contents(1st byte: int)=",($first4b1[0]),"\n";
		print STDERR "contents(2nd byte: int)=",($first4b1[1]),"\n";
		print STDERR "contents(3rd byte: int)=",($first4b1[2]),"\n";
		print STDERR "contents(4th byte: int)=",($first4b1[3]),"\n";
	}
	my $dtarelno = { 
		104 =>  'rel_3',    105 => 'rel_4or5', 108 => 'rel_6', 110 => 'rel_7first',
		111 => 'rel_7scnd', 113 => 'rel_8or9', 114 => 'rel_10',
	}; 
	 if ($first4b1[2] != 1) {
		print STDERR "$dfname is not sata type(failed: 1st step)\n" if $DEBUG;
	 } elsif ($first4b1[1] != 1 && $first4b1[1] != 2) {
		print STDERR "$dfname is not stata type(failed: 2nd step)\n" if $DEBUG;
	 } elsif (!exists($dtarelno->{"$first4b1[0]"})) {
		print STDERR "$dfname is neither stata-type nor one of the permissible release numbers(3rd step)\n" if $DEBUG; 
	} else {
		print STDERR "$dfname is a stata file: release number=",$dtarelno->{"$first4b1[0]"},"\n" if $DEBUG;
		return("application/x-stata");
	}

	# check the file against sav
	$template = "a4";
	my $first4b2 = unpack($template, $buff);

	print STDERR "The firt 4 byte[expected value=\$FL2]:",$first4b2,"\n" if $DEBUG;
	if ($first4b2 ne '$FL2'){
		print STDERR "$dfname is not an spss system (.sav) file or the character representation code of $buff might be non-7bit-ASCII\n" if $DEBUG;
	} else {
		print STDERR "$dfname is an sav file\n" if $DEBUG;
		return "application/x-spss-sav";
	}
	
	seek $dfname,  0, 0;
	binmode ($dfname);
	my $lines =[];
	my $line='';
	while (<$dfname>) {
	   tr/\015//d;
	   chomp $_;
	   $line .= $_;
	   push @{$lines}, length($_);
	   last if ($. == 6);
	}
	close ($dfname);

	my $bias =0;
	for (my $l=0;$l<=4;$l++){
		$bias += ($lines->[$l] - 80);
	}
	my $bias1st = $lines->[0]-80;
		
	if (uc(substr($line, (456+$bias), 8)) eq "SPSSPORT") {
		print STDERR "$dfname is a por file(step1)\n" if $DEBUG;
		return "application/x-spss-por";
	} elsif ( (uc(substr($line, (40+$bias1st), 20)) eq "ASCII SPSS PORT FILE") && ($line->[5] =~ /SPSSPORT/i) )  {
		print STDERR "$dfname is a por file(step2)\n" if $DEBUG;
		return("application/x-spss-por");
	}
	return undef;
}


1;


__END__

=head1 NAME

VDC::DSB::Ingest::StatDataFileReaderFactory - Factory for reading a statistical data file

=head1 SYNOPSIS

    use VDC::DSB::Ingest::StatDataFileReaderFactory;
    
    my $sourcefile='data_file.dat';
    my $fileid    ='file1';
    my $filenmbr  =1;
    my $FH  = new IO::File("> log_file.log"); # or *STDOUT;

    # SPSS Data Definition (Syntax: sps) file case
    my $spsfile = 'some_sps_file.sps';
    my $metadata = VDC::DSB::Ingest::StatDataFileReaderFactory->instantiate(SOURCEFILE=>$sourcefile,FILEID=>$fileid,FILENO=>$filenmbr,LOGFILE=>$FH,SPSFILE=>$spsfile ICPSR=>1);

    # SPSS POR, or SPSS SAV or Stata file case
    my $metadata = VDC::DSB::Ingest::StatDataFileReaderFactory->instantiate(SOURCEFILE=>$sourcefile,FILEID=>$fileid,FILENO=>$filenmbr,LOGFILE=>$FH);



=head1 DESCRIPTION

VDC::DSB::Ingest::StatDataFileReaderFactory is a factory class that set up an appropriate file-reading module according to the type of a given source (data) file.  This class has a method that checks whether a given data file can be read; Currently this class can read four file-formats: SPSS (sps, sav, and por) and Stata (dta).



=head1 USAGE

=head2 The Constructor

=over 4

=item instantiate()

invokes an appropriate file-reader if a data file is found readable.

    

=over 4

=item SOURCEFILE

data file to be read

=item FILEID

file id assigned to a data file

=item FILENO

is the order number of a data file within a set of data files to be read. Starts from 1.

=item LOGFILE

file handle of a log file

=item SPSFILE [applicable only for an SPSS syntax file case]

SPSS syntax (data defintion) file to be parsed 

=item ICPSR [applicable only for an SPSS syntax file case]

an option for an ICPSR-prepared SPSS syntax file; value '1' activates this option. 

=back


=back




=head1 AUTHOR

Akio Sone, Virtual Data Center Project, Harvard University

(<URL:http://thedata.org/>)

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2004 President and Fellows of Harvard University

GNU General Public License

=cut
