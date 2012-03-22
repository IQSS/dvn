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
package edu.harvard.iq.dvn.lockss.plugin;

import java.util.*;

import org.lockss.util.*;
import org.lockss.oai.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.NoSuchFieldException;
import java.io.IOException;
import org.xml.sax.SAXException;

public class DVNOAIHandler extends OaiHandler {

  protected static Logger logger = Logger.getLogger("DVNOAIHandler");
  private Set updatedUrls = new HashSet();
  protected String queryString;
  protected DVNOAIListRecords listRecords;
  private String baseUrl;
  private int retries;
//   private int maxRetries;
  private OaiRequestData oaiData;
  private String fromDate;
  private String untilDate;

  // the latest error is at the beginning of the list
  private LinkedList errList = new LinkedList();

  // the root node of all the oai records retrieved in this request
  private Set oaiRecords = new HashSet();

  public DVNOAIHandler(){
    retries = 0;
    oaiData = null;
    fromDate = null;
    untilDate = null;
    listRecords = null;
    queryString = null;
  }

  /**
   * Read varies things off the ListRecords
   * 1. check for error in creating ListRecords
   * 2. get all the information we need from the ListRecords
   * 3. create another ListRecords if there is a resumptionToken
   *
   * @param maxRetries number of times to retry the request if it fails due
   * to badResumptionToken
   */
  public void processResponse(int maxRetries){
    if (listRecords == null){
      logger.error("Make sure DVNOAIHandler.issueRequest() is executed");
      return;
    }
    if (maxRetries < 0){
      logger.warning("maxRetries is a negative number");
    }

    nextListRecords:
    while (listRecords != null) {

      //check if we have error
      NodeList errors = null;
      try {
	errors = listRecords.getErrors();
      } catch (TransformerException e) {
	logError("In calling getErrors", e);
      }
      if (errors != null && errors.getLength() > 0) {
	int length = errors.getLength();
	for (int i=0; i<length; ++i) {
	  Node item = errors.item(i);

	  //sample error code:
	  //<error code="badResumptionToken">More info about the error</error>
	  String errCode = ((Element)item).getAttribute("code");
	  String errMsg = errCode +  " : " +  item.getFirstChild().getNodeValue();

	  //create an error for logError
	  OaiResponseErrorException oaiErrEx = new OaiResponseErrorException(errMsg);

	  if (errCode != null && errCode.equals("noRecordsMatch")){ 
	      // this could be normal if there is no update
	      // from the repository.
	      // (hence we're NOT logging this as an error; just printing 
	      // a warning message!)
	      logger.warning("The combination of the values of the from, until, set "
                           +"and metadataPrefix arguments results in an empty list.");
	  } else if (errCode != null && errCode.equals("badResumptionToken")) {
	    if ( retries < maxRetries) {
	      logger.info("badResumptionToken error, re-issue a new oai request");
	      listRecords = issueRequest(oaiData, fromDate, untilDate);
	      retries++;
	      continue nextListRecords;
	    } else {
	      logger.warning("Exceeded maximum Oai Request retry times");
	      logError("badResumptionToken in Oai Response", oaiErrEx);
	    }
	  } else if (errCode != null && errCode.equals("badArgument")){
	    logError("badArgument in Oai Response, check the oai query string: "
		     + getOaiQueryString() , oaiErrEx);
	  } else if (errCode != null && errCode.equals("cannotDisseminateFormat")) {
	    logError("cannotDisseminateFormat in Oai Response, "
                     +"metadata format not supported by item or repositry", oaiErrEx);
	  } else if (errCode != null && errCode.equals("noSetHierarchy")) {
	    logError("The repository does not support sets.", oaiErrEx);
	  } else {
	    logError("Unexpected Error.", oaiErrEx);
	  }

	  //testing to see what is inside that error item
	  //	    logger.debug3("nodeToString");
	  //      logger.debug3(nodeToString(item));
	}

	logger.warning("Error record: " + listRecords.toString() );

	break; //apart from badResumptionToken, we cannot do much of other error case, thus just break
      }


      //see what is inside the lisRecord
//      logger.debug3("The content of listRecord : \n" + listRecords.toString() );
      //toString on ListRecords is currently not thread safe, so I commented this out
      //TSR 4/13/06

      //collect and store all the oai records
//       collectOaiRecords(listRecords); //XXX info collected is not being used now,
                                      //can turn off to increase performance

      //parser urls by some implementation of oaiMetadataHander
      NodeList metadataNodeList =
	listRecords.getDocument().getElementsByTagName("metadata");

      String metadataPrefix = oaiData.getMetadataPrefix();
      String oai_namespace  = oaiData.getMetadataNamespaceUrl();
      String oai_tagname  = oaiData.getUrlContainerTagName();

      // "Oai_dcHandler" has the DC format and "identifier" tag 
      // hard-coded. Commented out, replaced with more generic 
      // constructor call creating a new handler with the parameters
      // supplied in the OaiRequestData object. -- L.A.

      //OaiMetadataHandler metadataHandler = new Oai_dcHandler();

      OaiMetadataHandler metadataHandler = null; 

      // see if the oaiData object has its own metadataHandler 
      // already (an extended metadata handler object could be
      // defined by the plugin) -- L.A. 

      metadataHandler = oaiData.getMetadataHandler();
 
      // if not, we'll create a new one: -- L.A. 

      if (metadataHandler == null ) {
	  metadataHandler = new BaseOaiMetadataHandler ( 
							metadataPrefix,
							oai_namespace,
							oai_tagname
							); 
      }

      //apart from collecting urls, more actions might be done in the
      //metadata handler w.r.t. different metadata
      metadataHandler.setupAndExecute(metadataNodeList);

      //put all the collected urls to updatedUrls
      updatedUrls.addAll(metadataHandler.getArticleUrls());

      //see if all the records are include in the response by checking the presence of
      //resumptionToken. If there is more records, request them by resumptionToken
      try {
	String resumptionToken = listRecords.getResumptionToken();
	logger.debug3("resumptionToken: " + resumptionToken);
	if (resumptionToken == null || resumptionToken.length() == 0) {
	  break; //break out of the while loop as there is no more new url
	} else {
	  //XXX TODO: Before including a resumptionToken in the URL of a
	  // subsequent request,
	  // we must encode any special characters in it.
 	  listRecords = new DVNOAIListRecords(baseUrl, resumptionToken);
	}
      } catch (IOException ioe) {
	logError("In getting ResumptionToken and requesting new ListRecords",
	         ioe);
      } catch (NoSuchFieldException nsfe) {
	logError("In getting ResumptionToken and requesting new ListRecords",
	         nsfe);
      } catch (TransformerException tfe) {
	logError("In getting ResumptionToken and requesting new ListRecords",
	         tfe);
      } catch (SAXException saxe) {
	logError("In getting ResumptionToken and requesting new ListRecords",
	         saxe);
      } catch (ParserConfigurationException pce) {
	logError("In getting ResumptionToken and requesting new ListRecords",
	         pce);
      }

    } //loop until there is no resumptionToken
  }

