package VDC::DSB::DataService;

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
use Data::Dumper;
use URI::Escape;
use File::Copy;
use HTML::Entities qw(encode_entities_numeric);

#use Switch;


my $optcdtbl ={
	statis => {
		A01 =>'univariate_stat',
		A02 =>'univariabe_chart',
		A03 =>'cross-tabulation',
	}, 

	dwnld => {
		D01 =>'download_tab-delimited',
		D02 =>'download_RSplus',
		D03 =>'download_stata',
		D04 =>'download_RBinary',
	},
};
my $errMsgtbl = {
	A03 => {
		1	=> 'Cross-tabulation parameter(row variable) is unselected!',
		2	=> 'Cross-tabulation parameter(column variable) is unselected!',
		3	=> 'Cross-tabulation parameters(both row and column vars) are unselected!',
	},
};

my $ccfltrlst = [qw (_varNameA _varNameH _varType _varLabel _varNo _varNoMpTbl _valLblTbl _mssvlTbl _charVarTbl VarID _varFormat _formatName _formatCatgry _newVarNameSetA _newVarNameSetH _newVarSpec unsafeVarName unsafeNewVarName _varNameAsafe _newVarNameAsafe _varNameHsafe _newVarNameHsafe RsafeVarName2raw) ];

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


my $rangeOprMap = {
	'='=>1,
	'!='=>2,
	'>='=>3,
	'<='=>4,
	'>'=>5,
	'<'=>6,
};
my $rangeOpMap = {
	'1'=>'=',
	'2'=>'!=',
	'3'=>'>=',
	'4'=>'<=',
	'5'=>'>',
	'6'=>'<',
};

my $rangeOpMapR = {
	'3'=> '6',
	'4'=> '5',
	'5'=> '4',
	'6'=> '3',
};

my $rangeOpMapRx = {
	'>='=> '<',
	'<='=> '>',
	'>'=> '<=',
	'<'=> '>=',
};

# constructor

sub new {
	my $caller = shift;
	my $optn = { @_ };
	my $tmp  = $optn->{RAWCGIPARAM};
	my $self = &paramfilter($tmp);
	my $class = ref($caller) || $caller;
	bless $self, $class;
	$self->_init( @_ );
	$self->{WRKDIR} = $optn->{WRKDIR};
	#print TMPX "dump2:\n",Dumper($self),"\n";
	return $self;
}

sub _init{
	my $self= shift;
	#print "dump_init:\n", Dumper(%$tmp),"\n";
	
	# safeguard for CaseWise Parameters
	unless (exists($self->{rwsbst}->{RNGMIN_0})) {
		$self->{rwsbst}->{RNGMIN_0}=1;
	}
	
	unless (exists($self->{rwsbst}->{RNGMAX_0})) {
		$self->{rwsbst}->{RNGMAX_0}=$self->{rwsbst}->{maxnrow};
	}
	
	# set caseQnty
	$self->{rwsbst}->{caseQnty}=$self->{rwsbst}->{RNGMAX_0} - $self->{rwsbst}->{RNGMIN_0};


	$self->{process}='A';
	# cehck whether a request is to download a tab-delimiting
	if (exists($self->{dwnld})) {
		my $dtdwnld=[split("\0",$self->{dwnld}->{dtdwnld})];
		if (scalar(@$dtdwnld)){
			my $tabdwnld=0;
			if ($self->{dwnld}->{dtdwnld} eq 'D01'){
				$tabdwnld=1;
				#if ($self->{dwnld}->{ccoptn} eq 'TRUE') {$tabdwnld++;};
				$self->{dwnld}->{ccoptn} = 'TRUE';
				$tabdwnld++;
			}
			$self->{dwnld}->{tab}=$tabdwnld;
			$self->{process}='D';
		}
	}
	
	$self->{statis}->{aopdip}=[];
	my @optnset_a = split(/\|/,$self->{statis}->{optnlst_a});
	foreach my $opts (@optnset_a) {
		if (exists ($self->{statis}->{aop}->{$opts})) {
			push @{$self->{statis}->{aopdip}}, 1;
		} else {
			push @{$self->{statis}->{aopdip}}, 0;
		}
	}
	
	foreach my $e (@{$self->{rwsbst}->{varIDorder}}){
		$self->{VarID}->{$e}='y';
	}
	
	my $charVarQnty=0;
	if (exists($self->{rwsbst}->{chrvarset})){
		foreach my $el (@{$self->{rwsbst}->{varIDorder}}){
			if (exists($self->{rwsbst}->{chrvarset}->{$el})){
				$charVarQnty++;
			}
		}
	}
	$self->{rwsbst}->{charVarQnty}=$charVarQnty;

}

