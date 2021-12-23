#!/bin/bash
set -e
source ./buildEnv.sh
checkVar "SERVICE_ACCOUNT_FILE"
checkVar "android_keystore_password"
checkVar "android_keystore_key_alias"
checkVar "android_keystore_key_password"
checkVar "android_keystore_file"
checkVar "MAILGUN_POSTMASTER"
checkVar "MAILGUN_API_KEY"
checkVar "NOTIFICATION_RECIPIENT"
checkVar "NOTIFICATION_SENDER"
checkVar "MPO_ENCRYPTION_KEY"
checkVar "CODECOV_TOKEN"

safeRunCommand "gpg --passphrase $MPO_ENCRYPTION_KEY --pinentry-mode loopback -o $android_keystore_file -d $android_keystore_file.gpg"
safeRunCommand "gpg --passphrase $MPO_ENCRYPTION_KEY --pinentry-mode loopback -o $SERVICE_ACCOUNT_FILE -d $SERVICE_ACCOUNT_FILE.gpg"