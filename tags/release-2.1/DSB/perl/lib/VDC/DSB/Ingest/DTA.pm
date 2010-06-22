package VDC::DSB::Ingest::DTA;

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
@VDC::DSB::Ingest::DTA::ISA= qw(VDC::DSB::Ingest::StatData);
use IO::File;
use File::Copy;
use Data::Dumper;

# package variables
my $TMPDIR=File::Spec->tmpdir();
my $INT4 = 4;
my $OBS = 8;
my $MVstring="NA";
my $unpackSize = 100;

my ($bytrvrs, $fbytordr, $int16, $int32);
my %fieldlength =(
		releaseNo =>1,
		byteOrder =>1,
		headerBase=>26,
);
my ($releaseNo, $nvar);

my @dblMaxValue = (2**333, 2**1023);
my @lengthLabel = (32,81);
my @lengthName  = (9,33);
my @lengthFmt   = (12, 49, 7);
my @lengthExpFld= (2,4, 0);
my $VTC = {
	105 =>[ 127, 100, 102,  98, 105, 108 ], 
	111 =>[   0, 255, 254, 251, 252, 253 ],
};

my $RLSPARAMS = {

	104 =>	{	
				RlsNoString =>"3",
				lengthLabel =>$lengthLabel[0],
				lengthName  =>$lengthName[0],
				lengthExpFld=>$lengthExpFld[2],
				dblMaxValue =>$dblMaxValue[0],
				lengthFmt   =>$lengthFmt[2],
				varTypeCode =>$VTC->{105},
			},
			
	105 =>	{	
				RlsNoString =>"4 or 5",
				lengthLabel =>$lengthLabel[0],
				lengthName  =>$lengthName[0],
				lengthExpFld=>$lengthExpFld[0],
				dblMaxValue =>$dblMaxValue[0],
				lengthFmt   =>$lengthFmt[0],
				varTypeCode =>$VTC->{105},
			},
	108 =>{
				RlsNoString =>"6",
				lengthLabel =>$lengthLabel[1],
				lengthName  =>$lengthName[0],
				lengthExpFld=>$lengthExpFld[0],
				dblMaxValue =>$dblMaxValue[1],
				lengthFmt   =>$lengthFmt[0],
				varTypeCode =>$VTC->{105},
			},
	110 =>{
				RlsNoString =>"7",
				lengthLabel =>$lengthLabel[1],
				lengthName  =>$lengthName[1],
				lengthExpFld=>$lengthExpFld[1],
				dblMaxValue =>$dblMaxValue[1],
				lengthFmt   =>$lengthFmt[0],
				varTypeCode =>$VTC->{105},
			},
	111 =>{
				RlsNoString =>"7se",
				lengthLabel =>$lengthLabel[1],
				lengthName  =>$lengthName[1],
				lengthExpFld=>$lengthExpFld[1],
				dblMaxValue =>$dblMaxValue[1],
				lengthFmt   =>$lengthFmt[0],
				varTypeCode =>$VTC->{111},
			},
	113 =>{
				RlsNoString =>"8 or 9",
				lengthLabel =>$lengthLabel[1],
				lengthName  =>$lengthName[1],
				lengthExpFld=>$lengthExpFld[1],
				dblMaxValue =>$dblMaxValue[1],
				lengthFmt   =>$lengthFmt[0],
				varTypeCode =>$VTC->{111},
			},
	114 =>{
				RlsNoString =>"10",
				lengthLabel =>$lengthLabel[1],
				lengthName  =>$lengthName[1],
				lengthExpFld=>$lengthExpFld[1],
				dblMaxValue =>$dblMaxValue[1],
				lengthFmt   =>$lengthFmt[1],
				varTypeCode =>$VTC->{111},
			}, 

};
# stata 10 shipped (2007/06)
# stata  9 shipped (2005/04)

my $byteOrderKey;

my $fileDecodeParam = {
	0 =>{byteReversal =>0, int16 =>'s', int32 =>'l'},
	1 =>{byteReversal =>1, int16 =>'n', int32 =>'N'}, 
	2 =>{byteReversal =>1, int16 =>'v', int32 =>'V'},
};