sub paramfilter {
	my $cgiparam = shift;
	my $DEBUG=0;
	my $newset={};
	my $prmset_f ={
		uri=>'y',
		URLdata=>'y',
		studytitle=>'y',
		studyno=>'y',
		studyDate=>'y',
		authors=>'y',
		bblCit=>'y',
		CitationUNF=>'y',
		OnlineCitation=>'y',
		OfflineCitation=>'y',
	};
	my $xcldlst={
		searchStrng => 'y',
		launch =>'y',
		state => 'y',
	};
	my $prmset_d ={
		dtdwnld =>'y',
		ccoptn =>'y',
	};
	my $prmset_a ={
		analysis  =>'y',
		optnlst_a =>'y',
		fileid=>'y',
	};
	my $prmset_x ={
		nestoptn =>'y',
	};
	
	$newset={
		xtab => {
			"Percentages"=>'F',
			"Totals"     =>'F', 
			"Statistics" =>'F', 
			"ExtraTables"=>'F',
		},
	};
	my $dataframeTobeModified = 0;
	# user-defined vars
	# user-defined name
	if (exists($cgiparam->{newVarNameSet})){
		my $tmpvnhsh ={};
		$newset->{unsafeNewVarName}=0;
		$newset->{_newVarNameAsafe}=[];
		
		foreach my $v (split('\0', $cgiparam->{newVarNameSet})){
			$tmpvnhsh->{$v}='y';
			
			# check unsafe characers in each var name
			my $tmpVN = $v;
			foreach my $token (keys %{$CNVRSNTBL}){
				$tmpVN =~ s/[$token]/$CNVRSNTBL->{$token}/g;
			}
			if ($tmpVN ne $v){
				$newset->{unsafeNewVarName}++;
			}
			push @{$newset->{_newVarNameAsafe}}, $tmpVN;
			$newset->{_newVarNameHsafe}->{$v} = $tmpVN;
		}

		print "name varName hash:",Dumper ($tmpvnhsh) if $DEBUG;
		$newset->{_newVarNameSetH}=$tmpvnhsh;
		$newset->{_newVarNameSetA}=[split('\0', $cgiparam->{newVarNameSet})];
	
		foreach my $ky (keys %{$cgiparam}) {
			print "key to be checked=",$ky,"\n" if $DEBUG;
			if ($ky =~ m/^(ud_)([a-zA-Z][a-zA-Z0-9._]+)(_q_)(\S+)$/){
				my ($var, $vl) = split(/_q_/,$ky);
				print "(prefix,value)=", join(',',($var, $vl)), "\n" if $DEBUG;
				my ($fig, $newVarName) = split(/^ud_/, $var);
				#print "fig=", $fig, "\t", $newVarName, "\n";
				if ($newVarName){
					print "new variable Name=", $newVarName, "\n" if $DEBUG;
				}

				# check the key against the new var name list
				if (exists($newset->{_newVarNameSetH}->{$newVarName})){
					my $oldvar;
					if (!exists($newset->{_newVarSpec})){
						$newset->{_newVarSpec}={};
					}
					print "var name=",$newVarName ," is found\n" if $DEBUG;

					# $newset->{_newVarSpec}->{$ky}= $newset->{$ky};
					if (!exists($newset->{_newVarSpec}->{$newVarName})){
						# spec set is not recorded
						$newset->{_newVarSpec}->{$newVarName}={
							'label'=>'',
							'type'=>'',
							'valueTable'=>{},
						};
					}

					if ($vl eq 'lebaLrav'){
						$newset->{_newVarSpec}->{$newVarName}->{'label'} = $cgiparam->{$ky};
					} elsif ($vl eq 'epyTrav') {
						$newset->{_newVarSpec}->{$newVarName}->{'type'}  = $cgiparam->{$ky};
					} else {
						# vll:['eteleD|NATDISC|8','eteleD|NATDISC|7','eteleD|NATDISC|9']
						foreach my $vll (split('\0',$cgiparam->{$ky})){
							# 'NEVER|LOCDISC|1'
							my ($lbl, $vr, $vs)= split(/\|/, $vll);
							print "\nlbl=",$lbl, "\tbase var(vr)=", $vr,"\t condition(vs)=",$vs,"\n\n" if $DEBUG;
							if ($lbl ne 'eteleD'){
								$newset->{_newVarSpec}->{$newVarName}->{'valueTable'}->{$vl}->{'label'}=$lbl;
							} else {
								$dataframeTobeModified++;
							}
							if (!exists($newset->{_newVarSpec}->{$newVarName}->{'valueTable'}->{$vl}->{$vr})){
								$newset->{_newVarSpec}->{$newVarName}->{'valueTable'}->{$vl}->{$vr}=[];
							}
							print "returned range:\n", Dumper( &getValueRange($vs)) if $DEBUG;
							my $tmp = &getValueRange($vs);
							push @{$newset->{_newVarSpec}->{$newVarName}->{'valueTable'}->{$vl}->{$vr}}, $tmp->[0] ;
							#$newset->{_newVarSpec}->{$newVarName}->{'valueTable'}->{$vl}->{$vr}= &getValueRange($vs);
							if ($newset->{_newVarNameSetH}->{$newVarName} eq "y") {
								$newset->{_newVarNameSetH}->{$newVarName}=$vr;
							}
						}
					}
				}
			}
			if (($ky =~ m/^v\d+_\d+/) && 
				(scalar(@{$newset->{_newVarNameSetA}})) && 
				(exists($newset->{_newVarNameSetH}->{ $cgiparam->{$ky}})) ) {
				$newset->{newVarId2VarName}->{$ky} = $cgiparam->{$ky} ;
			}
		}
	}
	
	$newset->{dataframeModified}=$dataframeTobeModified;
	
	# zelig: pre-processing part
	if (exists($cgiparam->{zlg})){
		if ($cgiparam->{zlg} ne ''){
			$newset->{zlgParam}={};
			
			$newset->{zlgParam}->{zlgNo2MdlId}={};
			$newset->{zlgParam}->{zlgRqstdMdl}=[];
			my @tmp = split('\0', $cgiparam->{zlg});
			foreach my $i (@tmp) {
				my ($ky, $vl) = split('-', $i);
				# $ky :zlg_00i
				# $vl : ls, logit, etc.
				#$zlgNoMdlIdH->{$ky} = $vl;
				$newset->{zlgParam}->{zlgNo2MdlId}->{$ky}=$vl;
				#push @$zlgMdlRqst, $vl;
				push @{$newset->{zlgParam}->{zlgRqstdMdl}},$vl;
				$newset->{zlgParam}->{$vl}={};
				$newset->{zlgParam}->{$vl}->{No}=$ky;

				# no of boxes info
				my $noBoxes = "noBoxes_" . $vl;
				if (exists($cgiparam->{$noBoxes})){
					if ($cgiparam->{$noBoxes}){
						$newset->{zlgParam}->{$vl}->{noBoxes}=$cgiparam->{$noBoxes};
					} else {
						# for value-missing cases, set the default value
						$newset->{zlgParam}->{$vl}->{noBoxes}=2;
					}
				}
				# model type no: [0, 3]
				my $mdlType = "mdlType_" . $vl;
				if (exists($cgiparam->{$mdlType})){
					if ($cgiparam->{$mdlType}){
						$newset->{zlgParam}->{$vl}->{mdlType}=0+$cgiparam->{$mdlType};
					} else {
						# for value-missing cases, set the default value
						$newset->{zlgParam}->{$vl}->{mdlType}=0;
					}
				}
				# model title
				my $mdlTitle ="mdlTitle_" . $vl;
				if (exists($cgiparam->{$mdlTitle})){
					if ($cgiparam->{$mdlTitle}){
						$newset->{zlgParam}->{$vl}->{mdlTitle}=$cgiparam->{$mdlTitle};
					} else {
						# for value-missing cases, set the default value
						$newset->{zlgParam}->{$vl}->{mdlTitle}="Model Title Not Available";
					}
					# modelId 2 modelTitle hash
					$newset->{zlgParam}->{mdlId2mdlTitle}->{$vl} = $cgiparam->{$mdlTitle};
				}
				
				my $mdlDepVarType= "mdlDepVarType_" . $vl;
				if (exists($cgiparam->{$mdlDepVarType})){
					if ($cgiparam->{$mdlDepVarType}){
						$newset->{zlgParam}->{$vl}->{mdlDepVarType}=$cgiparam->{$mdlDepVarType};
					} else {
						# for value-missing cases, set the default value
						$newset->{zlgParam}->{$vl}->{mdlDepVarType}="";
					}
				}
			}
		} else {
			# for value-missing cases
		}
	}

	#print TMPX "\nzelig-related key-parameters (newset):\n",Dumper($newset);; 
	
	$newset->{rwsbst}->{yes}=0;
	while ((my $key, my $value) = each(%{$cgiparam})) {
		if (exists($xcldlst->{$key})) {
			next;
		} elsif (exists($prmset_f->{$key})) {
			$newset->{citation}->{$key}=$value;
		} elsif (exists($prmset_d->{$key})) {
			$newset->{dwnld}->{$key}=$value;
		} elsif (exists($prmset_a->{$key})) {
			
			if ($key eq 'analysis'){
				my @analysis = split("\0", $value);
				$newset->{statis}->{noAs} = scalar(@analysis);
				foreach my $el (@analysis){
					$newset->{statis}->{aop}->{$el}= 1;
				}
			} else {
				$newset->{statis}->{$key}=$value;
			}
		} elsif ($key eq 'maxnrow') {
			$newset->{rwsbst}->{maxnrow}=$value;
		} elsif ($key eq 'varbl') {
			$newset->{rwsbst}->{varIDorder}= [split("\0",$value)];
			$newset->{rwsbst}->{novars}=scalar(@{$newset->{rwsbst}->{varIDorder}});
		} elsif ($key eq 'charVarNoSet')  {
			my @tmpc= split(/\|/, $value);
			foreach my $temp (@tmpc){
				$newset->{rwsbst}->{chrvarset}->{$temp}= 'y'; 
			}
		} elsif ( ($key eq 'cellstat') && ($value)) {
			$newset->{xtab}->{cellStat}=[1,0,0,0,0];
			my @tmps0=split("\0",$value);
			foreach my $tmp0 (@tmps0){
				$newset->{xtab}->{cellStat}->[$tmp0]= 1; 
			}
		} elsif ($key eq 'nestoptn') {
			$newset->{xtab}->{$key} = $value;
		} else {
			my @tmpz=  split(/_/, $key);
			if ( ($value) && ($tmpz[0] =~ /^RNGMIN|^RNGMAX|^XCLD/))  { 

				#if ($DeBuG) {print TMPX $key, "=>", $value, "\n";}
				if (($key eq 'RNGMIN_0') && ($value != 1)){
					$newset->{rwsbst}->{yes}++;
				}
				if (($key eq 'RNGMAX_0') && ($value ne $cgiparam->{maxnrow})){
					$newset->{rwsbst}->{yes}++;
				}
				if (($key ne 'RNGMAX_0' ) && ($key ne 'RNGMIN_0')){
					$newset->{rwsbst}->{yes}++;
				}
				$newset->{rwsbst}->{$key} = $value;
				
			} elsif ($tmpz[0] =~ /^xtb/) { 
				#if ($DeBuG) {print TMPX $key, "=>", $value, "\n";}
				
				#if ($value eq 'c') { 
				#	push @{$newset->{xtab}->{colmvars}}, $tmpz[1];
				#} elsif ($value eq 'r1') {
				#	push @{$newset->{xtab}->{p1stRvar}}, $tmpz[1];
				#	push @{$newset->{xtab}->{rowvars} }, $tmpz[1];
				#} elsif ($value eq 'r2') {
				#	push @{$newset->{xtab}->{p2ndRvar}}, $tmpz[1];
				#	push @{$newset->{xtab}->{rowvars} }, $tmpz[1];
				#} elsif ($value eq 'r3') {
				#	push @{$newset->{xtab}->{p3rdRvar}}, $tmpz[1];
				#	push @{$newset->{xtab}->{rowvars} }, $tmpz[1];
				#} elsif ($value eq 'l') {
				#	push @{$newset->{xtab}->{cntlvars}}, $tmpz[1];
				#}
				
				if ($tmpz[1] eq 'nmBxR1'){
					$newset->{xtab}->{classVars}   = [split("\0",$value)];
				} elsif ($tmpz[1] eq 'Totals')      {
					$newset->{xtab}->{Totals}      = $value;
				} elsif ($tmpz[1] eq 'Percentages') {
					$newset->{xtab}->{Percentages} = $value;
				} elsif ($tmpz[1] eq 'Statistics')  {
					$newset->{xtab}->{Statistics}  = $value;
				} elsif ($tmpz[1] eq 'ExtraTables') {
					$newset->{xtab}->{ExtraTables} = $value;
				} elsif ($tmpz[1] eq 'nmBxR2')      {
					$newset->{xtab}->{freqVar}     = $value;
				}
				
			} elsif ($tmpz[0] =~ /^zlg/) {
				#print $key, " => ", $value, "\n" if $DEBUG;
				if ($#tmpz >1) {
					#print $tmpz[1] . '->' . $tmpz[2]. '==' . $value."\n";

					my $zky = join('_', (@tmpz[0..1]));
					my $zky2 = join('_', @tmpz[2..$#tmpz]);
					#print $zky .'<=>'. $zky2 ."\n";
					#my @valueset = split("\0", $value);
					$newset->{zlgParam}->{  $newset->{zlgParam}->{zlgNo2MdlId}->{$zky} }->{$zky2}=[split(/\0/, $value)];
				}
			}
		}
	}
	
	if (exists($newset->{dwnld})) {
		unless (exists($newset->{dwnld}->{dtdwnld})) {
			delete $newset->{dwnld};
		}
	}
	
	my $varId2pstn={};
	for (my $v=0;$v< @{$newset->{rwsbst}->{varIDorder}}; $v++) {
		$varId2pstn->{ $newset->{rwsbst}->{varIDorder}->[$v] }= $v+1;
	}
	
	foreach my $mdl_i (@{$newset->{zlgParam}->{zlgRqstdMdl}}){
		my $noBxes = $newset->{zlgParam}->{$mdl_i}->{noBoxes};
		$newset->{zlgParam}->{$mdl_i}->{varIds}=[];
		#$newset->{zlgParam}->{$mdl_i}->{varId2dfColNo}={};

		for (my $j=1;$j<=$noBxes;$j++){
			my $boxKey = 'nmBxR' . $j;
			if (exists($newset->{zlgParam}->{$mdl_i}->{$boxKey}) && ($newset->{zlgParam}->{$mdl_i}->{$boxKey})){
				push @{$newset->{zlgParam}->{$mdl_i}->{varIds}}, @{$newset->{zlgParam}->{$mdl_i}->{$boxKey}};
			}
		}
		
		#for my $k (@{$newset->{zlgParam}->{$mdl_i}->{varIds}}){
		#	$newset->{zlgParam}->{$mdl_i}->{varId2dfColNo}->{$k} = $varId2pstn->{$k};
		#}
	}

	return $newset;
}

# methods that check params 
sub checkParams {
	my $self = shift;
	my $chckoptn_a = {A03 =>1};
	my @chckoptn_d = {};
	my $rtrnvl = [undef, undef, undef];
	if (exists($self->{dwnld})){
		$rtrnvl->[0]='dwnld';
	} else {
		$rtrnvl->[0]='statis';
	}
	
	if ($rtrnvl->[0] eq 'statis'){
		my @optns = keys %{$self->{statis}->{aop}};
		foreach my $el (@optns){
			if (exists($chckoptn_a->{$el})){
				if ($el eq 'A03'){
					$rtrnvl->[1] = 'A03';
					$rtrnvl->[2] = $self->checkXtabParams();
					goto LASTBLOCK;
				}
			}
		
		}
	
	} elsif ($rtrnvl->[0] eq 'dwnld'){
	
	
	}
	LASTBLOCK: my $rtrnvalue=[undef, undef];
	if ($rtrnvl->[2]){
		$rtrnvalue->[0] = $optcdtbl->{$rtrnvl->[0]}->{$rtrnvl->[1]};
		if (exists($rtrnvl->[1])){
			$rtrnvalue->[1] = $errMsgtbl->{$rtrnvl->[2]};
		}
	}
	# 
	return $rtrnvalue;
}


sub checkStatOpt {
	my ($self) = shift;
	my $optn = shift;
	if ($optn) {
#		print TMPX "getAnalysisOpt=",$optn,"\n";
		return $self->{statis}->{aop}->{$optn};
	}
}

sub checkXtabParams{
	my $self =shift;
	my $rtrnvl;
	my $colsize= scalar( @{$self->{xtab}->{colmvars}});
	my $rowsize= scalar( @{$self->{xtab}->{rowvars}} );
	if      (($colsize <  1) && ($rowsize <  1)) {
		$rtrnvl=3;
	} elsif (($colsize <  1) && ($rowsize >= 1)) {
		$rtrnvl=2;
	} elsif (($colsize >= 1) && ($rowsize <  1)) {
		$rtrnvl=1;
	} else {
		$rtrnvl=0;
	}
	return $rtrnvl;
}





# basic access methods


sub addMetaData{
	my $self =shift;
	my $tmp = shift;
	#print  "arguments are not empty\n";
	while ( my ($ky, $vl) = each %{$tmp} ) {
		if ($ky eq '_dcml'){
			if (scalar(keys %{$tmp->{$ky}})){
				$self->{rwsbst}->{$ky}=$vl;
				$self->{rwsbst}->{yes}++;
			}
		} else {
			$self->{$ky}=$vl;
		}
		#$self->{metadata}->{$ky}=$vl;
		
	}
	# sanitize variable names for R
	#
	# attributes to be added
	# $self->{unsafeVarName} = scalar: 0 or more
	# $self->{_varNameAsafe} = array [safe variable name]
	# $self->{_varNameHsafe} = hash key: id => safe variable name: 
	my $count=0;
	my $rawvarname = $self->{_varNameA};
	#my $self->{'_varNameH'}; # id => name
	my $raw2RsafeVarName={};
	my $RsafeVarName2raw={};
	my $varNameWunderscore={};
	my $varName=[];
	for (my $i=0;$i<@{$rawvarname};$i++){
		my $tmpVN = $rawvarname->[$i];
		# check it against the R-reserved-word list
		if (exists($Rkywrd2safeVarName->{$tmpVN})){
			$tmpVN = $Rkywrd2safeVarName->{$tmpVN};
			$raw2RsafeVarName->{$rawvarname->[$i]}=$tmpVN;
			$RsafeVarName2raw->{$tmpVN}=$rawvarname->[$i];
			$count++;
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
				$count++;
			}
		}
		push @{$varName}, $tmpVN;
	}
	$self->{_varNameAsafe}    = $varName;
	
	my $tmph ={};
	while ((my $key, my $value) = each(%{$self->{'_varNameH'}})) {
		if (exists($raw2RsafeVarName->{$value})){
			$tmph->{$key} = $raw2RsafeVarName->{$value};
		} else {
			$tmph->{$key} = $value;
		}
	}
	$self->{_varNameHsafe}    = $tmph;
	$self->{raw2RsafeVarName} = $raw2RsafeVarName;
	$self->{RsafeVarName2raw} = $RsafeVarName2raw;
	$self->{unsafeVarName}    = $count;
}

sub getMetaData4CC {
	my $self =shift;
	my $tmp = shift;
	my $cgiparam = shift;
	my $incldlst = {};
	foreach my $el (@$ccfltrlst){
		$incldlst->{$el} ='y' ;
	}

	my $newset={};
	while ((my $key, my $value) = each(%{$self})) {
		if (exists($incldlst->{$key})) {
			$newset->{$key}=$value;
		} else {
			next;
		}
	}
	return $newset;
}

sub getVarIDhsh{
	my $self= shift;
	return $self->{VarID};
}


sub getXtabParams {
	my $self =shift;
	my $type=shift;
	if ($type)  {
		     if ($type eq 'rows') {
			$self->{xtab}->{rowvars};
		} elsif ($type eq 'row1') {
			$self->{xtab}->{p1stRvar};
		} elsif ($type eq 'row2') {
			$self->{xtab}->{p2ndRvar};
		} elsif ($type eq 'row3') {
			$self->{xtab}->{p3rdRvar};
		} elsif ($type eq 'column') {
			$self->{xtab}->{colmvars};
		} elsif ($type eq 'control'){
			$self->{xtab}->{cntlvars};
		}
	} else {
		$self->{xtab};
	}
}

sub getRsubsetparam{
	my $self =shift;
	my $args  = { @_ };
	my $DEBUG=0;
	# ddi:var[(location/@fileid='file2') and (@ID='AAA' or @ID='CCC' or @ID='BBB' )]
	# nodeSetString="(location/@fileid='FLIDX') and (@ID='AAA' or @ID='CCC' or @ID='BBB')"
	my $IDpart="";
	my $temp="";
	# here varIDorder has been already sorted
	for (my $i=0;$i<@{$self->{rwsbst}->{varIDorder}};$i++) {
		$temp = "(\@ID=\'$self->{rwsbst}->{varIDorder}->[$i]\')";
		if ($i != $#{$self->{rwsbst}->{varIDorder}} ) {
			# not last element
			$temp .= " or " ;
		}
		$IDpart .=  $temp;
	}
		$self->{Rsbstparam} = "\"(ddi:location/\@fileid=\'$self->{statis}->{fileid}\') and (" . $IDpart . ")\"" ;
	if ($DEBUG){
		print TMPX "subsetting string(IDs)=", $IDpart,"\n";
		print TMPX "subsetting string(param)=", $self->{Rsbstparam},"\n";
	}
	return $self->{Rsbstparam};
}

sub getCellStat{
	my ($self) = shift;
	return $self->{cellStat};
}

sub getAolist{
	my ($self) = shift;
	$self->{_aolist};
}

sub checkNonZeligArqst{
	my $self = shift;
	return $self->{statis}->{noAs};
}

sub getVarIDorder{
	my ($self) = shift;
	#$self->{rwsbst}->{varIDorder};


	my $tmpOrderedVarIdSet=[];
	foreach my $ky1 (sort {$self->{"_varNoMpTbl"}->{$a} <=> $self->{"_varNoMpTbl"}->{$b}} keys %{$self->{"_varNoMpTbl"}} ){
		#print $ky1 . " and " . $self->{"_varNoMpTbl"}->{$ky1} ."\n";
		push @$tmpOrderedVarIdSet, $ky1;
	}
	$self->{rwsbst}->{varIDorder}=$tmpOrderedVarIdSet;
	return $self->{rwsbst}->{varIDorder};


}


sub getRwSbst{
	my $self = shift;
	my $rv={};
	if (exists($self->{rwsbst})){
		while ((my $key, my $value) = each(%{$self->{rwsbst}}) ) {
			$rv->{$key} = $value;
		}
	}
	return $rv;
}

sub checkDwnldTab{
	my $self = shift;
	my $rv = 0;
	if (exists($self->{dwnld}) && exists($self->{dwnld}->{tab}) ){
		$rv = $self->{dwnld}->{tab};
	}
	return $rv;
}

sub checkCaseSubset{
	my $self =shift;
	return $self->{rwsbst}->{yes};
}

sub updateCaseQnty{
	my $self =shift;
	my $tmp  =shift;
	$self->{rwsbst}->{caseQnty}=$tmp;
}
sub  getCaseQnty{
	my $self =shift;
	return $self->{rwsbst}->{caseQnty};
}

sub getCharVarQnty{
	my $self =shift;
	
	my $charVarQnty=0;
	if (exists($self->{rwsbst}->{chrvarset})){
		foreach my $el (@{$self->{rwsbst}->{varIDorder}}){
			if (exists($self->{rwsbst}->{chrvarset}->{$el})){
				$charVarQnty++;
			}
		}
	}
	return $charVarQnty;
}

sub getParams{
	my $self =shift;
	my $rv;
	my $statis = $self->{statis} if (exists($self->{statis}));
	my $dwnld  = $self->{dwnld}  if (exists($self->{dwnld}));
	my $xtab   = $self->{xtab} if (exists($self->{xtab}));
	#$rv->{};
	return $rv;
}

sub getRqustType{
	my $self = shift;
	my $type = shift;
	my $rv=0;
	if ($self->{process} eq $type){
		$rv =1;
	}
	return $rv;
}
sub  getDwnldType{
	my $self = shift;
	my $rv;
	$rv = exists($self->{dwnld}) ? $self->{dwnld}->{dtdwnld} : undef ;
	return $rv;
}
sub getStatisOptn{
	my $self = shift;
	my $rv;
	if ($self->{process} eq 'A'){
		$rv = $self->{statis};
	}
	return $rv;
}

sub getDwnldOptn{
	my $self = shift;
	my $rv;
	if ($self->{process} eq 'D'){
		$rv = $self->{dwnld};
	}
	return $rv;
}

sub getVarNameSet{
	my $self = shift;
	my $rv = $self->{_varNameA};
	return $rv;
}
sub getVarNameSetAll{
	my $self = shift;
	my $rv;

	if (exists($self->{_newVarNameSetA})){
		$rv = [@{$self->{_varNameA}}, @{$self->{_newVarNameSetA}} ];
	} else {
		$rv = $self->{_varNameA};
	}
	return $rv;

}

sub hasNewVarNames{
	my $self = shift;
	my $size = 0;
	if (exists($self->{'_newVarNameSetA'})){
		$size = scalar(@{$self->{'_newVarNameSetA'}});
	}
	return $size;
}

# methods for the divie-and-conquire-R-code 
sub setVarRange {
	my $Rdata   = shift;
	my $varQnty = shift;
	my $caseQnty = shift;
	my $charVarQnty = shift;
	my $WRKDIR = shift; # until $TMPDIR becomes available
	
	my ($divisor,$increment);
	my $varRangeSet = [];
	
	# open(TMPZ, "> /tmp/tmpRange.$$.log");
	open(TMPZ, "> $WRKDIR/tmpRange.$$.log");
	
	#my $FH = *STDOUT;
	my $FH = *TMPZ;
	print $FH "\n\n////////// setVarRange //////////\n";
	print $FH "# of vars=",$varQnty,"\n";
	print $FH "# of char vars=",$charVarQnty,"\n";
	print $FH "# of cases=",$caseQnty,"\n";
	
	my $freeMemSize = &getfreeMemSize();
	print $FH "inactive mem size=",$freeMemSize,"\n";
	my $wght=0.10;
	print $FH "weight=",$wght,"\n";
	$freeMemSize = $wght*$freeMemSize;
	print $FH "inactive mem size(r)=",$freeMemSize,"\n";
	my @coefvars=(0.042, 0.290);
	my $prblmSize= ($coefvars[0]*($varQnty - $charVarQnty) + $coefvars[1]*$charVarQnty)*$caseQnty;
	print $FH "problem size=",$prblmSize,"\n";
	unless ($freeMemSize){
		$freeMemSize = $prblmSize +1;
	}
	if (1){
		$freeMemSize = 30;
		$prblmSize   = 10;
	}
	if ($freeMemSize <= $prblmSize) {
		print $FH "Inactive memory would be insuffcient to finish this R job\n";
		$divisor = int($prblmSize/$freeMemSize);
		# a sub-problem must have at least one var,
		# i.e., the divisor must be <= varQnty
		if ($divisor > $varQnty) {$divisor = $varQnty;}
		$increment = int($varQnty/$divisor);

		print $FH "problem size=",$prblmSize,"\n";
		print $FH "available free memory=",$freeMemSize,"\n";
		print $FH "divisor=",$divisor,"\n";
		print $FH "increment=",$increment,"\n";

		for (my $i=0;$i< $divisor;$i++){
			if ($i == ($divisor)){
				push @$varRangeSet, [$i*$increment,($varQnty-1) ];
			} else {
				push @$varRangeSet, [$i*$increment,($i+1)*$increment-1 ];
			}
			print $FH "range set($i)=",join("|",@{$varRangeSet->[$i]}),"\n";
		}
	} else {
		print $FH "Inactive memory would be suffcient to finish this R job\n";
		$divisor = 1;
		print $FH "divisor=",$divisor,"\n";
		push @$varRangeSet, [0,($varQnty-1)]
	}
	print $FH "varRangeSet\n", Dumper($varRangeSet);
	print $FH "////////// setVarRange //////////\n\n";
	my $basedir= $WRKDIR ;
	my $runtab=1;
	my $moptn = '-m ' . $$;
	my $foptn='-f';
	print $FH "Rdata=",$Rdata,"\n";
	print $FH "foptn=",$foptn,"\n";
	print $FH "moptn=",$moptn,"\n";
	
	for (my $i=0;$i<@$varRangeSet; $i++){
			$foptn .= ($varRangeSet->[$i][0]+1) . '-' . ($varRangeSet->[$i][1]+1) ;
		if ($i<(@$varRangeSet - 1)) {
			$foptn .=',';
		}
	}
	if ($divisor > 1){
		open(PH, "/usr/local/bin/rcut $foptn $moptn <$Rdata 2>&1 |");
		while (<PH>) {
			#print STDERR $_;
			if (/No such file or directory/i){
				print STDERR "error in rcut: an input(tab) file is missing=",$1,"\n";
				$runtab=0;
			}
		}
		close(PH);
	} elsif ($divisor == 1){
		my $RtmpData = "$basedir/t.$$.1.tab";
		copy($Rdata,$RtmpData);
	}
	close($FH);
	return $varRangeSet;
}

sub getfreeMemSize {
	my $freeMemsize=0;
	my $debug;
	my ($key, $value, %memstat);
		#open(PH, "grep -e 'MemT\\|MemF\\|Active' < /proc/meminfo | tr -d ' kB' 2>&1 |");
		open(PH, "grep -e 't_d\\|t_c' < /proc/meminfo | tr -d ' kB' 2>&1 |");
		while (<PH>) {
			chomp;
			if ($_){
				#print $_, "\n";
				($key, $value)=split(':', $_);
				print STDERR $key, "\t",$value,"\n"  if $debug;
				$memstat{$key}=$value;
			}
		}
		close (PH);
		if (exists($memstat{'Inact_dirty'}) && exists($memstat{'Inact_clean'}) ){
			$freeMemsize= $memstat{'Inact_dirty'} + $memstat{'Inact_clean'};
			print STDERR "getfreeMemSize:freeMemsize=",$freeMemsize,"\n" if $debug;
		}
	return $freeMemsize;
}


###############################################################################
# methods for writing R-code lines
sub printRcodeData{
	my $self = shift;
	my $optn = { @_ };
	
	my $htmlfl   = $self->{HTMLFILE} = $optn->{HTMLFILE};
	my $wh       = $self->{WH}       = $optn->{WH};
	my $tabfile  = $self->{TABFILE}  = $optn->{TABFILE};
	my $srvrcgi  = $self->{SRVRCGI}  = $optn->{SRVRCGI};
	my $imgflprfx  = $self->{IMGFLPRFX}  = $optn->{IMGFLPRFX};
	my $dwnldprfx= $self->{DWNLDPRFX}= $optn->{DWNLDPRFX};
	my $withZlg  = $optn->{WITHZLG};
	unless ($withZlg) {
		$withZlg='T';
	}
	my $novars      = $self->{rwsbst}->{novars};
	my $caseQnty    = $self->{rwsbst}->{caseQnty};
	my $charVarQnty = $self->{rwsbst}->{charVarQnty};
	
	# determine whether the machine has enough resources to finish 
	# this job or not; if not, divide the jobs into parts
	
	my $WRKDIR = $self->{WRKDIR};
	my $varRangeSet = $self->{VarRangeSet}= &setVarRange($tabfile, $novars, $caseQnty, $charVarQnty, $WRKDIR);
	my $update=0;
	if (exists($self->{'_newVarNameSetA'})){
		$update = scalar(@{$self->{'_newVarNameSetA'}});
	}
	
	$self->setBpRcode();
	$self->printBpRcode(line=>"startup");
	print $wh "# iteration lines begin here\n";
	for (my $i=0; $i< @{$varRangeSet}; $i++) {
		print $wh "\n# ///// i = $i ////// \n";
		$self->printVarType(iteration=>$i);
		$self->printVarFormat(iteration=>$i);
		$self->printReadTable(iteration=>$i);
		#if ($self->{unsafeVarName}){
			$self->printRsafe2rawList();
		#}
		$self->printBpRcode(line=>"asI");
		## user-defined vars
		if ($update){
			$self->printSubset(iteration=>$i);
			# user-defined vars are added to the data.frame
			$self->printUserDefinedVars(iteration=>$i);
			# update vartyp if necessary
			$self->printUpdateVartyp(iteration=>$i);
		}
		$self->printVarNo(iteration=>$i);# add user-defined cases
		$self->printVarLabel(iteration=>$i);# add user-defined cases
		$self->printBpRcode(line=>"VL"); 
		$self->printValueTable(iteration=>$i); # add user-defined cases
		$self->printBpRcode(line=>"attrVL"); 
		$self->printBpRcode(line=>"MVL");
		$self->printMissValueDsc(iteration=>$i); 
		$self->printBpRcode(line=>"attrMVL");
		$self->printBpRcode(line=>"attrVLMVL");
		#$self->printMissValueCntn(iteration=>$i);
		
		$self->printUserDefinedMissValues(iteration=>$i);
	}
	print $wh "# iteration lines end here\n";
	$self->printAoption();
	$self->printDoption();
	my $RqstType = $self->{process};
	if ($RqstType eq 'A'){
		# required all Ai
		$self->printUnivarStat();
		
		# request specific
		if (exists($self->{statis}->{aop}->{A02})) {
			$self->printUnivarChart(IMGFLPRFX=>$imgflprfx, STANDALONE=>$withZlg);
			
		}
		
		if ((exists($self->{statis}->{aop}->{A01}))|| (exists($self->{statis}->{aop}->{A02}))) {
			$self->printUnivarStatHtml(SRVRCGI=>$srvrcgi, HTMLFL=>$htmlfl, STANDALONE=>$withZlg);
		} 
		
		if (exists($self->{statis}->{aop}->{A03})){
			#$self->printXtabCellstat();
			#$self->printXtabContrlvars();
			#$self->printXtabClmnvars();
			#$self->printXtabRwvars();
			#$self->printXtabFrstpstn();
			#$self->printXtabScndpstn();
			#$self->printXtabThrdpstn();
			#$self->printXtab(HTMLFL=>$htmlfl);
			
			$self->printVDCxtabs(HTMLFL=>$htmlfl);
		}
	
	} elsif ($RqstType eq 'D') {
		$self->printUnivarDataDwnld(DWNLDPRFX=>$dwnldprfx);
	}
}

sub setBpRcode{
	my $self = shift;
	my $optn ={ @_ };
	$self->{bpRdata}={
		startup=>"########## VDC DS Request Code starts here ##########\nsource(\"/usr/local/VDC/R/library/vdc_startup.R\")\n\n",
		asI=>"for (i in 1:dim(x)[2]){if (attr(x,\"var.type\")[i] == 0) {\nx[[i]]<-I(x[[i]]);  x[[i]][ x[[i]] == '' ]<-NA  }}\n\n",
		VL=>"VALTABLE<-list()\n",
		attrVL=>"attr(x, \"val.table\")<-VALTABLE\n",
		MVL=>"MSVLTBL<-list()\n",
		attrMVL=>"attr(x, \"missval.table\")<-MSVLTBL\n",
		attrVLMVL=>"x<-createvalindex(dtfrm=x, attrname=\"val.index\")\nx<-createvalindex(dtfrm=x, attrname=\"missval.index\")\n"
	};
}

sub printBpRcode{
	my $self = shift;
	my $optn ={ @_ };
	my $line = $optn->{line};
	my $wh = $self->{WH};
	print $wh $self->{bpRdata}->{$line};
}

sub printVarType{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $varType = $self->{_varType};
	for (my $j=0; $j< scalar(@$varType); $j++){
		my $vn = $self->{_varNameA}->[$j];
		if ( (lc($self->{_varFormat}->{$vn}) eq 'date') || (lc($self->{_varFormat}->{$vn}) eq 'time')){
			$varType->[$j] = 0;
		}
	}
	
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};
	print $wh "vartyp <-c(\n", join(",\n", @$varType[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]]),")\n\n";
}

