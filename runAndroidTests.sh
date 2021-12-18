#!/bin/bash

function exitTestRun() {
  safeRunCommand "adb pull /sdcard/Android/data/com.vmenon.mpo/files/Pictures/test_run_screenshots /tmp/android_test/screenshots || true"
  safeRunCommand "adb logcat -d > /tmp/android_test/logcat.log || true"
}

trap exitTestRun EXIT
source ./buildEnv.sh
set -e
safeRunCommand "rm -rf /tmp/android_test || true"
safeRunCommand "mkdir -p /tmp/android_test"
safeRunCommand "./android-wait-for-emulator"
safeRunCommand "./gradlew app:deployBundleApksDebug app:installDebugAndroidTest"
#safeRunCommand "adb shell pm grant com.vmenon.mpo android.permission.WRITE_EXTERNAL_STORAGE"
#safeRunCommand "adb shell pm grant com.vmenon.mpo android.permission.READ_EXTERNAL_STORAGE"
safeRunCommand "adb shell am instrument -e listener \"com.vmenon.mpo.test.TestListener\" -e clearPackageData \"true\" -e tags \"@smoke\" -w com.vmenon.mpo.test/com.vmenon.mpo.test.CucumberTestRunner | tee -a /tmp/android_test/adb-test.log"

# adb doesn't propagate exit code from tests, see https://code.google.com/p/android/issues/detail?id=3254
# So we need to parse saved terminal log
safeRunCommand "cat /tmp/android_test/adb-test.log | grep \"OK (\""
#safeRunCommand "./gradlew connectedAndroidTest"
