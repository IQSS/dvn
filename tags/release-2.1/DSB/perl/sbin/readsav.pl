#!/usr/bin/perl
 BEGIN {
    my ( @reqs ) = ( "v5.6.0", "strict", "warnings", "Data::Dumper", "IO::File", "VDC::DSB::Ingest::StatDataFileReaderFactory");
	push(@INC,'/usr/local/VDC/perl/lib');
    foreach my $use ( @reqs ) {
        eval "use $use";
        $@ && die "$@\n";
    }
 };

if ($#ARGV < 2){
	die "The number of arguments must be equal to or more than 3.\nUsage: perl this_script log_file codebook_file source_file(s)\n";
}

############ @ARGV elements ############## 
# 0th: output (log) filename
# 1st: output (codebook in xml) filename
# 2nd ... ($#@ARGV)th : source filename(s)

# output (meta data/ingestion log) filename
my $logfile = $ARGV[0];
my $FH = new IO::File("> $logfile");
print $FH "log-file name:",$logfile,"\n";
# output (codebook in xml) filename
my $ddi = $ARGV[1];
print $FH "ddi-file name:",$ddi,"\n";

my $base = $ddi;
   $base =~ s/\.([^\.]*)$//;
$ddiSec3 = $base . ".sec3.xml" ;
my $ddifile = [];



# the number of por data files to be processed
my $nmbrdf = $#ARGV - 1;

my ($fileid, $filenmbr, $fileno);

# /////////////////////////////////////////////////////
#                     Coding note
# This version is based on the current ingest design,
# the ingest-a-single-data-file-at-a-time design.
# While there is a for-loop below, $nmbrdf is 1 and
# it is nominal.
# 
# In the future, when some ingest-multiple-data-file-
# at-a-time design is implemented, some verctorization
# of objects and their references will be necessary
# \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

for (my $i=0; $i< $nmbrdf; $i++) {

	#my $datafile = $ARGV[$i+2];
	#my $base=$datafile;
	#$base =~ s/\.([^\.]*)$//;
	#my $tabfile = $base . '.tab';
	$filenmbr = $i+1;
	$fileid   = 'file' . $filenmbr;
	$fileno   = sprintf "%03u", $filenmbr;
	
	my $metadata= VDC::DSB::Ingest::StatDataFileReaderFactory->instantiate(SOURCEFILE=>$ARGV[$i+2],FILEID=>$fileid,FILENO=>$filenmbr,LOGFILE=>$FH);

	$metadata->parse_file();

	#$metadata->writeVarLocMapFile();
	$metadata->addSumstat();
	# $metadata->writeDDIinXML(DDIFILE=>$ddifile);
	
	$ddifile->[$i] = $base . '.sec4.' . $fileno . ".xml" ;
	
	$metadata->writeDDIsec3(DDI3FILE=>$ddiSec3);
	$metadata->writeDDIsec4(DDI4FILE=>($ddifile->[$i]));

	print $FH "parsing result:\n",Dumper($metadata),"\n";
}

	&VDC::DSB::Ingest::StatData::mergeSubDDIs(DDI=>$ddi, DDI3FILE=>$ddiSec3, DDIFILES=>$ddifile);

	my $nofldl =  unlink($ddiSec3);
	if ($nofldl){
		print $FH "temp xml.sec3 file (",$ddiSec3,") deleted\n";
	}
	$nofldl =  unlink(<$base.sec4.*.xml>);
	if ($nofldl){
		print $FH "temp xml.sec4 (",$nofldl,") sub files deleted\n";
	}
