// supporting javascript for gary king website
if (window.name == 'ChangeDetectionWiz') { 
	top.location.href = 'http://www.ChangeDetection.com/detect.html?url='+escape(document.location.href)+'&email=enter+email';
}

if (document.title.substring(0,9) != 'Gary King') { 
	document.title = 'Gary King - ' + document.title;
}	
var gFileName = location.pathname.substring(location.pathname.lastIndexOf('/')+1); 
if (gFileName.toLowerCase() == 'group.shtml') document.location.href = 'group.php?relations=Staff' + document.location.hash;
gFileName = gFileName.substring(0,gFileName.lastIndexOf("."))
var lp = location.pathname.substring(0,location.pathname.lastIndexOf('/')).toLowerCase() 
var imgPrefix = imgPrefix = '/';

//not on gking server, so make all img & hrefs absolute URL's, like if we're on google search results or VDC server. -rmesard 01-apr-2006
if ( location.hostname.toLowerCase().indexOf( "gking.harvard.edu" ) == -1 ) imgPrefix = 'http://gking.harvard.edu/';

var urlPrefix = imgPrefix;
var isOffSiteHost = false;
if ( location.hostname.toLowerCase().indexOf( "gking." ) != -1 ) { 
	imgPrefix = '/';
	if ( lp.indexOf( "/beta" ) != -1 ) { 
		urlPrefix = urlPrefix + 'beta/';
	}
} else { 
	isOffSiteHost = true;
}
//alert(isOffSiteHost)
var headerCalled = false;
var footerCalled = false;
var isPrintPage = ( queryString('print',document.location) == '1' ) ? true : false;
var isGoogleCachePage = false; //used to decide if we need to bump all content down ~200px to accommodate search engine disclaimer text

if (document.location.search != 0 && isOffSiteHost) { 
	if ( document.location.search.toLowerCase().indexOf( "q=" ) != -1 ) {
		isGoogleCachePage = ( queryString('q',document.location).toLowerCase().indexOf( "cache" ) != -1 ) ? true : false;
	}
	// look for additional clues that we're a search engine, use some generic but reliable URL tests. -rmesard 16-jan-2006
	if (!isGoogleCachePage) { 
		//potential search query that's longer than 1 char, and we're not on an .edu server
		if (document.location.search.length > 4 && (location.hostname.toLowerCase().indexOf( ".edu" ) == -1)) {
			isGoogleCachePage = true;
		}
	}	
	if ( top.window.frames.length > 0 || location.pathname.toLowerCase().indexOf( "/u/gking" ) != -1 ) {
		// we're on regular king-branded google search results, or...
		// we may be on some offsite URL that has additional content in its own top frame, like google translation - no need to bump page contents down
		isGoogleCachePage = false
	}
}

var isHomePage = false;
var thepathArray = location.pathname.split("/")
var isPrimaryPage = true;
var isAbstractPage = false;
if (thepathArray.length == 2) { 
	if (thepathArray[1] == 'homepage.html' || thepathArray[1] == '') isHomePage = true;
}

function checklp(inputStr) {
	if (lp.indexOf(inputStr) != -1) { 
		isPrimaryPage = false;
		if (gFileName == 'index' || gFileName == '') { 
			isAbstractPage = true;
		}
	}
}

//exceptions to the isPrimaryPage rule go here
checklp("/amelia");
checklp("/clarify/docs");
checklp("/judgeit");
checklp("/matchit");
checklp("/whatif");
checklp("/yourcast");
checklp("/zelig");
checklp("/vign/eg/priv");
checklp("/vign");
checklp("/va");
checklp("/anchors");
checklp("/qr");
checklp("/readme");
checklp("/blogconv");

if (isPrintPage) isPrimaryPage = false;

// browser version check
var ver="unknown";
bName = navigator.appName;
bVer = parseInt(navigator.appVersion);
if      (bName == "Netscape" && bVer >= 3) ver = "n3";
else if (bName == "Netscape" && bVer == 2) ver = "n2";
else if ( navigator.userAgent.toLowerCase().indexOf( "opera" ) != -1 ) ver = "o4";
else if (bName == "Microsoft Internet Explorer" && bVer >= 2)
        ver = "e3";

