package VDCdiag;

# This is a module for diagnostic routines.
# Each component should provide a subclass package
# 
# If the subclass is a stub, all operations will be permitted.
# But if no subclass exists at all, all operations are forbidden.

use strict;
use vars qw($VERSION @ISA @EXPORT @EXPORT_OK);
require Exporter;


@ISA = qw(Exporter AutoLoader);
# Items to export into callers namespace by default. Note: do not export
# names by default without a very good reason. Use EXPORT_OK instead.
# Do not simply export all your public functions/methods/constants.
@EXPORT = qw(
	new diagnose begin end
);
$VERSION = '0.01';

use File::Temp qw/ tempfile tempdir /;
use URI::Escape;
use CGI qw(:standard);

# basic constructor
sub new {
	my $class = shift;
	my $self = {};
	our ($TMPDIR) = "/tmp"; # default
	do 'glv03';
	$self->{'TMPDIR'} = $TMPDIR;
	return bless $self, $class;
}

sub begin {
    print header ( -type=>'text/xml',  -Cache_control=>'max-age= 0' );
    print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    print '<Diagnose version="' . $VERSION . '" >' . "\n";
}

sub end{
    print "</Diagnose>\n";
}

sub diagnose {
	my ($self, $target, $method, @args) = @_;

    my  $fh = tempfile($self->{'TMPDIR'} . "/diagnoseXXXXXX" );
    my  ($status, $success);

    select $fh;
    eval('$status = $target->' . $method . '(@args);' );
    $status = $@ | $status;
    if ($status) {
	$success="FALSE";
    } else { 
	$success="TRUE";
    }

	
    select STDOUT;
    print '<test name=' . '"' . uri_escape($method) . '" success="' . $success .'" message="' . uri_escape($status) 
		. '" >' . "\n";
    print "<![CDATA[" ;
    seek($fh, 0, 0);
    print <$fh>;  
    truncate($fh,0);
    print "]]>";
    print "</test>\n";
    return $status ;
}

# Preloaded methods go here.

# Autoload methods go after =cut, and are processed by the autosplit program.

1;
__END__
