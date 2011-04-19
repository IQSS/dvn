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
 * A superclass for representing data in a statistical data file.
 * 
 * @author Akio Sone
 */
public class Data {

    /**
     * A <code>Sring</code> array that contains each variable's UNF values.
     */
    String[] unf;

    /**
     * Sets the value of unf
     * @param unf new value of unf
     */
    public void setUnf(String[] unf){
        this.unf = unf;
    }

    /**
     * Gets the value of unf
     *
     * @return the value of unf
     */
    public String[] getUnf(){
        return unf;
    }

    /**
     * A <code>Sring</code> contains the UNF value of this file.
     */
    String fileUnf;


    /**
     * Sets the value of fileUNF
     *
     * @param fileUNF new value of fileUNF
     */
    public void setFileUnf(String fileUNF){
        this.fileUnf = fileUNF;
    }


    /**
     * Gets the value of fileUNF
     *
     * @return the value of fileUNF
     */
    public String getFileUnf(){
        return fileUnf;
    }
    
}
