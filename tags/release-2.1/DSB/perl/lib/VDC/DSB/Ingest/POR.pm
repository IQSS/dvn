package VDC::DSB::Ingest::POR;

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
@VDC::DSB::Ingest::POR::ISA= qw(VDC::DSB::Ingest::StatData);
use IO::File;
use File::Copy;
use Data::Dumper;

# package variables
my $TMPDIR=File::Spec->tmpdir();

# hash(lookup) table base30tobase10 conversion table
my %baset2dtbl;
@baset2dtbl{0..9,'A'..'T'} = 0..30;

my $MVstring="NA";

my $POR_FRMT_CODE_TABLE={
'0'=>'CONTINUE','1'=>'A','2'=>'AHEX','3'=>'COMMA','4'=>'DOLLAR',
'5'=>'F','6'=>'IB','7'=>'PIBHEX','8'=>'P','9'=>'PIB',
'10'=>'PK','11'=>'RB','12'=>'RBHEX','15'=>'Z','16'=>'N',
'17'=>'E','102'=>'DATE','103'=>'TIME','104'=>'DATETIME','105'=>'ADATE',
'106'=>'JDATE','107'=>'DTIME','108'=>'WKDAY','109'=>'MONTH','110'=>'MOYR',
'111'=>'QYR','112'=>'WKYR','113'=>'PCT','114'=>'DOT','115'=>'CCA',
'116'=>'CCB','117'=>'CCC','118'=>'CCD','119'=>'CCE','120'=>'EDATE','121'=>'SDATE'};

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

my $FMT_MAP_TABLE={
'CONTINUE'=>'other','A'=>'other','AHEX'=>'other','COMMA'=>'other',
'DOLLAR'=>'currency','F'=>'other','IB'=>'other','PIBHEX'=>'other',
'P'=>'other','PIB'=>'other','PK'=>'other','RB'=>'other','RBHEX'=>'other',
'Z'=>'other','N'=>'other','E'=>'other','DATE'=>'D','TIME'=>'T',
'DATETIME'=>'DT','ADATE'=>'D','JDATE'=>'D','DTIME'=>'JT',
'WKDAY'=>'other','MONTH'=>'other','MOYR'=>'D','QYR'=>'D',
'WKYR'=>'D','PCT'=>'other','DOT'=>'other','CCA'=>'currency',
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
		my $logfile = $TMPDIR  . '/por.' . $$ . '.log';
		$FH = new IO::File("> $logfile");
		$self->{_prcssOptn}->{LOGFILE} =$FH;
		print $FH "default log file name will be used:", $logfile, "\n" if $DEBUG;
	} else{
		$self->{_prcssOptn}->{LOGFILE}= $FH = $args->{LOGFILE};
		print $FH "user-supplied log file name will be used:\n" if $DEBUG;
	}
	
	print $FH "args:\n", Dumper($args) if $DEBUG;
	
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
	$self->{_fileDscr}->{fileType} = 'application/x-spss-por';
	
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
	
	my $rfh = new IO::File;
	my $newporfile = $TMPDIR  . "/tmp.$$.por";
	$self->{_prcssOptn}->{tmpPORfile}=$newporfile;
	my $io = new IO::File("> $newporfile");

	my $fieldcode2Method ={
		1=>'productName', 2=>'licensee', 3=>'fileLabel',
		4=>'NofVars', 5=>'fieldNo5', 6=>'weightVar',
		7=>'VarInfo', 8=>'MissValuePoint', 9=>'MissValueRangeLO',
		A=>'MissValueRangeHI', B=>'MissValueRangeOrd', C=>'VarLabel',
		D=>'ValueLabel', E=>'Document', F=>'Data', '8S' => 'MissValuePointS', 
	};

	# step 0: strip new line character(s) and first 5 lines
	if ($rfh->open("< $datafile")) {
		if (defined $io) {
			&stripNewLines(RFL=>$rfh, WFL=>$io);
			$io->close();
		}
		$rfh->close();
	} else {
		die "Failed to open the por file (", $datafile, "): wrong filename or pathname?\n";
	}

	my $tfh = new IO::File("> $tabfile");

	$self->{RFL}=$io;
	$self->{TFL}=$tfh;
	my $timeset={};
	my ($start, $end);
	if ($io->open("< $newporfile")){
		# step 1: parse the pre-processed por file before field '1'
		$self->read_Sec2();
		
		# setp 2: parse the pre-processed port file up to 'F' field
		my ($method4field, $fieldcode);
		while(1) {
		$start=time() if $DEBUG;
			# read the first char [field code]
			read($io, $fieldcode, 1);
			print $FH "\n///////// fieldcode=",$fieldcode," /////////\n" if $DEBUG;
			print $FH "current byte offset[$fieldcode]=",tell $io,"\n"  if $DEBUG ;
			
			#if (ord($fieldcode) > 70) {last;} # 54=> 6 55=>'7' 67 ='C' 70 = 'F'
			if ( ($fieldcode eq '8') && ($self->getCrrntVarType()) ){
				$fieldcode='8S';
			}
			if ($fieldcode eq 'Z'){
				print $FH "The parsing process cannot proceed because this file lacks the data section.\nCheck the integrity of this spsss-portable file.\n";
				print STDERR "The pasring process cannot proceed due to the lack of a data section.\nCheck the integrity of this spsss-portable file.\n";
				die "The process was killed due to the lack of a data section: ";
			}
			print $FH "recordType=",$fieldcode,"\trecordType2Method=",$fieldcode2Method->{$fieldcode},"\n" if $DEBUG;
			
			$method4field = "read_" . $fieldcode2Method->{$fieldcode};
			$self->$method4field();
			$end=time() if $DEBUG;
			$timeset->{$fieldcode}+=($end - $start) if $DEBUG;
			if ($fieldcode eq 'F') {last;}
		}
	}
	
	print $FH "por:seconds spent:\n",Dumper($timeset),"\n" if $DEBUG;
	
	$rfh->close();
	$tfh->close();
	# post-parsing processing
	$self->setMVtypeCode();
	$self->setmvVrMpTbl();
	copy($tabfile,"$base.tab");	
	$io->close();
	#print $FH "self(final:por):\n",Dumper($self),"\n" if $DEBUG;
}

sub getCrrntVarType {
	my $self=shift;
	my $optn ={@_};
	my $var = $self->{_currentVar};
	return $self->{_varType}->{$var};
}


sub getVarTypeList{
	my $self=shift;
	my $optn ={@_};
	my $vartyplist = [];
	foreach my $var (@{$self->{_varNameA}}){
		push @$vartyplist, $self->{_varType}->{$var};
	}
	return $vartyplist;
}

