# Darija Translator

Public source repository: [Baaddrr/darija-translator](https://github.com/Baaddrr/darija-translator).

A Jakarta REST service that uses Google Gemini to translate into Moroccan Darija. The Java service owns provider access. Chrome, PHP, Python and React Native clients are planned around the same HTTP API. See the current [project status](docs/PROGRESS.md) and [acceptance criteria](docs/REQUIREMENTS.md).

## Run the Java backend

Use a JDK supported by the WildFly 42 series (17, 21 or 25), Maven 3.9+, and standard WildFly 42. On Windows, confirm `mvn -version` uses a JDK and set `JAVA_HOME` if the `java` on PATH is older.

Set these variables in the **WildFly process environment** before starting it.

| Variable | Value |
| --- | --- |
| `DARIJA_API_USERNAME` | Nonblank Basic username without a colon |
| `DARIJA_API_PASSWORD` | Nonblank Basic password |
| `GEMINI_API_KEY` | Server-only Google Gemini API key |
| `GEMINI_MODEL` | Optional Gemini model ID. Default `gemini-3.1-flash-lite` |

The [.env.example](.env.example) lists the names. WildFly reads the process environment. Use HTTPS for Basic authentication outside trusted local development. The Google pricing page lists the default model's free tier. Availability and quotas depend on the Google project. [Gemini pricing](https://ai.google.dev/gemini-api/docs/pricing#gemini-3.1-flash-lite)

From the repository root, build, then start WildFly. Use a second shell for the CLI and tests.

```powershell
mvn -f server/pom.xml clean verify
& "$env:JBOSS_HOME/bin/standalone.bat"
```

```powershell
& "$env:JBOSS_HOME/bin/jboss-cli.bat" --connect --file=scripts/configure-wildfly.cli
& "$env:JBOSS_HOME/bin/jboss-cli.bat" --connect "--command=deploy server/target/darija.war --force"
python scripts/test-api.py --live
```

On macOS or Linux, use `"$JBOSS_HOME/bin/standalone.sh"` and `"$JBOSS_HOME/bin/jboss-cli.sh"` with the same CLI arguments. Run the smoke test without `--live` if WildFly was started with no Gemini key. The script expects 503 after authenticating and validating a correct request in that mode. Both smoke-test modes require the Basic credentials in the test shell.

If deployment fails, confirm the CLI setup ran on the same WildFly instance before deploying. The `darija-translator` Undertow mapping uses non-integrated JASPI so identities can come from the environment. The servlet constraint protects the API prefix and declares the health route public. WildFly's Jakarta Authentication bridge delegates to the `BasicAuthentication` Jakarta Security mechanism before the JAX-RS resource runs. [WildFly Elytron activation](https://docs.wildfly.org/42/WildFly_Elytron_Security.html#activation) · [Jakarta Security 4.0](https://jakarta.ee/specifications/security/4.0/jakarta-security-spec-4.0.html)

## HTTP contract

Base URL for the default deployment is `http://127.0.0.1:8080/darija/api`. Client settings should store the API base through `/api` and append `/translate` or `/health`.

| Method and path | Request and response |
| --- | --- |
| `GET /health` | Public liveness. Returns `{"status":"ok"}`. |
| `POST /translate` | HTTP Basic (UTF-8) and `application/json`. Send `{"text":"Good morning","sourceLanguage":"English"}`. Returns `{"translation":"صباح الخير"}` for a successful response of this form. |

Omit `sourceLanguage` or send `"Auto"` for detection. Text may contain line breaks and up to 5,000 Unicode code points. The UTF-8 JSON body limit is 64 KiB. The provider deadline is 25 seconds. Clients should allow at least 30 seconds for the complete request.

Errors have the shape `{"error":{"code":"invalid_request","message":"Enter text to translate."}}`.

| Status | Main cases |
| --- | --- |
| 400 / 413 / 415 | Invalid input or JSON, body too large, unsupported content type |
| 401 | Missing, malformed, or rejected Basic credentials. Includes `WWW-Authenticate`. |
| 429 | Gemini request or quota limit |
| 502 | Provider failure or incomplete/malformed translation |
| 503 | Missing or rejected provider configuration, or authentication configuration unavailable |
| 504 | Provider deadline exceeded |

### cURL examples

```bash
curl --fail-with-body http://127.0.0.1:8080/darija/api/health

curl --fail-with-body --user "$DARIJA_API_USERNAME" \
  --header 'Content-Type: application/json' \
  --data '{"text":"Good morning","sourceLanguage":"English"}' \
  http://127.0.0.1:8080/darija/api/translate
```

The authenticated example prompts for the Basic password. If the service returns 503, verify the environment in the WildFly shell. Use `mvn -f server/pom.xml test` for the controlled local provider tests regardless of key availability.
