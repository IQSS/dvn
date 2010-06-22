use CGI; 
package vdcCGI;
@vdcCGI::ISA = qw(CGI);

use vars qw($ERROR_PAGE $MY_ADDR);


my ($remote_redirect);
do "/usr/local/VDC/etc/config_vals.pl";
if ($specs{'UIS_SERVER'}) {
	$remote_redirect = 1;	
	$ERROR_PAGE = $specs{'UIS_SERVER'} .  '/VDC/Error/ErrorPage.jsp'; 
}


sub new
{
    my $self = CGI::new(@_);
    return $self;
}

sub header {
    my($self,@p) = CGI::self_or_default(@_);

    my $header; 

    return undef if $self->{'.header_printed'}++ and $CGI::HEADERS_ONCE;
    my ($status,$nph,$type) = extract_status_plus (@p); 
    my ($code, $msg) = split ( /[ \t]+/, $status, 2 ); 


    if ( $code == 200 )
    {
	# get the standard CGI header; 
	$header = CGI::header (@p); 
    }
    else
    {
	# redirect to the appropriate error page;
	$header = $self->produce_redirect (
					   $code,
					   $ERROR_PAGE,
					   $msg,
					   $nph
					  ); 
    }

    return $header; 
}

## UTILS:

sub produce_redirect {
    my $self   = $_[0]; 
    my $code   = $_[1]; 
    my $dest   = $_[2]; 
    my $msg    = $_[3]; 
    my $nph= $_[4]; 

    my $iscomp     = $self->http('HTTP_X_VDC_COMPONENT');

    my $ua     = $self->user_agent; 

	
    if ($remote_redirect) {
       if (defined $self->https()) {
	$dest = 'https://'. $dest;
       } else {
	$dest = 'http://'. $dest;
       }
    }
    $dest .= "?status=" . $code;
    $dest .= "&show_default=true"  ;
    if ($msg =~ /(.*):(.*)/) {
        $dest .= "&message=" . _encode($2);
    }  
    


    my $red; 

    if (
	(!$iscomp) 
        && (($ua=~/mozilla/i && $ua=~!/compatible/i) || ($ua=~/MSIE/i)) # remove this when components
									# are all refactored to send header
       ) 
    {

	$red = $self->redirect ( -uri=>$dest, 
				    -nph=>$nph,
				    -type=>'text/html' ); 
    }
    else
    {
	$red = CGI::header ( -status=>"$code $msg", 
				    -nph=>$nph,
			     -refresh=>"0; URL=$dest" );
    } 
    
#    $red .= "<HTML> 
#
#       <HEAD> 
#       <META HTTP-EQUIV=\"refresh\" content=\"0;URL=$dest\"> 
#       <TITLE>Please wait...</TITLE> 
#       </HEAD> 
#       <BODY> 
#       <P>The VDC is redirecting your response to another page, but because you are using an unsupported
#browser, this will take a few seconds. <P>
#       <P>Or click <A HREF=\"$dest\">to go to the next page</A> right away.</P>
#       <HR>
#
#       Status:  $code Intercepted;\n<PRE>\nDeveloper debug information:\n";
#
#
#    my ($e,$value);
#
#    for $e (keys(%ENV)) 
#    {
#	$value=$ENV{"$e"};
#	$red .= "$e: $value\n";
#    }
#
#    $red .= "\n -- VALS -- \n";
#
#    my (@val,@nm,@v,$n);
#
#    @nm=$self->param;
#
#    for $n (@nm) 
#    {
#        @v=$self->param("$n");
#        $red .= "$n: @v\n";
#    }
#
#    $red .= "</PRE></BODY></HTML>\n";
#
#    return $red;
}

sub extract_status_plus {
    my ($status,$nph,$type); 
    my $i; 

    for ( $i = 0; $i < $#_; $i+=2 )
    {
	$status = $_[$i+1] if $_[$i] eq '-status'; 
	$nph = $_[$i+1] if $_[$i] eq '-nph'; 
	$type = $_[$i+1] if $_[$i] eq '-type'; 
    }

    $status = '200 OK' unless $status; 
    $type   = 'text/html' unless $type;
    $nph = '0' unless $nph; 

    return ($status,$nph); 
    
}

sub _encode {
        my ($in)= @_;
        $in=~s/([\W])/"%" . uc(sprintf("%2.2x",ord($1)))/eg;
        return($in);
}

1; 



