sub _init {
	my $self=shift;
	my $args = {@_};
	my $DEBUG;
	my $FH;
	
	unless (exists($args->{LOGFILE})){
		my $logfile = $TMPDIR  . '/dta.' . $$ . '.log';
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
		die "method parse_file [DTA.pm] needs at least an dta file name in its argument list!";
	} else {
		$self->{_prcssOptn}->{SOURCEFILE} = $datafile= $args->{SOURCEFILE};
		$base=$datafile;
		print $FH "dta file name: $datafile\n" if $DEBUG;
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
	
	
	$self->{_fileDscr}->{fileType} = 'application/x-stata';
		
	$self->{releaseNo}=undef;

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
	my $tfh = new IO::File("> $tabfile");
	
	my ($reverseBtyeOrder);
	my $io = new IO::File;
	$self->{RFL}=$io;
	$self->{TFL}=$tfh;

	my @field2Method =('Header','Descriptors','VariableLabels','ExpansionFields','Data','ValueLabels');

	if ($io->open("< $datafile")){
		my ($returnValue, $method4field );
		binmode($io);
		for (my $i=0;$i<@field2Method;$i++){
			print $FH "field Type=",$field2Method[$i],"\n" if $DEBUG;
			$method4field = "read_" . $field2Method[$i];
			my $relsuffix;
			if ($i == 3){
				# release 4 does not have ExpansionFields
				if ($self->{releaseNo} == 104){
					next;
				}
			}
			if ($i == 5){
				if ($self->{releaseNo} <= 105 ){
					$relsuffix="Rel105";
				} elsif ($self->{releaseNo} >= 108){
					$relsuffix="Rel108";
				}
				$method4field .= $relsuffix;
			}
			$returnValue=$self->$method4field();
			if ($returnValue) {
				last;
			}
		}
		
	} else {
		die "Failed to open the dta file (", $datafile, "): wrong filename or pathname?\n";
	}
	$tfh->close();
	copy($tabfile,"$base.tab");
	$io->close();
	print $FH "self(final:dta):\n",Dumper($self),"\n" if $DEBUG;
}

sub DESTROY{
	my $self =shift;
	unlink($self->{_prcssOptn}->{SMSTTFILE});
}




# /////////////////////////////////////////////////////////
# Method for each field
# /////////////////////////////////////////////////////////

sub read_Header {
	my $self=shift;
	my $rfh  = $self->{RFL};
	my $DEBUG=1;
	my $FH   = $self->{_prcssOptn}->{LOGFILE};

	my $OSendian= &VDC::DSB::Ingest::StatData::getOSbyteOrder();
	# 1. Header: read the release number
	my ($buff, $template);
	print $FH "releaseNo:length=", $fieldlength{releaseNo},"\n" if $DEBUG;
	read($rfh, $buff, $fieldlength{releaseNo});
	# Because in the near future the release number might be beyond 127,
	# unsigned 'C' is used instead of signed 'c'
	$template = "C";
	$releaseNo  = unpack($template, $buff);

	print $FH "Relese Number:", $releaseNo, "\n" if $DEBUG;
		 # note: stata (rel.5) use not 2**1023 but 2**333
		 # (little endian: 0x00 00 00 00 00 00 C0 54) as
		 # a missing value for a DOUBLE; see the Stata user manual(release 5) 19.2.1

	my ($lenname, $lenef, $dblmax);
	$self->{releaseNo}     = $releaseNo;
	$self->{releaseParams} = $RLSPARAMS->{$releaseNo};
	
	if (exists($RLSPARAMS->{$releaseNo})){
		print $FH "Relese Number of this file is ",$RLSPARAMS->{$releaseNo}->{RlsNoString},"\n" if $DEBUG;
	} elsif ($releaseNo < 104) {
		die "Error: Release number of this file is too old to be handled";
	} else {
		die "Error: Release number of this file seems to be out-of-range, or this file may not be a Stata file.\n";
	}

	# Byte order
	read($rfh, $buff,$fieldlength{byteOrder});
	$template = "c";
	my $fbytordr  = unpack($template, $buff);
	
	unless ($fbytordr - $OSendian){
		# byte-reversal is not necessary for floating numbers
		#endianess: platform == file
		$byteOrderKey=0;
	} elsif (abs($fbytordr - $OSendian) == 1) {
		# byte-reversal is  necessary
		if ($fbytordr == 1){
			#file: big endian
			$byteOrderKey=1;
		} elsif ($fbytordr == 2){
			#file: little endian
			$byteOrderKey=2;
		}
	} else {
		die "The byte-order of this file is neither big or little endian.\nThis may not be a STATA file.\n";
	}
	
	
	$int16   = $fileDecodeParam->{$byteOrderKey}->{int16};
	$int32   = $fileDecodeParam->{$byteOrderKey}->{int32};
	$bytrvrs = $fileDecodeParam->{$byteOrderKey}->{byteReversal};
	my $lenlbl  = $RLSPARAMS->{$releaseNo}->{lengthLabel};
	# length of the header (data label portion only)
	my $lengthHeader   = $RLSPARAMS->{$releaseNo}->{lengthLabel};
	
	$template = "c2${int16}${int32}Z${lenlbl}";
	# calculate the length of the remaining header portion
	if ($releaseNo > 104){
		# 10(10-byte part) - 2(already read) + 18(time stamp)    = 26
		$lengthHeader   += 26;
		# add the template for the time-stamp field
		$template .= "Z18";
	} elsif ($releaseNo == 104){
		# 10(10-byte part) - 2(already read) +  0(no time stamp) =  8
		$lengthHeader   += 8;
	}
	# 1.x Header: read the remaining fields

	read($rfh, $buff, $lengthHeader);
	print $FH "template for the header:", $template, "\n" if $DEBUG;
	print $FH "end of the header=" . tell($rfh) . "\n" if $DEBUG;

	(my $fltyp, my $unused, $nvar, my $nobs, my $dt_lbl, my $tm_stmp) = unpack($template, $buff);
	if ($DEBUG){
		print $FH "\n\nHeader Summary\n";
		print $FH "relese number:", $releaseNo, "\n";
		print $FH "file byteorder:", $fbytordr,"\n";
		print $FH "file type:", $fltyp, "\n";
		print $FH "unused 1-byte:", $unused, "\n";
		print $FH "variables(nvars):", $nvar, "\n";
		print $FH "observations(nobs):", $nobs, "\n";
			print $FH "data_label:", $dt_lbl, "\n";
		if ($releaseNo > 104){
			print $FH "time_stamp:", $tm_stmp, "\n\n";
		}
	}
	$self->{_fileDscr}->{varQnty}      =$nvar;
	$self->{_fileDscr}->{caseQnty}     =$nobs;
	$self->{_fileDscr}->{document}     =$dt_lbl;
	$self->{_fileDscr}->{stataFileType}=$fltyp ;
	return 0;
}

sub read_Descriptors {
	my $self=shift;
	my $rfh  = $self->{RFL};
	my $DEBUG=1;
	my $FH=$self->{_prcssOptn}->{LOGFILE};

	# 2. Descriptors
	# 2.1 Typlist(variable type list)
	my @typeList;
	my ($buff);
	my $lenname  = $RLSPARAMS->{$releaseNo}->{lengthName};
	my $lengthFmt= $RLSPARAMS->{$releaseNo}->{lengthFmt};
	print $FH "before descriptors=" . tell($rfh) . "\n" if $DEBUG;

	read($rfh, $buff, $nvar);
	if ($releaseNo >= 111) {
		@typeList = unpack("C$nvar", $buff);
	} else {
		@typeList = unpack("C$nvar", $buff);

		# Here one-byte type is unsigned (use "C" instead of "c", signed)
	}
	print $FH "contents=" . join("-",@typeList) . "\n" if $DEBUG;
	my @unpackTemplate = ();
	my $VTCno=105;
	if ($releaseNo >= 111) {
		$VTCno=111;
	}
		#$VTC = [ 0, 255, 254, 251, 252, 253 ];
		#$VTC = [ 127, 100, 102, 98, 105, 108 ];
	my @byteSizeList;
	my $noCharVar = 0;
	my $noDcmlVar = 0;
	my @varTypeCode = ();
	my $byteOffset =0;
	my $boTable = {};
	for (my $i = 0 ; $i < $nvar ; $i++) {
		# each variable is to be upacked by native modes with or without reversing the byte-order a floating number
		     if ($typeList[$i] == $VTC->{$VTCno}->[1]){
			# d
			$byteSizeList[$i]   = 8;
			$byteOffset +=8;
			$typeList[$i] = chr($typeList[$i]);
			$noDcmlVar++;
			push @varTypeCode, "d";
			push @unpackTemplate, "d";
		} elsif ($typeList[$i] == $VTC->{$VTCno}->[2]){
			# f
			$byteSizeList[$i]   = 4;
			$byteOffset +=4;
			$typeList[$i] = chr($typeList[$i]);
			$noDcmlVar++;
			push @varTypeCode, "f";
			push @unpackTemplate, "f";
		} elsif ($typeList[$i] == $VTC->{$VTCno}->[3]){
			# b
			$byteSizeList[$i]   = 1;
			$byteOffset +=1;
			$typeList[$i] = chr($typeList[$i]);
			push @varTypeCode, "b";
			push @unpackTemplate, "c";
		} elsif ($typeList[$i] == $VTC->{$VTCno}->[4]){
			# i
			$byteSizeList[$i]   = 2;
			$byteOffset +=2;
			$typeList[$i] = chr($typeList[$i]);
			push @varTypeCode, "i";
			push @unpackTemplate, "s";
		} elsif ($typeList[$i] == $VTC->{$VTCno}->[5]){
			# l
			$byteSizeList[$i]   = 4;
			$byteOffset +=4;
			$typeList[$i] = chr($typeList[$i]);
			push @varTypeCode, "l";
			push @unpackTemplate, "l";
		} elsif ($typeList[$i] >= $VTC->{$VTCno}->[0]) {
			$typeList[$i] = $byteSizeList[$i] = $typeList[$i] - $VTC->{$VTCno}->[0];
			$boTable->{$i} = [$byteOffset, $typeList[$i]];
			$noCharVar++;
			push @varTypeCode, "s";
			push @unpackTemplate, "Z$typeList[$i]";
			$byteOffset +=$typeList[$i];
		} else {
			print $FH "type_unknown=", $typeList[$i], "\n" if $DEBUG;
			die "unkown format type was found for $i-th var";
		}
	}
	$self->{_noDcmlVar}=$noDcmlVar;
	$self->{_noCharVar}=$noCharVar;
	$self->{byteOffset4charVar} = $boTable;
	if ($noCharVar){
		print $FH "byteOffset4charVar=\n", Dumper($self->{byteOffset4charVar}), "\n";
	}
	print $FH "stata variable-type code:", join("|", @varTypeCode), "\n\n" if $DEBUG;
	$self->{varTypeCode}=[@varTypeCode];

	print $FH "variable template to be used:" . join("", @unpackTemplate) . "\n" if $DEBUG;
	$self->{unpackTemplate}= [@unpackTemplate];
	
	# calculation of the number of bytes per observation
	my $nOBS = 0;
	foreach (@byteSizeList) {
		$nOBS += $_;
	}
	print $FH "bytes per observation:", $nOBS, "\n\n" if $DEBUG;
	print $FH "bye offset final:", $byteOffset, "\n\n" if $DEBUG;
	$self->{_fileDscr}->{nOBS}=$nOBS;
	$self->{byteSizeList}=[@byteSizeList];

	print $FH "after variable type=" . tell($rfh) . "\n" if $DEBUG;

	# 2.2 varlist(variable name list)

	read($rfh, $buff, $lenname * $nvar);
	my @varlst = unpack("Z$lenname" x $nvar, $buff);
	print $FH "varlst (Z$lenname):\n\t", join("\n\t", @varlst), "\n\n" if $DEBUG;
	$self->{_varNameA}=[@varlst];
	for (my $i=0;$i<@varlst;$i++){
		$self->{_varNameH}->{$varlst[$i]}=$i;
	}
	print $FH join("\t", @varlst), "\n" if $DEBUG;
	print $FH "after variable name=" . tell($rfh) . "\n" if $DEBUG;

	# 2.3 srtlist (variable sort list)
	my $lensrt = 2 * ($nvar + 1);
	my $int16   = $fileDecodeParam->{$byteOrderKey}->{int16};

	read($rfh, $buff, $lensrt);
	my @srtlst = unpack("${int16}$lensrt", $buff);
	print $FH "srtListString:", join("|",@srtlst),"\n\n" if $DEBUG;
	$self->{_fileDscr}->{sortVarlist}= [@srtlst];
	print $FH "after variable sort list=" . tell($rfh) . "\n" if $DEBUG;

	# 2.4 fmtlist(var format list)
	read($rfh, $buff, ($lengthFmt * $nvar));
	my @fmtlst = unpack("Z$lengthFmt" x $nvar, $buff);
	print $FH "fmtlst(Z$lengthFmt):\n\t", join("\n\t", @fmtlst), "\n" if $DEBUG;
	print $FH "after variable format list=" . tell($rfh) . "\n" if $DEBUG;

	# 2.5 lblist(value label for each variable (if applicable))
	read($rfh, $buff, ($lenname * $nvar));
	my @lblst = unpack("Z$lenname" x $nvar, $buff);
	print $FH "valblListString:\n\t", join("|", @lblst), "\n\n" if $DEBUG;
	print $FH "after value label for each variable=" . tell($rfh) . "\n" if $DEBUG;

	$self->setValVrMpTbl(\@lblst);
	$self->{labelList}=[@lblst];
	$self->{fmtlst}=[@fmtlst];
	
	$self->setVarType(\@varTypeCode);
	
	return 0;
}


sub setVarType{
	my $self=shift;
	my $FH = $self->{_prcssOptn}->{LOGFILE};
	my $typeList=shift;
	#print $FH "type list=",@{$typeList},"\n";
	for (my $i=0;$i<@{$typeList};$i++){
		my $vn = $self->{_varNameA}->[$i];
		if ( ($typeList->[$i] eq 'f') || ($typeList->[$i] eq 'd')){
			if ($self->{fmtlst}->[$i]=~/^[%][d]/) {
				$self->{_varType}->{$vn}=0;
				$self->{_charVarTbl}->{$vn}=$i;
				$self->{_noCharVar}++;
				
			} else {
				$self->{_dcmlVarTbl}->{$vn}='F';
				$self->{_varType}->{$vn}=2;
			}
		} elsif (($typeList->[$i] eq 'l') || ($typeList->[$i] eq 'i') || ($typeList->[$i] eq 'b')){
			$self->{_varType}->{$vn}=1;
		} else {
			$self->{_charVarTbl}->{$vn}=$i;
			$self->{_varType}->{$vn}=0;
		}
	}
}

sub setVarTypeOld{
	my $self=shift;
	my $fmtlst=shift;
	my $noCharVar=0;
	for (my $i=0;$i<@{$fmtlst};$i++){
		my $vn = $self->{_varNameA}->[$i];
		my $type = substr($fmtlst->[$i], -1,1);
		if ($type eq 's'){
			$noCharVar++;
			$self->{_charVarTbl}->{$vn}=$i;
			$self->{_varType}->{$vn}=0;
		} else {
			$self->{_varType}->{$vn}=1;
		}
	}
	$self->{_noCharVar}=$noCharVar;
}


sub setValVrMpTbl{
	my $self=shift;
	my $lblst=shift;
	for (my $i=0;$i<@{$lblst};$i++){
		if ($lblst->[$i]){
			my $vn = $self->{_varNameA}->[$i];
			$self->{_valVrMpTbl}->{$vn}=$lblst->[$i];
		}
	}

}

sub read_VariableLabels {
	my $self=shift;
	my $rfh  = $self->{RFL};
	my $DEBUG=1;
	my $FH = $self->{_prcssOptn}->{LOGFILE};
	my ($buff);
	# 3. Variable Labels (var label names);
	print $FH "before variable label=" . tell($rfh) . "\n" if $DEBUG;

	my $lenlbl  = $RLSPARAMS->{$releaseNo}->{lengthLabel};
	read($rfh, $buff, $lenlbl * $nvar);
	my @varlblst = unpack("Z$lenlbl" x $nvar, $buff);
	print $FH "varlblst:\n\t", join("\n\t", @varlblst), "\n\n" if $DEBUG;
	
	print $FH "after variable label=" . tell($rfh) . "\n" if $DEBUG;

	for (my $i=0;$i<@varlblst; $i++){
		my $vn = $self->{_varNameA}->[$i];
		$self->{_varLabel}->{$vn}=$varlblst[$i];
	}
	return 0;
}

sub read_ExpansionFields{
	my $self=shift;
	my $rfh  = $self->{RFL};
	my $DEBUG=1;
	my $FH = $self->{_prcssOptn}->{LOGFILE};
	my ($buff);
	print $FH "before expansion field=" . tell($rfh) . "\n" if $DEBUG;

	# 4. Expansion Fields
	my $ii = 0;
	my @expfld;
	my $lenef= $RLSPARAMS->{$releaseNo}->{lengthExpFld};
	for (; ;) {
		read($rfh, $buff, ($lenef + 1));
		$ii++;
		if ($lenef == 2) {
			@expfld = unpack("c${int16}", $buff);
		} else {
			@expfld = unpack("c${int32}", $buff);
		}

		print $FH "expfld ($ii):", join("|",@expfld), "\n\n" if $DEBUG;

		if (!($expfld[0] + $expfld[1])) {
			if ($ii == 1) {
				print $FH "The expansion field of this file is blank.\n" if $DEBUG;
			}
			print $FH "The end of expansion field\n\n" if $DEBUG;
			last;
		} else {
			read($rfh, $buff, $expfld[1]);
		}
	}
	print $FH "after expansion field=" . tell($rfh) . "\n" if $DEBUG;
	return 0;
}

sub read_Data {
	my $self=shift;
	my $rfh  = $self->{RFL};
	my $tfh  = $self->{TFL};

	my $DEBUG=0;
	my $FH = $self->{_prcssOptn}->{LOGFILE};
	my ($buff);
	print $FH "before data field=" . tell($rfh) . "\n" if $DEBUG;
	# 5. data
	
	my @unpackTemplate = @{$self->{unpackTemplate}};
	my $unpackTmplt;
	my $unpackTmpltbk;
	if ($bytrvrs) {
		$unpackTmplt   = join("", reverse(@unpackTemplate));
		$unpackTmpltbk = join("", @unpackTemplate);
		print $FH "reversed tmpltst:", $unpackTmplt, "\n" if $DEBUG;
		print $FH "tmpltst for string vars:", $unpackTmplt, "\n" if $DEBUG;
	} else {
		$unpackTmplt = join("", @unpackTemplate);
		print $FH "         tmpltst:", $unpackTmplt, "\n" if $DEBUG;
	}

	my $chr_na = 'NA';
	my $nmr_na = 'NA';
	my @tmpdata;
	my @tmpdatabk;
	my @dtmtrx;
	my $nOBS   = $self->{_fileDscr}->{nOBS};
	my $nobs   = $self->{_fileDscr}->{caseQnty};
	my $noVars = $self->{_fileDscr}->{varQnty};
	my $noCharVar=$self->{_noCharVar};
	my $bo4charVar = $self->{byteOffset4charVar};
	my $dateAdj={};
	for (my $i=0;$i<@{$self->{varTypeCode}}; $i++){
		if ($self->{fmtlst}->[$i]=~/^[%][d]/){
			print $FH "date variable is found at",$i,"-th position\n" if $DEBUG;
			$dateAdj->{"$i"}= 'y';
		}
	}
	print $FH Dumper($dateAdj) if $DEBUG;
	for (my $i = 0 ; $i < $nobs ; $i++) {
	
		read($rfh, $buff, $nOBS);
		if ($bytrvrs) {
			@tmpdata = reverse(unpack($unpackTmplt, reverse($buff)));
			
			# the following block for string variables is necessary for
			# the reversal case to deal with space-padded cases such as
			# [x20][x20][x00][ASU] ==> contents = '   ' not 'ASU'
			# The order of character bytes of a string datum 
			# is endian-neutoral 
			
			if ($noCharVar){
				@tmpdatabk = unpack($unpackTmpltbk, $buff);
				foreach my $k (keys %{$bo4charVar}){
					my $kx = $k+0;
					$tmpdata[$kx] = $tmpdatabk[$kx];
				}
				@tmpdatabk=();
			}
		} else {
			@tmpdata = unpack($unpackTmplt, $buff);
		}

		print $FH "raw data: obs # = $i\t", join("|", @tmpdata), "\n\n" if $DEBUG;
		
		# little endian Hex and decimals: missing values
		# string: ""
		# byte:   0x7f = 127
		# int :   0xff7f = 32,767
		# long:   0xffffff7f = 2,147, 483, 647
		# float:  0x0000007f = 2**127
		# double: 0x000000000000e07f = 2**1023
		my $dblmax = $RLSPARAMS->{$releaseNo}->{dblMaxValue};
		for (my $j = 0 ; $j <= $#tmpdata ; $j++) {
			if ($self->{varTypeCode}->[$j] eq "s") {
				if (!$tmpdata[$j]) {
					$tmpdata[$j] = $chr_na;
				} elsif ($tmpdata[$j]) {
					# if (ord(substr($tmpdata[$j],0,1)) == 46) # this may not work if the position of a period is other than 0.
					# replace non-space white characters with a space
					$tmpdata[$j] =~ s/[\t\r\f\n]+/ /g;
					$tmpdata[$j] =~ s/^\s+//;
					$tmpdata[$j] =~ s/\s+$//;
					(my $tmpprd = $tmpdata[$j]) =~ s/ //g;
					
					print $FH "obs # = $i\t before na check=", $tmpdata[$j], "\n" if $DEBUG;
					if (ord($tmpprd) == 46) {
						$tmpdata[$j] = $chr_na;
						print $FH "obs # = $i\t (string: '.'=na)=", $tmpdata[$j], "\n" if $DEBUG;
					} else {
						$tmpdata[$j] = $tmpdata[$j];
						print $FH "obs # = $i\t (string: not na)=", $tmpdata[$j], "\n" if $DEBUG;
					}
				}
			} elsif ($self->{varTypeCode}->[$j] eq "b") {
				if ($releaseNo >= 111) {
					if ($tmpdata[$j] >= (127-26)) {$tmpdata[$j] = $nmr_na;}
				} else {
					if ($tmpdata[$j] == 127) {$tmpdata[$j] = $nmr_na;}
				}
			} elsif ($self->{varTypeCode}->[$j] eq 'i') {
				if ($releaseNo >= 111) {
					if ($tmpdata[$j] >= (32767-26)) {$tmpdata[$j] = $nmr_na;}
				} else {
					if ($tmpdata[$j] == 32767) {$tmpdata[$j] = $nmr_na;}
				}
			} elsif ($self->{varTypeCode}->[$j] eq 'l') {
				if ($releaseNo >= 111) {
					if ($tmpdata[$j] >= (2147483647-26)) {$tmpdata[$j] = $nmr_na;}
				} else {
					if ($tmpdata[$j] == 2147483647) {$tmpdata[$j] = $nmr_na;}
				}
			} elsif ($self->{varTypeCode}->[$j] eq 'd') {
				if ($tmpdata[$j] == $dblmax) {
					$tmpdata[$j] = $nmr_na;
				} else {
					$tmpdata[$j] = sprintf "%g", (sprintf "%.14e", $tmpdata[$j]);
				}
			} elsif ($self->{varTypeCode}->[$j] eq 'f') {
				if ($tmpdata[$j] == (2**127)) {
					$tmpdata[$j] = $nmr_na;
				} else {
					if ((exists($dateAdj->{"$j"})) && ($dateAdj->{"$j"} eq 'y')){
						$tmpdata[$j] = &getDateString($tmpdata[$j]);
					} else {
						$tmpdata[$j] = sprintf "%g", (sprintf "%.7e", $tmpdata[$j]);
					}
					
				}
			} else {
				next;
			}
		}	# end of missing value processing
		# tab-delimited data to an external file
		print $FH "final: obs # = $i\tbyte=",tell($rfh),"\t",join("|", @tmpdata) , "\n\n" if $DEBUG;
		print $tfh join("\t", @tmpdata),"\n";
		
		if (eof $rfh) {
			print $FH "The end of this file has been detected.\nThis file does not have value label fields\n" if $DEBUG;
			return 1;
		}
	}
	print $FH "after data=" . tell($rfh) . "\n" if $DEBUG;
	return 0;
}

sub getDateString{
	my $days = shift;
	my $DEBUG=0;
	print "days=",$days, "\n" if $DEBUG;
	# stata date origin: 1960-1-1
	my $seconds = $days*24*60*60 + 11903760000;
	print "seconds=",$seconds,"\n" if $DEBUG;
	my $isoDateTime = &VDC::DSB::Ingest::StatData::findGregorianDate($seconds,'D');
	return $isoDateTime;
}

sub read_ValueLabelsRel105 {
	my $self=shift;
	my $rfh  = $self->{RFL};
	my $DEBUG=1;
	my $FH = $self->{_prcssOptn}->{LOGFILE};
	my $lenname= $RLSPARAMS->{$releaseNo}->{lengthName};
	my ($buff);

	print $FH "section 6:105\n" if $DEBUG;
	my (@lbindx, @lbindy, @labnm, @valarray, @valmtrx, @txtarray, @txtmtrx, @tblsize);
	my ($lenname8);
	my @lblst= @{$self->{labelList}};
	my $valLblTbl={};
	print $FH "before value label=" . tell($rfh) . "\n" if $DEBUG;

	# Release 5 or earlier
	for (my $i = 0 ; $i < $nvar ; $i++) {
		print $FH "top of " . $i . "-th value label=" . tell($rfh) . "\n" if $DEBUG;

		# number of entries in this variable
		read($rfh, $buff, 2);
		my $nentrs = unpack("${int16}", $buff);
		print $FH "nmbr of entries:", $nentrs, "\n" if $DEBUG;

		# label name
		read($rfh, $buff, $lenname + 1);
		my $labname = unpack("Z$lenname", $buff);
		print $FH "label name:",$labname,"\n" if $DEBUG;

		for (my $j = 0 ; $j < $nvar ; $j++) {
			if ($lblst[$j] eq $labname) {
				$lbindx[$j] = $nentrs;
				$labnm[$j]  = $labname;
				$lbindy[$j] = $i;

				#lbindz[$i] = $j;
				#$lbentr[$i] = $nentrs;
			}
		}

		# value list
		read($rfh, $buff, 2 * $nentrs);
		@valarray = unpack("${int16}$nentrs", $buff);
		my $tmpvalu = join('|', @valarray);
		print $FH "sorted value list:",$tmpvalu,"\n" if $DEBUG;

		push @valmtrx, [@valarray];

		# label txt list
		$lenname8 = $lenname - 1;
		read($rfh, $buff, $lenname8 * $nentrs);
		@txtarray = unpack("Z$lenname8" x $nentrs, $buff);

		my $tmpvaltxt = join('|', @txtarray);
		print $FH "label txt list:",$tmpvaltxt,"\n\n" if $DEBUG;


		$valLblTbl->{$labname}=[];
		for (my $k=0;$k<$nentrs;$k++){
			push @{$valLblTbl->{$labname}}, [$valarray[$k],$txtarray[$k], undef, undef];
		}

		push @txtmtrx, [@txtarray];


		if (eof $rfh) {
			$self->{_valLblTbl}=$valLblTbl;
			print $FH "\nvalLblTbl:\n",Dumper($valLblTbl) if $DEBUG;
			last;
		}
	}
	if ($DEBUG){
		print $FH "lablabel(updated):", join(",", @labnm),  "\n";
		print $FH "labindex:         ", join(",", @lbindx), "\n";
		print $FH "labindey:         ", join(",", @lbindy), "\n";
	}
}

sub read_ValueLabelsRel108 {
	my $self=shift;
	my $rfh  = $self->{RFL};
	my $DEBUG;
	my $FH = $self->{_prcssOptn}->{LOGFILE};
	my $lenname= $RLSPARAMS->{$releaseNo}->{lengthName};
	my ($buff);

	print $FH "section 6:108\n" if $DEBUG;
	my (@lbindx, @lbindy, @labnm, @valarray, @valmtrx, @txtarray, @txtmtrx, @tblsize);
	my ($lenname8);
	print $FH "before value label=" . tell($rfh) . "\n" if $DEBUG;

	# Release 6 or later
	my ($ttl, $labname, $nentrs, $lnttbl);
	my @lblst= @{$self->{labelList}};
	my $valLblTbl={};
	for (my $i = 0 ; $i < $nvar ; $i++) {
		# label_table length
		print $FH "top of " . $i . "-th value label=" . tell($rfh) . "\n" if $DEBUG;
		
		read($rfh, $buff, 4);
		$tblsize[$i] = unpack("${int32}", $buff);
		$ttl = $tblsize[$i] + 4 + $lenname + 3;
		print $FH "\ntblsize(total)   : (", $i, ")\t", $ttl, "\n" if $DEBUG;

		# label name plus padding
		read($rfh, $buff, $lenname + 3);
		$labname = unpack("Z$lenname", $buff);
		print $FH "label name:", $labname, "\n" if $DEBUG;

		# number of entries in this variable/length of txt[]
		read($rfh, $buff, 4);
		$nentrs = unpack("${int32}", $buff);
		print $FH "nmbr of entries:", $nentrs, "\n" if $DEBUG;

		for (my $j = 0 ; $j < $nvar ; $j++) {
			if ($lblst[$j] eq $labname) {
				$lbindx[$j] = $nentrs;
				$labnm[$j]  = $labname;
				$lbindy[$j] = $i;
			}
		}

		# length of txt[]
		read($rfh, $buff, 4);
		$lnttbl = unpack("${int32}", $buff);
		print $FH "length of txt table:", $lnttbl, "\n" if $DEBUG;

		# offset(tab) nmbrs of txt[]
		read($rfh, $buff, 4 * $nentrs);
		my $tmplctn = join('|', unpack("${int32}$nentrs", $buff));
		print $FH "start column nmbr of each label txt:", $tmplctn, "\n" if $DEBUG;

		# value list
		read($rfh, $buff, 4 * $nentrs);
		@valarray = unpack("${int32}$nentrs", $buff);
		my $tmpvalu = join('|', @valarray);

		#print $FH @valarray, "\n";
		print $FH "sorted value list:", $tmpvalu, "\n" if $DEBUG;

		push @valmtrx, [@valarray];

		# text list
		read($rfh, $buff, $lnttbl);
		@txtarray = split /\0/,join('', map(chr, unpack("c*" x $nentrs, $buff)));

		my $tmpvaltxt = join('|', @txtarray);

		#print $FH @txtarray, "\n";
		print $FH "value text list:", $tmpvaltxt, "\n\n" if $DEBUG;
		
		
		
		$valLblTbl->{$labname}=[];
		for (my $k=0;$k<$nentrs;$k++){
			push @{$valLblTbl->{$labname}}, [$valarray[$k],$txtarray[$k], undef, undef];
		}
		push @txtmtrx, [@txtarray];

		if (eof $rfh) {
			$self->{_valLblTbl}=$valLblTbl;
			print $FH "\nvalLblTbl:\n",Dumper($valLblTbl) if $DEBUG;
			last;
		}
	}
	if (0){
		print $FH "lablabel(updated):", join(",", @labnm),  "\n";
		print $FH "labindex:         ", join(",", @lbindx), "\n";
		print $FH "labindey:         ", join(",", @lbindy), "\n";
	}
}


sub getVarTypeSet{
	my $self = shift;
	my $vartype = $self->{_varType};
	my $varname = $self->{_varNameA};
	my $novar   = $self->{_fileDscr}->{varQnty};
	my $varcode=[];
	for (my $i=0;$i<$novar;$i++){
		#if ($self->{fmtlst}->[$i]=~/^[%][d]/){
		#	$varcode->[$i]=0;
		#	
		#} else {
			$varcode->[$i]=$vartype->{$varname->[$i]};
		#}
	}
	#print "varcode\n",Dumper($varcode);
	return $varcode;
}







1;


__END__

=head1 NAME

VDC::DSB::Ingest::DTA - Stata (dta) file reader

=head1 DEPENDENCIES

=head2 Nonstandard Modules

    VDC::DSB::Ingest::StatData

=head1 DESCRIPTION

VDC::DSB::Ingest::DTA parses a STATA dta file and returns parsing results (metadata) as a reference to a hash.  This module is called by VDC::DSB::Ingest::StatDataFileReaderFactory and is used with VDC::DSB::Ingest::StatData.


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

