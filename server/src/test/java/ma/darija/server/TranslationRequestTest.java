package ma.darija.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TranslationRequestTest {
    @Test
    void retainsUnicodeAndLineBreaksAndTrimsLanguage() throws IOException {
        var request = read("""
                {"text":"Good morning 🌞\\nكيف حالك؟","sourceLanguage":" English "}
                """);
        assertEquals("Good morning 🌞\nكيف حالك؟", request.text());
        assertEquals("English", request.sourceLanguage());
    }

    @Test
    void acceptsMissingSourceLanguageAndCountsCodePoints() throws IOException {
        var request = read("{\"text\":\"" + "🌞".repeat(5_000) + "\"}");
        assertEquals("Auto", request.sourceLanguage());
        assertEquals(5_000, request.text().codePointCount(0, request.text().length()));
        assertEquals(400, assertThrows(ApiFailure.class,
                () -> read("{\"text\":\"" + "🌞".repeat(5_001) + "\"}")).status());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "[]", "{", "{}", "{\"text\":24}", "{\"text\":\" \\t \"}",
            "{\"text\":\"hello\",\"sourceLanguage\":[]}", "{\"text\":\"hello\",\"sourceLanguage\":\"a\\nb\"}",
            "{\"text\":\"hello\"}{}"})
    void rejectsInvalidInput(String json) {
        assertEquals("invalid_request", assertThrows(ApiFailure.class, () -> read(json)).code());
    }

    @Test
    void rejectsOverlargeBodiesBeforeParsing() {
        assertEquals(413, assertThrows(ApiFailure.class, () -> read(" ".repeat(65_537))).status());
    }

    private static TranslationRequest read(String json) throws IOException {
        return TranslationRequest.read(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }
}
