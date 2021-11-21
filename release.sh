#!/bin/bash
source ./buildEnv.sh
./buildSetup.sh
safeRunCommand "bundle exec fastlane release"