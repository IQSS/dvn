package VDC::DSB::StatCodeWriter;


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

my $ccfltrlst = [ qw (_varNameA _varNameH _varType _varLabel _varNo _varNoMpTbl _valLblTbl _mssvlTbl _charVarTbl VarID _varFormat _formatName _formatCatgry _newVarNameSetA _newVarNameSetH _newVarSpec unsafeVarName unsafeNewVarName _varNameAsafe _newVarNameAsafe _varNameHsafe _newVarNameHsafe) ];

sub new {
	my $caller = shift;
	my $self   = shift;
	my $class = ref ($caller) || $caller;
	bless $self, $class;
	#print "VDC::DSB::StatCodeWriter::new:\n", Dumper( @_ );
	$self->_init( @_ );
	#print "VDC::DSB::StatCodeWriter::new:\n", Dumper($self);
	return $self;
}

sub _init{
	my $self= shift;
	my $args = { @_ };
	#print "args:\n", Dumper($args),"\n";
	if (%$args){
		#print "arguments are not empty\n";
		while ( my ($ky, $vl) = each %{$args} ) {
			$self->{$ky}=$vl;
		}
	} else {
		#print "arguments are empty\n";
	}
	# sanitize variable labels
	my $varLabel = $self->{_varLabel};
	for (my $i=0; $i<@$varLabel; $i++){
		$varLabel->[$i] =~ s/\042/\047/g; # replace "" with '
	}
	$self->{_varLabel} = $varLabel;
}

sub printCC{
	my $self= shift;
	my $args = { @_ };
	while ( my ($ky, $vl) = each %{$args} ) {
		$self->{$ky}=$vl;
	}
	
	#print "printCC:\n", Dumper($self),"\n";
	foreach my $type (@{$self->{TYPE}}){
		my $methodtype = 'print' . uc($type) . 'code';
		$self->$methodtype();
	}
}

