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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.sav;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.*;

import static java.lang.System.*;
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
 * SPSS System file (SAV) format.
 * 
 * @author Akio Sone at UNC-Odum
 */
public class SAVFileReader extends StatDataFileReader{

    // static fields ---------------------------------------------------------//
    private static String[] FORMAT_NAMES = {"sav", "SAV"};
    private static String[] EXTENSIONS = {"sav"};
    private static String[] MIME_TYPE = {"application/x-spss-sav"};


    // block length
    // RecordType 1
    
    private static final int LENGTH_SAV_INT_BLOCK = 4;
    // note: OBS block is either double or String, not Integer
    private static final int LENGTH_SAV_OBS_BLOCK = 8;
    
    private static final int SAV_MAGIC_NUMBER_LENGTH = LENGTH_SAV_INT_BLOCK;
    
    private static String SAV_FILE_SIGNATURE = "$FL2";

    
    
    // Record Type 1 fields
    private static final int LENGTH_RECORDTYPE1 = 172;
    
    private static final int LENGTH_SPSS_PRODUCT_INFO = 60;
    
    private static final int FILE_LAYOUT_CONSTANT = 2;
    
    private static final int LENGTH_FILE_LAYOUT_CODE =  LENGTH_SAV_INT_BLOCK;
    
    private static final int LENGTH_NUMBER_OF_OBS_UNITS_PER_CASE = LENGTH_SAV_INT_BLOCK;
    
    private static final int LENGTH_COMPRESSION_SWITCH = LENGTH_SAV_INT_BLOCK;
    
    private static final int LENGTH_CASE_WEIGHT_VARIABLE_INDEX = LENGTH_SAV_INT_BLOCK;
    
    private static final int LENGTH_NUMBER_OF_CASES =   LENGTH_SAV_INT_BLOCK;
    
    private static final int LENGTH_COMPRESSION_BIAS =  LENGTH_SAV_OBS_BLOCK;
    
    private static final int LENGTH_FILE_CREATION_INFO = 84;
    
    private static final int length_file_creation_date = 9;
    private static final int length_file_creation_time = 8;
    private static final int length_file_creation_label= 64;
    private static final int length_file_creation_padding = 3;
    
    // Recorde Type 2
    
    private static final int LENGTH_RECORDTYPE2_FIXED = 32;
    private static final int LENGTH_RECORD_TYPE2_CODE = 4;
    private static final int LENGTH_TYPE_CODE = 4;
    private static final int LENGTH_LABEL_FOLLOWS = 4;
    private static final int LENGTH_MISS_VALUE_FORMAT_CODE= 4;
    private static final int LENGTH_PRINT_FORMAT_CODE = 4;;
    private static final int LENGTH_WRITE_FORMAT_CODE = 4;
    private static final int LENGTH_VARIABLE_NAME =  8;
    private static final int LENGTH_VARIABLE_LABEL= 4;

    private static final int LENGTH_MISS_VAL_OBS_CODE = LENGTH_SAV_OBS_BLOCK;
    
    // Record Type 3/4
    private static final int LENGTH_RECORDTYPE3_HEADER_CODE = 4;
    private static final int LENGTH_RECORD_TYPE3_CODE = 4;
    private static final int LENGTH_RT3_HOW_MANY_LABELS = 4;
    private static final int LENGTH_RT3_VALUE  = LENGTH_SAV_OBS_BLOCK;
    private static final int LENGTH_RT3_LABEL_LENGTH =1;
    
    private static final int LENGTH_RECORD_TYPE4_CODE =      4;
    private static final int LENGTH_RT4_HOW_MANY_VARIABLES = 4;
    private static final int LENGTH_RT4_VARIABLE_INDEX =     4;
    
    // Record Type 6
    private static final int LENGTH_RECORD_TYPE6_CODE =  4;
    private static final int LENGTH_RT6_HOW_MANY_LINES = 4;
    private static final int LENGTH_RT6_DOCUMENT_LINE = 80;
    
    // Record Type 7
    private static final int LENGTH_RECORD_TYPE7_CODE =  4;
    private static final int LENGTH_RT7_SUB_TYPE_CODE =  4;

    // Record Type 999
    private static final int LENGTH_RECORD_TYPE999_CODE =  4;
    private static final int LENGTH_RT999_FILLER        =  4;

    
    private static final List<String> RecordType7SubType4Fields= new ArrayList<String>();
    private static final Set<Integer> validMissingValueCodeSet = new HashSet<Integer>();
    private static final Map<Integer, Integer> missingValueCodeUnits = new HashMap<Integer, Integer>();

    
    /** List of decoding methods for reflection */
    private static String[] decodeMethodNames = {
        "decodeHeader","decodeRecordType1","decodeRecordType2",
        "decodeRecordType3and4","decodeRecordType6","decodeRecordType7",
        "decodeRecordType999", "decodeRecordTypeData" };


    private static List<Method> decodeMethods  = new ArrayList<Method>();

    private static String unfVersionNumber = "3";

    private static double SYSMIS_LITTLE =0xFFFFFFFFFFFFEFFFL;
    private static double SYSMIS_BIG =0xFFEFFFFFFFFFFFFFL;
    
    private static Calendar GCO = new GregorianCalendar();

    static {
        // initialize method name list
        for (String n: decodeMethodNames){
            for (Method m: SAVFileReader.class.getDeclaredMethods()){
                if (m.getName().equals(n)){
                    decodeMethods.add(m);
                }
            }
        }
        
        // initialize validMissingValueCodeSet
        validMissingValueCodeSet.add(3);
        validMissingValueCodeSet.add(2);
        validMissingValueCodeSet.add(1);
        validMissingValueCodeSet.add(0);
        validMissingValueCodeSet.add(-2);
        validMissingValueCodeSet.add(-3);
        
        // initialize missingValueCodeUnits
        
        missingValueCodeUnits.put(1, 1);
        missingValueCodeUnits.put(2, 2);
        missingValueCodeUnits.put(3, 3);
        missingValueCodeUnits.put(-2,2);
        missingValueCodeUnits.put(-3, 3);
        missingValueCodeUnits.put(0, 0);

        RecordType7SubType4Fields.add("SYSMIS");
        RecordType7SubType4Fields.add("HIGHEST");
        RecordType7SubType4Fields.add("LOWEST");
        
        // set the origin of GCO to 1582-10-15
        GCO.set(1, 1582);// year
        GCO.set(2, 9); // month
        GCO.set(5, 15);// day of month
        GCO.set(9, 0);// AM(0) or PM(1)
        GCO.set(10, 0);// hh
        GCO.set(12, 0);// mm
        GCO.set(13, 0);// ss
        GCO.set(14, 0); // SS millisecond
        GCO.set(15, 0);// z
    }
    private static long SPSS_DATE_BIAS = 60*60*24*1000;

    private static long SPSS_DATE_OFFSET = SPSS_DATE_BIAS + Math.abs(GCO.getTimeInMillis());

   // instance fields -------------------------------------------------------//

   private static Logger dbgLog =
       Logger.getLogger(SAVFileReader.class.getPackage().getName());

    SDIOMetadata smd = new SAVMetadata();
    

    SDIOData sdiodata;

    DataTable savDataSection = new DataTable();

//    int release_number;
//    int header_length;
//    int data_label_length;

    
    double missing_value_double;

    boolean isLittleEndian = false;
    
    boolean isDataSectionCompressed = true;

    boolean hasCaseWeightVariable = false;
    
    int caseWeightVariableOBSIndex = 0;
    
    String caseWeightVariableName = null;
    
    int caseWeightVariableIndex = 0;
    
    Map<Integer, String> OBSIndexToVariableName =
        new LinkedHashMap<Integer, String>();
    
    int OBSUnitsPerCase;
    
//    int bytes_per_row;
    //boolean[] variableLocation ;

    Map<String, Integer> variableTypeTable;

    List<Integer> variableTypelList= new ArrayList<Integer>();
    List<Integer> OBSwiseTypelList= new ArrayList<Integer>();

    List<Integer> printFormatList = new ArrayList<Integer>();
    Map<String, String> printFormatTable = new LinkedHashMap<String, String>();
    Map<String, String> printFormatNameTable = new LinkedHashMap<String, String>();
    
    Map<String, String> formatCategoryTable = new LinkedHashMap<String, String>();
    

    //Map<String, Integer> StringVariableTable = new LinkedHashMap<String, Integer>();
    Set<Integer> obsStringVariableSet = new LinkedHashSet<Integer>();
    Set<Integer> obsNonVariableBlockSet = new LinkedHashSet<Integer>();
    Set<Integer> obsVariableBlockSet = new LinkedHashSet<Integer>();
//    int value_label_table_length;
    
    Map<String, Map<String, String>> valueLabelTable =
            new LinkedHashMap<String, Map<String, String>>();
    Map<String, String> valueVariableMappingTable = new LinkedHashMap<String, String>();

    String[] unfValues = null;
    
    String fileUnfValue = null;

    List<String> variableNameList = new ArrayList<String>();
    List<String> variableLabelList = new ArrayList<String>();

    Map<String, String> variableLabelMap = new LinkedHashMap<String, String>();
    
    Map<String, List<String>> missingValueTable = new LinkedHashMap<String, List<String>>();

    Map<String, InvalidData> invalidDataTable = new LinkedHashMap<String, InvalidData>();
    
    int caseQnty=0;
    
    int varQnty=0;

    Double systemMissingValue =Double.NaN;
    
//    String NA_String = "NA";
    
    String StringMissingValue =" ";
    String NumericMissingValue=".";


    Map<String, String> OBStypeIfomation = new LinkedHashMap<String, String>();
    //String[] OBStypes = { "SYSMIS", "HIGHEST", "LOWEST"};
    
    NumberFormat doubleNumberFormatter = new DecimalFormat();


    Set<Integer> decimalVariableSet = new HashSet<Integer>();

    int[] variableTypeFinal= null;
    
    String[] variableFormatTypeList= null;

    List<Integer> formatDecimalPointPositionList= new ArrayList<Integer>();

    
    Object[][] dataTable2 = null;


    // RecordType 7 
    // Subtype 3
    List<Integer> releaseMachineSpecificInfo = new ArrayList<Integer>();
    List<String> releaseMachineSpecificInfoHex = new ArrayList<String>();
    
    // Subytpe 4
    Map<String, Double> OBSTypeValue = new LinkedHashMap<String, Double>();
    Map<String, String> OBSTypeHexValue = new LinkedHashMap<String, String>();    
    //Subtype 11
    List<Integer> measurementLevel = new ArrayList<Integer>();
    List<Integer> columnWidth = new ArrayList<Integer>();
    List<Integer> alignment = new ArrayList<Integer>();

    Map<String, String> shortToLongVarialbeNameTable = new LinkedHashMap<String, String>();

    // date/time data format
    SimpleDateFormat sdf_ymd    = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf_ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf_dhms   = new SimpleDateFormat("DDD HH:mm:ss");
    SimpleDateFormat sdf_hms    = new SimpleDateFormat("HH:mm:ss");

    /**
     * The <code>String</code> that represents the numeric missing value 
     * for a tab-delimited data file, initially "NA" 
     * after R's missing value.
     */
    String MissingValueForTextDataFileNumeric = "NA";

    /**
     * Returns the value of the
     * <code>MissingValueForTextDataFileNumeric</code> field.
     * 
     * @return the value of the
     * <code>MissingValueForTextDataFileNumeric</code> field.
     */
    public String getMissingValueForTextDataFileNumeric() {
        return MissingValueForTextDataFileNumeric;
    }

    /**
     * Sets the new value of 
     * the <code>setMissingValueForTextDataFileNumeric</code> field.
     * 
     * @param MissingValueToken the new value of the
     * <code>setMissingValueForTextDataFileNumeric</code> field.
     */
    public void setMissingValueForTextDataFileNumeric(String MissingValueToken) {
        this.MissingValueForTextDataFileNumeric = MissingValueToken;
    }


    /**
     * The <code>String</code> that represents the string missing value 
     * for a tab-delimited data file, initially "".
     */
    String MissingValueForTextDataFileString = "";

    /**
     * Returns the value of the
     * <code>MissingValueForTextDataFileString</code> field.
     * 
     * @return the value of the
     * <code>MissingValueForTextDataFileString</code> field.     */
    public String getMissingValueForTextDataFileString() {
        return MissingValueForTextDataFileString;
    }

    /**
     * Sets the new value of 
     * the <code>MissingValueForTextDataFileString</code> field.
     * 
     * @param MissingValueToken the new value of the
     * <code>MissingValueForTextDataFileString</code> field.
     */
    public void setMissingValueForTextDataFileString(String MissingValueToken) {
        this.MissingValueForTextDataFileString = MissingValueToken;
    }


    // Constructor -----------------------------------------------------------//


    /**
     * Constructs a <code>SAVFileReader</code> instance with a 
     * <code>StatDataFileReaderSpi</code> object.
     * 
     * @param originator a <code>StatDataFileReaderSpi</code> object.
     */
    public SAVFileReader(StatDataFileReaderSpi originator) {
        super(originator);
        //init();
    }
    
    private void init(){
        sdf_ymd.setTimeZone(TimeZone.getTimeZone("GMT"));
        sdf_ymdhms.setTimeZone(TimeZone.getTimeZone("GMT"));
        sdf_dhms.setTimeZone(TimeZone.getTimeZone("GMT"));
        sdf_hms.setTimeZone(TimeZone.getTimeZone("GMT"));
        


        
        doubleNumberFormatter.setGroupingUsed(false);
        doubleNumberFormatter.setMaximumFractionDigits(16);
    }
    
    // Accessor Methods  ----------------------------------------------------//
    
    

 
 
