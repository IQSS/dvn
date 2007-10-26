#!/usr/bin/perl -w

# This script runs some DBI/Pg tests and initializes the VDC repository
# database and metadata table. It is based on the test script from the 
# DBD-Pg distribution. 

######################### We start with some black magic to print on failure.

BEGIN { $| = 1; }
END {print "test failed\n" unless $loaded;}
use DBI;
$loaded = 1;
use Config;
use strict;

my $destroy = 1 if $ARGV[0] eq "-destroy"; 
my $destroyonly = 1 if $ARGV[0] eq "-destroyonly";

######################### End of black magic.

my $os = $^O;
print "OS: $os\n";

my $dbmain = "template1";
my $dbvdc = "icpsrsyncdb";


my $dsn_main = "dbi:Pg:dbname=$dbmain";
my $dsn_test = "dbi:Pg:dbname=$dbvdc";

my ($dbh0, $dbh, $sth);

( $dbh0 = DBI->connect("$dsn_main" . ";host=localhost", "sqluser", "sqlpassword", { AutoCommit => 1 }) )
    and print "DBI->connect ............... ok\n"
    or  die   "DBI->connect ............... not ok: ", $DBI::errstr;

my $Name = $dbh0->{Name};
( "$dbmain" eq $Name )
    and print "\$dbh->{Name} ............... ok\n"
    or  print "\$dbh->{Name} ............... not ok: $Name\n";

( 1 == $dbh0->ping )
    and print "\$dbh->ping ................. ok\n"
    or  die   "\$dbh->ping ................. not ok: ", $DBI::errstr;

if  ( $destroy || $destroyonly )
{
    $dbh0->{PrintError} = 0; # do not complain when dropping $dbvdc
    $dbh0->do("DROP DATABASE $dbvdc");

    exit 0 if $destroyonly; 
}

( $dbh0->do("CREATE DATABASE $dbvdc") )
    and print "\$dbh->do ................... ok\n"
    or  die   "\$dbh->do ................... not ok: ", $DBI::errstr;

$dbh = DBI->connect("$dsn_test" . ";host=localhost", "leonid", "icpsrsync", { AutoCommit => 1 }) or die $DBI::errstr;

$dbh->do("Set DateStyle = 'ISO'");


######################### create tables:

# study table: 

$dbh->do("CREATE TABLE studytbl (
  stdynum	 int2		NULL,
  stdytitl	 char(511)	NULL,
  date		 int4		NULL,
  procdir	 char(31)	NULL,
  ndsets	 int2		NULL,
  nfiles	 int2		NULL,
  status	 int2		NULL,

  PRIMARY KEY (stdynum)

  )");


# dataset table: 

$dbh->do("CREATE TABLE dsettbl (
  stdynum	 int2		NULL,
  dsetnum	 int2		NULL,
  dsettitl	 char(255)	NULL, 
  nfiles         int2		NULL

  )");

# files table: 
$dbh->do("CREATE TABLE filetbl (

  istdynum	 int2		NULL,
  idsetnum	 int2		NULL,
  ifileid	 int4		NULL,
  ifilename	 char(31)	NULL,
  imaxlinesiz	 int4		NULL,
  icases	 int4		NULL,
  idate		 date		NULL,
  istatus	 boolean	NULL,

  vsyncdate	 int4		NULL,
  vprocdate	 int4		NULL,
  vtype		 char		NULL,
  vsize		 int4		NULL,
  vstatus	 int2		NULL,  

  PRIMARY KEY (ifileid)

  )");
  

######################### disconnect and drop test database

# disconnect

( $dbh->disconnect )
    and print "\$dbh->disconnect ........... ok\n"
    or  die   "\$dbh->disconnect ........... not ok: ", $DBI::errstr;

$dbh0->disconnect;

print "test sequence finished.\n";

######################### EOF
