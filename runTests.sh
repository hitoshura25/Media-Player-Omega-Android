#!/bin/bash
source ./buildEnv.sh
set -e
safeRunCommand "./android-wait-for-emulator"

# Seems with AGP 7.2+, coverage doesn't work as it did before since executionData was missing a lot
# when using dynamic features. So just do a smoke test with dynamic features to make sure things
# work with dynamic loading, then run full regression coverage without using dynamic features
safeRunCommand "adb uninstall com.vmenon.mpo || true"
safeRunCommand "./gradlew lint app:createDebugAndroidTestCoverageReport -PcucumberFlag=smoke -PuseDynamicFeatures=true"
safeRunCommand "adb uninstall com.vmenon.mpo || true"
safeRunCommand "./gradlew aggregateDebugTestCoverage -PcucumberFlag=regression -PuseDynamicFeatures=false"