document.writeln("<link rel='stylesheet' href='" + imgPrefix + "gking.css' MEDIA='screen' TYPE='text/css'>");
document.writeln("<style type='text/css'>");
var addH = 0;

if (document.compatMode == 'CSS1Compat' && ver != 'o4') { 
	document.writeln(" #navalsodiv { left: -6px; } ");
}	

if (isGoogleCachePage) { 
	addH = 210;
	document.writeln(" #gkingnamebox, #logodiv, #orangeline_vert  { top: " + eval(0 + addH) + "px; } "); 
	document.writeln(" #gkingheadbox { top: " + eval(-1 + addH) + "px; } ");
	document.writeln(" #homecopy { top: " + eval(190 + addH) + "px; } ");
	document.writeln(" #gking_name_shimbox, #orangeline_horiz { top: " + eval(90 + addH) + "px; } ");
	document.writeln(" #maindiv { top: " + eval(23 + addH) + "px; } ");
	document.writeln(" #navalsodiv { top: " + eval(70 + addH) + "px; } ");
	document.writeln(" #gkingheadbox { display: none; } ");
}

// ensure we don't display headshot jpg offsite, causes too much trouble
if (document.location.search != 0 && isOffSiteHost) document.writeln(" #gkingheadbox { display: none; } ");

if (!isPrimaryPage) { 
	document.writeln(" #maindiv { left: 10 ! important; } ");
	document.writeln(" #gking_name_shim3, #orangeline_vert, #orangeline_horiz, #logotd, #gkingheadbox { display: none; } ");
} else { 
	document.writeln("td.latexnavhome { display: none; } ");
	if (isHomePage) { 
		if (!isPrintPage && !isGoogleCachePage) document.writeln(" #maindiv { top: " + eval(142 + addH) + "px; } ");
		document.writeln(" #footerspecial {	height: 422px ! important; } ");
	}
}
if (isAbstractPage) { 
	document.writeln(" SPAN.copyright { display: none; } ");
	document.writeln(" #footer { margin:0; padding:0; } ");
}
document.writeln("</style>");

//cookie code, copied from pre-2006 gking site
var cookie_name = "gCookie";
function doCookie() {
    if(document.cookie) {
            index = document.cookie.indexOf(cookie_name);
    } else {
            index = -1;
    }

    if (index == -1) {
            document.cookie=cookie_name+"=1;";
    } else {
            countbegin = (document.cookie.indexOf("=", index) + 1);
            countend = document.cookie.indexOf(";", index);
            if (countend == -1) {
                    countend = document.cookie.length;
            }
            count = eval(document.cookie.substring(countbegin, countend)) + 1;
            document.cookie=cookie_name+"="+count+";";
    }
}

function gettimes() {
    if(document.cookie) {
    index = document.cookie.indexOf(cookie_name);
    if (index != -1) {
        countbegin = (document.cookie.indexOf("=", index) + 1);
        countend = document.cookie.indexOf(";", index);
            if (countend == -1) {
                countend = document.cookie.length;
            }
        count = document.cookie.substring(countbegin, countend);
        return (parseInt(count));
    }
    }
    return (0);
}

//utility functions, for getting name/val pairs off of querystring
var __gbl_qstr = stripQuery(this.location); 
function queryString(key, src) { 
  var __qstr = stripQuery(src); 
  var strIndex = __qstr.indexOf(key+'='); 
  if(strIndex == -1) return null; 
  var strReturn = '', ch = ''; 
 
  for(var i = strIndex + key.length; i < __qstr.length; i++) { 
    ch = __qstr.charAt(i); 
    if(ch == '&' || ch == ';') break; 
    if(ch == '+') strReturn += ' '; 
    else if(ch != '=') strReturn += ch; 
  } 
  return unescape(strReturn); 
} 

