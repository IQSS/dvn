package VDC::DSB::DDISAXparser;
# 
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



# package variables
my $curr_varattr={};
my $curr_chara="";
my $curr_fileID = "";
my $curr_var="";
my $curr_varLabel;
my $curr_catval =[];
my $curr_catvalset =[];
my $curr_tag ="";
my $curr_level ="";
my $curr_val = "";
my $curr_mv =[];
my $ID_ATTRB = "{}ID";
my $name_ATTRB = "{}name";
my $dcml_ATTRB = "{}dcml";
my $fileid_ATTRB = "{}fileid";
my $vartype_ATTRB = "{}type";
my $intrvl_ATTRB  = "{}intrvl";
my $level_ATTRB = "{}level";
my $VALUE_ATTRB = "{}VALUE";
my $max_ATTRB = "{}max";
my $min_ATTRB = "{}min";
my $varformatname_ATTRB ="{}formatname";
my $varformatcatgry_ATTRB="{}category";
my $varformatschema_ATTRB="{}schema";
my $novars;
my $varcntr=0;

sub new {
	my $class = shift;
	my $self = { @_ };
	$self->{_varNameA}=[];
	$self->{_varNameAsafe}=[];
	$self->{_varNameH}={};
	$self->{_varNameHsafe}={};
	$self->{_varType}=[];
	$self->{_varLabel}=[];
	$self->{_varNo}=[];
	$self->{_varNoMpTbl}={};
	$self->{_valLblTbl}={};
	$self->{_mssvlTbl}={};
	$self->{_charVarTbl}={};
	$self->{_varFormat}={};
	$self->{_formatName}={};
	$self->{_formatCatgry}={};
	#$self->{_charVarTbl}=[];
	$self->{unsafeVarName}=0;
	$novars = scalar(keys(%{$self->{VarID}}));
	
	bless $self, ref($class)||$class;
	return $self;
}

