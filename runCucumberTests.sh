#!/bin/bash

function exitTestRun() {
  safeRunCommand "adb pull /sdcard/Android/data/com.vmenon.mpo/files/Pictures/test_run_screenshots /tmp/android_test/screenshots || true"
  safeRunCommand "adb pull /sdcard/cucumber_tests_screenrecord.mp4 /tmp/android_test/cucumber_tests_screenrecord.mp4 || true"
  safeRunCommand "adb logcat -d > /tmp/android_test/logcat.log || true"
}

trap exitTestRun EXIT
source ./buildEnv.sh
set -e
safeRunCommand "rm -rf /tmp/android_test || true"
safeRunCommand "mkdir -p /tmp/android_test"
safeRunCommand "adb shell screenrecord /sdcard/cucumber_tests_screenrecord.mp4 &"
SCREENRECORD_PID=$!
safeRunCommand "adb shell am instrument -e listener \"com.vmenon.mpo.test.TestListener\" -e coverage \"true\" -e coverageFile \"/sdcard/Android/data/com.vmenon.mpo/files/coverage.ec\" -e clearPackageData \"true\" -e tags \"@smoke\" -w com.vmenon.mpo.test/com.vmenon.mpo.test.CucumberTestRunner | tee -a /tmp/android_test/adb-test.log"
safeRunCommand "kill $SCREENRECORD_PID"
# Wait for 3 seconds for the device to compile the video
safeRunCommand "sleep 3"
# adb doesn't propagate exit code from tests, see https://code.google.com/p/android/issues/detail?id=3254
# So we need to parse saved terminal log
safeRunCommand "cat /tmp/android_test/adb-test.log | grep \"OK (\""
safeRunCommand "mkdir -p ./app/build/outputs/code_coverage/debugAndroidTest/connected"
safeRunCommand "adb pull /sdcard/Android/data/com.vmenon.mpo/files/coverage.ec ./app/build/outputs/code_coverage/debugAndroidTest/connected"