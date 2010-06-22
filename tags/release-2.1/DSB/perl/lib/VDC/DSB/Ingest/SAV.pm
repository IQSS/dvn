package VDC::DSB::Ingest::SAV;

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
@VDC::DSB::Ingest::SAV::ISA= qw(VDC::DSB::Ingest::StatData);
use IO::File;
use File::Copy;
use Data::Dumper;

# package variables
my $TMPDIR=File::Spec->tmpdir();

my $INT4 = 4;
my $OBS  = 8;
my $MVstring="NA";

my $SAV_FRMT_CODE_TABLE={
'0'=>'CONTINUE','1'=>'A','2'=>'AHEX','3'=>'COMMA','4'=>'DOLLAR','5'=>'F',
'6'=>'IB','7'=>'PIBHEX','8'=>'P','9'=>'PIB','10'=>'PK',
'11'=>'RB','12'=>'RBHEX','15'=>'Z','16'=>'N','17'=>'E','20'=>'DATE',
'21'=>'TIME','22'=>'DATETIME','23'=>'ADATE','24'=>'JDATE','25'=>'DTIME',
'26'=>'WKDAY','27'=>'MONTH','28'=>'MOYR','29'=>'QYR','30'=>'WKYR',
'31'=>'PCT','32'=>'DOT','33'=>'CCA','34'=>'CCB','35'=>'CCC',
'36'=>'CCD','37'=>'CCE','38'=>'EDATE','39'=>'SDATE',};

my $SPSS_FRMT_CATGRY_TABLE={
'CONTINUE'=>'other','A'=>'other','AHEX'=>'other','COMMA'=>'other',
'DOLLAR'=>'currency','F'=>'other','IB'=>'other','PIBHEX'=>'other',
'P'=>'other','PIB'=>'other','PK'=>'other','RB'=>'other','RBHEX'=>'other',
'Z'=>'other','N'=>'other','E'=>'other','DATE'=>'date','TIME'=>'time',
'DATETIME'=>'time','ADATE'=>'date','JDATE'=>'date','DTIME'=>'time',
'WKDAY'=>'other','MONTH'=>'other','MOYR'=>'date','QYR'=>'date',
'WKYR'=>'date','PCT'=>'other','DOT'=>'other','CCA'=>'currency',
'CCB'=>'currency','CCC'=>'currency','CCD'=>'currency','CCE'=>'currency',
'EDATE'=>'date','SDATE'=>'date'};
# note: 

# MONTH and WKDAY are treated as string-type
my @WEEKDAY = qw(Sunday Monday Tuesday Wednesday Thursday Friday Saturday);
my @WEEKDAYs = qw(Sun Mon Tue Wed Thu Fri Sat);

my @MONTH   = qw(January February March April May June July August September October November December);
my @MONTHs  = qw(Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec);


sub _init {
	my $self=shift;
	my $args = {@_};
	my $DEBUG;
	my $FH;
	
	
	unless (exists($args->{LOGFILE})){
		my $logfile = $TMPDIR  . '/sav.' . $$ . '.log';
		$FH = new IO::File("> $logfile");
		$self->{_prcssOptn}->{LOGFILE} =$FH;
		print $FH "default log file name will be used:", $logfile, "\n" if $DEBUG;
	} else{
		$self->{_prcssOptn}->{LOGFILE}= $FH = $args->{LOGFILE};
		print $FH "user-supplied log file name will be used:\n" if $DEBUG;
	}
	
	print $FH "args:\n", Dumper($args) if $DEBUG ;
	
	
	my $datafile;
	my $base;

	unless (exists($args->{SOURCEFILE})){
		die "method parse_file [SAV.pm] needs at least an SAV file name in its argument list!";
	} else {
		$self->{_prcssOptn}->{SOURCEFILE} = $datafile= $args->{SOURCEFILE};
		$base=$datafile;
		print $FH "sav file name: $datafile\n" if $DEBUG;
	}
	my $tabfile;
	unless (exists($args->{DLMTDDATA})){
		$base =~s/\.([^\.]*)$//;
		$self->{_prcssOptn}->{DLMTDDATA}=$tabfile = $base . ".$$.tab";
		print $FH "package-generated tab-file name will be used: $tabfile\n" if $DEBUG;
	} else {
		$self->{_prcssOptn}->{DLMTDDATA}=$tabfile  = $args->{DLMTDDATA};
		print $FH "user-supplied tab-file name will be used: $tabfile\n" if $DEBUG;
	}
	my $fileid;
	unless (exists($args->{FILEID})){
		$self->{_fileDscr}->{fileID}=$fileid = 'file1';
		print $FH "package-generated fileid will be used: $fileid\n" if $DEBUG;
	} else{
		$self->{_fileDscr}->{fileID}=$fileid   = $args->{FILEID};
		print $FH "user-supplied fileid will be used: $fileid\n" if $DEBUG;
	}
	my $fileno;
	unless (exists($args->{FILENO})){
		$self->{_prcssOptn}->{FILENO}=$fileno = 1;
		print $FH "default filenumber 1 will be used: $fileno\n" if $DEBUG;
	} else{
		$self->{_prcssOptn}->{FILENO}=$fileno   = $args->{FILENO};
		print $FH "user-supplied filenumber will be used: $fileno\n" if $DEBUG;
	}
	
	$self->{_fileDscr}->{fileType} = 'application/x-spss-sav';
	$self->{_noCharVarCntn}=0;
	if (exists($args->{MVstring})){
		$MVstring = $args->{MVstring};
	}
	$self->{MVstring}= $MVstring;
	
}


sub parse_file {
	my $self=shift;
	my $args = {@_};
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};

	my $tabfile  = $self->{_prcssOptn}->{DLMTDDATA};
	my $base=$tabfile;
	$base =~ s/\.($$)\.tab$//;
	my $datafile = $self->{_prcssOptn}->{SOURCEFILE};

	my $recordType2Method ={
		1=>'RT1', 2=>'RT2', 3=>'RT3and4',
		6=>'RT6', 7=>'RT7', 999=>'RTdata'
	};
	
	my $tfh = new IO::File("> $tabfile");
	my ($optn, $reverseBtyeOrder);
	my $io = new IO::File;
	$self->{RFL}=$io;
	$self->{TFL}=$tfh;
	if ($io->open("< $datafile")){
		my ($className,$sav_field, $rtrn_obj,$location, $method4field );
		my $recordType;
		# step 1: parse the pre-processed por file before field '1'
		binmode($io);
		$recordType=0;
		my ($buff, $codeTemplate, $pstn, $fileInfo);
		$codeTemplate="Z4";
		while(1) {
			
			read($io, $buff,$INT4);
			print $FH "codeTemplate=",$codeTemplate,"\n" if $DEBUG;
			$recordType = unpack($codeTemplate, $buff);
			
			print $FH "recordType=",$recordType,"\n" if $DEBUG;
			if ($recordType eq '$FL2'){
				$recordType = 1;
			}
			
			$pstn = tell($io);
			print $FH "pstn=", $pstn,"\n" if $DEBUG;
			seek($io,-4, 1);
			$pstn = tell($io);
			print $FH "pstn_2=", $pstn,"\n" if $DEBUG;
			
			print $FH "recordType=",$recordType,"\trecordType2Method=",$recordType2Method->{$recordType},"\n" if $DEBUG;
			$method4field = "read_" . $recordType2Method->{$recordType};
			$self->$method4field();

			print $FH "object dump[after $recordType ]:\n",Dumper($self),"\n" if $DEBUG;
			
			if ($recordType == 1){
				# pass the unpacking code for the following stages
				# $reverseBtyeOrder = $self->{_fileDscr}->{reverseBtyeOrder};
				$codeTemplate     = $self->{_fileDscr}->{OBStemplate};
			} elsif ($recordType == 999) {
				print $FH "object(after 999)\n",Dumper($self),"\n" if $DEBUG;
				last;
			}
		}
	} else {
		die "Failed to open the sav file (",$datafile,"): wrong filename or pathname?\n";
	}
	$tfh->close();
	copy($tabfile,"$base.tab");	
	$io->close();
	print $FH "self(final:sav):\n",Dumper($self),"\n" if $DEBUG;
}

sub DESTROY{
	my $self =shift;
	unlink($self->{_prcssOptn}->{SMSTTFILE});
}


# /////////////////////////////////////////////////////////
# Method for each field
# /////////////////////////////////////////////////////////


