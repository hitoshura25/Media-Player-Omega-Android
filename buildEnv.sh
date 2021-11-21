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

export BUNDLE_LOCATION="./app/build/outputs/bundle/release/app-release.aab"

if [[ -z "${TRAVIS_BUILD_NUMBER}" ]]; then
  if [[ -z "${CIRCLE_BUILD_NUM}" ]]; then
    export BUILD_NUMBER="0"
  else
    export BUILD_NUMBER="${CIRCLE_BUILD_NUM}"
  fi
else
  export BUILD_NUMBER="${TRAVIS_BUILD_NUMBER}"
fi
