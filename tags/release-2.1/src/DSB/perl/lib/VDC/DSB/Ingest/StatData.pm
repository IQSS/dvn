package VDC::DSB::Ingest::StatData;

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
use HTML::Entities qw(encode_entities_numeric);
use IO::File;
use File::Copy;
use Data::Dumper;
#use lib '.';
# package variables

my $TMPDIR=File::Spec->tmpdir();

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

my $MNTH2N = {'JAN'=>1, 'FEB'=>2,'MAR'=>3, 'APR'=>4, 'MAY'=>5, 'JUN'=>6,
			  'JUL'=>7, 'AUG'=>8,'SEP'=>9, 'OCT'=>10, 'NOV'=>11, 'DEC'=>12};
my $QN2MNTH = {'1'=>'01', '2'=>'04', '3'=>'07','4'=>'10'};



# note: 
# MONTH and WKDAY are treated as string-type
my $CNVRSNTBL = {
	'#'=>'hex23',
	'$'=>'hex24',
	'@'=>'hex40',
	'_'=>'hex5F',
	'?'=>'hex3F',
};


# R-safe variable Name Mapping table
my $Rkywrd2safeVarName= {
	'NULL'=>'null',
	'NA'=>'na',
	'TRUE'=>'true',
	'FALSE'=>'false',
	'Inf'=>'inf',
	'NaN'=>'naN',
	'function'=>'Function',
	'while'=>'While',
	'repeat'=>'Repeat',
	'for'=>'For',
	'if'=>'If',
	'in'=>'In',
	'else'=>'Else',
	'next'=>'Next',
	'break'=>'Break',
};

my $tm = localtime;
#print "loca time=", $tm, "\n";
my @CY = split(/ /, $tm);
my $CURRENT_YEAR= $CY[$#CY];
#print "current year=",$CURRENT_YEAR,"\n";
my $CURRENT_YEAR_S= 0+substr($CURRENT_YEAR, 2,2);
my $CURRENT_C= 0+substr($CURRENT_YEAR, 0,2);
#print "current year short=",$CURRENT_YEAR_S,"\n";
#print "current century=",$CURRENT_C,"\n";



sub new {
	my ($class, %args) =@_;
	my $self ={};
	
	# file-wise attributes
	$self->{_fileDscr}={
		fileID   =>undef,
		varQnty  =>undef,
		caseQnty =>undef,
		charset  =>"ISO-8859-1",
		fileType =>undef,
		fileFrmt =>undef,
		fileUNF  =>undef,
		fileDate =>undef,
		fileTime =>undef,
	};
	
	# common var-wise attributes
	$self->{_varNameA}=[];
	$self->{_varNameH}={};
	$self->{_varType}={};
	$self->{_varFormat}={};
	$self->{_formatName}={};
	$self->{_varLabel}={};

	$self->{_charVarTbl}={};
	$self->{_dcmlVarTbl}={};
	
	$self->{_valVrMpTbl}={};
	$self->{_valLblTbl}={};
	
	$self->{_mssvlTbl}={};
	$self->{_mvVrMpTbl}={};
	$self->{_mvTpCdTbl}={};
	
	$self->{_noCharVar}=undef;
	$self->{_noDcmlVar}=undef;
	
	$self->{_UNF}=[];
	$self->{_sumStat}={};
	# processing states
	$self->{_prcssOptn} = {
		NAstring=>'NA',
	};
	
	bless $self, ref($class)||$class;
	$self->_init(%args);
	#print "-init:\n",Dumper(%args);
	return $self;
}

# /////////////////////////////////////////////////////////////////////////////
sub setFileParams {
	my $self=shift;
	my $optn ={@_};
	while ( my ($ky, $vl) = each %$optn ) {
		#my $key = '_' . $ky;
		$self->{_prcssOptn}->{$ky}= $vl;
	}
}

# /////////////////////////////////////////////////////////////////////////////

sub setMVtypeCode{
	my $self = shift;
	my $optn = shift;
	my $mvtbl = $self->{_mssvlTbl};
	my $mvrngon;
	my $mvtypcd={};
	my $DEBUG=0;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	
	# SAV missing value type coding
	#  0	No missing value
	#  1	One point type
	#  2	Two point types
	#  3	Three point types
	# -2	One range type
	# -3	One range type and one point type
	
	print $FH "entering getMVtypeCode\n" if $DEBUG;
	my $maxpnts=3;
	my $maxrng=2;
	print $FH "Missing Value Code List=\n",Dumper($mvtbl),"\n" if $DEBUG;
	my $elsize=[];
	while ( my ($ky, $vl) = each %{$mvtbl} ) {
		$mvrngon=0;
		my $vlsize = scalar(@{$vl});
		print $FH "key=",$ky,"\tsize=",$vlsize,"\n" if $DEBUG;
		if ($vlsize < 3){
			if ($vlsize == 2){
				if ( (scalar(@{$vl->[0]}) == 2) || (scalar(@{$vl->[1]}) == 2) ){
					$mvrngon=1;
				}
			} elsif ($vlsize == 1){
				if (scalar(@{$vl->[0]}) == 2){
					$mvrngon=1;
				}
			}
		}
		
		print $FH "range type?(1:yes, 0:no)=", $mvrngon,"\n" if $DEBUG;
		if ($mvrngon) {
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
				print $FH "after($ky)=", Dumper($vl), "\n" if $DEBUG;
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
				print $FH "after($ky)=", Dumper($vl), "\n" if $DEBUG;
				$mvtbl->{$ky}=$vl;
			} 
		}
	}
	print $FH "Missing Value Code List=\n",Dumper($mvtypcd),"\n" if $DEBUG;
	$self->{_mvTpCdTbl}=$mvtypcd;
}

sub setmvVrMpTbl {
	my $self=shift;
	my $mvtbl = $self->{_mssvlTbl};
	while ( my ($ky, $vl) = each %{$mvtbl} ) {
		$self->{_mvVrMpTbl}->{$ky}= $ky;
	}
}

sub setOperation {
	my $a =shift;
	my $b =shift;
	my $union =[];
	my $isect =[];
	my $diff = [];
	my %count = ();
	my $DEBUG;

	foreach my $e (@$a, @$b) { $count{$e}++ }
	#print "count=\n",Dumper(%count),"\n";
	if ($DEBUG) {print "count:\n";
		while ( my ($key, $value) = each %count){
			print "key=",$key,"\tvalue=",$value,"\n";
		}
	}
	foreach my $e (keys %count) {
		push(@$union, $e);
		if ($count{$e} == 2) {
			push @$isect, $e;
		} else {
			push @$diff, $e;
		}
	}
		
	my $rtrn=[$union,$isect,$diff];
	if ($DEBUG){
		print "\nrelationship between 1st and 2nd arrays\n";
		print "union=",join("|",@{$rtrn->[0]}),"\n";
		print "isect=",join("|",@{$rtrn->[1]}),"\n";
		print "diff =",join("|",@{$rtrn->[2]}),"\n\n";
	}
	return $rtrn;
}

sub checkSets{
	my $a =shift;
	my $b =shift;
	my $lena=scalar(@$a);
	my $lenb=scalar(@$b);
	my $DEBUG=0;
	my $obj =&setOperation($a, $b);
	# obj spec: 0: union, 1: intersect, 2: diff
	my $leni=scalar(@{$obj->[1]});
	my $lend=scalar(@{$obj->[2]});
	my $type='';
	my ($oa, $ob);
	my $incld=[undef, undef];
	if ($leni){
		if ( $lend == 0 ){
			# identical case
			$type=5;
		} elsif ($lena == $leni){
			# check case 4: b inclues a
			if ($DEBUG) {print "case 4:\n",Dumper($obj->[2]),"\n";}
			$incld->[1]=$obj->[2];
			$type=4;
		} elsif ($lenb == $leni){
			# check case 3: a includes b
			if ($DEBUG) {print "case 3:\n",Dumper($obj->[2]),"\n";}
			$incld->[0]=$obj->[2];
			$type=3;
		} else {
			$oa = &setOperation($a, $obj->[1]);
			$incld->[0]=$oa->[2];
			$ob = &setOperation($b, $obj->[1]);
			$incld->[1]=$ob->[2];
			$type=2;
		}
	} else {
		$type=1;
	}
	push @$obj, $type;
	push @$obj, $incld;
	if ($DEBUG) {
		print "incld:\n",Dumper($incld);
		print "obj:\n", Dumper($obj);
	}
	return $obj;
}

sub getfreeMemSize {
	my $self=shift;
	my $freeMemsize=-1;
	my $DEBUG=0;
	my $FH= $self->{_prcssOptn}->{LOGFILE};

	my ($key, $value, %memstat);
		#open(PH, "grep -e 'MemT\\|MemF\\|Active' < /proc/meminfo | tr -d ' kB' 2>&1 |");
		open(PH, "cat /proc/meminfo | tr -d ' kB' 2>&1 |");
		while (<PH>) {
			chomp;
			if ($_){
				print $FH $_, "\n" if $DEBUG;
				($key, $value)=split(':', $_);
				print $FH $key, "\t",$value,"\n"  if $DEBUG;
				$memstat{$key}=$value;
			}
		}
		close (PH);
		if (exists($memstat{'MemTotal'}) && exists($memstat{'Active'}) ){
                        $freeMemsize= $memstat{'MemTotal'} - $memstat{'Active'} ;
			print $FH "getfreeMemSize:freeMemsize=",$freeMemsize,"\n" if $DEBUG;
		}
	return $freeMemsize;
}

sub getVarRangeSet {
	my $self=shift;
	my $optn ={@_};
	my $DEBUG=1;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	
	my $varQnty= $self->{_fileDscr}->{varQnty};
	my $caseQnty = $self->{_fileDscr}->{caseQnty};
	my $charVarQnty = $self->{_noCharVar};
	# check inactive RAM size
	if ($DEBUG){
		print $FH "# of vars=",$varQnty,"\n";
		print $FH "# of char vars=",$charVarQnty,"\n";
		print $FH "# of cases=",$caseQnty,"\n";
	}
	#my @coefvars=(0.042, 0.290);
	my @coefvars=(0.015, 0.100);

	my $prblmSize= ($coefvars[0]*($varQnty - $charVarQnty) + $coefvars[1]*$charVarQnty)*$caseQnty;
	
	print $FH "problem size=",$prblmSize,"\n" if $DEBUG;
	my $freeMemSize = $self->getfreeMemSize();
	print $FH "inactive mem size=",$freeMemSize,"\n" if $DEBUG;
	my $wght=1.00;
	print $FH "weight=",$wght,"\n" if $DEBUG;
	$freeMemSize = $wght*$freeMemSize;
	print $FH "inactive mem size(r)=",$freeMemSize,"\n" if $DEBUG;

	my ($divisor,$increment);
	my $varRangeSet = [];
	if ($freeMemSize <= $prblmSize) {
		print $FH "Inactive memory would be insuffcient to finish this R job\n" if $DEBUG;
		if ($freeMemSize > 0) {
			$divisor = int($prblmSize/$freeMemSize) + 1;
		} else {
			print $FH "freeMemSize is non-positive\n" if $DEBUG;
			$divisor = 1;
		}
		print $FH "divisor=",$divisor,"\n" if $DEBUG;
		# a sub-problem must have at least one var,
		# i.e., the divisor must be <= varQnty
		if ($divisor > $varQnty) {$divisor = $varQnty;}
		$increment = int($varQnty/$divisor);
		if ($DEBUG){
			print $FH "problem size=",$prblmSize,"\n";
			print $FH "available free memory=",$freeMemSize,"\n";
			print $FH "divisor=",$divisor,"\n";
			print $FH "increment=",$increment,"\n";
		}
		for (my $i=0;$i<$divisor;$i++){
			if ($i == ($divisor-1)){
				push @$varRangeSet, [$i*$increment,($varQnty-1) ];
			} else {
				push @$varRangeSet, [$i*$increment,($i+1)*$increment-1 ];
			}
			print $FH "range set($i)=",join("|",@{$varRangeSet->[$i]}),"\n" if $DEBUG;
		}
	} else {
		print $FH "Inactive memory would be suffcient to finish this R job\n" if $DEBUG;
		push @$varRangeSet, [0,($varQnty-1)]
	}
	#if ($DEBUG) {$FH->close();}
	return $varRangeSet;
}

