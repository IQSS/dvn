#!/usr/bin/perl -I/usr/local/vdc-admin/icpsrsync/bin

$mdquery = shift @ARGV; 
$listfile = shift @ARGV; 

$mdquery = "mdquery.spss.sorted" unless $mdquery; 
$listfile = "list.success" unless $listfile; 

require "icpsr_sync.ph"; 

&select_Successful ( $mdquery, $listfile );
