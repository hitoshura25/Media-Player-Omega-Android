#!/bin/bash
source ./buildEnv.sh
./buildSetup.sh
safeRunCommand "bundle exec fastlane build_and_deploy_feature"