sub DESTROY{
	my $self =shift;
	#unlink($self->{_prcssOptn}->{SMSTTFILE});
	unlink($self->{_prcssOptn}->{tmpPORfile});
}


# /////////////////////////////////////////////////////////
# Method for each field
# /////////////////////////////////////////////////////////




# /////////////////////////////////////////////////////////////////////
# This sub provides parsing method for a numeric field 
# '[fieldcode]m/'  
# where m is a base-30 value that needs base30tobase10 conversion
# /////////////////////////////////////////////////////////////////////

sub parseNumericField{
	my $rfh  = shift;
	#print "\n\nentering parseNumericField\n";
	my $buff;
	my $fieldvalueT ='';
	until ($buff eq '/') {
		$fieldvalueT .= $buff;
		read($rfh, $buff, 1);
		#print "buff:", $buff, "\n";
		#print "lenfieldT:", $fieldvalueT, "\n";
	}
	#print "fieldvalue(base 30):", $fieldvalueT, "\n";
	my $lenfieldvalueT= length($fieldvalueT);
	if ($lenfieldvalueT > 32){
		print STDERR "length of a parsed segment to be converted to a base-10 value is ",$lenfieldvalueT," (more than 32)\n";
		print STDERR "parsing error is highly suspected\n";
		die "terminated within parseNumericField";
	}
	
	my $fieldvalueA = &base30tobase10(\$fieldvalueT);
	#print "fieldvalue(base 10):", $fieldvalueA, "\n";
	#print "current byte offset=",tell $rfh, "\n";
	return $fieldvalueA;
}

# /////////////////////////////////////////////////////////////////////
# This sub provides parsing method for a string field 
# '[fieldcode]m/n'  
# where m is a base-30 value that needs base30tobase10 conversion,
# and  n is a string whose length is given by m
# /////////////////////////////////////////////////////////////////////

sub parseStringField{
	my $rfh  = shift;
	#print "\nentering parseStringField\n";
	
	my @buff=();
	my $lenfieldT ='';
	until ($buff[0] eq '/') {
		$lenfieldT .= $buff[0];
		read($rfh, $buff[0], 1);
		#print "buff:", $buff[0], "\n";
		#print "lenfieldT:", $lenfieldT, "\n";
	}
	#print "lenfieldT:", $lenfieldT, "\n";
	# remove leading/trailing 0s if exist
	#$lenfieldT =~ s/^(0+|\s+)//;

	#print "fieldvalue Length (base 30):", $lenfieldT, "\n";
	#my $lenfieldA = &base30tobase10(\$lenfieldT);
	#my $lenfieldA = &basetINT2basedINT(\$lenfieldT);
	my $lenfieldA = &basetINT2basedINTunsgn(\$lenfieldT);
	
	#print "fieldvalue Length (base 10):", $lenfieldA, "\n";
	if ($lenfieldA > 256){
		print STDERR "length of a label to be parsed is ",$lenfieldA," (more than 256)\n";
		print STDERR "parsing error is highly suspected\n";
		die "terminated within parseStringField";
	}
	read($rfh, $buff[1], $lenfieldA);
	my $fieldvalue = unpack("Z$lenfieldA", $buff[1]);
	#print "fieldvalue:", $fieldvalue,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	return $fieldvalue;
}


sub read_Sec2{
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};

	### Header Section: part 1 (File Identification One Character) ###
	# parameters
	my $rawheader;
	my $offset; 		# 56 bytes;
	my $fileSig;		#  8;
	my $unknown;		#  1;
	my $len_filedate; 	#  1;
	my $filedate;		#  8;
	my $len_filetime;	#  1;
	my $filetime;		#  6;
	my @sep=(undef,undef);#1 each;
	my $len_rawheader = 56 + 8 + 1 + 1 + 1 + 8 + 1 + 1 + 6;
	read($rfh, $rawheader, $len_rawheader);
	#print "length(rawheader)=",$len_rawheader,"\n";
	my $template = "Z56Z8ZaCZ8aCZ6";
	#print "template=",$template,"\n";
	($offset, $fileSig, $unknown, $len_filedate, $sep[0],  $filedate, $len_filetime, $sep[1], $filetime)= unpack($template, $rawheader);
	
	if ( ($sep[0] eq 0x2F) && ($sep[1] eq 0x2F)  ){
		# date/time field separators '/' are correctly located
		if ($DEBUG){
			print $FH "offset=",$offset,"\n";
			print $FH "fileSig=",$fileSig,"\n";
			print $FH "unknown=",$unknown,"\n";
			print $FH "Date=",$filedate,"\n";
			print $FH "Time=",$filetime,"\n";
		}
		
		if ($fileSig eq "SPSSPORT"){
			print $FH "file signature SPSSPORT was found\n" if $DEBUG;
		} else {
			print $FH "unpacking unsuccessful: file signature SPSSPORT was not found at its expected position\n" if $DEBUG;
			print $FH "contents=",$fileSig,"\n" if $DEBUG;
			die "unpacking unsuccessful: file signature SPSSPORT was not found at its expected position : $!";
		}
	} else {
		print $FH "unpacking unsuccessful: field separators are not found at their expected positions\n" if $DEBUG;
		print $FH "contents:1st=",$sep[0],"\t2nd=",$sep[1],"\n" if $DEBUG;
		# this situation would happen if implicit assumptions in the above unpacking template no longer hold true:
		# more especially, changes to the byte-length of $unknown or $filedate or $filetime
		die "__PACKAGE__ : $!";
	}
	#print "current byte offset[porSec2]=", tell $rfh, "\n";
}


# //////////////////////////////////////////////////////////
# field 1 Product Name
# //////////////////////////////////////////////////////////

sub read_productName{
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $fieldvalue = &parseStringField($rfh);
	#print "fieldvalue:", $fieldvalue,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	$self->{_fileDscr}->{productName}=$fieldvalue;
}

# //////////////////////////////////////////////////////////
# field 2 licensee
# //////////////////////////////////////////////////////////

sub read_licensee{
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $fieldvalue = &parseStringField($rfh);
	#print "fieldvalue:", $fieldvalue,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	$self->{_fileDscr}->{licensee}=$fieldvalue;
}

# //////////////////////////////////////////////////////////
# field 3 file label
# //////////////////////////////////////////////////////////

sub read_fileLabel{
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $fieldvalue = &parseStringField($rfh);
	#print "fieldvalue:", $fieldvalue,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	$self->{_fileDscr}->{fileLabel}=$fieldvalue;
}

# //////////////////////////////////////////////////////////
# field 4 No of vars
# //////////////////////////////////////////////////////////

