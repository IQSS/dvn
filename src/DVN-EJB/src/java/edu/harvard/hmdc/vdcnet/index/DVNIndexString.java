/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.index;

/**
 *
 * @author roberttreacy
 */
public class DVNIndexString {
    
    public static String parse(String value) {
        char[] valueChars = value.toCharArray();
        int vLen = valueChars.length;
        char[] dvnChars = new char[vLen];
        for (int x = 0; x < vLen; x++) {
            if (Character.isLetterOrDigit(valueChars[x])) {
                dvnChars[x] = valueChars[x];
            } else {
                dvnChars[x] = ' ';
            }
        }
        return  new String(dvnChars);
    }

}
