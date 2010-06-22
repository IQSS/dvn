package VDC::DSB::CaseWiseSubset;

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

sub new {
	my $caller = shift;
	my $self   = shift;
	my $optns  = { @_ };
	my $class = ref($caller) || $caller;
	bless $self, $class;
	
	while ( my ($ky, $vl) = each %{$optns} ) {
		$self->{$ky}=$vl;
	}
	#print TMPX "\n\ndump2:\n",Dumper($self),"\n";
	return $self;
}

sub checkSubsetParams{
	my $self = shift;
	
	# row: min
	unless (exists($self->{RNGMIN_0}) && defined($self->{RNGMIN_0}) ){
		$self->{RNGMIN_0}=1;
	}
	# row: max
	unless (exists($self->{RNGMAX_0}) && defined($self->{RNGMAX_0})) {
		if (defined($self->{maxnrow})) {
			$self->{RNGMAX_0}=$self->{maxnrow};
		} else {
			$self->{RNGMAX_0}=10**10;
		}
	}
	
	# check2: min/max values for subsetting by row id number
	# lower limit: $self->{RNGMIN_0}
	# upper limit: $self->{RNGMAX_0}
	my $rowns = $self->{RNGMIN_0};
	my $rowne = $self->{RNGMAX_0};
	
	# '.' is not permissiable because rownumbers are integer
	# '0' prefixed numbers are treated as number-without-the-prefix
	my $returnCode=0;
	if ( $rowne <$rowns ) {
		$returnCode=1;
	} elsif ($rowns <1) {
		$returnCode=2;
	} elsif ( exists($self->{maxnrow}) && ($rowne > $self->{maxnrow}) ) {
		$returnCode=3;
	}
	return $returnCode;
}




