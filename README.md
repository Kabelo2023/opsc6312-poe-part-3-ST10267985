# SmartPlanner (Android)

A minimal, modern planner app for Android. Users can register/login, manage settings (stored securely), add tasks, and browse tasks via a **weekly calendar bar**. Networking uses Retrofit to a MockAPI backend. UI uses Material 3 with a dark theme.

##  Features

* **Auth**: Firebase Email/Password (secure handling; no client-side password storage).
* **Settings**: Username, email, timezone, dark mode, notifications — saved with **EncryptedSharedPreferences**.
* **Tasks**

  * Add a task (FAB).
  * Swipe **left** to mark done/undone.
  * Swipe **right** to delete with **Undo**.
  * Tasks are filtered by the **selected date** in a weekly calendar bar.
* **REST API**: Retrofit + Moshi + OkHttp logging (MockAPI).
* **Events (sample)**: Load/create demo Events from the API via AppBar actions.

##  Tech Stack

* **Language**: Kotlin (JVM 17)
* **Min/Target SDK**: 24 / 35
* **UI**: Material Components, RecyclerView, ViewBinding
* **Arch**: ViewModel + LiveData, Repository, Coroutines
* **Networking**: Retrofit, Moshi, OkHttp Logging
* **Auth**: Firebase Authentication
* **Security**: EncryptedSharedPreferences (AES-256)
* **Build**: AGP 8.9.x

##  Getting Started

### 1) Prerequisites

* Android Studio **Koala** or newer
* JDK **17**
* A Firebase project (Authentication enabled)
* A MockAPI project (or any hosted REST that matches the endpoints)

### 2) Clone & Open

```bash
git clone <your-repo-url>
cd SmartPlanner
# Open in Android Studio and sync Gradle
```

### 3) Firebase (Auth)

* Add an Android app in Firebase console with your `applicationId` (**com.example.smartplanner** by default).
* Download `google-services.json` into `app/`.
* In `build.gradle.kts (Module: app)` the Google Services plugin is already applied:

  ```kotlin
  plugins {
      id("com.google.gms.google-services")
  }
  ```
* Auth is used in `LoginActivity` / `RegisterActivity`. Passwords are handled securely by Firebase (TLS + server-side hashing). The app itself does **not** store plaintext passwords.

### 4) API (MockAPI)

* Create resources `events` and `tasks` in MockAPI.
* Copy your base URL into `ApiClient.kt`:

  ```kotlin
  private const val BASE_URL = "https://<YOUR-SUBDOMAIN>.mockapi.io/"
  ```
* Endpoints used:

  * `GET /events?createdBy={uid}`
  * `POST /events`
  * `GET /tasks?createdBy={uid}`
  * `POST /tasks`
  * `DELETE /tasks/{id}`

### 5) Run on Emulator/Device

* Build & run from Android Studio.
* Launcher activity is **SplashActivity**, which routes to Login or Home.
* Log in (or register, then log in).
* Tap the **Settings** icon in the top app bar to edit settings.

##  Core Screens

### Home

* Top app bar (menu: Settings, Load Events, Create Sample, Logout).
* **Weekly calendar bar** (horizontal) showing the current week (Sun–Sat). Selecting a day filters tasks.
* Task list with swipe gestures.
* **FAB** to add a task for the selected day.

### Settings

* Fields for username, email, timezone, and two switches.
* Values are saved to **EncryptedSharedPreferences** (`secure_settings.xml`).

### Auth

* Email/password register & login via Firebase Auth.

##  Security Notes

* **Passwords**: Never stored locally; handled by Firebase Auth.
* **Settings**: Stored with **EncryptedSharedPreferences** (AES-256-GCM/SIV).
* **Network**: HTTPS to MockAPI.


##  Testing

* Unit tests: `app/src/test/...`
* Instrumented tests: `app/src/androidTest/...`
* Run from **Run > Run…** or **Run tests** in Android Studio.

##  Troubleshooting

* **Toolbar/menu not showing**: Ensure `setSupportActionBar(binding.topAppBar)` is called in `HomeActivity.onCreate()` and `onCreateOptionsMenu()` inflates `R.menu.top_app_bar_menu`.
* **Seeing app label ("SmartPlanner") instead of "SyncUp"**:

  * Set `supportActionBar?.title = "SyncUp"` after `setSupportActionBar`.
* **MockAPI errors**:

  * Confirm `BASE_URL` in `ApiClient.kt`.
  * Ensure `events`/`tasks` resources exist and you pass a real `createdBy` (Firebase `uid`).
* **Login issues**:

  * Check Firebase Auth is enabled; try creating a new user and check logs.



##  License

This project is provided as-is for educational use. Add your preferred license (MIT, Apache-2.0, etc.) if publishing.

---


