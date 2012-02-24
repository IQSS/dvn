package edu.harvard.iq.dvn.api.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author leonidandreev
 */
@Provider
public class AuthorizationRequiredExceptionMapper implements ExceptionMapper<AuthorizationRequiredException>{
    
    public Response toResponse(AuthorizationRequiredException exception) {
        return Response.status(401).header("WWW-Authenticate", "Basic realm=\"DVN API\"").build();
    }
    
}
