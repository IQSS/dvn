
package vdcRAP::Subsetting;

use vars qw(@ISA @EXPORT @EXPORT_OK %EXPORT_TAGS $VERSION);

@ISA =     qw(Exporter); 

use strict;

use Exporter; 

$VERSION = 0.01; 

@EXPORT =  qw();


sub new_SubObject {

    my $self = {};

    bless $self;

    return $self;
}

sub produce_subset_filter {
    my $self = shift; 
    my $coldef_file = shift; 
    my $file_type = shift;
    my $varheader = shift; 
    my $cols = shift; 

    if ( $file_type =~/tab/ )
    {
	return $self->produce_subset_filter_tab ( $coldef_file, $varheader, $cols ); 
    }

    if ( $file_type =~/fixed/ )
    {
	return $self->produce_subset_filter_fixed ( $coldef_file, $varheader, $cols ); 
    }

    $self->{'error'} = "invalid datafile type; the only supported types are 'tab' and 'fixed'";
    return undef;
}

sub produce_subset_filter_tab  {
    my $self = shift; 
    my $coldef_file = shift; 
    my $varheader = shift; 
    my $cols = shift; 

    unless ( open ( C, $coldef_file ) )
    {
	$self->{'error'} = "could not open subsetting metadata file " . 
	    $coldef_file;
	return undef; 
    }

    my %coldef_hash;

    my @tokens; 

    my $label; 
    my $type; 

    my @header_specs; 

    while ( <C> )
    {
	chomp $_;
	split ( "\t", $_, 2 ); 

	if ($. == 1) 
	{
# tab files can't have virtual records
# made of multiple physical lines;
#	    $ret->{levels} = $_[0];

	    next;
	}

	$coldef_hash{$_[0]} = $_[1];

	my @tmp_tokens = split ( /\t/, $_ );	
	$coldef_hash{$tmp_tokens[2]} = $_[1];


# when the $cols argument isn't supplied we give them the entire contents 
# of the file; they still might want the variable label header, so we 
# have to create it for them, in the order in which the columns appear in 
# the file (and in the .vlm file):

	unless ( $cols )
	{
	    @tokens = split ( "\t", $_[1] ); 

	    $label     = $tokens[1]; 
	    $type      = $tokens[4]; 

	    if ( $type && ( $varheader ne "labelsonly" ) && ( $varheader ne "headeronly" ) )
	    {
		push (@header_specs, $label . "(" . $type . ")"); 
	    }
	    else
	    {
		push (@header_specs, $label); 
	    }
	}
    }
    
    close C;

    unless ( $cols )
    {
	$self->{'varheader'} = join ("\t", @header_specs) . "\n";
	return 'cat';
    }

    my @varids_list = split ( ',', $cols ); 
    
    my $filter = ""; 

    my $varid; 
    my $colnumber; 
    my $col_arg = ""; 

    my @col_specs; 

    for $varid (@varids_list)
    {
	unless ( $coldef_hash{$varid}  ) 
	{
	    $self->{'error'} = "no mapping for $varid found.";
	    return undef; 
	}
	
	@tokens = split ( "\t", $coldef_hash{$varid} ); 

	$label = $tokens[1]; 
	$colnumber = $tokens[2]; 
	$type = $tokens[4]; 
	push (@col_specs, $colnumber); 

	if ( $type && ( $varheader ne "labelsonly" ) && ( $varheader ne "headeronly" ) )
	{
	    push (@header_specs, $label . "(" . $type . ")"); 
	}
	else
	{
	    push (@header_specs, $label); 
	}
    }	

    $col_arg = join (",", @col_specs); 
    $self->{'varheader'} = join ("\t", @header_specs) . "\n";

    $filter = "/usr/local/VDC/bin/rcut -f" . $col_arg; 
    return $filter; 
}