sub read_NofVars{
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $buff;
	my $fieldvalueT ='';
	until ($buff eq '/') {
		$fieldvalueT .= $buff;
		read($rfh, $buff, 1);
		#print "buff:", $buff, "\n";
		#print "lenfieldT:", $fieldvalueT, "\n";
	}
	#print "fieldvalue(base 30):",$fieldvalueT,"\n";
	#
	# remove leading 0s if exist (e.g.,DBMS/COPY FOR IBM PC)
	# SPSS products do not padd leading 0s in this field
	#
	my $len = length($fieldvalueT);
	#print "length=",$len,"\n";
	$fieldvalueT =~ s/^((0+)|(\s+))//;
	#print "hit string(0)=",$2,"\n";
	#print "hit string( )=",$3,"\n";

	#print "hit string(len:0)=",length($2),"\n";
	#print "fieldvalueT(A1)=",$fieldvalueT,"\n";
	if ( ($fieldvalueT eq '') && (length($2) eq $len) ) {
		$fieldvalueT='0';
	}
	#print "fieldvalueT(A2)=",$fieldvalueT,"\n";

	my $fieldvalueA = &base30tobase10(\$fieldvalueT);
	#print "fieldvalue(base 10):", $fieldvalueA, "\n";

	#print "current byte offset=",tell $rfh, "\n";

	$self->{_fileDscr}->{varQnty}=$fieldvalueA;
}


# //////////////////////////////////////////////////////////
# field 5
# //////////////////////////////////////////////////////////

sub read_fieldNo5{
	my $self = shift;
	my $rfh  = $self->{RFL};
	
	my $fieldvalueA = &parseNumericField($rfh);
	#print "fieldvalue:", $fieldvalueA,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	$self->{_fileDscr}->{fieldNo5}=$fieldvalueA;
}

# //////////////////////////////////////////////////////////
# field 6 Weight var
# //////////////////////////////////////////////////////////

sub read_weightVar{
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $fieldvalue = &parseStringField($rfh);
	#print "fieldvalue:", $fieldvalue,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	$self->{_fileDscr}->{weightVar}=$fieldvalue;
}


# //////////////////////////////////////////////////////////
# field 7 Variable Info
# //////////////////////////////////////////////////////////

sub read_VarInfo{
	my $self = shift;
	my $rfh  = $self->{RFL};

	# my $varinfo=[];
	my $varinfo={};
	# $varinfo->{name}={
	#	varType=> undef,
	#	pwformat=> undef,
	# };
	# sub-field: variable type 
	# field type: numeric
	# [numeric:0; string:length(max 2 digits)]
	my $vartype = &parseNumericField($rfh);
	#print "variable type=",$vartype,"\n";
	# sub-field: variable Name 
	# field type: string
	#$varinfo->[0]=$vartypeA;
	my $varname= &parseStringField($rfh);
	#print "variable name=",$varname,"\n";
	#$varinfo->[1]=$varname;
	#push @$rfvarnamelst, $varname;

	# sub-field: print format
	# field type: numeric
	# print format [Type/Width/max Decimal places]
	# Type: max 2 digits (40 = 1A)
	# Width: max 2 digits (254 = 8E)
	# Decimal places: max 1 digit (max 16 = G)
	
	my $pwfrmt=[];
	for (my $i=0; $i<6; $i++){
		$pwfrmt->[$i]= &parseNumericField($rfh);
	}
	my $varpwfrmt = join("|", @{$pwfrmt});
	#print "p/w format=",$varpwfrmt,"\n";
	
	#$varinfo ={
	#	varname => $varname,
	#	vartype => $vartype,
	#	varpwfrmt=>$varpwfrmt,
	#};
	#return $varinfo;
	
	
	my $novars = scalar(@{$self->{_varNameA}});
	push @{$self->{_varNameA}}, $varname;
	$self->{_varNameH}->{$varname}=$novars;
	$self->{_varType}->{$varname}=$vartype;
	
	if (exists($POR_FRMT_CODE_TABLE->{ $pwfrmt->[0] })){
		$self->{_varFormat}->{$varname}=$POR_FRMT_CODE_TABLE->{ $pwfrmt->[0] };
		if (($pwfrmt->[0] eq '0') || ($pwfrmt->[0] eq '1') || ($pwfrmt->[0] eq '5')){
		#} elsif (($pwfrmt->[0] eq '108') || ($pwfrmt->[0] eq '109')){
			
		} else {
			my $d='';
			if ($pwfrmt->[2]){
				$d='.' . $pwfrmt->[2];
			}
			$self->{_formatName}->{$varname}= $POR_FRMT_CODE_TABLE->{ $pwfrmt->[0] } . $pwfrmt->[1] . $d ;
		}
	}

	$self->{_varPWformat}->{$varname}=$varpwfrmt;
	$self->{_currentVar}=$varname;
	my $chrcntr=0;
	if (($vartype) || ($pwfrmt->[0] eq '108')|| ($pwfrmt->[0] eq '109')){
		# string-type => $vartype >0
		$self->{_charVarTbl}->{$varname}='a';
		$chrcntr++;
	}
	$self->{_noCharVar} += $chrcntr;
	
}

# //////////////////////////////////////////////////////////
# field 8 Missing Value: Point type (numeric)
# //////////////////////////////////////////////////////////

sub read_MissValuePoint{
	my $self = shift;
	my $rfh  = $self->{RFL};
	
	my $fieldvalueA = &parseNumericField($rfh);
	#print "fieldvalue:", $fieldvalueA,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	my $currentvar = $self->{_currentVar};
	if (!exists($self->{_mssvlTbl}->{$currentvar})){
		$self->{_mssvlTbl}->{$currentvar}=[];
	}
	push @{ $self->{_mssvlTbl}->{$self->{_currentVar}}},[$fieldvalueA];

}


# //////////////////////////////////////////////////////////
# field 8S Missing Value: Point type (String)
# //////////////////////////////////////////////////////////
sub read_MissValuePointS{
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $fieldvalue = &parseStringField($rfh);
	#print "fieldvalue:", $fieldvalue,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	
	my $currentvar = $self->{_currentVar};
	if (!exists($self->{_mssvlTbl}->{$currentvar})){
		$self->{_mssvlTbl}->{$currentvar}=[];
	}
	push @{$self->{_mssvlTbl}->{$self->{_currentVar}}},[$fieldvalue];
}


# //////////////////////////////////////////////////////////
# field 9 Missing Value: Range LO, *
# //////////////////////////////////////////////////////////
sub read_MissValueRangeLO{
	# Range type: field code = 9
	my $self = shift;
	my $rfh  = $self->{RFL};
	
	my $fieldvalueA = &parseNumericField($rfh);
	#print "fieldvalue:", $fieldvalueA,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	my $currentvar = $self->{_currentVar};
	if (!exists($self->{_mssvlTbl}->{$currentvar})){
		$self->{_mssvlTbl}->{$currentvar}=[];
	}
	push @{$self->{_mssvlTbl}->{$self->{_currentVar}}},['LOWEST',$fieldvalueA];
}


