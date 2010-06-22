
package DSB::RAP; 

#use vars qw();

use Exporter;

#use CGI;
#use CGI::Cookie;
use LWP;
use LWP::UserAgent;
use HTTP::Cookies;

use HTTP::Request;
use HTTP::Response;
#use HTTP::Request::Common; 
use URI::Escape;

# CONSTRUCTOR: 

sub new {
    my $skip = shift;

    my $query = shift;
    my $logger = shift; 
    my $domain = shift; 
    my $tmpdir = shift; 

    my $self = bless {};

    # we inherit some objects from the parent object (DSB): 

    $self->{query} = $query if $query; 
    $self->{logger} = $logger if $logger;

    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "", "initialized itself; reusing the supplied logger object" ); 

    # Now we want to create a UsearAgent that will be used to perform
    # Repository operations; we want to reuse the vdcCookie that the user
    # might already have:

    my $ua = new LWP::UserAgent;
        
    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(new)",
				   "created default UserAgent" );

    unless ( $ua )
    {
	$self->{logger}->vdcLOG_fatal ( "VDC::DSB::RAP", "(new)", 
					"could not create user agent." );
	return -1; 
    }

    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(new)", 
				   "successfully created UserAgent" ); 

    $self->{ua} = $ua; 
    $self->{'TMPDIR'} = $tmpdir; 

    return $self;
}

# METHODS: 

sub create_RepositoryMeta {
    my $self   = shift; 

    my $repuri = shift; 

    my $name   = shift; 
    my $class  = shift; 
    my $mime   = shift; 
    my $mime_u = shift; 
    my $filen  = shift; 
    my $study  = shift; 

#    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(create_RepositoryMeta)", 
#				   "attempting to create new object, name=" . 
#				   $name ); 

#    $repuri .= ( "?name=$name&class=$class&mimet=$mime&mimeu=$mime_u" ); 
    $repuri .= ( "?name=$name&mimet=$mime&mimeu=$mime_u&filen=$filen" ); 

    if ( $study )
    {
	$study = uri_escape($study);
	$repuri .= "&study=$study";
    }

#    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(create_RepositoryMeta)", 
#				   "request uri: " . $repuri ); 

    my $request = HTTP::Request->new('GET', $repuri ); 
    $request->header('X-VDC-Component' => "DSB");
    my $vdcCookie = $self->{query}->cookie('VDCSESSIONID');


    if ( $vdcCookie )
    {
#	$self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(create_RepositoryMeta)", 
#				       "vdc cookie detected: " . $vdcCookie ); 

#	$self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(create_RepositoryMeta)", 
#				       "filler log message;" ); 

	$request->header('cookie' => "VDCSESSIONID=" . uri_escape($vdcCookie));

#	$self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(create_RepositoryMeta)", 
#				       "created cookie header;" ); 

    }



#    my $response = $self->{ua}->request($request);

    my $ua = new LWP::UserAgent;

#    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(create_RepositoryMeta)", 
#				       "new user agent created;" ); 

    my $response = $ua->request($request);

#   $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(create_RepositoryMeta)", 
#				       "sent request: " . $response->status_line); 

    
    if ( $response->is_success )
    {
##	$self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(create_RepositoryMeta)", 
##				   "created rep. metadata (" . $response->status_line .  ")" ); 
	return 1;
    }

    $self->{logger}->vdcLOG_fatal ( "VDC::DSB::RAP", "(create_RepositoryMeta)", 
				    "could not create Repository metadata " . 
				    $name . "(" . $response->status_line . ")" ); 
    
    $self->{RAP_ERROR} = "could not create Repository metadata for " . 
	$name . "(" . $response->status_line . ")";

    return undef;
}

sub deposit_RepositoryObject {
    my $self = shift; 
    my $repuri  = shift; 
    my $name = shift; 
    my $file = shift; 
    
    my $domain; 

#    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(deposit_RepositoryObject)", 
#				   "uri=$repuri, name=$name, file=$file" ); 


#    if ( $repuri =~ /(\.\w+\.\w+)\/VDC\// )
#    {
#	$domain = $1;
#    }

# Create the user agent to make the HTTP call

#    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(deposit_RepositoryObject)", 
#				   "depositing " . (stat($file))[7] . " bytes" ); 

    use HTTP::Request::Common; 

    my $request = POST $repuri . "/" . $name, 
    'X-VDC-Component' => "DSB",
    Content_Type => 'form-data',
    Content      => [ name => $name, 
		      uploaded_file => [$file]
		      ];

    my $vdcCookie = $self->{query}->cookie('VDCSESSIONID');

    if ( $vdcCookie )
    {
##	$self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(deposit_RepositoryObject)", 
##				       "vdc cookie detected: " . $vdcCookie ); 

	$request->header('cookie' => "VDCSESSIONID=" . uri_escape($vdcCookie));
    }


    unless ( $request )
    {
	$self->{logger}->vdcLOG_fatal ( "VDC::DSB::RAP", "(deposit_RepositoryObject)", 
					"could not create http request." );
	return undef; 
    }

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(deposit_RepositoryObject)", 
##				   "successfully created http request" ); 


    

    #$ua->proxy('http', $PROXY_URL);

    # Make the call

#    my $response = $self->{ua}->request($request);

    my $ua = new LWP::UserAgent;
    my $response = $ua->request($request);


    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(deposit_RepositoryObjec)", 
				   "sent http request (" . $response->status_line . " )" ); 


    unless ( $response->is_success )
    {
	$self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(deposit_RepositoryObjec)", 
					$response->status_line ); 
    }
    else
    {
	return "ok" if $response->is_success; 
    }

    $self->{RAP_ERROR} = "could not deposit document into Repository";
    return undef;

}

