#!/usr/bin/perl

use LWP::UserAgent;
use CGI qw/:standard/;

my $debugmode = 0; 

my $dvnserver     = "http://dvn.iq.harvard.edu"; 
my $collformurl   = $dvnserver . "/dvn/dv/datapass/faces/SearchPage.jsp?mode=1&collectionId=412"; 
my $searchpageurl = $dvnserver . "/dvn/faces/SearchPage.jsp"; 
my $form_saved    = "/tmp/collform_saved.$$.tmp"; 
my $file_final    = "/tmp/output_final.$$.html";

my $q = new CGI; 

my $keyword = $q->param ( "keyword" ); 

# the old VDC search form, /VDC/View/index.jsp, that the 
# datapass search form is still pointing to, is now aliased
# to this script. so if the "keyword" parameter is not defined, 
# we check for the "op_query" parameter that the VDC 
# search was using:

$keyword = $q->param ( "op_query" ) unless $keyword; 

my $ua = LWP::UserAgent->new;
###$ua->requests_redirectable( [] );

$ua->agent ("Mozilla/5.0"); 

# So, this is what we are doing: 
# First, we acess the SearchPage with the "mode=1&collectionId=412" 
# parameter that lists everything in the DataPass collection; then
# we use the "search within these results" form on that page to do 
# the actual search. 

# so, first access the collection searcg page: 

my $request = HTTP::Request->new('GET', $collformurl );

# and save it into a file: 

my $response = $ua->request($request,$form_saved);

my $code = $response->code; 
my $status = $response->status_line;

# grab the cookie the App has given us for the session:

my $cookieheader = $response->headers->header('Set-Cookie'); 

print STDERR "code: $code.\n" if $debugmode; 
print STDERR "status: $status" . "\n" if $debugmode; 
#print STDERR "headers: $headers" . "\n" if $debugmode; 
print STDERR "cookie header: $cookieheader" . "\n" if $debugmode; 

unless ( open (F, $form_saved) ) 
{
    $error = "Could not perform basic Datapass study listing search.";
}
else
{
    # Before we do anything else, let's check if we actually 
    # have a non-empty search parameter; if we don't, it means
    # they wanted to see everything in the collection, so we 
    # can just give them the "mode=1" search page that we have 
    # saved:
    
    unless ( $keyword ) 
    {
	# have to give them the session cookie, otherwise
	# they might have trouble following the study link on
	# the page and such: 

	print $q->header ( -cookie=>$cookieheader ); 
	print $q->start_html ( "DVN - DataPass Search" ); 

	while ( read ( F, $buf, 8192 ) )
	{
	    print $buf; 
	}

	close F; 

	exit 0; 

    }

    # now, if we have a search parameter:
    # let's read the page we have saved, extract the form parameters, 
    # then fire the "within these results" search, like we are a 
    # normal user: 

    while ( <F> )
    {
	if ( /<form [^>]*jsessionid=([0-9a-f]*)\.(dvnInstance[0-9]*)\"/ )
	{
	    $jsessionid = $1; 
	    $dvninstance = $2; 

	    # (actually, we already have these, in the cookie we got)
	}

	if ( /<input [^>]*vdcId\" value=\"([^\"]*)\"/ )
	{
	    $vdcid = $1; 
	}

	if ( /<input [^>]*studyListingIndex\" value=\"([^\"]*)\"/ )
	{
	    $studylistingindex = $1; 
	}

	if ( /<input [^>]*type=\"checkbox\"[^>]*name=\"([^\"]*)\"/ )
	{
	    $withincheckbox = $1;

	    # not sure about this checkbox thing -- it actually looks like 
	    # it is static, i.e., always named "j_id_id31pc4" -- but
	    # I'm checking for it just in case;
	}
	if ( /<input [^>]*ViewState\" value=\"([^\"]*)\"/ )
	{
	    $viewstate = $1; 
	    # this is the last parameter we need to parse, so 
	    # we can stop:
	    last; 
	  
	}

    }

    close F; 
    
    my $error = ""; 

    # Assuming we have found a valida jsessionid and all, we can 
    # now make a final search call: 

    if ( $jsessionid )
    {
	print STDERR "Now we can try and make a call to the search form;\n" if $debugmode;
	print STDERR "jsessionid: $jsessionid.\n" if $debugmode;
	print STDERR "dvninstance: $dvninstance.\n" if $debugmode;
	print STDERR "studylistingindex: $studylistingindex.\n" if $debugmode;
	print STDERR "withincheckbox: $withincheckbox.\n" if $debugmode;
	print STDERR "viewstate: $viewstate.\n" if $debugmode;
	print STDERR "vdcid: $vdcid.\n" if $debugmode;

	$enc_type = "application/x-www-form-urlencoded"; 
	
	my $content = 'content:searchPageView:form1:vdcId=' . $vdcid; 
	

	$content .= '&pageName=SearchPage';
	
	$content .= '&content:searchPageView:form1:studyListingIndex=' . $studylistingindex; 
	$content .= '&content:searchPageView:form1:dropdown1=' . 'any'; 
	$content .= '&content:searchPageView:form1:textField2=' . $keyword;
	$content .= '&' . $withincheckbox . '=on'; 
	$content .= '&content:searchPageView:form1:search=' . 'Go'; 
	$content .= '&content:searchPageView:form1_hidden=content:searchPageView:form1_hidden';
	$content .= '&javax.faces.ViewState=' . $viewstate; 


	my $fullsearchpageurl = $searchpageurl . ';jsessionid=' . $jsessionid . '.' . $dvninstance; 

	print STDERR "full search page url:\n" if $debugmode; 
	print STDERR $fullsearchpageurl . "\n\n" if $debugmode; 
	print STDERR "form content:\n" if $debugmode;
	print STDERR $content . "\n\n" if $debugmode; 

	$request = HTTP::Request->new(POST => $fullsearchpageurl);

	# remember to supply the session cookie with the request:

	$request->header('Cookie' => 'JSESSIONID=' . $jsessionid . '.' . $dvninstance); 
	$request->content_type('application/x-www-form-urlencoded');
	$request->content( $content );

	$response = $ua->request($request,$file_final);
    
	# now, if everything is ok, we have the final search saved
	# in the file above;

	$status = $response->status_line;

	print STDERR "status: $status" . "\n" if $debugmode; 
	print STDERR "code: $code" . "\n" if $debugmode; 

	# so we can just dump it on their screen (again, with the 
	# session cookie in the header)

	if ( $response->code==200 && ( open F, $file_final ) )
	{
	    print $q->header ( -cookie=>'JSESSIONID=' . $jsessionid . '.' . $dvninstance . '; Path=/dvn' ); 
	    print $q->start_html ( "DVN - DataPass Search" ); 

	    while ( read ( F, $buf, 8192 ) )
	    {
		print $buf; 
	    }

	    close F; 

	    exit 0; 
	}
	else 
	{
	    $error = "failed to get search results from DVN search page";
	}
    }
    else
    {
	$error = "Failed to obtain a valid session authorization.";
	print STDERR "NO jsessionid.\n" if $debugmode;
    }
}

# if we've made it this far, it means something went wrong;
# print an error message: 

print $q->header; 
print $q->start_html ( "DVN - DataPass Search" ); 
print $q->h1("ERROR: " . $error); 

exit 0; 

