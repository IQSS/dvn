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
package edu.harvard.iq.dvn.core.web.dataaccess;


import edu.harvard.iq.dvn.core.study.StudyFile;

import java.io.IOException;

/**
 *
 * @author landreev
 */

public class DataAccess {
    public DataAccess() {

    }

    public static DataAccessObject createDataAccessObject (StudyFile sf) throws IOException {
        return createDataAccessObject (sf, null);
    }

    public static DataAccessObject createDataAccessObject (StudyFile sf, DataAccessRequest req) throws IOException {

        if (sf == null ||
                sf.getFileSystemLocation() == null ||
                sf.getFileSystemLocation().equals("")) {
            throw new IOException ("createDataAccessObject: null or invalid study file.");
        }

        if (!sf.isRemote()) {
            return new FileAccessObject (sf, req);
        } else if (sf.getFileSystemLocation().matches(".*census\\.gov.*")) {
                return new CensusAccessObject (sf, req);
        }

        return new HttpAccessObject (sf, req);
    }
}