sub printVarFormat{
	my $self = shift;
	my $optn ={ @_ };
	my $varname;
	if ($self->{unsafeVarName}){
		$varname = $self->{_varNameAsafe};
	} else {
		$varname = $self->{_varNameA};
	}
	my $rawvarname=$self->{_varNameA};
	my $wh = $self->{WH};
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};
	my @varNameSet=@$varname[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]];
	# var used for a key of the hash is raw-type (not R-safe type)
	my @rawvarNameSet=@$rawvarname[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]];

	print $wh "varFmt<-list(","\n";
		my $contents="";
		for (my $v=0;$v<@varNameSet; $v++){
			
			my $vnr= $rawvarNameSet[$v];
			my $vf=$self->{_varFormat}->{$vnr};
			#print $v,"\t",$vf,"\n"; 

			my $fmtChar="";
			if (($self->{_formatCatgry}->{$vnr} eq 'date') || (lc($vf) eq 'date') ) {
				# date conversion
				$fmtChar='D';
			} elsif (($self->{_formatCatgry}->{$vnr} eq 'time') || (lc($vf) eq 'time') ){
			
				# time conversion
				if (uc($vf) eq 'DTIME'){
					$fmtChar='JT';
				} elsif (uc($vf) eq 'DATETIME'){
					$fmtChar='DT';
				} else {
					$fmtChar='T';
				}
				
			} elsif ($self->{_formatCatgry}->{$vnr} eq 'other') {
				# currently not applicable
			}
			
			if ($fmtChar){
				#my $terminator="";
				#if ($v != (scalar(@varNameSet)-1)){
				#	$terminator=",\n";
				#}
				$contents .= "'" . $varNameSet[$v] . "'='" . $fmtChar . "'" . ",\n"; 
				#print $wh "'",$varNameSet[$v],"'", "='", $fmtChar, "'", $terminator;
			}
		}
	#chomp($contents);
	#chop($contents);
	$contents =~ s/,\n$//;
	print $wh $contents , ');',"\n\n";
	
}