sub printSAScode{
	my $self=shift;
	my $DEBUG;
	#print "/////////////// printing sas code ///////////////\n";
	my $filenm = $self->{DATA};
	my $wfl= $self->{CCFLPFX};
	$wfl .= '.sas';
	open (CC, "> $wfl");
	my $wh = *CC;
	
	my $varname = $self->getVarAll();
	my $nameMapping=$self->getVarNameAll4Print();

	print "nameMapping:\n", Dumper($nameMapping) if $DEBUG;
	my $varType = $self->getVarTypeAll();
	
	my $fmt ={};
	if ( exists($self->{_valLblTbl}) || ($self->countUserDefinedValuTable()) ){
		print $wh "proc format;\n";

		for (my $i=0;$i<@{$varname};$i++ ){
			my $vnRaw = $varname->[$i];
			my $ud=0;
			if (exists($self->{_newVarNameSetH}->{$vnRaw})){
				$ud=1;
			}
			my $vn= $nameMapping->{$vnRaw};
			if ((exists($self->{_valLblTbl}->{$vnRaw})) || (exists($self->{_newVarSpec}->{$vnRaw}->{valueTable})) )  {
				my $vt;
				my $qt;
					#print "var no=",$i,"\t";
					#print "vt=",$self->{_varType}->[$i],"\n";
				#unless ($self->{_varType}->[$i]){
				unless ($varType->[$i]){
					# char var case like this (VALUE $INTVU4AFMT)
					#print "char var=",$vn,"\n";
					$vt = '$';
					$qt = '"';
				} else {
					#print "numeric var=",$vn,"\n";
					$vt='';
					$qt='';
				}
				my $value = $vt . $vn . 'FMT';
				print $wh "VALUE " . $value,"\n" ;
				$fmt->{$vnRaw} = $value;
				my $vltbl;
				unless($ud){
					$vltbl = $self->{_valLblTbl}->{$vnRaw}; 
					print "var= " . $vnRaw . ":\n" , Dumper($vltbl) if $DEBUG;
				} else {
					$vltbl = $self->getUserDefinedValueTable(varName=>$vnRaw);
					print "new var= " . $vnRaw . ":\n" , Dumper($vltbl) if $DEBUG;
				}
					
				#foreach my $vl (@{$self->{_valLblTbl}->{$vnRaw}}){
				foreach my $vl (@{$vltbl}){
					if ($vl->[1]) {
						my $safeValueLabel = $vl->[1]; 
						$safeValueLabel =~ s/\042/\047/g; # replace "" with '
						print $wh "\t " . $qt . $vl->[0] . $qt . ' = "', substr($safeValueLabel,0,40), "\"\n";
					}
				}
				
				print $wh ";\n";
			} else {
				next;
			}
		}
		print $wh "\n";
	}
	my $sasDataName = join('_',split(/\./,$filenm));
	print $wh "DATA $sasDataName;\n";
	print $wh "\tINFILE ". "'" .  $filenm . "'" . " DELIMITER='09'x FIRSTOBS=2;\n";
	my $vnstring ="";
	#my $varType = $self->getVarTypeAll();
	for (my $i=0;$i<@{$varname};$i++ ){
		my $vnRaw = $varname->[$i];
		my $vn= $nameMapping->{$vnRaw};
		my $token;
		
		unless ($varType->[$i]) {
			$token= ' $';
		} else {
			if (exists($self->{_formatCatgry})){
				my $catgryCode='';
				if (exists($self->{_formatCatgry}->{$vnRaw})){
					$catgryCode = $self->{_formatCatgry}->{$vnRaw}; 
				}
				if (($catgryCode eq 'date') || ($catgryCode eq 'time')) {
					$token= ' $';
				} else {
					$token= '';
				}
			} else {
				$token= '';
			}
		}
		$vnstring = $vnstring . ' ' . ($vn . $token );
	}
	print $wh "\t" . "INPUT ",$vnstring,";\n\n" ;
	# variable label
	if (exists($self->{_varLabel}) || scalar(@{$self->getNewVarLabel()})){
		my $varLabelAll = $self->getVarLabelAll();

		for (my $i=0;$i<@{$varname};$i++ ){
			my $vn= $nameMapping->{$varname->[$i]};

			#if ($self->{_varLabel}->[$i]){
			if ($varLabelAll->[$i]){
				#print  $wh "label " . $varname->[$i] . " = '" . substr($self->{_varLabel}->[$i],0,80) . "'" . ";\n";
				print  $wh "label " . $vn . ' = "' . substr($varLabelAll->[$i],0,80) . '"' . ";\n";
			}
		}
		print $wh "\n";
	}
	
	if (scalar(%$fmt)){
		print $wh "FORMAT\n";
		while (my ($k, $y) = each (%{$fmt})){
			my $vn= $nameMapping->{$k};
			print  $wh  $vn . " ". $y . ".\n" ;
		}
		print $wh ";\n\n";
	}
	
	if (exists($self->{_mssvlTbl})){
		for (my $i=0;$i<@{$varname};$i++ ){
			my $vn= $varname->[$i];
			my $vn4print= $nameMapping->{$vn};
			if  (exists($self->{_mssvlTbl}->{$vn})){
				my $mv = $self->{_mssvlTbl}->{$vn};
				my $qt;
				unless ($varType->[$i]){
					# char var case
					$qt = '"';
				} else {
					$qt='';
				}
				foreach my $el (@{$mv}) {
					print $wh "if " . $vn4print . "=" .$qt. $el->[0] . $qt ." then $vn =.;\n";
				}
			} else {
				next;
			}
			print $wh "\n";
		}
	}
	print $wh "RUN;\n";
	close($wh);
}


