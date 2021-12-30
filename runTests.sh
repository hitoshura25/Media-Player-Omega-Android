#!/bin/bash

source ./buildEnv.sh
set -e
safeRunCommand "./android-wait-for-emulator"
safeRunCommand "adb uninstall com.vmenon.mpo || true"
safeRunCommand "bundle exec fastlane test"