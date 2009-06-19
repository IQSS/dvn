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
 *
 * @author asone
 */
public class InvalidData {


    public InvalidData(int type) {
        this.type = type;
//        if (type > 0){
//            invalidValues = new ArrayList<String>(type);
//        } else if (type == -2){
//            invalidRange = new ArrayList<String>(2);
//        } else if (type == -3){
//            invalidValues = new ArrayList<String>(1);
//            invalidRange = new ArrayList<String>(2);
//        }
    }

    protected int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    protected List<String> invalidValues;

    public List<String> getInvalidValues() {
        return invalidValues;
    }

    public void setInvalidValues(List<String> invalidValues) {
        this.invalidValues = invalidValues;
    }

    protected List<String> invalidRange;

    public List<String> getInvalidRange() {
        return invalidRange;
    }

    public void setInvalidRange(List<String> invalidRange) {
        this.invalidRange = invalidRange;
    }



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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE);
    }

}