sub printReadTable{
	my $self = shift;
	my $optn ={ @_ };
	my $varname;
	if ($self->{unsafeVarName}){
		$varname = $self->{_varNameAsafe};
	} else {
		$varname = $self->{_varNameA};
	}
	

	my $wh = $self->{WH};
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};
	my @fltmp = split /\./, $self->{TABFILE};
	#my $RtmpData = $self->{TABFILE} .'.' . ($i+1) . '.tab' ;
	my $RData = $self->{WRKDIR} . '/t.' . $fltmp[1] . '.' . ($i+1) . '.tab' ;
	print $wh "x<-read.table141vdc(file=\"$RData\",col.names=c(\n\"",join("\",\n\"", @$varname[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]]), "\"),colClassesx=vartyp,varFormat=varFmt)\n\n";
}

sub printRsafe2rawList{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $varNameHsafe2raw = $self->{RsafeVarName2raw};
	print $wh "attr(x, \"Rsafe2raw\")<-list(\n";
	my $kys = [keys(%{$varNameHsafe2raw})];
	my $lv  = scalar(@{$kys});
	for (my $i=0; $i<$lv; $i++ ) {
		my $value = $varNameHsafe2raw->{$kys->[$i]};
		if ($i < ($lv -1)){
			print $wh "'" . $kys->[$i] . "'='" .  $value . "',\n";
		} else {
			print $wh "'" . $kys->[$i] . "'='" .  $value . "'";

		}
	}
	print $wh  ")\n\n";
}

sub printSubset{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};
	my $DEBUG;
	print $wh '# subsetting: eliminating rows' . "\n";
	
	if (exists($self->{'_newVarSpec'})){
	# note: for subsetting, the current GUI requires a new
	# user-defined var to subset
		foreach my $var (@{$self->{'_newVarNameSetA'}}){
			my $vt = $self->{_newVarSpec}->{$var}->{'type'};
			while (my ($k,$y) = each ( %{$self->{_newVarSpec}->{$var}->{'valueTable'}})){
				print "new value(k)=",$k,"\n" if $DEBUG;
				my $condition="";
				
				my $baseVar = $self->{_newVarNameSetH}->{$var};
				my $baseVarSafe = $baseVar;
				if ($self->{unsafeVarName}){
					$baseVarSafe= $self->getSafeVarName($baseVar);
				}
				
				my $yy = $y->{$baseVar};
				print "setting=", Dumper($yy) if $DEBUG;

				if ($k eq "eteleD") {
					# subsetting
					# subset(x, (conditions))

					my $vn ='x[["'. $baseVarSafe . '"]]';

					$condition .= "subset(x, ";

					my $fc = &printValueRange($yy, $vn, "eteleD", $vt);

					print $wh "x <- subset(x, ( " . $fc . " ))\n\n";
				}

			}
		}
	
	}

}

sub getSafeVarName{
	my $self=shift;
	my $tmp = shift;
	my $baseVarSafe;
	my $varName;
	if (ref($tmp)){
		$varName = $$tmp;
		$baseVarSafe = $$tmp;
	} else {
		$varName = $tmp;
		$baseVarSafe = $varName;
	}
	for (my $i=0; $i< @{$self->{_varNameA}}; $i++) {
		if ($self->{_varNameA}->[$i] eq $varName){
			if ($self->{_varNameA}->[$i] ne $self->{_varNameAsafe}->[$i]){
				$baseVarSafe = $self->{_varNameAsafe}->[$i];
			}
		}
	}
	return $baseVarSafe;
}


sub printUserDefinedVars{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};
	my $DEBUG;
	print $wh '# recoding: user-defined variables' . "\n";
	if (exists($self->{'_newVarSpec'})){
		foreach my $var (@{$self->{'_newVarNameSetA'}}){
			my $vt = $self->{_newVarSpec}->{$var}->{'type'};
			my $cnt =0;
			while (my ($k,$y) = each ( %{$self->{_newVarSpec}->{$var}->{'valueTable'}})){
				print "new value(k)=",$k,"\n" if $DEBUG;
				my $condition="";
				my $baseVar = $self->{_newVarNameSetH}->{$var};
				my $yy = $y->{$baseVar};
				print "setting=", Dumper($yy) if $DEBUG; 
				if ($k ne "eteleD") {
					$cnt++;
					
					my $baseVarSafe = $baseVar;
					if ($self->{unsafeVarName}){
						$baseVarSafe= $self->getSafeVarName($baseVar);
					}
					my $varSafe = $var;
					if (exists($self->{_newVarNameHsafe}->{$var})){
						$varSafe = $self->{_newVarNameHsafe}->{$var};
					}
					# recoding
					my $vn ='x[["'. $varSafe . '"]]';
					my $vo ='x[["'. $baseVarSafe . '"]]';
					# step 1
					# xx[["nv"]]<-NA
					if ($cnt == 1){
						print $wh $vn . '<- NA' . "\n";
					}
					# setp 2
					# x[["nv"]][ (x[["NATINT"]] == 4 ) | () | ...  ] <- 34
					my $cnd = &printValueRange($yy, $vo, "recode", $vt);
					if ($vt){
						print $wh $vn . '[' . $cnd . '] <- '. $k ."\n";
					} else {
						if ($k eq 'NA'){
							print $wh $vn . '[' . $cnd . '] <- '. $k ."\n";
						} else {
							print $wh $vn . '[' . $cnd . '] <- "'. $k .'"' ."\n";
						}
					}
				}
			}
			print $wh "\n";
		}
	}
}


sub printUpdateVartyp{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};
	my $DEBUG;
	print $wh '# update variable type' . "\n";

	print $wh 'vartyp <- append(vartyp, ' . "\n";
	my $size = 0;
	if (exists($self->{'_newVarNameSetA'})){
		$size = scalar(@{$self->{'_newVarNameSetA'}});
	}

	if ($size){
		for (my $i=0; $i< $size; $i++){
			my $var = $self->{'_newVarNameSetA'}->[$i];
			my $vt = $self->{_newVarSpec}->{$var}->{'type'};
			if ($i == 0){
				print $wh 'c(' . $vt . "\n";
			} else {
				print $wh ', ' . $vt . "\n";
			}

			if ($i == ($size -1)){
				print $wh  "))\n";
			}
		}
		print $wh "\n";
		if ($size){
			print $wh '# re-attach the variable type to the data.frame after subsetting'."\n";
			print $wh 'attr(x, "var.type") <- vartyp' . "\n\n";
		}
	}
}

###############################################################################
sub printValueRange{
	my $rangeSet=shift;
	my $variableName=shift;
	my $type=shift;
	my $vtype = shift;
	my $DEBUG=0;
	if (!defined($vtype)){
		print "variable type undefined:\n" if $DEBUG;
		$vtype=1;
	}
	my $sep =' | ';
	if ($type eq "eteleD"){
		$sep = ' & ';
	}
	print "range received:\n", Dumper($rangeSet) if $DEBUG;
	print "variable type =", $vtype, "\n"  if $DEBUG;
	my $finalCondition="";
	for (my $i=0; $i<@$rangeSet; $i++){
	
		print $i,"-th set=\n",Dumper($rangeSet->[$i]) if $DEBUG;
		#print $rangeSet->[$i]->[1] . "\t" . $rangeSet->[$i]->[3] ."\n";
		my $condition="";
		
		if (($rangeSet->[$i]->[1] eq $rangeSet->[$i]->[3]) && ($rangeSet->[$i]->[0]== 3) && ($rangeSet->[$i]->[2] ==4)) {
			
			# point type
			if ($vtype){
				$condition =  '(' . $variableName .' == ' .  $rangeSet->[$i]->[1] . ")";
			} else {
				$condition =  '(' . $variableName ." == '" .  $rangeSet->[$i]->[1] . "')";
			}
			if ($type eq "eteleD"){
				$condition = ' !'. $condition;
			} else {
				$condition =  ' '. $condition ;
			}
			print $i, "-th condition point:",$condition,"\n" if $DEBUG;
			
		} elsif ($rangeSet->[$i]->[0]==2) {
			# point negation
			if ($vtype){
				$condition =  '(' . $variableName .' != ' .  $rangeSet->[$i]->[1] . ")";
			} else {
				$condition =  '(' . $variableName ." != '" .  $rangeSet->[$i]->[1] . "')";
			}
			
			if ($type eq "eteleD"){
				$condition = ' !'. $condition;
			} else {
				$condition =  ' '. $condition ;
			}

			print $i, "-th condition point(negation):",$condition,"\n" if $DEBUG;
			
		} else {
			# range type
			my ($conditionL, $conditionU);
			
			if (($rangeSet->[$i]->[0] ==5) && ($rangeSet->[$i]->[1] eq '')){
				$conditionL = '';
				
			} else {
				$conditionL = '('. $variableName  . ' ' . $rangeOpMap->{$rangeSet->[$i]->[0]} . ' ' . $rangeSet->[$i]->[1] . ')';
			}
			
			print $i,"-th condition(Lower/upper bounds)=" . $conditionL . " " if $DEBUG;
			
			if (($rangeSet->[$i]->[2] == 6) && ($rangeSet->[$i]->[3] eq '')){
			
				$conditionU = '';
				
			} else {
				my $andop='';
				
				if ($conditionL){
					$andop=' & ';
				}
				
				$conditionU =  $andop . '('. $variableName  . ' ' .  $rangeOpMap->{$rangeSet->[$i]->[2]} . ' ' . $rangeSet->[$i]->[3] . ')';
			}
			
			print  $conditionU, "\n"  if $DEBUG;
			
			$condition = $conditionL . " " . $conditionU;
			
			if ($type eq "eteleD"){
				$condition = ' !'. $condition;
			} else {
				$condition =  ' '. $condition ;
			}

		}
		print $i, "-th ", $condition, "\n" if $DEBUG;
		
		if ($i < (@$rangeSet -1) ) {
			$finalCondition .= $condition . $sep ;
		} else {
			$finalCondition .= $condition;
		}
	}
	print "final condition:\n", $finalCondition,"\n\n" if $DEBUG;
	return $finalCondition;
}






sub printVarNo{
	my $self = shift;
	my $optn ={ @_ };
	my $varNo = $self->{_varNo};
	my $wh = $self->{WH};
	my $varRangeSet = $self->{VarRangeSet};
	if (0){
		foreach my $el (@{$self->{_varNo}}) {
			my ($tmp0, $tmp1) = split (/\./, $el);
			$el = $tmp1;
		}
	}
	my $i = $optn->{iteration};
	my @tmp = @$varNo[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]];
	if ($self->hasNewVarNames()){
		#for (my $j=0;$j<$size;$j++){
		#	my $vn = "udv" . $j;
		#	push @tmp, $vn;
		#}
		for (my $j=0;$j<$self->hasNewVarNames();$j++){
			my $var = $self->{'_newVarNameSetA'}->[$j];
			while (my ($k,$y) = each ( %{$self->{newVarId2VarName}})){
				if ($var eq $y){
					push @tmp, $k;
				}
			}
		}
	}
	print $wh "attr(x, \"var.nmbr\")<-c(\n" . '"' . join("\",\n\"", @tmp ) . '"' . ")\n\n";
	
	#print $wh "attr(x, \"var.nmbr\")<-c(\n" . '"' . join("\",\n\"", @$varNo[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]]) . '"' . ")\n\n";
	#print $wh "attr(x, \"var.nmbr\")<-c(\n", join(",\n", @$varNo[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]]), ")\n\n";
}

sub printVarLabel{
	my $self = shift;
	my $optn ={ @_ };
	my $varLabelraw = $self->{_varLabel};
	my $varLabel = $self->sanitizeVarLabel4Rcode();
	my $wh = $self->{WH};
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};
	my @tmp = @$varLabel[$varRangeSet->[$i][0]..$varRangeSet->[$i][1]];
	# variable labe may not be supplied by a user
	my $size=0;
	if (exists($self->{'_newVarNameSetA'})){
		$size = scalar(@{$self->{'_newVarNameSetA'}});
	}
	if ($size){
		for (my $j=0;$j<$size;$j++){
			my $var = $self->{'_newVarNameSetA'}->[$j];
			my $vl = $self->{_newVarSpec}->{$var}->{label};
			if ($vl){
				push @tmp, $vl;
			} else {
				push @tmp, "Label not available for $var";
			}
		}
	}
	print $wh "attr(x, \"var.labels\")<-c(\n" . '"' . join("\",\n\"", @tmp) . '"' . ")\n\n";
}

sub sanitizeVarLabel4Rcode{
	my $self = shift;
	my $optn = {@_ };
	my $varLabel = $self->{_varLabel};
	for (my $i=0; $i<@$varLabel; $i++){
		$varLabel->[$i] =~ s/\042/\047/g; # replace "" with '
	}
	#print Dumper($varLabel);
	return $varLabel; 
}

sub printValueTable{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $varname = $self->{_varNameA};
	my $varType = $self->{_varType};
	my $vtbl = $self->{_valLblTbl};
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};
	
	# for existing variables
	my $ll;
	for (my $l=$varRangeSet->[$i][0];$l<=$varRangeSet->[$i][1];$l++) {
		if (exists($vtbl->{$varname->[$l]})){
			print $wh "VALTABLE[[\"",($l+1),"\"]]<-list(\n";
			for (my $j=0;$j<@{$vtbl->{$varname->[$l]}};$j++){
				#if ($vtbl->{$varname->[$l]}->[$j][1]){
					my $safeValueLabel = $vtbl->{$varname->[$l]}->[$j][1]; 
					$safeValueLabel =~ s/\042/\047/g; # replace "" with '
					print $wh "\"",$vtbl->{$varname->[$l]}->[$j][0],"\"=\"",$safeValueLabel,"\"";
				#}
				if ($j < (scalar(@{$vtbl->{$varname->[$l]}})-1) ){
					print $wh ",\n";
				}
			}
			print $wh ")\n\n";
		}
		$ll=$l;
	}
	$ll++;
	
	# for user-defined variables
	my $DEBUG=0;
	my $size= 0;
	if (exists($self->{'_newVarNameSetA'})){
		$size=scalar(@{$self->{'_newVarNameSetA'}});
	}
	if ($size){
		my $udvltable = $self->getUserDefinedValueTable();
		print '# ' .  "user-defined value-label table:\n", Dumper($udvltable) if $DEBUG;
		$self->{_udValLblTbl}=$udvltable;
		my $nokys = scalar(keys(%$udvltable));
		if ($size && $nokys){
			foreach my $var (@{$self->{'_newVarNameSetA'}}){
				print $wh "VALTABLE[[\"",($ll+1),"\"]]<-list(\n";
				for (my $j=0;$j<@{$udvltable->{$var}};$j++){

					print $wh "\"",$udvltable->{$var}->[$j][0],"\"=\"",$udvltable->{$var}->[$j][1],"\"";
					if ($j < (scalar(@{$udvltable->{$var}})-1) ){
						print $wh ",\n";
					}
				}
				print $wh ")\n\n";
				$ll++;
			}
		}
	}
}

