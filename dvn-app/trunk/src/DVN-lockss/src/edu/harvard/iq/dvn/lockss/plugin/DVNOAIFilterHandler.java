package edu.harvard.iq.dvn.lockss.plugin;

import java.io.PrintWriter; 
import org.xml.sax.SAXException; 
import org.xml.sax.helpers.DefaultHandler; 
import org.xml.sax.Attributes; 
import org.lockss.util.*;

// This filter does a super-minimalistic processing of the incoming
// XML stream stripping pretty much everything except for the tags and 
// attributes that we are specifically interested in. 
// 
// It ain't pretty but it works. 

public class DVNOAIFilterHandler extends DefaultHandler {

    private int printflag = 1;
    private int textprintflag = 1;

    private PrintWriter myout = null; 


    public DVNOAIFilterHandler (PrintWriter out) {
	myout = out; 
    }

    public void startDocument() throws SAXException {
	myout.println ("<?xml version='1.0'?>\n"); 
    }
    public void endDocument() throws SAXException {
	myout.flush(); 
	myout.close(); 
    }

    public void startElement (String uri, String localName,
			      String qName, Attributes attributes) throws SAXException {

	String attString = ""; 
	
	if (qName.equals("dataDscr") ||
	    qName.equals("docDscr") ||
	    qName.equals("stdyDscr")) {
	    printflag = 0; 
	} else {
	    if (qName.equals("metadata")) {
		textprintflag = 0; 
	    }


	    // These are the sections that contains the URLs; 
	    // the URLs are stored in the XML attribute "URI", so 
	    // it is the only attribute we need:
	    
	    if (qName.equals("fileDscr") || 
		qName.equals("otherMat")) {
		attString = attributes.getValue ("URI"); 
		attString = minimallyEncodeString(attString);
		if ( attString != null && attString.length() > 0 ) {
		    attString = " URI=\"" + attString + "\""; 
		}
	    }

	    // We want to preserve all the attributes in the <OAI-PMH> and
	    // <codeBook> and a few other xml tags; 
	    // these may be needed for processing:

	    if (qName.equals("OAI-PMH") ||
		qName.equals("error") ||
		qName.equals("request") ||
		qName.equals("resumptionToken") ||
		qName.equals("notes") ||
		qName.equals("codeBook")) {
		int i = attributes.getLength(); 

		for ( int j = 0; j < i; j++ ) {
		    String aQName = attributes.getQName (j); 
		    String aValue = attributes.getValue (j); 

		    attString =  attString + " " + aQName + "=\"" + aValue + "\"";
		    // We are also specifically interested in access restriction
		    // text inside the special <notes> tags: 

		    if (qName.equals("notes") &&
			aQName.equals("type") &&
			aValue.equals("LOCKSS:CRAWLING")) {
			textprintflag = 1;
		    }
		}
	    }

	    /*
		int i = attributes.getLength(); 

		for ( int j = 0; j < i; j++ ) {
		    String aQName = attributes.getQName (j); 
		    String aValue = attributes.getValue (j); 

		    if (aQName.matches(".*LOCKSS.*")) {
			attString =  attString + " " + aQName + "=\"" + aValue + "\""; 
		    }
		}
		}*/

	    if ( printflag != 0 ) {
		if ( attString != null && attString.length() > 0 ) {
		    myout.println("<" + qName + attString + ">"); 
		} else {
		    myout.print("<" + qName + ">"); 
		}
	    }
	}   
    }
	
    public void endElement (String uri, String localName,
			      String qName) throws SAXException {
	
	if ( printflag != 0 ) {
	    myout.println("</" + qName + ">"); 
	}

	if (qName.equals("dataDscr") ||
	    qName.equals("docDscr") ||
	    qName.equals("stdyDscr")) {
	    printflag = 1; 
	}

	if (qName.equals("metadata")) {
            textprintflag = 1;
        }

	if (qName.equals("notes")) {
            textprintflag = 0;
        }

    }
       
    public void characters(char buf[], int offset, int len) throws SAXException {
	String output = new String(buf, offset, len); 
	if ( printflag != 0 
	     && 
	     textprintflag != 0
	     &&
	     output.length() != 0) {
	    myout.print(output); 
	}
    }

    private static String minimallyEncodeString(String textString) {
        textString = StringUtil.replaceString(textString, "&", "&amp;");
        return textString;
    }

}
