/*
 * Dataverse Network - A web application to distribute, share and
 * analyze quantitative data.
 * Copyright (C) 2009
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util;


import java.io.*;
import java.util.*;
import java.util.logging.*;
//import static java.lang.System.*;

import org.apache.commons.lang.*;
import org.apache.commons.math.stat.*;
import cern.colt.list.*;
import cern.jet.stat.Descriptive;




/**
 * <code>StatHelper</code> provides static methods for computing descriptive statistics and
 * frequency tables based on data stored in primitive arrays and for converting
 * a primitive, non-double array into a double array and for encoding a primitive
 * and String object into a sequence of bytes.
 *
 * @author Akio Sone at UNC-Odum
 */


public class StatHelper {

    // static fields ---------------------------------------------------------//
    private static final byte STATA_MISSING_VALUE_BIAS = 26;

    private static byte BYTE_MISSING_VALUE = Byte.MAX_VALUE;
    private static short SHORT_MISSIG_VALUE = Short.MAX_VALUE;
    private static int INT_MISSING_VALUE = Integer.MAX_VALUE;
    private static long LONG_MISSING_VALUE = Long.MAX_VALUE;

    /**
     * The maximum number of categories in a frequency table to be returned
     */
    public static final int MAX_CATEGORIES = 50;

   private static Logger dbgLog =
       Logger.getLogger(StatHelper.class.getPackage().getName());

    // static Methods --------------------------------------------------------//
    /**
     * Converts an array of primitive bytes to doubles
     *
     * @param x    a byte array
     * @return     a double array
     */
    public static double[] byteToDouble(byte[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = x[i] == BYTE_MISSING_VALUE ? Double.NaN : (double)x[i];
        }
        return z;
    }

    /**
     * Converts an array of primitive shorts to doubles
     *
     * @param x     a short array
     * @return      a double array
     */
    public static double[] shortToDouble(short[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = x[i] == SHORT_MISSIG_VALUE ? Double.NaN : (double)x[i];
        }
        return z;
    }

    /**
     * Converts an array of primitive ints to doubles
     *
     * @param x     an int array
     * @return      a double array
     */
    public static double[] intToDouble(int[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = x[i] == INT_MISSING_VALUE ? Double.NaN : (double)x[i];
        }
        return z;
    }

    /**
     * Converts an array of primitive longs to doubles
     *
     * @param x     a long array
     * @return      a double array
     */
    public static double[] longToDouble(long[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = x[i] == Long.MAX_VALUE ? Double.NaN : (double)x[i];
        }
        return z;
    }

