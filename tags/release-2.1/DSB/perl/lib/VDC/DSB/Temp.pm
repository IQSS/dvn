
package DSB::Temp; 

# This library is for monitoring the DSB temp space.

# CONSTRUCTOR: 

sub new {
    my $skip = shift;
    my $tmpdir = shift; 

    my $self = bless {};
    $self->{'TMPDIR'} = $tmpdir; 

    return $self;
}

# METHODS: 

sub check_TempDirectory {
    my $self = shift; 

    my $tmpdir = $self->{'TMPDIR'}; 

    for my $dir ( $tmpdir, $tmpdir . "/DSB", $tmpdir . "/webtemp" )
    {
	unless ( -d $dir )
	{
	    return 0 unless mkdir $dir; 
	}

	chmod 0775, $dir; 
    }

    1; 
}

1; 

