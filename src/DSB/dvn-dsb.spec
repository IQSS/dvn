Summary: This RPM provides the DSB modules
Group: Applications
Name: DVN-DSB
Version: 1.2
Release: 0
Source: DVN-DSB-%{version}.tar.gz
Copyright: Artistic or GPL
URL: http://thedata.org/
Packager: vdc-dev@latte.harvard.edu
BuildRoot: %{_tmppath}/%{name}-%{version}
Provides: %{name}
Provides: perl(glv03)
Provides: perl(Parse::RecDescent)
Provides: perl(XML::SAX::PurePerl::DTDDecls)
Provides: perl(XML::SAX::PurePerl::DocType)
Provides: perl(XML::SAX::PurePerl::EncodingDetect)
Provides: perl(XML::SAX::PurePerl::XMLDecl)

%description
The RPM provides the DSB modules, written by Akio Sone and Leonid Andreev.

%prep
rm -rf $RPM_BUILD_ROOT
gzip -dc %{SOURCE0} | tar -xf - --exclude CVS
%setup -T -D
%build
make RPM_PREFIX="$RPM_BUILD_ROOT" build

%install
make RPM_PREFIX="$RPM_BUILD_ROOT" install
find ${RPM_BUILD_ROOT} -name '*~' -exec rm -f '{}' ';'

%clean
rm -rf ${RPM_BUILD_ROOT}

%files
%defattr(-,root,root)
/usr/local/VDC/R/library/*
/usr/local/VDC/R/VDCutil/*
/usr/local/VDC/R/installR.sh
/usr/local/VDC/bin/rcut
/usr/local/VDC/cgi-bin/*
/usr/local/VDC/etc/*
%dir /usr/local/VDC/perl/lib
%dir /usr/local/VDC/perl/vdcRAP
/usr/local/VDC/perl/*
/usr/local/VDC/sbin/*
/usr/local/VDC/xslt/*
%attr (0777,apache,apache) %dir /tmp/VDC
%attr (0777,apache,apache) %dir /tmp/VDC/DSB
%attr (0777,apache,apache) %dir /tmp/VDC/webtemp

%post
(cd /usr/local/VDC/etc; /usr/bin/perl configure.perl /usr/local/VDC/etc /usr/local/VDC /tmp/VDC)
/sbin/service httpd restart
(cd /usr/local/VDC/R; ./installR.sh)

