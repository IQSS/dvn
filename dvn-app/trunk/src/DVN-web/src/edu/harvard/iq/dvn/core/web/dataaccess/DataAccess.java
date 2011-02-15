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
        } else if (sf.getFileSystemLocation().matches("census\\.gov")) {
                return new CensusAccessObject (sf, req);
        }

        return new HttpAccessObject (sf, req);
    }
}