sub printSPScode{
	my $self=shift;
	my $DEBUG;
	#print "/////////////// printing sps code ///////////////\n";
	my $filenm = $self->{DATA};
	my $wfl= $self->{CCFLPFX};
	$wfl .= '.sps';
	open (CC, "> $wfl");
	my $wh = *CC;
	
	my $varname= $self->getVarAll();
	
	my $nameMapping=$self->getVarNameAll4Print();
	print "nameMapping:\n", Dumper($nameMapping) if $DEBUG;
	my $varType = $self->getVarTypeAll();

	
	print $wh "GET TRANSLATE FILE=\"$filenm\" \n /TYPE=TAB\n /FIELDNAMES\n.\n\n";
	# variable label
	if (exists($self->{_varLabel}) || scalar(@{$self->getNewVarLabel()})){
		print $wh "VARIABLE LABELS\n";
		my $varLabelAll = $self->getVarLabelAll();
		for (my $i=0;$i<@{$varname};$i++ ){
			my $vnRaw= $varname->[$i];
			my $vn = $nameMapping->{$vnRaw};
			#if ($self->{_varLabel}->[$i]){
			if ($varLabelAll->[$i]){
				my $token;
				if ($i){
					$token ='/';
				} else {
					$token ='';
				}
				#print $wh ' ' . $token . $vn, " '" . substr($self->{_varLabel}->[$i],0,40) . "'\n";
				print $wh ' ' . $token . $vn, ' "' . substr($varLabelAll->[$i],0,40) .'"' . "\n";
			}
		}
		print $wh ".\n\n";
	}

	if (exists($self->{_valLblTbl}) || $self->countUserDefinedValuTable() ){
		my $cntr = scalar(keys(%{$self->{_valLblTbl}}));
		print $wh "VALUE LABELS\n";
		
		for (my $i=0;$i<@{$varname};$i++ ){
			my $vnRaw= $varname->[$i];
			my $vn= $nameMapping->{$vnRaw};
			my $ud=0;
			if (exists($self->{_newVarNameSetH}->{$vnRaw})){
				$ud=1;
			}

			if (exists($self->{_valLblTbl}->{$vnRaw}) || exists($self->{_newVarSpec}->{$vnRaw}->{valueTable})){
				my $qt;
					#print "var no=",$i,"\t";
					#print "vt=",$self->{_varType}->[$i],"\n";
				unless ($varType->[$i]){
					# char var case like this (VALUE $INTVU4AFMT)
					#print "char var=",$vn,"\n";
					$qt = '"';
				} else {
					#print "numeric var=",$vn,"\n";
					$qt='';
				}
				print $wh " ", $vn,"\n";
				my $vltbl;
				unless($ud){
					$vltbl = $self->{_valLblTbl}->{$vnRaw};
					print "var= " . $vnRaw . ":\n" , Dumper($vltbl) if $DEBUG;
				} else {
					$vltbl = $self->getUserDefinedValueTable(varName=>$vnRaw);
					print "new var= " . $vnRaw . ":\n" , Dumper($vltbl) if $DEBUG;
				}
				
				foreach my $vl (@{$vltbl}){
					if ($vl->[1]) {
						my $safeValueLabel = $vl->[1]; 
						$safeValueLabel =~ s/\042/\047/g; # replace "" with '
						print $wh " " . $qt . $vl->[0] . $qt . ' = "' . substr($safeValueLabel,0,40) .'"' . "\n";
					}
				}
				#$cntr--;
				#unless ($cntr){
				#	print $wh ".\n\n";
				#} else {
					print $wh " /\n";
				#}
			} else {
				next;
			}

		}
		print $wh ".\n\n";
	}

	if (exists($self->{_mssvlTbl})){
		my $cntr = scalar(keys(%{$self->{_mssvlTbl}}));
		print $wh "MISSING VALUES\n";
		
		for (my $i=0;$i<@{$varname};$i++ ){
			my $vn= $varname->[$i];
			my $vn4print= $nameMapping->{$vn};
			unless (exists($self->{_mssvlTbl}->{$vn})){
				next;
			} else {
				my $qt;
					#print "var no=",$i,"\t";
					#print "vt=",$self->{_varType}->[$i],"\n";
				unless ($varType->[$i]){
					# char var case like this (VALUE $INTVU4AFMT)
					#print "char var=",$vn,"\n";
					$qt = '"';
				} else {
					#print "numeric var=",$vn,"\n";
					$qt='';
				}
				print $wh " ", $vn4print,"(";
				for (my $j=0; $j<@{$self->{_mssvlTbl}->{$vn}};$j++){
					print $wh  $qt . $self->{_mssvlTbl}->{$vn}->[$j]->[0] . $qt ;
					unless ($j == (scalar(@{$self->{_mssvlTbl}->{$vn}}) - 1)){
						print $wh ",";
					} else {
						print $wh ")";
					}
				}
				$cntr--;
				#unless ($cntr){
				#	print $wh "\n.\n\n";
				#} else {
					#print $wh " /\n";
					print $wh "/\n";
				#}
			}
		}
		print $wh "\n.\n\n";
	}
	print $wh "execute.\n";
	close($wh);
}