function stripQuery(src) { 
  if(src == null) return __gbl_qstr; 
 
  if(typeof src == 'string') { 
    var __qstr   = new String(); 
    var __tmpNum = src.indexOf('?'); 
     
    __qstr = (__tmpNum != -1) 
             ? src.substr( 
                 __tmpNum + 1, src.length 
               ) 
             : null; 
     
    delete __tmpNum; 
    return __qstr; 
  } 
  else if(typeof src == 'object') { // assumes the object is of type location 
    return location.search.substr(1, location.search.length); 
  } 
  else return __gbl_qstr; 
}

// allows arbitrary places in the page to register window.onload handlers
var gk_onLoadFunctions;    // the array of registered handlers

// call this from a script element in the page to register a function to be called 
// when loading is completed. The function should be parameterless.
// If fn is a string, then, at onLoad time, we just eval the string. This is useful when the function
// referenced is not defined at the time of the call, e.g. it is in a different script file.
function gk_RegisterOnLoad(fn)
{
  if (typeof gk_onLoadFunctions == "undefined")
  {
    // this is the first one we've seen, create the array
    gk_onLoadFunctions = new Array();
    if (window.onload)
    {
      // we already had a (statically defined) onload - include it in the array
      gk_onLoadFunctions.push(window.onload);
    }
    // set our master handler to run when the window has completed loading
    window.onload = gk_execOnload;
  }
  // add our function to the array
  gk_onLoadFunctions.push(fn);
}

// called when gk_RegisterOnLoad() has been used to register onload handlers
// calls each of the registered functions in turn
function gk_execOnload()
{
  if (gk_onLoadFunctions)
  {
    for(var i = 0; i < gk_onLoadFunctions.length; i++)
    {
			var fn = gk_onLoadFunctions[i];
			if (typeof(fn) == "string")
				eval(fn)
			else
				fn();
    }
  }
}

function clearInputField(inputObject) { 
	if(inputObject.value.substring(0,5) == 'Enter') 
		{inputObject.value = '';} 
	return false; } 

function printWin() {
	if (isPrimaryPage) { 
		document.location = document.location + '?print=1';
	} else { 
		window.print(); 
	}
}

function print_page() {
  if (navigator.userAgent.toLowerCase().indexOf("mac") != - 1 && ! window.netscape)
    {
      window.print();
//      alert("Press Command+P to Print");
    }
  else
    {
      window.print();
    }
}

function spacerImg(h,w) {
	return '<img src="' + imgPrefix + 'images/c.gif" height="'+h+'" width="'+w+'" alt="" border="" class="spacer" />';
}

function displayNavItem(text,link) {
	return '<tr class="navmenutr"><td class="navmenutd" title="'+text+'" style="border-width: 0 0 1px 1px ! important;" onMouseOver="this.className=\'navmenutd2\';status=\''+text+'\'" onMouseOut="this.className=\'navmenutd\';status=\'\'" onClick="location=\''+ urlPrefix + link + '\'" id="navmenutd_top"><a href="'+ urlPrefix + link + '">' + text + '</a></td></tr>';
}

function navItem(text, link) { 
	this.text = text;
	this.link = link;
}

