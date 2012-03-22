/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/*
 * $Id: DVNDDIPlugin.java,v 1.3 2007/12/19 12:46:29 leonid Exp $
 */
/*
Copyright (c) 2000-2003 Board of Trustees of Leland Stanford Jr. University,
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
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OAUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
Except as contained in this notice, the name of Stanford University shall not
be used in advertising or otherwise to promote the sale, use or other dealings
in this Software without prior written authorization from Stanford University.
*/
package edu.harvard.iq.dvn.lockss.plugin;

import org.lockss.app.*;
import org.lockss.config.Configuration;
import org.lockss.daemon.*;
import org.lockss.plugin.*;
import org.lockss.plugin.definable.*;
import org.lockss.util.*;

import java.io.FileNotFoundException;

/**
 * <p>DVNOAIPlugin: 
 * this plugin extends the standard "definable plugin" that comes with 
 * the LOCKSS daemon. It extends the OAI crawler functionality by 
 * adding a metadataPrefix parameter (thus allowing us to harvest records
 * in DDI format, instead/in addition to OAI_DC) and DVN-specific 
 * authentication/terms-of-use acknowledgement to harvest restricted content. 
 * </p>
 * @author Leonid Andreev
 * @version 1.0
 */

public class DVNOAIPlugin extends DefinablePlugin {
    private static String PLUGIN_NAME = "DVNOAI";
    private static String CURRENT_VERSION = "Pre-release";

    static final ConfigParamDescr PD_BASE = ConfigParamDescr.BASE_URL;

    static Logger log = Logger.getLogger("DVNOAIPlugin");

    // public only so test methods can use them
    public static final String AUPARAM_BASE_URL = PD_BASE.getKey();

    public void initPlugin(LockssDaemon daemon){

	// need to add code to read the definitions from 
	// the XML file, from here.
	
	String defMapFile = "edu/harvard/iq/dvn/lockss/plugin/Definitions.xml"; 

	log.debug ("reading additional configuration from " + defMapFile); 

	ExternalizableMap defMap = new ExternalizableMap();

	//mapName = "edu.harvard.hmdc.dvnplugin.Definitions";
	// No, we actually specifically WANT to leave mapName null,
	// otherwise the plugin manager will get confused and think 
	// this is an XML-defined plugin.

	if ( this.getClass().getClassLoader() == null ) { 
	    log.debug ("could not find non-empty class loader!"); 
	    mapName = null; 
	} else {
	    try {
		log.debug ("creating defMap with this.getClass().getClassLoader();"); 
		defMap.loadMapFromResource(defMapFile, this.getClass().getClassLoader());
		this.definitionMap = defMap;
	    } catch (FileNotFoundException e) {
		log.debug ("failed to load defMap from file!"); 
		mapName = null; 
	    }
	}

	definitionMap.putString(KEY_PLUGIN_NAME, PLUGIN_NAME);
	definitionMap.putString(KEY_PLUGIN_VERSION, CURRENT_VERSION);
	definitionMap.putString(KEY_CRAWL_TYPE, CRAWL_TYPE_OAI);

	//URL url = loader.getResource(defMapFile);
	//if (url != null) {
	//    loadedFrom = url.toString();
	//}
	//// then call the overridden initializaton.

	super.initPlugin(daemon);

	// not sure if this is necessary: 
	initMimeMap();
	// well, it's not necessary for our purposes now, but it 
	// would be nice to preserve all the existing functionality 
	// of the definable Plugin, including being able to read 
	// mime maps from the XML definition files. 
    }


    public ArchivalUnit createAu0(Configuration auConfig)
    	throws ArchivalUnit.ConfigurationException {
    	// create a new archival unit
    	ArchivalUnit au = new DVNOAIArchivalUnit(this, definitionMap);
    	// then configure it
    	au.setConfiguration(auConfig);
    	return au;
    }
}