  /**
   * By create a ListRecords, an Oai request is issued and the response is also
   * store in the ListRecords object.
   *
   * @param oaiData oaiRequestData object that stores oai related information from OaiCrawlSpec
   * @param fromDate date from when we want to query about
   * @param untilDate date until when we want to query about
   */
  public DVNOAIListRecords issueRequest(OaiRequestData oaiData,
				  String fromDate, String untilDate){

    // do not check if oaiData == null, it is taken care in OaiRequestData constructor
    if (fromDate == null) {
      throw new NullPointerException("Called with null fromDate");
    } else if (untilDate == null) {
      throw new NullPointerException("Called with null untilDate");
    }

    this.oaiData = oaiData;
    this.fromDate = fromDate;
    this.untilDate = untilDate;

    baseUrl = oaiData.getOaiRequestHandlerUrl();
    String setSpec = oaiData.getAuSetSpec();
    String metadataPrefix = oaiData.getMetadataPrefix();
    //    ListRecords listRecords = null;

    // the query string that send to OAI repository
    queryString = baseUrl + "?verb=ListRecords&from=" + fromDate + "&until=" +
      untilDate + "&metadataPrefix=" + metadataPrefix + "&set=" + setSpec;

    try {
      listRecords = new DVNOAIListRecords(baseUrl, fromDate, untilDate, setSpec,
				    metadataPrefix);
    } catch (IOException ioe) {
      logError("In issueOaiRequest calling new ListRecords", ioe);
    } catch (ParserConfigurationException pce) {
      logError("In issueOaiRequest calling new ListRecords", pce);
    } catch (SAXException saxe) {
      logError("In issueOaiRequest calling new ListRecords", saxe);
    } catch (TransformerException tfe) {
      logError("In issueOaiRequest calling new ListRecords", tfe);
    }

    return listRecords;
  }

  /**
   * log the error message and the corresponding exception
   *
   * XXX need to be rewritten to reflect errors in crawlStatus
   */
  protected void logError(String msg, Exception ex) {
    logger.siteError(msg, ex);
    errList.addFirst(ex);
  }

  /**
   * Method to check if there is any error occurs in construct request,
   * issue request, and parse response.
   * Note: the latest error is at the beginning of the list, like a stack
   *
   * @return the error list
   *
   * XXX need to be rewritten to reflect errors in crawlStatus
   */
  public List getErrors() {
    return errList;
  }

  /**
   * Get a list of Urls extracted from the OAI response
   *
   * @return the list of Urls extracted from Oai response
   */
  public Set getUpdatedUrls(){
    return updatedUrls;
  }