sub mutate_RepositoryObject {
    my $self = shift; 
    my $repuri  = shift; 
    my $object  = shift; 

    my $name; 
    my $domain; 

#    my $tmpdir = $self->{'TMPDIR'}; 
    my $tmpdir = "/tmp";

    if ( $repuri =~ /(\.\w+\.\w+)\/VDC\// )
    {
	$domain = $1;
    }

    if ( $repuri =~ /^.*\/Access[\/\?](.*)$/ )
    {
	$name = $1;
	$name =~ s/name=//;
    }

    $repuri =~ s/Access.*$/Mutate/;
    $repuri .= "/" . $name;
#    $repuri =~ s/Access\?.*$/Mutate/;


    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(mutate_RepositoryObject)", 
				   "name=" . $name . " uri=$repuri, " . length ($object) . " bytes" ); 

    open ( TEMPUPLOAD, ">" . $tmpdir . "/_deposit_upload.$$" );
    print TEMPUPLOAD $object; 
    close TEMPUPLOAD; 


    use HTTP::Request::Common; 

    my $request = POST $repuri, 
   'X-VDC-Component' => "DSB",
    Content_Type => 'form-data',
    Content      => [ name => $name, 
		      uploaded_file => [ $tmpdir . "/_deposit_upload.$$" ]
		      ];

    unless ( $request )
    {
	$self->{logger}->vdcLOG_fatal ( "VDC::DSB::RAP", "(mutate_RepositoryObject)", 
					"could not create http request." );
	return undef; 
    }

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(mutate_RepositoryObject)", 
##				   "successfully created http request" ); 


    
    my $vdcCookie = $self->{query}->cookie('VDCSESSIONID');

    if ( $vdcCookie )
    {
	$request->header('cookie' => "VDCSESSIONID=" . uri_escape($vdcCookie));
    }

    #$ua->proxy('http', $PROXY_URL);

    # Make the call

#    my $response = $self->{ua}->request($request);


    my $ua = new LWP::UserAgent;
    my $response = $ua->request($request);


    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(mutate_RepositoryObjec)", 
				   "sent http request (" . $response->status_line . " )" ); 

    unlink ( $tmpdir . "/_deposit_upload.$$" );


    unless ( $response->is_success )
    {
	$self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(mutate_RepositoryObjec)", 
					$response->status_line ); 
    }
    else
    {
	return "ok" if $response->is_success; 
    }

    $self->{RAP_ERROR} = "could not mutate Repository object (" . $response->status_line . ")";
    return undef;

}

sub get_RepositoryObject {
    my $self = shift; 
    my $repuri  = shift; 

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "repuri=" . $repuri, "attempting to obtain the object from the Repository" ); 


    if ( $repuri =~ /(\.\w+\.\w+)\/VDC\// )
    {
	$domain = $1;
    }

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "domain=" . $domain, "(setting domain)" ); 


    #$repuri = uri_escape ( $repuri ); 

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "repuri=" . $repuri, "(escaped rep. URI)" ); 


    my $request = HTTP::Request->new('GET', $repuri ); 
    $request->header('X-VDC-Component' => "DSB");

    my $vdcCookie = $self->{query}->cookie('VDCSESSIONID');


    if ( $vdcCookie )
    {
##	$self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(get_RepositoryObject)", 
##				       "vdc cookie detected: " . $vdcCookie ); 

	$request->header('cookie' => "VDCSESSIONID=" . uri_escape($vdcCookie));
    }


    unless ( $request )
    {
	$self->{logger}->vdcLOG_fatal ( "VDC::DSB::RAP", "", 
					"could not create http request." );
	return -1; 
    }

##    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "", 
##				   "successfully created http request" ); 


    #$ua->proxy('http', $PROXY_URL);

    # Make the call

#    my $response = $self->{ua}->request( $request );
    my $ua = new LWP::UserAgent;
    my $response = $ua->request( $request );

    $self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(getobject)", 
				   "successfully sent http request (" . $response->status_line . ", length=" . length($response->content). " )" ); 


    unless ( $response->is_success )
    {
	$self->{logger}->vdcLOG_info ( "VDC::DSB::RAP", "(ua->request bombed)", 
					$response->error_as_HTML ); 
    }
    else
    {
	return $response->content if $response->is_success; 
    }

    $self->{DSB_RAP_ERROR} = "could not obtain document from the Repository";

    
    return undef;
}

1; 