function displayNavMenu() { 
	var navItemArray = new Array();
	navItemArray[0] = new navItem("Bio &amp; C.V.", "bio.shtml"); 
	navItemArray[1] = new navItem("Writings", "writings.shtml"); 
	navItemArray[2] = new navItem("Software", "stats.shtml"); 
	navItemArray[3] = new navItem("Data", "data.shtml"); 
	navItemArray[4] = new navItem ("Research Group", "group.php"); 
	navItemArray[5] = new navItem ("Class Materials", "class.shtml"); 
	navItemArray[6] = new navItem ("Links", "links.shtml"); 
	navItemArray[7] = new navItem ("Contact", "contact.shtml");

	var navItemArray2;
	var navMenuHTML;
	navMenuHTML = '<div !id="navmenudiv"><table border="0" cellpadding="0" cellspacing="0"><tr class="faderow"><td class="darkgray">' + spacerImg(10,1) + '</td>'
	+ '<td class="fadedark_horiz_top">' + spacerImg(10,1) + '</td><td class="fadedark_horiz_top">' + spacerImg(10,1) + '</td><td class="fadedark_horiz_top">' + spacerImg(10,10) + '</td></tr>'
	+ '<tr class="faderow"><td class="fadedark_vert_left">' + spacerImg(10,10) + '</td><td>' + spacerImg(10,10) + '</td><td colspan="2" class="navmenutd" id="navspecial">' + spacerImg(10,10) + '</td></tr>'
	+ '<tr><td class="fade_vert_left">' + spacerImg(1,10) + '</td><td>' + spacerImg(10,10) + '</td>'
	+ '<td id="navmenuoutertd" colspan="2"><table id="navmenutable" border="0" cellpadding="0" cellspacing="0">';
	for (var i=0;i<navItemArray.length;i++) {
		navMenuHTML += displayNavItem(navItemArray[i].text,navItemArray[i].link);
	}
	navMenuHTML += '</table></td></tr><tr class="faderow"><td><img src="' + imgPrefix + 'images/fadecorner_sw_top.gif" height="10" width="10" alt="" /></td><td>' + spacerImg(10,10) + '</td><td>' + spacerImg(10,10) + '</td>'
	+ '<td class="navmenutd" id="navspecial">' + spacerImg(10,10) + '</td></tr><tr class="faderow"><td><img src="' + imgPrefix + 'images/fadecorner_sw.gif" height="10" width="10" alt="" /></td>'
	+ '<td><img src="' + imgPrefix + 'images/fadecorner_sw_right.gif" height="10" width="10" alt="" /></td><td class="fade_horiz_bot">' + spacerImg(10,1) + '</td>'
	+ '<td class="fade_horiz_bot" align="right"><img src="' + imgPrefix + 'images/fadecorner_ne_inner.gif" width="10" height="10" alt="" /><img src="' + imgPrefix + 'images/ffffff.gif" width="10" height="10" border="0" alt="" /></td></tr></table>'; 
	w(navMenuHTML);
}

function doChangeDetectionPopup() {
	window.open(document.location,'ChangeDetectionWiz','resizable=yes,scrollbars=yes,width=624,height=460');
	return true;
}

function displaySearchGoogle() {
	var searchGoogleHTML;
	var q = ( (location.hostname.toLowerCase().indexOf( "google." ) != -1) && (queryString('q',document.location) != null ) ) ? queryString('q',document.location) : 'Enter search text';
	//<!-- Search Google -->
//	searchGoogleHTML = '<div align="center"><table cellspacing="0" border="0"><form method="get" action="http://www.google.com/u/gking" name="googlesearch" onSubmit="if (q.value == \'\' || q.value == \'Enter search text\'){alert(\'Please enter a search query.\');q.focus();return false;}if (sitesearch[2].checked){document.location.href=\'http://scholar.google.com/scholar?q=\'+q.value;return false}">'
	searchGoogleHTML = '<div align="center"><table cellspacing="0" border="0"><form method="get" action="http://www.google.com/u/gking" name="googlesearch" onSubmit="if (document.forms.googlesearch.q.value == \'\' || document.forms.googlesearch.q.value == \'Enter search text\'){document.forms.googlesearch.q.focus();return false;}if (document.forms.googlesearch.sitesearch[2].checked){document.location.href=\'http://scholar.google.com/scholar?q=\'+document.forms.googlesearch.q.value;return false};">'
	+ '<tr valign="middle" align="left"><td><input type="text" name="q" size="31" maxlength="255" value="' + q + '" class="txtinput" id="txtinputgoogle" title="Google Search" onFocus="return clearInputField(this)"> '
	+ '<input type="hidden" name="domains" value="gking.harvard.edu">'
	+ '<br /><input type="radio" name="sitesearch" id="sitesearch1" value="gking.harvard.edu" checked> <label for="sitesearch1">This Site</label>' 
	+ '<br /><input type="radio" name="sitesearch" id="sitesearch2" value="harvard.edu"> <label for="sitesearch2">Harvard University</label>'
	+ '<br /><input type="radio" name="sitesearch" id="sitesearch3" value="scholar.google.com"> <label for="sitesearch3">Google Scholar</label>'
	+ '<br /><input type="radio" name="sitesearch" id="sitesearch4" value=""> <label for="sitesearch4">The Web</label>'
	+ '<br /><button name="sa" class="button" id="buttongoogle" onClick="this.className=\'b2\';if (document.forms.googlesearch.q.value == \'\' || document.forms.googlesearch.q.value == \'Enter search text\'){document.forms.googlesearch.q.focus();return false;}if (document.forms.googlesearch.sitesearch[2].checked){document.location.href=\'http://scholar.google.com/scholar?q=\'+document.forms.googlesearch.q.value;return false};document.forms.googlesearch.submit();" onBlur="this.className=\'button\'">Google Search</button>'
	+ '</td></tr></form>'
	+ '</table></div>'
	//<!-- Search Google -->
	w(searchGoogleHTML);
//	alert('qstr = ' + qStr);
}