sub addSumstat{
	my $self =shift;
	my $DEBUG=0;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	my $filenmbr = $self->getFileNo();
	# substr($self->{_fileDscr}->{fileID}, 4);
	
	# sumStat/catStat-storage file
	my $smsttfile = "$TMPDIR/stt.$$.$filenmbr.tab";
	$self->{_prcssOptn}->{SMSTTFILE}=$smsttfile;
	
	# get a sutiable var-range set
	my $varRangeSet = $self->getVarRangeSet();
	print $FH "varRangeSet=\n", Dumper($varRangeSet), if $DEBUG;
	
	my $divisor = scalar(@$varRangeSet);
	my $Rdata   = $self->{_prcssOptn}->{DLMTDDATA};
	my $novar   = $self->{_fileDscr}->{varQnty};
	my $varcode = $self->getVarTypeSet();
	
	my $varname = $self->getRsafeVarNameSet();
	my $raw2RsafeVarName = $self->{raw2RsafeVarName};
	my $RsafeVarName2raw = $self->{RsafeVarName2raw};
	print $FH "varName changed=\n", Dumper($raw2RsafeVarName), if $DEBUG;

	# note: although a tab data file will be divided,
	# an output tab file that stores descriptive statistics is one file
	# step 1: save an R code file
		
	#my $rcode = "$TMPDIR/rcd.$$.R";
	my $rcode = "$TMPDIR/rcd.$$.$filenmbr.R";
	my $base = $Rdata;
	$base =~s/\.([^\.]*)$//;
	
	my $rh = new IO::File("> $rcode");
	my ($ii, $RtmpData);
	my $foptn='-f';
	
	# calculate nrows for performance
	my $nrows = `wc -l < "$Rdata"` + 0;
	if ($nrows<1) {
		$nrows=-1;
	}

	my $RtmpDataBase= "$TMPDIR/t.$$.$filenmbr";
	#my $RtmpDataBase= "$basedir/t.$$.$filenmbr";
	
	print $rh "source(\"/usr/local/VDC/R/library/vdc_startup.R\")\n\n";
	for (my $i=0;$i<@$varRangeSet; $i++){
		$ii = $i+1;
		$RtmpData= $RtmpDataBase . ".$ii.tab";
		
		
		print $rh "vartyp <-c(\n", join(",\n", @$varcode[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]]), ")\n";
		
		
		#///////////////////////////////////
		
			my @varNameSet=@$varname[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]];
			#print "Dumper:\n", Dumper(@varNameSet);
			#print "Dumper:\n", Dumper(@$varname[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]]);
			print $rh "varFmt<-list(","\n";
			my $cntr =0;
			for (my $v=0;$v<@varNameSet; $v++){


				my $vn=$varNameSet[$v];
				my $vf=$self->{_varFormat}->{$vn};
				#print $v,"\t",$vf,"\n"; 

				my $fmtChar="";
				if ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'date') {
					# date conversion
					$fmtChar='D';
				} elsif ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'time'){
					# time conversion
					if ($vf eq 'DTIME'){
						$fmtChar='JT';
					} elsif ($vf eq 'DATETIME'){
						$fmtChar='DT';
					} else {
						$fmtChar='T';
					}
				} elsif ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'other') {
					# currently not applicable
				}
				if ($fmtChar){
					$cntr++;
					my $terminator="";
					#if ($v != (scalar(@varNameSet)-1)){
					if ($cntr > 1){
						$terminator=",\n";

					}
					print $rh $terminator . "'",$vn,"'", "='", $fmtChar, "'";
				}
			}

			print $rh ');',"\n\n";

		
		#///////////////////////////////////
		
		print $rh "x<-read.table141vdc(file=\"$RtmpData\", sep=\"\\t\", col.names=c(\n\"", join("\",\n\"", @$varname[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]]), "\"),colClassesx=vartyp,varFormat=varFmt,nrows=$nrows)\n";
		print $rh "str(x)\n\n" if $DEBUG;
		
		if ($i){
			print $rh "sumStatTabAll.DDI(dtfrm=x,file=\"$smsttfile\", flid=$filenmbr, jobNo=$i)\n\n";
		} else {
			print $rh "sumStatTabAll.DDI(dtfrm=x,file=\"$smsttfile\", flid=$filenmbr, jobNo=$i, novars=$novar)\n\n";
		}
		print $rh "rm(x) \n\n";  # reduces memory use
		$foptn .= ($varRangeSet->[$i][0]+1) . '-' . ($varRangeSet->[$i][1]+1) . ',';
	}
	
	$rh->close();
	print $FH "foptn=",$foptn,"\n" if $DEBUG;
	# step 2: divid a tab file
	my $runtab=1;
	my $moptn = '-m ' . $RtmpDataBase;
	if ($divisor > 1){
		open(PH, "/usr/local/VDC/bin/rcut  $foptn $moptn < '$Rdata' 2>&1 |");
		while (<PH>) {
			print $FH $_ if $DEBUG;
			if (/No such file or directory/i){
				print STDERR "error in rcut: an input(tab) file is missing=",$1,"\n";
				$runtab=0;
			}
		}
		close(PH);
	} elsif ($divisor == 1){
		copy($Rdata,$RtmpData);
	}
	# step 3: execute the Rcode
	my $runr=1;
	open(PH, "R --no-save --no-restore  --min-vsize=750M --min-nsize=25M < $rcode  2>&1 |");
	# TODO, make min sizes based on data size
	while (<PH>) {
		print $FH $_ if $DEBUG;
		if (/(Error\s+in\s+file)|(Execution\s+halted)/i){
			print $FH "error in R run=",$1,"\t",$2,"\n" if $DEBUG;
			$runr=0;
		}
	}
	close(PH);
	unlink($rcode);
	#if ($divisor == 1){
	#	rename($RtmpData, $Rdata);
	#}
	
	my $nofldl =  unlink(<$RtmpDataBase.*.tab>);
	print $FH "\n",$nofldl," sub-tab files are deleted\n" if $DEBUG;
	
	# read back sumstat data
	my $sh = new IO::File;

	$sh->open("< $smsttfile");
	my $sumStat={};
	my $novars;
	my $fileid;
	#my $vllbtbl= $self->{_valLblTbl};
	my $vllbmap = $self->{_valVrMpTbl};
	print $FH "vllbmap=\n",Dumper($vllbmap),"\n" if $DEBUG;
	unless (exists($self->{_UNF})){
		$self->{_UNF}=[];
	}
	unless (exists($self->{_fileDscr}->{fileUNF})){
		$self->{_fileDscr}->{fileUNF}="";
	}
	my $unf     = $self->{_UNF};
	my $fileUNF = $self->{_fileDscr}->{fileUNF};
	
	my $mvtbl   = $self->{_mssvlTbl};
	my $mvvrmp  = $self->{_mvVrMpTbl};
	while ( my ($ky, $vl) = each %{$vllbmap} ) {
		if ($ky ne $vl){
			$self->{_valLblTbl}->{$ky}=[];
			$vllbmap->{$ky}=$ky;
			# clone the value-label set
			foreach my $vlset (@{$self->{_valLblTbl}->{$vl}}){
				push @{$self->{_valLblTbl}->{$ky}},[ @{$vlset} ];
			}
		}
	}
	print $FH "vltbl(pre assignment)=\n",Dumper($self->{_valLblTbl}),"\n" if $DEBUG;
	
	# file-wise UNF
	my $tmpUNF ="$TMPDIR/tmpUNF.$$.tmp";
	my $unfh = new IO::File;
	$unfh->open("> $tmpUNF");
	my $UNFcounter =0;
	
	
	
	
	
	
	my $catStat={};
	my $j=-1;
	my $vltbl;
	while(<$sh>){
		chomp;
		my @line = split (/\t/, $_);
		if ($. == 1){
			$self->{_fileDscr}->{caseQnty}=$line[0];
			$novars=$line[1];
			$fileid=$line[2];
		} else {
			print $FH "unf=",$line[$#line],"\n" if $DEBUG;
			$unf->[$j]=$line[$#line];
			
			# dump file-wise UNF
			print $unfh $line[$#line],"\n";
			print $FH $line[$#line],"\n" if $DEBUG;
			$UNFcounter++;
			if (exists($RsafeVarName2raw->{$line[0]})){
				print $FH "variable name for R ",$line[0]," is replaced with the orignal name:",$RsafeVarName2raw->{$line[0]},"\n";
				$line[0]= $RsafeVarName2raw->{$line[0]};
			}
			
			
			if ($line[1]==2){
				# continuous vars: # of elements to follow is fixed
				# although $line[2] tells how many items follow
				$sumStat->{$line[0]}=[ @line[3.. ($#line-1)]];
				
			} elsif ($line[1] < 2) {
				# set catStat values
				print $FH "\n+++++++++++++++++++++++++++++++ working on $line[0] +++++++++++++++++++++++++++++++\n" if $DEBUG;
				
				
				$sumStat->{$line[0]}=[ @line[($#line -3) .. ($#line-1)]];
				
				# call each value-table
				if ( (!exists($vllbmap->{$line[0]})) || (!exists($self->{_valLblTbl}->{$vllbmap->{$line[0]}})) || (!$self->{_valLblTbl}->{$vllbmap->{$line[0]}}) ){
					print $FH "pass the vltbl-missing case\n" if $DEBUG;
					$vllbmap->{$line[0]}=$line[0];
					$self->{_valLblTbl}->{$vllbmap->{$line[0]}}=[];
				}
				if (exists($vllbmap->{$line[0]}) && $vllbmap->{$line[0]} ) {
					$vltbl = $self->{_valLblTbl}->{$vllbmap->{$line[0]}};
					print $FH "key_var=",$vllbmap->{$line[0]},"\n" if $DEBUG;
					print $FH "vltbl(initial assignment)=\n",Dumper($vltbl),"\n" if $DEBUG;
				}
				# [[1st cell], [2nd cell], [3rd cell],..]
				# each value-label cell: structure
				# 1st: value
				# 2nd: label
				# 3rd: freq
				# 4th: miss var code
				# if the size of a cell is 4, it is a missing value
				# $catStat->{$line[0]}=[ @line[3 .. ($#line-4)] ];
				
				my $lineCatStat=[ @line[3 .. ($#line-4)] ];
				
				# ////////////////////////////////////////////////////////
				# start the merging
				# ////////////////////////////////////////////////////////

				print $FH "vltbl=\n",Dumper($vltbl),"\n" if $DEBUG;
				print $FH "no of categories(vltbl)=",$#{$vltbl},"\n" if $DEBUG;
				my $m=[];

				foreach my $em (@{$vltbl}){
					push @$m, $em->[0];
				}
				print $FH "main(m)=",join("|",@$m),"\n" if $DEBUG;
				my $lnm = scalar(@$m);

				my $cv=[];
				my $cf=[];
				my $ch={};
				until(scalar(@$lineCatStat) == 0){
					my $v1 = shift @$lineCatStat;
					my $v2 = shift @$lineCatStat;
					push @$cv,$v1;
					push @$cf,$v2;
					$ch->{$v1}=$v2;
				}
				#print "cv_value=",join("|",@$cv),"\n";
				#print "cf=",join("|",@$cf),"\n\n";
				#print "ch=",Dumper($ch),"\n\n";

				# check the relationship between m and s
				# [0] union, [1] intersect, [2] diff, [3] type(see value below)
				# 1 = no intersection 2=non-empty intersection
				# 3 = meta include actual 4 = actual included meta
				# 5 = completely equal
				my $obj = &checkSets($m, $cv);
				my $settype=$obj->[3];
				print $FH "merge type =",$settype,"\n\n" if $DEBUG;
				my $lnu= scalar(@{$obj->[0]});
				my $lni= scalar(@{$obj->[1]});
				my $lnd= scalar(@{$obj->[2]});
				my $vlset_mo = $obj->[4]->[0];
				my $vlset_co = $obj->[4]->[1];
				my $lnc= scalar(@$cv);

				my $vlset_moh={};
				if (defined ($vlset_mo)){
					foreach my $i (@$vlset_mo){
						$vlset_moh->{$i}='y';
					}
				}
				my $vlset_ih={};
				if ($lni){
					foreach my $i (@{$obj->[1]}){
						$vlset_ih->{$i}='y';
					}

				}
				#if (defined($vlset_co) ){
				#	print "c only values=",join('-',@$vlset_co),"\n";
				#}
				my $uplimit=50;

				my $mv;
				if ($lnu > $uplimit ) {
					print $FH "beyond the upper limit of categories\n" if $DEBUG;

					if ($settype == 1){
						# case 1: no intersection
						# scalar(@$m) == scalar(@$vltbl)
						# set zero to the main table
						for (my $k=0;$k <@$vltbl;$k++){
							$vltbl->[$k]->[2]=0;
						}
						# add new value-freq pairs to the main table
						#$mv=undef;
						#for (my $l=0;$l<@$cv; $l++) {
						#	if ($cv->[$l] eq '.'){my $mv= 'y';}
						#	push @$vltbl,[$cv->[$l], undef, $cf->[$l],$mv];
						#}


					} elsif ($settype ==5){
						# case 5
						# m and c are completely matched
						$mv=undef;
						for (my $k=0;$k<@$m; $k++) {
							if ($m->[$k] eq '.'){$mv= 'y';}
							$vltbl->[$k]->[2]=$ch->{$vltbl->[$k]->[0]};
							if ($mv) {$vltbl->[$k]->[3]=$mv;}
							$mv=undef;
						}

					} else {
						# cases 2, 3, 4
						# two steps
						# step 1: for all matched categories of m,
						# set freq from c to each of these categories
						# processing set: all intersect values: $obj->[1]
						for (my $k=0;$k <@$vltbl;$k++){
							if (exists($vlset_ih->{$vltbl->[$k]->[0]})) {
								$vltbl->[$k]->[2]=$ch->{$vltbl->[$k]->[0]};
							}
						}
						# step 2: for all non-matched categories of m, if exist,
						# set their freq = 0;
						# this step is not applicable to case 4
						# processing set: diff (M, (M and S)): $vlset_m = $obj->[4]->[0];
						if ($settype != 4){
							for (my $k=0;$k <@$vltbl;$k++){
								if (exists($vlset_moh->{$vltbl->[$k]->[0]})) {
									$vltbl->[$k]->[2]=0;
								}
							}
						}
					}
				} else {
					#print "within the upper limit of categories\n";
					# less than limit
					if ($settype == 1){

						# case 1
						# scalar(@$m) == scalar(@$vltbl)
						# set freq = 0 for all categories of the main table
						#print "case 1\n";
						for (my $k=0;$k <@$vltbl;$k++){
							$vltbl->[$k]->[2]=0;
						}
						# add all value-freq pairs of c to the main table
						$mv=undef;
						for (my $l=0;$l<@$cv; $l++) {
							if ($cv->[$l] eq '.'){$mv= 'y';}
							push @$vltbl,[$cv->[$l], undef, $cf->[$l],$mv];
							$mv=undef;
						}

					} elsif ($settype ==5){
						# case 5
						# m and c are completely matched
						#print "case 5\n";
						$mv=undef;
						for (my $k=0;$k<@$m; $k++) {
							if ($m->[$k] eq '.'){$mv= 'y';}
							$vltbl->[$k]->[2]=$ch->{$vltbl->[$k]->[0]};
							if ($mv) {$vltbl->[$k]->[3]=$mv;}
							$mv=undef;
						}

					} else{
						#print "cases 2,3,4\n";
						# cases 2, 3, 4
						# 3 [case 2] or 2[cases 3/4] steps
						# 
						# step 1: matching: the intersection [all 3 cases]
						$mv=undef;
						for (my $k=0;$k<@$vltbl; $k++) {
							if (exists($vlset_ih->{$vltbl->[$k]->[0]})) {
								if ($vltbl->[$k]->[0] eq '.'){$mv ='y';}
								$vltbl->[$k]->[2]=$ch->{$vltbl->[$k]->[0]};
								if ($mv) {$vltbl->[$k]->[3]=$mv;}
							}
							$mv=undef;
						}
						# setp 2: set freq = 0 for non-matching categories of m [cases 2 and 3 only]
						if ($settype != 4){
							for (my $k=0;$k <@$vltbl;$k++){
								if (exists($vlset_moh->{$vltbl->[$k]->[0]})) {
									$vltbl->[$k]->[2]=0;
								}
							}
						}
						# sep 3: add new value-freq pairs from c to m [cases 2 and 4 only]
						if ($settype != 3){
							$mv=undef;
							for (my $l=0;$l<@$vlset_co ; $l++) {
								if ($vlset_co->[$l] eq '.'){$mv= 'y';}
								push @$vltbl,[ $vlset_co->[$l], undef, $ch->{$vlset_co->[$l]},$mv];
								$mv=undef;
							}
						}
					} # end of within the uppler limit case
				} # more than the upper limit or not
				
				print $FH "result_table:\n",Dumper($vltbl),"\n" if $DEBUG;
				# $line[0];current var name
				print $FH "line[0](current var name)=",$line[0],"\n" if $DEBUG;
				
				if ( (exists($mvvrmp->{$line[0]})) &&  (exists($self->{_mssvlTbl}->{$mvvrmp->{$line[0]}})) && ($self->{_mssvlTbl}->{$mvvrmp->{$line[0]}}) ){
					my $mv_i = $self->{_mssvlTbl}->{$mvvrmp->{$line[0]}};
					my $mv_i_h = {};
					foreach my $em (@{$mv_i}){
						$mv_i_h->{$em->[0]} = 'y';
					}
					print $FH "mv i hash:\n",Dumper($mv_i_h),"\n" if $DEBUG;
					# here vltbl is $key_var's value-label table 
					for (my $k=0;$k <@$vltbl;$k++){
						if (exists($mv_i_h->{$vltbl->[$k]->[0]})) {
							$vltbl->[$k]->[3]='y';
						}
					}
				}
				print $FH "result_table(last):\n",Dumper($vltbl),"\n" if $DEBUG;
				
				# ////////////////////////////////////////////////////////
				# end 
				# ////////////////////////////////////////////////////////
			} # sumStat or catStat level
			
		} # 1st or 2nd line level 
		$j++;
	} # while(sumStat/catStat file) level
	
	
	$sh->close();
	
	# file-wise UNF
	$unfh->close();
	print $FH "\n",$UNFcounter," UNF records are written into ",$tmpUNF,"\n"  if $DEBUG;
	
	my $unfrcode = "$TMPDIR/unfrcd.$$.$filenmbr.R";
	$rh = new IO::File("> $unfrcode");
	
	# source("/usr/local/VDC/R/library/vdc_startup.R"); z<-read.table("tmpUNF.tmp",comment.char="",as.is=TRUE );cat(paste(summary(as.unf(z[[1]]))))
		
	print $rh "source(\"/usr/local/VDC/R/library/vdc_startup.R\"); z<-read.table(\"$tmpUNF\",sep=\"\\t\", comment.char=\"\",as.is=TRUE );cat(paste(summary(as.unf(z[[1]]))))\n";
	$rh->close();
	
	
	open(PH, "R --slave < $unfrcode  2>&1 |");
	while (<PH>) {
		chomp;
		print $FH $_ if $DEBUG;
		if (/(Error\s+in)|(Execution\s+halted)/i){
			print $FH "error in R run=",$1,"\t",$2,"\n" if $DEBUG;
			print $FH "error in R run=",$_,"\n" if $DEBUG;
			#$runr=0;
		} elsif (/^UNF:/) {
			print $FH "file-wise UNF:", $_,"\n" if $DEBUG;
			$fileUNF=$_;
		}
	}
	close(PH);

	print $FH "file-wise UNF=",$fileUNF,"\n" if $DEBUG;
	$self->{_fileDscr}->{fileUNF}=$fileUNF;
	unlink($unfrcode);
	unlink($tmpUNF);
	
	# access to each discret var's value-label table
	#
	$self->{_sumStat}=$sumStat;
	print $FH "sumStat:\n",Dumper($self->{_sumStat}),"\n" if $DEBUG;
	print $FH "catStat:\n",Dumper($self->{_valLblTbl}),"\n" if $DEBUG;
	print $FH "UNF:\n",Dumper($self->{_UNF}), "\n" if $DEBUG;
	print $FH "fileUNF: ",$self->{_fileDscr}->{fileUNF}, "\n" if $DEBUG;
	#$FH->close() if $DEBUG;
}

# /////////////////////////////////////////////////////////
# auxiliary methods for DDI writing methods
# /////////////////////////////////////////////////////////

sub getFileNo{
	my $self = shift;
	my $rv;
	if (exists($self->{_prcssOptn}->{FILENO})){
		$rv = $self->{_prcssOptn}->{FILENO};
	} else {
		$rv = 1;
	}
}

sub getVarID {
	my $self   = shift;
	my $filePt = shift;
	my $varPt  = shift;
	my $varID  = 'v' . $filePt . '.' . $varPt;
	return $varID;
}

sub getVarFrmtType {
	my $self = shift;
	my $varname = shift;
	my $attvalue="numeric";
	if (exists($self->{_prcssOptn}->{SPSFILE})) {
		# this block is necessary due to inconisistencies in variable-type code
		# between sps-file and sav(por)-file cases
		# for sps, char vars are coded '0' whereas for sav/por char vars code non-zero int
		
		if (exists($self->{_charVarTbl}->{$varname})){
			my $vCatgry = $SPSS_FRMT_CATGRY_TABLE->{ $self->{_varFormat}->{$varname} };
			if (($vCatgry eq 'date') || ($vCatgry eq 'time')){
				
			} else {
				# non date/time case
				$attvalue="character";
			}
		}
	} elsif ($self->{_fileDscr}->{fileType} =~ /stata/){
		my $j = $self->{_varNameH}->{$varname};
		if (exists($self->{_charVarTbl}->{$varname})){
			if ($self->{fmtlst}->[$j]=~/^[%][d]/) {
				$attvalue="numeric";
			} else {
				$attvalue="character";
			}
		}
	} else {

		if (exists($self->{_charVarTbl}->{$varname})){
			$attvalue="character";
		}
	
	}
	return $attvalue;
}

sub testMSVL{
	my $self = shift;
	my $test=1;
	if (exists($self->{_prcssOptn}->{MSVL})  && (!($self->{_prcssOptn}->{MSVL})) ){
		# sps && recode case 
		$test = 0;
	} 
	return $test;
}


sub testMissingValue{
	my $self=shift;
	my $rv=0;
	my $mvexists=0;
	if  (exists($self->{_mvTpCdTbl}) && (scalar(keys %{$self->{_mvTpCdTbl}}))){
		$mvexists=1;
	}
	if (exists($self->{_prcssOptn}->{MSVL}) && (!($self->{_prcssOptn}->{MSVL})) ){
		if (exists($self->{_rdTbl})){
			$rv=1;
		}
	} elsif ($mvexists) {
		$rv=1;
	}
	return $rv;
}
sub testVarLabel{
	my $self=shift;
	my $rv=0;
	if (exists($self->{_varLabel}) && (scalar(keys %{$self->{_varLabel}}))){
		$rv = 1;
	}
	return $rv;
}

sub testDcml{
	my $self=shift;
	my $rv=0;
	if (exists($self->{_noDcmlVar}) && ($self->{_noDcmlVar})){
		$rv = 1;
	}
	return $rv;
}

sub getVarAttDcml{
	my $self = shift;
	my $varname = shift;
	my $attvalue=0;
	if (exists($self->{_dcmlVarTbl}->{$varname})){
		$attvalue=$self->{_dcmlVarTbl}->{$varname};
	}
	return $attvalue;
}



sub getLocationContents{
	my $self=shift;
	my $VN=shift;
	my $varName = $$VN;
	my $rv="";
	if (exists($self->{_locationInfo})){
		my ($StartPos, $EndPos)=@{$self->{_locationInfo}->{$varName}}[1..2];
		my $RecSegNo =$self->{_locationInfo}->{$varName}->[0];
		$rv =join('', ("StartPos=\"",$StartPos,"\" EndPos=\"",$EndPos,"\" RecSegNo=\"",$RecSegNo,"\""));
		$rv .= ' ';
	}
	return $rv;
}


sub getVarNameSet{
	my $self = shift;
	my $varNameSet;
	if (exists($self->{varShrtLngMpTbl}) && (scalar(keys(%{$self->{varShrtLngMpTbl}}))) ){
		$varNameSet=[];
		foreach my $ky (@{$self->{_varNameA}}){
			push @{$varNameSet}, $self->{varShrtLngMpTbl}->{$ky};
		}
	} else {
		$varNameSet=$self->{_varNameA};
	}
	return $varNameSet;
}
sub getRsafeVarNameSet{
	my $self = shift;
	my $rawvarname = $self->{_varNameA};
	my $raw2RsafeVarName={};
	my $RsafeVarName2raw={};
	my $varName=[];
	for (my $i=0;$i<@{$rawvarname};$i++){
		my $tmpVN = $rawvarname->[$i];
		# check it against the R-reserved-word list
		if (exists($Rkywrd2safeVarName->{$tmpVN})){
			$tmpVN = $Rkywrd2safeVarName->{$tmpVN};
			$raw2RsafeVarName->{$rawvarname->[$i]}=$tmpVN;
			$RsafeVarName2raw->{$tmpVN}=$rawvarname->[$i];
		} else {
			# special characters
			foreach my $token (keys %{$CNVRSNTBL}){
				$tmpVN =~ s/[$token]/$CNVRSNTBL->{$token}/g;
			}
			# non-ASCII character check
			$tmpVN = encode_entities_numeric($tmpVN);
			$tmpVN =~ s/&#x(\w+);/hex$1/ig;

			if ($tmpVN ne $rawvarname->[$i]){
				$raw2RsafeVarName->{$rawvarname->[$i]}=$tmpVN;
				$RsafeVarName2raw->{$tmpVN}=$rawvarname->[$i];
			}
		}
		
		push @{$varName}, $tmpVN;
	}
	$self->{raw2RsafeVarName} = $raw2RsafeVarName;
	$self->{RsafeVarName2raw} = $RsafeVarName2raw;
	return $varName;
}


sub writeDDIinXML{
	my $self = shift;
	my $optns = {@_};
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};

	my $base     = $self->{_prcssOptn}->{SOURCEFILE};
	my $fileid   = $self->{_fileDscr}->{fileID};
	my $filenmbr = $self->{_prcssOptn}->{FILENO};
	
	my $ddifile;
	unless (exists($optns->{DDIFILE})){
		$base =~s/\.([^\.]*)$//;
		$ddifile = $base . '.xml';
		print $FH "package-generated ddi file name will be used: $ddifile\n" if $DEBUG;
	} else {
		$ddifile   = $optns->{DDIFILE};
		print $FH "user-supplied ddi file name will be used: $ddifile\n" if $DEBUG;
	}
	$self->{_prcssOptn}->{DDIFILE}=$ddifile;
	
	my $output = new IO::File(">$ddifile");
	
	# "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
	my $encoding = "ISO-8859-1" unless (exists($optns->{ENCODING}) && defined($optns->{ENCODING}));
	$output->print("<?xml version=\"1.0\"");
	if ($encoding) {
	  $output->print(" encoding=\"$encoding\"");
	}
	$output->print("?>\n");
	
	# DOCTYPE
	# "<!DOCTYPE codeBook SYSTEM \"http://www.icpsr.umich.edu/DDI/Version1-02-1.dtd\">\n"
	my $DDIverNo = "1-02-1" unless (exists($optns->{DDIVERNO}) && defined($optns->{DDIVERNO}));
	$output->print("<!DOCTYPE codeBook SYSTEM \"http://www.icpsr.umich.edu/DDI/Version$DDIverNo.dtd\">\n");
	
	# DDI: Sec. 1 to Sec. 2
	$output->print("<codeBook>\n<docDscr/>\n<stdyDscr>\n\t<citation>\n\t\t<titlStmt>\n\t\t\t<titl/>\n\t\t\t<IDNo agency=\"\"/>\n\t\t</titlStmt>\n\t</citation>\n</stdyDscr>\n");
	
	# DDI: Sec. 3
	my $nobs    = $self->{_fileDscr}->{caseQnty};
	my $nvar    = $self->{_fileDscr}->{varQnty};
	my $charset = $self->{_fileDscr}->{charset};
	my $filetype= $self->{_fileDscr}->{fileType};
	my $fileUNF = $self->{_fileDscr}->{fileUNF};
	
	my $recPrCas="";
	if (exists($self->{_fileDscr}->{RECORDS}) && ($self->{_fileDscr}->{RECORDS}) ){
		$recPrCas=$self->{_fileDscr}->{RECORDS};
		$recPrCas="\t\t\t<recPrCas>" . $recPrCas . "</recPrCas>\n";
	}
	
	my $fileFormat="";
	if (exists($self->{_fileDscr}->{fileFrmt}) && ($self->{_fileDscr}->{fileFrmt}) ){
		$fileFormat = $self->{_fileDscr}->{fileFrmt};
		$fileFormat = "\t\t<format>" . $fileFormat . "</format>\n";
	}
	
	# notes: UNF
	my $fileNoteUNF="";
	if (exists($self->{_fileDscr}->{fileUNF}) && ($self->{_fileDscr}->{fileUNF}) ){
		$fileNoteUNF = $self->{_fileDscr}->{fileUNF};
		$fileNoteUNF = "\t<notes subject=\"Universal Numeric Fingerprint\" level=\"file\" source=\"archive\" type=\"VDC:UNF\">" . $fileNoteUNF . "</notes>\n";
	}
	
	# notes: original file type

	my $fileNoteFileType="";
	if ($filetype){
		$fileNoteFileType="\t<notes subject=\"original file format\" level=\"file\" source=\"archive\" type=\"VDC:MIME\">" . $filetype . "</notes>\n";
	}
	
	my $filetypex="text/tab-separated-values";
	
	$output->print(join("","<fileDscr ID=\"",$fileid,"\" URI=\"\">\n\t<fileTxt>\n\t\t<dimensns>\n\t\t\t<caseQnty>",$nobs,"</caseQnty>\n\t\t\t<varQnty>",$nvar,"</varQnty>\n",$recPrCas,"\t\t</dimensns>\n\t\t<fileType charset=\"",$charset,"\">",$filetypex,"</fileType>\n",$fileFormat,"\t</fileTxt>\n",$fileNoteUNF,$fileNoteFileType,"</fileDscr>\n"));
	
	# DDI: Sec. 4
	$output->print("<dataDscr>\n");
	
	my ($varCntr, $varid, $varName, $varNameL, $attIntrvl, $attDcml, $attVarFrmtType);
	$varCntr=0;
	
	my @sumStatse=("mean", "medn", "mode", "vald", "invd", "min", "max", "stdev");
	my @sumStatseC=("vald", "invd");
	my ($minThrshld, $maxThrshld, $msvlRngType);
	my $varNameSet = $self->getVarNameSet();
	my $varTypeSet = $self->getVarTypeSet();
	for (my $j=0; $j<@{$self->{_varNameA}}; $j++){
		
		$varCntr++;
		
		# var tag: opening
		# @ID
		$varid = $self->getVarID($filenmbr, $varCntr);
		
		# @name
		$varName = $self->{_varNameA}->[$j];
		$varNameL = $varNameSet->[$j];
		$output->print(join('', ("\t<var ID=\"",$varid,"\" name=\"",encode_entities_numeric($varNameL),"\"")));
		
		# @intrvl & @dcml 
		$attIntrvl='discrete';
		if ($self->testDcml()){
			# contin vars exist
			if ($varTypeSet->[$j]==2){
				$attIntrvl='contin';
				$attDcml = $self->getVarAttDcml(\$varName);
				my $dcmlCntnts ="";
				if ($attDcml){
					$dcmlCntnts= " dcml=\"" . $attDcml . "\"";
				}
				$output->print(join('', (" intrvl=\"",$attIntrvl,"\"",$dcmlCntnts,">\n")));
			} else {
				$output->print(join('', (" intrvl=\"",$attIntrvl,"\">\n")));
			}
		} else {
			# no contin var => all discrete vars
			$output->print(join('', (" intrvl=\"", $attIntrvl,"\">\n")));
		}
		
		# location tag
		my $lctnCntnts="";
		$lctnCntnts = $self->getLocationContents(\$varName);
		
		$output->print(join('', ("\t\t<location ",$lctnCntnts,"fileid=\"",$fileid,"\"/>\n")));
		
		# var label tag 
		if ($self->testVarLabel()){
			if (exists($self->{'_varLabel'}->{$varName}) && ($self->{'_varLabel'}->{$varName} ne ' ') && ($self->{'_varLabel'}->{$varName} ne '') ){
				$output->print("\t\t<labl level=\"variable\">",encode_entities_numeric($self->{'_varLabel'}->{$varName}),"</labl>\n");
			}
		}
		
		# invalid rage
		my ($mvtbl,$mvvrmp,$mvtypcd, $mvSet, $mvType);
		
		# set up references
		my $mvexists = 0;
		if ($self->testMissingValue()){
			
			if ($self->testMSVL()){
				$mvtbl   = $self->{_mssvlTbl};
				$mvvrmp  = $self->{_mvVrMpTbl};
				$mvtypcd = $self->{_mvTpCdTbl};
			} else {
				$mvtbl   = $self->{_rdTbl};
				$mvvrmp  = $self->{_rdVrMpTbl};
				$mvtypcd = $self->{_rdTpCdTbl};
			}
			
			$mvexists=1;
			# print "processing missing value\n";
			undef($minThrshld);
			undef($maxThrshld);
			$msvlRngType=0;
			if ( exists($mvvrmp->{$varName}) && exists($mvtbl->{$mvvrmp->{$varName}})){
				# notational short-cut
				$mvSet = $mvtbl->{$mvvrmp->{$varName}};
				$mvType= $mvtypcd->{$mvvrmp->{$varName}};
				# print $varName,"(mv type=",$mvType,")\n";
				# deal with vars with miss.vl. only
				if ($mvType > 0) {
					# non-range-type missing values (up to 3)
					$output->print("\t\t<invalrng>\n");
					for (my $k=0; $k <=$#{$mvSet}; $k++){
						$output->print("\t\t\t<item VALUE=\"",$mvSet->[$k][0],"\"/>\n");
					}
					$output->print("\t\t</invalrng>\n");
					
				} elsif ($mvType == -2 ) {
					#range-type 1 missing values
					$output->print("\t\t<invalrng>\n");
					$output->print("\t\t\t<range");
					if ($mvSet->[0][0] ne 'LOWEST'){
						$output->print(" min=\"",$mvSet->[0][0],"\"");
						$minThrshld = $mvSet->[0][0];
						$msvlRngType++;
					}
					if ($mvSet->[0][1] ne 'HIGHEST') {
						$output->print(" max=\"",$mvSet->[0][1],"\"");
						$maxThrshld = $mvSet->[0][1];
						$msvlRngType++;
					}
					$output->print("/>\n");
					$output->print("\t\t</invalrng>\n");
				} elsif ($mvType == -3 ) {
					#range-type: 2 missing values
					
					$output->print("\t\t<invalrng>\n");
					
					# check the length of the first element:
					# one range(length = 1) or one item (length=0)
					
					if ($#{$mvSet->[0]} == 0){
						# first one is item and second one is range
						$output->print("\t\t\t<range");
						if ($mvSet->[1][0] ne 'LOWEST') {
							$output->print(" min=\"",$mvSet->[1][0],"\"");
							$minThrshld = $mvSet->[1][0];
							$msvlRngType++;
						}
						if ($mvSet->[1][1] ne 'HIGHEST') {
							$output->print(" max=\"",$mvSet->[1][1],"\"");
							$maxThrshld = $mvSet->[1][1];
							$msvlRngType++;
						}
						$output->print("/>\n");
						$output->print("\t\t\t<item VALUE=\"",$mvSet->[0][0],"\"/>\n");
					} elsif ($#{$mvSet->[0]} == 1) {
						$output->print("\t\t\t<range");
						if ($mvSet->[0][0] ne 'LOWEST') {
							$output->print(" min=\"",$mvSet->[0][0],"\"");
							$minThrshld = $mvSet->[0][0];
							$msvlRngType++;
						}
						if ($mvSet->[0][1] ne 'HIGHEST') {
							$output->print(" max=\"",$mvSet->[0][1],"\"");
							$maxThrshld = $mvSet->[0][1];
							$msvlRngType++;
						}
						$output->print("/>\n");
						$output->print("\t\t\t<item VALUE=\"",$mvSet->[1][0],"\"/>\n");
					}
					$output->print("\t\t</invalrng>\n");
				}
			}
		}
		#if (exists($self->{_dcmlVarTbl}->{$varName})) {
			# sumStat: $self->{_sumStat}
		if (scalar(@{$self->{_sumStat}->{$varName}})>3 ){
			for (my $l=0;$l<@sumStatse;$l++){
				$output->print("\t\t<sumStat type=\"",$sumStatse[$l],"\">",$self->{_sumStat}->{$varName}->[$l],"</sumStat>\n");
			}
		} else {
			for (my $l=0;$l<@sumStatseC;$l++){
				$output->print("\t\t<sumStat type=\"",$sumStatseC[$l],"\">",$self->{_sumStat}->{$varName}->[$l],"</sumStat>\n");
			}
		}
		#} els
		if (exists($self->{_valVrMpTbl}->{$varName})){
			if (exists($self->{_valLblTbl}->{$self->{_valVrMpTbl}->{$varName}}) && ($self->{_valLblTbl}->{$self->{_valVrMpTbl}->{$varName}}) ){
				#print "xml: current var=",$varName,"\n";
				# catStat: value/value labels
				# data structure 
				# [0:value, 1:label, 2:freq, 3:MV(yes='y')
				# 'PARTY3' =>[[1,'REPUBLICAN',undef,undef], [2,'DEMOCRATIC',undef,undef],..]
				my $mvtoken='';
				my $vllblSet = $self->{_valLblTbl}->{$self->{_valVrMpTbl}->{$varName}};
				#print Dumper($vllblSet);
				#my $varFmt = $self->{_varFormat}->{$varName};
				for (my $k=0; $k <= $#{$vllblSet}; $k++) {
					#print "varName=",$self->{_valVrMpTbl}->{$varName},"\n";
					#print $k,"\t", scalar(@{$vllblSet->[$k]}), "\n";
					if ( ($vllblSet->[$k][3]) && ($vllblSet->[$k][3] eq 'y')){
						$mvtoken=" missing=\"Y\"";
					}
					$output->print("\t\t<catgry",$mvtoken,">\n");
					
					if (($self->{_fileDscr}->{fileType} =~ 'spss') || exists($self->{_prcssOptn}->{SPSFILE})) {
						if ($self->{_varFormat}->{$varName} eq 'TIME'){
							my @timedata= split(/ /,$vllblSet->[$k][0]);
							$output->print("\t\t\t<catValu>",$timedata[1],"</catValu>\n");
						} else {
							if ($varTypeSet->[$j]==0){
								$output->print("\t\t\t<catValu>",encode_entities_numeric($vllblSet->[$k][0]),"</catValu>\n");
							} else {
								$output->print("\t\t\t<catValu>",$vllblSet->[$k][0],"</catValu>\n");
							}
						}
					} else {
						if ($varTypeSet->[$j]==0){
							$output->print("\t\t\t<catValu>",encode_entities_numeric($vllblSet->[$k][0]),"</catValu>\n");
						} else {
							$output->print("\t\t\t<catValu>",$vllblSet->[$k][0],"</catValu>\n");
						}
					}
					
					if ($vllblSet->[$k][1]){
						$output->print("\t\t\t<labl level=\"CATEGORY\">",encode_entities_numeric($vllblSet->[$k][1]),"</labl>\n");
					}
					$output->print("\t\t\t<catStat>",$vllblSet->[$k][2],"</catStat>\n");
					$output->print("\t\t</catgry>\n");
					$mvtoken='';
				}
			}
		}
		# varFormat tag
		if ($self->{_noCharVar} >0) {
			$attVarFrmtType=&getVarFrmtType($self,$varName);
		} else {
			$attVarFrmtType='numeric';
		}
		
		if (($self->{_fileDscr}->{fileType} =~ 'spss') || exists($self->{_prcssOptn}->{SPSFILE})) {
			if ( ($self->{_varFormat}->{$varName} eq 'A') || ($self->{_varFormat}->{$varName} eq 'F') || ($self->{_varFormat}->{$varName} eq 'I') ){
				$output->print(join('',("\t\t<varFormat type=\"",$attVarFrmtType,"\"/>\n")));
			} else {
				# print like this:
				# <varFormat type='numeric' schema='SPSS' formatname='ADATE' category=date />

				$output->print(join('',("\t\t<varFormat type=\"",$attVarFrmtType,"\" schema=\"SPSS\" formatname=\"",$self->{_formatName}->{$varName},"\" category=\"", $SPSS_FRMT_CATGRY_TABLE->{ $self->{_varFormat}->{$varName} },"\"/>\n")));
				
			}
		} elsif ($self->{_fileDscr}->{fileType} =~ /stata/){
			if ($self->{fmtlst}->[$j]=~/^[%][d]/) {
				$output->print(join('',("\t\t<varFormat type=\"",$attVarFrmtType,"\" schema=\"other\" formatname=\"",$self->{fmtlst}->[$j],"\" category=\"date\"/>\n")));
			} else {
				$output->print(join('',("\t\t<varFormat type=\"",$attVarFrmtType,"\"/>\n")));
			}
		} else {
			$output->print(join('',("\t\t<varFormat type=\"",$attVarFrmtType,"\"/>\n")));
		}
		
		# notes: UNF
		$output->print("\t\t<notes subject=\"Universal Numeric Fingerprint\" level=\"variable\" source=\"archive\" type=\"VDC:UNF\">",$self->{_UNF}->[$j],"</notes>\n");
		
		# var tag: closing
		$output->print("\t</var>\n");

	} # var-wise

	$output->print("</dataDscr>\n");
	
	# Sec. 5
	$output->print("<otherMat type=\"\" level=\"study\"  URI=\"\">\n\t<labl></labl>\n\t<txt></txt>\n\t<notes></notes>\n</otherMat>\n");
	# end
	$output->print("</codeBook>\n");
}

sub writeDDIsec3{
	my $self = shift;
	my $optns = {@_};
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};

	my $base     = $self->{_prcssOptn}->{SOURCEFILE};
	my $fileid   = $self->{_fileDscr}->{fileID};
	my $filenmbr = $self->{_prcssOptn}->{FILENO};

	my $ddifile;
	unless (exists($optns->{DDI3FILE})){
		$base =~s/\.([^\.]*)$//;
		$ddifile = $base . '.sec3.xml';
		print $FH "package-generated ddi file name will be used: $ddifile\n" if $DEBUG;
	} else {
		$ddifile   = $optns->{DDI3FILE};
		print $FH "user-supplied ddi file name will be used: $ddifile\n" if $DEBUG;
	}
	
	my $output;
	if ($filenmbr == 1) {
		$output = new IO::File(">$ddifile");
	} elsif ($filenmbr > 1){
		$output = new IO::File(">> $ddifile");
	}
	
	# DDI: Sec. 3
	my $nobs    = $self->{_fileDscr}->{caseQnty};
	my $nvar    = $self->{_fileDscr}->{varQnty};
	my $charset = $self->{_fileDscr}->{charset};
	my $filetype= $self->{_fileDscr}->{fileType};
	my $fileUNF = $self->{_fileDscr}->{fileUNF};
	
	my $recPrCas="";
	if (exists($self->{_fileDscr}->{RECORDS}) && ($self->{_fileDscr}->{RECORDS}) ){
		$recPrCas=$self->{_fileDscr}->{RECORDS};
		$recPrCas="\t\t\t<recPrCas>" . $recPrCas . "</recPrCas>\n";
	}
	
	my $fileFormat="";
	if (exists($self->{_fileDscr}->{fileFrmt}) && ($self->{_fileDscr}->{fileFrmt}) ){
		$fileFormat = $self->{_fileDscr}->{fileFrmt};
		$fileFormat = "\t\t<format>" . $fileFormat . "</format>\n";
	}
	
	# notes: UNF
	my $fileNoteUNF="";
	if (exists($self->{_fileDscr}->{fileUNF}) && ($self->{_fileDscr}->{fileUNF}) ){
		$fileNoteUNF = $self->{_fileDscr}->{fileUNF};
		$fileNoteUNF = "\t<notes subject=\"Universal Numeric Fingerprint\" level=\"file\" source=\"archive\" type=\"VDC:UNF\">" . $fileNoteUNF . "</notes>\n";
	}
	
	# notes: original file type
	
	my $fileNoteFileType="";
	if ($filetype){
		$fileNoteFileType="\t<notes subject=\"original file format\" level=\"file\" source=\"archive\" type=\"VDC:MIME\">" . $filetype . "</notes>\n";
	}
	
	my $filetypex="text/tab-separated-values";
	
	$output->print(join("","<fileDscr ID=\"",$fileid,"\" URI=\"\">\n\t<fileTxt>\n\t\t<dimensns>\n\t\t\t<caseQnty>",$nobs,"</caseQnty>\n\t\t\t<varQnty>",$nvar,"</varQnty>\n",$recPrCas,"\t\t</dimensns>\n\t\t<fileType charset=\"",$charset,"\">",$filetypex,"</fileType>\n",$fileFormat,"\t</fileTxt>\n",$fileNoteUNF,$fileNoteFileType,"</fileDscr>\n"));
	
	$output->close();
}

sub writeDDIsec4{
	my $self = shift;
	my $optns = {@_};
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};

	my $base     = $self->{_prcssOptn}->{SOURCEFILE};
	my $fileid   = $self->{_fileDscr}->{fileID};
	my $filenmbr = $self->{_prcssOptn}->{FILENO};
	
	my $ddifile;
	unless (exists($optns->{DDI4FILE})){
		$base =~s/\.([^\.]*)$//;
		$ddifile = $base . '.' . $filenmbr . '.xml';
		print $FH "package-generated ddi file name will be used: $ddifile\n" if $DEBUG;
	} else {
		$ddifile = $optns->{DDI4FILE};
		print $FH "user-supplied ddi file name will be used: $ddifile\n" if $DEBUG;
	}
	
	my $output = new IO::File(">$ddifile");
	
	# DDI: Sec. 4
#	$output->print("<dataDscr>\n");
	
	my ($varCntr, $varid, $varName, $varNameL, $attIntrvl, $attDcml, $attVarFrmtType);
	$varCntr=0;
	
	my @sumStatse=("mean", "medn", "mode", "vald", "invd", "min", "max", "stdev");
	my @sumStatseC=("vald", "invd");

	my ($minThrshld, $maxThrshld, $msvlRngType);
	my $varNameSet = $self->getVarNameSet();
	my $varTypeSet = $self->getVarTypeSet();
	
	for (my $j=0; $j<@{$self->{_varNameA}}; $j++){
		
		$varCntr++;
		
		# var tag: opening
		# @ID
		$varid = $self->getVarID($filenmbr, $varCntr);
		
		# @name
		$varName = $self->{_varNameA}->[$j];
		$varNameL = $varNameSet->[$j];
		$output->print(join('', ("\t<var ID=\"",$varid,"\" name=\"",encode_entities_numeric($varNameL),"\"")));
		
		# @intrvl & @dcml 
		$attIntrvl='discrete';
		if ($self->testDcml()){
			# contin vars exist
			if ($varTypeSet->[$j]==2){
				$attIntrvl='contin';
				$attDcml = $self->getVarAttDcml(\$varName);
				my $dcmlCntnts ="";
				if ($attDcml){
					$dcmlCntnts= " dcml=\"" . $attDcml . "\"";
				}
				$output->print(join('', (" intrvl=\"",$attIntrvl,"\"",$dcmlCntnts,">\n")));
			} else {
				$output->print(join('', (" intrvl=\"",$attIntrvl,"\">\n")));
			}
		} else {
			# no contin var => all discrete vars
			$output->print(join('', (" intrvl=\"", $attIntrvl,"\">\n")));
		}
		
		# location tag
		my $lctnCntnts="";
		$lctnCntnts = $self->getLocationContents(\$varName);
		
		$output->print(join('', ("\t\t<location ",$lctnCntnts,"fileid=\"",$fileid,"\"/>\n")));
		
		# var label tag 
		if ($self->testVarLabel()){
			if (exists($self->{'_varLabel'}->{$varName}) && ($self->{'_varLabel'}->{$varName} ne ' ') && ($self->{'_varLabel'}->{$varName} ne '') ){
				$output->print("\t\t<labl level=\"variable\">",encode_entities_numeric($self->{'_varLabel'}->{$varName}),"</labl>\n");
			}
		}
		
		# invalid rage
		my ($mvtbl,$mvvrmp,$mvtypcd, $mvSet, $mvType);
		
		# set up references
		my $mvexists = 0;
		if ($self->testMissingValue()){
			
			if ($self->testMSVL()){
				$mvtbl   = $self->{_mssvlTbl};
				$mvvrmp  = $self->{_mvVrMpTbl};
				$mvtypcd = $self->{_mvTpCdTbl};
			} else {
				$mvtbl   = $self->{_rdTbl};
				$mvvrmp  = $self->{_rdVrMpTbl};
				$mvtypcd = $self->{_rdTpCdTbl};
			}
			
			$mvexists=1;
			# print "processing missing value\n";
			undef($minThrshld);
			undef($maxThrshld);
			$msvlRngType=0;
			if ( exists($mvvrmp->{$varName}) && exists($mvtbl->{$mvvrmp->{$varName}})){
				# notational short-cut
				$mvSet = $mvtbl->{$mvvrmp->{$varName}};
				$mvType= $mvtypcd->{$mvvrmp->{$varName}};
				# print $varName,"(mv type=",$mvType,")\n";
				# deal with vars with miss.vl. only
				if ($mvType > 0) {
					# non-range-type missing values (up to 3)
					$output->print("\t\t<invalrng>\n");
					for (my $k=0; $k <=$#{$mvSet}; $k++){
						$output->print("\t\t\t<item VALUE=\"",$mvSet->[$k][0],"\"/>\n");
					}
					$output->print("\t\t</invalrng>\n");
					
				} elsif ($mvType == -2 ) {
					#range-type 1 missing values
					$output->print("\t\t<invalrng>\n");
					$output->print("\t\t\t<range");
					if ($mvSet->[0][0] ne 'LOWEST'){
						$output->print(" min=\"",$mvSet->[0][0],"\"");
						$minThrshld = $mvSet->[0][0];
						$msvlRngType++;
					}
					if ($mvSet->[0][1] ne 'HIGHEST') {
						$output->print(" max=\"",$mvSet->[0][1],"\"");
						$maxThrshld = $mvSet->[0][1];
						$msvlRngType++;
					}
					$output->print("/>\n");
					$output->print("\t\t</invalrng>\n");
				} elsif ($mvType == -3 ) {
					#range-type: 2 missing values
					
					$output->print("\t\t<invalrng>\n");
					
					# check the length of the first element:
					# one range(length = 1) or one item (length=0)
					
					if ($#{$mvSet->[0]} == 0){
						# first one is item and second one is range
						$output->print("\t\t\t<range");
						if ($mvSet->[1][0] ne 'LOWEST') {
							$output->print(" min=\"",$mvSet->[1][0],"\"");
							$minThrshld = $mvSet->[1][0];
							$msvlRngType++;
						}
						if ($mvSet->[1][1] ne 'HIGHEST') {
							$output->print(" max=\"",$mvSet->[1][1],"\"");
							$maxThrshld = $mvSet->[1][1];
							$msvlRngType++;
						}
						$output->print("/>\n");
						$output->print("\t\t\t<item VALUE=\"",$mvSet->[0][0],"\"/>\n");
					} elsif ($#{$mvSet->[0]} == 1) {
						$output->print("\t\t\t<range");
						if ($mvSet->[0][0] ne 'LOWEST') {
							$output->print(" min=\"",$mvSet->[0][0],"\"");
							$minThrshld = $mvSet->[0][0];
							$msvlRngType++;
						}
						if ($mvSet->[0][1] ne 'HIGHEST') {
							$output->print(" max=\"",$mvSet->[0][1],"\"");
							$maxThrshld = $mvSet->[0][1];
							$msvlRngType++;
						}
						$output->print("/>\n");
						$output->print("\t\t\t<item VALUE=\"",$mvSet->[1][0],"\"/>\n");
					}
					$output->print("\t\t</invalrng>\n");
				}
			}
		}
		#if (exists($self->{_dcmlVarTbl}->{$varName})) {
			# sumStat: $self->{_sumStat}
		#print "j=", $j, "\t", $varName, "\n";
		if (scalar(@{$self->{_sumStat}->{$varName}})>3 ){
			for (my $l=0;$l<@sumStatse;$l++){
				$output->print("\t\t<sumStat type=\"",$sumStatse[$l],"\">",$self->{_sumStat}->{$varName}->[$l],"</sumStat>\n");
			}
		} else {
			for (my $l=0;$l<@sumStatseC;$l++){
				$output->print("\t\t<sumStat type=\"",$sumStatseC[$l],"\">",$self->{_sumStat}->{$varName}->[$l],"</sumStat>\n");
			}
		}
		#} els
		if (exists($self->{_valVrMpTbl}->{$varName})){
			if (exists($self->{_valLblTbl}->{$self->{_valVrMpTbl}->{$varName}}) && ($self->{_valLblTbl}->{$self->{_valVrMpTbl}->{$varName}}) ){
				#print "xml: current var=",$varName,"\n";
				# catStat: value/value labels
				# data structure 
				# [0:value, 1:label, 2:freq, 3:MV(yes='y')
				# 'PARTY3' =>[[1,'REPUBLICAN',undef,undef], [2,'DEMOCRATIC',undef,undef],..]
				my $mvtoken='';
				my $vllblSet = $self->{_valLblTbl}->{$self->{_valVrMpTbl}->{$varName}};
				#print Dumper($vllblSet);
				my $varFmt = $self->{_varFormat}->{$varName};
				for (my $k=0; $k <= $#{$vllblSet}; $k++) {
					#print "varName=",$self->{_valVrMpTbl}->{$varName},"\n";
					#print $k,"\t", scalar(@{$vllblSet->[$k]}), "\n";
					if ( ($vllblSet->[$k][3]) && ($vllblSet->[$k][3] eq 'y')){
						$mvtoken=" missing=\"Y\"";
					}
					$output->print("\t\t<catgry",$mvtoken,">\n");
					
					if (($self->{_fileDscr}->{fileType} =~ 'spss') || exists($self->{_prcssOptn}->{SPSFILE})) {
						if ($self->{_varFormat}->{$varName} eq 'TIME'){
							my @timedata= split(/ /,$vllblSet->[$k][0]);
							$output->print("\t\t\t<catValu>",$timedata[1],"</catValu>\n");
						} else {
							if ($varTypeSet->[$j]==0){
								$output->print("\t\t\t<catValu>",encode_entities_numeric($vllblSet->[$k][0]),"</catValu>\n");
							} else {
								$output->print("\t\t\t<catValu>",$vllblSet->[$k][0],"</catValu>\n");
							}
						}
					} else {
						if ($varTypeSet->[$j]==0){
							$output->print("\t\t\t<catValu>",encode_entities_numeric($vllblSet->[$k][0]),"</catValu>\n");
						} else {
							$output->print("\t\t\t<catValu>",$vllblSet->[$k][0],"</catValu>\n");
						}
					}
					
					if ($vllblSet->[$k][1]){
						$output->print("\t\t\t<labl level=\"CATEGORY\">",encode_entities_numeric($vllblSet->[$k][1]),"</labl>\n");
					}
					$output->print("\t\t\t<catStat>",$vllblSet->[$k][2],"</catStat>\n");
					$output->print("\t\t</catgry>\n");
					$mvtoken='';
				}
			}
		}
		# varFormat tag
		if ($self->{_noCharVar} >0) {
			$attVarFrmtType=&getVarFrmtType($self,$varName);
		} else {
			$attVarFrmtType='numeric';
		}
		
		if (($self->{_fileDscr}->{fileType} =~ 'spss') || exists($self->{_prcssOptn}->{SPSFILE})) {
			if ( ($self->{_varFormat}->{$varName} eq 'A') || ($self->{_varFormat}->{$varName} eq 'F') || ($self->{_varFormat}->{$varName} eq 'I')){
				$output->print(join('',("\t\t<varFormat type=\"",$attVarFrmtType,"\"/>\n")));
			} else {
				# print like this:
				# <varFormat type='numeric' schema='SPSS' formatname='ADATE' category=date />

				$output->print(join('',("\t\t<varFormat type=\"",$attVarFrmtType,"\" schema=\"SPSS\" formatname=\"",$self->{_formatName}->{$varName},"\" category=\"", $SPSS_FRMT_CATGRY_TABLE->{ $self->{_varFormat}->{$varName} },"\"/>\n")));
				
			}
		} elsif ($self->{_fileDscr}->{fileType} =~ /stata/){
			if ($self->{fmtlst}->[$j]=~/^[%][d]/) {
				$output->print(join('',("\t\t<varFormat type=\"",$attVarFrmtType,"\" schema=\"other\" formatname=\"",$self->{fmtlst}->[$j],"\" category=\"date\"/>\n")));
			} else {
				$output->print(join('',("\t\t<varFormat type=\"",$attVarFrmtType,"\"/>\n")));
			}
		} else {
			$output->print(join('',("\t\t<varFormat type=\"",$attVarFrmtType,"\"/>\n")));
		}
		
		# notes: UNF
		$output->print("\t\t<notes subject=\"Universal Numeric Fingerprint\" level=\"variable\" source=\"archive\" type=\"VDC:UNF\">",$self->{_UNF}->[$j],"</notes>\n");
		
		# var tag: closing
		$output->print("\t</var>\n");

	} # var-wise

#	$output->print("</dataDscr>\n");
	
}

### ///////////////////////////////////////////////////////////////

sub mergeSubDDIs{
	#&VDC::DSB::mergeSubDDIs(DDI=>$ddi, DDI3FILE=>ddiSec3, DDIFILES=>$ddifile);
	my $optns = {@_};
	# encoding etc
	# DDI versons

	#$self->{_prcssOptn}->{MSVL} = exists($optns->{MSVL}) ? $optns->{MSVL}    : $MSVL;
	my $MSVL = exists($optns->{MSVL}) ? $optns->{MSVL} : 1;
	
	#my $output=$optns->{DDIFILE};
	my $ddifiles = $optns->{DDIFILES};
	my $ddi      = $optns->{DDI};
	my $ddiSec3  = $optns->{DDI3FILE};
	
	my $output = new IO::File("> $ddi");

	my $encoding = "ISO-8859-1" unless (exists($optns->{ENCODING}) && defined($optns->{ENCODING}));
	$output->print("<?xml version=\"1.0\"");
	if ($encoding) {
	  $output->print(" encoding=\"$encoding\"");
	}
	$output->print("?>\n");
	
	# DOCTYPE
	my $DDIverNo = "1-02-1" unless (exists($optns->{DDIVERNO}) && defined($optns->{DDIVERNO}));
	
	$output->print("<!DOCTYPE codeBook SYSTEM \"http://www.icpsr.umich.edu/DDI/Version$DDIverNo.dtd\">\n");
	
	# "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
	# "<!DOCTYPE codeBook SYSTEM \"http://www.icpsr.umich.edu/DDI/Version1-02-1.dtd\">\n"
	# DDI: Sec. 1 to Sec. 2
	$output->print("<codeBook>\n<docDscr/>\n<stdyDscr>\n\t<citation>\n\t\t<titlStmt>\n\t\t\t<titl/>\n\t\t\t<IDNo agency=\"\"/>\n\t\t</titlStmt>\n\t</citation>\n</stdyDscr>\n");
	
	# DDI: Sec. 3
	open(SECT, $ddiSec3) or die "cannot open $ddiSec3";
	while(<SECT>){
		$output->print( $_ );
	}
	close(SECT);
	# DDI: Sec. 4
	$output->print("<dataDscr>\n");
	
	for (my $i=0; $i<@{$ddifiles};$i++){
		open(SECF, ($ddifiles->[$i])) or die "cannot open $ddifiles->[$i] \n";
		while(<SECF>){
			$output->print( $_ );
		}
		close(SECF);
	}
	$output->print("</dataDscr>\n");
	# Sec. 5
	$output->print("<otherMat type=\"\" level=\"study\"  URI=\"\">\n\t<labl></labl>\n\t<txt></txt>\n\t<notes></notes>\n</otherMat>\n");
	# end
	$output->print("</codeBook>\n");
}

sub validateDDI{
	my $ddi = shift;
	my $returncode;
	my $MINSIZE = 10;
		my $ddisize =  -s $ddi;
		if ($ddisize > $MINSIZE) {
			$returncode=0;
		} elsif (($ddisize <=$MINSIZE) && ($ddisize >0) ) {
			$returncode=1;
		} elsif (-z $ddi ) {
			$returncode=2;
		} elsif (! -e $ddi) {
			$returncode=3;
		}
		# validity check
		$ENV{'XML_CATALOG_FILES'}= "/usr/local/VDC/catalog/DDIxmlcatalog";

		my $errorcount;
		if ($returncode==0) {
			open(PH, "xmllint  --catalogs --valid --noout $ddi 2>&1 | grep -c 'error' |");
			while (<PH>) {
				$errorcount=$_;
				# print $FH "error counts=", $errorcount;
			}
			close(PH);
			$errorcount +=0;
			if ($errorcount) {print STDERR "DDI validation:error count=",$errorcount,"\n";}
		}
		if ($errorcount>0){ 
			$returncode=9;
		}
	return $returncode;
}


# //////////////////////////////////////////////////////////
# The following methods may be overriden by a derived class
# //////////////////////////////////////////////////////////
sub getVarTypeSet{
	my $self = shift;
	my $vartype = $self->{_varType};
	my $varname = $self->{_varNameA};
	my $dcml    = $self->{_dcmlVarTbl};
	my $novar   = $self->{_fileDscr}->{varQnty};
	my $vllbmap = $self->{_valVrMpTbl};

	my $varcode=[];

	for (my $i=0;$i<$novar;$i++){
		if ($vartype->{$varname->[$i]}>0){
			# char var
			$varcode->[$i]=0 ;
		} elsif (exists($dcml->{$varname->[$i]})) {
			# numeric continuous var
			
			if (!exists($vllbmap->{$varname->[$i]})){
				$varcode->[$i]=2 ;
			} else {
				$varcode->[$i]=1 ;
			}
			
		} else{
			# numeric discrete var
			$varcode->[$i]=1 ;
			if (($self->{_fileDscr}->{fileType} =~ 'spss') || exists($self->{_prcssOptn}->{SPSFILE})) {
				my $vCatgry = $SPSS_FRMT_CATGRY_TABLE->{ $self->{_varFormat}->{$varname->[$i]} };
				if (($self->{_varFormat}->{$varname->[$i]} eq 'WKDAY')||($self->{_varFormat}->{$varname->[$i]} eq 'MONTH')) {
					# vars in spss WKDAY and MONTH are dumped as string variables
					$varcode->[$i]=0;
				}
			}
			if (($self->{_fileDscr}->{fileType} =~ '-por') || ($self->{_fileDscr}->{fileType} =~ '-sav')){
				if (($self->{_varFormat}->{$varname->[$i]} eq 'WKDAY')||($self->{_varFormat}->{$varname->[$i]} eq 'MONTH')) {
					# vars in spss WKDAY and MONTH are dumped as string variables
					$varcode->[$i]=0;
				}
				my $vCatgry = $SPSS_FRMT_CATGRY_TABLE->{ $self->{_varFormat}->{$varname->[$i]} };
				if (($vCatgry eq 'date') || ($vCatgry eq 'time')){
					$varcode->[$i]=0;
				}
			}

		}
	}
	return $varcode;
}

sub writeVarLocMapFile{
	# dump a location table of variables to an external file
	my $self = shift;
	my $optns = { @_ };
	my $vlmfile;
	my $DEBUG;
	my $FH= $self->{_prcssOptn}->{LOGFILE};

	my $base     = $self->{_prcssOptn}->{SOURCEFILE};
	my $filenmbr = $self->{_prcssOptn}->{FILENO};
	my $varNameSet = $self->getVarNameSet();

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
	
	print VLM "\n";
	print "\n";
	# @{$self->{_fileDscr}->{CARDORDER}} can be sorted by card number in ascending order 
	# however, the order of variable naming (listing, i.e., v001-v100) 
	# must be much more important than their physical order in a data file, and
	# not sorted here
	
	my $filetype='';
 	#print join("\t",qw(0[ID] 1[card] 2[var] 3[cls] 4[cle] 5[fmt] 6[wdth] 7[dcml])), "\n";
 	
 	my $placehldr='';
 	my $varNameL;
	for (my $j=0; $j<@{$self->{_varNameA}}; $j++){
		
		$varcntr++;
		my $currentvar= $self->{_varNameA}->[$j];
		
		$varNameL = $varNameSet->[$j];
		if (exists($self->{_charVarTbl}->{$currentvar})){
			$filetype='A';
		} elsif (exists($self->{_dcmlVarTbl}->{$currentvar})) {
			$filetype='F';
		} else {
			if (($self->{_fileDscr}->{fileType} =~ 'spss') || exists($self->{_prcssOptn}->{SPSFILE})) {
				my $vCatgry = $SPSS_FRMT_CATGRY_TABLE->{ $self->{_varFormat}->{$currentvar} };
				# date/time-type are now string-type (colon/hyphen are included)
				if ($vCatgry eq 'date') {
				
					$filetype='A';
				} elsif ($vCatgry eq 'time') {
					$filetype='A';
				} else {
					$filetype='I';
				}
			} else {
				$filetype='I';
			}
		}
		$varid = $self->getVarID($filenmbr, $varcntr);
		
		print VLM join("\t",($varid, 1, $varNameL, $varcntr,$placehldr,$filetype, $placehldr, $placehldr)),"\n";
		print $FH join("\t",($varid, 1, $varNameL, $varcntr,$placehldr,$filetype, $placehldr, $placehldr)),"\n";
		$filetype='';
		
	}
	
	close(VLM);
	$self->{_prcssOptn}->{VLMFILE}=$optns->{VLMFILE};
	return 0;
}

# /////////////////////////////////////////////////////////////////////////////
sub getOSbyteOrder {
	# 0. check the endiannes of the platform
	my $DEBUG;
	my $pltfrmEndian=0;
	if ( unpack("v", pack("s", 1)) == 1 ){
		$pltfrmEndian = 2;
		print "The platform is little:$pltfrmEndian\n" if $DEBUG;
	} elsif ( unpack("n", pack("s", 1)) == 1 ) {
		$pltfrmEndian = 1;
		print "The platform is big:$pltfrmEndian\n" if $DEBUG;
	} else {
		print "This platform is neither big nor little endian.\n" if $DEBUG;
	}
	
	return $pltfrmEndian;
}

sub unpackdata {
	my $self = shift;
	#print "before self=\n", Dumper($self);
	my $datafile    = $self->{_prcssOptn}->{SOURCEFILE}; 
	my $tabdatatfile= $self->{_prcssOptn}->{DLMTDDATA};
	my $revisedfield=[];

	push @$revisedfield, 'DLMTDDATA';
	my $DBG=0;
	my $FH= $self->{_prcssOptn}->{LOGFILE};
	my @tmpdata=();
	
	my $ccrdno=1;
	my $chrlst=$self->{_charVarTbl};
	my $dcmlst=$self->{_dcmlVarTbl};
	
	my ($datacard,  @tmpz, $obs);
	my $nocards=$self->{_fileDscr}->{RECORDS};
	my $offsethsh = $self->{_location};
	my $varnamelst = {};
	my $dcmlpstn = {};
	while ( my ($ky, $vl) = each(%$offsethsh)) {
		$varnamelst->{$ky}=[];
		$dcmlpstn->{$ky} = [];
		for(my $ii=0;$ii<@$vl;$ii++){
			push @{$varnamelst->{$ky}}, $vl->[$ii]->[0];
			if ($vl->[$ii]->[5]){
				$dcmlpstn->{ $vl->[$ii]->[0] }=[$ky, $ii];
			}
		}
	}
	
	my $lncntr = 0;
	my @tmp =();
	my @tmpmtrx =();
	# hash that stores decimal variables whose data had been already decimalized
	my %deldcml;
	# number of lines to be printed for debugging
	my $nolines=5;
	
	# cut-out data will be externally stored anyway 
	# before whether the decimal update is determined
	open(WDTA, ">$tabdatatfile") or die "errcd(I025): cannot open the tab-delimited data file(tabdatatfile):$!";
	open (FILE, $datafile) or die "errcd(I023): cannot open the data file($datafile): $!\n";
	while (<FILE>) {
		chomp $_;
		$datacard=$_;
		# pstn is the current card position within a set of cards
		my $pstn0 = $lncntr%$nocards;
		my $pstn = $pstn0+1;
		# note: add 1 to the observation counter if $lncntr is the first card
		if ($pstn == 1) {$obs++;}
		#print $FH "line=", $lncntr, "\tpstn=", $pstn,"\tscalar=", scalar(@{$offsethsh->{$pstn}}), "\n";
		
		if ( exists($offsethsh->{$pstn}) && (scalar(@{$offsethsh->{$pstn}}))){
			# the above condition is necessary to deal with empty cards
			for (my $jj=0; $jj<=$#{$offsethsh->{$pstn}}; $jj++) {
				push @tmp, substr($datacard, ($offsethsh->{$pstn}->[$jj]->[1]-1),$offsethsh->{$pstn}->[$jj]->[4] );
			}
			print $FH join("\t", @tmp), "\n";
			# if there is no character variable, numeric cast is applied to all data
			# no character var included
			unless($self->{_noCharVar}){
				# decimal check
				if ($self->{_noDcmlVar}) {
					for (my $k=0; $k< @tmp;$k++){
						my $vn = $varnamelst->{$pstn}->[$k];
							
							if ( exists($dcmlst->{$vn}) && ($tmp[$k]=~ m/[.][0-9]/) ) {
								# decimal point found, i.e., already decimalized
								# no decimalization is necessary
								unless (exists($deldcml{$vn})) {
									# remove this var from the hash
									$deldcml{$vn} = 1;
								}
							}
					} # loop $k
				} # end of decimal adj
				# numeric cast
				@tmp= map {  0+$_ } @tmp;
			 # end: no-character-var-included case
			} else {
			# character-var-included case
				for (my $k=0; $k< @tmp;$k++){
					my $vn = $varnamelst->{$pstn}->[$k];
					# date/time conversion check
					
					my $vf = $self->{_varFormat}->{ $vn };
					if (($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'date') || ($SPSS_FRMT_CATGRY_TABLE->{$vf} eq 'time')) {
						# date conversion
						print $FH "$obs-th case $k-th var(before ISO date conversion):",$tmp[$k],"\n" if $DBG;
						$tmp[$k]=&VDC::DSB::Ingest::StatData::convertDateTimeData($tmp[$k],$vf);
						print $FH "$obs-th case $k-th var(after ISO date conversion):",$tmp[$k],"\n" if $DBG;
					}
					
					# decimal check
					if ($self->{_noDcmlVar}) {
						if ( exists($dcmlst->{$vn}) && ($tmp[$k]=~ m/[.][0-9]/) ){
							unless (exists($deldcml{$vn})) {
								# remove this var from the hash
								$deldcml{$vn} = 1;
							}
						}
					} # end of decimal adj
					# numeric cast if var is not character-type
					if (!exists($chrlst->{$vn})){
						$tmp[$k] = 0+$tmp[$k];
					}
				} # loop $k
				# end: character-var-included case
			}
			@tmpz = (@tmpz,@tmp);
			@tmp=();
		}
		
		# at the last card
		if ($pstn == $nocards) {
			print WDTA join("\t", @tmpz),"\n";
			#push @tmpmtrx, [@tmpz];
			@tmpz=();
		}
		$lncntr ++;
	}
	close (FILE);
	close(WDTA);
	print $FH "\n", $lncntr, " records processed.\n";
	$self->{_fileDscr}->{caseQnty}=$lncntr;
	

	my $dcmloffyes = scalar(keys(%deldcml));
	if ($DBG) {
		if ($dcmloffyes) {
			print $FH "\n\nthere is a variable whose decimalization value must be deleted:\n";
			for my $key (sort {$a <=> $b} keys %deldcml){print $FH "\tkey=", $key, "\t value=",$deldcml{$key},"\n";}
		} 
	}
	unless ($dcmloffyes) {print $FH "This data file does not need to update decimalization values in metadata\n";}
	my $dcmlstadj={};
	my $noDcmlVaradj =$self->{_noDcmlVar};
	if ($dcmloffyes){
		push @$revisedfield, "_noDcmlVar";
		push @$revisedfield, "_dcmlVarTbl";
		push @$revisedfield, "_location";

		while (my ($ky, $vl) = each (%deldcml)){
			if (exists($dcmlst->{$ky})){
				#delete($dcmlst->{$ky});
				$noDcmlVaradj--;
				#
				# check card and location array
				# {'_location'} => {'1' => 
				#		[
				#			['caseid1',1,8,'a',8,undef],
				#			['state',1,1,undef,1,undef],
				#				...,
				#			[ 'length1', 37, 39, undef, 3, 1],
				#		],
				#					'2' => [
				#			[], [], [], ..., 
				#		],
				# 6-th element should be updated
				#
				my $pstninf= $dcmlpstn->{$ky};
				$offsethsh->{$pstninf->[0]}->[$pstninf->[1]][5]=undef;
				# $self->{_location}->{$pstninf->[0]}->[$pstninf->[1]][5]=undef;
			} else {
				$dcmlstadj->{$ky}=$vl;
			}
		}
		print $FH "decimal var table after adjustment\n",Dumper($dcmlst),"\n";
	}
	
	$nolines=5;
	
	# note: it was found that when the last element of an array is empty, 
	# the following join-split operations loose the last element [not 5 but 4]
	#	my @x=(1, 2, 3, 4,"");
	#	my @y= split(/\t/, join("\t", @x));
	#	Therefore, the concatenation by a tab must be done 
	#	after the decimal operation is completed;
	#	otherwise, such an empty last element will be lost during the join-split operation
	if ($self->{_noDcmlVar}>0) {
	print $FH "no of decimalization-required vars=",$self->{_noDcmlVar},"\n";
		my $tmptabfile="tmptabfile";
		print $FH "data matrix (decimal-adj-necessary case):\n";
		open(WDTA, ">$tmptabfile") or die "errcd(I026): cannot open the temp tab-delimited data file(tmptabfile):$!";
		open(RDTA, $tabdatatfile) or die "errcd(I025): cannot open the tab-delimited data file(tabdatatfile):$!";
		my @tmptabdata=();
		my $lncntrd=1;
		my $indxvar;
		my $dcmlvalue;
		while (<RDTA>) {
			chomp $_;
			@tmptabdata = split("\t", $_);
			for (my $j=0;$j<= $#tmptabdata ;$j++) {
				$indxvar = $self->{_varNameA}->[$j]; # $self->{_varNameA}->[$j] = var_name
				if (exists($dcmlst->{ $indxvar })){
					$dcmlvalue=$dcmlst->{ $indxvar } ;
					$tmptabdata[$j] = $tmptabdata[$j]*(10**(-1*$dcmlvalue));
					undef($dcmlvalue);
				}
			}
				if (($DBG)&& ($lncntrd<$nolines )) {print $FH "(",$lncntrd,")\t", join("\t", @tmptabdata), "\n";}
			print WDTA join("\t", @tmptabdata),"\n";
			$lncntrd++;
		}
		close(RDTA);
		close(WDTA);
		copy($tmptabfile, $tabdatatfile);
		unlink($tmptabfile);
		print $FH "End of the decimal-adj-necessary case\n";
	}
	
	if (scalar(@{$revisedfield})){
		$self->{_noDcmlVar_adj}=$noDcmlVaradj;
		$self->{_dcmlVarTbl_adj}=$dcmlstadj;
		$self->{_location}=$offsethsh;
	}
} # end of sub unpackdata




sub findGregorianDate {
	my $diff = shift;
	my $dateTimeType=shift;
	my $DEBUG=0;
	my $rTime = [];
	my $rDate = [];
	print $diff . ' seconds from the midnight of /10/14/1582' . "\n" if $DEBUG;

	# calculate second, minute, hours first

	my $sec = $diff % 60;
	#print 'seconds=' . $sec . "\n"  if $DEBUG;
	unshift @{$rTime}, (sprintf "%02u", $sec);
	
	$diff = ($diff - $sec) /60;
	my $min = $diff % 60;
	#print 'min=' . $min . "\n" if $DEBUG;
	unshift @{$rTime}, (sprintf "%02u", $min);

	$diff = ($diff - $min) /60;
	my $hours = $diff %  24;
	#print 'hours=' . $hours . "\n" if $DEBUG;
	unshift @{$rTime}, (sprintf "%02u", $hours);
	print "(H,M,S)=(" . join(',', ($hours, $min, $sec)) . ")\n" if $DEBUG;
	# calculate the number of days since /10/14/1582
	$diff = ($diff - $hours) /24;
	print $diff . ' days have passed since the midnight of 10/14/1582' . "\n" if $DEBUG;
	
	if ($dateTimeType eq 'JT'){
		my $rTm = join(':', @{$rTime});
		my $rtrnS = int($diff) . " " . $rTm;
		return $rtrnS;
	}
	
	my $daysWc;
	my $whch4Cu;
	my $yearQ=0;
	my $days16c;
	
	my $monthY;
	my $dayM;
	my $yrsInThisC=0;

	if ($diff >= 6288 ){
		# after 1600-1-1 including
		# if zero, 1st day
		# 4-th century adj
		print "days after 12/31/1599=" . ($diff - 6287) . "\n" if $DEBUG;
		$days16c = ($diff-6287) % 146097;
		$yearQ = int (($diff-6287)/146097) ;
		print "how many 4-Century unit=" .$yearQ . "\n" if $DEBUG;
		print "days in this Cenctury unit=" .$days16c . "\n" if $DEBUG;
		if ($days16c !=0){
			if ($days16c <=36525) {
				$whch4Cu = 0;
				$daysWc = $days16c;
			}elsif (($days16c > 36525) && ($days16c <=73049)) {
				$whch4Cu = 1;
				$daysWc = $days16c - 36525;
			} elsif (($days16c > 73049) && ($days16c <=109573)) {
				$whch4Cu = 2;
				$daysWc = $days16c - 73049;
			} elsif ($days16c >109573 ) {
				$whch4Cu = 3;
				$daysWc = $days16c - 109573;
			}
		} else {
			# the last day of this 4-Century unit such as 1999-12-31
			$whch4Cu=3;
			#$daysWc= 36524;
			$yearQ= $yearQ-1;
			$yrsInThisC=99;
			$monthY=12;
			$dayM = 31;
			print "this date is the late day of this 4-century unit (=",$yearQ,")\n" if $DEBUG;
			goto laststage;
		}
	} else {
		$whch4Cu = -1;
		$daysWc = $diff + 30237;
		print "date before 1600-1-1 is found\n"  if $DEBUG;
	}
	
	# determine the year 
	if ($DEBUG){
		print "which Century within [0..3]=" .  $whch4Cu . "-th\n" if $DEBUG;
		print "which Century=" . (4*$yearQ + $whch4Cu +16) . "XX\n" if $DEBUG;
		print "days within this 4-Century unit=" .$daysWc . "\n" if $DEBUG;
	}
	
	my $remain=$daysWc;
	my $daysWyear=365;
	
	# non-4th Cencutry leap-year correction
	if ($whch4Cu == 0) {
		$daysWyear++;
		print "4-th-Century-Leap-year adjustment is applied.\n" if $DEBUG;
	}
	until ($remain <= $daysWyear){
		my $r;
		if ($yrsInThisC > 0){
			$r = $yrsInThisC % 4;
			if ($r == 0) {
				$daysWyear = 366;
			} else {
				$daysWyear = 365;
			}
		}
		if ($DEBUG){
			print  "within unitl: year="  . $yrsInThisC . "\tremaing days=" .$remain. "\t" .$daysWyear. " days to be subtracted\n" ;
		}
		# subtract this year
		$remain -= $daysWyear;
		if ($yrsInThisC == 0) {
			# 4-th-century-specific leap-year adj
			$daysWyear =365;
		} else {
			if ($r == 0) {
				# reset to the non-leap-year value
				$daysWyear=365;
			}
		}
		if ($remain==0){
			print "\nThe date is the end of this year\n" if $DEBUG;
			last;
		}
		# add year-counter
		$yrsInThisC++;
		if ($DEBUG){
			print  "\tnext: year="  . $yrsInThisC . "\tremaing days=" .$remain. "\tevalution value=" .$daysWyear. "\n" ;
		}

	}
	print "\nafter until: year=" .$yrsInThisC . "\tremaining days=" . $remain ."\n" if $DEBUG;
	my $rm = $yrsInThisC % 4;
	my $leapYear;
	if ($rm) {
		$leapYear ='N';
	} else {
		if (($yrsInThisC==0) && ($whch4Cu!=0)){
			$leapYear='N';
		} else {
			$leapYear ='Y';
		}
	}
	print "\nLeap Year (yes:Y, no:N)=" . $leapYear . "\n" if $DEBUG;
	print "years Winthin this Century=" . $yrsInThisC ."\n" if $DEBUG;
	print "how many Days in this year =" . $remain ."\n\n" if $DEBUG;
	if ($remain == 0){
		$monthY=12;
		$dayM=31;
	} else {
		# determine the day within the year
		my $daysFeb = $leapYear eq 'Y' ?  29 : 28;
		my @daysM = (31,$daysFeb, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 );
		my $mnth =0;

		until($remain <= $daysM[$mnth]){
		} continue {
			print 'remain(b)=' . $remain . "\t" if $DEBUG;
			$remain = $remain - $daysM[$mnth];
			print 'subtracted=' . $daysM[$mnth] . "\t" if $DEBUG;
			print 'remain(a)=' . $remain . "\n" if $DEBUG;
			if (($remain == 0) || ($mnth == 11)) {
				last;
			} else {
				$mnth++;
			}
		}
		print "month index after until=". $mnth . "\n" if $DEBUG;
		$monthY = $mnth +1;
		$dayM = $remain;
		print  $remain . ' days have already passed ' . $monthY .'-th month' . "\n" if $DEBUG;
	}
	
	print 'month=' . $monthY  . ', day=' . $dayM . "\n" if $DEBUG;
	laststage: my $yearC= (4*$yearQ + $whch4Cu +16)*100 + $yrsInThisC;
	$monthY= sprintf "%02u", $monthY;
	$dayM  = sprintf "%02u", $dayM;
	unshift @{$rDate}, $dayM;
	unshift @{$rDate}, $monthY;
	unshift @{$rDate}, int($yearC);
	print 'date to be returned=' . join('|',@{$rDate}) . "\n" if $DEBUG;
	my $rTm = join(':', @{$rTime});
	my $rDt = join('-', @{$rDate});
	print 'date=', $rDt,"\ttime=",$rTm,"\n"  if $DEBUG;
	my $rtrnS;
	if ($dateTimeType eq 'T'){
		$rtrnS=$rTm; 
	} elsif ($dateTimeType eq 'D'){
		$rtrnS=$rDt;
	} else {
		$rtrnS=join('',($rDt,' ',$rTm))
	}
	return $rtrnS;
}





sub separateTypeFmt{
	my $rawFmt=shift;
	$rawFmt =~ /([a-zA-Z]+)([0-9.]+)/;
	#print join('-',($rawFmt, $1, $2)), "\n";
	if (!exists($SPSS_FRMT_CATGRY_TABLE->{uc($1)})) {
		print "some unknown type is found:", $1,"\n";
	}
	#return join('-', ($1, $2));
	return $1;
}
sub convertDateTimeData{
	my $rawData=shift;
	my $fmt=shift;
	my $parsedData; #=$rawData;
	my $DBG=0;
	print "raw data=",$rawData, "\t", $fmt, "\t" if $DBG;
	
	if ($fmt eq 'DATE') {
		# FMT_NO=20
		my ($d, $m, $y) = split(/-/, $rawData);
		
		if (length($y) ==2 ) {
			my $cn;
			if ($y > ($CURRENT_YEAR_S +30)){
				$cn = $CURRENT_C -1;
			} else {
				$cn = $CURRENT_C;
			}
			$y = $cn . $y;
		}
		$parsedData = join('-',($y,$MNTH2N->{$m}, $d));
	} elsif ($fmt eq 'TIME') {
		# FMT_NO=21
		my @tmp = split(//, $rawData);
		if ( $tmp[0] eq ' ') {
			print "[",$tmp[0], "]\n" if $DBG;
		#if (substr($rawData, 0,1) eq ' '){
			$rawData = '0' . substr($rawData, 1);
		}
		my $len= length($rawData);
		if ( $len== 5){
			$parsedData = $rawData . ":00";
		} elsif ($len == 8){
			$parsedData = $rawData;
		} elsif ($len == 11){
			$parsedData = (split(/\./,$rawData))[0];
		}
	} elsif ($fmt eq 'DATETIME') {
		# FMT_NO=22
		my ($dy, $tm) = split(/ /, $rawData);
		my ($d, $m, $y) = split(/-/, $dy);
		my $len= length($tm);
		my $tmx;
		if ( $len== 5){
			$tmx = $tm . ":00";
		} elsif ($len == 8){
			$tmx = $tm;
		} elsif ($len == 11){
			$tmx = (split(/\./,$tm))[0];
		}
		
		$parsedData = join('-', ($y,$MNTH2N->{$m}, $d)) . ' ' . $tmx;
	} elsif ($fmt eq 'ADATE') {
		# FMT_NO=23
		my ($m, $d, $y) = split(/\//, $rawData);
		
		if (length($y) ==2 ) {
			my $cn;
			if ($y > ($CURRENT_YEAR_S +30)){
				$cn = $CURRENT_C -1;
			} else {
				$cn = $CURRENT_C;
			}
			$y = $cn . $y;
		}
		
		$parsedData = join('-', ($y, $m, $d));
	} elsif ($fmt eq 'JDATE') {
		# FMT_NO=24
		my $len = length($rawData);
		my ($y,$d);
		if ($len == 5) {
			$y = substr($rawData, 0,2);
			my $cn;
			if ($y > ($CURRENT_YEAR_S +30)){
				$cn = $CURRENT_C -1;
			} else {
				$cn = $CURRENT_C;
			}
			$y = $cn . $y;
			$d = substr($rawData, 2);
		} else {
			$y = substr($rawData, 0,4);
			$d = substr($rawData, 4);
		}
		my $md = &findMonthDayInYear($d,$y);
		$parsedData = $y .'-'. $md;
	} elsif ($fmt eq 'DTIME') {
		# FMT_NO=25
		my ($dy, $tm) = split(/ /, $rawData);
		my $len= length($tm);
		my $tmx;
		if ( $len== 5){
			$tmx = $tm . ":00";
		} elsif ($len == 8){
			$tmx = $tm;
		} elsif ($len == 11){
			$tmx = (split(/\./,$tm))[0];
		}
		
		$parsedData = $dy .' '. $tmx;
	} elsif ($fmt eq 'MOYR') {
		# FMT_NO=28
		my ($m, $y) = split(/ /, $rawData);
		if (length($y) ==2 ) {
			my $cn;
			if ($y > ($CURRENT_YEAR_S +30)){
				$cn = $CURRENT_C -1;
			} else {
				$cn = $CURRENT_C;
			}
			$y = $cn . $y;
		}
		my $d = '01';
		$parsedData = join('-', ($y, $MNTH2N->{$m}, $d));
		
		
	} elsif ($fmt eq 'QYR') {
		# FMT_NO=29
		my ($q,$Q,$y) = split(/ /, $rawData);
		if (length($y) ==2 ) {
			my $cn;
			if ($y > ($CURRENT_YEAR_S +30)){
				$cn = $CURRENT_C -1;
			} else {
				$cn = $CURRENT_C;
			}
			$y = $cn . $y;
		}
		my $d = '01';
		$parsedData = join('-', ($y, $QN2MNTH->{$q}, $d));
	} elsif ($fmt eq 'WKYR') {
		# FMT_NO=30
		my ($w,$W,$y) = split(/ /, $rawData);
		if (length($y) ==2 ) {
			my $cn;
			if ($y > ($CURRENT_YEAR_S +30)){
				$cn = $CURRENT_C -1;
			} else {
				$cn = $CURRENT_C;
			}
			$y = $cn . $y;
		}
		my $dys= 7*($w-1) +1;
		print "w=",$w, "\tdys=",$dys,"\n" if $DBG;
		my $md = &findMonthDayInYear($dys,$y);
		
		$parsedData = $y .'-'. $md;
	} elsif ($fmt eq 'EDATE') {
		# FMT_NO=38
		
		my ($d, $m, $y) = split(/\./, $rawData);
		
		if (length($y) ==2 ) {
			my $cn;
			if ($y > ($CURRENT_YEAR_S +30)){
				$cn = $CURRENT_C -1;
			} else {
				$cn = $CURRENT_C;
			}
			$y = $cn . $y;
		}
		
		$parsedData = join('-', ($y, $m, $d));
	} elsif ($fmt eq 'SDATE') {
		# FMT_NO=39
		
		my ($y, $m, $d) = split(/\//, $rawData);
		
		if (length($y) ==2 ) {
			my $cn;
			if ($y > ($CURRENT_YEAR_S +30)){
				$cn = $CURRENT_C -1;
			} else {
				$cn = $CURRENT_C;
			}
			$y = $cn . $y;
		}
		
		$parsedData = join('-', ($y, $m, $d));
	} else {
		$parsedData=$rawData;
	}
	print $parsedData, "\n"  if $DBG;
	return $parsedData;
}

sub testLeapYear{
	my $year=shift;
	my $leap=0;
	my $r = $year%4;
	if (!$r) {
		# $r == 0 but still may not be a leap year;
		my $rx = $year%100;
		my $rxx= $year%400;
		if (!$rx) {
			# beginning of a century
			if (!$rxx){
				# 4-th century correction applicable
				# leap year
				$leap=1;
			}
		} else {
			$leap=1;
		}
	}
	return $leap;
}

sub findMonthDayInYear{
	my $DEBUG=0;
	my $remain = shift;
	my $yr= shift;
	my $leap = &testLeapYear($yr);	
	my $daysFeb = ($leap == 1) ?  29 : 28;
	my @daysM = (31,$daysFeb, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 );
	my $mnth =0;

	until($remain <= $daysM[$mnth]){
	} continue {
		print 'remain(b)=' . $remain . "\t" if $DEBUG;
		$remain = $remain - $daysM[$mnth];
		print 'subtracted=' . $daysM[$mnth] . "\t" if $DEBUG;
		print 'remain(a)=' . $remain . "\n" if $DEBUG;
		if (($remain == 0) || ($mnth == 11)) {
			last;
		} else {
			$mnth++;
		}
	}
	print "month index after until=". $mnth . "\n" if $DEBUG;
	my $monthY = $mnth +1;
	print 'month=' . $monthY  . ', day=' . $remain . "\n" if $DEBUG;
	my $m = sprintf "%02u", $monthY;
	my $d = sprintf "%02u", $remain;
	my $md = $m .'-' . $d;
	print "result=", $md, "\n" if $DEBUG;
	return $md;
}

1;



__END__


=head1 NAME

VDC::DSB::Ingest::StatData - parent class of statistical file reader classes

=head1 DESCRIPTION

VDC::DSB::Ingest::StatData is the parent class of statistical file reader classes (such as VDC::DSB::Ingest::POR, etc.) that sets up a common metadata container (hash) and provides various methods, which are shared by its sub-classes unless these sub-classes override them.  This module is called by VDC::DSB::Ingest::StatDataFileReaderFactory and is used with its sub-classes.



=head1 USAGE

=head2 The Constructor

=over 4

=item new()

sets up a common metadata container (hash) and calls _init() of its sub-class to initialize a statistical file reader instance.

=back

=head2 Methods

=over 4

=item addSumstat()

calculates descriptive statistics and adds them to the metadata 



=item writeDDIinXML(DDIFILE=>)

saves both section-3(s) and 4 of a DDI as a file.

=over 4

=item DDIFILE

sets the name of a DDI file

=back

=item writeDDIsec3(DDI3FILE=>)

saves DDI-section-3(s) as a file.

=over 4

=item DDI3FILE

sets the name of a DDI-section-3 file

=back

=item writeDDIsec4(DDI4FILE=>)

saves a DDI-section-4 as a file.

=over 4

=item DDI4FILE

sets the name of a DDI-Section-4 file

=back

=item mergeSubDDIs()

combines section 3(s) and section 4 of a DDI.

=item validateDDI()

validates a DDI file

=item writeVarLocMapFile(VLMFILE=>)

prints each variable's location-mapping-data itno a file 

=over 4

=item VLMFILE

sets the name of variable location-mapping file

=back


=back



=head1 SEE ALSO

    VDC::DSB::Ingest::StatDataFileReaderFactory

=head1 AUTHOR

Akio Sone, Virtual Data Center Project, Harvard University

(<URL:http://thedata.org/>)

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2004 President and Fellows of Harvard University

GNU General Public License



=cut




