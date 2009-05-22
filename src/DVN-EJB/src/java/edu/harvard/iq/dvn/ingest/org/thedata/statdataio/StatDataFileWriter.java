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
 *
 * @author akio sone
 */
public abstract class StatDataFileWriter {

    /**
     *
     */
    protected StatDataFileWriterSpi originatingProvider;

    /**
     *
     * @param originatingProvider
     */
    protected StatDataFileWriter(StatDataFileWriterSpi originatingProvider){
        this.originatingProvider = originatingProvider;
    }

    /**
     *
     * @return
     */
    public StatDataFileWriterSpi getOriginatingProvider() {
        return originatingProvider;
    }
    
    /**
     * 
     * @param stream
     * @param data
     * @return
     * @throws java.io.IOException
     */
    public abstract SDIOData write(OutputStream stream, SDIOData data)
        throws IOException;
}
