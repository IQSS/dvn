package edu.harvard.iq.dvn.api.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author leonidandreev
 */
@Provider
public class PermissionDeniedExceptionMapper implements ExceptionMapper<PermissionDeniedException> {
    public Response toResponse(PermissionDeniedException exception) {
        return Response.status(403).build();
    }
    
}
