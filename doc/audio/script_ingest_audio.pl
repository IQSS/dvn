#!/usr/bin/perl

use DBI;

my $filecategoryid = shift @ARGV; 

my $tx_host = "dvn-1.hmdc.harvard.edu";
my $username = "vdcApp";
my $password = "cg!sKnafel";
my $database = "vdcNet-prod";


my $dbh = DBI->connect("DBI:Pg:dbname=$database",$username,$password); 

while ( <> )
{
    chop; 
    ($filen, $fileloc, $mime, $description) = split (/\t/, $_, 4); 


    $description = $dbh->quote ( $description ); 
    $mime = $dbh->quote ( $mime ); 
    $fileloc = $dbh->quote ( $fileloc ); 
    $filen = $dbh->quote ( $filen ); 

    my $sql = qq {INSERT INTO studyfile (version, filename, restricted, subsettable, requestaccess, allowaccessrequest, displayorder, filesystemname, filesystemlocation, filetype, filecategory_id, description) VALUES (1, $filen, TRUE, FALSE, FALSE, FALSE, 0, $filen, $fileloc, $mime, $filecategoryid, $description)}; 

    my $sth = $dbh->prepare( $sql ); 

    print "executing " . $sql . "...\n"; 

    ###next; 

    unless ( $sth->execute() )
    {
	print STDERR "WARNING! Could not execute sql statement " . $sql . "!\n";
    }

    $sth->finish; 
}


$dbh->disconnect; 

exit 0; 

  
