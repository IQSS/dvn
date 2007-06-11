#  $Id: vdcLOG.pm, v x.xx 2000/09/.. xx:xx:xx L.A.
#
#  Copyright (c) HMDC-VDC
#
#  You may distribute this under the terms of either the GNU General Public
#  License or the Artistic License, as specified in the Perl README file.
#  (??)

#
#  This module implement VDC logging subsystem. 
#  The current implementation simply uses syslog. There are 4 levels of 
#  logging: info, debug, error, fatal. 

package vdcLOG; 

use strict;
use vars qw(@ISA @EXPORT @EXPORT_OK %EXPORT_TAGS $VERSION); 
use vars qw();

use Exporter; 

$VERSION = 0.01;
@ISA = qw(Exporter); 

@EXPORT    =   qw(new_vdcLOG);

@EXPORT_OK =   ();
%EXPORT_TAGS = ();

use Sys::Syslog qw(:DEFAULT setlogsock);


# METHODS:

sub new_vdcLOG {

    my $self = {}; 
    bless $self; 

    return $self; 
}


sub vdcLOG_debug {
    my $self = shift; 
    my $whoami = shift;
    my $verb = shift; 
    my $message = shift; 

    setlogsock('unix');
    openlog($whoami, 'cons,pid', 'user');
    syslog('debug', _clean($verb) . " " . _clean($message));
    closelog();

}

sub vdcLOG_info {
    my $self = shift; 
    my $whoami = shift;
    my $verb = shift; 
    my $message = shift; 

    setlogsock('unix');
    openlog($whoami, 'cons,pid', 'user');
    syslog('info', _clean($verb) . " " . _clean($message));

    closelog();

}

sub vdcLOG_warning {
    my $self = shift; 
    my $whoami = shift;
    my $verb = shift; 
    my $message = shift; 

    setlogsock('unix');
    openlog($whoami, 'cons,pid', 'user');
    syslog('warning', _clean($verb) . " " . _clean($message));
    closelog();
}


sub vdcLOG_fatal {
    my $self = shift; 
    my $whoami = shift;
    my $verb = shift; 
    my $message = shift; 

    setlogsock('unix');
    openlog($whoami, 'cons,pid', 'user');
    syslog('alert', _clean($verb) . " " . _clean($message));
    closelog();

}

sub _clean {
	my $msg= shift;	
	#$msg=~ s/\\/\\\\/g;
	#$msg=~ s/\//\\\//g;
	$msg=~ s/\%/%%/g;
	return ($msg);

}
1; 



