package edu.harvard.iq.dvn.api.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author leonidandreev
 */
@Provider
public class ServiceUnavailableExceptionMapper implements ExceptionMapper<ServiceUnavailableException> {

    public Response toResponse(ServiceUnavailableException exception) {
        return Response.status(503).build();
    }

}