# FoodWatch
This is an Android app that lets users quickly browse and search restaurant health inspection histories for my hometown: Surrey, BC. It uses data from the City of Surrey website, and I plan to add more cities depending on the available APIs.

How to try the app:
1. Pull to a local repository
2. Move the file .../FoodWatch/app/app-release.apk to a physical device or Android emulator with API level 23 or higher
3. Find the file on your device and tap it to install the app (you may need to modify security settings to allow installation of apk files from unknown sources.)

How to build this project:
1. Pull to a local repository
2. Open a command prompt in .../FoodWatch directory
3. Connect a physical device or start an Android emulator with API level 23 or higher
4. Run the command "gradlew tasks" to see a list of tasks you can run (e.g. "gradlew installDebug" will install the app on the connected device. "gradlew createDebugCoverageReport" will run all automated tests and create a code coverage report.)

You shouldn't need a local installation of Gradle as the wrapper will install it for you on your first "gradlew" command.