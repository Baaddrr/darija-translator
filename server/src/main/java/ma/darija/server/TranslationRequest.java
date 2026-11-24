package ma.darija.server;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;

record TranslationRequest(String text, String sourceLanguage) {
    private static final int MAX_BODY_BYTES = 64 * 1024;
    private static final int MAX_TEXT_LENGTH = 5_000;
    private static final int MAX_LANGUAGE_LENGTH = 64;

    static TranslationRequest read(InputStream body) throws IOException {
        byte[] bytes = body.readNBytes(MAX_BODY_BYTES + 1);
        if (bytes.length > MAX_BODY_BYTES) {
            throw new ApiFailure(413, "request_too_large", "Send a JSON body of at most 64 KiB.");
        }

        JsonObject json;
        try {
            String decoded = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
            try (var parser = Json.createParser(new StringReader(decoded))) {
                if (!parser.hasNext() || parser.next() != JsonParser.Event.START_OBJECT) {
                    throw invalid("Send a valid UTF-8 JSON object.");
                }
                json = parser.getObject();
                if (parser.hasNext()) {
                    throw invalid("Send a single JSON object.");
                }
            }
        } catch (JsonException | CharacterCodingException exception) {
            throw invalid("Send a valid UTF-8 JSON object.");
        }

        String text = stringField(json, "text");
        if (text == null || text.isBlank()) {
            throw invalid("Enter text to translate.");
        }
        if (text.codePointCount(0, text.length()) > MAX_TEXT_LENGTH) {
            throw invalid("Enter at most 5,000 characters.");
        }

        String language = stringField(json, "sourceLanguage");
        language = language == null || language.isBlank() ? "Auto" : language.strip();
        if (language.codePointCount(0, language.length()) > MAX_LANGUAGE_LENGTH
                || language.codePoints().anyMatch(Character::isISOControl)) {
            throw invalid("Enter a source language of at most 64 characters without control characters.");
        }
        return new TranslationRequest(text, language);
    }

    private static String stringField(JsonObject json, String name) {
        JsonValue field = json.get(name);
        if (field == null || field == JsonValue.NULL) {
            return null;
        }
        if (field instanceof JsonString string) {
            String value = string.getString();
            if (value.codePoints().anyMatch(cp -> cp >= Character.MIN_SURROGATE && cp <= Character.MAX_SURROGATE)) {
                throw invalid(name + " must contain valid Unicode.");
            }
            return value;
        }
        throw invalid(name + " must be a string.");
    }

    private static ApiFailure invalid(String message) {
        return new ApiFailure(400, "invalid_request", message);
    }
}
