#!/usr/bin/perl
use strict;
use warnings;
no warnings qw(portable);
use lib qw(/home/httpd/perl/lib);
use lib qw(/usr/local/VDC/perl);
use lib qw(/usr/local/VDC/perl/lib);
use CGI qw(-compile :all);
##use vdcDIR;
#use CGI::Carp ();



use vars qw($MP2 $MP199); 

BEGIN {

    eval "use mod_perl";

    if ( $@ ) {
	eval "use mod_perl2";
	$@ && die "*** Could not load mod_perl OR mod_perl2.\n";
    }

    $MP2 = $mod_perl::VERSION >= 2.0;
    $MP199 = ( $mod_perl::VERSION >= 1.99 && $mod_perl::VERSION < 2.0 );

    if ( $MP2 ) {
	require ModPerl::Registry;
	require Apache2::Const;
	Apache2::Const->import(-compile);
	eval "use Apache2::compat;" ;
        die $@ if $@;
    }
    elsif ( $MP199 ) {
	require ModPerl::Registry;
	require Apache::Const;
	Apache::Const->import(-compile);
	eval "use Apache2; use Apache::compat;" ;
        die $@ if $@;
    }
    else {
	require Apache::Registry;
	require Apache::Constants;
	Apache::Constants->import(qw(OK DECLINED));
    }
}

1;