    /**
     * Converts an array of primitive floats to doubles
     *
     * @param x     a float array
     * @return      a double array
     */
    public static double[] floatToDouble(float[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] =  x[i] == Float.NaN ? Double.NaN : (double)x[i];
        }
        return z;
    }

    /**
     * Returns a new double array of non-Double.NaN values only
     *
     * @param x     a double array
     * @return      a double array
     */
    public static double[] removeNaNs(double[] x){
        List<Double> dl = new ArrayList<Double>();
        for (double d : x){
            if (!Double.isNaN(d)){
                dl.add(d);
            }
        }
        return ArrayUtils.toPrimitive(
            dl.toArray(new Double[dl.size()]));
    }

    /**
     * Returns a new String array of non-empty data only
     * @param x     a String array
     * @return      a String array
     */
    public static String[] removeEmptyElements(String[] x){
        List<String> sl = new ArrayList<String>();
        for (String s : x){
            if (!StringUtils.isEmpty(s)){
                sl.add(s);
            }
        }
        return sl.toArray(new String[sl.size()]);
    }

    /**
     * Returns a double array of descriptive statistics from a byte array
     *
     * @param x     a byte array
     * @return      a double array
     */
    public static double[] calculateSummaryStatistics(byte[] x){
        double[] z = byteToDouble(x);
        return calculateSummaryStatistics(z);
    }

    /**
     * Returns a double array of descriptive statistics from a short array
     * @param x
     * @return      a double array
     */
    public static double[] calculateSummaryStatistics(short[] x){
        double[] z = shortToDouble(x);
        return calculateSummaryStatistics(z);
    }

    /**
     * Returns a double array of descriptive statistics from an int array
     *
     * @param x     an int array
     * @return      a double array
     */
    public static double[] calculateSummaryStatistics(int[] x){
        double[] z = intToDouble(x);
        return calculateSummaryStatistics(z);
    }


    /**
     *  Returns a double array of descriptive statistics from a long array
     *
     * @param x     a long array
     * @return      a double array
     */
    public static double[] calculateSummaryStatistics(long[] x){
        Map<String, Integer> freqTable = calculateCategoryStatistics(x);
        //out.println(freqTable);
        double[] z = longToDouble(x);
        return calculateSummaryStatistics(z);
    }

    /**
     * Returns a Map whose keys are the set of responses within a byte array,
     * and whose values are the corresponding frequencies.  If the number of
     * categories is more than MAX_CATEGORIES, returns null.
     * 
     * @param x     a byte array
     * @return      a Map as a categorical statistical table
     */
    public static Map<String, Integer> calculateCategoryStatistics(byte[] x){
        Map<String, Integer> frqTbl=null;
        int nobs =x.length;
        if ((nobs == 0) ) {
            frqTbl =null;
        } else {
            frqTbl = getFrequencyTable(x);
            //out.println("frequency Table="+frqTbl);
            String MissingValueKey = Byte.toString(Byte.MAX_VALUE);
            if (frqTbl.keySet().contains(MissingValueKey)){
                frqTbl.put(".", frqTbl.get(MissingValueKey));
                frqTbl.remove(MissingValueKey);
            }

            if (frqTbl.keySet().size() > MAX_CATEGORIES){
                frqTbl = null;
            }
        }

        return frqTbl;
    }

    /**
     * Returns a Map whose keys are the set of responses within a short array,
     * and whose values are the corresponding frequencies.  If the number of
     * categories is more than MAX_CATEGORIES, returns null.
     *
     * @param x a short array
     * @return  a Map as a categorical statistical table
     */
    public static Map<String, Integer> calculateCategoryStatistics(short[] x){
        Map<String, Integer> frqTbl=null;
        int nobs =x.length;
        if ((nobs == 0) ) {
            frqTbl =null;
        } else {
            frqTbl = getFrequencyTable(x);
            String MissingValueKey = Short.toString(Short.MAX_VALUE);
            if (frqTbl.keySet().contains(MissingValueKey)){
                frqTbl.put(".", frqTbl.get(MissingValueKey));
                frqTbl.remove(MissingValueKey);
            }

            //out.println("frequency Table="+frqTbl);
            if (frqTbl.keySet().size() > MAX_CATEGORIES){
                frqTbl = null;
            }
        }

        return frqTbl;
    }


    /**
     * Returns a Map whose keys are the set of responses within an int array,
     * and whose values are the corresponding frequencies.  If the number of
     * categories is more than MAX_CATEGORIES, returns null.
     * 
     * @param x an int array
     * @return  a Map as a categorical statistical table
     */
    public static Map<String, Integer> calculateCategoryStatistics(int[] x){
        Map<String, Integer> frqTbl=null;
        int nobs =x.length;
        if ((nobs == 0) ) {
            frqTbl =null;
        } else {
            frqTbl = getFrequencyTable(x);
            String MissingValueKey = Integer.toString(Integer.MAX_VALUE);
            if (frqTbl.keySet().contains(MissingValueKey)){
                frqTbl.put(".", frqTbl.get(MissingValueKey));
                frqTbl.remove(MissingValueKey);
            }
            //out.println("frequency Table="+frqTbl);
            if (frqTbl.keySet().size() > MAX_CATEGORIES){
                frqTbl = null;
            }
        }

        return frqTbl;
    }




    /**
     * Returns a Map whose keys are the set of responses within a long array,
     * and whose values are the corresponding frequencies.  If the number of
     * categories is more than MAX_CATEGORIES, returns null.
     *
     * @param x a long array
     * @return  a Map as a categorical statistical table
     */
    public static Map<String, Integer> calculateCategoryStatistics(long[] x){

        Map<String, Integer> frqTbl=null;
        int nobs =x.length;
        if ((nobs == 0) ) {
            frqTbl =null;
        } else {
            frqTbl = getFrequencyTable(x);

            String MissingValueKey = Long.toString(Long.MAX_VALUE);
            if (frqTbl.keySet().contains(MissingValueKey)){
                frqTbl.put(".", frqTbl.get(MissingValueKey));
                frqTbl.remove(MissingValueKey);
            }

            //out.println("frequency Table="+frqTbl);
            if (frqTbl.keySet().size() > MAX_CATEGORIES){
                frqTbl = null;
            }
        }

        return frqTbl;
    }


    /**
     * <p>Returns a Map whose keys are the set of responses within a float array,
     * and whose values are the corresponding frequencies.</p>
     *
     * <p>This methods is for a date or time variable whose storage type is not
     * integer-type but float.</p>
     *
     * @param x a float array
     * @return  a Map as a categorical statistical table
     */
    public static Map<String, Integer> calculateCategoryStatistics(float[] x){
        long[] ftol = new long[x.length];
        for (int i=0; i< x.length; i++){
            ftol[i] = Float.valueOf(x[i]).longValue();
        }
        return calculateCategoryStatistics(ftol);
    }

    /**
     * <p>Returns a Map whose keys are the set of responses within a double array,
     * and whose values are the corresponding frequencies.</p>
     *
     * <p>This methods is for a date or time variable whose storage type is not
     * integer-type but double.</p>
     *
     * @param x a double array
     * @return  a Map as a categorical statistical table
     */
    public static Map<String, Integer> calculateCategoryStatistics(double[] x){
        long[] dtol = new long[x.length];
        for (int i=0; i< x.length; i++){
            dtol[i] = Double.valueOf(x[i]).longValue();
        }
        return calculateCategoryStatistics(dtol);
    }

    /**
     * Returns a Map whose keys are the set of responses within a String array,
     * and whose values are the corresponding frequencies. If the number of
     * categories is more than MAX_CATEGORIES, returns null.
     *
     * @param x a String array
     * @return  a Map as a categorical statistical table
     */
    public static Map<String, Integer> calculateCategoryStatistics(String[] x){
        //double[] z = longToDouble(x);
        Map<String, Integer> frqTbl=null;
        int nobs =x.length;
        if ((nobs == 0) ) {
            frqTbl =null;
        } else {
            frqTbl = getFrequencyTable(x);
            //out.println("frequency Table="+frqTbl);
            if (frqTbl.keySet().size() > MAX_CATEGORIES){
                frqTbl = null;
            }
        }

        return frqTbl;
    }

    /**
     * Returns a double array of descriptive statistics from a float array
     *
     * @param x     a float array
     * @return      a double array
     */
    public static double[] calculateSummaryStatistics(float[] x){
        double[] z = floatToDouble(x);
        return calculateSummaryStatistics(z);
    }

    /**
     * Returns a double array of descriptive statistics from a double array
     *
     * @param x     a float array
     * @return      a double array
     */
    public static double[] calculateSummaryStatistics(double[] x){
        double[] newx = removeNaNs(x);
        double[] nx = new double[8];
        //("mean", "medn", "mode", "vald", "invd", "min", "max", "stdev");

        nx[0] = StatUtils.mean(newx);
        nx[1] = StatUtils.percentile(newx, 50);
        nx[2] = getMode(newx);
        nx[4] = countNaNs(x);//countNaNs(x);
        nx[3] = x.length - nx[4];

        nx[5] = StatUtils.min(newx);
        nx[6] = StatUtils.max(newx);
        nx[7] = Math.sqrt(StatUtils.variance(newx));
        return nx;
    }

    /**
     * Returns a String array of descriptive statistics from a String array
     *
     * @param x     a String array
     * @return      a String array
     */
    public static String[] calculateSummaryStatistics(String[] x){
        
        String[] nx = new String[3];
        //( "vald", "invd", "mode")
        int validcases = (removeEmptyElements(x)).length;
        
        nx[0] = Integer.toString(validcases);  // valid responses
        nx[1] = Integer.toString((x.length - validcases)); // invalid responses
        nx[2] = getMode(x);
        return nx;
    }


    /**
     * Returns the number of Double.NaNs in a double-type array
     *
     * @param x a double array
     * @return  the number of Double.NaNs
     */
    public static int countNaNs(double[] x){
        int NaNcounter=0;
        for (int i=0; i<x.length;i++){
            if (Double.isNaN(x[i])){
                NaNcounter++;
            }
        }
        return NaNcounter;
    }

    /**
     * Returns the mode statistic of a double variable
     *
     * @param x     a double array
     * @return      the mode value as double
     */
    public static double getMode(double[] x){
        double mode = Double.NaN;

        if ((countNaNs(x) == x.length) || (x.length < 1)){
            return mode;
        } else {
            DoubleArrayList dx = new DoubleArrayList(x);
            dx.sort();
            DoubleArrayList freqTable = new DoubleArrayList(1);
            IntArrayList countTable = new IntArrayList(1);
            Descriptive.frequencies(dx, freqTable, countTable);
            //out.println("freqTable="+
            //    ReflectionToStringBuilder.toString(freqTable));
            //out.println("freqTable="+
            //    ReflectionToStringBuilder.toString(countTable));
            int max_i = 0;
            for (int i=1; i< countTable.size();i++ ){
                if (countTable.get(i)> countTable.get(max_i)){
                    max_i = i;
                }
            }
            mode = freqTable.get(max_i);
            //out.println("position = "+
            //max_i+"\tits value="+freqTable.get(max_i));
        }
        return mode;
    }

    /**
     * Returns the mode statistic of a String variable
     *
     * @param x     a String array
     * @return      the mode value as String
     */
    public static String getMode(String[] x){
        String mode = null;
        int nobs = removeEmptyElements(x).length;
        if ((nobs == 0) || (nobs == x.length)) {
            mode ="";
        } else {
            Map<String, Integer> frqTbl = getFrequencyTable(x);
            if (frqTbl.keySet().size() < nobs){
                mode= getMode(frqTbl);
            } else {
                mode = "";
            }
        }
        return mode;
    }
    
    /**
     * Returns the mode statistic (String) of a frequency table as a Map object
     * whose responses (keys) are String and their corresponding frequencies are
     * Integers
     * 
     * @param FrequencyTable    the Map whose keys are String and values are
     *                           Integers
     * @return                  the mode statistic of the frequency table as a
     *                           String
     */
    public static String getMode(Map<String, Integer> FrequencyTable) {
        ArrayList<Map.Entry<String, Integer>> entries =
            new ArrayList<Map.Entry<String, Integer>>(FrequencyTable.entrySet());
        sortMapEntryListByValue(entries);

        return entries.get(0).getKey();
    }

    /**
     * Returns the mode statistic (int) of a frequency table as a Map object
     * whose responses (keys) are String and their corresponding frequencies are
     * Integers
     *
     * @param FrequencyTable    the Map whose keys are String and values are
     *                           Integers
     * @return                  the mode statistic of the frequency table as
     *                           an int
     */
    public static int getMaxResponse(Map<String, Integer> FrequencyTable) {
        ArrayList<Map.Entry<String, Integer>> entries =
            new ArrayList<Map.Entry<String, Integer>>(FrequencyTable.entrySet());
        sortMapEntryListByValue(entries);

        return entries.get(0).getValue();
    }

    /**
     * Sorts a List of Map.Entry into descending order by value
     *
     * @param entries   a List of Map.Etnry objects
     */
    public static void sortMapEntryListByValue(
        List<Map.Entry<String, Integer>> entries){
        sortMapEntryListByValue(entries, true);
    }

    /**
     * Sorts a List of Map.Entry into descending or ascending order by value
     *
     * @param entries       a List of Map.Etnry objects
     * @param descending    sort order
     */
    public static void sortMapEntryListByValue(
        List<Map.Entry<String, Integer>> entries, boolean descending){
        final int order = descending ? -1 : 1;
        Collections.sort(entries,
            new Comparator<Map.Entry<String, Integer>>(){
                public int compare(Map.Entry<String, Integer> e1,
                                      Map.Entry<String, Integer> e2){
                    Integer e1Value =  e1.getValue();
                    Integer e2Value =  e2.getValue();
                    return order*(e1Value.compareTo(e2Value));
                }
                public boolean equals(Object obj){
                      return super.equals(obj);
                }
            }
        );
        //return entries;
    }

    /**
     * Returns a frequency table constructed from a byte array
     *
     * @param x     a byte array
     * @return      a Map whose keys are the set of responses in the byte array,
     *               and whose values are the corresponding frequencies
     */
    public static Map<String, Integer>getFrequencyTable(byte[] x){
        Map<String, Integer> tbl = new TreeMap<String, Integer>();
        for (Byte entry : x) {
            String sentry = String.valueOf(entry);
            Integer freq = tbl.get(sentry);
            tbl.put(sentry, (freq == null ? 1 : freq + 1));
        }
        return tbl;
    }

    /**
     * Returns a frequency table constructed from a short array
     *
     * @param x     a short array
     * @return      a Map whose keys are the set of responses in the short array,
     *               and whose values are the corresponding frequencies
     */
    public static Map<String, Integer>getFrequencyTable(short[] x){
        Map<String, Integer> tbl = new TreeMap<String, Integer>();
        for (Short entry : x) {
            String sentry = String.valueOf(entry);
            Integer freq = tbl.get(sentry);
            tbl.put(sentry, (freq == null ? 1 : freq + 1));
        }
        return tbl;
    }

    /**
     * Returns a frequency table constructed from an int array
     *
     * @param x     an int array
     * @return      a Map whose keys are the set of responses in the int array,
     *               and whose values are the corresponding frequencies
     */
    public static Map<String, Integer>getFrequencyTable(int[] x){
        Map<String, Integer> tbl = new TreeMap<String, Integer>();
        for (Integer entry : x) {
            String sentry = String.valueOf(entry);
            Integer freq = tbl.get(sentry);
            tbl.put(sentry, (freq == null ? 1 : freq + 1));
        }
        return tbl;
    }

    /**
     * Returns a frequency table constructed from a long array
     *
     * @param x     a long array
     * @return      a Map whose keys are the set of responses in the long array,
     *               and whose values are the corresponding frequencies
     */
    public static Map<String, Integer>getFrequencyTable(long[] x){
        Map<String, Integer> tbl = new TreeMap<String, Integer>();
        for (Long entry : x) {
            String sentry = String.valueOf(entry);
            Integer freq = tbl.get(sentry);
            tbl.put(sentry, (freq == null ? 1 : freq + 1));
        }
        //out.println("x="+Arrays.toString(x));
        //out.println("tbl(getFrequencyTable):"+tbl);
        return tbl;
    }

    /**
     * Returns a frequency table constructed from a float array
     *
     * @param x     a float array
     * @return      a Map whose keys are the set of responses in the float array,
     *               and whose values are the corresponding frequencies
     */
    public static Map<String, Integer>getFrequencyTable(float[] x){
        Map<String, Integer> tbl = new TreeMap<String, Integer>();
        for (Float entry : x) {
            String sentry = String.valueOf(entry);
            Integer freq = tbl.get(sentry);
            tbl.put(sentry, (freq == null ? 1 : freq + 1));
        }
        return tbl;
    }


    /**
     * Returns a frequency table constructed from a double array
     *
     * @param x     a double array
     * @return      a Map whose keys are the set of responses in the double array,
     *               and whose values are the corresponding frequencies
     */
    public static Map<String, Integer>getFrequencyTable(double[] x){
        Map<String, Integer> tbl = new TreeMap<String, Integer>();
        for (Double entry : x) {
            String sentry = String.valueOf(entry);
            Integer freq = tbl.get(sentry);
            tbl.put(sentry, (freq == null ? 1 : freq + 1));
        }
        return tbl;
    }

    /**
     * Returns a frequency table constructed from a String array
     *
     * @param x     a String array
     * @return      a Map whose keys are the set of responses in the String array,
     *               and whose values are the corresponding frequencies
     */
    public static Map<String, Integer>getFrequencyTable(String[] x){
        Map<String, Integer> tbl = new TreeMap<String, Integer>();
        for (String sentry : x) {
            if (sentry == null){
                continue;
            }
            Integer freq = tbl.get(sentry);
            tbl.put(sentry, (freq == null ? 1 : freq + 1));
        }
        return tbl;
    }


    /**
     * Converts a primitive byte into an array of bytes
     *
     * @param data  a primitive byte
     * @return      a byte array
     */
    public static byte[] toByteArrays(byte data) {
        return new byte[]{data};
    }

    /**
     * Converts a primitive short into an array of bytes
     *
     * @param data      a primitive short
     * @return          a byte array
     */
    public static byte[] toByteArrays(short data) {
        return new byte[] {
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }

    /**
     * Converts a primitive char into an array of bytes
     *
     * @param data      a primitive char
     * @return          a byte array
     */
    public static byte[] toByteArrays(char data) {
        return new byte[] {
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }

    /**
     * Converts a primitive int into an array of bytes
     *
     * @param data      a primitive int
     * @return          a byte array
     */
    public static byte[] toByteArrays(int data) {
        return new byte[] {
            (byte)((data >> 24) & 0xff),
            (byte)((data >> 16) & 0xff),
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }


    /**
     * Converts a primitive long into an array of bytes
     *
     * @param data      a primitive long
     * @return          a byte array
     */
    public static byte[] toByteArrays(long data) {
        return new byte[] {
            (byte)((data >> 56) & 0xff),
            (byte)((data >> 48) & 0xff),
            (byte)((data >> 40) & 0xff),
            (byte)((data >> 32) & 0xff),
            (byte)((data >> 24) & 0xff),
            (byte)((data >> 16) & 0xff),
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }


    /**
     * Converts a primitive float into an array of bytes
     *
     * @param data      a primitive float
     * @return          a byte array
     */
    public static byte[] toByteArrays(float data) {
        return toByteArrays(Float.floatToRawIntBits(data));
    }


    /**
     * Converts a primitive double into an array of bytes
     *
     * @param data      a primitive double
     * @return          a byte array
     */
    public static byte[] toByteArrays(double data) {
        return toByteArrays(Double.doubleToRawLongBits(data));
    }


    /**
     * Converts a primitive boolean into an array of bytes
     *
     * @param data      a primitive boolean
     * @return          a byte array
     */
    public static byte[] toByteArrays(boolean data) {
        return new byte[]{(byte)(data ? 0x01 : 0x00)}; // bool -> {1 byte}
    }


    /**
     * Converts a String into an array of bytes using the platform's default
     * charset
     *
     * @param data      a String
     * @return          a byte array
     */
    public static byte[] toByteArrays(String data) {
        return (data == null) ? null : data.getBytes();
    }


    /**
     * Converts a String into an array of bytes using the named charset
     *
     * @param data          a String
     * @param charsetName   the name of a supported charset
     * @return              a byte array
     * @throws java.io.UnsupportedEncodingException
     */
    public static byte[] toByteArrays(String data, String charsetName)
        throws UnsupportedEncodingException {
        return (data == null) ? null : data.getBytes(charsetName);
    }
}
