#!/usr/bin/perl 

use DBI;
#use Getopt::Long;

my $dbh = DBI->connect ( "dbi:Pg:dbname=icpsrsyncdb;host=localhost", "leonid", "icpsrsync",  { AutoCommit => 1 }) || die $@; 

my $sql_cmd; 
my $sth; 

while ( <> )
{
    chop; 

    ($study, $dset, $title, $nfiles) = split ( "\t", $_ ); 

    $title =~s/\'/\"/g; 

    $sql_cmd = "INSERT INTO dsettbl VALUES (" . 
	$study . ", " . 
	$dset . ", '" . 
	$title . "', " . 
	$nfiles . ")"; 

#    print STDERR $sql_cmd . "\n";

    $sth = $dbh->prepare ( $sql_cmd ) || die $@; 
    $sth->execute || die $@; 

    $sth->finish; 
}
 
$dbh->disconnect;        

