package vdcLWP;

# LWP has a long-standing bug in loop detection. It will quit 
# when redirect to authenticate and back to the page. This is a workaround

use strict;
use vars qw($VERSION @ISA @EXPORT @EXPORT_OK);
require Exporter;
use LWP::UserAgent;
use HTTP::Request; 

@ISA = qw(LWP::UserAgent Exporter AutoLoader);

sub request {
	my $self = shift;
	my ($request, @args) = @_;

	$self->requests_redirectable( [] ); 

	my $cookie = $request->header ( 'Cookie' ); 

	my $response = $self->SUPER::request($request, @args);

	# transparent handling of redirects is disabled
	# so that we can create the redirection requests ourselves.
	# this way we can send along the original Cookie header with 
	# each of the redirect requests. 
	#
	# TODO: needs a counter in case of a redirection loop

        while ($response->code==302) { 
	    my $location = $response->header ( 'Location' ); 

	    $request = HTTP::Request->new( 'GET', $location ); 

	    $request->header ( 'cookie' => $cookie ); 
	    $response = $self->SUPER::request($request, @args);
        }
	return($response);
}