# start_element handler
sub start_element {
	my ($self, $element) = @_;
	my $varIDset = $self->{VarID};
	my $atts = $element->{Attributes};
	$curr_tag = $element->{Name};
	# if ($curr_tag eq "var") {print "///////// var: $atts->{$name_ATTRB}->{Value} $atts->{$ID_ATTRB}->{Value} /////////\n";}
	#print "element=",$curr_tag,"\n";
	if ( ($curr_tag eq "var") && (exists($varIDset->{$atts->{$ID_ATTRB}->{Value}})) ) {
		# <var> tag filter
		#print "++++++ $atts->{$ID_ATTRB}->{Value} var: $atts->{$name_ATTRB}->{Value} ++++++\n";
		$varcntr++;
		# <var>-tag identifier
		$curr_var='y';
		$curr_varattr->{ID}   = $atts->{$ID_ATTRB}->{Value};
		$curr_varattr->{name} = $atts->{$name_ATTRB}->{Value};
		push @{$self->{_varNameA}}, $curr_varattr->{name};
		
		push @{$self->{_varNo}},    $curr_varattr->{ID} ;
		$self->{_varNameH}->{ $curr_varattr->{ID} } = $curr_varattr->{name};
		
		
		$self->{_varNoMpTbl}->{ $curr_varattr->{ID} } = $varcntr;
		$curr_varLabel="";
		if (exists($atts->{$intrvl_ATTRB}->{Value})) {
			if ($atts->{$intrvl_ATTRB}->{Value} eq 'contin'){
				$curr_varattr->{type}=2;
			} elsif ($atts->{$intrvl_ATTRB}->{Value} eq 'discrete'){
				$curr_varattr->{type}=1;
			}
		} else {
			if (exists($atts->{$dcml_ATTRB}->{Value})) {
				if ($atts->{$dcml_ATTRB}->{Value}) {
					$curr_varattr->{type}=2;
				} else {
					$curr_varattr->{type}=1;
				}
			} else {
				$curr_varattr->{type}=1;
			}
		}
	} elsif ($curr_tag eq "item") {
		# <item> tag filter
		if ($curr_var eq 'y'){
			push @{$curr_mv}, [ $atts->{$VALUE_ATTRB}->{Value} ];
		}
	} elsif ($curr_tag eq "range") {
		# <range> tag filter
		if ($curr_var eq 'y'){
			if (  (exists($atts->{$max_ATTRB})) && (exists($atts->{$min_ATTRB}))  ) {
				# both
				push @{$curr_mv}, [$atts->{$min_ATTRB}->{Value} , $atts->{$max_ATTRB}->{Value}];

			} elsif (  (exists($atts->{$max_ATTRB})) && (!exists($atts->{$min_ATTRB}))  ){
				# max ony
				push @{$curr_mv}, ['-Inf', $atts->{$max_ATTRB}->{Value}];
			} elsif (  (!exists($atts->{$max_ATTRB})) && (exists($atts->{$min_ATTRB}))  ){
				# min only
				push @{$curr_mv}, [$atts->{$min_ATTRB}->{Value}, 'Inf'];
			}
		}
	} elsif ($curr_tag eq "catgry") {
		#<catgry> tag filter
		if ($curr_var eq 'y'){
			$curr_catval=[undef, undef];
		}
	} elsif ($curr_tag eq "labl") {
		# <label> tag filter
		if ($curr_var eq 'y'){
			$curr_level = $atts->{$level_ATTRB}->{Value};
		}
	} elsif ($curr_tag eq "varFormat") {
		# <varFormat> tag filter
		if ($curr_var eq 'y'){
			# @type
			if ($atts->{$vartype_ATTRB}->{Value} eq "character") {
				$curr_varattr->{type} -=1;
				#push @{$self->{_charVarTbl}}, $curr_varattr->{ID} ;
				$self->{_charVarTbl}->{ $curr_varattr->{ID} } = 'y';
			}
			# spss specific 
			if (uc($atts->{$varformatschema_ATTRB}->{Value}) eq "SPSS"){
				# @formatname
				if ($atts->{$varformatname_ATTRB}->{Value}){
					$self->{_formatName}->{$curr_varattr->{name}}=$atts->{$varformatname_ATTRB}->{Value};
					$self->{_varFormat}->{$curr_varattr->{name}}= &getFormatToken($atts->{$varformatname_ATTRB}->{Value});
				}
				# @category
				if ($atts->{$varformatcatgry_ATTRB}->{Value}) {
					$self->{_formatCatgry}->{$curr_varattr->{name}}=$atts->{$varformatcatgry_ATTRB}->{Value};
				}
			} elsif (uc($atts->{$varformatschema_ATTRB}->{Value}) eq "OTHER"){
				# @formatname
				if ($atts->{$varformatname_ATTRB}->{Value}){
					$self->{_formatName}->{$curr_varattr->{name}}=$atts->{$varformatname_ATTRB}->{Value};
					my $fmt= $atts->{$varformatname_ATTRB}->{Value};
					$fmt =~ s/\%//;
					$self->{_varFormat}->{$curr_varattr->{name}}= $fmt;
				}
				# @category
				if ($atts->{$varformatcatgry_ATTRB}->{Value}) {
					$self->{_formatCatgry}->{$curr_varattr->{name}}=$atts->{$varformatcatgry_ATTRB}->{Value};
				}
			}
		}
	}
}

# characters[text node] handler
sub characters {
	my ($self, $chara_data) = @_;
	# var - labl(variable) 
	# catgry - catValu()
	# catgry - labl(CATEGORY)
	
	if ($curr_var eq 'y'){
	
		my $text = $chara_data->{Data};
		$curr_val = $text;
		
		#print "characters: element=",$curr_tag,"\n";
		if ($curr_tag eq 'catValu') {
			#print "catValu=",$curr_val,"\n";
			$curr_catval->[0]=$curr_val;
		} elsif ($curr_tag eq 'labl') {
			# var case
			if ($curr_level eq 'variable'){
				#print "var_name=",$curr_varattr->{name},"\n";
				#print "var_labl=",$curr_val,"\n";
				#my $tmp = $curr_varattr->{name};
				#$self->{_varLabel}->{$tmp}= $curr_val;
				$curr_varLabel = $curr_val;
			}

			# catgry case
			if ( ($curr_level eq 'CATEGORY') || ($curr_level eq 'category') ) {
				#print "value_labl=",$curr_val,"\n";
				$curr_catval->[1]=$curr_val;
			}
		}
	}
}

