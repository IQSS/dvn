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
