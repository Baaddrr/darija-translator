package ma.darija.server;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.InputStream;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class TranslatorResource {
    @Inject
    GeminiTranslator translator;

    @GET
    @Path("health")
    public JsonObject health() {
        return Json.createObjectBuilder().add("status", "ok").build();
    }

    @POST
    @Path("translate")
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonObject translate(InputStream body) {
        final TranslationRequest request;
        try {
            request = TranslationRequest.read(body);
        } catch (IOException exception) {
            throw new ApiFailure(400, "invalid_request", "The request body could not be read.");
        }
        return Json.createObjectBuilder().add("translation", translator.translate(request)).build();
    }
}
