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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.dta;



import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.*;

import static java.lang.System.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.regex.*;
import java.text.*;

import org.apache.commons.lang.*;
import org.apache.commons.lang.builder.*;
import org.apache.commons.io.*;
import org.apache.commons.io.input.*;
import org.apache.commons.codec.binary.Hex;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;
import edu.harvard.iq.dvn.unf.*;



import edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util.*;


/**
 * A DVN-Project-implementation of <code>StatDataFileReader</code> for the 
 * Stata DTA format.
 *
 * @author Akio Sone at UNC-Odum
 */
public class DTAFileReader extends StatDataFileReader{

    // static fields ---------------------------------------------------------//

    private static Map<Integer, String> STATA_RELEASE_NUMBER = 
            new HashMap<Integer, String>();
    private static Map<String, Integer> release105type = new LinkedHashMap<String, Integer>();
    private static Map<String, Integer> release111type = new LinkedHashMap<String, Integer>();

    private static Map<Integer, Map<String, Integer>> CONSTATNT_TABLE =
            new LinkedHashMap<Integer, Map<String, Integer>>();

    private static Map<String, Integer> release104constant =
                                        new LinkedHashMap<String, Integer>();
                                        
    private static Map<String, Integer> release105constant =
                                        new LinkedHashMap<String, Integer>();
                                        
    private static Map<String, Integer> release108constant =
                                        new LinkedHashMap<String, Integer>();
                                        
    private static Map<String, Integer> release110constant =
                                        new LinkedHashMap<String, Integer>();
                                        
    private static Map<String, Integer> release111constant =
                                        new LinkedHashMap<String, Integer>();
                                        
    private static Map<String, Integer> release113constant =
                                        new LinkedHashMap<String, Integer>();
                                        
    private static Map<String, Integer> release114constant =
                                        new LinkedHashMap<String, Integer>();
                                        
    private static Map<Byte, Integer> byteLengthTable105 = 
                                        new HashMap<Byte, Integer>();
    private static Map<Byte, Integer> byteLengthTable111 = 
                                        new HashMap<Byte, Integer>();
                                        
    private static Map<Byte, String> variableTypeTable105 = 
                                        new LinkedHashMap<Byte, String>();
    private static Map<Byte, String> variableTypeTable111 = 
                                        new LinkedHashMap<Byte, String>();
    
    private static Map<String, Integer> variableTypeMap =
        new LinkedHashMap<String, Integer>();

    private static final int[] LENGTH_HEADER = {60, 109};
    private static final int[] LENGTH_LABEL = {32, 81};
    private static final int[] LENGTH_NAME = {9, 33};
    private static final int[] LENGTH_FORMAT_FIELD = {7, 12, 49};
    private static final int[] LENGTH_EXPANSION_FIELD ={0, 2, 4};
    private static final int[] DBL_MV_PWR = {333, 1023};
 
    static {
        
        STATA_RELEASE_NUMBER.put(104, "rel_3");
        STATA_RELEASE_NUMBER.put(105, "rel_4or5");
        STATA_RELEASE_NUMBER.put(108, "rel_6");
        STATA_RELEASE_NUMBER.put(110, "rel_7first");
        STATA_RELEASE_NUMBER.put(111, "rel_7scnd");
        STATA_RELEASE_NUMBER.put(113, "rel_8_or_9");
        STATA_RELEASE_NUMBER.put(114, "rel_10");
        
        release105type.put("STRING",  127);
        release105type.put("BYTE",     98);
        release105type.put("INT",     105);
        release105type.put("LONG",    108);
        release105type.put("FLOAT",   102);
        release105type.put("DOUBLE0", 100);
        
        release111type.put("STRING",   0);
        release111type.put("BYTE",   -5);
        release111type.put("INT",    -4);
        release111type.put("LONG",   -3);
        release111type.put("FLOAT",  -2);
        release111type.put("DOUBLE", -1);

        
//        TYPE_OFFSET_TABLE.put("REL_105", release105);
//        TYPE_OFFSET_TABLE.put("REL_111", release111);
        
        release104constant.put("HEADER",     LENGTH_HEADER[0]);
        release104constant.put("LABEL",     LENGTH_LABEL[0]);
        release104constant.put("NAME",      LENGTH_NAME[0]);
        release104constant.put("FORMAT",    LENGTH_FORMAT_FIELD[0]);
        release104constant.put("EXPANSION", LENGTH_EXPANSION_FIELD[0]);
        release104constant.put("DBL_MV_PWR",DBL_MV_PWR[0]);
        CONSTATNT_TABLE.put(104, release104constant);

        release105constant.put("HEADER",     LENGTH_HEADER[0]);
        release105constant.put("LABEL",     LENGTH_LABEL[0]);
        release105constant.put("NAME",      LENGTH_NAME[0]);
        release105constant.put("FORMAT",    LENGTH_FORMAT_FIELD[1]);
        release105constant.put("EXPANSION", LENGTH_EXPANSION_FIELD[1]);
        release105constant.put("DBL_MV_PWR",DBL_MV_PWR[0]);
        CONSTATNT_TABLE.put(105, release105constant);
        
        release108constant.put("HEADER",     LENGTH_HEADER[1]);
        release108constant.put("LABEL",     LENGTH_LABEL[1]);
        release108constant.put("NAME",      LENGTH_NAME[0]);
        release108constant.put("FORMAT",    LENGTH_FORMAT_FIELD[1]);
        release108constant.put("EXPANSION", LENGTH_EXPANSION_FIELD[1]);
        release108constant.put("DBL_MV_PWR",DBL_MV_PWR[1]);
        CONSTATNT_TABLE.put(108, release108constant);
        
        release110constant.put("HEADER",     LENGTH_HEADER[1]);
        release110constant.put("LABEL",     LENGTH_LABEL[1]);
        release110constant.put("NAME",      LENGTH_NAME[1]);
        release110constant.put("FORMAT",    LENGTH_FORMAT_FIELD[1]);
        release110constant.put("EXPANSION", LENGTH_EXPANSION_FIELD[2]);
        release110constant.put("DBL_MV_PWR",DBL_MV_PWR[1]);
        CONSTATNT_TABLE.put(110, release110constant);
        
        release111constant.put("HEADER",     LENGTH_HEADER[1]);
        release111constant.put("LABEL",     LENGTH_LABEL[1]);
        release111constant.put("NAME",      LENGTH_NAME[1]);
        release111constant.put("FORMAT",    LENGTH_FORMAT_FIELD[1]);
        release111constant.put("EXPANSION", LENGTH_EXPANSION_FIELD[2]);
        release111constant.put("DBL_MV_PWR",DBL_MV_PWR[1]);
        CONSTATNT_TABLE.put(111, release111constant);
        
        release113constant.put("HEADER",     LENGTH_HEADER[1]);
        release113constant.put("LABEL",     LENGTH_LABEL[1]);
        release113constant.put("NAME",      LENGTH_NAME[1]);
        release113constant.put("FORMAT",    LENGTH_FORMAT_FIELD[1]);
        release113constant.put("EXPANSION", LENGTH_EXPANSION_FIELD[2]);
        release113constant.put("DBL_MV_PWR",DBL_MV_PWR[1]);
        CONSTATNT_TABLE.put(113, release113constant);
        
        release114constant.put("HEADER",     LENGTH_HEADER[1]);
        release114constant.put("LABEL",     LENGTH_LABEL[1]);
        release114constant.put("NAME",      LENGTH_NAME[1]);
        release114constant.put("FORMAT",    LENGTH_FORMAT_FIELD[2]);
        release114constant.put("EXPANSION", LENGTH_EXPANSION_FIELD[2]);
        release114constant.put("DBL_MV_PWR",DBL_MV_PWR[1]);
        CONSTATNT_TABLE.put(114, release114constant);
        
        byteLengthTable105.put((byte) 98,1);
        byteLengthTable105.put((byte)105,2);
        byteLengthTable105.put((byte)108,4);
        byteLengthTable105.put((byte)102,4);
        byteLengthTable105.put((byte)100,8);
        
        byteLengthTable111.put((byte)-5,1);
        byteLengthTable111.put((byte)-4,2);
        byteLengthTable111.put((byte)-3,4);
        byteLengthTable111.put((byte)-2,4);
        byteLengthTable111.put((byte)-1,8);

       
        variableTypeTable105.put((byte) 98,"Byte");
        variableTypeTable105.put((byte)105,"Integer");
        variableTypeTable105.put((byte)108,"Long");
        variableTypeTable105.put((byte)102,"Float");
        variableTypeTable105.put((byte)100,"Double");
        
        variableTypeTable111.put((byte)-5,"Byte");
        variableTypeTable111.put((byte)-4,"Integer");
        variableTypeTable111.put((byte)-3,"Long");
        variableTypeTable111.put((byte)-2,"Float");
        variableTypeTable111.put((byte)-1,"Double");


        variableTypeMap.put("Byte",   -5);
        variableTypeMap.put("Integer",-4);
        variableTypeMap.put("Long",   -3);
        variableTypeMap.put("Float",  -2);
        variableTypeMap.put("Double", -1);
        variableTypeMap.put("String",  0);
        

    }

    private static String[] FORMAT_NAMES = {"dta", "DTA"};
    private static String[] EXTENSIONS = {"dta"};
    private static String[] MIME_TYPE = {"application/x-stata"};



    /** format-related constants */

    private static final int DTA_MAGIC_NUMBER_LENGTH = 4;
    private static final int NVAR_FIELD_LENGTH       = 2;
    private static final int NOBS_FIELD_LENGTH       = 4;
    private static final int TIME_STAMP_LENGTH      = 18;
    private static final int VAR_SORT_FIELD_LENGTH   = 2;
    private static final int VALUE_LABEL_HEADER_PADDING_LENGTH = 3;


