# Handoff

## Next task

Implement and verify the Manifest V3 Chrome client in `chrome-extension/` against the existing Java API. Keep the backend contract in the [README](../README.md) and the acceptance criteria in [REQUIREMENTS.md](REQUIREMENTS.md). Read [PROGRESS.md](PROGRESS.md) for the exact verification status.

1. Check current Chrome documentation for context menus, side-panel activation, extension storage and optional host permissions. Add an action toolbar entry, background service worker, context menu named **Translate to Darija**, side panel, and backend settings.
2. On context click, open the real side panel using the click gesture and hand selected text to it using session storage and messaging. Cover click before panel load, worker restart, and duplicate notifications. Scope selections to the right tab or browser window.
3. Wire manual and automatic translation to the same asynchronous API path. Preserve input on error. Verify source language, RTL result, copy, clear, busy state, and credential/backend setup with minimum permissions.
4. Run focused client checks and load the unpacked extension in Chrome where available. Record separately whether a configured live Gemini translation was verified. Update the checkpoints and make a local commit before starting another expensive phase.

## Continuation prompt

> Resume the local Darija Translator repo at the path I provide. Read AGENTS.md and docs/REQUIREMENTS.md, PROGRESS.md and HANDOFF.md. Implement and verify the Manifest V3 Chrome side-panel client against the current API. Preserve the completed backend, update the checkpoint docs, and commit locally without pushing.
