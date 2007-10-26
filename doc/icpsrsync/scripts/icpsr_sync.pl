#!/usr/bin/perl

$studyid = ""; 
$date = "";

$arg = shift @ARGV; 

if ( $arg =~/[0-9][0-9]\-[A-Z][A-Z][A-Z]\-[0-9][0-9][0-9][0-9]/ )
{
    $date = $arg; 
}
elsif ( $arg =~/^[0-9][0-9][0-9][0-9][0-9]$/ )
{
    $studyid = $arg;
}

$repository = shift @ARGV;

unless ( ( ( $studyid ne "" ) || ( $date ne "" ) )
	 &&
	 $repository )
{
    print STDERR "usage: \nicpsr_sync.pl <DD-MMM-YYYY> <REPOSITORY>\n";
    print STDERR "or\n";
    print STDERR "icpsr_sync.pl <STUDY> <REPOSITORY>\n";

    exit 1; 
}

$classrest = shift @ARGV;  
unless ( $classrest ) 
{
    $ou = $repository; 
    $ou =~s/[^a-zA-Z0-9]/_/g;
    $classrest = "vdcClass=PRIVATE_OBJ,ou=" . $ou . ",o=vdc";
    $classfree = shift @ARGV || "vdcClass=PUBLIC_OBJ,o=vdc";
}

$datalimit = 100000000; 

$ENV{'PATH'} = "/usr/local/vdc-admin/bin:/usr/local/vdc-admin/icpsrsync/bin:" . $ENV{'PATH'}; 

###goto MYLABEL2; 



# 1. get the ICPSR metadata for the new/updated studies: 

#system ( "lynx -width=1024 --dump 'http://www.icpsr.umich.edu/cgi/mdqry.prl?date=$date' | grep '^Study:' | sed 's/Study:.//' | sort -u > mdqry.list" );

if ( $date ne "" )
{

    system ( "links -source 'http://www.icpsr.umich.edu/cgi-bin/mdquery?date=$date' | grep '^Study:' | sed 's/Study:.//' | sort -u > mdquery.list" );

    if ( -f "mdquery.list" )
    {
	unlink "mdquery.raw";
	
	open ( LST, "mdquery.list" ); 
	
	while ( $study = <LST> )
	{
	    chop $study; 
	    system ( "links -source 'http://www.icpsr.umich.edu/cgi-bin/mdquery?study=$study' | grep -v '^\$' >> mdquery.raw" );
	}
	close LST;
    }
    else
    {
	print STDERR "failed to create mdquery.list.\n";
	exit 1; 
    }
}
else 
{
    system ( "links -source 'http://www.icpsr.umich.edu/cgi-bin/mdquery?study=$studyid' | grep -v '^\$' > mdquery.raw" );
}

system ( "script_mdqry_merge-line.pl  < mdquery.raw  > mdquery.out" ); 
   
MYLABEL2:


# the above has created the file mdquery.out (if we are lucky)

# 2. parse the records produced in step 1.

system ( "script_parse_mdquery_new.pl $repository < mdquery.out > mdquery.spss" ); 

system ( 'sort -n +2 -3 mdquery.spss | awk "{if (\$3<=' . $datalimit . ') print \$0}" > mdquery.spss.sorted' );
system ( 'sort -n +2 -3 downloads | awk "{if (\$3<=' . $datalimit . ') print \$0}" > downloads.sorted' );



### 3. download ICPSR-supplied study-level DDIs:


print STDERR "downloding the study-level DDIs from the ICPSR...\n";

open ( LS, "ls -d [0-9][0-9][0-9][0-9][0-9] |" ) || die $@;
#open ( LS, "list.studies.processing.final" ) || die $@;
#open ( LS, "potentiallysubsettable.list" ) || die $@;

while ( <LS> )
{
    chop;
    system ( "links -source 'http://www.icpsr.umich.edu/cgi-bin/rawxml.prl?study=$_'  > $_/$_.icpsr.xml" );
    print STDERR '.';
}


close LS;


## MYLABEL: 

print STDERR "\n";
print STDERR "...done\n";



# validate: 
system ( "java -jar /usr/local/msv/msv.jar /usr/local/VDC/UIS/VDC/Schema/DDI/Version2-1.dtd `find [0-9][0-9][0-9][0-9][0-9] -name '*.icpsr.xml'` 2>&1 | tee VALIDATE.icpsr.out" );

system ( "cat VALIDATE.icpsr.out | script_select_valid.pl list.icpsrxml.valid" ); 