function displayTools() {
	var toolsHTML = '<div id="tools"><hr /><form name="translate" method="get" action="http://translate.google.com/translate" target="_top">'
	+ '<input type="hidden" name="u" value="' + document.location.href + '" />'
	+ '<input type="hidden" name="langpair" value="" />'
	// not on google search results or translated page
	if (location.hostname.toLowerCase().indexOf( "google." ) == -1 && location.pathname.toLowerCase().indexOf( "translate_c" ) == -1) { 
		toolsHTML += '<a href="http://www.ChangeDetection.com/detect.html" target="ChangeDetectionWiz" onClick="window.open(\'\',\'ChangeDetectionWiz\',\'resizable=yes,scrollbars=yes,width=624,height=460\');return true"><img src="' + imgPrefix + 'images/delta.gif" height="15" width="15" border="0" alt="" /><span class="toolslink">Track changes</span></a><br />'
	}
	toolsHTML += '<a href="javascript:printWin()" title="Print Page" onMouseOver="self.status=this.title;return true;" onMouseOut="self.status=\'\';return true;"><img src="' + imgPrefix + 'images/print2.gif" height="13" width="15" border="0" alt="" /><span class="toolslink">Print page</span></a>'
	+ '<br /><a href="mailto:?subject=' + document.title + '&body=%0D%0A' + document.location + '%0D%0A" title="Email a Link to this page" onMouseOver="self.status=this.title;return true;" onMouseOut="self.status=\'\';return true;"><img src="' + imgPrefix + 'images/email2.gif" height="10" width="15" border="0" alt="" /><span class="toolslink">Email page</span></a>'
	+ '<br /><a href="javascript:addBookmark()" title="Bookmark Page" onMouseOver="self.status=this.title;return true;" onMouseOut="self.status=\'\';return true;"><img src="' + imgPrefix + 'images/bookmark2.gif" height="11" width="15" border="0" alt="" /><span class="toolslink">Bookmark page</span></a>'
	// not already on translated page, show translation option
	if (location.pathname.toLowerCase().indexOf( "translate_c" ) == -1) { 
		toolsHTML += '<br /><a href="javascript:onClick=document.forms.translate.langpairstub.focus()"><img src="' + imgPrefix + 'images/translate.gif" width="15" height="12" border="0" alt="" /></a>'
		+ '<select name="langpairstub" onChange="langpair.value=this.options[this.selectedIndex].value;this.selectedIndex=0;submit()">'
		+ '<option value="" selected>Translate page to...</option>'
		+ '<option value="en|de">German</option>'
		+ '<option value="en|es">Spanish</option>'
		+ '<option value="en|fr">French</option>'
		+ '<option value="en|it">Italian</option>'
		+ '<option value="en|pt">Portuguese</option>'
		+ '<option value="en|ja">Japanese</option>'
		+ '<option value="en|ko">Korean</option>'
		+ '<option value="en|zh-CN">Chinese (Simplified)</option>'
		+ '</select>'
	}
	w(toolsHTML);
	w('</form></div>');
}

