package VDC::DSB::VLMcreator;

use strict;
use vars qw($VERSION);
$VERSION = '0.01';
use IO::File;
use Data::Dumper;

# package variables
my $TMPDIR = File::Spec->tmpdir();

my $curr_varattr={};
my $curr_locationattr={};
my $curr_chara="";
my $curr_recPrCas="";
my $curr_fileID = "";
my $curr_fileType="";
my $curr_tag ="";
my $curr_val = "";
my $ID_ATTRB = "{}ID";
my $name_ATTRB = "{}name";
my $dcml_ATTRB = "{}dcml";
my $intrvl_ATTRB ="{}intrvl";
my $StartPos_ATTRB = "{}StartPos";
my $EndPos_ATTRB = "{}EndPos";
my $RecSegNo_ATTRB = "{}RecSegNo";
my $fileid_ATTRB = "{}fileid";
my $type_ATTRB = "{}type";
my $varCounter=0;

sub new {
	my $class = shift;
	my %options =@_;
	my $self = \%options;
	my $DEBUG;
	print "self(new)=\n" .  Dumper($self) if $DEBUG;
	$self->{_recPerCase}="";
	$self->{_vlmData}=[];
	$self->{_fileType}="";
	bless $self, $class;
	return $self;
}

# start_element handler
sub start_element {
	my ($self, $element) = @_;
	my $DEBUG;
	my %atts = %{$element->{Attributes}};
	$curr_tag = $element->{Name};
	print "element=" .$curr_tag . "\n" if $DEBUG;
	if ($curr_tag eq "fileDscr") {
		print "fileID: " . $atts{$ID_ATTRB}->{Value} . "\n" if $DEBUG;
		$curr_fileID = $atts{$ID_ATTRB}->{Value};
	} elsif ($curr_tag eq 'dataDscr'){
		$curr_fileID ="";
	} elsif ($curr_tag eq "var") {
		$curr_varattr->{ID}=$atts{$ID_ATTRB}->{Value};
		$curr_varattr->{name}=$atts{$name_ATTRB}->{Value};
		
		if (exists($atts{$intrvl_ATTRB}->{Value})) {
	
			$curr_varattr->{intrvl}=$atts{$intrvl_ATTRB}->{Value};
		} else {
			$curr_varattr->{intrvl}="";
		}
		
		
		if (exists($atts{$dcml_ATTRB}->{Value})) {
			$curr_varattr->{dcml}=$atts{$dcml_ATTRB}->{Value};
		} else {
			$curr_varattr->{dcml}="";
		}

	} elsif ($curr_tag eq "location") {
		$curr_locationattr->{StartPos}=$atts{$StartPos_ATTRB}->{Value};
		$curr_locationattr->{EndPos}  =$atts{$EndPos_ATTRB}->{Value};
		$curr_locationattr->{RecSegNo}=$atts{$RecSegNo_ATTRB}->{Value};
		$curr_locationattr->{fileid}  =$atts{$fileid_ATTRB}->{Value};
	} elsif ($curr_tag eq "varFormat") {
		if ($atts{$type_ATTRB}->{Value} eq "character") {
			$curr_chara="A";
		} elsif ($atts{$type_ATTRB}->{Value} eq "numeric"){
			$curr_chara="";
		}
	}
}

# characters[text node] handler
sub characters {
	my ($self, $chara_data) = @_;
	my $DEBUG;
	my $text = $chara_data->{Data};
	print "element=" . $curr_tag . "\n" if $DEBUG;
	if ($curr_tag eq 'recPrCas') {
		$curr_recPrCas = $text;
		print "record per casec=" . $curr_recPrCas . "\n" if $DEBUG;
	} elsif ($curr_tag eq 'fileType') {
		if ($text =~ /fixed-field/){
			$curr_fileType='ff';
			print "curr_fileID =" . $curr_fileID . "\n" if $DEBUG;
			print "ifxed-field type" . "\n" if $DEBUG;
			print "fileType: text node=" . $text . "\n" if $DEBUG;
		}
	} 
}

