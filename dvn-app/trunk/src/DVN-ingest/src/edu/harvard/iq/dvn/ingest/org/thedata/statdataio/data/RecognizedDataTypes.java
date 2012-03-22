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
package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data;

/**
 * This enum provides constants of the three common
 * dataset organizations: tabular, hierachical, and network.
 *
 * @author Akio Sone at UNC-Odum
 */
public enum RecognizedDataTypes {
    /**
     * Spreadsheet-like format, row and column are variables and cases or
     * <i>vice versa</i>.
     */
    TABULAR,
    /**
     * Tree-like format
     */
    HIERACHICAL,
    /**
     * Consists of data for the arcs and nodes of a network.
     * Used for the Social Network Analysis (SNA)
     */
    NETWORK;
}