function addBookmark() {
	if (ver == 'e3') { 
		window.external.AddFavorite(document.location,document.title);
	} else if (ver == 'o4') { 
		alert('Press CTRL-T to bookmark: \n' + document.title);
	}
	  else { 
		alert('Press CTRL-D to bookmark: \n' + document.title);
	}
}

function displayAlsoBox() {
	var alsoBoxHTML, alsoBoxHTML2;
	//<!-- also of interest box -->

	alsoBoxHTML = '<div id="alsotest"><table border="0" cellpadding="0" cellspacing="0"><tr class="faderow"><td><img src="' + imgPrefix + 'images/fadecorner_nw.gif" height="10" width="10" alt="" /></td>'
	+ '<td><img src="' + imgPrefix + 'images/fadecorner_nw_right.gif" height="10" width="10" alt="" /></td>'
	+ '<td class="fade_horiz_top">' + spacerImg(10,150) + '</td>'
	+ '<td><img src="' + imgPrefix + 'images/fadecorner_se_inner.gif" height="10" width="10" alt="" /></td>'
	+ '<td>' + spacerImg(10,1) + '</td></tr>'
	+ '<tr class="faderow"><td><img src="' + imgPrefix + 'images/fadecorner_nw_bot.gif" height="10" width="10" alt="" /></td>'
	+ '<td>' + spacerImg(1,10) + '</td>'
	+ '<td>' + spacerImg(1,10) + '</td>'
	+ '<td class="fadelight_vert_left">' + spacerImg(1,10) + '</td>'
	+ '<td>' + spacerImg(1,10) + '</td></tr>'
	+ '<tr><td class="fade_vert_left">' + spacerImg(1,10) + '</td>'
	+ '<td>' + spacerImg(1,10) + '</td>'
	+ '<td><table border="0" cellpadding="0" cellspacing="0" width="100%">'
	+ '		<tr><td>' + spacerImg(200,1) + '</td>'
	+ '		<td id="maindivoutertd" style="border: solid 1px #cc6733; border-width: 0 0 1px 1px;"><div id="maindivinner" style="border-width: 0">'
	+ '		<table cellpadding="0" cellspacing="0" border="0" id="alsotable">		<tr><td>'

	alsoBoxHTML2 = '</td></tr></table></div></td></tr></table></td>'
	+ '<td class="fadelight_vert_left" id="alsospecial1">' + spacerImg(10,10) + '</td><td id="alsospecial2">'
	+ spacerImg(10,10)
	
	+ '</td></tr>'
	+ '<tr class="faderow"><td><img src="' + imgPrefix + 'images/fadecorner_sw_top.gif" height="10" width="10" alt="" /></td>'
	+ '<td>' + spacerImg(1,10) + '</td><td>' + spacerImg(1,10) + '</td>'
	+ '<td class="fadelight_vert_left">' + spacerImg(1,10) + '</td><td>' + spacerImg(10,10) + '</td></tr>'
	+ '<tr class="faderow"><td><img src="' + imgPrefix + 'images/fadecorner_sw.gif" height="10" width="10" alt="" /></td><td><img src="' + imgPrefix + 'images/fadecorner_sw_right.gif" height="10" width="10" alt="" /></td>'
	+ '<td class="fade_horiz_bot">' + spacerImg(10,1) + '</td><td><img src="' + imgPrefix + 'images/fadecorner_ne_inner.gif" height="10" width="10" alt="" /></td>'
	+ '<td>' + spacerImg(1,10) + '</td></tr></table></div>'
	+ '</div>' //navmenudiv

	w(alsoBoxHTML);
	displaySearchGoogle();
	displayTools();
	w(alsoBoxHTML2);
}

