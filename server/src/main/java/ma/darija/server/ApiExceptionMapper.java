package ma.darija.server;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Exception> {
    private static final System.Logger LOG = System.getLogger(ApiExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof ApiFailure failure) {
            return json(Response.status(failure.status()), failure.code(), failure.getMessage());
        }
        if (exception instanceof WebApplicationException webException) {
            Response original = webException.getResponse();
            return switch (original.getStatus()) {
                case 400 -> json(Response.fromResponse(original), "invalid_request", "Send a valid JSON request.");
                case 404 -> json(Response.fromResponse(original), "not_found", "This API route does not exist.");
                case 405 -> json(Response.fromResponse(original), "method_not_allowed", "Use the method allowed for this route.");
                case 406 -> json(Response.fromResponse(original), "not_acceptable", "Accept an application/json response.");
                case 415 -> json(Response.fromResponse(original), "unsupported_media_type", "Send application/json.");
                default -> json(Response.fromResponse(original), "http_error", "The request could not be completed.");
            };
        }
        LOG.log(System.Logger.Level.ERROR, "Unhandled API exception: {0}", exception.getClass().getName());
        return json(Response.serverError(), "internal_error", "The service could not complete the request.");
    }

    private static Response json(Response.ResponseBuilder response, String code, String message) {
        return response.type(MediaType.APPLICATION_JSON).entity(ApiFailure.body(code, message)).build();
    }
}