sub read_RT1 {
	my $self=shift;
	my $rfh  = $self->{RFL};
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	
	my @RT1blcks = (64, 4, 12, 4, 8, 84);
	my @tmpltRT1 = ("Z4Z60", "L", "", "l", "d", "Z9Z8Z64Z3");
	my $TMPDIR='/tmp';
	my ($bytrvrs, $fbytordr, $nOBS, $ncases, $int16, $int32, $cmprss );

	my ($buff, $rbuff);
	my ($cmprssbias, $flDate, $flTime, $fllbl, $padding);
	my ($wghtVarIndx);

	my $OSendian= &VDC::DSB::Ingest::StatData::getOSbyteOrder();
	
	print $FH "platform endian=",$OSendian,"\n" if $DEBUG;
	$self->{_fileDscr}->{OSendian}=$OSendian;
	
	# 1.1 Read Record Type 1 
	# read the first 64 bytes
	read($rfh, $buff, $RT1blcks[0]);
	my ($rcrdtyp1, $prdctnm) = unpack($tmpltRT1[0], $buff);
	my $releaseNo;
	if ($prdctnm =~ m/\s+Release\s+((\d+)\.\d+\.\d+)\s*/ig){
		print $FH "release no:=", $1, "\n";
		print $FH "release no full=", $2, "\n";
		$releaseNo = $2;
	}
		print $FH "The recordtype code #1(expected value:\$FL2):$rcrdtyp1\n" if $DEBUG;
		print $FH "The Product name:$prdctnm\n\n" if $DEBUG;
	if ($rcrdtyp1 ne '$FL2'){
		die "This source file is not an spss system (.sav) file or its character representation code might be non-7bit-ASCII:$!";
	}
	
	# read the second 4 bytes (File layout: if the platform and the sav file are the same eindian, $bytordr = 2)
	read($rfh, $buff, $RT1blcks[1]);
		print $FH "buff in Hex:", unpack("H*", $buff), "\n" if $DEBUG;
		print $FH "buff in hex:", unpack("h*", $buff), "\n" if $DEBUG;
	$rbuff = reverse($buff);
		print $FH "rbuff in Hex:", unpack("H*", $rbuff), "\n" if $DEBUG;
		print $FH "rbuff in hex:", unpack("h*", $rbuff), "\n" if $DEBUG;
		
	my $bytordr  = unpack($tmpltRT1[1], $buff);
	my $rbytordr = unpack($tmpltRT1[1], $rbuff);
		print $FH " bytordr:",$bytordr, "\n" if $DEBUG;
		print $FH "rbytordr:",$rbytordr, "\n" if $DEBUG;
	my $dataEntrySection=0;
	if (($bytordr == 2) || ($bytordr == 3)) {
		# byte-orders are the same: no byte-reversal is necessary; use natives
		print $FH "File Layout:$bytordr\tuse s/l template characters\n" if $DEBUG;
		# 16 bit integer
		$int16 = "s";
		# 32 bit integer
		$int32 = "l";
		$bytrvrs = 0;
		$fbytordr = $OSendian;
		if ($bytordr == 3){
			$dataEntrySection=1
		}
	} elsif (($rbytordr == 2)||($rbytordr == 3)) {
		# byteorders are different: byte-reveral may be necessary
		$bytrvrs = 1;
		if ($OSendian == 2) {
			print $FH "File Layout is big endian\nuse n/N template characters\n" if $DEBUG;
			#big endian
			# 16 bit integer
			$int16 = "n";
			# 32 bit integer
			$int32 = "N";
			$fbytordr = 1;
		} elsif ($OSendian == 1) {
			print $FH "File Layout is little endian\nuse v/V template characters\n" if $DEBUG;
			#little endian 
			# 16 bit integer
			$int16 = "v";
			# 32 bit integer
			$int32 = "V";
			$fbytordr = 2;
		}
		if ($rbytordr == 3){
			$dataEntrySection=1
		}
	} else {
		die "The byte order of this sav file is neither big or little endian.\nThis script cannot handle such a case.";
		
	} 
	print $FH "\nbyte-reversal(bytrvrs):",$bytrvrs,"\n" if $DEBUG;
	print $FH "file byte-order(fbytordr):",$fbytordr,"\n\n" if $DEBUG;
		
	$self->{_fileDscr}->{dataEntrySection}=$dataEntrySection;	
	$self->{_fileDscr}->{reverseBtyeOrder}=$bytrvrs;
	$self->{_fileDscr}->{fileBtyeOrder}=$fbytordr;
	$self->{_fileDscr}->{OBStemplate}=$int32;
	$self->{_fileDscr}->{releaseNo}=$releaseNo+0;;

	# Read the remaining fields
	
	read($rfh, $buff, $RT1blcks[2]);
	$tmpltRT1[2]="${int32}${int32}${int32}";
	($nOBS, $cmprss, $wghtVarIndx) = unpack($tmpltRT1[2], $buff);
	$self->{_fileDscr}->{nOBS}=$nOBS;
	$self->{_fileDscr}->{Compression}=$cmprss;
	$self->{_fileDscr}->{wghtVarIndx}=$wghtVarIndx;
		
	read($rfh, $buff, $RT1blcks[3]);
	if ($bytrvrs) {
		$ncases = unpack($tmpltRT1[3], reverse($buff));
	} else {
		$ncases = unpack($tmpltRT1[3], ($buff));
	}
	$self->{_fileDscr}->{caseQnty}=$ncases;
		
	read($rfh, $buff, $RT1blcks[4]);
	if ($bytrvrs) {
		$cmprssbias = unpack($tmpltRT1[4], reverse($buff));
	} else {
		$cmprssbias = unpack($tmpltRT1[4], $buff);
	}
	$self->{_fileDscr}->{compressBias}=$cmprssbias;
	
	
	read($rfh, $buff, $RT1blcks[5]);
	($flDate, $flTime, $fllbl, $padding) = unpack($tmpltRT1[5], $buff);
	if ($cmprssbias != 100){
		die "The compression bias ($cmprssbias) is not 100.\nThis package cannot handle such a case:$!";
	}
	if ($DEBUG) {
		print $FH "\nSAV Header (RT1) Summary\n";
		print $FH "templates for unpacking:",join('|',@tmpltRT1),"\n";
		print $FH "Product Name:",$prdctnm,"\n";
		print $FH "Release Number:",$releaseNo,"\n";
		print $FH "Data Entry Section:",$dataEntrySection,"\n"; 
		print $FH "N of OBS:", $nOBS, "\n";
		print $FH "Compression switch:", $cmprss, "\n";
		print $FH "case-weight index:", $wghtVarIndx, "\n";
		print $FH "N of cases:", $ncases,"\n";
		print $FH "compression bias:", $cmprssbias,"\n";
		
		
		print $FH "creation Date:", $flDate,"\n";
		print $FH "creation Time:", $flTime,"\n";
		print $FH "file Label:", $fllbl,"\n\n";
	}
}

