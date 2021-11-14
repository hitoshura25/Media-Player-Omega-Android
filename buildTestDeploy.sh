#!/bin/bash
function checkVar() {
  if [[ -z "${!1}" ]]; then
    echo "$1 is undefined"
  exit 1
  fi
}

function safeRunCommand() {
  typeset command="$*"
  typeset ret_code

  echo command="$command"
  eval "$command"
  ret_code=$?
  if [ $ret_code != 0 ]; then
    printf "Error : [%d] when executing command: '$command'" $ret_code
    exit $ret_code
  fi
}

set -e
export BUNDLE_LOCATION="./app/build/outputs/bundle/release/app-release.aab"
checkVar "SERVICE_ACCOUNT_FILE"
checkVar "android_keystore_password"
checkVar "android_keystore_key_alias"
checkVar "android_keystore_key_password"
checkVar "android_keystore_file"
checkVar "NOTIFICATION_API_URL"
checkVar "NOTIFICATION_RECIPIENT"
checkVar "NOTIFICATION_SENDER"
checkVar "MPO_ENCRYPTION_KEY"

safeRunCommand "gpg --passphrase $MPO_ENCRYPTION_KEY --pinentry-mode loopback -o $android_keystore_file -d $android_keystore_file.gpg"
safeRunCommand "gpg --passphrase $MPO_ENCRYPTION_KEY --pinentry-mode loopback -o $SERVICE_ACCOUNT_FILE -d $SERVICE_ACCOUNT_FILE.gpg"

safeRunCommand "./gradlew clean test lint :app:bundleRelease"
safeRunCommand "./uploadBundle.sh"