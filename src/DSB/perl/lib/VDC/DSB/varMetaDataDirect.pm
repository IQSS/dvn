package VDC::DSB::varMetaDataDirect; 
# 
# Copyright (C) 2007 President and Fellows of Harvard University
#	  (Written by Leonid Andreev)
#	  (<URL:http://thedata.org/>)
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
# USA.
# 
# Redistributions of source code or binaries must retain the above copyright
# notice.
#

use DBI;

sub new {
	my $class = shift;

	my $self = { @_ };

	$self->{_varNameA}=[];
	$self->{_varNameAsafe}=[];
	$self->{_varRecSegN}={};
	$self->{_varNameH}={};
	$self->{_varNameHsafe}={};
	$self->{_varType}=[];
	$self->{_varLabel}=[];
	$self->{_varNo}=[];
	$self->{_varNoMpTbl}={};
	$self->{_varNoRcut}={}; 
	$self->{_valLblTbl}={};
	$self->{_mssvlTbl}={};
	$self->{_charVarTbl}={};
	$self->{_varFormat}={};
	$self->{_formatName}={};
	$self->{_formatCatgry}={};
	$self->{unsafeVarName}=0;
	$self->{logicalRecords}=0;

	# and 2 more hashes for the fixed-style
	# starts and ends:

	$self->{_varStartPos}={};
	$self->{_varEndPos}={};


	$self->{censusURL} = "";

	$self->{wholeFile} = 1; 

	$novars = scalar(keys(%{$self->{VarID}}));
	
	bless $self, ref($class)||$class;
	return $self;
}


