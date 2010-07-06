Summary: mini-DSB rpm
Group: Applications
Name: DVN-DSB-mini
Version: 1.1
Release: 16a
Source: DVN-DSB-%{version}.tar.gz
Copyright: Artistic or GPL
URL: http://thedata.org/
Packager: leonid@latte.harvard.edu
BuildRoot: %{_tmppath}/%{name}-%{version}
Provides: %{name}

%description
This RPM provides the bare minimum of the VDC DSB functionality necessary for processing ICPSR studies. The DSB is written by Akio Sone and Leonid Andreev.

%prep
rm -rf $RPM_BUILD_ROOT
gzip -dc %{SOURCE0} | tar -xf - --exclude CVS
%setup -T -D
%build
make RPM_PREFIX="$RPM_BUILD_ROOT" build-mini

%install
make RPM_PREFIX="$RPM_BUILD_ROOT" install
find ${RPM_BUILD_ROOT} -name '*~' -exec rm -f '{}' ';'

%clean
rm -rf ${RPM_BUILD_ROOT}

%files
%defattr(-,root,root)
%dir /usr/local/VDC
%dir /usr/local/VDC/R
%dir /usr/local/VDC/R/library
%dir /usr/local/VDC/etc
%dir /usr/local/VDC/sbin
%dir /usr/local/VDC/perl
%dir /usr/local/VDC/perl/lib
%dir /usr/local/VDC/perl/lib/VDC
%dir /usr/local/VDC/perl/lib/VDC/DSB
%dir /usr/local/VDC/perl/lib/VDC/DSB/Ingest
%dir /usr/local/VDC/perl/lib/Parse
/usr/local/VDC/R/library/vdc_startup.R
/usr/local/VDC/R/installR.sh
/usr/local/VDC/etc/Rmodules.txt
/usr/local/VDC/sbin/DDIsec4sps.pl
/usr/local/VDC/perl/lib/VDC/DSB/Ingest/SPS.pm
/usr/local/VDC/perl/lib/VDC/DSB/Ingest/StatDataFileReaderFactory.pm
/usr/local/VDC/perl/lib/VDC/DSB/Ingest/StatData.pm
/usr/local/VDC/perl/lib/Parse/RecDescent.pod
/usr/local/VDC/perl/lib/Parse/RecDescent.pm


%post
(cd /usr/local/VDC/R; ./installR.sh)

