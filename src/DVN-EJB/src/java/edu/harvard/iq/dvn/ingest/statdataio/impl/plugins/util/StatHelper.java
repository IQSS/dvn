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

import static java.lang.System.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.apache.commons.lang.*;
import org.apache.commons.lang.builder.*;
import org.apache.commons.math.stat.*;
import cern.colt.list.*;
import cern.jet.stat.Descriptive;




/**
 *
 * @author Akio Sone at UNC-Odum
 */


public class StatHelper {


    private static final byte STATA_MISSING_VALUE_BIAS = 26;
//    private static byte BYTE_MISSING_VALUE = Byte.MAX_VALUE - STATA_MISSING_VALUE_BIAS;
//    private static short SHORT_MISSIG_VALUE = Short.MAX_VALUE - STATA_MISSING_VALUE_BIAS;
//    private static int INT_MISSING_VALUE = Integer.MAX_VALUE - STATA_MISSING_VALUE_BIAS;
//    private static long LONG_MISSING_VALUE = Long.MAX_VALUE - STATA_MISSING_VALUE_BIAS;
    private static byte BYTE_MISSING_VALUE = Byte.MAX_VALUE;
    private static short SHORT_MISSIG_VALUE = Short.MAX_VALUE;
    private static int INT_MISSING_VALUE = Integer.MAX_VALUE;
    private static long LONG_MISSING_VALUE = Long.MAX_VALUE;

    /**
     *
     */
    public static final int MAX_CATEGORIES = 50;

   private static Logger dbgLog =
       Logger.getLogger(StatHelper.class.getPackage().getName());
    /**
     *
     * @param x
     * @return
     */
    public static double[] byteToDouble(byte[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = x[i] == BYTE_MISSING_VALUE ? Double.NaN : (double)x[i];
        }
        return z;
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] shortToDouble(short[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = x[i] == SHORT_MISSIG_VALUE ? Double.NaN : (double)x[i];
        }
        return z;
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] intToDouble(int[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = x[i] == INT_MISSING_VALUE ? Double.NaN : (double)x[i];
        }
        return z;
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] longToDouble(long[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = x[i] == Long.MAX_VALUE ? Double.NaN : (double)x[i];
        }
        return z;
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] floatToDouble(float[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] =  x[i] == Float.NaN ? Double.NaN : (double)x[i];
        }
        return z;
    }

    /**
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
     */
    public static double[] calculateSummaryStatistics(byte[] x){
        double[] z = byteToDouble(x);
        return calculateSummaryStatistics(z);
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] calculateSummaryStatistics(short[] x){
        double[] z = shortToDouble(x);
        return calculateSummaryStatistics(z);
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] calculateSummaryStatistics(int[] x){
        double[] z = intToDouble(x);
        return calculateSummaryStatistics(z);
    }


    /**
     *
     * @param x
     * @return
     */
    public static double[] calculateSummaryStatistics(long[] x){
        Map<String, Integer> freqTable = calculateCategoryStatistics(x);
        //out.println(freqTable);
        double[] z = longToDouble(x);
        return calculateSummaryStatistics(z);
    }

    /**
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
     */
    public static double[] calculateSummaryStatistics(float[] x){
        double[] z = floatToDouble(x);
        return calculateSummaryStatistics(z);
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] calculateSummaryStatistics(double[] x){
        double[] newx = removeNaNs(x);
        double[] nx = new double[8];
        //("mean", "medn", "mode", "vald", "invd", "min", "max", "stdev");

        nx[0] = StatUtils.mean(newx);
        nx[1] = StatUtils.percentile(x, 50);
        nx[2] = getMode(x);
        nx[4] = countNaNs(x);//countNaNs(x);
        nx[3] = x.length - nx[4];

        nx[5] = StatUtils.min(x);
        nx[6] = StatUtils.max(x);
        nx[7] = Math.sqrt(StatUtils.variance(newx));
        return nx;
    }

    /**
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param FrequencyTable
     * @return
     */
    public static String getMode(Map<String, Integer> FrequencyTable) {
        ArrayList<Map.Entry<String, Integer>> entries =
            new ArrayList<Map.Entry<String, Integer>>(FrequencyTable.entrySet());
        sortMapEntryListByValue(entries);

        return entries.get(0).getKey();
    }

    /**
     * 
     * @param FrequencyTable
     * @return
     */
    public static int getMaxResponse(Map<String, Integer> FrequencyTable) {
        ArrayList<Map.Entry<String, Integer>> entries =
            new ArrayList<Map.Entry<String, Integer>>(FrequencyTable.entrySet());
        sortMapEntryListByValue(entries);

        return entries.get(0).getValue();
    }
    /**
     *
     * @param entries
     */
    public static void sortMapEntryListByValue(
        List<Map.Entry<String, Integer>> entries){
        sortMapEntryListByValue(entries, true);
    }

    /**
     *
     * @param entries
     * @param descending
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     *
     * @param x
     * @return
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
     * 
     * @param data
     * @return
     */
    public static byte[] toByteArrays(byte data) {
        return new byte[]{data};
    }

    /**
     *
     * @param data
     * @return
     */
    public static byte[] toByteArrays(short data) {
        return new byte[] {
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }

    /**
     *
     * @param data
     * @return
     */
    public static byte[] toByteArrays(char data) {
        return new byte[] {
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }

    /**
     *
     * @param data
     * @return
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
     *
     * @param data
     * @return
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
     *
     * @param data
     * @return
     */
    public static byte[] toByteArrays(float data) {
        return toByteArrays(Float.floatToRawIntBits(data));
    }


    /**
     *
     * @param data
     * @return
     */
    public static byte[] toByteArrays(double data) {
        return toByteArrays(Double.doubleToRawLongBits(data));
    }


    /**
     *
     * @param data
     * @return
     */
    public static byte[] toByteArrays(boolean data) {
        return new byte[]{(byte)(data ? 0x01 : 0x00)}; // bool -> {1 byte}
    }


    /**
     *
     * @param data
     * @return
     */
    public static byte[] toByteArrays(String data) {
        return (data == null) ? null : data.getBytes();
    }


    /**
     *
     * @param data
     * @param charsetName
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public static byte[] toByteArrays(String data, String charsetName)
        throws UnsupportedEncodingException {
        return (data == null) ? null : data.getBytes(charsetName);
    }
}