sub getFormatToken{
	my $rawToken =shift;
	my $DEBUG;
	my @tmp  = split(//,$rawToken);

	my $tmps="";
	foreach my $e (@tmp){
		if (ord($e) < 65) {
			last;
		} else {
			$tmps .= $e;
		}
	}
	print "raw Token=",$rawToken,"\tformat=",$tmps,"\n" if $DEBUG;
	return $tmps;
}

sub obtainMeta {
	my $self = shift @_;

	my $varID = $self->{VarID};
	my $fileID = $self->{FileID};

	
	my $sqlHost = $self->{sqlHost}; 
	my $sqlPort = $self->{sqlPort}; 
	my $sqlUser = $self->{sqlUser};
	my $sqlDB   = $self->{sqlDB};
	my $sqlPw   = $self->{sqlPw};
	
	my $dbh; 

	my $database = "DBI:Pg:dbname=$sqlDB"; 

	if ( $sqlHost ne "" )
	{
	    $database .= ";host=$sqlHost"; 
	    if ( $sqlPort )
	    {
		$database .= ";port=$sqlPort" if $sqlPort != 5432; 
	    }
	}

	$dbh = DBI->connect( $database, $sqlUser, $sqlPw);

	return undef unless $dbh; 

	my %disc_Var = (); 
	my $min_disc_varid = 2**31; 
	my $max_disc_varid = 0; 
	my $count_disc_var = 0; 
	
	# 1st lookup: find out the datatable id by the studyfile id 
	# supplied: 

	my $sth = $dbh->prepare(qq {SELECT id,recordspercase FROM datatable WHERE studyfile_id=$fileID});
	$sth->execute();

	my ($datatable_id, $logical_records) = $sth->fetchrow(); 

	$self->{logicalRecords} = $logical_records; 

	$sth->finish; 

	# 2nd lookup: we can now look up the variables: 

	$sth = $dbh->prepare(qq {SELECT id,recordsegmentnumber,name,fileorder,label,variableformattype_id,variableintervaltype_id,filestartposition,fileendposition,formatschemaname FROM datavariable WHERE datatable_id=$datatable_id ORDER BY fileorder});
	$sth->execute();

	my $var_id; 
	my $var_recsegnum; 
	my $var_name; 
	my $var_order; 
	my $var_label; 
	my $var_format; 
	my $var_interval;  
	my $var_start; 
	my $var_end; 
	my $var_fmtschema;
	my $varcntr=0;
	while ( ($var_id,$var_recsegnum, $var_name,$var_order,$var_label,$var_format,$var_interval,$var_start,$var_end,$var_fmtschema)
		= $sth->fetchrow() )
	{

	    # the variable id in the Dataverse notation: 

	    my $dv_var_id = "v" . $var_id; 

	    # check if the variable is among the ones requested: 

	    if ( $varID->{$dv_var_id} )
	    {
		$self->{_varRecSegN}->{$dv_var_id} = $var_recsegnum; 
		$self->{_varNameH}->{$dv_var_id} = $var_name; 

		$self->{_varStartPos}->{$dv_var_id} = $var_start;
		$self->{_varEndPos}->{$dv_var_id} = $var_end;
		$self->{_varFormat}->{$var_name} = &getFormatToken($var_fmtschema);
		$self->{_formatName}->{$var_name} = $var_fmtschema;

		push @{$self->{_varNameA}}, $var_name; 
		$varcntr++ ;
		$self->{_varNoMpTbl}->{$dv_var_id} = $varcntr; 
		$self->{_varNoRcut}->{$dv_var_id} = $var_order + 1; 
		push @{$self->{_varNo}}, $dv_var_id; 
		

		my $var_type; 

		if ( $var_interval == 2 ) 
		{
		    $var_type = 2; 
		}
		elsif ( $var_interval == 1 ) 
		{
		    if ( $var_format == 1 ) 
		    {
			$var_type = 1; 
		    }
		    elsif ( $var_format == 2 ) 
		    {
			$var_type = 0; 
		    }
		}

		push @{$self->{_varType}}, $var_type; 

		if ( $var_type == 0 ) 
		{
		    $self->{_charVarTbl}->{$dv_var_id} = 'y'; 
		}
		
		$var_label=~s/\"/\\"/g;
		push @{$self->{_varLabel}}, $var_label; 

		# for discrete variables, more lookups will have to 
                # be done; but for now we are just going to 
                # remember them for future lookup.

		if ( $var_type == 1 || $var_type == 0 )
		{

		    $disc_Var->{$var_id} = 1; 

		    if ( $var_id > $max_disc_varid )
		    {
			$max_disc_varid = $var_id; 
		    }
		    if ( $var_id < $min_disc_varid )
		    {
			$min_disc_varid = $var_id; 
		    }
	
		    $count_disc_var++; 
		}

	    }
	    else
	    { 
		$self->{wholeFile} = 0; 
		# skip; 
	    }
	}

	$sth->finish; 

	# let's do the lookups for the discrete vars:

	if ( $count_disc_var )
	{
	    my $sth1 = $dbh->prepare(qq {SELECT id,label,value,datavariable_id FROM variablecategory WHERE ( datavariable_id >= $min_disc_varid AND datavariable_id <= $max_disc_varid ) });
	    $sth1->execute();

	    my $dv_var_id = "";
		
	    while ( my ($val_id, $val_label, $val_value, $var_dbid) = $sth1->fetchrow() )
	    {

		$dv_var_id = "v" . $var_dbid; 
		$var_name = $self->{_varNameH}->{$dv_var_id};

		# check if the variable is among the ones requested, 
		# and discrete.

		if ( $varID->{$dv_var_id} && $disc_Var->{$var_dbid} == 1 )
		{
		    if ( $val_label ne "" )
		    {
			$self->{_valLblTbl}->{$var_name} = [] unless $self->{_valLblTbl}->{$var_name}; 
			push @{$self->{_valLblTbl}->{$var_name}}, [$val_value, $val_label]; 
		    }
		}
	    }
		    
	    $sth1->finish; 

	    my $sth1 = $dbh->prepare(qq {SELECT endvalue,beginvalue,datavariable_id FROM variablerange WHERE ( datavariable_id >= $min_disc_varid AND datavariable_id <= $max_disc_varid ) });
	    $sth1->execute();

	    while ( my ($endvalue, $beginvalue, $var_dbid) = $sth1->fetchrow() )
	    {
		$dv_var_id = "v" . $var_dbid; 
		$var_name = $self->{_varNameH}->{$dv_var_id};

		# check if the variable is among the ones requested,
		# and discrete.

		if ( $varID->{$dv_var_id} && $disc_Var->{$var_dbid} == 1 )
		{
		    $self->{_mssvlTbl}->{$var_name} = [] unless $self->{_mssvlTbl}->{$var_name}; 
		    push @{$self->{_mssvlTbl}->{$var_name}}, [$beginvalue]; 
		}
	    }
		    
	    $sth1->finish; 
	}


	# finally, one more check to see if this is a Census URL:

	my $sth = $dbh->prepare(qq {SELECT filesystemlocation FROM studyfile WHERE id=$fileID});

	$sth->execute();
	my ($location) = $sth->fetchrow(); 

	if ( $location =~/^http:\/\/.*census\.gov/i )
	{
	    $self->{censusURL} = $location; 
	}
	 
	$sth->finish; 
	
	$dbh->disconnect; 

	my $temp= {};

	while ((my $key, my $value) = each(%{$self})) {
		$temp->{$key} = $value;
	}

	# additional values: 

	$temp->{unsafeVarName} = 0; 
	
	return $temp;
}

1;

