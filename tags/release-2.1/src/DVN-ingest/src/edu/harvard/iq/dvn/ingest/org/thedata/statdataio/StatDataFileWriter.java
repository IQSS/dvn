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

package edu.harvard.iq.dvn.ingest.org.thedata.statdataio;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import java.io.*;
/**
 * An abstract superclass for reading and writing of a statistical data file.
 * A class that implements a writer in the context of StatData I/O
 * framework must subclasse this superclass.
 *
 * @author akio sone
 */
public abstract class StatDataFileWriter {

    /**
     * The <code>StatDataFileWriterSpi</code> object that instantiated this 
     * object, or  <code>null</code> if its identity is not known or none
     * exists.  By default it is initially set to <code>null</code>.
     */
    protected StatDataFileWriterSpi originatingProvider;

    /**
     * Constructs an <code>StatDataFileWriter</code> and sets its 
     * <code>originatingProvider</code> field to the given value.
     * 
     * @param originatingProvider the <code>StatDataFileWriterSpi</code>
     * that invokes this constructor, or <code>null</code>.
     */
    protected StatDataFileWriter(StatDataFileWriterSpi originatingProvider){
        this.originatingProvider = originatingProvider;
    }

    /**
     * Returns the <code>StatDataFileWriterSpi</code> that was supplied to the
     * the constructor. This value may be <code>null</code>.
     * 
     * @return <code>StatDataFileWriterSpi</code>, or <code>null</code>.
     */
    public StatDataFileWriterSpi getOriginatingProvider() {
        return originatingProvider;
    }
    
    /**
     * Writes the given <code>SDIOData</code> as a statistical data file, 
     * using a supplied <code>OutputStream</code>.
     * 
     * @param stream an <code>OutputStream</code> instance where 
     * a statistical data file is connected
     * @param data a <code>SDIOData</code> object 
     * @throws java.io.IOException if a writing error occurs.
     */
    public abstract void write(OutputStream stream, SDIOData data)
        throws IOException;
}