sub read_RT2{
	my $self=shift;
	my $rfh  = $self->{RFL};
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	print $FH "within:RT2:fileInfo=\n",Dumper($self),"\n" if $DEBUG;
	my $nOBS             =$self->{_fileDscr}->{nOBS};
	my $int32            =$self->{_fileDscr}->{OBStemplate};
	my $OSendian         =$self->{_fileDscr}->{OSendian};
	my $reverseBtyeOrder =$self->{_fileDscr}->{reverseBtyeOrder};
	my $wghtVarIndx      =$self->{_fileDscr}->{wghtVarIndx};
	my $wghtVarNo;
	my $weightVar;
	# 1.2 Read Record Type 2
	my $buff;
	my ($lenvarlbl, $lenvarlblx, $ncase, @mssgval, @mssgvalx,$nomsvls);
	my (@RRT2F, $LWINF, $HIINF, $template02, $adjlenstrng);
	my $varordr = 0;
	#@tmplt02 = ("${int32}", "${int32}","${int32}", "${int32}","c4","c4","A8");
	my @tmplt02 = ("${int32}", "${int32}","${int32}", "${int32}","c4","c4");
	#my @tmplt02 = ("l", "l","l", "l","c4","c4");
	
	print  $FH "initial template before the byte-order check:", @tmplt02, "\n" if $DEBUG;
	my $template;
	
	if ($reverseBtyeOrder){
		#$template02 = join("", reverse(@tmplt02));
		$template02 = join("", @tmplt02);
		print  $FH "reversed template:", $template02, "\n" if $DEBUG;
	} else {
		$template02 = join("", @tmplt02);
		print  $FH "         template:", $template02, "\n" if $DEBUG;
	}

	my $varpwfrmt;
	for (my $i= 0;$i<$nOBS ;$i++) {
		# unpack the first 4*8(units) bytes
		#read($rfh, $buff, $INT4*8);
		read($rfh, $buff, $INT4*6);
		print $FH "\n\n$i-th OBS processing\n" if $DEBUG;
		#@RRT2F = unpack($template, $buff);
		if ($reverseBtyeOrder){
			#@RRT2F = reverse (unpack($template02, reverse($buff)));
			@RRT2F = unpack($template02, $buff);
		} else {
			@RRT2F = unpack($template02, $buff);
		}
		print $FH "$i -th RRT2F= ",join("|", @RRT2F), "\n" if $DEBUG;
		if ($RRT2F[0] != 2) {
			die"The record type code ($RRT2F[0]) is not 2.\n.";
		}
		read($rfh, $buff, $OBS);
		$template = "A8";
		$RRT2F[12] = unpack($template, $buff);
		print $FH "//////////////// varname: $RRT2F[12] ////////////////\n" if $DEBUG;
		if (($reverseBtyeOrder) && ($RRT2F[1] == 4294967295 )){
			print $FH "The code for a continuation of a string variable (-1) was found\n";
			# unpack function does not have a template for
			# signed long integers in big-endian order 
			$RRT2F[1] = -1;
		}	
		push @{$self->{_varTypeRaw}}, $RRT2F[1];
		# identify the variable type
		if ($RRT2F[1] >=0){
			# ///////////////////////////////
			# $i should be ($+1)?
			# ///////////////////////////////
			$self->{_varNameH}->{$RRT2F[12]} = $i ;
			
			
			$self->{_varType}->{$RRT2F[12]} = $RRT2F[1];
			if ($RRT2F[1]) {
				# 1st octet of a string variable
				print $FH "before adj length and remainder:",$RRT2F[1],"\t", ($RRT2F[1] % $OBS),"\n" if $DEBUG;
				if ($RRT2F[1] % $OBS){
					$adjlenstrng = $OBS*(int($RRT2F[1]/$OBS)+1);
				} elsif (!($RRT2F[1] % $OBS)) {
					$adjlenstrng = $RRT2F[1];
				}
				print $FH "after adj length:",$adjlenstrng,"\n" if $DEBUG;
				push @{$self->{_varTemplate}}, ("Z" . $adjlenstrng);
				$self->{_charVarTbl}->{$RRT2F[12]}=$varordr;
				($self->{_noCharVar})++;

				$adjlenstrng = '';
			} else {
				# numerica variable
				push @{$self->{_varTemplate}}, "d";
			}
			$varordr++;
			print $FH "variable counter:",$varordr,"\n" if $DEBUG;
			# set the id number of the case-weight variable
			if ($wghtVarIndx == ($i+1)) {
				$wghtVarNo = $varordr;
				$weightVar = $RRT2F[12];
			}
			# variable label handling
			if ($RRT2F[2]) {
				# read variable label length (4 bytes)
				read($rfh, $buff, $INT4);
				$template = "${int32}";
				$lenvarlbl= unpack($template, $buff);
				print $FH "The length of a variable label:",$lenvarlbl,"\n" if $DEBUG;
				if ($lenvarlbl > 256) {
					die "The length of a variable label($lenvarlbl) must be less than 257: $!";
				}
				print $FH "before adj length and remainder:", $lenvarlbl, "\t", ($lenvarlbl % $INT4), "\n" if $DEBUG;
				if ($lenvarlbl % $INT4){
					$lenvarlblx = 4*(int($lenvarlbl/4)+1);
				} else {
					$lenvarlblx = $lenvarlbl;
				}
				print $FH "after adj length:",$lenvarlblx,"\n" if $DEBUG;
				read($rfh, $buff, $lenvarlblx);
				$template = "A$lenvarlblx";
				## print $FH "template:",$template,"\n" if $DEBUG;
				my $varlbl= unpack($template, $buff);
				if (length($varlbl) > $lenvarlbl ) {
					$varlbl= substr($varlbl, 0, $lenvarlbl);
					print $FH $i, "-th variable lablel: adjusted\n" if $DEBUG;
				}
				if (($varlbl ne ' ') && ($varlbl ne '')){
					$self->{_varLabel}->{$RRT2F[12]}= $varlbl;
				}
				print $FH $varordr, "-th variable label:",$varlbl,"\n" if $DEBUG;
			}
			# missing values
			if ($RRT2F[3]){
				#print $FH "missing value code:", $RRT2F[3], "\n" if $DEBUG;
				$self->{_mvTpCdTbl}->{$RRT2F[12]}=$RRT2F[3];
				$self->{_mvVrMpTbl}->{$RRT2F[12]}=$RRT2F[12];
				$self->{_mssvlTbl}->{$RRT2F[12]}=[];
				if ($RRT2F[3] < 0) {
					$nomsvls = abs($RRT2F[3]);
				} else{
					$nomsvls =$RRT2F[3];
				}
				# read next $nomsvls units 
				if (!($RRT2F[1])) {
					# numeric variable case
					read($rfh, $buff, $OBS*$nomsvls);
					my $bufff = $buff;
					$template = "d$nomsvls";
					my $templatex = "H16";
					## print $FH "template: $template\n";
					if ($reverseBtyeOrder) {
						@mssgval = reverse (unpack($template, reverse($buff)));
						@mssgvalx = reverse (unpack($templatex x $nomsvls, reverse($bufff)));
					} else {
						@mssgval  = unpack($template, $buff);
						@mssgvalx  = unpack($templatex x $nomsvls, $bufff);
					}
					## print $FH "missing values:", join("|", @mssgval), "\n" if $DEBUG;
					if ($OSendian == 1) {
						$LWINF = 'ffeffffffffffffe';
						$HIINF = '7fefffffffffffff';
					} elsif ($OSendian == 2) {
						$LWINF = 'feffffffffffefff';
						$HIINF = 'ffffffffffffef7f';
					}
					if ( ($RRT2F[3] <=-2) && ($mssgvalx[0] eq $LWINF)) {
						## print $FH "LO\n" if $DEBUG;
						$mssgval[0]='LOWEST';
					}
					if ( ($RRT2F[3] <=-2) && ($mssgvalx[1] eq $HIINF)) {
						## print $FH "HI\n" if $DEBUG;
						$mssgval[1]='HIGHEST';
					}
					## print $FH "missing values:", join("|", @mssgvalx), "\n" if $DEBUG;
					## print $FH "missing values:", join("|", @mssgval), "\n" if $DEBUG;
					#push @{$self->{_mssvlTbl}->{$RRT2F[12]}}, [@mssgval];
					if ($RRT2F[3] >=1) {
						foreach my $msvel (@mssgval) {
							push @{$self->{_mssvlTbl}->{$RRT2F[12]}}, [$msvel];
						}
					} elsif ($RRT2F[3] == -2){
						push @{$self->{_mssvlTbl}->{$RRT2F[12]}}, [@mssgval];
					} elsif ($RRT2F[3] == -3){
						push @{$self->{_mssvlTbl}->{$RRT2F[12]}}, [$mssgval[0], $mssgval[1]];
						push @{$self->{_mssvlTbl}->{$RRT2F[12]}}, [$mssgval[2]] ;
					}
					@mssgvalx ='';
				} elsif ($RRT2F[1]>0) {
					# string variable case
					read($rfh, $buff, $OBS*$nomsvls);
					$template = "A$OBS";
					## print $FH "template:", $template, "\n" if $DEBUG;
					#print $FH "buffer:",$buff,"\n" if $DEBUG;
					if ($reverseBtyeOrder){
						@mssgval = reverse (unpack($template x $nomsvls , reverse($buff)));
					} else {
						@mssgval  = unpack($template x $nomsvls, $buff);
					}
					## print $FH "missing values:", join("|", @mssgval), "\n" if $DEBUG;
					foreach my $msvel (@mssgval) {
						push @{$self->{_mssvlTbl}->{$RRT2F[12]}}, [$msvel];
					}
				} else {
					die "Error: The range of the ${i}-th internal type code ($RRT2F[1]) is out of range [0, 256): $!";
				}
			} elsif (!($RRT2F[3])) {
				## print $FH "No missing value is specified for this OBS\n" if $DEBUG;
				@mssgval = ();
			} # end of mssing value processing
			my $pfrmtcd;
			
			my @wd=();
			if ((($reverseBtyeOrder==1)&&($OSendian==2))||(($reverseBtyeOrder==0)&&($OSendian==1))){
			
				#@pfrmtcd = reverse($RRT2F[4], $RRT2F[5], $RRT2F[6], $RRT2F[7]);
				#@pfrmtcd = reverse(@RRT2F[4..7]);
				#@wfrmtcd = reverse($RRT2F[8], $RRT2F[9], $RRT2F[10], $RRT2F[11]);
				#@wfrmtcd = reverse(@RRT2F[8..11]);
				#$varpwfrmt=join('|', (@pfrmtcd, @wfrmtcd));
				$varpwfrmt=join('|', ($RRT2F[7], $RRT2F[6], $RRT2F[5], $RRT2F[11], $RRT2F[10], $RRT2F[9], $RRT2F[8]));
				# format code
				$pfrmtcd=$RRT2F[5];
				$wd[0]=$RRT2F[6];
				$wd[1]=$RRT2F[7];
			} else {
				#@pfrmtcd = ($RRT2F[4], $RRT2F[5], $RRT2F[6], $RRT2F[7]);
				#@wfrmtcd = ($RRT2F[8], $RRT2F[9], $RRT2F[10], $RRT2F[11]);
				$varpwfrmt = join('|', @RRT2F[4..11]);
				# d-w-fmt-null
				# format code
				$pfrmtcd=$RRT2F[6];
				$wd[0]=$RRT2F[5];
				$wd[1]=$RRT2F[4];
			}

			# check print format code	
			if ( ($pfrmtcd != 1) && ($pfrmtcd != 5) ) {
			#	print STDERR "Warning!:\n\t$varordr-th variable has the format code ($pfrmtcd) that is neither 1(A:alphanumeric) nor 5(F[default numeric format]).\nCheck the codebook whether or not some special conversion is necessary for each case of this variable:$!"; 
				if ($pfrmtcd > 39){
					print STDERR "Warning!:\n\t$varordr-th variable has the format-code value ($pfrmtcd) that is out-of-range [1,39].\nCheck the codebook whether or not some special conversion is necessary for each case of this variable:$!"; 
				}
				#$self->{_varFormat}->{$RRT2F[12]}=$pfrmtcd;
			}
			if (exists($SAV_FRMT_CODE_TABLE->{ $pfrmtcd })){
				$self->{_varFormat}->{$RRT2F[12]}=$SAV_FRMT_CODE_TABLE->{ $pfrmtcd };
				
				if (($pfrmtcd == 0) || ($pfrmtcd == 1) || ($pfrmtcd == 5)){
				} else {
					my $d='';
					if ($wd[1]){
						$d='.' . $wd[1];
					}
					$self->{_formatName}->{$RRT2F[12]}= $SAV_FRMT_CODE_TABLE->{ $pfrmtcd } . $wd[0] . $d ;
				}
				
			}
			print $FH "varFrmt code =", $self->{_varFormat}->{$RRT2F[12]},"\n" if $DEBUG;
			print $FH "varpwfrmt=", $varpwfrmt,"\n" if $DEBUG;
			$self->{_varPWformat}->{$RRT2F[12]} = $varpwfrmt;
			push @{$self->{_varNameA}}, $RRT2F[12];
			$self->{_varNoRaw}->{$i+1} = $varordr;
			# numeric to string conversion for WKDAY and MONTH type
			if (($pfrmtcd eq '26')||($pfrmtcd eq '27')){
				# string-type => $vartype >0
				$self->{_charVarTbl}->{$RRT2F[12]}='a';
				($self->{_noCharVar})++;
			}

			
		} elsif ($RRT2F[1] == -1) {
			$self->{_noCharVarCntn}++;
			next;
		} else {
			die "Error: The range of the ${i}-th internal type code ($RRT2F[1]) is out of range [-1, 256): $!";
		}
		
	}
	$self->{_fileDscr}->{wghtVarNo}=$wghtVarNo;
	$self->{_fileDscr}->{varQnty}=$varordr;
	print $FH "Dump(RT2:last):\n",Dumper($self),"\n" if $DEBUG;
}

