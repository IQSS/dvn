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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A class that stores categorical statistics of a variable 
 * for rendering the metadata of a data file in DDI
 * (Data Documentation Initiative) format.
 *
 * @author Akio Sone at UNC-Odum
 */
public class CategoricalStatistic {

    /**
     * value of the value field.
     */
     String value;

    /**
     * Returns the value of the value field.
     * @return the value of the value field.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the new value of the value field.
     * 
     * @param value the new value of the value field.
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * value of the label field.
     */
    String label;

    /**
     * Returns the value of the label field.
     * 
     * @return the value of the label field.
     */
    public String getLabel() {
        return label;
    }

    /**
     *  Sets the new value of the label field.
     * @param label the new value of the label field.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * value of the frequency field.
     */
    int frequency;

    /**
     * Returns the frequency of this value.
     *
     * @return the frequency of this value.
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency of this value.
     * 
     * @param frequency the frequency of this value.
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * whether or not this value is a missing value,
     * by default, false.
     */
     boolean missingValue=false;

    /**
     * Tells whether or not this value is a missing value
     * 
     * @return true if this value is missing value.
     */
    public boolean isMissingValue() {
        return missingValue;
    }

    /**
     * Sets the new value of the missingValue field.
     * 
     * @param isMissingValue the new value of the missingValue field.
     */
    public void setMissingValue(boolean isMissingValue) {
        this.missingValue = isMissingValue;
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
