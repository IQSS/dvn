package VDC::DSB::Ingest::SPS;

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
@VDC::DSB::Ingest::SPS::ISA= qw(VDC::DSB::Ingest::StatData);
use IO::File;
use Data::Dumper;
use Parse::RecDescent;

# package variables
my $TMPDIR=File::Spec->tmpdir();

my $cmmndXtnsn = {
	DataList => 'datlst',
	VarLabel => 'varlab',
	ValLabel => 'vallab',
	MisValue => 'misval',
	RcdValue => 'recode',
	Formats  => 'formats'
};
	
#my @commandlst = qw(DataList Formats VarLabel ValLabel MisValue RcdValue);
my @commandlst = qw(DataList Formats VarLabel ValLabel MisValue );

my $command2Method ={
	DataList  => 'Datalist',	Formats   => 'Formats',
	VarLabel  => 'Varlabel',	MisValue  => 'MissValues',
	ValLabel  => 'Valuelabel', 	RcdValue  => 'Recode'
};

my $xcldkey = {
	'_cmmndStrng' => 'y',
	'_prcssOptn'  => 'y'
};
my $MAXSPSFL = 5e3;
# This value determines whether command strings will be saved
# as external files

my $MSVL = 1;
# This value determines whether missing-value command lines 
# ratgher than recode command lines will be used for
# missing-value data (ICPSR's sps tends to have both)
# by default, missing-value command lines are used

my $ICPSR = 0; 
# This param ($ICPSR) determines whether an ICPSR-specific parsing method,
# which removes a comment-out character at the beginning of a line 
# and parses the remaining line (typically missing values), 
# will be used; by default, it is not used;
# Thus if ICPSR-specific parsing mehtod is required, 
# an implementation script must override this default setting
my $MVstring="NA";


my $SPSS_FRMT_CATGRY_TABLE={
'CONTINUE'=>'other','A'=>'other','AHEX'=>'other','COMMA'=>'other',
'DOLLAR'=>'currency','F'=>'other','IB'=>'other','PIBHEX'=>'other',
'P'=>'other','PIB'=>'other','PK'=>'other','RB'=>'other','RBHEX'=>'other',
'Z'=>'other','N'=>'other','E'=>'other','DATE'=>'date','TIME'=>'time',
'DATETIME'=>'time','ADATE'=>'date','JDATE'=>'date','DTIME'=>'time',
'WKDAY'=>'other','MONTH'=>'other','MOYR'=>'date','QYR'=>'date',
'WKYR'=>'date','PCT'=>'other','DOT'=>'other','CCA'=>'currency',
'CCB'=>'currency','CCC'=>'currency','CCD'=>'currency','CCE'=>'currency',
'EDATE'=>'date','SDATE'=>'date',};




