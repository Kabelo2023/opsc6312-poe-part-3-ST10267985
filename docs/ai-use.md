Use of AI Tools 

I used AI (ChatGPT) as a coding assistant to speed up implementation and debugging for the SmartPlanner Android app. The tool was used in four main ways:

Scaffold and feature code generation.

Produced initial Kotlin scaffolding for UI features such as the weekly calendar bar (RecyclerView + adapter), swipe actions on the task list (ItemTouchHelper), and a dark-mode toggle (AppCompatDelegate + SharedPreferences).

Generated boilerplate for logout flow (clearing back stack and FirebaseAuth.signOut), local notifications (WorkManager + NotificationCompat) and recurring tasks (data model + occursOn() utility).

Targeted debugging.

Helped diagnose Gradle/JDK errors (e.g., “Cannot find a Java installation matching languageVersion=17”) by pointing Android Studio to a JDK 17 toolchain and adjusting Gradle JDK settings.

Interpreted “Address already in use: bind” by explaining conflicting emulators/ports and suggesting how to free the port or kill the running process.

Resolved “Unresolved reference: RecyclerView/ItemTouchHelper” by confirming missing imports and dependency alignment.

Documentation and communication.

Drafted concise commit messages, PR descriptions, and a short in-app explanation of where the REST API is called (TaskViewModel load/add) for the video walkthrough.

Verification and originality.
All AI-generated code was reviewed, compiled, and tested in the Android emulator; I adapted snippets to fit my project structure and naming. Where the AI suggested third-party concepts, I verified APIs against official Android documentation. No large external code blocks were copied verbatim beyond short boilerplate necessary for Android components.

No AI-generated images were used.
