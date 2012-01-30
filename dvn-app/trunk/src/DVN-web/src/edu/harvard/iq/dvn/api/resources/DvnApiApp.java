

package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.exceptions.*;
import edu.harvard.iq.dvn.api.entities.*;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author leonidandreev
 */
public class DvnApiApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register root resources/providers
        classes.add(DvnApiRootResource.class);
        classes.add(MetadataInstance.class);
        classes.add(MetadataResourceBean.class);
        classes.add(MetadataHolderSingletonBean.class);
        classes.add(NotFoundException.class);
        classes.add(NotFoundExceptionMapper.class);
        classes.add(MetadataWriter.class);
        return classes;
    }
}