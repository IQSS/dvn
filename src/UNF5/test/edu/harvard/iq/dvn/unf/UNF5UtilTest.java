/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.unf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author roberttreacy
 */
public class UNF5UtilTest {

    public UNF5UtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of calculateUNF method, of class UNF5Util.
     */
    @Test
    public void testCalculateUNF_doubleArr() throws Exception {
        System.out.println("calculateDoubleUNF");
        List testData = readFileData("test/DoubleTest");
        double[] numb = new double[testData.size()-1];
        String expResult =  (String) testData.get(0);
        for (int i=1; i < testData.size(); i++){
            numb[i-1] = Double.parseDouble((String) testData.get(i));
        }
        String result = UNF5Util.calculateUNF(numb);
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateUNF method, of class UNF5Util.
     */
    @Test
    public void testCalculateUNF_floatArr() throws Exception {
        System.out.println("calculateFloatUNF");
        List testData = readFileData("test/FloatTest");
        float[] numb = new float[testData.size() - 1];
        String expResult = (String) testData.get(0);
        for (int i = 1; i < testData.size(); i++) {
            numb[i - 1] = Float.parseFloat((String) testData.get(i));
        }
        String result = UNF5Util.calculateUNF(numb);
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateUNF method, of class UNF5Util.
     */
    @Test
    public void testCalculateUNF_shortArr() throws Exception {
        System.out.println("calculateShortUNF");
        List testData = readFileData("test/ShortTest");
        short[] numb = new short[testData.size() - 1];
        String expResult = (String) testData.get(0);
        for (int i = 1; i < testData.size(); i++) {
            numb[i - 1] = Short.parseShort((String) testData.get(i));
        }
        String result = UNF5Util.calculateUNF(numb);
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateUNF method, of class UNF5Util.
     */
    @Test
    public void testCalculateUNF_byteArr() throws Exception {
        System.out.println("calculateByteUNF");
        List testData = readFileData("test/ByteTest");
        byte[] numb = new byte[testData.size() - 1];
        String expResult = (String) testData.get(0);
        for (int i = 1; i < testData.size(); i++) {
            numb[i - 1] = Byte.parseByte((String) testData.get(i));
        }
        String result = UNF5Util.calculateUNF(numb);
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateUNF method, of class UNF5Util.
     */
    @Test
    public void testCalculateUNF_longArr() throws Exception {
        System.out.println("calculateLongUNF");
        List testData = readFileData("test/LongTest");
        long[] numb = new long[testData.size() - 1];
        String expResult = (String) testData.get(0);
        for (int i = 1; i < testData.size(); i++) {
            numb[i - 1] = Long.parseLong((String) testData.get(i));
        }
        String result = UNF5Util.calculateUNF(numb);
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateUNF method, of class UNF5Util.
     */
    @Test
    public void testCalculateUNF_intArr() throws Exception {
        System.out.println("calculateIntUNF");
        List testData = readFileData("test/IntTest");
        int[] numb = new int[testData.size() - 1];
        String expResult = (String) testData.get(0);
        for (int i = 1; i < testData.size(); i++) {
            numb[i - 1] = Integer.parseInt((String) testData.get(i));
        }
        String result = UNF5Util.calculateUNF(numb);
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateUNF method, of class UNF5Util.
     */
    @Test
    public void testCalculateUNF_booleanArr() throws Exception {
        System.out.println("calculateBooleanUNF");
        List testData = readFileData("test/BooleanTest");
        boolean[] numb = new boolean[testData.size() - 1];
        String expResult = (String) testData.get(0);
        for (int i = 1; i < testData.size(); i++) {
            numb[i - 1] = Boolean.parseBoolean((String) testData.get(i));
        }
        String result = UNF5Util.calculateUNF(numb);
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateUNF method, of class UNF5Util.
     */
    @Test
    public void testCalculateUNF_StringArr() throws Exception {
        System.out.println("calculateStringUNF");
        List testData = readFileData("test/StringTest");
        String[] chr = new String[testData.size() - 1];
        String expResult = (String) testData.get(0);
        for (int i = 1; i < testData.size(); i++) {
            chr[i - 1] = (String) testData.get(i);
        }
        String result = UNF5Util.calculateUNF(chr);
        assertEquals(expResult, result);
    }


    /**
     * Test of calculateUNF method, of class UNF5Util.
     */
    @Test
    public void testCalculateUNF_StringArr_StringArr() throws Exception {
        System.out.println("calculateDateTimeUNF");
        List testData = readFileData("test/DateTimeTest");
        String[] chr = new String[testData.size() - 1];
        String[] sdfFormat = new String[testData.size() - 1];
        String expResult = (String) testData.get(0);
        for (int i = 1; i < testData.size(); i++) {
            String dateAndFormat = (String) testData.get(i);
            int separatorIndex = dateAndFormat.indexOf('~');
            chr[i - 1] = dateAndFormat.substring(0, separatorIndex);
            sdfFormat[i - 1] = dateAndFormat.substring(separatorIndex+2);
        }
        String result = UNF5Util.calculateUNF(chr, sdfFormat);
        assertEquals(expResult, result);
    }
    /**
     * Test of calculateUNF method, of class UNF5Util.
     */
    @Test
    public void testCalculateUNF_BitStringArr() throws Exception {
        System.out.println("calculateUNF");
        List testData = readFileData("test/BitStringTest");
        BitString[] numb = new BitString[testData.size() - 1];
        String expResult = (String) testData.get(0);
        for (int i = 1; i < testData.size(); i++) {
            numb[i - 1] = new BitString((String) testData.get(i));
        }
        String result = UNF5Util.calculateUNF(numb);
        assertEquals(expResult, result);
    }

    private List readFileData(String filename) {
        List retList = new ArrayList();
        File file = new File(filename);
        InputStream in = null;
        try {
            in = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                retList.add(line);
            }
        } catch (IOException x) {
            System.err.println(x);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(UNF5UtilTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return retList;
    }
}