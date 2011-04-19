/* MPL License text (see http://www.mozilla.org/MPL/) */

package edu.harvard.iq.dvn.core.web.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.File;
import java.io.Serializable;


/**
 * <p>The InputFileSessionCleaner is responsible for cleaning up any files
 * that may have been uploaded by a session.  The InputFile component by
 * default stores files in an upload folder in the root directory of the
 * deployed application in a child folder named after session id.</p>
 * <p>In most deployment scenarios there is no need to keep files that
 * where uploaded during the session.  This class implements
 * HttpSessionListener and will clean up any uploaded files when the
 * #sessionDestroyed method is called be the Servlet container </p>
 * <p>Make sure that this been is properly register as a listener in the
 * application web.xml descriptor file.</p>
 *
 * @since 1.7
 */
public class InputFileSessionCleaner implements HttpSessionListener, Serializable {

    public static final Log log = LogFactory.getLog(InputFileSessionCleaner.class);

    public static final String FILE_UPLOAD_DIRECTORY = "upload";

    /**
     * This method is called by the servlet container when the session
     * is about to expire. This method will attempt to delete all files that
     * where uploaded into the folder which has the same name as the session
     * id.
     *
     * @param event JSF session event.
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        // get the session id, so we know which folder to remove
        String sessionId = event.getSession().getId();

        String applicationPath = event.getSession().getServletContext().getRealPath(
                event.getSession().getServletContext().getServletContextName());

        String sessionFileUploadPath =
                applicationPath + FILE_UPLOAD_DIRECTORY + sessionId;

        File sessionfileUploadDirectory = new File(sessionFileUploadPath);

        if (sessionfileUploadDirectory.isDirectory()) {
            try {
                sessionfileUploadDirectory.delete();
            }
            catch (SecurityException e) {
                log.error("Error deleting file upload directory: ", e);
            }
        }

    }

    public void sessionCreated(HttpSessionEvent event) {

    }

}
