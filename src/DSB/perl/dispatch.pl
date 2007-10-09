#!/usr/bin/perl -I/usr/local/VDC//perl/

use lib qw(/usr/local/VDC/perl);
use lib qw(/usr/local/VDC/perl/lib);
use lib qw(/usr/local/VDC/perl/lib/VDC);

package dispatcher; 

use strict; 
use POSIX; 


#
# We initialize once, when the Apache thread is started: 
#

my $hq; 
my $netloc;

if ( !defined ($dispatcher::init) ) 
{

    use CGI qw/:standard/;

    # Unbuffer stdout:
    select(STDOUT); $| = 1;
 
    # Initialize the logger: 
    use vdcLOG; 
    $dispatcher::catalog{logger} = new_vdcLOG(); 

    $dispatcher::catalog{logger}->vdcLOG_info("VDC::dispatcher", '<INIT>',
					      "Initializing Main Dispatcher.");
    $dispatcher::init = 1;
}

# Now we can initialize the service-specific code. This is also done 
# once for every thread -- the first time a service call to this 
# service comes in. 

my $SERVICE = ( $ENV{'SERVICE'} || "Repository" );

$dispatcher::query = new CGI;

$netloc = $dispatcher::query->url( -base=>1 );
    
$netloc =~ s:^http\://::gi;
$netloc =~ s/[:\/].*$//;


if ( !defined ($dispatcher::catalog{$SERVICE . ":" . $netloc}) ) 
{
    $dispatcher::catalog{logger}->vdcLOG_info 
	( "VDC::$SERVICE", '<INIT>',
	  "Initializing the service..." ); 

#    my $module = &chk_config_alias ($SERVICE); 
    my $module = $SERVICE; 
    
    my $success =  &my_require ($module);

    if ( $success == -1 ) 
    {
	$dispatcher::catalog{logger}->vdcLOG_warning 
	    ( "VDC::$SERVICE", 
	      '<INIT>',
	      "Service ($module) not available." ); 
	
	print $dispatcher::query->header ( 
			    -status=>'404 Not Found:Service unknown', 
			    -type=>'text/plain' 
			  ); 
    } 
    elsif ( !$success ) 
    {
        $dispatcher::catalog{logger}->vdcLOG_fatal 
	    ( 
	      "VDC::$SERVICE", 
	      '<INIT>',
	      "Could not load service module." ); 
	
	print $dispatcher::query->header 
	    ( -status=>"500 Internal Server Error: $SERVICE -Could not load service module", 
	      -type=>'text/plain' );
    }

    exit 0 unless $success > 0; 

    $dispatcher::catalog{logger}->vdcLOG_info 
	( "VDC::$SERVICE", 
	  '<INIT>',
	  "Successfully loaded service module." ); 


    $dispatcher::catalog{$SERVICE . ":" . $netloc} = eval $module . "->new";

    # Initialize the Service:

    $dispatcher::catalog{$SERVICE . ":" . $netloc}->{logger} = $dispatcher::catalog{logger};
    $dispatcher::catalog{$SERVICE . ":" . $netloc}->init( $netloc, $SERVICE ); 


    $dispatcher::catalog{logger}->vdcLOG_info ( "VDC::$SERVICE", '<INIT>', 
				  "Resuming normal operations." ); 
}
else
{
    $dispatcher::catalog{$SERVICE . ":" . $netloc}->{logger}->vdcLOG_info 
	( "VDC::$SERVICE", '<INIT>', 
	  "Reusing previously dispatched service module." ); 
}

# Dispatch the request: 

&Dispatch();

# Well, that's it. Some helper functions are below. 

# Dispatch -- the main workhorse:

