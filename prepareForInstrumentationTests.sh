#!/bin/bash
source ./buildEnv.sh
set -e
safeRunCommand "./gradlew assembleAndroidTest"