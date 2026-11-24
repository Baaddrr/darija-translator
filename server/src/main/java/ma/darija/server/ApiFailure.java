package ma.darija.server;

import jakarta.json.Json;
import jakarta.json.JsonObject;

final class ApiFailure extends RuntimeException {
    private final int status;
    private final String code;

    ApiFailure(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    int status() {
        return status;
    }

    String code() {
        return code;
    }

    static JsonObject body(String code, String message) {
        return Json.createObjectBuilder()
                .add("error", Json.createObjectBuilder().add("code", code).add("message", message))
                .build();
    }
}