sub read_RT3and4 {
	my $self=shift;

	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	#print $FH "within:RT3/4:fileInfo=\n",Dumper($self),"\n" if $DEBUG;

	my $rfh  = $self->{RFL};
	my $int32            =$self->{_fileDscr}->{OBStemplate};
	my $reverseBtyeOrder =$self->{_fileDscr}->{reverseBtyeOrder};
	my $varType  = $self->{_varType}; # hash by name
	#my $varNameH = $self->{_varNameH};# hash by name
	my $varNameA = $self->{_varNameA};# array of names 
	my $varNoRaw = $self->{_varNoRaw};
	
	# each var's type   { $varNameA->[$i] }
	# %{$self->{_varNameHR}}=reverse %{$self->{_varNameH}};
	my $codeTemplate="${int32}";
	
	my ($buff, $lenvallbl, $lenvallblx);
	my @varindx;
	my $valVrMpTbl={};
	my $valLblTbl ={};
	
	# RT3/4 processing cycle
	while(1){
	
		# //////////////////////////////////////////////////
		# 1.3 Record Type (value/label set)
		# //////////////////////////////////////////////////
		
		read($rfh, $buff, $INT4);
		my $template = "${int32}";
		my $RTcode = unpack($template, $buff);
		print $FH "RT code(3)=",$RTcode,"\n" if $DEBUG;
		
		read($rfh, $buff,$INT4);
		$template = "${int32}";
		print $FH "template:$template\n" if $DEBUG;
		my $novallbl = unpack($template, $buff);
		print $FH "number of value labels(novallbl):", $novallbl, "\n" if $DEBUG;
		my $valLblTbli=[];

		for (my $i=0; $i<$novallbl ;$i++){
			## print $FH "i-label:",$i,"\n" if $DEBUG;
			my $valueset =[undef,undef,undef,undef];
			# read a value
			read($rfh, $buff, $OBS);
			# keep $buff intact until the type of a variable is found later
			#push @vallst, $buff;
			$valueset->[0]=$buff;

			# read the length of a value label 
			read($rfh, $buff, 1);
			$template = "C";
			$lenvallbl= unpack($template, $buff);
			print $FH "The length of a value label:",$lenvallbl,"\n" if $DEBUG;
			if ($lenvallbl > 255) {
				die "The length of a value label($lenvallbl) must be less than 256: $!";
			}

			print $FH "pre-adj length and remainder:",$lenvallbl,"\t", (($lenvallbl+1) % $OBS), "\n" if $DEBUG;

			if (($lenvallbl+1) % $OBS){
				$lenvallblx = 8*(int(($lenvallbl+1)/8)+1) -1;
			} else {
				$lenvallblx = $lenvallbl;
			}
			print $FH "post-adj length:", $lenvallblx,"\n" if $DEBUG;

			# read the value label
			read($rfh, $buff, $lenvallblx);
			$template = "A$lenvallblx"; # 'A' is used here because value label is padded by x20
			my $vallbl = unpack($template, $buff);
			print $FH $i,"-th value label:[",$vallbl,"]\n" if $DEBUG;
			if (length($vallbl) > $lenvallbl ) {
				$vallbl= substr($vallbl, 0, $lenvallbl);
				print $FH $i, "-th value label(adj):[",$vallbl,"]\n" if $DEBUG;
			}

			# external entity check
			$valueset->[1]=$vallbl;

			push @{$valLblTbli}, $valueset;
		}
		
		#print $FH "valLblTbli(raw):\n", Dumper($valLblTbli) if $DEBUG;

		# //////////////////////////////////////////////////
		# 1.4 Record Type 4 (Variable Index Record)
		# //////////////////////////////////////////////////
		
		read($rfh, $buff, $INT4);
		$template = "${int32}";
		my $rcrdtypcd = unpack($template, $buff);
		if ($rcrdtypcd == 4) {
			read($rfh, $buff, $INT4);
			$template = "${int32}";
			my $novars = unpack($template, $buff);
			## print $FH "\nnovars:",$novars,"\n" if $DEBUG;
			read($rfh, $buff, $INT4*$novars);
			$template = "${int32}";
			## print $FH "template:",$template,"\n" if $DEBUG;
			
			# varindx is based on segment (raw var) numbers [starts from 1] 
			@varindx = unpack($template x $novars, $buff);
			print $FH "varindx:", join('|', @varindx), "\n" if $DEBUG;
			print $FH "varindx[0]=",$varNoRaw->{"$varindx[0]"},"\n" if $DEBUG;
			my $keyVarName = $varNameA->[ ($varNoRaw->{$varindx[0]}-1) ] ;
			print $FH "varindx[0]: varName=",$keyVarName,"\n" if $DEBUG;
			# test the consistency of a variable type

			my $tmp1=0;
			# variable index in RT4 starts from 1 NOT ** 0 **
			# e.g., if $varindx[$i]==5, then it is 4-th in $varNameA 

			for (my $i=0; $i < $novars; $i++){
				## print $FH "index value($i):", $varindx[$i],"\t", $varindx[$i] = ($varindx[$i] - 1), "\n" if $DEBUG;
				#$valVrMpTbl->{ $varNameA->[   ($varNoRaw->{$varindx[$i]} -1)   ] }= $varNameA->[ ($varNoRaw->{$varindx[0]}-1) ] ;
				$valVrMpTbl->{ $varNameA->[   ($varNoRaw->{$varindx[$i]} -1)   ] }= $keyVarName ;
				
			}

			# unpack a value as float or string now
			## print $FH "index and variable type code(N=0;A>0):", $varindx[0], "\t", $vartyp[ $varindx[0] ],"\n" if $DEBUG;

			# the above consistency test is ok, the first element is used for the remainder

			if ($varType->{ $keyVarName } >0) {
				# string variable
				$template = "A8";
				for (my $i = 0; $i<@{$valLblTbli}; $i++){
					$valLblTbli->[$i]->[0] =  unpack ($template, $valLblTbli->[$i]->[0]);
					## print $FH "vallst[i]:",$vallst[$i],"\n" if $DEBUG;
				}
			} elsif ($varType->{ $keyVarName } == 0) {
				$template = "d";
				# numeric variable
				if ($reverseBtyeOrder) {
					for (my $i=0;$i<@{$valLblTbli};$i++) {
						$valLblTbli->[$i]->[0] = unpack($template, reverse($valLblTbli->[$i]->[0]));
						## print $FH "vallst[i]:",$vallst[$i],"\n" if $DEBUG;
					}
				} else {
					for (my $i = 0; $i<@{$valLblTbli}; $i++){
						$valLblTbli->[$i]->[0] = unpack ($template, $valLblTbli->[$i]->[0]);
						## print $FH "vallst[i]:",$vallst[$i],"\n" if $DEBUG;
					}
				}

			} else {
				# $vartyp[$varindx[0]]= -1:
				# do nothing
			} # end of upacking for values
					
			#print "key var:",$keyVarName,"\n" if $DEBUG;
			#print "valLblTbli\n", Dumper($valLblTbli),if $DEBUG;
			
			$valLblTbl->{$keyVarName} = $valLblTbli;

			# //////////////////////////////////////////
			# check next field Type
			# //////////////////////////////////////////
			
				my $escape=0;
				read($rfh, $buff,$INT4);
				#print $FH "codeTemplate=",$codeTemplate,"\n" if $DEBUG;
				my $recordType = unpack($codeTemplate, $buff);

				#print $FH "recordType=",$recordType,"\n" if $DEBUG;
				if ($recordType >= 6){
					$escape = 1;
				}

				my $pstn = tell($rfh);
				#print $FH "pstn: current=", $pstn,"\n" if $DEBUG;

				seek($rfh,-4, 1);
				$pstn = tell($rfh);
				#print $FH "pstn: after=", $pstn,"\n" if $DEBUG;
				if ($escape){last;}
			# //////////////////////////////////////////
			#print $FH "valLblTbl:\n", Dumper($valLblTbl), if $DEBUG;
			#print $FH "valVrMpTbl:\n", Dumper($valVrMpTbl), if $DEBUG;	
		} else {
			die "Record Type 4 must come after Record Type 3\n";
		} # end of types 3/4
		@varindx = ();

	} # end of while
	print $FH "valLblTbl:\n", Dumper($valLblTbl), if $DEBUG;
	print $FH "valVrMpTbl:\n", Dumper($valVrMpTbl), if $DEBUG;	
	$self->{_valVrMpTbl}=$valVrMpTbl;
	$self->{_valLblTbl} =$valLblTbl;
} # end of sub rcrdtyp34