sub getUserDefinedValueTable{
	my $self = shift;
	my $optn ={ @_ };
	my $DEBUG=0;
	my $vltable={};
	
	if (exists($optn->{varName})){
		# requested var's value table only
		my $rqstdVar = $optn->{varName};
		if (exists($self->{_newVarNameSetH}->{$rqstdVar})){
			my $cntr=0;
			while (my ($k,$y) = each ( %{$self->{_newVarSpec}->{$rqstdVar}->{'valueTable'}})){
				print "new value(k)=",$k,"\n" if $DEBUG;
				if ($k ne "eteleD") {
					$cntr++;
					if ($cntr == 1){
						$vltable->{$rqstdVar}=[];
					}
					push @{$vltable->{$rqstdVar}}, [ $k, $y->{label} ];
				}
			}
		}
	} else {
		# user-defined var's value-tables
		my $size= 0;
		if (exists($self->{'_newVarNameSetA'})){
			$size = scalar(@{$self->{'_newVarNameSetA'}});
		}
		if ($size){
			foreach my $var (@{$self->{'_newVarNameSetA'}}){
				my $cntr=0;
				while (my ($k,$y) = each ( %{$self->{_newVarSpec}->{$var}->{'valueTable'}})){
					print "new value(k)=",$k,"\n" if $DEBUG;
					if ($k ne "eteleD") {
						$cntr++;
						if ($cntr == 1){
							$vltable->{$var}=[];
						}
						push @{$vltable->{$var}}, [ $k, $y->{label} ];
					}
				}
			}
		}
	
	} # all case
	
	return $vltable;
}




sub printUserDefinedMissValues{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $varname = $self->{_varNameA};
	my $varType = $self->{_varType};
	my $vtbl = $self->{_valLblTbl};
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};

}
sub printMissValueDsc{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $varname = $self->{_varNameA};
	my $varType = $self->{_varType};
	
	
	my $mvtbl = $self->{_mssvlTbl};
	my $mvTpCdTbl= $self->{_mvTpCdTbl};
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};
	#print "miss value table\n:",Dumper($mvtbl),"\n"; 
	#print "miss value code table\n:",Dumper($mvTpCdTbl),"\n"; 
	
	
	# existing vars
	for (my $l=$varRangeSet->[$i][0];$l<=$varRangeSet->[$i][1];$l++) {
		if (exists($mvtbl->{$varname->[$l]})){
			if ($varType->[$l]==1){
				unless ($mvTpCdTbl->{$varname->[$l]} == -2){
					print $wh "MSVLTBL[[\"",($l+1),"\"]]<-c(";
					my $mvCode;
					$mvCode=&getMVstring($mvtbl, $mvTpCdTbl, $varname->[$l]);
					#print "$l=",join(",", @{$mvCode}),"\n";
					#print $wh join(',',@{$mvtbl->{$varname->[$l]}->[0]});
					print $wh join(',',@{$mvCode});
					print $wh ")\n";
				} else {
				
					print $wh "x[[", $l+1, "]][ (x[[", $l+1, "]] >= ",$mvtbl->{$varname->[$l]}->[0][0],") & (x[[", $l+1, "]] <= ",$mvtbl->{$varname->[$l]}->[0][1]," )]<- NA\n";

				}
			} elsif ($varType->[$l]==0) {
				print $wh "MSVLTBL[[\"",($l+1),"\"]]<-c(";
				print $wh '"' . join('","',@{$mvtbl->{$varname->[$l]}->[0]} ) . '"';
				print $wh ")\n";
			}
		}
	}
	
	# user-defined vars
}

sub getMVstring{
	my $mssvlTbl=shift;
	my $mvTpCdTbl=shift;
	my $vari=shift;
	my $mvset = $mssvlTbl->{$vari};
	my $code  = $mvTpCdTbl->{$vari};
	my $mvstring;
	if ($code > 0){
		# points 
		#$mvstring= join(',',@{$mvset});
		$mvstring=[];
		foreach my $tmp (@{$mvset}){
			push @{$mvstring}, $tmp->[0];
		}
	} elsif ($code == -2){
		#print "no point-type missing values\n";
	} elsif ($code == -3){
		if (scalar(@{$mvset->[0]}) == 1 ){
			$mvstring = $mvset->[0];
		} elsif (scalar(@{$mvset->[1]}) == 1) {
			$mvstring = $mvset->[1];
		}
		
	}
	# print "\t\tmvstring=",$mvstring,"\n";
	return $mvstring;
}

sub printMissValueCntn{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $varname = $self->{_varNameA};
	my $varType = $self->{_varType};
	my $mvtbl = $self->{_mssvlTbl};
	my $mvTpCdTbl= $self->{_mvTpCdTbl};
	my $varRangeSet = $self->{VarRangeSet};
	my $i = $optn->{iteration};
	for (my $l=$varRangeSet->[$i][0];$l<=$varRangeSet->[$i][1];$l++) {
		if (($varType->[$l]==2) && (exists($mvtbl->{$varname->[$l]}))) {
			#foreach my $j (@{$mvtbl->{$varname->[$l]}}) {
			
				if ($mvTpCdTbl->{$varname->[$l]}>0){
					foreach my $ll (@{$mvtbl->{$varname->[$l]}}){
						print $wh "x[[",$l+1,"]][ x[[",$l+1,"]]==",$ll->[0]," ]<-NA\n";
					}
				} elsif ($mvTpCdTbl->{$varname->[$l]} == -2){
					#$mvCode=&getMVstring($mvtbl, $mvTpCdTbl, $vari);

					print $wh "x[[", $l+1, "]][ (x[[", $l+1, "]] >= ",$mvtbl->{$varname->[$l]}->[0][0],") & (x[[", $l+1, "]] <= ",$mvtbl->{$varname->[$l]}->[0][1]," )]<- NA\n";
					#print "$vari=",join(",", @{$mvCode}),"\n";
				} elsif ($mvTpCdTbl->{$varname->[$l]} == -3){
					if (scalar(@{$mvtbl->{$varname->[$l]}->[0]}) == 2){
						print $wh "x[[",$l+1,"]][ ((x[[",$l+1,"]] >= ",$mvtbl->{$varname->[$l]}->[0][0],") & (x[[",$l+1,"]] <= ",$mvtbl->{$varname->[$l]}->[0][1]," )) | (x[[",$l+1,"]] == ",$mvtbl->{$varname->[$l]}->[1][0]," )]<- NA\n";
					} elsif (scalar(@{$mvtbl->{$varname->[$l]}->[0]}) == 1){
						print $wh "x[[",$l+1,"]][ ((x[[",$l+1,"]] >= ",$mvtbl->{$varname->[$l]}->[1][0],") & (x[[",$l+1,"]] <= ",$mvtbl->{$varname->[$l]}->[1][1]," )) | (x[[",$l+1,"]] == ",$mvtbl->{$varname->[$l]}->[0][0]," )]<- NA\n";
					}
				} else {
					#print "$l-th iteration: none of the above\n";
				}
			
			
			
				#print $wh "x[[",($l+1),"]][ x[[",($l+1),"]]==",$j->[0]," ]<-NA\n";
			#}
		}
	}
}

sub printAoption{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $aolst=join(',', @{$self->{statis}->{aopdip}});
	print $wh "aol<-c($aolst)\n";
}

sub printDoption{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $dtdwnldf="";
	if ($self->{process} eq 'D') {
		$dtdwnldf=$self->{dwnld}->{dtdwnld};
	}
	print $wh "dol<-\"$dtdwnldf\"\n";
}

sub printXtabCellstat{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	# cellstatvl<-c(1,1,1,1,1)
	# default: freq only, i.e., cellstatvl<-c(1,0,0,0,0)
	unless (exists($self->{xtab}->{cellStat})){
		$self->{xtab}->{cellStat}=[1,0,0,0,0];
	}
	my $cellstat = join(',',@{$self->{xtab}->{cellStat}});
	print $wh "cellstatvl<-c($cellstat)\n";
}

sub printXtabContrlvars{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	#print $wh "contrlvars<-c(\n", join(",\n", @{$self->{xtab}->{cntlvars}}), ")\n\n";
	if ( (exists($self->{xtab}->{cntlvars})) && (scalar(@{$self->{xtab}->{cntlvars}})) ){
		print $wh "contrlvars<-c(\n" . '"' . join("\",\n\"", @{$self->{xtab}->{cntlvars}} ) . '"' . ")\n\n";
	} else {
		print $wh "contrlvars<-c();\n";
	}
}

sub printXtabClmnvars{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	#print $wh "clmnvars<-c(\n", join(",\n", @{$self->{xtab}->{colmvars}}), ")\n\n";
	 print $wh "clmnvars<-c(\n" . '"' . join("\",\n\"", @{$self->{xtab}->{colmvars}} ) . '"' . ")\n\n";
}

sub printXtabRwvars{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	#print $wh "rwvars<-c(\n", join(",\n", @{$self->{xtab}->{rowvars}}), ")\n\n";
	 print $wh "rwvars<-c(\n" . '"' . join("\",\n\"", @{$self->{xtab}->{rowvars}} ) . '"' . ")\n\n";

}

sub printXtabFrstpstn{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	#print $wh "frstpstn<-c(\n", join(",\n", @{$self->{xtab}->{p1stRvar}}), ")\n\n";
	if ( (exists($self->{xtab}->{p1stRvar})) && (scalar(@{$self->{xtab}->{p1stRvar}})) ){
		print $wh "frstpstn<-c(\n" . '"' . join("\",\n\"", @{$self->{xtab}->{p1stRvar}} ) . '"' . ")\n\n";
	} else {
		print $wh "frstpstn<-c();\n";
	}
}

sub printXtabScndpstn{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	#print $wh "scndpstn<-c(\n", join(",\n", @{$self->{xtab}->{p2ndRvar}}), ")\n\n";
	if ( (exists($self->{xtab}->{p2ndRvar})) && (scalar(@{$self->{xtab}->{p2ndRvar}})) ){
		print $wh "scndpstn<-c(\n" . '"' . join("\",\n\"", @{$self->{xtab}->{p2ndRvar}} ) . '"' . ")\n\n";
	} else {
		print $wh "scndpstn<-c();\n";
	}
}

sub printXtabThrdpstn{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	#print $wh "thrdpstn<-c(\n", join(",\n", @{$self->{xtab}->{p3rdRvar}}), ")\n\n";
	if ( (exists($self->{xtab}->{p3rdRvar})) && (scalar(@{$self->{xtab}->{p3rdRvar}})) ){
		print $wh "thrdpstn<-c(\n" . '"' . join("\",\n\"", @{$self->{xtab}->{p3rdRvar}} ) . '"' . ")\n\n";
	} else {
		print $wh "thrdpstn<-c();\n"; 
	}
}

sub printXtab{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $htmlfl = $optn->{HTMLFL};
	my $ochi; 
	if ( exists($self->{xtab}->{nestoptn})  && ($self->{xtab}->{nestoptn} eq 'TRUE')) {
		$ochi="TRUE";
	} else {
		$ochi="FALSE";
	}
	print $wh "try(crossTabulation(x=x, colmnvars=clmnvars, rowvars=rwvars, firstpstn=frstpstn, secndpstn=scndpstn, thirdpstn=thrdpstn, cntrlvars=contrlvars, tmphtmlfile=\"$htmlfl\", OPCHISQ=$ochi, cellstat=cellstatvl))\n";
}


sub printVDCxtabs{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $htmlfl = $optn->{HTMLFL};
	my @varName;
	

	foreach my $el (@{$self->{xtab}->{classVars}}){
		# here $el is id
		if ( exists($self->{_varNameHsafe}->{$el}) ){
			push @varName, $self->{_varNameHsafe}->{$el};
		} elsif ( exists($self->{_newVarNameHsafe}->{ $self->{newVarId2VarName}->{$el} } ) ) {
			push @varName, $self->{_newVarNameHsafe}->{ $self->{newVarId2VarName}->{$el} } ;
		} elsif (exists($self->{_varNameH}->{$el})) {
			push @varName, $self->{_varNameH}->{$el};
		} elsif ( exists($self->{newVarId2VarName}->{$el}) ) {
			push @varName, $self->{newVarId2VarName}->{$el};
		}
	}
	
	my $freqVarString ="";
	if (exists($self->{xtab}->{freqVar}) && $self->{xtab}->{freqVar} ne ""){
		if (exists($self->{_varNameHsafe}->{ $self->{xtab}->{freqVar}})){
			$freqVarString = "freqVars=(\"" .  $self->{_varNameHsafe}->{ $self->{xtab}->{freqVar}} . "\")";
		
		} else {
			$freqVarString = "freqVars=(\"" .  $self->{_varNameH}->{ $self->{xtab}->{freqVar}} . "\")";
		}
	}
	
	my $xtabWantString ="wantPercentages=$self->{xtab}->{Percentages}, wantTotals=$self->{xtab}->{Totals}, wantStats=$self->{xtab}->{Statistics}, wantExtraTables=$self->{xtab}->{ExtraTables}";

	#my $cmndLine =  "library(VDCutil);\ntry(VDCcrossTabulation(HTMLfile=\"$htmlfl\",\ndata=x,\nclassificationVars=c(\"" .  join('","', (@varName)) . '")' ;
	my $cmndLine =  "library(VDCutil);\ntry(VDCcrossTabulation(HTMLfile=\"$htmlfl\",\ndata=x,\nclassificationVars=c('" .  join("',\n'", (@varName)) . "')" ;
	
	if ($freqVarString) {
		$cmndLine  .= ",\n " . $freqVarString ;
	}
	
	$cmndLine .= ",\n " . $xtabWantString . "));\n";
	
	print $wh $cmndLine;

}


sub printUnivarStat{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	print $wh "try(x<-univarStat(dtfrm=x))\n";
}

