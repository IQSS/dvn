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
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package edu.harvard.hmdc.vdcnet.util;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 *
 * Compares String objects alphabetically, except if both Strings begin with a number, 
 * then compares numerically.  If one String begins with a number and the other doesn't, then 
 * the number precedes the non-number.
 *
 *  @throws ClassCastException if either parameter is not a String
 * @author Ellen Kraffmiller
 */
public class AlphaNumericComparator implements Comparator {
    public int compare(Object o1,Object o2) {
        String str1 = (String)o1;
        String str2 = (String)o2;
        BigDecimal num1 = getNumber(str1);
        BigDecimal num2 = getNumber(str2);
        if (num1==null) {
            if (num2==null) {
                // str1 and str2 both begin with alpha
                return str1.compareTo(str2);
            } else {
                // str1 begins with alpha; str2 begins with number
                return 1;
            }
        } else {
            if (num2==null) {
                // str1 begins with number; str2 begins with alpha
                return -1;
            } else {
                // str1 and str2 both begin with number
                return num1.compareTo(num2);
            }
        }
    }
    
 
    
    public boolean equals(Object obj) {
        return (obj instanceof AlphaNumericComparator);
    }

    public AlphaNumericComparator() {
    }

    private  BigDecimal getNumber(String s) {
        char period = '.';
        char comma = ',';
        BigDecimal bigDecimal=null;
        StringBuffer strBuff = new StringBuffer("");
        char[] charArray = s.trim().toCharArray();
        for (int i=0;i<charArray.length;i++) {
            char c =charArray[i];
            boolean isNextChar = i+1<charArray.length;
            if(Character.isDigit(c) || 
                (c==period && isNextChar && Character.isDigit(charArray[i+1])) ||
                (c==comma && isNextChar && Character.isDigit(charArray[i+1]))){
                strBuff.append(c);
            }
        }
        String numString = strBuff.toString();
        if (!numString.equals("")) {
            bigDecimal = new BigDecimal(numString);
       }
       return bigDecimal;
    }

}
