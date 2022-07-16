#!/bin/bash

function exitTestRun() {
  echo "runCucumberTests.sh failed"
  safeRunCommand "jobs"
  safeRunCommand "kill $(jobs -p) || true"
  safeRunCommand "adb pull /sdcard/Android/data/com.vmenon.mpo/files/Pictures/test_run_screenshots /tmp/android_test/screenshots || true"
  safeRunCommand "adb pull /sdcard/cucumber_tests_screenrecord /tmp/android_test/cucumber_tests_screenrecord || true"
  safeRunCommand "adb logcat -d > /tmp/android_test/adb-test-${CUCUMBER_TAG}.log || true"
}

trap exitTestRun EXIT
source ./buildEnv.sh
set -e
CUCUMBER_TAG=$1
CUCUMBER_TIMEOUT_SECS=600 # 10 minutes
safeRunCommand "rm -rf /tmp/android_test || true"
safeRunCommand "mkdir -p /tmp/android_test"
safeRunCommand "adb shell mkdir -p /sdcard/cucumber_tests_screenrecord"
safeRunCommand "adb shell screenrecord /sdcard/cucumber_tests_screenrecord/$CUCUMBER_TAG.mp4 &"
SCREENRECORD_PID=$!
safeRunCommand "timeout $CUCUMBER_TIMEOUT_SECS adb shell am instrument -e listener \"com.vmenon.mpo.test.TestListener\" -e coverage \"true\" -e coverageFile \"/sdcard/Android/data/com.vmenon.mpo/files/${CUCUMBER_TAG}_coverage.ec\" -e clearPackageData \"true\" -e tags \"@$CUCUMBER_TAG\" -w com.vmenon.mpo.test/com.vmenon.mpo.test.CucumberTestRunner | tee -a /tmp/android_test/adb-test-${CUCUMBER_TAG}.log"
safeRunCommand "kill $SCREENRECORD_PID"
# Wait for 3 seconds for the device to compile the video
safeRunCommand "sleep 3"
# adb doesn't propagate exit code from tests, see https://code.google.com/p/android/issues/detail?id=3254
# So we need to parse saved terminal log
safeRunCommand "cat /tmp/android_test/adb-test-${CUCUMBER_TAG}.log | grep \"OK (\""
safeRunCommand "mkdir -p ./app/build/outputs/code_coverage/debugAndroidTest/connected"
safeRunCommand "adb pull /sdcard/Android/data/com.vmenon.mpo/files/${CUCUMBER_TAG}_coverage.ec ./app/build/outputs/code_coverage/debugAndroidTest/connected"