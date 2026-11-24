# Requirements and acceptance

## Professor's brief

Source: Prof. El Habib Nfaoui, *Mini Project 2: LLM-powered RESTful Web Service*, p. 1. The project brief supplied in chat selects the specific stack and verification details below.

| Assignment requirement | Acceptance evidence |
| --- | --- |
| Maven Java REST service | A deployable WAR with Jakarta REST `TranslatorResource.translate()` translates source text into Moroccan Darija through a real free-tier-capable LLM. |
| HTTP verification | Document authenticated cURL calls or an equivalent tool and run them against a deployed service. |
| Jakarta Authentication | The container's Jakarta Authentication mechanism verifies Basic credentials before translation. Missing and wrong credentials return 401. Valid credentials reach the API. |
| Chrome extension | Manifest V3 context menu transfers selected webpage text to a genuine `chrome.sidePanel` and starts translation when configured. |
| Three other clients | Runnable PHP, Python and React Native apps consume the Java endpoint. |
| UML | Submit class and deployment diagrams that reflect the implementation. Keep editable PlantUML and render where possible. |
| Demo and source sharing | A five-minute source walkthrough and live demo video plus a shared GitHub link with full source, history and a descriptive README. The implementation phase prepares a demo outline. Recording, publication and submission are tracked separately. |

Speech features, a local LLM, an embedded container, use-case and sequence diagrams, and a technical post are optional. The initial scope excludes optional feature development.

## Implementation acceptance

- Backend uses WildFly, a public health route and a protected JSON translation route. The Gemini model and secrets are environment-configured. Return natural Darija in Arabic script while preserving meaning, tone, names and numbers. Bound requests and provider waits. Return clear JSON errors for validation, auth, upstream failures and rate limits.
- All clients accept a source language and multiline Unicode text, show progress and errors asynchronously, display and copy RTL output, and allow backend and credential configuration. Keep input on failure and prevent duplicate work.
- Chrome uses the native context menu and side panel, with selection handoff that works before panel load and across worker restarts. Support toolbar and manual use, copy and clear, and request only the permissions needed.
- PHP uses cURL server-side with separate request handling. Python uses PySide6 widgets with off-thread HTTP, a requirements file, copy and clear. Mobile uses Expo and native React Native controls, safe areas, keyboard handling and emulator/device network setup.
- Keep the translation workspace primary. Use restrained visual styling, accessible interactions and platform-appropriate layouts. Verify rendered screens and real interactions where available.
- Automate backend and meaningful client checks and run a separate live Gemini test when configured. Cover authentication, empty and invalid input, correct requests, Arabic and Unicode, malformed upstream responses, provider failure, timeouts and throttling.
- README covers prerequisites, configuration, deployment, all client launches, HTTP contract, tests, and troubleshooting. Include example environment variables and a five-minute demo outline. Final audit records actual results and any device, provider, or submission work pending.
