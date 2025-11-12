# SmartPlanner (Android, Kotlin)

Task planning with mock REST, Firebase Auth (email + Google), offline cache (Room), weather card, PDF/ICS export, and push notifications.

## Features
- Email/password **and Google SSO** (Firebase)
- Tasks + Events via Retrofit to MockAPI
- **Offline mode** for tasks (Room cache; create offline, sync when online)
- Focus/Pomodoro & Insights
- **Weather** card with hourly WorkManager sync
- **Realtime** notifications (FCM topic `all`)
- **Multi-language** (English, Afrikaans, Zulu) via in-app language switch
- Export **PDF** and **ICS**
- Dark/Light theme

## Getting started
1. Clone the repo.
2. Open in Android Studio (Giraffe / Hedgehog+).
3. Create a Firebase project, enable **Email/Password** and **Google** providers.
4. Download `google-services.json` into `app/`.
5. Set your MockAPI base URL in `ApiClient.kt`.
6. Run the app.

## Testing
- Unit: `./gradlew test`
- Instrumented: `./gradlew connectedAndroidTest`

## Build/CI
A GitHub Actions workflow builds the app and runs tests on every push/PR.

## Release (Play Store)
- Set `versionCode`/`versionName` in `app/build.gradle.kts`.
- Create a **release** signing config (Android Studio > Build > Generate Signed App Bundle).
- Fill in store listing + privacy policy (push notifications, network, analytics).
- Upload AAB.

## AI Use
See [`docs/ai-use.md`](docs/ai-use.md).

## License
MIT
