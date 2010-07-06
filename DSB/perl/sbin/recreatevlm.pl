#!/usr/bin/perl
BEGIN
{
    my ( @reqs ) = ( "v5.6.0", "strict", "File::Copy" );
    foreach my $use ( @reqs )
    {
        eval "use $use";
        $@ && die "$@\n";
    }
};

if ($#ARGV < 2)
{
    print STDERR "Usage: recreatecol.pl base_dir study_num filenumber [type]\n\n"; 
    die "(The number of arguments must be equal to 3 or 4).\n";
}

# The set up of directories
# change the following 3 directory settings before using this script:
# $SCRPTDIR 	the location (absolute path) of this script
# $XSLTDIR		the location (absolute path) of xslt stylesheet, rcrtcl.xsl
# $ABSWRKDIR 		the study directory where the DDI lives

my $ABSWRKDIR   = "$ARGV[0]";
my $DDI         = "$ARGV[1]";
my $FILEID    = "$ARGV[2]";
my $type        = "$ARGV[3]";

my $VDCROOT   = "/usr/local/VDC";

my $SCRPTDIR  = "/home/asone/scrpt";
my $XSLTDIR   = "";

###my $ABSWRKDIR = "$BASEDIR/$WRKDIR";

unless ( $XSLTDIR )
{
    $XSLTDIR = $VDCROOT . "/xslt"; 
}

our ($DEBUG, $FH);
$DEBUG = 0;
if (!$DEBUG) {
	$FH = *STDOUT;
} else {
	$FH = *LOGFILE;
	my $logfile = "$ABSWRKDIR/vlm.$FILEID.log"; 
	open(LOGFILE, ">$logfile") or die "cannot open $logfile:$!";
}

my $XSLTENGINE="xsltproc";
###my $DDI=$WRKDIR . ".merged.xml";


my $CLXSLSHEET="$XSLTDIR/rcrtcl.xsl";
$CLXSLSHEET="$XSLTDIR/rcrtcl_tab.xsl" if $type eq "tab";


#my $rjstfd = sprintf "%03u", $filenmbr;
#my $FILEID ="file" . $filenmbr;
#my $COLFILE="datafile." . $rjstfd . ".vlm";
my $COLFILE=$FILEID . ".vlm";
my $LSTX=join('',('"',"ddi:location/\@fileid='",$FILEID,"'",'"'));
# "ddi:location/@fileid='file1'"

	#$start=time();
	my ($laststep, $lasterrcd) ;
	my $pecntr=0;
	chdir $ABSWRKDIR;

		open(PH, "$XSLTENGINE --timing --novalid -o $COLFILE --stringparam lstx $LSTX --stringparam fileid $FILEID $CLXSLSHEET $DDI  2>&1 |");

		while (<PH>) {
			if (/(failed)|(unable)/){
				print $1,",", $2, "\n";
				$pecntr++;
			}
		}
		close(PH);
#	chdir $BASEDIR;
	if ($pecntr) {
		print $FH "generating the col file failed\n";
	}
	#$end=time();
#print "elapsed time=",$end-$start," seconds\n";


