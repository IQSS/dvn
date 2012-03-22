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
