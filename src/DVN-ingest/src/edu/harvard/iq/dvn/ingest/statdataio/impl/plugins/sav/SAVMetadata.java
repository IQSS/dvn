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
package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.sav;

import java.util.*;
import static java.lang.System.*;
import org.apache.commons.lang.builder.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;

/**
 *
 * @author Akio Sone
 */
public class SAVMetadata extends SDIOMetadata{

    private static String[] SAV_FILE_INFORMATION_ITEMS= {
        "releaseNo", "byteOrder", "OSByteOrder"};

    
    /**
     * Constructs a <code>SAVMetadata</code> object. 
     */
    public SAVMetadata() {
        super();
        _init();
    }

    private void _init(){
        for (String it : COMMON_FILE_INFORMATION_ITEMS){
            fileInformation.put(it, "");
        }

        for (String it : SAV_FILE_INFORMATION_ITEMS){
            fileInformation.put(it, "");
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE);
    }
}