    /** List of decoding methods for reflection */
    private static String[] decodeMethodNames = {
        "decodeHeader","decodeDescriptors","decodeVariableLabels",
        "decodeExpansionFields","decodeData","decodeValueLabels" };

    private static List<Method> decodeMethods  = new ArrayList<Method>();


    private static String unfVersionNumber = "3";

    private static int MISSING_VALUE_BIAS = 26;

    private static byte BYTE_MISSING_VALUE = Byte.MAX_VALUE;
    private static short INT_MISSIG_VALUE = Short.MAX_VALUE;
    private static int LONG_MISSING_VALUE = Integer.MAX_VALUE;
    
    private static float FLOAT_MISSING_VALUE = Float.MAX_VALUE;
    private static double DOUBLE_MISSING_VALUE = Double.MAX_VALUE;


    private static final List<Float> FLOAT_MISSING_VALUES = Arrays.asList(
        0x1.000p127f, 0x1.001p127f, 0x1.002p127f, 0x1.003p127f,
        0x1.004p127f, 0x1.005p127f, 0x1.006p127f, 0x1.007p127f,
        0x1.008p127f, 0x1.009p127f, 0x1.00ap127f, 0x1.00bp127f,
        0x1.00cp127f, 0x1.00dp127f, 0x1.00ep127f, 0x1.00fp127f,
        0x1.010p127f, 0x1.011p127f, 0x1.012p127f, 0x1.013p127f,
        0x1.014p127f, 0x1.015p127f, 0x1.016p127f, 0x1.017p127f,
        0x1.018p127f, 0x1.019p127f, 0x1.01ap127f);

    Set<Float> FLOAT_MISSING_VALUE_SET =
        new HashSet<Float>(FLOAT_MISSING_VALUES);

    private static final List<Double> DOUBLE_MISSING_VALUE_LIST = Arrays.asList(
        0x1.000p1023, 0x1.001p1023, 0x1.002p1023, 0x1.003p1023, 0x1.004p1023,
        0x1.005p1023, 0x1.006p1023, 0x1.007p1023, 0x1.008p1023, 0x1.009p1023,
        0x1.00ap1023, 0x1.00bp1023, 0x1.00cp1023, 0x1.00dp1023, 0x1.00ep1023,
        0x1.00fp1023, 0x1.010p1023, 0x1.011p1023, 0x1.012p1023, 0x1.013p1023,
        0x1.014p1023, 0x1.015p1023, 0x1.016p1023, 0x1.017p1023, 0x1.018p1023,
        0x1.019p1023, 0x1.01ap1023);

    Set<Double> DOUBLE_MISSING_VALUE_SET =
        new HashSet<Double>(DOUBLE_MISSING_VALUE_LIST);


