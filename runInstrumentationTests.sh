#!/bin/bash
source ./buildEnv.sh
set -e
safeRunCommand "./android-wait-for-emulator"
safeRunCommand "./gradlew app:deployBundleApksDebug app:installDebugAndroidTest"
safeRunCommand "adb shell am instrument -e listener \"com.vmenon.mpo.test.TestListener\" -e clearPackageData \"true\" -e tags \"@smoke\" -w com.vmenon.mpo.test/com.vmenon.mpo.test.CucumberTestRunner"
#safeRunCommand "./gradlew connectedAndroidTest"
safeRunCommand "adb pull /sdcard/Pictures/screenshots /tmp/android_test_screenshots"