# end_element handler
sub end_element {
	my ($self, $element) = @_;
	my $DEBUG;
	my $curr_eelement = $element->{Name};
	if ($curr_fileID eq $self->{fileID}){
		if ($curr_eelement eq 'recPrCas') {
			# store recPrCas
			print "record per case=" . $curr_recPrCas . "\n" if $DEBUG;
			print "curr_fileID =" . $curr_fileID . "\n" if $DEBUG;
			# data to be saved
			# 1. no of cards per record
			$self->{_recPerCase}=$curr_recPrCas;
		}
		if ($curr_fileType eq 'ff') {
			$self->{_fileType} = $curr_fileType;
		} else {
			$self->{_fileType} = 'nff';
		}

	} elsif ($curr_eelement eq 'var') {
		if ($curr_locationattr->{fileid} eq $self->{fileID}){
			# supply non-A variable type
			if (($curr_chara ne "A") && ($curr_chara eq "")){
				if ($curr_varattr->{intrvl} eq 'discrete'){
					$curr_chara = "I";
				} elsif ($curr_varattr->{intrvl} eq "contin"){
					$curr_chara = "F";
				} else {
					if ($curr_varattr->{dcml} eq ""){
						$curr_chara = "I";
					} else {
						$curr_chara = "I";
					}
				
				}
			
			}
			$varCounter++;
			my $dataline="";
			if ($self->{_fileType} eq 'ff') {
				$dataline=join("\t", ($curr_varattr->{ID}, $curr_locationattr->{RecSegNo}, $curr_varattr->{name}, $curr_locationattr->{StartPos}, $curr_locationattr->{EndPos}, $curr_chara, ($curr_locationattr->{EndPos} - $curr_locationattr->{StartPos} +1), $curr_varattr->{dcml})) . "\n";
			} elsif ($self->{_fileType} eq 'nff'){
				$dataline=join("\t", ($curr_varattr->{ID}, '1', $curr_varattr->{name}, $varCounter,'', $curr_chara,'','')) . "\n";
			}
			
			print $dataline if $DEBUG;
			#############################################
			# data to be saved
			# 3. column data
			push @{$self->{_vlmData}}, $dataline;
		}
	} elsif ($curr_eelement eq 'dataDscr'){
		return;
	}
	print "current element=",$curr_eelement,"\n" if $DEBUG;
}

sub end_document {
	my $DEBUG;
	print "end_document\n" if $DEBUG;
}

sub printVLMfile {
	my ($self, %options)=@_;
	my $DEBUG;
	print "self dump:\n" . Dumper($self) . "\n" if $DEBUG;
	my $VLMfileName = $self->{fileID} . ".vlm";
	print "VLM (printdata):" . $VLMfileName . "\n" if $DEBUG;
	open(VLM,">$VLMfileName") or die "cannot open $VLMfileName:$!";
	print VLM $self->{_recPerCase} . "\n";
	print VLM join("", @{$self->{_vlmData}});
	close(VLM);
}

1;
__END__

=head1 NAME
VDC::DSB::VLMcreator
=head1 VERSION

This pod refers to version 0.01 of VDC::DSB::VLMcreator.
=head1 SYNOPSIS

use VDC::DSB::VLMcreator;
use XML::SAX::ParserFactory;

my $ddiname = "09223.xml";
my $handler = VDC::DSB::VLMcreator->new(DDIname =>$ddiname, fileID=>'file5');
my $parser = XML::SAX::ParserFactory->parser(Handler => $handler);
$parser->parse_uri($ddiname);

$handler->printVLMfile();


=head1 DESCRIPTION
This module generate a VLM file(s) from a DDI by using a SAX-based parser that is specified in XML::SAX::ParserFactory.
A VLM file contains a set of  parameters for subsetting a plain-text data file.
The name of a returned VLM file is fileId_option_value.VLM.
=head1 BUGS

=head1 FILES

=head1 Author(s)
Akio Sone, VDC project
URL:http://thedata.org/

=head COPYRIGHT
Copyright (C) 2006 President and Fellows of Harvard University

=cut