function displayHeader() {
	if (!headerCalled) { 
		var headerHTML;
		headerHTML = '<!-- name graphic -->'
		+ '<div id="gkingnamebox"><a href="'+urlPrefix+'homepage.html"><img src="' + imgPrefix + 'images/gking_name.gif" width="280" height="105" border="0" alt="Gary King" id="gkingnameimg" /></a></div>'
		+ '<div id="gking_name_shimbox"><img src="' + imgPrefix + 'images/gking_name_shim.gif" height="28" width="105" alt="" class="spacer" /></div>';

		if (isPrimaryPage) {
			w(headerHTML);
			w('<div id="navalsodiv"><table cellpadding="0" cellspacing="0" id="special"><tr><td valign="top" align="right" id="specialtd">');
			displayNavMenu();
			w('</td></tr><tr><td valign="top">');
			displayAlsoBox();
			w('</td></tr></table></div>');
		}	

		headerHTML = '<div id="maindiv">'
		+ '<table border="0" cellpadding="0" cellspacing="0">'
		+ '<tr class="faderow"><td colspan="4" align="right" id="logotd" style="background-color:Transparent"><a href="http://iq.harvard.edu"><img src="' + imgPrefix + 'images/logo_new_iq.gif" width="41" height="47" border="0" alt="IQ Logo" /></a><a href="http://www.harvard.edu"><img src="' + imgPrefix + 'images/logo_new_harvard.gif" width="42" height="47" border="0" alt="Harvard Logo" /></a></td><td class="gray"></td><td class="gray"></td></tr>'
		+ '<tr class="faderow"><td><img src="' + imgPrefix + 'images/fadecorner_nw.gif" height="10" width="10" alt="" /></td>'
		+ '<td class="fadecorner_nw_right">' + spacerImg(10,10) + '</td><td class="fade_horiz_top" nowrap="true"><img src="' + imgPrefix + 'images/gking_name_shim3.gif" id="gking_name_shim3" height="10" width="14" alt="" />' + spacerImg(10,1) + '</td>'
		+ '<td class="fade_horiz_top"><img src="' + imgPrefix + 'images/fadecorner_ne_left.gif" height="10" width="10" alt="" /></td><td><img src="' + imgPrefix + 'images/fadecorner_ne.gif" height="10" width="10" alt="" /></td></tr>'
		+ '<tr class="faderow"><td><img src="' + imgPrefix + 'images/fadecorner_nw_bot.gif" height="10" width="10" alt="" /></td><td>' + spacerImg(1,10) + '</td><td>' + spacerImg(1,10) + '</td><td>' + spacerImg(1,10) + '</td><td><img src="' + imgPrefix + 'images/fadecorner_ne_bot.gif" height="10" width="10" alt="" /></td></tr>'
		+ '<tr><td class="fade_vert_left">' + spacerImg(1,10) + '</td><td style="border-right:solid 1px #CC6733">' + spacerImg(1,10) + '</td><td id="maindivoutertd"><div id="maindivinner" style="border-width: 0">';

		w(headerHTML);
//		if (isAbstractPage) { 
//			w('<table border="0" cellpadding="0" cellspacing="0" class="latexnav"><tr><td class="latexnavhome"><a href="/"><img src="/images/gking_name_sm.gif" height="30" width="119" border="0" alt="Gary King Homepage" /></a></td></tr></table>');
//		}
		headerCalled = true;
	}
}

