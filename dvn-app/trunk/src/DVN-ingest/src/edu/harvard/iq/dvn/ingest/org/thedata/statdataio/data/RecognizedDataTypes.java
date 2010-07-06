/*
 * Dataverse Network - A web application to distribute, share and
 * analyze quantitative data.
 * Copyright (C) 2009
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