sub SubsetCaseWise{
	my $self = shift;
	my $keepMV=shift;
	my $dcmlAdjOff=shift;
	my $DEBUG=0;
	my $FH;
	if ($DEBUG) {
		$FH = *LOGFILE;
		my $logfile = "subsetNew.$$.log"; 
		open(LOGFILE, ">$logfile") or die "cannot open $logfile:$! sub rowwiseSubset";
		print $FH "self=\n",Dumper($self), "\n";
	}
	
	unless($keepMV) {
		$keepMV=1;
	}
	# True : Missing Values are kept, otherwise excluded
	# if not true, any observation that has a missing value will be excluded
	my @missValueSet=(' ', 'NA','.');
	
	open(INFILE,  $self->{RAWFILE} ) or die "cannot open $self->{RAWFILE} : $! (within sub rowwiseSubset)";
	open(FILE,  ">$self->{OUTFILE}") or die "cannot open $self->{OUTFILE}: $! (within sub rowwiseSubset)";
	my $dcmladj=0;
	my @dcmlon= (0) x scalar(@{$self->{varIDorder}});
	if ((exists($self->{_dcml})) && (scalar(keys %{$self->{_dcml}}))){
		$dcmladj=1;
		
		for (my $i=0; $i< @{$self->{varIDorder}}; $i++){
			if (exists($self->{_dcml}->{ $self->{varIDorder}->[$i] }) && ($self->{_dcml}->{ $self->{varIDorder}->[$i] } > 0) ){
				$dcmlon[$i] = 10**($self->{_dcml}->{ $self->{varIDorder}->[$i] }) ;
			}
		}
	}
	if ($dcmlAdjOff){
		$dcmladj=0; # now decimalization is now done by rcut in the Repository
	}
	# when this subroutine is called, data file is already column-wise cut
	# i.e., there is no irrelevant variables.
	# The physical order of these variables in a subset file is found: $self->{varIDorder}->[$i]
	my $casecounter=0;
	while (<INFILE>) {
		# first eliminate out-of-range rows 
		# this reduces the processing time of the subsequent subsetting-by-point and -range
		# row-wise subsetting 1: subsetting by Row-number is done by the following conditional statement
		
		if ( ($. >= $self->{RNGMIN_0}) && ($. <= $self->{RNGMAX_0} ) ) {
		
			# remove the new line character
			chomp;
			my @temp = split(/\t/,$_);
			# contents of @temp: e.g., (var_2, var_4, var_5, var_7, var_8);
			#my $temp = join("|", split(/\t/,$_));

			if ($DEBUG) {print $FH "\n$. -th record=",join("|",@temp ),"\n";}

			# subsetting by points and range
			#
			# for each variable [$i-th element=$i-th column]
			#my $inclusion=0;
			my ($varIDi, $varIDix) ;
			for (my $i=0;$i<=$#temp;$i++){
				if ($DEBUG) {print $FH $i,"-th col[",$temp[$i],"]",$#temp,"\n";}
				
				$varIDi = $self->{varIDorder}->[$i];
				(my $skip, $varIDix) = split (/\./, $varIDi);
				
				if (!exists($self->{chrvarset}->{$varIDi})) {
				if ($DEBUG) {print $FH $i, "-th var[] = numeric type\n";}
										

					if ($keepMV) {
						if ( ($temp[$i] eq $missValueSet[0]) || ($temp[$i] eq $missValueSet[1]) || ($temp[$i] eq $missValueSet[2]) )  {
							#$temp[$i] =~ s/ //;
							$temp[$i] = "NA";
							if ($DEBUG) {print $FH $i, "-th col missing value\n";}
						}
					} else {
						goto EXCLUSION;
					}
					# numeric var case
					# subsetting by range
					# not applicable for character var
					# criteria are two at max: 3 possible situations
					# data structure:
					# RNGMIN_7=1/ RNGMAX_7=3
					# generate id
					my $keyrmin = "RNGMIN_" . $varIDix;
					my $keyrmax = "RNGMAX_" . $varIDix;
					if ($DEBUG) {
						print $FH "$keyrmin\n$keyrmax\n";
					}
					
					
					my $minnotempty=0;
					my $maxnotempty=0;
					if ( exists($self->{$keyrmin}) && ($self->{$keyrmin}) ) {
						if ($DEBUG) {print $FH $i, "-th: min not empty (var id=",$varIDi,")\n";}
						$minnotempty=1;
					}
					if ( exists($self->{$keyrmax}) && ($self->{$keyrmax}) ) {
						if ($DEBUG) {print $FH $i, "-th: max not empty (var id=",$varIDi,")\n";}
						$maxnotempty=1;
					}
					
					
					# decimal check
					if ($dcmladj){
						if ($dcmlon[$i]){
								$temp[$i] = $temp[$i]/$dcmlon[$i];
						}
						# numeric cast
					} # end of decimal adj

					
					
					
					if ( ($minnotempty)  || ($maxnotempty) ) {

						if ( ($minnotempty) && ($maxnotempty) ) {
						# (1) both specified
							if ($DEBUG) {print $FH $i, "-th enters the range:both=(",$self->{$keyrmin},"|",$self->{$keyrmax}, ")";}
							
							if ( ($temp[$i] < $self->{$keyrmin}) || ( $temp[$i] > $self->{$keyrmax} ) ) {
								if ($DEBUG) {print $FH "|excluded\n";}
								goto EXCLUSION;
							}
							if ($DEBUG) {print $FH "|kept\n";}
						} elsif ( ($minnotempty) && (!$maxnotempty) ) {
						# (2) min only
							if ($DEBUG) {print $FH $i, "-th enters the range:min=(",$self->{$keyrmin},")";}
							if ($temp[$i] < $self->{$keyrmin}) {
								if ($DEBUG) {print $FH "|excluded\n";}
								goto EXCLUSION;
							}
							if ($DEBUG) {print $FH "|kept\n";}
						} elsif ( (!$minnotempty) && ($maxnotempty) ){
						# (3) max only
							if ($DEBUG) {print $FH $i, "-th enters the range:max=(",$self->{$keyrmax},")";}
							if ($temp[$i] > $self->{$keyrmax}) {
								if ($DEBUG) {print $FH "|excluded\n";}
								goto EXCLUSION;
							}
							if ($DEBUG) {print $FH "|kept\n";}
						}
					} else {

						if ($DEBUG) {print $FH $i, "-th has no range set\n";}

					}
					# subsetting by point
					# generate id
					
					my $keypoints = "XCLD_" . $varIDix;
					if (exists($self->{$keypoints}) && ($self->{$keypoints}) ){
						if ($DEBUG) {print $FH $i, "-th enters the excld-point set:",$keypoints,"\n";}

						my @pointset = split(/\0/,$self->{$keypoints});

						# XCLD_7="97\098\099"
						for (my $k=0;$k<@pointset;$k++) {
							if ($DEBUG) {print $FH $k, "-th excld point=",$pointset[$k];}
							
							# missing value case[=NA]
							if ($pointset[$k] eq $missValueSet[1]){
								
								if ( $temp[$i] eq $pointset[$k]) {
									if ($DEBUG) {print $FH "|excluded\n";}
									goto EXCLUSION;
								}
							} else {
							# non-missing value case
								if ( $temp[$i] == $pointset[$k]) {
									if ($DEBUG) {print $FH "|excluded\n";}
									goto EXCLUSION;
								}
							}
							if ($DEBUG) {print $FH "|kept\n";}
						}
					} else {
						if ($DEBUG) {print $FH $i, "-th has no point set\n";}
					}
					$temp[$i] =  0+$temp[$i];
					if ($DEBUG) {print $FH "\n";}
					
				} else {
					# char var case
					# subsetting by point only
					my $keypoints = "XCLD_" . $varIDix;
					if ($DEBUG) {
						print $FH $i, "-th var = char type\n";
						print $FH $i, "-th enters the excld-point:",$keypoints,"\n";
					}
					if (exists($self->{$keypoints}) && ($self->{$keypoints}) ){
						my @pointset = split(/\0/,$self->{$keypoints});
						# XCLD_7="XX\0XX\0XX"
						for (my $k=0;$k<@pointset;$k++) {
							if ($DEBUG) {print $FH $k, "-th excld-point=",$pointset[$k];}

							if ( $temp[$i] eq $pointset[$k]) {
								if ($DEBUG) {print $FH "|excluded\n";}

								goto EXCLUSION;
							}
							if ($DEBUG) {print $FH "|kept\n";}
						}
					} else {
						if ($DEBUG) {print $FH $i, "-th has no excld-point set\n";}
					}
					if ($DEBUG) {print $FH "\n";}

				}

			}

			if ($DEBUG) {print $FH "$. -th row will be included\n";}
			$casecounter++;
			print FILE join("\t", @temp),"\n";
		} else {
			EXCLUSION:
			# do nothing
		}
	}
	close (INFILE);
	close (FILE);
	#my $returnCode=0;
	if ($DEBUG) {
		print $FH "\n$. records processed.\n";
		print $FH "end of row-wise subsetting.\n";
		close (LOGFILE);
	}
	return $casecounter;
}

1;

__END__

=head1 NAME

VDC::DSB::CaseWiseSubset - casewise-subsetting of a data file

=head1 SYNOPSIS

    use VDC::DSB::CaseWiseSubset;
    
    my $rwsbstparam= $CGIparamSet->getRwSbst();
    my $rs = VDC::DSB::CaseWiseSubset->new($rwsbstparam, RAWFILE=>$RtmpDataRaw, OUTFILE=>$RtmpData);

    $rs->checkSubsetParams();
    my $vdc_MSV=0;  # turn-off the missing value elimination
    my $vdc_DCML=1; # turn-off the decimalization
    my $caseQnty = $rs->SubsetCaseWise(\$vdc_MSV, \$vdc_DCML);


=head1 DESCRIPTION

VDC::DSB::CaseWiseSubset subsets a tab-delimited file with a set of case-wise subsetting parameters and returns the number of subset cases.

=head1 USAGE

=head2 The Constructor

=over 4

=item new($subsetting_parameters, RAWFILE=>$raw_file_name, OUTFILE=>$subset_file_name)

    

The first argument is a reference to a hash that contains subsetting parameters for case-wise parameters

    

=over 4

=item RAWFILE

a tab-delimited file to be subset

=item OUTFILE

a subset file

=back

=back

=head2 Methods

=over 4

=item checkSubsetParams()

checks the sanity of case-wise subetting parameters. Non-zero return values indicates some error in a set of subsetting parameters.

=item SubsetCaseWise()

elimiates case accodring to a set of case-wise subsetting parameters.

=back

=head1 AUTHOR

Akio Sone, Virtual Data Center Project, Harvard University

<URL:http://thedata.org/>

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2004 President and Fellows of Harvard University

GNU General Public License

=cut