# //////////////////////////////////////////////////////////
# field A Missing Value: Range type
# //////////////////////////////////////////////////////////
sub read_MissValueRangeHI{
	my $self = shift;
	my $rfh  = $self->{RFL};
	
	my $fieldvalueA = &parseNumericField($rfh);
	#print "fieldvalue:", $fieldvalueA,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	my $currentvar = $self->{_currentVar};
	if (!exists($self->{_mssvlTbl}->{$currentvar})){
		$self->{_mssvlTbl}->{$currentvar}=[];
	}
	push @{$self->{_mssvlTbl}->{$self->{_currentVar}}},[$fieldvalueA, 'HIGHEST'];

}



# //////////////////////////////////////////////////////////
# field B Missing Value: Point/Range type
# //////////////////////////////////////////////////////////
sub read_MissValueRangeOrd{

	my $self = shift;
	my $rfh  = $self->{RFL};
	my $range =[undef, undef];
	$range->[0] = &parseNumericField($rfh);
	#print "fieldvalue:lower=", $range->[0],"\n";
		
	$range->[1] = &parseNumericField($rfh);
	#print "fieldvalue:upper=",  $range->[1],"\n";
	
	#print "current byte offset=",tell $rfh, "\n";
	my $currentvar = $self->{_currentVar};
	if (!exists($self->{_mssvlTbl}->{$currentvar})){
		$self->{_mssvlTbl}->{$currentvar}=[];
	}
	push @{$self->{_mssvlTbl}->{$self->{_currentVar}}}, $range;
}

# //////////////////////////////////////////////////////////
# field C var label
# //////////////////////////////////////////////////////////

sub read_VarLabel{
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $fieldvalue = &parseStringField($rfh);
	#print "fieldvalue:", $fieldvalue,"\n";
	#print "current byte offset=",tell $rfh, "\n";
	my $currentvar = $self->{_currentVar};
	if ($fieldvalue ne ' '){
		$self->{_varLabel}->{$currentvar}=$fieldvalue;
	}
}

# //////////////////////////////////////////////////////////
# field D Value/Value Label 
# //////////////////////////////////////////////////////////

sub read_ValueLabel {
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $arg  = $self->{_varType};

	my $valueLabel=[];
	
	# sub-field: number of vars in this set
	# field type: numeric
	my $novars = &parseNumericField($rfh);
	#print "no of vars in this set=",$novars,"\n";
	
	# sub-field: variable name set
	# field type: string
	my $varnameset =[];
	for (my $i=0; $i<$novars; $i++){
		$varnameset->[$i]= &parseStringField($rfh);
	}
	#print "varnameset=",join("|",@$varnameset),"\n";
	
	# sub-field: number of value-label sets follow
	# field type:numeric
	
	my $novaluelabelset= &parseNumericField($rfh);
	#print "no of value-label set follows=",$novaluelabelset,"\n";

	# sub-field: value-label sets
	# field type: numeric
	# field type: string
	
	my $valuelabelset=[];
	my $vllblset=[undef,undef,undef,undef];
	my $varType= $arg->{$varnameset->[0]};
	for (my $i=0; $i<$novaluelabelset; $i++){
		if ($varType){
			#print "++++++  value-label: String Var Case ++++++\n";
			# string var case
			$vllblset->[0] = &parseStringField($rfh);
		} else {
			# numeric var case
			$vllblset->[0] = &parseNumericField($rfh);
		}
		$vllblset->[1] = &parseStringField($rfh);
		push @$valuelabelset, $vllblset;
		$vllblset=[undef,undef,undef,undef];
	}
	#print "current byte offset[field: valueLabel]=",tell $rfh, "\n";
	#$valueLabel=[$varnameset, $valuelabelset];
	
	# $optn = [1st, 2nd];
	# 1st ref: varname set
	# 2nd ref: value-label set
	my $keyvar = $varnameset->[0];
	# set up a value-label table
	$self->{_valLblTbl}->{$keyvar}=$valuelabelset;
	# set up a key2member-var mapping table
	for (my $i=0;$i<@{$varnameset};$i++ ){
		$self->{_valVrMpTbl}->{$varnameset->[$i]}=$keyvar;
	}
	
}

# //////////////////////////////////////////////////////////
# field E Document
# //////////////////////////////////////////////////////////

sub read_Document{
	my $self = shift;
	my $rfh  = $self->{RFL};

	# sub-field: number of lines in this field
	# field type: numeric
	my $nolines = &parseNumericField($rfh);
	#print "no of lines in this field=",$nolines,"\n";
	
	# sub-field: comment lines
	# field type: string
	my $lines =[];
	for (my $i=0; $i<$nolines; $i++){
		$lines->[$i]= &parseStringField($rfh);
	}
	#print "comment lines:\n",join("\n",@$lines),"\n";
	#print "current byte offset[field: document]=",tell $rfh, "\n";
	$self->{_fileDscr}->{documnet}=$lines;
}


# //////////////////////////////////////////////////////////
# field F Data
# //////////////////////////////////////////////////////////
# last version before the introduction of the buffer