# end_element handler
sub end_element {
	my ($self, $element) = @_;
	my $curr_eelement = $element->{Name};
	
	if ($curr_var eq 'y'){
	
		if ($curr_eelement eq 'var') {
			my $tmp=$curr_varattr->{name};
			# valueTable
			if (scalar(@{$curr_catvalset})){
				$self->{_valLblTbl}->{$tmp}=$curr_catvalset;
			}
			$curr_catvalset=[];
			# varType
			push @{$self->{_varType}}, $curr_varattr->{type};
			
			$curr_varattr={};
			$curr_var = undef;
			
			push @{$self->{_varLabel}}, $curr_varLabel;
			$curr_varLabel = undef;
			
			if ( scalar(@{$self->{_varNameA}}) == $novars ) {
				#print TMPX "all var tags are processed\n";
				die "all var tags are processed";
			}
			
		} elsif ($curr_eelement eq 'invalrng') {
			# push @$invalrng, $tmpinvalrng;
			$self->{_mssvlTbl}->{"$curr_varattr->{name}"}=$curr_mv;
			$curr_mv=[];
		} elsif ($curr_eelement eq 'catgry') {
			if ($curr_catval->[1] ne ''){
				push @{$curr_catvalset}, $curr_catval;
			}
			$curr_catval=[];
		} elsif ($curr_eelement eq 'labl') {
			$curr_level=undef;
		}
		#print "current element=",$curr_eelement,"\n";
		$curr_tag=undef;
	}
}

sub end_document {
	#my ($self, $element) = @_;
	#print "\nend_document\n";
}

sub passMetadata {
	my $self = shift;
	my $temp= {};

	while ((my $key, my $value) = each(%{$self})) {
		$temp->{$key} = $value;
	}
	
	return $temp;
}


sub getFormatToken{
	my $rawToken =shift;
	my $DEBUG;
	my @tmp  = split(//,$rawToken);

	my $tmps="";
	foreach my $e (@tmp){
		if (ord($e) < 65) {
			last;
		} else {
			$tmps .= $e;
		}
	}
	print "raw Token=",$rawToken,"\tformat=",$tmps,"\n" if $DEBUG;
	return $tmps;
}


1;

__END__


=head1 NAME

VDC::DSB::DDISAXparser - XML-SAX parser of a DDI(Data Documentation Initiative) file

=head1 DEPENDENCIES

=head2 Nonstandard Modules

    XML::SAX::ParserFactory

=head1 SYNOPSIS

    use VDC::DSB::DDISAXparser;
    
    my $ddimtdt = VDC::DSB::DDISAXparser->new(DDIname =>$ddiforR, VarID=>$varIDhsh);
    my $parser = XML::SAX::ParserFactory->parser(Handler => $ddimtdt);

    eval {
        $parser->parse_uri($ddiforR);
    };
    if ($@){
        if ($@ =~ /^all var tags are processed/) {
            # parsing has been successfully finished;
        } else {
            # some parsing error occured
            {my($ts)=$@; die $ts;}
        }
    }

    # get a reference to retrieved metadata 
    my $MD =$ddimtdt->passMetadata();


=head1 DESCRIPTION

VDC::DSB::DDISAXparser parses a DDI (XML) file and returns requested variables' metadata.

=head1 USAGE

=head2 The Constructor

=over 4

=item new()

takes two arguments: a DDI name to be parsed (DDIname) and a reference to a hash of variable IDs (VarID) whose metadata are requested

=back

=head2 Methods

=over 4

=item passMetadata()

returns a reference to requested variables' metadata.

=back

=head1 AUTHOR

Akio Sone, Virtual Data Center Project, Harvard University

(<URL:http://thedata.org/>)

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2004 President and Fellows of Harvard University

GNU General Public License



=cut

