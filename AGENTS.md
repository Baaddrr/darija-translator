# Darija Translator

- Read [requirements](docs/REQUIREMENTS.md), [progress](docs/PROGRESS.md), and [handoff](docs/HANDOFF.md) before changing code. Inspect the source related to the next task and keep these files current.
- Keep a Maven Jakarta REST WAR on WildFly and separate Manifest V3 Chrome, PHP cURL, PySide6, and Expo React Native applications. Use the Java API as the single translation boundary. Keep the Gemini key on the server and protect translation with Jakarta Authentication and HTTP Basic. Do not commit or log credentials or submitted text.
- Preserve the documented JSON API. Keep resource, validation, provider, authentication, networking, and presentation responsibilities clear. Prefer straightforward code and framework features. Use test doubles only in explicit tests.
- Keep each client responsive during translation. Preserve failed input for retry, prevent duplicate requests, and show concise feedback. Use neutral colors, one accent, accessible controls, RTL Darija output and platform-appropriate layouts. Keep styling and networking separate.
- Apply the good-code-no-slop skill for implementation and review. Use premium-web-design for relevant UI work and honor the platform and assignment constraints.
- Run focused tests for the current work and record actual results and unverified behavior. At a coherent session boundary, update the project docs, make a descriptive local Git commit, and give the user the next task and a short continuation prompt. Leave remote pushes and submission for an explicitly authorized step.
