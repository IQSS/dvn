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

package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata;

import java.util.*;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
/**
 * A class that stores information about a variables' invalid data.
 * 
 * @author asone
 */
public class InvalidData {


    /**
     * Constructs an <code>InvalidData</code> object with the given
     * type as an <code>int</code> value.
     * 
     * @param type an <code>int</code> value representing the type of
     * invalid datum.
     */
    public InvalidData(int type) {
        this.type = type;
    }

    /**
     * The type of a <code>InvalidData</code> object represented by 
     * <code>int</code> following the coding scheme of SPSS SAV foramt.
     * 
     */
    int type;

    /**
     * Returns the value of the type field.
     * 
     * @return the value of the type field.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the value of the type field.
     * 
     * @param type new value of the type field.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * A <code>List</code> object that stores invalid values,
     * usually missing values.
     */
    List<String> invalidValues;

    /**
     * Returns the value of the invalidValues field.
     * 
     * @return a <code>List</code> object that holds invalid values.
     */
    public List<String> getInvalidValues() {
        return invalidValues;
    }

    /**
     * Sets the value of the invalidValues field.
     
     * @param invalidValues a <code>List</code> object
     * that holds invalid values.
     */
    
    public void setInvalidValues(List<String> invalidValues) {
        this.invalidValues = invalidValues;
    }

    /**
     * A <code>List</code> object that stores information about 
     * the invalid range.
     */
    List<String> invalidRange;

    /**
     * Returns the value of the invalidRange field.
     * 
     * @return a <code>List</code> object that holds data 
     * about the invalid range.
     */
    public List<String> getInvalidRange() {
        return invalidRange;
    }

    /**
     * Sets the value of the invalidRange field.
     * 
     * @param invalidRange a <code>List</code> object
     * that holds data about the invalid range.
     */
    public void setInvalidRange(List<String> invalidRange) {
        this.invalidRange = invalidRange;
    }



    /**
     * Returns a string representation of this instance as
     * an invalrng tag of the DDI format.
     * 
     * @return a string as an invalrng tag of the DDI format.
     */
    public String toDDItag(){
        StringBuilder sb = new StringBuilder();

        switch(type){
            case 1: case 2: case 3:
                    sb.append("\t\t<invalrng>\n");
                    for (int k=0; k < invalidValues.size();k++){
                        sb.append("\t\t\t<item VALUE=\"" + invalidValues.get(k)+"\"/>\n");
                    }
                    sb.append("\t\t</invalrng>\n");
                break;
            case -2:
                    // range-type 1 missing values
                    sb.append("\t\t<invalrng>\n");
                    sb.append("\t\t\t<range");
                    if (!invalidRange.get(0).equals("LOWEST")){
                        sb.append(" min=\""+invalidRange.get(0)+"\"");
                    }
                    if (!invalidRange.get(1).equals("HIGHEST")) {
                        sb.append(" max=\"" + invalidRange.get(1) + "\"");
                    }
                    sb.append("/>\n");
                    sb.append("\t\t</invalrng>\n");
                break;
            case -3:
                    // range-type: 2 missing values
                    sb.append("\t\t<invalrng>\n");
                    sb.append("\t\t\t<range");
                    if (!invalidRange.get(0).equals("LOWEST")) {
                        sb.append(" min=\""+invalidRange.get(0)+"\"");
                    }
                    if (!invalidRange.get(1).equals("HIGHEST")) {
                        sb.append(" max=\"" + invalidRange.get(1) +"\"");
                    }
                    sb.append("/>\n");
                    sb.append("\t\t\t<item VALUE=\"" +invalidValues.get(0)+"\"/>\n");
                    sb.append("\t\t</invalrng>\n");

                break;
            default:
        }
        return sb.toString();
    }

    /**
     * Returns a string representation of this instance.
     * 
     * @return a string representing this instance.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE);
    }

}