sub printDTAcode{
	my $self=shift;
	my $DEBUG;
	
	#print "/////////////// printing stata code ///////////////\n";
	my $filenm = $self->{DATA};
	my $wfl= $self->{CCFLPFX};
	$wfl .= '.do';
	open (CC, "> $wfl");
	my $wh = *CC;
	
	my $varname = $self->getVarAll();
	my $nameMapping=$self->getVarNameAll4Print();
	my $varType = $self->getVarTypeAll();

	print $wh "insheet using $filenm, tab \n\n";
	# get all var labels
	
	if (exists($self->{_varLabel}) || scalar(@{$self->getNewVarLabel()}) ){
		my $varLabelAll = $self->getVarLabelAll();
		
		for (my $i=0;$i<@{$varname};$i++ ){
			my $vn = $varname->[$i]; 
			my $lvn= $nameMapping->{$vn};
			$lvn=~tr/[A-Z]/[a-z]/; 
			#if ($self->{_varLabel}->[$i]){
			if ($varLabelAll->[$i]){
				#print $wh "label variable ", $lvn, " \"" . substr($self->{_varLabel}->[$i],0,80) . "\"\n";
				print $wh "label variable ", $lvn, " \"" . substr($varLabelAll->[$i],0,80) . "\"\n";

			}
		}
		print $wh "\n";
	}
	
	if (exists($self->{_mssvlTbl})){
		
		for (my $i=0;$i<@{$varname};$i++ ){
			my $vn = $varname->[$i];
			my $lvn = $nameMapping->{$vn};
			$lvn=~tr/[A-Z]/[a-z]/;
			unless (exists($self->{_mssvlTbl}->{$vn})){
				next;
			} else {
				if ($varType->[$i]){
					for (my $j=0; $j<@{$self->{_mssvlTbl}->{$vn}};$j++){
						my $mv = $self->{_mssvlTbl}->{$vn}->[$j]->[0];
						print $wh "recode ${lvn} ${mv}=.\n";
					}
				}
			}
		}
		print $wh "\n";
	}


	if (exists($self->{_valLblTbl})  || $self->countUserDefinedValuTable()){
		for (my $i=0;$i<@{$varname};$i++ ){
			my $vnRaw = $varname->[$i];
			my $ud=0;
			if (exists($self->{_newVarNameSetH}->{$vnRaw})){
				$ud=1;
			}
			my $lvn= $nameMapping->{$vnRaw};
			$lvn=~tr/[A-Z]/[a-z]/;
			if ((exists($self->{_valLblTbl}->{$vnRaw})) || (exists($self->{_newVarSpec}->{$vnRaw}->{valueTable})) )  {
				if  ($varType->[$i]){
					my $vltbl;
					unless($ud){
						$vltbl = $self->{_valLblTbl}->{$vnRaw}; 
						print "var= " . $vnRaw . ":\n" , Dumper($vltbl) if $DEBUG;
					} else {
						$vltbl = $self->getUserDefinedValueTable(varName=>$vnRaw);
						print "new var= " . $vnRaw . ":\n" , Dumper($vltbl) if $DEBUG;
					}
					
					foreach my $vl (@{$vltbl}){
						if ($vl->[1]){
						my $safeValueLabel = $vl->[1]; 
						$safeValueLabel =~ s/\042/\047/g; # replace "" with '
						print $wh "lab def ${lvn}_l ",$vl->[0]," \"" . substr($safeValueLabel,0,40) . "\", add\n";
						}
					}
					
					print  $wh "label values ${lvn} ${lvn}_l\n\n";
				}
			} else {
				next;
			}
		}
	}
	# user-defined vars
	
	close($wh);
}

