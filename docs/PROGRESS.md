# Progress

## Current phase

Backend checkpoint, 2026-11-24. The API contract is documented and the next implementation task is the Chrome extension. The user authorized publication to the public GitHub repository [Baaddrr/darija-translator](https://github.com/Baaddrr/darija-translator), configured as `origin`.

## Completed

- Maven WAR for Jakarta EE 11 on WildFly 42, targeting Java 17. Added public health, protected translation, bounded UTF-8 JSON validation and stable error codes.
- Configurable Gemini REST integration with server-only credentials, separate translation instructions and input, a full-response deadline and handling for rate limits and incomplete responses.
- Basic authentication through the Jakarta Authentication servlet bridge with an application-specific Elytron mapping. Credentials come from the WildFly environment.
- Persistent requirements, progress, handoff and project instructions, plus build, configuration and HTTP examples.

## Verification

- JDK 17.0.19 and Maven 3.9.16 compiled the server and ran 26 tests. All passed. The provider tests use an ephemeral loopback mock for payload, Unicode, parsing, errors, rate limit, and a stalled response body.
- Maven packaged `server/target/darija.war` after resolving the packaging dependencies. Deployed that WAR on WildFly 42.0.0.Beta1 and ran `python scripts/test-api.py --api http://127.0.0.1:8260/darija/api`. Public liveness, JSON 401 challenges, authenticated validation, body and media-type limits, and the missing-key 503 all passed.
- After routing authentication errors through the container error dispatch, the deployed checks passed again. The server log contained no request exception, smoke-test credentials, or source text. The temporary server was shut down.
- The local shell defaults to Java 8. Set `JAVA_HOME` to `C:\Users\asus\.jdks\ms-17.0.19` before using Maven or WildFly on this host. The installed WildFly is `C:\Program Files\wildfly-42.0.0.Beta1`. Maven 3.9.16 is available under `C:\Users\asus\.m2\wrapper\dists\apache-maven-3.9.16`.
- A live Gemini translation requires `GEMINI_API_KEY`. Run `python scripts/test-api.py --live` with the deployed backend and matching test-shell Basic credentials. Account quota and natural Darija output still require live verification.

## Pending

1. Implement and verify the Manifest V3 Chrome side-panel client as described in [HANDOFF.md](HANDOFF.md).
2. Implement and verify PHP, PySide6 and React Native clients. Verify all four clients with a deployed backend.
3. Complete PlantUML class and deployment diagrams, all client instructions, demo outline, and final requirements audit.
4. Track the assignment's recorded demo through completion with the user. The public GitHub repository contains the completed backend checkpoint. Client projects and final deliverables remain pending.
