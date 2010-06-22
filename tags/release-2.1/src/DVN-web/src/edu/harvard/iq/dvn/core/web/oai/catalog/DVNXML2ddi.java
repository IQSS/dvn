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
*The Original Code is XML2oai_dc.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/


package edu.harvard.iq.dvn.core.web.oai.catalog;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import java.util.Properties;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

/**
 * Convert native "item" to oai_dc. In this case, the native "item"
 * is assumed to already be formatted as an OAI <record> element,
 * with the possible exception that multiple metadataFormats may
 * be present in the <metadata> element. The "crosswalk", merely
 * involves pulling out the one that is requested.
 */
public class DVNXML2ddi extends Crosswalk implements java.io.Serializable  {
    private static final String elementName = "codeBook";
    private static final String elementStart = "<" + elementName;
    private static final String elementEnd = elementName + ">";
	
    /**
     * The constructor assigns the schemaLocation associated with this crosswalk. Since
     * the crosswalk is trivial in this case, no properties are utilized.
     *
     * @param properties properties that are needed to configure the crosswalk.
     */
    public DVNXML2ddi(Properties properties) {
//	super("http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
        super("http://www.icpsr.umich.edu/DDI http://www.icpsr.umich.edu/DDI/Version2-0.xsd");
    }

    /**
     * Can this nativeItem be represented in ddi format?
     * @param nativeItem a record in native format
     * @return true if ddi format is possible, false otherwise.
     */
    public boolean isAvailableFor(Object nativeItem) {
	String fullItem = (String)nativeItem;
	if ((fullItem.indexOf(elementStart)) >= 0)
	    return true;
	return false;
    }

    /**
     * Perform the actual crosswalk.
     *
     * @param nativeItem the native "item". In this case, it is
     * already formatted as an OAI <record> element, with the
     * possible exception that multiple metadataFormats are
     * present in the <metadata> element.
     * @return a String containing the XML to be stored within the <metadata> element.
     * @exception CannotDisseminateFormatException nativeItem doesn't support this format.
     */
    public String createMetadata(Object nativeItem)
	throws CannotDisseminateFormatException {
	String fullItem = (String)nativeItem;
//        Document fullItem = (Document) nativeItem;

        
	int startOffset = fullItem.indexOf(elementStart);
	if (startOffset == -1) {
	    throw new CannotDisseminateFormatException(getSchemaLocation());
	}
	int endOffset = fullItem.indexOf(elementEnd) + elementEnd.length();
	return fullItem.substring(startOffset, endOffset);
         
    }
}