  /**
   * Stores <record>.......</record> element in the oai response in a list.
   * Information collected maybe useful to our future developement.
   *
   * //XXX do we want to have a place in the file system to store it ?
   * should it be output as an xml ? Methods to output the OaiRecords as
   * a file needed to be implemented.
   */
  private void collectOaiRecords(DVNOAIListRecords listRecords){
    NodeList nodeList = listRecords.getDocument().getElementsByTagName("record");
    for (int i=0; i<nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node != null) {
	oaiRecords.add(node);
	logger.debug3("Record ("+ i +") :" + displayXML(node));
      }
    }
  }

  /**
   * return all the <record> nodes in the oai response
   * each element in the Set is of class "org.w3c.dom.Node".
   *
   * @return the set of <record> nodes
   */
  public Set getOaiRecords(){
    return oaiRecords;
  }

  /**
   * Print out the OAI query string for error tracing
   */
  public String getOaiQueryString(){
    return queryString;
  }

  /**
   * print out the content of a node, its attribute and its children
   * it might be useful to put it in some kind of Util
   */
  String nodeToString(Node domNode){
    // An array of names for DOM node-types
    // (Array indexes = nodeType() values.)
    String[] typeName = {
      "none",
      "Element",
      "Attr",
      "Text",
      "CDATA",
      "EntityRef",
      "Entity",
      "ProcInstr",
      "Comment",
      "Document",
      "DocType",
      "DocFragment",
      "Notation",
    };

    String s = typeName[domNode.getNodeType()];
    String nodeName = domNode.getNodeName();
    if (! nodeName.startsWith("#")) {
      s += ": " + nodeName;
    }
    if (domNode.getNodeValue() != null) {
      if (s.startsWith("ProcInstr"))
	s += ", ";
      else
	s += ": ";
      // Trim the value to get rid of NL's at the front
      String t = domNode.getNodeValue().trim();
      int x = t.indexOf("\n");
      if (x >= 0) t = t.substring(0, x);
      s += t;
    }
    s += "\n";
    NamedNodeMap attrMap = domNode.getAttributes();
    if (attrMap != null){
      s += "Its attributes are : \n";
      for (int iy=0; iy <attrMap.getLength(); iy++){
	Attr attr = (Attr) attrMap.item(iy);
	s += attr.getName() + " = " + attr.getValue() + "\n";
      }
    }
    NodeList childList = domNode.getChildNodes();
    if (childList != null) {
      s += "Child nodes of "+ nodeName + ": \n";
      for (int ix=0; ix <childList.getLength(); ix++){
	s += nodeToString(childList.item(ix));
      }
    }
    return s;
  }

  //walk the DOM tree and print as u go
  static String displayXML(Node node){

    StringBuffer tmpStr = new StringBuffer();

        int type = node.getNodeType();
        switch(type)
        {
            case Node.DOCUMENT_NODE:
            {
              tmpStr.append("<?xml version=\"1.0\" encoding=\""+
                                "UTF-8" + "\"?>");
              break;
            }//end of document
            case Node.ELEMENT_NODE:
            {
                tmpStr.append('<' + node.getNodeName() );
                NamedNodeMap nnm = node.getAttributes();
                if(nnm != null )
                {
                    int len = nnm.getLength() ;
                    Attr attr;
                    for ( int i = 0; i < len; i++ )
                    {
                        attr = (Attr)nnm.item(i);
                        tmpStr.append(' '
                             + attr.getNodeName()
                             + "=\""
                             + attr.getNodeValue()
                             +  '"' );
                    }
                }
                tmpStr.append('>');

                break;

            }//end of element
            case Node.ENTITY_REFERENCE_NODE:
            {

               tmpStr.append('&' + node.getNodeName() + ';' );
               break;

            }//end of entity
            case Node.CDATA_SECTION_NODE:
            {
                    tmpStr.append( "<![CDATA["
                            + node.getNodeValue()
                            + "]]>" );
                     break;

            }
            case Node.TEXT_NODE:
            {
                tmpStr.append(node.getNodeValue());
                break;
            }
            case Node.PROCESSING_INSTRUCTION_NODE:
            {
                tmpStr.append("<?"
                    + node.getNodeName() ) ;
                String data = node.getNodeValue();
                if ( data != null && data.length() > 0 ) {
                    tmpStr.append(' ');
                    tmpStr.append(data);
                }
                tmpStr.append("?>");
                break;

             }
        }//end of switch


        //recurse
        for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
        {
            tmpStr.append(displayXML(child));
        }

        //without this the ending tags will miss
        if ( type == Node.ELEMENT_NODE )
        {
            tmpStr.append("</" + node.getNodeName() + ">");
        }

	return tmpStr.toString();

    }//end of displayXML

  /**

    public static class OaiResponseErrorException extends Exception {


    public OaiResponseErrorException(String errMsg) {
      super(errMsg);
    }

    public OaiResponseErrorException(){
      this("");
    }
  }
     */

}
