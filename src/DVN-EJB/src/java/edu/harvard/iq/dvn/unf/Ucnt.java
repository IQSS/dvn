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
 * Description: Unicodes characters 
 * @Author Elena Villalon
 *  email:evillalon@iq.harvard.edu
 *  
 */
package edu.harvard.iq.dvn.unf;

public enum Ucnt {

    dot('\u002E'), //decimal separator "."
    plus('\u002b'),//"+" sign
    min('\u002d'),//"-"
    e('\u0065'), //"e"
    percntg('\u0025'),//"%"
    pndsgn('\u0023'), //"#"
    zero('\u0030'), //'0'
    s('\u0073'),//"s"
    nil('\u0000'), //'\0' for null terminator
    frmfeed('\u000C'), //form feed
    ls('\u2028'),//line separator
    nel('\u0085'),//next line
    psxendln('\n'); //posix end-of-line
    private final char ucode;

    Ucnt(char c) {
        this.ucode = c;
    }

    public char getUcode() {
        return ucode;
    }
}