sub read_RT6 {
	my $self=shift;
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	#print $FH "within:RT6:fileInfo=\n",Dumper($self),"\n" if $DEBUG;

	my $rfh              = $self->{RFL};
	my $int32            =$self->{_fileDscr}->{OBStemplate};
	my $reverseBtyeOrder =$self->{_fileDscr}->{reverseBtyeOrder};

	my ($buff);
		
	read($rfh, $buff, $INT4);
	my $template = "${int32}";
	my $RTcode = unpack($template, $buff);
	print $FH "RT code(6)=",$RTcode,"\n" if $DEBUG ;
	
	read($rfh, $buff,$INT4);
	$template = "${int32}";
	my $nolines = unpack($template, $buff);
	print $FH "nolines:", $nolines, "\n" if $DEBUG;
	$template = "Z80";
	my $docinfo='';
	for (my $i=0; $i<$nolines;$i++) {
		read($rfh, $buff, 80);
		$docinfo = unpack($template, $buff);
		print $FH $docinfo if $DEBUG;
		$docinfo =~ s/\s+$//;
		$self->{_fileDscr}->{document} .= $docinfo ;
		$docinfo='';
	}
	print $FH "RT6(last): document info:\n", $self->{_fileDscr}->{document}, "\n" if $DEBUG ;
	my $pstn = tell($rfh);
	#print $FH "pstn[RT6:end]=", $pstn,"\n" if $DEBUG ;

} # end of sub parseField

sub read_RT7 {
	my $self=shift;

	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	#print $FH "within:RT7:fileInfo=\n",Dumper($self),"\n" if $DEBUG;

	my $rfh  = $self->{RFL};
	my $int32            =$self->{_fileDscr}->{OBStemplate};
	my $codeTemplate="${int32}";

	my $reverseBtyeOrder = $self->{_fileDscr}->{reverseBtyeOrder};
	my $fileBtyeOrder    = $self->{_fileDscr}->{fileBtyeOrder};
	my $varNameA         = $self->{_varNameA};# array of names 
	
	my $RMSINTinfo={};
	my $RMSOBSinfo={};
	my $Measurement={};
	my $varShrtLngMpTbl={};
	my (@mchninf, @msrmntlvl);
	
	my ($buff, $template, $RTcode);

	while(1){
		#read($rfh, $buff, $INT4);
		#$template = "${int32}";
		#$RTcode = unpack($template, $buff);
		#print $FH "RT code(7)=",$RTcode,"\n";

		read($rfh, $buff,$INT4*4);
		$template = "${int32}4";
		## print $FH "template:$template\n";

		my ($RTcode, $sbtypcd, $dttypcd, $lenfllw) = unpack($template, $buff);
		print $FH "\n(SubtypeNo, bytes per unit, Units)=($sbtypcd, $dttypcd, $lenfllw)\n" if $DEBUG;

		if ( ($sbtypcd == 3) && ($dttypcd == 4) ){
			read($rfh, $buff,($lenfllw*$dttypcd));
			#$template = "${int32}$lenfllw";
			$template = "l$lenfllw";

			if ($reverseBtyeOrder){
				@mchninf = reverse (unpack($template, reverse($buff)));
			} else {
				@mchninf = unpack($template, $buff);
			}

			print $FH "machine-specific info:", join("|",@mchninf),"\n" if $DEBUG;
			print $FH "Subtype 3(Machine-specific information):\n" if $DEBUG;
			$RMSINTinfo->{RelNo}=$mchninf[0];
			$RMSINTinfo->{RelSubNo}=$mchninf[1];
			$RMSINTinfo->{SpecialRelNo}=$mchninf[2];
			$RMSINTinfo->{MachineCode}=$mchninf[3];
			$RMSINTinfo->{FPtypeCode}=$mchninf[4];
			$RMSINTinfo->{CompressCode}=$mchninf[5];
			$RMSINTinfo->{FileEndianCode}=$mchninf[6];
			$RMSINTinfo->{CharacterCode}=$mchninf[7];
			if ($DEBUG) {
				print $FH "Floating-point representation code:", $mchninf[4], "\n";
				print $FH "Endianness code:", $mchninf[6], "\n";
				print $FH "Character representation code:", $mchninf[7], "\n";
			}

			if (($mchninf[4] == 2) || ($mchninf[4] == 3)) {
				die "Floating-point representation code is not 1(IEEE) and this script cannot read non-IEEE floating-point numbers: $!";
			}
			if (($mchninf[6] != 1) && ($mchninf[6] != 2)){
				die "The endianness code of this file is neither 1(big) or 2(little):this script cannot read such a byte-order file: $!";
			}
			if (($mchninf[6] != $fileBtyeOrder )){
				die "The endianness code of this file differs from the counterpart based on the filelayout code: $!";
			}

			if ($mchninf[7] != 2){
				# die "Character representation code is not 2(7-bit ASCII): this script cannot read non-7-bit ASCII file: $!";
				
				if (($mchninf[7] >= 10) && ($self->{_fileDscr}->{releaseNo} >=14 )){
					print $FH "code page identifier (rel=",$self->{_fileDscr}->{releaseNo},") is ",$mchninf[7], "\n";
				}
			}
			print $FH "RMSINTinfo:\n",Dumper($RMSINTinfo) if $DEBUG;

		} elsif ( ($sbtypcd == 4) && ($dttypcd == 8) ) {
			read($rfh, $buff,($lenfllw*$dttypcd));
			$template = "d$lenfllw";
			my $templatex = "H16";
			my @sysmisx =();
			my @sysmis  =();
			if ($reverseBtyeOrder){
				@sysmis = reverse (unpack($template, reverse($buff)));
				@sysmisx   = reverse (unpack($templatex x 3, reverse($buff)));
			} else {
				@sysmis = unpack($template, $buff);
				@sysmisx   = unpack($templatex x 3, $buff);
			}
			if ($DEBUG){
				print $FH "system missing value($sysmisx[0]):", $sysmis[0], "\n";
				print $FH "       HIGHEST value($sysmisx[1]): ", $sysmis[1], "\n";
				print $FH "       LOWEST  value($sysmisx[2]):", $sysmis[2], "\n";
			}
			$RMSOBSinfo->{SYSMIS}=$sysmis[0];
			$RMSOBSinfo->{HIGHEST}=$sysmis[1];
			$RMSOBSinfo->{LOWEST}=$sysmis[2];

			print $FH "RMSOBSinfo:\n",Dumper($RMSOBSinfo) if $DEBUG;

		} elsif ( ($sbtypcd == 5) && ($dttypcd == 1) ) {
			read($rfh, $buff,($lenfllw*$dttypcd));
			print $FH "The routine for Record Type7: Subtype 5 is not implemented.\n" if $DEBUG;
		} elsif ( ($sbtypcd == 6) && ($dttypcd == 4) ) {
			read($rfh, $buff,($lenfllw*$dttypcd));
			print $FH "The routine for Record Type7: Subtype 6 is not implemented.\n" if $DEBUG;
		} elsif ($sbtypcd == 7) {
			read($rfh, $buff,($lenfllw*$dttypcd));
			print $FH "The routine for Record Type7: Subtype 7 is not implemented.\n" if $DEBUG;
		} elsif ($sbtypcd == 8) {
			read($rfh, $buff,($lenfllw*$dttypcd));
			print $FH "The routine for Record Type7: Subtype 8 is not implemented.\n" if $DEBUG;
		} elsif ($sbtypcd == 10) {
			read($rfh, $buff,($lenfllw*$dttypcd));
			print $FH "The routine for Record Type7: Subtype 10 is not implemented.\n" if $DEBUG;
		} elsif ( ($sbtypcd == 11) && ($dttypcd == 4) ) {
			read($rfh, $buff,($lenfllw*$dttypcd));
			my $notrios =  $lenfllw/3;
			#$template = "${int32}3";
			$template = "l3";
			if ($reverseBtyeOrder) {
				@msrmntlvl = reverse (unpack($template x $notrios, reverse($buff)));
			} else {
				@msrmntlvl = unpack($template x $notrios, $buff);
			}
			## print $FH "measurment level:", join("|",@msrmntlvl),"\n";
			# (1) measurement [unknown=>0, nominal=>1, ordinal=>2, ratio=>3], 
			# (2) column width,
			# (3) alignment [left=>0, right=>1, center=>2]
			my @tmptrio;
			for (my $i= 0; $i< $notrios; $i++){  
				@tmptrio=();
				for (my $j= 0; $j<3; $j++) {
					push @tmptrio, shift @msrmntlvl;
				}
				$Measurement->{$varNameA->[$i]} =[@tmptrio];
			}
			print $FH "The routine for Record Type7: Subtype 11(measurment level)\n" if $DEBUG;
			print $FH "Measurement:\n",Dumper($Measurement), if $DEBUG;
		} elsif ($sbtypcd == 12){
			read($rfh, $buff,($lenfllw*$dttypcd));
			print $FH "The routine for Record Type7: Subtype 12 is not implemented.\n" if $DEBUG;
		} elsif ($sbtypcd == 13){
			read($rfh, $buff,($lenfllw*$dttypcd));
			print $FH "The routine for Record Type7: Subtype 13 (short-long var name pairs)\n" if $DEBUG;
			my $pairlen = $lenfllw*$dttypcd;
			my @varpairs = split("\t",unpack("Z$pairlen", $buff));
			#my $slvartbl = {};
			foreach my $pr (@varpairs){
				my ($vshrt, $vlong) = split("=", $pr);
				$varShrtLngMpTbl->{$vshrt}= $vlong;
			}
			print $FH "varShrtLngMpTbl:\n", Dumper($varShrtLngMpTbl),"\n" if $DEBUG;
		} else {
			read($rfh, $buff,($lenfllw*$dttypcd));
			print $FH "The routine for Record Type7: Subtype($sbtypcd) is not implemented.\n" if $DEBUG;
		}
		
		# //////////////////////////////////////////
		# check next field Type
		# //////////////////////////////////////////
			my $escape=0;
			read($rfh, $buff,$INT4);
			#print "codeTemplate=",$codeTemplate,"\n" if $DEBUG;
			my $recordType = unpack($codeTemplate, $buff);

			#print "recordType=",$recordType,"\n" if $DEBUG;
			if ($recordType > 7){
				$escape = 1;
			}

			my $pstn = tell($rfh);
			#print "pstn: current=", $pstn,"\n" if $DEBUG;

			seek($rfh,-4, 1);
			$pstn = tell($rfh);
			#print "pstn: after=", $pstn,"\n" if $DEBUG;
			if ($escape){last;}
		# //////////////////////////////////////////

	} # end of while

	$self->{RMSINTinfo}=$RMSINTinfo;
	$self->{RMSOBSinfo}=$RMSOBSinfo;
	$self->{Measurement}=$Measurement;
	$self->{varShrtLngMpTbl}=$varShrtLngMpTbl;

} # end of sub rcrdtyp7 

