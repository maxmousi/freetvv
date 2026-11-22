BladiiTV - Google TV app
========================
Method 1 (GitHub, nothing to install):
 1. Create a new repository on github.com and upload everything in this folder (including .github).
 2. Open Actions > Build APK > Run workflow.
 3. After ~3 minutes download the file from Artifacts: BladiiTV-apk.

Method 2 (Android Studio):
 1. File > Open, select this folder, wait for Sync.
 2. Build > Build APK(s). Output: app/build/outputs/apk/debug/app-debug.apk

Install on Google TV:
 - Enable Developer options, then Network debugging
 - adb connect TV_IP  then  adb install app-debug.apk
 - or copy the file with "Send Files to TV" and install it with a file manager.

To change the site or the ad list: edit the constants at the top of MainActivity.java
