#!/usr/bin/perl

use DBI;

my $studyid = shift @ARGV; 
my $name = shift @ARGV; 


my $tx_host = "dvn-1.hmdc.harvard.edu";
my $username = "vdcApp";
my $password = "cg!sKnafel";
my $database = "vdcNet-prod";


my $dbh = DBI->connect("DBI:Pg:dbname=$database",$username,$password); 

$name = $dbh->quote ( $name ); 

my $sql = qq {INSERT INTO filecategory (name, version, displayorder, study_id) VALUES ($name, 1, 0, $studyid)}; 

my $sth = $dbh->prepare( $sql ); 

print "executing " . $sql . "...\n"; 


unless ( $sth->execute() )
{
    print STDERR "WARNING! Could not execute sql statement " . $sql . "!\n";
}

$sth->finish; 

$dbh->disconnect; 

exit 0; 

  