sub read_RTdata {
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $cmprss           = $self->{_fileDscr}->{Compression};
	#print "within:RTdata:fileInfo=\n",Dumper($fileInfo),"\n";
	my $int32            = $self->{_fileDscr}->{OBStemplate};
	
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};

	my ($buff, $template);

	read($rfh, $buff, $INT4*2);
	$template = "${int32}2";
	
	my ($RTcode, $sbtypcd) = unpack($template, $buff);
	print $FH "RT code(999)=",$RTcode,"\n" if $DEBUG;
	print $FH "SubtypeNo(0)=",$sbtypcd,"\n" if $DEBUG;
	
	my $dataReader;
	
	if ($cmprss == 1) {
		$dataReader ="compressedData";
	} elsif (!($cmprss)) {
		$dataReader ="noncompressedData";
	} else {
		die "Unknown compression switch value ($cmprss): it must be either 0 or 1: $!"; 
	}
	
	$self->$dataReader();
	
	return;
}

sub compressedData {
	my $self =shift;
	
	my $rfh  = $self->{RFL};
	my $tfh  = $self->{TFL};
	my $NAstring=$self->{_prcssOptn}->{NAstring};

	#&datafile(\@tmplt, \@sysmis, \$strngctn);
	#my ($rftmplt, $rfsysmis, $rfstrngctn) = @_;
	#my ($buff, @dtpercase, $ncases, $caseindx, @tmpstore, $dtpoint, @vartypx, @vartyp);
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	
	my ($buff, $template, $dtprcs, $dtpoint, $typindx);
	my $FC=0;
	my $caseindx =0;
	my @tmpstore=();
	
	my $reverseBtyeOrder = $self->{_fileDscr}->{reverseBtyeOrder};
	#my $cmprss          = $self->{_fileDscr}->{compressBias};
	my $cmprss           = $self->{_fileDscr}->{Compression};
	my $nOBS             = $self->{_fileDscr}->{nOBS};
	my $ncases           = $self->{_fileDscr}->{caseQnty};
	my $noCharVarCntn    = $self->{_noCharVarCntn};
	my $varNameA    = $self->{_varNameA};
	my $varType     = $self->{_varType};
	my @varTypeRaw  = @{$self->{_varTypeRaw}};
	my $RMSOBSinfo= $self->{_fileDscr}->{RMSOBSinfo};
	#my $varNameH = $self->{_varNameH};# hash by name

	my $tmplt = $self->{_varTemplate};
	print $FH "template:", join("|", @{$tmplt}), "\n" if $DEBUG;

	my @vartypx;
	foreach my $k (@{$varNameA}) {
		push @vartypx, $varType->{$k};
	}
	print $FH "vartypx=", join('|',@vartypx), "\n" if $DEBUG;
	#Variable types(all:vartyp):39|-1|-1|-1|-1|8|8|0|0|0
	#Variable types(>0:vartypx):39|8|8|0|0|0
	#The unpack template for the data file:Z40|Z8|Z8|d|d|d
	my $vt = [(0) x scalar(@{$varNameA}) ];
	my $nodcml=0;

	# ///////////////////// compressed case ///////////////////// 
	my $ii = 0;
	while(1) {
		read($rfh, $buff, 8);
		my $template = "C8";
		my @dtclstr = unpack($template, $buff);
		## print $FH "buffer:",unpack("H8", $buff),"\n" if $DEBUG;

		## print $FH "$ii-th data octet:", join("|",@dtclstr),"\n" if $DEBUG;

		for (my $i=0;$i<=$#dtclstr; $i++){
			#print $FH "process $i-th element of $ii-th data octet:\n" if $DEBUG;
			#print $FH "process", $ii*$OBS + $i, "-th element\n" if $DEBUG;

			#print $FH "process $i-th element of $ii-th data octet:\n" if $DEBUG;
			if ($dtclstr[$i] == 252) {
				# FC
				# end of the data file
				print $FH "EOF Mark (FC) was found at $i-th position of $ii-th data octet\n" if $DEBUG;
				$FC = 1;
				last;
			} elsif ($dtclstr[$i] == 253) {
				# FD
				# uncompressed datum follows
				#print $FH "enter 253:\n" if $DEBUG;
				read($rfh, $buff, $OBS);
				my $typindx = ($ii*$OBS + $i )%$nOBS;
				my $vartypvl = $varTypeRaw[$typindx];
				if(  ($vartypvl > 0) || ($vartypvl == -1)  ){
					# string: template Z
					#print $FH "a:typindx:", $typindx, "\n" if $DEBUG;
					#print $FH "a:vartyp:", $vartyp[$typindx], "\n" if $DEBUG;
					$template = "Z8";
					$dtpoint = unpack($template, $buff);
				} elsif ($vartypvl == 0) {
					#print $FH "n:typindx:", $typindx, "\n" if $DEBUG;
					#print $FH "n:vartyp:", $vartyp[$typindx], "\n" if $DEBUG;
					# numeric: template d 
					$template = "d";
					if ($reverseBtyeOrder){
						$dtpoint = unpack($template, reverse($buff));
					} else {
						$dtpoint = unpack($template, $buff);
					}
				} else {
					die "out-of-range value ((vartyp[$typindx = (($ii*$OBS + $i +1)%$nOBS)]) at the $i-th element of the $ii-th data octet: valid range is [-1, 0, 255]: $!"; 
				}
				push @tmpstore, $dtpoint;
				#next;
			} elsif ($dtclstr[$i] == 254){
				# FE
				# All eight bytes are spaces (X20)
				$dtpoint = "        ";
				push @tmpstore, $dtpoint; 
			} elsif ($dtclstr[$i] == 255){
				# FF
				# system missing value
				#$dtpoint = $RMSOBSinfo->{SYSMIS};
				#$dtpoint = 'NA';
				$dtpoint = $NAstring;
				push @tmpstore, $dtpoint; 
			} elsif ($dtclstr[$i] == 0) {
				# 00
				# "skip this code" (what does this mean? do nothing?)
				print $FH "CODE 'X00' was detected at $i-th element of the $ii-th data octet: $!" if $DEBUG;
			} elsif (($dtclstr[$i] > 0 ) && ($dtclstr[$i] < 252 )) {
				# compressed datum: decompress it
				$dtpoint = $dtclstr[$i] - 100;
				push @tmpstore, $dtpoint; 
			} else {
				die "out-of-range value ($dtclstr[$i]) at the $i-th element of the $ii-th data octet: valid range is [0, 255]: $!"; 
			} # end of if-set
			undef($dtpoint);
			#print  $FH "end of $i-th datapoint\n\n";
			
			if ( !(($ii*$OBS + $i +1) % $nOBS) ) {
				if  ($noCharVarCntn) {
					# concatenating string data elements
					## print $FH "before:", join ("|", @tmpstore), "\n\n" if $DEBUG;
					my $tmpstrng='';
					for (my $j=0;$j<$nOBS;$j++){
						if ( $varTypeRaw[$j] == 0) {
							 # all 3 cases
							if ($tmpstrng){push @tmpstore, $tmpstrng;$tmpstrng="";}
							push @tmpstore,  shift(@tmpstore) ;
						} elsif ($varTypeRaw[$j] >0) {
							# all 3 cases
							if ($tmpstrng){push @tmpstore, $tmpstrng;$tmpstrng="";}
							$tmpstrng = shift (@tmpstore);
							if ($j==($nOBS-1)){push @tmpstore, $tmpstrng;$tmpstrng="";}
						} elsif ($varTypeRaw[$j] == -1) {
							# 2 cases (1 or -1)
							$tmpstrng .= shift(@tmpstore);
							if ($j==($nOBS-1)){push @tmpstore, $tmpstrng;$tmpstrng="";}
						}
						## print $FH "$j-th:", join ("|", @tmpstore), "\n";
					} # end of for (concatanating)
				}
				$caseindx = int(($ii*$OBS + $i +1)/ $nOBS); # $caseindx starts from 1 not 0
				# remove trailing spaces added by the 8-byte-alignment from string vars
				for (my $k=0;$k<=$#tmpstore;$k++) {
					# $vartypx[$k] 
					if ($vartypx[$k]) {
						$vt->[$k]=-1;
						if (length($tmpstore[$k]) - $vartypx[$k]) {
							## print $FH "adj at $k-th position:\n" if $DEBUG;
							## print $FH "old and new lengths:",length($tmpstore[$k]),"\t$vartypx[$k]\n" if $DEBUG;
							$tmpstore[$k]= substr($tmpstore[$k], 0, $vartypx[$k]);
							## print $FH "actual length:",length($tmpstore[$k]),"\n" if $DEBUG;
						} elsif ($vartypx[$k] > length($tmpstore[$k])) {
							die "At $k-th var position, the adjusted(8-byte aligned) one($tmpstore[$k]) is shorter than the original length ($vartypx[$k]): $!"; 
						}
						# to check '.' which is mistyped as a missing value for a string variable
						(my $tmpprd = $tmpstore[$k]) =~ s/ //g;
						if ( ord($tmpprd) == 46){
							## if (ord(substr($tmpstore[$k],0,1)) == 46) # this does not work if the position of a period is not 0
							## print $FH "A mistyped period as a missing value in a string variable($k-th position):",$tmpstore[$k],"\n";
							#$tmpstore[$k]= 'NA';
							$tmpstore[$k]= $NAstring;
						} else {
							$tmpstore[$k] =~ s/\s+$//;
						}
						
					}
					if ($tmpstore[$k] ne 'NA'){
						# date conversion
						my $vf = $self->{_varFormat}->{ $self->{_varNameA}->[$k] };

						if ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'date'){
							# date conversion
							print $FH "$k-th var(before ISO date conversion):",$tmpstore[$k],"\n" if $DEBUG;
							$tmpstore[$k]=&VDC::DSB::Ingest::StatData::findGregorianDate($tmpstore[$k], 'D');
							print $FH "$k-th var(after ISO date conversion):",$tmpstore[$k],"\n" if $DEBUG;
						} elsif ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'time') {
							# time conversion
							print $FH "$k-th var(before ISO time conversion):",$tmpstore[$k],"\n";# if $DEBUG;
							if ($vf eq 'DTIME'){
								$tmpstore[$k]=&VDC::DSB::Ingest::StatData::findGregorianDate($tmpstore[$k], 'JT');
							} elsif ($vf eq 'DATETIME'){
								$tmpstore[$k]=&VDC::DSB::Ingest::StatData::findGregorianDate($tmpstore[$k], 'DT');
							} else {
								$tmpstore[$k]=&VDC::DSB::Ingest::StatData::findGregorianDate($tmpstore[$k], 'T');
							}
							print $FH "$k-th var(after ISO time conversion):",$tmpstore[$k],"\n" if $DEBUG;

						} elsif ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'other'){
							# day of week or month : numeric to string


							if ($vf eq 'WKDAY'){
								# day of week
								$tmpstore[$k]=$WEEKDAYs[ $tmpstore[$k] -1];
							} elsif ($vf eq 'MONTH'){
								# month
								$tmpstore[$k]=$MONTHs[ $tmpstore[$k] -1];
							}
						}

					}
				}
				
				
				$dtprcs = join ("\t", @tmpstore);
				#print $FH "\n\n final($caseindx):", $dtprcs, "\n\n" if $DEBUG;
				print $FH $dtprcs, "\n" if $DEBUG;
				print $tfh $dtprcs, "\n";
				
				my $intdatum;
				for (my $l=0;$l<@tmpstore;$l++){
					unless ($vartypx[$l]){
						unless ($tmpstore[$l] eq $NAstring ) {
						$intdatum = int($tmpstore[$l]);
						if ($intdatum != $tmpstore[$l]){
							unless ($vt->[$l]){
								$vt->[$l]=1;
								$nodcml++;
							}
						}
						
						}
					}
				}
				
				@tmpstore = ();
			} # end of if( $nOB == sum data points )
		} # end of for (octet processing)
		if ((eof $rfh) || ($FC) ) {
			print $FH "The end of this file has been detected after ", $caseindx ," cases.\n" if $DEBUG;
			if ($ncases == -1) {
				$ncases = $caseindx;
				print $FH "The number of cases in this file is set to:$ncases.\n" if $DEBUG;
			} elsif ($ncases == $caseindx) {
				print $FH "The end of the data section has been correctly detected after $ncases iterations.\n" if $DEBUG;
			} elsif ($ncases ==  4294967295) {
				#if the unpac of -1 fails, the value becomes 4294967295
				print $FH  "The number of iterations($caseindx) and the number of cases recorded in this file ($ncases) are different due to some bug in unpack(N/V) function:\nThe number of iterations($caseindx) is used:$!\n" if $DEBUG;
				$ncases = $caseindx;
			} else {
				print $FH  "The number of iterations($caseindx) and the number of cases recorded in this file ($ncases) are different:\nThe number of iterations($caseindx) is used:$!\n" if $DEBUG;
				$ncases = $caseindx;
			}
			close ($rfh);
			last;
		}
		$ii++;
	} # end of while (endless)
	 # end of compressed case
	my $fltcntr=0;
	for (my $i=0;$i<@{$varNameA}; $i++){
		if ($vt->[$i]>0){
			$self->{_dcmlVarTbl}->{$varNameA->[$i]}='F';
			$fltcntr++;
		}
	}
	$self->{_noDcmlVar}=$fltcntr;
	$self->{_fileDscr}->{caseQnty}= $ncases;
	
}