    // Methods ---------------------------------------------------------------//
    /**
     * Read the given SPSS SAV-format file via a <code>BufferedInputStream</code>
     * object.  This method calls an appropriate method associated with the given 
     * field header by reflection.
     * 
     * @param stream a <code>BufferedInputStream</code>.
     * @return an <code>SDIOData</code> object
     * @throws java.io.IOException if an reading error occurs.
     */
    @Override
    public SDIOData read(BufferedInputStream stream) throws IOException{

        dbgLog.info("***** SAVFileReader: read() start *****");

        for (Method mthd : decodeMethods){

            try {
                // invoke this method

                dbgLog.fine("method Name="+mthd.getName());

                mthd.invoke(this, stream);

            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                err.format(cause.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            smd.setValueLabelTable(valueLabelTable);
            smd.setVariableFormat(printFormatList);
            smd.setVariableFormatName(printFormatNameTable);
            smd.setVariableFormatCategory(formatCategoryTable);
        }
        if (sdiodata == null){
            sdiodata = new SDIOData(smd, savDataSection);
        }
        dbgLog.info("***** SAVFileReader: read() end *****");
        return sdiodata;

    }
    
    
    void decodeHeader(BufferedInputStream stream){
        dbgLog.fine("***** decodeHeader(): start *****");
        
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        // the length of the magic number is 4 (1-byte character * 4)
        // its value is expected to be $FL2

        byte[] b = new byte[SAV_MAGIC_NUMBER_LENGTH];
        
        try {
            if (stream.markSupported()){
                stream.mark(100);
            }
            int nbytes = stream.read(b, 0, SAV_MAGIC_NUMBER_LENGTH);

            if (nbytes == 0){
                throw new IOException();
            }

        } catch (IOException ex){
            ex.printStackTrace();
        }

        //printHexDump(b, "hex dump of the byte-array");

        String hdr4sav = new String(b);
        dbgLog.fine("from string=" + hdr4sav);

        if (hdr4sav.equals(SAV_FILE_SIGNATURE)) {
            dbgLog.fine("this file is spss-sav type");
            // initialize version-specific parameter
            init();
            
            smd.getFileInformation().put("mimeType", MIME_TYPE[0]);
            smd.getFileInformation().put("fileFormat", MIME_TYPE[0]);
            
        } else {
            dbgLog.fine("this file is NOT spss-sav type");

            throw new IllegalArgumentException("given file is not spss-sav type");
        }

        dbgLog.fine("smd dump:"+smd.toString());
        dbgLog.fine("***** decodeHeader(): end *****");

    }



    void decodeRecordType1(BufferedInputStream stream){
        dbgLog.fine("***** decodeRecordType1(): start *****");
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        // how to read each recordType
        // 1. set-up the following objects before reading bytes
        // a. the working byte array
        // b. the storage object
        // the length of this field: 172bytes = 60 + 4 + 12 + 4 + 8 + 84
        // this field consists of 6 distinct blocks
        
        byte[] recordType1 = new byte[LENGTH_RECORDTYPE1];
        
        try {
            int nbytes = stream.read(recordType1, 0, LENGTH_RECORDTYPE1);
            
            
            //printHexDump(recordType1, "recordType1");
            
            if (nbytes == 0){
                throw new IOException("reading recordType1: no byte was read");
            }
            
            // 1.1 60 byte-String that tells the platform/version of SPSS that
            // wrote this file
            
            int offset_start = 0;
            int offset_end = LENGTH_SPSS_PRODUCT_INFO; // 60 bytes
            
            String productInfo = new String(Arrays.copyOfRange(recordType1, offset_start,
                offset_end),"US-ASCII");
                
            dbgLog.fine("productInfo:\n"+productInfo+"\n");
            
            // add the info to the fileInfo
            smd.getFileInformation().put("productInfo", productInfo);
            
            
            // 1.2) 4-byte file-layout-code (byte-order)
            
            offset_start = offset_end;
            offset_end += LENGTH_FILE_LAYOUT_CODE; // 4 byte
            
            ByteBuffer bb_fileLayout_code  = ByteBuffer.wrap(
                    recordType1, offset_start, LENGTH_FILE_LAYOUT_CODE);
            
            ByteBuffer byteOderTest = bb_fileLayout_code.duplicate();
            // interprete the 4 byte as int

            int int2test = byteOderTest.getInt();
            
            if (int2test == 2 ){
                dbgLog.info("integer == 2: the byte-oder of the writer is the same "+
                "as the counterpart of Java: Big Endian");
            } else {
                // Because Java's byte-order is always big endian, 
                // this(!=2) means this sav file was  written on a little-endian machine
                // non-string, multi-bytes blocks must be byte-reversed

                bb_fileLayout_code.order(ByteOrder.LITTLE_ENDIAN);

                if (bb_fileLayout_code.getInt()==2){
                    dbgLog.info("The sav file was saved on a little endian machine");
                    dbgLog.info("Reveral of the bytes is necessary to decode "+
                            "multi-byte, non-string blocks");
                            
                    isLittleEndian = true;
                    
                } else {
                    throw new IOException("reading recordType1:unknown file layout code="+int2test);
                }
            }

            dbgLog.fine("Endian of this platform:"+ByteOrder.nativeOrder().toString());
            
            smd.getFileInformation().put("OSByteOrder", ByteOrder.nativeOrder().toString());
            smd.getFileInformation().put("byteOrder", int2test);



            // 1.3 4-byte Number_Of_OBS_Units_Per_Case 
            // (= how many RT2 records => how many varilables)
            
            offset_start = offset_end;
            offset_end += LENGTH_NUMBER_OF_OBS_UNITS_PER_CASE; // 4 byte
            
            ByteBuffer bb_OBS_units_per_case  = ByteBuffer.wrap( 
                    recordType1, offset_start,LENGTH_NUMBER_OF_OBS_UNITS_PER_CASE);
            
            if (isLittleEndian){
                bb_OBS_units_per_case.order(ByteOrder.LITTLE_ENDIAN);
            }
            
            
            OBSUnitsPerCase = bb_OBS_units_per_case.getInt();
            
            dbgLog.fine("OBSUnitsPerCase="+OBSUnitsPerCase);
            
            smd.getFileInformation().put("OBSUnitsPerCase", OBSUnitsPerCase);


            // 1.4 4-byte Compression_Switch
            
            offset_start = offset_end;
            offset_end += LENGTH_COMPRESSION_SWITCH; // 4 byte
            
            ByteBuffer bb_compression_switch  = ByteBuffer.wrap(recordType1, 
                    offset_start, LENGTH_COMPRESSION_SWITCH);
            
            if (isLittleEndian){
                bb_compression_switch.order(ByteOrder.LITTLE_ENDIAN);
            }
            
            int compression_switch = bb_compression_switch.getInt();
            if ( compression_switch == 0){
                // data section is not compressed
                isDataSectionCompressed = false;
                dbgLog.info("data section is not compressed");
            } else {
                dbgLog.info("data section is compressed:"+compression_switch);
            }
            
            smd.getFileInformation().put("compressedData", compression_switch);

            // 1.5 4-byte Case-Weight Variable Index
            // warning: this variable index starts from 1, not 0
            
            offset_start = offset_end;
            offset_end += LENGTH_CASE_WEIGHT_VARIABLE_INDEX; // 4 byte
            
            ByteBuffer bb_Case_Weight_Variable_Index = ByteBuffer.wrap(recordType1, 
                    offset_start, LENGTH_CASE_WEIGHT_VARIABLE_INDEX);
            
            if (isLittleEndian){
                bb_Case_Weight_Variable_Index.order(ByteOrder.LITTLE_ENDIAN);
            }
            
            caseWeightVariableOBSIndex = bb_Case_Weight_Variable_Index.getInt();
            
            
            if ( caseWeightVariableOBSIndex == 0){
                // no weight variable
                hasCaseWeightVariable = false;
                dbgLog.info("caseWeightVariableIndex is not specified [none-weighted data]");
            } else {
                dbgLog.info("caseWeightVariableIndex is specified="+caseWeightVariableOBSIndex);
                hasCaseWeightVariable = true;
            }
            
            smd.getFileInformation().put("caseWeightVariableOBSIndex", caseWeightVariableOBSIndex);

            // 1.6 4-byte Number of Cases

            offset_start = offset_end;
            offset_end += LENGTH_NUMBER_OF_CASES; // 4 byte
            
            ByteBuffer bb_Number_Of_Cases = ByteBuffer.wrap(recordType1, 
                    offset_start, LENGTH_NUMBER_OF_CASES);
            
            if (isLittleEndian){
                bb_Number_Of_Cases.order(ByteOrder.LITTLE_ENDIAN);
            }
            
            int numberOfCases = bb_Number_Of_Cases.getInt();
            
            if ( numberOfCases < 0){
                // -1 if numberOfCases is unknown
                dbgLog.info("number of cases is not recoded in the header");
            } else {
                dbgLog.info("number of cases is recorded= "+numberOfCases);
                caseQnty = numberOfCases;
                smd.getFileInformation().put("caseQnty", numberOfCases);
            }

            // 1.7 8-byte compression-bias [not long but double]
            
            offset_start = offset_end;
            offset_end += LENGTH_COMPRESSION_BIAS; // 8 byte
            
            ByteBuffer bb_compression_bias = ByteBuffer.wrap( 
                    Arrays.copyOfRange(recordType1, offset_start,
                offset_end));

            if (isLittleEndian){
               bb_compression_bias.order(ByteOrder.LITTLE_ENDIAN);
            }

            Double compressionBias = bb_compression_bias.getDouble();
            
            if ( compressionBias == 100d){
                // 100 is expected
                dbgLog.info("compressionBias is 100 as expected");
                smd.getFileInformation().put("compressionBias", 100);
            } else {
                dbgLog.info("compression bias is not 100: "+ compressionBias);
                smd.getFileInformation().put("compressionBias", compressionBias);
            }
            
            
            // 1.8 84-byte File Creation Information (date/time: dd MM yyhh:mm:ss +
            // 64-bytelabel)
            
            offset_start    = offset_end;
            offset_end += LENGTH_FILE_CREATION_INFO; // 84 bytes
            
            String fileCreationInfo = getNullStrippedString(new String(Arrays.copyOfRange(recordType1, offset_start,
                offset_end),"US-ASCII"));
                
            dbgLog.fine("fileCreationInfo:\n"+fileCreationInfo+"\n");
            
            String fileCreationDate = fileCreationInfo.substring(0,length_file_creation_date);
            int dateEnd = length_file_creation_date+length_file_creation_time;
            String fileCreationTime = fileCreationInfo.substring(length_file_creation_date,
                    (dateEnd));
            String fileCreationNote = fileCreationInfo.substring(dateEnd,length_file_creation_label);


            dbgLog.fine("fileDate="+ fileCreationDate);
            dbgLog.fine("fileTime="+ fileCreationTime);
            dbgLog.fine("fileNote"+ fileCreationNote);
            
            smd.getFileInformation().put("fileDate", fileCreationDate);
            smd.getFileInformation().put("fileTime", fileCreationTime);
            smd.getFileInformation().put("fileNote", fileCreationNote);
            smd.getFileInformation().put("varFormat_schema", "SPSS");
            
            // add the info to the fileInfo
            
            smd.getFileInformation().put("mimeType", MIME_TYPE[0]);
            smd.getFileInformation().put("fileFormat", MIME_TYPE[0]);
            
            smd.setValueLabelMappingTable(valueVariableMappingTable);
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        dbgLog.fine("***** decodeRecordType1(): end *****");
    }
    
    
    void decodeRecordType2(BufferedInputStream stream){
        dbgLog.fine("***** decodeRecordType2(): start *****");
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        // this field repeats as many as the number of variables in 
        // this sav file
        // Each field constists of a fixed (36-byte) segment and
        // variable one (string <=256 + 3 missing-value units[optional])
        int variableCounter = 0;

        for (int j= 0; j< OBSUnitsPerCase;j++){
            dbgLog.fine("\n\n+++++++++++ "+j+"-th RT2 unit is to be decoded +++++++++++");
            // 2.0: read the fixed[=non-optional] 32-byte segment
            byte[] recordType2Fixed = new byte[LENGTH_RECORDTYPE2_FIXED];

            try {
                int nbytes = stream.read(recordType2Fixed, 0, LENGTH_RECORDTYPE2_FIXED);


                //printHexDump(recordType2Fixed, "recordType2 part 1");

                if (nbytes == 0){
                    throw new IOException("reading recordType2: no byte was read");
                }

                int offset = 0;

            // 2.1: create int-view of the bytebuffer for the first 16-byte segment
                int rt2_1st_4_units = 4;
                ByteBuffer[] bb_record_type2_fixed_part1 = new ByteBuffer[rt2_1st_4_units];
                int[] recordType2FixedPart1 = new int[rt2_1st_4_units];
                for (int i= 0; i < rt2_1st_4_units;i++ ){

                    bb_record_type2_fixed_part1[i]  =
                            ByteBuffer.wrap(recordType2Fixed, offset,
                            LENGTH_SAV_INT_BLOCK);

                    offset +=LENGTH_SAV_INT_BLOCK;
                    if (isLittleEndian){
                       bb_record_type2_fixed_part1[i].order(ByteOrder.LITTLE_ENDIAN);
                    }
                    recordType2FixedPart1[i] = bb_record_type2_fixed_part1[i].getInt();
                }


                dbgLog.fine("recordType2FixedPart="+
                    ReflectionToStringBuilder.toString(recordType2FixedPart1,
                    ToStringStyle.MULTI_LINE_STYLE));


                // 1st ([0]) element must be 2 otherwise no longer Record Type 2
                if (recordType2FixedPart1[0] != 2){
                    dbgLog.fine(j+"-th RT header value="+recordType2FixedPart1[0]);
                    break;
                    //throw new IOException("RT2 reading error: The current position is no longer Record Type 2");
                }
                dbgLog.fine("variable type[must be 2]="+recordType2FixedPart1[0]);


                // 2nd ([1]) element: numeric variable = 0 :for string variable 
                // this block indicates its datum-length, i.e, >0 ;
                // if -1, this RT2 unit is a non-1st RT2 unit for a string variable
                // whose value contains more than 8-character long

                boolean isNumericVariable = false;

                dbgLog.fine("variable type(0: numeric; > 0: String;-1 continue )="+
                    recordType2FixedPart1[1]);

                OBSwiseTypelList.add(recordType2FixedPart1[1]);

                int HowManyRt2Units=1;


                if (recordType2FixedPart1[1] == -1) {
                    dbgLog.fine("this RT2 is contiguous one of a previous string variable");
                    obsNonVariableBlockSet.add(j);
                    continue;
                } else if (recordType2FixedPart1[1] == 0){
                     variableCounter++;
                     isNumericVariable = true;
                     variableTypelList.add(recordType2FixedPart1[1]);
                     obsVariableBlockSet.add(j);
                } else if (recordType2FixedPart1[1] > 0){
                
                    variableCounter++;
                    obsVariableBlockSet.add(j);
                    obsStringVariableSet.add(j);
                    if (recordType2FixedPart1[1] % LENGTH_SAV_OBS_BLOCK == 0){
                        HowManyRt2Units = recordType2FixedPart1[1] / LENGTH_SAV_OBS_BLOCK;
                    } else {
                         HowManyRt2Units = recordType2FixedPart1[1] / LENGTH_SAV_OBS_BLOCK +1;
                    }
                    variableTypelList.add(recordType2FixedPart1[1]);
                }
                dbgLog.fine("HowManyRt2Units for this variable="+HowManyRt2Units);


                // 3rd ([2]) element: = 1 variable-label block follows; 0 = no label
                //
                dbgLog.fine("variable label follows?(1:yes; 0: no)="+recordType2FixedPart1[2]);
                boolean hasVariableLabel = recordType2FixedPart1[2] == 1 ? true :
                    false;
                if ((recordType2FixedPart1[2] != 0) && (recordType2FixedPart1[2] != 1)) {
                    throw new IOException("RT2: reading error: value is neither 0 or 1"+
                        recordType2FixedPart1[2]);
                }

                // 4th ([3]) element: Missing value type code
                // 0[none], 1, 2, 3 [point-type],-2[range], -3 [range type+ point]

                dbgLog.fine("missing value unit follows?(if 0, none)="+recordType2FixedPart1[3]);
                boolean hasMissingValues = (validMissingValueCodeSet.contains(
                    recordType2FixedPart1[3]) && (recordType2FixedPart1[3] !=0)) ?
                        true : false;
                InvalidData invalidDataInfo = null;
                if (recordType2FixedPart1[3] !=0){
                   invalidDataInfo = new InvalidData(recordType2FixedPart1[3]);
                   dbgLog.fine("missing value type="+invalidDataInfo.getType());
                }
                
            // 2.2: print/write formats: 4-byte each = 8 bytes

                byte[] printFormt = Arrays.copyOfRange(recordType2Fixed, offset, offset+
                    LENGTH_PRINT_FORMAT_CODE);
                dbgLog.fine("printFrmt="+new String (Hex.encodeHex(printFormt)));


                offset +=LENGTH_PRINT_FORMAT_CODE;
                int formatCode = isLittleEndian ? printFormt[2] : printFormt[1];
                int formatWidth = isLittleEndian ? printFormt[1] : printFormt[2];
                int formatDecimalPointPosition = isLittleEndian ? printFormt[0] : printFormt[3];
                dbgLog.fine("format code{5=F, 1=A[String]}="+formatCode);
                
                formatDecimalPointPositionList.add(formatDecimalPointPosition);


                if (!SPSSConstants.FORMAT_CODE_TABLE_SAV.containsKey(formatCode)){
                    throw new IOException("Unknown format code was found = "
                        + formatCode);
                } else{
                    printFormatList.add(formatCode);
                }

                byte[] writeFormt = Arrays.copyOfRange(recordType2Fixed, offset, offset+
                    LENGTH_WRITE_FORMAT_CODE);

                dbgLog.fine("writeFrmt="+new String (Hex.encodeHex(writeFormt)));
                if (writeFormt[3] != 0x00){
                    dbgLog.fine("byte-order(write format): reversal required");
                }


                offset +=LENGTH_WRITE_FORMAT_CODE;




            // 2.3 variable name: 8 byte(space[x20]-padded)

                //printHexDump(Arrays.copyOfRange(recordType2Fixed, offset,
                //    (offset+LENGTH_VARIABLE_NAME)), "variableName");

                String RawVariableName = new String(Arrays.copyOfRange(recordType2Fixed, offset,
                    (offset+LENGTH_VARIABLE_NAME)),"US-ASCII");
                offset +=LENGTH_VARIABLE_NAME;
                String variableName = null;
                if (RawVariableName.indexOf(' ') >= 0){
                    variableName = RawVariableName.substring(0, RawVariableName.indexOf(' '));
                } else {
                    variableName = RawVariableName;
                }
                // caseWeightVariableOBSIndex starts from 1: 0 is used for does-not-exist cases
                if (j == (caseWeightVariableOBSIndex -1)){
                    caseWeightVariableName = variableName;
                    caseWeightVariableIndex = variableCounter;

                    smd.setCaseWeightVariableName(caseWeightVariableName);
                    smd.getFileInformation().put("caseWeightVariableIndex", caseWeightVariableIndex);
                }
                OBSIndexToVariableName.put(j, variableName);
                dbgLog.fine("\nvariable name="+variableName+"<-");
                dbgLog.fine(j+"-th variable name="+variableName+"<-");
                variableNameList.add(variableName);





                if (!SPSSConstants.ORDINARY_FORMAT_CODE_SET.contains(formatCode)){
                    StringBuilder sb = new StringBuilder(
                        SPSSConstants.FORMAT_CODE_TABLE_SAV.get(formatCode)+
                        formatWidth);
                    if (formatDecimalPointPosition > 0){
                        sb.append("."+ formatDecimalPointPosition);
                    }
                    printFormatNameTable.put(variableName, sb.toString());
                    
                }
                printFormatTable.put(variableName, SPSSConstants.FORMAT_CODE_TABLE_SAV.get(formatCode));
                
                
                
                
                
                
                

            // 2.4 [optional]The length of a variable label followed: 4-byte int
            // 3rd element of 2.1 indicates whether this field exists
            // *** warning: The label block is padded to a multiple of the 4-byte
            // NOT the raw integer value of this 4-byte block
            if (hasVariableLabel){
                byte[] length_variable_label= new byte[4];
                int nbytes_2_4 = stream.read(length_variable_label);
                if (nbytes_2_4 == 0){
                    throw new IOException("RT 2: reading recordType2.4: no byte was read");
                } else {
                    dbgLog.fine("nbytes_2_4="+nbytes_2_4);
                }
                ByteBuffer bb_length_variable_label = ByteBuffer.wrap(
                    length_variable_label, 0, LENGTH_VARIABLE_LABEL);
                if (isLittleEndian){
                    bb_length_variable_label.order(ByteOrder.LITTLE_ENDIAN);
                }
                int rawVariableLabelLength = bb_length_variable_label.getInt();

                dbgLog.fine("rawVariableLabelLength="+rawVariableLabelLength);
                int variableLabelLength = getSAVintAdjustedBlockLength(rawVariableLabelLength);
                dbgLog.fine("variableLabelLength="+variableLabelLength);


            // 2.5 [optional]variable label whose length is found at 2.4

                byte[] variable_label = new byte[variableLabelLength];
                int nbytes_2_5 = stream.read(variable_label);
                if (nbytes_2_5 == 0){
                    throw new IOException("RT 2: reading recordType2.5: no byte was read");
                } else {
                    dbgLog.fine("nbytes_2_5="+nbytes_2_5);
                }
                String variableLabel = new String(Arrays.copyOfRange(variable_label,
                    0, rawVariableLabelLength),"US-ASCII");
                dbgLog.fine("variableLabel="+variableLabel+"<-");

                variableLabelMap.put(variableName, variableLabel);
                variableLabelList.add(variableLabel);
            } else {
                variableLabelList.add("");
            }
            // 2.6 [optional] missing values:4-byte each if exists
            //     4th element of 2.1 indicates the structure of this sub-field
                if (hasMissingValues){
                    dbgLog.fine("decoding missing value: type="+recordType2FixedPart1[3]);
                    int howManyMissingValueUnits = missingValueCodeUnits.get(recordType2FixedPart1[3]);
                    //int howManyMissingValueUnits = recordType2FixedPart1[3] > 0 ? recordType2FixedPart1[3] :  0;

                    dbgLog.fine("howManyMissingValueUnits="+howManyMissingValueUnits);

                    byte[] missing_value_code_units = new byte[LENGTH_SAV_OBS_BLOCK*howManyMissingValueUnits];
                    int nbytes_2_6 = stream.read(missing_value_code_units);
                    if (nbytes_2_6 == 0){
                        throw new IOException("RT 2: reading recordType2.6: no byte was read");
                    } else {
                        dbgLog.fine("nbytes_2_6="+nbytes_2_6);
                    }

                    //printHexDump(missing_value_code_units, "missing value");

                    if (isNumericVariable){

                        double[] missingValues = new double[howManyMissingValueUnits];
                        //List<String> mvp = new ArrayList<String>();
                        List<String> mv = new ArrayList<String>();

                        ByteBuffer[] bb_missig_value_code = 
                            new ByteBuffer[howManyMissingValueUnits];

                        int offset_start = 0;
                        
                        for (int i= 0; i < howManyMissingValueUnits;i++ ){

                            bb_missig_value_code[i]  =
                                    ByteBuffer.wrap(missing_value_code_units, offset_start,
                                    LENGTH_SAV_OBS_BLOCK);

                            offset_start +=LENGTH_SAV_OBS_BLOCK;
                            if (isLittleEndian){
                               bb_missig_value_code[i].order(ByteOrder.LITTLE_ENDIAN);
                            }
                            ByteBuffer temp = bb_missig_value_code[i].duplicate();


                            missingValues[i] = bb_missig_value_code[i].getDouble();
                            if (Double.toHexString(missingValues[i]).equals("-0x1.ffffffffffffep1023")){
                                dbgLog.fine("1st value is LOWEST");
                                mv.add(Double.toHexString(missingValues[i]));
                            } else if (Double.valueOf(missingValues[i]).equals(Double.MAX_VALUE)){
                                dbgLog.fine("2nd value is HIGHEST");
                                mv.add(Double.toHexString(missingValues[i]));
                            } else {
                                mv .add(doubleNumberFormatter.format(missingValues[i]));
                            }
                            dbgLog.fine(i+"-th missing value="+Double.toHexString(missingValues[i]));
                        }
                        dbgLog.fine("variableName="+variableName);
                        if (recordType2FixedPart1[3] > 0) {
                            // point cases only
                            dbgLog.fine("mv(>0)="+mv);
                            missingValueTable.put(variableName, mv);
                            invalidDataInfo.setInvalidValues(mv);
                        } else if (recordType2FixedPart1[3]== -2) {
                            dbgLog.fine("mv(-2)="+mv);
                            // range
                            invalidDataInfo.setInvalidRange(mv);
                        } else if (recordType2FixedPart1[3]== -3){
                            // mixed case
                            dbgLog.fine("mv(-3)="+mv);
                            invalidDataInfo.setInvalidRange(mv.subList(0, 2));
                            invalidDataInfo.setInvalidValues(mv.subList(2, 3));
                            missingValueTable.put(variableName, mv.subList(2, 3));
                        }
                        
                        dbgLog.fine("missing value="+
                            StringUtils.join(missingValueTable.get(variableName),"|"));
                        dbgLog.fine("invalidDataInfo(Numeric):\n"+invalidDataInfo);
                        invalidDataTable.put(variableName, invalidDataInfo);
                    } else {
                        // string variable case
                        String[] missingValues = new String[howManyMissingValueUnits];
                        List<String> mv = new ArrayList<String>();
                        int offset_start = 0;
                        int offset_end   = LENGTH_SAV_OBS_BLOCK;
                        for (int i= 0; i < howManyMissingValueUnits;i++ ){

                            missingValues[i] = StringUtils.stripEnd(new
                                String(Arrays.copyOfRange(missing_value_code_units,
                                offset_start, offset_end),"US-ASCII"), " ");
                            dbgLog.fine("missing value="+missingValues[i]+"<-");

                            offset_start = offset_end;
                            offset_end +=LENGTH_SAV_OBS_BLOCK;


                            mv.add(missingValues[i]);
                        }
                        invalidDataInfo.setInvalidValues(mv);
                        missingValueTable.put(variableName, mv);
                        invalidDataTable.put(variableName, invalidDataInfo);
                        dbgLog.fine("missing value(str)="+
                            StringUtils.join(missingValueTable.get(variableName),"|"));
                        dbgLog.fine("invalidDataInfo(String):\n"+invalidDataInfo);

                    } // string case
                    dbgLog.fine("invalidDataTable:\n"+invalidDataTable);
                } // if msv


            } catch (IOException ex){
                ex.printStackTrace();
            } catch (Exception ex){
                ex.printStackTrace();
            }

            if (j == (OBSUnitsPerCase-1) ){
                dbgLog.fine("RT2 metadata-related exit-chores");
                smd.getFileInformation().put("varQnty", variableCounter);
                varQnty = variableCounter;
                smd.setVariableName(variableNameList.toArray(new String[variableNameList.size()]));
                smd.setVariableLabel(variableLabelMap);
                smd.setMissingValueTable(missingValueTable);
                smd.getFileInformation().put("caseWeightVariableName", caseWeightVariableName);
                smd.setVariableTypeMinimal(ArrayUtils.toPrimitive(
                variableTypelList.toArray(new Integer[variableTypelList.size()])));

                
                dbgLog.fine("OBSwiseTypelList="+OBSwiseTypelList);
                
                // variableType is determined after the valueTable is finalized
            }

        } // j-loop
        dbgLog.fine("***** decodeRecordType2(): end *****");
    }
    
    
    void decodeRecordType3and4(BufferedInputStream stream){
        dbgLog.fine("***** decodeRecordType3and4(): start *****");
        int safteyCounter = 0;
while(true ){
        try {
            if (stream ==null){
                throw new IllegalArgumentException("stream == null!");
            }
            // this secton may not exit so first check the 4-byte header value
            //if (stream.markSupported()){
            stream.mark(1000);
            //}
            // 3.0 check the first 4 bytes
            byte[] headerCode = new byte[LENGTH_RECORD_TYPE3_CODE];

            int nbytes_rt3 = stream.read(headerCode, 0, LENGTH_RECORD_TYPE3_CODE);
            // to-do check against nbytes
            //printHexDump(headerCode, "RT3 header test");
            ByteBuffer bb_header_code  = ByteBuffer.wrap(headerCode,
                       0, LENGTH_RECORD_TYPE3_CODE);
            if (isLittleEndian){
                bb_header_code.order(ByteOrder.LITTLE_ENDIAN);
            }

            int intRT3test = bb_header_code.getInt();
            dbgLog.fine("header test value: RT3="+intRT3test);
            if (intRT3test != 3){
            //if (stream.markSupported()){
                dbgLog.fine("iteration="+safteyCounter);

                stream.reset();
                return;
            //}
            }
            // 3.1 how many value-label pairs follow
            byte[] number_of_labels = new byte[LENGTH_RT3_HOW_MANY_LABELS];

            int nbytes_3_1 = stream.read(number_of_labels);
            if (nbytes_3_1 == 0){
                throw new IOException("RT 3: reading recordType3.1: no byte was read");
            }
             ByteBuffer bb_number_of_labels  = ByteBuffer.wrap(number_of_labels,
                   0, LENGTH_RT3_HOW_MANY_LABELS);
            if (isLittleEndian){
                bb_number_of_labels.order(ByteOrder.LITTLE_ENDIAN);
            }

            int numberOfValueLabels = bb_number_of_labels.getInt();
            dbgLog.fine("number of value-label pairs="+numberOfValueLabels);
            
            
            ByteBuffer[] tempBB = new ByteBuffer[numberOfValueLabels];
            
            String valueLabel[] = new String[numberOfValueLabels];
            
            
            for (int i=0; i<numberOfValueLabels;i++){
            
                // read 8-byte as value
            
            
                
                byte[] value = new byte[LENGTH_RT3_VALUE];
                int nbytes_3_value = stream.read(value);
                
                if (nbytes_3_value == 0){
                    throw new IOException("RT 3: reading recordType3 value: no byte was read");
                }
                // note these 8 bytes are interpreted later
                // currently no information about which variable's (=> type unknown)
                ByteBuffer bb_value  = ByteBuffer.wrap(value,
                           0, LENGTH_RT3_VALUE);
                if (isLittleEndian){
                    bb_value.order(ByteOrder.LITTLE_ENDIAN);
                }
                tempBB[i] = bb_value;
                dbgLog.fine("bb_value="+Hex.encodeHex(bb_value.array()));
                
            /*
                double valueD = bb_value.getDouble();
                
                dbgLog.fine("value="+valueD);
            */
                // read 1st byte as unsigned integer = label_length
                
                // read label_length byte as label
                byte[] labelLengthByte = new byte[LENGTH_RT3_LABEL_LENGTH];
                
                int nbytes_3_label_length = stream.read(labelLengthByte);

                // add check-routine here
                
                dbgLog.fine("labelLengthByte"+Hex.encodeHex(labelLengthByte));
                
                dbgLog.fine("label length = "+labelLengthByte[0]);
                // the net-length of a value label is saved as
                // unsigned byte; however, the length is less than 127
                // byte should be ok
                int rawLabelLength = labelLengthByte[0] & 0xFF;
                dbgLog.fine("rawLabelLength="+rawLabelLength);
                // -1 =>1-byte already read
                int labelLength = getSAVobsAdjustedBlockLength(rawLabelLength+1)-1;
                
                byte[] valueLabelBytes = new byte[labelLength];
                int nbytes_3_value_label = stream.read(valueLabelBytes);
                
                
               // ByteBuffer bb_label = ByteBuffer.wrap(valueLabel,0,labelLength);
               
                valueLabel[i] = StringUtils.stripEnd(new
                    String(Arrays.copyOfRange(valueLabelBytes,
                    0, rawLabelLength),"US-ASCII"), " ");
                    
                dbgLog.fine(i+"-th valueLabel="+valueLabel[i]+"<-");


            } // iter rt3

            dbgLog.fine("end of RT3 block");
            dbgLog.fine("start of RT4 block");

            // 4.0 check the first 4 bytes
            byte[] headerCode4 = new byte[LENGTH_RECORD_TYPE4_CODE];

            int nbytes_rt4 = stream.read(headerCode4, 0, LENGTH_RECORD_TYPE4_CODE);
            
            if (nbytes_rt4 == 0){
                throw new IOException("RT4: reading recordType4 value: no byte was read");
            }

            //printHexDump(headerCode4, "RT4 header test");
            
            ByteBuffer bb_header_code_4  = ByteBuffer.wrap(headerCode4,
                       0, LENGTH_RECORD_TYPE4_CODE);
            if (isLittleEndian){
                bb_header_code_4.order(ByteOrder.LITTLE_ENDIAN);
            }

            int intRT4test = bb_header_code_4.getInt();
            dbgLog.fine("header test value: RT4="+intRT4test);
            
            if (intRT4test != 4){
                throw new IOException("RT 4: reading recordType4 header: no byte was read");
            }
            
            // 4.1 read the how-many-variables bytes
            byte[] howManyVariablesfollow = new byte[LENGTH_RT4_HOW_MANY_VARIABLES];
            
            int nbytes_rt4_1 = stream.read(howManyVariablesfollow, 0, LENGTH_RT4_HOW_MANY_VARIABLES);
            
            ByteBuffer bb_howManyVariablesfollow = ByteBuffer.wrap(howManyVariablesfollow,
                       0, LENGTH_RT4_HOW_MANY_VARIABLES);
            if (isLittleEndian){
                bb_howManyVariablesfollow.order(ByteOrder.LITTLE_ENDIAN);
            }

            int howManyVariablesRT4 = bb_howManyVariablesfollow.getInt();
            dbgLog.fine("how many variables follow: RT4="+howManyVariablesRT4);
            
            int length_indicies = LENGTH_RT4_VARIABLE_INDEX*howManyVariablesRT4;
            byte[] variableIdicesBytes = new byte[length_indicies];

            int nbytes_rt4_2 = stream.read(variableIdicesBytes, 0, length_indicies);
            
            // !!!!! Caution: variableIndex in RT4 starts from 1 NOT ** 0 **
            
            
            int[] variableIndex = new int[howManyVariablesRT4];
            int offset = 0;
            for (int i=0; i< howManyVariablesRT4; i++){
                
                ByteBuffer bb_variable_index = ByteBuffer.wrap(variableIdicesBytes,
                       offset, LENGTH_RT4_VARIABLE_INDEX);            
                offset +=LENGTH_RT4_VARIABLE_INDEX;


                if (isLittleEndian){
                    bb_variable_index.order(ByteOrder.LITTLE_ENDIAN);
                }

                variableIndex[i] = bb_variable_index.getInt();
                dbgLog.fine(i+"-th variable index number="+variableIndex[i]);
                
            }
            
            dbgLog.fine("variable index set="+ArrayUtils.toString(variableIndex));
            dbgLog.fine("subtract 1 from variableIndex for getting a variable info");
            
            boolean isNumeric = OBSwiseTypelList.get(variableIndex[0]-1)==0 ? true : false;
            
            Map<String, String> valueLabelPair = new LinkedHashMap<String, String>();
            if (isNumeric){
                // numeric variable
                dbgLog.fine("processing of a numeric value-label table");
                for (int j=0;j<numberOfValueLabels;j++){
                    valueLabelPair.put(doubleNumberFormatter.format(tempBB[j].getDouble()), 
                    valueLabel[j] );
                }
            } else {
                // String variable
                dbgLog.fine("processing of a string value-label table");
                for (int j=0;j<numberOfValueLabels;j++){
                    valueLabelPair.put(
                    StringUtils.stripEnd(new String((tempBB[j].array()),"US-ASCII"), " "),
                    valueLabel[j] );
                }
            }

            dbgLog.fine("valueLabePair="+valueLabelPair);
            dbgLog.fine("key variable's (raw) index ="+variableIndex[0]);
            
            valueLabelTable.put(OBSIndexToVariableName.get(variableIndex[0]-1),valueLabelPair);
            
            dbgLog.fine("valueLabelTable="+valueLabelTable);
            
            // create a mapping table that finds the key variable for this mapping table
            String keyVariableName = OBSIndexToVariableName.get(variableIndex[0]-1);
            for (int vn : variableIndex){
                valueVariableMappingTable.put(OBSIndexToVariableName.get(vn - 1), keyVariableName);
            }
            
            dbgLog.fine("valueVariableMappingTable:\n"+valueVariableMappingTable);            
        } catch (IOException ex){
            ex.printStackTrace();
        }

        safteyCounter++;
        if (safteyCounter >= 1000000){
            break;
        }
} //while





        dbgLog.fine("***** decodeRecordType3and4(): end *****");
    }
    
    
    

    void decodeRecordType6(BufferedInputStream stream){
        dbgLog.fine("***** decodeRecordType6(): start *****");
        try {
            if (stream ==null){
                throw new IllegalArgumentException("stream == null!");
            }
            // this secton may not exit so first check the 4-byte header value
            //if (stream.markSupported()){
            stream.mark(1000);
            //}
            // 6.0 check the first 4 bytes
            byte[] headerCodeRt6 = new byte[LENGTH_RECORD_TYPE6_CODE];

            int nbytes_rt6 = stream.read(headerCodeRt6, 0, LENGTH_RECORD_TYPE6_CODE);
            // to-do check against nbytes
            //printHexDump(headerCodeRt6, "RT6 header test");
            ByteBuffer bb_header_code_rt6  = ByteBuffer.wrap(headerCodeRt6,
                       0, LENGTH_RECORD_TYPE6_CODE);
            if (isLittleEndian){
                bb_header_code_rt6.order(ByteOrder.LITTLE_ENDIAN);
            }

            int intRT6test = bb_header_code_rt6.getInt();
            dbgLog.fine("header test value: RT6="+intRT6test);
            if (intRT6test != 6){
            //if (stream.markSupported()){
                //out.print("iteration="+safteyCounter);
                //dbgLog.fine("iteration="+safteyCounter);
                dbgLog.fine("intRT6test failed="+intRT6test);
                
                stream.reset();
                return;
            //}
            }
            // 6.1 check 4-byte integer that tells how many lines follow
            
            byte[] length_how_many_line_bytes = new byte[LENGTH_RT6_HOW_MANY_LINES];

            int nbytes_rt6_1 = stream.read(length_how_many_line_bytes, 0,
                LENGTH_RT6_HOW_MANY_LINES);
            // to-do check against nbytes
            
            //printHexDump(length_how_many_line_bytes, "RT6 how_many_line_bytes");
            ByteBuffer bb_how_many_lines = ByteBuffer.wrap(length_how_many_line_bytes,
                       0, LENGTH_RT6_HOW_MANY_LINES);
            if (isLittleEndian){
                bb_how_many_lines.order(ByteOrder.LITTLE_ENDIAN);
            }

            int howManyLinesRt6 = bb_how_many_lines.getInt();
            dbgLog.fine("how Many lines follow="+howManyLinesRt6);
            
            // 6.2 read 80-char-long lines 
            String[] documentRecord = new String[howManyLinesRt6];
            
            for (int i=0;i<howManyLinesRt6; i++){
                
                byte[] line = new byte[80];
                int nbytes_rt6_line = stream.read(line);
               
                documentRecord[i] = StringUtils.stripEnd(new
                    String(Arrays.copyOfRange(line,
                    0, LENGTH_RT6_DOCUMENT_LINE),"US-ASCII"), " ");
                    
                dbgLog.fine(i+"-th line ="+documentRecord[i]+"<-");
            }
            dbgLog.fine("documentRecord:\n"+StringUtils.join(documentRecord, "\n"));


        } catch (IOException ex){
            ex.printStackTrace();
        }
        
        dbgLog.fine("***** decodeRecordType6(): end *****");
    }
    
    

    void decodeRecordType7(BufferedInputStream stream){
        dbgLog.fine("***** decodeRecordType7(): start *****");
        int counter=0;
        int[] headerSection = new int[2];
    while(true){
        try {
            if (stream ==null){
                throw new IllegalArgumentException("RT7: stream == null!");
            }
            // first check the 4-byte header value
            //if (stream.markSupported()){
            stream.mark(1000);
            //}
            // 7.0 check the first 4 bytes
            byte[] headerCodeRt7 = new byte[LENGTH_RECORD_TYPE7_CODE];

            int nbytes_rt7 = stream.read(headerCodeRt7, 0, 
                LENGTH_RECORD_TYPE7_CODE);
            // to-do check against nbytes
            //printHexDump(headerCodeRt7, "RT7 header test");
            ByteBuffer bb_header_code_rt7  = ByteBuffer.wrap(headerCodeRt7,
                       0, LENGTH_RECORD_TYPE7_CODE);
            if (isLittleEndian){
                bb_header_code_rt7.order(ByteOrder.LITTLE_ENDIAN);
            }

            int intRT7test = bb_header_code_rt7.getInt();
            dbgLog.fine("header test value: RT7="+intRT7test);
            if (intRT7test != 7){
            //if (stream.markSupported()){
                //out.print("iteration="+safteyCounter);
                //dbgLog.fine("iteration="+safteyCounter);
                dbgLog.fine("intRT7test failed="+intRT7test);
                dbgLog.fine("counter="+counter);
                stream.reset();
                return;
            //}
            }
            
            
            
            // 7.1 check 4-byte integer Sub-Type Code
            
            byte[] length_sub_type_code = new byte[LENGTH_RT7_SUB_TYPE_CODE];

            int nbytes_rt7_1 = stream.read(length_sub_type_code, 0,
                LENGTH_RT7_SUB_TYPE_CODE);
            // to-do check against nbytes
            
            //printHexDump(length_how_many_line_bytes, "RT7 how_many_line_bytes");
            ByteBuffer bb_sub_type_code = ByteBuffer.wrap(length_sub_type_code,
                       0, LENGTH_RT7_SUB_TYPE_CODE);
            if (isLittleEndian){
                bb_sub_type_code.order(ByteOrder.LITTLE_ENDIAN);
            }

            int subTypeCode = bb_sub_type_code.getInt();
            dbgLog.fine("subTypeCode="+subTypeCode);
            
            
            switch (subTypeCode) {
                case 3:
                    // 3: Release andMachine-Specific Integer Information
                    
                    //parseRT7SubTypefield(stream);
                    
                    
                    headerSection = parseRT7SubTypefieldHeader(stream);
                    if (headerSection != null){
                        int unitLength = headerSection[0];
                        int numberOfUnits = headerSection[1];
                        
                        
                        for (int i=0; i<numberOfUnits; i++){
                            dbgLog.finer(i+"-th fieldData");
                            byte[] work = new byte[unitLength];

                            int nb = stream.read(work);
                            dbgLog.finer("raw bytes in Hex:"+ new String(Hex.encodeHex(work)));
                            ByteBuffer bb_field = ByteBuffer.wrap(work);
                            if (isLittleEndian){
                                bb_field.order(ByteOrder.LITTLE_ENDIAN);
                            }
                            String dataInHex = new String(Hex.encodeHex(bb_field.array()));
                            releaseMachineSpecificInfoHex.add(dataInHex);
                            
                            dbgLog.finer("raw bytes in Hex:"+ dataInHex);
                            if (unitLength==4){
                                int fieldData = bb_field.getInt();
                                dbgLog.finer("fieldData(int)="+fieldData);
                                dbgLog.finer("fieldData in Hex=0x"+Integer.toHexString(fieldData));
                                releaseMachineSpecificInfo.add(fieldData);
                            }
                            
                        }
                       
                        dbgLog.fine("releaseMachineSpecificInfo="+releaseMachineSpecificInfo);
                        dbgLog.fine("releaseMachineSpecificInfoHex="+releaseMachineSpecificInfoHex);
                       
                    } else {
                        // throw new IOException
                    }
                    
                    
                    dbgLog.fine("***** end of subType 3 ***** \n");
                    
                    break;
                case 4: 
                    // Release andMachine-SpecificOBS-Type Information
                    headerSection = parseRT7SubTypefieldHeader(stream);
                    if (headerSection != null){
                        int unitLength = headerSection[0];
                        int numberOfUnits = headerSection[1];


                        for (int i=0; i<numberOfUnits; i++){
                            dbgLog.finer(i+"-th fieldData:"+RecordType7SubType4Fields.get(i));
                            byte[] work = new byte[unitLength];

                            int nb = stream.read(work);

                            dbgLog.finer("raw bytes in Hex:"+ new String(Hex.encodeHex(work)));
                            ByteBuffer bb_field = ByteBuffer.wrap(work);
                            dbgLog.finer("byte order="+bb_field.order().toString());
                            if (isLittleEndian){
                                bb_field.order(ByteOrder.LITTLE_ENDIAN);
                            }
                            ByteBuffer bb_field_dup = bb_field.duplicate();
//                            OBSTypeHexValue.put(RecordType7SubType4Fields.get(i),
//                                new String(Hex.encodeHex(bb_field.array())) );
                            dbgLog.finer("raw bytes in Hex:"+
                                OBSTypeHexValue.get(RecordType7SubType4Fields.get(i)));
                            if (unitLength==8){
                                double fieldData = bb_field.getDouble();
                                OBSTypeValue.put(RecordType7SubType4Fields.get(i), fieldData);
                                dbgLog.finer("fieldData(double)="+fieldData);
                                OBSTypeHexValue.put(RecordType7SubType4Fields.get(i),
                                    Double.toHexString(fieldData));
                                dbgLog.fine("fieldData in Hex="+Double.toHexString(fieldData));
                            }
                        }
                        dbgLog.fine("OBSTypeValue="+OBSTypeValue);
                        dbgLog.fine("OBSTypeHexValue="+OBSTypeHexValue);

                    } else {
                        // throw new IOException
                    }
                    

                    dbgLog.fine("***** end of subType 4 ***** \n");
                    break;
                case 5:
                    // Variable Sets Information
                    parseRT7SubTypefield(stream);
                    break;
                case 6:
                    // Trends date information
                    parseRT7SubTypefield(stream);
                    break;
                case 7:
                    // Multiple response groups
                    parseRT7SubTypefield(stream);
                    break;
                case 8:
                    // Windows Data Entry data
                    parseRT7SubTypefield(stream);
                    break;
                case 9:
                    //
                    parseRT7SubTypefield(stream);
                    break;
                case 10:
                    // TextSmart data
                    parseRT7SubTypefield(stream);
                    break;
                case 11:
                    // Msmt level, col width, & alignment
                    //parseRT7SubTypefield(stream);

                    headerSection = parseRT7SubTypefieldHeader(stream);
                    if (headerSection != null){
                        int unitLength = headerSection[0];
                        int numberOfUnits = headerSection[1];

                        for (int i=0; i<numberOfUnits; i++){
                            dbgLog.finer(i+"-th fieldData");
                            byte[] work = new byte[unitLength];

                            int nb = stream.read(work);
                            dbgLog.finer("raw bytes in Hex:"+ new String(Hex.encodeHex(work)));
                            ByteBuffer bb_field = ByteBuffer.wrap(work);
                            if (isLittleEndian){
                                bb_field.order(ByteOrder.LITTLE_ENDIAN);
                            }
                            dbgLog.finer("raw bytes in Hex:"+ new String(Hex.encodeHex(bb_field.array())));
                            
                            if (unitLength==4){
                                int fieldData = bb_field.getInt();
                                dbgLog.finer("fieldData(int)="+fieldData);
                                dbgLog.finer("fieldData in Hex=0x"+Integer.toHexString(fieldData));
                                
                                int remainder = i%3;
                                dbgLog.finer("remainder="+remainder);
                                if (remainder == 0){
                                    measurementLevel.add(fieldData);
                                } else if (remainder == 1){
                                    columnWidth.add(fieldData);
                                } else if (remainder == 2){
                                    alignment.add(fieldData);
                                }
                            }

                        }

                    } else {
                        // throw new IOException
                    }
                    dbgLog.fine("measurementLevel="+measurementLevel);
                    dbgLog.fine("columnWidth="+columnWidth);
                    dbgLog.fine("alignment="+alignment);
                    dbgLog.fine("***** end of subType 11 ***** \n");

                    break;
                case 12:
                    // Windows Data Entry GUID
                    parseRT7SubTypefield(stream);
                    break;
                case 13:
                    // Extended variable names
                    // parseRT7SubTypefield(stream);
                    headerSection = parseRT7SubTypefieldHeader(stream);

                    if (headerSection != null){
                        int unitLength = headerSection[0];
                        dbgLog.fine("unitLength="+unitLength);
                        int numberOfUnits = headerSection[1];
                        dbgLog.fine("numberOfUnits="+numberOfUnits);
                        byte[] work = new byte[unitLength*numberOfUnits];
                        int nbtyes13 = stream.read(work);

                        String[] variableShortLongNamePairs = new String(work,"US-ASCII").split("\t");

                        for (int i=0; i<variableShortLongNamePairs.length; i++){
                            dbgLog.fine(i+"-th pair"+variableShortLongNamePairs[i]);
                            String[] pair = variableShortLongNamePairs[i].split("=");
                            shortToLongVarialbeNameTable.put(pair[0], pair[1]);
                        }

                        dbgLog.fine("shortToLongVarialbeNameTable"+
                                shortToLongVarialbeNameTable);
                        smd.setShortToLongVarialbeNameTable(shortToLongVarialbeNameTable);
                    } else {
                        // throw new IOException
                    }

                    break;
                case 14:
                    // Extended strings
                    parseRT7SubTypefield(stream);
                    break;
                case 15:
                    // Clementine Metadata
                    parseRT7SubTypefield(stream);
                    break;
                case 16:
                    // 64 bit N of cases
                    parseRT7SubTypefield(stream);
                    break;
                case 17:
                    // File level attributes
                    parseRT7SubTypefield(stream);
                    break;
                case 18:
                    // Variable attributes
                    parseRT7SubTypefield(stream);
                    break;
                case 19:
                    // Extended multiple response groups
                    parseRT7SubTypefield(stream);
                    break;
                case 20:
                    // Encoding, aka code page
                    parseRT7SubTypefield(stream);
                    break;
                case 21:
                    // Value labels for long strings
                    parseRT7SubTypefield(stream);
                    break;
                case 22:
                    // Missing values for long strings
                    parseRT7SubTypefield(stream);
                    break;
                default:
                    parseRT7SubTypefield(stream);
            }

        } catch (IOException ex){
            ex.printStackTrace();
        }

        counter++;

        if (counter > 20){
            break;
        }
    }
        
        dbgLog.fine("***** decodeRecordType7(): end *****");
    }
    
    
    void decodeRecordType999(BufferedInputStream stream){
        dbgLog.fine("***** decodeRecordType999(): start *****");
        try {
            if (stream ==null){
                throw new IllegalArgumentException("RT999: stream == null!");
            }
            // first check the 4-byte header value
            //if (stream.markSupported()){
            stream.mark(1000);
            //}
            // 999.0 check the first 4 bytes
            byte[] headerCodeRt999 = new byte[LENGTH_RECORD_TYPE999_CODE];

            int nbytes_rt999 = stream.read(headerCodeRt999, 0, 
                LENGTH_RECORD_TYPE999_CODE);
            // to-do check against nbytes
            //printHexDump(headerCodeRt999, "RT999 header test");
            ByteBuffer bb_header_code_rt999  = ByteBuffer.wrap(headerCodeRt999,
                       0, LENGTH_RECORD_TYPE999_CODE);
            if (isLittleEndian){
                bb_header_code_rt999.order(ByteOrder.LITTLE_ENDIAN);
            }

            int intRT999test = bb_header_code_rt999.getInt();
            dbgLog.fine("header test value: RT999="+intRT999test);
            if (intRT999test != 999){
            //if (stream.markSupported()){
                dbgLog.fine("intRT999test failed="+intRT999test);
                stream.reset();
               throw new IOException("RT999:Header value(999) was not correctly detected:"+intRT999test);
            //}
            }
            
            
            
            // 999.1 check 4-byte integer Filler block
            
            byte[] length_filler = new byte[LENGTH_RT999_FILLER];

            int nbytes_rt999_1 = stream.read(length_filler, 0,
                LENGTH_RT999_FILLER);
            // to-do check against nbytes
            
            //printHexDump(length_how_many_line_bytes, "RT999 how_many_line_bytes");
            ByteBuffer bb_filler = ByteBuffer.wrap(length_filler,
                       0, LENGTH_RT999_FILLER);
            if (isLittleEndian){
                bb_filler.order(ByteOrder.LITTLE_ENDIAN);
            }

            int rt999filler = bb_filler.getInt();
            dbgLog.fine("rt999filler="+rt999filler);
            
            if (rt999filler == 0){
                dbgLog.fine("the end of the dictionary section");
            } else {
                throw new IOException("RT999: failed to detect the end mark(0): value="+rt999filler);
            }

            // missing value processing concerning HIGHEST/LOWEST values

            Set<Map.Entry<String,InvalidData>> msvlc = invalidDataTable.entrySet();
            for (Iterator<Map.Entry<String,InvalidData>> itc = msvlc.iterator(); itc.hasNext();){
                Map.Entry<String, InvalidData> et = itc.next();
                String variable = et.getKey();
                dbgLog.fine("variable="+variable);
                InvalidData invalidDataInfo = et.getValue();

                if (invalidDataInfo.getInvalidRange() != null &&
                    !invalidDataInfo.getInvalidRange().isEmpty()){
                    if (invalidDataInfo.getInvalidRange().get(0).equals(OBSTypeHexValue.get("LOWEST"))){
                        dbgLog.fine("1st value is LOWEST");
                        invalidDataInfo.getInvalidRange().set(0, "LOWEST");
                    } else if (invalidDataInfo.getInvalidRange().get(1).equals(OBSTypeHexValue.get("HIGHEST"))){
                        dbgLog.fine("2nd value is HIGHEST");
                        invalidDataInfo.getInvalidRange().set(1,"HIGHEST");
                    }
                }
            }
            dbgLog.fine("invalidDataTable:\n"+invalidDataTable);
            smd.setInvalidDataTable(invalidDataTable);
        } catch (IOException ex){
            ex.printStackTrace();
            exit(1);
        }
        
        dbgLog.fine("***** decodeRecordType999(): end *****");
    }
    
    

    void decodeRecordTypeData(BufferedInputStream stream){
        dbgLog.fine("***** decodeRecordTypeData(): start *****");
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        if (isDataSectionCompressed){
            decodeRecordTypeDataCompressed(stream);
        } else {
            decodeRecordTypeDataUnCompressed(stream);
        }



        unfValues = new String[varQnty];

        dbgLog.fine("variableTypeFinal:"+Arrays.toString(variableTypeFinal));
            
        for (int j=0;j<varQnty; j++){
            //int variableTypeNumer = variableTypelList.get(j) > 0 ? 1 : 0;
            int variableTypeNumer = variableTypeFinal[j];
            try {
                unfValues[j] = getUNF(dataTable2[j], variableTypeNumer,
                    unfVersionNumber, j);
                dbgLog.fine(j+"th unf value"+unfValues[j]);

            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            } catch (UnfException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        }
        

        dbgLog.fine("unf set:\n"+Arrays.deepToString(unfValues));
        
        try {
            fileUnfValue = UNFUtil.calculateUNF(unfValues,unfVersionNumber);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        } catch (UnfException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        
        dbgLog.fine("file-unf="+fileUnfValue);
        
        savDataSection.setUnf(unfValues);

        savDataSection.setFileUnf(fileUnfValue);

        smd.setVariableUNF(unfValues);
        
        smd.getFileInformation().put("fileUNF", fileUnfValue);

        dbgLog.fine("unf values:\n"+unfValues);
        
        savDataSection.setData(dataTable2);
        dbgLog.fine("dataTable2:\n"+Arrays.deepToString(dataTable2));

        dbgLog.fine("***** decodeRecordTypeData(): end *****");
    }


    void decodeRecordTypeDataCompressed(BufferedInputStream stream){
        dbgLog.fine("***** decodeRecordTypeDataCompressed(): start *****");
        if (stream ==null){
            throw new IllegalArgumentException("decodeRecordTypeDataCompressed: stream == null!");
        }
        
        FileOutputStream fileOutTab = null;
        PrintWriter pwout = null;
        
        try {
            // create a File object to save the tab-delimited data file
            File tabDelimitedDataFile = File.createTempFile("tempTabfile.", ".tab");

            String tabDelimitedDataFileName   = tabDelimitedDataFile.getAbsolutePath();

            // save the temp file name in the metadata object
            smd.getFileInformation().put("tabDelimitedDataFileLocation", tabDelimitedDataFileName);

            fileOutTab = new FileOutputStream(tabDelimitedDataFile);
            
            pwout = new PrintWriter(new OutputStreamWriter(fileOutTab, "utf8"), true);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        }

        boolean hasStringVarContinuousBlock = 
            obsNonVariableBlockSet.size() > 0 ? true : false;
        dbgLog.fine("hasStringVarContinuousBlock="+hasStringVarContinuousBlock);
        
        int ii = 0;
        
        int OBS = LENGTH_SAV_OBS_BLOCK;
        int nOBS = OBSUnitsPerCase;
        
        dbgLog.fine("OBSUnitsPerCase="+OBSUnitsPerCase);
        
        int caseIndex = 0;

        dbgLog.fine("printFormatTable:\n"+printFormatTable);

        dbgLog.fine("printFormatNameTable:\n"+printFormatNameTable);
        variableFormatTypeList = new String[varQnty];

        for (int i=0; i<varQnty;i++){
            variableFormatTypeList[i]=SPSSConstants.FORMAT_CATEGORY_TABLE.get(
                    printFormatTable.get(variableNameList.get(i)));
            dbgLog.fine("i="+i+"th variableFormatTypeList="+variableFormatTypeList[i]);
            formatCategoryTable.put(variableNameList.get(i), variableFormatTypeList[i]);
        }
        dbgLog.fine("variableFormatType:\n"+Arrays.deepToString(variableFormatTypeList));
        dbgLog.fine("formatCategoryTable:\n"+formatCategoryTable);

        // contents (variable) checker concering decimals
        variableTypeFinal = new int[varQnty];
        Arrays.fill(variableTypeFinal, 0);

        int numberOfDecimalVariables = 0;
        
        List<String> dataLine = new ArrayList<String>();
        List<String> dataLine2 = new ArrayList<String>();

        // Sets for NA-string-to-NaN conversion
        Set<Integer> NaNlocationNumeric = new LinkedHashSet<Integer>();
        Set<Integer> NaNlocationString = new LinkedHashSet<Integer>();
        // missing values are written to the tab-delimited file by
        // using the default or user-specified missing-value  strings;
        // however, to calculate UNF/summary statistics,
        // classes for these calculations require their specific 
        // missing values that differ from the above missing-value
        // strings; therefore, after row data for the tab-delimited 
        // file are written, missing values in a row are changed to
        // UNF/summary-statistics-OK ones.
        
        // data-storage object for sumStat
        dataTable2 = new Object[varQnty][caseQnty];
        
        try {
            // this compression is applied only to non-float data, i.e. integer;
            // 8-byte float datum is kept in tact
            boolean hasReachedEOF = false;
            
        OBSERVATION: while(true){
            dbgLog.finer("ii="+ii+"-th iteration");

            byte[] octate = new byte[LENGTH_SAV_OBS_BLOCK];

            Object[] dataRow = new Object[varQnty];


            int nbytes = stream.read(octate);

            String dphex = new String(Hex.encodeHex(octate));
            //dbgLog.finer("dphex="+ dphex);

            
            for (int i = 0; i < LENGTH_SAV_OBS_BLOCK; i++) {

                dbgLog.finer("i="+i+"-th iteration");
                int octate_i = octate[i];
                //dbgLog.fine("octate="+octate_i);
                if (octate_i < 0){
                    octate_i += 256;
                }
                int byteCode = octate_i;//octate_i & 0xF;
                //out.println("byeCode="+byteCode);
                switch (byteCode) {
                    case 252:
                        // end of the file
                        dbgLog.finer("end of file mark [FC] was found");
                        hasReachedEOF = true;
                        break;
                    case 253:
                        // FD: uncompressed data follows after this octate
                        // long string datum or float datum
                        // read the following octate
                        byte[] uncompressedByte = new byte[LENGTH_SAV_OBS_BLOCK];
                        int ucbytes = stream.read(uncompressedByte);
                        int typeIndex = (ii*OBS + i)%nOBS;
                        
                        if ((OBSwiseTypelList.get(typeIndex) > 0)||
                            (OBSwiseTypelList.get(typeIndex)== -1)){
                            // code= >0 |-1: string or its conitiguous block
                            // decode as a string object
                            String strdatum = new String(
                                Arrays.copyOfRange(uncompressedByte,
                                0, LENGTH_SAV_OBS_BLOCK),"US-ASCII");
                            //out.println("str_datum="+strdatum+"<-");
                            // add this non-missing-value string datum 
                            dataLine.add(strdatum);
                            //out.println("dataLine(String)="+dataLine);
                        } else if (OBSwiseTypelList.get(typeIndex)==0){
                            // code= 0: numeric 
                            
                            ByteBuffer bb_double = ByteBuffer.wrap(
                                uncompressedByte, 0, LENGTH_SAV_OBS_BLOCK);
                            if (isLittleEndian){
                                bb_double.order(ByteOrder.LITTLE_ENDIAN);
                            }

                            Double ddatum  = bb_double.getDouble();
                            // out.println("ddatum="+ddatum);
                            // add this non-missing-value numeric datum
                            dataLine.add(doubleNumberFormatter.format(ddatum)) ;

                        } else {
                            throw new IOException("out-of-range value was found");
                        }
                        
                        // EOF-check after reading this octate
                        if (stream.available() == 0){
                            hasReachedEOF = true;
                            dbgLog.fine(
                            "*** After reading an uncompressed octate," +
                             " reached the end of the file at "+ii
                                +"th iteration and i="+i+"th octate position [0-start] *****");
                        }


                        break;
                    case 254:
                        // FE: used as the missing value for string variables
                        // an empty case in a string variable also takes this value
                        // string variable does not accept space-only data
                        // cf: uncompressed case
                        // 20 20 20 20 20 20 20 20
                        // add the string missing value
                        // out.println("254: String missing data");
                        dataLine.add(" ");
 
                        break;
                    case 255:
                        // FF: system missing value for numeric variables
                        // cf: uncompressed case (sysmis)
                        // FF FF FF FF FF FF eF FF(little endian)
                        // add the numeric missing value
                        dataLine.add(MissingValueForTextDataFileNumeric);

                        break;
                    case 0:
                        // 00: do nothing
                        break;
                    default:
                        //out.println("byte code(default)="+ byteCode);
                        if ((byteCode > 0) && (byteCode < 252)) {
                            // datum is compressed
                            //Integer unCompressed = Integer.valueOf(byteCode -100);
                            // add this uncompressed numeric datum
                            Double unCompressed = Double.valueOf(byteCode -100);
                            dataLine.add(doubleNumberFormatter.format(unCompressed));
                            // out.println("uncompressed="+unCompressed);
                            // out.println("dataline="+dataLine);
                        }
                }// end of switch
                
                // out.println("end of switch");
                
                
                // The-end-of-a-case(row)-processing
                int varCounter = (ii*OBS + i +1)%nOBS;
                dbgLog.finer("variable counter="+varCounter+"(ii="+ii+")");
                
                if ((ii*OBS + i +1)%nOBS == 0){
                    //out.println("dataLine(before)="+dataLine);

                    // out.println("all variables in a case are parsed == nOBS");
                    // out.println("hasStringVarContinuousBlock="+hasStringVarContinuousBlock);
                    
                    // check whether a string-variable's continuous block exits
                    // if so, they must be joined
                    if (hasStringVarContinuousBlock){
                    // string-variable's continuous-block-concatenating-processing
                    
                        //out.println("concatenating process starts");
                        //out.println("dataLine(before)="+dataLine);
                        //out.println("dataLine(before:size)="+dataLine.size());

                        StringBuilder sb = new StringBuilder("");
                        int firstPosition = 0;
                        
                        Set<Integer> removeJset = new HashSet<Integer>();
                        for (int j=0; j< nOBS; j++){
                            dbgLog.finer("j="+j+"-th type ="+OBSwiseTypelList.get(j));
                            if (OBSwiseTypelList.get(j) == -1){
                                // String continued fount at j-th 
                                // look back the j-1 
                                firstPosition = j-1;
                                int lastJ = j;
                                String concatanated = null;
                               
                                removeJset.add(j);
                                sb.append(dataLine.get(j-1));
                                sb.append(dataLine.get(j));
                                for (int jc =1; ; jc++ ){
                                    if (OBSwiseTypelList.get(j+jc) != -1){
                                    // j is the end unit of this string variable
                                        concatanated = sb.toString();
                                        sb.setLength(0);
                                       lastJ = j+jc;
                                       break;
                                    } else {
                                        sb.append(dataLine.get(j+jc));
                                        removeJset.add(j+jc);
                                    }
                                }
                                dataLine.set(j-1, concatanated); 
                                
                                //out.println(j-1+"th concatanated="+concatanated);
                                j = lastJ -1; 
                           
                            } // end-of-if: continuous-OBS only
                        } // end of loop-j
                        
                        //out.println("removeJset="+removeJset);
                        
                        // a new list that stores a new case with concatanated string data
                        List<String> newDataLine = new ArrayList<String>();
                        
                        for (int jl=0; jl<dataLine.size();jl++){
                            //out.println("jl="+jl+"-th datum =["+dataLine.get(jl)+"]");
                            
                            if (!removeJset.contains(jl) ){
                            
//                                if (dataLine.get(jl).equals(MissingValueForTextDataFileString)){
//                                    out.println("NA-S jl= "+jl+"=["+dataLine.get(jl)+"]");
//                                } else if (dataLine.get(jl).equals(MissingValueForTextDataFileNumeric)){
//                                    out.println("NA-N jl= "+jl+"=["+dataLine.get(jl)+"]");
//                                } else if (dataLine.get(jl)==null){
//                                    out.println("null case jl="+jl+"=["+dataLine.get(jl)+"]");
//                                } else if (dataLine.get(jl).equals("NaN")){
//                                    out.println("NaN jl= "+jl+"=["+dataLine.get(jl)+"]");
//                                } else if (dataLine.get(jl).equals("")){
//                                    out.println("blank jl= "+jl+"=["+dataLine.get(jl)+"]");
//                                } else if (dataLine.get(jl).equals(" ")){
//                                    out.println("space jl= "+jl+"=["+dataLine.get(jl)+"]");
//                                }
                                
                                newDataLine.add(dataLine.get(jl));
                            } else {
//                                out.println("Excluded: jl="+jl+"-th datum=["+dataLine.get(jl)+"]");
                            }
                        }  // end of loop-jl

                        //out.println("new dataLine="+newDataLine);
                        //out.println("new dataLine(size)="+newDataLine.size());

                        dataLine = newDataLine;

                    } // end-if: stringContinuousVar-exist case

                    for (int el=0; el< dataLine.size(); el++){
                        dataLine2.add(new String(dataLine.get(el)));
                    }

                    // caseIndex starts from 1 not 0
                    caseIndex = (ii*OBS + i + 1)/nOBS;
                    //dbgLog.finer("caseIndex="+caseIndex);
                    
                    for (int k=0; k<dataLine.size(); k++){
                        
                        dbgLog.fine("k="+k+"-th variableTypelList="+variableTypelList.get(k));

                        if (variableTypelList.get(k)>0){
                            // String variable case: set to  -1
                            variableTypeFinal[k] = -1;
                            
                            // shorten the string up to its length (no padding)
                            
                            if ((dataLine.get(k)).toString().length() > variableTypelList.get(k)){
                            
                                dataLine.set(k,
                                    (StringUtils.substring(dataLine.get(k).toString(), 0,
                                    variableTypelList.get(k))));
                                    
                            } else if (variableTypelList.get(k) < dataLine.get(k).toString().length() ){
                                throw new IOException("string-reading error: at "+k+"th variable");
                            }
                            
                            // check '.' dot
                            String trimmed = StringUtils.strip(dataLine.get(k).toString());
                            //dbgLog.fine("trimed="+trimmed);
                            
                            if (trimmed.equals(".")){
                                // missing value
                                // old dataLine.set(k, StringMissingValue);
                                dataLine.set(k, MissingValueForTextDataFileString);

                                // add this index to NaN-to-NA-replacement sentinel
                                NaNlocationString.add(k);
                            } else if (dataLine.get(k).equals(" ") && trimmed.equals("")) {
                                // space-missing value
                                // old dataLine.set(k, StringMissingValue);
                                dataLine.set(k, MissingValueForTextDataFileString);

                                // add this index to NaN-to-NA-replacement sentinel
                                NaNlocationString.add(k);
                            } else {
                                // padding removed
                                String paddRemoved = StringUtils.stripEnd(dataLine.get(k).toString(), null);
                                //dbgLog.fine("paddRemoved="+paddRemoved);
                                dataLine.set(k,paddRemoved);
                            } 
                            
                            // deep-copy the above change to dataLine2 for stats
                            dataLine2.set(k,new String(dataLine.get(k)));
                            
                            // end of String var case
                        
                        } else {
                            // numeric var case
                            if (dataLine.get(k).equals(MissingValueForTextDataFileNumeric)){
                                // out.println("NA-N k= "+k+"=["+dataLine.get(k)+"]");
                                // add this index to NaN-to-NA-replacement sentinel
                                NaNlocationNumeric.add(k);
                            }
                            
                        } // end of variable-type check
                        
                        
                        //out.println("NaNlocationNumeric="+NaNlocationNumeric);
                        //out.println("NaNlocationString="+NaNlocationString);

                        
                        // old if (!dataLine.get(k).equals("NaN")){
                        if (!dataLine.get(k).equals(MissingValueForTextDataFileNumeric)){
                        
// to do date conversion
                            String variableFormatType = variableFormatTypeList[k];
                            dbgLog.finer("k="+k+"th printFormatTable format="+printFormatTable.get(variableNameList.get(k)));
                            
                            int formatDecimalPointPosition = formatDecimalPointPositionList.get(k);

                            
                            if (variableFormatType.equals("date")){
                                dbgLog.finer("date case");
                                
                                long dateDatum = Long.parseLong(dataLine.get(k).toString())*1000L- SPSS_DATE_OFFSET;
                                
                                String newDatum = sdf_ymd.format(new Date(dateDatum));
                                dbgLog.finer("k="+k+":"+newDatum);
                                
                                dataLine.set(k, newDatum);
                                //formatCategoryTable.put(variableNameList.get(k), "date");
                            } else if (variableFormatType.equals("time")) {
                                dbgLog.finer("time case:DTIME or DATETIME or TIME");
                                //formatCategoryTable.put(variableNameList.get(k), "time");
                            
                                if (printFormatTable.get(variableNameList.get(k)).equals("DTIME")){

                                    if (dataLine.get(k).toString().indexOf(".") < 0){
                                        long dateDatum  = Long.parseLong(dataLine.get(k).toString())*1000L - SPSS_DATE_BIAS;
                                        String newDatum = sdf_dhms.format(new Date(dateDatum));
                                        dbgLog.finer("k="+k+":"+newDatum);
                                        dataLine.set(k, newDatum);
                                    } else {
                                        // decimal point included
                                        String[] timeData = dataLine.get(k).toString().split("\\.");

                                        dbgLog.finer(StringUtils.join(timeData, "|"));
                                        long dateDatum = Long.parseLong(timeData[0])*1000L - SPSS_DATE_BIAS;
                                        StringBuilder sb_time = new StringBuilder(
                                            sdf_dhms.format(new Date(dateDatum)));
                                        dbgLog.finer(sb_time.toString());
                                        
                                        if (formatDecimalPointPosition > 0){
                                            sb_time.append("."+timeData[1].substring(0,formatDecimalPointPosition));
                                        }
                                        
                                        dbgLog.finer("k="+k+":"+sb_time.toString());
                                        dataLine.set(k, sb_time.toString());
                                    }
                                } else if (printFormatTable.get(variableNameList.get(k)).equals("DATETIME")){

                                    if (dataLine.get(k).toString().indexOf(".") < 0){
                                        long dateDatum  = Long.parseLong(dataLine.get(k).toString())*1000L - SPSS_DATE_OFFSET;
                                        String newDatum = sdf_ymdhms.format(new Date(dateDatum));
                                        dbgLog.finer("k="+k+":"+newDatum);
                                        dataLine.set(k, newDatum);
                                    } else {
                                        // decimal point included
                                        String[] timeData = dataLine.get(k).toString().split("\\.");

                                        //dbgLog.finer(StringUtils.join(timeData, "|"));
                                        long dateDatum = Long.parseLong(timeData[0])*1000L- SPSS_DATE_OFFSET;
                                        StringBuilder sb_time = new StringBuilder(
                                            sdf_ymdhms.format(new Date(dateDatum)));
                                        //dbgLog.finer(sb_time.toString());
                                        
                                        if (formatDecimalPointPosition > 0){
                                            sb_time.append("."+timeData[1].substring(0,formatDecimalPointPosition));
                                        }
                                        
                                        dbgLog.finer("k="+k+":"+sb_time.toString());
                                        dataLine.set(k, sb_time.toString());
                                    }
                                } else if (printFormatTable.get(variableNameList.get(k)).equals("TIME")){
                                    if (dataLine.get(k).toString().indexOf(".") < 0){
                                        long dateDatum = Long.parseLong(dataLine.get(k).toString())*1000L;
                                        String newDatum = sdf_hms.format(new Date(dateDatum));
                                        dbgLog.finer("k="+k+":"+newDatum);
                                        dataLine.set(k, newDatum);
                                    } else {
                                        // decimal point included
                                        String[] timeData = dataLine.get(k).toString().split("\\.");

                                        //dbgLog.finer(StringUtils.join(timeData, "|"));
                                        long dateDatum = Long.parseLong(timeData[0])*1000L;
                                        StringBuilder sb_time = new StringBuilder(
                                            sdf_hms.format(new Date(dateDatum)));
                                        //dbgLog.finer(sb_time.toString());
                                        
                                        if (formatDecimalPointPosition > 0){
                                            sb_time.append("."+timeData[1].substring(0,formatDecimalPointPosition));
                                        }
                                        
                                        dbgLog.finer("k="+k+":"+sb_time.toString());
                                        dataLine.set(k, sb_time.toString());
                                    }
                                }
                            
                            } else if (variableFormatType.equals("other")){
                                dbgLog.finer("other non-date/time case:="+i);
                            
                                if (printFormatTable.get(variableNameList.get(k)).equals("WKDAY")){
                                    // day of week
                                    dbgLog.finer("data k="+k+":"+dataLine.get(k));
                                    dbgLog.finer("data k="+k+":"+SPSSConstants.WEEKDAY_LIST.get(Integer.valueOf(dataLine.get(k).toString())-1));
                                    dataLine.set(k, SPSSConstants.WEEKDAY_LIST.get(Integer.valueOf(dataLine.get(k).toString())-1));
                                    dbgLog.finer("wkday:k="+k+":"+dataLine.get(k));
                                } else if (printFormatTable.get(variableNameList.get(k)).equals("MONTH")){
                                    // month
                                    dbgLog.finer("data k="+k+":"+dataLine.get(k));
                                    dbgLog.finer("data k="+k+":"+SPSSConstants.MONTH_LIST.get(Integer.valueOf(dataLine.get(k).toString())-1));
                                    dataLine.set(k, SPSSConstants.MONTH_LIST.get(Integer.valueOf(dataLine.get(k).toString())-1));
                                    dbgLog.finer("month:k="+k+":"+dataLine.get(k));
                                }
                            } 

                        
                        } // end: date-time-datum check
                    
                    
                    } // end: loop-k(2nd: variable-wise-check)
                    
                    // dump the line to the tab-delimited file first
                    // then replace the missing value token with those for
                    // calculating UNF/summary statistic
                    // dbgLog.finer("tab-data: caseIndex="+caseIndex+"-th:"+StringUtils.join(dataLine, "\t"));

                    
                    pwout.println(StringUtils.join(dataLine, "\t"));
                    
                    // replace NA-strings for a tab-limited file with
                    // those for UNF/summaryStatistics 
                    // numeric variable
                    
                    if (NaNlocationNumeric.size() > 0){
                        // NA-String to NaN conversion
                        for (int el : NaNlocationNumeric){
                            //out.println("replaced="+el+"th element="+dataLine.get(el));
                            if (dataLine.get(el).equals(MissingValueForTextDataFileNumeric)){
                            
                                if (variableFormatTypeList[el].equals("date") ||
                                    variableFormatTypeList[el].equals("time")){
                                    dataLine.set(el, MissingValueForTextDataFileString);
                                } else{
                                    dataLine2.set(el, Double.toHexString(systemMissingValue));
                                    //out.println("replaced="+el+"th element="+dataLine.get(el));

                                }
                            }

                        }
                        //out.println(caseIndex+"-th case:(after NA processing[N]):"+StringUtils.join(dataLine, "\t"));

                    }
                    // string variable
                    if (NaNlocationString.size() > 0){
                        // NaN-String to NaN conversion
                        for (int el : NaNlocationString){

                            if (dataLine.get(el).equals(MissingValueForTextDataFileString)){
                               dataLine2.set(el, Double.toHexString(systemMissingValue));
                                //out.println("replaced="+el+"th element="+dataLine.get(el));
                            }
                        }
                        //out.println(caseIndex+"-th case:(after NA processing[S]):"+StringUtils.join(dataLine, "\t"));

                    }
                    
                    
                    //out.println(caseIndex+"-th case:(after NA processing):"+StringUtils.join(dataLine, "\t")+"\n\n");
                    
                    
                    
                    for (int ij=0; ij<varQnty;ij++ ){
                        if (variableFormatTypeList[ij].equals("date") ||
                            variableFormatTypeList[ij].equals("time")){
                            dataTable2[ij][caseIndex-1] = dataLine.get(ij);
                        } else {
                            dataTable2[ij][caseIndex-1] = dataLine2.get(ij);
                        }
                    }
                    
                    // numeric contents-check
                    for (int l=0;l< dataLine.size();l++){
                        if (variableTypeFinal[l]==0){
                            if (dataLine.get(l).toString().indexOf(".") >=0){
                                // l-th variable is not integer
                                variableTypeFinal[l]=1;
                                numberOfDecimalVariables++;
                            }
                        }
                    }
                    
                    // reset the case-wise working objects

                    NaNlocationNumeric.clear();
                    NaNlocationString.clear();
                    dataLine.clear();
                    dataLine2.clear();
                    if (hasReachedEOF){
                        break;
                    }
                } // if(The-end-of-a-case(row)-processing)
                
                

            } // loop-i (OBS unit)
            
            if ( (hasReachedEOF) ||(stream.available() == 0)){
                // reached the end of this file
                // do exit-processing

                // check the actual number of cases
                if (caseQnty == -1){
                    // caseQnty is unknown case
                    caseQnty = caseIndex;

                } else if (caseQnty == caseIndex){
                    // header's case information was correct
                    dbgLog.fine("recorded number of cases is the same as the actual number");
                } else {
                    // header's case information disagree with the actual one
                    // take the actual one
                    caseQnty = caseIndex;
                }


                dbgLog.fine("***** reached the end of the file at "+ii
                    +"th iteration *****");


                break OBSERVATION;
            }

            ii++;
            
        } // while loop

            pwout.close();
        } catch (IOException ex) {
           
        }
        
        // save a decimal-type variable's number
        for (int l=0; l< variableTypeFinal.length;l++){
            if (variableTypeFinal[l]>0){
                decimalVariableSet.add(l);
            }
            
            if (variableFormatTypeList[l].equals("date") || 
                variableFormatTypeList[l].equals("time")){
                variableTypeFinal[l]=-1;
            }
        }
        smd.setDecimalVariables(decimalVariableSet);
        smd.getFileInformation().put("caseQnty", caseQnty);

        // contents check
        //out.println("variableType="+ArrayUtils.toString(variableTypeFinal));
        dbgLog.fine("numberOfDecimalVariables="+numberOfDecimalVariables);
        dbgLog.fine("decimalVariableSet="+decimalVariableSet);
        //out.println("variableTypelList=\n"+ variableTypelList.toString());

        // out.println("dataTable2:\n"+Arrays.deepToString(dataTable2));
        dbgLog.fine("***** decodeRecordTypeDataCompressed(): end *****");
    }


    void decodeRecordTypeDataUnCompressed(BufferedInputStream stream){
        dbgLog.fine("***** decodeRecordTypeDataUnCompressed(): start *****");

        if (stream ==null){
            throw new IllegalArgumentException("decodeRecordTypeDataUnCompressed: stream == null!");
        }
        // 
        // set-up tab file
        
        FileOutputStream fileOutTab = null;
        PrintWriter pwout = null;
        
        try {
            // create a File object to save the tab-delimited data file
            File tabDelimitedDataFile = File.createTempFile("tempTabfile.", ".tab");

            String tabDelimitedDataFileName   = tabDelimitedDataFile.getAbsolutePath();

            // save the temp file name in the metadata object
            smd.getFileInformation().put("tabDelimitedDataFileLocation", tabDelimitedDataFileName);

            fileOutTab = new FileOutputStream(tabDelimitedDataFile);
            
            pwout = new PrintWriter(new OutputStreamWriter(fileOutTab, "utf8"), true);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        }
        
        boolean hasStringVarContinuousBlock = 
            obsNonVariableBlockSet.size() > 0 ? true : false;
        dbgLog.fine("hasStringVarContinuousBlock="+hasStringVarContinuousBlock);
        
        int ii = 0;
        
        int OBS = LENGTH_SAV_OBS_BLOCK;
        int nOBS = OBSUnitsPerCase;
        
        dbgLog.fine("OBSUnitsPerCase="+OBSUnitsPerCase);
        
        int caseIndex = 0;
        
        
        out.println("printFormatTable:\n"+printFormatTable);

        out.println("printFormatNameTable:\n"+printFormatNameTable);
        variableFormatTypeList = new String[varQnty];

        for (int i=0; i<varQnty;i++){
            variableFormatTypeList[i]=SPSSConstants.FORMAT_CATEGORY_TABLE.get(
                    printFormatTable.get(variableNameList.get(i)));
            out.println("i="+i+"th variableFormatTypeList="+variableFormatTypeList[i]);
            formatCategoryTable.put(variableNameList.get(i), variableFormatTypeList[i]);
        }
        out.println("variableFormatType:\n"+Arrays.deepToString(variableFormatTypeList));
        out.println("formatCategoryTable:\n"+formatCategoryTable);

        // contents (variable) checker concering decimals
        variableTypeFinal = new int[varQnty];
        Arrays.fill(variableTypeFinal, 0);

        int numberOfDecimalVariables = 0;
        
        List<String> dataLine = new ArrayList<String>();
        List<String>  dataLine2 = new ArrayList<String>();
        
        // Sets for NA-string-to-NaN conversion
        Set<Integer> NaNlocationNumeric = new LinkedHashSet<Integer>();
        Set<Integer> NaNlocationString = new LinkedHashSet<Integer>();
        
        // data-storage object for sumStat
        dataTable2 = new Object[varQnty][caseQnty];


        try {
            for (int i=0; ;i++){  // case-wise loop
                
                byte[] buffer = new byte[OBS*nOBS];
                
                int nbytesuc =  stream.read(buffer);
                
                StringBuilder sb_stringStorage = new StringBuilder("");

                for (int k=0;k<nOBS; k++){
                    int offset= OBS*k;

                    // uncompressed case
                    // numeric missing value == sysmis
                    // FF FF FF FF FF FF eF FF(little endian)
                    // string missing value
                    // 20 20 20 20 20 20 20 20
                    // cf: compressed case 
                    // numeric type:sysmis == 0xFF
                    // string type: missing value == 0xFE
                    // 

                    boolean isNumeric = OBSwiseTypelList.get(k)==0 ? true : false;
                    
                    if (isNumeric){
                        dbgLog.finer(k+"-th variable is numeric");
                        // interprete as double
                        ByteBuffer bb_double = ByteBuffer.wrap(
                            buffer, offset , LENGTH_SAV_OBS_BLOCK);
                        if (isLittleEndian){
                            bb_double.order(ByteOrder.LITTLE_ENDIAN);
                        }
                        //char[] hexpattern =
                        String dphex = new String(Hex.encodeHex(
                                Arrays.copyOfRange(bb_double.array(),
                                offset, offset+LENGTH_SAV_OBS_BLOCK)));
                        dbgLog.finer("dphex="+ dphex);
                            
                        //if (dphex.equals(OBStypeIfomation.get("SYSMIS"))){
                        if ((dphex.equals("ffffffffffffefff"))||
                            (dphex.equals("ffefffffffffffff"))){
                            //dataLine.add(systemMissingValue);
                            // add the numeric missing value
                            dataLine.add(MissingValueForTextDataFileNumeric);
                        } else {
                            Double ddatum  = bb_double.getDouble();
                            dbgLog.finer("ddatum="+ddatum);

                            // add this non-missing-value numeric datum
                            dataLine.add(doubleNumberFormatter.format(ddatum)) ;
                        }
                    
                    } else {
                        dbgLog.finer(k+"-th variable is string");
                        // string case
                        // strip space-padding
                        // do not trim: string might have spaces within it
                        // the missing value (hex) for a string variable is:
                        // "20 20 20 20 20 20 20 20"
                        
                        
                        String strdatum = new String(
                            Arrays.copyOfRange(buffer,
                            offset, (offset+LENGTH_SAV_OBS_BLOCK)),"US-ASCII");
                        dbgLog.finer("str_datum="+strdatum);
                        // add this non-missing-value string datum 
                        dataLine.add(strdatum);

                    } // if isNumeric
                

                
                } // k-loop
                
                //dbgLog.fine("all variables in a case(row) are parsed == nOBS");
                //dbgLog.fine("hasStringVarContinuousBlock="+hasStringVarContinuousBlock);

                // out.println("i="+i+"th dataLine(before)="+dataLine);

                // String-variable's continuous block exits
                if (hasStringVarContinuousBlock){
                // continuous blocks: string case
                // concatenating process
                    //dbgLog.fine("concatenating process starts");

                    //dbgLog.fine("dataLine(before)="+dataLine);
                    //dbgLog.fine("dataLine(before:size)="+dataLine.size());

                    StringBuilder sb = new StringBuilder("");
                    int firstPosition = 0;

                    Set<Integer> removeJset = new HashSet<Integer>();
                    for (int j=0; j< nOBS; j++){
                        dbgLog.finer("j="+j+"-th type ="+OBSwiseTypelList.get(j));
                        if (OBSwiseTypelList.get(j) == -1){
                            // String continued fount at j-th 
                            // look back the j-1 
                            firstPosition = j-1;
                            int lastJ = j;
                            String concatanated = null;

                            removeJset.add(j);
                            sb.append(dataLine.get(j-1));
                            sb.append(dataLine.get(j));
                            for (int jc =1; ; jc++ ){
                                if (OBSwiseTypelList.get(j+jc) != -1){
                                // j is the end unit of this string variable
                                    concatanated = sb.toString();
                                    sb.setLength(0);
                                   lastJ = j+jc;
                                   break;
                                } else {
                                    sb.append(dataLine.get(j+jc));
                                    removeJset.add(j+jc);
                                }
                            }
                            dataLine.set(j-1, concatanated); 

                            //out.println(j-1+"th concatanated="+concatanated);
                            j = lastJ -1; 

                        } // end-of-if: continuous-OBS only
                    } // end of loop-j

                    //out.println("removeJset="+removeJset);
                    // a new list that stores a new case with concatanated string data
                    List<String> newDataLine = new ArrayList<String>();
                    
                    for (int jl=0; jl<dataLine.size();jl++){
                        //out.println("jl="+jl+"-th datum =["+dataLine.get(jl)+"]");
                        
                        if (!removeJset.contains(jl) ){
                        
//                            if (dataLine.get(jl).equals(MissingValueForTextDataFileString)){
//                                out.println("NA-S jl= "+jl+"=["+dataLine.get(jl)+"]");
//                            } else if (dataLine.get(jl).equals(MissingValueForTextDataFileNumeric)){
//                                out.println("NA-N jl= "+jl+"=["+dataLine.get(jl)+"]");
//                            } else if (dataLine.get(jl)==null){
//                                out.println("null case jl="+jl+"=["+dataLine.get(jl)+"]");
//                            } else if (dataLine.get(jl).equals("NaN")){
//                                out.println("NaN jl= "+jl+"=["+dataLine.get(jl)+"]");
//                            } else if (dataLine.get(jl).equals("")){
//                                out.println("blank jl= "+jl+"=["+dataLine.get(jl)+"]");
//                            } else if (dataLine.get(jl).equals(" ")){
//                                out.println("space jl= "+jl+"=["+dataLine.get(jl)+"]");
//                            }
                                
                            newDataLine.add(dataLine.get(jl));
                        } else {
//                            out.println("Excluded: jl="+jl+"-th datum=["+dataLine.get(jl)+"]");
                        }

                    }

                    dbgLog.fine("new dataLine="+newDataLine);
                    dbgLog.fine("new dataLine(size)="+newDataLine.size());
                    
                    dataLine = newDataLine;

                } // end-if: stringContinuousVar-exist case

                for (int el=0; el< dataLine.size(); el++){
                    dataLine2.add(new String(dataLine.get(el)));
                }
                
                caseIndex++;
                dbgLog.finer("caseIndex="+caseIndex);
                
                for (int k=0; k<dataLine.size(); k++){
                    
                    //dbgLog.fine("k="+k+"-th variableTypelList="+variableTypelList.get(k));

                    if (variableTypelList.get(k)>0){
                        // String variable case: set to  -1
                        variableTypeFinal[k] = -1;

                        // shorten the string up to its length (no padding)

                        if ((dataLine.get(k)).toString().length() > variableTypelList.get(k)){
                            dataLine.set(k,
                                (StringUtils.substring(dataLine.get(k).toString(), 0,
                                variableTypelList.get(k))));
                        } else if (variableTypelList.get(k) < dataLine.get(k).toString().length() ){
                            throw new IOException("string-reading error: at "+k+"th variable");
                        }

                        // check '.' dot
                        String trimmed = StringUtils.strip(dataLine.get(k).toString());
                        //dbgLog.fine("trimed="+trimmed);

                        if (trimmed.equals(".")){
                            // missing value
                            // old dataLine.set(k, StringMissingValue);
                            dataLine.set(k, MissingValueForTextDataFileString);

                            // add this index to NaN-to-NA-replacement sentinel
                            NaNlocationString.add(k);

                         } else if (dataLine.get(k).equals(" ") && trimmed.equals("")) {
                            // space-missing value
                            // old dataLine.set(k, StringMissingValue);
                            dataLine.set(k, MissingValueForTextDataFileString);

                            // add this index to NaN-to-NA-replacement sentinel
                            NaNlocationString.add(k);

                        } else {
                            // padding removed
                            String paddRemoved = StringUtils.stripEnd(dataLine.get(k).toString(), null);
                            //dbgLog.fine("paddRemoved="+paddRemoved);
                            dataLine.set(k,paddRemoved);
                        }
                        
                        // deep-copy the above change to dataLine2 for stats
                        dataLine2.set(k,new String(dataLine.get(k)));
                        
                        // end of String var case

                    } else {
                        // numeric var case
                        if (dataLine.get(k).equals(MissingValueForTextDataFileNumeric)){
                            //out.println("NA-N k= "+k+"=["+dataLine.get(k)+"]");
                            // add this index to NaN-to-NA-replacement sentinel
                            NaNlocationNumeric.add(k);
                        }
                    
                    } // end of variable-type check

                    //out.println("NaNlocationNumeric="+NaNlocationNumeric);
                    //out.println("NaNlocationString="+NaNlocationString);

                    
                    if (!dataLine.get(k).equals(MissingValueForTextDataFileNumeric)){
                        
// to do date conversion
                        String variableFormatType =  variableFormatTypeList[k];
                        dbgLog.finer("k="+k+"th variable format="+variableFormatType);
                        
                        int formatDecimalPointPosition = formatDecimalPointPositionList.get(k);


                        if (variableFormatType.equals("date")){
                            dbgLog.finer("date case");

                            long dateDatum = Long.parseLong(dataLine.get(k).toString())*1000L- SPSS_DATE_OFFSET;

                            String newDatum = sdf_ymd.format(new Date(dateDatum));
                            dbgLog.finer("k="+k+":"+newDatum);

                            dataLine.set(k, newDatum);
                            //formatCategoryTable.put(variableNameList.get(k), "date");
                        } else if (variableFormatType.equals("time")) {
                            dbgLog.finer("time case:DTIME or DATETIME or TIME");
                            //formatCategoryTable.put(variableNameList.get(k), "time");
                            
                            if (printFormatTable.get(variableNameList.get(k)).equals("DTIME")){

                                if (dataLine.get(k).toString().indexOf(".") < 0){
                                    long dateDatum  = Long.parseLong(dataLine.get(k).toString())*1000L - SPSS_DATE_BIAS;
                                    String newDatum = sdf_dhms.format(new Date(dateDatum));
                                    dbgLog.finer("k="+k+":"+newDatum);
                                    dataLine.set(k, newDatum);
                                } else {
                                    // decimal point included
                                    String[] timeData = dataLine.get(k).toString().split("\\.");

                                    dbgLog.finer(StringUtils.join(timeData, "|"));
                                    long dateDatum = Long.parseLong(timeData[0])*1000L - SPSS_DATE_BIAS;
                                    StringBuilder sb_time = new StringBuilder(
                                        sdf_dhms.format(new Date(dateDatum)));
                                    
                                    if (formatDecimalPointPosition > 0){
                                        sb_time.append("."+timeData[1].substring(0,formatDecimalPointPosition));
                                    }
                                    
                                    
                                    dbgLog.finer("k="+k+":"+sb_time.toString());
                                    dataLine.set(k, sb_time.toString());
                                }
                            } else if (printFormatTable.get(variableNameList.get(k)).equals("DATETIME")){

                                if (dataLine.get(k).toString().indexOf(".") < 0){
                                    long dateDatum  = Long.parseLong(dataLine.get(k).toString())*1000L - SPSS_DATE_OFFSET;
                                    String newDatum = sdf_ymdhms.format(new Date(dateDatum));
                                    dbgLog.finer("k="+k+":"+newDatum);
                                    dataLine.set(k, newDatum);
                                } else {
                                    // decimal point included
                                    String[] timeData = dataLine.get(k).toString().split("\\.");

                                    //dbgLog.finer(StringUtils.join(timeData, "|"));
                                    long dateDatum = Long.parseLong(timeData[0])*1000L- SPSS_DATE_OFFSET;
                                    StringBuilder sb_time = new StringBuilder(
                                        sdf_ymdhms.format(new Date(dateDatum)));
                                    //dbgLog.finer(sb_time.toString());
                                    
                                    if (formatDecimalPointPosition > 0){
                                        sb_time.append("."+timeData[1].substring(0,formatDecimalPointPosition));
                                    }
                                    
                                    dbgLog.finer("k="+k+":"+sb_time.toString());
                                    dataLine.set(k, sb_time.toString());
                                }
                            } else if (printFormatTable.get(variableNameList.get(k)).equals("TIME")){
                                if (dataLine.get(k).toString().indexOf(".") < 0){
                                    long dateDatum = Long.parseLong(dataLine.get(k).toString())*1000L;
                                    String newDatum = sdf_hms.format(new Date(dateDatum));
                                    dbgLog.finer("k="+k+":"+newDatum);
                                    dataLine.set(k, newDatum);
                                } else {
                                    // decimal point included
                                    String[] timeData = dataLine.get(k).toString().split("\\.");

                                    //dbgLog.finer(StringUtils.join(timeData, "|"));
                                    long dateDatum = Long.parseLong(timeData[0])*1000L;
                                    StringBuilder sb_time = new StringBuilder(
                                        sdf_hms.format(new Date(dateDatum)));
                                    //dbgLog.finer(sb_time.toString());
                                    
                                    if (formatDecimalPointPosition > 0){
                                        sb_time.append("."+timeData[1].substring(0,formatDecimalPointPosition));
                                    }
                                    
                                    dbgLog.finer("k="+k+":"+sb_time.toString());
                                    dataLine.set(k, sb_time.toString());
                                }
                            }
                            
                            

                        } else if (variableFormatType.equals("other")){
                            dbgLog.finer("other non-date/time case");

                            if (printFormatTable.get(variableNameList.get(k)).equals("WKDAY")){
                                // day of week
                                dbgLog.finer("data k="+k+":"+dataLine.get(k));
                                dbgLog.finer("data k="+k+":"+SPSSConstants.WEEKDAY_LIST.get(Integer.valueOf(dataLine.get(k).toString())-1));
                                dataLine.set(k, SPSSConstants.WEEKDAY_LIST.get(Integer.valueOf(dataLine.get(k).toString())-1));
                                dbgLog.finer("wkday:k="+k+":"+dataLine.get(k));
                            } else if (printFormatTable.get(variableNameList.get(k)).equals("MONTH")){
                                // month
                                dbgLog.finer("data k="+k+":"+dataLine.get(k));
                                dbgLog.finer("data k="+k+":"+SPSSConstants.MONTH_LIST.get(Integer.valueOf(dataLine.get(k).toString())-1));
                                dataLine.set(k, SPSSConstants.MONTH_LIST.get(Integer.valueOf(dataLine.get(k).toString())-1));
                                dbgLog.finer("month:k="+k+":"+dataLine.get(k));
                                
                                
                                
                            }
                        } 
// end of date/time block
                    } // end: date-time-datum check

                } // end: loop-k(2nd: variablte-wise-check)



                // dump the line to the tab-delimited file first
                // then replace the missing value token with those for
                // calculating UNF/summary statistic
                // dbgLog.finer("tab-data: caseIndex="+caseIndex+"-th:"+StringUtils.join(dataLine, "\t"));

                pwout.println(StringUtils.join(dataLine, "\t"));
                
                // replace NA-strings for a tab-limited file with
                // those for UNF/summaryStatistics 
                // numeric variable
                
                if (NaNlocationNumeric.size() > 0){
                    // NA-String to NaN conversion
                    for (int el : NaNlocationNumeric){
                        //out.println("replaced="+el+"th element="+dataLine.get(el));
                        if (dataLine.get(el).equals(MissingValueForTextDataFileNumeric)){
                        
                            if (variableFormatTypeList[el].equals("date") ||
                                variableFormatTypeList[el].equals("time")){
                                dataLine.set(el, MissingValueForTextDataFileString);
                            } else{
                                dataLine2.set(el, Double.toHexString(systemMissingValue));
                                //out.println("replaced="+el+"th element="+dataLine.get(el));
                            }
                        }

                    }
                    //out.println(caseIndex+"-th case:(after NA processing[N]):"+StringUtils.join(dataLine, "\t"));

                }
                // string variable
                if (NaNlocationString.size() > 0){
                    // NaN-String to NaN conversion
                    for (int el : NaNlocationString){

                        if (dataLine.get(el).equals(MissingValueForTextDataFileString)){
                           dataLine2.set(el, Double.toHexString(systemMissingValue));
                            //out.println("replaced="+el+"th element="+dataLine.get(el));                            

                        }
                    }
                    //out.println(caseIndex+"-th case:(after NA processing[S]):"+StringUtils.join(dataLine, "\t"));
                }
                
                //out.println(caseIndex+"-th case:(after NA processing):"+StringUtils.join(dataLine, "\t")+"\n\n");
                
                
                for (int ij=0; ij<varQnty;ij++ ){
                    if (variableFormatTypeList[ij].equals("date") ||
                        variableFormatTypeList[ij].equals("time")){
                        dataTable2[ij][caseIndex-1] = dataLine.get(ij);
                    } else {
                        dataTable2[ij][caseIndex-1] = dataLine2.get(ij);
                    }
                }
                
                // numeric contents-check
                for (int l=0;l< dataLine.size();l++){
                    if (variableTypeFinal[l]==0){
                        if (dataLine.get(l).toString().indexOf(".") >=0){
                            // l-th variable is not integer
                            variableTypeFinal[l]=1;
                            numberOfDecimalVariables++;
                        }
                    }
                }
                
                // reset the case-wise working objects
                NaNlocationNumeric.clear();
                NaNlocationString.clear();
                dataLine.clear();
                dataLine2.clear();
                
                if (stream.available() == 0){
                    // reached the end of this file
                    // do exit-processing

                    // check the actual number of cases
                    if (caseQnty == -1){
                        // caseQnty is unknown case
                        caseQnty = caseIndex;

                    } else if (caseQnty == caseIndex){
                        // header's case information was correct
                        dbgLog.info("recorded number of cases is the same as the actual number");
                    } else {
                        // header's case information disagree with the actual one
                        // take the actual one
                        dbgLog.info("recorded number of cases in the header is not the same as the actual number:"+caseIndex);
                        caseQnty = caseIndex;
                    }


                    dbgLog.fine("***** reached the end of the file at "+ii
                        +"th iteration *****");

                    break;
                } // if eof processing

            } //i-loop: case(row) iteration

            // close the writer
            pwout.close();
            

        } catch (IOException ex) {
           ex.printStackTrace();
        }


        // save a decimal-type variable's number
        for (int l=0; l< variableTypeFinal.length;l++){
            if (variableTypeFinal[l]>0){
                decimalVariableSet.add(l);
            }
            if (variableFormatTypeList[l].equals("date") || 
                variableFormatTypeList[l].equals("time")){
                variableTypeFinal[l]=-1;
            }
        }
        smd.getFileInformation().put("caseQnty", caseQnty);
        smd.setDecimalVariables(decimalVariableSet);
        
        // contents check
        dbgLog.fine("variableType="+ArrayUtils.toString(variableTypeFinal));
        dbgLog.fine("numberOfDecimalVariables="+numberOfDecimalVariables);
        dbgLog.fine("decimalVariableSet="+decimalVariableSet);

        dbgLog.fine("***** decodeRecordTypeDataUnCompressed(): end *****");
    }





    // Utility Methods  -----------------------------------------------------//



    private int getSAVintAdjustedBlockLength(int rawLength){
        int adjustedLength = rawLength;
        if ((rawLength%LENGTH_SAV_INT_BLOCK ) != 0){
            adjustedLength = 
                LENGTH_SAV_INT_BLOCK*(rawLength/LENGTH_SAV_INT_BLOCK +1) ;
        }
        return adjustedLength;
    }
    
    private int getSAVobsAdjustedBlockLength(int rawLength){
        int adjustedLength = rawLength;
        if ((rawLength%LENGTH_SAV_OBS_BLOCK ) != 0){
            adjustedLength = 
                LENGTH_SAV_OBS_BLOCK*(rawLength/LENGTH_SAV_OBS_BLOCK +1) ;
        }
        return adjustedLength;
    }
    
    
    private int[] parseRT7SubTypefieldHeader(BufferedInputStream stream){
        int length_unit_length = 4;
        int length_number_of_units = 4;
        int storage_size = length_unit_length + length_number_of_units;
        
        int[] headerSection = new int[2];
        
        byte[] byteStorage = new byte[storage_size];

        try{
            int nbytes = stream.read(byteStorage);
            // to-do check against nbytes

            //printHexDump(byteStorage, "RT7:storage");

            ByteBuffer bb_data_type = ByteBuffer.wrap(byteStorage,
                       0, length_unit_length);
            if (isLittleEndian){
                bb_data_type.order(ByteOrder.LITTLE_ENDIAN);
            }

            int unitLength = bb_data_type.getInt();
            dbgLog.fine("parseRT7 SubTypefield: unitLength="+unitLength);

            ByteBuffer bb_number_of_units = ByteBuffer.wrap(byteStorage,
                       length_unit_length, length_number_of_units);
            if (isLittleEndian){
                bb_number_of_units.order(ByteOrder.LITTLE_ENDIAN);
            }

            int numberOfUnits = bb_number_of_units.getInt();
            dbgLog.fine("parseRT7 SubTypefield: numberOfUnits="+numberOfUnits);

            headerSection[0] = unitLength;
            headerSection[1] = numberOfUnits;
            return headerSection;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private void parseRT7SubTypefield(BufferedInputStream stream){
        int length_unit_length = 4;
        int length_number_of_units = 4;
        int storage_size = length_unit_length + length_number_of_units;
        
        int[] headerSection = new int[2];
        
        byte[] byteStorage = new byte[storage_size];

        try{
            int nbytes = stream.read(byteStorage);
            // to-do check against nbytes

            //printHexDump(byteStorage, "RT7:storage");

            ByteBuffer bb_data_type = ByteBuffer.wrap(byteStorage,
                       0, length_unit_length);
            if (isLittleEndian){
                bb_data_type.order(ByteOrder.LITTLE_ENDIAN);
            }

            int unitLength = bb_data_type.getInt();
            dbgLog.fine("parseRT7 SubTypefield: unitLength="+unitLength);

            ByteBuffer bb_number_of_units = ByteBuffer.wrap(byteStorage,
                       length_unit_length, length_number_of_units);
            if (isLittleEndian){
                bb_number_of_units.order(ByteOrder.LITTLE_ENDIAN);
            }

            int numberOfUnits = bb_number_of_units.getInt();
            dbgLog.fine("parseRT7 SubTypefield: numberOfUnits="+numberOfUnits);

            headerSection[0] = unitLength;
            headerSection[1] = numberOfUnits;
            
            for (int i=0; i<numberOfUnits; i++){
                byte[] work = new byte[unitLength];
                
                int nb = stream.read(work);
                dbgLog.finer("raw bytes in Hex:"+ new String(Hex.encodeHex(work)));
                ByteBuffer bb_field = ByteBuffer.wrap(work);
                if (isLittleEndian){
                    bb_field.order(ByteOrder.LITTLE_ENDIAN);
                }
                dbgLog.finer("raw bytes in Hex:"+ new String(Hex.encodeHex(bb_field.array())));
                if (unitLength==4){
                    int fieldData = bb_field.getInt();
                    dbgLog.finer(i+"-th fieldData="+fieldData);
                    dbgLog.finer("fieldData in Hex="+Integer.toHexString(fieldData));
                } else if (unitLength==8){
                    double fieldData = bb_field.getDouble();
                    dbgLog.finer(i+"-th fieldData="+fieldData);
                    dbgLog.finer("fieldData in Hex="+Double.toHexString(fieldData));
                
                }
                dbgLog.finer("");
            }
           
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    private List<byte[]> getRT7SubTypefieldData(BufferedInputStream stream){
        int length_unit_length = 4;
        int length_number_of_units = 4;
        int storage_size = length_unit_length + length_number_of_units;
        List<byte[]> dataList = new ArrayList<byte[]>();
        int[] headerSection = new int[2];
        
        byte[] byteStorage = new byte[storage_size];

        try{
            int nbytes = stream.read(byteStorage);
            // to-do check against nbytes

            //printHexDump(byteStorage, "RT7:storage");

            ByteBuffer bb_data_type = ByteBuffer.wrap(byteStorage,
                       0, length_unit_length);
            if (isLittleEndian){
                bb_data_type.order(ByteOrder.LITTLE_ENDIAN);
            }

            int unitLength = bb_data_type.getInt();
            dbgLog.fine("parseRT7SubTypefield: unitLength="+unitLength);

            ByteBuffer bb_number_of_units = ByteBuffer.wrap(byteStorage,
                       length_unit_length, length_number_of_units);
            if (isLittleEndian){
                bb_number_of_units.order(ByteOrder.LITTLE_ENDIAN);
            }

            int numberOfUnits = bb_number_of_units.getInt();
            dbgLog.fine("parseRT7SubTypefield: numberOfUnits="+numberOfUnits);

            headerSection[0] = unitLength;
            headerSection[1] = numberOfUnits;

            for (int i=0; i<numberOfUnits; i++){

                byte[] work = new byte[unitLength];
                int nb = stream.read(work);
                dbgLog.finer(new String(Hex.encodeHex(work)));
                dataList.add(work);
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return dataList;
    }    
    
    void print2Darray(Object[][] datatable, String title){
        dbgLog.fine(title);
        for (int i=0; i< datatable.length; i++){
            dbgLog.fine(StringUtils.join(datatable[i], "|"));
        }
    }    
    
    
    private String getUNF(Object[] varData, int variableType, 
        String unfVersionNumber, int variablePosition)
        throws NumberFormatException, UnfException,
        IOException, NoSuchAlgorithmException{
        String unfValue = null;


        dbgLog.fine("variableType="+variableType);
        dbgLog.finer("unfVersionNumber="+unfVersionNumber);
        dbgLog.fine("variablePosition="+variablePosition);
        dbgLog.fine("variableName="+variableNameList.get(variablePosition));
        dbgLog.fine("varData:\n"+Arrays.deepToString(varData));

        switch(variableType){
            case 0:
                // Integer case
                // note: due to DecimalFormat class is used to
                // remove an unnecessary decimal point and 0-padding
                // numeric (double) data are now String objects

                dbgLog.fine("Integar case");
                 long[] ldata = new long[varData.length];
                    for (int i=0;i<varData.length;i++){
                        //dbgLog.finer("double: i th case ="+i+" "+varData[i]);
                        if ( (varData[i] == null) ||
                             (varData[i].equals("NaN")) ||
                             (varData[i].equals(""))  )
                        {
                            ldata[i] = Long.MAX_VALUE;
                        } else {
                            ldata[i] = Long.parseLong((String)varData[i]);
                        }
                    }

                unfValue = UNFUtil.calculateUNF(ldata, unfVersionNumber);
                dbgLog.finer("integer:unfValue="+unfValue);

                dbgLog.finer("sumstat:long case="+Arrays.deepToString(
                        ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ldata))));

                smd.getSummaryStatisticsTable().put(variablePosition,
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ldata)));


                Map<String, Integer> catStat = StatHelper.calculateCategoryStatistics(ldata);
                //out.println("catStat="+catStat);

                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), catStat);

                break;

            case 1:
                // double case
                // note: due to DecimalFormat class is used to
                // remove an unnecessary decimal point and 0-padding
                // numeric (double) data are now String objects

                dbgLog.finer("double case");
                 double[] ddata = new double[varData.length];
                    for (int i=0;i<varData.length;i++){
                        //dbgLog.finer("double: i th case ="+i+" "+varData[i]);
                        if ( (varData[i] == null) ||
                             (varData[i].equals("NaN")) ||
                             (varData[i].equals(""))  )
                        {
                            ddata[i] = Double.NaN;
                        } else {
                            ddata[i] =  Double.parseDouble((String)varData[i]);
                        }
                    }

                unfValue = UNFUtil.calculateUNF(ddata, unfVersionNumber);
                dbgLog.finer("double:unfValue="+unfValue);
                smd.getSummaryStatisticsTable().put(variablePosition,
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ddata)));

                break;
            case  -1:
                // String case
                dbgLog.finer("string case");
                //out.println("string data="+Arrays.deepToString(varData));
                    for (int i=0;i<varData.length;i++){
                        //dbgLog.fine("string: i ="+i+" "+varData[i]);
                        if (varData[i] == null || ((String)varData[i]).equals("")){
                            varData[i] = "";
                        }
                    }
                String[] strdata = Arrays.asList(varData).toArray(
                    new String[varData.length]);
                
                unfValue = UNFUtil.calculateUNF(strdata, unfVersionNumber);
                dbgLog.finer("string:unfValue="+unfValue);

                smd.getSummaryStatisticsTable().put(variablePosition,
                    StatHelper.calculateSummaryStatistics(strdata));

                Map<String, Integer> StrCatStat = StatHelper.calculateCategoryStatistics(strdata);
                //out.println("catStat="+StrCatStat);
                
                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), StrCatStat);

                break;
            default:
                dbgLog.fine("unknown variable type found");
                String errorMessage =
                    "unknow variable Type found at varData section";
                    throw new IllegalArgumentException(errorMessage);

        } // switch

        dbgLog.fine("unfvalue(last)="+unfValue);
        return unfValue;
    }


}