sub Dispatch {
    my ($service, $verb, $version, $query_string);

    my (%params, %context); 

    $service = ( $ENV{'SERVICE'} || "Repository" )  . ":" . $netloc; 
    $verb    = ( $ENV{'VERB'} || "Deposit" ); 
    $version = ( $ENV{'VERSION' } || "0.1" );

    $dispatcher::catalog{$service}->{logger}->vdcLOG_info ( "VDC::$service", 
							    "<Dispatch>",
							    "dispatching" );

    $dispatcher::catalog{$service}->{logger}->vdcLOG_info ( "VDC::$service", 
							    "<Dispatch>",
							    "created query" );


    $dispatcher::catalog{$service}->{query} = $dispatcher::query;


# CGI parameters: 
# we are packing them in a hash and pass it to the appropriate verb
# method by reference:

    my $p;
 
    foreach ( $dispatcher::query->url_param() )
    {   
        $p = $dispatcher::query->url_param($_);
        $params{$_} = $p if $p || $p == 0;
    }

    foreach ( $dispatcher::query->param() )
    {
	$p = $dispatcher::query->param($_);
        $params{$_} = $p if $p || $p == 0;
    }

    unless ( $params{'name'} )
    {
	$params{'name'} = $ENV{'NAME'};
    }

        $dispatcher::catalog{$service}->{logger}->vdcLOG_info ( "Dispatcher", "params", join(":",%params));


# In addition to the parameters we want to pack some environment data
# (user agent, request method, etc.), or "context" and pass it to the
# dispatched method too.

# note, we want to use the SCRIPT_URL env var instead of SCRIPT_NAME
# to get the _original_ URL, before it was resolved to point to this
# script using mod_rewrite rules:

    my($script_name) = $ENV{'SCRIPT_URL'};

# everything else is straightforward:

    my($request_method) = $dispatcher::query->request_method();
    my($path_info)      = $dispatcher::query->path_info();
    my($accept)         = $dispatcher::query->Accept();
    my($remote_host)    = $dispatcher::query->remote_host();
    my($user_agent)     = $dispatcher::query->user_agent();

    if ( $dispatcher::query->http('X-VDC-PROXY-DIRECT') )
    {
	$dispatcher::catalog{$service}->{logger}->vdcLOG_info ( "Dispatcher", "VDC::$service", "X-VDC-PROXY-DIRECT header detected." ); 
    }
    else
    {
	$dispatcher::catalog{$service}->{logger}->vdcLOG_info ( "Dispatcher", "VDC::$service", "X-VDC-PROXY-DIRECT header NOT detected." ); 
    }

    my $cookie_value = $dispatcher::query->cookie('VDCSESSIONID');
    $dispatcher::catalog{$service}->{logger}->vdcLOG_info ( "Dispatcher", "VDC::$service", "cookie checked: " . $cookie_value ); 


    %context = (
		'script_name'    => $ENV{'SCRIPT_NAME'},
		'request_method' => $request_method,
		'full_url'       => $dispatcher::query->url,   
		'netloc'         => $dispatcher::query->url(-base=>1),   
		'url'            => $script_name,
		'accept'         => $accept,
		'remote_host'    => $remote_host,
		'remote_addr'    => $user_agent,
		'agent'          => $user_agent, 
		'version'        => $version
		);

    if ($query_string = $dispatcher::query->query_string()) 
    {
	$context{'url'} .= '?' . $query_string;
    } 

    my ($ret, $msg) = &Execute_Verb ($service, $verb, \%params, \%context);

    if ($ret) 
    {
        my($hq)= new CGI;
	print $hq->header( -status=>"$ret $msg", -type=>'text/plain' );
	#print "The server was unable to process your request.\n",
#	      "because of the following error:\n",
	#      $msg, "\n"; 

	return;
    }

}

sub Execute_Verb {
    my ($service, $verb, $params, $context) = @_; 

    my $ref; 

    if ( $ref = $dispatcher::catalog{$service}->can($verb) ) 
    {
	return $ref->($dispatcher::catalog{$service}, $params, $context);
    }
    else
    {
	return (400, "Unsupported or malformed request: $service/$verb.");
    }

    return; 
}


# a customized version of `require' method: 
# we use it to load Service modules (Index, Repository, etc.). 
# Contrary to the normal "require" or "use" it doesn't die when it 
# can't load the object, but returns an error code. This way I can 
# simply send back a message saying that the requested service is not
# available on this server. It also checks for the non-zero value to 
# be returned by the code, just like require or use. 

sub my_require {
    my($filename) = @_;

    return 1 if $INC{$filename};

    my($prefix,$realfilename,$result);
  ITER: {
      foreach $prefix (@INC) {
	  $realfilename = "$prefix/$filename.pm";

	  if (-f $realfilename) {
	      $result = do $realfilename;
	      last ITER;
	  }
      }
      $result = -1; 
  }

    $INC{$filename} = $realfilename if $result > 0;
    return $result;
}                                                              





