#!/bin/bash
source ./buildEnv.sh
set -e
safeRunCommand "bundle exec fastlane test_coverage_report"