sub _init {
	my $self=shift;
	my $args = {@_};
	
	my $DEBUG=0;
	my $FH;
	
	unless (exists($args->{LOGFILE})){
		my $logfile = $TMPDIR  . '/sps.' . $$ . '.log';
		$FH = new IO::File("> $logfile");
		$self->{_prcssOptn}->{LOGFILE} =$FH;
		print $FH "default log file name will be used:", $logfile, "\n" if $DEBUG;
	} else{
		$self->{_prcssOptn}->{LOGFILE}= $FH = $args->{LOGFILE};
		print $FH "user-supplied log file name will be used:\n" if $DEBUG;
	}
	
	print $FH "args:\n", Dumper($args) if $DEBUG ;
	
	
	my $datafile;
	my $spsfile;
	my $base;

	unless (exists($args->{SPSFILE})){
		die "method parse_file [SPS.pm] needs at least an sps file name in its argument list!";
	} else {
		$self->{_prcssOptn}->{SPSFILE} = $spsfile= $args->{SPSFILE};
		print $FH "sps file name: $spsfile\n" if $DEBUG;
	}
	
	unless (exists($args->{SOURCEFILE})){
		die "method parse_file [SPS.pm] needs at least a data file name in its argument list!";
	} else {
		$self->{_prcssOptn}->{SOURCEFILE} = $datafile= $args->{SOURCEFILE};
		$base=$datafile;
		print $FH "data file name: $datafile\n" if $DEBUG;
	}
	my $tabfile;
	unless (exists($args->{DLMTDDATA})){
		$base =~s/\.([^\.]*)$//;
		$self->{_prcssOptn}->{DLMTDDATA}=$tabfile = $base . ".$$.tab";
		print $FH "package-generated tab-file name will be used: $tabfile\n" if $DEBUG;
	} else {
		$self->{_prcssOptn}->{DLMTDDATA}=$tabfile  = $args->{DLMTDDATA};
		print "user-supplied tab-file name will be used: $tabfile\n" if $DEBUG;
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
	
	if (exists($args->{ICPSR})){
		$ICPSR = $args->{ICPSR};
	}
	$self->{ICPSR}= $ICPSR;
	
	
	$self->{_prcssOptn}->{MSVL} = exists($args->{MSVL}) ? $args->{MSVL}    : $MSVL;
	$self->{_prcssOptn}->{FLSZ} = exists($args->{FLSZ}) ? $args->{SPSFLSZ} : $MAXSPSFL;
	my $charset = $self->{_fileDscr}->{charset};

	$self->{_fileDscr}->{fileType}=join(';', ("text/plain","charset=&quot;$charset&quot;", "type=&quot;fixed-field&quot;"));
	$self->{_fileDscr}->{fileFrmt}='card-image';
	
	if (exists($args->{MVstring})){
		$MVstring = $args->{MVstring};
	}
	$self->{MVstring}= $MVstring;

}


sub parse_file {
	my $self=shift;
	my $options = {@_};
	my $DEBUG=0;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	
	# separate the sps file into command lines
	$self->getCommandLines();

	my $svmode = $self->{_prcssOptn}->{StorageMode};
	my ($methodName,$returnValue);

	print $FH "commandlst=",join('|',@commandlst),"\n" if $DEBUG;
	
	foreach my $cmndkey (@commandlst){
		print $FH "cmndkey=",$cmndkey,"\n" if $DEBUG;
		
		if (exists($self->{_cmmndStrng}->{$cmndkey})){
			print $FH "method=",$command2Method->{$cmndkey},"\n" if $DEBUG;
			$methodName = 'read_' . $command2Method->{$cmndkey};
			$returnValue = $self->$methodName();
			
			print $FH "return value from $command2Method->{$cmndkey}:\n",Dumper($returnValue),"\n" if $DEBUG;
			
			if ($cmndkey eq 'DataList'){
				print $FH "$command2Method->{$cmndkey} data:\n",Dumper($self),"\n" if $DEBUG;
			}
		}
	}
	
	$self->setLocationInfo();
	print $FH "location info:\n", Dumper($self->getLocationInfo()) if $DEBUG;
	print $FH "self(final:sps):\n",Dumper($self),"\n" if $DEBUG;

	# unpack a non-delimited data file
	$self->unpackdata();
}

sub DESTROY{
	my $self =shift;
	$self->exitChores();
}



sub setStorageMode {
	my $self=shift;
	my $DEBUG=1;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	open (FILE, $self->{_prcssOptn}->{SPSFILE}) or die "cannot open $self->{_prcssOptn}->{SPSFILE} to check its file size: $!\n";
	my $filesize = -s FILE;
	my $mode;
	if ($filesize < $self->{_prcssOptn}->{FLSZ}) {
		$mode = "internal";
	} else {
		print $FH "sps file size is $filesize bytes and default (extenal) storage mode is used\n" if $DEBUG;
		$mode = "external";
	}
	$self->{_prcssOptn}->{StorageMode}=$mode;
	return $mode;
}

sub getCommandLines {
	my $self= shift;
	my $optns = { @_ };
	
	my $DEBUG=1;
	my $FH= $self->{_prcssOptn}->{LOGFILE};

	print $FH "optns=\n",Dumper($optns),"\n" if $DEBUG;
	
	print $FH "getCommandLInes: self=\n",Dumper($self),"\n" if $DEBUG;
	
	my @spstmp = split('/', $self->{_prcssOptn}->{SPSFILE});
	my $SPSBASE = $spstmp[$#spstmp];
	print $FH "spstmp=", $spstmp[$#spstmp], "\n" if $DEBUG;
	
	my $tmpSPSDIR = $TMPDIR .'/' . $SPSBASE;
	$self->{_prcssOptn}->{tmpSPSDIR}=$tmpSPSDIR;
	
	my $svmode = $self->setStorageMode();
	my $file= $self->{_prcssOptn}->{SPSFILE};
	open (INFILE, "< $file") or die "cannot open $self->{_prcssOptn}->{SPSFILE} : $!\n";
	
	print $FH "\nstart dividing SPSS data definition file\n" if $DEBUG;
	
	my ($lines, @commands, $frstchr);
	my $counter =0;
	while (<INFILE>){
		#chomp;
		my $line = $_;
		$line =~ s%\s+$%%;
		#$line = $line . "\n";
		$line .=  " ";
		
		# check whether this line is a blank or terminator-only line
		# if so, skip the line

		if ( ($line =~ m/^[ ]*?\./) || ($line =~ m/^[ ]*?[\n]/) || ($line =~ m/^comment/i)){
			#print "counter($counter):\n", $line, "[eol]\n";
			if (!eof) {next} else {push @commands, $lines;last}
		}

		# check the first character
		#  space: continuation line or blank line
		#  not space: a new command line

		$frstchr = substr($line, 0, 1);

		#print "first character:", $frstchr, "\n";

		if ($frstchr =~ m/(\s+)/) {
			# A continuation line was found
			$line =~ s/^\s+//;
			$lines .= ' ' . $line;
			
			#print "lines:", $lines,"\n";
		} else {
			# A new command line was found
			# if not the first line, add the contents of the command squence 
			#  already in the buffer ($lines) to the storage
			
			if ($counter) {push @commands, $lines;}
			
			# clear the buffer
			
			$lines = '';
			
			# add the current line to the buffer
			
			$lines .= $line;
		}
		# if the end of this file is found, 
		# don't forget to pick up the command sequence already in the buffer

		if (eof) {push @commands, $lines;}
		$counter++;
	}
	close (INFILE);

	print $FH "cmmnddmp(0):\n",Dumper(@commands), "\n" if $DEBUG;

	my (@cmmndlst, $cmmndpt1, $cmmndpt2, $ncases, $tmp);
	
	if ($ICPSR) {
		my $dlpass=0;
		for (my $i=0; $i< @commands;$i++) {
			if ($commands[$i] =~ m/^data[ ]*?list[ ]/i) {
				#print "${i}-th dl pass:",$commands[$i] ,"\n";
				$dlpass =1 
			}
			if ($dlpass) {
				if ( $commands[$i] =~ m/^\*[ ]*?(MISSING|MIS)[ ]*?(VALUES|VAL) /i ) {
					#print "${i}-th mv before:",$commands[$i] ,"\n";
					$commands[$i] =~ s/^\*[ ]*?(MISSING|MIS)[ ]*?(VALUES|VAL) /MISSING VALUES /i;
					#print "${i}-th mv after:",$commands[$i] ,"\n";
					print $FH "MISSING VALUES command line is activated\n" if $DEBUG;
					
				} elsif ( $commands[$i] =~ m/^\*[ ]*?RECODE /i ) {
					#print "${i}-th recode before:",$commands[$i] ,"\n";
					$commands[$i] =~ s/^\*[ ]*?RECODE /RECODE /i;
					#print "${i}-th recode after:",$commands[$i] ,"\n";
					print $FH "RECODE command line is activated\n" if $DEBUG;
				}
			} else {
				next;
			}
		}
	}
	my $cmdstrng = {};
	my $j=0;
	foreach (@commands){
		#print "$j -th", $_, "\n";
		($tmp =  $_)  if ( ($_ !~ m/^[*]/) && ($_ !~ m/^comment/i));
		#print $FH "tmp($j-th):", $tmp, "\n";
		$tmp =~ s/^(\w+?)\s+?(\w+)//;
		#print "tmp($j-th):", $tmp, "\n";
		if ($1){ $cmmndpt1=lc($1);} else { $cmmndpt1='';}
		if ($2){ $cmmndpt2=lc($2);} else { $cmmndpt2='';}
		#print "1st token:", $cmmndpt1, "\n";
		#print "2nd token:", $cmmndpt2, "\n";
		if ( ($cmmndpt1 =~ m/^data/) && ($cmmndpt2 =~ m/^list/) ) {
			#print $FH "tmp($j-th):\n", $tmp, "\n";
			$cmdstrng->{DataList} .=  '/'. $tmp;
			#print $FH "strng datalist($j-th):\n", $cmdstrng->{DataList}, "\n";
		} elsif ( ($cmmndpt1 =~ m/^var/) && ($cmmndpt2 =~ m/^lab/) ) {
			#print $FH "tmp($j-th):\n", $tmp, "\n";
			$tmp =~ s/[.]{0,1}\s*$/\n/;
			$cmdstrng->{VarLabel} .=  $tmp;
			#print $FH "strng vrlb($j-th):\n",$cmdstrng->{VarLabel}, "\n";
		} elsif (  ($cmmndpt1 =~ m/^val/) && ($cmmndpt2 =~ m/^lab/) ) {
			#print "tmp($j-th):\n", $tmp, "\n";
			$tmp =~ s/[.]{0,1}\s*$//;
			$tmp =~ s%[/]{0,1}\s*$%%;
			$cmdstrng->{ValLabel} .=  $tmp .'/';
			#print $FH "strng vllb($j-th):\n", $cmdstrng->{ValLabel}, "\n";
		} elsif (  ($cmmndpt1 =~ m/^mis/) && ($cmmndpt2 =~ m/^val/) ) {
			#print $FH "tmp($j-th):\n", $tmp, "\n";
			$tmp =~ s/[.]{0,1}\s*$/\n/ ;
			$cmdstrng->{MisValue} .= $tmp;
			#print $FH "strng msngvl($j-th):\n", $cmdstrng->{MisValue}, "\n";
		} elsif ($cmmndpt1 =~ m/^recode/) {
			#print $FH "tmp($j-th):\n", $tmp, "\n";
			$tmp =~ s/[.]{0,1}\s*$/\n/;
			$cmdstrng->{RcdValue} .=  uc($cmmndpt2) . $tmp .'/';
			#print $FH "strng recode($j-th):\n", $cmdstrng->{RcdValue}, "\n";
		} elsif ($cmmndpt1 =~ m/^formats/){
			#print "tmp($j-th):\n", $tmp, "\n";
			$tmp =~ s/[.]{0,1}\s*$/\n/;
			$cmdstrng->{Formats} .=  uc($cmmndpt2) . $tmp .'/';
			#print $FH "strng formats($j-th):\n", $cmdstrng->{Formats}, "\n";
		} elsif ( ($cmmndpt1 =~ m/^n/) && ($cmmndpt2 =~ m/^of/) ) {
			#print $FH "tmp($j-th):\n", $tmp, "\n";
			if ( $tmp =~ m/^\s+(\w+)\s+(\d+)/) {
				#print $FH "(1):", $1, "\n";
				#print $FH "(2):", $2, "\n";
				if (lc($1) eq 'cases' && $2 !~/^[A-Z]/i){
					$ncases=$2;
					if ($DEBUG) {print $FH "no of cases:", $ncases, "\n";}
				} else {
					if ($DEBUG) {print $FH "no of cases is not found\n";}
				}
			}
		}
		$j++;
		$tmp='';$cmmndpt1='';$cmmndpt2='';
	}

	if (exists($cmdstrng->{MisValue}) && exists($cmdstrng->{RcdValue})) {
		print $FH "both missing values and recode command lines exist\n" if $DEBUG;
		if ($MSVL) {
			delete($cmdstrng->{RcdValue});
			print $FH "missing values command line will be used(default)\n" if $DEBUG;
		} else {
			delete($cmdstrng->{MisValue});
			print $FH "recode command line will be used\n" if $DEBUG;
		}
	}

	# store each strings
	print $FH "command string:\n", Dumper($cmdstrng),"\n" if $DEBUG;
	my @cmmndset = keys %{$cmdstrng};
	print $FH "cmmndset=", join('|',@cmmndset),"\n" if $DEBUG;
	my $cmmndname ={
		DataList => 'data list',
		VarLabel => 'var lab',
		ValLabel => 'val lab',
		MisValue => 'mis val',
		RcdValue => 'recode',
		Formats  => 'formats'
	};
	my $check=0;
	foreach my $cmndkey (@cmmndset){
		if (($cmndkey eq 'DataList') && ($cmdstrng->{$cmndkey})) {
			$check=1;
		}
		$cmdstrng->{$cmndkey} =~ s%^/%%;
		$cmdstrng->{$cmndkey} =~ s/\.$//;
		
		#if ($DEBUG) {print $FH "$cmndkey=\n",$cmdstrng->{$cmndkey},"\n";}
		if ($svmode eq 'internal') {
			$self->{_cmmndStrng}->{$cmndkey} = $cmmndname->{$cmndkey} . ' ' . $cmdstrng->{$cmndkey};
		} elsif ($svmode eq 'external')  {
			if ($DEBUG) {print $FH "cmmndXtnsn=",$cmmndXtnsn->{$cmndkey},"\n";}
			my $cmdflname = $tmpSPSDIR . '.' . $cmmndXtnsn->{$cmndkey};
			$self->{_cmmndStrng}->{$cmndkey} = $cmdflname;
			open (CMD, ">$cmdflname") or die "cannot open $cmmndname->{$cmndkey} file";
			print CMD ($cmmndname->{$cmndkey} . ' ' . $cmdstrng->{$cmndkey});
			close(CMD)
		}
	}
	if ($ncases) {
		$self->{_fileDscr}->{caseQnty}=$ncases;
	}
	#close($FH);
	print $FH "separator result=",$check,"\n" if $DEBUG;
	return $check;
}


sub setLocationInfo{
	my $self = shift;
	$self->{_locationInfo}={};
	foreach my $i (@{$self->{_fileDscr}->{CARDORDER}}){
		# 0[var] 1[cls] 2[cle] 3[fmt] 4[wdth] 5[dcml])) 
		for (my $j=0; $j<@{$self->{_location}->{$i}}; $j++){
			my $varNamei = $self->{_location}->{$i}->[$j]->[0];
			my $el= $self->{_location}->{$i}->[$j];
			#$self->{_locationInfo}->{$varNamei}=[$i, $el->[1], $el->[2],$el->[3],$el->[4],$el->[5]];
			$self->{_locationInfo}->{$varNamei}=[$i, @{$el}[1..5]];
		}
	}
}
sub getLocationInfo{
	my $self = shift;
	return $self->{_locationInfo};
}


sub exitChores{
	my $self=shift;
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	my $rv;
	if ($self->{_prcssOptn}->{StorageMode} eq 'external'){
		while ( my ($ky, $file) = each %{$self->{_cmmndStrng}} ) {
			$rv = unlink($file);
			if ($rv) {
				print $FH "temp sps fragment file (",$file,") deleted\n" if $DEBUG;
			}
		}
	}
	
	if(!$DEBUG) {
		$rv = unlink($self->{_prcssOptn}->{SMSTTFILE});
	}
	if ($rv) {
 		print $FH "temp sumStat file (",$self->{_prcssOptn}->{SMSTTFILE},") deleted\n" if $DEBUG;
	}
}


## ///////////////////////////////////////////////////////////////
#  methods to be overriden
## ///////////////////////////////////////////////////////////////

sub writeVarLocMapFile{
	# dump a location table of variables to an external file
	my $self = shift;
	my $optns = { @_ };
	my $vlmfile;
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};

	my $base=$self->{_prcssOptn}->{SOURCEFILE};
	my $filenmbr = $self->{_prcssOptn}->{FILENO};

	unless (exists($optns->{VLMFILE})){
		$base =~s/\.([^\.]*)$//;
		$vlmfile = $base . '.vlm';
		print $FH "package-generated vlm file name will be used: $vlmfile\n" if $DEBUG;
	} else {
		$vlmfile   = $optns->{VLMFILE};
		print $FH "user-supplied vlm file name will be used: $vlmfile\n" if $DEBUG;
	}
	
	# print "entering writeVarLocMapFile\n";
	# print "vlm file name=", $vlmfile,"\n";
	
	open(VLM, ">$vlmfile") or die "cannot open $vlmfile";
	my ($varid, $varcntr);
	$varcntr=0;
	
	print VLM $self->{_fileDscr}->{RECORDS},"\n";
	print $FH $self->{_fileDscr}->{RECORDS},"\n";
	
	# @{$self->{_fileDscr}->{CARDORDER}} can be sorted by card number in ascending order 
	# however, the order of variable naming (listing, i.e., v001-v100) 
	# must be much more important than their physical order in a data file, and
	# not sorted here
	
	my $filetype='';
 	#print join("\t",qw(0[ID] 1[card] 2[var] 3[cls] 4[cle] 5[fmt] 6[wdth] 7[dcml])), "\n";
	
 	for  (my $i=0; $i<@{$self->{_varNameA}};$i++ ){
		my $currentvar= $self->{_varNameA}->[$i];
		my $location= $self->{_locationInfo}->{$currentvar};
		#  [0]	card number
		#  [1]	starting position
		#  [2]	ending position
		#  [3]	variable type
		#  [4]	width
		#  [5]	decimal position
		if (exists($self->{_charVarTbl}->{$currentvar})){
			$filetype='A';
		} elsif (exists($self->{_dcmlVarTbl}->{$currentvar})) {
			$filetype='F';
		} else {
			$filetype='I';
		}

		$varid = $self->getVarID($filenmbr, $i+1);
		for(my $jj=0;$jj<@{$location};$jj++){
			if ($jj == 3) {
				$location->[$jj]=$filetype;
			} elsif ($jj == 5) {
				unless (defined($location->[$jj])){
					$location->[$jj]="";
				}
			}
		}
		print VLM join("\t",($varid, $location->[0],$currentvar, @{$location}[1..5])),"\n";
		print $FH join("\t",($varid, $location->[0],$currentvar, @{$location}[1..5])),"\n";

		$filetype='';
		$location=[];
	}
	close(VLM);
	$self->{_prcssOptn}->{VLMFILE}=$optns->{VLMFILE};
	return 0;
}

sub getVarTypeSet{
	my $self = shift;
	my $char    = $self->{_charVarTbl};
	my $varname = $self->{_varNameA};
	my $dcml    = $self->{_dcmlVarTbl};
	my $novar   = $self->{_fileDscr}->{varQnty};
	my $varcode=[];

	for (my $i=0;$i<$novar;$i++){
		if (exists($char->{$varname->[$i]})){
			# char var
			$varcode->[$i]=0 ;
		} elsif (exists($dcml->{$varname->[$i]})) {
			# numeric continuous var
			$varcode->[$i]=2 ;
		} else{
			# numeric discrete var
			$varcode->[$i]=1 ;
		}
	}
	return $varcode;
}

# ///////////////////////////////////////////////////////
# command parsing methods
# ///////////////////////////////////////////////////////


# ////////////////////////////////////////////////////////////
# Datalist-related methods
# ////////////////////////////////////////////////////////////

sub read_Datalist{
	my $self= shift;
	# Debugging parameters for Parse::RecDescent
	# $::RD_TRACE = 'true';
	# $::RD_HINT = 'true';#
	# $::RD_ERRORS = 'true';
	
	my $DEBUG=0;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	if ($DEBUG) {print $FH "\nstart parsing data list\n";}
	
	my $svmode = $self->{_prcssOptn}->{StorageMode};
	if ($svmode eq 'external')  {
		my $dumpflname = $self->{_cmmndStrng}->{DataList};
		open (CMD, "$dumpflname") or die "cannot open data list file";
	} elsif ($svmode eq 'internal') {
		if ($self->{_cmmndStrng}->{DataList}) {
			if ($DEBUG) { print $FH "text to be parsed:",$self->{_cmmndStrng}->{DataList},"\n";}
		} else {
			print $FH "data list is empty\n";
			die "data list is empty";
		}
	}
	
	
	$self->{_varNameA}=[];
	$self->{_varNameH}={};
	$self->{_location}={};
	$self->{_lastcolumn}={};
	$self->{_novarset}=[];
	$self->{_postfrmt}={};
	$self->{_charVarTbl}={};
	$self->{_dcmlVarTbl}={};
	
	
	
	# global variables: initialization
		$::frmtspc='';
		$::clmnscntr = 0;
		$::fortrancntr = 0;
		$::toison = 0;
		$::toflg = 0;
	# parsing block only
		$::crdcntr=0;
		$::checkvarcounter=0;
		$::checkvar={};
		$::rawvarhsh ={};
		$::sbcmnd={};
		
		$::dlSpec ={};
		$::dlSpec->{CARDORDER}=[];

		
	# start of grammar
	my $grammar = q(
		file: datalist 
		{
			#print "datalist:$item[1]\n";
			#my $rec = {%{$item[1]}};
			#$rec;
			$item[1];
		}
			datalist: /data list/ program(?) var(s) 
			{
				#print "datalist: var:", &::Dumper($item[3]),"\n";
				#{prog => $item[2], vars=>$item[3]}
				$item[3];
			}
				program: majorspec(?) table(?) dlmtr(?) junk(?) 
				{
					[@item];
				}
				majorspec: filespec(?) types(1) subcommand(s?) 
				{
					#print "mjspc1: item10=", $item[1][0],"\n";
					#print "mjspc1: item20=", $item[2][0],"\n";
					#print "mjspc1: item30=", $item[3][0],"\n";
					#print "mjspc1: item300=", $item[3][0][0],"\n";
					#print "mjspc1: item301=", $item[3][0][1],"\n";
					#[ $item[1], $item[2], $item[3]];
					
					{flspc =>$item[1][0],  dltype=> $item[2][0] ,sbcmnd => $::sbcmnd}
					#[@item]
				}
				 | filespec(?) subcommand(s?) types(1) 
				{
					#print "mjspc2: item1=", $item[1][0],"\n";
					#print "mjspc2: item2=", $item[2][0][0],"\n";
					#print "mjspc2: item2=", $item[2][0][1],"\n";
					#print "mjspc2: item3=", $item[3][0],"\n";
					#[ $item[1], $item[2], $item[3]];
					{flspc =>$item[1][0], sbcmnd => $item[2] , dltype=> $item[3][0]}
					#[@item]
				}
				 | filespec(?) subcommand(s?)
				{
					#print "mjspc3: item1=", $item[1][0],"\n";
					#print "mjspc3: item2=", &::Dumper($item[2]),"\n";
					#print "mjspc3: item2=", $item[2][0][0],"\n";
					#print "mjspc3: item2=", $item[2][0][1],"\n";
					#[ $item[1], $item[2]];
					{flspc =>$item[1][0], sbcmnd => $item[2] }
					#[@item]
				}
					filespec: /FILE/i '=' m%^(["']([\\[]|[\\(]|[\\{]|)[a-zA-Z0-9_: .\\\\-]*([\\)]|[\\]]|[\\}]|)['"])|[a-zA-Z0-9_:.\\\\-]*% 
					{
						# print "flsc: item 1 is:", $item[1], "\n";
						# print "flsc: item 3 is:", $item[3], "\n";
						#[$item[1], $item[3]];
						$item[3];
					}
					subcommand: /\\w+/ '=' /[0-9a-zA-Z]+/
					{
						my $rcrds=uc($item[1]);
						# print "RECORD spec is:", $rcrds, "\n";
						#if ($item[1]) {$rcrds =~ tr/a-z/A-Z/;}
						if ($rcrds eq 'RECORDS') {
							# print "RECORD spec is:", $item[3], "\n";
						}
						# print "subc: item 1 is:", $item[1], "\n";
						# print "subc: item 3 is:", $item[3], "\n";
						$::sbcmnd->{$item[1]} = $item[3];
						$::dlSpec->{$rcrds} = $item[3];
						# [$item[1], $item[3]];
					}
					types: /FIXED|FREE|LIST/i
					{
						# print "type=",$item[1],"\n";
						#if ( (lc($item[1]) eq 'free') || (lc($item[1]) eq 'list') ) {
						#	print "\nThis script cannot parse free/list-type data list command:";
						#	die "errcd(F211): error in parseDatalist(free/list-type data list command):$!\n";
						#}
						$::dlSpec->{DATAFORMAT} = $item[1];
						$item[1];
					}
					
					table: /TABLE|NOTABLE/i
					
					dlmtr: '(' oqt(?) dlmtrchr cqt(?) ')'
					{
						$item[3];
					}
						oqt: /[\\042]|[\\047]/
						dlmtrchr: /,|TAB|[^a-zA-Z0-9]/
						{
							# print "delimiter=",$item[1],"\n";
							$::dlSpec->{DELIMITER} = $item[1];
							$item[1];
						}
						cqt: /[\\042]|[\\047]/
						
					junk: prept(?) mddlept sufpt(?)
						prept: /[\\042]|[\\047]/
						mddlept: /([^\\057])+/i
						sufpt: /[\\042]|[\\047]/
				var:  separator cardnum(?) varlist(s?)
				{ 
					##print "\tvar:\n", $item[2][0], "\n";
					#print "\t\tvar:", @{$item[2]}, "\n";
					#print "\tvar:", $item[3], "\n";
					#print "\tvar3:", $item[3][0][0], "\n";
					# print "current card no=",$::crrntCrdNo,"\n";
					if ($item[2][0] =~ m/\\d+/){
						# print "card no[2]=",$item[2][0], "\n";
						push @{$::dlSpec->{CARDORDER}}, $item[2][0];
					} else {
						$::crrntCrdNo = $::crdcntr;
						#print "card no(crdcntr)=", $::crrntCrdNo,"\n";
						push @{$::dlSpec->{CARDORDER}}, $::crdcntr;
					}
					$::rawvarhsh->{$::crrntCrdNo}=$item[3];
					# print "varlist=",&::Dumper($item[3]),"\n";
					#print "\tvar all:",join("\t", ( $item[3][0][0], $item[3][0][1], $item[3][0][2], $item[3][0][3], $item[3][0][4]) ), "\n";
					#$item[3];
					{"$::crrntCrdNo" => $item[3]}
				}
					separator:'/'
					{
						$::crdcntr++;
						$item[1];
					}
					cardnum: /\\d+/
					{
						# print "cardnum:", $item[1], "\n\n";
						$::crrntCrdNo= $item[1];
						$item[1];
					}
					varlist: varname columns(?) frmt(?)
					{
						# note: $item[2] and $item[3] are refs
						##print "\t\tvarlist:", $item[1][0],"\t", $item[1][1],"\t", $item[2][0][0], "\t", $item[2][0][1],"\t",$item[3][0][0],"\t",$item[3][0][1], "\n"; 
						[ $item[1][0], $item[1][1], $item[2][0][0], $item[2][0][1], $item[3][0][0], $item[3][0][1]]
					}
						varname: varnamestart sepcmma(?) varnameend(?)
						{
							# note: $item[2] is ref 
							#print "\t\t\tvarname[s]:", $item[1], "\n";
							#print "item3-0=",$item[3][0] ,"\n";
							$::frmtspc = 'fortran';
							[ $item[1], $item[3][0] ]
						}
							varnamestart: /[a-z@\\043][a-z0-9._@\\043\\044]{0,63}/i
							{
								##print "\t\t\tvarname_start:", $item[1], "\n";
								if (!exists($::checkvar->{$item[1]})){
									$::checkvar->{$item[1]} = $::checkvarcounter;
									$::checkvarcounter++;
									
								} else {
									print "The current var(",$item[1],") is already found at:",$::checkvar->{$item[1]},"-th position\n";
									print "\nCoding error in the DATA LIST statement is suspected\n";
									die "errcd(F213): error in parseDatalist(duplicated var name):$!\n";;
								}
								$::toflg = 0;
								$item[1]
							}
							sepcmma: /,/
							varnameend: /to\\s/i /[a-z@\\043][a-z0-9._@\\043\\044]{0,63}/i
							{
								if  ($item[1]) {$::toison++;}
								$::toflg = 1;
								$item[2]
							}
							columns: start sepend(?) sepcmma(?)
							{
								# note $item[2] is a ref (not scalar) due to (?)
								##print "\t\t\tcolumns:", $item[1], "\t", $item[2][0], "\n";
								if ($item[1]) {
									$::frmtspc = 'clmns';
									if( (!$::toflg) && (!$item[2][0]) ) {$item[2][0] = $item[1];}
								}
								[  $item[1] , $item[2][0] ]
							}
								start: /\\d+/
								{
									## print "start:", $item[1], "\n";
									$item[1]
								}
								sepend: /-/ /\\d+/
								{
									## print "end:", $item[2], "\n";
									$item[2]
								}
								sepcmma: /,/
							frmt: '(' <matchrule:$::frmtspc> ')'
							{
								## print "dgt:", $item[2], "\n";
								if ($::frmtspc eq 'clmns') {
									# col-style case
									##print "frmt(s)[2][1]:", $item[2][0], "\n";
									##print "frmt(s)[2][1]:", $item[2][1], "\n";
									[ $item[2][0], $item[2][1] ]
								} elsif ($::frmtspc eq 'fortran') {
										##print "frmt(f)[2]:", $item[2], "\n";
										[ $item[2], '' ]
								}
							}
								clmns: frmttyp(?) cmm(?) wdth(?)
								{
									$::clmnscntr++;
									##print "clmns[1]:", $item[3][0], "\n";
									[ $item[1][0], $item[3][0] ]
								}
									frmttyp: /(f|comma|dot|dollar|pct|z|datetime|adate|edate|jdate|sdate|date|qyr|moyr|wkyr|dtime|time|wkday|month|a|n|e)/i
									{
										#print "\n+++++++++++++++++++++frmttyp:", $item[1], " +++++++++++++++++++++\n\n";
										$item[1]
									}
									cmm: /,/
									wdth: /[0-9]+/
									{
										##print "wdth:", $item[1], "\n";
										$item[1]
									}
								fortran: /[a-z0-9. ,\n\t\r\f]+/i
								{
									$::fortrancntr++;
									##print "fortran[1]:", $item[1], "\n";
									$item[1]
								}
	); # end of grammar
	
	my $parser = Parse::RecDescent->new($grammar);
	my $text; 
	{
		if ($svmode eq 'internal') { 
			$text = $self->{_cmmndStrng}->{DataList};
		} elsif ($svmode eq 'external') {
			local $/ = undef;
			$text = <CMD>;
		}
	}
	$parser->file($text);
	
	if ($svmode eq 'external') {close(CMD);}
	if ($DEBUG) {
		print $FH "\nrawvarhsh:\n",Dumper($::rawvarhsh),"\n";
		#INFO(__PACKAGE__,"\trawvarhsh:\n",Dumper($::rawvarhsh));
	}
	my $cosSize = scalar(@{$::dlSpec->{CARDORDER}});
	
	if (exists ($::dlSpec->{RECORDS})){
		$::dlSpec->{DIFFCRD}= $::dlSpec->{RECORDS} - $cosSize;
		if ($DEBUG) {
			print $FH "difference between RECORDS and no of cards =",$::dlSpec->{DIFFCRD},"\n";
		}
		if ($::dlSpec->{DIFFCRD} != 0){
			if ($DEBUG) {
				print $FH "RECORDS subcommand value (=", $::dlSpec->{RECORDS}, ") in DATA LIST command does not agree with the no of cards (=", $cosSize, ")\n";
				print $FH "This implies that ", $::dlSpec->{DIFFCRD}," card(s) are to be skipped\n";
			}
			
			my @cardNoSet = sort {$a <=> $b} @{$::dlSpec->{CARDORDER}};
			
			if ($cardNoSet[$#cardNoSet] > $cosSize){
				print $FH "last card number(=$cardNoSet[$#cardNoSet]) is not equal to the number of cards to be read(=$cosSize)\n";
			}
			if ($::dlSpec->{DIFFCRD} < 0) {
				print $FH "RECORDS subcommand value(=",$::dlSpec->{RECORDS},") is less than the number of cards to be read(=",$cosSize,")\n";
				die "errcd(F215): error in parseDatalist(RECORDS subcommand value):$!\n";
			}
		}
	} else {
		$::dlSpec->{RECORDS} = $cosSize;
		if ($DEBUG) {
			print $FH "no RECORDS parameter was found: no of cards (=",$cosSize,") is used\n";
		}
	}
	# check each card's raw-var size
	my $cntr1 = 0;
	my $skpvrs = 0;
	my @skpvar;
if ($DEBUG) {
	foreach my $i (@{$::dlSpec->{CARDORDER}}){
		print $FH "\n", $i, "-th card: raw-var size=", scalar(@{$::rawvarhsh->{$i}}),"\n";
		print $FH join ( "\t", ("(i,j):","0[vrs]", "1[vre]", "2[cls]", "3[cle]", "4[fmt]", "5[dcml]")), "\n";
		for (my $j=0; $j<@{$::rawvarhsh->{$i}}; $j++){
			print $FH "($i,$j):\t", join("\t",@{$::rawvarhsh->{$i}->[$j]}),"\n";
			
			if ( ( $::rawvarhsh->{$i}->[$j]->[1] && $::rawvarhsh->{$i}->[$j]->[4] ) || ($::rawvarhsh->{$i}->[$j]->[1] && $::rawvarhsh->{$i}->[$j]->[5]) ){
				# second var and format is on 
				$cntr1++;
			}
			if ( ($::rawvarhsh->{$i}->[$j]->[0]) && (!$::rawvarhsh->{$i}->[$j]->[2]) && (!$::rawvarhsh->{$i}->[$j]->[4]) && (!$::rawvarhsh->{$i}->[$j]->[5]) ){
				# F-format or column comes later
				$skpvrs++;
				push @skpvar, "$i-$j";
			}
		}
	}
}
	if ($DEBUG) {print $FH "\nraw var  hsh:\n";
		&printRawVarHsh($::rawvarhsh, $::dlSpec);
	}
	if ($DEBUG) {
		print $FH "Post-parsing processing:\n";
		print $FH "\n\nInitial Parsing Results(DATA LIST command: Summary)\n";
		print $FH "Number of var-set with frmt/wdth/dcml:",$::clmnscntr,"\n";
		print $FH "Number of F-frmt or col-data-come-later var-set:",$::fortrancntr,"\n";
		print $FH "Number of var_i-to-var_j-type var-set:",$::toison,"\n\n";
	}
$DEBUG=0;

if ($DEBUG) {
	if (!$cntr1 && !$::toison && !$::fortrancntr){
		print $FH "\nno post-parsing processing is necessary\n";
	} elsif (!$::toison && $::fortrancntr ){
		print $FH "var_i-to-var_j processing is unnecessary\n";
		print $FH "but $::fortrancntr F-format or col-data-come-later processing case(s) exist\n";
	} elsif ($::toison  && !$::clmnscntr && !$::fortrancntr ) {
		print $FH "$::toison var_i-to-var_j cases(var extension is necessary), \n";
		print $FH "but no format processing is necessary\n";
	} else {
		print $FH "$::toison var_i-to-var_j cases: var extension is necessary, \n";
		print $FH "and column format processing cases are $::clmnscntr,\n";
		print $FH "and F-format or col-data-come-later processing cases are $::fortrancntr.\n";
		print $FH "There are $skpvrs var(s) that lack their own F-format or column(s) due to shortcut.\n"; if ($skpvrs) { print $FH "Their positions are:", join(',', @skpvar),"\n";} 
	}
}
$DEBUG=0;

	my @tempvarset; 
	my $frtcntr=0;
	my $wrkhsh= {};
	my $varslice = [];
	my $extdvars = {vars=>[], cols=>[], cole=>[], frmt =>[], wdth =>[], dcml=>[], type=>[],vlen=>undef};

	# global vars for tab-position

	$::tabState=0;
	$::lastTABpstn=0;
	$::crrntpstn = 0;
	
$DEBUG=0;
	# for each card 
	foreach my $i (@{$::dlSpec->{CARDORDER}}){
		
		if ($DEBUG) { print $FH "\n######### $i-th card's process begins ######### \n";}
		
		$wrkhsh->{$i}=[];
		 if ($i!=1){ 
			if ($DEBUG){ 
				print $FH "\n$i-th card: current column position=",$::crrntpstn,"\n";
			 }
			# because the processing moved to the 2nd-or-after card, 
			# reset the column origin to 0
			$::crrntpstn = 0;
			$::tabState=0;
			$::lastTABpstn=0;
			if ($DEBUG) {
				 print $FH "\nAt $i-th card, column origin was reset to 0.\n";
			}
		} 

		# for each raw var entry
		for (my $j=0; $j<@{$::rawvarhsh->{$i}}; $j++){
			if ($DEBUG) { print $FH "\n ////// $j-th var's process begins //////\n";}

			$varslice=[ @{$::rawvarhsh->{$i}->[$j]} ];
			#"0[vrs]", "1[vre]", "2[cls]", "3[cle]", "4[fmt]", "5[dcml]";
			
			
			
			if ( (!$varslice->[1]) && ($varslice->[2]) ) {
				# /////////////////////////////////////////////////
				# case 1: no var_e and col_s
				# [!var-e and col-s] no 'to' nor F-style spec, but start col not empty; 
				# dcml is either empy or filled
				# /////////////////////////////////////////////////

				if (!$frtcntr){
					# no accumulated format data
					if ($DEBUG) {
						print $FH "\n$j-th iteration enters col1-part1[no carryover var]:\n";
						print $FH "var to be processed:",$varslice->[1],"\n";

						if ($varslice->[5]) {
							print $FH "decimal column is not empty\n";
						} else {
							print $FH "decimal column is empty\n";
						}
					}

					if ($varslice->[3]) {
						$::crrntpstn = $varslice->[3];
						if ($DEBUG) { print $FH "current column position moved to:", $::crrntpstn,"\n";}
					} elsif ($varslice->[2])  {
						$::crrntpstn = $varslice->[2];
						if ($DEBUG) { print $FH "current column position moved to:", $::crrntpstn,"\n";}
					}
					push @{$wrkhsh->{$i}}, [ $varslice->[0],$varslice->[2],$varslice->[3], $varslice->[4],($varslice->[3] - $varslice->[2] +1),$varslice->[5] ];
					
					
					
					
				} else {
					# accumulated format data exist
					#
					# ending of a sequence of variables: (var_i k-l)
					# decimal position may not be empty
					$extdvars->{type}=[0, 1, 0];
					if ($DEBUG) { 
						print $FH "\n$j-th iteration enters col1-part2[carryover var exists]:\n";
						print $FH "var to be processed:",$varslice->[0],"\n";
						print $FH "process type=",join("|", @{$extdvars->{type}}) ,"\n";
						if ($varslice->[5]) {
							print $FH "decimal column is NOT empty\n";
						} else {
							print $FH "decimal column is empty\n";
						}
					}
					push @{$extdvars->{vars}}, $varslice->[0];

					my $accvarset = scalar(@{$extdvars->{vars}});
					if ($DEBUG) {
						print $FH "number of accumulated vars=", $accvarset,"\n";
						# print $FH "accumlated vars so far:",join("|", @tmpvars) ,"\n";
						print $FH "accumlated vars so far:",join("|", @{$extdvars->{vars}}) ,"\n";
					}

					$extdvars->{vlen}= $accvarset;
					&expandColumns($varslice,$extdvars);
					my $fmtcvrdvars = $extdvars->{vlen};
					
					if ($DEBUG) {
						print $FH "returned cols:",join('|', @{$extdvars->{cols}}),"\n";
						print $FH "returned cole:",join('|', @{$extdvars->{cole}}),"\n";
					}

					if ($varslice->[3]) {
						$::crrntpstn = $varslice->[3];
						if ($DEBUG) {print $FH "current col position moved to:",
						$::crrntpstn,"\n";}
					} elsif ($varslice->[2])  {
						$::crrntpstn = $varslice->[2];
						if ($DEBUG) {print $FH "current col position moved to:", $::crrntpstn,"\n";}
					}
					my $offset = $accvarset-$fmtcvrdvars;
					for (my $k = 0; $k< @{$extdvars->{vars}}; $k++) {
						if ($k < $offset) {
							push @{$wrkhsh->{$i}},[ $extdvars->{vars}->[$k],undef, undef,undef,undef,undef ];
						} else {
							push @{$wrkhsh->{$i}}, [ $extdvars->{vars}->[$k], $extdvars->{cols}->[$k-$offset], $extdvars->{cole}->[$k-$offset], $varslice->[4], ($extdvars->{cole}->[$k-$offset] - $extdvars->{cols}->[$k-$offset] + 1), $varslice->[5] ];
						 }
					}
					# reached the row that has a col spec and reset the flag var:
					$frtcntr=0;
					# reset temp objects
					&clearVarSets($extdvars);
					
				}
			} elsif ( $varslice->[1] && $varslice->[3] ) {
				# /////////////////////////////////////////////////
				# case 2: var_e and col_e
				# case: [var-e and col-e] last line var_i-TO-var_j and column-format
				# /////////////////////////////////////////////////
				
				# rest the TAB
				$::tabState=0;
				$::lastTABpstn=0;
				if ($DEBUG){print $FH "col2: reset the tabState\n"; }

				if (!$frtcntr){
					$extdvars->{type}=[1, 1, 0];
					if ($DEBUG){
						print $FH "\n$j-th iteration enters col2-part1[no carryover var]:\n";
						print $FH "process type=",join("|", @{$extdvars->{type}}) ,"\n";
						print $FH "var names: start|end:(", $varslice->[0], "|",$varslice->[1],")\n";
						print $FH "columns: start|end:(", $varslice->[2],"|",$varslice->[3], ")\n";
						if ($varslice->[5]) {
								print $FH "decimal column is not empty\n";
						} else {
								print $FH "decimal column is empty\n";
						}
					}
					&expandVars($varslice,$extdvars);
					&expandColumns($varslice,$extdvars);
					if ($DEBUG) {
						print $FH "expanded vars:",join("|", @{$extdvars->{vars}}) ,"\n";
						print $FH "returned cols:",join('|', @{$extdvars->{cols}}),"\n";
						print $FH "returned cole:",join('|', @{$extdvars->{cole}}),"\n";
					}
					$::crrntpstn = $varslice->[3];
					if ($DEBUG) { print $FH "current column position moved to:", $::crrntpstn,"\n";}
					for (my $k = 0; $k< @{$extdvars->{vars}}; $k++) {
						push @{$wrkhsh->{$i}}, [ $extdvars->{vars}->[$k], $extdvars->{cols}->[$k], $extdvars->{cole}->[$k], $varslice->[4], ($extdvars->{cole}->[$k] - $extdvars->{cols}->[$k] + 1), $varslice->[5] ];
					}
					
					# reset temp objects
					&clearVarSets($extdvars);
					
				} else {
					# the end of the series of col-type variables: var_i TO var_j k-l
					if ($DEBUG) { 
						print $FH "\n$j-th iteration enters col2-part2[carryover var exists]\n";
						print $FH "process type: expand vars and columns separately\n";
						print $FH "var names: start|end:(",$varslice->[0],"|",$varslice->[1], ")\n";
						print $FH "columns: start|end:(",$varslice->[2],"|",$varslice->[3], ")\n";

						if ($varslice->[5]) {
								print $FH "decimal column is NOT empty\n";
						} else {
								print $FH "decimal column is empty\n";
						}
					}
					$extdvars->{type}=[1, 0, 0];
					# temporally store unfinished variables so far
					@tempvarset = @{$extdvars->{vars}};
					my $tempvarsetlength=scalar(@tempvarset); 

					$extdvars->{vars}=[]; 
					if ($DEBUG) {
						print $FH "accumulated vars so far:\n",join("|", @tempvarset) ,"\n";
					}
					&expandVars($varslice,$extdvars);
					if ($DEBUG) {
						print $FH "expanded vars:\n",join("|", @{$extdvars->{vars}}) ,"\n";
					}
					my $addedvars = scalar(@{$extdvars->{vars}});
					my $colmnslength = $varslice->[3]- $varslice->[2] +1;
					print $FH "newly expanded vars=",$addedvars,"\tavailable columns=",($colmnslength),"\n";
					if (($tempvarsetlength+$addedvars) > $colmnslength) {
						print $FH "number of available columns is less than the number of vars accumulate\n";
						print $FH "columns are allocated to the last var set only\n";
						
						for (my $jx=0; $jx<@tempvarset; $jx++) {
							push @{$wrkhsh->{$i}}, [$tempvarset[$jx], undef, undef, undef, undef, undef];
						}
						@tempvarset=();
					}
					
					my @newvarset = (@tempvarset, @{$extdvars->{vars}}) ;
					$extdvars->{vars} = [@newvarset];
					@tempvarset=();
					@newvarset=();
					my $tmpvarslength=scalar(@{$extdvars->{vars}}); 
					if ($DEBUG) {
						print $FH "number of accumulated vars=", $tmpvarslength,"\n";
						print $FH "all accumulated vars:",join("|",@{$extdvars->{vars}}) ,"\n";
					}
					$extdvars->{type}=[0, 1, 0];
					$extdvars->{vlen}= $tmpvarslength;
					&expandColumns($varslice,$extdvars);
					if ($DEBUG) {
						print $FH "check returned cols:",join('|', @{$extdvars->{cols}}),"\n";
						print $FH "check returned cole:",join('|', @{$extdvars->{cole}}),"\n";
					}

					if ($DEBUG) { print $FH "current column position:", $::crrntpstn,"\n";}
					for (my $k = 0; $k< @{$extdvars->{vars}}; $k++) {
						push @{$wrkhsh->{$i}},[ $extdvars->{vars}->[$k], $extdvars->{cols}->[$k], $extdvars->{cole}->[$k], $varslice->[4], ($extdvars->{cole}->[$k] - $extdvars->{cols}->[$k] + 1), $varslice->[5] ];
					}
					$::crrntpstn= $varslice->[3];
					if ($DEBUG) { print $FH "current column position moved to:", $::crrntpstn,"\n";}

					# reached the row that has a col spec and reset the flag var:
					$frtcntr = 0;
					# reset temp objects
					&clearVarSets($extdvars);
				}

			} elsif ($varslice->[1] && !$varslice->[2] ){ 
				# /////////////////////////////////////////////////
				# case 3: var_e and no col_s
				# case: [var-e and !col-s] var_i-TO-var_j [plural variables] and 
				# (1) fortran-style format may exist, i.e., $varslice->[4] is not empty, or  
				# (2) neither format nor cols/cole exists, i.e., [2-4] are all empty
				if ($DEBUG) {
					print $FH "\n$j-th iteration enters f1[with TO: plural vars]:\n";
					print $FH "var names: start|end:(", $varslice->[0],"|",$varslice->[1],")\n";
					print $FH "current format:", $varslice->[4],"\n";
				}
				if ($varslice->[4]) {
					$extdvars->{type}=[1, 0, 1];
					if ($DEBUG){
						print $FH "$j-th iteration: this TO comes with a format:\n";
						print $FH "process type=",join("|", @{$extdvars->{type}}) ,"\n";
					}
					# temporally store uncommitted variables
					@tempvarset = @{$extdvars->{vars}};
					$extdvars->{vars}=[]; 
					if ($DEBUG) {
						print $FH "check accumulated vars so far:",join("|", @tempvarset) ,"\n";
					}
					if ($DEBUG) { print $FH "current column position(f1:b):", $::crrntpstn,"\n";}
					
					&expandVars($varslice,$extdvars);
					my @newvarset = (@tempvarset, @{$extdvars->{vars}}) ;
					$extdvars->{vars} = [@newvarset];
					@tempvarset=();
					@newvarset=();
					my $tmpvarslength=scalar(@{$extdvars->{vars}}); 
					if ($DEBUG) {
						print $FH "number of accumulated vars=", $tmpvarslength,"\n\n";
						print $FH "all accumulated vars:",join("|",@{$extdvars->{vars}}) ,"\n\n";
					}
					&expandFormats($varslice,$extdvars);
					my $fmtcvrdvars=$extdvars->{vlen};
					my $offset = $tmpvarslength-$fmtcvrdvars;
					if ( $offset > 0){
						if ($DEBUG) { print $FH "no of vars NOT covered by the expanded format set=",$offset, "\n";}
					}
					if ($DEBUG) {
						print $FH "expanded vars:",join("|",@{$extdvars->{vars}}) ,"\n";
						print $FH "fmt covered vars.length=",$fmtcvrdvars,"\n";
						print $FH "returned cols:",join('|',@{$extdvars->{cols}}),"\n";
						print $FH "returned cole:",join('|',@{$extdvars->{cole}}),"\n";
						print $FH "returned frmt:",join('|',@{$extdvars->{frmt}}),"\n";
						print $FH "returned wdth:",join('|',@{$extdvars->{wdth}}),"\n";
						print $FH "returned dcml:",join('|',@{$extdvars->{dcml}}),"\n";
					}
					
					for (my $k = 0; $k< @{$extdvars->{vars}}; $k++) {
						if ($k < $offset) {
							push @{$wrkhsh->{$i}},[ $extdvars->{vars}->[$k],undef, undef,undef,undef,undef ];
						} else {
							# print $FH "pass the format=",$k,"\n";
							push @{$wrkhsh->{$i}},[ $extdvars->{vars}->[$k], $extdvars->{cols}->[$k-$offset],$extdvars->{cole}->[$k-$offset], $extdvars->{frmt}->[$k-$offset], $extdvars->{wdth}->[$k-$offset], $extdvars->{dcml}->[$k-$offset] ];
						}
					}
					
					if ($DEBUG) { print $FH "current column position moved to(f1:a):", $::crrntpstn,"\n";}
					# reached the row that has a col spec and reset the flag var:
					$frtcntr = 0;
					
					# clear temp objects
					&clearVarSets($extdvars);
					
				} else {
					# no format data; just expand the var set
					$extdvars->{type}=[1, 0, 0];
					@tempvarset = @{$extdvars->{vars}};
					$extdvars->{vars}=[]; 
					if ($DEBUG){
						print $FH "This TO has No format: just expanding var names\n";
						print $FH "process type=",join("|", @{$extdvars->{type}}) ,"\n";
						print $FH "var names accumulated so far:",join("|", @tempvarset) ,"\n";
					}
					&expandVars($varslice, $extdvars);
					if ($DEBUG) {
						print $FH "check expanded vars:",join("|", @{$extdvars->{vars}}) ,"\n";
					}
					# store newly expanded variables
					my @newvarset = (@tempvarset, @{$extdvars->{vars}}) ;
					$extdvars->{vars} = [@newvarset];
					if ($DEBUG) {
						print $FH "all vars accumulated so far:",join("|", @{$extdvars->{vars}}) ,"\n";
					}
					@tempvarset =();
					$frtcntr++;
				}
			} elsif (!$varslice->[1] && !$varslice->[2]){ 
				# /////////////////////////////////////////////////
				# case 4: [!var-e and !col-s] single variable
				# (1) FORTRAN-style format may exit, i.e., $varslice->[4] is not empty or
				# (2) neither f-format nor cols exists, i.e.,$varslice->[2-4] are all empty and later Formats command provides FORTRAN-style formats
				if ($DEBUG) {
					print $FH "\n$j-th iteration enters f2[without a TO]:\n";
					print $FH "var name: start:", $varslice->[0],"\n";
					print $FH "current format:", $varslice->[4],"\n";
				}
				# save a variable until a variable with a format[4] is found
				push @{$extdvars->{vars}}, $varslice->[0];
				if ($varslice->[4]) {
					# the current row has a format spec
					# decode the fortran-like format spec 
					$extdvars->{type}=[0, 0, 1];
					if ($DEBUG) {
						print $FH "This var comes with a Format:\n";
						print $FH "process type=",join("|", @{$extdvars->{type}}) ,"\n";
						print $FH "var length=",scalar(@{$extdvars->{vars}}),"\n";
					}
					&expandFormats($varslice,$extdvars);
					my $fmtcvrdvars = $extdvars->{vlen};
					my $accvarset = scalar(@{$extdvars->{vars}});
					my $offset = $accvarset-$fmtcvrdvars;
					if ($DEBUG) {
						print $FH "expanded vars:",join("\t",@{$extdvars->{vars}}), "\n"; 
						print $FH "returned cols:",join("\t",@{$extdvars->{cols}}), "\n"; 
						print $FH "returned cole:",join("\t",@{$extdvars->{cole}}), "\n"; 
						print $FH "current column position:(f2:b)", $::crrntpstn,"\n";
						print $FH "no of vars with format=",$fmtcvrdvars,"\n";
						print $FH "no of vars =",$accvarset,"\n";
						print $FH "no of vars NOT covered by the expanded format set=",$offset, "\n";
					}
					for (my $k = 0; $k< @{$extdvars->{vars}}; $k++) {
						if ($k < $offset) {
							push @{$wrkhsh->{$i}},[ $extdvars->{vars}->[$k],undef, undef,undef,undef,undef ];
						} else {
							# print $FH "pass the format=",$k,"\n";
							push @{$wrkhsh->{$i}},[ $extdvars->{vars}->[$k], $extdvars->{cols}->[$k-$offset],$extdvars->{cole}->[$k-$offset], $extdvars->{frmt}->[$k-$offset], $extdvars->{wdth}->[$k-$offset], $extdvars->{dcml}->[$k-$offset] ];
						}
					}
					if ($DEBUG) { print $FH "current column position moved to(f2:a):",$::crrntpstn,"\n";}
					
					# reached the row that has a col spec and reset the flag var:
					$frtcntr = 0;
					# clear temp objects
					&clearVarSets($extdvars);
				} else {
					if ($DEBUG) {
						print $FH "This var has NO format spec:\n";
						# print $FH "check all accumulated vars so far:",join("|",@{$extdvars->{vars}}), "\n"; 
					}
					$frtcntr++;
				}
			}

		} # j (raw-var-entry-wise loop)
		if (scalar(@{$extdvars->{vars}})>0){
			# this process is added after ICPSR 13570
			# this process assumes that no cross-card format spec is allowed, 
			# i.e., any format must be specified by the end of each card.
			# ok      /1 V1 To V100  (100F5.2) /2 V101 to V200 (100F5.2)
			# not ok: /1 V1 To V100 /2 V101 to V200 (200F5.2)
			#$DEBUG=1;
			if ($DEBUG) {
				print $FH "contents of extdvars after $i-th iter(card):\n",join("|",@{$extdvars->{vars}}),"\n";
				print $FH "extdvars after $i-th iter (card):length=",scalar(@{$extdvars->{vars}}),"\n";
				print $FH "the above vars are to be committed\n";
			}
			
			for (my $jx=0; $jx<@{$extdvars->{vars}}; $jx++) {
				push @{$wrkhsh->{$i}}, [$extdvars->{vars}->[$jx], undef, undef, undef, undef, undef];
			}
			#$DEBUG=0;
			$self->{_postfrmt}->{$i}=$extdvars->{vars}->[0];
			&clearVarSets($extdvars);
		}
			# recod each card's last column for Formats command
			$self->{_lastcolumn}->{$i}=$::crrntpstn;
	} # i (card-wise loop)
	
	# /////////////////////////////////////////////////////////////////////////////
	if ($DEBUG) {print $FH "\nwork hsh:\n",Dumper($wrkhsh), "\n";}
$DEBUG=0;
	
if ($DEBUG){
	print $FH "\nwork  var hsh:\n";
	&printVarHsh($wrkhsh, $::dlSpec);
	print $FH "\nlocation mapping file:\n";
	print $FH join("\t", ("0[ID]","1[card]","2[var]","3[cls]","4[cle]","5[fmt]","6[wdth]","7[dcml]")),"\n";
	&printLocationHsh($wrkhsh, $::dlSpec,"$self->{_prcssOptn}->{fileID}" );
}	

#$DEBUG=0;	
	
	$self->{_location}=$wrkhsh;
	# print $FH "\nlocation:\n",Dumper($self->{_location}), "\n";
	
if ($DEBUG){
	print $FH "important params(dlSpec):\n",Dumper($::dlSpec), "\n";
	print $FH "important params(_fileDscr:b):\n",Dumper($self->{_fileDscr}), "\n";
}
	while (my ($k, $v) = each (%{$::dlSpec})) {
		$self->{_fileDscr}->{$k} = $v;
	}
	if ($DEBUG){print $FH "important params(_fileDscr:a):\n",Dumper($self->{_fileDscr}), "\n";}
	
	# create variable name list
	my $vnlst = [];
	my $vnhsh ={};
	my $varcntr=0;
	if ($DEBUG) {print $FH "no of vars per card\n";}

	foreach my $i (@{$self->{_fileDscr}->{CARDORDER}}){
		push @{$self->{_novarset}}, scalar(@{$self->{_location}->{$i}});
		for (my $j=0; $j<@{$self->{_location}->{$i}}; $j++){
			push @{$vnlst}, $self->{_location}->{$i}->[$j]->[0];
			$varcntr++;
			$vnhsh->{$self->{_location}->{$i}->[$j]->[0]}=$varcntr;
		}
		if ($DEBUG) {print $FH $i,"-th card=",scalar(@{$self->{_location}->{$i}}),"\n";}
	}
	$self->{_varNameA}=$vnlst;
	$self->{_varNameH}=$vnhsh;
	if ($DEBUG) {
		print $FH "no of vars =",$varcntr,"\n";
		print $FH "no of vars set=",join("-",@{$self->{_novarset}}),"\n";
	}
	$self->{_fileDscr}->{varQnty}=$varcntr;
	
	if ($DEBUG) {
		print $FH "variable name array/hash is created in Data list section\n";
	}
	my $dsgrcntr =0;
	for (my $i=0;$i<@{$vnlst};$i++){
		if (($i+1) ne $vnhsh->{$vnlst->[$i]} ) {
			print $FH $i,"-th\t",$vnlst->[$i],"\thash=",$vnhsh->{$vnlst->[$i]},"\n";
			$dsgrcntr++;
		}
	}
	if ($DEBUG) {
		if ($dsgrcntr){print $FH "$dsgrcntr pairs disagreed\n";}
	}
	&setVarTypeTbl($self);
	if ($DEBUG) {
		print "charVarTbl:\n",Dumper($self->{_charVarTbl}),"\n";
		print "dcmlVarTbl:\n",Dumper($self->{_dcmlVarTbl}),"\n";
	}
	
	undef($::rawvarhsh);
	undef($::dlSpec);
	#undef($::crdcntr);
	#undef($::checkvarcounter);
	#undef($::checkvar);
	#undef($::sbcmnd);
}



sub clearVarSets {
	my $extdvars = shift;
	$extdvars->{vars}=[];
	$extdvars->{cols}=[];
	$extdvars->{cole}=[];
	$extdvars->{frmt}=[];
	$extdvars->{wdth}=[];
	$extdvars->{dcml}=[];
	$extdvars->{type}=[0,0,0];
	$extdvars->{vlen}=undef;
}



sub setVarTypeTbl{
	my $self =shift;
	# varname => 'a'
	my $charVarTbl={};
	# varname => i > 0
	my $dcmlVarTbl={};
	my $varcntr=-1;
	my $chrCntr=0;
	my $dcmlCntr=0;
	foreach my $i (@{$self->{_fileDscr}->{CARDORDER}}){
 		# 0[var] 1[cls] 2[cle] 3[fmt] 4[wdth] 5[dcml])) 
		for (my $j=0; $j<@{$self->{_location}->{$i}}; $j++){
			$varcntr++;
			if (lc($self->{_location}->{$i}->[$j][3]) eq 'a'){
				$charVarTbl->{$self->{_varNameA}->[$varcntr]}='a';
				$chrCntr++;
				$self->{_varFormat}->{$self->{_varNameA}->[$varcntr]}='A';

			} elsif ( ($SPSS_FRMT_CATGRY_TABLE->{uc($self->{_location}->{$i}->[$j][3])} eq 'date') ||
			($SPSS_FRMT_CATGRY_TABLE->{uc($self->{_location}->{$i}->[$j][3])} eq 'time') ){
				my $fmt= $self->{_location}->{$i}->[$j][3]; 
				$charVarTbl->{$self->{_varNameA}->[$varcntr]}=$fmt;
				
				$chrCntr++;
				$self->{_varFormat}->{$self->{_varNameA}->[$varcntr]}=uc($fmt);
				my $fmtn=$self->{_location}->{$i}->[$j][4];
				if (($self->{_location}->{$i}->[$j][5] eq '')||($self->{_location}->{$i}->[$j][5] ==0)) {
				} else {
					$fmtn=$fmtn . '.' . $self->{_location}->{$i}->[$j][5];
				}
				$self->{_formatName}->{$self->{_varNameA}->[$varcntr]}= uc($fmt) . $fmtn;
				
				
				
			} elsif ($self->{_location}->{$i}->[$j][5] > 0){
				$self->{_varFormat}->{$self->{_varNameA}->[$varcntr]}='F';
			} else {
				$self->{_varFormat}->{$self->{_varNameA}->[$varcntr]}='I';
			}
			
			if ($self->{_location}->{$i}->[$j][5] > 0){
				if ($SPSS_FRMT_CATGRY_TABLE->{uc($self->{_location}->{$i}->[$j][3])} eq 'time') {
				} else {
					$dcmlVarTbl->{$self->{_varNameA}->[$varcntr]}= $self->{_location}->{$i}->[$j][5];
					$dcmlCntr++;
				}
			}
		}
	}
	$self->{_charVarTbl}=$charVarTbl;
	$self->{_dcmlVarTbl}=$dcmlVarTbl;
	$self->{_noCharVar}=$chrCntr;
	$self->{_noDcmlVar}=$dcmlCntr;
}


# //////////////////////////////////////////////////////////////////////////////
# non-oo-interface subroutines
# //////////////////////////////////////////////////////////////////////////////

sub printLocationHsh {
	my $varHsh = shift;
	my $fileSpec = shift;
	my $fileID= shift;
	my ($varid, $varcntr);
	$varcntr=0;
	print scalar(@{$fileSpec->{CARDORDER}}),"\n";
	foreach my $i (@{$fileSpec->{CARDORDER}}){
 		print join ( "\t", ("0[ID]","1[card]","2[var]","3[cls]","4[cle]","5[fmt]","6[wdth]","7[dcml]")), "\n";
		for (my $j=0; $j<@{$varHsh->{$i}}; $j++){
			$varcntr++;
			$varid = &getVarID($fileID, $varcntr);
			print join("\t",($varid, $i, @{$varHsh->{$i}->[$j]})),"\n";
		}
	}
}

sub printRawVarHsh {
	my $varHsh = shift;
	my $cardOrder = shift;
	foreach my $i (@{$cardOrder->{CARDORDER}}){
		print "\n", $i, "-th card: raw-var size=", scalar(@{$varHsh->{$i}}),"\n";
 		print join ( "\t", ("(i,j):","0[vrs]", "1[vre]", "2[cls]", "3[cle]", "4[fmt]", "5[dcml]")), "\n";
		for (my $j=0; $j<@{$varHsh->{$i}}; $j++){
			print "($i,$j):\t", join("\t",@{$varHsh->{$i}->[$j]}),"\n";
		}
	}
}


sub printVarHsh {
	my $varHsh = shift;
	my $cardOrder = shift;
	foreach my $i (@{$cardOrder->{CARDORDER}}){
		print "\n", $i, "-th card: raw-var size=", scalar(@{$varHsh->{$i}}),"\n";
 		print join ( "\t", ("(i,j):","0[var]","1[cls]","2[cle]","3[fmt]","4[wdth]","5[dcml]")), "\n";
		for (my $j=0; $j<@{$varHsh->{$i}}; $j++){
			print "($i,$j):\t", join("\t",@{$varHsh->{$i}->[$j]}),"\n";
		}
	}
}

# ////////////////////////////////////////////////////////////
# Formats-related methods
# ////////////////////////////////////////////////////////////


sub read_Formats{
	my $self= shift;
	# Debugging parameters for Parse::RecDescent
	# $::RD_TRACE = 'true';
	# $::RD_HINT = 'true';
	# $::RD_ERRORS = 'true';
	
	
	my $DEBUG=0;
	my $FH   = $self->{_prcssOptn}->{LOGFILE};
	if ($DEBUG) {print $FH "start parsing Formats\n";}

	my $svmode = $self->{_prcssOptn}->{StorageMode};
	if ($svmode eq 'external') {
		my $dumpflname = $self->{_cmmndStrng}->{Formats};
		open (CMD, "$dumpflname") or die "cannot open frmts file";
	} elsif ($svmode eq 'internal') {
		if ($DEBUG) {print $FH "text to be parsed:",$self->{_cmmndStrng}->{Formats},"\n";}
	}
	
	
	$self->{_fmtTbl}={};
	$self->{_fmtVrMpTbl}={};
	
	
	# formats PT1_1 to PT3_209 (F9.0) /PT4_1 to PT4_3 (F9.1).    
	# global vars
	$::fmtvlst=[];
	$::fmtvrnmlst=[];
	$::fmtvrnmlst2=[];
	$::fmtscndvr = 0;
	$::fmtvrcntr=-1;
	$::fmtscndvron={};
	
	
my $grammar = q(
	file: formats
		formats: /formats/ formatsbody(s) trmntr(?)
		{
		}
			formatsbody: varname frmts specsep(?)
			{
				if (scalar(@{$item[2]})) {
					push @{$::fmtvlst},[ @{$::fmtvrnmlst},$item[2]];
				}
				
				$::fmtvrnmlst=[];
				[ $item[1], $item[2] ]
			}
				varname: varnamestart varnamecontin(s?)
				{
					for (my $i=0; $i<=$#{$item[2]};$i++){
						if ( ($item[2][$i][0] ne ',') && (defined($item[2][$i][0])) ){
							push @{$::fmtvrnmlst2}, $item[2][$i][0] ;
						}
						if ($item[2][$i][1]){
							push @{$::fmtvrnmlst2}, $item[2][$i][1] ;
						}
					}
					$::fmtvrcntr++;
					if (scalar(@{$::fmtvrnmlst2})) {
						$::fmtscndvr++;
						$::fmtscndvron->{$::fmtvrcntr}="1";
					}
					push @{$::fmtvrnmlst}, [ [$item[1]],  $::fmtvrnmlst2 ];
					#_ print "varnameset=\n", &::Dumper($::fmtvrnmlst);
					$::fmtvrnmlst2 =[];
				}
					varnamestart: /^[a-z@\\043][a-z0-9._@\\043\\044]{0,63}/i
					{
						#_ print "varname(s):",$item[1],"\n";
						$item[1];
					}
					varnamecontin: continmrk(?) varnameend
					{
						#_ print "cntin=",$item[1][0],"\n";
						#_ print "end=",$item[2],"\n";
						[$item[1][0], $item[2]]
					}
						continmrk: /to|,| /i
						{
							if ($item[1] eq ' '){$item[1]=',';}
							$item[1];
						}
						varnameend:/[a-z@\\043][a-z0-9._@\\043\\044]{0,63}/i
						{
							#_ print "varname(e):",$item[1], "\n";
							$item[1];
						}
				frmts:'(' frmtspec ')'
				{
					#_ print "format spec=\n", &::Dumper($item[2]);
					$item[2]
				}
					frmtspec: frmtype wdth dcml(?)
					{
						[$item[1], $item[2], $item[3][0]]
					}
						frmtype: /(f|comma|dot|dollar|pct|z|datetime|adate|edate|jdate|sdate|date|qyr|moyr|wkyr|dtime|time|wkday|month|a|n|e)/i
						{
							#_ print "frmtspec=", $item[1],"\n";
							$item[1];
						}
						wdth: /[0-9]+/
						{
							#_ print "wdth=", $item[1], "\n";
							$item[1];
						}
						dcml: '.' /[0-9]+/
						{
							#_ print "dcml",$item[1], "\n";
							$item[2];
						}
				specsep: '/'
			trmntr:'.'
	); # end of grammar
	my $parser = Parse::RecDescent->new($grammar);
	my $text; 
	{
		if ($svmode eq 'internal') { 
			$text = $self->{_cmmndStrng}->{Formats};
		} elsif ($svmode eq 'external') {
			local $/ = undef;
			$text = <CMD>;
		}
	}
	$parser->file($text); 
	if ($svmode eq 'external') {close(CMD);}
#$DEBUG=1;

if ($DEBUG){
	print $FH "\nformats (before extension):\n",Dumper($::fmtvlst), "\n";
	if ($::fmtscndvr) {
		print $FH "$::formats value(s) assigned to multiple variables:\n";
	}
	for (my $i=0;$i<@{$::fmtvlst}; $i++) {
		print $FH "row no=",$i,"\tvar1=",$::fmtvlst->[$i][0][0][0],"\tvar2=",join('|',@{$::fmtvlst->[$i][0][1]}),"\n";
	}
	print $FH "\nnon-empty 2nd var(h)=",Dumper($::fmtscndvron),"\n";
}
	# expand varsets if exists
	my $fmtvrmp={};
	my $fmttbl={};
	for (my $i=0; $i<@{$::fmtvlst}; $i++){
		$fmttbl->{$::fmtvlst->[$i][0][0][0]}= $::fmtvlst->[$i][1];
		if (exists($::fmtscndvron->{$i})) {
			my $varslice= $::fmtvlst->[$i][0];
			#if ($DEBUG) {print $FH "\nvarslice=",join('|',@{$varslice->[1]}),"\n";}
			&expndvllbl2($varslice, $fmtvrmp, $self->{_varNameA});
			#print "var1=",$::fmtvlst->[$i][0][0][0],"\tvar keys=",join("|",keys(%{$fmtvrmp})),"\n";
		} else {
			#if ($DEBUG) {print $FH "\nvar1=",$::fmtvlst->[$i][0][0][0],"\tfmt set set=",join("|",@{$::fmtvlst->[$i][1]}),"\n";}
			$fmtvrmp->{$::fmtvlst->[$i][0][0][0]}=$::fmtvlst->[$i][0][0][0];
		}
	}
	
if ($DEBUG){
	print $FH "\n\nformts table:\n",Dumper($fmttbl),"\n\n";
	print $FH "formats hash table:\n",Dumper($fmtvrmp),"\n";
}

	# store value-label table and its associated var-mapping table
	$self->{_fmtTbl}=$fmttbl;
	$self->{_fmtVrMpTbl}=$fmtvrmp;
	# print "location=", Dumper($self->{'_location'});
	# if (scalar(keys(%{$self->{'_postfrmt'}}))){
		
	#	print $FH "How many cards=", scalar(keys(%{$self->{'_postfrmt'}}))," formats must update the location table\n";
		
		
	my ($cols, $cole, $frmt);
	foreach my $i (@{$self->{_fileDscr}->{CARDORDER}}){
	
		my $cloffset = $self->{_lastcolumn}->{$i};
		if ($DEBUG){print $FH "last var's ending column=",$cloffset,"\n";}
		
		for (my $jz=0; $jz<@{$self->{_location}->{$i}}; $jz++){
			my $var = $self->{_location}->{$i}->[$jz]->[0];
			
			if ($DEBUG){print $FH "var=",$var,"";}
			if (exists($fmttbl->{$fmtvrmp->{$var}})){
				$frmt=$fmttbl->{$fmtvrmp->{$var}};
				if ($DEBUG){print $FH "\tvar=",$var,"\t",$fmtvrmp->{$var},"\tformat=",join('-',@{$fmttbl->{$fmtvrmp->{$var}}}),"";}
				# update start(1), end(2), frmt(3), wdth(4), dcml(5)
				$cols = $cloffset + 1;
				$cole = $cloffset + $frmt->[1];

				$self->{_location}->{$i}->[$jz][1] = $cols ;
				$self->{_location}->{$i}->[$jz][2] = $cole ;
				if (lc($frmt->[0]) eq 'a'){
					$self->{_location}->{$i}->[$jz][3] = 'A';
				}

				$self->{_location}->{$i}->[$jz][4] = $frmt->[1];
				if ($frmt->[2]) {
					$self->{_location}->{$i}->[$jz][5] = $frmt->[2];
				}
				$cloffset= $cole;
				if ($DEBUG){print $FH "\tformat=",join('-',@{$self->{_location}->{$i}->[$jz]}),"\n";}
			} else {
				if ($DEBUG){print $FH "\tthis var is not listed in the processing list: skipp this var\n";}
			}
		}
	}

	# clear global variables
	$::fmtvrnmlst=[];$::fmtvrnmlst2 =[];$::fmtvlst=[];$::fmtscndvr =0;$::fmtvrcntr=-1;
} 


# ////////////////////////////////////////////////////////////
# Varlabel-related methods
# ////////////////////////////////////////////////////////////

sub read_Varlabel{
	my $self= shift;
	# Debugging parameters for Parse::RecDescent
	# $::RD_TRACE = 'true';
	# $::RD_HINT = 'true';
	# $::RD_ERRORS = 'true';

	my $DEBUG=0;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	if ($DEBUG) {print $FH "start parsing the var labels\n";}
		
	my $svmode = $self->{_prcssOptn}->{StorageMode};
	if ($svmode eq 'external') {
		my $dumpflname = $self->{_cmmndStrng}->{VarLabel};
		open (CMD, "$dumpflname") or die "cannot open varlab file";
	} elsif ($svmode eq 'internal') {
		if ($DEBUG) {print $FH "text to be parsed:",$self->{_cmmndStrng}->{VarLabel},"\n";}
	}
	
	
	
	$self->{_varLabel}={};
	
	
	
	# global vars
	$::vrlblst={};
	
	my $grammar = q(
	# start of grammar
		file: varlbl
			varlbl: /var lab/ var(s)
				var: varname sep(?) label  prd(?) trmntr(?)
				{
					$::vrlblst->{$item[1]}=$item[3];
				}
					varname: /[a-z@\\043][a-z0-9._@\\043]{0,7}/i
					{
						#print "\t\tvarname(0):", $item[1], "\n";
						$::qmrkdmy = 'qmrkno';
						$::qmrklbl = 'qmrkoff';
						$item[1]
					}
					sep: /,|=/
					label: qmrks(?) <matchrule:$::qmrklbl> qmrke(?)
					{
						#print "\t\tlabel(b):", $item[2], "\n";
						# foreach my $key (@{$::Entref}) {$item[2] =~ s/$key/$::EnternalRef->{$key}/g;}
						#print "\t\tlabel(a):", $item[2], "\n";
						$item[2]
					}
						qmrks: /^[\\047\\042]/
						{
							#print 'qmrks:', $item[1] , "\n";
							if ($item[1]) {$::qmrkdmy ='qmrkyes';$::qmrklbl ='qmrkon';}
							$::qstrt = $item[1]
						}
						qmrkon: /[^"$::qstrt"]{1,255}/
						{
							$item[1] =~ s/\s+$//;
							#print "\t\t\tlabel(0):", $item[1], "\n";
							$item[1]
						}
						qmrkoff: /[^\\057]{1,255}/
						{
							$item[1] =~ s/\s+$//;
							#print "\t\t\tlabel(0):", $item[1], "\n";
							$item[1]
						}
						qmrke: <matchrule:$::qmrkdmy>
							qmrkyes: /$::qstrt/
							qmrkno: //
					prd:/[.]/
					trmntr: '/'
	); 
	# end of grammar
	my $parser = Parse::RecDescent->new($grammar);
	my $text; 
	{
		if ($svmode eq 'internal') {
			$text = $self->{_cmmndStrng}->{VarLabel};
		} elsif ($svmode eq 'external') {
			local $/ = undef;
			$text = <CMD>;
		}
	}
	$parser->file($text); 
	if ($svmode eq 'external') {close(CMD);}
	
	if ($DEBUG) {print $FH "\nvariable label:\n",Dumper($::vrlblst), "\n";}
	$self->{_varLabel}= $::vrlblst;
	if ($DEBUG) {print $FH "varlbl:\n",Dumper($self->{_varLabel}),"\n";}
	# clear variables
	undef($::vrlblst);
}

# ////////////////////////////////////////////////////////////
# MissValues-related methods
# ////////////////////////////////////////////////////////////
sub read_MissValues {
	my $self= shift;
	# Debugging parameters for Parse::RecDescent
	# $::RD_TRACE = 'true';
	# $::RD_HINT = 'true';
	# $::RD_ERRORS = 'true';

	my $DEBUG=0;
	my $FH   = $self->{_prcssOptn}->{LOGFILE};
	if ($DEBUG) {print $FH "start parsing the missing value\n";}
	
	my $svmode = $self->{_prcssOptn}->{StorageMode};
	if ($svmode eq 'external')  {
		my $dumpflname = $self->{_cmmndStrng}->{MisValue};
		open (CMD, "$dumpflname") or die "cannot open misval file";
	} elsif ($svmode eq 'internal') {
		if ($DEBUG) { print $FH "text to be parsed:",$self->{_cmmndStrng}->{MisValue},"\n";}
	}
	
	
	$self->{_mssvlTbl}={};
	$self->{_mvVrMpTbl}={};
	$self->{_mvTpCdTbl}={};
	$self->{_mvVarAll}=0;
	
	
	$::mvlst = [];				# $::vllblst=[];
	$::mvscndvr = 0;			# $::vlscndvr
	$::mvvllsttype = 'numeric';
	$::mvclsmrk = '';
	$::mvqmrk = 'qoff';
	$::mv3rdtoken='part3null';
	$::mvvrnmlst=[];			# $::vrnmlst=[];
	$::mvvrnmlst2=[];			# $::vrnmlst2=[];
	$::mvpvltmp=[];				# $::pvltmp=[];
	$::mvvrcntr=-1;				# $::vlvrcntr=-1;
	$::mvscndvron={};			# $::vlscndvron={};
	$::mvrngtyp=0;
	$::mvrngon={};
	$::mvvarall=0;
	# SPSS SAV file:  missing value: type coding
	#  0	No missing value
	#  1	One point type
	#  2	Two point types
	#  3	Three point types
	# -2	One range type
	# -3	One range type and one point type
	
	my $grammar = q(
		# start of grammar
		file: missingvals
		missingvals: /mis val/  mssngval(s)
		mssngval: varname '(' range ')' trmntr(?)
		{
			#print "\trange.length:", $#{$item[3]}, "\n";
			#print "\trange.0:", $item[3][0], "\n";
			# print "var=",$::mvvrnmlst->[0][0][0],"\trange=",$::mvrngtyp,"\n";
			#push @{$::mvlst},[@{$::mvvrnmlst}, [ @{$::mvpvltmp} ]];
		#	push @{$::mvlst},[@{$::mvvrnmlst}, [ @{$::mvpvltmp} ]];
			push @{$::mvlst},[@{$::mvvrnmlst}, $::mvpvltmp ];
			if ($::mvrngtyp) {$::mvrngon->{$::mvvrnmlst->[0][0][0]}=1;$::mvrngtyp=0;}
			$::mvvrnmlst=[]; $::mvpvltmp=[];
			[ $item[1], $item[3] ]
		}
			varname: varnamestart varnamecontin(s?)
			{
				#print "\tvarname_s(1):", $item[1], "\n";
				#print "\tvarname_e_size(1):", $#{$item[2]}, "\n";
				for (my $i=0; $i<=$#{$item[2]};$i++) {
					#print "\tvarnamesizes(1):", $item[2][$i], "\n";
					#if ($item[2][$i] ne ',') {push @{$::mvvrnmlst2}, $item[2][$i];}
					if (($item[2][$i][0] ne ',') && (defined($item[2][$i][0])) ){
						#print "\tvarnamee(within1):", $item[2][$i][0], "\n";
						push @{$::mvvrnmlst2}, $item[2][$i][0] ;
					} 
					if ($item[2][$i][1]){
						push @{$::mvvrnmlst2}, $item[2][$i][1] ;
					}
				}
				$::mvvrcntr++;
				#print "\t\tvarlist2:", join('|', @{$::mvvrnmlst2}), "\n";
				if (scalar(@{$::mvvrnmlst2})) {
					$::mvscndvr++;
					$::mvscndvron->{$::mvvrcntr}="1";
				}
				push @{$::mvvrnmlst}, [ [$item[1]],  [@{$::mvvrnmlst2}]];
				$::mvvrnmlst2 =[];
				#$item[1]
			}
				varnamestart: /^all$|[a-z@\\043][a-z0-9._@\\043\\044]{0,7}/i
				{
					#print "\t\tvarnams(0):",$item[1], "\n";
					if (lc($item[1]) eq 'all'){$::mvvarall=1;}
					$item[1];
				}
				varnamecontin: continmrk(?) varnameend
				{
					#print "varnamecontin:",$item[1][0], "\t", $item[2],"\n";
					[$item[1][0], $item[2]]
				}
					continmrk:/to|,| /i
					{
						if ($item[1] eq ' '){$item[1]=',';}
						$item[1];
					}
					varnameend:/[a-z@\\043][a-z0-9._@\\043\\044]{0,7}/i
					{
						$item[1];
					}
			range: vallst(s)
			{
				$item[1];
			}
				vallst: opnq(?) <matchrule:$::mvvllsttype> <matchrule:$::mvqmrk> endmrk(?)
				{
					#print "\tvallst size:", $#{$item[2]}, "\n";
					#print "\topnq:", $item[1][0], "\n";
					#print "\tvalues:", $item[2][0], "\n";
					#print "\tvalues:", $item[2][1], "\n";
					#print "\tendmrk:", @{$item[4]}, "\n";
					if (defined($item[1][0])) {
						#print "\topnq yes\n";
						push @{$::mvpvltmp}, [$item[2]];
					} elsif (defined($item[2][1])) {
						push @{$::mvpvltmp}, [$item[2][0], $item[2][1]];
					} else {
						push @{$::mvpvltmp}, [$item[2][0]];
					};
					#print &::Dumper($::mvpvltmp);
					#[$item[2][0], $item[2][1], @{$item[4]} ]
				}
				opnq: /^(\\042|\\047|\\x91|\\x92|\\x93|\\x94)/
				{
					# 042("),047('), x91('), x92('), x93("), x94(")
					#print "\t\tqmark(opnq):", $item[1], "\n";
					$::mvvllsttype = 'string';
					$::mvqmrk = 'qon';
					$::mvclsmrk = $item[1];
					$item[1];
				}
				string: <skip: "\\t*|\\n*"> stringbody
				{
					#print "\t\tstring:", $item[2], "\n";
					$item[2];
				}
					stringbody: /[^"$::mvclsmrk"]{1,}/
					{
						#print "\t\t\tstringbody:", $item[1], "\n";
						#if ($item[1] eq ' ') {print "\t\t\tspace was found\n";}
						$::mvvllsttype = 'numeric';
						$item[1];
					}
				qon:/["$::mvclsmrk"]/
				{
					$::mvqmrk = 'qoff';
				}
				qoff://
				{
					#print "\t\tpass the off\n";
					$item[1];
				}
						numeric: part1 part2(?) <matchrule:$::mv3rdtoken>
						{
							if ($::mvqmrk ne 'qon') {$::mvqmrk = 'qoff'}
							if (defined($item[3])){
								#print "numeric(all/range):[" . $item[1] . '-' . $item[3] . "]\n";
							} elsif (defined($item[1])){
								#print "numeric(all):[" . $item[1] . "]\n";
							}
							
							if (defined($item[1]) && defined($item[3])  && $item[3]){
								# print "After $::mvvrcntr : range type was found\n";
								$::mvrngtyp=1;
								[$item[1], $item[3] ];
							} elsif (defined($item[1])){
								[$item[1] ];
							}
							
						}
							part1: /[0-9-.]{1,}|^lo(west|)/i
							{
								#print "\t\t\tnumeric(1):", $item[1], "\n";
								if ($item[1] =~ m/^lo(west|)/i) { $item[1] = 'LOWEST';} else {$item[1] = $item[1] + 0;}
								$item[1];
							}
							part2: /^thru/i
							{
								if ($item[1]) {
									$::mv3rdtoken='part3';
								}
								$item[1];
							}
							part3null: //
							part3: /[0-9-.]{1,}|^hi(ghest|)/i
							{
								#print "\t\t\tnumeric(3):", $item[1], "\n";
								if ($item[1] =~ m/^hi(ghest|)/i) { $item[1] = 'HIGHEST';} else {$item[1] = $item[1] + 0;}
								$::mv3rdtoken='part3null';
								$item[1];
							}
				endmrk:/,| /
				{
					#print "\t\t\tpass the comma\n";
					$item[1];
				}
		trmntr: '/'
		{
			$item[1];
		}
	); # end of grammar
	my $parser = Parse::RecDescent->new($grammar);
	my $text; 
	{
		if ($svmode eq 'internal') { 
			$text = $self->{_cmmndStrng}->{MisValue};
		} elsif ($svmode eq 'external') {
			local $/ = undef;
			$text = <CMD>;
		}
	}
	$parser->file($text); 
	if ($svmode eq 'external') {close(CMD);}
	
if ($DEBUG){
	print $FH "\nmissing values (before extension):\n",Dumper($::mvlst), "\n\n";
	if ($::mvscndvr) {print $FH "$::mvscndvr missng value set(s) assigned to multiple variables:\n";}
	for (my $i=0;$i<@{$::mvlst}; $i++) {
		print $FH "row no=",$i,"\tvar1=",$::mvlst->[$i][0][0][0],"\tvar2=",join("|", @{$::mvlst->[$i][0][1]}),"\n";
	}
	print $FH "\nnon-empty 2nd var(h)=",Dumper($::mvscndvron),"\n";
}
	# create variable name list
	if (scalar(@{$self->{_varNameA}}) == 0 ){
		my $vnl = [];
		my $varcntr=0;
		foreach my $i (@{$self->{_fileDscr}->{CARDORDER}}){
			for (my $j=0; $j<@{$self->{_location}->{$i}}; $j++){
				push @{$vnl}, $self->{_location}->{$i}->[$j]->[0];
			}
		}
		$self->{_varNameA}=$vnl;
		if ($DEBUG) {print $FH "variable name list is created in the missing value section\n"}
	}
	# expand varsets if exists
	my $mvvrmp={};
	my $mvtbl={};
	for (my $i=0; $i<@{$::mvlst}; $i++){
		#if (!exists($mvtbl->{$::mvlst->[$i][0][0][0]})) {
		#	$mvtbl->{$::mvlst->[$i][0][0][0]}=[];
		#}
		#print Dumper($::mvlst->[$i][1]);
		#push @{$mvtbl->{$::mvlst->[$i][0][0][0]}}, $::mvlst->[$i][1];
		$mvtbl->{$::mvlst->[$i][0][0][0]}= $::mvlst->[$i][1];
		if (exists($::mvscndvron->{$i})) {
			my $varslice= $::mvlst->[$i][0];
			#if ($DEBUG) {print $FH "varslice=",join('|',@{$varslice->[1]}),"\n";}
			&expndvllbl2($varslice, $mvvrmp, $self->{_varNameA});
			#print "var1=",$::mvlst->[$i][0][0][0],"\tvar keys=",join("|",keys(%{$mvvrmp})),"\n";
		} else {
			if ($DEBUG) {print $FH "var1=",$::mvlst->[$i][0][0][0],"\tvalue-label set=",join("|",@{$::mvlst->[$i][1][0]}),"\n";}
			$mvvrmp->{$::mvlst->[$i][0][0][0]}=$::mvlst->[$i][0][0][0];
		}
	}
if ($DEBUG){
	print $FH "\n\nmissing value table:\n",Dumper($mvtbl),"\n\n";
	print $FH "missing value hash table:\n",Dumper($mvvrmp),"\n";
	print $FH "range type hash table(missVal):\n",Dumper($::mvrngon),"\n\n";
}
	my $mvtypcd = &getMVtypeCode($mvtbl, $::mvrngon);
if ($DEBUG) {
	print $FH "\n\nmissing value table:\n",Dumper($mvtbl),"\n\n";
	print $FH "mv code table:\n",Dumper($mvtypcd),"\n\n";
}
	# store value-label table and its associated var-mapping table
	if ($::mvvarall) {$self->{_mvVarAll}=1;}
	$self->{_mssvlTbl} =$mvtbl;
	$self->{_mvVrMpTbl}=$mvvrmp;
	$self->{_mvTpCdTbl}=$mvtypcd;

	# clear global variables
	#$::mvvrnmlst=[];$::mvvrnmlst2=[];$::mvpvltmp =[];$::mvlst = [];$::mvvllsttype = '';
	#$::mvclsmrk = '';$::mvqmrk = '';$::mvvrcntr=-1;
}

# ////////////////////////////////////////////////////////////
# Recode-related methods
# ////////////////////////////////////////////////////////////

sub read_Recode{
	my $self= shift;
	# Debugging parameters for Parse::RecDescent
	# $::RD_TRACE = 'true';
	# $::RD_HINT = 'true';
	# $::RD_ERRORS = 'true';

	my $DEBUG=0;
	my $FH   = $self->{_prcssOptn}->{LOGFILE};
	
	if ($DEBUG) {print $FH "start parsing the Recode\n";}
	my $svmode = $self->{_prcssOptn}->{StorageMode};
	if ($svmode eq 'external') {
		my $dumpflname = $self->{_cmmndStrng}->{RcdValue};
		open (CMD, "$dumpflname") or die "cannot open recode file";
	} elsif ($svmode eq 'internal') {
		if ($DEBUG) {print $FH "text to be parsed:",$self->{_cmmndStrng}->{RcdValue},"\n";}
	}

	
	$self->{_rdTbl}={};
	$self->{_rdVrMpTbl}={};
	$self->{_rdTpCdTbl}={};
	$self->{_rdVarAll}=0;
	
	#use vars qw(@vrnmlst @pvltmp @mssngvllst $secondvarm );
	$::rdvlst=[];		# $::mvlst = [];	# $::vllblst=[];  @recodelst = ();
	$::rdvrnmlst=[];	# $::mvvrnmlst=[];	# $::vrnmlst=[];  @vrnmlst=();
	$::rdvrnmlst2=[];	# $::mvvrnmlst2=[];	# $::vrnmlst2=[]; @vrnmlst2 =();
	$::rdscndvr = 0;	# $::mvscndvr=0;	# $::vlscndvr
	$::rdvrcntr=-1;		# $::mvvrcntr=-1;	# $::vlvrcntr=-1;
	$::rdpvltmp=[];		# $::mvpvltmp=[];	# $::pvltmp=[]; @pvltmp=();
	$::rdscndvron={};	# $::mvscndvron={};	# $::vlscndvron={};
	
	$::rdrngtyp=0;
	$::rdrngon={};
	$::rdvarall=0;

	#		$::mvvllsttype = 'numeric';
	#		$::mvclsmrk = '';
	#		$::mvqmrk = 'qoff';

	my $grammar = q(
		# start of grammar
		file: recodevals
			recodevals: /recode/ recodeval(s)
				recodeval: varname body trmntr(?)
				{
					if (scalar(@{$item[2]})) {
						push @{$::rdvlst},[ @{$::rdvrnmlst},$::rdpvltmp];
					}
					# print "var=",$::rdvrnmlst->[0][0][0],"\trange=",$::rdrngtyp,"\n";
					if ($::rdrngtyp) {$::rdrngon->{$::rdvrnmlst->[0][0][0]}=1;$::rdrngtyp=0;}
					$::rdpvltmp=[]; $::rdvrnmlst=[];
					[ $item[1], $item[2] ]
				}
					varname: varnamestart varnamecontin(s?)
					{
						#print "\tvarname_s(1):", $item[1], "\n";
						#print "\tvarname_e_size(1):", $#{$item[2]}, "\n";
						for (my $i=0; $i<=$#{$item[2]};$i++){
							#print "\tvarnamesizes(1):", $item[2][$i], "\n";
							if ( ($item[2][$i][0] ne ',') && (defined($item[2][$i][0])) ){
								push @{$::rdvrnmlst2}, $item[2][$i][0] ;
							}
							if ($item[2][$i][1]){
								push @{$::rdvrnmlst2}, $item[2][$i][1] ;
							}
						}
						$::rdvrcntr++;
						# print "varlist2:", join('|', @{$::rdvrnmlst2}), "\n";
						if (scalar(@{$::rdvrnmlst2})) {
							$::rdscndvr++;
							$::rdscndvron->{$::rdvrcntr}="1";
						}
						push @{$::rdvrnmlst}, [ [$item[1]],  [@{$::rdvrnmlst2}]];
						$::rdvrnmlst2 =[];
						#$item[1]
					}
						varnamestart: /^all$|[a-z@\\043][a-z0-9._@\\043\\044]{0,7}/i
						{
							#print "\t\tvarname(0s):",$item[1], "\n";
							if (lc($item[1]) eq 'all'){$::rdvarall=1;}
							$item[1]
						}
						varnamecontin: continmrk(?) varnameend
						{
							#print "\t\tvarnamecontin:",$item[1][0], "\t", $item[2],"\n";
							[$item[1][0], $item[2]]
						}
							continmrk: /to|,| /i
							{
								if ($item[1] eq ' '){$item[1]=',';}
								$item[1];
							}
							varnameend:/[a-z@\\043][a-z0-9._@\\043\\044]{0,7}/i
							{
								#print "\t\tvarname(0e):",$item[1], "\n";
								$item[1]
							}
					body: '(' lefthand '=' righthand ')'
					{
						#print "\tbody:", $item[1], $item[4], "\n";
						# print "body=",&::Dumper($::rdpvltmp),"\n";
						if (lc($item[4]) ne 'sysmis') {undef $item[2]; }
						$item[2];
					}
						lefthand: valset(s)
						{
							$item[1];
						}
							valset: blck sep(?)
							{
								$item[1];
							}
							blck: part1 part2(?) part3(?)
							{
								# 0000009 THRU HI, 0000000
								# 0000000, 0000009 THRU HI
								# LO THRU -999.00, 000.00
								# print "blck(left-hand):[1]=", $item[1],"\t[2]=", $item[2][0],"\t[3]=", $item[3][0], "\n";
								if (defined($item[3][0]) && (lc($item[2][0]) eq 'thru') ) {
									push @{$::rdpvltmp},[$item[1], $item[3][0]];
									$::rdrngtyp=1;
								} else {
									push @{$::rdpvltmp},[$item[1]];
								}
								[$item[1], $item[3][0] ]
							}
							sep: /,| /
								part1: /[0-9-.]{1,}|^lo(west|)/i
								{
									#print "\t\t\tleft-hand value(1):", $item[1], "\n";
									#if (($item[1] =~ m/lowest/i)||($item[1] =~ m/lo/i)) { $item[1] = 'LOWEST';}
									if ($item[1] =~ m/^lo(west|)/i) { $item[1] = 'LOWEST';} else {$item[1] = $item[1] + 0;}
									$item[1]
								}
								part2: /^thru/i
								{
									$item[1]
								}
								part3: /[0-9-.]{1,}|^hi(ghest|)/i
								{
									#print "\t\t\tleft-hand value(3):", $item[1], "\n";
									#if (($item[1] =~ m/highest/i)||($item[1] =~ m/hi/i)) {$item[1] = 'HIGHEST';}
									if ($item[1] =~ m/^hi(ghest|)/i) {
										$item[1] = 'HIGHEST';
									} else {
										$item[1] = $item[1] + 0;
									}
									$item[1]
								}
						righthand:/[0-9-.]{1,}|SYSMIS/i
						{
							#print "\t\t\tright-hand value:", $item[1], "\n";
							$item[1]
						}
					trmntr: '/'
					{
						$item[1]
					}
	); # end of grammar
	my $parser = Parse::RecDescent->new($grammar);
	my $text; 
	{
		if ($svmode eq 'internal') { 
			$text = $self->{_cmmndStrng}->{RcdValue};
		} elsif ($svmode eq 'external') {
			local $/ = undef;
			$text = <CMD>;
		}
	}
	$parser->file($text); 
	if ($svmode eq 'external') {close(CMD);}

if ($DEBUG){
	print $FH "\nrecode (before extension):\n",Dumper($::rdvlst), "\n";
	if ($::rdscndvr) {
		print $FH "$::rdscndvr recode value(s) assigned to multiple variables:\n";
	}
	for (my $i=0;$i<@{$::rdvlst}; $i++) {
		print $FH "row no=",$i,"\tvar1=",$::rdvlst->[$i][0][0][0],"\tvar2=",join('|',@{$::rdvlst->[$i][0][1]}),"\n";
	}
	print $FH "\nnon-empty 2nd var(h)=",Dumper($::rdscndvron),"\n";
}
	# create variable name list
	if (scalar(@{$self->{_varNameA}}) == 0 ){
		my $vnl = [];
		my $varcntr=0;
		foreach my $i (@{$self->{_fileDscr}->{CARDORDER}}){
			for (my $j=0; $j<@{$self->{_location}->{$i}}; $j++){
				push @{$vnl}, $self->{_location}->{$i}->[$j]->[0];
			}
		}
		$self->{_varNameA}=$vnl;
		if ($DEBUG) {print $FH "variable name list is created in the missing value section\n"}
	}

	# expand varsets if exists
	my $rdvrmp={};
	my $rdtbl={};
	for (my $i=0; $i<@{$::rdvlst}; $i++){
		$rdtbl->{$::rdvlst->[$i][0][0][0]}= $::rdvlst->[$i][1];
		if (exists($::rdscndvron->{$i})) {
			my $varslice= $::rdvlst->[$i][0];
			#if ($DEBUG) {print $FH "\nvarslice=",join('|',@{$varslice->[1]}),"\n";}
			&expndvllbl2($varslice, $rdvrmp, $self->{_varNameA});
			#print "var1=",$::rdvlst->[$i][0][0][0],"\tvar keys=",join("|",keys(%{$rdvrmp})),"\n";
		} else {
			#if ($DEBUG) {print $FH "\nvar1=",$::rdvlst->[$i][0][0][0],"\tvalue-label set=",join("|",@{$::rdvlst->[$i][1]}),"\n";}
			$rdvrmp->{$::rdvlst->[$i][0][0][0]}=$::rdvlst->[$i][0][0][0];
		}
	}
if ($DEBUG){
	print $FH "\n\nrecode table:\n",Dumper($rdtbl),"\n\n";
	print $FH "recode hash table:\n",Dumper($rdvrmp),"\n";
	print $FH "range type hash table(recode):\n",Dumper($::rdrngon),"\n\n";
}
	my $rdtypcd= &getMVtypeCode($rdtbl, $::rdrngon,);
	
if ($DEBUG){
	# print $FH "\n\nrecode value table(after):\n",Dumper($rdtbl),"\n\n";
	print $FH "mv code table(recode):\n",Dumper($rdtypcd),"\n\n";
}
	
	# store value-label table and its associated var-mapping table
	if ($::rdvarall) {$self->{_rdVarAll}=1;}
	$self->{_rdTbl}=$rdtbl;
	$self->{_rdVrMpTbl}=$rdvrmp;
	$self->{_rdTpCdTbl}=$rdtypcd;

	# clear global variables
	$::rdvrnmlst=[];$::rdvrnmlst2 =[];$::rdpvltmp =[];$::rdvlst=[];$::rdscndvr =0;$::rdvrcntr=-1;
	$::rdrngtyp=0; $::rdrngon={}; $::rdvarall=0;


}

# ////////////////////////////////////////////////////////////
# Valuelabel-related methods
# ////////////////////////////////////////////////////////////
sub read_Valuelabel{
	my $self= shift;
	# Debugging parameters for Parse::RecDescent
	# $::RD_TRACE = 'true';
	# $::RD_HINT = 'true';
	# $::RD_ERRORS = 'true';

	my $DEBUG=0;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	
	if ($DEBUG) {print $FH "start parsing the value labels\n";}
	my $svmode = $self->{_prcssOptn}->{StorageMode};
	if ($svmode eq 'external') {
		my $dumpflname = $self->{_cmmndStrng}->{ValLabel};
		open (CMD, "$dumpflname") or die "cannot open vallab file";
	} elsif ($svmode eq 'internal') {
		if ($DEBUG) {print $FH "text to be parsed:",$self->{_cmmndStrng}->{ValLabel},"\n";}
	}
	
	$self->{_valVrMpTbl}={};
	$self->{_valLblTbl}={};
	
	
	# global variables

	$::vllblst=[];
	$::vrnmlst=[];
	$::vrnmlst2=[];
	$::pvltmp=[];
	$::vlscndvr=0;
	$::vlscndvron={};
	#- $::vlscndvron=[];
	$::vlvrcntr=-1;
	my $grammar = q(
		# start of grammar
		file: vallbl
			vallbl: /val lab/ sep(?) value(s)
			sep: '/'
			value: varname vllblset(s?)  trmntr(s?) prd(?)
			{
				#print "\tlabeset.length:", $#{$item[2]}, "\n";
				for (my $i=0; $i<=$#{$item[2]};$i++){
					#print "$i-th set:", $item[2][$i][0],"\t" , $item[2][$i][1], "\n";
					push @{$::pvltmp}, [ $item[2][$i][0], $item[2][$i][1] ];
				}
				push @{$::vllblst},[@{$::vrnmlst}, [ @{$::pvltmp} ]];
				$::pvltmp=[];$::vrnmlst=[];
				[ $item[1], $item[2] ]
			}
				varname: varnamestart varnamecontin(s?)
				{
					#print "\tvarname_s(1):", $item[1], "\n";
					#print "\tvarname_e_size(1):", $#{$item[2]}, "\n";
					for (my $i=0; $i<=$#{$item[2]};$i++) {
							#print "\tvarnamee(1):", $item[2][$i][0], "\n";
							#print "\tvarnamee(2):", $item[2][$i][1], "\n";
							#if ((defined($item[2][$i][0])) ) 
						if (($item[2][$i][0] ne ',') && (defined($item[2][$i][0])) ) {
							#print "\tvarnamee(within1):", $item[2][$i][0], "\n";
							push @{$::vrnmlst2}, $item[2][$i][0] ;
						}
						if ($item[2][$i][1]) {
							push @{$::vrnmlst2}, $item[2][$i][1] ;
						}
					}
					$::vlvrcntr++;
					# print "current var set no=",$::vlvrcntr,"\n";
					# print "\t\tvarlist2:", join('|', @{$::vrnmlst2}), "\n";
					if (scalar(@{$::vrnmlst2})) {
						$::vlscndvr++;
						#- push @{$::vlscndvron},$::vlvrcntr ;
						$::vlscndvron->{$::vlvrcntr}="1";
					}
					push @{$::vrnmlst}, [ [$item[1]], [@{$::vrnmlst2}] ];
					$::vrnmlst2 =[];
					#$item[1]
				}
					varnamestart: /[a-z@\\043][a-z0-9._@\\043\\044]{0,7}/i
					{
						#print "\t\tvarname(0):",$item[1], "\n";
						#$::vlscndvr = 0;
						$item[1]
					}
					varnamecontin: continmrk(?) varnameend
					{
						#print "\t\tvarnamecontin:",$item[1][0], "\t", $item[2],"\n";
						[$item[1][0], $item[2]]
					}
					continmrk:/to|,/i
					{
						$item[1]
					}
					varnameend:/[a-z@\\043][a-z0-9._@\\043\\044]{0,7}/i
					{
						$item[1]
					}

			vllblset: vl label
			{
				#print "\tvalue(2):", $item[1],"\n";
				#print "\tlabel(2):", $item[2],"\n";
				$::qmrkvl = 'qoffvl';
				$::clsmrkvl ='';
				[ $item[1], $item[2] ]
			}
				vl: opnqvl(?) val <matchrule:$::qmrkvl>
				{
					#print "\t\tvalue(1):", $item[2],"\n";
					$::labelstrng = 'withoutq';
					$::clsmrklb = '';
					$item[2]
				}
					opnqvl: /^(\\042|\\047|\\050)/
					{
						#if ($item[1] eq '\('  )
						if ($item[1] eq "\050" ){
							$::clsmrkvl = "\051";
							#print 'itempar1:', $item[1] , "\n";
							#print 'itempar1c:', $::clsmrkvl, "\n";
						} else {
							$::clsmrkvl = $item[1];
							#print 'itempar2:', $item[1] , "\n";
							#print 'itempar2c:', $::clsmrkvl , "\n";
						}
						$::qmrkvl = 'qonvl';
						$item[1]
					}
					#val: /[0-9a-z_.+:@\055]{1,125}/i
					val: /[0-9a-z_.+:@\\043\\055]{1,225}/i
					{
						#print "\t\t\tvalue(0):", $item[1], "\n";
						if ($::qmrkvl ne 'qonvl'){
							$::qmrkvl = 'qoffvl';
							$item[1] = $item[1] + 0;
						} else {
							#print "\t\t\tvalue(0)qmrkvl:", $::qmrkvl, "\n";
							# foreach my $key (@{$::Entref}) {$item[1] =~ s/$key/$::EnternalRef->{$key}/g;}
						}
						$item[1]
					}
					qonvl: /["$::clsmrkvl"]/
					{
						$item[1]
					}
					qoffvl://
					{
						$item[1]
					}
				label: opnqlb(?) <matchrule:$::labelstrng> <matchrule:$::qmrklb>
				{
					#print "\t\tlabel(1):", $item[2],"\n";
					$item[2]
				}
					opnqlb: /^[\\042\\047]/
					{
						#print 'opnqlb:', $item[1] , "\n";
						if ($item[1]) {
							# single
							$::qmrklb ='qonlb';
							#$::labelstrng = 'withqs';
							$::labelstrng = 'withq';
							$::clsmrklb = $item[1];
						}
						$item[1]
					}
					#withq: /[^$::clsmrklb]{0,}/
					withq:  /[^"$::clsmrklb"]{0,60}/
					{
						#$item[1] =~ s/\s+$//;<skip:'[\t\r\n\f]'> 
						#print "\t\t\tlabel(0):", $item[1], "\n";
						# foreach my $key (@{$::Entref}) {$item[1] =~ s/$key/$::EnternalRef->{$key}/g;}
						$item[1]
					}
					#withoutq:  /[0-9A-Za-z .<+&\\[\\]!\\$*;^\\-|,%_>?`:#@='"]{1,}/
					withoutq:  /[^\\057\\050]{1,60}/
					{
						$::qmrklb ='qofflb';
						$item[1] =~ s/\s+$//;
						#print "\t\t\tlabel(0):", $item[1], "\n";
						# foreach my $key (@{$::Entref}) {$item[1] =~ s/$key/$::EnternalRef->{$key}/g;}
						$item[1]
					}
					qonlb: /["$::clsmrklb"]/
					{
						$item[1]
					}
					qofflb: //
					{
						$item[1]
					}
				prd:/[.]/
				{
					$item[1]
				}
				trmntr: '/'
				{
					$item[1]
				}
	); # end of grammar
	my $parser = Parse::RecDescent->new($grammar);
	my $text; 
	{
		if ($svmode eq 'internal') { 
			$text = $self->{_cmmndStrng}->{ValLabel};
		} elsif ($svmode eq 'external') {
			local $/ = undef;
			$text = <CMD>;
		}
	}
	$parser->file($text); 
	if ($svmode eq 'external') {close(CMD);}
	
if ($DEBUG){
	print $FH "\nvalue label (before the extension):\n",Dumper($::vllblst), "\n";
	if ($::vlscndvr) {print  $FH "$::vlscndvr val-label set(s) assigned to multiple variables:\n";}
	for (my $i=0;$i<@{$::vllblst}; $i++) {
		print $FH "row no=",$i,"\tvar1=",$::vllblst->[$i][0][0][0],"\tvar2=",$::vllblst->[$i][0][1][0],"\n";
	}
	print $FH "non-empty 2nd var(h)=",Dumper($::vlscndvron),"\n";
}
	# create variable name list
	if (scalar(@{$self->{_varNameA}}) == 0 ){
		my $vnl = [];
		my $varcntr=0;
		foreach my $i (@{$self->{_fileDscr}->{CARDORDER}}){
			for (my $j=0; $j<@{$self->{_location}->{$i}}; $j++){
				push @{$vnl}, $self->{_location}->{$i}->[$j]->[0];
			}
		}
		$self->{_varNameA}=$vnl;
		if ($DEBUG) {print $FH "variable name list is created in the value-label section\n"}
	}
	# expand varsets if exists
	my $vllblvrmp={};
	my $vllbltbl={};
	for (my $i=0; $i<@{$::vllblst}; $i++){
		if (exists($vllbltbl->{$::vllblst->[$i][0][0][0]})){
			if ($DEBUG){
				print $FH "this var(" . $::vllblst->[$i][0][0][0]. ") is already included in the list" . "\n";
				print $FH "current=" . Dumper($vllbltbl->{$::vllblst->[$i][0][0][0]});
				print $FH "new=" . Dumper($::vllblst->[$i][1]);
			}
		}
		$vllbltbl->{$::vllblst->[$i][0][0][0]}=$::vllblst->[$i][1];
		
		
		
		if (exists($::vlscndvron->{$i})) {
			my $varslice= $::vllblst->[$i][0];
			if ($DEBUG) {print $FH "\nvarslice=",join('|',@{$varslice->[1]}),"\n";}
			&expndvllbl2($varslice, $vllblvrmp, $self->{_varNameA});
			#print "var1=",$::vllblst->[$i][0][0][0],"\tvar keys=",join("|",keys(%{$vllblvrmp})),"\n";
		} else {
			#print $FH "\nvar1=",$::vllblst->[$i][0][0][0],"\tvalue-label set=",join("|",@{$::vllblst->[$i][1]}),"\n";
			if ($DEBUG) {print $FH "\nvar1=",$::vllblst->[$i][0][0][0],"\tvalue-label set=\n",Dumper($::vllblst->[$i][1]),"\n";}
			$vllblvrmp->{$::vllblst->[$i][0][0][0]}=$::vllblst->[$i][0][0][0];
		}
	}
if ($DEBUG){
	print $FH "\n\nvalue-lablel table:\n",Dumper($vllbltbl),"\n\n";
	print $FH "value-lablel table:\n",Dumper($vllblvrmp),"\n";
}
	# store value-label table and its associated var-mapping table
	$self->{_valLblTbl} =$vllbltbl;
	$self->{_valVrMpTbl}=$vllblvrmp;
}


# ////////////////////////////////////////////////////////////
# utility methods for parsing an SPS file
# ////////////////////////////////////////////////////////////

sub expandVars{
	my ($varslice, $extdvars) = @_;
	# expanding variable names in data list
	# legend: arguments

	# $varslice->[0]	var_s
	# $varslice->[1]	var_e
	# $varslice->[2]	col_s
	# $varslice->[3]	col_e
	# $varslice->[4]	frmt 
	#
	# $extdvars = {
	# vars =>[],cols =>[],cole =>[],frmt =>[],wdth =>[],dcml =>[],
	# type =>[],vlen =>undef};

	# caller			$extdvars = {}
	# $tmvars			$extdvars->{vars}
	# $tmpcols			$extdvars->{cols}
	# $tmpcole			$extdvars->{cole}
	# $tmpfrmt			$extdvars->{frmt}
	# $tmpwdth			$extdvars->{wdth}
	# $tmpdcml			$extdvars->{dcml}
	# $prcsstyp			$extdvars->{type}
	# $tmpvarslength	$extdvars->{vlen}

	my $FH = *STDOUT;
	my @vrnmset=();
	# variable names/cols
	my $DEBUG = 0;
	if ($DEBUG) {print $FH "\n\nentering the expansion routine(variables):\n\n";}
	my $vrnm_s = $varslice->[0];
	my $vrnm_e = $varslice->[1];

	# expand the name set
	if ($DEBUG)	{
		print $FH "variable names: start/end:", $vrnm_s,"/", $vrnm_e, "\n";
	}
	my @vrs = split('', $vrnm_s);
	my @vre = split('', $vrnm_e);
	if ($DEBUG) {
		print $FH "varname(s):",join(',', @vrs),"\n";
		print $FH "varname(e):",join(',', @vre),"\n";
	}

	if ((ord($vrs[length($vrnm_s)-1])>=65) || (ord($vre[length($vrnm_e)-1])>=65)){
		@vrnmset = ($vrnm_s .. $vrnm_e);
		if ($DEBUG) {print $FH "variable name set:", @vrnmset, "\n";}
	} else {
		my $ssfx = '';
		my $esfx = '';
		my $prfxs='';
		my $prfxe='';
		my @tmps = @vrs;
		my @tmpe = @vre;
		for (my $i = 0; $i<@vre; $i++){
			my $tmpss = pop @tmps;
			 if (ord($tmpss) < 65){
				$ssfx .= $tmpss;
			} else {
				push @tmps, $tmpss;
				$prfxs = join('', @tmps);
				if ($DEBUG) {print $FH "prfx(s):", $prfxs, "\n";}
				last;
			}
		}
		$ssfx = reverse($ssfx);
		if ($DEBUG) {
			print $FH "ssfx:", $ssfx,"\n";
			print $FH "prfx(s):", $prfxs, "\n";
		}
		for (my $i = 0; $i<@vre; $i++){
			my $tmpee = pop @tmpe;
			if (ord($tmpee) < 65){
				$esfx .= $tmpee;
			} else {
				push @tmpe, $tmpee;
				$prfxe = join('', @tmpe);
				if ($DEBUG) {print $FH "prfx(e):", $prfxe, "\n";}
				last;
			}
		}
		$esfx = reverse($esfx);
		if ($DEBUG){
			print $FH "esfx:", $esfx,"\n";
			print $FH "prfx(e):", $prfxe, "\n";
		}
		my $prfx;
		if ($prfxs eq $prfxe) {
			$prfx = $prfxs;
		} else {
			print $FH "prefx: the start-side is not equal to the end-side\n";
			die "errcd(FA11): error in expandVars(disagreement in var name):$!\n";
		}
		my @tmpsffx = "$ssfx".."$esfx";
		if ($DEBUG) {print $FH "tmpsffx:",join("|", @tmpsffx),"\n";}
		@vrnmset = map(($prfx . $_), @tmpsffx);
		# note: approach (@vrnmset = $vrnm_s..$vrnm_e; ) does not work for v1 .. v100 case
	}
	if ($DEBUG) {
		print $FH "variable name set:", join("|",@vrnmset), "\n";
		print $FH "size(variable name set):",scalar(@vrnmset), "\n";
	}
	$extdvars->{vars}=[ @vrnmset ];
	$extdvars->{vlen}= scalar(@vrnmset);
	if ($DEBUG) {print $FH "###### leaving expandVars ######\n\n\n";}
}


sub expandFormats{
	my ($varslice, $extdvars) = @_;
	# expand the format spec
	# legend: arguments

	# $varslice->[0]	var_s
	# $varslice->[1]	var_e
	# $varslice->[2]	col_s
	# $varslice->[3]	col_e
	# $varslice->[4]	frmt 
	#
	# $extdvars = {
	# vars =>[],cols =>[],cole =>[],frmt =>[],wdth =>[],dcml =>[],
	# type =>[],vlen =>undef};

	# caller			$extdvars = {}
	# $tmvars			$extdvars->{vars}
	# $tmpcols			$extdvars->{cols}
	# $tmpcole			$extdvars->{cole}
	# $tmpfrmt			$extdvars->{frmt}
	# $tmpwdth			$extdvars->{wdth}
	# $tmpdcml			$extdvars->{dcml}
	# $prcsstyp			$extdvars->{type}
	# $tmpvarslength	$extdvars->{vlen}
	# 	my $FH   = $self->{_prcssOptn}->{LOGFILE};

	my $FH = *STDOUT;
	my $DEBUG = 0;
	if ($DEBUG) {print $FH "\n\nentering the expansion routine(expndFormats):\n\n";}
	
	# variable names/cols
	my $fortfmt = $varslice->[4];
	my @cls = ();
	my @cle = ();
	my @cols = ();
	my @cole = ();
	my @frmt = ();
	my @wdth = ();
	my @dcml = ();
	my @tabflg=();

	if ($DEBUG){
		print $FH "format received:", $fortfmt, "\n";
	}
	my @tmparray = split(/[, \n\t\r\f]/, $fortfmt);
	if ($DEBUG) {
		print $FH "frmt(b):# of frmts =",scalar(@tmparray),"(",join("|",@tmparray), ")\n\n";
	}
	my @tmparraya = ();
	# remove spaces from the array
	foreach (@tmparray){
		push (@tmparraya, lc($_) ) if ($_) ;
	}
	if ($DEBUG){
		print $FH "frmt(a):# of frmts =",scalar(@tmparraya),"(",join("|",@tmparraya),")\n";
	}

	my @tmparrayb = ();
	my @tmparrayc = ();
	# break each format into 5 parts [mFMTw.d]
	# m		times (# of vars, >=1)
	# FMT	format specifier 
	# 		numeric/sring: F|A|N|E|COMMA|DOT|DOLLAR|PCT|Z|
	# 		date/time:DATE|ADATE|EDATE|JDATE|SDATE|QYR|MOYR|WKYR|
	#				  DATETIME|TIME|DTIME|WKDAY|MONTH
	# w		width
	# d		decimal

	my $ttlvars = 0;	# to check the  number of the corresponding variables
	my $ttllngth = 0;
	my $ttlrw = 0;
	my $tabcounter=0;
	my $j = 0;
	foreach (@tmparraya) {

		m/^(\d*)(f|comma|dot|dollar|pct|datetime|adate|edate|jdate|sdate|date|qyr|moyr|wkyr|dtime|time|wkday|month|x|t|a|n|e|z)(\d*)([.]?)(\d*)/ ;
		 @tmparrayc[0..3] = ($1, $2, $3, $5);
		if ($DEBUG) {print $FH "$j-th resuts:",join("|", ($1, $2, $3, $5)), "\n";}
		#print $FH "$j-th\t",join("|", @tmparrayc), "\n";
		# [x has no post-position($tmparrayc[2]) value]
		# [t has no pre-position ($tmparrayc[0]) value]
		# iX skips i columns; x is acceptable 
		# Ti skips (i-1) columns where i >0, i.e., t is not acceptable
		if ( ($tmparrayc[1] eq 'x') && !$tmparrayc[0] ){
			# case:x 
			# change the format to the standard one
			$tmparrayc[0] = 1;
			$tmparrayc[2] = 1; 
		} elsif ( ($tmparrayc[1] eq 'x') &&  $tmparrayc[0] ) {
			# case: ix
			# change the format to the standard one
			$tmparrayc[2] = $tmparrayc[0]; 
			$tmparrayc[0] = 1;
		} elsif ( ($tmparrayc[1] eq 't') && $tmparrayc[2] ) {
			# case:ti (t is not acceptable)
			$tmparrayc[0] = 1;
			$tmparrayc[2] = $tmparrayc[2] - 1;
			$tabcounter++;
		} elsif (($tmparrayc[1] eq 't') && !$tmparrayc[2]) {
			# case: t [this  is illegal]
			print $FH "\nCoding error in the DATA LIST statement is detected\n";
			print $FH "Reason:T format does not have the column number: this is illegal.\n";
			die "errcd(FA31): error in expandFormats(misspecified T format):$!\n";
		} elsif ( !$tmparrayc[0] ) {
			$tmparrayc[0] = 1;
		}

		if ($DEBUG) {print $FH "$j-th\t",join("|", @tmparrayc), "\n";}

		#push @tmparrayb, [$1, $2, $3, $5];
		#if (!$tabflg[$j]) {
		#	$ttllngth += ($tmparrayc[0]*$tmparrayc[2]) ;
		#}

		if  ( !($tmparrayc[1] eq 'x') && !($tmparrayc[1] eq 't') ) {
			$ttlvars += $tmparrayc[0];
		}

		$ttlrw += $tmparrayc[0];
		for (my $i = 0; $i<$tmparrayc[0]; $i++) {
			push @tmparrayb, [ $tmparrayc[1], $tmparrayc[2], $tmparrayc[3]];
		}
		$j++;
	}
	my $varLength= scalar(@{$extdvars->{vars}});
	if ($DEBUG) {
		print $FH "number of rows in this set:",$ttlrw, "\n";
		print $FH "number of vars covered by this set:",$ttlvars, "\n";
		#print $FH "number of digits (spaces incl.):",$ttllngth, "\n";
		print $FH "number of tabs:",$tabcounter, "\n";
		print $FH "contents of decoded format set:\n",Dumper(@tmparrayb),"\n";
		print $FH "number of accumulated vars so far=",$varLength,"\n";
		if ($varLength > $ttlvars) {
			print $FH "vars accumulated before the current var have no format\n";
		}
	}
	if ($DEBUG) {
		for (my $i = 0; $i<@tmparrayb; $i++) {
			print $FH "$i|";
			for ( $j = 0; $j<= $#{$tmparrayb[$i]}; $j++){
				print $FH "$tmparrayb[$i][$j]|";
			}
			print $FH "\n";
		}
	}
	# case like "xi (F8.2 T2) xj (F8.2)", last T format affects the adjacent F-type formats
	# unless another T format appears
	# however, if the column format follows a F-type format, the previous tab is reset

	my $tmpcrrntpstn=$::crrntpstn;

	for (my $i=0;$i<@tmparrayb;$i++){
		if ($DEBUG) {
			print $FH "\n$i-th iteration: tabState(before)=",$::tabState,"\n";
			print $FH "the current lastTABpstn(before)=", $::lastTABpstn,"\n";
		}
		if ($tmparrayb[$i][0] eq 't') {
			# reset the previous tab setting
			$::tabState=1;
			$::lastTABpstn=$tmparrayb[$i][1];
			if ($DEBUG) {
				print $FH "$i-th iteration: tabState becomes on\n";
				print $FH "the current lastTABpstn", $::lastTABpstn,"\n";
			}
		}
		if (!$i) { 
			# 1st row
			if ($::tabState) {
				# tab is on
				if ($tmparrayb[$i][0] ne 't') {
					# tab is already on
					$cls[0] = 1+$::lastTABpstn;
					$cle[0] = $tmparrayb[$i][1]+$::lastTABpstn;
				} else {
					# tab becomes on during this iteration
					$cls[0] = 1;
					$cle[0] = $::lastTABpstn;
				}
			} else {
				# tab is off
				$cls[0] = 1 + $::crrntpstn;
				$cle[0] = $tmparrayb[$i][1] + $::crrntpstn;
				$tmpcrrntpstn=$cle[0];
				if ($DEBUG) {print $FH "$i-th: current position=",$tmpcrrntpstn,"\n";}
			}

		} else {
			# 2nd row and after
			if ($::tabState) {
				# tab is on
				if ($tmparrayb[$i][0] ne 't') {
					# tab is already on
					$cls[$i] = 1 + $cle[$i - 1];
					$cle[$i] =     $cle[$i - 1] + $tmparrayb[$i][1];
				} else {
					# tab becomes on during this iteration
					$cls[$i] = 1 ;
					$cle[$i] =  $::lastTABpstn;;
				}
			} else {
				# tas is off
				$cls[$i] = 1 + $cle[$i - 1] ;
				$cle[$i] =     $cle[$i - 1] + $tmparrayb[$i][1] ;
				$tmpcrrntpstn=$cle[0];
				if ($DEBUG) {print $FH "$i-th:current position=",$tmpcrrntpstn,"\n";}
			}
		}
		if ($DEBUG) {
			print $FH "\nfinal:\n";
			print $FH "$i-th|",join("|", $cls[$i],$cle[$i],$tmparrayb[$i][0],$tmparrayb[$i][1],$tmparrayb[$i][2]),"\n";
		}

		if ( ($tmparrayb[$i][0] ne 'x') && ($tmparrayb[$i][0] ne 't') ) {
			push @cols, $cls[$i];
			push @cole, $cle[$i];
			push @frmt, $tmparrayb[$i][0];
			push @wdth, $tmparrayb[$i][1];
			push @dcml, $tmparrayb[$i][2];
		}
		# update $lastposition 
		if ($i==$#tmparrayb) {
			if ($DEBUG) {print $FH "current position=",$tmpcrrntpstn,"\n";} 
			if ($DEBUG) {print $FH "old current position=",$::crrntpstn,"\n";} 
			if ($::tabState) {
				$::lastTABpstn=$cle[$i];
			} else {
				$::crrntpstn=$tmpcrrntpstn;
			}

		}
	}
	$extdvars->{vlen} =  $ttlvars;$extdvars->{cols} =  [@cols];
	$extdvars->{cole} =  [@cole];$extdvars->{frmt} =  [@frmt];
	$extdvars->{wdth} =  [@wdth];$extdvars->{dcml} =  [@dcml];
	if ($DEBUG) {print $FH "###### leaving expandFormats ######\n\n\n";}
}



sub expandColumns{
	my ($varslice, $extdvars) = @_;
	# legend: arguments

	# $varslice->[0]	var_s
	# $varslice->[1]	var_e
	# $varslice->[2]	col_s
	# $varslice->[3]	col_e
	# $varslice->[4]	frmt 
	#
	# $extdvars = {
	# vars =>[],cols =>[],cole =>[],frmt =>[],wdth =>[],dcml =>[],
	# type =>[],vlen =>undef};

	# caller			$extdvars = {}
	# $tmvars			$extdvars->{vars}
	# $tmpcols			$extdvars->{cols}
	# $tmpcole			$extdvars->{cole}
	# $tmpfrmt			$extdvars->{frmt}
	# $tmpwdth			$extdvars->{wdth}
	# $tmpdcml			$extdvars->{dcml}
	# $prcsstyp			$extdvars->{type}
	# $tmpvarslength	$extdvars->{vlen}

	my $FH = *STDOUT;
	# variable names/cols
	my $DEBUG = 0;
	if ($DEBUG) {print $FH "\n\nentering the expansion routine(expndColumns):\n\n";}
	my @cols = ();
	my @cole = ();

	# expand the column number set
	my $colsets = $varslice->[2];
	my $colsete = $varslice->[3];

	my $lenvrnmst;
	my $lenvrnmstx;
	if ($DEBUG) {print "var length from the caller\n";}
	$lenvrnmst = $extdvars->{vlen};
	my $dfcl0 = $colsete - $colsets + 1;
	# checking the length
	if ($lenvrnmst > $dfcl0) {
		$lenvrnmstx=$lenvrnmst;
		$lenvrnmst=1;
		if ($DEBUG) {
			print $FH "number of available columns is less than the number of vars\n";
			print $FH "columns are allocated to the last var only\n";
		}
	}

	my $dfcl1 = $dfcl0%($lenvrnmst);
	if ($DEBUG) {
		print $FH "col start|end:(",$colsets,"|",$colsete,")\n";
		print $FH "allocated columns=", $dfcl0,"\n";
		print $FH "number of vars=", $lenvrnmst,"\n";
		print $FH "remainder:", $dfcl1,"\n";
	}
	if ($dfcl1) {
		print $FH "The remainder($dfcl1) is not zero:\nA coding error(the number of columns does not divide equally) is suspected[$dfcl0 is divided by $lenvrnmst]\n"; 
			die "errcd(FA21): error in expandColumns(disagreement between varname and columns):$!\n";
	}

	my $dvdnt = $dfcl0/$lenvrnmst;
	if ($DEBUG) {print $FH "divident:", $dvdnt, "\n";}
	if ($dvdnt == 1){
		for (my $i = 0; $i<$lenvrnmst;$i++) {
			$cols[$i] = $colsets + $dvdnt*$i;
		} 
		if ($DEBUG) {
			print $FH "length of cols:", my $lencol = @cols, "\n";
			print $FH "cols:", join("|", @cols), "\n";
		}
		$extdvars->{cols} = [@cols];
		$extdvars->{cole} = [@cols];
		if ($cols[$#cols] != $colsete) {
			print $FH "error\nthe last element(",$cols[$#cols],") does not match the specified last col position(",$colsete,")\n";
			die "errcd(FA23): error in expandColumns(misspecified last column position):$!\n";
		}
	} elsif ($dvdnt > 1) {
		for (my $i = 0; $i<$lenvrnmst;$i++) {
			$cols[$i] = $colsets + $dvdnt*$i;
			$cole[$i] = $colsets + $dvdnt*($i+1) -1;
		} 
		if ($DEBUG) {
			print $FH "length of cols:", scalar(@cols), "\n";
			print $FH "length of cole:", scalar(@cole), "\n";
			print $FH "cols:", join("|", @cols), "\n";
			print $FH "cole:", join("|", @cole), "\n";
		}

		$extdvars->{cols}= [@cols];
		$extdvars->{cole}= [@cole];

		if ($cole[$#cole] != $colsete){
			print $FH "error\nthe last element(",$cole[$#cole],") does not match the specified last col position",$colsete,"\n";
			die "errcd(FA25): error in expandColumns(misspecified last column position):$!\n";
		}
	}
	$extdvars->{vlen} = $lenvrnmst;
	if ($DEBUG) {print $FH "###### leaving expandColumns ######\n\n\n";}

}


sub expndvllbl2{
	my $varslice =shift;
	my $extvarset=shift;
	my $vnl =shift;
	my $DEBUG=0;
	my($vrnm_s, $vrnm_e, @vrnmsets,@tempvars, @tmp);
		for (my $j=0;$j<@{$varslice->[1]};$j++){
			if ($DEBUG) {print "var sec(end)[$j-th element]:", $varslice->[1][$j],"\n";}
			# if j-th element is 'to', determine the start/end vars
			# according to the situation (0-th elment or not)

			if (lc($varslice->[1][$j]) eq 'to' ){

				if (!$j){
					# 0-th element:     use start var in var sec(start)
					$vrnm_s = $varslice->[0][0];
					if ($DEBUG) {print "\tto case(start var:[0]):", $vrnm_s,"\n";}
				} else {
					# not 0-th element: use previous one in var sec(end) 
					$vrnm_s = $varslice->[1][$j-1];
					if ($DEBUG) {print "\tto case(start var:[j-1]):", $vrnm_s,"\n";}
				}

				# set the end var[use next one in var sec(end)]
				$vrnm_e = $varslice->[1][$j+1];
				if ($DEBUG) {print "\tto case(end var:[j+1]):", $vrnm_e,"\n";}

				# expand the name
				my $startposition = 0;
				my $endposition  = 0;
				# search the postions of the start var/end var in the full var list
				for (my $i=0;$i<@{$vnl}; $i++){
					if ($vnl->[$i] eq $vrnm_s) {$startposition=$i}
					if ($vnl->[$i] eq $vrnm_e) {$endposition=$i; last}
				}
				if ($DEBUG) { print "start pos=",$startposition,"\tend pos=",$endposition,"\n";}
				@vrnmsets = @{$vnl}[$startposition..$endposition];
				if ($DEBUG) {print "\t$j-th varname list(end):", join("|",@vrnmsets), "\n";}

				push @tempvars, (@vrnmsets);

				# clear the temp var name arrary
				@vrnmsets=();
				# shift the current index position to next
				$j++;
			} else {
				# because j is not 'to', the current (j-th) element is a single var
				if ($DEBUG) {print "\tvar sec(end)[$j-th element] is a single var\n";}
				if (!$j) {push @tempvars, $vrnm_s = $varslice->[0][0];};
				push @tempvars, $varslice->[1][$j];
			}
		} # end of var sec(end) processing


		# print "extended varset:", join("|",@tempvars),"\n";
		#- push @newvarset, [@tempvars];

		for (my $i=0;$i<@tempvars;$i++) {
			if ($DEBUG) {print "i=",$i,"\ttempvars=",$tempvars[$i],"\n";}
			$extvarset->{$tempvars[$i]} = $varslice->[0][0];
		}
		#$DEBUG=1;
		if ($DEBUG) {print "extended vars: hash:\n", Dumper($extvarset), "\n\n";}

		@tempvars=();
}


sub getMVtypeCode{
	my $mvtbl = shift;
	my $mvrngon = shift;
	my $mvtypcd={};
	my $DEBUG=0;
	my $FH;
	$FH = *STDOUT;
	
	# SAV missing value type coding
	#  0	No missing value
	#  1	One point type
	#  2	Two point types
	#  3	Three point types
	# -2	One range type
	# -3	One range type and one point type
	
	if ($DEBUG) {print $FH "entering getMVtypeCode\n";}
	my $maxpnts=3;
	my $maxrng=2;
	if ($DEBUG) {print $FH "Missing Value Code List=\n",Dumper($mvtbl),"\n";}
	while ( my ($ky, $vl) = each %{$mvtbl} ) {
		my $vlsize = scalar(@{$vl});
		if ($DEBUG) { print $FH "key=",$ky,"\tvalue=",$vlsize,"\n";}
		if (exists($mvrngon->{$ky})) {
			# range type
			if ($vlsize == ($maxrng-1)) {
				$mvtypcd->{$ky}=-2;
			} elsif ($vlsize == $maxrng) {
				$mvtypcd->{$ky}=-3;
			} elsif ($vlsize > $maxrng){
				if ($DEBUG){
					print $FH "no of missing values must be <= $maxrng if a range type is included\n";
					print $FH "3rd element and after are removed(var=$ky)\n";
					print $FH "before($ky)=", Dumper($vl), "\n";
				}
				$#{$vl}=$maxrng-1;
				if ($DEBUG) {print $FH "after($ky)=", Dumper($vl), "\n";}
				$mvtbl->{$ky}=$vl;
			}
		} else {
			# point type
			if ($vlsize <= $maxpnts) {
				$mvtypcd->{$ky}=$vlsize;
				# print "others($ky)=", Dumper($vl), "\n";
			} elsif ($vlsize > $maxpnts ) {
				if ($DEBUG){
					print $FH "no of missing values must be <= $maxpnts\n";
					print $FH "4th element and after are removed(var=$ky)\n";
					print $FH "before($ky)=", Dumper($vl), "\n";
				}
				$#{$vl}= $maxpnts-1;
				if ($DEBUG) {print $FH "after($ky)=", Dumper($vl), "\n";}
				$mvtbl->{$ky}=$vl;
			} 
		}
	}
	if ($DEBUG) {print $FH "Missing Value Code List=\n",Dumper($mvtypcd),"\n";}
	return $mvtypcd;
}

	

1;


__END__


=head1 NAME

VDC::DSB::Ingest::SPS - SPSS data definition file parser

=head1 DEPENDENCIES

=head2 Nonstandard Modules

    VDC::DSB::Ingest::StatData

=head1 DESCRIPTION

VDC::DSB::Ingest::SPS parses an SPSS syntax (data defintion) file and returns parsing results (metadata) as a reference to a hash.  This module is called by VDC::DSB::Ingest::StatDataFileReaderFactory and is used with VDC::DSB::Ingest::StatData.


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