sub printUnivarChart{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $imgflprfx = $optn->{IMGFLPRFX};
	my $withZlg = $optn->{STANDALONE};
	print $wh "try({x<-univarChart(dtfrm=x,\nanalysisoptn=aol,\nimgflprfx=\"$imgflprfx\",\nstandalone=$withZlg)})\n";
}

sub printUnivarStatHtml{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $srvrcgi = $optn->{SRVRCGI};
	my $htmlfl = $optn->{HTMLFL};
	my $withZlg = $optn->{STANDALONE};
	print $wh "try(univarStatHtml(dtfrm=x,\ntmpimgfile=\"$srvrcgi\",\nanalysisoptn=aol,\ntmphtmlfile=\"$htmlfl\",\nstandalone=$withZlg))\n";
}
		
sub printUnivarDataDwnld{
	my $self = shift;
	my $optn ={ @_ };
	my $wh = $self->{WH};
	my $dwnldprfx = $optn->{DWNLDPRFX};
	print $wh "univarDataDwnld(dtfrm=x,dwnldoptn=dol,dsnprfx=\"$dwnldprfx\")\n";
}

sub setMVtypeCode{
	my $self = shift;
	my $optn = shift;
	my $mvtbl = $self->{_mssvlTbl};
	my $mvrngon;
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
	my $elsize=[];
	while ( my ($ky, $vl) = each %{$mvtbl} ) {
		$mvrngon=0;
		my $vlsize = scalar(@{$vl});
		if ($DEBUG) { print $FH "key=",$ky,"\tsize=",$vlsize,"\n";}
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
		
		if ($DEBUG) {print $FH "range type?(1:yes, 0:no)=", $mvrngon,"\n";}
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
	
	$self->{_mvTpCdTbl}=$mvtypcd;
}

# new methods added Fall 2005
sub getCaseSelectConditions{
	my $rangeSet=shift;
	my $variableName=shift;
	my $type=shift;
	my $vtype = shift;
	my $DEBUG=0;
	if (!defined($vtype)){
		print "variable type undefined:\n" if $DEBUG;
		$vtype=1;
	}
	my $sep =' | ';
	if ($type eq "eteleD"){
		$sep = ' AND ';
	}
	print "range received:\n", Dumper($rangeSet) if $DEBUG;
	print "variable type =", $vtype, "\n"  if $DEBUG;
	my $finalCondition="";
	
	for (my $i=0; $i<@$rangeSet; $i++){
	
		print $i,"-th set=\n",Dumper($rangeSet->[$i]) if $DEBUG;
		#print $rangeSet->[$i]->[1] . "\t" . $rangeSet->[$i]->[3] ."\n";
		my $condition="";
		
		if (($rangeSet->[$i]->[1] eq $rangeSet->[$i]->[3]) && ($rangeSet->[$i]->[0]== 3) && ($rangeSet->[$i]->[2] ==4)) {
			
			# point type
			my $op=' = ';
			if ($type eq "eteleD"){
				$op = ' != ';
			}
			
			if ($vtype){
				$condition =  '(' . $variableName . $op .  $rangeSet->[$i]->[1] . ")";
			} else {
				$condition =  '(' . $variableName . $op . "'" .  $rangeSet->[$i]->[1] . "')";
			}
			print $i, "-th condition point:",$condition,"\n" if $DEBUG;
			
		} elsif ($rangeSet->[$i]->[0]==2) {
			# point negation
			my $op = ' != ';
			if ($type eq "eteleD"){
				$op = ' = '. $condition;
			}
			if ($vtype){
				$condition =  '(' . $variableName . $op .  $rangeSet->[$i]->[1] . ")";
			} else {
				$condition =  '(' . $variableName . $op . "'" .  $rangeSet->[$i]->[1] . "')";
			}
			
			print $i, "-th condition point(negation):",$condition,"\n" if $DEBUG;
			
		} else {
			# range type
			# usually numerical type
			my @cL=();
			my @cU=();
			
			my ($conditionL, $conditionU);
			
			if (($rangeSet->[$i]->[0] ==5) && ($rangeSet->[$i]->[1] eq '')){
				#$conditionL = '';
			} else {
				#$conditionL = '('. $variableName  . ' ' . $rangeOpMap->{$rangeSet->[$i]->[0]} . ' ' . $rangeSet->[$i]->[1] . ')';
				
				@cL = ('('. $variableName  . ' ', $rangeOpMap->{$rangeSet->[$i]->[0]}, ' ' . $rangeSet->[$i]->[1] . ')');
			}
			
			#print $i,"-th condition(Lower bound)=" . $conditionL . " " if $DEBUG;
			print $i,"-th condition: Lower only=" . join(', ', @cL) . "\n" if $DEBUG;
			
			if (($rangeSet->[$i]->[2] == 6) && ($rangeSet->[$i]->[3] eq '')){
				#$conditionU = '';
			} else {
				#my $andop='';
				#if ($conditionL){
				#	$andop=' AND ';
				#}
				#$conditionU =  $andop . '('. $variableName  . ' ' .  $rangeOpMap->{$rangeSet->[$i]->[2]} . ' ' . $rangeSet->[$i]->[3] . ')';
				
				@cU = ( '('. $variableName  . ' ', $rangeOpMap->{$rangeSet->[$i]->[2]}, ' ' . $rangeSet->[$i]->[3] . ')');
			}
			
			#print  $conditionU, "\n"  if $DEBUG;
			print  $i,"-th condition: upper only =" . join(', ', @cU) . "\n\n" if $DEBUG;
			my $relation="";
			my $conditionType = 0;
			if (scalar(@cL) && scalar(@cU)){
				# both
				$conditionType = 1;
				print "This case is range type \n" if $DEBUG;
				if ( ($rangeSet->[$i]->[0] == 3 || $rangeSet->[$i]->[0] == 5) && ( $rangeSet->[$i]->[2] == 4 || $rangeSet->[$i]->[2] == 6 )  ) {
					# current: AND case 
					print "AND case\n" if $DEBUG;
					# operator
					$relation = "AND";
					
					if ($type eq "eteleD"){
						# AND => OR
						print "delete:reverse from AND to OR\n" if $DEBUG;
						$relation = "OR";
						# reverse the operators
						$cL[1] =$rangeOpMapRx->{$cL[1]} ;
						$cU[1] =$rangeOpMapRx->{$cU[1]} ;
					}
				} elsif ( ($rangeSet->[$i]->[0] == 6 || $rangeSet->[$i]->[0] == 4) && ( $rangeSet->[$i]->[2] == 5 || $rangeSet->[$i]->[2] == 3 )  ){
					# current: OR case
					print "OR case\n" if $DEBUG;
					# operator
					$relation = "OR";
					if ($type eq "eteleD"){
						# OR => AND
						print "delete:reverse from OR to AND\n" if $DEBUG;
						$relation = "AND";
						$cL[1] =$rangeOpMapRx->{$cL[1]} ;
						$cU[1] =$rangeOpMapRx->{$cU[1]} ;
					}
					
				} else {
					# possible?
				}
				
			} elsif ( (scalar(@cL) == 0)  && scalar(@cU) ) {
				# no lower condition
				
				if ($type eq "eteleD"){
					$cU[1] =$rangeOpMapRx->{$cU[1]} ;
				}

			} elsif ( scalar(@cL) && (scalar(@cU)== 0) ){
				# no upper condition
				if ($type eq "eteleD"){
					$cL[1] =$rangeOpMapRx->{$cL[1]} ;
				}
			}
			
			my $tmpL = join("", @cL);
			my $tmpU = join("", @cU);
			print "lower/upper=", $tmpL .'/' . $tmpU, "\n" if $DEBUG;
			if ($conditionType){
				$condition = '('. join(' ', ($tmpL, $relation, $tmpU)) .  ')';
			} else {
				print "tmpL=" . $tmpL. "<=\n" if $DEBUG;
				print "tmpU=" . $tmpU. "<=\n" if $DEBUG;
				print "relation=" . $relation. "<=\n" if $DEBUG;
				$condition = join('', ($tmpL, $relation, $tmpU)) ;
			}
			print "\n\nBoth conditions:",$condition, "\n\n" if $DEBUG;
			
		}
		print $i, "-th ", $condition, "\n" if $DEBUG;
		
		if ($i < (@$rangeSet -1) ) {
			$finalCondition .= $condition . $sep ;
		} else {
			$finalCondition .= $condition;
		}
	}
	print "final condition:\n", $finalCondition,"\n\n" if $DEBUG;
	return $finalCondition;
}

sub getCaseSelectionCriteria{
	my $self = shift;
	my $optns = {@_};
	my $DEBUG=0;


	my $nv = $self->{_newVarSpec}; # keys are user-defined vars
	my $h = $self->{_varNameH};
	
	my $caseSbstParam="";
	if (exists($self->{_newVarNameSetA})){
		my $cntr=0;
		# case-wise subsetting
		my $endcnt = scalar(@{$self->{'_newVarNameSetA'}});
		foreach my $var (@{$self->{'_newVarNameSetA'}}){
			$cntr++;
			my $vt = $self->{_newVarSpec}->{$var}->{'type'};
			my $condition="";
			while (my ($k,$y) = each ( %{$self->{_newVarSpec}->{$var}->{'valueTable'}})){
				print "new value(k)=",$k,"\n" if $DEBUG;
				
				my $baseVar = $self->{_newVarNameSetH}->{$var};
				my $baseVarSafe = $baseVar;
				if ($self->{unsafeVarName}){
					$baseVarSafe= $self->getSafeVarName($baseVar);
				}
				
				my $yy = $y->{$baseVar};
				print "setting=", Dumper($yy) if $DEBUG;

				if ($k eq "eteleD") {
					# subsetting

					my $vn = $baseVarSafe;

					$condition.= &getCaseSelectConditions($yy, $vn, "eteleD", $vt);
					print   $condition . "\n" if $DEBUG;

				}
			
			}
			my $prefix=' AND ';
			if ($cntr == 1){
				$prefix ="";
			}
			if ($condition){
				$caseSbstParam .= $prefix .  $condition;
			}
		}
	
	}
	
	
	
	
	
		
		#" AND (", $h->{'v1.3'}," >=",$r->{'v1.3'},") AND (", v1.3 <=2.17) .
		# AND (v1.13 >= 1) AND (v1.13 <= 5)
		# AND (v1.10 !='97') AND (v1.10 !='98') AND (v1.10 !='99') AND (v1.10 !=' ')
		# AND (v1.13 !=7) AND (v1.13 !=8) AND (v1.13 !=9)
		# AND (v1.22 !=7) AND (v1.22 !=8) AND (v1.22 !=9) AND (v1.22 !=0)
		# AND (v1.40 !=7) AND (v1.40 !=8) AND (v1.40 !=9) AND (v1.40 !=0) AND (v1.40 >=1) AND (v1.40 <=3)
		# )

	# terminator
	
	#$rowSbstParam = 'rows=(' . $rowSbstParam . ')';
	print "final:\n",$caseSbstParam,"\n" if $DEBUG;
	return $caseSbstParam;
}

sub getRowSelectionCriteria {
	my $self = shift;
	my $optns = {@_};
	my $DEBUG=0;

	my $r = $self->{rwsbst};
	my $h = $self->{_varNameH};
	
	my $rowSbstParam = "(firstObs=" . $r->{'RNGMIN_0'} . ") AND (lastObs=" . $r->{'RNGMAX_0'} . ")";
	if ($r->{'yes'}){
		print "varIds:" . @{$r->{'varIDorder'}} . "\n"  if $DEBUG;
		foreach my $varId (@{$r->{'varIDorder'}}){
			my $varName = $h->{$varId};
			print "varname=" . $varName . "\n"  if $DEBUG;
			my ($tmp, $varNo) = split(/\./,$varId);
			print "varno=" . $varNo . "\n"  if $DEBUG;
			my $paramString="";
			
			# exclude
			my $xkey = 'XCLD_' . $varNo;
			if (exists($r->{$xkey})){
				print "exclude key=", $xkey, "\n"  if $DEBUG;
				my @xset = split(/\\0/, $r->{$xkey});
				print "xset=", join('|',@xset), "\n"  if $DEBUG;
				for my $j (@xset) {
					if (exists($r->{chrvarset}->{$varId})) {
						$paramString .= " AND (" . $varName . ' !=' . "'" . $j . "')";
					} else {
						$paramString .= " AND (" . $varName . ' !=' . $j . ")";
					}
				}
			}
			
			# range
			my $lkey = 'RNGMIN_' . $varNo;
			my $ukey = 'RNGMAX_' . $varNo;
			if (exists($r->{$lkey})){
				$paramString .= " AND (" . $varName . '>=' . $r->{$lkey} . ")";
			} 
			if (exists($r->{$ukey})){
				$paramString .= " AND (" . $varName . '<=' . $r->{$ukey} . ")";
			}
			$rowSbstParam .= $paramString;
		}
		
		#" AND (", $h->{'v1.3'}," >=",$r->{'v1.3'},") AND (", v1.3 <=2.17) .
		# AND (v1.13 >= 1) AND (v1.13 <= 5)
		# AND (v1.10 !='97') AND (v1.10 !='98') AND (v1.10 !='99') AND (v1.10 !=' ')
		# AND (v1.13 !=7) AND (v1.13 !=8) AND (v1.13 !=9)
		# AND (v1.22 !=7) AND (v1.22 !=8) AND (v1.22 !=9) AND (v1.22 !=0)
		# AND (v1.40 !=7) AND (v1.40 !=8) AND (v1.40 !=9) AND (v1.40 !=0) AND (v1.40 >=1) AND (v1.40 <=3)
		# )


	}
	# terminator
	
	#$rowSbstParam = 'rows=(' . $rowSbstParam . ')';
	print "final:\n",$rowSbstParam,"\n" if $DEBUG;
	
	return $rowSbstParam;
}


sub getFileUNF {
	my $self = shift;
	my $optns = {@_};
	my $DEBUG=0;
	unless (exists($optns->{TABFILE}) && ($optns->{TABFILE})){
		print STDERR "The source tab-delimited file for the UNF-calculation is not specified.\n";
		print "usage: \&VDC::DSB::DataService::getFileUNF(TABFILE=>\'/some_directory/tab-fileName\');\n";
		print "usage: \$self->getFileUNF(TABFILE=>\'/some_directory/tab-fileName\');\n";
		return 0;
	}
	my $fileSize = -s ($optns->{TABFILE});
	print "file size=",$fileSize, "\n" if $DEBUG;
	unless ($fileSize) {
		print STDERR "The specified source tab-delimited file(" . $optns->{TABFILE} . ") is non-existent and/or empty.\n";
		print STDERR "Note: The source tab-delimited file name must include its full(absolute)-path name.\n";
		return 0;
	}
	# file UNF information
	# input: absolute data file name
	# output: a string UNF
	

	my $tabFileName = $optns->{TABFILE};
	
	my $rcode = "library(UNF);summary(unf(read.table(\"" . $tabFileName . "\", header=F, quote=\"\", sep=\"\\t\",as.is=TRUE),version=3))";
	my $codefile="unfcode_$$.R";
	print "rcode=[",$rcode,"]\n" if $DEBUG;
	#  ; cat $codefile 
	my $flunf = `cd /tmp; touch $codefile; echo -n '$rcode' >> $codefile;R --slave < $codefile  2>&1; `;
	print "raw unf output=",$flunf,"\n" if $DEBUG;
	# remove the code file
	`cd /tmp; rm -f $codefile`;
	
	#unf output= [1] "UNF:3:32:fYv1zsQAcxhn1lLI1fKneQ=="
	# or error messages
	#Error: syntax error
	#Execution halted
	
	my $unf="";
	
	if ($flunf =~ /\[1] "(\S+)"/) {
		$unf=$1;
	}
	
	print "unf output=[" . $unf . "]\n" if $DEBUG;
	if (exists($self->{citation})){
		$self->{citation}->{fileUNF}= $unf;
	}
	return $unf;
}


sub createDataCitationFile{
	my $self =shift;
	my $optns = {@_};
	my $DEBUG =0;
	# (0) process-dependent items as arguments
	# (0-a) citation file name
	# 	$optns->{CITATIONFILE}
	# if specified, it must includ its full pathname
	my $citationFile;
	if (exists($optns->{CITATIONFILE}) && ($optns->{CITATIONFILE})){
		$citationFile=$optns->{CITATIONFILE};
	}
	my $outputType='html';
	if (exists($optns->{OUTPUTTYPE}) && ($optns->{OUTPUTTYPE})){
		$outputType=$optns->{OUTPUTTYPE};
	}

	# (0-b) data file name
	# 	$optns->{SBSTDATAFILE}
	my $sbstdatafile;
	if (exists($optns->{SBSTDATAFILE}) && ($optns->{SBSTDATAFILE})){
		$sbstdatafile=$optns->{SBSTDATAFILE};
	} else {
		print STDERR "The source tab-delimited file for the UNF-calculation is not specified.\n";
		return;
	}
	
	
	# (0-c) organization
	#	$optns->{PROVIDER}
	my $provider="Dataverse";
	if (exists($optns->{PROVIDER}) && ($optns->{PROVIDER})){
		$provider=$optns->{PROVIDER};
	}

	# (1) items via cgi-parameters;
	# (1-a) those stored in $CGIparamset->{citation}
	# items 			hash key
	# --------------------------
	# authors			authors	
	# title				studytitle
	# ddi URL			uri		
	# study date	studyDate	
	# data URL			URLdata	
	# study number		studyno	
	# bib info			bblCit	
	# 
	# (1-b) those stored in other hashes of $CGIparamset
	#
	# variable ID		{rwsbst}->{varIDorder} or
	# 					{_varNo}
	# variable name		{_varNameA}
	# 					ID => Name mapping hash is {_varNameH}
	#
	# (2) locally obtained information
	# (2a) file UNF information
	# input: absolute data file name
	# output: a string UNF data without quotation marks
	
	
	my $fileUNFdata = "NA";
	if (!exists($self->{citation}->{fileUNF})){
		$fileUNFdata = 	$self->getFileUNF(TABFILE=>$sbstdatafile);
		#my $fileUNFdata = "UNF:3:32:fYv1zsQAcxhn1lLI1fKneQ==";
		if ($fileUNFdata) {
			print "returned UNF=",$fileUNFdata,"\n" if $DEBUG;
			print "after UNF:\n", Dumper($self->{citation}) if $DEBUG;
		} else {
			$fileUNFdata="NA";
		}
	} else {
		$fileUNFdata = $self->{citation}->{fileUNF};
	}
	# print the file
	# citation file consists of 3 main parts:
	# pt1: bookMark
	# pt2: citation 
	# pt3: variable information 
	
	# locally assembled parts 
	# (i)   url of the data			$datasetURL
	# (ii)  citation				$cttnString
	# (iii) varID2varName table		$varId2Name
	# (iv) row-selection criteria	$rsCriteria
	
	my $cTemplate=[];
	
	# my $rsCriteria = $self->getRowSelectionCriteria(); # old method
	my $rsCriteria = $self->getCaseSelectionCriteria();

	if ($rsCriteria){
		if ($outputType eq 'html') {
			$rsCriteria = "<p>_Row selection criteria for the subset used in your analysis_:</p>\n\n<blockquote>" . $rsCriteria . "</blockquote>\n\n";
		} elsif ($outputType eq 'txt') {
			$rsCriteria = "_Row selection Criteria for the subset you chose_:\n\n" . $rsCriteria;
		}
	}

	print "rsCriteria:\n", $rsCriteria if $DEBUG ;


	#my $datasetURL= $self->{citation}->{uri} . '?' . join(',', @{$self->{_varNo}}) . "&rows=" . $rsCriteria ;
	
	# up to the study view
	my $datasetURL= $self->{citation}->{uri};

	print "datasetURL:\n", $datasetURL if $DEBUG ;
	
	
	
	
	if ($outputType eq 'html') {
		# $cTemplate->[0]= "<blockquote>Extract From \"" . $self->{citation}->{studytitle} . "\"</blockquote>\n\n";
		$cTemplate->[0]= "";
	} elsif ($outputType eq 'txt') {
		# $cTemplate->[0]= "Extract From \"" . $self->{citation}->{studytitle} . "\"\n\n";
		$cTemplate->[0]= "";
	}
	
	print "citation file(part 1):\n", $cTemplate->[0] if $DEBUG ;
	
	my $cDate="NA";
	
	if ( exists($self->{citation}->{studyDate}) && ($self->{citation}->{studyDate}) ){
		$cDate = $self->{citation}->{studyDate};
	}
	
	my $studyTitle = '"' . $self->{citation}->{studytitle} . '"';
	my $studyHandle = (split(?Access/?, $self->{citation}->{uri}))[1] ;
	my $studyPURL   = 'http://id.thedata.org/' . uri_escape($studyHandle);
	#my $studyPURL   = 'http://handle.thedata.org/VDC/GNRS/0.1/Resolve/' . $studyHandle;
	my $cttnString  = join(',', ( $self->{citation}->{authors}, $cDate, $studyTitle, $studyHandle, $studyPURL)) . "\n<" . $fileUNFdata . ">";
	
	print "cttnString:\n", $cttnString . "\n\n"  if $DEBUG ;

	
	if ($outputType eq 'html') {
		$cTemplate->[1]= "<pre>_Citation_ for the full data set you chose_:</pre>\n\n<blockquote>" . $self->{citation}->{OfflineCitation} . "</blockquote>\n\n";
	}  elsif ($outputType eq 'txt') {
		$cTemplate->[1]= "_Citation for the full data set you chose_:\n\n" . $self->{citation}->{OfflineCitation} . "\n\n";
	}
	print "citation file(part 2):\n", $cTemplate->[1] if $DEBUG ;

	
	my $varId2Name="\tName\n" ;
	foreach my $k (@{$self->{_varNo}}) {
		$varId2Name .= "\t" . $self->{_varNameH}->{$k} . "\n";
	}
	
	print "varId2Name:\n", $varId2Name if $DEBUG ;
	
#	$cTemplate->[2]= "_Variable information_:\n\nThe particular subset of variables you have selected is:\n\n" . $varId2Name . "\n\nAnd the row selection criteria were:\n\tWHERE (" . $rsCriteria . ")\n\nThe unique fingerprint for this subset is:\n\t" . $fileUNFdata . "\n\nPlease include this UNF when describing and publishing analyses and tables based on this subset.\n";
	my $zeligModelName =$self->{'zlgParam'}->{'zlgRqstdMdl'}->[0];
	print "zeligModelName=" . $zeligModelName . "\n" if $DEBUG ;
	my $zeligString = '';
	if ($zeligModelName){
		my $zeligModelURL = 'http://gking.harvard.edu/zelig/docs/' . $zeligModelName . '.pdf';
		$zeligString = '<pre>_Information on the statistical model you ran, including how to cite it_:</pre><blockquote><a href="' . $zeligModelURL . '" >'. $zeligModelURL . '</a></blockquote>';
	}
	if ($outputType eq 'html') {
		$cTemplate->[2]= $rsCriteria . "<pre>_Citation for this subset used in your analysis_:</pre>\n\n<blockquote>" . $self->{citation}->{OfflineCitation} . ' ' . join(',', @{$self->{_varNameA}}) . ' [VarGrp/@var(DDI)]; ' . "\n" . $fileUNFdata . '</blockquote>' . $zeligString ;
	}  elsif ($outputType eq 'txt') {
		$cTemplate->[2]= $rsCriteria . "\n\n_Citation for this subset you chose_:\n\n" . $self->{citation}->{OfflineCitation} . ' ' . join(',', @{$self->{_varNameA}}) . ' [VarGrp/@var(DDI)]; ' . "\n" . $fileUNFdata . "\n";
	}
	
	print "citation file(part 3):\n", $cTemplate->[2] if $DEBUG ;

	
	my $citationString = join('', @{$cTemplate});
	my $WRKDIR= $self->{WRKDIR};
	if ($citationFile){
		#`cd $WRKDIR ; touch $citationFile; echo -en "$citationString" >> $citationFile`;
		open TMPFILE, ">" . $citationFile;
		print TMPFILE $citationString; 
		close TMPFILE; 
	} else {
		return $citationString;
	}
}

sub printZeligCode{
	my $self =shift;
	my $optns = {@_};
	my $wh;
	unless(exists($self->{WH})){
		$wh = $optns->{WH};
	} else {
		$wh= $self->{WH};
	}
	my $PID = $optns->{PID};
	my $ZELIGOUTPUTDIR = $optns->{ZELIGOUTPUTDIR};
	my $zlgOutIdFile   = $optns->{ZLGOUTIDFILE} ;
	#my $zlgOutIdFile="/tmp/VDC/DSB/zlout_" . $$ ;
	#my $zlgOutIdFile= (split(?zlg_$$?, $ZELIGOUTPUTDIR))[0] . "zlout_" . $$ ;
	# dataframe: 
	my $dtfrm = $optns->{DATAFRAME};
	# open(T, ">/tmp/VDC/zlg.code.$$");	
	my $DEBUG;
	my $str={};
	my $mdlCounter=0;
	# optional terms
	# default option settings
	my $defaultOptions ={
		'Summary'     => 'T',
		'Plots'       => 'F',
		'Sensitivity' => 'F',
		'Sim'         => 'F',
		'BinOutput'   => 'F',
		'sensitivityQ'=> '',
		'simArgs'     => '',
		'naMethod'    =>"exclude",
	};	
	my @wants = qw(Summary Plots Sensitivity Sim BinOutput);
	print join(',', @wants), "\n" if $DEBUG;
	
	# print T "size = ", $size, "\n";
	# notational short-cut
	# sanitize characters in a variable name
	my $Id2Name;
	if ($self->{unsafeVarName}){
		$Id2Name = $self->{_varNameHsafe};
	} else {
		$Id2Name = $self->{_varNameH};
	}
	# print T "Raw:\n", Dumper($Id2Name), "\n\n";
	
	# step 1: formula-term

	$self->{zlgParam}->{processOrder}=[];
	
	foreach my $model (@{ $self->{zlgParam}->{zlgRqstdMdl} }) {
		push @{ $self->{zlgParam}->{runOrder} }, $model;
		$mdlCounter++;
		$str->{$model}={};
		my $lhsVars="";
		my $rhsVars="";
		my $formula="";
		
		if ($self->hasNewVarNames()){
			foreach my $nvi (@{ $self->{zlgParam}->{$model}->{varIds} }){
				if ( exists($self->{newVarId2VarName}->{$nvi}) ){
					if ( exists($self->{_newVarNameHsafe}->{ $self->{newVarId2VarName}->{$nvi} } ) ) {
						$Id2Name->{$nvi} = $self->{_newVarNameHsafe}->{ $self->{newVarId2VarName}->{$nvi} };
					} else {
						$Id2Name->{$nvi} = $self->{newVarId2VarName}->{$nvi};
					}
				
				}
			}
		}
		
		#print T "updated:\n", Dumper($Id2Name);
		
		# build the formula
		my @explatoryVars;
		if (exists($self->{zlgParam}->{$model}->{nmBxR3})) {
			@explatoryVars = @{ $self->{zlgParam}->{$model}->{nmBxR3} }; 
		} else {
			@explatoryVars = @{ $self->{zlgParam}->{$model}->{nmBxR2} };
		}
		
		if (scalar(@explatoryVars)) {
			# rhs: x[[j]] + x[[k]] + x[[l]] 
			foreach my $vi (@explatoryVars){
				$rhsVars .=  $Id2Name->{$vi} . "+";
			}
			chop($rhsVars);
		} else {
			$rhsVars="NULL";
			$self->{zlgParam}->{$model}->{Sim}->[0] = "F";
			$self->{zlgParam}->{$model}->{simArgs}->[0] = "";
		}
		
		my @dependentVars;
		if (exists($self->{zlgParam}->{$model}->{nmBxR3})) {
			@dependentVars = (@{$self->{zlgParam}->{$model}->{nmBxR1}}, 
			@{$self->{zlgParam}->{$model}->{nmBxR2}});
		} else {
			@dependentVars = @{$self->{zlgParam}->{$model}->{nmBxR1}};
		}
		
		foreach my $vi (@dependentVars){
			$lhsVars .= $Id2Name->{$vi} . ",";
		}
		
		chop($lhsVars);
		if (scalar(@dependentVars) > 1) {
			$lhsVars="list(" . $lhsVars .")";
		}
		
		
		$formula = $lhsVars . " ~ " . $rhsVars;
		
		$str->{$model}->{formula}=$formula;
		print "test: formula($model): " . $str->{$model}->{formula} . "\n" if $DEBUG;
		
		
		# step 2: options
		# set default values
		while ( my ($key, $value) = each (%{ $defaultOptions })){
			if (exists($self->{zlgParam}->{$model}->{$key}) && ($self->{zlgParam}->{$model}->{$key}->[0] ne "")){
			} else {
				$self->{zlgParam}->{$model}->{$key}->[0] = $value;
			}
		}
		
		
		my $otherArgs ="";
		my $wantString ="";
		foreach my $i (@wants) {
			$wantString .= 'want' . $i . '=' . $self->{zlgParam}->{$model}->{$i}->[0] . ',';
		}
		chop($wantString);
		$otherArgs .= $wantString;
		print "test: wants($model): " . $otherArgs . "\n" if $DEBUG;

		if ($self->{zlgParam}->{$model}->{sensitivityQ}->[0]) {
			$otherArgs  .=",sensitivityArgs=list(\"ptb.s\"=" . $self->{zlgParam}->{$model}->{sensitivityQ}->[0] . ")";
		}
		if ($self->{zlgParam}->{$model}->{simArgs}->[0]){
			$otherArgs  .= ",simArgs=list(" . $self->{zlgParam}->{$model}->{simArgs}->[0] . ")"; 
		}
		
		# By variable 
		#$str->{$model}->{byVar}="";
		#my $byVar = "nmBxR" . $self->{zlgParam}->{$model}->{noBoxes};
		#if (exists($self->{zlgParam}->{$model}->{$byVar}) && #($self->{zlgParam}->{$model}->{$byVar}->[0])) {
		#	$str->{$model}->{byVar} = $Id2Name->{  $self->{zlgParam}->{$model}->{$byVar}->[0] } ;
		#}
		
		#print "test: by variable=",$str->{$model}->{byVar} ,"\n" if $DEBUG;
		
		#if ($str->{$model}->{byVar}){
		#	$otherArgs  .= ",by=\"" . $str->{$model}->{byVar} . "\""; 
		#}
		
		
		# setx
		my $SimString="";
		my $SimString2="";
		if ($self->{zlgParam}->{$model}->{Sim} eq 'T'){
			# conditional simulation
			if ($self->{zlgParam}->{$model}->{Sim_Cond}==1) {
			
				$SimString .= 'cond=T';
			}
			if ($self->{zlgParam}->{$model}->{setx}){
				# non-default setx request
				#$str->{$model}->{optns}->{setxArgs}=[];
				if (exists($self->{zlgParam}->{$model}->{setx_val_1}) && ($self->{zlgParam}->{$model}->{setx_val_1})) {
					# set 1st option
					$SimString .= ',' . $Id2Name->{ $self->{zlgParam}->{$model}->{setx_var}->[0] } . '=' . $self->{zlgParam}->{$model}->{setx_val_1}->[0] ;
					#$str->{$model}->{optns}->{setxArgs}->[0] = $SimString . $Id2Name->{ $self->{zlgParam}->{$model}->{setx_var}->[0] } . '=' . $self->{zlgParam}->{$model}->{setx_val_1}->[0] . ',';
				}
				if (exists($self->{zlgParam}->{$model}->{setx_val_1}) && ($self->{zlgParam}->{$model}->{setx_val_1})) {
					# set 1st option
					$SimString2 = $Id2Name->{ $self->{zlgParam}->{$model}->{setx_var}->[1] } . '=' . $self->{zlgParam}->{$model}->{setx_val_2}->[0] ;
					#$str->{$model}->{optns}->{setxArgs}->[1] = 'setx2Args=list(' . $Id2Name->{ $self->{zlgParam}->{$model}->{setx_var}->[1] } . '=' . $self->{zlgParam}->{$model}->{setx_val_2}->[0] .')';
				}
			}
		} else {
			#$str->{$model}->{optns}->{setxArgs}=[];
		}
		#my $setxArgSet = join(',', @{ $str->{$model}->{optns}->{setxArgs}});
		my $setxArgSet = 'setxArgs=list(' . $SimString . '), setx2Args=list(' . $SimString2 . ')' ;
		print "test: setx argument=",$setxArgSet,"\n" if $DEBUG;
		
		if ($setxArgSet){
			$otherArgs  .= ",\n" . $setxArgSet;
		}
		
		# naMethod
		#$otherArgs  .= ",naMethod=\"" . $self->{zlgParam}->{$model}->{'naMethod'}->[0] . "\""; 

		
		print "test: otherArgs($model): " . $otherArgs . "\n" if $DEBUG;
		print "code object by model:\n", Dumper($str) if $DEBUG;
		
		
		if ($mdlCounter == 1) {
			# printing Zelig-related R code
			print $wh "\n########## Code listing ##########\n";
			print $wh "library(VDCutil)\n";
		}
		
		print $wh "########## Code for the requested option starts here ##########\n";
		print $wh "########## Requested option = $model ##########\n";
		
		# temporary fix
		# recode the binary response of a dependent var to (0, 1)
		if ($self->{zlgParam}->{$model}->{mdlDepVarType} eq 'binary'){
			my $binVarNo = $self->{_varNoMpTbl}->{ $self->{zlgParam}->{$model}->{nmBxR1}->[0] };
			print $wh "bnryVarTbl <-attr(table(x[[" . $binVarNo. "]]), 'dimnames')[[1]];\n";
			print $wh "if (length(bnryVarTbl) == 2){\nif ((bnryVarTbl[1] == 0) && (bnryVarTbl[2]==1)){\n\tcat('this variable is already 0-1\\n');\n} else {\n\tcat('this variable needs the conversion\\n');\n\tcat(paste( bnryVarTbl[1],' is recoded to 1; ', bnryVarTbl[2],' is recoded to 0;\\n', sep=''));\n\tx[[" . $binVarNo . "]]<-as.integer(x[[". $binVarNo. "]] == bnryVarTbl[1]);\n}\n}\n";
		}
		my $rqstDir = $ZELIGOUTPUTDIR . '/rqst_' . $mdlCounter;
		mkdir $rqstDir;
		print $wh "\ntry( {zlg.out<- VDCgenAnalysis(\noutDir=\"${rqstDir}\",\n" . join(",\n", ($formula,  "model=\"$model\"", "data=$dtfrm", $otherArgs, )) .  ",\nHTMLInitArgs=list(Title=\"Dataverse Analysis\")" . "\n, HTMLnote= \"<em>The following are the results of your requested analysis.</em><br/><a href='javascript:window.history.go(-1);'>Go back to the previous page</a>\"" .  ")} )\n\n";
		print $wh "if (exists('zlg.out')) {\ntry(cat(file=\"" . $zlgOutIdFile . "\",\npaste(zlg.out,collapse=\"\\t\"),append=T,sep=\"\\n\"))\n} else {\ntry(cat(file=\"" . $zlgOutIdFile . "\",\n\"${mdlCounter}_${model}_failed\",\nappend=T,sep=\"\\n\"))}\n";
		print $wh "########## Code for the request ends here ##########\n";
	}
	print $wh "########## Code listing: end ##########\n";
}

sub zeligModelsRequested{
	my $self =shift;
	my $optns = {@_};
	my $noZlgMdls = 0;
	if (exists($self->{zlgParam})){
		$noZlgMdls =  scalar(@{ $self->{zlgParam}->{zlgRqstdMdl} });
	}
	return $noZlgMdls;
}


sub zeligModelsRunOrder{
	my $self =shift;
	my $optns = {@_};
	my $rt = [];
	if (exists($self->{zlgParam})){
		foreach my $mdlName (@{$self->{zlgParam}->{runOrder}}) {
			push @{$rt}, $self->{zlgParam}->{mdlId2mdlTitle}->{$mdlName};
		}
	}
	return $rt;
}



sub getValueRange{
	my $step0 = shift;
	my $DEBUG=0;
	print 'before step 0=',$step0,"\n" if $DEBUG;

	$step0 =~ s/ //g;
	print 'after  step 0=',$step0,"\n" if $DEBUG;

	# string into tokens
	my @step1raw = split(/,/,$step0);
	print 'after  step 1raw=', join('|', @step1raw), "\n" if $DEBUG;
	my @step1=();
	foreach my $el (@step1raw) {
		if ($el ne '') {
			push @step1, $el;
		}
	}
	print 'after  step 1=', join('|', @step1), "\n" if $DEBUG;

	my $rangeData =[];
	# for each token, check the range operator
	for (my $i=0; $i<@step1; $i++){
		my @tmp = split (//, $step1[$i]);
		my $token = {};
		if (($tmp[0] ne '[') && ($tmp[0] ne '(')){
			# no LHS range operator
			# assume [
			$token->{'start'}=3;
		} elsif ($tmp[0] eq '[') {
			$token->{'start'}=3;
			shift(@tmp);
		} elsif ($tmp[0] eq '(') {
			$token->{'start'}=5;
			shift(@tmp);
		}
		if (($tmp[$#tmp] ne ']') && ($tmp[$#tmp] ne ')')){
			# no RHS range operator
			# assume ]
			$token->{'end'}=4;
		} elsif ($tmp[$#tmp] eq ']'){
			pop(@tmp);
			$token->{'end'} = 4;
		} elsif ($tmp[$#tmp] eq ')'){
			pop(@tmp);
			$token->{'end'}=6; 
		}
		
		# after these steps, the string does not have range operators;
		# i.e., '-9--3', '--9', '-9-','-9', '-1-1', '1', '3-4', '6-'

		if (($tmp[0] eq '!') && ($tmp[1] eq '=') ){
			# != negation string is found
			$token->{'start'}=2;
			$token->{'end'}=''; 
			#shift(@tmp);
			#shift(@tmp);
			$token->{'v1'}= join('', @tmp[2..$#tmp]);
			$token->{'v2'}='';
			print "value=",join('',@tmp[2..$#tmp]),"\n" if $DEBUG;
		} elsif (($tmp[0] eq '-') && ($tmp[1] eq '-')){
			# type 2: --9
			$token->{'v1'}= '';
			shift(@tmp);
			$token->{'v2'}= join('',@tmp); 
		} elsif (($tmp[0] eq '-') && ($tmp[$#tmp] eq '-')) {
			# type 3: -9-
			$token->{'v2'} = '';
			pop(@tmp);
			$token->{'v1'} = join('', @tmp);
		} elsif (($tmp[0] ne '-') && ($tmp[$#tmp] eq '-')) {
			# type 8: 6-
			$token->{'v2'} = '';
			pop(@tmp);
			$token->{'v1'} = join('', @tmp);
		} else {
			my $count=0;
			my @index=();
			for (my $j=0;$j<@tmp; $j++){
				if ($tmp[$j] eq '-'){
					$count++;
					push @index, $j;
				}
			}

			if ($count >=2){
				# range type
				# divide the second hyphen
				# types 1 and 5: -9--3, -1-1
				$token->{'v1'}=join('',@tmp[0..($index[1]-1)]);
				$token->{'v2'}=join('',@tmp[($index[1]+1)..$#tmp]);

			} elsif ($count == 1){
				if ($tmp[0] eq '-'){
					# point negative type
					# type 4: -9 or -inf,9
					# do nothing
					if ( ($token->{'start'}==5) && ( ($token->{'end'}==6)|| ($token->{'end'}==4) ) ) {
						$token->{'v1'}= '';
						shift(@tmp);
						$token->{'v2'}= join('', @tmp);
					} else {
						$token->{'v1'}= join('', @tmp);
						$token->{'v2'}= join('', @tmp);
					}
				} else {
					# type 7: 3-4
					# both positive value and range type
					my @vset = split(/-/, join('', @tmp));
					$token->{'v1'}=$vset[0];
					$token->{'v2'}=$vset[1];
				}

			} else{
				# type 6: 1
				$token->{'v1'}= join('', @tmp);
				$token->{'v2'}= join('', @tmp);
			}
		}
		# 
		print $i, "-th result=", join('|', ($token->{'start'},$token->{'v1'},$token->{'end'},$token->{'v2'})), "\n" if $DEBUG;
		push @$rangeData, [$token->{'start'},$token->{'v1'},$token->{'end'},$token->{'v2'}];

	}

	print Dumper($rangeData) if $DEBUG;
	return $rangeData;
}



1;



__END__

=head1 NAME

VDC::DSB::DataService - a module that handle data-service-related cgi parameters

=head1 SYNOPSIS

    use VDC::DSB::DataService;
    
    my $q = new CGI;
    my $frmprm  = { %{$q->Vars} };
    my $CGIparamSet = VDC::DSB::DataService->new($frmprm);
    
    


=head1 DESCRIPTION

VDC::DSB::DataService 

=head1 USAGE

=head2 The Constructor

=over 4

=item new()

The first argument is a subet (hash) of an original cgi parameter set sent from a user's data service (statistical analyses or data download).

=back

=head2 Methods

=over 4



=item addMetaData()

addes requested variables' addtional metadata from a DDI (XML) file concerning as the first argument

=item checkCaseSubset()

validates case-wise subsetting parameters

=item checkDwnldTab()

checks whether a download request is the tab-delimited type

=item checkParams()

validates given CGI parameters

=item getDwnldType()

returns the file-type of a requested downloading

=item getMetaData4CC()

returns requested variables's metadata to write syntax files

=item getRqustType()

returns a requested data service

=item getRsubsetparam()

returns a case-wise subsetting parameters

=item getRwSbst()

returns a case-wise subset data file

=item getVarIDhsh()

returns requested variables' IDs as a reference to a hash

=item getVarIDorder()

returns requested variables' ID sequence as a reference to an array

=item getVarNameSet()

returns requested variables' names as a reference to an arrary

=item printRcodeData()

prints/saves R code as a temporary file

    

=over 4

=item WH

file handle of a Rcode file

=item HTMLFILE

html file that stores retulsts of a requested statistical analysis

=item TABFILE

tab-delimited file to be read by an R code file

=item SRVRCGI

directory-path of a cgi-script that calls this module

=item DWNLDPRFX

directory-path of a locally saved data file

=item IMGFLPRFX

directory-path of a locally saved image file

=back

=item setMVtypeCode()

sets a missing value string

=item updateCaseQnty()

replaces the number of cases with a new figure (the first argument)

=back



=head1 AUTHOR

Akio Sone, Virtual Data Center Project, Harvard University

(<URL:http://thedata.org/>)

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2004 President and Fellows of Harvard University

GNU General Public License



=cut