sub read_Data{
	my $self = shift;
	my $rfh  = $self->{RFL};
	my $tfh  = $self->{TFL};
	my $varType  = $self->getVarTypeList();
	my $dcml = {};
	my $timeset={};
	my ($start, $end);

	my $DEBUG=0;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	
	# sub-field: data
	# field type: mixed
	
	my $novars= $self->{_fileDscr}->{varQnty};
	my $vt = [(0) x $novars];

	print $FH "no of vars per case=",$novars,"\n" if $DEBUG;
	print $FH "var type set=",join("|", @$varType),"\n" if $DEBUG;
	my ($buff, $lendt, $lendtd, $sep,$datums, $datumt, $datumn,$MVyes );
	my ($intA, $nocases);
	my $j = 0;
	my $nodcml=0;
	$start=time() if $DEBUG;
	#$start=time();
	my $lastvar= $novars-1;
	my @mntssT=();
	my $casewiseRecord='';
	FBLOCK: while (1) {
	# number of cases is unkown
		# per-case processing according to the type of a variable
		$j++;
		for (my $i=0; $i<$novars; $i++) {
			if ($varType->[$i] > 0) {
			# string var cases
				$vt->[$i]=-1;
				$buff='';
				$lendt='';
				until ($buff eq '/'){
					$lendt .= $buff;
					read($rfh, $buff, 1);
					if (eof $rfh) {
						print $FH "\n\nTerminal string:",$lendt ,"\n" if $DEBUG;
						$nocases = $j-1;
						print $FH "\nn of cases:", $nocases , "\n" if $DEBUG;
						last FBLOCK;
					}
					print $FH "buff:", $buff, "\n" if $DEBUG;
					print $FH "lendt:", $lendt, "\n" if $DEBUG;
				}
				print "$j-th case $i-th var: datum_length(base-30)", $lendt, "\n" if $DEBUG;
				
				# length is always a positive integer
				if (length($lendt)==1) {
					$lendtd  = $baset2dtbl{$lendt};
				} elsif (length($lendt)==2)  {
					@mntssT = split(//, $lendt);
					$lendtd = $baset2dtbl{$mntssT[1]} + ($baset2dtbl{$mntssT[0]})*30;
					#$lendtd  = &basetINT2basedINTunsgn(\$lendt);
				} else {
					print STDERR "the length of this string variable seems to be more than 256\n";
					print STDERR "parsing error is highly likely at $j-th case, $i-th var (string)\n";
					die "parsing error possible ($j-th case, $i-th var)";
				}
				print $FH "$j-th case $i-th var: datum_length(base-10)", $lendtd, "\n" if $DEBUG;

				read($rfh, $datums, $lendtd);
				print $FH "$j-th case $i-th var(S):",$datums,"\n" if $DEBUG;;
				
				# missing value processing (string)
				if ( ($lendtd == 1) && ($datums eq ' ') ){
					$datums =$MVstring;
				}

				if ($i == $lastvar) {
					$casewiseRecord .= $datums;
				} else {
					$casewiseRecord  .=  $datums . "\t";
					#$casewiseRecord  .=   "$datums\t";
				}
					
				$buff='';
				$datums = ''; 
				$lendt = ''; 
				$lendtd = 0;
				
				# end of string var cases
			} elsif ($varType->[$i]==0) {
			 	# numeric var case
				$buff='';
				$MVyes = 0;

				until ($buff eq '/'){
					$datumt .= $buff;
					read($rfh, $buff, 1);
					if (eof $rfh) {
						print $FH "\n\nTerminal string:", $datumt ,"\n" if $DEBUG;
						$nocases = $j-1;
						print $FH "\nncases:", $nocases, "\n" if $DEBUG;
						last FBLOCK;
					} elsif ($buff eq '*'){
						# missing value '*.'handling
						# genuine SPSS for windows uses *. as a system missing value
						# however, DBMS/COPY uses ** as a system missing value
							
						$datumn = $MVstring;
						$MVyes = 1;
						# skip the second character '.' or '*'
						read($rfh, $buff,1);
						last;
						
					}
					# print $FH "buff:", $buff, "\n"  if $DEBUG;
					# print $FH "lendt:", $datumt, "\n"  if $DEBUG;
				}
				print $FH "$j-th case $i-th var(N:base-30):",$datumt,"\n" if $DEBUG;
				
				unless ($MVyes) { 
					# non-missing value
					if ((length($datumt)==1) && ($datumt =~ m/[0-9A-T]/)) {
						# fast track: convert a one-character-base-30 unsigned integer to base-10 one
						$datumn = $baset2dtbl{$datumt};
					} else {
						$datumn = &base30tobase10(\$datumt);
					}
					print $FH "$j-th case $i-th var(N:base-10):",$datumn,"\n" if $DEBUG;
					
					# convert date/time data to ISO expression
					
					my $vf = $self->{_varFormat}->{ $self->{_varNameA}->[$i] };
					
					
					
					if ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'date') {
						# date conversion
						$datumn=&VDC::DSB::Ingest::StatData::findGregorianDate($datumn, 'D');
						
						print $FH "$j-th case $i-th var(after ISO date conversion):",$datumn,"\n" if $DEBUG;
						
						
					} elsif ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'time'){
					
						# time conversion
						if ($vf eq 'DTIME'){
							$datumn=&VDC::DSB::Ingest::StatData::findGregorianDate($datumn, 'JT');
							
							
						} elsif ($vf eq 'DATETIME'){
							$datumn=&VDC::DSB::Ingest::StatData::findGregorianDate($datumn, 'DT');
						} else {
							$datumn=&VDC::DSB::Ingest::StatData::findGregorianDate($datumn, 'T');
						}
						
						print $FH "$j-th case $i-th var(time-type: after conversion):",$datumn,"\n" if $DEBUG;
					} elsif ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'other') {
					
						if ($vf eq 'WKDAY'){
							# day of week
							$datumn=$WEEKDAY[$datumn-1];
						} elsif ($vf eq 'MONTH'){
							# month
							$datumn=$MONTH[$datumn-1];
						}
					}
					unless ($vt->[$i]) {
						$intA = int($datumn);
						if ($intA != $datumn) {
							$vt->[$i]=1;
							$nodcml++;
						}
					}
				} 
				
				if ($i == $lastvar) {
					$casewiseRecord .=$datumn;
				} else {
					$casewiseRecord  .=  $datumn . "\t";
					#$casewiseRecord  .=  "$datumn\t";
				}
			
				$buff='';
				$datumn=undef;
				$datumt='';
				$MVyes=undef;
				# end of numeric var cases
			}
		} # end of per-case processing
		print $FH "+++++++++++++++++ End of Case +++++++++++++++++\n\n" if $DEBUG;	
		#print "+++++++++++++++++ End of Case +++++++++++++++++\n\n" if $DEBUG;	
		print $tfh $casewiseRecord . "\n";
		$casewiseRecord='';
		
	} # end of while
	
	print $FH "decimal type=",join('|',@$vt),"\n" if $DEBUG;
	$end=time() if $DEBUG;
	#$end=time();
	print $FH "seconds spent=", ($end - $start), "\n" if $DEBUG;
	#print "seconds spent=", ($end - $start), "\n";
	$self->{_fileDscr}->{caseQnty}=$nocases;
	for (my $i=0;$i<@{$self->{_varNameA}}; $i++){
		if ($vt->[$i]>0){
			$self->{_dcmlVarTbl}->{$self->{_varNameA}->[$i]}='F';
		}
	}
	$self->{_noDcmlVar}=$nodcml;
	
	## var-type-adjustment for WKDAY and MONTH format
	
	
}


sub stripNewLines{
	my $optn ={@_};
	my $rfh  = $optn->{RFL};
	my $wfh  = $optn->{WFL};
	my $len;
	while (<$rfh>) {
		chomp; 
		s/\r//; # this line removes DOS-stile newlines
		$len = length ($_); 
		if ($. >= 6) {
			while ($len < 80){
				$_ .= " ";
				$len = length ($_); 
			}
			print $wfh $_ ;
		}
	}
}