    private static SimpleDateFormat sdf_ymdhmsS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); // sdf


    private static SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd"); // sdf2


    private static SimpleDateFormat sdf_hms = new SimpleDateFormat("HH:mm:ss"); // stf


    private static SimpleDateFormat sdf_yw = new SimpleDateFormat("yyyy-'W'ww");



    // stata's calendar
    private static Calendar GCO_STATA = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

    private static String[] DATE_TIME_FORMAT= {
        "%tc", "%td", "%tw", "%tq","%tm", "%th", "%ty",
                "%d",  "%w",  "%q", "%m",  "h"};
    private static String[] DATE_TIME_CATEGORY={
        "time", "date", "date", "date", "date", "date", "date",
                        "date", "date", "date", "date", "date"
    };
    private static Map<String, String> DATE_TIME_FORMAT_TABLE=  new LinkedHashMap<String, String>();

    private static long SECONDS_PER_YEAR = 24*60*60*1000L;

    private static long STATA_BIAS_TO_EPOCH;

    static {

        for (String n: decodeMethodNames){
            for (Method m: DTAFileReader.class.getDeclaredMethods()){
                if (m.getName().equals(n)){
                    decodeMethods.add(m);
                }
            }
        }
        sdf_ymdhmsS.setTimeZone(TimeZone.getTimeZone("GMT"));
        sdf_ymd.setTimeZone(TimeZone.getTimeZone("GMT"));
        sdf_hms.setTimeZone(TimeZone.getTimeZone("GMT"));
        sdf_yw.setTimeZone(TimeZone.getTimeZone("GMT"));

        // set stata's calendar
        GCO_STATA.set(1, 1960);// year
        GCO_STATA.set(2, 0); // month
        GCO_STATA.set(5, 1);// day of month
        GCO_STATA.set(9, 0);// AM(0) or PM(1)
        GCO_STATA.set(10, 0);// hh
        GCO_STATA.set(12, 0);// mm
        GCO_STATA.set(13, 0);// ss
        GCO_STATA.set(14, 0); // SS millisecond


        STATA_BIAS_TO_EPOCH  = GCO_STATA.getTimeInMillis(); // =  -315619200000
        
        for (int i=0; i<DATE_TIME_FORMAT.length; i++){
            DATE_TIME_FORMAT_TABLE.put(DATE_TIME_FORMAT[i],DATE_TIME_CATEGORY[i]);
        }



    }
    /*
        // note: stata-missing value(old version)
        // little endian Hex and decimals: missing values
        string: ""
        byte:   0x7f = 127
        int :   0xff7f = 32,767
        long:   0xffffff7f = 2,147, 483, 647
        float:  0x0000007f = 2**127
        double: 0x000000000000e07f = 2**1023

    */
   




    // instance fields -------------------------------------------------------//

    private static Logger dbgLog = Logger.getLogger(DTAFileReader.class.getPackage().getName());

    NumberFormat doubleNumberFormatter = new DecimalFormat();

    SDIOMetadata smd = new DTAMetadata();
    

    SDIOData sdiodata;

    DataTable stataDataSection = new DataTable();

    int release_number;
    int header_length;
    int data_label_length;



    Map<String, Integer> type_offset_table ;
    
    Map<String, Integer> constant_table ;
    
    Map<Byte, Integer> byteLengthTable;
    
    double missing_value_double;

    boolean isLittleEndian = false;

    int bytes_per_row;

    Map<Byte, String> variableTypeTable;

    String[] variableTypelList=null;
    
    String[] variableTypelListFinal= null;
    boolean[] isDateTimeDatumList = null;

    Map<Integer, Integer> StringVariableTable = new LinkedHashMap<Integer, Integer>();
    
    int value_label_table_length;
    
    Map<String, Map<String, String>> valueLabelTable =
            new LinkedHashMap<String, Map<String, String>>();

    //Map<Integer, List<?  extends Object>> columnwiseDataTable;
    
    String[] unfValues = null;
    
    String fileUnfValue = null;

    List<String> variableNameList = new ArrayList<String>();

    Map<String, String> variableLabelMap = new LinkedHashMap<String, String>();


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
     * <code>MissingValueForTextDataFileString</code> field.
     */
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

    String[] variableFormats = null;

    Map<String, String> formatCategoryTable = new LinkedHashMap<String, String>();
    
    Map<String, String> formatNameTable = new LinkedHashMap<String, String>();

    Map<String, String> valueLabelSchemeMappingTable = new LinkedHashMap<String, String>();

    NumberFormat twoDigitFormatter = new DecimalFormat("00");

    // Constructor -----------------------------------------------------------//

    /**
     * Constructs a <code>DTAFileReader</code> instance with a 
     * <code>StatDataFileReaderSpi</code> object.
     * 
     * @param originator a <code>StatDataFileReaderSpi</code> object.
     */
    public DTAFileReader(StatDataFileReaderSpi originator){
        super(originator);
    }


    // Methods ---------------------------------------------------------------//

    private void init(){
        //
        dbgLog.fine("release number="+release_number);
        
        if (release_number < 111) {
            type_offset_table = release105type;
            variableTypeTable = variableTypeTable105;
            byteLengthTable = byteLengthTable105;
        } else {
            type_offset_table = release111type;
            variableTypeTable = variableTypeTable111;
            byteLengthTable = byteLengthTable111;
            BYTE_MISSING_VALUE   -= MISSING_VALUE_BIAS;
            INT_MISSIG_VALUE     -= MISSING_VALUE_BIAS;
            LONG_MISSING_VALUE   -= MISSING_VALUE_BIAS;
        }
        
        if (release_number <= 105){
            value_label_table_length = 2;
        } else {
            value_label_table_length = 4;
        }
        
        dbgLog.fine("type-offset table to be used:\n"+type_offset_table);

        constant_table = CONSTATNT_TABLE.get(release_number);

        header_length = constant_table.get("HEADER") - DTA_MAGIC_NUMBER_LENGTH;
        
        data_label_length = header_length - (NVAR_FIELD_LENGTH +
            NOBS_FIELD_LENGTH + TIME_STAMP_LENGTH);
        dbgLog.fine("data_label_length="+data_label_length);

        dbgLog.fine("constant table to be used:\n"+constant_table);

        missing_value_double = Math.pow(2, constant_table.get("DBL_MV_PWR"));

        //out.format("%e\n", missing_value_double);

        doubleNumberFormatter.setGroupingUsed(false);
        doubleNumberFormatter.setMaximumFractionDigits(7);

    }

    /**
     * Read the given Stata DTA-format file via a <code>BufferedInputStream</code>
     * object.  This method calls an appropriate method associated with the given 
     * field header by reflection.
     * 
     * @param stream a <code>BufferedInputStream</code>.
     * @return an <code>SDIOData</code> object
     * @throws java.io.IOException if an reading error occurs.
     */
    @Override
    public SDIOData read(BufferedInputStream stream) throws IOException{
        dbgLog.info("***** DTAFileReader: read() start *****");

        for (Method mthd : decodeMethods){

            try {
                // invoke this method
                dbgLog.fine("method Name="+mthd.getName());

                if (mthd.getName().equals("decodeExpansionFields")){
                    if (release_number == 104){
                        // release 104 does not have the expansion fields
                        continue;
                    }
                }

                mthd.invoke(this, stream);

            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                err.format(cause.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        
        smd.setVariableFormatName(formatNameTable);
        smd.setVariableFormatCategory(formatCategoryTable);
        smd.setValueLabelMappingTable(valueLabelSchemeMappingTable);
        
        smd.setVariableStorageType(variableTypelListFinal);

        
        if (sdiodata == null){
            sdiodata = new SDIOData(smd, stataDataSection);
        }
        dbgLog.info("***** DTAFileReader: read() end *****");
        return sdiodata;
    }



    void decodeHeader(BufferedInputStream stream){
        dbgLog.fine("***** decodeHeader(): start *****");

        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }

        dbgLog.fine("reading the header segument 1: 4 byte\n");
        byte[] magic_number = new byte[DTA_MAGIC_NUMBER_LENGTH];

        try{

            int nbytes = stream.read(magic_number, 0, DTA_MAGIC_NUMBER_LENGTH);

            if (nbytes == 0){
                throw new IOException();
            }
        //            dbgLog.fine("header: hex dump");
        //            for (int i = 0; i < magic_number.length; ++i) {
        //                out.printf("%2d\t0x%02X\n", i, magic_number[i]);
        //            }

       dbgLog.info("hex dump: 1st 4bytes =>" +
                new String(Hex.encodeHex(magic_number)) + "<-");


        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (magic_number[2] != 1) {
            dbgLog.fine("3rd byte is not 1: given file is not stata-dta type");
            throw new IllegalArgumentException("given file is not stata-dta type");
        } else if ((magic_number[1] != 1) && (magic_number[1] != 2)) {
            dbgLog.fine("2nd byte is neither 0 nor 1: this file is not stata-dta type");
            throw new IllegalArgumentException("given file is not stata-dta type");
        } else if (!STATA_RELEASE_NUMBER.containsKey((int)magic_number[0])) {
            dbgLog.fine("1st byte (" + magic_number[0]+
                    ") is not within the ingestable range [rel. 3-10]:"+
                    "this file is NOT stata-dta type");
            throw new IllegalArgumentException("given file is not stata-dta type");
        } else {
            release_number = (int)magic_number[0];
            smd.getFileInformation().put("releaseNumber", release_number);
            smd.getFileInformation().put("byteOrder", (int)magic_number[1]);
            smd.getFileInformation().put("OSByteOrder", ByteOrder.nativeOrder().toString());

            smd.getFileInformation().put("mimeType", MIME_TYPE[0]);
            smd.getFileInformation().put("fileFormat", MIME_TYPE[0]);
            init();

            dbgLog.fine("this file is stata-dta type: " +
                    STATA_RELEASE_NUMBER.get((int)magic_number[0]) +
                    "(Number=" + magic_number[0] + ")");
            dbgLog.fine("Endian(file)(Big: 1; Little:2)="+magic_number[1]);

            if ((int)magic_number[1] == 2 ){
                isLittleEndian = true;
                dbgLog.info("Reveral of the bytes is necessary to decode "+
                        "multi-byte fields");
            }
            dbgLog.fine("Endian of this platform:"+ByteOrder.nativeOrder().toString());
        }

        dbgLog.fine("reading the remaining header segument 2: 60 or 109-byte");
        //  60 - 4 = 56
        // 109 - 4 = 105
        try {

            byte[] header = new byte[header_length];
            int nbytes = stream.read(header, 0, header_length);
            //printHexDump(header, "header:\n");

            // 1. number of variables: short (2 bytes)
            ByteBuffer bbnvar = ByteBuffer.wrap(header, 0, NVAR_FIELD_LENGTH);
            ByteBuffer dupnvar = bbnvar.duplicate();
            short short_nvar = dupnvar.getShort();

            dbgLog.fine("get original short view(nvar)="+ short_nvar);
            if (isLittleEndian){
                bbnvar.order(ByteOrder.LITTLE_ENDIAN);

            }
            //ShortBuffer s_nvar = nvar.asShortBuffer();
            //out.println("get short view(nvar)="+ nvar.getShort());
            //out.println("as short view(nvar)="+ s_nvar.get());
            short shrt_nvar = bbnvar.getShort();
            smd.getFileInformation().put("varQnty", new Integer(shrt_nvar));

            // setup variableTypeList
            int nvar = shrt_nvar;
            variableTypelList = new String[nvar];



            // 2. number of observations: int (4 bytes)
            ByteBuffer nobs = ByteBuffer.wrap(header, NVAR_FIELD_LENGTH,
                                NOBS_FIELD_LENGTH);
            ByteBuffer dupnobs = nobs.duplicate();
            int int_dupnobs = dupnobs.getInt();
            dbgLog.fine("raw nobs="+int_dupnobs);
            if (isLittleEndian){
                nobs.order(ByteOrder.LITTLE_ENDIAN);
            }
            int int_nobs = nobs.getInt();
            dbgLog.fine("reversed nobs="+int_nobs);

            smd.getFileInformation().put("caseQnty", new Integer(int_nobs));

            // 3. data_label: 32 or 81 bytes
            int dl_offset = NVAR_FIELD_LENGTH + NOBS_FIELD_LENGTH ;
            dbgLog.fine("dl_offset="+dl_offset);
            dbgLog.fine("data_label_length="+data_label_length);

            String data_label = new String(Arrays.copyOfRange(header, dl_offset,
                (dl_offset+data_label_length)), "US-ASCII");



            dbgLog.fine("data_label_length="+data_label.length());
            //dbgLog.fine("data_label=["+data_label+"]");
            dbgLog.fine("loation of the null character="+data_label.indexOf(0));

            String dataLabel = getNullStrippedString(data_label);
            dbgLog.fine("data_label_length="+dataLabel.length());
            dbgLog.fine("data_label=["+dataLabel+"]");

            smd.getFileInformation().put("dataLabel", dataLabel);

            // 4. time_stamp: ASCII String (18 bytes)
            // added after release 4
            if (release_number > 104){
                int ts_offset = dl_offset + data_label_length;
                String time_stamp = new String(Arrays.copyOfRange(header, ts_offset,
                    ts_offset+TIME_STAMP_LENGTH),"US-ASCII");
                dbgLog.fine("time_stamp_length="+time_stamp.length());
                //dbgLog.fine("time_stamp=["+time_stamp+"]");
                dbgLog.fine("loation of the null character="+time_stamp.indexOf(0));

                String timeStamp = getNullStrippedString(time_stamp);
                dbgLog.fine("timeStamp_length="+timeStamp.length());
                dbgLog.fine("timeStamp=["+timeStamp+"]");

                smd.getFileInformation().put("timeStamp", timeStamp);
                smd.getFileInformation().put("fileDate", timeStamp);
                smd.getFileInformation().put("fileTime", timeStamp);
                smd.getFileInformation().put("varFormat_schema", "STATA");

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        dbgLog.fine("smd dump:"+smd.toString());
        dbgLog.fine("***** decodeHeader(): end *****");
    }



    void decodeDescriptors(BufferedInputStream stream){

        dbgLog.fine("***** decodeDescriptors(): start *****");
        
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        
        // part 1: variable type list
        int nvar = (Integer)smd.getFileInformation().get("varQnty");
        byte[] typeList = new byte[nvar];
        try {
            // note: the offset param of read() is relative to
            // the current position, not absolute position
            int nbytes = stream.read(typeList, 0, nvar);
            //printHexDump(typeList, "variable type list");
            if (nbytes == 0){
                throw new IOException("reading the descriptior: no byte was read");
            }
            /*
              111 type
              Type:   b   i   l   f   d (byte, int, long, float, double)
              byte:  -5  -4  -3  -2  -1 (signed byte = java's byte type)
              byte: 251 252 253 254 255 (unsigned byte)
               HEX:  FB  FC  FD  FE  FF
               
              105 type(type chars correspond to their hex/decimal expressions
              Type:   b   i   l   f   d (byte, int, long, float, double)
              byte:  98 105 108 102 100 (signed byte = java's byte type)
              byte:  98 105 108 102 100 (unsigned byte)
               HEX:  62  69  6C  66  64  
            */
            dbgLog.fine("type_offset_table:\n"+type_offset_table);
            dbgLog.fine("type code list:\n");

            bytes_per_row = 0;
            for (int i=0; i<typeList.length; i++){
                dbgLog.fine(i+"-th value="+typeList[i]);
                
                if (byteLengthTable.containsKey(typeList[i])){
                    bytes_per_row += byteLengthTable.get(typeList[i]);
                    
                    variableTypelList[i]= variableTypeTable.get(typeList[i]);


                } else {
                        // pre-111 string type
                    if (release_number < 111){
                        int stringType = 256 + typeList[i];
                        if ( stringType >= type_offset_table.get("STRING")) {
                            int string_var_length = stringType
                                - type_offset_table.get("STRING");
                            dbgLog.fine("string_var_length="+string_var_length);
                            bytes_per_row += string_var_length;

                            variableTypelList[i] = "String";
                            StringVariableTable.put(i,string_var_length);


                        } else if ((256+ typeList[i]) <
                            type_offset_table.get("STRING")) {
                            throw new IOException(
                            "unknown variable type was detected: reading errors?");
                        }
                    } else if (release_number >= 111){
                        // post-111 string type
                        int stringType = typeList[i] > 127 ? 256 + typeList[i] :typeList[i] ;
                        
                        if ( stringType >= type_offset_table.get("STRING")) {
                            int string_var_length = stringType - type_offset_table.get("STRING");
                            dbgLog.fine("string_var_length="+string_var_length);
                            bytes_per_row += string_var_length;

                            variableTypelList[i] = "String";
                            StringVariableTable.put(i,string_var_length);


                        } else if (stringType < type_offset_table.get("STRING")) {
                            throw new IOException(
                            "unknown variable type was detected: reading errors?");
                        }
                    } else {
                        throw new IOException("uknown release number ");
                    }

                }
                dbgLog.fine(i+"=th\t sum="+ bytes_per_row);
            }
            dbgLog.fine("bytes_per_row(final)="+bytes_per_row);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        dbgLog.fine("variableTypelList:\n"+Arrays.deepToString(variableTypelList));

        dbgLog.fine("StringVariableTable="+StringVariableTable);
        
        variableTypelListFinal = new String[nvar];
        for (int i=0; i< variableTypelList.length; i++){
            variableTypelListFinal[i] = new String(variableTypelList[i]);
        }
        
        
        
        // part 2: Variable_Name List
        // name length= 9(release 105) or 33 (release 111) each null terminated
        int length_var_name = constant_table.get("NAME");
        int length_var_name_list = length_var_name*nvar;
        dbgLog.fine("length_var_name_list="+length_var_name_list);

        byte[] variableNameBytes = new byte[length_var_name_list];
        //String[] variableNames = new String[nvar];
        try {
            int nbytes = stream.read(variableNameBytes, 0, length_var_name_list);

            //printHexDump(variableNameList, "variable name list");
            if (nbytes == 0){
                throw new IOException("reading the var name list: no var name was read");
            }
            int offset_start = 0;
            int offset_end = 0;
            for (int i= 0; i< nvar; i++){
                offset_end += length_var_name;
                String vari = new String(Arrays.copyOfRange(variableNameBytes, offset_start,
                    offset_end),"US-ASCII");
                variableNameList.add(getNullStrippedString(vari));
                dbgLog.fine(i+"-th name=["+variableNameList.get(i)+"]");
                offset_start = offset_end;
            }
            dbgLog.fine("variableNameList=\n"+StringUtils.join(variableNameList, ",\n")+"\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        smd.setVariableName(variableNameList.toArray(new String[variableNameList.size()]));

        // Part 3: variable sort list
        // length of this field = short(2bytes)*(nvar +1)
        int length_var_sort_list = VAR_SORT_FIELD_LENGTH*(nvar+1);
        dbgLog.fine("length_var_sort_list="+length_var_sort_list);

        byte[] varSortList = new byte[length_var_sort_list];
        short[] variableSortList = new short[nvar+1];

        try {
            int nbytes = stream.read(varSortList, 0, length_var_sort_list);

            //printHexDump(varSortList, "varSortList");

            if (nbytes == 0){
                throw new IOException("reading error: the varSortList");
            }
            
            int offset_start = 0;
            for (int i= 0; i<= nvar; i++){
                //dbgLog.fine(i+"-th iteration:(offset="+ offset_start+")");

                ByteBuffer bb_varSortList = ByteBuffer.wrap(varSortList, 
                    offset_start, VAR_SORT_FIELD_LENGTH);
                if (isLittleEndian){
                    bb_varSortList.order(ByteOrder.LITTLE_ENDIAN);
                }
                variableSortList[i] = bb_varSortList.getShort();
                //dbgLog.fine(variableSortList[i]);
                offset_start += VAR_SORT_FIELD_LENGTH;
            }
            dbgLog.fine("variableSortList="+ Arrays.toString(variableSortList));

        } catch (IOException ex) {
           ex.printStackTrace();
        }

        // Part 4: variable format list
        // VAR_FORMAT_FIELD_LENGTH (7,12, 49 bytes) * navar
        // null-terminated string
        int length_var_format =  constant_table.get("FORMAT");
        int length_var_format_list = length_var_format*nvar;
        dbgLog.fine("length_var_format_list="+length_var_format_list);

        byte[] variableFormatList = new byte[length_var_format_list];
        variableFormats = new String[nvar];
        isDateTimeDatumList = new boolean[nvar];
        try {
            int nbytes = stream.read(variableFormatList, 0, length_var_format_list);

            //printHexDump(variableFormatList, "variableFormatList");
            if (nbytes == 0){
                throw new IOException("reading var formats: no format was read");
            }
            int offset_start = 0;
            int offset_end = 0;
            for (int i= 0; i< nvar; i++){
                offset_end += length_var_format;
                String vari = new String(Arrays.copyOfRange(variableFormatList, offset_start,
                    offset_end),"US-ASCII");
                variableFormats[i] = getNullStrippedString(vari);
                dbgLog.fine(i+"-th format=["+variableFormats[i]+"]");
                
                
                
                String variableFormat = variableFormats[i];
                String variableFormatKey= null;
                if (variableFormat.startsWith("%t")){
                    variableFormatKey = variableFormat.substring(0, 3);
                } else {
                    variableFormatKey = variableFormat.substring(0, 2);
                }
                dbgLog.fine(i+" th variableFormatKey="+variableFormatKey);

                if (DATE_TIME_FORMAT_TABLE.containsKey(variableFormatKey)){
                
                    formatNameTable.put(variableNameList.get(i), variableFormat);
                    
                    formatCategoryTable.put(variableNameList.get(i) , DATE_TIME_FORMAT_TABLE.get(variableFormatKey));
                    
                    isDateTimeDatumList[i] = true;
                    dbgLog.fine(i+"th var: category="+
                        DATE_TIME_FORMAT_TABLE.get(variableFormatKey)
                        );
                        
                    variableTypelListFinal[i]="String";
                } else {
                    isDateTimeDatumList[i] = false;
                }


                
                offset_start = offset_end;
            }
            dbgLog.fine("variableFormats=\n"+StringUtils.join(variableFormats, ",\n")+"\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        //smd.setVariableFormat(variableFormats);

        // Part 5: value-label list
        // variable_name * nvar null-terminated String
        
        int length_label_name = constant_table.get("NAME");
        int length_label_name_list = length_label_name*nvar;
        dbgLog.fine("length_label_name="+length_label_name_list);

        byte[] labelNameList = new byte[length_label_name_list];
        String[] labelNames = new String[nvar];
        try {
            int nbytes = stream.read(labelNameList, 0, length_label_name_list);

            //printHexDump(labelNameList, "labelNameList");
            if (nbytes == 0){
                throw new IOException("reading value-label list:: no var name was read");
            }
            int offset_start = 0;
            int offset_end = 0;
            for (int i= 0; i< nvar; i++){
                offset_end += length_label_name;
                String vari = new String(Arrays.copyOfRange(labelNameList, offset_start,
                    offset_end),"US-ASCII");
                labelNames[i] = getNullStrippedString(vari);
                dbgLog.fine(i+"-th label=["+labelNames[i]+"]");
                offset_start = offset_end;
            }
            dbgLog.fine("labelNames=\n"+StringUtils.join(labelNames, ",\n")+"\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        for (int i=0; i <nvar; i++){
            if ((labelNames[i] != null) && (!labelNames[i].equals(""))){
                valueLabelSchemeMappingTable.put(variableNameList.get(i), labelNames[i]);
            }
        }
        dbgLog.fine("valueLabelSchemeMappingTable:\n"+valueLabelSchemeMappingTable);
        
        dbgLog.fine("smd dump (Descriptor):\n"+smd.toString());
        dbgLog.fine("***** decodeDescriptors(): end *****");

    }
    

    void decodeVariableLabels(BufferedInputStream stream){

        dbgLog.fine("***** decodeVariableLabels(): start *****");
        
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        
        // variable label length (32 or 81 bytes)*nvar, each null-terminated
        int nvar = (Integer)smd.getFileInformation().get("varQnty");
        int length_var_label = constant_table.get("LABEL");
        int length_var_label_list = length_var_label*nvar;
        dbgLog.fine("length_label_name="+length_var_label_list);

        byte[] variableLabelBytes = new byte[length_var_label_list];
        String[] variableLabels = new String[nvar];
        try {
            int nbytes = stream.read(variableLabelBytes, 0, length_var_label_list);

            //printHexDump(variableLabelList, "variableLabelList");
            if (nbytes == 0){
                throw new IOException("reading variable label list: no label was read");
            }
            int offset_start = 0;
            int offset_end = 0;
            for (int i= 0; i< nvar; i++){
                offset_end += length_var_label;
                String vari = new String(Arrays.copyOfRange(variableLabelBytes, offset_start,
                    offset_end),"US-ASCII");
                variableLabelMap.put(variableNameList.get(i), getNullStrippedString(vari));
                dbgLog.fine(i+"-th label=["+variableLabels[i]+"]");
                offset_start = offset_end;
            }
            dbgLog.fine("variableLabelMap=\n"+variableLabelMap.toString() +"\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        smd.setVariableLabel(variableLabelMap);
        
        dbgLog.fine("smd dump (variable label):\n"+smd.toString());
        dbgLog.fine("***** decodeVariableLabels(): end *****");

    }
    

    void decodeExpansionFields(BufferedInputStream stream){

        dbgLog.fine("***** decodeExpansionFields(): start *****");
        
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        
        // Added since release 105
        // [1-byte byte_field][short(2)/int(4)_field][variable_field whose
        // length is specified by the previous short/int field]
        
        int int_type_expansion_field = constant_table.get("EXPANSION");
        dbgLog.fine("int_type_expansion_field="+int_type_expansion_field);
        while(true){
            byte[] firstByte = new byte[1];
            byte[] lengthBytes = new byte[int_type_expansion_field];

            try {
                int nbyte = stream.read(firstByte, 0, 1);
                dbgLog.fine("read 1st byte");
                int nbytes = stream.read(lengthBytes, 0, int_type_expansion_field);
                dbgLog.fine("read next integer");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            ByteBuffer bb_field_length = ByteBuffer.wrap(lengthBytes);

            if (isLittleEndian){
                bb_field_length.order(ByteOrder.LITTLE_ENDIAN);
                dbgLog.fine("byte reversed");
            }

            int field_length;

            if (int_type_expansion_field == 2){
                field_length = bb_field_length.getShort();
            } else {
                field_length = bb_field_length.getInt();
            }
            
            dbgLog.fine("field_length="+field_length);
            dbgLog.fine("firstByte[0]="+firstByte[0]);
            if ((field_length + (int)firstByte[0]) == 0){
                // reached the end of this field
                break;
            } else {
                byte[] stringField = new byte[field_length];
                try {
                    
                    int nbyte = stream.read(stringField, 0, field_length);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        dbgLog.fine("***** decodeExpansionFields(): end *****");

    }
    

    void decodeData(BufferedInputStream stream){

        dbgLog.fine("\n***** decodeData(): start *****");
        
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        
        int nvar = (Integer)smd.getFileInformation().get("varQnty");
        int nobs = (Integer)smd.getFileInformation().get("caseQnty");
        dbgLog.fine("data diminsion[rxc]=("+nobs+","+nvar+")");
        dbgLog.fine("bytes per row="+bytes_per_row+" bytes");
        
        dbgLog.fine("variableTypelList="+Arrays.deepToString(variableTypelList));
        dbgLog.fine("StringVariableTable="+StringVariableTable);

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


        // data storage
        // Object[][] dataTable = new Object[nobs][nvar];
        // for later variable-wise calculations of statistics
        // dataTable2 sotres cut-out data columnwise
        Object[][] dataTable2 = new Object[nvar][nobs];
        
        for (int i=0; i< nobs; i++){
            byte[] dataRowBytes = new byte[bytes_per_row];
            Object[] dataRow = new Object[nvar];
            
            try {
                int nbytes = stream.read(dataRowBytes, 0, bytes_per_row);
                String rowTitle = "dataRowBytes("+i + "-th)";
                //printHexDump(dataRowBytes, rowTitle);

                if (nbytes == 0){
                    String errorMessage = "reading data: no data were read at("+
                        i+"th row)";
                    throw new IOException(errorMessage);
                }
                // decoding each row
                int byte_offset = 0;
                for (int columnCounter = 0;
                    columnCounter<variableTypelList.length; columnCounter++){
                    //String variable_id = "v"+ Integer.toString(columnCounter);
                    Integer varType = 
                        variableTypeMap.get(variableTypelList[columnCounter]);
                    //out.print(columnCounter+"th varType="+varType);
                    //out.println(columnCounter+"th variableFormats="+variableFormats[columnCounter]);

                    String variableFormat = variableFormats[columnCounter];

                    boolean isDateTimeDatum = isDateTimeDatumList[columnCounter];

                    switch( varType != null ? varType: 256){
                        case -5:
                            // Byte case
                            //out.print("\tbyte case:"+columnCounter+"th var");
                            // note: 1 byte signed
                            byte byte_datum = dataRowBytes[byte_offset];
                            
                            //out.println(byte_datum);
                            dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column byte ="+byte_datum);
                            if (byte_datum >= BYTE_MISSING_VALUE){
                                dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column byte MV="+byte_datum);
                                dataRow[columnCounter] = MissingValueForTextDataFileNumeric;
                                dataTable2[columnCounter][i] = Byte.MAX_VALUE;
                            } else {
                                dataRow[columnCounter] = byte_datum;
                                dataTable2[columnCounter][i] = byte_datum;
                            }


                            byte_offset++;
                            break;
                        case -4:
                            // Stata-int (=java's short: 2byte) case
                            //out.print("\tstata int case");
                            
                            // note: 2-byte signed int, not java's int
                            ByteBuffer int_buffer = 
                                ByteBuffer.wrap(dataRowBytes, byte_offset, 2);
                            if (isLittleEndian){
                                int_buffer.order(ByteOrder.LITTLE_ENDIAN);
                                //dbgLog.fine("short: byte reversed");
                            }
                            short short_datum = int_buffer.getShort();
                            
                            //out.println(short_datum);

                            dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column stata int ="+short_datum);
                            if (short_datum >= INT_MISSIG_VALUE){
                                dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column stata long missing value="+short_datum);
                                    
                                if (isDateTimeDatum){
                                    dataRow[columnCounter] =  MissingValueForTextDataFileString;
                                    dataTable2[columnCounter][i] = "";
                                } else {
                                    dataRow[columnCounter] = MissingValueForTextDataFileNumeric;
                                    dataTable2[columnCounter][i] = Short.MAX_VALUE;
                                }
                            } else {
                            
                                if (isDateTimeDatum){
                                    //dataTable2[columnCounter][i] = short_datum;
                                    dataRow[columnCounter] = decodeDateTimeData("short",variableFormat, Short.toString(short_datum));
                                    dataTable2[columnCounter][i] = dataRow[columnCounter];

                                } else {
                                    dataTable2[columnCounter][i] = short_datum;
                                    dataRow[columnCounter] = short_datum;
                                }
                            }
                            byte_offset += 2;
                            break;
                        case -3:
                            // stata-Long (= java's int: 4 byte) case
                            //out.print("\tstata long case");
                            // note: 4-byte singed, not java's long
                            ByteBuffer long_buffer = 
                                ByteBuffer.wrap(dataRowBytes, byte_offset, 4);
                            if (isLittleEndian){
                                long_buffer.order(ByteOrder.LITTLE_ENDIAN);
                                //dbgLog.fine("int: byte reversed");
                            }
                            int int_datum = long_buffer.getInt();
                            //out.println(int_datum);

                            dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column stata long ="+int_datum);
                            if (int_datum >=LONG_MISSING_VALUE){
                                dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column stata long missing value="+int_datum);
                                if (isDateTimeDatum){
                                    dataRow[columnCounter] =  MissingValueForTextDataFileString;
                                    dataTable2[columnCounter][i] = "";
                                } else {
                                    dataRow[columnCounter] = MissingValueForTextDataFileNumeric;
                                    dataTable2[columnCounter][i] = Integer.MAX_VALUE;
                                }
                            } else {

                                //out.println("columnCounter="+columnCounter+"\t"+int_datum);
                                if (isDateTimeDatum){
                                    //dataTable2[columnCounter][i] = int_datum;
                                    dataRow[columnCounter] = decodeDateTimeData("int",variableFormat, Integer.toString(int_datum));
                                    
                                    dataTable2[columnCounter][i] = dataRow[columnCounter];
                                    //out.println("columnCounter="+columnCounter+"\t"+dataTable2[columnCounter][i]);
                                } else {
                                    dataTable2[columnCounter][i] = int_datum;
                                    dataRow[columnCounter] = int_datum;
                                }

                            }
                            byte_offset += 4;
                            break;
                        case -2:
                            // float case
                            //out.print("\tfloat case");
                            // note: 4-byte
                            ByteBuffer float_buffer =
                                ByteBuffer.wrap(dataRowBytes, byte_offset, 4);
                            if (isLittleEndian){
                                float_buffer.order(ByteOrder.LITTLE_ENDIAN);
                                //dbgLog.fine("float: byte reversed");
                            }
                            float float_datum = float_buffer.getFloat();
                            
                            //out.println(float_datum);
                            dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column float ="+float_datum);
                            if (FLOAT_MISSING_VALUE_SET.contains(float_datum)){
                                dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column float missing value="+float_datum);
                                    
                                if (isDateTimeDatum){
                                    dataRow[columnCounter] =  MissingValueForTextDataFileString;
                                    dataTable2[columnCounter][i] = "";
                                } else {
                                    dataRow[columnCounter] = MissingValueForTextDataFileNumeric;
                                    dataTable2[columnCounter][i] = Float.NaN;
                                }
                                
                            } else {

                                if (isDateTimeDatum){
                                    //dataTable2[columnCounter][i] = float_datum;
                                    dataRow[columnCounter] = decodeDateTimeData("float",variableFormat, doubleNumberFormatter.format(float_datum));
                                    dataTable2[columnCounter][i] = dataRow[columnCounter];
                                } else {
                                    dataTable2[columnCounter][i] = float_datum;
                                    dataRow[columnCounter] = doubleNumberFormatter.format(float_datum);
                                }

                            }
                            byte_offset += 4;
                            break;
                        case -1:
                            // double case
                            //out.print("\tdouble case");
                            // note: 8-byte
                            ByteBuffer double_buffer =
                                ByteBuffer.wrap(dataRowBytes, byte_offset, 8);
                            if (isLittleEndian){
                                double_buffer.order(ByteOrder.LITTLE_ENDIAN);
                                //dbgLog.fine("double: byte reversed");
                            }
                            double double_datum = double_buffer.getDouble();

//                            dbgLog.finer(i+"-th row "+columnCounter+
//                                    "=th column double ="+double_datum);
//                            dbgLog.finer(i+"-th row "+columnCounter+
//                                    "=th column double ="+Double.toHexString(double_datum));
//                            String double_datum_hex = new String (Hex.encodeHex(StatHelper.toByteArrays(double_datum)));
//                            dbgLog.finer("double_datum="+double_datum_hex);

                            if (DOUBLE_MISSING_VALUE_SET.contains(double_datum)){
                                dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column double missing value="+double_datum);
                                if (isDateTimeDatum){
                                    dataRow[columnCounter] =  MissingValueForTextDataFileString;
                                    dataTable2[columnCounter][i] = "";
                                } else {
                                    dataRow[columnCounter] =  MissingValueForTextDataFileNumeric;
                                    dataTable2[columnCounter][i] = Double.NaN;
                                }
                            } else {

                                if (isDateTimeDatum){
                                    
                                    //dataTable2[columnCounter][i] = double_datum;
                                    
                                    dataRow[columnCounter] = decodeDateTimeData("double",variableFormat, doubleNumberFormatter.format(double_datum));
                                    dataTable2[columnCounter][i] = dataRow[columnCounter];
                                } else {
                                    dataTable2[columnCounter][i] = double_datum;
                                    dataRow[columnCounter] = doubleNumberFormatter.format(double_datum);
                                }

                            }
                            byte_offset += 8;
                            break;
                        case  0:
                            // String case
                            int strVarLength = StringVariableTable.get(columnCounter);
                            //out.print("\t"+columnCounter+"-th string var length="+strVarLength);
                            //out.println("byte_offset="+byte_offset);
                            String raw_datum = new String(Arrays.copyOfRange(dataRowBytes, byte_offset,
                                (byte_offset+strVarLength)), "US-ASCII");
                            //out.println("raw_datum="+raw_datum);
//                            int null_position = raw_datum.indexOf(0);
//                            String string_datum=null;
//                            if (null_position >= 0){
//                                // string is terminated by the null
//                                string_datum = raw_datum.substring(0, null_position);
//                            } else {
//                                // not null-termiated (sometimes space-paddded, instead)
//                                // get up to the length and
//                                // then trim spaces ("x20") form the end
//                                string_datum = StringUtils.stripEnd(raw_datum.substring(0, strVarLength)," ");
//                            }
                            String string_datum = StringUtils.stripEnd(getNullStrippedString(raw_datum), " ");
                            //out.println(string_datum);
                            dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column string ="+string_datum);
                            if (string_datum.equals("")){
                                dbgLog.finer(i+"-th row "+columnCounter+
                                    "=th column string missing value="+string_datum);
                                dataRow[columnCounter] =  MissingValueForTextDataFileString;
                                dataTable2[columnCounter][i] = "";
                            } else {
                                dataRow[columnCounter] = string_datum;
                                dataTable2[columnCounter][i] = string_datum;
                            }
                            byte_offset +=strVarLength;
                            break;
                        default:
                            dbgLog.fine("unknown variable type found");
                            String errorMessage =
                                "unknow variable Type found at data section";
                            throw new InvalidObjectException(errorMessage);
                    } // switch
                } // for-columnCounter


           
            // dump the row of data to the external file
            pwout.println(StringUtils.join(dataRow, "\t"));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            dbgLog.fine(i+"-th row's data={"+StringUtils.join(dataRow, ",")+"};");

            
            
            //dataTable[i] = dataRow;
        }  // for- i (row)

        
        pwout.close();

        //out.println("\ndataTable2(variable-wise):\n");
        //out.println(Arrays.deepToString(dataTable2));

        dbgLog.fine("variableTypelList:\n"+Arrays.deepToString(variableTypelList));
        dbgLog.fine("variableTypelListFinal:\n"+Arrays.deepToString(variableTypelListFinal));
        unfValues = new String[nvar];

        for (int j=0;j<nvar; j++){
           //String variableType_j =  variableTypelList[j];
           String variableType_j =  variableTypelListFinal[j];
            try {
                unfValues[j] = getUNF(dataTable2[j], variableType_j,
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
        
        stataDataSection.setUnf(unfValues);

        stataDataSection.setFileUnf(fileUnfValue);

        smd.setVariableUNF(unfValues);
        
        smd.getFileInformation().put("fileUNF", fileUnfValue);
        dbgLog.fine("unf values:\n"+unfValues);
        
        stataDataSection.setData(dataTable2);
        
        // close the stream

        dbgLog.fine("***** decodeData(): end *****\n\n");

    }


    
    /**
     *
     * @param stream
     */
    void decodeValueLabels(BufferedInputStream stream){

        dbgLog.fine("***** decodeValueLabels(): start *****");
        
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        
        
        try {
            if (stream.available() != 0) {
                if ((Integer) smd.getFileInformation().get("releaseNumber") <= 105) {
                    parseValueLabelsRelease105(stream);
                } else if ((Integer) smd.getFileInformation().get("releaseNumber") >= 105) {
                    parseValueLabelsReleasel108(stream);
                }
            } else {
                dbgLog.fine("no value-label table: end of file");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
       dbgLog.fine("***** decodeValueLabels(): end *****");
    }
    
    
    void parseValueLabelsRelease105(BufferedInputStream stream){

        dbgLog.fine("***** parseValueLabelsRelease105(): start *****");
        
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        
        int nvar = (Integer)smd.getFileInformation().get("varQnty");
        int length_label_name = constant_table.get("NAME") + 1;
        // note: caution +1 as the null character, not 9 byte

        
        int length_value_label_header = value_label_table_length
                                       + length_label_name;

        dbgLog.fine("value_label_table_length="+value_label_table_length);
        dbgLog.fine("length_value_label_header="+length_value_label_header);

        int length_lable_name_field = 8;

        /*
            Seg  field         byte    type
            1-1. no of pairs      2    int  (= m)
            1-2. vlt_name        10    includes char+(\0) == name used in Sec2.part 5
         -----------------------------------
                                 11
            2-1. values         2*n    int[]
            2-2. labels         8*n    char
        */
        
        for (int i=0; i< nvar; i++){
            dbgLog.fine("\n\n"+i+"th value-label table header");
            
            byte[] valueLabelHeader = new byte[length_value_label_header];

            try {
                // Part 1: reading the header of a value-label table if exists
                int nbytes = stream.read(valueLabelHeader, 0, 
                    length_value_label_header);

                if (nbytes == 0){
                    throw new IOException("reading value label header: no datum");
                }
                //printHexDump(valueLabelHeader, "value-label header");
                
                // 1.1 number of value-label pairs in this table (= m)
                ByteBuffer bb_value_label_pairs =
                                    ByteBuffer.wrap(valueLabelHeader, 0,
                                    value_label_table_length);
                if (isLittleEndian){
                    bb_value_label_pairs.order(ByteOrder.LITTLE_ENDIAN);
                    //dbgLog.fine("value lable table lenth: byte reversed");
                }
                int no_value_label_pairs = bb_value_label_pairs.getShort();

                dbgLog.fine("no_value_label_pairs="+no_value_label_pairs);

                // 1.2 labelName
                String rawLabelName = new String(Arrays.copyOfRange(
                                    valueLabelHeader,
                                   value_label_table_length,
                                  (value_label_table_length+length_label_name)),
                                  "US-ASCII");

                dbgLog.fine("rawLabelName(length)="+rawLabelName.length());
                String labelName = rawLabelName.substring(0, rawLabelName.indexOf(0));

                dbgLog.fine("label name = "+labelName+"\n");


                dbgLog.fine(i+"-th value-label table");
                // Part 2: reading the value-label table
                // the length of the value-label table is: 2*m + 8*m = 10*m
                int length_value_label_table = (value_label_table_length + 
                                length_lable_name_field)*no_value_label_pairs;

                dbgLog.fine("length_value_label_table="+length_value_label_table);
                
                byte[] valueLabelTable_i = new byte[length_value_label_table];
                int noBytes = stream.read(valueLabelTable_i, 0,
                                 length_value_label_table);
                if (noBytes == 0){
                    throw new IOException("reading value label table: no datum");
                }
                //printHexDump(valueLabelTable_i, "valueLabelTable_i");
                
                // 2-1. 2-byte-integer array (2*m): value array (sorted)
                
                dbgLog.fine("value array");
                
                short[] valueList = new short[no_value_label_pairs];
                int offset_value = 0;

                for (int k= 0; k< no_value_label_pairs; k++){

                    ByteBuffer bb_value_list = 
                        ByteBuffer.wrap(valueLabelTable_i, offset_value, 
                        value_label_table_length);
                    if (isLittleEndian){
                        bb_value_list.order(ByteOrder.LITTLE_ENDIAN);
                        //dbgLog.fine("value list: byte reversed");
                    }
                    valueList[k] = bb_value_list.getShort();

                    //dbgLog.fine(k+"-th value_list=["+valueList[k]+"]");

                    offset_value += value_label_table_length;
                }

                dbgLog.fine("value_list="+Arrays.toString(valueList)+"\n");
                
                // 2-2. 8-byte chars that store label data (m units of labels)

                dbgLog.fine("current offset_value="+offset_value);
                
                int offset_start = offset_value;
                int offset_end = offset_value + length_lable_name_field;
                String[] labelList = new String[no_value_label_pairs];

                for (int l= 0; l< no_value_label_pairs; l++){



                    String string_l = new String(Arrays.copyOfRange(valueLabelTable_i, offset_start,
                        offset_end),"US-ASCII");

                    int null_position = string_l.indexOf(0);
                    if (null_position != -1){
                        labelList[l] = string_l.substring(0, null_position);
                    } else {
                        labelList[l] = string_l;
                    }
                    
                    //dbgLog.fine(l+"-th label=["+labelList[l]+"]");
                    
                    offset_start = offset_end;
                    offset_end += length_lable_name_field;
                }
                

                //dbgLog.fine("label list: length="+labelList.length);
                //dbgLog.fine("label list ="+StringUtils.join(labelList, ",")+"\n");


                Map<String, String> tmpValueLabelTable = new LinkedHashMap<String, String>();

                for (int j=0; j< no_value_label_pairs; j++){
                    dbgLog.fine(j+"-th pair:"+valueList[j]+"["+labelList[j]+"]");
                    tmpValueLabelTable.put(Integer.toString(valueList[j]),labelList[j]);
                }
                valueLabelTable.put(labelName, tmpValueLabelTable);
                
                
                if (stream.available() == 0){
                    // reached the end of this file
                    // do exit-processing
                    dbgLog.fine("***** reached the end of the file at "+i
                        +"th value-label Table *****");
                    break;
                }

            } catch (IOException ex){

            }

        
        } // for-loop
        
        dbgLog.fine("valueLabelTable:\n"+valueLabelTable);
        
        
        smd.setValueLabelTable(valueLabelTable);
        
        
        dbgLog.fine("***** parseValueLabelsRelease105(): end *****");

    }


    private void parseValueLabelsReleasel108(BufferedInputStream stream){

        dbgLog.fine("***** parseValueLabelsRelease108(): start *****");
        
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        
        int nvar = (Integer)smd.getFileInformation().get("varQnty");
        int length_label_name = constant_table.get("NAME");
        int length_value_label_header = value_label_table_length
                                       + length_label_name
                                       + VALUE_LABEL_HEADER_PADDING_LENGTH;
                                       
        dbgLog.fine("value_label_table_length="+value_label_table_length);
        dbgLog.fine("length_value_label_header="+length_value_label_header);
        /*
            Seg  field         byte    type
            1-1. len_vlt(Seg.2)   4    int
            1-2. vlt_name      9/33    char+(\0) == name used in Sec2.part 5
            1-3. padding          3    byte
         -----------------------------------
                              16/40
            2-1. n(# of vls)      4    int
            2-2. m(len_labels)    4    int
            2-3. label_offsets    4*n  int[]
            2-4. values           4*n  int[]
            2-5. labels           m    char
        */
        
        for (int i=0; i< nvar; i++){
            dbgLog.fine("\n\n"+i+"th value-label table header");
            
            byte[] valueLabelHeader = new byte[length_value_label_header];

            try {
                // Part 1: reading the header of a value-label table if exists
                int nbytes = stream.read(valueLabelHeader, 0, 
                    length_value_label_header);

                if (nbytes == 0){
                    throw new IOException("reading value label header: no datum");
                }
                //printHexDump(valueLabelHeader, "value-label header");
                
                // 1.1 length_value_label_table
                ByteBuffer bb_value_label_header =
                                    ByteBuffer.wrap(valueLabelHeader, 0,
                                    value_label_table_length);
                if (isLittleEndian){
                    bb_value_label_header.order(ByteOrder.LITTLE_ENDIAN);
                    //dbgLog.fine("value lable table lenth: byte reversed");
                }
                int length_value_label_table = bb_value_label_header.getInt();

                dbgLog.fine("length of this value-label table="+
                                length_value_label_table);

                // 1.2 labelName
                String rawLabelName = new String(Arrays.copyOfRange(
                                    valueLabelHeader,
                                   value_label_table_length,
                                  (value_label_table_length+length_label_name)),
                                  "US-ASCII");
                String labelName = getNullStrippedString(rawLabelName);

                dbgLog.fine("label name = "+labelName+"\n");


                dbgLog.fine(i+"-th value-label table");
                // Part 2: reading the value-label table
                byte[] valueLabelTable_i = new byte[length_value_label_table];
                int noBytes = stream.read(valueLabelTable_i, 0,
                                 length_value_label_table);
                if (noBytes == 0){
                    throw new IOException("reading value label table: no datum");
                }
                //printHexDump(valueLabelTable_i, "valueLabelTable_i");

                // 2-1. 4-byte-integer: number of units in this table (n)
                int valueLabelTable_offset = 0;
                ByteBuffer bb_value_label_pairs =
                    ByteBuffer.wrap(valueLabelTable_i, valueLabelTable_offset,
                        value_label_table_length);
                if (isLittleEndian){
                    bb_value_label_pairs.order(ByteOrder.LITTLE_ENDIAN);
                    //dbgLog.fine("value_label pairs: byte reversed");
                }

                int no_value_label_pairs = bb_value_label_pairs.getInt();

                valueLabelTable_offset += value_label_table_length;

                dbgLog.fine("no_value_label_pairs="+no_value_label_pairs);


                // 2-2. 4-byte-integer: length of the label section (m bytes)

                ByteBuffer bb_length_label_segment =
                    ByteBuffer.wrap(valueLabelTable_i, valueLabelTable_offset,
                        value_label_table_length);
                if (isLittleEndian){
                    bb_length_label_segment.order(ByteOrder.LITTLE_ENDIAN);
                    //dbgLog.fine("length label segment: byte reversed");
                }

                int length_label_segment = bb_length_label_segment.getInt();
                valueLabelTable_offset += value_label_table_length;

                //dbgLog.fine("length_label_segment="+length_label_segment);

                // 2-3. 4-byte-integer array (4xm): offset values for the label sec.
                
                //dbgLog.fine("label offset array");
                
                int[] label_offsets = new int[no_value_label_pairs];
                int byte_offset = valueLabelTable_offset;

                for (int j= 0; j< no_value_label_pairs; j++){

                    // note: 4-byte singed, not java's long
                    ByteBuffer bb_label_offset = 
                        ByteBuffer.wrap(valueLabelTable_i, byte_offset, 
                        value_label_table_length);
                    if (isLittleEndian){
                        bb_label_offset.order(ByteOrder.LITTLE_ENDIAN);
                        //dbgLog.fine("label offset: byte reversed");
                    }
                    label_offsets[j] = bb_label_offset.getInt();

                    byte_offset += value_label_table_length;

                    //dbgLog.fine(j+"-th label_offset=["+label_offsets[j]+"]");
                }

                //dbgLog.fine("label_offsets="+Arrays.toString(label_offsets)+"\n");
                

                // 2-4. 4-byte-integer array (4xm): value array (sorted)
                
                dbgLog.fine("value array");
                
                int[] valueList = new int[no_value_label_pairs];
                int offset_value = byte_offset;

                for (int k= 0; k< no_value_label_pairs; k++){

                    ByteBuffer bb_value_list = 
                                    ByteBuffer.wrap(valueLabelTable_i, offset_value, 
                                    value_label_table_length);
                    if (isLittleEndian){
                        bb_value_list.order(ByteOrder.LITTLE_ENDIAN);
                        //out.println("value list: byte reversed");
                    }
                    valueList[k] = bb_value_list.getInt();

                    //dbgLog.fine(k+"-th value_list=["+valueList[k]+"]");

                    offset_value += value_label_table_length;

                }

                //dbgLog.fine("value_list="+Arrays.toString(valueList)+"\n");
                

                // 2-5. m-byte chars that store label data (m units of labels)

                //dbgLog.fine("current offset_value="+offset_value);
                //dbgLog.fine("current length_label_segment="+length_label_segment);
                String label_segment = new String(
                                Arrays.copyOfRange(valueLabelTable_i,
                                offset_value,
                                (length_label_segment+offset_value)), "US-ASCII");

                String[] labelList = label_segment.split("\0");

                //dbgLog.fine("label list: length="+labelList.length);
                //dbgLog.fine("label list ="+StringUtils.join(labelList, ",")+"\n");

                Map<String, String> tmpValueLabelTable = new LinkedHashMap<String, String>();
                
                for (int l=0; l< no_value_label_pairs; l++){
                    dbgLog.fine(l+"-th pair:"+valueList[l]+"["+labelList[l]+"]");
                    
                    tmpValueLabelTable.put(Integer.toString(valueList[l]),labelList[l]);
                }
                
                valueLabelTable.put(labelName, tmpValueLabelTable);
                
                
                if (stream.available() == 0){
                    // reached the end of this file
                    // do exit-processing
                    dbgLog.fine("***** reached the end of the file at "+i
                        +"th value-label Table *****");
                    break;
                }



            } catch (IOException ex){

            }

        }  // for loop
        dbgLog.fine("valueLabelTable:\n"+valueLabelTable);
        
        smd.setValueLabelTable(valueLabelTable);
        
        dbgLog.fine("***** parseValueLabelsRelease108(): end *****");
    }


    void print2Darray(Object[][] datatable, String title){
        dbgLog.fine(title);
        for (int i=0; i< datatable.length; i++){
            dbgLog.fine(StringUtils.join(datatable[i], "|"));
        }
    }

    private String getUNF(Object[] varData, String variableType, 
        String unfVersionNumber, int variablePosition)
        throws NumberFormatException, UnfException,
        IOException, NoSuchAlgorithmException{
        String unfValue = null;
//                for (int j=0; j<nvar;j++){
        dbgLog.fine(variablePosition+"-th varData:\n"+Arrays.deepToString(varData));
        dbgLog.fine("variableType="+variableType);
        dbgLog.fine("unfVersionNumber="+unfVersionNumber);
        Integer var_Type = variableTypeMap.get(variableType);
        dbgLog.fine("var_Type="+var_Type);
        Map<String, Integer> catStat = null;
        switch( var_Type != null ? var_Type: 256){
            case -5:
                // Byte case
                dbgLog.fine("byte case");

                byte[] bdata = ArrayUtils.toPrimitive(
                        Arrays.asList(varData).toArray(new Byte[varData.length]));

                unfValue = UNFUtil.calculateUNF(bdata, unfVersionNumber);

                smd.getSummaryStatisticsTable().put(variablePosition, 
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(bdata)));

                catStat = StatHelper.calculateCategoryStatistics(bdata);
                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), catStat);
                
                break;
            case -4:
                // Stata-int (=java's short: 2byte) case
                dbgLog.fine("stata int case");
                // note: 2-byte signed int, not java's int

                short[] sdata = ArrayUtils.toPrimitive(
                        Arrays.asList(varData).toArray(new Short[varData.length]));
                unfValue = UNFUtil.calculateUNF(sdata, unfVersionNumber);

                smd.getSummaryStatisticsTable().put(variablePosition, 
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(sdata)));

                catStat = StatHelper.calculateCategoryStatistics(sdata);
                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), catStat);

                break;
            case -3:
                // stata-Long (= java's int: 4 byte) case
                dbgLog.fine("stata long case");
                // note: 4-byte singed, not java's long

                int[] idata = ArrayUtils.toPrimitive(
                        Arrays.asList(varData).toArray(new Integer[varData.length]));
                unfValue = UNFUtil.calculateUNF(idata, unfVersionNumber);

                smd.getSummaryStatisticsTable().put(variablePosition, 
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(idata)));
                    
                catStat = StatHelper.calculateCategoryStatistics(idata);
                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), catStat);
                break;
            case -2:
                // float case
                dbgLog.fine("float case");
                // note: 4-byte
                float[] fdata = ArrayUtils.toPrimitive(
                        Arrays.asList(varData).toArray(new Float[varData.length]));

                unfValue = UNFUtil.calculateUNF(fdata, unfVersionNumber);

                smd.getSummaryStatisticsTable().put(variablePosition,
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(fdata)));

                if (valueLabelSchemeMappingTable.containsKey(variableNameList.get(variablePosition))){
                    catStat = StatHelper.calculateCategoryStatistics(fdata);
                }
                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), catStat);

                break;
            case -1:
                // double case
                dbgLog.fine("double case");
                // note: 8-byte

                double[] ddata= ArrayUtils.toPrimitive(
                        Arrays.asList(varData).toArray(new Double[varData.length]));
                        
                unfValue = UNFUtil.calculateUNF(ddata, unfVersionNumber);
                
                smd.getSummaryStatisticsTable().put(variablePosition,
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ddata)));

                if (valueLabelSchemeMappingTable.containsKey(variableNameList.get(variablePosition))){
                    catStat = StatHelper.calculateCategoryStatistics(ddata);
                }
                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), catStat);

                break;
            case  0:
                // String case
                dbgLog.fine("string case");

                String[] strdata = Arrays.asList(varData).toArray(
                    new String[varData.length]);
                dbgLog.fine("strdata="+Arrays.deepToString(strdata));
                unfValue = UNFUtil.calculateUNF(strdata, unfVersionNumber);
                dbgLog.fine("string:unfValue"+unfValue);

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
                //} // for-loop
        dbgLog.fine("unfvalue(last)="+unfValue);
        return unfValue;
    }

//    private String getNullStrippedString(String rawString){
//        String nullRemovedString = null;
//        int null_position = rawString.indexOf(0);
//        if (null_position >= 0){
//            // string is terminated by the null
//            nullRemovedString = rawString.substring(0, null_position);
//        } else {
//            // not null-termiated (sometimes space-paddded, instead)
//            // get up to the length
//            nullRemovedString = rawString.substring(0, rawString.length());
//        }
//        return nullRemovedString;
//    }


    private String decodeDateTimeData(String storageType, String FormatType, String rawDatum){

        dbgLog.finer("(storageType, FormatType, rawDatum)=("+
        storageType +", " +FormatType +", " +rawDatum+")");
        /*
         *         Historical note:
                   pseudofunctions,  td(), tw(), tm(), tq(), and th()
                used to be called     d(),  w(),  m(),  q(), and  h().
                Those names still work but are considered anachronisms.

        */
        
        long milliSeconds;
        String decodedDateTime=null;

        if (FormatType.matches("^%tc(\\w|[:\\.])*")){
            // tc is a relatively new format
            // datum is millisecond-wise

            milliSeconds = Long.parseLong(rawDatum)+ STATA_BIAS_TO_EPOCH;
            decodedDateTime = sdf_ymdhmsS.format(new Date(milliSeconds));
            dbgLog.finer("tc: result="+decodedDateTime);
            
        } else if (FormatType.matches("^%t?d(\\w|[:\\.])*")){
            milliSeconds = Long.parseLong(rawDatum)*SECONDS_PER_YEAR + STATA_BIAS_TO_EPOCH;
            dbgLog.finer("milliSeconds="+milliSeconds);
            
            decodedDateTime = sdf_ymd.format(new Date(milliSeconds));
            dbgLog.finer("td:"+decodedDateTime);

        } else if (FormatType.matches("^%t?w(\\w|[:\\.])*")){

            long weekYears = Long.parseLong(rawDatum);
            long left = Math.abs(weekYears)%52L;
            long years;
            if (weekYears < 0L){
                left = 52L - left;
                //out.println("left="+left);
                years = (Math.abs(weekYears) -1)/52L +1L;
                years *= -1L;
            } else {
                years = weekYears/52L;
            }
// alterantive decoding 1: ISO style YYYY-Www-D
            String week = null;

            if (left == 52L){
                left = 0L;
            }
            
            Long weekdata = (left+1);
            week = "-W"+twoDigitFormatter.format(weekdata).toString();
            long year  = 1960L + years;
            String weekYear = Long.valueOf(year).toString() + week;
            dbgLog.finer("rawDatum="+rawDatum+": weekYear="+weekYear);

            //decodedDateTime = weekYear;
            
// alterantive decoding 2: for R
            Calendar wyr = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            wyr.set(1, (int)year);// year
            wyr.set(3,weekdata.intValue());
            wyr.set(9, 0);// AM(0) or PM(1)
            wyr.set(10, 0);// hh
            wyr.set(12, 0);// mm
            wyr.set(13, 0);// ss
            wyr.set(14, 0); // SS millisecond
            dbgLog.finer("rawDatum="+rawDatum+" date="+
            sdf_yw.format(new Date(wyr.getTimeInMillis()))+"\n");
            
            decodedDateTime = sdf_ymd.format(new Date(wyr.getTimeInMillis()));
            
        } else if (FormatType.matches("^%t?m(\\w|[:\\.])*")){
            // month 
            long monthYears = Long.parseLong(rawDatum);
            long left = Math.abs(monthYears)%12L;
            long years;
            if (monthYears < 0L){
                left = 12L - left;
                //out.println("left="+left);
                years = (Math.abs(monthYears) -1)/12L +1L;
                years *= -1L;
            } else {
                years = monthYears/12L;
            }

            String month = null;
            if (left == 12L){
                left = 0L;
            }
            Long monthdata = (left+1);
            month = "-"+twoDigitFormatter.format(monthdata).toString()+"-01";
            long year  = 1960L + years;
            String monthYear = Long.valueOf(year).toString() + month;
            dbgLog.finer("rawDatum="+rawDatum+": monthYear="+monthYear);
            
            decodedDateTime = monthYear;
            dbgLog.finer("tm:"+decodedDateTime);

        } else if (FormatType.matches("^%t?q(\\w|[:\\.])*")){
            // quater
            long quaterYears = Long.parseLong(rawDatum);
            long left = Math.abs(quaterYears)%4L;
            long years;
            if (quaterYears < 0L){
                left = 4L - left;
                //out.println("left="+left);
                years = (Math.abs(quaterYears) -1)/4L +1L;
                years *= -1L;
            } else {
                years = quaterYears/4L;
            }

            String quater = null;

            if ((left == 0L) || (left == 4L)){
                //quater ="q1"; //
                quater = "-01-01";
            } else if (left ==1L) {
                //quater = "q2"; //
                quater = "-04-01";
            } else if (left ==2L) {
                //quater = "q3"; //
                quater = "-07-01";
            } else if (left ==3L) {
                //quater = "q4"; //
                quater = "-11-01";
            }

            long year  = 1960L + years;
            String quaterYear = Long.valueOf(year).toString() + quater;
            dbgLog.finer("rawDatum="+rawDatum+": quaterYear="+quaterYear);

            decodedDateTime = quaterYear;
            dbgLog.finer("tq:"+decodedDateTime);

        } else if (FormatType.matches("^%t?h(\\w|[:\\.])*")){
            // half year
            // odd number:2nd half
            // even number: 1st half
            
            long halvesYears = Long.parseLong(rawDatum);
            long left = Math.abs(halvesYears)%2L;
            long years;
            if (halvesYears < 0L){
                years = (Math.abs(halvesYears) -1)/2L +1L;
                years *= -1L;
            } else {
                years = halvesYears/2L;
            }

            String half = null;
            if (left != 0L){
                // odd number => 2nd half: "h2"
                //half ="h2"; //
                half = "-07-01";
            } else {
                // even number => 1st half: "h1"
                //half = "h1"; //
                half = "-01-01";
            }
            long year  = 1960L + years;
            String halfYear = Long.valueOf(year).toString() + half;
            dbgLog.finer("rawDatum="+rawDatum+": halfYear="+halfYear);
            
            decodedDateTime = halfYear;
            dbgLog.finer("th:"+decodedDateTime);
            
        } else if (FormatType.matches("^%t?y(\\w|[:\\.])*")){
            // year type's origin is 0 AD
            decodedDateTime = rawDatum;
            dbgLog.finer("th:"+decodedDateTime);
        } else {
            decodedDateTime = rawDatum;
        }
        return decodedDateTime;
    }

}
