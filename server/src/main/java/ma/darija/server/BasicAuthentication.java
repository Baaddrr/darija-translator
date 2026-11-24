package ma.darija.server;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Set;

@ApplicationScoped
public class BasicAuthentication implements HttpAuthenticationMechanism {
    private final String username = System.getenv("DARIJA_API_USERNAME");
    private final String password = System.getenv("DARIJA_API_PASSWORD");

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response,
                                                HttpMessageContext context) {
        if (!context.isProtected()) {
            return context.doNothing();
        }
        if (username == null || username.isBlank() || username.contains(":") || password == null || password.isBlank()) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return AuthenticationStatus.SEND_FAILURE;
        }
        if (matches(request.getHeader("Authorization"))) {
            return context.notifyContainerAboutLogin(new CallerPrincipal(username), Set.of());
        }

        response.setHeader("WWW-Authenticate", "Basic realm=\"Darija Translator\", charset=\"UTF-8\"");
        return context.responseUnauthorized();
    }

    private boolean matches(String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, "Basic ", 0, 6)) {
            return false;
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(authorization.substring(6).strip());
            String credentials = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
            int colon = credentials.indexOf(':');
            if (colon < 0) {
                return false;
            }
            boolean userMatches = equal(username, credentials.substring(0, colon));
            boolean passwordMatches = equal(password, credentials.substring(colon + 1));
            return userMatches && passwordMatches;
        } catch (IllegalArgumentException | CharacterCodingException exception) {
            return false;
        }
    }

    private static boolean equal(String configured, String supplied) {
        return MessageDigest.isEqual(configured.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8));
    }

}
