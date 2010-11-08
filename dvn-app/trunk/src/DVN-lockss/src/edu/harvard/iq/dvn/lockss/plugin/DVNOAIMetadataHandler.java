/*

Copyright (c) 2000-2002 Board of Trustees of Leland Stanford Jr. University,
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

import org.lockss.oai.*;
import java.util.HashSet;
import java.util.Set;
import org.lockss.util.Logger;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.apache.xml.serialize.*;
import org.xml.sax.InputSource;

public class DVNOAIMetadataHandler extends BaseOaiMetadataHandler{

  protected static Logger logger = Logger.getLogger("DVNOAIMetadataHandler");

  /**
   * Constructor;
   *
   * @param metadataPrefix the metadata prefix string
   * @param metadataNamespaceUrl the url that describe the metadata namespace
   * @param urlContainerTagName the tag name where a url can be found
   */

  private boolean crawlRestricted = false;

  public DVNOAIMetadataHandler(String metadataPrefix,
			       String metadataNamespaceUrl,
			       String urlContainerTagName) {

      super (metadataPrefix, metadataNamespaceUrl, urlContainerTagName); 
  }
    

  /**
   * Collect Urls
   * overriding this method in order to be able to parse for URLs stored 
   * in more than 1 metadata tag and in tag attributes. 
   * e.g., in the DDI metadata, as used by the DVN, the URLs that our
   * LOCKSS daemon needs to be able to crawl are stored in the URI 
   * attributes of the "<fileDscr" and "<otherMat" XML tags. 
   */

    protected Set collectArticleUrls() {
	Set articleUrls = new HashSet();

	//tag and attribute names hard-coded, for now; -L.A.
	
	String[] tag_names = {
	    "fileDscr",
	    "otherMat",
	}; 

	String tag_attribute = "URI"; 

	logger.debug3("Processing " + metadataNodeList.getLength() + " metadata nodes");

	for(int i = 0; i < metadataNodeList.getLength(); i++) {
	    Node node = metadataNodeList.item(i);
	    if(node != null) {
		//logger.debug3("metadataNodeList ("+i+") = " + OaiHandler.displayXML(node) );
		logger.debug3("node (" + i + ")");

		for ( int n = 0; n < 2; n++ ) {
		    NodeList list =
			((Element)node).getElementsByTagNameNS(metadataNamespaceUrl, tag_names[n]);
		    if (list.getLength() > 0) {
			logger.debug3("Processing " + list.getLength() + " " + tag_names[n] + " child nodes");
			for (int j = 0; j < list.getLength(); j++) {
			    String str = null; 
			    logger.debug3("child node (" + j + ")");
 
			    Attr attr = (Attr)list.item(j).getAttributes().getNamedItem(tag_attribute);

			    if (attr != null) {
				str = attr.getNodeValue();
				logger.debug3("attr. value = " + str);
			    }

			    if ( str != null ) {

				boolean crawlThisURL = true; 

				// Unless we have reasons to believe that 
				// we are specifically  authorized to crawl
				// restricted content, let's check for the 
				// restriction flags in the metadata. If these 
				// files are restricted there's no need to even
				// try crawling them -- that will only result 
				// in 403s that will need to be retried, so 
				// we'll be better off just skipping them. 

				if (!crawlRestricted()) {
				    // custom access restriction tags are 
				    // stored in child element <notes> inside
				    // <fileDscr> or <otherMat> elements. 

				    Node thisnode = list.item(j);
				    NodeList notes =
					((Element)thisnode).getElementsByTagNameNS(metadataNamespaceUrl, "notes");
				    if (notes.getLength() > 0) {
					logger.debug("Processing " + notes.getLength() + " notes");
					for (int k = 0; k < notes.getLength(); k++) {
					    logger.debug3("note (" + k + ")");
 
					    Attr noteattr = (Attr)notes.item(k).getAttributes().getNamedItem("subject");
					    if (noteattr != null && "LOCKSS Permission".equals(attr.getNodeValue())) {
						logger.debug3("found LOCKSS Permission note."); 
						
						String notevalue = notes.item(k).getFirstChild().getNodeValue();
						if (notevalue != null && notevalue.equals("restricted")) {
						    crawlThisURL = false;
						}
					    }
					}
				    }
				}
				
				if (crawlThisURL) {
				    articleUrls.add(str);
				}
			    }
			}
		    } else {
			logger.debug("No XML elements with the tag name : "+tag_names[n]);
		    }
		}
	    } 	
	}
	return articleUrls;
    }

    public boolean crawlRestricted () {
	return crawlRestricted; 
    }

    public void setCrawlRestricted (boolean cr) {
	this.crawlRestricted = cr; 
    }
}