sub produce_subset_filter_fixed  {
    my $self = shift; 
    my $coldef_file = shift; 
    my $varheader = shift; 
    my $cols = shift; 

    my @tokens; 
    my $level = -1;

# no range expansion for now. 

#   my @rawcolnums = split ( ',', $cols ); 
#   my ($rawcol, $a, $b);
#   my @colnums;
#   for $rawcol (@rawcolnums){
#		   if ( $rawcol =~/^[0-9]+$/ ){
#			push ( @colnums, $rawcol ); 
#		   } elsif ( $rawcol =~/^[0-9]+\-[0-9]+$/ ) {
#		  	($a, $b) = split ('-', $rawcol, 2); 
#		  	while ( $a <= $b ){
#		  		push ( @colnums, $a++ ); 
#		  	}
#		   }
#   }

    # let's read in the col file:

    select STDOUT; #?

    unless ( open ( C, $coldef_file ) )
    {
	$self->{'error'} = "could not open subsetting metadata file " . 
	                  $coldef_file;
	return undef; 
    }

    my $logical_records; 
    my %coldef_hash;

    my $filter = ""; 
    
    my $varid; 

    my $level;  # level = the number of the physical sub-record in a 
                # multi-line virtual record
    my $label; 
    my $offset; 
    my $length;
    my $type; 

    my @col_specs; 

    my @header_specs; 

    my $col_arg = ""; 
	open(TMPZ,"> /tmp/sb.$$.txt");
	my $cString="";

    while ( <C> )
    {
	chomp $_;
	split ( "\t", $_, 2 ); 

	if ($. == 1) 
	{
	    $logical_records = $_[0];
	    $logical_records = 1 unless $logical_records > 1; 
	    next;
	}

	my @tmp_tokens = split ( /\t/, $_ );	

	$coldef_hash{$_[0]} = $_[1];
	$coldef_hash{$tmp_tokens[2]} = $_[1];

# when the $cols argument isn't supplied we give them the entire contents 
# of the file; they still might want the variable label header, so we 
# have to create it for them, in the order in which the columns appear in 
# the file (and in the .vlm file):
	unless ( $cols )
	{
	    @tokens = split ( "\t", $_[1] ); 
	    $type = $tokens[4];
	    $type .= ":$tokens[6]" if $tokens[6]; 

	    $level  = $tokens[0];
	    $label  = $tokens[1]; 
	    $offset = $tokens[2]-1;
	    $length = $tokens[5];

	    push @col_specs, $level . ":" . ($offset+1) . "-" . ($offset+$length);
	    
		if ($tokens[6]){
			$cString = $cString . $level . ":" . ($offset+1) . "-" . ($offset+$length) . "\@" . $tokens[6];
		} elsif ($tokens[4] eq 'A') {
			$cString = $cString . $level . ":" . ($offset+1) . "-" . ($offset+$length) . "\@" . 'c';
		} else {
			$cString = $cString . $level . ":" . ($offset+1) . "-" . ($offset+$length);
		}
		$cString .= ",";
		print TMPZ "$cString\n";
		print TMPZ "$col_arg\n";

	    if ( $type && ( $varheader !~/labelsonly/i ) && ( $varheader !~/headeronly/i )  )
	    {
		push (@header_specs, $label . "(" . $type . ")"); 
	    }
	    else
	    {
		push (@header_specs, $label); 
	    }
	}
    }

    close C;

    unless ( $cols )
    {
		$self->{'varheader'} = join ("\t", @header_specs) . "\n";
		chop($cString);
		$col_arg = join (",", @col_specs); 
    	#$filter = "/usr/local/VDC/bin/rcut -r" . $logical_records . " -c" . $cString . " -o"; 
    	$filter = "/usr/local/VDC/bin/rcut -r" . $logical_records . " -c$cString" . " -o"; 
		return $filter; 
    }

    # if the $cols argument was supplied, we create the variable label
    # header and the rcut filter to produce the columns in the order 
    # the user requested: 

    my @varids_list = split ( ',', $cols ); 

    for $varid (@varids_list)
    {
	unless ( $coldef_hash{$varid} ) 
	{
	    $self->{'error'} = "no mapping for $varid found.";
	    return undef; 
	}

	@tokens = split ( "\t", $coldef_hash{$varid} ); 

	$type = $tokens[4];
	$type .= ":$tokens[6]" if $tokens[6]; 

	$level  = $tokens[0];
	$label  = $tokens[1]; 
	$offset = $tokens[2]-1;
	$length = $tokens[5];

	push @col_specs, $level . ":" . ($offset+1) . "-" . ($offset+$length); 
	if ($tokens[6]){
		$cString = $cString . $level . ":" . ($offset+1) . "-" . ($offset+$length) . "\@" . $tokens[6];
	} elsif ($tokens[4] eq 'A') {
		$cString = $cString . $level . ":" . ($offset+1) . "-" . ($offset+$length) . "\@" . 'c';
	} else {
		$cString = $cString . $level . ":" . ($offset+1) . "-" . ($offset+$length);
	}
	$cString .= ",";
	if ( $type && ( $varheader !~/labelsonly/i ) && ( $varheader !~/headeronly/i ) )
	{
	    push (@header_specs, $label . "(" . $type . ")"); 
	}
	else
	{
	    push (@header_specs, $label); 
	}
    }	
	chop($cString);
	print TMPZ "new=$cString\n";
    $col_arg = join (",", @col_specs); 
    $self->{'varheader'} = join ("\t", @header_specs) . "\n";
    print TMPZ "old=$col_arg\n";
	close(TMPZ);
    
    #$filter = "/usr/local/VDC/bin/rcut -r" . $logical_records . " -c" . $cString . " -o"; 
    #$filter = "/usr/local/VDC/bin/rcut -r" . $logical_records . " -c$col_arg" . " -o"; 
     $filter = "/usr/local/VDC/bin/rcut -r" . $logical_records . " -c$cString" . " -o"; 

    return $filter; 
}

1; 


