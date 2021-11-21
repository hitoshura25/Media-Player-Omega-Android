#!/bin/bash
source ./buildEnv.sh
./buildSetup.sh
safeRunCommand "bundle exec fastlane deploy_internal_share"