sub base30tobase10 {
	my $tmpt = shift;
	my $numberT = $$tmpt;

	my $DEBUG=0; # set $DEBUG 1 for dumping intermediate results
	# remove leading/trailing whitespaces
	# 
		if ($DEBUG) {print "\n////// number in baseT=", $numberT, "//////\n";}
	
	# /////////////////////////////////////////////////////
	# fast-track to a one-letter, positive integer case
	# /////////////////////////////////////////////////////
	#if ( (length($numberT) == 1) && ($numberT =~ m/[0-9A-T]/) ){
	#	return $baset2dtbl{$numberT};
	#}

	# /////////////////////////////////////////////////////
	# general case [scientific notation ]
	
	# base-30 numbers generated by genuine SPSS
	#
	#	if se notation is used, 
	#	significand's maximal length is 11
	#	exponent takes 2 or 3 characters
	#	if negative, plus 1 [sign (-)]
	#	totally 14 or 15
	#
	#	   23N7IFFCQJB+D/
	#	  -23N7IFFCQJB+D/
	#	  E3AQ82SFN8O-14/
	#	   4KKPFHOM54O+G/
	#	  -4KKPFHOM54O+G/
	#	 -6BR3TTO388R-17/
	#	 ---------------
	#	 543210987654321
	#
	#	if se notation is not used, 
	#	significand's maximal length is 13
	#	if negative, plus 1 [sign (-)]
	#	totally 13 or 14
	# 
	#	 1EDRCDKSMC6BS
	#	-1EDRCDKSMC6BS
	#	---------------
	#	43210987654321
	#
	# /////////////////////////////////////////////////////
	
	# /////////////////////////////////////////////////////
	# step 1: check the negative sign 
	my $ngtv = 0;
	if (substr($numberT, 0,1) eq '-') {
		$numberT = substr($numberT, 1);
		$ngtv=1;
	}
		if ($DEBUG) {
			print "numberT a:", $numberT, "\n";
			print "sgn:", $ngtv, "\n";
		}
	# +++++++++++++++++++++++++++++++++++++++++++++++++++++
	# step 2:
	# check whether the exponent segment, 
	# which comes with + or -, exists
	# locate the sign of the exponent (+ or -):
	# it is not located at the first position (i.e.,0)
	# if not found, the return value = -1
	# if found, divide the string into the mantisa and exponent segments
	# then adjust the position of decimal in the significand
	
	my ($sgnfcndT, $expnntT);
	my ($expnntA, $dcmlvl);
	my (@tmpy, @tmpwhl, @tmpfrc);
	$expnntA   = 0;
	
	my ($lenfrctn, $dcmlpn, $whlnmbrt,$frctnt,$dffdgt);

	if (index($numberT, '+')>0) {
		# positive exponent
		($sgnfcndT, $expnntT) = split(/\+/, $numberT);
		# even exponent value is expressed in base-30 
		#$expnntA = &basetINT2basedINTunsgn(\$expnntT);
		
		my @mntssT = split(//, $expnntT);
		for (my $i=0; $i< @mntssT; $i++) {
			$expnntA += ($baset2dtbl{$mntssT[$#mntssT - $i]})*(30**$i);
			if ($DEBUG) {print "$i-th base-10 value(int):", (sprintf "%.15e", $expnntA), "\n";}
		}
		
			if ($DEBUG) {
				print "Significand+:", $sgnfcndT,"\n";
				print "exponent(t)+:", $expnntT,"\n";
				print "exponent(d)+:", $expnntA,"\n";
			}
			
		# length of the significand
		my $lensgnfcnd = length($sgnfcndT);
			print "length of the Significand:", $lensgnfcnd, "\n" if $DEBUG;
		# (no decimal point = 0 not -l)
		my $dcmlp = index($sgnfcndT, '.') +1;
			print "decimal point:",$dcmlp,"\n" if $DEBUG;
		if ($lensgnfcnd == $dcmlp){
			# value is integer but the decimal point is located at the right-hand-side edge
			# remove this decimal point
			$dcmlp=0;
			$sgnfcndT=substr($sgnfcndT,0, ($lensgnfcnd-1));
			$lensgnfcnd= ($sgnfcndT);
			if ($DEBUG) {
				print "updated Significand+:", $sgnfcndT,"\n";
				print "updated length of the Significand:", $lensgnfcnd, "\n";
				print "updated decimal point:",$dcmlp,"\n";
			}
		}
		# the exponent has positive value
		unless ($dcmlp) {
			$lenfrctn = 0;
		} else {
			$lenfrctn = $lensgnfcnd - $dcmlp;
		}
			print "length of fraction(e+):", $lenfrctn, "\n" if $DEBUG;
		$dffdgt = $expnntA - $lenfrctn;
		if ($dffdgt < 0) {
			# fraction part exists after the shift
			# exponent > 0
			# the shift of the decimal point is necessary
			print "expnntA", $expnntA, "\n" if $DEBUG;
			@tmpy  = split(/\./, $sgnfcndT);
			my $dW   = substr($tmpy[1], 0, $expnntA);
			my $newF = substr($tmpy[1], $expnntA);
			my $newW = $tmpy[0] . $dW;
			@tmpwhl= split(//, $newW);
			@tmpfrc= split(//, $newF);

			# ///////////////////////////////////////////
			#$dcmlvl = &baseT2baseA(\@tmpwhl, \@tmpfrc);

			$dcmlvl=0;
			if (scalar(@tmpwhl)) {
				for (my $i=0; $i<@tmpwhl; $i++) {
					$dcmlvl += ($baset2dtbl{$tmpwhl[$#tmpwhl - $i]})*(30**$i);
					if ($DEBUG) {print "$i-th base-10 value(whole):", (sprintf "%.15e", $dcmlvl), "\n";}
				}
			}
			if ($DEBUG) {print "\twhole part:",$dcmlvl, "\n";}

			if (scalar(@tmpfrc)) {
				for (my $i=1; $i<=@tmpfrc; $i++) {
					$dcmlvl += ($baset2dtbl{$tmpfrc[$i-1]})*(30**(-$i));
					if ($DEBUG) {print "$i-th base-10 value(fraction):", (sprintf "%.15e", $dcmlvl), "\n";}

				}
			}

			# ///////////////////////////////////////////
			if ($ngtv) {$dcmlvl *=(-1);}

			if ($DEBUG) {
				my @check=();
				print "whole=",$newW,"\t";
				print "fraction=",$newF,"\n";
				print "\nbase-10 value(+:1):", (sprintf "%.15e", $dcmlvl), "\n";
			}
			return $dcmlvl;

		} elsif ($dffdgt>=0) {
			# no fraction part (whole number part only) but shift still necessary
			if ($lenfrctn){
				$whlnmbrt = join('',split(/\./, $sgnfcndT));
			} else {
				$whlnmbrt=$sgnfcndT;
			}
			if ($dffdgt){
				$whlnmbrt .= sprintf "%0${dffdgt}u",0;
			}
			# +++++++++++++++++++++++++++++++++++++++++++
			#$dcmlvl = &basetINT2basedINTunsgn(\$whlnmbrt);

			$dcmlvl=0;
			my @mntssT = split(//, $whlnmbrt);
			if ($DEBUG) {print "mntssT:",join('|',@mntssT),"\n";}
			if ($mntssT[$#mntssT] eq '.') {
				pop @mntssT;
				if ($DEBUG) {print "updated mntssT:",join('|',@mntssT),"\n";}
			}
			for (my $i=0; $i< @mntssT; $i++) {
				$dcmlvl += ($baset2dtbl{$mntssT[$#mntssT - $i]})*(30**$i);
				if ($DEBUG) {print "$i-th base-10 value(int):", (sprintf "%.15e", $dcmlvl), "\n";}
			}

			# +++++++++++++++++++++++++++++++++++++++++++
			if ($ngtv) {$dcmlvl *=(-1);}

			if ($DEBUG) {
				print "whlnmbrt=",$whlnmbrt,"\n";
				print "base-30 value(whole number(+:2)):", $whlnmbrt, "\n";
				print "base-10 value(+:2):",(sprintf "%.16e", $dcmlvl), "\n";
			}
			return $dcmlvl;
		}
		# end of a positive exponent case

	} elsif (index($numberT, '-')>0) {
		# negative exponent
		($sgnfcndT, $expnntT) = split(/-/, $numberT);
		#$expnntA = &basetINT2basedINTunsgn(\$expnntT);
		
		my @mntssT = split(//, $expnntT);
		for (my $i=0; $i< @mntssT; $i++) {
			$expnntA += ($baset2dtbl{$mntssT[$#mntssT - $i]})*(30**$i);
			if ($DEBUG) {print "$i-th base-10 value(int):", (sprintf "%.15e", $expnntA), "\n";}
		}
		$expnntA *= (-1);
		
			if ($DEBUG) {
				print "Significand-:", $sgnfcndT,"\n";
				print "exponent(t)-:", $expnntT,"\n";
				print "exponent(d)-:", $expnntA, "\n";
			}
			
		# length of the significand
		my $lensgnfcnd = length($sgnfcndT);
			print "length of the Significand:", $lensgnfcnd, "\n" if $DEBUG;
		# (no decimal point = 0 not -l)
		my $dcmlp = index($sgnfcndT, '.') +1;
			print "decimal point:",$dcmlp,"\n" if $DEBUG;
			
		unless($dcmlp){
			$dcmlpn = $lensgnfcnd;
		} else {
			$dcmlpn = $dcmlp;
		}
			if ($DEBUG) {
				print "         dcmplp:", $dcmlp, "\nadjusted dcmplp:", $dcmlpn, "\n";
			}
		$dffdgt   = $dcmlpn + $expnntA ;

		if ($dffdgt > 0){
			# whole number part exists
			@tmpy = split(/\./, $sgnfcndT);
			my ($newW, $dW, $newF);
			if ($dffdgt >1) {
				my $bndry = length($tmpy[0])+$expnntA;
				$newW = substr($tmpy[0], 0, $bndry);
				$dW   = substr($tmpy[0], $bndry, abs($expnntA));
				$newF = $dW . $tmpy[1];
				print "4th case: whole part still exists\n" if $DEBUG;
				
			} elsif ($dffdgt == 1){
				print "4th case: no whole part\n" if $DEBUG;
				$newW ="";
				$newF = $tmpy[0] . $tmpy[1];
			}
			@tmpwhl= split(//, $newW);
			@tmpfrc= split(//, $newF);
			# ///////////////////////////////////////////
			#$dcmlvl=&baseT2baseA(\@tmpwhl, \@tmpfrc);
			
			$dcmlvl=0;
			if (scalar(@tmpwhl)) {
				for (my $i=0; $i<@tmpwhl; $i++) {
					$dcmlvl += ($baset2dtbl{$tmpwhl[$#tmpwhl - $i]})*(30**$i);
					if ($DEBUG) {print "$i-th base-10 value(whole):", (sprintf "%.15e", $dcmlvl), "\n";}
				}
			}
			if ($DEBUG) {print "\twhole part:",$dcmlvl, "\n";}

			if (scalar(@tmpfrc)) {
				for (my $i=1; $i<=@tmpfrc; $i++) {
					$dcmlvl += ($baset2dtbl{$tmpfrc[$i-1]})*(30**(-$i));
					if ($DEBUG) {print "$i-th base-10 value(fraction):", (sprintf "%.15e", $dcmlvl), "\n";}

				}
			}
			
			# ///////////////////////////////////////////
			if ($ngtv) {$dcmlvl *=(-1);}
			if ($DEBUG) {
				my @check=();
				print "whole(tmpwhl)=",$check[0]=join('',@tmpwhl),"\t";
				print "fraction(tmpfrc)",$check[1]=join('',@tmpfrc),"\n";
				print "whole=",$newW,"\tfraction=",$newF,"\n";
				print "\nbase-10 value(-:1):", sprintf "%.15e", $dcmlvl,"\n";
			}
			return $dcmlvl;

		} elsif ($dffdgt <= 0) {
			# no whole number part (fraction part only)
			if ($dcmlp){
				$frctnt = join('',split(/\./, $sgnfcndT));
			} else {
				$frctnt=$sgnfcndT;
			}
			if (abs($dffdgt)){
				$frctnt = (sprintf "%0${dffdgt}u",0)  . $frctnt;
			}
			if ($DEBUG) {
				print "frctnt:", $frctnt, "\n";
				print "dffdgt:", $dffdgt, "\n";
			}
			
			@tmpwhl = undef;
			@tmpfrc = split(//, $frctnt);
			# ///////////////////////////////////////////
			#$dcmlvl=&baseT2baseA(\@tmpwhl, \@tmpfrc);
			
			$dcmlvl=0;
			if (scalar(@tmpfrc)) {
				for (my $i=1; $i<=@tmpfrc; $i++) {
					$dcmlvl += ($baset2dtbl{$tmpfrc[$i-1]})*(30**(-$i));
					if ($DEBUG) {print "\t$i-th base-10 value(fraction):", (sprintf "%.15e", $dcmlvl), "\n";}

				}
			}
			
			# ///////////////////////////////////////////
			if ($ngtv) {$dcmlvl *=(-1);}
			if ($DEBUG) {
				my @check=();
				print "fraction(tmpfrc)",$check[0]=join('',@tmpfrc),"\n";
				print "base-10 value(-:2):", sprintf "%.15e", $dcmlvl,"\n";
			}
			return $dcmlvl;
		}
		 # end of a negative exponent
			
	} else {
	
		# no exponent: 12345 or 1.235
			if ($DEBUG) {print "exponent(d)", $expnntA, "\n";}
		
		if (index($numberT, '.') == -1){
			# no decimal point, i.e., integer: 12345
			# fast-track for integer
			# ///////////////////////////////////////////
			#$dcmlvl = &basetINT2basedINTunsgn(\$numberT);
			
			$dcmlvl=0;
			my @mntssT = split(//, $numberT);
			for (my $i=0; $i< @mntssT; $i++) {
				$dcmlvl += ($baset2dtbl{$mntssT[$#mntssT - $i]})*(30**$i);
				if ($DEBUG) {print "$i-th base-10 value(int):", (sprintf "%.15e", $dcmlvl), "\n";}
			}
			
			
			# ///////////////////////////////////////////
			if ($ngtv) {$dcmlvl *= (-1);}
			return $dcmlvl;
		} else {
			# decimal point exists but no exponent: .12345 or 1.235
			@tmpy  = split(/\./, $numberT);
			@tmpwhl= split(//, $tmpy[0]);
			@tmpfrc= split(//, $tmpy[1]);
			if ($DEBUG) {
				my @check=();
				print "whole(tmpwhl)=",$check[0]=join('',@tmpwhl),"\t";
				print "fraction(tmpfrc)",$check[1]=join('',@tmpfrc),"\n";
			}
			# ///////////////////////////////////////////
			#$dcmlvl = &baseT2baseA(\@tmpwhl, \@tmpfrc);
			
			$dcmlvl=0;
			if (scalar(@tmpwhl)) {
				for (my $i=0; $i<@tmpwhl; $i++) {
					$dcmlvl += ($baset2dtbl{$tmpwhl[$#tmpwhl - $i]})*(30**$i);
					if ($DEBUG) {print "$i-th base-10 value(whole):", (sprintf "%.15e", $dcmlvl), "\n";}
				}
			}
			if ($DEBUG) {print "\twhole part:",$dcmlvl, "\n";}

			if (scalar(@tmpfrc)) {
				for (my $i=1; $i<=@tmpfrc; $i++) {
					$dcmlvl += ($baset2dtbl{$tmpfrc[$i-1]})*(30**(-$i));
					if ($DEBUG) {print "$i-th base-10 value(fraction):", (sprintf "%.15e", $dcmlvl), "\n";}

				}
			}
			
			# ///////////////////////////////////////////
			if ($ngtv) {$dcmlvl *= (-1);}
			return $dcmlvl;
		}
	}
	
} # end of sub 

sub basetINT2basedINT {
	my ($nmbrTr) = @_;
	my ($nmbrTrx, $nmbrD, @mntssT);
	my $DEBUG=0;
	if ($DEBUG) {print "\n\tbasetINT2basedINT: A base-30 integer to be converted:",$nmbrTr,"\n";}
	
	my $sign=0;
	$nmbrD=0;
	if (index($$nmbrTr,'-') == 0) {
		$nmbrTrx=substr($$nmbrTr, 1);
		$sign=1;
	} else {
		$nmbrTrx=$$nmbrTr;
	}
	@mntssT = split(//, $nmbrTrx);
	for (my $i=0; $i< @mntssT; $i++) {
		$nmbrD += ($baset2dtbl{$mntssT[$#mntssT - $i]})*(30**$i);
		if ($DEBUG) {print "\t$i-th base-10 value(int):", (sprintf "%.15e", $nmbrD), "\n";}
	}
	if ($sign){$nmbrD *=(-1);}
	if ($DEBUG) {print "\tbasetINT2basedINT: base 10 value to be returned:",$nmbrD, "\n\n";}
	return $nmbrD;
}

sub basetINT2basedINTunsgn {
	my $nmbrTr =shift;
	my @mntssT;
	my $DEBUG=0;
	my $nmbrD=0;
	if ($DEBUG) {print "\n\tbasetINT2basedINT: A base-30 integer to be converted:",$nmbrTr,"\n";}
	@mntssT = split(//, $$nmbrTr);
	for (my $i=0; $i< @mntssT; $i++) {
		$nmbrD += ($baset2dtbl{$mntssT[$#mntssT - $i]})*(30**$i);
		if ($DEBUG) {print "\t$i-th base-10 value(int):", (sprintf "%.15e", $nmbrD), "\n";}
	}
	if ($DEBUG) {print "\tbasetINT2basedINT: base 10 value to be returned:",$nmbrD, "\n\n";}
	return $nmbrD;
}

sub baseT2baseA {
	my $nmbrW = shift;# baseT number (w);
	my $nmbrF = shift;# baseT number (f);
	my $DEBUG=0;
	if ($DEBUG) {
		print "\tbaseT2baseA:A base-30 integer:",join('', @{$nmbrW}) . '.' . join('', @{$nmbrF}),"\n";
	}
	my $nmbrD=0;
	if (@{$nmbrW}) {
		for (my $i=0; $i<@{$nmbrW}; $i++) {
			$nmbrD += ($baset2dtbl{$nmbrW->[$#{$nmbrW} - $i]})*(30**$i);
			if ($DEBUG) {print "\t$i-th base-10 value(whole):", (sprintf "%.15e", $nmbrD), "\n";}
		}
	}
	if ($DEBUG) {print "\twhole part:",$nmbrD, "\n";}
	
	if (@{$nmbrF}){
		for (my $i=1; $i<=@{$nmbrF}; $i++) {
			$nmbrD += ($baset2dtbl{$nmbrF->[$i-1]})*(30**(-$i));
			if ($DEBUG) {print "\t$i-th base-10 value(fraction):", (sprintf "%.15e", $nmbrD), "\n";}

		}
	}
	if ($DEBUG) {print "\tbaseT2baseA:after adding the faction:",$nmbrD, "\n\n";}
	
	return $nmbrD;
}



1;


__END__

=head1 NAME

VDC::DSB::Ingest::POR - SPSS portable (por) file reader

=head1 DEPENDENCIES

=head2 Nonstandard Modules

    VDC::DSB::Ingest::StatData

=head1 DESCRIPTION

VDC::DSB::Ingest::POR parses an SPSS por file and returns parsing results (metadata) as a reference to a hash.  This module is called by VDC::DSB::Ingest::StatDataFileReaderFactory and is used with VDC::DSB::Ingest::StatData.


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

