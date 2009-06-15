/*
 * Dataverse Network - A web application to distribute, share and
 * analyze quantitative data.
 * Copyright (C) 2008
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

/**
 * Description For special numbers (infinity, -infinity,NaN)
 * represents them as in Micah's code
 * @author evillalon
 */
package edu.harvard.iq.dvn.unf;

import java.util.*;
import java.text.*;

public class FormatNumbSymbols {

    private final Locale currentLocale;
    private final DecimalFormatSymbols decimalFmtSymb;

    public FormatNumbSymbols() {
        currentLocale = new Locale("en", "US");
        decimalFmtSymb = new DecimalFormatSymbols(currentLocale);
    }

    public FormatNumbSymbols(Locale currentLocale) {
        this.currentLocale = currentLocale;
        decimalFmtSymb = new DecimalFormatSymbols(currentLocale);

    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Micah algor defining +infinity
     * @return String
     */
    public String getPlusInf() {
        return "+inf";
    }

    /**
     * Micah algor defining -infinity
     * @return String
     */
    public String getMinusInf() {
        return "-inf";
    }

    /**
     * Micah algor defining nan
     * @return String
     */
    public String getNan() {
        return "+nan";
    }

    /**
     * Micah decimal separator
     * @return char
     */
    public char getDecSep() {
        return '.';
    }

    public String getPlusInfinity() {

        return decimalFmtSymb.getInfinity();
    }

    public String getMinusInfinity() {

        return new String(decimalFmtSymb.getMinusSign() +
                decimalFmtSymb.getInfinity());
    }

    public String getNaN() {

        return (decimalFmtSymb.getNaN());

    }

    public char getDecimalSep() {
        return (decimalFmtSymb.getDecimalSeparator());
    }

    public void setDecimalSep(char dot) {
        decimalFmtSymb.setDecimalSeparator(dot);
    }
}