function displayFooter() {
	if (!footerCalled) { 
//		alert(document.getElementsByTagName("address").length);
		var isLatexPage = (document.getElementsByTagName("address").length != 0) ? true : false;
		var minWidth = (isPrimaryPage) ? 524 : 714;
		var minHeight = (isHomePage) ? 441 : 561;
		var footerHTML = '	</div></td><td id="footerspecial">' + spacerImg(minHeight,9) + '</td><td class="fade_vert_right">' + spacerImg(1,10) + '</td></tr>'
		+ '<tr><td class="fade_vert_left">' + spacerImg(1,10) + '</td>'
		+ '<td style="border: solid 1px #CC6733; border-width: 0 1px 0 0">' + spacerImg(1,10) + '</td><td>'
		if (!isLatexPage) { 
			footerHTML += '<div id="footer"><span class="copyright"><a name="end">'
			+ '<a href="'+urlPrefix+'copyright.shtml">Copyright</a>	&copy; 1996-2007</span> <a href="'+urlPrefix+'">';
			if (isAbstractPage) { 
				footerHTML += '<img src="' + imgPrefix + 'images/gking_name_sm.gif" height="30" width="119" border="0" alt="Gary King" style="border-color: #CC6733; border-width: 1px 1px 0 0;" />';
			} else { 
				footerHTML += 'Gary King';
			}
			footerHTML += '</a><span class="copyright">, All Rights Reserved.</span>'
			+ '</div>'
		}
		footerHTML += '		<!-- ChangeDetection.com id="2w10djcjjd" -->'
		+ '</td>'
		+ '<td style="border: solid 1px #CC6733; border-width: 0 0 0 1px">' + spacerImg(1,10) + '</td><td class="fade_vert_right">' + spacerImg(1,10) + '</td></tr>'
		+ '<tr class="faderow"><td><img src="' + imgPrefix + 'images/fadecorner_sw_top.gif" height="10" width="10" alt="" /></td>'
		+ '<td>' + spacerImg(1,10) + '</td><td style="border: solid 1px #CC6733; border-width: 1px 0 0 0">' + spacerImg(1,10) + '</td>'
		+ '<td>' + spacerImg(1,10) + '</td><td><img src="' + imgPrefix + 'images/fadecorner_se_top.gif" height="10" width="10" alt="" /></td></tr>'
		+ '<tr class="faderow"><td><img src="' + imgPrefix + 'images/fadecorner_sw.gif" height="10" width="10" alt="" /></td>'
		+ '<td class="fade_horiz_bot"><img src="' + imgPrefix + 'images/fadecorner_sw_right.gif" height="10" width="10" alt="" /></td>'
		+ '<td class="fade_horiz_bot">' + spacerImg(10,minWidth) + '</td>'
		+ '<td class="fade_horiz_bot"><img src="' + imgPrefix + 'images/fadecorner_se_left.gif" height="10" width="10" alt="" /></td>'
		+ '<td><img src="' + imgPrefix + 'images/fadecorner_se.gif" height="10" width="10" alt="" /></td>'
		+ '<td class="gray">' + spacerImg(1,10) + '</td></tr></table></div>'
		+ '<div id="orangelines"><div id="orangeline_horiz">' + spacerImg(1,200) + '</div><div id="orangeline_vert">' + spacerImg(200,1) + '</div></div>'
		w(footerHTML);

		// hide old-fashioned latex navigation buttons. -rmesard 23dec2005
		for (var i=0;i<document.images.length;i++) {
			if ( (document.images[i].src.toLowerCase().indexOf( "/home.gif" ) != -1 ) || (document.images[i].src.toLowerCase().indexOf( "/previous_" ) != -1 ) || (document.images[i].src.toLowerCase().indexOf( "/up_" ) != -1 ) || (document.images[i].src.toLowerCase().indexOf( "/next_" ) != -1 ) )
			{ 
//				document.images[i].width = 0;
//				document.images[i].height = 0;
				document.images[i].style.display = 'none';
			}
		}

/*		for (var i=0;i<document.links.length;i++) { 
			alert(document.links[i]);
		}
*/
		if (isPrintPage) { 
			gk_RegisterOnLoad(print_page);
			w('<div id="backdiv">');
			if (document.referrer != '') { 
				w('<a id="backlink" href="' + document.referrer + '">');
			} else { 
				w('<a id="backlink" href="javascript:history.back()">');
			}
			w('&lt;&lt; back</a>')
			w('&nbsp;&nbsp;<a href="javascript:printWin()" title="Print Page" onMouseOver="self.status=this.title;return true;" onMouseOut="self.status=\'\';return true;">print</a>');
			w('</div>');
		}
		footerCalled = true;
	}
document.write("</script>");
document.write("<script src='http://www.google-analytics.com/urchin.js' type='text/javascript'>");
document.write("_uacct = 'UA-301128-1' ");
document.write("urchinTracker()");
document.write("</script>");
}

function w(thetext) { 
	document.writeln(thetext);
}

//alert(document.compatMode)