open ( LS, "list.icpsrxml.valid" ) || die $@;

while ( <LS> )
{
    chop;
    system ( "script_edit_icpsrddi.pl 'Version2-1.dtd' $repository $_ < $_/$_.icpsr.xml > $_/$_.study.xml" );
    
    print STDERR '+';
}

close LS;

print STDERR "\n";

# validate: 
system ( 'java -jar /usr/local/msv/msv.jar /usr/local/VDC/UIS/VDC/Schema/DDI/Version2-1.dtd `cat list.icpsrxml.valid | while read d; do ls $d/$d.study.xml; done` 2>&1 | tee VALIDATE.study.out' );

system ( "cat VALIDATE.study.out | script_select_valid.pl list.vdcxml.valid" ); 
#MYLABEL:



# 4. Run the conversion scripts:
#
system ( "script_batch_presorted.pl" ); 



### system ( "grep '0\$' jbtbl.out | awk '{print \$5}' > list.successful" );

&select_Successful ( "mdquery.spss.sorted", "list.successful" ); 


### NEED another validation step here!

#MYLABEL2: 

system ( 'cat list.successful | while read d rest; do /bin/mv -f ${d}/${d}sec4.xml ${d}/${d}.data.xml; done' );



##MYLABEL2: 

# 7. Merge:

open ( LS, "list.vdcxml.valid" ) || die $@;

while ( <LS> )
{
    chop;
    system ( "script_merge_ns.pl $_ '1-3' $repository > $_/$_.merged.xml" );
    print STDERR '.';
}

close LS; 

# insert study-level UNFs:

system ( 'cat list.successful | script_studyUNF.sh' );


print STDERR "\n";

# 8a. Validate (and remove invalid)

system ( 'java -jar /usr/local/msv/msv.jar /usr/local/VDC/UIS/VDC/Schema/DDI/Version1-3.xsd `cat list.vdcxml.valid | while read d; do ls $d/$d.merged.xml; done` 2>&1 | tee VALIDATE.merged.out' );

system ( "cat VALIDATE.merged.out | script_select_valid.pl list.final.valid" ); 

# 8. Create metadata: 

system ( "cat list.final.valid | script_producemeta_ddi.sh 1902.2 > ddi.meta" ); 

# 10. Ingest the valid studies:

#system ( 'script_install_studies.sh >INSTALL.LOG 2>&1' ); 

##MYLABEL2: 

exit 0; 

# subroutines:

sub select_Successful {
    my $spss_file = shift @_; 
    my $result_file = shift @_; 

    return undef unless open IN_FILE, $spss_file; 

    my $line; 
    my @studies; 

    while ( $line = <IN_FILE> )
    {
	return undef unless $line =~/^([0-9]+)[ \t]/; 
	push @studies, $1;
    }

    close IN_FILE; 

    return undef unless open OUT_FILE, ">" . $result_file; 

    my $study; 
    my $status; 

    for $study ( @studies )
    {
	# first we check if the section 4 (data) DDI fragment has been generated:

	unless ( -f $study . "/" . $study . "sec4.xml" )
	{
	    next; 
	}

	$status = &parse_LogFile ( "$study" . "/" . $study . ".log" ); 

	if ( $status->{'ok'} )
	{
	    print OUT_FILE $study;
	    print OUT_FILE " " . $status->{'failed_parts'} 
	           if $status->{'failed_parts'}; 
	    print OUT_FILE "\n";	    
	}
    }

    close OUT_FILE; 
    
    1; 
}

sub parse_LogFile {
    my $log_file = shift @_; 

    my $status = {}; 

    $status->{'ok'} = undef; 
    $status->{'failed_parts'} = undef; 

    open LF, "tail $log_file|" || return $status; 

    my $l; 
    $status->{'ok'} = 1; 
    while ( $l = <LF> )
    {
	if ( $l =~/^validation result=[0-9]*/ ||
	     $l =~/^The requested processing failed/ )
	{
	    $status->{'ok'} = undef; 
	    last; 
	}

	if ( $l =~/^The requested processing is partially failed/ )
	{
	    my @failed_files; 

	    $l = <>; 
	    chop $l;

	    while ( $l=~/^[0-9]*th file\tsource file=(.*)/ )
	    {
		push @failed_files, $1;
		$l = <>; 
		chop $l;
	    }
	    $status->{'failed_parts'} = join ( " ", @failed_files ); 	    
	    last; 
	}		
    }

    close LF; 
    return $status; 
}
