/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2009
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
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.spss;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.*;

import java.util.*;
import java.lang.reflect.*;
import java.util.regex.*;

import org.apache.commons.lang.builder.*;
import org.apache.commons.io.*;
import org.apache.commons.io.input.*;

import org.apache.commons.lang.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;
import edu.harvard.iq.dvn.unf.*;


import edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util.*;
import java.text.*;

import org.apache.commons.codec.binary.Hex;

/**
 * A DVN-Project-implementation of <code>StatDataFileReader</code> for the 
 * SPSS Control Card format.
 * 
 * @author Akio Sone, Leonid Andreev
 */
public class SPSSFileReader extends StatDataFileReader{
//public class SPSSFileReader extends StatDataFileReader {


    // static fields ---------------------------------------------------------//
    private static String[] FORMAT_NAMES = {"spss", "SPSS"};
    private static String[] EXTENSIONS = {"spss", "sps"};
    private static String[] MIME_TYPE = {"text/plain"};


    
    private static String SPSS_CONTROL_CARD_SIGNATURE = "$FL2";

    

    private static Logger dbgLog =
       Logger.getLogger(SPSSFileReader.class.getPackage().getName());

    SDIOMetadata smd = new SPSSMetadata();
    
    SDIOData sdiodata;

    DataTable savDataSection = new DataTable();


    /**
     * Constructs a <code>SPSSFileReader</code> instance with a
     * <code>StatDataFileReaderSpi</code> object.
     *
     * @param originator a <code>StatDataFileReaderSpi</code> object.
     */
    public SPSSFileReader(StatDataFileReaderSpi originator) {
        super(originator);
        //init();
    }

    // global variables -------------------------------------------------------//

    int caseQnty=0; 
    int varQnty=0;   

    private List<Integer> variableTypelList= new ArrayList<Integer>(); 

    Map<String, String> printFormatTable = new LinkedHashMap<String, String>(); 
    Map<String, String> printFormatNameTable = new LinkedHashMap<String, String>();     
    

    Map<String, String> valueVariableMappingTable = new LinkedHashMap<String, String>(); 

    List<String> variableNameList = new ArrayList<String>(); 

    int[] variableTypeFinal= null; 
    
    String[] variableFormatTypeList= null; 

    
    private void init(){
    }
    
 
    // Methods ---------------------------------------------------------------//
    /**
     * Read the given SPSS Control Card via a <code>BufferedInputStream</code>
     * object.  This method calls an appropriate method associated with the given 
     * field header by reflection.
     * 
     * @param stream a <code>BufferedInputStream</code>.
     * @return an <code>SDIOData</code> object
     * @throws java.io.IOException if an reading error occurs.
     */
    @Override
    public SDIOData read(BufferedInputStream stream) throws IOException {

        dbgLog.fine("***** SPSSFileReader: read() start *****");
	    
        if (sdiodata == null){
            sdiodata = new SDIOData(smd, savDataSection);
        }
        dbgLog.fine("***** SAVFileReader: read() end *****");
        return sdiodata;
    }
    

}
