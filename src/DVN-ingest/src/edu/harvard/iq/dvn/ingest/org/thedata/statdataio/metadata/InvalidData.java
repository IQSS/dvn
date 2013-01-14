/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
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
