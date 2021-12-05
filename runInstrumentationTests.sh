#!/bin/bash
source ./buildEnv.sh
set -e
safeRunCommand "./gradlew app:deployBundleApksDebug app:installDebugAndroidTest"
safeRunCommand "adb shell am instrument -e clearPackageData \"true\" -e tags \"@smoke\" -w com.vmenon.mpo.test/com.vmenon.mpo.test.CucumberTestRunner"
#safeRunCommand "./gradlew connectedAndroidTest"