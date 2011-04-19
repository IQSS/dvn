package edu.harvard.iq.dvn.core.web.dataaccess;

// java core imports:
import java.util.Map;
import java.util.HashMap;


// DVN App imports:
import edu.harvard.iq.dvn.core.study.StudyFile;

/**
 *
 * @author landreev
 */

public class DataAccessRequest {

    public DataAccessRequest () {
        this(null);
    }

    public DataAccessRequest (StudyFile file) {
        this.file = file;
        this.requestParameters = new HashMap<String, String>();
    }

    private StudyFile file;
    private Map<String, String> requestParameters;

    public void setFile (StudyFile file) {
        this.file = file;
    }

    public StudyFile getFile () {
        return this.file; 
    }

    public void setParameter (String name, String value) {
        if (requestParameters != null) {
            requestParameters.put(name, value);
        }
    }

    public String getParameter (String name) {
        if (requestParameters != null) {
            return requestParameters.get(name);
        }
        return null; 
    }

    private DataAccessRequest(Object object) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
