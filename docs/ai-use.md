# AI Use in SmartPlanner

SmartPlanner does **not** send user content to third-party AI services at runtime. AI was used during development for:
- Code generation and refactoring suggestions (e.g., offline cache pattern, FCM wiring).
- Drafting documentation and tests.

Runtime behavior:
- No model inference is performed in the app.
- Network calls are limited to MockAPI, Open-Meteo, and Firebase services (Auth, Messaging, Analytics).
