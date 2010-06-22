/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/**
*Copyright (c) 2000-2002 OCLC Online Computer Library Center,
*Inc. and other contributors. All rights reserved.  The contents of this file, as updated
*from time to time by the OCLC Office of Research, are subject to OCLC Research
*Public License Version 2.0 (the "License"); you may not use this file except in
*compliance with the License. You may obtain a current copy of the License at
*http://purl.oclc.org/oclc/research/ORPL/.  Software distributed under the License is
*distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
*or implied. See the License for the specific language governing rights and limitations
*under the License.  This software consists of voluntary contributions made by many
*individuals on behalf of OCLC Research. For more information on OCLC Research,
*please see http://www.oclc.org/oclc/research/.
*
*The Original Code is XMLRecordFactory.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/

package edu.harvard.iq.dvn.core.web.oai.catalog;

import ORG.oclc.oai.server.catalog.RecordFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * XMLRecordFactory converts native XML "items" to "record" Strings.
 * This factory assumes the native XML item looks exactly like the <record>
 * element of an OAI GetRecord response, with the possible exception that the
 * <metadata> element contains multiple metadataFormats from which to choose.
 */
public class DVNXMLRecordFactory extends RecordFactory implements java.io.Serializable  {
    private static final String identifierStart = "<identifier>";
    private static final String identifierEnd = "</identifier>";
    private static final String datestampStart = "<datestamp>";
    private static final String datestampEnd = "</datestamp>";
    private static final String setSpecStart = "<setSpec>";
    private static final String setSpecEnd = "</setSpec>";
    private static final String aboutStart = "<about>";
    private static final String aboutEnd = "</about>";
    
    /**
     * Construct an XMLRecordFactory capable of producing the Crosswalk(s)
     * specified in the properties file.
     * @param properties Contains information to configure the factory:
     *                   specifically, the names of the crosswalk(s) supported
     * @exception IllegalArgumentException Something is wrong with the argument.
     */
    public DVNXMLRecordFactory(Properties properties)
	throws IllegalArgumentException {
	super(properties);
    }

    /**
     * Utility method to parse the 'local identifier' from the OAI identifier
     *
     * @param identifier OAI identifier (e.g. oai:oaicat.oclc.org:ID/12345)
     * @return local identifier (e.g. ID/12345).
     */
    public String fromOAIIdentifier(String identifier) {
	return identifier;
    }

    /**
     * Construct an OAI identifier from the native item
     *
     * @param nativeItem native Item object
     * @return OAI identifier
     */
    public String getOAIIdentifier(Object nativeItem) {
	String xmlRec = (String)nativeItem;
	int startOffset = xmlRec.indexOf(identifierStart);
	int endOffset = xmlRec.indexOf(identifierEnd);
	return xmlRec.substring(startOffset + identifierStart.length(), endOffset);
    }

    /**
     * get the datestamp from the item
     *
     * @param nativeItem a native item presumably containing a datestamp somewhere
     * @return a String containing the datestamp for the item
     * @exception IllegalArgumentException Something is wrong with the argument.
     */
    public String getDatestamp(Object nativeItem)
	throws IllegalArgumentException  {
	String xmlRec = (String)nativeItem;
	int startOffset = xmlRec.indexOf(datestampStart);
	int endOffset = xmlRec.indexOf(datestampEnd);
	return xmlRec.substring(startOffset + datestampStart.length(), endOffset);
    }

    /**
     * get the setspec from the item
     *
     * @param nativeItem a native item presumably containing a setspec somewhere
     * @return a String containing the setspec for the item
     * @exception IllegalArgumentException Something is wrong with the argument.
     */
    public Iterator getSetSpecs(Object nativeItem)
	throws IllegalArgumentException  {
	ArrayList list = new ArrayList();
	String xmlRec = (String)nativeItem;
	for (int startOffset = xmlRec.indexOf(setSpecStart);
	     startOffset >= 0;
	     startOffset = xmlRec.indexOf(setSpecStart, startOffset + 1)) {
	    int endOffset = xmlRec.indexOf(setSpecEnd, startOffset + 1);
	    list.add(xmlRec.substring(startOffset + setSpecStart.length(), endOffset));
	}
	return list.iterator();
    }

    /**
     * Get the about elements from the item
     *
     * @param nativeItem a native item presumably containing about information somewhere
     * @return a Iterator of Strings containing &lt;about&gt;s for the item
     * @exception IllegalArgumentException Something is wrong with the argument.
     */
    public Iterator getAbouts(Object nativeItem) throws IllegalArgumentException {
	ArrayList list = new ArrayList();
	String xmlRec = (String)nativeItem;
	for (int startOffset = xmlRec.indexOf(aboutStart);
	     startOffset >= 0;
	     startOffset = xmlRec.indexOf(aboutStart, startOffset + 1)) {
	    int endOffset = xmlRec.indexOf(aboutEnd, startOffset + 1);
	    list.add(xmlRec.substring(startOffset + aboutStart.length(), endOffset));
	}
	return list.iterator();
    }

    /**
     * Is the record deleted?
     *
     * @param nativeItem a native item presumably containing a possible delete indicator
     * @return true if record is deleted, false if not
     * @exception IllegalArgumentException Something is wrong with the argument.
     */
    public boolean isDeleted(Object nativeItem)
	throws IllegalArgumentException {
	String xmlRec = (String)nativeItem;
//	String xmlRec = (String)nativeItem;
//	return xmlRec.indexOf("<header status=\"deleted\"") != -1;
	return xmlRec.indexOf("<status>deleted</status>") != -1;
    }

    /**
     * Allows classes that implement RecordFactory to override the default create() method.
     * This is useful, for example, if the entire &lt;record&gt; is already packaged as the native
     * record. Return null if you want the default handler to create it by calling the methods
     * above individually.
     * 
     * @param nativeItem the native record
     * @param schemaURL the schemaURL desired for the response
     * @param the metadataPrefix from the request
     * @return a String containing the OAI &lt;record&gt; or null if the default method should be
     * used.
     */
    public String quickCreate(Object nativeItem, String schemaLocation, String metadataPrefix) {
	// Don't perform quick creates
	return null;
    }
}