sub printDTAvalueLabelDoFile{
	my $self=shift;
	my $args = { @_ };
	while ( my ($ky, $vl) = each %{$args} ) {
		$self->{$ky}=$vl;
	}

	#print "/////////////// printing stata value-label do file ///////////////\n";
	my $wfl= $self->{CCFLPFX};
	$wfl .= '.do';
	open (CC, "> $wfl");
	my $wh = *CC;
	
	#my $ccfltrlst = [qw (_varNameA _varNameH _varType _varLabel _varNo _varNoMpTbl _valLblTbl _mssvlTbl _charVarTbl VarID) ];
	my $varname;
	if ($self->{unsafeVarName}){
		$varname = $self->{_varNameAsafe};
	} else {
		$varname = $self->{_varNameA};
	}
	
	
	if (exists($self->{_newVarNameSetA})){
		my $newVarname = $self->{_newVarNameSetA};
		if ($self->{unsafeNewVarName}){
			$newVarname = $self->{_newVarNameAsafe}
		}
		$varname = [@$varname, @$newVarname];
	}

	
	print $wh '* To attach value labels to the downloaded stata file,',"\n";
	print $wh '* execute the following command lines after starting STATA', "\n";
	print $wh '* ', "\n", '* use data_xxxx.dta', "\n", '* do CCxxxx', "\n", '*', "\n\n";
	
	if (exists($self->{_valLblTbl})){
		for (my $i=0;$i<@{$varname};$i++ ){
			my $vn = my $lvn = $varname->[$i];
			$lvn=~tr/[A-Z]/[a-z]/;
			unless (exists($self->{_valLblTbl}->{$vn})){
				next;
			} else {
				if  ($self->{_varType}->[$i]){
					foreach my $vl (@{$self->{_valLblTbl}->{$vn}}){
						if ($vl->[1]){
							my $safeValueLabel = $vl->[1]; 
							$safeValueLabel =~ s/\042/\047/g; # replace "" with '
							print $wh "lab def ${lvn}_l ",$vl->[0]," \"" . substr($safeValueLabel,0,40) . "\", add\n";
						}
					}
					#print  $wh "label values ${lvn} ${lvn}_l\n\n";
					print  $wh "label values ${vn} ${lvn}_l\n\n";
				}
			}
		}
	}
	
	close($wh);
}
sub getVarAll{
	my $self = shift;
	my $varname= $self->{_varNameA};
	
	if (exists($self->{_newVarNameSetA})){
		my $newVarname = $self->{_newVarNameSetA};
		$varname = [@$varname, @$newVarname];
	}
	return $varname;
}

sub getVarLabelAll {
	my $self = shift;
	my $vl = $self->{_varLabel};
	if (exists($self->{_newVarNameSetA})){
		my @tmp=();
		foreach my $vn (@{$self->{_newVarNameSetA}}){
			push @tmp, $self->{_newVarSpec}->{$vn}->{'label'};
		}
		$vl = [@$vl, @tmp];
	}
	return $vl;
}

sub getNewVarLabel{
	my $self = shift;
	my $vl=[];
	if (exists($self->{_newVarNameSetA})){
		foreach my $vn (@{$self->{_newVarNameSetA}}){
			push @$vl, $self->{_newVarSpec}->{$vn}->{'label'};
		}
	}
	return $vl;
}

