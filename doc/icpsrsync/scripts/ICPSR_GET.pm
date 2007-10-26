package ICPSR_GET;

use vars qw(@EXPORT @EXPORT_OK %EXPORT_TAGS $VERSION);
use vars qw(@ISA $ICPSR_host $ICPSR_user $ICPSR_pw);


@ISA =     qw(Exporter); 

use strict;

use Exporter; 

use HTTP::Request;
use HTTP::Response;
use URI::URL;      
use Net::HTTP;
use Net::HTTPS;
use URI::Escape;

$ICPSR_host = "www.icpsr.umich.edu";
$ICPSR_user = "<ICPSR SUBSCRIPTION UID>";
$ICPSR_pw   = "<ICPSR SUBSCRIPTION PASSWORD>";


$VERSION = 0.01;


@EXPORT =  qw(new_IO ICPSR_GET); 

sub new_IO {

    my $self = {};

    bless $self;


    $self->{'cookie'} = $self->obtain_ICPSR_cookie;

    unless ( $self->{'cookie'} )
    {
	$self->{ICPSR_GET_error} = "ICPSR_GET: could not initialize the module and/or obtain the authorization cookie";
	return $self; 
    }
    
    return $self;
}

sub obtain_ICPSR_cookie {
    my $self = shift; 

# HACK! HACK! HACK!

    my $host = $ICPSR_host;

    my $loginurl = "https://" . $ICPSR_host . ":443/ticketlogin";

    my $subscriber = uri_escape ( $ICPSR_user );
    my $password = uri_escape ( $ICPSR_pw );
    my $path = 'ICPSR';
    my $request_uri = uri_escape ( 'http://' . $ICPSR_host . '/cgi-bin/bob/archive?study=6635' );

    my $content = "email=" . $subscriber . "&password=" . $password . "&path=" . $path . "&request_uri=" . $request_uri;

    my $s = Net::HTTPS->new(Host => $host);

    my %headers;

    $headers{'Content-Type'} = 'application/x-www-form-urlencoded';
    $headers{'User-Agent'} = "Mozilla/5.0";

    $s->write_request( POST => $loginurl, %headers, $content );

    my ($code, $mess, %h) = $s->read_response_headers;

    return undef unless $code == 200; 

    my $cookie;

    if ( $cookie = $h{'Set-Cookie'} )
    {
        $cookie =~/(Ticket=.[^;]*)/;
        $cookie = $1;
    }
    # we are returning undef in the case where we haven't gotten back
    # a Set-Cookie header containing the Ticket cookie;

    return $cookie;
}


sub ICPSR_GET {
    my ( $self, $url, $file ) = @_; 

    my $s = Net::HTTP->new(Host => $ICPSR_host);

    unless ( $s )
    {
	$self->{ICPSR_GET_error} = "ICPSR_GET: could not initialize the Net::HTTP object";
	return undef; 
    }


    my %headers_in;

    $headers_in{'Cookie'} = $self->{'cookie'}; 

    $s->write_request(
                      GET => $url, 
                      %headers_in,
                      ); 

    my($code, $mess, %headers_out) = $s->read_response_headers;

    return undef unless $code == 200; 

    open ( F_OUT, ">" . $file ) || return undef; 

    my $bytes_read;

    while ( $s->read_entity_body($bytes_read, 4096) )
    {
	print F_OUT $bytes_read;
    }

    close F_OUT;

    return  $file;
}

1;    