sub noncompressedData{
	my $self =shift;
	
	my $rfh  = $self->{RFL};
	my $tfh  = $self->{TFL};
	my $NAstring=$self->{_prcssOptn}->{NAstring};

	#&datafile(\@tmplt, \@sysmis, \$strngctn);
	#my ($rftmplt, $rfsysmis, $rfstrngctn) = @_;
	
	# $self->{casetemplate}=[];
	# $self->{sysmis}=[];
	# $self->{strngctn}=; # scalar

	#&datafile(\@tmplt, \@sysmis, \$strngctn);
	#my ($rftmplt, $rfsysmis, $rfstrngctn) = @_;
	#my ($buff, @dtpercase, $ncases, $caseindx, @tmpstore, $dtpoint, @vartypx, @vartyp);
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	
	my ($buff, $template, $dtprcs, $dtpoint, $typindx);
	my $FC=0;
	my $caseindx =0;
	
	my $int32            = $self->{_fileDscr}->{OBStemplate};
	my $reverseBtyeOrder = $self->{_fileDscr}->{reverseBtyeOrder};
	#my $cmprss           = $self->{_fileDscr}->{compressBias};
	my $cmprss           = $self->{_fileDscr}->{Compression};
	my $nOBS             = $self->{_fileDscr}->{nOBS};
	my $ncases           = $self->{_fileDscr}->{caseQnty};
	my $noCharVarCntn    = $self->{_noCharVarCntn};
	my $varNameA    = $self->{_varNameA};
	my $varType     = $self->{_varType};
	my @varTypeRaw  = @{$self->{_varTypeRaw}};
	my $RMSOBSinfo= $self->{_fileDscr}->{RMSOBSinfo};

	#my $varNameH = $self->{_varNameH};# hash by name

	my $tmplt = $self->{_varTemplate};
	print $FH "template:", join("|", @{$tmplt}), "\n" if $DEBUG;

	my @vartypx;
	foreach my $k (@{$varNameA}) {
		push @vartypx, $varType->{$k};
	}
	print "vartypx=", join('|',@vartypx), "\n" if $DEBUG;
	#Variable types(all:vartyp):39|-1|-1|-1|-1|8|8|0|0|0
	#Variable types(>0:vartypx):39|8|8|0|0|0
	#The unpack template for the data file:Z40|Z8|Z8|d|d|d
	my $vt = [(0) x scalar(@{$varNameA}) ];
	my $nodcml=0;

	if ($reverseBtyeOrder){
		$template = join("", reverse(@{$tmplt}));
		print  $FH "reversed template:", $template, "\n" if $DEBUG;
	} else {
		$template = join("", @{$tmplt});
		print  $FH "         template:", $template, "\n" if $DEBUG;
	}
	
	print $FH "template:", join("|", @{$tmplt}), "\n" if $DEBUG;
	
	# for ($i=0;$i<$ncases;$i++)
	for (my $i=0; ;$i++){
		read($rfh, $buff, $OBS*$nOBS);
		my @dtpercase;
		if ($reverseBtyeOrder){
			#print  $FH "reversed template:", $template = join("", reverse(@tmplt)), "\n" if $DEBUG;
			@dtpercase = reverse (unpack($template, reverse($buff)));
		} else {
			#print  $FH "         template:", $template = join("", @tmplt), "\n" if $DEBUG;
			@dtpercase = unpack($template, $buff);
		}
		## print  $FH " pre-msvl $i:", join("|", @dtpercase), "\n" if $DEBUG;
		# missing value processing and remove trailing spaces added by the 8-byte-alignment from string vars
		for (my $k=0;$k<=$#dtpercase;$k++){
			# regular missing values: '.' for a numeric var and '' for a string var
			if ($dtpercase[$k] == $RMSOBSinfo->{SYSMIS}){
				#$dtpercase[$k] = 'NA';
				$dtpercase[$k] = $NAstring;
			}
			if ($vartypx[$k]) {
				 # string var cases
				if (length($dtpercase[$k]) - $vartypx[$k]) {
					## print $FH "adj at $k-th position:\n" if $DEBUG;
					## print $FH "old and new lengths:",length($dtpercase[$k]),"\t$vartypx[$k]\n" if $DEBUG;
					$dtpercase[$k]= substr($dtpercase[$k], 0, $vartypx[$k]);
					## print $FH "actual length:",length($dtpercase[$k]),"\n" if $DEBUG;
				} elsif ($vartypx[$k] > length($dtpercase[$k])) {
					die "At $k-th var position, the original length ($vartypx[$k]) must be shorter than the adjusted(8-byte aligned) one($dtpercase[$k]): $!"; 
				}
				# to check '.' which is mistyped as a missing value for a string variable
				(my $tmpprd = $dtpercase[$k]) =~ s/ //g;
				if ( ord($tmpprd) == 46) {
					## if (ord(substr($dtpercase[$k],0,1)) == 46) # this does not work if the position of a period is not 0

					## print $FH "A mistyped period as a missing value in a string variable($k-th position):",$dtpercase[$k],"\n" if $DEBUG;
					#$dtpercase[$k] = 'NA';
					$dtpercase[$k] = $NAstring;

				} else {
					$dtpercase[$k] =~ s/\s+$//;
				}
			}
			
				if ($dtpercase[$k] ne 'NA'){
					# date conversion
					my $vf = $self->{_varFormat}->{ $self->{_varNameA}->[$k] };
					
					if ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'date'){
						# date conversion
						$dtpercase[$k]=&VDC::DSB::Ingest::StatData::findGregorianDate(\$dtpercase[$k], 'D');
						#print $FH "$k-th var(after ISO date conversion):",$dtpercase[$k],"\n" if $DEBUG;
					} elsif ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'time') {
						# time conversion
						
						if ($vf eq 'DTIME'){
							$dtpercase[$k]=&VDC::DSB::Ingest::StatData::findGregorianDate($dtpercase[$k], 'JT');
						} elsif ($vf eq 'DATETIME'){
							$dtpercase[$k]=&VDC::DSB::Ingest::StatData::findGregorianDate($dtpercase[$k], 'DT');
						} else {
							$dtpercase[$k]=&VDC::DSB::Ingest::StatData::findGregorianDate($dtpercase[$k], 'T');
						}

					} elsif ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'other'){
						# day of week or month : numeric to string
						
						
						if ($vf eq 'WKDAY'){
							# day of week
							$dtpercase[$k]=$WEEKDAYs[ $dtpercase[$k] -1];
						} elsif ($vf eq 'MONTH'){
							# month
							$dtpercase[$k]=$MONTHs[ $dtpercase[$k] -1];
						}
					}
				}
			
			
		}
		$dtprcs = join("\t", @dtpercase);
		## print  $FH "post-msvl $i:", $dtprcs,"\n" if $DEBUG;
		print $FH $dtprcs,"\n" if $DEBUG;
		print $tfh $dtprcs, "\n";
		
		my $intdatum;
		for (my $l=0;$l<@dtpercase;$l++){
			unless ($vartypx[$l]){
			
				unless ($dtpercase[$l] eq $NAstring ) {
			
				$intdatum = int($dtpercase[$l]);
				if ($intdatum != $dtpercase[$l]){
					unless ($vt->[$l]){
						$vt->[$l]=1;
						$nodcml++;
					}
				}
				
				}
			}
		}
		
		if (eof $rfh) {
			print $FH "The end of this file has been detected after ", ($i+1) ," iterations.\n" if $DEBUG;
			if ($ncases == -1) {
				$ncases = $i +1;
				print $FH "The number of cases in this file is set to:", $ncases,".\n" if $DEBUG;
			} elsif ($ncases == ($i +1)) {
				print $FH "The end of the data section has been correctly detected after ",$ncases," iterations.\n" if $DEBUG;
			} else {
				die "The number of iterations(", $i+1, ") and the number of cases recorded in this file ($ncases) are different:$!\n";
			}
			close ($rfh);
			last;
		}
	} # end of data processing
	print $FH "The processing of the uncompressed data file is completed.\n" if $DEBUG;
	#return [$ncases, $vt, $nodcml];
	my $fltcntr=0;

	for (my $i=0;$i<@{$varNameA}; $i++){
		if ($vt->[$i]>0){
			$self->{_dcmlVarTbl}->{$varNameA->[$i]}='F';
			$fltcntr++;
		}
	}
	$self->{_noDcmlVar}=$fltcntr;
	$self->{_fileDscr}->{caseQnty}= $ncases;
	#return $ncases;
}

1;


__END__

=head1 NAME

VDC::DSB::Ingest::SAV - SPSS system (sav) file reader

=head1 DEPENDENCIES

=head2 Nonstandard Modules

    VDC::DSB::Ingest::StatData

=head1 DESCRIPTION

VDC::DSB::Ingest::SAV parses an SPSS sav file and returns parsing results (metadata) as a reference to a hash.  This module is called by VDC::DSB::Ingest::StatDataFileReaderFactory and is used with VDC::DSB::Ingest::StatData.


=head1 SEE ALSO

    VDC::DSB::Ingest::StatDataFileReaderFactory

    VDC::DSB::Ingest::StatData

=head1 AUTHOR

Akio Sone, Virtual Data Center Project, Harvard University

(<URL:http://thedata.org/>)

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2004 President and Fellows of Harvard University

GNU General Public License



=cut




