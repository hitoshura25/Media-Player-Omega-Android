#!/bin/bash
source ./buildEnv.sh
./buildSetup.sh
echo "buildFeature.sh, build number: $BUILD_NUMBER"
safeRunCommand "bundle exec fastlane build_feature"