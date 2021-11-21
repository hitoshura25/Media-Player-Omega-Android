#!/bin/bash
source ./buildEnv.sh
safeRunCommand "bundle exec fastlane increment_version version:$1"