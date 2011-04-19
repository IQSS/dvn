/*
 * $Id: DVNOAIArchivalUnit.java,v 1.3 2007/12/19 16:27:34 leonid Exp $
 */

/*

Copyright (c) 2000-2005 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Except as contained in this notice, the name of Stanford University shall not
be used in advertising or otherwise to promote the sale, use or other dealings
in this Software without prior written authorization from Stanford University.

*/

package edu.harvard.iq.dvn.lockss.plugin;

import java.net.*;
import java.util.*;

import org.apache.oro.text.regex.*;

import org.lockss.config.Configuration;
import org.lockss.daemon.*;
import org.lockss.plugin.UrlCacher; 
import org.lockss.plugin.definable.*;
import org.lockss.util.*;
import org.lockss.oai.*;

/**
 * <p>DVNOAIArchivalUnit: The Archival Unit Class for DVNOAIPlugin.  
 * This archival unit adds extra functionality necessary to crawl and 
 * archive DVN repositories.</p>
 * @author Leonid Andreev
 * @version 1.0
 */

public class DVNOAIArchivalUnit extends DefinableArchivalUnit {
    protected Logger logger = Logger.getLogger("DVNOAIArchivalUnit");

    public DVNOAIArchivalUnit(DefinablePlugin myPlugin,
			      ExternalizableMap map) {
	super(myPlugin, map);
    }



    protected OaiRequestData makeOaiData() {
	URL oai_request_url =
	    paramMap.getUrl(ConfigParamDescr.OAI_REQUEST_URL.getKey());
	String oaiRequestUrlStr = oai_request_url.toString();
	String oai_au_spec = null;
	String oai_namespace = null; 
	String oai_tagname = null; 
	String oai_metaprefix = null; 

	OaiMetadataHandler metadataHandler = null; 
	
	try {
	    oai_au_spec = paramMap.getString(ConfigParamDescr.OAI_SPEC.getKey());
	} catch (NoSuchElementException ex) {
	    // This is acceptable.  Null value will fetch all entries.
	    logger.debug("No oai_spec for this plugin.");
	}

	if (oai_au_spec != null && oai_au_spec.equals("::@default@::")) {
	    // This is the IMLS code for "no set". 
	    // (It cannot be left blank when configuring the plugin, since
	    // the parameter is used as "definitional" -- i.e. it is part 
	    // of what defines the AU. All definitional parameters are 
	    // mandatory. 
	    oai_au_spec = null; 
	}

	//oai_namespace = "http://purl.org/dc/elements/1.1/";
	//oai_tagname = "identifier"; 
	//oai_metaprefix = "oai_dc";

	oai_namespace = "http://www.icpsr.umich.edu/DDI";
	oai_tagname = "otherMat"; 
	oai_metaprefix = "ddi";


	logger.debug3("Creating OaiRequestData with oaiRequestUrlStr" +
		   oaiRequestUrlStr + " and oai_au_spec " + oai_au_spec + 
		   " and metadataprefix " + oai_metaprefix + 
		   " and tag " + oai_tagname);


	metadataHandler = new DVNOAIMetadataHandler ( 
						     oai_metaprefix,
						     oai_namespace,
						     oai_tagname
						     ); 

	//	return new OaiRequestData(oaiRequestUrlStr,
	//		  oai_namespace,
	//		  oai_tagname,
	//		  oai_au_spec,
	//		  oai_metaprefix
	//);
	return new OaiRequestData(oaiRequestUrlStr,
				  oai_au_spec,
				  metadataHandler
				  );
    }

    public boolean isLoginPageUrl(String url) {
	Pattern urlPat =
	    (Pattern)paramMap.getMapElement(KEY_AU_REDIRECT_TO_LOGIN_URL_PATTERN);

	if (urlPat == null) {
	    logger.debug("no login page pattern available");
	    return false;
	} else {
	    logger.debug("Login page pattern: " + urlPat.toString());
	}
	Perl5Matcher matcher = RegexpUtil.getMatcher();
	return  matcher.contains(url, urlPat);
    }    
    
    protected CrawlSpec makeCrawlSpec() throws LockssRegexpException {

	CrawlRule rule = makeRules();
	boolean follow_links = true; 
	    
	return new OaiCrawlSpec(makeOaiData(), getPermissionPages(),
				null, rule, follow_links,
				makeLoginPageChecker());
    }


    public UrlCacher makeUrlCacher(String url) {
	return new DVNOAIUrlCacher(this, url);
    }

}