sub getVarNameAll4Print{
	my $self=shift;
	my $nameMapping={};
	
	my $varname = $self->{_varNameA}; # for keys
	my $varname4print= $self->{_varNameA}; # for code-printing
	if ($self->{unsafeVarName}){
		$varname4print = $self->{_varNameAsafe};
	}

	# user-defined var
	my $size = 0;
	if (exists($self->{'_newVarNameSetA'})){
		$size = scalar(@{$self->{'_newVarNameSetA'}});
	}
	if ($size){
		my $newVarname = $self->{_newVarNameSetA};
		my $newVarname4print= $self->{_newVarNameSetA};
		if ($self->{unsafeNewVarName}){
			$newVarname4print = $self->{_newVarNameAsafe}
		}
		$varname = [@$varname, @$newVarname];
		$varname4print = [@$varname4print, @$newVarname4print];
	}
	for (my $i=0;$i<@$varname; $i++){
		$nameMapping->{$varname->[$i]} = $varname4print->[$i]; 
	}
	return $nameMapping;
}

sub getVarTypeAll{
	my $self=shift;
	my $vt = $self->{_varType};
	if (exists($self->{_newVarNameSetA})){
		my @tmp=();
		foreach my $vn (@{$self->{_newVarNameSetA}}){
			push @tmp, $self->{_newVarSpec}->{$vn}->{'type'};
		}
		$vt = [@$vt, @tmp];
	}
	return $vt;
}

sub countUserDefinedValuTable{
	my $self = shift;
	my $DEBUG=0;
	my $howmany=0;
		# user-defined var's value-tables
		my $size = 0;
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
					}
				}
				if ($cntr){
					$howmany++;
				}
			}
		}
	return $howmany;
}

sub getUserDefinedValueTable{
	my $self = shift;
	my $optn ={ @_ };
	my $DEBUG=0;
	my $vltable;
	
	if (exists($optn->{varName})){
		# requested var's value table only
		my $rqstdVar = $optn->{varName};
		if (exists($self->{_newVarNameSetH})){
			if (exists($self->{_newVarNameSetH}->{$rqstdVar})){
				my $cntr=0;
				while (my ($k,$y) = each ( %{$self->{_newVarSpec}->{$rqstdVar}->{'valueTable'}})){
					print "new value(k)=",$k,"\n" if $DEBUG;
					if ($k ne "eteleD") {
						$cntr++;
						if ($cntr == 1){
							$vltable=[];
						}
						push @{$vltable}, [ $k, $y->{label} ];
					}
				}
			}
		}
	}
	return $vltable;
}

1;

__END__

=head1 NAME

VDC::DSB::StatCodeWriter - a statistical-package-syntax-file writer

=head1 SYNOPSIS

    use VDC::DSB::StatCodeWriter;
    
    my $ct = [ qw ( sps sas dta ) ];
	my $ccMD= $CGIparamSet->getMetaData4CC();
	my $ccw = VDC::DSB::StatCodeWriter->new($ccMD);
	$ccw->printCC(TYPE=>$ct, CCFLPFX=>"/tmp/code_file_prefix", DATA=>"/data_file.dat");


=head1 DESCRIPTION

VDC::DSB::StatCodeWriter writes a syntax file, which instructs a statistical package to read a given tab-delimited data file, with its metadata (i.e., variable names, variable labels, etc.).  Currently the module can write three major statistical packages' syntax file: SPSS, SAS, and STATA. 


=head1 USAGE

=head2 The Constructor

=over

=item new()

The first argument is a set of metadata to be dumped as part of a syntax file

=back

=head2 Methods

=over 4

=item printCC(TYPE=>$ref_to_array, CCFLPFX=>"/tmp/syntax_file_prefix", DATA=>"/tmp/data_file")

    

prints a set of syntax files



=over 4

=item TYPE

a reference to an array that lists syntax 3-letter file extensions

=item CCFLPFX

prefix to each syntax file

=item DATA

an accompanying tab-delimited data file

=back

=back

=head1 AUTHOR

Akio Sone, Virtual Data Center Project, Harvard University

<URL:http://thedata.org/>

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2004 President and Fellows of Harvard University

GNU General Public License



=cut

