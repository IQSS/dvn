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
import java.util.*;

import org.apache.commons.lang.*;
import org.apache.commons.lang.builder.*;
import org.apache.commons.math.stat.*;
import cern.colt.list.*;
import cern.jet.stat.Descriptive;

/**
 *
 * @author Akio Sone
 */


public class StatHelper {


    /**
     *
     * @param x
     * @return
     */
    public static double[] byte2double(byte[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = (double)x[i];
        }
        return z;
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] short2double(short[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = (double)x[i];
        }
        return z;
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] int2double(int[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = (double)x[i];
        }
        return z;
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] long2double(long[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = (double)x[i];
        }
        return z;
    }

    /**
     * 
     * @param x
     * @return
     */
    public static double[] float2double(float[] x){
        double[] z= new double[x.length];
        for (int i=0; i<x.length;i++){
            z[i] = (double)x[i];
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
        double[] z = byte2double(x);
        return calculateSummaryStatistics(z);
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] calculateSummaryStatistics(short[] x){
        double[] z = short2double(x);
        return calculateSummaryStatistics(z);
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] calculateSummaryStatistics(int[] x){
        double[] z = int2double(x);
        return calculateSummaryStatistics(z);
    }


    /**
     *
     * @param x
     * @return
     */
    public static double[] calculateSummaryStatistics(long[] x){
        double[] z = long2double(x);
        return calculateSummaryStatistics(z);
    }

    /**
     *
     * @param x
     * @return
     */
    public static double[] calculateSummaryStatistics(float[] x){
        double[] z = float2double(x);
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
        String mode = "";

        if (removeEmptyElements(x).length == 0) {
            return mode;
        } else {
          // To be complted later probably with help of suitable Java packages
        }
        return mode;
    }
}
