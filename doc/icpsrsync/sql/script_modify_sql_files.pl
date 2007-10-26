#!/usr/bin/perl 

use DBI;
#use Getopt::Long;

my $dbh = DBI->connect ( "dbi:Pg:dbname=icpsrsyncdb;host=localhost", "leonid", "icpsrsync",  { AutoCommit => 1 }) || die $@; 

my $sql_cmd; 
my $sth; 

while ( <> )
{
    chop; 

    @_ = split ( "\t", $_ ); 

    unless ( $#_ == 12 )
    {
	print STDERR "WARNING! only $#@ fields in line: \n";
	print STDERR $_ . "\n";
    }

    ($istdynum, $idsetnum, $ifileid, $ifilename, $imaxlinesiz, $icases, $idate, $istatus, $vsyncdate, $vprocdate, $vtype, $vsize, $vstatus) = @_; 

    $title =~s/\'/\"/g; 

    $sql_cmd = "INSERT INTO filetbl VALUES (" . 
	$istdynum. ", " . 
	$idsetnum . ", " . 
	$ifileid . ", '" . 
	$ifilename . "', " . 
	$imaxlinesiz . ", " . 
	$icases . ", '" . 
	$idate . "', '" . 
	$istatus . "', " . 

	$vsyncdate . ", " . 
	$vprocdate . ", '" . 
	$vtype . "', " . 
	$vsize . ", " . 
	$vstatus . ")"; 

#    print STDERR $sql_cmd . "\n";

    unless ( $sth = $dbh->prepare ( $sql_cmd ) ) 
    {
	print STDERR "could not prepare " . $sql_cmd . "\n";
	print STDERR "dying...\n";
	die $@; 
    }
    unless ( $sth->execute )
    {
	print STDERR "could not execute " . $sql_cmd . "\n";
	print STDERR "dying...\n"; 
	die $@; 
    }

    $sth->finish; 
}
 
$dbh->disconnect;        

