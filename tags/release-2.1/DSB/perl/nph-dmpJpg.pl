#!/usr/bin/perl

# Copyright (C) 2001 President and Fellows of Harvard University
#	  (Written by Akio Sone)
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

use CGI;

$q = new CGI;
my $jpgflnm = $q->param('jpgfn');

#($dev, $ino, $mode, $nlink, $uid, $gid, $rdev, $size) = stat $jpgflnm ;
#($size) = stat $jpgflnm ;
$size = -s $jpgflnm;

print $q->header( -type=>'image/jpeg', -expires=>'+5m', -nph=>1);
binmode STDOUT;
open(jpgFile, $jpgflnm);
while (read jpgFile, $buffer, $size){
    print $buffer;
}
close(jpgFile);

 unlink ($jpgflnm);
 
