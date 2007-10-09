#!/usr/bin/perl 
BEGIN {
	my ( @reqs ) = ( "v5.6.0", "strict", "warnings", "Data::Dumper", "IO::File", 
	"VDC::DSB::Ingest::StatDataFileReaderFactory");
	#push(@INC,'/home/asone/new4/lib');
	push(@INC,'/usr/local/VDC/perl/lib');
	foreach my $use ( @reqs ) {
		eval "use $use";
		$@ && die "$@\n";
	}
};

my $noflsets = ($#ARGV-2)/2;

if ($#ARGV < 3){
	print STDERR "Usage: [perl] this_script.pl (0)log_file (1)codebook_file (2)source_type {na|sps} (3)source_data_file(s)\n\n";
	die "The number of arguments must be equal to or more than 4.\n";
} elsif (($ARGV[0] eq 'sps') && (($#ARGV-2)%2 !=0)) {
	print STDERR "the number of syntax files is not equal to the number of data files.\n";
	print STDERR "Usage: [perl] this_script.pl (0)log_file (1)codebook_file (2)source_type {na|sps} (3) sps file(s) data_file(s)\n\n";
	die "check the command line arguments, especially, syntax/data filenames.\n";
}

my $supportedformats = {
	dta =>'DTA',
	por =>'POR',
	sav =>'SAV',
	sps =>'SPS',
	na  =>'OTHER'
};

#unless (exists($supportedformats->{$ARGV[2]})){
#	print STDERR "the specified format-type(",$ARGV[2],") cannot be handled by this script!\n";
#	die "unspported source-type (error at 3rd command-line argument)";
#}

my $DEBUG =1;
my ($log_file, $FH);

if ($DEBUG){
	$log_file = $ARGV[0]; 
	$FH = new IO::File("> $log_file");
	#$FH = *STDOUT;
}
print $FH "source type=",$ARGV[2],"\n";
if ($ARGV[2] eq 'sps') {print $FH "no of source files=",$noflsets,"\n";}

if ($ARGV[2] eq 'sps') {
	print $FH "The following $noflsets file pair(s) will be processed\n";
	for (my $i=0;$i<$noflsets;$i++) {
		print $FH $i, "-th pair: (syntax file=",$ARGV[$i+3],"; data file=",$ARGV[$noflsets+$i+3],")\n" ; 
	}
}

############ @ARGV elements ############## 
# 0th: log_file
# 1st: codebook_file
# 2nd: source type (sav, por, dta, sps)
# 3rd + : source datafile names
# or 
# 1st set : spss data definition (sps)) file names
# 2nd set : corresponding data file names


my $ddi = $ARGV[1] ;
my	$ddiSec3 = $ddi;
	$ddiSec3 =~ s/\.([^\.]*)$//;
$ddiSec3 = $ddiSec3 . ".sec3.xml" ;


my @spsfile;
if ($ARGV[2] eq 'sps'){
	@spsfile  = @ARGV[3..($noflsets+2)];
	@sourcefile = @ARGV[($noflsets+3)..$#ARGV];
} else{
	@sourcefile = @ARGV[3..$#ARGV];
}
print $FH "source data files=",join(",", @sourcefile),"\n";

my @VLMFILES=();
my @TABFILES=();
my ($fileid, $filenmbr, $fileno,$location);
my (@base, @logfile, @vlmfile, @tabfile);
my (@source, @VLM, @dlmtdfl, @update);
my $ddifile = [];
my @metadata=();
my $SOURCETYPE = $supportedformats->{$ARGV[2]};

my $nofiles;
if ($ARGV[2] eq 'sps') {
	$nofiles = $noflsets;
} else {
	$nofiles = scalar(@sourcefile);
}
my @failedfiles=();
print $FH "no of source files=",$nofiles,"\n";
for (my $i=0; $i< $nofiles; $i++) {
	$filenmbr = $i+1;
	$fileid   = 'file' . $filenmbr;
	$fileno   = sprintf "%03u", $filenmbr;
	
	$base[$i]     = $sourcefile[$i];
	$base[$i]     =~ s/\.([^\.]*)$//;

	$logfile[$i]   = $base[$i] . '.log';
	$ddifile->[$i] = $base[$i] . '.sec4.' . $fileno . ".xml" ;
	$vlmfile[$i]   = $base[$i] . '.vlm';
	
	# ////////////////////////////////////////////////
	# step 1i: extract metadata from each source file
	# ////////////////////////////////////////////////
	
	if ($ARGV[2] eq 'sps'){
		$metadata[$i]= VDC::DSB::Ingest::StatDataFileReaderFactory->instantiate(SOURCEFILE=>$sourcefile[$i],FILEID=>$fileid,FILENO=>$filenmbr,LOGFILE=>$FH,SPSFILE=>$spsfile[$i], ICPSR=>1);
	} else {
		$metadata[$i]= VDC::DSB::Ingest::StatDataFileReaderFactory->instantiate(SOURCEFILE=>$sourcefile[$i],FILEID=>$fileid,FILENO=>$filenmbr,LOGFILE=>$FH);
	}
	unless ($metadata[$i]){
		print $FH "StatDataFactory failed at $i-th iteration(source file: $sourcefile[$i])\n";
		push @failedfiles, $i;
		next;
	}
	$metadata[$i]->parse_file();
	print $FH "meta data:\n",Dumper($metadata[$i]),"\n" if $DEBUG;
	
	# ////////////////////////////////////////////////
	# step 2i: calculate sumstat/catstat/unf data
	# ////////////////////////////////////////////////
	
	$metadata[$i]->addSumstat();
	print $FH "after sumstat: self =\n",Dumper($metadata[$i]),"\n" if $DEBUG;
	
	# ////////////////////////////////////////////////
	# step 3i: dump metadata as DDI-sections 3 and 4
	# ////////////////////////////////////////////////
	
	#$metadata->writeDDIinXML(DDIFILE=>$ddifile);
	$metadata[$i]->writeDDIsec3(DDI3FILE=>$ddiSec3);
	$metadata[$i]->writeDDIsec4(DDI4FILE=>($ddifile->[$i]));
	
	# ////////////////////////////////////////////////
	# step 4i: generate a Variable-Location-Mapping (VLM) file
	# ////////////////////////////////////////////////
	
	$metadata[$i]->writeVarLocMapFile(VLMFILE=>$vlmfile[$i]);
}

if ( (-e $ddiSec3) && (-s $ddiSec3) ){
	# ////////////////////////////////////////////////
	# step 5: merge DDI-sections 3 and 4
	# ////////////////////////////////////////////////

	&VDC::DSB::Ingest::StatData::mergeSubDDIs(DDI=>$ddi, DDI3FILE=>$ddiSec3, DDIFILES=>$ddifile);

	# ////////////////////////////////////////////////
	# step 6: validate the merged DDI
	# ////////////////////////////////////////////////

	my $chkvalid = &VDC::DSB::Ingest::StatData::validateDDI($ddi);
	# $chkvalid must be 0 if the merged DDI is valid

	if ($chkvalid){
		print $FH "validation result=",$chkvalid,"\n" if $DEBUG;
	} else {
		print $FH "validation passed\n" if $DEBUG;
	}

	my $nofldl =  unlink($ddiSec3);
	if ($nofldl){
		print $FH "temp xml.sec3 file (",$ddiSec3,") deleted\n";
	}
	for (my $i=0; $i<@{$ddifile};$i++) {
		$nofldl =  unlink($ddifile->[$i]);
	}
	if ($nofldl){
		print $FH "temp xml.sec4 (",$nofldl,") sub files deleted\n";
	}
	if (scalar(@failedfiles)){
		print $FH "The requested processing is partially failed.\n";
		print $FH "list of Failed files:\n";
		foreach my $el (@failedfiles){
			print $FH $el,"-th file\tsource file=",$sourcefile[$el],"\n";
		}
	}
	for (my $i=0; $i<@base;$i++) {
		#rename "$base[$i].$$.tab", "$base[$i].tab";
		unlink "$base[$i].$$.tab";
	}
} else {
	print $FH "The requested processing failed.\n";
	print $FH "list of Failed files:\n";
	foreach my $el (@failedfiles){
		print $FH $el, "-th file\tsource file=",$sourcefile[$el],"\n";
	}
}
