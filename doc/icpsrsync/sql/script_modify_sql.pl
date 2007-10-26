#!/usr/bin/perl 

use DBI;
use Getopt::Long;

my $dbh = DBI->connect ( "dbi:Pg:dbname=icpsrsyncdb;host=localhost", "leonid", "icpsrsync",  { AutoCommit => 1 }) || die $@; 

my $sql_cmd; 
my $sth; 

while ( <> )
{
    chop; 

    ($study, $title, $time, $dir, $ndsets, $nfiles, $status) = split ( "\t", $_ ); 

    $title =~s/\'/\"/g; 

    $sql_cmd = "INSERT INTO studytbl VALUES (" . 
	$study . ", '" . 
	$title . "', " . 
	$time . ", '" . 
	$dir . "', " . 
	$ndsets . "', " . 
	$nfiles . "', " . 
	$status . ")"; 

    print STDERR $sql_cmd . "\n";

    $sth = $dbh->prepare ( $sql_cmd ) || die $@; 
    $sth->execute || die $@; 

    $sth->finish; 
}
 
$dbh->disconnect;        

